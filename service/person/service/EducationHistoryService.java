package us.tx.state.dfps.service.person.service;

import us.tx.state.dfps.service.common.request.EducationHistoryReq;
import us.tx.state.dfps.service.common.response.EducationHistoryRes;

public interface EducationHistoryService {

	/**
	 * 
	 * Method Description: This Method will retrieve rows from the
	 * EDUCATIONAL_HISTORY table given an ID_PERSON Service Name: CCFC17S DAM:
	 * CLSC49D
	 * 
	 * @param educationHistoryReq
	 * @return EducationHistoryRes @
	 */

	public EducationHistoryRes getPersonEducationHistoryList(EducationHistoryReq educationHistoryReq);

}
