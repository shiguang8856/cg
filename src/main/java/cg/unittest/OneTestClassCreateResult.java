package cg.unittest;

import java.io.File;

public class OneTestClassCreateResult {
	private File file;
	private Class<?> clzForTest;
	private String code;

	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public Class<?> getClzForTest() {
		return clzForTest;
	}
	public void setClzForTest(Class<?> clzForTest) {
		this.clzForTest = clzForTest;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
}
