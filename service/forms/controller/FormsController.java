package us.tx.state.dfps.service.forms.controller;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import us.tx.state.dfps.common.exception.FormsException;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.common.request.CompositeFormsReq;
import us.tx.state.dfps.service.common.request.DocLogReq;
import us.tx.state.dfps.service.common.request.FormsReq;
import us.tx.state.dfps.service.common.request.FormsSaveReq;
import us.tx.state.dfps.service.common.request.PlcmntFormReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.DocumentTemplateListRes;
import us.tx.state.dfps.service.common.response.FormsDocumentRes;
import us.tx.state.dfps.service.common.response.FormsServiceRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cps.service.CpsClosingSummaryService;
import us.tx.state.dfps.service.forms.dto.DocumentMetaData;
import us.tx.state.dfps.service.forms.dto.DocumentTemplateDto;
import us.tx.state.dfps.service.forms.dto.DocumentTmpltCheckDto;
import us.tx.state.dfps.service.forms.dto.NewUsingDocumentDto;
import us.tx.state.dfps.service.forms.service.FormsService;
import us.tx.state.dfps.service.person.dto.CrpRequestStatusDto;
import us.tx.state.dfps.service.placement.dto.PlcmntFstrResdntCareNarrDto;
import us.tx.state.dfps.service.placement.service.ChildPlanPlacementService;
import us.tx.state.dfps.service.riskassessment.service.RiskAssessmentService;
import us.tx.state.dfps.service.stage.service.StageService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jul 25, 2017- 2:06:12 PM © 2017 Texas Department of
 * Family and Protective Services
 *  * **********  Change History *********************************
 * 02/05/2021 thompswa artf172715 saveActionForFbiFingerprint. 
 * 03/24/2024 thompswa artf257957 add insertCrpRequestStatus. 
 */
@RestController
@RequestMapping("/forms")
public class FormsController {

	private static final Logger log = LoggerFactory.getLogger(FormsController.class);
	@Autowired
	FormsService formsService;

	@Autowired
	private PostEventService postEventService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ChildPlanPlacementService childPlanPlacementService;

	@Autowired
	private RiskAssessmentService riskAssessmentService;

	@Autowired
	private CpsClosingSummaryService cpsClosingSummaryService;

	@Autowired
	private StageService stageService;

