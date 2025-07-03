package us.tx.state.dfps.service.arstageprog.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import us.tx.state.dfps.common.dto.StageClosureValueDto;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.dto.PersonEligibilityDto;
import us.tx.state.dfps.service.person.dto.PersonEligibilityValueDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Dao
 * Interface for functions required for implementing ARStageProg functionality
 * Sep 6, 2017- 7:51:48 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface ArStageProgDao {

	/**
	 * Method Name: insertIntoStageLink Method Description:This function inserts
	 * new Record into Stage_Link table. This is basically used to link new
	 * Stage with Old Stage.
	 * 
	 * @param stageValueBeanDto
	 * @param newStageId
	 * @ -------------------
	 */
	public void insertIntoStageLink(StageValueBeanDto stageValueBeanDto, Long newStageId);

	/**
	 * Method Name: insertIntoEvent Method Description: This method inserts
	 * single Record into Event Table.
	 * 
	 * @param stageBean
	 * @param newStageId
	 * @param idCreatedPerson
	 * @return Long ------------------
	 */
	public Long insertIntoEvent(StageValueBeanDto stageBean, Long newStageId, Long idCreatedPerson);

	/**
	 * Method Name: getPersonEligibilityRecord Method Description:Verify was
	 * there any open PERSON_ELIGIBILITY record already exist for each person
	 * 
	 * @param idPerson
	 * @return
	 * @throws DataNotFoundException
	 */
	public PersonEligibilityDto getPersonEligibilityRecord(Integer idPerson) throws DataNotFoundException;

	/**
	 * Method Name: getAllEligiblePrinciplesArStage Method Description:Inserts
	 * Stage Ids into Stage Link table
	 * 
	 * @param idStage
	 * @return List<Integer>
	 * @throws DataNotFoundException
	 */
	public List<Integer> getAllEligiblePrinciplesArStage(Long idStage) throws DataNotFoundException;

	/**
	 * Method Name: updatePersonEligibility Method Description: Update
	 * PERSON_ELIGIBILITY records with PersonEligibilityProgOpenCode
	 * 
	 * @param personEligibilityValueDto
	 */
	public void updatePersonEligibility(PersonEligibilityValueDto personEligibilityValueDto)
			throws DataNotFoundException;

	/**
	 * Method Name: getEligibilityStartDate Method Description:Earliest of
	 * (Stage progress date from A-R to FPR and Approved Service Auths begin
	 * date in A-R Stage)
	 * 
	 * @param idStage
	 * @return Date
	 * @throws DataNotFoundException
	 */
	public Date getEligibilityStartDate(Long idStage) throws DataNotFoundException;

	/**
	 * 
	 * Method Name: createPersonEligibility Method Description:Create New
	 * PERSON_ELIGIBILITY record.
	 * 
	 * @param idPerson
	 * @param dtPersEligStart
	 * @return
	 * @throws DataNotFoundException
	 */
	public Long createPersonEligibility(Long idPerson, Date dtPersEligStart) throws DataNotFoundException;

	/**
	 * Method Name: closeStage Method Description: This method closes the
	 * specified Stage and also updates the 'reason for stage closing'.
	 * 
	 * @param stageClosureValueDto
	 * @return long
	 * 
	 */
	public long closeStage(StageValueBeanDto stageValueBeanDto);

	/**
	 * Method Name: updateStagePersonLink Method Description: This method
	 * updates StagePersonLink table.
	 * 
	 * @param stagePersonValueDto
	 * @return long
	 * 
	 */
	public long updateStagePersonLink(StagePersonValueDto stagePersonValueDto);

	/**
	 * Method Name: updateStagePersonLink Method Description: This method
	 * updates StagePersonLink table. It will update Type, Role and Rel-Int
	 * 
	 * @param idOldPerson
	 * @param idNewPerson
	 * @param idStage
	 * @param idCase
	 * @param cdRole
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long updateStagePersonLink(Long idOldPerson, Long idNewPerson, Long idStage, Long idCase, String cdRole)
			throws DataNotFoundException;

	/**
	 * Method Name: createEvent Method Description: Creates a new Event. This
	 * method uses all the columns in the table.Generic function
	 * 
	 * @param eventValueDto
	 * @return int
	 * 
	 */
	public int createEvent(EventValueDto eventValueDto);

	/**
	 * Method Name: closeStage Method Description: This method closes the
	 * specified Stage and also updates the 'reason for stage closing'.
	 * 
	 * @param stageClosureValueDto
	 * @return long
	 * 
	 */
	public long closeStage(StageClosureValueDto stageClosureValueDto);

	/**
	 * Method Name: fetchForwardPersonsForStagePersons Method Description: This
	 * method will fetch the forward persons for all Stage persons
	 * 
	 * @param idStage
	 * @return Map<Integer,Integer>
	 * 
	 */
	public Map<Integer, Integer> fetchForwardPersonsForStagePersons(Long idStage) throws DataNotFoundException;

	/**
	 * Method Name: fetchIntakeAllegations Method Description: This method
	 * fetches Intake Allegations.
	 * 
	 * @param idIntakeStage
	 * @return List<AllegationDto>
	 * 
	 */
	public List<AllegationDto> fetchIntakeAllegations(Long idIntakeStage);

	/**
	 * Method Name: createInvestigationAllegations Method Description: Creates
	 * Investigation Allegations For A-R Stage.
	 * 
	 * @param idToStage
	 * @param idCase
	 * @param allegationDtoList
	 * @return Long[]
	 * @throws DataNotFoundException
	 */
	public Long[] createInvestigationAllegations(Long idToStage, Long idCase, List<AllegationDto> allegationDtoList)
			throws DataNotFoundException;

	/**
	 * Method Name: insertCPSInvestigationDetail Method Description:This method
	 * inserts the INVESTIGATION Details when INV Stage is created
	 * 
	 * @param eventID
	 * @param caseID
	 * @param stageID
	 * @param idUser
	 * @param inTakeStartDate
	 * @return Long
	 * 
	 */
	public Long insertCPSInvestigationDetail(Long caseID, Long stageID, Long idUser, Date inTakeStartDate,
			Long idEvent);

	/**
	 * Method Name: updatePriorStageAndIncomingCallDate Method
	 * Description:Updates the Prior Stage and Incoming call date. A trigger
	 * exists for this but will not work for this scenario. Stage link issue.
	 * 
	 * @param idNewStage
	 * @param idCase
	 * @param idPriorStage
	 * @param incomingCallDate
	 * 
	 * @throws DataNotFoundException
	 */
	public void updatePriorStageAndIncomingCallDate(Long idNewStage, Long idCase, Long idPriorStage,
			Timestamp incomingCallDate);

	/**
	 * Method Name: fetchRecordRetentionDate Method Description:This method will
	 * fetch the record retention type month and Final records retention date
	 * 
	 * @return Date
	 * @throws DataNotFoundException
	 */
	public Date fetchRecordRetentionDate() throws DataNotFoundException;

	/**
	 * Method Name: createRecordRetentionRecord Method Description:lternative
	 * Response Records Retention A-R stage needs new retention code,
	 * REC_RETEN_TYPE row, and retention date calculation modifications Creates
	 * a new record REtention record
	 * 
	 * @param idCase
	 * @param recordRetentionType
	 * @param destroyActualDate
	 * @param eligDate
	 * @throws DataNotFoundException
	 */
	public void createRecordRetentionRecord(int idCase, String recordRetentionType, Date destroyActualDate,
			Date eligDate);
}
