package us.tx.state.dfps.service.cpsinvstsummary.serviceimpl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.notiftolawenforcement.dto.PriorStageDto;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstSctnTaskValueDto;
import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstValueDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dto.RiskAssessmentFactorDto;
import us.tx.state.dfps.service.casepackage.dao.PcspListPlacmtDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CpsInvstSummaryReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.conservatorship.dao.CnsrvtrshpRemovalDao;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvReportMergedDto;
import us.tx.state.dfps.service.cpsinvreport.dao.CpsInvReportDao;
import us.tx.state.dfps.service.cpsinvstsummary.dao.CpsInvstSummaryDao;
import us.tx.state.dfps.service.cpsinvstsummary.dto.CpsInvstSummaryDto;
import us.tx.state.dfps.service.cpsinvstsummary.dto.RiskAssessmentInfoDto;
import us.tx.state.dfps.service.cpsinvstsummary.service.CpsInvstSummaryService;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.CpsInvstSummaryPrefillData;
import us.tx.state.dfps.service.investigation.dto.CpsInvstDetailDto;
import us.tx.state.dfps.service.notiftolawenforce.dao.NotifToLawEnforcementDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.populateform.dao.PopulateFormDao;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.riskassessment.dao.RiskAssessmentDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * implementation of CINV65S Mar 28, 2018- 4:36:56 PM © 2017 Texas Department of
 * Family and Protective Services
 *  * **********  Change History *********************************
 * 05/04/2020 thompswa artf147748 : CPI June 2020 Project - adjustment for removal checklist data model change
 */
@Service
@Transactional
public class CpsInvstSummaryServiceImpl implements CpsInvstSummaryService {

	@Autowired
	private DisasterPlanDao disasterPlanDao;

	@Autowired
	private PopulateFormDao populateFormDao;

	@Autowired
	private CpsInvstSummaryDao cpsInvstSummaryDao;

	@Autowired
	private RiskAssessmentDao riskAssessmentDao;

	@Autowired
	private NotifToLawEnforcementDao notifToLawEnforcementDao;

	@Autowired
	private PopulateLetterDao populateLetterDao;

	@Autowired
	private StageDao stageDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private CnsrvtrshpRemovalDao cnsrvtrshpRemovalDao;

	@Autowired
	private CpsInvstSummaryPrefillData prefillData;

	@Autowired
	CpsInvReportDao cpsInvReportDao;	

	@Autowired
	PcspListPlacmtDao pcspListPlacmtDao;

	@Autowired
	PersonDao personDao;

