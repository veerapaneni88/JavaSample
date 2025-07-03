package us.tx.state.dfps.service.casepackage.daoimpl;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.casemanagement.dto.NotificationFileDto;
import us.tx.state.dfps.casemanagement.dto.ProviderPlacementDto;
import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.CaseImageApiAudit;
import us.tx.state.dfps.common.domain.CgNotifFileUpload;
import us.tx.state.dfps.common.domain.CgNotifFileUploadDtl;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dto.CaseCheckoutPersonDto;
import us.tx.state.dfps.service.casepackage.dto.CaseStageSummaryDto;
import us.tx.state.dfps.service.casepackage.dto.CaseSummaryDto;
import us.tx.state.dfps.service.casepackage.dto.SelectStageDto;
import us.tx.state.dfps.service.casepackage.dto.ToDoDetailDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.dao.UnitDao;
import us.tx.state.dfps.service.common.request.CaregiverAckReq;
import us.tx.state.dfps.service.common.response.ListObjectRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.casepackage.dto.MergedIntakeARStageDto;

/**
 * 
 * ImpactWSIntegration - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:
 * CCMN37S Tuxedo DAM Name: CCMN15D, CCMND9D, CSECD2D, CSEC54D, CSECF4D Class
 * Description: Case Summary daoimpl to retrieve case summary Apr 14, 2017 -
 * 10:17:46 AM
 */

