package us.tx.state.dfps.service.subcare.dao;

import us.tx.state.dfps.common.domain.HMRequest;
import us.tx.state.dfps.common.domain.Placement;
import us.tx.state.dfps.common.dto.*;
import us.tx.state.dfps.service.admin.dto.ResourceServiceInDto;
import us.tx.state.dfps.service.common.request.PlacementReq;
import us.tx.state.dfps.service.common.response.CommonCountRes;
import us.tx.state.dfps.service.fce.EligibilityDto;
import us.tx.state.dfps.service.kin.dto.KinChildDto;
import us.tx.state.dfps.service.placement.dto.AlertPlacementLsDto;
import us.tx.state.dfps.service.placement.dto.PlacementAUDDto;
import us.tx.state.dfps.service.placement.dto.PlacementDtlGpDto;
import us.tx.state.dfps.service.placement.dto.TemporaryAbsenceDto;
import us.tx.state.dfps.service.subcare.dto.ChildBillOfRightsDto;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dto.EventDto;

import java.util.Date;
import java.util.List;

/**
 * Class Name:PlacementDao Class Description:PlacementDao performs some of the
 * database activities related to Placement Page/table. Oct 10, 2017- 1:42:10 PM
 * © 2017 Texas Department of Family and Protective Services
 */
public interface PlacementDao {

	/**
	 * 
	 * Method Name: selectPlacement Method Description:This method retrieves
	 * Placement Details from the database using idPlcmtEvent.
	 * 
	 * @param idPlcmtEvent
	 * @return PlacementDto
	 * 
	 */
	public Placement selectPlacement(Long idPlcmtEvent);

	/**
	 * 
	 * Method Name: selectLatestPlacement Method Description:This method
	 * retrieves Latest Placement for the given
	 * 
	 * @param idStage
	 * @return PlacementDto
	 *
	 */
	public PlacementDto selectLatestPlacement(Long idStage);

	/**
	 * Method Name: checkLegalAction Method Description:This method Checks for
	 * the proper Legal Action
	 * 
	 * @param placementValueDto
	 * @return Boolean
	 * 
	 */
	public Boolean checkLegalAction(PlacementValueDto placementValueDto);

	/**
	 * Method Name: findActivePlacements Method Description:Fetches the most
	 * recent open Active Placement for the idPerson
	 * 
	 * @param idPerson
	 * @return List<PlacementValueDto>
	 * 
	 */
	public List<PlacementValueDto> findActivePlacements(Long idPerson);

	/**
	 * Method Name: checkPlcmtDateRange Method Description:This method checks if
	 * there is any Placement for the Child in the Range of Placement Start
	 * Date.
	 * 
	 * @param idPerson
	 * @param dtPlcmtStart
	 * @return List<PlacementValueDto>
	 */
	public List<PlacementValueDto> checkPlcmtDateRange(Long idPerson, Date dtPlcmtStart);

	/**
	 * Method Name: findAllPlacementsForStage Method Description:This method
	 * returns all the placements for the given Stage
	 * 
	 * @param stageId
	 * @return List<PlacementValueDto>
	 */
	public List<PlacementValueDto> findAllPlacementsForStage(Long stageId);

	List<PlacementValueDto> findAllQTRPPlacements(Long childID);

	/**
	 * Method Name: getActiveSilContract Method Description:This method returns
	 * an active SIL
	 * 
	 * @param idResource
	 * @return List<PlacementValueDto>
	 */
	public List<PlacementValueDto> getActiveSilContract(Long idResource);

	/**
	 * Method Name: getAllContractPeriods Method Description:This method returns
	 * all contract periods
	 *
	 * @param idResource
	 * @return List<PlacementValueDto>
	 */
	public List<PlacementValueDto> getAllContractPeriods(Long idResource);

	/**
	 * Method Name: getSILRsrsSvc Method Description:This method returns List of
	 * SIL resource services.
	 * 
	 * @param idResource
	 * @return List<PlacementValueDto>
	 */
	public List<PlacementValueDto> getSILRsrsSvc(Long idResource);

