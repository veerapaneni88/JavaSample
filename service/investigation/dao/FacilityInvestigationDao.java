package us.tx.state.dfps.service.investigation.dao;

import us.tx.state.dfps.xmlstructs.inputstructs.FacilityInvestigationDto;
import us.tx.state.dfps.xmlstructs.outputstructs.FacilInvstInfoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:FacilityInvestigationDao Sep 9, 2017- 10:40:22 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface FacilityInvestigationDao {

	/**
	 * Method Name: getFacilityInvestigationDetail Method Description: fetches
	 * Facility Investigation Details.
	 * 
	 * @param facilityInvestigationDto
	 * @return FacilInvstInfoDto
	 */
	public FacilInvstInfoDto getFacilityInvestigationDetail(FacilityInvestigationDto facilityInvestigationDto);

}
