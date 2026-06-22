# 모델 결과를 논문 저장소로 전달하는 규약

논문 저장소에는 COMSOL 소스 전체를 복사하지 않는다. 검증된 실행 결과를 다음 단위로 전달한다.

```text
handoff/<run-id>/
├─ manifest.yml
├─ parameters.csv
├─ results.csv
├─ figures/
└─ checksums.sha256
```

`manifest.yml` 최소 항목:

```yaml
model_repository: sk2-comsol-battery
model_commit: <full git commit hash>
model_tag: <tag or null>
module: thermal-abuse
source_file: thermal-abuse/src/main/java/SOC100_3cell_offset_v02.java
comsol_version: "6.4"
run_timestamp: <ISO-8601 timestamp>
solver_status: <completed|failed|not-run>
log_location: <path or URI>
```

논문 본문·그림 캡션·부록은 이 manifest의 태그 또는 커밋을 기준으로 결과를 추적한다.
