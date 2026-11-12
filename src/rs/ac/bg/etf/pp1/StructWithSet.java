package rs.ac.bg.etf.pp1;

import javax.naming.ldap.ExtendedRequest;

import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class StructWithSet extends Struct {

	// kodiranje tipova
	public static final int Set = 8;
	
	private int kind; // None, Int, Char, Array, Class, Bool, Enum, Interface, Set

	// niz: tip elementa niza
	// klasa: tip roditeljske klase
	// set: tip elementa skupa
	private Struct elemType; 
	private int size;
	
	public int getSize() {
		return this.size;
	}
	
	public StructWithSet(int kind) {
		super(kind);
		
		// ista struktura kao za niz, samo je kind drugaciji
		if(kind == Struct.Array) {
			this.kind = Set;
			this.elemType = null;
			this.size = 0;
		}
	}
	
	public StructWithSet(int kind, Struct elemType) {
		super(kind, elemType);
		
		// ista struktura kao za niz, samo je kind drugaciji
		if(kind == Struct.Array) {
			this.kind = Set;
			this.elemType = elemType;
			this.size = 0;
		}
	}
	
	@Override
	public boolean equals(Struct other) {
		if(this == other) return true;
		if(!(other instanceof StructWithSet)) return false;
		return this.kind == ((StructWithSet) other).kind;
	}
	
	@Override
	public boolean isRefType() {
		if(this.kind == Set) return true;
		return super.isRefType();
	}
	
	@Override
	public boolean compatibleWith(Struct other) {
		return super.compatibleWith(other);
	}
	
	@Override
	public boolean assignableTo(Struct dest) {
		if(this.kind == Set && dest.getKind() == Set) return true;
		return super.assignableTo(dest);
	}
	
}
