/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Cses15dDao
 * Aug 18, 2017- 11:41:34 AM © 2017 Texas Department of Family and Protective
 * Services
 */
package us.tx.state.dfps.service.placement.dao;

import us.tx.state.dfps.common.dto.NumberOfRowsDto;
import us.tx.state.dfps.common.dto.PersonAssignedIdToDoDto;
import us.tx.state.dfps.service.common.request.LevelOfCareRtrvReq;
import us.tx.state.dfps.service.common.request.PlacementReq;
import us.tx.state.dfps.service.placement.dto.*;
import us.tx.state.dfps.web.todo.bean.ToDoStagePersonDto;

import java.util.Date;
import java.util.List;

public interface PersonLocPersonDao {

	/**
	 * 
	 * Method Name: cses15dQUERYdam Method Description: It retrieves a single
	 * row from the Person_Loc table
	 * 
	 * @param pInputDataRec
	 * @return List<Cses15doDto> @
	 */
	public List<PersonLocPersonOutDto> fetchPersonLOCByIdPlocEvent(Long idPlocEvent);

	/**
	 * 
	 * Method Name: CINV51DQUERYdam Method Description:This Method Rtrv
	 * (CINV51DQUERY service) person given role of PC.
	 * 
	 * @param pCCMN45DInputRec
	 * @return List<EventIdOutDto>
	 */
	public List<PersonAssignedIdToDoDto> retrievePersonByRoleAndStage(ToDoStagePersonDto toDoStagePersonDto);

	/**
	 * 
	 * Method Name: CSUB81DQUERYdam Method Description: Retrieve (CSUB81D)from
	 * Person LOC
	 * 
	 * @param pCCMN45DInputRec
	 * @return List<EventIdOutDto>
	 */
	public List<NumberOfRowsDto> checkForAuthorizedPLOC(PersonLevelOfCareDto personLevelOfCareDto);

	/**
	 * 
	 * Method Name: CSEC33D Method Description: This will retrieve a row from
	 * PERSON LOC table using ID PERSON With an input date that falls between
	 * the start and finish date.
	 * 
	 * @param pInputDataRec
	 * @return PersonLocOutDto @
	 */
	public PersonLocOutDto getPersonLocById(PersonLocInDto pInputDataRec);

	/**
	 * 
	 * Method Name: checkIfALOCServiceRecordExistsForInsert Method Description:
	 * Check if there's any record of this ID_PERSON with an ALOC with in this
	 * stage.
	 *
	 * @param Long,
	 *            Long
	 * @return PLOCDetailDto @
	 */
	public List<PLOCDetailDto> checkIfALOCServiceRecordExistsForInsert(PLOCDetailInDto pLOCDetailInDto);

	/**
	 * 
	 * Method Name: alocServiceInsertValidation1
	 * 
	 * Method Description: VALIDATE 1: Check if new records overlaps other
	 * records on LEFT (works whether new record overlaps 1 or more existing
	 * records
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto @
	 */
	public List<PLOCDetailDto> alocServiceInsertValidation1(PLOCDetailInDto pLOCDetailInDto);

	/**
	 * 
	 * Method Name: alocServiceInsertValidation2 Method Description: Check if
	 * new records overlaps other records on RIGHT (works whether new record
	 * overlaps 1 or more existing records
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto @
	 */
	public List<PLOCDetailDto> alocServiceInsertValidation2(PLOCDetailInDto pLOCDetailInDto);

	/**
	 * 
	 * Method Name: alocServiceInsertValidation3
	 * 
	 * Method Description: Check if new records is either identical OR within a
	 * record
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto @
	 */
	public List<PLOCDetailDto> alocServiceInsertValidation3(PLOCDetailInDto pLOCDetailInDto);

