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

import us.tx.state.dfps.service.admin.dao.ApprovalEventDao;
import us.tx.state.dfps.service.admin.dao.ApprovalRecordDao;
import us.tx.state.dfps.service.admin.dao.ApproverUpdateDao;
import us.tx.state.dfps.service.admin.dao.EventProcessDao;
import us.tx.state.dfps.service.admin.dao.FetchEventDao;
import us.tx.state.dfps.service.admin.dao.UpdateEventDao;
import us.tx.state.dfps.service.admin.dto.ApprovalEventLinkDto;
import us.tx.state.dfps.service.admin.dto.ApprovalEventOutputDto;
import us.tx.state.dfps.service.admin.dto.ApprovalHostDto;
import us.tx.state.dfps.service.admin.dto.ApprovalOutputDto;
import us.tx.state.dfps.service.admin.dto.ApprovalRecordDto;
import us.tx.state.dfps.service.admin.dto.ApprovalTaskDto;
import us.tx.state.dfps.service.admin.dto.ApprovalUpdateDto;
import us.tx.state.dfps.service.admin.dto.EventInputDto;
import us.tx.state.dfps.service.admin.dto.EventTaskDto;
import us.tx.state.dfps.service.admin.dto.FetchEventDto;
import us.tx.state.dfps.service.admin.dto.FetchEventResultDto;
import us.tx.state.dfps.service.admin.dto.UpdateEventiDto;
import us.tx.state.dfps.service.admin.service.ApprovalTaskService;
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
public class ApprovalTaskServiceImpl implements ApprovalTaskService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	FetchEventDao objCcmn45dDao;

	@Autowired
	EventProcessDao objCcmn46dDao;

	@Autowired
	ApprovalRecordDao objCcmn55dDao;

	@Autowired
	ApprovalEventDao objCcmn57dDao;

	@Autowired
	UpdateEventDao objCcmn62dDao;

	@Autowired
	ApproverUpdateDao objCcmn88dDao;

	private static final Logger log = Logger.getLogger(ApprovalTaskServiceImpl.class);

	/**
	 * Description : Changes the status of all Approvers records on the
	 * Approvers Table from PEND to INVD.
	 * 
	 * @param pInputMsg
	 * @param pOutputMsg
	 * @return pOutputMsg @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ApprovalOutputDto InvalidateAprvl(ApprovalTaskDto pInputMsg, ApprovalOutputDto pOutputMsg) {
		log.debug("Entering method InvalidateAprvl in ApprovalTaskServiceImpl");
		pOutputMsg.setMoreDataInd("false");
		ApprovalRecordDto pCCMN55DInputRec = new ApprovalRecordDto();
		ApprovalUpdateDto pCCMN88DInputRec = new ApprovalUpdateDto();
		FetchEventDto pCCMN45DInputRec = new FetchEventDto();
		ApprovalEventLinkDto pCCMN57DInputRec = new ApprovalEventLinkDto();
		if (pInputMsg.getArchInputStructDto().getUlSysNbrReserved1() != null) {

			pCCMN55DInputRec.setUlIdEvent(pInputMsg.getUlIdEvent());
			List<ApprovalHostDto> resoures = objCcmn55dDao.getIDApproval(pCCMN55DInputRec);
			if (null != resoures && resoures.size() > 0) {
				pInputMsg.setUlIdApproval(resoures.get(0).getUlIdApproval());
			}

			// If Approval ID exist continue
			if (pInputMsg.getUlIdApproval() > 0) {
				/**
				 * Load the Event row for the given ID APPROVAL/EVENT
				 */
				pCCMN45DInputRec.setIdEvent(pInputMsg.getUlIdApproval());
				List<FetchEventResultDto> objCcmn45doDto = objCcmn45dDao.fetchEventDao(pCCMN45DInputRec);
				if (null != objCcmn45doDto && objCcmn45doDto.size() > 0) {
					for (FetchEventResultDto pCCMN45DOutputRec : objCcmn45doDto) {
						pInputMsg.setUlIdEvent(pCCMN45DOutputRec.getIdEvent());
						pInputMsg.setUlIdStage(pCCMN45DOutputRec.getIdStage());
						pInputMsg.setUlIdPerson(pCCMN45DOutputRec.getIdPerson());
						pInputMsg.setDtDtEventOccurred(pCCMN45DOutputRec.getDtDtEventCreated());
						pInputMsg.setSzCdEventStatus(pCCMN45DOutputRec.getCdEventStatus());
						pInputMsg.setSzCdTask(pCCMN45DOutputRec.getCdTask());
						pInputMsg.setSzCdEventType(pCCMN45DOutputRec.getCdEventType());
						pInputMsg.setSzTxtEventDescr(pCCMN45DOutputRec.getTxtEventDescr());

						pInputMsg.setTsLastUpdate(pCCMN45DOutputRec.getDtLastUpdate());

					}
				}
				/**
				 * Save a modified version of the captured Event row
				 */
				EventInputDto pCCMN46DInputRec = new EventInputDto();
				pCCMN46DInputRec.setIdEvent(pInputMsg.getUlIdEvent());
				pCCMN46DInputRec.setIdPerson(pInputMsg.getUlIdPerson());
				pCCMN46DInputRec.setIdStage(pInputMsg.getUlIdStage());
				pCCMN46DInputRec.setDtDtEventOccurred(pInputMsg.getDtDtEventOccurred());
				pCCMN46DInputRec.setCdEventStatus(ServiceConstants.EVENT_STAT_COMP);
				pCCMN46DInputRec.setCdTask(pInputMsg.getSzCdTask());
				pCCMN46DInputRec.setCdEventType(pInputMsg.getSzCdEventType());
				pCCMN46DInputRec.setTxtEventDescr(pInputMsg.getSzTxtEventDescr());

				pCCMN46DInputRec.setReqFunctionCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
				pCCMN46DInputRec.setDtDtEventOccurred(pInputMsg.getDtDtEventOccurred());
				pCCMN46DInputRec.setEventLastUpdate(pInputMsg.getTsLastUpdate());
				objCcmn46dDao.ccmn46dAUDdam(pCCMN46DInputRec);

				/**
				 * Get all of the ID EVENTS related to the captured ID APPROVAL
				 * from the Approval Event Link Table.
				 */

				pCCMN57DInputRec.setUlIdApproval(pInputMsg.getUlIdApproval());
				List<ApprovalEventOutputDto> liCcmn57doDtoResources = objCcmn57dDao
						.getApprovalEventLink(pCCMN57DInputRec);
				List<EventTaskDto> liRowccmn57doDto = new ArrayList<EventTaskDto>();
				if (null != liCcmn57doDtoResources && liCcmn57doDtoResources.size() > 0) {
					for (int i = 0; i < liCcmn57doDtoResources.size(); i++) {
						ApprovalEventOutputDto refCcmn57doDto = (ApprovalEventOutputDto) liCcmn57doDtoResources.get(i);
						EventTaskDto objROWCCMN57DO = new EventTaskDto();
						objROWCCMN57DO.setUlIdEvent(refCcmn57doDto.getHostIdEvent());
						objROWCCMN57DO.setSzCdTask(refCcmn57doDto.getHostCdTask());
						liRowccmn57doDto.add(objROWCCMN57DO);
					}
					pInputMsg.setROWCCMN57DO(liRowccmn57doDto);
				}

				if (liRowccmn57doDto.size() > 0) {
					UpdateEventiDto pCCMN62DInputRec = new UpdateEventiDto();
					for (int iCounter = 0; iCounter < pInputMsg.getROWCCMN57DO().size(); iCounter++) {
						pCCMN62DInputRec.setUlIdEvent(pInputMsg.getROWCCMN57DO().get(iCounter).getUlIdEvent());
						pCCMN62DInputRec.setSzCdEventStatus(ServiceConstants.EVENTSTATUS_COMPLETE);
						pCCMN62DInputRec.setcReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
						objCcmn62dDao.updateEvent(pCCMN62DInputRec);
					}
				}

				/**
				 * Execute the DAM that changes the status of all Approvers
				 * records on the Approvers Table from PEND to INVD.
				 */
				pCCMN88DInputRec.setUlIdApproval(pInputMsg.getUlIdApproval());
				pCCMN88DInputRec.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
				objCcmn88dDao.updateApproverStatus(pCCMN88DInputRec);
				pOutputMsg.setMoreDataInd(ServiceConstants.ARC_SUCCESS);

			}
		}

		log.debug("Exiting method InvalidateAprvl in ApprovalTaskServiceImpl");
		return pOutputMsg;
	}

	/**
	 * Description:This method invokes InvalidateAprvl method
	 * 
	 * @param pInputMsg
	 * @return pOutputMsg @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ApprovalOutputDto callCcmn05uService(ApprovalTaskDto pInputMsg) {
		log.debug("Entering method callCcmn05uService in ApprovalTaskServiceImpl");
		ApprovalOutputDto pOutputMsg = new ApprovalOutputDto();
		pOutputMsg = InvalidateAprvl(pInputMsg, pOutputMsg);

		pOutputMsg.setMoreDataInd(ServiceConstants.ARC_SUCCESS);
		log.debug("Exiting method callCcmn05uService in ApprovalTaskServiceImpl");
		return pOutputMsg;
	}

}
