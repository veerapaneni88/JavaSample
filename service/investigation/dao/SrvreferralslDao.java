package us.tx.state.dfps.service.investigation.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.Contact;
import us.tx.state.dfps.common.domain.CpsChecklist;
import us.tx.state.dfps.common.domain.CpsChecklistItem;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.service.casepackage.dto.PcspDto;
import us.tx.state.dfps.service.common.request.PcspReq;
import us.tx.state.dfps.service.workload.dto.TodoDto;

public interface SrvreferralslDao {

	/**
	 * 
	 * Method Description: This Method will retrieve the CPS checklist item from
	 * table CPS CheckList. Dam Name: CSESA2D
	 * 
	 * @param eventId
	 * @return CpsChecklist @
	 */
	CpsChecklist getCpsChecklistByEventId(Long eventId);

	/**
	 * 
	 * Method Description: This Method will retrieve the earliest non-NULL DT
	 * CONTACT OCCURRED for a given ID STAGE. It will return NULL is a date does
	 * not exist Dam Name: CSYS15D
	 * 
	 * @param SrvrflReq
	 * @return Contact @
	 */
	Contact getContactByStageId(long SrvrflReq);

	/**
	 * 
	 * Method Description: This Method will Deleting cps checklist items if
	 * exits Dam Name: CLSS81D
	 * 
	 * @param cpsChecklistItem
	 * @return deleteCpsChecklistItem @
	 */
	void deleteCpsChecklistItem(CpsChecklistItem cpsChecklistItem);

	/**
	 * 
	 * Method Description: This Method will Save cps checklist item in to the
	 * table. Dam Name: CAUDE3D
	 * 
	 * @param cpsChecklistItem
	 * @return saveCpsChecklistItem @
	 */
	void saveCpsChecklistItem(CpsChecklistItem cpsChecklistItem);

	/**
	 * 
	 * Method Description: This Method will get cps checklist item from the
	 * table. Dam Name: CSESA2D
	 * 
	 * @param uidCpsCheckList
	 * @return CpsChecklist @
	 */
	CpsChecklist getCpsChecklist(Long uidCpsCheckList);

	/**
	 * 
	 * Method Description: This Method will Save or update checklist item in the
	 * table. Dam Name: CAUDE3D
	 * 
	 * @param cpsChecklist
	 * @return void @
	 */
	void saveOrUpdateCpsChecklist(CpsChecklist cpsChecklist);

	/**
	 * 
	 * Method Description: This Method will Save or update checklist item in the
	 * TODO table. Dam Name: CINV43D
	 * 
	 * @param toDo
	 * @return void @
	 */
	void saveOrUpdateToDO(Todo toDo);

	/**
	 * 
	 * Method Description: This Method will retrieve the EventId from Event
	 * table. Dam Name: CCMN45D
	 * 
	 * @param uidEvent
	 * @return Event @
	 */
	Event getEventById(Long uidEvent);

	/**
	 * 
	 * Method Description: This Method will retrieve the caseId from caps_case
	 * table. Dam Name: CAUDE4D
	 * 
	 * @param uidCapsCase
	 * @return CapsCase @
	 */
	CapsCase getCaseById(Long uidCapsCase);

	/**
	 * 
	 * Method Description: This Method will retrieve the stageId from Stage
	 * table. Dam Name: CAUDE4D
	 * 
	 * @param uidStage
	 * @return Stage @
	 */

	Stage getStageById(Long uidStage);

	/**
	 * Retrieves the parental child safety placement details from the
	 * CHILD_SAFETY_PLCMT, PERSON, STAGE tables. Service Name: PCSPEjb
	 * 
	 * @param pcspReq
	 * @return List<PCSPDto> @
	 */
	List<PcspDto> getPcspList(PcspReq pcspReq);

	void updateOrSaveToDO(TodoDto toDo);

}
