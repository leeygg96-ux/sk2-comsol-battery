# SK 2차년도 COMSOL 배터리 모델

COMSOL 배터리 모델의 공통 상위 구조는 공유하되, **열적 남용(thermal abuse)**과 **충·방전(charge/discharge)**을 독립 모듈로 관리하는 저장소다.

논문 원고와 투고 파일은 이 저장소에 넣지 않는다. 논문은 별도 저장소 `sk2-paper`에서 관리하고, 이 저장소의 검증된 태그와 내보낸 결과만 참조한다.

## 구조

```text
sk2-comsol-battery/
├─ shared/              # 두 모듈이 실제로 함께 쓰는 항목만
├─ thermal-abuse/       # 열적 남용·열폭주·전파 모델
├─ charge-discharge/    # 충·방전 전기화학/열 모델
└─ docs/                # 저장소 전체 규칙과 의사결정
```

현재 시작점은 `thermal-abuse/src/main/java/SOC100_3cell_offset_v02.java`이다.

## 핵심 운영 원칙

1. `main`은 재현 가능한 통합 상태만 유지한다.
2. 열적 남용 작업은 `ta/<task>`, 충·방전 작업은 `cd/<task>` 브랜치를 사용한다.
3. 동시에 작업할 때는 Git worktree로 작업 폴더를 물리적으로 분리한다.
4. `shared/`는 두 모듈에서 실제 중복이 확인된 뒤에만 사용한다.
5. Java가 모델 정의의 원본이다. 생성된 MPH와 대용량 결과는 기본적으로 Git에 커밋하지 않는다.
6. 물리식·경계조건·물성치를 바꾼 커밋은 근거와 검증 결과를 함께 기록한다.

## 첫 확인

```bash
python thermal-abuse/tests/verify_source_contract.py
python thermal-abuse/scripts/extract_parameters.py --check
```

COMSOL 6.4가 설치된 Windows에서는 다음 스크립트로 Java 컴파일과 MPH 생성을 시도할 수 있다.

```powershell
./thermal-abuse/scripts/compile-model.ps1
./thermal-abuse/scripts/build-mph.ps1
```

세부 브랜치 및 worktree 사용법은 `docs/BRANCHING_AND_WORKTREES.md`를 참고한다.
