package us.tx.state.dfps.service.casepackage.dao;

import us.tx.state.dfps.service.casepackage.dto.CaseFileMgmtUnitDetailInDto;
import us.tx.state.dfps.service.casepackage.dto.CaseFileMgmtUnitDetailOutDto;

public interface CaseFileMgmtUnitDetailFetchDao {
	/**
	 * 
	 * Method Name: caseFileMgmtUnitDetailFetch Method Description: Ccmnc0d
	 * 
	 * @param caseFileMgmtUnitDetailInDto
	 * @return @
	 */
	public CaseFileMgmtUnitDetailOutDto caseFileMgmtUnitDetailFetch(
			CaseFileMgmtUnitDetailInDto caseFileMgmtUnitDetailInDto);

}
