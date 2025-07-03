package us.tx.state.dfps.service.populateletter.dao;

import java.util.List;

import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CINV63S
 * Class Jan 11, 2018- 3:38:39 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface PopulateLetterDao {

	/**
	 * Method Name: getCaseInfoById Method Description: This method retrieves
	 * all info about principals of the case based upon an id stage. Table
	 * Names:NAME,PERSON_PHONE, PERSON_ADDRESS, ADDRESS_PERSON_LINK, PERSON_ID,
	 * PERSON, STAGE_PERSON_LINK Tux method : CLSC01D
	 * 
	 * @param idStage
	 * @return List<CaseInfoDto>
	 * @throws DataNotFoundException
	 * @
	 */
	public List<CaseInfoDto> getCaseInfoById(Long idStage, String cdStagePersType);

	/**
	 * Method Name: getReporterInfoById Method Description:Retrieves all
	 * information about the REPORTERs in the stage. Tux method : CSEC18D Table
	 * Names:NAME,PERSON_PHONE, PERSON_ADDRESS, ADDRESS_PERSON_LINK, PERSON_ID,
	 * PERSON, STAGE_PERSON_LINK
	 * 
	 * @param idStage
	 * @return List<CaseInfoDto>
	 * @throws DataNotFoundException
	 * @
	 */
	public List<CaseInfoDto> getReporterInfoById(Long idStage);

	/**
	 * Method Name: getPersonInfoByCode CLSC03D Method Description:This DAM
	 * retrieves the name of the person to whom the letter is being sent and the
	 * name of the child participant. Table Name: CODES_TABLES
	 * 
	 * @param aCodeType,bCodeType
	 * @return List<CodesTablesDto>
	 * @throws DataNotFoundException
	 * @
	 */
	public List<CodesTablesDto> getPersonInfoByCode(String aCodeType, String bCodeType);

	/**
	 * Method Name: getAddressMatchById Method Description: TUX Service :CINTA1D
	 * This function returns a count of address matches for a given reporter and
	 * all PRNs in the case. Table Name: stage_person_link ,address_person_link
	 * ,person_address
	 * 
	 * @param idCase
	 * @param idPerson
	 * @return matchNum
	 * @throws DataNotFoundException
	 * @
	 */
	public Long getAddressMatchById(long idCase, long idPerson);
	
	/**
	 * Method Name: getPPMCaseInfoById Method Description: This method retrieves
	 * all info about principals of the case based upon an id stage. Table
	 * Names:NAME,PERSON_PHONE, PERSON_ADDRESS, ADDRESS_PERSON_LINK, PERSON_ID,
	 * PERSON, STAGE_PERSON_LINK CLSC01D ORDER BY SPL.IND_STAGE_PERS_REPORTER
	 * 
	 * @param idStage
	 ** @param cdStagePersType
	 * @return List<CaseInfoDto>
	 */
	public List<CaseInfoDto> getPPMCaseInfoById(Long idStage, String cdStagePersType);

}
