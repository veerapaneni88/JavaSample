package us.tx.state.dfps.service.adoptionasstnc.service;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.adoptionasstnc.AdoptionAsstncDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for AdoptionAsstncService Oct 30, 2017- 2:02:45 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface AdoptionAsstncService {

	/**
	 * Method Name: getNonRecurringAdoptionAsstncCeiling Method
	 * Description:Determines the maximum one-time payment amount for a
	 * non-recurring adoption assistance payment.
	 * 
	 * @return Double
	 */
	public Double getNonRecurringAdoptionAsstncCeiling();

	/**
	 * Method Name: getRecurringAdoptionAsstncCeiling Method Description:
	 * Determines the monthly adoption assistance ceiling using the person id of
	 * the child being placed into adoption and the "effective" adoption
	 * assistance start date, which is either the start date of the adoption
	 * assistance record being added/updated or the start date of the earliest
	 * adoption assistance record for the person/resource combination, if others
	 * exist.
	 * 
	 * @param adoptionAsstncDto
	 * @return Double
	 */
	public Double getRecurringAdoptionAsstncCeiling(AdoptionAsstncDto adoptionAsstncDto);

	/**
	 * Method Name: getValidationErrors Method Description:Validates the
	 * specified adoption assistance amount based upon the adoption assistance
	 * type and the "effective" adoption assistance start date, which is either
	 * the start date of the adoption assistance record being added/updated or
	 * the start date of the earliest adoption assistance record for the
	 * person/resource combination, if others exist.
	 * 
	 * @param adoptionAsstncDto
	 * @return String
	 */
	public String getValidationErrors(AdoptionAsstncDto adoptionAsstncDto);

	/**
	 * Method Name: getAlocWithGreatestStartDate Method Description:Retrieves
	 * the Authorized Level of Care (ALOC) record with the greatest start date
	 * for the given person id.
	 * 
	 * @param personId
	 * @return String
	 */
	public String getAlocWithGreatestStartDate(Long personId);

	/**
	 * Method Name: getPlacementWithGreatestStartDate Method
	 * Description:Retrieves the Placement record with the greatest start date
	 * for the given person id.
	 * 
	 * @param personId
	 * @param resourceId
	 * @return Date
	 */
	public String getPlacementWithGreatestStartDate(Long personId, Long resourceId);

	/**
	 * Method Name: adoptionPlacementDate Method Description:Retrieves the most
	 * recent ADO Placement start date for the given person id
	 * 
	 * @param personId
	 * @return Date
	 */
	public Date adoptionPlacementDate(Long personId);

	/**
	 * Method Name: fetchLatestAdptAsstncRecord Method Description:Retrieves the
	 * Adoption Subsidy details
	 * 
	 * @param idPerson
	 * @return Long
	 */
	public Long fetchLatestAdptAsstncRecord(Long idPerson);

	/**
	 * Method Name: fetchAdptAsstncDetail Method Description:Retrieves the
	 * Adoption Subsidy details
	 * 
	 * @param idAdptSub
	 * @return AdoptionAsstncDto
	 */
	public AdoptionAsstncDto fetchAdptAsstncDetail(Long idAdptSub);

	/**
	 * Method Name: fetchAllAdptAsstncRecord Method Description:Fetches the all
	 * the adoption assistance record for the given person id if one exists.
	 * 
	 * @param personId
	 * @return List<AdoptionAsstncDto>
	 */
	public List<AdoptionAsstncDto> fetchAllAdptAsstncRecord(Long personId);

	/**
	 * Method Name: isAdptAsstncCreatedPostAugRollout Method
	 * Description:Determines if the the Adoption Subsidy is created Pre/Post
	 * Aug 22 2010 rollout.
	 * 
	 * @param idAdptSub
	 * @return Boolean
	 */
	public Boolean isAdptAsstncCreatedPostAugRollout(Long idAdptSub);

	/**
	 * Method Name: getRecentAdoptPlcmStartDate Method Description:Fetches the
	 * Adoptive Placement Start date for Resource and Child Combination if one
	 * exists.
	 * 
	 * @param personId
	 * @param idResource
	 * @return Date
	 */
	public Date getRecentAdoptPlcmStartDate(Long personId, Long idResource);

	/**
	 * Method Name: findEligibilityOrPrimayWorkerForStage Method
	 * Description:This function return if Eligibility Specialist that is
	 * assigned to stage. If None assigned returns Primary worker for Stage.
	 * 
	 * @param idStage
	 * @return Long
	 */
	public Long findEligibilityOrPrimayWorkerForStage(Long idStage);

	/**
	 * Method Name: getAlocOnAdptAssistAgrmntSignDt Method Description:Retrieves
	 * the Authorized Level of Care (ALOC) on the day when Adoption Assist
	 * Agreement was Signed
	 * 
	 * @param personId
	 * @param dtAdptAsstAgreement
	 * @return String
	 */
	public String getAlocOnAdptAssistAgrmntSignDt(Long personId, Date dtAdptAsstAgreement);

	/**
	 * Method Name: isAdptSubsidyCreatedOnAfterAppl Method Description:Returns
	 * true if the Subsidy was created on or after the Adopt Assistance
	 * Application was created
	 * 
	 * @param idEventSubsidy
	 * @param idEventApplication
	 * @return Boolean
	 */
	public Boolean isAdptSubsidyCreatedOnAfterAppl(Long idEventSubsidy, Long idEventApplication);

	/**
	 * Method Name: isAdptSubsidyEnded Method Description:Returns false if any
	 * of the adoption subsidies(for that stage) is not ended.
	 * 
	 * @param idStage
	 * @return Boolean
	 */
	public Boolean isAdptSubsidyEnded(Long idStage);

	/**
	 * Method Name: getAdptPlcmtInfo Method Description:Retrieves the Adoptive
	 * Placement informations
	 * 
	 * @param idAdptSub
	 * @return AdoptionAsstncDto
	 */
	public AdoptionAsstncDto getAdptPlcmtInfo(Long idAdptSub);

	/**
	 * Method Name: fetchActiveAdpList Method Description:Get Person Active ADP
	 * Eligibilities for input person id
	 * 
	 * @param idPerson
	 * @return List<AdoptionAsstncDto>
	 */
	public List<AdoptionAsstncDto> fetchActiveAdpList(Long idPerson);

}
