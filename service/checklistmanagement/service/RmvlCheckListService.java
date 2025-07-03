package us.tx.state.dfps.service.checklistmanagement.service;

import java.util.List;

import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstLinkDto;
import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstLookupDto;
import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstRspnDto;
import us.tx.state.dfps.service.common.response.CnsrvtrshpRemovalRes;
import us.tx.state.dfps.service.common.response.RmvlCheckListRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * RmvlCheckListService will have all operation which are mapped to
 * RmvlCheckList module. Feb 9, 2018- 2:01:02 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface RmvlCheckListService {

	/**
	 * 
	 * Method Name: getRmvlChcklsts Method Description: This service will return
	 * all the checklist
	 * 
	 * @return @
	 */
	public RmvlCheckListRes getRmvlChcklsts();

	/**
	 * 
	 * Method Name: getRmvlChcklstDtl Method Description: Return the details for
	 * a checklist given the checklist Id
	 * 
	 * @param checklistId
	 * @return @
	 */
	public RmvlCheckListRes getRmvlChcklstDtl(Long checklistId,Long idPerson, Long idRmvlChcklstLink, Long idStage, Long idRemovalEvent);

	/**
	 * 
	 * Method Name: copyRmvlChcklst Method Description: This service will create
	 * a new checklist from an existing checklist
	 * 
	 * @param checklistId
	 * @return @
	 */

	public RmvlCheckListRes copyRmvlChcklst(Long checklistId);

	/**
	 * 
	 * Method Name: deleteRmvlChcklstTaskDtl Method Description: The service
	 * will perform on soft delete on a task
	 * 
	 * @param taskId
	 * @return @
	 */
	public RmvlCheckListRes deleteRmvlChcklstTaskDtl(List<Long> taskId);

	/**
	 * 
	 * Method Name: saveRmvlCheckList Method Description: This service will save
	 * and/or save&publish a checklist
	 * 
	 * @param checklist
	 * @param IndSave
	 * @return @
	 */
	public RmvlCheckListRes saveRmvlCheckList(RmvlChcklstLookupDto checklist, String IndSave);

	/**
	 * 
	 * Method Name: updateRmvlCheckList Method Description: This method will
	 * update a given checklist
	 * 
	 * @param checklist
	 * @return @
	 */
	public RmvlCheckListRes updateRmvlCheckList(RmvlChcklstLookupDto checklist, Long idPerson, Long idRmvlChcklstLink);

	/**
	 * 
	 * Method Name: getRmvlChcklstLink Method Description: retrieves all
	 * checklist links for a given idPerson
	 * 
	 * @param idPerson
	 * @return
	 */
	public RmvlCheckListRes getRmvlChcklstLink(Long idPerson, Long idStage, Long idRemovalEvent);

	/**
	 * 
	 * Method Name: indRecordExist Method Description: check if a checklist link
	 * exist given the event id.
	 * 
	 * @param idRmvlEvent
	 * @return @
	 */
	public boolean indRecordExist(Long idRmvlEvent);

	/**
	 * 
	 * Method Name: getPersonList Method Description: Get the person list given
	 * the idEvent
	 * 
	 * @param idRmvlEvent
	 * @return @
	 */
	public CnsrvtrshpRemovalRes getPersonList(Long idRmvlEvent);

	/**
	 * 
	 * Method Name: saveRmvlChcklstRspn Method Description: This service will
	 * save a new response in the checklstRspn
	 * 
	 * @param checklstRspn
	 * @return @
	 */
	public RmvlCheckListRes saveRmvlChcklstRspn(List<RmvlChcklstRspnDto> checklstRspn);

	/**
	 * 
	 * Method Name: updateRmvlChcklstRspn Method Description: This service will
	 * update the given response with the new data
	 * 
	 * @param checklstRspn
	 * @return @
	 */
	public RmvlCheckListRes updateRmvlChcklstRspn(List<RmvlChcklstRspnDto> checklstRspn);

	/**
	 * 
	 * Method Name: getRmvlChcklstRspn Method Description: Retrieve all response
	 * associated with a given person ID
	 * 
	 * @param idPerson
	 * @return @
	 */
	public RmvlCheckListRes getRmvlChcklstRspn(Long idPerson);

	/**
	 * 
	 * Method Name: saveRmvlChcklstLink Method Description: This service will
	 * save the given checklist link
	 * 
	 * @param rmvlChcklstLinkDto
	 * @return @
	 */
	public RmvlCheckListRes saveRmvlChcklstLink(List<RmvlChcklstLinkDto> rmvlChcklstLinkDto);

	/**
	 * 
	 * Method Name: updateRmvlChcklstLink Method Description:
	 * 
	 * @param rmvlChcklstLinkDto
	 * @return @
	 */
	public RmvlCheckListRes updateRmvlChcklstLink(List<RmvlChcklstLinkDto> rmvlChcklstLinkDto);

}