	/**
	 * This REST operation will be used to get the forms template
	 * 
	 * @param documentMetaData
	 * @return NewUsingDocumentValueBean @
	 */
	@RequestMapping(value = "/getdocumentBlob", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  NewUsingDocumentDto getDocumentBlob(@RequestBody DocumentMetaData documentMetaData) {
		return formsService.selectDocumentBlob(documentMetaData);
	}

	/**
	 * This REST operation will be used to get the Composite forms template
	 * 
	 * @param compositeFormsReq
	 * @return NewUsingDocumentValueBean @
	 */
	@RequestMapping(value = "/getCompositedoucmentBlob", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  NewUsingDocumentDto getCompositedoucmentBlob(
			@RequestBody CompositeFormsReq compositeFormsReq) {
		return formsService.selectCompositeDocumentBlob(compositeFormsReq);
	}

	/**
	 * This REST operation will be used to get the forms template information by
	 * passing the template id
	 * 
	 * @param formsReq
	 * @return DocumentTemplateDto @
	 */
	@RequestMapping(value = "/getTemplateInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DocumentTemplateDto getDocumentTemplateInfo(@RequestBody FormsReq formsReq) {

		return formsService.selectDocumentTemplateInfo(formsReq.getTemplateId());
	}
	
	
	/**
	 * This REST operation will be used to get the forms template information by
	 * passing the template id
	 * 
	 * @param formsReq
	 * @return DocumentTemplateDto @
	 */
	@RequestMapping(value = "/getAllTemplates", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DocumentTemplateListRes getAllTemplates() {
		DocumentTemplateListRes documentTemplateListRes = new DocumentTemplateListRes();
		documentTemplateListRes.setDocumentTemplateDtos(formsService.selectDocumentTemplate());		 
		return documentTemplateListRes;
	}

	/**
	 * This REST operation will be used to get the forms template information by
	 * passing the template type
	 * 
	 * @param formsReq
	 * @return DocumentTemplateDto @
	 */
	@RequestMapping(value = "/getLatestTemplateType", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  DocumentTemplateDto getLatestTemplateType(@RequestBody FormsReq formsReq) {
		return formsService.selectLatestTemplateType(formsReq.getTemplateType());
	}

	/**
	 * This REST operation will be used to save the forms template
	 * 
	 * @param documentMetaDataRform
	 * @param documentData
	 * @return DocumentMetaData @
	 */
	@RequestMapping(value = "/saveForms", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FormsServiceRes saveForms(@RequestBody FormsSaveReq formsSaveReq) {

		return formsService.saveForms(formsSaveReq.getDocumentMetaData(), formsSaveReq.getDocumentData());
	}

	/**
	 * This REST operation will be used to delete the forms template
	 * 
	 * @param documentMetaData
	 * @param documentData
	 * @return DocumentMetaData @
	 */
	@RequestMapping(value = "/deleteForms", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  void deleteForms(@RequestBody FormsSaveReq formsSaveReq) {

		formsService.deleteForms(formsSaveReq.getDocumentMetaData());
	}

	/**
	 * This REST operation will be used to retrieve time stamp for the given
	 * template
	 * 
	 * @param documentMetaData
	 * @param documentData
	 * @return DocumentMetaData @
	 */
	@RequestMapping(value = "/getTimeStamp", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FormsServiceRes getTimeStamp(@RequestBody FormsSaveReq formsSaveReq) {

		return formsService.getTimeStamp(formsSaveReq.getDocumentMetaData());
	}

	/**
	 * This REST operation will be used to save the forms template
	 * 
	 * @param documentMetaData
	 * @param documentData
	 * @return DocumentMetaData @
	 */
	@RequestMapping(value = "/saveIntakeForms", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DocumentMetaData saveIntakeReport(@RequestBody FormsSaveReq formsSaveReq) {

		return formsService.saveIntakeReport(formsSaveReq.getDocumentMetaData(), formsSaveReq.getDocumentData(),
				formsSaveReq.getCaseId());
	}

	/**
	 * This REST operation will be used to save the forms template
	 * 
	 * @param documentMetaData
	 * @param documentData
	 * @return DocumentMetaData @
	 */
	@RequestMapping(value = "/saveFormEvent", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  DocumentMetaData saveFormEvent(@RequestBody FormsSaveReq formsSaveReq) {

		return formsService.saveFormEvent(formsSaveReq.getDocumentMetaData(), formsSaveReq.getDocumentData());
	}

	/**
	 * This REST operation will be used to record the display of a form
	 * 
	 * @param formsSaveReq
	 * @return @
	 */
	@RequestMapping(value = "/getDocument", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FormsDocumentRes getDocument(@RequestBody FormsSaveReq formsSaveReq) {

		return formsService.selectDocument(formsSaveReq.getDocumentMetaData());
	}

	/**
	 * 
	 * @param formsSaveReq
	 * @return @
	 */
	@RequestMapping(value = "/getNewTemplate", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FormsServiceRes getNewTemplate(@RequestBody FormsSaveReq formsSaveReq) {

		return formsService.selectNewTemplate(formsSaveReq.getDocumentMetaData());
	}

	/**
	 * As discussed, the method below is not accessed from the application and
	 * does not have permissions to be used from the Application, hence
	 * commenting it out, if required in future.
	 * 
	 * @param formsSaveReq
	 * @return @
	 */
	/*
	 * @RequestMapping(value = "/getFBADocuments", headers = {
	 * "Accept=application/json" }, method = RequestMethod.POST)
	 * public  FormsDocumentRes getFBADocuments(@RequestBody
	 * FormsSaveReq formsSaveReq) {
	 * 
	 * return
	 * formsService.selectFBADocuments(formsSaveReq.getDocumentMetaData()); }
	 */

	@RequestMapping(value = "/createevent", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FormsServiceRes createEvent(@RequestBody FormsReq formsReq) {
		FormsServiceRes formsServiceRes = new FormsServiceRes();
		formsServiceRes.setPostEventOPDto(
				postEventService.checkPostEventStatus(formsReq.getPostEventIPDto(), formsReq.getServiceReqHeaderDto()));
		return formsServiceRes;
	}

	/**
	 * Method Description: This method will save the Record check notification
	 * details by passing idRecordsCheckNotif as input
	 * 
	 * @param formsReq
	 * @return FormsServiceRes @
	 */
	@RequestMapping(value = "/saveRecordsCheckNotif", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FormsServiceRes saveRecordsCheckNotification(@RequestBody FormsReq formsReq) {

		return formsService.saveRecordsCheckNotification(formsReq.getIdRecordsCheckNotif(), formsReq.getIdUser());
	}

	/**
	 * Method Description: This method will save the records check manual
	 * input indicator and records check date completed. artf172715
	 * 
	 * @param formsReq
	 * @return FormsServiceRes @
	 */
	@RequestMapping(value = "/savefbiaction", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FormsServiceRes saveActionForFbiFingerprint(@RequestBody FormsReq formsReq) {

		return formsService.saveActionForFbiFingerprint(formsReq.getIdCrimHist(), formsReq.getIdUser());
	}

	/**
	 * Method Description: his method gets the primary key for tableMetaData.
	 * 
	 * @param tableName
	 * @return String
	 */
	@RequestMapping(value = "/getDatabaseMetaDataForPk", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FormsServiceRes getDatabaseMetaDataForPk(@RequestBody FormsReq formsReq) {
		return formsService.getDatabaseMetaDataForPk(formsReq.getTableName());
	}

	/**
	 * Method Description: This method will fetch the Record check notification
	 * details by passing idRecordsCheckNotif as input
	 * 
	 * @param formsReq
	 * @return FormsServiceRes @
	 */
	@RequestMapping(value = "/getRecordsCheckNotification", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FormsServiceRes getRecordsCheckNotification(@RequestBody FormsReq formsReq) {

		return formsService.getRecordsCheckNotification(formsReq.getIdRecordsCheckNotif());
	}

	/**
	 * Method Description: This method will update the Record check notification
	 * details.
	 * 
	 * @param formsReq
	 * @return FormsServiceRes @
	 */
	@RequestMapping(value = "/updateRecordsCheckNotfcn", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FormsServiceRes updateRecordsCheckNotfcn(@RequestBody FormsReq formsReq) {

		return formsService.updateRecordsCheckNotfcn(formsReq.getRecordsCheckNotifDto());
	}

	/**
	 * Method Description: This method will create the new Record check
	 * notification details.
	 * 
	 * @param formsReq
	 * @return FormsServiceRes @
	 */
	@RequestMapping(value = "/insertRecordsCheckNotfcn", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FormsServiceRes insertRecordsCheckNotfcn(@RequestBody FormsReq formsReq) {

		return formsService.insertRecordsCheckNotfcn(formsReq.getRecordsCheckNotifDto());
	}

	/**
	 * 
	 * Method Name: getChildPlanPlacement - to placementInfo form Method
	 * Description: To retrieve the Child's Plan placement header information
	 * CSUB24S
	 * 
	 * @param commonApplicationReq
	 * @return childPlanPlacementDto
	 */
	@RequestMapping(value = "/getChildPlanPlacementData", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getChildPlanPlacement(@RequestBody CommonApplicationReq commonApplicationReq) {
		if (ObjectUtils.isEmpty(commonApplicationReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(commonApplicationReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(childPlanPlacementService
				.getChildPlanPlacement(commonApplicationReq.getIdEvent(), commonApplicationReq.getIdStage())));
		return commonFormRes;
	}

	/**
	 * 
	 * Method Name: getPpmData -Notice of Permanency Planning Meeting-Spanish
	 * form CSC36O00 Method Description: This method used to validate the
	 * incoming request and retrieving the Participant data
	 * 
	 * @param ppmReq
	 *            CSUB76S
	 * @return
	 */
	/*
	 * @RequestMapping(value = "/getPpmData", headers = {
	 * "Accept=application/json" }, method = RequestMethod.POST)
	 * 
	 *  public PpmDto getPpmData(@RequestBody PpmReq ppmReq) {
	 * 
	 * if (TypeConvUtil.isNullOrEmpty(ppmReq.getIdPptEvent())) { throw new
	 * InvalidRequestException(messageSource.getMessage(
	 * "ppm.pptEvent.mandatory", null, Locale.US)); } if
	 * (TypeConvUtil.isNullOrEmpty(ppmReq.getIdStage())) { throw new
	 * InvalidRequestException(messageSource.getMessage("ppm.stageId.mandatory",
	 * null, Locale.US)); } if
	 * (TypeConvUtil.isNullOrEmpty(ppmReq.getIdPptPart())) { throw new
	 * InvalidRequestException(messageSource.getMessage("ppm.ptPart.mandatory",
	 * null, Locale.US)); } PpmDto ppmDto = null;//ppmService.getPpm(ppmReq);
	 * return ppmDto; }
	 */

	/**
	 * 
	 * Method Name: getCpsClosingSummaryData Method Description: To retrieve the
	 * CPS Closing Summary form information CSVC22S
	 * 
	 * @param commonApplicationReq
	 * @return CommonFormRes
	 */
	@RequestMapping(value = "/getCpsClosingSummary", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getCpsClosingSummaryData(@RequestBody CommonApplicationReq commonApplicationReq) {
		CommonFormRes commonFormRes = new CommonFormRes();

		if (ObjectUtils.isEmpty(commonApplicationReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		commonFormRes.setPreFillData(TypeConvUtil
				.getXMLFormat(cpsClosingSummaryService.getCpsClosingSummaryData(commonApplicationReq.getIdStage())));

		return commonFormRes;
	}

	/**
	 * 
	 * Method Name: getRiskAssmntData Method Description: To retrieve the Risk
	 * Assessment information CINV77S
	 * 
	 * @param commonApplicationReq
	 * @return riskAssessmentNarrativeDto
	 */
	@RequestMapping(value = "/getRiskAssmntData", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getRiskAssmntData(@RequestBody CommonApplicationReq commonApplicationReq) {
		if (ObjectUtils.isEmpty(commonApplicationReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(riskAssessmentService.getRiskAssmntData(commonApplicationReq.getIdStage())));
		return commonFormRes;
	}

	/**
	 * 
	 * Method Name: getRiskAssmntInvData Method Description: To retrieve the
	 * Risk Assessment for Ruled-Out Investigation information CINV84S
	 * 
	 * @param commonApplicationReq
	 * @return riskAssessmentNarrativeDto
	 */
	@RequestMapping(value = "/getRiskAssmntInvData", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getRiskAssmntInvData(@RequestBody CommonApplicationReq commonApplicationReq) {
		if (ObjectUtils.isEmpty(commonApplicationReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil
				.getXMLFormat(riskAssessmentService.getRiskAssmntInvData(commonApplicationReq.getIdStage())));
		return commonFormRes;
	}

	/**
	 * 
	 * Method Name: getStageAndCaseDtls Method Description: To retrieve the Risk
	 * Assessment and Spanish Family Service Plan Parent-Child Contact and
	 * Financial Support (Visitation Plan)information CSUB61S & CSUB75S
	 * 
	 * @param commonApplicationReq
	 * @return stageCaseDtlDto
	 */
	@RequestMapping(value = "/getStageAndCaseDtls", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getStageAndCaseDtls(@RequestBody CommonApplicationReq commonApplicationReq) {
		if (ObjectUtils.isEmpty(commonApplicationReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(stageService.getStageAndCaseDtls(commonApplicationReq.getIdStage())));
		return commonFormRes;
	}

	/**
	 * 
	 * Method Name: saveFormLog Method Description:Service used to save the
	 * launched forms into the document logger
	 * 
	 * @param docLogReq
	 * @return CommonFormRes
	 */
	@RequestMapping(value = "/saveDocLog", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes saveFormLog(@RequestBody DocLogReq docLogReq) {
		return formsService.saveDocumentLog(docLogReq.getDocumentLogDto());
	}

	/**
	 * 
	 * Method Name: completePlcmntFstrResdntCareNarr Method Description: Update
	 * PlcmntFstrResdntCareNarr When User clicks save and submit button.
	 * 
	 * @param plcmntFormReq
	 */
	@RequestMapping(value = "/completePlcmntFstrResdntCareNarr", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes completePlcmntFstrResdntCareNarr(@RequestBody PlcmntFormReq plcmntFormReq) {
		//Warranty Defect#11830 - Added new parameter to pass the user id to update in the narrative table
		Boolean updateSuccess = formsService
						.completePlcmntFstrResdntCareNarr(plcmntFormReq.getPlcmntFstrResdntCareNarrDto(),plcmntFormReq.getUserId());
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setUpdateSuccess(updateSuccess);
		return commonFormRes;
	}

	/**
	 * 
	 * Method Name: getPlcmntFstrResdntCareNarr Method Description: Get
	 * PlcmntFstrResdntCareNarr
	 * 
	 * @param commonApplicationReq
	 * @return CommonFormRes
	 */
	@RequestMapping(value = "/getPlcmntFstrResdntCareNarr", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getPlcmntFstrResdntCareNarr(@RequestBody CommonApplicationReq commonApplicationReq) {
		List<PlcmntFstrResdntCareNarrDto> plcmntFstrResdntCareNarrDtoList = formsService
				.getPlcmntFstrResdntCareNarr(commonApplicationReq.getIdEvent());
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPlcmntFstrResdntCareNarrDtoList(plcmntFstrResdntCareNarrDtoList);
		return commonFormRes;
	}

	/**
	 * 
	 * Method Name: deletePlcmntFstrResdntCareNarr Method Description: Delete
	 * PlcmntFstrResdntCareNarr When User clicks delete button.
	 * 
	 * @param plcmntFormReq
	 */
	@RequestMapping(value = "/deletePlcmntFstrResdntCareNarr", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes deletePlcmntFstrResdntCareNarr(@RequestBody PlcmntFormReq plcmntFormReq) {
		Boolean updateSuccess = formsService.deletePlcmntFstrResdntCareNarr(
				plcmntFormReq.getPlcmntFstrResdntCareNarrDto().getIdEvent(),
				plcmntFormReq.getPlcmntFstrResdntCareNarrDto().getIdDocumentTemplate());
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setUpdateSuccess(updateSuccess);
		return commonFormRes;
	}

	/**
	 * 
	 * Method Name: checkdocumentTemplate Method Description: This Method is
	 * used to check the document template is Legacy or Impact Phase 2 template
	 * or not.
	 * 
	 * @param formsReq
	 * @param DocumentTemplateDto
	 */
	@RequestMapping(value = "/isLegacyTemplate", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public DocumentTemplateDto checkdocumentTemplate(@RequestBody FormsReq formsReq) {
		if (TypeConvUtil.isNullOrEmpty(formsReq.getTableName()))
			throw new InvalidRequestException(messageSource.getMessage("narrative.table.name", null, Locale.US));
		if (TypeConvUtil.isNullOrEmpty(formsReq.getCommonHelperReq().getIdEvent()))
			throw new InvalidRequestException(messageSource.getMessage("narrative.eventId", null, Locale.US));
		DocumentTmpltCheckDto documentTmpltCheckDto = new DocumentTmpltCheckDto();
		documentTmpltCheckDto.setNarrEventId(formsReq.getCommonHelperReq().getIdEvent());
		documentTmpltCheckDto.setTableName(formsReq.getTableName());
		//removed the formsService call as this endpoint is not called
		log.info("Is Legacy Template has been removed. Please contact the CSC");
		DocumentTemplateDto documentTemplateDto = new DocumentTemplateDto();
		documentTemplateDto.setIdTemplate(0L);
		documentTemplateDto.setIndLgcy("N");
		return documentTemplateDto;
	}


	/**
	 * Method Description: This method will fetch the crp record check notification
	 * details for action Classes by passing idRecordsCheckNotif as input
	 *
	 * @param formsReq
	 * @return FormsServiceRes @
	 */
	@RequestMapping(value = "/getCrpNotification", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FormsServiceRes getCrpNotification(@RequestBody FormsReq formsReq) {

		return formsService.getCrpRecordNotification(formsReq.getIdCrpRecordNotif());
	}

	/**
	 * Method Description: This method will update the crp record check notification
	 * details for action Classes.
	 *
	 * @param formsReq
	 * @return FormsServiceRes @
	 */
	@RequestMapping(value = "/savecrpaction", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FormsServiceRes updateCrpNotification(@RequestBody FormsReq formsReq) {

		return formsService.updateCrpRecordNotfcn(formsReq);
	}

	/**
	 * Method Description: This method will create the new crp record check
	 * notification details for action Classes.
	 *
	 * @param formsReq
	 * @return FormsServiceRes @
	 */
	@RequestMapping(value = "/insertCrpNotification", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FormsServiceRes insertCrpNotification(@RequestBody FormsReq formsReq) {

		return formsService.insertCrpNotification(formsReq.getCrpRecordNotifDto());
	}

	/**
	 * Method Description: This method will create the new crp request status
	 * in @impactp for crp email action Class.
	 *
	 * @return FormsServiceRes @
	 */
	@RequestMapping(value = "/insertCrpRequestStatus", headers = {"Accept=application/json"}, method = RequestMethod.POST)
	public FormsServiceRes insertCrpRequestStatus(@RequestBody FormsReq formsReq) {

		return formsService.insertCrpRequestStatus(formsReq.getCrpRequestStatusDto());
	}


}
