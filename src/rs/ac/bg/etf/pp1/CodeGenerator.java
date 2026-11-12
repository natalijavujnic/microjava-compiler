package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class CodeGenerator extends VisitorAdaptor{

	private int mainPC;
	
	private Stack<Integer> falseCondFact = new Stack<>(); // sve andrese u kojima se desio netacan uslov u jednom CondFact-u
	private Stack<Integer> trueCondTerm = new Stack<>(); // sve andrese u kojima se desio tacan uslov u jednom CondTerm-u
	
	private Stack<Integer> elseAdr = new Stack<>(); // adresa posle Then dela (stek zbog ugnezdjenih petlji)
	private Stack<Integer> afterIfAdr = new Stack<>(); // adresa posle Else dela (stek zbog ugnezdjenih petlji)
	private Stack<Integer> doAdr = new Stack<>(); // adresa pocetka Do dela
	
	private Stack<List<Integer>> breakAdr = new Stack<>();
	private Stack<List<Integer>> continueAdr = new Stack<>();
	
	// private Struct boolType = Tab.find("bool").getType();
	private Struct setType = Tab.find("set").getType();
	
	public int getMainPc() {
		return this.mainPC;
	}

	private int relopCode(Relop Relop) {
		if(Relop instanceof Relop_Eq) return Code.eq;
		else if(Relop instanceof Relop_Neq) return Code.ne;
		else if(Relop instanceof Relop_Gt) return Code.gt;
		else if(Relop instanceof Relop_Gte) return Code.ge;
		else if(Relop instanceof Relop_Lt) return Code.lt;
		else if(Relop instanceof Relop_Lte) return Code.le;
		else return 0;
	}
	
	private void initMethods() {
		// chr(i) - vrsi konverziju int u char
		Obj chrObj = Tab.find("chr");
		chrObj.setAdr(Code.pc);
		Code.put(Code.enter);
		Code.put(1);
		Code.put(1);
		Code.put(Code.load_n);
		Code.put(Code.exit);
		Code.put(Code.return_);
		
		// ord(ch) - vrsi konverziju char u int
		Obj ordObj = Tab.find("ord");
		ordObj.setAdr(Code.pc);
		Code.put(Code.enter);
		Code.put(1);
		Code.put(1);
		Code.put(Code.load_n);
		Code.put(Code.exit);
		Code.put(Code.return_);
		
		// len(arr) - vraca duzinu niza
		Obj lenObj = Tab.find("len");
		lenObj.setAdr(Code.pc);
		Code.put(Code.enter);
		Code.put(1);
		Code.put(1);
		Code.put(Code.load_n);
		Code.put(Code.arraylength);
		Code.put(Code.exit);
		Code.put(Code.return_);
		
		// add(a,b) - dodaje int b u skup a
		Obj addObj = Tab.find("add");
		addObj.setAdr(Code.pc);
		Code.put(Code.enter);
		Code.put(2);
		Code.put(2);
		
		// i == 0
		Code.loadConst(0); // i
		
		// loop: if(i == a.size) jump(addNew)
		int loopAdr = Code.pc;
		Code.put(Code.dup); // i i
		Code.put(Code.load_n); // i i a
		loadSetSize(); // i i size
		
		Code.putFalseJump(Code.ne, 0); // i
		int addNewAdr = Code.pc - 2;
		
		// if(a[i] == b) jump(end)
		Code.put(Code.load_n); // i a
		Code.put(Code.dup2); // i a i a
		Code.put(Code.pop); // i a i
		Code.put(Code.aload); // i a[i]
		Code.put(Code.load_n+1); // i a[i] b
		
		Code.putFalseJump(Code.ne, 0); // i
		int endAdr = Code.pc - 2;
		
		// i++
		Code.loadConst(1); // i 1
		Code.put(Code.add); // i
		
		// jump(loop)
		Code.putJump(loopAdr);
		
		// addNew: a[i] = b
		Code.fixup(addNewAdr);
		
		// if(a.size == a.len - 1) error(set full)
		
		Code.put(Code.load_n); // i a
		Code.put(Code.dup_x1); // a i a
		Code.put(Code.pop); // a i
		Code.put(Code.load_n+1); // a i b
		Code.put(Code.astore); //
		
		// a[a.len] = a.size + 1
		Code.put(Code.load_n); // a
		Code.put(Code.dup); // a a
		Code.put(Code.arraylength); // a len
		Code.loadConst(1); // a len 1
		Code.put(Code.sub); // a len-1
		Code.put(Code.load_n); // a len-1 a
		loadSetSize(); // a len-1 size
		Code.loadConst(1); // a len-1 size 1
		Code.put(Code.add); // a len-1 size+1
		Code.put(Code.astore); //
		
		Code.loadConst(0); // 0
		
		// end:
		Code.fixup(endAdr);
		Code.put(Code.pop); //
		
		Code.put(Code.exit);
		Code.put(Code.return_);
		
		// addAll(a,b) - dodaje sve elemente celobrojnog niza b u skup a
		Obj addAllObj = Tab.find("addAll");
		addAllObj.setAdr(Code.pc);
		Code.put(Code.enter);
		Code.put(2);
		Code.put(2);
		
		// i = 0
		Code.loadConst(0); // i
		
		// loop: if(i == b.len) jump(end)
		loopAdr = Code.pc;
		Code.put(Code.dup); // i i
		Code.put(Code.load_n+1); // i i b
		Code.put(Code.arraylength); // i i b.len
		Code.putFalseJump(Code.ne, 0); // i
		endAdr= Code.pc - 2;
		
		// add(a, b[i])
		Code.put(Code.load_n); // i a
		Code.put(Code.dup2); // i a i a
		Code.put(Code.pop); // i a i
		Code.put(Code.load_n+1); // i a i b
		Code.put(Code.dup_x1); // i a b i b
		Code.put(Code.pop); // i a b i
		Code.put(Code.aload); // i a b[i]
		addElem(); // i
		
		// i++
		Code.loadConst(1); // i 1
		Code.put(Code.add); // i
		
		// jump(loop)
		Code.putJump(loopAdr);
		
		// end:
		Code.fixup(endAdr);
		Code.put(Code.pop); //
		
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	private void loadSetSize() {
		// adr
		Code.put(Code.dup); // adr adr
		Code.put(Code.arraylength); // adr len
		
		Code.loadConst(1); // adr len 1
		Code.put(Code.sub); // adr len-1
		
		Code.put(Code.aload); //count
	}
	
	private void addElem() {
		// a b
		
		// i == 0
		Code.loadConst(0); // a b i
		
		// loop: if(i == a.size) jump(addNew)
		int loopAdr = Code.pc;
		Code.put(Code.dup_x2); // i a b i
		Code.put(Code.dup_x1); // i a i b i
		Code.put(Code.pop); // i a i b
		Code.put(Code.dup_x2); // i b a i b
		Code.put(Code.pop); // i b a i
		Code.put(Code.dup2); // i b a i a i
		Code.put(Code.pop); // i b a i a
		loadSetSize(); // i b a i size
		
		Code.putFalseJump(Code.ne, 0); // i b a
		int addNewAdr = Code.pc - 2;
		
		// if(a[i] == b) jump(end)
		Code.put(Code.dup_x2); // a i b a
		Code.put(Code.dup_x2); // a a i b a
		Code.put(Code.pop); // a a i b
		Code.put(Code.dup_x2); // a b a i b
		Code.put(Code.dup_x2); // a b b a i b
		Code.put(Code.pop); // a b b a i
		Code.put(Code.dup_x2); // a b i b a i
		Code.put(Code.aload); // a b i b a[i]
		
		Code.putFalseJump(Code.ne, 0); // a b i
		int endAdr = Code.pc - 2;
		
		// i++
		Code.loadConst(1); // a b i 1
		Code.put(Code.add); // a b i
		
		// jump(loop)
		Code.putJump(loopAdr);
		
		// addNew: a[i] = b
		Code.fixup(addNewAdr);
		
		// if(a.size == a.len - 1) error(set full)
		
		Code.put(Code.dup_x2); // a i b a
		Code.put(Code.dup_x2); // a a i b a
		Code.put(Code.pop); // a a i b
		Code.put(Code.astore); // a
		
		// a[a.len] = a.size + 1
		Code.put(Code.dup); // a a
		Code.put(Code.arraylength); //  a len
		Code.loadConst(1); // a len 1
		Code.put(Code.sub); // a len-1
		Code.put(Code.dup2); // a len-1 a len-1
		Code.put(Code.pop); // a len-1 a
		loadSetSize(); // a len-1 size
		Code.loadConst(1); // a len-1 size 1
		Code.put(Code.add); // a len-1 size+1
		Code.put(Code.astore); //
		
		Code.loadConst(1); // 1
		Code.loadConst(1); // 1 1
		Code.loadConst(1); // 1 1 1
		
		// end:
		Code.fixup(endAdr);
		
		Code.put(Code.pop);
		Code.put(Code.pop);
		Code.put(Code.pop); //
	}
	
	// VISIT METODE
	
	// Program - u semantickoj prebrojava globalne podatke
	@Override
	public void visit(ProgramName ProgramName) {
		initMethods();
	}
	
	// Method
	
	@Override
	public void visit(MethodName_Void MethodName_Void) {
		MethodName_Void.obj.setAdr(Code.pc);
		if(MethodName_Void.getI1().equalsIgnoreCase("main")) this.mainPC = Code.pc;
		
		Code.put(Code.enter);
		Code.put(MethodName_Void.obj.getLevel()); // broj formalnih parametara (b1)
		Code.put(MethodName_Void.obj.getLocalSymbols().size()); // zbir lokalnih i formalnih parametara (b2)
	}
	
	@Override
	public void visit(MethodName_Type MethodName_Type) {
		MethodName_Type.obj.setAdr(Code.pc); // dohvatanje trenutnog pc
		
		Code.put(Code.enter);
		Code.put(MethodName_Type.obj.getLevel()); // broj formalnih parametara (b1)
		Code.put(MethodName_Type.obj.getLocalSymbols().size()); // zbir lokalnih i formalnih parametara (b2)
	}

	@Override
	public void visit(MethodDecl MethodDecl) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	// Statement
	
	@Override
	public void visit(Statement_Break Statement_Break) {
		Code.putJump(0);
		breakAdr.peek().add(Code.pc - 2);
	}
	
	@Override
	public void visit(Statement_Continue Statement_Continue) {
		Code.putJump(0);
		continueAdr.peek().add(Code.pc - 2);
	}
	
	@Override
	public void visit(Statement_Return Statement_Return) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override
	public void visit(Statement_Read Statement_Read) {
		if(Statement_Read.getDesignator().obj.getType().equals(Tab.charType)) Code.put(Code.bread);
		else Code.put(Code.read);
		Code.store(Statement_Read.getDesignator().obj);
	}
	
	@Override
	public void visit(Statement_Print Statement_Print) {
		if(Statement_Print.getExpr().struct.equals(setType)) {
			Code.put(Code.dup_x1); // width set width
			Code.put(Code.pop); // width set
			
			// i=0
			Code.loadConst(0); // width set i
			
			// loopStart: if(i == set.size) jump(end)
			int loopStartAdr = Code.pc;
			Code.put(Code.dup2); // width set i set i
			Code.put(Code.pop); // width set i set
			loadSetSize(); // width set i size
			Code.put(Code.dup2); // width set i size i size
			Code.put(Code.pop); // width set i size i
			
			Code.putFalseJump(Code.ne, 0); // width set i
			int endAdr = Code.pc - 2;
			
			// if(i != 0) print(space)
			Code.put(Code.dup); // width set i i
			Code.loadConst(0); // width set i i 0
			Code.putFalseJump(Code.ne, 0); // width set i
			int noSpaceAdr = Code.pc - 2;
			
			Code.loadConst(' '); // width set i 32
			Code.loadConst(0); // width set i 32 o
			Code.put(Code.bprint); // width set i

			Code.fixup(noSpaceAdr);
			
			// print(set[i])
			Code.put(Code.dup_x2); // i width set i
			Code.put(Code.pop); // i width set
			Code.put(Code.dup_x2); // set i width set
			Code.put(Code.pop); // set i width
			Code.put(Code.dup_x2); // width set i width
			Code.put(Code.dup_x2); // width width set i width
			Code.put(Code.pop); // width width set i
			Code.put(Code.dup_x1); // width width i set i
			Code.put(Code.pop); // width width i set
			Code.put(Code.dup_x2); // width set width i set
			Code.put(Code.dup_x1); // width set width set i set
			Code.put(Code.pop); // width set width set i
			Code.put(Code.dup_x2); // width set i width set i
			
			Code.put(Code.aload); // width set i width set[i]
			Code.put(Code.dup_x1); // width set i set[i] width set[i]
			Code.put(Code.pop); // width set i set[i] width
			Code.put(Code.print); // width set i
			
			// i++
			Code.loadConst(1); // width set i 1
			Code.put(Code.add); // width set i
			
			// jump(loopStart)
			Code.putJump(loopStartAdr);
			
			// end
			Code.fixup(endAdr);
			Code.put(Code.pop); // width set
			Code.put(Code.pop); // width
			Code.put(Code.pop); //
		}
		else if(Statement_Print.getExpr().struct.equals(Tab.charType)) Code.put(Code.bprint);
		else Code.put(Code.print);
	}
	
	@Override
	public void visit(Statement_DoWhile Statement_DoWhile) {
		Code.putJump(doAdr.pop());
		Code.fixup(elseAdr.pop());
		
		while(!breakAdr.peek().isEmpty()) Code.fixup(breakAdr.peek().remove(0));
		breakAdr.pop();
	}
	
	@Override
	public void visit(DoBegins DoBegins) {
		doAdr.push(Code.pc);
		
		breakAdr.push(new ArrayList<Integer>());
		continueAdr.push(new ArrayList<Integer>());
	}
	
	@Override
	public void visit(WhileBegins WhileBegins) {
		while(!continueAdr.peek().isEmpty()) Code.fixup(continueAdr.peek().remove(0));
		continueAdr.pop();
	}
	
	@Override
	public void visit(WithNumConst WithNumConst) {
		Code.loadConst(WithNumConst.getN1());
	}
	
	@Override
	public void visit(NoNumConst NoNumConst) {
		Code.loadConst(0);
	}
	
	@Override
	public void visit(ElseBegins ElseBegins) {
		Code.putJump(0); // kraj if
		afterIfAdr.push(Code.pc - 2);
		Code.fixup(elseAdr.pop());
	}

	@Override
	public void visit(WithElse WithElse) {
		Code.fixup(afterIfAdr.pop());
	}
	
	@Override
	public void visit(NoElse NoElse) {
		Code.fixup(elseAdr.pop());
	}
	
	// DesignatorStatement

	@Override
	public void visit(DesignatorStatement_Assign DesignatorStatement_Assign) {
		Code.store(DesignatorStatement_Assign.getDesignator().obj);
	}
	
	@Override
	public void visit(DesignatorStatement_ActPars DesignatorStatement_ActPars) {
		int offset = DesignatorStatement_ActPars.getDesignator().obj.getAdr()-Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
		
		if(DesignatorStatement_ActPars.getDesignator().obj.getType() != Tab.noType) Code.put(Code.pop);
	}
	
	@Override
	public void visit(DesignatorStatement_Inc DesignatorStatement_Inc) {
		if(DesignatorStatement_Inc.getDesignator().obj.getKind() == Obj.Elem) Code.put(Code.dup2);
		Code.load(DesignatorStatement_Inc.getDesignator().obj);
		Code.loadConst(1);
		Code.put(Code.add);
		Code.store(DesignatorStatement_Inc.getDesignator().obj);
	}
	
	@Override
	public void visit(DesignatorStatement_Dec DesignatorStatement_Dec) {
		if(DesignatorStatement_Dec.getDesignator().obj.getKind() == Obj.Elem) Code.put(Code.dup2);
		Code.load(DesignatorStatement_Dec.getDesignator().obj);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(DesignatorStatement_Dec.getDesignator().obj);
	}
	
	@Override
	public void visit(DesignatorStatement_Set DesignatorStatement_Set) {
		Obj s = DesignatorStatement_Set.getDesignator().obj;
		Obj s1 = DesignatorStatement_Set.getDesignator1().obj;
		Obj s2 = DesignatorStatement_Set.getDesignator2().obj;
		
		
		if(DesignatorStatement_Set.getSetop() instanceof Setop_Union) {
			// i = 0
			Code.loadConst(0); // i
			
			// loop1: if(i == s1.size) jump(end1)
			int loop1 = Code.pc;
			Code.put(Code.dup); // i i
			Code.load(s1); // i i s1
			loadSetSize(); // i i s1.size
			Code.putFalseJump(Code.ne, 0); // i
			int end1 = Code.pc - 2;
			
			// add(s, s1[i])
			Code.load(s); // i s
			Code.put(Code.dup2); // i s i s
			Code.put(Code.pop); // i s i
			Code.load(s1); // i s i s1
			Code.put(Code.dup_x1); // i s s1 i s1
			Code.put(Code.pop); // i s s1 i
			Code.put(Code.aload); // i s s1[i]
			addElem(); // i
			
			// i++
			Code.loadConst(1); // i 1
			Code.put(Code.add); // i
			
			// jump(loop1)
			Code.putJump(loop1);
			
			// end1: j = 0
			Code.fixup(end1);
			Code.put(Code.pop); //
			Code.loadConst(0); // j
			
			// loop2: if(j == s2.size) jump(end2)
			int loop2 = Code.pc;
			Code.put(Code.dup); // j j
			Code.load(s2); // j j s2
			loadSetSize(); // j j s2.size
			Code.putFalseJump(Code.ne, 0); // j
			int end2 = Code.pc - 2;
			
			// add(s, s2[j])
			Code.load(s); // j s
			Code.put(Code.dup2); // j s j s
			Code.put(Code.pop); // j s j
			Code.load(s2); // j s j s2
			Code.put(Code.dup_x1); // j s s2 j s2
			Code.put(Code.pop); // j s s2 j
			Code.put(Code.aload); // j s s2[j]
			addElem(); // j
			
			// j++
			Code.loadConst(1); // j 1
			Code.put(Code.add); // j
			
			// jump(loop2)
			Code.putJump(loop2);
			
			// end2:
			Code.fixup(end2);
			Code.put(Code.pop); //
		} else if(DesignatorStatement_Set.getSetop() instanceof Setop_Intersection) {
			// i = 0
			Code.loadConst(0); // i
			
			// loopI: if(i == s1.size) jump(end)
			int loopI = Code.pc;
			Code.put(Code.dup); // i i
			Code.load(s1); // i i s1
			loadSetSize(); // i i s1.size
			Code.putFalseJump(Code.ne, 0); // i
			int end = Code.pc - 2;
			
			// j = 0
			Code.loadConst(0); // i j
			
			// loopJ: if(j == s2.size) jump(addI)
			int loopJ = Code.pc;
			Code.put(Code.dup); // i j j
			Code.load(s2); // i j j s2
			loadSetSize(); // i j j s2.size
			Code.putFalseJump(Code.ne, 0); // i j
			int addI = Code.pc - 2;
			
			// if(s1[i] == s2[j]) jump(notI)
			Code.put(Code.dup2); // i j i j
			Code.put(Code.pop); // i j i
			Code.load(s1); // i j i s1
			Code.put(Code.dup_x1); // i j s1 i s1
			Code.put(Code.pop); // i j s1 i
			Code.put(Code.aload); // i j s1[i]
			Code.put(Code.dup2); // i j s1[i] j s1[i]
			Code.put(Code.pop); // i j s1[i] j
			Code.load(s2); // i j s1[i] j s2
			Code.put(Code.dup_x1); // i j s1[i] s2 j s2
			Code.put(Code.pop); // i j s1[i] s2 j
			Code.put(Code.aload); // i j s1[i] s2[j]
			Code.putFalseJump(Code.ne, 0); // i j
			int notI = Code.pc - 2;
			
			// j++
			Code.loadConst(1); // i j 1
			Code.put(Code.add); // i j
			
			// jump(loopJ1)
			Code.putJump(loopJ);
			
			// addI: add(s, s1[i])
			Code.fixup(addI);
			Code.put(Code.dup); // i j i j
			Code.put(Code.pop); // i j i
			Code.load(s); // i j i s
			Code.put(Code.dup_x1); // i j s i s
			Code.put(Code.pop); // i j s i
			Code.load(s1); // i j s i s1
			Code.put(Code.dup_x1); // i j s s1 i s1
			Code.put(Code.pop); // i j s s1 i
			Code.put(Code.aload); // i j s s1[i]
			addElem(); // i j
			
			// notI: i++
			Code.fixup(notI);
			Code.put(Code.pop); // i
			Code.loadConst(1); // i 1
			Code.put(Code.add); // i
			
			// jump(loopI)
			Code.putJump(loopI);
			
			// end:
			Code.fixup(end);
			Code.put(Code.pop); //
		}
		
		
	}
	
	// ActPars
	
	// Condition
	
	@Override
	public void visit(Condition_Correct Condition_Correct) {
		// CondTermList = false
		Code.putJump(0); // else grana
		elseAdr.push(Code.pc - 2);
		
		// CondTermList = true
		while (!trueCondTerm.empty()) Code.fixup(trueCondTerm.pop());
	}
	
	// CondTerm
	
	@Override
	public void visit(CondTerm CondTerm) {
		// CondFactList = true
		Code.putJump(0); // then grana
		trueCondTerm.push(Code.pc - 2);
		
		// CondFactList = false
		while (!falseCondFact.empty()) Code.fixup(falseCondFact.pop());
	}
	
	// CondFact
	
	@Override
	public void visit(CondFact_Relop CondFact_Relop) {
		Code.putFalseJump(relopCode(CondFact_Relop.getRelop()), 0); //skace ako je Expr = false
		falseCondFact.push(Code.pc - 2);
		// Expr = true
	}
	
	@Override
	public void visit(CondFact_Expr CondFact_Expr) {
		Code.loadConst(0); //dodajemo vestacki nulu na stek da bismo imali sa cim da poredimo Expr
		Code.putFalseJump(Code.ne, 0); //skace ako je Expr = false
		falseCondFact.push(Code.pc - 2);
		// Expr = true
	}
	
	// Expr
	
	@Override
	public void visit(Expr_Map Expr_Map) {
		Obj function = Expr_Map.getDesignator().obj;
		Obj array = Expr_Map.getDesignator1().obj;
		
		// sum = 0
		Code.loadConst(0); // sum
		
		// counter = 0
		Code.loadConst(0); // sum cnt
		
		int loopConditionAdr = Code.pc;
		
		// loopCondition: if(cnt >= array.len()) jump(loopEnd)
		Code.put(Code.dup); // sum cnt cnt
		
		Code.load(array); // sum cnt cnt adr
		Code.put(Code.arraylength); // sum cnt cnt len
		
		Code.putFalseJump(Code.lt, 0); // sum cnt
		int loopStartAdr = Code.pc - 2;
		
		// loopStart: f(niz[cnt])
		Code.load(array); // sum cnt adr
		Code.put(Code.dup2); // sum cnt adr cnt adr
		Code.put(Code.pop); // sum cnt adr cnt
		Code.put(Code.aload); // sum cnt val
		
		int offset = function.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset); // sum cnt val
		
		// sum = sum + f(niz[cnt])
		Code.put(Code.dup_x1); // sum val cnt val
		Code.put(Code.pop); // sum val cnt
		Code.put(Code.dup_x2); // cnt sum val cnt
		Code.put(Code.pop); // cnt sum val
		
		Code.put(Code.add); // cnt sum
		
		Code.put(Code.dup_x1); // sum cnt sum
		Code.put(Code.pop); // sum cnt
		
		// cnt++
		Code.loadConst(1); // sum cnt 1
		Code.put(Code.add); // sum cnt
		
		// jump(loopCondition)
		Code.putJump(loopConditionAdr); // sum cnt
		
		// end: return sum
		Code.fixup(loopStartAdr);
		
		Code.put(Code.pop); // sum
		
	}
	
	@Override
	public void visit(AddopList_Addop AddopList_Addop) {
		if(AddopList_Addop.getAddop() instanceof Addop_Plus) Code.put(Code.add);
		else if(AddopList_Addop.getAddop() instanceof Addop_Minus) Code.put(Code.sub);
	}
	
	// Term
	
	@Override
	public void visit(UnaryFactorList_Mulop UnaryFactorList_Mulop) {
		if(UnaryFactorList_Mulop.getMulop() instanceof Mulop_Mul) Code.put(Code.mul);
		else if(UnaryFactorList_Mulop.getMulop() instanceof Mulop_Div) Code.put(Code.div);
		else if(UnaryFactorList_Mulop.getMulop() instanceof Mulop_Mod) Code.put(Code.rem);
	}
	
	@Override
	public void visit(UnaryFactor_Minus UnaryFactor_Minus) {
		Code.put(Code.neg);
	}
	
	// Factor
	
	@Override
	public void visit(Factor_Designator Factor_Designator) {
		if(Factor_Designator.getOptActParsOpt() instanceof WithActParsOpt) {
			// poziv metode
			int offset = Factor_Designator.getDesignator().obj.getAdr() - Code.pc;
			Code.put(Code.call);
			Code.put2(offset);
		}
		else if(Factor_Designator.getOptActParsOpt() instanceof NoActParsOpt) Code.load(Factor_Designator.getDesignator().obj);
	}
	
	@Override
	public void visit(Factor_NumConst Factor_NumConst) {
		Code.loadConst(Factor_NumConst.getN1());
	}
	
	@Override
	public void visit(Factor_CharConst Factor_CharConst) {
		Code.loadConst(Factor_CharConst.getC1());
	}
	
	@Override
	public void visit(Factor_BoolConst Factor_BoolConst) {
		Code.loadConst(Factor_BoolConst.getB1());
	}
	
	@Override
	public void visit(Factor_New Factor_New) {
		if(Factor_New.getType().struct.equals(setType)) {
			Code.loadConst(1);
			Code.put(Code.add);
			// poslednju lokaciju rezervisemo za broj elemenata (0 pri stvaranju skupa)
		}
		
		Code.put(Code.newarray);
		if(Factor_New.getType().struct.equals(Tab.charType))Code.put(0);
		else Code.put(1);
	}
	
	// Designator
	
	@Override
	public void visit(DesignatorArrayName DesignatorArrayName) {
		Code.load(DesignatorArrayName.obj);
	}
	
}
