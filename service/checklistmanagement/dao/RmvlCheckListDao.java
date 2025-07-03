package us.tx.state.dfps.service.checklistmanagement.dao;

import java.util.List;
import java.util.Map;

import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstLinkDto;
import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstLookupDto;
import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstRspnDto;
import us.tx.state.dfps.service.conservatorship.dto.CnsrvtrshpRemovalDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * RmvlCheckListDao will defined all operation defined related RmvlCheckList
 * module. Feb 9, 2018- 2:02:21 PM © 2017 Texas Department of Family and
 * Protective Services
 */

public interface RmvlCheckListDao {
	/**
	 *
	 * Method Name: getRmvlChcklsts Method Description: This method will
	 * retrieve all the checklst record from RMVL_CHCKLST_LOOKUP Table
	 *
	 * @return @
	 */
	public List<RmvlChcklstLookupDto> getRmvlChcklsts();

	/**
	 *
	 * Method Name: getRmvlChcklstDtl Method Description: Retrieves a checklist
	 * given the ID. This method queries RMVL_CHCKLST_LOOKUP,
	 * RMVL_CHCKLST_SCTN_LOOKUP and RMVL_CHCKLST__TASK_LOOKUP table
	 *
	 * @param checklistId
	 * @return @
	 */
	public RmvlChcklstLookupDto getRmvlChcklstDtl(Long checklistId, Long idPerson, Long idRmvlChcklstLink);

	/**
	 *
	 * Method Name: copyRmvlChcklst Method Description: this method creates a
	 * new checklist from an existing checklist. This method queries
	 * RMVL_CHCKLST_LOOKUP, RMVL_CHCKLST_SCTN_LOOKUP and
	 * RMVL_CHCKLST__TASK_LOOKUP table
	 *
	 * @param checklistId
	 * @return @
	 */
	public RmvlChcklstLookupDto copyRmvlChcklst(Long checklistId);

	/**
	 *
	 * Method Name: deleteRmvlChcklstTaskLookupDtl Method Description:Perform a
	 * soft delete on a task given the Task ID. It will update the
	 * RMVL_CHCKLST__TASK_LOOKUP table
	 *
	 * @param taskId
	 * @
	 */
	public void deleteRmvlChcklstTaskLookupDtl(Long taskId);

	/**
	 *
	 * Method Name: saveRmvlChcklst Method Description: create a new checklist
	 * record. This method will insert a new record in RMVL_CHCKLST_LOOKUP,
	 * RMVL_CHCKLST_SCTN_LOOKUP and RMVL_CHCKLST__TASK_LOOKUP table
	 *
	 * @param checklist
	 * @param IndSave
	 * @return @
	 */
	public Long saveRmvlChcklst(RmvlChcklstLookupDto checklist, String IndSave);

	/**
	 *
	 * Method Name: updateRmvlChcklst Method Description: This method will
	 * update the a checklist.This method will update the RMVL_CHCKLST_LOOKUP,
	 * RMVL_CHCKLST_SCTN_LOOKUP and RMVL_CHCKLST__TASK_LOOKUP table
	 *
	 * @param checklist
	 * @return @
	 */
	public RmvlChcklstLookupDto updateRmvlChcklst(RmvlChcklstLookupDto checklist, Long idPerson, Long idRmvlChcklstLink);

	/**
	 *
	 * Method Name: getRmvlChcklstLink Method Description: Retrieves records
	 * from RMVL_CHCKLST__LINK given the idPerson and idStage
	 *
	 * @param idPerson
	 * @return @
	 */
	public List<RmvlChcklstLinkDto> getRmvlChcklstLink(Long idPerson, Long idStage, Long idRemovalEvent);

	/**
	 *
	 * Method Name: indRecordExist Method Description: Check if a record exist
	 * in RMVL_CHCKLST__LINK table given the idEvent.
	 *
	 * @param idRmvlEvent
	 * @return @
	 */
	public boolean indRecordExist(Long idRmvlEvent);

	/**
	 *
	 * Method Name: getPersonList Method Description: Retrieves the person List
	 * given the idEvent
	 *
	 * @param idRmvlEvent
	 * @return @
	 */
	public List<CnsrvtrshpRemovalDto> getPersonList(Long idRmvlEvent);

	/**
	 *
	 * Method Name: saveRmvlChcklstRspn Method Description: Insert a new record
	 * in RMVL_CHCKLST__RSPN_LOOKUP table
	 *
	 * @param checklstRspn
	 * @return @
	 */
	public List<Long> saveRmvlChcklstRspn(List<RmvlChcklstRspnDto> checklstRspn);

	/**
	 *
	 * Method Name: updateRmvlChcklstRspn Method Description: update a record in
	 * RMVL_CHCKLST__RSPN_LOOKUP table
	 *
	 * @param checklstRspn
	 * @return @
	 */
	public String updateRmvlChcklstRspn(List<RmvlChcklstRspnDto> checklstRspn);

	/**
	 *
	 * Method Name: getRmvlChcklstRspn Method Description: Retrive a list of
	 * response from the RMVL_CHCKLST__RSPN_LOOKUP table given idPerson.
	 *
	 * @param IdPerson
	 * @return @
	 */
	public List<RmvlChcklstRspnDto> getRmvlChcklstRspn(Long IdPerson);

	/**
	 *
	 * Method Name: saveRmvlChcklstLink Method Description: Insert a new record
	 * in RMVL_CHCKLST__LINK table
	 *
	 * @param rmvlChcklstLinkDto
	 * @return @
	 */
	public Map<Long,Long> saveRmvlChcklstLink(List<RmvlChcklstLinkDto> rmvlChcklstLinkDto);

	/**
	 *
	 * Method Name: updateRmvlChcklstLink Method Description: update a record in
	 * RMVL_CHCKLST__LINK table
	 *
	 * @param rmvlChcklstLinkDto
	 * @return @
	 */
	public String updateRmvlChcklstLink(List<RmvlChcklstLinkDto> rmvlChcklstLinkDto);

}
