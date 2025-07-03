package us.tx.state.dfps.service.admin.controller;

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
import us.tx.state.dfps.service.admin.dto.CpsInvConclSelectiDto;
import us.tx.state.dfps.service.admin.dto.CpsInvConclSelectoDto;
import us.tx.state.dfps.service.admin.service.CpsInvConclSelectService;
import us.tx.state.dfps.service.common.response.CpsInvConclSelectoRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 *
 * Class Description:This service is used in the Predisplay callback of window
 * CINV06W - CPS INV CONCLUSION. It retrieves all the values necessary to
 * populate window. Aug 6, 2017- 12:35:27 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@RestController
@RequestMapping("/cpsinvconclselect")
public class CpsInvConclSelectController {

	@Autowired
	CpsInvConclSelectService objCpsInvConclSelectService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(CpsInvConclSelectController.class);

	/**
	 * 
	 * Method Name: CpsInvConclSelecto Method Description: This service is used
	 * in the Predisplay callback of window CINV06W - CPS INV CONCLUSION. It
	 * retrieves all the values necessary to populate window.
	 * 
	 * @param objCpsInvConclSelectiDto
	 * @return objCpsInvConclSelectiDto
	 */
	@RequestMapping(value = "/cpsinvconclselecto", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CpsInvConclSelectoRes CpsInvConclSelecto(@RequestBody CpsInvConclSelectiDto objCpsInvConclSelectiDto) {
		log.debug("Entering method CpsInvConclSelecto in CpsInvConclSelectController");
		CpsInvConclSelectoRes response = new CpsInvConclSelectoRes();
		CpsInvConclSelectoDto objCpsInvConclSelectoDto = new CpsInvConclSelectoDto();
		if (TypeConvUtil.isNullOrEmpty(objCpsInvConclSelectiDto.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("CpsInvConclSelect.ulIdEvent.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(objCpsInvConclSelectiDto.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("CpsInvConclSelect.ulIdStage.mandatory", null, Locale.US));
		}
		// Invoking service method
		objCpsInvConclSelectoDto = objCpsInvConclSelectService.callCpsInvConclSelectService(objCpsInvConclSelectiDto);
		if (!TypeConvUtil.isNullOrEmpty(objCpsInvConclSelectoDto)) {
			response.setResponse(objCpsInvConclSelectoDto);
		}
		log.debug("Exiting method CpsInvConclSelecto in CpsInvConclSelectController");
		return response;
	}
}
