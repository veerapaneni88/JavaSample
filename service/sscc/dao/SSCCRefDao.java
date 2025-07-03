package us.tx.state.dfps.service.sscc.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.expression.ParseException;

import us.tx.state.dfps.common.dto.AssignmentGroupDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCListDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCParameterDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefFamilyDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefListDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefPlcmtDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCResourceDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCTimelineDto;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION EJB Service Name:
 * SSCCReferralDao Class Description: SSCCRefDao Mar 26, 2018 - 8:58:10 PM
 */
public interface SSCCRefDao {
	/**
	 * Method fetches the contract region for the stage using stage county
	 * 
	 * @param idStage
	 * @return @
	 */
	public SSCCParameterDto fetchSSCCCntrctRegionforStageCounty(Long idStage);

	/**
	 * Fetch all the active SSCC Referrals for stage for a the input Referral
	 * type
	 * 
	 * @param idStage
	 * @param strReferralType
	 * @return @
	 */
	public List<SSCCRefDto> fetchActiveSSCCReferralsForStage(Long idStage, String strReferralType);

	/**
	 * Fetch SSCC Contract Region for the given stage where region is fetched
	 * based on the Primary Child's Legal County
	 * 
	 * @param idCase
	 * @param idPrimaryChild
	 * @param cdStageType
	 * @return @
	 */
	public SSCCParameterDto fetchSSCCCntrctRegionSUB(Long idCase, Long idPrimaryChild, String cdStageType);

	/**
	 * Method fetches the SSCC Resource information for the input SSCC Contract
	 * Region
	 * 
	 * @param cdSSCCCntrctRegion
	 * @return @
	 */
	public SSCCResourceDto fetchSSCCResourceInfo(String cdSSCCCntrctRegion, Long idSSCCCatchment);

	/**
	 * Method returns true if the logged in user is SSCC External Staff.
	 * UNIT.IND_EXTERNAL = Y CD_UNIT_REGION = cdSSCCCntrctRegion
	 * UNIT.CD_UNIT_SPECIALIZATION = SSC EMPLOYEE. CD_EXTERNAL_TYPE = SSC
	 * 
	 * @param ulIdPerson
	 * @param cdSSCCCntrctRegion
	 * @return @
	 */
	public boolean isUserSSCCExternal(Long ulIdPerson, String cdSSCCCatchment);

	/**
	 * Method fetches a Referral information for all the referrals in the case,
	 * creates a list of SSCCReferralValueBean objects, sets the list into the
	 * SSCCRefListValueBean and returns it.
	 * 
	 * @param ssccRefListValBean
	 * @return @
	 */
	public SSCCRefListDto fetchReferralsForCase(SSCCRefListDto sSCCRefListDto);

	/**
	 * Method fetches a list of all the referrals in the case that have not been
	 * rescinded
	 * 
	 * @param idCase
	 * @param cdStatus
	 * @param strReferralType
	 * @return @
	 */
	public List<SSCCRefDto> fetchSSCCNotRescindRefForCase(Long idCase, String cdStatus, String strReferralType);

	/**
	 * This method will fetch the referrals for a stage with a specific status
	 * (for eg. Active) and a specific referral type.
	 * 
	 * @param idStage
	 * @param cdStatus
	 * @param strReferralType
	 * @return @
	 */
	public List<SSCCRefDto> fetchSSCCReferralsForStage(Long idStage, String cdStatus, String strReferralType);

	/**
	 * Method deletes all records in SSCC_REFERRAL table that have a 'NEW'
	 * status
	 * 
	 * @param idCase
	 * @
	 */
	public void deleteNewSSCCReferral(Long idCase);

	/**
	 * Check if Case has an active SSCC Referral
	 * 
	 * @param hasSSCCReferralReq
	 * @return HasSSCCReferralRes @
	 */
	public String hasActiveSSCCReferral(Long caseId);

