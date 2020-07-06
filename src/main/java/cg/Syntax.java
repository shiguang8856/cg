package cg;

public class Syntax {
	public static String REFERENCE_SYMBOL = "$";
	public static String ASSIGN_SYMBOL = "=";
	public static String NEW_LINE = "\r\n";
	public static String END_OF_LINE = ";\r\n";
	public static String PACKAGE_DECLARE = "package $PACKAGE_NAME$$END_OF_LINE$";
	public static String FIELD_DECLARE = "$PRIVILEDGE_MODIFIER$$STATIC_MODIFIER$$ABSTRACT_MODIFIER$$FINAL_MODIFIER$$VOLATILE_MODIFIER$$SYNC_MODIFIER$ $FIELD_CLASS_NAME$ $FIELD_INSTANCE_NAME$$END_OF_LINE$";
	public static String FIELD_DECLARE_WITH_ASSIGN = "$PRIVILEDGE_MODIFIER$$STATIC_MODIFIER$$ABSTRACT_MODIFIER$$FINAL_MODIFIER$$SYNC_MODIFIER$ $FIELD_CLASS_NAME$ $FIELD_INSTANCE_NAME$=$TMP_INSTANCE_VALUE$$END_OF_LINE$";

	public static String IMPORT_LINE = "import $QUALIFIER$$END_OF_LINE$";
	public static String CLASS_BEGIN_LINE = "$PRIVILEDGE_MODIFIER$$STATIC_MODIFIER$$ABSTRACT_MODIFIER$$FINAL_MODIFIER$$SYNC_MODIFIER$ class $TMP_CLASS_NAME$ {$NEW_LINE$";
	public static String CLASS_END_LINE = "}";
	public static String DECLARE_LINE = "$TMP_CLASS_NAME$ $TMP_INSTANCE_NAME$;$NEW_LINE$";
	public static String ASSIGN_LINE = "$TMP_CLASS_NAME$ $TMP_INSTANCE_NAME$ = $TMP_INSTANCE_VALUE$;$NEW_LINE$";
	public static String ARRAY_DECLARE_LINE = "$TMP_CLASS_NAME$ $TMP_INSTANCE_NAME$[];$NEW_LINE$";
	public static String ARRAY_ASSIGN_LINE = "$TMP_CLASS_NAME$ $TMP_INSTANCE_NAME[]$ = $TMP_INSTANCE_VALUE$;$NEW_LINE$";
	public static String CAST_LINE = "$TARGET_CLASS_NAME$$SEPERATOR$$TARGET_INSTANCE_NAME$ = $TMP_CLASS_NAME$.class.cast(TMP_INSTANCE_VALUE);$NEW_LINE$";
	public static String CAST_EXPLICIT_LINE = "$TARGET_CLASS_NAME$$SEPERATOR$$TARGET_INSTANCE_NAME$ = ($TMP_CLASS_NAME$)$TMP_INSTANCE_VALUE$;$NEW_LINE$";
	public static String ABSTRACT_NEW_INSTANCE_LINE_WITH_DECLARE_OR_NOT = "$ABSTRACT_CLASS_NAME$$SEPERATOR$$ABSTRACT_INSTANCE_NAME$ = new $TMP_CLASS_NAME$($METHOD_PARAMS_VARIABLE$);$NEW_LINE$";
	public static String NEW_INSTANCE_LINE = "$TMP_INSTANCE_NAME$ = new $TMP_CLASS_NAME$();$NEW_LINE$";
	public static String INIT_CLASS_WITH_CONCRETE_LINE = "$ABSTRACT_CLASS_NAME$ $ABSTRACT_INSTANCE_NAME$ = $TMP_CLASS_NAME$.$METHOD_NAME$($METHOD_PARAMS_VARIABLE$);$NEW_LINE$";
	public static String NEW_ABSTRACT_CLASS_INSTANCE_LINE_BEGIN_LINE = "$TMP_INSTANCE_NAME$ = new $TMP_CLASS_NAME$(){$CLASS_END_LINE$";
	public static String NEW_ABSTRACT_CLASS_METHOD_WITH_RETURN = "@Override$CLASS_END_LINE$"
			+ "public $METHOD_RETURN_TYPE$ $METHOD_NAME$($METHOD_PARAMS_DECLARE$) {" + "return null;" + "}";

