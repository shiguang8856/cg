package cg.unittest;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cg.Const;
import cg.Replacement;
import cg.Syntax;
import cg.complexobj.CodeGenContext;
import cg.util.Common;

public class UnitTestCodeGeneratorDependOnClass {
	MockClzFactoryGenerator im = new MockClzFactoryGenerator();
	int maxMethod = 10;
	ClzNeedMockDetermination cmd = new ClzNeedMockDetermination();

	public void init(Class<?> clzToTest, CodeGenContext context) {
		context.getVariableMap().put(Replacement.CLASS_NAME.name(), Common.getClassName(clzToTest, context));
		context.getVariableMap().put(Replacement.INSTANCE_NAME.name(), Common.getInstanceName(clzToTest));
	}

	public void genPackage(Class<?> clzToTest, CodeGenContext context) throws Exception {
		context.getVariableMap().put(Replacement.PACKAGE_NAME.name(), clzToTest.getPackage().getName());
		String packageStr = Common.replaceAllKeyWord(Syntax.PACKAGE_DECLARE, context.getVariableMap());
		context.getPackageSB().append(packageStr);
	}

	private void importSimple(String qualifier, CodeGenContext context) throws Exception {
		Class<?> c = null;
		try {
			c = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner().loadClass(qualifier, true);
		} catch (Exception e) {
		}
		if (c != null)
			Common.importC(c, context);
	}

	public void genImport(Class<?> clzToTest, CodeGenContext context) throws Exception {
		Common.importC(clzToTest, context);
		Common.importPack("org.junit.*", context.getVariableMap(), context.getImportSB());
		Common.importPack("org.mockito.*", context.getVariableMap(), context.getImportSB());
	}

	public void determineGenTestParam(Class<?> clzToTest, GenTestParam gtp, CodeGenContext context) throws Exception {
		if (gtp == null) {
			gtp = new GenTestParam();
		}
		this.determineFields(clzToTest, gtp, context);
		this.determineMockClzFactoryCreateParams(clzToTest, gtp, context);
		this.determineMockClzCreateParams(clzToTest, gtp, context);
		this.determineMethodCaseCreateParams(clzToTest, gtp, context);
	}

	public void determineMockClzCreateParams(Class<?> clzToTest, GenTestParam gtp, CodeGenContext context)
			throws Exception {
		List<MockClzCreateParams> list = new ArrayList<>();

		List<FieldInitParams> fipList = gtp.getOneTestClassParamByClzEvenNotPresent(clzToTest).getFieldInitParamList();
		for (FieldInitParams fip : fipList) {
			Field f = fip.getField();
			Class<?> fType = f.getType();
			if (Common.isFinal(f) || f.getName().contains("$") || this.canIgnore(fType))
				continue;
			if (fip.isComplexType()) {
				Common.importC(fType, context);
				boolean isTooManyMethods = fip.isTooManyMethods();
				boolean needMockFactroy = fip.isNeedMockFactroyClz() || isTooManyMethods;

				boolean needMock = needMockFactroy ? cmd.needMock(fip.getFieldType()) : true;
				fip.setNeedMock(needMock);
				if (needMock) {
					MockClzCreateParams mcp = new MockClzCreateParams();
					mcp.setClzBeMock(clzToTest);
					list.add(mcp);
				}
			}
		}
	}

	public void determineMockClzFactoryCreateParams(Class<?> clzToTest, GenTestParam gtp, CodeGenContext context)
			throws Exception {
		List<MockClzFactoryCreateParam> list = gtp.getMockClzFactoryCreateParamList();
		List<FieldInitParams> fipList = gtp.getOneTestClassParamByClzEvenNotPresent(clzToTest).getFieldInitParamList();
		for (FieldInitParams fip : fipList) {
			Field f = fip.getField();
			Class<?> fType = f.getType();
			if (Common.isFinal(f) || f.getName().contains("$") || this.canIgnore(fType))
				continue;
			if (fip.isComplexType()) {
				fip.setNeedMock(cmd.needMock(fip.getFieldType()));
				boolean isTooManyMethods = fip.isTooManyMethods();
				boolean needMockFactroyClz = isTooManyMethods || fip.getFieldType().isInterface();
				fip.setNeedMockFactroyClz(needMockFactroyClz);
				if (needMockFactroyClz) {
					MockClzFactoryCreateParam mccp = new MockClzFactoryCreateParam();
					if (!list.stream().anyMatch(e -> {
						return e.getClzBeMock().equals(fType);
					})) {
						mccp.setClzBeMock(fType);
						mccp.setFactoryMethodName("create" + fType.getSimpleName());
						mccp.setTargetDir(gtp.getTargetDir() + File.separator + "mock");
						list.add(mccp);
					}
				}
			} else {
				fip.setNeedMock(false);
				fip.setNeedMockFactroyClz(false);
			}
		}
		// for all method params and return type, check if need to create a mock class
		// factory
	}

