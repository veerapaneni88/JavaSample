package us.tx.state.dfps.service.pal.serviceimpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Situation;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.dto.CloseStageCaseInputDto;
import us.tx.state.dfps.common.dto.InCheckStageEventStatusDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.common.dto.TodoCommonFunctionDto;
import us.tx.state.dfps.common.dto.TodoCommonFunctionInputDto;
import us.tx.state.dfps.service.admin.dao.EventRiskAssessmentDao;
import us.tx.state.dfps.service.admin.dao.UpdateToDoDao;
import us.tx.state.dfps.service.admin.dao.WorkloadStgPerLinkSelDao;
import us.tx.state.dfps.service.admin.dto.EmpUnitDto;
import us.tx.state.dfps.service.admin.dto.EventRiskAssessmentInDto;
import us.tx.state.dfps.service.admin.dto.EventRiskAssessmentOutDto;
import us.tx.state.dfps.service.admin.dto.UpdateToDoDto;
import us.tx.state.dfps.service.admin.dto.WorkloadStgPerLinkSelInDto;
import us.tx.state.dfps.service.admin.dto.WorkloadStgPerLinkSelOutDto;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dao.CaseFileManagementAUDDao;
import us.tx.state.dfps.service.casepackage.dao.CaseFileManagementDao;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dao.RecordsRetentionAUDDao;
import us.tx.state.dfps.service.casepackage.dao.RecordsRetentionDao;
import us.tx.state.dfps.service.casepackage.dao.SituationDao;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.CaseFileManagementDto;
import us.tx.state.dfps.service.casepackage.dto.CaseFileManagementInDto;
import us.tx.state.dfps.service.casepackage.dto.CaseFileManagementOutDto;
import us.tx.state.dfps.service.casepackage.dto.RecordRetentionDataInDto;
import us.tx.state.dfps.service.casepackage.dto.RecordRetentionDataOutDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.dao.UnitDao;
import us.tx.state.dfps.service.common.request.EventReq;
import us.tx.state.dfps.service.common.request.PalInformationReq;
import us.tx.state.dfps.service.common.request.UpdtPalServiceTrainingReq;
import us.tx.state.dfps.service.common.response.EventDetailRes;
import us.tx.state.dfps.service.common.response.PalInformationRes;
import us.tx.state.dfps.service.common.response.RecordsRetentionRtrvRes;
import us.tx.state.dfps.service.common.service.CheckStageEventStatusService;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.lookup.service.LookupService;
import us.tx.state.dfps.service.nytd.dao.NytdDao;
import us.tx.state.dfps.service.nytd.dto.NytdReportPeriodDto;
import us.tx.state.dfps.service.pal.dao.PalInformationDao;
import us.tx.state.dfps.service.pal.dto.PALSummaryDto;
import us.tx.state.dfps.service.pal.dto.PalInformationDto;
import us.tx.state.dfps.service.pal.dto.PalServiceTrainingDto;
import us.tx.state.dfps.service.pal.service.PalInformationService;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.PostEventDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;
import us.tx.state.dfps.service.workload.service.CloseStageCaseService;
import us.tx.state.dfps.service.workload.service.TodoCommonFunctionService;
import us.tx.state.dfps.service.workload.service.WorkloadService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Apr 20, 2018- 4:48:15 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
public class PalInformationServiceImpl implements PalInformationService {

	private static final Logger logger = Logger.getLogger("PalInformationServiceImpl");
	private static final String RECORD_UNPAID_TRAINING_DESC = "Record Services/Training";
	private static final String RECORD_UNPAID_TRAINING = "RUT";

	@Autowired
	private CaseSummaryDao caseSummaryDao;

	@Autowired
	private EventRiskAssessmentDao eventRiskAssessmentDao;

	@Autowired
	private EventDao eventDao;

	@Autowired
	private PalInformationDao palInformationDao;

	@Autowired
	private NytdDao nytdDao;

	@Autowired
	private WorkloadService workloadService;

	@Autowired
	private StageDao stageDao;

	@Autowired
	CheckStageEventStatusService checkStageEventStatusService;

	@Autowired
	UpdateToDoDao todoDao;

	@Autowired
	WorkloadStgPerLinkSelDao workloadStgPerLinkSelDao;

	@Autowired
	UnitDao unitDao;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	RecordsRetentionDao recordsRetentionDao;

	@Autowired
	RecordsRetentionAUDDao recordsRetentionAUDDao;

	@Autowired
	CaseFileManagementDao caseFileManagementDao;

	@Autowired
	CaseFileManagementAUDDao caseFileManagementAUDDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	LookupService lookupService;
	@Autowired
	EventService eventService;
	@Autowired
	TodoCommonFunctionService commonTodoService;
	@Autowired
	CloseStageCaseService closeStageCaseService;
	@Autowired
	WorkLoadDao workLoadDao;
	@Autowired
	PostEventService postEventService;
	@Autowired
	UpdateToDoDao updateToDoDao;

	@Autowired
	SituationDao situationDao;

	@Autowired
	CapsCaseDao capsCaseDao;

	/**
	 * 
	 * Method Name: getPalInfo Method Description:Fetches the PalInfromation's
	 * Assessment ,Follow-Up,Services and training and Pal Stage Closure Details
	 * 
	 * @param idStage
	 * @param cdTask
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PalInformationDto getPalInfo(Long idCase, Long idStage, Long idUser) {
		logger.info("Start - getPalInfo method - PalInformationServiceImpl class");
		PalInformationDto palInformationDto = new PalInformationDto();
		palInformationDto.setIdCase(idCase);
		palInformationDto.setIdStage(idStage);
		palInformationDto
				.setDtStageStart(caseSummaryDao.getStage(idStage, ServiceConstants.STAGE_CURRENT).getDtStartDate());
		// get the idAssessmentEvent
		palInformationDto.setIdAssessmentEvent(getEventId(idStage, ServiceConstants.PAL_ILS_TASK));
		// get the idSummaryEvent
		palInformationDto.setIdSummaryEvent(getEventId(idStage, ServiceConstants.PAL_SUMMARY));
		// get the idServicesEvent
		palInformationDto.setIdServicesEvent(getEventId(idStage, ServiceConstants.PAL_SERVICES_AND_TRAINING));
		// call setSummary CCFC03S
		palInformationDto = readSummary(0L, idStage, idUser, palInformationDto);
		if (!ObjectUtils.isEmpty(palInformationDto.getIdAssessmentEvent())
				&& !ServiceConstants.ZERO.equals(palInformationDto.getIdAssessmentEvent())) {
			// retrievePalFields method logic in legacy is also included in the
			// below method call
			palInformationDto = getIlsAssessment(idStage, palInformationDto);
		}
		// call fetch services and training CCFC10S
		getPalServiceTrainings(idStage, palInformationDto);
		// Call to fetch the follow up event list
		palInformationDto = getFollowUp(idStage, idCase, palInformationDto, idUser);
		logger.info("End - getPalInfo method - PalInformationServiceImpl class");
		return palInformationDto;
	}

	/**
	 * 
	 * Method Name: getEventId Method Description:get the
	 * idAssessmentEvent,idSummaryEvent and idServicesEvent
	 * 
	 * @param idStage
	 * @param cdTask
	 * @return
	 */
	private Long getEventId(Long idStage, String cdTask) {
		logger.info("Start - getEventId method - PalInformationServiceImpl class");
		Long idEvent = 0L;
		EventRiskAssessmentInDto inputDto = new EventRiskAssessmentInDto();
		inputDto.setIdStage(idStage);
		inputDto.setCdTask(cdTask);
		List<EventRiskAssessmentOutDto> eventList = eventRiskAssessmentDao.getEvent(inputDto);
		if (!ObjectUtils.isEmpty(eventList)) {
			idEvent = eventList.get(0).getIdEvent();
		}
		logger.info("End - getEventId method - PalInformationServiceImpl class");
		return idEvent;

	}

