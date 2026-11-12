package rs.ac.bg.etf.pp1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java_cup.runtime.Symbol;

import org.apache.log4j.*;
import org.apache.log4j.xml.DOMConfigurator;

import rs.ac.bg.etf.pp1.ast.*;
import rs.ac.bg.etf.pp1.util.Log4JUtils;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class MJParserTest {

	static {
		DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
		Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
	}
	
	public static void main(String[] args) throws Exception {
		
		Logger log = Logger.getLogger(MJParserTest.class);
		
		Reader br = null;
		try {
			File sourceCode = new File("test/program.mj");
			log.info("Compiling source file: " + sourceCode.getAbsolutePath());
			
			br = new BufferedReader(new FileReader(sourceCode));
			Yylex lexer = new Yylex(br);
			
			MJParser p = new MJParser(lexer);
			
			// formiranje sintaksnog stabla
	        Symbol s = p.parse();  //Symbol s = p.debug_parse(); //ispis SH/RD
	        
	        Program prog = (Program)(s.value);
	        
			// ispis AST
			log.info(prog.toString(""));
			log.info("===================================");
			
			// inicijalizacija tabele simbola
			Tab.init();
			
			{
				// dodavanje tipova bool i set
				Struct boolType = new Struct(Struct.Bool);
				Obj boolObj = Tab.insert(Obj.Type, "bool", boolType);
				boolObj.setAdr(-1);
				boolObj.setLevel(-1);
				
				Struct setType = new Struct(StructWithSet.Set, Tab.intType);
				Obj setObj = Tab.insert(Obj.Type, "set", setType);
				setObj.setAdr(-1);
				setObj.setLevel(-1);
				
				// azuriranje broja formalnih parametara postojecih metoda
				for(Obj o: Tab.chrObj.getLocalSymbols()) {
					if(o.getName().equals("i")) {
						o.setFpPos(1);
					}
				}
				
				for(Obj o: Tab.ordObj.getLocalSymbols()) {
					if(o.getName().equals("ch")) {
						o.setFpPos(1);
					}
				}
				
				for(Obj o: Tab.lenObj.getLocalSymbols()) {
					if(o.getName().equals("arr")) {
						o.setFpPos(1);
					}
				}
				
				// dodavanje novih metoda
				Obj addObj = new Obj(Obj.Meth, "add", Tab.noType, 0, 2);
				Tab.currentScope().addToLocals(addObj);
				Tab.openScope();
				Obj addA = new Obj(Obj.Var, "a", setType, 0, 1);
				addA.setFpPos(1);
				Tab.currentScope().addToLocals(addA);
				Obj addB = new Obj(Obj.Var, "b", Tab.intType, 1, 1);
				addB.setFpPos(1);
				Tab.currentScope().addToLocals(addB);
				addObj.setLocals(Tab.currentScope().getLocals());
				Tab.closeScope();
	
				Obj addAllObj = new Obj(Obj.Meth, "addAll", Tab.noType, 0, 2);
				Tab.currentScope().addToLocals(addAllObj);
				Tab.openScope();
				Obj addAllA = new Obj(Obj.Var, "a", setType, 0, 1);
				addAllA.setFpPos(1);
				Tab.currentScope().addToLocals(addAllA);
				Obj addAllB = new Obj(Obj.Var, "b", new Struct(Struct.Array, Tab.intType), 1, 1);
				addAllB.setFpPos(1);
				Tab.currentScope().addToLocals(addAllB);
				addAllObj.setLocals(Tab.currentScope().getLocals());
				Tab.closeScope();
			}
			
			// semanticka analiza
			SemanticAnalyzer v = new SemanticAnalyzer();
			prog.traverseBottomUp(v);
			
			// ispis tabele simbola
			log.info("===================================");
			Tab.dump();
			
			/*
			// ispis prepoznatih programskih konstrukcija
			RuleVisitor v = new RuleVisitor();
			prog.traverseBottomUp(v); 
	      
			log.info(" Print count calls = " + v.printCallCount);

			log.info(" Deklarisanih promenljivih ima = " + v.varDeclCount);
			*/ 
			
			if(!p.errorDetected && v.passed()) {
				
				// generisanje koda
				File objFile = new File("test/program.obj");
				if(objFile.exists()) objFile.delete();
				
				CodeGenerator codeGenerator = new CodeGenerator();
				prog.traverseBottomUp(codeGenerator);
				Code.dataSize = v.nVars;
				Code.mainPc = codeGenerator.getMainPc();
				Code.write(new FileOutputStream(objFile));
				
				log.info("Parsiranje uspesno zavrseno");
			} else {
				log.info("Parsiranje NIJE uspesno zavrseno");
			}
		} 
		finally {
			if (br != null) try { br.close(); } catch (IOException e1) { log.error(e1.getMessage(), e1); }
		}

	}
	
	
}
