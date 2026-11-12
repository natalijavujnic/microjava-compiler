package rs.ac.bg.etf.pp1;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class SemanticAnalyzer extends VisitorAdaptor{
	
	private boolean errorDetected;
	Logger log = Logger.getLogger(getClass());
	
	private Struct boolType = Tab.find("bool").getType();
	private Struct setType = Tab.find("set").getType();
	
	private Obj currentProgram;
	private Struct currentType;
	private int constant;
	private Struct constType;
	private Obj MainMethod;
	private Obj currentMethod;
	private boolean ReturnStatement;
	private int DoWhileCounter = 0;
	int nVars;
	

	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message); 
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.info(msg.toString());
	}
	
	public boolean passed(){
    	return !errorDetected;
    }
	
	
	// VISIT METODE
	
	// Program
	
	@Override
	public void visit(ProgramName ProgramName) {
		currentProgram = Tab.insert(Obj.Prog, ProgramName.getI1(), Tab.noType);
		Tab.openScope();
	}
	
	@Override
	public void visit(Program Program) {
		nVars = Tab.currentScope().getnVars(); // prebrojavanje globalnih podataka (CodeGenerator)
		
		Tab.chainLocalSymbols(currentProgram);
		Tab.closeScope();
		currentProgram = null;
		
		if(MainMethod == null || MainMethod.getLevel() > 0) {
			report_error("Program nema odgovarajuci main metod.", Program);
		}
	}

	// Const
	
	@Override
	public void visit(ConstDecl ConstDecl) {
		Obj constObj = Tab.find(ConstDecl.getI1());
		if(constObj != Tab.noObj) {
			report_error("Ponovno definisanje konstante: " + ConstDecl.getI1(), ConstDecl);
		}else {
			if(constType.assignableTo(currentType)) {
				constObj = Tab.insert(Obj.Con, ConstDecl.getI1(), currentType);
				constObj.setAdr(constant);
				report_info("Simbolicka konstanta " + constObj.getName() + printObj(constObj), ConstDecl);
			} else {
				report_error("Tipovi nisu ekvivalentni za konstantu: " + ConstDecl.getI1(), ConstDecl);
			}
			
		}
	}
	
	@Override
	public void visit(NumConst NumConst) {
		constant = NumConst.getN1();
		constType = Tab.intType;
	}
	
	@Override
	public void visit(CharConst CharConst) {
		constant = CharConst.getC1();
		constType = Tab.charType;
	}
	
	@Override
	public void visit(BoolConst BoolConst) {
		constant = BoolConst.getB1();
		constType = boolType;
	}
	
	// Var
	
	@Override
	public void visit(VarDecl VarDecl) {
		Obj varObj = null;
		if(currentMethod == null ) {
			varObj = Tab.find(VarDecl.getI1()); // globalna promenljiva
		} else {
			varObj = Tab.currentScope().findSymbol(VarDecl.getI1()); // lokalna promenljiva
		}
		
		if(varObj == null || varObj == Tab.noObj) {
			if(VarDecl.getBracketOpt() instanceof NoBracket){
				varObj = Tab.insert(Obj.Var, VarDecl.getI1(), currentType);
			} else {
				varObj = Tab.insert(Obj.Var, VarDecl.getI1(), new Struct(Struct.Array, currentType));
			}
			if(varObj.getLevel() == 0) {
				report_info("Globalna promenljiva " + varObj.getName() + printObj(varObj), VarDecl);
			} else {
				report_info("Lokalna promenljiva " + varObj.getName() + printObj(varObj), VarDecl);
			}
			
		} else {
			report_error("Ponovno deklarisanje promenljive: "+ VarDecl.getI1(), VarDecl);
		}
	}
	
	// Method
	
	@Override
	public void visit(MethodName_Void MethodName_Void) {
		MethodName_Void.obj = currentMethod = Tab.insert(Obj.Meth, MethodName_Void.getI1(), Tab.noType);
		Tab.openScope();
		
		if(MethodName_Void.getI1().equalsIgnoreCase("main")) {
			MainMethod = currentMethod;
		}
	}
	
	@Override
	public void visit(MethodName_Type MethodName_Type) {
		MethodName_Type.obj = currentMethod = Tab.insert(Obj.Meth, MethodName_Type.getI2(), currentType);
		Tab.openScope();
	}
	
	@Override
	public void visit(MethodDecl MethodDecl) {
		Tab.chainLocalSymbols(currentMethod);
		Tab.closeScope();
		
		if(currentMethod.getType() != Tab.noType && !ReturnStatement) {
			report_error("Metod nema iskaz return unutar svog tela: " + currentMethod.getName(), MethodDecl);
		}
		
		currentMethod = null;
		ReturnStatement = false;
	}
	
	// FormPar
	
	@Override
	public void visit(FormPar FormPar) {
		Obj formParObj = null;
		formParObj = Tab.currentScope().findSymbol(FormPar.getI2());
		
		if(formParObj == null || formParObj == Tab.noObj) {
			if(FormPar.getBracketOpt() instanceof NoBracket){
				formParObj = Tab.insert(Obj.Var, FormPar.getI2(), currentType);
			} else {
				formParObj = Tab.insert(Obj.Var, FormPar.getI2(), new Struct(Struct.Array, currentType));
			}
			formParObj.setFpPos(1);
			currentMethod.setLevel(currentMethod.getLevel()+1);
		} else {
			report_error("Ponovno deklarisanje formalnog parametra metode: "+ FormPar.getI2(), FormPar);
		}
	}
	
	// Type
	
	@Override
	public void visit(Type Type) {
		Obj typeObj = Tab.find(Type.getI1());
		if(typeObj == Tab.noObj) {
			report_error("Nepostojeci tip podatka: " + Type.getI1(), Type);
			Type.struct = currentType = Tab.noType;
		} else if(typeObj.getKind() != Obj.Type) {
			report_error("Neodgovarajuci tip podatka: " + Type.getI1(), Type);
			Type.struct = currentType = Tab.noType;
		} else {
			Type.struct = currentType = typeObj.getType();
		}
	}
	
	
	// Statement
	
	@Override
	public void visit(Statement_Break Statement_Break) {
		if(DoWhileCounter == 0) {
			report_error("Iskaz break se moze koristiti samo unutar do-while petlje.", Statement_Break);
		}
	}
	
	@Override
	public void visit(Statement_Continue Statement_Continue) {
		if(DoWhileCounter == 0) {
			report_error("Iskaz continue se moze koristiti samo unutar do-while petlje.", Statement_Continue);
		}
	}
	
	@Override
	public void visit(Statement_Return Statement_Return) {
		ReturnStatement = true;
		
		if(Statement_Return.getExprOpt() instanceof NoExpr) {
			if(currentMethod.getType()!=Tab.noType) {
				report_error("Neodgovarajuci return iskaz unutar metode: " + currentMethod.getName(), Statement_Return);
			}
			
		} else {
			WithExpr expr = (WithExpr) Statement_Return.getExprOpt();
			if(!currentMethod.getType().equals(expr.getExpr().struct)) {
				report_error("Neodgovarajuci return iskaz unutar metode: " + currentMethod.getName(), Statement_Return);
			}
		}
	}
	
	@Override
	public void visit(Statement_Read Statement_Read) {
		Obj designator = Statement_Read.getDesignator().obj;
		if(designator.getKind() != Obj.Var && designator.getKind() != Obj.Elem) {
			report_error("Promenljiva na koju se primenjuje read operacija nije odgovarajuca: " + designator.getName(), Statement_Read);
		} else if(!designator.getType().equals(Tab.intType) && !designator.getType().equals(Tab.charType) && !designator.getType().equals(boolType)) {
			report_error("Promenljiva na koju se primenjuje read operacija mora biti tipa int, char ili bool: " + designator.getName(), Statement_Read);
		}
	}
	
	@Override
	public void visit(Statement_Print Statement_Print) {
		Struct expr = Statement_Print.getExpr().struct;
		if(!expr.equals(Tab.intType) && !expr.equals(Tab.charType) && !expr.equals(boolType) && !expr.equals(setType)) {
			report_error("Promenljiva na koju se primenjuje print operacija mora biti tipa int, char, bool ili set", Statement_Print);
		}
	}
	
	@Override
	public void visit(DoBegins DoBegins) {
		DoWhileCounter++;
	}
	
	@Override
	public void visit(Statement_DoWhile Statement_DoWhile) {
		DoWhileCounter--;
	}
	
		
	// DesignatorStatement
	
	public void visit(DesignatorStatement_Assign DesignatorStatement_Assign) {
		Obj designator = DesignatorStatement_Assign.getDesignator().obj;
		Struct expr = DesignatorStatement_Assign.getExpr().struct;
		if(designator.getKind() != Obj.Var && designator.getKind() != Obj.Elem) {
			report_error("Desna strana dodele nije odgovarajuca: " + designator.getName(), DesignatorStatement_Assign);
		} else if(!expr.assignableTo(designator.getType())) {
			report_error("Tipovi nisu kompatibilni pri dodeli: " + designator.getName(), DesignatorStatement_Assign);
		}
	};
	
	@Override
	public void visit(DesignatorStatement_ActPars DesignatorStatement_ActPars) {
		Obj designator = DesignatorStatement_ActPars.getDesignator().obj;
		if(designator.getKind() != Obj.Meth) {
			report_error("Metoda koja se poziva nije odgovarajuca: " + designator.getName(), DesignatorStatement_ActPars);
		} else {
			List<Struct> formalParameters = new ArrayList<>();
			for(Obj o: DesignatorStatement_ActPars.getDesignator().obj.getLocalSymbols()) {
				if(o.getKind() == Obj.Var && o.getLevel() == 1 && o.getFpPos() ==1) {
					formalParameters.add(o.getType());
				}
			}
			
			ActParsPass pass = new ActParsPass();
			DesignatorStatement_ActPars.getActParsOpt().traverseBottomUp(pass);
			List<Struct> actualParameters = pass.actualParameters;
			
			if(formalParameters.size() != actualParameters.size()) {
				report_error("Razlicit broj formalnih i stvarnih argumenata metode: " + designator.getName(), DesignatorStatement_ActPars);
			} else {
				int i = 0;
				while(i < formalParameters.size()) {
					if(!actualParameters.get(i).assignableTo(formalParameters.get(i))) break;
					i++;
				}
				if(i != formalParameters.size()) {
					report_error("Neodgovarajuci tipovi formalnih i stvarnih argumenata metode: " + designator.getName(), DesignatorStatement_ActPars);
				} else {
					report_info("Poziv globalne funkcije " + designator.getName() + printObj(designator), DesignatorStatement_ActPars);
				}
			}
		}
	}
	
	@Override
	public void visit(DesignatorStatement_Inc DesignatorStatement_Inc) {
		Obj designator = DesignatorStatement_Inc.getDesignator().obj;
		if(designator.getKind() != Obj.Var && designator.getKind() != Obj.Elem) {
			report_error("Promenljiva koja se inkrementira nije odgovarajuca: " + designator.getName(), DesignatorStatement_Inc);
		} else if(!designator.getType().equals(Tab.intType)) {
			report_error("Promenljiva koja se inkrementira mora biti tipa int: " + designator.getName(), DesignatorStatement_Inc);
		}
	}
	
	@Override
	public void visit(DesignatorStatement_Dec DesignatorStatement_Dec) {
		Obj designator = DesignatorStatement_Dec.getDesignator().obj;
		if(designator.getKind() != Obj.Var && designator.getKind() != Obj.Elem) {
			report_error("Promenljiva koja se dekrementira nije odgovarajuca: " + designator.getName(), DesignatorStatement_Dec);
		} else if(!designator.getType().equals(Tab.intType)) {
			report_error("Promenljiva koja se dekrementira mora biti tipa int: " + designator.getName(), DesignatorStatement_Dec);
		}
	}
	
	@Override
	public void visit(DesignatorStatement_Set DesignatorStatement_Set) {
		Obj left = DesignatorStatement_Set.getDesignator().obj;
		Obj middle = DesignatorStatement_Set.getDesignator1().obj;
		Obj right = DesignatorStatement_Set.getDesignator2().obj;
		if(left.getType() != setType) {
			report_error("Promenljiva na koje se primenjuje skupovna operacija nije odgovarajuce: " + left.getName(), DesignatorStatement_Set);
		} else if(middle.getType() != setType) {
			report_error("Promenljiva na koje se primenjuje skupovna operacija nije odgovarajuce: " + middle.getName(), DesignatorStatement_Set);
		} else if(right.getType() != setType) {
			report_error("Promenljiva na koje se primenjuje skupovna operacija nije odgovarajuce: " + right.getName(), DesignatorStatement_Set);
		}
	}
	
	// ActPars - posebna klasa
	
	// Condition
	
	@Override
	public void visit(Condition_Correct Condition_Correct) {
		Condition_Correct.struct = Condition_Correct.getCondTermList().struct;
	}
	
	@Override
	public void visit(CondTermList_List CondTermList_List) {
		Struct left = CondTermList_List.getCondTermList().struct;
		Struct right = CondTermList_List.getCondTerm().struct;
		if(!left.equals(boolType) || !right.equals(boolType)) {
			report_error("Vrednosti nisu tipa bool u operaciji OR.", CondTermList_List);
			CondTermList_List.struct = Tab.noType;
		} else {
			CondTermList_List.struct = boolType;
		}
	}
	
	@Override
	public void visit(CondTermList_Term CondTermList_Term) {
		CondTermList_Term.struct = CondTermList_Term.getCondTerm().struct;
	}
	
	// CondTerm
	
	@Override
	public void visit(CondTerm CondTerm) {
		CondTerm.struct = CondTerm.getCondFactList().struct;
	}
	
	@Override
	public void visit(CondFactList_List CondFactList_List) {
		Struct left = CondFactList_List.getCondFactList().struct;
		Struct right = CondFactList_List.getCondFact().struct;
		if(!left.equals(boolType) || !right.equals(boolType)) {
			report_error("Vrednosti nisu tipa bool u operaciji AND.", CondFactList_List);
			CondFactList_List.struct = Tab.noType;
		} else {
			CondFactList_List.struct = boolType;
		}
	}
	
	@Override
	public void visit(CondFactList_Fact CondFactList_Fact) {
		CondFactList_Fact.struct = CondFactList_Fact.getCondFact().struct;
	}
	
	// CondFact
	
	@Override
	public void visit(CondFact_Relop CondFact_Relop) {
		Struct left = CondFact_Relop.getExpr().struct;
		Struct right = CondFact_Relop.getExpr1().struct;
		if(!left.compatibleWith(right)) {
			report_error("Tipovi oba izraza moraju biti kompatibilni.", CondFact_Relop);
			CondFact_Relop.struct = Tab.noType;
		} else {
			if(left.isRefType() || right.isRefType()) {
				if(CondFact_Relop.getRelop() instanceof Relop_Eq || CondFact_Relop.getRelop() instanceof Relop_Neq) {
					CondFact_Relop.struct = boolType;
				} else {
					report_error("Nizovi se mogu porediti samo operatorima == i !=.", CondFact_Relop);
				}
			} else {
				CondFact_Relop.struct = boolType;
			}
			
		}
	}
	
	@Override
	public void visit(CondFact_Expr CondFact_Expr) {
		if(!CondFact_Expr.getExpr().struct.equals(boolType)) {
			report_error("Tip uslovnog izraza mora biti bool", CondFact_Expr);
			CondFact_Expr.struct = Tab.noType;
		} else {
			CondFact_Expr.struct = boolType;
		}
	}
	
	// Expr
	
	@Override
	public void visit(Expr_Term Expr_Term) {
		Expr_Term.struct = Expr_Term.getAddopList().struct;
	}
	
	@Override
	public void visit(Expr_Map Expr_Map) {
		Obj left = Expr_Map.getDesignator().obj;
		if(left.getKind() != Obj.Meth) {
			report_error("Metoda koja se poziva pri funkciji map nije odgovarajuca: " + left.getName(), Expr_Map);
			Expr_Map.struct = Tab.noType;
		} else {
			List<Struct> formalParameters = new ArrayList<>();
			for(Obj o: left.getLocalSymbols()) {
				if(o.getKind() == Obj.Var && o.getLevel() == 1 && o.getFpPos() == 1) {
					formalParameters.add(o.getType());
				}
			}
			Obj right = Expr_Map.getDesignator1().obj;
			
			if(!left.getType().equals(Tab.intType)) {
				report_error("Povratna vrednost metode koja se poziva pri funkciji map nije odgovarajuca: " + left.getName(), Expr_Map);
				Expr_Map.struct = Tab.noType;
			} else if (formalParameters.size() != 1) {
				report_error("Broj parametara metode koja se poziva pri funkciji map nije odgovarajuci: " + left.getName(), Expr_Map);
				Expr_Map.struct = Tab.noType;
			} else if (!formalParameters.get(0).equals(Tab.intType)) {
				report_error("Parametar metode koja se poziva pri funkciji map nije odgovarajuceg tipa: " + left.getName(), Expr_Map);
				Expr_Map.struct = Tab.noType;
			} else if (right.getKind() != Obj.Var || right.getType().getKind() != Struct.Array || right.getType().getElemType() != Tab.intType) {
				report_error("Niz koji se koristi u funkciji map nije odgovarajuci: " + right.getName(), Expr_Map);
				Expr_Map.struct = Tab.noType;
			} else {
				Expr_Map.struct = Tab.intType;
			}
		}
	}
	
	@Override
	public void visit(AddopList_Addop AddopList_Addop) {
		Struct left = AddopList_Addop.getAddopList().struct;
		Struct right = AddopList_Addop.getTerm().struct;
		if(!left.equals(Tab.intType) || !right.equals(Tab.intType)) {
			report_error("Vrednosti nisu tipa int u operaciji addop.", AddopList_Addop);
			AddopList_Addop.struct = Tab.noType;
		} else {
			AddopList_Addop.struct = Tab.intType;
		}
	}
	
	@Override
	public void visit(AddopList_Term AddopList_Term) {
		AddopList_Term.struct = AddopList_Term.getTerm().struct;
	}
	
	// Term

	@Override
	public void visit(Term Term) {
		Term.struct = Term.getUnaryFactorList().struct;
	}
	
	@Override
	public void visit(UnaryFactorList_Mulop UnaryFactorList_Mulop) {
		Struct left = UnaryFactorList_Mulop.getUnaryFactorList().struct;
		Struct right = UnaryFactorList_Mulop.getUnaryFactor().struct;
		if(!left.equals(Tab.intType) || !right.equals(Tab.intType)) {
			report_error("Vrednosti nisu tipa int u operaciji mulop.", UnaryFactorList_Mulop);
			UnaryFactorList_Mulop.struct = Tab.noType;
		} else {
			UnaryFactorList_Mulop.struct = Tab.intType;
		}
	}
	
	@Override
	public void visit(UnaryFactorList_UnaryFactor UnaryFactorList_UnaryFactor) {
		UnaryFactorList_UnaryFactor.struct = UnaryFactorList_UnaryFactor.getUnaryFactor().struct;
	}
	
	// Factor
	
	@Override
	public void visit(UnaryFactor_Minus UnaryFactor_Minus) {
		if(!UnaryFactor_Minus.getFactor().struct.equals(Tab.intType)) {
			report_error("Negirana vrednost nije tipa int", UnaryFactor_Minus);
			UnaryFactor_Minus.struct = Tab.noType;
		} else {
			UnaryFactor_Minus.struct = Tab.intType;
		}
	}
	
	@Override
	public void visit(UnaryFactor_Plus UnaryFactor_Plus) {
		UnaryFactor_Plus.struct = UnaryFactor_Plus.getFactor().struct;
	}
	
	@Override
	public void visit(Factor_Designator Factor_Designator) {
		Obj designator = Factor_Designator.getDesignator().obj;
		if(Factor_Designator.getOptActParsOpt() instanceof WithActParsOpt) {
			if(designator.getKind() != Obj.Meth) {
				report_error("Metoda koja se poziva nije odgovarajuca: " + designator.getName(), Factor_Designator);
				Factor_Designator.struct = Tab.noType;
			} else {
				Factor_Designator.struct = designator.getType(); // uzima Type iz obj cvora i upisuje u struct
				
				List<Struct> formalParameters = new ArrayList<>();
				for(Obj o: designator.getLocalSymbols()) {
					if(o.getKind() == Obj.Var && o.getLevel() == 1 && o.getFpPos() == 1) {
						formalParameters.add(o.getType());
					}
				}
				
				ActParsPass pass = new ActParsPass();
				((WithActParsOpt)Factor_Designator.getOptActParsOpt()).getActParsOpt().traverseBottomUp(pass);
				List<Struct> actualParameters = pass.actualParameters;
				
				//report_info("AP: " + actualParameters.size(), opt);
				//report_info("FP: " + formalParameters.size(), opt);
				
				if(formalParameters.size() != actualParameters.size()) {
					report_error("Razlicit broj formalnih i stvarnih argumenata metode: " + designator.getName(), Factor_Designator);
				} else {
					int i = 0;
					while(i < formalParameters.size()) {
						if(!actualParameters.get(i).assignableTo(formalParameters.get(i))) break;
						i++;
					}
					if(i != formalParameters.size()) {
						report_error("Neodgovarajuci tipovi formalnih i stvarnih argumenata metode: " + designator.getName(), Factor_Designator);
					} else {
						report_info("Poziv globalne funkcije " + designator.getName() + printObj(designator), Factor_Designator);
					}
				}
			}
		} else {
			Factor_Designator.struct = designator.getType();
		}
	}
	
	@Override
	public void visit(Factor_NumConst Factor_NumConst) {
		Factor_NumConst.struct = Tab.intType;
	}
	
	@Override
	public void visit(Factor_CharConst Factor_CharConst) {
		Factor_CharConst.struct = Tab.charType;
	}
	
	@Override
	public void visit(Factor_BoolConst Factor_BoolConst) {
		Factor_BoolConst.struct = boolType;
	}
	
	@Override
	public void visit(Factor_New Factor_New) {
		if(!Factor_New.getExpr().struct.equals(Tab.intType)) {
			report_error("Velicina niza ili skupa nije tipa int.", Factor_New);
			Factor_New.struct = Tab.noType;
		} else {
			if(Factor_New.getType().struct.equals(setType)) Factor_New.struct = new StructWithSet(StructWithSet.Array, currentType);
			else Factor_New.struct = new Struct(Struct.Array, currentType);
		}
	}
	
	@Override
	public void visit(Factor_Expr Factor_Expr) {
		Factor_Expr.struct = Factor_Expr.getExpr().struct;
	}
	
	// Designator
	
	@Override
	public void visit(Designator_Ident Designator_Ident) {
		Obj varObj = Tab.find(Designator_Ident.getI1());
		if(varObj == Tab.noObj) {
			report_error("Pristup nedeklarisanoj promenljivoj: " + Designator_Ident.getI1(), Designator_Ident);
			Designator_Ident.obj = Tab.noObj;
		} else if(varObj.getKind() != Obj.Var && varObj.getKind() != Obj.Con && varObj.getKind() != Obj.Meth) {
			report_error("Pristup promenljivoj neodgovarajuceg tipa: " + Designator_Ident.getI1(), Designator_Ident);
			Designator_Ident.obj = Tab.noObj;
		} else {
			Designator_Ident.obj = varObj;
			if(varObj.getLevel() == 0) {
				report_info("Globalna promenljiva " + varObj.getName() + printObj(varObj), Designator_Ident);
			} else if (varObj.getFpPos() == 1) {
				report_info("Formalni argument funkcije " + varObj.getName() + printObj(varObj), Designator_Ident);
			} else {
				report_info("Lokalna promenljiva " + varObj.getName() + printObj(varObj), Designator_Ident);
			}
			
		}
	}
	
	@Override
	public void visit(DesignatorArrayName DesignatorArrayName) {
		Obj arrObj = Tab.find(DesignatorArrayName.getI1());
		if(arrObj == Tab.noObj) {
			report_error("Pristup nedeklarisanoj promenljivoj niza: " + DesignatorArrayName.getI1(), DesignatorArrayName);
			DesignatorArrayName.obj = Tab.noObj;
		} else if(arrObj.getKind() != Obj.Var || arrObj.getType().getKind() != Struct.Array) {
			report_error("Pristup promenljivoj niza neodgovarajuceg tipa: " + DesignatorArrayName.getI1(), DesignatorArrayName);
			DesignatorArrayName.obj = Tab.noObj;
		} else {
			DesignatorArrayName.obj = arrObj;
		}
	}
	
	@Override
	public void visit(Designator_Array Designator_Array) {
		Obj arrObj = Designator_Array.getDesignatorArrayName().obj;
		if(arrObj == Tab.noObj) {
			// ne treba report error, vec se prijavila nize u stablu
			Designator_Array.obj = Tab.noObj;
		} else if(!Designator_Array.getExpr().struct.equals(Tab.intType)) {
			report_error("Indeks niza nije tipa int.", Designator_Array);
			Designator_Array.obj = Tab.noObj;
		}
		else {
			Designator_Array.obj = new Obj(Obj.Elem, arrObj.getName() + "[]", arrObj.getType().getElemType());
			report_info("Pristup elementu niza " + arrObj.getName() + printObj(arrObj), Designator_Array);
		}
	}
	
	public String printObj (Obj obj) {
		String[] kind = {"Con", "Var", "Type", "Meth", "Fld", "Elem", "Prog"};
		String[] type = {"None", "Int", "Char", "Array", "Class", "Bool", "Enum", "Interface", "Set"};
		
		return " (kind: " + kind[obj.getKind()] + ", type: " + type[obj.getType().getKind()] + ", adr: " + obj.getAdr() + ", level: " + obj.getLevel() + ", fpPos: " + obj.getFpPos() + ")";
	}
}
