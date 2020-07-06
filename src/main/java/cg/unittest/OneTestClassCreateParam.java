package cg.unittest;

import java.util.ArrayList;
import java.util.List;

public class OneTestClassCreateParam {
	Class<?> clzBeTest;
	List<FieldInitParams> fieldInitParamList=new ArrayList<>();
	List<MockClzCreateParams> mockClzCreateParamList=new ArrayList<>();
	List<MethodCaseCreateParam> MethodCaseCreateParamList =new ArrayList<>();
	private OneTestClassCreateResult oneTestClassCreateResult;
	public Class<?> getClzBeTest() {
		return clzBeTest;
	}
	public void setClzBeTest(Class<?> clzBeTest) {
		this.clzBeTest = clzBeTest;
	}
	public List<FieldInitParams> getFieldInitParamList() {
		return fieldInitParamList;
	}
	public void setFieldInitParamList(List<FieldInitParams> fieldInitParamList) {
		this.fieldInitParamList = fieldInitParamList;
	}
	public List<MockClzCreateParams> getMockClzCreateParamList() {
		return mockClzCreateParamList;
	}
	public void setMockClzCreateParamList(List<MockClzCreateParams> mockClzCreateParamList) {
		this.mockClzCreateParamList = mockClzCreateParamList;
	}
	public List<MethodCaseCreateParam> getMethodCaseCreateParamList() {
		return MethodCaseCreateParamList;
	}
	public void setMethodCaseCreateParamList(List<MethodCaseCreateParam> methodCaseCreateParamList) {
		MethodCaseCreateParamList = methodCaseCreateParamList;
	}
	public OneTestClassCreateResult getOneTestClassCreateResult() {
		return oneTestClassCreateResult;
	}
	public void setOneTestClassCreateResult(OneTestClassCreateResult oneTestClassCreateResult) {
		this.oneTestClassCreateResult = oneTestClassCreateResult;
	}
	
	

}
