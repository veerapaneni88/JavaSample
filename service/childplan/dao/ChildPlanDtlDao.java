package us.tx.state.dfps.service.childplan.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.childplan.dto.ChidPlanPsychMedctnDtlDto;
import us.tx.state.dfps.service.childplan.dto.ChildPalnBehaviorMgntDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanAdtnlSctnDtlDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanEducationDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanEmtnlThrptcDtlDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanFmlyTeamPrtctpnDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanHealthCareSummaryDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanHighRiskServicesDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanInformationDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanIntellectualDevelopDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanLegalGrdnshpDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanOfServiceDtlDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanOfServiceDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanPriorAdpInfoDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanSocialRecreationalDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanSupervisionDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanTransAdltAbvDtlDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanTransAdtltBlwDtlDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanTreatmentServiceDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanVisitationCnctFmlyDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanQrtpPrmnncyMeetingDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanYouthParentingDto;
import us.tx.state.dfps.service.common.request.ChildPlanDtlReq;
import us.tx.state.dfps.service.common.response.ChildPlanDtlRes;
import us.tx.state.dfps.service.person.dto.ServicePackageDtlDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * will interact with the database to save, save&submit, delete child plan
 * detail information and execute the stored procedure to fetch the data. May 4,
 * 2018- 10:25:08 AM © 2017 Texas Department of Family and Protective Services
 */
public interface ChildPlanDtlDao {

	/**
	 * Method Name: getPriorAdptn Method Description: This method is used to
	 * retrieve prior adoption section.
	 * 
	 * @param idChildEvent
	 * @return List<ChildPlanPriorAdpInfoDto>
	 */
	public List<ChildPlanPriorAdpInfoDto> getPriorAdptn(Long idChildEvent);

	/**
	 * Method Name: getLegalGuardianship Method Description: This method is used
	 * to retrieve Legal Guardianship section.
	 * 
	 * @param idChildPlanEvent
	 * @return List<ChildPlanLegalGrdnshpDto>
	 */
	public List<ChildPlanLegalGrdnshpDto> getLegalGuardianship(Long idChildPlanEvent);

	/**
	 * Method Name: getPlanVisitation Method Description: This method is used to
	 * retrieve child plan visitation section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanVisitationCnctFmlyDto
	 */
	public ChildPlanVisitationCnctFmlyDto getPlanVisitation(Long idChildPlanEvent);

	/**
	 * Method Name: getQrtpPermanencyMtng Method Description: This method is used to
	 * retrieve child plan QRTP Permanency Meeting Section.
	 *
	 * @param idChildPlanEvent
	 * @return ChildPlanQrtpPrmnncyMeetingDto
	 */
	public ChildPlanQrtpPrmnncyMeetingDto getQrtpPermanencyMtng(Long idChildPlanEvent);

	/**
	 * Method Name: getIntellectualDevelop Method Description: This method is
	 * used to retrieve Intellectual development section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanIntellectualDevelopDto
	 */
	public ChildPlanIntellectualDevelopDto getIntellectualDevelop(Long idChildPlanEvent);

	/**
	 * Method Name: getChildEducation Method Description:This method is used to
	 * retrieve education section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanEducationDto
	 */
	public ChildPlanEducationDto getChildEducation(Long idChildPlanEvent);

	/**
	 * Method Name: getEmtnlThrptcDtl Method Description: This method is used to
	 * retrieve child's emotional, psyc, Thrpt section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanEmtnlThrptcDtlDto
	 */
	public ChildPlanEmtnlThrptcDtlDto getEmtnlThrptcDtl(Long idChildPlanEvent);

	/**
	 * Method Name: getBehaviorMgnt Method Description: This method is used to
	 * retrieve Behavior management section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPalnBehaviorMgntDto
	 */
	public ChildPalnBehaviorMgntDto getBehaviorMgnt(Long idChildPlanEvent);

