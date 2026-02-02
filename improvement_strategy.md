# 신규 도메인 리드 수익화 전략 (2026.02 최신화)

## 📊 현재 상황 요약

| 항목 | 상태 |
|------|------|
| **도메인 연령** | 신규 (DA/PA ≈ 0) |
| **pSEO** | ✅ 구현 완료 (URL 구조, Schema, 내부링크) |
| **리드 CTA** | ⚠️ UI만 존재, 실제 연동 없음 |
| **제휴 파트너** | ❌ 미확보 |
| **분석 도구** | ❌ 미설치 |

---

## 🎯 핵심 전략: "SEO 기다리지 말고, 소셜로 우회하라"

신규 도메인은 구글 신뢰도를 쌓는 데 **6~12개월**이 걸립니다.  
그동안 **소셜 트래픽 → 리드 → 수익 → SEO 투자** 순환을 만들어야 합니다.

```
┌─────────────────────────────────────────────────────────────┐
│                    수익화 퍼널                              │
├─────────────────────────────────────────────────────────────┤
│  [틱톡/릴스 바이럴] ─┐                                      │
│                      ├──▶ [사이트 방문] ──▶ [판정 결과]     │
│  [SEO 트래픽 (장기)] ┘                                      │
│                                                             │
│  [판정: SELL] ──▶ [리드폼] ──▶ [Peddle/CarBrain] ──▶ $$$   │
│  [판정: FIX]  ──▶ [리드폼] ──▶ [RepairPal]       ──▶ $$$   │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔥 Phase 1: 즉시 실행 (이번 주)

### 1.1 리드 캡쳐 더미 테스트
현재 `leadUrl`이 더미 상태입니다. 실제 연동 전 **클릭률(CTR) 측정**이 필요합니다.

```java
// VerdictPresenter.java 수정
// 현재: leadUrl = "https://example.com/..."
// 변경: leadUrl = "/lead?verdict=SELL&brand=..." (내부 리드폼으로 연결)
```

### 1.2 간이 리드폼 구현
```html
<!-- lead_form.jte (새로 생성) -->
<form action="/api/lead" method="POST">
    <input type="hidden" name="verdict" value="${verdict}">
    <input type="hidden" name="brand" value="${brand}">
    <input type="hidden" name="model" value="${model}">
    
    <input type="text" name="name" placeholder="Name" required>
    <input type="tel" name="phone" placeholder="Phone" required>
    <input type="text" name="zipCode" placeholder="ZIP Code" required>
    
    <button type="submit">Get My Free Offer</button>
</form>
```

### 1.3 GA4 이벤트 설정
```javascript
// 추적 이벤트
gtag('event', 'verdict_shown', { verdict_type: 'SELL' });
gtag('event', 'cta_click', { cta_label: 'Get Your Free Offer' });
gtag('event', 'lead_submit', { brand: 'Toyota', model: 'Camry' });
```

---

## 📱 Phase 2: 틱톡 바이럴 전략 (1-2주)

### 2.1 컨텐츠 전략
신규 도메인이 SEO에서 이기기 어렵다면, **바이럴 가능한 결과 화면**이 무기입니다.

```
[틱톡 영상 훅 예시]
"정비소에서 수리비 200만원 나왔어요 😱"
→ "그 차, 지금 팔면 얼마인지 알아?"
→ (사이트에서 테스트하는 화면)
→ (충격적인 결과: "팔면 $4,500, 고치면 $5,800 더 손해")
→ "링크 프로필에!"
```

### 2.2 결과 화면 최적화 (캡쳐 유도)
현재 `verdict_card.jte`가 이미 시각적으로 훌륭하지만, **캡쳐하고 싶은 요소** 강화:

1. **큰 숫자 대비**: "$2,500 vs $4,800"
2. **SELL/KEEP 배지**: 빨강/초록 명확한 대비
3. **해시태그 복사 버튼**: "#CarMoneyPit 💀 점수: 12점"

### 2.3 동적 소스 파라미터
```java
// ?source=tiktok 파라미터 감지
if (source.equals("tiktok")) {
    // 더 자극적인 카피로 변경
    headline = "내 차가 돈 먹는 하마인지 확인하기 🦛💸";
}
```

---

## 💰 Phase 3: 제휴 파트너 확보 (2-4주)

### 3.1 우선순위 제휴사

| 제휴사 | 타겟 판정 | 리드당 수익 | 난이도 |
|--------|-----------|-------------|--------|
| **Peddle** | SELL (정크카) | $50-100 | 쉬움 |
| **CarBrain** | SELL (손상차) | $30-80 | 쉬움 |
| **RepairPal** | FIX | $10-30 | 보통 |
| **CarMax** | SELL (일반) | $20-50 | 어려움 |

### 3.2 제휴 신청 시 피칭 포인트
```
"저희 CarMoneyPit은 자동차 수리비 VS 판매 의사결정 도구입니다.
사용자가 '팔아야 한다'는 판정을 받으면, 바로 귀사 서비스로 연결됩니다.
리드는 'warm lead' — 수리비 견적을 받고 판매를 고민 중인 사람들입니다.
월 예상 리드: [X개] (트래픽 확보 후 업데이트)"
```

### 3.3 리드 품질 관리 (승인율 ↑)
제휴사가 리드를 거절하는 이유:
- ❌ 가짜 연락처
- ❌ 관심 없는 사용자
- ❌ 서비스 불가 지역

**해결책:**
```java
// 전화번호 검증
if (!PhoneValidator.isValid(phone)) { reject(); }

