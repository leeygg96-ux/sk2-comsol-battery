# 브랜치와 Git worktree 운영

## 브랜치 이름

- 열적 남용: `ta/<짧은-작업명>`
- 충·방전: `cd/<짧은-작업명>`
- 공통부: `shared/<짧은-작업명>`
- 저장소 관리: `ops/<짧은-작업명>`

예시:

```text
ta/interface-calibration
ta/heater-selection-fix
cd/initial-1d-model
shared/parameter-schema
```

## 권장 worktree 구성

기준 저장소를 `sk2-comsol-battery`에 둔 경우:

```bash
cd sk2-comsol-battery
git switch main
git pull --ff-only

git worktree add ../sk2-comsol-ta -b ta/interface-calibration main
git worktree add ../sk2-comsol-cd -b cd/initial-model main
```

그 뒤 Codex는 각각의 폴더에서 별도로 실행한다.

```bash
cd ../sk2-comsol-ta
codex

cd ../sk2-comsol-cd
codex
```

열적 남용 Codex 세션에는 `thermal-abuse/AGENTS.md`가, 충·방전 세션에는 `charge-discharge/AGENTS.md`가 추가로 적용된다.

작업이 끝난 브랜치를 병합한 뒤 worktree를 제거한다.

```bash
git worktree remove ../sk2-comsol-ta
git branch -d ta/interface-calibration
```

## 커밋 접두어

```text
feat(ta): ...
fix(ta): ...
docs(ta): ...
feat(cd): ...
refactor(shared): ...
chore(repo): ...
```

## 병합 전 최소 조건

- 관련 정적 검사 통과
- 과학적 변경 여부 명시
- COMSOL 실행 여부와 로그 위치 명시
- 생성된 MPH, class, log가 staged 상태가 아닌지 확인
