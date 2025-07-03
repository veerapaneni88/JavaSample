package us.tx.state.dfps.service.forms.serviceimpl;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CriminalHistory;
import us.tx.state.dfps.common.exception.FormsException;
import us.tx.state.dfps.common.exception.StageClosedException;
import us.tx.state.dfps.phoneticsearch.IIRHelper.Messages;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonOutDto;
import us.tx.state.dfps.service.admin.dto.CrimHistDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CompositeFormsReq;
import us.tx.state.dfps.service.common.request.CriminalHistoryUpdateReq;
import us.tx.state.dfps.service.common.request.FormsReq;
import us.tx.state.dfps.service.common.request.RecordsCheckDetailReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.FormsDocumentRes;
import us.tx.state.dfps.service.common.response.FormsServiceRes;
import us.tx.state.dfps.service.common.service.ApprovalService;
import us.tx.state.dfps.service.common.utils.FacilityAbuseInvUtil;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.forms.dao.BookmarkDao;
import us.tx.state.dfps.service.forms.dao.FormsDao;
import us.tx.state.dfps.service.forms.dto.ApsFacilNarrDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.Column;
import us.tx.state.dfps.service.forms.dto.DocumentLogDto;
import us.tx.state.dfps.service.forms.dto.DocumentMetaData;
import us.tx.state.dfps.service.forms.dto.DocumentTemplateDto;
import us.tx.state.dfps.service.forms.dto.DocumentTmpltCheckDto;
import us.tx.state.dfps.service.forms.dto.GroupBookmarkDto;
import us.tx.state.dfps.service.forms.dto.NewUsingDocumentDto;
import us.tx.state.dfps.service.forms.dto.RecordsCheckNotifDto;
import us.tx.state.dfps.service.forms.dto.TableFields;
import us.tx.state.dfps.service.forms.service.FormsService;
import us.tx.state.dfps.service.investigation.dao.FacilityAbuseInvReportDao;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.CriminalHistoryDao;
import us.tx.state.dfps.service.person.dao.CrpRecordNotifDao;
import us.tx.state.dfps.service.person.dao.NameDao;
import us.tx.state.dfps.service.person.dao.RecordsCheckDao;
import us.tx.state.dfps.service.person.dao.RecordsCheckNotifDao;
import us.tx.state.dfps.service.person.dto.CrimHistorySaveDto;
import us.tx.state.dfps.service.person.dto.CrpRecordNotifDto;
import us.tx.state.dfps.service.person.dto.CrpRequestStatusDto;
import us.tx.state.dfps.service.person.dto.RecordsCheckDetailDto;
import us.tx.state.dfps.service.person.serviceimpl.CrpRecordNotifServiceImpl;
import us.tx.state.dfps.service.placement.dto.PlcmntFstrResdntCareNarrDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jul 25, 2017- 2:06:12 PM © 2017 Texas Department of
 * Family and Protective Services
 *  * **********  Change History *********************************
 * 02/05/2021 thompswa artf172715 saveActionForFbiFingerprint.
 * 04/09/2021 nairl artf179420  Comment are not displaying when user saves it during manual copy in Record check detail page. 
 * 03/24/2024 thompswa artf257957  insertCrpRequestStatus. 
 */
@Service
@Transactional
public class FormsServiceImpl implements FormsService {

	private static final Logger logger = Logger.getLogger(FormsServiceImpl.class);

	
	@Autowired
	FormsDao formsDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	BookmarkDao bookmarkDao;

	@Autowired
	RecordsCheckNotifDao recordsCheckNotifDao;
	@Autowired
	CrpRecordNotifDao crpRecordNotifDao;
	
	@Autowired
	EventDao eventDao;
	
	@Autowired
	LookupDao lookupDao;
	
	@Autowired
	ApprovalService approvalService;
	
	@Autowired
	ApprovalCommonService approvalCommonService;
	
	@Autowired
	FacilityAbuseInvReportDao facilityAbuseInvReportDao;

