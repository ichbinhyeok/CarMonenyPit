# AutoMoneyPit 프로젝트 평가 보고서 (2026.02.24 최신화)

## 1. 요약 (Executive Summary)

| 항목 | 상태 | 비고 |
|------|------|------|
| **로직 및 UX** | ✅ 우수함 | 후회 이론 기반 의사결정 엔진 완성 |
| **pSEO 구현** | ✅ 완료 | Fault, Mileage, Year 기반 랜딩 페이지 생성 |
| **디렉토리 구조** | ✅ 완료 | /models → /models/{brand} → /models/{brand}/{model} 사일로 |
| **내부 링크** | ✅ 완료 | Breadcrumb, 관련 모델, 마일리지 분석 링크 |
| **Schema.org** | ✅ 완료 | FAQPage, HowTo, BreadcrumbList, WebApplication |
| **DecisionEngine 통합** | ✅ 완료 | pSEO + 메인 결과 페이지 모두 동일 DecisionEngine 사용 (SSOT) |
| **리드 파이프라인** | ✅ 구현 완료 | PartnerRoutingConfig + LeadController + lead_capture.jte |
| **GA4 분석** | ✅ 설치 완료 | G-1NQYSFWZ7C |
| **수익화** | ⚠️ 승인 대기 중 | `app.partner.approval-pending=true` 상태. Waitlist 폼으로 fallback |

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

#### 2.2 DecisionEngine (후회 계산 엔진) — SSOT 통합 완료
- `DecisionEngine`: RF(수리 후회) vs RM(정리 후회) 통합 판정
  - **메인 결과 페이지**: `CarDecisionController` → `DecisionEngine.evaluate()`
  - **pSEO 페이지**: `PSeoController` → `DecisionEngine.evaluate()` (Phase 2에서 통합)
- `RegretCalculator`: RF/RM 세부 계산
- `CostOfInactionCalculator`: 자산 유출(Asset Bleed) 계산
- `ValuationService`: 시장 가치 추정
- `MarketPulseService`: 2주 단위 SEO 신선도 업데이트

#### 2.3 데이터 레이어
- `CarDataService`: JSON 기반 차량 데이터 (`@JsonIgnoreProperties(ignoreUnknown=true)` 적용)
  - `car_models.json`: 차량 정보 (중복 slug 해결됨)
  - `model_reliability.json`: 신뢰성 데이터
  - `model_market.json`: 시장 가격 데이터
  - `major_faults.json`: 주요 고장 데이터 (확장 메타데이터 PoC 포함)

#### 2.4 UI/UX
- `verdict_card.jte`: 판정 결과 카드 (CTA 포함)
- `smart_receipt.jte`: 비용 내역 영수증 스타일
- `sticky_bar.jte`: 플로팅 CTA 바
- 시뮬레이션 랩: HTMX 기반 실시간 변수 조정

#### 2.5 리드 파이프라인 (Phase 3 — 신규)
- `PartnerRoutingConfig.java`: `app.partner.approval-pending` 플래그로 동적 URL 라우팅
- `VerdictPresenter.java`: 하드코딩 URL 제거 → PartnerRoutingConfig 기반 동적 URL
- `LeadController.java`: `/lead` (CSV 로깅 + 조건부 리다이렉트) + `/lead-capture` (대기 폼)
- `lead_capture.jte`: verdict별 맞춤 메시지 대기 페이지

---

## 3. 수익화 현황 분석

### 3.1 현재 리드 라우팅 구조
```java
// PartnerRoutingConfig.java
approvalPending = true  → 모든 리드가 /lead-capture (내부 Waitlist)로 라우팅
approvalPending = false → 파트너 URL로 직접 리다이렉트 (Peddle, RepairPal 등)
```

### 3.2 CTA 위치
1. **verdict_card.jte 라인 402-413**: "Don't Fix" 판정 시 빨간색 CTA 카드
2. **verdict_card.jte 라인 440-445**: "Fix" 판정 시 파란색 "Audit Quote" CTA
3. **sticky_bar.jte**: 스크롤 시 나타나는 플로팅 CTA
4. **pseo_landing.jte / pseo_mileage.jte**: `/lead` 트래킹 URL 기반 CTA

### 3.3 리드 로깅
- `LeadController.java`의 `/lead` 엔드포인트에서 CSV 형식 로깅
- 기록 항목: event_type, page_type, verdict_type, brand, model, detail, referrer_path, placement
- 입력값 sanitize 처리 (CSV 인젝션 방지)

### 3.4 남은 수익화 작업
1. ~~제휴사 실제 연동 없음~~ → PartnerRoutingConfig에 URL 설정 완료, 승인만 대기
2. ~~리드 캡쳐 폼 없음~~ → lead_capture.jte 구현 완료
3. **A/B 테스트 인프라 없음**: 전환율 추적 미구축
4. **이메일 수집 후 발송 연동 없음**: Waitlist 이메일은 현재 클라이언트 alert만

---

## 4. 다음 단계 액션 아이템

### 즉시 실행
1. [x] ~~VerdictPresenter의 leadUrl을 동적 라우팅으로 변경~~ ✅ 완료
2. [x] ~~Google Analytics 4 설치~~ ✅ 완료 (G-1NQYSFWZ7C)
3. [ ] Peddle/CarBrain 제휴 승인 후 `app.partner.approval-pending=false` 전환
4. [ ] GA4 전환 이벤트 커스텀 설정 (CTA 클릭, 리드 제출)

### 단기 (2주 내)
1. [ ] 이메일 수집 백엔드 연동 (현재 클라이언트 alert만)
2. [ ] A/B 테스트 인프라 구축
3. [ ] Waitlist 이메일 발송 자동화

### 중기 (1개월)
1. [ ] 제휴사 승인 후 실제 리다이렉트 전환
2. [ ] 추가 제휴사 확보 (Endurance Warranty, CARCHEX)
3. [ ] 리드 품질 모니터링 대시보드

---

## 5. SEO 현황 평가

### ✅ 완료
1. **Programmatic SEO 완전 구현**: 차량/고장/마일리지 조합별 고유 URL
2. **Schema.org 마크업**: FAQPage, HowTo, BreadcrumbList, WebApplication
3. **내부 링크 구조**: 사일로 구조 + 관련 모델 링크
4. **SEO 신선도**: MarketPulseService로 2주마다 콘텐츠 업데이트
5. **sitemap.xml 동적 생성**: RootController에서 자동 생성
6. **robots.txt**: 설정 완료 (Disallow: /verdict/share)
7. **GA4 태그 설치**: 모든 페이지 적용
8. **Canonical URL 통일**: baseUrl 환경변수 기반 (`app.baseUrl`)
9. **OG 이미지**: 내부 호스팅 (`/og-image.png`)으로 전환 완료

### ⚠️ 개선 필요
1. **Core Web Vitals**: LCP, CLS 최적화 필요 (HTMX defer 등)
2. **Google Search Console 등록**: 필요
3. **AEO Answer Block**: pSEO 페이지 상단에 직접 답변 추가 필요

---

## 6. 기술 스택 요약

| 레이어 | 기술 |
|--------|------|
| Backend | Spring Boot 3.x + Java 21 |
| Template | JTE (Java Template Engine) |
| Frontend | HTMX + Vanilla CSS |
| Data | JSON (resources/data/) |
| Analytics | GA4 (G-1NQYSFWZ7C) |
| Hosting | Dockerfile 준비됨 |
| Lead Routing | PartnerRoutingConfig (환경변수 기반) |
