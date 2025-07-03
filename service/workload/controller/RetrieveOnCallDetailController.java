package us.tx.state.dfps.service.workload.controller;

import java.util.List;
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
import us.tx.state.dfps.service.common.response.RetrieveOnCallDetailRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.workload.dto.RetrieveOnCallDetailDto;
import us.tx.state.dfps.service.workload.service.RetrieveOnCallDetailService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<The purpose
 * of this class is to retrieve OnCall details from EMP_ON_CALL_LINK table using
 * idOnCall> Aug 2, 2017- 8:35:42 PM © 2017 Texas Department of Family and
 * Protective Services
 */

@RestController
@RequestMapping("/retrieveoncalldetail")
public class RetrieveOnCallDetailController {

	@Autowired
	RetrieveOnCallDetailService retrieveOnCallDetailService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(RetrieveOnCallDetailController.class);

	/**
	 * Method Name: getOnCallDetailEmployeeList Method Description:This method
	 * is used to call the service implementation which is used to fetch the
	 * employee list from the EMP_ON_CALL_LINK table for a particular on call
	 * schedule.
	 * 
	 * @param retrieveOnCallDetailiDto
	 * @return retrieveOnCallDetailRes
	 */
	@RequestMapping(value = "/getemployeelistforoncall", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  RetrieveOnCallDetailRes getOnCallDetailEmployeeList(
			@RequestBody RetrieveOnCallDetailDto retrieveOnCallDetailDto) {
		log.debug("Entering method RetrieveOnCallDetailo in RetrieveOnCallDetailController");
		RetrieveOnCallDetailRes retrieveOnCallDetailRes = new RetrieveOnCallDetailRes();
		try {
			if (TypeConvUtil.isNullOrEmpty(retrieveOnCallDetailDto.getIdOnCall())) {
				throw new InvalidRequestException(
						messageSource.getMessage("ulIdOnCallDtls.ulIdOnCall.mandatory", null, Locale.US));
			}
			// Invoking service method
			List<RetrieveOnCallDetailDto> liResponse = retrieveOnCallDetailService
					.callRetrieveOnCallDetailService(retrieveOnCallDetailDto);
			if (!TypeConvUtil.isNullOrEmpty(liResponse)) {
				retrieveOnCallDetailRes.setResponse(liResponse);
			}
		} catch (DataNotFoundException e) {
			DataNotFoundException dataNotFoundException = new DataNotFoundException(
					messageSource.getMessage("retrieveOnCallDetails.ulIdOnCallDtls.data", null, Locale.US));
			dataNotFoundException.initCause(e);
			throw dataNotFoundException;
		}
		log.debug("Exiting method RetrieveOnCallDetailo in RetrieveOnCallDetailController");
		// returning the response from the service to the web layer.
		return retrieveOnCallDetailRes;
	}
}
