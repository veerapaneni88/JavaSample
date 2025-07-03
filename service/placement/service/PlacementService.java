package us.tx.state.dfps.service.placement.service;

import us.tx.state.dfps.common.domain.Placement;
import us.tx.state.dfps.common.dto.PlacementValueDto;
import us.tx.state.dfps.service.common.request.PlacementReq;
import us.tx.state.dfps.service.common.request.SavePlacementDetailReq;
import us.tx.state.dfps.service.common.response.CommonCountRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.HmEligibilityReq;
import us.tx.state.dfps.service.common.response.HmEligibilityRes;
import us.tx.state.dfps.service.common.response.SavePlacementDtlRes;
import us.tx.state.dfps.service.placement.dto.AlertPlacementLsDto;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dto.EventDto;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.List;

/**
 * 
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:PlacementService
 *Oct 11, 2017- 6:05:53 PM
 *Â© 2017 Texas Department of Family and Protective Services
 */
public interface PlacementService {

    /**
	 *Method Name:	checkLegalAction
	 *Method Description:This method Checks for the proper Legal Action
	 *@param placementValueDto
	 *@return Boolean
	 *@
	 */
	public Boolean checkLegalAction(PlacementValueDto placementValueDto);

	/**
	 *Method Name:	fetchPlacement
	 *Method Description:This method retrieves Placement Details from the database using idPlcmtEvent.
	 *@param idPlcmtEvent
	 *@return PlacementDto
	 *@;
	 */
	public PlacementDto fetchPlacement(Long idPlcmtEvent) ;
	
	/**
	 *Method Name:	fetchLatestPlacement
	 *Method Description:This method retrieves Latest Placement for the given
	 *@param stageId
	 *@return PlacementDto
	 *@
	 */
	public PlacementDto fetchLatestPlacement(Long stageId) ;

	/**
	 *Method Name:	findActivePlacements
	 *Method Description:Fetches the most recent open Active Placement for the idPerson
	 *@param idPerson
	 *@return List<PlacementValueDto>
	 *@
	 */
	public List<PlacementValueDto> findActivePlacements(Long idPerson) ;

	/**
	 *Method Name:	checkPlcmtDateRange
	 *Method Description:This method checks if there is any Placement for the Child in the Range of Placement Start Date.
	 *@param idPerson
	 *@param dtPlcmtStart
	 *@return List<PlacementValueDto>
	 *@
	 *
	 */
	public List<PlacementValueDto> checkPlcmtDateRange(Long idPerson, Date dtPlcmtStart) ;

	/**
	 *Method Name:	findAllPlacementsForStage
	 *Method Description:This method returns all the placements for the given Stage
	 *@param stageId
	 *@return List<PlacementValueDto>
	 *@
	 */
	public List<PlacementValueDto> findAllPlacementsForStage(Long stageId) ;

	public List<PlacementValueDto> findAllQTRPPlacements(Long childID) ;


	/**
	 *Method Name:	getContractedSvcLivArr
	 *Method Description:This method returns an active SIL contract service to  filter the living arrangement for SSCC SIL placement
	 *@param idResource
	 *@return List<PlacementValueDto>
	 *@
	 */
	public List<PlacementValueDto> getContractedSvcLivArr(Long idResource) ;

	/**
	 *Method Name:	getResourceSvcLivArr
	 *Method Description:This method returns List of SIL resource services to  filter the living arrangement for SIL placement
	 *@param idResource
	 *@return List<PlacementValueDto>
	 *@
	 */
	public List<PlacementValueDto> getResourceSvcLivArr(Long idResource) ;

	/**
	 *Method Name:	getActiveSilContract
	 *Method Description:This method returns an active SIL
	 *@param idResource
	 *@return List<PlacementValueDto>
	 *@
	 */
	public List<PlacementValueDto> getActiveSilContract(Long idResource) ;

	/**
	 *Method Name:	getAllContractPeriods
	 *Method Description:This method returns all contract periods
	 *@param idResource
	 *@return List<PlacementValueDto>
	 *@
	 */
	public List<PlacementValueDto> getAllContractPeriods(Long idResource) ;

	/**
	 *Method Name:	getCorrespondingPlacement
	 *Method Description:This method returns List corresponding parent placement for the child  within a stage
	 *@param stageId
	 *@return List<PlacementValueDto>
	 *@
	 */
	public List<PlacementValueDto> getCorrespondingPlacement(Long stageId);

	/**
	 *Method Name:	getContractCounty
	 *Method Description:This method gets address county in SIL Contract services
	 *@param placementValueDto
	 *@return List<PlacementValueDto>
	 *@
	 */
	public List<PlacementValueDto> getContractCounty(PlacementValueDto placementValueDto) ;

