// generated with ast extension for cup
// version 0.8
// 19/7/2025 21:6:27


package rs.ac.bg.etf.pp1.ast;

public class Relop_Gte extends Relop {

    public Relop_Gte () {
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
        buffer.append("Relop_Gte(\n");

        buffer.append(tab);
        buffer.append(") [Relop_Gte]");
        return buffer.toString();
    }
}
