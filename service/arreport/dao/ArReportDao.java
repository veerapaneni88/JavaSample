package us.tx.state.dfps.service.arreport.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.arinvconclusion.dto.ArInvCnclsnDto;
import us.tx.state.dfps.arinvconclusion.dto.PCSPDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtAreaValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtFactorValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtValueDto;
import us.tx.state.dfps.service.arreport.dto.ArPrincipalsHistoryDto;
import us.tx.state.dfps.service.arreport.dto.ArRelationshipsDto;
import us.tx.state.dfps.service.arreport.dto.ArServiceReferralsDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityAllegationInfoDto;
import us.tx.state.dfps.service.investigation.dto.AllegationDetailDto;
import us.tx.state.dfps.service.person.dto.CharacteristicsDto;
import us.tx.state.dfps.service.riskassesment.dto.SdmSafetyRiskAssessmentsDto;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * methods that make calls to database Apr 3, 2018- 10:19:34 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ArReportDao {

	/**
	 * Method Name: getContactList Method Description: Retrieves all the
	 * contacts for A-R stage, as well as the mref indicator from the case
	 * Legacy Dao: ARFormsDao.getContactList
	 * 
	 * @param idStage
	 * @param mergedStages
	 * @return List<ContactDto>
	 */
	public List<ContactDto> getContactList(Long idStage, String mergedStages);

	/**
	 * Method Name: getHistoricalPrincipals Method Description:Returns
	 * historical list of principals (principals in other stages). When looking
	 * for 'other INV', we also make sure we Legacy Dao:
	 * ARFormsDao.getHistoricalPrincipals don't pull in the INV immediately
	 * following this A-R
	 * 
	 * @param idStage
	 * @return List<ArPrincipalsHistoryDto>
	 */
	public List<ArPrincipalsHistoryDto> getHistoricalPrincipals(Long idStage);

	/**
	 * Method Name: getArAllegationsByStage Method Description: Gets allegations
	 * for A-R and INV stages for a given person/stage Legacy Dao:
	 * ARFormsDao.getArAllegationsByStage
	 * 
	 * @param idStage
	 * @return List<FacilityAllegationInfoDto>
	 */
	public List<FacilityAllegationInfoDto> getArAllegationsByStage(Long idStage);

	/**
	 * Method Name: getArSafetyAssmtsByStage Method Description: Gets Safety
	 * Assessments for Alternative Response stage Legacy Dao:
	 * ARSafetyAssmtDAO.getArSafetyAssmtsByStage
	 * 
	 * @param idStage
	 * @return List<ARSafetyAssmtValueDto>
	 */
	public List<ARSafetyAssmtValueDto> getArSafetyAssmtsByStage(Long idStage);

	/**
	 * Method Name: getArSafetyFactorsByStage Method Description: Gets Safety
	 * Factors for Alternative Response stage Legacy Dao:
	 * ARSafetyAssmtDAO.getArSafetyFactorsByStage
	 * 
	 * @param idStage
	 * @return List<ARSafetyAssmtFactorValueDto>
	 */
	public List<ARSafetyAssmtFactorValueDto> getArSafetyFactorsByStage(Long idStage);

	/**
	 * Method Name: getArSafetyAssmtAreasAll Method Description: Gets Safety
	 * Areas for Alternative Response stage Legacy Dao:
	 * ARSafetyAssmtDAO.getArSafetyAssmtAreasAll
	 * 
	 * @param idStage
	 * @return List<ARSafetyAssmtAreaValueDto>
	 */
	public List<ARSafetyAssmtAreaValueDto> getArSafetyAssmtAreasAll(Long idStage);

	/**
	 * Method Name: selectServiceReferrals Method Description: Method to
	 * retrieve service referrals for a stage ID or service referral ID. Legacy
	 * Dao: ArServReferralDao.selectServiceReferrals
	 * 
	 * @param idStage
	 * @param idServRef
	 * @return List<ArServiceReferralsDto>
	 */
	public List<ArServiceReferralsDto> selectServiceReferrals(Long idStage, Long idServRef);

	/**
	 * Method Name: getSafetyPlacements Method Description: Gets safety
	 * placement information for given stage for A-R report and CpsInvReport
	 * Legacy Dao: ARFormsDao.getSafetyPlacements
	 * 
	 * @param idStage
	 * @return List<PCSPDto>
	 */
	public List<PCSPDto> getSafetyPlacements(Long idStage);

	/**
	 * Method Name: selectARConclusion Method Description: Gets Alternative
	 * Response conclusion Legacy Dao: ARConclusionDao.selectARConclusion
	 * 
	 * @param idStage
	 * @return ArInvCnclsnDto
	 */
	public ArInvCnclsnDto selectARConclusion(Long idStage);

	/**
	 * Method Name: getClosureApproval Method Description: Gets the stage
	 * closure date from approval so that it has a timestamp portion Legacy Dao:
	 * ARFormsDao.getClosureApproval
	 * 
	 * @param idEvent
	 * @return Date
	 */
	public Date getClosureApproval(Long idEvent);

	/**
	 * Method Name: getRelationships Method Description: Returns a list of
	 * relationships Legacy Dao: ARFormsDao.getRelationships
	 * 
	 * @param idStage
	 * @param dtMaxComp
	 * @return List<ArRelationshipsDto>
	 */
	public List<ArRelationshipsDto> getRelationships(Long idStage, Date dtMaxComp);

	/**
	 * Method Name: getMrefStatus Method Description: Returns the String mref
	 * status, used by A-R report Legacy Dao: ARFormsDao.getMrefStatus
	 * 
	 * @param idStage
	 * @return String
	 */
	public String getMrefStatus(Long idStage);

	/**
	 * Method Name: getStageMergeInfo Method Description: Returns the INT prior
	 * stage, used by A-R report Legacy Dao: ARFormsDao.getPriorIntStage
	 * 
	 * @param idStage
	 * @return Long
	 */
	public Long getStageMergeInfo(Long idStage);

	/**
	 * Method Name: getIntakeAllegations Method Description: Retrieves the
	 * intake allegations for given stage ID Legacy Dao:
	 * ARFormsDao.getIntakeAllegations
	 * 
	 * @param idStage
	 * @return List<AllegationDetailDto>
	 */
	public List<AllegationDetailDto> getIntakeAllegations(Long idStage);

	/**
	 * Method Name: getPrnCharacteristicsByStage Method Description: This
	 * returns the prn characteristics for the input stage Legacy Dao:
	 * CharacteristicsDao.getPrnCharacteristicsByStage
	 * 
	 * @param idStage
	 *            List<CharacteristicsDto>
	 * @return
	 */
	public List<CharacteristicsDto> getPrnCharacteristicsByStage(Long idStage);

	/**
	 * Method Name: getPlanCompletionDate Method Description: Based on the plan
	 * type parameter, this method retrieves initial safety plan completion date
	 * or initial family plan completion date from contacts Legacy Dao:
	 * ARServReferralDao.getPlanCompletionDate
	 * 
	 * @param idStage
	 * @param planType
	 * @return
	 */
	public Date getPlanCompletionDate(Long idStage, String planType);

	/**
	 * Method Name: getPriorStage Method Description: Returns details for the
	 * stage prior to the given stage as indicated by the STAGE_LINK table
	 * Legacy Dao: CaseUtility.getPriorStage
	 * 
	 * @param idStage
	 * @return StageDto
	 */
	public StageDto getPriorStage(Long idStage);

	/**
	 * Method Name: getARSafetyAssmt Method Description:This method is called
	 * from display method in SafetyAssmtConversation if the page has been
	 * previously saved. It retrieves back all the responses
	 * 
	 * @param idStage
	 * @param cdAssmtType
	 * @param idUser
	 * @return ARSafetyAssmtValueDto
	 * @throws DataNotFoundException
	 */
	public ARSafetyAssmtValueDto getARSafetyAssmt(Integer idStage, String cdAssmtType, Integer idUser)
			throws DataNotFoundException;

	/**
	 * Method Name: getSdmSafetyRiskAssessments Method Description: This
	 * returns the CPS SA and CPS RA details for AR Report Form for the input stage 	 * 
	 * @param idStage
	 *            List<SdmSafetyRiskAssessmentsDto>
	 * @return
	 */
	List<SdmSafetyRiskAssessmentsDto> getSdmSafetyRiskAssessments(Long idStage);
}
