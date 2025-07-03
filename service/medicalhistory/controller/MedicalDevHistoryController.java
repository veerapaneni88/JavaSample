package us.tx.state.dfps.service.medicalhistory.controller;

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
import us.tx.state.dfps.service.common.request.MedicalDevHistoryReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.medicalhistory.service.MedicalDevHistoryService;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Description:
 * MedicalDevHistoryController will have the operation which are mapped to
 * Medical and Developmental History. Jan 29, 2018 - 11:30:29 AM
 */
@RestController
@RequestMapping("/medicalDevHistory")
public class MedicalDevHistoryController {
	@Autowired
	MedicalDevHistoryService medicalDevHistoryService;

	@Autowired
	MessageSource messageSource;

	private static final Logger logger = Logger.getLogger(MedicalDevHistoryController.class);

	/**
	 * Tuxedo Service Name: CSUB73S. Method Description: This Service is used to
	 * retrieve the Medical and Developmental History form. This form fully
	 * documents the Medical and Developmental History by passing IdStage as
	 * input request
	 * 
	 * @param medicalDevHistoryReq
	 * @return CommonFormRes
	 */
	@RequestMapping(value = "/getMedicalDevHistory", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getMedicalDevHistory(@RequestBody MedicalDevHistoryReq medicalDevHistoryReq){
		if (TypeConvUtil.isNullOrEmpty(medicalDevHistoryReq.getIdStage()))
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(medicalDevHistoryService.getMedicalDevHistory(medicalDevHistoryReq)));
		logger.info("TransactionId :" + medicalDevHistoryReq.getTransactionId());
		return commonFormRes;
	}
}
