package us.tx.state.dfps.service.kin.service;

import us.tx.state.dfps.service.kin.dto.KinPlacementInfoValueDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This is the
 * Bean class used for Placement Information. The Person is checked if he is the
 * Primary Kin Caregiver, If yes, then the resourceId and the resourceName are
 * retrieved. Sep 6, 2017- 3:21:43 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface KinPlacementInfoService {

	/**
	 * Method Name: getResourceDetails Method Description: Gets the Resource
	 * Details based on the personId if the person is Primary Kinship Caregiver.
	 * 
	 * @param personId
	 * @return KinPlacementInfoValueDto @
	 */
	public KinPlacementInfoValueDto getResourceDetails(long personId);

	/**
	 * Method Name: getHomeStatus Method Description: Gets the status of the
	 * home based on the resourceId
	 * 
	 * @param resourceId
	 * @return KinPlacementInfoValueDto @
	 */
	public KinPlacementInfoValueDto getHomeStatus(long resourceId);
}
