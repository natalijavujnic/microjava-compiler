// generated with ast extension for cup
// version 0.8
// 19/7/2025 21:6:27


package rs.ac.bg.etf.pp1.ast;

public class Factor_Designator extends Factor {

    private Designator Designator;
    private OptActParsOpt OptActParsOpt;

    public Factor_Designator (Designator Designator, OptActParsOpt OptActParsOpt) {
        this.Designator=Designator;
        if(Designator!=null) Designator.setParent(this);
        this.OptActParsOpt=OptActParsOpt;
        if(OptActParsOpt!=null) OptActParsOpt.setParent(this);
    }

    public Designator getDesignator() {
        return Designator;
    }

    public void setDesignator(Designator Designator) {
        this.Designator=Designator;
    }

    public OptActParsOpt getOptActParsOpt() {
        return OptActParsOpt;
    }

    public void setOptActParsOpt(OptActParsOpt OptActParsOpt) {
        this.OptActParsOpt=OptActParsOpt;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Designator!=null) Designator.accept(visitor);
        if(OptActParsOpt!=null) OptActParsOpt.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Designator!=null) Designator.traverseTopDown(visitor);
        if(OptActParsOpt!=null) OptActParsOpt.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Designator!=null) Designator.traverseBottomUp(visitor);
        if(OptActParsOpt!=null) OptActParsOpt.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Factor_Designator(\n");

        if(Designator!=null)
            buffer.append(Designator.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OptActParsOpt!=null)
            buffer.append(OptActParsOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Factor_Designator]");
        return buffer.toString();
    }
}
