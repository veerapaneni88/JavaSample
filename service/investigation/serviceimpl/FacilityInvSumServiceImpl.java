package us.tx.state.dfps.service.investigation.serviceimpl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.notiftolawenforcement.dto.FacilInvDtlDto;
import us.tx.state.dfps.notiftolawenforcement.dto.MultiAddressDto;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.FacilityInvSumReq;
import us.tx.state.dfps.service.contacts.dao.AllegFacilDao;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityAllegationInfoDto;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityInvSumDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.FacilityInvSumPrefillData;
import us.tx.state.dfps.service.investigation.dao.FacilityInvSumDao;
import us.tx.state.dfps.service.investigation.dao.FacilityInvestigationDao;
import us.tx.state.dfps.service.investigation.service.FacilityInvSumService;
import us.tx.state.dfps.service.notiftolawenforce.dao.NotifToLawEnforcementDao;
import us.tx.state.dfps.service.person.dto.AllegationWithVicDto;
import us.tx.state.dfps.service.populateform.dao.PopulateFormDao;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;
import us.tx.state.dfps.xmlstructs.inputstructs.AllegationFacilAllegPersonDto;
import us.tx.state.dfps.xmlstructs.inputstructs.FacilityInvestigationDto;
import us.tx.state.dfps.xmlstructs.outputstructs.FacilAllegPersonDto;
import us.tx.state.dfps.xmlstructs.outputstructs.FacilInvstInfoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: CINV68S Mar
 * 16, 2018- 2:11:06 PM © 2017 Texas Department of Family and Protective
 * Services
 */

@Service
@Transactional
public class FacilityInvSumServiceImpl implements FacilityInvSumService {

	@Autowired
	private DisasterPlanDao disasterPlanDao;

	@Autowired
	private PopulateFormDao populateFormDao;

	@Autowired
	private PopulateLetterDao populateLetterDao;

	@Autowired
	private StageDao stageDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private FacilityInvSumDao facilityInvSumDao;

	@Autowired
	private AllegFacilDao allegFacilDao;

	@Autowired
	private FacilityInvestigationDao facilityInvestigationDao;

	@Autowired
	private NotifToLawEnforcementDao notifToLawEnforcementDao;

	@Autowired
	private FacilityInvSumPrefillData facilityInvSumPrefillData;

	public FacilityInvSumServiceImpl() {
		super();
	}

	/**
	 * Method Name: getFacilityInvSumReport Method Description: Populates form
	 * cfiv1300, which outputs the Facility Investigation Summary form.
	 * 
	 * @return PreFillDataServiceDto @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getFacilityInvSumReport(FacilityInvSumReq facilityInvSumReq) {

		FacilityInvSumDto facilityInvSumDto = new FacilityInvSumDto();

		Long idCase = 0L;
		Long idPerson = 0L;
		EmployeePersPhNameDto employeePersPhNameDtoWorker = null;
		EmployeePersPhNameDto employeePersPhNameDtoSupv = null;

		/* retrieves stage and caps_case table CallCSEC02D */
		GenericCaseInfoDto genCaseInfoDto = disasterPlanDao.getGenericCaseInfo(facilityInvSumReq.getIdStage());
		idCase = genCaseInfoDto.getIdCase();

		/* retrieves Allegation info CLSC05D */
		List<AllegationWithVicDto> allegationWithVicDtoList = populateFormDao
				.getAllegationById(facilityInvSumReq.getIdStage());

		if (!ObjectUtils.isEmpty(allegationWithVicDtoList)) {
			for (AllegationWithVicDto allegVic : allegationWithVicDtoList) {
				if (allegVic.getcNmPersonFull().equals(ServiceConstants.NULL_STRING)
						&& (allegVic.getfCdFacilAllegSrc().equals(ServiceConstants.FAC_SOURCE)
								|| allegVic.getfCdFacilAllegSrcSupr().equals(ServiceConstants.FAC_SOURCE))) {
					allegVic.setcNmPersonFull(ServiceConstants.SYSTEM_ISSUE);
				}
			}
		}

		/* facility inv Detail info CallCSES39D */
		FacilInvDtlDto facilInvDtlDto = notifToLawEnforcementDao.getFacilityInvDtlbyId(facilityInvSumReq.getIdStage());

