package us.tx.state.dfps.service.placement.service;

import us.tx.state.dfps.service.common.request.PlcmtInfoRetrivalReq;
import us.tx.state.dfps.service.common.request.PlcmtInfoRsrcRetrivalReq;
import us.tx.state.dfps.service.common.response.CommonCountRes;
import us.tx.state.dfps.service.common.response.PlcmtInfoRetrivalResp;
import us.tx.state.dfps.service.common.response.PlcmtInfoRsrcRetrivalResp;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PlacementInfoRetrivalService Jan 04, 2018- 5:05:53 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface PlacementInfoRetrivalService {

	/**
	 * 
	 * Method Name: getPlacementInformation Method Description:This method is
	 * used to get the placement Information .Its conversion of tuxedo CSUB25S
	 * 
	 * @param plcmtInfoRetrivalReq
	 * @return @
	 */
	public PlcmtInfoRetrivalResp getPlacementInformation(PlcmtInfoRetrivalReq plcmtInfoRetrivalReq);

	/**
	 * Method Name: getPlacementRsrcInfo Method Description:This method is used
	 * to get the placement Information .Its conversion of tuxedo CSUB31S
	 * 
	 * @param plcmtInfoRsrcRetrivalReq
	 * @return @
	 */
	public PlcmtInfoRsrcRetrivalResp getPlacementRsrcInfo(PlcmtInfoRsrcRetrivalReq plcmtInfoRsrcRetrivalReq);;

	/**
	 * 
	 * Method Name:Method to get placement details with eventid
	 *
	 * @param idPlacementEvent
	 * @return PlacementDto @
	 */
	public PlacementDto getPlacementDetails(Long idPlacementEvent);

	/**
	 * Method Name: callOtherChildInPlacementCnt
	 *
	 * Method Description:this method is used to get the other child in the
	 * given resource by childId and resouceId
	 * 
	 * @param idRsrcFacil
	 * @param idPlcmtChild
	 * @return
	 */
	public CommonCountRes callOtherChildInPlacementCnt(long idRsrcFacil, long idPlcmtChild);

}