	/**
	 * Method Name: getYouthParenting Method Description: This method is used to
	 * retrieve youth parenting section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanYouthParentingDto
	 */
	public ChildPlanYouthParentingDto getYouthParenting(Long idChildPlanEvent);

	/**
	 * Method Name: getHealthCareSummary Method Description: This method is used
	 * to retrieve health care summary section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanHealthCareSummaryDto
	 */
	public ChildPlanHealthCareSummaryDto getHealthCareSummary(Long idChildPlanEvent, Long idStage);

	/**
	 * Method Name: getSupervision Method Description:This method is used to
	 * retrieve supervision section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanSupervisionDto
	 */
	public ChildPlanSupervisionDto getSupervision(Long idChildPlanEvent);

	/**
	 * Method Name: getSocialRecreational Method Description:This method is used
	 * to retrieve social recreational section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanSocialRecreationalDto
	 */
	public ChildPlanSocialRecreationalDto getSocialRecreational(Long idChildPlanEvent);

	/**
	 * Method Name: getTransAdltAbvDtl Method Description: This method is used
	 * to retrieve transition adulthood above fourteen section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanTransAdltAbvDtlDto
	 */
	public ChildPlanTransAdltAbvDtlDto getTransAdltAbvDtl(Long idChildPlanEvent);

	/**
	 * Method Name: getTransAdtltBlwDtl Method Description:This method is used
	 * to retrieve transition adulthood below thirteen section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanTransAdtltBlwDtlDto
	 */
	public ChildPlanTransAdtltBlwDtlDto getTransAdtltBlwDtl(Long idChildPlanEvent);

	/**
	 * Method Name: getHighRiskServices Method Description: This method is used
	 * to retrieve high risk services section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanHighRiskServicesDto
	 */
	public ChildPlanHighRiskServicesDto getHighRiskServices(Long idChildPlanEvent);

	/**
	 * Method Name: getTreatmentService Method Description: This method is used
	 * to retrieve treatment services section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanTreatmentServiceDto
	 */
	public ChildPlanTreatmentServiceDto getTreatmentService(Long idChildPlanEvent);

	/**
	 * Method Name: getFmlyTeamPrtctpn Method Description: This method is used
	 * to retrieve family team participation section.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanFmlyTeamPrtctpnDto
	 */
	public ChildPlanFmlyTeamPrtctpnDto getFmlyTeamPrtctpn(Long idChildPlanEvent);

	/**
	 * Method Name: getAdtnlSctnDtl Method Description: This method is used to
	 * retrieve additional section details in child plan page
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanAdtnlSctnDtlDto
	 */
	public ChildPlanAdtnlSctnDtlDto getAdtnlSctnDtl(Long idChildPlanEvent);

	/**
	 * Method Name: getChildPlanInformation Method Description: This method is
	 * used to retrieve child plan information.
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanInformationDto
	 */
	public ChildPlanInformationDto getChildPlanInformation(Long idChildPlanEvent);

	/**
	 * Method Name: saveAdtnlSctnDtl Method Description: This method is used to
	 * save child plan information section.
	 * 
	 * @param childPlanAdtnlSctnDtlDto
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes saveAdtnlSctnDtl(ChildPlanAdtnlSctnDtlDto childPlanAdtnlSctnDtlDto, String szUserId);

	/**
	 * Method Name: saveChildPlanInfo Method Description: This method is used to
	 * save child plan additional section details.
	 * 
	 * @param childPlanInformationDto
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes saveChildPlanInfo(ChildPlanInformationDto childPlanInformationDto, String szUserId);

	/**
	 * Method Name: saveLegalGuardian Method Description: This method is used to
	 * save legal guardian section in child plan.
	 * 
	 * @param childPlanLegalGrdnshpDtoList
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes saveLegalGuardian(ChildPlanLegalGrdnshpDto childPlanLegalGrdnshpDtoList, String szUserId);

	/**
	 * Method Name: savePriorAdoption Method Description: This method is used to
	 * save prior adoption section in child plan.
	 * 
	 * @param childPlanLegalGrdnshpDtoList
	 */
	public ChildPlanDtlRes savePriorAdoption(ChildPlanPriorAdpInfoDto childPlanPriorAdpInfoDto, String szUserId);

