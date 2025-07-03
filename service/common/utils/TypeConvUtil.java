package us.tx.state.dfps.service.common.utils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

public class TypeConvUtil {
	private static final Logger log = Logger.getLogger(TypeConvUtil.class);
	static DateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy");

	public static boolean isNullOrEmpty(String inputStr) {

		return inputStr == null || (inputStr.trim()).length() == 0;
	}

	public static boolean isNullOrEmpty(int inputInt) {

		return inputInt == 0;
	}

	public static boolean isNullOrEmpty(Long inputStr) {

		return inputStr == null || inputStr == 0l;
	}

	public static boolean isNullOrEmpty(Date inputDt) {

		return inputDt == null;
	}

	public static boolean isNullOrEmpty(List<Object> objectList) {
		boolean check = false;
		if (objectList == null) {
			check = true;
		} else {
			if (objectList.size() == 0) {
				check = true;
			}

		}
		return check;
	}

	public static boolean isNullOrEmptyAge(Long inputStr) {

		return inputStr == null;
	}

	public static String toString(Object rec) {

		String outString;
		if (rec != null) {
			outString = rec.toString();
		} else {
			outString = "";
		}
		return outString;
	}

	public static Long toLong(Object rec) {

		long outlong = 0;
		if (rec != null) {
			outlong = Long.parseLong(rec.toString());
		}

		return outlong;
	}

	public static int toInt(Object rec) {

		int outint = 0;
		if (rec != null) {
			outint = Integer.parseInt(rec.toString());
		}

		return outint;
	}

	public static Date passeDatTxt(String string) {
		Date date = new Date();
		if (string != null) {
			try {
				date = formatter.parse(string);
			} catch (ParseException e) {
				log.error(e.getMessage());
			}
		}

		return date;
	}

	public static BigDecimal toBigDecimal(Object rec) {

		BigDecimal outBigDecimal = new BigDecimal(0);
		if (rec != null) {
			outBigDecimal = (BigDecimal) rec;
		}

		return outBigDecimal;
	}

	public static Date passeDatTxt(Object obj) {
		Date date = new Date();

		if (obj != null) {

			date = (Date) obj;
			/*
			 * DateFormat dateformatter = new SimpleDateFormat("yyyy-MMM-DD");
			 * date = dateformatter.parse(obj+"");
			 */
		} else {
			date = null;
		}

		return date;
	}

	public static boolean isNullOrEmpty(Object obj) {

		return obj == null;
	}

	public static String stringHelperForLikeCheck(String string) {
		String outString = null;
		if (string != null) {
			outString = "'%" + string.toUpperCase() + "%'";
		}
		return outString;
	}

	public static String stringHelperForLike(String string) {
		String outString = null;
		if (string != null) {
			outString = "%" + string.toUpperCase() + "%";
		}
		return outString;
	}

	public static String stringHelperForSql(String string) {
		String outString = null;
		if (string != null) {
			outString = "'" + string.toUpperCase() + "'";
		}
		return outString;
	}

	public static boolean isNullOrEmptyBdDecm(BigDecimal inputStr) {

		return inputStr == null || inputStr.equals(BigDecimal.valueOf(0.0));
	}

	public Date addDate(Date inputDate) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(inputDate);
		cal.add(Calendar.DATE, 1);
		inputDate = cal.getTime();

		return inputDate;
	}

	public static boolean isNullOrEmpty(final Collection<?> collection) {
		return null == collection || collection.isEmpty();
	}

	public static Object isDateNullCheck(Object inputDate) {
		if (null != inputDate) {
			return inputDate;
		} else {
			return "";
		}
	}

	public static boolean compareString(String str1, String str2) {
		return (null == str1 ? null == str2 : str1.equals(str2));
	}

	public static boolean checkForEquality(String thisString, String comparedString) {
		boolean result;
		if (thisString == null) {
			if (comparedString == null) {
				result = true;
			} else {
				result = false;
			}
		} else {
			result = thisString.equals(comparedString);
		}
		return result;
	}

	public static boolean compareLong(Long str1, Long str2) {
		if (null == str1) {
			return false;
		} else if (null == str2) {
			return false;
		} else {
			return (str1 == str2);
		}
	}

	public static String formatDate(Date date) {
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);

		return dateString;
	}

	public static Integer convertLongToInteger(Long ulInput) {
		if (null != ulInput) {
			return ulInput.intValue();
		} else {
			return 0;
		}
	}

	public static boolean isValid(String inputStr) {
		return isNullOrEmpty(inputStr) ? false : true;

	}

	public static double convertToTwoDecimalPlace(Long input) {
		DecimalFormat df = new DecimalFormat("#.00");
		return Double.parseDouble(df.format(input.doubleValue()));
	}

}
