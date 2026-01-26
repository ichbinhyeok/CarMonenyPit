# 구현 계획 - 스마트 영수증 (설득력 강화)

## 목표
불투명한 "판정 결과"를 투명한 **"후회 비용 명세서(Accounting of Regret)"**로 변환하여, 사용자가 보이지 않는 리스크를 시각적으로 인지하고 의사결정을 내릴 수 있도록 돕습니다.

## 사용자 검토 필요
> [!NOTE]
> 이 기능은 정확한 회계 장부가 아니라, **"의사결정을 돕는 논리적 근거"**를 보여주는 것입니다.
> 너무 구체적인 숫자로 오해를 사지 않도록, "~(약)" 표시와 "추정(Estimated)" 표현을 사용하여 EEAT 리스크를 관리합니다.

## 변경 제안

### 1. 데이터 모델 업데이트
프론트엔드로 "영수증 항목"을 전달할 구조체 생성

#### [NEW] `src/main/java/com/carmoneypit/engine/api/OutputModels/FinancialLineItem.java`
-   `String label`: 항목명 (예: "향후 변속기 고장 리스크", "스트레스 비용")
-   `String amount`: 금액 (예: "~$2,200", "+$500")
-   `String description`: 설명 (예: "이 주행거리 구간에서는 30% 확률로 발생")
-   `boolean isNegative`: 스타일링 힌트 (빨간색/회색 구분)

#### [MODIFY] `src/main/java/com/carmoneypit/engine/api/OutputModels/VerdictResult.java`
-   `List<FinancialLineItem> costBreakdown` 필드 추가

### 2. 로직 강화 (RegretCalculator)
단순 점수 합산이 아니라, 계산 과정을 "항목화"하여 반환하도록 수정

#### [MODIFY] `src/main/java/com/carmoneypit/engine/core/RegretCalculator.java`
-   `calculateRF`와 `calculateRM`을 리팩토링하여 상세 내역(Line Items)을 수집
-   **핵심 로직:**
    -   추상적인 "Points"를 "대략적인 달러 가치"로 환산하여 표시 (예: 1 Point = $10, 단순 스케일링)
    -   리스크 레벨에 따른 "근거 텍스트" 생성 (예: CRITICAL 레벨이면 "연쇄 고장 위험 높음" 경고 추가)

### 3. UI 구현 (JTE 템플릿)
결과 화면에 "스마트 영수증" 카드 추가

#### [MODIFY] `src/main/jte/result.jte`
-   **"수리 시나리오" vs "교체 시나리오"**를 나란히 비교하는 영수증 카드 추가
-   숨겨진 비용(Hidden Cost)을 시각적으로 강조하여 "왜 손해인지" 직관적으로 보여줌

### 4. 데이터 보강 (가볍게)
#### [MODIFY] `engine_data.v1.json`
-   `risk_level`에 대한 `authoritative_text` 필드 추가
    -   예: "High" -> "High Risk (Statistically 1 in 3 cars fail at this stage)"
    -   데이터 자체를 정밀하게 바꾸는 게 아니라, **"설명력"**을 높이는 텍스트 추가

## 검증 계획

### 자동화 테스트
-   `RegretCalculatorTest`: `costBreakdown` 리스트가 정상적으로 생성되고, 합산 점수가 맞는지 검증

### 수동 검증
-   애플리케이션 실행 (`./gradlew bootRun`)
-   **고위험 시나리오 입력:** (예: 16만 마일 세단) -> "잠재적 고장 비용"이 높게 찍히는지 확인
-   **UI 확인:** 영수증 형태가 "회계 문서"보다는 **"의사결정 도우미"**처럼 친절하게 보이는지 확인
