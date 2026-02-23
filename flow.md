# AutoMoneyPit – User Decision Flow (v.1)

## 목적
이 Flow는 자동차 수리 vs 손절 판단을 위한 **Regret-based Decision Flow**다.  
가격 계산이나 시세 산출이 아니라, **후회 최소화 판단**을 목표로 한다.

- 판단은 빠르게
- 정밀화는 결과 페이지에서
- 계산기 느낌은 제거

---

## 전체 구조 (2-Phase)

1) **Fast Verdict (입력 최소화)**
2) **Simulation Lab (체류 + 정밀도 보정)**

UX/디자인은 이 문서 범위를 벗어난다.

---

# Phase 1. Fast Verdict (입력 단계)

### 입력 원칙
- 입력은 **3개**
- 한 화면에서 완료
- 신뢰는 주되, 계산을 지배하지 않음

---

### Input 1. Vehicle Type
**질문:** 차량 유형은?
- Sedan
- SUV
- Truck / Van

> 용도: 잔존가치/유동성 **미세 보정용**
> 핵심 계산 변수 아님

---

### Input 2. Mileage
**질문:** 현재 주행거리는?
- 숫자 입력 (마일)

> 용도: Failure Cascade의 **핵심 축**

---

### Input 3. Repair Quote
**질문:** 이번 수리 견적은?
- 숫자 입력 (USD, 대략)

> 용도:
> - 내부에서 points로 변환
> - RF(Repair Regret) 기본값 설정

---

### Action
**CTA:** [Analyze My Money Pit]

---

# Phase 1 Output. Instant Verdict

### 출력 형식
- 숫자 점수 ❌
- 상태 + 메타포 ⭕

### Verdict States
- 🟢 **Stable**
- 🟡 **Borderline**
- 🔴 **Time Bomb**

### 1줄 판정 문장
> “이 수리는 문제를 해결하기보다, 결정을 미루는 비용이 될 가능성이 큽니다.”

> 주의:
> - 확률/퍼센트/정확한 금액 노출 금지
> - 패턴 기반 판단임을 암시

---

# Phase 2. Simulation Lab (정밀화 단계)

## 목적
- 사용자가 **직접 상황을 조정**
- “내 판단”이라는 착각 유도
- 체류시간 증가

---

## Control Panel (Sliders / Toggles)

### Control 1. Failure Severity
**질문:** 실제 고장 수준은?
- General / Unknown
- Suspension / Brakes
- Engine / Transmission

> 효과:
> - RF 급상승/하락
> - Failure Cascade 반영

---

### Control 2. Mobility Status
**질문:** 현재 차량 상태는?
- Drivable
- Needs Tow

> 효과:
> - Pain Index 반영
> - RF 즉시 증가

---

### Control 3. Hassle Tolerance
**질문:** 차를 바꾸는 게 얼마나 싫은가?
- Hate Switching
- Neutral
- Want New Car

> 효과:
> - RM(정리 후회) 조정

---

### (Optional) Control 4. Vehicle Type Fine-tune
- Sedan
- SUV
- Truck

> 효과:
> - 잔존가치/매각 가능성 미세 조정
> - Phase 1 판정 뒤집지 않음

---

# Phase 2 Output. Dynamic Feedback

### 변화 요소
- Verdict State 변경
- RF vs RM 밸런스 시각화
- Money Pit 상태 변화 (범주형)

> 숫자 노출 ❌  
> 변화 방향만 시각적으로 표현

---

# Final Section. Closing Argument

### 동적 결론 문장 (1~2줄)
> “당신은 차를 바꾸는 걸 싫어하지만,  
> 이번 고장은 그 불편함을 감수할 만큼의 안정성을 주지 않습니다.”

### CTA
- [See Exit Options]
- [Try One More Scenario]

---

## 변경 금지 사항 (LOCKED)
- Phase 1 입력 개수 증가 ❌
- 차량 모델 / 연식 입력 ❌
- 확률, 퍼센트, 정확 금액 외부 노출 ❌
- 계산식 변경 ❌

---

## 요약
- **결정은 빠르게**
- **납득은 길게**
- **정밀도는 사용자가 완성**

이 Flow는 계산기가 아니라,
**결정을 멈추게 하는 장치**다.
