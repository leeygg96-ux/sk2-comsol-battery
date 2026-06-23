# Thermal-abuse documentation changelog

## v0.3.1 - COMSOL material tensor API fix

- Fixed COMSOL Java API material thermal-conductivity tensor format from nested String[][] to flat String[] to resolve nonscalar matrix item error.

## v0.3.0 - material nodes and explicit solver sequence

- Added `SOC100_3cell_offset_v03.java` from `SOC100_3cell_offset_v02.java`.
- Preserved `SOLVE_MODEL = false` by default.
- Preserved the three-cell geometry, stacked `z1`, `z2`, `z3` interpretation, and cell-to-cell effective interface layers.
- Preserved cell-specific SOH handles `m_soh1`, `m_soh2`, `m_soh3`, `h_soh1`, `h_soh2`, and `h_soh3`.
- Preserved `Hchem_base = 12000[J]` as the already volume-corrected total additional energy for one scaled active cell.
- Added named selections `shellAll`, `activeAll`, `interfaceAll`, `negativeMetal`, `positiveMetal`, `heaterDomain`, `cell1Active`, `cell2Active`, and `cell3Active`.
- Replaced Heat Transfer physics-level user-defined material-property assignments with explicit COMSOL Material nodes.
- Added an explicit time-dependent solver sequence with `range(0,0.1,500)`, maximum BDF order `2`, a segregated temperature/reaction-state structure, and PARDISO direct solver nodes.
- Added best-effort solver variable lower-limit API hooks for reaction-state variables and `Qaccum`; local COMSOL 6.4 verification is required because lower-limit property names can depend on the exported solver tree.

## Scientific behavior intentionally not changed

- Thermal runaway kinetic parameters were not changed.
- Heat-release values were not changed.
- Threshold temperatures were not changed.
- Reaction equations were not changed.
- Geometry dimensions, interface thickness, and mesh level were not changed.
- `INCLUDE_BUSBARS` was not enabled.
