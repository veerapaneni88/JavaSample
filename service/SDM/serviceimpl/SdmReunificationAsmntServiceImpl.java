/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: Implementation Class for SDM Reunification Service. It has
 * Method to excute all required operations related to SDM Reunification Assessment Page.
 *Jun 12, 2018- 4:49:30 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.SDM.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.SDM.dao.SDMSafetyAssessmentDao;
import us.tx.state.dfps.service.SDM.dao.SdmReunificationAsmntDao;
import us.tx.state.dfps.service.SDM.service.SdmReunificationAsmntService;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonOutDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dao.PcspListPlacmtDao;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.EventPersonLinkDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.dao.TaskDao;
import us.tx.state.dfps.service.common.request.SdmReunificationAsmntFetchReq;
import us.tx.state.dfps.service.common.request.SdmReunificationAsmntReq;
import us.tx.state.dfps.service.common.response.SdmReunificationAsmntFetchRes;
import us.tx.state.dfps.service.common.response.SdmReunificationAsmntSaveRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.JSONUtil;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.CVSReunificationPrefillData;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonListDto;
import us.tx.state.dfps.service.reunificationAssessment.dto.ReunificationAssessmentAnsDto;
import us.tx.state.dfps.service.reunificationAssessment.dto.ReunificationAssessmentChildDto;
import us.tx.state.dfps.service.reunificationAssessment.dto.ReunificationAssessmentDto;
import us.tx.state.dfps.service.reunificationAssessment.dto.ReunificationAssessmentQuestionsDto;
import us.tx.state.dfps.service.reunificationAssessment.dto.ReunificationAssessmentResponseDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.EventPersonLinkDto;
import us.tx.state.dfps.service.workload.dto.PostEventDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.TaskDto;
import us.tx.state.dfps.web.sdm.bean.SDMReunificationAsmntBean;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class SdmReunificationAsmntServiceImpl implements SdmReunificationAsmntService {

	public SdmReunificationAsmntServiceImpl() {
	}

	private static final Logger log = Logger.getLogger(SdmReunificationAsmntServiceImpl.class);

	private static final int CHILD_AGE_LIMIT = 18;
	private static final String REUNIFICATION_ASMNT_TASK_CODE = "7490";
	private static final String SDM_REUNIFICATION = "Reunification";

	@Autowired
	private SdmReunificationAsmntDao sdmReunificationAsmntDao;

	@Autowired
	private PersonDao personDao;

	@Autowired
	private EventPersonLinkDao eventPersonLinkDao;

	@Autowired
	private TaskDao taskDao;

	@Autowired
	private PostEventService postEventService;

	@Autowired
	private EventDao eventDao;

	@Autowired
	private CaseSummaryDao caseSummaryDao;

	@Autowired
	private SDMSafetyAssessmentDao safetyDao;

	@Autowired
	CapsCaseDao capsCaseDao;

	@Autowired
	private ApprovalCommonService approvalCommonService;

	@Autowired
	CVSReunificationPrefillData cVSReunificationPrefillData;
	
	@Autowired
	PcspListPlacmtDao pcspListPlacmtDao;
	
	@Autowired
	StageDao stageDao;

	@Autowired
	JSONUtil jsonUtil;

	/**
	 * Method Name: fetchSdmReunificationAsmnt Method Description: Fetch the
	 * Page data for new and Saved/Existing SDM Reunification Assessment.
	 * 
	 * @param sdmReunificationAsmntReq
	 * @return response
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public SdmReunificationAsmntFetchRes fetchSdmReunificationAsmnt(
			SdmReunificationAsmntFetchReq sdmReunificationAsmntReq) {
		log.info("fetchSdmReunificationAsmnt method in SdmReunificationAsmntServiceImpl : execution Started.");
		// Create the Page Data for adding new SDM Reunification Assessment.
		Long idStage = sdmReunificationAsmntReq.getIdStage();
		Long idEvent = sdmReunificationAsmntReq.getIdEvent();
		SdmReunificationAsmntFetchRes response = new SdmReunificationAsmntFetchRes();
		ReunificationAssessmentDto reunificationAsmntDto = new ReunificationAssessmentDto();
		// Check if the Request has idEvent. If idEvent is null or 0 get the
		// Initial Page values.
		if (!ObjectUtils.isEmpty(idEvent) && !ServiceConstants.ZERO.equals(idEvent)) {
			reunificationAsmntDto = sdmReunificationAsmntDao.fetchSdmReunificationAssessment(idEvent);
			// Fetch the Person from Event Person Link for the Event.
			List<Long> idEventPersonLst = new ArrayList<>();
			List<Long> idHouseholdMemberLst = new ArrayList<>();
			// Set the Event Status.
			reunificationAsmntDto.setCdEventStatus(eventDao.getEventStatus(idEvent));
			List<EventPersonLinkDto> eventPersonList = eventPersonLinkDao.getEventPersonLinkForIdEvent(idEvent);
			if (!ObjectUtils.isEmpty(eventPersonList)) {
				eventPersonList.stream().forEach(p -> {
					idEventPersonLst.add(p.getIdPerson());
					if (ServiceConstants.Y.equals(p.getIndHousehold())) {
						idHouseholdMemberLst.add(p.getIdPerson());
					}
				});
			}
			response.setIdEventPersonList(idEventPersonLst);
			response.setIdHdhldMemberList(idHouseholdMemberLst);
		} else {
			reunificationAsmntDto.setIdStage(idStage);
			reunificationAsmntDto.setIdEvent(idEvent);
			// Set the Reunification Assessment Version.
			reunificationAsmntDto.setNbrReunfctnVersion(sdmReunificationAsmntDao.getReunificationAsmntLkpVersion());
		}
		// ALM ID : 62702 : need this date to show SDM in the form
		StageDto stageDto = stageDao.getStageById(idStage);
		reunificationAsmntDto.setDtStageClose(stageDto.getDtStageClose());
		
		// Set the Date in order to calculate child's age on assessment launch
		// date for New Assessment.
		Date dtAsmntLaunch = ObjectUtils.isEmpty(reunificationAsmntDto.getDtCreated()) ? new Date()
				: reunificationAsmntDto.getDtCreated();
		// Set the Person Lists for Reunification Assessment.
		response = setPersonListForSdmReunificationAsmnt(idStage, response, dtAsmntLaunch);
		// Set the Reunification Assessment Child List.
		reunificationAsmntDto = initiateChildList(reunificationAsmntDto, response.getChildList());
		// Fetch the question List for Reunification Assessment Screen.
		List<ReunificationAssessmentQuestionsDto> questionList = sdmReunificationAsmntDao
				.fetchQuestionListForAsmnt(reunificationAsmntDto);
		response.setReunificationAsmntQstnDtoList(questionList);
		response.setReunificationAsmntDto(reunificationAsmntDto);
		// Set Latest Assessment date in the Stage.
		response.setDtLatestAsmntInStage(sdmReunificationAsmntDao.getLatestAssessmentDateInStage(idStage, idEvent));
		//Check if Event is submitted for Approval.
		response.setIsSubmittedForApproval(pcspListPlacmtDao.setHasBeenSubmittedForApprovalCps(idEvent));
		log.info("fetchSdmReunificationAsmnt method in SdmReunificationAsmntServiceImpl : Return Response.");
		return response;
	}

	/**
	 * Method Name: fetchSdmReunificationAsmnt Method Description: Fetch the
	 * Form data for new and Saved/Existing SDM Reunification Assessment.
	 * 
	 * @param sdmReunificationAsmntReq
	 ** @param sDMReunificationAsmntBean
	 * @return response
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public PreFillDataServiceDto fetchSdmReunificationAsmntForm(
			SdmReunificationAsmntFetchReq sdmReunificationAsmntReq) {
		SdmReunificationAsmntFetchRes sdmReunificationAsmntFetchRes = new SdmReunificationAsmntFetchRes();
		log.info("fetchSdmReunificationAsmnt method in SdmReunificationAsmntServiceImpl : execution Started.");
		sdmReunificationAsmntFetchRes = fetchSdmReunificationAsmnt(sdmReunificationAsmntReq);
		CapsCaseDto capsCaseDto = capsCaseDao.getCapsCaseByid(sdmReunificationAsmntReq.getIdCase());
		SDMReunificationAsmntBean sDMReunificationAsmntBean = new SDMReunificationAsmntBean();
		sDMReunificationAsmntBean.setIdCase(capsCaseDto.getIdCase());
		sDMReunificationAsmntBean.setNmCase(capsCaseDto.getNmCase());
		sdmReunificationAsmntFetchRes.setsDMReunificationAsmntBean(sDMReunificationAsmntBean);
		log.info("fetchSdmReunificationAsmnt method in SdmReunificationAsmntServiceImpl : Return Response.");
		return cVSReunificationPrefillData.returnPrefillData(sdmReunificationAsmntFetchRes);
	}

	/**
	 * Method Name: sdmReunificationAsmntAUD Method Description: Service method
	 * to Add/Update/Delete the SDM Reunification Assessment.
	 * 
	 * @param sdmReunificationAsmntReq
	 * @return response
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public SdmReunificationAsmntSaveRes sdmReunificationAsmntAUD(SdmReunificationAsmntReq sdmReunificationAsmntReq) {
		log.info("sdmReunificationAsmntAUD method in SdmReunificationAsmntServiceImpl : execution Started.");
		// Decide the Action to be executed.
		SdmReunificationAsmntSaveRes response = new SdmReunificationAsmntSaveRes();
		String action = sdmReunificationAsmntReq.getReqFuncCd();
		ReunificationAssessmentDto reunificationAsmntDto = sdmReunificationAsmntReq.getReunificationAssessmentDto();
		PostEventOPDto eventDto = setEventDto(sdmReunificationAsmntReq);
		switch (action) {
		case ServiceConstants.ADD:
			// Set the Required Elements and add the SDM Reunification
			// Assessment.
			reunificationAsmntDto.setIdEvent(eventDto.getIdEvent());
			reunificationAsmntDto.setDtLastUpdate(new Date());
			reunificationAsmntDto.setDtCreated(new Date());
			// Map the Response to The Assesment.
			reunificationAsmntDto = mapResponsesToQuestionDto(reunificationAsmntDto,
					sdmReunificationAsmntReq.getReunificationAsmntQstnDtoList());
			response.setIdEvent(sdmReunificationAsmntDao.addSdmReunificationAssessment(reunificationAsmntDto));
			break;
		case ServiceConstants.UPDATE:
			// Map the Response to The Assesment.
			reunificationAsmntDto = mapResponsesToQuestionDto(reunificationAsmntDto,
					sdmReunificationAsmntReq.getReunificationAsmntQstnDtoList());
			// Call the Update Sdm Reunificaiton for the Incoming record.
			response.setIdEvent(sdmReunificationAsmntDao.updateSdmReunificationAssessment(reunificationAsmntDto));
			break;
		case ServiceConstants.DELETE:
			// call the Delete Reunification Assessment.
			sdmReunificationAsmntDao.deleteSdmReunificationAssessment(reunificationAsmntDto);
			PostEventIPDto deleteEventDto = new PostEventIPDto();
			deleteEventDto.setIdEvent(sdmReunificationAsmntReq.getIdEvent());
			List<EventPersonLinkDto> eventPersonLstDb = eventPersonLinkDao
					.getEventPersonLinkForIdEvent(deleteEventDto.getIdEvent());
			// If Saved Event has Event Person Link but Incoming details
			// doesn't, Delete the Existing ones.
			List<PostEventDto> eventPersonList = new ArrayList<>();
			if (!ObjectUtils.isEmpty(eventPersonLstDb)) {
				eventPersonLstDb.stream().forEach(epl -> {
					PostEventDto eventPerson = new PostEventDto();
					eventPerson.setIdEventPersonLink(epl.getIdEventPersonLink());
					eventPersonList.add(eventPerson);
				});
			}
			deleteEventDto.setPostEventDto(eventPersonList);
			ServiceReqHeaderDto reqHeader = new ServiceReqHeaderDto();
			reqHeader.setReqFuncCd(action);
			postEventService.checkPostEventStatus(deleteEventDto, reqHeader);
			break;
		default:
			break;
		}
		log.info("sdmReunificationAsmntAUD method in SdmReunificationAsmntServiceImpl : Return response");
		return response;
	}

	/**
	 * Method Name: setPersonListForSdmReunificationAsmnt Method Description:
	 * Method used to fetch the List of Persons for SDM Reunification
	 * Assessment.
	 * 
	 * @param idStage
	 * @param response
	 * @return response
	 */
	private SdmReunificationAsmntFetchRes setPersonListForSdmReunificationAsmnt(Long idStage,
			SdmReunificationAsmntFetchRes response, Date assessmentLaunchDate) {
		log.info(
				"setPersonListForSdmReunificationAsmnt method in SdmReunificationAsmntServiceImpl : execution Started.");
		// Get Person List for the Current Stage.
		List<PersonListDto> personList = personDao.getPersonListByIdStage(idStage, ServiceConstants.STF);
		// setting the person Age calculated based on the assessment launch date.
		if (!ObjectUtils.isEmpty(personList)) {
			personList.stream().forEach(person -> {
				person.setPersonAge(DateUtils.getPersonListAge(person.getDtPersonBirth(), assessmentLaunchDate));
			});
		}
		List<PersonListDto> principalPersonList = personList.stream()
				.filter(person -> ServiceConstants.PRN_TYPE.equals(person.getStagePersType()))
				.collect(Collectors.toList());
		// Get the Principle Child List
		List<PersonListDto> childList = principalPersonList.stream().filter(person -> Objects.nonNull(person.getDtPersonBirth()))
				.collect(Collectors.toList()).stream().filter(person -> CHILD_AGE_LIMIT > person.getPersonAge())
				.collect(Collectors.toList());
		response.setChildList(childList);
		response.setEldersList(personList);
		// Artifact: artf148811 - Defect: 14807 - Removed the code, which fetches Parents assessed in prior INV
		// to load in Household dropdown.
		// Set only the Principles in FSU stage to display in Household dropdown.
		response.setHouseHoldList(principalPersonList);
		// END: Artifact: artf148811 - Defect: 14807

		// Set all the Person Lists to Response Object
		response.setPrinclipalList(principalPersonList);
		log.info("setPersonListForSdmReunificationAsmnt method in SdmReunificationAsmntServiceImpl : Return response");
		return response;
	}

	/**
	 * Method Name: setEventDto Method Description: The methods saves/Updates
	 * the Event for Reunification Assessment.
	 * 
	 * @param req
	 * @return eventODto
	 */
	private PostEventOPDto setEventDto(SdmReunificationAsmntReq req) {
		log.info("setEventDto method in SdmReunificationAsmntServiceImpl : execution Started.");
		ServiceReqHeaderDto reqHeader = new ServiceReqHeaderDto();
		//Calculate the Event Status.
		Long idEvent = req.getIdEvent();
		Long idCase = req.getIdCase();
		Long idStage = req.getIdStage();
		String action = req.getReqFuncCd();
		ReunificationAssessmentDto reunificationAsmnt = req.getReunificationAssessmentDto();
		PostEventIPDto eventDto = new PostEventIPDto();
		List<PostEventDto> eventPersonList = new ArrayList<>();
		PostEventOPDto eventODto = null;
		Person person = null;
		reqHeader.setReqFuncCd(action);
		switch (action) {
		case ServiceConstants.ADD:
			// Fetch the Task detail to set the Event.
			TaskDto taskDetail = taskDao.getTaskDetails(REUNIFICATION_ASMNT_TASK_CODE);
			eventDto.setCdEventStatus(
					req.getIsSaveSubmit() ? ServiceConstants.COMPLETE : ServiceConstants.PROCESS_EVENT);
			eventDto.setIdStage(idStage);
			eventDto.setIdPerson(reunificationAsmnt.getIdCreatedPerson());
			eventDto.setCdTask(taskDetail.getCdTask());
			eventDto.setCdEventType(taskDetail.getCdTaskEventType());
			eventDto.setDtEventOccurred(new Date());
			if (!ObjectUtils.isEmpty(reunificationAsmnt.getIdHshldPerson())) {
				person = personDao.getPersonByPersonId(reunificationAsmnt.getIdHshldPerson());
				eventDto.setEventDescr(SDM_REUNIFICATION + ServiceConstants.HYPHEN + person.getNmPersonFull());
			}
			eventDto.setIdCase(idCase);
			// Set the Event Person Link also. This will set all the selected
			// persons to be save in Event Person Link
			if (!ObjectUtils.isEmpty(req.getIdEventPersonList())) {
				req.getIdEventPersonList().stream().forEach(e -> {
					PostEventDto eventPerson = new PostEventDto();
					eventPerson.setIdPerson(e);
					if (!ObjectUtils.isEmpty(req.getIdHdhldMemberList()) && req.getIdHdhldMemberList().contains(e)) {
						eventPerson.setIndHousehold(ServiceConstants.Y);
					}
					eventPerson.setCdScrDataAction(ServiceConstants.ADD);
					eventPersonList.add(eventPerson);
				});
			}
			// Set the Event Person List to Event Dto
			eventDto.setPostEventDto(eventPersonList);
			eventODto = postEventService.checkPostEventStatus(eventDto, reqHeader);
			break;
		case ServiceConstants.UPDATE:
			// Check if the save is not clicked in approval mode and the Event
			// is in PEND Status.
			// then invalidate the pending approval. In approval mode
			// UlSysNbrReserved1 will be sent
			// as true.
			if (!req.getSysNbrReserved1()
					&& ServiceConstants.PEND.equalsIgnoreCase(reunificationAsmnt.getCdEventStatus())) {
				// If yes, it means the current assessment is already pending
				// for approval,
				// and since it is saved again, we need to invalidate the
				// Pending Approval.
				ApprovalCommonInDto pInputMsg = new ApprovalCommonInDto();
				ApprovalCommonOutDto pOutputMsg = new ApprovalCommonOutDto();
				pInputMsg.setIdEvent(idEvent);
				pInputMsg.setCdEventStatus(ServiceConstants.EVENT_PROC);
				approvalCommonService.InvalidateAprvl(pInputMsg, pOutputMsg);
				// If invalidating the approval, always set the event status to COMP.
				log.info("setEventDto method in SdmReunificationAsmntServiceImpl: Invalidate Approval Status : "
						+ pOutputMsg.getMoreDataInd());
			}
			// Fetch the Event to update the status.
			EventDto event = eventDao.getEventByid(idEvent);
			eventDto.setIdEvent(idEvent);
			eventDto.setDtEventOccurred(event.getDtEventCreated());
			// Set the Event status to PROC if save is done from approval mode.
			// Else set the current status.
			eventDto.setCdEventStatus((!req.getSysNbrReserved1()
					&& (ServiceConstants.PEND.equalsIgnoreCase(reunificationAsmnt.getCdEventStatus())
							|| req.getIsSaveSubmit())) ? ServiceConstants.COMPLETE : event.getCdEventStatus());
			eventDto.setIdStage(event.getIdStage());
			eventDto.setIdPerson(event.getIdPerson());
			eventDto.setCdTask(event.getCdTask());
			eventDto.setCdEventType(event.getCdEventType());
			eventDto.setDtEventOccurred(event.getDtEventOccurred());
			eventDto.setEventDescr(event.getEventDescr());
			eventDto.setIdCase(event.getIdCase());
			// Set or Update the Event Person Link also. This will set all the
			// selected persons to be save in Event Person Link
			List<EventPersonLinkDto> eventPersonLstDb = eventPersonLinkDao.getEventPersonLinkForIdEvent(idEvent);
			if (!ObjectUtils.isEmpty(eventPersonLstDb) && !ObjectUtils.isEmpty(req.getIdEventPersonList())) {
				// If both Saved and Incoming values have the Event Person Link
				// record Handle accordingly.
				List<Long> idEplToBeAdded = req.getIdEventPersonList();
				eventPersonLstDb.stream().forEach(epl -> {
					PostEventDto eventPerson = new PostEventDto();
					Long ideplDto = req.getIdEventPersonList().stream().filter(o -> epl.getIdPerson().equals(o))
							.findAny().orElse(null);
					if (!StringUtils.isEmpty(ideplDto)) {
						eventPerson.setCdScrDataAction(ServiceConstants.UPDATE);
						eventPerson.setIdPerson(ideplDto);
						eventPerson.setIdEventPersonLink(epl.getIdEventPersonLink());
						if (!ObjectUtils.isEmpty(req.getIdHdhldMemberList())
								&& req.getIdHdhldMemberList().contains(ideplDto)) {
							eventPerson.setIndHousehold(ServiceConstants.Y);
						}
						// Remove the updated record from to be added list.
						idEplToBeAdded.remove(ideplDto);
					} else {
						eventPerson.setCdScrDataAction(ServiceConstants.DELETE);
						eventPerson.setIdPerson(epl.getIdPerson());
						eventPerson.setIdEventPersonLink(epl.getIdEventPersonLink());
						eventPerson.setTsLastUpdate(epl.getTsLastUpdate());
					}
					eventPersonList.add(eventPerson);
				});
				// Check the updated to be added list, if still record is
				// available, then add them.
				if (!ObjectUtils.isEmpty(idEplToBeAdded)) {
					idEplToBeAdded.stream().forEach(e -> {
						PostEventDto eventPerson = new PostEventDto();
						eventPerson.setIdPerson(e);
						if (!ObjectUtils.isEmpty(req.getIdHdhldMemberList())
								&& req.getIdHdhldMemberList().contains(e)) {
							eventPerson.setIndHousehold(ServiceConstants.Y);
						}
						eventPerson.setCdScrDataAction(ServiceConstants.ADD);
						eventPersonList.add(eventPerson);
					});
				}
			} else if (!ObjectUtils.isEmpty(eventPersonLstDb) && ObjectUtils.isEmpty(req.getIdEventPersonList())) {
				// If Saved Event has Event Person Link but Incoming details
				// doesn't, Delete the Existing ones.
				eventPersonLstDb.stream().forEach(epl -> {
					PostEventDto eventPerson = new PostEventDto();
					eventPerson.setCdScrDataAction(ServiceConstants.DELETE);
					eventPerson.setIdPerson(epl.getIdPerson());
					eventPerson.setTsLastUpdate(epl.getTsLastUpdate());
					eventPerson.setIdEventPersonLink(epl.getIdEventPersonLink());
					eventPersonList.add(eventPerson);
				});
			} else if (ObjectUtils.isEmpty(eventPersonLstDb) && !ObjectUtils.isEmpty(req.getIdEventPersonList())) {
				// If Save Event doesn't have any Event Person Link record but
				// Incoming details has, Add them.
				req.getIdEventPersonList().stream().forEach(e -> {
					PostEventDto eventPerson = new PostEventDto();
					eventPerson.setIdPerson(e);
					if (!ObjectUtils.isEmpty(req.getIdHdhldMemberList()) && req.getIdHdhldMemberList().contains(e)) {
						eventPerson.setIndHousehold(ServiceConstants.Y);
					}
					eventPerson.setCdScrDataAction(ServiceConstants.ADD);
					eventPersonList.add(eventPerson);
				});
			}
			// Set the Event Person List to Event Dto
			eventDto.setPostEventDto(eventPersonList);
			eventODto = postEventService.checkPostEventStatus(eventDto, reqHeader);
			break;
		default:
			break;
		}
		log.info("setEventDto method in SdmReunificationAsmntServiceImpl : return updated event");
		return eventODto;
	}

	/**
	 * Method Name: mapResponsesToQuestionDto Method Description: This method
	 * maps the Response selected for Assessment from Question Dto List to
	 * Assesment Detail Dto.
	 * 
	 * @param asmntDto
	 * @param questionDto
	 * @return asmntDto
	 */
	private ReunificationAssessmentDto mapResponsesToQuestionDto(ReunificationAssessmentDto asmntDto,
			List<ReunificationAssessmentQuestionsDto> questionDto) {
		log.info("mapResponsesToQuestionDto method in SdmReunificationAsmntServiceImpl : Execution Started");
		List<ReunificationAssessmentResponseDto> asmntResponseList = new ArrayList<>();
		questionDto.stream().forEach(q -> {
			ReunificationAssessmentResponseDto asmntRspnse = q.getReunfctnAsmntRspns();
			if (!ObjectUtils.isEmpty(asmntRspnse) && !StringUtils.isEmpty(asmntRspnse.getCdReunfctnAsmntAnswr())) {
				ReunificationAssessmentAnsDto ansDto = q.getReunfctnAsmntAnsList().stream()
						.filter(a -> asmntRspnse.getCdReunfctnAsmntAnswr().equals(a.getCdReunfctnAsmntAnswr()))
						.findFirst().get();
				asmntRspnse.setIdReunfctnAsmntQstn(q.getIdReunfctnAsmntQstnLkp());
				asmntRspnse.setIdReunfctnAsmntAns(ansDto.getIdReunfctnAsmntAnsLkp());
				if (StringUtils.isEmpty(asmntRspnse.getIdReunfctnAsmntRspns())) {
					asmntRspnse.setDtCreated(new Date());
					asmntRspnse.setIdCreatedPerson(asmntDto.getIdCreatedPerson());
				}
				asmntRspnse.setDtLastUpdate(new Date());
				asmntRspnse.setIdLastUpdatePerson(asmntDto.getIdLastUpdatePerson());
				asmntResponseList.add(asmntRspnse);
			}
		});
		asmntDto.setReunificationAsmntRspnsList(asmntResponseList);
		log.info("mapResponsesToQuestionDto method in SdmReunificationAsmntServiceImpl : return Assessment Info");
		return asmntDto;
	}

	/**
	 * Method Name: initiateChildList Method Description: This Service Method
	 * Sets the Reunification Assessment Child Dto List for Display.
	 * 
	 * @param asmntDto
	 * @param childList
	 * @return asmntDto
	 */
	private ReunificationAssessmentDto initiateChildList(ReunificationAssessmentDto asmntDto,
			List<PersonListDto> childList) {
		log.info("initiateChildList method in SdmReunificationAsmntServiceImpl : Execution Started");
		// create child name map for each child.
		if (!ObjectUtils.isEmpty(childList)) {
			// Sorting the list of child based on age in order of oldest to
			// youngest.
			childList.sort(Comparator.comparing(PersonListDto::getPersonAge).reversed());
			if (!ObjectUtils.isEmpty(asmntDto.getReunificationAsmntChildList())) {
				final Map<Long, Integer> ageMap = (Map<Long, Integer>) childList.stream()
						.collect(Collectors.toMap(PersonListDto::getIdPerson, PersonListDto::getPersonAge));
				final Map<Long, String> nameMap = (Map<Long, String>) childList.stream()
						.collect(Collectors.toMap(PersonListDto::getIdPerson, PersonListDto::getPersonFull));
				List<ReunificationAssessmentChildDto> assessedChildList = asmntDto.getReunificationAsmntChildList();
				assessedChildList.stream().forEach(c -> {
					c.setNmPersonFull(nameMap.get(c.getIdPerson()));
					c.setChildAge(!StringUtils.isEmpty(ageMap.get(c.getIdPerson())) ? ageMap.get(c.getIdPerson())
							: ServiceConstants.Zero);
				});
				// Assessed Child IDs
				List<Long> assessedChildIds = assessedChildList.stream()
						.map(ReunificationAssessmentChildDto::getIdPerson).collect(Collectors.toList());
				childList.stream().forEach(child -> {
					if (!assessedChildIds.contains(child.getIdPerson())) {
						ReunificationAssessmentChildDto newChild = new ReunificationAssessmentChildDto();
						newChild.setIdPerson(child.getIdPerson());
						newChild.setNmPersonFull(child.getPersonFull());
						newChild.setChildAge(!StringUtils.isEmpty(child.getPersonAge()) ? child.getPersonAge()
								: ServiceConstants.Zero);
						assessedChildList.add(newChild);
					}
				});
				if (!ObjectUtils.isEmpty(assessedChildList)) {
					// Sorting the children based on age oldest to youngest for assessment sections.
					assessedChildList
							.sort(Comparator.comparing(ReunificationAssessmentChildDto::getChildAge).reversed());
				}
				asmntDto.setReunificationAsmntChildList(assessedChildList);
			} else {
				List<ReunificationAssessmentChildDto> initialChildList = new ArrayList<>();
				childList.stream().forEach(child -> {
					ReunificationAssessmentChildDto newChild = new ReunificationAssessmentChildDto();
					newChild.setIdPerson(child.getIdPerson());
					newChild.setNmPersonFull(child.getPersonFull());
					initialChildList.add(newChild);
				});
				asmntDto.setReunificationAsmntChildList(initialChildList);
			}
		}
		log.info("initiateChildList method in SdmReunificationAsmntServiceImpl : Return response Assessment Info.");
		return asmntDto;
	}

	/**
	 * Method Name: isHouseholdHavePendAsmnt Method Description: The method
	 * checks if the selected household has any pending Reunfication Assessment
	 * in given stage.
	 * 
	 * @param idStage
	 * @param idHousehold
	 * @return result
	 */
	@Override
	public Boolean isHouseholdHavePendAsmnt(Long idStage, Long idHousehold, Long idEvent) {
		log.info("isHouseholdHavePendAsmnt method in SdmReunificationAsmntServiceImpl : Execution Started");
		Boolean result = Boolean.FALSE;
		// Fetch the Reunification Assessments in the current Stage having
		// queried houshold.
		List<EventDto> eventList = sdmReunificationAsmntDao.getidAsmntEventsForStageAndHshld(idStage, idHousehold);
		if (!ObjectUtils.isEmpty(eventList)) {
			// Iterate over the idEventList fetched above, to check if any of
			// these Events in PEND or PROC Status.
			result = eventList.stream()
					.anyMatch(e -> !idEvent.equals(e.getIdEvent())
							&& (ServiceConstants.PEND.equalsIgnoreCase(e.getCdEventStatus())
									|| ServiceConstants.EVENT_PROC.equalsIgnoreCase(e.getCdEventStatus())));
		}
		log.info("isHouseholdHavePendAsmnt method in SdmReunificationAsmntServiceImpl : Return result " + result);
		return result;
	}

	/**
	 * Method Name: isParentHavePendAsmnt Method Description: Method checks if
	 * the selected parent is available as primary or secondary parent in any
	 * other assessment in progress for current stage.
	 * 
	 * @param idStage
	 * @param idParent
	 * @return result
	 */
	@Override
	public Boolean isParentHavePendAsmnt(Long idStage, Long idParent, Long idEvent) {
		log.info("isParentHavePendAsmnt method in SdmReunificationAsmntServiceImpl : Execution Started");
		Boolean result = Boolean.FALSE;
		// Fetch the Reunification Assessments in the current Stage having
		// queried Parent.
		List<EventDto> eventList = sdmReunificationAsmntDao.getidAsmntEventsForStageAndParent(idStage, idParent);
		if (!ObjectUtils.isEmpty(eventList)) {
			// Iterate over the idEventList fetched above, to check if any of
			// these Events in PEND or PROC Status.
			result = eventList.stream()
					.anyMatch(e -> !idEvent.equals(e.getIdEvent())
							&& (ServiceConstants.PEND.equalsIgnoreCase(e.getCdEventStatus())
									|| ServiceConstants.EVENT_PROC.equalsIgnoreCase(e.getCdEventStatus())));
		}
		log.info("isParentHavePendAsmnt method in SdmReunificationAsmntServiceImpl : Return Result " + result);
		return result;
	}
}
