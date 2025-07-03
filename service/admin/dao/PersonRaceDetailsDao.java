package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.PersonRaceInDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Clss79 Aug 5, 2017- 11:38:00 AM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface PersonRaceDetailsDao {

	/**
	 * 
	 * Method Name: getRaceDetails Method Description: This method will get data
	 * from Person Race table.
	 * 
	 * @param personRaceInDto
	 * @return List<PersonRaceOutDto>
	 */
	public List<PersonRaceOutDto> getRaceDetails(PersonRaceInDto personRaceInDto);
}
