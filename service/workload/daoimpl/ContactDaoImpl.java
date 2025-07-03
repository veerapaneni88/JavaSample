package us.tx.state.dfps.service.workload.daoimpl;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Contact;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptAllegDispValueDto;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptCPAValueDto;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptCPSValueDto;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptResourceValueDto;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptRsrcVoltnsValueDto;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptValueDto;
import us.tx.state.dfps.service.contact.dto.CFTSafetyAssessmentInfoDto;
import us.tx.state.dfps.service.contact.dto.ChildFatalityContactDto;
import us.tx.state.dfps.service.contact.dto.ContactStageIdsDto;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.workload.dao.ContactDao;
import us.tx.state.dfps.service.workload.dto.ContactDto;

/**
 *
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ContactDaoImpl Aug 2, 2018- 12:50:46 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class ContactDaoImpl implements ContactDao {

	@Value("${ContactDaoImpl.getFacilityType}")
	private String getFacilityTypeProcedure;

	@Value("${ContactDaoImpl.fetchCurrAbuseNglctInfo}")
	private String fetchCurrAbuseNglctInfo;

	@Value("${ContactDaoImpl.fetchChildFatalityInfo}")
	private String fetchChildFatalityInfo;

	@Value("${ContactDaoImpl.fetchPriorHistoryInfo}")
	private String fetchPriorHistoryInfo;

	@Value("${ContactDaoImpl.getContactEntityById}")
	private String getContactEntityByIdSql;

	@Value("${ContactDaoImpl.getContactById}")
	private String getContactByIdSql;

	@Value("${ContactDaoImpl.getByEventId}")
	private String getByEventId;


	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	public ContactDaoImpl() {

	}

	private static final Logger log = Logger.getLogger(ContactDaoImpl.class);

	/**
	 * This DAM is an AUD for the CONTACT table. Inputs: pSQLCA -- SQL
	 * communication area for Oracle return data. pInputDataRec -- Record
	 * containing a stage id. pOutputDataRec -- Record containing a stage table
	 * row
	 *
	 * Outputs: Service return code.
	 *
	 * NOTE: This DAM contains non-GENDAM generated code which would need to be
	 * copied if this DAM is re-GENDAM'd.
	 *
	 *
	 * Tuxedo Service Name: CCMN04U, DAM Name: CSYS07D
	 *
	 * @param contact
	 * @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public ContactDto getContactById(Long idContact) {
		ContactDto contactDto = new ContactDto();
		Query queryContact = sessionFactory.getCurrentSession().createSQLQuery(getContactByIdSql)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idContactStage", StandardBasicTypes.LONG)
				.addScalar("dtCntctMnthlySummBeg", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtCntctMnthlySummEnd", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtContactApprv", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdContactLocation", StandardBasicTypes.STRING)
				.addScalar("cdContactMethod", StandardBasicTypes.STRING)
				.addScalar("cdContactOthers", StandardBasicTypes.STRING)
				.addScalar("cdContactPurpose", StandardBasicTypes.STRING)
				.addScalar("cdContactType", StandardBasicTypes.STRING)
				.addScalar("dtContactOccurred", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtCntctNextSummDue", StandardBasicTypes.TIMESTAMP)
				.addScalar("indContactAttempted", StandardBasicTypes.STRING)
				.addScalar("idLastEmpUpdate", StandardBasicTypes.LONG)
				.addScalar("dtLastEmpUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("indEmergency", StandardBasicTypes.STRING)
				.addScalar("cdRsnScrout", StandardBasicTypes.STRING).addScalar("indRecCons", StandardBasicTypes.STRING)
				.addScalar("kinCaregiver", StandardBasicTypes.STRING).addScalar("cdRsnAmtne", StandardBasicTypes.STRING)
				.addScalar("amtNeeded", StandardBasicTypes.INTEGER)
				.addScalar("indSiblingVisit", StandardBasicTypes.STRING)
				.addScalar("cdChildSafety", StandardBasicTypes.STRING)
				.addScalar("cdPendLegalAction", StandardBasicTypes.STRING)
				.addScalar("indPrincipalInterview", StandardBasicTypes.STRING)
				.addScalar("cdProfCollateral", StandardBasicTypes.STRING)
				.addScalar("cdAdministrative", StandardBasicTypes.STRING)
				.addScalar("txtComments", StandardBasicTypes.STRING)
				.addScalar("indAnnounced", StandardBasicTypes.STRING)
				.addScalar("indSafPlanComp", StandardBasicTypes.STRING)
				.addScalar("indFamPlanComp", StandardBasicTypes.STRING)
				.addScalar("indSafConResolv", StandardBasicTypes.STRING)
				.addScalar("estContactHours", StandardBasicTypes.SHORT)
				.addScalar("estContactMins", StandardBasicTypes.SHORT)
				.setResultTransformer(Transformers.aliasToBean(ContactDto.class));
		queryContact.setParameter("idEvent", idContact);
		contactDto = (ContactDto) queryContact.uniqueResult();
		return contactDto;
	}

	public ContactDto getContactByEventId(Long idEvent) {
		ContactDto contactDto = new ContactDto();
		Contact contact = getContactEntityById(idEvent);
		BeanUtils.copyProperties(contact, contactDto);
		return contactDto;
	}

	/**
	 * This DAM is an AUD for the CONTACT table. Inputs: pSQLCA -- SQL
	 * communication area for Oracle return data. pInputDataRec -- Record
	 * containing a stage id. pOutputDataRec -- Record containing a stage table
	 * row
	 *
	 * Outputs: Service return code.
	 *
	 * NOTE: This DAM contains non-GENDAM generated code which would need to be
	 * copied if this DAM is re-GENDAM'd.
	 *
	 *
	 * Tuxedo Service Name: CCMN04U, DAM Name: CSYS07D
	 *
	 * @param contact
	 * @
	 */

	@Override
	public void saveContact(Contact contact) {

		sessionFactory.getCurrentSession().save(contact);

	}

	/**
	 * This DAM is an AUD for the CONTACT table. Inputs: pSQLCA -- SQL
	 * communication area for Oracle return data. pInputDataRec -- Record
	 * containing a stage id. pOutputDataRec -- Record containing a stage table
	 * row
	 *
	 * Outputs: Service return code.
	 *
	 * NOTE: This DAM contains non-GENDAM generated code which would need to be
	 * copied if this DAM is re-GENDAM'd.
	 *
	 *
	 * Tuxedo Service Name: CCMN04U, DAM Name: CSYS07D
	 *
	 * @param contact
	 * @
	 */
	@Override
	public void updateContact(Contact contact) {

		sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(contact));

	}

	/**
	 * This DAM is an AUD for the CONTACT table. Inputs: pSQLCA -- SQL
	 * communication area for Oracle return data. pInputDataRec -- Record
	 * containing a stage id. pOutputDataRec -- Record containing a stage table
	 * row
	 *
	 * Outputs: Service return code.
	 *
	 * NOTE: This DAM contains non-GENDAM generated code which would need to be
	 * copied if this DAM is re-GENDAM'd.
	 *
	 *
	 * Tuxedo Service Name: CCMN04U, DAM Name: CSYS07D
	 *
	 * @param contact
	 * @
	 */
	@Override
	public void deleteContact(Contact contact) {

		sessionFactory.getCurrentSession().delete(contact);

	}

	/**
	 * This DAM is an AUD for the CONTACT table. Inputs: pSQLCA -- SQL
	 * communication area for Oracle return data. pInputDataRec -- Record
	 * containing a stage id. pOutputDataRec -- Record containing a stage table
	 * row
	 *
	 * Outputs: Service return code.
	 *
	 * NOTE: This DAM contains non-GENDAM generated code which would need to be
	 * copied if this DAM is re-GENDAM'd.
	 *
	 *
	 * Tuxedo Service Name: CCMN04U, DAM Name: CSYS07D
	 *
	 * @param contact
	 * @
	 */
	@Override
	public Contact getContactEntityById(Long idContact) {
		Contact contact = new Contact();
		contact = (Contact) sessionFactory.getCurrentSession().createCriteria(Contact.class)
				.add(Restrictions.eq("idEvent", idContact)).uniqueResult();
		return contact;
	}

	/**
	 * Method Name: insertContact Method Description:This method inserts record
	 * into CONTACT table.
	 *
	 * @param contact
	 * @return Long
	 */
	@Override
	public Long insertContact(ContactDto contactDto) {
		Contact contact = new Contact();
		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, contactDto.getIdContactWorker());
		// Sending back the response as exception if the given person details
		// are not found
		if (TypeConvUtil.isNullOrEmpty(person)) {
			throw new DataNotFoundException(messageSource.getMessage("ContactDao.person.NotFound", null, Locale.US));
		}
		// Sending back the response as exception if the given stage details are
		// not found
		Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, contactDto.getIdContactStage());
		if (TypeConvUtil.isNullOrEmpty(stage)) {
			throw new DataNotFoundException(messageSource.getMessage("ContactDao.Stage.NotFound", null, Locale.US));
		}
		// Sending back the response as exception if the given event details are
		// not found
		Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, contactDto.getIdEvent());
		if (TypeConvUtil.isNullOrEmpty(event)) {
			throw new DataNotFoundException(messageSource.getMessage("ContactDao.Event.NotFound", null, Locale.US));
		}
		// populating the response from the entity
		contact.setAmtNeeded(contactDto.getAmtNeeded());
		contact.setCdAdministrative(contactDto.getCdAdministrative());
		contact.setCdChildSafety(contactDto.getCdChildSafety());
		contact.setCdContactLocation(contactDto.getCdContactLocation());
		contact.setCdContactMethod(contactDto.getCdContactMethod());
		contact.setCdContactOthers(contactDto.getCdContactOthers());
		contact.setCdContactPurpose(contactDto.getCdContactPurpose());
		contact.setCdContactType(contactDto.getCdContactType());
		contact.setCdPendLegalAction(contactDto.getCdPendLegalAction());
		contact.setCdProfCollateral(contactDto.getCdProfCollateral());
		contact.setCdRsnAmtne(contactDto.getCdRsnAmtne());
		contact.setCdRsnScrout(contactDto.getCdRsnScrout());
		contact.setDtCntctMnthlySummBeg(contactDto.getDtCntctMnthlySummBeg());
		contact.setDtCntctMnthlySummEnd(contactDto.getDtCntctMnthlySummEnd());
		contact.setDtCntctNextSummDue(contactDto.getDtCntctNextSummDue());
		contact.setDtContactApprv(contactDto.getDtContactApprv());
		contact.setDtContactOccurred(contactDto.getDtContactOccurred());
		contact.setDtLastEmpUpdate(contactDto.getDtLastEmpUpdate());
		contact.setDtLastUpdate(new Date());
		contact.setEstContactHours(contactDto.getEstContactHours());
		contact.setEstContactMins(contactDto.getEstContactMins());
		contact.setIdCase(contactDto.getIdCase());
		contact.setIdEvent(contactDto.getIdEvent());
		contact.setIdLastEmpUpdate(contactDto.getIdLastEmpUpdate());
		contact.setIndAnnounced(contactDto.getIndAnnounced());
		contact.setIndContactAttempted(contactDto.getIndContactAttempted());
		contact.setIndEmergency(contactDto.getIndEmergency());
		contact.setIndFamPlanComp(contactDto.getIndFamPlanComp());
		contact.setIndPrincipalInterview(contactDto.getIndPrincipalInterview());
		contact.setIndRecCons(contactDto.getIndRecCons());
		contact.setIndSafConResolv(contactDto.getIndSafConResolv());
		contact.setIndSafPlanComp(contactDto.getIndSafPlanComp());
		contact.setIndSiblingVisit(contactDto.getIndSiblingVisit());
		contact.setTxtComments(contactDto.getTxtComments());
		contact.setTxtKinCaregiver(contactDto.getKinCaregiver());
		contact.setEvent(event);
		contact.setStage(stage);
		contact.setPerson(person);
		Long idContact = (Long) sessionFactory.getCurrentSession().save(contact);
		return idContact;
	}

	/**
	 * Method Name: updateContact Method Description: This method updates
	 * CONTACT table.
	 *
	 * @param contactDto
	 * @return Long @
	 */
	@Override
	public Long updateContact(ContactDto contactDto) {
		Contact contact = (Contact) sessionFactory.getCurrentSession().get(Contact.class, contactDto.getIdEvent());
		if (TypeConvUtil.isNullOrEmpty(contact)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Csys07dDaoImpl.contact.not.found", null, Locale.US));
		}
		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, contactDto.getIdContactWorker());
		if (TypeConvUtil.isNullOrEmpty(person)) {
			throw new DataNotFoundException(messageSource.getMessage("ContactDao.person.NotFound", null, Locale.US));
		}
		contact.setPerson(person);
		sessionFactory.getCurrentSession().saveOrUpdate(contact);
		return ServiceConstants.ONE_VAL;
	}

	/**
	 *
	 * Method Name: queryStage Method Description:This method queries the
	 * database to find the Stage type
	 *
	 * @param ulIdStage
	 * @return @
	 */
	@Override
	public String queryStage(Long ulIdStage) {
		String szcdStage = new String();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Stage.class);
		criteria.add(Restrictions.eq("idStage", ulIdStage));
		Stage stage = (Stage) criteria.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(stage)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		ContactStageIdsDto contactStageIdsDto = new ContactStageIdsDto();
		if (!TypeConvUtil.isNullOrEmpty(stage.getCdStage()))
			contactStageIdsDto.setCdStage(stage.getCdStage());
		szcdStage = contactStageIdsDto.getCdStage();

		return szcdStage;
	}

	/**
	 * Method Name: getCaseInitiationContact Method Description:Check for case
	 * initiation contacts in a stage , given a stage id
	 *
	 * @param stageId
	 * @return Long @
	 */
	public Long getCaseInitiationContact(Long stageId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Contact.class);
		criteria.add(Restrictions.in("cdContactType", new String[] { ServiceConstants.SVC_CD_CONTACT_TYPE_HOURC24,
				ServiceConstants.SVC_CD_CONTACT_TYPE_HOURC24N }));
		criteria.add(Restrictions.eq("stage.idStage", stageId));
		criteria.setMaxResults(ServiceConstants.One);
		Contact contact = (Contact) criteria.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(contact)) {
			return ServiceConstants.ONE_LONG;
		}
		return ServiceConstants.ZERO_VAL;
	}

	/**
	 *
	 * Method Name: fetchFacilityType Method Description: Method to fetch
	 * facility type by idStage
	 *
	 * @param idStage
	 * @return String @
	 */
	public String fetchFacilityType(Long idStage) {

		String facilityType = "";
		int errorCode = 0;
		String errorMessage = ServiceConstants.EMPTY_STR;
		SessionImplementor sessionImplementor = (SessionImplementor) sessionFactory.getCurrentSession();
		ErrorDto errorDto = new ErrorDto();
		try {
			Connection connection = sessionImplementor.getJdbcConnectionAccess().obtainConnection();
			CallableStatement callableStatement = connection.prepareCall(getFacilityTypeProcedure);
			try {

				callableStatement.setLong(2, idStage);
				callableStatement.registerOutParameter(1, java.sql.Types.VARCHAR);
				callableStatement.registerOutParameter(3, java.sql.Types.BIGINT);
				callableStatement.registerOutParameter(4, java.sql.Types.VARCHAR);

				callableStatement.execute();
				facilityType = callableStatement.getString(1);
				errorCode = callableStatement.getInt(3);
				errorMessage = callableStatement.getString(4);
				callableStatement.close();
				connection.close();
			} catch (Exception e) {
				errorDto.setErrorMsg(
						"Validating Name failed. Please contact the CSC and provide them with the following information: Common Application database is down."
								+ " " + errorCode + " " + errorMessage);
			} finally {
				if (!ObjectUtils.isEmpty(callableStatement))
					callableStatement.close();
				if (!ObjectUtils.isEmpty(connection))
					connection.close();
			}
		} catch (SQLException e1) {
			log.error("SQLException occured" + e1.getMessage());
		}
		return facilityType;
	}

	/**
	 *
	 * Method Name: fetchCurrAbuseNglctInfo Method Description: Method to fetch
	 * abuse and neglect info
	 *
	 * @param idStage,idCase
	 * @return String @
	 */
	public CFTRlsInfoRptCPSValueDto fetchCurrAbuseNglctInfo(Long idStage, Long idCase) {

		CFTRlsInfoRptCPSValueDto cFTRlsInfoRptCPSValueDto = new CFTRlsInfoRptCPSValueDto();
		SessionImplementor sessionImplementor = (SessionImplementor) sessionFactory.getCurrentSession();
		ErrorDto errorDto = new ErrorDto();
		int errorCode = 0;
		String errorMessage = ServiceConstants.EMPTY_STR;
		try {
			Connection connection = sessionImplementor.getJdbcConnectionAccess().obtainConnection();
			CallableStatement callableStatement = connection.prepareCall(fetchCurrAbuseNglctInfo);
			try {
				callableStatement.setLong(2, idStage);
				callableStatement.setLong(1, idStage);
				callableStatement.setLong(2, idCase);
				callableStatement.registerOutParameter(3, java.sql.Types.VARCHAR);
				callableStatement.registerOutParameter(4, java.sql.Types.VARCHAR);
				callableStatement.registerOutParameter(5, java.sql.Types.VARCHAR);
				callableStatement.registerOutParameter(6, java.sql.Types.VARCHAR);
				callableStatement.registerOutParameter(7, java.sql.Types.ARRAY, "TYPE_CFT_SAFETY_INFO_ARR");
				callableStatement.registerOutParameter(8, java.sql.Types.VARCHAR);
				callableStatement.registerOutParameter(9, java.sql.Types.BIGINT);
				callableStatement.registerOutParameter(10, java.sql.Types.VARCHAR);

				cFTRlsInfoRptCPSValueDto.setSafetyRiskAssmnt(callableStatement.getString(3));
				cFTRlsInfoRptCPSValueDto.setServicesReferrals(callableStatement.getString(5));
				cFTRlsInfoRptCPSValueDto.setCaseAction(callableStatement.getString(6));
				cFTRlsInfoRptCPSValueDto.setCftSafetyAssessmentInfoDtoList(
						(List<CFTSafetyAssessmentInfoDto>) callableStatement.getObject(7));

				cFTRlsInfoRptCPSValueDto.setTxtSDMRAFinalRiskLevel(callableStatement.getString(8));
				errorCode = callableStatement.getInt(9);
				errorMessage = callableStatement.getString(10);

				callableStatement.close();

				connection.close();
			} catch (Exception e) {

				errorDto.setErrorMsg(
						"Validating Name failed. Please contact the CSC and provide them with the following information: Common Application database is down."
								+ " " + errorCode + " " + errorMessage);

			} finally {
				if (!ObjectUtils.isEmpty(callableStatement))
					callableStatement.close();
				if (!ObjectUtils.isEmpty(connection))
					connection.close();
			}
		} catch (SQLException e1) {
			log.error("SQLException occured" + e1.getMessage());
		}
		return cFTRlsInfoRptCPSValueDto;

	}

	/**
	 *
	 * Method Name: fetchPriorHistoryInfo Method Description: Method to fetch
	 * prior historyInfo
	 *
	 * @param idPerson
	 * @return idStage
	 */
	@SuppressWarnings("unchecked")
	public List<CFTRlsInfoRptCPSValueDto> fetchPriorHistoryInfo(Long idPerson, Long idStage) {

		int errorCode = 0;
		String errorMessage = ServiceConstants.EMPTY_STR;
		SessionImplementor sessionImplementor = (SessionImplementor) sessionFactory.getCurrentSession();
		List<CFTRlsInfoRptCPSValueDto> priorHistryList = new ArrayList<CFTRlsInfoRptCPSValueDto>();
		try {
			Connection connection = sessionImplementor.getJdbcConnectionAccess().obtainConnection();
			CallableStatement callableStatement = connection.prepareCall(fetchPriorHistoryInfo);
			try {

				callableStatement.setLong(1, idStage);
				callableStatement.setLong(2, idPerson);
				callableStatement.registerOutParameter(3, java.sql.Types.ARRAY, "TYPE_PRIOR_HISTORY_ARR");
				callableStatement.registerOutParameter(4, java.sql.Types.BIGINT);
				callableStatement.registerOutParameter(5, java.sql.Types.VARCHAR);

				callableStatement.executeQuery();

				Array priorHisArry = (Array) callableStatement.getObject(3);

				Object[] priorHistry = (Object[]) priorHisArry.getArray();

				CFTRlsInfoRptCPSValueDto priorHisBean = null;

				for (int i = 0; i < priorHistry.length; i++) {
					priorHisBean = new CFTRlsInfoRptCPSValueDto();
					Struct priorHistryRec = (Struct) priorHistry[i];
					Object priorHistryAtrr[] = priorHistryRec.getAttributes();
					priorHisBean.setIdCase(Long.valueOf(priorHistryAtrr[0].toString()));

					priorHisBean.setIdStage(Long.valueOf(priorHistryAtrr[1].toString()));
					priorHisBean.setIdPerson(Long.valueOf(priorHistryAtrr[2].toString()));
					if (priorHistryAtrr[3] != null && priorHistryAtrr[3].toString().length() > 0)
						priorHisBean.setSafetyRiskAssmnt(priorHistryAtrr[3].toString());
					if (priorHistryAtrr[4] != null && priorHistryAtrr[4].toString().length() > 0)
						priorHisBean.setServicesReferrals(priorHistryAtrr[4].toString());
					if (priorHistryAtrr[5] != null && priorHistryAtrr[5].toString().length() > 0)
						priorHisBean.setCaseAction(priorHistryAtrr[5].toString());
					if (priorHistryAtrr[6] != null && priorHistryAtrr[6].toString().length() > 0)
						priorHisBean.setRiskAssmntMsg(priorHistryAtrr[6].toString());

					priorHisBean.setCftSafetyAssessmentInfoDtoList(((List) priorHistryAtrr[7]));

					priorHisBean.setCdHistoryType("HS");

					if (priorHistryAtrr[8] != null && priorHistryAtrr[8].toString().length() > 0)
						priorHisBean.setTxtSDMRAFinalRiskLevel(priorHistryAtrr[8].toString());

					priorHistryList.add(priorHisBean);
				}
				errorCode = callableStatement.getInt(4);
				errorMessage = callableStatement.getString(5);
				if (errorCode < 0) {
					throw new SQLException(errorMessage);
				}
			} catch (Exception e) {
				log.error(e.getMessage());
				DataLayerException dataException = new DataLayerException(e.getMessage());
				dataException.initCause(e);
				throw dataException;
			} finally {
				if (!ObjectUtils.isEmpty(callableStatement))
					callableStatement.close();
				if (!ObjectUtils.isEmpty(connection))
					connection.close();
			}
		} catch (SQLException e1) {
			log.error("SQLException occured" + e1.getMessage());
		}
		return priorHistryList;
	}

	/**
	 *
	 * Method Name: fetchChildFatalityInfo Method Description: Method to fetch
	 * child fatality info
	 *
	 * @param idStage,idPerson,idUser
	 * @return String @
	 */
	@Transactional
	public ChildFatalityContactDto fetchChildFatalityInfo(Long idStage, Long idPerson, Long userId) {
		ResultSet resultSet = null;
		ChildFatalityContactDto currCFT1050BReportDto = new ChildFatalityContactDto();
		SessionImplementor sessionImplementor = (SessionImplementor) sessionFactory.getCurrentSession();
		Connection connection = null;
		CallableStatement callableStatement = null;
		try {
			connection = sessionImplementor.getJdbcConnectionAccess().obtainConnection();
			callableStatement = connection.prepareCall(fetchChildFatalityInfo);
			int errorCode = 0;
			String errorMessage = ServiceConstants.EMPTY_STRING;
			String operType = fetchFacilityType(idStage);

			if (operType.equals(ServiceConstants.TXT_UNKNOWN)) {
				CFTRlsInfoRptResourceValueDto currCFTRlsInfoRptResourceValueDto = new CFTRlsInfoRptResourceValueDto();
				currCFTRlsInfoRptResourceValueDto.setTxtOperationType(operType);
				// currCFT1050BReportDto.setRlsInfoRptRsrc(currCFTRlsInfoRptResourceValueDto);
			} else {
				if (null != connection)

					// ID_STAGE, ID_PERSON, operationType, resource ID are In &
					// 7
					// OUT parms including 6 cursors
					callableStatement.setLong(1, idStage);
				callableStatement.setLong(2, idPerson);
				callableStatement.setString(3, operType);

				callableStatement.registerOutParameter(4, java.sql.Types.VARCHAR, "p_oper_name");
				callableStatement.registerOutParameter(5, java.sql.Types.NUMERIC, "p_resource_id");
				callableStatement.registerOutParameter(6, java.sql.Types.REF_CURSOR, "p_cur_agency_history_ct");
				callableStatement.registerOutParameter(7, java.sql.Types.REF_CURSOR, "p_cur_agency_history_cl");
				callableStatement.registerOutParameter(8, java.sql.Types.REF_CURSOR, "p_cur_agency_history_inv");

				callableStatement.registerOutParameter(9, java.sql.Types.REF_CURSOR, "p_cur_allegations_y");
				callableStatement.registerOutParameter(10, java.sql.Types.REF_CURSOR, "p_cur_allegations_n");
				callableStatement.registerOutParameter(11, java.sql.Types.REF_CURSOR, "p_cur_std_violations");

				callableStatement.registerOutParameter(12, java.sql.Types.DATE);
				callableStatement.registerOutParameter(13, java.sql.Types.BIGINT);
				callableStatement.registerOutParameter(14, java.sql.Types.VARCHAR);

				callableStatement.executeQuery();

				String nameOfHome = callableStatement.getString(4);
				int nbrRsrc = callableStatement.getInt(5);

				Date dateOperationLicensed = callableStatement.getDate(12);
				errorCode = callableStatement.getInt(13);
				errorMessage = callableStatement.getString(14);

				if (errorCode < 0) {
					throw new SQLException(errorMessage);
				}

				CFTRlsInfoRptValueDto currCFTRlsInfoRptValueDto = new CFTRlsInfoRptValueDto();
				CFTRlsInfoRptResourceValueDto currCFTRlsInfoRptResourceValueDto = new CFTRlsInfoRptResourceValueDto();
				List<CFTRlsInfoRptCPAValueDto> currCFTRlsInfoRptCPAValueDtoList = new ArrayList<>();
				List<CFTRlsInfoRptAllegDispValueDto> currCFTRlsInfoRptAllegDispValueDtoList = new ArrayList<>();
				List<CFTRlsInfoRptRsrcVoltnsValueDto> currCFTRlsInfoRptRsrcVoltnsValueDtoList = new ArrayList<>();

				currCFTRlsInfoRptValueDto.setIdPerson(idPerson);
				currCFTRlsInfoRptValueDto.setDtCreated(new Date());
				currCFTRlsInfoRptValueDto.setIdCreatedPerson(userId);
				currCFTRlsInfoRptValueDto.setIdLastUpdatePerson(userId);
				currCFTRlsInfoRptValueDto.setDtLastUpdate(new Date());

				currCFTRlsInfoRptResourceValueDto.setNmRsrc(nameOfHome);
				currCFTRlsInfoRptResourceValueDto.setTxtOperationType(operType);
				currCFTRlsInfoRptResourceValueDto.setNbrRsrc(nbrRsrc);
				currCFTRlsInfoRptResourceValueDto.setDtCreated(new Date());
				currCFTRlsInfoRptResourceValueDto.setIdCreatedPerson(userId);
				currCFTRlsInfoRptResourceValueDto.setIdLastUpdatePerson(userId);
				currCFTRlsInfoRptResourceValueDto.setDtLastUpdate(new Date());

				currCFTRlsInfoRptResourceValueDto.setDtDateOperationLicensed(dateOperationLicensed);

				if (operType.equals(ServiceConstants.CRCLFACD_AFH)) {
					try {
						resultSet = (ResultSet) callableStatement.getObject(6);
						while (resultSet.next()) {
							CFTRlsInfoRptCPAValueDto currCFTRlsInfoRptCPAValueDto = new CFTRlsInfoRptCPAValueDto();
							currCFTRlsInfoRptCPAValueDto
									.setNmCpa(resultSet.getString(1) == null ? "" : resultSet.getString(1));
							currCFTRlsInfoRptCPAValueDto
									.setDtCpaVerified(resultSet.getDate(2) == null ? null : resultSet.getDate(2));
							currCFTRlsInfoRptCPAValueDto
									.setDtCpaLicensed(resultSet.getDate(3) == null ? null : resultSet.getDate(3));
							currCFTRlsInfoRptCPAValueDto.setDtAHVerifRelinquished(
									resultSet.getDate(4) == null ? null : resultSet.getDate(4));
							currCFTRlsInfoRptCPAValueDto.setReasonAHVerifRelinquished(
									resultSet.getString(5) == null ? "" : resultSet.getString(5));
							currCFTRlsInfoRptCPAValueDto.setDtCreated(new Date());
							currCFTRlsInfoRptCPAValueDto.setDtLastUpdate(new Date());

							currCFTRlsInfoRptCPAValueDtoList.add(currCFTRlsInfoRptCPAValueDto);
						}
					} catch (Exception e) {
						log.error(e.getMessage());
						DataLayerException dataException = new DataLayerException(e.getMessage());
						dataException.initCause(e);
						throw dataException;
					}

					try {
						resultSet = (ResultSet) callableStatement.getObject(7);
						while (resultSet.next()) {
							CFTRlsInfoRptCPAValueDto currCFTRlsInfoRptCPAValueDto = new CFTRlsInfoRptCPAValueDto();
							currCFTRlsInfoRptCPAValueDto
									.setNmCpa(resultSet.getString(1) == null ? "" : resultSet.getString(1));
							currCFTRlsInfoRptCPAValueDto
									.setDtCpaVerified(resultSet.getDate(2) == null ? null : resultSet.getDate(2));
							currCFTRlsInfoRptCPAValueDto
									.setDtCpaLicensed(resultSet.getDate(3) == null ? null : resultSet.getDate(3));
							currCFTRlsInfoRptCPAValueDto.setDtAHVerifRelinquished(
									resultSet.getDate(4) == null ? null : resultSet.getDate(4));
							currCFTRlsInfoRptCPAValueDto.setReasonAHVerifRelinquished(
									resultSet.getString(5) == null ? "" : resultSet.getString(5));
							currCFTRlsInfoRptCPAValueDto.setDtCreated(new Date());
							currCFTRlsInfoRptCPAValueDto.setDtLastUpdate(new Date());

							if (!currCFTRlsInfoRptCPAValueDtoList.contains(currCFTRlsInfoRptCPAValueDto)) {
								currCFTRlsInfoRptCPAValueDtoList.add(currCFTRlsInfoRptCPAValueDto);
							}
						}
					} catch (Exception e) {
						log.error(e.getMessage());
						DataLayerException dataException = new DataLayerException(e.getMessage());
						dataException.initCause(e);
						throw dataException;
					}

					try {
						resultSet = (ResultSet) callableStatement.getObject(8);
						while (resultSet.next()) {
							CFTRlsInfoRptCPAValueDto currCFTRlsInfoRptCPAValueDto = new CFTRlsInfoRptCPAValueDto();
							currCFTRlsInfoRptCPAValueDto
									.setNmCpa(resultSet.getString(1) == null ? "" : resultSet.getString(1));
							currCFTRlsInfoRptCPAValueDto
									.setDtCpaVerified(resultSet.getDate(2) == null ? null : resultSet.getDate(2));
							currCFTRlsInfoRptCPAValueDto
									.setDtCpaLicensed(resultSet.getDate(3) == null ? null : resultSet.getDate(3));
							currCFTRlsInfoRptCPAValueDto.setDtAHVerifRelinquished(
									resultSet.getDate(4) == null ? null : resultSet.getDate(4));
							currCFTRlsInfoRptCPAValueDto.setReasonAHVerifRelinquished(
									resultSet.getString(5) == null ? "" : resultSet.getString(5));
							currCFTRlsInfoRptCPAValueDto.setDtCreated(new Date());
							currCFTRlsInfoRptCPAValueDto.setDtLastUpdate(new Date());

							if (!currCFTRlsInfoRptCPAValueDtoList.contains(currCFTRlsInfoRptCPAValueDto)) {
								currCFTRlsInfoRptCPAValueDtoList.add(currCFTRlsInfoRptCPAValueDto);
							}
						}
					} catch (Exception e) {
						log.error(e.getMessage());
					}

				}

				try {
					resultSet = (ResultSet) callableStatement.getObject(9);
					while (resultSet.next()) {
						CFTRlsInfoRptAllegDispValueDto currCFTRlsInfoRptAllegDispValueDto = new CFTRlsInfoRptAllegDispValueDto();
						currCFTRlsInfoRptAllegDispValueDto.setDtInvStart((Date) resultSet.getDate(1));
						currCFTRlsInfoRptAllegDispValueDto.setTxtAllegType(resultSet.getString(2));
						currCFTRlsInfoRptAllegDispValueDto.setTxtAllegDisposition(resultSet.getString(3));
						currCFTRlsInfoRptAllegDispValueDto
								.setIndPendingAppeal(resultSet.getString(4) == null ? "N" : resultSet.getString(4));
						currCFTRlsInfoRptAllegDispValueDto.setIndDeceasedAllegedVictim(resultSet.getString(5));
						currCFTRlsInfoRptAllegDispValueDto.setDtCreated(new Date());
						currCFTRlsInfoRptAllegDispValueDto.setDtLastUpdate(new Date());

						currCFTRlsInfoRptAllegDispValueDtoList.add(currCFTRlsInfoRptAllegDispValueDto);
					}
				} catch (Exception e) {
					log.error(e.getMessage());
					DataLayerException dataException = new DataLayerException(e.getMessage());
					dataException.initCause(e);
					throw dataException;
				}

				try {
					resultSet = (ResultSet) callableStatement.getObject(10);
					while (resultSet.next()) {
						CFTRlsInfoRptAllegDispValueDto currCFTRlsInfoRptAllegDispValueDto = new CFTRlsInfoRptAllegDispValueDto();
						currCFTRlsInfoRptAllegDispValueDto.setDtInvStart((Date) resultSet.getDate(1));
						currCFTRlsInfoRptAllegDispValueDto.setTxtAllegType(resultSet.getString(2));
						currCFTRlsInfoRptAllegDispValueDto.setTxtAllegDisposition(resultSet.getString(3));
						currCFTRlsInfoRptAllegDispValueDto
								.setIndPendingAppeal(resultSet.getString(4) == null ? "N" : resultSet.getString(4));
						currCFTRlsInfoRptAllegDispValueDto.setIndDeceasedAllegedVictim(resultSet.getString(5));
						currCFTRlsInfoRptAllegDispValueDto.setDtCreated(new Date());
						currCFTRlsInfoRptAllegDispValueDto.setDtLastUpdate(new Date());

						currCFTRlsInfoRptAllegDispValueDtoList.add(currCFTRlsInfoRptAllegDispValueDto);
					}
				} catch (Exception e) {
					log.error(e.getMessage());
					DataLayerException dataException = new DataLayerException(e.getMessage());
					dataException.initCause(e);
					throw dataException;
				}

				try {
					resultSet = (ResultSet) callableStatement.getObject(11);
					while (resultSet.next()) {
						CFTRlsInfoRptRsrcVoltnsValueDto currCFTRlsInfoRptRsrcVoltnsValueDto = new CFTRlsInfoRptRsrcVoltnsValueDto();
						currCFTRlsInfoRptRsrcVoltnsValueDto.setDtViolation((Date) resultSet.getDate(1));
						currCFTRlsInfoRptRsrcVoltnsValueDto.setTxtTac(resultSet.getString(2));
						currCFTRlsInfoRptRsrcVoltnsValueDto.setTxtTacDesc(resultSet.getString(3));
						currCFTRlsInfoRptRsrcVoltnsValueDto.setDtCreated(new Date());
						currCFTRlsInfoRptRsrcVoltnsValueDto.setDtLastUpdate(new Date());

						currCFTRlsInfoRptRsrcVoltnsValueDtoList.add(currCFTRlsInfoRptRsrcVoltnsValueDto);
					}
				} catch (Exception e) {
					log.error(e.getMessage());
					DataLayerException dataException = new DataLayerException(e.getMessage());
					dataException.initCause(e);
					throw dataException;
				}
				currCFT1050BReportDto.setCftRlsInfoRpt(currCFTRlsInfoRptValueDto);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			DataLayerException dataException = new DataLayerException(e.getMessage());
			dataException.initCause(e);
			throw dataException;
		} finally {
			try {
				if (!ObjectUtils.isEmpty(callableStatement))
					callableStatement.close();
				if (!ObjectUtils.isEmpty(connection))
					connection.close();
			} catch (SQLException e) {
				log.error(e.getMessage());
			}
		}
		return currCFT1050BReportDto;
	}

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Override
	public ContactDto getByIdEvent(Long idEvent) {
		ContactDto contactDto = new ContactDto();
		Query queryContact = sessionFactory.getCurrentSession().createSQLQuery(getByEventId)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("cdContactType", StandardBasicTypes.STRING)
				.addScalar("cdContactPurpose", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(ContactDto.class));
		queryContact.setParameter("idEvent", idEvent);
		contactDto = (ContactDto) queryContact.uniqueResult();
		return contactDto;
	}

}
