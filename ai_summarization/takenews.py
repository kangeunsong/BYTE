
import requests
from bs4 import BeautifulSoup

# 원하는 분야 뉴스 페이지 url 생성
def makeUrl(section):
    if section == "정치":
        sectionNUM = "100"
        url = "https://news.naver.com/section/" + sectionNUM
        print("생성url: ", url)
        return url
    elif section == "경제":
        sectionNUM = "101"
        url = "https://news.naver.com/section/" + sectionNUM
        print("생성url: ", url)
        return url  
    elif section == "사회":
        sectionNUM = "102"
        url = "https://news.naver.com/section/" + sectionNUM
        print("생성url: ", url)
        return url  
    elif section == "생활/문화":
        sectionNUM = "103"
        url = "https://news.naver.com/section/" + sectionNUM
        print("생성url: ", url)
        return url  
    elif section == "세계":
        sectionNUM = "104"
        url = "https://news.naver.com/section/" + sectionNUM
        print("생성url: ", url)
        return url  
    elif section == "IT/과학":
        sectionNUM = "105"
        url = "https://news.naver.com/section/" + sectionNUM
        print("생성url: ", url)
        return url  

# 특정 속성 값을 추출하여 리스트로 반환
def news_attrs_crawler(articles,attrs):
    attrs_content=[]
    for i in articles:
        attrs_content.append(i.attrs[attrs])
    return attrs_content

# ConnectionError방지
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
    # content_text = content_article[0].get_text(separator="\n", strip=True)
    content_text = " ".join([element.get_text(strip=True) for element in content_article])

    
    print("기사제목: ", title_text)
    print("기사내용: ", content_text)




##### 뉴스크롤링 시작 #####

# 정치 or 경제 or 사회 or 생활/문화 or 세계 or IT/과학
section = input("뉴스 분야를 선택해주세요:")

# naver url 생성
url = makeUrl(section)
urls = url_crawler(url)

for url in urls:
    print(url)
    article_crawler(url)


# 크롤링된 헤드라인 뉴스 url의 분야와 선택 분야가 다를 수 있음 (뉴스 분야가 2개 이상인 경우도 있기 때문)