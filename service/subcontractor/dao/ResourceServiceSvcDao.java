package us.tx.state.dfps.service.subcontractor.dao;

import java.util.List;

import us.tx.state.dfps.service.kin.dto.ResourceServiceDto;
import us.tx.state.dfps.service.subcontractor.dto.ResourceServiceSvcInDto;
import us.tx.state.dfps.service.subcontractor.dto.ResourceServiceSvcOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * 
 * Class Description:Interface for ResourceService
 * 
 * Aug 2, 2017- 8:35:34 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface ResourceServiceSvcDao {

	/**
	 * 
	 * Method Name: getResourceServiceDetails
	 * 
	 * Method Description: This method DAM Clss14d will get data from
	 * RESOURCE_SERVICE table.
	 * 
	 * @param pInputDataRec:
	 *            request containing resource id
	 * @return List<ResourceServiceSvcOutDto> - List of resource services as
	 *         response
	 */
	public List<ResourceServiceSvcOutDto> getResourceServiceDetails(ResourceServiceSvcInDto pInputDataRec);

	/**
	 * 
	 * Method Name: getResourceServiceById
	 * 
	 * Method Description:This returns all rows from the RESOURCE_SERVICE table
	 * for a specified resource ID.
	 * 
	 * DAM Name : CRES10D
	 * 
	 * @param pInputDataRec
	 *            - request containing resource id
	 * @return List<ResourceServiceDto> - response of resource services
	 */
	public List<ResourceServiceDto> getResourceServiceById(ResourceServiceSvcInDto pInputDataRec);
}
