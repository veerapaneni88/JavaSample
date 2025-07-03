package us.tx.state.dfps.service.person.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.PersonPotentialDup;
import us.tx.state.dfps.service.person.dto.PersonPotentialDupDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: interface
 * for PersonDupDao. Sep 25, 2017- 11:07:54 AM © 2017 Texas Department of Family
 * and Protective Services
 */
public interface PotentialDupDao {

	/**
	 * Method Name: getPersonPotentialDupList Method Description: Returns list
	 * of person Potential Duplicates.
	 * 
	 * @param idPerson
	 * @return List<PersonPotentialDupDto>
	 */
	public List<PersonPotentialDupDto> getPersonPotentialDupList(Long idPerson);

	/**
	 * Method Name: getPersonPotentialDup Method Description:Retrieves potential
	 * Duplicate and other information related to a person given primary key
	 * 
	 * @param idPerson
	 * @return PersonPotentialDupDto @
	 */
	public PersonPotentialDupDto getPersonPotentialDup(Long idPerson);

	/**
	 * Method Name: savePersPotentialDupInfo Method Description:save potential
	 * Duplicate information
	 * 
	 * @param personPotentialDup
	 * @param operation
	 * @return void @
	 */
	public void savePersPotentialDupInfo(PersonPotentialDup personPotentialDup, String operation);

	/**
	 * Method Name: getActivePersonPotentialDupDetail Method Description:
	 * Returns person duplicate given idPerson and idDupPerson.
	 * 
	 * @param idPerson
	 * @param idDupPerson
	 * @return PersonPotentialDupDto
	 */
	public PersonPotentialDupDto getActivePersonPotentialDupDetail(Long idPerson, Long idDupPerson);

}
