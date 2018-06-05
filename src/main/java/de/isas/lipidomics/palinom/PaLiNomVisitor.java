/*
 * 
 */
package de.isas.lipidomics.palinom;

import de.isas.lipidomics.domain.FattyAcid;
import de.isas.lipidomics.domain.Lipid;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 *
 * @author nilshoffmann
 */
public class PaLiNomVisitor extends PaLiNomBaseListener {
    
    private Lipid lipid;
    private FattyAcid fa1;
    private FattyAcid fa2;
    private FattyAcid activeFa;
    
    public Lipid visit(PaLiNomParser.LipidIdentifierContext context) {
        ParseTreeWalker.DEFAULT.walk(this, context);
        lipid.setFa1(fa1);
        lipid.setFa2(fa2);
        return lipid;
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        super.enterEveryRule(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitHg(PaLiNomParser.HgContext ctx) {
        super.exitHg(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterHg(PaLiNomParser.HgContext ctx) {
        lipid.setHeadGroup(ctx.getText());
    }

    @Override
    public void exitLipidIdentifier(PaLiNomParser.LipidIdentifierContext ctx) {
        super.exitLipidIdentifier(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterLipidIdentifier(PaLiNomParser.LipidIdentifierContext ctx) {
        this.lipid = new Lipid();
    }

    @Override
    public void exitDb(PaLiNomParser.DbContext ctx) {
        super.exitDb(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterDb(PaLiNomParser.DbContext ctx) {
        activeFa.setNDoubleBond(Integer.parseInt(ctx.getText()));
    }

    @Override
    public void exitPl(PaLiNomParser.PlContext ctx) {
        super.exitPl(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterPl(PaLiNomParser.PlContext ctx) {
        //this.lipid.setHeadGroup(ctx.getText());
    }

    @Override
    public void exitCategory(PaLiNomParser.CategoryContext ctx) {
        super.exitCategory(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterCategory(PaLiNomParser.CategoryContext ctx) {
        super.enterCategory(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exitFa(PaLiNomParser.FaContext ctx) {
        activeFa = null;
    }

    @Override
    public void enterFa(PaLiNomParser.FaContext ctx) {
        if(fa1==null) {
            fa1 = new FattyAcid();
            fa1.setName("FA1");
            activeFa = fa1;
        } else if(fa2==null) {
            fa2 = new FattyAcid();
            fa2.setName("FA2");
            activeFa = fa2;
        }
    }

    @Override
    public void exitCarbon(PaLiNomParser.CarbonContext ctx) {
        super.exitCarbon(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterCarbon(PaLiNomParser.CarbonContext ctx) {
        if(activeFa!=null) {
            activeFa.setNCarbon(Integer.parseInt(ctx.getText()));
        }
    }

    @Override
    public void exitHydro(PaLiNomParser.HydroContext ctx) {
        super.exitHydro(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void enterHydro(PaLiNomParser.HydroContext ctx) {
        if(activeFa!=null) {
            activeFa.setNHydroxy(Integer.parseInt(ctx.getText()));
        }
    }
    
}