		/* retrieves principal and reporter info CallCLSC01D */
		List<CaseInfoDto> caseInfoDtoprincipal = populateLetterDao.getCaseInfoById(facilityInvSumReq.getIdStage(),
				ServiceConstants.PRINCIPAL);

		/* retrieves Collateral info CallCLSC01D */
		List<CaseInfoDto> caseInfoDtocollateral = populateLetterDao.getCaseInfoById(facilityInvSumReq.getIdStage(),
				ServiceConstants.COLLATERAL);

		/* retrieve stage_person_link CallCCMN19D */
		StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(facilityInvSumReq.getIdStage(),
				ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);

		idPerson = stagePersonDto.getIdTodoPersWorker();

		/* retrieves worker (callCSEC01D) for CallCCMN19D */
		if (!ObjectUtils.isEmpty(idPerson)) {
			employeePersPhNameDtoWorker = employeeDao.searchPersonPhoneName(idPerson);

			if (!ObjectUtils.isEmpty(employeePersPhNameDtoWorker.getIdJobPersSupv())) {
				/* retrieves Supervisor(callCSEC01D) for CallCCMN19D */
				employeePersPhNameDtoSupv = employeeDao
						.searchPersonPhoneName(employeePersPhNameDtoWorker.getIdJobPersSupv());
			}
		}

		/* retrieves facility injury record for stage CallCLSC17D */

		FacilityAllegationInfoDto facilityAllegationInfoDto = facilityInvSumDao
				.getAllegationInfo(facilityInvSumReq.getIdStage());

		/* retrieves facility alletation rows for stage CallCLSC16D */

		AllegationFacilAllegPersonDto allegationFacilAllegPersonDto = new AllegationFacilAllegPersonDto();
		allegationFacilAllegPersonDto.setUlIdAllegationStage(facilityInvSumReq.getIdStage().intValue());
		FacilAllegPersonDto facilAllegPersonDto = allegFacilDao
				.getAllegationFacilAllegPerson(allegationFacilAllegPersonDto);

		/* retrieves the component code for the facility CallCINV17D */
		FacilityInvestigationDto facilityInvestigationDto = new FacilityInvestigationDto();

		facilityInvestigationDto.setUlIdStage(facilityInvSumReq.getIdStage().intValue());

		FacilInvstInfoDto facilInvstInfoDto = facilityInvestigationDao
				.getFacilityInvestigationDetail(facilityInvestigationDto);

		/*
		 * retrieves the dt_contact_occurred for request for review contacts
		 * CallCINVB8D
		 */
		Date dtContactOccured = facilityInvSumDao.getContactDate(facilityInvSumReq.getIdStage());

		// CallCLSCGCD
		List<MultiAddressDto> multiAddressDtoList = notifToLawEnforcementDao
				.getMultiAddress(facilityInvSumReq.getIdStage(), idCase);

		facilityInvSumDto.setAllegationWithVicDtoList(allegationWithVicDtoList);
		facilityInvSumDto.setCaseInfoDtocollateral(caseInfoDtocollateral);
		facilityInvSumDto.setCaseInfoDtoprincipal(caseInfoDtoprincipal);
		facilityInvSumDto.setDtContactOccured(dtContactOccured);
		facilityInvSumDto.setEmployeePersPhNameDtoSupv(employeePersPhNameDtoSupv);
		facilityInvSumDto.setEmployeePersPhNameDtoWorker(employeePersPhNameDtoWorker);
		facilityInvSumDto.setFacilAllegPersonDto(facilAllegPersonDto);
		facilityInvSumDto.setFacilInvDtlDto(facilInvDtlDto);
		facilityInvSumDto.setFacilInvstInfoDto(facilInvstInfoDto);
		facilityInvSumDto.setGenCaseInfoDto(genCaseInfoDto);
		facilityInvSumDto.setMultiAddressDtoList(multiAddressDtoList);
		facilityInvSumDto.setStagePersonDto(stagePersonDto);
		facilityInvSumDto.setFacilityAllegationInfoDto(facilityAllegationInfoDto);

		return facilityInvSumPrefillData.returnPrefillData(facilityInvSumDto);
	}

}
