package us.tx.state.dfps.service.sscc.controller;

import java.util.Locale;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.SSCCExceptCareDesiReq;
import us.tx.state.dfps.service.common.response.SSCCExceptCareDesiRes;
import us.tx.state.dfps.service.sscc.service.SSCCExceptCareDesignationService;

@RestController
@Api(tags = { "referral", "ssccexceptioncare" })
@RequestMapping("/ssccExceptCareDesignation")
public class SSCCExceptCareDesignationController {
	@Autowired
	MessageSource messageSource;

	@Autowired
	SSCCExceptCareDesignationService ssccExceptCareDesignationService;

	/**
	 * 
	 * Method Description: Get eligibility placement information for stage id.
	 * Service Name: getEligibilityPlcmtInfo
	 * 
	 * @param sSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes
	 */

	@RequestMapping(value = "/getEligibilityPlcmtInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SSCCExceptCareDesiRes getEligibilityPlcmtInfo(@RequestBody SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		if (ObjectUtils.isEmpty(sSCCExceptCareDesiReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		SSCCExceptCareDesiRes ssccExceptCareDesiRes = ssccExceptCareDesignationService
				.getEligibilityPlcmtInfo(sSCCExceptCareDesiReq);
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

	@RequestMapping(value = "/fetchSSCCResourceInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SSCCExceptCareDesiRes fetchSSCCResourceInfo(@RequestBody SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		if (ObjectUtils.isEmpty(sSCCExceptCareDesiReq.getSsccResourceDto())
				&& ObjectUtils.isEmpty(sSCCExceptCareDesiReq.getSsccResourceDto().getCdSSCCCntrctRegion())
				&& ObjectUtils.isEmpty(sSCCExceptCareDesiReq.getSsccResourceDto().getIdSSCCCatchment())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		SSCCExceptCareDesiRes ssccExceptCareDesiRes = ssccExceptCareDesignationService
				.fetchSSCCResourceInfo(sSCCExceptCareDesiReq);
		return ssccExceptCareDesiRes;
	}

	/**
	 * 
	 * Method Description: Get an except care desig record and This method is
	 * used to gets SSCC Resource and Contract information. Service Name:
	 * getExistECDesig & getSsccRsrcContractInfo
	 * 
	 * @param sSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes
	 */

	@RequestMapping(value = "/getExistECDesigAndSsccRsrcContractInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SSCCExceptCareDesiRes getExistECDesigAndSsccRsrcContractInfo(
			@RequestBody SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {

		SSCCExceptCareDesiRes ssccExceptCareDesiRes = ssccExceptCareDesignationService
				.getExistECDesigAndSsccRsrcContractInfo(sSCCExceptCareDesiReq);
		return ssccExceptCareDesiRes;
	}

	/**
	 * 
	 * Method Description: Get an except care desig record list Service Name:
	 * getSSCCExpCareList
	 * 
	 * @param sSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes
	 */
	@ApiOperation(value = "Get SSCC Extensive Care List", tags = { "ssccexceptioncare" })
	@RequestMapping(value = "/getSSCCExpCareList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCExceptCareDesiRes getSSCCExpCareList(
			@RequestBody SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		SSCCExceptCareDesiRes ssccExceptCareDesiRes = new SSCCExceptCareDesiRes();
		ssccExceptCareDesiRes = ssccExceptCareDesignationService.getSSCCExpCareServiceList(sSCCExceptCareDesiReq);
		return ssccExceptCareDesiRes;
	}

	/**
	 * 
	 * Method Description: Insert a new record into SSCC_EXCEPTIONAL_CARE_DESIG
	 * or Updates a record from the same table. Service Name: getSSCCExpCareList
	 * 
	 * @param sSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes
	 */

	@RequestMapping(value = "/saveOrUpdateExceptCareDesig", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCExceptCareDesiRes saveOrUpdateExceptCareDesig(
			@RequestBody SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		if (ObjectUtils.isEmpty(sSCCExceptCareDesiReq.getSsccExcpCareDesDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		SSCCExceptCareDesiRes ssccExceptCareDesiRes = ssccExceptCareDesignationService
				.saveOrUpdateExceptCareDesig(sSCCExceptCareDesiReq);
		return ssccExceptCareDesiRes;
	}

	/**
	 * 
	 * Method Description: Get except care before save and approve Service Name:
	 * getExcpCareOnSaveAndApprove
	 * 
	 * @param sSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes
	 */

	@RequestMapping(value = "/getExcpCareOnSaveAndApprove", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SSCCExceptCareDesiRes getExcpCareOnSaveAndApprove(@RequestBody SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		if (ObjectUtils.isEmpty(sSCCExceptCareDesiReq.getIdECDesig())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		SSCCExceptCareDesiRes ssccExceptCareDesiRes = ssccExceptCareDesignationService
				.getExcpCareOnSaveAndApprove(sSCCExceptCareDesiReq);
		return ssccExceptCareDesiRes;
	}

	/**
	 * 
	 * Method Description: Method fetchs a list of Timeline records for a stage
	 * Service Name: getSSCCTimelineList
	 * 
	 * @param sSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes
	 */


	@ApiOperation(value = "Get SSCC Timeline list", tags = { "referral" })
	@RequestMapping(value = "/getSSCCTimelineList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SSCCExceptCareDesiRes getSSCCTimelineList(@RequestBody SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		if (ObjectUtils.isEmpty(sSCCExceptCareDesiReq.getSsccTimelineDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		SSCCExceptCareDesiRes ssccExceptCareDesiRes = ssccExceptCareDesignationService
				.getSSCCTimelineList(sSCCExceptCareDesiReq);
		return ssccExceptCareDesiRes;
	}

	/**
	 * 
	 * Method Description: GThis method updates the sscc list Service Name:
	 * updateStatus
	 * 
	 * @param sSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes
	 */
	@ApiOperation(value = "Rescind SSCC Exceptional Care List", tags = { "ssccexceptioncare" })
	@RequestMapping(value = "/rscIndUpdateStatus", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCExceptCareDesiRes updateStatus(@RequestBody SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		SSCCExceptCareDesiRes ssccExceptCareDesiRes = new SSCCExceptCareDesiRes();
		ssccExceptCareDesiRes = ssccExceptCareDesignationService.getUpdateSSCCExceptCare(sSCCExceptCareDesiReq);
		return ssccExceptCareDesiRes;
	}

	/**
	 * 
	 * Method Description: Gets list of all exceptional care records that linked
	 * to an sscc placement. Service Name: getEligibilityPlcmtInfo
	 * 
	 * @param sSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes
	 */

	@RequestMapping(value = "/getExceptCareList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SSCCExceptCareDesiRes getExceptCareList(@RequestBody CommonHelperReq commonHelperReq) {
		if (ObjectUtils.isEmpty(commonHelperReq.getIdCase()) && ObjectUtils.isEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		SSCCExceptCareDesiRes ssccExceptCareDesiRes = ssccExceptCareDesignationService
				.getExceptCareList(commonHelperReq);
		return ssccExceptCareDesiRes;
	}

	/**
	 * 
	 * Method Description: Method inserts a timeline record into the
	 * sscc_timeline table Service Name: insertTimeLine
	 * 
	 * @param sSCCExceptCareDesiReq
	 * @return ssccExceptCareDesiRes
	 */

	@RequestMapping(value = "/insertTimeLine", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCExceptCareDesiRes insertTimeLine(
			@RequestBody SSCCExceptCareDesiReq sSCCExceptCareDesiReq) {
		if (ObjectUtils.isEmpty(sSCCExceptCareDesiReq.getSsccTimelineDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		SSCCExceptCareDesiRes ssccExceptCareDesiRes = ssccExceptCareDesignationService
				.insertTimeLine(sSCCExceptCareDesiReq);
		return ssccExceptCareDesiRes;
	}

}
