from flask import Flask, jsonify, request
from kobart_transformers import get_kobart_tokenizer
from transformers import BartForConditionalGeneration
import torch
import requests
from bs4 import BeautifulSoup
import firebase_admin
from firebase_admin import credentials, firestore
import datetime
import os
import uuid

app = Flask(__name__)

# Firestore 초기화
cred = credentials.Certificate("./byte-d568d-firebase-adminsdk-h5lcl-b5636a74c0.json")
if cred:
    print("initialized success")
firebase_admin.initialize_app(cred)
db = firestore.client()

# Load the model from Hugging Face
def load_model():
    model = BartForConditionalGeneration.from_pretrained('simminah/news')  # Hugging Face 저장소 경로 사용
    return model

# Initialize model and tokenizer  
model = load_model()
tokenizer = get_kobart_tokenizer()

def summarize(text):
    text = text.replace('\n', '')
    input_ids = tokenizer.encode(text, max_length=512, truncation=True)  # 입력 길이 제한 추가
    input_ids = torch.tensor(input_ids)
    input_ids = input_ids.unsqueeze(0) 
    try:
        output = model.generate(input_ids, eos_token_id=tokenizer.eos_token_id, max_length=512, num_beams=5)
        summary = tokenizer.decode(output[0], skip_special_tokens=True)
    except IndexError as e:
        print(f"Error during summarization: {e}")
        summary = "요약을 생성하는 중 오류가 발생했습니다."
    return summary

# 원하는 분야 뉴스 페이지 url 생성
def makeUrl(section):
    sections = {
        "정치": "100",
        "경제": "101",
        "사회": "102",
        "생활/문화": "103",
        "세계": "104",
        "IT/과학": "105"
    }
    sectionNUM = sections.get(section)
    if sectionNUM:
        url = f"https://news.naver.com/section/{sectionNUM}"
        return url
    else:
        print("올바른 분야를 선택해주세요.")
        return None

# 특정 속성 값을 추출하여 리스트로 반환
def news_attrs_crawler(articles, attrs):
    attrs_content = []
    for i in articles:
        attrs_content.append(i.attrs[attrs])
    return attrs_content

# ConnectionError 방지
headers = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/98.0.4758.102"}

def url_crawler(url):
    original_html = requests.get(url, headers=headers)
    html = BeautifulSoup(original_html.text, "html.parser")

    # 헤드라인 뉴스 url 크롤링
    url_naver = html.select("div.section_article.as_headline._TEMPLATE ul.sa_list li.sa_item div.sa_text > a.sa_text_title._NLOG_IMPRESSION")
    url = news_attrs_crawler(url_naver, 'href')

    return url

def article_crawler(url):
    original_html = requests.get(url, headers=headers)
    html = BeautifulSoup(original_html.text, "html.parser")

    # 제목 크롤링
    title_article = html.select("div.newsct > div.media_end_head.go_trans > div.media_end_head_title > h2.media_end_head_headline > span")
    title_text = title_article[0].get_text(strip=True) if title_article else "제목을 찾을 수 없습니다."

    # 내용 크롤링
    content_article = html.select("div.newsct > div.newsct_body > div.newsct_article._article_body > article.go_trans._article_content")
    content_text = " ".join([element.get_text(strip=True) for element in content_article])

    # 이미지 URL 크롤링
    image_tag = html.select_one("img#img1")  # img 태그에서 id가 img1인 것만 선택
    image_url = None
    
    if image_tag:
        # 우선 src 속성을 가져오고, 없으면 다른 속성에서 가져오기
        image_url = image_tag.get('src') or image_tag.get('data-src') or image_tag.get('data-original')
        if not image_url:
            image_url = None

    return title_text, content_text, image_url

def save_to_firestore(section, articles):
    today = datetime.datetime.now().strftime('%Y%m%d')

    for i, article in enumerate(articles, 1):
        newsUID = str(uuid.uuid4())  # UUID 생성
        print(f"Saving article {i} in section {section}: {article}")
        article_with_extra_fields = {
            **article,
            "likeNUM": 0,
            "newsUID": newsUID
        }
        
        # 'TodayNews'에 저장
        doc_ref_today = db.collection('TodayNews').document(today).collection(section).document(f'news{i}')
        doc_ref_today.set(article_with_extra_fields)

        # 'AllNews'에 저장
        doc_ref_all = db.collection('AllNews').document(section).collection(today).document(newsUID)
        doc_ref_all.set(article_with_extra_fields)


def summarize_and_store_all_sections():
    sections = {
        "Politics": "정치",
        "Economy": "경제",
        "Society": "사회",
        "LifeCulture": "생활/문화",
        "World": "세계",
        "ITScience": "IT/과학"
    }
    
    for section_en, section_kr in sections.items():
        url = makeUrl(section_kr)
        if url:
            urls = url_crawler(url)
            if urls:
                results = []
                for first_url in urls[:10]:  # 각 분야에서 최대 10개의 뉴스 가져오기
                    title, content, image_url = article_crawler(first_url)
                    summary = summarize(content)
                    result = {
                        "title": title,
                        "summary": summary,
                        "newsURL": first_url,
                        "imgURL": image_url
                    }
                    print(result)
                    results.append(result)
                
                # Firestore에 저장
                save_to_firestore(section_en, results)
            else:
                print(f"No articles found for section {section_kr}")
        else:
            print(f"Invalid section: {section_kr}")

    print("All sections processed and stored in Firestore.")
    os._exit(0)  # 모든 작업 완료 후 애플리케이션 종료

@app.route('/summarize_and_store', methods=['GET'])
def summarize_and_store():
    summarize_and_store_all_sections()
    return jsonify({"status": "completed", "message": "All sections processed and stored in Firestore."})

if __name__ == '__main__':
    summarize_and_store_all_sections()  # 앱 실행 시 모든 섹션을 처리하고 종료
    app.run(host='0.0.0.0', port=5000)