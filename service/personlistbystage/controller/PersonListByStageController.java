package us.tx.state.dfps.service.personlistbystage.controller;

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
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PersonListStageInReq;
import us.tx.state.dfps.service.common.response.PersonListStageOutRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.personlistbystage.service.PersonListByStageService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Controller
 * to fetch information for personlist by stage. Implements methods from
 * Csys03sBean.java Oct 10, 2017- 6:52:49 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@RestController
@RequestMapping("/personlistbystage")
public class PersonListByStageController {

	@Autowired
	private PersonListByStageService personListByStageService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(PersonListByStageController.class);

	/**
	 * Method Name: fetchPersonListInfoByStage Method Description:Retrieves
	 * information for the Person List by stage
	 * 
	 * @param personListStageInReq
	 * @return PersonListStageOutRes
	 */
	@RequestMapping(value = "/fetchPersonListInfoByStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonListStageOutRes fetchPersonListInfoByStage(
			@RequestBody PersonListStageInReq personListStageInReq) {
		log.debug("Entering method fetchPersonListInfoByStage in PersonListByStageController");
		if (TypeConvUtil.isNullOrEmpty(personListStageInReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("personlistbystage.personListStageInDto.mandatory", null, Locale.US));
		}
		personListStageInReq.setCdStagePersType(ServiceConstants.STAFF_TYPE);
		PersonListStageOutRes personListStageOutRes = personListByStageService
				.fetchPersonListInfoByStage(personListStageInReq);
		log.debug("Exiting method fetchPersonListInfoByStage in PersonListByStageController");
		return personListStageOutRes;
	}
}
