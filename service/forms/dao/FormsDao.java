package us.tx.state.dfps.service.forms.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.forms.dto.DocumentLogDto;
import us.tx.state.dfps.service.forms.dto.DocumentMetaData;
import us.tx.state.dfps.service.forms.dto.DocumentTemplateDto;
import us.tx.state.dfps.service.forms.dto.DocumentTmpltCheckDto;
import us.tx.state.dfps.service.forms.dto.NewUsingDocumentDto;
import us.tx.state.dfps.service.placement.dto.PlcmntFstrResdntCareNarrDto;

public interface FormsDao {

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
	 * @param blobId
	 * @param tableName
	 * @param colNarrColumn
	 * @param colIdentColumn
	 * @return
	 */
	public NewUsingDocumentDto selectCompositeDocumentBlob(Long blobId, String tableName, String colNarrColumn,
			String colIdentColumn);

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
	public int saveForms(DocumentMetaData documentMetaData, byte[] documentData);

	/**
	 * 
	 * Method Name: updateForms Method Description:Method used to update the
	 * existing forms information
	 * 
	 * @param documentMetaData
	 * @param documentData
	 * @return
	 */
	public int updateForms(DocumentMetaData documentMetaData, byte[] documentData);

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
	public Date getTimeStamp(DocumentMetaData documentMetaData);

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
	public DocumentLogDto selectDocument(DocumentMetaData documentMetaData);

	/**
	 * 
	 * Method Name: selectNewTemplate Method Description:Method used to retrieve
	 * the new template information
	 * 
	 * @param documentMetaData
	 * @return
	 */
	public DocumentMetaData selectNewTemplate(DocumentMetaData documentMetaData);

	/**
	 * 
	 * Method Name: getDatabaseMetaDataForPk Method Description:This method gets
	 * the primary key for tableMetaData.
	 * 
	 * @param tableName
	 * @return
	 */
	public String getDatabaseMetaDataForPk(String tableName);

	/**
	 * 
	 * Method Name: saveDocumentLog Method Description:Method used to save the
	 * form details into document logger
	 * 
	 * @param documentLogDto
	 * @return String
	 */
	public String saveDocumentLog(DocumentLogDto documentLogDto);

	/**
	 * 
	 * Method Name: completePlcmntFstrResdntCareNarr Method Description: Update
	 * PlcmntFstrResdntCareNarr When User clicks save and submit button.
	 * 
	 * @param PlcmntFstrResdntCareNarrDto
	 *            plcmntFstrResdntCareNarrDto
	 */
	public Boolean completePlcmntFstrResdntCareNarr(PlcmntFstrResdntCareNarrDto plcmntFstrResdntCareNarrDto,Long idPerson);

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
	 * Method Name: deleteHandwrittenDataByIdEvent Method Description: Delete
	 * Handwritten Data by ID Event
	 * 
	 * @param idEvent
	 *
	 */
	public Boolean deleteHandwrittenDataByIdEvent(Long idEvent);

	/**
	 * 
	 * Method Name: documentTemplateCheck. Method Description: This Method is
	 * used to check the document template is Legacy or Impact Phase 2 template.
	 * 
	 * @param documentTmpltCheckDto
	 * @param DocumentTemplateDto
	 */
	public DocumentTemplateDto documentTemplateCheck(DocumentTmpltCheckDto documentTmpltCheckDto);
	
	/**
	 * 
	 * Method Name: copyNarrativeDocForNewUsing. Method Description: This Method is
	 * used to copy the existing narrative document to new event.
	 * 
	 * @param idNewEvent
	 * @param idPrevEvent
	 * @param tableName
	 */
	public void copyNarrativeDocForNewUsing(Long idNewEvent, Long idPrevEvent, String tableName);
	
	/**
	 * 
	 * Method Name: selectDocumentTemplate
	 * Method Description:Method used to retrieve all the Modernized template information
	 * 
	 * @return List<DocumentTemplateDto>
	 */
	public List<DocumentTemplateDto> selectDocumentTemplate();
}
