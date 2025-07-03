package us.tx.state.dfps.service.familytree.controller;

import java.util.List;
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
import us.tx.state.dfps.service.common.request.FamilyTreeRelationsReq;
import us.tx.state.dfps.service.common.request.PersonDetailsReq;
import us.tx.state.dfps.service.common.response.FTRelationshipRes;
import us.tx.state.dfps.service.common.response.FamilyTreeRelationsRes;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationBean;
import us.tx.state.dfps.service.familyTree.bean.PersonRelSuggestionDto;
import us.tx.state.dfps.service.familytree.service.FTRelationshipService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is used to handle the service requests related to Family Tree Relationships
 * details including retrieve , save/update Feb 12, 2018- 2:35:22 PM © 2017
 * Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/familytreerelationship")
public class FamilyTreeRelationsController {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private FTRelationshipService ftRelationshipService;

	private static final Logger logger = Logger.getLogger(FamilyTreeRelationsController.class);

	/**
	 * Method Name: getFamilyRelationshipDetails Method Description:This method
	 * will be called to fetch the data on "Family Tree Relationship Details"
	 * page. It retrieves Context Person List, Associated Person List based on
	 * the Family Tree Context. It also retrieves single Record of Relationship
	 * if the id is passed.
	 * 
	 * @param familyTreeRelationsReq
	 *            - This request object will hold the input parameters for
	 *            retrieving the Family Tree Relationship details.
	 * @return FamilyTreeRelationsRes - This response object will hold the dto
	 *         containing the family tree relationship information.
	 */
	@RequestMapping(value = "/getrelationshipdetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyTreeRelationsRes getFamilyRelationshipDetails(
			@RequestBody FamilyTreeRelationsReq familyTreeRelationsReq) {
		// If the request dto is null , then throw exception that the mandatory
		// parameters is not present.
		if (TypeConvUtil.isNullOrEmpty(familyTreeRelationsReq.getFamilyTreeRelationshipDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("ftreldetails.ftRelationshipDBDto.notFound", null, Locale.US));
		}
		logger.info("Exiting method fetchRelationshipDetails in FTRelDetailsController");
		// Calling the service implementation to get the family tree
		// relationship
		// details.
		FamilyTreeRelationsRes familyTreeRelationsRes = ftRelationshipService
				.getRelationshipDetails(familyTreeRelationsReq);
		return familyTreeRelationsRes;
	}

	/**
	 * 
	 * Method Name: fetchRelationshipInfo Method Description:This method returns
	 * All the information that needs to be displayed on Family Tree
	 * Relationship page including Context Person List, Relationships and
	 * Suggested Relationships.
	 * 
	 * @param fTRelationshipDBReq
	 * @return FTRelationshipDBRes
	 */
	@RequestMapping(value = "/fetchRelationshipInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyTreeRelationsRes fetchRelationshipInfo(
			@RequestBody FamilyTreeRelationsReq familyTreeRelationsReq) {
		logger.debug("Entering method fetchRelationshipInfo in FTRelationsController");
		if (TypeConvUtil.isNullOrEmpty(familyTreeRelationsReq.getFamilyTreeRelationshipDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("FtRelationshipDBDto.is.mandatory", null, Locale.US));
		}
		FamilyTreeRelationsRes ftRelationshipDBRes = new FamilyTreeRelationsRes();
		ftRelationshipDBRes.setTransactionId(familyTreeRelationsReq.getTransactionId());
		logger.info("TransactionId :" + familyTreeRelationsReq.getTransactionId());
		ftRelationshipDBRes.setFamilyTreeRelationshipDto(
				ftRelationshipService.fetchRelationshipInfo(familyTreeRelationsReq.getFamilyTreeRelationshipDto()));

		logger.debug("Exiting method fetchRelationshipInfo in FTRelationsController");
		return ftRelationshipDBRes;
	}

