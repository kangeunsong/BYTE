import torch
from kobart_transformers import get_kobart_tokenizer
from transformers import BartForConditionalGeneration
import requests
from bs4 import BeautifulSoup

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
        print("생성url: ", url)
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

    return title_text, content_text

##### 뉴스크롤링 및 요약 시작 #####

while True:
    # 정치 or 경제 or 사회 or 생활/문화 or 세계 or IT/과학
    section = input("뉴스 분야를 선택해주세요: ")

    # naver url 생성
    url = makeUrl(section)
    if url:
        break

urls = url_crawler(url)

for url in urls:
    print("기사 URL:", url)
    title, content = article_crawler(url)
    print("\n기사제목:", title)
    print("\n기사내용:", content)

    if content:
        print("\nKoBART 요약 결과:")
        summary = summarize(content)
        print(summary)