	/**
	 * 
	 * Method Name: alocServiceInsertValidation4
	 * 
	 * Method Description: Check if the gap on LEFT of hI_dtDtPlocStart is
	 * bigger than 1 day. SELECT statement will return record if it finds one,
	 * which means gap is >= 1.0 day ==> ERROR!
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto @
	 */
	public List<PLOCDetailDto> alocServiceInsertValidation4(PLOCDetailInDto pLOCDetailInDto);

	/**
	 * 
	 * Method Name: alocServiceInsertValidation5 Method Description: Check if
	 * the gap on RIGHT of hI_dtDtPlocStart is bigger than 1 day. SELECT
	 * statement will return record if it finds one, which means gap is >= 1.0
	 * day ==> ERROR!
	 * 
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto @
	 */
	public List<PLOCDetailDto> alocServiceInsertValidation5(PLOCDetailInDto pLOCDetailInDto);

	/**
	 * 
	 * Method Name: autoGeneratePlocEventId Method Description: Auto Generate
	 * ploc event id
	 * 
	 * @param
	 * @return Long @
	 */

	public long autoGeneratePlocEventId();

	/**
	 * 
	 * Method Name: newPlocRecordInsertion Method Description: When all
	 * validations are passed insert new record
	 * 
	 * @param PLOCDetailInDto
	 * @return void @
	 */

	public void newPlocRecordInsertion(PLOCDetailInDto pLOCDetailInDto);

	/**
	 * 
	 * Method Name: checkIfPlocExistBeforeUpdate Method Description: Check if
	 * there's any record at all. It should already exist in order to do an
	 * update.
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto @
	 */

	public List<PLOCDetailDto> checkIfPlocExistBeforeUpdate(PLOCDetailInDto pLOCDetailInDto);

	/**
	 * 
	 * Method Name: checkPlocUpdateValidation1 Method Description: check for
	 * LEFT-SIDE OVERLAP If new START_DATE overlaps any of its LEFT record(s)
	 * (If its overlaps some, then it must at least overlaps its immediate
	 * previous record, and that's what we want to know)
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto @
	 */
	public List<PLOCDetailDto> checkPlocUpdateValidation1(PLOCDetailInDto pLOCDetailInDto, Date currPlocStart);

	/**
	 * 
	 * Method Name: checkPlocUpdateValidation2 Method Description: check for
	 * RIGHT-SIDE OVERLAP If new START_DATE overlaps any of its RIGHT record(s)
	 * (If its overlaps some, then it must at least overlaps its immediate next
	 * record, and that's what we want to know)
	 * 
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto @
	 */
	public List<PLOCDetailDto> checkPlocUpdateValidation2(PLOCDetailInDto pLOCDetailInDto, Date currPlocEnd);

	/**
	 * 
	 * Method Name: checkPlocUpdateValidation3 Method Description: Gap LEFT of
	 * hI_dtDtPlocStart Check this gap ONLY IF hI_dtDtPlocStart <>
	 * curr_ploc_star because: if the 2 are the same, then the user does NOT
	 * want to update that end. Only when the 2 are different does it mean that
	 * the user wants to update that end
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto @
	 */
	public List<PLOCDetailDto> checkPlocUpdateValidation3(PLOCDetailInDto pLOCDetailInDto, Date currPlocStart);

	/**
	 * 
	 * Method Name: checkPlocUpdateValidation4 Method Description: Gap RIGHT of
	 * hI_dtDtPlocEnd Check this gap ONLY IF hI_dtDtPlocEnd <> curr_ploc_end
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto @
	 */
	public List<PLOCDetailDto> checkPlocUpdateValidation4(PLOCDetailInDto pLOCDetailInDto, String dtPlocEndDate,
			Date currPLOCEnd);

	/**
	 * 
	 * Method Name: updatePlocRecord Method Description: New record could be: 1.
	 * RLOC type: Just update it regardless if time overlaps 2. non-RLOC type:
	 * Pass all validation (supposing it is requeted to do so) Update current
	 * record with information from host input variables DO NOT add CD_PLOC_TYPE
	 * in this UDPATE because it is not updateable
	 * 
	 * @param PLOCDetailInDto
	 * @return void
	 */