	/**
	 * Method Name: getCorrespondingPlacement Method Description:This method
	 * returns List corresponding parent placement for the child within a stage
	 * 
	 * @param stageId
	 * @return List<PlacementValueDto>
	 */
	public List<PlacementValueDto> getCorrespondingPlacement(Long stageId);

	/**
	 * Method Name: getContractCounty Method Description:This method gets
	 * address county in SIL Contract services
	 * 
	 * @param placementValueDto
	 * @return List<PlacementValueDto>
	 */
	public List<PlacementValueDto> getContractCounty(PlacementValueDto placementValueDto);

	/**
	 * Method Name: getAddCntInSilRsrc Method Description:This method gets
	 * address county in SIL resource services
	 * 
	 * @param idResource
	 * @param szCountyCode
	 * @param livArr
	 * @return List<PlacementValueDto>
	 */
	public List<PlacementValueDto> getAddCntInSilRsrc(Long idResource, String szCountyCode, String livArr);

	/**
	 * Method Name: isSSCCPlacement Method Description:This method checks to see
	 * if the placement is a SSCC placement
	 * 
	 * @param idPlcmtEvent
	 * @return Boolean
	 */
	public Boolean isSSCCPlacement(Long idPlcmtEvent);

	/**
	 * Method Name: getExeptCareCreaters Method Description:This method to
	 * create an alert to do to staff who created Exception Care
	 * 
	 * @param idEvent
	 * @return Long
	 */
	public Long getExeptCareCreaters(Long idEvent);

	/**
	 * Method Name: isActSsccCntrctExist Method Description:This method gets
	 * active SSCC contract services for the catchment area
	 * 
	 * @param placeReq
	 * @return PlacementValueDto
	 */
	public PlacementValueDto isActSsccCntrctExist(PlacementReq placeReq);

	/**
	 * Method Name: getActiveChildPlcmtReferral Method Description:This method
	 * gets valid child placement referral information for the stage id (an
	 * active referral here is not base on the status, it's base on the referral
	 * recorded and discharged dates.
	 * 
	 * @param stageId
	 * @return PlacementValueDto
	 */
	public PlacementValueDto getActiveChildPlcmtReferral(Long stageId);

	/**
	 * Method Name: updateIndPlcmtSSCC Method Description:This method updates
	 * indicator placement sscc
	 * 
	 * @param placementValueDto
	 * @return PlacementValueDto
	 */
	public PlacementValueDto updateIndPlcmtSSCC(PlacementValueDto placementValueDto);

	/**
	 * Method Name: updateIdPlcmtSSCC Method Description:This method updates id
	 * placement sscc
	 * 
	 * @param placementValueDto
	 * @return PlacementValueDto
	 */
	public PlacementValueDto updateIdPlcmtSSCC(PlacementValueDto placementValueDto);

	/**
	 * Method Name: updateChildPlanDue Method Description:This method updates id
	 * placement sscc
	 * 
	 * @param placementValueDto
	 * @return PlacementValueDto
	 */
	public PlacementValueDto updateChildPlanDue(PlacementValueDto placementValueDto);

	/**
	 * Method Name: updateIndEfcActive Method Description:This method updates
	 * indicator EFC Active
	 * 
	 * @param placementValueDto
	 * @return PlacementValueDto
	 */
	public PlacementValueDto updateIndEfcActive(PlacementValueDto placementValueDto);

	/**
	 * Method Name: updateIndEfc Method Description:This method updates
	 * indicator EFC
	 * 
	 * @param placementValueDto
	 * @return PlacementValueDto
	 */
	public PlacementValueDto updateIndEfc(PlacementValueDto placementValueDto);

	/**
	 * Method Name: updateIndLinkedPlcmtData Method Description: This method
	 * updates indicator linked placement data
	 * 
	 * @param placementValueDto
	 * @return PlacementValueDto
	 */
	public PlacementValueDto updateIndLinkedPlcmtData(PlacementValueDto placementValueDto);

