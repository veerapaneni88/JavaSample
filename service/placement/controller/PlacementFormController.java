package us.tx.state.dfps.service.placement.controller;

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
import us.tx.state.dfps.service.common.request.PlacementFormReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.placement.service.PlacementFormService;

/**
 * IMPACT PHASE 2 MODERNIZATION Class Description:This service is used to launch
 * the CVS Placement forms for Foster/Residential Care, Kinship/Non-Foster Care
 * and Legal Care Tux Service name - CSUB85S Form Name - CVS01O00,CVS02O00 and
 * CVS03O00 Oct 30, 2017- 3:26:43 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@RestController
@RequestMapping("/placementform")
public class PlacementFormController {

	@Autowired
	PlacementFormService placementFormService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(PlacementFormController.class);

	/**
	 * Method Name:getPlacementFormLegalRisk Method Descripton:Method to launch
	 * placement forms for Legal Care, launch placement forms for
	 * Foster/Residential Care and launch placement forms for Kinship/Non-Foster
	 * Care
	 * 
	 * @param placementFormReq
	 * @return commonFormRes
	 */
	@RequestMapping(value = "/getPlacementFormLegalRisk", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getPlacementFormLegalRisk(@RequestBody PlacementFormReq placementFormReq) {
		log.debug("Entering method CSUB85S in PlacementFormController");

		if (TypeConvUtil.isNullOrEmpty(placementFormReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(placementFormReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(placementFormReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(
				placementFormService.getSubCareLOCAuthorization(placementFormReq, placementFormReq.getFormName())));
		log.debug("Exiting method CSUB85S in PlacementFormController");
		return commonFormRes;
	}

}
