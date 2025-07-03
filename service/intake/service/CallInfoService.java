package us.tx.state.dfps.service.intake.service;

import us.tx.state.dfps.service.common.request.CommonStageIdReq;
import us.tx.state.dfps.service.common.request.FacDetailUpdtReq;
import us.tx.state.dfps.service.common.request.PersListReq;
import us.tx.state.dfps.service.common.response.FacilRtrvRes;
import us.tx.state.dfps.service.common.response.PersListRes;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Mar 27, 2017 - 9:12:38 AM
 */
public interface CallInfoService {
	/**
	 * Method Description: to get person list details for call information
	 * screen
	 * 
	 * @param stageidIn
	 * @return PersListRes
	 */
	public PersListRes getPersonList(PersListReq persListReq);

	/**
	 * Method Description:to get facility details for call information screen
	 * 
	 * @param stageidIn
	 * @return FacilRtrvRes
	 */
	public FacilRtrvRes getFacilDetail(CommonStageIdReq stageidIn);

	/**
	 * Method Description:
	 * 
	 * @param facDetailUpdtReq
	 * @return
	 */
	public String updtFacilityDetail(FacDetailUpdtReq facDetailUpdtReq);

}
