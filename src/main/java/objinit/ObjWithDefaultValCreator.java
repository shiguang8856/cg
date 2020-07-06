package objinit;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import cg.complexobj.CodeGenContext;

public class ObjWithDefaultValCreator {
	public static Object getDefaultVal(Class c) {
		Object defaultVal = null;
		if (c != null) {
			String typeName = c.getName();
			if (typeName.equals("java.lang.String")) {
				defaultVal = "";
			}
			if (typeName.equals("java.lang.Boolean") || typeName.equals("boolean")) {
				defaultVal = Boolean.FALSE;
			}
			if (typeName.equals("java.lang.Long") || typeName.equals("long")) {
				defaultVal = Long.valueOf(1L);
			}
			if (typeName.equals("java.lang.Double") || typeName.equals("double")) {
				defaultVal = Double.valueOf(0d);
			}
			if (typeName.equals("java.lang.Integer") || typeName.equals("int")) {
				defaultVal = Integer.valueOf(1);
			}
			if (typeName.equals("java.lang.Short") || typeName.equals("short")) {
				defaultVal = Short.valueOf((short) 1);
			}
			if (typeName.equals("java.lang.Float") || typeName.equals("float")) {
				defaultVal = Float.valueOf(1f);
			}
			if (typeName.equals("java.lang.Character") || typeName.equals("char")) {
				return 'a';
			}
			if (typeName.equals("java.util.Date")) {
				defaultVal = new Date();
			}
			if (typeName.equals("java.sql.Date")) {
				defaultVal = new java.sql.Date(new Date().getTime());
			}
			if (typeName.startsWith("java.util.Map")) {
				defaultVal = new HashMap();
			}
			if (typeName.startsWith("java.util.Set")) {
				defaultVal = new HashSet();
			}
			if (typeName.equals("java.math.BigInteger")) {
				defaultVal = java.math.BigInteger.valueOf(1);
			}
			if (typeName.equals("java.math.BigDecimal")) {
				defaultVal = java.math.BigDecimal.valueOf(0);
			}
			if (typeName.equals("javax.xml.datatype.XMLGregorianCalendar")) {
				defaultVal = toXMLGregorianCalendar(new java.util.Date());
			}
			if (c != null && c.isEnum()) {
				defaultVal = getEnum(c);
			}
		}
		return defaultVal;
	}

	private static DatatypeFactory df = null;
	static {
		try {
			df = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException dce) {
			throw new IllegalStateException("Exception", dce);
		}
	}

