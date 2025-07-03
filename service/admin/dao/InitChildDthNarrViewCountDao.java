package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.InitChildDthNarrViewCountInDto;
import us.tx.state.dfps.service.admin.dto.InitChildDthNarrViewCountOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Cdyn25d Aug 6, 2017- 8:41:38 AM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface InitChildDthNarrViewCountDao {

	/**
	 * 
	 * Method Name: getEventRelatedRecords Method Description: This method will
	 * give the count.
	 * 
	 * @param initChildDthNarrViewCountInDto
	 * @return List<InitChildDthNarrViewCountOutDto>
	 */
	public List<InitChildDthNarrViewCountOutDto> getEventRelatedRecords(
			InitChildDthNarrViewCountInDto initChildDthNarrViewCountInDto);
}