	/**
	 * Method Name: savePlanVisitation Method Description: This method is used
	 * to save child visitation plan section in child plan.
	 * 
	 * @param childPlanVisitationCnctFmlyDto
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes savePlanVisitation(ChildPlanVisitationCnctFmlyDto childPlanVisitationCnctFmlyDto,
			String szUserId);

	/**
	 * Method Name: saveQrtpPermanencyMtng Method Description: This method is used
	 * to save QRTP PTM section in child plan.
	 *
	 * @param childPlanQrtpPrmnncyMeetingDto
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes saveQrtpPermanencyMtng(ChildPlanQrtpPrmnncyMeetingDto childPlanQrtpPrmnncyMeetingDto,
											  String szUserId);

	/**
	 * Method Name: saveIntellectualDevelop Method Description: This method is
	 * used to save intellectual development section in child plan.
	 * 
	 * @param childPlanIntellectualDevelopDto
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes saveIntellectualDevelop(ChildPlanIntellectualDevelopDto childPlanIntellectualDevelopDto,
			String szUserId);

	/**
	 * Method Name: saveEducation Method Description: This method is used to sae
	 * the education section in child plan.
	 * 
	 * @param childPlanEducationDto
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes saveEducation(ChildPlanEducationDto childPlanEducationDto, String szUserId);

	/**
	 * Method Name: saveEmtnlThrptcDtl Method Description: This method is used
	 * to save emotional, physic, thrpt section in child plan.
	 * 
	 * @param childPlanEmtnlThrptcDtlDto
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes saveEmtnlThrptcDtl(ChildPlanEmtnlThrptcDtlDto childPlanEmtnlThrptcDtlDto, String szUserId);

	/**
	 * Method Name: saveBehaviorMgnt Method Description: This method is used to
	 * save behavior management section
	 * 
	 * @param childPalnBehaviorMgntDto
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes saveBehaviorMgnt(ChildPalnBehaviorMgntDto childPalnBehaviorMgntDto, String szUserId);

	/**
	 * Method Name: saveYouthParenting Method Description: This method is used
	 * for saving youth parenting section in child plan.
	 * 
	 * @param childPlanYouthParentingDto
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes saveYouthParenting(ChildPlanYouthParentingDto childPlanYouthParentingDto, String szUserId);

	/**
	 * Method Name: saveHealthCareSummary Method Description: This method is
	 * used to save the health care summary details in child plan.
	 * 
	 * @param childPlanHealthCareSummaryDto
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes saveHealthCareSummary(ChildPlanHealthCareSummaryDto childPlanHealthCareSummaryDto,
			String szUserId);

	/**
	 * Method Name: saveSupervision Method Description: This method is used to
	 * save supervision section in child plan.
	 * 
	 * @param childPlanSupervisionDto
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes saveSupervision(ChildPlanSupervisionDto childPlanSupervisionDto, String szUserId);

	/**
	 * Method Name: saveSocialRecreational Method Description: This method is
	 * used to save social recreational section.
	 * 
	 * @param childPlanSocialRecreationalDto
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes saveSocialRecreational(ChildPlanSocialRecreationalDto childPlanSocialRecreationalDto,
			String szUserId);

	/**
	 * Method Name: saveTransAdulthoodBelowThirteen Method Description: This
	 * method is used to save Transition of adulthood below thirteen section.
	 * 
	 * @param transAdulthoodBelowThirteenDto
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes saveTransAdulthoodBelowThirteen(ChildPlanTransAdtltBlwDtlDto transAdulthoodBelowThirteenDto,
			String szUserId);

	/**
	 * Method Name: saveTransAdulthoodAboveFourteen Method Description: This
	 * method is used to save Transition of adulthood above fourteen section.
	 * 
	 * @param transAdulthoodAboveFourteenDto
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes saveTransAdulthoodAboveFourteen(ChildPlanTransAdltAbvDtlDto transAdulthoodAboveFourteenDto,
			String szUserId);

	/**
	 * Method Name: saveHighRiskServices Method Description: This method is used
	 * to save the high risk services section.
	 * 
	 * @param childPlanHighRiskServicesDto
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes saveHighRiskServices(ChildPlanHighRiskServicesDto childPlanHighRiskServicesDto,
			String szUserId);

	/**
	 * Method Name: saveTreatmentService Method Description: This method is used
	 * to save child plan treatment services section.
	 * 
	 * @param childPlanTreatmentServiceDto
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes saveTreatmentService(ChildPlanTreatmentServiceDto childPlanTreatmentServiceDto,
			String szUserId);

	/**
	 * Method Name: saveFmlyTeamPrtctpn Method Description: This method is used
	 * to save child plan family team participation section.
	 * 
	 * @param childPlanFmlyTeamPrtctpnDto
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes saveFmlyTeamPrtctpn(ChildPlanFmlyTeamPrtctpnDto childPlanFmlyTeamPrtctpnDto,
			String szUserId);

	/**
	 * Method Name: createAndReturnEventid. Method Description: This method will
	 * create a new event for new child plan or update the event table for
	 * existing child plan and return the idEvent..
	 * 
	 * @param childPlanDtlReq
	 * @return Long
	 */
	public Long createAndReturnEventid(ChildPlanDtlReq childPlanDtlReq,boolean conservatorshipCreated);

