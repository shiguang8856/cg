package cg.complexobj;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cg.AbstractConcreteClassMapping;
import cg.MethodMatcher;
import cg.unittest.MethodCallingVariable;
import cg.util.Common;
import cg.util.MethodCallingKey;
import qj.util.lang.ReloadableDynamicClassLoaderOwner;

public class CodeGenContext {
	public byte[] appBytes;
	boolean genFullQualifierName;
	private static String mavenHome = System.getProperty("mavenHome");
	private static String localRepoDir = System.getProperty("repositoryPath");

	File pomFile;
	List<String> jrePrimitiveTypes;
	List<String> jreClasses;
	ReloadableDynamicClassLoaderOwner reloadableDynamicClassLoaderOwner;
//	static DynamicClassLoader ucl;
	StringBuilder packageSB;
	StringBuilder importSB;
	StringBuilder fieldsSB;
	StringBuilder setupSB;
	StringBuilder allTestCaseSB;
	StringBuilder createdComplexTypeMethod;
	StringBuilder addtionalMethods;
	List<String> createdMethodName;
	Map<String, String> variableMap;
	List<Class> alreadyImportClasses;
	Map<Class, String> classSimpleNameMap;
	Map<Class<?>, Short> sameTypeCreatedTimesMap;
	Map<MethodCallingKey, MethodCallingVariable> complexTypeCreateMapping;
	public List<Field> allFieldList;

	/** global param **/
	private Map<Class<?>, AbstractConcreteClassMapping> abstractConcreteClassMap;
	Map<Class, MethodMatcher> methodMatchers;
	List<URL> loadedJarList = new ArrayList<>();
//	List<String> loadedClassesName = new ArrayList<>();

	List<Class> attachedClzList;
	private static CodeGenContext codeGenContext;
	private int clipBoardMonitorServerPort;
	
	//enable memory areas
	private static List<String> memoryAreas;

	Map<Class<?>, Object> uiModelMap;
//	List<URL> templateJarList = new ArrayList<>();

	private Map<String, Object> paramPossibleValsMap;
	
	public CodeGenContext() {
		init();
	}

	public static CodeGenContext getInstance() {
		if (codeGenContext == null) {
			return newInstance();
		} else {
			return codeGenContext;
		}
	}

	public static CodeGenContext newInstance() {
		CodeGenContext context = new CodeGenContext();
		context.init();
		codeGenContext = context;
		return context;
	}

	public void init() {
		packageSB = new StringBuilder();
		importSB = new StringBuilder();
		fieldsSB = new StringBuilder();
		setupSB = new StringBuilder();
		allTestCaseSB = new StringBuilder();
		createdComplexTypeMethod = new StringBuilder();
		addtionalMethods = new StringBuilder();
		createdMethodName = new ArrayList<>();
		variableMap = Common.createVariableMap();
		alreadyImportClasses = new ArrayList<Class>();
		classSimpleNameMap = new HashMap<>();
		complexTypeCreateMapping = new HashMap<>();
		allFieldList = new ArrayList<>();
		abstractConcreteClassMap = new HashMap<>();
		methodMatchers = new HashMap<>();
		sameTypeCreatedTimesMap = new HashMap<>();
		attachedClzList = new ArrayList<Class>();
		reloadableDynamicClassLoaderOwner = new ReloadableDynamicClassLoaderOwner();
		jreClasses = new ArrayList<>();
		jrePrimitiveTypes = Arrays.asList("int", "boolean", "long", "double", "short","float","byte","char");
		uiModelMap = new HashMap<>();
		paramPossibleValsMap = new HashMap<String, Object>();
		memoryAreas =new ArrayList<>();
//		extractJREClasses();
	}

