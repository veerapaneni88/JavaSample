package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.PersonRaceInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceInsUpdDelOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Caudd5 Aug 10, 2017- 2:09:12 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface PersonRaceInsUpdDelDao {

	/**
	 * 
	 * Method Name: updatePersonRaceRecord Method Description: update the Person
	 * Race record Caudd5d
	 * 
	 * @param personRaceInsUpdDelInDto
	 * @return PersonRaceInsUpdDelOutDto
	 */
	public PersonRaceInsUpdDelOutDto updatePersonRaceRecord(PersonRaceInsUpdDelInDto personRaceInsUpdDelInDto);
}
