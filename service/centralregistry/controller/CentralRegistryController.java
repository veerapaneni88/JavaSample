package us.tx.state.dfps.service.centralregistry.controller;

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
import us.tx.state.dfps.service.centralregistry.service.CentralRegistryService;
import us.tx.state.dfps.service.common.request.CentralRegistryReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CentralRegistryController will have all operation which are
 * mapped to CentralRegistry module May 2, 2018- 1:58:51 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/centralregistry")
public class CentralRegistryController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	CentralRegistryService centralRegistryService;

	private static final Logger logger = Logger.getLogger(CentralRegistryController.class);

	/**
	 * 
	 * Method Name: getCentralRegistryInfo Method Description: This method is
	 * used to retrieve the information for Central registry form by passing
	 * IdPerson as input request
	 * 
	 * @param centralRegistryReq
	 * @return
	 */
	@RequestMapping(value = "/getcentralregistryinfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getCentralRegistryInfo(@RequestBody CentralRegistryReq centralRegistryReq) {

		if (TypeConvUtil.isNullOrEmpty(centralRegistryReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(centralRegistryService.getCentralRegistryInfo(centralRegistryReq)));
		logger.info("TransactionId :" + centralRegistryReq.getTransactionId());

		return commonFormRes;

	}
}