	/**
	 * Method Name: getIndPlcmtSSCC Method Description:This method gets indicate
	 * placement sscc
	 * 
	 * @param idReferral
	 * @return PlacementValueDto
	 */
	public PlacementValueDto getIndPlcmtSSCC(Long idReferral);

	/**
	 * Method Name: getActiveSSCCReferral Method Description:This method gets
	 * active sscc referral for stage id
	 * 
	 * @param stageId
	 * @return PlacementValueDto
	 */
	public List<PlacementValueDto> getActiveSSCCReferral(Long stageId);

	/**
	 * Method Name: childLastestPlcmtSSCC Method Description:This method gets
	 * child latest placement sscc
	 * 
	 * @param stageId
	 * @param dtPlcmtStart
	 * @return PlacementValueDto
	 */
	public PlacementValueDto childLastestPlcmtSSCC(Long stageId, Date dtPlcmtStart);

	/**
	 * Method Name: getChildPlanInitiateInfo Method Description:This method gets
	 * the latest child plan initiate info within a stage at the time of
	 * approval of an sscc placement
	 * 
	 * @param idReferral
	 * @return PlacementValueDto
	 */
	public PlacementValueDto getChildPlanInitiateInfo(Long idReferral);

	/**
	 * Method Name: getExcpCareDaysUsed Method Description:This method gets the
	 * number of exceptional care used in a contract period
	 * 
	 * @param idReferral
	 * @return Long
	 */
	public Long getExcpCareDaysUsed(Long idReferral);

	/**
	 * Method Name: findActivePlacementsForEligibilty Method Description:Fetches
	 * the most recent open Active Placement for the idPerson
	 * 
	 * @param idPerson
	 * @return List<PlacementDto>
	 */
	public PlacementDto findActivePlacementsForEligibilty(Long idPerson);

	/**
	 * Method Name: findActivePlacementsForFosterCare Method Description: This
	 * method is used to find ActivePlacements For FosterCare
	 * 
	 * @param idPerson
	 * @return List<PlacementDto>
	 */
	public List<PlacementDto> findActivePlacementsForFosterCare(Long idPerson);

	/**
	 * Method Name: findActivePlacement Method Description: This method is used
	 * to findActivePlacement
	 * 
	 * @param idPerson
	 * @return PlacementValueDto
	 */
	public PlacementValueDto findActivePlacement(Long idPerson);

	/**
	 * Method Name: findRecentPlacements Method Description: This method is used
	 * to findRecentPlacements
	 * 
	 * @param idStage
	 * @return List<PlacementDto>
	 */
	public List<PlacementDto> findRecentPlacements(Long idStage);

	/**
	 * 
	 * Method Name: findOtherChildInPlacementCount (DAm Name : CLSS01D ) Method
	 * Description:This Dam returns the count of other children with palcements
	 * for a given idRsrcFacil and idPlcmtChild
	 * 
	 * @param resourceServiceInDto
	 * @return
	 */
	public CommonCountRes findOtherChildInPlacementCount(ResourceServiceInDto resourceServiceInDto);

	/**
	 * Method Name: getPlacementsByChildId Method Description: This method is
	 * used to getPlacements By ChildId
	 * 
	 * @param resourceServiceInDto
	 * @return List<PlacementDto>
	 */
	public List<PlacementDto> getPlacementsByChildId(ResourceServiceInDto resourceServiceInDto);

	public List<PlacementDto> getPlacementDetailsByChildId(Long childId);

	/**
	 * Method Name: getDistinctService Method Description: This method is used
	 * to getDistinctService
	 * 
	 * @param placementDtlGpDto
	 * @return String
	 */
	public String getDistinctService(PlacementDtlGpDto placementDtlGpDto);

	/**
	 * Method Name: getContractDtl Method Description: Retrieves CONTRACT IDs
	 * for the passed Resource ID
	 * 
	 * DAM Name: CLSS67D Service Name: CSUB26S
	 * 
	 * @param idResourse
	 *            - The resource for which the Contacts should be fetched
	 * @return List<ContractDto> - The List of Contracts for the Resource
	 */
	public List<ContractDto> getContractDtl(Long idResourse);

