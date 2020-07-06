package cg.unittest;

import java.io.File;

public class MockClzFactoryCreateResult {
	private String moclClzName;
	private String moclClzSimpleName;
	private Class<?> clzBeMock;
	private String factoryMethodName;
	private String code;
	private File file;
	public String getMoclClzName() {
		return moclClzName;
	}
	public void setMoclClzName(String moclClzName) {
		this.moclClzName = moclClzName;
	}
	public String getMoclClzSimpleName() {
		return moclClzSimpleName;
	}
	public void setMoclClzSimpleName(String moclClzSimpleName) {
		this.moclClzSimpleName = moclClzSimpleName;
	}
	public Class<?> getClzBeMock() {
		return clzBeMock;
	}
	public void setClzBeMock(Class<?> clzBeMock) {
		this.clzBeMock = clzBeMock;
	}
	public String getFactoryMethodName() {
		return factoryMethodName;
	}
	public void setFactoryMethodName(String factoryMethodName) {
		this.factoryMethodName = factoryMethodName;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}

}
