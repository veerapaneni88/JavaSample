/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This service
 * will retrieve an event row using the Event ID as the key. It will also
 * retrieve a row from the Level of Care table using the Event ID as the key.
 * This information will be used to populate the Level of Care window. Aug 18,
 * 2017- 2:12:11 PM © 2017 Texas Department of Family and Protective Services
 */
package us.tx.state.dfps.service.placement.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.LevelOfCareRtrvReq;
import us.tx.state.dfps.service.common.request.PlacementReq;
import us.tx.state.dfps.service.common.response.LevelOfCareRtrvRes;
import us.tx.state.dfps.service.common.response.PlacementRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.placement.dto.LevelOfCareRtrvoDto;
import us.tx.state.dfps.service.placement.service.LevelOfCareRtrvService;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/levelofcare")
public class LevelOfCareRtrvController {

	@Autowired
	LevelOfCareRtrvService objLevelOfCareRtrvService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(LevelOfCareRtrvController.class);
	
	public static final String EVENT_TYPE_LOC = "LOC";
	public static final String PLOC_TASKCODE = "3140";

	/**
	 * 
	 * Method Name: CSUB16S Method Description: This service will retrieve an
	 * event row using the Event ID as the key. It will also retrieve a row from
	 * the Level of Care table using the Event ID as the key. This information
	 * will be used to populate the Level of Care window.
	 * 
	 * @param objLevelOfCareRtrviDto
	 * @return LevelOfCareRtrvRes
	 */
	@RequestMapping(value = "/levelofcarertrv", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public LevelOfCareRtrvRes LevelOfCareRtrv(@RequestBody LevelOfCareRtrvReq objLevelOfCareRtrviDto) {
		log.debug("Entering method LevelOfCareRtrv in LevelOfCareRtrvController");
		if (TypeConvUtil.isNullOrEmpty(objLevelOfCareRtrviDto.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("levelOfCare.require.idEventId", null, Locale.US));
		}
		List<LevelOfCareRtrvoDto> objLevelOfCareRtrvoDto = objLevelOfCareRtrvService
				.callLevelOfCareRtrvService(objLevelOfCareRtrviDto);
		LevelOfCareRtrvRes levelOfCareRtrvRes = new LevelOfCareRtrvRes();
		levelOfCareRtrvRes.setLevelOfCareRtrvoDto(objLevelOfCareRtrvoDto);
		log.debug("Exiting method LevelOfCareRtrv in LevelOfCareRtrvController");
		return levelOfCareRtrvRes;
	}

	/**
	 * 
	 * Method Name: CSUB17S Method Description: This service will confirm that
	 * event processing is OK using Check Stage Event Status function and
	 * add/update an event row and a Level of Care row. If primary child is
	 * unknown, it will be determined.
	 * 
	 * @param objLevelOfCareRtrviDto
	 * @return LevelOfCareRtrvRes
	 */
	@RequestMapping(value = "/levelofcaresave", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public LevelOfCareRtrvRes LevelOfCareSave(@RequestBody LevelOfCareRtrvReq objLevelOfCareRtrviDto) {
		log.debug("Entering method LevelOfCareSave in LevelOfCareRtrvController");
		LevelOfCareRtrvoDto objLevelOfCareRtrvoDto = objLevelOfCareRtrvService
				.levelOfCareSaveService(objLevelOfCareRtrviDto);
		LevelOfCareRtrvRes levelOfCareRtrvRes = new LevelOfCareRtrvRes();
		levelOfCareRtrvRes.setLocRtrvoDto(objLevelOfCareRtrvoDto);
		if (!ObjectUtils.isEmpty(objLevelOfCareRtrvoDto.getErrorDto())) {
			levelOfCareRtrvRes.setErrorDto(objLevelOfCareRtrvoDto.getErrorDto());
		}

		log.debug("Exiting method LevelOfCareRtrv in LevelOfCareRtrvController");
		return levelOfCareRtrvRes;
	}
	
	@RequestMapping(value = "/getpersonlocaddupdate", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	@ResponseBody
	public LevelOfCareRtrvRes getPersonLocAddUpdate(@RequestBody PlacementReq placementReq)
			{
		log.debug("Entering method LevelOfCareSave in LevelOfCareRtrvController");
		LevelOfCareRtrvoDto objLevelOfCareRtrvoDto =  objLevelOfCareRtrvService
				.getPersonLocAddUpdate(placementReq);
		
		LevelOfCareRtrvRes levelOfCareRtrvRes = new LevelOfCareRtrvRes();
		levelOfCareRtrvRes.setLocRtrvoDto(objLevelOfCareRtrvoDto);
		
		log.debug("Exiting method LevelOfCareRtrv in LevelOfCareRtrvController");
		return levelOfCareRtrvRes;
	}

	@RequestMapping(value = "/getpersonlocaddupdateforqrtp", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	@ResponseBody
	public LevelOfCareRtrvRes getPersonLocAddUpdateForQrtp(@RequestBody PlacementReq placementReq)
	{
		log.debug("Entering method getPersonLocAddUpdateForQrtp in LevelOfCareRtrvController");
		LevelOfCareRtrvoDto objLevelOfCareRtrvoDto =  objLevelOfCareRtrvService
				.getPersonLocAddUpdateforQRTP(placementReq);

		LevelOfCareRtrvRes levelOfCareRtrvRes = new LevelOfCareRtrvRes();
		levelOfCareRtrvRes.setLocRtrvoDto(objLevelOfCareRtrvoDto);

		log.debug("Exiting method LevelOfCareRtrv in LevelOfCareRtrvController");
		return levelOfCareRtrvRes;
	}

	@GetMapping(value = "/getQrtpPlacementStartDt/{idCase}/{idStage}", headers = { "Accept=application/json" })
	@ResponseBody
	public PlacementRes getQrtpPlacementStartDt(@PathVariable(value = "idCase") Long idCase, @PathVariable(value = "idStage") Long idStage)
	{
		log.debug("Entering method getQrtpPlacementStartDt in LevelOfCareRtrvController");
		PlacementRes placementRes = new PlacementRes();
		PlacementDto dto = new PlacementDto();
		dto.setDtPlacementStartDate(objLevelOfCareRtrvService
				.getPlacementStartDt(idCase,idStage));
		placementRes.setPlacementDto(dto);

		log.debug("Exiting method getQrtpPlacementStartDt in LevelOfCareRtrvController");
		return placementRes;
	}
}
