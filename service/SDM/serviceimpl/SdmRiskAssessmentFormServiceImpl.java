package us.tx.state.dfps.service.SDM.serviceimpl;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.SdmRiskAssmtFormDto;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.service.SDM.dao.SDMRiskAssessmentDao;
import us.tx.state.dfps.service.SDM.service.SdmRiskAssessmentFormService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.SDMRiskAssessmentReq;
import us.tx.state.dfps.service.common.util.StringUtil;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.SdmRiskAssmtFormPrefillData;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentAnswerDto;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentDto;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentFollowupDto;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentQuestionDto;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssmtSecondaryFollowupDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.sdmriskassessment.dto.OptionDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:SdmRiskAssessmentFormServiceImpl will implemented all operation
 * defined in SdmRiskAssessmentFormService Interface related
 * SdmRiskAssessmentForm module. May 04, 2018- 2:01:28 PM © 2017 Texas
 * Department of Family and Protective Services
 */

@Service
@Transactional
public class SdmRiskAssessmentFormServiceImpl implements SdmRiskAssessmentFormService {

	@Autowired
	CaseUtils caseUtils;

	@Autowired
	SDMRiskAssessmentDao sDMRiskAssessmentDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	LookupDao lookupDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	SdmRiskAssmtFormPrefillData sdmRiskAssmtFormPrefillData;
	
	public static final String NEW_SDM_RISK_ASSMT_TASK_CODE = "9998";

	/**
	 * 
	 * Method Name: getSdmRiskAssmtFormInfo Method Description: Method
	 * Description : Returns Risk Assessment DataBean based on Risk Assessment
	 * Event Id and Stage Id It puuls back the questions, answers, followups,
	 * secondary followups and responses
	 * 
	 * @param sDMRiskAssessmentReq
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getSdmRiskAssmtFormInfo(SDMRiskAssessmentReq sDMRiskAssessmentReq) {
		Long sdmraEvent = ServiceConstants.ZERO_VAL;
		String formtxt1 = null;
		String formtxt2 = null;
		String caretakerName = null;
		SdmRiskAssmtFormDto sdmRiskAssmtFormDto = new SdmRiskAssmtFormDto();
		StageDto stageDto = stageDao.getStageById(sDMRiskAssessmentReq.getIdStage());
		List<EventDto> riskEventList = eventDao.getCpsEventByStageIdAndTaskCode(sDMRiskAssessmentReq.getIdStage(),
				ServiceConstants.SDM_RISK_ASSMT_TASK);
		EventDto riskEvent = null;
		
		// Warranty Defect - 12194 - To pull the Modernised SDM Risk Assessment
		if (ObjectUtils.isEmpty(riskEventList)) {
			riskEventList = eventDao.getCpsEventByStageIdAndTaskCode(sDMRiskAssessmentReq.getIdStage(),
					NEW_SDM_RISK_ASSMT_TASK_CODE);		
		}
		
		 /*Defect # 9159 Code changes for the right Risk Assessment to be pulled */

		if(!ObjectUtils.isEmpty(riskEventList)&&sDMRiskAssessmentReq.getIdEvent()!=null){
 			Optional<EventDto> riskEventOptional=riskEventList.stream()
 					.filter(filterRiskEvent->filterRiskEvent.getIdEvent().equals(sDMRiskAssessmentReq.getIdEvent()))
					.findFirst();
 			if(riskEventOptional.isPresent()){
 				riskEvent = riskEventOptional.get();
     			sdmraEvent = riskEvent.getIdEvent();
 			}
 		} else if (!ObjectUtils.isEmpty(riskEventList)&&riskEvent==null) {
 			//Event id of SDM Risk assessment for the household selected in conclusion page
 			Long idEvent=sDMRiskAssessmentDao.getStageHouseholdSDMRAEvent(sDMRiskAssessmentReq.getIdStage());
 			//ALM ID : 14365 : above method will return null when there is no Risk Assessment.
 			if(idEvent != null) {
	 			Optional<EventDto> riskEventOptional=riskEventList.stream()
	 					.filter(filterRiskEvent->filterRiskEvent.getIdEvent().longValue()==idEvent.longValue())
	 					.findFirst();
	 			if(riskEventOptional.isPresent()){
	 				riskEvent = riskEventOptional.get();
	 				sdmraEvent = riskEvent.getIdEvent();
	 			}
 			}
		}
		//Defect 16735, launch the form when risk assessment event is from different stage.
		if(ObjectUtils.isEmpty(riskEvent) && !ObjectUtils.isEmpty(sDMRiskAssessmentReq.getIdEvent())){
			riskEvent = eventDao.getEventByid(sDMRiskAssessmentReq.getIdEvent());
			sdmraEvent = riskEvent.getIdEvent();
		}
		List<String> primCaregiverList = new ArrayList<String>();
		List<String> secCaregiverList = new ArrayList<String>();
		SDMRiskAssessmentDto sDMRiskAssessmentDto = null;

