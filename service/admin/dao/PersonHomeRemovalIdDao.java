package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.PersonHomeRemovalInDto;
import us.tx.state.dfps.service.admin.dto.PersonHomeRemovalOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Cses46DAO Aug 5, 2017- 2:21:25 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface PersonHomeRemovalIdDao {

	/**
	 * 
	 * Method Name: getPersonHomeRemoval Method Description: This method will
	 * get data from PERSON_HOME_REMOVAL table.
	 * 
	 * @param personHomeRemovalInDto
	 * @return List<PersonHomeRemovalOutDto>
	 */
	public List<PersonHomeRemovalOutDto> getPersonHomeRemoval(PersonHomeRemovalInDto personHomeRemovalInDto);
}
