package us.tx.state.dfps.service.geomap.service;

import us.tx.state.dfps.service.common.request.GeoMapReq;
import us.tx.state.dfps.service.common.response.AddressDtlRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Thsi class
 * is used to get the school details that are with in the given boundaries Aug
 * 11, 2017- 4:02:50 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface GeoMapService {

	/**
	 * Method Description: This method is used to get the details of all the
	 * schools based on coordinates .
	 * 
	 * @param geoMapReq
	 * @return @
	 */
	public AddressDtlRes getSchoolAddressDtls(GeoMapReq geoMapReq);

}
