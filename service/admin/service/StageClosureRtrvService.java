package us.tx.state.dfps.service.admin.service;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.LegalActionEventOutDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusDetailDto;
import us.tx.state.dfps.service.admin.dto.PcaSubsidyOutDto;
import us.tx.state.dfps.service.admin.dto.StageClosureRtrvDto;
import us.tx.state.dfps.service.common.request.StageClosureRtrvReq;
import us.tx.state.dfps.service.common.response.StageClosureRtrvRes;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dto.StagePersDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is the interface for StageClosureRtrvServiceImpl. Aug 19, 2017- 8:50:18 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface StageClosureRtrvService {
	/**
	 * 
	 * Method Name: getStageClosureDetails Method Description: (legacy reference
	 * callStageClosureRtrvService)
	 * 
	 * @param idStage
	 * @param idEvent
	 * @return StageClosureRtrvDto
	 */
	StageClosureRtrvDto getStageClosureDetails(Long idStage, Long idEvent);

	/**
	 * 
	 * Method Name: getStageClosurePageDetails Method Description: This is
	 * consolidated service to get Page details
	 * 
	 * @param idStage
	 * @param cdTask
	 * @return StageClosureRtrvRes
	 */
	StageClosureRtrvRes getStageClosurePageDetails(Long idStage, String cdTask);

	/**
	 * Method Name: callStageClosureAudService Method Description: This Service
	 * runs all the validations , if no error found then saves the stageClousure
	 * data
	 * 
	 * @param stageClosureRtrvReq
	 * @return
	 */
	StageClosureRtrvRes saveStageClosure(StageClosureRtrvReq stageClosureRtrvReq);

	/**
	 * Method Name: isActiveReferral Method Description: Returns true/false if
	 * there is an active child referral for stage id
	 * 
	 * @param idStage
	 * @return
	 */
	boolean isActiveReferral(Long idStage);

	/**
	 * Method Name: getPrtActiveActionPlan Method Description: This method
	 * returns a boolean value based on whether or not a sub stage is currently
	 * in open status.
	 * 
	 * @param idPerson
	 * @return
	 */
	public boolean getPrtActiveActionPlan(Long idPerson);

	/**
	 * Method Name: getPrtActionPlanInProcStatus Method Description: This method
	 * returns a boolean value based on whether or not a sub stage is currently
	 * in Proc status.
	 * 
	 * @param idPerson
	 * @return
	 */
	boolean getPrtActionPlanInProcStatus(Long idPerson);

	/**
	 * Method Name: getPlacementsByStageId Method Description:CLSS84D this
	 * service will fetchPlacments
	 * 
	 * @param idPerson
	 * @return
	 */

	List<PlacementDto> getPlacementsByStageId(Long idSatge);

	/**
	 * 
	 * Method Name: getEventStatus(CallCSVC34D) Method Description: this Service
	 * gets EventStatus using StageId Of dayCare event which are pending
	 * 
	 * @param idStage
	 * @return
	 */
	String getEventStatus(Long idStage);

	/**
	 * 
	 * Method Name: getLegalActionType Method Description: this Service Will
	 * fetch LegalActionEvent( CSESD6D )
	 * 
	 * @param idPerson
	 * @param idCase
	 * @param idStage
	 * @return LegalActionEventOutDto
	 */
	LegalActionEventOutDto getLegalActionEvent(Long idPerson, Long idCase, Long idSatge);

	/**
	 * 
	 * Method Name: getPlacementDtoByPersonId Method Description: CSES44D--This
	 * Service performs a complete row retrieval from the most recent actual
	 * adoption placement from the PLACEMENT table, given the ID_PLCMT_CHILD.
	 * This Service is a sister DAM of CSES34D.
	 * 
	 * @param idPlcmtChild
	 * @return
	 */
	List<PlacementDto> getPlacementDtoByPersonId(Long idPerson);

	/**
	 * 
	 * Method Name: getPlacementDtoByPersonIdandPlacement Method Description:
	 * CSES44D This service retrieves the most recent living arrangement,
	 * placement start date from the Placement table using ID_PLCMT_CHILD.
	 * 
	 * @param idPlcmtChild
	 * @return
	 */
	List<PlacementDto> getPlacementDtoByPersonIdandPlacement(Long idPerson);

	/**
	 * 
	 * Method Name: getLatestLegalStatus (CSES78D) Method Description:This
	 * Service retrieves a full row from LEGAL_STATUS.
	 * 
	 * @param idPerson
	 * @param idCase
	 * @return
	 */
	LegalStatusDetailDto getLatestLegalStatus(Long idPerson, Long idCase);

	/**
	 * 
	 * Method Name: getPersonLegalStatus(CCMNH9D) Method Description:Retrieves
	 * all the person with Legal Statuses in a given Case.
	 * 
	 * @param idCase
	 * @param cdEventType
	 * @return
	 */
	List<StagePersDto> getPersonLegalStatus(Long idCase);

	/**
	 * 
	 * Method Name: getPCASubsidyRecord Method Description:CCMNI8D - Retrieves
	 * the PCA subsidy record for an event id.
	 * 
	 * @param idEvent
	 * @return
	 */
	List<PcaSubsidyOutDto> getPCASubsidyRecord(Long idEvent);

	/**
	 * 
	 * Method Name: saveSubmit Method Description:
	 * 
	 * @param stageClosureRtrvReq
	 * @return stageClosureRtrvRes
	 */
	StageClosureRtrvRes saveSubmit(StageClosureRtrvReq stageClosureRtrvReq);

}
