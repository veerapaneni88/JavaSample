package us.tx.state.dfps.service.conservatorship.dao;

import java.util.List;

import us.tx.state.dfps.service.cvs.dto.PersonHomeRemovalSelRemDetlsInDto;
import us.tx.state.dfps.service.cvs.dto.PersonHomeRemovalSelRemDetlsOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for PersonHomeRemovalSelRemDetlsDao Aug 2, 2017- 8:35:21 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface PersonHomeRemovalSelRemDetlsDao {

	/**
	 * 
	 * Method Name: getPersonHomeRemovalDetails Method Description: This method
	 * will get data from PERSON_HOME_REMOVAL table.
	 * 
	 * @param personHomeRemovalSelRemDetlsInDto
	 * @return List<PersonHomeRemovalSelRemDetlsOutDto> @
	 */
	public List<PersonHomeRemovalSelRemDetlsOutDto> getPersonHomeRemovalDetails(
			PersonHomeRemovalSelRemDetlsInDto personHomeRemovalSelRemDetlsInDto);
}
