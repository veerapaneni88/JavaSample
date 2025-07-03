package us.tx.state.dfps.service.ppm.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.admin.dao.LegalStatusPersonMaxStatusDtDao;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtInDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtOutDto;
import us.tx.state.dfps.service.childbackground.dao.ChildBackgroundDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PpmReq;
import us.tx.state.dfps.service.conservatorship.dao.RemovalReasonDao;
import us.tx.state.dfps.service.conservatorship.dto.CnsrvtrshpRemovalDto;
import us.tx.state.dfps.service.conservatorship.dto.RemovalReasonDto;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PpaDto;
import us.tx.state.dfps.service.forms.dto.PpaReviewDto;
import us.tx.state.dfps.service.forms.dto.PptDetailsOutDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.PpaPrefillData;
import us.tx.state.dfps.service.forms.util.PpaReviewPrefillData;
import us.tx.state.dfps.service.person.dto.UnitDto;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dao.PersonLocPersonDao;
import us.tx.state.dfps.service.placement.dto.EventChildPlanDto;
import us.tx.state.dfps.service.placement.dto.PersonLocInDto;
import us.tx.state.dfps.service.placement.dto.PersonLocOutDto;
import us.tx.state.dfps.service.placement.dto.PlacementAUDDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.ppm.dao.PPMDao;
import us.tx.state.dfps.service.ppm.dao.PparDao;
import us.tx.state.dfps.service.ppm.service.PparService;
import us.tx.state.dfps.service.prt.dto.PRTParticipantDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:interface
 * for Permanency Planning May 29, 2018- 4:43:37 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class PparServiceImpl implements PparService {

	@Autowired
	DisasterPlanDao disasterPlanDao;

	@Autowired
	PparDao pparDao;

	@Autowired
	PpaPrefillData ppaPrefillData;

	@Autowired
	PpaReviewPrefillData ppaReviewPrefillData;

	@Autowired
	CommonApplicationDao commonApplicationDao;

	@Autowired
	PPMDao ppmDao;

	@Autowired
	PersonLocPersonDao personLocPersonDao;

	@Autowired
	ChildBackgroundDao childBackgroundDao;

	@Autowired
	RemovalReasonDao removalReasonDao;

	@Autowired
	LegalStatusPersonMaxStatusDtDao legalStatusPersonMaxStatusDtDao;

	@Autowired
	PopulateLetterDao populateLetterDao;

	/**
	 * Method Name: getPpaCaseReview CSUB78S Method Description: Populates form
	 * csc39o00, which Populates Permanency Planning Review Invited Parties and
	 * Participation Information.
	 * 
	 * @return PreFillDataServiceDto
	 */

	@Override
	public PreFillDataServiceDto getPpaCaseReview(PpmReq ppmReq) {

		PpaDto ppaDto = new PpaDto();

		/*
		 ** Call to pCSEC02D DAM to get the Case Name, Case Id, and Case action
		 * for the Risk Assessment
		 */

		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(ppmReq.getIdStage());
		ppaDto.setGenericCaseInfoDto(genericCaseInfoDto);

		/*
		 ** Call to pCLSS05D dam to get the purpose, risk finding and the id
		 * event for the Risk Narrative
		 */

		List<PRTParticipantDto> pRTParticipantDtolist = pparDao.getPPTParticipant(ppmReq.getIdEvent());
		ppaDto.setpRTParticipantDto(pRTParticipantDtolist);
		return ppaPrefillData.returnPrefillData(ppaDto);
	}

	/**
	 * Method Name: getPpaCaseReviewAdmin CSUB51S Method Description: Populates
	 * form csc30o00,Used to record the results of the Permanency Planning
	 * Administrative Case Review for a child in substitute care.
	 * 
	 * @return PreFillDataServiceDto
	 */
	@Override
	public PreFillDataServiceDto getPpaCaseReviewAdmin(PpmReq ppmReq) {

		PlacementAUDDto placementAUDDto = null;
		PersonLocOutDto personLocOutDto = null;
		CnsrvtrshpRemovalDto cnsrvtrshpRemovalDto1 = null;
		List<RemovalReasonDto> removalResonDtoList1 = null;
		CnsrvtrshpRemovalDto cnsrvtrshpRemovalDto2 = null;
		List<RemovalReasonDto> removalResonDtoList2 = null;
		EventChildPlanDto eventChildPlanDto = null;
		List<LegalStatusPersonMaxStatusDtOutDto> legalStatusPersonMaxStatusList = null;
		String eventPerson = null;

		PpaReviewDto ppaReviewDto = new PpaReviewDto();

		/* DAM call to retrieve case info about child */
		// call DAM CSEC15D
		StagePersonLinkCaseDto stagePersonLinkCaseDto = commonApplicationDao.getStagePersonCaseDtl(ppmReq.getIdStage(),
				ServiceConstants.SPL);

		ppaReviewDto.setStagePersonLinkCaseDto(stagePersonLinkCaseDto);
		// Call CSES14D retrieves date, time, and address
		PptDetailsOutDto pPtDetailsOutDtoAddress = ppmDao.getPptAddress(ppmReq.getIdPptEvent());

		ppaReviewDto.setpPtDetailsOutDtoAddress(pPtDetailsOutDtoAddress);

		if (!ObjectUtils.isEmpty(pPtDetailsOutDtoAddress.getDtPptDate())
				&& !ObjectUtils.isEmpty(stagePersonLinkCaseDto.getIdPerson())) {

			/* DAM retrieves facility info CallCSEC32D */

			placementAUDDto = pparDao.getPlcmt(stagePersonLinkCaseDto.getIdPerson(),
					pPtDetailsOutDtoAddress.getDtPptDate());

			ppaReviewDto.setPlacementAUDDto(placementAUDDto);

			/* DAM receives authorized LOC CallCSEC33D */

			PersonLocInDto personLocInDto = new PersonLocInDto();
			personLocInDto.setIdPerson(stagePersonLinkCaseDto.getIdPerson());
			personLocInDto.setDtPlocStart(pPtDetailsOutDtoAddress.getDtPptDate());
			personLocInDto.setCdPlocType(ServiceConstants.LOC_TYPE_ALOC);
			personLocOutDto = personLocPersonDao.getPersonLocById(personLocInDto);

			ppaReviewDto.setPersonLocOutDto(personLocOutDto);

		}

		/* DAM retrieves initial removal info CallCDYN10D */

		if (!ObjectUtils.isEmpty(stagePersonLinkCaseDto.getIdPerson())) {
			cnsrvtrshpRemovalDto1 = childBackgroundDao.getRmvlDateAndRmvlEvent(stagePersonLinkCaseDto.getIdPerson(),
					ServiceConstants.STR_ZERO_VAL);

			ppaReviewDto.setCnsrvtrshpRemovalDto1(cnsrvtrshpRemovalDto1);

			/* DAM retrieves INITIAL REMOVAL REASON CallCLSS21D */

			if (!ObjectUtils.isEmpty(cnsrvtrshpRemovalDto1.getIdRemovalEvent())) {
				List<Long> idEventList = new ArrayList<Long>();
				idEventList.add(cnsrvtrshpRemovalDto1.getIdRemovalEvent());
				removalResonDtoList1 = removalReasonDao.getRemReasonDtl(idEventList);
			}

			ppaReviewDto.setRemovalResonDtoList1(removalResonDtoList1);
		}

		/* DAM retrieves most recent removal info CallCDYN10D */

		if (!ObjectUtils.isEmpty(stagePersonLinkCaseDto.getIdPerson())) {
			cnsrvtrshpRemovalDto2 = childBackgroundDao.getRmvlDateAndRmvlEvent(stagePersonLinkCaseDto.getIdPerson(),
					ServiceConstants.SYS_CARC_RQST_FUNC_CODE);

			ppaReviewDto.setCnsrvtrshpRemovalDto2(cnsrvtrshpRemovalDto2);

			/* DAM retrieves most recent REMOVAL REASON CallCLSS21D */

			if (!ObjectUtils.isEmpty(cnsrvtrshpRemovalDto2.getIdRemovalEvent())) {
				List<Long> idEventList = new ArrayList<Long>();
				idEventList.add(cnsrvtrshpRemovalDto2.getIdRemovalEvent());
				removalResonDtoList2 = removalReasonDao.getRemReasonDtl(idEventList);
			}

			ppaReviewDto.setRemovalResonDtoList2(removalResonDtoList2);

			for (RemovalReasonDto removalReasonDto : removalResonDtoList2) {
				if (cnsrvtrshpRemovalDto1.getIdRemovalEvent() == cnsrvtrshpRemovalDto2.getIdRemovalEvent()) {
					cnsrvtrshpRemovalDto2.setDtRemoval(null);
					removalReasonDto.setCdRemovalReason(null);
				}

			}
		}

		/* DAM retrieves permanent goal info CallCSEC20D */

		if (!ObjectUtils.isEmpty(stagePersonLinkCaseDto.getIdPerson())) {
			eventChildPlanDto = commonApplicationDao.getEventChildPlanDtls(stagePersonLinkCaseDto.getIdPerson());

			ppaReviewDto.setEventChildPlanDto(eventChildPlanDto);

			/* DAM retrieves current legal status info CallCSES32D */

			LegalStatusPersonMaxStatusDtInDto inputRec = new LegalStatusPersonMaxStatusDtInDto();
			inputRec.setIdPerson(stagePersonLinkCaseDto.getIdPerson());
			legalStatusPersonMaxStatusList = legalStatusPersonMaxStatusDtDao.getRecentLegelStatusRecord(inputRec);

			ppaReviewDto.setLegalStatusPersonMaxStatusList(legalStatusPersonMaxStatusList);

			/* DAM retrieves current legal status info CallCSEC22D */
			eventPerson = pparDao.getEventPerson(stagePersonLinkCaseDto.getIdPerson());

			ppaReviewDto.setEventPerson(eventPerson);
		}

		/* DAM retrieves UNIT info CallCSEC19D */

		UnitDto unitDto = pparDao.getUnitInfo(ppmReq.getIdStage());

		ppaReviewDto.setUnitDto(unitDto);

		/* DAM retrieves prior PPT date info CallCSEC31D */

		PptDetailsOutDto pptDetailsOutDto = pparDao.getEventPpt(ppmReq.getIdStage());

		ppaReviewDto.setPptDetailsOutDto(pptDetailsOutDto);

		/* DAM retrieves data for principals list CallCLSC01D */

		List<CaseInfoDto> caseInfoDtoList = populateLetterDao.getPPMCaseInfoById(ppmReq.getIdStage(),
				ServiceConstants.PRINCIPAL);

		ppaReviewDto.setCaseInfoDtoList(caseInfoDtoList);

		return ppaReviewPrefillData.returnPrefillData(ppaReviewDto);
	}

}
