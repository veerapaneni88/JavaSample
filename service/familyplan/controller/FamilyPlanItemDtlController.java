/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:Class to get family plan item detail
 *Jul 6, 2018- 12:15:26 PM
 *
 */
package us.tx.state.dfps.service.familyplan.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.FamilyPlanItemDtlReq;
import us.tx.state.dfps.service.common.response.FamilyPlanItemDtlRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.familyplan.service.FamilyPlanItemDtlService;
import us.tx.state.dfps.service.forms.dto.FamilyPlanItemDto;

@RestController
@RequestMapping("/familyplanitem")
public class FamilyPlanItemDtlController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	FamilyPlanItemDtlService familyPlanItemDtlService;

	/**
	 * Method Name: getFamilyPlanItemDtl
	 * 
	 * Method Description:This method is used to get the family plan item
	 * detail/ Area of concern detail
	 * 
	 * @param familyPlanItemDtlReq
	 * @return familyPlanItemDtlRes
	 */

	@RequestMapping(value = "/details", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanItemDtlRes getFamilyPlanItemDtl(
			@RequestBody FamilyPlanItemDtlReq familyPlanItemDtlReq) {
		FamilyPlanItemDtlRes resp = new FamilyPlanItemDtlRes();
		if (TypeConvUtil.isNullOrEmpty(familyPlanItemDtlReq.getFamilyPlanDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(familyPlanItemDtlReq.getFamilyPlanDto().getCdSelectedAreaOfConcern())) {
			throw new InvalidRequestException(
					messageSource.getMessage("areaofconcern.input.mandatory", null, Locale.US));
		}
		FamilyPlanItemDto familyPlanItemDto = familyPlanItemDtlService
				.getFamilyPlanItemDtl(familyPlanItemDtlReq.getFamilyPlanDto(), familyPlanItemDtlReq.getPageMode());
		resp.setFamilyPlanItemDto(familyPlanItemDto);
		return resp;

	}

	/**
	 * 
	 * Method Name: saveFamilyPlanItem Method Description: This method is used
	 * to call the service for saving the family plan item detail.
	 * 
	 * @param familyPlanItemDtlReq
	 * @return
	 */
	@RequestMapping(value = "/saveFamilyPlanItems", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanItemDtlRes saveFamilyPlanItem(
			@RequestBody FamilyPlanItemDtlReq familyPlanItemDtlReq) {
		return familyPlanItemDtlService.saveFamilyPlanItemDtl(familyPlanItemDtlReq.getFamilyPlanItemDto(),
				familyPlanItemDtlReq.getFamilyPlanDto());
	}

	/**
	 * 
	 * Method Name: deleteFamilyPlanItem Method Description:This method is used
	 * to call the service for saving the family plan item detail.
	 * 
	 * @param familyPlanItemDtlReq
	 * @return
	 */
	@RequestMapping(value = "/deleteFamilyPlanItem", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanItemDtlRes deleteFamilyPlanItem(
			@RequestBody FamilyPlanItemDtlReq familyPlanItemDtlReq) {
		FamilyPlanItemDtlRes resp = new FamilyPlanItemDtlRes();
		resp.setIdFamilyPlanItem(familyPlanItemDtlService.deleteFamilyPlanItem(
				familyPlanItemDtlReq.getFamilyPlanItemDto(), familyPlanItemDtlReq.getFamilyPlanDto()));
		return resp;

	}

}
