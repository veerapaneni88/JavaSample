package us.tx.state.dfps.service.cpsinvreport.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.arinvconclusion.dto.ArEaEligibilityDto;
import us.tx.state.dfps.common.dto.CpsChecklistDto;
import us.tx.state.dfps.service.contact.dto.ContactNarrGuideDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsChecklistItemDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvAllegDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvComDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvContactSdmSafetyAssessDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvCrimHistDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvIntakePersonPrincipalDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvPrincipalDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvReportIntakeDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvReportMergedDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvRiskDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvSdmSafetyRiskDto;
import us.tx.state.dfps.service.cpsinv.dto.ServRefDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: all dao
 * method declarations for CPSInvReport, these methods are specially converted
 * for CPSInvReport, different from the original DAM queries. Apr 4, 2018-
 * 10:54:22 AM © 2017 Texas Department of Family and Protective Services
 *  * **********  Change History *********************************
 * 07/13/2020 thompswa artf159096 : idHouseholdEvent needed for emergency assist
 * 01/31/2023 thompswa artf238090 PPM 73576 add idVictim,idAllegPrep to getAllegationsSafe.
 */
public interface CpsInvReportDao {

    /**
     * Method Name: getRemovals Method Description: convert the clsce2d/clsce3d
     * to get removals.
     * 
     * @param idStage
     * @return List<CpsInvComDto>
     */
    public List<CpsInvComDto> getRemovals(Long idStage);

    /**
     * Method Name: getContactNames Method Description: convert the clscded to
     * get Contact Names.
     * 
     * @param stageString
     * @return List<CpsInvComDto>
     */
    public List<CpsInvComDto> getContactNames(String stageString);

    /**
     * Method Name: getPrincipals Method Description: convert the clsce1d to get
     * stage principals.
     * 
     * @param idStage
     * @return List<CpsInvPrincipalDto>
     */
    public List<CpsInvPrincipalDto> getPrincipals(Long idStage);

    /**
     * Method Name: getCriminalHistory Method Description: convert the clsce0d
     * to get principals criminal history
     * 
     * @param idStage
     * @return List<CpsInvCrimHistDto>
     */
    public List<CpsInvCrimHistDto> getCriminalHistory(Long idStage);

    /**
     * Method Name: getPriorIntStages Method Description: convert clscd2d to get
     * prior INT stage of INV(or of A-R if A-R is prior)
     * 
     * @param idStage
     * @return Long
     */
    public Long getPriorIntStage(Long idStage);

    /**
     * Method Name: getMergeHistory Method Description: for CPSInvReport
     * 
     * @param idCase
     * @return List<Long>
     */
    public List<Long> getMergeHistory(Long idCase);

    /**
     * Method Name: getMref Method Description: From DAM for csece2d
     * 
     * @param idStage
     * @return String
     */
    public String getMref(Long idStage);

    /**
     * Method Name: getValidSdmRa Method Description: convert the csecfad to get
     * sdm risk assmt
     * 
     * @param idStage
     * @return String
     */
    public String getValidSdmRa(Long idStage);

    /**
     * Method Name: getRiskAssessment Method Description: convert the csesb6d to
     * get risk_assessment.
     * 
     * @param idStage
     * @return List<CpsInvRiskDto>
     */
    public List<CpsInvRiskDto> getRiskAssessment(Long idStage);

    /**
     * Method Name: getSafetyAssessment Method Description: convert the csesb7d
     * to get safety_assessement.
     * 
     * @param idStage
     * @return List<CpsInvSdmSafetyRiskDto>
     */
    public List<CpsInvSdmSafetyRiskDto> getSafetyAssessment(Long idStage);

    /**
     * Method Name: getRiskArea Method Description: convert the clsce5d to get
     * RISK_AREA text for a given idStage.
     * 
     * @param idStage
     * @return List<CpsInvSdmSafetyRiskDto>
     */
    public List<CpsInvSdmSafetyRiskDto> getRiskArea(Long idStage);

    /**
     * Method Name: getRiskFactors Method Description: convert the clscd9d to
     * get RISK_FACTORS text for a given idStage.
     * 
     * @param idStage
     * @return List<CpsInvSdmSafetyRiskDto>
     */
    public List<CpsInvSdmSafetyRiskDto> getRiskFactors(Long idStage);

    /**
     * Method Name: getsafetyFactors Method Description: convert the clscd8d to
     * get SAFETY_AREA text for a given idStage.
     * 
     * @param idStage
     * @return List<CpsInvSdmSafetyRiskDto>
     */
    public List<CpsInvSdmSafetyRiskDto> getSafetyFactors(Long idStage);

    /**
     * Method Name: getsafetyFactors Method Description: From DAM CLSCDDD FOR
     * SDM Safety assessment questions-answers by stageId
     * 
     * @param String
     *            stageString
     * @return List<CpsInvSdmSafetyRiskDto>
     */
    public List<CpsInvSdmSafetyRiskDto> getSdmQa(String stageString);

    /**
     * Method Name: getPersonSplInfo Method Description: Retrieves person name,
     * and information from the stage_person_link record. Simple replacement for
     * cint66d. role 'R' is reporter, 'C' is collateral
     * 
     * @param String
     *            role, Long idStage
     * @return List<CpsInvIntakePersonPrincipalDto>
     */
    public List<CpsInvIntakePersonPrincipalDto> getPersonSplInfo(String role, Long idStage);

    /**
     * Method Name: getPrincipalsHistory Method Description: convert clscd7d
     * 
     * @param Long
     *            idStage
     * @return List<CpsInvIntakePersonPrincipalDto>
     */
    public List<CpsInvIntakePersonPrincipalDto> getPrincipalsHistory(Long idStage);

