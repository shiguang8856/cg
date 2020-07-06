package cg.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.sql.Date;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cg.AbstractConcreteClassMapping;
import cg.MethodCallParamVariableCode;
import cg.Replacement;
import cg.Syntax;
import cg.complexobj.CodeGenContext;
import cg.complexobj.PojoSetterGenerator;
import cg.unittest.MethodCallingVariable;

public class Common {
	public static Charset utf8Charset = Charset.forName("UTF-8");
	public static CharsetEncoder utf8CharsetEncoder = utf8Charset.newEncoder();
	public static CharsetDecoder utf8CharsetDecoder = utf8Charset.newDecoder();

	public static String getRunningJar() {
		ProtectionDomain protectionDomain = Common.class.getProtectionDomain();
		CodeSource codeSource = protectionDomain.getCodeSource();
		URL uRL = codeSource.getLocation();
		String path = uRL.getPath();
		File jarFile = new File(path);
		String jarFilePath = jarFile.getAbsolutePath();
		return jarFilePath;
	}

	public static String getWorkingDir() {
		ProtectionDomain protectionDomain = Common.class.getProtectionDomain();
		CodeSource codeSource = protectionDomain.getCodeSource();
		URL uRL = codeSource.getLocation();
		String path = uRL.getPath();
		File jarFile = new File(path);
		String jarFilePath = jarFile.getAbsolutePath();
		String s = jarFilePath.replace(jarFile.getName(), "");
		return s;
	}
	public static Class[] getClassByName(String... clzNames) {
		List<Class<?>> list = new ArrayList<>();
		for(String name : clzNames) {
			Class<?> c = null;
//			try {
//				c = Class.forName(name, false, CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner().getJarDynamicClassLoader());
//			} catch (Exception e) {
//			}
//			try {
//				if(null==c) {
//					c = Class.forName(name, false, CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner().getDirDynamicClassLoader());
//				}
//			} catch (ClassNotFoundException e) {
//			}
			try {
				if(null==c) {
					c = Class.forName(name, false, Common.class.getClassLoader());
				}
			} catch (Exception e) {
			}
			if(null!=c) {
				list.add(c);
			}
		}
		return list.toArray(new Class[list.size()] );
	}
	public static Class getClzByName(String clzName) {
		Class o = null;
		try {
			o = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner().loadClass(clzName, true);
		} catch (Exception e) {
			boolean isArrayType = false;
			String name = null;
			if (clzName.trim().endsWith("[]")) {
				isArrayType = true;
				String componentType = clzName.replaceAll("\\[\\]", "");
				name = componentType;
			} else {
				name = clzName;
			}
			try {
				Class componentClz = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner().loadClass(name,
						true);
			} catch (ClassNotFoundException e1) {
				if (void.class.getSimpleName().equals(name)) {
					o = void.class;
				} else if (name.equals("java.lang.Object")) {
					o = Object.class;
				} else if (name.equals("java.lang.String")) {
					o = String.class;
				} else if (name.equals("java.lang.Boolean")) {
					o = Boolean.class;
				} else if (name.equals("java.lang.Long")) {
					o = Long.class;
				} else if (name.equals("java.lang.Double")) {
					o = Double.class;
				} else if (name.equals("java.lang.Integer")) {
					o = Integer.class;
				} else if (name.equals("java.lang.Short")) {
					o = Short.class;
				} else if (name.equals("java.lang.Float")) {
					o = Float.class;
				} else if (name.equals("java.lang.Byte")) {
					o = Byte.class;
				} else if (name.equals("java.lang.Character")) {
					o = Character.class;
				} else if (name.equals("java.sql.Date")) {
					o = java.sql.Date.class;
				} else if (name.equals("boolean")) {
					o = boolean.class;
				} else if (name.equals("long")) {
					o = long.class;
				} else if (name.equals("double")) {
					o = double.class;
				} else if (name.equals("int")) {
					o = int.class;
				} else if (name.equals("short")) {
					o = short.class;
				} else if (name.equals("float")) {
					o = float.class;
				} else if (name.equals("byte")) {
					o = byte.class;
				} else if (name.equals("char")) {
					o = char.class;
				} else if (name.equals("java.sql.Timestamp")) {
					o = java.sql.Timestamp.class;
				} else if (name.equals("java.util.Date")) {
					o = Date.class;
				} else if (name.startsWith("java.util.Map")) {
					o = java.util.HashMap.class;
				} else if (name.startsWith("java.util.Set")) {
					o = java.util.HashSet.class;
				} else if (name.equals("java.math.BigInteger")) {
					o = java.math.BigInteger.class;
				} else if (name.equals("java.math.BigDecimal")) {
					o = java.math.BigDecimal.class;
				}
			}

//			else if (int.class.getSimpleName().equals(name)) {
//				o = int.class;
//			} else if (double.class.getSimpleName().equals(name)) {
//				o = double.class;
//			} else if (float.class.getSimpleName().equals(name)) {
//				o = float.class;
//			} else if (long.class.getSimpleName().equals(name)) {
//				o = long.class;
//			} else if (char.class.getSimpleName().equals(name)) {
//				o = char.class;
//			} else if (byte.class.getSimpleName().equals(name)) {
//				o = byte.class;
//			} else if (short.class.getSimpleName().equals(name)) {
//				o = short.class;
//			} else if (boolean.class.getSimpleName().equals(name)) {
//				o = boolean.class;
//			} else if (void.class.getSimpleName().equals(name)) {
//				o = void.class;
//			} else if (void.class.getSimpleName().equals(name)) {
//				o = void.class;
//			} else if (void.class.getSimpleName().equals(name)) {
//				o = void.class;
//			} else if (void.class.getSimpleName().equals(name)) {
//				o = void.class;
//			} 
			if (isArrayType) {
				Object arrayObj = Array.newInstance(o, 1);
				o = arrayObj.getClass();
			}
		}
		return o;
	}

	public static <T extends Annotation> T getParameterAnnotationByType(Class<T> c, Parameter param) {
		Annotation panns1[] = param.getAnnotations();
		Annotation ann = null;
		for (Annotation an : panns1) {
			if (c.isAssignableFrom(an.annotationType())) {
				ann = an;
				break;
			}
		}
		return (T) ann;
	}

	public static List<String> getDefaultConcernedPacks(Class c) {
		List<String> concernedPacks = new ArrayList<String>();
		String packageName = c.getPackage() != null ? c.getPackage().getName() : "";
		String packDeep[] = packageName.split("\\.");
		StringBuilder sb = new StringBuilder();
		if (packDeep.length > 2) {
			for (int i = 0; i <= 2; i++) {
				sb.append(packDeep[i]).append(".");
			}
			sb.delete(sb.length() - 1, sb.length());
		} else {
			sb.append(packageName);
		}
		concernedPacks.add(sb.toString());
		return concernedPacks;
	}

	public static boolean isConcernPack(String packName, List<String> concernedPackageNames) {
		boolean isconcern = false;
		isconcern = concernedPackageNames.stream().anyMatch(cp -> {
			if (packName.startsWith(cp.trim())) {
				return true;
			} else {
				return false;
			}
		});
		return isconcern;
	}

	public static List<Field> getAllFields(Class c) {
		List<Field> allFieldList = new ArrayList<>();
		while (c != Object.class) {
			allFieldList.addAll(Arrays.asList(c.getDeclaredFields()));
			c = c.getSuperclass();
		}
		return allFieldList;
	}

	public static void getAllFields(Class c, List<String> concernedPackageNames, List<Field> allFieldList) {
		for (Field f : c.getDeclaredFields()) {
			allFieldList.add(f);
		}
		Class superClass = c.getSuperclass();
		if (superClass != null) {
			String packName = superClass.getPackage().getName();
			isConcernPack(packName, concernedPackageNames);
			getAllFields(superClass, concernedPackageNames, allFieldList);
		}
	}

	public static void getAllFieldNames(Class c, List<String> concernedPackageNames, List<String> allFieldList) {
		for (Field f : c.getDeclaredFields()) {
			allFieldList.add(f.getName());
		}
		Class superClass = c.getSuperclass();
		if (superClass != null) {
			String packName = superClass.getPackage().getName();
			isConcernPack(packName, concernedPackageNames);
			getAllFieldNames(superClass, concernedPackageNames, allFieldList);
		}
	}

	public static Boolean isMethodReturnBoolean(Method m) {
		boolean b = false;
		Class c = m.getReturnType();
		if (Boolean.class.equals(c) || boolean.class.equals(c)) {
			b = true;
		}
		return b;
	}

	public static boolean isMethodReturnCollectionType(Method m) {
		boolean b = false;
		Class c = m.getReturnType();
		if (isACollection(c)) {
			b = true;
		}
		return b;
	}

	public static boolean isACollection(Class s) {
		boolean b = false;
		if (java.util.Collection.class.isAssignableFrom(s)) {
			b = true;
		}
		return b;
	}

