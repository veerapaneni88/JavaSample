package us.tx.state.dfps.service.riskreasmnt.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.fsna.dto.CpsFsnaDto;
import us.tx.state.dfps.service.SDM.dao.SDMSafetyAssessmentDao;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonOutDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.casemanagement.dao.ArHelperDao;
import us.tx.state.dfps.service.casepackage.dto.SelectStageDto;
import us.tx.state.dfps.service.casepackage.service.CaseSummaryService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.SDMRiskReassessmentReq;
import us.tx.state.dfps.service.common.response.SDMRiskReassessmentRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.JSONUtil;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.FbssReassessmentPrefillData;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.reunificationAssessment.dto.ReunificationAssessmentDto;
import us.tx.state.dfps.service.riskreasmnt.SDMRiskReasmntAnswerDto;
import us.tx.state.dfps.service.riskreasmnt.SDMRiskReasmntDto;
import us.tx.state.dfps.service.riskreasmnt.SDMRiskReasmntFollowupDto;
import us.tx.state.dfps.service.riskreasmnt.SDMRiskReasmntQstnDto;
import us.tx.state.dfps.service.riskreasmnt.dao.SDMRiskReassessmentDao;
import us.tx.state.dfps.service.riskreasmnt.service.SDMRiskReassessmentService;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.PostEventDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * provides the implementation for the methods defined in the Service Interface
 * class.This class provides the service implementation to fetch ,save , delete
 * the sdm risk reassessment details.Jun 14, 2018- 3:54:35 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class SDMRiskReassessmentServiceImpl implements SDMRiskReassessmentService {

	@Autowired
	SDMRiskReassessmentDao sdmRiskReassessmentDao;

	@Autowired
	PostEventService postEventService;

	@Autowired
	SDMSafetyAssessmentDao sdmSafetyAssessmentDao;

	public static final String EVNT_DESC_SDM_RISK_REASSMNT = "Risk Reassessment";

	private static final String CONTINUE_SERVICES = "Continue services.";

	private static final String CLOSE_SERVICES = "Close, if there are no unresolved dangers.";

	private static final String STRING_D = "D";

	private static final String STRING_C = "C";

	private static final String ANSWER_6C = "6C";

	@Autowired
	PersonDao personDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	CaseSummaryService caseSummaryService;

	@Autowired
	ApprovalCommonService approvalCommonService;

	@Autowired
	ArHelperDao arHelperDao;

	@Autowired
	FbssReassessmentPrefillData fbssReassessmentPrefillData;

	@Autowired
	JSONUtil jsonUtil;

	/**
	 * Method Name: getSDMRiskReassessmentDetails Method Description:This method is
	 * used to get a new SDM Risk Reassessment details or an existing Risk
	 * reassessment details.This method fetches the data by calling the dao
	 * implementation.This method performs the business logic to show the risk
	 * details.
	 * 
	 * @param sdmRiskReassessmentReq
	 *            - This dto will hold the input parameters to retrieve the SDM Risk
	 *            Reassessment details.
	 * @return SDMRiskReassessmentRes - This dto will have the SDM Risk Reassessment
	 *         details.
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SDMRiskReassessmentRes getSDMRiskReassessmentDtls(SDMRiskReasmntDto sdmRiskReasmntDto,
			Long idRiskReasmntLkp) {
		SDMRiskReassessmentRes sdmRiskReassessmentRes = new SDMRiskReassessmentRes();
		List<PersonDto> personListPrincipals = new ArrayList<PersonDto>();
		// If the idEvent is null or 0 , then its a new reassessment
		if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getIdEvent()) && 0l != sdmRiskReasmntDto.getIdEvent()) {
			sdmRiskReasmntDto = sdmRiskReassessmentDao.getExistingReassessmentData(sdmRiskReasmntDto);
			EventDto eventDto = eventDao.getEventByid(sdmRiskReasmntDto.getIdEvent());
			sdmRiskReasmntDto.setCdEventStatus(eventDto.getCdEventStatus());
		} else {
			sdmRiskReasmntDto = sdmRiskReassessmentDao.getNewReassessmentData(idRiskReasmntLkp, sdmRiskReasmntDto);
			sdmRiskReasmntDto
					.setDtPrvRiskReasmnt(sdmRiskReassessmentDao.getPreviousReasmntDate(sdmRiskReasmntDto.getIdStage()));

		}

		if (CodesConstant.CSTAGES_FRE.equals(sdmRiskReasmntDto.getCdStage())) {
			// get the prior stage id for the FRE stage
			Long idPriorStage = getPriorStage(sdmRiskReasmntDto.getIdStage());
			// Call the dao impl to get the list of sdm risk reunification
			// assessments
			List<ReunificationAssessmentDto> reunificationList = sdmRiskReassessmentDao
					.getReunificationAssessmentList(idPriorStage);
			/*
			 * check if there is no reunification assessment, else get the principals from
			 * person list.
			 */
			if (CollectionUtils.isEmpty(reunificationList)) {
				List<PersonDto> personList = arHelperDao.getPersonCharacteristics(sdmRiskReasmntDto.getIdStage());
				// filter the principals from the person list.
				personListPrincipals = personList.stream()
						.filter(person -> CodesConstant.CPRSNTYP_PRN.equals(person.getCdStagePersType()))
						.collect(Collectors.toList());

			}
			populateHouseholdPrmryScndryMap(sdmRiskReasmntDto, reunificationList, null, personListPrincipals);
			sdmRiskReasmntDto.setPersonList(personListPrincipals);

		} else if (CodesConstant.CSTAGES_FPR.equals(sdmRiskReasmntDto.getCdStage())) {
			// Call the dao impl to get the list of sdm fsna assessments
			List<CpsFsnaDto> cpsFsnaList = sdmRiskReassessmentDao.getFSNAAssessmentList(sdmRiskReasmntDto.getIdStage());
			List<PersonDto> personList = arHelperDao.getPersonCharacteristics(sdmRiskReasmntDto.getIdStage());

			populateHouseholdPrmryScndryMap(sdmRiskReasmntDto, null, cpsFsnaList, personListPrincipals);
			// get the person list for the stage to be displayed in the
			// household member
			// section for FPR stage
			personListPrincipals = personList.stream()
					.filter(person -> CodesConstant.CPRSNTYP_PRN.equals(person.getCdStagePersType()))
					.collect(Collectors.toList());
			personListPrincipals.forEach(person -> {
				if (!ObjectUtils.isEmpty(person.getDtPersonBirth())) {
					person.setPersonAge(DateUtils.getAge(person.getDtPersonBirth(), person.getDtPersonDeath()));
				}
			});

			sdmRiskReasmntDto.setPersonList(personListPrincipals);
			// For FPR stage , getting the household members which were saved in
			// the event
			// person link table.
			if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getIdEvent()) && 0l != sdmRiskReasmntDto.getIdEvent()) {
				// Get the saved household members for the saved assessment.
				List<Long> eventPersonList = sdmSafetyAssessmentDao.getEventPersonLink(sdmRiskReasmntDto.getIdEvent());
				if (!CollectionUtils.isEmpty(eventPersonList)) {
					sdmRiskReasmntDto.setHouseholdMembers(eventPersonList);
				}

			}

		}

		if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getDtLastUpdate())) {
			sdmRiskReasmntDto.setTmRiskReasmntUpdated(DateUtils.getTime(sdmRiskReasmntDto.getDtLastUpdate()));
		}
		if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getDtCreatedOn())) {
			sdmRiskReasmntDto.setTmRiskReasmntCreated(DateUtils.getTime(sdmRiskReasmntDto.getDtCreatedOn()));
		}
		// Setting the recommendation based on the final risk level
		if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getCdFinalRiskLevel())) {
			if (CodesConstant.CSDMRLVL_HIGH.equals(sdmRiskReasmntDto.getCdFinalRiskLevel())
					|| CodesConstant.CSDMRLVL_VERYHIGH.equals(sdmRiskReasmntDto.getCdFinalRiskLevel())) {
				sdmRiskReasmntDto.setTxtRecommendation(CONTINUE_SERVICES);
			} else if (CodesConstant.CSDMRLVL_MOD.equals(sdmRiskReasmntDto.getCdFinalRiskLevel())
					|| CodesConstant.CSDMRLVL_LOW.equals(sdmRiskReasmntDto.getCdFinalRiskLevel())) {
				sdmRiskReasmntDto.setTxtRecommendation(CLOSE_SERVICES);
			}
		}

		sdmRiskReasmntDto.setIdFormVersion(sdmRiskReasmntDto.getQuestions().get(0).getIdRiskReasmntLookUp());
		sdmRiskReassessmentRes.setSdmRiskReasmntDto(sdmRiskReasmntDto);
		return sdmRiskReassessmentRes;
	}

	/**
	 * Method Name: saveSDMRiskReassessmentDetails Method Description:This method is
	 * used to save the SDM Risk Reassessment details.This method performs the
	 * business logic to determine to create or update the risk reassessment
	 * details. Also this method calls the implementation to invalidate the existing
	 * tasks.This method calls the dao implementation to save/update the details.
	 * 
	 * @param sdmRiskReassessmentReq
	 *            - This dto will hold the SDM Risk reassessment details to be saved
	 *            or updated.
	 * @return SDMRiskReassessmentRes - This dto will hold the saved/updated SDM
	 *         Risk reassessment details.
	 * @throws Exception
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SDMRiskReassessmentRes saveSDMRiskReassessmentDtls(SDMRiskReassessmentReq sdmRiskReassessmentReq)
			throws Exception {

		SDMRiskReasmntDto sdmRiskReasmntDto = sdmRiskReassessmentReq.getSdmRiskReasmntDto();
		Long idEvent = sdmRiskReasmntDto.getIdEvent();
		/*
		 * If it is a new Risk Reasmnt , then create the event first and then create the
		 * risk reasmnt.
		 */
		if (ObjectUtils.isEmpty(idEvent) || (!ObjectUtils.isEmpty(idEvent) && idEvent.equals(0l))) {
			// Creating new event or updating event status for SDM Risk
			// Reassessment
			idEvent = callPostEvent(sdmRiskReassessmentReq.getReqFuncCd(),
					sdmRiskReassessmentReq.getSdmRiskReasmntDto());
			sdmRiskReasmntDto.setIdEvent(idEvent);
			sdmRiskReassessmentReq.setSdmRiskReasmntDto(sdmRiskReasmntDto);
		} else {
			/*
			 * check if the event status is PEND , and if the approval flag is false , and
			 * if the user clicked on the save button, then invalidate the existing approval
			 * tasks.
			 * 
			 */
			if (!sdmRiskReasmntDto.isIndApprovalFlag()) {
				if (CodesConstant.CEVTSTAT_PEND.equals(sdmRiskReasmntDto.getCdEventStatus())) {

					ApprovalCommonInDto pInputMsg = new ApprovalCommonInDto();
					ApprovalCommonOutDto pOutputMsg = new ApprovalCommonOutDto();
					pInputMsg.setIdEvent(sdmRiskReasmntDto.getIdEvent());
					// Call Service to invalidate approvals
					approvalCommonService.InvalidateAprvl(pInputMsg, pOutputMsg);
				}
			}
			// update the event
			callPostEvent(ServiceConstants.REQ_FUNC_CD_UPDATE, sdmRiskReasmntDto);

		}
		/*
		 * Call the dao implementation to save or update the risk reasmnt along with the
		 * user responses to the qstns.
		 */
		processFollowUpUpForQstn6(sdmRiskReasmntDto);
		SDMRiskReassessmentRes response = sdmRiskReassessmentDao.saveSDMRiskReassessmentDtls(sdmRiskReassessmentReq);
		return response;
	}

	/**
	 * Method Name: processFollowUpUpForQstn6 Method Description:
	 * 
	 * @param sdmRiskReasmntDto
	 */
	private void processFollowUpUpForQstn6(SDMRiskReasmntDto sdmRiskReasmntDto) {
		SDMRiskReasmntQstnDto sdmRiskReasmntQstnDto = sdmRiskReasmntDto.getQuestions().stream()
				.filter(question -> question.getIdQstn().equals(ServiceConstants.SIX_QUESTION_LOOKUP_ID)).findFirst()
				.orElse(null);
		if (!ObjectUtils.isEmpty(sdmRiskReasmntQstnDto)) {
			if (!ObjectUtils.isEmpty(sdmRiskReasmntQstnDto.getCdAnsPrimary())) {
				SDMRiskReasmntAnswerDto sdmRiskReasmntAnswerDto = sdmRiskReasmntQstnDto.getAnswers().stream()
						.filter(answer -> answer.getCdAnswer().equals(sdmRiskReasmntQstnDto.getCdAnsPrimary()))
						.findFirst().orElse(null);
				if (!ObjectUtils.isEmpty(sdmRiskReasmntAnswerDto)) {
					if (sdmRiskReasmntQstnDto.getCdAnsPrimary().equals(ANSWER_6C)) {
						// Create a temp list to get all the values from
						// followup for option D and then set it back to option
						// C
						List<SDMRiskReasmntFollowupDto> tempList = new ArrayList<>();
						for (SDMRiskReasmntFollowupDto sdmRiskReasmntFollowupDto : sdmRiskReasmntQstnDto.getAnswers()
								.get(3).getFollowupQuestions()) {
							SDMRiskReasmntFollowupDto temp = new SDMRiskReasmntFollowupDto();
							BeanUtils.copyProperties(sdmRiskReasmntFollowupDto, temp);
							tempList.add(temp);
						}
						sdmRiskReasmntAnswerDto.setFollowupQuestions(tempList);

						for (SDMRiskReasmntFollowupDto sdmRiskReasmntFollowupDto : sdmRiskReasmntAnswerDto
								.getFollowupQuestions()) {
							if (!ObjectUtils.isEmpty(sdmRiskReasmntFollowupDto.getCdAnswerSelected())) {
								sdmRiskReasmntFollowupDto.setCdAnswerSelected(
										sdmRiskReasmntFollowupDto.getCdAnswerSelected().replaceAll(STRING_D, STRING_C));
							}
							sdmRiskReasmntFollowupDto.setCdFollowup(
									sdmRiskReasmntFollowupDto.getCdFollowup().replaceAll(STRING_D, STRING_C));

						}
					}
				}
			}
			if (!ObjectUtils.isEmpty(sdmRiskReasmntQstnDto.getCdAnsSecondary())) {
				SDMRiskReasmntAnswerDto sdmRiskReasmntAnswerDto = sdmRiskReasmntQstnDto.getAnswers().stream()
						.filter(answer -> answer.getCdAnswer().equals(sdmRiskReasmntQstnDto.getCdAnsSecondary()))
						.findFirst().orElse(null);
				if (!ObjectUtils.isEmpty(sdmRiskReasmntAnswerDto)) {
					if (sdmRiskReasmntQstnDto.getCdAnsSecondary().equals(ANSWER_6C)) {
						// Create a temp list to get all the values from
						// followup for option D and then set it back to option
						// C
						List<SDMRiskReasmntFollowupDto> tempList = new ArrayList<>();
						for (SDMRiskReasmntFollowupDto sdmRiskReasmntFollowupDto : sdmRiskReasmntQstnDto.getAnswers()
								.get(3).getFollowupQuestionSec()) {
							SDMRiskReasmntFollowupDto temp = new SDMRiskReasmntFollowupDto();
							BeanUtils.copyProperties(sdmRiskReasmntFollowupDto, temp);
							tempList.add(temp);
						}
						sdmRiskReasmntAnswerDto.setFollowupQuestionSec(tempList);

						for (SDMRiskReasmntFollowupDto sdmRiskReasmntFollowupDto : sdmRiskReasmntAnswerDto
								.getFollowupQuestionSec()) {
							if (!ObjectUtils.isEmpty(sdmRiskReasmntFollowupDto.getCdAnswerSelected())) {

								sdmRiskReasmntFollowupDto.setCdAnswerSelected(
										sdmRiskReasmntFollowupDto.getCdAnswerSelected().replaceAll(STRING_D, STRING_C));

							}
							sdmRiskReasmntFollowupDto.setCdFollowup(
									sdmRiskReasmntFollowupDto.getCdFollowup().replaceAll(STRING_D, STRING_C));
						}
					}
				}
			}
		}

	}

	/**
	 * Method Name: callPostEvent Method Description:This method is used to
	 * create/update/delete an Event. This method is used to call service
	 * implementation to create a new Event ,Event Person Link, or Updating event
	 * status or delete an event.
	 * 
	 * @param sdmRiskReasmntDto
	 *            - The dto will hold the input paramter values.
	 * 
	 * @return Long - The id of the event newly created.
	 */
	private Long callPostEvent(String dataAction, SDMRiskReasmntDto sdmRiskReasmntDto) {
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		serviceReqHeaderDto.setReqFuncCd(dataAction);
		if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(dataAction)) {
			EventDto eventDto = eventDao.getEventByid(sdmRiskReasmntDto.getIdEvent());
			postEventIPDto.setDtEventOccurred(eventDto.getDtEventOccurred());
			BeanUtils.copyProperties(eventDto, postEventIPDto);

			/*
			 * Call the dao implementation to delete the existing event person link before
			 * adding the ones selected in the front end.
			 */
			sdmRiskReassessmentDao.deleteEventPersonLink(sdmRiskReasmntDto.getIdEvent());
		}
		// Setting the event details
		postEventIPDto.setIdEvent(sdmRiskReasmntDto.getIdEvent());
		// PostEventDto postEventDto = new PostEventDto();
		postEventIPDto.setIdPerson(new Long(sdmRiskReasmntDto.getIdUser()));
		postEventIPDto.setIdCase(sdmRiskReasmntDto.getIdCase());
		postEventIPDto.setIdStage(sdmRiskReasmntDto.getIdStage());
		postEventIPDto.setCdEventType(ServiceConstants.CEVNTTYP_ASM);
		postEventIPDto.setCdTask(sdmRiskReasmntDto.getCdTask());
		String eventStatus = CodesConstant.CEVTSTAT_PROC;
		if (sdmRiskReasmntDto.isIndApprovalFlag()
				&& CodesConstant.CEVTSTAT_PEND.equals(sdmRiskReasmntDto.getCdEventStatus())) {
			eventStatus = CodesConstant.CEVTSTAT_PEND;
		}
		String eventDesc = EVNT_DESC_SDM_RISK_REASSMNT;
		postEventIPDto.setEventDescr(eventDesc);
		postEventIPDto.setCdEventStatus(eventStatus);
		postEventIPDto.setTsLastUpdate(sdmRiskReasmntDto.getDtEventLastUpdate());
		List<PostEventDto> eventPersonList = new ArrayList<PostEventDto>();
		// Creating the list of person which are to be saved in the event person
		// link
		// table.
		if (!CollectionUtils.isEmpty(sdmRiskReasmntDto.getHouseholdMembers())
				&& !ServiceConstants.REQ_FUNC_CD_DELETE.equals(dataAction)) {
			sdmRiskReasmntDto.getHouseholdMembers().forEach(idPerson -> {
				PostEventDto postEventDto = new PostEventDto();
				postEventDto.setIdPerson(idPerson);
				postEventDto.setCdScrDataAction(ServiceConstants.REQ_IND_AUD_ADD);
				eventPersonList.add(postEventDto);
			});

		}
		postEventIPDto.setPostEventDto(eventPersonList);
		// Calling the post event service to create/update/delete the event.
		PostEventOPDto response = postEventService.checkPostEventStatus(postEventIPDto, serviceReqHeaderDto);
		// returning the idEvent of the ASM event.
		return response.getIdEvent();
	}

	/**
	 * Method Name: getPriorStage Method Description:This method is used to get the
	 * prior stage of a FRE stage in which a risk reassessment is being created or
	 * updated.
	 * 
	 * @param idStage
	 *            - The id stage.
	 * @return idPriorStage - The id of the prior stage.
	 */
	private Long getPriorStage(Long idStage) {
		boolean checkForPriorStageFurther = true;
		Long idPriorStage = null;
		CommonHelperReq commonHelperReq = new CommonHelperReq();
		commonHelperReq.setIdStage(idStage);
		// Calling the service to get the prior stage details.
		SelectStageDto selectStageDto = caseSummaryService.getPriorStage(commonHelperReq);
		idPriorStage = selectStageDto.getIdStage();
		// If the prior stage was FSU , then consider it as the prior stage.
		if (CodesConstant.CSTAGES_FSU.equals(selectStageDto.getCdStage())) {
			checkForPriorStageFurther = false;
		}
		// If the prior stage to FRE was SUB , then getting the prior stage of
		// the SUB
		// stage.
		if (checkForPriorStageFurther) {
			CommonHelperReq commonHelperReq1 = new CommonHelperReq();
			commonHelperReq1.setIdStage(idPriorStage);
			SelectStageDto subStagePriorIdStage = caseSummaryService.getPriorStage(commonHelperReq1);
			/*
			 * If the prior stage for the SUB stage is INV, then retrieve the forward stage
			 * of the INV stage.
			 */
			if (CodesConstant.CSTAGES_INV.equals(subStagePriorIdStage.getCdStage())) {
				idPriorStage = sdmRiskReassessmentDao.getFamilySubstituteCareStageId(subStagePriorIdStage.getIdStage(),
						idPriorStage);
			}
		}
		// returing the prior stage id.
		return idPriorStage;
	}

	/**
	 * Method Name: populateHouseholdPrmryScndryMap Method Description:This method
	 * is used to create the household map , primary person map and the secondary
	 * person map. For FRE stage , the map is created from the existing
	 * re-unification assessments.For FPR stage , the map is created from the
	 * existing FSNA assessments.
	 * 
	 * @param sdmRiskReasmntDto
	 *            - The dto will hold the SDM Risk reassessment details.
	 * @param reunificationListc-
	 *            The list of re-unification assessments for the particular stage.
	 * @param sdmFsnaList
	 *            - The list of fsna assessments for the particular stage.
	 */
	private void populateHouseholdPrmryScndryMap(SDMRiskReasmntDto sdmRiskReasmntDto,
			List<ReunificationAssessmentDto> reunificationList, List<CpsFsnaDto> sdmFsnaList,
			List<PersonDto> personListPrincipals) {
		Map<Long, String> householdMap = new HashMap<Long, String>();
		Map<Long, String> primaryMap = new HashMap<Long, String>();
		Map<Long, String> secondaryMap = new HashMap<Long, String>();
		// For FRE stage , the re-unification assessment list will be iterated
		// over.
		if (!CollectionUtils.isEmpty(reunificationList)) {
			reunificationList.forEach(reunification -> {

				householdMap.put(reunification.getIdHshldPerson(), getNameWithAge(reunification.getIdHshldPerson()));
				primaryMap.put(reunification.getIdPrmryPerson(), getNameWithAge(reunification.getIdPrmryPerson()));
				personListPrincipals.add(getPersonDetails(reunification.getIdHshldPerson()));
				personListPrincipals.add(getPersonDetails(reunification.getIdPrmryPerson()));

				if (!ObjectUtils.isEmpty(reunification.getIdSecndryPerson())) {
					secondaryMap.put(reunification.getIdSecndryPerson(),
							getNameWithAge(reunification.getIdSecndryPerson()));
					personListPrincipals.add(getPersonDetails(reunification.getIdPrmryPerson()));
				}
			});

		}
		// For FPR stage , the fsna assessments will be iterated over.
		else if (!CollectionUtils.isEmpty(sdmFsnaList)) {

			sdmFsnaList.forEach(sdmFsna -> {

				primaryMap.put(sdmFsna.getIdPrmryCrgvrPrnt(), getNameWithAge(sdmFsna.getIdPrmryCrgvrPrnt()));
				if (!ObjectUtils.isEmpty(sdmFsna.getIdSecndryCrgvrPrnt())) {
					secondaryMap.put(sdmFsna.getIdSecndryCrgvrPrnt(), getNameWithAge(sdmFsna.getIdSecndryCrgvrPrnt()));
				}
			});
			householdMap.putAll(primaryMap);
		} else if (CollectionUtils.isEmpty(reunificationList) && CollectionUtils.isEmpty(sdmFsnaList)
				&& !CollectionUtils.isEmpty(personListPrincipals)) {
			personListPrincipals.forEach(principalPerson -> {
				householdMap.put(principalPerson.getIdPerson(), getNameWithAge(principalPerson.getIdPerson()));
			});
			primaryMap.putAll(householdMap);
			secondaryMap.putAll(householdMap);
		}
		/*
		 * If the primary and secondary are selected and saved in the RISK_REASMNT table
		 * , then they should be added to the primary, secondary , household map
		 * explictly.
		 */
		if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getIdPrmryPrsn())
				&& ObjectUtils.isEmpty(primaryMap.get(sdmRiskReasmntDto.getIdPrmryPrsn()))) {
			primaryMap.put(sdmRiskReasmntDto.getIdPrmryPrsn(), getNameWithAge(sdmRiskReasmntDto.getIdPrmryPrsn()));

			personListPrincipals.add(getPersonDetails(sdmRiskReasmntDto.getIdPrmryPrsn()));
		}
		if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getIdHshldAssessed())
				&& ObjectUtils.isEmpty(householdMap.get(sdmRiskReasmntDto.getIdHshldAssessed()))) {
			householdMap.put(sdmRiskReasmntDto.getIdHshldAssessed(),
					getNameWithAge(sdmRiskReasmntDto.getIdHshldAssessed()));
			personListPrincipals.add(getPersonDetails(sdmRiskReasmntDto.getIdHshldAssessed()));
		}
		if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getIdScndryPrsn())
				&& ObjectUtils.isEmpty(secondaryMap.get(sdmRiskReasmntDto.getIdScndryPrsn()))) {
			secondaryMap.put(sdmRiskReasmntDto.getIdScndryPrsn(), getNameWithAge(sdmRiskReasmntDto.getIdScndryPrsn()));
			personListPrincipals.add(getPersonDetails(sdmRiskReasmntDto.getIdScndryPrsn()));
		}
		sdmRiskReasmntDto.setHshldAssessedMap(householdMap);
		sdmRiskReasmntDto.setPrmryPersonMap(primaryMap);
		sdmRiskReasmntDto.setScndryPersonMap(secondaryMap);
	}

	/**
	 * Method Name: getNameWithAge Method Description:This method is used to form
	 * the name which is formed by concatenating the full name with the person;s
	 * age.
	 * 
	 * @param idPerson
	 *            - The id of the person.
	 * @return fullName - The newly formed name .
	 */
	private String getNameWithAge(Long idPerson) {
		Person hshldPerson = personDao.getPerson(idPerson);
		String fullName = hshldPerson.getNmPersonFull();
		if (!ObjectUtils.isEmpty(hshldPerson.getDtPersonBirth())) {
			int age = DateUtils.getAge(hshldPerson.getDtPersonBirth());
			fullName = fullName + " (" + age + ")";
		}
		return fullName;
	}

	/**
	 * Method Name: checkRiskReasmntExists Method Description:This method is used to
	 * check if for a particular person , a Risk Reassessment exists in PROC or PEND
	 * status.
	 * 
	 * @param sdmRiskReassessmentReq
	 *            - This dto will hold the input parameters to check if a risk
	 *            reassessment exists.
	 * @return SDMRiskReassessmentRes - This dto will hold the risk reassessment id
	 *         if present for a person in the stage.
	 */
	@Override
	public SDMRiskReassessmentRes checkRiskReasmntExists(Long idPerson, String indHshldPrmryScndry,
			List<String> eventStatusList, Long idStage) {
		SDMRiskReassessmentRes sdmRiskReassessmentRes = new SDMRiskReassessmentRes();
		Long idRiskReasmnt = null;
		// Call the dao implementation to retrieve the id of the risk reasmnt in
		// PROC or
		// PEND status if present.
		idRiskReasmnt = sdmRiskReassessmentDao.checkRiskReasmntExists(idPerson, indHshldPrmryScndry, eventStatusList,
				idStage);
		sdmRiskReassessmentRes.setIdRiskReasmnt(idRiskReasmnt);
		return sdmRiskReassessmentRes;
	}

	/**
	 * Method Name: deleteSDMRiskReassessmentDtls Method Description:This method is
	 * used to delete the SDM Risk reassessment details.This method calls the
	 * service to delete the event and calls the dao implementation to delete the
	 * risk reassessment.
	 * 
	 * @param sdmRiskReassessmentReq
	 *            - This dto will hold the SDM Risk reassessment id to be deleted.
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SDMRiskReassessmentRes deleteSDMRiskReassessmentDtls(SDMRiskReassessmentReq sdmRiskReassessmentReq) {
		SDMRiskReassessmentRes sdmRiskReassessmentRes = new SDMRiskReassessmentRes();
		// call the dao implementation to delete the risk reassessment
		sdmRiskReassessmentRes = sdmRiskReassessmentDao.saveSDMRiskReassessmentDtls(sdmRiskReassessmentReq);

		// Call post event to delete the event.
		callPostEvent(sdmRiskReassessmentReq.getReqFuncCd(), sdmRiskReassessmentReq.getSdmRiskReasmntDto());
		return sdmRiskReassessmentRes;
	}

	/**
	 * Method Name: getSDMRiskReassessmentDetails Method Description:This method is
	 * used to get a new SDM Risk Reassessment details or an existing Risk
	 * reassessment details.This method fetches the data by calling the dao
	 * implementation.This method performs the business logic to show the risk
	 * details.
	 * 
	 * @param sdmRiskReasmntDto
	 * @param idRiskReasmntLkp
	 * @return
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getSDMRiskReassessmentDetails(Long idEvent, String cdStage) {
		SDMRiskReassessmentRes sdmRiskReassessmentRes = new SDMRiskReassessmentRes();
		SDMRiskReasmntDto sdmRiskReassmntDto = new SDMRiskReasmntDto();
		sdmRiskReassmntDto.setIdEvent(idEvent);
		sdmRiskReassmntDto.setCdStage(cdStage);
		sdmRiskReassessmentRes = getSDMRiskReassessmentDtls(sdmRiskReassmntDto, null);
		StageDto stageDto = stageDao.getStageById(sdmRiskReassessmentRes.getSdmRiskReasmntDto().getIdStage());
		sdmRiskReassessmentRes.getSdmRiskReasmntDto().setIdCase(stageDto.getIdCase());
		sdmRiskReassessmentRes.getSdmRiskReasmntDto().setNmCase(stageDto.getNmCase());
		sdmRiskReassessmentRes.getSdmRiskReasmntDto().setdtStageClosure(stageDto.getDtStageClose());
		return fbssReassessmentPrefillData.returnPrefillData(sdmRiskReassessmentRes);
	}

	private PersonDto getPersonDetails(Long idPerson) {
		Person person = personDao.getPerson(idPerson);
		PersonDto personDto = new PersonDto();
		personDto.setIdPerson(person.getIdPerson());
		personDto.setNmPersonFull(person.getNmPersonFull());
		if (!StringUtils.isEmpty(person.getNbrPersonAge())) {
			personDto.setPersonAge(Integer.valueOf(person.getNbrPersonAge()));
		}		
		return personDto;
	}
}
