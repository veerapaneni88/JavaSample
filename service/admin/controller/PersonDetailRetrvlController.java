package us.tx.state.dfps.service.admin.controller;

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
import us.tx.state.dfps.service.admin.service.PersonDetailRetrvlService;
import us.tx.state.dfps.service.common.request.PersonDetailsReq;
import us.tx.state.dfps.service.common.response.PersonDetailsRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Service
 * Retrieves all the information for the person detail window. Aug 3, 2017-
 * 9:53:33 PM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/persondetailretrvl")
public class PersonDetailRetrvlController {

	@Autowired
	PersonDetailRetrvlService personDetailRetrvlService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(PersonDetailRetrvlController.class);

	/**
	 * 
	 * Method Name: PersonDetailRetrvlo Method Description: This Service
	 * Retrieves all the information for the person detail window.
	 * 
	 * @param objPersonDetailRetrvliDto
	 * @return PersonDetailRetrvloRes
	 */
	@RequestMapping(value = "/persondetailretrvlo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public PersonDetailsRes PersonDetailRetrvlo(@RequestBody PersonDetailsReq objPersonDetailRetrvliDto) {
		log.debug("Entering method PersonDetailRetrvlo in PersonDetailRetrvlController");

		if (TypeConvUtil.isNullOrEmpty(objPersonDetailRetrvliDto.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("person.retrieve.details.IdPerson.mandatory", null, Locale.US));
		}
		PersonDetailsRes objPersonDetailRetrvloRes = personDetailRetrvlService
				.personDetailRetrvl(objPersonDetailRetrvliDto);

		log.debug("Exiting method PersonDetailRetrvlo in PersonDetailRetrvlController");
		return objPersonDetailRetrvloRes;
	}
}
