package us.tx.state.dfps.service.childbackground.serviceimpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.ChildBackgroundServiceDto;
import us.tx.state.dfps.common.dto.PrincipalLegalStatusDto;
import us.tx.state.dfps.common.dto.WorkerDetailDto;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.admin.dao.EligibilityDao;
import us.tx.state.dfps.service.admin.dto.EligibilityInDto;
import us.tx.state.dfps.service.admin.dto.EligibilityOutDto;
import us.tx.state.dfps.service.admin.dto.PlacementActPlannedOutDto;
import us.tx.state.dfps.service.childbackground.dao.ChildBackgroundDao;
import us.tx.state.dfps.service.childbackground.service.ChildBackgroundService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ChildBackgroundReq;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dao.RemovalCharAdultDao;
import us.tx.state.dfps.service.conservatorship.dao.RemovalReasonDao;
import us.tx.state.dfps.service.conservatorship.dto.CnsrvtrshpRemovalDto;
import us.tx.state.dfps.service.conservatorship.dto.RemovalCharAdultDto;
import us.tx.state.dfps.service.conservatorship.dto.RemovalReasonDto;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dao.SubcareLOCFormDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ChildBackgroundFormPrefillData;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.ServicePackageDao;
import us.tx.state.dfps.service.person.dto.ChildPlanDto;
import us.tx.state.dfps.service.person.dto.PersonIdDto;
import us.tx.state.dfps.service.person.dto.ServicePackageDtlDto;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dao.PersonIdDtlsDao;
import us.tx.state.dfps.service.placement.dto.PersonLocDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;

