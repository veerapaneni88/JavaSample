package us.tx.state.dfps.service.legal.controller;

import java.util.List;
import java.util.Locale;

import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.LegalActRtrvReq;
import us.tx.state.dfps.service.common.response.LegalActRtrvRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.legal.dto.CaseDbDto;
import us.tx.state.dfps.service.legal.dto.RetrvOutDto;
import us.tx.state.dfps.service.legal.service.LegalActionRetrievalService;
import us.tx.state.dfps.service.sdmriskassessment.dto.StageDBDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * implements the methods declared in Csub38sBean Oct 30, 2017- 4:21:06 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/legalActionOutcomeRetrieval")
public class LegalActionRetrievalController {

	@Autowired
	private LegalActionRetrievalService legalActionRetrievalService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(LegalActionRetrievalController.class);

	/**
	 * 
	 * Method Name: legalActionOutcomeRtrv Method Description:This is the
	 * retrieval service for the Legal Action/Outcome window. Tuxedo : CSUB38S
	 * 
	 * @param legalActRtrvReq
	 * @return LegalActRtrvRes
	 */
	@ApiOperation(value = "Get Legal Actions Info", tags = { "Legal-Service" })
	@RequestMapping(value = "/fetchLegalAction", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  LegalActRtrvRes legalActionOutcomeRtrv(@RequestBody LegalActRtrvReq legalActRtrvReq){
		log.debug("Entering method legalActionOutcomeRtrv in LegalActionRetrievalController");
		if (TypeConvUtil.isNullOrEmpty(legalActRtrvReq.getRetrvInDto())) {
			throw new InvalidRequestException(messageSource.getMessage("RetrvInDto.is.mandatory", null, Locale.US));
		}
		RetrvOutDto retrvOutDto = legalActionRetrievalService.legalActionOutcomeRtrv(legalActRtrvReq.getRetrvInDto());
		LegalActRtrvRes legalActRtrvRes = new LegalActRtrvRes();
		legalActRtrvRes.setTransactionId(legalActRtrvReq.getTransactionId());
		log.info("TransactionId :" + legalActRtrvReq.getTransactionId());
		legalActRtrvRes.setRetrvOutDto(retrvOutDto);
		log.debug("Exiting method legalActionOutcomeRtrv in LegalActionRetrievalController");
		return legalActRtrvRes;
	}

	/**
	 * 
	 * Method Name: fetchStageDBAndPrincipals Method Description:This is the
	 * retrieval for stages and related principals for the case.
	 * 
	 * @param ulIdCase
	 * @return LegalActRtrvRes
	 */
	@RequestMapping(value = "/fetchStageDBAndPrincipals", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  LegalActRtrvRes fetchStageDBAndPrincipals(@RequestBody LegalActRtrvReq legalActRtrvReq){
		log.debug("Entering method fetchStageDBAndPrincipals in LegalActionRetrievalController");
		if (TypeConvUtil.isNullOrEmpty(legalActRtrvReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("UlIdCase.is.mandatory", null, Locale.US));
		}

		List<StageDBDto> stageDbDtoList = legalActionRetrievalService
				.fetchStageDBAndPrincipals(legalActRtrvReq.getIdCase());
		LegalActRtrvRes legalActRtrvRes = new LegalActRtrvRes();
		legalActRtrvRes.setTransactionId(legalActRtrvReq.getTransactionId());
		log.info("TransactionId :" + legalActRtrvReq.getTransactionId());
		legalActRtrvRes.setStageDbDtoList(stageDbDtoList);
		log.debug("Exiting method fetchStageDBAndPrincipals in LegalActionRetrievalController");
		return legalActRtrvRes;
	}

	/**
	 * 
	 * Method Name: getCaseSummary Method Description:This is the retrieval of
	 * Case with all stages info.
	 * 
	 * @param legalActRtrvReq
	 * @return LegalActRtrvRes
	 */
	@RequestMapping(value = "/getCaseSummary", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  LegalActRtrvRes getCaseSummary(@RequestBody LegalActRtrvReq legalActRtrvReq) {
		log.debug("Entering method getCaseSummary in LegalActionRetrievalController");
		if (TypeConvUtil.isNullOrEmpty(legalActRtrvReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("UlIdCase.is.mandatory", null, Locale.US));
		}
		CaseDbDto caseDbDto = legalActionRetrievalService.getCaseSummary(legalActRtrvReq.getIdCase());
		LegalActRtrvRes legalActRtrvRes = new LegalActRtrvRes();
		legalActRtrvRes.setTransactionId(legalActRtrvReq.getTransactionId());
		log.info("TransactionId :" + legalActRtrvReq.getTransactionId());
		legalActRtrvRes.setCaseDbDto(caseDbDto);
		log.debug("Exiting method getCaseSummary in LegalActionRetrievalController");
		return legalActRtrvRes;
	}
}
