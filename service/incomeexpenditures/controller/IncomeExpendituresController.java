package us.tx.state.dfps.service.incomeexpenditures.controller;

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
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.IncomeExpenditureReq;
import us.tx.state.dfps.service.common.request.SaveIncRsrcReq;
import us.tx.state.dfps.service.common.response.IncomeExpenditureRes;
import us.tx.state.dfps.service.common.response.PersonIncomeResourceRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.FceUtil;
import us.tx.state.dfps.service.incomeexpenditures.service.IncomeExpendituresService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * IncomeExpendituresController will have all operation which are mapped to
 * ncomeExpenditures module. Nov 13, 2017- 4:20:29 PM © 2017 Texas Department of
 * Family and Protective Services
 */

@RestController
@RequestMapping("/incomeExpenditures")
public class IncomeExpendituresController {

	@Autowired
	IncomeExpendituresService incomeExpendituresService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	FceUtil fceUtil;

	private static final Logger log = Logger.getLogger(IncomeExpendituresController.class);

	/**
	 * 
	 * Method Description: This Method is used to load income expenditure detail
	 * under Foster care eligibility
	 * 
	 * @param incomeExpenditureReq
	 * @return incomeExpenditureRes
	 */

	@RequestMapping(value = "/readIncomeExpenditureDtl", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  IncomeExpenditureRes readIncomeExpDtl(@RequestBody IncomeExpenditureReq incomeExpenditureReq) {

		log.debug("Entering method readIncomeExpDtl in IncomeExpendituresController");
		if (TypeConvUtil.isNullOrEmpty(incomeExpenditureReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage(ServiceConstants.STAGEID_MANDATORY, null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(incomeExpenditureReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage(ServiceConstants.EVENTID_MANDATORY, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(incomeExpenditureReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}

		IncomeExpenditureRes incomeExpenditureRes = incomeExpendituresService.readIncomeExpDtl(incomeExpenditureReq);
		// Set fce application income and resource details for child and parent
		incomeExpendituresService.setIncomeAndResources(incomeExpenditureRes.getIncomeExpenditureDto());
		log.debug("Exiting method readIncomeExpDtl in IncomeExpendituresController");

		return incomeExpenditureRes;
	}

	/**
	 * 
	 * Method Description: This Method is used to save income expenditure detail
	 * under Foster care eligibility
	 * 
	 * @param incomeExpenditureReq
	 * @return incomeExpenditureRes
	 */

	@RequestMapping(value = "/saveIncomeExpenditureDtl", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  IncomeExpenditureRes saveIncomeExpDtl(@RequestBody IncomeExpenditureReq incomeExpenditureReq) {

		if (TypeConvUtil.isNullOrEmpty(incomeExpenditureReq.getIncomeExpenditureDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("incomeExpenditureDto.mandatory", null, Locale.US));
		}

		log.debug("Entering method saveIncomeExpDtl in IncomeExpendituresController");
		IncomeExpenditureRes incomeExpenditureRes = incomeExpendituresService.saveFceApplication(incomeExpenditureReq);
		log.debug("Exiting method saveIncomeExpDtl in IncomeExpendituresController");

		return incomeExpenditureRes;
	}

	/**
	 * Need to implement
	 * 
	 * Method Description: This Method is used to submit income expenditure
	 * detail
	 * 
	 * @param incomeExpenditureReq
	 * @return incomeExpenditureRes
	 */

	@RequestMapping(value = "/submitFceApplicationDtl", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  IncomeExpenditureRes submitFceApplicationDtl(
			@RequestBody IncomeExpenditureReq incomeExpenditureReq) {

		log.debug("Entering method submitFceApplicationDtl in IncomeExpendituresController");
		if (TypeConvUtil.isNullOrEmpty(incomeExpenditureReq.getIncomeExpenditureDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("incomeExpenditureDto.mandatory", null, Locale.US));
		}

		IncomeExpenditureRes incomeExpenditureRes = incomeExpendituresService
				.submitApplication(incomeExpenditureReq.getIncomeExpenditureDto());

		log.debug("Exiting method submitFceApplicationDtl in IncomeExpendituresController");

		return incomeExpenditureRes;
	}

	/**
	 * Need to implement
	 * 
	 * Method Description: This Method is used to calcualte income expenditure
	 * detail
	 * 
	 * @param incomeExpenditureReq
	 * @return incomeExpenditureRes
	 */

	@RequestMapping(value = "/calculateFceIncomeExpDtl", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  IncomeExpenditureRes calculateFceIncomeExpDtl(
			@RequestBody IncomeExpenditureReq incomeExpenditureReq) {

		log.debug("Entering method calculateFceIncomeExpDtl in IncomeExpendituresController");
		if (TypeConvUtil.isNullOrEmpty(incomeExpenditureReq.getIncomeExpenditureDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("incomeExpenditureDto.mandatory", null, Locale.US));
		}
		IncomeExpenditureRes incomeExpenditureRes = incomeExpendituresService.calcualteFceData(incomeExpenditureReq);
		log.debug("Exiting method calculateFceIncomeExpDtl in IncomeExpendituresController");

		return incomeExpenditureRes;
	}

	/**
	 * 
	 * Method Description: This Method is used to readDepCareDeductions
	 * 
	 * @param incomeExpenditureReq
	 * @return incomeExpenditureRes
	 */

	@RequestMapping(value = "/readDepCareDeduction", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  IncomeExpenditureRes readDepCareDeduction(
			@RequestBody IncomeExpenditureReq incomeExpenditureReq) {

		log.debug("Entering method readDepCareDeduction in IncomeExpendituresController");
		if (TypeConvUtil.isNullOrEmpty(incomeExpenditureReq.getIncomeExpenditureDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("incomeExpenditureDto.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(incomeExpenditureReq.getIncomeExpenditureDto().getIdFceEligibility())) {
			throw new InvalidRequestException(messageSource.getMessage("IdFceEligibility.mandatory", null, Locale.US));
		}
		IncomeExpenditureRes incomeExpenditureRes = incomeExpendituresService
				.readDepCareDeduction(incomeExpenditureReq);
		log.debug("Exiting method readDepCareDeduction in IncomeExpendituresController");

		return incomeExpenditureRes;
	}

	/**
	 * 
	 * Method Description: This Method is used to readDepCareDeductions
	 * 
	 * @param incomeExpenditureReq
	 * @return incomeExpenditureRes
	 */

	@RequestMapping(value = "/checkDepCareDeductionErrors", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  IncomeExpenditureRes checkDepCareDeductionErrors(
			@RequestBody IncomeExpenditureReq incomeExpenditureReq) {

		log.debug("Entering method calculateFceIncomeExpDtl in IncomeExpendituresController");
		IncomeExpenditureRes incomeExpenditureRes = incomeExpendituresService
				.checkDepCareDeductionErrors(incomeExpenditureReq.getIncomeExpenditureDto());
		log.debug("Exiting method calculateFceIncomeExpDtl in IncomeExpendituresController");

		return incomeExpenditureRes;
	}

	/**
	 * Method Name: getPersonIncomeResource Method Description:fetching persons
	 * income and resorces for idPerson
	 * 
	 * @param idPerson
	 * @return
	 */
	@RequestMapping(value = "/getPersonIncomeResorce", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonIncomeResourceRes getPersonIncomeResource(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq) || TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("person.idPerson.mandatory", null, Locale.US));
		}
		PersonIncomeResourceRes personIncomeResourceRes = new PersonIncomeResourceRes();
		personIncomeResourceRes
				.setPersonIncomeResourceDto(incomeExpendituresService.getIncomeResource(commonHelperReq.getIdPerson()));
		return personIncomeResourceRes;
	}

	/**
	 * Method Name: savePersonIncmAndRescr Method Description:Saves data persons
	 * income and resources
	 * 
	 * @param saveIncRsrcReq
	 */
	@RequestMapping(value = "/savePersonIncomeResorce", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public void savePersonIncmAndRescr(@RequestBody SaveIncRsrcReq saveIncRsrcReq) {
		if (TypeConvUtil.isNullOrEmpty(saveIncRsrcReq)
				|| TypeConvUtil.isNullOrEmpty(saveIncRsrcReq.getIncomeAndResourceDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		incomeExpendituresService.saveIncomeResource(saveIncRsrcReq);
	}

	/**
	 * Method Name: deletePersonIncmAndRescr Method Description:Deletes data
	 * persons income and resources
	 * 
	 * @param saveIncRsrcReq
	 */
	@RequestMapping(value = "/deletePersonIncomeResorce", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public void deletePersonIncmAndRescr(@RequestBody SaveIncRsrcReq saveIncRsrcReq) {
		if (TypeConvUtil.isNullOrEmpty(saveIncRsrcReq)
				|| TypeConvUtil.isNullOrEmpty(saveIncRsrcReq.getIncomeAndResourceDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		incomeExpendituresService.deleteIncomeResource(saveIncRsrcReq);
	}
}
