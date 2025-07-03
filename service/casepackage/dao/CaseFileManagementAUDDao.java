package us.tx.state.dfps.service.casepackage.dao;

import us.tx.state.dfps.service.casepackage.dto.CaseFileManagementInDto;
import us.tx.state.dfps.service.casepackage.dto.CaseFileManagementOutDto;
import us.tx.state.dfps.service.common.response.CommonHelperRes;

public interface CaseFileManagementAUDDao {
	/**
	 * 
	 * Method Name: caseFileManagementAUD Method Description: DAM caud76dAUDdam
	 * 
	 * @param caseFileManagementInDto
	 * @param caseFileManagementOutDto
	 */
	public CommonHelperRes caseFileManagementAUD(CaseFileManagementInDto caseFileManagementInDto,
			CaseFileManagementOutDto caseFileManagementOutDto);

}
