package us.tx.state.dfps.service.person.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.common.request.EducListDtlSaveiDto;
import us.tx.state.dfps.service.common.response.EducListDtlSaveoDto;
import us.tx.state.dfps.service.person.service.EducListDtlSaveService;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:
 * EducListDtlSaveController Class Name: EducListDtlSaveController Class
 * Description: A class to map the call to appropriate service call for insert,
 * save , updating Educational detail List Mar 24, 2017 - 3:19:51 PM
 */

@RestController
@RequestMapping("/educListdtlsavecontroller")
public class EducListDtlSaveController {

	@Autowired
	EducListDtlSaveService educListDtlSaveService;

	private static final Logger log = Logger.getLogger(EducListDtlSaveController.class);

	/**
	 * Method name: modifyEducationalDetail Method Description: This service
	 * will add/update/delete changed or added rows to the EDUCATIONAL HISTORY
	 * table for a given ID_EDHIST. A new ID_EDHIST will be triggered if a new
	 * row is added. the method is converted for CCFC18S
	 * 
	 * @param educListDtlSaveiDto
	 * @return
	 */
	@RequestMapping(value = "/educationaldetail_aud", method = RequestMethod.POST, headers = {
			"Accept=application/json" })
	
	public EducListDtlSaveoDto modifyEducationalDetail(@RequestBody EducListDtlSaveiDto educListDtlSaveiDto) {
		log.debug("Entering method modifyEducationalDetail in EducListDtlSaveController");

		EducListDtlSaveoDto educListDtlSaveoDto = educListDtlSaveService.saveEducationalDetail(educListDtlSaveiDto);

		log.debug("Exiting method modifyEducationalDetail in EducListDtlSaveController");
		return educListDtlSaveoDto;
	}

}
