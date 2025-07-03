package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.SvcAuthDetailNameInDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthDetailNameOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO
 * Interface for fetching ServiceAuthentication details> Aug 4, 2017- 2:37:17 PM
 * © 2017 Texas Department of Family and Protective Services
 */
public interface SvcAuthDetailNameDao {

	/**
	 * 
	 * Method Name: getServiceAuthentication Method Description: This method
	 * will get data from SVC_AUTH_DETAIL,SERVICE_AUTHORIZATION and NAME table.
	 * 
	 * @param pInputDataRec
	 * @return List<SvcAuthDetailNameOutDto> @
	 */
	public List<SvcAuthDetailNameOutDto> getServiceAuthentication(SvcAuthDetailNameInDto pInputDataRec);
}
