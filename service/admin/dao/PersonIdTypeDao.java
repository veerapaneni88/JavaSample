package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.PersonIdTypeInDto;
import us.tx.state.dfps.service.admin.dto.PersonIdTypeOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for Csesf7 Aug 5, 2017- 11:17:10 AM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface PersonIdTypeDao {

	/**
	 * 
	 * Method Name: getSSNforPersonID Method Description: This method will
	 * retrieve SSN for a Person from Person ID table.
	 * 
	 * @param personIdTypeInDto
	 * @return List<PersonIdTypeOutDto>
	 */
	public List<PersonIdTypeOutDto> getSSNforPersonID(PersonIdTypeInDto personIdTypeInDto);
	
	/**
	 * 
	 *Method Name:	verifyYouthInNYDTSurvey
	 *Method Description: This method checks for a Youth in NYTD survey.
	 *@param idStage
	 *@return
	 */
	public Boolean verifyYouthInNYDTSurvey(Long idStage) ;
}
