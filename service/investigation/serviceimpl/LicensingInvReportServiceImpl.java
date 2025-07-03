package us.tx.state.dfps.service.investigation.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.common.request.PreDisplayPriorityClosureReq;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.LicensingInvReportPrefillData;
import us.tx.state.dfps.service.investigation.dao.LicensingInvReportDao;
import us.tx.state.dfps.service.investigation.dto.LicensingInvReportDto;
import us.tx.state.dfps.service.investigation.service.LicensingInvReportService;
import us.tx.state.dfps.service.person.dto.AllegationWithVicDto;
import us.tx.state.dfps.service.person.dto.PersonListAlleDto;
import us.tx.state.dfps.service.populateform.dao.PopulateFormDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PriorityClosureLicensingDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;
import us.tx.state.dfps.notiftolawenforcement.dto.PriorStageDto;
import us.tx.state.dfps.service.cpsintakereport.dao.CpsIntakeReportDao;
import us.tx.state.dfps.service.investigation.dao.LicensingInvstSumDao;
import us.tx.state.dfps.service.investigation.dto.LicensingInvstDtlDto;
import us.tx.state.dfps.service.notiftolawenforce.dao.NotifToLawEnforcementDao;
import us.tx.state.dfps.service.workload.service.WorkloadService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Apr 13, 2018- 2:29:21 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class LicensingInvReportServiceImpl implements LicensingInvReportService {

	@Autowired
	DisasterPlanDao disasterPlanDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	EmployeeDao employeeDao;

	@Autowired
	LicensingInvReportDao licensingInvReportDao;

	@Autowired
	PopulateFormDao populateFormDao;

	@Autowired
	LicensingInvReportPrefillData licensingInvReportPrefillData;

	@Autowired
	NotifToLawEnforcementDao notifToLawEnforcementDao;

	@Autowired
	private CpsIntakeReportDao cpsIntakeReportDao;

	@Autowired
	LicensingInvstSumDao licensingInvstSumDao;

	@Autowired
	WorkloadService workLoadService;

	/**
	 * Method Name: getLicensingInvReportDtls Method Description: Gathers data
	 * to generate prefill string
	 * 
	 * @param req
	 * @return PreFillDataServiceDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getLicensingInvReportDtls(CommonApplicationReq commonApplicationReq) {
		Long idCaseworker = ServiceConstants.ZERO_VAL;
		LicensingInvReportDto prefillDto = new LicensingInvReportDto();

		// retrieve licensing investigation conclusion details based on stage id.
		LicensingInvstDtlDto licensingInvstDtlDto = licensingInvstSumDao
				.getLicensingInvstDtlDaobyParentId(commonApplicationReq.getIdStage());

		if (ObjectUtils.isEmpty(licensingInvstDtlDto.getNbrAcclaim())) {
			PriorStageDto priorStageDto = notifToLawEnforcementDao.getPriorStagebyId(commonApplicationReq.getIdStage());
			PreDisplayPriorityClosureReq priorityClosureReq = new PreDisplayPriorityClosureReq();
			priorityClosureReq.setIdStage(priorStageDto.getIdPriorStage());
			PriorityClosureLicensingDto priorityClosureLicensingDto = workLoadService.getLicensingInformation(priorityClosureReq, licensingInvstDtlDto.getIdCase());
			if (null != priorityClosureLicensingDto) {
				licensingInvstDtlDto.setIdResource(priorityClosureLicensingDto.getIdResource());
				licensingInvstDtlDto.setNmResource(priorityClosureLicensingDto.getNmResource());
				licensingInvstDtlDto.setNbrAcclaim(priorityClosureLicensingDto.getNbrAcclaim());
				licensingInvstDtlDto.setNbrAgency(priorityClosureLicensingDto.getNbrAgency());
				licensingInvstDtlDto.setNbrBranch(priorityClosureLicensingDto.getNbrBranch());
				licensingInvstDtlDto.setCdRsrcFacilType(priorityClosureLicensingDto.getCdRsrcFacilType());
				licensingInvstDtlDto.setClassFacilType(priorityClosureLicensingDto.getClassFacilType());
			}
		}

		prefillDto.setLicensingInvstDtlDto(licensingInvstDtlDto);

		// CLSC89D
		List<PersonListAlleDto> personListAlleDtoList = licensingInvReportDao
				.getPersonAllegationHistDtls(commonApplicationReq.getIdStage());
		List<PersonListAlleDto> updatedPersonListAlleDtoList = new ArrayList<PersonListAlleDto>();
		for (PersonListAlleDto personListAlleDto : personListAlleDtoList) {
			if (ObjectUtils.isEmpty(personListAlleDto.getNmPersonFirst())
					&& ObjectUtils.isEmpty(personListAlleDto.getNmPersonLast())
					&& ObjectUtils.isEmpty(personListAlleDto.getNmPersonMiddle())
					&& !ObjectUtils.isEmpty(personListAlleDto.getPersonFull())) {
				personListAlleDto.setNmPersonFirst(personListAlleDto.getPersonFull()
						.replaceAll(ServiceConstants.SPACE_SYMBOL, ServiceConstants.EMPTY_STRING));
				updatedPersonListAlleDtoList.add(personListAlleDto);
			} else {
				updatedPersonListAlleDtoList.add(personListAlleDto);
			}
		}
		prefillDto.setPersonListAlleDtoList(personListAlleDtoList);
		// CLSC90D
		List<PersonListAlleDto> personAllVictimAllegationList = licensingInvReportDao
				.getAllVicitimAllegationHist(commonApplicationReq.getIdStage());
		List<PersonListAlleDto> updatedpersonAllVictimAllegationDto = new ArrayList<PersonListAlleDto>();
		for (PersonListAlleDto personAllVictimAllegationDto : personAllVictimAllegationList) {
			if (ObjectUtils.isEmpty(personAllVictimAllegationDto.getNmPersonFirst())
					&& ObjectUtils.isEmpty(personAllVictimAllegationDto.getNmPersonLast())
					&& ObjectUtils.isEmpty(personAllVictimAllegationDto.getNmPersonMiddle())
					&& !ObjectUtils.isEmpty(personAllVictimAllegationDto.getPersonFull())) {
				personAllVictimAllegationDto.setNmPersonFirst(personAllVictimAllegationDto.getPersonFull()
						.replaceAll(ServiceConstants.SPACE_SYMBOL, ServiceConstants.EMPTY_STRING));
				updatedpersonAllVictimAllegationDto.add(personAllVictimAllegationDto);
			} else {
				updatedpersonAllVictimAllegationDto.add(personAllVictimAllegationDto);
			}
		}
		prefillDto.setPersonAllVictimAllegationList(updatedpersonAllVictimAllegationDto);
		// CLSC05D
		List<AllegationWithVicDto> vctmPrepetratorAllegationDtlList = populateFormDao
				.getAllegationById(commonApplicationReq.getIdStage());
		List<AllegationWithVicDto> updatedvctmPrepetratorAllegationDtlList = new ArrayList<AllegationWithVicDto>();
		for (AllegationWithVicDto vctmPrepetratorAllegationDtl : vctmPrepetratorAllegationDtlList) {
			if (ObjectUtils.isEmpty(vctmPrepetratorAllegationDtl.getbNmPersonFirst())
					&& ObjectUtils.isEmpty(vctmPrepetratorAllegationDtl.getbNmPersonLast())
					&& ObjectUtils.isEmpty(vctmPrepetratorAllegationDtl.getbNmPersonMiddle())
					&& !ObjectUtils.isEmpty(vctmPrepetratorAllegationDtl.getB_nmPersonFull())) {
				vctmPrepetratorAllegationDtl.setbNmPersonFirst(vctmPrepetratorAllegationDtl.getB_nmPersonFull()
						.replaceAll(ServiceConstants.SPACE_SYMBOL, ServiceConstants.EMPTY_STRING));
				updatedvctmPrepetratorAllegationDtlList.add(vctmPrepetratorAllegationDtl);
			} else {
				updatedvctmPrepetratorAllegationDtlList.add(vctmPrepetratorAllegationDtl);
			}
		}
		prefillDto.setVctmPrepetratorAllegationDtl(updatedvctmPrepetratorAllegationDtlList);
		// CSEC02D
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(commonApplicationReq.getIdStage());
		prefillDto.setGenericCaseInfoDto(genericCaseInfoDto);
		// CCMN19D
		StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(commonApplicationReq.getIdStage(),
				ServiceConstants.PRIMARY_CASEWORKER);
		if (!ObjectUtils.isEmpty(stagePersonDto)) {
			idCaseworker = stagePersonDto.getIdTodoPersWorker();
		}
		// CSEC01D
		EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idCaseworker);
		prefillDto.setEmployeePersPhNameDto(employeePersPhNameDto);
		return licensingInvReportPrefillData.returnPrefillData(prefillDto);
	}

}
