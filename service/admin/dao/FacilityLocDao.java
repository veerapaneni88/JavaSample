package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.FacilityLocDto;
import us.tx.state.dfps.service.admin.dto.FacilityLocInDto;
import us.tx.state.dfps.service.admin.dto.FacilityLocOutDto;
import us.tx.state.dfps.service.common.request.FacilityDetailSaveReq;
import us.tx.state.dfps.service.common.response.FacilityDetailRes;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Retrieves
 * up to 9 levels of care for each facility in the output message. Aug 9, 2017-
 * 7:25:30 PM © 2017 Texas Department of Family and Protective Services
 */
public interface FacilityLocDao {

	public List<FacilityLocOutDto> getFacilityCare(FacilityLocInDto pInputDataRec);

	/**
	 * 
	 * Method Name: getFclityLocByResourceId Method Description: (DAM CSEC25D
	 * )will select a row from FACILITY_LOC that falls the given placement start
	 * dates when passed an ID_RESOURCE
	 * 
	 * @param resourceId
	 * @param plcmtStartDt
	 * @return @
	 */
	public List<FacilityLocDto> getFclityLocByResourceId(FacilityLocInDto facilityLocInDto);

	/**
	 * 
	 * Method Name: getFclityLocByResourceId Method Description: (DAM CSECC2D
	 * )will select a row from FACILITY_LOC that falls the given placement start
	 * dates when passed an ID_RESOURCE
	 * 
	 * @param resourceId
	 * @param plcmtStartDt
	 * @return @
	 */
	public List<FacilityLocDto> getFclityLocByRsrcId(FacilityLocInDto facilityLocInDto);

	/**
	 * Method Name: updateFacilityLoc Method Description: This method is used to
	 * updateFacilityLoc
	 * 
	 * @param facilityDetailSaveReq
	 * @ @return FacilityDetailRes
	 */
	public FacilityDetailRes updateFacilityLoc(FacilityDetailSaveReq facilityDetailSaveReq,
			FacilityDetailRes facilityDetailRes);
}
