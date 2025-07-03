package us.tx.state.dfps.service.ppm.serviceimpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import oracle.jdbc.proxy.annotation.Post;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.PostEventPersonDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.common.dto.TodoCommonFunctionDto;
import us.tx.state.dfps.common.dto.TodoCommonFunctionInputDto;
import us.tx.state.dfps.service.admin.dto.StageTaskInDto;
import us.tx.state.dfps.service.admin.dto.TodoCreateInDto;
import us.tx.state.dfps.service.admin.dto.TodoCreateOutDto;
import us.tx.state.dfps.service.admin.service.StageEventStatusCommonService;
import us.tx.state.dfps.service.childplan.dao.ChildServicePlanFormDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.request.PPMInfoReq;
import us.tx.state.dfps.service.common.request.PostEventReq;
import us.tx.state.dfps.service.common.response.PPMInfoRes;
import us.tx.state.dfps.service.common.response.PostEventRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.commontodofunction.service.CommonToDoFunctionService;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.forms.dto.PptDetailsOutDto;
import us.tx.state.dfps.service.person.dto.PPTParticipantDto;
import us.tx.state.dfps.service.ppm.dao.PPMDao;
import us.tx.state.dfps.service.ppm.dto.PPMInfoDto;
import us.tx.state.dfps.service.ppm.dto.PermPlanningInfoDto;
import us.tx.state.dfps.service.ppm.service.PPMInfoService;
import us.tx.state.dfps.service.servicedelivery.dto.ServiceDeliveryRtrvDtlsInDto;
import us.tx.state.dfps.service.servicedelivery.dto.ServiceDeliveryRtrvDtlsOutDto;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.service.TodoCommonFunctionService;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION Class Description: This
 * class is used to call the DAO to populate PPM Information parameters and
 * return back to the controller Sept 20, 2018 - 12:44:36 PM © 2018 Texas
 * Department of Family and Protective Services
 * 
 */
@Service
@Transactional
public class PPMInfoServiceImpl implements PPMInfoService {

	private static final Logger log = Logger.getLogger(PPMInfoServiceImpl.class);

	@Autowired
	private PPMDao ppmDao;

	@Autowired
	private StageEventStatusCommonService stageEventStatusCommonService;

	@Autowired
	private TodoCommonFunctionService todoCommonFunction;

	@Autowired
	WorkLoadDao workLoadDao;

	@Autowired
	EventService eventService;

	@Autowired
	CommonToDoFunctionService commonToDoFunctionService;

	@Autowired
	ChildServicePlanFormDao childServicePlanFormDao;

	@Autowired
	EventDao eventDao;

	private static final String START_LINE = "A Permanency Planning Meeting is scheduled on ";
	private static final String MIDDLE_LINE = " at ";
	private static final String LAST_LINE = ".";

