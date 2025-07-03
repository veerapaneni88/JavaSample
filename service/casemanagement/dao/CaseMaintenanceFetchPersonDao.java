package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.RtrvPersonInfoInDto;
import us.tx.state.dfps.service.casepackage.dto.RtrvPersonInfoOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceFetchPersonDao Feb 7, 2018- 5:45:06 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface CaseMaintenanceFetchPersonDao {
	public void fetchPersonDtl(RtrvPersonInfoInDto rtrvPersonInfoInDto, RtrvPersonInfoOutDto rtrvPersonInfoOutDto);

}
