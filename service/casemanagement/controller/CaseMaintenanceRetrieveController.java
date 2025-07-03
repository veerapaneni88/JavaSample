package us.tx.state.dfps.service.casemanagement.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.casemanagement.service.CaseMaintenanceRetrieveService;
import us.tx.state.dfps.service.common.request.CaseMaintenanceRetrieveReq;
import us.tx.state.dfps.service.common.response.CaseMaintenanceRetrieveRes;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: REST
 * service controller for display CaseMaintenance page Jan 22, 2018- 9:20:54 PM
 * © 2017 Texas Department of Family and Protective Services
 */

@RestController
@RequestMapping("/caseMaintenance")
public class CaseMaintenanceRetrieveController {

	@Autowired
	CaseMaintenanceRetrieveService objRetrieveCaseNameService;

	public static final String CLASSIFICATION_LRC = "LRC";
	public static final String CLASSIFICATION_LCC = "LCC";
	public static final String CLASSIFICATION_RCL = "RCL";
	public static final String CLASSIFICATION_CCL = "CCL";
	public static final String SUBCARE_STAGE = "SUB";
	public static final String ADOPTION_STAGE = "ADO";
	public static final String PAL_STAGE = "PAL";
	public static final String POST_ADOPT_STAGE = "PAD";
	public static final String PERM_CARE_ASSIST_STAGE = "PCA";
	public static final int NM_PERSON_FULL_LEN = 26;
	public static final String CASE_NM_ET_AL = " et al";
	public static final int CASE_NM_ET_AL_LEN = 6;
	public static final String CLASSIFICATION_APS_FAC = "AFC";
	public static final String PERSON_ROLE = "PC";

	private static final Logger log = Logger.getLogger(CaseMaintenanceRetrieveController.class);

	/**
	 * Method Name: caseMaintenanceRetrieve Method Description: Retrieves all
	 * principals related to the most recent stage of the case and the facility
	 * associated with the case.Tuxedo: CCMN85S
	 *
	 * @param caseMaintenanceRetrieveReq
	 * @return caseMaintenanceRetrieveRes
	 */
	@RequestMapping(value = "/retrieveCaseDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CaseMaintenanceRetrieveRes caseMaintenanceRetrieve(
			@RequestBody CaseMaintenanceRetrieveReq caseMaintenanceRetrieveReq) {
		log.debug("Entering method caseMaintenanceRetrieve in CaseMaintenanceRetrieveController");
		CaseMaintenanceRetrieveRes caseMaintenanceRetrieveRes = objRetrieveCaseNameService
				.callRetrieveCaseNameService(caseMaintenanceRetrieveReq);
		log.debug("Exiting method caseMaintenanceRetrieve in CaseMaintenanceRetrieveController");
		return caseMaintenanceRetrieveRes;
	}

}
