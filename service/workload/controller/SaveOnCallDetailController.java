package us.tx.state.dfps.service.workload.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.workload.dto.SaveOnCallDetailDto;
import us.tx.state.dfps.service.workload.dto.SaveOnCallDetailRes;
import us.tx.state.dfps.service.workload.service.SaveOnCallDetailService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is used to handle the service requests for add/update of On Call detail. Feb
 * 9, 2018- 1:31:52 PM © 2017 Texas Department of Family and Protective Services
 */

@RestController
@RequestMapping("/onCallScheduleDetail")
public class SaveOnCallDetailController {

	@Autowired
	SaveOnCallDetailService saveOnCallDetailServices;

	private static final Logger log = Logger.getLogger(SaveOnCallDetailController.class);

	/**
	 * This service is called to save the on call schedule details.
	 *
	 * @param saveOnCallDetailDto
	 *            - This dto is used to hold the on call detail information.
	 * @return saveOnCallDetailRes - This dto is used to hold the response of
	 *         the add/update of the on call detail request.
	 */
	@RequestMapping(value = "/saveOncallScheduleDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SaveOnCallDetailRes saveOnCallDetails(@RequestBody SaveOnCallDetailDto saveOnCallDetailDto) {
		log.debug("Entering method CCMN10S in SaveOnCallDetailController");
		// call the service implementation to save the on call schedule details
		SaveOnCallDetailRes saveOnCallDetailRes = saveOnCallDetailServices.saveOnCallDetail(saveOnCallDetailDto);
		log.debug("Exiting method CCMN10S in SaveOnCallDetailController");
		// returning the response from the service end to the business delegate
		return saveOnCallDetailRes;
	}

}
