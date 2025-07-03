package us.tx.state.dfps.service.person.controller;

import java.sql.SQLException;
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
import us.tx.state.dfps.service.common.request.AbcsRecordCheckReq;
import us.tx.state.dfps.service.common.request.RecordsCheckDetailReq;
import us.tx.state.dfps.service.common.request.RecordsCheckNotifReq;
import us.tx.state.dfps.service.common.request.RecordsCheckReq;
import us.tx.state.dfps.service.common.request.RecordsCheckStatusReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.RecordsCheckListRes;
import us.tx.state.dfps.service.common.response.RecordsCheckNotifIdRes;
import us.tx.state.dfps.service.common.response.RecordsCheckRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.person.service.AbcsRecordsCheckService;
import us.tx.state.dfps.service.person.service.RecordsCheckService;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo and EJB Service Name:
 * RecordsCheckController Class Description: A class to map the call to
 * appropriate service call for insert, save , updating Records check detail
 * List April 24, 2018 - 3:19:51 PM
 *
 * ********Change History**********
 *  02/12/2021 nairl artf172946 : DEV BR 21.01 Support Manual Entry of Results from DPS’ SecureSite into IMPACT P2
 */

@RestController
@RequestMapping("/recordCheck")
public class RecordsCheckController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	AbcsRecordsCheckService abcsRecordsCheckService;

	@Autowired
	RecordsCheckService recordsCheckService;

	private static final Logger logger = Logger.getLogger(RecordsCheckController.class);

	/**
	 * Method Description: This Service is used to retrieve the notification
	 * form for record check detail screen.
	 * 
	 * @param abcsRecordCheckReq
	 * @return CommonFormRes
	 */
	@RequestMapping(value = "/getRecordsCheckNtfcnForm", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getRecordsCheckNtfcnForm(@RequestBody AbcsRecordCheckReq abcsRecordCheckReq) {
		if (TypeConvUtil.isNullOrEmpty(abcsRecordCheckReq.getIdRecCheck()))
			throw new InvalidRequestException(messageSource.getMessage("common.recCheckId.mandatory", null, Locale.US));
		logger.info("TransactionId :" + abcsRecordCheckReq.getTransactionId());
		return abcsRecordsCheckService.getRecordsCheckNtfcnForm(abcsRecordCheckReq);
	}

	/**
	 * 
	 * Method Name: getRecordsCheckList implementation for CCFC26S Method
	 * Description: This service is used to retrieve the list of Records Check
	 * 
	 * @param recordCheckRetrieveReq
	 * @return RecordCheckRetrieveRes
	 */
	@RequestMapping(value = "/getRecordsCheckList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  RecordsCheckListRes getRecordsCheckList(@RequestBody RecordsCheckReq recordCheckListReq) {
		if (TypeConvUtil.isNullOrEmpty(recordCheckListReq.getIdRecCheckPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("recordcheck.ulIdRecCheckPerson.mandatory", null, Locale.US));
		}
		RecordsCheckListRes recordCheckRetrieveRes = recordsCheckService
				.getRecordsCheckList(recordCheckListReq);
		recordCheckRetrieveRes.setNmRequestedBy(recordCheckListReq.getLastName());
		return recordCheckRetrieveRes;
	}

	/**
	 * 
	 * Method Name: getRecordsCheckList implementation for CCFC26S Method
	 * Description: This service is used to retrieve the list of Records Check
	 * 
	 * @param recordCheckRetrieveReq
	 * @return RecordCheckRetrieveRes
	 */
	@RequestMapping(value = "/getMaxIdRecCheckNotifId", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  RecordsCheckNotifIdRes getRecordsCheckList(
			@RequestBody RecordsCheckNotifReq recordsCheckNotifReq) {
		Integer idRecCheckNotifId;
		RecordsCheckNotifIdRes recordsCheckNotifIdRes = new RecordsCheckNotifIdRes();
		idRecCheckNotifId = recordsCheckService.getMaxIdRecCheckNotif();
		recordsCheckNotifIdRes.setIdRecordsCheckNotif(idRecCheckNotifId);
		logger.info("TransactionId :" + recordsCheckNotifReq.getTransactionId());
		return recordsCheckNotifIdRes;
	}

	/**
	 * 
	 * Method Name: RecordsCheckAUD Method Description:This service will add,
	 * Update or Delete a Records Check a given IdRecCheck
	 * 
	 * @param RecordsCheckDetailReq
	 * @return RecordsCheckListRes
	 */
	@RequestMapping(value = "/recordsCheckAUD", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  RecordsCheckListRes recordsCheckAUD(@RequestBody RecordsCheckDetailReq recordsCheckDetailReq) {
		if (TypeConvUtil.isNullOrEmpty(recordsCheckDetailReq.getReqFuncCd())) {

			throw new InvalidRequestException(
					messageSource.getMessage("recordcheck.szCdScrDataAction.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(recordsCheckDetailReq.getIdRecCheckPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("recordcheck.ulIdRecCheckPerson.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(recordsCheckDetailReq.getUserId())) {
			throw new InvalidRequestException(
					messageSource.getMessage("recordcheck.ulIdRecCheckRequestor.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(recordsCheckDetailReq.getRecordsCheckDtoList())) {
			throw new InvalidRequestException(
					messageSource.getMessage("recordcheck.szCdRecChkDeterm.mandatory", null, Locale.US));
		}

		RecordsCheckListRes recordsCheckListRes = recordsCheckService.recordsCheckAUDService(recordsCheckDetailReq);
		logger.info("TransactionId :" + recordsCheckDetailReq.getTransactionId());
		return recordsCheckListRes;
	}

	/**
	 * Method Name: updateRecordsCheckStatus Method Description:This method is
	 * for updating status for corresponding idRecCheck
	 * 
	 * @param RecordsCheckStatusReq
	 * @return RecordsCheckRes
	 */
	@RequestMapping(value = "/updateRecordsCheckStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  RecordsCheckRes updateRecordsCheckStatus(
			@RequestBody RecordsCheckStatusReq recordsCheckStatusReq) {

		if (TypeConvUtil.isNullOrEmpty(recordsCheckStatusReq.getIdRecCheck())
				|| TypeConvUtil.isNullOrEmpty(recordsCheckStatusReq.getStatus())) {
			throw new InvalidRequestException(messageSource.getMessage("common.recCheckId.mandatory", null, Locale.US));
		}

		RecordsCheckRes recordsCheckRes = recordsCheckService.updateRecordsCheckStatus(recordsCheckStatusReq);
		logger.info("TransactionId :" + recordsCheckStatusReq.getTransactionId());
		return recordsCheckRes;
	}

	/**
	 * Method Name: generateAlerts Method Description: This method is used to
	 * generate alerts for Person when the results are returned for the DPS
	 * Criminal History Record Check.
	 * 
	 * @param RecordsCheckStatusReq
	 * @return RecordsCheckRes
	 */
	@RequestMapping(value = "/generateAlerts", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  RecordsCheckRes generateAlerts(@RequestBody RecordsCheckStatusReq recordsCheckStatusReq) {

		if (TypeConvUtil.isNullOrEmpty(recordsCheckStatusReq.getIdRecCheckPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("recordcheck.idRecCheckPerson.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(recordsCheckStatusReq.getIdRecCheckRequestor())) {
			throw new InvalidRequestException(
					messageSource.getMessage("recordcheck.idRecCheckRequestor.mandatory", null, Locale.US));
		}
		/*
		 * if (TypeConvUtil.isNullOrEmpty(recordsCheckStatusReq.getIdStage())) {
		 * throw new InvalidRequestException(messageSource.getMessage(
		 * "common.stageid.mandatory", null, Locale.US)); }
		 */
		if (TypeConvUtil.isNullOrEmpty(recordsCheckStatusReq.getStatus())) {
			throw new InvalidRequestException(
					messageSource.getMessage("recordcheck.processDPSNameSearchStatus.mandatory", null, Locale.US));
		}
		RecordsCheckRes personAlertRes = recordsCheckService.generateAlerts(recordsCheckStatusReq);
		logger.info("TransactionId :" + recordsCheckStatusReq.getTransactionId());

		return personAlertRes;
	}

	/**
	 * Method Name: getServiceCode Method Description:Retrives Service code from
	 * stored procedure call for a giving Record Check id
	 * 
	 * @param RecordsCheckStatusReq
	 * @return RecordsCheckRes
	 */
	@RequestMapping(value = "/getServiceCode", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  RecordsCheckRes getServiceCode(@RequestBody RecordsCheckStatusReq recordsCheckStatusReq){
		if (TypeConvUtil.isNullOrEmpty(recordsCheckStatusReq.getIdRecCheck())) {
			throw new InvalidRequestException(messageSource.getMessage("common.recCheckId.mandatory", null, Locale.US));
		}
		RecordsCheckRes recordsCheckRes = recordsCheckService.getServiceCode(recordsCheckStatusReq);
		logger.info("TransactionId :" + recordsCheckStatusReq.getTransactionId());
		return recordsCheckRes;
	}
	/* Added for artf172946 */
	/**
	 * Method Name: updateRecordsCheck
	 * Method Description:This method is
	 * for updating the RecordsCheck for the corresponding idRecCheck
	 *
	 * @param recordsCheckDetailReq
	 * @return RecordsCheckRes
	 */
	@RequestMapping(value = "/updateRecordsCheck", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  RecordsCheckRes updateRecordsCheck(
			@RequestBody RecordsCheckDetailReq recordsCheckDetailReq) {

		if (TypeConvUtil.isNullOrEmpty(recordsCheckDetailReq.getRecordsCheckDtoList().get(0).getIdRecCheck())) {
			throw new InvalidRequestException(messageSource.getMessage("common.recCheckId.mandatory", null, Locale.US));
		}
		RecordsCheckRes recordsCheckRes = recordsCheckService.updateRecordsCheck(recordsCheckDetailReq);
		logger.info("TransactionId :" + recordsCheckDetailReq.getTransactionId());
		return recordsCheckRes;
	}

	/**
	 *
	 * Method Name: recordsCheckDetermHistoryAD
	 * Method Description:This service will add and delete Records Check Determination History for a given IdRecCheck
	 *
	 * @param recordsCheckDetailReq
	 * @return RecordsCheckListRes
	 */
	@RequestMapping(value = "/recordsCheckDetermHistAD", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  RecordsCheckListRes recordsCheckDetermHistoryAD(@RequestBody RecordsCheckDetailReq recordsCheckDetailReq) {
		if (TypeConvUtil.isNullOrEmpty(recordsCheckDetailReq.getReqFuncCd())) {

			throw new InvalidRequestException(
					messageSource.getMessage("recordcheck.szCdScrDataAction.mandatory", null, Locale.US));
		}

		if (ObjectUtils.isEmpty(recordsCheckDetailReq.getRecordsCheckDtoList())) {
			throw new InvalidRequestException(
					messageSource.getMessage("recordCheck.not.found.attributes", null, Locale.US));
		}

		RecordsCheckListRes recordsCheckListRes = recordsCheckService.recdsCheckDetermHistoryAD(recordsCheckDetailReq);
		logger.info("TransactionId :" + recordsCheckDetailReq.getTransactionId());
		return recordsCheckListRes;
	}

	/**
	 *
	 * Method Name: getRecCheckDetermHistory
	 * Method Description:This service will get Records Check Determination History for a given IdRecCheck
	 *
	 * @param idRecCheck
	 * @return RecordsCheckListRes
	 */
	@RequestMapping(value = "/getRecCheckDetermHistory", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckListRes getRecCheckDetermHistory(@RequestBody Long idRecCheck)  {
		if (TypeConvUtil.isNullOrEmpty(idRecCheck)) {
			throw new InvalidRequestException(messageSource.getMessage("common.recCheckId.mandatory", null, Locale.US));
		}
		RecordsCheckListRes recordsCheckListRes = recordsCheckService
				.getRecCheckDetermHistory(idRecCheck);

		return recordsCheckListRes;
	}
	/* End of artf172946 */
}
