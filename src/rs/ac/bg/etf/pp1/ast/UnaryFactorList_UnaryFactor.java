// generated with ast extension for cup
// version 0.8
// 19/7/2025 21:9:25


package rs.ac.bg.etf.pp1.ast;

public class UnaryFactorList_UnaryFactor extends UnaryFactorList {

    private UnaryFactor UnaryFactor;

    public UnaryFactorList_UnaryFactor (UnaryFactor UnaryFactor) {
        this.UnaryFactor=UnaryFactor;
        if(UnaryFactor!=null) UnaryFactor.setParent(this);
    }

    public UnaryFactor getUnaryFactor() {
        return UnaryFactor;
    }

    public void setUnaryFactor(UnaryFactor UnaryFactor) {
        this.UnaryFactor=UnaryFactor;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(UnaryFactor!=null) UnaryFactor.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(UnaryFactor!=null) UnaryFactor.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(UnaryFactor!=null) UnaryFactor.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("UnaryFactorList_UnaryFactor(\n");

        if(UnaryFactor!=null)
            buffer.append(UnaryFactor.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [UnaryFactorList_UnaryFactor]");
        return buffer.toString();
    }
}