	/**
	 * 
	 * Method Name: hasSSCCActExcptCareDesig Method Description: Returns true if
	 * referral has any SSCC Exceptional care designations in PROPOSE status
	 * 
	 * @param idSsccReferral
	 * @return Boolean
	 */
	public Boolean hasSSCCActExcptCareDesig(Long idSsccReferral);

	/**
	 * 
	 * Method Name: hasActiveSSCCSvcAuthReq Method Description: Returns true if
	 * the referral has any SSCC Service Auth Request in the following status 1.
	 * INITIATE 2. PROPOSE
	 * 
	 * @param servAuthRetrieveReq
	 * @return Boolean
	 */
	public Boolean hasActiveSSCCSvcAuthReq(Long idSsccReferral);

	/**
	 * 
	 * Method Name: hasActivePlcmtCircumforRef Method Description :Returns true
	 * if referral has any Placement Circumstances in the following status 1.
	 * PROPOSE 2. ACKNOWLEDGE
	 * 
	 * @param idSsccReferral
	 * @return Boolean
	 */
	public Boolean hasActivePlcmtCircumforRef(Long idSsccReferral);

	/**
	 * Method Name: primaryChildHasLegalStatusForCase Method Description:Method
	 * returns true if the Primary Child has at least one Legal Status record
	 * for the case
	 * 
	 * @param idCase
	 * @param idPrimaryChild
	 * @return boolean
	 */
	public boolean primaryChildHasLegalStatusForCase(Long idCase, Long idPrimaryChild);

	/**
	 * 
	 * Method Name: fetchActiveSSCCRefForStage Method Description : Fetch all
	 * the active SSCC Referrals for stage for a the input Referral type
	 * 
	 * @param idStage
	 * @param cdRefType
	 * @return List
	 */
	public List<SSCCRefDto> fetchActiveSSCCRefForStage(Long idStage, String cdRefType);

	/**
	 * Method Name: userHasSSCCCatchmentAccess Method Description:Method returns
	 * true is user is In or Out assigned to a unit whose region matches the
	 * input region
	 * 
	 * @param ulIdPerson
	 * @param cdSSCCCatchment
	 * @return boolean
	 */
	public boolean userHasSSCCCatchmentAccess(Long ulIdPerson, String cdSSCCCatchment);

	/**
	 * Method Name: fetchReferralByPK Method Description:Fetches the Referral
	 * record for the given Primary Key
	 * 
	 * @param idSsccReferral
	 * @return SSCCRefDto
	 */
	public SSCCRefDto fetchReferralByPK(Long idSsccReferral);

	/**
	 * 
	 * Method Name: fetchDFPSStaffAssignedtoStage Method Description:Fetch
	 * Primary and Secondary staff assigned to stage
	 * 
	 * @param idStage
	 * @return HashMap<Long ,AssignmentGroupDto>
	 */
	public HashMap<Long, AssignmentGroupDto> fetchDFPSStaffAssignedtoStage(Long idStage);

	/**
	 * 
	 * Method Name: fetchSSCCSecondaryForStage Method Description:Method fetches
	 * the SSCC Secondary Staff assigned to stage
	 * 
	 * @param idStage
	 * @param cdSSCCCatchment
	 * @return List<Long>
	 */
	public List<Long> fetchSSCCSecondaryForStage(Long idStage, String cdSSCCCatchment);

	/**
	 * Method Name: getSSCCStaffInCatchment Method Description:Method returns an
	 * arraylist of all the staff in a given catchment
	 * 
	 * @param cdSSCCCatchment
	 * @return List<Long>
	 */
	public List<Long> getSSCCStaffInCatchment(String cdSSCCCatchment);

	/**
	 * 
	 * Method Name: hasActivePlcmtOptionsforRef Method Description: Returns true
	 * if referral has any Placement Option in the following status 1.PROPOSE
	 * 2.ACKNOWLEDGE 3.APPROVE 4.PLACED
	 * 
	 * @param idSsccReferral
	 * @return Boolean
	 */
	public Boolean hasActivePlcmtOptionsforRef(Long idSsccReferral);

