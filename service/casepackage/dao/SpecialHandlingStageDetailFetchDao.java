package us.tx.state.dfps.service.casepackage.dao;

import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingStageDetailInDto;
import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingStageDetailOutDto;

public interface SpecialHandlingStageDetailFetchDao {
	/**
	 * 
	 * Method Name: specialHandlingStageDetailFetch Method Description: DAM
	 * Ccmne1d
	 * 
	 * @param specialHandlingStageDetailInDto
	 * @param specialHandlingStageDetailOutDto
	 * @
	 */
	public void specialHandlingStageDetailFetch(SpecialHandlingStageDetailInDto specialHandlingStageDetailInDto,
			SpecialHandlingStageDetailOutDto specialHandlingStageDetailOutDto);

}
