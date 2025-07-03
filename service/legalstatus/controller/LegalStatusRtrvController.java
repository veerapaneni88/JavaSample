package us.tx.state.dfps.service.legalstatus.controller;

import java.util.Locale;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.SSCCExceptCareDesignationDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.phoneticsearch.IIRHelper.Messages;
import us.tx.state.dfps.service.admin.dto.*;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.LegalStatusUpdateReq;
import us.tx.state.dfps.service.common.request.SSCCExceptCareRequest;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.LatestLegalStatusRes;
import us.tx.state.dfps.service.common.response.LegalStatusRtrvoRes;
import us.tx.state.dfps.service.common.response.SSCCExceptCareResponse;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.legalstatus.service.LegalStatusService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This is the
 * service controller class for legal status detail> Mar 13, 2018- 8:15:28 PM ©
 * 2017 Texas Department of Family and Protective Services.
 */
@RestController
@Api(tags = { "legalStatus" })
@RequestMapping("/legalstatusrtrv")
public class LegalStatusRtrvController {

	/** The legal status service. */
	@Autowired
	LegalStatusService legalStatusService;

	/** The message source. */
	@Autowired
	MessageSource messageSource;

	/** The Constant log. */
	private static final Logger log = Logger.getLogger(LegalStatusRtrvController.class);

