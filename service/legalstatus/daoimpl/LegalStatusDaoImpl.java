package us.tx.state.dfps.service.legalstatus.daoimpl;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.Eligibility;
import us.tx.state.dfps.common.domain.LegalStatus;
import us.tx.state.dfps.common.domain.LegalStatusSubType;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.SSCCExceptCareDesignationDto;
import us.tx.state.dfps.service.admin.dto.EligibilityByPersonInDto;
import us.tx.state.dfps.service.admin.dto.LegalActionEventInDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusDetailDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusInDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusOutDto;
import us.tx.state.dfps.service.admin.dto.ResourcePlacementOutDto;
import us.tx.state.dfps.service.admin.dto.WorkLoadDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.kin.dto.KinChildDto;
import us.tx.state.dfps.service.legalstatus.dao.LegalStatusDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Legal
 * Status Dao Impl layer> Feb 8, 2018- 2:07:08 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class LegalStatusDaoImpl implements LegalStatusDao {

	private static final String STRING2 = ",";

	private static final String ORDER_BY_L_DT_LEGAL_ACT_OUTCOME_DT_DESC_L_ID_LEGAL_ACT_EVENT_DESC = " ORDER BY L.DT_LEGAL_ACT_OUTCOME_DT DESC ,L.ID_LEGAL_ACT_EVENT DESC FETCH FIRST ROW ONLY";

	private static final String STRING = ")";
	private static final String AND_L_CD_LEGAL_ACT_ACTN_SUBTYPE_IN = " AND L.CD_LEGAL_ACT_ACTN_SUBTYPE IN (";

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${LegalStatusDaoImpl.getLegalStatus}")
	private String getLegalStatus;

	@Value("${LegalStatusDaoImpl.getLegalStatusSubType}")
	private String getLegalStatusSubType;

	@Value("${LegalStatusDaoImpl.getLegalStatusCount}")
	private String getLegalStatusCount;

	@Value("${LegalStatusDaoImpl.getEligibilityEvent}")
	private String getEligibilityEvent;

	@Value("${LegalStatusDaoImpl.getPersonForStage}")
	private String getPersonForStage;

	@Value("${LegalStatusDaoImpl.getLegalStatusCode}")
	private String getLegalStatusCode;

	@Value("${LegalStatusDaoImpl.updateLegalStatus}")
	private String updateLegalStatus;

	@Value("${LegalStatusDaoImpl.deleteLegalStatus}")
	private String deleteLegalStatus;

	@Value("${LegalStatusDaoImpl.deleteLegalStatusSubType}")
	private String deleteLegalStatusSubType;

	@Value("${LegalStatusDaoImpl.updatePersonGuardCnsrv}")
	private String updatePersonGuardCnsrv;

	@Value("${LegalStatusDaoImpl.deleteSubCareEvent}")
	private String deleteSubCareEvent;

	@Value("${LegalStatusDaoImpl.getResourceDetail}")
	private String getResourceDetail;

	@Value("${LegalStatusDaoImpl.modifyNbrRscOpenSlots}")
	private String modifyNbrRscOpenSlots;

	@Value("${LegalStatusDaoImpl.selectWorkloadDetail}")
	private String selectWorkloadDetail;

	@Value("${LegalStatusDaoImpl.getIndLegalStatMissing}")
	private String getIndLegalStatMissing;

	@Value("${LegalStatusDaoImpl.updateSsccIndLegalStatus}")
	private String updateIndLegalStatMissing;

	@Value("${LegalStatusDaoImpl.selectLatestLegalActionSubType}")
	private String selectLatestLegalActionSubType;

	@Value("${LegalStatusDaoImpl.selectLatestLegalStatus}")
	private String selectLatestLegalStatus;

	@Value("${LegalStatusDaoImpl.getLatestLegalStatus}")
	private String getLatestLegalStatus;

	@Value("${LegalStatusDaoImpl.getRecentLegalStatusForChild}")
	private String getRecentLegalStatusForChild;

	// UIDS 2.3.3.5 - Remove a child from home - Income and Expenditures
	@Value("${LegalStatusDaoImpl.getLegalStatusForChild}")
	private String getLegalStatusForChild;

	@Value("${LegalStatusDaoImpl.getRecentLegalRegionForChild}")
	private String getRecentLegalRegionForChild;

	@Value("${LegalStatusDaoImpl.getMaxLegalStatusDate}")
	private String getMaxLegalStatusDateSql;

	@Value("${LegalStatusDaoImpl.getPlacementLegalStatusInfoWithDate}")
	private String getPlacementLegalStatusInfoWithDateSql;

	@Value("${LegalStatusDaoImpl.getLatestLegalStatusByPersonId}")
	private String getLatestLegalStatusByPersonId;

	@Value("${LegalStatusDaoImpl.getLatestLegalStatusInfoByPersonId}")
	private String getLatestLegalStatusInfoByPersonId;

	@Value("${LegalStatusDaoImpl.getLatestLegalStatusInfoByEventId}")
	private String getLatestLegalStatusInfoByEventId;

	private static final Logger log = Logger.getLogger(LegalStatusDaoImpl.class);

	/**
	 * 
	 * Method Name: getLegalStatusForEvent Method Description:Fetch legal status
	 * for provided event, cses11dQUERYdam
	 * 
	 * @param pInputDataRec
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<LegalStatusOutDto> getLegalStatusForEvent(LegalStatusInDto pInputDataRec) {
		log.info("Entering method getLegalStatusForEvent in LegalStatusDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getLegalStatus)
				.addScalar("idLegalStatEvent", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.DATE).addScalar("IdPerson", StandardBasicTypes.LONG)
				.addScalar("cdLegalStatCnty", StandardBasicTypes.STRING)
				.addScalar("cdLegalStatStatus", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatusDt", StandardBasicTypes.DATE)
				.addScalar("txtLegalStatCauseNbr", StandardBasicTypes.STRING)
				.addScalar("txtLegalStatCourtNbr", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatTMCDismiss", StandardBasicTypes.DATE)
				.addScalar("cdCourtNbr", StandardBasicTypes.STRING)
				.addScalar("cdLegStatDischargeRsn", StandardBasicTypes.STRING)
				.addScalar("indJmcPrntReltnshpKnsp", StandardBasicTypes.STRING) // ADS
																				// Change
																				// -
																				// 2.6.2.4
																				// -
																				// Legal
																				// Status
				.setParameter("hI_IdLegalStatEvent", pInputDataRec.getIdLegalStatEvent())
				.setResultTransformer(Transformers.aliasToBean(LegalStatusOutDto.class)));
		log.info("Exiting method getLegalStatusForEvent in LegalStatusDaoImpl");
		return (List<LegalStatusOutDto>) sQLQuery1.list();
	}

	// ADS Change - 2.6.2.4 - Legal Status Fetch the Legal Status SubType

	/**
	 * 
	 * Method Name: getLegalStatusSubTypeForEvent Method Description:Fetch legal
	 * status subtype for provided event
	 * 
	 * @param pInputDataRec
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<LegalStatusOutDto> getLegalStatusSubTypeForEvent(LegalStatusInDto pInputDataRec) {
		log.info("Entering method getLegalStatusSubTypeForEvent in LegalStatusDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getLegalStatusSubType)
				.addScalar("cdLegalStatusSubType", StandardBasicTypes.STRING)
				.setParameter("hI_IdLegalStatEvent", pInputDataRec.getIdLegalStatEvent())
				.setResultTransformer(Transformers.aliasToBean(LegalStatusOutDto.class)));
		log.info("Exiting method getLegalStatusSubTypeForEvent in LegalStatusDaoImpl");
		return (List<LegalStatusOutDto>) sQLQuery1.list();
	}

	/**
	 * 
	 * Method Name: getLegalStatusCount Method Description: CSUB80D - This
	 * method will look in the Legal Status table for legal statuses that
	 * already exist for the same Person ID and date.
	 * 
	 * @param idPerson
	 * @param dtLegalStatStatusDt
	 * @return
	 */
	@Override
	public int getLegalStatusCount(Long idPerson, Date dtLegalStatStatusDt) {
		log.info("Entering method getLegalStatusCount in LegalStatusDaoImpl");
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getLegalStatusCount)
				.setParameter("idPerson", idPerson).setParameter("dtLegalStatStatusDt", dtLegalStatStatusDt);
		BigDecimal bigDecimalCount = ((BigDecimal) query.uniqueResult());
		log.info("Exiting method getLegalStatusCount in LegalStatusDaoImpl");
		if (!TypeConvUtil.isNullOrEmpty(bigDecimalCount)) {
			return bigDecimalCount.intValue();
		} else
			return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.legalstatus.dao.LegalStatusDao#
	 * getLegalStatusForChild(java.lang.Long, java.lang.Long)
	 */
	// UIDS 2.3.3.5 - Remove a child from home - Income and Expenditures
	@Override
	public int getLegalStatusForChild(Long idPerson, Long idCase) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getLegalStatusForChild)
				.setParameter("idPerson", idPerson).setParameter("idCase", idCase);
		BigDecimal bigDecimalCount = ((BigDecimal) query.uniqueResult());
		if (!ObjectUtils.isEmpty(bigDecimalCount)) {
			return bigDecimalCount.intValue();
		} else
			return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.legalstatus.dao.LegalStatusDao#
	 * getRecentLegalRegionForChild(java.lang.Long)
	 */
	// UIDS 2.3.3.5 - Remove a child from home - To-Do Detail
	@Override
	public String getRecentLegalRegionForChild(Long idPerson) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getRecentLegalRegionForChild)
				.setParameter("idPerson", idPerson);
		String legalRegion = (String) query.uniqueResult();
		return legalRegion;
	}

	/**
	 * 
	 * Method Name: getEligibilityEventForStage Method Description:CSESG3D -
	 * selects a Rec from the ELEGIBILITY table.
	 * 
	 * @param idStage
	 * @return
	 */
	@Override
	public EligibilityByPersonInDto getEligibilityEventForStage(Long idStage) {
		log.info("Entering method getEligibilityEventForStage in LegalStatusDaoImpl");
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Eligibility.class);
		criteria.add(Restrictions.eq("idStage", idStage));
		Date endDate;
		try {
			endDate = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_MMDDYYYY).parse("12/31/4712");
		} catch (ParseException e) {
			endDate = new Date();
		}
		criteria.add(Restrictions.eq("dtEligEnd", endDate));
		Eligibility eligibility = (Eligibility) criteria.uniqueResult();
		EligibilityByPersonInDto eligibilityByPersonInDto = null;
		if (eligibility != null) {
			eligibilityByPersonInDto = new EligibilityByPersonInDto();
			eligibilityByPersonInDto.setIdEligibilityEvent(eligibility.getIdEligEvent());
			eligibilityByPersonInDto.setCdEligSelected(eligibility.getCdEligSelected());
		}
		log.info("Exiting method getEligibilityEventForStage in LegalStatusDaoImpl");
		return eligibilityByPersonInDto;
	}

	/**
	 * 
	 * Method Name: getPersonNameForStage Method Description:CSESG4D - select
	 * person name for stage
	 * 
	 * @param idStage
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getPersonNameForStage(Long idStage) {
		log.info("Entering method getPersonNameForStage in LegalStatusDaoImpl");
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonForStage)
				.setParameter("idStage", idStage);
		log.info("Exiting method getPersonNameForStage in LegalStatusDaoImpl");
		return (List<String>) sQLQuery1.list();
	}

	/**
	 * 
	 * Method Name: getLegalStatusCode Method Description:CMSC54D - select Cd
	 * Legal Stat Status for an Id Person and dt legal stat status dt from the
	 * Legal Status table
	 * 
	 * @param idPerson
	 * @param idLegalStatEvent
	 * @param dtLegalStatStatusDt
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getLegalStatusCode(Long idPerson, Long idLegalStatEvent, Date dtLegalStatStatusDt) {
		log.info("Entering method getLegalStatusCode in LegalStatusDaoImpl");
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getLegalStatusCode)
				.setParameter("idPerson", idPerson).setParameter("idLegalStatEvent", idLegalStatEvent)
				.setParameter("dtLegalStatStatusDt", dtLegalStatStatusDt);
		log.info("Exiting method getLegalStatusCode in LegalStatusDaoImpl");
		return (List<String>) sQLQuery1.list();
	}

	/**
	 * 
	 * Method Name: updateLegalStatus Method Description: CAUD05D - update Legal
	 * Status
	 * 
	 * @param pInputDataRec
	 */
	@Override
	public void updateLegalStatus(LegalStatusDto pInputDataRec) {
		log.info("Entering method updateLegalStatus in LegalStatusDaoImpl");
		switch (pInputDataRec.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			LegalStatus legalStatus = new LegalStatus();
			legalStatus.setIdLegalStatEvent(pInputDataRec.getIdLegalStatEvent());
			legalStatus.setIndCsupSend(pInputDataRec.getIndCsupSend().charAt(0));
			legalStatus.setCdLegalStatStatus(pInputDataRec.getCdLegalStatStatus());
			legalStatus.setCdDischargeRsn(pInputDataRec.getCdLegStatDischargeRsn());
			legalStatus.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
			legalStatus.setTxtLegalStatCauseNbr(pInputDataRec.getTxtLegalStatCauseNbr());
			legalStatus.setDtLegalStatTmcDismiss(pInputDataRec.getDtLegalStatTMCDismiss());
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Person.class);
			criteria.add(Restrictions.eq("idPerson", pInputDataRec.getIdPerson()));
			Person person = (Person) criteria.uniqueResult();
			legalStatus.setPerson(person);
			legalStatus.setIdCase(pInputDataRec.getIdCase());
			legalStatus.setCdCourtNbr(pInputDataRec.getCdCourtNbr());
			legalStatus.setIdLastUpdatePerson(pInputDataRec.getIdLastUpdatePerson());
			legalStatus.setCdLegalStatCnty(pInputDataRec.getCdLegalStatCnty());
			legalStatus.setTxtLegalStatCourtNbr(pInputDataRec.getTxtLegalStatCourtNbr());
			legalStatus.setDtLegalStatStatusDt(pInputDataRec.getDtLegalStatStatusDt());
			legalStatus.setIndJmcPrntReltnshpKnsp(
					pInputDataRec.getIndJmcPrntReltnshpKnsp() ? ServiceConstants.YES : ServiceConstants.NO);
			sessionFactory.getCurrentSession().save(legalStatus);

			if (null != pInputDataRec.getCdLegalStatSubType()) {
				// ADS Change - 2.6.2.4 - Legal Status - Save Legal Status Sub
				// Type
				LegalStatusSubType legalStatusSubType = new LegalStatusSubType();
				legalStatusSubType.setCdLegalStatSubtype(pInputDataRec.getCdLegalStatSubType());
				legalStatusSubType.setIdLegalStatEvent(pInputDataRec.getIdLegalStatEvent());
				legalStatusSubType.setIdCreatedPerson(pInputDataRec.getIdPerson());
				legalStatusSubType
						.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
				legalStatusSubType.setIdLastUpdatePerson(pInputDataRec.getIdLastUpdatePerson());
				sessionFactory.getCurrentSession().save(legalStatusSubType);
			}

			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			LegalStatus legalStatusForUpdate = (LegalStatus) sessionFactory.getCurrentSession()
					.createCriteria(LegalStatus.class)
					.add(Restrictions.eq("idLegalStatEvent", pInputDataRec.getIdLegalStatEvent())).uniqueResult();
			legalStatusForUpdate.setIdLegalStatEvent(pInputDataRec.getIdLegalStatEvent());
			legalStatusForUpdate.setIndCsupSend(pInputDataRec.getIndCsupSend().charAt(0));
			legalStatusForUpdate.setCdLegalStatStatus(pInputDataRec.getCdLegalStatStatus());
			legalStatusForUpdate.setCdDischargeRsn(pInputDataRec.getCdLegStatDischargeRsn());
			legalStatusForUpdate
					.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
			legalStatusForUpdate.setTxtLegalStatCauseNbr(pInputDataRec.getTxtLegalStatCauseNbr());
			legalStatusForUpdate.setDtLegalStatTmcDismiss(pInputDataRec.getDtLegalStatTMCDismiss());
			legalStatusForUpdate.setCdCourtNbr(pInputDataRec.getCdCourtNbr());
			legalStatusForUpdate.setIdLastUpdatePerson(pInputDataRec.getIdLastUpdatePerson());
			legalStatusForUpdate.setCdLegalStatCnty(pInputDataRec.getCdLegalStatCnty());
			legalStatusForUpdate.setTxtLegalStatCourtNbr(pInputDataRec.getTxtLegalStatCourtNbr());
			legalStatusForUpdate.setDtLegalStatStatusDt(pInputDataRec.getDtLegalStatStatusDt());
			legalStatusForUpdate.setIndJmcPrntReltnshpKnsp(
					pInputDataRec.getIndJmcPrntReltnshpKnsp() ? ServiceConstants.YES : ServiceConstants.NO);
			sessionFactory.getCurrentSession().saveOrUpdate(legalStatusForUpdate);

			//if (null != pInputDataRec.getCdLegalStatSubType()) {
				// ADS Change - 2.6.2.4 - Legal Status -Update the Legal Status
				// SubType
			LegalStatusSubType legalStatusSubTypeForUpdate = (LegalStatusSubType) sessionFactory.getCurrentSession()
					.createCriteria(LegalStatusSubType.class)
					.add(Restrictions.eq("idLegalStatEvent", pInputDataRec.getIdLegalStatEvent())).uniqueResult();
			if(legalStatusSubTypeForUpdate!=null) {
				if (null != pInputDataRec.getCdLegalStatSubType()) {
					legalStatusSubTypeForUpdate.setCdLegalStatSubtype(pInputDataRec.getCdLegalStatSubType());
					legalStatusSubTypeForUpdate.setIdLegalStatEvent(pInputDataRec.getIdLegalStatEvent());
					legalStatusSubTypeForUpdate.setIdCreatedPerson(pInputDataRec.getIdPerson());
					legalStatusSubTypeForUpdate
							.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
					legalStatusSubTypeForUpdate.setIdLastUpdatePerson(pInputDataRec.getIdLastUpdatePerson());
					sessionFactory.getCurrentSession().saveOrUpdate(legalStatusSubTypeForUpdate);
				} else {
					sessionFactory.getCurrentSession().delete(legalStatusSubTypeForUpdate); //artf284194 -subtype deselected
				}
			} else if(pInputDataRec.getCdLegalStatSubType()!=null){  //artf284194 - Subtype was selected in Update.
				LegalStatusSubType legalStatusSubTypeForAdd = new LegalStatusSubType();
				legalStatusSubTypeForAdd.setCdLegalStatSubtype(pInputDataRec.getCdLegalStatSubType());
				legalStatusSubTypeForAdd.setIdLegalStatEvent(pInputDataRec.getIdLegalStatEvent());
				legalStatusSubTypeForAdd.setIdCreatedPerson(pInputDataRec.getIdPerson());
				legalStatusSubTypeForAdd
						.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
				legalStatusSubTypeForAdd.setIdLastUpdatePerson(pInputDataRec.getIdLastUpdatePerson());
				sessionFactory.getCurrentSession().save(legalStatusSubTypeForAdd);
			}

			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:

			// JIRA 90342: Legal Status record deletion
			// To delete the record from the Legal Status SubType Table
			SQLQuery sQLQuery3 = (SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(deleteLegalStatusSubType)
					.setParameter("idLegalStatEvent", pInputDataRec.getIdLegalStatEvent());
			sQLQuery3.executeUpdate();


			// To delete the record from the Legal Status Table
			LegalStatus legalStatusForDelete = (LegalStatus) sessionFactory.getCurrentSession()
					.createCriteria(LegalStatus.class)
					.add(Restrictions.eq("idLegalStatEvent", pInputDataRec.getIdLegalStatEvent())).uniqueResult();
			if(null != pInputDataRec.getUserLogonId()) {
				legalStatusForDelete.setIdLastUpdatePerson(Long.parseLong(pInputDataRec.getUserLogonId()));
			}
			sessionFactory.getCurrentSession().saveOrUpdate(legalStatusForDelete);
			sessionFactory.getCurrentSession().flush();
			sessionFactory.getCurrentSession().delete(legalStatusForDelete);

			break;
		default:
			break;
		}
		log.info("Exiting method updateLegalStatus in LegalStatusDaoImpl");
	}

	/**
	 * 
	 * Method Name: updatePersonGuardCnsrv Method Description:CAUDB8D - updates
	 * the Person Table for individuals with the status of conservatorship, and
	 * updates the opposite case
	 * 
	 * @param idPerson
	 * @param cdPersGuardCnsrv
	 */
	@Override
	public void updatePersonGuardCnsrv(Long idPerson, String cdPersGuardCnsrv) {
		log.info("Entering method updatePersonGuardCnsrv in LegalStatusDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updatePersonGuardCnsrv)
				.setParameter("cdPersGuardCnsrv", cdPersGuardCnsrv).setParameter("idPerson", idPerson));
		sQLQuery1.executeUpdate();
		log.info("Exiting method updatePersonGuardCnsrv in LegalStatusDaoImpl");
	}

	/**
	 * 
	 * Method Name: deleteSubcareEvent Method Description: CAUD07D - Call stored
	 * procedure COMPLEX_DELETE.DELETE_SUBCARE_EVENT to delete a set of tables.
	 * 
	 * @param idEvent
	 * @param tsLastUpdate
	 * @throws SQLException
	 */
	@Override
	public void deleteSubcareEvent(Long idEvent, Date lastUpdateDate) throws SQLException {
		log.info("Entering method deleteSubcareEvent in LegalStatusDaoImpl");
		SessionImplementor sessionFactoryImplementation = (SessionImplementor) sessionFactory.getCurrentSession();
		Connection connection = sessionFactoryImplementation.getJdbcConnectionAccess().obtainConnection();
		CallableStatement callableStatement = null;
		try {
			callableStatement = connection.prepareCall(deleteSubCareEvent);
			callableStatement.setLong(1, idEvent);
			callableStatement.setDate(2, new java.sql.Date(lastUpdateDate.getTime()));
			callableStatement.execute();
			callableStatement.close();
			connection.close();
		} catch (Exception e) {
			DataLayerException dataLayerException = new DataLayerException(e.getMessage());
			dataLayerException.initCause(e);
			throw dataLayerException;

		} finally {
			if (!ObjectUtils.isEmpty(callableStatement))
				callableStatement.close();
			if (!ObjectUtils.isEmpty(connection))
				connection.close();
		}
		log.info("Exiting method deleteSubcareEvent in LegalStatusDaoImpl");
	}

	/**
	 * 
	 * Method Name: getResourceDetail Method Description: CSES28D - Get Resource
	 * and Placement details for person
	 * 
	 * @param idPerson
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ResourcePlacementOutDto> getResourcePlacementDetail(Long idPerson) {
		log.info("Entering method getResourceDetail in LegalStatusDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getResourceDetail)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("idPlcmtAdult", StandardBasicTypes.LONG).addScalar("idPlcmtChild", StandardBasicTypes.LONG)
				.addScalar("idRsrcAgency", StandardBasicTypes.LONG).addScalar("idRsrcFacil", StandardBasicTypes.LONG)
				.addScalar("addrPlcmtCity", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtCnty", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtLn1", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtLn2", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtSt", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtZip", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo1", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo2", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo3", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo4", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo5", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo6", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo7", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtRemovalRsn", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtActPlanned", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtType", StandardBasicTypes.STRING)
				.addScalar("dtPlcmtCaregvrDiscuss", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtChildDiscuss", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtChildPlan", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtEducLog", StandardBasicTypes.DATE).addScalar("dtPlcmtEnd", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtMeddevHistory", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtParentsNotif", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtPreplaceVisit", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtSchoolRecords", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtStart", StandardBasicTypes.DATE)
				.addScalar("indPlcmtContCntct", StandardBasicTypes.STRING)
				.addScalar("indPlcmtEducLog", StandardBasicTypes.STRING)
				.addScalar("indPlcmetEmerg", StandardBasicTypes.STRING)
				.addScalar("indPlcmtNotApplic", StandardBasicTypes.STRING)
				.addScalar("indPlcmtSchoolDoc", StandardBasicTypes.STRING)
				.addScalar("indPlcmtWriteHistory", StandardBasicTypes.STRING)
				.addScalar("nbrPlcmtPhoneExt", StandardBasicTypes.STRING)
				.addScalar("nbrPlcmtTelephone", StandardBasicTypes.STRING)
				.addScalar("nmPlcmtAgency", StandardBasicTypes.STRING)
				.addScalar("nmPlcmtContact", StandardBasicTypes.STRING)
				.addScalar("nmPlcmtFacil", StandardBasicTypes.STRING)
				.addScalar("nmPlcmtPersonFull", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtAddrComment", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtDiscussion", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtDocuments", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtRemovalRsn", StandardBasicTypes.STRING)
				.addScalar("idResource", StandardBasicTypes.LONG).addScalar("addrRsrcStLn1", StandardBasicTypes.STRING)
				.addScalar("addrRsrcStLn2", StandardBasicTypes.STRING)
				.addScalar("addrRsrcCity", StandardBasicTypes.STRING)
				.addScalar("cdRsrcState", StandardBasicTypes.STRING).addScalar("addrRsrcZip", StandardBasicTypes.STRING)
				.addScalar("addrRsrcAttn", StandardBasicTypes.STRING).addScalar("cdRsrcCnty", StandardBasicTypes.STRING)
				.addScalar("cdRsrcInvolClosure", StandardBasicTypes.STRING)
				.addScalar("cdRsrcClosureRsn", StandardBasicTypes.STRING)
				.addScalar("cdRsrcCampusType", StandardBasicTypes.STRING)
				.addScalar("cdRsrcCategory", StandardBasicTypes.STRING)
				.addScalar("cdRsrcCertBy", StandardBasicTypes.STRING)
				.addScalar("cdRsrcEthnicity", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeStatus", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType1", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType2", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType3", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType4", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType5", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType6", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType7", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFacilType", StandardBasicTypes.STRING)
				.addScalar("cdRsrcLanguage", StandardBasicTypes.STRING)
				.addScalar("cdRsrcMaintainer", StandardBasicTypes.STRING)
				.addScalar("cdRsrcMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("cdRsrcOperBy", StandardBasicTypes.STRING)
				.addScalar("cdRsrcOwnership", StandardBasicTypes.STRING)
				.addScalar("cdRsrcPayment", StandardBasicTypes.STRING)
				.addScalar("cdRsrcRecmndReopen", StandardBasicTypes.STRING)
				.addScalar("cdRsrcRegion", StandardBasicTypes.STRING)
				.addScalar("cdRsrcReligion", StandardBasicTypes.STRING)
				.addScalar("cdRsrcRespite", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSchDist", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSetting", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSourceInquiry", StandardBasicTypes.STRING)
				.addScalar("cdRsrcStatus", StandardBasicTypes.STRING).addScalar("cdRsrcType", StandardBasicTypes.STRING)
				.addScalar("dtRsrcMarriage", StandardBasicTypes.DATE).addScalar("dtRsrcCert", StandardBasicTypes.DATE)
				.addScalar("dtRsrcClose", StandardBasicTypes.DATE)
				.addScalar("idRsrcFaHomeEvent", StandardBasicTypes.LONG)
				.addScalar("idRsrcFaHomeStage", StandardBasicTypes.LONG)
				.addScalar("indRsrcCareProv", StandardBasicTypes.STRING)
				.addScalar("indRsrcEmergPlace", StandardBasicTypes.STRING)
				.addScalar("indRsrcInactive", StandardBasicTypes.STRING)
				.addScalar("indRsrindivStudy", StandardBasicTypes.STRING)
				.addScalar("indRsrcNonPrs", StandardBasicTypes.STRING)
				.addScalar("indRsrcTransport", StandardBasicTypes.STRING)
				.addScalar("indRsrcWriteHist", StandardBasicTypes.STRING)
				.addScalar("nmRsrcLastUpdate", StandardBasicTypes.STRING)
				.addScalar("nmResource", StandardBasicTypes.STRING)
				.addScalar("nmRsrcContact", StandardBasicTypes.STRING)
				.addScalar("nbrRsrcAnnualIncome", StandardBasicTypes.DOUBLE)
				.addScalar("nbrSchCampunbr", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcFacilAcclaim", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcFacilCapacity", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcFMAgeMax", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcFMAgeMin", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcMlAgeMax", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcMlAgeMin", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcIntChildren", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcIntFeAgeMax", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcIntFeAgeMin", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcIntMaAgeMax", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcIntMaAgeMin", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcOpenSlots", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcPhn", StandardBasicTypes.STRING)
				.addScalar("nbrFacilPhoneExtension", StandardBasicTypes.STRING)
				.addScalar("nbrRsrcVid", StandardBasicTypes.STRING)
				.addScalar("txtRsrcAddrCmnts", StandardBasicTypes.STRING)
				.addScalar("txtRsrcComments", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(ResourcePlacementOutDto.class)));
		log.info("Exiting method getResourceDetail in LegalStatusDaoImpl");
		return (List<ResourcePlacementOutDto>) sQLQuery1.list();

	}

	/**
	 * 
	 * Method Name: modifyNbrRscOpenSlots Method Description:CMSC16D - will
	 * increment or decrement NBR_RSRSC_OPEN_SLOTS by 1 depending on what is
	 * passed into the DAM through ID_RESOURCE
	 * 
	 * @param idResource
	 * @param nbrRsrcOpenSlots
	 */
	@Override
	public void modifyNbrRscOpenSlots(Long idResource, String nbrRsrcOpenSlots) {
		log.info("Entering method modifyNbrRscOpenSlots in LegalStatusDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(modifyNbrRscOpenSlots)
				.setParameter("idResource", idResource).setParameter("nbrRsrcOpenSlots", nbrRsrcOpenSlots));
		sQLQuery1.executeUpdate();
		log.info("Exiting method modifyNbrRscOpenSlots in LegalStatusDaoImpl");
	}
	/**
	 * 
	 * Method Name: modifyNbrRscOpenSlots Method Description:CMSC16D - will
	 * increment or decrement NBR_RSRSC_OPEN_SLOTS by 1 depending on what is
	 * passed into the DAM through ID_RESOURCE
	 * 
	 * @param idResource
	 * @param nbrRsrcOpenSlots
	 */
	@Override
	public Long updateNbrRscOpenSlots(Long idResource, String nbrRsrcOpenSlots) {
		log.info("Entering method updateNbrRscOpenSlots in LegalStatusDaoImpl");
		CapsResource capsResource = (CapsResource) sessionFactory.getCurrentSession().createCriteria(CapsResource.class)
				.add(Restrictions.eq("idResource", idResource)).uniqueResult();
		Long rsrcOpenSlots = capsResource.getNbrRsrcOpenSlots();
		rsrcOpenSlots = ObjectUtils.isEmpty(rsrcOpenSlots) ? 0L : rsrcOpenSlots;
		if (!ObjectUtils.isEmpty(nbrRsrcOpenSlots)) {
			rsrcOpenSlots += Long.parseLong(nbrRsrcOpenSlots);
			capsResource.setNbrRsrcOpenSlots(rsrcOpenSlots);
			sessionFactory.getCurrentSession().persist(capsResource);
		}
		
		log.info("Exiting method updateNbrRscOpenSlots in LegalStatusDaoImpl");
		return rsrcOpenSlots;
	}

	/**
	 * 
	 * Method Name: getWorkLoadsForStage Method Description: CSEC86D - Get
	 * workload detail for stage
	 * 
	 * @param idStage
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<WorkLoadDto> getWorkLoadsForStage(long idStage) {
		log.info("Entering method getWorkLoadsForStage in LegalStatusDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectWorkloadDetail)
				.addScalar("idWrldPerson", StandardBasicTypes.LONG).addScalar("idWkldStage", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("cdWkldStagePersRole", StandardBasicTypes.STRING)
				.addScalar("dtWkldStagePersLink", StandardBasicTypes.DATE)
				.addScalar("indWkldStagePersNew", StandardBasicTypes.STRING)
				.addScalar("nmWkldStage", StandardBasicTypes.STRING).addScalar("cdWkldStage", StandardBasicTypes.STRING)
				.addScalar("cdWkldStageCnty", StandardBasicTypes.STRING)
				.addScalar("cdWkldStageType", StandardBasicTypes.STRING)
				.addScalar("cdWkldStageRegion", StandardBasicTypes.STRING)
				.addScalar("cdWkldStageRsnCls", StandardBasicTypes.STRING)
				.addScalar("cdWkldStageProgram", StandardBasicTypes.STRING)
				.addScalar("idWkldUnit", StandardBasicTypes.LONG).addScalar("nbrWkldUnit", StandardBasicTypes.STRING)
				.addScalar("nmWkldCase", StandardBasicTypes.STRING)
				.addScalar("indWkldCaseSensitive", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(WorkLoadDto.class)));
		log.info("Exiting method getWorkLoadsForStage in LegalStatusDaoImpl");
		return (List<WorkLoadDto>) sQLQuery1.list();
	}

	/**
	 * 
	 * Method Name: getIndLegalStatMissing Method Description: GET
	 * IND_LEGAL_STATE_MISSING
	 * 
	 * @param idReferral
	 * @return
	 */
	@Override
	public SSCCExceptCareDesignationDto getIndLegalStatMissing(Long idReferral) {
		log.info("Entering method getIndLegalStatMissing in LegalStatusDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getIndLegalStatMissing)
				.addScalar("indLegalStatMissing").setParameter("idReferral", idReferral)
				.setResultTransformer(Transformers.aliasToBean(SSCCExceptCareDesignationDto.class)));
		log.info("Exiting method getIndLegalStatMissing in LegalStatusDaoImpl");
		if (CollectionUtils.isNotEmpty(sQLQuery1.list()))
			return (SSCCExceptCareDesignationDto) sQLQuery1.list().get(0);
		else
			return null;

	}

	/**
	 * 
	 * Method Name: updateSsccIndLegalStatus Method Description:update SSCC_LIST
	 * TABLE , IND_LEGAL_STATUS_MISSING
	 * 
	 * @param indLegalStatusMissing
	 * @param idSsccReferral
	 */
	@Override
	public void updateSsccIndLegalStatus(String indLegalStatusMissing, Long idSsccReferral) {
		log.info("Entering method updateSsccIndLegalStatus in LegalStatusDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateIndLegalStatMissing)
				.setParameter("indLegalStatusMissing", indLegalStatusMissing)
				.setParameter("idSsccReferral", idSsccReferral));
		sQLQuery1.executeUpdate();
		log.info("Exiting method updateSsccIndLegalStatus in LegalStatusDaoImpl");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.legalstatus.dao.LegalStatusDao#
	 * selectLatestLegalActionSubType(us.tx.state.dfps.service.admin.dto.
	 * LegalActionEventInDto)
	 */
	@Override
	public CommonStringRes selectLatestLegalActionSubType(LegalActionEventInDto legalActionEventInDto) {
		String query = selectLatestLegalActionSubType;
		List<String> subTypes = legalActionEventInDto.getLegalActionSubTypes();
		String subTypeStr = "";

		if (!ObjectUtils.isEmpty(subTypes)) {
			for (int index = 0; index < subTypes.size(); index++) {
				subTypeStr += subTypes.get(index);
				if (index != subTypes.size() - 1) {
					subTypeStr += STRING2;
				}
			}
			query += AND_L_CD_LEGAL_ACT_ACTN_SUBTYPE_IN + subTypeStr + STRING;
		}
		query += ORDER_BY_L_DT_LEGAL_ACT_OUTCOME_DT_DESC_L_ID_LEGAL_ACT_EVENT_DESC;

		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(query)
				.addScalar("commonRes", StandardBasicTypes.STRING)
				.setParameter("idPerson", legalActionEventInDto.getIdPerson())
				.setParameter("idCase", legalActionEventInDto.getIdCase())
				.setParameter("idStage", legalActionEventInDto.getIdStage())
				.setParameter("legalActionType", legalActionEventInDto.getLegalActionType())
				.setResultTransformer(Transformers.aliasToBean(CommonStringRes.class)));

		return (CommonStringRes) sqlQuery.uniqueResult();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.legalstatus.dao.LegalStatusDao#
	 * selectLatestLegalStatus(us.tx.state.dfps.service.admin.dto.
	 * LegalActionEventInDto)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public LegalStatusDetailDto selectLatestLegalStatus(LegalActionEventInDto legalActionEventInDto) {
		LegalStatusDetailDto resp = new LegalStatusDetailDto();
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectLatestLegalStatus)
				.addScalar("idLegalStatEvent", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.DATE).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("cdLegalStatCnty", StandardBasicTypes.STRING)
				.addScalar("cdLegalStatStatus", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatusDt", StandardBasicTypes.DATE)
				.addScalar("txtLegalStatCauseNbr", StandardBasicTypes.STRING)
				.addScalar("txtLegalStatCourtNbr", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatTMCDismiss", StandardBasicTypes.DATE)
				.addScalar("indCsupSend", StandardBasicTypes.STRING).addScalar("cdCourtNbr", StandardBasicTypes.STRING)
				.addScalar("cdLegStatDischargeRsn", StandardBasicTypes.STRING)
				.setParameter("idPerson", legalActionEventInDto.getIdPerson())
				.setParameter("cdLegalStatStatus", legalActionEventInDto.getCdLegalStatStatus())
				.setResultTransformer(Transformers.aliasToBean(LegalStatusDetailDto.class)));

		List<LegalStatusDetailDto> legalStatusDetailDtoList = sQLQuery1.list();
		if (!ObjectUtils.isEmpty(legalStatusDetailDtoList)) {
			resp = legalStatusDetailDtoList.get(0);

		}
		return resp;
	}

	/**
	 * Method Name: getRecentLegalStatusForChild Method Description:Fetches the
	 * Latest legal Status of the child
	 * 
	 * @param idPerson
	 * @return
	 */
	@Override
	public LegalStatusDto getRecentLegalStatusForChild(Long idPerson) {
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getRecentLegalStatusForChild)
				.addScalar("cdLegalStatStatus").setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(LegalStatusDto.class)));
		return (LegalStatusDto) query.uniqueResult();
	}

	/**
	 * 
	 * Method Name: getLatestLegalStatus (CSES78D) Method Description:This
	 * Service retrieves a full row from LEGAL_STATUS.
	 * 
	 * @param idPerson
	 * @param idCase
	 * @return
	 */
	@Override
	public LegalStatusDetailDto getLatestLegalStatus(Long idPerson, Long idCase) {
		LegalStatusDetailDto resp = new LegalStatusDetailDto();
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getLatestLegalStatus)
				.addScalar("idLegalStatEvent", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.DATE).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("cdLegalStatCnty", StandardBasicTypes.STRING)
				.addScalar("cdLegalStatStatus", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatusDt", StandardBasicTypes.DATE)
				.addScalar("txtLegalStatCauseNbr", StandardBasicTypes.STRING)
				.addScalar("txtLegalStatCourtNbr", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setParameter("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(LegalStatusDetailDto.class)));

		List<LegalStatusDetailDto> legalStatusDetailDtoList = sQLQuery1.list();
		if (!ObjectUtils.isEmpty(legalStatusDetailDtoList)) {
			resp = legalStatusDetailDtoList.get(0);

		}
		return resp;
	}

	/**
	 * Method Name: getPersonFullName Method Description:Fetch the Person full
	 * name by joining the legal_statu and person table from ID_LEGAL_STAT_EVENT
	 * passing to the legal_status
	 * 
	 * @param idEvent
	 * @return String
	 */
	public String getPersonFullName(Long idEvent) {
		String personName = " ";
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LegalStatus.class)
				.add(Restrictions.eq("idLegalStatEvent", idEvent));
		LegalStatus legalStatus = (LegalStatus) criteria.uniqueResult();
		if (!ObjectUtils.isEmpty(legalStatus) &&
				!ObjectUtils.isEmpty(legalStatus.getPerson()) &&
				!ObjectUtils.isEmpty(legalStatus.getPerson().getNmPersonFull())) {
			personName = legalStatus.getPerson().getNmPersonFull();
		}
		return personName;
	}

	public Date getMaxLegalStatusDate(Long childId) {
		Query querySql = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(getMaxLegalStatusDateSql)
				.setParameter("childId", childId));
		return (Date) querySql.uniqueResult();
	}

	public KinChildDto getPlacementLegalStatusInfoWithDate(Long childId, Date maxLegalStatusDate) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPlacementLegalStatusInfoWithDateSql)
				.addScalar("legalStatusStatDate", StandardBasicTypes.DATE)
				.addScalar("legalStatusDismissDate", StandardBasicTypes.DATE)
				.addScalar("cdLegalStatus", StandardBasicTypes.STRING)
				.setParameter("childId", childId)
				.setParameter("maxLegalStatusDate", maxLegalStatusDate)
				.setResultTransformer(Transformers.aliasToBean(KinChildDto.class));

		return (KinChildDto) query.uniqueResult();
	}

	//PPM 77834 – FCL CLASS Webservice for Data Exchange
	/**
	 * @param idPerson
	 * @return
	 */
	@Override
	public LegalStatusDetailDto getLatestLegalStatusByPersonId(Long idPerson) {
		LegalStatusDetailDto resp = new LegalStatusDetailDto();
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getLatestLegalStatusByPersonId)
				.addScalar("idLegalStatEvent", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("cdLegalStatCnty", StandardBasicTypes.STRING)
				.addScalar("cdLegalStatStatus", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatusDt", StandardBasicTypes.DATE)
				.addScalar("txtLegalStatCauseNbr", StandardBasicTypes.STRING)
				.addScalar("txtLegalStatCourtNbr", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatTMCDismiss", StandardBasicTypes.DATE)
				.addScalar("indCsupSend", StandardBasicTypes.STRING)
				.addScalar("cdCourtNbr", StandardBasicTypes.STRING)
				.addScalar("cdLegStatDischargeRsn", StandardBasicTypes.STRING)
				.addScalar("indJmcPrntReltnshpKnsp", StandardBasicTypes.BOOLEAN)
				.addScalar("indCaseSensitive", StandardBasicTypes.STRING)
				.setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(LegalStatusDetailDto.class)));

		List<LegalStatusDetailDto> legalStatusDetailDtoList = sQLQuery1.list();
		if (!ObjectUtils.isEmpty(legalStatusDetailDtoList)) {
			resp = legalStatusDetailDtoList.get(0);

		}
		return resp;
	}
	/**
	 * @param idPerson
	 * @return
	 */
	@Override
	public LegalStatusDetailDto getLatestLegalStatusInfoByPersonId(Long idPerson) {
		LegalStatusDetailDto resp = new LegalStatusDetailDto();
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getLatestLegalStatusInfoByPersonId)
				.addScalar("idLegalStatEvent", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("cdLegalStatCnty", StandardBasicTypes.STRING)
				.addScalar("cdLegalStatStatus", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatusDt", StandardBasicTypes.DATE)
				.addScalar("txtLegalStatCauseNbr", StandardBasicTypes.STRING)
				.addScalar("txtLegalStatCourtNbr", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatTMCDismiss", StandardBasicTypes.DATE)
				.addScalar("indCsupSend", StandardBasicTypes.STRING)
				.addScalar("cdCourtNbr", StandardBasicTypes.STRING)
				.addScalar("cdLegStatDischargeRsn", StandardBasicTypes.STRING)
				.addScalar("indJmcPrntReltnshpKnsp", StandardBasicTypes.BOOLEAN)
				.addScalar("idLastUpdatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("cdLegalStatSubType", StandardBasicTypes.STRING)
				.setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(LegalStatusDetailDto.class)));

		List<LegalStatusDetailDto> legalStatusDetailDtoList = sQLQuery1.list();
		if (!ObjectUtils.isEmpty(legalStatusDetailDtoList)) {
			resp = legalStatusDetailDtoList.get(0);
		}
		return resp;
	}

	/**
	 * @param idEvent
	 * @return
	 */
	@Override
	public LegalStatusDetailDto getLatestLegalStatusInfoByEventId(Long idEvent) {
		LegalStatusDetailDto resp = new LegalStatusDetailDto();
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getLatestLegalStatusInfoByEventId)
				.addScalar("idLegalStatEvent", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("cdLegalStatCnty", StandardBasicTypes.STRING)
				.addScalar("cdLegalStatStatus", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatusDt", StandardBasicTypes.DATE)
				.addScalar("txtLegalStatCauseNbr", StandardBasicTypes.STRING)
				.addScalar("txtLegalStatCourtNbr", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatTMCDismiss", StandardBasicTypes.DATE)
				.addScalar("indCsupSend", StandardBasicTypes.STRING)
				.addScalar("cdCourtNbr", StandardBasicTypes.STRING)
				.addScalar("cdLegStatDischargeRsn", StandardBasicTypes.STRING)
				.addScalar("indJmcPrntReltnshpKnsp", StandardBasicTypes.BOOLEAN)
				.addScalar("idLastUpdatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("cdLegalStatSubType", StandardBasicTypes.STRING)
				.setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(LegalStatusDetailDto.class)));

		List<LegalStatusDetailDto> legalStatusDetailDtoList = sQLQuery1.list();
		if (!ObjectUtils.isEmpty(legalStatusDetailDtoList)) {
			resp = legalStatusDetailDtoList.get(0);
		}
		return resp;
	}
}




