# Raw Data Specification (LOCKED)

## 공통 원칙
- 차량 모델/연식 단위 ❌
- 차급 × 주행거리 × 이벤트 ⭕
- 실시간 API ❌
- 절대 금액 ❌
- 범위 / 비율 / 계수 / 확률 ⭕

---

## 1. Failure Cascade Probability
- 주행거리 구간 × 수리 유형별
- 12개월 내 추가 대형 고장 확률

## 2. Major Repair Cost Range
- 고장 유형별 평균 수리비 범위
- 정확한 평균값 불필요

## 3. Depreciation Acceleration
- 대형 수리 후
- 고주행 구간 진입 후
- 반복 수리 후

## 4. Sellability / Liquidity
- 상태별 매각 가능성 지표
- 가격이 아니라 “팔리기 쉬움”

## 5. Time & Stress (Pain Index)
- 고장 유형별 평균 다운타임
- 견인 / 렌트 발생 확률
- 달러 환산 금지

## 6. Residual Value Ratio
- 차급 × 연식/주행거리 구간
- 신차 대비 잔존 비율

## 7. Brand Reputation Multiplier
- 브랜드 직접 수치 ❌
- 신뢰도 그룹별 계수 ⭕

## 8. Opportunity Cost Floor
- 고철 수준 하한선 개념
- 절대 금액 대신 “선택권 상실” 표현
