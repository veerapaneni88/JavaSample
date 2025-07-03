package us.tx.state.dfps.service.admin.controller;

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
import us.tx.state.dfps.service.admin.dto.AddrPhoneDto;
import us.tx.state.dfps.service.admin.dto.AddrPhoneReq;
import us.tx.state.dfps.service.admin.service.AddrPhoneRtrvService;
import us.tx.state.dfps.service.common.response.AddrPhoneRtrvRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * service retrieves a person's current primary address and phone number based
 * on the person's id. Aug 8, 2017- 7:48:38 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@RestController
@RequestMapping("/addrphonertrv")
public class AddrPhoneRtrvController {

	@Autowired
	AddrPhoneRtrvService addrPhoneRtrvService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(AddrPhoneRtrvController.class);

	/**
	 * This service retrieves a person's current primary address and phone
	 * number based on the person's id.
	 *
	 * @param addrPhoneReq
	 *            the addr phone req
	 * @return the addr phone rtrv res
	 */
	@RequestMapping(value = "/addrphonertrv", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public AddrPhoneRtrvRes AddrPhoneRtrv(@RequestBody AddrPhoneReq addrPhoneReq) {
		log.debug("Entering method AddrPhoneRtrv in AddrPhoneRtrvController");
		AddrPhoneRtrvRes addrPhoneRtrvRes = new AddrPhoneRtrvRes();
		AddrPhoneDto addrPhoneDto = new AddrPhoneDto();
		if (ObjectUtils.isEmpty(addrPhoneReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("personaddress.personId.mandatory", null, Locale.US));
		}
		addrPhoneDto = addrPhoneRtrvService.callAddrPhoneRtrvService(addrPhoneReq.getIdPerson());
		if (!ObjectUtils.isEmpty(addrPhoneDto)) {
			addrPhoneRtrvRes.setAddrPhoneDto(addrPhoneDto);
		}
		return addrPhoneRtrvRes;
	}
}
