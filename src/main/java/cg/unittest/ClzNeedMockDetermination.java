package cg.unittest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import cg.util.Common;

public class ClzNeedMockDetermination {
	public boolean needMock(Class<?> c) throws ClassNotFoundException {
		boolean b = true;
		boolean isInterface = c.isInterface();
		boolean isAbstract = Common.isAbstract(c);
		if (isInterface || isAbstract) {
			return true;
		}
		boolean isAnySpecifiedAnn = isAnySpecifiedAnnPackMatch(c,
				new String[] { "javax.persistence", "javax.xml.bind", "com.fasterxml.jackson.databind" });
		if (isAnySpecifiedAnn) {
			return false;
		}
		// for map or collection, treate it as data, even i
		// t likes Map<class, Repository>
		boolean isSimpleType = isSimpleTypeOrCollectionOfSimple(c);
		boolean isSimpleTypeMap = isSimpleTypeOrCollectionOfSimple(c);
		boolean isAllFieldsSimpleType = isAllFieldsSimpleType(c);
		if (isSimpleType) {
			return false;
		}
		if (isSimpleTypeMap) {
			return false;
		}
		if (isAllFieldsSimpleType) {
			return false;
		}
		boolean isAllStaticMethods = isAllMethodStatic(c);
		if (isAllStaticMethods) {
			return false;
		}
		return b;
	}

	public boolean isAllMethodStatic(Class<?> c) {
		Method[] ms = Common.getAllMethods(c);
		for (Method m : ms) {
			if ((m.getModifiers() & Modifier.STATIC) != 1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * enum and all primitive types not need mock Object and Exception child classes
	 * and has default constructor or simply type param constructor are data Object
	 * Collection, arrays of all above classes not need mock
	 */
	public boolean isSimpleTypeOrMapOfSimple(Class<?> c) {
		boolean b = false;
		if (c.isAssignableFrom(Map.class)) {
			return true;
		} else {

		}
		b = Common.isPrimitiveType(c);
		if (b) {
			return true;
		} else {
			if (c.isAssignableFrom(Map.class)) {
				return true;
			} else {

			}
		}
		return b;
	}

	/**
	 * enum and all primitive types not need mock Object and Exception child classes
	 * and has default constructor or simply type param constructor are data Object
	 * Collection, arrays of all above classes not need mock
	 */
	public boolean isSimpleType(Class<?> c) {
		boolean b = false;
		b = Common.isPrimitiveType(c);
		if (b) {
			return true;
		} else {
			if (c.isAssignableFrom(Exception.class)) {
				return true;
			} else {

			}
		}
		return b;
	}

	/**
	 * enum and all primitive types not need mock Object and Exception child classes
	 * and has default constructor or simply type param constructor are data Object
	 * Collection, arrays of all above classes not need mock
	 */
	public boolean isSimpleTypeOrCollectionOfSimple(Class<?> c) {
		boolean b = false;
		b = Common.isPrimitiveType(c);
		if (b) {
			return true;
		} else {
			if (c.isAssignableFrom(Exception.class)) {
				return true;
			} else if (c.isAssignableFrom(Collection.class) || c.isAssignableFrom(Map.class)) {
				return true;
			}
		}
		return b;
	}

	private boolean isAllFieldsSimpleType(Class<?> c) {
		List<Field> fList = Common.getAllFields(c);
		boolean isAllFieldsSimpleType = fList.stream().anyMatch(f -> {
			return isSimpleType(f.getDeclaringClass());
		});
		return isAllFieldsSimpleType;
	}

	/**
	 * Entity, JaxbElement, xmlType, Json related objects are data Object
	 */
	private boolean isAnySpecifiedAnnPackMatch(Class<?> clz, String[] annPackName) throws ClassNotFoundException {
		List<Annotation> list = new ArrayList<>();
		Common.getAllClzAnnotation(clz, list);
		boolean anySpecifiedAnnPackMatch = list.stream().anyMatch(ann -> {
			boolean am = false;
			for (String packName : annPackName) {
				if (ann.annotationType().getName().startsWith(packName)) {
					am = true;
					break;
				}
			}
			return am;
		});
		return anySpecifiedAnnPackMatch;
	}
	/**
	 * Entity, JaxbElement, xmlType, Json related objects are data Object
	 *//*
		 * private boolean isSpecifiedAnn(Class<?> clz, String annClzName) throws
		 * ClassNotFoundException { Class annClz =
		 * CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner().loadClass(annClzName, true); return isSpecifiedAnnoInclz(clz,
		 * annClz); }
		 * 
		 * private <A extends Annotation> boolean isSpecifiedAnnoInclz(Class<?> c,
		 * Class<A> ac) { Annotation anns[] = c.getAnnotationsByType(ac); return
		 * anns.length > 1; }
		 */
}