import static us.tx.state.dfps.service.common.ServiceConstants.SERVICE_PACKAGE_RECOMMENDED;
import static us.tx.state.dfps.service.common.ServiceConstants.SERVICE_PACKAGE_SELECTED;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ChildBackgroundServiceImpl will implemented all operation defined
 * in ChildBackgroundService Interface related ChildBackground module. March 27,
 * 2018- 2:01:28 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class ChildBackgroundServiceImpl implements ChildBackgroundService {

	@Autowired
	private CommonApplicationDao commonApplicationDao;

	@Autowired
	private SubcareLOCFormDao subcareLOCFormDao;

	@Autowired
	private PersonDao personDao;

	@Autowired
	private PersonIdDtlsDao personIdDtlsDao;

	@Autowired
	private ChildBackgroundDao childBackgroundDao;

	@Autowired
	private LookupDao lookupDao;

	@Autowired
	private EligibilityDao eligibilityDao;

	@Autowired
	private RemovalReasonDao removalReasonDao;

	@Autowired
	private RemovalCharAdultDao removalCharAdultDao;

	@Autowired
	private WorkLoadDao workLoadDao;

	@Autowired
	private DisasterPlanDao disasterPlanDao;

	@Autowired
	private PopulateLetterDao populateLetterDao;

	@Autowired
	private ChildBackgroundFormPrefillData childBackgroundFormPrefillData;

	@Autowired
	private ServicePackageDao servicePackageDao;


	/**
	 * Method Description: This method is used to retrieve the information for
	 * ChildBackgroundService Form by passing IdStage input request
	 * 
	 * @param childBackgroundReq
	 * @return PreFillDataServiceDto
	 * 
	 */

	@SuppressWarnings("static-access")
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getChildBackgroundInfo(ChildBackgroundReq childBackgroundReq) {
		Long idPerson = ServiceConstants.ZERO_VAL;
		Long idPerson2 = ServiceConstants.ZERO_VAL;
		Long idRemovalEvent = ServiceConstants.ZERO_VAL;
		EligibilityOutDto eligibilityOutDto = new EligibilityOutDto();
		ChildBackgroundServiceDto childBackgroundServiceDto = new ChildBackgroundServiceDto();

		// call DAM CSEC15D
		StagePersonLinkCaseDto stagePersonLinkCaseDto = commonApplicationDao
				.getStagePersonCaseDtl(childBackgroundReq.getIdStage(), ServiceConstants.SPL);
		if (!ObjectUtils.isEmpty(stagePersonLinkCaseDto)) {
			idPerson = stagePersonLinkCaseDto.getIdPerson();
			childBackgroundReq.setIdCase(stagePersonLinkCaseDto.getIdCase());
		}
		Date currentDate = lookupDao.getCurrentDate();
		DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
		stagePersonLinkCaseDto.setTmScrTmGeneric8(dateFormat.format(currentDate));

		Calendar cal = Calendar.getInstance();
		cal.set(cal.YEAR, ServiceConstants.ARC_MAX_YEAR);
		cal.set(cal.MONTH, cal.DECEMBER);
		cal.set(cal.DATE, ServiceConstants.ARC_MAX_DAY);
		cal.set(cal.HOUR_OF_DAY, ServiceConstants.Zero_INT);
		cal.set(cal.MINUTE, ServiceConstants.Zero_INT);
		cal.set(cal.SECOND, ServiceConstants.Zero_INT);
		cal.set(cal.MILLISECOND, ServiceConstants.Zero_INT);

		// call DAM CCMN72D
		PersonIdDto personIdDto = subcareLOCFormDao.getMedicaidNbrByPersonId(idPerson, ServiceConstants.SOCIAL_SECURITY,
				ServiceConstants.IND_PERSON_ID, cal.getTime());

		// call DAM CCMN44D
		PersonDto personDto = personDao.getPersonById(idPerson);

		// Call DAM CSES34D
		PlacementActPlannedOutDto placementActPlannedOutDto = personIdDtlsDao.getPlacementRecord(idPerson);

		// Call CSES35D
		if (!ObjectUtils.isEmpty(commonApplicationDao.getPersonLocDtls(idPerson, ServiceConstants.CARE_LEVEL))) {
			PersonLocDto personBlocLocDto = commonApplicationDao.getPersonLocDtls(idPerson, ServiceConstants.CARE_LEVEL)
					.get(0);
			childBackgroundServiceDto.setPersonBlocLocDto(personBlocLocDto);
		}

		// call CSES38D
		EligibilityInDto eligibilityInputDto = new EligibilityInDto();
		eligibilityInputDto.setIdPerson(idPerson);
		eligibilityInputDto.setDtScrDtCurrentDate(currentDate);
		List<EligibilityOutDto> eligiList = eligibilityDao.getEligibilityRecord(eligibilityInputDto);
		if (!TypeConvUtil.isNullOrEmpty(eligiList)) {
			eligibilityOutDto = eligibilityDao.getEligibilityRecord(eligibilityInputDto).get(0);
		}
		// call DAM CSECB9D
		ChildPlanDto childPlanDto = childBackgroundDao.getChildPlan(idPerson, childBackgroundReq.getIdStage());

		// call DAM CDYN10D
		CnsrvtrshpRemovalDto cnsrvtrshpRemovalDto = childBackgroundDao.getRmvlDateAndRmvlEvent(idPerson,
				ServiceConstants.SYS_CARC_RQST_FUNC_CODE);
		idRemovalEvent = cnsrvtrshpRemovalDto.getIdRemovalEvent();

		// call DAM CLSS21D
		List<Long> idEventList = new ArrayList<Long>();
		idEventList.add(idRemovalEvent);
		List<RemovalReasonDto> removalResonDtoList = removalReasonDao.getRemReasonDtl(idEventList);

		// call CLSS23D
		List<RemovalCharAdultDto> removalCharAdultDtoList = removalCharAdultDao.getRemCharAdultDtl(idEventList);

		// call DAM CINV51D
		idPerson2 = workLoadDao.getPersonIdByRole(childBackgroundReq.getIdStage(), ServiceConstants.PRIMARY_WORKER);

		// call DAM CSEC01D
		WorkerDetailDto workerDetailDto = disasterPlanDao.getWorkerInfoById(idPerson2);

		// call DAM CLSC01D

		List<CaseInfoDto> caseInfoDtoList = populateLetterDao.getCaseInfoById(childBackgroundReq.getIdStage(),
				ServiceConstants.PRINCIPAL);

		// call DAM CLSC34D
		List<PrincipalLegalStatusDto> principalLegalStatusDto = childBackgroundDao
				.getPrincipalLegalStatus(childBackgroundReq.getIdStage(), ServiceConstants.PRINCIPAL);

		childBackgroundServiceDto.setCaseInfoDtoList(caseInfoDtoList);
		childBackgroundServiceDto.setCnsrvtrshpRemovalDto(cnsrvtrshpRemovalDto);
		childBackgroundServiceDto.setEligibilityOutDto(eligibilityOutDto);
		childBackgroundServiceDto.setPersonDto(personDto);
		childBackgroundServiceDto.setPersonIdDto(personIdDto);
		childBackgroundServiceDto.setPlacementActPlannedOutDto(placementActPlannedOutDto);
		childBackgroundServiceDto.setPrincipalLegalStatusDto(principalLegalStatusDto);
		childBackgroundServiceDto.setRemovalCharAdultDtoList(removalCharAdultDtoList);
		childBackgroundServiceDto.setRemovalResonDtoList(removalResonDtoList);
		childBackgroundServiceDto.setStagePersonLinkCaseDto(stagePersonLinkCaseDto);
		childBackgroundServiceDto.setWorkerDetailDto(workerDetailDto);
		childBackgroundServiceDto.setChildPlanDto(childPlanDto);
		setServicePackageDetails(childBackgroundServiceDto, childBackgroundReq);

		return childBackgroundFormPrefillData.returnPrefillData(childBackgroundServiceDto);

	}

	private void setServicePackageDetails(ChildBackgroundServiceDto childBackgroundServiceDto, ChildBackgroundReq childBackgroundReq) {
		List<ServicePackageDtlDto> servicePackages = servicePackageDao.getServicePackages(childBackgroundReq.getIdCase(), childBackgroundReq.getIdStage());
		if (!CollectionUtils.isEmpty(servicePackages)) {
			childBackgroundServiceDto.setRecommendedServicePackage(servicePackages.stream()
					.filter(servicePackage -> SERVICE_PACKAGE_RECOMMENDED.equalsIgnoreCase(servicePackage.getSvcPkgTypeCd()))
					.max(Comparator.comparing(ServicePackageDtlDto::getDtSvcStart)).orElse(null));
		}
		if(!ObjectUtils.isEmpty(childBackgroundServiceDto.getRecommendedServicePackage())){
			childBackgroundServiceDto.setSelectedServicePackage(servicePackages.stream()
					.filter(servicePackage -> SERVICE_PACKAGE_SELECTED.equalsIgnoreCase(servicePackage.getSvcPkgTypeCd()))
					.max(Comparator.comparing(ServicePackageDtlDto::getDtSvcStart)).orElse(null));
			childBackgroundServiceDto.setAddonServicePackages(servicePackageDao.getServicePackageDetails(childBackgroundReq.getIdCase(), childBackgroundReq.getIdStage()));

			if(!ObjectUtils.isEmpty(childBackgroundServiceDto.getPersonBlocLocDto()) &&
					childBackgroundServiceDto.getPersonBlocLocDto().getDtPlocStart().after(childBackgroundServiceDto.getRecommendedServicePackage().getDtSvcStart())){
				childBackgroundServiceDto.setRecommendedServicePackage(null);
			}
		}

	}

}
