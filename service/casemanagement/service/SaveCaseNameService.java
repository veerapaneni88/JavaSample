package us.tx.state.dfps.service.casemanagement.service;

import us.tx.state.dfps.service.common.request.SaveCaseNameReq;
import us.tx.state.dfps.service.common.response.SaveCaseNameRes;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:SaveCaseNameService Feb 7, 2018- 5:56:35 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface SaveCaseNameService {
	public SaveCaseNameRes saveCaseNameService(SaveCaseNameReq saveCaseNameReq);

	public void updateCaseName(SaveCaseNameReq saveCaseNameReq, SaveCaseNameRes saveCaseNameRes);

	public void retrieveStageDtl(SaveCaseNameReq saveCaseNameReq, SaveCaseNameRes saveCaseNameRes);

	public void updateStageName(SaveCaseNameReq saveCaseNameReq, SaveCaseNameRes saveCaseNameRes);

	public void updateTodo(SaveCaseNameReq saveCaseNameReq, SaveCaseNameRes saveCaseNameRes);

	public void updateStagePersonLink(SaveCaseNameReq saveCaseNameReq, SaveCaseNameRes saveCaseNameRes);

	public void updatePersonLink(SaveCaseNameReq saveCaseNameReq, SaveCaseNameRes saveCaseNameRes);

}