	public void determineFields(Class<?> clzToTest, GenTestParam gtp, CodeGenContext context) throws Exception {
		List<FieldInitParams> list = new ArrayList<>();
		List<String> concernedPackageNames = Common.getDefaultConcernedPacks(clzToTest);
		Common.getAllFields(clzToTest, concernedPackageNames, context.getAllFieldList());
		Field fa[] = context.getAllFieldList().toArray(new Field[0]);
		for (Field f : fa) {
			Class<?> fType = f.getType();
			if (Common.isFinal(f) || f.getName().contains("$") || canIgnore(fType))
				continue;
			FieldInitParams fieldInitParams = new FieldInitParams();
			boolean isTooManyMethods = Common.getAllDeclaredPublicMethodsForTest(fType).length > maxMethod ? true
					: false;
			fieldInitParams.setTooManyMethods(isTooManyMethods);
			fieldInitParams.setField(f);
//			fieldInitParams.setDefaultVal("");
			fieldInitParams.setMod(Modifier.PRIVATE);
			fieldInitParams.setFieldType(fType);
			fieldInitParams.setFieldName(Common.getInstanceName(fType));
			fieldInitParams.setComplexType(Common.isComplexType(fType));
			list.add(fieldInitParams);
		}
		gtp.getOneTestClassParamByClzEvenNotPresent(clzToTest).getFieldInitParamList().addAll(list);
	}

	public void determineMethodCaseCreateParams(Class<?> clzToTest, GenTestParam gtp, CodeGenContext context)
			throws Exception {
		Method ms[] = Common.getAllDeclaredPublicMethodsForTest(clzToTest);
		for (Method m : ms) {
			determineOneMethod(clzToTest, gtp, m, context);
		}
	}

	public void determineOneMethod(Class<?> clzToTest, GenTestParam gtp, Method m, CodeGenContext context) {
		MethodCaseCreateParam methodCaseCreateParam = new MethodCaseCreateParam();
		methodCaseCreateParam.setMod(Modifier.PUBLIC);
		methodCaseCreateParam.setMethodForTest(m);
		methodCaseCreateParam.setReturnType(m.getReturnType());
//		String testMethodName = Common.makeFirstCharInUpperCase("test" + m.getName());
		String testMethodName = Common.makeFirstCharInUpperCase(m.getName());
		methodCaseCreateParam.setMethodName(Common.getMethodName(testMethodName, context));
		gtp.getOneTestClassParamByClzEvenNotPresent(clzToTest).getMethodCaseCreateParamList()
				.add(methodCaseCreateParam);
		Class<?> expClasses[] = m.getExceptionTypes();
		String expCaseNameFormat = "test%sWhen%s";
		for (Class<?> exp : expClasses) {
			MethodCaseCreateParam methodCaseCreateParam1 = new MethodCaseCreateParam();
			methodCaseCreateParam1.setMod(Modifier.PUBLIC);
			methodCaseCreateParam1.setReturnType(m.getReturnType());
			methodCaseCreateParam1.setMethodName(
					Common.getMethodName(String.format(expCaseNameFormat, m.getName(), exp.getSimpleName()), context));
			methodCaseCreateParam1.setExpected(exp);
			methodCaseCreateParam1.setMethodForTest(m);
			methodCaseCreateParam1.setThrowExp(exp);
			gtp.getOneTestClassParamByClzEvenNotPresent(clzToTest).getMethodCaseCreateParamList()
					.add(methodCaseCreateParam1);
		}
	}

