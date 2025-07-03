package us.tx.state.dfps.service.placement.service;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.EventIdOutDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtOutDto;
import us.tx.state.dfps.service.common.request.CommonAppShortFormReq;
import us.tx.state.dfps.service.common.response.CommonAppShortFormRes;
import us.tx.state.dfps.service.conservatorship.dto.CnsrvtrshpRemovalDto;
import us.tx.state.dfps.service.fce.dto.FceApplicationDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.pca.dto.PlacementDtlDto;
import us.tx.state.dfps.service.person.dto.ServicePackageDtlDto;
import us.tx.state.dfps.service.person.dto.TraffickingDto;
import us.tx.state.dfps.service.person.dto.UnitDto;
import us.tx.state.dfps.service.placement.dto.CommonApplicationShortFormDto;
import us.tx.state.dfps.service.placement.dto.PersonLocDto;
import us.tx.state.dfps.service.placement.dto.ShortFormCsaEpisodeIncdntsDto;
import us.tx.state.dfps.service.placement.dto.ShortFormEducationSrvDto;
import us.tx.state.dfps.service.placement.dto.ShortFormMedicationDto;
import us.tx.state.dfps.service.placement.dto.ShortFormPsychiatricDto;
import us.tx.state.dfps.service.placement.dto.ShortFormRtnRunawayDto;
import us.tx.state.dfps.service.placement.dto.ShortFormSiblingsDto;
import us.tx.state.dfps.service.placement.dto.ShortFormSpecialProgrammingDto;
import us.tx.state.dfps.service.placement.dto.ShortFormTherapyDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimHistoryDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimIncidentDto;
import us.tx.state.dfps.web.placement.bean.ShortFormSubstanceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CommonApplicationController will have all operation which are
 * mapped to Placement module. Feb 9, 2018- 2:13:50 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface CommonAppShortFormService {

	public StagePersonLinkCaseDto retrieveHeaderInfo(Long stageId);

	public EmployeePersPhNameDto getSupervisorInfo(Long stageId);

	public UnitDto getUnitInfo(Long unitId);

	public EmployeePersPhNameDto geCaseworkerInfo(Long stageId);

	public PersonDto getChildInfo(Long personId);

	public FceEligibilityDto getCitizenInfo(Long personId);

	public String getEthnicityInfo(Long personId);

	public String getRaceInfo(Long personId);

	public CommonApplicationShortFormDto getCommonAppShortFormById(Long caExCommonApplicationId);

	public CommonApplicationShortFormDto getCommonAppShortFormByEventId(Long eventId);

	public CommonAppShortFormRes saveShortForm(CommonAppShortFormReq commonAppShortFormReq);
	
	public List<LegalStatusPersonMaxStatusDtOutDto> getLegalCounty(Long personId);

	public FceApplicationDto getRemovalAddr(Long personId);

	public EventIdOutDto getEventDetails(Long eventId);

	public PersonLocDto retrieveLevelOfCare(Long personId);

	public ShortFormSpecialProgrammingDto getSplProgByCaExId(Long idCaEx);

	public CommonAppShortFormRes approveShortForm(CommonApplicationShortFormDto commonApplicationShortFormDto, Long approverId);

	public CommonAppShortFormRes rejectShortForm(CommonApplicationShortFormDto commonApplicationShortFormDto,
			Long approverId, String rejectReason);

	public ShortFormRtnRunawayDto getRtnRunawayByCaExId(Long idCaEx);

	public ShortFormEducationSrvDto getEduSrvByCaExId(Long idCaEx);

	public List<PlacementDtlDto> getPlacementLog(Long personId);

	public List<ShortFormSiblingsDto> getSiblingsList(Long stageId, CommonApplicationShortFormDto shortFormDto);

	public List<ShortFormSiblingsDto> getSiblingsByCaExId(Long idCaEx);

	public List<PlacementDtlDto> getPlacementLogByExId(Long caExCommonApplicationId);

	public List<ShortFormMedicationDto> getMedicationByCaExId(Long idCaEx);

	public List<ShortFormTherapyDto> getTherapyDtlByCaExId(Long idCaEx);

	public ShortFormSubstanceDto getSubstanceAbuseByExId(Long caExCommonApplicationId);

	public List<ShortFormPsychiatricDto> getHospitalizationByCaExId(Long idCaEx);

	public void createEventForNotification(CommonAppShortFormReq commonAppShortFormReq);

	public SexualVictimHistoryDto getSexualVictimization(Long personId);

	public List<TraffickingDto> getTrafficking(Long personId);

	List<ShortFormCsaEpisodeIncdntsDto> getCSAEpisodeIncdntDtls(Long personId);

	CnsrvtrshpRemovalDto getRemovalDate(Long personId);

	List<SexualVictimIncidentDto> getSFSexualVictimization(Long idCaEx);

	List<TraffickingDto> getSFTraffickingHistory(Long idCaEx);

	List<ShortFormCsaEpisodeIncdntsDto> getSFEpisodeIncidents(Long idCaEx);
	

	/**
	 * Method Description: This method is used to retrieve the common application
	 * short form. This form fully documents the Child Information, Trauma
	 * and Trafficking History, Health Care Summary, Substance Abuse,Risk and
	 * sexualized behavior,Education,Family and Placement history emotional,
	 * Transition planning for a Adulthood,Juvenile Justice Involvement account
	 * of the child by passing IdEvent as input request
	 */
	public PreFillDataServiceDto getCommonApplShortForm(CommonAppShortFormReq commonAppShortFormReq);

	public ServicePackageDtlDto retrieveRecommendedServicePackage(Long stageId);
}
