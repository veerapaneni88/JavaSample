package us.tx.state.dfps.service.placement.controller;

import java.util.Locale;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PlacementReq;
import us.tx.state.dfps.service.common.request.PlcmtInfoRetrivalReq;
import us.tx.state.dfps.service.common.request.PlcmtInfoRsrcRetrivalReq;
import us.tx.state.dfps.service.common.response.CommonCountRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.PlcmtInfoRetrivalResp;
import us.tx.state.dfps.service.common.response.PlcmtInfoRsrcRetrivalResp;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.placement.service.PlacementInfoRetrivalService;
import us.tx.state.dfps.service.placement.service.PlacementService;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Class with
 * CSUB25S and CSUB31S tuxedo implementation for Placement Page Jan 17, 2018-
 * 2:57:12 PM © 2017 Texas Department of Family and Protective Services
 */
@Api(tags = { "placements" })
@RestController
@RequestMapping("/placementinfo")
public class PlacementInfoRetrivalController {

	@Autowired
	private PlacementInfoRetrivalService placementInfoService;

	@Autowired
	private PlacementService placementService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(PlacementInfoRetrivalController.class);

	/**
	 * 
	 * Method Name: getPlacementInformation Method Description:service to
	 * retrieve the display information Service Name : CSUB25S
	 * 
	 * @param plcmtInfoRetrivalReq
	 * @return
	 */
	@ApiOperation(value = "Get placement details", tags = { "placements" })
	@RequestMapping(value = "/details", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PlcmtInfoRetrivalResp getPlacementInformation(
			@RequestBody PlcmtInfoRetrivalReq plcmtInfoRetrivalReq) {

		if (TypeConvUtil.isNullOrEmpty(plcmtInfoRetrivalReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return placementInfoService.getPlacementInformation(plcmtInfoRetrivalReq);
	}

	/**
	 * 
	 * Method Name: getPlacementInformation Method Description:service to
	 * retrieve the placement Resource display information Service Name :
	 * CSUB31S
	 * 
	 * @param plcmtInfoRetrivalReq
	 * @return
	 */
	@RequestMapping(value = "/rsrcdetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PlcmtInfoRsrcRetrivalResp getPlacementRsrcInfo(
			@RequestBody PlcmtInfoRsrcRetrivalReq plcmtInfoRsrcRetrivalReq) {
		log.debug("Entering method getPlacementRsrcInfo in PlacementController");

		if (TypeConvUtil.isNullOrEmpty(plcmtInfoRsrcRetrivalReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(plcmtInfoRsrcRetrivalReq.getIdResource())) {
			throw new InvalidRequestException(
					messageSource.getMessage("placement.resourceId.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(plcmtInfoRsrcRetrivalReq.getIdPlcmtChild())) {
			throw new InvalidRequestException(
					messageSource.getMessage("placement.placementchildId.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(plcmtInfoRsrcRetrivalReq.getCdRsrcFacilType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("placement.resourcefacility.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(plcmtInfoRsrcRetrivalReq.getAddrPlcmtCnty())) {
			throw new InvalidRequestException(
					messageSource.getMessage("placement.addrplcmtcnty.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(plcmtInfoRsrcRetrivalReq.getIndPlcmetEmerg())) {
			throw new InvalidRequestException(
					messageSource.getMessage("placement.indPlcmetEmerg.mandatory", null, Locale.US));
		}

		return placementInfoService.getPlacementRsrcInfo(plcmtInfoRsrcRetrivalReq);
	}

	/**
	 * 
	 * Method Name:Method to get placement details with eventid
	 *
	 * @param commonHelperReq
	 * @return PlacementDto @
	 */
	@ApiOperation(value = "Get placement details by eventId", tags = { "placements" })
	@RequestMapping(value = "/getPlacementbyId", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementDto getPlacementById(@RequestBody CommonHelperReq commonHelperReq) {

		PlacementDto placementDto = new PlacementDto();

		placementDto = placementInfoService.getPlacementDetails(commonHelperReq.getIdEvent());
		return placementDto;
	}

	/**
	 * Method Name: getPriorPlacementsById Method Description: This method
	 * returns prior placement list based on idPlacementEvent
	 * 
	 * @param idPriorPlacementEvent
	 * @return CommonHelperRes
	 */

	@RequestMapping(value = "/getPriorPlacementsById", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes getPriorPlacements(@RequestBody CommonHelperReq commonHelperReq) {

		CommonHelperRes commonHelperRes = new CommonHelperRes();

		commonHelperRes = placementService.getPriorPlacementsById(commonHelperReq.getIdEvent());
		return commonHelperRes;
	}

	/**
	 * Method Name: getIndChildSibling1 Method Description: This method returns
	 * prior Placement sibling .
	 * 
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 */

	@RequestMapping(value = "/getIndChildSibling1", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes getIndChildSibling1(@RequestBody CommonHelperReq commonHelperReq) {

		CommonHelperRes commonHelperRes = new CommonHelperRes();

		commonHelperRes = placementService.getIndChildSibling1(commonHelperReq.getIdPerson());
		return commonHelperRes;
	}

	/**
	 * Method Name: getEligibility Method Description: This method returns prior
	 * eligibility for Pca .
	 * 
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 */

	@RequestMapping(value = "/getEligibility", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes getEligiblity(@RequestBody CommonHelperReq commonHelperReq) {

		CommonHelperRes commonHelperRes = new CommonHelperRes();

		commonHelperRes = placementService.getEligibilityEvent(commonHelperReq.getIdPerson());
		return commonHelperRes;
	}

	/**
	 * Method Name: getOtherChildCount Method Description: This method returns
	 * child count.
	 * 
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/otherChildCount", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonCountRes getOtherChildCount(@RequestBody PlacementReq placementReq) {
		if (TypeConvUtil.isNullOrEmpty(placementReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("placement.placementchildId.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(placementReq.getIdResource())) {
			throw new InvalidRequestException(
					messageSource.getMessage("placement.resourceId.mandatory", null, Locale.US));
		}

		return placementInfoService.callOtherChildInPlacementCnt(placementReq.getIdResource(),
				placementReq.getIdPerson());
	}

}