	/**
	 * Method Name: fetchCaseProgram Method Description:Method returns the case
	 * program for the given case
	 * 
	 * @param idCase
	 * @return String
	 */
	public String fetchCaseProgram(Long idCase);

	/**
	 * Method Name: fetchLegalStatusForCase Method Description: Fetches the
	 * Legal Status for the Primary Child in a given case
	 * 
	 * @param ssccRefDto
	 * @return SSCCRefDto
	 */
	public SSCCRefDto fetchLegalStatusForCase(SSCCRefDto ssccRefDto);

	/**
	 * Method Name: fetchCaseInformationForPC Method Description: Method fetches
	 * the list of closed SUB stages from other cases where the person is the
	 * Primary Child of the SUB stage
	 * 
	 * @param ssccRefDto
	 * @return List<StageDto>
	 */
	public List<StageDto> fetchCaseInformationForPC(SSCCRefDto ssccRefDto);

	/**
	 * Method returns true if there is at least one active SSCC Family Service
	 * Referral for a given case.
	 * 
	 * @param idCase
	 * @return
	 */
	public boolean hasActiveSSCCFamilySvcRefInCase(Long idCase);

	/**
	 * Method Name: hasActiveSSCCChildPlanContent Method Description:Method
	 * returns true if there is at least one active SSCC Child Plan topic for
	 * referral
	 * 
	 * @param ssccRefValBean
	 * @return boolean
	 */
	public boolean hasActiveSSCCChildPlanContent(SSCCRefDto ssccRefValBean);

	/**
	 * Method Name: isPlcmtDatesWithinRefRange Method Description: Returns true
	 * if the given placement start and end dates are within the range of at
	 * least one discharged SSCC Referral for stage and resource
	 * 
	 * @param sSCCRefDto
	 * @return boolean
	 */
	public boolean isPlcmtDatesWithinRefRange(Long idPlcmtEvent, Long idStage);

	/**
	 * Method Name: isPlcmtDatesWithinRefRange Method Description: Returns true
	 * if the given placement start and end dates are within the range of at
	 * least one discharged SSCC Referral for stage and resource.
	 * 
	 * @param sSCCRefDto
	 * @param string
	 * @return List<SSCCRefDto>
	 */
	public List<SSCCRefDto> fetchAprvSvcAuthList(SSCCRefDto sSCCRefDto, Long vendorId);

	/**
	 * Method Name: fetchCnsrvtrshpRemovalData Method Description: Fetch the
	 * Conservatorship removal date for PC in SUB stage
	 * 
	 * @param ssccReferralDto
	 * @return
	 */
	public SSCCRefDto fetchCnsrvtrshpRemovalData(SSCCRefDto ssccRefDto);

	/**
	 * 
	 * Method Name: fetchVendorIdforReferral Method Description:Returns the
	 * Vendor Id from the batch_sscc_parameters table for a specific Resource Id
	 * and Region.
	 * 
	 * @param ssccRefDto
	 * @return
	 */
	public Long fetchVendorIdforReferral(SSCCRefDto ssccRefDto);

	/**
	 * 
	 * Method Name: hasActiveSvcforRefPerson Method Description:true if referral
	 * person has any Service Authorization in PROC, COMP ,PEND status.
	 * 
	 * @param ssccRefDto
	 * @param vendorId
	 * @return
	 */
	public boolean hasActiveSvcforRefPerson(SSCCRefDto ssccRefDto, Long vendorId);

	/**
	 * Inserts record into the SSCC Timeline table
	 * 
	 * @param timelineDto
	 */
	public Long saveSSCCTimeline(SSCCTimelineDto timelineDto);

	/**
	 * Method updates an SSCC Referral record in the SSCC_REFERRAL table
	 * 
	 * @param ssccReferralDto
	 * @return
	 * 
	 * 		public SSCCRefDto updateSSCCRefHeader(SSCCRefDto
	 *         ssccReferralDto);
	 * 
	 *         /** Method updates an cdStatus in the SSCC_REFERRAL table for the
	 *         input idSSCCReferral
	 * 
	 * @param cdStatus
	 * @param idSsccReferral
	 * 
	 */
	public int updatePlcmtHeaderStatus(String cdStatus, Long idSsccReferral);