	/**
	 * Method Name: getContractPeriodByIdContract MEthod Description: Retrieves
	 * CONTRACT based on id_contract from CONTRACT_PERIOD table
	 * 
	 * DAM Name: CSES80D Service Name: CCMN35S
	 * 
	 * @param idContract
	 * @return List<ContractPeriod>
	 */
	List<ContractPeriodDto> getContractPeriodByIdContract(Long idContract);

	/**
	 * Method Name: getContractSignedOrNot Method Description: This method is
	 * used to check whether Contract is Signed or Not
	 * 
	 * @param placementDtlGpDto
	 * @return Long
	 */
	public Long getContractSignedOrNot(PlacementDtlGpDto placementDtlGpDto);

	/**
	 * Method Name: getEligibilityDtl Method Description: This method is used to
	 * get Eligibility Detail
	 * 
	 * @param idEvent
	 * @return EligibilityDto
	 */
	public EligibilityDto getEligibilityDtl(Long idEvent);

	/**
	 * Method Name: getWorkloadDtl Method Description: This method is used to
	 * getWorkloadDtl
	 * 
	 * @param idStage
	 * @return WorkloadDto
	 */
	public WorkloadDto getWorkloadDtl(Long idStage);

	/**
	 * Method Name: getDtActiveADO Method Description: This method will retrieve
	 * a date of active ADO from the ADoptionSUB (DAM CSUB89D from legacy Tuxedo
	 * Code)
	 * 
	 * @param idEvent
	 * @return PlacementDtlGpDto
	 */
	PlacementDtlGpDto getDtActiveADO(Long idEvent);

	/**
	 * Method Name: updateOpenDDPlacement Method Description: This method
	 * Updates PLACEMENT table to close an open DA/DD placement by populating
	 * the placement closure reason and placement end date.
	 * 
	 * @param placementDtoList
	 * @param idPerson
	 * @return String
	 */
	String updateOpenDDPlacement(List<PlacementDto> placementDtoList, Long idPerson);

	// CMSC09d
	/**
	 * Method Name: getCountSubcareStage Method Description: This method will
	 * return the count of the rows that meet the requirements
	 * 
	 * @param commonDto
	 * @return Long
	 */
	public Long getCountSubcareStage(CommonDto commonDto);

	/**
	 * Method Name: getPriorPlacementsById Method Description: This method
	 * returns prior placement list based on idPlacementEvent
	 * 
	 * @param idPriorPlacementEvent
	 * @return idPlacementEvents
	 */

	public List<Long> getPriorPlacementsById(Long idPriorPlacementEvent);

	/**
	 * Method Name: getIndChildSibling1 Method Description: This method returns
	 * prior Sibling person in the pca application.
	 * 
	 * @param idPerson
	 * @return indChildSibling1
	 */

	public String getIndChildSibling1(Long idPerson);

	/**
	 * 
	 * Method Name: getActualPlacement Method Description:CLSS84D -this Service
	 * retrieves all 'actual' PLACEMENT records for the given child and stage
	 * id.
	 **
	 * @param idStage
	 * @return
	 */
	List<PlacementDto> getActualPlacement(Long idStage);

	/**
	 * 
	 * Method Name: getMostRecentPlacement Method Description: CSES44D--This
	 * Service performs a complete row retrieval from the most recent actual
	 * adoption placement from the PLACEMENT table, given the ID_PLCMT_CHILD.
	 * This DAM is a sister DAM of CSES34D.
	 * 
	 * @param idPlcmtChild
	 * @return
	 */
	List<PlacementDto> getMostRecentPlacement(Long idPlcmtChild);

