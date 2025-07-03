/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Feb 15, 2018- 2:58:31 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.placement.serviceimpl;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Placement;
import us.tx.state.dfps.common.domain.PlacementTa;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.PostEventPersonDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.EventPersonLinkDao;
import us.tx.state.dfps.service.common.request.ChildRecoveryRetreiveReq;
import us.tx.state.dfps.service.common.request.ChildRecoverySaveReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.MissingChildRetrieveReq;
import us.tx.state.dfps.service.common.request.MissingChildSaveReq;
import us.tx.state.dfps.service.common.request.PostEventReq;
import us.tx.state.dfps.service.common.request.TemporaryAbsenceInfoReq;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.placement.dao.RunawayMissingChildDao;
import us.tx.state.dfps.service.placement.dao.TemporaryAbsenceDao;
import us.tx.state.dfps.service.placement.dto.*;
import us.tx.state.dfps.service.placement.service.PlacementService;
import us.tx.state.dfps.service.placement.service.RunawayMissingChildService;
import us.tx.state.dfps.service.placement.service.TemporaryAbsenceService;
import us.tx.state.dfps.service.subcare.dao.PlacementDao;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.EventPersonLinkDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.web.placement.bean.TemporaryAbsenceInfoBean;


/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Feb 15, 2018- 2:58:31 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class RunawayMissingChildServiceImpl implements RunawayMissingChildService {

	public static final String MISSING_CHILD_CODE = "07";
	private static final String PLCMT_TYPE_UNAUTH = "080";
	@Autowired
	RunawayMissingChildDao runawayMsngDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	EventService eventService;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	TodoDao todoDao;

	@Autowired
	EventPersonLinkDao eventPersonLinkDao;

	@Autowired
	TemporaryAbsenceDao temporaryAbsenceDao;

	@Autowired
	TemporaryAbsenceService temporaryAbsenceService;

	@Autowired
	PlacementDao placementDao;

	@Autowired
	PlacementService placementService;

	private static final String PRIMARY_CHILD = "PC";
	private static final String TASK_TODO_DESC = "Complete Child Recovery Detail Page within 7 days";
	private static final String UNAUTHORIZED_LIVING_ARRANGEMENT = "UP";
	private static final Logger log = Logger.getLogger(RunawayMissingChildServiceImpl.class);

	/**
	 *
	 * Method Name: fetchRunawayMissingList Method Description: retrieves a full
	 * row from CHILD_MSNG_DTL and CHILD_RECOVERY_DTL table for given case and
	 * stage
	 *
	 * @param commonHelperReq
	 * @return List<RunawayMissingDto>
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RunawayChildMissingRes fetchRunawayMissingList(CommonHelperReq commonHelperReq) {
		log.debug("Start method fetchRunawayMissingList in RunawayMissingChildServiceImpl");
		RunawayChildMissingRes runawayChildMissingRes = new RunawayChildMissingRes();
		// Fetch the list of Missing Child Detail and Recovery Child Detail for
		// the case
		// and stage
		// from CHILD_MSNG_DTL and CHILD_RECOVERY_DTL table through DaoImpl
		List<RunawayMissingDto> runawayMsngDtoList = runawayMsngDao.fetchRunawayMissingList(commonHelperReq);
		runawayChildMissingRes.setRunawayMsngList(runawayMsngDtoList);
		log.debug("end method fetchRunawayMissingList in RunawayMissingChildServiceImpl");
		return runawayChildMissingRes;
	}

	/**
	 *
	 * Method Name: fetchMissingChildDetail Method Description: retrieves a row
	 * from CHILD_MSNG_DTL table for given idMissingChildDetail
	 *
	 * @param msngChldReq
	 * @return ChildRecoveryDetailRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public MissingChildDetailRes fetchMissingChildDetail(MissingChildRetrieveReq msngChldReq) {
		log.debug("Start method fetchMissingChildDetail in RunawayMissingChildServiceImpl");
		MissingChildDetailRes msngChildRetrieveRes = new MissingChildDetailRes();
		RunawayMsngRcvryDto runawayMsngRcvryDto = new RunawayMsngRcvryDto();
		MissingChildDetailDto msngChildDetailDto = null;
		NotificationPartiesDto notifyPartyDto = null;
		Long idPerson = runawayMsngDao.fetchPrimaryPerson(msngChldReq.getIdStage());
		// check if idMissingChildDetail is equals '0' and if it is equals '0'
		// set empty data in the response
		if (!ObjectUtils.isEmpty(msngChldReq)
				&& (ServiceConstants.Zero_Value == msngChldReq.getIdChlsMsngDtl()
				|| ObjectUtils.isEmpty(msngChldReq.getIdChlsMsngDtl()))
				&& (ServiceConstants.Zero_Value == msngChldReq.getIdEvent()
				|| ObjectUtils.isEmpty(msngChldReq.getIdEvent()))) {

			msngChildDetailDto = new MissingChildDetailDto();
			runawayMsngRcvryDto = runawayMsngDao.fetchDetailForValidation(idPerson, msngChldReq.getIdCase());
			msngChildDetailDto.setIdPerson(idPerson);
			msngChildDetailDto.setRunawayMsngRcvryDto(runawayMsngRcvryDto);
			if (!ObjectUtils.isEmpty(msngChildDetailDto)
					&& !ObjectUtils.isEmpty(msngChildDetailDto.getIdChldMsngDtl())) {
				msngChildDetailDto.setIsChildRecoveryExists(
						runawayMsngDao.childRecoveryExist(msngChildDetailDto.getIdChldMsngDtl()));
			}
			notifyPartyDto = new NotificationPartiesDto();
			msngChildRetrieveRes.setMsngChildDetailDto(msngChildDetailDto);
			msngChildRetrieveRes.setNotifyPartyDto(notifyPartyDto);
		} else {
			// Fetch the Missing Child Detail for idMissingChildDetail
			// from CHILD_MSNG_DTL table through DaoImpl
			msngChildDetailDto = runawayMsngDao.fetchMissingChildDetail(msngChldReq.getIdChlsMsngDtl(),
					msngChldReq.getIdEvent());
			msngChildDetailDto.setIdPerson(idPerson);
			runawayMsngRcvryDto = runawayMsngDao.fetchDetailForValidation(msngChildDetailDto.getIdPerson(),
					msngChldReq.getIdCase());
			msngChildDetailDto.setRunawayMsngRcvryDto(runawayMsngRcvryDto);
			if (!ObjectUtils.isEmpty(msngChildDetailDto.getIdChldMsngDtl())) {
				msngChildDetailDto.setIsChildRecoveryExists(
						runawayMsngDao.childRecoveryExist(msngChildDetailDto.getIdChldMsngDtl()));
				msngChildDetailDto.setDisplayDeleteFlag(!runawayMsngDao.isChildRecoveryExists(msngChildDetailDto.getIdChldMsngDtl()));
			}
			// Fetch the Notification Party detail for idEvent from
			// CHILD_MSNG_DTL table
			// from NOTIFCTN_PARTIES table through DaoImpl
			notifyPartyDto = runawayMsngDao.fetchNotificationDetail(msngChildDetailDto.getIdEvent());
			msngChildRetrieveRes.setMsngChildDetailDto(msngChildDetailDto);
			if (!ObjectUtils.isEmpty(notifyPartyDto)) {
				msngChildRetrieveRes.setNotifyPartyDto(notifyPartyDto);
			} else {
				notifyPartyDto = new NotificationPartiesDto();
				msngChildRetrieveRes.setNotifyPartyDto(notifyPartyDto);
			}
			EventDto eventDto = eventDao.getEventByid(msngChildDetailDto.getIdEvent());
			if (!ObjectUtils.isEmpty(eventDto)) {
				msngChildRetrieveRes.setCdEventStatus(eventDto.getCdEventStatus());
			}

		}
		log.debug("End method fetchMissingChildDetail in RunawayMissingChildServiceImpl");
		return msngChildRetrieveRes;
	}

	/**
	 *
	 * Method Name: fetchMissingChildDetail Method Description: retrieves a row
	 * from CHILD_MSNG_DTL table for given idMissingChildDetail
	 *
	 * @param chldRcvryReq
	 * @return ChildRecoveryDetailRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ChildRecoveryDetailRes fetchChildRecoveryDetail(ChildRecoveryRetreiveReq chldRcvryReq) {
		log.debug("Start method fetchChildRecoveryDetail in RunawayMissingChildServiceImpl");
		ChildRecoveryDetailRes childRcvryRetrieveRes = new ChildRecoveryDetailRes();
		RunawayMsngRcvryDto runawayMsngRcvryDto = new RunawayMsngRcvryDto();
		ChildRecoveryDetailDto childRecoveryDetailDto = null;
		NotificationPartiesDto notificationPartiesDto = null;
		List<ChildAbsenceReasonDto> chldAbsenceRsnList = null;
		MissingChildDetailDto msngChildDetailDto = new MissingChildDetailDto();
		// Get the Child Missing detail from idChildMissingDtl
		if (!ObjectUtils.isEmpty(chldRcvryReq.getIdChldMsngDtl())
				&& chldRcvryReq.getIdChldMsngDtl() > ServiceConstants.ZERO_VAL) {
			msngChildDetailDto = runawayMsngDao.fetchMissingChildDetail(chldRcvryReq.getIdChldMsngDtl(),
					ServiceConstants.ZERO_VAL);
			childRecoveryDetailDto = runawayMsngDao.fetchChildRecoveryDetail(chldRcvryReq.getIdChldMsngDtl(),
					ServiceConstants.ZERO_VAL);
		} else if (!ObjectUtils.isEmpty(chldRcvryReq.getIdEvent())) {
			childRecoveryDetailDto = runawayMsngDao.fetchChildRecoveryDetail(chldRcvryReq.getIdChldMsngDtl(),
					chldRcvryReq.getIdEvent());
			msngChildDetailDto = runawayMsngDao.fetchMissingChildDetail(childRecoveryDetailDto.getIdChldMsngDtl(),
					ServiceConstants.ZERO_VAL);
		}
		runawayMsngRcvryDto = runawayMsngDao.fetchDetailForValidation(msngChildDetailDto.getIdPerson(),
				chldRcvryReq.getIdCase());
		msngChildDetailDto.setRunawayMsngRcvryDto(runawayMsngRcvryDto);
		childRcvryRetrieveRes.setMsngChildDetailDto(msngChildDetailDto);

		// Fetch the Missing Child Detail for idMissingChildDetail
		// from CHILD_MSNG_DTL table through DaoImpl

		childRcvryRetrieveRes.setChildRecoveryDetailDto(childRecoveryDetailDto);
		// check if IdChildRecoveryDetailDto is equals '0' and if it is equals
		// '0'
		// set empty data in the response
		if (!ObjectUtils.isEmpty(childRecoveryDetailDto)
				&& (ObjectUtils.isEmpty(childRecoveryDetailDto.getIdChldRecoveryDtl())
				|| ServiceConstants.Zero_Value == childRecoveryDetailDto.getIdChldRecoveryDtl())) {
			notificationPartiesDto = new NotificationPartiesDto();
			chldAbsenceRsnList = new ArrayList<ChildAbsenceReasonDto>();
			childRcvryRetrieveRes.setNotificationPartiesDto(notificationPartiesDto);
			childRcvryRetrieveRes.setChldAbsenceRsnList(chldAbsenceRsnList);
			if (!ObjectUtils.isEmpty(msngChildDetailDto.getIndChildReturn())
					&& ServiceConstants.N.equalsIgnoreCase(msngChildDetailDto.getIndChildReturn())
					&& !ObjectUtils.isEmpty(msngChildDetailDto.getCdReasonNotRtrnd())) {
				EventDto eventDto = eventDao.getEventByid(msngChildDetailDto.getIdEvent());
				childRcvryRetrieveRes.setCdEventStatus(eventDto.getCdEventStatus());
			} else {
				childRcvryRetrieveRes.getMsngChildDetailDto().setIndChildReturn(ServiceConstants.EMPTY_STRING);
			}
		} else {
			// Fetch the Notification Party detail for given idEvent(from
			// CHILD_MSNG_DTL
			// table) which link
			// from NOTIFCTN_PARTIES table through DaoImpl
			if (!ObjectUtils.isEmpty(childRecoveryDetailDto)
					&& !ObjectUtils.isEmpty(childRecoveryDetailDto.getIdEvent())) {
				notificationPartiesDto = runawayMsngDao.fetchNotificationDetail(childRecoveryDetailDto.getIdEvent());
				EventDto eventDto = eventDao.getEventByid(childRecoveryDetailDto.getIdEvent());
				childRcvryRetrieveRes.setCdEventStatus(eventDto.getCdEventStatus());
			}
			if (!ObjectUtils.isEmpty(notificationPartiesDto)) {
				childRcvryRetrieveRes.setNotificationPartiesDto(notificationPartiesDto);
			} else {
				notificationPartiesDto = new NotificationPartiesDto();
				childRcvryRetrieveRes.setNotificationPartiesDto(notificationPartiesDto);
			}
			// Fetch the Child Confirmed Reason for Absence detail for
			// idChldRecoveryDtl
			// from CHILD_CNFRMD_RSN_ABSENCE table
			// from NOTIFCTN_PARTIES table through DaoImpl
			if (!ObjectUtils.isEmpty(childRecoveryDetailDto)
					&& !ObjectUtils.isEmpty(childRecoveryDetailDto.getIdChldRecoveryDtl())) {
				chldAbsenceRsnList = runawayMsngDao
						.fetchChildAbsenceReason(childRecoveryDetailDto.getIdChldRecoveryDtl());
			}
			if (!ObjectUtils.isEmpty(chldAbsenceRsnList)) {
				childRcvryRetrieveRes.setChldAbsenceRsnList(chldAbsenceRsnList);
			} else {
				chldAbsenceRsnList = new ArrayList<ChildAbsenceReasonDto>();
				childRcvryRetrieveRes.setChldAbsenceRsnList(chldAbsenceRsnList);
			}

		}
		log.debug("End method fetchChildRecoveryDetail in RunawayMissingChildServiceImpl");
		return childRcvryRetrieveRes;
	}

	/**
	 *
	 * Method Name: saveChildRecoveryDetail Method Description: This method to
	 * call service impl to save the child recovery detail
	 *
	 * @param chldRcvySaveReq
	 * @return ChildRecoveryDetailSaveRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ChildRecoveryDetailSaveRes saveChildRecoveryDetail(ChildRecoverySaveReq chldRcvySaveReq) {
		log.debug("Start method saveChildRecoveryDetail in RunawayMissingChildServiceImpl");
		ChildRecoveryDetailSaveRes chldRcvrySaveRes = new ChildRecoveryDetailSaveRes();
		TodoDto todoDto = new TodoDto();
		ChildRecoveryDetailDto childRecoveryDetailDto = chldRcvySaveReq.getChildRecoveryDetailDto();
		NotificationPartiesDto notificationPartiesDto = chldRcvySaveReq.getNotificationPartiesDto();
		List<ChildAbsenceReasonDto> chldAbsenceRsnList = chldRcvySaveReq.getChldAbsenceRsnList();
		MissingChildDetailDto msngChildDetailDto = chldRcvySaveReq.getMsngChildDetailDto();
		EventPersonLinkDto eventPersonDto = new EventPersonLinkDto();
		// Check whether Required function is Add, and call Post event to save
		// the event
		if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(chldRcvySaveReq.getReqFuncCd())) {
			eventPersonDto = callPostEvent(chldRcvySaveReq.getEventDto(), chldRcvySaveReq.getReqFuncCd(),
					chldRcvySaveReq.getIdCreatedPerson());
			populateAndSaveChildMissingDetail(msngChildDetailDto);
			// Update the Missing Child Detail if there is any record for child
			// Not Return
			if (!ObjectUtils.isEmpty(msngChildDetailDto)
					&& StringUtils.isNotEmpty(msngChildDetailDto.getIndChildReturn())) {
				// Artifact: artf162914 - ALM Defect#15379 : The below is Update for Missing Child Record when Recovery Event is Added.
				// This idCreatedPerson is used to set value of idLastUpdatedPerson in Update scenario.
				msngChildDetailDto.setIdCreatedPerson(chldRcvySaveReq.getIdCreatedPerson());
				runawayMsngDao.saveMissingChildDetail(msngChildDetailDto, ServiceConstants.REQ_FUNC_CD_UPDATE);
				if (!ObjectUtils.isEmpty(msngChildDetailDto)) {
					chldRcvrySaveRes.setErrorDto(msngChildDetailDto.getErrorDto());
				}
			}
			// check whether it is new event and create a task to notify that
			// there is
			// Recovery Detail which should be
			// Completed within 7 days
			if (!ObjectUtils.isEmpty(chldRcvySaveReq) && !ObjectUtils.isEmpty(chldRcvySaveReq.getEventDto())
					&& StringUtils.isNotEmpty(chldRcvySaveReq.getEventDto().getCdEventStatus())
					&& CodesConstant.CEVTSTAT_PROC.equalsIgnoreCase(chldRcvySaveReq.getEventDto().getCdEventStatus())) {
				populateToDo(todoDto, chldRcvySaveReq, eventPersonDto.getIdEvent());
				ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
				serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
				// call todo dao to create the task
				todoDao.todoAUD(todoDto, serviceReqHeaderDto);
			}
			// Check whether Required function is Update, and call Post event to
			// update the
			// event
		} else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equalsIgnoreCase(chldRcvySaveReq.getReqFuncCd())) {
			eventPersonDto = callPostEvent(chldRcvySaveReq.getEventDto(), chldRcvySaveReq.getReqFuncCd(),
					chldRcvySaveReq.getIdCreatedPerson());
			populateAndSaveChildMissingDetail(msngChildDetailDto);
			// Update the Missing Child Detail if there is any record for child
			// Not Return
			if (!ObjectUtils.isEmpty(msngChildDetailDto)
					&& StringUtils.isNotEmpty(msngChildDetailDto.getIndChildReturn())) {
				runawayMsngDao.saveMissingChildDetail(msngChildDetailDto, ServiceConstants.REQ_FUNC_CD_UPDATE);
				if (!ObjectUtils.isEmpty(msngChildDetailDto)) {
					chldRcvrySaveRes.setErrorDto(msngChildDetailDto.getErrorDto());
				}
			}

			// Update To do complete if the event status is Complete
			if (!ObjectUtils.isEmpty(childRecoveryDetailDto)
					&& !ObjectUtils.isEmpty(childRecoveryDetailDto.getIdEvent())
					&& !ObjectUtils.isEmpty(chldRcvySaveReq) && !ObjectUtils.isEmpty(chldRcvySaveReq.getEventDto())
					&& StringUtils.isNotEmpty(chldRcvySaveReq.getEventDto().getCdEventStatus())
					&& CodesConstant.CEVTSTAT_COMP.equalsIgnoreCase(chldRcvySaveReq.getEventDto().getCdEventStatus())) {
				List<TodoDto> todoList = todoDao.fetchToDoListForEvent(chldRcvySaveReq.getEventDto().getIdEvent());
				if(CollectionUtils.isNotEmpty(todoList)) {
					todoDto = todoList.get(ServiceConstants.Zero_INT);
					todoDto.setDtTodoCompleted(new Date());
					ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
					serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_UPDATE);
					// call todo dao to update and complete the task if the event is
					// complete
					todoDao.todoAUD(todoDto, serviceReqHeaderDto);
				}
			}
		}
		// if there is no record of ChildNot Return then save the Child Recovery
		// Detail
		// close TA while creating child recovery detail PPM 65209
		Date taEndDt = null;
		PlacementTa placementTa = temporaryAbsenceDao.getPlacementTAByMissingChild(msngChildDetailDto.getIdChldMsngDtl());
		if (!ObjectUtils.isEmpty(msngChildDetailDto) && StringUtils.isNotEmpty(msngChildDetailDto.getIndChildReturn())
				&& ServiceConstants.Y.equalsIgnoreCase(msngChildDetailDto.getIndChildReturn())) {
			// set idevent from post event
			childRecoveryDetailDto.setIdEvent(eventPersonDto.getIdEvent());
			// set id IdChldMsngDtl
			childRecoveryDetailDto.setIdChldMsngDtl(msngChildDetailDto.getIdChldMsngDtl());
			childRecoveryDetailDto.setIdCreatedPerson(chldRcvySaveReq.getIdCreatedPerson());
			populateAndSaveChildRecovery(childRecoveryDetailDto);
			// call runawayMsngDao dao impl to save Child Recovery Detail data
			// into
			// MSNG_CHILD_RCVERY_DTL
			runawayMsngDao.saveChildRecoveryDetail(childRecoveryDetailDto, chldRcvySaveReq.getReqFuncCd(),
					msngChildDetailDto);
			if (!ObjectUtils.isEmpty(childRecoveryDetailDto)) {
				chldRcvrySaveRes.setErrorDto(childRecoveryDetailDto.getErrorDto());
			}
			notificationPartiesDto.setIdEvent(eventPersonDto.getIdEvent());
			notificationPartiesDto.setIdCreatedPerson(chldRcvySaveReq.getIdCreatedPerson());
			populateAndSaveNofityParty(notificationPartiesDto);
			// call runawayMsngDao dao impl to save Notification party data into
			// MSNG_CHILD_RCVRY_NOTIFCTN
			runawayMsngDao.saveNotificationDetail(notificationPartiesDto, chldRcvySaveReq.getReqFuncCd());
			if (!ObjectUtils.isEmpty(notificationPartiesDto)) {
				chldRcvrySaveRes.setErrorDto(notificationPartiesDto.getErrorDto());
			}
			// call runawayMsngDao dao impl to save Child Runaway Absence
			// Reason detail into ID_MSNG_CHILD_RNWY_RSN
			runawayMsngDao.saveConfirmedRsnAbsDetail(chldAbsenceRsnList, childRecoveryDetailDto);
			if (placementTa != null && !ObjectUtils.isEmpty(placementTa.getIdPlacementTa())) {
				Placement placement = placementDao.selectPlacement(placementTa.getEventByIdPlcmtEvent().getIdEvent());
				if (!ObjectUtils.isEmpty(placement) && DateUtils.getMaxJavaDate().compareTo(placement.getDtPlcmtEnd()) != 0) {
					taEndDt = placement.getDtPlcmtEnd().before(childRecoveryDetailDto.getDtChldRetrnd()) ? placement.getDtPlcmtEnd() : childRecoveryDetailDto.getDtChldRetrnd();
				} else {
					taEndDt = childRecoveryDetailDto.getDtChldRetrnd();
				}
			}
		} else {
			//artf198170 as Fixer you can change from Yes to No for child Returned
			// call runawayMsngDao dao impl to save Notification party data into
			// MSNG_CHILD_RCVRY_NOTIFCTN
			runawayMsngDao.deleteNotificationDetail(notificationPartiesDto);

			// call runawayMsngDao dao impl to save Child Runaway Absence
			// Reason detail into ID_MSNG_CHILD_RNWY_RSN
			runawayMsngDao.deleteConfirmedRsnAbsDetail(chldAbsenceRsnList, childRecoveryDetailDto);

			runawayMsngDao.deleteChildRecoveryDetail(childRecoveryDetailDto, msngChildDetailDto);
			if(placementTa!=null && !ObjectUtils.isEmpty(placementTa.getIdPlacementTa())){
				Placement placement = placementDao.selectPlacement(placementTa.getEventByIdPlcmtEvent().getIdEvent());
				taEndDt = !ObjectUtils.isEmpty(placement) && DateUtils.getMaxJavaDate().compareTo(placement.getDtPlcmtEnd())!=0?placement.getDtPlcmtEnd():null;
			}
		}
		TemporaryAbsenceInfoBean temporaryAbsenceInfoBean = chldRcvySaveReq.getTemporaryAbsenceInfoBean();

		//temporaryAbsenceInfoBean.setAllowHistoricalCreateTA(false);
		if (ObjectUtils.isEmpty(placementTa) && !ObjectUtils.isEmpty(temporaryAbsenceInfoBean)
				&& !ObjectUtils.isEmpty(temporaryAbsenceInfoBean.getAllowHistoricalCreateTA())
				&& temporaryAbsenceInfoBean.getAllowHistoricalCreateTA()) {
			MissingChildSaveReq missingChildSaveReq = new MissingChildSaveReq();
			missingChildSaveReq.setMsngChildDetailDto(chldRcvySaveReq.getMsngChildDetailDto());
			temporaryAbsenceInfoBean.getTaDto().setDtTemporaryAbsenceEnd(childRecoveryDetailDto.getDtChldRetrnd());
			missingChildSaveReq.setTemporaryAbsenceInfoBean(temporaryAbsenceInfoBean);
			missingChildSaveReq.setIdCreatedPerson(chldRcvySaveReq.getIdCreatedPerson());
			createTA(missingChildSaveReq, chldRcvySaveReq.getMsngChildDetailDto());
		} else if (placementTa != null && !ObjectUtils.isEmpty(placementTa.getIdPlacementTa()) && !CompareDates(placementTa.getDtEnd(), taEndDt)) {
			temporaryAbsenceDao.updateTAEndDate(placementTa.getIdPlacementTa(), taEndDt, chldRcvySaveReq.getIdCreatedPerson());
		}
		chldRcvrySaveRes.setIdChldMsngDtl(childRecoveryDetailDto.getIdChldMsngDtl());

		//Added to the response for the intsscc api
		chldRcvrySaveRes.setChildRecoveryDetailDto(childRecoveryDetailDto);
		chldRcvrySaveRes.setNotificationPartiesDto(notificationPartiesDto);
		chldRcvrySaveRes.setChldAbsenceRsnList(chldAbsenceRsnList);
		chldRcvrySaveRes.setMissingChildDetailDto(msngChildDetailDto);
		log.debug("End method saveChildRecoveryDetail in RunawayMissingChildServiceImpl");
		return chldRcvrySaveRes;
	}
	
	

	private boolean CompareDates(Date dtEnd, Date taEndDt) {
		if(dtEnd==null && taEndDt!=null)return false;
		if(dtEnd!=null && taEndDt==null)return false;
		if(dtEnd==null && taEndDt==null)return true;
		return dtEnd.compareTo(taEndDt)==0;
	}

	/**
	 *
	 * Method Name: deleteChildRecoveryDetail Method Description: This method to
	 * call service impl to delete the child recovery detail
	 *
	 * @param chldRcvySaveReq
	 * @return ChildRecoveryDetailSaveRes
	 */
	// artf198170 : enable delete function for fixer
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ChildRecoveryDetailSaveRes deleteChildRecoveryDetail(ChildRecoverySaveReq chldRcvySaveReq) {
		log.debug("Start method deleteChildRecoveryDetail in RunawayMissingChildServiceImpl");
		ChildRecoveryDetailSaveRes chldRcvrySaveRes = new ChildRecoveryDetailSaveRes();
		TodoDto todoDto = new TodoDto();
		ChildRecoveryDetailDto childRecoveryDetailDto = chldRcvySaveReq.getChildRecoveryDetailDto();
		NotificationPartiesDto notificationPartiesDto = chldRcvySaveReq.getNotificationPartiesDto();
		List<ChildAbsenceReasonDto> chldAbsenceRsnList = chldRcvySaveReq.getChldAbsenceRsnList();
		MissingChildDetailDto msngChildDetailDto = chldRcvySaveReq.getMsngChildDetailDto();
		String indChildReturn = msngChildDetailDto.getIndChildReturn();
		EventPersonLinkDto eventPersonDto = new EventPersonLinkDto();

		if (ServiceConstants.REQ_FUNC_CD_DELETE.equalsIgnoreCase(chldRcvySaveReq.getReqFuncCd())) {

			populateAndSaveChildMissingDetail(msngChildDetailDto);
			msngChildDetailDto.setIndChildReturn("N");
			// Update the Missing Child Detail if there is any record for child
			// Not Return
			if (!ObjectUtils.isEmpty(msngChildDetailDto)
					&& StringUtils.isNotEmpty(msngChildDetailDto.getIndChildReturn())) {
				runawayMsngDao.saveMissingChildDetail(msngChildDetailDto, ServiceConstants.REQ_FUNC_CD_UPDATE);
				if (!ObjectUtils.isEmpty(msngChildDetailDto)) {
					chldRcvrySaveRes.setErrorDto(msngChildDetailDto.getErrorDto());
				}
			}

			// Update To do complete if the event status is Complete
			if (!ObjectUtils.isEmpty(childRecoveryDetailDto)
					&& !ObjectUtils.isEmpty(childRecoveryDetailDto.getIdEvent())
					&& !ObjectUtils.isEmpty(chldRcvySaveReq) && !ObjectUtils.isEmpty(chldRcvySaveReq.getEventDto())
			) {
				List<TodoDto> todoList = todoDao.fetchToDoListForEvent(chldRcvySaveReq.getEventDto().getIdEvent());
				if(CollectionUtils.isNotEmpty(todoList)) {
					todoDto = todoList.get(ServiceConstants.Zero_INT);
					todoDto.setDtTodoCompleted(new Date());
					ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
					serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_DELETE);
					// call todo dao to update and complete the task if the event is
					// complete
					todoDao.todoAUD(todoDto, serviceReqHeaderDto);
				}
			}
		}
		// if there is no record of ChildNot Return then save the Child Recovery
		// Detail
		if (ServiceConstants.Y.equalsIgnoreCase(indChildReturn)) {
			notificationPartiesDto.setIdEvent(eventPersonDto.getIdEvent());
			notificationPartiesDto.setIdCreatedPerson(chldRcvySaveReq.getIdCreatedPerson());
			populateAndSaveNofityParty(notificationPartiesDto);
			// call runawayMsngDao dao impl to save Notification party data into
			// MSNG_CHILD_RCVRY_NOTIFCTN
			runawayMsngDao.deleteNotificationDetail(notificationPartiesDto);

			// call runawayMsngDao dao impl to save Child Runaway Absence
			// Reason detail into ID_MSNG_CHILD_RNWY_RSN
			runawayMsngDao.deleteConfirmedRsnAbsDetail(chldAbsenceRsnList, childRecoveryDetailDto);

			//remove end date from Missing TA if present PPM 65209
			//find if there is missing TA
			PlacementTa placementTa = temporaryAbsenceDao.getPlacementTAByMissingChild(msngChildDetailDto.getIdChldMsngDtl());
			if(placementTa!=null && !ObjectUtils.isEmpty(placementTa.getIdPlacementTa())){
				//check to see placement is present
				Placement placement = placementDao.selectPlacement(placementTa.getEventByIdPlcmtEvent().getIdEvent());
				Date taEndDt = null;
				if(!ObjectUtils.isEmpty(placement) && DateUtils.getMaxJavaDate().compareTo(placement.getDtPlcmtEnd())!=0){
					taEndDt = placement.getDtPlcmtEnd();
				}
				temporaryAbsenceDao.updateTAEndDate(placementTa.getIdPlacementTa(), taEndDt,chldRcvySaveReq.getIdCreatedPerson());
			}



			// set idevent from post event
			childRecoveryDetailDto.setIdEvent(eventPersonDto.getIdEvent());
			// set id IdChldMsngDtl
			childRecoveryDetailDto.setIdChldMsngDtl(msngChildDetailDto.getIdChldMsngDtl());
			childRecoveryDetailDto.setIdCreatedPerson(chldRcvySaveReq.getIdCreatedPerson());
			populateAndSaveChildRecovery(childRecoveryDetailDto);
			// call runawayMsngDao dao impl to save Child Recovery Detail data
			// into
			// MSNG_CHILD_RCVERY_DTL
			runawayMsngDao.deleteChildRecoveryDetail(childRecoveryDetailDto, msngChildDetailDto);
			if (!ObjectUtils.isEmpty(childRecoveryDetailDto)) {
				chldRcvrySaveRes.setErrorDto(childRecoveryDetailDto.getErrorDto());
			}
		}
		eventPersonDto = callPostEvent(chldRcvySaveReq.getEventDto(), chldRcvySaveReq.getReqFuncCd(),
				chldRcvySaveReq.getIdCreatedPerson());
		chldRcvrySaveRes.setIdChldMsngDtl(childRecoveryDetailDto.getIdChldMsngDtl());
		log.debug("End method deleteChildRecoveryDetail in RunawayMissingChildServiceImpl");
		return chldRcvrySaveRes;
	}

	/**
	 *
	 * Method Name: populateToDo Method Description: populate ToDoDto for
	 * creating task
	 *
	 * @param todoDto
	 * @param chldRcvySaveReq
	 * @param idEvent
	 */
	private void populateToDo(TodoDto todoDto, ChildRecoverySaveReq chldRcvySaveReq, Long idEvent) {
		Calendar cal = Calendar.getInstance();
		Date dueDate = new Date();
		cal.setTime(dueDate);
		// populate to ToDo Dto
		cal.add(Calendar.DATE, ServiceConstants.SEVEN_VALUE);
		dueDate = cal.getTime();
		todoDto.setCdTodoType(ServiceConstants.TODO_ACTIONS_TASK);
		todoDto.setCdTodoTask(chldRcvySaveReq.getEventDto().getCdTask());
		todoDto.setDtTodoCompleted(ServiceConstants.NULL_DATE_TYPE);
		todoDto.setDtTodoCreated(new Date());
		todoDto.setDtTodoDue(dueDate);
		todoDto.setDtTodoTaskDue(todoDto.getDtTodoTaskDue());
		todoDto.setIdTodoCase(chldRcvySaveReq.getEventDto().getIdCase());
		todoDto.setIdTodoPersCreator(chldRcvySaveReq.getIdCreatedPerson());
		todoDto.setIdTodoPersAssigned(chldRcvySaveReq.getIdCreatedPerson());
		todoDto.setIdTodoPersWorker(chldRcvySaveReq.getIdCreatedPerson());
		todoDto.setIdTodoStage(chldRcvySaveReq.getEventDto().getIdStage());
		todoDto.setIdTodoEvent(idEvent);
		todoDto.setTodoDesc(TASK_TODO_DESC);
		todoDto.setIdTodo(todoDto.getIdTodo());
		todoDto.setDtLastUpdate(new Date());
	}

	/**
	 *
	 * Method Name: callPostEvent Method Description: This method is used to
	 * call post event to insert/Update Event Table
	 *
	 * @param evtDto
	 * @param evtDto
	 * @param cReqFunc
	 * @return EventPersonLinkDto
	 */
	private EventPersonLinkDto callPostEvent(EventDto evtDto, String cReqFunc, Long createdPerson) {
		EventPersonLinkDto eventPersonDto = new EventPersonLinkDto();
		PostEventReq postEventReq = new PostEventReq();
		List<PostEventPersonDto> postEventPersonList=new ArrayList<>();
		PostEventPersonDto postEventPersonDto=new PostEventPersonDto();
		// if the event is not zero then it is update populate the Dto for Add
		if (!ObjectUtils.isEmpty(evtDto.getIdEvent()) && ServiceConstants.ZERO_VAL != evtDto.getIdEvent()) {

			postEventReq.setUlIdEvent(evtDto.getIdEvent());
			// call the eventDao to fetch event by eventid
			EventDto eventDto = eventDao.getEventByid(evtDto.getIdEvent());
			// set the values for postevent from eventDto
			postEventReq.setReqFuncCd(cReqFunc);
			postEventReq.setDtDtEventOccurred(eventDto.getDtEventOccurred());
			postEventReq.setTsLastUpdate(new Date());
			postEventReq.setSzCdTask(eventDto.getCdTask());
			postEventReq.setSzCdEventStatus(evtDto.getCdEventStatus());
			postEventReq.setSzCdEventType(eventDto.getCdEventType());
			if (!TypeConvUtil.isNullOrEmpty(eventDto.getIdPerson())) {
				postEventReq.setUlIdPerson(createdPerson);
			}
			if (!TypeConvUtil.isNullOrEmpty(eventDto.getEventDescr())) {
				postEventReq.setSzTxtEventDescr(evtDto.getEventDescr());
			}
			Long idChild=eventPersonLinkDao.getPersonId(eventDto.getIdEvent());
			eventPersonDto.setIdPerson(idChild);
			postEventPersonDto.setIdPerson(idChild);
		} else {
			// if the event is zero then it is add
			// stagePersonLinkDao clled to get person id of the PrimaryChild of
			// the stage
			Long idPerson = stagePersonLinkDao.getPersonIdByRole(evtDto.getIdStage(), PRIMARY_CHILD);
			// set the values for postevent
			postEventReq.setReqFuncCd(cReqFunc);
			postEventReq.setUlIdEvent(evtDto.getIdEvent());

			postEventReq.setSzCdTask(evtDto.getCdTask());
			postEventReq.setSzCdEventStatus(evtDto.getCdEventStatus());
			postEventReq.setSzCdEventType(evtDto.getCdEventType());
			postEventReq.setSzTxtEventDescr(evtDto.getEventDescr());
			postEventPersonDto.setIdPerson(idPerson);
			eventPersonDto.setIdPerson(idPerson);
		}
		postEventReq.setUlIdStage(evtDto.getIdStage());
		if (!TypeConvUtil.isNullOrEmpty(evtDto.getIdCase())) {
			postEventReq.setUlIdCase(evtDto.getIdCase());
		}
		if (!ObjectUtils.isEmpty(createdPerson)) {
			postEventReq.setUlIdPerson(createdPerson);
			postEventReq.setUserId(createdPerson.toString());
		}
		postEventPersonList.add(postEventPersonDto);
		postEventReq.setPostEventPersonList(postEventPersonList);
		// call pos event for insert/update the event
		PostEventRes postEventRes = eventService.postEvent(postEventReq);
		eventPersonDto.setIdEvent(postEventRes.getUlIdEvent());
		return eventPersonDto;

	}

	/**
	 *
	 * Method Name: saveMissingChildDetail Method Description: This method to
	 * call service impl to save the Missing child detail
	 *
	 * @param msngChildSaveReq
	 * @return MissingChildDetailSaveRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public MissingChildDetailSaveRes saveMissingChildDetail(MissingChildSaveReq msngChildSaveReq) {
		log.debug("Start method saveMissingChildDetail in RunawayMissingChildServiceImpl");
		MissingChildDetailSaveRes msngChildSaveRes = new MissingChildDetailSaveRes();
		NotificationPartiesDto notificationPartiesDto = msngChildSaveReq.getNotifyPartyDto();
		MissingChildDetailDto msngChildDetailDto = msngChildSaveReq.getMsngChildDetailDto();
		EventPersonLinkDto eventPersonDto = new EventPersonLinkDto();
		boolean pendingEvent = false;
		//check if already Missing Child Information Exist for the case. If Yes, return with Error.
		if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(msngChildSaveReq.getReqFuncCd())
				&& (org.springframework.util.StringUtils.isEmpty(msngChildSaveReq.getEventDto().getIdEvent())
						|| ServiceConstants.ZERO_VAL.equals(msngChildSaveReq.getEventDto().getIdEvent()))) {
			CommonHelperReq req = new CommonHelperReq();
			req.setIdCase(msngChildSaveReq.getEventDto().getIdCase());
			req.setIdStage(msngChildSaveReq.getEventDto().getIdStage());
			List<RunawayMissingDto> missingChildDtoList = runawayMsngDao.fetchRunawayMissingList(req);
			if (!ObjectUtils.isEmpty(missingChildDtoList)) {
				pendingEvent = missingChildDtoList.stream().anyMatch(
						missingEvent -> CodesConstant.CEVTSTAT_PROC.equals(missingEvent.getCdMissingEventStatus())
								|| StringUtils.isEmpty(missingEvent.getCdRecoveryEventStatus())
								|| CodesConstant.CEVTSTAT_PROC.equals(missingEvent.getCdRecoveryEventStatus()));
			}
		}

		if(!pendingEvent){
			// Check whether Required function is Add/Update, and call Post event to save
			// the event
			if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(msngChildSaveReq.getReqFuncCd())
					|| ServiceConstants.REQ_FUNC_CD_UPDATE.equalsIgnoreCase(msngChildSaveReq.getReqFuncCd())) {
				eventPersonDto = callPostEvent(msngChildSaveReq.getEventDto(), msngChildSaveReq.getReqFuncCd(),
						msngChildSaveReq.getIdCreatedPerson());
			}
			msngChildDetailDto.setIdEvent(eventPersonDto.getIdEvent());
			msngChildDetailDto.setIdPerson(eventPersonDto.getIdPerson());
			msngChildDetailDto.setIdCreatedPerson(msngChildSaveReq.getIdCreatedPerson());
			populateAndSaveChildMissingDetail(msngChildDetailDto);
			// call runawayMsngDao dao impl to save Missing Child Detail data into
			// MSNG_CHILD_DTL
			msngChildDetailDto = runawayMsngDao.saveMissingChildDetail(msngChildDetailDto, msngChildSaveReq.getReqFuncCd());
			if (!ObjectUtils.isEmpty(msngChildDetailDto)) {
				msngChildSaveRes.setErrorDto(msngChildDetailDto.getErrorDto());
				msngChildSaveRes.setMsngChildDetailDto(msngChildDetailDto);
			}
			notificationPartiesDto.setIdEvent(eventPersonDto.getIdEvent());
			notificationPartiesDto.setIdCreatedPerson(msngChildSaveReq.getIdCreatedPerson());
			populateAndSaveNofityParty(notificationPartiesDto);
			// call runawayMsngDao dao impl to save Notification party data into
			// MSNG_CHILD_RCVRY_NOTIFCTN
			runawayMsngDao.saveNotificationDetail(notificationPartiesDto, msngChildSaveReq.getReqFuncCd());
			if (!ObjectUtils.isEmpty(notificationPartiesDto)) {
				msngChildSaveRes.setErrorDto(notificationPartiesDto.getErrorDto());
				msngChildSaveRes.setNotifyPartyDto(notificationPartiesDto);
			}
			msngChildSaveRes.setIdChldMsngDtl(msngChildDetailDto.getIdChldMsngDtl());
			msngChildSaveRes.setIdChldMsngEvent(eventPersonDto.getIdEvent());
			log.debug("End method saveMissingChildDetail in RunawayMissingChildServiceImpl");
			log.debug("Start of  auto create/ update Temporary absence in RunawayMissingChildServiceImpl");
			if(!ObjectUtils.isEmpty(msngChildSaveReq.getTemporaryAbsenceInfoBean())) {
				msngChildSaveReq.getTemporaryAbsenceInfoBean().setAllowHistoricalCreateTA(false);
			}
			createTA(msngChildSaveReq, msngChildDetailDto);
		}else{
			ErrorDto error = new ErrorDto();
			error.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			msngChildSaveRes.setErrorDto(error);
		}
		return msngChildSaveRes;
	}

	private void createTA(MissingChildSaveReq msngChildSaveReq, MissingChildDetailDto msngChildDetailDto) {
		TemporaryAbsenceInfoBean taInfoBean = msngChildSaveReq.getTemporaryAbsenceInfoBean();
		if (!ObjectUtils.isEmpty(taInfoBean)) {
			TemporaryAbsenceDto taDto = taInfoBean.getTaDto();
			boolean createTA = true;
			PlacementDto placementDto = placementService.fetchPlacement(taDto.getIdLinkedPlcmtEvent());
			if (!ObjectUtils.isEmpty(placementDto) && !ObjectUtils.isEmpty(placementDto.getIdPlcmtEvent())) {
				//setting the placemnt start dt for intsscc api response, to chack for alertmessage
				if(!ObjectUtils.isEmpty(placementDto.getDtPlcmtStart())){
					msngChildDetailDto.setDtPlacementStart(placementDto.getDtPlcmtStart());
				}
				TemporaryAbsenceDto existingTaDto = temporaryAbsenceDao.getActiveTAForPlacement(placementDto.getIdPlcmtEvent());
				if(!ObjectUtils.isEmpty(existingTaDto)) {
					createTA = taInfoBean.getAllowHistoricalCreateTA()
							|| DateUtils.isBefore(existingTaDto.getDtTemporaryAbsenceStart(), msngChildDetailDto.getDtChildMissing());
				}
				TemporaryAbsenceInfoReq taInfoReq = new TemporaryAbsenceInfoReq();
				PlacementTa placementTa = temporaryAbsenceDao.getPlacementTAByMissingChild(msngChildDetailDto.getIdChldMsngDtl());
				taInfoReq.setTaDto(taDto);
				if (!ObjectUtils.isEmpty(placementTa) && placementTa.getIdPlacementTa() > 0L) {

					if(!ObjectUtils.isEmpty(taDto.getDtTemporaryAbsenceStart())) {
						if (DateUtils.isBefore(taDto.getDtTemporaryAbsenceStart(), placementDto.getDtPlcmtStart())) {
							temporaryAbsenceDao.deleteTaInfo(placementTa.getIdPlacementTa(), taInfoBean.getUserId());
						} else {
							temporaryAbsenceDao.updateTAStartDate(placementTa.getIdPlacementTa(),
									taInfoBean.getTaDto().getDtTemporaryAbsenceStart(), taInfoBean.getUserId());
						}
					}
				} else if(createTA && DateUtils.isAfter(msngChildDetailDto.getDtChildMissing(), placementDto.getDtPlcmtStart())) {
					if (!ObjectUtils.isEmpty(existingTaDto) && !ObjectUtils.isEmpty(existingTaDto.getIdPlacementTa())) {
						if (DateUtils.isBefore(existingTaDto.getDtTemporaryAbsenceStart(), msngChildDetailDto.getDtChildMissing())) {
							temporaryAbsenceDao.updateTAEndDate(existingTaDto.getIdPlacementTa(), msngChildSaveReq.getMsngChildDetailDto().getDtChildMissing()
									, msngChildSaveReq.getMsngChildDetailDto().getIdCreatedPerson());
						}
					}
					if(!PLCMT_TYPE_UNAUTH.equalsIgnoreCase(taInfoBean.getTaDto().getPlcmtType())
							&& !ServiceConstants.CPLLAFRM_01.equalsIgnoreCase(taInfoBean.getTaDto().getLivingArrangement())
							&& !ServiceConstants.CPLLAFRM_24.equalsIgnoreCase(taInfoBean.getTaDto().getLivingArrangement())
							&& !ServiceConstants.CPLLAFRM_28.equalsIgnoreCase(taInfoBean.getTaDto().getLivingArrangement())
							&& !ServiceConstants.CPLLAFRM_29.equalsIgnoreCase(taInfoBean.getTaDto().getLivingArrangement())
							&& !ServiceConstants.CPLLAFRM_47.equalsIgnoreCase(taInfoBean.getTaDto().getLivingArrangement())
							&& !ServiceConstants.CPLLAFRM_76.equalsIgnoreCase(taInfoBean.getTaDto().getLivingArrangement())
							|| UNAUTHORIZED_LIVING_ARRANGEMENT.equalsIgnoreCase(taInfoBean.getTaDto().getLivingArrangement()))
					{
						if (!ObjectUtils.isEmpty(taDto)) {
							taDto.setTemporaryAbsenceType("07");
							taDto.setIdChldMsngDtl(msngChildDetailDto.getIdChldMsngDtl());
							taInfoReq.setTaDto(taDto);
						}
						if (!ObjectUtils.isEmpty(taInfoBean.getUserId())) {
							taInfoReq.setUserId(taInfoBean.getUserId().toString());
						}
						taInfoReq.setReqFuncCd("A");
						if (!ObjectUtils.isEmpty(taInfoBean.getIdStage())) {
							taInfoReq.setIdStage(taInfoBean.getIdStage());
						}
						if (!ObjectUtils.isEmpty(taInfoBean.getIdCase())) {
							taInfoReq.setIdCase(taInfoBean.getIdCase());
						}
						TemporaryAbsenceInfoRes temporaryAbsenceInfoRes = temporaryAbsenceService.saveTaDetailInfo(taInfoReq);
						if(taInfoBean.getAllowHistoricalCreateTA() && !ObjectUtils.isEmpty(temporaryAbsenceInfoRes)
								&& !ObjectUtils.isEmpty(temporaryAbsenceInfoRes.getTemporaryAbsenceDto().getIdPlacementTa())) {
							temporaryAbsenceDao.updateTAEndDate(temporaryAbsenceInfoRes.getTemporaryAbsenceDto().getIdPlacementTa(),
									taDto.getDtTemporaryAbsenceEnd(),msngChildSaveReq.getIdCreatedPerson());
						}
					}
				}
			}
		}
	}

	/**
	 *
	 * Method Name: deleteMissingChildDetail Method Description: This method to
	 * call service impl to delete the Missing child detail
	 *
	 * @param msngChildSaveReq
	 * @return MissingChildDetailSaveRes
	 */
	// artf198170 : enable delete function for fixer
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public MissingChildDetailSaveRes deleteMissingChildDetail(MissingChildSaveReq msngChildSaveReq) {
		log.debug("Start method deleteMissingChildDetail in RunawayMissingChildServiceImpl");
		MissingChildDetailSaveRes msngChildSaveRes = new MissingChildDetailSaveRes();
		NotificationPartiesDto notificationPartiesDto = msngChildSaveReq.getNotifyPartyDto();
		MissingChildDetailDto msngChildDetailDto = msngChildSaveReq.getMsngChildDetailDto();

		populateAndSaveNofityParty(notificationPartiesDto);

		//delete missing TA if present for PPM 65209
		//check to see if missing TA present
		PlacementTa placementTa = temporaryAbsenceDao.getPlacementTAByMissingChild(msngChildDetailDto.getIdChldMsngDtl());
		if(placementTa!=null && !ObjectUtils.isEmpty(placementTa.getIdPlacementTa())){
			temporaryAbsenceDao.deleteTaInfo(placementTa.getIdPlacementTa(),msngChildSaveReq.getIdCreatedPerson());
		}


		//delete Notification detail of MSNG_CHILD
		runawayMsngDao.deleteNotificationDetail(notificationPartiesDto);

		//delete Missing Child Detail data MSNG_CHILD_DTL
		runawayMsngDao.deleteMissingChildDetail(msngChildDetailDto);

		// Check whether Required function is Add/Update, and call Post event to save
		// the event
		callPostEvent(msngChildSaveReq.getEventDto(), msngChildSaveReq.getReqFuncCd(),
				msngChildSaveReq.getIdCreatedPerson());

		msngChildSaveRes.setIdChldMsngDtl(msngChildDetailDto.getIdChldMsngDtl());
		log.debug("End method deleteMissingChildDetail in RunawayMissingChildServiceImpl");
		return msngChildSaveRes;
	}

	/**
	 *
	 * Method Name: populateAndSaveChildMissingDetail Method Description: This
	 * method will combine Data and time and set in the dto
	 *
	 * @param msngChildDetailDto
	 * @return MissingChildDetailDto
	 */
	private void populateAndSaveChildMissingDetail(MissingChildDetailDto msngChildDetailDto) {
		// combine the Date and Time when child missed and set the Time in dto
		if (!ObjectUtils.isEmpty(msngChildDetailDto.getDtChildMissing())) {
			msngChildDetailDto.setDtChildMissing(Date.from(DateUtils
					.getDateTime(msngChildDetailDto.getDtChildMissing(), msngChildDetailDto.getTimeChildMissing())
					.atZone(ZoneId.systemDefault()).toInstant()));
		}
		// combine the Date and Time of worker notified and set the Time in dto
		if (!ObjectUtils.isEmpty(msngChildDetailDto.getDtWorkerNotified())) {
			msngChildDetailDto.setDtWorkerNotified(Date.from(DateUtils
					.getDateTime(msngChildDetailDto.getDtWorkerNotified(), msngChildDetailDto.getTimeWorkerNotified())
					.atZone(ZoneId.systemDefault()).toInstant()));
		}

	}

	/**
	 *
	 * Method Name: setDateTimeForNofityParty Method Description: This method
	 * will combine Data and time and set in the dto
	 *
	 * @param notifyPartyDto
	 */
	private void populateAndSaveNofityParty(NotificationPartiesDto notifyPartyDto) {
		// combine the Date and Time of Attorney Ad Litem and set the Time
		if (!ObjectUtils.isEmpty(notifyPartyDto.getDtAtrnyNotified())
				&& !ObjectUtils.isEmpty(notifyPartyDto.getTimeAtrnyNotified())) {
			notifyPartyDto.setDtAtrnyNotified(Date.from(
					DateUtils.getDateTime(notifyPartyDto.getDtAtrnyNotified(), notifyPartyDto.getTimeAtrnyNotified())
							.atZone(ZoneId.systemDefault()).toInstant()));
		}
		// combine the Date and Time of CASA and set the Time
		if (!ObjectUtils.isEmpty(notifyPartyDto.getDtCasaNotified())
				&& !ObjectUtils.isEmpty(notifyPartyDto.getTimeCasaNotified())) {
			notifyPartyDto.setDtCasaNotified(Date.from(
					DateUtils.getDateTime(notifyPartyDto.getDtCasaNotified(), notifyPartyDto.getTimeCasaNotified())
							.atZone(ZoneId.systemDefault()).toInstant()));
		}
		// combine the Date and Time of Court and set the Time
		if (!ObjectUtils.isEmpty(notifyPartyDto.getDtCourtNotified())
				&& !ObjectUtils.isEmpty(notifyPartyDto.getTimeCourtNotified())) {
			notifyPartyDto.setDtCourtNotified(Date.from(
					DateUtils.getDateTime(notifyPartyDto.getDtCourtNotified(), notifyPartyDto.getTimeCourtNotified())
							.atZone(ZoneId.systemDefault()).toInstant()));
		}
		// combine the Date and Time of Attorney Ad Litem and set the Time
		if (!ObjectUtils.isEmpty(notifyPartyDto.getDtGrdnNotified())
				&& !ObjectUtils.isEmpty(notifyPartyDto.getTimeGrdnNotified())) {
			notifyPartyDto.setDtGrdnNotified(Date.from(
					DateUtils.getDateTime(notifyPartyDto.getDtGrdnNotified(), notifyPartyDto.getTimeGrdnNotified())
							.atZone(ZoneId.systemDefault()).toInstant()));
		}
		// combine the Date and Time of Juvenile Justice and set the Time
		if (!ObjectUtils.isEmpty(notifyPartyDto.getDtJuvnleNotified())
				&& !ObjectUtils.isEmpty(notifyPartyDto.getTimeJuvnleNotified())) {
			notifyPartyDto.setDtJuvnleNotified(Date.from(
					DateUtils.getDateTime(notifyPartyDto.getDtJuvnleNotified(), notifyPartyDto.getTimeJuvnleNotified())
							.atZone(ZoneId.systemDefault()).toInstant()));
		}
		// combine the Date and Time of Law Enforcement and set the Time
		if (!ObjectUtils.isEmpty(notifyPartyDto.getDtLENotified())
				&& !ObjectUtils.isEmpty(notifyPartyDto.getTimeLENotified())) {
			notifyPartyDto.setDtLENotified(Date
					.from(DateUtils.getDateTime(notifyPartyDto.getDtLENotified(), notifyPartyDto.getTimeLENotified())
							.atZone(ZoneId.systemDefault()).toInstant()));
		}
		// combine the Date and Time of NCMEC and set the Time
		if (!ObjectUtils.isEmpty(notifyPartyDto.getDtNCMECNotified())
				&& !ObjectUtils.isEmpty(notifyPartyDto.getTimeNCMECNotified())) {
			notifyPartyDto.setDtNCMECNotified(Date.from(
					DateUtils.getDateTime(notifyPartyDto.getDtNCMECNotified(), notifyPartyDto.getTimeNCMECNotified())
							.atZone(ZoneId.systemDefault()).toInstant()));
		}
		// combine the Date and Time of Parents Attorney and set the Time
		if (!ObjectUtils.isEmpty(notifyPartyDto.getDtPrntsatrnyNotified())
				&& !ObjectUtils.isEmpty(notifyPartyDto.getTimePrntsatrnyNotified())) {
			notifyPartyDto.setDtPrntsatrnyNotified(Date.from(DateUtils
					.getDateTime(notifyPartyDto.getDtPrntsatrnyNotified(), notifyPartyDto.getTimePrntsatrnyNotified())
					.atZone(ZoneId.systemDefault()).toInstant()));
		}
		// combine the Date and Time of Parents and set the Time
		if (!ObjectUtils.isEmpty(notifyPartyDto.getDtPrntsNotified())
				&& !ObjectUtils.isEmpty(notifyPartyDto.getTimePrntsNotified())) {
			notifyPartyDto.setDtPrntsNotified(Date.from(
					DateUtils.getDateTime(notifyPartyDto.getDtPrntsNotified(), notifyPartyDto.getTimePrntsNotified())
							.atZone(ZoneId.systemDefault()).toInstant()));
		}
		// combine the Date and Time of Special Investigation Division and set
		// the Time
		if (!ObjectUtils.isEmpty(notifyPartyDto.getDtSpclINVNTFD())
				&& !ObjectUtils.isEmpty(notifyPartyDto.getTimeSpclINVNTFD())) {
			notifyPartyDto.setDtSpclINVNTFD(Date
					.from(DateUtils.getDateTime(notifyPartyDto.getDtSpclINVNTFD(), notifyPartyDto.getTimeSpclINVNTFD())
							.atZone(ZoneId.systemDefault()).toInstant()));
		}

	}

	/**
	 *
	 * Method Name: populateAndSaveChildRecovery Method Description: This method
	 * will combine Data and time and set in the dto
	 *
	 *
	 * @param childRcvryDetailDto
	 */
	private void populateAndSaveChildRecovery(ChildRecoveryDetailDto childRcvryDetailDto) {
		// combine the Date and Time when child returned and set the Time in dto
		if (!ObjectUtils.isEmpty(childRcvryDetailDto.getDtChldRetrnd())) {
			childRcvryDetailDto.setDtChldRetrnd(Date.from(DateUtils
					.getDateTime(childRcvryDetailDto.getDtChldRetrnd(), childRcvryDetailDto.getTimeChldRetrnd())
					.atZone(ZoneId.systemDefault()).toInstant()));
		}
		// combine the Date and Time of worker notified and set the Time in dto
		if (!ObjectUtils.isEmpty(childRcvryDetailDto.getDtWorkerNotified())) {
			childRcvryDetailDto.setDtWorkerNotified(Date.from(DateUtils
					.getDateTime(childRcvryDetailDto.getDtWorkerNotified(), childRcvryDetailDto.getTimeWorkerNotified())
					.atZone(ZoneId.systemDefault()).toInstant()));
		}
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public MissingChildIdsRes fetchMissingChildIds(Long idEvent) {
		log.debug("Start method fetchMissingChildIds in RunawayMissingChildServiceImpl");
		// Fetch the Missing Child Id and Notification Id for
		// the event
		// from MSNG_CHILD_DTL and MSNG_CHILD_RCVRY_NOTIFCTN table through DaoImpl
		RunawayMissingIdsDto runawayMissingIdsDto = runawayMsngDao.fetchMissingChildIds(idEvent);
		MissingChildIdsRes missingChildIdsRes = new MissingChildIdsRes();
		missingChildIdsRes.setRunawayMissingIdsDto(runawayMissingIdsDto);
		log.debug("end method fetchMissingChildIds in RunawayMissingChildServiceImpl");
		return missingChildIdsRes;
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ChildRecoveryIdsRes fetchChildRecoveryIds(Long idEvent) {
		log.debug("Start method fetchChildRecoveryIds in RunawayMissingChildServiceImpl");
		// Fetch the Missing Child Id and Notification Id and Child recovery Id for
		// the event
		// from MSNG_CHILD_RCVRY_DTL and MSNG_CHILD_RCVRY_NOTIFCTN table through DaoImpl
		RunawayChildRecoveryIdsDto runawayChildRecoveryIdsDto = runawayMsngDao.fetchChildRecoveryIds(idEvent);

		MissingChildDetailDto msngChildDetailDto = runawayMsngDao.fetchMissingChildDetail(runawayChildRecoveryIdsDto.getIdChldMsngDtl(), 0L);

		List<ChildAbsenceReasonDto> chldAbsenceRsnList = new ArrayList<>();
		if (!ObjectUtils.isEmpty(runawayChildRecoveryIdsDto)
				&& !ObjectUtils.isEmpty(runawayChildRecoveryIdsDto.getIdChldMsngRcvryDtl())) {
			chldAbsenceRsnList = runawayMsngDao
					.fetchChildAbsenceReason(runawayChildRecoveryIdsDto.getIdChldMsngRcvryDtl());
		}


		ChildRecoveryIdsRes childRecoveryIdsRes = new ChildRecoveryIdsRes();
		childRecoveryIdsRes.setRunawayChildRecoveryIdsDto(runawayChildRecoveryIdsDto);
		childRecoveryIdsRes.setMsngChildDetailDto(msngChildDetailDto);
		if (!ObjectUtils.isEmpty(chldAbsenceRsnList)) {
			childRecoveryIdsRes.setChldAbsenceRsnList(chldAbsenceRsnList);
		} else {
			chldAbsenceRsnList = new ArrayList<ChildAbsenceReasonDto>();
			childRecoveryIdsRes.setChldAbsenceRsnList(chldAbsenceRsnList);
		}
		log.debug("end method fetchMissingChildIds in RunawayMissingChildServiceImpl");
		return childRecoveryIdsRes;
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ChildRecoveryLastUpdIdsRes fetchChildRecoveryLastUpdate(Long idEvent) {
		log.debug("Start method fetchChildRecoveryLastUpdate in RunawayMissingChildServiceImpl");
		// Fetch the Missing Child Id, Notification Id, Child recovery Id and dt Last Update for
		// the event
		// from MSNG_CHILD_RCVRY_DTL and MSNG_CHILD_RCVRY_NOTIFCTN table through DaoImpl
		RunawayChildRecoveryIdsDto runawayChildRecoveryIdsDto = runawayMsngDao.fetchChildRecoveryIds(idEvent);

		ChildRecoveryLastUpdIdsRes childRecoveryLastUpdIdsRes = new ChildRecoveryLastUpdIdsRes();
		childRecoveryLastUpdIdsRes.setRunawayChildRecoveryIdsDto(runawayChildRecoveryIdsDto);
		log.debug("end method fetchChildRecoveryLastUpdate in RunawayMissingChildServiceImpl");
		return childRecoveryLastUpdIdsRes;
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public MissingChildRes fetchMissingChild(Long idChldMsngDtl) {
		log.debug("Start method fetchMissingChild in RunawayMissingChildServiceImpl");
		MissingChildDetailDto msngChildDetailDto = runawayMsngDao.fetchMissingChildDetail(idChldMsngDtl,
				0L);

		MissingChildRes missingChildRes = new MissingChildRes();
		missingChildRes.setMsngChildDetailDto(msngChildDetailDto);
		log.debug("end method fetchMissingChild in RunawayMissingChildServiceImpl");
		return missingChildRes;
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RunawayMsngRcvryRes fetchDetailForValidation(Long idPerson, Long idCase) {
		log.debug("Start method fetchDetailForValidation in RunawayMissingChildServiceImpl");

		RunawayMsngRcvryDto runawayMsngRcvryDto = runawayMsngDao.fetchDetailForValidation(idPerson, idCase);

		RunawayMsngRcvryRes runawayMsngRcvryRes = new RunawayMsngRcvryRes();
		runawayMsngRcvryRes.setRunawayMsngRcvryDto(runawayMsngRcvryDto);
		log.debug("end method fetchDetailForValidation in RunawayMissingChildServiceImpl");
		return runawayMsngRcvryRes;
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RunawayMsngDtRemovalRes fetchDtRemoval(Long idStage) {
		log.debug("Start method fetchDtRemoval in RunawayMissingChildServiceImpl");

		RunawayMsngDtRemovalDto runawayMsngDtRemovalDto = runawayMsngDao.fetchDtRemoval(idStage);

		RunawayMsngDtRemovalRes runawayMsngDtRemovalRes = new RunawayMsngDtRemovalRes();
		runawayMsngDtRemovalRes.setRunawayMsngDtRemovalDto(runawayMsngDtRemovalDto);
		log.debug("end method fetchDtRemoval in RunawayMissingChildServiceImpl");
		return runawayMsngDtRemovalRes;
	}

}
