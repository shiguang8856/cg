package cg;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodMatcher {
	private Class c;
	private String methodName;
	private Class classParams[];
	private List<MethodMatcher> miList = new ArrayList<>();

	public MethodMatcher(Class c) {
		super();
		this.c = c;
	}

	public MethodMatcher add(String methodName, Class... cp) {
		MethodMatcher mi = new MethodMatcher(c);
		mi.setMethodName(methodName);
		mi.setClassParams(cp);
		miList.add(mi);
		return this;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class getC() {
		return c;
	}

	public Class[] getClassParams() {
		return classParams;
	}

	public void setClassParams(Class[] classParams) {
		this.classParams = classParams;
	}

	public List<MethodMatcher> getMiList() {
		return miList;
	}

	public void setMiList(List<MethodMatcher> miList) {
		this.miList = miList;
	}

	public boolean match(Method m) {
		Class mC = m.getDeclaringClass();
		boolean b = false;
		if (!mC.equals(this.c)) {
			return b;
		}
		Class mp[] = m.getParameterTypes();
		if (m.getName().equals(this.methodName)) {
			if (m.getParameterCount() == 0 && (this.classParams == null || this.classParams.length == 0)) {
				return true;
			}
			if (m.getParameterCount() > 0) {
				if (this.classParams != null && this.classParams.length == mp.length) {
					for (int i = 0; i < mp.length; i++) {
						if (!mp[i].equals(this.classParams[i])) {
							i = mp.length;
						} else if (i == mp.length - 1) {
							b = true;
						}
					}
				}
			}
		}
		return b;
	}
}
