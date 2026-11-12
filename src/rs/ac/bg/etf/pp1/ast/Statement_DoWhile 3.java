// generated with ast extension for cup
// version 0.8
// 19/7/2025 21:6:27


package rs.ac.bg.etf.pp1.ast;

public class Statement_DoWhile extends Statement {

    private DoBegins DoBegins;
    private Statement Statement;
    private WhileBegins WhileBegins;
    private ConditionOpt ConditionOpt;

    public Statement_DoWhile (DoBegins DoBegins, Statement Statement, WhileBegins WhileBegins, ConditionOpt ConditionOpt) {
        this.DoBegins=DoBegins;
        if(DoBegins!=null) DoBegins.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
        this.WhileBegins=WhileBegins;
        if(WhileBegins!=null) WhileBegins.setParent(this);
        this.ConditionOpt=ConditionOpt;
        if(ConditionOpt!=null) ConditionOpt.setParent(this);
    }

    public DoBegins getDoBegins() {
        return DoBegins;
    }

    public void setDoBegins(DoBegins DoBegins) {
        this.DoBegins=DoBegins;
    }

    public Statement getStatement() {
        return Statement;
    }

    public void setStatement(Statement Statement) {
        this.Statement=Statement;
    }

    public WhileBegins getWhileBegins() {
        return WhileBegins;
    }

    public void setWhileBegins(WhileBegins WhileBegins) {
        this.WhileBegins=WhileBegins;
    }

    public ConditionOpt getConditionOpt() {
        return ConditionOpt;
    }

    public void setConditionOpt(ConditionOpt ConditionOpt) {
        this.ConditionOpt=ConditionOpt;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(DoBegins!=null) DoBegins.accept(visitor);
        if(Statement!=null) Statement.accept(visitor);
        if(WhileBegins!=null) WhileBegins.accept(visitor);
        if(ConditionOpt!=null) ConditionOpt.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DoBegins!=null) DoBegins.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
        if(WhileBegins!=null) WhileBegins.traverseTopDown(visitor);
        if(ConditionOpt!=null) ConditionOpt.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DoBegins!=null) DoBegins.traverseBottomUp(visitor);
        if(Statement!=null) Statement.traverseBottomUp(visitor);
        if(WhileBegins!=null) WhileBegins.traverseBottomUp(visitor);
        if(ConditionOpt!=null) ConditionOpt.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statement_DoWhile(\n");

        if(DoBegins!=null)
            buffer.append(DoBegins.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statement!=null)
            buffer.append(Statement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(WhileBegins!=null)
            buffer.append(WhileBegins.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConditionOpt!=null)
            buffer.append(ConditionOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Statement_DoWhile]");
        return buffer.toString();
    }
}
