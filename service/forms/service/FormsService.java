package us.tx.state.dfps.service.forms.service;

import java.sql.SQLException;
import java.util.List;

import us.tx.state.dfps.service.common.request.CompositeFormsReq;
import us.tx.state.dfps.service.common.request.FormsReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.FormsDocumentRes;
import us.tx.state.dfps.service.common.response.FormsServiceRes;
import us.tx.state.dfps.service.forms.dto.DocumentLogDto;
import us.tx.state.dfps.service.forms.dto.DocumentMetaData;
import us.tx.state.dfps.service.forms.dto.DocumentTemplateDto;
import us.tx.state.dfps.service.forms.dto.DocumentTmpltCheckDto;
import us.tx.state.dfps.service.forms.dto.NewUsingDocumentDto;
import us.tx.state.dfps.service.forms.dto.RecordsCheckNotifDto;
import us.tx.state.dfps.service.person.dto.CrpRecordNotifDto;
import us.tx.state.dfps.service.person.dto.CrpRequestStatusDto;
import us.tx.state.dfps.service.placement.dto.PlcmntFstrResdntCareNarrDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Forms
 * Service Interface Aug 31, 2018- 1:17:24 PM © 2017 Texas Department of Family
 * and Protective Services
 *  * **********  Change History *********************************
 * 02/05/2021 thompswa artf172715 saveActionForFbiFingerprint. 
 * 03/24/2024 thompswa artf257957  insertCrpRequestStatus. 
 */
public interface FormsService {

	/**
	 * 
	 * Method Name: selectDocumentBlob Method Description:Method used to select
	 * the document narrative blob
	 * 
	 * @param documentMetaData
	 * @return
	 */
	public NewUsingDocumentDto selectDocumentBlob(DocumentMetaData documentMetaData);

	/**
	 * 
	 * Method Name: selectCompositeDocumentBlob Method Description:Method used
	 * to select the composite document narrative blob
	 * 
	 * @param compositeFormsReq
	 * @return
	 */
	public NewUsingDocumentDto selectCompositeDocumentBlob(CompositeFormsReq compositeFormsReq);

	/**
	 * 
	 * Method Name: selectDocumentTemplateInfo Method Description:Method used to
	 * retrieve the template information
	 * 
	 * @param templateID
	 * @return
	 */
	public DocumentTemplateDto selectDocumentTemplateInfo(Long templateID);

	/**
	 * 
	 * Method Name: selectLatestTemplateType Method Description:Method used to
	 * retrieve the latest template information
	 * 
	 * @param templateType
	 * @return
	 */
	public DocumentTemplateDto selectLatestTemplateType(String templateType);

	/**
	 * 
	 * Method Name: saveForms Method Description:Method used to save the forms
	 * information
	 * 
	 * @param documentMetaData
	 * @param documentData
	 * @return
	 */
	public FormsServiceRes saveForms(DocumentMetaData documentMetaData, byte[] documentData);

	/**
	 * 
	 * Method Name: deleteForms Method Description:Method used to delete the
	 * forms information
	 * 
	 * @param documentMetaData
	 * @return
	 */
	public String deleteForms(DocumentMetaData documentMetaData);

	/**
	 * 
	 * Method Name: getTimeStamp Method Description:Method used to retrieve the
	 * time stamp for the required forms
	 * 
	 * @param documentMetaData
	 * @return
	 */
	public FormsServiceRes getTimeStamp(DocumentMetaData documentMetaData);

	/**
	 * 
	 * Method Name: saveIntakeReport Method Description:Method used to save
	 * intake forms information
	 * 
	 * @param documentMetaData
	 * @param documentData
	 * @param caseId
	 * @return
	 */
	public DocumentMetaData saveIntakeReport(DocumentMetaData documentMetaData, byte[] documentData, Long caseId);

	/**
	 * 
	 * Method Name: saveFormEvent Method Description:Method used to save the
	 * forms event information
	 * 
	 * @param documentMetaData
	 * @param documentData
	 * @return
	 */
	public DocumentMetaData saveFormEvent(DocumentMetaData documentMetaData, byte[] documentData);

	/**
	 * 
	 * Method Name: selectDocument Method Description:Method used to retrieve
	 * the document information
	 * 
	 * @param documentMetaData
	 * @return
	 */
	public FormsDocumentRes selectDocument(DocumentMetaData documentMetaData);

	/**
	 * 
	 * Method Name: selectNewTemplate Method Description:Method used to retrieve
	 * the new template information
	 * 
	 * @param documentMetaData
	 * @return
	 */
	public FormsServiceRes selectNewTemplate(DocumentMetaData documentMetaData);

	/**
	 * 
	 * Method Name: fetchBookmarks Method Description:Method used to retrieve
	 * the bookmark details
	 * 
	 * @param formName
	 * @param className
	 * @param groupClassName
	 * @return
	 */
	public Object fetchBookmarks(String formName, String className, String groupClassName);

	/**
	 * Method Name: saveRecordsCheckNotification Method Description: This method
	 * will update the Record check notification details by passing
	 * idRecordsCheckNotif as input
	 * 
	 * @param idRecordsCheckNotif
	 * @param userId
	 * @return FormsServiceRes @
	 */
	public FormsServiceRes saveRecordsCheckNotification(Long idRecordsCheckNotif, Long userId);