	/**
	 * Method Name: getParticipants Method Description: Fetch PPM participants
	 *
	 * @param idEvent
	 * @return ppmInfoDto
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PPMInfoDto getParticipants(Long idEvent) {
		PPMInfoDto ppmInfoDto = new PPMInfoDto();
		if (!ObjectUtils.isEmpty(idEvent)) {
			List<PPTParticipantDto> participantList = ppmDao.getParticipantList(idEvent);
			ppmInfoDto.setPptParticipantList(participantList);
		}
		return ppmInfoDto;
	}

	/**
	 * Method Name: getPPMInfo Method Description: Fetch meeting
	 * information/location and PPM participants
	 *
	 * @param idEvent
	 * @return ppmInfoDto
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PPMInfoDto getPPMInfo(Long idEvent) {
		PPMInfoDto ppmInfoDto = new PPMInfoDto();
		if (!ObjectUtils.isEmpty(idEvent)) {
			PptDetailsOutDto pptDetailsOut = ppmDao.getPPTDetails(idEvent);
			ppmInfoDto.setPptDetailsOut(pptDetailsOut);
			List<PPTParticipantDto> participantList = ppmDao.getParticipantList(idEvent);
			ppmInfoDto.setPptParticipantList(participantList);
		}
		return ppmInfoDto;
	}

	/**
	 * Method Name: persistPPM Method Description: saves PPM information
	 * 
	 * @param ppmInfoDto
	 * @return Boolean
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PptDetailsOutDto savePPM(PPMInfoDto ppmInfoDto) {
		PptDetailsOutDto pptDetailsOutDto = null;
		PptDetailsOutDto pptDetails = null;
		if (!ObjectUtils.isEmpty(ppmInfoDto)) {
			pptDetailsOutDto = ppmInfoDto.getPptDetailsOut();
		}
		if (!ObjectUtils.isEmpty(pptDetailsOutDto)) {
			pptDetails = ppmDao.saveOrUpdatePPM(pptDetailsOutDto);
		}
		return pptDetails;
	}

	/**
	 * Method Name: deletePPTParticipant Method Description:deletes PPT
	 * Participant
	 * 
	 * @param ppmInfoDto
	 * @return Boolean
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean deletePPTParticipant(PPMInfoDto ppmInfoDto) {
		Boolean participantDeleted = Boolean.FALSE;
		PPTParticipantDto participant = null;
		if (!ObjectUtils.isEmpty(ppmInfoDto)) {
			participant = ppmInfoDto.getPptParticipant();
		}
		Long idPptPart = null;
		if (!ObjectUtils.isEmpty(participant)) {
			idPptPart = participant.getIdPptPart();
		}
		if (!ObjectUtils.isEmpty(idPptPart)) {
			participantDeleted = ppmDao.deletePPTParticipant(idPptPart);
		}
		return participantDeleted;
	}

	/**
	 * Method Name: getPPTParticipant Method Description: Service Method to
	 * fetch the saved Participant Details.
	 * 
	 * @param idPPTParticipant
	 * @return pptParticipant
	 */
	@Override
	public PPTParticipantDto getPPTParticipant(Long idPPTParticipant) {
		log.info("getPPTParticipant Method of PPMInfoServiceImpl : Execution Started");
		// Call the Dao Layer with the ID.
		PPTParticipantDto pptParticipant = ppmDao.getParticipantData(idPPTParticipant);
		log.info("getPPTParticipant Method of PPMInfoServiceImpl : Returning Participant Data");
		return pptParticipant;
	}

	/**
	 * Method Name:getParticipantList Method description: This method fetches
	 * entire row from table (PPT_PARTICIPANT) based on the ID_EVENT passed. DAM
	 * Name: CSUB27S or EJB(getPPTParticipantList) Both query is same
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PPTParticipantDto> getParticipantList(PPMInfoReq ppmInfoReq) {
		return ppmDao.getParticipantList(ppmInfoReq.getIdEvent());
	}

	/**
	 * Method Name: fetchppmInfo Method Description: Fetch meeting
	 * information/location and PPM participants
	 *
	 * @param idEvent
	 * @return ppmInfoDto
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PPMInfoDto fetchppmInfo(Long idEvent) {
		PPMInfoDto ppmInfoDto = new PPMInfoDto();
		PermPlanningInfoDto permPlanningInfoDto = new PermPlanningInfoDto();
		PptDetailsOutDto pPtDetailsOutDto = new PptDetailsOutDto();
		if (!ObjectUtils.isEmpty(idEvent)) {

			/**
			 * Dam Name: CCMN45D This Dam performs a retrive operation from
			 * EVENT table by passing the idEvent.
			 */
			EventDto eventDto = eventService.getEvent(idEvent);
			if (!ObjectUtils.isEmpty(eventDto)) {
				BeanUtils.copyProperties(eventDto, permPlanningInfoDto);
			}
			ppmInfoDto.setPermPlanningInfoDto(permPlanningInfoDto);

			/** If the CdEventStatus is not new, the retrieve the PPT Table. */

