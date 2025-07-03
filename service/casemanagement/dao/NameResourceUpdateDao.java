package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.CapsCaseResourceUpdateInDto;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseResourceUpdateOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:NameResourceUpdateDao Feb 7, 2018- 5:47:16 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface NameResourceUpdateDao {
	public void updateNameResource(CapsCaseResourceUpdateInDto capsCaseResourceUpdateInDto,
			CapsCaseResourceUpdateOutDto capsCaseResourceUpdateOutDto);

}
