package us.tx.state.dfps.service.common.handwriting.util;

import java.util.StringTokenizer;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.handwriting.dto.HandWritingValueDto;

/**
 * service-common- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * methods are used in HandWriting Dao Implementation Sep 25, 2017- 2:29:30 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
public class HandWritingUtils {
	/**
	 * Method Name: isTemporaryNote Method Description: This method checks if
	 * handwriting key is a temporary one. This method is used to see if notes
	 * are temporary and not yet linked to event We need to ignore such notes
	 * which checkin. We need not show such notes in Linked to page list.
	 * 
	 * @param handWritingValueDto
	 * @return boolean
	 */
	public static boolean isTemporaryNote(HandWritingValueDto handWritingValueDto) {
		if (!TypeConvUtil.isNullOrEmpty(handWritingValueDto.getTxtFieldName())) {
			if ((TypeConvUtil.isNullOrEmpty(handWritingValueDto.getIdEvent()))
					|| (handWritingValueDto.getIdEvent().intValue() == ServiceConstants.EVENTSTATUS_NEW_PRIORITY)) {
				return true;
			}
			StringTokenizer stringTokenizer = new StringTokenizer(handWritingValueDto.getTxtKeyValue(),
					ServiceConstants.UNDERSCORE_SYMBOL);
			while (stringTokenizer.hasMoreTokens()) {
				String string = stringTokenizer.nextToken();
				if (HandWritingUtils.isParsableToLong(string)) {
					if (string.length() >= 13) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks if the passed string parsable to long.
	 *
	 * @param string
	 *            the string
	 * @return true, if is parsable to long
	 */
	public static boolean isParsableToLong(String string) {
		Boolean isParsable = true;
		try {
			Long.parseLong(string);
			isParsable = true;
		} catch (NumberFormatException exception) {
			isParsable = false;
		}
		return isParsable;
	}
}