	public void genFields(Class<?> clzToTest, GenTestParam gtp, CodeGenContext context) throws Exception {
		List<FieldInitParams> fipList = gtp.getOneTestClassParamByClzEvenNotPresent(clzToTest).getFieldInitParamList();
		for (FieldInitParams fip : fipList) {
			Field f = fip.getField();
			Class<?> fType = fip.getFieldType();
			if (Common.isFinal(f) || f.getName().contains("$") || canIgnore(fType))
				continue;
			String fieldTypeName = fType.getName();
			importSimple(fieldTypeName, context);
			String fieldTypeSimpleName = fType.getSimpleName();
			context.getVariableMap().put(Replacement.FIELD_CLASS_NAME.name(), fieldTypeSimpleName);
			context.getVariableMap().put(Replacement.FIELD_INSTANCE_NAME.name(), f.getName());
			Common.setModifier(Modifier.PRIVATE, context);
			String fieldStr = Common.replaceAllKeyWord(Syntax.FIELD_DECLARE, context.getVariableMap());
			context.getFieldsSB().append(fieldStr);
		}
		String fieldTypeSimpleName = clzToTest.getSimpleName();
		context.getVariableMap().put(Replacement.FIELD_CLASS_NAME.name(), fieldTypeSimpleName);
		context.getVariableMap().put(Replacement.FIELD_INSTANCE_NAME.name(), Common.getInstanceName(clzToTest));
		Common.setModifier(Modifier.PRIVATE, context);
		String fieldStr = Common.replaceAllKeyWord(Syntax.FIELD_DECLARE, context.getVariableMap());
		context.getFieldsSB().append(fieldStr);
		if (fipList.size() > 0) {
			Common.importC(Field.class, context);
			String setTargetObjFieldMethodStr = Common.replaceAllKeyWord(Syntax.UNIT_TEST_SET_TARGET_OBJ_FIELD_METHOD,
					context.getVariableMap());
			String getFieldMethodStr = Common.replaceAllKeyWord(Syntax.UNIT_TEST_GET_FIELD_METHOD,
					context.getVariableMap());
			context.getAddtionalMethods().append(Syntax.NEW_LINE).append(setTargetObjFieldMethodStr);
			context.getAddtionalMethods().append(getFieldMethodStr);
		}
	}

	private boolean canIgnore(Class<?> fieldType) {
		boolean b = false;
		if (fieldType.isAssignableFrom(Logger.class)) {
			b = true;
		}
		if (fieldType.isAssignableFrom(org.slf4j.Logger.class)) {
			b = true;
		}
		if ("logger".equalsIgnoreCase(fieldType.getSimpleName())) {
			b = true;
		}
		return b;
	}

