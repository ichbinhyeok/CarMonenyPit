# CarMoneyPit 종합 구현 마스터 플랜 (Final)

## 1. 프로젝트 정체성 & 톤앤매너
### Identity: "차량 의사결정 지원 도구 (Decision Support Engine)"
*   **Core Philosophy:** "납득이 되어야 지갑을 연다 (Conviction First, Lead Second)"

### 톤앤매너: "Visual Authority (시각적 권위)"
> **Q. 틱톡에 맞추면 유치해지지 않을까요?**
> **A. 아니오.** 춤추는 틱톡이 아니라, **"빨간펜 선생님"** 같은 틱톡입니다.
*   **Not Childish:** 귀여운 캐릭터, 말장난, 가벼운 말투 ❌
*   **Visual Shock:** 고지서, 경고장, 빨간색 그래프, 굵은 폰트 ⭕
*   **전략:** "유치한 게(Silly)" 아니라 **"직관적인(Direct)"** 것입니다.
    *   금융 전문가들이 틱톡에서 차트 띄워놓고 "이 주식 지금 위험합니다"라고 할 때의 그 진지함과 직관성을 벤치마킹합니다.

---

## 2. 사용자 경험 흐름 (User Flow v2)

### Phase 1. Minimal Input (입력)
*   사용자 피로도 최소화를 위해 필수 데이터(차종, 주행거리, 견적)만 입력.

### Phase 1.5. Tension & Authority (권위 빌드업) **[NEW]**
*   **Loading Screen:** 1.5초~2초간 "AI 분석 과정"을 텍스트로 중계.
    *   *"Checking actuarial risk tables..."* (통계표 대조 중)
    *   *"Calculating depreciation curve..."* (감가상각 계산 중)
*   **효과:** "그냥 나온 숫자가 아니라, 정밀한 계산 결과구나"라는 **신뢰(Authority)** 형성.

### Phase 2. Smart Receipt (핵심 납득 장치)
*   **Iceberg Receipt (빙산 영수증):**
    *   눈앞의 수리비($1,500) 밑에, 더 거대한 **잠재 리스크($2,200)**를 빨간색 블록으로 시각화.
    *   설명 툴팁으로 "업계 통계(Industry Data)"를 언급하여 객관성 확보.

### Phase 3. Simulation Lab (능동적 납득)
*   **Interactive Sliders:** "스트레스 내성" 등을 직접 조절하며 비용 변화를 체험.
*   사용자가 스스로 "아, 내 성격상 이 차는 못 타겠네"라고 느끼게 유도.

### Phase 4. Contextual Exit (리드 전환)
*   판정 결과에 따른 **"다른 문 열어주기"**:
    *   **🔴 Time Bomb:** "지금 파는 게 이득" -> **[내 차 팔기 (Cash Offer)]**
    *   **🟡 Stable:** "고쳐서 타세요" -> **[정비소 예약 / 보증 연장]**
    *   **🟠 Borderline:** "돈이 문제인가요?" -> **[수리비 대출 알아보기]**

---

## 3. 구현 로드맵 (Roadmap)

### Step 1. 납득 엔진 구현 (Smart Receipt)
1.  **Backend:** `FinancialLineItem` 모델 생성 및 `VerdictResult` 수정.
2.  **Logic:** `RegretCalculator`에서 "숨겨진 비용" 산출 및 "권위적 멘트" 생성 로직 추가.
3.  **Frontend:** `result.jte`에 "빙산 영수증" 카드 구현.

### Step 2. UX 디테일 (Flow v2)
1.  **Loading:** HTMX를 이용한 "순차적 로딩 애니메이션" 구현 (Phase 1.5).
2.  **Simulation Lab:** 드롭다운을 슬라이더 UI로 교체.

### Step 3. 리드 연동 (Exit)
1.  결과 페이지 하단에 판정 상태별(Red/Yellow/Orange) 다른 버튼 노출.

---

## ✅ 승인 요청
가장 중요한 **"Step 1. 납득 엔진(Smart Receipt)"** 부터 구현을 시작하겠습니다.
코드는 **진지하고 권위 있는 톤**을 유지하며 작성됩니다.