	public static String CONTROLLER_METHOD = "@RequestMapping(value=\"$PATH$\"$METHOD_STR$$HEADERS$$CONSUMERS$$PRODUCES$)"
			+ "$NEW_LINE$$PRIVILEDGE_MODIFIER$$STATIC_MODIFIER$$ABSTRACT_MODIFIER$$FINAL_MODIFIER$$SYNC_MODIFIER$ $METHOD_RETURN_TYPE$ $METHOD_NAME$($METHOD_PARAMS_DECLARE$) $THROWS_EXCEPTION${$NEW_LINE$"
			+ "    $METHOD_BODY$$NEW_LINE$" + "    return $METHOD_RETURN_VALUE$$END_OF_LINE$" + "}";
	public static String NORMAL_METHOD = "$PRIVILEDGE_MODIFIER$$STATIC_MODIFIER$$ABSTRACT_MODIFIER$$FINAL_MODIFIER$$SYNC_MODIFIER$ $METHOD_RETURN_TYPE$ $METHOD_NAME$($METHOD_PARAMS_DECLARE$) $THROWS_EXCEPTION${$NEW_LINE$"
			+ "    $METHOD_BODY$$NEW_LINE$" + "    return $METHOD_RETURN_VALUE$$END_OF_LINE$" + "}";
	public static String METHOD_END_LINE = "}";
	/***/
	public static String INIT_CLASS_WITH_DEFAULT_VALUE = "$TMP_CLASS_NAME$$SEPERATOR$$TMP_INSTANCE_NAME$ = $DEFAULT_VALUE$;$NEW_LINE$";
	public static String INIT_LIST = "List<$GENERIC_TYPE_NAME$> $TMP_INSTANCE_NAME$ = new $TMP_CLASS_NAME$<>();";
	public static String INIT_Map = "Map<$GENERIC_TYPE_KEY$, $GENERIC_TYPE_VAL$> $TMP_INSTANCE_NAME$ = new $TMP_CLASS_NAME$<>();";
	public static String NEW_ABSTRACT_CLASS_METHOD_RETURN_VOID = "@Override$CLASS_END_LINE$"
			+ "public void $METHOD_NAME$($METHOD_PARAMS_DECLARE$) {" + "return ;" + "}";
	public static String NEW_EXCEPTION_THROW = "throw new $TMP_CLASS_NAME$($METHOD_PARAMS_VARIABLE$);";
	public static String RETURN = "return $TMP_INSTANCE_VALUE$;";
	public static String LOGGER_TRACE = "log.trace(String.format(\" %s\", s));";
	public static String LOGGER_DEBUG = "log.debug(String.format(\" %s\", s));";
	public static String LOGGER_INFO = "log.info(String.format(\" %s\", s));";
	public static String LOGGER_ERROR = "log.error(String.format(\" %s\", s));";
	public static String LOGGER_FATAL = "log.fatal(String.format(\" %s\", s));";
	public static String NORMAL_SETUP_BEGIN_LINE = "@Before$NEW_LINE$"
			+ "public void setUp() throws Exception {$NEW_LINE$";
	public static String NORMAL_TEST_CASE_BEGIN_LINE = "@Test$NEW_LINE$" + "public void test$METHOD_NAME$() {";
	public static String EXCEPTION_TEST_CASE_BEGIN_LINE = "@Test(expected=$EXCEPTION_CLASS$.class)$CLASS_END_LINE$"
			+ "public void test$METHOD_NAME$() {";
	public static String METHOD_WITH_RETURN_CALLING = "$RETURN_TYPE$ $RETURN_TYPE_INSTANCE$ = $TMP_INSTANCE_NAME$.$METHOD_NAME$($METHOD_PARAMS_VARIABLE$);$NEW_LINE$";
	public static String VOID_METHOD_CALLING = "$TMP_INSTANCE_NAME$.$METHOD_NAME$($METHOD_PARAMS_VARIABLE$);$NEW_LINE$";
	public static String METHOD_PARAMS_VARIABLE_LINE = "$METHOD_PARAM1$, $METHOD_PARAM2$";
	public static String IF_ELSE_BLOCK = "$PREFIX_STR$if($IF_EXPRESSION$){$NEW_LINE$$NEW_LINE$" + "}";
	public static String ELSE_BLOCK = "else{$NEW_LINE$$NEW_LINE$" + "}";
	public static String FOR_EACH_BLOCK = "for($TMP_CLASS_NAME$ $ITEM_NAME$ : $TMP_INSTANCE_NAME$){$NEW_LINE$}";
	public static String FOR_LOOP_INCREMENT_BLOCK = "for($TMP_CLASS_NAME$ $TMP_INSTANCE_NAME$=0;$TMP_INSTANCE_NAME$<.length();$TMP_INSTANCE_NAME$++){$NEW_LINE$}";
	public static String FOR_LOOP_DECREMENT_BLOCK = "for($TMP_CLASS_NAME$ $TMP_INSTANCE_NAME$=.length()-1;$TMP_INSTANCE_NAME$>=0;$TMP_INSTANCE_NAME$--){$NEW_LINE$}";
	public static String SWITCH_BLOCK = "switch($TMP_INSTANCE_NAME$){$NEW_LINE$$BLOCK_BODY$}";
	public static String SWITCH_CASE_BLOCK = "case $TMP_INSTANCE_NAME$:$NEW_LINE$$NEW_LINE$$NEW_LINE$break$END_OF_LINE$";
	public static String SWITCH_DEFAULT_BLOCK = "default :$NEW_LINE$$NEW_LINE$break$END_OF_LINE$";
	public static String WHILE_BLOCK = "while($TMP_INSTANCE_NAME$){$NEW_LINE$$BLOCK_BODY$}";
	public static String DO_WHILE_BLOCK = "do{$NEW_LINE$$BLOCK_BODY$}while($TMP_INSTANCE_NAME$)$END_OF_LINE$";
	public static String SYNCHRONIZED_BLOCK = "synchronized($TMP_INSTANCE_NAME$){$NEW_LINE$$BLOCK_BODY$}";
	public static String TRY_CATCH_BLOCK = "try{$NEW_LINE$" + "$TRY_BODY$$NEW_LINE$"
			+ "}catch($EXCEPTIONS_DECLARE$){$NEW_LINE$" + "$CATCH_BODY$$NEW_LINE$" + "}finally{" + "$FINALLY_BODY$"
			+ "}";
	public static String COLLECTION_STREAM = "$TMP_CLASS_NAME$ $TMP_INSTANCE_NAME$$ASSIGN_SYMBOL$$TMP_INSTANCE_VALUE$.stream()";
	public static String STREAM_DISTINCT = ".distinct()";
	public static String STREAM_SORT = ".sorted((Object o1, Object o2)-> {$NEW_LINE$ if(o1 > o2) {$NEW_LINE$return -1;$NEW_LINE$}else if(o1 == o2) {$NEW_LINE$return 0;$NEW_LINE$}else {$NEW_LINE$return 1;} $NEW_LINE$})";
	public static String STREAM_FOREACH = ".forEach(e-> {})";
	public static String STREAM_FILTER = ".filter(e -> {if (\"\".equals(e)) { return true; } return false; })";
	public static String STREAM_MAP = ".map(e-> {return e;})";
	public static String STREAM_COLLECT_AS_LIST = ".collect(Collectors.toList())";
	public static String STREAM_COLLECT_AS_SET = ".collect(Collectors.toSet())";
	public static String STREAM_COLLECT_AS_MAP = ".collect(Collectors.toMap(e-> e.getId().toString().toLowerCase(), e->e.getValue(), (e1,e2)->{return e1;}))";
	public static String STREAM_COLLECT_AS_TREE_MAP = ".collect(Collectors.toMap(e-> e.getId().toString().toLowerCase(), e->e.getValue(), (e1,e2)->{return e1;}, ()->new TreeMap(String.CASE_INSENSITIVE_ORDER))";
	public static String STREAM_COLLECT_AS_CONCURRENTMAP = ".collect(Collectors.toConcurrentMap($GENERIC_TYPE_KEY$, $GENERIC_TYPE_VAL$))";
	public static String MAP_ENTRY_SET = "$TMP_INSTANCE_NAME$.entrySet()";
	public static String MAP_VALUES = "$TMP_INSTANCE_NAME$.values()";
	public static String MAP_KEY_SET = "$TMP_INSTANCE_NAME$.keySet()";
	public static String MAIN_BLOCK = "public static void main(String[] args){\r\n}";
	public static String EXCEPTIONS_DECLARE_LINE = "$EXCEPTION_NAME$ $EXCEPTION_INSTANCE_NAME$";
	public static String COLLECTION_LAMBDA_FOR_EACH = "$TMP_INSTANCE_NAME$.$METHOD_NAME$().forEach(($LAMBDA_PARAMS_VARIABLE$) -> {$NEW_LINE$"
			+ "$METHOD_BODY$" + "$NEW_LINE$});$NEW_LINE$";
	public static String SYS_IN = "BufferedReader br = new BufferedReader(new InputStreamReader(System.in));$NEW_LINE$"
			+ "while (true) {$NEW_LINE$" + "    String input = br.readLine();$NEW_LINE$"
			+ "    if (input != null && !input.isEmpty()){$NEW_LINE$" + "        switch (input){$NEW_LINE$"
			+ "            case \"\":$NEW_LINE$" + "                break;$NEW_LINE$"
			+ "            case \"exit\":$NEW_LINE$" + "                System.exit(0);$NEW_LINE$"
			+ "            default :$NEW_LINE$" + "                $NEW_LINE$" + "         }$NEW_LINE$"
			+ "    }$NEW_LINE$" + "}$NEW_LINE$";
	public static String SYS_OUT = "System.out.$METHOD_NAME$($METHOD_PARAMS_VARIABLE$);";
	public static String STRING_FORMAT = "String.format($METHOD_PARAMS_VARIABLE$);";

