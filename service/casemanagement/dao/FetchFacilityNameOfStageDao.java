package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.FacilityNameInputDto;
import us.tx.state.dfps.service.casepackage.dto.FacilityNameOutputDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 22, 2018- 9:26:42 PM © 2017 Texas Department of
 * Family and Protective Services
 */
// Ccmna3dDao
public interface FetchFacilityNameOfStageDao {
	public void retrieveFacilityName(FacilityNameInputDto facilityNameInputDto,
			FacilityNameOutputDto facilityNameOutputDto);

}
