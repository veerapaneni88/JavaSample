package us.tx.state.dfps.service.afistatement.controller;

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
import us.tx.state.dfps.service.afistatement.service.AFIStatementService;
import us.tx.state.dfps.service.common.request.AFIStatementReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * ApsFacilityInvestigationsStatement for form civ39o00 Mar 14, 2018- 11:13:02
 * AM © 2017 Texas Department of Family and Protective Services
 */

@RestController
@RequestMapping("/afistatement")
public class AFIStatementController {

	@Autowired
	AFIStatementService aFIStatementService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger("ServiceBusiness-AFIStatementControllerLog");

	/**
	 * 
	 * Method Description: Populates APS FACILITY INVESTIGATIONS STATEMENT form
	 * Name: civ39o00
	 * 
	 * @param aFIStatementReq
	 * @return commonFormRes
	 */

	@RequestMapping(value = "/getafistatement", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getAFIStatement(@RequestBody AFIStatementReq aFIStatementReq) {
		if (ObjectUtils.isEmpty(aFIStatementReq.getIdStage()))
			throw new InvalidRequestException(
					messageSource.getMessage("aFIStatementReq.idStage.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(aFIStatementReq.getIdEvent()))
			throw new InvalidRequestException(
					messageSource.getMessage("aFIStatementReq.IdEvent.mandatory", null, Locale.US));

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(aFIStatementService.getStatement(aFIStatementReq)));
		log.info("TransactionId :" + aFIStatementReq.getTransactionId());

		return commonFormRes;
	}

}