	/**
	 *Method Name:	getAddCntInSilRsrc
	 *Method Description:This method gets address county in SIL resource services
	 *@param idResource
	 *@param szCountyCode
	 *@param livArr
	 *@return List<PlacementValueDto>
	 *@
	 */
	public List<PlacementValueDto> getAddCntInSilRsrc(Long idResource, String szCountyCode, String livArr);

	/**
	 *Method Name:	isSSCCPlacement
	 *Method Description:This method checks to see if the placement is a SSCC placement
	 *@param idPlcmtEvent
	 *@return
	 *@
	 */
	public Boolean isSSCCPlacement(Long idPlcmtEvent);

	/**
	 *Method Name:	createExceptCareAlert
	 *Method Description:This method to create an alert to do to staff who created Exception Care
	 *@param szNmStage 
	 *@param szCdStage 
	 *@param userId 
	 *@param stageId 
	 *@param szCdTask 
	 *@param idEvent 
	 *@return Long
	 *@
	 */
	public Long createExceptCareAlert(Long idEvent, String szCdTask, Long stageId, Long userId, String szCdStage, String szNmStage ); 
	
	/**
	 *Method Name:	isActSsccCntrctExist
	 *Method Description:This method gets active SSCC contract services for the  catchment area
	 *@param placeReq
	 *@return PlacementValueDto
	 *@
	 */
	public PlacementValueDto isActSsccCntrctExist(PlacementReq placeReq);

	/**
	 *Method Name:	getActiveChildPlcmtReferral
	 *Method Description:This method gets valid child placement referral information  for the stage id (an active referral here is not base on the status, it's 
     * base on the referral recorded and discharged dates.
	 *@param stageId
	 *@return PlacementValueDto
	 *@
	 */
	public PlacementValueDto getActiveChildPlcmtReferral(Long stageId) ;

	/**
	 *Method Name:	updateIndPlcmtSSCC
	 *Method Description:This method updates indicator placement sscc
	 *@param placementValueDto
	 *@return PlacementValueDto
	 *@
	 */
	public PlacementValueDto updateIndPlcmtSSCC(PlacementValueDto placementValueDto);

	/**
	 *Method Name:	updateIdPlcmtSSCC
	 *Method Description:This method updates id placement sscc
	 *@param placementValueDto
	 *@return PlacementValueDto
	 *@
	 */
	public PlacementValueDto updateIdPlcmtSSCC(PlacementValueDto placementValueDto);

	/**
	 *Method Name:	updateChildPlanDue
	 *Method Description:This method updates id placement sscc
	 *@param placementValueDto
	 *@return PlacementValueDto
	 *@
	 */
	public PlacementValueDto updateChildPlanDue(PlacementValueDto placementValueDto);

	/**
	 *Method Name:	updateIndEfcActive
	 *Method Description:This method updates indicator EFC Active
	 *@param placementValueDto
	 *@return PlacementValueDto
	 *@
	 */
	public PlacementValueDto updateIndEfcActive(PlacementValueDto placementValueDto);

	/**
	 *Method Name:	updateIndEfc
	 *Method Description:This method updates indicator EFC 
	 *@param placementValueDto
	 *@return PlacementValueDto
	 *@
	 */
	public PlacementValueDto updateIndEfc(PlacementValueDto placementValueDto);

	/**
	 *Method Name:	updateIndLinkedPlcmtData
	 *Method Description: This method updates indicator linked placement data
	 *@param placementValueDto
	 *@return PlacementValueDto
	 *@
	 */
	public PlacementValueDto updateIndLinkedPlcmtData(PlacementValueDto placementValueDto);

	/**
	 *Method Name:	getIndPlcmtSSCC
	 *Method Description:This method gets indicate placement sscc
	 *@param idReferral
	 *@return PlacementValueDto
	 *@
	 */
	public PlacementValueDto getIndPlcmtSSCC(Long idReferral);

	/**
	 *Method Name:	getActiveSSCCReferral
	 *Method Description:This method gets active sscc referral for stage id
	 *@param stageId
	 *@return PlacementValueDto
	 *@
	 */
	public  PlacementValueDto  getActiveSSCCReferral(Long stageId);

	/**
	 *Method Name:	childLastestPlcmtSSCC
	 *Method Description:This method gets child latest placement sscc
	 *@param stageId
	 *@param dtPlcmtStart
	 *@return PlacementValueDto
	 *@
	 */
	public PlacementValueDto childLastestPlcmtSSCC(Long stageId, Date dtPlcmtStart);

