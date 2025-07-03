package us.tx.state.dfps.service.hsegh.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.ChildPlanRecordDto;
import us.tx.state.dfps.common.dto.HseghDto;
import us.tx.state.dfps.common.dto.PersonOnHseghDto;
import us.tx.state.dfps.common.dto.ServicePlanDto;
import us.tx.state.dfps.service.admin.dao.LegalStatusPersonMaxStatusDtDao;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtInDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.HseghReq;
import us.tx.state.dfps.service.common.response.HseghRes;
import us.tx.state.dfps.service.conservatorship.dao.CharacteristicsDao;
import us.tx.state.dfps.service.conservatorship.dto.CharacteristicsDto;
import us.tx.state.dfps.service.conservatorship.dto.CnsrvtrshpRemovalDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.HseghFormPrefillData;
import us.tx.state.dfps.service.hsegh.dao.HseghDao;
import us.tx.state.dfps.service.hsegh.service.HseghService;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.ServicePackageDao;
import us.tx.state.dfps.service.person.dto.PersonDtlDto;
import us.tx.state.dfps.service.person.dto.ServicePackageDtlDto;
import us.tx.state.dfps.service.person.service.ServicePackageService;
import us.tx.state.dfps.service.person.serviceimpl.ServicePackageServiceImpl;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dto.AllegationCpsInvstDtlDto;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.placement.dto.PersonLocDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:HseghServiceImpl will implemented all operation defined in
 * PcaService Interface related HSEGH module. Feb 22, 2018- 2:01:28 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class HseghServiceImpl implements HseghService {

	@Autowired
	CommonApplicationDao commonApplicationDao;

	@Autowired
	HseghDao hseghDao;

	@Autowired
	LegalStatusPersonMaxStatusDtDao legalStatusPersonMaxStatusDtDao;

	@Autowired
	CharacteristicsDao characteristicsDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	HseghFormPrefillData hseghFormPrefillData;

	@Autowired
	ServicePackageDao servicePackageDao;

	/**
	 * 
	 * Method Name: getHsegh Service Name: CSUB71S Description: The Health,
	 * Social, Educational, and Genetic History (HSEGH) is required by the Texas
	 * Family Code, chapter 16.032 to be completed before placing a child for
	 * adoption with anyone other than a biological relative or relative by
	 ** marriage or if the child has been in substitute care for more than twelve
	 * months. the worker will begin gathering information at the point of
	 * investigation and removal. The worker can then add information throughout
	 * th life of the case. The HSEGH fully documents the child's readiness for
	 * adoption and informs the adoptive parents about the child's history and
	 * needs. Even if the child's permanency plan is not adoption, the HSEGH
	 * will be required is the child is in substitute care for more than twelve
	 * months. External documentation such as Education log, Medical/Mental log
	 * will be attached to the HSEGH for the caretakers to review.
	 * 
	 * @param hseghReq
	 * @return
	 * 
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getHsegh(HseghReq hseghReq) {

		Long idPerson15 = ServiceConstants.ZERO_VAL;
		HseghDto hseghDto = new HseghDto();
		// call DAM CSEC15D
		StagePersonLinkCaseDto stagePersonLinkCaseDto = commonApplicationDao
				.getStagePersonCaseDtl(hseghReq.getIdStage(), ServiceConstants.SPL);
		idPerson15 = stagePersonLinkCaseDto.getIdPerson();
		hseghDto.setStagePersonLinkCaseDto(stagePersonLinkCaseDto);

		// call DAM CSEC15D
		NameDetailDto nameDetailDto = commonApplicationDao.getNameDetails(idPerson15);
		hseghDto.setNameDetailDto(nameDetailDto);

		// call DAM CLSS29D
		List<AllegationCpsInvstDtlDto> allegationCpsInvsDtlDtoList = commonApplicationDao
				.getAllegationCpsInvstDetails(idPerson15);
		hseghDto.setAllegationCpsInvsDtlDtoList(allegationCpsInvsDtlDtoList);

		// call DAM CLSS42D
		List<CnsrvtrshpRemovalDto> cnsrvtrshpRemovalDtoList = hseghDao.getConservatorshipById(idPerson15);
		hseghDto.setCnsrvtrshpRemovalDtoList(cnsrvtrshpRemovalDtoList);

		// call DAM CLSS43D
		List<CnsrvtrshpRemovalDto> cnsrvtrshpRemovalReasonDtoList = hseghDao
				.getRmvlReasonForCnsrvtrshpRemoval(idPerson15);
		hseghDto.setCnsrvtrshpRemovalReasonDtoList(cnsrvtrshpRemovalReasonDtoList);

		// call DAM CSEC41D
		List<ServicePlanDto> servicePlanDtoList = hseghDao.getOldestApprFP(idPerson15);
		hseghDto.setServicePlanDtoList(servicePlanDtoList);

		// call DAM CSES32D
		LegalStatusPersonMaxStatusDtInDto inputRec = new LegalStatusPersonMaxStatusDtInDto();
		inputRec.setIdPerson(idPerson15);
		List<LegalStatusPersonMaxStatusDtOutDto> legalStatusPersonMaxStatusList = legalStatusPersonMaxStatusDtDao
				.getRecentLegelStatusRecord(inputRec);
		hseghDto.setLegalStatusPersonMaxStatusList(legalStatusPersonMaxStatusList);

		// call DAM CSES35D
		if (!ObjectUtils.isEmpty(commonApplicationDao.getPersonLocDtls(idPerson15, ServiceConstants.CD_PLOC_TYPE))) {
			PersonLocDto personLocDto = commonApplicationDao.getPersonLocDtls(idPerson15, ServiceConstants.CD_PLOC_TYPE)
					.get(0);
			hseghDto.setPersonLocDto(personLocDto);
		}

		// call DAM CSES35D
		List<CharacteristicsDto> characteristicDtoList = characteristicsDao.getCharByPersonIdAndCategory(idPerson15,
				ServiceConstants.CD_CHAR_CATEGORY);
		hseghDto.setCharacteristicDtoList(characteristicDtoList);

		// call DAM CSEC58D
		List<PersonDtlDto> personDtlDtoList = personDao.getPersonDtlByIdStage(hseghReq.getIdStage(),
				ServiceConstants.PRIMARY_CHILD);
		hseghDto.setPersonDtlDtoList(personDtlDtoList);

		// call DAM CLSC43D
		List<PersonOnHseghDto> allPeopleOnHseghDtoList = hseghDao.getAllPeopleOnHsegh(hseghReq.getIdStage());
		hseghDto.setAllPeopleOnHseghDtoList(allPeopleOnHseghDtoList);

		// call DAM CLSS41D
		List<ChildPlanRecordDto> childPlanRecordDtoList = hseghDao.getChildPlanRecords(hseghReq.getIdStage());
		if (!ObjectUtils.isEmpty(childPlanRecordDtoList)) {
			List<ChildPlanRecordDto> tempChildPlanList = new ArrayList<ChildPlanRecordDto>();
			for (int i = ServiceConstants.Zero_INT; i < ServiceConstants.HSEGH_PAGE_SIZE
					&& i < childPlanRecordDtoList.size(); i++) {
				tempChildPlanList.add(childPlanRecordDtoList.get(i));
			}
			hseghDto.setChildPlanRecordDtoList(tempChildPlanList);
		}

		// call DAM CSC32O00
		List<ServicePackageDtlDto> servicePackagesDetails = servicePackageDao.getServicePackageDetails(hseghReq.getIdCase(), hseghReq.getIdStage());
		hseghDto.setServicePackageDetails(servicePackagesDetails);
		List<ServicePackageDtlDto> servicePackages = servicePackageDao.getServicePackages(hseghReq.getIdCase(), hseghReq.getIdStage());
		hseghDto.setServicePackages(servicePackages);

		HseghRes hseghRes = new HseghRes();
		hseghRes.setHseghDto(hseghDto);
		return hseghFormPrefillData.returnPrefillData(hseghDto);
	}

}
