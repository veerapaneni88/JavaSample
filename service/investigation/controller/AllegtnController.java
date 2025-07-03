package us.tx.state.dfps.service.investigation.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.AllegationAUDReq;
import us.tx.state.dfps.service.common.request.CalOverallDispReq;
import us.tx.state.dfps.service.common.request.CommonCaseIdReq;
import us.tx.state.dfps.service.common.request.CommonStageIdReq;
import us.tx.state.dfps.service.common.request.DisplayAllegDtlReq;
import us.tx.state.dfps.service.common.request.InvAllegListReq;
import us.tx.state.dfps.service.common.request.SaveAllgtnMultiReq;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.common.response.AllegationAUDRes;
import us.tx.state.dfps.service.common.response.CalOverallDispRes;
import us.tx.state.dfps.service.common.response.CommonDateRes;
import us.tx.state.dfps.service.common.response.DisplayAllegDtlRes;
import us.tx.state.dfps.service.common.response.InvAllegListRes;
import us.tx.state.dfps.service.common.response.SaveAllgtnMultiRes;
import us.tx.state.dfps.service.common.request.*;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.investigation.service.AllegtnService;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Apr 3, 2017 - 9:41:50 AM
 */

@RestController
@RequestMapping("/allegtn")
public class AllegtnController {

	@Autowired
	AllegtnService allegtnService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(AllegtnController.class);