	public static String PATTERN_COMPILE = "Pattern $TMP_INSTANCE_VALUE$ = Pattern.compile($TMP_INSTANCE_NAME$, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);";
	public static String PATTERN_CREATE_MATCHER = "Matcher $TMP_INSTANCE_VALUE$ = $TMP_INSTANCE_NAME$.matcher($METHOD_PARAMS_VARIABLE$);";
	public static String MATCHER_FIND = "boolean $TMP_INSTANCE_VALUE$ = $TMP_INSTANCE_NAME$.find();";
	public static String MATCHER_MATCHES = "boolean $TMP_INSTANCE_VALUE$ = $TMP_INSTANCE_NAME$.matches();";

	public static String UNIT_TEST_SET_TARGET_OBJ_FIELD_CALLING = "setTargetObjField(\"$FIELD_INSTANCE_NAME$\", $TMP_INSTANCE_NAME$, $FIELD_VALUE$)$END_OF_LINE$";
	public static String UNIT_TEST_SET_TARGET_OBJ_FIELD_METHOD = "private void setTargetObjField(String fieldName, Object target, Object fieldObj){$NEW_LINE$"
			+ "try {$NEW_LINE$" + "Field f = getField(fieldName, target.getClass());$NEW_LINE$"
			+ "if(f == null){$NEW_LINE$" + "return;$NEW_LINE$" + "}$NEW_LINE$" + "f.setAccessible(true);$NEW_LINE$"
			+ "f.set(target, fieldObj);$NEW_LINE$"
			+ "} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {$NEW_LINE$"
			+ "e.printStackTrace();$NEW_LINE$" + "}$NEW_LINE$" + "}";
	public static String UNIT_TEST_GET_FIELD_METHOD = "private Field getField(String fieldName, Class target){$NEW_LINE$"
			+ "Field f = null;$NEW_LINE$" + "try{$NEW_LINE$" + "f = target.getDeclaredField(fieldName);$NEW_LINE$"
			+ "}catch(NoSuchFieldException e){$NEW_LINE$" + "Class superC = target.getSuperclass();$NEW_LINE$"
			+ "if(superC != null){$NEW_LINE$" + "return getField(fieldName, superC);$NEW_LINE$" + "}$NEW_LINE$"
			+ "}$NEW_LINE$" + "return f;$NEW_LINE$" + "}$NEW_LINE$";
	public static String ASSERT_FAIL = "Assert.fail()$END_OF_LINE$";
	public static String ASSERT_NOT_NULL = "Assert.assertNotNull($TMP_INSTANCE_NAME$)$END_OF_LINE$";
	public static String ASSERT_EQUAL = "Assert.assertEquals($EXPECT_VALUE$, $ACTUAL_VALUE$)$END_OF_LINE$";
	public static String MOCKITO_VERIFY = "Mockito.verify($INSTANCE_NAME$, Mockito.times(1)).$METHOD_NAME$($MATCH_PARAMS_VARIABLE$)$END_OF_LINE$";
	public static String POWER_MOCK_STATIC_VERIFY = "";
	public static String ANY_MATCH = "Mockito.any()";
	public static String ANY_OF_CLASS_MATCH = "Mockito.any($TMP_CLASS_NAME$.class)";
	public static String INTERFACE_MOCK_CLASS_NAME = "$TMP_CLASS_NAME$MOCK";
	public static String MOCK_CLASS_LINE = "$TMP_CLASS_NAME$ $TMP_INSTANCE_NAME$ = Mockito.mock($TMP_CLASS_NAME$.class)$END_OF_LINE$";
	public static String MOCK_THROW = "Mockito.doThrow(Exception.class).when($TMP_INSTANCE_NAME$).$METHOD_NAME$($METHOD_PARAMS_VARIABLE$)$END_OF_LINE$";
	public static String MOCK_RETURN_VOID = "Mockito.doNothing().when($TMP_INSTANCE_NAME$).$METHOD_NAME$($METHOD_PARAMS_VARIABLE$)$END_OF_LINE$";
	public static String MOCK_RETURN = "Mockito.doReturn($METHOD_RETURN_VALUE$).when($TMP_INSTANCE_NAME$).$METHOD_NAME$($METHOD_PARAMS_VARIABLE$)$END_OF_LINE$";

