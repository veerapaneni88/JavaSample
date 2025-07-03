package us.tx.state.dfps.service.caretakerinfo.controller;

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
import us.tx.state.dfps.service.caretakerinfo.service.CareTakerInfoService;
import us.tx.state.dfps.service.common.request.CaretakerInformationReq;
import us.tx.state.dfps.service.common.response.CaretakerInformationRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Populate
 * the service to access DB for Caretaker Information page Feb 8, 2018- 7:38:14
 * PM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/careTakerInfo")
public class CareTakerInfoController {
	@Autowired
	CareTakerInfoService caretakerInfoService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(CareTakerInfoController.class);

	/**
	 * Method Name: getCaretakerInfo Method Description: Get the values about
	 * Caretaker Information page.
	 * 
	 * @param careTakerInfoReq
	 * @return CaretakerInformationRes
	 */
	@RequestMapping(value = "/getcaretakerinfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CaretakerInformationRes getCaretakerInfo(
			@RequestBody CaretakerInformationReq careTakerInfoReq) {
		log.debug("Entering method getCaretakerInfo in CareTakerInfoController");
		if (TypeConvUtil.isNullOrEmpty(careTakerInfoReq)
				&& TypeConvUtil.isNullOrEmpty(careTakerInfoReq.getIdResource())) {
			throw new InvalidRequestException(messageSource.getMessage("msg.idresource.mandatory", null, Locale.US));
		}
		CaretakerInformationRes CaretakerInformationRes = caretakerInfoService.getCaretakerInfo(careTakerInfoReq);
		log.debug("Exiting method getCaretakerInfo in CareTakerInfoController");
		return CaretakerInformationRes;
	}

	/**
	 * Method Name: deleteCareTakerInfo Method Description: Delete a row in the
	 * Caretakers table about CareTakerInformation Page.
	 * 
	 * @param careTakerInfoReq
	 * @return CaretakerInformationRes
	 */
	@RequestMapping(value = "/deletecaretakerInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CaretakerInformationRes deleteCareTakerInfo(
			@RequestBody CaretakerInformationReq careTakerInfoReq) {
		if (TypeConvUtil.isNullOrEmpty(careTakerInfoReq)
				&& TypeConvUtil.isNullOrEmpty(careTakerInfoReq.getIdCaretaker())) {
			throw new InvalidRequestException(messageSource.getMessage("msg.idcaretaker.mandatory", null, Locale.US));
		}

		CaretakerInformationRes CaretakerInformationRes = caretakerInfoService.deleteCareTaker(careTakerInfoReq);
		log.debug("Exiting method deleteCareTakerInfo in CareTakerInfoController");
		return CaretakerInformationRes;
	}
}
