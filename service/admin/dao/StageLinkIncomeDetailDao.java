package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.StageLinkIncomeDetailInDto;
import us.tx.state.dfps.service.admin.dto.StageLinkIncomeDetailOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Clsc84dDao
 * Aug 6, 2017- 5:29:55 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface StageLinkIncomeDetailDao {

	/**
	 * 
	 * Method Name: stageIncomingDtls Method Description: Get data from Stage
	 * Link and Incoming Detail tables.
	 * 
	 * @param pInputDataRec
	 * @return List<StageLinkIncomeDetailOutDto> @
	 */
	public List<StageLinkIncomeDetailOutDto> stageIncomingDtls(StageLinkIncomeDetailInDto pInputDataRec);
}
