package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.PersonEthnicityInDto;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Clss80 Aug 5, 2017- 11:44:18 AM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface PersonSelectEthnicityDao {

	/**
	 * 
	 * Method Name: getPersonEthnicity Method Description: This method will get
	 * data from PERSON_ETHNICITY table.
	 * 
	 * @param personEthnicityInDto
	 * @return List<PersonEthnicityOutDto>
	 */
	public List<PersonEthnicityOutDto> getPersonEthnicity(PersonEthnicityInDto personEthnicityInDto);
}
