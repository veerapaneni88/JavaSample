package us.tx.state.dfps.service.admin.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.admin.dao.ApprovalEventLinkSelectByIdDao;
import us.tx.state.dfps.service.admin.dao.ApproversUpdDao;
import us.tx.state.dfps.service.admin.dao.EventApprovalEventLinkDao;
import us.tx.state.dfps.service.admin.dao.EventIdDao;
import us.tx.state.dfps.service.admin.dao.EventInsUpdDelDao;
import us.tx.state.dfps.service.admin.dao.EventUpdEventStatusDao;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonOutDto;
import us.tx.state.dfps.service.admin.dto.ApprovalEventLinkSelectByIdInDto;
import us.tx.state.dfps.service.admin.dto.ApprovalEventLinkSelectByIdOutDto;
import us.tx.state.dfps.service.admin.dto.ApproversUpdInDto;
import us.tx.state.dfps.service.admin.dto.CpsApprovalEventLinkDto;
import us.tx.state.dfps.service.admin.dto.EventApprovalEventLinkInDto;
import us.tx.state.dfps.service.admin.dto.EventApprovalEventLinkOutDto;
import us.tx.state.dfps.service.admin.dto.EventIdInDto;
import us.tx.state.dfps.service.admin.dto.EventIdOutDto;
import us.tx.state.dfps.service.admin.dto.EventInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.EventUpdEventStatusInDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * will find the the ID APPROVAL (ID EVENT of the approval event) from the
 * Approval Event List Table. This key information will allow the function to
 * complete the following: Update the Approval Event, Get any other functional
 * events related to the same Approval and demote them, set any pending related
 * approvers to invalid status. Aug 8, 2017- 11:16:29 PM � 2017 Texas Department
 * of Family and Protective Services
 */