	/**
	 * 
	 * Method Name: getMostRecentPlacement Method Description: CSES44D This
	 * service retrieves the most recent living arrangement, placement start
	 * date from the Placement table using ID_PLCMT_CHILD.
	 * 
	 * @param idPlcmtChild
	 * @return List<PlacementDto>
	 */
	List<PlacementDto> getMostRecentLinvingArrangement(Long idPlcmtChild, String cdTask);

	public List<Long> getApprovers(Long idEvent);

	/**
	 * This DAM will retreive a Relative Placement from the PLACEMENT table
	 * where ID PERSON = the host and Dt Plcmt Strt <= input date and input date
	 * =< Max and IND PLCMT ACT PLANNED = true CSECC1D
	 * 
	 * @param idPlcmtChild
	 * @param dtSvcAuthEff
	 * @return
	 */
	public PlacementDto retrieveRelativePlacement(Long idPlcmtChild, Date dtSvcAuthEff);

	/**
	 * 
	 * Method Name: getEligibilityEvent Method Description: get eligibility
	 * event
	 * 
	 * @param idPerson
	 * @return
	 */
	public EligibilityDto getEligibilityEvent(Long idPerson);

	/**
	 * Method Name: alertPlacementReferral Method Description:
	 * 
	 * @param alertPlacementLsDto
	 */
	public void alertPlacementReferral(AlertPlacementLsDto alertPlacementLsDto);

	/**
	 * Method Name: getLatestPlcmntEvent Method Description:.
	 *
	 * @param idEvent
	 *            the id event
	 * @param idStage
	 *            the id stage
	 * @param cdEventType
	 *            the cd event type
	 * @return the latest plcmnt event
	 */
	public EventDto getLatestPlcmntEvent(Long idEvent, Long idStage, String cdEventType);

	/**
	 * 
	 * Method Name: retrievePlacementByEventId Method Description: get placement
	 * by eventId
	 * 
	 * @param idEvent
	 * @return
	 */
	public Placement retrievePlacementByEventId(Long idEvent);

	/**
	 * Method Name: getPlacementEventList Method Description: This method is
	 * used to get Placement EventList
	 * 
	 * @param idPlcmtChild
	 * @param cdPlcmtActPlanned
	 * @return List<Long>
	 */
	public List<Long> getPlacementEventList(Long idPlcmtChild, String cdPlcmtActPlanned);

	/**
	 * Method Name: getStageCount Method Description: This method is used to
	 * getStageCount
	 * 
	 * @param idStage
	 * @return Long
	 */
	public Long getStageCount(Long idStage);

	/**
	 * Method Name: getEventStageCount Method Description: This method is used
	 * to getEventStageCount
	 * 
	 * @param idStage
	 * @return Long
	 */
	public Long getEventStageCount(Long idStage);

	/**
	 * Method Name: checkOpenPlacement Method Description: This method is used
	 * to checkOpenPlacement
	 * 
	 * @param placementAUDDto
	 * @return Long
	 */
	public Long checkOpenPlacement(PlacementAUDDto placementAUDDto);

	/**
	 * Method Name: checkOtherOpenPlacement Method Description: This method is
	 * used to checkOtherOpenPlacement
	 * 
	 * @param placementAUDDto
	 * @return Long
	 */
	public Long checkOtherOpenPlacement(PlacementAUDDto placementAUDDto);

	/**
	 * Method Name: checkLeftOverlaps Method Description: This method is used to
	 * checkLeftOverlaps
	 * 
	 * @param placementAUDDto
	 * @return List<Long>
	 */
	public List<Long> checkLeftOverlaps(PlacementAUDDto placementAUDDto);

	/**
	 * Method Name: getOpenPlacementInOpenStages Method Description: This method
	 * is used to getOpenPlacementInOpenStages
	 * 
	 * @param idPlacemntEvent
	 * @return Long
	 */
	public Long getOpenPlacementInOpenStages(Long idPlacemntEvent);

	/**
	 * Method Name: getOpenPlacementInClosedStages Method Description: This
	 * method is used to getOpenPlacementInClosedStages
	 * 
	 * @param idPlacemntEvent
	 * @return Long
	 */
	public Long getOpenPlacementInClosedStages(Long idPlacemntEvent);

