/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: This is the Service Controller for Child Sexual Aggression Page. 
 *Sep 17, 2018- 4:34:10 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.csa.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.casepackage.dto.CSADto;
import us.tx.state.dfps.service.common.request.CSAReq;
import us.tx.state.dfps.service.common.request.SexualVictimHistoryReq;
import us.tx.state.dfps.service.common.response.CSARes;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.SexualVictimHistoryRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.csa.service.CSAService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This is the
 * Service Controller for Child Sexual Aggression Page.> Sep 17, 2018- 4:34:10
 * PM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/csa")
public class CSAController {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	CSAService csaService;

	public static final String ID_PERSON_VALIDATION = "common.personid.mandatory";

	/**
	 * Method Name: fetchCSA Method Description: This Method is to fetch Child
	 * Sexual Aggression Details.
	 * 
	 * @param csaReq
	 * @return
	 */
	@RequestMapping(value = "/fetchCSA", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CSARes fetchCSA(@RequestBody CSAReq csaReq) {
		CSARes csaRes = new CSARes();
		/* if idPerson is Null, the following exception is thrown. */
		if (ObjectUtils.isEmpty(csaReq.getCsaDto().getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage(ID_PERSON_VALIDATION, null, Locale.US));
		}
		/* Service call to fetch records for Child Sexual Aggression Page. */
		csaRes.setCsaDto(csaService.fetchCSA(csaReq.getCsaDto()));
		return csaRes;
	}

	/**
	 * Method Name: saveCSA Method Description: This Method is to save newly
	 * added Child Sexual Aggression Details.
	 * 
	 * @param csaReq
	 * @return
	 */
	@RequestMapping(value = "/saveCSA", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CSARes saveCSA(@RequestBody CSAReq csaReq) {
		CSARes csaRes = new CSARes();
		/* if idPerson is Null, the following exception is thrown. */
		if (ObjectUtils.isEmpty(csaReq.getCsaDto().getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage(ID_PERSON_VALIDATION, null, Locale.US));
		}
		/* Service call to save newly added records. */
		csaRes.setCsaDto(csaService.saveCSA(csaReq.getCsaDto()));
		return csaRes;
	}

	/**
	 * Name: fetchFSNAFbssForm Description: This is the service to get the CSA
	 * Episode details for CSA form.
	 * 
	 * @param csaReq
	 * @return
	 */
	@RequestMapping(value = "/getCSADetailsByIDPersonAndEpisodes", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getCSADetailsByIDPersonAndEpisodes(@RequestBody CSAReq csaReq) {

		/* if idPerson is Null, the following exception is thrown. */
		if (ObjectUtils.isEmpty(csaReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage(ID_PERSON_VALIDATION, null, Locale.US));
		}

		CSADto csaDto = new CSADto();
		csaDto.setIdPerson(csaReq.getIdPerson());

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil
				.getXMLFormat(csaService.getCSADetailsByIDPersonAndEpisodes(csaDto, csaReq.getSelectedEpisodes())));
		return commonFormRes;

	}

	/**
	 *
	 *Method Name:	deleteCSAIncident
	 *Method Description:Service method for deleting CSA Details
	 *@param request
	 *@return SexualVictimHistoryRes
	 */
	@RequestMapping(value = "/deleteCsaIncidents", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SexualVictimHistoryRes deleteCsaEpisodesAndIncidents(@RequestBody CSAReq request) {
		if (TypeConvUtil.isNullOrEmpty(request.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		List<Long> episodeIncidentIds = request.getIncidentIdList();
		if (episodeIncidentIds != null) csaService.deleteCsaEpisodeIncidents(episodeIncidentIds, request.getIdPerson());
		SexualVictimHistoryRes response = new SexualVictimHistoryRes();
		return response;
	}
}
