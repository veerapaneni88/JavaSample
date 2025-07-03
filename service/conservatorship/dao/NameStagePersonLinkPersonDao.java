package us.tx.state.dfps.service.conservatorship.dao;

import java.util.List;

import us.tx.state.dfps.service.cvs.dto.NameStagePersonLinkPersonInDto;
import us.tx.state.dfps.service.cvs.dto.NameStagePersonLinkPersonOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for NameStagePersonLinkPersonDao Aug 2, 2017- 8:04:44 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface NameStagePersonLinkPersonDao {

	/**
	 * 
	 * Method Name: getStagePersonLinkDetails Method Description: This method
	 * will get data from NAME, STAGE_PERSON_LINK and PERSON P tables.
	 * 
	 * @param nameStagePersonLinkPersonInDto
	 * @return List<NameStagePersonLinkPersonOutDto> @
	 */
	public List<NameStagePersonLinkPersonOutDto> getStagePersonLinkDetails(
			NameStagePersonLinkPersonInDto nameStagePersonLinkPersonInDto);
}
