package us.tx.state.dfps.service.servicedlvryclosure.dao;

import java.util.HashMap;
import java.util.List;

import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.workload.dto.GuardianshipDto;
import us.tx.state.dfps.service.workload.dto.GuardianDetailsDto;
import us.tx.state.service.servicedlvryclosure.dto.ClosureNotificationLettersDto;
import us.tx.state.service.servicedlvryclosure.dto.DlvryClosureApproversDto;
import us.tx.state.service.servicedlvryclosure.dto.DlvryClosureValidationDto;
import us.tx.state.service.servicedlvryclosure.dto.OutcomeMatrixDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * Dlvry closure Dao May 18, 2018- 2:45:11 PM © 2017 Texas Department of Family
 * and Protective Services
 */
public interface ServiceDlvryClosureDao {

	/**
	 * Method Name: (CSVC20D) getPersonCount Method Description:This method
	 * counts the number of people on the Person table where the date of death
	 * is NULL based on the stage id
	 * 
	 * @param idStage
	 * @return count
	 */
	public int getPersonCount(Long idStage);

	/**
	 * Method Name: (CINV96Ddam) getOutcomeMatrixDetails Method Description:
	 * This method retrieves all the Outcome Matrix Information based on EventId
	 * for INV and SVS
	 * 
	 * @param idEvent
	 * @return List
	 */
	public List<OutcomeMatrixDto> getOutcomeMatrixDetails(Long idEvent, String cdReqFunction);

	/**
	 * Method Name: (CSVC26Ddam) updateOutcomeMatrix Method Description: dam
	 * call to updates the outcome matrix
	 * 
	 * @param dlvryClosureValidationDto
	 * @return boolean
	 */
	public boolean updateOutcomeMatrix(DlvryClosureValidationDto dlvryValidationDto, String reqFunction);

	/**
	 * Method Name: (CSVC19Ddam) getEventStatus Method Description: This DAM
	 * selects the event status from the Event table based on the contact type
	 * and event status passed
	 * 
	 * @param dlvryClosureValidationDto
	 * @return boolean
	 */
	public String getEventStatus(DlvryClosureValidationDto dlvryValidationDto, String contactType);

	/**
	 * Method Name: (CSVC27Ddam) updateEvent Method Description: This DAM
	 * updates the event status on the Event table based on the stage Id and
	 * event type
	 * 
	 * @param dlvryClosureValidationDto
	 * @return boolean
	 */
	public boolean updateEvent(DlvryClosureValidationDto dlvryValidationDto, String reqFunction);

	/**
	 * Method Name: (CCMN56D) getApproversList Method Description: to get
	 * approvallist based on eventId
	 * 
	 * @param idApproval
	 * @return List
	 */
	public List<DlvryClosureApproversDto> getApproversList(long idApproval);

	/**
	 * Method Name: (CLSCB6D) guardianshipList Method Description: DAM call to
	 * reterive the guradianship list
	 * 
	 * @param idStage
	 * @param cdEventType
	 * @param idEventStage
	 * @return List
	 */
	public List<GuardianshipDto> guardianshipList(String cdEventType, Long idEventStage);

	/**
	 * Method Name: (CSVC48D) getLegalActionCount Method Description: fetch
	 * legal action count based on idperson and idcase
	 * 
	 * @param idPerson
	 * @param idCase
	 * @return int
	 */
	public int getLegalActionCount(Long idPerson, Long idCase);

	/**
	 * Method Name: (CSVC49D) getGuardianshipStatus Method Description: to get
	 * guardianship status based on idcase
	 * 
	 * @param idCase
	 * @return List
	 */
	public List<GuardianshipDto> getGuardianshipStatus(Long idCase);

	/**
	 * Method Name: (CCMNB6D) retrieveIdCase Method Description:retrieve
	 * DlvryClosureValidationDto based on id stage
	 * 
	 * @param idStage
	 * @returnDlvryClosureValidationDto
	 */
	public DlvryClosureValidationDto retrieveIdCase(Long idStage);

	/**
	 * Method Name: getPersonIdInFDTC Method Description: retrieve persons in
	 * fdtc based on id case
	 * 
	 * @param caseId
	 * @return getPersonIdInFDTC
	 */
	@SuppressWarnings("rawtypes")
	public HashMap getPersonIdInFDTC(Long caseId);

	/**
	 * Method Name: getMostRecentFDTCSubtype Method Description: retrieve recent
	 * FDTC based on id person
	 * 
	 * @param personId
	 * @return getMostRecentFDTCSubtype
	 */
	public HashMap<String, String> getMostRecentFDTCSubtype(Long personId);

	/**
	 * Method Name: getClosureNotificationletters Method Description: This
	 * method is to fetch all records from Database
	 * 
	 * @param idStage
	 * @return
	 */
	public List<ClosureNotificationLettersDto> getClosureNotificationletters(Long idStage);

	/**
	 * Method Name: getSDMAssessmentCount Method Description: get Safety
	 * Assessment Count from Database
	 * 
	 * @param idStage
	 * @return
	 */
	public boolean getSDMAssessmentCount(Long idStage);

	/**
	 * 
	 * Method Name: getPrintTaskExistsSql Method Description: this method is
	 * used to get check if the print task exists for the logged in person for a
	 * given case , satge and event .
	 * 
	 * @param idCase
	 * @param idStage
	 * @param idEvent
	 * @return
	 * 
	 */
	public CommonBooleanRes getPrintTaskExistsFPR(Long idCase, Long idStage, Long idEvent);
	
	/**
	 * Method Name: getPCHasOpenSUBStage Method Description: This method is to check
	 * whether the respective Case has an Open SUB Stage or not
	 * 
	 * @param idCase
	 * @return boolean
	 */
	public boolean getPCHasOpenSUBStage(Long idCase);

	/**
	 * Method Name: (CLSC41D) guardianshipListByStageId Method Description: DAM call to
	 * reterive the guradianship list
	 *
	 * @param cdEventType
	 * @param idStage
	 * @return List
	 */

	List<GuardianDetailsDto> getGuardianDetailsbyEventStage(String cdEventType, Long idStage);
}