	@Deprecated
	public static Class getOneClassUnderDirectory(String packName, String directory) {
		Class c = null;
		File childDirectory = new File(directory);
		if (childDirectory.isDirectory()) {
			File files[] = childDirectory.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.getName().endsWith(".class")) {
						StringBuilder sb = new StringBuilder(file.getName());
						String className = sb.delete(sb.length() - 6, sb.length()).toString();
						try {
							c = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner()
									.loadClass(packName + "." + className, true);
							return c;
						} catch (Exception e) {
						}
					}
				}
			}
		}
		return c;
	}

	public static void getClassesUnderPackage(Class oneOfClass, List<Class> classList) {
		String packName = oneOfClass.getPackage().getName();
		if ("".equals(packName)) {
			getAllTopLevelClassesInClassesDirectory(oneOfClass, classList);
		} else {
			List<String> loadedClzNames = CodeGenContext.getInstance().getLoadedClassesName();
			List<String> claNames = loadedClzNames.stream().filter(e -> e.startsWith(packName))
					.collect(Collectors.toList());
			for (String clzName : claNames) {
				try {
					Class<?> clz = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner()
							.loadClass(clzName, true);
					classList.add(clz);
				} catch (ClassNotFoundException e1) {
					System.out.println(String.format("cannot find class by name %s", clzName));
				}
			}
		}

//		getAllTopLevelClasses(oneOfClass, classList);
	}

	public static void getAllTopLevelClassesInClassesDirectory(Class oneOfClass, List<Class> classList) {
		Package sourcePack = oneOfClass.getPackage();
		List<String> classNameList = new ArrayList<String>();
		String sourceDirectory = sourcePack.getName().replaceAll("\\.", "/");
		URL url = oneOfClass.getClassLoader().getResource(sourceDirectory);
		try {
			if (null != url) {
				java.net.URI uri = url.toURI();
				Path path;
				if (uri.getScheme().equals("jar")) {
					Map<String, Object> map = new HashMap<>();
					FileSystem fs = null;
					try {
						try {
							fs = FileSystems.getFileSystem(uri);
						} catch (Exception e) {
						}
						if (fs == null)
							fs = FileSystems.newFileSystem(uri, map);
					} catch (Exception e) {
					}
					if (fs == null) {
						System.out.print("cannot get FileSystem");
						return;
					}
					path = fs.getPath(sourceDirectory);
				} else {
					path = Paths.get(uri);
				}
				java.util.stream.Stream<Path> stream = Files.walk(path, Integer.MAX_VALUE);
				stream.forEach(e -> {
					if (e.toString().endsWith(".class")) {
						try {
							File f = e.toFile();
							classNameList.add(sourcePack.getName() + "." + f.getName());
						} catch (UnsupportedOperationException e1) {
							String name = e.toString();
							if (name.startsWith("/")) {
								name = name.substring(1);
							}
							name = name.replaceAll("/", ".");
							// name = name.replaceAll("\\$", ".");
							classNameList.add(name);
						}
					}
				});
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		 * File fileDirectory = new File(url.getPath()); File files[] =
		 * fileDirectory.listFiles(); if(files != null){ for(File file : files){
		 * if(file.isDirectory()){ String packName = sourcePack.getName() + "." +
		 * file.getName(); String childDirectoryStr =
		 * sourcePack.getName().replaceAll("\\.", "/") + "/"+ file.getName(); URL
		 * childUrl = ClassLoader.getSystemClassLoader().getResource(childDirectoryStr);
		 * if(childUrl != null){ Class c = getOneClassUnderDirectory(packName,
		 * childUrl.getPath()); if(c != null){ getAllTopLevelClasses(c.getPackage(),
		 * classList); } } }else{ if(file.getName().endsWith(".class")){
		 * classNameList.add(sourcePack.getName() + "." + file.getName()); } } } }
		 */
		classNameList.forEach(name -> {
			StringBuilder sb = new StringBuilder(name);
			String className = sb.delete(sb.length() - 6, sb.length()).toString();
			try {
				Class c = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner().loadClass(className,
						true);
				classList.add(c);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		/*
		 * classNameList.forEach(name -> { StringBuilder sb = new StringBuilder(name);
		 * String className = sb.delete(sb.length() -6 , sb.length()).toString(); try {
		 * Class c =
		 * CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner().loadClass
		 * (className); classList.add(c); } catch (Exception e) { e.printStackTrace(); }
		 * });
		 */
		return;
	}

	/*
	 * @Deprecated public static void getAllTopLevelClasses(Package sourcePack,
	 * List<Class> classList){ List<String> classNameList = new ArrayList<String>();
	 * String sourceDirectory = sourcePack.getName().replaceAll("\\.", "/"); URL url
	 * = Common.class.getClassLoader().getResource(sourceDirectory); File
	 * fileDirectory = new File(url.getPath()); File files[] =
	 * fileDirectory.listFiles(); if(files != null){ for(File file : files){
	 * if(file.isDirectory()){ String packName = sourcePack.getName() + "." +
	 * file.getName(); String childDirectoryStr =
	 * sourcePack.getName().replaceAll("\\.", "/") + "/"+ file.getName(); URL
	 * childUrl = ClassLoader.getSystemClassLoader().getResource(childDirectoryStr);
	 * if(childUrl != null){ Class c = getOneClassUnderDirectory(packName,
	 * childUrl.getPath()); if(c != null){ getAllTopLevelClasses(c.getPackage(),
	 * classList); } } }else{ if(file.getName().endsWith(".class")){
	 * classNameList.add(sourcePack.getName() + "." + file.getName()); } } } }
	 * classNameList.forEach(name -> { StringBuilder sb = new StringBuilder(name);
	 * String className = sb.delete(sb.length() -6 , sb.length()).toString(); try {
	 * Class c =
	 * CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner().loadClass
	 * (className); classList.add(c); } catch (Exception e) { e.printStackTrace(); }
	 * }); return ; }
	 */
	public static void getAllTopLevelClasses(Class oneOfClass, boolean startDirectory, List<Class> classList) {
		getClassesUnderPackage(oneOfClass, classList);
	}

	public static String makeFirstCharInLowercase(String name) {
		char firstCh = name.charAt(0);
		String lowerCaseCh = new String(new char[] { firstCh });
		lowerCaseCh = lowerCaseCh.toLowerCase();
		return name.replaceFirst("\\w{1}", lowerCaseCh);
	}
	public static String makeFirstCharInUpperCase(String name) {
		char firstCh = name.charAt(0);
		String upperCaseCh = new String(new char[] { firstCh });
		upperCaseCh = upperCaseCh.toUpperCase();
		return name.replaceFirst("\\w{1}", upperCaseCh);
	}
	public static String getInstanceName(Class c, String prefix, String suffix) {
		if(null==c) {
			return "";
		}
		if (prefix != null && !"".equals(prefix.trim())) {
			String name = prefix + c.getSimpleName();
			if (suffix != null)
				name = name + suffix;
			return name;
		} else {
			char firstCh = c.getSimpleName().charAt(0);
			String lowerCaseCh = new String(new char[] { firstCh });
			lowerCaseCh = lowerCaseCh.toLowerCase();
			if (!isComplexType(c)) {
				return lowerCaseCh;
			}
			if (c == Class.class) {
				return "clz";
			}
			return c.getSimpleName().replaceFirst("\\w{1}", lowerCaseCh);
		}
	}

	public static String getInstanceName(Class c, String prefix) {
		return getInstanceName(c, prefix, null);
	}

	public static String getInstanceName(Class c) {
		return getInstanceName(c, null, null);
	}

	public static String getDefaultVal(Parameter p) {
		String defaultVal = null;
		if (p != null) {
			String typeName = null;
			typeName = p.getParameterizedType().getTypeName();
			Class c = null;
			try {
				c = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner().loadClass(typeName, true);
			} catch (ClassNotFoundException e) {
				// e.printStackTrace();
			}
			if (typeName.equals("java.lang.Object")) {
				defaultVal = "new Object()";
			}
			if (typeName.equals("java.lang.String")) {
				defaultVal = "\"\"";
			}
			if (typeName.equals("java.lang.Boolean") || typeName.equals("boolean")) {
				defaultVal = "false";
			}
			if (typeName.equals("java.lang.Long") || typeName.equals("long")) {
				defaultVal = "1L";
			}
			if (typeName.equals("java.lang.Double") || typeName.equals("double")) {
				defaultVal = "1d";
			}
			if (typeName.equals("java.lang.Integer") || typeName.equals("int")) {
				defaultVal = "1";
			}
			if (typeName.equals("java.lang.Short") || typeName.equals("short")) {
				defaultVal = "(short)1";
			}
			if (typeName.equals("java.lang.Float") || typeName.equals("float")) {
				defaultVal = "1f";
			}
			if (typeName.equals("java.lang.Byte") || typeName.equals("byte")) {
				defaultVal = "(byte)0";
			}
			if (typeName.equals("java.lang.Character") || typeName.equals("char")) {
				defaultVal = "'a'";
			}
			if (typeName.equals("java.util.Date")) {
				defaultVal = "new java.util.Date()";
			}
			if (typeName.equals("java.sql.Date") || typeName.equals("java.sql.Timestamp")) {
				defaultVal = "new " + c.getName() + "(new java.util.Date().getTime())";
			}
			if (typeName.startsWith("java.util.Map")) {
				defaultVal = "new java.util.HashMap()";
			}
			if (typeName.startsWith("java.util.Set")) {
				defaultVal = "new java.util.HashSet()";
			}
			if (typeName.equals("java.math.BigInteger")) {
				defaultVal = "java.math.BigInteger.valueOf(1)";
			}
			if (typeName.equals("java.math.BigDecimal")) {
				defaultVal = "java.math.BigDecimal.valueOf(0)";
			}
			if (typeName.equals("javax.xml.datatype.XMLGregorianCalendar")) {
//				defaultVal = "util.ConverterUtil.toXMLGregorianCalendar(new java.util.Date())";
				defaultVal = "javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(new java.util.GregorianCalendar())";
			}
			if (c != null && c.isEnum()) {
				defaultVal = Common.getEnumCreateStr(c);
			}
			if (defaultVal == null && c != null) {
				defaultVal = getInstanceName(c);
			}
		}
		return defaultVal;
	}

	//// TODO
	public static int getArrayDemension(Class<?> ptype) throws Exception {
		boolean b = ptype.isArray();
		int m = -1;
		if (b) {
			m = 1;
			Class<?> componentType = ptype.getComponentType();
			while (componentType.isArray()) {
				componentType = componentType.getComponentType();
				m++;
			}
		} else {
			throw new Exception("Object is not an array instance");
		}
		return m;
	}

	public static String getArrayComponentTypeByrepstr(String str) {
		String type = null;
		if (str.trim().length() == 1) {
			char ch = str.charAt(0);
			switch (ch) {
			case 'D':
				type = "double";
				break;
			case 'F':
				type = "float";
				break;
			case 'S':
				type = "short";
				break;
			case 'B':
				type = "byte";
				break;
			case 'Z':
				type = "boolean";
				break;
			case 'I':
				type = "int";
				break;
			case 'C':
				type = "char";
				break;
			default:
				break;
			}
		} else {
			str = str.replaceFirst("L", "");
			str = str.replaceFirst(";", "");
			type = str;
		}
		return type;
	}

	public static String getArrayClzTypeJavaCode(Class c) {
		String realType = null;
		if (c.isArray()) {
			String s = c.toString();
			String sArray[] = s.split(" ");
			if (sArray.length > 0) {
				String strTmp = sArray[1];
				int d = 0;
				int i = 0;
				char[] ch = strTmp.toCharArray();
				for (; i < ch.length; i++) {
					if (ch[i] != '[') {
						break;
					}
				}
				d = i;
				StringBuilder sb = new StringBuilder();
				String repsentType = strTmp.substring(i);
				realType = getArrayComponentTypeByrepstr(repsentType);
				for (; d > 0; d--) {
					sb.append("[]");
				}
				realType = realType + sb.toString();
			} else {

			}
		}
		return realType;
	}

	private static String getArrayComponentTypeJavaName(Class arrayClz) throws Exception {
		boolean b = arrayClz.isArray();
		Class<?> componentType = null;
		if (b) {
			componentType = arrayClz.getComponentType();
			while (componentType.isArray()) {
				componentType = componentType.getComponentType();
			}
		} else {
			throw new Exception("Object is not an array instance");
		}
		String str = componentType.getName();
		return str;
	}

	private static String getIntArrayInitJavaCode(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(1).append(",");
		}
		if (length > 0) {
			sb.delete(sb.length() - 1, sb.length());
		}
		return sb.toString();
	}

	public static String getDefaultVal(Class c) {
		String defaultVal = null;
		if (c != null) {
			if (c.isArray()) {
				int dn;
				try {
					dn = getArrayDemension(c);
					String arrayDimensionJavaCode = getIntArrayInitJavaCode(dn);
					String arrayComponentTypeName = getArrayComponentTypeJavaName(c);
					defaultVal = "(" + getArrayClzTypeJavaCode(c) + ")Array.newInstance(" + arrayComponentTypeName
							+ ".class ," + arrayDimensionJavaCode + ")";
				} catch (Exception e) {
					defaultVal = "null";
				}
			}
			String typeName = c.getName();
			if (typeName.equals("java.lang.Object")) {
				defaultVal = "new Object()";
			}
			if (typeName.equals("java.lang.String")) {
				defaultVal = "\"\"";
			}
			if (typeName.equals("java.lang.Boolean") || typeName.equals("boolean")) {
				defaultVal = "false";
			}
			if (typeName.equals("java.lang.Long") || typeName.equals("long")) {
				defaultVal = "1L";
			}
			if (typeName.equals("java.lang.Double") || typeName.equals("double")) {
				defaultVal = "1d";
			}
			if (typeName.equals("java.lang.Integer") || typeName.equals("int")) {
				defaultVal = "1";
			}
			if (typeName.equals("java.lang.Short") || typeName.equals("short")) {
				defaultVal = "(short)1";
			}
			if (typeName.equals("java.lang.Float") || typeName.equals("float")) {
				defaultVal = "1f";
			}
			if (typeName.equals("java.lang.Byte") || typeName.equals("byte")) {
				defaultVal = "(byte)1";
			}
			if (typeName.equals("java.lang.Character") || typeName.equals("char")) {
				defaultVal = "'a'";
			}
			if (typeName.equals("java.util.Date")) {
				defaultVal = "new java.util.Date()";
			}
			if (typeName.equals("java.sql.Date") || typeName.equals("java.sql.Timestamp")) {
				defaultVal = "new " + c.getName() + "(new java.util.Date().getTime())";
			}
			if (typeName.startsWith("java.util.Map")) {
				defaultVal = "new java.util.HashMap()";
			}
			if (typeName.startsWith("java.util.Set")) {
				defaultVal = "new java.util.HashSet()";
			}
			if (typeName.equals("java.math.BigInteger")) {
				defaultVal = "java.math.BigInteger.valueOf(1)";
			}
			if (typeName.equals("java.math.BigDecimal")) {
				defaultVal = "java.math.BigDecimal.valueOf(0)";
			}
			if (typeName.equals("javax.xml.datatype.XMLGregorianCalendar")) {
//				defaultVal = "util.ConverterUtil.toXMLGregorianCalendar(new java.util.Date())";
				defaultVal = "javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(new java.util.GregorianCalendar())";
			}
			if (c != null && c.isEnum()) {
				defaultVal = getEnumCreateStr(c);
			}
			if (defaultVal == null && c != null) {
				defaultVal = getInstanceName(c);
			}
		}
		return defaultVal;
	}

	public static String getEnumCreateStr(Class c) {
		StringBuilder sb = new StringBuilder();
		String classNameForPrint = c.getName().replaceAll("\\$", "\\.");
		Object obj[] = c.getEnumConstants();
		sb.append(classNameForPrint).append(".").append(obj[0].toString());
		return sb.toString();
	}

	public static boolean isPrimitiveType(Class c) {
		return !isComplexType(c);
	}

	public static boolean isComplexType(Class c) {
		boolean b = false;
		if (c.isPrimitive() || c.isEnum()) {
			return b;
		}
		if (c.isArray()) {
			c = c.getComponentType();
		}
		String typeName = c.getName();
		if (typeName.equals("java.lang.Object")) {
			return b;
		}
		if (typeName.equals("java.lang.String") || typeName.equals("java.lang.StringBuilder")
				|| typeName.equals("java.lang.StringBuffer")) {
			return b;
		}
		if (typeName.equals("java.lang.Boolean") || typeName.equals("boolean")) {
			return b;
		}
		if (typeName.equals("java.lang.Character") || typeName.equals("char")) {
			return b;
		}
		if (typeName.equals("java.lang.Long") || typeName.equals("long")) {
			return b;
		}
		if (typeName.equals("java.lang.Double") || typeName.equals("double")) {
			return b;
		}
		if (typeName.equals("java.lang.Integer") || typeName.equals("int")) {
			return b;
		}
		if (typeName.equals("java.lang.Short") || typeName.equals("short")) {
			return b;
		}
		if (typeName.equals("java.lang.Float") || typeName.equals("float")) {
			return b;
		}
		if (typeName.equals("java.lang.Byte") || typeName.equals("byte")) {
			return b;
		}
		if (typeName.equals("java.util.Date") || typeName.equals("java.sql.Date")
				|| typeName.equals("java.sql.Timestamp")) {
			return b;
		}
		if (typeName.equals("java.math.BigInteger")) {
			return b;
		}
		if (typeName.equals("java.math.BigDecimal")) {
			return b;
		}
		if (typeName.equals("javax.xml.datatype.XMLGregorianCalendar")) {
			return b;
		}
		b = true;
		return b;
	}

	public static Method[] getAllDeclaredPublicMethodsForTest(Class clzToTest) {
		Method ms[] = clzToTest.getDeclaredMethods();
		List<Method> list = Arrays.asList(ms);
		list = list.stream().filter(m -> {
			if (Modifier.isPublic(m.getModifiers()) && !m.isBridge()) {
				return true;
			}
			return false;
		}).collect(Collectors.toList());
		int mc = list.size();
		Method publicMethods[] = new Method[mc];
		return list.toArray(publicMethods);
	}

	public static Method[] getAllPublicMethods(Class clzToTest) {
		Method ms[] = clzToTest.getMethods();
		List<Method> list = Arrays.asList(ms);
		list = list.stream().filter(m -> {
			if (Modifier.isPublic(m.getModifiers())) {
				return true;
			}
			return false;
		}).collect(Collectors.toList());
		int mc = list.size();
		Method publicMethods[] = new Method[mc];
		return list.toArray(publicMethods);
	}
	public static List<Method> getAllMethodList(Class c) {
		Method ms[] = c.getMethods();
		Method dms[] = c.getDeclaredMethods();
		Method allM[] = new Method[ms.length + dms.length];
		System.arraycopy(ms, 0, allM, 0, ms.length);
		System.arraycopy(dms, 0, allM, ms.length, dms.length);
		List<Method> list = Arrays.asList(allM);
		return list;
	}
	public static Method[] getAllMethods(Class c) {
		Method ms[] = c.getMethods();
		Method dms[] = c.getDeclaredMethods();
		Method allM[] = new Method[ms.length + dms.length];
		System.arraycopy(ms, 0, allM, 0, ms.length);
		System.arraycopy(dms, 0, allM, ms.length, dms.length);
		List<Method> list = Arrays.asList(allM);
		list = list.stream().distinct().collect(Collectors.toList());
		int mc = list.size();
		Method publicMethods[] = new Method[mc];
		return list.toArray(publicMethods);
	}
	public static String[] getAllMethodsAndConstructorStr(Class<?> c) {
		List<Method> mlist = new ArrayList<>();
		List<String> list = new ArrayList<>();
		Constructor[] cs = c.getConstructors();
		for(Constructor cn : cs) {
			list.add(cn.toGenericString());
		}
		mlist.addAll(Arrays.asList(getAllMethods(c)));
		for(Method m : mlist) {
			list.add(m.toGenericString());
		}
		String publicMethods[] = new String[list.size()];
		return list.toArray(publicMethods);
	}
	public static List<String> getAllMethodQualifiers(Class<?> c) {
		List<Method> mlist = Arrays.asList(getAllMethods(c));
		List<String> list = new ArrayList<>();
		for(Method m : mlist) {
//			list.add(m.toGenericString());
			list.add(m.toString());
		}
		return list;
	}
	public static void getAllClzAnnotation(Class<?> c, List<Annotation> list) {
		list.addAll(Arrays.asList(c.getAnnotations()));
		Class<?> superC = c.getSuperclass();
		if (superC != null) {
			getAllClzAnnotation(superC, list);
		}
	}

	public static boolean isClassArray(Class c) {
		boolean b = false;
		if (c.isArray() && c.getComponentType() == Class.class) {
			b = true;
		}
		return b;
	}

	public static boolean isVoid(Method m) {
		boolean b = false;
		Class c = m.getReturnType();
		if (c.equals(Void.TYPE)) {
			b = true;
		}
		return b;
	}

	public static boolean isStatic(Method m) {
		boolean b = false;
		if ((m.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
			b = true;
		}
		return b;
	}

	public static boolean isAbstract(Method m) {
		boolean abs = false;
		if ((m.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT) {
			abs = true;
		}
		return abs;
	}

	public static boolean isAbstractOrInterface(Class clzToTest) {
		boolean abs = false;
		if (clzToTest.isInterface() || isAbstract(clzToTest)) {
			abs = true;
		}
		return abs;
	}

	public static boolean isAbstract(Class clzToTest) {
		boolean abs = false;
		if ((clzToTest.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT) {
			abs = true;
		}
		return abs;
	}

	public static boolean isFinal(Field f) {
		boolean abs = false;
		if ((f.getModifiers() & Modifier.FINAL) == Modifier.FINAL) {
			abs = true;
		}
		return abs;
	}

	public static Map<String, String> createVariableMap() {
		Map<String, String> variableMap = new HashMap<>();
		variableMap.put(Replacement.ASSIGN_SYMBOL.name(), Syntax.ASSIGN_SYMBOL);
		variableMap.put(Replacement.END_OF_LINE.name(), Syntax.END_OF_LINE);
		variableMap.put(Replacement.NEW_LINE.name(), Syntax.NEW_LINE);
		variableMap.put(Replacement.CLASS_END_LINE.name(), Syntax.CLASS_END_LINE);
		return variableMap;
	}

	public static String replaceAllKeyWord(String syntax, Map<String, String> variableMap) throws Exception {
		String str = syntax;
		Iterator<Map.Entry<String, String>> it = variableMap.entrySet().iterator();
		Entry<String, String> entry;
		while (it.hasNext()) {
			entry = it.next();
			String key = "\\" + Syntax.REFERENCE_SYMBOL + entry.getKey() + "\\" + Syntax.REFERENCE_SYMBOL;
			boolean replaceAble = true;
			if (entry.getValue() == null) {
				System.out.println("stopping");
				replaceAble = false;
			}
			if (replaceAble) {
				String replacement = null;
				try {
					replacement = replacePlaceHolder(entry.getValue(), variableMap);
					if (replacement.indexOf("$") != -1) {
						replacement = Matcher.quoteReplacement(replacement);
					}
					/*
					 * if(key.indexOf("$")!=-1){ key = Pattern.quote(key); }
					 */
					str = str.replaceAll(key, replacement);
				} catch (Exception e) {
					System.out.println(String.format("key=%s,replacement=%s,keyQuote=%s,replacementQuote=%s", key,
							replacement, Pattern.quote(key), Matcher.quoteReplacement(replacement)));
					e.printStackTrace();
				}
			}
		}
		return str;
	}

	private static String replacePlaceHolder(String strContainPlaceHolder, Map<String, String> variableMap) {
		if (strContainPlaceHolder.indexOf("${") != -1) {
			Pattern p = Pattern.compile("\\$\\{[\\w\\.\\(\\)]+\\}");
			Matcher m = p.matcher(strContainPlaceHolder);
			if (m.find()) {
				String value = null;
				if (m.groupCount() >= 1) {
					value = m.group(1);
				} else {
					value = m.group();
				}
				try {
					String keyname = value.substring(2, value.length() - 1);
					if (variableMap.containsKey(keyname)) {
						String regex = "\\" + Syntax.REFERENCE_SYMBOL + "\\{" + keyname + "\\}";
						strContainPlaceHolder = strContainPlaceHolder.replaceFirst(regex, variableMap.get(keyname));
					} else {
						return strContainPlaceHolder;
					}
				} catch (IndexOutOfBoundsException e) {
					return strContainPlaceHolder;
				}
				if (strContainPlaceHolder.indexOf("${") != -1) {
					return replacePlaceHolder(strContainPlaceHolder, variableMap);
				} else {
					return strContainPlaceHolder;
				}
			}
		}
		return strContainPlaceHolder;
	}

	public static String genPackage(Class clzToTest, Map<String, String> variableMap) throws Exception {
		variableMap.put(Replacement.PACKAGE_NAME.name(), clzToTest.getPackage().getName());
		String packageStr = Common.replaceAllKeyWord(Syntax.PACKAGE_DECLARE, variableMap);
		return packageStr;
	}

	public static String genMockClassBegin(Class clzToTest, CodeGenContext context) throws Exception {
		String className = clzToTest.getSimpleName();
		context.getVariableMap().put(Replacement.TMP_CLASS_NAME.name(), className + "Mock");
		String beginstr = Common.replaceAllKeyWord(Syntax.CLASS_BEGIN_LINE, context.getVariableMap());
		return beginstr;
	}

	public static String genClassBegin(Class clzToTest, Map<String, String> variableMap) throws Exception {
		String className = clzToTest.getSimpleName();
		variableMap.put(Replacement.TMP_CLASS_NAME.name(), className);
		String beginstr = Common.replaceAllKeyWord(Syntax.CLASS_BEGIN_LINE, variableMap);
		return beginstr;
	}

	public static String genUnitTestMatchersMethodParamsVariable(Map<String, String> variableMap, Class... cs)
			throws Exception {
		List<String> strList = new ArrayList<>();
		for (Class c : cs) {
			String matchStr = null;
			if (isComplexType(c)) {
				variableMap.put(Replacement.TMP_CLASS_NAME.name(), c.getSimpleName());
				matchStr = Common.replaceAllKeyWord(Syntax.ANY_OF_CLASS_MATCH, variableMap);
			} else {
				matchStr = Common.replaceAllKeyWord(Syntax.ANY_MATCH, variableMap);
			}
			strList.add(matchStr);
		}
		String strArray[] = new String[strList.size()];
		strArray = strList.toArray(strArray);
		return genMethodParamsVariable(strArray);
	}

	public static String genMethodParamsVariable(String... strings) {
		StringBuilder sb = new StringBuilder();
		for (String p : strings) {
			sb.append(p).append(", ");
		}
		if (sb.length() > 0) {
			sb.delete(sb.length() - 2, sb.length());
		}
		return sb.toString();
	}

	public static String genMethodParamsDeclaration(Class... cs) {
		StringBuilder sb = new StringBuilder();
		for (Class p : cs == null ? new Class[] {} : cs) {
			sb.append(p.getSimpleName()).append(" ").append(Common.getInstanceName(p)).append(", ");
		}
		if (sb.length() > 0) {
//			sb.insert(0, "throws ");
			sb.delete(sb.length() - 2, sb.length());
		}
		return sb.toString();
	}

	public static String genExceptionThrows(Class<? extends Exception>... cs) {
		StringBuilder sb = new StringBuilder();
		for (Class p : cs == null ? new Class[] {} : cs) {
			sb.append(p.getSimpleName()).append(", ");
		}
		if (sb.length() > 0) {
			sb.insert(0, "throws ");
			sb.delete(sb.length() - 2, sb.length());
		}
		return sb.toString();
	}

	public static String genMethodParamDeclareVariable(Map.Entry<Class, String>... params) {
		if (params == null || params.length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<Class, String> e : params) {
			sb.append(e.getKey().getSimpleName()).append("  ").append(e.getValue()).append(", ");
		}
		if (sb.length() > 0) {
			sb.delete(sb.length() - 2, sb.length());
		}
		return sb.toString();
	}

	public static String genimportStr(List<Class> alreadyImportClasses, Map<String, String> variableMap,
			Class... clzToImport) throws Exception {
		StringBuilder sb = new StringBuilder();
		for (Class c : clzToImport) {
			String str = genimportStr(c, alreadyImportClasses, variableMap);
			if (!"".equals(str)) {
				sb.append(str).append(Syntax.NEW_LINE);
			}
		}
		return sb.toString();
	}

	public static void importPack(String packageName, Map<String, String> variableMap, StringBuilder importBuilder)
			throws Exception {
		variableMap.put(Replacement.QUALIFIER.name(), packageName);
		String importStr = Common.replaceAllKeyWord(Syntax.IMPORT_LINE, variableMap);
		importBuilder.append(importStr);
	}

	@Deprecated
	public static void importC(Class clzToImport, List<Class> alreadyImportClasses, Map<String, String> variableMap,
			StringBuilder importBuilder) throws Exception {
		String str = genimportStr(clzToImport, alreadyImportClasses, variableMap);
		if (!"".equals(str)) {
			importBuilder.append(str).append(Syntax.NEW_LINE);
		}
	}

	public static void importC(Class[] classesToImport, CodeGenContext context) throws Exception {
		for (Class p : classesToImport) {
			String str = genimportStr(p, context.getAlreadyImportClasses(), context.getVariableMap());
			if (!"".equals(str)) {
				context.getImportSB().append(str).append(Syntax.NEW_LINE);
			}
		}
	}

	public static void importC(Class clzToImport, CodeGenContext context) throws Exception {
		String str = genimportStr(clzToImport, context.getAlreadyImportClasses(), context.getVariableMap());
		if (!"".equals(str)) {
			context.getImportSB().append(str).append(Syntax.NEW_LINE);
		}
	}

	public static String genimportStr(Class clzToImport, List<Class> alreadyImportClasses,
			Map<String, String> variableMap) throws Exception {
		if (clzToImport.isArray()) {
			clzToImport = clzToImport.getComponentType();
		}
		if (clzToImport.getName().startsWith("java.lang")) {
			String name = clzToImport.getName();
			String packageFragement[] = name.split("\\.");
			if (packageFragement.length == 3)
				return "";
		}
		if (clzToImport.isPrimitive()) {
			return "";
		}
		if (alreadyImportClasses.contains(clzToImport)) {
			return "";
		}
		boolean ambiguousClass = isSameSimpleNameClassImported(clzToImport, alreadyImportClasses);
		if (!ambiguousClass) {
			String className = clzToImport.getName();
			if (clzToImport.getEnclosingClass() != null) {
				className = className.replaceAll("\\$", "\\.");
			}
			variableMap.put(Replacement.QUALIFIER.name(), className);
			String importStr = Common.replaceAllKeyWord(Syntax.IMPORT_LINE, variableMap);
			// importSB.append(importStr);
			alreadyImportClasses.add(clzToImport);
			return importStr;
		}
		return "";
	}

	private static boolean isSameSimpleNameClassImported(Class c, List<Class> alreadyImportClasses) {
		boolean b = alreadyImportClasses.stream().anyMatch(a -> {
			String simpleName = c.getSimpleName();
			String existingSimpleName = a.getSimpleName();
			if (existingSimpleName.equals(simpleName))
				return true;
			else
				return false;
		});
		return b;
	}

	@Deprecated
	public static String genImportByQualifier(String qualifier, List<Class> alreadyImportClasses,
			Map<String, String> variableMap) throws Exception {
		Class c = null;
		try {
			c = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner().loadClass(qualifier, true);
		} catch (Exception e) {
		}
		if (c != null) {
			return genimportStr(c, alreadyImportClasses, variableMap);
		} else {
			return "";
		}
	}

	public static String genImportByQualifier(String qualifier, CodeGenContext context) throws Exception {
		Class c = null;
		try {
			c = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner().loadClass(qualifier, true);
		} catch (Exception e) {
		}
		if (c != null) {
			return genimportStr(c, context.getAlreadyImportClasses(), context.getVariableMap());
		} else {
			return "";
		}
	}

	@Deprecated
	public static String initAClass1(Class c, Map<String, String> variableMap, Map<Class, String> classSimpleNameMap)
			throws Exception {
		return initAClass1(c, null, variableMap, classSimpleNameMap);
	}

	@Deprecated
	public static String initAClass1(Class c, String instanceName, Map<String, String> variableMap,
			Map<Class, String> classSimpleNameMap) throws Exception {
		StringBuilder sb = new StringBuilder();
		if (Common.hasDefaultConstruct(c)) {
			variableMap.put(Replacement.TMP_CLASS_NAME.name(), getClassName1(c, classSimpleNameMap));
			variableMap.put(Replacement.TMP_INSTANCE_NAME.name(),
					instanceName != null ? instanceName : Common.getInstanceName(c));
			String instanceLine = Common.replaceAllKeyWord(Syntax.NEW_INSTANCE_LINE, variableMap);
			sb.append(instanceLine);
		} else {
			String defaultValue = getDefaultVal(c);
			variableMap.put(Replacement.DEFAULT_VALUE.name(), defaultValue);
			variableMap.put(Replacement.TMP_INSTANCE_NAME.name(),
					instanceName != null ? instanceName : Common.getInstanceName(c));
			String initLine = Common.replaceAllKeyWord(Syntax.INIT_CLASS_WITH_DEFAULT_VALUE, variableMap);
			sb.append(initLine);
		}
		return sb.toString();
	}

	public static String initAClass(Class c, CodeGenContext context) throws Exception {
		return initAClass(c, null, context);
	}

	public static String initAClass(Class c, String instanceName, CodeGenContext context) throws Exception {
		return initAClass(c, instanceName, false, context);
	}

	public static String initAClass(Class c, Constructor ct, String instanceName, boolean hasClassNameDeclare,
			CodeGenContext context) throws Exception {
		StringBuilder sb = new StringBuilder();
		if (context.getAbstractConcreteClassMap().get(c) != null) {
			AbstractConcreteClassMapping acMap = context.getAbstractConcreteClassMap().get(c);
			if (hasClassNameDeclare) {
				context.getVariableMap().put(Replacement.SEPERATOR.name(), " ");
				context.getVariableMap().put(Replacement.ABSTRACT_CLASS_NAME.name(), getClassName(c, context));
			} else {
				context.getVariableMap().put(Replacement.ABSTRACT_CLASS_NAME.name(), "");
				context.getVariableMap().put(Replacement.SEPERATOR.name(), "");
			}
			if (instanceName == null) {
				instanceName = acMap.getAbstractClassInstanceName();
				if (instanceName == null) {
					instanceName = Common.getInstanceName(acMap.getAbstractClass());
				}
			}
			context.getVariableMap().put(Replacement.ABSTRACT_INSTANCE_NAME.name(), instanceName);
			context.getVariableMap().put(Replacement.TMP_CLASS_NAME.name(),
					getClassName(acMap.getConcreteClass(), context));
			String initLine = null;
			if (acMap.getConcreteCFactoryMethod() != null) {
				context.getVariableMap().put(Replacement.METHOD_NAME.name(),
						acMap.getConcreteCFactoryMethod().getName());
				if (acMap.getConcreteCFactoryMethod().getParameterCount() > 0) {
					context.getVariableMap().put(Replacement.METHOD_PARAMS_VARIABLE.name(),
							Common.genMethodParamsVariable(acMap.getMethodParamVariables()));
				} else {
					context.getVariableMap().put(Replacement.METHOD_PARAMS_VARIABLE.name(), "");
				}
				initLine = Common.replaceAllKeyWord(Syntax.INIT_CLASS_WITH_CONCRETE_LINE, context.getVariableMap());
			} else {
				if (c.isInterface()) {
					Class concreteClass = acMap.getConcreteClass();
					if (Common.hasConstruct(concreteClass)) {
						if (Common.hasDefaultConstruct(concreteClass)) {
							context.getVariableMap().put(Replacement.METHOD_PARAMS_VARIABLE.name(), "");
							initLine = Common.replaceAllKeyWord(Syntax.ABSTRACT_NEW_INSTANCE_LINE_WITH_DECLARE_OR_NOT,
									context.getVariableMap());
						} else {
							ct = ct == null ? Common.tryGetConstructorHasPrimitiveParam(concreteClass) : ct;
							List<java.lang.String> params = new ArrayList<>();
							if (ct != null) {
								Class cts[] = ct.getParameterTypes();
								for (Class pt : cts) {
									String dv = Common.getDefaultVal(pt);
									params.add(dv);
								}
							} else {
								ct = concreteClass.getConstructors()[0];
								Parameter pts[] = ct.getParameters();
								for (Parameter p : pts) {
									params.add(p.getName());
								}
							}
							String paramArray[] = params.toArray(new String[params.size()]);
							context.getVariableMap().put(Replacement.METHOD_PARAMS_VARIABLE.name(),
									Common.genMethodParamsVariable(paramArray));
							initLine = Common.replaceAllKeyWord(Syntax.ABSTRACT_NEW_INSTANCE_LINE_WITH_DECLARE_OR_NOT,
									context.getVariableMap());
						}
					}
				}
			}
			sb.append(initLine);
			Common.importC(acMap.getConcreteClass(), context);
		} else {
			Class concreteClass = null;
			if (c.isInterface()) {
				concreteClass = getDefaultConcreteClass(c);
			}
			if (Common.hasConstruct(c) || (concreteClass != null && Common.hasConstruct(concreteClass))) {
				if (hasClassNameDeclare) {
					context.getVariableMap().put(Replacement.SEPERATOR.name(), " ");
					context.getVariableMap().put(Replacement.ABSTRACT_CLASS_NAME.name(), getClassName(c, context));
					context.getVariableMap().put(Replacement.TMP_CLASS_NAME.name(),
							getClassName(c.isInterface() ? concreteClass : c, context));
					context.getVariableMap().put(Replacement.ABSTRACT_INSTANCE_NAME.name(),
							instanceName != null ? instanceName : Common.getInstanceName(c));
					if (Common.hasDefaultConstruct(c)
							|| (concreteClass != null && Common.hasConstruct(concreteClass))) {
						context.getVariableMap().put(Replacement.METHOD_PARAMS_VARIABLE.name(), "");
						String instanceLine = Common.replaceAllKeyWord(
								Syntax.ABSTRACT_NEW_INSTANCE_LINE_WITH_DECLARE_OR_NOT, context.getVariableMap());
						sb.append(instanceLine);
					} else {
						ct = ct == null
								? Common.tryGetConstructorHasPrimitiveParam(!c.isInterface() ? c : concreteClass)
								: ct;
						List<java.lang.String> params = new ArrayList<>();
						if (ct != null) {
							Class cts[] = ct.getParameterTypes();
							for (Class pt : cts) {
								String dv = Common.getDefaultVal(pt);
								params.add(dv);
							}
						} else {
							ct = !c.isInterface() ? c.getConstructors()[0] : concreteClass.getConstructors()[0];
							Parameter pts[] = ct.getParameters();
							for (Parameter p : pts) {
								params.add(p.getName());
							}
						}
						String paramArray[] = params.toArray(new String[params.size()]);
						context.getVariableMap().put(Replacement.METHOD_PARAMS_VARIABLE.name(),
								Common.genMethodParamsVariable(paramArray));
						String instanceLine = Common.replaceAllKeyWord(
								Syntax.ABSTRACT_NEW_INSTANCE_LINE_WITH_DECLARE_OR_NOT, context.getVariableMap());
						sb.append(instanceLine);
					}
				} else {
					context.getVariableMap().put(Replacement.TMP_CLASS_NAME.name(),
							getClassName(c.isInterface() ? concreteClass : c, context));
					context.getVariableMap().put(Replacement.TMP_INSTANCE_NAME.name(),
							instanceName != null ? instanceName : Common.getInstanceName(c));
					String instanceLine = Common.replaceAllKeyWord(Syntax.NEW_INSTANCE_LINE, context.getVariableMap());
					sb.append(instanceLine);
				}
			} else {
				String defaultValue = getDefaultVal(c);
				context.getVariableMap().put(Replacement.DEFAULT_VALUE.name(), defaultValue);
				context.getVariableMap().put(Replacement.TMP_INSTANCE_NAME.name(),
						instanceName != null ? instanceName : Common.getInstanceName(c));
				if (hasClassNameDeclare) {
					context.getVariableMap().put(Replacement.SEPERATOR.name(), " ");
					context.getVariableMap().put(Replacement.TMP_CLASS_NAME.name(), c.getSimpleName());
				} else {
					context.getVariableMap().put(Replacement.SEPERATOR.name(), "");
					context.getVariableMap().put(Replacement.TMP_CLASS_NAME.name(), "");
				}
				String initLine = Common.replaceAllKeyWord(Syntax.INIT_CLASS_WITH_DEFAULT_VALUE,
						context.getVariableMap());
				sb.append(initLine);
			}
		}
		return sb.toString();
	}

	public static String initAClass(Class c, String instanceName, boolean hasClassNameDeclare, CodeGenContext context)
			throws Exception {
		return initAClass(c, null, instanceName, hasClassNameDeclare, context);
	}

	public static Constructor tryGetConstructorHasPrimitiveParam(Class c) {
		Constructor ct = null;
		Constructor[] cc = c.getConstructors();
		for (Constructor cte : cc) {
			boolean allPrimitiveTypes = true;
			Class pts[] = cte.getParameterTypes();
			for (Class p : pts) {
				if (Common.isComplexType(p)) {
					allPrimitiveTypes = false;
				}
			}
			if (allPrimitiveTypes) {
				ct = cte;
			}
		}
		return ct;
	}

	public static boolean hasConstruct(Class c) {
		boolean b = false;
		Constructor[] cc = c.getConstructors();
		b = cc.length > 0 ? true : false;
		return b;
	}

	public static boolean hasDefaultConstruct(Class c) {
		boolean b = false;
		Constructor[] cc = c.getConstructors();
		for (Constructor cons : cc) {
			if (cons.getParameterCount() == 0) {
				b = true;
				break;
			}
		}
		return b;
	}

	public static String getDefaultCreateMethodName(Class c) {
		String defaultCreateMethodName = "create" + c.getSimpleName();
		return defaultCreateMethodName;
	}

	@Deprecated
	public static String getComplexTypeCreateMethodName(Class target, List<String> createdMethodName) {
		String methodName = "create" + target.getSimpleName();
		int sameMethodNameCound = 0;
		for (String name : createdMethodName) {
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

	public static String getComplexTypeCreateMethodName(Class target, CodeGenContext context) {
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

	public static String getMethodName(String rootMethodName, CodeGenContext context) {
		String methodName = rootMethodName;
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

	public static String getDefaultMockClassName(Class c, CodeGenContext context) {
		return c.getName() + "Mock";
	}

	public static String getClassName(Class c, CodeGenContext context) {
		String name = null;
		Map<Class, String> classSimpleName = context.getClassSimpleNameMap();
		if (!classSimpleName.containsKey(c)) {
			boolean ambiguousClass = false;
			ambiguousClass = classSimpleName.entrySet().stream().anyMatch(e -> {
				if (c.getSimpleName().equals(e.getValue().trim())) {
					return true;
				}
				return false;
			});
			if ((context != null && context.isGenFullQualifierName()) || ambiguousClass) {
				classSimpleName.put(c, c.getName());
			} else {
				classSimpleName.put(c, c.getSimpleName());
			}
		}
		name = classSimpleName.get(c);
		return name;
	}

	@Deprecated
	/**
	 * use getClassName(Class c, CodeGenContext context) instead
	 * 
	 * @param c
	 * @param classSimpleName
	 * @return
	 */
	public static String getClassName1(Class c, Map<Class, String> classSimpleName) {
		String name = null;
		if (!classSimpleName.containsKey(c)) {
			boolean ambiguousClass = false;
			ambiguousClass = classSimpleName.entrySet().stream().anyMatch(e -> {
				if (c.getSimpleName().equals(e.getValue().trim())) {
					return true;
				}
				return false;
			});
			if (ambiguousClass) {
				classSimpleName.put(c, c.getName());
			} else {
				classSimpleName.put(c, c.getSimpleName());
			}
		}
		name = classSimpleName.get(c);
		return name;
	}

	public static Class getConcreteClass(Class c, CodeGenContext context) {
		Class concreteClass = c;
		if (context.getAbstractConcreteClassMap().get(c) != null) {
			AbstractConcreteClassMapping acMap = context.getAbstractConcreteClassMap().get(c);
			concreteClass = acMap.getConcreteClass();
		} else if (c.isInterface() || Common.isAbstract(c)) {
			concreteClass = getDefaultConcreteClass(c);
		}
		return concreteClass;
	}

	public static Class[] getConcreteClass(Class cs[], CodeGenContext context) {
		Class ccs[] = null;
		List<Class> list = new ArrayList<>();
		for (Class c : cs) {
			Class cc = getConcreteClass(c, context);
			if (cc != null) {
				list.add(cc);
			} else {
				System.out.println(String.format("cannot get concrete class for %s, return class itself", c.getName()));
				list.add(c);
			}
		}
		ccs = new Class[list.size()];
		return list.toArray(ccs);
	}

	public static boolean isBlank(String s) {
		return s == null || "".equals(s.trim());
	}

	public static Class getDefaultConcreteClass(Class interfaceC) {
		Class concreteC = null;
		if (!interfaceC.isInterface() && !isAbstract(interfaceC) && !interfaceC.isArray()) {
			return interfaceC;
		}
		if (List.class.isAssignableFrom(interfaceC) || AbstractList.class.isAssignableFrom(interfaceC)
				|| Collection.class == interfaceC) {
			concreteC = ArrayList.class;
		} else if (Map.class.isAssignableFrom(interfaceC) || AbstractMap.class.isAssignableFrom(interfaceC)) {
			concreteC = HashMap.class;
		} else if (Set.class.isAssignableFrom(interfaceC) || AbstractSet.class.isAssignableFrom(interfaceC)) {
			concreteC = HashSet.class;
		}
		return concreteC;
	}

	/*
	 * public static MethodCallingVariable
	 * genIfNotExistAndGetClassCreateMethod(Class c, Class genericTypes[], boolean
	 * forceGenerate,Map<MethodCallingKey, MethodCallingVariable>
	 * complexTypeCreateMapping, List<String> createdMethodName, Map<Class, String>
	 * classSimpleName, Map<String, String> variableMap) throws Exception{ Class
	 * concreteC = getDefaultConcreteClass(c); if(concreteC == null){ throw new
	 * Exception(String.format( "cannot get the concrete class for %s",
	 * c.getName())); } MethodCallingVariable mc = null; String
	 * defaultCreateMethodName = getDefaultCreateMethodName(c); MethodCallingKey
	 * defaultMethodCallingKey = new MethodCallingKey(c, defaultCreateMethodName);
	 * MethodCallingKey newMethodCallingKey = defaultMethodCallingKey;
	 * if(forceGenerate ||
	 * !complexTypeCreateMapping.containsKey(defaultMethodCallingKey)){ String
	 * methodBody = ObjSetCodeGenAfterCreateMethod.generateCode(concreteC,
	 * genericTypes); mc = new MethodCallingVariable(); mc.setC(c);
	 * mc.setMethodBody(methodBody); String methodName =
	 * getComplexTypeCreateMethodName(c, createdMethodName);
	 * mc.setMethodName(methodName); //mc.setParams(new Object[]{});
	 * newMethodCallingKey = new MethodCallingKey(c, methodName);
	 * complexTypeCreateMapping.put(newMethodCallingKey, mc);
	 * variableMap.put(Replacement.PRIVILEDGE_MODIFIER.name(), "private");
	 * if(!variableMap.containsKey(Replacement.STATIC_MODIFIER.name())){
	 * variableMap.put(Replacement.STATIC_MODIFIER.name(), ""); }
	 * variableMap.put(Replacement.METHOD_RETURN_TYPE.name(), getClassName(c,
	 * classSimpleName)); variableMap.put(Replacement.METHOD_NAME.name(),
	 * mc.getMethodName());
	 * variableMap.put(Replacement.METHOD_PARAMS_DECLARE.name(), "");
	 * variableMap.put(Replacement.METHOD_BODY.name(), mc.getMethodBody()); String
	 * instanceName = Common.getInstanceName(concreteC);
	 * variableMap.put(Replacement.METHOD_RETURN_VALUE.name(), instanceName); String
	 * createComplexTypeMethod = Common.replaceAllKeyWord(Syntax.NORMAL_METHOD,
	 * variableMap); mc.setMethodBlock(createComplexTypeMethod);
	 * createdMethodName.add(methodName); } mc =
	 * complexTypeCreateMapping.get(newMethodCallingKey); return mc; }
	 */
	/*
	 * public static MethodCallingVariable
	 * genIfNotExistAndGetClassCreateMethod(Class c, boolean
	 * forceGenerate,Map<MethodCallingKey, MethodCallingVariable>
	 * complexTypeCreateMapping, List<String> createdMethodName, Map<Class, String>
	 * classSimpleName, Map<String, String> variableMap) throws Exception{ return
	 * Common.genIfNotExistAndGetClassCreateMethod(c, null, forceGenerate,
	 * complexTypeCreateMapping, createdMethodName, classSimpleName, variableMap); }
	 */
	public static Class getGenericTypeIfOnlyOne(String typeName) {
		Class g = null;
		String genericType = null;
		int start = typeName.indexOf("<");
		int end = typeName.indexOf(">");
		genericType = typeName.substring(start + 1, end);
		try {
			g = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner().loadClass(genericType, true);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return g;
	}

	public static MethodCallingVariable genIfNotExistAndGetClassCreateMethod(Class c, boolean forceGenerate,
			CodeGenContext context) throws Exception {
		return genIfNotExistAndGetClassCreateMethod(c, null, forceGenerate, context);
	}

	public static MethodCallingVariable genIfNotExistAndGetClassCreateMethod(Class c, Class genericTypes[],
			boolean forceGenerate, CodeGenContext context) throws Exception {
		MethodCallingVariable mc = null;
		String defaultCreateMethodName = getDefaultCreateMethodName(c);
		MethodCallingKey defaultMethodCallingKey = new MethodCallingKey(c, defaultCreateMethodName);
		MethodCallingKey newMethodCallingKey = defaultMethodCallingKey;
		if (forceGenerate || !context.getComplexTypeCreateMapping().containsKey(defaultMethodCallingKey)) {
			Class concreteClass = c;
			if (context.getAbstractConcreteClassMap().get(c) != null) {
				AbstractConcreteClassMapping acMap = context.getAbstractConcreteClassMap().get(c);
				concreteClass = acMap.getConcreteClass();
			} else if (c.isInterface() || Common.isAbstract(c)) {
				concreteClass = getDefaultConcreteClass(c);
			}
			if (concreteClass == null) {
				System.out.println(String.format("cannot get concrete class for class %s null", c));
				// leave the instance null, and comment as 'dont know the concrete class, pls
				// complete the concrete class initialization
				return mc;
			}
			// for factory method calling, TODO
			String methodBody = PojoSetterGenerator.generateCode(concreteClass, genericTypes, context);
			mc = new MethodCallingVariable();
			mc.setMethodBody(methodBody);
			String methodName = getComplexTypeCreateMethodName(concreteClass, context);
			mc.setMethodName(methodName);
			// mc.setParams(new Object[]{});
			newMethodCallingKey = new MethodCallingKey(concreteClass, methodName);
			context.getComplexTypeCreateMapping().put(newMethodCallingKey, mc);
			context.getVariableMap().put(Replacement.METHOD_RETURN_TYPE.name(), Common.getClassName(c, context));
			context.getVariableMap().put(Replacement.METHOD_NAME.name(), mc.getMethodName());
			context.getVariableMap().put(Replacement.METHOD_PARAMS_DECLARE.name(), "");
			context.getVariableMap().put(Replacement.METHOD_BODY.name(), mc.getMethodBody());
			String instanceName = Common.getInstanceName(concreteClass);
			context.getVariableMap().put(Replacement.METHOD_RETURN_VALUE.name(), instanceName);
			context.getVariableMap().put(Replacement.THROWS_EXCEPTION.name(), "");
			String createComplexTypeMethod = Common.replaceAllKeyWord(Syntax.NORMAL_METHOD, context.getVariableMap());
			context.getCreatedComplexTypeMethod().append(createComplexTypeMethod).append(Syntax.NEW_LINE);
			context.getCreatedMethodName().add(methodName);
		}
		mc = context.getComplexTypeCreateMapping().get(newMethodCallingKey);
		return mc;
	}

	public static String readAsStr(File f) throws IOException {
		FileInputStream fis = new FileInputStream(f);
		return readAsStr(fis);
	}

	public static byte[] readAllInputStream(InputStream is) throws IOException {
		int nRead;
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		byte[] data = new byte[16384];
		while ((nRead = is.read(data, 0, data.length)) != -1) {
			bao.write(data, 0, nRead);
		}
		byte bArray[] = bao.toByteArray();
		return bArray;
	}

	public static String readAsStr(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = in.readLine()) != null) {
				sb.append(line).append("\r\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		return sb.toString();
	}

	public static void write(File f, String fileStr) throws IOException {
		f.getParentFile().mkdirs();
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(fileStr.getBytes());
		fos.flush();
		fos.close();
	}

	public static void writeInputStreamAllByte(InputStream is, OutputStream os) throws IOException {
		byte[] buffer = new byte[2048];
		// InputStream is = part.getInputStream();
		int read = 0;
		// FileOutputStream fileOutputStream = new FileOutputStream(fileName1);
		while ((read = is.read(buffer)) != -1) {
			os.write(buffer, 0, read);
		}
		is.close();
		os.flush();
		os.close();
		return;
	}

	public static boolean isFunctionInterface(Class functionInterface) {
		boolean b = false;
		Method ms[] = Common.getInterfaceAbstractMethods(functionInterface);
		if (ms != null && ms.length == 1) {
			b = true;
		}
		return b;
	}

	public static Method[] getInterfaceAbstractMethods(Class functionInterface) {
		Method ms[] = null;
		if (functionInterface.isInterface()) {
			List<Method> list = Arrays.asList(Common.getAllDeclaredPublicMethodsForTest(functionInterface)).stream()
					.filter(m -> {
						return Common.isAbstract(m);
					}).collect(Collectors.toList());
			ms = list.toArray(new Method[list.size()]);
		}
		return ms;
	}

	public static String genFunctionImplementation(Class functionInterface, String... params) {
		StringBuilder sb = new StringBuilder();
		Method ms[] = Common.getInterfaceAbstractMethods(functionInterface);
		if (ms != null && ms.length == 1) {
			Method m = ms[0];
			String paramStr = Common.genMethodParamsVariable(params);
			sb.append("(").append(paramStr).append(")").append("{").append(Syntax.NEW_LINE);
			if (!Common.isVoid(m)) {
				sb.append("return ").append(Syntax.END_OF_LINE);
			}
			sb.append("}");
		}
		return sb.toString();
	}

	public static String getInstanceName(Class<?> rt, Map<Class<?>, Short> map) {
		short num;
		if (map.containsKey(rt)) {
			num = map.get(rt);
			num++;
		} else {
			num = 0;
		}
		map.put(rt, num);
		String name = Common.getInstanceName(rt) + (num == 0 ? "" : Short.toString(num));
		return name;
	}

	public static Class getClass(Type type) {
		if (type instanceof Class) {
			return (Class) type;
		} else if (type instanceof ParameterizedType) {
			return getClass(((ParameterizedType) type).getRawType());
		} else if (type instanceof GenericArrayType) {
			java.lang.reflect.Type componentType = ((GenericArrayType) type).getGenericComponentType();
			Class componentC = getClass(componentType);
			if (componentC != null) {
				java.lang.Object object = Array.newInstance(componentC, 0);
				return object.getClass();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static <T> List<Class<?>> getTypeArguments(Class<T> baseClass, Class<? extends T> childClass) {
		Map<Type, Type> resolvedTypes = new HashMap<>();
		Type type = childClass;
		while (type != null && !getClass(type).equals(baseClass)) {
			if (type instanceof Class) {
				Class tc = (Class) type;
				// type = tc.getGenericSuperclass();
				Type itype[] = tc.getGenericInterfaces();
				type = itype[0];
			} else {
				ParameterizedType pt = (ParameterizedType) type;
				java.lang.reflect.Type rawType = pt.getRawType();
				Class rawT = (Class) rawType;
				java.lang.reflect.Type actualTypeArgs[] = pt.getActualTypeArguments();
				java.lang.reflect.TypeVariable typeParams[] = rawT.getTypeParameters();
				for (int i = 0; i < actualTypeArgs.length; i++) {
					resolvedTypes.put(typeParams[i], actualTypeArgs[i]);
				}
				if (!rawT.equals(baseClass)) {
					type = rawT.getGenericSuperclass();
				} else {
				}
			}
		}
		Type actualTypeArgs[];
		if (type instanceof Class) {
			actualTypeArgs = ((Class) type).getTypeParameters();
		} else {
			actualTypeArgs = ((ParameterizedType) type).getActualTypeArguments();
		}
		List<java.lang.Class<?>> list = new ArrayList<>();
		for (Type baseType : actualTypeArgs) {
			while (resolvedTypes.containsKey(baseType)) {
				baseType = resolvedTypes.get(baseType);
			}
			list.add(getClass(baseType));
		}
		return list;
	}

	public static List<java.lang.Class<?>> getInterfaceGenericTypes(Class c) {
		List<java.lang.Class<?>> list = new ArrayList<>();
		Type type = getClass(c);
		if (type instanceof Class) {
			Class tc = (Class) type;
			// type = tc.getGenericSuperclass();
			Type itypes[] = tc.getGenericInterfaces();
			for (Type t : itypes) {
				System.out.print(t.getTypeName());
				if (t instanceof ParameterizedType) {
					ParameterizedType pt = (ParameterizedType) t;
					Type ts[] = pt.getActualTypeArguments();
					for (Type t1 : ts) {
						if (t instanceof TypeVariable) {
							TypeVariable tv = (TypeVariable) t;
							System.out.print(tv.getName());
						} else {
							list.add(getClass(t1));
						}
					}
				}
			}
		}
		return list;
	}

	public Class[] getRepositorySuperClassActualParam(Class c) {
		Type t = c.getGenericSuperclass();
		if (t instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) t;
			Type ts[] = pt.getActualTypeArguments();
			if (ts != null) {
				Class cces[] = new Class[ts.length];
				cces = Arrays.copyOf(ts, ts.length, Class[].class);
				return cces;
			} else {
				return null;
			}
		}
		return null;
	}

	public static int createRandom(int min, int max) {
		java.util.concurrent.ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
		int i = threadLocalRandom.nextInt(min, max + 1);
		return i;
	}

	public static void main(String args[]) throws MalformedURLException {
		Common.getRunningJar();
		Map<String, String> variableMap = new HashMap<>();
		variableMap.put("abc", "handlerClass");
		variableMap.put("abc.11shandlerClass", "sss");
		// System.out.println(replacePlaceHolder("${abc.11s${abc}}",
		// variableMap));
		System.out.println(int.class.getSimpleName());
		
		try {
			List<String> names = cg.util.Common.getClassesNameFromJar(CodeGenContext.getInstance(), new URL[] {new URL("file://D:\\Java\\jdk1.8.0_131\\jre\\lib\\rt.jar")});
			StringBuilder sb = new StringBuilder();
			names = names.stream().filter(e -> e.startsWith("java.") || e.startsWith("org.") || e.startsWith("javax.")).sorted((String str1, String str2)-> {
				if(str1.startsWith("java.") && str2.startsWith("java.")) {
					if(str1.startsWith("java.lang") && !str2.startsWith("java.lang")) {
						return -1;
					}else if(!str1.startsWith("java.lang") && str2.startsWith("java.lang")) {
						return 1;
					}else {
						if(str1.startsWith("java.util") && !str2.startsWith("java.util")) {
							return -1;
						}else if(!str1.startsWith("java.util") && str2.startsWith("java.util")) {
							return 1;
						}else {
							if(str1.startsWith("java.io") && !str2.startsWith("java.io")) {
								return -1;
							}else if(!str1.startsWith("java.io") && str2.startsWith("java.io")) {
								return 1;
							}else {
								return str1.compareTo(str2);
							}
						}
					}
				}else {
					return str1.compareTo(str2);
				}
			}).collect(Collectors.toList());
			names.forEach(e-> {
				sb.append(e).append("\r\n");
			 });

			File file = new File("d:\\jreclz.txt");
			Common.write(file,sb.toString());

		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * try { URL url =new
		 * URL("D:\\androidMavenRepository\\junit\\junit\\3.8.1\\junit-3.8.1.jar");
		 * getClassesFromJar(new URL[]{url}, CodeGenContext.newInstance()); } catch
		 * (ClassNotFoundException | IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
	}

	public List<Class> getMethodReturnGenericTypes(Method m) {
		List<Class> list = new ArrayList<>();
		Type returnType = m.getGenericReturnType();
		if (returnType instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) returnType;
			Type ts[] = pt.getActualTypeArguments();
			for (Type t1 : ts) {
				if (t1 instanceof Class) {
					list.add((Class) t1);
				}
			}
		}
		return list;
	}

	public static void setModifier(int mod, CodeGenContext context) {
		context.getVariableMap().put(Replacement.PRIVILEDGE_MODIFIER.name(), "");
		context.getVariableMap().put(Replacement.STATIC_MODIFIER.name(), "");
		context.getVariableMap().put(Replacement.ABSTRACT_MODIFIER.name(), "");
		context.getVariableMap().put(Replacement.FINAL_MODIFIER.name(), "");
		context.getVariableMap().put(Replacement.SYNC_MODIFIER.name(), "");
		context.getVariableMap().put(Replacement.VOLATILE_MODIFIER.name(), "");
		if (Modifier.isPublic(mod)) {
			context.getVariableMap().put(Replacement.PRIVILEDGE_MODIFIER.name(), "public");
		}
		if (Modifier.isPrivate(mod)) {
			context.getVariableMap().put(Replacement.PRIVILEDGE_MODIFIER.name(), "private");
		}
		if (Modifier.isProtected(mod)) {
			context.getVariableMap().put(Replacement.PRIVILEDGE_MODIFIER.name(), "protected");
		}

		if (Modifier.isStatic(mod)) {
			context.getVariableMap().put(Replacement.STATIC_MODIFIER.name(), " static ");
		}
		if (Modifier.isAbstract(mod)) {
			context.getVariableMap().put(Replacement.ABSTRACT_MODIFIER.name(), " abstract ");
		}
		if (Modifier.isFinal(mod)) {
			context.getVariableMap().put(Replacement.FINAL_MODIFIER.name(), " final ");
		}
		if (Modifier.isVolatile(mod)) {
			context.getVariableMap().put(Replacement.VOLATILE_MODIFIER.name(), " volatile ");
		}
		if (Modifier.isSynchronized(mod)) {
			context.getVariableMap().put(Replacement.SYNC_MODIFIER.name(), " synchronized ");
		}
	}

	public static File getFileByPackage(String baseDir, String packName) {
		String regex = "\\.";
		String path = null;
		try {
			path = baseDir + File.separator + packName.replaceAll(regex, "\\" + File.separator);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new File(path);
	}

	public static void listAllFiles(File dir, String fileNameRegex, List<File> allFiles) {
		File fileArray[] = dir.listFiles();
		for (File file : fileArray) {
			if (file.isFile()) {
				String s = file.getName();
				boolean b = s.matches(fileNameRegex);
				if (b) {
					allFiles.add(file);
				}
			} else {
				listAllFiles(file, fileNameRegex, allFiles);
			}
		}
	}

	public static boolean isJarFile(File jarfile) {
		String fileName = jarfile.getName().toLowerCase();
		if (!fileName.endsWith("jar")) {
			return false;
		}
		try {
			ZipFile file = new ZipFile(jarfile);
			Enumeration<? extends ZipEntry> e = file.entries();
			if (e.hasMoreElements()) {
				ZipEntry entry = e.nextElement();
//				System.out.println(entry.getName());
				return true;
			}
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	public static List<String> getClassesNameFromJar(CodeGenContext context, URL... uls)
			throws IOException, ClassNotFoundException {
		return getClassesNameFromJar(uls, null, context);
	}

	public static List<String> getClassesNameFromJar(URL uls[], String clzName, CodeGenContext context) {
		List<String> list = new ArrayList<>();
		for (int i = 0; i < uls.length; i++) {
			JarFile jarFile = null;
			try {
				jarFile = new JarFile(uls[i].getPath());
				list.addAll(getJarFileClassNames(jarFile, context));
			} catch (IOException e2) {
			}
		}
		return list;
	}

	public static List<String> getJarFileClassNames(JarFile jarFile, CodeGenContext context) {
		List<String> list = new ArrayList<>();
		if (jarFile != null) {
			Enumeration<JarEntry> e = jarFile.entries();
			while (e.hasMoreElements()) {
				JarEntry entry = e.nextElement();
				String name = entry.getName();
				if (name.endsWith(".class")) {
					CharSequence charSequence = null;
					name = name.replace(".class", "");
					name = name.replace("/", ".");
					if ("junit.framework.TestCase".equals(name)) {
						System.out.println("***");
					}
					list.add(name);
				}
			}
			try {
				jarFile.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return list;
	}

	public static Class[] getClassesFromJar(URL uls[], String clzName, CodeGenContext context) {
		List<Class> list = new ArrayList<>();
//		URLClassLoader cl = URLClassLoader.newInstance(uls, context.getUcl());
		for (int i = 0; i < uls.length; i++) {
			JarFile jarFile = null;
			try {
				jarFile = new JarFile(uls[i].getPath());
			} catch (IOException e2) {
			}
			if (jarFile != null) {
				Enumeration<JarEntry> e = jarFile.entries();
				while (e.hasMoreElements()) {
					JarEntry entry = e.nextElement();
					String name = entry.getName();
					if (name.endsWith(".class")) {
						CharSequence charSequence = null;
						name = name.replace(".class", "");
						name = name.replace("/", ".");
						if ("junit.framework.TestCase".equals(name)) {
							System.out.println("***");
						}
						Class c = null;
						try {
							c = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner().loadClass(name,
									true);
//							c = cl.loadClass(name);
							if (c != null) {
								String s = c.getCanonicalName();
								if (clzName != null && s != null && s.endsWith(clzName)) {
									list.add(c);
								} else if (clzName == null) {
									list.add(c);
								}
							}
						} catch (Exception e1) {
							// e1.printStackTrace();
						}
					}
				}
				try {
					jarFile.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return (Class[]) list.toArray(new Class[list.size()]);
	}

	public static URL getJarUrlByResource(String resourceName) throws Exception {
		URL url = Common.class.getClassLoader().getResource(resourceName);
		boolean b = false;
		while (!b) {
			String p = url.getProtocol();
			if ("jar".equalsIgnoreCase(p)) {
				return url;
			}
			File pf = new File(url.getPath()).getParentFile();
			url = new URL(pf.getAbsolutePath());
		}
		return null;
	}
	public static List<Constructor> getConstructorWithPrimitiveArgs(Class<?> clz) {
		List<Constructor> list = new ArrayList<>();
		Constructor constructorArray[] = clz.getConstructors();
		for(Constructor constructor : constructorArray){
			Class clzArray[] = constructor.getParameterTypes();
			boolean b = true;
			for(Class pt : clzArray){
				if(!Common.isPrimitiveType(pt)){
					b = false;
					break;
				}
			}
			if(b){
				list.add(constructor);
			}
		}
	    return list;
	}
	
}
