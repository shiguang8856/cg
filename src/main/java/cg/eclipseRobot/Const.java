package cg.eclipseRobot;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Const {

	public static final int MAX_FONT_SIZE = 20;
	public static final int MIN_FONT_SIZE = 10;
	public static final double MAX_FONT_COLOR_UNMATCH_PERCENTAGE = 0.02;
	public static final int MIN_FONT_COLOR_UNMATCH = 3;
	public static final int MIN_BACK_COLOR_UNMATCH = 2;
	public static final double MAX_BACKGROUND_COLOR_UNMATCH_PERCENTAGE = 0.01;
	public static final String CHECKBOX_FOR_CREATE_CONSTRUCTOR_WHEN_NEW_CLASS_RS_NAME = null;
	public static final String BTN_ADD_INTERFACE_WHEN_NEW_CLASS_RS_NAME = null;
	public static final String TEXT_FIELD_SUPER_CLASS_WHEN_NEW_CLASS_RS_NAME = null;
	public static final String CLASS_NAME_WHEN_NEW_CLASS_RS_NAME = null;
	public static final String PACKNAME_WHEN_NEW_CLASS_RS_NAME = null;
	public static final String NEW_CLASS_IN_POP_MENU_RS_NAME = null;

	public static final List<Class<?>> primitiveClz = Arrays.asList(
			new Class[] { java.lang.String.class, int.class, boolean.class, long.class, double.class, short.class,
					float.class, byte.class, char.class, Integer.class, Boolean.class, Long.class, Double.class,
					Short.class, Float.class, Byte.class, Character.class, java.sql.Date.class, java.util.Date.class,
					java.sql.Timestamp.class, java.math.BigInteger.class, java.math.BigDecimal.class,
					javax.xml.datatype.XMLGregorianCalendar.class, File.class, java.lang.Object.class });

	public static final List<Class<?>> returnTypePrimitiveClz = Arrays.asList(
			new Class[] { java.lang.String.class, int.class, boolean.class, long.class, double.class, short.class,
					float.class, byte.class, char.class, Integer.class, Boolean.class, Long.class, Double.class,
					Short.class, Float.class, Byte.class, Character.class, java.sql.Date.class, java.util.Date.class,
					java.sql.Timestamp.class, java.math.BigInteger.class, java.math.BigDecimal.class,
					javax.xml.datatype.XMLGregorianCalendar.class, File.class, java.lang.Object.class, void.class });

	public static final String ECLIPSE_WINDOW_TITLE_KEYWORD = "eclipse";

	public static enum WinOperationEnum {
		RESIZE("resize"), SWITCH_ECLIPSE("switchProcess"), GET_ACTIVE_WINDOW("getActiveWindow");
		String value;

		private WinOperationEnum(String value) {
			this.value = value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

	}

}
