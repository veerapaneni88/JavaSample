/**
 * 
 */
package us.tx.state.dfps.service.trafficking.controller;

import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.InTakeDateRequest;
import us.tx.state.dfps.service.common.request.InTakeDateResponse;
import us.tx.state.dfps.service.common.request.TraffickingReq;
import us.tx.state.dfps.service.common.response.TraffickingRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.person.service.TraffickingService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 22, 2018- 5:21:44 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@RestController
@RequestMapping("/trafficking")
public class TraffickingController {

	private static final Logger log = Logger.getLogger(TraffickingController.class);

	@Autowired
	MessageSource messageSource;

	@Autowired
	TraffickingService traffickingService;

	/**
	 * Method Name: displayTraffickingList Method Description: This method is to
	 * display Trafficking List
	 * 
	 * @param traffickingReq
	 * @return
	 */
	@RequestMapping(value = "/traffickingList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public TraffickingRes displayTraffickingList(@RequestBody TraffickingReq traffickingReq) {
		TraffickingRes traffickingres;
		log.debug("Entering method Trafficking List");
		if (TypeConvUtil.isNullOrEmpty(traffickingReq.getTraffickingDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.retrieve.details.IdPerson.mandatory", null, Locale.US));
		}
		traffickingres = traffickingService.getTraffickingList(traffickingReq);
		if(!ObjectUtils.isEmpty(traffickingReq.getTraffickingDto()) && !ObjectUtils.isEmpty(traffickingReq.getTraffickingDto().getIdPerson())) {
			traffickingres.setNmPerson(traffickingService.getNmPerson(traffickingReq.getTraffickingDto().getIdPerson().longValue()));
		}
		log.debug("Exiting method PersonDetailRetrvlo in PersonDetailRetrvlController");
		return traffickingres;
	}

	/**
	 * Method Name: saveTraffickingDtl Method Description: This method is to
	 * save Trafficking details to TRFCKNG_DTL
	 * 
	 * @param traffickingReq
	 * @return
	 */
	@RequestMapping(value = "/saveTraffickingDtl", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public TraffickingRes saveTraffickingDtl(@RequestBody TraffickingReq traffickingReq) {
		log.debug("Entering method saveTraffickingDtl in TraffickingController"
				+ traffickingReq.getTraffickingDto());
		if (TypeConvUtil.isNullOrEmpty(traffickingReq.getTraffickingDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.retrieve.details.IdPerson.mandatory", null, Locale.US));
		}
		TraffickingRes traffickingres = traffickingService.saveTraffickingDetails(traffickingReq);
		log.debug("Exiting method saveTraffickingDtl in TraffickingController");
		return traffickingres;
	}

	/**
	 * Method Name: updateOrDeleteTraffickingDtl
	 * Method Description: This method is to update / delete Trafficking details based on function code (U/D)(Table: TRFCKNG_DTL).
	 *
	 * @param traffickingReq
	 * @return TraffickingRes
	 */
	@RequestMapping(value = "/updateTraffickingDtl", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public TraffickingRes updateOrDeleteTraffickingDtl(@RequestBody TraffickingReq traffickingReq) {
		log.debug("Entering method updateOrDeleteTraffickingDtl in TraffickingController"
				+ traffickingReq.getTraffickingDto());
		if (TypeConvUtil.isNullOrEmpty(traffickingReq.getTraffickingDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.retrieve.details.IdPerson.mandatory", null, Locale.US));
		}
		TraffickingRes traffickingres = new TraffickingRes();
		if(traffickingReq.getReqFuncCd().equalsIgnoreCase("U")) {
			traffickingres = traffickingService.updateTraffickingDetails(traffickingReq);
		}else if(traffickingReq.getReqFuncCd().equalsIgnoreCase("D")) {
			traffickingres = traffickingService.deleteTraffickingDetails(traffickingReq);
		}
		log.debug("Exiting method updateOrDeleteTraffickingDtl in TraffickingController");
		return traffickingres;
	}

	/**
	 * Method Name: getIntakeDate Method Description: This method is to retrieve
	 * earliest intake date.
	 * 
	 * @param inTakeDateReq
	 * @return
	 */
	@RequestMapping(value = "/getIntakeDate", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public InTakeDateResponse getIntakeDate(@RequestBody InTakeDateRequest inTakeDateReq) {
		log.debug("Entering method getIntakeDate in TraffickingController");
		if (TypeConvUtil.isNullOrEmpty(inTakeDateReq.getPersonId())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.retrieve.details.IdPerson.mandatory", null, Locale.US));
		}
		Date inTakeDate = traffickingService.getIntakeDate(inTakeDateReq.getPersonId());
		InTakeDateResponse response = new InTakeDateResponse();
		response.setIntakeDate(inTakeDate);
		log.debug("Exiting method getIntakeDate in TraffickingController :");
		return response;
	}
	
	
	
	/**
	 * 
	 * Method Name: getPerson Method Description: This Method is 
	 * to get Person Object to validate the Date of Incident before save Trafficking Dtl 
	 * to Trafficking Detail Page.
	 * @param TraffickingReq
	 * @return TraffickingRes
	 */
	@RequestMapping(value = "/getPersonBirthDtl", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public TraffickingRes getPersonBirthDtl(@RequestBody TraffickingReq traffickingReq) {
		log.debug("Entering method getPersonBirthDtl in TraffickingController"
				+ traffickingReq.getTraffickingDto());
		if (TypeConvUtil.isNullOrEmpty(traffickingReq.getTraffickingDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.retrieve.details.IdPerson.mandatory", null, Locale.US));
		}
		TraffickingRes traffickingres = traffickingService.getPerson(traffickingReq);
		log.debug("Exiting method getPersonBirthDtl in TraffickingController");
		return traffickingres;
	}
	
	/**
	 * 
	 *Method Name:	checkConfirmedSxTrafficking
	 *Method Description: Check Person has confirmed SxTrafficking or not
	 *@param traffickingReq
	 *@return
	 */
	@RequestMapping(value = "/checkConfirmedSxTrafficking", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public TraffickingRes checkConfirmedSxTrafficking(@RequestBody TraffickingReq traffickingReq) {
		log.debug("Entering method checkConfirmedSxTrafficking in TraffickingController"
				+ traffickingReq.getIdPerson());
		if (TypeConvUtil.isNullOrEmpty(traffickingReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.retrieve.details.IdPerson.mandatory", null, Locale.US));
		}
		TraffickingRes traffickingres = new TraffickingRes();
		traffickingres.setIndConfirmedSxTrafficking(traffickingService.getConfirmedSxTraffickingIndicator(traffickingReq.getIdPerson()));
		log.debug("Exiting method checkConfirmedSxTrafficking in TraffickingController");
		return traffickingres;
	}

}
