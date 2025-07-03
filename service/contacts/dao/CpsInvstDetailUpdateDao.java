package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.xmlstructs.inputstructs.InitialContactUpdateDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CpsInvstDetailUpdateDao Aug 2, 2018- 6:31:56 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface CpsInvstDetailUpdateDao {
	/**
	 * 
	 * Method Name: updateInitialContactDate Method Description:This method
	 * updates CPS_INVST_DETAIL table.
	 * 
	 * @param initialContactUpdateDto
	 * @return long
	 */
	public long updateInitialContactDate(InitialContactUpdateDto initialContactUpdateDto);

}
