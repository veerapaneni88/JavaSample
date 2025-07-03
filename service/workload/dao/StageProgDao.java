package us.tx.state.dfps.service.workload.dao;

import java.util.List;

import us.tx.state.dfps.common.dto.SafetyAssmtDto;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.safetyassessment.dto.SafetyFactorDto;
import us.tx.state.dfps.service.workload.dto.StageProgDto;

public interface StageProgDao {

	/**
	 * 
	 * Method Description: This Method will return the lookup of stage
	 * progression opportunities based upon the CD Stage, CD Stage Program and
	 * CD Stage Reason closed. Also calculates whether or not progression will
	 * be automatic or manual Dam Name: CCMNB8D
	 * 
	 * @param cdStageProgStage
	 * @param cdStageProgProgram
	 * @param cdStageProgRsnClose
	 * @return StageProcDto @
	 */
	public List<StageProgDto> getStgProgroession(String cdStageProgStage, String cdStageProgProgram,
			String cdStageProgRsnClose);

	/**
	 * Method Name: updateStagePersonLink Method Description:This method
	 * retrieves a Record from STAGE_PERSON_LINK using StageId and Id Person.
	 * and updates
	 * 
	 * @param commonHelperReq
	 */
	public void updateStagePersonLink(CommonHelperReq commonHelperReq);

	/**
	 * Method Name: linkPersonToNewStage Method Description:This method links
	 * Person (including Staff) to the new Stage by creating entry into Stage
	 * Person Link table. It first retrieves Person information of given Roles
	 * from Stage Person Link table of the Old Stage and then inserts them into
	 * new Stage.
	 * 
	 * @param commonHelperReq
	 * @
	 */
	public void linkPersonToNewStage(CommonHelperReq commonHelperReq);

	/**
	 * This method retrieves a Record from STAGE_PERSON_LINK using StageId and
	 * Person Role.
	 * 
	 * @param idStage
	 * @param personRoles
	 * 
	 * @return List<StagePersonValueDto> - Person information for the Stage.
	 */
	public List<StagePersonValueDto> selectStagePersonLink(int idFromStage, List<String> requestedRoles);

	/**
	 * Method Name: linkPersonToNewStage Method Description:This method links
	 * Person (including Staff) to the new Stage by creating entry into Stage
	 * Person Link table. It first retrieves Person information of given Roles
	 * from Stage Person Link table of the Old Stage and then inserts them into
	 * new Stage.
	 * 
	 * @param idStage
	 * @param idNewStage
	 * @param personRole
	 * @throws DataNotFoundException
	 * @
	 */
	public void linkPersonToNewStage(Long idStage, Long idNewStage, String personRole) throws DataNotFoundException;

	/**
	 * Method Name: linkNonStaffPersonToNewStage Method Description:This method
	 * links Non-Staff Persons to the new Stage by creating entry into Stage
	 * Person Link table. It first retrieves Person information of given Roles
	 * from Stage Person Link table of the Old Stage and then inserts them into
	 * new Stage.
	 * 
	 * @param idStage
	 * @param idNewStage
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long linkNonStaffPersonToNewStage(Long idStage, Long idNewStage);

	/**
	 * 
	 * Method Name: isAnotherStageOpen Method Description: to find the given
	 * stage is open
	 * 
	 * @param stage
	 * @return Boolean
	 * @throws DataNotFoundException
	 */
	public Boolean isAnotherStageOpen(Long idCase, String cdToStage);

	/**
	 * Method Name: linkSecondaryWorkerToNewStage Method Description: * This
	 * method assigns Secondary Worker to a Stage. It first retrieves Primary
	 * Worker information from Stage Person Link table, substitutes Secondary
	 * Worker Id, Role in the record and inserts a new Record into Stage Person
	 * Link table.
	 * 
	 * @param idStage
	 * @param idSecWorker
	 * @return Long
	 * @throws DataNotFoundException
	 * @
	 */
	public Long linkSecondaryWorkerToNewStage(Long idStage, Long idSecWorker);

	/**
	 * This method batch updates StagePersonLink table using
	 * StagePersonValueBean list.
	 * 
	 * @param spLinkBeans
	 *            ArrayList of StagePersonValueBeans
	 * @return Integer new AllegationIDs
	 * 
	 */
	public Integer updateStagePersonLinks(List<StagePersonValueDto> finalizedList);

	/**
	 * Method Name: insertIntoStagePersonLinks Method Description:This method
	 * batch inserts StagePersonLink table using StagePersonValueBean list.
	 * 
	 * @param StagePersonValueDtoList
	 * @return int
	 * @throws DataNotFoundException
	 */
	public int insertIntoStagePersonLinks(List<StagePersonValueDto> stagePersonValueDtoList)
			throws DataNotFoundException;

	/**
	 * Method Name: queryPageData Method Description:Retrieve the data needed to
	 * build the Safety Assessment page.
	 * 
	 * @param safetyAssmtDto
	 * @return SafetyAssmtDto
	 * @throws DataNotFoundException
	 */
	public SafetyAssmtDto queryPageData(SafetyAssmtDto safetyAssmtDto) throws DataNotFoundException;

	/**
	 * Method Name: getSubStageOpen Method Description:Returns true if SubStage
	 * is Open else returns false
	 * 
	 * @param safetyAssmtDto
	 * @return Boolean
	 * @throws DataNotFoundException
	 */
	public Boolean getSubStageOpen(SafetyAssmtDto safetyAssmtDto) throws DataNotFoundException;

	/**
	 * Method Name: getTodoId Method Description:Returns back an integer
	 * containing Todo Id
	 * 
	 * @param eventId
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long getTodoId(Long eventId) throws DataNotFoundException;

	/**
	 * @param safetyAssmtDto
	 * @return
	 */
	public long addSafetyAssmtDetails(SafetyAssmtDto safetyAssmtDto);

	/**
	 * @param safetyFactorDB
	 * @param safetyAssmtDto
	 * @return
	 */
	public long addAreaDetails(SafetyFactorDto safetyFactorDB, SafetyAssmtDto safetyAssmtDto);

	/**
	 * @param safetyFactorDB
	 * @param safetyAssmtDto
	 * @return
	 */
	public long addFactorDetails(SafetyFactorDto safetyFactorDB, SafetyAssmtDto safetyAssmtDto);

	/**
	 * 
	 * Method Name: updateINVSafetyAssignmentWithARSafetyAssignment Method
	 * Description:Update the safety Assessment indicator suggesting existence
	 * of a approved AR Safety assessment
	 * 
	 * @param idARSafetyAssessment
	 * @param idCase
	 * @param idStage
	 * @return
	 * @throws DataNotFoundException
	 */
	public Long updateINVSafetyAssignmentWithARSafetyAssignment(Long idARSafetyAssessment, Long idCase, Long idStage)
			throws DataNotFoundException;

	/**
	 * Method Name: updateStageLink Method Description:This method batch updates
	 * StageLink table using StageValueBean. currently only updates case id ,
	 * can be used to update other fields
	 * 
	 * @param stageValueBeanDto
	 * @param newStageId
	 * @throws DataNotFoundException
	 */
	public void updateStageLink(StageValueBeanDto stageValueBeanDto, Long newStageId) throws DataNotFoundException;
}
