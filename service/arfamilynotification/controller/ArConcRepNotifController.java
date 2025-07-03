package us.tx.state.dfps.service.arfamilynotification.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.arfamilynotification.service.ArConcRepNotifService;
import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Sends input
 * parameters to service and returns prefill data May 8, 2018- 4:36:49 PM © 2017
 * Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("arconclusion")
public class ArConcRepNotifController {

	@Autowired
	private ArConcRepNotifService arConcRepNotifService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(ArConcRepNotifController.class);

	/**
	 * Method Name: getNotification Method Description: Gets the prefill string
	 * for the ARRENOT/S forms
	 * 
	 * @param req
	 * @return PreFillDataServiceDto
	 * 
	 */
	@RequestMapping(value = "/getnotification", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getNotification(@RequestBody PopulateFormReq request) {
		log.debug("Entering method arrenot getNotification in ArConcRepNotifController");
		if (TypeConvUtil.isNullOrEmpty(request.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(request.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(request.getIdWorker())) {
			throw new InvalidRequestException(messageSource.getMessage("common.workerid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(request.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(arConcRepNotifService.getReporterNotif(request)));

		return commonFormRes;
	}

}
