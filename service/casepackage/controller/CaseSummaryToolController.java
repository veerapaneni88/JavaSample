/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jun 6, 2018- 2:10:45 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.casepackage.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.casepackage.service.CaseSummaryToolService;
import us.tx.state.dfps.service.common.request.CaseSumToolReq;
import us.tx.state.dfps.service.common.response.CaseSumToolRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:The Service
 * Controller for Case Summary Tool. Jun 6, 2018- 2:10:45 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/casesumtool")
public class CaseSummaryToolController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	CaseSummaryToolService caseSummaryToolService;

	/**
	 * 
	 * Method Name: getCaseSumToolPersonList Method Description: To retrieve the
	 * person list on the Case Summary Tool page.
	 * 
	 * @param caseSumToolReq
	 * @return caseSumToolRes
	 */
	@RequestMapping(value = "/getcasesumtoolpersonlist", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CaseSumToolRes getCaseSumToolPersonList(@RequestBody CaseSumToolReq caseSumToolReq) {
		if (ObjectUtils.isEmpty(caseSumToolReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		CaseSumToolRes caseSumToolRes = new CaseSumToolRes();
		caseSumToolRes = caseSummaryToolService.getCaseSumToolPersonList(caseSumToolReq.getIdStage());
		return caseSumToolRes;
	}
}