@Service
@Transactional
public class ApprovalCommonServiceImpl implements ApprovalCommonService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	EventIdDao objCcmn45dDao;

	@Autowired
	EventInsUpdDelDao objCcmn46dDao;

	@Autowired
	ApprovalEventLinkSelectByIdDao objCcmn55dDao;

	@Autowired
	EventApprovalEventLinkDao objCcmn57dDao;

	@Autowired
	EventUpdEventStatusDao objCcmn62dDao;

	@Autowired
	ApproversUpdDao objCcmn88dDao;

	private static final Logger log = Logger.getLogger(ApprovalCommonServiceImpl.class);

	/**
	 * Description : Changes the status of all Approvers records on the
	 * Approvers Table from PEND to INVD.
	 * 
	 * @param pInputMsg
	 * @param pOutputMsg
	 * @return pOutputMsg @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ApprovalCommonOutDto InvalidateAprvl(ApprovalCommonInDto pInputMsg, ApprovalCommonOutDto pOutputMsg) {
		log.debug("Entering method InvalidateAprvl in ApprovalCommonServiceImpl");
		pOutputMsg.setMoreDataInd("false");
		ApprovalEventLinkSelectByIdInDto pCCMN55DInputRec = new ApprovalEventLinkSelectByIdInDto();
		ApproversUpdInDto pCCMN88DInputRec = new ApproversUpdInDto();
		EventIdInDto pCCMN45DInputRec = new EventIdInDto();
		EventApprovalEventLinkInDto pCCMN57DInputRec = new EventApprovalEventLinkInDto();
		// if (!pInputMsg.getUlSysNbrReserved1()) {
		pCCMN55DInputRec.setIdEvent(pInputMsg.getIdEvent());
		List<ApprovalEventLinkSelectByIdOutDto> resoures = objCcmn55dDao.getIDApproval(pCCMN55DInputRec);
		if (null != resoures && resoures.size() > 0) {
			pInputMsg.setIdApproval(resoures.get(0).getIdApproval());
		}
		// If Approval ID exist continue
		if (pInputMsg.getIdApproval() != null && pInputMsg.getIdApproval() > 0) {
			/**
			 * Load the Event row for the given ID APPROVAL/EVENT
			 */
			pCCMN45DInputRec.setIdEvent(pInputMsg.getIdApproval());
			List<EventIdOutDto> objCcmn45doDto = objCcmn45dDao.getEventDetailList(pCCMN45DInputRec);
			if (null != objCcmn45doDto && objCcmn45doDto.size() > 0) {
				for (EventIdOutDto pCCMN45DOutputRec : objCcmn45doDto) {
					pInputMsg.setIdEvent(pCCMN45DOutputRec.getIdEvent());
					pInputMsg.setIdStage(pCCMN45DOutputRec.getIdStage());
					pInputMsg.setIdPerson(pCCMN45DOutputRec.getIdPerson());
					pInputMsg.setDtEventOccurred(pCCMN45DOutputRec.getDtEventCreated());
					pInputMsg.setCdEventStatus(pCCMN45DOutputRec.getCdEventStatus());
					pInputMsg.setCdTask(pCCMN45DOutputRec.getCdTask());
					pInputMsg.setCdEventType(pCCMN45DOutputRec.getCdEventType());
					pInputMsg.setEventDescr(pCCMN45DOutputRec.getEventDescr());
					pInputMsg.setTsLastUpdate(pCCMN45DOutputRec.getTsLastUpdate());
				}
			}
			/**
			 * Save a modified version of the captured Event row
			 */
			EventInsUpdDelInDto pCCMN46DInputRec = new EventInsUpdDelInDto();
			pCCMN46DInputRec.setIdEvent(pInputMsg.getIdEvent());
			pCCMN46DInputRec.setIdPerson(pInputMsg.getIdPerson());
			pCCMN46DInputRec.setIdStage(pInputMsg.getIdStage());
			pCCMN46DInputRec.setDtEventOccurred(pInputMsg.getDtEventOccurred());
			pCCMN46DInputRec.setCdEventStatus(ServiceConstants.EVENT_STAT_COMP);
			pCCMN46DInputRec.setCdTask(pInputMsg.getCdTask());
			pCCMN46DInputRec.setCdEventType(pInputMsg.getCdEventType());
			pCCMN46DInputRec.setEventDescr(pInputMsg.getEventDescr());
			pCCMN46DInputRec.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
			pCCMN46DInputRec.setDtEventOccurred(pInputMsg.getDtEventOccurred());
			pCCMN46DInputRec.setDtEventLastUpdate(pInputMsg.getTsLastUpdate());
			objCcmn46dDao.ccmn46dAUDdam(pCCMN46DInputRec);
			/**
			 * Get all of the ID EVENTS related to the captured ID APPROVAL from
			 * the Approval Event Link Table.
			 */
			pCCMN57DInputRec.setIdApproval(pInputMsg.getIdApproval());
			List<EventApprovalEventLinkOutDto> liCcmn57doDtoResources = objCcmn57dDao
					.getApprovalEventLink(pCCMN57DInputRec);
			List<CpsApprovalEventLinkDto> liRowccmn57doDto = new ArrayList<CpsApprovalEventLinkDto>();
			if (null != liCcmn57doDtoResources && liCcmn57doDtoResources.size() > 0) {
				for (int i = 0; i < liCcmn57doDtoResources.size(); i++) {
					EventApprovalEventLinkOutDto refCcmn57doDto = (EventApprovalEventLinkOutDto) liCcmn57doDtoResources
							.get(i);
					CpsApprovalEventLinkDto objROWCCMN57DO = new CpsApprovalEventLinkDto();
					objROWCCMN57DO.setIdEvent(refCcmn57doDto.getHostIdEvent());
					objROWCCMN57DO.setCdTask(refCcmn57doDto.getHostCdTask());
					liRowccmn57doDto.add(objROWCCMN57DO);
				}
				pInputMsg.setROWCCMN57DO(liRowccmn57doDto);
			}
			if (liRowccmn57doDto.size() > 0) {
				EventUpdEventStatusInDto pCCMN62DInputRec = new EventUpdEventStatusInDto();
				for (int iCounter = 0; iCounter < pInputMsg.getROWCCMN57DO().size(); iCounter++) {
					pCCMN62DInputRec.setIdEvent(pInputMsg.getROWCCMN57DO().get(iCounter).getIdEvent());
					pCCMN62DInputRec.setCdEventStatus(ServiceConstants.EVENTSTATUS_COMPLETE);
					pCCMN62DInputRec.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
					objCcmn62dDao.updateEvent(pCCMN62DInputRec);
				}
			}
			/**
			 * Execute the DAM that changes the status of all Approvers records
			 * on the Approvers Table from PEND to INVD.
			 */
			pCCMN88DInputRec.setIdApproval(pInputMsg.getIdApproval());
			pCCMN88DInputRec.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
			objCcmn88dDao.updateApproverStatus(pCCMN88DInputRec);
			pOutputMsg.setMoreDataInd(ServiceConstants.ARC_SUCCESS);
		}
		// }
		log.debug("Exiting method InvalidateAprvl in ApprovalCommonServiceImpl");
		return pOutputMsg;
	}

	/**
	 * Description :This method invokes InvalidateAprvl method
	 * 
	 * @param pInputMsg
	 * @return pOutputMsg @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ApprovalCommonOutDto callCcmn05uService(ApprovalCommonInDto pInputMsg) {
		log.debug("Entering method callCcmn05uService in ApprovalCommonServiceImpl");
		ApprovalCommonOutDto pOutputMsg = new ApprovalCommonOutDto();
		pOutputMsg = InvalidateAprvl(pInputMsg, pOutputMsg);
		pOutputMsg.setMoreDataInd(ServiceConstants.ARC_SUCCESS);
		log.debug("Exiting method callCcmn05uService in ApprovalCommonServiceImpl");
		return pOutputMsg;
	}
}
