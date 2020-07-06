package cg.unittest;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cg.Replacement;
import cg.Syntax;
import cg.complexobj.CodeGenContext;
import cg.util.Common;

public class MockClzFactoryGenerator {

	private String genClassBegin(Class c, CodeGenContext context) throws Exception {
		Common.setModifier(Modifier.PUBLIC, context);
		return Common.genMockClassBegin(c, context);
	}

	public MockClzFactoryCreateResult genMock(Class clzToBeMock, String targetDirectory, CodeGenContext context)
			throws Exception {
		MockClzFactoryCreateResult mcr = genMockClassSource(clzToBeMock, context);
		if (mcr == null) {
			return mcr;
		}
		File file = new File(targetDirectory);
		if (!file.exists()) {
			file.mkdirs();
		}
		File mockFactoryFile = new File(targetDirectory + File.separator + clzToBeMock.getSimpleName() + "Mock.java");
		mcr.setFile(mockFactoryFile);
		FileOutputStream fos = new FileOutputStream(mockFactoryFile);
		FileChannel fc = fos.getChannel();
		ByteBuffer bb = Common.utf8CharsetEncoder.encode(CharBuffer.wrap(mcr.getCode()));
		fc.write(bb);
		fc.close();
		fos.close();
		return mcr;
	}

	public MockClzFactoryCreateResult genMockClassSource(Class c, CodeGenContext context) throws Exception {
		MockClzFactoryCreateResult mockClzCreateResult = new MockClzFactoryCreateResult();
		mockClzCreateResult.setMoclClzName(c.getName());
		mockClzCreateResult.setMoclClzSimpleName(c.getSimpleName());
		mockClzCreateResult.setClzBeMock(c);
		String methodName = Common.getDefaultCreateMethodName(c);
		mockClzCreateResult.setFactoryMethodName(methodName);

		StringBuilder sb = new StringBuilder();
		String classBegin = genClassBegin(c, context);
		context.getPackageSB().append(Common.genPackage(c, context.getVariableMap()));
		context.getImportSB()
				.append(Common.genimportStr(c, context.getAlreadyImportClasses(), context.getVariableMap()));
		String methodBody = genAllMethodMockLines(c, context);
		Common.setModifier(Modifier.PUBLIC | Modifier.STATIC, context);
//		context.getVariableMap().put(Replacement.PRIVILEDGE_MODIFIER.name(), "public");
//		context.getVariableMap().put(Replacement.STATIC_MODIFIER.name(), "static");
		context.getVariableMap().put(Replacement.METHOD_RETURN_TYPE.name(), c.getSimpleName());
		context.getVariableMap().put(Replacement.METHOD_NAME.name(), methodName);
		context.getVariableMap().put(Replacement.METHOD_PARAMS_DECLARE.name(), "");
		String returnTypeInstanceName = Common.getInstanceName(c);
		context.getVariableMap().put(Replacement.METHOD_BODY.name(), methodBody);
		context.getVariableMap().put(Replacement.METHOD_RETURN_VALUE.name(), returnTypeInstanceName);
		context.getVariableMap().put(Replacement.THROWS_EXCEPTION.name(), "");
		String mockMethodCreateStr = Common.replaceAllKeyWord(Syntax.NORMAL_METHOD, context.getVariableMap());
		sb.append(context.getPackageSB()).append(Syntax.NEW_LINE).append(context.getImportSB()).append(classBegin);
		sb.append(Syntax.NEW_LINE).append(mockMethodCreateStr);
		// sb.append(Syntax.NEW_LINE).append(complexTypeCreateMethodSB);
		sb.append(Syntax.NEW_LINE).append(context.getCreatedComplexTypeMethod());
		sb.append(Syntax.NEW_LINE).append(Syntax.CLASS_END_LINE);
		context.clearExceptGlobalParam();
		mockClzCreateResult.setCode(sb.toString());
		return mockClzCreateResult;
	}

	public boolean isConcern(CodeGenContext context, Method m) {
		boolean b = false;
		cg.MethodMatcher mi = context.getMethodMatchers().get(m.getDeclaringClass());
		boolean notEmptyMM = mi != null && mi.getMiList() != null && mi.getMiList().size() > 0;
		if (notEmptyMM) {
			for (cg.MethodMatcher mm : mi.getMiList()) {
				if (mm.match(m)) {
					return true;
				}
			}
		} else {
			b = true;
		}
		return b;
	}

