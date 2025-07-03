package us.tx.state.dfps.service.fce.controller;

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
import us.tx.state.dfps.service.common.request.DependentCareReadReq;
import us.tx.state.dfps.service.common.response.DependentCareReadRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.fce.service.DepCareDeductionService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Provide
 * these services about read, insert, save and inspect operation about Deduction
 * for Dependent Care Cost Detail Feb 26, 2018- 11:40:42 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping(value = "/fce")
public class DepCareDeductionController {

	@Autowired
	MessageSource messageSource;
	@Autowired
	DepCareDeductionService depCareDeductionService;

	private static final Logger log = Logger.getLogger(DepCareDeductionController.class);

	/**
	 * Method Name:read Method Description:read Dependent Care Detail record
	 * 
	 * @param dependentCareReadReq
	 * @return dependentCareReadRes
	 */
	@RequestMapping(value = "/fceRead", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DependentCareReadRes display(@RequestBody DependentCareReadReq dependentCareReadReq) {
		log.debug("Inside the method display of DepCareDeductionController");
		if (TypeConvUtil.isNullOrEmpty(dependentCareReadReq.getIdFceApplication())) {
			throw new InvalidRequestException(messageSource.getMessage("IdFceApplication.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(dependentCareReadReq.getIdFceEligiblity())) {
			throw new InvalidRequestException(messageSource.getMessage("IdFceEligibility.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + dependentCareReadReq.getTransactionId());
		DependentCareReadRes dependentCareReadRes = new DependentCareReadRes();
		dependentCareReadRes = depCareDeductionService.readDepCare(dependentCareReadReq);
		return dependentCareReadRes;
	}

	/**
	 * Method Name:save Method Description:save Dependent Care Detail record
	 * 
	 * @param dependentCareReadReq
	 * @return dependentCareReadRes
	 */
	@RequestMapping(value = "/save", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DependentCareReadRes save(@RequestBody DependentCareReadReq dependentCareReadReq) {
		log.debug("Inside the method save of DepCareDeductionController");
		if (TypeConvUtil.isNullOrEmpty(dependentCareReadReq.getIdFceApplication())) {
			throw new InvalidRequestException(messageSource.getMessage("IdFceApplication.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(dependentCareReadReq.getIdFceEligiblity())) {
			throw new InvalidRequestException(messageSource.getMessage("IdFceEligibility.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + dependentCareReadReq.getTransactionId());
		DependentCareReadRes dependentCareReadRes = new DependentCareReadRes();
		dependentCareReadRes = depCareDeductionService.save(dependentCareReadReq);
		return dependentCareReadRes;
	}

	/**
	 * Method Name:getValidAdultDependent Method Description:get
	 * FceDepCareDeduct record for valid Adult and Dependent Person
	 * 
	 * @param dependentCareReadReq
	 * @return dependentCareReadRes
	 */
	@RequestMapping(value = "/getValidAdultDependent", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DependentCareReadRes getValidAdultDependent(
			@RequestBody DependentCareReadReq dependentCareReadReq) {
		log.debug("Inside the method getValidAdultDependent of DepCareDeductionController");
		if (TypeConvUtil.isNullOrEmpty(dependentCareReadReq.getIdFceAdultPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("IdFceAdultPerson.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(dependentCareReadReq.getIdFceDependentPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("IdFceDependentPerson.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + dependentCareReadReq.getTransactionId());
		DependentCareReadRes dependentCareReadRes = new DependentCareReadRes();
		dependentCareReadRes = depCareDeductionService.getValidAdultDependent(dependentCareReadReq);
		return dependentCareReadRes;
	}

	/**
	 * Method Name:syncDepCareDeductions Method Description:for an Eligibility
	 * Application re-check if deductions are valid based on fce person ages
	 * 
	 * @param dependentCareReadReq
	 * @return dependentCareReadRes
	 */
	@RequestMapping(value = "/syncDepCareDeductions", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DependentCareReadRes syncDepCareDeductions(
			@RequestBody DependentCareReadReq dependentCareReadReq) {
		log.debug("Inside the method syncDepCareDeductions of DepCareDeductionController");
		if (TypeConvUtil.isNullOrEmpty(dependentCareReadReq.getIdFceApplication())) {
			throw new InvalidRequestException(messageSource.getMessage("IdFceApplication.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(dependentCareReadReq.getIdFceEligiblity())) {
			throw new InvalidRequestException(messageSource.getMessage("IdFceEligibility.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + dependentCareReadReq.getTransactionId());
		DependentCareReadRes dependentCareReadRes = new DependentCareReadRes();
		dependentCareReadRes = depCareDeductionService.syncDepCareDeductions(dependentCareReadReq);
		return dependentCareReadRes;
	}

}