	/**
	 * Method Name: getRiskAssessmentInfo Method Description: Makes DAO calls
	 * and returns prefill string for form CFIV1000
	 * 
	 * @param request
	 * @return PreFillDataServiceDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getRiskAssessmentInfo(CpsInvstSummaryReq request) {
		// Declare global variables and Prefill dto
		CpsInvstSummaryDto prefillDto = new CpsInvstSummaryDto();
		Long idPerson = ServiceConstants.ZERO_VAL;
		Long idJobPersSupv = ServiceConstants.ZERO_VAL;
		GenericCaseInfoDto genericCaseInfoDto = new GenericCaseInfoDto();
		String mergeString;
		String idMethSusp = null;
		Long intakeStageId = ServiceConstants.ZERO;

		// CSEC02D
		prefillDto.setGenericCaseInfoDto(disasterPlanDao.getGenericCaseInfo(request.getIdStage()));

		// CLSC05D
		prefillDto.setAllegationWithVicList(populateFormDao.getAllegationById(request.getIdStage()));

		// CSECFAD
		List<RiskAssessmentInfoDto> riskAssessmentInfoList = cpsInvstSummaryDao
				.getRiskAssessmentInfo(request.getIdStage());
		if (!ObjectUtils.isEmpty(riskAssessmentInfoList)) {
			prefillDto.setRiskAssessmentInfoDto(riskAssessmentInfoList.get(0));
		}

		// only call CINV14D if CSECFAD returns 0 rows
		else {
			// CINV14D
			List<RiskAssessmentFactorDto> riskAssessmentFactorList = riskAssessmentDao
					.getRiskAssessmentFactorDtls(request.getIdStage());
			if (!ObjectUtils.isEmpty(riskAssessmentFactorList)) {
				prefillDto.setRiskAssessmentFactorDto(riskAssessmentFactorList.get(0));
			}
		}

		// CINV95D
		List<CpsInvstDetailDto> cpsInvstDetailList = cpsInvstSummaryDao.getCpsInvstDetail(request.getIdStage());
		if (!ObjectUtils.isEmpty(cpsInvstDetailList)) {
			CpsInvstDetailDto cpsInvstDetail = cpsInvstDetailList.get(0);
			// need day of first contact - investigation may not have been saved
			// yet
			if (ObjectUtils.isEmpty(cpsInvstDetail.getDtCpsInvstDtlBegun())) {
				cpsInvstDetail.setDtCpsInvstDtlBegun(request.getDtCpsInvstDtlBegun());
			}
			prefillDto.setCpsInvstDetail(cpsInvstDetail);
		}
		
		// csecd2d no java version, gets the prior INT stage ID
		if (!ObjectUtils.isEmpty(cpsInvReportDao.getPriorIntStage(request.getIdStage()))) {
			intakeStageId = cpsInvReportDao.getPriorIntStage(request.getIdStage());
		}
		if(!ObjectUtils.isEmpty(intakeStageId) && !ServiceConstants.ZERO.equals(intakeStageId)){
			idMethSusp = pcspListPlacmtDao.getMethIndicator(intakeStageId);
		}
		prefillDto.setIdMethSusp(idMethSusp);
		// CINV86D
		PriorStageDto priorStageDto = notifToLawEnforcementDao.getPriorStagebyId(request.getIdStage());
		if (!ObjectUtils.isEmpty(priorStageDto)) {
			prefillDto.setPriorStageDto(priorStageDto);

			// CINT07D - needs idPriorStage from CINV86D
			prefillDto.setIncomingDetail(cpsInvstSummaryDao.getIncomingDetail(priorStageDto.getIdPriorStage()));
		}

		// CLSC01D - Principal/Reporter
		List<CaseInfoDto> caseInfoPrincipalList = populateLetterDao.getCaseInfoById(request.getIdStage(),
				ServiceConstants.PRINCIPAL);

		if (!ObjectUtils.isEmpty(caseInfoPrincipalList)) {

			for (CaseInfoDto caseInfoDto : caseInfoPrincipalList) {

				if ( null == caseInfoDto.getIndPersonDobApprox()) caseInfoDto.setIndPersonDobApprox(ServiceConstants.N); // artf151738
				String role = caseInfoDto.getCdStagePersRole();
				// Determine whether victim or perpetrator
				if (ServiceConstants.DESIGNATED_VICTIM.equals(role) || ServiceConstants.DESIGNATED_BOTH.equals(role)
						|| ServiceConstants.VICTIM.equals(role) || ServiceConstants.AV_PERP.equals(role)) {
					caseInfoDto.setIndPersCancelHist(ServiceConstants.VIEW);
				} else if (ServiceConstants.DESIGNATED_PERP.equals(role) || ServiceConstants.ALLEG_PERP.equals(role)) {
					caseInfoDto.setIndPersCancelHist(ServiceConstants.PHONE);
				} else {
					caseInfoDto.setIndPersCancelHist(ServiceConstants.OTHER_O);
				}

				// Calculate age
				if (!ObjectUtils.isEmpty(caseInfoDto.getDtPersonBirth())) {
					caseInfoDto.setNbrPersonAge(
							Integer.valueOf(DateUtils.getAge(caseInfoDto.getDtPersonBirth())).shortValue());
				}

			}
			prefillDto.setCaseInfoPrincipalList(caseInfoPrincipalList);
		}

		// CLSC01D - Collateral
		List<CaseInfoDto> caseInfoCollateralList = populateLetterDao.getCaseInfoById(request.getIdStage(),
				ServiceConstants.COL_TYPE);
		if (!ObjectUtils.isEmpty(caseInfoCollateralList)) {
			for (CaseInfoDto caseInfoDto : caseInfoCollateralList) {
				String role = caseInfoDto.getCdStagePersRole();
				// Determine whether victim or perpetrator
				if (ServiceConstants.DESIGNATED_VICTIM.equals(role) || ServiceConstants.DESIGNATED_BOTH.equals(role)
						|| ServiceConstants.VICTIM.equals(role) || ServiceConstants.AV_PERP.equals(role)) {
					caseInfoDto.setIndPersCancelHist(ServiceConstants.VIEW);
				} else if (ServiceConstants.DESIGNATED_PERP.equals(role) || ServiceConstants.ALLEG_PERP.equals(role)) {
					caseInfoDto.setIndPersCancelHist(ServiceConstants.PHONE);
				} else {
					caseInfoDto.setIndPersCancelHist(ServiceConstants.OTHER_O);
				}

				// Calculate age
				if (!ObjectUtils.isEmpty(caseInfoDto.getDtPersonBirth())) {
					caseInfoDto.setNbrPersonAge(
							new Integer(DateUtils.getAge(caseInfoDto.getDtPersonBirth())).shortValue());
				}
			}
			prefillDto.setCaseInfoCollateralList(caseInfoCollateralList);
		}

		// CCMN19D
		StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(request.getIdStage(),
				ServiceConstants.PRIMARY_WORKER);
		if (!ObjectUtils.isEmpty(stagePersonDto)) {
			prefillDto.setStagePersonDto(stagePersonDto);
			idPerson = stagePersonDto.getIdTodoPersWorker();

			// CSEC01D - Worker; needs idPerson from CCMN19D
			EmployeePersPhNameDto empWorkerDto = employeeDao.searchPersonPhoneName(idPerson);
			if (!ObjectUtils.isEmpty(empWorkerDto)) {
				prefillDto.setEmpWorkerDto(empWorkerDto);
				idJobPersSupv = empWorkerDto.getIdJobPersSupv();

				// CSEC01D - Supervisor of Worker
				prefillDto.setEmpSupvDto(employeeDao.searchPersonPhoneName(idJobPersSupv));
			}
		}

		// Set the Data for the Template Removal CheckList

		/**
		 * Need to get all of the merged stages using the merged stage string
		 * from stored procedure clscdbd, getMergedStages.
		 */
		List<RmvlChcklstValueDto> checklists = null;
		