	public String genAllMethodMockLines(Class c, CodeGenContext context) throws Exception {
		context.renewSameTypeCreatedTimesMap();
		StringBuilder sb = new StringBuilder();
		StringBuilder mockMethodsLine = new StringBuilder();
		Method ms[] = Common.getAllPublicMethods(c);
		context.getVariableMap().put(Replacement.TMP_CLASS_NAME.name(), c.getSimpleName());
		String instanceName = Common.getInstanceName(c);
		context.getVariableMap().put(Replacement.TMP_INSTANCE_NAME.name(), instanceName);
		String mockClassLine = Common.replaceAllKeyWord(Syntax.MOCK_CLASS_LINE, context.getVariableMap());
		sb.append(mockClassLine);
		Common.importPack("org.mockito.*", context.getVariableMap(), context.getImportSB());
		boolean hasExceptionThrow = false;
		for (Method m : ms) {
			if (!Common.isStatic(m) && isConcern(context, m)// &&
															// Common.isAbstract(m)
			) {
				if (m.getExceptionTypes() != null && m.getExceptionTypes().length > 0) {
					hasExceptionThrow = true;
				}
				Class<?>[] ccs = Common.getConcreteClass(m.getParameterTypes(), context);
				if (!Common.isVoid(m)) {
					context.getVariableMap().put(Replacement.TMP_INSTANCE_NAME.name(), instanceName);
					context.getVariableMap().put(Replacement.METHOD_NAME.name(), m.getName());
					context.getImportSB().append(
							Common.genimportStr(context.getAlreadyImportClasses(), context.getVariableMap(), ccs));
					String paramVariables = Common.genUnitTestMatchersMethodParamsVariable(context.getVariableMap(),
							Common.getConcreteClass(m.getParameterTypes(), context));
					context.getVariableMap().put(Replacement.METHOD_PARAMS_VARIABLE.name(), paramVariables);
					String mockThrow = Common.replaceAllKeyWord(Syntax.MOCK_THROW, context.getVariableMap());
					Class rt = m.getReturnType();
					String returnValue = null;
					if (Common.isComplexType(rt)) {
						String typeName = m.getGenericReturnType().getTypeName();
						Class collectionGenericType = null;
						if (Common.isACollection(rt)) {
							String genericType = null;
							int start = typeName.indexOf("<");
							int end = typeName.indexOf(">");
							genericType = typeName.substring(start + 1, end);
							try {
								collectionGenericType = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner().loadClass(genericType, true);
								Common.importC(collectionGenericType, context);
							} catch (Exception e1) {
								// e1.printStackTrace();
							}
						}
						Common.importC(rt, context);
//						context.getVariableMap().put(Replacement.STATIC_MODIFIER.name(), "static");
						// MethodCallingVariable mc =
						// Common.genIfNotExistAndGetClassCreateMethod(rt, new
						// Class[]{collectionGenericType}, false,
						// complexTypeCreateMapping, createdMethodName,
						// classSimpleName, context.getVariableMap());
						Common.setModifier(Modifier.PUBLIC | Modifier.STATIC, context);
						MethodCallingVariable mc = Common.genIfNotExistAndGetClassCreateMethod(rt,
								new Class[] { collectionGenericType }, false, context);
						if (mc != null) {
							returnValue = Common.getInstanceName(rt, context.getSameTypeCreatedTimesMap());
							context.getVariableMap().put(Replacement.RETURN_TYPE.name(), rt.getSimpleName());
							context.getVariableMap().put(Replacement.RETURN_TYPE_INSTANCE.name(), returnValue);
							context.getVariableMap().put(Replacement.TMP_INSTANCE_NAME.name(),
									c.getSimpleName() + "Mock");
							context.getVariableMap().put(Replacement.METHOD_NAME.name(), mc.getMethodName());
							context.getVariableMap().put(Replacement.METHOD_PARAMS_VARIABLE.name(), "");
							String methodCallingStr = Common.replaceAllKeyWord(Syntax.METHOD_WITH_RETURN_CALLING,
									context.getVariableMap());
							mockMethodsLine.append(methodCallingStr);
						}
					} else {
						returnValue = Common.getDefaultVal(rt);
						returnValue = returnValue == null ? "" : returnValue;
					}
					context.getVariableMap().put(Replacement.TMP_INSTANCE_NAME.name(), instanceName);
					context.getVariableMap().put(Replacement.METHOD_RETURN_VALUE.name(), returnValue);
					context.getVariableMap().put(Replacement.METHOD_NAME.name(), m.getName());
					context.getVariableMap().put(Replacement.METHOD_PARAMS_VARIABLE.name(), paramVariables);
					String mockWithReturn = Common.replaceAllKeyWord(Syntax.MOCK_RETURN, context.getVariableMap());
					mockMethodsLine.append(mockThrow).append(mockWithReturn);
				} else {
					context.getVariableMap().put(Replacement.TMP_INSTANCE_NAME.name(), instanceName);
					context.getVariableMap().put(Replacement.METHOD_NAME.name(), m.getName());
					Common.importC(ccs, context);
					String paramVariables = Common.genUnitTestMatchersMethodParamsVariable(context.getVariableMap(),
							ccs);
					context.getVariableMap().put(Replacement.METHOD_PARAMS_VARIABLE.name(), paramVariables);
					String mockThrow = Common.replaceAllKeyWord(Syntax.MOCK_THROW, context.getVariableMap());
					String mockReturnVoid = Common.replaceAllKeyWord(Syntax.MOCK_RETURN_VOID, context.getVariableMap());
					mockMethodsLine.append(mockThrow).append(mockReturnVoid);
				}
			} else {
				// not support static
			}
		}
		if (!hasExceptionThrow) {
			sb.append(mockMethodsLine).append(Syntax.NEW_LINE);
		} else {
			context.getVariableMap().put(Replacement.TRY_BODY.name(), mockMethodsLine.toString());
			context.getVariableMap().put(Replacement.EXCEPTION_NAME.name(), Exception.class.getSimpleName());
			context.getVariableMap().put(Replacement.EXCEPTION_INSTANCE_NAME.name(), "e");
			String exceptionsDeclare = Common.replaceAllKeyWord(Syntax.EXCEPTIONS_DECLARE_LINE,
					context.getVariableMap());
			context.getVariableMap().put(Replacement.EXCEPTIONS_DECLARE.name(), exceptionsDeclare);
			context.getVariableMap().put(Replacement.CATCH_BODY.name(), "e.printStackTrace();");
			context.getVariableMap().put(Replacement.FINALLY_BODY.name(), "");
			String tryCatch = Common.replaceAllKeyWord(Syntax.TRY_CATCH_BLOCK, context.getVariableMap());
			sb.append(tryCatch).append(Syntax.NEW_LINE);
		}
		return sb.toString();
	}

