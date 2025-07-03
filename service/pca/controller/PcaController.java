package us.tx.state.dfps.service.pca.controller;

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
import us.tx.state.dfps.service.common.request.PcaApplicationReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.pca.service.PcaService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PcaController will have all operation which are mapped to PCA
 * module Feb 9, 2018- 1:58:51 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@RestController
@RequestMapping("/pca")
public class PcaController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	PcaService pcaService;

	private static final Logger logger = Logger.getLogger(PcaController.class);

	/**
	 * Tuxedo Service Name: CSUB86S. Method Description: This method is used to
	 * retrieve the information for ADOPTION ASSISTANCE/PCA DENIAL LETTER by
	 * passing IdStage and IdPerson as input request
	 * 
	 * @param pcaApplicationReq
	 * @return PcaApplicationRes
	 */
	@RequestMapping(value = "/getDenailLetter", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getDenailLetter(@RequestBody PcaApplicationReq pcaApplicationReq) {

		if (TypeConvUtil.isNullOrEmpty(pcaApplicationReq.getIdStage()))
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		// Commented the code as it might be needed in coming releases
		/*
		 * if (TypeConvUtil.isNullOrEmpty(pcaApplicationReq.getIdPerson()))
		 * throw new InvalidRequestException(messageSource.getMessage(
		 * "common.personid.mandatory", null, Locale.US));
		 */
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(pcaService.getDenailLetter(pcaApplicationReq)));
		logger.info("TransactionId :" + pcaApplicationReq.getTransactionId());
		return commonFormRes;
	}

}