	/**
	 * Method updates the cd_status column in the sscc_child_plan_topic table
	 * 
	 * @param cdStatus
	 * @param idSsccReferral
	 * 
	 */
	public int updateSSCCChildPlanTopicStatus(String cdStatus, Long idSsccReferral);

	/**
	 * Method updates the cd_status column in the sscc_child_plan table
	 * 
	 * @param cdStatus
	 * @param idSsccReferral
	 * 
	 */
	public int updateSSCCChildPlanStatus(String cdStatus, Long idSsccReferral);

	/**
	 * Method updates a row in the SSCC_SERVICE_AUTHORIZATION table
	 * 
	 * @param cdStatus
	 * @param idSsccReferral
	 * 
	 */
	public int updateSSCCServiceAuth(String cdStatus, Long idSsccReferral);

	/**
	 * Method unassign's all SSCC Secondary Staff for stage Also, unassign's
	 * staff when necessary
	 * 
	 * @param idStaffPerson
	 * @param idStage
	 * 
	 */
	public void unAssignAllSSCCSecondaryStaff(SSCCRefDto ssccReferralDto);

	/**
	 * Method updates an SSCC Referral record in the SSCC_REFERRAL table
	 * 
	 * @param ssccReferralDto
	 * @return
	 * 
	 * @throws ParseException
	 */
	public Long updateSSCCRefHeader(SSCCRefDto ssccReferralDto);

	/**
	 * Method Name: fetchSSCCReferralFamilyPersonList Method Description:
	 * Fetches SSCC Referral Family records for an SSCC Referral
	 * 
	 * @param idSSCCReferral
	 * @return
	 */
	public ArrayList<SSCCRefFamilyDto> fetchSSCCReferralFamilyPersonList(Long idSSCCReferral);

	/**
	 * Method Name: fetchLegalStatusForRef Method Description: Fetches the Legal
	 * Status information for the Referral Placement Information section from
	 * sscc_referral_event, legal_status and event tables
	 * 
	 * @param idSSCCReferral
	 * @return
	 */
	public SSCCRefDto fetchLegalStatusForRef(SSCCRefDto ssccRefDto);

	/**
	 * Method name: fetchRefPlcmtCnsrvtrshpRemovalDt Method Description: Fetches
	 * the conservator-ship removal date for Placement Referral from
	 * sscc_referral_event, event and cnsrvtrshp_removal tables.
	 * 
	 * @param ssccRefDto
	 * @return
	 */
	public SSCCRefDto fetchRefPlcmtCnsrvtrshpRemovalDt(SSCCRefDto ssccRefDto);

	/**
	 * 
	 * Method Name: fetchSSCCResourceDtStart Method Description: This method is
	 * used to retrieve start date of the resource from the sscc_parameters
	 * table.
	 * 
	 * @param idSSCCResource
	 * @param idSSCCCatchment
	 * @param cdCntrctRegion
	 * @param refType 
	 */
	public Date fetchSSCCResourceDtStart(Long idSSCCResource, Long idSSCCCatchment, String cdCntrctRegion, String refType);

	/**
	 * Method Name: updateSsccReferralEvent Method Description: This mthod
	 * updated sscc referral event table.
	 * 
	 * @param ssccRefPlcmtDto
	 * @return
	 */
	public Long updateSsccReferralEvent(SSCCRefPlcmtDto ssccRefPlcmtDto);

	/**
	 * Method Name: deleteStagePersonLink Method Description: This method is
	 * used to delete stageperson link table.
	 * 
	 * @param idStaffPerson
	 * @param idStage
	 */
	public void deleteStagePersonLink(Long idStaffPerson, Long idStage);

