/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:LevelOfCareRtrvService Aug 18, 2017- 12:12:10 PM © 2017 Texas
 * Department of Family and Protective Services
 */
package us.tx.state.dfps.service.placement.service;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.common.request.LevelOfCareRtrvReq;
import us.tx.state.dfps.service.common.request.PlacementReq;
import us.tx.state.dfps.service.placement.dto.LevelOfCareRtrvoDto;
import us.tx.state.dfps.service.workload.dto.EventDto;

public interface LevelOfCareRtrvService {

	/**
	 * 
	 * Method Name: callLevelOfCareRtrvService Method Description: This service
	 * will retrieve an event row and Level of Care row
	 * 
	 * @param pInputMsg
	 * @return List<LevelOfCareRtrvoDto> @
	 */
	public List<LevelOfCareRtrvoDto> callLevelOfCareRtrvService(LevelOfCareRtrvReq pInputMsg);

	/**
	 * 
	 * Method Name: levelOfCareSaveService Method Description: This service will
	 * save an event row and Level of Care row
	 * 
	 * @param pInputMsg
	 * @return List<LevelOfCareRtrvoDto> @
	 */
	public LevelOfCareRtrvoDto levelOfCareSaveService(LevelOfCareRtrvReq pInputMsg);
	
	/**
	 * 
	 * Method Name: getPersonLocAddUpdate 
	 * Method Description: This service will
	 * update and add service level aloc and bloc for TFC placement
	 * 
	 * @param placementReq
	 * @return LevelOfCareRtrvoDto
	 * 
	 */
	public LevelOfCareRtrvoDto getPersonLocAddUpdate(PlacementReq placementReq) ;

	/**
	 *
	 * Method Name: getPersonLocAddUpdateforQRTP
	 * Method Description: This service will
	 * update and add service level aloc and bloc for QRTP placement
	 *
	 * @param placementReq
	 * @return LevelOfCareRtrvoDto
	 *
	 */
	public LevelOfCareRtrvoDto getPersonLocAddUpdateforQRTP(PlacementReq placementReq) ;

	/**
	 *
	 * Method Name: getPlacementStartDt
	 * Method Description: This service will
	 * get placement start date of child for qrtp
	 *
	 * @param caseId
	 * @param stageId
	 * @return Date
	 *
	 */
	public Date getPlacementStartDt(long caseId, Long stageId) ;
}
