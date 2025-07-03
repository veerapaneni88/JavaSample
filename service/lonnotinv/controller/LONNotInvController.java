package us.tx.state.dfps.service.lonnotinv.controller;

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
import us.tx.state.dfps.service.common.request.LONNotInvReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.lonnotinv.service.LONNotInvService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: controller
 * for Letter of Notification - Not Investigated cinv87s Mar 22, 2018- 5:27:31
 * PM © 2017 Texas Department of Family and Protective Services
 */

@RestController
@RequestMapping("/lonnotinv")
public class LONNotInvController {

	@Autowired
	LONNotInvService lONNotInvService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger("LONNotInvControllerLog.class");

	/**
	 * 
	 * Method Description: Populates Letter of Notification - Not Investigated
	 * form Name: cfiv2100
	 * 
	 * @param LONNotInvReq
	 * @return commonFormRes
	 */

	@RequestMapping(value = "/getlonnotinv", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getLONNotInv(@RequestBody LONNotInvReq lONNotInvReq) {
		if (ObjectUtils.isEmpty(lONNotInvReq.getIdStage()))
			throw new InvalidRequestException(
					messageSource.getMessage("LONNotInvReq.idStage.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(lONNotInvReq.getIdPerson()))
			throw new InvalidRequestException(
					messageSource.getMessage("LONNotInvReq.idPerson.mandatory", null, Locale.US));

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(lONNotInvService.getLetter(lONNotInvReq)));
		log.info("TransactionId :" + lONNotInvReq.getTransactionId());

		return commonFormRes;
	}

}