	public static String WRITE_FILE = "public static void write(File f, String fileStr) throws IOException {"
			+ "$NEW_LINE$" + "FileOutputStream fos = new FileOutputStream(f);" + "$NEW_LINE$"
			+ "fos.write(fileStr.getBytes());" + "$NEW_LINE$" + "fos.flush();" + "$NEW_LINE$" + "fos.close();"
			+ "$NEW_LINE$" + "}";

	public static String READ_FILE = "public static String readAsStr(File f) throws IOException {" + "$NEW_LINE$"
			+ "FileInputStream is = new FileInputStream(f);" + "$NEW_LINE$" + "StringBuilder sb = new StringBuilder();"
			+ "$NEW_LINE$" + "BufferedReader in = null;" + "$NEW_LINE$" + "try {" + "$NEW_LINE$"
			+ "	in = new BufferedReader(new InputStreamReader(is));" + "$NEW_LINE$" + "	String line = null;"
			+ "$NEW_LINE$" + "	while ((line = in.readLine()) != null) {" + "$NEW_LINE$"
			+ "		sb.append(line).append(\"\r\n\");" + "$NEW_LINE$" + "	}" + "$NEW_LINE$"
			+ "} catch (IOException e) {" + "$NEW_LINE$" + "	e.printStackTrace();" + "$NEW_LINE$" + "	throw e;"
			+ "$NEW_LINE$" + "} finally {" + "$NEW_LINE$" + "	if (in != null)" + "$NEW_LINE$" + "		try {"
			+ "$NEW_LINE$" + "			in.close();" + "$NEW_LINE$" + "		} catch (IOException e) {" + "$NEW_LINE$"
			+ "			e.printStackTrace();" + "$NEW_LINE$" + "		}" + "$NEW_LINE$" + "}" + "$NEW_LINE$"
			+ "return sb.toString();" + "$NEW_LINE$" + "}";