		StageDto stageDto = stageDao.getStageById(request.getIdStage());
		StringBuilder stageString = new StringBuilder(stageDto.getIdStage().toString());
		
		CpsInvReportMergedDto cpsInvReportMergedDto = new CpsInvReportMergedDto();
		cpsInvReportMergedDto = cpsInvReportDao.getMergedStages(stageDto);
		if (!ObjectUtils.isEmpty(cpsInvReportMergedDto.getStrMergedStages())) {
			stageString.append(",");
			stageString.append(cpsInvReportMergedDto.getStrMergedStages());
		}
		checklists = cnsrvtrshpRemovalDao.getRmvlGroupsByStage(stageString.toString());
		prefillDto.setChecklists(checklists);

		List<RmvlChcklstSctnTaskValueDto> sctnTasks = cnsrvtrshpRemovalDao.getRmvlSctnTaskByStage(stageString.toString());
		prefillDto.setSctnTasks(sctnTasks);

    // Get Citizenship Status for stage id and Oldest Victim.
    String citizenStatus =
        personDao.getOldestVictimCitizenshipByStageIdAndRelType(
            request.getIdStage(), ServiceConstants.CRPTRINT_OV);
		if(!StringUtils.isEmpty(citizenStatus)) {
			prefillDto.setCitizenshipStatus(citizenStatus);
		}

		return prefillData.returnPrefillData(prefillDto);
	}

}
