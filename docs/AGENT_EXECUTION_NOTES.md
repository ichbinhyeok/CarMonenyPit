# 🕵️ AutoMoneyPit Agent Execution Notes (2026.02.24 최신화)

## 1. 현재 규칙 요약 (Core Rules Extracted)
*   **데이터 스펙 (`data-spec.md`)**: 정확한 단일 금액 단위 지양, 대신 **"범위(Range) / 비율 / 계수 / 확률"** 사용. 실시간 API 연동 불가. 
*   **계산식 (`계산식.md`)**: 단순 비용 비교가 아닌 **후회 기반 결정(Regret-Based Decision: RF vs RM)**. 결과값은 100단위로 반올림하여 신뢰성(EEAT) 보장.
*   **톤앤매너 (`implementation_plan.md`)**: "Visual Authority". 가볍고 유치한(Silly) 방식이 아니라, 금융 전문가와 같은 진지하고 직관적인(Direct) 경고(Iceberg Receipt) 제공.
*   **수익화/리드 전략**: `PartnerRoutingConfig`의 `approvalPending` 플래그로 제휴 승인 전/후 라우팅 자동 전환. 승인 전에는 `/lead-capture` waitlist 폼으로 fallback.
*   **마케팅 방식 (`marketing_agent_prompt.md`)**: "SEO 위기관리(Hijacking)". RF/RM 같은 개발 용어를 감추고 인간적이고 취약성을 인정하는 방식으로 신뢰 빌드업.

---

## 2. 완료된 Phase 요약

### Phase 1: 데이터 무결성 ✅
- `car_models.json` 중복 slug 해결 (Sentra, Grand Cherokee, Jetta)
- `@JsonIgnoreProperties(ignoreUnknown=true)` 적용 → 스키마 진화 안전
- `major_faults.json` 확장 메타데이터 PoC (F-150, Silverado)
- `DataIntegrityTest` 통과

### Phase 2: 판정 로직 통합 (SSOT) ✅
- `PSeoController`가 `DecisionEngine`을 직접 사용 (기존 naive 50% 규칙 제거)
- pSEO 페이지와 메인 결과 페이지의 판정 결과 100% 일치
- 모든 단위 테스트 통과

### Phase 3: 리드 파이프라인 ✅
- `PartnerRoutingConfig.java`: `app.partner.approval-pending` 플래그 기반 동적 라우팅
- `VerdictPresenter.java`: 하드코딩 URL → PartnerRoutingConfig 기반
- `LeadController.java`: `/lead` (CSV 로깅 + 파트너 리다이렉트) + `/lead-capture` (waitlist)
- `lead_capture.jte`: verdict별 맞춤 대기 페이지
- 모든 단위 테스트 통과

---

## 3. 해결된 충돌 지점
1. ~~**메인 결정 엔진 vs pSEO 결정 편차**~~ → Phase 2에서 `DecisionEngine` SSOT 통합 완료
2. ~~**데이터 확장 시 Jackson 파싱 에러**~~ → `@JsonIgnoreProperties(ignoreUnknown=true)` 적용 완료
3. ~~**하드코딩된 파트너 URL**~~ → Phase 3에서 `PartnerRoutingConfig`로 전환 완료
4. ~~**도메인 혼란 (automoneypit vs carmoneypit)**~~ → `app.baseUrl` 환경변수로 통일

## 4. 남은 작업
- **이메일 수집 백엔드**: lead_capture.jte의 이메일 저장 연동 필요
- **A/B 테스트 인프라**: CTA 문구/색상 변경 테스트 환경 구축
- **GA4 커스텀 이벤트**: verdict_shown, cta_click, lead_submit 등
- **파트너 승인 후**: `app.partner.approval-pending=false` 전환
- **VerdictConsistencyTest**: pSEO와 메인 엔진 출력 자동 일치 검증 테스트

---

## 5. 릴리즈 체크리스트 (Release Checklist)
- [x] (`Phase 1`) `gradlew test` 전체 통과 확인
- [x] (`Phase 1`) Data Integrity Validator가 중복 slug 검출 확인
- [x] (`Phase 2`) pSEO와 메인 엔진이 동일한 `DecisionEngine` 사용 확인
- [x] (`Phase 2`) Sitemap에 최종 Canonical URL만 등재
- [x] (`Phase 3`) `approvalPending=true` 상태에서 `/lead-capture`로 정상 fallback 확인
- [x] (`Phase 3`) LeadController의 입력값 sanitize 처리 확인
- [ ] 이메일 수집 백엔드 연동
- [ ] GA4 전환 이벤트 설정
- [ ] 파트너 승인 후 실제 리다이렉트 테스트
