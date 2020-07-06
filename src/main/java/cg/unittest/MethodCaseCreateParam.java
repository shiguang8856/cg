package cg.unittest;

import java.lang.reflect.Method;

public class MethodCaseCreateParam {
	private Method methodForTest;
	private String methodName;
	private int mod;
	private Class returnType;
	private Class expected;
	
	private Class throwExp;
	
	
	public Method getMethodForTest() {
		return methodForTest;
	}
	public void setMethodForTest(Method methodForTest) {
		this.methodForTest = methodForTest;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public int getMod() {
		return mod;
	}
	public void setMod(int mod) {
		this.mod = mod;
	}
	public Class getReturnType() {
		return returnType;
	}
	public void setReturnType(Class returnType) {
		this.returnType = returnType;
	}
	public Class getExpected() {
		return expected;
	}
	public void setExpected(Class expected) {
		this.expected = expected;
	}
	public Class getThrowExp() {
		return throwExp;
	}
	public void setThrowExp(Class throwExp) {
		this.throwExp = throwExp;
	}

}
