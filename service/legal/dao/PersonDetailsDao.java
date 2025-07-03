package us.tx.state.dfps.service.legal.dao;

import java.util.List;

import us.tx.state.dfps.service.legal.dto.PersonDetailsdiDto;
import us.tx.state.dfps.service.legal.dto.PersonDetailsdoDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for PersonDetails Aug 5, 2017- 11:02:15 AM © 2017 Texas Department of Family
 * and Protective Services
 */
public interface PersonDetailsDao {
	/**
	 * 
	 * Method Name: getPersonRecord Method Description: fetch person record
	 * 
	 * @param pInputDataRec
	 * @return List<PersonDetailsdoDto> @
	 */
	public List<PersonDetailsdoDto> getPersonRecord(PersonDetailsdiDto personDetailsdiDto);

	/**
	 * Method Name: getPersonInformation Method Description: This method is
	 * called from RecordCheckRetrieve EJB used to get Person Details.
	 * 
	 * @param personDetailsdiDto
	 * @return List<PersonDetailsdoDto> throws DataNotFoundException
	 */

	public PersonDetailsdoDto getPersonInformation(PersonDetailsdiDto personDetailsdiDto);
}
