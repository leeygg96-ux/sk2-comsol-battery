/*
 * SOC100_3cell_offset_v01.java
 *
 * Three-cell thermal-abuse / propagation baseline derived from
 * SOC100_paper_revise_2.
 *
 * Main assumptions in v0.1
 *  - Three geometrically identical pouch cells are stacked in the z direction.
 *  - The original lumped Arrhenius thermal-runaway reaction model is solved
 *    independently in each active domain.
 *  - Hchem is treated as the already volume-corrected total additional energy
 *    for ONE scaled cell, as specified by the model owner.
 *  - Cell-to-cell coupling is represented by a thin effective interlayer.
 *    Its areal resistance Rth_int is a calibration parameter, not a fixed
 *    literature value.
 *  - Busbars are disabled by default. An approximate tab-to-tab thermal bridge
 *    can be enabled after actual module dimensions/materials are confirmed.
 *  - External abuse heating is represented by a very thin heater solid on the
 *    outer broad face of Cell 1. Its volumetric source is q_heater/heater_t,
 *    equivalent to the original surface heat flux when heater thermal mass is
 *    negligible.
 *
 * IMPORTANT
 *  - This file cannot be COMSOL-compiled in the ChatGPT environment.
 *    Run it with COMSOL 6.4 Java or compile it using the COMSOL classpath.
 *  - Generated geometry selections (geom1_<feature>_dom) are used so that
 *    domain numbering is not hard-coded.
 */

import com.comsol.model.*;
import com.comsol.model.util.*;

public class SOC100_3cell_offset_v02 {

  /* Set true only after an approximate busbar geometry is desired. */
  private static final boolean INCLUDE_BUSBARS = false;

  /* Set true to solve before saving. False creates an unsolved MPH template. */
  private static final boolean SOLVE_MODEL = false;

  private static String[] append(String[] base, String... extra) {
    String[] out = new String[base.length + extra.length];
    System.arraycopy(base, 0, out, 0, base.length);
    System.arraycopy(extra, 0, out, base.length, extra.length);
    return out;
  }

  private static void addBlock(
      Model model,
      String tag,
      String label,
      String[] size,
      String[] pos) {
    model.component("comp1").geom("geom1").create(tag, "Block");
    model.component("comp1").geom("geom1").feature(tag).label(label);
    model.component("comp1").geom("geom1").feature(tag).set("base", "corner");
    model.component("comp1").geom("geom1").feature(tag).set("size", size);
    model.component("comp1").geom("geom1").feature(tag).set("pos", pos);
    model.component("comp1").geom("geom1").feature(tag).set("selresult", true);
  }

  private static void addCell(Model model, int i, String z0) {
    String n = Integer.toString(i);

    addBlock(model, "shell" + n, "Cell " + n + " shell",
        new String[]{"cell_L", "cell_W", "cell_T"},
        new String[]{"0", "0", z0});

    addBlock(model, "act" + n, "Cell " + n + " active",
        new String[]{"active_L", "active_W", "active_T"},
        new String[]{"(cell_L-active_L)/2", "(cell_W-active_W)/2",
            z0 + "+(cell_T-active_T)/2"});

    addBlock(model, "tabN" + n, "Cell " + n + " negative tab",
        new String[]{"tab_L", "tab_W", "tab_T"},
        new String[]{"-tab_L", "(cell_W-tab_W)/2",
            z0 + "+(cell_T-tab_T)/2"});

    addBlock(model, "tabP" + n, "Cell " + n + " positive tab",
        new String[]{"tab_L", "tab_W", "tab_T"},
        new String[]{"cell_L", "(cell_W-tab_W)/2",
            z0 + "+(cell_T-tab_T)/2"});
  }

