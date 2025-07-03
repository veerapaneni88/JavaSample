package us.tx.state.dfps.service.kinresourceservice.service;

import us.tx.state.dfps.service.kin.dto.KinHomeInfoDto;
import us.tx.state.dfps.service.kin.dto.ResourceServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:KinResourceServiceService May 14, 2018- 8:45:50 PM © 2018 Texas
 * Department of Family and Protective Services
 */
public interface KinResourceServiceService {

	/**
	 * Method Name: updateResourceService Method Description: updates the
	 * RESOURCE_SERVICE table.
	 * 
	 * @param resourceServiceDto
	 * @return Long
	 * 
	 */
	public Long updateResourceService(ResourceServiceDto resourceServiceDto);

	/**
	 * Method Name: getResourceService Method Description: Selects from
	 * RESOURCE_SERVICE table.
	 * 
	 * @param resourceServiceDto
	 * @return ResourceServiceDto
	 * 
	 */
	public ResourceServiceDto getResourceService(ResourceServiceDto resourceServiceDto);

	/**
	 * Method Name: getKinTrainCompleted Method Description: Selects from
	 * FA_INDIV_TRAINING , STAGE_PERSON_LINK tables.
	 * 
	 * @param kinHomeInfoDto
	 * @return String
	 * 
	 */
	public String getKinTrainCompleted(KinHomeInfoDto kinHomeInfoDto);

	/**
	 * Method Name: insertResourceService Method Description:insert the values
	 * in resource_service table
	 * 
	 * @param resourceServiceDto
	 * @return Long
	 * 
	 */
	public Long insertResourceService(ResourceServiceDto resourceServiceDto);

}
