package us.tx.state.dfps.service.forms.daoimpl;

import static us.tx.state.dfps.service.common.ServiceConstants.TABLE_DISASTER_PLAN_NARR;
import static us.tx.state.dfps.service.common.ServiceConstants.TABLE_HOME_STUD_NARR;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.common.exception.FormsException;
import us.tx.state.dfps.familyplan.dto.FamilyPlanNarrDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.familyplan.dao.FamilyPlanDao;
import us.tx.state.dfps.service.forms.dao.FormsDao;
import us.tx.state.dfps.service.forms.dto.*;
import us.tx.state.dfps.service.placement.dto.PlcmntFstrResdntCareNarrDto;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jul 25, 2017- 2:06:12 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class FormsDaoImpl implements FormsDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MobileUtil mobileUtil;

	@Value("${FormsDaoImpl.selectDocumentTemplateInfo}")
	private String selectDocumentTemplateInfoSql;

	@Value("${FormsDaoImpl.selectDocumentTemplateType}")
	private String selectDocumentTemplateTypeSql;

	@Value("${FormsDaoImpl.getTemplateId}")
	private String getTemplateIdSql;

	@Value("${FormsDaoImpl.getDatabaseMetaDataForPk}")
	private String getDatabaseMetaDataForPk;

	@Value("${FormsDaoImpl.documentTemplateCheckQuery}")
	private String documentTemplateCheckQuery;

	@Value("${FormsDaoImpl.selectDocBlobWthOutIndFmlyPln}")
	private String selectDocBlobWithOutIndFmlyPln;

	@Value("${FormsDaoImpl.selectDocBlobWthIndFmlyPln}")
	private String selectDocBlobWithIndFmlyPln;

	@Value("${FormsDaoImpl.insertForms}")
	private String insertFormsSql;

	@Value("${FormsDaoImpl.insertFormsValues}")
	private String insertFormsValSql;

	@Value("${FormsDaoImpl.updateForms}")
	private String updateFormsSql;

	@Value("${FormsDaoImpl.updateCNForms}")
	private String updateCNFormsSql;

	@Value("${FormsDaoImpl.selectCompositeDocBlob}")
	private String selectCompositeDocBlob;

	@Value("${FormsDaoImpl.deleteForms}")
	private String deleteFormsSql;

	@Value("${FormsDaoImpl.getTimeStamp}")
	private String getTimeStamp;

	@Value("${FormsDaoImpl.selectDocument}")
	private String selectDocumentSql;

	@Value("${FormsDaoImpl.selectExistNarrDoc}")
	private String selectExistNarrDocSql;

	@Value("${FormsDaoImpl.insertExistNarrDoc}")
	private String insertExistNarrDocSql;
	
	@Value("${FormsDaoImpl.selectDocumentTemplate}")
	private String selectDocumentTemplateSql;

	private static final Logger FormsDaoImpllog = Logger.getLogger(FormsDaoImpl.class);
	
	private static final String ARC_DOCS_ERR_TIMESTAMP_MISMATCH = "ARC_DOCS_ERR_TIMESTAMP_MISMATCH";
	
	private static final String SQL_DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";
	  
	private static final SimpleDateFormat sqlDateFormat = new SimpleDateFormat( SQL_DATE_FORMAT );


	@Autowired
	FamilyPlanDao familyPlanDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.forms.dao.FormsDao#selectDocumentBlob(us.tx.
	 * state.dfps.service.forms.dto.DocumentMetaData)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NewUsingDocumentDto selectDocumentBlob(DocumentMetaData documentMetaData) {
		StringBuilder selectBlobQuery = new StringBuilder();
		String selectQuery = ServiceConstants.EMPTY_STRING;
		NewUsingDocumentDto newUsingDocumentDto = new NewUsingDocumentDto();
		boolean indFamilyPlanNarr = false;

		if (ServiceConstants.FAMILYPLANFPR.equalsIgnoreCase(documentMetaData.getDocumentType())
				|| ServiceConstants.FAMILYPLANFRE.equalsIgnoreCase(documentMetaData.getDocumentType())
				|| ServiceConstants.FAMILYPLANFSU.equalsIgnoreCase(documentMetaData.getDocumentType())
				|| ServiceConstants.FAMILYPLANEVALFPR.equalsIgnoreCase(documentMetaData.getDocumentType())
				|| ServiceConstants.FAMILYPLANEVALFRE.equalsIgnoreCase(documentMetaData.getDocumentType())
				|| ServiceConstants.FAMILYPLANEVALFSU.equalsIgnoreCase(documentMetaData.getDocumentType())) {
			indFamilyPlanNarr = true;
		}
		if (indFamilyPlanNarr) {
			selectQuery = selectDocBlobWithIndFmlyPln;
		} else {
			selectQuery = selectDocBlobWithOutIndFmlyPln;
		}
		selectBlobQuery
				.append(selectQuery.replaceFirst("%narrCol", documentMetaData.getTableMetaData().getNarrativeColumn())
						.replaceFirst("%table", documentMetaData.getTableMetaData().getTableName()));
		// Get the table columns
		List<String> parameterList = new ArrayList<>();
		boolean isValid = false;
		for (Column column : documentMetaData.getTableMetaData().getTableFields().getColumn()) {
			if (!ObjectUtils.isEmpty(column.getName()) && !ObjectUtils.isEmpty(column.getContent())) {
				if (!ServiceConstants.DT_UPDATE.equalsIgnoreCase(column.getName().toUpperCase())
						&& !ServiceConstants.ID_CREATED_PRSN.equalsIgnoreCase(column.getName().toUpperCase())
						&& !ServiceConstants.ID_LAST_UPDATE_PRSN.equalsIgnoreCase(column.getName().toUpperCase())) {

					if (isValid && documentMetaData.getTableMetaData().getTableFields().getColumn().size() > 0) {
						selectBlobQuery.append(" AND ");
					}
					selectBlobQuery.append(column.getName());
					selectBlobQuery.append(" = ?");
					isValid = true;
					parameterList.add(column.getContent());
				}
			}
		}
		String selectBlobQueryString = selectBlobQuery.toString();
		if(mobileUtil.isMPSEnvironment()){
			selectBlobQueryString = selectBlobQueryString.replaceAll("documentBlob","documentBytes");
		}
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(selectBlobQueryString);
		query.addScalar("templateId", StandardBasicTypes.LONG);
		if (mobileUtil.isMPSEnvironment() ){
			query.addScalar("documentBytes", StandardBasicTypes.BINARY);
		} else {
			query.addScalar("documentBlob", StandardBasicTypes.BLOB);
		}
		if (indFamilyPlanNarr) {
			query.addScalar("idNarrative", StandardBasicTypes.LONG);
		}
		int count = 0;
		for (String string : parameterList) {
			query.setParameter(count, string);
			count++;
		}
		List<FormsMetadataDto> tempList = new ArrayList<FormsMetadataDto>();
		FormsMetadataDto templateDto = new FormsMetadataDto();

		if (indFamilyPlanNarr) {

			tempList = query.setResultTransformer(Transformers.aliasToBean(FormsMetadataDto.class)).list();

			List<Column> columns = documentMetaData.getTableMetaData().getTableFields().getColumn();
			Long idStage = Long
					.valueOf(columns.stream().filter(x -> "id_stage".equals(x.getName())).findAny().get().getContent());
			Long idEvent = Long
					.valueOf(columns.stream().filter(x -> "id_event".equals(x.getName())).findAny().get().getContent());
			String selectedDtLastUpdate = columns.stream().filter(x -> "dt_last_update".equals(x.getName())).findAny()
					.get().getContent().replace("+", " ");
			Date dtLastUpdate = null;
			try {
				dtLastUpdate = DateUtils.parseDate(selectedDtLastUpdate,
						new String[] { ServiceConstants.SQL_DATE_FORMAT });
			} catch (ParseException e) {
				throw new FormsException(e.getMessage());
			}

			List<FamilyPlanNarrDto> narrList = familyPlanDao.getFamilyPlanNarrList(idStage, idEvent);

			Long idFamilyPlanNarr = ServiceConstants.ZERO;

			for (FamilyPlanNarrDto familyPlanNarrDto : narrList) {
				if (familyPlanNarrDto.getDtLastUpdate().compareTo(dtLastUpdate) == 0) {
					idFamilyPlanNarr = familyPlanNarrDto.getIdFamilyPlanNarr();
					break;
				}
			}
			for (FormsMetadataDto formsMetadataDto : tempList) {
				if (idFamilyPlanNarr.equals(formsMetadataDto.getIdNarrative())) {
					templateDto = formsMetadataDto;
					break;
				}
			}
		} else {
			templateDto = (FormsMetadataDto) query
					.setResultTransformer(Transformers.aliasToBean(FormsMetadataDto.class)).uniqueResult();
		}
		try {
			if (!mobileUtil.isMPSEnvironment() &&!ObjectUtils.isEmpty(templateDto) && templateDto.getDocumentBlob().length() > 0) {
				newUsingDocumentDto.setDocumentData(getBytes(templateDto.getDocumentBlob().getBinaryStream()));
			} else if(mobileUtil.isMPSEnvironment()){
				newUsingDocumentDto.setDocumentData(templateDto.getDocumentBytes());
			}
		} catch (SQLException e) {
			FormsDaoImpllog.error(e.getMessage());
			DataLayerException dataLayerException = new DataLayerException(e.getMessage());
			dataLayerException.initCause(e);
			throw dataLayerException;
		}
		if (!ObjectUtils.isEmpty(templateDto)) {
			newUsingDocumentDto.setIdTemplate(templateDto.getTemplateId());
		}
		return newUsingDocumentDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.forms.dao.FormsDao#selectDocumentTemplateInfo(
	 * java.lang.Long)
	 */
	@Override
	public DocumentTemplateDto selectDocumentTemplateInfo(Long templateId) {
		DocumentTemplateDto templateValueDto = new DocumentTemplateDto();
		String queryString = selectDocumentTemplateInfoSql;
		if(mobileUtil.isMPSEnvironment()){
			queryString = queryString.replaceAll("htmlTemplate","htmlTemplateBytes");
		}
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(queryString).addScalar("templateID", StandardBasicTypes.LONG)
				.addScalar("typeID", StandardBasicTypes.LONG).addScalar("versionMajor", StandardBasicTypes.LONG)
				.addScalar("versionMinor", StandardBasicTypes.LONG)
				.addScalar("versionRevision", StandardBasicTypes.LONG)
				.addScalar("versionString", StandardBasicTypes.STRING)
				.addScalar("shortDescription", StandardBasicTypes.STRING)
				.addScalar("longDescription", StandardBasicTypes.STRING)
				.addScalar("active", StandardBasicTypes.STRING)
				.addScalar("typeName", StandardBasicTypes.STRING).addScalar("typeDocument", StandardBasicTypes.STRING)
				.addScalar("timeStamp", StandardBasicTypes.STRING).addScalar("indLgcy", StandardBasicTypes.STRING)
				.setLong("templateId", templateId);
		if(mobileUtil.isMPSEnvironment()){
			query.addScalar("htmlTemplateBytes", StandardBasicTypes.BINARY);
		} else {
			query.addScalar("htmlTemplate", StandardBasicTypes.BLOB);
		}
		FormsTemplateDto formsTemplateDto = (FormsTemplateDto) query
				.setResultTransformer(Transformers.aliasToBean(FormsTemplateDto.class)).uniqueResult();
		try {
			org.apache.commons.beanutils.BeanUtils.copyProperties(templateValueDto, formsTemplateDto);
			if(mobileUtil.isMPSEnvironment()){
				templateValueDto.setHtml(getCompressedHtml(formsTemplateDto.getHtmlTemplateBytes()));
			} else {
				templateValueDto.setHtml(getCompressedHtml(formsTemplateDto.getHtmlTemplate()));
			}
			templateValueDto.setIdTemplate(formsTemplateDto.getTemplateID());
		} catch (Exception e) {
			FormsDaoImpllog.error(e.getMessage());
			DataLayerException dataLayerException = new DataLayerException(e.getMessage());
			dataLayerException.initCause(e);
			throw dataLayerException;
		}
		return templateValueDto;
	}
	
	/**
	 * 
	 * Method Name: selectDocumentTemplate
	 * Method Description:Method used to retrieve all the Modernized template information
	 * 
	 * @return List<DocumentTemplateDto>
	 */
	@Override
	public List<DocumentTemplateDto> selectDocumentTemplate() {
		ArrayList<DocumentTemplateDto> templateValueDto = new ArrayList<DocumentTemplateDto>();
		String queryString = selectDocumentTemplateSql;
		if(mobileUtil.isMPSEnvironment()){
			queryString = queryString.replaceAll("htmlTemplate","htmlTemplateBytes");
		}
		SQLQuery query=sessionFactory.getCurrentSession()
				.createSQLQuery(queryString).addScalar("templateID", StandardBasicTypes.LONG)
				.addScalar("typeID", StandardBasicTypes.LONG).addScalar("versionMajor", StandardBasicTypes.LONG)
				.addScalar("versionMinor", StandardBasicTypes.LONG)
				.addScalar("versionRevision", StandardBasicTypes.LONG)
				.addScalar("versionString", StandardBasicTypes.STRING)
				.addScalar("shortDescription", StandardBasicTypes.STRING)
				.addScalar("longDescription", StandardBasicTypes.STRING)
				.addScalar("active", StandardBasicTypes.STRING)
				.addScalar("typeName", StandardBasicTypes.STRING).addScalar("typeDocument", StandardBasicTypes.STRING)
				.addScalar("timeStamp", StandardBasicTypes.STRING)
				.addScalar("indLgcy", StandardBasicTypes.STRING);
		if(mobileUtil.isMPSEnvironment()){
			query.addScalar("htmlTemplateBytes", StandardBasicTypes.BINARY);
		} else {
			query.addScalar("htmlTemplate", StandardBasicTypes.BLOB);
		}
		List<FormsTemplateDto> formsTemplateDto = (List<FormsTemplateDto> ) query
				.setResultTransformer(Transformers.aliasToBean(FormsTemplateDto.class)).list();
		try {
			formsTemplateDto.forEach(dto -> {
				DocumentTemplateDto templateDto = new DocumentTemplateDto();
				BeanUtils.copyProperties(dto, templateDto);
				if(mobileUtil.isMPSEnvironment()){
					templateDto.setHtml(getCompressedHtml(dto.getHtmlTemplateBytes()));
				} else {
					templateDto.setHtml(getCompressedHtml(dto.getHtmlTemplate()));
				}

				templateDto.setIdTemplate(dto.getTemplateID());
				templateValueDto.add(templateDto);
			});

		} catch (Exception e) {
			FormsDaoImpllog.error(e.getMessage());
			DataLayerException dataLayerException = new DataLayerException(e.getMessage());
			dataLayerException.initCause(e);
			throw dataLayerException;
		}
		return templateValueDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.forms.dao.FormsDao#selectLatestTemplateType(java
	 * .lang.String)
	 */
	@Override
	public DocumentTemplateDto selectLatestTemplateType(String templateType) {
		String queryString = selectDocumentTemplateTypeSql;

		if(mobileUtil.isMPSEnvironment()){
			queryString = queryString.replaceAll("htmlTemplate", "htmlTemplateBytes");
		}

		DocumentTemplateDto templateValueDto = new DocumentTemplateDto();
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(queryString).addScalar("templateID", StandardBasicTypes.LONG)
				.addScalar("typeID", StandardBasicTypes.LONG).addScalar("versionMajor", StandardBasicTypes.LONG)
				.addScalar("versionMinor", StandardBasicTypes.LONG)
				.addScalar("versionRevision", StandardBasicTypes.LONG)
				.addScalar("versionString", StandardBasicTypes.STRING)
				.addScalar("shortDescription", StandardBasicTypes.STRING)
				.addScalar("longDescription", StandardBasicTypes.STRING)
				.addScalar("active", StandardBasicTypes.STRING)
				.addScalar("typeName", StandardBasicTypes.STRING).addScalar("typeDocument", StandardBasicTypes.STRING)
				.addScalar("timeStamp", StandardBasicTypes.STRING).setString("templateType", templateType);
		if(mobileUtil.isMPSEnvironment()){
			query.addScalar("htmlTemplateBytes", StandardBasicTypes.BINARY);
		} else {
			query.addScalar("htmlTemplate", StandardBasicTypes.BLOB);
		}
		List<FormsTemplateDto> formsTemplateDtoList = (List<FormsTemplateDto>) query
				.setResultTransformer(Transformers.aliasToBean(FormsTemplateDto.class)).list();
		FormsTemplateDto formsTemplateDto = (formsTemplateDtoList.size() > 0) ? formsTemplateDtoList.get(0)
				: new FormsTemplateDto();

		try {
			org.apache.commons.beanutils.BeanUtils.copyProperties(templateValueDto, formsTemplateDto);
			if(mobileUtil.isMPSEnvironment()){
				templateValueDto.setHtml(getCompressedHtml(formsTemplateDto.getHtmlTemplateBytes()));
			} else {
				templateValueDto.setHtml((!org.springframework.util.ObjectUtils.isEmpty(formsTemplateDto))
						? getCompressedHtml(formsTemplateDto.getHtmlTemplate()) : null);
			}
			templateValueDto.setIdTemplate(formsTemplateDto.getTemplateID());
		} catch (Exception e) {
			FormsDaoImpllog.error(e.getMessage());
			DataLayerException dataLayerException = new DataLayerException(e.getMessage());
			dataLayerException.initCause(e);
			throw dataLayerException;
		}
		return templateValueDto;
	}

	/**
	 * 
	 * Method Name: getBytes Method Description:Method used to get the byte
	 * array for the given input stream
	 * 
	 * @param inputStream
	 * @return byte[]
	 */
	private byte[] getBytes(InputStream inputStream) {

		BufferedInputStream buffInputStream = new BufferedInputStream(inputStream);
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		try {
			byte[] b = new byte[2048];
			for (int n = buffInputStream.read(b); n > 0; n = buffInputStream.read(b)) {
				byteOutputStream.write(b, 0, n);
			}
		} catch (Exception e) {
			FormsDaoImpllog.error(e.getMessage());
			DataLayerException dataLayerException = new DataLayerException(e.getMessage());
			dataLayerException.initCause(e);
			throw dataLayerException;
		}
		return byteOutputStream.toByteArray();
	}

	/**
	 * 
	 * Method Name: getCompressedHtml Method Description:Method used to get the
	 * compressed data
	 * 
	 * @param htmlData
	 * @return
	 */
	private String getCompressedHtml(Blob htmlData) {

		String htmlDataString = "";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// initialize the byte variable
		try {
			InputStream is = htmlData.getBinaryStream();
			byte[] bufdata = new byte[1024];
			while (true) {
				int n = is.read(bufdata);
				// EOF, come out of loop
				if (n <= 0)
					break;
				baos.write(bufdata, 0, n);
			}

			htmlDataString = new String(baos.toByteArray(), ServiceConstants.CHARACTER_ENCODING);
		} catch (Exception e) {
			FormsDaoImpllog.error(e.getMessage());
			DataLayerException dataLayerException = new DataLayerException(e.getMessage());
			dataLayerException.initCause(e);
			throw dataLayerException;
		}

		return htmlDataString;
	}

	private String getCompressedHtml(byte[] htmlData) {
		try {
			return new String(htmlData, ServiceConstants.CHARACTER_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.forms.dao.FormsDao#saveForms(us.tx.state.dfps.
	 * service.forms.dto.DocumentMetaData, byte[])
	 */
	@Override
	public int saveForms(DocumentMetaData documentMetaData, byte[] documentData) {
		int insertedRecNum = ServiceConstants.Zero_INT;
		StringBuilder insertFormsQuery = new StringBuilder();
		Long idEvent = 0l;
		insertFormsQuery
				.append(insertFormsSql.replaceFirst("%table", documentMetaData.getTableMetaData().getTableName()));
		Map<Integer, Column> parameterMap = new HashMap<Integer, Column>();
		int paramCount = -1;
		for (int x = 0; x < documentMetaData.getTableMetaData().getTableFields().getColumn().size(); x++) {
			Column column = documentMetaData.getTableMetaData().getTableFields().getColumn().get(x);
			if (!column.getName().toUpperCase().equals("DT_LAST_UPDATE")) {
				if (column.getName().toUpperCase().equals("ID_EVENT")) { //Defect 11203
					idEvent = Long.valueOf(column.getContent());
				}
				insertFormsQuery.append(column.getName());
				insertFormsQuery.append(", ");
				paramCount++;
				parameterMap.put(Integer.valueOf(paramCount), column);
			}
		}
		//Defect 11203- To check event id is already existing in contact narrative table to call insert or update operation
		if (!ServiceConstants.ZERO.equals(idEvent) && 
				!ObjectUtils.isEmpty(sessionFactory.getCurrentSession().get(ContactNarrative.class, idEvent))) {
			insertedRecNum = updateForms(documentMetaData, documentData);
		} else {
			if (Arrays.asList(ServiceConstants.FAMILYPLANFPR, ServiceConstants.FAMILYPLANFRE, ServiceConstants.FAMILYPLANFSU
					, ServiceConstants.FAMILYPLANEVALFPR, ServiceConstants.FAMILYPLANEVALFRE, ServiceConstants.FAMILYPLANEVALFSU /*
					, CodesConstant.CRPNOTTY_ARIFNOT, CodesConstant.CRPNOTTY_MATCNOT, CodesConstant.CRPNOTTY_SOAHNOT
					, CodesConstant.CRPNOTTY_LTRHNOT */).contains(documentMetaData.getDocumentType())) {
				Date date = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
				insertFormsQuery.append("dt_created, ");
				Column column = new Column();
				column.setName("dt_created");
				column.setContent(String.valueOf(dateFormat.format(date)));
				paramCount++;
				parameterMap.put(Integer.valueOf(paramCount), column);
			}

			if (!documentMetaData.isLegacy()) {
				insertFormsQuery.append("ID_DOCUMENT_TEMPLATE, ");
				paramCount++;
				parameterMap.put(Integer.valueOf(paramCount), null);
			}
			insertFormsQuery.append(documentMetaData.getTableMetaData().getNarrativeColumn());
			insertFormsQuery.append(insertFormsValSql);

			for (int x = 0; x <= paramCount + 1; x++) {
				if (x != paramCount + 1) {
					insertFormsQuery.append("?, ");
				} else {
					insertFormsQuery.append('?');
				}
			}
			insertFormsQuery.append(')');

			Query query = sessionFactory.getCurrentSession().createSQLQuery(insertFormsQuery.toString());

			for (Entry<Integer, Column> entry : parameterMap.entrySet()) {
				Column column = entry.getValue();
				if (null == column) {
					query.setParameter(entry.getKey(), documentMetaData.getActualTemplateVersion());
				} else {
					query.setParameter(entry.getKey(), column.getContent());
				}
			}

			query.setBinary(paramCount + 1, documentData);
			
			try {
				insertedRecNum = query.executeUpdate();
			} catch (Exception e) {
				StringBuilder additionalInfo = new StringBuilder();
				additionalInfo.append(System.lineSeparator() + "Troubleshoot Forms insert issue - Begin");
				additionalInfo
						.append(System.lineSeparator() + "Forms Insert Statement: " + insertFormsQuery.toString());
				additionalInfo.append(System.lineSeparator() + "Doc Type: "
						+ (!ObjectUtils.isEmpty(documentMetaData.getDocumentType()) ? documentMetaData.getDocumentType()
								: ""));
				additionalInfo.append(System.lineSeparator() + "Display Name: "
						+ (!ObjectUtils.isEmpty(documentMetaData.getDocumentDisplayName())
								? documentMetaData.getDocumentDisplayName() : ""));
				additionalInfo.append(System.lineSeparator() + "Doc Exists: "
						+ (!ObjectUtils.isEmpty(documentMetaData.isDocumentExists())
								? documentMetaData.isDocumentExists() : ""));
				// Table information
				for (int x = 0; x < documentMetaData.getTableMetaData().getTableFields().getColumn().size(); x++) {
					Column column = documentMetaData.getTableMetaData().getTableFields().getColumn().get(x);
					if (!ObjectUtils.isEmpty(column)) {
						additionalInfo.append(System.lineSeparator() + "Param Name: "
								+ (!ObjectUtils.isEmpty(column.getName()) ? column.getName() : ""));
						additionalInfo.append(System.lineSeparator() + "Param Value: "
								+ (!ObjectUtils.isEmpty(column.getContent()) ? column.getContent() : ""));
					}
				}
				additionalInfo.append("Troubleshoot Forms insert issue - End");
				FormsDaoImpllog.error(e.getMessage() + additionalInfo.toString());
				DataLayerException dataLayerException = new DataLayerException(e.getMessage());
				dataLayerException.initCause(e);
				throw dataLayerException;
			}
		}
		return insertedRecNum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.forms.dao.FormsDao#updateForms(us.tx.state.dfps.
	 * service.forms.dto.DocumentMetaData, byte[])
	 */
	@Override
	public int updateForms(DocumentMetaData documentMetaData, byte[] documentData) {
		StringBuilder updateFormsQuery = new StringBuilder();
		//Warranty Defect#12027 - Added condition to check the same form is updated from different tabs
		// artf195412 - Added HM_REQ_NARR & HM_SAFETY_CHECK_NARR to list to update the Last update person as well
		String updateSQL = updateFormsSql;
		int y = 2;
		if (ServiceConstants.FORMS_TABLE_NAME_AUDIT.contains(documentMetaData.getTableMetaData().getTableName())) {
			updateSQL = updateCNFormsSql;
			y = 3;
		}
		updateFormsQuery
				.append(updateSQL.replaceFirst("%table", documentMetaData.getTableMetaData().getTableName())
						.replaceFirst("%column", documentMetaData.getTableMetaData().getNarrativeColumn()));

		Long version = null;
		Map<Integer, Column> parameterMap = new HashMap<Integer, Column>();

		boolean addAndFlag = false;
		for (int x = 0; x < documentMetaData.getTableMetaData().getTableFields().getColumn().size(); x++) {
			Column column = documentMetaData.getTableMetaData().getTableFields().getColumn().get(x);

			if (!ObjectUtils.isEmpty(column.getName()) && !ObjectUtils.isEmpty(column.getContent())) {
				if (!ServiceConstants.ID_CREATED_PRSN.equalsIgnoreCase(column.getName().toUpperCase())
						&& !ServiceConstants.ID_LAST_UPDATE_PRSN.equalsIgnoreCase(column.getName().toUpperCase())) {
					if (addAndFlag) {
						updateFormsQuery.append(" AND ");
					} else {
						addAndFlag = true;
					}
					if (ServiceConstants.DT_UPDATE.equalsIgnoreCase(column.getName()) &&
							(TABLE_HOME_STUD_NARR.equalsIgnoreCase(documentMetaData.getTableMetaData().getTableName()) ||
									TABLE_DISASTER_PLAN_NARR.equalsIgnoreCase(documentMetaData.getTableMetaData().getTableName()))) {
						updateFormsQuery.append(" to_date(to_char(");
						updateFormsQuery.append(column.getName().toUpperCase());
						updateFormsQuery.append(", 'MM/dd/yyyy'),'MM/dd/yyyy') = to_date(to_char(?, 'MM/dd/yyyy'),'MM/dd/yyyy') ");
					}else{
						updateFormsQuery.append(column.getName().toUpperCase());
						updateFormsQuery.append(" = ? ");
					}
					parameterMap.put(y, column);
					y = y + 1;
				}
			}
			if (ServiceConstants.FORMS_TABLE_NAME_AUDIT.contains(documentMetaData.getTableMetaData().getTableName())
					&& !ObjectUtils.isEmpty(column.getContent())
					&& ServiceConstants.ID_LAST_UPDATE_PRSN.equalsIgnoreCase(column.getName().toUpperCase())){
				parameterMap.put(2, column);
			}
		}

		if (!documentMetaData.isLegacy()) {
			version = documentMetaData.getActualTemplateVersion();
		}

		if (ServiceConstants.FAMILYPLANFPR.equalsIgnoreCase(documentMetaData.getDocumentType())
				|| ServiceConstants.FAMILYPLANEVALFPR.equalsIgnoreCase(documentMetaData.getDocumentType())) {
			List<Column> columns = documentMetaData.getTableMetaData().getTableFields().getColumn();
			Long idStage = Long
					.valueOf(columns.stream().filter(x -> "id_stage".equals(x.getName())).findAny().get().getContent());
			Long idEvent = Long
					.valueOf(columns.stream().filter(x -> "id_event".equals(x.getName())).findAny().get().getContent());
			String selectedDtLastUpdate = columns.stream().filter(x -> "dt_last_update".equals(x.getName())).findAny()
					.get().getContent();
			Date dtLastUpdate = null;
			try {
				dtLastUpdate = DateUtils.parseDate(selectedDtLastUpdate,
						new String[] { ServiceConstants.SQL_DATE_FORMAT });
			} catch (ParseException e) {
				throw new FormsException(e.getMessage());
			}

			List<FamilyPlanNarrDto> narrList = familyPlanDao.getFamilyPlanNarrList(idStage, idEvent);
			Long idFamilyPlanNarr = ServiceConstants.ZERO;
			for (FamilyPlanNarrDto familyPlanNarrDto : narrList) {
				if (familyPlanNarrDto.getDtLastUpdate().compareTo(dtLastUpdate) == 0) {
					idFamilyPlanNarr = familyPlanNarrDto.getIdFamilyPlanNarr();
					break;
				}
			}
			if (idFamilyPlanNarr > 0) {
				updateFormsQuery.append(" AND ID_FAMILY_PLAN_NARR = ").append(idFamilyPlanNarr);
			}
		}

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateFormsQuery.toString())
				.setBinary(0, documentData).setLong(1, version);
		for (Entry<Integer, Column> entry : parameterMap.entrySet()) {
			if (ServiceConstants.DT_UPDATE.equalsIgnoreCase(entry.getValue().getName().toUpperCase())) {
				Date dtLastUpdate = null;			
				try {
					if(!ObjectUtils.isEmpty(entry.getValue().getContent()))
					{
					dtLastUpdate = DateUtils.parseDate(entry.getValue().getContent(),
							new String[] { ServiceConstants.SQL_DATE_FORMAT});
					query.setParameter(entry.getKey(), dtLastUpdate);
					}
				} catch (ParseException e) {
					 query.setParameter(entry.getKey(), new Date(entry.getValue().getContent()));
				}
			} else {
				query.setParameter(entry.getKey(), entry.getValue().getContent());
			}
		}
		int updatedRecNum = ServiceConstants.Zero_INT;
		try {
			updatedRecNum = query.executeUpdate();
			if (0 == updatedRecNum) {
				throw new DataLayerException(ARC_DOCS_ERR_TIMESTAMP_MISMATCH);
			}
		} catch (Exception e) {
			StringBuilder additionalInfo = new StringBuilder();
			additionalInfo.append(System.lineSeparator() + "Troubleshoot Forms update issue - Begin");
			additionalInfo.append(System.lineSeparator() + "Forms Insert Statement: " + updateFormsQuery.toString());
			additionalInfo.append(
					System.lineSeparator() + "Doc Type: " + (!ObjectUtils.isEmpty(documentMetaData.getDocumentType())
							? documentMetaData.getDocumentType() : ""));
			additionalInfo.append(System.lineSeparator() + "Display Name: "
					+ (!ObjectUtils.isEmpty(documentMetaData.getDocumentDisplayName())
							? documentMetaData.getDocumentDisplayName() : ""));
			additionalInfo.append(
					System.lineSeparator() + "Doc Exists: " + (!ObjectUtils.isEmpty(documentMetaData.isDocumentExists())
							? documentMetaData.isDocumentExists() : ""));
			// Table information
			for (int x = 0; x < documentMetaData.getTableMetaData().getTableFields().getColumn().size(); x++) {
				Column column = documentMetaData.getTableMetaData().getTableFields().getColumn().get(x);
				if (!ObjectUtils.isEmpty(column)) {
					additionalInfo.append(System.lineSeparator() + "Param Name: "
							+ (!ObjectUtils.isEmpty(column.getName()) ? column.getName() : ""));
					additionalInfo.append(System.lineSeparator() + "Param Value: "
							+ (!ObjectUtils.isEmpty(column.getContent()) ? column.getContent() : ""));
				}
			}
			additionalInfo.append("Troubleshoot Forms update issue - End");
			FormsDaoImpllog.error(e.getMessage() + additionalInfo.toString());
			DataLayerException dataLayerException = new DataLayerException(e.getMessage());
			dataLayerException.initCause(e);
			throw dataLayerException;
		}

		return updatedRecNum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.forms.dao.FormsDao#selectCompositeDocumentBlob(
	 * java.lang.Long, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public NewUsingDocumentDto selectCompositeDocumentBlob(Long blobId, String tableName, String colNarrColumn,
			String colIdentColumn) {
		String selectBlobQuery = ServiceConstants.EMPTY_STRING;
		NewUsingDocumentDto newUsingDocumentDto = new NewUsingDocumentDto();
		selectBlobQuery = selectCompositeDocBlob.replaceFirst("%colName", colNarrColumn)
				.replaceFirst("%table", tableName).replaceFirst("%identCol", colIdentColumn);
		if (mobileUtil.isMPSEnvironment()){
			selectBlobQuery.replaceAll("documentBlob","documentBytes");
		}
		try {
			SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(selectBlobQuery.toString()).addScalar("templateId", StandardBasicTypes.LONG)
					.setParameter("blobId", blobId);
			if(mobileUtil.isMPSEnvironment()){
				query.addScalar("documentBytes", StandardBasicTypes.BINARY);
			} else {
				query.addScalar("documentBlob", StandardBasicTypes.BLOB);
			}
			FormsMetadataDto templateDto = (FormsMetadataDto) query
					.setResultTransformer(Transformers.aliasToBean(FormsMetadataDto.class)).uniqueResult();
			if(mobileUtil.isMPSEnvironment()){
				newUsingDocumentDto.setDocumentData(templateDto.getDocumentBytes());
			} else {
				// Added the null condition check for DocumentBlob -Warranty Defect 11072
				newUsingDocumentDto.setDocumentData(
						(!ObjectUtils.isEmpty(templateDto) && !ObjectUtils.isEmpty(templateDto.getDocumentBlob())) ? getBytes(templateDto.getDocumentBlob().getBinaryStream())
								: newUsingDocumentDto.getDocumentData());
			}
			if (!ObjectUtils.isEmpty(templateDto)) {
				newUsingDocumentDto.setIdTemplate(templateDto.getTemplateId());
			}
		} catch (SQLException e) {
			FormsDaoImpllog.error(e.getMessage());
			DataLayerException dataLayerException = new DataLayerException(e.getMessage());
			dataLayerException.initCause(e);
			throw dataLayerException;
		}
		return newUsingDocumentDto;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.forms.dao.FormsDao#deleteForms(us.tx.state.dfps.
	 * service.forms.dto.DocumentMetaData)
	 */
	@Override
	public String deleteForms(DocumentMetaData documentMetaData) {
		StringBuilder deleteFormsQuery = new StringBuilder();
		deleteFormsQuery
				.append(deleteFormsSql.replaceFirst("%table", documentMetaData.getTableMetaData().getTableName()));
		List<String> parameterList = new ArrayList<>();
		for (int x = 0; x < documentMetaData.getTableMetaData().getTableFields().getColumn().size(); x++) {
			Column column = documentMetaData.getTableMetaData().getTableFields().getColumn().get(x);

			String tempName = column.getName().toUpperCase();
			if (!tempName.equals("DT_LAST_UPDATE")) {
				if (x != 0)
					deleteFormsQuery.append(" AND ");
				deleteFormsQuery.append(column.getName());
				deleteFormsQuery.append(" = ? ");
				/*
				 * deleteFormsQuery.append(column.getName()); if
				 * (column.getFormat() != null) {
				 * deleteFormsQuery.append(" = to_date( ? , '");
				 * deleteFormsQuery.append(column.getFormat());
				 * deleteFormsQuery.append("')"); } else { deleteFormsQuery.
				 * append(" = to_date( ? ,'MM/DD/YYYY HH24:MI:SS')"); }
				 */
				parameterList.add(column.getContent());
			}
		}

		Query query = sessionFactory.getCurrentSession().createSQLQuery(deleteFormsQuery.toString());
		int count = 0;
		Date date = new Date();
		for (String string : parameterList) {
			if (StringUtils.isEmpty(string))
				query.setParameter(count, date);
			query.setParameter(count, string);
			count++;
		}
		return (query.setResultTransformer(Transformers.aliasToBean(DocumentMetaData.class)).executeUpdate() > 0)
				? "SUCCESS" : "FAILURE";

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.forms.dao.FormsDao#getTimeStamp(us.tx.state.dfps
	 * .service.forms.dto.DocumentMetaData)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Date getTimeStamp(DocumentMetaData documentMetaData) {

		StringBuilder getTimeStampQuery = new StringBuilder();
		getTimeStampQuery
				.append(getTimeStamp.replaceFirst("%table", documentMetaData.getTableMetaData().getTableName()));
		Map<Integer, Column> parameterMap = new HashMap<Integer, Column>();
		int paramcount = -1;
		for (int x = 0; x < documentMetaData.getTableMetaData().getTableFields().getColumn().size(); x++) {
			Column column = documentMetaData.getTableMetaData().getTableFields().getColumn().get(x);
			String tempName = column.getName().toUpperCase();
			//Warranty Defect#12027 - to get the last updated skipping the created person and last update person in where clause
			if (!tempName.equalsIgnoreCase("DT_LAST_UPDATE") && !ServiceConstants.ID_CREATED_PRSN.equalsIgnoreCase(tempName)
					&& !ServiceConstants.ID_LAST_UPDATE_PRSN.equalsIgnoreCase(tempName)) {
				if (x != 0) {
					getTimeStampQuery.append(" AND ");
				}
				getTimeStampQuery.append( column.getName());
				getTimeStampQuery.append(" = ?");
				paramcount++;
				parameterMap.put(paramcount, column);
			}

		}

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getTimeStampQuery.toString());
		boolean isValid = false;
		for (Entry<Integer, Column> entry : parameterMap.entrySet()) {
			Column column = entry.getValue();
			if (null != column) {
				query.setParameter(entry.getKey(), column.getContent());
				isValid = (!ObjectUtils.isEmpty(column.getContent()) && Long.valueOf(column.getContent()) >= 0)
						? Boolean.TRUE : Boolean.FALSE;
				if (!isValid)
					break;
			}
		}
		List<Date> datelist = (isValid) ? (ArrayList<Date>) query.list() : null;
		return !ObjectUtils.isEmpty(datelist) ? datelist.get(0) : null;
	}

	@Override
	public DocumentMetaData saveIntakeReport(DocumentMetaData documentMetaData, byte[] documentData, Long caseId) {

		return null;
	}

	@Override
	public DocumentMetaData saveFormEvent(DocumentMetaData documentMetaData, byte[] documentData) {

		return null;
	}

	/**
	 * 
	 * Method Name: getTemplateId Method Description:Method used to get the
	 * template id
	 * 
	 * @param templateType
	 * @return
	 */
	public Long getTemplateId(String templateType) {
		return (Long) sessionFactory.getCurrentSession().createSQLQuery(getTemplateIdSql.toString())
				.setString("templateType", templateType).uniqueResult();

	}

	@Override
	public DocumentLogDto selectDocument(DocumentMetaData documentMetaData) {
		StringBuilder selectDocumentsql = new StringBuilder();
		selectDocumentsql
				.append(selectDocumentSql.replaceFirst("%col", documentMetaData.getTableMetaData().getNarrativeColumn())
						.replaceFirst("%table", documentMetaData.getTableMetaData().getTableName()));

		for (int i = 0; i < documentMetaData.getTableMetaData().getTableFields().getColumn().size(); i++) {
			Column column = documentMetaData.getTableMetaData().getTableFields().getColumn().get(i);
			String tempName = column.getName().toUpperCase();
			if (!tempName.equals("DT_LAST_UPDATE")) {
				if (i != 0) {
					selectDocumentsql.append(" AND ");
				}
				selectDocumentsql.append(column.getName());
				selectDocumentsql.append('=');
				selectDocumentsql.append(column.getContent());
			}
		}
		DocumentLogDto documentLogDtoFrmQuery = (DocumentLogDto) sessionFactory.getCurrentSession()
				.createSQLQuery(selectDocumentsql.toString()).addScalar("idDocTmplt", StandardBasicTypes.LONG)
				.addScalar("nbrPrefillLngth", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(DocumentLogDto.class)).uniqueResult();
		DocumentLogDto doc = new DocumentLogDto();
		if (null != documentLogDtoFrmQuery) {
			try {
				doc = (DocumentLogDto) documentLogDtoFrmQuery;
			} catch (HibernateException e) {
				FormsDaoImpllog.error(e.getMessage());
			}
		}
		return doc;
	}

	@Override
	public DocumentMetaData selectNewTemplate(DocumentMetaData documentMetaData) {
		DocumentMetaData docMetaData = new DocumentMetaData();
		Long templateVers = (Long) sessionFactory.getCurrentSession().createSQLQuery(getTemplateIdSql)
				.addScalar("ID_DOCUMENT_TEMPLATE", StandardBasicTypes.LONG)
				.setParameter("templateType", documentMetaData.getTemplateType().toLowerCase()).uniqueResult();
		if (0 != templateVers) {
			docMetaData.setActualTemplateVersion(templateVers);
		}
		return docMetaData;
	}

	/**
	 * The method below is not accessed from the application and does not have
	 * permissions to be used from the Application, hence commenting it out, if
	 * required in future.
	 */
	/*
	 * @Override public ArrayList<DocumentEventDto>
	 * selectFBADocuments(DocumentMetaData documentMetaData) {
	 * 
	 * DocumentEventDto docEvent = new DocumentEventDto();
	 * ArrayList<DocumentEventDto> docEventList = new
	 * ArrayList<DocumentEventDto>();
	 * 
	 * StringBuffer fbaDocsql = new StringBuffer(); fbaDocsql.append(
	 * "SELECT A.DT_LAST_UPDATE as dtLastUpdate, " + "LENGTH(A." +
	 * documentMetaData.getTableMetaData().getNarrativeColumn()
	 * +") idEvent FROM " ); fbaDocsql.append("FBA_" +
	 * documentMetaData.getTableMetaData().getTableName() + " A, ");
	 * fbaDocsql.append("(SELECT DISTINCT ");
	 * fbaDocsql.append(documentMetaData.getTableMetaData().getTableFields().
	 * getColumn(0).getName().toUpperCase() );
	 * fbaDocsql.append(" ,MAX(DT_LAST_UPDATE) DT_LAST_UPDATE ,  LENGTH(" +
	 * documentMetaData.getTableMetaData().getNarrativeColumn()+ ") FROM " );
	 * fbaDocsql.append("FBA_" +
	 * documentMetaData.getTableMetaData().getTableName());
	 * fbaDocsql.append(" WHERE " +
	 * documentMetaData.getTableMetaData().getTableFields().getColumn(0).getName
	 * ().toUpperCase() + " = " +
	 * documentMetaData.getTableMetaData().getTableFields().getColumn(0).
	 * getContent()); fbaDocsql.append( " GROUP BY LENGTH(" +
	 * documentMetaData.getTableMetaData().getNarrativeColumn() + "), " +
	 * documentMetaData.getTableMetaData().getTableFields().getColumn(0).getName
	 * ().toUpperCase() + " )b"); fbaDocsql.append(" WHERE A." +
	 * documentMetaData.getTableMetaData().getTableFields().getColumn(0).getName
	 * ().toUpperCase() + " = B." +
	 * documentMetaData.getTableMetaData().getTableFields().getColumn(0).getName
	 * ().toUpperCase() + " AND A.DT_LAST_UPDATE = B.DT_LAST_UPDATE ");
	 * fbaDocsql.append(" ORDER BY DT_LAST_UPDATE DESC");
	 * 
	 * 
	 * DocumentEventDto documentEventDtofrmQuery = (DocumentEventDto)
	 * sessionFactory.getCurrentSession().createSQLQuery(fbaDocsql.toString())
	 * .addScalar("dtLastUpdate", StandardBasicTypes.DATE) .addScalar("idEvent",
	 * StandardBasicTypes.LONG)
	 * .setResultTransformer(Transformers.aliasToBean(DocumentEventDto.class)).
	 * uniqueResult(); if(null != documentEventDtofrmQuery) { try {
	 * 
	 * DocumentEventDto docEventdto = (DocumentEventDto)
	 * documentEventDtofrmQuery;
	 * docEvent.setDateLastUpdate(docEventdto.getDateLastUpdate());
	 * docEvent.setIdEvent(docEventdto.getIdEvent());
	 * docEventList.add(docEvent);
	 * 
	 * } catch (HibernateException e) { FormsDaoImpllog.error(e.getMessage()); }
	 * } return docEventList; }
	 */

	/**
	 * Method Description: his method gets the primary key for tableMetaData.
	 * 
	 * @param tableName
	 * @return String
	 */
	@Override
	public String getDatabaseMetaDataForPk(String tableName) {
		String pkey = "";
		if (!ServiceConstants.EMPTY_STRING.equalsIgnoreCase(tableName)) {
			pkey = (String) sessionFactory.getCurrentSession().createSQLQuery(getDatabaseMetaDataForPk)
					.setParameter("tableName", tableName).uniqueResult();
		}
		return pkey;
	}

	/**
	 * 
	 * Method Name: saveDocumentLog Method Description:Method used to save the
	 * form details into document logger
	 * 
	 * @param documentLogDto
	 * @return String
	 */
	@Override
	public String saveDocumentLog(DocumentLogDto documentLogDto) {
		if(mobileUtil.isMPSEnvironment()){
			return "Failed, MPS";
		}
		String isSuccess = "";
		DocLog docLog = new DocLog();
		BeanUtils.copyProperties(documentLogDto, docLog);
		docLog.setDtCreated(new Date());
		if (!ObjectUtils.isEmpty(documentLogDto)) {
			sessionFactory.getCurrentSession().saveOrUpdate(docLog);
			isSuccess = ServiceConstants.SUCCESS;
		}
		return isSuccess;
	}

	/**
	 * 
	 * Method Name: completePlcmntFstrResdntCareNarr Method Description: Update
	 * PlcmntFstrResdntCareNarr When User clicks save and submit button.
	 * 
	 * @param PlcmntFstrResdntCareNarrDto
	 *            plcmntFstrResdntCareNarrDto
	 */
	@Override
	public Boolean completePlcmntFstrResdntCareNarr(PlcmntFstrResdntCareNarrDto plcmntFstrResdntCareNarrDto,Long idPerson) {
		PlcmntFstrResdntCareNarr plcmntFstrResdntCareNarr = (PlcmntFstrResdntCareNarr) sessionFactory
				.getCurrentSession().createCriteria(PlcmntFstrResdntCareNarr.class)
				.add(Restrictions.eq("idEvent", plcmntFstrResdntCareNarrDto.getIdEvent()))
				.add(Restrictions.eq("idDocumentTemplate", plcmntFstrResdntCareNarrDto.getIdDocumentTemplate()))
				.uniqueResult();
		if (!ObjectUtils.isEmpty(plcmntFstrResdntCareNarr)) {
			plcmntFstrResdntCareNarr.setIndSaved(ServiceConstants.Y);
			plcmntFstrResdntCareNarr.setDtLastUpdate(new Date());
			//Warranty Defect#11830 - Updating the userid in the narrative table
			plcmntFstrResdntCareNarr.setIdCreatedPerson(idPerson);
			plcmntFstrResdntCareNarr.setIdLastUpdatePerson(idPerson);
			sessionFactory.getCurrentSession().saveOrUpdate(plcmntFstrResdntCareNarr);
			return true;
		}
		return false;
	}

	/**
	 * 
	 * Method Name: getPlcmntFstrResdntCareNarr Method Description: Get
	 * PlcmntFstrResdntCareNarr
	 * 
	 * @param idEvent
	 * @return List<PlcmntFstrResdntCareNarrDto>
	 */
	@Override
	public List<PlcmntFstrResdntCareNarrDto> getPlcmntFstrResdntCareNarr(Long idEvent) {
		List<PlcmntFstrResdntCareNarrDto> plcmntFstrResdntCareNarrDtoList = new ArrayList();
		List<PlcmntFstrResdntCareNarr> plcmntFstrResdntCareNarrList = (List<PlcmntFstrResdntCareNarr>) sessionFactory
				.getCurrentSession().createCriteria(PlcmntFstrResdntCareNarr.class)
				.add(Restrictions.eq("idEvent", idEvent)).list();
		if (CollectionUtils.isNotEmpty(plcmntFstrResdntCareNarrList)) {
			plcmntFstrResdntCareNarrList.stream().forEach(plcmntFstrResdntCareNarr -> {
				PlcmntFstrResdntCareNarrDto plcmntFstrResdntCareNarrDto = new PlcmntFstrResdntCareNarrDto();
				if (ObjectUtils.isEmpty(plcmntFstrResdntCareNarr.getIndSaved()))
					plcmntFstrResdntCareNarrDto.setIndSaved(false);
				else
					plcmntFstrResdntCareNarrDto.setIndSaved(true);
				plcmntFstrResdntCareNarrDto.setIdDocumentTemplate(plcmntFstrResdntCareNarr.getIdDocumentTemplate());
				plcmntFstrResdntCareNarrDto.setIdEvent(plcmntFstrResdntCareNarr.getIdEvent());
				// Get id document template type
				DocumentTemplate documentTemplate = (DocumentTemplate) sessionFactory.getCurrentSession()
						.createCriteria(DocumentTemplate.class)
						.add(Restrictions.eq("idDocumentTemplate", plcmntFstrResdntCareNarr.getIdDocumentTemplate()))
						.uniqueResult();
				plcmntFstrResdntCareNarrDto.setIdDocumentTemplateType(
						documentTemplate.getDocumentTemplateType().getIdDocumentTemplateType());
				plcmntFstrResdntCareNarrDtoList.add(plcmntFstrResdntCareNarrDto);
			});

		}
		return plcmntFstrResdntCareNarrDtoList;
	}

	/**
	 * 
	 * Method Name: deletePlcmntFstrResdntCareNarr Method Description: Delete
	 * PlcmntFstrResdntCareNarr Record
	 * 
	 * @param idEvent
	 * @param idDocumentTemplate
	 */
	@Override
	public Boolean deletePlcmntFstrResdntCareNarr(Long idEvent, Long idDocumentTemplate) {
		PlcmntFstrResdntCareNarr plcmntFstrResdntCareNarr = (PlcmntFstrResdntCareNarr) sessionFactory
				.getCurrentSession().createCriteria(PlcmntFstrResdntCareNarr.class)
				.add(Restrictions.eq("idEvent", idEvent)).add(Restrictions.eq("idDocumentTemplate", idDocumentTemplate))
				.uniqueResult();
		if (!ObjectUtils.isEmpty(plcmntFstrResdntCareNarr)) {
			sessionFactory.getCurrentSession().delete(plcmntFstrResdntCareNarr);
			return true;
		}
		return false;

	}

	/**
	 * 
	 * Method Name: deleteHandwrittenDataByIdEvent Method Description: Delete
	 * Handwritten Data by ID Event
	 * 
	 * @param idEvent
	 *
	 */
	@Override
	public Boolean deleteHandwrittenDataByIdEvent(Long idEvent) {
		Boolean result = Boolean.FALSE;
		List<HandwrittenData> handwrittenData = (List<HandwrittenData>) sessionFactory.getCurrentSession()
				.createCriteria(HandwrittenData.class).add(Restrictions.eq("idEvent", idEvent)).list();

		if (!ObjectUtils.isEmpty(handwrittenData)) {
			for (HandwrittenData row : handwrittenData) {
				sessionFactory.getCurrentSession().delete(row);
			}
			result = Boolean.TRUE;
		}

		return result;

	}

	/**
	 * 
	 * Method Name: documentTemplateCheck. Method Description: This Method is
	 * used to check the document template is Legacy or Impact Phase 2 template.
	 * 
	 * @param documentTmpltCheckDto
	 * @param DocumentTemplateDto
	 */
	public DocumentTemplateDto documentTemplateCheck(DocumentTmpltCheckDto documentTmpltCheckDto) {
		DocumentTemplateDto documentTemplateDto = new DocumentTemplateDto();
		// Updated the query as part of SPD-88
		documentTemplateDto = (DocumentTemplateDto) sessionFactory.getCurrentSession()
				.createSQLQuery(documentTemplateCheckQuery).addScalar("idTemplate", StandardBasicTypes.LONG)
				.addScalar("idType", StandardBasicTypes.LONG).addScalar("active", StandardBasicTypes.STRING)
				.addScalar("indLgcy", StandardBasicTypes.STRING)
				.setParameter("idEvent", documentTmpltCheckDto.getNarrEventId())
				.setResultTransformer(Transformers.aliasToBean(DocumentTemplateDto.class)).uniqueResult();
		return documentTemplateDto;
	}

	/**
	 * 
	 * Method Name: copyNarrativeDocForNewUsing. Method Description: This Method
	 * is used to copy the existing narrative document to new event.
	 * 
	 * @param idNewEvent
	 * @param idPrevEvent
	 * @param tableName
	 */
	public void copyNarrativeDocForNewUsing(Long idNewEvent, Long idPrevEvent, String tableName) {
		NarrativeTableDto narrativeTableDto = new NarrativeTableDto();
		String selectNarrDocQuery = selectExistNarrDocSql.replaceFirst("%tableName", tableName);
		if(mobileUtil.isMPSEnvironment()){
			selectNarrDocQuery = selectNarrDocQuery.replaceAll("narrativeBlob","narrativeBytes");
		}
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectNarrDocQuery)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idDocTemplate", StandardBasicTypes.LONG)
				.setParameter("idEvent", idPrevEvent);
		if(mobileUtil.isMPSEnvironment()){
			query.addScalar("narrativeBytes", StandardBasicTypes.BINARY);
		} else {
			query.addScalar("narrativeBlob", StandardBasicTypes.BLOB);
		}
		narrativeTableDto = (NarrativeTableDto) query
				.setResultTransformer(Transformers.aliasToBean(NarrativeTableDto.class)).uniqueResult();

		if (!ObjectUtils.isEmpty(narrativeTableDto.getNarrativeBlob()) && !mobileUtil.isMPSEnvironment()) {
			try {
				narrativeTableDto.setNarrativeBytes(getBytes(narrativeTableDto.getNarrativeBlob().getBinaryStream()));
			} catch (SQLException e) {
				FormsDaoImpllog.error(e.getMessage());
				DataLayerException dataLayerException = new DataLayerException(e.getMessage());
				dataLayerException.initCause(e);
				throw dataLayerException;
			}
		}

		String insertNarrDocQuery = insertExistNarrDocSql.replaceFirst("%tableName", tableName);
		SQLQuery insertNarr = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(insertNarrDocQuery)
				.setParameter("idEvent", idNewEvent).setParameter("dtLastUpdate", new Date())
				.setParameter("idCase", narrativeTableDto.getIdCase())
				.setParameter("narrativeBlob", narrativeTableDto.getNarrativeBytes())
				.setParameter("idDocTemplate", narrativeTableDto.getIdDocTemplate());
		insertNarr.executeUpdate();
	}
}