	/**
	 * 
	 * Method Name: getIlsAssessment Method Description:get the details of
	 * IlsAssessment
	 * 
	 * @param idStage
	 * @param palInformationDto
	 * @return
	 */

	public PalInformationDto getIlsAssessment(Long idStage, PalInformationDto palInformationDto) {
		logger.info("Start - getIlsAssessment method - PalInformationServiceImpl class");

		EventDto eventDto = eventDao.getEventByid(palInformationDto.getIdAssessmentEvent());
		palInformationDto.setAssessmentEventStatusCode(eventDto.getCdEventStatus());
		palInformationDto.setDtAssessmentLastUpdateSecond(eventDto.getDtLastUpdate());

		if (!ServiceConstants.STATUS_NEW.equalsIgnoreCase(eventDto.getCdEventStatus())) {

			PalInformationDto palDto = palInformationDao.getPal(idStage);

			if (!ObjectUtils.isEmpty(palDto)) {
				palInformationDto.setCdPalCloseLivArr(palDto.getCdPalCloseLivArr());
				palInformationDto.setNoIlsReason(palDto.getNoIlsReason());
				palInformationDto.setNoIlsAssessReason(palDto.getNoIlsAssessReason());
				palInformationDto.setDtTraingCompleted(palDto.getDtTraingCompleted());
				palInformationDto.setNoIlsAssessment(palDto.getNoIlsAssessment());
				palInformationDto.setDtAssessmentLastUpdate(palDto.getDtAssessmentLastUpdate());

				palInformationDto.setDtPreAssessment(palDto.getDtPreAssessment());
				palInformationDto.setPreAssessmentScore(
						ObjectUtils.isEmpty(palDto.getPreAssessmentScore()) ? 0.0 : palDto.getPreAssessmentScore());
				palInformationDto.setPreAssessmentNoScore(palDto.getPreAssessmentNoScore());

				palInformationDto.setDtPostAssessment(palDto.getDtPostAssessment());
				palInformationDto.setPostAssessmentScore(
						ObjectUtils.isEmpty(palDto.getPostAssessmentScore()) ? 0.0 : palDto.getPostAssessmentScore());
				palInformationDto.setPostAssessmentNoScore(palDto.getPostAssessmentNoScore());
				palInformationDto.setTxtSummaryComments(palDto.getTxtSummaryComments());

			}

		}
		logger.info("End - getIlsAssessment method - PalInformationServiceImpl class");

		return palInformationDto;

	}

	/**
	 * 
	 * Method Name: getFollowUp Method Description:get the followUpListDetails
	 * 
	 * @param idStage
	 * @param idCase
	 * @param palInformationDto
	 * @param idUser
	 * @return
	 */

	public PalInformationDto getFollowUp(Long idStage, Long idCase, PalInformationDto palInformationDto, Long idUser) {
		logger.info("Start - getFollowUp method - PalInformationServiceImpl class");

		EventReq eventReq = new EventReq();
		eventReq.setbIndCaseSensitive(ServiceConstants.Y);
		eventReq.setReqFuncCd(ServiceConstants.UPDATE);
		eventReq.setPerfInd(ServiceConstants.Y);
		eventReq.setDataAcsInd(ServiceConstants.Y);
		eventReq.setPageNbr(1);
		eventReq.setPageSizeNbr(20);
		eventReq.setUserId(String.valueOf(idUser));
		List<String> eventTypeList = new ArrayList<>();
		eventTypeList.add("FUP");
		eventReq.setEventType(eventTypeList);
		List<String> stageCodeList = new ArrayList<>();
		stageCodeList.add("PAL");
		eventReq.setStageType(stageCodeList);

		eventReq.setUlIdCase(idCase);
		eventReq.setUlIdEventPerson(0L);
		eventReq.setUlIdPerson(0L);
		eventReq.setUlIdStage(idStage);

		EventDetailRes eventDetails = workloadService.getEventDetails(eventReq);
		if (!ObjectUtils.isEmpty(eventDetails)) {
			palInformationDto.setPalFollowUpList(eventDetails.getEventSearchDto());
		}
		logger.info("End - getFollowUp method - PalInformationServiceImpl class");
		return palInformationDto;

	}

	/**
	 * 
	 * Method Name: readSummary Method Description:Fetches the Closure
	 * date,Living Arrangement and Closure Reason
	 * 
	 * @param idEvent
	 * @param idStage
	 * @param idPerson
	 * @param dto
	 * @return
	 */

	private PalInformationDto readSummary(Long idEvent, Long idStage, Long idPerson, PalInformationDto dto) {
		logger.info("Start - readSummary method - PalInformationServiceImpl class");
		PALSummaryDto palSummaryDto = new PALSummaryDto();
		palSummaryDto.setDtSystemDate(new Date());

		// CCFC03S

		palSummaryDto = palInformationDao.retrievePalInformation(idStage, idPerson, idEvent, palSummaryDto);
		if (!ObjectUtils.isEmpty(palSummaryDto)) {
			dto.setDtClosure(palSummaryDto.getDtStageClose());
			dto.setClosureReason(palSummaryDto.getCdStageReasonClosed());
			dto.setCdPalCloseLivArr(palSummaryDto.getCdPalCloseLivArr());
			dto.setSysIndPalIlsAssmt(palSummaryDto.getSysIndPalIlsAssmt());
			dto.setSysIndPalSvcAuth(palSummaryDto.getSysIndPalSvcAuth());
			dto.setPersonAge(palSummaryDto.getSysIndPalOverEighteen());
			dto.setClosureLivingArrangement(palSummaryDto.getCdPalCloseLivArr());
		}
		logger.info("End - readSummary method - PalInformationServiceImpl class");
		return dto;

	}

