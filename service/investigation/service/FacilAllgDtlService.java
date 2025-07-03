package us.tx.state.dfps.service.investigation.service;

import us.tx.state.dfps.service.common.request.GetFacilAllegDetailReq;
import us.tx.state.dfps.service.common.request.UpdtFacilAllegDetailReq;
import us.tx.state.dfps.service.common.request.UpdtFacilAllegMultiDtlReq;
import us.tx.state.dfps.service.common.response.GetFacilAllegDetailRes;
import us.tx.state.dfps.service.common.response.UpdtFacilAllegDetailRes;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:
 * CINV07S,CINV08S,CINV10S Class Description: This class is to
 * retrieves,saves,updates,multi update Facility Allegation Detail page.
 */
public interface FacilAllgDtlService {

	/**
	 * 
	 * Method Description: Populates the Allegation List for Facility
	 * Allegations. legacy DAM name - CINV70S,CINV08D,CINVF8D,CSEC54D,CCMNB5D
	 * 
	 * @param idAllegation
	 * @return GetFacilAllegDetailRes @
	 */
	public GetFacilAllegDetailRes getallegtnlist(GetFacilAllegDetailReq getFacilAllegDetailReq);

	/**
	 * This method is to update facility allegation details legacy service name
	 * - CINV08S
	 * 
	 * @param updtFacilAllegDetailReq
	 * @return UpdtFacilAllegDetailRes @
	 */
	public UpdtFacilAllegDetailRes updateFacilAlleg(UpdtFacilAllegDetailReq updtFacilAllegDetailReq);

	/**
	 * Method Description:This service to update multiple allegations Legacy
	 * Service:CINV10S
	 * 
	 * @param updtFacilAllegMultiDtlReq
	 * @return void @
	 */
	public void updateFacilAllegMulti(UpdtFacilAllegMultiDtlReq updtFacilAllegMultiDtlReq);

}
