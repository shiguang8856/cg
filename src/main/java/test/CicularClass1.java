package test;

import java.math.BigDecimal;
import java.math.BigInteger;

import cg.complexobj.CodeGenContext;
import cg.complexobj.PojoSetterGenerator;

public class CicularClass1 {
	private CirularClass2 cirularClass2;
	private CicularClass1 cirularClass1;
	private CirularClass3 cirularClass3;
	private Integer i = 1;
	private Boolean b4 = false;
	private String s1 = "";
	private Long l = 1L;
	private Double d = 1d;
	private Float f = 1f;
	private Character c = 'a';
	private Short st = (short) 1;
	private Byte b1 = (byte) 1;
	private BigInteger b2 = java.math.BigInteger.valueOf(1);
	private BigDecimal b3 = java.math.BigDecimal.valueOf(0);

	public static void main(String[] args) {
		Class clz = CicularClass1.class;
		Class[] c = null;
		int i = 1;
		String str = PojoSetterGenerator.generateCodeWithoutRecursive(clz, CodeGenContext.getInstance());
		System.out.print(str);
		System.out.print("*************");
		str = PojoSetterGenerator.generateCode(clz, c, CodeGenContext.getInstance());
		System.out.print(str);
	}

	public CirularClass2 getCirularClass2() {
		return cirularClass2;
	}

	public void setCirularClass2(CirularClass2 cirularClass2) {
		this.cirularClass2 = cirularClass2;
	}

	public Integer getI() {
		return i;
	}

	public void setI(Integer i) {
		this.i = i;
	}

	public Boolean getB4() {
		return b4;
	}

	public void setB4(Boolean b4) {
		this.b4 = b4;
	}

	public String getS1() {
		return s1;
	}

	public void setS1(String s1) {
		this.s1 = s1;
	}

	public Long getL() {
		return l;
	}

	public void setL(Long l) {
		this.l = l;
	}

	public Double getD() {
		return d;
	}

	public void setD(Double d) {
		this.d = d;
	}

	public Float getF() {
		return f;
	}

	public void setF(Float f) {
		this.f = f;
	}

	public Character getC() {
		return c;
	}

	public void setC(Character c) {
		this.c = c;
	}

	public Short getSt() {
		return st;
	}

	public void setSt(Short st) {
		this.st = st;
	}

	public Byte getB1() {
		return b1;
	}

	public void setB1(Byte b1) {
		this.b1 = b1;
	}

	public BigInteger getB2() {
		return b2;
	}

	public void setB2(BigInteger b2) {
		this.b2 = b2;
	}

	public BigDecimal getB3() {
		return b3;
	}

	public void setB3(BigDecimal b3) {
		this.b3 = b3;
	}

}
