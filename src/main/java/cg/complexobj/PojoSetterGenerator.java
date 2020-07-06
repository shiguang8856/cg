package cg.complexobj;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import cg.AbstractConcreteClassMapping;
import cg.Replacement;
import cg.Syntax;
import cg.util.Common;
import objinit.CicularClass1;
import objinit.ObjWithDefaultValCreator;

public class PojoSetterGenerator {
	private static StringBuilder allSetSB = new StringBuilder();
	private static final int START_DEEP_INDEX = 1;
	private static List<String> concernedPackageNames = new ArrayList<String>();
	private static List<ClzRefDeep> deepList = new ArrayList<ClzRefDeep>();
	public static List<String> allFieldList = new ArrayList<String>();
	private static CodeGenContext context;
	public static List<Class> calcedList = new ArrayList<>();
	public static Map<Class, AtomicInteger> circularDependencyClz = new HashMap<>();

	public static void init() {
	}

	public static void clear() {
		allSetSB = new StringBuilder();
		calcedList.clear();
		circularDependencyClz.clear();
		concernedPackageNames.clear();
		deepList.clear();
		allFieldList.clear();
		context.getSameTypeCreatedTimesMap().clear();
	}

	public static String getInstanceName(Class c) {
		return Common.getInstanceName(c);
	}

