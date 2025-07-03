package us.tx.state.dfps.service.common.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.admin.dto.PostDto;
import us.tx.state.dfps.service.admin.dto.PostOutputDto;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.InvdApprReq;
import us.tx.state.dfps.service.common.request.PostEventReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.InvalidateApprovalRes;
import us.tx.state.dfps.service.common.response.PostEventRes;
import us.tx.state.dfps.service.common.service.ApprovalService;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.webservices.gold.dto.GoldCommunicationDto;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Apr 6, 2017 - 1:59:18 PM
 */
@RestController
@RequestMapping(value = "/aprroval")
public class ApprovalController {

	/**
	 * 
	 */
	public ApprovalController() {
		// TODO Auto-generated constructor stub
	}

	@Autowired
	ApprovalService approvalService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	PostEventService postEventService;

	private static final Logger log = Logger.getLogger(ApprovalController.class);

	/**
	 * This library / common function will the approval related to a given ID
	 * EVENT. Functional Events are uniquely identified by this input. This
	 * function will find the the ID APPROVAL (ID EVENT of the approval event)
	 * from the Approval Event List Table. This key information will allow the
	 * function to complete the following: Update the Approval Event, Get any
	 * other functional events related to the same Approval and demote them, set
	 * any pending related approvers to invalid status. Method Description:
	 * legacy service name - CCMN05U
	 * 
	 * @param invalidateApprovalReq
	 * @return
	 */
	@RequestMapping(value = "/invalidateapproval", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  InvalidateApprovalRes invalidateApproval(@RequestBody InvdApprReq invalidateApprovalReq) {

		if (invalidateApprovalReq.getUlIdApproval() == null) {
			throw new InvalidRequestException(
					messageSource.getMessage("approval.approavlid.mandatory", null, Locale.US));
		}
		if (invalidateApprovalReq.getUlIdEvent() == null) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + invalidateApprovalReq.getTransactionId());
		return approvalService.invalidateApproval(invalidateApprovalReq);
	}

	@RequestMapping(value = "/ccmn01ServiceCall", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PostOutputDto ccmn01ServiceCall(@RequestBody PostDto postDto){
		PostOutputDto postOutputDto = postEventService.callCcmn01uService(postDto);
		return postOutputDto;
	}

	@RequestMapping(value = "/ccmn05ServiceCall", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public InvalidateApprovalRes ccmn05ServiceCall(@RequestBody InvdApprReq invalidateApprovalReq){
		InvalidateApprovalRes invalidateApprovalRes = invalidateApproval(invalidateApprovalReq);
		return invalidateApprovalRes;
	}
}
