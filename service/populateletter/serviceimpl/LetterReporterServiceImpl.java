package us.tx.state.dfps.service.populateletter.serviceimpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.populateletter.dto.PopulateLetterDto;
import us.tx.state.dfps.service.admin.dao.CpsInvstDetailStageIdDao;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dao.StageSituationDao;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailStageIdInDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailStageIdOutDto;
import us.tx.state.dfps.service.admin.dto.StageSituationInDto;
import us.tx.state.dfps.service.admin.dto.StageSituationOutDto;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PopulateLetterReq;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.PopulateLetterPrefillData;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.populateletter.service.LetterReporterService;
import us.tx.state.dfps.service.workload.dao.StageProgDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;
import us.tx.state.dfps.service.workload.dto.StageProgDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service
 * class for populating LetterToReporter form. Jan 11, 2018- 3:56:20 PM © 2017
 * Texas Department of Family and Protective Services
 * ********Change History**********
 * 07/11/2023 thompswa artf250175 pass R/O dispostion to prefillData in TmScrTmGeneric9.
 */
@Service
@Transactional
public class LetterReporterServiceImpl implements LetterReporterService {

	/**
	 * 
	 */
	@Autowired
	private PopulateLetterDao populateLetterDao;

	@Autowired
	private StageSituationDao stageSituationDao;

	@Autowired
	private StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	private StageProgDao stageProgDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private DisasterPlanDao disasterPlanDao;

	@Autowired
	private CpsInvstDetailStageIdDao cpsInvstDetailsStageIdDao;

	@Autowired
	private PopulateLetterPrefillData populateLetterPrefillData;

	public LetterReporterServiceImpl() {
	}

	/**
	 * Service Name: CCMN63S Method Description: This method is used to cThis
	 * service will retrieve the user's supervisor if the ReqFuncCd is
	 * REQ_FUNC_CD_APPROVAL, or it will get the NM STAGE, NM TASK, TASK DUE DT,
	 * PRIMARY WORKER of STAGE if the ReqFuncCd is REQ_FUNC_CD_ASSIGN, or it
	 * will get the information related to the ID TODO specified.
	 * 
	 * @param PopulateLetterReq
	 * @return PreFillDataServiceDto
	 * @throws Exception
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto populateLetter(PopulateLetterReq populateLetterReq, boolean spanish) {

		Boolean bOpenStage = false;
		String cdStage = null;
		String cdStageProgram = null;
		String cdStageReasonClosed = null;
		Long ulIdPerson = 0L;

		PopulateLetterDto populateLetterDto = new PopulateLetterDto();
		/*
		 ** retrieves stage and caps_case table CSEC02D
		 */
		GenericCaseInfoDto genCaseInfoDto = disasterPlanDao.getGenericCaseInfo(populateLetterReq.getIdStage());

		cdStage = genCaseInfoDto.getCdStage();
		cdStageProgram = genCaseInfoDto.getCdStageProgram();
		cdStageReasonClosed = genCaseInfoDto.getCdStageReasonClosed();

		/*
		 ** retrieves letterhead info CLSC03D
		 */
		CodesTablesDto codesTablesDto = populateLetterDao
				.getPersonInfoByCode(ServiceConstants.TITLE, ServiceConstants.NAME).get(0);

		/*
		 ** retrieves principal and reporter info CLSC01D
		 */
		List<CaseInfoDto> caseInfoDtoprincipal = populateLetterDao.getCaseInfoById(populateLetterReq.getIdStage(),
				ServiceConstants.PRINCIPAL);
		/*
		 ** retrieves principal and reporter info CSEC18D
		 */
		// Warranty Defect Fix - 12154 - To set Value for the Reporter's Confidential Pop-up
		for (CaseInfoDto caseInfoDto : caseInfoDtoprincipal) {
			if (!ObjectUtils.isEmpty(caseInfoDto.getIdPerson())
					&& caseInfoDto.getIdPerson().equals(populateLetterReq.getIdPerson())
							&& ServiceConstants.PARENT_RELTN.stream().anyMatch(relation -> relation.equalsIgnoreCase(caseInfoDto.getCdStagePersRelInt())
							)) {
				populateLetterDto.setUlSysNbrGenericCntr(ServiceConstants.ZERO);
			}
		}

		List<CaseInfoDto> caseInfoDtoList = new ArrayList<CaseInfoDto>();
		caseInfoDtoList = populateLetterDao.getReporterInfoById(populateLetterReq.getIdStage());

		for (CaseInfoDto caseInfoDto : caseInfoDtoList) {
			if (!ObjectUtils.isEmpty(caseInfoDto.getIdPerson())
					&& caseInfoDto.getIdPerson().equals(populateLetterReq.getIdPerson())) {
				caseInfoDtoList.set(0, caseInfoDto);
				break;
			}
		}