			if (!ServiceConstants.SVC_CD_EVENT_STATUS_NEW
					.equals(ppmInfoDto.getPermPlanningInfoDto().getCdEventStatus())) {

				/**
				 * DAM Name: CCMN14D This DAM will perform a full row retrieval
				 * from PPT when the host input variable ID event matches an
				 * element in the table.
				 */
				pPtDetailsOutDto = ppmDao.populatePptAddress(idEvent);
				if (!ObjectUtils.isEmpty(pPtDetailsOutDto)) {
					ppmInfoDto.setPptDetailsOut(pPtDetailsOutDto);
				}

				/**
				 * DAM Name: CSYS06D Method Name: csys06dQUERYdam Method
				 * Description: This methid will retrive the data from the
				 * PPT_NARR based on the ID_EVENT
				 */
				ServiceDeliveryRtrvDtlsInDto serviceDeliveryRtrvDtlsInDto = new ServiceDeliveryRtrvDtlsInDto();
				serviceDeliveryRtrvDtlsInDto.setIdEvent(idEvent);
				ServiceDeliveryRtrvDtlsOutDto serviceDeliveryRtrvDtlsOutDto = ppmDao
						.getPPTNarrDetails(serviceDeliveryRtrvDtlsInDto);
				ppmInfoDto.setServiceDeliveryRtrvDtlsOutDto(serviceDeliveryRtrvDtlsOutDto);
			}
		}
		List<PPTParticipantDto> participantList = ppmDao.getParticipantList(idEvent);
		ppmInfoDto.setPptParticipantList(participantList);
		return ppmInfoDto;
	}

	/**
	 * Method Name: insertOrUpdatePPTInfo Method Description: This Method
	 * Performs AUD operations on PPT table based on the IdPptEvent
	 * 
	 * TUXEDO Name: CSUB50
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PPMInfoRes savePPMInfo(PPMInfoReq ppmInfoReq) {
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();

		PPMInfoDto pPMInfoDto = ppmInfoReq.getPpmInfoDto();
		PptDetailsOutDto pptDetailsOut = !ObjectUtils.isEmpty(pPMInfoDto.getPptDetailsOut())
				? pPMInfoDto.getPptDetailsOut() : new PptDetailsOutDto();

		// This flag is set for the insertOrUpdatePPTInfo (CAUD09D)
		if (ObjectUtils.isEmpty(pptDetailsOut.getIdEvent())) {
			pptDetailsOut.setPptFlag(ServiceConstants.TRUEVAL);
		} else {
			pptDetailsOut.setPptFlag(ServiceConstants.FALSEVAL);
		}

		PPMInfoRes pPMInfoRes = new PPMInfoRes(); // Responce object for service
													// responce
		StageTaskInDto stageTaskInDto = new StageTaskInDto(); // CCMN01
		PostEventRes postEventRes = new PostEventRes(); // CCMN01
		PPMInfoReq pPMInfoReq = new PPMInfoReq(); // CAUD50
		ServiceDeliveryRtrvDtlsOutDto serviceDeliveryRtrvDtlsOutDto = new ServiceDeliveryRtrvDtlsOutDto();
		TodoCreateInDto todoCreateInDto = new TodoCreateInDto();
		Long idPerson = 0L; // Initillizating idPerson for CINV51D

		if (ObjectUtils.isEmpty(pptDetailsOut.getIdEvent())) {
			serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		} else {
			serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		}

		if (!ObjectUtils.isEmpty(pptDetailsOut.getCdTask())) {
			stageTaskInDto.setCdTask(pptDetailsOut.getCdTask());
		}

		if (!ObjectUtils.isEmpty(pptDetailsOut.getIdStage())) {
			stageTaskInDto.setIdStage(pptDetailsOut.getIdStage());
		}

		String responceCCMN = stageEventStatusCommonService.checkStageEventStatus(stageTaskInDto); // CCMN01

		/** Analyzeing the responce from above common function */
		String returnValue = ServiceConstants.NULL_STRING;
		switch (responceCCMN) {

		case ServiceConstants.ARC_SUCCESS:
			returnValue = ServiceConstants.SUCCESS;
			break;

		case ServiceConstants.MSG_SYS_STAGE_CLOSED:
			returnValue = ServiceConstants.FAIL;
			break;

		case ServiceConstants.MSG_SYS_EVENT_STS_MSMTCH:
			if (ServiceConstants.SUB_PPT_TASK.equals(pptDetailsOut.getCdTask())) {
				returnValue = ServiceConstants.SUCCESS;
			} else {
				returnValue = ServiceConstants.FAIL;
			}
			break;

		case ServiceConstants.MSG_SYS_MULT_INST:
			returnValue = ServiceConstants.FAIL;
			break;

		default:
			returnValue = ServiceConstants.FAIL;
			break;
		}

		if ((returnValue.equals(ServiceConstants.SUCCESS) && (ServiceConstants.ZERO.equals(pptDetailsOut.getIdPerson())
				|| null == (pptDetailsOut.getIdPerson())))) {
			/**
			 * Populate Common Function Input Structure CINV51D with idStage
			 * CdStagePersRole as PRIMARY_CHILD
			 */

			idPerson = workLoadDao.getStagePersonIdByRole(pptDetailsOut.getIdStage(), ServiceConstants.PRIMARY_CHILD); // Call
																														// CINV51D
			pPMInfoRes.setIdPerson(idPerson);
			if (TypeConvUtil.isNullOrEmpty(idPerson)) {
				returnValue = ServiceConstants.FAIL;
			}
		}

		if ((!TypeConvUtil.isNullOrEmpty(idPerson)) && (returnValue.equals(ServiceConstants.SUCCESS))) {
			// Need to populate the request object postEventReq from save req
			PostEventReq postEventReq = new PostEventReq();
			if (ObjectUtils.isEmpty(pptDetailsOut.getIdEvent())) {
				postEventReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
			} else {
				postEventReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
			}
			//artf128774 : If the status of the existing PPT event is New, add the child who is Self from the stage to the EventPersonLink table
			Event existingEvent = null;
			if (!ObjectUtils.isEmpty(pptDetailsOut.getIdEvent())) {
				existingEvent = eventDao.getEventById(pptDetailsOut.getIdEvent());
			}
			if (ObjectUtils.isEmpty(pptDetailsOut.getIdEvent())
					|| ServiceConstants.EVENTSTATUS_NEW.equals(existingEvent.getCdEventStatus())) {
				List<PostEventPersonDto> eventPersonlist = new ArrayList<>();
				PostEventPersonDto eventPerson = new PostEventPersonDto();
				eventPerson.setCdScrDataAction(ServiceConstants.REQ_FUNC_CD_ADD);
				eventPerson.setIdPerson(idPerson);
				eventPersonlist.add(eventPerson);
				postEventReq.setPostEventPersonList(eventPersonlist);
			}
			postEventReq.setDtDtEventOccurred(new Date());
			postEventReq.setUlIdEvent(pptDetailsOut.getIdEvent());
			postEventReq.setUlIdStage(pptDetailsOut.getIdStage());
			postEventReq.setUlIdPerson(pptDetailsOut.getIdPerson());
			postEventReq.setSzCdTask(pptDetailsOut.getCdTask());
			postEventReq.setSzCdEventStatus(pptDetailsOut.getCdEventStatus());
			postEventReq.setSzCdEventType(pptDetailsOut.getCdEventType());
			postEventReq.setSzTxtEventDescr(pptDetailsOut.getTxtEventDescr());
			postEventReq.setTsLastUpdate(new Date());
			postEventReq.setUlIdPerson(pPMInfoDto.getIdUser());
			postEventRes = eventService.postEvent(postEventReq); // CCMN01

			/**
			 * Analyze return code TO_DO And copy the id_event to the ressponce.
			 */
			pPMInfoRes.setIdEvent(postEventRes.getUlIdEvent());
			if (!ObjectUtils.isEmpty(postEventRes)) {
				serviceDeliveryRtrvDtlsOutDto.setIdEvent(postEventRes.getUlIdEvent());

				/**
				 ** If the Event Status is complete or the Id Event is not new
				 * update the To_do associated with that event
				 */
				if ((ServiceConstants.STATUS_COMPLETE.equals(pptDetailsOut.getCdEventStatus()))
						|| (!ObjectUtils.isEmpty(pptDetailsOut.getIdEvent()))) {

					/**
					 ** Set the Requested Fucntion Code to Update so that the
					 * existing Todos are updated
					 */
					serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
					pPMInfoReq.setIdEvent(pptDetailsOut.getIdEvent());
					pPMInfoReq.setDtTodoCompleted(pptDetailsOut.getDtTodoCompleted());
					serviceDeliveryRtrvDtlsOutDto = ppmDao.updateToDoTable(pPMInfoReq, serviceReqHeaderDto); // CAUD50
				}
				// if (!ObjectUtils.isEmpty(serviceDeliveryRtrvDtlsOutDto)) {

				/** Populate DAM CAUD09 input structure */

				if (ObjectUtils.isEmpty(pptDetailsOut.getIdPptEvent())) {
					// copyin gidEvent from above responce
					pptDetailsOut.setIdPptEvent(postEventRes.getUlIdEvent());
				} else {
					pptDetailsOut.setIdPptEvent(pptDetailsOut.getIdPptEvent());
				}

				if (pptDetailsOut.getPptFlag()) {
					serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
				} else {
					serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
				}

				// Setting Zip-Extension
				if (ObjectUtils.isEmpty(pptDetailsOut.getExpandedZip())) {
					pptDetailsOut.setAddrPptZip(pptDetailsOut.getAddrPptZip());
				} else {
					StringBuilder zipExtension = new StringBuilder();
					zipExtension.append(pptDetailsOut.getAddrPptZip()).append("-")
							.append(pptDetailsOut.getExpandedZip());
					pptDetailsOut.setAddrPptZip(zipExtension.toString());
				}

				// Setting Date and time
				try {
					pptDetailsOut.setDtPptDate(
							DateUtils.getTimestamp(pptDetailsOut.getDtPptDate(), pptDetailsOut.getStartTime()));
				} catch (ParseException e) {
					ServiceLayerException serviceLayerException = new ServiceLayerException(e.toString());
					serviceLayerException.initCause(e);
					throw serviceLayerException;
				}

				serviceDeliveryRtrvDtlsOutDto = ppmDao.insertOrUpdatePPTInfo(pptDetailsOut, serviceReqHeaderDto); // CAUD09D
				// }
			}

		}

		/** Condition check for ADO 8680 */
		if (ServiceConstants.INDICATOR_YES.equals(pptDetailsOut.getSysIndDtPptCompFlld())
				&& (ServiceConstants.ADO_PPT_TASK.equals(pptDetailsOut.getCdTask()))) {
			/** Call CCMN01 */
			PostEventReq postEventReq = new PostEventReq();
			serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
			postEventReq.setDtDtEventOccurred(new Date());
			postEventReq.setSzCdEventStatus(ServiceConstants.STATUS_NEW);
			//postEventReq.setUlIdPerson(pptDetailsOut.getIdPerson());
			postEventReq.setUlIdPerson(pPMInfoDto.getIdUser());
			postEventReq.setSzTxtEventDescr(ServiceConstants.PPR);
			postEventReq.setUlIdEvent(pptDetailsOut.getIdEvent());
			postEventReq.setUlIdStage(pptDetailsOut.getIdStage());
			postEventReq.setSzCdTask(pptDetailsOut.getCdTask());
			postEventReq.setSzCdEventType(pptDetailsOut.getCdEventType());

			postEventRes = eventService.postEvent(postEventReq); // CCMN01

			if (!ObjectUtils.isEmpty(postEventRes)) {

				todoCreateInDto.setSysCdTodoCf(ServiceConstants.TODO_CODE1);
				todoCreateInDto.setDtSysDtTodoCfDueFrom(pptDetailsOut.getDtPptDate());
				todoCreateInDto.setSysIdTodoCfPersCrea(pptDetailsOut.getSysIdTodoCfPersCrea());
				todoCreateInDto.setSysIdTodoCfStage(pptDetailsOut.getSysIdTodoCfPersCrea());
				todoCreateInDto.setSysIdTodoCfEvent(postEventRes.getUlIdEvent());
				TodoCreateOutDto todoCreateOutDto = commonToDoFunctionService.TodoCommonFunction(todoCreateInDto);

				if (!ObjectUtils.isEmpty(todoCreateOutDto)) {
					todoCreateInDto.setSysCdTodoCf(ServiceConstants.TODO_CODE2);
					todoCreateInDto.setDtSysDtTodoCfDueFrom(pptDetailsOut.getDtPptDate());
					todoCreateInDto.setSysIdTodoCfPersCrea(pptDetailsOut.getSysIdTodoCfPersCrea());
					todoCreateInDto.setSysIdTodoCfStage(pptDetailsOut.getSysIdTodoCfPersCrea());
					todoCreateInDto.setSysIdTodoCfEvent(postEventRes.getUlIdEvent());
					commonToDoFunctionService.TodoCommonFunction(todoCreateInDto);
				}
			}
		}
		pPMInfoRes.setIdEvent(postEventRes.getUlIdEvent());
		return pPMInfoRes;

	}

	/**
	 * Method Name: pptParticipantAUD Method Description: Service for doing AUD
	 * operations on PPT Participant Table. Equivalent Tux: CSUB28S.
	 * 
	 * @param pptParticipantDto
	 * @param reqFuncCd
	 * @return ppmInfoRes
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PPMInfoRes pptParticipantAUD(PPTParticipantDto pptParticipantDto, String reqFuncCd) {
		log.info("pptParticipantAUD Method of PPMInfoServiceImpl : Execution Started");
		PPMInfoRes ppmInfoRes = new PPMInfoRes();
		// If ADD or update check if duplicate exists.
		if (ServiceConstants.ADD.equals(reqFuncCd) || ServiceConstants.UPDATE.equals(reqFuncCd)) {
			if (checkDuplicateParticipant(pptParticipantDto)) {
				ErrorDto error = new ErrorDto();
				error.setErrorCode(ServiceConstants.MSG_DUPLICATE_RECORD);
				ppmInfoRes.setErrorDto(error);
				log.info("pptParticipantAUD Method of PPMInfoServiceImpl : Returning with Duplicate Error.");
				return ppmInfoRes;
			}
		}
		// Call checkEventStageStatus before doing any AUD function.
		StageTaskInDto stageTaskInDto = new StageTaskInDto();
		stageTaskInDto.setIdStage(pptParticipantDto.getIdStage());
		stageTaskInDto.setCdReqFunction(reqFuncCd);
		// Set the CdTask, If SUB task code is 3180 else if ADO task code is
		// 8680
		switch (pptParticipantDto.getCdStage()) {
		case ServiceConstants.SUB_CARE_STG:
			stageTaskInDto.setCdTask(ServiceConstants.TASK_3180);
			break;
		case ServiceConstants.ADOPTION_STG:
			stageTaskInDto.setCdTask(ServiceConstants.TASK_8680);
			break;
		default:
			break;
		}
		if (ServiceConstants.ARC_SUCCESS
				.equalsIgnoreCase(stageEventStatusCommonService.checkStageEventStatus(stageTaskInDto))) {
			switch (reqFuncCd) {
			case ServiceConstants.ADD:
				// If Add Case Add the participant.
				ppmDao.savePPTParticipant(pptParticipantDto);
				break;
			case ServiceConstants.UPDATE:
				ppmDao.savePPTParticipant(pptParticipantDto);
				break;
			case ServiceConstants.DELETE:
				ppmDao.deletePPTParticipant(pptParticipantDto.getIdPptPart());
				break;
			default:
				break;
			}
			// Check if Ind Send Alert is set to Y. Create the Todo.
			if (ServiceConstants.Y.equals(pptParticipantDto.getIndSendAlert())) {
				TodoCommonFunctionInputDto todoInput = new TodoCommonFunctionInputDto();
				todoInput.setTodoCommonFunctionDto(setCommonTodoForSendAlert(pptParticipantDto));
				//[artf205297] Defect: 18849 -  when this flag is not set, TodoCommonFunction when creating alerts, clears DT_TODO_DUE
				todoInput.getTodoCommonFunctionDto().setDayCareAbsInd(Boolean.TRUE);
				todoCommonFunction.TodoCommonFunction(todoInput);
			}
		}
		log.info("pptParticipantAUD Method of PPMInfoServiceImpl : Returning Response.");
		return ppmInfoRes;
	}

	/**
	 * Method Name: checkDuplicateParticipant Method Description: Method checks
	 * if the Duplicate record of the Participant entered for Current Event
	 * exists.
	 * 
	 * @param pptParcipantDto
	 * @return dupExist
	 */
	private Boolean checkDuplicateParticipant(PPTParticipantDto pptParcipantDto) {
		// Fetch the list of Participant for the Event.
		boolean dupExist = false;
		List<PPTParticipantDto> pptParticipantLst = ppmDao.getParticipantList(pptParcipantDto.getIdEvent());
		final Long idCrntParticipant = pptParcipantDto.getIdPptPart();
		final Long idNewPerson = pptParcipantDto.getIdPerson();
		final String nmPersonFull = pptParcipantDto.getNmPptPartFull();
		final String txtRelationship = pptParcipantDto.getSdsPptPartRelationship();
		if (!ObjectUtils.isEmpty(pptParticipantLst)) {
			dupExist = pptParticipantLst.stream()
					.anyMatch(participant -> (((CodesConstant.CPARTYPE_PIC.equals(participant.getCdPptPartType())
							|| CodesConstant.CPARTYPE_STA.equals(participant.getCdPptPartType()))
							&& (participant.getIdPerson().equals(idNewPerson)
									&& !participant.getIdPptPart().equals(idCrntParticipant)))
							|| (CodesConstant.CPARTYPE_OTH.equals(participant.getCdPptPartType())
									&& nmPersonFull.equalsIgnoreCase(participant.getNmPptPartFull()) && txtRelationship
											.equalsIgnoreCase(participant.getSdsPptPartRelationship())
									&& !participant.getIdPptPart().equals(idCrntParticipant))));
		}
		return dupExist;
	}

	/**
	 * Method Name: setCommonTodoForSendAlert Method Description: Method sets
	 * the Common Todo Dto for Creating todo on Send Alert.
	 * 
	 * @param pptParticipantDto
	 * @return
	 */
	private TodoCommonFunctionDto setCommonTodoForSendAlert(PPTParticipantDto pptParticipantDto) {
		TodoCommonFunctionDto todoCommonFunctionDto = new TodoCommonFunctionDto();
		todoCommonFunctionDto.setSysCdTodoCf(ServiceConstants.TODO_INFO_27_CODE);
		todoCommonFunctionDto.setSysIdTodoCfPersCrea(pptParticipantDto.getIdTodoPersCreated());
		todoCommonFunctionDto.setSysIdTodoCfStage(pptParticipantDto.getIdStage());
		todoCommonFunctionDto.setTsLastUpdate(new Date());
		//[artf205297] Defect: 18339 -  permanency planning alerts not showing
		todoCommonFunctionDto.setDtSysDtTodoCfDueFrom(new Date());
		todoCommonFunctionDto.setSysIdTodoCfPersAssgn(pptParticipantDto.getIdPerson());
		String txtTodoDesc = START_LINE + DateUtils.stringDt(pptParticipantDto.getDtPPTMeeting()) + MIDDLE_LINE
				+ DateUtils.getTime(pptParticipantDto.getDtPPTMeeting()) + LAST_LINE;
		todoCommonFunctionDto.setSysTxtTodoCfDesc(txtTodoDesc);
		return todoCommonFunctionDto;
	}

}