	public static XMLGregorianCalendar toXMLGregorianCalendar(long timeInMillis) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(timeInMillis);
		return df.newXMLGregorianCalendar(gc);
	}

	public static XMLGregorianCalendar toXMLGregorianCalendar(java.util.Date date) {
		if (date == null) {
			return null;
		} else {
			return toXMLGregorianCalendar(date.getTime());
		}
	}

	public static boolean isComplexType(Class c) {
		boolean b = false;
		if (c.isPrimitive() || c.isEnum()) {
			return b;
		}
		/*
		 * if (c.isArray()) { c = c.getComponentType(); }
		 */
		String typeName = c.getName();
		if (typeName.equals("java.lang.String")) {
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
		if (typeName.equals("java.util.Date")) {
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

	public static Object getEnum(Class c) {
		StringBuilder sb = new StringBuilder();
		String classNameForPrint = c.getName().replaceAll("\\$", "\\.");
		Object obj[] = c.getEnumConstants();
		return obj[0];
	}

	public <T> T newInstanceByConstructor(Class<T> qn) {
		T obj = null;
		if (!this.isComplexType(qn)) {
			obj = (T) getDefaultVal(qn);
			return obj;
		}
		try {
			obj = qn.newInstance();
		} catch (InstantiationException | IllegalAccessException e1) {
			Constructor<?> cts[] = qn.getConstructors();
			List<Constructor> list = Arrays.asList(cts);
			list.sort((c1, c2) -> {
				int c1ParamCount = c1.getParameterCount();
				int c2ParamCount = c1.getParameterCount();
				return c1ParamCount - c2ParamCount;
			});
			if (list.size() > 0) {
				Constructor<?> cs = list.get(0);
				// TODO has bug here
				Parameter pts[] = cs.getParameters();
				try {
					obj = (T) cs.newInstance();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					// e.printStackTrace();
				}
			}
		}
		return obj;
	}

	public static boolean isAbstractOrInterface(Class clzToTest) {
		boolean abs = false;
		if (!clzToTest.isPrimitive() && (clzToTest.isInterface() || isAbstract(clzToTest))) {
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

	public static Class getDefaultConcreteClass(Class interfaceC) {
		Class concreteC = null;
		if (!interfaceC.isInterface() && !isAbstract(interfaceC) && !interfaceC.isArray()) {
			return interfaceC;
		}
		if (List.class.isAssignableFrom(interfaceC) || AbstractList.class.isAssignableFrom(interfaceC)
				|| Iterable.class.isAssignableFrom(interfaceC)) {
			concreteC = ArrayList.class;
		} else if (Map.class.isAssignableFrom(interfaceC) || AbstractMap.class.isAssignableFrom(interfaceC)) {
			concreteC = HashMap.class;
		} else if (Set.class.isAssignableFrom(interfaceC) || AbstractSet.class.isAssignableFrom(interfaceC)) {
			concreteC = HashSet.class;
		}
		return concreteC;
	}

	class clzRef {
		Class c;
		Class referenceC;
		AtomicInteger passByTimes = new AtomicInteger(0);

		public AtomicInteger getPassByTimes() {
			return passByTimes;
		}

		public void setPassByTimes(AtomicInteger passByTimes) {
			this.passByTimes = passByTimes;
		}

		public Class getC() {
			return c;
		}

		public void setC(Class c) {
			this.c = c;
		}

		public Class getReferenceC() {
			return referenceC;
		}

		public void setReferenceC(Class referenceC) {
			this.referenceC = referenceC;
		}

		public clzRef(Class c, Class referenceC) {
			super();
			this.c = c;
			this.referenceC = referenceC;
			this.passByTimes.getAndSet(0);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((c == null) ? 0 : c.hashCode());
			result = prime * result + ((referenceC == null) ? 0 : referenceC.hashCode());
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
			clzRef other = (clzRef) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (c == null) {
				if (other.c != null)
					return false;
			} else if (!c.equals(other.c))
				return false;
			if (referenceC == null) {
				if (other.referenceC != null)
					return false;
			} else if (!referenceC.equals(other.referenceC))
				return false;
			return true;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("clzRef [");
			builder.append(c.getSimpleName());
			builder.append("-->");
			builder.append(referenceC.getSimpleName());
			builder.append("]");
			return builder.toString();
		}

		private ObjWithDefaultValCreator getOuterType() {
			return ObjWithDefaultValCreator.this;
		}
	}

	public Map<Class, AtomicInteger> getAllCircularDependencyRelatedClz(Map<clzRef, List<clzRef>> map) {
		List<Class> list = new ArrayList<>();
		map.entrySet().forEach(e -> {
			Class keyC = e.getKey().getC();
			Class keyRefC = e.getKey().getReferenceC();
			List<clzRef> valList = e.getValue();
			if (!list.contains(keyC))
				list.add(keyC);
			if (!list.contains(keyRefC))
				list.add(keyRefC);
			valList.forEach(e1 -> {
				if (!list.contains(e1.getC()))
					list.add(e1.getC());
				if (!list.contains(e1.getReferenceC()))
					list.add(e1.getReferenceC());
			});
		});
		Map<Class, AtomicInteger> circularDependencyRelatedClzMap = list.stream()
				.collect(Collectors.toMap(e -> e, e -> {
					return new AtomicInteger(0);
				}, (e1, e2) -> e1));
		return circularDependencyRelatedClzMap;
	}

	public Map<clzRef, List<clzRef>> getCircularDependencyPath(List<clzRef> clzRefList) {
		Map<clzRef, List<clzRef>> alldependencyPath = new HashMap<>();
		Map<clzRef, List<clzRef>> selfdependencyPath = clzRefList.stream().filter(cr -> {
			return cr.getC().equals(cr.getReferenceC());
		}).collect(Collectors.toMap(e -> clzRef.class.cast(e), e -> {
			return Stream.of(clzRef.class.cast(e)).collect(Collectors.toList());
		}, (e1, e2) -> e1));
		alldependencyPath.putAll(selfdependencyPath);
		List<clzRef> notSelfDependencyList = clzRefList.stream().filter(cr -> {
			if (!cr.getC().equals(cr.getReferenceC())) {
				return true;
			} else {
				return false;
			}
		}).collect(Collectors.toList());
		List<Class> distinctReferenceClz = notSelfDependencyList.stream().map(clzRef::getReferenceC).distinct()
				.collect(Collectors.toList());
		List<clzRef> startPointClzRefMayContainsCircularDependency = notSelfDependencyList.stream().filter(cr -> {
			return distinctReferenceClz.contains(cr.getC());
		}).distinct().collect(Collectors.toList());
		Map<clzRef, List<clzRef>> dependencyPath = new HashMap<>();
		startPointClzRefMayContainsCircularDependency.stream().forEach(cr -> {
			List<clzRef> allPath = new ArrayList<>();
			allPath.add(cr);
			dependencyPath.put(cr, allPath);
			getCircularDepencyPath(cr, null, notSelfDependencyList, dependencyPath);
			notSelfDependencyList.stream().forEach(d -> {
				d.getPassByTimes().set(0);
			});
		});
		dependencyPath.keySet().forEach(k -> {
			List<clzRef> tmpdependencyPath = dependencyPath.get(k);
			if (tmpdependencyPath.size() >= 2) {
				clzRef startclzRef = tmpdependencyPath.get(0);
				clzRef lastclzRef = tmpdependencyPath.get(tmpdependencyPath.size() - 1);
				if (startclzRef.getC().equals(lastclzRef.getReferenceC())) {
					alldependencyPath.put(k, tmpdependencyPath);
				}
			}
		});
		return alldependencyPath;
	}

	public void getCircularDepencyPath(clzRef startClzRef, clzRef nextClzRef, List<clzRef> notSelfDependencyList,
			Map<clzRef, List<clzRef>> dependencyPath) {
		if (nextClzRef == null) {
			startClzRef.getPassByTimes().incrementAndGet();
			List<clzRef> nextClzRefList = notSelfDependencyList.stream().filter(cr -> {
				return cr.getPassByTimes().get() == 0 && cr.getC().equals(startClzRef.getReferenceC());
			}).collect(Collectors.toList());
			nextClzRefList.forEach(cr -> {
				getCircularDepencyPath(startClzRef, cr, notSelfDependencyList, dependencyPath);
			});
		} else {
			nextClzRef.getPassByTimes().incrementAndGet();
			if (dependencyPath.containsKey(startClzRef)) {
				dependencyPath.get(startClzRef).add(nextClzRef);
			}
			if (nextClzRef.getReferenceC().equals(startClzRef.getC())) {
				return;
			}
			List<clzRef> nextClzRefList = notSelfDependencyList.stream().filter(cr -> {
				return cr.getPassByTimes().get() == 0 && cr.getC().equals(nextClzRef.getReferenceC());
			}).collect(Collectors.toList());
			nextClzRefList.forEach(cr -> {
				getCircularDepencyPath(startClzRef, cr, notSelfDependencyList, dependencyPath);
			});
		}
	}

	/*
	 * public void getCircularDepencyPath(clzRef startClzRef, clzRef nextClzRef,
	 * List<clzRef> notSelfDependencyList, Map<clzRef, List<clzRef>> dependencyPath)
	 * { if (nextClzRef == null) { startClzRef.getPassByTimes().incrementAndGet();
	 * List<clzRef> nextClzRefList = notSelfDependencyList.stream().filter(cr -> {
	 * return cr.getPassByTimes().get() == 0 &&
	 * cr.getC().equals(startClzRef.getReferenceC());
	 * }).collect(Collectors.toList()); nextClzRefList.forEach(cr -> {
	 * getCircularDepencyPath(startClzRef, cr, notSelfDependencyList,
	 * dependencyPath); }); } else { nextClzRef.getPassByTimes().incrementAndGet();
	 * if (dependencyPath.containsKey(startClzRef)) {
	 * dependencyPath.get(startClzRef).add(nextClzRef); } if
	 * (nextClzRef.getReferenceC().equals(startClzRef.getC())) { return; }
	 * List<clzRef> nextClzRefList = notSelfDependencyList.stream().filter(cr -> {
	 * return cr.getPassByTimes().get() == 0 &&
	 * cr.getC().equals(nextClzRef.getReferenceC());
	 * }).collect(Collectors.toList()); nextClzRefList.forEach(cr -> {
	 * getCircularDepencyPath(startClzRef, cr, notSelfDependencyList,
	 * dependencyPath); }); } }
	 */
	public void findCircularDependcy(Class c1, List<Class<?>> alreadyWalkThrough, List<clzRef> clzRefList) {
		List<Field> list = new ArrayList<>();
		if (alreadyWalkThrough.contains(c1)) {
			return;
		}
		if (isComplexType(c1)) {
			alreadyWalkThrough.add(c1);
			list = getAllFields(c1);
			list.forEach(f -> {
				Class c = f.getType();
				if (isComplexType(c)) {
					Object complexObj = null;
					boolean isArray = c.isArray();
					boolean isAbstractOrInterface = isAbstractOrInterface(c);
					if (!isAbstractOrInterface && !isArray) {
						complexObj = newInstanceByConstructor(c);
						if (complexObj != null) {
							boolean isComplexType = isComplexType(complexObj.getClass());
							if (isComplexType) {
								clzRefList.add(new clzRef(c1, complexObj.getClass()));
								findCircularDependcy(complexObj.getClass(), alreadyWalkThrough, clzRefList);
							}
						}
					} else if (isArray) {
						// TODO how to handle a multiple dimension array
						complexObj = Array.newInstance(c.getComponentType(), 1);
					} else if (isAbstractOrInterface) {
						Class<?> concreteC = getDefaultConcreteClass(c);
						if (concreteC != null) {
							complexObj = this.newInstanceByConstructor(concreteC);
						}
					}
					if (complexObj != null) {
						// set field generic type value
						if (isArray) {
							Class componentType = c.getComponentType();
							Class concreteC = componentType;
							if (isAbstractOrInterface(componentType)) {
								concreteC = getDefaultConcreteClass(c);
							}
							if (concreteC != null) {
								Object componentTypeObj = this.newInstanceByConstructor(concreteC);
								if (componentTypeObj != null) {
									boolean isComplexType = isComplexType(componentTypeObj.getClass());
									if (isComplexType) {
										clzRefList.add(new clzRef(c1, componentTypeObj.getClass()));
										findCircularDependcy(componentTypeObj.getClass(), alreadyWalkThrough,
												clzRefList);
									}
								}
							}
						}
						if (java.util.Collection.class.isAssignableFrom(c)) {
							String genericTypeName = f.getGenericType().getTypeName();
							String genericType = null;
							Pattern p = Pattern.compile("<(.+)>");
							Matcher m = p.matcher(genericTypeName);
							if (m.find()) {
								genericType = m.group(1);
								try {
									String javaformatgenericType = genericType;// genericType.replaceAll("\\$",
																				// "\\.");
									Class genericTypeC = CodeGenContext.getInstance()
											.getReloadableDynamicClassLoaderOwner()
											.loadClass(javaformatgenericType, true);
									Class concreteC = genericTypeC;
									if (isAbstractOrInterface(genericTypeC)) {
										concreteC = getDefaultConcreteClass(c);
									}
									if (concreteC != null) {
										Object componentTypeObj = this.newInstanceByConstructor(concreteC);
										if (componentTypeObj != null) {
											boolean isComplexType = isComplexType(componentTypeObj.getClass());
											boolean at = alreadyWalkThrough.contains(componentTypeObj.getClass());
											if (isComplexType) {
												clzRefList.add(new clzRef(c1, componentTypeObj.getClass()));
												findCircularDependcy(componentTypeObj.getClass(), alreadyWalkThrough,
														clzRefList);
											}
										}
									}
								} catch (Exception e) {
									// e.printStackTrace();
								}
							}
						}
						if (java.util.Map.class.isAssignableFrom(c)) {
							String genericTypeName = f.getGenericType().getTypeName();
							String genericType = null;
							Pattern p = Pattern.compile("<(.+)>");
							Matcher m = p.matcher(genericTypeName);
							Class keyType = null;
							Class valType = null;
							if (m.find()) {
								try {
									genericType = m.group(1);
								} catch (Exception e1) {
									e1.printStackTrace();
								}
								if (genericType != null) {
									try {
										String javaformatgenericType = genericType;// genericType.replaceAll("\\$",
																					// "\\.");
										String gt[] = javaformatgenericType.split(",", -1);
										keyType = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner()
												.loadClass(gt[0].trim(), true);
										valType = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner()
												.loadClass(gt[1].trim(), true);
									} catch (Exception e) {
										// e.printStackTrace();
									}
								}
							}
							if (keyType != null && valType != null) {
								Class concreteKeyC = keyType;
								Class concreteValC = valType;
								if (isAbstractOrInterface(keyType)) {
									concreteKeyC = getDefaultConcreteClass(keyType);
								}
								if (isAbstractOrInterface(valType)) {
									concreteValC = getDefaultConcreteClass(valType);
								}
								if (concreteKeyC != null && concreteValC != null) {
									Object keyObj = this.newInstanceByConstructor(concreteKeyC);
									Object valObj = this.newInstanceByConstructor(concreteValC);
									if (keyObj != null && valObj != null) {
										boolean isComplexType = isComplexType(keyObj.getClass());
										if (isComplexType) {
											clzRefList.add(new clzRef(c1, keyObj.getClass()));
											findCircularDependcy(keyObj.getClass(), alreadyWalkThrough, clzRefList);
										}
										boolean isvalObjComplexType = isComplexType(valObj.getClass());
										if (isvalObjComplexType) {
											clzRefList.add(new clzRef(c1, valObj.getClass()));
											findCircularDependcy(valObj.getClass(), alreadyWalkThrough, clzRefList);
										}
									}
								}
							}
						}
					}
				}
			});
		}
	}

	public Map<Class, AtomicInteger> getAllCircularDependencyClzMap(Class c) {
		List<Class<?>> alreadyWalkThrough = new ArrayList<>();
		List<clzRef> clzRefList = new ArrayList<>();
		this.findCircularDependcy(c, alreadyWalkThrough, clzRefList);
		Map<clzRef, List<clzRef>> circularDependencyMap = this.getCircularDependencyPath(clzRefList);
		Map<Class, AtomicInteger> allCircularDependencyClz = this
				.getAllCircularDependencyRelatedClz(circularDependencyMap);
		return allCircularDependencyClz;
	}

	public static boolean isExclusive(String expression, String exclusiveRegex) {
		if (exclusiveRegex == null || "".equals(exclusiveRegex)) {
			return false;
		}
		Pattern p = Pattern.compile(exclusiveRegex);
		Matcher m = p.matcher(expression);
		return m.matches();
	}

	public void newInstanceRecusively(Object tobj, List allComplexTypeObj,
			Map<Class, AtomicInteger> circularDependencyRelatedClzMap, int defaultSize, String exclusiveRegex) {
		List<Field> list = new ArrayList<>();
		Class c1 = tobj.getClass();
		if (isComplexType(c1)) {
			allComplexTypeObj.add(tobj);
			if (circularDependencyRelatedClzMap.containsKey(c1)) {
				if (circularDependencyRelatedClzMap.get(c1).get() > 0) {
					return;
				} else {
					circularDependencyRelatedClzMap.get(c1).incrementAndGet();
				}
			}
			list = getAllFields(c1, exclusiveRegex);
			list.forEach(f -> {
				Class c = f.getType();
				boolean isExclusive = isExclusive(c.getName(), exclusiveRegex);
				if (!isExclusive && isComplexType(c)) {
					Object complexObj = null;
					boolean isArray = c.isArray();
					boolean isAbstractOrInterface = isAbstractOrInterface(c);
					if (!isAbstractOrInterface && !isArray) {
						complexObj = newInstanceByConstructor(c);
						if (complexObj != null) {
							boolean isComplexType = isComplexType(complexObj.getClass());
							if (isComplexType) {
								allComplexTypeObj.add(complexObj);
								newInstanceRecusively(complexObj, allComplexTypeObj, circularDependencyRelatedClzMap,
										defaultSize, exclusiveRegex);
							}
						}
					} else if (isArray) {
						// TODO how to handle a multiple dimension array
						complexObj = Array.newInstance(c.getComponentType(), defaultSize);
					} else if (isAbstractOrInterface) {
						Class<?> concreteC = getDefaultConcreteClass(c);
						if (concreteC != null) {
							complexObj = this.newInstanceByConstructor(concreteC);
						}
					}
					if (complexObj != null) {
						f.setAccessible(true);
						try {
							isExclusive = isExclusive(complexObj.getClass().getName(), exclusiveRegex);
							if (!isExclusive)
								f.set(tobj, complexObj);
						} catch (Exception e) {
							// e.printStackTrace();
						}
					}
					// set field generic type value
					if (isArray) {
						Class componentType = c.getComponentType();
						Class concreteC = componentType;
						if (isAbstractOrInterface(componentType)) {
							concreteC = getDefaultConcreteClass(c);
						}
						if (concreteC != null) {
							for (int i = 0; i < defaultSize; i++) {
								Object componentTypeObj = this.newInstanceByConstructor(concreteC);
								if (componentTypeObj != null) {
									Array.set(complexObj, i, componentTypeObj);
									boolean isComplexType = isComplexType(componentTypeObj.getClass());
									if (isComplexType) {
										allComplexTypeObj.add(componentTypeObj);
										newInstanceRecusively(componentTypeObj, allComplexTypeObj,
												circularDependencyRelatedClzMap, defaultSize, exclusiveRegex);
									}
								}
							}
						}
					}
					if (java.util.Collection.class.isAssignableFrom(c)) {
						String genericTypeName = f.getGenericType().getTypeName();
						String genericType = null;
						Pattern p = Pattern.compile("<(.+)>");
						Matcher m = p.matcher(genericTypeName);
						if (m.find()) {
							genericType = m.group(1);
							try {
								String javaformatgenericType = genericType;// genericType.replaceAll("\\$",
																			// "\\.");
								Class genericTypeC = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner()
										.loadClass(javaformatgenericType, true);
								Class concreteC = genericTypeC;
								if (isAbstractOrInterface(genericTypeC)) {
									concreteC = getDefaultConcreteClass(c);
								}
								isExclusive = isExclusive(concreteC.getName(), exclusiveRegex);
								if (!isExclusive && concreteC != null) {
									for (int i = defaultSize; i > 0; i--) {
										Object componentTypeObj = this.newInstanceByConstructor(concreteC);
										if (componentTypeObj != null) {
											List listTmp = (List) complexObj;
											listTmp.add(componentTypeObj);
											boolean isComplexType = isComplexType(componentTypeObj.getClass());
											if (isComplexType) {
												allComplexTypeObj.add(componentTypeObj);
												newInstanceRecusively(componentTypeObj, allComplexTypeObj,
														circularDependencyRelatedClzMap, defaultSize, exclusiveRegex);
											}
										}
									}
								}
							} catch (Exception e) {
								// e.printStackTrace();
							}
						}
					}
					if (java.util.Map.class.isAssignableFrom(c)) {
						String genericTypeName = f.getGenericType().getTypeName();
						String genericType = null;
						Pattern p = Pattern.compile("<(.+)>");
						Matcher m = p.matcher(genericTypeName);
						Class keyType = null;
						Class valType = null;
						if (m.find()) {
							try {
								genericType = m.group(1);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							if (genericType != null) {
								try {
									String javaformatgenericType = genericType;// genericType.replaceAll("\\$",
																				// "\\.");
									String gt[] = javaformatgenericType.split(",", -1);
									keyType = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner()
											.loadClass(gt[0].trim(), true);
									valType = CodeGenContext.getInstance().getReloadableDynamicClassLoaderOwner()
											.loadClass(gt[1].trim(), true);
								} catch (Exception e) {
									// e.printStackTrace();
								}
							}
						}
						if (keyType != null && valType != null) {
							Class concreteKeyC = keyType;
							Class concreteValC = valType;
							if (isAbstractOrInterface(keyType)) {
								concreteKeyC = getDefaultConcreteClass(keyType);
							}
							if (isAbstractOrInterface(valType)) {
								concreteValC = getDefaultConcreteClass(valType);
							}
							boolean isKeyConcern = isExclusive(concreteKeyC.getName(), exclusiveRegex);
							boolean isValConcern = isExclusive(concreteValC.getName(), exclusiveRegex);
							if (isKeyConcern && isValConcern && concreteKeyC != null && concreteValC != null) {
								for (int i = defaultSize; i > 0; i--) {
									Object keyObj = this.newInstanceByConstructor(concreteKeyC);
									Object valObj = this.newInstanceByConstructor(concreteValC);
									if (keyObj != null && valObj != null) {
										Map tmp = (Map) complexObj;
										tmp.put(keyObj, valObj);
										boolean isComplexType = isComplexType(keyObj.getClass());
										if (isComplexType) {
											allComplexTypeObj.add(keyObj);
											newInstanceRecusively(keyObj, allComplexTypeObj,
													circularDependencyRelatedClzMap, defaultSize, exclusiveRegex);
										}
										boolean isvalObjComplexType = isComplexType(valObj.getClass());
										if (isvalObjComplexType) {
											allComplexTypeObj.add(valObj);
											newInstanceRecusively(valObj, allComplexTypeObj,
													circularDependencyRelatedClzMap, defaultSize, exclusiveRegex);
										}
									}
								}
							}
						}
					}
				}
			});
		}
	}

	public static List<Field> getAllFields(Class c) {
		return getAllFields(c, null);
	}

	public static List<Field> getAllFields(Class c, String exclusiveRegex) {
		List<Field> allFieldList = new ArrayList<>();
		while (c != Object.class) {
			boolean isExclusive = isExclusive(c.getName(), exclusiveRegex);
			if (isExclusive) {
				break;
			}
			allFieldList.addAll(Arrays.asList(c.getDeclaredFields()));
			c = c.getSuperclass();
		}
		return allFieldList;
	}

	public void assignPrimitiveTypes(Object tobj) {
		assignPrimitiveTypes(tobj, null);
	}

	public void assignPrimitiveTypes(Object tobj, String exclusiveRegex) {
		List<Field> list = new ArrayList<>();
		Class c1 = tobj.getClass();
		list = getAllFields(c1, exclusiveRegex);
		list.forEach(f -> {
			Class c = f.getType();
			boolean isExclusive = isExclusive(c.getName(), exclusiveRegex);
			if (!isExclusive && !isComplexType(c)) {
				Object obj = null;
				f.setAccessible(true);
				/*
				 * try { obj = f.get(tobj); } catch (Exception e) { e.printStackTrace(); }
				 */
				Object value = this.getDefaultVal(c);
				try {
					f.set(tobj, value);
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
		});
	}

	public void assignComplextTypes(Object tobj, List<?> allComplexTypeObj) {
		assignComplextTypes(tobj, allComplexTypeObj, null);
	}

	public void assignComplextTypes(Object tobj, List<?> allComplexTypeObj, String exclusiveRegex) {
		List<Field> list = new ArrayList<>();
		Class c1 = tobj.getClass();
		list = getAllFields(c1, exclusiveRegex);
		list.forEach(f -> {
			try {
				f.setAccessible(true);
				Object fval = f.get(tobj);
				if (fval == null) {
					Class c = f.getType();
					Object alreadyCreatedInstance = null;
					try {
						alreadyCreatedInstance = allComplexTypeObj.stream().filter(e -> {
							return c.isInstance(e);
						}).findAny().get();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						// e1.printStackTrace();
					}
					boolean isExclusive = isExclusive(alreadyCreatedInstance.getClass().getCanonicalName(),
							exclusiveRegex) || isExclusive(c.getCanonicalName(), exclusiveRegex);
					if (alreadyCreatedInstance != null && !isExclusive) {
						try {
							f.set(tobj, alreadyCreatedInstance);
						} catch (Exception e) {
							// e.printStackTrace();
						}
					}
				}
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
		});
	}

	public <T> List<Class<?>> getAllReferComplexType(Class<T> t){
		List<Class<?>> allComplexTypeObj = new ArrayList<>();
		List<Class<?>> alreadyWalkThrough = new ArrayList<>();
		T obj = newInstanceByConstructor(t);
		if (obj != null) {
			List<clzRef> clzRefList = new ArrayList<>();
			this.findCircularDependcy(t, alreadyWalkThrough, clzRefList);
			for(clzRef cr : clzRefList){
				if(null!=cr.getC()){
					allComplexTypeObj.add(cr.getC());
				}
				if(null!=cr.getReferenceC()){
					allComplexTypeObj.add(cr.getReferenceC());
				}
			}
			allComplexTypeObj=allComplexTypeObj.stream().distinct().collect(Collectors.toList());
		}
		return allComplexTypeObj;
	}
	public <T> T newInstance(Class<T> t) {
		return newInstance(t, 2);
	}

	public <T> T newInstance(Class<T> t, int defaultSize) {
		return newInstance(t, defaultSize, null);
	}

	public <T> T newInstance(Class<T> t, int defaultSize, String exclusiveRegex) {
		List<?> allComplexTypeObj = new ArrayList<>();
		List<Class<?>> alreadyWalkThrough = new ArrayList<>();
		T obj = newInstanceByConstructor(t);
		if (obj != null) {
			List<clzRef> clzRefList = new ArrayList<>();
			this.findCircularDependcy(t, alreadyWalkThrough, clzRefList);
			Map<clzRef, List<clzRef>> circularDependencyMap = this.getCircularDependencyPath(clzRefList);
			Map<Class, AtomicInteger> allCircularDependencyClz = this
					.getAllCircularDependencyRelatedClz(circularDependencyMap);
			newInstanceRecusively(obj, allComplexTypeObj, allCircularDependencyClz, defaultSize, exclusiveRegex);
			allComplexTypeObj.forEach(ct -> {
				assignPrimitiveTypes(ct, exclusiveRegex);
				// assign circular dependency
				assignComplextTypes(ct, allComplexTypeObj, exclusiveRegex);
			});
		}
		return obj;
	}

	public static void main(String[] args) {
		ObjWithDefaultValCreator obf = new ObjWithDefaultValCreator();
		CicularClass1 obj = obf.newInstance(CicularClass1.class, 1);
		System.out.println(obj);
		List<Class<?>> allComplextTypes = obf.getAllReferComplexType(CicularClass1.class);
		System.out.println(allComplextTypes);
		// Map<Class, AtomicInteger> map =
		// obf.getAllCircularDependencyClzMap(CicularClass1.class);
		// map.size();
	}
}