		if (!ObjectUtils.isEmpty(sdmraEvent) && 0l != sdmraEvent) {
			sDMRiskAssessmentDto = sDMRiskAssessmentDao.queryRiskAssessment(sdmraEvent,
					sDMRiskAssessmentReq.getIdStage());
		}
		//Forms framwork will display this message instead of Null pointer exception message.
		if(sDMRiskAssessmentDto == null) {
			throw new RuntimeException("no risk assessments found");
		}

		List personList = new ArrayList();

		List<StagePersonValueDto> stagePersonList = new ArrayList();

		List<StagePersonValueDto> stagePersonListDto = personDao.getPersonDtlList(sDMRiskAssessmentReq.getIdStage());

		for (StagePersonValueDto stagePersonDto : stagePersonListDto) {
			StringBuilder caregiver = new StringBuilder();
			caregiver.append(stagePersonDto.getNmPersonLast());
			caregiver.append(ServiceConstants.COMMA_SPACE);
			caregiver.append(stagePersonDto.getNmPersonFirst());
			if (!ObjectUtils.isEmpty(stagePersonDto.getNmPersonMiddle())) {
				char[] cg1Mname = stagePersonDto.getNmPersonMiddle().toCharArray();
				caregiver.append(ServiceConstants.SPACE);
				caregiver.append(cg1Mname[0]);
				caregiver.append(ServiceConstants.PERIOD);
			}
			if (!ObjectUtils.isEmpty(stagePersonDto.getCdPersonSuffix())) {
				caregiver.append(ServiceConstants.COMMA_SPACE);
				String cg1Sname = stagePersonDto.getCdPersonSuffix();
				caregiver.append(lookupDao.simpleDecodeSafe(CodesConstant.CSUFFIX, cg1Sname));
			}
			caretakerName = caregiver.toString();

			OptionDto optionDto = new OptionDto(String.valueOf(stagePersonDto.getIdPerson()), caretakerName);
			caretakerName = null;
			personList.add(optionDto);
			if (!ObjectUtils.isEmpty(sDMRiskAssessmentDto)) {
				sDMRiskAssessmentDto.setPrimaryCareTakerList(personList);
				stagePersonList.add(stagePersonDto);
				sDMRiskAssessmentDto.setPersonList(stagePersonList);
			}

		}

		/*
		* Defect 9159
		* Getting the caregivers from assement_houshold_link instead of stage person link
		* */
		SDMRiskAssessmentDto careGiverNamesDto=sDMRiskAssessmentDao.getCaregiverNames(sDMRiskAssessmentDto.getId());
		primCaregiverList.add(careGiverNamesDto.getNmPrimaryCaregiver());
		secCaregiverList.add(careGiverNamesDto.getNmSecondaryCaregiver());


		formtxt1 = lookupDao.getMessageByNumber(ServiceConstants.MSG_SDM_RA_NOT_RQD);
		formtxt2 = lookupDao.getMessageByNumber(ServiceConstants.MSG_SDM_RA_INCOMPLETE);
		if (!ObjectUtils.isEmpty(sDMRiskAssessmentDto)) {
			sDMRiskAssessmentDto.setIndSection1Complete(isSection1Complete(sDMRiskAssessmentDto));
			sDMRiskAssessmentDto.setSection2Compete(isSection2Complete(sDMRiskAssessmentDto));
		}

		sdmRiskAssmtFormDto.setPrimCaregiverList(primCaregiverList);
		sdmRiskAssmtFormDto.setRiskEvent(riskEvent);
		sdmRiskAssmtFormDto.setsDMRiskAssessmentDto(sDMRiskAssessmentDto);
		sdmRiskAssmtFormDto.setSecCaregiverList(secCaregiverList);
		sdmRiskAssmtFormDto.setStageDto(stageDto);
		sdmRiskAssmtFormDto.setFormtxt1(formtxt1);
		sdmRiskAssmtFormDto.setFormtxt2(formtxt2);