	/**
	 * Method Name: getPrefillReadOnlyInfo Method Description:This Method is
	 * used to fetch all the CPOS prefill read only fields from stored proc
	 * 
	 * @param childPlanOfServiceDto
	 * @return ChildPlanOfServiceDto
	 */
	public ChildPlanOfServiceDto getPrefillReadOnlyInfo(ChildPlanOfServiceDto childPlanOfServiceDto);

	/**
	 * Method Name: getPrefillEditableInfo Method Description: This Method is
	 * used to fetch all the CPOS prefill editable fields from stored proc
	 * 
	 * @param childPlanOfServiceDto
	 * @return ChildPlanOfServiceDto
	 */
	public ChildPlanOfServiceDto getPrefillEditableInfo(ChildPlanOfServiceDto childPlanOfServiceDto);

	/**
	 * Method Name: saveChildPlanDtl Method Description:
	 * 
	 * @param childPlanOfServiceDtlDto
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes saveChildPlanDtl(ChildPlanOfServiceDtlDto childPlanOfServiceDtlDto, String szUserId,
			Boolean externalUser);

	/**
	 * Method Name: getAllChildPlanDetails Method Description:This Method is
	 * used to get all the details of the Child plan
	 * 
	 * @param idChildPlanEvent
	 * @return ChildPlanOfServiceDto
	 */
	public ChildPlanOfServiceDto getAllChildPlanDetails(Long idChildPlanEvent,
			ChildPlanOfServiceDto childPlanOfServiceDto);

	/**
	 * Method Name: getChildPlanDtlInfo Method Description:
	 * 
	 * @param childPlanOfServiceDtlDto
	 * @return ChildPlanOfServiceDtlDto
	 */
	ChildPlanOfServiceDtlDto getChildPlanDtlInfo(Long idChildPlanEvent,
			ChildPlanOfServiceDtlDto childPlanOfServiceDtlDto);

