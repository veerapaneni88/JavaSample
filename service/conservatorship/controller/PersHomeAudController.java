package us.tx.state.dfps.service.conservatorship.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.common.request.PersHomeAudReq;
import us.tx.state.dfps.service.common.response.PersHomeAudRes;
import us.tx.state.dfps.service.conservatorship.service.PersHomeAudService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PersHomeAudController used for add update delete person in the
 * home Mar 1, 2018- 5:22:46 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@RestController
@RequestMapping("/persHomeAud")
public class PersHomeAudController {

	@Autowired
	PersHomeAudService persHomeAudService;

	private static final Logger log = Logger.getLogger(PersHomeAudController.class);

	/**
	 * 
	 * Method Name: personInHomeSave Method Description:This is the Add, Update,
	 * Delete service for the Persons in Home Removal Window. It modifies the
	 * PERSON_HOME_RMVL table according to whether the user has added a person
	 * to the list to be included or deleted from the window list
	 * 
	 * @param persHomeAudReq
	 * @return PersHomeAudRes
	 */
	@RequestMapping(value = "/save", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PersHomeAudRes personInHomeSave(@RequestBody PersHomeAudReq persHomeAudReq) {
		log.debug("Entering method personInHomeSave in PersHomeAudController");
		PersHomeAudRes persHomeAudRes = persHomeAudService.personHomeAud(persHomeAudReq);
		log.debug("Exiting method personInHomeSave in PersHomeAudController");
		return persHomeAudRes;
	}

}