	@Autowired
	CriminalHistoryDao criminalHistoryDao;

	@Autowired
	RecordsCheckDao recordsCheckDao;

	@Autowired
	NameDao nameDao;

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public NewUsingDocumentDto selectDocumentBlob(DocumentMetaData documentMetaData) {
		return formsDao.selectDocumentBlob(documentMetaData);
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public DocumentTemplateDto selectDocumentTemplateInfo(Long templateId) {
		return formsDao.selectDocumentTemplateInfo(templateId);
	}
	
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<DocumentTemplateDto> selectDocumentTemplate() {
		return formsDao.selectDocumentTemplate();
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public DocumentTemplateDto selectLatestTemplateType(String templateType) {
		return formsDao.selectLatestTemplateType(templateType);
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FormsServiceRes saveForms(DocumentMetaData documentMetaData, byte[] documentData) {
		ApsFacilNarrDto apsFacilNarrDto = null;
		
		if (0 != documentMetaData.getCheckStage()) {
			// Check to see if the stage is closed; an exception will be thrown
			// if it is closed.
			StageDto stageDto = stageDao.getStageById(Long.valueOf(documentMetaData.getCheckStage()));
			//Modified the code for warranty defect 11976
			if (!documentMetaData.getDocumentType().equalsIgnoreCase(ServiceConstants.CFIV1600)
					&& !facilityAbuseInvReportDao.getStageIdForDataFix(Long.valueOf(documentMetaData.getCheckStage()))
							.equals(Long.valueOf(documentMetaData.getCheckStage())))					
			{				
			if (ServiceConstants.STRING_IND_Y.equalsIgnoreCase(stageDto.getIndStageClose()))
				throw new StageClosedException("The document cannot be saved.The current stage has been closed.");
			}
		}
		verifyModifiableEvent(documentMetaData);
		FormsServiceRes response = new FormsServiceRes();
		//Modified the code for warranty defect 11976
		if (documentMetaData.getDocumentType().equalsIgnoreCase(ServiceConstants.CFIV1600)
				&& facilityAbuseInvReportDao.getStageIdForDataFix(Long.valueOf(documentMetaData.getCheckStage()))
						.equals(Long.valueOf(documentMetaData.getCheckStage())))
		{
			documentMetaData.setDocumentExists(ServiceConstants.TRUE_VALUE);	
		}
		if("CONTACT_NARRATIVE".equals(documentMetaData.getTableMetaData().getTableName())
				&& !ObjectUtils.isEmpty(documentMetaData.getUserId())){
			Column lastUpdatedBy = new Column();
			lastUpdatedBy.setContent(documentMetaData.getUserId());
			lastUpdatedBy.setName("id_last_update_person");
			lastUpdatedBy.setRequestName("slastUpdatedBy");
			lastUpdatedBy.setType("NUMBER");
			documentMetaData.getTableMetaData().getTableFields().getColumn().add(lastUpdatedBy);
			documentMetaData.getTableMetaData().setLastUpdatedBy(lastUpdatedBy);
			if(!documentMetaData.isDocumentExists()) {
				Column createdBy = new Column();
				createdBy.setContent(documentMetaData.getUserId());
				createdBy.setName("id_created_person");
				createdBy.setRequestName("sCreatedBy");
				createdBy.setType("NUMBER");
				documentMetaData.getTableMetaData().getTableFields().getColumn().add(createdBy);
			}
		}
		int recordNum = documentMetaData.isDocumentExists() ? formsDao.updateForms(documentMetaData, documentData)
				: formsDao.saveForms(documentMetaData, documentData);

		Date dtLastUpdate = formsDao.getTimeStamp(documentMetaData);
		
		// SD 56377: R2 Sev 5 Defect 10107	
		// Replace the REPT_TXT_EVIDENCE_LIST_2 Tag value with TXT_BLANK_NARRATIVE tag value.
		if (documentMetaData.getDocumentType().equalsIgnoreCase(ServiceConstants.EEVL)){
			try {
				apsFacilNarrDto = facilityAbuseInvReportDao.getApsFacilNarr(Long.valueOf(documentMetaData.getCheckStage()));
				if(!ObjectUtils.isEmpty(apsFacilNarrDto) && apsFacilNarrDto.getDocumentBlob() != null) {
					byte[] apsDocumentData = apsFacilNarrDto.getNarrativeBytes();
					String eevlFieldValue  = FacilityAbuseInvUtil.getFieldValue(documentData, ServiceConstants.TXT_BLANK_NARRATIVE);
					Document blobDocument  = FacilityAbuseInvUtil.updateFieldValue(apsDocumentData, ServiceConstants.REPT_TXT_EVIDENCE_LIST_2, eevlFieldValue);
					byte[]  facilityAbuseDocument = FacilityAbuseInvUtil.getByteArray(blobDocument.toString(), 0);
					facilityAbuseInvReportDao.updateApsFacilNarr(facilityAbuseDocument, Long.valueOf(documentMetaData.getCheckStage()), dtLastUpdate);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		SimpleDateFormat dateformat = null;
		if (!ObjectUtils.isEmpty(dtLastUpdate)) {
			for (int x = 0; x < documentMetaData.getTableMetaData().getTableFields().getColumn().size(); x++) {
				Column column = documentMetaData.getTableMetaData().getTableFields().getColumn().get(x);
				String tempName = column.getName().toUpperCase();
				if (tempName.equals("DT_LAST_UPDATE")) {
					dateformat = new SimpleDateFormat("M/d/yyyy H:m:s");
					String dateString = dateformat.format(dtLastUpdate);
					column.setContent(dateString);
				}
			}
			documentMetaData.setDocumentExists(Boolean.TRUE);
		}
		response.setDocumentMetaData(documentMetaData);
		return response;
	}
	
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public NewUsingDocumentDto selectCompositeDocumentBlob(CompositeFormsReq compositeFormsReq) {
		return formsDao.selectCompositeDocumentBlob(compositeFormsReq.getBlobId(), compositeFormsReq.getTableName(),
				compositeFormsReq.getColNarrColumn(), compositeFormsReq.getColIdentColumn());
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public String deleteForms(DocumentMetaData documentMetaData) {

		StageDto stageDto = stageDao.getStageById(Long.valueOf(documentMetaData.getCheckStage()));
		if (ServiceConstants.STRING_IND_Y.equalsIgnoreCase(stageDto.getIndStageClose()))
			throw new ServiceLayerException("The document cannot be saved.  The current stage has been closed.");

		return formsDao.deleteForms(documentMetaData);
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FormsServiceRes getTimeStamp(DocumentMetaData documentMetaData) {
		FormsServiceRes formsServiceRes = new FormsServiceRes();
		formsServiceRes.setTimeStamp(formsDao.getTimeStamp(documentMetaData));
		return formsServiceRes;
	}

	@Override
	public DocumentMetaData saveIntakeReport(DocumentMetaData documentMetaData, byte[] documentData, Long caseId) {
		return null;
	}

	@Override
	public DocumentMetaData saveFormEvent(DocumentMetaData documentMetaData, byte[] documentData) {
		return null;
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FormsDocumentRes selectDocument(DocumentMetaData documentMetaData) {
		DocumentLogDto doc = formsDao.selectDocument(documentMetaData);
		FormsDocumentRes res = new FormsDocumentRes();
		res.setDocumentLogDto(doc);
		return res;
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FormsServiceRes selectNewTemplate(DocumentMetaData documentMetaData) {
		DocumentMetaData docMetaData = formsDao.selectNewTemplate(documentMetaData);
		FormsServiceRes res = new FormsServiceRes();
		res.setDocumentMetaData(docMetaData);
		return res;
	}

	/**
	 * This method below is not accessed from the application and does not have
	 * permissions to be used from the Application, hence commenting it out, if
	 * required in future.
	 */
	/*
	 * @Override public FormsDocumentRes selectFBADocuments(DocumentMetaData
	 * documentMetaData) { FormsDocumentRes formsDocumentRes = new
	 * FormsDocumentRes(); ArrayList<DocumentEventDto> docEventdtoList =
	 * formsDao.selectFBADocuments(documentMetaData);
	 * 
	 * formsDocumentRes.setDocEventdtoList(docEventdtoList); return
	 * formsDocumentRes; }
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Object fetchBookmarks(String formName, String className, String groupClassName) {
		List<BookmarkDto> formfieldsBKList = bookmarkDao.selectFormFieldsBookmarks(formName);
		List<GroupBookmarkDto> groupsBKList = bookmarkDao.selectGroupBookmarks(formName);
		Object response = null;
		try {
			Class<?> dtoClass = Class.forName(className);
			Object dtoObj = dtoClass.newInstance();
			Class<?> groupClass = Class.forName(groupClassName);
			Object groupDtoObj = groupClass.newInstance();
			/* iteration of form fields bookmarks */
			if (!ObjectUtils.isEmpty(formfieldsBKList)) {
				formfieldsBKList.stream().forEach(dto -> {
					try {
						BeanUtils.setProperty(dtoObj, formatBookmarName(dto.getBookmarkName()), dto.getBookmarkName());
					} catch (IllegalAccessException | InvocationTargetException ex) {
						throw new ServiceLayerException(FormConstants.PROP_NOT_FOUND);
					}

				});
			}
			/* iteration of group bookmarks */
			if (!ObjectUtils.isEmpty(groupsBKList)) {
				groupsBKList.stream().forEach(dto -> {
					try {
						BeanUtils.setProperty(groupDtoObj, formatBookmarName(dto.getBookmarkName()),
								dto.getBookmarkName());
					} catch (IllegalAccessException | InvocationTargetException ex) {
						throw new ServiceLayerException(FormConstants.PROP_NOT_FOUND);
					}

				});
			}
			String[] groupNameArray = groupClassName.split(FormConstants.SPLIT_DOT);
			BeanUtils.setProperty(dtoObj, StringUtils.uncapitalize(groupNameArray[groupNameArray.length - 1]),
					groupDtoObj);
			response = dtoObj;
		} catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException
				| InstantiationException ex) {
			ServiceLayerException serviceLayerException = new ServiceLayerException(
					FormConstants.CLASS_NOT_FOUND_EXCEPTION);
			serviceLayerException.initCause(ex);
			throw serviceLayerException;
		}

		return response;
	}

	/**
	 * 
	 * Method Name: formatBookmarName Method Description:Private method used to
	 * retrieve the bookmark name
	 * 
	 * @param bookmarkName
	 * @return
	 */
	private String formatBookmarName(String bookmarkName) {
		String response = ServiceConstants.EMPTY_STRING;
		if (StringUtils.isNotBlank(bookmarkName)) {
			String[] nameArray = bookmarkName.split(FormConstants.SPLIT_UNDERSCORE);
			if (nameArray.length > 0) {
				nameArray[0] = WordUtils.swapCase(nameArray[0]);
				for (int i = 1; i < nameArray.length; i++) {
					nameArray[i] = WordUtils.capitalizeFully(nameArray[i]);
				}
			}
			StringBuilder concatenateValue = new StringBuilder();
			for (String value : nameArray) {
				concatenateValue.append(value);
			}
			response = concatenateValue.toString();
		}
		return response;
	}

	/**
	 * Method Name: saveRecordsCheckNotification Method Description: This method
	 * will update the Record check notification details by passing
	 * idRecordsCheckNotif as input
	 * 
	 * @param idRecordsCheckNotif
	 * @param userId
	 * @return FormsServiceRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FormsServiceRes saveRecordsCheckNotification(Long idRecordsCheckNotif, Long userId) {
		FormsServiceRes formsServiceRes = new FormsServiceRes();
		String rtnMsg = ServiceConstants.EMPTY_STRING;
		RecordsCheckNotifDto recordsCheckNotifDto = recordsCheckNotifDao
				.getRecordsCheckNotification(idRecordsCheckNotif);
		if (null != recordsCheckNotifDto
				&& CodesConstant.CNOTSTAT_NEW.equals(recordsCheckNotifDto.getCdNotifctnStat())) {
			recordsCheckNotifDto.setIdLastUpdatePerson(userId);
			recordsCheckNotifDto.setIdSndrPerson(userId);
			rtnMsg = recordsCheckNotifDao.updateRecordsCheckNotif(recordsCheckNotifDto);
			formsServiceRes.setReturnMessage(rtnMsg);
		}
		return formsServiceRes;
	}

	/**
	 * Method Name: saveActionForFbiFingerprint Method Description: This method
	 * will update  the CriminalHistory indManualSave and RecordsCheck dtRecCheckCompleted by passing
	 * idCrimHist as input
	 * 
	 * @param idCrimHist
	 * @param userId
	 * @return FormsServiceRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FormsServiceRes saveActionForFbiFingerprint(Long idCrimHist, Long userId) {
		FormsServiceRes formsServiceRes = new FormsServiceRes();
		Long result = ServiceConstants.ZERO;
		CrimHistDto crimHistDto = nameDao.getCriminalHistById(idCrimHist);
		if (!ObjectUtils.isEmpty(crimHistDto)){
			if (!ServiceConstants.Y.equals(crimHistDto.getIndManualSave()) && ObjectUtils.isEmpty(crimHistDto.getDtRecCheckCompl())) {
				CriminalHistoryUpdateReq criminalHistoryUpdateReq = new CriminalHistoryUpdateReq();
				criminalHistoryUpdateReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
				List<CrimHistorySaveDto> criminalHistoryList = new ArrayList<CrimHistorySaveDto>();
				CrimHistorySaveDto crimHistorySaveDto = new CrimHistorySaveDto();
				crimHistorySaveDto.setIdCrimHist(idCrimHist);
				crimHistorySaveDto.setIndManualSave(ServiceConstants.Y);
				crimHistorySaveDto.setIdLastModifiedPerson(userId);
				crimHistorySaveDto.setDtLastModified(new Date());
				crimHistorySaveDto.setDtLastUpdate(crimHistDto.getDtLastUpdate());
				crimHistorySaveDto.setTxtDpsSid(crimHistDto.getTxtDpsSid());
				crimHistorySaveDto.setDtResultsPosted(new Date());
				crimHistorySaveDto.setdPSMatchType(crimHistDto.getdPSMatchType());
				crimHistorySaveDto.setIndNarrative(ServiceConstants.Y);
				criminalHistoryList.add(crimHistorySaveDto);
				criminalHistoryUpdateReq.setCriminalHistoryList(criminalHistoryList);
	  			criminalHistoryDao.criminalHistoryRecAUD(criminalHistoryUpdateReq);
			}
			if ( 0 < crimHistDto.getIdRecCheck()){
				RecordsCheckDetailReq recordsCheckDetailReq = new RecordsCheckDetailReq();
				recordsCheckDetailReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
				List<RecordsCheckDetailDto> recordsCheckDetailDtoList = new ArrayList<RecordsCheckDetailDto>();
				RecordsCheckDetailDto recordsCheckDetailDto = new RecordsCheckDetailDto();
				recordsCheckDetailDto.setIdRecCheck(crimHistDto.getIdRecCheck());
				if (crimHistDto.getdPSMatchType().equalsIgnoreCase(ServiceConstants.INITIAL_FBI)
						|| crimHistDto.getdPSMatchType().equalsIgnoreCase(ServiceConstants.RAP_BACK)) {
					recordsCheckDetailDto.setDtRecCheckReceived(new Date());
				}
				if (crimHistDto.getdPSMatchType().equalsIgnoreCase(ServiceConstants.FRB_SUB)) {
					recordsCheckDetailDto.setCdFbiSubscriptionStatus(ServiceConstants.CD_RAPBKSS);
				}

				recordsCheckDetailDto.setDtLastUpdate(crimHistDto.getDtLastUpdateRecCheck());
				recordsCheckDetailDto.setRecCheckComments(crimHistDto.getTxtRecCheckComments()); // Added to fix defect artf179420
				recordsCheckDetailDtoList.add(recordsCheckDetailDto);
				recordsCheckDetailReq.setRecordsCheckDtoList(recordsCheckDetailDtoList);
				result = recordsCheckDao.updateRecordCheckforResults(recordsCheckDetailReq);
				formsServiceRes.setTotalRecCount(result);
			}
		}
		return formsServiceRes;
	}

	/**
	 * Method Name: getDatabaseMetaDataForPk Method Description: This method gets
	 * the primary key for tableMetaData.
	 * 
	 * @param tableName
	 * @return String
	 * @ @throws
	 *       SQLException
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FormsServiceRes getDatabaseMetaDataForPk(String tableName) {
		FormsServiceRes formsServiceRes = new FormsServiceRes();
		formsServiceRes.setPrimaryKey(formsDao.getDatabaseMetaDataForPk(tableName));
		return formsServiceRes;
	}

	/**
	 * Method Name: getRecordsCheckNotification Method Description: This method will
	 * fetch the Record check notification details by passing idRecordsCheckNotif as
	 * input
	 * 
	 * @param idRecordsCheckNotif
	 * @return FormsServiceRes @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FormsServiceRes getRecordsCheckNotification(Long idRecordsCheckNotif) {
		FormsServiceRes formsServiceRes = new FormsServiceRes();
		formsServiceRes.setRecordsCheckNotifDto(recordsCheckNotifDao.getRecordsCheckNotification(idRecordsCheckNotif));
		return formsServiceRes;
	}

	/**
	 * Method Name: updateRecordsCheckNotfcn Method Description: This method will
	 * update the Record check notification details
	 * 
	 * @param recordsCheckNotifDto
	 * @return FormsServiceRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FormsServiceRes updateRecordsCheckNotfcn(RecordsCheckNotifDto recordsCheckNotifDto) {
		FormsServiceRes formsServiceRes = new FormsServiceRes();
		formsServiceRes.setReturnMessage(recordsCheckNotifDao.updateRecordsCheckNotif(recordsCheckNotifDto));
		return formsServiceRes;
	}

	/**
	 * Method Name: insertRecordsCheckNotfcn Method Description: This method will
	 * create the new Record check notification details
	 * 
	 * @param recordsCheckNotifDto
	 * @return FormsServiceRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FormsServiceRes insertRecordsCheckNotfcn(RecordsCheckNotifDto recordsCheckNotifDto) {
		FormsServiceRes formsServiceRes = new FormsServiceRes();
		formsServiceRes.setIdRecordsCheckntfcn(recordsCheckNotifDao.insertRecordsCheckNotif(recordsCheckNotifDto));
		return formsServiceRes;
	}

	/**
	 * 
	 * Method Name: saveDocumentLog Method Description:Method used to save the form
	 * details into document logger
	 * 
	 * @param documentLogDto
	 * @return CommonFormRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonFormRes saveDocumentLog(DocumentLogDto documentLogDto) {
		formsDao.saveDocumentLog(documentLogDto);
		return null;
	}

	/**
	 * 
	 * Method Name: completePlcmntFstrResdntCareNarr Method Description: Update
	 * PlcmntFstrResdntCareNarr When User clicks save and submit button.
	 * 
	 * @param PlcmntFstrResdntCareNarrDto
	 *            plcmntFstrResdntCareNarrDto
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	@Override
	public Boolean completePlcmntFstrResdntCareNarr(PlcmntFstrResdntCareNarrDto plcmntFstrResdntCareNarrDto,String idPerson) {
		//Warranty Defect#11830 - Added new parameter to pass the user id to update in the narrative table
		Long userId = !ObjectUtils.isEmpty(idPerson) ? Long.valueOf(idPerson) : 0l; 
		return formsDao.completePlcmntFstrResdntCareNarr(plcmntFstrResdntCareNarrDto,userId);
	}

	/**
	 * 
	 * Method Name: getPlcmntFstrResdntCareNarr Method Description: Get
	 * PlcmntFstrResdntCareNarr
	 * 
	 * @param idEvent
	 * @return List<PlcmntFstrResdntCareNarrDto>
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	@Override
	public List<PlcmntFstrResdntCareNarrDto> getPlcmntFstrResdntCareNarr(Long idEvent) {
		return formsDao.getPlcmntFstrResdntCareNarr(idEvent);
	}

	/**
	 * 
	 * Method Name: deletePlcmntFstrResdntCareNarr Method Description: Delete
	 * PlcmntFstrResdntCareNarr Record
	 * 
	 * @param idEvent
	 * @param idDocumentTemplate
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	@Override
	public Boolean deletePlcmntFstrResdntCareNarr(Long idEvent, Long idDocumentTemplate) {
		Boolean result = Boolean.FALSE;
		// Delete narratives
		result = formsDao.deletePlcmntFstrResdntCareNarr(idEvent, idDocumentTemplate);

		// If narrative delete is successful, delete associated signatures
		if (result) {
			result = formsDao.deleteHandwrittenDataByIdEvent(idEvent);
		}

		return result;
	}

	/**
	 * 
	 * Method Name: documentTemplateCheck Method Description: This Method is used to
	 * check the document template is Legacy or Impact Phase 2 template or not.
	 * 
	 * @param documentTmpltCheckDto
	 * @param DocumentTemplateDto
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public DocumentTemplateDto documentTemplateCheck(DocumentTmpltCheckDto documentTmpltCheckDto) {
		return formsDao.documentTemplateCheck(documentTmpltCheckDto);
	}

	/**
	 * MethodName: verifyModifiableEvent. MethodDescription: This method will
	 * check the event record is in Pend status, if its in PEND status it will
	 * invalidate the pending approval and update the event status as COMP in
	 * event table..
	 * 
	 * @param documentMetaData
	 */
	private void verifyModifiableEvent(DocumentMetaData documentMetaData) {
		if (!ObjectUtils.isEmpty(documentMetaData) && !ObjectUtils.isEmpty(documentMetaData.getTableMetaData())) {
			TableFields tableFields = documentMetaData.getTableMetaData().getTableFields();
			for (Column column : tableFields.getColumn()) {
				if (ServiceConstants.ID_EVENT.equalsIgnoreCase(column.getName())) {
					EventDto eventDto = eventDao.getEventByid(Long.valueOf(column.getContent()));
					if (!ObjectUtils.isEmpty(eventDto) && !ObjectUtils.isEmpty(eventDto.getCdEventStatus())) {
						Boolean updateSafe = Boolean.FALSE;
						// For each valid status
						if (!ObjectUtils.isEmpty(documentMetaData.getValidEventStatus())) {
							List<String> validStatus = documentMetaData.getValidEventStatus().getValidStatus();
							for (String validEventStatus : validStatus) {
								if (eventDto.getCdEventStatus().toUpperCase()
										.equalsIgnoreCase(validEventStatus.toUpperCase())) {
									updateSafe = Boolean.TRUE;
									break;
								}
							}
							if (updateSafe == Boolean.FALSE) {
								throw new FormsException(lookupDao.getMessage(Messages.ARC_DOCS_ERR_INVALID_STATUS));								
							}
						}
						if ((!ObjectUtils.isEmpty(documentMetaData.isInApproverMod())
								&& !documentMetaData.isInApproverMod())
								&& CodesConstant.CEVTSTAT_PEND.equalsIgnoreCase(eventDto.getCdEventStatus())) {
							ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
							ApprovalCommonOutDto approvalCommonOutDto = new ApprovalCommonOutDto();
							approvalCommonInDto.setIdEvent(Long.valueOf(column.getContent()));
							approvalCommonInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
							approvalCommonInDto.setTransactionId(ServiceConstants.EMPTY_STRING);
							approvalCommonService.InvalidateAprvl(approvalCommonInDto,approvalCommonOutDto);
						}
					}
				}
			}
		}
	}

	/**
	 * Method Name: getRecordsCheckNotification Method Description: This method will
	 * fetch the Record check notification details by passing idRecordsCheckNotif as
	 * input
	 *
	 * @param idCrpRecordNotif
	 * @return FormsServiceRes @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FormsServiceRes getCrpRecordNotification(Long idCrpRecordNotif) {
		FormsServiceRes formsServiceRes = new FormsServiceRes();
		formsServiceRes.setCrpRecordNotifDto(crpRecordNotifDao.getCrpRecordNotification(idCrpRecordNotif));
		return formsServiceRes;
	}

	/**
	 * Method Name: updateRecordsCheckNotfcn Method Description: This method will
	 * update the Record check notification details
	 *
	 * @param formsReq
	 * @return FormsServiceRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FormsServiceRes updateCrpRecordNotfcn(FormsReq formsReq) {
		FormsServiceRes formsServiceRes = new FormsServiceRes();
		CrpRecordNotifDto crpRecordNotifDto = new CrpRecordNotifDto();

		if(formsReq.getIdCrpRecordNotif() != null)
		{
			crpRecordNotifDto = crpRecordNotifDao.getCrpRecordNotification(formsReq.getIdCrpRecordNotif());
		}
        else if(formsReq.getCrpRecordNotifDto() != null && formsReq.getCrpRecordNotifDto().getIdCrpRecordNotif() != null) {
			crpRecordNotifDto = crpRecordNotifDao.getCrpRecordNotification(formsReq.getCrpRecordNotifDto().getIdCrpRecordNotif());
			crpRecordNotifDto.setTxtSndrEmail(formsReq.getCrpRecordNotifDto().getTxtSndrEmail());
			crpRecordNotifDto.setTxtRecpntEmail(formsReq.getCrpRecordNotifDto().getTxtRecpntEmail());
			crpRecordNotifDto.setIdSndrPerson(formsReq.getCrpRecordNotifDto().getIdSndrPerson());
		}

		crpRecordNotifDto.setIdLastUpdatePerson(formsReq.getIdUser());
		formsServiceRes.setReturnMessage(crpRecordNotifDao.updateCrpRecordNotif(crpRecordNotifDto));

		return formsServiceRes;
	}


	/**
	 * Method Name: insertCrpNotification Method Description: This method
	 * will create the new crp record notification details
	 *
	 * @param crpRecordNotifDto
	 * @return FormsServiceRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FormsServiceRes insertCrpNotification(CrpRecordNotifDto crpRecordNotifDto) {
		FormsServiceRes formsServiceRes = new FormsServiceRes();
		formsServiceRes.setIdCrpRecordNtfcn(crpRecordNotifDao.insertCrpRecordNotif(crpRecordNotifDto));
		return formsServiceRes;

	}

	/**
	 * Method Description: This method is used to provide the
	 * CRP Record Notif Form PDF. This pdf constitutes the body of an email,
	 * that provides notification of results of the run of background checks
	 * as per the Public Central Registry Portal in 2024
	 *
	 * @param crpRequestStatusDto
	 * @return commonFormRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public FormsServiceRes insertCrpRequestStatus(CrpRequestStatusDto crpRequestStatusDto) {
		FormsServiceRes formsServiceRes = new FormsServiceRes();
		formsServiceRes.setReturnMessage(crpRecordNotifDao.insertCrpRequestStatus(crpRequestStatusDto));

		return formsServiceRes;

	}

}
