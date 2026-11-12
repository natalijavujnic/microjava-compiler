// generated with ast extension for cup
// version 0.8
// 19/7/2025 21:9:25


package rs.ac.bg.etf.pp1.ast;

public class Setop_Union extends Setop {

    public Setop_Union () {
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Setop_Union(\n");

        buffer.append(tab);
        buffer.append(") [Setop_Union]");
        return buffer.toString();
    }
}
