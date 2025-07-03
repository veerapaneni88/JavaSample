package us.tx.state.dfps.service.geomap.dao;

import us.tx.state.dfps.service.common.request.GeoMapReq;
import us.tx.state.dfps.service.common.response.AddressDtlRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * interface is used to get the school address based on given boundaries Aug 07,
 * 2017- 4:30:57 PM © 2017 Texas Department of Family and Protective Services
 */
public interface GeoMapDao {

	/**
	 * This method is used to get the address details of all the schools
	 * 
	 * @return AddressDtlRes with list of address details
	 * @param req
	 *            this contains the boundaries to fetch the schools
	 * @return
	 */
	public AddressDtlRes getSchoolAddressDtls(GeoMapReq req);

}
