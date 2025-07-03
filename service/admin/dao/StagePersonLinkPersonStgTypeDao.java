package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.StagePersonLinkPersonStgTypeInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkPersonStgTypeOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Clsc18dDao
 * Aug 6, 2017- 7:04:18 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface StagePersonLinkPersonStgTypeDao {

	/**
	 * 
	 * Method Name: getPersonDtls Method Description: This method will get data
	 * from Stage Person Link and Person tables.
	 * 
	 * @param pInputDataRec
	 * @return List<StagePersonLinkPersonStgTypeOutDto> @
	 */
	public List<StagePersonLinkPersonStgTypeOutDto> getPersonDtls(StagePersonLinkPersonStgTypeInDto pInputDataRec);
}
