// generated with ast extension for cup
// version 0.8
// 19/7/2025 21:6:27


package rs.ac.bg.etf.pp1.ast;

public class UnaryFactorList_Mulop extends UnaryFactorList {

    private UnaryFactorList UnaryFactorList;
    private Mulop Mulop;
    private UnaryFactor UnaryFactor;

    public UnaryFactorList_Mulop (UnaryFactorList UnaryFactorList, Mulop Mulop, UnaryFactor UnaryFactor) {
        this.UnaryFactorList=UnaryFactorList;
        if(UnaryFactorList!=null) UnaryFactorList.setParent(this);
        this.Mulop=Mulop;
        if(Mulop!=null) Mulop.setParent(this);
        this.UnaryFactor=UnaryFactor;
        if(UnaryFactor!=null) UnaryFactor.setParent(this);
    }

    public UnaryFactorList getUnaryFactorList() {
        return UnaryFactorList;
    }

    public void setUnaryFactorList(UnaryFactorList UnaryFactorList) {
        this.UnaryFactorList=UnaryFactorList;
    }

    public Mulop getMulop() {
        return Mulop;
    }

    public void setMulop(Mulop Mulop) {
        this.Mulop=Mulop;
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
        if(UnaryFactorList!=null) UnaryFactorList.accept(visitor);
        if(Mulop!=null) Mulop.accept(visitor);
        if(UnaryFactor!=null) UnaryFactor.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(UnaryFactorList!=null) UnaryFactorList.traverseTopDown(visitor);
        if(Mulop!=null) Mulop.traverseTopDown(visitor);
        if(UnaryFactor!=null) UnaryFactor.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(UnaryFactorList!=null) UnaryFactorList.traverseBottomUp(visitor);
        if(Mulop!=null) Mulop.traverseBottomUp(visitor);
        if(UnaryFactor!=null) UnaryFactor.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("UnaryFactorList_Mulop(\n");

        if(UnaryFactorList!=null)
            buffer.append(UnaryFactorList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Mulop!=null)
            buffer.append(Mulop.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(UnaryFactor!=null)
            buffer.append(UnaryFactor.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [UnaryFactorList_Mulop]");
        return buffer.toString();
    }
}
