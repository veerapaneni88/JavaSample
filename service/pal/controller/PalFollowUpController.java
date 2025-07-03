package us.tx.state.dfps.service.pal.controller;

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
import us.tx.state.dfps.service.common.request.PalFollowUpReq;
import us.tx.state.dfps.service.common.response.PalFollowUpRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.pal.service.PalFollowUpService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PalFollowUpController for PalFollowUpBean Oct 11, 2017- 6:50:04
 * PM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/palFollowUp")
public class PalFollowUpController {

	@Autowired
	private PalFollowUpService palFollowUpService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-PalFollowUpControllerLog");

	/**
	 * Method Name: retrievePal Method Description: Retrieves the Pal record
	 * with to new fields CD_NO_ILS_REASON and DT_TRAINING_CMPLTD using
	 * ID_PAL_STAGE from database.
	 * 
	 * @param palFollowUpReq
	 * @return PalFollowUpRes
	 */
	@RequestMapping(value = "/getPal", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PalFollowUpRes retrievePal(@RequestBody PalFollowUpReq palFollowUpReq) {
		LOG.debug("Entering method retrievePal in PalFollowUpController");

		if (TypeConvUtil.isNullOrEmpty(palFollowUpReq.getPalFollowUpDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("pal.getPalFollowUpDto.mandatory", null, Locale.US));
		}
		PalFollowUpRes palFollowUpRes = palFollowUpService.retrievePal(palFollowUpReq);
		LOG.debug("Exiting method retrievePal in PalFollowUpController");
		return palFollowUpRes;
	}

	/**
	 * Method Name: retrievePalFollowUp Method Description: Retrieves the Pal
	 * Follow Up record set details from the database.
	 * 
	 * @param palFollowUpReq
	 * @return PalFollowUpRes
	 */
	@RequestMapping(value = "/getPalFollowUp", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PalFollowUpRes retrievePalFollowUp(@RequestBody PalFollowUpReq palFollowUpReq) {
		LOG.debug("Entering method retrievePalFollowUp in PalFollowUpController");
		if (TypeConvUtil.isNullOrEmpty(palFollowUpReq.getPalFollowUpDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("pal.getPalFollowUpDto.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(palFollowUpReq.getPalFollowUpDto().getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		PalFollowUpRes palFollowUpRes = palFollowUpService.retrievePalFollowUp(palFollowUpReq);
		LOG.debug("Exiting method retrievePalFollowUp in PalFollowUpController");
		return palFollowUpRes;
	}

	/**
	 * Method Name: insertPalFollowUp Method Description: Inserts the Pal Follow
	 * Up records into the database.
	 * 
	 * @param palFollowUpReq
	 * @return PalFollowUpRes
	 */
	@RequestMapping(value = "/savePalFollowUp", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PalFollowUpRes insertPalFollowUp(@RequestBody PalFollowUpReq palFollowUpReq) {
		LOG.debug("Entering method insertPalFollowUp in PalFollowUpController");
		if (TypeConvUtil.isNullOrEmpty(palFollowUpReq.getPalFollowUpDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("pal.getPalFollowUpDto.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(palFollowUpReq.getPalFollowUpDto().getIdPalFollupStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(palFollowUpReq.getPalFollowUpDto().getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idCase.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(palFollowUpReq.getPalFollowUpDto().getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idPerson.mandatory", null, Locale.US));
		}
		PalFollowUpRes palFollowUpRes = palFollowUpService.insertPalFollowUp(palFollowUpReq);
		LOG.debug("Exiting method insertPalFollowUp in PalFollowUpController");
		return palFollowUpRes;
	}

	/**
	 * Method Name: updatePal Method Description: Update the Pal table.
	 * 
	 * @param palFollowUpReq
	 * @return PalFollowUpRes
	 */
	@RequestMapping(value = "/updatePal", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PalFollowUpRes updatePal(@RequestBody PalFollowUpReq palFollowUpReq) {
		LOG.debug("Entering method updatePal in PalFollowUpController");
		if (TypeConvUtil.isNullOrEmpty(palFollowUpReq.getPalFollowUpDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("pal.getPalFollowUpDto.mandatory", null, Locale.US));
		}
		PalFollowUpRes palFollowUpRes = palFollowUpService.updatePal(palFollowUpReq);

		LOG.debug("Exiting method updatePal in PalFollowUpController");
		return palFollowUpRes;
	}

}
