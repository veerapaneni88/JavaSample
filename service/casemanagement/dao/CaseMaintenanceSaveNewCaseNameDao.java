package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.CapsCaseUpdateInDto;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseUpdateOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceSaveNewCaseNameDao Feb 7, 2018- 5:45:34 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface CaseMaintenanceSaveNewCaseNameDao {
	public void saveNewCaseName(CapsCaseUpdateInDto capsCaseUpdateInDto, CapsCaseUpdateOutDto capsCaseUpdateOutDto);

}
