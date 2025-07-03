package us.tx.state.dfps.service.investigation.serviceimpl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.casepackage.serviceimpl.CaseSummaryServiceImpl;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.LicensingInvSummaryPrefillData;
import us.tx.state.dfps.service.investigation.dao.LicensingInvstSumDao;
import us.tx.state.dfps.service.investigation.dto.InvstRestraintDto;
import us.tx.state.dfps.service.investigation.dto.LicensingInvstDtlDto;
import us.tx.state.dfps.service.investigation.dto.LicensingInvstSumDto;
import us.tx.state.dfps.service.investigation.service.LicensingInvSumService;
import us.tx.state.dfps.service.person.dto.AllegationWithVicDto;
import us.tx.state.dfps.service.populateform.dao.PopulateFormDao;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.workload.dao.StageWorkloadDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.RcciMrefDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:<Implementation class for LicensingInvSumService> Mar 27, 2018-
 * 3:06:20 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class LicensingInvSumServiceImpl implements LicensingInvSumService {

	@Autowired
	private DisasterPlanDao disasterPlanDao;

	@Autowired
	private PopulateFormDao populateFormDao;

	@Autowired
	private PopulateLetterDao populateLetterDao;

	@Autowired
	private LicensingInvstSumDao licensingInvstSumDao;

	@Autowired
	private StageDao stageDao;

	@Autowired
	private StageWorkloadDao stageWorkloadDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private LicensingInvSummaryPrefillData licensingInvSummaryPrefillData;

	public LicensingInvSumServiceImpl() {

	}

	/**
	 * Method Name: getFacilityInvSumReport Method Description: Populates form
	 * cfiv1100, which Populates the LICENSING INVESTIGATION REPORT form.
	 * 
	 * @return PreFillDataServiceDto @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getLicensingInvSumReport(PopulateFormReq populateFormReq) {

		LicensingInvstSumDto licensingInvstSumDto = new LicensingInvstSumDto();
		Long idPerson = 0L;
		List<RcciMrefDto> rcciMrefList = null;

		// CallCSEC02D
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(populateFormReq.getIdStage());
		if (!ObjectUtils.isEmpty(genericCaseInfoDto)) {
			genericCaseInfoDto.getIdCase();
			licensingInvstSumDto.setGenericCaseInfoDto(genericCaseInfoDto);

			// Apply MREF if case is available. genericCaseInfoDto
			List<Long> singleCaseList = new LinkedList<>();
			singleCaseList.add(genericCaseInfoDto.getIdCase());
			rcciMrefList = stageWorkloadDao.getRcciMrefDataByCaseList(singleCaseList);

		}


		if (!ObjectUtils.isEmpty(rcciMrefList)) {
			RcciMrefDto mrefDto = rcciMrefList.get(0);
			licensingInvstSumDto.getGenericCaseInfoDto().setRcciMref(
					CaseSummaryServiceImpl.applyRcciMrefThresholds(mrefDto.getRcciMrefCnt(), genericCaseInfoDto.getCdStageProgram(),
							mrefDto.getNbrRsrcFacilCapacity(), mrefDto.getCdRsrcFacilType()));
		}

		// CallCLSC05D
		List<AllegationWithVicDto> allegationWithVicDtolist = populateFormDao
				.getAllegationById(populateFormReq.getIdStage());
		licensingInvstSumDto.setAllegationWithVicDtolist(allegationWithVicDtolist);

		// CallCINV74D
		LicensingInvstDtlDto licensingInvstDtlDto = licensingInvstSumDao
				.getLicensingInvstDtlDaobyParentId(populateFormReq.getIdStage());
		licensingInvstSumDto.setLicensingInvstDtl(licensingInvstDtlDto);

		// CallCLSC01D _PRINCIPAL
		List<CaseInfoDto> caseInfoDtoprincipal = populateLetterDao.getCaseInfoById(populateFormReq.getIdStage(),
				ServiceConstants.PRINCIPAL);
		if (!ObjectUtils.isEmpty(caseInfoDtoprincipal)) {
			for (CaseInfoDto caseInfoDto : caseInfoDtoprincipal) {
				if (caseInfoDto.getCdStagePersRole().equals(ServiceConstants.DESIGNATED_VICTIM)
						|| caseInfoDto.getCdStagePersRole().equals(ServiceConstants.DESIGNATED_BOTH)
						|| (caseInfoDto.getCdStagePersRole().equals(ServiceConstants.VICTIM)
								|| caseInfoDto.getCdStagePersRole().equals(ServiceConstants.VICTIM_PERP))) {
					caseInfoDto.setIndPersCancelHist(ServiceConstants.ALL_VICT);
				} else if (caseInfoDto.getCdStagePersRole().equals(ServiceConstants.DESIGNATED_PERP)
						|| caseInfoDto.getCdStagePersRole().equals(ServiceConstants.ALLEGED_PERP)) {
					caseInfoDto.setIndPersCancelHist(ServiceConstants.PERP);
				} else {
					caseInfoDto.setIndPersCancelHist(ServiceConstants.OTHER_O);
				}
			}
		}
		licensingInvstSumDto.setCaseInfoDtoList(caseInfoDtoprincipal);

		// CallCLSC01D _COLLATERAL
		List<CaseInfoDto> caseInfoDtoCollateral = populateLetterDao.getCaseInfoById(populateFormReq.getIdStage(),
				ServiceConstants.COLLATERAL);
		licensingInvstSumDto.setCaseInfoDtoCollateral(caseInfoDtoCollateral);

		// CALLCSESE1D
		List<InvstRestraintDto> invstRestraintDtoList = licensingInvstSumDao
				.getInvstConclusionResById(populateFormReq.getIdStage());
		if (ObjectUtils.isEmpty(invstRestraintDtoList)) {
			invstRestraintDtoList = new ArrayList<InvstRestraintDto>();
			invstRestraintDtoList.add(new InvstRestraintDto());
		}
		licensingInvstSumDto.setInvstRestraintDtoList(invstRestraintDtoList);

		/* retrieve stage_person_link CallCCMN19D */
		StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(populateFormReq.getIdStage(),
				ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);
		licensingInvstSumDto.setStagePersonDto(stagePersonDto);
		idPerson = stagePersonDto.getIdTodoPersWorker();

		/* retrieves worker info CallCSEC01D */
		if (!ObjectUtils.isEmpty(idPerson)) {
			EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idPerson);
			licensingInvstSumDto.setEmployeePersPhNameDto(employeePersPhNameDto);

			if (!ObjectUtils.isEmpty(employeePersPhNameDto.getIdJobPersSupv())) {
				/* retrieves Supervisor info CallCSEC01D */
				EmployeePersPhNameDto employeeSuperInfo = employeeDao
						.searchPersonPhoneName(employeePersPhNameDto.getIdJobPersSupv());
				licensingInvstSumDto.setEmployeeSuperInfo(employeeSuperInfo);
			}
		}

		return licensingInvSummaryPrefillData.returnPrefillData(licensingInvstSumDto);
	}

}