	public static String printAllListFields(Class c) {
		if (c.getSimpleName().equals("String") || c.getSimpleName().equals("Integer")
				|| c.getSimpleName().equals("Long")) {
			return "";
		}
		String instanceName = getInstanceName(c);
		StringBuilder sb = new StringBuilder();
		Field fArray[] = c.getDeclaredFields();
		List<Field> fList = (List) Arrays.asList(fArray);
		fList.forEach(f -> {
			String simpleName = f.getType().getSimpleName();
			String genericTypeName = f.getGenericType().getTypeName();
			if (genericTypeName.startsWith("java.util.List")) {
				String genericType = null;
				String genericTypeNameForPrint = null;
				int start = genericTypeName.indexOf("<");
				int end = genericTypeName.indexOf(">");
				genericType = genericTypeName.substring(start + 1, end);
				genericTypeNameForPrint = genericType.replaceAll("\\$", "\\.");
				String listName = null;
				try {
					listName = getInstanceName(CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner()
							.loadClass(genericType, true)) + "List";
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				StringBuilder refType = new StringBuilder();
				refType.append("List<").append(genericTypeNameForPrint).append("> ").append(listName).append("=")
						.append(" new ").append(" ArrayList<").append(genericTypeNameForPrint).append(">();\r\n");
				// System.out.print(refType.toString());
				allSetSB.append(refType.toString());
			}
		});
		return sb.toString();
	}

	public class ClzRefDeep<T> {
		int i;
		Class<T> c;
		Class genericTypes[];
		boolean hasCollectionReturn;

		public int getI() {
			return i;
		}

		public void setI(int i) {
			this.i = i;
		}

		public Class<T> getC() {
			return c;
		}

		public void setC(Class<T> c) {
			this.c = c;
		}

		public boolean isHasCollectionReturn() {
			return hasCollectionReturn;
		}

		public void setHasCollectionReturn(boolean hasCollectionReturn) {
			this.hasCollectionReturn = hasCollectionReturn;
		}

		public Class[] getGenericTypes() {
			return genericTypes;
		}

		public void setGenericTypes(Class[] genericTypes) {
			this.genericTypes = genericTypes;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			// result = prime * result + getOuterType().hashCode();
			result = prime * result + ((c == null) ? 0 : c.hashCode());
			result = prime * result + Arrays.hashCode(genericTypes);
			result = prime * result + i;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ClzRefDeep other = (ClzRefDeep) obj;
			/*
			 * if (!getOuterType().equals(other.getOuterType())) return false;
			 */
			if (c == null) {
				if (other.c != null)
					return false;
			} else if (!c.equals(other.c))
				return false;
			if (!Arrays.equals(genericTypes, other.genericTypes))
				return false;
			// TODO if object ref each other recucively
			if (i != other.i)
				return false;
			return true;
		}
		/*
		 * private ObjSetCodeGenAfterCreateMethod getOuterType() { return
		 * ObjSetCodeGenAfterCreateMethod.this; }
		 */
	}

	public static boolean containsIgnoreCase(Collection<String> coll, final String val) {
		return coll.stream().anyMatch(e -> {
			boolean b = e.equalsIgnoreCase(val);
			return b;
		});
	}

	public static String getClassCreateStr(Class c) {
		if (c.isEnum()) {
			return Common.getEnumCreateStr(c);
		} else if (c.isArray()) {
			return getObjCreateStrWithNew(c);
		} else {
			return getObjCreateStrWithNew(c);
		}
	}

	public static String getObjCreateStrWithNew(Class c) {
		StringBuilder sb = new StringBuilder();
		String classNameForPrint = Common.getClassName(c, context);
		if (classNameForPrint.indexOf("$") != -1) {
			classNameForPrint = classNameForPrint.replaceAll("\\$", "\\.");
		}
		sb.append(" new ").append(classNameForPrint).append("()");
		return sb.toString();
	}
	public static  String genNewClz(Class c) {
		StringBuilder sb = new StringBuilder();
		String classNameForPrint = Common.getClassName(c, context);
		if (classNameForPrint.indexOf("$") != -1) {
			classNameForPrint = classNameForPrint.replaceAll("\\$", "\\.");
		}
		String instanceName = Common.getInstanceName(c, context.getSameTypeCreatedTimesMap());
		sb.append(classNameForPrint).append(" ").append(instanceName).append(" = ").append(getClassCreateStr(c))
				.append(";\r\n");
	    return sb.toString();
	}
	public static String printAllSet(Class c, CodeGenContext context) {
		return printAllSet(c, new HashMap(), null, context);
	}

	public static String printAllSet(Class c, Map<Class, String> typeInstanceNames, Class genericTypes[],
			CodeGenContext context) {
		if (!Common.isComplexType(c)) {
			return "";
		}
		//TODO
//		String instanceName = Common.getInstanceName(c, context.getSameTypeCreatedTimesMap());
		String instanceName = Common.getInstanceName(c);
		StringBuilder sb = new StringBuilder();
		typeInstanceNames.put(c, instanceName);
		allFieldList.clear();
		Common.getAllFieldNames(c, concernedPackageNames, allFieldList);
		List<String> incompleteFieldNames = allFieldList;
		Method methods[] = c.getMethods();
		List<Method> methodList = (List) Arrays.asList(methods);
		List<Method> allSetMethods = methodList.stream().filter(m -> {
			boolean isFieldSet = false;
			if (m.getName().startsWith("set") || m.getName().startsWith("is") || m.getName().startsWith("has")) {
				// and method length of parameters is 1
				int paramCount = m.getParameterCount();
				if (paramCount == 1) {
					isFieldSet = true;
					String fieldName = null;
					if (m.getName().startsWith("set") || m.getName().startsWith("has")) {
						fieldName = m.getName().substring(3, m.getName().length());
					} else if (m.getName().startsWith("is")) {
						fieldName = m.getName().substring(2, m.getName().length());
					}
					incompleteFieldNames.remove(Common.makeFirstCharInLowercase(fieldName));
				}
			}
			return isFieldSet;
		}).collect(Collectors.toList());
		allSetMethods.forEach(m -> {
			Parameter p[] = m.getParameters();
			if (p != null) {
				Parameter p1 = p[0];
				String typeName = null;
				typeName = p1.getParameterizedType().getTypeName();
				if (typeName.startsWith("java.util.List")) {
					String genericType = null;
					int start = typeName.indexOf("<");
					int end = typeName.indexOf(">");
					genericType = typeName.substring(start + 1, end);
					String listName = null;
					String listItemName = null;
					Class pClass = null;
					try {
						pClass = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner()
								.loadClass(genericType, true);
						listItemName = Common.getInstanceName(pClass, context.getSameTypeCreatedTimesMap());
						listName = listItemName + "List";
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					StringBuilder refType = new StringBuilder();
					String genericTypeForPrint = genericType.replaceAll("\\$", "\\.");
					refType.append("List<").append(genericTypeForPrint).append("> ").append(listName).append("=")
							.append(" new ").append(" ArrayList<").append(genericTypeForPrint).append(">();\r\n");
					String defaultVal = null;
					if (typeInstanceNames.containsKey(pClass)) {
						defaultVal = typeInstanceNames.get(pClass);
					} else {
						defaultVal = Common.getDefaultVal(pClass);
					}
					refType.append(listName).append(".add(").append(defaultVal).append(");\r\n");
					// System.out.print(refType.toString());
					allSetSB.append(refType.toString());
					sb.append(instanceName).append(".").append(m.getName()).append("(").append(listName)
							.append(");\r\n");
				} else {
					if (Common.isComplexType(p1.getType())) {
						if (context.getAbstractConcreteClassMap().containsKey(p1.getType())) {
							AbstractConcreteClassMapping cm = context.getAbstractConcreteClassMap().get(p1.getType());
							Class cc = cm.getConcreteClass();
							sb.append(instanceName).append(".").append(m.getName()).append("(")
									.append(Common.getInstanceName(cc)).append(");\r\n");
						} else {
							sb.append(instanceName).append(".").append(m.getName()).append("(")
									.append(Common.getDefaultVal(p1)).append(");\r\n");
						}
					} else {
						sb.append(instanceName).append(".").append(m.getName()).append("(")
								.append(Common.getDefaultVal(p1)).append(");\r\n");
					}
				}
			}
		});
		if (incompleteFieldNames.size() > 0) {
			List<Method> listGetMethods = methodList.stream().filter(m -> {
				boolean isListFieldGet = false;
				if (m.getName().startsWith("get")) {
					// and method length of parameters is 1
					int paramCount = m.getParameterCount();
					if (paramCount == 0) {
						String fieldNameInMethod = m.getName().substring(3);
						String fieldInstanceName = Common.makeFirstCharInLowercase(fieldNameInMethod);
						if (containsIgnoreCase(incompleteFieldNames, fieldInstanceName)
								&& Common.isMethodReturnCollectionType(m)) {
							isListFieldGet = true;
							incompleteFieldNames.remove(fieldInstanceName);
						}
					}
				}
				return isListFieldGet;
			}).collect(Collectors.toList());
			listGetMethods.forEach(m -> {
				String fieldNameInMethod = m.getName().substring(3);
				String fieldInstanceName = Common.makeFirstCharInLowercase(fieldNameInMethod);
				String typeName = m.getGenericReturnType().getTypeName();
				String genericType = null;
				int start = typeName.indexOf("<");
				int end = typeName.indexOf(">");
				genericType = typeName.substring(start + 1, end);
				Class listGenericType = null;
				try {
					listGenericType = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner()
							.loadClass(genericType, true);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				// if(eitherPrimitiveOrPrimitivePresentation(listGenericType)){
				if (!Common.isComplexType(listGenericType)) {
					sb.append(instanceName).append(".").append(m.getName()).append("().add(")
							.append(Common.getDefaultVal(listGenericType)).append(");\r\n");
				} else {
					String defaultVal = null;
					if (typeInstanceNames.containsKey(listGenericType)) {
						defaultVal = typeInstanceNames.get(listGenericType);
					} else {
						defaultVal = fieldInstanceName;
					}
					sb.append(instanceName).append(".").append(m.getName()).append("().add(").append(defaultVal)
							.append(");\r\n");
				}
			});
		}
		if (Common.isACollection(c) && genericTypes != null && genericTypes[0] != null) {
			Class gt = genericTypes[0];
			if (Common.isComplexType(gt)) {
				String gtInstanceName = Common.getInstanceName(gt);
				sb.append(instanceName).append(".").append("add").append("(").append(gtInstanceName).append(");\r\n");
			} else {
				sb.append(instanceName).append(".").append("add").append("(").append(Common.getDefaultVal(gt))
						.append(");\r\n");
			}

		}
		// System.out.print(sb.toString());
		allSetSB.append(sb.toString());
		return sb.toString();
	}

	public static String generateCode(Class c, Class genericTypes[], CodeGenContext context) {
		if (null == c) {
			return "";
		}
		if (c.isInterface() || c.getEnclosingClass() != null) {
			return "";
		}
		if (c.isArray()) {
			c = c.getClass().getComponentType();
		}
		// calc default concerned packages
		List<String> concernedPacks = Common.getDefaultConcernedPacks(c);
		if (genericTypes != null) {
			for (Class gt : genericTypes) {
				if (gt != null) {
					concernedPacks.addAll(Common.getDefaultConcernedPacks(gt));
				}
			}
		}
		return generateCode(c, genericTypes, concernedPacks, context);
	}
	
	public static String generateCode(Class c, CodeGenContext context) {
		return generateCode(c, (Class[]) null, context);
	}

	public static String generateCode(Class c, List<String> concernedPacks, CodeGenContext context) {
		return generateCode(c, (Class[]) null, concernedPacks, context);
	}

	public static String generateCode(Class c, Class genericTypes[], List<String> concernedPacks,
			CodeGenContext context) {
		return generateCodeWithoutRecursive(c, concernedPacks, context, true);
	}
	public static String generateCodeold(Class c, Class genericTypes[], List<String> concernedPacks,
			CodeGenContext context) {
		
		ObjWithDefaultValCreator obf = new ObjWithDefaultValCreator();
		List<Class<?>> allComplextTypes = new ArrayList<>();
		concernedPackageNames = concernedPacks;
		deepList.clear();
		String code = "";
		try {
			PojoSetterGenerator.context = context;
			Map<java.lang.Class, String> typeInstanceNames = new HashMap<>();
			allComplextTypes.addAll(obf.getAllReferComplexType(c));
			if (genericTypes != null) {
				for (Class gt : genericTypes) {
					allComplextTypes.addAll(obf.getAllReferComplexType(gt));
				}
			}
			deepList=allComplextTypes.stream().map(e-> {
				ClzRefDeep cdf = new PojoSetterGenerator().new ClzRefDeep();
				cdf.setC(e);
				cdf.setI(0);
				cdf.setHasCollectionReturn(false);
				return cdf;
			}).collect(Collectors.toList());
			StringBuilder allNewClzCodeLines = new StringBuilder();
			allComplextTypes.stream().forEach(e-> {
				allNewClzCodeLines.append(PojoSetterGenerator.genNewClz(e)); 
			});

			// sort
			deepList.forEach(cd -> {
				Class clzTmp = cd.getC();
				try {
//					Class clzTmp = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner()
//							.loadClass(typeName, true);
					Common.importC(clzTmp, context);
					
					printAllSet(clzTmp, typeInstanceNames, cd.getGenericTypes(), context);
				} catch (Exception e) {
					e.printStackTrace();
				}
				;
			});
			allSetSB.insert(0, allNewClzCodeLines.toString());
			code = allSetSB.toString();
			allSetSB = new StringBuilder();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			clear();
		}
		return code;
	}

	public static String generateCodeWithoutRecursive(Class c, CodeGenContext context, boolean clearAfterFinish) {
		List<String> concernedPacks = Common.getDefaultConcernedPacks(c);
		return generateCodeWithoutRecursive(c, concernedPacks, context, clearAfterFinish);
	}

	public static String generateCodeWithoutRecursive(Class c, CodeGenContext context) {
		List<String> concernedPacks = Common.getDefaultConcernedPacks(c);
		return generateCodeWithoutRecursive(c, concernedPacks, context);
	}

	public static String generateCodeWithoutRecursive(Class c, List<String> concernedPacks, CodeGenContext context) {
		return generateCodeWithoutRecursive(c, concernedPacks, context, true);
	}
	public static String generateCodeWithoutRecursive(Class c, List<String> concernedPacks, CodeGenContext context,
			boolean clearAfterFinish) {
		context.getVariableMap().put(Replacement.TMP_CLASS_NAME.name(), c.getSimpleName());
		context.getVariableMap().put(Replacement.TMP_INSTANCE_NAME.name(), Common.getInstanceName(c));
		String str;
		try {
			str = Common.replaceAllKeyWord(Syntax.PODAM_INIT_POJO, context.getVariableMap());
		} catch (Exception e) {
			e.printStackTrace();
			str="";
		}
		return str;
	}
	public static String generateCodeWithoutRecursiveOld(Class c, List<String> concernedPacks, CodeGenContext context,
			boolean clearAfterFinish) {
		concernedPackageNames = concernedPacks;
		PojoSetterGenerator.context = context;
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(PojoSetterGenerator.genNewClz(c)); 
			String str = printAllSet(c, context);
			sb.append(str);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (clearAfterFinish) {
				clear();
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		List<String> list = new ArrayList<>();
		PojoSetterGenerator pojoSetterGenerator = new PojoSetterGenerator();
		CodeGenContext codeGenContext = CodeGenContext.getInstance();
		String s = PojoSetterGenerator.generateCode(CicularClass1.class, codeGenContext);
		System.out.println(s);
	}
}
