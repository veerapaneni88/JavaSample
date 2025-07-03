package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.RetrieveCapsCaseInputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveCapsCaseOutputDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 22, 2018- 11:14:10 PM © 2017 Texas Department of
 * Family and Protective Services
 */
// Ccmnd9dDao
public interface FetchFullRowOfCapsCaseDao {
	public void retrieveCapsCase(RetrieveCapsCaseInputDto retrieveCapsCaseInputDto,
			RetrieveCapsCaseOutputDto retrieveCapsCaseOutputDto);

}