	/**
	 * Method Name: insertIntoStagePersonLink Method Description: This method
	 * inserts stage person link table
	 * 
	 * @param stagePersonValueDto
	 * @return
	 */
	public Long insertIntoStagePersonLink(SSCCRefDto ssccReferralDto, Long idSecondaryStaff);

	/**
	 * Method Name: createSecondaryAssignTodo Method Description: This method is
	 * used to create todo's for secondary assigned staff.
	 * 
	 * @param ssccReferralDto
	 * @param userId
	 * @param idPesonAssigned
	 * @param idPrimaryForStage
	 * @return
	 */
	public void createSecondaryAssignTodo(SSCCRefDto ssccReferralDto, String userId, long idPesonAssigned,
			long idPrimaryForStage);

	/**
	 * Method Name: updateSSCCList Method Description: This method is used to
	 * update sscclist table.
	 * 
	 * @param ssccListDto
	 * @return
	 */
	public Long updateSSCCList(SSCCListDto ssccListDto);

	/**
	 * Method Name: deleteSSCCReferral Method Description: This method deletes
	 * the sscc referral header information from SSCC_REFERRAL table
	 * 
	 * @param idSSCCReferral
	 * @return
	 */
	public boolean deleteSSCCReferral(Long idSSCCReferral);

	/**
	 * Method name: hasFamRefMaxDtDischargeInCase Method Description:Method
	 * returns true if the given Family SVC Referral has the maximum actual
	 * discharge date for a given case.
	 * 
	 * 
	 * @param idSSCCReferral,idCase
	 * @return boolean
	 */

	public boolean hasFamRefMaxDtDischargeInCase(Long idSSCCReferral, Long idCase);

	/**
	 * Method name: hasActiveSSCCPlcmtReferralExistsForStage Method Description:
	 * Method returns true if there is at least one active SSCC Family Service
	 * Referral for a given case
	 * 
	 * @param idStage
	 *            , placementReferaal
	 * @return boolean
	 */

	public boolean hasActiveSSCCPlcmtReferralExistsForStage(Long idStage, String placementReferaal);

	/**
	 * Method name: hasPlcmtRefMaxDtDischargeInStage Method Description: Method
	 * returns true if the given Placement Referral has the maximum actual
	 * discharge date for a given stage.
	 * 
	 * @param idSSCCReferral,idStage
	 * @return
	 */

	public boolean hasPlcmtRefMaxDtDischargeInStage(Long idSSCCReferral, Long idStage);

	/**
	 * Method name: updateSSCCRefStatus Method Description: this method is used
	 * to update SSCC Referral Status
	 * 
	 * @param ssccRefDto
	 * @return
	 */

	public void updateSSCCRefStatus(SSCCRefDto ssccRefDto, String cdStatus);

	/**
	 * 
	 * Method Name: fetchSSCCPlcmtEndDtList Method Description: Method fetches a
	 * list of SSCC Placement End Dates for a stage and sscc resource id where
	 * placement start date is same as placement end date
	 * 
	 * @param sSCCRefDto
	 * @return
	 */
	public List<PlacementDto> fetchSSCCPlcmtEndDtList(Long idStage, Long idSSCCResource);

	/**
	 * 
	 * Method Name: deleteSSCCChildPlanParticip Method Description: This method
	 * is used to delete all the sscc Child Plans which are linked with
	 * idSSCCReferral.
	 * 
	 * @param sSCCRefDto
	 * @return
	 */
	public void deleteSSCCChildPlanParticip(Long idSSCCReferral);

	/**
	 * 
	 * Method Name: deleteSSCCTimeline Method Description: This method is used
	 * to delete all the sscc child plan topics linked with idSSCCReferral
	 * 
	 * @param sSCCRefDto
	 * @return
	 */
	public void deleteSSCCTimeline(Long idSSCCReferral);

