package us.tx.state.dfps.service.prt.controller;

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

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.PpmReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.ppm.service.PparService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: controller
 * for PPM Information May 30, 2018- 10:29:03 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@RestController
@RequestMapping("/ppminformation")
public class PpaController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	PparService pparService;

	private static final Logger log = Logger.getLogger(PpaController.class);

	/**
	 * 
	 * Method Description: Permanency Planning Review Invited Parties and
	 * Participation Information form Name: csc39o00
	 * 
	 * @param ppmReq
	 * @return commonFormRes
	 */

	@RequestMapping(value = "/getppacasereview", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getPprForm(@RequestBody PpmReq ppmReq){
		if (ObjectUtils.isEmpty(ppmReq.getIdStage()))
			throw new InvalidRequestException(messageSource.getMessage("ppmReq.idStage.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(ppmReq.getIdEvent()))
			throw new InvalidRequestException(messageSource.getMessage("ppmReq.idEvent.mandatory", null, Locale.US));

		log.info("TransactionId :" + ppmReq.getTransactionId());

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(pparService.getPpaCaseReview(ppmReq)));
		return commonFormRes;
	}

	/**
	 * 
	 * Method Description: Permanency Planning Administrative Case Review
	 * Information form Name: csc30o00
	 * 
	 * @param ppmReq
	 * @return commonFormRes
	 */

	@RequestMapping(value = "/getppacasereviewadmin", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getPprAdminForm(@RequestBody PpmReq ppmReq){
		if (ObjectUtils.isEmpty(ppmReq.getIdStage()))
			throw new InvalidRequestException(messageSource.getMessage("ppmReq.idStage.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(ppmReq.getIdPptEvent()))
			throw new InvalidRequestException(messageSource.getMessage("ppmReq.idPptEvent.mandatory", null, Locale.US));

		log.info("TransactionId :" + ppmReq.getTransactionId());

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(pparService.getPpaCaseReviewAdmin(ppmReq)));
		return commonFormRes;
	}

}
