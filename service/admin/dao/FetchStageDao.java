package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.FetchStagediDto;
import us.tx.state.dfps.service.admin.dto.FetchStagedoDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Dao for
 * FetchStageDao Aug 7, 2017- 6:56:27 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface FetchStageDao {

	/**
	 * Method Name: getStageDetails Method Description: Method to fetch the
	 * stage details for a given stage Id.
	 *
	 * @param fetchStagediDto
	 *            the fetch stagedi dto
	 * @return List<FetchStagedoDto>
	 */
	public List<FetchStagedoDto> getStageDetails(FetchStagediDto fetchStagediDto);

}
