package us.tx.state.dfps.service.basepcasession.serviceimpl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.admin.dto.TodoCreateInDto;
import us.tx.state.dfps.service.admin.dto.TodoCreateOutDto;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.basepcasession.dao.BasePcaSessionDao;
import us.tx.state.dfps.service.basepcasession.service.BasePcaSessionService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.PersonDtlReq;
import us.tx.state.dfps.service.common.response.PersonDtlRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.commontodofunction.service.CommonToDoFunctionService;
import us.tx.state.dfps.service.eventutility.service.EventUtilityService;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.pca.dto.PcaAppAndBackgroundDto;
import us.tx.state.dfps.service.pca.dto.PcaApplAndDetermDBDto;
import us.tx.state.dfps.service.pcaappandbackground.dao.PcaAppAndBackgroundDao;
import us.tx.state.dfps.service.pcaappandbackground.service.PcaAppAndBackgroundService;
import us.tx.state.dfps.service.person.dto.PersonDtlDto;
import us.tx.state.dfps.service.person.service.PersonDtlService;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;
import us.tx.state.dfps.xmlstructs.inputstructs.MergeSplitToDoDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ServiceInputDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * BasePcaSessionServiceImpl for BasePcaSession Oct 6, 2ServiceConstants.Zero17-
 * 3:ServiceConstants.Zero3:42 PM © 2ServiceConstants.Zero17 Texas Department of
 * Family and Protective Services
 */

@Service
@Transactional
public class BasePcaSessionServiceImpl implements BasePcaSessionService {

	@Autowired
	BasePcaSessionDao basePcaSessionDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	PostEventService postEventService;

	@Autowired
	PcaAppAndBackgroundDao pcaAppAndBackgroundDao;

	@Autowired
	CaseUtils caseUtils;

	@Autowired
	EventUtilityService eventUtilityService;

	@Autowired
	PersonDtlService personDtlService;

	@Autowired
	CommonToDoFunctionService commonToDoFunctionService;

	@Autowired
	PcaAppAndBackgroundService pcaAppAndBackgroundService;

	private static final Logger log = Logger.getLogger(BasePcaSessionServiceImpl.class);

