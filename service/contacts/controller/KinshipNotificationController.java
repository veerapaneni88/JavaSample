package us.tx.state.dfps.service.contacts.controller;

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
import us.tx.state.dfps.service.common.request.KinshipNotificationReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contacts.service.KinshipNotificationService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Implements
 * methods from . Apr 23, 2018- 9:27:34 PM Â© 2017 Texas Department of Family
 * and Protective Services
 */
@RestController
@RequestMapping("/kinship")
public class KinshipNotificationController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	KinshipNotificationService kinshipNotificationService;

	private static final Logger log = Logger.getLogger(ContactDetailsController.class);

	/**
	 * 
	 * Method Description: Form Name: KIN15O00,KIN16O00 Service Name: CKIN07S
	 * 
	 * @param kinshipNotificationReq
	 * @return KinshipNotificationDto
	 */

	@RequestMapping(value = "/getNotifications", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getKinshipNotification(@RequestBody KinshipNotificationReq kinshipNotificationReq) {

		log.debug("Entering the KinshipNotificationController - getKinshipNotification");
		if (ObjectUtils.isEmpty(kinshipNotificationReq.getIdStage()))
			throw new InvalidRequestException(
					messageSource.getMessage("KinshipNotificationReq.idStage.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(kinshipNotificationReq.getIdCase()))
			throw new InvalidRequestException(
					messageSource.getMessage("KinshipNotificationReq.idCase.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(kinshipNotificationReq.getIdEvent()))
			throw new InvalidRequestException(
					messageSource.getMessage("KinshipNotificationReq.idEvent.mandatory", null, Locale.US));

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil
				.getXMLFormat(kinshipNotificationService.getKinshipNotificationDetails(kinshipNotificationReq)));

		log.debug("Exiting the KinshipNotificationController - getKinshipNotification");

		return commonFormRes;
	}

}
