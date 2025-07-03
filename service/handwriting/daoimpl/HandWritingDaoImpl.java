package us.tx.state.dfps.service.handwriting.daoimpl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.HandwrittenData;
import us.tx.state.dfps.service.casepackage.dto.HWKeyEventDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.handwriting.util.HandWritingUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.handwriting.dao.HandWritingDao;
import us.tx.state.dfps.service.handwriting.dto.HandWritingDto;
import us.tx.state.dfps.service.handwriting.dto.HandWritingLinkedNotesDto;
import us.tx.state.dfps.service.handwriting.dto.HandWritingStageDto;
import us.tx.state.dfps.service.handwriting.dto.HandWritingValueDto;
import us.tx.state.dfps.service.sdmriskassessment.dto.StageDBDto;

/**
 * Dao implementation for functions required for implementing handwriting
 * functionality
 *
 */
@Repository
public class HandWritingDaoImpl implements HandWritingDao {

	/*
	 * @Value("${HandWritingDaoImpl.updateHandWrittenData}") private transient
	 * String updateHandWrittenDataSql;
	 * 
	 * @Value("${HandWritingDaoImpl.updateHandwritingKeyAndEventWithNew}")
	 * private transient String updateHandwritingKeyAndEventWithNewSql;
	 */

	@Autowired
	private SessionFactory sessionFactory;

	public HandWritingDaoImpl() {
		// Default constructor
	}

	private static final Logger logger = Logger.getLogger("HandWritingDaoImpl Class");

	@Value("${HandWritingDaoImpl.fetchEventStageInfo}")
	private transient String fetchEventStageInfo;

	@Value("${HandWritingDaoImpl.fetchHandwritableFieldList}")
	private transient String fetchHandwritableFieldList;

	@Value("${HandWritingDaoImpl.fetchHandwrittenDataForEvent}")
	private transient String fetchHandwrittenDataForEvent;

	@Value("${HandWritingDaoImpl.fetchHandwrittenDataForKeyEstm}")
	private transient String fetchHandwrittenDataForKeyEstm;

	@Value("${HandWritingDaoImpl.fetchHandwrittenDataForKey}")
	private transient String fetchHandwrittenDataForKey;

	@Value("${HandWritingDaoImpl.fetchLinkedNotesForStages}")
	private transient String fetchLinkedNotesForStages;

	@Value("${HandWritingDaoImpl.fetchStageName}")
	private transient String fetchStageName;

	@Value("${HandWritingDaoImpl.fetchStageInfo}")
	private transient String fetchStageInfo;

	@Value("${HandWritingDaoImpl.fetchCaseIdForStageId}")
	private transient String fetchCaseIdForStageId;

	@Value("${HandWritingDaoImpl.fetchEventforContact}")
	private transient String fetchEventforContact;

	@Autowired
	private MessageSource messageSource;

