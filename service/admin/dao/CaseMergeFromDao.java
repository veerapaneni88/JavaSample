package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.CaseMergeFromInDto;
import us.tx.state.dfps.service.admin.dto.CaseMergeFromOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Clsc67dDao
 * Aug 6, 2017- 7:38:22 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface CaseMergeFromDao {

	/**
	 * 
	 * Method Name: getCaseMergeDtls Method Description: This method will
	 * retrieve case merge details.
	 * 
	 * @param pInputDataRec
	 * @return List<CaseMergeFromOutDto> @
	 */
	public List<CaseMergeFromOutDto> getCaseMergeDtls(CaseMergeFromInDto pInputDataRec);
}
