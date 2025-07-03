
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
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.CriminalHistoryReq;
import us.tx.state.dfps.service.common.request.CriminalHistoryUpdateReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.CrimHistoryRes;
import us.tx.state.dfps.service.common.response.CriminalHistoryNarrRes;
import us.tx.state.dfps.service.common.response.CriminalHistoryRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.person.service.CriminalHistoryService;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo and EJB Service Name:
 * CriminalHistoryController Class Description: A class to map the call to
 * appropriate service call for insert, save , updating Criminal History detail
 * List April 30, 2018 - 3:19:51 PM
 *
 * ********Change History**********
 * *  02/12/2021 nairl artf172946 : DEV BR 21.01 Support Manual Entry of Results from DPS’ SecureSite into IMPACT P2
 */
@RestController
@RequestMapping("/criminalHistory")
public class CriminalHistoryController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	CriminalHistoryService criminalHistoryService;

	private static final Logger logger = Logger.getLogger(CriminalHistoryController.class);

	/**
	 * 
	 * Method Description: This service will Update rows in the Criminal History
	 * Table for a given IdCrimHist. Also, this service will delete rows from
	 ** Crim Hist Narr Table for a given IdCrimHist when the XIndDeleteNarr flag
	 * is set for that IdCrimHist. Service Name: CCFC32S
	 * 
	 * 
	 * @param CriminalHistoryUpdateReq
	 * @return CriminalHistoryRes
	 */
	@RequestMapping(value = "/updateCriminalHistoryRec", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CriminalHistoryRes updateCriminalHistoryRec(@RequestBody CriminalHistoryUpdateReq criminalHistoryUpdateReq) {
		if (ObjectUtils.isEmpty(criminalHistoryUpdateReq.getCriminalHistoryList())) {
			throw new InvalidRequestException(
					messageSource.getMessage("criminal.history.save.IdCrimHist", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(criminalHistoryUpdateReq.getCriminalHistoryList().get(0).getIdCrimHist())
				&& (!ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(criminalHistoryUpdateReq.getReqFuncCd()) && !ServiceConstants.REQ_FUNC_CD_DELETE.equalsIgnoreCase(criminalHistoryUpdateReq.getReqFuncCd()))) { // Added for artf172946
			throw new InvalidRequestException(
					messageSource.getMessage("criminal.history.save.IdCrimHist", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(criminalHistoryUpdateReq.getCriminalHistoryList().get(0).getIdRecCheck())) {
			throw new InvalidRequestException(
					messageSource.getMessage("criminal.history.save.recordCheckID", null, Locale.US));
		}
		CriminalHistoryRes criminalHistoryRes = criminalHistoryService
				.updateCriminalHistoryRec(criminalHistoryUpdateReq);
		logger.info("TransactionId :" + criminalHistoryUpdateReq.getTransactionId());
		return criminalHistoryRes;
	}

	/**
	 * 
	 * Method Description: This service will retrieve all rows from the Criminal
	 * History Table for a given ID_REC_CHECK. Service Name: CCFC31S
	 * 
	 * 
	 * @param CriminalHistoryReq
	 * @return CrimHistoryRes
	 */
	@RequestMapping(value = "/getCriminalHistoryList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CrimHistoryRes getCriminalHistoryList(@RequestBody CriminalHistoryReq criminalHistoryReq) {
		if (ObjectUtils.isEmpty(criminalHistoryReq.getIdRecCheck())) {
			throw new InvalidRequestException(
					messageSource.getMessage("criminal.history.save.recordCheckID", null, Locale.US));
		}
		CrimHistoryRes crimHistoryRes = criminalHistoryService.getCriminalHistoryList(criminalHistoryReq);
		logger.info("TransactionId :" + criminalHistoryReq.getTransactionId());
		return crimHistoryRes;
	}

	/**
	 * Method Name: checkCrimHistAction Method Description: This method to get
	 * the idPerson if the Criminal History Action is null for the given
	 * Id_Stage.
	 * 
	 * @param criminalHistoryReq
	 * @return criminalHistoryRes
	 */
	@RequestMapping(value = "/checkCrimHistAction", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CriminalHistoryRes checkCrimHistAction(@RequestBody CriminalHistoryReq criminalHistoryReq) {
		if (TypeConvUtil.isNullOrEmpty(criminalHistoryReq.getIdStage())) {
			throw new InvalidRequestException(messageSource
					.getMessage("criminalHistoryReq.checkCrimHistAction.getIdStage.mandatory", null, Locale.US));
		}
		CriminalHistoryRes criminalHistoryRes = new CriminalHistoryRes();
		criminalHistoryRes.setCriminalHistoryActionCheckResult(
				criminalHistoryService.checkCrimHistAction(criminalHistoryReq.getIdStage()));
		logger.info("TransactionId :" + criminalHistoryReq.getTransactionId());
		return criminalHistoryRes;
	}

	/**
	 * Method Name: isCrimHistPending Method Description: To check if any DPS
	 * Criminal History check is pending.
	 * 
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/crimHistPending", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes isCrimHistPending(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource
					.getMessage("criminalHistoryReq.checkCrimHistAction.getIdStage.mandatory", null, Locale.US));
		}
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setResult(criminalHistoryService.isCrimHistPending(commonHelperReq.getIdStage()));
		return commonHelperRes;
	}

	@RequestMapping(value = "/isCrimHistoryNarr", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CriminalHistoryNarrRes isCrimHistNarrPresentForRecordCheck(@RequestBody CriminalHistoryReq crimHistoryReq) {
		CriminalHistoryNarrRes res = new CriminalHistoryNarrRes();
		boolean isCriminalHistoryComplete = criminalHistoryService.isCrimHistNarrPresentForRecordCheck(crimHistoryReq);
		res.setCrimHistNarrPresent(isCriminalHistoryComplete);
		return res;
	}
}
