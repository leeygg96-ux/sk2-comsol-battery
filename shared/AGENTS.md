# Shared-module instructions

- `shared/` is not a general utility folder.
- Add an item only when both `thermal-abuse/` and `charge-discharge/` consume the same stable contract.
- A shared change must state which checks were run for both modules.
- Do not move thermal-runaway kinetics, abuse thresholds, or heater logic into shared code.
- Prefer data schemas and coordinate/unit conventions before shared implementation code.
