package cg;

import java.lang.reflect.Method;

public class AbstractConcreteClassMapping {
	Class abstractClass;
	String abstractClassInstanceName;
	Class concreteClass;
	String concreteClassInstanceName;
	Method concreteCFactoryMethod;
	/** support $ symbol **/
	String methodParamVariables[];

	public Class getAbstractClass() {
		return abstractClass;
	}

	public void setAbstractClass(Class abstractClass) {
		this.abstractClass = abstractClass;
	}

	public String getAbstractClassInstanceName() {
		return abstractClassInstanceName;
	}

	public void setAbstractClassInstanceName(String abstractClassInstanceName) {
		this.abstractClassInstanceName = abstractClassInstanceName;
	}

	public Class getConcreteClass() {
		return concreteClass;
	}

	public void setConcreteClass(Class concreteClass) {
		this.concreteClass = concreteClass;
	}

	public String getConcreteClassInstanceName() {
		return concreteClassInstanceName;
	}

	public void setConcreteClassInstanceName(String concreteClassInstanceName) {
		this.concreteClassInstanceName = concreteClassInstanceName;
	}

	public Method getConcreteCFactoryMethod() {
		return concreteCFactoryMethod;
	}

	public void setConcreteCFactoryMethod(Method concreteCFactoryMethod) {
		this.concreteCFactoryMethod = concreteCFactoryMethod;
	}

	public String[] getMethodParamVariables() {
		return methodParamVariables;
	}

	public void setMethodParamVariables(String[] methodParamVariables) {
		this.methodParamVariables = methodParamVariables;
	}
}
