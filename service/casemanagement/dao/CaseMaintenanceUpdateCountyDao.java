package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.CaseCountyUpdateInDto;
import us.tx.state.dfps.service.casepackage.dto.CaseCountyUpdateOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceUpdateCountyDao Feb 7, 2018- 5:46:34 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface CaseMaintenanceUpdateCountyDao {
	public void updateCounty(CaseCountyUpdateInDto caseCountyUpdateInDto,
			CaseCountyUpdateOutDto caseCountyUpdateOutDto);

}