	/**
	 * Method Name: getOverlapingRecords Method Description: This method is used
	 * to getOverlapingRecords
	 * 
	 * @param placementAUDDto
	 * @return List<Long>
	 */
	public List<Long> checkRightOverlaps(PlacementAUDDto placementAUDDto);

	/**
	 * Method Name: getIdenticalRecords Method Description: This method is used
	 * to getIdenticalRecords
	 * 
	 * @param placementAUDDto
	 * @return List<Long>
	 */
	public List<Long> getIdenticalRecords(PlacementAUDDto placementAUDDto);

	/**
	 * Method Name: getPlacmentsStartingNextDay Method Description: This method
	 * is used to getPlacmentsStartingNextDay
	 * 
	 * @param placementAUDDto
	 * @return List<PlacementDto>
	 */
	public List<PlacementDto> checkLeftGaps(PlacementAUDDto placementAUDDto);

	/**
	 * Method Name: getPlacmentsStartingPreviousDay Method Description: This
	 * method is used to getPlacmentsStartingPreviousDay
	 * 
	 * @param placementAUDDto
	 * @return List<PlacementDto>
	 */
	public List<PlacementDto> checkRightGaps(PlacementAUDDto placementAUDDto);

	/**
	 * Method Name: savePlacement Method Description: This method is used to
	 * savePlacement
	 * 
	 * @param placementAUDDto
	 * @return Long
	 */
	public Long savePlacement(PlacementAUDDto placementAUDDto);

	/**
	 * Method Name: getPlacementForUpdate Method Description: This method is
	 * used to getPlacementForUpdate
	 * 
	 * @param placementAUDDto
	 * @return PlacementDto
	 */
	public PlacementDto getPlacementForUpdate(PlacementAUDDto placementAUDDto);

	/**
	 * Method Name: getAbsDates Method Description: This method is used to
	 * getAbsDates
	 * 
	 * @param placementDto
	 * @return PlacementDto
	 */
	public PlacementDto getAbsDates(PlacementDto placementDto);

	/**
	 * Method Name: checkLeftOverlapForUpdate Method Description: This method is
	 * used to checkLeftOverlapForUpdate
	 * 
	 * @param placementAUDDto
	 * @param placementDto
	 * @return List<Long>
	 */
	public List<Long> checkLeftOverlapForUpdate(PlacementAUDDto placementAUDDto, PlacementDto placementDto);

	/**
	 * Method Name: getOpenSubStageCount Method Description: This method is used
	 * to getOpenSubStageCount
	 * 
	 * @param idPlacementEvent
	 * @return Long
	 */
	public Long getOpenSubStageCount(Long idPlacementEvent);

	/**
	 * Method Name: checkRightOverlapForUpdate Method Description: This method
	 * is used to checkRightOverlapForUpdate
	 * 
	 * @param placementAUDDto
	 * @param placementDto
	 * @return List<Long>
	 */
	public List<Long> checkRightOverlapForUpdate(PlacementAUDDto placementAUDDto, PlacementDto placementDto);

	/**
	 * Method Name: getPlacmentsStartingNextDayForUpdate Method Description:
	 * This method is used to getPlacmentsStartingNextDayForUpdate
	 * 
	 * @param placementAUDDto
	 * @param placementDto
	 * @return List<PlacementDto>
	 */
	public List<PlacementDto> checkLeftGapForUpdate(PlacementAUDDto placementAUDDto, PlacementDto placementDto);

	/**
	 * Method Name: getPlacmentsStartingPreviousDayForUpdate Method Description:
	 * This method is used to getPlacmentsStartingPreviousDayForUpdate
	 * 
	 * @param placementAUDDto
	 * @param placementDto
	 * @return List<PlacementDto>
	 */
	public List<PlacementDto> checkRightGapForUpdate(PlacementAUDDto placementAUDDto, PlacementDto placementDto);

