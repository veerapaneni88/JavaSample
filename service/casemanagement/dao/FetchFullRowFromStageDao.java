package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.RetrieveFullRowStageInputDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveFullRowStageOutputDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 22, 2018- 9:33:40 PM © 2017 Texas Department of
 * Family and Protective Services
 */

// Cint40dDao
public interface FetchFullRowFromStageDao {
	public RetrieveFullRowStageOutputDto retrieveStage(RetrieveFullRowStageInputDto retrieveFullRowStageInputDto);

}
