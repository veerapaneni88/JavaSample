package us.tx.state.dfps.service.admin.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.admin.dto.RecordsCheckRtrviDto;
import us.tx.state.dfps.service.admin.service.RecordsCheckRtrvService;
import us.tx.state.dfps.service.common.response.RecordsCheckListRes;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This service
 * will retrieve all rows from the Records Check Table for a given
 * IdRecCheckPerson(maximum page size retrieved is 11 rows) Aug 7, 2017- 1:14:58
 * PM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/recordscheckrtrv")
public class RecordsCheckRtrvController {

	@Autowired
	RecordsCheckRtrvService recordCheckRtrvService;

	private static final Logger log = Logger.getLogger("ServiceBusiness-RecordsCheckRtrvController");

	/**
	 * 
	 * Method Name: RecordsCheckRtrv Method Description:This service will
	 * retrieve all rows from the Records Check Table for a given
	 * IdRecCheckPerson(maximum page size retrieved is 11 rows).
	 * 
	 * @param recordCheckRtrviDto
	 * @return
	 * 
	 */
	@RequestMapping(value = "/recordscheckrtrv", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public RecordsCheckListRes RecordsCheckRtrv(@RequestBody RecordsCheckRtrviDto recordCheckRtrviDto){
		log.debug("Entering method RecordsCheckRtrv in RecordsCheckRtrvController");
		RecordsCheckListRes recordCheckRtrvListDto = new RecordsCheckListRes();
		recordCheckRtrvListDto = recordCheckRtrvService.callRecordsCheckRtrvService(recordCheckRtrviDto);
		log.debug("Exiting method RecordsCheckRtrv in RecordsCheckRtrvController");
		return recordCheckRtrvListDto;
	}
}