	/**
	 * Method Name: checkPlacementsOverlapingInDifferenctCases Method
	 * Description: This method is used to
	 * checkPlacementsOverlapingInDifferenctCases
	 * 
	 * @param placementAUDDto
	 * @param placementDto
	 * @return Long
	 */
	public Long checkPlacementsOverlapingInDifferenctCases(PlacementAUDDto placementAUDDto, PlacementDto placementDto);

	/**
	 * Method Name: updatePlacement Method Description: This method is used to
	 * updatePlacement
	 * 
	 * @param placementAUDDto
	 */
	public void updatePlacement(PlacementAUDDto placementAUDDto);

	/**
	 * Method Name: updateKinshipRecord Method Description: This method is used
	 * to updateKinshipRecord
	 * 
	 * @param placementAUDDto
	 */
	public void updateKinshipRecord(PlacementAUDDto placementAUDDto);

	/**
	 * Method Name: checkIdStage Method Description: This method is used to
	 * checkIdStage
	 * 
	 * @param placementAUDDto
	 * @return Long
	 */
	public Long checkIdStage(PlacementAUDDto placementAUDDto);

	/**
	 * Method Name: getPlacementEventForChild Method Description: This method is
	 * used to getPlacementEventForChild
	 * 
	 * @param placementAUDDto
	 * @return Long
	 */
	public List<Long> getPlacementEventForChild(PlacementAUDDto placementAUDDto);

	/**
	 * Method Name: checkLeftOverlapForNewRecords Method Description: This
	 * method is used to checkLeftOverlapForNewRecords
	 * 
	 * @param placementAUDDto
	 * @return List<Long>
	 */
	public List<Long> checkLeftOverlapForNewRecords(PlacementAUDDto placementAUDDto);

	/**
	 * Method Name: checkRightOverlapForNewRecords Method Description: This
	 * method is used to checkRightOverlapForNewRecords
	 * 
	 * @param placementAUDDto
	 * @return List<Long>
	 */
	public List<Long> checkRightOverlapForNewRecords(PlacementAUDDto placementAUDDto);

	/**
	 * Method Name: getIdenticalNewRecords Method Description: This method is
	 * used to getIdenticalNewRecords
	 * 
	 * @param placementAUDDto
	 * @return List<Long>
	 */
	public List<Long> getIdenticalNewRecords(PlacementAUDDto placementAUDDto);

	/**
	 * Method Name: getNewRecordsHavingLeftGapMoreThanOneDay Method Description:
	 * This method is used to getNewRecordsHavingLeftGapMoreThanOneDay
	 * 
	 * @param placementAUDDto
	 * @return List<PlacementDto>
	 */
	public List<PlacementDto> getNewRecordsHavingLeftGapMoreThanOneDay(PlacementAUDDto placementAUDDto);

	/**
	 * Method Name: getNewRecordsHavingRightGapMoreThanOneDay Method
	 * Description: This method is used to
	 * getNewRecordsHavingRightGapMoreThanOneDay
	 * 
	 * @param placementAUDDto
	 * @return List<PlacementDto>
	 */
	public List<PlacementDto> getNewRecordsHavingRightGapMoreThanOneDay(PlacementAUDDto placementAUDDto);

	/**
	 * Method Name: saveAndClosePlacement Method Description: This method is
	 * used to saveAndClosePlacement
	 * 
	 * @param placementAUDDto
	 */
	public void saveAndClosePlacement(PlacementAUDDto placementAUDDto);

	/**
	 * Method Name: updateKinshipInd Method Description: This method is used to
	 * update KinshipInd
	 * 
	 * @param placementAUDDto
	 */
	public void updateKinshipInd(PlacementAUDDto placementAUDDto);
	

	/**
	 * Method Name: getActiveTEPContract 
	 * Method Description:This method returns
	 * an active idcontract
	 * @param placementReq
	 * @return PlacementValueDto>	 
	 * */
	PlacementValueDto getActiveTepContract(PlacementReq placementReq);
	
