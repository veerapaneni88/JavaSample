package us.tx.state.dfps.service.hsegh.controller;

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
import us.tx.state.dfps.service.common.request.HseghReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.hsegh.service.HseghService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:HseghController will have all operation which are mapped to HSEGH
 * module Feb 22, 2018- 1:58:51 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@RestController
@RequestMapping("/hsegh")
public class HseghController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	HseghService hseghService;

	private static final Logger logger = Logger.getLogger(HseghController.class);

	/**
	 * 
	 * Method Name: getHsegh Method Description:This method is used to retrieve
	 * the information for HSEGH form by passing IdStage as input request
	 * 
	 * @param hseghReq
	 * @return
	 */
	@RequestMapping(value = "/gethsegh", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getHsegh(@RequestBody HseghReq hseghReq) {

		if (TypeConvUtil.isNullOrEmpty(hseghReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(hseghService.getHsegh(hseghReq)));
		logger.info("TransactionId :" + hseghReq.getTransactionId());

		return commonFormRes;

	}

}
