# 🕵️ AutoMoneyPit Agent Execution Notes

## 1. 현재 규칙 요약 (Core Rules Extracted)
*   **데이터 스펙 (`data-spec.md`)**: 정확한 단일 금액 단위 지양, 대신 **"범위(Range) / 비율 / 계수 / 확률"** 사용. 실시간 API 연동 불가. 
*   **계산식 (`계산식.md`)**: 단순 비용 비교가 아닌 **후회 기반 결정(Regret-Based Decision: RF vs RM)**. 결과값은 100단위로 반올림하여 신뢰성(EEAT) 보장.
*   **톤앤매너 (`implementation_plan.md`)**: "Visual Authority". 가볍고 유치한(Silly) 방식이 아니라, 금융 전문가와 같은 진지하고 직관적인(Direct) 경고(Iceberg Receipt) 제공.
*   **수익화/리드 전략 (`improvement_strategy.md`)**: 제휴 승인 전에는 SEO/소셜 트래픽을 내부 리드폼이나 문의로 돌려 **전환 가능성(CTR)을 먼저 증명**해야 함. (승인 대기 모드)
*   **마케팅 방식 (`marketing_agent_prompt.md`)**: "SEO 위기관리(Hijacking)". RF/RM 같은 개발 용어를 감추고 인간적이고 취약성을 인정하는 방식으로 신뢰 빌드업.

---

## 2. 충돌 지점 및 쟁점 (Conflicts & Bottlenecks)
1.  **메인 결정 엔진 vs pSEO 결정 편차**: 
    *   *문제*: 현재 pSEO는 "수리비 > 차값 50%"라는 단순 규칙을 사용 중이나, 메인 엔진은 "RF vs RM" (Regret 기반)을 사용. 일치하지 않으면 사용자가 랜딩 후 결과 불일치로 이탈 발생.
    *   *해결*: 두 로직을 `VerdictPolicyService` 하나로 통합 (SSOT).
2.  **데이터 확장 시 Jackson 파싱 에러 위험**:
    *   *문제*: 신뢰도(confidence), last_updated, 출처(sources) 등의 필드를 대량 추가하면 기존 파서에서 에러가 발생하여 서비스 장애 유발 가능.
    *   *해결*: `@JsonIgnoreProperties(ignoreUnknown=true)` 적극 도입 및 점진적 필드 매핑(schema evolution).
3.  **데이터 품질(환각) vs 양(Volume)**:
    *   *문제*: 수백 개의 차량 모델에 한 번에 출처와 신뢰도를 자동화로 넣을 경우 AI 환각(Hallucination) 리스크 큼.
    *   *해결*: 상위 2~3개 베스트셀러 모델에만 PoC로 수동/반자동 데이터 강건화 적용, 나머지는 시스템 검증 후 스크립트로 처리.

---

## 3. 에이전트 채택 우선순위 및 해결 방식 (Adopted Strategy)
*   **안정성 최우선 방어적 코딩**: 데이터 스키마 확장은 기존 시스템을 깨지 않는 `fallback` 및 `ignoreUnknown` 설정을 기반으로 진행.
*   **페이즈(Phase) 분할 실행**: 방대한 작업이 한 번에 섞이지 않도록 기능 단위로 분할하여 PR 커밋 진행.
    *   **Phase 1**: 데이터 스키마 유연성 확보 & 무결성 테스트(CI) 도입, 상위 모델 JSON 신뢰도 필드 추가.
    *   **Phase 2**: `VerdictPolicyService` 통합 및 pSEO 정합성 개선.
    *   **Phase 3**: 제휴 승인 전/후 대응 가능한 `/lead` 라우팅(플래그 기반) 및 분석 로직, 최종 산출물 리포트(3종) 발행.

---

## 4. 릴리즈 체크리스트 (Release Checklist)
- [ ] (`Phase 1`) `gradlew test` 등 유닛 테스트에서 기존/신규 Data 관련 기능 100% 통과 확인.
- [ ] (`Phase 1`) Data Integrity Validator(스크립트 또는 JUnit)가 누락된 필수 필드, slug 중복 등을 올바로 잡아내는지 테스트.
- [ ] (`Phase 2`) pSEO 생성 결과물과 웹 메인 엔진의 Verdict 판정(SELL/FIX 등)이 100% 일치하는지 스모크 테스트.
- [ ] (`Phase 2`) Sitemap에 버킷 301 리다이렉트가 아닌 최종 Canonical URL들만 정상 등재되었는지 확인.
- [ ] (`Phase 3`) `APPROVAL_PENDING=true` 상태에서 `/lead`가 파트너사 URL이 아닌 내부 문의/리드폼으로 정상 fallback 되는지 점검.
- [ ] (`Phase 3`) UTM 및 Referrer 값이 `/lead`를 거칠 때 XSS나 오픈 리다이렉트에 취약하지 않고 인코딩 전달되는지 테스트.
- [ ] CHANGELOG_AGENT.md, DATA_REPORT.md, REVENUE_MODEL.md 최종 작성 완료.
