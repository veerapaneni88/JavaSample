package us.tx.state.dfps.service.ppm.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.PPMInfoReq;
import us.tx.state.dfps.service.common.response.PPMInfoRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.PptDetailsOutDto;
import us.tx.state.dfps.service.ppm.dto.PPMInfoDto;
import us.tx.state.dfps.service.ppm.service.PPMInfoService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This is the
 * Service Controller PPM Information Page.> Sep 22, 2018- 7:37:29 PM © 2017
 * Texas Department of Family and Protective Services
 */

@RestController
@RequestMapping("/ppminfo")
public class PPMInfoController {

	@Autowired
	PPMInfoService ppmInfoService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(PPMInfoController.class);

	/**
	 * method name: getParticipants description: This method fetches all
	 * participants from DB
	 * 
	 * @param ppmInfoReq
	 * @return ppmInfoRes
	 */
	@RequestMapping(value = "/getParticipants", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PPMInfoRes getParticipants(@RequestBody PPMInfoReq ppmInfoReq) {
		log.debug("Entering method getParticipants in PPMInfoController");
		Long idEvent = ppmInfoReq.getIdEvent();
		if (TypeConvUtil.isNullOrEmpty(idEvent)) {
			throw new InvalidRequestException(
					messageSource.getMessage("PPMInfoReq.IdEvent.mandatory", null, Locale.US));
		}
		PPMInfoRes ppmInfoRes = new PPMInfoRes();
		PPMInfoDto ppmInfo = ppmInfoService.getParticipants(idEvent);
		ppmInfoRes.setPpmInfoDto(ppmInfo);
		log.debug("Exiting method getParticipants in PPMInfoController");
		return ppmInfoRes;
	}

	/**
	 * method name: getPPMInfo description: This method fetches all data for PPM
	 * Information from DB
	 * 
	 * @param ppmInfoReq
	 * @return ppmInfoRes
	 */
	@RequestMapping(value = "/getPPMInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PPMInfoRes getPPMInfo(@RequestBody PPMInfoReq ppmInfoReq) {
		log.debug("Entering method getPPMInfo in PPMInfoController");
		Long idEvent = ppmInfoReq.getIdEvent();
		if (TypeConvUtil.isNullOrEmpty(idEvent)) {
			throw new InvalidRequestException(
					messageSource.getMessage("PPMInfoReq.IdEvent.mandatory", null, Locale.US));
		}
		PPMInfoRes ppmInfoRes = new PPMInfoRes();
		PPMInfoDto ppmInfo = ppmInfoService.getPPMInfo(idEvent);
		ppmInfoRes.setPpmInfoDto(ppmInfo);
		log.debug("Exiting method getPPMInfo in PPMInfoController");
		return ppmInfoRes;
	}

	/**
	 * 
	 * Method Name: persistPPM Method Description: This method saves PPM
	 * Information
	 * 
	 * @param ppmInfoReq
	 * @return ppmInfoRes
	 */
	@RequestMapping(value = "/persistPPM", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PPMInfoRes persistPPM(@RequestBody PPMInfoReq ppmInfoReq) {
		log.debug("Entering method persistPPM in PPMInfoController");
		PPMInfoDto ppmInfo = ppmInfoReq.getPpmInfoDto();
		if (TypeConvUtil.isNullOrEmpty(ppmInfo)) {
			throw new InvalidRequestException(
					messageSource.getMessage("PPMInfoReq.ppmInfo.mandatory", null, Locale.US));
		}
		PPMInfoRes ppmInfoRes = new PPMInfoRes();
		PptDetailsOutDto pptDetailsOutDto = ppmInfoService.savePPM(ppmInfo);
		ppmInfo.setPptDetailsOut(pptDetailsOutDto);
		ppmInfoRes.setPpmInfoDto(ppmInfo);
		log.debug("Exiting method persistPPM in PPMInfoController");
		return ppmInfoRes;
	}

	/**
	 * 
	 * Method Name: deleteParticipant Method Description: This method deletes
	 * PPT Participant
	 * 
	 * @param ppmInfoReq
	 * @return ppmInfoRes
	 */
	@RequestMapping(value = "/delParticipant", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PPMInfoRes deleteParticipant(@RequestBody PPMInfoReq ppmInfoReq) {
		log.debug("Entering method deleteParticipant in PPMInfoController");
		PPMInfoDto ppmInfo = ppmInfoReq.getPpmInfoDto();
		if (TypeConvUtil.isNullOrEmpty(ppmInfo)) {
			throw new InvalidRequestException(
					messageSource.getMessage("PPMInfoReq.ppmInfo.mandatory", null, Locale.US));
		}
		PPMInfoRes ppmInfoRes = new PPMInfoRes();
		Boolean deleted = ppmInfoService.deletePPTParticipant(ppmInfo);
		if (deleted) {
			ppmInfoRes.setPpmInfoDto(ppmInfo);
		}
		log.debug("Exiting method getPPMInfo in PPMInfoController");
		return ppmInfoRes;
	}

	/**
	 * Method Name: getPPTParticipantDetail Method Description: Rest Controller
	 * method to fetch the PPT Participant Details.
	 *
	 * @param ppmInfoReq
	 * @return PPMInfoRes
	 */
	@RequestMapping(value = "/getPPTParticipantDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PPMInfoRes getPPTParticipantDetail(@RequestBody PPMInfoReq ppmInfoReq) {
		log.info("Entering method getPPTParticipantDetail in PPMInfoController");
		if (TypeConvUtil.isNullOrEmpty(ppmInfoReq.getIdPPTParticipant())) {
			throw new InvalidRequestException(
					messageSource.getMessage("PPMInfoReq.idPPTParticipant.mandatory", null, Locale.US));
		}
		PPMInfoRes response = new PPMInfoRes();
		PPMInfoDto ppmInfo = new PPMInfoDto();
		ppmInfo.setPptParticipant(ppmInfoService.getPPTParticipant(ppmInfoReq.getIdPPTParticipant()));
		response.setPpmInfoDto(ppmInfo);
		log.info("Exiting method getPPTParticipantDetail in PPMInfoController");
		return response;
	}

	/**
	 * Method Name: insertOrUpdatePPTInfo Method Description: This Method
	 * Performs AUD operations on PPT table based on the IdPptEvent
	 * 
	 * DAM Name: CAUD09D
	 * 
	 * @param ppmInfoReq
	 * @return PPMInfoRes
	 */
	@RequestMapping(value = "/savePPMInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PPMInfoRes savePPMInfo(@RequestBody PPMInfoReq ppmInfoReq) {
		log.debug("Entering method persistPPM in PPMInfoController");
		if (TypeConvUtil.isNullOrEmpty(ppmInfoReq)) {
			throw new InvalidRequestException(
					messageSource.getMessage("PPMInfoReq.ppmInfo.mandatory", null, Locale.US));
		}
		PPMInfoRes ppmIdEventRes = ppmInfoService.savePPMInfo(ppmInfoReq);
		if (!ObjectUtils.isEmpty(ppmIdEventRes) && !ObjectUtils.isEmpty(ppmIdEventRes.getIdEvent())) {
			PPMInfoDto ppmInfoDto = !ObjectUtils.isEmpty(ppmIdEventRes.getPpmInfoDto()) ? ppmIdEventRes.getPpmInfoDto()
					: new PPMInfoDto();
			PptDetailsOutDto pptOutDto = !ObjectUtils.isEmpty(ppmInfoDto.getPptDetailsOut())
					? ppmInfoDto.getPptDetailsOut() : new PptDetailsOutDto();
			pptOutDto.setIdPptEvent(ppmIdEventRes.getIdEvent());
			ppmInfoDto.setPptDetailsOut(pptOutDto);
			ppmIdEventRes.setPpmInfoDto(ppmInfoDto);
		}
		log.debug("Exiting method persistPPM in PPMInfoController");
		return ppmIdEventRes;
	}

	/**
	 * Method name: fetchppmInfo Description: This method fetches all data for
	 * PPM Information from DB
	 * 
	 * @param ppmInfoReq
	 * @return ppmInfoRes
	 */
	@RequestMapping(value = "/fetchppmInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PPMInfoRes fetchppmInfo(@RequestBody PPMInfoReq ppmInfoReq) {
		log.debug("Entering method fetchppmInfo in PPMInfoController");
		Long idEvent = ppmInfoReq.getIdEvent();
		if (TypeConvUtil.isNullOrEmpty(idEvent)) {
			throw new InvalidRequestException(
					messageSource.getMessage("PPMInfoReq.IdEvent.mandatory", null, Locale.US));
		}
		PPMInfoRes ppmInfoRes = new PPMInfoRes();
		PPMInfoDto ppmInfo = ppmInfoService.fetchppmInfo(idEvent);
		ppmInfoRes.setPpmInfoDto(ppmInfo);
		log.debug("Exiting method fetchppmInfo in PPMInfoController");
		return ppmInfoRes;
	}

	/**
	 * Method Name: pptParticipantAUD Method Description: Rest Controller Method
	 * to handle the AUD operaiton on PPT Participant
	 * 
	 * @param ppmInfoReq
	 * @return
	 */
	@RequestMapping(value = "/pptParticipantAUD", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PPMInfoRes pptParticipantAUD(@RequestBody PPMInfoReq ppmInfoReq) {
		log.info("Entering method pptParticipantAUD in PPMInfoController");
		if (TypeConvUtil.isNullOrEmpty(ppmInfoReq.getReqFuncCd())) {
			throw new InvalidRequestException(
					messageSource.getMessage("PPMInfoReq.reqFnCd.mandatory", null, Locale.US));
		}
		PPMInfoRes response = ppmInfoService.pptParticipantAUD(ppmInfoReq.getPpmInfoDto().getPptParticipant(),
				ppmInfoReq.getReqFuncCd());
		log.info("Exiting method pptParticipantAUD in PPMInfoController");
		return response;
	}

}
