package us.tx.state.dfps.service.investigation.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.PcspReq;
import us.tx.state.dfps.service.common.request.SrvreferralsReq;
import us.tx.state.dfps.service.common.response.PcspRes;
import us.tx.state.dfps.service.common.response.SrvreferralsRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.investigation.service.SrvreferralsService;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CINV54S,CINV55S
 * Class Description: Service to retrieve all the information needed to
 * poplulate the Services and Referrals Checklist window and save, update,
 * insert, delete,depending on what was changed in the Services and Referrals
 * Checklist window.
 */

@RestController
@RequestMapping("/serviceReferrals")
public class SrvreferralsController {

	@Autowired
	private SrvreferralsService srvrflService;

	@Autowired
	MessageSource messageSource;

	public SrvreferralsController() {

	}

	/**
	 * 
	 * Method Description: This Method is to get response through service layer
	 * by giving ulIdEvent,ulIdStage,szCdStage in request
	 * object(ServiceAndRefferalsRequest) Service Name: CINV54S
	 * 
	 * @param srvrflReq
	 * @return
	 */
	@RequestMapping(value = "/getServiceReferrals", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SrvreferralsRes getSrvrflInfo(@RequestBody SrvreferralsReq srvrflReq) {

		if (TypeConvUtil.isNullOrEmpty(srvrflReq)) {
			throw new InvalidRequestException(messageSource.getMessage("srvrfl.req.mandatory", null, Locale.US));
		}
		if (StringUtils.isEmpty(srvrflReq.getUlIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("srvrfl.event.id.mandatory", null, Locale.US));
		}
		return srvrflService.getSrvrflInfo(srvrflReq);

	}

	/**
	 * 
	 * Method Description: This method will save or update all columns on the
	 * CPS_CHKLST table. It will also insert or delete one or more rows from the
	 * CPS_CHKLST_ITEM table, depending on what was changed in the Services and
	 * Referrals Checklist window. Service Name: CINV55S
	 * 
	 * @param srvrflReq
	 * @return
	 */
	@RequestMapping(value = "/saveServiceReferrals", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SrvreferralsRes saveOrUpdateSrvrflInfo(@RequestBody SrvreferralsReq srvrflReq) {

		if (TypeConvUtil.isNullOrEmpty(srvrflReq)) {
			throw new InvalidRequestException(messageSource.getMessage("srvrfl.req.mandatory", null, Locale.US));
		}
		if (StringUtils.isEmpty(srvrflReq.getCpsChecklistDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("srvrfl.cps.checklist.mandatory", null, Locale.US));
		}
		return srvrflService.saveOrUpdateSrvrflInfo(srvrflReq);

	}

	/**
	 * Retrieves the parental child safety placement details from the
	 * CHILD_SAFETY_PLCMT, PERSON, STAGE tables. Service Name: PCSPEjb
	 * 
	 * @param pcspReq
	 * @return PcspRes
	 */
	@RequestMapping(value = "/pcspList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PcspRes getPcspList(@RequestBody PcspReq pcspReq) {

		if (TypeConvUtil.isNullOrEmpty(pcspReq.getCaseId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(pcspReq.getCdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.cdStage.mandatory", null, Locale.US));
		}

		return srvrflService.displayPCSPList(pcspReq);
	}

}