	public static String THREAD_POOL_EXECUTOR = "$PRIVILEDGE_MODIFIER$$STATIC_MODIFIER$$ABSTRACT_MODIFIER$$FINAL_MODIFIER$ BlockingQueue<Runnable> arrayBlockingQueue = new ArrayBlockingQueue<>($MAX_POOL_SIZE$);"
			+ "$NEW_LINE$"
			+ "$PRIVILEDGE_MODIFIER$$STATIC_MODIFIER$$ABSTRACT_MODIFIER$$FINAL_MODIFIER$ ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor($CORE_POOL_SIZE$, $MAX_POOL_SIZE$, 1L, java.util.concurrent.TimeUnit.SECONDS, arrayBlockingQueue $NEW_THREAD_FACTORY$);"
			+ "$NEW_LINE$"
			+ "$PRIVILEDGE_MODIFIER$$STATIC_MODIFIER$$ABSTRACT_MODIFIER$$FINAL_MODIFIER$ void shutdownThreadPool() {"
			+ "$NEW_LINE$" + "    threadPoolExecutor.shutdown();" + "$NEW_LINE$" + "}";
	public static String NEW_THREAD_FACTORY = "new ThreadFactory(){" + "$NEW_LINE$" + "@Override" + "$NEW_LINE$"
			+ "public Thread newThread(Runnable r) {" + "$NEW_LINE$" + "    return new Thread(r);" + "$NEW_LINE$"
			+ "}}";
	public static String SUBMIT_CALLABLE_TASK = "$PRIVILEDGE_MODIFIER$$STATIC_MODIFIER$$ABSTRACT_MODIFIER$$FINAL_MODIFIER$ $RETURN_TYPE$ submitTask(Callable<$RETURN_TYPE$> task) throws Exception {"
			+ "$NEW_LINE$" + "    Future<$RETURN_TYPE$> f = threadPoolExecutor.submit(task);" + "$NEW_LINE$"
			+ "    return f.get();" + "$NEW_LINE$" + "}";
	public static String SUBMIT_RUNNABLE_TASK = "$PRIVILEDGE_MODIFIER$$STATIC_MODIFIER$$ABSTRACT_MODIFIER$$FINAL_MODIFIER$ void submitTask(Runnable task) {"
			+ "$NEW_LINE$" + "    threadPoolExecutor.submit(task);" + "$NEW_LINE$" + "}";
	
	
	public static String MAVEN_DEPENDENCY = "<dependency>$NEW_LINE$<groupId>$GROUP_ID$</groupId>$NEW_LINE$<artifactId>$ARTIFACTOR_ID$</artifactId>$NEW_LINE$"
			+ "<version>$VERSION$</version>$NEW_LINE$<scope>$SCOPE$</scope>$NEW_LINE$</dependency>";
	
