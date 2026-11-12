// generated with ast extension for cup
// version 0.8
// 19/7/2025 21:9:25


package rs.ac.bg.etf.pp1.ast;

public class NoActPars extends ActParsOpt {

    private ActParsBegins ActParsBegins;

    public NoActPars (ActParsBegins ActParsBegins) {
        this.ActParsBegins=ActParsBegins;
        if(ActParsBegins!=null) ActParsBegins.setParent(this);
    }

    public ActParsBegins getActParsBegins() {
        return ActParsBegins;
    }

    public void setActParsBegins(ActParsBegins ActParsBegins) {
        this.ActParsBegins=ActParsBegins;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ActParsBegins!=null) ActParsBegins.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ActParsBegins!=null) ActParsBegins.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ActParsBegins!=null) ActParsBegins.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("NoActPars(\n");

        if(ActParsBegins!=null)
            buffer.append(ActParsBegins.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [NoActPars]");
        return buffer.toString();
    }
}