	/**
	 * 
	 * Method Name: deleteSSCCSvcAuth Method Description: This method is used to
	 * delete all the SSCC service authorizations linked with idSSCCReferral
	 * 
	 * @param sSCCRefDto
	 * @return
	 */
	public void deleteSSCCSvcAuth(Long idSSCCReferral);

	/**
	 * 
	 * Method Name: deleteSSCCRefFamily Method Description: This method is used
	 * to delete all the sscc family referrals linked with idSSCCReferrals.
	 * 
	 * @param sSCCRefDto
	 * @return
	 */
	public void deleteSSCCRefFamily(Long idSSCCReferral);

	/**
	 * 
	 * Method Name: deleteSSCCRefEvent Method Description: This method is used
	 * to delete all the ssccReferralEvents linked with idSSCCReferral
	 * 
	 * @param sSCCRefDto
	 * @return
	 */
	public void deleteSSCCRefEvent(Long idSSCCReferral);

	/**
	 * 
	 * Method Name: deleteSSCCPlcmtHeader Method Description: This method is
	 * used to delete all the SSCC placements linked with idSSCCReferral
	 * 
	 * @param sSCCRefDto
	 * @return
	 */
	public void deleteSSCCPlcmtHeader(Long idSSCCReferral);

	/**
	 * 
	 * Method Name: deleteSSCCPlcmtCircumstance Method Description: This method
	 * is used to delete all the sscc placement circumstances linked with
	 * idSSCCReferral
	 * 
	 * @param sSCCRefDto
	 * @return
	 */
	public void deleteSSCCPlcmtCircumstance(Long idSSCCReferral);

	/**
	 * 
	 * Method Name: deleteSSCCPlcmtName Method Description: This method is used
	 * to delete all the sscc placement name linked with idSSCCReferral
	 * 
	 * @param sSCCRefDto
	 * @return
	 */
	public void deleteSSCCPlcmtName(Long idSSCCReferral);

	/**
	 * 
	 * Method Name: deleteSSCCPlcmtMedCnsntr Method Description: This method is
	 * used to delete all the sscc placement medical consentors linked with
	 * idSSCCReferral
	 * 
	 * @param sSCCRefDto
	 * @return
	 */
	public void deleteSSCCPlcmtMedCnsntr(Long idSSCCReferral);

	/**
	 * 
	 * Method Name: deleteSSCCPlcmtInfo Method Description: This method is used
	 * to delete all the sscc placement info linked with idSSCCReferral
	 * 
	 * @param sSCCRefDto
	 * @return
	 */
	public void deleteSSCCPlcmtInfo(Long idSSCCReferral);

	/**
	 * 
	 * Method Name: deleteSSCCPlcmtPlaced Method Description: This method is
	 * used to delete all the sscc placements placed linked with idSSCCReferral
	 * 
	 * @param sSCCRefDto
	 * @return
	 */
	public void deleteSSCCPlcmtPlaced(Long idSSCCReferral);

	/**
	 * 
	 * Method Name: deleteSSCCPlcmtNarr Method Description: This method is used
	 * to delete all the sscc placement narratives linked with idSSCCReferral
	 * 
	 * @param sSCCRefDto
	 * @return
	 */
	public void deleteSSCCPlcmtNarr(Long idSSCCReferral);

	/**
	 * 
	 * Method Name: deleteSSCCList Method Description: This method is used to
	 * delete all the sscc list linked with idSSCCReferral
	 * 
	 * @param sSCCRefDto
	 * @return
	 */
	public void deleteSSCCList(Long idSSCCReferral);

	/**
	 * 
	 * Method Name: deleteSSCCExceptCareDesig Method Description: This method is
	 * used to delete all the sscc exceptional care designee linked with
	 * idSSCCReferral
	 * 
	 * @param sSCCRefDto
	 * @return
	 */
	public void deleteSSCCExceptCareDesig(Long idSSCCReferral);

	/**
	 * 
	 * Method Name: deleteSSCCDaycareRequest Method Description: This method is
	 * used to delete all the sscc day care requests linked with idSSCCReferral
	 * 
	 * @param sSCCRefDto
	 * @return
	 */
	public void deleteSSCCDaycareRequest(Long idSSCCReferral);

