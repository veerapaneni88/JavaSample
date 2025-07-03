package us.tx.state.dfps.service.geomap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.GeoMapReq;
import us.tx.state.dfps.service.common.response.AddressDtlRes;
import us.tx.state.dfps.service.geomap.service.GeoMapService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * is used to get the details for GeoMap Aug 08, 2017- 4:00:24 PM © 2017 Texas
 * Department of Family and Protective Services
 */

@RestController
@RequestMapping(value = "/map")
public class GeoMapController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	GeoMapService geoMapService;

	/**
	 * This method is used to get the address details including lat and long for
	 * all the schools in the system based on the boundary values
	 * 
	 * @return
	 */
	@RequestMapping(value = "/schoolAddress", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  AddressDtlRes getSchoolAddressDtls(@RequestBody GeoMapReq geoMapReq) {
		return geoMapService.getSchoolAddressDtls(geoMapReq);
	}

}
