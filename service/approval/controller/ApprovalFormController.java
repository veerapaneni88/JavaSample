package us.tx.state.dfps.service.approval.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.approval.dto.ApprovalSecondaryCommentsDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.approval.service.ApprovalFormService;
import us.tx.state.dfps.service.common.request.ApprovalFormReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Controller
 * that retrieves Approval Form data Mar 14, 2018- 10:34:29 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("approvalform")
public class ApprovalFormController {

	@Autowired
	private ApprovalFormService approvalFormService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(ApprovalFormController.class);

	/**
	 * Method Name: getApprovalFormData Method Description: Sends input data to
	 * service to retrieve data
	 * 
	 * @param approvalFormReq
	 * @return CommonFormRes
	 */
	@RequestMapping(value = "/getData", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getApprovalFormData(@RequestBody ApprovalFormReq approvalFormReq) {
		log.debug("Entering method CFASD29S getApprovalFormData in ApprovalFormController");
		if (TypeConvUtil.isNullOrEmpty(approvalFormReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("ppm.stageId.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes
				.setPreFillData(TypeConvUtil.getXMLFormat(approvalFormService.getApprovalFormData(approvalFormReq)));

		return commonFormRes;
	}

	/**
	 * Method Name: getSecondaryComments Method Description: Controller method
	 * to get secondary comments
	 * 
	 * @param approvalFormReq
	 * @return ApprovalSecondaryCommentsDto
	 */
	@RequestMapping(value = "/secondarycomments", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public ApprovalSecondaryCommentsDto getSecondaryComments(@RequestBody ApprovalFormReq approvalFormReq) {
		log.debug("Entering method getSecondaryComments in ApprovalFormController");
		if (TypeConvUtil.isNullOrEmpty(approvalFormReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		ApprovalSecondaryCommentsDto approvalSecondaryCommentsDto = approvalFormService
				.getApprovalSecondaryComments(approvalFormReq);
		if (ObjectUtils.isEmpty(approvalSecondaryCommentsDto)) {
			approvalSecondaryCommentsDto = new ApprovalSecondaryCommentsDto();
		}
		return approvalSecondaryCommentsDto;
	}

}
