package us.tx.state.dfps.service.admin.service;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.FacilityDetailDto;
import us.tx.state.dfps.service.common.request.FacilityDetailReq;
import us.tx.state.dfps.service.common.request.FacilityDetailSaveReq;
import us.tx.state.dfps.service.common.request.HmStatusReq;
import us.tx.state.dfps.service.common.response.FacilityDetailRes;
import us.tx.state.dfps.service.common.response.HmStatusRes;
import us.tx.state.dfps.service.hmm.dto.HeightenedMonitoringDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 22, 2017- 7:24:13 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface FacilityDetailService {

	/**
	 * Method Name: getFacilityDetails Method Description: This method is used
	 * to getFacilityDetails
	 * 
	 * @param pInputMsg
	 * @ @return List<FacilityDetailDto>
	 */
	public List<FacilityDetailDto> getFacilityDetails(FacilityDetailReq pInputMsg);

	/**
	 * Method Name: saveFacilityDetails Method Description: This method is used
	 * to saveFacilityDetails
	 * 
	 * @param facilityDetailSaveReq
	 * @ @return FacilityDetailRes
	 */
	public FacilityDetailRes saveFacilityDetails(FacilityDetailSaveReq facilityDetailSaveReq);

	/**
	 * Method Name: getOpenHTFacilityServiceTypeCount Method Description: This method is used
	 * to get the count of open Human Trafficking Service type
	 *
	 * @param idResource
	 * @ @return FacilityDetailRes
	 */
	public Integer getOpenHTFacilityServiceTypeCount(Long idResource);

}
