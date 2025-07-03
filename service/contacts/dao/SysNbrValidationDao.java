package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.xmlstructs.inputstructs.NbrValidationDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ValidationMsgDto;

public interface SysNbrValidationDao {
	/**
	 * This dam will check whether a person has taken 3 or more PAL training
	 * elements.
	 *
	 * @param NbrValidationDto
	 * @return The {@link ValidationMsgDto} object populated with the
	 *         ulSysNbrValidationMsg and the number of rows.
	 */
	public ValidationMsgDto getUlSysNbrValidationMsg(NbrValidationDto cmsc14di);
}
