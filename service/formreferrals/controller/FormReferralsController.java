package us.tx.state.dfps.service.formreferrals.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.AudDiligentSearchReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.CourtesyFormReq;
import us.tx.state.dfps.service.common.request.DiligentSearchRtrvReq;
import us.tx.state.dfps.service.common.request.FormReferralsReq;
import us.tx.state.dfps.service.common.request.OnLoadDlgntHdrReq;
import us.tx.state.dfps.service.common.response.AudDiligentSearchRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.CourtesyFormRes;
import us.tx.state.dfps.service.common.response.DiligentSearchRtrvRes;
import us.tx.state.dfps.service.common.response.FormReferralsRes;
import us.tx.state.dfps.service.common.response.OnLoadDlgntHdrRes;
import us.tx.state.dfps.service.common.response.PrsnListRtrvForDlgntSrchRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.formreferrals.dto.CaseWorkerDtlDto;
import us.tx.state.dfps.service.formreferrals.dto.FormReferralsDto;
import us.tx.state.dfps.service.formreferrals.dto.FbssReferralsDto;
import us.tx.state.dfps.service.formreferrals.service.FormReferralsService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jul 7, 2017- 2:27:56 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@RestController
@RequestMapping(value = "/forms")
public class FormReferralsController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	FormReferralsService formReferralsService;

	private static final Logger log = Logger.getLogger(FormReferralsController.class);

	/**
	 * Method Name: formReferralsList Method Description: Method to retrieve
	 * form referral list.
	 * 
	 * @param FormReq
	 * @return formReferralsRes
	 */
	@RequestMapping(value = "/formReferralsList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FormReferralsRes getFormReferralsList(@RequestBody FormReferralsReq formReq) {
		log.info("TransactionId :" + formReq.getTransactionId());
		FormReferralsRes formReferralsRes = new FormReferralsRes();
		formReferralsRes = formReferralsService.getFormReferralsList(formReq);
		return formReferralsRes;

	}

	/**
	 * Method Name: formReferralsDelete Method Description: Method to delete a
	 * record from FormsReferralTable.
	 * 
	 * @param FormReq
	 * @return formReferralsRes
	 */
	@RequestMapping(value = "/formReferralsDelete", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FormReferralsRes formReferralsDelete(@RequestBody FormReferralsReq formReq) {
		FormReferralsRes formReferralsRes = new FormReferralsRes();
		formReferralsRes = formReferralsService.formReferralsDelete(formReq);
		log.info("TransactionId :" + formReq.getTransactionId());
		return formReferralsRes;

	}

	/**
	 * Method Name: GetCourtesyDetail Method Description: Method to retrieve
	 * details from courtesy referral table.
	 * 
	 * @param FormReq
	 * @return CourtesyReferlRes
	 */
	@RequestMapping(value = "/getCourtesyFormDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CourtesyFormRes getCourtesyFormDetail(@RequestBody CourtesyFormReq courtesyFormReq) {

		log.info("TransactionId :" + courtesyFormReq.getTransactionId());
		return formReferralsService.getCourtesyReferralDetail(courtesyFormReq);

	}

	/**
	 * Method Name: saveCourtesyFormDetail Method Description: Method to save
	 * Courtesy detail.
	 * 
	 * @param formReferralsReq
	 * @return
	 */
	@RequestMapping(value = "/saveCourtesyFormDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CourtesyFormRes saveCourtesyFormDetail(@RequestBody FormReferralsReq formReferralsReq) {

		log.info("TransactionId :" + formReferralsReq.getTransactionId());
		return formReferralsService.saveCourtesyReferralDetail(formReferralsReq);

	}

	/**
	 * Method Name: getQuickFind Method Description:Method to retrieve record
	 * from quick_find table.
	 * 
	 * @param formReq
	 * @return
	 */
	@RequestMapping(value = "/getQuickFind", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FormReferralsRes getQuickFind(@RequestBody FormReferralsReq formReq) {
		log.info("TransactionId :" + formReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(formReq.getIdFormReferral())) {
			throw new InvalidRequestException(
					messageSource.getMessage("msg.formReferrals.formReferralsId", null, Locale.US));
		}
		return formReferralsService.getQuickFind(formReq);
	}

	/**
	 * Method Name: quickFindSave Method Description:Method to save record in
	 * quick_find table.
	 * 
	 * @param formReq
	 * @return
	 */
	@RequestMapping(value = "/saveQuickFind", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FormReferralsRes saveQuickFind(@RequestBody FormReferralsReq formReq) {
		log.info("TransactionId :" + formReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(formReq.getQuickFindList())) {
			throw new InvalidRequestException(
					messageSource.getMessage("msg.formReferrals.quickFindDett", null, Locale.US));
		} else if (TypeConvUtil.isNullOrEmpty(formReq.getFormReferralsList())) {
			throw new InvalidRequestException(
					messageSource.getMessage("msg.formReferrals.formReferralsDet", null, Locale.US));
		}
		return formReferralsService.saveQuickFind(formReq);
	}

	/**
	 * Method Name: getFbssReferrals Method Description:Method to retrieve
	 * record from fbss_referrals table.
	 * 
	 * @param formReq
	 * @return
	 */
	@RequestMapping(value = "/getFbssReferrals", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FormReferralsRes getFbssReferrals(@RequestBody FormReferralsReq formReq) {
		log.info("TransactionId :" + formReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(formReq.getIdFormReferral())) {
			throw new InvalidRequestException(messageSource.getMessage("msg.formReferrals.fbssDet", null, Locale.US));
		}
		return formReferralsService.getFbssReferrals(formReq);
	}

	/**
	 * Method Name: fbssSave Method Description:Method to save record in
	 * fbss_referrals table.
	 * 
	 * @param formReq
	 * @return
	 */
	@RequestMapping(value = "/saveFbss", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FormReferralsRes saveFBSS(@RequestBody FormReferralsReq formReq) {
		log.info("TransactionId :" + formReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(formReq.getFormReferralsList())) {
			throw new InvalidRequestException(
					messageSource.getMessage("msg.formReferrals.formReferralsDet", null, Locale.US));
		} else if (TypeConvUtil.isNullOrEmpty(formReq.getFbssReferralsList())) {
			throw new InvalidRequestException(messageSource.getMessage("msg.formReferrals.fbssDet", null, Locale.US));
		}
		return formReferralsService.saveFBSS(formReq);
	}

	/**
	 * Method Name: quickFindDelete Method Description:Method to delete record
	 * from quick_find table
	 * 
	 * @param formReq
	 * @return
	 */
	@RequestMapping(value = "/deleteQuickFind", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FormReferralsRes deleteQuickFind(@RequestBody FormReferralsReq formReq) {
		FormReferralsRes formReferralsRes = new FormReferralsRes();
		if (TypeConvUtil.isNullOrEmpty(formReq.getFormReferralsList())) {
			throw new InvalidRequestException(
					messageSource.getMessage("msg.formReferrals.formReferralsDet", null, Locale.US));
		} else if (formReq.getFormReferralsList().get(0).getIdFormsReferrals() == 0) {
			throw new InvalidRequestException(
					messageSource.getMessage("msg.formReferrals.formReferralsId", null, Locale.US));
		}
		formReferralsRes = formReferralsService.deleteQuickFind(formReq);
		log.info("TransactionId :" + formReq.getTransactionId());
		return formReferralsRes;

	}

	/**
	 * Method Name: fbssDelete Method Description: Method to delete record from
	 * the fbss_referrals table.
	 * 
	 * @param formReq
	 * @return
	 */
	@RequestMapping(value = "/deleteFbss", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FormReferralsRes deleteFBSS(@RequestBody FormReferralsReq formReq) {
		FormReferralsRes formReferralsRes = new FormReferralsRes();
		if (TypeConvUtil.isNullOrEmpty(formReq.getFormReferralsList())) {
			throw new InvalidRequestException(
					messageSource.getMessage("msg.formReferrals.formReferralsDet", null, Locale.US));
		} else if (formReq.getFormReferralsList().get(0).getIdFormsReferrals() == 0) {
			throw new InvalidRequestException(
					messageSource.getMessage("msg.formReferrals.formReferralsId", null, Locale.US));
		}
		formReferralsRes = formReferralsService.deleteFBSS(formReq);
		log.info("TransactionId :" + formReq.getTransactionId());
		return formReferralsRes;

	}

	/**
	 * Method Name: saveCourtesyFormIntrvw Method Description: Method to save
	 * Courtesy form interview
	 * 
	 * @param FormReq
	 * @return
	 */
	@RequestMapping(value = "/saveCourtesyFormIntrvw", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CourtesyFormRes saveCourtesyFormIntrvw(@RequestBody FormReferralsReq formReferralsReq) {

		log.info("TransactionId :" + formReferralsReq.getTransactionId());
		return formReferralsService.saveCourtesyReferralIntrvw(formReferralsReq);

	}

	/**
	 * Method Name: courtesyReferralsDelete Method Description: Method to delete
	 * a record from courtesyReferrals Table.
	 * 
	 * @param FormReq
	 * @return formReferralsRes
	 */
	@RequestMapping(value = "/courtesyReferralsDelete", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FormReferralsRes courtesyReferralsDelete(@RequestBody CourtesyFormReq courtesyFormReq) {
		FormReferralsRes formReferralsRes = new FormReferralsRes();
		formReferralsRes = formReferralsService.courtesyReferralDelete(courtesyFormReq);
		log.info("TransactionId :" + courtesyFormReq.getTransactionId());
		return formReferralsRes;

	}

	/**
	 * Method Name: getQuickFindPerson Method Description:This method is used to
	 * fetch the page values onload.
	 * 
	 * @param formReq
	 * @return FormReferralsRes
	 */
	@RequestMapping(value = "/getQuickFindPerson", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FormReferralsRes getQuickFindPerson(@RequestBody FormReferralsReq formReq) {
		if (TypeConvUtil.isNullOrEmpty(formReq.getQuickFindPersonDto())
				&& TypeConvUtil.isNullOrEmpty(formReq.getQuickFindPersonDto().get(0).getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return formReferralsService.getQuickFindPerson(formReq);
	}

	/**
	 * 
	 * Method Name: getDiligentSearch Method Description:This service will
	 * retrieve data for Diligent Search screen.
	 * 
	 * @param diligentSearchRtrvReq
	 * @return DiligentSearchRtrvRes
	 */
	@RequestMapping(value = "/getDiligentSearch", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DiligentSearchRtrvRes getDiligentSearch(
			@RequestBody DiligentSearchRtrvReq diligentSearchRtrvReq) {

		if (TypeConvUtil.isNullOrEmpty(diligentSearchRtrvReq.getIdFormsReferrals())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.idFormsReferrals.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + diligentSearchRtrvReq.getTransactionId());
		return formReferralsService.getDiligentSearchRtrv(diligentSearchRtrvReq);
	}

	/**
	 * 
	 * Method Name: getDiligentSearch Method Description: This method will
	 * perform SAVE and UPDATE operations on Diligent Search Screen.
	 * 
	 * @param audDiligentSearchReq
	 * @return AudDiligentSearchRes
	 */
	@RequestMapping(value = "/saveOrUpdateDiligentSearch", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  AudDiligentSearchRes saveOrUpdateDiligentSearch(
			@RequestBody AudDiligentSearchReq audDiligentSearchReq) {

		if (TypeConvUtil.isNullOrEmpty(audDiligentSearchReq.getFormReferralDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.formReferralsDto.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(audDiligentSearchReq.getReqFuncCd())) {
			throw new InvalidRequestException(messageSource.getMessage("common.ReqFuncCd.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + audDiligentSearchReq.getTransactionId());
		return formReferralsService.saveAndUpdateDiligentSearch(audDiligentSearchReq);
	}

	/**
	 * 
	 * Method Name: rtrvPersonList Method Description: This service retrieves
	 * list of Persons by passing stage id.
	 * 
	 * @param diligentSearchRtrvReq
	 * @return PrsnListRtrvForDlgntSrchRes
	 */
	@RequestMapping(value = "/dlgntSrchPersonList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PrsnListRtrvForDlgntSrchRes rtrvPersonList(
			@RequestBody DiligentSearchRtrvReq diligentSearchRtrvReq) {

		if (TypeConvUtil.isNullOrEmpty(diligentSearchRtrvReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + diligentSearchRtrvReq.getTransactionId());
		return formReferralsService.getPersonList(diligentSearchRtrvReq);
	}

	/**
	 * Method Name: deletedlgtSearch Method Description:Method to delete record
	 * from the
	 * DLGNT_SRCH_HDR,DLGNT_SRCH_DTL,DLGNT_SRCH_CHILD_DTL,FORMS_REFERRALS table.
	 * 
	 * @param formReq
	 * @return FormReferralsRes
	 */
	@RequestMapping(value = "/deletedlgtSearch", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FormReferralsRes deletedlgtSearch(@RequestBody FormReferralsReq formReq) {
		FormReferralsRes formReferralsRes = new FormReferralsRes();
		if (TypeConvUtil.isNullOrEmpty(formReq.getIdFormReferral())) {
			throw new InvalidRequestException(
					messageSource.getMessage("msg.formReferrals.formReferralsId", null, Locale.US));
		}
		formReferralsRes = formReferralsService.deletedlgtSearch(formReq);
		log.info("TransactionId :" + formReq.getTransactionId());
		return formReferralsRes;

	}

	/**
	 * 
	 * Method Name: getDlgntSrchHdrByStageId Method Description: This Service
	 * will retrieve Case Worker, Requester and Supervisor info for Diligent
	 * Search Header.
	 * 
	 * @param onLoadDlgntHdrReq
	 * @return OnLoadDlgntHdrRes
	 */
	@RequestMapping(value = "/dlgntSrchHdrByStageId", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  OnLoadDlgntHdrRes getDlgntSrchHdrByStageId(@RequestBody OnLoadDlgntHdrReq onLoadDlgntHdrReq) {
		if (TypeConvUtil.isNullOrEmpty(onLoadDlgntHdrReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(onLoadDlgntHdrReq.getIdRqstrPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.idRqstrPerson.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + onLoadDlgntHdrReq.getTransactionId());
		return formReferralsService.getDlgntSrchHdrByStageId(onLoadDlgntHdrReq);
	}

	/**
	 * 
	 * Method Name: Method will retrieve the county of the caseworker.
	 * 
	 * @param onLoadDlgntHdrReq
	 * @return OnLoadDlgntHdrRes
	 */
	@RequestMapping(value = "/getCaseWorkerCounty", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CaseWorkerDtlDto getCaseWorkerCounty(@RequestBody CourtesyFormReq courtesyFormReq) {

		if (TypeConvUtil.isNullOrEmpty(courtesyFormReq.getIdCaseWorker())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.idRqstrPerson.mandatory", null, Locale.US));
		}

		return formReferralsService.getCaseWorkerCounty(courtesyFormReq.getIdCaseWorker());
	}
	
	/**
	 * artf151021 
	 * Method Name: Method will retrieve the county of the caseworker.
	 * 
	 * @param onLoadDlgntHdrReq
	 * @return OnLoadDlgntHdrRes
	 */
	@RequestMapping(value = "/getFbssReferralForm", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FormReferralsRes getFbssReferralForm(@RequestBody FormReferralsReq formReferralsReq) {

		if (TypeConvUtil.isNullOrEmpty(formReferralsReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		
		FormReferralsRes formReferralsRes = new FormReferralsRes();

		formReferralsRes.setPreFillData(TypeConvUtil.getXMLFormat(formReferralsService.getFbssReferralForm(formReferralsReq)));
		
		return formReferralsRes;
	}

	/**PPM#46797 artf150671
	 * Method Name: getHouseHoldDetails
	 * Method Description: This method retrieves household SDM safety assessment, and address details.
	 *
	 * @param formReferralsReq
	 * @return
	 */
	@RequestMapping(value = "/getHouseHoldDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FormReferralsRes getHouseHoldDetails(@RequestBody FormReferralsReq formReferralsReq) {

		if (TypeConvUtil.isNullOrEmpty(formReferralsReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		FormReferralsRes	response =new FormReferralsRes();
		response.setQuickFindPersonList(formReferralsService.getHouseHoldDetails(formReferralsReq.getIdStage()));
		return response;
	}


	/**
	 * PPM#46797 artf150671
	 * Method Name: validateSaveAndSubmit
	 * Method Description: This method valdiates on SaveAndSubmit
	 * @param CommonHelperReq
	 * @return
	 */
	@RequestMapping(value = "/validateSaveAndSubmit", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes validateSaveAndSubmit(@RequestBody CommonHelperReq helperReq) {

		if (TypeConvUtil.isNullOrEmpty(helperReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(helperReq.getIndApprovalFlow())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}

		CommonHelperRes	response =new CommonHelperRes();
		response.setValidationMessageNbr(formReferralsService.validateSaveAndSubmit(helperReq.getIdStage(),
				helperReq.getIndApprovalFlow(), helperReq.getIdCpsSA(), helperReq.getIdPerson(), helperReq.getIdApproval()));
		return response;
	}

	/**
	 * Artifact ID: artf151569
	 * Method Name: getFbssReferralByApproval
	 * Method Description: This method retrieves the FbssReferral based on the Approval ID
	 *
	 * @param commonHelperReq
	 * @return
	 */
	@RequestMapping(value = "getFbssReferralsByApproval", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public FormReferralsRes getFbssReferralByApproval(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdApproval())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}

		Long idFormReferrals =  formReferralsService.getFormReferralByApprovalId(commonHelperReq.getIdApproval());

		FormReferralsReq formReq = new FormReferralsReq();
		formReq.setIdFormReferral(idFormReferrals);

		return formReferralsService.getFbssReferrals(formReq);
	}

	/**
	 * PPM#46797 artf150671
	 * Method Name: getFBSSReferrals
	 * Method Description: Method to retrieve FBSS referral details
	 * @param FormReferralsReq
	 * @return FormReferralsRes
	 */
	@RequestMapping(value = "/getFBSSReferralsForFPR", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FormReferralsRes getFBSSReferralsForFPR(@RequestBody FormReferralsReq formReq) {
		log.info("TransactionId :" + formReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(formReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		FormReferralsRes formReferralsRes = new FormReferralsRes();
		List<FormReferralsDto> formReferralsDtoList = new ArrayList<FormReferralsDto>();
		formReferralsDtoList.add(formReferralsService.getFBSSReferralsForFPR(formReq.getIdStage()));
		formReferralsRes.setFormReferralsList(formReferralsDtoList);
		return formReferralsRes;
	}


	/**PPM#46797 artf150671
	 * Method Name: getHouseHoldDetailsByCpsSA
	 * Method Description: This method retrieves household SDM safety assessment, and address details based on ID CPS SA.
	 *
	 * @param formReferralsReq
	 * @return
	 */
	@RequestMapping(value = "/getHouseHoldDetailsByCpsSA", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FormReferralsRes getHouseHoldDetailsByCpsSA(@RequestBody FormReferralsReq formReferralsReq) {

		if (TypeConvUtil.isNullOrEmpty(formReferralsReq.getIdCpsSA())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.request.mandatory", null, Locale.US));
		}
		FormReferralsRes	response =new FormReferralsRes();
		response.setQuickFindPersonList(formReferralsService.getHouseHoldDetailsBySA(formReferralsReq.getIdCpsSA()));
		return response;
	}

	@RequestMapping(value = "/getDiligentSearchHdrId", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DiligentSearchRtrvRes getDiligentSearchHdrId(
			@RequestBody DiligentSearchRtrvReq diligentSearchRtrvReq) {

		if (TypeConvUtil.isNullOrEmpty(diligentSearchRtrvReq.getIdFormsReferrals())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.idFormsReferrals.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + diligentSearchRtrvReq.getTransactionId());
		return formReferralsService.getDiligentSearchHdrId(diligentSearchRtrvReq);
	}

}
