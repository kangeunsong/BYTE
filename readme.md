# _BYTE_ ; AI 요약 모델 기반 뉴스 요약 어플리케이션

### Are you busy?

### Don't have time to read the whole news?

#### Then,

### Why don't you just **_Grab a BYTE_**?

<br/>
<br/>
<br/>
<br/>

## 목차

1. [프로젝트 이름](#프로젝트-이름)
2. [프로젝트 설명](#프로젝트-설명)
3. [프로젝트 기능](#프로젝트-기능)
4. [프로젝트 사용 방법](#프로젝트-사용-방법)
5. [추후 개선 방향](#추후-개선-방향)
6. [팀 소개](#팀-소개)
7. [라이센스](#라이센스)

<br/>

---

<br/>
<br/>
<br/>

## 프로젝트 이름

디지털 정보의 기본 단위인 'byte'에서 영감을 받아 지어진 이름 **_BYTE_** 는 프로젝트가 제공하는 서비스의 핵심, 즉 정보의 핵심만을 전달하는 요약 뉴스 제공 기능을 나타낸다. 발음이 유사한 단어 'bite'와의 언어유희를 통해 독특한 슬로건을 가지고 있다. 가볍게 한 입 한다는 의미의 'grab a bite'를 활용하여 간단히 요약 뉴스를 맛보고 세상의 흐름을 알아간다는 의미의 'grab a **_BYTE_** '를 메인 슬로건으로 삼고 있다.

> Grab a **_BYTE_**!

> Enjoy your **_BYTE_**!

<br/>
<br/>

## 프로젝트 설명

뉴스는 사회의 흐름을 파악하는 중요한 수단이다. 하지만 정보의 홍수 속에 놓인 현대사회에서 우리는 수많은 뉴스 기사를 모두 읽고 소화할 수 없다. BYTE는 이러한 문제를 해결하기 위해 개발되었다. AI 기반 요약 모델을 활용하여 사용자 맞춤형 뉴스를 요약해 제공함으로써, 사용자가 짧은 시간 안에 중요한 정보를 파악할 수 있도록 돕는다. 필요한 정보만을 제공하여 바쁜 현대인들이 보다 효율적인 삶을 살아갈 수 있도록 한다.

<br/>
<br/>

## 프로젝트 기능
이 애플리케이션은 AI 기반 요약 모델을 통해 뉴스를 요약하여 제공한다. 사용자는 원하는 카테고리를 선택해 뉴스를 읽을 수 있으며, '원문보기' 기능을 통해 요약된 기사 외에도 실제 뉴스 원문을 URL을 클릭하여 확인할 수 있다. 또한, '좋아요' 기능을 통해 기사에 공감을 표현할 수 있으며, 활동 관리 탭에서 자신이 좋아요를 누른 기사의 목록을 확인하고 삭제할 수 있다.
<br/>
<br/>

## 프로젝트 사용 방법
보안상의 이유로 해당 프로젝트의 Firebase 데이터베이스에 접근하기 위한 json 파일은 삭제하였다. 각 데이터들의 경로는 api.py와 NewsdetailFragment.kt, NewsDetailDialog.kt 등의 코드에서 볼 수 있다.

<br/>

api.py는 아래의 모듈을 필요로 한다. 
> Flask==2.1.1

> kobart-transformers==0.1.4

> transformers==4.32.1

> torch==2.0.1

> requests==2.32.3

> beautifulsoup4==4.12.3

> firebase-admin==6.5.0

> (+ Python 3.9.13)

<br/>
<br/>

## 추후 개선 방향

현재 깃허브 프로젝트 내에 존재하는 api.py는 Hugging Face에 등록된 모델을 활용해 웹크롤링 결과물을 요약하여 Firebase에 저장하는 코드이다. 다양한 환경에서 기능을 사용하기 위해 이를 온라인 플랫폼에 등록하려 하였으나 무료 서비스 내에서는 불가능한 큰 용량으로, 추후 유료 결제를 통해 온라인에 api를 등록할 예정이다.

<br/>
<br/>

## 팀 소개

### 팀명: 넷마루

<br/>

### 강은송 <span style="font-size: 15px;">팀장</span>

- BACK-END 담당
- 전반적인 백엔드 기능 구현 및 DB 연결&관리

### 노현아 <span style="font-size: 15px;">팀원</span>

- FRONT-END 담당
- 전반적인 프론트 기능 구현

### 박에스더 <span style="font-size: 15px;">팀원</span>

- AI 담당
- kobart v2 모델을 사용한 머신러닝을 통해 기능 구현

### 심민아 <span style="font-size: 15px;">팀원</span>

- AI 담당
- kobart v2 모델을 사용한 머신러닝을 통해 기능 구현

<br/>
<br/>

## 라이센스

이 프로젝트는 다음과 같은 오픈소스 라이브러리를 사용합니다:

- 저작권: [sparrow007] (https://github.com/sparrow007/CarouselRecyclerview)
- 라이선스: [Apache License 2.0](https://opensource.org/licenses/Apache-2.0)
- 라이선스 URL: https://opensource.org/licenses/Apache-2.0

