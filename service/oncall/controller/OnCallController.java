package us.tx.state.dfps.service.oncall.controller;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.admin.dto.AddOnCallInDto;
import us.tx.state.dfps.service.common.request.OnCallSearchReq;
import us.tx.state.dfps.service.common.response.AddOnCallResponse;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.oncall.service.OnCallService;

@RestController
@RequestMapping("/onCall")
public class OnCallController {

	@Autowired
	OnCallService onCallService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(OnCallController.class);

	@RequestMapping(value = "/addOnCall", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  AddOnCallResponse addOnCall(@RequestBody OnCallSearchReq onCallSearchReq) {
		AddOnCallInDto addOnCallInDto = new AddOnCallInDto();
		AddOnCallResponse addOnCallResponse = new AddOnCallResponse();
		try {
			BeanUtils.copyProperties(addOnCallInDto, onCallSearchReq);
			addOnCallResponse = onCallService.addProposedShiftOrBlock(addOnCallInDto);
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.error(e.getMessage());
		}
		return addOnCallResponse;
	}

	/**
	 * Artifact ID: artf151569
	 * Method Name: getRouterPersonOnCall
	 * Method Description: Retrieves the Person with Router Name if the Router Schedule found for
	 * given county with Program FBSS
	 *
	 * @param onCallSearchReq
	 * @return
	 */
	@RequestMapping(value = "/getRouterPersonOnCall", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getRouterPersonOnCall(@RequestBody OnCallSearchReq onCallSearchReq) {

		Person person = onCallService.getRouterPersonOnCall(onCallSearchReq);

		CommonHelperRes commonHelperRes = new CommonHelperRes();
		if (!ObjectUtils.isEmpty(person)) {
			commonHelperRes.setUlIdPerson(person.getIdPerson());
			commonHelperRes.setNmPerson(person.getNmPersonFull());
		}

		return commonHelperRes;
	}

}
