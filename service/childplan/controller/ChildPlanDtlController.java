
package us.tx.state.dfps.service.childplan.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.common.exception.TimeMismatchException;
import us.tx.state.dfps.service.childplan.service.ChildPlanDtlService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ChildPlanDtlReq;
import us.tx.state.dfps.service.common.response.ChildPlanDtlRes;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.CommonIdRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.ServiceLayerException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * will have the fetch, save, save&submit and delete functionality for ChildPlan
 * Detail Screen. May 4, 2018- 10:19:42 AM © 2017 Texas Department of Family and
 * Protective Services
 */
@RestController
@RequestMapping("/childPlanDtl")
@Api(tags = { "childplan" })
public class ChildPlanDtlController {

	private static final String INVALID_INPUT_PROVIDED = "Invalid input Provided .";

	@Autowired
	private ChildPlanDtlService childPlanServiceDtl;

	private static final Logger log = Logger.getLogger(ChildPlanDtlController.class);

	/**
	 * 
	 * Method Name: getChildPlanDtl Method Description:This Method is used to
	 * get the child plan Details based on the input .
	 * 
	 * @param childPlanDtlReq
	 * @return
	 */
	@ApiOperation(value = "Get Child Plan detail response", tags = { "childplan" })
	@RequestMapping(value = "/getChildPlanDtl", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ChildPlanDtlRes getChildPlanDtl(@RequestBody ChildPlanDtlReq childPlanDtlReq) {
		if (ObjectUtils.isEmpty(childPlanDtlReq) || ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto())
				|| ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto())) {
			throw new InvalidRequestException(INVALID_INPUT_PROVIDED);
		}
		if (!ObjectUtils.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto())
				&& ObjectUtils
						.isEmpty(childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdCase())
				|| ObjectUtils.isEmpty(
						childPlanDtlReq.getChildPlanOfServiceDto().getChildPlanOfServiceDtlDto().getIdStage())) {
			throw new InvalidRequestException(INVALID_INPUT_PROVIDED);
		}

		return childPlanServiceDtl.getChildPlanDtl(childPlanDtlReq);

	}

	/**
	 * 
	 * Method Name: saveChildPlanDtl Method Description:This Method is used to
	 * save the child plan Details based on the input .
	 * 
	 * @param childPlanDtlReq
	 * @return
	 */
	@RequestMapping(value = "/saveChildPlanDtl", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public ChildPlanDtlRes saveChildPlanDtl(@RequestBody ChildPlanDtlReq childPlanDtlReq) {

		log.debug("Entering method queryChildPlan in ChildPlanDtlController");
		ChildPlanDtlRes resp = null;
		try {
			resp = childPlanServiceDtl.saveChildPlanDtl(childPlanDtlReq);
		} catch (ServiceLayerException e) {
			if (!ObjectUtils.isEmpty(e.getErrorCode())
					&& ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH == e.getErrorCode().intValue()) {
				throw new TimeMismatchException();
			} else {
				throw e;
			}
		}
		return resp;

	}

	/**
	 * 
	 * Method Name: getChildPlanDtlForForm Method Description:This Method is
	 * used to get the child plan Form based on the input .
	 * 
	 * @param childPlanDtlReq
	 * @return CommonFormRes
	 */
	@RequestMapping(value = "/getChildPlanDtlForForm", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getChildPlanDtlForForm(@RequestBody ChildPlanDtlReq childPlanDtlReq) {
		if (ObjectUtils.isEmpty(childPlanDtlReq) || ObjectUtils.isEmpty(childPlanDtlReq.getIdCase())
				|| ObjectUtils.isEmpty(childPlanDtlReq.getIdEvent())
				|| ObjectUtils.isEmpty(childPlanDtlReq.getIdStage())) {
			throw new InvalidRequestException(INVALID_INPUT_PROVIDED);
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes
				.setPreFillData(TypeConvUtil.getXMLFormat(childPlanServiceDtl.getChildPlanDtlForm(childPlanDtlReq)));

		return commonFormRes;
	}

	/**
	 * Method Name: deleteChildPlanDtl Method Description:This Method is used to
	 * delete the child plan Details based on the input .
	 * 
	 * @param childPlanDtlReq
	 * @return
	 */
	@RequestMapping(value = "/deleteChildPlanDtl", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ChildPlanDtlRes deleteChildPlanDtl(@RequestBody ChildPlanDtlReq childPlanDtlReq) {

		return childPlanServiceDtl.deleteChildPlanDtl(childPlanDtlReq);
	}

	@RequestMapping(value = "/saveAndSubmitChildPlanDtl", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ChildPlanDtlRes saveAndSubmitChildPlanDtl(@RequestBody ChildPlanDtlReq childPlanDtlReq) {
		ChildPlanDtlRes resp = null;
		try {
			resp = childPlanServiceDtl.saveAndSubmitChildPlanDtl(childPlanDtlReq);
		} catch (ServiceLayerException e) {
			if (!ObjectUtils.isEmpty(e.getErrorCode())
					&& ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH == e.getErrorCode().intValue()) {
				throw new TimeMismatchException();
			} else {
				throw e;
			}
		}
		return resp;
	}

	/**
	 * Method Name: alertreadyforreview
	 * 
	 * Method Description:This Method is used to create an alert to the primary
	 * worker when the external user clicks ready for review
	 * 
	 * 
	 * @param childPlanDtlReq
	 * @return
	 */
	@RequestMapping(value = "/alertreadyforreview", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonIdRes alertReadyForReview(@RequestBody ChildPlanDtlReq childPlanDtlReq) {

		return childPlanServiceDtl.alertReadyForReview(childPlanDtlReq);
	}

}