	public void clearExceptGlobalParam() {
		packageSB.delete(0, packageSB.length());
		importSB.delete(0, importSB.length());
		fieldsSB.delete(0, fieldsSB.length());
		setupSB.delete(0, setupSB.length());
		allTestCaseSB.delete(0, allTestCaseSB.length());
		createdComplexTypeMethod.delete(0, createdComplexTypeMethod.length());
		addtionalMethods.delete(0, addtionalMethods.length());
		createdMethodName.clear();
		variableMap = Common.createVariableMap();
		alreadyImportClasses.clear();
		classSimpleNameMap.clear();
		complexTypeCreateMapping.clear();
		allFieldList.clear();
		methodMatchers.clear();
		sameTypeCreatedTimesMap.clear();
		paramPossibleValsMap.clear();
//		uiModelMap.clear();
	}

	public void clear() {
		clearExceptGlobalParam();
		/** global param **/
		abstractConcreteClassMap.clear();
		methodMatchers.clear();
		attachedClzList.clear();
	}
	public void extractJREClassesByJavaVerbose() {
		try {
			Process p = java.lang.Runtime.getRuntime().exec("java -verbose --show-version");
			StringBuilder sb = new StringBuilder();
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = null;
				while ((line = in.readLine()) != null) {
					sb.append(line).append("\r\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			} finally {
				if (in != null)
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			String output = sb.toString();
			Pattern pattern = Pattern.compile("\\[class,load\\]\\s*([\\w\\.\\$]+)[ ]+source:");
			Matcher matcher = pattern.matcher(output);
			while(matcher.find()) {
				String className = matcher.group(1);
//				className = className.replaceAll("\\$", ".");
				try {
					Class c = Class.forName(className, false, this.getClass().getClassLoader());
					if(null==c.getCanonicalName()) {
						System.out.print(c);
					}else {
						jreClasses.add(c.getCanonicalName());
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.print(jreClasses.size());
	}
//	public void extractJREClasses() {
//		
//		System.out.print(jreClasses.size());
//	}

	public void renewSameTypeCreatedTimesMap() {
		sameTypeCreatedTimesMap.clear();
	}

	public void registerAbstractConcreteClassMappingSimple(Class<?> abstractClass, Class<?> concreteClass) {
		registerAbstractConcreteClassMappingSimple(abstractClass, concreteClass, (Method) null, null);
	}

	public void registerAbstractConcreteClassMappingSimple(Class<?> abstractClass, Class<?> concreteClass,
			String factoryMethodName) {
		registerAbstractConcreteClassMappingSimple(abstractClass, concreteClass, factoryMethodName, null);
	}

	public void registerAbstractConcreteClassMappingSimple(Class<?> abstractClass, Class<?> concreteClass,
			String factoryMethodName, String methodParamVariables[]) {
		Method ms[] = Common.getAllDeclaredPublicMethodsForTest(concreteClass);
		for (Method m : ms) {
			if (m.getName().equals(factoryMethodName)) {
				registerAbstractConcreteClassMappingSimple(abstractClass, concreteClass, m, methodParamVariables);
				break;
			}
		}
	}

	public void registerAbstractConcreteClassMappingSimple(Class<?> abstractClass, Class<?> concreteClass,
			Method factoryMethod, String methodParamVariables[]) {
		AbstractConcreteClassMapping mapping = new AbstractConcreteClassMapping();
		mapping.setAbstractClass(abstractClass);
		mapping.setConcreteClass(concreteClass);
		if (factoryMethod != null) {
			mapping.setConcreteCFactoryMethod(factoryMethod);
			if (factoryMethod.getParameterCount() > 0)
				mapping.setMethodParamVariables(methodParamVariables);
		}
		register(mapping);
	}

	public void register(AbstractConcreteClassMapping abstractConcreteClassMapping) {
		abstractConcreteClassMap.put(abstractConcreteClassMapping.getAbstractClass(), abstractConcreteClassMapping);
	}

	public void resetVariableMap() {
		variableMap = Common.createVariableMap();
	}

	public Map<Class<?>, AbstractConcreteClassMapping> getAbstractConcreteClassMap() {
		return abstractConcreteClassMap;
	}

	public void setAbstractConcreteClassMap(Map<Class<?>, AbstractConcreteClassMapping> abstractConcreteClassMap) {
		this.abstractConcreteClassMap = abstractConcreteClassMap;
	}

	public List<String> getCreatedMethodName() {
		return createdMethodName;
	}

	public void setCreatedMethodName(List<String> createdMethodName) {
		this.createdMethodName = createdMethodName;
	}

	public Map<String, String> getVariableMap() {
		return variableMap;
	}

	public void setVariableMap(Map<String, String> variableMap) {
		this.variableMap = variableMap;
	}

	public List<Class> getAlreadyImportClasses() {
		return alreadyImportClasses;
	}

	public void setAlreadyImportClasses(List<Class> alreadyImportClasses) {
		this.alreadyImportClasses = alreadyImportClasses;
	}

	public Map<Class, String> getClassSimpleNameMap() {
		return classSimpleNameMap;
	}

	public void setClassSimpleNameMap(Map<Class, String> classSimpleNameMap) {
		this.classSimpleNameMap = classSimpleNameMap;
	}

	public Map<MethodCallingKey, MethodCallingVariable> getComplexTypeCreateMapping() {
		return complexTypeCreateMapping;
	}

	public void setComplexTypeCreateMapping(Map<MethodCallingKey, MethodCallingVariable> complexTypeCreateMapping) {
		this.complexTypeCreateMapping = complexTypeCreateMapping;
	}

	public List<Field> getAllFieldList() {
		return allFieldList;
	}

	public void setAllFieldList(List<Field> allFieldList) {
		this.allFieldList = allFieldList;
	}

	public StringBuilder getPackageSB() {
		return packageSB;
	}

	public void setPackageSB(StringBuilder packageSB) {
		this.packageSB = packageSB;
	}

	public StringBuilder getImportSB() {
		return importSB;
	}

	public void setImportSB(StringBuilder importSB) {
		this.importSB = importSB;
	}

	public StringBuilder getFieldsSB() {
		return fieldsSB;
	}

	public void setFieldsSB(StringBuilder fieldsSB) {
		this.fieldsSB = fieldsSB;
	}

	public StringBuilder getSetupSB() {
		return setupSB;
	}

	public void setSetupSB(StringBuilder setupSB) {
		this.setupSB = setupSB;
	}

	public StringBuilder getAllTestCaseSB() {
		return allTestCaseSB;
	}

	public void setAllTestCaseSB(StringBuilder allTestCaseSB) {
		this.allTestCaseSB = allTestCaseSB;
	}

	public StringBuilder getCreatedComplexTypeMethod() {
		return createdComplexTypeMethod;
	}

	public void setCreatedComplexTypeMethod(StringBuilder createdComplexTypeMethod) {
		this.createdComplexTypeMethod = createdComplexTypeMethod;
	}

	public StringBuilder getAddtionalMethods() {
		return addtionalMethods;
	}

	public void setAddtionalMethods(StringBuilder addtionalMethods) {
		this.addtionalMethods = addtionalMethods;
	}

	public Map<Class, MethodMatcher> getMethodMatchers() {
		return methodMatchers;
	}

	public Map<Class<?>, Short> getSameTypeCreatedTimesMap() {
		return sameTypeCreatedTimesMap;
	}

	public void setSameTypeCreatedTimesMap(Map<Class<?>, Short> sameTypeCreatedTimesMap) {
		this.sameTypeCreatedTimesMap = sameTypeCreatedTimesMap;
	}

	public boolean isGenFullQualifierName() {
		return genFullQualifierName;
	}

	public void setGenFullQualifierName(boolean genFullQualifierName) {
		this.genFullQualifierName = genFullQualifierName;
	}

	public void setMethodMatchers(Map<Class, MethodMatcher> methodMatchers) {
		this.methodMatchers = methodMatchers;
	}

	public static String getLocalRepoDir() {
		return localRepoDir;
	}

	public static void setLocalRepoDir(String localRepoDir) {
		CodeGenContext.localRepoDir = localRepoDir;
	}

//	public static DynamicClassLoader getUcl() {
//		if (null == ucl) {
//			ucl = new DynamicClassLoader(CodeGenContext.class.getClassLoader(), new URL[] {});
//		}
//		return ucl;
//	}
//
//	public static void setUcl(DynamicClassLoader ucl) {
//		CodeGenContext.ucl = ucl;
//	}

//	public List<URL> getLoadedJarList() {
//		return templateJarList;
//	}

	public static String getPathRegex() {
		String pathRegex = File.separator;
		if ("\\".equals(pathRegex)) {
			pathRegex = "\\\\";
		}
		return pathRegex;
	}

//	public List<String> mapJarClasses() {
//		final String pathRegex = getPathRegex();
//		templateJarList.stream().forEach(e -> {
//			try {
//				File file = new File(e.toURI());
//				if (file.isDirectory()) {
//					String folderPath = file.getAbsolutePath() + File.separator;
//					List<File> classFileList = new ArrayList<>();
//					codegen.util.Common.listAllFiles(file, "[\\w\\$]+\\.class", classFileList);
//					for (File classFile : classFileList) {
//						String clzFilePath;
//						try {
//							clzFilePath = classFile.getCanonicalPath();
//							clzFilePath = clzFilePath.replace(folderPath, "");
//							clzFilePath = clzFilePath.replaceAll(pathRegex, "\\.");
//							clzFilePath = clzFilePath.replaceAll(".class", "");
//							loadedClassesName.add(clzFilePath);
//						} catch (IOException e2) {
//							e2.printStackTrace();
//						}
//					}
//				}
//			} catch (URISyntaxException e2) {
//				// TODO Auto-generated catch block
//				e2.printStackTrace();
//			}
//
//			try {
//				List<String> cs = Common.getClassesNameFromJar(new URL[] { e }, this);
//				loadedClassesName.addAll(cs);
//			} catch (Exception e1) {
//
//			}
//		});
//		// target
//		return loadedClassesName;
//	}

	public List<String> getLoadedClassesName() {
		List<String> loadedClassesName = new ArrayList<>();
		loadedClassesName.add("");
		loadedClassesName.addAll(this.jrePrimitiveTypes);
		
		return loadedClassesName;
	}

	public int getClipBoardMonitorServerPort() {
		return clipBoardMonitorServerPort;
	}

	public void setClipBoardMonitorServerPort(int clipBoardMonitorServerPort) {
		this.clipBoardMonitorServerPort = clipBoardMonitorServerPort;
	}

	public List<Class> getAttachedClzList() {
		return attachedClzList;
	}

	public Map<Class<?>, Object> getUiModelMap() {
		return uiModelMap;
	}

	public static String getMavenHome() {
		return mavenHome;
	}

	public static void setMavenHome(String mavenHome) {
		CodeGenContext.mavenHome = mavenHome;
	}

	public byte[] getAppBytes() {
		return appBytes;
	}

	public void setAppBytes(byte[] appBytes) {
		this.appBytes = appBytes;
	}

	public ReloadableDynamicClassLoaderOwner getReloadableDynamicClassLoaderOwner() {
		return reloadableDynamicClassLoaderOwner;
	}

	public void setReloadableDynamicClassLoaderOwner(
			ReloadableDynamicClassLoaderOwner reloadableDynamicClassLoaderOwner) {
		this.reloadableDynamicClassLoaderOwner = reloadableDynamicClassLoaderOwner;
	}

	public List<String> getJreClasses() {
		return jreClasses;
	}

	public void setJreClasses(List<String> jreClasses) {
		this.jreClasses = jreClasses;
	}

	public File getPomFile() {
		return pomFile;
	}

	public void setPomFile(File pomFile) {
		this.pomFile = pomFile;
	}

	public List<String> getJrePrimitiveTypes() {
		return jrePrimitiveTypes;
	}

    public static List<String> getMemoryAreas() {
        return memoryAreas;
    }

    public Map<String, Object> getParamPossibleValsMap() {
        return paramPossibleValsMap;
    }

}
