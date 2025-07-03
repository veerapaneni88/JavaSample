package us.tx.state.dfps.service.casemanagement.service;

import us.tx.state.dfps.service.common.request.CaseMaintenanceRetrieveReq;
import us.tx.state.dfps.service.common.response.CaseMaintenanceRetrieveRes;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 22, 2018- 10:43:41 PM © 2017 Texas Department of
 * Family and Protective Services
 */
// RetrieveCaseNameService
public interface CaseMaintenanceRetrieveService {
	public CaseMaintenanceRetrieveRes callRetrieveCaseNameService(CaseMaintenanceRetrieveReq pInputMsg);

	public void retrieveFacilityName(CaseMaintenanceRetrieveReq pInputMsg, CaseMaintenanceRetrieveRes pOutputMsg);

	public void retrieveNmResource(CaseMaintenanceRetrieveReq pInputMsg, CaseMaintenanceRetrieveRes pOutputMsg);

	public void retrieveNamesForChangeName(CaseMaintenanceRetrieveReq pInputMsg, CaseMaintenanceRetrieveRes pOutputMsg);

	public void getMostRecentlyClosedPreviousIdStage(CaseMaintenanceRetrieveReq pInputMsg,
			CaseMaintenanceRetrieveRes pOutputMsg);

	public void retrieveIncomingFacility(CaseMaintenanceRetrieveReq pInputMsg, CaseMaintenanceRetrieveRes pOutputMsg);

	public void retrieveCapsCase(CaseMaintenanceRetrieveReq pInputMsg, CaseMaintenanceRetrieveRes pOutputMsg);

	public void retrieveFromStage(CaseMaintenanceRetrieveReq pInputMsg, CaseMaintenanceRetrieveRes pOutputMsg);

	public void retrieveAllVictims(CaseMaintenanceRetrieveReq pInputMsg, CaseMaintenanceRetrieveRes pOutputMsg);

	public String appendEtAlToName(String szNameToAppend);

}