	/**
	 * Method Name: updateAppEvent
	 * 
	 * @param idPcaEligApplication
	 * @param eventValueDto
	 * @param idUpdatePerson
	 * @param evtStatus
	 * @param eventDesc
	 * @return long @
	 */

	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED,
	 * propagation = Propagation.REQUIRED) public long updateAppEvent(long
	 * idPcaEligApplication, EventValueDto eventValueDto, long idUpdatePerson,
	 * String evtStatus, String eventDesc) {
	 * LOG.debug("Entering method updateAppEvent in BasePcaSessionService");
	 * long Result = ServiceConstants.Zero; try {
	 * updateEventStatus(eventValueDto, eventValueDto.getIdPerson(), evtStatus,
	 * eventDesc); List eventList =
	 * pcaAppAndBackgroundDao.fetchPcaAppEvents(idPcaEligApplication); for (int
	 * index = ServiceConstants.Zero; index < eventList.size(); index++) {
	 * EventValueDto pcaAppEvent = (EventValueDto) eventList.get(index); if
	 * (pcaAppEvent.getIdEvent() != eventValueDto.getIdEvent()) { pcaAppEvent =
	 * eventUtilityService.fetchEventInfo(pcaAppEvent.getIdEvent());
	 * pcaAppEvent.setCdEventStatus(eventValueDto.getCdEventStatus());
	 * pcaAppEvent.setEventDescr(eventValueDto.getEventDescr());
	 * pcaAppEvent.setIdPerson(eventValueDto.getIdPerson()); String cdReqFunc =
	 * idPcaEligApplication != 0L ? ServiceConstants.REQ_FUNC_CD_ADD:
	 * ServiceConstants.REQ_FUNC_CD_UPD;
	 * pcaAppEvent.setReqFunctionCode(cdReqFunc); Result =
	 * updateEventStatus(pcaAppEvent, idUpdatePerson, evtStatus, eventDesc); } }
	 * } catch (DataNotFoundException e) { LOG.error(e.getMessage()); }
	 * LOG.debug("Exiting method updateAppEvent in BasePcaSessionService");
	 * return Result; }
	 */

	/**
	 * method name: updateEventStatus Description: This is helper function to
	 * update Event Status by calling PostEventBean.
	 * 
	 * @param eventValueDto
	 * @param idUpdatePerson
	 * @param evtStatus
	 * @param eventDesc
	 * @return long @
	 */

	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED,
	 * propagation = Propagation.REQUIRED) public long
	 * updateEventStatus(EventValueDto eventValueDto, long idUpdatePerson,
	 * String evtStatus, String eventDesc) {
	 * LOG.debug("Entering method updateEventStatus in BasePcaSessionService");
	 * long idEvent = ServiceConstants.Zero; PostEventOPDto postEventOPDto = new
	 * PostEventOPDto(); PostEventIPDto postEventIPDto = new PostEventIPDto();
	 * ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
	 * postEventIPDto.setCdEventStatus(ServiceConstants.EVENTSTATUS_COMPLETE);
	 * postEventIPDto.setCdEventType(ServiceConstants.EVENTSTATUS_ASSIGNMENT);
	 * postEventIPDto.setIdPerson(idUpdatePerson); try { if
	 * (!TypeConvUtil.isNullOrEmpty(evtStatus)) {
	 * eventValueDto.setCdEventStatus(evtStatus); } if
	 * (!TypeConvUtil.isNullOrEmpty(eventDesc)) {
	 * eventValueDto.setEventDescr(eventDesc); } if (idUpdatePerson !=
	 * ServiceConstants.Zero) { eventValueDto.setIdPerson(idUpdatePerson); }
	 * 
	 * Date dtlastUpdate = new Date();
	 * serviceReqHeaderDto.setcReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD); //
	 * postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto,
	 * serviceReqHeaderDto);
	 * 
	 * idEvent = postEventOPDto !=null ? postEventOPDto.getIdEvent() : idEvent;
	 * //idEvent = postEventService.checkPostEventStatus(postEventIPDto,
	 * ServiceReqHeaderDto)(eventValueDto, ServiceConstants.REQ_FUNC_CD_UPDATE,
	 * ServiceConstants.Zero); } catch (DataNotFoundException e) {
	 * LOG.error(e.getMessage()); } return idEvent; }
	 */

	/**
	 * method name:createPcaRecertTodo description: This method creates Pca
	 * Recertification Todo for Primary Worker.
	 * 
	 * @param eventValueDto
	 * @param cdTodoInfoType
	 * @param dtToDoDue
	 * @param idPersonPC
	 * @
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public TodoCreateOutDto createPcaRecertTodo(EventValueDto eventValueDto, String cdTodoInfoType, Date dtToDoDue,
			long idPersonPC) {
		log.debug("Entering method createPcaRecertTodo in BasePcaSessionService");
		TodoCreateOutDto todoCreateOutDto = new TodoCreateOutDto();
		try {
			TodoCreateInDto todoCreateInDto = new TodoCreateInDto();
			MergeSplitToDoDto mergeSplitToDoDto = new MergeSplitToDoDto();
			todoCreateInDto.setServiceInputDto(new ServiceInputDto());
			mergeSplitToDoDto.setCdTask(ServiceConstants.CD_TASK_PCA_PCA_APPL_REVIEW);
			mergeSplitToDoDto.setCdTodoCf(cdTodoInfoType);
			mergeSplitToDoDto.setDtTodoCfDueFrom(DateUtils.toCastorDate(dtToDoDue));
			mergeSplitToDoDto.setIdTodoCfPersCrea(eventValueDto.getIdPerson());
			mergeSplitToDoDto.setIdTodoCfStage(eventValueDto.getIdStage());
			if (!TypeConvUtil.isNullOrEmpty(findEligibilityOrPrimayWorkerForStage(eventValueDto.getIdStage()))) {
				mergeSplitToDoDto
						.setIdTodoCfPersAssgn(findEligibilityOrPrimayWorkerForStage(eventValueDto.getIdStage()));
			}
			mergeSplitToDoDto.setIdTodoCfPersWkr(eventValueDto.getIdPerson());
			PersonDtlReq personDtlReq = new PersonDtlReq();
			personDtlReq.setIdPerson(idPersonPC);
			mergeSplitToDoDto.setIdTodoCfEvent(eventValueDto.getIdEvent());
			PersonDtlRes personDetlsRes = personDtlService.getPersonDtl(personDtlReq);
			PersonDtlDto personValBean = personDetlsRes.getPersonDtlDtoList();
			String toDoDesc = "PCA Recertification is due for " + personValBean.getNmPersonMaidenName();
			mergeSplitToDoDto.setTodoCfDesc(toDoDesc);
			todoCreateInDto.setMergeSplitToDoDto(mergeSplitToDoDto);
			todoCreateOutDto = commonToDoFunctionService.callCSUB40U(todoCreateInDto);
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}
		return todoCreateOutDto;
	}

	/**
	 * method name:findEligibilityOrPrimayWorkerForStage description: This
	 * function return if Eligibility Specialist that is assigned to stage. If
	 * None assigned returns Primary worker for Stage.
	 * 
	 * @param idStage
	 * @return long @
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public long findEligibilityOrPrimayWorkerForStage(long idStage) {
		log.debug("Entering method findEligibilityOrPrimayWorkerForStage in BasePcaSessionService");

		long idWorker = ServiceConstants.Zero;
		try {
			String[] eligWorkerProfiles = ServiceConstants.eligWorkerProfiles;
			List<StagePersonLinkDto> fcWorkers = stageDao.findWorkersForStage(idStage, eligWorkerProfiles);
			if (!TypeConvUtil.isNullOrEmpty(fcWorkers) && fcWorkers.size() > ServiceConstants.Zero) {
				idWorker = fcWorkers.get(ServiceConstants.Zero).getIdPerson();
			} else {
				idWorker = caseUtils.getPrimaryWorkerIdForStage(idStage);
			}
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}
		return idWorker;

	}

	/**
	 * method name: withdrawPCAApplication description: This method is called to
	 * withdraw the PCA Application process. It updates all the Events
	 * associated with the Application.
	 * 
	 * @param idPcaEligApplication
	 * @param eventValueDto
	 * @return long @
	 */

	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long withdrawPCAApplication(PcaApplAndDetermDBDto pcaAppDB, long idPcaEligApplication,
			EventValueDto eventValueDto) {
		log.debug("Entering method withdrawPCAApplication in BasePcaSessionService");
		long idEvent = ServiceConstants.Zero;
		try {
			/*
			 * result = updateAppEvent(idPcaEligApplication, eventValueDto,
			 * eventValueDto.getIdPerson(), ServiceConstants.CEVTSTAT_COMP,
			 * ServiceConstants.APPLICATION_COMP_WITHDRAW_FROM_PCA_PROCESS_DESC)
			 * ;
			 */

			eventValueDto.setCdEventStatus(ServiceConstants.CEVTSTAT_COMP);
			eventValueDto.setEventDescr(ServiceConstants.APPLICATION_COMP_WITHDRAW_FROM_PCA_PROCESS_DESC);
			eventValueDto.setCdEventType(ServiceConstants.PCA_APPLICATION_EVENT_TYPE);
			idEvent = pcaAppAndBackgroundService.updateAppEvent(pcaAppDB.getAppValueBean(), eventValueDto,
					pcaAppDB.getAppValueBean().getIdLastUpdatePerson(), ServiceConstants.CEVTSTAT_COMP,
					ServiceConstants.APPLICATION_COMP_WITHDRAW_FROM_PCA_PROCESS_DESC);
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}
		return idEvent;
	}

	/**
	 * method name: isChildSibling1 description: This method has the logic to
	 * find out if the Current child is Sibling 1.
	 * 
	 * @param pcaAppAndBackgroundDto
	 * @return boolean @
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public boolean isChildSibling1(PcaAppAndBackgroundDto pcaAppAndBackgroundDto) {
		log.debug("Entering method isChildSibling1 in BasePcaSessionService");

		boolean isChild1 = true;
		try {
			if (pcaAppAndBackgroundDto.getIdQualSibPerson() != 0) {
				String[] statusArray = ServiceConstants.statusArray;
				PcaAppAndBackgroundDto siblingApp = pcaAppAndBackgroundDao
						.selectLatestApplication(pcaAppAndBackgroundDto.getIdQualSibPerson(), statusArray);
				if (siblingApp.getIdPcaEligApplication() != 0
						&& ServiceConstants.Y.equals(siblingApp.getIndChildSibling1())) {
					isChild1 = false;
				}
			}
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}
		return isChild1;

	}
}