		return sdmRiskAssmtFormPrefillData.returnPrefillData(sdmRiskAssmtFormDto);
	}

	/**
	 * 
	 * Method Name: isSection1Complete Method Description:
	 * 
	 * @param sDMRiskAssessmentDto
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	private boolean isSection1Complete(SDMRiskAssessmentDto sDMRiskAssessmentDto) {

		boolean followupAnswered = true;

		Set answeredQuestionSet = new HashSet();
		Set notAnsweredFollowupSet = new HashSet();
		Set secNotAnsweredFollowupSet = new HashSet();
		boolean allAnswered = false;
		boolean allFollowupAnswered = true;
		boolean allSecondaryFollowupAnswered = true;
		List<SDMRiskAssessmentQuestionDto> questions = sDMRiskAssessmentDto.getQuestions();

		if (!ObjectUtils.isEmpty(questions)) {
			for (SDMRiskAssessmentQuestionDto questionDB : questions) {
				List<SDMRiskAssessmentAnswerDto> answers = questionDB.getAnswers();

				if (!ObjectUtils.isEmpty(answers)) {
					for (SDMRiskAssessmentAnswerDto answerDB : answers) {

						if (ServiceConstants.ANSWERCODE_WITH_FOLLOWUPS.contains(answerDB.getAnswerCode().toString())
								&& answerDB.getResponseCode() != null) {

							List<SDMRiskAssessmentFollowupDto> followupQuestions = answerDB.getFollowupQuestions();

							if (!ObjectUtils.isEmpty(followupQuestions)) {

								for (SDMRiskAssessmentFollowupDto followupDto : followupQuestions) {
									if (ServiceConstants.STRING_IND_Y
											.equalsIgnoreCase(followupDto.getIndRaFollowup())) {
										followupAnswered = true;
										break;
									}

									else {

										followupAnswered = false;

									}

									if ((ServiceConstants.FOLLOWUPID_WITH_SECFOLLOWUP
											.contains(new Integer(followupDto.getFollowupId())))
											&& (ServiceConstants.YES
													.equalsIgnoreCase(followupDto.getIndRaFollowup()))) {
										List<SDMRiskAssmtSecondaryFollowupDto> secondaryFollowupQuestions = followupDto
												.getSecondaryFollowupQuestions();

										if (!ObjectUtils.isEmpty(secondaryFollowupQuestions)) {

											for (SDMRiskAssmtSecondaryFollowupDto secFollowupDto : secondaryFollowupQuestions) {
												if (ServiceConstants.YES
														.equalsIgnoreCase(secFollowupDto.getIndSecFollowupLookup())) {
													break;
												} else {
													secNotAnsweredFollowupSet
															.add(new Integer(answerDB.getAnswerLookupId()));
												}
											}
										}

									}
								}
							}
							if (!followupAnswered) {

								notAnsweredFollowupSet.add(new String(answerDB.getAnswerCode()));
							}
						}

						if (!ObjectUtils.isEmpty(answerDB.getResponseCode())) {
							answeredQuestionSet.add(new Integer(answerDB.getQuestionLookupId()));
							break;
						}
					}
				}
			}
		}
		if (!ObjectUtils.isEmpty(sDMRiskAssessmentDto) && !ObjectUtils.isEmpty(sDMRiskAssessmentDto.getQuestions())
				&& answeredQuestionSet.size() == sDMRiskAssessmentDto.getQuestions().size()) {
			allAnswered = true;

		}
		if (notAnsweredFollowupSet.size() > 0) {

			allFollowupAnswered = false;

		}

		if (secNotAnsweredFollowupSet.size() > 0) {
			allSecondaryFollowupAnswered = false;

		}

		if (allSecondaryFollowupAnswered && allFollowupAnswered && allAnswered) {
			return true;
		}
		return false;
	}

	private boolean isSection2Complete(SDMRiskAssessmentDto sDMRiskAssessmentDto) {
		boolean isSection2Complete = false;
		if (ServiceConstants.CDOVRIDE_NOOVERRIDE.equalsIgnoreCase(sDMRiskAssessmentDto.getOverrideCode())) {
			isSection2Complete = true;
		}

		else if (ServiceConstants.CDOVRIDE_POLICYOVERRIDE.equalsIgnoreCase(sDMRiskAssessmentDto.getOverrideCode())) {
			if ((!ObjectUtils.isEmpty(sDMRiskAssessmentDto.getIsPOChildDeath())
					|| !ObjectUtils.isEmpty(sDMRiskAssessmentDto.getIsPOChildLessThanThreeInjured())
					|| !ObjectUtils.isEmpty(sDMRiskAssessmentDto.getIsPOChildLessThanSixteenInjured())
					|| !ObjectUtils.isEmpty(sDMRiskAssessmentDto.getIsPOChildSexualAbuse()))
					&& StringUtil.isValid(sDMRiskAssessmentDto.getOverrideReason())) {
				isSection2Complete = true;
			}

		}
		if (ServiceConstants.CDOVRIDE_DISCRETIONARYOVERRID.equalsIgnoreCase(sDMRiskAssessmentDto.getOverrideCode())
				&& StringUtil.isValid(sDMRiskAssessmentDto.getOverrideReason())) {
			isSection2Complete = true;
		}

		return isSection2Complete;
	}

}
