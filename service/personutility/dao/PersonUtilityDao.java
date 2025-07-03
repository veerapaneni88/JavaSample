package us.tx.state.dfps.service.personutility.dao;

import java.util.HashSet;

import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PersonUtilityDao Oct 10, 2017- 10:28:31 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface PersonUtilityDao {

	/**
	 * Method Name: isPersonInOneOfThesePrograms Method Description:Returns true
	 * if given person exists in at least one given stage program
	 * 
	 * @param idPerson
	 * @param hashSet
	 * @return Boolean
	 * @throws DataNotFoundException
	 */
	public Boolean isPersonInOneOfThesePrograms(Long idPerson, HashSet hashSet);

	/**
	 * Method Name: getPRNRaceEthnicityStat Method Description:returns true if
	 * race and ethnicity data is not found for one or more Principals.
	 * 
	 * @param idStage
	 * @return Boolean
	 */
	public Boolean getPRNRaceEthnicityStat(Long idStage);

	/**
	 * Method Name: isPlcmntCaregiverExists Method Description:returns true if
	 * person(caregiver) is associated with PCSP Placement in a Case
	 * 
	 * @param caseId
	 * @param personId
	 * @return Boolean
	 */
	public Boolean isPlcmntCaregiverExists(Long caseId, Long personId);

	/**
	 * Method Name: isAssmntChildOhmExists Method Description:Method returns
	 * true if person(child/other household member) is associated with PCSP
	 * Assessment in a Stage
	 * </p>
	 * 
	 * @param personId
	 * @param stageId
	 * @return Boolean
	 */
	public Boolean isAssmntChildOhmExists(Long personId, Long stageId);

	/**
	 * Method Name: isAssmntCaregiverExists Method Description:Method returns
	 * true if person(caregiver) is associated with PCSP Assessment in a Stage
	 * </p>
	 * 
	 * @param personId
	 * @param stageId
	 * @return Boolean
	 */
	public Boolean isAssmntCaregiverExists(Long personId, Long stageId);

	/**
	 * Method Name: isPlcmntChildExists Method Description:returns true if
	 * person(child) is associated with PCSP Placement in a Case
	 * 
	 * @param caseId
	 * @param personId
	 * @return Boolean
	 */
	public Boolean isPlcmntChildExists(Long caseId, Long personId);

}
