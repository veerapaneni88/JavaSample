package us.tx.state.dfps.service.personmergesplit.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.common.request.PersonMergeSplitFetchReq;
import us.tx.state.dfps.service.common.request.PersonMergeSplitReq;
import us.tx.state.dfps.service.common.response.MergePersonsRes;
import us.tx.state.dfps.service.common.response.PersonMergeSplitFetchRes;
import us.tx.state.dfps.service.common.response.PersonMergeSplitUpdateRes;
import us.tx.state.dfps.service.person.dto.PersonMergeSplitDto;
import us.tx.state.dfps.service.personmergesplit.dto.PersonMergeSplitReqDto;
import us.tx.state.dfps.service.personmergesplit.service.PersonMergeSplitService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * PersonMergeSplitBean. EJB implementation for functions required for
 * implementing Person Merge Split functionality Oct 3, 2017- 2:51:03 PM © 2017
 * Texas Department of Family and Protective Services
 */

@RestController
@RequestMapping("/personmergesplit")
public class PersonMergeSplitController {

	@Autowired
	PersonMergeSplitService personMergeSplitService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger("ServiceBusiness-PersonMergeSplitControllerLog");

	/**
	 * This function carries out the person merge. At high level it performs
	 * following sequence of steps - Make before merge snapshots for person
	 * forward and person closed - Call PersonMergeHelper to perform person and
	 * stage data updates - Save the warnings/informations if any thrown as part
	 * of validation step - Save the select forward fields as chosen by user -
	 * Save the areas updated by person merge - Save the after merge snapshots
	 * for the person forward
	 * 
	 * @param personMergeSplitReq
	 * @return MergePersonsRes
	 */
	@RequestMapping(value = "/mergepersons", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  MergePersonsRes mergePersons(@RequestBody PersonMergeSplitReqDto personMergeSplitReq) {
		log.info("Enter method mergePersons in Class PersonMergeSplitController");
		MergePersonsRes personMergeSplitRes = new MergePersonsRes();
		PersonMergeSplitDto mergePersonsDB = personMergeSplitService.mergePersons(personMergeSplitReq);
		personMergeSplitRes.setMergePersonsDB(mergePersonsDB);
		log.info("Exit method mergePersons in Class PersonMergeSplitController");
		return personMergeSplitRes;

	}

	/**
	 * This function carries out the person split.
	 * 
	 * @param personMergeSplitReq
	 * @return PersonMergeSplitUpdateRes
	 */
	@RequestMapping(value = "/splitpersonmerge", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PersonMergeSplitUpdateRes splitPersonMerge(
			@RequestBody PersonMergeSplitReq personMergeSplitReq) {
		log.info("Enter method splitPersonMerge in Class PersonMergeSplitController");
		PersonMergeSplitUpdateRes personMergeSplitRes = new PersonMergeSplitUpdateRes();
		long updateResult = personMergeSplitService.splitPersonMerge(personMergeSplitReq.getPersonMergeSplitDto());
		log.info("TransactionId :" + personMergeSplitReq.getTransactionId());
		personMergeSplitRes.setUpdateResult(updateResult);
		log.info("Exit method splitPersonMerge in Class PersonMergeSplitController");
		return personMergeSplitRes;

	}

	/**
	 * Method Name: getPersonMergeInfo Method Description: This method returns
	 * Person Merge row based on IdPersonMerge
	 * 
	 * @param personMergeSplitFetchReq
	 * @return PersonMergeSplitFetchRes
	 * 
	 */
	@RequestMapping(value = "/getPersonMergeInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PersonMergeSplitFetchRes getPersonMergeInfo(
			@RequestBody PersonMergeSplitFetchReq personMergeSplitFetchReq) {
		log.info("Enter method getPersonMergeInfo in Class PersonMergeSplitController");
		PersonMergeSplitFetchRes personMergeSplitFetchRes = new PersonMergeSplitFetchRes();
		personMergeSplitFetchRes.setPersonMergeSplitValueDto(personMergeSplitService.getPersonMergeInfo(
				personMergeSplitFetchReq.getIdPersonMerge(), personMergeSplitFetchReq.isHasSensitiveCaseAccessRight()));
		personMergeSplitFetchRes.setTransactionId(personMergeSplitFetchReq.getTransactionId());
		log.info("Exit method getPersonMergeInfo in Class PersonMergeSplitController");
		return personMergeSplitFetchRes;
	}

}