	/**
	 * This method is to get the allegation list for the allegation screen the
	 * Investigation Allegation List list box. It also retrieves the stage's
	 * overall disposition. Method Description: legacy service name - CINV44S
	 * 
	 * @param invAllegListReq
	 * @return
	 */
	@RequestMapping(value = "/getallegtn", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  InvAllegListRes getAllegations(@RequestBody InvAllegListReq invAllegListReq) {

		if (TypeConvUtil.isNullOrEmpty(invAllegListReq.getSzCdStageProgram())) {
			throw new InvalidRequestException(
					messageSource.getMessage(ServiceConstants.STAGEID_MANDATORY, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(invAllegListReq.getSzCdAllegIncidentStage())) {
			throw new InvalidRequestException(messageSource.getMessage(ServiceConstants.STAGE_MANDATORY_CMN, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(invAllegListReq.getUlIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		log.info(ServiceConstants.TRANSACTION_ID + invAllegListReq.getTransactionId());
		InvAllegListRes allegRes = allegtnService.getAllegations(invAllegListReq);
		return allegRes;

	}

	/**
	 * This method is to Updates multiple Allegation Details. Method
	 * Description: legacy service name - CINV09S
	 * 
	 * @param SaveAllgtnMultiReq
	 * @return SaveAllgtnMultiRes
	 */
	@RequestMapping(value = "/saveallgtnmulti", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SaveAllgtnMultiRes saveAllgtnMulti(@RequestBody SaveAllgtnMultiReq saveAllgtnMultiReq) {

		if (TypeConvUtil.isNullOrEmpty(saveAllgtnMultiReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage(ServiceConstants.STAGE_MANDATORY_CMN, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(saveAllgtnMultiReq.getSzCdStageProgram())) {
			throw new InvalidRequestException(
					messageSource.getMessage(ServiceConstants.STAGEID_MANDATORY, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(saveAllgtnMultiReq.getAllegationIdList().size() > 0)) {
			throw new InvalidRequestException(
					messageSource.getMessage("allegation.atleastoneAllgn.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(saveAllgtnMultiReq.getSzCdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("allegation.atleastoneAllgn.mandatory", null, Locale.US));
		}
		log.info(ServiceConstants.TRANSACTION_ID + saveAllgtnMultiReq.getTransactionId());
		return allegtnService.saveAllgtnMulti(saveAllgtnMultiReq);

	}

	/**
	 * This service is to display the allegation details legacy service name -
	 * CINV46S
	 * 
	 * @param displayAllegDtlReq
	 * @return
	 */
	@RequestMapping(value = "/diaplyallegtndtl", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DisplayAllegDtlRes diaplyAllegtnDetail(@RequestBody DisplayAllegDtlReq displayAllegDtlReq) {

		if (TypeConvUtil.isNullOrEmpty(displayAllegDtlReq.getUlIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage(ServiceConstants.STAGE_MANDATORY_CMN, null, Locale.US));
		}
		log.info(ServiceConstants.TRANSACTION_ID + displayAllegDtlReq.getTransactionId());
		return allegtnService.diaplyAllegtnDetail(displayAllegDtlReq);

	}

	/**
	 * this is AUD service for Allegation legacy service name - CINV47S
	 * 
	 * @param allegationAUDReq
	 * @return
	 */
	@RequestMapping(value = "/allegationaud", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  AllegationAUDRes allegationAUD(@RequestBody AllegationAUDReq allegationAUDReq) {

		if (TypeConvUtil.isNullOrEmpty(allegationAUDReq.getSzCdStageProgram())) {
			throw new InvalidRequestException(messageSource.getMessage("common.program.mandatory", null, Locale.US));
		}
		log.info(ServiceConstants.TRANSACTION_ID + allegationAUDReq.getTransactionId());
		return allegtnService.allegationAUD(allegationAUDReq);

	}

	/**
	 * This is the Overall Roles and Disposition Service. It is run when all the
	 * allegations in a given stage have been assigned a disposition but no
	 * overall disposition has yet been assigned. This service calculates each
	 * person's overall role and the stage's overall disposition. Overall roles
	 * are saved in STAGE_PERSON_LINK; overall disposition is saved to the
	 * appropriate Investigation Conclusion table, based on Program. legacy
	 * service name - CINV45S
	 * 
	 * @param calOverallDispReq
	 * @return
	 */
	@RequestMapping(value = "/caloveralldisp", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CalOverallDispRes calOverallDisp(@RequestBody CalOverallDispReq calOverallDispReq) {

		if (TypeConvUtil.isNullOrEmpty(calOverallDispReq.getUlIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage(ServiceConstants.STAGE_MANDATORY_CMN, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(calOverallDispReq.getSzCdStageProgram())) {
			throw new InvalidRequestException(
					messageSource.getMessage(ServiceConstants.STAGEID_MANDATORY, null, Locale.US));
		}
		log.info(ServiceConstants.TRANSACTION_ID + calOverallDispReq.getTransactionId());
		return allegtnService.calOverallDisp(calOverallDispReq);

	}

	/**
	 * This method nullifies any answered Child Sex/Labor Trafficking question
	 * in the case
	 * 
	 * @param commonCaseIdReq
	 * @return
	 */
	@RequestMapping(value = "/updtchldsexlabortraffic", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ServiceResHeaderDto updateChildSexLaborTrafficking(
			@RequestBody CommonCaseIdReq commoncaseIdReq) {

		if (TypeConvUtil.isNullOrEmpty(commoncaseIdReq.getUlIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		log.info(ServiceConstants.TRANSACTION_ID + commoncaseIdReq.getTransactionId());
		return allegtnService.updateChildSexLaborTrafficking(commoncaseIdReq);

	}

	@RequestMapping(value = "/fetchdtintakeforidstage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonDateRes fetchDtIntakeForIdStage(@RequestBody CommonStageIdReq commonStageIdReq) {

		if (TypeConvUtil.isNullOrEmpty(commonStageIdReq.getUlIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage(ServiceConstants.STAGE_MANDATORY_CMN, null, Locale.US));
		}
		log.info(ServiceConstants.TRANSACTION_ID + commonStageIdReq.getTransactionId());
		return allegtnService.fetchDtIntakeForIdStage(commonStageIdReq);

	}
	@RequestMapping(value = "/getValidallegtions", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  boolean getValidallegtions(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		log.info("IdStage :" + commonHelperReq.getIdStage());
		boolean isValidAlleg  = allegtnService.getValidAllegations(Long.valueOf(commonHelperReq.getIdStage()));
		return isValidAlleg;

	}
	
	/**
	 * Method to find the allegation problem count for selected allegation id
	 *
	 * @param idAllegation selected allegation id
	 * @return return response
	 */
	@RequestMapping(value = "/allegationProblemCnt", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonCountRes fetchAllegationProblemCount(@RequestBody Long idAllegation){
		return allegtnService.getAllegationProblemCount(idAllegation);
	}

	/**
	 * Method helps to get the request from Web and process it Service business
	 *
	 * @param audReq requested allegation data
	 */
	@RequestMapping(value = "/deleteSPSourcesForAllegation", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public void deleteSPSourcesForAllegation(@RequestBody AllegationAUDReq audReq){
		Long idStage = audReq.getAllegationDetail().getIdStage();
		Long idAllegation = audReq.getAllegationDetail().getIdAllegation();
		allegtnService.deleteSPSourcesForAllegationRecord(idStage, idAllegation);

	}

}