	/*
	 * @Deprecated public void genMock(Class clzToTest, String targetDirectory)
	 * throws Exception { String strForFile = genMockClassSource(clzToTest); if
	 * (strForFile == null) { return; } File file = new File(targetDirectory +
	 * File.separator + clzToTest.getSimpleName() + "Mock.java"); FileOutputStream
	 * fos = new FileOutputStream(file); FileChannel fc = fos.getChannel();
	 * ByteBuffer bb =
	 * Common.utf8CharsetEncoder.encode(CharBuffer.wrap(strForFile)); fc.write(bb);
	 * fc.close(); fos.close(); }
	 */

	public void genAllInterfaceMockUnderPack(Class oneOfClass, String targetDirectory) throws Exception {
		Package sourcePack = oneOfClass.getPackage();
		if (sourcePack == null) {
			System.out.println("sourcePack is null");
			return;
		}
		File file = new File(targetDirectory);
		if (file.listFiles() != null && file.listFiles().length > 0) {
			System.out.println(
					String.format("targetDirectory %s is not empty, will skip the generating", targetDirectory));
			return;
		}
		if (!file.exists()) {
			file.mkdirs();
		}
		List<Class> list = new ArrayList<>();
		Common.getAllTopLevelClasses(oneOfClass, true, list);
		CodeGenContext context = CodeGenContext.getInstance();
		list.forEach(c -> {
			try {
				if (c.isInterface()) {
					genMock(c, targetDirectory, context);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void main(String[] args) {
		MockClzFactoryGenerator gen = new MockClzFactoryGenerator();
		try {
			Class rt = Object.class;
			Map<Class, Short> map = new HashMap<>();
			System.out.println(Modifier.PRIVATE | Modifier.STATIC);
			// System.out.println(gen.genMockClassSource(AbsClassForTest.class));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
