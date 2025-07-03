package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.APSInvestigationDto;

public interface ApsInvstDao {
	/**
	 * 
	 * Method Name: updateAPSInvestigationDetail Method Description:long
	 * 
	 * @param apsInvestigationDto
	 * @return long
	 * @throws DataNotFoundException
	 */
	public long updateAPSInvestigationDetail(APSInvestigationDto apsInvestigationDto);
}