	/**
	 * 
	 * Method Name: deleteSSCCChildPlan Method Description: This method is used
	 * to delete all the sscc child plan linked with idSSCCReferral
	 * 
	 * @param sSCCRefDto
	 * @return
	 */
	public void deleteSSCCChildPlan(Long idSSCCReferral);

	/**
	 * 
	 * Method Name: deleteSSCCChildPlanTopic Method Description: This method is
	 * used to delete all the sscc child plan topics linked with idSSCCReferral
	 * 
	 * @param sSCCRefDto
	 * @return
	 */
	public void deleteSSCCChildPlanTopic(Long idSSCCReferral);

	/**
	 * 
	 * Method Name: hasActiveSSCCChildPlan Method Description: This method
	 * returns true if there is an SSCC Child Plan in PROC status
	 * 
	 * @param sSCCRefDto
	 * @return
	 */
	public boolean hasActiveSSCCChildPlan(Long idSSCCResource);

	/**
	 * 
	 * Method Name: fetchDischargedSSCCReferralsforStage Method Description:
	 * Returns an array list of SSCCRefDto objects of discharged referrals
	 * 
	 * @param sSCCRefDto
	 * @return
	 */
	public List<SSCCRefDto> fetchDischargedSSCCReferralsforStage(SSCCRefDto ssccRefDto);


	/**
	 * Method Name: fetchPriorDischargeActualDt Method Description: Method
	 * fetches the Actual Discharge Date of the most recent discharged SSCC
	 * Referral in the stage
	 * 
	 * @param idStage
	 * @return
	 */
	public Date fetchPriorDischargeActualDt(Long idStage);

	/**
	 * Method Name: updateSSCCRefIndPlcmtData Method Description: Method to
	 * update the Linked Placement Data Indicator When the Placement is created
	 * through SSCC PlcmtOpt Page.
	 * 
	 * @param idSSCCReferral
	 */
	void updateSSCCRefIndPlcmtData(Long idSSCCReferral);

	/**
	 * Method Name: saveSSCCRefFamily Method Description:this method is used to
	 * save the fmily referrals
	 * 
	 * @param ssccRefFamilyDto
	 * @return
	 */
	Long saveSSCCRefFamily(SSCCRefFamilyDto ssccRefFamilyDto, String idUser);

	/**
	 * Method Name: updateSSCCRefFamily Method Description:This method saves the
	 * end date with new date when user removes the person from the family
	 * referral
	 * 
	 * @param ssccRefFamilyDto
	 * @param idUser
	 * @return
	 */
	Long updateSSCCRefFamily(SSCCRefFamilyDto ssccRefFamilyDto, String idUser);

	/**
	 * Method Name: updateSSCCRefFamilyForUndoDischarge Method Description:This method removes the 
	 * end date for the persons who were ended when the family referral was discharged.
	 * @param ssccRefDto
	 * @param idUser
	 */
	void updateSSCCRefFamilyForUndoDischarge(SSCCRefDto ssccRefDto, Long idUser);

	/**
	 * Returns SSCCRefFamilyDto objects of person with end date
	 */
	public SSCCRefFamilyDto fetchEndDateForPersonInFamReferral(Long idPerson, Long idCase);

	/**
	 * code added for artf231094
	 * @param idSSCCReferral
	 * @param idPerson
	 * @return
	 */
	List<Long> fetchSSCCRefCount(Long idSSCCReferral, Long idPerson);

	/**
	 * Method Name: insertIntoStagePersonLinkForTransfer Method Description: This method
	 * inserts stage person link table
	 *
	 * @param ssccReferralDto
	 * @param sSCCRefFamilyDto
	 * @return
	 */
	public Long insertIntoStagePersonLinkForTransfer(SSCCRefDto ssccReferralDto, SSCCRefFamilyDto sSCCRefFamilyDto, Long oldStageId);
}
