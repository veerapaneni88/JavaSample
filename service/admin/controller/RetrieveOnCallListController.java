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
import us.tx.state.dfps.service.admin.dto.RetrieveOnCallListiDto;
import us.tx.state.dfps.service.admin.service.RetrieveOnCallListService;
import us.tx.state.dfps.service.common.response.RetrieveOnCallListRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:It retrieves
 * a full row of the ON_CALL table based on dynamic input Aug 17, 2017- 6:00:12
 * PM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/retrieveoncalllist")
public class RetrieveOnCallListController {

	@Autowired
	RetrieveOnCallListService objRetrieveOnCallListService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(RetrieveOnCallListController.class);

	/**
	 * Method Name:RetrieveOnCallList Description: It retrieves a full row of
	 * the ON_CALL table based on dynamic input
	 *
	 * @param objRetrieveOnCallListiDto
	 * @return RetrieveOnCallListRes
	 */
	@RequestMapping(value = "/retrieveoncalllist", headers = { "Accept=application/json" }, method = RequestMethod.POST)

	public  RetrieveOnCallListRes RetrieveOnCallList(
			@RequestBody RetrieveOnCallListiDto objRetrieveOnCallListiDto) {
		log.debug("Entering method RetrieveOnCallList in RetrieveOnCallListController");
		if (TypeConvUtil.isNullOrEmpty(objRetrieveOnCallListiDto.getSzCdOnCallCounty())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RetrieveOnCallListiDto.SzCdOnCallCounty.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(objRetrieveOnCallListiDto.getSzCdOnCallProgram())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RetrieveOnCallListiDto.SzCdOnCallProgram.mandatory", null, Locale.US));
		}
		RetrieveOnCallListRes retrieveOnCallListRes = objRetrieveOnCallListService
				.callRetrieveOnCallListService(objRetrieveOnCallListiDto);
		log.debug("Exiting method RetrieveOnCallList in RetrieveOnCallListController");
		return retrieveOnCallListRes;
	}
}
