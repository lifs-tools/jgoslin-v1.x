/*
 * 
 */
package de.isas.lipidomics.palinom;

import de.isas.lipidomics.domain.Adduct;
import de.isas.lipidomics.domain.FattyAcid;
import de.isas.lipidomics.domain.Lipid;
import java.util.LinkedHashMap;
import java.util.Map;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 *
 * @author nilshoffmann
 */
public class PaLiNomVisitor extends PaLiNomBaseListener {

    private Adduct adduct;

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        super.exitEveryRule(ctx);
    }

    @Override
    public void exitDb_position(PaLiNomParser.Db_positionContext ctx) {
        super.exitDb_position(ctx);
    }

    @Override
    public void enterDb_position(PaLiNomParser.Db_positionContext ctx) {
        super.enterDb_position(ctx);
        if (this.activeFa != null) {
            this.activeFa.addDoubleBondWithLocation(Integer.parseInt(ctx.
                getText()));
        }
    }

    @Override
    public void exitDb_count(PaLiNomParser.Db_countContext ctx) {
        super.exitDb_count(ctx);
    }

    @Override
    public void enterDb_count(PaLiNomParser.Db_countContext ctx) {
        super.enterDb_count(ctx);
        if (this.activeFa != null) {
            this.activeFa.addDoubleBond();
        }
    }

    @Override
    public void exitLcb(PaLiNomParser.LcbContext ctx) {
        super.exitLcb(ctx);
    }

    @Override
    public void enterLcb(PaLiNomParser.LcbContext ctx) {
        super.enterLcb(ctx);
        if (activeFa != null) {
            throw new IllegalStateException(
                "Previous FA context was not exited!");
        }
        FattyAcid fa1 = new FattyAcid();
        fa1.setName("LCB");
        this.activeFa = fa1;
        fa.put(fa1.getName(), fa1);
    }

    @Override
    public void exitEther(PaLiNomParser.EtherContext ctx) {
        super.exitEther(ctx);
    }

    @Override
    public void enterEther(PaLiNomParser.EtherContext ctx) {
        super.enterEther(ctx);
    }

    @Override
    public void exitFa_pure(PaLiNomParser.Fa_pureContext ctx) {
        super.exitFa_pure(ctx);
    }

    @Override
    public void enterFa_pure(PaLiNomParser.Fa_pureContext ctx) {
        super.enterFa_pure(ctx);
    }

    @Override
    public void exitMediator(PaLiNomParser.MediatorContext ctx) {
        super.exitMediator(ctx);
    }

    @Override
    public void enterMediator(PaLiNomParser.MediatorContext ctx) {
        super.enterMediator(ctx);
        this.lipid.setCategory("ME");
        this.lipid.setHeadGroup(ctx.getText());
    }

    @Override
    public void exitChe(PaLiNomParser.CheContext ctx) {
        super.exitChe(ctx);
    }

    @Override
    public void enterChe(PaLiNomParser.CheContext ctx) {
        super.enterChe(ctx);
        this.lipid.setHeadGroup(ctx.getText());
    }

    @Override
    public void exitCholesterol(PaLiNomParser.CholesterolContext ctx) {
        super.exitCholesterol(ctx);
    }

    @Override
    public void enterCholesterol(PaLiNomParser.CholesterolContext ctx) {
        super.enterCholesterol(ctx);
        this.lipid.setCategory("CH");
    }

    @Override
    public void exitDsl(PaLiNomParser.DslContext ctx) {
        super.exitDsl(ctx);
    }

    @Override
    public void enterDsl(PaLiNomParser.DslContext ctx) {
        super.enterDsl(ctx);
        this.lipid.setHeadGroup(ctx.getText());
    }

    @Override
    public void exitLsl(PaLiNomParser.LslContext ctx) {
        super.exitLsl(ctx);
    }

    @Override
    public void enterLsl(PaLiNomParser.LslContext ctx) {
        super.enterLsl(ctx);
        this.lipid.setHeadGroup(ctx.getText());
    }

    @Override
    public void exitSl(PaLiNomParser.SlContext ctx) {
        super.exitSl(ctx);
    }

    @Override
    public void enterSl(PaLiNomParser.SlContext ctx) {
        super.enterSl(ctx);
        this.lipid.setCategory("SL");
    }

    @Override
    public void exitMlcl(PaLiNomParser.MlclContext ctx) {
        super.exitMlcl(ctx);
    }

    @Override
    public void enterMlcl(PaLiNomParser.MlclContext ctx) {
        super.enterMlcl(ctx);
    }

    @Override
    public void exitCl(PaLiNomParser.ClContext ctx) {
        super.exitCl(ctx);
    }

    @Override
    public void enterCl(PaLiNomParser.ClContext ctx) {
        super.enterCl(ctx);
    }

    @Override
    public void exitDpl_o(PaLiNomParser.Dpl_oContext ctx) {
        super.exitDpl_o(ctx);
    }

    @Override
    public void enterDpl_o(PaLiNomParser.Dpl_oContext ctx) {
        super.enterDpl_o(ctx);
    }

    @Override
    public void exitDpl(PaLiNomParser.DplContext ctx) {
        super.exitDpl(ctx);
    }

    @Override
    public void enterDpl(PaLiNomParser.DplContext ctx) {
        super.enterDpl(ctx);
    }

    @Override
    public void exitLpl_o(PaLiNomParser.Lpl_oContext ctx) {
        super.exitLpl_o(ctx);
    }

    @Override
    public void enterLpl_o(PaLiNomParser.Lpl_oContext ctx) {
        super.enterLpl_o(ctx);
    }

    @Override
    public void exitLpl(PaLiNomParser.LplContext ctx) {
        super.exitLpl(ctx);
    }

    @Override
    public void enterLpl(PaLiNomParser.LplContext ctx) {
        super.enterLpl(ctx);
    }

    @Override
    public void exitPl_o(PaLiNomParser.Pl_oContext ctx) {
        super.exitPl_o(ctx);
    }

    @Override
    public void enterPl_o(PaLiNomParser.Pl_oContext ctx) {
        super.enterPl_o(ctx);
    }

    @Override
    public void exitTgl(PaLiNomParser.TglContext ctx) {
        super.exitTgl(ctx);
    }

    @Override
    public void enterTgl(PaLiNomParser.TglContext ctx) {
        super.enterTgl(ctx);
    }

    @Override
    public void exitSgl(PaLiNomParser.SglContext ctx) {
        super.exitSgl(ctx);
    }

    @Override
    public void enterSgl(PaLiNomParser.SglContext ctx) {
        super.enterSgl(ctx);
    }

    @Override
    public void exitDgl(PaLiNomParser.DglContext ctx) {
        super.exitDgl(ctx);
    }

    @Override
    public void enterDgl(PaLiNomParser.DglContext ctx) {
        super.enterDgl(ctx);
    }

    @Override
    public void exitMgl(PaLiNomParser.MglContext ctx) {
        super.exitMgl(ctx);
    }

    @Override
    public void enterMgl(PaLiNomParser.MglContext ctx) {
        super.enterMgl(ctx);
    }

    @Override
    public void exitGl(PaLiNomParser.GlContext ctx) {
        super.exitGl(ctx);
    }

    @Override
    public void enterGl(PaLiNomParser.GlContext ctx) {
        super.enterGl(ctx);
        this.lipid.setCategory("GL");
    }

    @Override
    public void exitAdduct_term(PaLiNomParser.Adduct_termContext ctx) {
        super.exitAdduct_term(ctx);
    }

    @Override
    public void enterAdduct_term(PaLiNomParser.Adduct_termContext ctx) {
        super.enterAdduct_term(ctx);
        this.adduct = new Adduct();
    }

    private Lipid lipid;
    private Map<String, FattyAcid> fa = new LinkedHashMap<>();
    private FattyAcid activeFa;

    public Lipid visit(PaLiNomParser.LipidIdentifierContext context) {
        ParseTreeWalker.DEFAULT.walk(this, context);
        lipid.setFa(fa);
        return lipid;
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        super.enterEveryRule(ctx);
    }

    @Override
    public void exitCistrans(PaLiNomParser.CistransContext ctx) {
        super.exitCistrans(ctx);
    }

    @Override
    public void enterCistrans(PaLiNomParser.CistransContext ctx) {
        super.enterCistrans(ctx);
    }

    @Override
    public void exitHg_che(PaLiNomParser.Hg_cheContext ctx) {
        super.exitHg_che(ctx);
    }

    @Override
    public void enterHg_che(PaLiNomParser.Hg_cheContext ctx) {
        super.enterHg_che(ctx);
        this.lipid.setHeadGroup(ctx.getText());
    }

    @Override
    public void exitCh(PaLiNomParser.ChContext ctx) {
        super.exitCh(ctx);
    }

    @Override
    public void enterCh(PaLiNomParser.ChContext ctx) {
        super.enterCh(ctx);
        this.lipid.setHeadGroup(ctx.getText());
    }

    @Override
    public void exitHg_dsl(PaLiNomParser.Hg_dslContext ctx) {
        super.exitHg_dsl(ctx);
    }

    @Override
    public void enterHg_dsl(PaLiNomParser.Hg_dslContext ctx) {
        super.enterHg_dsl(ctx);
        this.lipid.setHeadGroup(ctx.getText());
    }

    @Override
    public void exitHg_lsl(PaLiNomParser.Hg_lslContext ctx) {
        super.exitHg_lsl(ctx);
    }

    @Override
    public void enterHg_lsl(PaLiNomParser.Hg_lslContext ctx) {
        super.enterHg_lsl(ctx);
        this.lipid.setHeadGroup(ctx.getText());
    }

    @Override
    public void exitHg_pl_o(PaLiNomParser.Hg_pl_oContext ctx) {
        super.exitHg_pl_o(ctx);
    }

    @Override
    public void enterHg_pl_o(PaLiNomParser.Hg_pl_oContext ctx) {
        super.enterHg_pl_o(ctx);
        this.lipid.setHeadGroup(ctx.getText());
    }

    @Override
    public void exitHg_lpl_o(PaLiNomParser.Hg_lpl_oContext ctx) {
        super.exitHg_lpl_o(ctx);
    }

    @Override
    public void enterHg_lpl_o(PaLiNomParser.Hg_lpl_oContext ctx) {
        super.enterHg_lpl_o(ctx);
        this.lipid.setHeadGroup(ctx.getText());
    }

    @Override
    public void exitHg_lpl(PaLiNomParser.Hg_lplContext ctx) {
        super.exitHg_lpl(ctx);
    }

    @Override
    public void enterHg_lpl(PaLiNomParser.Hg_lplContext ctx) {
        super.enterHg_lpl(ctx);
        this.lipid.setHeadGroup(ctx.getText());
    }

    @Override
    public void exitHg_pl(PaLiNomParser.Hg_plContext ctx) {
        super.exitHg_pl(ctx);
    }

    @Override
    public void enterHg_pl(PaLiNomParser.Hg_plContext ctx) {
        super.enterHg_pl(ctx);
        this.lipid.setHeadGroup(ctx.getText());
    }

    @Override
    public void exitHg_mlcl(PaLiNomParser.Hg_mlclContext ctx) {
        super.exitHg_mlcl(ctx);
    }

    @Override
    public void enterHg_mlcl(PaLiNomParser.Hg_mlclContext ctx) {
        super.enterHg_mlcl(ctx);
        this.lipid.setHeadGroup(ctx.getText());
    }

    @Override
    public void exitHg_cl(PaLiNomParser.Hg_clContext ctx) {
        super.exitHg_cl(ctx);
    }

    @Override
    public void enterHg_cl(PaLiNomParser.Hg_clContext ctx) {
        super.enterHg_cl(ctx);
        this.lipid.setHeadGroup(ctx.getText());
    }

    @Override
    public void exitHg_tgl(PaLiNomParser.Hg_tglContext ctx) {
        super.exitHg_tgl(ctx);
    }

    @Override
    public void enterHg_tgl(PaLiNomParser.Hg_tglContext ctx) {
        super.enterHg_tgl(ctx);
        this.lipid.setHeadGroup(ctx.getText());
    }

    @Override
    public void exitHg_sgl(PaLiNomParser.Hg_sglContext ctx) {
        super.exitHg_sgl(ctx);
    }

    @Override
    public void enterHg_sgl(PaLiNomParser.Hg_sglContext ctx) {
        super.enterHg_sgl(ctx);
        this.lipid.setHeadGroup(ctx.getText());
    }

    @Override
    public void exitHg_dgl(PaLiNomParser.Hg_dglContext ctx) {
        super.exitHg_dgl(ctx);
    }

    @Override
    public void enterHg_dgl(PaLiNomParser.Hg_dglContext ctx) {
        super.enterHg_dgl(ctx);
        this.lipid.setHeadGroup(ctx.getText());
    }

    @Override
    public void exitHg_mgl(PaLiNomParser.Hg_mglContext ctx) {
        super.exitHg_mgl(ctx);
    }

    @Override
    public void enterHg_mgl(PaLiNomParser.Hg_mglContext ctx) {
        super.enterHg_mgl(ctx);
        this.lipid.setHeadGroup(ctx.getText());
    }

    @Override
    public void exitCharge_sign(PaLiNomParser.Charge_signContext ctx) {
        super.exitCharge_sign(ctx);
    }

    @Override
    public void enterCharge_sign(PaLiNomParser.Charge_signContext ctx) {
        super.enterCharge_sign(ctx);
        this.adduct.setChargeSign(Integer.parseInt(ctx.getText()));
    }

    @Override
    public void exitCharge(PaLiNomParser.ChargeContext ctx) {
        super.exitCharge(ctx);
    }

    @Override
    public void enterCharge(PaLiNomParser.ChargeContext ctx) {
        super.enterCharge(ctx);
        this.adduct.setCharge(Integer.parseInt(ctx.getText()));
    }

    @Override
    public void exitAdduct(PaLiNomParser.AdductContext ctx) {
        super.exitAdduct(ctx);
    }