	/**
	 * 
	 * Method Name: getCountOfAllPlacements  Method
	 * Description:This sql returns the count of actual TFC children with active palcements
	 * for a given idRsrcFacil and idPlcmtChild
	 * 
	 * @param placementReq
	 * @return
	 */
	CommonCountRes getCountOfAllPlacements(PlacementReq placementReq);

	/**
	 * 
	 * Method Name: getCountOfActiveTfcPlmnts  Method
	 * Description:This sql returns the count of other children with palcements
	 * for a given idRsrcFacil and idPlcmtChild
	 * 
	 * @param placementReq
	 * @return
	 */
	CommonCountRes getCountOfActiveTfcPlmnts(PlacementReq placementReq);

	/**
	 *Method Name:	getCountyRegion
	 *Method Description: Method to get the county region based on county.
	 *@param cdCounty
	 */
	public String getCountyRegion(String cdCounty);

	int getOpenPlacementCountForCase(Long idCase);

	public Long getOpenEligibilityEvent(Long idStage);



	//PPM 70054(FCL) - IMPACT Date Requirement for RCYFC Notification
	/**
	 * @param childBillOfRightsDto
	 * @param isPlacementApproved
	 * @param indNewActualPlcmt
	 */
	public void saveToChildBillOfRightsHistory(ChildBillOfRightsDto childBillOfRightsDto, boolean isPlacementApproved, String indNewActualPlcmt);

	/**
	 * @param idPlcmtChild
	 * @return
	 */
	public List<ChildBillOfRightsDto> getBillOfRightsDatesByChildId(Long idPlcmtChild);

	/**
	 * @param idPlcmtChild
	 * @return
	 */
	public Long getCountOfAllPlacementsByChildId(Long idPlcmtChild);

	/**
	 * @param idPlcmtEvent
	 * @param idPlcmtChild
	 * @return
	 */
	public List<ChildBillOfRightsDto> getBillOfRightsDates(Long idPlcmtEvent, Long idPlcmtChild);

	/**
	 * @param idPlcmtEvent
	 */
	public void setInitialBorDisableInd(Long idPlcmtEvent);

	/**
	 * @param idPlcmtChild
	 * @return
	 */
	public List<Date> getEarliestReviewBillOfRights(Long idPlcmtChild);

	public List<TemporaryAbsenceDto> getTemporaryAbsenceList(Long placementEventId);

	/**
	 * @param childBillOfRightsDto
	 */
	public void saveIntialBillOfRights(ChildBillOfRightsDto childBillOfRightsDto);

	/**
	 *Method Name:	getAllChildPlacementsId
	 *Method Description: Method to get all placement child IDs based on resource ID.
	 * @param homeResourceId
	 * @return
	 */
	public List<KinChildDto> getAllChildPlacementsId(Long homeResourceId);

	public KinChildDto getPlacementChildInfo(Long childId, Long resourceId);

	public boolean getHasApprovedPlacementForAChild(Long resourceId, Long placementChildId);

	public KinChildDto getPlacementInfo(Long placementChildId);

	public KinChildDto getPlacementLegalStatusInfo(Long placementChildId);

	public Long getPlacementsAdultId(Long resourceId);


	/**
	 * @param stageId
	 * @return
	 */
	public List<PlacementValueDto>  getChildPlcmtReferrals(Long stageId);

	public boolean chkValidFPSContractRsrc(Long idResource, Long idRsrcSscc);

	List<PlacementValueDto> getChildPlacement(Long idPerson, Long stageId);

	Boolean getAddOnSvcPkg(Long idPerson, Long idStage);

	public PlacementDto getLatestPlacement(Long plcmntChild, Long idCase);

	List<PlacementValueDto> getParentPlacement(Long idPerson, Long stageId);
	CommonCountRes getCountCPBPlcmntsForYouthParent(Long idPerson, Long stageId,Date placementStartDate);

	public List<String> getContractServices(PlacementDtlGpDto placementDtlGpDto, List<String> servicePackages);

	Boolean checkAlocBlocForNonT3cPlcmt(Long idCase, Date dtPlcmtStart);
}
