/**
 *service-ejb-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Nov 17, 2017- 1:25:13 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.fce.controller;

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

import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.AgeCitizenshipReq;
import us.tx.state.dfps.service.common.request.FceReq;
import us.tx.state.dfps.service.common.request.SaveFceApplicationReq;
import us.tx.state.dfps.service.common.request.TexasZipCountyValidateReq;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.FceResp;
import us.tx.state.dfps.service.common.response.GetFceApplicationRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.fce.dto.AgeCitizenshipDto;
import us.tx.state.dfps.service.fce.service.AgeCitizenshipService;
import us.tx.state.dfps.service.fce.service.FceService;

/**
 * This is the controller that handles requests to FCE (foster Care Eligibility)
 * Service
 * 
 * service-ejb-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter
 * the description of class> Nov 17, 2017- 1:25:13 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@RestController
@RequestMapping(value = "/fosterCare")
public class FceSvcController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	FceService fceService;

	/** The age citizenship service. */
	@Autowired
	AgeCitizenshipService ageCitizenshipService;

	private static final Logger log = Logger.getLogger(FceSvcController.class);

	@RequestMapping(value = "/hasOpenFosterCareEligibility", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FceResp hasOpenFosterCareEligibility(@RequestBody FceReq fceReq) {
		log.info("TransactionId :" + fceReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(fceReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		FceResp fceResp = new FceResp();
		fceResp.setIndHasOpenFce(fceService.hasOpenFosterCareEligibility(fceReq.getIdPerson()));
		return fceResp;
	}

	@RequestMapping(value = "/getOpenFceEventForStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FceResp getOpenFceEventForStage(@RequestBody FceReq fceReq) {
		log.info("TransactionId :" + fceReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(fceReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		FceResp fceResp = new FceResp();
		fceResp.setIdOpenFceEvent(fceService.getOpenFceEventForStage(fceReq.getIdStage()));
		return fceResp;
	}

	@RequestMapping(value = "/fetchLatestEligibility", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FceResp fetchLatestEligibility(@RequestBody FceReq fceReq) {
		log.info("TransactionId :" + fceReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(fceReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		FceResp fceResp = new FceResp();
		fceResp.setEligibilityDto(fceService.fetchLatestEligibility(fceReq.getIdPerson()));
		return fceResp;
	}

	@RequestMapping(value = "/fetchActiveFceList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FceResp fetchActiveFceList(@RequestBody FceReq fceReq) {
		log.info("TransactionId :" + fceReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(fceReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		FceResp fceResp = new FceResp();
		fceResp.setActiveFceList(fceService.fetchActiveFceList(fceReq.getIdPerson()));
		return fceResp;
	}

	@RequestMapping(value = "/initializeFceEligibility", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FceResp initializeFceEligibility(@RequestBody FceReq fceReq) {
		log.info("TransactionId :" + fceReq.getTransactionId());
		checkForFceRequiredIds(fceReq);

		FceResp fceResp = new FceResp();
		fceResp.setFceContextDto(fceService.initializeFceEligibility(fceReq.getIdStage(), fceReq.getIdappevent(),
				fceReq.getIdlastupdatePerson()));

		return fceResp;
	}

	@RequestMapping(value = "/initializeFceReview", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FceResp initializeFceReview(@RequestBody FceReq fceReq) {
		log.info("TransactionId :" + fceReq.getTransactionId());
		checkForFceRequiredIds(fceReq);

		FceResp fceResp = new FceResp();
		fceResp.setFceContextDto(fceService.initializeFceReview(fceReq.getIdStage(), fceReq.getIdappevent(),
				fceReq.getIdlastupdatePerson()));

		return fceResp;
	}

	private void checkForFceRequiredIds(FceReq fceReq) {
		if (TypeConvUtil.isNullOrEmpty(fceReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(fceReq.getIdlastupdatePerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.lastUpdatePersonId.mandatory", null, Locale.US));
		}
	}

	/**
	 * This service is to get the Fce application information for App/Background
	 * page display also creates new applications when add / new using pressed
	 * 
	 * @param fceReq
	 * @return
	 */
	@RequestMapping(value = "/getFceApplication", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  GetFceApplicationRes getFceApplication(@RequestBody FceReq fceReq) {
		log.info("TransactionId :" + fceReq.getTransactionId());
		checkForFceRequiredIds(fceReq);
		if (!ServiceConstants.NEW_USING.equals(fceReq.getPageMode())) {

			return fceService.getFceApplication(fceReq);
		}
		// The following will handle the new using
		FceReq newFceReq = new FceReq();
		newFceReq.setIdlastupdatePerson(fceReq.getIdlastupdatePerson());
		newFceReq.setIdStage(fceReq.getIdStage());
		newFceReq.setIdappevent(0l);
		// create new app
		GetFceApplicationRes newFceApplicationRes = fceService.createNewApp(newFceReq);
		// copy Old app data to new App
		fceService.copyOldApptoNewApp(newFceApplicationRes, fceReq);

		return newFceApplicationRes;
	}

	/**
	 * This service is to save the FCE application from App/Background page.
	 * 
	 * @param saveFceApplicationReq
	 * @return
	 */
	@RequestMapping(value = "/saveFceApplication", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ServiceResHeaderDto saveFceApplication(
			@RequestBody SaveFceApplicationReq saveFceApplicationReq) {
		log.info("TransactionId :" + saveFceApplicationReq.getTransactionId());
		if (ObjectUtils.isEmpty(saveFceApplicationReq.getFceApplicationDto().getIdFceApplication()))
			throw new InvalidRequestException(messageSource.getMessage("IdFceApplication.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(saveFceApplicationReq.getFceApplicationDto().getIdFceEligibility()))
			throw new InvalidRequestException(messageSource.getMessage("IdFceEligibility.mandatory", null, Locale.US));
		return fceService.saveFceApplication(saveFceApplicationReq);
	}

	/**
	 * This service is to check if the given zip and county is valid for texas
	 * 
	 * @param texasZipCountyValidateReq
	 * @return
	 */
	@RequestMapping(value = "/isvalidtexaszipcounty", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonBooleanRes isValidTexasZipAndCounty(
			@RequestBody TexasZipCountyValidateReq texasZipCountyValidateReq) {
		if (ObjectUtils.isEmpty(texasZipCountyValidateReq.getAddrZip()))
			throw new InvalidRequestException(
					messageSource.getMessage("person.AddrZipCode.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(texasZipCountyValidateReq.getCdAddrCounty()))
			throw new InvalidRequestException(messageSource.getMessage("common.countyCode.mandatory", null, Locale.US));
		return fceService.isValidTexasZipAndCounty(texasZipCountyValidateReq);
	}

	/**
	 * Method Name: read Method Description:Fetches the Age Citizenship details
	 * 
	 * @param ageCitizenshipReq
	 * @return ageCitizenshipRes
	 */
	@RequestMapping(value = "/ageCitizenshipRead", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  AgeCitizenshipDto read(@RequestBody AgeCitizenshipReq ageCitizenshipReq) {
		if (TypeConvUtil.isNullOrEmpty(ageCitizenshipReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(ageCitizenshipReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(ageCitizenshipReq.getIdUser())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.lastUpdatePersonId.mandatory", null, Locale.US));
		}
		AgeCitizenshipDto ageCitizenshipDto = new AgeCitizenshipDto();

		ageCitizenshipDto = ageCitizenshipService.read(ageCitizenshipReq.getIdStage(), ageCitizenshipReq.getIdEvent(),
				ageCitizenshipReq.getIdUser());

		return ageCitizenshipDto;

	}

	/**
	 * Method Name: save Method Description:Saves the Age Citizenship details
	 * 
	 * @param ageCitizenshipReq
	 * @return ageCitizenshipRes
	 */
	@RequestMapping(value = "/ageCitizenshipSave", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  AgeCitizenshipDto save(@RequestBody AgeCitizenshipReq ageCitizenshipReq) {
		if (TypeConvUtil.isNullOrEmpty(ageCitizenshipReq.getAgeCitizenshipDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.ageCitiznishipDto.mandatory", null, Locale.US));
		}
		String cdEventStatus = ageCitizenshipService.save(ageCitizenshipReq.getAgeCitizenshipDto());
		AgeCitizenshipDto ageCitizenshipDto = new AgeCitizenshipDto();
		ageCitizenshipDto.setCdEventStatus(cdEventStatus);
		return ageCitizenshipDto;
	}

}