//    @Override
//    public void exitHg(PaLiNomParser.HgContext ctx) {
    @Override
    public void enterAdduct(PaLiNomParser.AdductContext ctx) {
        super.enterAdduct(ctx);
        this.adduct.setType(ctx.getText());
    }

//        super.exitHg(ctx); 
//    }
//
//    @Override
//    public void enterHg(PaLiNomParser.HgContext ctx) {
//        lipid.setHeadGroup(ctx.getText());
//    }
    @Override
    public void exitLipidIdentifier(
        PaLiNomParser.LipidIdentifierContext ctx) {
        super.exitLipidIdentifier(ctx);
    }

    @Override
    public void enterLipidIdentifier(PaLiNomParser.LipidIdentifierContext ctx) {
        this.lipid = new Lipid();
    }

    @Override
    public void exitDb(PaLiNomParser.DbContext ctx) {
        super.exitDb(ctx);
    }

    @Override
    public void enterDb(PaLiNomParser.DbContext ctx) {
        this.activeFa.addDoubleBonds(Integer.parseInt(ctx.getText()));
    }

    @Override
    public void exitPl(PaLiNomParser.PlContext ctx) {
        super.exitPl(ctx);
    }

    @Override
    public void enterPl(PaLiNomParser.PlContext ctx) {
        this.lipid.setCategory("PL");
    }

    @Override
    public void exitCategory(PaLiNomParser.CategoryContext ctx) {
        super.exitCategory(ctx);
    }

    @Override
    public void enterCategory(PaLiNomParser.CategoryContext ctx) {
        super.enterCategory(ctx);
    }

    @Override
    public void exitFa(PaLiNomParser.FaContext ctx) {
        activeFa = null;
    }

    @Override
    public void enterFa(PaLiNomParser.FaContext ctx) {
        if (activeFa != null) {
            throw new IllegalStateException(
                "Previous FA context was not exited!");
        }
        FattyAcid fa1 = new FattyAcid();
        fa1.setName("FA" + (fa.size() + 1));
        this.activeFa = fa1;
        fa.put(fa1.getName(), fa1);
    }

    @Override
    public void exitCarbon(PaLiNomParser.CarbonContext ctx) {
        super.exitCarbon(ctx);
    }

    @Override
    public void enterCarbon(PaLiNomParser.CarbonContext ctx) {
        if (activeFa != null) {
            activeFa.setNCarbon(Integer.parseInt(ctx.getText()));
        }
    }

    @Override
    public void exitHydro(PaLiNomParser.HydroContext ctx) {
        super.exitHydro(ctx);
    }

    @Override
    public void enterHydro(PaLiNomParser.HydroContext ctx) {
        if (activeFa != null) {
            activeFa.setNHydroxy(Integer.parseInt(ctx.getText()));
        }
    }

}
