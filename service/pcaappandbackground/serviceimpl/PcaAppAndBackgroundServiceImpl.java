/**
 *service-ejb-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Nov 14, 2017- 2:31:19 PM
 *© 2017 Texas Department of Family and Protective Services
 */
package us.tx.state.dfps.service.pcaappandbackground.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.PostEventPersonDto;
import us.tx.state.dfps.service.admin.dao.FetchEventDao;
import us.tx.state.dfps.service.admin.dto.FetchEventDto;
import us.tx.state.dfps.service.admin.dto.FetchEventResultDto;
import us.tx.state.dfps.service.admin.service.AdminEventService;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.basepcasession.service.BasePcaSessionService;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CommonEventIdReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PcaAppAndBackgroundReq;
import us.tx.state.dfps.service.common.request.PcaApplAndDetermReq;
import us.tx.state.dfps.service.common.request.PostEventReq;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.CommonIdRes;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.PcaAppAndBackgroundRes;
import us.tx.state.dfps.service.common.response.PostEventRes;
import us.tx.state.dfps.service.common.response.StageRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.PersonUtil;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.eventutility.service.EventUtilityService;
import us.tx.state.dfps.service.fce.EligibilityDeterminationDBDto;
import us.tx.state.dfps.service.pca.dto.PcaAppAndBackgroundDto;
import us.tx.state.dfps.service.pca.dto.PcaApplAndDetermDBDto;
import us.tx.state.dfps.service.pca.dto.PcaEligDeterminationDto;
import us.tx.state.dfps.service.pcaappandbackground.dao.PcaAppAndBackgroundDao;
import us.tx.state.dfps.service.pcaappandbackground.service.PcaAppAndBackgroundService;
import us.tx.state.dfps.service.pcaeligdetermination.dao.PcaEligDeterminationDao;
import us.tx.state.dfps.service.pcaeligdetermination.utils.PcaEligDetermUtils;
import us.tx.state.dfps.service.pcaeligdetermination.utils.PcaUtility;
import us.tx.state.dfps.service.person.dao.ChildPlanDao;
import us.tx.state.dfps.service.person.dao.FceEligibilityDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.PersonDtlDao;
import us.tx.state.dfps.service.person.dto.PersonValueDto;
import us.tx.state.dfps.service.resource.dto.ResourceValueBeanDto;
import us.tx.state.dfps.service.resourcesearch.dao.ResourceSearchDao;
import us.tx.state.dfps.service.stageprogression.service.StageProgressionService;
import us.tx.state.dfps.service.workload.dao.StageProgDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-ejb-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * class will have business logic for fetch,save and submit PCA details
 *
 * Nov 14, 2017- 2:31:19 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Service
@Transactional
public class PcaAppAndBackgroundServiceImpl implements PcaAppAndBackgroundService {

	private String[] statusArray = { ServiceConstants.CEVTSTAT_PEND, ServiceConstants.CEVTSTAT_COMP,
			ServiceConstants.CEVTSTAT_APRV };

	/**
	 *
	 *
	 */
	private static final String PRIMARY_CHILD_DOES_NOT_EXIST_FOR_THIS_STAGE = "Primary Child does not exist for this stage : ";
	private static final String PRIMARY_CHILD = "PC";

	@Autowired
	AdminEventService ccmn01uService;

	@Autowired
	BasePcaSessionService basePcaSessionService;

	@Autowired
	StageProgressionService stageProgressionService;

	@Autowired
	EventUtilityService eventUtilityService;

	@Autowired
	PersonUtil personUtil;

	@Autowired
	PcaUtility pcaUtility;

	@Autowired
	PcaEligDeterminationDao pcaEligDeterminationDao;

	@Autowired
	StageProgDao stageProgDao;

	@Autowired
	ChildPlanDao childPlanDao;

	@Autowired
	FceEligibilityDao fceEligibilityDao;

	@Autowired
	PcaAppAndBackgroundDao pcaAppAndBackgroundDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	FetchEventDao fetchEventDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	PersonDtlDao personDtlDao;

	@Autowired
	ResourceSearchDao resourceSearchDao;