	public void genSetup(Class<?> clzToTest, GenTestParam gtp, CodeGenContext context) throws Exception {
		List<FieldInitParams> fipList = gtp.getOneTestClassParamByClzEvenNotPresent(clzToTest).getFieldInitParamList();
		String setupStr = Common.replaceAllKeyWord(Syntax.NORMAL_SETUP_BEGIN_LINE, context.getVariableMap());
		context.getSetupSB().append(setupStr);
		context.getSetupSB().append(Common.initAClass(clzToTest, context));
		String clzToTestInstanceName = Common.getInstanceName(clzToTest);
		// Field fa[] = context.getAllFieldList().toArray(new Field[0]);
		for (FieldInitParams fip : fipList) {
			Field f = fip.getField();
			Class<?> fType = f.getType();
			if (Common.isFinal(f) || f.getName().contains("$") || this.canIgnore(fType))
				continue;
			if (fip.isNeedMockFactroyClz()) {
				MockClzFactoryCreateResult mccr = gtp.getMockClzFactoryCreateResult(fType);
				if (mccr != null) {
					context.getVariableMap().put(Replacement.RETURN_TYPE.name(), "");
					context.getVariableMap().put(Replacement.RETURN_TYPE_INSTANCE.name(), f.getName());
					context.getVariableMap().put(Replacement.TMP_INSTANCE_NAME.name(), Common.getDefaultVal(fType));
					context.getVariableMap().put(Replacement.METHOD_NAME.name(), mccr.getFactoryMethodName());
					context.getVariableMap().put(Replacement.METHOD_PARAMS_VARIABLE.name(), "");
					String mockClzFactoryCallLine = Common.replaceAllKeyWord(Syntax.METHOD_WITH_RETURN_CALLING,
							context.getVariableMap());
					context.getSetupSB().append(mockClzFactoryCallLine);
				} else {
					throw new Exception(String.format("cannot get MockClzFactoryCreateResult for mockClz %s", fType));
				}
			} else if (fip.isNeedMock()) {
				tryRegistryMockConcreteClass(fType, context);
				Common.importC(fType, context);
				// context.getVariableMap().put(Replacement.TMP_CLASS_NAME.name(),
				// Common.getInstanceName(clzToTest));
				context.getVariableMap().put(Replacement.TMP_CLASS_NAME.name(), fType.getSimpleName());
//				context.getVariableMap().put(Replacement.TMP_INSTANCE_NAME.name(), f.getName());
				String mockLine = Common.replaceAllKeyWord(Syntax.MOCK_CLASS_LINE, context.getVariableMap());
				context.getSetupSB().append(mockLine);
			}
			boolean isPrimitiveType = Common.isPrimitiveType(fType);
			if (isPrimitiveType) {
				context.getVariableMap().put(Replacement.TMP_CLASS_NAME.name(), "");
//				context.getVariableMap().put(Replacement.TMP_INSTANCE_NAME.name(), f.getName());
				context.getVariableMap().put(Replacement.DEFAULT_VALUE.name(), Common.getDefaultVal(fType));
				context.getVariableMap().put(Replacement.SEPERATOR.name(), "");
				String fieldAssign = Common.replaceAllKeyWord(Syntax.INIT_CLASS_WITH_DEFAULT_VALUE,
						context.getVariableMap());
				context.getSetupSB().append(fieldAssign);
			}
			context.getVariableMap().put(Replacement.TMP_INSTANCE_NAME.name(), clzToTestInstanceName);
			context.getVariableMap().put(Replacement.FIELD_INSTANCE_NAME.name(), f.getName());
			context.getVariableMap().put(Replacement.FIELD_VALUE.name(), f.getName());
			String setFieldMethodStr = Common.replaceAllKeyWord(Syntax.UNIT_TEST_SET_TARGET_OBJ_FIELD_CALLING,
					context.getVariableMap());
			context.getSetupSB().append(setFieldMethodStr);
		}
		context.getSetupSB().append(Syntax.METHOD_END_LINE);
	}

	public void tryRegistryMockConcreteClass(Class<?> c, CodeGenContext context) {
		String mockClassName = Common.getDefaultMockClassName(c, context);
		try {
			Class<?> mockC = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner().loadClass(mockClassName, true);
			String defaultCreateMethodName = Common.getDefaultCreateMethodName(c);
			Method ms[] = Common.getAllDeclaredPublicMethodsForTest(mockC);
			for (Method m : ms) {
				if (m.getName().equals(defaultCreateMethodName) && m.getParameterCount() == 0) {
					context.registerAbstractConcreteClassMappingSimple(c, mockC, defaultCreateMethodName);
					break;
				}
			}
		} catch (ClassNotFoundException e) {
		}
	}

	public StringBuilder genOneParamObject(Class<?> paramClz) {
		return null;
	}

	@SuppressWarnings("unused")
	private static String getEnumCreateStr(Class<?> c) {
		StringBuilder sb = new StringBuilder();
		String classNameForPrint = c.getName().replaceAll("\\$", "\\.");
		Object obj[] = c.getEnumConstants();
		sb.append(classNameForPrint).append(".").append(obj[0].toString());
		return sb.toString();
	}

