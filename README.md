# 🥃 SYSO - Select Your Spirit Online

> Java Swing 기반 위스키 쇼핑몰 데스크탑 애플리케이션

---

## 📌 프로젝트 소개

**SYSO(Select Your Spirit Online)** 는 Java Swing으로 구현한 위스키 전문 쇼핑몰 데스크탑 앱입니다.
싱글몰트, 블렌디드, 버번, 재패니즈 등 다양한 카테고리의 위스키 40여 종을 둘러보고,
장바구니 담기 → 결제 → 주문 내역 확인까지의 쇼핑 플로우를 경험할 수 있습니다.

---

## 🛠 기술 스택

| 항목 | 내용 |
|------|------|
| Language | Java |
| UI Framework | Java Swing |
| IDE | Eclipse |
| 데이터 저장 | In-memory (런타임) |

---

## 📁 프로젝트 구조

```
SYSO(Select_Your_Spirit_Online)/
├── src/
│   ├── main/
│   │   └── Main.java               # 진입점, Swing Event Dispatch Thread 실행
│   ├── frame/
│   │   ├── FrameBase.java          # 싱글 프레임 + 페이드 화면 전환, 뒤로가기 히스토리
│   │   ├── StartPage.java          # 스플래시 / 시작 화면
│   │   ├── FrameBegin.java         # 첫 화면 (로그인 / 회원가입 선택)
│   │   ├── LoginPage.java          # 로그인
│   │   ├── SignUpPage.java         # 회원가입
│   │   ├── FrameMain.java          # 상품 목록 메인 화면
│   │   ├── FrameItemSelect.java    # 카테고리 선택
│   │   ├── FrameItemInfo.java      # 상품 상세 정보
│   │   ├── CartFrame.java          # 장바구니
│   │   ├── MyPage.java             # 마이페이지 (회원정보 / 주문 내역)
│   │   └── OrderLibrary.java       # 주문 내역 화면
│   ├── DAO/
│   │   └── ItemDAO.java            # 상품 데이터 초기화 및 관리 (40종)
│   ├── DTO/
│   │   ├── Item.java               # 상품 도메인 모델
│   │   └── MyData.java             # 회원 도메인 모델
│   ├── shoppingCart/
│   │   ├── Cart.java               # 장바구니 CRUD 및 총액 계산
│   │   ├── CartItem.java           # 장바구니 아이템 (상품 + 수량)
│   │   ├── Order.java              # 주문 생성, 취소, 내역 조회
│   │   ├── Payment.java            # 결제 수단 Enum (신용카드, 간편결제, 계좌이체)
│   │   └── OrderStatus.java        # 주문 상태 Enum (대기, 완료, 취소)
│   ├── search/
│   │   ├── SearchLogic.java        # 키워드 검색 로직 (대소문자/공백 무시)
│   │   └── FrameSearchResult.java  # 검색 결과 화면
│   └── user/
│       ├── SignUpValidator.java     # 회원가입 유효성 검사
│       └── UserMap.java            # 회원 데이터 관리
├── Img/                            # 상품 썸네일 이미지 (00~39.png)
├── img_info/                       # 상품 상세 이미지 (00~39)
└── ui_img/                         # UI 아이콘 및 로고
```

---

## ✨ 주요 기능

### 🔐 회원
- 회원가입 (아이디, 비밀번호, 이름, 이메일, 생년월일, 전화번호)
- 로그인 / 로그아웃
- 마이페이지에서 회원 정보 확인

### 🔎 회원가입 유효성 검사
- 비밀번호: 영문 대/소문자, 숫자, 특수문자 포함 9자 이상
- 이메일 형식 검사
- 전화번호: 010으로 시작하는 11자리
- 생년월일: 8자리 숫자, 유효 날짜, **만 18세 이상**만 가입 가능

### 🛍 상품
- 40종 위스키 상품 목록 (싱글몰트, 블렌디드, 버번, 재패니즈 등)
- 카테고리별 필터링
- 상품명 키워드 검색 (대소문자·공백 무시)
- 상품 상세 정보 및 재고 수량 확인

### 🛒 장바구니
- 상품 추가 / 수량 변경 / 삭제
- 재고 초과 수량 담기 방지
- 총 금액 실시간 계산
- 장바구니 전체 비우기

### 💳 주문 / 결제
- 결제 수단 선택 (신용카드 / 간편결제 / 계좌이체)
- 결제 시 재고 자동 차감
- 주문 취소 시 재고 복구
- 주문 내역 날짜별 조회

### 🖥 UI / UX
- 싱글 프레임 구조 (화면 교체 방식)
- 화면 전환 시 페이드 애니메이션 효과
- 뒤로가기 히스토리 스택

---

## 🚀 실행 방법

1. **Java 11 이상** 설치 확인
2. Eclipse에서 프로젝트 import
   ```
   File → Import → Existing Projects into Workspace
   ```
3. `src/main/Main.java` 실행 (`Run As → Java Application`)

> ⚠️ 이미지 파일(`Img/`, `img_info/`, `ui_img/`)이 프로젝트 루트에 있어야 정상 표시됩니다.

---

## 👥 팀원 및 역할

| 이름 | 담당 |
|------|------|
|  |  |

---

## 📝 기타

- 모든 데이터는 런타임 메모리에만 저장되며, 앱 종료 시 초기화됩니다.
- 상품 데이터는 `ItemDAO.java`에 하드코딩되어 있습니다.
