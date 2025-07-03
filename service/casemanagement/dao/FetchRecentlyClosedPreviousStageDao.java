package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.PreviousStageInputDto;
import us.tx.state.dfps.service.casepackage.dto.PreviousStageOutputDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 22, 2018- 9:26:37 PM © 2017 Texas Department of
 * Family and Protective Services
 */
// Ccmnb5dDao
public interface FetchRecentlyClosedPreviousStageDao {
	public void fetchRecentlyClosedPreviousStage(PreviousStageInputDto previousStageInputDto,
			PreviousStageOutputDto previousStageOutputDto);

}