	public String getComplexTypeCreateMethodName(Class<?> target, CodeGenContext context) {
		String methodName = "create" + target.getSimpleName();
		int sameMethodNameCound = 0;
		for (String name : context.getCreatedMethodName()) {
			Pattern p = Pattern.compile(methodName + "(\\d*)");
			Matcher m = p.matcher(name);
			if (m.find()) {
				String methodNameIndex = m.group(1);
				if (methodNameIndex != null && !"".equals(methodNameIndex.trim())) {
					try {
						sameMethodNameCound = Integer.valueOf(methodNameIndex);
					} catch (Exception e) {
						throw e;
					}
				}
				sameMethodNameCound++;
			}
		}
		if (sameMethodNameCound > 0)
			methodName = methodName + sameMethodNameCound;
		return methodName;
	}

	public void genAllTestCase(GenTestParam gtp, Class<?> clzToTest, CodeGenContext context) throws Exception {
		List<MethodCaseCreateParam> mcpList = gtp.getOneTestClassParamByClzEvenNotPresent(clzToTest)
				.getMethodCaseCreateParamList();
		for (MethodCaseCreateParam mcp : mcpList) {
			context.getAllTestCaseSB().append(genOneTestCase(clzToTest, mcp, context));
		}
	}

	public String genOneTestCase(Class<?> clzToTest, MethodCaseCreateParam mcc, CodeGenContext context)
			throws Exception {
		boolean isStatic = Common.isStatic(mcc.getMethodForTest());
		String clzInstanceName = Common.getInstanceName(clzToTest);
		context.getVariableMap().put(Replacement.METHOD_NAME.name(), mcc.getMethodName());
		StringBuilder sb = new StringBuilder();
		StringBuilder caseBodyExceptTryCatch = new StringBuilder();
		sb.append(Syntax.NEW_LINE);
		String caseBeginStr = Common.replaceAllKeyWord(Syntax.NORMAL_TEST_CASE_BEGIN_LINE, context.getVariableMap());
		sb.append(caseBeginStr).append(Syntax.NEW_LINE);
		Class<?> returnType = mcc.getReturnType();
		// TODO if returnType is an array, it will cause an error
		/** begin of method calling replacement **/
		Parameter[] ps = mcc.getMethodForTest().getParameters();
		Class<?> paramClasses[] = mcc.getMethodForTest().getParameterTypes();
		StringBuilder methodParamVariables = new StringBuilder();
		for (Parameter p : ps) {
			Class<?> c = p.getType();
			if (c != null) {
				String name = p.getName();
				Common.importC(c, context);
				// if p is a complextype, then call setcodegen.generate,
				// otherwise
				// create a instance line of
				String value = "null";
				if (!Common.isComplexType(c)) {
					value = Common.getDefaultVal(c);
				} else {
					Class<?> collectionGenericType = null;
					if (Common.isACollection(c)) {
						String genericType = null;
						String typeName = null;
						typeName = p.getParameterizedType().getTypeName();
						int start = typeName.indexOf("<");
						int end = typeName.indexOf(">");
						genericType = typeName.substring(start + 1, end);
						try {
							collectionGenericType = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner().loadClass(genericType, true);
							Common.importC(collectionGenericType, context);
						} catch (Exception e1) {
						}
					}
					MethodCallingVariable mc = Common.genIfNotExistAndGetClassCreateMethod(c,
							new Class[] { collectionGenericType }, false, context);
					if (mc != null)
						value = mc.getMethodName() + "()";
				}
				context.getVariableMap().put(Replacement.TMP_CLASS_NAME.name(), Common.getClassName(c, context));
				context.getVariableMap().put(Replacement.TMP_INSTANCE_NAME.name(), name);
				context.getVariableMap().put(Replacement.TMP_INSTANCE_VALUE.name(), value);
				String paramAssignment = Common.replaceAllKeyWord(Syntax.ASSIGN_LINE, context.getVariableMap());
				caseBodyExceptTryCatch.append(Syntax.NEW_LINE).append(paramAssignment);
				methodParamVariables.append(name).append(", ");
			} else {
				System.out.println(String.format("p type %s is null", p));
			}
		}

		if (methodParamVariables.length() > 0) {
			methodParamVariables.delete(methodParamVariables.length() - 2, methodParamVariables.length());
		}
		String returnTypeInstanceName = Common.getInstanceName(returnType);
		boolean isVoidMethod = Common.isVoid(mcc.getMethodForTest());
		if (!isVoidMethod) {
			context.getVariableMap().put(Replacement.RETURN_TYPE.name(), Common.getClassName(returnType, context));
			context.getVariableMap().put(Replacement.RETURN_TYPE_INSTANCE.name(), returnTypeInstanceName);
		}
		String tempInstanceName = !isStatic ? clzInstanceName : clzToTest.getSimpleName();
		context.getVariableMap().put(Replacement.TMP_INSTANCE_NAME.name(), tempInstanceName);
		context.getVariableMap().put(Replacement.METHOD_NAME.name(), mcc.getMethodForTest().getName());
		context.getVariableMap().put(Replacement.METHOD_PARAMS_VARIABLE.name(), methodParamVariables.toString());
		String methodCallingStr = Common.replaceAllKeyWord(
				isVoidMethod ? Syntax.VOID_METHOD_CALLING : Syntax.METHOD_WITH_RETURN_CALLING,
				context.getVariableMap());
		caseBodyExceptTryCatch.append(Syntax.NEW_LINE).append(methodCallingStr);
		/** end of method calling replacement **/
		if (!isVoidMethod) {
			if (!returnType.isPrimitive()) {
				context.getVariableMap().put(Replacement.TMP_INSTANCE_NAME.name(), returnTypeInstanceName);
				String assertNotNull = Common.replaceAllKeyWord(Syntax.ASSERT_NOT_NULL, context.getVariableMap());
				caseBodyExceptTryCatch.append(Syntax.NEW_LINE).append(assertNotNull);
			}
			String expectedReturnTypeInstanceName = Common.getInstanceName(returnType, "expected");
			String value = null;
			if (!Common.isComplexType(returnType)) {
				value = Common.getDefaultVal(returnType);
			} else {
				// TODO comment right now.
				/*
				 * MethodCallingVariable mc =
				 * Common.genIfNotExistAndGetClassCreateMethod(returnType, true, context); value
				 * = mc.getMethodName() + "()";
				 */
			}
			context.getVariableMap().put(Replacement.TMP_CLASS_NAME.name(), Common.getClassName(returnType, context));
			context.getVariableMap().put(Replacement.TMP_INSTANCE_NAME.name(), expectedReturnTypeInstanceName);
			context.getVariableMap().put(Replacement.TMP_INSTANCE_VALUE.name(), value);
			String expectedReturnTypeAssign = Common.replaceAllKeyWord(Syntax.ASSIGN_LINE, context.getVariableMap());
			// caseBodyExceptTryCatch.append(Syntax.NEW_LINE).append(expectedReturnTypeAssign);
			context.getVariableMap().put(Replacement.EXPECT_VALUE.name(), expectedReturnTypeInstanceName);
			context.getVariableMap().put(Replacement.ACTUAL_VALUE.name(), returnTypeInstanceName);
			String assertEqual = Common.replaceAllKeyWord(Syntax.ASSERT_EQUAL, context.getVariableMap());
			// caseBodyExceptTryCatch.append(Syntax.NEW_LINE).append(assertEqual);
			Common.importC(returnType, context);
		}
		context.getVariableMap().put(Replacement.INSTANCE_NAME.name(), clzInstanceName);
		context.getVariableMap().put(Replacement.METHOD_NAME.name(), mcc.getMethodName());
		// TODO
		StringBuilder methodMatchVariables = new StringBuilder();
		for (int i = 0; i < ps.length; i++) {
			String anyMatch = Common.replaceAllKeyWord(Syntax.ANY_MATCH, context.getVariableMap());
			methodMatchVariables.append(anyMatch).append(", ");
		}
		if (methodMatchVariables.length() > 0) {
			methodMatchVariables.delete(methodMatchVariables.length() - 2, methodMatchVariables.length());
		}
		context.getVariableMap().put(Replacement.MATCH_PARAMS_VARIABLE.name(), methodMatchVariables.toString());
		String mockitoVerify = Common.replaceAllKeyWord(Syntax.MOCKITO_VERIFY, context.getVariableMap());
		// caseBodyExceptTryCatch.append(Syntax.NEW_LINE).append(mockitoVerify);
		Class<?> expClasses[] = mcc.getMethodForTest().getExceptionTypes();
		if (expClasses == null || expClasses.length == 0) {
			sb.append(caseBodyExceptTryCatch);
		} else {
			String names = Stream.of(expClasses).map(e -> {
				return e.getSimpleName();
			}).collect(Collectors.joining(" | "));
			// StringBuilder eb = new StringBuilder();
			for (Class<?> e : expClasses) {
				// eb.append(e.getSimpleName()).append(" | ");
				Common.importC(e, context);
			}
			// eb.delete(eb.length() - 2, sb.length());
			context.getVariableMap().put(Replacement.EXCEPTION_NAME.name(), names);
			context.getVariableMap().put(Replacement.EXCEPTION_INSTANCE_NAME.name(), "e");
			String exceptionsDeclare = Common.replaceAllKeyWord(Syntax.EXCEPTIONS_DECLARE_LINE,
					context.getVariableMap());
			context.getVariableMap().put(Replacement.TRY_BODY.name(), caseBodyExceptTryCatch.toString());
			String catchBody = Common.replaceAllKeyWord(Syntax.ASSERT_FAIL, context.getVariableMap());
			context.getVariableMap().put(Replacement.CATCH_BODY.name(), catchBody);
			context.getVariableMap().put(Replacement.FINALLY_BODY.name(), "");
			context.getVariableMap().put(Replacement.EXCEPTIONS_DECLARE.name(), exceptionsDeclare);
			String tryCatchBlock = Common.replaceAllKeyWord(Syntax.TRY_CATCH_BLOCK, context.getVariableMap());
			sb.append(tryCatchBlock);
		}
		sb.append(Syntax.NEW_LINE).append(Syntax.CLASS_END_LINE).append(Syntax.NEW_LINE);
		return sb.toString();
	}