		/*
		 ** Now Let the architecture function think that only one row was
		 * returned so it doesn't print multiple REPORTER addresses.
		 */

		Long addressMatch = 0L;

		if (genCaseInfoDto.getCdStageProgram().equals(ServiceConstants.STAGE_PROGRAM)) {
			// CINTA1D
			addressMatch = populateLetterDao.getAddressMatchById(populateLetterReq.getIdCase(),
					populateLetterReq.getIdPerson());
			if (!ObjectUtils.isEmpty(populateLetterDto.getUlSysNbrGenericCntr())) {
				populateLetterDto.setUlSysNbrGenericCntr(ServiceConstants.ONE_LONG);
			}
		}

		/*
		 ** retrieves closure info from STAGE_PROG table; code identifies if case
		 * is being opened or closed CallCCMNB8D
		 */

		List<StageProgDto> stageProgDtoList = stageProgDao.getStgProgroession(cdStage, cdStageProgram,
				cdStageReasonClosed);
		List<StageSituationOutDto> stageSituationOutDtoList = new ArrayList<StageSituationOutDto>();
		/*
		 ** If the closure reason is Subcare - Prot. Place or Removal/Subcare,
		 * find out if there are any open stages besides ARI. CLSS30D
		 */
		if (stageProgDtoList.size() != 0) {
			if (stageProgDtoList.get(0).getCdStageProgRsnClose() != null
					&& stageProgDtoList.get(0).getCdStageProgRsnClose().equals(ServiceConstants.SUBCAREPP)
					|| stageProgDtoList.get(0).getCdStageProgRsnClose().equals(ServiceConstants.RSUBCARE)) {
				StageSituationInDto stageSituationInDto = new StageSituationInDto();
				stageSituationInDto.setIdCase(populateLetterReq.getIdCase());
				stageSituationOutDtoList = stageSituationDao.getStageDetails(stageSituationInDto);
				for (StageSituationOutDto stageSituation : stageSituationOutDtoList) {
					Calendar cal = Calendar.getInstance();
					if (!TypeConvUtil.isNullOrEmpty(stageSituation)) {
						if (!TypeConvUtil.isNullOrEmpty(stageSituation.getDtStageClose())) {
							cal.setTime(stageSituation.getDtStageClose());
							if (stageSituation.getCdStage().equals(ServiceConstants.ARI_STAGE_CINV63S)
									&& cal.MONTH == ServiceConstants.NULL_DATE && cal.YEAR == ServiceConstants.NULL_DATE
									&& cal.DAY_OF_MONTH == ServiceConstants.NULL_DATE) {
								bOpenStage = true;
							}
						}
					}
				}

				if (bOpenStage) {
					stageProgDtoList.get(0).setIndStageProgClose(ServiceConstants.PERSON_CHAR_ONE);
				}

			}
		}

		/*
		 * SIR 26248, 26265 - when ARC_SUCCESS:
		 **
		 ** Send data to form to indicate if CASE is closed or open using
		 * tmScrTmGeneric1 which is not otherwise needed by the form.
		 **
		 ** Also - send data to form to indicate which variant of the the form to
		 * present - UTC, ADM, OPENING A CASE, CLOSING A CASE using
		 * tmScrTmGeneric9 which is not otherwise needed by the form
		 **
		 ** If overall disposition = UTC, then UTC variant over-rules any reason
		 * closed value If overall disposition = ADM, then ADM variant
		 * over-rules any reason closed value otherwise when overall disposition
		 * not UTC and not ADM, then OPENING A CASE for certain reasons closed
		 ** (driven by stage progression indicator = 1) else CLOSING A CASE for
		 * all other reasons closed. CINV95D
		 */

		CpsInvstDetailStageIdInDto cpsInvstDetailStageIdInDto = new CpsInvstDetailStageIdInDto();
		cpsInvstDetailStageIdInDto.setIdStage(populateLetterReq.getIdStage());
		List<CpsInvstDetailStageIdOutDto> CpsInvstDetailStageIdOutDtoList = cpsInvstDetailsStageIdDao
				.getInvstDtls(cpsInvstDetailStageIdInDto);
		//Modified the code the for Warranty defect 11485
		if (!TypeConvUtil.isNullOrEmpty(genCaseInfoDto)) {
			if (TypeConvUtil.isNullOrEmpty(genCaseInfoDto.getDtStageClose())) {

				caseInfoDtoList.get(0).setTmScrTmGeneric1(ServiceConstants.OPEN_CASE);
			} else {
				caseInfoDtoList.get(0).setTmScrTmGeneric1(ServiceConstants.CLOSED_CASE);
			}
		}
		
