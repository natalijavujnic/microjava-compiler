// generated with ast extension for cup
// version 0.8
// 19/7/2025 21:6:27


package rs.ac.bg.etf.pp1.ast;

public class WithElse extends ElseOpt {

    private ElseBegins ElseBegins;
    private Statement Statement;

    public WithElse (ElseBegins ElseBegins, Statement Statement) {
        this.ElseBegins=ElseBegins;
        if(ElseBegins!=null) ElseBegins.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
    }

    public ElseBegins getElseBegins() {
        return ElseBegins;
    }

    public void setElseBegins(ElseBegins ElseBegins) {
        this.ElseBegins=ElseBegins;
    }

    public Statement getStatement() {
        return Statement;
    }

    public void setStatement(Statement Statement) {
        this.Statement=Statement;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ElseBegins!=null) ElseBegins.accept(visitor);
        if(Statement!=null) Statement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ElseBegins!=null) ElseBegins.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ElseBegins!=null) ElseBegins.traverseBottomUp(visitor);
        if(Statement!=null) Statement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("WithElse(\n");

        if(ElseBegins!=null)
            buffer.append(ElseBegins.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statement!=null)
            buffer.append(Statement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [WithElse]");
        return buffer.toString();
    }
}
