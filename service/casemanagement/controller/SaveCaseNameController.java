package us.tx.state.dfps.service.casemanagement.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.casemanagement.service.SaveCaseNameService;
import us.tx.state.dfps.service.common.request.SaveCaseNameReq;
import us.tx.state.dfps.service.common.response.SaveCaseNameRes;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Save/Update
 * the given case name for the given case Feb 7, 2018- 5:40:26 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/caseNameMaintenance")
public class SaveCaseNameController {

	@Autowired
	SaveCaseNameService saveCaseNameService;

	private static final Logger log = Logger.getLogger(SaveCaseNameController.class);

	/**
	 * 
	 * Method Name:saveCaseName Method Description:Saves New Name to NM_CASE of
	 * CAPS CASE table and to NM STAGE all stages of the STAGE table (except
	 * subcare) if current stage is not Subcare. If the current stage is
	 * Subcare, save the new name only to the Subcare stage. Tuxedo: CCMN86S
	 * 
	 * @param saveCaseNameReq
	 * @return SaveCaseNameRes
	 */
	@RequestMapping(value = "/saveCaseName", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SaveCaseNameRes saveCaseName(@RequestBody SaveCaseNameReq saveCaseNameReq) {
		log.debug("Entering method saveCaseName in SaveCaseNameController");
		SaveCaseNameRes saveCaseNameRes = saveCaseNameService.saveCaseNameService(saveCaseNameReq);
		log.debug("Exiting method saveCaseName in SaveCaseNameController");
		return saveCaseNameRes;
	}

}
