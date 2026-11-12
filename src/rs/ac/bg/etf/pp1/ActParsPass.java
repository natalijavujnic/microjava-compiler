package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.concepts.Struct;

public class ActParsPass extends VisitorAdaptor {
	
	List<Struct> actualParameters;
	Stack<List<Struct>> stack = new Stack<>(); // potreban je zbog ugnezdenih poziva
	
	@Override
	public void visit(ActParsBegins ActParsBegins) {
		stack.push(new ArrayList<>());
	}
	
	@Override
	public void visit(ActPar ActPar) {
		stack.peek().add(ActPar.getExpr().struct);
	}
	
	@Override
	public void visit(WithActPars WithActPars) {
		actualParameters = stack.pop();
	}
	
	@Override
	public void visit(NoActPars NoActPars) {
		actualParameters = stack.pop();
	}
}