	@Override
	public long updateHandWrittenData(HandWritingDto handWritingDto) {

		Blob blobSignatureImg = null;
		HandwrittenData handWrittenData = new HandwrittenData();
		if (!ObjectUtils.isEmpty(handWritingDto.getImg())) {
			try {
				blobSignatureImg = new SerialBlob(handWritingDto.getImg());
			} catch (SerialException e) {
				logger.error(e.getMessage());
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
			handWrittenData.setImgHandwrittenData(blobSignatureImg);
		}

		Date date = new Date();
		handWrittenData.setIdHandwrittenData(handWritingDto.getDataKey());
		handWrittenData.setDtLastUpdate(date);

		handWrittenData.setDtCreated(date);
		handWrittenData.setIdEvent(new Long(handWritingDto.getIdEvent()));
		handWrittenData.setIdStage(new Long(handWritingDto.getIdStage()));
		handWrittenData.setCdStage(handWritingDto.getCdStage());
		handWrittenData.setIdCase(new Long(handWritingDto.getIdCase()));
		handWrittenData.setCdNotesType(handWritingDto.getCdNotesType());
		handWrittenData.setTxtFieldName(handWritingDto.getTxtFieldName());
		sessionFactory.getCurrentSession().saveOrUpdate(handWrittenData);
		return 1L;
	}

	public long getEventforContact(HandWritingDto handWritingDto) {

		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(fetchEventforContact);
		query.addScalar("idEvent", StandardBasicTypes.LONG).setParameter("idCase", handWritingDto.getIdCase())
				.setParameter("idStage", handWritingDto.getIdStage());

		Long idEvent = (Long) query.uniqueResult();
		return idEvent;
	}

	@Override
	public long updateHandwritingKeyAndEventWithNew(HWKeyEventDto hwKeyEventDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(HandwrittenData.class);
		criteria.add(Restrictions.eq("id.idHandwrittenData", hwKeyEventDto.getOldKey()));
		HandwrittenData handwrittenData = (HandwrittenData) criteria.uniqueResult();
		handwrittenData.setTxtFieldName(hwKeyEventDto.getsNewField());
		handwrittenData.setIdEvent(new Long(hwKeyEventDto.getEventId()));
		handwrittenData.setIdHandwrittenData(hwKeyEventDto.getNewKey());
		sessionFactory.getCurrentSession().saveOrUpdate(handwrittenData);
		return criteria.list().size();

	}

	@Override
	public int updateHandwritingKeyWithNew(HWKeyEventDto hwKeyEventDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(HandwrittenData.class);
		criteria.add(Restrictions.eq("id.idHandwrittenData", hwKeyEventDto.getOldKey()));
		HandwrittenData handwrittenDataResult = (HandwrittenData) criteria.uniqueResult();
		handwrittenDataResult.setTxtFieldName(hwKeyEventDto.getsNewField());
		handwrittenDataResult.setIdHandwrittenData(hwKeyEventDto.getNewKey());
		sessionFactory.getCurrentSession().saveOrUpdate(handwrittenDataResult);
		return criteria.list().size();
	}

	@Override
	public int updateHandwrittenFieldViewed(String idWrittenData) {
		return 0;
	}

	@Override
	public int saveHandwrttenData(HandWritingDto handWritingDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(HandwrittenData.class);
		criteria.add(Restrictions.eq("idHandwrittenData", handWritingDto.getDataKey()));
		HandwrittenData handwrittenData = (HandwrittenData) criteria.uniqueResult();
		Date date = new Date();
		handwrittenData.setDtLastUpdate(date);
		byte[] bs = handWritingDto.getBinTranslatedNotes().getBytes();
		Blob blobTransNotes = null;
		Blob blobSignatureImg = null;
		try {
			blobTransNotes = new SerialBlob(bs);
			blobSignatureImg = new SerialBlob(handWritingDto.getImg());
		} catch (SerialException e) {
			logger.error(e.getMessage());
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		handwrittenData.setImgHandwrittenData(blobSignatureImg);
		handwrittenData.setBinTranslatedNotes(blobTransNotes);
		sessionFactory.getCurrentSession().saveOrUpdate(handwrittenData);
		return ServiceConstants.ONE_INT;
	}

	@SuppressWarnings("unchecked")
	@Override
	public long deleteHandwrittenData(HandWritingDto handWritingDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(HandwrittenData.class);
		criteria.add(Restrictions.eq("id.idHandwrittenData", handWritingDto.getDataKey()));
		List<HandwrittenData> handWrittenDatas = criteria.list();
		for (HandwrittenData handWrittenToDel : handWrittenDatas) {
			sessionFactory.getCurrentSession().delete(handWrittenToDel);
		}
		return criteria.list().size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public long deleteHandwrittenDataForEvent(HandWritingDto handWritingDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(HandwrittenData.class);
		criteria.add(Restrictions.eq("id.idEvent", new BigDecimal(handWritingDto.getIdEvent())));
		List<HandwrittenData> handWrittenDataList = criteria.list();
		for (HandwrittenData handWrittenToDel : handWrittenDataList) {
			sessionFactory.getCurrentSession().delete(handWrittenToDel);
		}
		return criteria.list().size();
	}

	@Override
	public long deleteUsedHandwrittenData(String string) {
		HandwrittenData handWrittenData = new HandwrittenData();
		handWrittenData.setIdHandwrittenData(string);
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(HandwrittenData.class);
		criteria.add(Restrictions.eq("idHandwrittenData", string));
		List<HandwrittenData> handwrittenDatas = criteria.list();
		for (HandwrittenData handwrittenToDel : handwrittenDatas) {
			sessionFactory.getCurrentSession().delete(handwrittenToDel);
		}
		return criteria.list().size();

	}

	/**
	 * Method Name: fetchEventStageInfo Method Description: Fetches
	 * miscellaneous Event information and some stage information based on an
	 * Event
	 * 
	 * @param eventId
	 * @return handWritingValueDto
	 */
	@Override
	public HandWritingValueDto fetchEventStageInfo(Long eventId) {
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(fetchEventStageInfo)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("cdStage", StandardBasicTypes.STRING).setParameter("idEvent", eventId)
				.setResultTransformer(Transformers.aliasToBean(HandWritingValueDto.class));
		HandWritingValueDto handWritingValueDto = (HandWritingValueDto) query.uniqueResult();
		return handWritingValueDto;
	}

	/**
	 * Method Name: fetchHandwritableFieldList Method Description: This method
	 * fetches the list of fields on which handwriting need to be provided This
	 * method is used only for fetching handwritable fields in forms
	 * 
	 * @param sDocType
	 * @return handWritingValueDtoList
	 */
	@Override
	public List<HandWritingValueDto> fetchHandwritableFieldList(String sDocType) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchHandwritableFieldList)
				.addScalar("txtDocType", StandardBasicTypes.STRING).addScalar("txtFieldName", StandardBasicTypes.STRING)
				.addScalar("cdNotesType", StandardBasicTypes.STRING).setParameter("sDocType", sDocType)
				.setResultTransformer(Transformers.aliasToBean(HandWritingValueDto.class));
		List<HandWritingValueDto> handWritingValueDtoList = sqlQuery.list();
		if (TypeConvUtil.isNullOrEmpty(handWritingValueDtoList)) {
			throw new DataNotFoundException(
					messageSource.getMessage("handwritingFieldsValue.is.null", null, Locale.US));
		}
		return handWritingValueDtoList;
	}

	/**
	 * Method Name: fetchHandwrittenDataForEvent Method Description: This method
	 * fetches handwritten data for an event
	 * 
	 * @param eventId
	 * @param bFetchImage
	 * @return filteredHandWritingValueDtoList @
	 */
	@Override
	public List<HandWritingValueDto> fetchHandwrittenDataForEvent(Long eventId, boolean bfetchImage) {
		List<HandWritingValueDto> filteredHandWritingValueDtoList = new ArrayList<>();
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchHandwrittenDataForEvent)
				.addScalar("txtKeyValue", StandardBasicTypes.STRING).addScalar("cdNotesType", StandardBasicTypes.STRING)
				.addScalar("txtTranslatedNotes", StandardBasicTypes.BLOB)
				.addScalar("blobImgHandwrittenData", StandardBasicTypes.BLOB)
				.addScalar("txtFieldName", StandardBasicTypes.STRING).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("strIsConverted", StandardBasicTypes.STRING).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("idFamilyPlanNarr", StandardBasicTypes.LONG)
				.setParameter("idEvent", eventId)
				.setResultTransformer(Transformers.aliasToBean(HandWritingStageDto.class));
		try {
			List<HandWritingStageDto> handWritingStageDtos = sqlQuery.list();
			for (HandWritingStageDto handWritingStageDtos2 : handWritingStageDtos) {
				HandWritingValueDto sethandWritingValueDto = new HandWritingValueDto();
				sethandWritingValueDto.setIdFamilyPlanNarr(handWritingStageDtos2.getIdFamilyPlanNarr());
				sethandWritingValueDto.setTxtKeyValue(handWritingStageDtos2.getTxtKeyValue());
				sethandWritingValueDto.setCdNotesType(handWritingStageDtos2.getCdNotesType());
				Blob txtTranslatedNotesBlob = handWritingStageDtos2.getTxtTranslatedNotes();
				if (!TypeConvUtil.isNullOrEmpty(txtTranslatedNotesBlob)) {
					byte[] txtTranslatedNotesByte;
					txtTranslatedNotesByte = txtTranslatedNotesBlob.getBytes(1, (int) txtTranslatedNotesBlob.length());
					String txtTranslatedNotesString = new String(txtTranslatedNotesByte);
					sethandWritingValueDto.setTxtTranslatedNotes(txtTranslatedNotesString);
				}
				sethandWritingValueDto.setTxtFieldName(handWritingStageDtos2.getTxtFieldName());
				sethandWritingValueDto.setIdEvent(handWritingStageDtos2.getIdEvent());
				sethandWritingValueDto.setIdCase(handWritingStageDtos2.getIdCase());
				sethandWritingValueDto.setIdStage(handWritingStageDtos2.getIdStage());
				sethandWritingValueDto.setDtCreated(handWritingStageDtos2.getDtCreated());
				sethandWritingValueDto.setDtLastUpdate(handWritingStageDtos2.getDtLastUpdate());
				sethandWritingValueDto.setNmStage(handWritingStageDtos2.getNmStage());
				if (handWritingStageDtos2.getStrIsConverted().equals(ServiceConstants.Y)) {
					sethandWritingValueDto.setIsConverted(Boolean.TRUE);
				} else {
					sethandWritingValueDto.setIsConverted(Boolean.FALSE);
				}
				if (bfetchImage && !ObjectUtils.isEmpty(handWritingStageDtos2.getBlobImgHandwrittenData())) {
					sethandWritingValueDto.setImgHandwrittenData(
							getBytes(handWritingStageDtos2.getBlobImgHandwrittenData().getBinaryStream()));
				}

				if (!HandWritingUtils.isTemporaryNote(sethandWritingValueDto)) {
					filteredHandWritingValueDtoList.add(sethandWritingValueDto);
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return filteredHandWritingValueDtoList;
	}

	/**
	 * Method Name: fetchHandwrittenDataForKey Method Description: This method
	 * fetches handwritten data for a key value
	 * 
	 * @param dataKey
	 * @param bfetchImage
	 * @return List<HandWritingValueDto>
	 */
	@Override
	public List<HandWritingValueDto> fetchHandwrittenDataForKey(String dataKey, Boolean bfetchImage) {
		List<HandWritingValueDto> filteredHandWritingValueDtoList = new ArrayList<>();
		try {
			if (dataKey.startsWith(ServiceConstants.ESTM)) {
				SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession()
						.createSQLQuery(fetchHandwrittenDataForKeyEstm)
						.addScalar("txtKeyValue", StandardBasicTypes.STRING)
						.addScalar("cdNotesType", StandardBasicTypes.STRING)
						.addScalar("txtTranslatedNotes", StandardBasicTypes.BLOB)
						.addScalar("blobImgHandwrittenData", StandardBasicTypes.BLOB)
						.addScalar("txtFieldName", StandardBasicTypes.STRING)
						.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
						.addScalar("idStage", StandardBasicTypes.LONG)
						.addScalar("strIsConverted", StandardBasicTypes.STRING)
						.addScalar("dtCreated", StandardBasicTypes.DATE)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("nmStage", StandardBasicTypes.STRING).setParameter("dataKey", dataKey)
						.setResultTransformer(Transformers.aliasToBean(HandWritingStageDto.class));
				List<HandWritingStageDto> handWritingStageDtos = sqlQuery.list();
				for (HandWritingStageDto handWritingStageDtos2 : handWritingStageDtos) {
					HandWritingValueDto sethandWritingValueDto = new HandWritingValueDto();
					sethandWritingValueDto.setTxtKeyValue(handWritingStageDtos2.getTxtKeyValue());
					sethandWritingValueDto.setCdNotesType(handWritingStageDtos2.getCdNotesType());
					Blob txtTranslatedNotesBlob = handWritingStageDtos2.getTxtTranslatedNotes();
					if (!TypeConvUtil.isNullOrEmpty(txtTranslatedNotesBlob)) {
						byte[] txtTranslatedNotesByte = txtTranslatedNotesBlob.getBytes(1,
								(int) txtTranslatedNotesBlob.length());
						String txtTranslatedNotesString = new String(txtTranslatedNotesByte);
						sethandWritingValueDto.setTxtTranslatedNotes(txtTranslatedNotesString);
					}
					sethandWritingValueDto.setTxtFieldName(handWritingStageDtos2.getTxtFieldName());
					sethandWritingValueDto.setIdEvent(handWritingStageDtos2.getIdEvent());
					sethandWritingValueDto.setIdCase(handWritingStageDtos2.getIdCase());
					sethandWritingValueDto.setIdStage(handWritingStageDtos2.getIdStage());
					sethandWritingValueDto.setDtCreated(handWritingStageDtos2.getDtCreated());
					sethandWritingValueDto.setDtLastUpdate(handWritingStageDtos2.getDtLastUpdate());
					sethandWritingValueDto.setNmStage(handWritingStageDtos2.getNmStage());

					if (handWritingStageDtos2.getStrIsConverted().equals(ServiceConstants.Y)) {
						sethandWritingValueDto.setIsConverted(Boolean.TRUE);
					} else {
						sethandWritingValueDto.setIsConverted(Boolean.FALSE);
					}
					if (bfetchImage && !ObjectUtils.isEmpty(handWritingStageDtos2.getBlobImgHandwrittenData())) {
						sethandWritingValueDto.setImgHandwrittenData(
								getBytes(handWritingStageDtos2.getBlobImgHandwrittenData().getBinaryStream()));
					}

					if (!HandWritingUtils.isTemporaryNote(sethandWritingValueDto)) {
						filteredHandWritingValueDtoList.add(sethandWritingValueDto);
					}
				}
			} else {
				SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession()
						.createSQLQuery(fetchHandwrittenDataForKeyEstm)
						.addScalar("txtKeyValue", StandardBasicTypes.STRING)
						.addScalar("cdNotesType", StandardBasicTypes.STRING)
						.addScalar("txtTranslatedNotes", StandardBasicTypes.BLOB)
						.addScalar("blobImgHandwrittenData", StandardBasicTypes.BLOB)
						.addScalar("txtFieldName", StandardBasicTypes.STRING)
						.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
						.addScalar("idStage", StandardBasicTypes.LONG)
						.addScalar("strIsConverted", StandardBasicTypes.STRING)
						.addScalar("dtCreated", StandardBasicTypes.DATE)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("nmStage", StandardBasicTypes.STRING).setParameter("dataKey", dataKey)
						.setResultTransformer(Transformers.aliasToBean(HandWritingStageDto.class));
				List<HandWritingStageDto> handWritingStageDtos = sqlQuery.list();
				for (HandWritingStageDto handWritingStageDtos2 : handWritingStageDtos) {
					HandWritingValueDto sethandWritingValueDto = new HandWritingValueDto();
					sethandWritingValueDto.setTxtKeyValue(handWritingStageDtos2.getTxtKeyValue());
					sethandWritingValueDto.setCdNotesType(handWritingStageDtos2.getCdNotesType());
					Blob txtTranslatedNotesBlob = handWritingStageDtos2.getTxtTranslatedNotes();
					if (!TypeConvUtil.isNullOrEmpty(txtTranslatedNotesBlob)) {
						byte[] txtTranslatedNotesByte = txtTranslatedNotesBlob.getBytes(1,
								(int) txtTranslatedNotesBlob.length());
						String txtTranslatedNotesString = new String(txtTranslatedNotesByte);
						sethandWritingValueDto.setTxtTranslatedNotes(txtTranslatedNotesString);
					}
					sethandWritingValueDto.setTxtFieldName(handWritingStageDtos2.getTxtFieldName());
					sethandWritingValueDto.setIdEvent(handWritingStageDtos2.getIdEvent());
					sethandWritingValueDto.setIdCase(handWritingStageDtos2.getIdCase());
					sethandWritingValueDto.setIdStage(handWritingStageDtos2.getIdStage());
					sethandWritingValueDto.setDtCreated(handWritingStageDtos2.getDtCreated());
					sethandWritingValueDto.setDtLastUpdate(handWritingStageDtos2.getDtLastUpdate());
					sethandWritingValueDto.setNmStage(handWritingStageDtos2.getNmStage());

					if (handWritingStageDtos2.getStrIsConverted().equals(ServiceConstants.Y)) {
						sethandWritingValueDto.setIsConverted(Boolean.TRUE);
					} else {
						sethandWritingValueDto.setIsConverted(Boolean.FALSE);
					}
					if (bfetchImage && !ObjectUtils.isEmpty(handWritingStageDtos2.getBlobImgHandwrittenData())) {
						sethandWritingValueDto.setImgHandwrittenData(
								getBytes(handWritingStageDtos2.getBlobImgHandwrittenData().getBinaryStream()));
					}

					if (!HandWritingUtils.isTemporaryNote(sethandWritingValueDto)) {
						filteredHandWritingValueDtoList.add(sethandWritingValueDto);
					}
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return filteredHandWritingValueDtoList;
	}

	/**
	 * Method Name: fetchLinkedNotesForStages Method Description: This method is
	 * called at the time of stage Check-in when we want to check if handwritten
	 * notes linked to pages exist. So we fetch only notes which are linked to
	 * pages
	 * 
	 * @param stageList
	 * @return List<HandWritingValueDto>
	 */
	@Override
	public List<HandWritingValueDto> fetchLinkedNotesForStages(List<String> stageList) {
		StringBuilder stringBuilder = new StringBuilder(fetchLinkedNotesForStages);
		StringBuilder stringBuilder2 = new StringBuilder(ServiceConstants.SQUARE_BRACKET_OPEN);
		for (String stageLists : stageList) {
			if (stringBuilder2.toString().equals(ServiceConstants.SQUARE_BRACKET_OPEN)) {
				stringBuilder2.append((String) stageLists);
			} else {
				stringBuilder2.append(',').append((String) stageLists);
			}
		}
		stringBuilder2.append(')');
		stringBuilder.append(ServiceConstants.S_ID_STAGE);
		stringBuilder.append(stringBuilder2.toString());
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString())
				.addScalar("idHandwrittenData", StandardBasicTypes.STRING)
				.addScalar("dtCreated", StandardBasicTypes.DATE).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("txtFieldName", StandardBasicTypes.STRING)
				.addScalar("txtTaskDecode", StandardBasicTypes.STRING).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("nmCase", StandardBasicTypes.STRING)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("cdStage", StandardBasicTypes.STRING).addScalar("cdTask", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(HandWritingLinkedNotesDto.class));
		List<HandWritingLinkedNotesDto> handWritingLinkedNotesDtos = sqlQuery.list();
		List<HandWritingValueDto> handWritingValueDtoList = new ArrayList<HandWritingValueDto>();
		if (!TypeConvUtil.isNullOrEmpty(handWritingLinkedNotesDtos)) {
			for (HandWritingLinkedNotesDto handWritingLinkedNotesDtos2 : handWritingLinkedNotesDtos) {
				HandWritingValueDto sethandWritingValueDto = new HandWritingValueDto();
				sethandWritingValueDto.setTxtKeyValue(handWritingLinkedNotesDtos2.getIdHandwrittenData());
				sethandWritingValueDto.setTxtFieldName(handWritingLinkedNotesDtos2.getTxtFieldName());
				sethandWritingValueDto.setIdEvent(handWritingLinkedNotesDtos2.getIdEvent());
				sethandWritingValueDto.setIdCase(handWritingLinkedNotesDtos2.getIdCase());
				sethandWritingValueDto.setIdStage(handWritingLinkedNotesDtos2.getIdStage());
				sethandWritingValueDto.setCdTask(handWritingLinkedNotesDtos2.getCdTask());
				sethandWritingValueDto.setTxtTaskName(handWritingLinkedNotesDtos2.getTxtTaskDecode());
				sethandWritingValueDto.setNmCase(handWritingLinkedNotesDtos2.getNmCase());
				sethandWritingValueDto.setNmStage(handWritingLinkedNotesDtos2.getNmStage());
				sethandWritingValueDto.setCdStage(handWritingLinkedNotesDtos2.getCdStage());
				sethandWritingValueDto.setDtCreated(handWritingLinkedNotesDtos2.getDtCreated());
				sethandWritingValueDto.setDtLastUpdate(handWritingLinkedNotesDtos2.getDtLastUpdate());
				if (!HandWritingUtils.isTemporaryNote(sethandWritingValueDto)) {
					handWritingValueDtoList.add(sethandWritingValueDto);
				}
			}
		}
		return handWritingValueDtoList;
	}

	/**
	 * Method Name: fetchStageName Method Description: This method fetches the
	 * stage information
	 * 
	 * @param idStage
	 * @return String
	 */
	@Override
	public String fetchStageName(Long idStage) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchStageName)
				.addScalar("idWkldStage", StandardBasicTypes.STRING).addScalar("idWkldCase", StandardBasicTypes.LONG)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(HandWritingValueDto.class));
		List<HandWritingValueDto> handWritingValueDto = sqlQuery.list();
		if (!TypeConvUtil.isNullOrEmpty(handWritingValueDto)) {
			for (HandWritingValueDto handWritingValueDto2 : handWritingValueDto) {
				String stageName = handWritingValueDto2.getIdWkldStage();
				if (null != stageName) {
					return stageName;
				}
			}
		}
		return null;
	}

	/**
	 * Method Name: fetchStageInfo Method Description: This method fetches the
	 * stage information
	 * 
	 * @param idStage
	 * @return StageDBDto
	 */
	@Override
	public StageDBDto fetchStageInfo(Long idStage) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchStageInfo)
				.addScalar("caseId", StandardBasicTypes.LONG).addScalar("stageName", StandardBasicTypes.STRING)
				.addScalar("caseName", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(StageDBDto.class));
		StageDBDto stageDBDto = (StageDBDto) sqlQuery.uniqueResult();
		StageDBDto stageDB = new StageDBDto();
		stageDB.setStageId(idStage);
		if (!TypeConvUtil.isNullOrEmpty(stageDBDto)) {
			stageDB.setCaseName(stageDBDto.getCaseName());
			stageDB.setStageName(stageDBDto.getStageName());
			stageDB.setCaseId(stageDBDto.getCaseId());
		}
		return stageDB;
	}

