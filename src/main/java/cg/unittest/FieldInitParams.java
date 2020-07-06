package cg.unittest;

import java.lang.reflect.Field;

public class FieldInitParams {
	private Field field;

	private Class<?> fieldType;
	private int mod;
	private String fieldName;
	private String defaultVal;
	private boolean tooManyMethods;
	private boolean needMock;
	private boolean needMockFactroyClz;
	private boolean isComplexType;
	public boolean isNeedMockFactroyClz() {
		return needMockFactroyClz;
	}
	public void setNeedMockFactroyClz(boolean needMockFactroyClz) {
		this.needMockFactroyClz = needMockFactroyClz;
	}
	public Field getField() {
		return field;
	}
	public void setField(Field field) {
		this.field = field;
	}
	public Class<?> getFieldType() {
		return fieldType;
	}
	public void setFieldType(Class<?> fieldType) {
		this.fieldType = fieldType;
	}
	public int getMod() {
		return mod;
	}
	public void setMod(int mod) {
		this.mod = mod;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getDefaultVal() {
		return defaultVal;
	}
	public void setDefaultVal(String defaultVal) {
		this.defaultVal = defaultVal;
	}
	public boolean isNeedMock() {
		return needMock;
	}
	public void setNeedMock(boolean needMock) {
		this.needMock = needMock;
	}
	public boolean isTooManyMethods() {
		return tooManyMethods;
	}
	public void setTooManyMethods(boolean tooManyMethods) {
		this.tooManyMethods = tooManyMethods;
	}
	public boolean isComplexType() {
		return isComplexType;
	}
	public void setComplexType(boolean isComplexType) {
		this.isComplexType = isComplexType;
	}

}
