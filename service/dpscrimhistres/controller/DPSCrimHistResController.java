package us.tx.state.dfps.service.dpscrimhistres.controller;

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
import us.tx.state.dfps.service.common.request.DPSCrimHistResReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.dpscrimhistres.service.DPSCrimHistResService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: controller
 * class for form ccf12o00 Apr 30, 2018- 5:00:36 PM © 2017 Texas Department of
 * Family and Protective Services
 */

@RestController
@RequestMapping("/dpscrimhistres")
public class DPSCrimHistResController {

	@Autowired
	DPSCrimHistResService dPSCrimHistResService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger("ServiceBusiness-DPSCrimHistResLog");

	/**
	 * 
	 * Method Description: Populates DPS Criminal History Results form Name:
	 * ccf12o00
	 * 
	 * @param DPSCrimHistResReq
	 * @return commonFormRes
	 */

	@RequestMapping(value = "/getdpscrimhistres", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getCrimHistRes(@RequestBody DPSCrimHistResReq dPSCrimHistResReq) {
		if (ObjectUtils.isEmpty(dPSCrimHistResReq.getIdCase()))
			throw new InvalidRequestException(
					messageSource.getMessage("dPSCrimHistResReq.IdCase.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(dPSCrimHistResReq.getIdCrimHist()))
			throw new InvalidRequestException(
					messageSource.getMessage("dPSCrimHistResReq.IdCrimHist.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(dPSCrimHistResReq.getIdPerson()))
			throw new InvalidRequestException(
					messageSource.getMessage("dPSCrimHistResReq.IdPerson.mandatory", null, Locale.US));

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes
				.setPreFillData(TypeConvUtil.getXMLFormat(dPSCrimHistResService.getCrimHistRes(dPSCrimHistResReq)));
		log.info("TransactionId :" + dPSCrimHistResReq.getTransactionId());

		return commonFormRes;
	}

}
