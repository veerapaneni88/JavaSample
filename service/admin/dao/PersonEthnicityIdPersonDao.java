package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.PersonEthnicityIdPersonInDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Caudd4d Aug 10, 2017- 12:50:53 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface PersonEthnicityIdPersonDao {
	/**
	 * 
	 * Method Name: deletePersonEthnicity Method Description: Delete the
	 * previous ethinicity record for the person,if exists and insert a new one.
	 * 
	 * @param personEthnicityIdPersonInDto
	 * @return int
	 */
	public int deletePersonEthnicity(PersonEthnicityIdPersonInDto personEthnicityIdPersonInDto);

}