	/**
	 * Method Name: deleteAdoption. Method Description: This method will delete
	 * the selected domestic/International adoption details
	 * 
	 * @param deleteIds
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes deleteAdoption(List<Long> deleteIds);

	/**
	 * Method Name: deleteLegalGuardianship. Method Description: This method
	 * will delete the selected legal guardianship information.
	 * 
	 * @param deleteIds
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes deleteLegalGuardianship(List<Long> deleteIds);

	/**
	 * Method Name: deleteGoals. Method Description: This method will delete the
	 * selected goals details for the coressponding section.
	 * 
	 * @param deleteIds
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes deleteGoals(List<Long> deleteIds);

	/**
	 * Method Name: deletePyscMdctnHc. Method Description: This method will
	 * delete the selected Non-Psychotropic Medication(s) and Psychotropic
	 * Medication(s) details.
	 * 
	 * @param deleteIds
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes deletePyscMdctnHc(List<Long> deleteIds);

	/**
	 * Method Name: deleteAdoption Method Description: This method will delete
	 * the selected family participant details.
	 * 
	 * @param deleteIds
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes deleteCpPartcptnTeam(List<Long> deleteIds);

	/**
	 * Method Name: deleteQrtpPtmParticipants Method Description: This method will delete
	 * the selected QRTP PTM participant details.
	 *
	 * @param deleteIds
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes deleteQrtpPtmParticipants(List<Long> deleteIds);

	/**
	 * Method Name: chckVictimHasExistingCP Method Description: This Method will
	 * check the victim has existing child plan or not.
	 * 
	 * @param idPerson
	 * @return Boolean
	 */
	public Boolean chckVictimHasExistingCP(Long idPerson);

	/**
	 * 
	 * Method Name: getPrePsycnDtls Method Description:This method to get
	 * previous APRV child plans psyc medc details
	 * 
	 * @param idPerson
	 * @return
	 */
	public List<ChidPlanPsychMedctnDtlDto> getPrePsycnDtls(Long idPerson, Long idStage);

	
	
	/**
	 *Method Name:	getPreviousApprovedChildPlanId
	 *Method Description:Retrievs the previous approved child plan event id of P2 child plan
	 *@param idPerson
	 *@param idStage
	 *@return
	 */
	public Long getPreviousApprovedChildPlanId(Long idPerson, Long idStage);
	
	/**
	 *Method Name:	isYouthOwnConsenter
	 *Method Description: Method to check if youth is their own consenter
	 *@param childPlanHealthCareSummaryDto
	 *@return
	 */
	public ChildPlanHealthCareSummaryDto isYouthOwnConsenter(Long idStage,ChildPlanHealthCareSummaryDto childPlanHealthCareSummaryDto);

	/**
	 *Method Name:	deletePriorAdoptions
	 *Method Description: This method is used to deletePriorAdoptions
	 *@param idEvent
	 *@param adoptionType
	 */
	public void deletePriorAdoptions(Long idEvent, String adoptionType);

	/**
	 *Method Name:	deleteLegalGuardians
	 *Method Description: This method is used to deleteLegalGuardians
	 *@param idChildPlanEvent
	 */
	public void deleteLegalGuardians(Long idChildPlanEvent);

	/**
	 *Method Name:	deleteGoals
	 *Method Description: This method is used to deleteGoals
	 *@param idChildPlanEvent
	 *@param topic
	 */
	public void deleteGoals(Long idChildPlanEvent, String topic);

	/**
	 *Method Name:	deleteTreatmentService
	 *Method Description: This method is used to deleteTreatmentService
	 *@param idChildPlanEvent
	 *@param topic
	 */
	public void deleteTreatmentService(Long idChildPlanEvent, String topic);

	//PPM 70054(FCL) - IMPACT Date Requirement for RCYFC Notification
	/**
	 * @param idStage
	 * @return
	 */
	public Date getChildInitialBORDate(Long idStage);

	/**
	 * @param idStage
	 * @return
	 */
	public Date getChildMostRecentBORDate(Long idStage);

    public String getQrtpRecommendations(Long idStage);

    public ServicePackageDtlDto getSvcPkgDetails(Long idCase, Long idStage, String svcPkgType);
}
