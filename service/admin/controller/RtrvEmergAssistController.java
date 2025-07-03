package us.tx.state.dfps.service.admin.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.admin.dto.RtrvEmergAssistiDto;
import us.tx.state.dfps.service.admin.dto.RtrvEmergAssistoDto;
import us.tx.state.dfps.service.admin.service.RtrvEmergAssistService;
import us.tx.state.dfps.service.common.response.RtrvEmergAssistoRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:The purpose
 * of this class is to retrieve information for the Emergency Assistance window.
 * Aug 5, 2017- 3:06:59 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@RestController
@RequestMapping("/rtrvemergassist")
public class RtrvEmergAssistController {

	@Autowired
	RtrvEmergAssistService objRtrvEmergAssistService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(RtrvEmergAssistController.class);

	/**
	 * 
	 * Method Name: RtrvEmergAssisto Method Description: This method will
	 * retrieve information for the Emergency Assistance window.
	 * 
	 * @param objRtrvEmergAssistiDto
	 * @return RtrvEmergAssistoRes
	 */
	@RequestMapping(value = "/rtrvemergassisto", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public RtrvEmergAssistoRes RtrvEmergAssisto(@RequestBody RtrvEmergAssistiDto objRtrvEmergAssistiDto) {
		log.debug("Entering method RtrvEmergAssisto in RtrvEmergAssistController");
		RtrvEmergAssistoRes response = new RtrvEmergAssistoRes();
		/*
		 * if
		 * (TypeConvUtil.isNullOrEmpty(objRtrvEmergAssistiDto.getUlIdEvent())) {
		 * throw new InvalidRequestException(
		 * messageSource.getMessage("RtrvEmergAssist.ulIdEvent.mandatory", null,
		 * Locale.US)); }
		 */
		if (TypeConvUtil.isNullOrEmpty(objRtrvEmergAssistiDto.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("RtrvEmergAssist.ulIdStage.mandatory", null, Locale.US));
		}
		// Invoking service method
		RtrvEmergAssistoDto responseData = objRtrvEmergAssistService.callRtrvEmergAssistService(objRtrvEmergAssistiDto);
		if (!TypeConvUtil.isNullOrEmpty(responseData)) {
			response.setResponse(responseData);
		}
		log.debug("Exiting method RtrvEmergAssisto in RtrvEmergAssistController");
		return response;
	}
}
