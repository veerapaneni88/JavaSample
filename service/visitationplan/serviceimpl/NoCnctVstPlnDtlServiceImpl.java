package us.tx.state.dfps.service.visitationplan.serviceimpl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonOutDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.casepackage.dao.PcspListPlacmtDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.NoCnctVstPlnDtlReq;
import us.tx.state.dfps.service.common.request.VisitationPlanDtlReq;
import us.tx.state.dfps.service.common.response.NoCnctVstPlnDtlRes;
import us.tx.state.dfps.service.common.response.VisitationPlanDtlRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.FamilyPlanVisitationPlanPrefillData;
import us.tx.state.dfps.service.forms.util.NoContactVisitationPlanPrefillData;
import us.tx.state.dfps.service.visitationplan.dao.NoCnctVstPlnDtlDao;
import us.tx.state.dfps.service.visitationplan.service.NoCnctVstPlnDtlService;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.visitationplan.dto.NoCnctVstPlnDetailDto;
import us.tx.state.dfps.visitationplan.dto.VisitationPlanDetailDto;
import us.tx.state.dfps.visitationplan.dto.VstPlanPartcpntDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:this class
 * is for No Contact Visitation Plan Detail service Sep 20, 2018- 12:08:39 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class NoCnctVstPlnDtlServiceImpl implements NoCnctVstPlnDtlService {

	public NoCnctVstPlnDtlServiceImpl() {
		// default constructor
	}

	private static final String VIS = "VIS";
	private static final String VISITATION_PLAN = "Visitation Plan";
	private static final String NCV = "NCV";
	private static final String TASK_CODE_VISITATION_PLAN_NO_CONTACT = "4391";
	private static final String TASK_CODE_VISITATION_PLAN = "4392";
	private static final String NO_CONTACT_VISITATION_PLAN = "No Contact Visitation Plan";

	@Autowired
	NoCnctVstPlnDtlDao noCnctVstPlnDtlDao;

	@Autowired
	PostEventService postEventService;

	@Autowired
	PcspListPlacmtDao pcspDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	NoContactVisitationPlanPrefillData noContactVisitationPlan;

	@Autowired
	FamilyPlanVisitationPlanPrefillData familyPlanVisitationPlan;

	@Autowired
	ApprovalCommonService approvalCommonService;

	@Autowired
	StageDao stageDao;

	private static final Logger log = Logger.getLogger(NoCnctVstPlnDtlServiceImpl.class);

	/**
	 * Method Name: retrieveNoContactVisitationPlnDetails Method Description: to
	 * retrieve no contact visitation plan details
	 * 
	 * @param idStage
	 * @param idEvent
	 * @return NoCnctVstPlnDtlRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public NoCnctVstPlnDtlRes retrieveNoContactVisitationPlnDetails(Long idStage, Long idEvent) {

		NoCnctVstPlnDtlRes noCnctVstPlnDtlRes = new NoCnctVstPlnDtlRes();
		NoCnctVstPlnDetailDto noCnctVstPlnDetailDto = new NoCnctVstPlnDetailDto();
		if (!TypeConvUtil.isNullOrEmpty(idEvent)) {
			noCnctVstPlnDetailDto = noCnctVstPlnDtlDao.reteriveNoContactVisitPlan(idEvent, idStage);
		}
		if (ObjectUtils.isEmpty(noCnctVstPlnDetailDto)) {
			noCnctVstPlnDetailDto = new NoCnctVstPlnDetailDto();
		}

		// display cause number
		if (!ObjectUtils.isEmpty(noCnctVstPlnDetailDto)
				&& !ObjectUtils.isEmpty(noCnctVstPlnDetailDto.getNoCnctVstPlanPartcpntList())) {
			List<VstPlanPartcpntDto> vstPlanPartcpntList = noCnctVstPlnDetailDto.getNoCnctVstPlanPartcpntList();
			StringBuilder causeNumber = new StringBuilder();
			for (VstPlanPartcpntDto vstPlanPartcpntDto : vstPlanPartcpntList) {
				// display cause number
				if (!ObjectUtils.isEmpty(vstPlanPartcpntDto.getTxtCauseNbr())) {
					if (causeNumber.length() > 0) {
						causeNumber = causeNumber.append(",").append(vstPlanPartcpntDto.getTxtCauseNbr().trim());
					} else {
						causeNumber = causeNumber.append(vstPlanPartcpntDto.getTxtCauseNbr().trim());
					}
				}

			}
			noCnctVstPlnDetailDto.setTxtCauseNbr(causeNumber.toString());
		}
		noCnctVstPlnDtlRes.setNoCnctVstPlnDetailDto(noCnctVstPlnDetailDto);
		boolean hasBeenSubmitted = pcspDao.setHasBeenSubmittedForApprovalCps(idEvent);
		noCnctVstPlnDtlRes.setHasSubmittedForApproval(hasBeenSubmitted);
		log.debug("EXIT THE RETRIEVE NO CONTACT VISITATION PLAN");
		return noCnctVstPlnDtlRes;
	}

	/**
	 * Method Name: retrieveNoContactVisitationPlnFormDetails Method
	 * Description: To retrieve no contact visitation plan details for forms
	 * 
	 * @param idStage
	 * @param idEvent
	 * @param idCase
	 * @param nmCase
	 * @return PreFillDataServiceDto
	 */
	@Override
	public PreFillDataServiceDto retrieveNoContactVisitationPlnFormDetails(Long idStage, Long idEvent, Long idCase,
			String nmCase) {

		NoCnctVstPlnDtlRes noCnctVstPlnDtlRes = retrieveNoContactVisitationPlnDetails(idStage, idEvent);
		StageDto stageDto = getStageDtl(idStage);
		noCnctVstPlnDtlRes.getNoCnctVstPlnDetailDto().setIdCase(stageDto.getIdCase());

		noCnctVstPlnDtlRes.getNoCnctVstPlnDetailDto().setNmCase(stageDto.getNmCase());

		return noContactVisitationPlan.returnPrefillData(noCnctVstPlnDtlRes.getNoCnctVstPlnDetailDto());

	}

	private StageDto getStageDtl(Long idStage)

	{

		return stageDao.getStageById(idStage);

	}

	/**
	 * Method Name: saveNoContactVisitationPlnDetails Method Description: To
	 * save no contact visitation plan details
	 * 
	 * @param noCnctVstPlnDtlReq
	 * @return NoCnctVstPlnDtlRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false)
	public NoCnctVstPlnDtlRes saveNoContactVisitationPlnDetails(NoCnctVstPlnDtlReq noCnctVstPlnDtlReq) {
		return save(noCnctVstPlnDtlReq);
	}

	/**
	 * Method Name: save Method Description: save no contact visitation plan
	 * details
	 * 
	 * @param noCnctVstPlnDtlReq
	 * @return NoCnctVstPlnDtlRes
	 */
	private NoCnctVstPlnDtlRes save(NoCnctVstPlnDtlReq noCnctVstPlnDtlReq) {
		NoCnctVstPlnDtlRes noCnctVstPlnDtlRes = new NoCnctVstPlnDtlRes();
		if (!ObjectUtils.isEmpty(noCnctVstPlnDtlReq.getNoCnctVstPlnDetailDto())) {
			VisitationPlanDtlRes visitationPlanDtlRes = null;
			/*
			 * Checking for the participant list whether it is duplicate or not
			 */
			if (!ObjectUtils.isEmpty(noCnctVstPlnDtlReq.getNoCnctVstPlnDetailDto().getNoCnctVstPlanPartcpntList())) {
				HashMap<VstPlanPartcpntDto, List<VstPlanPartcpntDto>> participantMap = new HashMap<>();
				noCnctVstPlnDtlReq.getNoCnctVstPlnDetailDto().getNoCnctVstPlanPartcpntList().stream()
						.forEach(participant -> {
							if (!ObjectUtils.isEmpty(participant)
									&& ServiceConstants.Y.equalsIgnoreCase(participant.getIndChildRmvd())) {
								/*
								 * populating a Map of sub care Child as Key and
								 * other participant as values.
								 */
								List<VstPlanPartcpntDto> listParticipantDto = noCnctVstPlnDtlReq
										.getNoCnctVstPlnDetailDto().getNoCnctVstPlanPartcpntList().stream()
										.filter(person -> !ObjectUtils.isEmpty(person.getIdPerson())
												&& !"Y".equalsIgnoreCase(person.getIndChildRmvd()))
										.collect(Collectors.toList());
								participantMap.put(participant, listParticipantDto);
							}
						});
				if (!ObjectUtils.isEmpty(participantMap)) {
					visitationPlanDtlRes = noCnctVstPlnDtlDao.visitationPlanExist(noCnctVstPlnDtlReq.getIdStage(),
							participantMap, noCnctVstPlnDtlReq.getNoCnctVstPlnDetailDto().getIdEvent());
				}

			}

			if (ObjectUtils.isEmpty(visitationPlanDtlRes) || ObjectUtils.isEmpty(visitationPlanDtlRes.getResult())
					|| ServiceConstants.NO.equalsIgnoreCase(visitationPlanDtlRes.getResult())) {
				// page mode is new insert new record else update the existing
				// record
				if (!ObjectUtils.isEmpty(noCnctVstPlnDtlReq.getNoCnctVstPlnDetailDto())) {
					if (ServiceConstants.NEW.equals(noCnctVstPlnDtlReq.getPageMode())
							&& TypeConvUtil.isNullOrEmpty(noCnctVstPlnDtlReq.getNoCnctVstPlnDetailDto().getIdEvent())) {
						poupulateSaveNoCntctVstPlnDtlsDomainObject(noCnctVstPlnDtlReq);

						Long idNoContactVisitationPlnDetails = noCnctVstPlnDtlDao
								.saveNoContactVisitationPlnDetails(noCnctVstPlnDtlReq.getNoCnctVstPlnDetailDto());

						NoCnctVstPlnDetailDto noCnctVstPlnDetailDto = new NoCnctVstPlnDetailDto();
						noCnctVstPlnDetailDto.setIdVisitPlanNoCntct(idNoContactVisitationPlnDetails);
						noCnctVstPlnDetailDto.setIdEvent(noCnctVstPlnDtlReq.getNoCnctVstPlnDetailDto().getIdEvent());
						noCnctVstPlnDtlRes.setNoCnctVstPlnDetailDto(noCnctVstPlnDetailDto);

					} else {
						noCnctVstPlnDtlReq.getNoCnctVstPlnDetailDto()
								.setIdLastUpdatePerson(Long.valueOf(noCnctVstPlnDtlReq.getUserId()));
						NoCnctVstPlnDetailDto noCnctVstPlnDetailDto = noCnctVstPlnDtlDao
								.updateNoContactVisitationPlnDetails(noCnctVstPlnDtlReq);
						noCnctVstPlnDtlRes.setNoCnctVstPlnDetailDto(noCnctVstPlnDetailDto);
					}
				}

				// find the event has a submitted for approval
				boolean hasBeenSubmitted = pcspDao
						.setHasBeenSubmittedForApprovalCps(noCnctVstPlnDtlReq.getNoCnctVstPlnDetailDto().getIdEvent());
				noCnctVstPlnDtlRes.setHasSubmittedForApproval(hasBeenSubmitted);
				// invalidate the approval when save is clicked on pend status
				if (!noCnctVstPlnDtlReq.isApprovalMode()
						&& ServiceConstants.PAGEMODE_MODIFY.equals(noCnctVstPlnDtlReq.getPageMode())
						&& ServiceConstants.PEND
								.equalsIgnoreCase(noCnctVstPlnDtlReq.getNoCnctVstPlnDetailDto().getEventStatus())) {
					ApprovalCommonInDto pInputMsg = new ApprovalCommonInDto();
					ApprovalCommonOutDto pOutputMsg = new ApprovalCommonOutDto();
					pInputMsg.setIdEvent(noCnctVstPlnDtlReq.getNoCnctVstPlnDetailDto().getIdEvent());
					pInputMsg.setCdEventStatus(ServiceConstants.EVENT_COMP);
					approvalCommonService.InvalidateAprvl(pInputMsg, pOutputMsg);
				}
			}
			log.debug("EXIT THE SAVE NO CONTACT VISITATION PLAN");
		}
		return noCnctVstPlnDtlRes;

	}

	/**
	 * Method Name: poupulateSaveNoCntctVstPlnDtlsDomainObject Method
	 * Description: to create event for save method
	 * 
	 * @param noCnctVstPlnDtlReq
	 */
	private void poupulateSaveNoCntctVstPlnDtlsDomainObject(NoCnctVstPlnDtlReq noCnctVstPlnDtlReq) {
		EventValueDto eventValueDto = new EventValueDto();
		eventValueDto.setIdCase(noCnctVstPlnDtlReq.getIdCase());
		eventValueDto.setIdStage(noCnctVstPlnDtlReq.getIdStage());
		String taskCode = TASK_CODE_VISITATION_PLAN_NO_CONTACT;
		eventValueDto.setCdEventTask(taskCode);
		eventValueDto.setIdEvent(0L);
		String eventDescription = NO_CONTACT_VISITATION_PLAN;
		eventValueDto.setEventDescr(eventDescription);
		eventValueDto.setCdEventType(NCV);
		eventValueDto.setIdPerson(Long.valueOf(noCnctVstPlnDtlReq.getUserId()));
		eventValueDto.setDtEventCreated(new Date());
		eventValueDto.setIdStage(noCnctVstPlnDtlReq.getIdStage());
		eventValueDto.setCdEventStatus(ServiceConstants.CEVTSTAT_PROC);
		Long idNoCnctVstPlnDtlReqEvent = postEventService.postEvent(eventValueDto, ServiceConstants.REQ_FUNC_CD_ADD,
				noCnctVstPlnDtlReq.getNoCnctVstPlnDetailDto().getIdCreatedPerson());
		noCnctVstPlnDtlReq.getNoCnctVstPlnDetailDto().setIdEvent(idNoCnctVstPlnDtlReqEvent);
		noCnctVstPlnDtlReq.getNoCnctVstPlnDetailDto().setIdCreatedPerson(Long.valueOf(noCnctVstPlnDtlReq.getUserId()));
		noCnctVstPlnDtlReq.getNoCnctVstPlnDetailDto()
				.setIdLastUpdatePerson(Long.valueOf(noCnctVstPlnDtlReq.getUserId()));
	}

	/**
	 * 
	 * Method Name: retrieveVisitationPlnDetail Method Description: This method
	 * is used to Save/Update the New/Existing visitation Plan.
	 * 
	 * @param idStage
	 * @param idEvent
	 * @param idCase
	 * @return VisitationPlanDtlRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public VisitationPlanDetailDto retrieveVisitationPlnDetail(Long idStage, Long idEvent, Long idCase) {
		VisitationPlanDetailDto visitationPlanDetailDto = new VisitationPlanDetailDto();
		/*
		 * Checking for the id event if exist then fetch the visitation plan
		 * from DB else return the newly created DTO to let user create the
		 * visitation plan.
		 */
		if (!TypeConvUtil.isNullOrEmpty(idEvent)) {
			visitationPlanDetailDto = noCnctVstPlnDtlDao.reteriveVisitationPlanDetail(idEvent, idStage);
			/*
			 * fetching and setting the event status of the visitation plan to
			 * the visitation plan DTO.
			 */
			visitationPlanDetailDto.setCdEventStatus(eventDao.getEventStatus(idEvent));
			/*
			 * Fetching the cause number of the participant who have a SUB
			 * stage.
			 */
			if (!ObjectUtils.isEmpty(visitationPlanDetailDto.getVisitPlanPartcpntList())) {
				List<VstPlanPartcpntDto> vstPlanPartcpntList = visitationPlanDetailDto.getVisitPlanPartcpntList();
				StringBuilder causeNumber = new StringBuilder();
				for (VstPlanPartcpntDto vstPlanPartcpntDto : vstPlanPartcpntList) {
					if (!ObjectUtils.isEmpty(vstPlanPartcpntDto.getTxtCauseNbr())
							&& ServiceConstants.Y.equalsIgnoreCase(vstPlanPartcpntDto.getIndChildRmvd())) {
						if (causeNumber.length() > 0) {
							causeNumber = causeNumber.append(",").append(vstPlanPartcpntDto.getTxtCauseNbr().trim());
						} else {
							causeNumber = causeNumber.append(vstPlanPartcpntDto.getTxtCauseNbr().trim());
						}
					}

				}
				visitationPlanDetailDto.setTxtCauseNbr(causeNumber.toString());
			}
		}
		return visitationPlanDetailDto;
	}

	/**
	 * 
	 * Method Name: retrieveVisitationPlnDetail Method Description: This method
	 * is used to Save/Update the New/Existing visitation Plan.
	 * 
	 * @param idStage
	 * @param idEvent
	 * @param idCase
	 * @return VisitationPlanDtlRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto retrieveVisitationPlnFormDetail(Long idStage, Long idEvent, Long idCase,
			String nmCase) {
		VisitationPlanDetailDto visitationPlanDetailDto = retrieveVisitationPlnDetail(idStage, idEvent, idCase);

		StageDto stageDto = getStageDtl(idStage);

		visitationPlanDetailDto.setIdCase(stageDto.getIdCase());

		visitationPlanDetailDto.setNmCase(stageDto.getNmCase());

		return familyPlanVisitationPlan.returnPrefillData(visitationPlanDetailDto);
	}

	/**
	 * 
	 * Method Name: saveVisitationPlnDetail Method Description: This method is
	 * used to Save/Update the New/Existing visitation Plan.
	 * 
	 * @param visitationPlanDtlReq
	 * @return VisitationPlanDtlRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false)
	public VisitationPlanDtlRes saveVisitationPlnDetail(VisitationPlanDtlReq visitationPlanDtlReq) {
		VisitationPlanDtlRes visitationPlanDtlRes = new VisitationPlanDtlRes();
		if (!ObjectUtils.isEmpty(visitationPlanDtlReq.getVisitationPlanDetailDto())) {
			/*
			 * Checking for the participant list whether it is duplicate or not
			 */
			if (!ObjectUtils.isEmpty(visitationPlanDtlReq.getVisitationPlanDetailDto().getVisitPlanPartcpntList())) {
				HashMap<VstPlanPartcpntDto, List<VstPlanPartcpntDto>> participantMap = new HashMap<>();
				visitationPlanDtlReq.getVisitationPlanDetailDto().getVisitPlanPartcpntList().stream()
						.forEach(participant -> {
							if (!ObjectUtils.isEmpty(participant)
									&& ServiceConstants.Y.equalsIgnoreCase(participant.getIndChildRmvd())) {
								/*
								 * populating a Map of sub care Child as Key and
								 * other participant as values.
								 */
								List<VstPlanPartcpntDto> listParticipantDto = visitationPlanDtlReq
										.getVisitationPlanDetailDto().getVisitPlanPartcpntList().stream()
										.filter(person -> !ObjectUtils.isEmpty(person.getIdPerson())
												&& !"Y".equalsIgnoreCase(person.getIndChildRmvd()))
										.collect(Collectors.toList());
								participantMap.put(participant, listParticipantDto);
							}
						});
				if (!ObjectUtils.isEmpty(participantMap)) {
					visitationPlanDtlRes = noCnctVstPlnDtlDao.visitationPlanExist(visitationPlanDtlReq.getIdStage(),
							participantMap, visitationPlanDtlReq.getVisitationPlanDetailDto().getIdEvent());
				}

			}

			if (ObjectUtils.isEmpty(visitationPlanDtlRes.getResult())
					|| ServiceConstants.NO.equalsIgnoreCase(visitationPlanDtlRes.getResult())) {
				/*
				 * If the id event is coming as empty it means its a new
				 * visitation plan so we need to created a new visitation plan
				 * as requested. if the event id is there means event is already
				 * created and user wants to update that existing visitation
				 * plan.
				 */
				if (TypeConvUtil.isNullOrEmpty(visitationPlanDtlReq.getVisitationPlanDetailDto().getIdEvent())) {
					/*
					 * Calling the populate method to populate the request to
					 * save the new visitation plan.
					 */
					poupulateSaveVisitaionPlanDtl(visitationPlanDtlReq);
					Long idVisitationPlnDetail = noCnctVstPlnDtlDao
							.saveVisitationPlnDetail(visitationPlanDtlReq.getVisitationPlanDetailDto());
					VisitationPlanDetailDto visitationPlanDetailDto = new VisitationPlanDetailDto();
					visitationPlanDetailDto.setIdVisitPlan(idVisitationPlnDetail);
					visitationPlanDetailDto.setIdEvent(visitationPlanDtlReq.getVisitationPlanDetailDto().getIdEvent());
					visitationPlanDtlRes.setVisitationPlanDetailDto(visitationPlanDetailDto);
				} else {
					visitationPlanDtlReq.getVisitationPlanDetailDto()
							.setIdLastUpdatePerson(visitationPlanDtlReq.getIdUser());
					/*
					 * If the event is in pend status it means it is in approval
					 * mode already and if the request reached till this point
					 * it means existing approval have to be invalidated. hence
					 * checking for the event status if the event status is PEND
					 * then invalidating the pending approval.
					 */
					if (ServiceConstants.PEND
							.equalsIgnoreCase(visitationPlanDtlReq.getVisitationPlanDetailDto().getCdEventStatus())
							&& !visitationPlanDtlReq.getVisitationPlanDetailDto().getApprovalMode()) {
						ApprovalCommonInDto pInputMsg = new ApprovalCommonInDto();
						ApprovalCommonOutDto pOutputMsg = new ApprovalCommonOutDto();
						pInputMsg.setIdEvent(visitationPlanDtlReq.getVisitationPlanDetailDto().getIdEvent());
						pInputMsg.setCdEventStatus(ServiceConstants.EVENT_COMP);
						approvalCommonService.InvalidateAprvl(pInputMsg, pOutputMsg);
					}
					/*
					 * Calling the update method to update the changes user
					 * wants to make in existing visitation plan.
					 */
					VisitationPlanDetailDto visitationPlanDetailDto = noCnctVstPlnDtlDao
							.updateVisitationPlnDetail(visitationPlanDtlReq);
					visitationPlanDtlRes.setVisitationPlanDetailDto(visitationPlanDetailDto);
				}
			}

		}
		return visitationPlanDtlRes;

	}

	/**
	 * 
	 * Method Name: poupulateSaveVisitaionPlanDtl Method Description: This
	 * method is used to create the Event for Visitation Plan
	 * 
	 * @param visitationPlanDtlReq
	 */
	private void poupulateSaveVisitaionPlanDtl(VisitationPlanDtlReq visitationPlanDtlReq) {
		/*
		 * Creating a new event DTO to create a event if the new visitation plan
		 * is being created
		 */
		EventValueDto eventValueDto = new EventValueDto();
		eventValueDto.setIdCase(visitationPlanDtlReq.getIdCase());
		eventValueDto.setIdStage(visitationPlanDtlReq.getIdStage());
		eventValueDto.setCdEventTask(TASK_CODE_VISITATION_PLAN);
		eventValueDto.setIdEvent(0L);
		eventValueDto.setEventDescr(VISITATION_PLAN);
		eventValueDto.setCdEventType(VIS);
		eventValueDto.setIdPerson(visitationPlanDtlReq.getIdUser());
		eventValueDto.setDtEventCreated(new Date());
		eventValueDto.setIdStage(visitationPlanDtlReq.getIdStage());
		eventValueDto.setCdEventStatus(ServiceConstants.CEVTSTAT_PROC);
		/*
		 * Calling the post event service to save and do the post event
		 * processing for new event.
		 */
		Long idVstPlnDtlReqEvent = postEventService.postEvent(eventValueDto, ServiceConstants.REQ_FUNC_CD_ADD,
				visitationPlanDtlReq.getVisitationPlanDetailDto().getIdCreatedPerson());
		/*
		 * post event will return u the created event id that id we are saving
		 * into the visitation plan DTO to bind the visitation plan with that
		 * particular event only.
		 */
		visitationPlanDtlReq.getVisitationPlanDetailDto().setIdEvent(idVstPlnDtlReqEvent);
		visitationPlanDtlReq.getVisitationPlanDetailDto().setIdCreatedPerson(visitationPlanDtlReq.getIdUser());
		visitationPlanDtlReq.getVisitationPlanDetailDto().setIdLastUpdatePerson(visitationPlanDtlReq.getIdUser());
	}

	/**
	 * 
	 * Method Name: deleteVisitationPlanDtl Method Description:This method is
	 * used to delete the visitation Plan and No contact visitation Plan.
	 * 
	 * @param idEvent
	 * @return String
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false)
	public String deleteVisitationPlanDtl(Long idEvent) {
		return noCnctVstPlnDtlDao.deleteVisitationPlanDtl(idEvent);

	}
}