	/**
	 * Method Name: saveActionForFbiFingerprint Method Description: This method
	 * will update the CriminalHistory indManualSave and RecordsCheck dtRecCheckCompleted
	 * idCrimHist as input
	 * 
	 * @param idCrimHist
	 * @param userId
	 * @return FormsServiceRes @
	 */
	public FormsServiceRes saveActionForFbiFingerprint(Long idCrimHist, Long userId);

	/**
	 * Method Name: getDatabaseMetaDataForPk Method Description: This method
	 * gets the primary key for tableMetaData.
	 * 
	 * @param tableName
	 * @return String
	 * @ @throws
	 *       SQLException
	 */
	public FormsServiceRes getDatabaseMetaDataForPk(String tableName);

	/**
	 * Method Name: getRecordsCheckNotification Method Description: This method
	 * will fetch the Record check notification details by passing
	 * idRecordsCheckNotif as input
	 * 
	 * @param idRecordsCheckNotif
	 * @return FormsServiceRes @
	 */
	public FormsServiceRes getRecordsCheckNotification(Long idRecordsCheckNotif);

	/**
	 * Method Name: updateRecordsCheckNotfcn Method Description: This method
	 * will update the Record check notification details
	 * 
	 * @param recordsCheckNotifDto
	 * @return FormsServiceRes @
	 */
	public FormsServiceRes updateRecordsCheckNotfcn(RecordsCheckNotifDto recordsCheckNotifDto);

	/**
	 * Method Name: insertRecordsCheckNotfcn Method Description: This method
	 * will create the new Record check notification details
	 * 
	 * @param recordsCheckNotifDto
	 * @return FormsServiceRes @
	 */
	public FormsServiceRes insertRecordsCheckNotfcn(RecordsCheckNotifDto recordsCheckNotifDto);

	/**
	 * 
	 * Method Name: saveDocumentLog Method Description:Method used to save the
	 * form details into document logger
	 * 
	 * @param documentLogDto
	 * @return CommonFormRes
	 */
	public CommonFormRes saveDocumentLog(DocumentLogDto documentLogDto);

	/**
	 * 
	 * Method Name: completePlcmntFstrResdntCareNarr Method Description: Update
	 * PlcmntFstrResdntCareNarr When User clicks save and submit button.
	 * 
	 * @param PlcmntFstrResdntCareNarrDto
	 *            plcmntFstrResdntCareNarrDto
	 */
	public Boolean completePlcmntFstrResdntCareNarr(PlcmntFstrResdntCareNarrDto plcmntFstrResdntCareNarrDto,String userId);

	/**
	 * 
	 * Method Name: getPlcmntFstrResdntCareNarr Method Description: Get
	 * PlcmntFstrResdntCareNarr
	 * 
	 * @param idEvent
	 * @return List<PlcmntFstrResdntCareNarrDto>
	 */
	public List<PlcmntFstrResdntCareNarrDto> getPlcmntFstrResdntCareNarr(Long idEvent);

	/**
	 * 
	 * Method Name: deletePlcmntFstrResdntCareNarr Method Description: Delete
	 * PlcmntFstrResdntCareNarr Record
	 * 
	 * @param idEvent
	 * @param idDocumentTemplate
	 */
	public Boolean deletePlcmntFstrResdntCareNarr(Long idEvent, Long idDocumentTemplate);

	/**
	 * 
	 * Method Name: documentTemplateCheck. Method Description: This Method is
	 * used to check the document template is Legacy or Impact Phase 2 template.
	 * 
	 * @param documentTmpltCheckDto
	 * @param DocumentTemplateDto
	 */
	public DocumentTemplateDto documentTemplateCheck(DocumentTmpltCheckDto documentTmpltCheckDto);

	public List<DocumentTemplateDto> selectDocumentTemplate();

	/**
	 * Method Name: getCrpRecordNotification Method Description: This method
	 * will fetch the crp record check notification details by passing
	 * idRecordsCheckNotif as input
	 *
	 * @param idCrpRecordNotif
	 * @return FormsServiceRes @
	 */
	public FormsServiceRes getCrpRecordNotification(Long idCrpRecordNotif);

	/**
	 * Method Name: updateCrpRecordNotfcn Method Description: This method
	 * will update the crp record check notification details
	 *
	 * @param formsReq
	 * @return FormsServiceRes @
	 */
	public FormsServiceRes updateCrpRecordNotfcn(FormsReq formsReq);

	/**
	 * Method Name: insertCrpNotification Method Description: This method
	 * will create the new crp record notification details
	 *
	 * @param crpRecordNotifDto
	 * @return FormsServiceRes @
	 */
	public FormsServiceRes insertCrpNotification(CrpRecordNotifDto crpRecordNotifDto);

	/**
	 * Method Description: This method is used to provide the
	 * CRP Record Notif Form PDF. This pdf constitutes the body of an email,
	 * that provides notification of results of the run of background checks
	 * as per the Public Central Registry Portal in 2024
	 *
	 * @param crpRequestStatusDto
	 * @return FormsServiceRes @
	 */
	public FormsServiceRes insertCrpRequestStatus(CrpRequestStatusDto crpRequestStatusDto);



}
