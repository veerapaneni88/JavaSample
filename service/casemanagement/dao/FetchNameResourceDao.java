package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.RetrieveNmResourceInputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveNmResourceOutputDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 22, 2018- 9:34:05 PM © 2017 Texas Department of
 * Family and Protective Services
 */

// Ccmni0dDao
public interface FetchNameResourceDao {
	public void fetchNameResource(RetrieveNmResourceInputDto retrieveNmResourceInputDto,
			RetrieveNmResourceOutputDto retrieveNmResourceOutputDto);

}
