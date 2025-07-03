package us.tx.state.dfps.service.dcr.serviceimpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.dto.StaffDto;
import us.tx.state.dfps.service.admin.dto.FetchEventDto;
import us.tx.state.dfps.service.admin.dto.FetchEventRowDto;
import us.tx.state.dfps.service.admin.dto.TodoCreateInDto;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.admin.service.TodoCreateService;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dao.PcspListPlacmtDao;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefDto;
import us.tx.state.dfps.service.casepackage.dto.SelectStageDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.EventFetDao;
import us.tx.state.dfps.service.common.response.DayCareRequestRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.ServiceAuthTWCCommUtils;
import us.tx.state.dfps.service.common.utils.ServiceAuthTWCCommUtils.SvcAuthDtlTWCChange;
import us.tx.state.dfps.service.commontodofunction.service.CommonToDoFunctionService;
import us.tx.state.dfps.service.dcr.dao.DayCareReqPersonDao;
import us.tx.state.dfps.service.dcr.dao.DayCareRequestDao;
import us.tx.state.dfps.service.dcr.dto.DayCareFacilityDto;
import us.tx.state.dfps.service.dcr.dto.DayCarePersonDto;
import us.tx.state.dfps.service.dcr.dto.DayCareRequestDto;
import us.tx.state.dfps.service.dcr.dto.DayCareSearchListDto;
import us.tx.state.dfps.service.dcr.dto.SSCCDayCareRequestDto;
import us.tx.state.dfps.service.dcr.service.DayCareRequestService;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.financial.dao.ServiceAuthExtCommDao;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonListDto;
import us.tx.state.dfps.service.person.dto.PersonValueDto;
import us.tx.state.dfps.service.sscc.service.SSCCRefService;
import us.tx.state.dfps.service.sscc.util.SSCCRefUtil;
import us.tx.state.dfps.service.stage.service.StageService;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.EventIdDto;
import us.tx.state.dfps.xmlstructs.inputstructs.MergeSplitToDoDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ServiceInputDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:this class
 * is for DayCareRequestServiceImpl Sep 25, 2017- 12:12:21 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class DayCareRequestServiceImpl implements DayCareRequestService {

	private static final String PERS_REL_INT_PARENT_BIRTH = "PB";

	@Autowired
	DayCareRequestDao dayCareRequestDao;

	@Autowired
	TodoCreateService todoCreateService;

	@Autowired
	TodoDao todoDao;

	@Autowired
	PostEventService postEventService;

	@Autowired
	SSCCRefUtil ssccRefUtil;

	@Autowired
	DayCareReqPersonDao dayCareReqPersonDao;

	@Autowired
	LookupDao lookupDao;

	@Autowired
	ServiceAuthExtCommDao serviceAuthExtCommDao;

	@Autowired
	EventFetDao eventFetDao;

	@Autowired
	ServiceAuthTWCCommUtils serviceAuthTWCCommUtils;

	@Autowired
	private CommonToDoFunctionService commonToDoFunctionService;

	@Autowired
	EventDao eventDao;

	@Autowired
	private PersonDao personDao;

	@Autowired
	private CaseSummaryDao caseSummaryDao;

	@Autowired
	SSCCRefService ssccRefService;

	@Autowired
	StageService stageService;

	@Autowired
	PcspListPlacmtDao pcspDao;

	private static final Logger LOG = Logger.getLogger(DayCareRequestServiceImpl.class);
	public static final String CSSCCSTA_70 = "70";

	/**
	 * 
	 * Method Name: createTWCTransmissionFailureAlert Method Description:This
	 * function creates Alert to the RDCC and the CaseWorker notifying them of
	 * the failed transmission to TWC.
	 * 
	 * @param idSvcAuthEvent
	 * @param idStage
	 * @param idUser
	 * @return Long @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long createTWCTransmissionFailureAlert(Long idSvcAuthEvent, Long idStage, Long userId) {

		String shortDesc = ServiceConstants.SHORTDESC;
		String longDesc = ServiceConstants.LONGDESC;
		// Create Alert for CaseWorker.
		if (userId != 0) {
			createRdccToDo(shortDesc, longDesc, ServiceConstants.CD_TODO_SA_TWC_TRNS_FAILURE, userId, userId, idStage,
					idSvcAuthEvent);
		}
		// Create Alert for Rdcc.
		PersonValueDto rdcc = dayCareRequestDao.getDaycareCoordinator(idSvcAuthEvent.intValue());
		if (rdcc != null && !rdcc.getPersonId().equals(0L) && !rdcc.getPersonId().equals(userId)) {
			createRdccToDo(shortDesc, longDesc, ServiceConstants.CD_TODO_SA_TWC_TRNS_FAILURE, rdcc.getPersonId(),
					userId, idStage, idSvcAuthEvent);
		}
		Long personId = !ObjectUtils.isEmpty(rdcc) ? rdcc.getPersonId() : null;
		return personId;
	}

	/**
	 * 
	 * Method Name: createRdccToDo Method Description:this method calls the
	 * todoCreateService.
	 * 
	 * @param toDoDesc
	 * @param toDoLongDesc
	 * @param cdTodoInfoType
	 * @param idPrsnAssgn
	 * @param idUser
	 * @param idStage
	 * @param idEvent
	 * @
	 */
	private void createRdccToDo(String toDoDesc, String toDoLongDesc, String cdTodoInfoType, Long idPrsnAssgn,
			Long idUser, Long idStage, Long idEvent) {
		TodoCreateInDto todoCreateInDto = new TodoCreateInDto();
		MergeSplitToDoDto mergeSplitToDoDto = new MergeSplitToDoDto();
		todoCreateInDto.setServiceInputDto(new ServiceInputDto());
		mergeSplitToDoDto.setCdTodoCf(cdTodoInfoType);
		mergeSplitToDoDto.setDtTodoCfDueFrom(null);
		mergeSplitToDoDto.setIdTodoCfStage((long) idStage.intValue());
		mergeSplitToDoDto.setIdTodoCfPersAssgn((long) idPrsnAssgn.intValue());
		mergeSplitToDoDto.setIdTodoCfPersWkr((long) idUser.intValue());

		if (toDoDesc != null)
			mergeSplitToDoDto.setTodoCfDesc(toDoDesc);
		if (toDoLongDesc != null)
			mergeSplitToDoDto.setTodoCfLongDesc(toDoLongDesc);
		if (idEvent > 0) {
			mergeSplitToDoDto.setIdTodoCfEvent(idEvent);
		}

		todoCreateInDto.setMergeSplitToDoDto(mergeSplitToDoDto);
		todoCreateService.TodoCommonFunction(todoCreateInDto);

	}

	/**
	 * 
	 * Method Name: saveDayCareRequestDetail Method Description:This method will
	 * insert/update the DAYCARE_REQUEST table
	 * 
	 * @param dayCareRequestValueDto
	 * @param saveType
	 * @return Long @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long saveDayCareRequestDetail(DayCareRequestDto dayCareRequestValueDto, String saveType) {
		Long idDayCareReqEvent = 0L;

		if (ServiceConstants.A.equals(saveType)) {

			EventValueDto eventValueDto = dayCareRequestValueDto.getEventValueDto();
			idDayCareReqEvent = postEventService.postEvent(eventValueDto, ServiceConstants.REQ_FUNC_CD_ADD,
					dayCareRequestValueDto.getIdCreatedPerson());
			dayCareRequestValueDto.setIdEvent(idDayCareReqEvent);
			Long idDayCareRequest = dayCareRequestDao.insertDayCareRequest(dayCareRequestValueDto);

			if (!ObjectUtils.isEmpty(dayCareRequestValueDto.getIsSSCCDayCareRequest())
					&& dayCareRequestValueDto.getIsSSCCDayCareRequest()) {
				SSCCDayCareRequestDto ssccDayCareReqDto = dayCareRequestValueDto.getSsccDayCareRequestDto();
				ssccDayCareReqDto.setIdDayCareRequest(idDayCareRequest);
				ssccDayCareReqDto.setIdCreatedPerson(dayCareRequestValueDto.getIdCreatedPerson());
				ssccDayCareReqDto.setIdlastUpdatePerson(dayCareRequestValueDto.getIdlastUpdatePerson());

				ssccDayCareReqDto.setCdStatus(ServiceConstants.CSSCCSTA_20);

				List<SSCCRefDto> actRfrlList = ssccRefUtil.fetchActiveSSCCRefForStage(
						dayCareRequestValueDto.getIdStage(), ServiceConstants.PLACEMENT_REFERRAL);
				if (!TypeConvUtil.isNullOrEmpty(actRfrlList) && !actRfrlList.isEmpty()) {
					SSCCRefDto ssccRefDto = (SSCCRefDto) actRfrlList.get(ServiceConstants.Zero);
					ssccDayCareReqDto.setIdSSCCReferral(ssccRefDto.getIdSSCCReferral());
					dayCareRequestDao.insertSSCCDayCareRequest(ssccDayCareReqDto);
				}
			}
		} else {
			dayCareRequestDao.updateDayCareRequestDetail(dayCareRequestValueDto);
			idDayCareReqEvent = dayCareRequestValueDto.getIdEvent();
		}
		return idDayCareReqEvent;

	}

	/**
	 * 
	 * Method Name: saveDayCarePersonInfo Method Description:This method
	 * saves(insert/update) Day Care Request Person Information into the
	 * database.
	 * 
	 * @param dayCareRequestValueDto
	 * @return Long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Long saveDayCarePersonInfo(DayCareRequestDto dayCareRequestValueDto){

		DayCarePersonDto dayCarePersonDto = dayCareRequestValueDto.getDayCarePersonDtoList().get(ServiceConstants.Zero);
		Long idDaycarePersonLink = dayCarePersonDto.getIdDaycarePersonLink();
		Long idEvent = dayCareRequestValueDto.getIdEvent();
		try {

			if (idDaycarePersonLink == ServiceConstants.LongZero) {
				idDaycarePersonLink = dayCareReqPersonDao.insertDayCarePersonLink(dayCarePersonDto);
				dayCarePersonDto.setIdDaycarePersonLink(idDaycarePersonLink);
			} else {

				dayCareReqPersonDao.updateDayCarePersonLink(dayCarePersonDto);

				if (dayCarePersonDto.getDecisionRedetermined()
						|| TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getCdDetermService())
						|| dayCarePersonDto.getCdDetermService().equals(ServiceConstants.NO_DAYCARE)) {
					dayCareReqPersonDao.deleteDayCarePersonFacilLink(idDaycarePersonLink);
					dayCarePersonDto.getDayCareFacilityDtoList().clear();
				}

				if (dayCareRequestValueDto.getInvalidateApproval()) {
					dayCareRequestDao.updateApproversStatus(idEvent);
					dayCareRequestDao.updateAppEventStatus(idEvent);
				}

				createTWCTransmissionUpdateAlert(idEvent, dayCareRequestValueDto.getIdlastUpdatePerson());
			}

			saveDayCareFacilityInfo(dayCarePersonDto);
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}
		return idDaycarePersonLink;

	}

	/**
	 * 
	 * Method Name: saveDayCareFacilityInfo Method Description:This method saves
	 * Day Care Facility information into database.
	 * 
	 * @param dayCarePersonDto
	 */
	private void saveDayCareFacilityInfo(DayCarePersonDto dayCarePersonDto) {
		// Get the Id Day care person Link
		Long idDaycarePersonLink = dayCarePersonDto.getIdDaycarePersonLink();

		// Get the Day care facility list already selected from DB
		List<DayCareFacilityDto> dayCareFacilityListFromDb = dayCareReqPersonDao
				.retrieveDayCarePersonFacilLink(idDaycarePersonLink, ServiceConstants.TRUEVAL);

		// Get the Day care facility list currently selected from Screen
		List<DayCareFacilityDto> dayCareFacilityList = dayCarePersonDto.getDayCareFacilityDtoList();

		// To insert the DTO which is not in DB
		if (!ObjectUtils.isEmpty(dayCareFacilityList)) {
			dayCareFacilityList.forEach(screenDto -> {
				DayCareFacilityDto dcfacilityDto = null;
				if (!ObjectUtils.isEmpty(dayCareFacilityListFromDb)) {
					dcfacilityDto = dayCareFacilityListFromDb.stream()
							.filter(dbDto -> dbDto.getIdFacility().equals(screenDto.getIdFacility())).findAny()
							.orElse(null);
				}
				if (ObjectUtils.isEmpty(dcfacilityDto)) {
					// call to insert entity
					dayCareReqPersonDao.insertDayCarePersonFacilLink(screenDto);
				}
			});
		}

		// To Delete the DTO which is selected from Screen
		if (!ObjectUtils.isEmpty(dayCareFacilityListFromDb)) {
			dayCareFacilityListFromDb.forEach(dbDto -> {
				DayCareFacilityDto dcfacilityDto = null;
				if (!ObjectUtils.isEmpty(dayCareFacilityList)) {
					dcfacilityDto = dayCareFacilityList.stream()
							.filter(screenDto -> screenDto.getIdFacility().equals(dbDto.getIdFacility())).findAny()
							.orElse(null);
				}
				if (ObjectUtils.isEmpty(dcfacilityDto)) {
					// call to delete entity
					dayCareReqPersonDao.deleteDayCarePersonFacilLink(dbDto.getIdDaycarePersonFacilLink());
				}
			});
		}

	}

	/**
	 * 
	 * Method Name: updateSSCCDayCareRequest Method Description:This method
	 * updates SSCC DayCare Request table.
	 * 
	 * @param ssccDayCareRequestDto
	 * @return Long @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long updateSSCCDayCareRequest(SSCCDayCareRequestDto ssccDayCareRequestDto) {
		Long idSsccDaycareRequest = ServiceConstants.LongZero;
		try {

			idSsccDaycareRequest = dayCareRequestDao.updateSSCCDayCareRequest(ssccDayCareRequestDto);
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}
		return idSsccDaycareRequest;

	}

	/**
	 * 
	 * Method Name: updateDayCarePersonLink Method Description:This method is to
	 * update Daycare Person Link table
	 * 
	 * @param dayCareRequestValueDto
	 * @return Long @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long updateDayCarePersonLink(DayCareRequestDto dayCareRequestValueDto) {
		Long totalRowCount = ServiceConstants.LongZero;
		try {
			totalRowCount = dayCareRequestDao.updateDayCarePersonLink(dayCareRequestValueDto);
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}
		return totalRowCount;

	}

	/**
	 * 
	 * Method Name: retrievePersonDayCareDetails Method Description:Retrieve all
	 * valid daycare requests for input person
	 * 
	 * @param dayCareRequestValueDto
	 * @return DayCareRequestValueDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public DayCareRequestDto retrievePersonDayCareDetails(DayCareRequestDto dayCareRequestValueDto) {

		dayCareRequestValueDto = dayCareRequestDao.retrievePersonDayCareDetails(dayCareRequestValueDto);

		LOG.debug("Exiting method retrievePersonDayCareDetails in DayCareRequestService");

		return dayCareRequestValueDto;

	}

	/**
	 * 
	 * Method Name: createDayCareRejectAlert Method Description:this method
	 * returns approvers count
	 * 
	 * @param idEvent
	 * @param szCdTask
	 * @param idStage
	 * @param userId
	 * @param szCdStage
	 * @param szNmStage
	 * @return Long @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long createDayCareRejectAlert(Long idEvent, String szCdTask, Long idStage, Long userId, String szCdStage,
			String szNmStage) {
		LOG.debug("Entering method createDayCareRejectAlert in DayCareRequestService");
		Long[] approvers = new Long[0];
		try {

			approvers = dayCareRequestDao.getDayCareApprovers(idEvent);

			StringBuilder rejectdesc = new StringBuilder();
			rejectdesc.append(ServiceConstants.DAY_CARE_REJ_LOGGED);
			rejectdesc.append(szNmStage);
			rejectdesc.append(ServiceConstants.DAY_CARE_OTHER_APP_REQ);
			String toDoApprvrDesc = (rejectdesc.toString());
			if (!TypeConvUtil.isNullOrEmpty(approvers)) {
				for (Long approver : approvers) {

					if (!approver.equals(userId)) {

						createRdccToDo(toDoApprvrDesc, toDoApprvrDesc, ServiceConstants.CD_TODO_INFO_ALERT, approver,
								userId, idStage, idEvent);
					}
				}
			}

		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}
		LOG.debug("Exiting method createDayCareRejectAlert in DayCareRequestService");
		return (long) approvers.length;

	}

	/**
	 * 
	 * Method Name: updateApproversStatus Method Description:Updates the
	 * Approvers Status to Invalid.
	 * 
	 * @param idEvent
	 * @return Long @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long updateApproversStatus(Long idEvent) {
		LOG.debug("Entering method updateApproversStatus in DayCareRequestService");
		Long approvalId = ServiceConstants.LongZero;
		approvalId = dayCareRequestDao.updateApproversStatus(idEvent);
		LOG.debug("Exiting method updateApproversStatus in DayCareRequestService");
		return approvalId;

	}

	/**
	 * 
	 * Method Name: updateAppEventStatus Method Description:Updates the APP
	 * event to Invalid.
	 * 
	 * @param idEvent
	 * @return Long @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long updateAppEventStatus(Long idEvent) {
		Long eventId = ServiceConstants.LongZero;
		DayCareRequestDao daycareRequestDao = dayCareRequestDao;
		Long idApprovalEvent = daycareRequestDao.getAppEventId(idEvent);
		eventId = daycareRequestDao.updateAppEventStatus(idApprovalEvent);
		return eventId;

	}

	/**
	 * 
	 * Method Name: createDayCareTerminationAlert Method Description:This method
	 * creates Alert for DayCare Coordinator when the Service Authorization is
	 * Terminated
	 * 
	 * @param idEvent
	 * @param idStage
	 * @param userId
	 * @param idChild
	 * @param idSvcAuth
	 * @return Long @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long createDayCareTerminationAlert(Long idEvent, Long idStage, Long userId, Long idChild, Long idSvcAuth) {
		LOG.debug("Entering method createDayCareTerminationAlert in DayCareRequestService");
		PersonValueDto personValueDto = new PersonValueDto();
		try {

			personValueDto = dayCareRequestDao.getDaycareCoordinator(idEvent.intValue());
			String cdAlert = ServiceConstants.DCR005;
			String shortDesc = ServiceConstants.OPEN_DAYCARE;
			StringBuilder longDesc = new StringBuilder();
			longDesc.append(ServiceConstants.PID).append(idChild).append(ServiceConstants.SVC_AUTH_CHANGE);
			if (!TypeConvUtil.isNullOrEmpty(personValueDto)
					&& personValueDto.getPersonId() != ServiceConstants.LongZero) {
				createRdccToDo(shortDesc, longDesc.toString(), cdAlert, personValueDto.getPersonId(), userId, idStage,
						idEvent);
			}
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());

		}
		LOG.debug("Exiting method createDayCareTerminationAlert in DayCareRequestService");
		return personValueDto.getPersonId();
	}

	/**
	 * 
	 * Method Name: retrieveSSCCDayCareRequest Method Description:This method
	 * retrieves SSCC DayCare Request Record using idEvent.
	 * 
	 * @param idEvent
	 * @return SSCCDayCareRequestDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCDayCareRequestDto retrieveSSCCDayCareRequest(Long idEvent) {
		SSCCDayCareRequestDto ssccDayCareRequestDto = new SSCCDayCareRequestDto();
		ssccDayCareRequestDto = retrieveSSCCDayCare(idEvent);
		return ssccDayCareRequestDto;

	}

	/**
	 * 
	 * Method Name: retrieveSSCCDayCareRequest Method Description:This method
	 * retrieves SSCC DayCare Request Record using idEvent.
	 * 
	 * @param idEvent
	 * @return SSCCDayCareRequestDto @
	 */
	private SSCCDayCareRequestDto retrieveSSCCDayCare(Long idEvent) {
		SSCCDayCareRequestDto ssccDayCareRequestDto = new SSCCDayCareRequestDto();
		ssccDayCareRequestDto = dayCareRequestDao.retrieveSSCCDayCareRequest(idEvent);
		return ssccDayCareRequestDto;

	}

	/**
	 * 
	 * Method Name: deleteDayCareRequest Method Description:If DFPS Worker
	 * deletes DFPS DayCare Request Follow the current process of using DayCare
	 * Request Complex delete.
	 * 
	 * If DFPS Worker deletes SSCC DayCare Request, Delete from all DayCare
	 * Request tables except for SSCC_DAYCARE_REQUEST table. set
	 * ID_DAYCARE_REQUEST = 0 in SSCC_DAYCARE_REQUEST table.
	 * 
	 * If SSCC Worker deletes SSCC DayCare Request, Delete from all DayCare
	 * Request tables including SSCC_DAYCARE_REQUEST table.
	 * 
	 * @param idEvent
	 * @param isSSCCWorker
	 * @return SSCCDayCareRequestDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCDayCareRequestDto deleteDayCareRequest(Long idEvent, Boolean isSSCCWorker) {
		SSCCDayCareRequestDto ssccDayCareRequestDto = new SSCCDayCareRequestDto();

		ssccDayCareRequestDto = dayCareRequestDao.retrieveSSCCDayCareRequest(idEvent);

		if (!TypeConvUtil.isNullOrEmpty(ssccDayCareRequestDto)
				&& !ObjectUtils.isEmpty(ssccDayCareRequestDto.getIdSSCCDayCareRequest())
				&& ssccDayCareRequestDto.getIdSSCCDayCareRequest() != ServiceConstants.LongZero) {

			if (isSSCCWorker) {
				dayCareRequestDao.deleteSSCCDayCareRequest(ssccDayCareRequestDto.getIdSSCCDayCareRequest());
			} else {
				ssccDayCareRequestDto.setIdDayCareRequest(ServiceConstants.LongZero);
				dayCareRequestDao.updateSSCCDayCareRequest(ssccDayCareRequestDto);
			}
		}
		dayCareRequestDao.deleteDayCareRequest(idEvent);
		return ssccDayCareRequestDto;

	}

	/**
	 * 
	 * Method Name: fetchActiveReferralForChild Method Description:This function
	 * returns Active SSCC Referral for the Child.
	 * 
	 * @param idPerson
	 * @return Long @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long fetchActiveReferralForChild(Long idPerson) {
		Long idActiveReferral = ServiceConstants.LongZero;
		idActiveReferral = dayCareRequestDao.fetchActiveReferralForChild(idPerson);
		return idActiveReferral;

	}

	/**
	 * 
	 * Method Name: fetchReferralsForAllPersonsInDaycareRequest Method
	 * Description:This function returns All the Referrals(Active and Inactive)
	 * for the DayCare Request.
	 * 
	 * @param idDayCareRequest
	 * @return List<SSCCRefDto> @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<SSCCRefDto> fetchReferralsForAllPersonsInDaycareRequest(Long idDayCareRequest) {
		List<SSCCRefDto> ssccRefDtoList = new ArrayList<>();
		ssccRefDtoList = dayCareRequestDao.fetchReferralsForAllPersonsInDaycareRequest(idDayCareRequest);
		return ssccRefDtoList;

	}

	/**
	 * 
	 * Method Name: isDayCareReqAfterNov2013Rel Method Description:This function
	 * checks the following Conditions for generating Service Authorization
	 * Transmission Alerts to TWC.
	 * 
	 * 1. Is this Day Care Service Authorization Request? 2. Day Care Request
	 * has been created After Nov 2013 Release Date. 3. Online Parameter value
	 * is set to Y.
	 * 
	 * @param idSvcAuthEvent
	 * @return Boolean
	 */
	private Boolean isDayCareReqAfterNov2013Rel(Long idSvcAuthEvent){
		Boolean dcReqPostNov13 = ServiceConstants.FALSEVAL;

		Long idDayCareReqEvent = dayCareRequestDao.retrieveDCReqEventForSvcAuthEvent(idSvcAuthEvent);
		if (idDayCareReqEvent != ServiceConstants.LongZero) {

			FetchEventDto fetchEventDto = new FetchEventDto();
			fetchEventDto.setIdEvent(idDayCareReqEvent);
			FetchEventRowDto fetchEventRowDto = eventFetDao.fetchEventDetails(fetchEventDto).getFetchEventRowDto();

			String nov13RelDt = lookupDao.simpleDecode(ServiceConstants.CRELDATE,
					ServiceConstants.CRELDATE_NOV_2013_IMPACT);
			
			Date date1 = null;
			try {
				date1 = new SimpleDateFormat("MM/dd/yyyy").parse(nov13RelDt);
			} catch (ParseException e) {
				new ServiceLayerException(e.getMessage());
			}
			if (!TypeConvUtil.isNullOrEmpty(date1) && fetchEventRowDto.getDtDtEventCreated().after(date1)) {

				String onlineParamTwcEnabled = serviceAuthExtCommDao
						.selectOnlineParameterValue(ServiceConstants.ONLINE_PARAM_TWC_AUT_TRANS);
				if (ServiceConstants.Y.equals(onlineParamTwcEnabled)) {
					dcReqPostNov13 = ServiceConstants.TRUEVAL;
				}

			}
		}

		return dcReqPostNov13;
	}

	/**
	 * 
	 * Method Name: createRdccToDo Method Description:This method retrieves data
	 * from todocommunication method
	 * 
	 * @param toDoDesc
	 * @param toDoLongDesc
	 * @param cdTodoInfoType
	 * @param idPrsnAssgn
	 * @param idUser
	 * @param idStage
	 * @param idEvent
	 * @
	 */
	private void createRdccToDoFunc(String toDoDesc, String toDoLongDesc, String cdTodoInfoType, Long idPrsnAssgn,
			Long idUser, Long idStage, Long idEvent) {

		try {
			TodoCreateInDto todoCreateInDto = new TodoCreateInDto();
			MergeSplitToDoDto mergeSplitToDoDto = new MergeSplitToDoDto();
			todoCreateInDto.setServiceInputDto(new ServiceInputDto());
			mergeSplitToDoDto.setCdTodoCf(cdTodoInfoType);
			mergeSplitToDoDto.setDtTodoCfDueFrom(null);
			mergeSplitToDoDto.setIdTodoCfPersCrea(idUser);
			mergeSplitToDoDto.setIdTodoCfStage(idStage);
			mergeSplitToDoDto.setIdTodoCfPersAssgn(idPrsnAssgn);
			mergeSplitToDoDto.setIdTodoCfPersWkr(idUser);
			if (toDoDesc != null)
				mergeSplitToDoDto.setTodoCfDesc(toDoDesc);
			if (toDoLongDesc != null)
				mergeSplitToDoDto.setTodoCfLongDesc(toDoLongDesc);
			if (idEvent > 0) {
				mergeSplitToDoDto.setIdTodoCfEvent(idEvent);
			}
			todoCreateInDto.setMergeSplitToDoDto(mergeSplitToDoDto);
			commonToDoFunctionService.TodoCommonFunction(todoCreateInDto);
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}

	}

	/**
	 * Method Name: createTWCTransmissionAlert Method Description:This function
	 * creates Alert to RDCC notifying them that the Day Care Service
	 * Authorization has been approved but not transmitted to TWC.
	 * 
	 * @param idSvcAuthEvent
	 * @param idStage
	 * @param userId
	 * @return Long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Long createTWCTransmissionAlert(Long idSvcAuthEvent, Long idStage, Long userId){

		LOG.debug("Entering method createTWCTransmissionAlert in DayCareRequestService");
		PersonValueDto personValueDto = new PersonValueDto();
		try {

			if (isDayCareReqAfterNov2013Rel(idSvcAuthEvent)) {
				personValueDto = dayCareRequestDao.getDaycareCoordinator(idSvcAuthEvent.intValue());
				String shortDesc = ServiceConstants.DAY_CARE_SERVICE_AUTH_TWC_APPROVED;
				String longDesc = ServiceConstants.DAY_CARE_SERVICE_AUTH_TWC_APPROVED;
				if (!TypeConvUtil.isNullOrEmpty(personValueDto)
						&& personValueDto.getPersonId() != ServiceConstants.LongZero) {
					createRdccToDoFunc(shortDesc, longDesc, ServiceConstants.CD_TODO_SA_TWC_TRNS_INITIAL,
							personValueDto.getPersonId(), userId, idStage, idSvcAuthEvent);
				}
			}
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}
		LOG.debug("Exiting method createTWCTransmissionAlert in DayCareRequestService");
		return personValueDto.getPersonId();
	}

	/**
	 * 
	 * Method Name: createTWCTransmissionUpdateAlert Method Description:This
	 * function creates Alert to the RDCC notifying them that the Approved Day
	 * Care Request has been modified and the Update needs to be Transmitted to
	 * TWC.
	 * 
	 * Approved Day Care Request can only be modified by RDCC. So the Alert
	 * needs to be displayed for the logged in user.
	 * 
	 * Since RDCC can modify Day Care Request multiple times before sending the
	 * Servive Authorization Xml to TWC, we need to check if there is an
	 * existing Alert before creating new Alert to avoid creating a new Alert
	 * every time the page has been saved.
	 * 
	 * @param idDayCareEvent
	 * @param userId
	 */
	private void createTWCTransmissionUpdateAlert(Long idDayCareEvent, Long userId){

		Long idSvcAuth = serviceAuthExtCommDao.retrieveDayCareSvcAuthId(idDayCareEvent);
		if (idSvcAuth != ServiceConstants.LongZero) {
			Long idSvcAuthEvent = serviceAuthExtCommDao.retrieveDayCareSvcAuthEventId(idDayCareEvent);

			FetchEventDto fetchEventDto = new FetchEventDto();
			fetchEventDto.setIdEvent(idDayCareEvent);
			FetchEventRowDto fetchEventRowDto = eventFetDao.fetchEventDetails(fetchEventDto).getFetchEventRowDto();

			if (isDayCareReqAfterNov2013Rel(idSvcAuthEvent)
					&& ServiceConstants.CEVTSTAT_APRV.equals(fetchEventRowDto.getCdEventStatus())) {
				SvcAuthDtlTWCChange baselineChanged = serviceAuthTWCCommUtils.isTWCBaselineModified(idSvcAuthEvent,
						idSvcAuth);
				if (baselineChanged == SvcAuthDtlTWCChange.UPDATE) {

					Boolean todoExists = todoDao.isTodoExists(idSvcAuthEvent,
							ServiceConstants.CD_TODO_SA_TWC_TRNS_UPDATE, userId);
					if (todoExists == ServiceConstants.FALSEVAL) {
						String shortDesc = ServiceConstants.DAY_CARE_SERVICE_AUTH_RDCC_TWC;
						String longDesc = ServiceConstants.DAY_CARE_SERVICE_AUTH_RDCC_TWC;
						createRdccToDoFunc(shortDesc, longDesc, ServiceConstants.CD_TODO_SA_TWC_TRNS_UPDATE, userId,
								userId, fetchEventRowDto.getIdStage(), idSvcAuthEvent);
					}
				}
			}
		}

	}

	/**
	 * 
	 * Method Name: createTWCTransmissionTerminateAlert Method Description: This
	 * function creates Alert to the User notifying them that the Approved Day
	 * Care Service Authorization has been terminated and Alert should be sent
	 * to the user.
	 * 
	 * @param idSvcAuthEvent
	 * @param idStage
	 * @param userId
	 * @return Boolean
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Boolean createTWCTransmissionTerminateAlert(Long idSvcAuthEvent, Long idStage, Long userId){
		LOG.debug("Entering method createTWCTransmissionTerminateAlert in DayCareRequestService");
		Boolean todoExists = ServiceConstants.FALSEVAL;
		try {

			todoExists = todoDao.isTodoExists(idSvcAuthEvent, ServiceConstants.CD_TODO_SA_TWC_TRNS_TERMINATE, userId);
			if (isDayCareReqAfterNov2013Rel(idSvcAuthEvent) && todoExists == ServiceConstants.FALSEVAL) {
				String shortDesc = ServiceConstants.DAY_CARE_SERVICE_AUTH_TERMINATED;
				String longDesc = ServiceConstants.DAY_CARE_SERVICE_AUTH_TERMINATED;
				createRdccToDoFunc(shortDesc, longDesc, ServiceConstants.CD_TODO_SA_TWC_TRNS_TERMINATE, userId, userId,
						idStage, idSvcAuthEvent);
			}
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
			throw new ServiceLayerException(e.getMessage());
		}
		LOG.debug("Exiting method createTWCTransmissionTerminateAlert in DayCareRequestService");
		return todoExists;
	}

	/**
	 * 
	 * Method Name: retrieveDayCareRequestSvcAuthPersonDtl Method Description :
	 * Retrive Day Care Person Svc Auth Person Details List
	 * 
	 * @param idSvcAuthEvent
	 * @param idStage
	 * @param userId
	 * @return Boolean
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<DayCarePersonDto> retrieveDayCareRequestSvcAuthPersonDtl(Long idEvent, Long idStage) {
		List<DayCarePersonDto> dayCarePersonDtoList = dayCareRequestDao.retrieveDayCareRequestSvcAuthPersonDtl(idEvent,
				idStage);
		dayCarePersonDtoList.forEach(dayCarePersonDto -> {
			PersonValueDto personValueDto = personDao.fetchStagePersonLinkInfo(idStage, dayCarePersonDto.getIdPerson());
			if (!ObjectUtils.isEmpty(personValueDto)) {
				dayCarePersonDto.setCdPersonRelInt(personValueDto.getRoleInStageCode());
			}
		});
		return dayCarePersonDtoList;
	}

	/**
	 * 
	 * Method Name: retrieveDayCareRequestSvcAuth Method Description: Retrieve
	 * the daycarerequest event id from dayCare_svc_auth_link table
	 * 
	 * @param svcAuthEvent
	 *            - Service Authorization event id
	 * @return dayCareEventDto - Day care event Id
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public EventIdDto retrieveDayCareRequestSvcAuth(Long idSvcAuthEvent) {
		return dayCareRequestDao.retrieveDayCareRequestSvcAuth(idSvcAuthEvent);
	}

	/**
	 * 
	 * Method Name: daycareSearch Method Description:This is service
	 * implementation layer for Day Care Search(CLASS)
	 * 
	 * @param searchDto
	 * @return
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<DayCareSearchListDto> daycareSearch(DayCareSearchListDto searchDto) {
		return dayCareRequestDao.daycareSearch(searchDto);
	}

	/**
	 * 
	 * Method Name: getFacilityById Method Description: This is Service
	 * implementation layer for retrieving facility by facility id
	 * 
	 * @param idFacility
	 * @return DayCareSearchListDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public DayCareSearchListDto getFacilityById(Long idFacility) {
		return dayCareRequestDao.getFacilityById(idFacility);
	}

	/**
	 * 
	 * Method Name: retrieveDayCarePersonLink Method Description: This is
	 * Service implementation layer for retrieving Day care Person by day care
	 * request id
	 * 
	 * @param idDayCareRequest
	 * @return List<DayCarePersonDto>
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<DayCarePersonDto> retrieveDayCarePersonLink(Long idDayCareRequest) {

		return retrieveDayCarePersonLinks(idDayCareRequest);

	}

	/**
	 * 
	 * Method Name: retrieveDayCarePersonLinks Method Description: This is
	 * Service implementation layer for retrieving Day care Person by day care
	 * request id
	 * 
	 * @param idDayCareRequest
	 * @return List<DayCarePersonDto>
	 */
	private List<DayCarePersonDto> retrieveDayCarePersonLinks(Long idDayCareRequest) {

		List<DayCarePersonDto> dayCarePersonDtoList = dayCareReqPersonDao.retrieveDayCarePersonLink(idDayCareRequest);
		dayCarePersonDtoList.stream().forEach(dayCarePersonDto -> {
			dayCarePersonDto.setPersonAge(!StringUtils.isEmpty(dayCarePersonDto.getDtBirth())
					? calculateAge(dayCarePersonDto.getDtBirth()) : ServiceConstants.Zero_INT);
		});

		return dayCarePersonDtoList;

	}

	/**
	 * artf263275 : based on the defect it was decided to change the query to match legacy impact
	 * Method Name: getDayCarePersonsInfo Method Description: This is
	 * Service implementation layer for retrieving Day care Person by day care
	 * request id
	 *
	 * @param idDayCareRequest
	 * @return List<DayCarePersonDto>
	 */
	private List<DayCarePersonDto> getDayCarePersonsInfo(Long idDayCareRequest) {

		List<DayCarePersonDto> dayCarePersonDtoList = dayCareReqPersonDao.getDayCarePersonsInfo(idDayCareRequest);
		dayCarePersonDtoList.stream().forEach(dayCarePersonDto -> {
			dayCarePersonDto.setPersonAge(!StringUtils.isEmpty(dayCarePersonDto.getDtBirth())
					? calculateAge(dayCarePersonDto.getDtBirth()) : ServiceConstants.Zero_INT);
		});

		return dayCarePersonDtoList;

	}

	/**
	 * 
	 * Method Name: hasChangedSystemResponses Method Description:
	 * 
	 * @param idPerson
	 * @param idDayCareRequest
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean hasChangedSystemResponses(Long idPerson, Long idDayCareRequest) {

		return dayCareReqPersonDao.hasChangedSystemResponses(idPerson.intValue(), idDayCareRequest.intValue());

	}

	/**
	 * 
	 * Method Name: retrieveDayCareRequestDetail Method Description: This is
	 * Service implementation layer for retrieving DayCareRequest by event id
	 * 
	 * @param idEvent
	 * @return DayCareRequestDto
	 */
	@Override
	public DayCareRequestDto retrieveDayCareRequestDetail(Long idEvent) {

		DayCareRequestDto dayCareRequestDto = new DayCareRequestDto();

		dayCareRequestDto = dayCareRequestDao.retrieveDayCareRequestDetail(idEvent);
		SSCCDayCareRequestDto ssCCDayCareRequestDto = retrieveSSCCDayCare(idEvent);
		if (!TypeConvUtil.isNullOrEmpty(ssCCDayCareRequestDto)
				&& !TypeConvUtil.isNullOrEmpty(ssCCDayCareRequestDto.getIdSSCCDayCareRequest())) {
			dayCareRequestDto.setSsccDayCareRequestDto(ssCCDayCareRequestDto);
		}
		return dayCareRequestDto;
	}

	/**
	 * 
	 * Method Name: isDayCareRequestLinkedToServiceAuth Method Description:
	 * 
	 * @param idEvent
	 * @return
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isDayCareRequestLinkedToServiceAuth(Long idEvent) {
		return dayCareRequestDao.isDayCareRequestLinkedToServiceAuth(idEvent);
	}

	/**
	 * 
	 * Method Name: generateServiceAuth Method Description:
	 * 
	 * @param idDayCareEvent
	 * @param idUser
	 * @param idSvcAuthEvent
	 * @return idDaycareSvcAuthLink
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long generateServiceAuth(Long idDayCareEvent, Long idUser, EventValueDto eventValueDto) {

		Long idSvcAuthEvent = postEventService.postEvent(eventValueDto, ServiceConstants.REQ_FUNC_CD_ADD, 0L);
		return dayCareRequestDao.generateServiceAuth(idDayCareEvent, idUser, idSvcAuthEvent);
	}

	/**
	 * 
	 * Method Name: getStaffInformation Method Description: This is Service
	 * implementation layer for retrieving StaffInformation by stage id
	 * 
	 * @param idStage
	 * @return StaffDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public StaffDto getStaffInformation(Long idStage) {
		return dayCareRequestDao.getStaffInformation(idStage);
	}

	/**
	 * Method Name: dayCareService Method Description:
	 * 
	 * @param idDayCareRequest
	 * @return Boolean
	 */
	@Override
	public Boolean dayCareService(Long idDayCareRequest) {
		return dayCareRequestDao.dayCareService(idDayCareRequest);
	}

	/**
	 * Method Name: getDaycareCodes Method Description: GET DAY CARE CODE
	 * 
	 * @param
	 * @return Map<String, String>
	 */
	public Map<String, String> getDaycareCodes() {
		return dayCareRequestDao.getDaycareCodes();
	}

	/**
	 * Method Name: populateAddress Method Description:
	 * 
	 * @param idStage
	 * @return
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<DayCarePersonDto> populateAddress(Long idStage) {
		return dayCareReqPersonDao.populateAddress(idStage);

	}

	/**
	 * 
	 * Method Name: calculateAge Method Description: This method is to calculate
	 * age
	 * 
	 * @param date
	 * @return age
	 */
	private int calculateAge(Date date) {

		int age = 0;
		Calendar now = Calendar.getInstance();
		Calendar dob = Calendar.getInstance();
		dob.setTime(date);
		int year1 = now.get(Calendar.YEAR);
		int year2 = dob.get(Calendar.YEAR);
		age = year1 - year2;
		int month1 = now.get(Calendar.MONTH);
		int month2 = dob.get(Calendar.MONTH);
		if (month2 > month1) {
			age--;
		} else if (month1 == month2) {
			int day1 = now.get(Calendar.DAY_OF_MONTH);
			int day2 = dob.get(Calendar.DAY_OF_MONTH);
			if (day2 > day1) {
				age--;
			}
		}
		return age;
	}

	/**
	 * Method Name: savePersonList Method Description: Service Impl to Save
	 * Person List
	 * 
	 * @param idStage
	 * @param idEvent
	 * @param personListDto
	 * @param isApprovalMode
	 * @return DayCareRequestDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public DayCareRequestDto savePersonList(long idStage, long idEvent, List<PersonListDto> personListDto,
			boolean isApprovalMode) {

		List<DayCarePersonDto> dayCarePersonListDto = populateAddress(idStage);
		DayCareRequestDto dayCareRequestDto = retrieveDayCareRequestDetail(idEvent);
		SelectStageDto selectStageDto = caseSummaryDao.getStage(idStage, ServiceConstants.STAGE_CURRENT);
		List<DayCarePersonDto> dayCarePersonDtoList = new ArrayList<>();

		if (ObjectUtils.isEmpty(dayCarePersonListDto)) {
			dayCarePersonListDto = new ArrayList<>();
		}
		for (PersonListDto personDto : personListDto) {
			DayCarePersonDto dayCarePersonDto = new DayCarePersonDto();
			dayCarePersonDto.setIdPerson(personDto.getIdPerson());
			dayCarePersonDto.setDtBirth(personDto.getDtPersonBirth());
			dayCarePersonDto.setNmPersonFullName(personDto.getPersonFull());
			dayCarePersonDto.setIdDayCareRequest(dayCareRequestDto.getIdDayCareRequest());

			if (!ObjectUtils.isEmpty(dayCarePersonDto.getDtBirth())) {
				// Day care request ADS changes if person is lesser than 18
				// years and relation
				// is parent birth
				// then person will be a care giver
				// ADS change - Additional condition to apply the logic for SUB,
				// FSU and FRE
				if (calculateAge(dayCarePersonDto.getDtBirth()) < 18) {
					dayCarePersonDto.setCdPersonType(CodesConstant.CGPROLE_010);
					if (PERS_REL_INT_PARENT_BIRTH.equals(personDto.getStagePersRelInt())
							&& (CodesConstant.CSTAGES_FRE.equalsIgnoreCase(selectStageDto.getCdStage())
									|| CodesConstant.CSTAGES_FSU.equalsIgnoreCase(selectStageDto.getCdStage())
									|| CodesConstant.CSTAGES_SUB.equalsIgnoreCase(selectStageDto.getCdStage()))) {
						dayCarePersonDto.setCdPersonType(CodesConstant.CGPROLE_030);
					}
				} else {
					dayCarePersonDto.setCdPersonType(CodesConstant.CGPROLE_030);
				}
			}

			dayCarePersonListDto.stream().filter(x -> x.getIdPerson().equals(dayCarePersonDto.getIdPerson()))
					.forEach(y -> {
						dayCarePersonDto.setStreetLine1(y.getStreetLine1());
						dayCarePersonDto.setStreetLine2(y.getStreetLine2());
						dayCarePersonDto.setCity(y.getCity());
						dayCarePersonDto.setCounty(y.getCounty());
					});

			Long idActiveReferral = fetchActiveReferralForChild(dayCarePersonDto.getIdPerson());
			dayCarePersonDto.setIdSSCCReferral(idActiveReferral);
			dayCarePersonDtoList.add(dayCarePersonDto);
			dayCareReqPersonDao.insertDayCarePersonLink(dayCarePersonDto);
		}
		String eventStatus = "";
		EventDto eventDto = eventDao.getEventByid(idEvent);
		if (!ObjectUtils.isEmpty(eventDto)) {
			eventStatus = eventDto.getCdEventStatus();
		}
		dayCareRequestDto.setDayCarePersonDtoList(dayCarePersonDtoList);

		if (!isApprovalMode && CodesConstant.CEVTSTAT_PEND.equals(eventStatus)) {
			updateApproversStatus(idEvent);
			updateAppEventStatus(idEvent);
		}

		return dayCareRequestDto;
	}

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public boolean deletePerson(long idDayCareRequest, long idPerson, long idEvent, boolean approvalMode) {
		boolean deleteSucess = false;
		int deleteRecordsCount = dayCareReqPersonDao.deleteDayCarePersonLink(idDayCareRequest, idPerson);
		if (deleteRecordsCount > 0) {
			deleteSucess = true;
		}
		String eventStatus = "";
		EventDto eventDto = eventDao.getEventByid(idEvent);
		if (!ObjectUtils.isEmpty(eventDto)) {
			eventStatus = eventDto.getCdEventStatus();
		}
		if (!approvalMode && CodesConstant.CEVTSTAT_PEND.equals(eventStatus)) {
			updateApproversStatus(idEvent);
			updateAppEventStatus(idEvent);
		}
		return deleteSucess;

	}

	/**
	 * Method Name: createDayCareRegionalAlert Method Description: Create Alert
	 * for Regional Daycare Coordinator when the Daycare Service Authorization
	 * is Terminated.
	 * 
	 * @param dayCareRequestDto
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public void createDayCareRegionalAlert(DayCareRequestDto dayCareRequestDto){

		EventIdDto eventIdDto = retrieveDayCareRequestSvcAuth(dayCareRequestDto.getIdSvcAuthEvent());
		if (dayCareRequestDto.getSvcAuthTerminated() && !ObjectUtils.isEmpty(eventIdDto)
				&& !ObjectUtils.isEmpty(eventIdDto.getIdEvent())) {
			createDayCareTerminationAlert(dayCareRequestDto.getIdEvent(), dayCareRequestDto.getIdStage(),
					dayCareRequestDto.getUserId(), dayCareRequestDto.getIdChild(), dayCareRequestDto.getIdSvcAuth());
		}

		// Create Alert for the User notifying them that Service
		// Authorization terminate Date has been changed but not been
		// transmitted to TWC.
		if (dayCareRequestDto.getSvcAuthTerminated() && !ObjectUtils.isEmpty(eventIdDto)
				&& !ObjectUtils.isEmpty(eventIdDto.getIdEvent()) && "APRV".equals(dayCareRequestDto.getCdEventStatus())
				&& "N".equals(dayCareRequestDto.getIndStageClose())
				&& "CPS".equals(dayCareRequestDto.getCdStageProgram())) {
			SvcAuthDtlTWCChange baselineChanged = serviceAuthTWCCommUtils
					.isTWCBaselineModified(dayCareRequestDto.getIdSvcAuthEvent(), dayCareRequestDto.getIdSvcAuth());
			if (baselineChanged == SvcAuthDtlTWCChange.TERMINATE) {
				createTWCTransmissionTerminateAlert(dayCareRequestDto.getIdSvcAuthEvent(),
						dayCareRequestDto.getIdStage(), dayCareRequestDto.getUserId());
			}
		}
	}

	/**
	 * Method Name: validateAndNotify Method Description:Services method for
	 * validate and notify
	 * 
	 * @param idPerson
	 * @param idEvent
	 * @return long
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long validateAndNotify(long idPerson, long idEvent) {

		SSCCDayCareRequestDto ssccDayCareRequestDto = dayCareRequestDao.retrieveSSCCDayCareRequest(idEvent);
		if (!TypeConvUtil.isNullOrEmpty(ssccDayCareRequestDto)
				&& !ObjectUtils.isEmpty(ssccDayCareRequestDto.getIdSSCCDayCareRequest())
				&& ssccDayCareRequestDto.getIdSSCCDayCareRequest() != ServiceConstants.LongZero) {
			ssccDayCareRequestDto.setIdlastUpdatePerson(idPerson);
			ssccDayCareRequestDto.setCdStatus(CodesConstant.CSSCCSTA_60);

		}
		Long idSsccDaycareRequest = updateSSCCDayCareRequest(ssccDayCareRequestDto);
		return idSsccDaycareRequest;

	}

	/**
	 * Method Name: retrieveSvcAuthPersonCount Method Description: Retrieve the
	 * list of persons from dayCare related Service Auth
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long retrieveSvcAuthPersonCount(Long idEvent) {
		return dayCareReqPersonDao.retrieveSvcAuthPersonCount(idEvent);
	}

	/**
	 * Method Name: retrieveDayCareRequestDetailsForDisplay Method
	 * Description:Services method for retrieveDayCareRequestDetailsForDisplay
	 * 
	 * @param idStage
	 * @param idEvent
	 * @param idUser
	 * @return DayCareRequestRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public DayCareRequestRes retrieveDayCareRequestDetailsForDisplay(Long idStage, Long idEvent, Long idUser) {

		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();

		SelectStageDto stageDto = caseSummaryDao.getStage(idStage, ServiceConstants.STAGE_CURRENT);

		StaffDto staffDto = dayCareRequestDao.getStaffInformation(idStage);

		boolean caseCheckOutPersonStatus = false;
		if (caseSummaryDao.getCaseCheckoutPerson(idStage) > 0) {
			caseCheckOutPersonStatus = true;
		}

		EventDto eventDto = null;

		String eventStatus = null;

		boolean dayCareLinked = false;
		boolean withChildHasDiffSvc = false;

		boolean userSSCC = false;
		boolean hasBeenSubmitted = false;
		DayCareRequestDto dayCareRequestDto = new DayCareRequestDto();

		boolean rescindedRfrls = false;
		if (!TypeConvUtil.isNullOrEmpty(idEvent)) {
			eventDto = eventDao.getEventByid(idEvent);
			eventStatus = eventDto.getCdEventStatus();
			dayCareLinked = dayCareRequestDao.isDayCareRequestLinkedToServiceAuth(idEvent);
			dayCareRequestDto = retrieveDayCareRequestDetail(idEvent);
			//artf263275 : based on the defect it was decided to change the query to match legacy impact
			List<DayCarePersonDto> dayCarePersonDtos = getDayCarePersonsInfo(
					dayCareRequestDto.getIdDayCareRequest());
			dayCareRequestRes.setDayCarePersonDtos(dayCarePersonDtos);
			withChildHasDiffSvc = dayCareService(dayCareRequestDto.getIdDayCareRequest());
			hasBeenSubmitted = pcspDao.setHasBeenSubmittedForApprovalCps(idEvent);
			List<SSCCRefDto> ssccRefDtoList = new ArrayList<>();
			ssccRefDtoList = dayCareRequestDao
					.fetchReferralsForAllPersonsInDaycareRequest(dayCareRequestDto.getIdDayCareRequest());
			Long countSSCCDPR = ssccRefDtoList.stream()
					.filter(ssccRefDto -> CSSCCSTA_70.equals(ssccRefDto.getCdRefStatus())).count();
			if (countSSCCDPR > 0) {
				rescindedRfrls = true;
			}
		}

		Long pendingStageClosureEventId = eventDao.getPendingStageClosureEvent(idStage);
		if (ObjectUtils.isEmpty(pendingStageClosureEventId)) {
			pendingStageClosureEventId = 0L;
		}
		userSSCC = ssccRefService.isUserSSCC(idUser);
		dayCareRequestRes.setStageDto(stageDto);
		dayCareRequestRes.setCaseCheckOutPersonStatus(caseCheckOutPersonStatus);
		dayCareRequestRes.setEventDto(eventDto);
		dayCareRequestRes.setEventStatus(eventStatus);
		dayCareRequestRes.setDayCareLinked(dayCareLinked);
		dayCareRequestRes.setWithChildHasDiffSvc(withChildHasDiffSvc);
		dayCareRequestRes.setUserSSCC(userSSCC);
		dayCareRequestRes.setStaffDto(staffDto);
		dayCareRequestRes.setHasSubmittedForApproval(hasBeenSubmitted);
		dayCareRequestRes.setPendingStageClosureEvent(pendingStageClosureEventId);
		dayCareRequestRes.setRescindedRfrls(rescindedRfrls);
		dayCareRequestRes.setDayCareRequestValueDto(dayCareRequestDto);

		return dayCareRequestRes;
	}

}
