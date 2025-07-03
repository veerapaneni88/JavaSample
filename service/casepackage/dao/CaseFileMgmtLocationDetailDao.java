package us.tx.state.dfps.service.casepackage.dao;

import us.tx.state.dfps.service.casepackage.dto.CaseFileMgmtLocatioOutDto;
import us.tx.state.dfps.service.casepackage.dto.CaseFileMgmtLocationInDto;

public interface CaseFileMgmtLocationDetailDao {
	/**
	 * 
	 * Method Name: caseFileMgmtLocationDetail Method Description: DAM CCMNA5D
	 * 
	 * @param caseFileMgmtLocationInDto
	 * @return @
	 */
	public CaseFileMgmtLocatioOutDto caseFileMgmtLocationDetail(CaseFileMgmtLocationInDto caseFileMgmtLocationInDto);

}
