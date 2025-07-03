package us.tx.state.dfps.service.fce.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.DomicileDeprivationReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.DomicileDeprivationRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.domiciledeprivation.dto.DomicileDeprivationDto;
import us.tx.state.dfps.service.fce.service.DomicileDeprivationService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * executes the method in DomicileDeprivation Mar 15, 2018- 12:10:12 PM © 2017
 * Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/domicileDeprivation")
public class DomicileDeprivationController {

	@Autowired
	private DomicileDeprivationService domicileDeprivationService;

	@Autowired
	private MessageSource messageSource;

	/**
	 * Method Name: fetchDomicileDep Method Description: This method fetches the
	 * DomicileDeprivation details
	 * 
	 * @param commonHelperReq
	 * @return DomicileDeprivationRes
	 */
	@RequestMapping(value = "/fetchDomicile", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DomicileDeprivationRes fetchDomicileDep(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getUserId())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.lastUpdatePersonId.mandatory", null, Locale.US));
		}

		DomicileDeprivationRes domicileDeprivationRes = new DomicileDeprivationRes();
		DomicileDeprivationDto domicileDeprivationDto = domicileDeprivationService.fetchDomicileDeprivation(
				commonHelperReq.getIdStage(), commonHelperReq.getIdEvent(), Long.valueOf(commonHelperReq.getUserId()));

		domicileDeprivationRes.setDomicileDeprivationDto(domicileDeprivationDto);
		return domicileDeprivationRes;

	}

	/**
	 * Method Name: save Method Description: This method saves the new
	 * DomicileDeprivation details
	 * 
	 * @param domicileDeprivationReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/save", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes save(@RequestBody DomicileDeprivationReq domicileDeprivationReq) {
		if (TypeConvUtil.isNullOrEmpty(domicileDeprivationReq.getDomicileDeprivationDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.DomicileDeprivationDto.mandatory", null, Locale.US));
		}
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes = domicileDeprivationService
				.saveDomicileDeprivation(domicileDeprivationReq.getDomicileDeprivationDto());
		commonHelperRes.setTransactionId(domicileDeprivationReq.getTransactionId());
		return commonHelperRes;
	}
}