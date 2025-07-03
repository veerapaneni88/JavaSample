package us.tx.state.dfps.service.personsearch.controller;

import java.util.Locale;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.PersonPartialReq;
import us.tx.state.dfps.service.common.request.PersonSearchReq;
import us.tx.state.dfps.service.common.response.PersonPartialRes;
import us.tx.state.dfps.service.common.response.PersonSearchRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.personsearch.service.PersonSearchService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * PersonSearchBean EJB implementation for functions required for implementing
 * functionality Oct 30, 2017- 6:44:05 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Api(tags = { "personsearch" })
@RestController
@RequestMapping("/personSearch")
public class PersonSearchController {

	@Autowired
	private PersonSearchService personSearchService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger("ServiceBusiness-PersonSearchControllerLog");

	/**
	 * 
	 * Method Name: getPersonIdentifier Method Description: This method gets the
	 * person identifier number for a given person identifier type
	 * 
	 * @param personSearchReq
	 * @return PersonSearchRes
	 */
	@RequestMapping(value = "/getPersonIdentifier", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonSearchRes getPersonIdentifier(@RequestBody PersonSearchReq personSearchReq) {
		log.debug("Entering method getPersonIdentifier in PersonSearchController");
		if (TypeConvUtil.isNullOrEmpty(personSearchReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("PersonSearchReq.ulIdPerson", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personSearchReq.getIdType())) {
			throw new InvalidRequestException(messageSource.getMessage("PersonSearchReq.idType", null, Locale.US));
		}
		PersonSearchRes personSearchRes = new PersonSearchRes();
		personSearchRes.setPersonIdentifier(
				personSearchService.getPersonIdentifier(personSearchReq.getIdPerson(), personSearchReq.getIdType()));
		log.debug("Exiting method getPersonIdentifier in PersonSearchController");
		return personSearchRes;
	}

	/**
	 * 
	 * Method Name: getPersonAddress Method Description:This method get person
	 * address details
	 * 
	 * @param personSearchReq
	 * @return PersonSearchRes
	 */
	@RequestMapping(value = "/getPersonAddress", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PersonSearchRes getPersonAddress(@RequestBody PersonSearchReq personSearchReq) {
		log.debug("Entering method getPersonAddress in PersonSearchController");
		if (TypeConvUtil.isNullOrEmpty(personSearchReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("PersonSearchReq.ulIdPerson", null, Locale.US));
		}
		PersonSearchRes personSearchRes = new PersonSearchRes();
		personSearchRes.setAddressValueDto(personSearchService.getPersonAddress(personSearchReq.getIdPerson()));
		log.debug("Exiting method getPersonAddress in PersonSearchController");
		return personSearchRes;
	}

	/**
	 * 
	 * Method Name: performPartialSearch Method Description:This method returns
	 * result of Partial Search
	 * 
	 * @param personPartialReq
	 * @return PersonPartialRes
	 */
	@ApiOperation(value = "Get person list for partial search", tags = { "personsearch" })
	@RequestMapping(value = "/performPartialSearch", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonPartialRes performPartialSearch(@RequestBody PersonPartialReq personPartialReq) {
		log.debug("Entering method performPartialSearch in PersonSearchController");
		if (TypeConvUtil.isNullOrEmpty(personPartialReq.getPaginationResultDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("personPartialReq.PaginationResultDto", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(personPartialReq.getPersonSearchInRecDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("personPartialReq.PersonSearchInRecDto", null, Locale.US));
		}
		PersonPartialRes personPartialRes = new PersonPartialRes();
		personPartialRes.setPersonSearchOutRecDto(personSearchService.performPartialSearch(
				personPartialReq.getPersonSearchInRecDto(), personPartialReq.getPaginationResultDto()));
		log.debug("Exiting method performPartialSearch in PersonSearchController");
		return personPartialRes;

	}

	/**
	 * Method Name: getPersonSearchView Method Description: Returns result of
	 * person view search
	 * 
	 * @param personSearchReq
	 * @return PersonSearchRes
	 */
	@ApiOperation(value = "Get person list from the identifier", tags = { "personsearch" })
	@RequestMapping(value = "/getPersonSearchView", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonSearchRes getPersonSearchView(@RequestBody PersonSearchReq personSearchReq) {
		log.debug("Entering method getPersonSearchView in PersonSearchController");
		if (TypeConvUtil.isNullOrEmpty(personSearchReq.getPaginationResultDto())) {
			throw new InvalidRequestException("personSearchReq.paginationResultBeanDto");
		}
		if (TypeConvUtil.isNullOrEmpty(personSearchReq.getPersonIdentifierValueNumber())) {
			throw new InvalidRequestException("personSearchReq.personIdentifierValueNumber");
		}
		PersonSearchRes personSearchRes = personSearchService.getSearchPersonView(personSearchReq);
		log.debug("Exiting method getPersonSearchView in PersonSearchController");
		return personSearchRes;
	}

	/**
	 * Method Name: populateAddtnlInfoIntakeSearch Method Description: Populates
	 * additional info from intake search
	 * 
	 * @param personSearchReq
	 * @return PersonSearchRes
	 */
	@RequestMapping(value = "/populateAddtnlInfoIntakeSearch", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonSearchRes populateAddtnlInfoIntakeSearch(@RequestBody PersonSearchReq personSearchReq) {
		log.debug("Entering method populateAddtnlInfoIntakeSearch in PersonSearchController");
		if (TypeConvUtil.isNullOrEmpty(personSearchReq.getPrsnSrchListpInitArrayDto())) {
			throw new InvalidRequestException("personSearchReq.prsnSrchListpInitArrayDto");
		}
		PersonSearchRes personSearchRes = personSearchService.populateAddtnlInfoIntakeSearch(personSearchReq);
		log.debug("Exiting method populateAddtnlInfoIntakeSearch in PersonSearchController");
		return personSearchRes;
	}

	/**
	 * Method Name: populateAddtnlInfoRegularSearch Method Description:
	 * Populates additional info from Regular search
	 * 
	 * @param personSearchReq
	 * @return PersonSearchRes
	 */
	@RequestMapping(value = "/populateAddtnlInfoRegularSearch", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonSearchRes populateAddtnlInfoRegularSearch(@RequestBody PersonSearchReq personSearchReq) {
		log.debug("Entering method populateAddtnlInfoRegularSearch in PersonSearchController");
		if (TypeConvUtil.isNullOrEmpty(personSearchReq.getPrsnSearchOutRecArrayDto())) {
			throw new InvalidRequestException("personSearchReq.prsnSrchListpInitArrayDto");
		}
		PersonSearchRes personSearchRes = personSearchService.populateAddtnlInfoRegularSearch(personSearchReq);
		log.debug("Exiting method populateAddtnlInfoRegularSearch in PersonSearchController");
		return personSearchRes;
	}

	/**
	 * Method Name: performDOBSearch Method Description: Returns result of DOB
	 * search
	 * 
	 * @param personSearchReq
	 * @return PersonSearchRes
	 */
	@ApiOperation(value = "Get person list from date of birth", tags = { "personsearch" })
	@RequestMapping(value = "/performDOBSearch", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PersonSearchRes performDOBSearch(@RequestBody PersonSearchReq personSearchReq) {
		log.debug("Entering method performDOBSearch in PersonSearchController");
		if (TypeConvUtil.isNullOrEmpty(personSearchReq.getPaginationResultDto())) {
			throw new InvalidRequestException("personSearchReq.paginationResultBeanDto");
		}
		if (TypeConvUtil.isNullOrEmpty(personSearchReq.getPersonSearchInRecDto())) {
			throw new InvalidRequestException("personSearchReq.getPersonSearchInRecDto");
		}
		PersonSearchRes personSearchRes = personSearchService.performDOBSearch(personSearchReq);
		log.debug("Exiting method performDOBSearch in PersonSearchController");
		return personSearchRes;
	}
	// FIX MESSAGE ERRORS
}
