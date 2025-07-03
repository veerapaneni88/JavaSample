package us.tx.state.dfps.service.admin.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.admin.dto.PersonDetailUpdateDto;
import us.tx.state.dfps.service.admin.dto.PersonDtlUpdateDto;
import us.tx.state.dfps.service.admin.service.PersonDetailUpdateService;
import us.tx.state.dfps.service.common.response.PersonDetailUpdateRes;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * Service calls to update a number of tables. A Row is entered or updated in
 * the Person, relationship, Stage person link, and the To Do. Aug 9, 2017-
 * 2:17:59 PM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/persondetailupdate")
public class PersonDetailUpdateController {

	@Autowired
	PersonDetailUpdateService personDetailUpdateService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(PersonDetailUpdateController.class);

	/**
	 * Method Name: CINV05S Method Description:This Service calls update a
	 * number of tables. A Row is entered or updated in the Person,
	 * relationship, Stage person link, and the To Do.
	 *
	 * @param personDetailUpdateiDto
	 * @return PersonDetailUpdateoRes
	 */
	@RequestMapping(value = "/persondetailupdateo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public PersonDetailUpdateRes personDetailUpdate(@RequestBody PersonDtlUpdateDto personDetailUpdateiDto) {
		log.debug("Entering method PersonDetailUpdateo in PersonDetailUpdateController");
		PersonDetailUpdateRes personDetailUpdateoRes = new PersonDetailUpdateRes();
		PersonDetailUpdateDto personDetailUpdateoDto = personDetailUpdateService
				.personDetailUpdate(personDetailUpdateiDto);
		personDetailUpdateoRes.setPersonDetailUpdateDto(personDetailUpdateoDto);
		log.debug("Exiting method PersonDetailUpdateo in PersonDetailUpdateController");
		return personDetailUpdateoRes;
	}
}