	/**
	 * Method Name: getPalServiceTrainings Method Description: This method is
	 * used to getPalServiceTrainings
	 * 
	 * @param idStage
	 * @param idEvent
	 * @param palInformationDto
	 * @param idUser
	 */
	public void getPalServiceTrainings(Long idStage, PalInformationDto palInformationDto) {
		logger.info("Start - getPalServiceTrainings method - PalInformationServiceImpl class");
		StageDto stageDto = new StageDto();
		EventDto eventDto = new EventDto();
		List<PalServiceTrainingDto> palServiceTrainings = new ArrayList<>();

		Long idEvent = palInformationDto.getIdServicesEvent();

		if (!ObjectUtils.isEmpty(idStage))
			stageDto = stageDao.getStageById(idStage);

		Date dtStageStart = stageDto.getDtStageStart();
		palInformationDto.setDtStageStart(dtStageStart);

		String eventStatus = ServiceConstants.EMPTY_STR;
		if (!ObjectUtils.isEmpty(idEvent) && !ServiceConstants.ZERO.equals(idEvent)) {
			eventDto = eventDao.getEventByid(idEvent);

			eventStatus = eventDto.getCdEventStatus();

			palInformationDto.setDtServicesLastUpdate(eventDto.getDtLastUpdate());
		}

		if (ObjectUtils.isEmpty(eventStatus)
				|| (!ObjectUtils.isEmpty(eventStatus) && !ServiceConstants.EVENTSTATUS_NEW.equals(eventStatus))) {
			palServiceTrainings = palInformationDao.getPalServiceTrainings(idStage);
		}
		palInformationDto.setPalServiceTrainingList(palServiceTrainings);

		if (!ObjectUtils.isEmpty(palServiceTrainings)) {
			setPalServicesTraining(palInformationDto);
		}

		logger.info("End - getPalServiceTrainings method - PalInformationServiceImpl class");
	}

	/**
	 * Method Name: setPalServicesTraining Method Description: This method is
	 * used to setPalServicesTraining
	 * 
	 * @param palInformationDto
	 */
	private void setPalServicesTraining(PalInformationDto palInformationDto) {
		logger.info("Start - setPalServicesTraining method - PalInformationServiceImpl class");
		List<PalServiceTrainingDto> palServiceTrainings = palInformationDto.getPalServiceTrainingList();
		NytdReportPeriodDto nytdReportPeriodDto = nytdDao.getNytdReportingPeriod(new Date());

		Date dtCurrNytdStart = nytdReportPeriodDto.getDtReportStart();
		Date dtCurrNytdEnd = nytdReportPeriodDto.getDtReportEnd();
		Date dtPrevNytdStart;
		Date dtPrevNytdEnd;

		if (!ObjectUtils.isEmpty(dtCurrNytdStart)) {
			NytdReportPeriodDto nytdPrevReportPeriodDto = nytdDao
					.getNytdReportingPeriod(DateUtils.addToDate(dtCurrNytdStart, 0, 0, -1));
			dtPrevNytdStart = nytdPrevReportPeriodDto.getDtReportStart();
			dtPrevNytdEnd = nytdPrevReportPeriodDto.getDtReportEnd();
		} else {
			dtPrevNytdStart = ServiceConstants.NULL_DATE_TYPE;
			dtPrevNytdEnd = ServiceConstants.NULL_DATE_TYPE;
		}

		if (!ObjectUtils.isEmpty(palServiceTrainings)) {
			updateInfo(palServiceTrainings, dtCurrNytdStart, dtCurrNytdEnd, dtPrevNytdStart, dtPrevNytdEnd);
		}

		palInformationDto.setPalServiceTrainingList(palServiceTrainings);
		logger.info("End - setPalServicesTraining method - PalInformationServiceImpl class");
	}

	/**
	 * Method Name: updateInfo Method Description:
	 * 
	 * @param palServiceTrainings
	 * @param dtCurrNytdStart
	 * @param dtCurrNytdEnd
	 * @param dtPrevNytdStart
	 * @param dtPrevNytdEnd
	 */
	private void updateInfo(List<PalServiceTrainingDto> palServiceTrainings, Date dtCurrNytdStart, Date dtCurrNytdEnd,
			Date dtPrevNytdStart, Date dtPrevNytdEnd) {
		logger.info("Start - updateInfo method - PalInformationServiceImpl class");
		palServiceTrainings.forEach(palServiceTraining -> {
			palServiceTraining.setEditable(Boolean.TRUE);

			if (ServiceConstants.Character_IND_Y.equals(palServiceTraining.getIndNytd())) {
				if (ServiceConstants.Character_IND_Y.equals(palServiceTraining.getIndNytdReported())) {
					palServiceTraining.setEditable(Boolean.FALSE);
				} else if (!ObjectUtils.isEmpty(dtCurrNytdStart) && !ObjectUtils.isEmpty(dtCurrNytdEnd)) {
					NytdReportPeriodDto nytdSrvcTrnPeriodDto = nytdDao
							.getNytdReportingPeriod(palServiceTraining.getDtPalServiceDate());
					if (ObjectUtils.isEmpty(nytdSrvcTrnPeriodDto.getDtReportStart())
							|| ObjectUtils.isEmpty(nytdSrvcTrnPeriodDto.getDtReportEnd())) {
						palServiceTraining.setEditable(Boolean.TRUE);
					} else if (!(palServiceTraining.getDtPalServiceDate().before(dtCurrNytdStart)
							|| palServiceTraining.getDtPalServiceDate().after(dtCurrNytdEnd))) {
						palServiceTraining.setEditable(Boolean.TRUE);
					} else if (DateUtils.isAfter(palServiceTraining.getDtPalServiceDate(), new Date())) {
						palServiceTraining.setEditable(Boolean.TRUE);
					} else if (DateUtils.isAfter(DateUtils.addToDate(dtCurrNytdStart, 0, 0, 15), new Date())) {
						if (ObjectUtils.isEmpty(dtPrevNytdStart) || ObjectUtils.isEmpty(dtPrevNytdEnd)) {
							palServiceTraining.setEditable(Boolean.TRUE);
						} else if (!(palServiceTraining.getDtPalServiceDate().before(dtPrevNytdStart)
								|| palServiceTraining.getDtPalServiceDate().after(dtPrevNytdEnd))) {
							palServiceTraining.setEditable(Boolean.TRUE);
						} else {
							palServiceTraining.setEditable(Boolean.FALSE);
						}
					} else {
						palServiceTraining.setEditable(Boolean.FALSE);
					}
				}

			}
		});
		logger.info("Start - updateInfo method - PalInformationServiceImpl class");
	}