	@Autowired
	EventService eventService;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.pcaappandbackground.service.
	 * PcaAppAndBackgroundService#fetchApplicationDetails(us.tx.state.dfps.
	 * service.common.request.PcaAppAndBackgroundReq)
	 */
	@Override
	public PcaAppAndBackgroundRes fetchApplicationDetails(PcaAppAndBackgroundReq pcaAppAndBackgroundReq) {
		PcaApplAndDetermDBDto pcaApplAndDetermDBDto = new PcaApplAndDetermDBDto();
		Long idAppEvent = pcaAppAndBackgroundReq.getIdAppEvent();
		EventValueDto eventValueDto = new EventValueDto();

		Long idSubStage = findPriorSubStageId(pcaAppAndBackgroundReq);
		// Retrieve App & Background from database.
		PcaAppAndBackgroundDto pcaAppAndBackgroundDto = (0 != idAppEvent)
				? pcaAppAndBackgroundDao.selectPcaEligAppFromEvent(idAppEvent) : new PcaAppAndBackgroundDto();
		pcaApplAndDetermDBDto.setAppValueBean(pcaAppAndBackgroundDto);
		// New Using Button Pressed.
		if (null != pcaApplAndDetermDBDto && pcaAppAndBackgroundReq.getNewUsing()) {
			// Set app background detail in eligibility determination dto
			pcaApplAndDetermDBDto.setAppValueBean(pcaAppAndBackgroundDto);
			// Clear Some values.
			pcaAppAndBackgroundDto.setIdPcaEligApplication(ServiceConstants.ZERO_VAL);
			PcaUtility.clearAppWithdrawal(pcaAppAndBackgroundDto);
			// Populate Sibling Info and Placement Info for Application.
			populateSblgAndPlcmtInfo(pcaAppAndBackgroundReq.getIdStage(), pcaApplAndDetermDBDto);
		}
		// Existing Application.
		else if (idAppEvent != 0L) {
			// Retrieve Eligibility Determination, if exists.
			PcaEligDeterminationDto pcaEligDeterminationDto = pcaAppAndBackgroundDao
					.selectEligFromIdPcaApp(pcaAppAndBackgroundDto.getIdPcaEligApplication());
			pcaApplAndDetermDBDto.setDetermValueBean(pcaEligDeterminationDto);
			// Retrieve Application Event.
			eventValueDto = fetchEventInfo(idAppEvent);
			pcaApplAndDetermDBDto.setAppEvent(eventValueDto);
			// Populate Sibling Info and Placement Info for Application.
			populateSblgAndPlcmtInfo(pcaAppAndBackgroundReq.getIdStage(), pcaApplAndDetermDBDto);
		} else {
			pcaAppAndBackgroundDto = new PcaAppAndBackgroundDto();
			Long idPersonPC = stageDao.findPrimaryChildForStage(pcaAppAndBackgroundReq.getIdStage());
			if (idPersonPC == 0) {
				throw new IllegalStateException(
						PRIMARY_CHILD_DOES_NOT_EXIST_FOR_THIS_STAGE + pcaAppAndBackgroundReq.getIdStage());
			}
			pcaAppAndBackgroundDto.setIdPerson(idPersonPC);
			// Populate indHmRemJudicial from Most Recent Fce.
			populateFceHmRemJudicial(idSubStage, pcaAppAndBackgroundDto);

		}
		// Get Primary Child.
		PersonDto personDto = personDao.getPersonById(pcaAppAndBackgroundDto.getIdPerson());
		if (personDto != null) {
			pcaApplAndDetermDBDto.setFullName(personDto.getNmPersonFull());
			pcaApplAndDetermDBDto.setDateOfBirth(personDto.getDtPersonBirth());
		}
		// Get Social Security Number.
		pcaApplAndDetermDBDto.setNbrSocialSecurity(personUtil.findSsn(pcaAppAndBackgroundDto.getIdPerson()));
		// Get Medicaid Number.
		pcaApplAndDetermDBDto.setNbrMedicaid(personUtil.findMedicaid(pcaAppAndBackgroundDto.getIdPerson()));

		// fetch the Legal Status Latest
		pcaApplAndDetermDBDto
				.setCdLegalStatStatus(pcaUtility.findLatestLegalStatusForChild(pcaAppAndBackgroundDto.getIdPerson()));

		CommonStringRes commonStringRes = personDtlDao.getBirthCitizenShip(pcaAppAndBackgroundDto.getIdPerson());
		pcaApplAndDetermDBDto.setCdPersonBirthCitizenship(commonStringRes.getCommonRes());

		// Fetch Permanency Goal and Age at Qualification until the Application
		// is Approved.
		if (!ServiceConstants.CEVTSTAT_APRV.equals(eventValueDto.getCdEventStatus())) {
			// Get Most Recent Permanency Goal for the Child.
			CommonStringRes childPlanRes = childPlanDao.selectMostRecentChildPlanPermGoal(idSubStage);
			pcaAppAndBackgroundDto.setCdChildPermGoal(childPlanRes.getCommonRes());
			// Age at Qualification.
			if (personDto != null && personDto.getDtPersonBirth() != null) {
				pcaAppAndBackgroundDto
						.setNbrChildQualifyAge(Long.valueOf(DateUtils.getAge(personDto.getDtPersonBirth())));
			}
		}

		pcaApplAndDetermDBDto.setAppValueBean(pcaAppAndBackgroundDto);
		PcaAppAndBackgroundRes pcaAppAndBackgroundRes = new PcaAppAndBackgroundRes();
		pcaAppAndBackgroundRes.setPcaApplAndDetermDBDto(pcaApplAndDetermDBDto);

		return pcaAppAndBackgroundRes;
	}

	/**
	 * Method Name: populateFceHmRemJudicial Method Description:Get Most Recent
	 * Fce Determination Record and populate.
	 *
	 * @param idStage
	 * @param appValueBean
	 * @
	 */
	private void populateFceHmRemJudicial(Long idSubStage, PcaAppAndBackgroundDto appValueBean) {
		EligibilityDeterminationDBDto fceEligDetermDb = fceEligibilityDao.selectLatestEligDeterm(idSubStage);
		if (fceEligDetermDb != null && fceEligDetermDb.getIdFceEligibility() != 0) {
			Boolean indFceRmvlOrdered = fceEligDetermDb.getIndRemovalChildOrdered();
			appValueBean.setIdFceEligibility(fceEligDetermDb.getIdFceEligibility());
			// Fix for defect 13402 - 8888 Error When Adding PCA Application
			if (!ObjectUtils.isEmpty(indFceRmvlOrdered) && indFceRmvlOrdered) {
				appValueBean.setIndFceRmvlChildOrdered(ServiceConstants.Y);
				appValueBean.setIndHmRemJudicial(ServiceConstants.Y);
			} else {
				appValueBean.setIndFceRmvlChildOrdered(ServiceConstants.N);
				appValueBean.setIndHmRemJudicial(ServiceConstants.N);
			}
		}

	}

	/**
	 * Method Name: fetchEventInfo Method Description:
	 *
	 * @param idAppEvent
	 * @return
	 */
	private EventValueDto fetchEventInfo(Long idAppEvent) {
		EventValueDto eventValueDto = new EventValueDto();
		FetchEventDto fetchEventDto = new FetchEventDto();
		fetchEventDto.setIdEvent(idAppEvent);
		List<FetchEventResultDto> fetchEventResultDtos = fetchEventDao.fetchEventDao(fetchEventDto);
		FetchEventResultDto fetchEventResultDto = fetchEventResultDtos.get(0);
		eventValueDto.setCdEventType(fetchEventResultDto.getCdEventType());
		eventValueDto.setEventDescr(fetchEventResultDto.getTxtEventDescr());
		eventValueDto.setCdEventTask(fetchEventResultDto.getCdTask());
		eventValueDto.setCdEventStatus(fetchEventResultDto.getCdEventStatus());
		eventValueDto.setIdEvent(fetchEventResultDto.getIdEvent());
		eventValueDto.setIdStage(fetchEventResultDto.getIdStage());
		eventValueDto.setIdPerson(fetchEventResultDto.getIdPerson());
		eventValueDto.setDtEventOccurred(fetchEventResultDto.getDtDtEventOccurred());
		eventValueDto.setDtLastUpdate(fetchEventResultDto.getDtLastUpdate());
		eventValueDto.setIdCase(fetchEventResultDto.getIdCase());
		eventValueDto.setDtEventCreated(fetchEventResultDto.getDtDtEventCreated());
		
		return eventValueDto;
	}

	/**
	 * Method Name: populateSblgAndPlcmtInfo Method Description:This method
	 * Populates Sibling Info if Qualified Sibling is present for Application
	 * and Populates Placement Info if Placement is present for Application.
	 *
	 * @param idStage
	 * @param pcaApplAndDetermDBDto
	 */
	private void populateSblgAndPlcmtInfo(Long idStage, PcaApplAndDetermDBDto pcaAppDBDto) {
		PcaAppAndBackgroundDto appValueBean = pcaAppDBDto.getAppValueBean();
		// Set the sibling info.
		if (!TypeConvUtil.isNullOrEmpty(appValueBean.getIdQualSibPerson())) {
			PersonValueDto siblingPersonInfo = personDao.fetchStagePersonLinkInfo(idStage,
					appValueBean.getIdQualSibPerson());
			appValueBean.setNameQualSibPersonFull(siblingPersonInfo.getFullName());
			appValueBean.setCdStageQualSibPersonRelInt(siblingPersonInfo.getRoleInStageCode());
			if (!ObjectUtils.isEmpty(siblingPersonInfo.getAge()))
				appValueBean.setNbrAgeQualSibPerson(Long.valueOf(siblingPersonInfo.getAge()));
		}
		// Set the Placement info.
		if (!TypeConvUtil.isNullOrEmpty(appValueBean) && !TypeConvUtil.isNullOrEmpty(appValueBean.getIdPlcmtEvent())) {
			EventValueDto plcmtEvent = fetchEventInfo(appValueBean.getIdPlcmtEvent());

			// fetch placement created by detail
			/*
			 * PersonValueDto plcmntCreatedByInfo =
			 * personDao.fetchStagePersonLinkInfo(idStage,
			 * plcmtEvent.getIdPerson()); if(null != plcmtEvent && null !=
			 * plcmntCreatedByInfo) {
			 * plcmtEvent.setUpdatedBy(plcmntCreatedByInfo.getFullName()); }
			 */
			pcaAppDBDto.setPlcmtEvent(plcmtEvent);
		}

	}

	/**
	 * Method Name: findPriorSubStageId Method Description:This method returns
	 * Corresponding Sub Stage Id for the Given Stage Id.
	 *
	 * @param pcaAppAndBackgroundReq
	 * @return @
	 */
	@Override
	public Long findPriorSubStageId(PcaAppAndBackgroundReq pcaAppAndBackgroundReq) {
		Long idSubStage = pcaAppAndBackgroundReq.getIdStage();
		StageDto stageDto = stageDao.getStageById(pcaAppAndBackgroundReq.getIdStage());

		if (stageDto != null && !ServiceConstants.CSTAGES_SUB.equals(stageDto.getCdStage())) {
			stageDto = pcaAppAndBackgroundDao.findLinkedSubStage(idSubStage, ServiceConstants.CSTAGES_SUB);

		}
		if (stageDto != null) {
			idSubStage = stageDto.getIdStage();
		}

		return idSubStage;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.pcaappandbackground.service.
	 * PcaAppAndBackgroundService#saveApplAndBackgroundInfo(us.tx.state.dfps.
	 * service.common.request.PcaApplAndDetermReq)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public CommonIdRes saveApplAndBackgroundInfo(PcaApplAndDetermReq pcaApplAndDetermReq) {
		PcaApplAndDetermDBDto pcaApplAndDetermDBDto = pcaApplAndDetermReq.getPcaApplAndDetermDBDto();
		CommonIdRes resp = new CommonIdRes();
		Long idAppEvent = 0L;
		if (pcaApplAndDetermDBDto != null) {
			PcaAppAndBackgroundDto appValueBean = pcaApplAndDetermDBDto.getAppValueBean();

			if (!ServiceConstants.Y.equals(appValueBean.getIndCtznshpPerm())
					&& !ServiceConstants.Y.equals(appValueBean.getIndCtznshpOthQualAlien())) {
				appValueBean.setDtChildEnteredUs(null);
				appValueBean.setTxtChildEnteredUs(null);
			}
			// New Application
			if (appValueBean.getIdPcaEligApplication() == null || 0l == appValueBean.getIdPcaEligApplication()) {
				pcaApplAndDetermDBDto.getAppEvent().setReqFunctionCode(ServiceConstants.REQ_FUNC_CD_ADD);
				idAppEvent = createNewPcaApplication(pcaApplAndDetermDBDto);

			} else
			// Update Existing Application
			{
				pcaApplAndDetermDBDto.getAppEvent().setReqFunctionCode(ServiceConstants.REQ_FUNC_CD_UPDATE);
				idAppEvent = updateExistingApplication(pcaApplAndDetermDBDto);
			}
			resp.setResultId(idAppEvent);
		}
		return resp;

	}

	/**
	 * Method Name: updateExistingApplication Method Description:This method
	 * updates Existing PCA Application(with event) and Background information.
	 * It also updates Event Status to COMP in case of Withdrawal of
	 * Application.
	 *
	 * @param pcaApplAndDetermDBDto
	 * @return @
	 */
	private Long updateExistingApplication(PcaApplAndDetermDBDto pcaAppDB) {
		PcaAppAndBackgroundDto appValueBean = pcaAppDB.getAppValueBean();
		Long idPcaEligApplication = appValueBean.getIdPcaEligApplication();
		EventValueDto appEvent = pcaAppDB.getAppEvent();
		String eventStatusDescr = null;

		// If the answer to "Select Sibling" question is No, clear sibling
		// details.
		if (ServiceConstants.N.equals(appValueBean.getIndWithSibling())) {
			appValueBean.setIdQualSibPerson(ServiceConstants.LongZero);
		}

		// If Withdraw reason is selected, update all the application events.
		if (TypeConvUtil.isValid(appValueBean.getCdWithdrawRsn())) {
			basePcaSessionService.withdrawPCAApplication(pcaAppDB, idPcaEligApplication, appEvent);
			// Since PCA Application can be seen from both SUB and PCA stages
			// there can be
			// two events for the same Application.
			// TODOs will be created for the Event in PCA Stage.
			// If the Application is withdrawn from SUB stage, mark TODOs
			// complete for both Pca Event also.
			List pcaAppEventList = pcaAppAndBackgroundDao.fetchPcaAppEvents(idPcaEligApplication);
			for (int index = 0; index < pcaAppEventList.size(); index++) {
				EventValueDto pcaAppEvent = (EventValueDto) pcaAppEventList.get(index);
				// Mark the associated Todo with event as complete.
				pcaAppAndBackgroundDao.markTodoComplete(pcaAppEvent.getIdEvent());
			}
		}
		// If the Application is in COMP or APRV status and user tries to update
		// the Application, status should be switched back to Pending.
		else {
			String eventStatus = appEvent.getCdEventStatus();
			if (!ServiceConstants.CEVTSTAT_PROC.equals(eventStatus)) {
				eventStatus = ServiceConstants.CEVTSTAT_PEND;
			}

			if ((ServiceConstants.CEVTSTAT_PROC).equalsIgnoreCase(eventStatus)) {
				eventStatusDescr = ServiceConstants.PROCESS_EVENT_APP;
			} else if ((ServiceConstants.CEVTSTAT_PEND).equalsIgnoreCase(eventStatus)) {
				eventStatusDescr = ServiceConstants.PENDING_EVENT_APP;
			} else if ((ServiceConstants.CEVTSTAT_APRV).equalsIgnoreCase(eventStatus)) {
				eventStatusDescr = ServiceConstants.PCA_WITHDRAW_EVENT_DESC;
			}

			updateAppEvent(appValueBean, appEvent, pcaAppDB.getAppValueBean().getIdLastUpdatePerson(), eventStatus,
					eventStatusDescr);
		}
		// Update PCA Eligibility Application.
		pcaAppAndBackgroundDao.updatePcaEligApplication(appValueBean);
		// If the Eligibility Determination record exist then save that also.
		//artf247565 : idPcaEligDeterm is can be null. make sure it is not null before comparing it with 0
		if (null != pcaAppDB.getDetermValueBean() && pcaAppDB.getDetermValueBean().getIdPcaEligDeterm() != null
				&& pcaAppDB.getDetermValueBean().getIdPcaEligDeterm() != 0
				&& null != pcaAppDB.getDetermValueBean().getIdInitialDetermPerson()
				&& null != pcaAppDB.getDetermValueBean().getIdFinalDetermPerson()) {
			pcaEligDeterminationDao.updateEligibilityDetermination(pcaAppDB.getDetermValueBean());
		}

		if (pcaAppDB.getAppEvent() != null) {
			return pcaAppDB.getAppEvent().getIdEvent();
		}
		return appEvent.getIdEvent();

	}

	/**
	 * Method Name: createNewPcaApplication Method Description:This method
	 * creates new PCA Application(with event) and Background information. New
	 * Pca Application is created by CVS worker in SUB stage. So Event needs be
	 * created for SUB Stage.
	 *
	 * @param pcaAppDB
	 * @return @
	 */
	private Long createNewPcaApplication(PcaApplAndDetermDBDto pcaAppDB) {

		PcaAppAndBackgroundDto appValueBean = pcaAppDB.getAppValueBean();
		Long idperson = appValueBean.getIdPerson();
		// 1. Create new Event for Pca Application in SUB Stage.
		EventValueDto eventValBean = pcaAppDB.getAppEvent();
		eventValBean.setEventDescr(ServiceConstants.PROCESS_EVENT_APP);
		eventValBean.setCdEventStatus(ServiceConstants.PROCESS_EVENT);
		eventValBean.setCdEventType(ServiceConstants.CEVNTTYP_PEA);
		eventValBean.setIdPerson(pcaAppDB.getAppEvent().getIdPerson());

		PostEventReq postEventReq = new PostEventReq();
		// call populate post event req function
		postEventReq = populatePostEventReq(eventValBean, ServiceConstants.REQ_FUNC_CD_ADD);
		postEventReq.setIdUser(eventValBean.getIdPerson());
		
	 
		//postEventReq.setUlIdPerson(pcaAppDB.getAppEvent().getIdUser());
		// Call post event service for Event table and personlinkEvent table
		// update
		PostEventRes postEventRes = eventService.postEvent(postEventReq);

		Long idEvent = (null != postEventRes) ? postEventRes.getUlIdEvent() : 0L;
		// set idperson
		appValueBean.setIdPerson(idperson);

		// 2. Create PCA Application.
		appValueBean.setIdCreatedPerson(appValueBean.getIdLastUpdatePerson());
		Long idPcaEligApplication = pcaAppAndBackgroundDao.insertPcaEligApplication(appValueBean);
		// 3. Create Entry into PCA Application Event Link table.
		pcaAppAndBackgroundDao.insertPcaAppEventLink(idPcaEligApplication, 0L, idEvent, eventValBean.getIdCase(),
				appValueBean.getIdLastUpdatePerson());
		// SIR 1004310 - If withdrawal reason is selected on new Application,
		if (TypeConvUtil.isValid(appValueBean.getCdWithdrawRsn())) {
			EventValueDto appEventValBean = fetchEventInfo(idEvent);
			basePcaSessionService.withdrawPCAApplication(pcaAppDB, idPcaEligApplication, appEventValBean);
		}

		return idEvent;
	}

	/**
	 * Method Name: populatePostEventReq Method Description:
	 *
	 * @param EventValueDto
	 * @param reqFuncCdAdd
	 */

	public PostEventReq populatePostEventReq(EventValueDto eventValueDto, String reqFuncCd) {
		PostEventReq postEventReq = new PostEventReq();
		Long idPerson = 0L;
		postEventReq.setUlIdStage(eventValueDto.getIdStage());
		postEventReq.setUlIdCase(eventValueDto.getIdCase());

		if (null != eventValueDto && (null == eventValueDto.getIdPerson() && 0L == eventValueDto.getIdPerson())) {
			idPerson = stagePersonLinkDao.getPersonIdByRole(eventValueDto.getIdStage(), PRIMARY_CHILD);
			eventValueDto.setIdPerson(idPerson);
		} else {
			// Unless cinv51d was called,Person should be null
			postEventReq.setUlIdPerson(eventValueDto.getIdPerson());
		}
		 List<PostEventPersonDto> postEventPersonList = new ArrayList<>();
		 PostEventPersonDto personLinkList = new PostEventPersonDto();
		 
		 personLinkList.setIdPerson(eventValueDto.getIdPerson());
		 personLinkList.setCdScrDataAction("U");
		 personLinkList.setTsLastUpdate(new Date());
		 postEventPersonList.add(personLinkList);
		
		if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(reqFuncCd)) {
			postEventReq.setDtDtEventOccurred(new Date());
			personLinkList.setCdScrDataAction("A");
		//Fix for defect 12990
		} else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equalsIgnoreCase(reqFuncCd)) {
			postEventReq.setDtDtEventOccurred(eventValueDto.getDtEventOccurred());
			personLinkList.setCdScrDataAction("U");
			// set id event
			postEventReq.setUlIdEvent(eventValueDto.getIdEvent());
		}
		// set event Lastupdate
		postEventReq.setTsLastUpdate(new Date());
		// set taskcode
		postEventReq.setSzCdTask(eventValueDto.getCdEventTask());
		// set EventType
		postEventReq.setSzCdEventType(ServiceConstants.PCA_APPLICATION_EVENT_TYPE);
		// set EventStatus
		if (null != eventValueDto && !ObjectUtils.isEmpty(eventValueDto.getCdEventStatus())) {
			postEventReq.setSzCdEventStatus(eventValueDto.getCdEventStatus());
		}
		// set EventDescr
		if (null != eventValueDto && !ObjectUtils.isEmpty(eventValueDto.getEventDescr())) {
			postEventReq.setSzTxtEventDescr(eventValueDto.getEventDescr());
		}

		// set Function action
		if (!StringUtils.isEmpty(reqFuncCd)) {
			postEventReq.setReqFuncCd(reqFuncCd);
		}
		postEventReq.setPostEventPersonList(postEventPersonList);


		return postEventReq;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.pcaappandbackground.service.
	 * PcaAppAndBackgroundService#createPCACICAStage(us.tx.state.dfps.service.
	 * common.request.CommonHelperReq)
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public StageRes createPCACICAStage(CommonHelperReq commonHelperReq) {
		StageRes resp = new StageRes();
		StageValueBeanDto pcaStageDto = stageDao.findStageInfoForPerson(commonHelperReq);
		resp.setStageValueBeanDto(pcaStageDto);
		if (pcaStageDto == null || pcaStageDto.getIdStage() == ServiceConstants.LongZero) {
			// Create New Case and Stage by calling StageProgression Bean using
			// primary child's details.

			pcaStageDto = new StageValueBeanDto();
			pcaStageDto.setIdStage(commonHelperReq.getIdStage());
			pcaStageDto.setCdStage(ServiceConstants.CSTAGES_PCA);
			pcaStageDto.setIdCreatedPerson(commonHelperReq.getUserID());

			pcaStageDto.setCdStageType(ServiceConstants.CSTGTYPE_CICA);

			Long newStageId = stageProgressionService.createNewStage(pcaStageDto);

			List<Long> stageIdList = new ArrayList();
			stageIdList.add(newStageId);
			commonHelperReq.setStageIDs(stageIdList);
			// Change Primary Child's Role.
			// selectStagePersonLinkUsingIdPerson is merged in
			// updateStagePersonLink
			stageProgDao.updateStagePersonLink(commonHelperReq);

			// Copy Secondary Worker Information to new Stage.
			stageProgDao.linkPersonToNewStage(commonHelperReq);
			resp.setStageValueBeanDto(pcaStageDto);
		}

		return resp;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.pcaappandbackground.service.
	 * PcaAppAndBackgroundService#findPriorPlcmtIds(us.tx.state.dfps.service.
	 * common.request.CommonEventIdReq)
	 */
	@Override
	public CommonIdRes findPriorPlcmtIds(CommonEventIdReq commonEventIdReq) {
		CommonIdRes resp = new CommonIdRes();
		List<Long> idList = pcaAppAndBackgroundDao.fetchPriorPlcmts(commonEventIdReq.getIdEvent());
		resp.setIdList(idList);
		return resp;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.pcaappandbackground.service.
	 * PcaAppAndBackgroundService#fetchPcaAppEvents(us.tx.state.dfps.service.
	 * common.request.PcaAppAndBackgroundReq)
	 */
	@Override
	public List<EventValueDto> fetchPcaAppEvents(PcaAppAndBackgroundReq pcaAppAndBackgroundReq) {
		return pcaAppAndBackgroundDao.fetchPcaAppEvents(pcaAppAndBackgroundReq.getIdPcaEligApplication());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.pcaappandbackground.service.
	 * PcaAppAndBackgroundService#fetchAppAndBackgound(us.tx.state.dfps.service.
	 * common.request.PcaAppAndBackgroundReq)
	 */
	@Override
	public PcaAppAndBackgroundRes fetchAppAndBackgound(PcaAppAndBackgroundReq pcaAppAndBackgroundReq) {
		PcaAppAndBackgroundRes resp = new PcaAppAndBackgroundRes();
		PcaApplAndDetermDBDto pcaApplAndDetermDBDto = new PcaApplAndDetermDBDto();
		PcaAppAndBackgroundDto pcaAppAndBackgroundDto = pcaAppAndBackgroundDao
				.selectPcaEligApplication(pcaAppAndBackgroundReq);
		pcaApplAndDetermDBDto.setAppValueBean(pcaAppAndBackgroundDto);
		PcaEligDeterminationDto determValueBean = pcaAppAndBackgroundDao
				.selectEligFromIdPcaApp(pcaAppAndBackgroundReq.getIdPcaEligApplication());
		pcaApplAndDetermDBDto.setDetermValueBean(determValueBean);
		if (pcaAppAndBackgroundDto != null) {
			PersonDto personDto = personDao.getPersonById(pcaAppAndBackgroundDto.getIdPerson());
			if (personDto != null) {
				pcaApplAndDetermDBDto.setDateOfBirth(personDto.getDtPersonBirth());
			}
		}
		resp.setPcaApplAndDetermDBDto(pcaApplAndDetermDBDto);
		return resp;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.pcaappandbackground.service.
	 * PcaAppAndBackgroundService#selectLatestAppForStage(us.tx.state.dfps.
	 * service.common.request.PcaAppAndBackgroundReq)
	 */
	@Override
	public PcaAppAndBackgroundRes selectLatestAppForStage(PcaAppAndBackgroundReq pcaAppAndBackgroundReq) {
		PcaAppAndBackgroundRes resp = new PcaAppAndBackgroundRes();
		PcaAppAndBackgroundDto pcaAppAndBackgroundDto = pcaAppAndBackgroundDao
				.selectLatestAppForStage(pcaAppAndBackgroundReq.getIdStage());
		resp.setPcaAppAndBackgroundDto(pcaAppAndBackgroundDto);

		return resp;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.pcaappandbackground.service.
	 * PcaAppAndBackgroundService#selectPrevApplication(us.tx.state.dfps.service
	 * .common.request.PcaAppAndBackgroundReq)
	 */
	@Override
	public PcaAppAndBackgroundRes selectPrevApplication(PcaAppAndBackgroundReq pcaAppAndBackgroundReq) {
		PcaAppAndBackgroundRes resp = new PcaAppAndBackgroundRes();
		PcaAppAndBackgroundDto pcaAppAndBackgroundDto = pcaAppAndBackgroundDao
				.selectPrevApplication(pcaAppAndBackgroundReq);
		resp.setPcaAppAndBackgroundDto(pcaAppAndBackgroundDto);
		return resp;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.pcaappandbackground.service.
	 * PcaAppAndBackgroundService#selectLatestValidApplication(us.tx.state.dfps.
	 * service.common.request.PcaAppAndBackgroundReq)
	 */
	@Override
	public PcaAppAndBackgroundRes selectLatestValidApplication(PcaAppAndBackgroundReq pcaAppAndBackgroundReq) {
		PcaAppAndBackgroundRes resp = new PcaAppAndBackgroundRes();
		PcaAppAndBackgroundDto pcaAppAndBackgroundDto = pcaAppAndBackgroundDao
				.selectLatestApplication(pcaAppAndBackgroundReq.getIdPerson(), statusArray);
		resp.setPcaAppAndBackgroundDto(pcaAppAndBackgroundDto);
		return resp;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.pcaappandbackground.service.
	 * PcaAppAndBackgroundService#selectLatestApplication(us.tx.state.dfps.
	 * service.common.request.PcaAppAndBackgroundReq)
	 */
	@Override
	public PcaAppAndBackgroundRes selectLatestApplication(PcaAppAndBackgroundReq pcaAppAndBackgroundReq) {
		return selectLatestValidApplication(pcaAppAndBackgroundReq);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.pcaappandbackground.service.
	 * PcaAppAndBackgroundService#determineQualification(us.tx.state.dfps.
	 * service.common.request.PcaApplAndDetermReq)
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonBooleanRes determineQualification(PcaApplAndDetermReq pcaApplAndDetermReq) {
		CommonBooleanRes resp = new CommonBooleanRes();
		boolean isChildQualified = false;
		PcaApplAndDetermDBDto pcaAppDB = pcaApplAndDetermReq.getPcaApplAndDetermDBDto();
		Long idAppEvent = pcaAppDB.getAppEvent().getIdEvent();
		PcaAppAndBackgroundDto appValueBean = pcaAppAndBackgroundDao.selectPcaEligAppFromEvent(idAppEvent);
		PcaEligDeterminationDto determValueBean = pcaEligDeterminationDao
				.selectPcaEligDeterminationFromEvent(idAppEvent);
		EventValueDto appEvent = fetchEventInfo(idAppEvent);

		// Check if the Child is Qualified for PCA.
		isChildQualified = PcaEligDetermUtils.determineQualification(appValueBean, determValueBean);
		// if the child does not qualify then update the event to COMP.
		if (!isChildQualified) {

			updateAppEvent(pcaAppDB.getAppValueBean(), appEvent, pcaAppDB.getIdLastUpdatePerson(),
					ServiceConstants.CEVTSTAT_COMP, ServiceConstants.APPLICATION_COMP_EVENT_DESC);
			determValueBean.setIndChildQualify(ServiceConstants.N);
		} else {
			if (ServiceConstants.CEVTSTAT_PROC.equals(appEvent.getCdEventStatus())) {
				updateAppEvent(pcaAppDB.getAppValueBean(), appEvent, pcaAppDB.getIdLastUpdatePerson(),
						ServiceConstants.CEVTSTAT_PEND, ServiceConstants.APPLICATION_PEND_EVENT_DESC);
			}
			determValueBean.setIndChildQualify(ServiceConstants.Y);
		}

		// update Pca Application
		pcaAppAndBackgroundDao.updatePcaEligApplication(appValueBean);
		// update the Determination
		determValueBean.setIdLastUpdatePerson(pcaAppDB.getIdLastUpdatePerson());
		pcaEligDeterminationDao.updateEligibilityDetermination(determValueBean);
		resp.setExists(isChildQualified);
		return resp;
	}

	/**
	 * Method Name: updateAppEvent Method Description:This method updates PCA
	 * Application Event Status. There can have two Application events one is
	 * SUB stage and another one in PCA stage for the same PCA Application. It
	 * first Queries PCA_APP_EVENT_LINK table to find all the events associated
	 * with Application and then updates all the events with new status.
	 *
	 * @param idPcaEligApplication
	 * @param appEvent
	 * @param idLastUpdatePerson
	 * @param cevtstatComp
	 * @param applicationCompEventDesc
	 * @
	 */
	@Override
	public Long updateAppEvent(PcaAppAndBackgroundDto pcaAppAndBackgroundDto, EventValueDto appEvent,
			Long idLastUpdatePerson, String evtStatus, String eventDesc) {
		Long idEvent = 0L;
		appEvent.setDtLastUpdate(pcaAppAndBackgroundDto.getDtLastUpdate());
		appEvent.setDtEventOccurred(pcaAppAndBackgroundDto.getDtCreated());
		updateEventStatus(appEvent, idLastUpdatePerson, evtStatus, eventDesc);
		if (null != pcaAppAndBackgroundDto.getIdPcaEligApplication()) {
			List<EventValueDto> eventList = pcaAppAndBackgroundDao
					.fetchPcaAppEvents(pcaAppAndBackgroundDto.getIdPcaEligApplication());
			for (int index = 0; index < eventList.size(); index++) {
				EventValueDto pcaAppEvent = eventList.get(index);
				if (!pcaAppEvent.getIdEvent().equals(appEvent.getIdEvent())) {
					// Retrieve Event Object again.
					pcaAppEvent = eventUtilityService.fetchEventInfo(pcaAppEvent.getIdEvent());
					// Update the Event.
					pcaAppEvent.setCdEventStatus(appEvent.getCdEventStatus());
					pcaAppEvent.setEventDescr(appEvent.getEventDescr());
					pcaAppEvent.setIdPerson(appEvent.getIdPerson());
					idEvent = updateEventStatus(pcaAppEvent, idLastUpdatePerson, evtStatus, eventDesc);
				}
			}
		}
		return idEvent;
	}

	/**
	 * Method Name: updateEventStatus Method Description:This is helper function
	 * to update Event Status by calling PostEventBean.
	 *
	 * @param appEvent
	 * @param idUpdatePerson
	 * @param evtStatus
	 * @param eventDesc
	 * @
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long updateEventStatus(EventValueDto appEvent, Long idUpdatePerson, String evtStatus, String eventDesc) {
		Long idEvent = 0L;
		if (TypeConvUtil.isValid(evtStatus)) {
			appEvent.setCdEventStatus(evtStatus);
		}
		if (TypeConvUtil.isValid(eventDesc)) {
			appEvent.setEventDescr(eventDesc);
		}
		if (null != idUpdatePerson && idUpdatePerson != 0L) {
			appEvent.setUpdatedBy(String.valueOf(idUpdatePerson));
		}

		PostEventReq postEventReq = new PostEventReq();
		// call populate post event req function
		postEventReq = populatePostEventReq(appEvent, ServiceConstants.REQ_FUNC_CD_UPD);

		// Call post event service for Event table and personlinkEvent table
		// update
		PostEventRes postEventRes = eventService.postEvent(postEventReq);
		idEvent = null != postEventRes ? postEventRes.getUlIdEvent() : 0;

		return idEvent;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.pcaappandbackground.service.
	 * PcaAppAndBackgroundService#submitPCAApplication(us.tx.state.dfps.service.
	 * common.request.PcaAppAndBackgroundReq)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public PcaAppAndBackgroundRes submitPCAApplication(PcaAppAndBackgroundReq pcaAppAndBackgroundReq) {
		PcaAppAndBackgroundRes resp = new PcaAppAndBackgroundRes();

		StageValueBeanDto pcaStageValBean = null;
		String cdReqFunc = "";
		// Get PCA Application from the database using Event Id.
		PcaAppAndBackgroundDto appValueBean = pcaAppAndBackgroundDao
				.selectPcaEligAppFromEvent(pcaAppAndBackgroundReq.getIdAppEvent());
		if (appValueBean != null) {
			// Get PCA Stage Id for the Child.
			CommonHelperReq commonHelperReq = new CommonHelperReq();
			commonHelperReq.setIdPerson(appValueBean.getIdPerson());
			commonHelperReq.setCdStage(ServiceConstants.CSTAGES_PCA);
			commonHelperReq.setCdStagePersonRole(PRIMARY_CHILD);
			// cdReqFunc = (null != appValueBean.getIdPcaEligApplication()) ?
			// ServiceConstants.REQ_FUNC_CD_ADD:
			// ServiceConstants.REQ_FUNC_CD_UPD;

			// Get PCA Stage Id for the Child.
			pcaStageValBean = stageDao.findStageInfoForPerson(commonHelperReq);
			// Get Stage Code (SUB or PCA) for the given Stage Id.
			StageValueBeanDto fromStage = stageDao.retrieveStageInfo(pcaAppAndBackgroundReq.getIdStage());

			// If PCA Stage does not Already Exists, Create New PCA Case and
			// Stage.
			if (TypeConvUtil.isNullOrEmpty(pcaStageValBean)) {
				// Get Resource Name using ID_PLCMT_EVENT.
				// Case Name is same as Selected Placement Resource Name.
				ResourceValueBeanDto resValueBean = resourceSearchDao
						.getResourceDetailsUsingIdPlcmtEvent(appValueBean.getIdPlcmtEvent());

				String nmNewCase = null != resValueBean ? resValueBean.getNmResource() : "";

				// Create New Case and Stage by calling StageProgression Bean.
				StageValueBeanDto stageCreationValBean = new StageValueBeanDto();
				stageCreationValBean.setIdStage(pcaAppAndBackgroundReq.getIdStage());
				stageCreationValBean.setCdToStageCode(ServiceConstants.CSTAGES_PCA);
				stageCreationValBean.setIdCreatedPerson(pcaAppAndBackgroundReq.getIdPerson());
				stageCreationValBean.setNmNewCase(nmNewCase);
				stageCreationValBean.setIdFromStage(pcaAppAndBackgroundReq.getIdStage());

				// SIR 1004448 - If a SUB/C-IC is stage progressed to a PCA
				// stage,
				// the Stage Type should be C-ICA.
				if (ServiceConstants.CSTAGES_SUB.equalsIgnoreCase(fromStage.getCdStage())
						&& (ServiceConstants.CSTGTYPE_CICA.equalsIgnoreCase(fromStage.getCdStageType())
								|| ServiceConstants.CSTGTYPE_CIC.equalsIgnoreCase(fromStage.getCdStageType()))) {
					stageCreationValBean.setCdStageType(ServiceConstants.CSTGTYPE_CICA);
				}
				pcaStageValBean = stageProgressionService.createNewCaseAndStage(stageCreationValBean);
				// Add selected Eligibility Specialist as Secondary worker for
				// new PCA Stage.
				stageProgressionService.linkSecondaryWorkerToStage(pcaStageValBean.getIdStage(),
						pcaAppAndBackgroundReq.getIdAssignedEligWorker());
			}

			// If the Application is Coming from "SUB" Stage, Create new PCA
			// Application Event in PCA Stage in order to show PCA
			// Application in PCA Stage.
			// If the Application is submitted from "SUB" Stage.
			if (ServiceConstants.CSTAGES_SUB.equals(fromStage.getCdStage())) {
				// Create New Application Event for PCA Stage.
				EventValueDto pcaAppEvent = new EventValueDto();
				pcaAppEvent.setIdPerson(pcaAppAndBackgroundReq.getIdPerson());
				pcaAppEvent.setIdCase(pcaStageValBean.getIdCase());
				pcaAppEvent.setIdStage(pcaStageValBean.getIdStage());
				pcaAppEvent.setCdEventType(ServiceConstants.CEVNTTYP_PEA);
				pcaAppEvent.setCdEventTask(ServiceConstants.PCA_PCA_APPLICATION_TASK_CODE);
				// Create PCA Stage Application event also in Pending status.
				pcaAppEvent.setCdEventStatus(ServiceConstants.CEVTSTAT_PEND);
				pcaAppEvent.setEventDescr(ServiceConstants.APPLICATION_PEND_EVENT_DESC);

				PostEventReq postEventReq = new PostEventReq();
				// call populate post event req function
				pcaAppEvent.setDtEventCreated(new Date());
				cdReqFunc = ServiceConstants.REQ_FUNC_CD_ADD;
				pcaAppEvent.setIdPerson(appValueBean.getIdPerson());
				pcaAppEvent.setIdEvent(pcaAppAndBackgroundReq.getIdAppEvent());

				postEventReq = populatePostEventReq(pcaAppEvent, cdReqFunc);
				

				// Call post event service for Event table and personlinkEvent
				// table update
				PostEventRes postEventRes = eventService.postEvent(postEventReq);

				Long idPcaAppEvent = (null != postEventRes) ? postEventRes.getUlIdEvent() : 0L;

				pcaStageValBean.setIdAppEvent(idPcaAppEvent);

				// Create New entry into Application Event Link table for PCA
				// Stage.
				pcaAppAndBackgroundDao.insertPcaAppEventLink(appValueBean.getIdPcaEligApplication(), 0L, idPcaAppEvent,
						pcaStageValBean.getIdCase(), pcaAppAndBackgroundReq.getIdUser());
			} else {
				pcaStageValBean.setIdAppEvent(pcaAppAndBackgroundReq.getIdAppEvent());
			}

			// Change Current (SUB or PCA) Application Event Status to PEND.
			EventValueDto currentAppEvent = fetchEventInfo(pcaAppAndBackgroundReq.getIdAppEvent());
			updateEventStatus(currentAppEvent, pcaAppAndBackgroundReq.getIdUser(), ServiceConstants.CEVTSTAT_PEND,
					ServiceConstants.APPLICATION_PEND_EVENT_DESC);

			// Create Empty Eligibility Determination Record.
			createNewDetermination(appValueBean, pcaAppAndBackgroundReq.getIdUser());

			// Determine if Current Child is Child1 or Child2 and update
			// Application.
			String indChildSibling1 = isChildSibling1(appValueBean) ? ServiceConstants.Y : ServiceConstants.N;
			appValueBean.setIndChildSibling1(indChildSibling1);

			pcaAppAndBackgroundDao.updatePcaEligApplication(appValueBean);

			resp.setStageValueBeanDto(pcaStageValBean);
		}
		return resp;
	}

	/**
	 * Method Name: isChildSibling1 Method Description:
	 *
	 * @param appValueBean
	 * @return
	 */
	private boolean isChildSibling1(PcaAppAndBackgroundDto appValueBean) {
		boolean isChild1 = true;

		if (appValueBean.getIdQualSibPerson() != null && appValueBean.getIdQualSibPerson() != 0) {

			PcaAppAndBackgroundDto siblingApp = pcaAppAndBackgroundDao
					.selectLatestApplication(appValueBean.getIdQualSibPerson(), statusArray);
			if (!ObjectUtils.isEmpty(siblingApp.getIdPcaEligApplication()) && siblingApp.getIdPcaEligApplication() != 0
					&& ServiceConstants.Y.equals(siblingApp.getIndChildSibling1())) {
				isChild1 = false;
			}
		}
		return isChild1;
	}

	/**
	 * Method Name: createNewDetermination Method Description:
	 *
	 * @param appValueBean
	 * @param idUser
	 */
	private void createNewDetermination(PcaAppAndBackgroundDto appValueBean, Long idUser) {

		PcaEligDeterminationDto determValueBean = new PcaEligDeterminationDto();
		determValueBean.setIdPcaEligApplication(appValueBean.getIdPcaEligApplication());
		determValueBean.setIdPlcmtEvent(appValueBean.getIdPlcmtEvent());
		determValueBean.setIdCreatedPerson(idUser);
		determValueBean.setIdLastUpdatePerson(idUser);
		// If Placement Requirements are not met, validations would stop the
		// user
		// from submitting the Application. But if both indEligFcmp6mthsRel,
		// indEligFcmp6mthsFicKin are No(Req not Met), it would prompt Yes/No
		// popup
		// using which user can continue. So we need to only check for these
		// indicators here to make sure Placement Requirements are met.
		if (ServiceConstants.N.equals(appValueBean.getIndEligFcmp6mthsRel())
				&& ServiceConstants.N.equals(appValueBean.getIndEligFcmp6mthsFicKin())) {
			determValueBean.setIndPlcmtReqMetOutcome(ServiceConstants.N);
		} else {
			determValueBean.setIndPlcmtReqMetOutcome(ServiceConstants.Y);
		}
		pcaEligDeterminationDao.insertPcaEligDetermination(determValueBean);

	}

}
