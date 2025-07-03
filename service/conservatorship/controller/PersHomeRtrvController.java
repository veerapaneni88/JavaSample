package us.tx.state.dfps.service.conservatorship.controller;

import java.util.List;
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
import us.tx.state.dfps.service.common.request.PersHomeRtrvReq;
import us.tx.state.dfps.service.common.response.PersHomeRtrvoRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.service.PersHomeRtrvService;
import us.tx.state.dfps.service.cvs.dto.PersHomeRtrvoDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * service retrieves all persons associated with an event from STAGE_PERSON_LINK
 * and a subset of all persons at home during removal from PERSON_HOME_RMVL
 * table. If the person is found in both tables, an attribute is set to true, so
 * that the person will be 'checked' when displayed to the window.
 *
 * Aug 2, 2017- 7:08:50 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@RestController
@RequestMapping("/pershomertrv")
public class PersHomeRtrvController {

	@Autowired
	PersHomeRtrvService persHomeRtrvService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(PersHomeRtrvController.class);

	/**
	 * 
	 * Method Name: persHomeRtrv Method Description:This service retrieves all
	 * persons associated with an event from STAGE_PERSON_LINK and a subset of
	 * all persons at home during removal from PERSON_HOME_RMVL table. If the
	 * person is found in both tables, an attribute is set to true, so that the
	 * person will be 'checked' when displayed to the window.
	 * 
	 * @param persHomeRtrvReq
	 * @return PersHomeRtrvoRes
	 */
	@RequestMapping(value = "/retrieve", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PersHomeRtrvoRes persHomeRtrv(@RequestBody PersHomeRtrvReq persHomeRtrvReq) {
		log.debug("Entering method PersHomeRtrvo in PersHomeRtrvController");
		PersHomeRtrvoRes persHomeRtrvoRes = new PersHomeRtrvoRes();
		if (TypeConvUtil.isNullOrEmpty(persHomeRtrvReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("personHomeRetrieve.idStage.mandatory", null, Locale.US));
		}
		List<PersHomeRtrvoDto> liPersHomeRtrvoDto = persHomeRtrvService.personHomeRtrv(persHomeRtrvReq);
		persHomeRtrvoRes.setPersHomeRtrvoDto(liPersHomeRtrvoDto);
		log.debug("Exiting method PersHomeRtrvo in PersHomeRtrvController");
		return persHomeRtrvoRes;
	}
}