		// Warranty Defect - 12175 - Null Pointer Check
		if(!ObjectUtils.isEmpty(CpsInvstDetailStageIdOutDtoList)
				&& !ObjectUtils.isEmpty(CpsInvstDetailStageIdOutDtoList.get(0).getCdCpsOverallDisptn())
				&& !ObjectUtils.isEmpty(stageProgDtoList.get(0).getIndStageProgClose())) {

			String overallDispostion = CpsInvstDetailStageIdOutDtoList.get(0).getCdCpsOverallDisptn();
			String indClose = stageProgDtoList.get(0).getIndStageProgClose();
			// artf250175
			if (overallDispostion.equals(ServiceConstants.RULED_OUT)) {
				caseInfoDtoList.get(0).setTmScrTmGeneric9(ServiceConstants.RULED_OUT);
			} else if (overallDispostion.equals(ServiceConstants.UTC_CODE)) {
				caseInfoDtoList.get(0).setTmScrTmGeneric9(ServiceConstants.UTC_CODE);
			} else if (overallDispostion.equals(ServiceConstants.ADM_CODE)) {
				caseInfoDtoList.get(0).setTmScrTmGeneric9(ServiceConstants.ADM_CODE);
			} else if (indClose.equals(ServiceConstants.STAGE_PROG_0)) {
				caseInfoDtoList.get(0).setTmScrTmGeneric9(ServiceConstants.CLOSING_A_CASE);
			} else if (indClose.equals(ServiceConstants.STAGE_PROG_1)) {
				caseInfoDtoList.get(0).setTmScrTmGeneric9(ServiceConstants.OPENING_A_CASE);
			} else {
				caseInfoDtoList.get(0).setTmScrTmGeneric9(ServiceConstants.UNEXPECTED_CONDITION);
			}
		}
		/*
		 ** retrieve stage_person_link CallCCMNB9D
		 */
		EmployeePersPhNameDto employeePersPhNameDto = new EmployeePersPhNameDto();
		List<StagePersonLinkDto> stagePersonLinkDtoList = new ArrayList<StagePersonLinkDto>();

		stagePersonLinkDtoList = stagePersonLinkDao.getStagePersonLinkByIdStage(populateLetterReq.getIdStage());

		if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDtoList)) {
			for (StagePersonLinkDto stagePerson : stagePersonLinkDtoList) {
				if (stagePerson.getCdStagePersRole().equalsIgnoreCase(ServiceConstants.HISTORICAL_WORKER_ROLE)
						|| stagePerson.getCdStagePersRole().equalsIgnoreCase(ServiceConstants.PRIMARY_WORKER_ROLE)) {
					ulIdPerson = stagePerson.getIdPerson();
					break;

				}
			}

			/* retrieves worker info CallCSEC01D */

			employeePersPhNameDto = employeeDao.searchPersonPhoneName(ulIdPerson);
			if (!TypeConvUtil.isNullOrEmpty(employeePersPhNameDto)) {
				if (!TypeConvUtil.isNullOrEmpty(employeePersPhNameDto.getCdPhoneType())) {

					if (!(employeePersPhNameDto.getCdPhoneType().equals(ServiceConstants.BUSINESS_PHONE)
							|| employeePersPhNameDto.getCdPhoneType().equals(ServiceConstants.BUSINESS_CELL))) {
						employeePersPhNameDto.setNbrPhone(employeePersPhNameDto.getMailCodePhone());
						employeePersPhNameDto.setNbrPhoneExtension(employeePersPhNameDto.getMailCodePhoneExt());
					}
				}
			}
		}

		populateLetterDto.setGenCaseInfoDto(genCaseInfoDto);
		populateLetterDto.setCaseInfoDto(caseInfoDtoList);
		populateLetterDto.setCaseInfoDtoprincipal(caseInfoDtoprincipal);
		populateLetterDto.setCodesTablesDto(codesTablesDto);
		populateLetterDto.setCpsInvstDetailStageIdOutDtoList(CpsInvstDetailStageIdOutDtoList);
		populateLetterDto.setEmployeePersPhNameDto(employeePersPhNameDto);
		populateLetterDto.setMatchedAddress(addressMatch);
		populateLetterDto.setStagePersonLinkDtoList(stagePersonLinkDtoList);
		populateLetterDto.setStageSituationOutDtoList(stageSituationOutDtoList);
		populateLetterDto.setStageProgDtoList(stageProgDtoList);
		populateLetterDto.setSpanish(spanish);

		return populateLetterPrefillData.returnPrefillData(populateLetterDto);
	}

}