	public void updatePlocRecord(PLOCDetailInDto pLOCDetailInDto);

	/**
	 * 
	 * Methods for DAM CAUD11D . All the validations methods follow.
	 */

	/**
	 * 
	 * Method Name: checkIfPlocExistBeforeInsertion Method Description: Check if
	 * there's any record of this ID_PERSON and not RLOC. If none, then
	 * everything passed. No need to go through all these validation. If some,
	 * then must go through all checks.
	 * 
	 * @param Long,
	 *            Long
	 * @return PLOCDetailDto @
	 */

	public List<PLOCDetailDto> checkIfPlocExistBeforeInsertion(Long idPerson, String plocType);

	/**
	 * 
	 * Method Name: plocInsertionValidation1
	 * 
	 * Method Description: VALIDATE 1: Check if new records overlaps other
	 * records on LEFT (works whether new record overlaps 1 or more existing
	 * records
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto @
	 */
	public List<PLOCDetailDto> plocInsertionValidation1(PLOCDetailInDto pLOCDetailInDto);

	/**
	 * 
	 * Method Name: plocInsertionValidation2 Method Description: Check if new
	 * records overlaps other records on RIGHT (works whether new record
	 * overlaps 1 or more existing records
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto @
	 */
	public List<PLOCDetailDto> plocInsertionValidation2(PLOCDetailInDto pLOCDetailInDto);

	/**
	 * 
	 * Method Name: plocInsertionValidation3
	 * 
	 * Method Description: Check if new records is either identical OR within a
	 * record /*
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto @
	 */

	public List<PLOCDetailDto> plocInsertionValidation3(PLOCDetailInDto pLOCDetailInDto);

	/**
	 * 
	 * Method Name: plocInsertionValidation4
	 * 
	 * Method Description: Check if the gap on LEFT of hI_dtDtPlocStart is
	 * bigger than 1 day. SELECT statement will return record if it finds one,
	 * which means gap is >= 1.0 day ==> ERROR!
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto @
	 */
	public List<PLOCDetailDto> plocInsertionValidation4(PLOCDetailInDto pLOCDetailInDto);

	/**
	 * 
	 * Method Name: plocInsertionValidation5 Method Description: Check if the
	 * gap on RIGHT of hI_dtDtPlocStart is bigger than 1 day. SELECT statement
	 * will return record if it finds one, which means gap is >= 1.0 day ==>
	 * ERROR!
	 * 
	 * 
	 * @param PLOCDetailInDto
	 * @return PLOCDetailDto @
	 */
	public List<PLOCDetailDto> plocInsertionValidation5(PLOCDetailInDto pLOCDetailInDto);
	
	/**
	 * Method Name: updatePloc 
	 * Method Description:to update ploc for TEP placement
	 * @param startDate
	 * @param idPerson
	 */
	public void updatePloc(Date startDate, Long idPerson);

	
	/**
	 * Method Name: updateServiceLevel 
	 * Method Description:to update ploc for TFC placement
	 * @param placementReq
	 * @param placementType
	 * @return LevelOfCareRtrvReq
	 */
	LevelOfCareRtrvReq updateServiceLevel(PlacementReq placementReq, String placementType);

	/**
	 * Method Name: updateServiceLevel
	 * Method Description:to update ploc for QRTP placement
	 * @param placementReq
	 * @param placementType
	 * @return LevelOfCareRtrvReq
	 */
	LevelOfCareRtrvReq updateServiceLevelForQRTP(PlacementReq placementReq, String placementType);

	/**
	 * Method Name: getQrtpPlacementStartDate
	 * Method Description:to retrieve placement start date for QRTP placement
	 * @param caseId
	 * @param stageId
	 * @return Date
	 */
	Date getQrtpPlacementStartDate(Long caseId, Long stageId);

	List<LevelOfCareRtrvReq> fetchOpenServiceLevels(PlacementReq placementReq);
}