	/**
	 * Method Name: getExistingRelationsList Method Description:This method will
	 * be invoked to get the existing relationships between two persons which
	 * are valid and not end dated.
	 * 
	 * @param familyTreeRelationsReq
	 *            - This request object will hold the input parameters for
	 *            retrieving the existing relationship list.
	 * @return FamilyTreeRelationsRes - This response object will hold the list
	 *         containing the existing relationship details.
	 */
	@RequestMapping(value = "/getexistingrelationships", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyTreeRelationsRes getExistingRelationsList(
			@RequestBody FamilyTreeRelationsReq familyTreeRelationsReq) {
		FamilyTreeRelationsRes familyTreeRelationsRes = new FamilyTreeRelationsRes();
		List<FTPersonRelationBean> relationsList = ftRelationshipService.getExistingRelationships(
				familyTreeRelationsReq.getFamilyTreeRelationshipDto().getFtPersonRelationBean());
		familyTreeRelationsRes.setRelationsList(relationsList);
		;
		return familyTreeRelationsRes;
	}

	/**
	 * Method Name: saverelationship Method Description:This method will be
	 * invoked to save/update the relationship details.
	 * 
	 * @param familyTreeRelationsReq
	 *            - This request object will hold the relationship details to be
	 *            saved/updated .
	 * @return FamilyTreeRelationsRes - This response object will hold the
	 *         unique identifier which will be generated/available after
	 *         saving/updating the relationship details.
	 */
	@RequestMapping(value = "/saverelationship", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyTreeRelationsRes saverelationship(
			@RequestBody FamilyTreeRelationsReq familyTreeRelationsReq) {
		FamilyTreeRelationsRes familyTreeRelationsRes = new FamilyTreeRelationsRes();
		Long idPersonRelation = ftRelationshipService
				.saverelationship(familyTreeRelationsReq.getFamilyTreeRelationshipDto());
		familyTreeRelationsRes.setIdPersonRelation(idPersonRelation);

		return familyTreeRelationsRes;
	}

	/**
	 * Method Name: isRelIntMatches Method Description:This method would be
	 * invoked to check if the relInt entered matches with the data available in
	 * the database.
	 * 
	 * @param familyTreeRelationsReq
	 *            - This request object will hold the input parameters for
	 *            retrieving the existing relInt from the
	 *            PERSON_RELATION_RELINT_DEF table.
	 * @return FamilyTreeRelationsRes - This response object will hold the
	 *         boolean value to indicate if the relInt matches or not.
	 */
	@RequestMapping(value = "/relintmatches", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyTreeRelationsRes isRelIntMatches(
			@RequestBody FamilyTreeRelationsReq familyTreeRelationsReq) {
		FamilyTreeRelationsRes familyTreeRelationsRes = new FamilyTreeRelationsRes();
		boolean isRelIntMatches = ftRelationshipService
				.isRelIntMatches(familyTreeRelationsReq.getFamilyTreeRelationshipDto().getFtPersonRelationBean());
		familyTreeRelationsRes.setRelIntMatches(isRelIntMatches);

		return familyTreeRelationsRes;
	}

	/**
	 * Method Name: getPossibleRelation Method Description:This method will be
	 * invoked to retrieve a possible relation based on the input parameters
	 * passed in the request.
	 * 
	 * @param familyTreeRelationsReq
	 *            - This request object will hold the input parameters for
	 *            retrieving the possible relation from the
	 *            PERSON_RELATION_SUG_DEF table.
	 * @return FamilyTreeRelationsRes - This response object will hold the dto
	 *         containing the possible relation match details.
	 */
	@RequestMapping(value = "/getpossiblerelation", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyTreeRelationsRes getPossibleRelation(
			@RequestBody FamilyTreeRelationsReq familyTreeRelationsReq) {
		FamilyTreeRelationsRes familyTreeRelationsRes = new FamilyTreeRelationsRes();
		PersonRelSuggestionDto personRelSuggestionDto = ftRelationshipService
				.getPossibleRelation(familyTreeRelationsReq);
		familyTreeRelationsRes.setPersonRelSuggestionDto(personRelSuggestionDto);

		return familyTreeRelationsRes;
	}

	/**
	 * 
	 * Method Name: saveSuggestedRelations Method Description:This method
	 * inserts suggested relationship details into the database.
	 * 
	 * @param fTRelationshipDBReq
	 * @return FTRelationshipDBRes
	 */
	@RequestMapping(value = "/saveSuggestedRelations", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyTreeRelationsRes saveSuggestedRelations(
			@RequestBody FamilyTreeRelationsReq fTRelationshipDBReq) {
		logger.debug("Entering method saveSuggestedRelations in FTRelationshipController");
		if (TypeConvUtil.isNullOrEmpty(fTRelationshipDBReq.getFamilyTreeRelationshipDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("FamilyTreeRelationshipDto.is.mandatory", null, Locale.US));
		}
		FamilyTreeRelationsRes ftRelationshipDBRes = new FamilyTreeRelationsRes();
		ftRelationshipDBRes.setTransactionId(fTRelationshipDBReq.getTransactionId());
		logger.info("TransactionId :" + fTRelationshipDBReq.getTransactionId());
		ftRelationshipDBRes.setRowQty(
				ftRelationshipService.saveSuggestedRelations(fTRelationshipDBReq.getFamilyTreeRelationshipDto()));
		logger.debug("Exiting method saveSuggestedRelations in FTRelationshipController");
		return ftRelationshipDBRes;
	}

	/**
	 * 
	 * Method Name: getPersonDetails Method Description: This method gives the
	 * person details.
	 * 
	 * @param fTRelationshipDBReq
	 * @return FTRelationshipDBRes 
	 */
	@RequestMapping(value = "/getPersonDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyTreeRelationsRes getPersonDetails(
			@RequestBody FamilyTreeRelationsReq fTRelationshipDBReq) {
		logger.debug("Entering method getPersonDetails in FTRelationshipController");
		if (TypeConvUtil.isNullOrEmpty(fTRelationshipDBReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("FamilyTreeRelationshipDto.IdPerson.is.mandatory", null, Locale.US));
		}
		FamilyTreeRelationsRes ftRelationshipDBRes = new FamilyTreeRelationsRes();
		ftRelationshipDBRes.setTransactionId(fTRelationshipDBReq.getTransactionId());
		logger.info("TransactionId :" + fTRelationshipDBReq.getTransactionId());
		ftRelationshipDBRes.setPersonValueDto(ftRelationshipService.getPersonDetails(fTRelationshipDBReq.getIdPerson(),
				fTRelationshipDBReq.getIdStage()));
		logger.debug("Exiting method saveSuggestedRelations in FTRelationshipController");
		return ftRelationshipDBRes;
	}

	/**
	 * 
	 * Method Name: generateRelSuggestionsRelint Method Description:This
	 * function suggests the relationships based on Rel/Int values.
	 * 
	 * @param fTRelationshipDBReq
	 * @return FTRelationshipDBRes
	 */
	@RequestMapping(value = "/generateRelSuggestionsRelint", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyTreeRelationsRes generateRelSuggestionsRelint(
			@RequestBody FamilyTreeRelationsReq fTRelationshipDBReq) {
		logger.debug("Entering method generateRelSuggestionsRelint in FTRelationshipController");
		if (TypeConvUtil.isNullOrEmpty(fTRelationshipDBReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("idStage.is.mandatory", null, Locale.US));
		}
		FamilyTreeRelationsRes ftRelationshipDBRes = new FamilyTreeRelationsRes();
		ftRelationshipDBRes.setTransactionId(fTRelationshipDBReq.getTransactionId());
		logger.info("TransactionId :" + fTRelationshipDBReq.getTransactionId());
		ftRelationshipDBRes.setfTPersonRelationDtoList(
				ftRelationshipService.generateRelSuggestionsRelint(fTRelationshipDBReq.getIdStage()));
		logger.debug("Exiting method generateRelSuggestionsRelint in FTRelationshipController");
		return ftRelationshipDBRes;
	}

	/**
	 * 
	 * Method Name: genSuggestionsBasedonExistingRel Method Description:This
	 * function suggests the relationships based on existing Relationships.
	 * 
	 * @param fTRelationshipDBReq
	 * @return FTRelationshipDBRes
	 */
	@RequestMapping(value = "/genSuggestionsBasedonExistingRel", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyTreeRelationsRes genSuggestionsBasedonExistingRel(
			@RequestBody FamilyTreeRelationsReq fTRelationshipDBReq) {
		logger.debug("Entering method genSuggestionsBasedonExistingRel in FTRelationshipController");
		if (TypeConvUtil.isNullOrEmpty(fTRelationshipDBReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("idSatage.is.mandatory", null, Locale.US));
		}
		FamilyTreeRelationsRes ftRelationshipDBRes = new FamilyTreeRelationsRes();
		ftRelationshipDBRes.setTransactionId(fTRelationshipDBReq.getTransactionId());
		logger.info("TransactionId :" + fTRelationshipDBReq.getTransactionId());
		ftRelationshipDBRes.setfTPersonRelationDtoList(
				ftRelationshipService.genSuggestionsBasedonExistingRel(fTRelationshipDBReq.getIdStage()));
		logger.debug("Exiting method genSuggestionsBasedonExistingRel in FTRelationshipController");
		return ftRelationshipDBRes;
	}

	/**
	 * 
	 * Method Name: fetchRelationshipsForGraph Method Description:This method
	 * returns Relationships for Basic/Extended Graphs.
	 * 
	 * @param fTRelationshipDBReq
	 * @return FTRelationshipDBRes
	 */
	@RequestMapping(value = "/fetchRelationshipsForGraph", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyTreeRelationsRes fetchRelationshipsForGraph(
			@RequestBody FamilyTreeRelationsReq fTRelationshipDBReq) {
		if (TypeConvUtil.isNullOrEmpty(fTRelationshipDBReq.getFamilyTreeRelationshipDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("FtRelationshipDBDto.is.mandatory", null, Locale.US));
		}
		FamilyTreeRelationsRes ftRelationshipDBRes = new FamilyTreeRelationsRes();
		ftRelationshipDBRes.setFamilyTreeRelationshipDto(
				ftRelationshipService.fetchRelationshipsForGraph(fTRelationshipDBReq.getFamilyTreeRelationshipDto()));
		return ftRelationshipDBRes;
	}

	/**
	 * Method Name: fetchRelationshipsForReport Method Description:This method
	 * is used to fetch the family tree relationships details which will be used
	 * for generating the report.
	 * 
	 * @param familyTreeRelationsReq
	 *            - The request dto will have the input parameters for fetching
	 *            the relationship details for generating the report.
	 * @return familyTreeRelationsRes - The response dto will hold the family
	 *         tree relationship details .
	 */
	@RequestMapping(value = "/fetchRelationshipsForReport", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyTreeRelationsRes fetchRelationshipsForReport(
			@RequestBody FamilyTreeRelationsReq familyTreeRelationsReq) {

		/*
		 * Checking if the input dto is empty then throwing an exception to the
		 * web layer that the request is invalid.
		 */
		if (ObjectUtils.isEmpty(familyTreeRelationsReq.getFamilyTreeRelationshipDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("FtRelationshipDBDto.is.mandatory", null, Locale.US));
		}
		FamilyTreeRelationsRes familyTreeRelationsRes = new FamilyTreeRelationsRes();
		/*
		 * Calling the 'fetchRelationshipsForReport' method in the service
		 * implementation to get the family relationship details for report
		 * generation.
		 */
		familyTreeRelationsRes.setFamilyTreeRelationshipDto(ftRelationshipService
				.fetchRelationshipsForReport(familyTreeRelationsReq.getFamilyTreeRelationshipDto()));
		logger.debug("Exiting method fetchRelationshipsForReport in FamilyTreeRelationsController");
		// returning the response from the service.
		return familyTreeRelationsRes;
	}

	/**
	 * Method Name: fetchAllDirectPersonRelationShip Method Description:This
	 * method will be invoked to get the existing relationships for a person
	 * which are valid and not end dated.
	 * 
	 * @param familyTreeRelationsReq
	 *            - This request object will hold the input parameters for
	 *            retrieving the existing relationship list.
	 * @return FamilyTreeRelationsRes - This response object will hold the list
	 *         containing the existing relationship details.
	 */
	@RequestMapping(value = "/getAllDirectPersonRelationships", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyTreeRelationsRes fetchAllDirectPersonRelationShip(
			@RequestBody FamilyTreeRelationsReq familyTreeRelationsReq) {
		FamilyTreeRelationsRes familyTreeRelationsRes = new FamilyTreeRelationsRes();
		List<FTPersonRelationBean> relationsList = ftRelationshipService.fetchAllDirectPersonRelationShip(
				familyTreeRelationsReq.getFamilyTreeRelationshipDto().getFtPersonRelationBean());
		familyTreeRelationsRes.setRelationsList(relationsList);
		return familyTreeRelationsRes;
	}

	/**
	 * Method Name: isStaff Method Description: This methods checks whether the
	 * given person is a staff
	 * 
	 * @param personDetailsReq
	 * @return RelDetailsRes
	 */
	@RequestMapping(value = "/isStaff", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FTRelationshipRes isStaff(@RequestBody PersonDetailsReq personDetailsReq) {
		logger.debug("Entering method isStaff in FTRelDetailsController");
		FTRelationshipRes relDetailsRes = new FTRelationshipRes();
		relDetailsRes.setStaff(ftRelationshipService.isStaff(personDetailsReq.getIdPerson()));
		logger.debug("Exiting method isStaff in FTRelDetailsController");
		return relDetailsRes;
	}

}
