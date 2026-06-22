# Codex task template

## Objective
정확히 어떤 산출물을 완료해야 하는가?

## Read first
- `AGENTS.md`
- 해당 모듈의 `AGENTS.md`
- 관련 model card / decision log

## Allowed paths
수정 가능한 디렉터리와 파일을 명시한다.

## Forbidden changes
변경하면 안 되는 물리식, 태그, 단위, 경계조건, 파일을 명시한다.

## Scientific context
현재 가정, 알려진 placeholder, 검증되지 않은 부분을 적는다.

## Acceptance criteria
예:

1. Java 정적 계약 검사 통과
2. COMSOL 6.4 컴파일 성공
3. MPH 생성 성공
4. 지정 probe/selection 존재
5. 변경 전후 차이와 남은 불확실성 기록

## Evidence to return
- 수정 파일 목록
- 실행 명령
- 테스트/COMSOL 로그
- 수치 결과 또는 실패 원인
- 과학적 의미가 바뀐 부분
