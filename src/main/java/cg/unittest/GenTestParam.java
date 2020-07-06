package cg.unittest;

import java.util.ArrayList;
import java.util.List;

public class GenTestParam {
	List<OneTestClassCreateParam> oneTestClassParamList=new ArrayList<>();
	List<MockClzFactoryCreateParam> mockClzFactoryCreateParamList=new ArrayList<>();
	List<MockClzFactoryCreateResult> mockClzFactoryCreateResult = new ArrayList<>();
	String targetDir;
	
	public String getTargetDir() {
		return targetDir;
	}
	public void setTargetDir(String targetDir) {
		this.targetDir = targetDir;
	}
	public OneTestClassCreateParam getOneTestClassParamByClzEvenNotPresent(Class<?> clzToTest){
		for(OneTestClassCreateParam otc : getOneTestClassParamList()){
			if(otc.getClzBeTest().equals(clzToTest)){
				return otc;
			}
		}
		OneTestClassCreateParam otcp = new OneTestClassCreateParam();
		otcp.setClzBeTest(clzToTest);
		oneTestClassParamList.add(otcp);
		return otcp;
	}
	public OneTestClassCreateParam getOneTestClassParamByClz(Class<?> clzToTest){
		for(OneTestClassCreateParam otc : getOneTestClassParamList()){
			if(otc.getClzBeTest().equals(clzToTest)){
				return otc;
			}
		}
		return null;
	}
	public MockClzFactoryCreateResult getMockClzFactoryCreateResult(Class<?> clzBeMock){
		for(MockClzFactoryCreateResult mccr : getMockClzFactoryCreateResult()){
			if(mccr.getClzBeMock().equals(clzBeMock)){
				return mccr;
			}
		}
		return null;
	}
	public List<OneTestClassCreateParam> getOneTestClassParamList() {
		return oneTestClassParamList;
	}
	public void setOneTestClassParamList(List<OneTestClassCreateParam> oneTestClassParamList) {
		this.oneTestClassParamList = oneTestClassParamList;
	}
	public List<MockClzFactoryCreateParam> getMockClzFactoryCreateParamList() {
		return mockClzFactoryCreateParamList;
	}
	public void setMockClzFactoryCreateParamList(List<MockClzFactoryCreateParam> mockClzFactoryCreateParamList) {
		this.mockClzFactoryCreateParamList = mockClzFactoryCreateParamList;
	}
	public List<MockClzFactoryCreateResult> getMockClzFactoryCreateResult() {
		return mockClzFactoryCreateResult;
	}
	public void setMockClzFactoryCreateResult(List<MockClzFactoryCreateResult> mockClzFactoryCreateResult) {
		this.mockClzFactoryCreateResult = mockClzFactoryCreateResult;
	}
	
	
}
