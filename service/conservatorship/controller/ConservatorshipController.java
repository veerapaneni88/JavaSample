package us.tx.state.dfps.service.conservatorship.controller;

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
import us.tx.state.dfps.service.common.request.CnsrvtrshpRemovalAlertReq;
import us.tx.state.dfps.service.common.request.CnsrvtrshpRemovalReq;
import us.tx.state.dfps.service.common.request.CommonEventIdReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.response.CnsrvtrshpRemovalAlertRes;
import us.tx.state.dfps.service.common.response.CnsrvtrshpRemovalRes;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.service.ConservRmvlSaveService;
import us.tx.state.dfps.service.conservatorship.service.ConservatorshipService;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.workload.service.WorkloadService;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CSUB14S Class
 * Description: This is the controller for service used by Conservatorship
 * Removal to retrieve information regarding the Removal Event. It retrieves the
 * detail for the event, as well as the removal reason, child removal
 * characteristics, and adult removal characteristics. May 2, 2017 - 5:10:51 PM
 */
@RestController
@RequestMapping("/conservatorship")
public class ConservatorshipController {

	@Autowired
	ConservatorshipService conservatorshipService;

	@Autowired
	ConservRmvlSaveService conservRmvlSaveService;

	@Autowired
	WorkloadService workloadService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(ConservatorshipController.class);

	/**
	 * Method Description: This is the retrieve service used by Conservatorship
	 * Removal to retrieve information regarding the Removal Event. It retrieves
	 * the detail for the event, as well as the removal reason, child removal
	 * characteristics, and adult removal characteristics.
	 * 
	 * @param cnsrvtrshpRemovalReq
	 * @return CnsrvtrshpRemovalRes
	 */
	@RequestMapping(value = "/getCnsrvtrshpRmvl", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CnsrvtrshpRemovalRes getConservatorshipRemoval(
			@RequestBody CnsrvtrshpRemovalReq cnsrvtrshpRemovalReq) {

		if (TypeConvUtil.isNullOrEmpty(cnsrvtrshpRemovalReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(cnsrvtrshpRemovalReq.getSysCdWinMode())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.sysCdWinMode.mandatory", null, Locale.US));
		}
		if (!ObjectUtils.isEmpty(cnsrvtrshpRemovalReq.getIdPersonList())
				&& (cnsrvtrshpRemovalReq.getIdPersonList().size() == 0)
				&& !ObjectUtils.isEmpty(cnsrvtrshpRemovalReq.getIdEventList())
				&& (cnsrvtrshpRemovalReq.getIdEventList().size() == 0)) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + cnsrvtrshpRemovalReq.getTransactionId());
		return conservatorshipService.getRmvlEventDtls(cnsrvtrshpRemovalReq);
	}

	/**
	 * Method Description: generate Random Number in IDRMVLGROUP for List of
	 * Event
	 * 
	 * @param eventIdList
	 * @return updateIdGroupRmvl
	 */

	@RequestMapping(value = "/updateIdRmvlGroup", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes updateIdRmvlGroup(@RequestBody CommonEventIdReq eventIdList) {

		if (TypeConvUtil.isNullOrEmpty(eventIdList.getIdEvents()) || 0 == eventIdList.getIdEvents().size()) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		CommonHelperRes updateIdGroupRmvl = new CommonHelperRes();

		updateIdGroupRmvl = conservatorshipService.updateIdRmvlGroup(eventIdList);
		if (null == updateIdGroupRmvl.getMessage()) {
			throw new DataNotFoundException(messageSource.getMessage("person.personlist.data", null, Locale.US));
		}
		log.info("TransactionId :" + eventIdList.getTransactionId());
		return updateIdGroupRmvl;
	}

	/**
	 * Method Name: CVSRemovalAlert Method Description: This method will trigger
	 * alert in “CVS Removal” page
	 * 
	 * @param cnsrvtrshpRemovalAlertReq
	 * @return
	 */
	@RequestMapping(value = "/getAlertForCVSRemoval", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CnsrvtrshpRemovalAlertRes getAlertForCVSRemoval(
			@RequestBody CnsrvtrshpRemovalAlertReq cnsrvtrshpRemovalAlertReq) {
		if (TypeConvUtil.isNullOrEmpty(cnsrvtrshpRemovalAlertReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + cnsrvtrshpRemovalAlertReq.getTransactionId());
		return conservatorshipService.getAlertForCVSRemoval(cnsrvtrshpRemovalAlertReq);
	}

	@RequestMapping(value = "/babyMosesRemoval", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonBooleanRes babyMosesRmlRsnExists(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return conservatorshipService.babyMosesRemovalReasonExists(commonHelperReq);
	}

	/**
	 * 
	 * Method Name: fetchEmployeeEmail Method Description: This Method is used
	 * for fetching the primary and secondary case-workers employee email
	 * addresses based on the event id
	 * 
	 * @param commonHelperReq
	 * @return
	 */
	@RequestMapping(value = "/sendEmail", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes fetchEmployeeEmail(@RequestBody CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		String message = null;
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEventList())
				|| TypeConvUtil.isNullOrEmpty(commonHelperReq.getDtCVSRemoval())) {
			throw new InvalidRequestException(messageSource.getMessage("common.request.mandatory", null, Locale.US));
		}
		message = conservatorshipService.fetchEmployeeEmail(commonHelperReq);
		if (!ObjectUtils.isEmpty(commonHelperReq.getIdStageFsu())) {
			// ADS change for creating a calendar event for creating initial
			// FSNA
			// to the primary/secondary worker in FSU stage.
			conservRmvlSaveService.createCalendarEventForFSU(commonHelperReq.getHostName(),
					commonHelperReq.getDtCVSRemoval(), commonHelperReq.getIdStageFsu());
		} else {

			workloadService.createCalendarEventForReassignment(commonHelperReq.getIdStage(), null,
					commonHelperReq.getHostName(), ServiceConstants.YES, ServiceConstants.N);
		}
		commonHelperRes.setMessage(message);
		return commonHelperRes;
	}

}