	/**
	 * Method Name: fetchCaseIdForStageId Method Description: This method
	 * fetches the Case Id for a Stage Id
	 * 
	 * @param idStage
	 * @return Long
	 */
	@Override
	public Long fetchCaseIdForStageId(Long idStage) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchCaseIdForStageId)
				.addScalar("caseId", StandardBasicTypes.LONG).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(StageDBDto.class));
		StageDBDto stageDBDto = (StageDBDto) sqlQuery.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(stageDBDto)) {
			return stageDBDto.getCaseId();
		}
		return (long) 0;
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
			// FormsDaoImpllog.error(e.getMessage());
			DataLayerException dataLayerException = new DataLayerException(e.getMessage());
			dataLayerException.initCause(e);
			throw dataLayerException;
		}
		return byteOutputStream.toByteArray();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void updateFamilyPlanNarrToSignatures(Long idEvent, Long idFamilyPlanNarr) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(HandwrittenData.class);
		criteria.add(Restrictions.eq("idEvent", idEvent));
		List<HandwrittenData> signaturesList  = criteria.list();
		
		if(!CollectionUtils.isEmpty(signaturesList)) {
			signaturesList.forEach(handwrittenData->{
				if(ObjectUtils.isEmpty(handwrittenData.getIdFamilyPlanNarr())) {
					handwrittenData.setIdFamilyPlanNarr(idFamilyPlanNarr);
					sessionFactory.getCurrentSession().update(handwrittenData);
				}
				
			});
		}
	}
}