	public static String HTML_TAG_A = "<a$ID$ href=$LINK$></a>";
	public static String HTML_TAG_TEXT = "<input type=\"text\"$ID$ name=\"$TAG_NAME$\" value=\"\"></input>";
	public static String HTML_TAG_HIDDEN = "<input type=\"hidden\"$ID$ name=\"$TAG_NAME$\"  value=\"\"></input>";
	public static String HTML_TAG_BTN = "<input type=\"button\"$ID$ name=\"$TAG_NAME$\"  value=\"\"></input>";
	public static String HTML_TAG_FILE = "<input type=\"file\"$ID$ name=\"$TAG_NAME$\"  value=\"\"></input>";
	public static String HTML_TAG_SELECT = "<input type=\"select\"$ID$ name=\"$TAG_NAME$\">$NEW_LINE$<option value=\"\"></option>$NEW_LINE$</input>";
	public static String HTML_TAG_FORM = "<form$ID$ method=$REQUEST_METHOD$$ID$ action=$LINK$$TARGET$$NEW_LINE$</form>";
	
	public static String PODAM_INIT_POJO = "$TMP_CLASS_NAME$ $TMP_INSTANCE_NAME$ = new PodamFactoryImpl().manufacturePojo($TMP_CLASS_NAME$.class)$END_OF_LINE$";
}