	/**
	 * Method Name: getChildPlanInitiateInfo Method Description:This method gets the
	 * latest child plan initiate info within a stage at the time of approval of an
	 * sscc placement
	 *
	 * @param idReferral
	 * @return PlacementValueDto
	 */
	public PlacementValueDto getChildPlanInitiateInfo(Long idReferral);

	/**
	 *Method Name:	getExceptionalCareDaysUsed
	 *Method Description:This method gets the number of exceptional
     * care used in a contract period
	 *@param idReferral
	 *@return Long
	 *@
	 */
	public Long getExceptionalCareDaysUsed(Long idReferral);

	
	SavePlacementDtlRes saveOrUpdatePlacementDtl(SavePlacementDetailReq savePlacementDtlReq) throws MessagingException;
	

	/**
	 * Method Name: getPriorPlacementsById Method Description: This method
	 * returns prior placement list based on idPlacementEvent
	 * 
	 * @param idPriorPlacementEvent
	 * @return CommonHelperRes
	 */

	public CommonHelperRes getPriorPlacementsById(Long idPriorPlacementEvent);
	
	

	/**
	 * Method Name: getIndChildSibling1 Method Description: This method
	 * returns prior placement Sibling based on idPerson
	 * 
	 * @param idPerson
	 * @return CommonHelperRes
	 */

	public CommonHelperRes getIndChildSibling1(Long idPerson);

	/**
	 * MethodName: createIcpcRejectToDo MethodDescription: 
	 * @param idEvent
	 * @param cdTask
	 * @param idStage
	 * @param userId
	 * @param cdStage
	 */
	public void createIcpcRejectToDo(Long idEvent, String cdTask, Long idStage, Long userId, String cdStage);
	
	public void createIcpcAlert(String toDoDesc, String toDoLongDesc, String cdTodoInfoType, String cdTask,
			Date dtToDoDue, Long idPrsnAssgn, Long idUser, Long idStage, Long idEvent);
	
	public CommonHelperRes getEligibilityEvent(Long idPerson);
	
	/**
	 *Method Name:	alertPlacementReferral
	 *Method Description:
	 *@param savePlacementDtlReq
	 *@param checkFlag
	 */
	public void alertPlacementReferral(AlertPlacementLsDto alertPlacementLsDto);

	/**
	 *Method Name:	getLatestPlcmntEvent
	 *Method Description:
	 *@param eventDto
	 */
	public EventDto getLatestPlcmntEvent(EventDto eventDto);
	
	/**
	 * @param placement
	 * @return PlacementDto
	 */
	public PlacementDto processPlacement(Placement placement);
	
	
	/**
	 * Method Name: getActiveTepContract 
	 * Method Description:This method returns
	 * an active TEP
	 * 
	 * @param idResource
	 * @return PlacementValueDto 
	 */
	PlacementValueDto getActiveTepContract(PlacementReq placementReq);
	
	/**
	 * Method Name: getActiveTepContract 
	 * Method Description:This method returns
	 * an active TEP
	 * 
	 * @param idResource
	 * @return PlacementValueDto 
	 */
	CommonCountRes getCountOfActiveTfcPlmnts(PlacementReq placementReq);
	
	/**
	 * Method Name: getActiveTepContract 
	 * Method Description:This method returns
	 * an active TEP
	 * 
	 * @param idResource
	 * @return PlacementValueDto 
	 */
	CommonCountRes getCountOfAllPlacements(PlacementReq placementReq);

	public CommonHelperRes chckPlcmntEndedOrNot(Long idEvent);

	public List<PlacementValueDto> getPlacementHistory(Long idPerson);


	/**
	 * @param stageId
	 * @return
	 */
	public List<PlacementValueDto>  getChildPlcmtReferrals(Long stageId);

	public boolean chkValidFPSContractRsrc(Long idResource, Long idRsrcSscc);
	List<PlacementValueDto> getChildPlacement(Long idPerson, Long idStage);

	Boolean getAddOnSvcPkg(Long idPerson, Long idStage);

	/**
	 *
	 * @param placementReq
	 * @return retrieves the latest closed placement for the person.
	 */
	HmEligibilityRes checkPlacementHmEligibility(HmEligibilityReq placementReq);

	List<PlacementValueDto> getParentPlacement(Long idPerson, Long idStage);

	CommonCountRes getCountCPBPlcmntsForYouthParent(Long idPerson, Long idStage, Date placementStartDate);

	Boolean checkAlocBlocForNonT3cPlcmt(Long idCase, Date dtPlcmtStart);
}