	/**
	 * This service is AUD service for PAL record Services/Training This service
	 * will update all columns for an Id Stage/ Id PAL Services from the PAL
	 * SERVICE table. It will also update all columns for an Id Event from the
	 * EVENT table. It can add or modify the EVENT row. It will also retrieve
	 * the primary child for the PAL stage in order to lik the event with the
	 * primary child whem adding the Services/Training event.
	 * 
	 * @param updtPalServiceTrainingReq
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ServiceResHeaderDto updtPalServiceTraining(UpdtPalServiceTrainingReq updtPalServiceTrainingReq) {
		logger.info("Start - updtPalServiceTraining method - PalInformationServiceImpl class");
		PalServiceTrainingDto palServiceTrainingDto = updtPalServiceTrainingReq.getPalServiceTrainingDto();
		ServiceResHeaderDto serviceResHeaderDto = new ServiceResHeaderDto();
		InCheckStageEventStatusDto inCheckStageEventStatusDto = new InCheckStageEventStatusDto();
		inCheckStageEventStatusDto.setCdTask(palServiceTrainingDto.getCdTask());
		inCheckStageEventStatusDto.setIdStage(palServiceTrainingDto.getIdStage());
		inCheckStageEventStatusDto.setCdReqFunction(updtPalServiceTrainingReq.getReqFuncCd());
		checkStageEventStatusService.chkStgEventStatus(inCheckStageEventStatusDto);
		WorkloadStgPerLinkSelInDto workloadStgPerLinkSelInDto = new WorkloadStgPerLinkSelInDto();
		workloadStgPerLinkSelInDto.setCdStagePersRole(ServiceConstants.PRIMARY_CHILD);
		workloadStgPerLinkSelInDto.setIdStage(palServiceTrainingDto.getIdStage());
		List<WorkloadStgPerLinkSelOutDto> workloadStgPerLinkS = workloadStgPerLinkSelDao
				.getWorkLoad(workloadStgPerLinkSelInDto);
		/*
		 * Place id_person of the Primary Child on the Event Person Link Table
		 */
		Long idPersonPrimayChild = null;
		Optional<WorkloadStgPerLinkSelOutDto> workloadStgPerLinkSelOutDtoOpt = workloadStgPerLinkS.stream().findFirst();
		if (workloadStgPerLinkSelOutDtoOpt.isPresent())
			idPersonPrimayChild = workloadStgPerLinkSelOutDtoOpt.get().getIdTodoPersAssigned();
		else
			throw new ServiceLayerException("9003");
		if (!ObjectUtils.isEmpty(palServiceTrainingDto.getIdEvent())
				|| !ServiceConstants.REQ_FUNC_CD_DELETE.equals(updtPalServiceTrainingReq.getReqFuncCd())) {
			Long idEvent = callPostEvent(updtPalServiceTrainingReq, idPersonPrimayChild);
			palServiceTrainingDto.setIdEvent(idEvent);
		}
		palServiceTrainingDto = palInformationDao.updtPalService(updtPalServiceTrainingReq);
		if (!ObjectUtils.isEmpty(palServiceTrainingDto.getIdEvent())) {
			UpdateToDoDto updateToDoDto = new UpdateToDoDto();

			updateToDoDto.setIdEvent(palServiceTrainingDto.getIdEvent());
			todoDao.completeTodo(updateToDoDto);
		}
		logger.info("End - updtPalServiceTraining method - PalInformationServiceImpl class");
		return serviceResHeaderDto;
	}

	private Long callPostEvent(UpdtPalServiceTrainingReq updtPalServiceTrainingReq, long idPersonPrimayChild) {
		logger.info("Start - callPostEvent method - PalInformationServiceImpl class");
		PalServiceTrainingDto palServiceTrainingDto = updtPalServiceTrainingReq.getPalServiceTrainingDto();
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
		archInputDto.setReqFuncCd(updtPalServiceTrainingReq.getReqFuncCd());
		postEventIPDto.setIdPerson(palServiceTrainingDto.getIdPerson());
		List<PostEventDto> postEventDtos = new ArrayList<>();
		// If the idSErviceEvent is null, i.e. no service event exists, add the primary child for Event person link.
		if (ObjectUtils.isEmpty(palServiceTrainingDto.getIdServicesEvent())){
			PostEventDto postEventDto = new PostEventDto();
			postEventDto.setCdScrDataAction(ServiceConstants.REQ_FUNC_CD_ADD);
			postEventDto.setIdPerson(idPersonPrimayChild);
			postEventDtos.add(postEventDto);
		}
		/*else
			postEventDto.setCdScrDataAction(ServiceConstants.REQ_FUNC_CD_UPDATE);*/
		postEventIPDto.setPostEventDto(postEventDtos);
		postEventIPDto.setCdEventStatus(ServiceConstants.EVENT_PROC);
		postEventIPDto.setCdEventType(RECORD_UNPAID_TRAINING);
		postEventIPDto.setDtEventOccurred(new Date());

		postEventIPDto.setIdEvent(palServiceTrainingDto.getIdServicesEvent());

		postEventIPDto.setIdStage(palServiceTrainingDto.getIdStage());
		postEventIPDto.setIdCase(palServiceTrainingDto.getIdCase());
		postEventIPDto.setCdTask(palServiceTrainingDto.getCdTask());
		postEventIPDto.setEventDescr(RECORD_UNPAID_TRAINING_DESC);

		if (!ObjectUtils.isEmpty(palServiceTrainingDto.getDtLastUpdate())) {
			postEventIPDto.setTsLastUpdate(palServiceTrainingDto.getDtLastUpdate());
		}

		postEventIPDto.setUserId(updtPalServiceTrainingReq.getUserId());
		PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, archInputDto);
		return postEventOPDto.getIdEvent();
	}

	/**
	 * Method Name: reopenPALStage Method Description:This service will update
	 * the close date ofr the Stage, Situation and Case tables to null. It will
	 * also set the stage closure reason to null on the STAGE table.
	 * Additionally, the new primary worker will be added t the stage along with
	 * a link to the primary child. Finally, the ILS Assessment and PAL Services
	 * event statuses will be set back to "PROC". When the case is reopened, the
	 * records retention recored must be deleted and the case file management
	 * recored must be updated with the appropriate information.
	 * 
	 * @param commonHelperReq
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PalInformationDto reopenPALStage(Long idCase, Long idStage, String userLogon, Long idUser) {
		logger.info("Start - reopenPALStage method - PalInformationServiceImpl class");
		Long idPersonChild = 0l;
		StagePersonLinkDto stagePersonLinkPRDto = new StagePersonLinkDto();
		RecordRetentionDataInDto recordRetentionDataInDto = new RecordRetentionDataInDto();
		RecordRetentionDataOutDto recordRetentionDataOutDto = new RecordRetentionDataOutDto();
		CaseFileManagementInDto caseFileManagementInDto = new CaseFileManagementInDto();
		CaseFileManagementOutDto caseFileManagementOutDto = new CaseFileManagementOutDto();
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();

		PalInformationDto palInformationDto = new PalInformationDto();
		// Return the IdPerson for the PAL Coordinator assigned to a Stage.
		Long palCoordID = palInformationDao.getPALCoordinatorID(idStage);
		if (palCoordID > 0l) {
			idUser = palCoordID;
		}
		// CINV51D
		// Calling cinv51d this service is to get Personid
		// Set CINV51D IdStage and CdStagePersRole to "PC" for primary child and
		// personId
		WorkloadStgPerLinkSelInDto workloadStgPerLinkSelInDto = new WorkloadStgPerLinkSelInDto();
		workloadStgPerLinkSelInDto.setCdStagePersRole(CodesConstant.CROLES_PC);
		workloadStgPerLinkSelInDto.setIdStage(idStage);
		List<WorkloadStgPerLinkSelOutDto> workLoadList = workloadStgPerLinkSelDao
				.getWorkLoad(workloadStgPerLinkSelInDto);
		if (!CollectionUtils.isEmpty(workLoadList)) {
			idPersonChild = workLoadList.get(0).getIdTodoPersAssigned();
			// CSES94D
			// This DAM will check for any OPEN PAL Stages for the id_person
			// passed
			// in and Display a New Message
			// Open PAL Stage already exists - to prevent a PAL worker from
			// opening
			// a PAL stage for a PC that already has an open PAL stage.

			serviceReqHeaderDto.setReqFuncCd(ServiceConstants.OPEN_STAGES);
			Long countStages = stageDao.getOpenStagesCount(idPersonChild, ServiceConstants.PERSON_ROLE_PRIM_CHILD,
					ServiceConstants.PREP_ADULT, serviceReqHeaderDto);
			if (countStages > 0) {
				throw new ServiceLayerException("", 8420L, null);
			}

		}

		// Call CINT40D
		// This DAM will return one row from the stage table based upon the
		// id_stage passed into it.
		StageDto stageDto = stageDao.getStageById(idStage);
		if (!ObjectUtils.isEmpty(stageDto)) {
			updatePalReopen(idCase, idStage, idUser, idPersonChild, stagePersonLinkPRDto, serviceReqHeaderDto,
					stageDto);
		}
		// CSES56D
		// This DAM will retrieve a full row from RECORDS RETENTION table and
		// will take as input ID_CASE
		RecordsRetentionRtrvRes recordsRetentionRtrvRes = recordsRetentionDao.getRecordsRetentionByCaseId(idCase);
		if (!ObjectUtils.isEmpty(recordsRetentionRtrvRes)
				&& !ObjectUtils.isEmpty(recordsRetentionRtrvRes.getTsLastUpdate())) {
			// CAUD75D
			// This DAM will add/update/delete a full row from the RECORDS
			// RETENTION table.
			recordRetentionDataInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_DELETE);
			recordRetentionDataInDto.setIdCase(idCase);
			recordRetentionDataInDto.setTsLastUpdate(recordsRetentionRtrvRes.getTsLastUpdate());
			recordsRetentionAUDDao.recordsRetentionAUD(recordRetentionDataInDto, recordRetentionDataOutDto);
		}
		// CSES57D
		// This dam will retrieve a full row from the Case File Management
		// table.
		CaseFileManagementDto caseFileManagementDto = caseFileManagementDao.getCaseFileDetails(idCase);
		if (!ObjectUtils.isEmpty(caseFileManagementDto)) {

			// CAUD76D
			// This DAM add/update/delete a full row from Case File Management.
			caseFileManagementInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
			caseFileManagementInDto.setNmCaseFileOffice(caseFileManagementDto.getNmCaseFileOffice());
			caseFileManagementInDto.setAddrCaseFileStLn1(caseFileManagementDto.getAddrCaseFileStLn1());
			caseFileManagementInDto.setAddrCaseFileStLn2(caseFileManagementDto.getAddrCaseFileStLn2());
			caseFileManagementInDto.setCdCaseFileOfficeType(caseFileManagementDto.getCdCaseFileOfficeType());
			caseFileManagementInDto.setDtCaseFileArchCompl(caseFileManagementDto.getDtCaseFileArchCompl());
			caseFileManagementInDto.setDtCaseFileArchElig(caseFileManagementDto.getDtCaseFileArchElig());
			if (null != caseFileManagementDto.getIdOffice()) {
				caseFileManagementInDto.setIdOffice(caseFileManagementDto.getIdOffice());
			}
			if (null != caseFileManagementDto.getIdUnit()) {
				caseFileManagementInDto.setIdUnit(caseFileManagementDto.getIdUnit());
			}
			if (caseFileManagementInDto.getIdCase() != 0) {
				caseFileManagementAUDDao.caseFileManagementAUD(caseFileManagementInDto, caseFileManagementOutDto);
			}
		}

		// CCMNB9D
		// Retrieves all the records in the STAGE_PERSON_LINK table for the ID
		// STAGE given as input
		List<StagePersonLinkDto> stagePersonLinkDtolist = stagePersonLinkDao.getStagePersonLinkByIdStage(idStage);
		for (StagePersonLinkDto dto : stagePersonLinkDtolist) {
			// CAUD74D
			// This function calls CAUD74D which updates a person's status.
			Person person = personDao.getPerson(dto.getIdPerson());
			person.setCdPersonStatus(ServiceConstants.CD_PERSON_STATUS_A);
			personDao.updatePerson(person);
		}
		palInformationDto.setStagePersonLinkDtolist(stagePersonLinkDtolist);
		logger.info("End - reopenPALStage method - PalInformationServiceImpl class");
		return palInformationDto;
	}

	/**
	 * Method Name: updatePalReopen Method Description:This service will update
	 * the close date ofr the Stage, Situation and Case tables to null. It will
	 * also set the stage closure reason to null on the STAGE table.
	 * Additionally, the new primary worker will be added t the stage along with
	 * a link to the primary child. Finally, the ILS Assessment and PAL Services
	 * event statuses will be set back to "PROC". When the case is reopened, the
	 * records retention recored must be deleted and the case file management
	 * recored must be updated with the appropriate information.
	 * 
	 * @param idCase
	 * @param idStage
	 * @param idUser
	 * @param idPersonChild
	 * @param stagePersonLinkPRDto
	 * @param serviceReqHeaderDto
	 * @param stageDto
	 */
	private void updatePalReopen(Long idCase, Long idStage, Long idUser, Long idPersonChild,
			StagePersonLinkDto stagePersonLinkPRDto, ServiceReqHeaderDto serviceReqHeaderDto, StageDto stageDto) {
		logger.info("Start - updatePalReopen method - PalInformationServiceImpl class");
		List<EmpUnitDto> empUnitDtoList;
		Long idSituation = stageDto.getIdSituation();
		// CMSC17D
		// This DAM will UPDATE DT_SITUATION_CLOSED on the SITUATION table based
		// on input of ID_SITUATION.
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		// Read situation Record
		Situation situation = situationDao.getSituationEntityById(idSituation);
		situation.setDtSituationClosed(null);
		// Updates situation Record
		situationDao.updateSituation(situation);
		// CCMNC6D - Close case
		CapsCase capsCase = capsCaseDao.getCapsCaseEntityById(idCase);
		capsCase.setDtCaseClosed(null);
		// Updates Case Record
		capsCaseDao.updateCapsCase(capsCase);
		// Read situation Record
		Stage stage = stageDao.getStageEntityById(idStage);
		stage.setDtStageClose(null);
		stage.setCdStageReasonClosed(null);
		stage.setTxtStageClosureCmnts(null);
		// Updates situation Record
		stageDao.updtStage(stage, serviceReqHeaderDto.getReqFuncCd());
		// CMSC15D
		// This dam updates the PAL table's living arrangment based upon id
		// stage.
		palInformationDao.updatePal(serviceReqHeaderDto, idStage, null);
		// CCMN39D
		// This DAM receives an ID PERSON and a CD UNIT MEMBER ROLE and returns
		// a full row from the UNIT table, a full row from the UNIT EMP LINK
		// table and NM PERSON FULL from
		// the PERSON table. The returned information applies to the unit to
		// which the ID PERSON is assigned with the given
		// CD UNIT MEMBER ROLE. The NM PERSON FULL is the name of the Unit
		// Approver for that unit.
		empUnitDtoList = unitDao.searchUnitAttributesByPersonId(idUser, ServiceConstants.UNIT_MEMBER_IN_ASSIGNED);
		if (!CollectionUtils.isEmpty(empUnitDtoList)) {
			// CAUDA0D
			// This dam retrieves the historical primary linked to stage. If a
			// historical primary is found the DAM returns the
			// Stage Person Link ID. If no historical primary record is found no
			// rows will be returned.
			StagePersonLinkDto stagePersonLinkDto = palInformationDao.getIdStagePersonLinkData(idStage);
			Long idStagePersonLink = stagePersonLinkDto.getIdStagePersonLink();
			if (!ObjectUtils.isEmpty(idStagePersonLink) && idStagePersonLink != 0l) {
				serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
				stagePersonLinkPRDto.setIdStagePersonLink(idStagePersonLink);
			} else {
				serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
			}
			// CCMND3D
			// If the required function is ADD: This DAM will perform a full row
			// add in the STAGE_PERSON_LINK table.
			stagePersonLinkPRDto.setCdStagePersRole(ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);
			stagePersonLinkPRDto.setCdStagePersType(ServiceConstants.STAFF);
			stagePersonLinkPRDto.setIndStagePersEmpNew(ServiceConstants.IND_STGPER_EMPNEW);
			stagePersonLinkPRDto.setIdPerson(idUser);
			stagePersonLinkPRDto.setDtStagePersLink(Calendar.getInstance().getTime());
			if (!TypeConvUtil.isNullOrEmpty(idStage)) {
				stagePersonLinkPRDto.setIdStage(idStage);
			}
			stagePersonLinkDao.getStagePersonLinkAUD(stagePersonLinkPRDto, serviceReqHeaderDto);
			// CCMN01U
			// This dam will create a new event.
			createPostEvent(idUser, idStage, idPersonChild);
			// CAUD64D
			// This DAM will update the CD EVENT STATUS for a row in the EVENT
			// table given CD EVENT TYPE and the ID STAGE. This
			// DAM will change all events for the stage with the CD EVENT TYPE
			// specified. It will change the CD EVENT STATUS to the status
			// specified in the input.
			eventDao.updateEventStatus(ServiceConstants.REQ_FUNC_CD_UPDATE, ServiceConstants.PAL_RUT_EVENT_TYPE,
					idStage, ServiceConstants.CEVTSTAT_PROC);
			eventDao.updateEventStatus(ServiceConstants.REQ_FUNC_CD_UPDATE, ServiceConstants.PAL_ILS_EVENT_TYPE,
					idStage, ServiceConstants.CEVTSTAT_PROC);
			logger.info("End - updatePalReopen method - PalInformationServiceImpl class");
		}
	}

	/**
	 * Method Name: createPostEvent Method Description: creates an event.
	 * 
	 * @param idPerson
	 * @param idStage
	 * @param idPersonChild
	 * @param taskCode
	 * @return
	 */
	private Long createPostEvent(Long idPerson, Long idStage, Long idPersonChild) {
		logger.info("Start - createPostEvent method - PalInformationServiceImpl class");
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
		List<PostEventDto> postEventDtoList = new ArrayList<>();
		Date dtCurrent = new Date();

		postEventIPDto.setEventDescr("PAL Stage has been Re-Opened");
		postEventIPDto.setCdEventType(ServiceConstants.PAL_PRO_EVENT_TYPE);
		postEventIPDto.setCdEventStatus(ServiceConstants.COMPLETE);
		postEventIPDto.setIdPerson(idPerson);
		postEventIPDto.setIdStage(idStage);
		archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		postEventIPDto.setTsLastUpdate(dtCurrent);
		postEventIPDto.setDtEventOccurred(dtCurrent);

		PostEventDto postEventDto = new PostEventDto();
		postEventDto.setIdPerson(idPersonChild);
		postEventDto.setCdScrDataAction(ServiceConstants.REQ_IND_AUD_ADD);
		postEventDtoList.add(postEventDto);
		postEventIPDto.setPostEventDto(postEventDtoList);

		PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, archInputDto);
		logger.info("End - createPostEvent method - PalInformationServiceImpl class");
		return postEventOPDto.getIdEvent();
	}

	/**
	 * 
	 * Method Name: populateInputForSaveSummary Method Description:Populates the
	 * input for Save Summary
	 * 
	 * @param pal
	 * @param userLogon
	 * @param idUser
	 * @param cdStageProgram
	 * @param dtLastUpdateList
	 * @return
	 */
	public PALSummaryDto populateInputForSaveSummary(PalInformationDto pal, String userLogon, Long idUser,
			String cdStageProgram, List<Date> dtLastUpdateList) {
		logger.info("Start - populateInputForSaveSummary method - PalInformationServiceImpl class");
		PALSummaryDto palSummaryDto = new PALSummaryDto();
		palSummaryDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		palSummaryDto.setUserId(userLogon);
		EventDto eventDto = new EventDto();
		eventDto.setCdTask(ServiceConstants.PAL_SUMMARY);
		if (dtLastUpdateList.size() >= 2) {
			eventDto.setDtLastUpdate(dtLastUpdateList.get(1));
		}
		eventDto.setEventDescr(lookupService.simpleDecodeSafe(CodesConstant.CPALCLRN, pal.getClosureReason()));
		eventDto.setIdPerson(idUser);
		palSummaryDto.setEventDto(eventDto);
		palSummaryDto.setDtStageClose(new Date());
		palSummaryDto.setCdPalCloseLivArr(pal.getClosureLivingArrangement());
		palSummaryDto.setCdStageProgram(cdStageProgram);
		palSummaryDto.setCdStageReasonClosed(pal.getClosureReason());
		if (!ObjectUtils.isEmpty(dtLastUpdateList)) {
			palSummaryDto.setDtLastUpdate(dtLastUpdateList.get(0));
		}
		palSummaryDto.setIdEvent(0l);
		palSummaryDto.setIdStage(pal.getIdStage());
		logger.info("End - populateInputForSaveSummary method - PalInformationServiceImpl class");
		return palSummaryDto;
	}

	/**
	 * Method Name: savePalInformation Method Description: This method is used
	 * to save PalInformation
	 * 
	 * @param palInformationDto
	 * @param palSummaryDto
	 * @param idPerson
	 * @param reqFunctionCd
	 *
	 *
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PalInformationRes savePalInformation(PalInformationReq palInformationReq) {
		logger.info("Start - savePalInformation method - PalInformationServiceImpl class");
		PalInformationDto palInformationDto = palInformationReq.getPalInformationDto();
		PalInformationRes palInformationRes = new PalInformationRes();
		palInformationRes = saveIlsAssessment(palInformationReq);
		if (ObjectUtils.isEmpty(palInformationDto.getDtClosure())
				&& !ObjectUtils.isEmpty(palInformationDto.getClosureReason())
				&& !ObjectUtils.isEmpty(palInformationDto.getClosureLivingArrangement()))
			palInformationRes = saveSummary(palInformationReq.getPalSummaryDto(), palInformationDto);
		logger.info("End - savePalInformation method - PalInformationServiceImpl class");
		return palInformationRes;
	}

	private PalInformationRes saveSummary(PALSummaryDto palSummaryDto, PalInformationDto palInformationDto) {
		PalInformationRes palInformationRes = new PalInformationRes();
		logger.info("Start - saveSummary method - PalInformationServiceImpl class");
		WorkloadStgPerLinkSelInDto workloadStgPerLinkSelInDto = new WorkloadStgPerLinkSelInDto();
		workloadStgPerLinkSelInDto.setIdStage(palSummaryDto.getIdStage());
		workloadStgPerLinkSelInDto.setCdStagePersRole(ServiceConstants.PRIMARY_CHILD);
		WorkloadStgPerLinkSelOutDto workloadStgPerLinkSelOutDto = workloadStgPerLinkSelDao
				.getWorkLoad(workloadStgPerLinkSelInDto).get(0);

		PostEventIPDto postEventIPDto = new PostEventIPDto();
		if (ObjectUtils.isEmpty(palSummaryDto.getIdEvent())
				|| ServiceConstants.Zero_Value.equals(palSummaryDto.getIdEvent())) {
			postEventIPDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		} else {
			postEventIPDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
			if (!ObjectUtils.isEmpty(palSummaryDto.getDtLastUpdateList())
					&& !palSummaryDto.getDtLastUpdateList().isEmpty())
				postEventIPDto.setTsLastUpdate(palSummaryDto.getDtLastUpdateList().get(0));
		}
		PostEventDto postEventDto = new PostEventDto();
		postEventDto.setCdScrDataAction(ServiceConstants.REQ_FUNC_CD_ADD);
		postEventDto.setIdPerson(workloadStgPerLinkSelOutDto.getIdTodoPersAssigned());
		List<PostEventDto> postEventDtoList = new ArrayList<>();
		postEventIPDto.setPostEventDto(postEventDtoList);
		postEventIPDto.setCdTask(ServiceConstants.PAL_ILS_TASK);
		postEventIPDto.setIdPerson(workloadStgPerLinkSelOutDto.getIdTodoPersAssigned());
		postEventIPDto.setCdEventStatus(ServiceConstants.EVENTSTATUS_COMPLETE);
		postEventIPDto.setDtEventOccurred(new Date());
		postEventIPDto.setCdEventType(ServiceConstants.EVENT_TYPE_PCL);
		postEventIPDto.setIdEvent(palSummaryDto.getIdEvent());
		postEventIPDto.setIdStage(palSummaryDto.getIdStage());
		postEventIPDto.setCdTask(ServiceConstants.PAL_ILS_TASK);

		postEventIPDto.setEventDescr(palSummaryDto.getEventDto().getEventDescr());
		ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
		archInputDto.setReqFuncCd(postEventIPDto.getReqFuncCd());
		postEventService.checkPostEventStatus(postEventIPDto, archInputDto);

		palInformationDto = palInformationDao.updatePALSummary(ServiceConstants.REQ_FUNC_CD_UPDATE,
				palSummaryDto.getIdStage(), palSummaryDto.getCdPalCloseLivArr(), palInformationDto);
		palInformationRes.setPalInformationDto(palInformationDto);
		if (ObjectUtils.isEmpty(palInformationDto.getErrorDto())) {
			boolean eventUpdated = eventDao.updateEventStatus(ServiceConstants.REQ_FUNC_CD_UPDATE,
					ServiceConstants.EVENTSTATUS_COMPLETE, palSummaryDto.getIdStage(), ServiceConstants.EVENT_TYPE_RUT);
			if (!eventUpdated) {
				eventDao.updateEventStatus(ServiceConstants.REQ_FUNC_CD_UPDATE, ServiceConstants.EVENTSTATUS_COMPLETE,
						palSummaryDto.getIdStage(), ServiceConstants.PAL_ILS_EVENT_TYPE);
			}
			Long idPALWorker = stageDao.getIdPersonForPALWorker(palSummaryDto.getIdStage());
			PersonDto personDto = personDao.getPersonById(workloadStgPerLinkSelOutDto.getIdTodoPersAssigned());
			TodoCommonFunctionInputDto todoCommonFunctionInputDto = new TodoCommonFunctionInputDto();
			TodoCommonFunctionDto commonTodoInDto = new TodoCommonFunctionDto();
			todoCommonFunctionInputDto.setReqFuncCd(palSummaryDto.getReqFuncCd());
			commonTodoInDto.setSysCdTodoCf(ServiceConstants.PAL_STAGE_TODO);
			commonTodoInDto.setSysTxtTodoCfDesc(ServiceConstants.TODO_DESCRIP.concat(personDto.getNmPersonFull()));
			commonTodoInDto.setDtSysDtTodoCfDueFrom(palSummaryDto.getDtStageClose());
			commonTodoInDto.setSysIdTodoCfPersAssgn(idPALWorker);
			commonTodoInDto.setSysIdTodoCfPersCrea(palSummaryDto.getEventDto().getIdPerson());
			commonTodoInDto.setSysIdTodoCfEvent(palSummaryDto.getIdEvent());
			commonTodoInDto.setSysIdTodoCfStage(palSummaryDto.getIdStage());
			// artf228724 DayCareAbsInd of false will cause due date to be null and the alert will not display. It's a long
			// story, but we've decided to fix these on a case-by-case basis because the due date depends on the alert type.
			// Surgical fixes also help us manage testing of the fixes.
			commonTodoInDto.setDayCareAbsInd(true);
			todoCommonFunctionInputDto.setTodoCommonFunctionDto(commonTodoInDto);
			commonTodoService.TodoCommonFunction(todoCommonFunctionInputDto);
			CloseStageCaseInputDto closeStageCaseInputDto = new CloseStageCaseInputDto();
			closeStageCaseInputDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
			closeStageCaseInputDto.setCdStageReasonClosed(palSummaryDto.getCdStageReasonClosed());
			closeStageCaseInputDto.setCdStage(ServiceConstants.CSTAGES_PAL);
			closeStageCaseInputDto.setIdStage(palSummaryDto.getIdStage());
			closeStageCaseInputDto.setCdStageProgram(palSummaryDto.getCdStageProgram());
			closeStageCaseService.closeStageCase(closeStageCaseInputDto);
			logger.info("End - saveSummary method - PalInformationServiceImpl class");
		}
		return palInformationRes;
	}

	/**
	 * Method Name: saveIlsAssessment Method Description: This method is used to
	 * save PalInformation
	 * 
	 * @param PalInformationReq
	 */

	private PalInformationRes saveIlsAssessment(PalInformationReq palInformationReq) {
		logger.info("Start - saveIlsAssessment method - PalInformationServiceImpl class");
		PalInformationDto palInformationDto = palInformationReq.getPalInformationDto();
		PalInformationRes palInformationRes = new PalInformationRes();
		if (!(ObjectUtils.isEmpty(palInformationDto.getIdAssessmentEvent()))
				|| (!ObjectUtils.isEmpty(palInformationDto.getNoIlsAssessment()))
				|| (!ObjectUtils.isEmpty(palInformationDto.getNoIlsReason()))
				|| (!ObjectUtils.isEmpty(palInformationDto.getDtPreAssessment()))
				|| (!ObjectUtils.isEmpty(palInformationDto.getPreAssessmentScore())
						|| (!ObjectUtils.isEmpty(palInformationDto.getPreAssessmentNoScore()))
						|| (!ObjectUtils.isEmpty(palInformationDto.getDtPostAssessment()))
						|| (!ObjectUtils.isEmpty(palInformationDto.getPostAssessmentScore()))
						|| (!ObjectUtils.isEmpty(palInformationDto.getPostAssessmentNoScore())))) {

			palInformationReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
			EventDto eventDto = eventDao.getEventByid(palInformationDto.getIdAssessmentEvent());
			if (ObjectUtils.isEmpty(eventDto)
					|| ServiceConstants.STATUS_NEW.equals(eventDto.getCdEventStatus())) {
				palInformationReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
			}
			// Call CCFC02S

			WorkloadStgPerLinkSelInDto workloadStgPerLinkSelInDto = new WorkloadStgPerLinkSelInDto();
			workloadStgPerLinkSelInDto.setCdStagePersRole(ServiceConstants.PRIMARY_CHILD);
			workloadStgPerLinkSelInDto.setIdStage(palInformationDto.getIdStage());
			List<WorkloadStgPerLinkSelOutDto> workloadStgPerLinkS = workloadStgPerLinkSelDao
					.getWorkLoad(workloadStgPerLinkSelInDto);
			/*
			 * Place id_person of the Primary Child on the Event Person Link
			 * Table
			 * 
			 */
			Long idPerson = null;
			Optional<WorkloadStgPerLinkSelOutDto> workloadStgPerLinkSelOutDtoOpt = workloadStgPerLinkS.stream()
					.findFirst();
			if (workloadStgPerLinkSelOutDtoOpt.isPresent())
				idPerson = workloadStgPerLinkSelOutDtoOpt.get().getIdTodoPersAssigned();
			else
				throw new ServiceLayerException("9003");
			PostEventIPDto postEventIPDto = new PostEventIPDto();
			if (!ObjectUtils.isEmpty(palInformationDto.getIdAssessmentEvent())
					&& palInformationDto.getIdAssessmentEvent() > ServiceConstants.ZERO) {
				postEventIPDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
				postEventIPDto.setTsLastUpdate(palInformationDto.getDtAssessmentLastUpdate());
			} else {
				postEventIPDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
			}
			postEventIPDto.setIdPerson(idPerson);
			postEventIPDto.setCdEventStatus(ServiceConstants.EVENTSTATUS_PROCESS);
			postEventIPDto.setCdEventType(ServiceConstants.PAL_ILS_EVENT_TYPE);
			postEventIPDto.setDtEventOccurred(new Date());
			postEventIPDto.setIdEvent(palInformationDto.getIdAssessmentEvent());
			postEventIPDto.setIdStage(palInformationDto.getIdStage());
			postEventIPDto.setIdCase(palInformationDto.getIdCase());
			postEventIPDto.setCdTask(ServiceConstants.PAL_ILS_TASK);
			postEventIPDto.setEventDescr(ServiceConstants.INDEPENDENT_LIVING_SKILLS);
			postEventIPDto.setUserId(palInformationReq.getUserId());
			ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
			archInputDto.setReqFuncCd(postEventIPDto.getReqFuncCd());
			PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, archInputDto);

			if (!ObjectUtils.isEmpty(postEventOPDto)) {

				// Call CAUD49D
				palInformationDto = palInformationDao.saveIlsAssessment(palInformationDto,
						palInformationReq.getReqFuncCd());
			}
			// Call CINV43D
			UpdateToDoDto updateToDoDto = new UpdateToDoDto();
			updateToDoDto.setIdEvent(postEventOPDto.getIdEvent());
			todoDao.completeTodo(updateToDoDto);

		}
		palInformationRes.setPalInformationDto(palInformationDto);
		logger.info("End - saveIlsAssessment method - PalInformationServiceImpl class");
		return palInformationRes;
	}

}
