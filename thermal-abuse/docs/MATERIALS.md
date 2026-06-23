# Materials - SOC100 three-cell thermal-abuse v0.3

## Scope

`SOC100_3cell_offset_v03.java` assigns thermal properties through explicit COMSOL Material nodes rather than Heat Transfer physics-level user-defined properties. This is an API-organization change intended to preserve the v0.2 thermal behavior while making material ownership easier to audit in COMSOL.

## Named selections used for materials

| Selection | Domains |
|---|---|
| `shellAll` | `geom1_shell1_dom`, `geom1_shell2_dom`, `geom1_shell3_dom` |
| `activeAll` | `cell1Active`, `cell2Active`, `cell3Active` |
| `cell1Active` | `geom1_act1_dom` |
| `cell2Active` | `geom1_act2_dom` |
| `cell3Active` | `geom1_act3_dom` |
| `interfaceAll` | `geom1_int12_dom`, `geom1_int23_dom` |
| `negativeMetal` | negative tabs, plus optional negative busbar if enabled |
| `positiveMetal` | positive tabs, plus optional positive busbar if enabled |
| `heaterDomain` | `geom1_heater1_dom` |

## Material nodes

| Material node | Selection | Thermal conductivity | Density | Heat capacity |
|---|---|---|---|---|
| `matShell` | `shellAll` | `0.26[W/(m*K)]` | `1150[kg/m^3]` | `1700[J/(kg*K)]` |
| `matActive` | `activeAll` | diagonal tensor: `k_active_x`, `k_active_y`, `k_active_z` | `rho_active` | `Cp_active` |
| `matInterface` | `interfaceAll` | `k_int` | `1000[kg/m^3]` | `1000[J/(kg*K)]` |
| `matCopper` | `negativeMetal` | `400[W/(m*K)]` | `8960[kg/m^3]` | `385[J/(kg*K)]` |
| `matAluminum` | `positiveMetal` | `238[W/(m*K)]` | `2700[kg/m^3]` | `900[J/(kg*K)]` |
| `matHeater` | `heaterDomain` | `400[W/(m*K)]` | `1[kg/m^3]` | `1[J/(kg*K)]` |

## Scientific notes

- The v0.3 material values match the requested v03 material map.
- The active-domain conductivity remains parameterized by `k_active_x`, `k_active_y`, and `k_active_z`.
- The interface conductivity remains `k_int = gap_t/Rth_int`; `Rth_int` remains a calibration parameter.
- Heater density and heat capacity remain intentionally low so the heater solid approximates a heat-flux patch.
- `INCLUDE_BUSBARS` remains `false` by default; busbar material assignment only applies if optional busbar geometry is explicitly enabled.

## Local verification required

After COMSOL 6.4 compilation, inspect the generated MPH and confirm that every domain is covered by exactly one intended material selection, especially any domains split by CAD Boolean operations.
