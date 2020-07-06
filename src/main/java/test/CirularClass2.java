package test;

import java.math.BigDecimal;
import java.math.BigInteger;

public class CirularClass2 {
//	private CicularClass1 cirularClass1;

	private CirularClass3 cirularClass3;
	private Integer i = 1;
	private Boolean b1 = false;
	private String s1 = "";
	private Long l = 1L;
	private Double d = 1d;
	private Float f = 1f;
	private Character c = 'a';
	private Short s2 = (short) 1;
	private Byte b3 = (byte) 1;
	private BigInteger b4 = java.math.BigInteger.valueOf(1);
	private BigDecimal b5 = java.math.BigDecimal.valueOf(0);

	public CirularClass2() {
	}

	public CirularClass3 getCirularClass3() {
		return cirularClass3;
	}

	public void setCirularClass3(CirularClass3 cirularClass3) {
		this.cirularClass3 = cirularClass3;
	}

	public Integer getI() {
		return i;
	}

	public void setI(Integer i) {
		this.i = i;
	}

	public Boolean getB1() {
		return b1;
	}

	public void setB1(Boolean b1) {
		this.b1 = b1;
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

	public Short getS2() {
		return s2;
	}

	public void setS2(Short s2) {
		this.s2 = s2;
	}

	public Byte getB3() {
		return b3;
	}

	public void setB3(Byte b3) {
		this.b3 = b3;
	}

	public BigInteger getB4() {
		return b4;
	}

	public void setB4(BigInteger b4) {
		this.b4 = b4;
	}

	public BigDecimal getB5() {
		return b5;
	}

	public void setB5(BigDecimal b5) {
		this.b5 = b5;
	}

}