@Repository
public class CaseSummaryDaoImpl implements CaseSummaryDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CaseSummary.getCaseInfo}")
	private String getCaseInfoSql;

	@Value("${CaseSummary.cpsCaseStageInfo}")
	private String getCaseStageCpsInfo;

	@Value("${CaseSummary.apsCaseStageInfo}")
	private String getCaseStageApsInfo;

	@Value("${CaseSummary.cclCaseStageInfo}")
	private String getCaseStageCclInfo;

	@Value("${CaseSummary.dtlCaseStageInfo}")
	private String getCaseStageDtlInfo;

	@Value("${CaseSummary.checkChildFt}")
	private String getCheckChildFtStPro;

	@Value("${CaseSummary.phonePersonCaseStageInfo}")
	private String getCaseStagephonePersonInfo;

	@Value("${CaseSummary.intStageIdInfo}")
	private String getIntStageIdInfo;

	@Value("${CaseSummary.incomingDetail}")
	private String getIncomingCall;

	@Value("${CaseSummary.isSDMEventExists}")
	private String isSDMEventExists;

	@Value("${CaseSummary.getCaseCheckoutPerson}")
	private String getCaseCheckoutPerson;

	@Value("${CaseSummary.getStageCommon}")
	private String getStageCommon;

	@Value("${CaseSummary.getPriorStage}")
	private String getPriorStage;

	@Value("${CaseSummary.getLaterStage}")
	private String getLaterStage;

	@Value("${CaseSummary.getCurrentStage}")
	private String getCurrentStage;

	@Value("${CaseSummaryDaoImpl.getCountForCpsInvCnclsn}")
	private String getCount;

	@Value("${CaseSummaryDao.hasStageAccessToAnyStage}")
	private String stageAccessToAnyStage;

	@Value("${CaseSummary.getApprovalToDo}")
	private String getApprovalToDo;

	@Value("${CaseSummary.getToDo}")
	private String getToDo;

	@Value("${CaseSummary.getPendingEventTasks}")
	private String getPendingEventTasks;

	@Value("${CaseSummary.getStageByTypeAndPriorStage}")
	private String getStageByTypeAndPriorStage;

	@Value("${CaseSummary.hasAccessToCase}")
	private String hasAccessToCase;

	@Value("${CaseSummary.getOldestIntakeStageByCaseId}")
	private String getOldestIntakeStageByCaseId;

	@Value("${CaseSummary.getRegionByCounty}")
	private String getRegionByCounty;

	@Value("${CaseSummaryDaoImpl.getIntIntakeDate}")
	private String getIntIntakeDate;

	@Value("${CapsCaseDaoImpl.getLaterSage}")
	private String getLaterSageSql;
	
	@Value("${CaseSummaryDaoImpl.getIntStgIdForSelctdStg}")
	private String getIntStgIdForSelctdStgSql;
	
	@Value("${CaseSummaryDaoImpl.getPriorStgWthStgType}")
	private String getPriorStgWthStgTypeSql;

	@Value("${CaseSummary.phonePersonCaseStageInfoForMPS}")
	private String getCaseStagephonePersonInfoForMPS;

	@Value("${CaseSummary.incomingDetailForMPS}")
	private String getIncomingCallForMPS;

	@Value("${CaseSummary.apsCaseStageInfoForMPS}")
	private String getCaseStageApsInfoForMPS;

	@Value("${CaseSummaryDaoImpl.getIntIntakeDateForMPS}")
	private String getIntIntakeDateForMPS;


	@Value("${CaseSummaryDaoImpl.getIntStgIdForSelctdStgForMPS}")
	private String getIntStgIdForSelctdStgSqlForMPS;

	@Value("${CaseSummaryDaoImpl.getLinkedIntakeARStages}")
	private String getLinkedIntakeARStages;


    @Value("${CaseSummaryDaoImpl.fetchCurrentPlacementInformation}")
    private String fetchCurrentPlacementInformation;

    @Value("${CaseSummaryDaoImpl.getNeubusIdentifiers}")
    private String getNeubusIdentifiersSql;

	@Value("${CaseSummaryDaoImpl.getNebusRecordById}")
	private String getNebusRecordByIdSql;

    @Value("${CaseSummaryDaoImpl.findAckRequestParameters}")
    private String findAckRequestParameters;

	@Value("${CaseSummaryDaoImpl.findVendorId}")
	private String findVendorId;

	@Value("${CaseSummaryDaoImpl.sqlToGetCodeFromCodesTable}")
	private String sqlToGetCodeFromCodesTable;

	@Autowired
	StageDao stageDao;

	@Autowired
	UnitDao unitDao;

	@Autowired
	MobileUtil mobileUtil;

	private ProjectionList projectionCache = null;
	private ProjectionList fileProjectionCache = null;
    private ProjectionList recordProjectionCache = null;

	private static final Logger log = Logger.getLogger(CaseSummaryDaoImpl.class);

	public CaseSummaryDaoImpl() {

	}

	/**
	 * 
	 * Method Description: This method is used to retrieve Case summary based on
	 * id case Tuxedo Service Name:CCMN37S Tuxedo DAM Name :CCMND9D
	 * 
	 * @param idCase
	 * @throws ServiceExceptio
	 * @returns caseInfo
	 */
	@Override
	public CaseSummaryDto getCaseInfo(Long idCase) {

		CaseSummaryDto caseInfo = new CaseSummaryDto();

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getCaseInfoSql)
				.addScalar("caseRegion", StandardBasicTypes.STRING)
				.addScalar("caseSpeclHndlg", StandardBasicTypes.STRING)
				.addScalar("dtCaseClosed", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtCaseOpened", StandardBasicTypes.TIMESTAMP).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("nmCase", StandardBasicTypes.STRING).addScalar("indCaseSensitive", StandardBasicTypes.STRING)
				.addScalar("indCaseWorkerSafety", StandardBasicTypes.STRING).addScalar("txtWrkrSafty", StandardBasicTypes.STRING).setParameter("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(CaseSummaryDto.class));
		caseInfo = (CaseSummaryDto) query.uniqueResult();
		return caseInfo;
	}

	/**
	 * 
	 * Method Description: This method is used to retrieve case summary based on
	 * case and stage table from CPS Program Tuxedo Service Name:CCMN37S Tuxedo
	 * DAM Name :CCMN15D
	 * 
	 * @param idCase
	 * @param stagePersonRoleOpen
	 * @param stagePersonRoleClose
	 * @param personPhonePrimary
	 * @param personPhoneType
	 * @ @returns caseStageInfo
	 */

	@SuppressWarnings("unchecked")
	private List<CaseStageSummaryDto> caseSummary(String caseSummary, Long idCase) {
		List<CaseStageSummaryDto> caseStageInfo = null;
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(caseSummary)
				.addScalar("nmStage", StandardBasicTypes.STRING).addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("cdStageType", StandardBasicTypes.STRING)
				.addScalar("dtStageStart", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtStageClose", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdStageRegion", StandardBasicTypes.STRING)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("idSituation", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdCpsOverallDisptn", StandardBasicTypes.STRING)
				.addScalar("dtMultiRef", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtStageCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("tmStageCreated", StandardBasicTypes.STRING)
				.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING).setParameter("idCase", idCase)
				.setParameter("stagePersonRoleClose", ServiceConstants.stagePersonRoleClose)
				.setParameter("stagePersonRoleOpen", ServiceConstants.stagePersonRoleOpen)
				.setResultTransformer(Transformers.aliasToBean(CaseStageSummaryDto.class));
		caseStageInfo = (List<CaseStageSummaryDto>) query.list();
		return caseStageInfo;
	}

	// CPS
	@Override
	public List<CaseStageSummaryDto> getCaseStageCPSInfo(Long idCase) {
		List<CaseStageSummaryDto> caseStageInfo = null;
		caseStageInfo = caseSummary(getCaseStageCpsInfo, idCase);
		return caseStageInfo;
	}

	/**
	 * 
	 * Method Description: This method is used to retrieve case summary based on
	 * case and stage table from APS Program Tuxedo Service Name:CCMN37S Tuxedo
	 * DAM Name :CCMN15D
	 * 
	 * @param idCase
	 * @param stagePersonRoleOpen
	 * @param stagePersonRoleClose
	 * @param personPhonePrimary
	 * @param personPhoneType
	 * @ @returns caseStageInfo
	 */
	@Override
	public List<CaseStageSummaryDto> getCaseStageAPSInfo(Long idCase) {
		List<CaseStageSummaryDto> caseStageInfo = null;
		if(mobileUtil.isMPSEnvironment()){
			caseStageInfo = caseSummary(getCaseStageApsInfoForMPS, idCase);
		}else {
		caseStageInfo = caseSummary(getCaseStageApsInfo, idCase);
		}
		return caseStageInfo;
	}

	/**
	 * 
	 * Method Description: This method is used to retrieve case summary based on
	 * case and stage table from CCL Program Tuxedo Service Name:CCMN37S Tuxedo
	 * DAM Name :CCMN15D
	 * 
	 * @param idCase
	 * @param stagePersonRoleOpen
	 * @param stagePersonRoleClose
	 * @param personPhonePrimary
	 * @param personPhoneType
	 * @ @returns caseStageInfo
	 */

	@Override
	public List<CaseStageSummaryDto> getCaseStageCCLInfo(Long idCase) {
		List<CaseStageSummaryDto> caseStageInfo = null;
		caseStageInfo = caseSummary(getCaseStageCclInfo, idCase);
		return caseStageInfo;
	}

	/**
	 * 
	 * Method Description: This method is used to retrieve case summary based on
	 * case and stage table from DTL Program Tuxedo Service Name:CCMN37S Tuxedo
	 * DAM Name :CCMN15D
	 * 
	 * @param idCase
	 * @param stagePersonRoleOpen
	 * @param stagePersonRoleClose
	 * @param personPhonePrimary
	 * @param personPhoneType
	 * @ @returns caseStageInfo
	 */
	@Override
	public List<CaseStageSummaryDto> getCaseStageDTLInfo(Long idCase) {
		List<CaseStageSummaryDto> caseStageInfo = null;
		caseStageInfo = caseSummary(getCaseStageDtlInfo, idCase);
		return caseStageInfo;
	}

	@SuppressWarnings("unchecked")
	public List<CaseStageSummaryDto> getCaseStagePhonePersonInfo(Long idPerson) {
		List<CaseStageSummaryDto> caseStageInfo = null;
		if(mobileUtil.isMPSEnvironment()){
			Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getCaseStagephonePersonInfoForMPS)
					.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("nbrPhone", StandardBasicTypes.STRING).addScalar("endDate", StandardBasicTypes.DATE)
					.setParameter("idPerson", idPerson)
					.setParameter("indPersonPhonePrimary", ServiceConstants.indPersonPhonePrimary)
					.setParameter("indPersonPhoneType", ServiceConstants.indPersonPhoneType)
					.setResultTransformer(Transformers.aliasToBean(CaseStageSummaryDto.class));
			caseStageInfo = (List<CaseStageSummaryDto>) query.list();
		}else {
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getCaseStagephonePersonInfo)
					.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("nbrPhone", StandardBasicTypes.STRING).addScalar("endDate", StandardBasicTypes.DATE)
				.setParameter("idPerson", idPerson)
				.setParameter("indPersonPhonePrimary", ServiceConstants.indPersonPhonePrimary)
				.setResultTransformer(Transformers.aliasToBean(CaseStageSummaryDto.class));
		caseStageInfo = (List<CaseStageSummaryDto>) query.list();
		}
		return caseStageInfo;
	}

	/**
	 * 
	 * Method Description: This method is used to get prior stage id based on id
	 * stage as input Tuxedo Service Name: CCMN37S Tuxedo DAM Name :CSECD2D
	 * 
	 * @param idStage
	 * @ @return intStageInfoResultSet
	 */

	@Override
	public Long getStageMergeInfo(Long idStage) {
		Long intStageInfoResultSet = 0l;

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getIntStageIdInfo)
				.addScalar("idPriorStage", StandardBasicTypes.LONG).setParameter("idStage", idStage);
		intStageInfoResultSet = (Long) query.uniqueResult();
		return intStageInfoResultSet;
	}

	/**
	 * 
	 * Method Description: This Method is used to retrieve Date of Incoming
	 * Detail by giving id stage as input Tuxedo Service Name: CCMN37S Tuxedo
	 * DAM Name : CSEC54D
	 * 
	 * @param idStage
	 * @ @return date
	 */

	@Override
	public CaseStageSummaryDto getIncomingDetail(Long idStage) {
		CaseStageSummaryDto dtIncomingCall = new CaseStageSummaryDto();
		if(mobileUtil.isMPSEnvironment()){
			Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getIncomingCallForMPS)
					.addScalar("dtIncomingCall", StandardBasicTypes.TIMESTAMP)
					.addScalar("tmIncmgCall", StandardBasicTypes.STRING).setParameter("idStage", idStage)
					.setResultTransformer(Transformers.aliasToBean(CaseStageSummaryDto.class));
			dtIncomingCall = (CaseStageSummaryDto) query.uniqueResult();
		}else {
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getIncomingCall)
				.addScalar("dtIncomingCall", StandardBasicTypes.TIMESTAMP)
				.addScalar("tmIncmgCall", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(CaseStageSummaryDto.class));
		dtIncomingCall = (CaseStageSummaryDto) query.uniqueResult();
		}
		return dtIncomingCall;
	}

	/**
	 * 
	 * Method Description: This method is used to check for child fatality from
	 * stored procedure Tuxedo Service Name:CCMN37S Tuxedo DAM Name :CSECF4D
	 * 
	 * @param idCase
	 * @ @return isValid
	 * @throws SQLException
	 */

	@Override
	public boolean checkChildFt(Long idCase) {
		String fatalitylValue = "";
		int errorCode = 0;
		String errorMessage = null;
		SessionImplementor sessionImplementor = (SessionImplementor) sessionFactory.getCurrentSession();
		ErrorDto errorDto = new ErrorDto();
		try {
			Connection connection = sessionImplementor.getJdbcConnectionAccess().obtainConnection();
			CallableStatement callableStatement = connection.prepareCall(getCheckChildFtStPro);
			try {
				callableStatement.setLong(1, idCase);
				callableStatement.registerOutParameter(2, java.sql.Types.VARCHAR);
				callableStatement.registerOutParameter(3, java.sql.Types.BIGINT);
				callableStatement.registerOutParameter(4, java.sql.Types.VARCHAR);

				callableStatement.execute();
				fatalitylValue = callableStatement.getString(2);
				// log.info("fatalitylValue: " + fatalitylValue);

				errorCode = callableStatement.getInt(3);
				errorMessage = callableStatement.getString(4);
				callableStatement.close();
				connection.close();
			} catch (Exception e) {
				log.error("error occured in checkChildFt of CaseSummaryDaoImpl:" + errorCode + " message:"
						+ errorMessage);
				errorDto.setErrorMsg(
						"Validating Name failed. Please contact the CSC and provide them with the following information: Common Application database is down.");
			} finally {
				if (!ObjectUtils.isEmpty(callableStatement))
					callableStatement.close();
				if (!ObjectUtils.isEmpty(connection))
					connection.close();
			}
		} catch (SQLException e1) {
			log.error("SQLException occured" + e1.getMessage());
		}
		return ServiceConstants.Y.equalsIgnoreCase(fatalitylValue) ? true : false;
	}

	/**
	 * 
	 * Method Description: This method is used to get the count of AFC stage
	 * which are still pending approval for a case Tuxedo Service Name: NA
	 * Tuxedo DAM Name: NA
	 * 
	 * @paramidCase - Case ID
	 * @returnLong
	 */

	@Override
	public Long getAFCPendingCount(Long idCase) {
		Long afcPendingCount = 0l;

		Criteria returnAFCCountCr = sessionFactory.getCurrentSession().createCriteria(Event.class)
				.setProjection(Projections.rowCount());
		returnAFCCountCr.add(Restrictions.eq("idCase", idCase)).add(Restrictions.eq("cdTask", "2460"))
				.add(Restrictions.eq("cdEventStatus", "PROC"));

		afcPendingCount = (Long) returnAFCCountCr.uniqueResult();

		return afcPendingCount;
	}

	/**
	 * 
	 * Method Description: This method is used to check if an SDM assessment
	 * exists for the stage ID passed Tuxedo Service Name: NA Tuxedo DAM Name:
	 * NA
	 * 
	 * @paramidStage - Stage ID
	 * @returnLong
	 */

	@Override
	public Long sdmEventExists(Long idStage) {

		Long sdmEventCount = 0l;

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(isSDMEventExists)
				.addScalar("REC_EXISTS", StandardBasicTypes.LONG);
		query.setParameter("idStage", idStage);
		sdmEventCount = (Long) query.uniqueResult();
		return sdmEventCount;

	}

	/**
	 * 
	 * Method Description: This method is used to get the person ID who has
	 * checked out the case for the passed stage ID Tuxedo Service Name: NA
	 * Tuxedo DAM Name: NA
	 * 
	 * @paramidStage - Stage ID
	 * @returnLong
	 */

	@Override
	public Long getCaseCheckoutPerson(Long idStage) {

		CaseCheckoutPersonDto caseCheckoutPersonDto = new CaseCheckoutPersonDto();

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getCaseCheckoutPerson)
				.addScalar("idWkldPerson", StandardBasicTypes.LONG)
				.addScalar("cdMobileStatus", StandardBasicTypes.STRING)
				.addScalar("cdWkldStagePersRole", StandardBasicTypes.STRING).setParameter("idWkldStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(CaseCheckoutPersonDto.class));
		caseCheckoutPersonDto = (CaseCheckoutPersonDto) query.uniqueResult();
		if (null == caseCheckoutPersonDto) {
			return 0l;
		} else {
			return caseCheckoutPersonDto.getIdWkldPerson();
		}

	}

	/**
	 * 
	 * Method Description: Returns details for the stage passed. When the type
	 * is current, the passed stage details is fetched. When the type is prior,
	 * the details of the stage prior to the one passed if fetched
	 * 
	 * Tuxedo Service Name: NA Tuxedo DAM Name: NA
	 * 
	 * @param idStage
	 *            - Stage ID
	 * @ @return SelectStageDto
	 */

	@SuppressWarnings("unchecked")
	@Override
	public SelectStageDto getStage(Long idStage, String stageType) {

		List<SelectStageDto> selectStageDtoList = new ArrayList<SelectStageDto>();

		StringBuilder getStageSQL = new StringBuilder(getStageCommon);
		if (stageType.equals(ServiceConstants.STAGE_PRIOR)) {
			getStageSQL.append(ServiceConstants.CHAR_SPACE);
			getStageSQL.append(getPriorStage);
		} else if (stageType.equals(ServiceConstants.STAGE_CURRENT)) {
			getStageSQL.append(ServiceConstants.CHAR_SPACE);
			getStageSQL.append(getCurrentStage);
		} else if (stageType.equals(ServiceConstants.STAGE_LATER)) {
			getStageSQL.append(ServiceConstants.CHAR_SPACE);
			getStageSQL.append(getLaterStage);
		}
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getStageSQL.toString())
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idSituation", StandardBasicTypes.LONG).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("cdStage", StandardBasicTypes.STRING).addScalar("cdStageType", StandardBasicTypes.STRING)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("cdStageClassification", StandardBasicTypes.STRING)
				.addScalar("dtStartDate", StandardBasicTypes.TIMESTAMP)
				.addScalar("indStageClose", StandardBasicTypes.STRING)
				.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("idUnit", StandardBasicTypes.LONG).addScalar("nmCase", StandardBasicTypes.STRING)
				.addScalar("dtStageClose", StandardBasicTypes.TIMESTAMP).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(SelectStageDto.class));
		selectStageDtoList = (List<SelectStageDto>) query.list();

		if (selectStageDtoList.size() > 0) {
			return selectStageDtoList.get(0);
		} else {
			return new SelectStageDto();
		}

	}

	/**
	 * 
	 * Method Description: Method will fetch the last update date for the passed
	 * Entity Class
	 * 
	 * Tuxedo Service Name: NA Tuxedo DAM Name: NA
	 * 
	 * @paramentityClass - The Entity Class to fetch
	 * @paramprimaryKey - Primary Key for the Entity Class
	 * @parmaentityID - value for the Primary Key
	 * @returnDate
	 */

	@Override
	public Date getLastUpdateDate(String entityClass, String primaryKey, Long entityID) {

		Date lastUpdatedDate = new Date();

		lastUpdatedDate = (Date) sessionFactory.getCurrentSession().createCriteria(Stage.class)
				.setProjection(Projections.property("dtLastUpdate")).add(Restrictions.eq(primaryKey, entityID))
				.uniqueResult();

		return lastUpdatedDate;

	}

	/**
	 * Method Description: This method returns if all questions have been
	 * answered. Service Name: CpsInvCnclsn
	 * 
	 * @param idStage
	 * @return Integer @
	 */
	public Integer getCountForCpsInvCnclsn(Long idStage) {
		Integer count = 0;

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getCount).setParameter("allgStgId",
				idStage);
		count = ((BigDecimal) query.uniqueResult()).intValueExact();

		return count;
	}

	/**
	 * 
	 * Method Description: Method to get a case record, given the case id.
	 * 
	 * Tuxedo Service Name: NA Tuxedo DAM Name: NA
	 * 
	 * @param idStage
	 *            - caseId
	 * @ @return CaseSummaryDto
	 */
	@Override
	@SuppressWarnings("unchecked")
	public CaseSummaryDto getCaseDetails(Long caseId) {

		List<CaseSummaryDto> caseSummaryDto = (List<CaseSummaryDto>) sessionFactory.getCurrentSession()
				.createCriteria(CapsCase.class, "case")
				.setProjection(Projections.projectionList().add(Projections.property("dtCaseClosed"), "dtCaseClosed")
						.add(Projections.property("cdCaseProgram"), "cdCaseProgram"))
				.add(Restrictions.eq("case.idCase", caseId))
				.setResultTransformer(Transformers.aliasToBean(CaseSummaryDto.class)).list();
		if (null == caseSummaryDto) {
			return null;
		} else {
			return caseSummaryDto.get(0);
		}

	}

	/**
	 * Method Description: Method to look at the case to determine if the given
	 * user has access to any open or closed stage.
	 * 
	 * @paramindStageClose, idCase, userID
	 * 
	 * @returnBoolean
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Boolean hasStageAccessToAnyStage(String indStageClose, Long ulIdCase, Long ulIdPerson) {
		Boolean bStageAccess = Boolean.FALSE;

		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();

		List<PersonDto> personList = (List<PersonDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(stageAccessToAnyStage).addScalar("idPerson", StandardBasicTypes.LONG)
				.setParameter("idCase", ulIdCase).setParameter("idPerson", ulIdPerson)
				.setParameter("indStageClose", indStageClose).setParameter("now", now)
				.setParameter("CUNMBRRL_20", ServiceConstants.CUNMBRRL_20)
				.setParameter("CUNMBRRL_30", ServiceConstants.CUNMBRRL_30)
				.setParameter("CUNMBRRL_40", ServiceConstants.CUNMBRRL_40)
				.setResultTransformer(Transformers.aliasToBean(PersonDto.class)).list();

		if (!personList.isEmpty())
			bStageAccess = Boolean.TRUE;

		return bStageAccess;
	}

	/**
	 * 
	 * Method Description: Returns a the event and todo id's for an approval
	 * given an event from the stage.
	 * 
	 * Tuxedo Service Name: NA Tuxedo DAM Name: NA
	 * 
	 * @paramidEvent - Event ID
	 * @returnList<ARPendingStagesDto>
	 */

	@Override
	public ToDoDetailDto getApprovalToDo(Long idEvent) {

		ToDoDetailDto toDoDetailDto = new ToDoDetailDto();

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getApprovalToDo)
				.addScalar("idToDo", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(ToDoDetailDto.class));
		List<ToDoDetailDto> results = (List<ToDoDetailDto>) query.list();
		if (!ObjectUtils.isEmpty(results)) {
			toDoDetailDto = results.get(ServiceConstants.Zero);
		}
		return toDoDetailDto;
	}

	/**
	 * 
	 * Method Description: Returns a the event and todo id's for an approval
	 * given an event from the stage.
	 * 
	 * Tuxedo Service Name: NA Tuxedo DAM Name: NA
	 * 
	 * @paramidToDo - To Do ID
	 * @returnList<ARPendingStagesDto>
	 */

	@Override
	public ToDoDetailDto getToDo(Long idToDo) {

		ToDoDetailDto toDoDetailDto = new ToDoDetailDto();

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getToDo)
				.addScalar("idToDo", StandardBasicTypes.LONG).addScalar("idPersonAssigned", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("nmCase", StandardBasicTypes.STRING)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("cdStage", StandardBasicTypes.STRING).addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("cdStageType", StandardBasicTypes.STRING).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING).addScalar("cdTask", StandardBasicTypes.STRING)
				.addScalar("cdTaskEventType", StandardBasicTypes.STRING)
				.addScalar("indStageClosure", StandardBasicTypes.STRING)
				.addScalar("eventDetailUrl", StandardBasicTypes.STRING).setParameter("idToDo", idToDo)
				.setResultTransformer(Transformers.aliasToBean(ToDoDetailDto.class));
		toDoDetailDto = (ToDoDetailDto) query.uniqueResult();

		return toDoDetailDto;

	}

	/**
	 * 
	 * Method Description: Returns a the event and todo id's for an approval
	 * given an event from the stage.
	 * 
	 * Tuxedo Service Name: NA Tuxedo DAM Name: NA
	 * 
	 * @paramidStage - Stage ID
	 * @returnListObjectRes
	 */

	@SuppressWarnings("unchecked")
	@Override
	public ListObjectRes getPendingEventTasks(Long idStage) {

		ListObjectRes listObjectRes = new ListObjectRes();
		List<String> pendingTasksList = new ArrayList<>();

		Query query = (Query) sessionFactory.getCurrentSession().createQuery(getPendingEventTasks)
				.setParameter("idStage", idStage);
		pendingTasksList = query.list();

		listObjectRes.setObjectList(new ArrayList<Object>(pendingTasksList));
		return listObjectRes;

	}

	/**
	 * 
	 * Method Description: Returns details for the stage of the given type, if
	 * one exists, that originated from the given stage id. (USAGE: This method
	 * was written for SIR 16114 to find the FSU stage that most closely
	 * precedes the FRE stage with the given start date.)
	 * @parampriorStageID - Prior Stage ID
	 * @paramstageCode - Stage Code
	 * @returnSelectStageDto
	 */

	@SuppressWarnings("unchecked")
	@Override
	public SelectStageDto getStageByTypeAndPriorStage(Long priorStageID, String stageCode) {
		SelectStageDto selectStageDto = null;
		List<SelectStageDto> selectStageDtoList = new ArrayList<SelectStageDto>();
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getStageByTypeAndPriorStage)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdStage", StandardBasicTypes.STRING)
				.setParameter("priorStageID", priorStageID).setParameter("cdStage", stageCode)
				.setResultTransformer(Transformers.aliasToBean(SelectStageDto.class));
		selectStageDtoList = (List<SelectStageDto>) query.list();
		if(!ObjectUtils.isEmpty(selectStageDtoList)){
			selectStageDto = selectStageDtoList.get(0);
		}
		return selectStageDto;
	}

	/**
	 * 
	 * Method Description: Returns the most recent event id, event status, task
	 * code and timestamp for the given stage and event type.
	 * 
	 * Tuxedo Service Name: NA Tuxedo DAM Name: NA
	 * 
	 * @paramidStage - Stage ID
	 * @paramcdEventType - Event Type
	 * @returnSelectStageDto
	 */

	@SuppressWarnings("unchecked")
	@Override
	public EventDto getEventByStageAndEventType(Long idStage, String cdEventType) {

		List<EventDto> eventDtoList = new ArrayList<EventDto>();

		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("idEvent"), "idEvent")
				.add(Projections.property("cdEventStatus"), "cdEventStatus")
				.add(Projections.property("cdTask"), "cdTask")
				.add(Projections.property("dtLastUpdate"), "dtLastUpdate");
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class).setProjection(projectionList)
				.add(Restrictions.eq("stage.idStage", idStage)).add(Restrictions.eq("cdEventType", cdEventType))
				.addOrder(Order.desc("idEvent")).setResultTransformer(Transformers.aliasToBean(EventDto.class));

		eventDtoList = criteria.list();

		if (eventDtoList.size() > 0) {
			return eventDtoList.get(0);
		} else {
			return new EventDto();
		}

	}

	/**
	 * 
	 * Method Description: Looks at the case to determine if the given user has
	 * access to any stage. Use this version of the method if you want to test
	 * access for the current user. The following items are checked: primary
	 * worker assigned to stage, one of the four secondary workers assigned to
	 * the stage, the supervisor of any of the above, the designee of any of the
	 * above supervisors
	 * 
	 * Tuxedo Service Name: NA Tuxedo DAM Name: NA
	 * 
	 * @paramidCAse - Case ID
	 * @paramidPerson - Person ID
	 * @returnBoolean
	 */

	@Override
	public Boolean hasAccessToCase(Long idCase, Long idPerson) {
		Long personID = 0l;

		personID = (Long) sessionFactory.getCurrentSession().createSQLQuery(hasAccessToCase)
				.addScalar("idPerson", StandardBasicTypes.LONG).setParameter("idCase", idCase)
				.setParameter("idPerson", idPerson).setParameter("currentDate", new Date())
				.setParameterList("memberRoles", ServiceConstants.MEMBER_ROLES).uniqueResult();

		if (null != personID && personID > 0) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}

	}

	/**
	 * Method Description: This method inserts closed case image access audit
	 * data into CASE_IMAGE_API_AUDIT table. Service Name: ClosedCaseImageAccess
	 * 
	 * @param idCase
	 * @param idPerson
	 * @return String @
	 * 
	 */
	public String insertApiAuditRecord(Long idCase, Long idPerson) {

		CaseImageApiAudit caseImageApiAudit = new CaseImageApiAudit();
		String returnMsg = "";
		Date date = new Date();
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.passeDatTxt(date)))
			caseImageApiAudit.setDtImageApiAccessed(date);
		caseImageApiAudit.setDtLastUpdate(date);
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(idCase)))
			caseImageApiAudit.setIdCase(idCase);
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(idPerson)))
			caseImageApiAudit.setIdPerson(idPerson);
		sessionFactory.getCurrentSession().save(caseImageApiAudit);
		returnMsg = ServiceConstants.SUCCESS;

		return returnMsg;
	}

	/**
	 * Method Description: Retrieves the Stage details for the passed Case ID
	 * 
	 * @param idCase
	 * @return StageDto @
	 * 
	 */
	@SuppressWarnings("unchecked")
	public StageDto getOldestIntakeStageByCaseId(Long idCase) {

		StageDto oldestIntake = new StageDto();

		List<StageDto> caseStages = ((List<StageDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getOldestIntakeStageByCaseId).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP).addScalar("idUnit", StandardBasicTypes.LONG)
				.addScalar("dtStageClose", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdStageType", StandardBasicTypes.STRING)
				.addScalar("cdStageClassification", StandardBasicTypes.STRING)
				.addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("cdStageCurrPriority", StandardBasicTypes.STRING)
				.addScalar("cdStageInitialPriority", StandardBasicTypes.STRING)
				.addScalar("cdStageRsnPriorityChgd", StandardBasicTypes.STRING)
				.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("indStageClose", StandardBasicTypes.STRING)
				.addScalar("stagePriorityCmnts", StandardBasicTypes.STRING)
				.addScalar("cdStageCnty", StandardBasicTypes.STRING).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("cdStageRegion", StandardBasicTypes.STRING)
				.addScalar("dtStageCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idSituation", StandardBasicTypes.LONG)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING).addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("stageClosureCmnts", StandardBasicTypes.STRING).setParameter("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(StageDto.class)).list());

		if (!caseStages.isEmpty()) {
			oldestIntake = caseStages.get(0);
		}

		return oldestIntake;
	}

	/**
	 * Method Description: Update the stage close date for the new reason
	 * 
	 * @param idStage
	 * @return String @
	 * 
	 */
	@Override
	public String updateStageCloseReason(Long idStage) {
		Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, idStage);
		stage.setDtStageClose(new Date());
		stage.setIndStageClose(ServiceConstants.Y);
		sessionFactory.getCurrentSession().update(stage);
		return ServiceConstants.SUCCESS;
	}

	/**
	 * 
	 * Method Name: getRegionByCounty Method Description:
	 * 
	 * @param county
	 * @return
	 */
	@Override
	public String getRegionByCounty(String county) {
		String region = (String) sessionFactory.getCurrentSession().createSQLQuery(getRegionByCounty)
				.setParameter("cdCounty", county).uniqueResult();
		return region;
	}

	/**
	 * Method Name: CVSRemovalAlert Method Description: This method will trigger
	 * alert in “CVS Removal” page
	 * 
	 * @param cnsrvtrshpRemovalAlertReq
	 * @param idEvent
	 * @param idPerson
	 * @return @
	 */
	public String getAlertForIntakeNotes(Long idStage, String nmStage, Long idCase) {
		boolean isAlertSend = false;
		if (!ObjectUtils.isEmpty(idStage) && !ObjectUtils.isEmpty(idCase)) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class)
					.add(Restrictions.eq("stage.idStage", idStage)).add(Restrictions.eq("capsCase.idCase", idCase))
					.add(Restrictions.eq("cdTodoTask", ServiceConstants.INTAKE_TASK_CODE))
					.setProjection(Projections.projectionList()
							.add(Projections.property("personByIdTodoPersCreator.idPerson"), "idPerson"));
			if (!CollectionUtils.isEmpty(criteria.list())) {
				List<Long> idPersonList = criteria.list();
				for (Long idPerson : idPersonList) {
					Person personFromRegion = (Person) sessionFactory.getCurrentSession().get(Person.class, idPerson);
					Todo todoEntityOnReg = createTodo(idStage, nmStage, idCase);
					if (personFromRegion != null)
						todoEntityOnReg.setPersonByIdTodoPersAssigned(personFromRegion);
					sessionFactory.getCurrentSession().saveOrUpdate(todoEntityOnReg);
					isAlertSend = true;
				}
			}
		}
		return (isAlertSend) ? ServiceConstants.CONTACT_SUCCESS : ServiceConstants.CONTACT_FAILURE;
	}

	/**
	 * Used to insert the alert in todo table
	 * 
	 * @param idStage
	 * @param stageProgram
	 * @param idCase
	 * @param idTodoEvent
	 * @param idToDoPersCreator
	 * @return
	 */
	private Todo createTodo(Long idStage, String nmStage, Long idCase) {
		String desc = null;
		String shortDesc = null;
		Todo todoEntity = new Todo();
		todoEntity.setCdTodoType(ServiceConstants.ALERT_TODO);
		todoEntity.setCdTodoTask(null);
		todoEntity.setDtTodoDue(new Date());
		todoEntity.setDtLastUpdate(new Date());
		todoEntity.setDtTodoCreated(new Date());
		todoEntity.setDtTodoCompleted(new Date());

		// in case if event also need to considered uncomment bellow code
		/*
		 * Event event = (Event)
		 * sessionFactory.getCurrentSession().get(Event.class, idTodoEvent);
		 * todoEntity.setEvent(event); Person person = (Person)
		 * sessionFactory.getCurrentSession().get(Person.class,
		 * idToDoPersCreator); todoEntity.setPersonByIdTodoPersCreator(person);
		 */

		Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, idStage);
		if (stage != null) {
			todoEntity.setStage(stage);

			desc = (ServiceConstants.INTAKE_DESC + ServiceConstants.SPACE + nmStage
					+ ServiceConstants.SPACE + ServiceConstants.REVIEWED_AND_READY_DESC);
			shortDesc = (ServiceConstants.INTAKE_DESC + ServiceConstants.SPACE + ServiceConstants.REVIEWED_AND_READY_DESC);
			todoEntity.setTxtTodoDesc(shortDesc);
			todoEntity.setTxtTodoLongDesc(desc);
		}
		CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class, idCase);
		if (capsCase != null)
			todoEntity.setCapsCase(capsCase);
		return todoEntity;
	}

	/**
	 * Method Name: getIntIntakeDate Method Description: This method is used to
	 * get Int Intake Date
	 * 
	 * @param idStage
	 * @return Date
	 */
	@Override
	public Date getIntIntakeDate(Long idStage) {
		String strQuery=getIntIntakeDate;
		if(mobileUtil.isMPSEnvironment()){
			strQuery = getIntIntakeDateForMPS; // This is currently a basic query. TODO Come up with a hirearchial query that works in SQL Anywhere DB.
		}
		return (Date) sessionFactory.getCurrentSession().createSQLQuery(strQuery)
				.addScalar("intakeDate", StandardBasicTypes.DATE).setParameter("idStage", idStage).uniqueResult();
	}

	@Override
	public SelectStageDto getLaterFSUStage(Long idStage) {

		List<SelectStageDto> selectStageDtoList = new ArrayList<SelectStageDto>();
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getLaterSageSql)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("nmCase", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(SelectStageDto.class));
		selectStageDtoList = (List<SelectStageDto>) query.list();

		if (!CollectionUtils.isEmpty(selectStageDtoList)) {
			return selectStageDtoList.get(0);
		} else {
			return null;
		}

	}
	
	/**
	 * Method Name: getIntakeStageIdForSelectedStage Method Description: Method to fetch
	 * the Intake Stage Id for the passed stage ID
	 * 
	 * @param idStage
	 * @return Long
	 */

	@Override
	public Long getIntakeStageIdForSelectedStage(Long idStage) {
		long intakeId = 0l;
		String strQuery = getIntStgIdForSelctdStgSql;
		if(mobileUtil.isMPSEnvironment()){
			strQuery = getIntStgIdForSelctdStgSqlForMPS;
		}
		BigDecimal idIntStage = (BigDecimal) sessionFactory.getCurrentSession()
				.createSQLQuery(strQuery).setParameter("idStage", idStage).uniqueResult();
		if (!ObjectUtils.isEmpty(idIntStage)) {
			intakeId = idIntStage.longValue();
		} 
		return intakeId;
	}
	
	/**
	 *Method Name:	getPriorStgWthStageType
	 *Method Description:The method returns the specific prior stage for passed current
	 *Stage.
	 *@param idStage
	 *@param cdStage
	 *@return
	 */
	@Override
	public Long getPriorStgWthStageType(Long idStage, String cdStage) {
		long priorStage = 0l;
		BigDecimal idPriorStg = (BigDecimal) sessionFactory.getCurrentSession()
				.createSQLQuery(getPriorStgWthStgTypeSql).setParameter("idStage", idStage)
				.setParameter("cdStage", cdStage).uniqueResult();
		if (!ObjectUtils.isEmpty(idPriorStg)) {
			priorStage = idPriorStg.longValue();
		} 
		return priorStage;
	}

	/**
	 * Method Name: getARStageListForCase
	 * Method Description: This method will return all the alternative response stage id for a case.
	 *
	 * @param: idCase
	 * @return:
	 */
	@Override
	public List<MergedIntakeARStageDto> getLinkedIntakeARStages(int idCase) {
		List <MergedIntakeARStageDto> mergedArIntakeDtoList = Collections.emptyList();

		if(!ObjectUtils.isEmpty(idCase)){
			Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getLinkedIntakeARStages)
					.addScalar("stageReasonClosed", StandardBasicTypes.STRING)
					.addScalar("arStageId", StandardBasicTypes.LONG)
					.addScalar("arStageCode", StandardBasicTypes.STRING)
					.addScalar("intakeStageId", StandardBasicTypes.LONG)
					.addScalar("intakeStageCode", StandardBasicTypes.STRING)
					.setParameter("idCase", idCase)
					.setResultTransformer(Transformers.aliasToBean(MergedIntakeARStageDto.class));
			mergedArIntakeDtoList = query.list();
		}
		return mergedArIntakeDtoList !=null ? mergedArIntakeDtoList: Collections.emptyList();
	}


	/* The neubus business model is as follows:
     * Files have nuid as an id, and are grouped under Records that have document_id as an id. It's a bit confusing
     * since document_id seems like it would be the key for the File object, but it's not.
	 */

    /**
     * Search for a Record in the IMPACT database. Returns null if no result is found.
     * @param idDocument
     * @param idStage
     * @param filename
     * @param comments
     * @param cdDocType
     * @param loginUserId
     */
	@Override
    public List<CgNotifFileUpload> getNeubusRecords(Long idCase) {
        ProjectionList projectionList = getRecordProjectionList();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CgNotifFileUpload.class)
            .setProjection(projectionList)
            .add(Restrictions.eq("idCase", idCase))
            .setResultTransformer(Transformers.aliasToBean(CgNotifFileUpload.class));

        List<CgNotifFileUpload> retVal = criteria.list();

        return retVal;
    }

    public Long createNeubusRecord(String idDocument, Long idCase, Long idStage, Long loginUserId) {
        CgNotifFileUpload cgNotifFileUpload = new CgNotifFileUpload();

        cgNotifFileUpload.setIdDocNum(idDocument);
        cgNotifFileUpload.setIdCase(idCase);
        cgNotifFileUpload.setIdStage(idStage);
        cgNotifFileUpload.setIdCreatedPerson(loginUserId);
        cgNotifFileUpload.setIdLastUpdatePerson(loginUserId);

        sessionFactory.getCurrentSession().save(cgNotifFileUpload);
        return cgNotifFileUpload.getIdCgNotifFileUpload();
    }

	public Long getNebusRecordById(String idDocument, Long idCase, Long idStage, Long loginUserId) {
		Long newIdCgNotifFileUpload = null;
		newIdCgNotifFileUpload = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getNebusRecordByIdSql)
				.setParameter("idCase", idCase)
				.setParameter("idStage", idStage))
				.addScalar("newIdCgNotifFileUpload", StandardBasicTypes.LONG).uniqueResult();
		return newIdCgNotifFileUpload;
	}

    @Override
    public List<CgNotifFileUploadDtl> getNeubusFileList(List<Long> recordIdList) {
        ProjectionList projectionList = getFileProjectionList();

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CgNotifFileUploadDtl.class)
                .setProjection(projectionList)
            .add(Restrictions.in("idCgNotifFileUpload", recordIdList))
			.addOrder(Order.desc("dtCreated"))
			.setResultTransformer(Transformers.aliasToBean(CgNotifFileUploadDtl.class));


        List<CgNotifFileUploadDtl> retVal = criteria.list();
        return retVal;
    }


	/**
	 * Fetch the code from CodeTable for AA
	 * @param cdDocType
	 */
	private String fetchCodeFromCodeTableforAA(String cdDocType){
		return (String)((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(sqlToGetCodeFromCodesTable).setParameter("codeType", CodesConstant.CCDOCTYP)
				.setParameter("cdDocType", cdDocType)).uniqueResult();
	}

	@Override
    public Long createNeubusFile(String newFileNuid, Long idCgNotifFileUpload, String filename, String cdDocType,
                                 String cdReqStatus, String comments, Long placementId, Long loginUserId) {



		CgNotifFileUploadDtl cgNotifFileUploadDtl = new CgNotifFileUploadDtl();

        cgNotifFileUploadDtl.setIdNuidNum(newFileNuid);
        cgNotifFileUploadDtl.setIdCgNotifFileUpload(idCgNotifFileUpload);
        cgNotifFileUploadDtl.setTxtUploadFileNm(filename);
        cgNotifFileUploadDtl.setCdDocType(fetchCodeFromCodeTableforAA(cdDocType));
        cgNotifFileUploadDtl.setCdRequestStatus(cdReqStatus);
        cgNotifFileUploadDtl.setTxtComments(comments);
        cgNotifFileUploadDtl.setIdCreatedPerson(loginUserId);
        cgNotifFileUploadDtl.setIdLastUpdatePerson(loginUserId);
        cgNotifFileUploadDtl.setIdPlacementEvent(placementId);

        sessionFactory.getCurrentSession().save(cgNotifFileUploadDtl);
        return cgNotifFileUploadDtl.getIdCgNotifFileUploadDtl();
    }

	@Override
    public void updateNeubusFile(Long idCgNotifFileUploadDtl, String status, Long loginUserId) {
	    if (status != null) {
            ProjectionList projectionList = getFileProjectionList();

            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CgNotifFileUploadDtl.class)
                .setProjection(projectionList)
                    .add(Restrictions.eq("idCgNotifFileUploadDtl", idCgNotifFileUploadDtl))
                    .setResultTransformer(Transformers.aliasToBean(CgNotifFileUploadDtl.class));

            CgNotifFileUploadDtl cgNotifFileUpload = (CgNotifFileUploadDtl)criteria.uniqueResult();

            if (cgNotifFileUpload != null) {
                cgNotifFileUpload.setCdRequestStatus(status);
                if (CodesConstant.CCACKREQ_SE.equals(status)) {
                    cgNotifFileUpload.setDtAckSent(new Date());
                }

                cgNotifFileUpload.setIdLastUpdatePerson(loginUserId);

                sessionFactory.getCurrentSession().saveOrUpdate(cgNotifFileUpload);
            }
    }
    }

    @Override
    public NotificationFileDto getNeubusIdentifiers(Long idCgNotifFileUpload, Long idCgNotifFileUploadDtl) {
        NotificationFileDto rawResult = null;
        Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getNeubusIdentifiersSql)
            .addScalar("idDocNum", StandardBasicTypes.STRING).addScalar("idNuidNum", StandardBasicTypes.STRING)
            .addScalar("txtUploadFileNm", StandardBasicTypes.STRING)
            .setParameter("idCgNotifFileUpload", idCgNotifFileUpload)
            .setParameter("idCgNotifFileUploadDtl", idCgNotifFileUploadDtl)
            .setResultTransformer(Transformers.aliasToBean(NotificationFileDto.class));

        rawResult = (NotificationFileDto) query.uniqueResult();

        return rawResult;
    }

	private ProjectionList getRecordProjectionList() {
        if (recordProjectionCache == null) {
            recordProjectionCache = Projections.projectionList();
            recordProjectionCache.add(Projections.property("idCgNotifFileUpload"), "idCgNotifFileUpload")
                .add(Projections.property("idCase"), "idCase")
                .add(Projections.property("idStage"), "idStage")
                .add(Projections.property("idDocNum"), "idDocNum")
                .add(Projections.property("dtCreated"), "dtCreated")
                .add(Projections.property("dtLastUpdate"), "dtLastUpdate")
                .add(Projections.property("idCreatedPerson"), "idCreatedPerson")
                .add(Projections.property("idLastUpdatePerson"), "idLastUpdatePerson");
        }
        return recordProjectionCache;
    }

    private ProjectionList getFileProjectionList() {
        if (fileProjectionCache == null) {
            fileProjectionCache = Projections.projectionList();
            fileProjectionCache.add(Projections.property("idCgNotifFileUploadDtl"), "idCgNotifFileUploadDtl")
                .add(Projections.property("idCgNotifFileUpload"), "idCgNotifFileUpload")
                .add(Projections.property("idNuidNum"), "idNuidNum")
                .add(Projections.property("txtUploadFileNm"), "txtUploadFileNm")
                .add(Projections.property("cdDocType"), "cdDocType")
                .add(Projections.property("cdRequestStatus"), "cdRequestStatus")
                .add(Projections.property("dtAckSent"), "dtAckSent")
                .add(Projections.property("dtAckRcvd"), "dtAckRcvd")
                .add(Projections.property("txtComments"), "txtComments")
                .add(Projections.property("dtCreated"), "dtCreated")
                .add(Projections.property("dtLastUpdate"), "dtLastUpdate")
                .add(Projections.property("idCreatedPerson"), "idCreatedPerson")
                .add(Projections.property("idLastUpdatePerson"), "idLastUpdatePerson")
                .add(Projections.property("idPlacementEvent"), "idPlacementEvent");
        }
        return fileProjectionCache;
    }

    @Override
    public List<ProviderPlacementDto> fetchCurrentPlacementInformation(Long idCase) {
		ProviderPlacementDto retVal = null;
        Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(fetchCurrentPlacementInformation)
            .addScalar("childName", StandardBasicTypes.STRING).addScalar("resourceName", StandardBasicTypes.STRING)
			.addScalar("startDate", StandardBasicTypes.DATE).addScalar("address", StandardBasicTypes.STRING)
			.addScalar("city", StandardBasicTypes.STRING).addScalar("stageId", StandardBasicTypes.LONG)
			.addScalar("placementId", StandardBasicTypes.LONG).addScalar("facilityType", StandardBasicTypes.STRING)
			.addScalar("childSpecific", StandardBasicTypes.STRING)
            .setParameter("idCase", idCase)
            .setResultTransformer(Transformers.aliasToBean(ProviderPlacementDto.class));
        List<ProviderPlacementDto> selectStageDtoList = new ArrayList<>();
        selectStageDtoList =(List<ProviderPlacementDto>) query.list();

        return selectStageDtoList;
    }

    /**
     * Collects information about the child, their placement, and the document to be acknowledged to send to the
     * provider portal via Neubus. Having the DAO return the actual JSON request object is unconventional but
     * efficient.
     * @param idCgNotifFileUploadDtl
     * @return
     */
    @Override
    public CaregiverAckReq findAckRequestParameters(Long idCgNotifFileUploadDtl) {

        CaregiverAckReq retVal = null;
        Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(findAckRequestParameters)
            .addScalar("fullName", StandardBasicTypes.STRING).addScalar("lastName", StandardBasicTypes.STRING)
            .addScalar("firstName", StandardBasicTypes.STRING).addScalar("pid", StandardBasicTypes.STRING)
            .addScalar("providerIdentifier", StandardBasicTypes.STRING)
            .addScalar("documentId", StandardBasicTypes.STRING).addScalar("documentName", StandardBasicTypes.STRING)
            .addScalar("nuid", StandardBasicTypes.STRING).addScalar("documentVersion", StandardBasicTypes.STRING)
            .addScalar("documentType", StandardBasicTypes.STRING).addScalar("impactPlacementDate", StandardBasicTypes.STRING)
            .addScalar("placementEventId", StandardBasicTypes.STRING)
            .setParameter("idCgNotifFileUploadDtl", idCgNotifFileUploadDtl)
            .setResultTransformer(Transformers.aliasToBean(CaregiverAckReq.class));
        List<CaregiverAckReq> selectStageDtoList = query.list();

        if (!CollectionUtils.isEmpty(selectStageDtoList)) {
            retVal = selectStageDtoList.get(0);
			retVal.setVendorIdentificationNumber(findVendorId(retVal.getProviderIdentifier()));
        }
        return retVal;
    }

	private String findVendorId(String idResource) {
		String vendorId = (String) sessionFactory.getCurrentSession()
				.createSQLQuery(findVendorId).setParameter("idResource", idResource)
				.uniqueResult();
		return vendorId;
	}
}
