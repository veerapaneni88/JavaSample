package us.tx.state.dfps.service.fcl.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.SexualVictimHistoryReq;
import us.tx.state.dfps.service.common.response.SexualVictimHistoryRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.fcl.service.SexualVictimizationHistoryService;
import us.tx.state.dfps.web.fcl.dto.SexualVictimHistoryDto;

/**
 * 
 *
 *Class Description:<Service Controller Class for Sexual Victimization History Page>
 *Oct 23, 2019- 2:59:02 PM
 *© 2019 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/svh")
public class SexualVictimizationHistoryController {
	@Autowired
	SexualVictimizationHistoryService sexualVictimHistoryService;
	@Autowired
	MessageSource messageSource;
	private static final Logger log = Logger.getLogger(SexualVictimizationHistoryController.class);
	
	/**
	 * 
	 *Method Name:	getSexualVictimHistory
	 *Method Description: Service method for getting Sexual Victimization History Details by Person Id
	 *@param request
	 *@return SexualVictimHistoryRes
	 */
	@RequestMapping(value = "/getSexualVictimHistory", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SexualVictimHistoryRes getSexualVictimHistory(@RequestBody SexualVictimHistoryReq request) {
		log.info("TransactionId :" + request.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(request.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		SexualVictimHistoryDto sexualVictimHistoryDto = sexualVictimHistoryService
				.getSexualVictimHistoryDto(request.getIdPerson());
		SexualVictimHistoryRes response = new SexualVictimHistoryRes();
		response.setSexualVictimHistoryDto(sexualVictimHistoryDto);
		return response;
	}
	/**
	 * 
	 *Method Name:	updateSexualVictimHistory
	 *Method Description:Service method for updating Sexual Victimization History Details
	 *@param request
	 *@return SexualVictimHistoryRes
	 */
	@RequestMapping(value = "/updateSexualVictimHistory", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SexualVictimHistoryRes updateSexualVictimHistory(@RequestBody SexualVictimHistoryReq request) {
		log.info("TransactionId :" + request.getTransactionId());
		// artf283322 use setIndUnconfirmedTrafficking to indicate this is a user initiated update, since their setting for unconfirmed trafficking is treated differently.
		request.getSexualVictimHistoryDto().setIndUserRequest(true);
		sexualVictimHistoryService.saveOrUpdateSexualVictimHistory(request.getSexualVictimHistoryDto());
		SexualVictimHistoryRes response = new SexualVictimHistoryRes();
		return response;
	}
	
	/**
	 * 
	 *Method Name:	deleteSvhIncident
	 *Method Description:Service method for deleting Sexual Victimization History Details
	 *@param request
	 *@return SexualVictimHistoryRes
	 */
	@RequestMapping(value = "/deleteSvhIncident", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SexualVictimHistoryRes deleteSvhIncident(@RequestBody SexualVictimHistoryReq request) {
		log.info("TransactionId :" + request.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(request.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		sexualVictimHistoryService.deleteIncidents(request.getIncidentIds(), request.getIdPerson());
		SexualVictimHistoryRes response = new SexualVictimHistoryRes();
		return response;
	}
}
