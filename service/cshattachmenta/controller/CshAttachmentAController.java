
package us.tx.state.dfps.service.cshattachmenta.controller;

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
import us.tx.state.dfps.service.common.request.CshAttachmentAReq;
import us.tx.state.dfps.web.fcl.dto.SexualVictimIncidentDto;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.csa.service.CSAService;
import us.tx.state.dfps.service.cshattachmenta.service.AttachmentAService;
import us.tx.state.dfps.service.person.service.TraffickingService;
import us.tx.state.dfps.service.person.serviceimpl.TraffickingServiceImpl;

/** service-business-FCL Class 
 * Name: CshAttachmentAController
 * Description:This is the Sexual Victimization history Report Attachment A Controller
 * � 2019 Texas Department of Family and Protective Services
 * Artifact ID: artf128756
 * **/
@RestController
@RequestMapping("/cshattachmenta")
public class CshAttachmentAController {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	AttachmentAService cshService;		
	
	public static final String ID_PERSON_VALIDATION = "common.personid.mandatory";

	/**
	 * Name: getCshAttachmentA
	 * Description: This is the service to get the Child Sexual aggression,Sexual Victimization history and 
	 * Trafficking details for Attachment A form 
	 * @param CSHAttachmentAReq cshReq 
	 * @return CommonFormRes
	 * Artifact ID: artf128756
	 */
	@RequestMapping(value = "/fetchAttachmentA", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getCshAttachmentA(@RequestBody CshAttachmentAReq cshReq) {

		/* if idPerson is Null, the following exception is thrown. */
		if (ObjectUtils.isEmpty(cshReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage(ID_PERSON_VALIDATION, null, Locale.US));
		}	
	
		CommonFormRes commonFormRes = new CommonFormRes();		
		commonFormRes.setPreFillData(TypeConvUtil
				.getXMLFormat(cshService.getCshDetailsByIDPerson(cshReq.getIdPerson())));		

		return commonFormRes;

	}

}