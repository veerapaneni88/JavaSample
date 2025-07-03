package us.tx.state.dfps.service.casepackage.dao;

import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingCaseDetailInDto;
import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingCaseDetailOutDto;

public interface SpecialHandlingCaseDetailFetchDao {
	/**
	 * 
	 * Method Name: specialHandlingCaseDetailFetch Method Description: Ccmnb1d
	 * 
	 * @param specialHandlingCaseDetailInDto
	 * @return @
	 */
	public SpecialHandlingCaseDetailOutDto specialHandlingCaseDetailFetch(
			SpecialHandlingCaseDetailInDto specialHandlingCaseDetailInDto);

}
