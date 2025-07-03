package us.tx.state.dfps.service.riskassessment.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.InvstActionQuestion;
import us.tx.state.dfps.common.domain.RiskAssessment;
import us.tx.state.dfps.common.domain.RiskFactors;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.riskandsafetyassmt.dto.EventUpdateDto;
import us.tx.state.dfps.riskandsafetyassmt.dto.InvstActionQuestionDto;
import us.tx.state.dfps.riskandsafetyassmt.dto.NarrativeDto;
import us.tx.state.dfps.riskandsafetyassmt.dto.PrincipalListResDto;
import us.tx.state.dfps.riskandsafetyassmt.dto.RiskFactorDto;
import us.tx.state.dfps.riskandsafetyassmt.dto.RiskFactorRtrvResDto;
import us.tx.state.dfps.riskandsafetyassmt.dto.SafetyFactorEvalDto;
import us.tx.state.dfps.riskandsafetyassmt.dto.SafteyEvalResDto;
import us.tx.state.dfps.service.SDM.dao.SafetyEvalDao;
import us.tx.state.dfps.service.admin.dao.AllegationDtlsDao;
import us.tx.state.dfps.service.admin.dao.EventStagePersonLinkInsUpdDao;
import us.tx.state.dfps.service.admin.dao.RiskAssmtNarrOrIraDao;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdInDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdOutDto;
import us.tx.state.dfps.service.admin.dto.RiskAssessmentFactorDto;
import us.tx.state.dfps.service.admin.dto.RiskAssmtNarrOrIraInDto;
import us.tx.state.dfps.service.admin.dto.RiskAssmtNarrOrIraOutDto;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.EventReq;
import us.tx.state.dfps.service.common.request.RiskAssessmentReq;
import us.tx.state.dfps.service.common.response.RiskAssessmentRes;
import us.tx.state.dfps.service.common.response.RiskFactorRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cpsinvstsummary.dao.CpsInvstSummaryDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.dto.RiskAssessmentNarrativeDto;
import us.tx.state.dfps.service.forms.util.RiskAssessmentInvPrefillData;
import us.tx.state.dfps.service.forms.util.RiskAssessmentPrefillData;
import us.tx.state.dfps.service.investigation.dto.CpsInvstDetailDto;
import us.tx.state.dfps.service.pca.dao.PcaDao;
import us.tx.state.dfps.service.pca.dto.StageCaseDtlDto;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonGenderSpanishDto;
import us.tx.state.dfps.service.populateform.dao.PopulateFormDao;
import us.tx.state.dfps.service.riskassesment.dto.AllegationNameDtlDto;
import us.tx.state.dfps.service.riskassesment.dto.InvActionDtlDto;
import us.tx.state.dfps.service.riskassesment.dto.RiskAssmtDtlDto;
import us.tx.state.dfps.service.riskassesment.dto.RiskFactorsDto;
import us.tx.state.dfps.service.riskassessment.dao.RiskAssessmentDao;
import us.tx.state.dfps.service.riskassessment.service.RiskAssessmentService;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.EventListDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: To
 * implement the operations of RiskAssessmentService Mar 15, 2018- 10:29:03 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class RiskAssessmentServiceImpl implements RiskAssessmentService {

	@Autowired
	private PcaDao pcaDao;

	@Autowired
	private RiskAssessmentDao riskAssessmentDao;

	@Autowired
	private AllegationDtlsDao allegationDtlsDao;

	@Autowired
	private RiskAssessmentInvPrefillData prefillData1;

	@Autowired
	private RiskAssessmentPrefillData prefillData2;

	@Autowired
	private EventStagePersonLinkInsUpdDao eventStagePersonLinkInsUpdDao;

	@Autowired
	private EventDao eventDao;

	@Autowired
	private StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	private SafetyEvalDao safetyEvalDao;

	@Autowired
	private PersonDao personDao;

	@Autowired
	private PopulateFormDao populateFormDao;

	@Autowired
	private RiskAssmtNarrOrIraDao riskAssmtNarrOrIraDao;

	@Autowired
	private CpsInvstSummaryDao cpsInvstSummaryDao;

	/**
	 * Service Name: CINV77S Method Name: getRiskAssmntData Method Description:
	 * This service will get forms populated by receiving idStage from
	 * controller, then retrieving data from caps_case, stage to get the forms
	 * populated.
	 *
	 * @param idStage
	 * @return RiskAssessmentNarrativeDto @ the service exception
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getRiskAssmntData(Long idStage) {

		RiskAssessmentNarrativeDto riskAssessmentNarrativeDto = new RiskAssessmentNarrativeDto();
		StageCaseDtlDto stageCaseDtlDto = null;
		List<RiskAssessmentFactorDto> riskAssessmentFactorDtoList = null;

		// CSEC02D
		stageCaseDtlDto = pcaDao.getStageAndCaseDtls(idStage);
		// CINV14D
		riskAssessmentFactorDtoList = riskAssessmentDao.getRiskAssessmentFactorDtls(idStage);

		riskAssessmentNarrativeDto.setStageCaseDtlDto(stageCaseDtlDto);
		riskAssessmentNarrativeDto.setRiskAssessmentFactorDtoList(riskAssessmentFactorDtoList);
		return prefillData2.returnPrefillData(riskAssessmentNarrativeDto);
	}

	/**
	 * Service Name: CINV84S Method Name: getRiskAssmntInvData Method
	 * Description: This service will get forms populated by receiving idEvent
	 * and idStage from controller, then retrieving data from caps_case, stage
	 * etc tables to get the forms populated.
	 *
	 * @param idStage
	 * @param idEvent
	 * @return RiskAssessmentNarrativeDto @ the service exception
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getRiskAssmntInvData(Long idStage) {

		RiskAssessmentNarrativeDto riskAssessmentNarrativeDto = new RiskAssessmentNarrativeDto();
		StageCaseDtlDto stageCaseDtlDto = null;
		List<RiskAssessmentFactorDto> riskAssessmentFactorDtoList = null;
		List<RiskFactorsDto> riskFactorsDtoList = null;
		List<AllegationNameDtlDto> allegationNameDtlDtoList = null;

		// CSEC02D
		stageCaseDtlDto = pcaDao.getStageAndCaseDtls(idStage);
		// CINV14D
		riskAssessmentFactorDtoList = riskAssessmentDao.getRiskAssessmentFactorDtls(idStage);
		// CSEC76D
		if (!ObjectUtils.isEmpty(riskAssessmentFactorDtoList)) {
			for (RiskAssessmentFactorDto riskAssessmentFactorDto : riskAssessmentFactorDtoList) {
				if (!ObjectUtils.isEmpty(riskAssessmentFactorDto.getIdEvent())) {
					riskFactorsDtoList = riskAssessmentDao.getRiskFactorsDtls(riskAssessmentFactorDto.getIdEvent());
				}
			}
		}
		// CSES90D
		allegationNameDtlDtoList = allegationDtlsDao.getAllegationNameDtls(idStage);

		riskAssessmentNarrativeDto.setStageCaseDtlDto(stageCaseDtlDto);
		riskAssessmentNarrativeDto.setRiskAssessmentFactorDtoList(riskAssessmentFactorDtoList);
		riskAssessmentNarrativeDto.setRiskFactorsDtoList(riskFactorsDtoList);
		riskAssessmentNarrativeDto.setAllegationNameDtlDtoList(allegationNameDtlDtoList);
		return prefillData1.returnPrefillData(riskAssessmentNarrativeDto);
	}

	/**
	 * Service Name: cinv02s Method Name: getInvActionDetails Method
	 * Description: This service Retrieves information for the Inv Action Window
	 *
	 * @param idStage
	 * @param idEvent
	 * @return RtrvInvActQuestResDto
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public InvActionDtlDto getInvActionDetails(Long idStage, Long idEvent, String cdReqFunction) {

		InvActionDtlDto invActionDtlDto = new InvActionDtlDto();

		// Call CCMN87D
		if (!ObjectUtils.isEmpty(idStage)) {
			EventStagePersonLinkInsUpdInDto eventStagePersonLinkInsUpdInDto = new EventStagePersonLinkInsUpdInDto();
			eventStagePersonLinkInsUpdInDto.setIdStage(idStage);
			eventStagePersonLinkInsUpdInDto.setCdReqFunction(cdReqFunction);
			eventStagePersonLinkInsUpdInDto.setCdEventType(ServiceConstants.CONCLUSION_EVENT);
			List<EventStagePersonLinkInsUpdOutDto> eventStagePersonLinkInsUpList = eventStagePersonLinkInsUpdDao
					.getEventAndStatusDtls(eventStagePersonLinkInsUpdInDto);

			if (!ObjectUtils.isEmpty(eventStagePersonLinkInsUpList.get(ServiceConstants.Zero_INT).getCdEventStatus())
					&& !eventStagePersonLinkInsUpList.get(0).getCdEventStatus()
							.equals(ServiceConstants.PENDING_EVENT_STATUS)) {
				invActionDtlDto.setIdEvent(eventStagePersonLinkInsUpList.get(0).getIdEvent());
			} else {
				invActionDtlDto.setIdEvent(ServiceConstants.ZERO_VAL);
			}

		}

		// Call CCMN45D
		if (!ObjectUtils.isEmpty(idEvent)) {
			EventDto eventDto = eventDao.getEventByid(idEvent);
			invActionDtlDto.setEventDto(eventDto);

			// Call CINV04D
			List<InvstActionQuestion> invstActionQuestionList = riskAssessmentDao.getInvstActionQuestions(idEvent);

			List<InvstActionQuestionDto> invstActionQuestionDtoList = new ArrayList<InvstActionQuestionDto>();

			populateInvstActionQuestionDto(invstActionQuestionList, invstActionQuestionDtoList);

			invActionDtlDto.setInvstActionQuestionList(invstActionQuestionDtoList);

		}

		return invActionDtlDto;
	}

	/**
	 * Service Name: cinv51s Method Name: getRiskFactorDetails Method
	 * Description: A retrieval service which obtains risk factors for either a
	 * Principal or an Incident type from the RISK FACTORS table. The service
	 * also returns the current time stamp for the ID EVENT on the Event table.
	 *
	 * @param idStage
	 * @param idEvent
	 * @return RiskFactorRtrvResDto
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RiskFactorRes getRiskFactorDetails(Long idPerson, Long idEvent, String getData) {

		RiskFactorRes riskFactorRes = new RiskFactorRes();
		RiskFactorRtrvResDto riskFactorRtrvResDto = new RiskFactorRtrvResDto();

		// Call CCMN45D

		EventDto eventDto = eventDao.getEventByid(idEvent);

		if (!ObjectUtils.isEmpty(eventDto)) {
			EventUpdateDto eventUpdateDto = new EventUpdateDto();
			eventUpdateDto.setCdEventStatus(eventDto.getCdEventStatus());
			eventUpdateDto.setDtLastUpdate(eventDto.getDtLastUpdate());
			eventUpdateDto.setCdTask(eventDto.getCdTask());
			eventUpdateDto.setCdEventType(eventDto.getCdEventType());
			if (!ObjectUtils.isEmpty(eventDto.getIdPerson().intValue())) {
				eventUpdateDto.setIdPerson(eventDto.getIdPerson().intValue());
				eventUpdateDto.setHasIdPerson(ServiceConstants.TRUEVAL);
			}
			if (!ObjectUtils.isEmpty(eventDto.getIdEvent().intValue())) {
				eventUpdateDto.setIdEvent(eventDto.getIdEvent().intValue());
				eventUpdateDto.setHasIdEvent(ServiceConstants.TRUEVAL);
			}
			if (!ObjectUtils.isEmpty(eventDto.getIdStage().intValue())) {
				eventUpdateDto.setIdStage(eventDto.getIdStage().intValue());
				eventUpdateDto.setHasIdStage(ServiceConstants.TRUEVAL);
			}
			eventUpdateDto.setDtEventOccurred(eventDto.getDtEventOccurred());
			riskFactorRtrvResDto.setEventUpdateDto(eventUpdateDto);
		}

		// Call CINV65D
		if (!ObjectUtils.isEmpty(eventDto)
				// &&
				// eventDto.getCdEventStatus().equals(ServiceConstants.EVENTSTATUS_NEW)
				&& getData.equals(ServiceConstants.Y)) {
			List<RiskFactors> riskFactorsList = riskAssessmentDao.getRiskFactor(idEvent, idPerson);

			List<RiskFactorDto> RiskFactorDtoList = new ArrayList<RiskFactorDto>();

			populateRiskFactorsDto(riskFactorsList, RiskFactorDtoList);

			riskFactorRtrvResDto.setRiskFactorDtoList(RiskFactorDtoList);
		}

		// CCMND2D
		List<PersonDto> personDtoList = eventDao.getPersonFromEventPlanLinkByIdEvent(idEvent);
		riskFactorRes.setPersonDtoList(personDtoList);

		riskFactorRes.setRiskFactorRtrvRes(riskFactorRtrvResDto);

		return riskFactorRes;
	}

	private void populateRiskFactorsDto(List<RiskFactors> riskFactorsList, List<RiskFactorDto> riskFactorDtoList) {
		if (!ObjectUtils.isEmpty(riskFactorsList)) {
			for (RiskFactors riskFactors : riskFactorsList) {
				RiskFactorDto riskFactorDto = new RiskFactorDto();
				riskFactorDto.setCdRiskFactor(riskFactors.getCdRiskFactor());
				riskFactorDto.setCdRiskFactorCateg(riskFactors.getCdRiskFactorCateg());
				riskFactorDto.setCdRiskFactorResponse(riskFactors.getCdRiskFactorResponse());
				riskFactorDto.setDtLastUpdate(riskFactors.getDtLastUpdate());
				if (!ObjectUtils.isEmpty(riskFactors.getTxtRiskFactorComment())) {
					riskFactorDto.setRiskFactorComment(riskFactors.getTxtRiskFactorComment());
				}
				riskFactorDto.setIdRiskFactor((int) riskFactors.getIdRiskFactor());
				riskFactorDtoList.add(riskFactorDto);

			}
		}

	}

	private void populateInvstActionQuestionDto(List<InvstActionQuestion> invstActionQuestionList,
			List<InvstActionQuestionDto> invstActionQuestionDtoList) {
		if (!ObjectUtils.isEmpty(invstActionQuestionList)) {
			for (InvstActionQuestion invstActionQuestion : invstActionQuestionList) {
				InvstActionQuestionDto invstActionQuestionDto = new InvstActionQuestionDto();

				invstActionQuestionDto.setCdInvstActionAns(invstActionQuestion.getCdInvstActionAns());
				invstActionQuestionDto.setCdInvstActionQuest(invstActionQuestion.getCdInvstActionQuest());
				invstActionQuestionDto.setDtLastUpdate(invstActionQuestion.getDtLastUpdate());
				invstActionQuestionDto
						.setHasIdInvstActionQuest(!ObjectUtils.isEmpty(invstActionQuestion.getCdInvstActionQuest())
								? ServiceConstants.TRUEVAL : ServiceConstants.FALSEVAL);
				if (!ObjectUtils.isEmpty(invstActionQuestion.getTxtInvstActionCmnts())) {
					invstActionQuestionDto.setInvstActionCmnts(invstActionQuestion.getTxtInvstActionCmnts());
				}
				invstActionQuestionDtoList.add(invstActionQuestionDto);

			}
		}

	}

	/**
	 * Service Name: cinv00s Method Name: getSafetyEval Method Description: This
	 * service calls 2 DAMS to retrieve the information for the Safety
	 * Evaluation window. The first service gets information from the Safety
	 * Evaluation Table to be used for entry fields. The second DAM is a list
	 * that will retieve the Safety Eval Factor information for the safety
	 * factor list box. The service also retrieves a full row from the event
	 * table, and also the status of the Conclusion event for this stage. IT
	 * also calls a DAM to determine if the ID Event passed in corresponds to
	 * the most recent Safety Evaluation and sets a flag accordingly
	 *
	 * @param idStage
	 * @param idEvent
	 * @return RiskFactorRtrvResDto
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SafteyEvalResDto getSafetyEval(CommonHelperReq commonHelperReq) {

		SafteyEvalResDto safteyEvalResDto = new SafteyEvalResDto();
		// Call CCMN87D - Retrieve the Conclusion Event ID for the stage
		if (!ObjectUtils.isEmpty(commonHelperReq.getIdStage())) {
			EventStagePersonLinkInsUpdInDto eventStagePersonLinkInsUpdInDto = new EventStagePersonLinkInsUpdInDto();
			eventStagePersonLinkInsUpdInDto.setIdStage(commonHelperReq.getIdStage());
			eventStagePersonLinkInsUpdInDto.setCdReqFunction(ServiceConstants.UPDATE);
			eventStagePersonLinkInsUpdInDto.setCdEventType(ServiceConstants.CONCLUSION_EVENT);
			List<EventStagePersonLinkInsUpdOutDto> eventStagePersonLinkInsUpList = eventStagePersonLinkInsUpdDao
					.getEventAndStatusDtls(eventStagePersonLinkInsUpdInDto);

			if (!ObjectUtils.isEmpty(eventStagePersonLinkInsUpList.get(0).getCdEventStatus())
					&& !eventStagePersonLinkInsUpList.get(0).getCdEventStatus()
							.equals(ServiceConstants.PENDING_EVENT_STATUS)) {
				safteyEvalResDto.setIdEvent(eventStagePersonLinkInsUpList.get(0).getIdEvent().intValue());
			} else {
				safteyEvalResDto.setIdEvent(ServiceConstants.Zero);
			}

		}

		// Call CCMN45D
		if (!ObjectUtils.isEmpty(commonHelperReq.getIdEvent())) {

			EventDto eventDto = eventDao.getEventByid(commonHelperReq.getIdEvent());

			if (!ObjectUtils.isEmpty(eventDto)
					&& !eventDto.getCdEventStatus().equals(ServiceConstants.EVENTSTATUS_NEW)) {

				// Call CINV00D
				safteyEvalResDto = safetyEvalDao.getSafetyEval(commonHelperReq.getIdEvent());

				// Call CINV01D
				List<SafetyFactorEvalDto> safetyFactorEvalDtoList = safetyEvalDao
						.getSafetyEvalFactors(commonHelperReq.getIdEvent());
				safteyEvalResDto.setSafetyFactorEvalDtoList(safetyFactorEvalDtoList);

				// Call CINVA2D
				Long recentSafetyEvalEvent = safetyEvalDao.getRecentSafetyEval(commonHelperReq.getIdEvent(),
						commonHelperReq.getIdStage());
				if (!ObjectUtils.isEmpty(recentSafetyEvalEvent)) {
					safteyEvalResDto.setHasIdEvent(recentSafetyEvalEvent.equals(commonHelperReq.getIdEvent())
							? ServiceConstants.TRUE_VALUE : ServiceConstants.MOBILE_IMPACT);
				}

			}

		}

		return safteyEvalResDto;
	}

	/**
	 * Service Name: CINV36S Method Name: getPrincipalList Method Description: A
	 * retrieval service to fill the Principal list box on the Risk Assessment
	 * window.
	 *
	 * @param CommonHelperReq
	 * @return PrincipalListResDto
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PrincipalListResDto getPrincipalList(CommonHelperReq commonHelperReq) {

		PrincipalListResDto principalListResDto = new PrincipalListResDto();

		List<PersonDto> personDtoList = new ArrayList<PersonDto>();

		List<PersonDto> ccmnpersonDtoList = new ArrayList<PersonDto>();

		// Call CINV80D
		List<StagePersonLink> stagePersonLinkList = stagePersonLinkDao
				.getStagePersonLinkDtl(commonHelperReq.getIdStage(), ServiceConstants.PRINCIPAL);

		// Call CSES87D
		principalListResDto.setIndNoDataFound(personDao.getActiveFosterParent(commonHelperReq.getIdPerson(),
				commonHelperReq.getIdStage(), ServiceConstants.ACTIVE_VAL, ServiceConstants.FOSTER_ADOPTIVE_HOME));

		// Call CINV81D
		if (!ObjectUtils.isEmpty(stagePersonLinkList)) {
			for (StagePersonLink stagePersonLink : stagePersonLinkList) {

				// With each ID PERSON, get their full name and age from the
				// PERSON table.
				PersonDto personDto = new PersonDto();
				personDto.setIdPerson(stagePersonLink.getIdPerson());
				PersonGenderSpanishDto personGenderSpanishDto = populateFormDao
						.isSpanGender(stagePersonLink.getIdPerson());
				if (!ObjectUtils.isEmpty(personGenderSpanishDto.getNmPersonFull())) {
					personDto.setNmPersonFull(personGenderSpanishDto.getNmPersonFull());
				}
				if (!ObjectUtils.isEmpty(personGenderSpanishDto.getNbrPersonAge())) {
					personDto.setNbrPersonAge(personGenderSpanishDto.getNbrPersonAge().shortValue());
				}
				if (!ObjectUtils.isEmpty(personGenderSpanishDto.getDtPersonBirth())) {
					personDto.setDtPersonBirth(personGenderSpanishDto.getDtPersonBirth());
				}

				personDtoList.add(personDto);
			}

		}

		// CCMN45D
		EventDto eventDto = eventDao.getEventByid(commonHelperReq.getIdEvent());

		if (!ObjectUtils.isEmpty(eventDto)) {
			EventUpdateDto eventUpdateDto = new EventUpdateDto();
			eventUpdateDto.setCdEventStatus(eventDto.getCdEventStatus());
			eventUpdateDto.setDtLastUpdate(eventDto.getDtLastUpdate());
			eventUpdateDto.setCdTask(eventDto.getCdTask());
			eventUpdateDto.setCdEventType(eventDto.getCdEventType());
			if (!ObjectUtils.isEmpty(eventDto.getIdPerson().intValue())) {
				eventUpdateDto.setIdPerson(eventDto.getIdPerson().intValue());
				eventUpdateDto.setHasIdPerson(ServiceConstants.TRUEVAL);
			}
			if (!ObjectUtils.isEmpty(eventDto.getIdEvent().intValue())) {
				eventUpdateDto.setIdEvent(eventDto.getIdEvent().intValue());
				eventUpdateDto.setHasIdEvent(ServiceConstants.TRUEVAL);
			}
			if (!ObjectUtils.isEmpty(eventDto.getIdStage().intValue())) {
				eventUpdateDto.setIdStage(eventDto.getIdStage().intValue());
				eventUpdateDto.setHasIdStage(ServiceConstants.TRUEVAL);
			}
			eventUpdateDto.setDtEventOccurred(eventDto.getDtEventOccurred());
			principalListResDto.setEventUpdateDto(eventUpdateDto);
		}

		/* Grab all Persons associated with the event */
		if (eventDto.getCdEventStatus().equals(ServiceConstants.EVENTSTATUS_NEW)) {
			// CCMND2D
			ccmnpersonDtoList = eventDao.getPersonFromEventPlanLinkByIdEvent(commonHelperReq.getIdEvent());
			if (!ObjectUtils.isEmpty(ccmnpersonDtoList)) {
				for (PersonDto personDto : ccmnpersonDtoList) {
					personDtoList.add(personDto);
				}
			}
		}

		// Call CINV64D
		List<RiskAssessment> riskAssessmentList = riskAssessmentDao.getRiskAssessment(commonHelperReq.getIdEvent());
		RiskAssessment riskAssessment = riskAssessmentList.get(ServiceConstants.Zero_INT);
		if (!ObjectUtils.isEmpty(riskAssessment)) {
			principalListResDto.setCdRiskAssmtApAccess(riskAssessment.getIndRiskAssmtApAccess());
			principalListResDto.setCdRiskAssmtPurpose(riskAssessment.getCdRiskAssmtPurpose());
			principalListResDto.setCdRiskAssmtRiskFind(riskAssessment.getCdRiskAssmtRiskFind());
			principalListResDto.setDtLastUpdate(riskAssessment.getDtLastUpdate());
		}

		// CSYS13D
		RiskAssmtNarrOrIraInDto riskAssmtNarrOrIraInDto = new RiskAssmtNarrOrIraInDto();
		riskAssmtNarrOrIraInDto.setIdEvent(commonHelperReq.getIdEvent());
		riskAssmtNarrOrIraInDto.setSysTxtTablename("RISK_ASSMT_NARR");
		if (!TypeConvUtil.isNullOrEmpty(riskAssmtNarrOrIraInDto)) {
			List<RiskAssmtNarrOrIraOutDto> riskAssmtNarrOrIraOutDtoList = riskAssmtNarrOrIraDao
					.getEventDtls(riskAssmtNarrOrIraInDto);

			if (!ObjectUtils.isEmpty(riskAssmtNarrOrIraOutDtoList)) {
				RiskAssmtNarrOrIraOutDto riskAssmtNarrOrIraOutDto = riskAssmtNarrOrIraOutDtoList
						.get(ServiceConstants.Zero_INT);
				NarrativeDto narrativeDto = new NarrativeDto();
				narrativeDto.setDtLastUpdate(riskAssmtNarrOrIraOutDto.getTsLastUpdate());
				narrativeDto.setNarrStatus(ServiceConstants.TXT_NARR_EXISTS);
				principalListResDto.setNarrativeDto(narrativeDto);
			}

		}

		// Call CINV95D
		if (commonHelperReq.getCdStage().equals(ServiceConstants.CSTAGES_INV)) {
			List<CpsInvstDetailDto> cpsInvstDetailList = cpsInvstSummaryDao
					.getCpsInvstDetail(commonHelperReq.getIdStage());
			if (!ObjectUtils.isEmpty(cpsInvstDetailList)) {
				CpsInvstDetailDto cpsInvstDetailDto = cpsInvstDetailList.get(ServiceConstants.Zero_INT);
				principalListResDto.setCdCpsOverallDisptn(cpsInvstDetailDto.getCdCpsOverallDisptn());
			}

		}

		principalListResDto.setPersonDtoList1(personDtoList);
		principalListResDto.setPersonDtoList2(personDtoList);

		return principalListResDto;
	}

	/**
	 * Method Name: queryRiskAssmtExists Method Description:Query the Risk
	 * Assessment to check if Risk Assessment already exists.
	 * 
	 * @param CommonHelperReq
	 * @return RiskAssessmentRes
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RiskAssessmentRes queryRiskAssmtExists(RiskAssessmentReq riskAssessmentReq) {

		RiskAssessmentRes riskAssessmentRes = null;

		RiskAssmtDtlDto riskAssmtDtlDto = riskAssessmentDao.queryRiskAssmtExists(riskAssessmentReq.getIdStage(),
				riskAssessmentReq.getIdCase());

		if (!ObjectUtils.isEmpty(riskAssmtDtlDto)) {
			riskAssessmentRes = new RiskAssessmentRes();
			riskAssessmentRes.setEventId(riskAssmtDtlDto.getIdEvent().intValue());
			riskAssessmentRes.setEventStatus(riskAssmtDtlDto.getCdEventStatus());

			riskAssessmentRes.setVersionNBR((riskAssmtDtlDto.getNbrVersion() == ServiceConstants.ZERO)
					? ServiceConstants.NBR_VERSION_2 : riskAssmtDtlDto.getNbrVersion().intValue());
		}

		return riskAssessmentRes;
	}

	/**
	 * Method Name: checkRiskAssmtTaskCode SIR 24696, Check the stage table to
	 * see if INV stage is closed and event table, if it has task code for Risk
	 * Assessment.
	 * 
	 * @param CommonHelperReq
	 * @return RiskAssessmentRes
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RiskAssessmentRes checkRiskAssmtTaskCode(RiskAssessmentReq riskAssessmentReq) {

		RiskAssessmentRes riskAssessmentRes = new RiskAssessmentRes();

		RiskAssmtDtlDto riskAssmtDtlDto = riskAssessmentDao.checkRiskAssmtTaskCode(riskAssessmentReq.getIdStage(),
				riskAssessmentReq.getIdCase(), riskAssessmentReq.getIdEvent());

		if (!ObjectUtils.isEmpty(riskAssmtDtlDto)) {
			riskAssessmentRes.setEventId(riskAssmtDtlDto.getIdEvent().intValue());
			riskAssessmentRes.setCdTask(riskAssmtDtlDto.getCdTask());
			riskAssessmentRes.setIndINVStageClose(riskAssmtDtlDto.getIndStageClose());

		}

		return riskAssessmentRes;
	}

	/**
	 * Method Name: checkIfRiskAssmtCreatedUsingIRA Query the
	 * IND_RISK_ASSMT_INTRANET column on the RISK_ASSESSMENT table to determine
	 * if the Risk Assessment was created using IRA or IMPACT. has task code for
	 * Risk Assessment.
	 * 
	 * @param CommonHelperReq
	 * @return RiskAssessmentRes
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RiskAssessmentRes checkIfRiskAssmtCreatedUsingIRA(RiskAssessmentReq riskAssessmentReq) {

		RiskAssessmentRes riskAssessmentRes = new RiskAssessmentRes();

		RiskAssmtDtlDto riskAssmtDtlDto = riskAssessmentDao
				.checkIfRiskAssmtCreatedUsingIRA(riskAssessmentReq.getIdStage(), riskAssessmentReq.getIdCase());

		if (!ObjectUtils.isEmpty(riskAssmtDtlDto)) {
			riskAssessmentRes.setEventId(riskAssmtDtlDto.getIdEvent().intValue());
			riskAssessmentRes.setCdTask(riskAssmtDtlDto.getCdTask());

			if (riskAssmtDtlDto.getIndRiskAssmtIntranet().equals(ServiceConstants.IND_RISK_ASSMT_INTRANET_Y)
					|| riskAssmtDtlDto.getIndRiskAssmtIntranet().equals(ServiceConstants.IND_RISK_ASSMT_INTRANET_M)) {
				riskAssessmentRes.setCreatedUsingIRAorIMPACT(ServiceConstants.TRUE_VALUE);
			}

		}

		return riskAssessmentRes;
	}

	/**
	 * Method Name: getCurrentEventStatus Method Description : Returns the
	 * current Event status
	 * 
	 * @param RiskAssessmentReq
	 * @return RiskAssessmentRes
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RiskAssessmentRes getCurrentEventStatus(RiskAssessmentReq riskAssessmentReq) {

		RiskAssessmentRes riskAssessmentRes = new RiskAssessmentRes();

		EventReq eventReq = new EventReq();
		eventReq.setUlIdCase(riskAssessmentReq.getIdCase());
		eventReq.setUlIdStage(riskAssessmentReq.getIdStage());
		eventReq.setSzCdTask(ServiceConstants.INV_INITIAL_SAFETY_ASSESSMENT_TASK_CODE);
		List<String> eventType = new ArrayList<String>();
		eventType.add(ServiceConstants.SAFETY_EVAL_EVENT_TYPE);
		eventReq.setEventType(eventType);
		List<EventListDto> event = eventDao.getEventDetails(eventReq);

		if (!ObjectUtils.isEmpty(event)) {
			riskAssessmentRes.setEventStatus(event.get(ServiceConstants.Zero_INT).getCdEventStatus());
		}

		return riskAssessmentRes;
	}

}