package us.tx.state.dfps.service.approval.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.approval.dto.ApprovalFormDto;
import us.tx.state.dfps.approval.dto.ApprovalSecondaryCommentsDto;
import us.tx.state.dfps.approval.dto.ApproverJobHistoryDto;
import us.tx.state.dfps.service.approval.dao.ApprovalFormDao;
import us.tx.state.dfps.service.approval.service.ApprovalFormService;
import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceSelectStageDao;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvInDto;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvOutDto;
import us.tx.state.dfps.service.childplan.dao.ChildServicePlanFormDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ApprovalFormReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ApprovalFormPrefillData;
import us.tx.state.dfps.service.person.dto.EventDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Gathers
 * data from DB for Approval Form Mar 14, 2018- 10:52:46 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class ApprovalFormServiceImpl implements ApprovalFormService {

	@Autowired
	ChildServicePlanFormDao childServicePlanFormDao;

	@Autowired
	CaseMaintenanceSelectStageDao caseMaintenanceSelectStageDao;

	@Autowired
	ApprovalFormDao approvalFormDao;

	@Autowired
	ApprovalFormPrefillData approvalFormPrefillData;

	/**
	 * Method Name: getApprovalFormData Method Description: Retrieves data for
	 * Approval Form from DB
	 * 
	 * @param approvalFormReq
	 * @return PreFillDataServiceDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getApprovalFormData(ApprovalFormReq approvalFormReq) {
		// Declare constants and prefill dto
		Long idStage = ServiceConstants.LONG_ZERO_VAL;
		ApprovalFormDto approvalFormDto = new ApprovalFormDto();

		// CCCMN45D
		List<EventDto> eventDetailsList = childServicePlanFormDao.fetchEventDetails(approvalFormReq.getIdEvent());
		if (!ObjectUtils.isEmpty(eventDetailsList)) {
			idStage = eventDetailsList.get(eventDetailsList.size() - 1).getIdStage();
			approvalFormDto.setEventDetailsList(eventDetailsList);

			// CINT21D
			StageRtrvInDto stageRtrvInDto = new StageRtrvInDto();
			StageRtrvOutDto stageRtrvOutDto = new StageRtrvOutDto();
			stageRtrvInDto.setUlIdStage(idStage);
			caseMaintenanceSelectStageDao.selectStage(stageRtrvInDto, stageRtrvOutDto);
			approvalFormDto.setStageRtrvOutDto(stageRtrvOutDto);
		}

		// CSES04D
		approvalFormDto.setApprovalFormDataDto(approvalFormDao.getApprovalData(approvalFormReq.getIdEvent()));

		// CLSS50D
		approvalFormDto.setEventList(approvalFormDao.getApprovalEventLink(approvalFormReq.getIdEvent()));

		// CLSC02D
		List<ApproverJobHistoryDto> approverList = approvalFormDao.getApprover(approvalFormReq.getIdEvent());
		approvalFormDto.setApproverList(approverList);

		if (!ObjectUtils.isEmpty(approverList) && ServiceConstants.Y.equalsIgnoreCase(approvalFormReq.getRejection())) {
			for (ApproverJobHistoryDto approverDto : approverList) {
				if (ServiceConstants.APPROVAL_REJECT.equals(approverDto.getCdApproversStatus())) {
					// CCMNI4D
					approvalFormDto.setApprovalRejectionPersonDto(approvalFormDao.getApprovalRejection(idStage).get(0));
					break;
				}
			}
		}

		return approvalFormPrefillData.returnPrefillData(approvalFormDto);
	}

	/**
	 * Method Name: getApprovalSecondaryComments Method Description: to get
	 * approval comments
	 * 
	 * @param approvalFormReq
	 * @return ApprovalSecondaryCommentsDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public ApprovalSecondaryCommentsDto getApprovalSecondaryComments(ApprovalFormReq approvalFormReq) {
		return approvalFormDao.getSecondaryApprovalComments(approvalFormReq.getIdEvent());
	}

}
