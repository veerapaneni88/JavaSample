package us.tx.state.dfps.service.resourcehistory.dao;

import us.tx.state.dfps.common.domain.ResourceHistory;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.resource.dto.ResourceHistoryDto;
import us.tx.state.dfps.service.resource.dto.ResourceHistoryInDto;
import java.util.List;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * has methods for Resource hsitory Jan 4, 2018- 2:00:53 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ResourceHistoryDao {

	/**
	 * 
	 * Method Name: getRsrcHistoryByIdAndDate Method Description:This will
	 * select a full row of the first record for the ID RESOURCE on the RESOURCE
	 * HISTORY table(where DT RSHS EFFECTIVE is <= PlacementStartDate and DT
	 * RSHS END is greater than PlacementStartDate). This date will be important
	 * in retrieving the correct row for historical placements.
	 * 
	 * @param resourceHistoryInDto
	 * @return
	 */
	public ResourceHistoryDto getRsrcHistory(ResourceHistoryInDto resourceHistoryInDto);

	/**
	 * Method Name: getRsrcHistoryByIdAndDate Method Description:DAM (CRES54D )
	 * 
	 * @param resourceHistoryInDto
	 * @return
	 */
	public ResourceHistory getRsrcHistoryByIdAndDate(ResourceHistoryInDto resourceHistoryInDto);

	/**
	 *
	 * @param resourceDto
	 * @return
	 */
	public List<ResourceHistory> getRsrcHistoryByStageId(ResourceDto resourceDto);
}
