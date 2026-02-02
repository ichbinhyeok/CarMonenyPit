# 🎱 AutoMoneyPit 검색어 자동 생성 매트릭스 (Keyword Generator)

> **[Instruction]**: 이 문서는 1,500개 이상의 타겟 검색어를 생성하기 위한 **조합 공식**이다. 
> 이 데이터를 기반으로 나에게 "검색어 50개 뽑아줘"라고 하면, 매번 다른 조합으로 고수익 키워드를 생성하라.

---

## 1. 조합 공식 (The Formula)

검색어는 아래 [A]와 [B] 중 하나를 선택하고, [C]를 결합하여 생성한다.

`[A: 차종]` + **`([B1: 고장] OR [B2: 마일리지] OR [B3: 상태])`** + `[C: 의도 질문]`

> **예시 1 (고장)**: `2015 Nissan Altima` + `CVT transmission failure` + `worth fixing`
> **예시 2 (마일리지)**: `2012 Toyota Camry` + `high mileage` + `life expectancy`
> **예시 3 (상태)**: `2014 Ford F-150` + `blown engine` + `scrap value`

---

## 2. 데이터 블록 (Building Blocks)

### [A] 차종 및 연식 (Target Models)
*데이터가 풍부하고 고질병이 확실한 모델들*

- **Trucks**: 2011-2019 Ford F-150 / 2014-2018 Chevy Silverado / 2009-2018 RAM 1500
- **Sedans**: 2013-2017 Honda Accord / 2012-2017 Toyota Camry / 2013-2018 Nissan Altima / 2016-2020 Honda Civic / 2014-2018 Mazda3 / 2011-2018 VW Jetta
- **SUVs**: 2011-2019 Ford Explorer / 2014-2020 Nissan Rogue / 2012-2016 Honda CR-V / 2013-2018 Toyota RAV4 / 2011-2021 Jeep Grand Cherokee / 2013-2019 Ford Escape
- **Luxury**: 2012-2018 BMW 3 Series / 2015-2021 Mercedes C-Class / 2017-2023 Audi A4 / 2014-2023 Porsche Macan

### [B1] 고장 증상 (Pain Points - 수리 고민)
*돈 많이 드는 수리*
- expensive repair quote
- blown head gasket
- timing chain rattle
- excessive oil consumption
- engine knocking / rod knock
- cam phaser noise
- turbo failure
- transmission slipping / CVT failure
- torque converter shudder
- transmission rebuild cost
- air suspension failure
- catalytic converter theft
- hybrid battery replacement

### [B2] 마일리지 상황 (Mileage Anxiety - 수명 고민)
*고장은 안 났지만 불안한 상태*
- high mileage
- over 150k miles
- over 200k miles
- 100k mile maintenance
- reliability at high mileage
- life expectancy
- max mileage
- longevity

### [B3] 상태/가치 (Condition - 처분 고민)
*똥차 상태에서의 가치 확인*
- bad transmission value
- blown engine value
- scrap value
- salvage value
- trade in value with mechanical issues
- non-running value
- totaled car value
- accident damage value

### [C] 의도 질문 (Search Intent)
*사용자의 고민 (Situation에 따라 적절히 매칭)*

- **(수리용)**: worth fixing?, repair or sell?, fix vs trade in, should I repair?, sinking money into old car
- **(마일리지용)**: keep or sell?, when to sell?, how long will it last?, is it reliable?, maintenance cost vs value
- **(상태용)**: how much is it worth?, sell as is?, scrap or sell?

---

## 3. 고급 조합 필터 (Smart Filters)

무작위 조합이 아니라, **상황에 맞는(Contextual)** 조합을 만들어라.

1.  **Nissan/CVT (고질병)**: `Nissan` + `CVT/Transmission` + `worth fixing`
2.  **Subaru/Head Gasket**: `Subaru` + `Head Gasket` + `repair or sell`
3.  **High Mileage**: `Toyota/Honda` + `200k miles` + `keep or sell`
4.  **Trucks/Engine**: `Ford/Chevy/RAM` + `Cam Phaser/Lifter` + `fix or trade in`
5.  **Scrap Value**: `Old Luxury (BMW/Audi)` + `blown engine` + `scrap value`

---

## 4. 실행 명령 (Execute)

> "자, 이제 [A]와 [B1, B2, B3]를 골고루 섞어서 **유효한 검색어 50개**를 리스트로 뽑아줘. 
> 단, 차종 특성에 맞는 고질병([B1])과 상황([B2, B3])을 자연스럽게 매칭해."
