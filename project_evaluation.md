# CarMoneyPit 프로젝트 평가 보고서 (2026.02 최신화)

## 1. 요약 (Executive Summary)

| 항목 | 상태 | 비고 |
|------|------|------|
| **로직 및 UX** | ✅ 우수함 | 후회 이론 기반 의사결정 엔진 완성 |
| **pSEO 구현** | ✅ 완료 | Fault, Mileage 기반 랜딩 페이지 생성 |
| **디렉토리 구조** | ✅ 완료 | /models → /models/{brand} → /models/{brand}/{model} 사일로 |
| **내부 링크** | ✅ 완료 | Breadcrumb, 관련 모델, 마일리지 분석 링크 |
| **Schema.org** | ✅ 완료 | FAQPage, HowTo, BreadcrumbList, WebApplication |
| **리드 CTA** | ⚠️ 부분 구현 | verdictCard에 존재하나 실제 제휴 연동 미완성 |
| **수익화** | ❌ 미구현 | 실제 리드 캡쳐 및 제휴사 연동 필요 |

---

## 2. 현재 아키텍처 분석

### ✅ 완성된 기능

#### 2.1 Programmatic SEO (pSEO)
```
URL 패턴:
├── /verdict/{brand}/{model}/{fault-slug}     → pseo_landing.jte
├── /verdict/{brand}/{model}/{mileage}-miles  → pseo_mileage.jte
├── /should-i-fix/{year}-{brand}-{model}      → pseo.jte
├── /models                                    → 브랜드 목록
├── /models/{brand}                            → 모델 목록
└── /models/{brand}/{model}                    → 고장 목록
```

#### 2.2 DecisionEngine (후회 계산 엔진)
- `RegretCalculator`: RF(수리 후회) vs RM(정리 후회) 계산
- `CostOfInactionCalculator`: 자산 유출(Asset Bleed) 계산
- `ValuationService`: 시장 가치 추정
- `MarketPulseService`: 2주 단위 SEO 신선도 업데이트

#### 2.3 데이터 레이어
- `CarDataService`: JSON 기반 차량 데이터
  - `models.json`: 차량 정보
  - `model_reliability.json`: 신뢰성 데이터
  - `model_market.json`: 시장 가격 데이터
  - `major_faults.json`: 주요 고장 데이터

#### 2.4 UI/UX
- `verdict_card.jte`: 판정 결과 카드 (CTA 포함)
- `smart_receipt.jte`: 비용 내역 영수증 스타일
- `sticky_bar.jte`: 플로팅 CTA 바
- 시뮬레이션 랩: HTMX 기반 실시간 변수 조정

---

## 3. 수익화 현황 분석

### 3.1 현재 구현된 CTA
```java
// VerdictPresenter.java에서 생성
leadLabel: "Get Your Free Offer" / "Find Fair Repair Shops"
leadUrl: "https://example.com/..." (임시 URL)
```

### 3.2 CTA 위치
1. **verdict_card.jte 라인 402-413**: "Don't Fix" 판정 시 빨간색 CTA 카드
2. **verdict_card.jte 라인 440-445**: "Fix" 판정 시 파란색 "Audit Quote" CTA
3. **sticky_bar.jte**: 스크롤 시 나타나는 플로팅 CTA

### 3.3 ⚠️ 수익화 미완성 항목
1. **제휴사 실제 연동 없음**: leadUrl이 더미 링크
2. **리드 캡쳐 폼 없음**: 이름/전화번호 수집 기능 없음
3. **A/B 테스트 인프라 없음**: 전환율 추적 불가
4. **지역 필터링 없음**: 제휴사 서비스 가능 지역 체크 없음

---

## 4. 긴급 액션 아이템 (수익화 실현)

### Phase 1: 리드 캡쳐 구현 (1주)
```java
// 필요한 새 파일
└── src/main/java/com/carmoneypit/engine/web/LeadController.java
└── src/main/jte/fragments/lead_form.jte
└── src/main/jte/fragments/lead_success.jte
```

리드 폼 필수 필드:
- 이름
- 전화번호 (형식 검증)
- 이메일 (선택)
- Zip Code (지역 필터링용)
- 차량 정보 (자동 채움)
- 판정 결과 (자동 채움)

### Phase 2: 제휴사 연동 (2주)
| 제휴사 | 유형 | 예상 리드당 수익 |
|--------|------|------------------|
| Peddle | 정크카 매입 | $50-100 |
| CarBrain | 손상차 매입 | $30-80 |
| RepairPal | 정비소 연결 | $10-30 |
| CarMax | 중고차 매입 | $20-50 |

### Phase 3: 분석 인프라 (2주)
- Google Analytics 4 이벤트 추적
- 전환 퍼널 설정
- A/B 테스트 (버튼 문구, 색상, 위치)

---

## 5. SEO 현황 평가

### ✅ 잘된 점
1. **Programmatic SEO 완전 구현**: 차량/고장/마일리지 조합별 고유 URL
2. **Schema.org 마크업**: FAQPage, HowTo, BreadcrumbList 완비
3. **내부 링크 구조**: 사일로 구조 + 관련 모델 링크
4. **SEO 신선도**: MarketPulseService로 2주마다 콘텐츠 업데이트

### ⚠️ 개선 필요
1. **sitemap.xml 생성**: 구글 서치콘솔 제출용
2. **robots.txt 최적화**: 크롤링 예산 관리
3. **Core Web Vitals**: LCP, CLS 최적화 필요 (측정 필요)

### 🚨 신규 도메인 한계
- **Domain Authority 0**: 상위 노출까지 6-12개월 예상
- **대안**: 소셜 트래픽(틱톡/릴스) 우선 → SEO 병행

---

## 6. 결론 및 권장 사항

### 즉시 실행 (이번 주)
1. [ ] VerdictPresenter의 leadUrl을 실제 제휴 URL로 교체 (또는 리드폼 URL)
2. [ ] Google Analytics 4 설치 + 전환 이벤트 설정
3. [ ] Peddle/CarBrain 제휴 문의 발송

### 단기 (2주 내)
1. [ ] LeadController + lead_form.jte 구현
2. [ ] 리드 데이터 저장 (DB 또는 외부 서비스)
3. [ ] sitemap.xml 자동 생성

### 중기 (1개월)
1. [ ] 틱톡/릴스 바이럴 전략 실행
2. [ ] A/B 테스트 인프라 구축
3. [ ] 제휴사 승인 후 실제 연동

---

## 7. 기술 스택 요약

| 레이어 | 기술 |
|--------|------|
| Backend | Spring Boot 3.x + Java 21 |
| Template | JTE (Java Template Engine) |
| Frontend | HTMX + Vanilla CSS |
| Data | JSON (resources/data/) |
| Hosting | Dockerfile 준비됨 |
