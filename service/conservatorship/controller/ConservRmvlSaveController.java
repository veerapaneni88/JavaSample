package us.tx.state.dfps.service.conservatorship.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.ConservRmvlSaveReq;
import us.tx.state.dfps.service.common.response.ConservRmvlSaveRes;
import us.tx.state.dfps.service.conservatorship.service.ConservRmvlSaveService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This is the
 * save service for Conservatorship Removal which includes creating a new
 * Conservatorship Removal Detail, Removal Reason record, Child Removal
 * Characteristic record & Adult Removal Characteristic record. Aug 15, 2017-
 * 8:32:33 PM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/conservRmvlSave")
public class ConservRmvlSaveController {

	@Autowired
	ConservRmvlSaveService conservRmvlSaveService;

	private static final Logger log = Logger.getLogger(ConservRmvlSaveController.class);

	/**
	 * 
	 * Method Name: conservRmvlSave Method Description:This is the save service
	 * for Conservatorship Removal which includes creating a new Conservatorship
	 * Removal Detail, Removal Reason record, Child Removal Characteristic
	 * record & Adult Removal Characteristic record. REST service for CSUB15S
	 * 
	 * @param conservRmvlSaveiDto
	 * @return ConservRmvlSaveRes
	 */
	@RequestMapping(value = "/save", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ConservRmvlSaveRes conservRmvlSave(@RequestBody ConservRmvlSaveReq conservRmvlSaveReq) {
		log.debug("Entering method conservRmvlSave in ConservRmvlSaveController");
		ConservRmvlSaveRes response = new ConservRmvlSaveRes();
		// Invoking service method
		response = conservRmvlSaveService.conservatorshipRemovalSave(conservRmvlSaveReq);
		log.debug("Exiting method conservRmvlSave in ConservRmvlSaveController");
		return response;
	}

}
