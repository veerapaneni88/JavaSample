package us.tx.state.dfps.service.icpc.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.common.request.IcpcDocUploadReq;
import us.tx.state.dfps.service.common.response.IcpcDocUploadRes;
import us.tx.state.dfps.service.icpc.service.IcpcDocumentUploadService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Controller
 * class for ICPC Document upload Aug 11, 2018- 3:10:42 PM © 2017 Texas
 * Department of Family and Protective Services
 */

@RestController
@RequestMapping("/ICPCDocUpload")
public class ICPCDoumentUploadController {

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-ICPCDocumentUpload");

	@Autowired
	private IcpcDocumentUploadService icpcDocumentUploadService;

	/**
	 * method name: fetchDocument description: This method fetches all
	 * strategies from Icpc Document and file storage *
	 * 
	 * @param IcpcDocUploadReq
	 * @return IcpcDocUploadRes
	 * 
	 */
	@RequestMapping(value = "/fetchDocument", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  IcpcDocUploadRes fetchDocument(@RequestBody IcpcDocUploadReq icpcDocUploadReq){
		LOG.debug("Entering fetch method");
		IcpcDocUploadRes icpcDocUploadRes = new IcpcDocUploadRes();
		icpcDocUploadRes = icpcDocumentUploadService.fetchDocument(icpcDocUploadReq);
		return icpcDocUploadRes;
	}

	/**
	 * method name: fetchDocument description: This method fetches all
	 * strategies from Icpc Document and file storage *
	 * 
	 * @param IcpcDocUploadReq
	 * @return IcpcDocUploadRes
	 * 
	 */
	@RequestMapping(value = "/saveDocument", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  IcpcDocUploadRes saveDocument(@RequestBody IcpcDocUploadReq icpcDocUploadReq) {
		LOG.debug("Entering  save method");
		IcpcDocUploadRes icpcDocUploadRes = new IcpcDocUploadRes();
		icpcDocUploadRes = icpcDocumentUploadService.saveDocument(icpcDocUploadReq);
		return icpcDocUploadRes;
	}

	@PostMapping(value = "/taskComplete")
	public void taskComplete(@RequestParam(name = "idEvent") Long idEvent,
										 @RequestParam(name = "cdType") String cdType,
										 @RequestParam(name = "userId") Long userId) {

		LOG.debug("Entering ICPC Doc Task complete");
		icpcDocumentUploadService.updateTaskComplete(idEvent, cdType, userId);
	}

}