  public static Model buildModel() {
    Model model = ModelUtil.create("Model");
    model.label("SOC100_3cell_offset_v02.mph");

    /* ------------------------------------------------------------------ */
    /* 1. Geometry and module-layout parameters                            */
    /* ------------------------------------------------------------------ */
    model.param().set("scale", "1/3", "Geometric scale retained from the original model");
    model.param().set("cell_L", "354[mm]*scale");
    model.param().set("cell_W", "101[mm]*scale");
    model.param().set("cell_T", "9.5[mm]*scale");
    model.param().set("active_L", "350[mm]*scale");
    model.param().set("active_W", "98[mm]*scale");
    model.param().set("active_T", "7.5[mm]*scale");
    model.param().set("tab_L", "28[mm]*scale");
    model.param().set("tab_W", "50[mm]*scale");
    model.param().set("tab_T", "2[mm]*scale");

    /* Placeholder full-scale separation. Replace with the measured module value. */
    model.param().set("gap_full", "0.6[mm]", "Placeholder full-scale face-to-face interface thickness");
    model.param().set("gap_t", "gap_full*scale", "Scaled effective interface thickness");
    model.param().set("cell_pitch", "cell_T+gap_t");
    model.param().set("z1", "0");
    model.param().set("z2", "cell_pitch");
    model.param().set("z3", "2*cell_pitch");
    model.param().set("stack_H", "3*cell_T+2*gap_t");

    /* Effective interface: k_int = gap_t/Rth_int. Rth_int must be calibrated. */
    model.param().set("Rth_int", "1e-3[m^2*K/W]", "Placeholder areal cell-to-cell thermal resistance");
    model.param().set("k_int", "gap_t/Rth_int", "Equivalent interface thermal conductivity");
    model.param().set("rho_int", "1000[kg/m^3]", "Placeholder interface density");
    model.param().set("Cp_int", "1000[J/(kg*K)]", "Placeholder interface heat capacity");

    /* Approximate busbar parameters. Geometry is created only if INCLUDE_BUSBARS=true. */
    model.param().set("busbar_margin", "1[mm]*scale");
    model.param().set("busbar_z0", "(cell_T-tab_T)/2");
    model.param().set("busbar_H", "z3+(cell_T+tab_T)/2-busbar_z0");

    /* ------------------------------------------------------------------ */
    /* 2. Original thermal-runaway kinetic parameters                      */
    /* ------------------------------------------------------------------ */
    model.param().set("Ae", "3e15[1/s]", "Electrolyte decomposition frequency factor");
    model.param().set("Apos1", "1.75e9[1/s]", "Positive-solvent frequency factor 1");
    model.param().set("Apos2", "1.077e12[1/s]", "Positive-solvent frequency factor 2");
    model.param().set("Asei", "1.667e15[1/s]", "SEI decomposition frequency factor");
    model.param().set("Ea_e", "1.7e5[J/mol]");
    model.param().set("Ea_neg", "3.3e4[J/mol]");
    model.param().set("Ea_pos1", "1.1495e5[J/mol]");
    model.param().set("Ea_pos2", "1.5888e5[J/mol]");
    model.param().set("Ea_sei", "1.3508e5[J/mol]");
    model.param().set("Hele", "800[J/g]");
    model.param().set("Hneg", "1714[J/g]");
    model.param().set("Hpos1", "77[J/g]");
    model.param().set("Hpos2", "84[J/g]");
    model.param().set("Hsei", "257[J/g]");
    model.param().set("c_sei_ref", "1");

    model.param().set("Hsep", "-233.2[J/g]");
    model.param().set("Asep", "1.5e50[1/s]");
    model.param().set("Ea_sep", "4.2e5[J/mol]");

    model.param().set("ce0", "1");
    model.param().set("cneg0", "1");
    model.param().set("csei0", "0.15");
    model.param().set("cpos10", "0.999");
    model.param().set("cpos20", "0.999");
    model.param().set("csep0", "1");
    model.param().set("alpha", "0.7");

    /* Cell-specific phenomenological SOH handles retained for future coupling. */
    model.param().set("m_soh1", "1", "Cell 1 effective reacting-mass factor");
    model.param().set("m_soh2", "1", "Cell 2 effective reacting-mass factor");
    model.param().set("m_soh3", "1", "Cell 3 effective reacting-mass factor");
    model.param().set("h_soh1", "1", "Cell 1 additional-heat factor");
    model.param().set("h_soh2", "1", "Cell 2 additional-heat factor");
    model.param().set("h_soh3", "1", "Cell 3 additional-heat factor");

    /* Hchem_base is the volume-corrected total energy for ONE scaled cell. */
    model.param().set("Hchem_base", "12000[J]", "Corrected total additional energy per scaled cell");
    model.param().set("V_act_cell", "350[mm]*98[mm]*7.5[mm]*scale^3", "Active volume of one scaled cell");
    model.param().set("dt_chem", "5[s]");

    /* ------------------------------------------------------------------ */
    /* 3. Heat-transfer and abuse parameters                               */
    /* ------------------------------------------------------------------ */
    model.param().set("T_init", "35[degC]");
    model.param().set("T_amb", "35[degC]");
    model.param().set("h_conv", "7.17[W/(m^2*K)]");
    model.param().set("q_heater", "15000[W/m^2]");
    model.param().set("T_heater_off", "500[K]");
    model.param().set("heater_L", "cell_L/2");
    model.param().set("heater_W", "cell_W/2");
    model.param().set("heater_t", "0.05[mm]", "Numerically thin heater-solid thickness");

    /* Active-cell thermal properties are parameterized for later anisotropy. */
    model.param().set("k_active_x", "98[W/(m*K)]");
    model.param().set("k_active_y", "98[W/(m*K)]");
    model.param().set("k_active_z", "98[W/(m*K)]");
    model.param().set("rho_active", "1800[kg/m^3]");
    model.param().set("Cp_active", "1200[J/(kg*K)]");

    model.component().create("comp1", true);
    model.component("comp1").geom().create("geom1", 3);
    model.component("comp1").geom("geom1").geomRep("cadps");
    model.component("comp1").geom("geom1").designBooleans(true);

    /* ------------------------------------------------------------------ */
    /* 4. Three-cell geometry                                              */
    /* ------------------------------------------------------------------ */
    addCell(model, 1, "z1");
    addCell(model, 2, "z2");
    addCell(model, 3, "z3");

    addBlock(model, "int12", "Effective interface Cell 1-2",
        new String[]{"cell_L", "cell_W", "gap_t"},
        new String[]{"0", "0", "cell_T"});
    addBlock(model, "int23", "Effective interface Cell 2-3",
        new String[]{"cell_L", "cell_W", "gap_t"},
        new String[]{"0", "0", "z2+cell_T"});

    /* External heater on the outer broad face of Cell 1. */
    addBlock(model, "heater1", "Cell 1 external heater",
        new String[]{"heater_L", "heater_W", "heater_t"},
        new String[]{"(cell_L-heater_L)/2", "(cell_W-heater_W)/2", "-heater_t"});

    if (INCLUDE_BUSBARS) {
      /*
       * Deliberately simple thermal-bridge representation only.
       * Replace this with measured busbar geometry before electrical coupling.
       */
      addBlock(model, "busN", "Approximate negative busbar",
          new String[]{"tab_L+busbar_margin", "tab_W", "busbar_H"},
          new String[]{"-tab_L-busbar_margin", "(cell_W-tab_W)/2", "busbar_z0"});
      addBlock(model, "busP", "Approximate positive busbar",
          new String[]{"tab_L+busbar_margin", "tab_W", "busbar_H"},
          new String[]{"cell_L", "(cell_W-tab_W)/2", "busbar_z0"});
    }

    model.component("comp1").geom("geom1").run();

    /* ------------------------------------------------------------------ */
    /* 5. Robust named selections based on geometry-feature output         */
    /* ------------------------------------------------------------------ */
    model.component("comp1").selection().create("activeAll", "Union");
    model.component("comp1").selection("activeAll").set("input",
        new String[]{"geom1_act1_dom", "geom1_act2_dom", "geom1_act3_dom"});
    model.component("comp1").selection("activeAll").label("All active domains");

    model.component("comp1").selection().create("interfaceAll", "Union");
    model.component("comp1").selection("interfaceAll").set("input",
        new String[]{"geom1_int12_dom", "geom1_int23_dom"});
    model.component("comp1").selection("interfaceAll").label("Cell-to-cell effective interfaces");

    String[] negInputs = new String[]{"geom1_tabN1_dom", "geom1_tabN2_dom", "geom1_tabN3_dom"};
    String[] posInputs = new String[]{"geom1_tabP1_dom", "geom1_tabP2_dom", "geom1_tabP3_dom"};
    if (INCLUDE_BUSBARS) {
      negInputs = append(negInputs, "geom1_busN_dom");
      posInputs = append(posInputs, "geom1_busP_dom");
    }

    model.component("comp1").selection().create("negativeMetal", "Union");
    model.component("comp1").selection("negativeMetal").set("input", negInputs);
    model.component("comp1").selection("negativeMetal").label("Negative tabs and optional busbar");

    model.component("comp1").selection().create("positiveMetal", "Union");
    model.component("comp1").selection("positiveMetal").set("input", posInputs);
    model.component("comp1").selection("positiveMetal").label("Positive tabs and optional busbar");

    /* Select all domains, then generate the true exterior-boundary selection. */
    model.component("comp1").selection().create("allDom", "Explicit");
    model.component("comp1").selection("allDom").geom("geom1", 3);
    model.component("comp1").selection("allDom").all();

    model.component("comp1").selection().create("outerBnd", "Adjacent");
    model.component("comp1").selection("outerBnd").set("entitydim", 3);
    model.component("comp1").selection("outerBnd").set("input", new String[]{"allDom"});
    model.component("comp1").selection("outerBnd").set("outputdim", 2);
    model.component("comp1").selection("outerBnd").set("exterior", true);
    model.component("comp1").selection("outerBnd").set("interior", false);
    model.component("comp1").selection("outerBnd").label("True exterior boundaries");

    /* ------------------------------------------------------------------ */
    /* 6. Local variables and thermal-runaway sources                      */
    /* ------------------------------------------------------------------ */
    model.component("comp1").variable().create("varTR");
    model.component("comp1").variable("varTR").selection().named("activeAll");

    model.component("comp1").variable("varTR").set("m_soh_loc",
        "if(z<z2,m_soh1,if(z<z3,m_soh2,m_soh3))",
        "Cell-wise effective reacting-mass factor");
    model.component("comp1").variable("varTR").set("h_soh_loc",
        "if(z<z2,h_soh1,if(z<z3,h_soh2,h_soh3))",
        "Cell-wise additional-heat factor");

    model.component("comp1").variable("varTR").set("Wc_loc", "m_soh_loc*alpha*391.14[kg/m^3]");
    model.component("comp1").variable("varTR").set("We_loc", "m_soh_loc*alpha*420[kg/m^3]");
    model.component("comp1").variable("varTR").set("Wp_loc", "m_soh_loc*alpha*696.29[kg/m^3]");
    model.component("comp1").variable("varTR").set("Wsep_loc", "m_soh_loc*alpha*68.44[kg/m^3]");
    model.component("comp1").variable("varTR").set("Hchem_loc", "h_soh_loc*Hchem_base");

    model.component("comp1").variable("varTR").set("Aneg", "if(T<260[degC],0.035[1/s],5[1/s])");
    model.component("comp1").variable("varTR").set("R_e", "Ae*exp(-Ea_e/R_const/T)*c_e");
    model.component("comp1").variable("varTR").set("R_neg",
        "Aneg*c_neg*exp(-Ea_neg/R_const/T)*exp(-c_sei/c_sei_ref)");
    model.component("comp1").variable("varTR").set("R_sei",
        "Asei*exp(-Ea_sei/R_const/T)*c_sei-5*(Aneg*c_neg*exp(-Ea_neg/R_const/T)*exp(-c_sei/c_sei_ref))");
    model.component("comp1").variable("varTR").set("R_pos1",
        "Apos1*c_pos1*(1-c_pos1)*exp(-Ea_pos1/R_const/T)");
    model.component("comp1").variable("varTR").set("R_pos2",
        "Apos2*c_pos2*(1-c_pos2)*exp(-Ea_pos2/R_const/T)");
    model.component("comp1").variable("varTR").set("R_sep",
        "Asep*exp(-Ea_sep/R_const/T)*c_sep");

    model.component("comp1").variable("varTR").set("Qsei",
        "if(T>=50[degC],Wc_loc*Hsei*R_sei,0)");
    model.component("comp1").variable("varTR").set("Qne",
        "if(T>=50[degC],Wc_loc*Hneg*R_neg,0)");
    model.component("comp1").variable("varTR").set("Qele",
        "if(T>=140[degC],We_loc*Hele*R_e,0)");
    model.component("comp1").variable("varTR").set("Qpe1",
        "if(T>=180[degC],Wp_loc*Hpos1*R_pos1,0)");
    model.component("comp1").variable("varTR").set("Qpe2",
        "if(T>=220[degC],Wp_loc*Hpos2*R_pos2,0)");
    model.component("comp1").variable("varTR").set("Qsep",
        "if(T>=120[degC],Wsep_loc*Hsep*R_sep,0)");

    /* max() prevents a numerical overshoot from producing negative additional heat. */
    model.component("comp1").variable("varTR").set("Qchem0",
        "if(c_sep<=0.025,max(Hchem_loc-Qaccum,0[J])/dt_chem,0[W])");
    model.component("comp1").variable("varTR").set("Q_tot",
        "Qsei+Qne+Qele+Qpe1+Qpe2+Qsep+Qchem0/V_act_cell");

    /* ------------------------------------------------------------------ */
    /* 7. Heat transfer                                                    */
    /* ------------------------------------------------------------------ */
    model.component("comp1").physics().create("ht", "HeatTransfer", "geom1");
    model.component("comp1").physics("ht").feature("init1").set("Tinit", "T_init");

    /* Default solid model: pouch shell / enclosure properties. */
    model.component("comp1").physics("ht").feature("solid1").set("k_mat", "userdef");
    model.component("comp1").physics("ht").feature("solid1")
        .set("k", new String[][]{{"0.26[W/(m*K)]"}, {"0"}, {"0"},
                                  {"0"}, {"0.26[W/(m*K)]"}, {"0"},
                                  {"0"}, {"0"}, {"0.26[W/(m*K)]"}});
    model.component("comp1").physics("ht").feature("solid1").set("rho_mat", "userdef");
    model.component("comp1").physics("ht").feature("solid1").set("rho", "1150[kg/m^3]");
    model.component("comp1").physics("ht").feature("solid1").set("Cp_mat", "userdef");
    model.component("comp1").physics("ht").feature("solid1").set("Cp", "1700[J/(kg*K)]");

    model.component("comp1").physics("ht").create("solidAct", "SolidHeatTransferModel", 3);
    model.component("comp1").physics("ht").feature("solidAct").selection().named("activeAll");
    model.component("comp1").physics("ht").feature("solidAct").set("k_mat", "userdef");
    model.component("comp1").physics("ht").feature("solidAct")
        .set("k", new String[][]{{"k_active_x"}, {"0"}, {"0"},
                                  {"0"}, {"k_active_y"}, {"0"},
                                  {"0"}, {"0"}, {"k_active_z"}});
    model.component("comp1").physics("ht").feature("solidAct").set("rho_mat", "userdef");
    model.component("comp1").physics("ht").feature("solidAct").set("rho", "rho_active");
    model.component("comp1").physics("ht").feature("solidAct").set("Cp_mat", "userdef");
    model.component("comp1").physics("ht").feature("solidAct").set("Cp", "Cp_active");

    model.component("comp1").physics("ht").create("solidInt", "SolidHeatTransferModel", 3);
    model.component("comp1").physics("ht").feature("solidInt").selection().named("interfaceAll");
    model.component("comp1").physics("ht").feature("solidInt").set("k_mat", "userdef");
    model.component("comp1").physics("ht").feature("solidInt")
        .set("k", new String[][]{{"k_int"}, {"0"}, {"0"},
                                  {"0"}, {"k_int"}, {"0"},
                                  {"0"}, {"0"}, {"k_int"}});
    model.component("comp1").physics("ht").feature("solidInt").set("rho_mat", "userdef");
    model.component("comp1").physics("ht").feature("solidInt").set("rho", "rho_int");
    model.component("comp1").physics("ht").feature("solidInt").set("Cp_mat", "userdef");
    model.component("comp1").physics("ht").feature("solidInt").set("Cp", "Cp_int");

    model.component("comp1").physics("ht").create("solidNeg", "SolidHeatTransferModel", 3);
    model.component("comp1").physics("ht").feature("solidNeg").selection().named("negativeMetal");
    model.component("comp1").physics("ht").feature("solidNeg").set("k_mat", "userdef");
    model.component("comp1").physics("ht").feature("solidNeg")
        .set("k", new String[][]{{"400[W/(m*K)]"}, {"0"}, {"0"},
                                  {"0"}, {"400[W/(m*K)]"}, {"0"},
                                  {"0"}, {"0"}, {"400[W/(m*K)]"}});
    model.component("comp1").physics("ht").feature("solidNeg").set("rho_mat", "userdef");
    model.component("comp1").physics("ht").feature("solidNeg").set("rho", "8960[kg/m^3]");
    model.component("comp1").physics("ht").feature("solidNeg").set("Cp_mat", "userdef");
    model.component("comp1").physics("ht").feature("solidNeg").set("Cp", "385[J/(kg*K)]");

    model.component("comp1").physics("ht").create("solidPos", "SolidHeatTransferModel", 3);
    model.component("comp1").physics("ht").feature("solidPos").selection().named("positiveMetal");
    model.component("comp1").physics("ht").feature("solidPos").set("k_mat", "userdef");
    model.component("comp1").physics("ht").feature("solidPos")
        .set("k", new String[][]{{"238[W/(m*K)]"}, {"0"}, {"0"},
                                  {"0"}, {"238[W/(m*K)]"}, {"0"},
                                  {"0"}, {"0"}, {"238[W/(m*K)]"}});
    model.component("comp1").physics("ht").feature("solidPos").set("rho_mat", "userdef");
    model.component("comp1").physics("ht").feature("solidPos").set("rho", "2700[kg/m^3]");
    model.component("comp1").physics("ht").feature("solidPos").set("Cp_mat", "userdef");
    model.component("comp1").physics("ht").feature("solidPos").set("Cp", "900[J/(kg*K)]");

    model.component("comp1").physics("ht").create("solidHeater", "SolidHeatTransferModel", 3);
    model.component("comp1").physics("ht").feature("solidHeater").selection().named("geom1_heater1_dom");
    model.component("comp1").physics("ht").feature("solidHeater").set("k_mat", "userdef");
    model.component("comp1").physics("ht").feature("solidHeater")
        .set("k", new String[][]{{"400[W/(m*K)]"}, {"0"}, {"0"},
                                  {"0"}, {"400[W/(m*K)]"}, {"0"},
                                  {"0"}, {"0"}, {"400[W/(m*K)]"}});
    /* Very low thermal mass so that the domain approximates a flux patch. */
    model.component("comp1").physics("ht").feature("solidHeater").set("rho_mat", "userdef");
    model.component("comp1").physics("ht").feature("solidHeater").set("rho", "1[kg/m^3]");
    model.component("comp1").physics("ht").feature("solidHeater").set("Cp_mat", "userdef");
    model.component("comp1").physics("ht").feature("solidHeater").set("Cp", "1[J/(kg*K)]");

    model.component("comp1").physics("ht").create("hsTR", "HeatSource", 3);
    model.component("comp1").physics("ht").feature("hsTR").selection().named("activeAll");
    model.component("comp1").physics("ht").feature("hsTR").set("Q0", "Q_tot");

    model.component("comp1").physics("ht").create("hsHeater", "HeatSource", 3);
    model.component("comp1").physics("ht").feature("hsHeater").selection().named("geom1_heater1_dom");
    model.component("comp1").physics("ht").feature("hsHeater")
        .set("Q0", "if(T<T_heater_off,q_heater/heater_t,0[W/m^3])");

    model.component("comp1").physics("ht").create("hfConv", "HeatFluxBoundary", 2);
    model.component("comp1").physics("ht").feature("hfConv").selection().named("outerBnd");
    model.component("comp1").physics("ht").feature("hfConv").set("HeatFluxType", "ConvectiveHeatFlux");
    model.component("comp1").physics("ht").feature("hfConv").set("h", "h_conv");
    model.component("comp1").physics("ht").feature("hfConv").set("Text", "T_amb");

    /* ------------------------------------------------------------------ */
    /* 8. Domain ODEs: independent fields over each disconnected cell      */
    /* ------------------------------------------------------------------ */
    model.component("comp1").physics().create("dodeSep", "DomainODE", "geom1");
    model.component("comp1").physics("dodeSep").field("dimensionless").component(new String[]{"c_sep"});
    model.component("comp1").physics("dodeSep").selection().named("activeAll");
    model.component("comp1").physics("dodeSep").prop("Units").set("CustomSourceTermUnit", "s^-1");
    model.component("comp1").physics("dodeSep").feature("dode1").set("f", "-R_sep");
    model.component("comp1").physics("dodeSep").feature("init1").set("c_sep", "csep0");

    model.component("comp1").physics().create("dodeChem", "DomainODE", "geom1");
    model.component("comp1").physics("dodeChem").field("dimensionless").component(new String[]{"Qaccum"});
    model.component("comp1").physics("dodeChem").prop("Units").set("DependentVariableQuantity", "energy");
    model.component("comp1").physics("dodeChem").prop("Units").set("SourceTermQuantity", "power");
    model.component("comp1").physics("dodeChem").selection().named("activeAll");
    model.component("comp1").physics("dodeChem").feature("dode1").set("f", "Qchem0");

    model.component("comp1").physics().create("dodeEle", "DomainODE", "geom1");
    model.component("comp1").physics("dodeEle").field("dimensionless").component(new String[]{"c_e"});
    model.component("comp1").physics("dodeEle").selection().named("activeAll");
    model.component("comp1").physics("dodeEle").prop("Units").set("CustomSourceTermUnit", "s^-1");
    model.component("comp1").physics("dodeEle").feature("dode1").set("f", "-R_e");
    model.component("comp1").physics("dodeEle").feature("init1").set("c_e", "ce0");

    model.component("comp1").physics().create("dodeNeg", "DomainODE", "geom1");
    model.component("comp1").physics("dodeNeg").field("dimensionless").component(new String[]{"c_neg", "c_sei"});
    model.component("comp1").physics("dodeNeg").selection().named("activeAll");
    model.component("comp1").physics("dodeNeg").prop("Units").set("CustomSourceTermUnit", "s^-1");
    model.component("comp1").physics("dodeNeg").feature("dode1")
        .set("f", new String[][]{{"-R_neg"}, {"-R_sei"}});
    model.component("comp1").physics("dodeNeg").feature("init1").set("c_neg", "cneg0");
    model.component("comp1").physics("dodeNeg").feature("init1").set("c_sei", "csei0");

    model.component("comp1").physics().create("dodePos", "DomainODE", "geom1");
    model.component("comp1").physics("dodePos").field("dimensionless").component(new String[]{"c_pos1", "c_pos2"});
    model.component("comp1").physics("dodePos").selection().named("activeAll");
    model.component("comp1").physics("dodePos").prop("Units").set("CustomSourceTermUnit", "s^-1");
    model.component("comp1").physics("dodePos").feature("dode1")
        .set("f", new String[][]{{"-R_pos1"}, {"-R_pos2"}});
    model.component("comp1").physics("dodePos").feature("init1").set("c_pos1", "cpos10");
    model.component("comp1").physics("dodePos").feature("init1").set("c_pos2", "cpos20");

    /* ------------------------------------------------------------------ */
    /* 9. Mesh, study, cell-wise probes                                    */
    /* ------------------------------------------------------------------ */
    model.component("comp1").mesh().create("mesh1");
    model.component("comp1").mesh("mesh1").autoMeshSize(7);

    model.result().table().create("tblCells", "Table");
    model.result().table("tblCells").label("Three-cell temperature probes");

    for (int i = 1; i <= 3; i++) {
      String n = Integer.toString(i);
      String sel = "geom1_act" + n + "_dom";

      model.component("comp1").probe().create("Tavg" + n, "Domain");
      model.component("comp1").probe("Tavg" + n).label("Cell " + n + " active average temperature");
      model.component("comp1").probe("Tavg" + n).selection().named(sel);
      model.component("comp1").probe("Tavg" + n).set("expr", "T");
      model.component("comp1").probe("Tavg" + n).set("type", "average");
      model.component("comp1").probe("Tavg" + n).set("unit", "degC");
      model.component("comp1").probe("Tavg" + n).set("table", "tblCells");

      model.component("comp1").probe().create("Tmax" + n, "Domain");
      model.component("comp1").probe("Tmax" + n).label("Cell " + n + " active maximum temperature");
      model.component("comp1").probe("Tmax" + n).selection().named(sel);
      model.component("comp1").probe("Tmax" + n).set("expr", "T");
      model.component("comp1").probe("Tmax" + n).set("type", "maximum");
      model.component("comp1").probe("Tmax" + n).set("unit", "degC");
      model.component("comp1").probe("Tmax" + n).set("table", "tblCells");

      model.component("comp1").probe().create("CsepAvg" + n, "Domain");
      model.component("comp1").probe("CsepAvg" + n).label("Cell " + n + " average c_sep");
      model.component("comp1").probe("CsepAvg" + n).selection().named(sel);
      model.component("comp1").probe("CsepAvg" + n).set("expr", "c_sep");
      model.component("comp1").probe("CsepAvg" + n).set("type", "average");
      model.component("comp1").probe("CsepAvg" + n).set("unit", "1");
      model.component("comp1").probe("CsepAvg" + n).set("table", "tblCells");
    }

    model.study().create("std1");
    model.study("std1").create("time", "Transient");
    model.study("std1").feature("time").set("tlist", "range(0,0.1,500)");

    return model;
  }

  public static void main(String[] args) {
    try {
      Model model = buildModel();

      if (SOLVE_MODEL) {
        model.study("std1").run();
      }

      String output = (args.length > 0) ? args[0] : "SOC100_3cell_offset_v02.mph";
      model.save(output);
      System.out.println("Saved COMSOL model to: " + output);
    } catch (java.io.IOException e) {
      throw new RuntimeException("Failed to save COMSOL model.", e);
    }
  }
}
