// generated with ast extension for cup
// version 0.8
// 19/7/2025 21:6:27


package rs.ac.bg.etf.pp1.ast;

public class WithActPars extends ActParsOpt {

    private ActParsBegins ActParsBegins;
    private ActPars ActPars;

    public WithActPars (ActParsBegins ActParsBegins, ActPars ActPars) {
        this.ActParsBegins=ActParsBegins;
        if(ActParsBegins!=null) ActParsBegins.setParent(this);
        this.ActPars=ActPars;
        if(ActPars!=null) ActPars.setParent(this);
    }

    public ActParsBegins getActParsBegins() {
        return ActParsBegins;
    }

    public void setActParsBegins(ActParsBegins ActParsBegins) {
        this.ActParsBegins=ActParsBegins;
    }

    public ActPars getActPars() {
        return ActPars;
    }

    public void setActPars(ActPars ActPars) {
        this.ActPars=ActPars;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ActParsBegins!=null) ActParsBegins.accept(visitor);
        if(ActPars!=null) ActPars.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ActParsBegins!=null) ActParsBegins.traverseTopDown(visitor);
        if(ActPars!=null) ActPars.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ActParsBegins!=null) ActParsBegins.traverseBottomUp(visitor);
        if(ActPars!=null) ActPars.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("WithActPars(\n");

        if(ActParsBegins!=null)
            buffer.append(ActParsBegins.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ActPars!=null)
            buffer.append(ActPars.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [WithActPars]");
        return buffer.toString();
    }
}
