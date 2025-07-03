package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.CaseMergeToInDto;
import us.tx.state.dfps.service.admin.dto.CaseMergeToOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Clsc68dDao
 * Aug 6, 2017- 7:15:33 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface CaseMergeToDao {

	/**
	 * 
	 * Method Name: getCaseMergeDtls Method Description: This method will
	 * retrieve case merge details.
	 * 
	 * @param pInputDataRec
	 * @return List<CaseMergeToOutDto> @
	 */
	public List<CaseMergeToOutDto> getCaseMergeDtls(CaseMergeToInDto pInputDataRec);
}
