package us.tx.state.dfps.service.personutility.service;

import java.util.HashSet;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PersonUtilityService Oct 10, 2017- 10:26:08 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface PersonUtilityService {

	/**
	 * Method Name: isPersonInOneOfThesePrograms Method Description:Returns true
	 * if given person exists in at least one given stage program
	 * 
	 * @param idPerson
	 * @param hashSet
	 * @return Boolean
	 */
	public Boolean isPersonInOneOfThesePrograms(Long idPerson, HashSet hashSet);

	/**
	 * Method Name: getPersonRaceEthnicity Method Description:returns true if
	 * race and ethnicity data is not found for one or more Principals.
	 * 
	 * @param personId
	 * @return RaceEthnicityDto @
	 */
	// public RaceEthnicityDto getPersonRaceEthnicity(Long personId) ;

	/**
	 * Method Name: isPRNRaceStatMissing Method Description:if race and
	 * ethnicity data has been entered for Principals in given stage
	 * 
	 * @param idStage
	 * @return Boolean
	 */
	public Boolean isPRNRaceStatMissing(Long idStage);

	/**
	 * Method Name: isPlcmntPerson Method Description:to check if a person is
	 * associated with an open or closed PCSP placement
	 * 
	 * @param caseId
	 * @param personId
	 * @param cdStage
	 * @return Boolean @
	 */
	public Boolean isPlcmntPerson(Long caseId, Long personId, String cdStage);

	/**
	 * Method Name: isAssmntPerson Method Description:to check if a person is
	 * associated with PROC or COMP
	 * 
	 * @param personId
	 * @param stageId
	 * @return Boolean @
	 */
	public Boolean isAssmntPerson(Long personId, Long stageId);
}
