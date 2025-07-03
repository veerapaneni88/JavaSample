package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.RetrieveAllVictimsInputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveAllVictimsOutputDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 22, 2018- 9:33:51 PM © 2017 Texas Department of
 * Family and Protective Services
 */

// Clsc90dDao
public interface FetchAllVictimsForCaseDao {
	public void retrieveAllVictim(RetrieveAllVictimsInputDto retrieveAllVictimsInputDto,
			RetrieveAllVictimsOutputDto retrieveAllVictimsOutputDto);

}
