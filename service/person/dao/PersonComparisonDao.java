package us.tx.state.dfps.service.person.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.person.dto.PersonEmailDto;
import us.tx.state.dfps.service.person.dto.PersonIdDto;
import us.tx.state.dfps.service.person.dto.PersonIncomeResourceDto;
import us.tx.state.dfps.service.person.dto.PersonMergeInfoDto;
import us.tx.state.dfps.service.person.dto.PersonPotentialDupDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Dao methods
 * for Person Comparison Form per03o00 May 30, 2018- 3:50:45 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface PersonComparisonDao {

	/**
	 * Method Name: getDemInfo Method Description: This DAM will Join the Person
	 * Merge Table to retrieve the Id Person Forward for the Given host
	 * variable. It joins will the Address, Id, Name and Person table to
	 * retrieve demographic information for the Id Pers Merge Forward. CSEC67D
	 * 
	 * @param idPerson
	 * @return PersonMergeInfoDto
	 */
	public PersonMergeInfoDto getDemInfo(Long idPerson);

	/**
	 * Method Name: getMergeIds Method Description: Display all merged ID_PERSON
	 * for a given ID_PERSON and their latest PRIMARY names, regardless whether
	 * these merged names are invalid or not. CLSC46D
	 * 
	 * @param idPerson
	 * @return List<PersonMergeInfoDto>
	 */
	public List<PersonMergeInfoDto> getMergeIds(Long idPerson);

	/**
	 * Method Name: getPersDup Method Description:Full row retrieval from
	 * PERSON_POTENTIAL_DUP. CLSSB4D
	 * 
	 * @param idPerson
	 * @return PersonPotentialDupDto
	 */
	public PersonPotentialDupDto getPersDup(Long idPerson);

	/**
	 * Method Name: getPersEmail Method Description:Retrieves all rows from
	 * PERSON_EMAIL for input idPerson CLSSB3D
	 * 
	 * @param idPerson
	 * @return List<PersonEmailDto>
	 */
	public List<PersonEmailDto> getPersEmail(Long idPerson);

	/**
	 * Method Name: getPersIncomeResrc Method Description:This DAM will do a
	 * full row retrieval from the INCOME AND RESOURCES table an join with the
	 * PERSON table to retrieve the worker's name using Id Event. CLSS58D
	 * 
	 * @param idPerson
	 * @return List<PersonIncomeResourceDto>
	 */
	public List<PersonIncomeResourceDto> getPersIncomeResrc(Long idPerson);

	/**
	 * Method Name: getPersIntakeInv Method Description:This DAM will retrieve
	 * all of the identifiers for a person from the person_id table. The numbers
	 * are sorted differently for investigation and intake. CINT17D
	 * 
	 * @param idPerson
	 * @return List<PersonIdDto>
	 */
	public List<PersonIdDto> getPersIntakeInv(Long idPerson, Boolean indIntake, Date dtSysTsQuery);

}