	private String genClassBegin(Class<?> clzToTest, CodeGenContext context) throws Exception {
		String className = Common.getClassName(clzToTest, context) + "Test";
		Common.setModifier(Modifier.PUBLIC, context);
		context.getVariableMap().put(Replacement.TMP_CLASS_NAME.name(), className);
		String beginstr = Common.replaceAllKeyWord(Syntax.CLASS_BEGIN_LINE, context.getVariableMap());
		return beginstr;
	}

	public OneTestClassCreateResult genAllTest(Class<?> clzToTest, GenTestParam gtp, CodeGenContext context)
			throws Exception {
		if (clzToTest.isInterface() || clzToTest.getEnclosingClass() != null) {
			// TODO for inner class
			return null;
		}
		if (clzToTest.isEnum()) {
			return null;
		}
		OneTestClassCreateResult oneTestClassCreateResult = new OneTestClassCreateResult();
		oneTestClassCreateResult.setClzForTest(clzToTest);

		this.init(clzToTest, context);
		context.getVariableMap().put(Const.CLASS_SIMPLE_NAME_KEY, clzToTest.getSimpleName());
		context.getVariableMap().put(Const.CLASS_NAME_KEY, clzToTest.getName());
		StringBuilder sb = new StringBuilder();
//		Method ms[] = Common.getAllDeclaredPublicMethodsForTest(clzToTest);
		this.determineFields(clzToTest, gtp, context);
		this.determineMockClzFactoryCreateParams(clzToTest, gtp, context);
		this.determineMockClzCreateParams(clzToTest, gtp, context);
		List<MockClzFactoryCreateParam> mcfcpList = gtp.getMockClzFactoryCreateParamList();
		for (MockClzFactoryCreateParam mcfcp : mcfcpList) {
			MockClzFactoryCreateResult mcfcr = im.genMock(mcfcp.getClzBeMock(), mcfcp.getTargetDir(), context);
			if (mcfcr.getFile() == null) {
				throw new Exception(String.format("mock class %s file not created", mcfcp.getClzBeMock()));
			} else {
				gtp.getMockClzFactoryCreateResult().add(mcfcr);
			}
		}
		this.determineMethodCaseCreateParams(clzToTest, gtp, context);
		genPackage(clzToTest, context);
		genImport(clzToTest, context);
		List<MethodCaseCreateParam> list = gtp.getOneTestClassParamByClzEvenNotPresent(clzToTest)
				.getMethodCaseCreateParamList();
		gtp.getOneTestClassParamByClzEvenNotPresent(clzToTest).getFieldInitParamList();
		genFields(clzToTest, gtp, context);
		genSetup(clzToTest, gtp, context);
		for (MethodCaseCreateParam mcp : list) {
			String oneTestCase = genOneTestCase(clzToTest, mcp, context);
			context.getAllTestCaseSB().append(oneTestCase);
		}
		sb.append(context.getPackageSB());
		sb.append(context.getImportSB());
		sb.append(genClassBegin(clzToTest, context));
		sb.append(context.getFieldsSB());
		sb.append(context.getSetupSB());
		sb.append(context.getAddtionalMethods());
		sb.append(context.getAllTestCaseSB());
		sb.append(context.getCreatedComplexTypeMethod());
		sb.append(Syntax.CLASS_END_LINE);
		context.clearExceptGlobalParam();
		oneTestClassCreateResult.setCode(sb.toString());
		gtp.getOneTestClassParamByClz(clzToTest).setOneTestClassCreateResult(oneTestClassCreateResult);
		File dir = Common.getFileByPackage(gtp.getTargetDir(), clzToTest.getPackage().getName());
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		File file = new File(dir.getAbsolutePath() + File.separator + clzToTest.getSimpleName() + "Test.java");
		Common.write(file, sb.toString());
		oneTestClassCreateResult.setFile(file);
		return oneTestClassCreateResult;
	}

