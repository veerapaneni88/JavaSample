package us.tx.state.dfps.service.person.controller;

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
import us.tx.state.dfps.service.common.request.PersonDtlReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.person.service.PersonComparisonService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:controller
 * for PER03O00 Jun 4, 2018- 4:01:33 PM © 2017 Texas Department of Family and
 * Protective Services
 */

@RestController
@RequestMapping("/person")
public class PersonComparisonController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	PersonComparisonService personComparisonService;

	private static final Logger log = Logger.getLogger(PersonComparisonService.class);

	/**
	 * 
	 * Method Description: Person Comparison Form form Name: per03o00
	 * 
	 * @param personDtlReq
	 * @return commonFormRes
	 */

	@RequestMapping(value = "/getpersoncomparison", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getPersonComparison(@RequestBody PersonDtlReq personDtlReq){
		if (ObjectUtils.isEmpty(personDtlReq.getIdPerson()))
			throw new InvalidRequestException(
					messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(personDtlReq.getIdPersonId()))
			throw new InvalidRequestException(
					messageSource.getMessage("common.personid.mandatory", null, Locale.US));

		log.info("TransactionId :" + personDtlReq.getTransactionId());

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes
				.setPreFillData(TypeConvUtil.getXMLFormat(personComparisonService.getPersonComparison(personDtlReq)));
		return commonFormRes;
	}

}
