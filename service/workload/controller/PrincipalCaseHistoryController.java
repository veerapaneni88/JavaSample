package us.tx.state.dfps.service.workload.controller;

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
import us.tx.state.dfps.service.common.request.PrincipalCaseHistoryReq;
import us.tx.state.dfps.service.common.response.PrincipalCaseHistoryRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.workload.service.PrincipalCaseHistoryService;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION EJB Name: PrincipalCaseHistoryBean
 * Class Description: PrincipalCaseHistoryController will have all operation
 * which are mapped to PrincipalCaseHistory module. Aug 2, 2017 - 3:18:29 PM
 */
@RestController
@RequestMapping("/principalCaseHistory")
public class PrincipalCaseHistoryController {

	/** The principal case history service. */
	@Autowired
	PrincipalCaseHistoryService principalCaseHistoryService;

	/** The message source. */
	@Autowired
	MessageSource messageSource;

	/** The Constant log. */
	private static final Logger log = Logger.getLogger(PrincipalCaseHistoryController.class);

	/**
	 * Method Name: caseList Method Description: This method used to get Case
	 * List Information based on the given Case ID.
	 * 
	 * @param principalCaseHistoryReq
	 *            the principal case history req
	 * @return the principal case history res
	 */
	@RequestMapping(value = "/caseList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PrincipalCaseHistoryRes caseList(
			@RequestBody PrincipalCaseHistoryReq principalCaseHistoryReq) {
		if (TypeConvUtil.isNullOrEmpty(principalCaseHistoryReq.getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("principalcasehistory.caseid.mandatory", null, Locale.US));
		}
		PrincipalCaseHistoryRes principalCaseHistoryRes = principalCaseHistoryService.caseList(principalCaseHistoryReq);
		log.info("principalCaseHistoryRes: " + principalCaseHistoryRes);
		return principalCaseHistoryRes;
	}

	/**
	 ** Method Name: Select principal list. Method Description:This method used
	 * to get the Principal List info based on the user selected Radio Button on
	 * the page.
	 * 
	 * @param principalCaseHistoryReq
	 *            the principal case history req
	 * @return the principal case history res
	 */
	@RequestMapping(value = "/selectPrincipalList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PrincipalCaseHistoryRes selectPrincipalList(
			@RequestBody PrincipalCaseHistoryReq principalCaseHistoryReq) {
		if (TypeConvUtil.isNullOrEmpty(principalCaseHistoryReq.getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("principalcasehistory.caseid.mandatory", null, Locale.US));
		}
		PrincipalCaseHistoryRes principalCaseHistoryRes = principalCaseHistoryService
				.selectPrincipalList(principalCaseHistoryReq);
		log.info("principalCaseHistoryRes: " + principalCaseHistoryRes);
		return principalCaseHistoryRes;
	}

	/**
	 * Method Description: Method to to perform insert operations. This method
	 * will insert Parent Child relationship Checked Linked case information
	 * into CASE_LINK Table. EJB Name: PrincipalCaseHistoryBean
	 *
	 * @param principalCaseHistoryReq
	 *            the principal case history req
	 * @return PrincipalCaseHistoryRes
	 */
	@RequestMapping(value = "/insertCaseInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PrincipalCaseHistoryRes insertCaseInfo(
			@RequestBody PrincipalCaseHistoryReq principalCaseHistoryReq) {

		if (TypeConvUtil.isNullOrEmpty(principalCaseHistoryReq.getIdUser())) {
			throw new InvalidRequestException(
					messageSource.getMessage("principalcasehistory.userid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(principalCaseHistoryReq.getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("principalcasehistory.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(principalCaseHistoryReq.getIdLinkCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("principalcasehistory.linkcaseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(principalCaseHistoryReq.getIndicator())) {
			throw new InvalidRequestException(
					messageSource.getMessage("principalcasehistory.indicator.mandatory", null, Locale.US));
		}
		PrincipalCaseHistoryRes principalCaseHistoryRes = principalCaseHistoryService
				.insertCaseInfo(principalCaseHistoryReq);

		return principalCaseHistoryRes;
	}

	/**
	 * Method Description: Method to to perform update operations. This method
	 * will insert Parent Child relationship Checked Linked case information
	 * into CASE_LINK Table. EJB Name: PrincipalCaseHistoryBean
	 *
	 * @param principalCaseHistoryReq
	 *            the principal case history req
	 * @return PrincipalCaseHistoryRes
	 */
	@RequestMapping(value = "/updateCaseInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PrincipalCaseHistoryRes updateCaseInfo(
			@RequestBody PrincipalCaseHistoryReq principalCaseHistoryReq) {

		if (TypeConvUtil.isNullOrEmpty(principalCaseHistoryReq.getIdUser())) {
			throw new InvalidRequestException(
					messageSource.getMessage("principalcasehistory.userid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(principalCaseHistoryReq.getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("principalcasehistory.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(principalCaseHistoryReq.getIdLinkCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("principalcasehistory.linkcaseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(principalCaseHistoryReq.getIndicator())) {
			throw new InvalidRequestException(
					messageSource.getMessage("principalcasehistory.indicator.mandatory", null, Locale.US));
		}
		PrincipalCaseHistoryRes principalCaseHistoryRes = principalCaseHistoryService
				.updateCaseInfo(principalCaseHistoryReq);

		return principalCaseHistoryRes;
	}

}