	public void genAllTestCasesAndSave(Class<?> clzToTest, GenTestParam gtp, CodeGenContext context) throws Exception {

		File file = new File(gtp.getTargetDir());
		boolean isMockClzFactoryCreated = !gtp.getMockClzFactoryCreateResult().isEmpty();
		boolean isTestClzFileCreated = gtp.getOneTestClassParamByClz(clzToTest) != null
				&& gtp.getOneTestClassParamByClz(clzToTest).getOneTestClassCreateResult() != null ? true : false;
		if (!isMockClzFactoryCreated && !isTestClzFileCreated && !gtp.getMockClzFactoryCreateResult().isEmpty()
				&& file.listFiles() != null && file.listFiles().length > 0) {
			System.out.println(
					String.format("targetDirectory %s is not empty, will skip the generating", gtp.getTargetDir()));
			return;
		}
		if (!file.exists()) {
			file.mkdirs();
		}
		OneTestClassCreateResult oneTestClassCreateResult = genAllTest(clzToTest, gtp, context);
		if (oneTestClassCreateResult == null) {
			return;
		}

	}

	@SuppressWarnings("rawtypes")
	public void genAllTestUnderPack(Class<?> oneOfClass, GenTestParam gtp, CodeGenContext context) throws Exception {
		Package sourcePack = oneOfClass.getPackage();
		if (sourcePack == null) {
			System.out.println("sourcePack is null");
			return;
		}
		File file = new File(gtp.getTargetDir());
		if (file.listFiles() != null && file.listFiles().length > 0) {
			System.out.println(
					String.format("targetDirectory %s is not empty, will skip the generating", gtp.getTargetDir()));
			return;
		}
		if (!file.exists()) {
			file.mkdirs();
		}
		List<Class> list = new ArrayList<>();
		Common.getAllTopLevelClasses(oneOfClass, true, list);
		list.forEach(c -> {
			try {
				genAllTestCasesAndSave(c, gtp, context);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void main(String[] args) {
		UnitTestCodeGeneratorDependOnClass app = new UnitTestCodeGeneratorDependOnClass();
		try {
			Class<?> c = ClassLoader.getSystemClassLoader().loadClass("classfortest.Test1");
			GenTestParam gtp = new GenTestParam();
			gtp.setTargetDir("d:\\test1\\test2");
			app.genAllTestUnderPack(c, gtp, CodeGenContext.newInstance());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * Class c = ClassToTest.class; try { app.genAllTest(c, "c:\\"); } catch
		 * (Exception e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 */
	}
}
