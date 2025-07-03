package us.tx.state.dfps.service.afistatement.service;

import us.tx.state.dfps.service.common.request.AFIStatementReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ApsFacilityInvestigationsStatement for form civ39o00 Mar 14,
 * 2018- 9:19:13 AM © 2017 Texas Department of Family and Protective Services
 */
public interface AFIStatementService {

	/**
	 * Method Name: getStatement Method Description: Populates form civ39o00
	 * 
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getStatement(AFIStatementReq aFIStatementReq);

}
