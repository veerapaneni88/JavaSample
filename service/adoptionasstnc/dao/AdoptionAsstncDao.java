package us.tx.state.dfps.service.adoptionasstnc.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.adoptionasstnc.AdoptionAsstncDto;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dto.EventDto;

public interface AdoptionAsstncDao {

	/**
	 * Method Name: fetchActiveAdpForPerson Method Description:Fetches the
	 * Active adoption assistance record for the given person id if one exists.
	 * 
	 * @param idPerson
	 * @return List<AdoptionAsstncDto>
	 */
	public List<AdoptionAsstncDto> fetchActiveAdpForPerson(Long idPerson);

	/**
	 * Method Name: endDateAdoptionSubsidy Method Description:This method set
	 * the end date on adoption subsidy record
	 * 
	 * @param adoptionAsstncDto
	 * @param dtSubsidyEnd
	 * @param cdSubsidyEndReason
	 * @return int
	 */
	public int endDateAdoptionSubsidy(AdoptionAsstncDto adoptionAsstncDto, Date dtSubsidyEnd,
			String cdSubsidyEndReason);

	/**
	 * Method Name: getAdptSubsidyEventList Method Description:This method
	 * fetches the list of events associated with a adoption_subsidy record
	 * 
	 * @param idAdptSub
	 * @return List<EventDto>
	 */
	public List<EventDto> getAdptSubsidyEventList(int idAdptSub);

	/**
	 * Method Name: queryEarliestAdoptionAsstncRecord Method Description:Queries
	 * the earliest adoption assistance record for the given person id and
	 * resource id combination, if one exists.
	 * 
	 * @param personId
	 * @param resourceId
	 * @return AdoptionAsstncDto
	 */
	public AdoptionAsstncDto queryEarliestAdoptionAsstncRecord(Long personId, Long resourceId);

	/**
	 * Method Name: queryAlocWithGreatestStartDate Method Description:Queries
	 * the child's ALOC with the greatest start date, whether active or not.
	 * 
	 * @param personId
	 * @return String
	 */
	public String queryAlocWithGreatestStartDate(Long personId);

	/**
	 * Method Name: queryPlacementWithGreatestStartDate Method
	 * Description:Queries the child's Placement with the greatest start date
	 * 
	 * @param personId
	 * @param resourceId
	 * @return Date
	 */
	public PlacementDto queryPlacementWithGreatestStartDate(Long personId, Long resourceId);

	/**
	 * Method Name: adoptionPlacementDate Method Description:Queries the child's
	 * most recent ADO Placement start date(for the person).
	 * 
	 * @param personId
	 * @return Date
	 */
	public Date adoptionPlacementDate(Long personId);

	/**
	 * Method Name: fetchLatestOpenAdptAsstncRecord Method Description: Fetches
	 * the latest open in PROC state - adoption assistance record for the given
	 * person id if one exists.
	 * 
	 * @param idPerson
	 * @return Long
	 */
	public Long fetchLatestOpenAdptAsstncRecord(Long idPerson);

	/**
	 * Method Name: fetchAdptAsstncRecord Method Description: Fetches the
	 * adoption assistance record for the given idAdptSub
	 * 
	 * @param idAdptSub
	 * @return AdoptionAsstncDto
	 */
	public AdoptionAsstncDto fetchAdptAsstncRecord(Long idAdptSub);

	/**
	 * Method Name: fetchAllAdptAsstncRecord Method Description: Fetches the all
	 * the adoption assistance record for the given person id if one exists.
	 * 
	 * @param personId
	 * @return List<AdoptionAsstncDto>
	 */
	public List<AdoptionAsstncDto> fetchAllAdptAsstncRecord(Long personId);

	/**
	 * Method Name: isAdptAsstncCreatedPostAugRollout Method Description:Returns
	 * true if the Adoption Assistance Record was created after august rollout.
	 * 
	 * @param idAdptSub
	 * @return Boolean
	 */
	public Boolean isAdptAsstncCreatedPostAugRollout(Long idAdptSub);

	/**
	 * Method Name: getAdoptAssistForRsrcAndChild Method Description:Fetches the
	 * list of AdoptionAsstncValueBean with the for the Resource and Child
	 * Combination if one exists.
	 * 
	 * @param personId
	 * @param idResource
	 * @return List<AdoptionAsstncDto>
	 */
	public List<AdoptionAsstncDto> getAdoptAssistForRsrcAndChild(Long personId, Long idResource);

	/**
	 * Method Name: getAlocOnAdptAssistAgrmntSignDt Method Description:Retrieves
	 * the Authorized Level of Care (ALOC) on the day when Adoption Assist
	 * Agreement was Signed.
	 * 
	 * @param personId
	 * @param dtAdptAsstAgreement
	 * @return String
	 */
	public String getAlocOnAdptAssistAgrmntSignDt(Long personId, Date dtAdptAsstAgreement);

	/**
	 * Method Name: getAdoptAssistForStage Method Description:Fetches the list
	 * of adoption subsidy events that are in PROC status
	 * 
	 * @param idStage
	 * @return List<Long>
	 */
	public List<Long> getAdoptAssistForStage(Long idStage);

	/**
	 * Method Name: getAdoptAssistRsnClosure Method Description:Fetches the
	 * closure reason for the adoption subsidy for that particular event Id
	 * 
	 * @param idEvent
	 * @return AdoptionAsstncDto
	 */
	public AdoptionAsstncDto getAdoptAssistRsnClosure(Long idEvent);

	/**
	 * Method Name: getAdptPlcmtInfo Method Description: Retrieves the Adoptive
	 * Placement informations.
	 * 
	 * @param idAdptSub
	 * @return AdoptionAsstncDto
	 */
	public AdoptionAsstncDto getAdptPlcmtInfo(Long idAdptSub);

	/**
	 * Method Name: updateAdptSubsidy Method Description:This method updates
	 * ADOPTION_SUBSIDY table using ApplicationBackgroundValueService.
	 * 
	 * @param adptAsstncValueBeanDto
	 * @return Long
	 */
	public Long updateAdptSubsidy(AdoptionAsstncDto adptAsstncValueBeanDto);

	/**
	 * Method Name: findEligibilityOrPrimayWorkerForStage Method
	 * Description:Retrives the Eligibility Or PrimayWorker
	 * 
	 * @param stageId
	 * @return Long @
	 */
	public Long findEligibilityOrPrimayWorkerForStage(Long stageId);

	public List<PlacementDto> fetchADOPlacements(Long idStageADOForPlacements);

	public Boolean getAdoProcessStatus(Long idStage);
}
