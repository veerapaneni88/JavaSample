package us.tx.state.dfps.service.sscc.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ApprovalEventLink;
import us.tx.state.dfps.common.domain.Approvers;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.dto.SSCCExceptCareDesignationDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.casepackage.dao.SSCCTimelineDao;
import us.tx.state.dfps.service.casepackage.dto.SSCCResourceDto;
import us.tx.state.dfps.service.casepackage.dto.ToDoDetailDto;
import us.tx.state.dfps.service.casepackage.service.CaseSummaryService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.ApproversDao;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.SSCCExceptCareDesiReq;
import us.tx.state.dfps.service.common.response.SSCCExceptCareDesiRes;
import us.tx.state.dfps.service.investigation.dao.SrvreferralslDao;
import us.tx.state.dfps.service.sscc.dao.SSCCExceptCareDesignationDao;
import us.tx.state.dfps.service.sscc.dao.SSCCRefDao;
import us.tx.state.dfps.service.sscc.service.SSCCExceptCareDesignationService;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.PostEventDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Feb 15, 2018- 2:58:31 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class SSCCExceptCareDesignationServiceImpl implements SSCCExceptCareDesignationService {

	@Autowired
	SSCCExceptCareDesignationDao ssccExceptCareDesignationDao;

	@Autowired
	SSCCRefDao sSCCRefDao;

	@Autowired
	SSCCTimelineDao ssccTimelineDao;
	
	@Autowired
	PostEventService postEventService;
	
	@Autowired
	SrvreferralslDao srvreferralsDao;
	
	@Autowired
	CaseSummaryService caseSummaryService;
	
	@Autowired
	TodoDao todoDao;
	
	@Autowired
	EventDao eventDao;

	@Autowired
	private ApproversDao approversDao;
	
	/**
	 * 
	 * Method Description: Gets child SSCC eligible placement for stage id.
	 * Service Name: getEligibilityPlcmtInfo
	 * 
	 * @param sSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes
	 */

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCExceptCareDesiRes getEligibilityPlcmtInfo(SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		SSCCExceptCareDesiRes ssccExceptCareDesiRes = new SSCCExceptCareDesiRes();
		ssccExceptCareDesiRes.setSsccExcpCareDesList(
				ssccExceptCareDesignationDao.getEligibilityPlcmtInfo(sSCCExceptCareDesiReq.getIdStage()));
		return ssccExceptCareDesiRes;
	}

	/**
	 * 
	 * Method Description: Method fetches the SSCC Resource information for the
	 * input SSCC Contract Region. Service Name: fetchSSCCResourceInfo
	 * 
	 * @param sSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCExceptCareDesiRes fetchSSCCResourceInfo(SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {

		SSCCResourceDto ssccResourceDto = new SSCCResourceDto();
		if (!ObjectUtils.isEmpty(sSCCExceptCareDesiReq.getSsccResourceDto())) {
			ssccResourceDto = sSCCRefDao.fetchSSCCResourceInfo(
					sSCCExceptCareDesiReq.getSsccResourceDto().getCdSSCCCntrctRegion(),
					sSCCExceptCareDesiReq.getSsccResourceDto().getIdSSCCCatchment());
		}
		SSCCExceptCareDesiRes ssccExceptCareDesiRes = new SSCCExceptCareDesiRes();
		ssccExceptCareDesiRes.setSsccResourceDto(ssccResourceDto);
		return ssccExceptCareDesiRes;
	}

	/**
	 * 
	 * Method Description: Gets an existed exceptional care designation record &
	 * Get resource and contract information for stage id. & Gets an active
	 * child sscc referral from the SSCC_REFERRAL table Service Name:
	 * getExistECDesig & getSsccRsrcContractInfo
	 * 
	 * @param sSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCExceptCareDesiRes getExistECDesigAndSsccRsrcContractInfo(SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		SSCCExceptCareDesiRes ssccExceptCareDesiRes = new SSCCExceptCareDesiRes();
		if (!ObjectUtils.isEmpty(sSCCExceptCareDesiReq.getIdECDesig())) {
			ssccExceptCareDesiRes.setSsccExistECDesig(
					ssccExceptCareDesignationDao.getExistECDesig(sSCCExceptCareDesiReq.getIdECDesig()));
		}
		if (!ObjectUtils.isEmpty(sSCCExceptCareDesiReq.getIdStage())) {
			ssccExceptCareDesiRes.setSsccRsrcContractInfo(
					ssccExceptCareDesignationDao.getSsccRsrcContractInfo(sSCCExceptCareDesiReq.getIdStage(),sSCCExceptCareDesiReq.getIdPlcmntEvent()));
			ssccExceptCareDesiRes.setSsccActiveChildPlcmtRefferal(
					ssccExceptCareDesignationDao.getActiveChildPlcmtRefferal(sSCCExceptCareDesiReq.getIdStage()));
		}
		return ssccExceptCareDesiRes;
	}

	/**
	 * 
	 * Method Description: Insert a new record into SSCC_EXCEPTIONAL_CARE_DESIG
	 * or Updates a record from the same table. Service Name:
	 * saveOrUpdateExceptCareDesig
	 * 
	 * @param sSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCExceptCareDesiRes saveOrUpdateExceptCareDesig(SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		SSCCExceptCareDesiRes ssccExceptCareDesiRes = new SSCCExceptCareDesiRes();
		
		Long idEvent = 0L;
		/*
		 * CBC Release 3 - Added an Event for SSCC Exception Care and TODOTable for
		 * creating a TASK for the DFPS Staff to approve.
		 */
		if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(sSCCExceptCareDesiReq.getReqFuncCd())) {
			ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
			sSCCExceptCareDesiReq.getPostEventIPDto().setTsLastUpdate(new Date());
			String cdCurrentEventStatus = sSCCExceptCareDesiReq.getSsccExcpCareDesDto().getCdEventStatus();

			if ((ServiceConstants.NEW).equalsIgnoreCase(cdCurrentEventStatus)
					|| ServiceConstants.EMPTY_STR.equalsIgnoreCase(cdCurrentEventStatus)) {
				/* Event Person Link information */

				Long idPlacementChild = sSCCExceptCareDesiReq.getSsccExcpCareDesDto().getIdPlcmtChild();
				if (!ObjectUtils.isEmpty(idPlacementChild)) {
					PostEventDto postEventDto = new PostEventDto();
					List<PostEventDto> postEventDtoList = new ArrayList<>();
					postEventDto.setIdPerson(idPlacementChild);
					postEventDto.setCdScrDataAction(ServiceConstants.REQ_FUNC_CD_ADD);
					postEventDtoList.add(postEventDto);
					sSCCExceptCareDesiReq.getPostEventIPDto().setPostEventDto(postEventDtoList);
				}
			}

			if (ObjectUtils.isEmpty(sSCCExceptCareDesiReq.getPostEventIPDto().getIdEvent())
					|| 0L == sSCCExceptCareDesiReq.getPostEventIPDto().getIdEvent()) {
				serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
			}
			PostEventOPDto postEventOPDto = postEventService
					.checkPostEventStatus(sSCCExceptCareDesiReq.getPostEventIPDto(), serviceReqHeaderDto);
			idEvent = postEventOPDto.getIdEvent();
			if (!ObjectUtils.isEmpty(idEvent)) {
				sSCCExceptCareDesiReq.getSsccExcpCareDesDto().setIdEvent(idEvent);
			}
			// Write to todo table
			if (0L != idEvent) {
				if (0L != sSCCExceptCareDesiReq.getPostEventIPDto().getIdEvent()
						|| !ObjectUtils.isEmpty(sSCCExceptCareDesiReq.getPostEventIPDto().getIdEvent())) {
					TodoDto toDoDto = new TodoDto();
					toDoDto.setIdTodoEvent(sSCCExceptCareDesiReq.getPostEventIPDto().getIdEvent());
					srvreferralsDao.updateOrSaveToDO(toDoDto);
				}

			}
		} else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equalsIgnoreCase(sSCCExceptCareDesiReq.getReqFuncCd())) {
			idEvent = sSCCExceptCareDesiReq.getIdEvent();
			if(!ObjectUtils.isEmpty(idEvent)) {
				Event event = eventDao.getEventById(idEvent);
				if (!ObjectUtils.isEmpty(event) && (ServiceConstants.PEND).equalsIgnoreCase(event.getCdEventStatus())) {
					event.setCdEventStatus(ServiceConstants.EVENTSTATUS_COMPLETE);
					event.setDtEventModified(new Date());
					eventDao.updateEvent(event, ServiceConstants.REQ_FUNC_CD_UPDATE);
				}
				if (sSCCExceptCareDesiReq.getSsccExcpCareDesDto().getCdStatus().equalsIgnoreCase(ServiceConstants.CSSCCSTA_60)) {
					boolean isApprovalPending = isApprovalPending(idEvent);
					if (!isApprovalPending) {
						if (0L != idEvent) {
							TodoDto toDoDto = new TodoDto();
							toDoDto.setIdTodoEvent(idEvent);
							srvreferralsDao.updateOrSaveToDO(toDoDto);
						}
					}
				}
			}
			
			if (sSCCExceptCareDesiReq.getSsccExcpCareDesDto().getCdStatus().equalsIgnoreCase(ServiceConstants.CSSCCSTA_90)) {
				/* Update the Approvers and Event */
				updateApproversEvent(sSCCExceptCareDesiReq);
				/* Delete the Todo since the SSCC Exception Care is approved */
				deleteTodo(sSCCExceptCareDesiReq);
			}
		}

		ssccExceptCareDesiRes.setSsccExcpCareDesDto(ssccExceptCareDesignationDao.saveOrUpdateExceptCareDesig(
				sSCCExceptCareDesiReq.getSsccExcpCareDesDto(), sSCCExceptCareDesiReq.getReqFuncCd()));

		if (!ObjectUtils.isEmpty(idEvent)) {
			ssccExceptCareDesiRes.getSsccExcpCareDesDto().setIdEvent(idEvent);
		}
		
				return ssccExceptCareDesiRes;
	}

	/**
	 * @param idEvent
	 * @return
	 */
	private boolean isApprovalPending(Long idEvent) {
		boolean isApprovalPending = true;
		ApprovalEventLink ael = eventDao.getAppEvntLinkByParentId(idEvent);
		if(!ObjectUtils.isEmpty(ael)) {
			isApprovalPending = false;
		}
		return isApprovalPending;
	}

	/**
	 * @param sSCCExceptCareDesiReq
	 * CBC R3 - This method updates the APPROVERS and EVENT Table when an SSCC Exception Care in APPROVED.
	 */
	private void updateApproversEvent(SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		Long eventId = sSCCExceptCareDesiReq.getIdEvent();
		/* Find the Approval Event ID >> IN: ID Event OUT: ID Approval */
		ApprovalEventLink ael = eventDao.getAppEvntLinkByParentId(eventId);
		/* Get the Approval Event >> IN: ID Approval OUT: Event Row */
		if(!ObjectUtils.isEmpty(ael)) {
			Event event = eventDao.getEventById(ael.getIdApproval());
			if(!ObjectUtils.isEmpty(event)) {
				event.setCdEventStatus(ServiceConstants.EVENTSTATUS_COMPLETE);
				event.setDtEventModified(new Date());
				eventDao.updateEvent(event, ServiceConstants.REQ_FUNC_CD_UPDATE);
			}
			
			Approvers approvers = approversDao.getPendingApproversByApprovalId(ael.getIdApproval());
			if (!ObjectUtils.isEmpty(approvers)) {
				approvers.setCdApproversStatus(ServiceConstants.EVENTSTATUS_APPROVE);
				approvers.setDtApproversDetermination(new Date());
				approversDao.updtApprovers(approvers, ServiceConstants.REQ_FUNC_CD_UPDATE);
			}
		}
		
		
	}
	/**
	 * 
	 * Method Description: Get except care before save and approve Service Name:
	 * getExcpCareOnSaveAndApprove
	 * 
	 * @param sSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes
	 */

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCExceptCareDesiRes getExcpCareOnSaveAndApprove(SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		SSCCExceptCareDesiRes ssccExceptCareDesiRes = new SSCCExceptCareDesiRes();
		ssccExceptCareDesiRes.setSsccExcpCareDesDto(
				ssccExceptCareDesignationDao.getExcpCareOnSaveAndApprove(sSCCExceptCareDesiReq.getIdECDesig()));
		return ssccExceptCareDesiRes;
	}

	/**
	 * 
	 * Method Description: Method returns a Resposne object with a list of
	 * Timeline objects related to a specific SSCC Referral Id and reference
	 * stage Service Name: getSSCCTimelineList
	 * 
	 * @param sSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCExceptCareDesiRes getSSCCTimelineList(SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		SSCCExceptCareDesiRes ssccExceptCareDesiRes = new SSCCExceptCareDesiRes();
		ssccExceptCareDesiRes.setSsccTimelineDtoList(
				ssccTimelineDao.getSSCCTimelineList(sSCCExceptCareDesiReq.getSsccTimelineDto()));
		return ssccExceptCareDesiRes;
	}

	/**
	 * Method Name: getSSCCExpCareServiceList Method Description: This method
	 * used for Gets list of child sscc placement for stage id
	 * 
	 * @param SSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes @
	 */

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCExceptCareDesiRes getUpdateSSCCExceptCare(SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		Boolean ssccupdateCdExceptCareStatus = ssccExceptCareDesignationDao.getUpdateCdExceptCareStatus(
				sSCCExceptCareDesiReq.getCdExceptCareStatus(), sSCCExceptCareDesiReq.getIdSsccReferral());
		Boolean ssccupdateSSCCExceptCareDesigStatus = false;
		if (!ObjectUtils.isEmpty(sSCCExceptCareDesiReq.getIdExceptCareDesig())
				&& sSCCExceptCareDesiReq.getIdExceptCareDesig() > 0) {
			ssccupdateSSCCExceptCareDesigStatus = ssccExceptCareDesignationDao.getUpdateSSCCExceptCareDesigStatus(
					sSCCExceptCareDesiReq.getCdStatus(), sSCCExceptCareDesiReq.getIdExceptCareDesig());
		}
		SSCCExceptCareDesiRes ssccExceptCareDesiRes = new SSCCExceptCareDesiRes();
		ssccExceptCareDesiRes.setSsccupdateCdExceptCareStatus(ssccupdateCdExceptCareStatus);
		ssccExceptCareDesiRes.setSsccupdateSSCCExceptCareDesigStatus(ssccupdateSSCCExceptCareDesigStatus);
		//CBC R3 - Delete the associated Todo when an exception care is rescinded
		if(sSCCExceptCareDesiReq.getCdExceptCareStatus().equalsIgnoreCase(ServiceConstants.CSSCCSTA_70)) {
			deleteTodo(sSCCExceptCareDesiReq);  
		}
		
		 
		return ssccExceptCareDesiRes;
	}

	private void deleteTodo(SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		ToDoDetailDto toDoDetailDto = new ToDoDetailDto(); 
		CommonHelperReq commonHelperReq = new CommonHelperReq();
		Long idEvent = sSCCExceptCareDesiReq.getIdEvent();
		if(!ObjectUtils.isEmpty(idEvent)){
			commonHelperReq.setIdEvent(idEvent); 
			toDoDetailDto =  caseSummaryService.getApprovalToDo(commonHelperReq);
			if (toDoDetailDto != null && toDoDetailDto.getIdToDo() !=null) {
				todoDao.deleteTodo(toDoDetailDto.getIdToDo());
			}
		}
		
	}

	/**
	 * Method Name: getSSCCExpCareServiceList Method Description: This method
	 * used for Gets list of child sscc placement for stage id
	 * 
	 * @param SSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes @
	 */

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)

	public SSCCExceptCareDesiRes getSSCCExpCareServiceList(SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		SSCCExceptCareDesiRes ssccExceptCareDesiRes = new SSCCExceptCareDesiRes();
		List<SSCCExceptCareDesignationDto> ssccExceptCareList = ssccExceptCareDesignationDao
				.getSSCCExceptCareList(sSCCExceptCareDesiReq.getIdCase(), sSCCExceptCareDesiReq.getIdStage());
		List<SSCCExceptCareDesignationDto> ssccEligibilityPlacList = ssccExceptCareDesignationDao
				.getEligibilityPlcmtInfo(sSCCExceptCareDesiReq.getIdStage());
		SSCCExceptCareDesignationDto ssccActiveChildPlcmtReferralList = ssccExceptCareDesignationDao
				.getActiveChildPlcmtRefferal(sSCCExceptCareDesiReq.getIdStage());
		List<SSCCExceptCareDesignationDto> ssccActivePlcmtList = ssccExceptCareDesignationDao
				.getActiveSsccPlcmt(sSCCExceptCareDesiReq.getIdStage());
		if (!ObjectUtils.isEmpty(ssccActiveChildPlcmtReferralList)
				&& !ObjectUtils.isEmpty(ssccActiveChildPlcmtReferralList.getIdSsccReferral())) {
			SSCCExceptCareDesignationDto ssccExceptCareDays = ssccExceptCareDesignationDao
					.getSSCCExceptCareDays(ssccActiveChildPlcmtReferralList.getIdSsccReferral());
			ssccExceptCareDesiRes.setSsccExceptCareDays(ssccExceptCareDays);
		}

		ssccExceptCareDesiRes.setSsccExcpCareDesList(ssccExceptCareList);
		ssccExceptCareDesiRes.setSsccActiveChildPlcmtRefferal(ssccActiveChildPlcmtReferralList);
		ssccExceptCareDesiRes.setSsccEligiblityPlacInfoList(ssccEligibilityPlacList);
		ssccExceptCareDesiRes.setSsccActivePlacList(ssccActivePlcmtList);

		return ssccExceptCareDesiRes;
	}

	/**
	 * Method Name: getActiveSsccPlcmt Method Description: This method used for
	 * Gets list of child sscc placement for stage id
	 * 
	 * @param SSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes @
	 */

	@Override
	public SSCCExceptCareDesiRes getActiveSsccPlcmt(SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		List<SSCCExceptCareDesignationDto> ssccExceptCareList = ssccExceptCareDesignationDao
				.getActiveSsccPlcmt(sSCCExceptCareDesiReq.getIdStage());
		SSCCExceptCareDesiRes ssccExceptCareDesiRes = new SSCCExceptCareDesiRes();
		ssccExceptCareDesiRes.setSsccExcpCareDesList(ssccExceptCareList);
		return ssccExceptCareDesiRes;
	}

	/**
	 * Method Name: getActiveChildPlcmtRefferal Method Description:This method
	 * gets active child placement referral for the stage id
	 * 
	 * @param SSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes @
	 */

	@Override
	public SSCCExceptCareDesiRes getActiveChildPlcmtRefferal(SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		SSCCExceptCareDesignationDto ssccActiveChildPlcmtRefferal = ssccExceptCareDesignationDao
				.getActiveChildPlcmtRefferal(sSCCExceptCareDesiReq.getIdStage());
		SSCCExceptCareDesiRes ssccExceptCareDesiRes = new SSCCExceptCareDesiRes();
		ssccExceptCareDesiRes.setSsccActiveChildPlcmtRefferal(ssccActiveChildPlcmtRefferal);
		return ssccExceptCareDesiRes;
	}

	/**
	 * Method Name: getActiveChildPlcmtRefferal Method Description:This method
	 * gets active child placement referral for the stage id
	 * 
	 * @param SSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes @
	 */

	@Override
	public Boolean getUpdateCdExceptCareStatus(SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		Boolean ssccUpdateCdExceptCareStatus = ssccExceptCareDesignationDao.getUpdateCdExceptCareStatus(
				sSCCExceptCareDesiReq.getCdExceptCareStatus(), sSCCExceptCareDesiReq.getIdSsccReferral());

		return ssccUpdateCdExceptCareStatus;
	}

	/**
	 * Method Name: getUpdateSSCCExceptCareDesigStatus Method Description:This
	 * method gets active child placement referral for the stage id
	 * 
	 * @param SSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes @
	 */

	@Override
	public Boolean getUpdateSSCCExceptCareDesigStatus(SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		Boolean ssccUpdateSSCCExceptCareDesigStatus = ssccExceptCareDesignationDao.getUpdateSSCCExceptCareDesigStatus(
				sSCCExceptCareDesiReq.getCdStatus(), sSCCExceptCareDesiReq.getIdExceptCareDesig());
		return ssccUpdateSSCCExceptCareDesigStatus;
	}

	/**
	 * Method Name: getExceptCareList Method Description:Get list of exceptional
	 * care record per idCase and idStage
	 * 
	 * @param SSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCExceptCareDesiRes getExceptCareList(CommonHelperReq commonHelperReq) {
		SSCCExceptCareDesiRes ssccExceptCareDesiRes = new SSCCExceptCareDesiRes();
		ssccExceptCareDesiRes.setExceptionalCareList(ssccExceptCareDesignationDao
				.getExceptCareList(commonHelperReq.getIdCase(), commonHelperReq.getIdStage()));
		return ssccExceptCareDesiRes;
	}

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCExceptCareDesiRes insertTimeLine(SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		SSCCExceptCareDesiRes ssccExceptCareDesiRes = new SSCCExceptCareDesiRes();
		ssccTimelineDao.insertSSCCTimeline(sSCCExceptCareDesiReq.getSsccTimelineDto());
		return ssccExceptCareDesiRes;
	}

}
