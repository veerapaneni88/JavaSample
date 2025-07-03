package us.tx.state.dfps.service.personutility.controller;

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
import us.tx.state.dfps.service.common.request.PersonUtilityReq;
import us.tx.state.dfps.service.common.response.PersonUtilityRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.personutility.service.PersonUtilityService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * executes the method declared in the PersonUtilityBean. Oct 10, 2017- 10:47:05
 * AM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/personUtility")
public class PersonUtilityController {

	@Autowired
	private PersonUtilityService personUtilityService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-PersonUtilityControllerLog");

	/**
	 * Method Name: isPersonInOneOfThesePrograms Method Description:Returns true
	 * if given person exists in at least one given stage program
	 * 
	 * @param personUtilityReq
	 * @return PersonUtilityRes
	 */
	@RequestMapping(value = "/isPersonInOneOfThesePrograms", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonUtilityRes isPersonInOneOfThesePrograms(@RequestBody PersonUtilityReq personUtilityReq) {
		LOG.debug("Entering method isPersonInOneOfThesePrograms in PersonUtilityController");
		if (TypeConvUtil.isNullOrEmpty(personUtilityReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		PersonUtilityRes personUtilityRes = new PersonUtilityRes();
		personUtilityRes.setResult(personUtilityService.isPersonInOneOfThesePrograms(personUtilityReq.getIdPerson(),
				personUtilityReq.getHashSet()));
		personUtilityRes.setTransactionId(personUtilityReq.getTransactionId());
		LOG.debug("Exiting method isPersonInOneOfThesePrograms in PersonUtilityController");
		return personUtilityRes;
	}

	/**
	 * Method Name: getPersonRaceEthnicity Method Description:returns true if
	 * race and ethnicity data is not found for one or more Principals.
	 * 
	 * @param personUtilityReq
	 * @return PersonUtilityRes
	 */
	@RequestMapping(value = "/getPersonRaceEthnicity", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonUtilityRes getPersonRaceEthnicity(@RequestBody PersonUtilityReq personUtilityReq) {
		LOG.debug("Entering method getPersonRaceEthnicity in PersonUtilityController");
		if (TypeConvUtil.isNullOrEmpty(personUtilityReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		PersonUtilityRes personUtilityRes = new PersonUtilityRes();
		// personUtilityRes.setRaceEthnicityDto(personUtilityService.getPersonRaceEthnicity(personUtilityReq.getPersonId()));
		personUtilityRes.setTransactionId(personUtilityReq.getTransactionId());
		LOG.debug("Exiting method getPersonRaceEthnicity in PersonUtilityController");
		return personUtilityRes;
	}

	/**
	 * Method Name: isPRNRaceStatMissing Method Description:returns true if race
	 * and ethnicity data has been entered for Principals in given stage
	 * 
	 * @param personUtilityReq
	 * @return PersonUtilityRes
	 */
	@RequestMapping(value = "/isPRNRaceStatMissing", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonUtilityRes isPRNRaceStatMissing(@RequestBody PersonUtilityReq personUtilityReq) {
		LOG.debug("Entering method isPRNRaceStatMissing in PersonUtilityController");
		if (TypeConvUtil.isNullOrEmpty(personUtilityReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		PersonUtilityRes personUtilityRes = new PersonUtilityRes();
		personUtilityRes.setResult(personUtilityService.isPRNRaceStatMissing(personUtilityReq.getIdStage()));
		personUtilityRes.setTransactionId(personUtilityReq.getTransactionId());
		LOG.debug("Exiting method isPRNRaceStatMissing in PersonUtilityController");
		return personUtilityRes;
	}

	/**
	 * Method Name: isPlcmntPerson Method Description:to check if a person is
	 * associated with an open or closed PCSP placement
	 * 
	 * @param personUtilityReq
	 * @return PersonUtilityRes
	 */
	@RequestMapping(value = "/isPlcmntPerson", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PersonUtilityRes isPlcmntPerson(@RequestBody PersonUtilityReq personUtilityReq) {
		LOG.debug("Entering method isPlcmntPerson in PersonUtilityController");
		if (TypeConvUtil.isNullOrEmpty(personUtilityReq.getCdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("displayPCSPList.Stage.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personUtilityReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personUtilityReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		PersonUtilityRes personUtilityRes = new PersonUtilityRes();
		personUtilityRes.setResult(personUtilityService.isPlcmntPerson(personUtilityReq.getIdCase(),
				personUtilityReq.getIdPerson(), personUtilityReq.getCdStage()));
		personUtilityRes.setTransactionId(personUtilityReq.getTransactionId());
		LOG.debug("Exiting method isPlcmntPerson in PersonUtilityController");
		return personUtilityRes;
	}

	/**
	 * Method Name: isAssmntPerson Method Description:to check if a person is
	 * associated with PROC or COMP
	 * 
	 * @param personUtilityReq
	 * @return PersonUtilityRes
	 */
	@RequestMapping(value = "/isAssmntPerson", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PersonUtilityRes isAssmntPerson(@RequestBody PersonUtilityReq personUtilityReq) {
		LOG.debug("Entering method isAssmntPerson in PersonUtilityController");
		if (TypeConvUtil.isNullOrEmpty(personUtilityReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personUtilityReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		PersonUtilityRes personUtilityRes = new PersonUtilityRes();
		personUtilityRes.setResult(
				personUtilityService.isAssmntPerson(personUtilityReq.getIdPerson(), personUtilityReq.getIdStage()));
		personUtilityRes.setTransactionId(personUtilityReq.getTransactionId());
		LOG.debug("Exiting method isAssmntPerson in PersonUtilityController");
		return personUtilityRes;
	}
}
