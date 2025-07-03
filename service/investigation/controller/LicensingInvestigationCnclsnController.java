package us.tx.state.dfps.service.investigation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.LicensingInvCnclusnReq;
import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.LicensingInvCnclusnRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.investigation.service.LicensingInvSumService;
import us.tx.state.dfps.service.investigation.service.LicensingInvstCnclusnService;

import java.util.Locale;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Controller
 * class for sending req to LicensingInvstCnclusnService class,
 * LicensingInvSumService class> Mar 27, 2018- 3:05:39 PM © 2017 Texas
 * Department of Family and Protective Services.
 */
@RestController
@RequestMapping("/licensingInvCnclsn")
public class LicensingInvestigationCnclsnController {

	/** The licensing invst cnclusn service. */
	@Autowired
	LicensingInvstCnclusnService licensingInvstCnclusnService;

	/** The message source. */
	@Autowired
	MessageSource messageSource;

	@RequestMapping(value = "/stageHasContactTypeToPerson", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes stageHasContactTypeToPerson(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdContactType())) {
			throw new InvalidRequestException(messageSource.getMessage("common.contactType.mandatory", null, Locale.US));
		}
		return licensingInvstCnclusnService.stageHasContactTypeToPerson(commonHelperReq);
	}
}
