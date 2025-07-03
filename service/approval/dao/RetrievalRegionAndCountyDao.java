package us.tx.state.dfps.service.approval.dao;

import us.tx.state.dfps.approval.dto.RetrievalRegionAndCountyReq;
import us.tx.state.dfps.approval.dto.RetrievalRegionAndCountyRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: June 19,
 * 2018- 11:04:32 PM © 2018 Texas Department of Family and Protective Services
 */
public interface RetrievalRegionAndCountyDao {

	/**
	 * Method Name: retrievalRegionAndCounty Method Description: Region
	 * retrieval from Region/County table. Dam Name: CSES82D Service Name:
	 * CCMN35S
	 * 
	 * @param RetrievalRegionAndCountyReq
	 * @return RetrievalRegionAndCountyRes
	 */
	public RetrievalRegionAndCountyRes retrievalRegionAndCounty(
			RetrievalRegionAndCountyReq retrievalRegionAndCountyReq);
}
