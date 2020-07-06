package cg.unittest;

public class MockClzFactoryCreateParam {
	String targetDir;
	private Class<?> clzBeMock;
	private String factoryMethodName;
	
	public String getFactoryMethodName() {
		return factoryMethodName;
	}
	public void setFactoryMethodName(String factoryMethodName) {
		this.factoryMethodName = factoryMethodName;
	}
	public String getTargetDir() {
		return targetDir;
	}
	public void setTargetDir(String targetDir) {
		this.targetDir = targetDir;
	}
	public Class<?> getClzBeMock() {
		return clzBeMock;
	}
	public void setClzBeMock(Class<?> clzBeMock) {
		this.clzBeMock = clzBeMock;
	}


}
