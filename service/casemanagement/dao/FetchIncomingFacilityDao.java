package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.RetreiveIncomingFacilityInputDto;
import us.tx.state.dfps.service.casepackage.dto.RetreiveIncomingFacilityOutputDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 22, 2018- 9:33:30 PM © 2017 Texas Department of
 * Family and Protective Services
 */
// Cint09dDao
public interface FetchIncomingFacilityDao {
	public void fetchIncomingFacility(RetreiveIncomingFacilityInputDto retreiveIncomingFacilityInputDto,
			RetreiveIncomingFacilityOutputDto retreiveIncomingFacilityOutputDto);

}