// 의향 확인
checkbox: "☑ I'm seriously considering selling my car"

// 지역 필터링
if (!peddleServiceAreas.contains(zipCode)) {
    // 다른 제휴사로 라우팅 또는 대기 리스트
}
```

---

## 📈 Phase 4: 분석 및 최적화 (지속)

### 4.1 전환 퍼널 측정

```
[방문] → [결과 조회] → [CTA 클릭] → [리드 제출] → [제휴사 승인]
  100%      70%           25%          10%            7%
```

### 4.2 A/B 테스트 우선순위

| 항목 | A 버전 | B 버전 |
|------|--------|--------|
| CTA 문구 | "Get Your Free Offer" | "Sell My Car Now" |
| CTA 색상 | 빨강 | 초록 |
| 결과 톤 | 점잖게 | 자극적으로 |
| 리드폼 위치 | 결과 아래 | 팝업 |

### 4.3 성공 지표 (KPI)

| 지표 | 목표 |
|------|------|
| CTR (CTA 클릭) | 15%+ |
| 리드 제출률 | 30%+ (클릭 대비) |
| 제휴사 승인률 | 70%+ |
| 리드당 수익 | $40+ |

---

## 🛠 구현 체크리스트

### 즉시 (이번 주)
- [ ] GA4 설치 및 이벤트 설정
- [ ] VerdictPresenter의 leadUrl을 `/lead?...` 로 변경
- [ ] 간이 리드폼 페이지 생성 (lead_form.jte)
- [ ] CTA 클릭 이벤트 추적

### 단기 (2주)
- [ ] LeadController.java 구현 (리드 저장)
- [ ] Peddle 제휴 문의 발송
- [ ] 틱톡 계정 생성 + 첫 영상 업로드
- [ ] 결과 화면 "공유" 기능 강화

### 중기 (1개월)
- [ ] 제휴사 승인 후 실제 연동
- [ ] A/B 테스트 인프라 구축
- [ ] sitemap.xml 자동 생성 (SEO)
- [ ] Google Search Console 등록

### 장기 (3개월)
- [ ] 틱톡 광고 테스트 ($100 테스트 예산)
- [ ] SEO 트래픽 모니터링
- [ ] 추가 제휴사 확보

---

## 💡 핵심 메시지

> **"신규 도메인에서 SEO를 기다리지 마세요.  
> 바이럴 → 리드 → 수익 → 그 돈으로 SEO 투자"**

리드 승인율을 높이려면:
1. **타이밍** — 사용자가 "빡친" 순간에 CTA
2. **품질** — 진짜 팔 의향 있는 사람만 필터링
3. **매칭** — 서비스 가능 지역만 연결

---

## 📁 관련 파일

| 파일 | 역할 | 수정 필요 |
|------|------|----------|
| `VerdictPresenter.java` | 리드 URL/라벨 생성 | ✅ 수정 필요 |
| `verdict_card.jte` | CTA 버튼 UI | 선택 |
| `sticky_bar.jte` | 플로팅 CTA | 선택 |
| (새로 생성) `LeadController.java` | 리드 처리 | ⭐ 생성 필요 |
| (새로 생성) `lead_form.jte` | 리드폼 UI | ⭐ 생성 필요 |