    /**
     * Method Name: getIntakes Method Description: Wrapped the clscgad with
     * clsc29d to get intakes for the merged inv stage list from cmsc0ad.
     * 
     * @param String
     *            stageString
     * @return List<CpsInvIntakePersonPrincipalDto>
     */
    public List<CpsInvIntakePersonPrincipalDto> getIntakes(String stageString);

    public CpsInvContactSdmSafetyAssessDto getInrContactFields(Long idEvent);

    /**
     * Method Name: getSdmSafetyAssessments Method Description:convert the
     * clssbad to get Sdm Safety Assessments.
     * 
     * @param Long
     *            idStage
     * @return List<CpsInvContactSdmSafetyAssessDto>
     */
    public List<CpsInvContactSdmSafetyAssessDto> getSdmSafetyAssessments(Long idStage);

    /**
     * Method Name: getAllegations Method Description:convert the cses90d to get
     * allegations.
     * 
     * @param Long
     *            idStage
     * @return List<CpsInvAllegDto>
     */
    public List<CpsInvAllegDto> getAllegations(Long idStage);

    /**
     * Method Name: getAllegations Method Description:convert the cses90d to get
     * allegations. artf238090, artf113751 To fix problem of multiple rows output from person merge view
     *
     * @param Long idStage
     * @param b
     * @return List<CpsInvAllegDto>
     */
    public List<CpsInvAllegDto> getAllegationsSafe(Long idStage, boolean b);

    /**
     * Method Name: getPrnInvAllegations Method Description:convert the clss97d
     * to get investigation principals allegations.
     * 
     * @param Long
     *            idStage
     * @return List<CpsInvAllegDto>
     */
    public List<CpsInvAllegDto> getPrnInvAllegations(Long idStage);
    
    /**
     *Method Name:	getSdmSafetyRiskAssessments
     *Method Description: This returns the sdm safety assessments for the input stage
     *@param idStage
     *@return List<CpsInvContactSdmSafetyAssessDto>
     */
    public List<CpsInvContactSdmSafetyAssessDto> getSdmSafetyRiskAssessments(Long idStage);
    
    /**
     *Method Name:	getChecklistItems
     *Method Description: Gets a list of checklist items for a given event (DAM: CLSS81D)
     *@param idEvent
     *@return List<CpsChecklistItemDto>
     */
    public List<CpsChecklistItemDto> getChecklistItems(Long idEvent);
    
    /**
     *Method Name:	getCpsChecklist
     *Method Description: DAM: CSESA2D converts from entity to DTO
     *@param idEvent
     *@return
     */
    public CpsChecklistDto getCpsChecklist(Long idEvent);
    
    /**
     *Method Name:	getServicesReferrals
     *Method Description: Gathers data for services and referrals
     *@param idStage
     *@param idEvent
     *@param cdStage
     *@return ServRefDto
     */
    public ServRefDto getServicesReferrals(Long idStage, Long idEvent, String cdStage);
    
    /**
     *Method Name:	getEventIdByStageAndEventType
     *Method Description: Uses CaseUtils method and returns id of resulting event
     *@param idStage
     *@param cdEventType
     *@return Long
     */
    public Long getEventIdByStageAndEventType(Long idStage, String cdEventType);
    
    /**
     *Method Name:	getEmergencyAssistance
     *Method Description: Gets EA info based off of stage
     *@param idStage
     *@param cdStage
     *@return List<ArEaEligibilityDto>
     */
    public List<ArEaEligibilityDto> getEmergencyAssistance(Long idStage, String cdStage);
    
    /**
     *Method Name:	getPriorArStages
     *Method Description: Returns prior stages for inv merge stages
     *@param mergedStages
     *@return List<Long>
     */
    public List<Long> getPriorArStages(String mergedStages);
    
    /**
     * 
     *Method Name:	getMergedStages
     *Method Description:
     *@param stageDto
     *@return CpsInvReportMergedDto
     */
    public CpsInvReportMergedDto getMergedStages(StageDto stageDto);
    
    /**
     * Add in the intake for the selected stage to the merged stage string.
     * Then add in any intakes for prior ar stages then add intakes from 
     * stages closed to merge to the selected stage.
     */
    public CpsInvReportMergedDto getAllIntStages( CpsInvReportMergedDto arStagesVb, StageDto caseInfo, List<CpsInvReportIntakeDto> intakes);
    
    /**
     * 
     *Method Name:	getContactGuideList
     *Method Description:
     *@param caseInfo
     *@param dtSampleFrom
     *@param dtSampleTo
     *@return List<ContactNarrGuideDto>
     */
    public List<ContactNarrGuideDto> getContactGuideList(StageDto caseInfo, Date dtSampleFrom,Date dtSampleTo);
    
    /**
     * 
     *Method Name:	getLogContactNames
     *Method Description:
     *@param cpsInvReportMergedDtoList
     *@return
     */
    public List<CpsInvComDto> getLogContactNames(List<CpsInvReportMergedDto>  cpsInvReportMergedDtoList);
    
    /**
     * 
     *Method Name:	getArStages
     *Method Description:
     *@param mergedStages
     *@param idCase
     *@return
     */
    public CpsInvReportMergedDto getArStages(String mergedStages,Long idCase);
    
    /**
     * 
     *Method Name:	getContacts
     *Method Description:Retrieves all the contacts for a stage, closed to merge stages, prior stages
     * For A-R Report also.
     *@param allStages
     *@return List<CpsInvContactSdmSafetyAssessDto>
     */
    public List<CpsInvContactSdmSafetyAssessDto> getContacts(List<CpsInvReportMergedDto> allStages);



}
