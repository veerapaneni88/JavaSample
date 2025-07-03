package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.ApsInvStageDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactDateDto;

public interface ContactDateDao {
	/**
	 * 
	 * Method Name: getEarliestContactDate Method Description:this method
	 * retrieves the earliest 24HR non-NULL DT CONTACT OCCURRED for a given APS
	 * INV Stage
	 * 
	 * @param apsInvStageDto
	 * @return ContactDateDto
	 * @throws DataNotFoundException
	 */
	public ContactDateDto getEarliestContactDate(ApsInvStageDto apsInvStageDto);

}