	/**
	 * Method Name: getLegalStatus Method Description :This is the retrieval
	 * service for Legal Status which will get legal status detail .
	 *
	 * @param objLegalStatusRtrviDto
	 *            the obj legal status rtrvi dto
	 * @return LegalStatusRtrvoRes
	 */
	@ApiOperation(value = "Get Legal Status Info", tags = { "legalStatus" })
	@RequestMapping(value = "/getLegalStatusDtl", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public LegalStatusRtrvoRes getLegalStatus(@RequestBody LegalStatusRtrviDto objLegalStatusRtrviDto) {
		LegalStatusRtrvoRes objLegalStatusRtrvoRes = new LegalStatusRtrvoRes();
		log.info("Entering method getLegalStatus in LegalStatusRtrvController");
		if (TypeConvUtil.isNullOrEmpty(objLegalStatusRtrviDto.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("legal.status.rtrv.stageId", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(objLegalStatusRtrviDto.getCdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("legal.status.rtrv.cdStage", null, Locale.US));
		}
		LegalStatusRtrvoDto legalStatusRtrvoDto = legalStatusService.getLegalStatusDetails(objLegalStatusRtrviDto);
		objLegalStatusRtrvoRes.setLegalStatusRtrvoDto(legalStatusRtrvoDto);
		log.info("Exiting method getLegalStatus in LegalStatusRtrvController");
		return objLegalStatusRtrvoRes;
	}


	/**
	 * Method Name: getPersonId Method Description :This is the retrieval
	 * service for Legal Status which will get Person id .
	 *
	 * @param Long
	 *
	 * @return Long
	 */
	@GetMapping(value = "/getPersonDetails/{idStage}")
	public Long getPersonDetails(@PathVariable(value = "idStage") Long idStage) {
		log.info("Entering method getPersonDetails in LegalStatusRtrvController");
		if (idStage == null) {
			throw new InvalidRequestException(messageSource.getMessage("legal.status.idStage.mandatory", null, Locale.US));
		}
		return legalStatusService.getLegalStatusPersonDetail(idStage);
	}

	/**
	 * Method Name: saveLegalStatus Method Description: This method will add ,
	 * delete, update legal status detail.
	 *
	 * @param legalStatusUpdateReq
	 *            the legal status update req
	 * @return the legal status rtrvo res
	 */
	@RequestMapping(value = "/updatelegalstatus", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public LegalStatusRtrvoRes saveLegalStatus(@RequestBody LegalStatusUpdateReq legalStatusUpdateReq) {
		LegalStatusRtrvoRes objLegalStatusRtrvoRes = new LegalStatusRtrvoRes();
		ErrorDto errorDto = null;
		log.info("Entering method saveLegalStatus in LegalStatusRtrvController");
		try {

			int errorCode = legalStatusService.updateLegalStatus(legalStatusUpdateReq);

			if (errorCode != 0) {
				errorDto = new ErrorDto();
				errorDto.setErrorCode(errorCode);

			}

		} catch (ServiceLayerException e) {
			errorDto = new ErrorDto();
			errorDto.setErrorCode(Messages.MSG_CMN_TMSTAMP_MISMATCH);
		}
		objLegalStatusRtrvoRes.setErrorDto(errorDto);
		log.info("Exiting method saveLegalStatus in LegalStatusRtrvController");
		return objLegalStatusRtrvoRes;
	}

	/**
	 * Method Name: getIndLegalStatMissing Method Description: This method will
	 * get LegalStatMissing flag.
	 *
	 * @param ssccExceptCareReq
	 *            the sscc except care req
	 * @return the ind legal stat missing
	 */
	@RequestMapping(value = "/getIndLegalStatMissing", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SSCCExceptCareResponse getIndLegalStatMissing(@RequestBody SSCCExceptCareRequest ssccExceptCareReq) {
		log.info("Entering method getIndLegalStatMissing in LegalStatusRtrvController");

		SSCCExceptCareResponse response = new SSCCExceptCareResponse();
		SSCCExceptCareDesignationDto ssccDto = legalStatusService
				.getIndLegalStatMissing(ssccExceptCareReq.getIdReferral());
		response.setSsccExceptCareDesiDto(ssccDto);
		log.info("Exit method getIndLegalStatMissing in LegalStatusRtrvController");
		return response;
	}

	/**
	 * Method Name: updateIndLegalStatMissing Method Description:This method
	 * will update LegalStatusMissing flag.
	 *
	 * @param ssccExceptCareReq
	 *            the sscc except care req
	 * @return the SSCC except care response
	 */
	@RequestMapping(value = "/updateIndLegalStatMissing", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SSCCExceptCareResponse updateIndLegalStatMissing(@RequestBody SSCCExceptCareRequest ssccExceptCareReq) {
		log.info("Entering method getIndLegalStatMissing in LegalStatusRtrvController");
		if (TypeConvUtil.isNullOrEmpty(ssccExceptCareReq.getIdReferral())) {
			throw new InvalidRequestException(
					messageSource.getMessage("legal.status.referralId.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(ssccExceptCareReq.getIndLegalStateMissing())) {
			throw new InvalidRequestException(
					messageSource.getMessage("legal.status.indLegalStateMissing.mandatory", null, Locale.US));
		}
		SSCCExceptCareResponse response = new SSCCExceptCareResponse();
		legalStatusService.updateSsccIndLegalStatus(ssccExceptCareReq.getIndLegalStateMissing(),
				ssccExceptCareReq.getIdReferral());
		log.info("Exit method getIndLegalStatMissing in LegalStatusRtrvController");
		return response;
	}

	/**
	 * Select latest legal action sub type.
	 *
	 * @param legalActionEventInDto
	 *            the legal action event in dto
	 * @return the common string res
	 */
	@RequestMapping(value = "/latestlegalActionSubtype", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonStringRes selectLatestLegalActionSubType(@RequestBody LegalActionEventInDto legalActionEventInDto) {
		if (TypeConvUtil.isNullOrEmpty(legalActionEventInDto.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(legalActionEventInDto.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(legalActionEventInDto.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(legalActionEventInDto.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("legal.status.legalActionType.mandatory", null, Locale.US));
		}
		return legalStatusService.selectLatestLegalActionSubType(legalActionEventInDto);
	}

	@RequestMapping(value = "/selectLatestLegalStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public LatestLegalStatusRes selectLatestLegalStatus(@RequestBody LegalActionEventInDto legalActionEventInDto) {
		LatestLegalStatusRes resp = new LatestLegalStatusRes();
		if (TypeConvUtil.isNullOrEmpty(legalActionEventInDto.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(legalActionEventInDto.getCdLegalStatStatus())) {
			throw new InvalidRequestException(
					messageSource.getMessage("legal.status.cdLegalStatStatus.mandatory", null, Locale.US));
		}

		LegalStatusDetailDto legalStatusDetailDto = legalStatusService.selectLatestLegalStatus(legalActionEventInDto);
		resp.setLegalStatusDetailDto(legalStatusDetailDto);
		return resp;
	}

	//PPM 77834 – FCL CLASS Webservice for Data Exchange
	@ApiOperation(value = "Get Legal Status", tags = { "legalStatus" })
	@RequestMapping(value = "/latestLegalStatusByPersonId", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public LatestLegalStatusRes getLatestLegalStatus(@RequestBody CommonHelperReq commonHelperReq) {
		LatestLegalStatusRes resp = new LatestLegalStatusRes();
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		LegalStatusDetailDto legalStatusDetailDto = legalStatusService.getLatestLegalStatusByPersonId(commonHelperReq.getIdPerson());
		resp.setLegalStatusDetailDto(legalStatusDetailDto);
		return resp;
	}

	@RequestMapping(value = "/latestLegalStatusInfoByPersonId", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public LatestLegalStatusRes getLatestLegalStatusInfo(@RequestBody CommonHelperReq commonHelperReq) {
		LatestLegalStatusRes resp = new LatestLegalStatusRes();
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		LegalStatusDetailDto legalStatusDetailDto = legalStatusService.getLatestLegalStatusInfoByPersonId(commonHelperReq.getIdPerson());
		resp.setLegalStatusDetailDto(legalStatusDetailDto);
		return resp;
	}

	@RequestMapping(value = "/latestLegalStatusInfoByEventId", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public LatestLegalStatusRes getLatestLegalStatusInfoByEventId(@RequestBody CommonHelperReq commonHelperReq) {
		LatestLegalStatusRes resp = new LatestLegalStatusRes();
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		LegalStatusDetailDto legalStatusDetailDto = legalStatusService.getLatestLegalStatusInfoByEventId(commonHelperReq.getIdEvent());
		resp.setLegalStatusDetailDto(legalStatusDetailDto);
		return resp;
	}

}
