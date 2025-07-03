package us.tx.state.dfps.service.casepackage.daoimpl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import static us.tx.state.dfps.service.common.CodesConstant.CATOFSVC_24;
import static us.tx.state.dfps.service.common.CodesConstant.CINVUTYP_DA2;
import static us.tx.state.dfps.service.common.CodesConstant.CNPERIOD_DAY;
import static us.tx.state.dfps.service.common.CodesConstant.CSVATYPE_INI;
import static us.tx.state.dfps.service.common.ServiceConstants.CHAR_IND_Y;
import static us.tx.state.dfps.service.common.ServiceConstants.CSVATYPE_TRM;
import static us.tx.state.dfps.service.common.ServiceConstants.CSVCCODE_68O;
import static us.tx.state.dfps.service.common.ServiceConstants.CSVCCODE_68P;
import static us.tx.state.dfps.service.common.ServiceConstants.KINSHIP_AUTOMATED_SYSTEM_ID;
import static us.tx.state.dfps.service.common.ServiceConstants.STRING_IND_N;
import static us.tx.state.dfps.service.common.ServiceConstants.STRING_IND_Y;

import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.Contract;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.EventPersonLink;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.ServiceAuthorization;
import us.tx.state.dfps.common.domain.SvcAuthDetail;
import us.tx.state.dfps.common.domain.SvcAuthEventLink;
import us.tx.state.dfps.phoneticsearch.IIRHelper.DateHelper;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkInDto;
import us.tx.state.dfps.service.casepackage.dao.ServiceAuthorizationDao;
import us.tx.state.dfps.service.casepackage.dto.ServAuthRetrieveDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ServAuthRetrieveReq;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.kin.dto.KinChildDto;
import us.tx.state.dfps.service.kin.dto.KinHomeInfoDto;
import us.tx.state.dfps.service.kin.dto.KinMonthlyExtPaymentDto;
import us.tx.state.dfps.service.person.dto.EventPersonDto;
import us.tx.state.dfps.service.person.dto.ServiceAuthDto;
import us.tx.state.dfps.service.workload.dto.SVCAuthDetailDto;
import us.tx.state.dfps.service.workload.dto.SVCAuthDetailRecDto;
import us.tx.state.dfps.service.workload.dto.ServiceAuthorizationDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.ServiceAuthDetailDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCON24S Class
 * Description: This Method extends BaseDao and implements ServAuthRetrieveDao.
 * This is used to retrieve ServAuthRetrieve details from database. Apr 03, 2017
 * - 4:50:30 PM
 */
@Repository
public class ServiceAuthorizationDaoImpl implements ServiceAuthorizationDao {

	@Value("${ServAuthRetrieveDaoImpl.getServAuthRetrieveList}")
	private String ServAuthRetrievesql;

	@Value("${ServAuthRetrieveDaoImpl.getServiceAuthorizationById}")
	private String getServiceAuthorizationByIdSql;

	@Value("${ServAuthRetrieveDaoImpl.getServiceAuthorizationEntityById}")
	private String getServiceAuthorizationEntityByIdSql;

	@Value("${ServAuthRetrieveDaoImpl.getSVCAuthDetailDtoById}")
	private String getSVCAuthDetailDtoByIdSql;

	@Value("${ServAuthRetrieveDaoImpl.getSVCAuthListByPerson}")
	private String getSVCAuthListByPerson;

	@Value("${ServAuthRetrieveDaoImpl.getOverlappingSvcAuthDtl}")
	private String getOverlappingSvcAuthDtl;

	@Value("${ServAuthRetrieveDaoImpl.getSvcAuthDtTerm}")
	private String getSvcAuthDtTerm;

	@Value("${ServAuthRetrieveDaoImpl.getSVCAuthDetailRecordSql}")
	private String getSVCAuthDetailRecordSql;

	@Value("${ServAuthRetrieveDaoImpl.updateServiceAuthDetails}")
	private String updateServiceAuthDetailsSql;

	@Value("${ServAuthRetrieveDaoImpl.getSadStartDateInFuture}")
	private String getSadStartDateInFutureSql;

	@Value("${ServAuthRetrieveDaoImpl.getOtherServiceAuthStartDate}")
	private String getOtherServiceAuthStartDateSql;

	@Value("${ServAuthRetrieveDaoImpl.updateServiceAuthTermDetail}")
	private String updateServiceAuthTermDetailSql;

	@Value("${ServAuthRetrieveDaoImpl.getServiceAuthUnitsUsed}")
	private String getServiceAuthUnitsUsedSql;

	@Value("${ServAuthRetrieveDaoImpl.getServiceAuthUnitsUsedForOpen}")
	private String getServiceAuthUnitsUsedForOpenSql;

	@Value("${ServAuthRetrieveDaoImpl.getServiceAuthUnitsRequested}")
	private String getServiceAuthUnitsRequestedSql;

	@Value("${ServAuthRetrieveDaoImpl.getServiceAuthUnitsRequestedTermed}")
	private String getServiceAuthUnitsRequestedTermedSql;

	@Value("${ServAuthRetrieveDaoImpl.getServiceAuthDtlOpenExists}")
	private String getServiceAuthDtlOpenExistsSql;

	@Value("${ServAuthRetrieveDaoImpl.getServiceAuthDtlClosedExists}")
	private String getServiceAuthDtlClosedExistsSql;

	@Value("${ServAuthRetrieveDaoImpl.getIs68OPaidFull}")
	private String getIs68OPaidFullSql;

	@Value("${ServAuthRetrieveDaoImpl.getSADPendingTermDate}")
	private String getSADPendingTermDateSql;

	@Value("${ServAuthRetrieveDaoImpl.getSADCurrentMonthTermDate}")
	private String getSADCurrentMonthTermDateSql;

	@Value("${ServAuthRetrieveDaoImpl.getSAHeaderIdSet}")
	private String getSAHeaderIdSetSql;

	@Value("${ServAuthRetrieveDaoImpl.insertServcAuthEventLink}")
	private String insertServcAuthEventLinkSql;

	@Value("${ServAuthRetrieveDaoImpl.getServiceAuthId}")
	private String getServiceAuthIdSql;

	@Value("${ServAuthRetrieveDaoImpl.getServiceAuthList}")
	private String getServiceAuthListSql;

	@Value("${ServiceAuthorizationDaoImpl.insertMonthlyExtensionSADetail}")
	private String insertMonthlyExtensionSADetailSql;

	@Value("${ServAuthRetrieveDaoImpl.getSvcAuthEventInfo}")
	private String getSvcAuthEventInfoSql;


	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(ServiceAuthorizationDaoImpl.class);

	public ServiceAuthorizationDaoImpl() {

	}

	/**
	 * 
	 * Method Description: This Method to retrieve the list of rows from the
	 * SERVICE_AUTHORIZATION_TABLE based on inputs Dam Name: CSES23D, CLSSS25D,
	 * CLSC14D, CSEC13D
	 * 
	 * @param servAuthRetrieveReq
	 * @return List<ServAuthRetrieveDto>
	 * @,DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public List<ServAuthRetrieveDto> getAuthDetails(ServAuthRetrieveReq servAuthRetrieveReq) {
		List<ServAuthRetrieveDto> servAuthRetrieveDtoList = new ArrayList<ServAuthRetrieveDto>();

		servAuthRetrieveDtoList = (List<ServAuthRetrieveDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(ServAuthRetrievesql).setParameter("stageID", servAuthRetrieveReq.getUlIdStage())
				.setParameter("authID", servAuthRetrieveReq.getUlIdSvcAuth()))
						.addScalar("idSvcAuth", StandardBasicTypes.LONG)
						.addScalar("tsLastUpdate", StandardBasicTypes.STRING).addScalar("cdSvcAuthCounty")
						.addScalar("idResource", StandardBasicTypes.LONG)
						.addScalar("idContract", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("idPrimaryClient", StandardBasicTypes.LONG).addScalar("cdSvcAuthAbilToRespond")
						.addScalar("cdSvcAuthCategory").addScalar("cdSvcAuthRegion").addScalar("cdSvcAuthService")
						.addScalar("dtSvcAuthVerbalReferl").addScalar("indSvcAuthComplete", StandardBasicTypes.STRING)
						.addScalar("svcAuthComments").addScalar("directToHome").addScalar("homeEnviron")
						.addScalar("medicalConditions").addScalar("svcAuthSecProvdr").addScalar("dtSvcAuthEff")
						.addScalar("indDntdCmmtySvc", StandardBasicTypes.STRING)
						.addScalar("amtEstValue", StandardBasicTypes.LONG)
						.addScalar("idApsInhomeSvcAuth", StandardBasicTypes.LONG).addScalar("cdAPSInHomeTask")
						.addScalar("cdRsrcService").addScalar("idStagePerson", StandardBasicTypes.LONG)
						.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdStagePersRole")
						.addScalar("indStagePersInLaw").addScalar("cdStagePersType").addScalar("indCdStagePersSearch")
						.addScalar("stagePersNotes").addScalar("dtStagePersLink").addScalar("cdStagePersRelInt")
						.addScalar("indStagePersReporter").addScalar("indStagePersEmpNew").addScalar("personSex")
						.addScalar("nmPersonFull").addScalar("personAge", StandardBasicTypes.LONG)
						.addScalar("dtPersonDeath").addScalar("dtPersonBirth").addScalar("cdPersonReligion")
						.addScalar("cdPersonChar").addScalar("indPersonDobApprox").addScalar("cdPersonLivArr")
						.addScalar("cdPersGuardCnsrv").addScalar("cdPersonStatus").addScalar("cdPersonDeath")
						.addScalar("cdPersonMaritalStatus").addScalar("occupation").addScalar("cdPersonLanguage")
						.addScalar("cdPersonEthnicGroup").addScalar("indPersCancelHist")
						.setResultTransformer(Transformers.aliasToBean(ServAuthRetrieveDto.class)).list();

		if (servAuthRetrieveDtoList.size() == ServiceConstants.ZERO_VAL) {

			throw new DataNotFoundException(messageSource.getMessage("common.data.emptyset", null, Locale.US));

		}

		// Throw Exception if Data is not found

		log.info("TransactionId :" + servAuthRetrieveReq.getTransactionId());
		return servAuthRetrieveDtoList;
	}

	/**
	 * This DAM performs a full row retrieval from the
	 * SERVICE_AUTHORIZATION_TABLE when IDSVC_AUth is equal to the input
	 * variable.
	 * 
	 * Service Name : CCMN03U, DAM Name : CSES23D
	 * 
	 * @param idSvcAuth
	 * @return @
	 */
	@Override
	public ServiceAuthorizationDto getServiceAuthorizationById(Long idSvcAuth) {
		ServiceAuthorizationDto serviceAuthorizationDto = new ServiceAuthorizationDto();
		Query queryEvent = (Query) sessionFactory.getCurrentSession().createSQLQuery(getServiceAuthorizationByIdSql)
				.addScalar("idSvcAuth", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdSvcAuthCounty", StandardBasicTypes.STRING)
				.addScalar("idResource", StandardBasicTypes.LONG).addScalar("idContract", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idPrimaryClient", StandardBasicTypes.LONG)
				.addScalar("cdSvcAuthAbilToRespond", StandardBasicTypes.STRING)
				.addScalar("cdSvcAuthCategory", StandardBasicTypes.STRING)
				.addScalar("cdSvcAuthRegion", StandardBasicTypes.STRING)
				.addScalar("cdSvcAuthService", StandardBasicTypes.STRING)
				.addScalar("dtSvcAuthVerbalReferl", StandardBasicTypes.DATE)
				.addScalar("indSvcAuthComplete", StandardBasicTypes.STRING)
				.addScalar("svcAuthComments", StandardBasicTypes.STRING)
				.addScalar("svcAuthDirToHome", StandardBasicTypes.STRING)
				.addScalar("svcAuthHomeEnviron", StandardBasicTypes.STRING)
				.addScalar("svcAuthMedCond", StandardBasicTypes.STRING)
				.addScalar("svcAuthSecProvdr", StandardBasicTypes.STRING)
				.addScalar("dtSvcAuthEff", StandardBasicTypes.TIMESTAMP)
				.addScalar("indDontdComntySvc", StandardBasicTypes.STRING)
				.addScalar("amtEstValue", StandardBasicTypes.BIG_DECIMAL)
				.setResultTransformer(Transformers.aliasToBean(ServiceAuthorizationDto.class));
		queryEvent.setParameter("idSvcAuth", idSvcAuth);
		serviceAuthorizationDto = (ServiceAuthorizationDto) queryEvent.uniqueResult();
		return serviceAuthorizationDto;
	}

	/**
	 * This DAM selects a full row from the svc_auth_detail with id_svc_auth as
	 * input.
	 * 
	 * Service Name : CCMN03U, DAM Name : CLSS24D
	 * 
	 * @param idSvcAuth
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SVCAuthDetailDto> getSVCAuthDetailDtoById(Long idSvcAuth) {
		List<SVCAuthDetailDto> svcAuthDetailDtoList = new ArrayList<>();
		Query queryEvent = (Query) sessionFactory.getCurrentSession().createSQLQuery(getSVCAuthDetailDtoByIdSql)
				.addScalar("idSvcAuthDtl", StandardBasicTypes.LONG).addScalar("idSvcAuth", StandardBasicTypes.LONG)
				.addScalar("idName", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdSvcAuthDtlAuthType", StandardBasicTypes.STRING)
				.addScalar("cdSvcAuthDtlPeriod", StandardBasicTypes.STRING)
				.addScalar("cdSvcAuthDtlUnitType", StandardBasicTypes.STRING)
				.addScalar("cdSvcAuthDtlSvc", StandardBasicTypes.STRING)
				.addScalar("dtSvcAuthDtl", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtSvcAuthDtlBegin", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtSvcAuthDtlEnd", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtSvcAuthDtlTerm", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtSvcAuthDtlShow", StandardBasicTypes.TIMESTAMP)
				.addScalar("amtSvcAuthDtlAmtReq", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("amtSvcAuthDtlAmtUsed", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("svcAuthDtlFreq", StandardBasicTypes.SHORT)
				.addScalar("svcAuthDtlLineItm", StandardBasicTypes.SHORT)
				.addScalar("svcAuthDtlSugUnit", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("svcAuthDtlUnitsReq", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("svcAuthDtlUnitRate", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("svcAuthDtlUnitUsed", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("indSvcAuthComplete", StandardBasicTypes.STRING)
				.addScalar("nmNameFirst", StandardBasicTypes.STRING)
				.addScalar("nmNameMiddle", StandardBasicTypes.STRING).addScalar("nmNameLast", StandardBasicTypes.STRING)
				.addScalar("cdNameSuffix", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(SVCAuthDetailDto.class));
		queryEvent.setParameter("idSvcAuth", idSvcAuth);
		svcAuthDetailDtoList = queryEvent.list();
		return svcAuthDetailDtoList;
	}

	/**
	 * The DAM will insert a new SVC_AUTH_ID for a particular event.
	 * 
	 * Service Name : CCMN03U, DAM Name : CAUD34D
	 * 
	 * @param svcAuthEventLink
	 * @
	 */
	@Override
	public void svcAuthEventLinkSave(SvcAuthEventLink svcAuthEventLink) {
		sessionFactory.getCurrentSession().save(svcAuthEventLink);
		sessionFactory.getCurrentSession().flush();
	}

	/**
	 * This DAM performs a full row retrieval from the
	 * SERVICE_AUTHORIZATION_TABLE when IDSVC_AUth is equal to the input
	 * variable.
	 * 
	 * Service Name : CCMN03U, DAM Name : CSES23D
	 * 
	 * @param idSvcAuth
	 * @return @
	 */
	@Override
	public ServiceAuthorization getServiceAuthorizationEntityById(Long idSvcAuth) {
		ServiceAuthorization serviceAuthorization = new ServiceAuthorization();
		Query queryEvent = (Query) sessionFactory.getCurrentSession().createQuery(getServiceAuthorizationEntityByIdSql);
		queryEvent.setParameter("idSvcAuth", idSvcAuth);
		serviceAuthorization = (ServiceAuthorization) queryEvent.uniqueResult();
		return serviceAuthorization;
	}

	/**
	 * 
	 * Method Name: getServiceAuthDtlListForPerson Method Description:
	 * 
	 * @param personId
	 * @return @
	 */
	@Override
	public ArrayList<SVCAuthDetailDto> getServiceAuthDtlListForPerson(Long personId) {
		List<SVCAuthDetailDto> svcAuthDetailDtoList = new ArrayList<>();
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getSVCAuthListByPerson)
				.addScalar("idSvcAuthDtl", StandardBasicTypes.LONG).addScalar("idSvcAuth", StandardBasicTypes.LONG)
				.addScalar("idName", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdSvcAuthDtlAuthType", StandardBasicTypes.STRING)
				.addScalar("cdSvcAuthDtlPeriod", StandardBasicTypes.STRING)
				.addScalar("cdSvcAuthDtlUnitType", StandardBasicTypes.STRING)
				.addScalar("cdSvcAuthDtlSvc", StandardBasicTypes.STRING)
				.addScalar("dtSvcAuthDtl", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtSvcAuthDtlBegin", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtSvcAuthDtlEnd", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtSvcAuthDtlTerm", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtSvcAuthDtlShow", StandardBasicTypes.TIMESTAMP)
				.addScalar("amtSvcAuthDtlAmtReq", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("amtSvcAuthDtlAmtUsed", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("svcAuthDtlFreq", StandardBasicTypes.SHORT)
				.addScalar("svcAuthDtlLineItm", StandardBasicTypes.SHORT)
				.addScalar("svcAuthDtlSugUnit", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("svcAuthDtlUnitsReq", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("svcAuthDtlUnitRate", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("svcAuthDtlUnitUsed", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("indSvcAuthComplete", StandardBasicTypes.STRING)
				.addScalar("idResource", StandardBasicTypes.LONG).setParameter("idPerson", personId)
				.setResultTransformer(Transformers.aliasToBean(SVCAuthDetailDto.class));
		svcAuthDetailDtoList = query.list();
		return (ArrayList<SVCAuthDetailDto>) svcAuthDetailDtoList;
	}

	@Override
	public ArrayList<ServiceAuthDto> getOverlappingSvcAuthDtlListForPerson(SVCAuthDetailDto svcAuthBean,
			Long personId) {
		ArrayList<ServiceAuthDto> svcAuthDetailDtoList = new ArrayList<ServiceAuthDto>();
		SimpleDateFormat sqlDateFormat = new SimpleDateFormat(ServiceConstants.SQL_DATE_FORMAT);
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getOverlappingSvcAuthDtl)
				.addScalar("idServiceAuth", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("indSensitiveCase", StandardBasicTypes.STRING)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.addScalar("dtEventCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("eventDescr", StandardBasicTypes.STRING)
				.addScalar("dtStageStart", StandardBasicTypes.TIMESTAMP).addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING).setParameter(0, svcAuthBean.getIdSvcAuthDtl())
				.setParameter(1, svcAuthBean.getIdResource()).setParameter(2, svcAuthBean.getCdSvcAuthDtlSvc())
				.setParameter(3, personId).setParameter(4, sqlDateFormat.format(svcAuthBean.getDtSvcAuthDtlBegin()))
				.setParameter(5, sqlDateFormat.format(svcAuthBean.getDtSvcAuthDtlTerm()))
				.setParameter(6, sqlDateFormat.format(svcAuthBean.getDtSvcAuthDtlBegin()))
				.setParameter(7, sqlDateFormat.format(svcAuthBean.getDtSvcAuthDtlTerm()))
				.setParameter(8, sqlDateFormat.format(svcAuthBean.getDtSvcAuthDtlBegin()))
				.setParameter(9, sqlDateFormat.format(svcAuthBean.getDtSvcAuthDtlBegin()))
				.setParameter(10, sqlDateFormat.format(svcAuthBean.getDtSvcAuthDtlBegin()))
				.setParameter(11, sqlDateFormat.format(svcAuthBean.getDtSvcAuthDtlTerm()))
				.setResultTransformer(Transformers.aliasToBean(ServiceAuthDto.class));
		svcAuthDetailDtoList = (ArrayList<ServiceAuthDto>) query.list();
		return svcAuthDetailDtoList;
	}

	/**
	 ** Description: This dam will retrieve Dt Svc Auth Dt Term from the Svc Auth
	 * Dtl Event Link table. DAM Name : CLSC60D This would return the highest
	 * date if multiple dates are found.
	 * 
	 * @param idCase
	 * @
	 */

	public List<Date> getSvcAuthDtTerm(Long idCase) {
		@SuppressWarnings("unchecked")
		List<Date> dtSvcAuthTermList = (ArrayList<Date>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getSvcAuthDtTerm).setParameter("idCase", idCase))
						.addScalar("dtSvcAuthDtlTerm", StandardBasicTypes.DATE).list();
		return dtSvcAuthTermList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SVCAuthDetailRecDto> getSVCAuthDetailRecord(Long idSvcAuth) {
		List<SVCAuthDetailRecDto> svcAuthDetailRecDto = new ArrayList<>();
		Query queryEvent = (Query) sessionFactory.getCurrentSession().createSQLQuery(getSVCAuthDetailRecordSql)
				.addScalar("idSvcAuthDtl", StandardBasicTypes.LONG).addScalar("idSvcAuth", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("cdSvcAuthDtlAuthType", StandardBasicTypes.STRING)
				.addScalar("cdSvcAuthDtlPeriod", StandardBasicTypes.STRING)
				.addScalar("cdSvcAuthDtlSvc", StandardBasicTypes.STRING)
				.addScalar("cdSvcAuthDtlUnitType", StandardBasicTypes.STRING)
				.addScalar("dtSvcAuthDtl", StandardBasicTypes.DATE)
				.addScalar("dtSvcAuthDtlBegin", StandardBasicTypes.DATE)
				.addScalar("dtSvcAuthDtlEnd", StandardBasicTypes.DATE)
				.addScalar("dtSvcAuthDtlTerm", StandardBasicTypes.DATE)
				.addScalar("dtSvcAuthDtlShow", StandardBasicTypes.DATE)
				.addScalar("amtSvcAuthDtlAmtReq", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("amtSvcAuthDtlAmtUsed", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("nbrSvcAuthDtlFreq", StandardBasicTypes.SHORT)
				.addScalar("nbrSvcAuthDtlLineItm", StandardBasicTypes.SHORT)
				.addScalar("nbrSvcAuthDtlSugUnit", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("nbrSvcAuthDtlUnitsReq", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("nbrSvcAuthDtlUnitRate", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("nbrSvcAuthDtlUnitUsed", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("cdPersonSex", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("nbrPersonAge", StandardBasicTypes.SHORT).addScalar("dtPersonDeath", StandardBasicTypes.DATE)
				.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("cdPersonReligion", StandardBasicTypes.STRING)
				.addScalar("cdPersonChar", StandardBasicTypes.STRING)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("cdPersonLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPersGuardCnsrv", StandardBasicTypes.STRING)
				.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
				.addScalar("cdPersonDeath", StandardBasicTypes.STRING)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("txtPersonOccupation", StandardBasicTypes.STRING)
				.addScalar("cdPersonLanguage", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("indPersCancelHist", StandardBasicTypes.STRING)
				.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING)
				.addScalar("idStagePersonLink", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("indStagePersInLaw", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersSearchInd", StandardBasicTypes.STRING)
				.addScalar("txtStagePersNotes", StandardBasicTypes.STRING)
				.addScalar("dtStagePersLink", StandardBasicTypes.DATE)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("indStagePersReporter", StandardBasicTypes.STRING)
				.addScalar("indStagePersEmpNew", StandardBasicTypes.STRING)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("cdPersonSuffix", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(SVCAuthDetailRecDto.class));
		queryEvent.setParameter("idSvcAuth", idSvcAuth);
		svcAuthDetailRecDto = queryEvent.list();
		return svcAuthDetailRecDto;
	}

	/**
	 * 
	 * Method Name: insertIntoServiceAuthorizationEventLinks Method Description:
	 * This method batch inserts/updates records into SVC_AUTH_EVENT_LINK table
	 * 
	 * @param serviceAuthEventLinkValueBeans
	 * @return
	 * @throws DataNotFoundException
	 */
	@Override
	public List<Long> insertIntoServiceAuthorizationEventLinks(
			List<SvcAuthEventLinkInDto> serviceAuthEventLinkValueBeans) throws DataNotFoundException {
		List<Long> primaryKeyList = new ArrayList<>();

		if (TypeConvUtil.isNullOrEmpty(serviceAuthEventLinkValueBeans))
			return primaryKeyList;
		for (SvcAuthEventLinkInDto serviceAuthEventLinkDto : serviceAuthEventLinkValueBeans) {
			SvcAuthEventLink authEventLink = new SvcAuthEventLink();

			authEventLink.setIdSvcAuthEvent(serviceAuthEventLinkDto.getSvcAuthEventId());
			authEventLink.setIdCase(serviceAuthEventLinkDto.getCaseId());
			authEventLink.setDtLastUpdate(Calendar.getInstance().getTime());

			ServiceAuthorization serviceAuthorization = new ServiceAuthorization();
			serviceAuthorization.setIdSvcAuth(serviceAuthEventLinkDto.getSvcAuthId());
			authEventLink.setServiceAuthorization(serviceAuthorization);

			primaryKeyList.add((Long) sessionFactory.getCurrentSession().save(authEventLink));
		}
		if (ObjectUtils.isEmpty(primaryKeyList))
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));

		return primaryKeyList;
	}

	/**
	 * 
	 * Method Name: insertIntoEventPersonLinks Method Description: This method
	 * batch inserts into Event_person_link table using EventPersonValueBean
	 * list
	 * 
	 * @param spLinkBeans
	 * @return List<Long>
	 * @throws DataNotFoundException
	 */
	@Override
	public List<Long> insertIntoEventPersonLinks(List<EventPersonDto> spLinkBeans) throws DataNotFoundException {
		List<Long> resultList = new ArrayList<>();
		if ((TypeConvUtil.isNullOrEmpty(spLinkBeans))) {
			return resultList;
		}

		for (EventPersonDto eventPersonDto : spLinkBeans) {
			EventPersonLink eventPersonLink = new EventPersonLink();

			Person person = new Person();
			person.setIdPerson(eventPersonDto.getIdpersonId());
			eventPersonLink.setPerson(person);

			Event event = new Event();
			event.setIdEvent(eventPersonDto.getIdEvent());
			eventPersonLink.setEvent(event);

			eventPersonLink.setIdCase(eventPersonDto.getIdCase());
			eventPersonLink.setDtLastUpdate(Calendar.getInstance().getTime());

			Long count = (Long) sessionFactory.getCurrentSession().save(eventPersonLink);
			resultList.add(count);
		}

		if (TypeConvUtil.isNullOrEmpty(resultList)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		return resultList;
	}
	
	/**
	 * 
	 *Method Name:	checkSvcAuthEventLinkExists
	 *Method Description: This method checks if the SvcAuthEventLink exists for an event
	 *@param idSvcAuthEvent
	 *@return
	 */
	@Override
	public boolean checkSvcAuthEventLinkExists(Long idSvcAuthEvent) {
		SvcAuthEventLink svcAuthEventLink = (SvcAuthEventLink) sessionFactory.getCurrentSession()
				.get(SvcAuthEventLink.class, idSvcAuthEvent);
		if (ObjectUtils.isEmpty(svcAuthEventLink)) {
			return false;
		}
		return true;
	}

    @Override
    public int updateServiceAuthDetails(KinHomeInfoDto homeInfoBean, Long contractId, String serviceCode, KinChildDto kinChildDto) {

		Date sadStartDate = isSvcAuthStartingInfuture(kinChildDto.getChildId(), serviceCode,
				homeInfoBean.getIdHomeResource(), contractId);

		if (ObjectUtils.isEmpty(sadStartDate)) {
			if (!ObjectUtils.isEmpty(kinChildDto.getPaymentEligibilityEndDate())) {
				//Defect13671 start
				sadStartDate = DateHelper.addToDate(kinChildDto.getPaymentEligibilityEndDate(), 0,0,-1);
				//Defect13671 - end
			} else {
				sadStartDate = new Date();
			}
		}

		Query queryUpdate = sessionFactory.getCurrentSession().createSQLQuery(updateServiceAuthDetailsSql);
		queryUpdate.setParameter("authDetailSvcTermDate", sadStartDate);
		queryUpdate.setParameter("authDetailSvcType", CSVATYPE_TRM);
		queryUpdate.setParameter("lastUpdatePersonId", KINSHIP_AUTOMATED_SYSTEM_ID);
		queryUpdate.setDate("lastUpdateDate", new Date());
		queryUpdate.setParameter("termRecordPersonId", KINSHIP_AUTOMATED_SYSTEM_ID);
		queryUpdate.setDate("termRecordedDate", new Date());
		queryUpdate.setParameter("personId", kinChildDto.getChildId());
		queryUpdate.setParameter("authDetailSvcCode", serviceCode);
		queryUpdate.setParameter("resourceId", homeInfoBean.getIdHomeResource());
		queryUpdate.setParameter("contractId", contractId);
		return queryUpdate.executeUpdate();
	}

	private Date isSvcAuthStartingInfuture(Long childId, String serviceCode,
										   Long resourceId, Long contractId) {
		Query query = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(getSadStartDateInFutureSql)
				.setParameter("personId", childId)
				.setParameter("authDetailSvcCode", serviceCode)
				.setParameter("resourceId", resourceId)
				.setParameter("contractId", contractId));
		Date sadStartDate = ((Date) query.uniqueResult());
		return sadStartDate;
	}

	@Override
	public int termOtherServiceAuthDetails(KinHomeInfoDto homeInfoBean, KinChildDto kinChildDto) {
		Date startDate = null;
		Date termDate = null;
		Date startDateToPass = null;
		Long idSvcAuthDtl;
		int updateRecordCount = 0;

		Query query = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(getOtherServiceAuthStartDateSql)
				.addScalar("idSvcAuthDtl", StandardBasicTypes.LONG)
				.addScalar("dtSvcAuthDtlBegin", StandardBasicTypes.TIMESTAMP)
				.setParameter("resourceId", homeInfoBean.getIdHomeResource())
				.setParameter("personId", kinChildDto.getChildId())).setResultTransformer(Transformers.aliasToBean(ServiceAuthDetailDto.class));
		final List<ServiceAuthDetailDto> otherServiceAuthStartDateResults = query.list();

		if (!CollectionUtils.isEmpty(otherServiceAuthStartDateResults)) {
			idSvcAuthDtl = otherServiceAuthStartDateResults.get(0).getIdSvcAuthDtl();
			startDate = otherServiceAuthStartDateResults.get(0).getDtSvcAuthDtlBegin();

			if (startDate != null && startDate.after(new Date())) {
				startDateToPass = startDate;
			} else {
				Date d = new Date();
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(d);
				cal.add(Calendar.DATE, -1);
				termDate =  cal.getTime();
				startDateToPass = termDate;
			}

			Query queryUpdate = sessionFactory.getCurrentSession().createSQLQuery(updateServiceAuthTermDetailSql);
			queryUpdate.setDate("authDetailSvcTermDate", startDateToPass);
			queryUpdate.setParameter("termRecordedPersonID", KINSHIP_AUTOMATED_SYSTEM_ID);
			queryUpdate.setParameter("personId", KINSHIP_AUTOMATED_SYSTEM_ID);
			queryUpdate.setParameter("authDetailSvcId", idSvcAuthDtl);

			updateRecordCount =  queryUpdate.executeUpdate();
		}
		return updateRecordCount;
	}

	public int getServiceAuthUnitsUsed(Long childId,  Date legalStartDate, String serviceCode, boolean termed) {
		Query query = null;
		if (termed) {
			query = ((Query) sessionFactory.getCurrentSession().createSQLQuery(getServiceAuthUnitsUsedSql));
		} else {
			query = ((Query) sessionFactory.getCurrentSession().createSQLQuery(getServiceAuthUnitsUsedForOpenSql));
		}

		query.setParameter("childId", childId)
				.setParameter("legalStartDate", legalStartDate)
				.setParameter("serviceCode", serviceCode);
		BigDecimal serviceAuthUnitsUsed = ((BigDecimal) query.uniqueResult());
		return !ObjectUtils.isEmpty(serviceAuthUnitsUsed) ? serviceAuthUnitsUsed.intValue() : 0;
	}

	public int getServiceAuthUnitsRequested(Long childId,  Date legalStartDate, String serviceCode, boolean termed) {
		Query query = null;
		if (termed) {
			query = ((Query) sessionFactory.getCurrentSession().createSQLQuery(getServiceAuthUnitsRequestedTermedSql));
		} else {
			query = ((Query) sessionFactory.getCurrentSession().createSQLQuery(getServiceAuthUnitsRequestedSql));
		}

		query.setParameter("childId", childId)
				.setParameter("legalStartDate", legalStartDate)
				.setParameter("serviceCode", serviceCode);
		BigDecimal serviceAuthUnitsRequested = ((BigDecimal) query.uniqueResult());
		return !ObjectUtils.isEmpty(serviceAuthUnitsRequested) ? serviceAuthUnitsRequested.intValue() : 0;
	}

	public boolean getServiceAuthDtlOpenExists(Long childId, Date legalStatusDate, String serviceCode) {
		Query query = ((Query) sessionFactory.getCurrentSession().createSQLQuery(getServiceAuthDtlOpenExistsSql)
				.setParameter("childId", childId)
				.setParameter("legalStatusDate", legalStatusDate)
				.setParameter("serviceCode", serviceCode));
		return CHAR_IND_Y == (char) query.uniqueResult();

	}

	public boolean getServiceAuthDtlClosedExists(Long childId, Date legalStatusDate, String serviceCode) {
		Query query = ((Query) sessionFactory.getCurrentSession().createSQLQuery(getServiceAuthDtlClosedExistsSql)
				.setParameter("childId", childId)
				.setParameter("legalStatusDate", legalStatusDate)
				.setParameter("serviceCode", serviceCode));
		return CHAR_IND_Y == (char) query.uniqueResult();
	}
	public boolean getIs68OPaidFull(Long childId, Date legalStatusDate, Long resourceId) {
		Query query = ((Query) sessionFactory.getCurrentSession().createSQLQuery(getIs68OPaidFullSql)
				.setParameter("childId", childId)
				.setParameter("legalStatusDate", legalStatusDate)
				.setParameter("resourceId", resourceId));
		return CHAR_IND_Y == (char) query.uniqueResult();
	}

	public Date getSADPendingTermDate(Long childId,  Date legalStartDate, String serviceCode) {
		Query query = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(getSADPendingTermDateSql)
				.setParameter("childId", childId)
				.setParameter("legalStartDate", legalStartDate)
				.setParameter("serviceCode", serviceCode));
		Date sadStartDate = ((Date) query.uniqueResult());
		return sadStartDate;
	}

	public Date getSADCurrentMonthTermDate(Long childId,  Date legalStartDate, String serviceCode) {
		Query query = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(getSADCurrentMonthTermDateSql)
				.setParameter("childId", childId)
				.setParameter("legalStartDate", legalStartDate)
				.setParameter("serviceCode", serviceCode));
		Date sadStartDate = ((Date) query.uniqueResult());
		return sadStartDate;
	}
	public Set getSAHeaderIdSet(Long resourceId, Long contractId, Long caseId) {
		Set<Long> serviceAuthSet = new HashSet<Long>();
		if (!ObjectUtils.isEmpty(resourceId) && !ObjectUtils.isEmpty(contractId) && !ObjectUtils.isEmpty(caseId)) {
			Query query = ((Query) sessionFactory.getCurrentSession()
					.createSQLQuery(getSAHeaderIdSetSql)
					.addScalar("ID_SVC_AUTH", StandardBasicTypes.LONG)
					.setParameter("resourceId", resourceId)
					.setParameter("contractId", contractId)
					.setParameter("caseId", caseId));
			List<Long> serviceAuthList = (List<Long>) query.list();

			if (!ObjectUtils.isEmpty(serviceAuthList)) {
				serviceAuthSet.addAll(serviceAuthList);
			}
		}

		return serviceAuthSet;
	}

	@Override
	public List<SVCAuthDetailDto> getServiceAuthList(Long resourceId, Long personId) {
		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getServiceAuthListSql)
				.addScalar("idSvcAuthDtl", StandardBasicTypes.LONG)
				.addScalar("idSvcAuth", StandardBasicTypes.LONG)
				.setParameter("resourceId", resourceId)
				.setParameter("personId", personId)
				.setResultTransformer(Transformers.aliasToBean(SVCAuthDetailDto.class)));
		return sqlQuery.list();
	}

	public Long getServiceAuthId(Long resourceId, Long childId) {
		Query query = ((Query) sessionFactory.getCurrentSession().createSQLQuery(getServiceAuthIdSql)
				.addScalar("ID_SVC_AUTH", StandardBasicTypes.LONG)
				.setParameter("resourceId", resourceId)
				.setParameter("childId", childId));
		return (Long) query.uniqueResult();
	}

	public Long insertServcAuth(KinHomeInfoDto homeInfoBean, KinChildDto childBean, String serviceCode, Long contractId,
								 KinHomeInfoDto savedBean, Long placementsAdultId) {
		ServiceAuthorization serviceAuthorization = populateServcAuth(homeInfoBean, childBean, serviceCode,
				contractId, savedBean, placementsAdultId);
		Long idSvcAuth = (Long) sessionFactory.getCurrentSession().save(serviceAuthorization);
		sessionFactory.getCurrentSession().flush();
		return idSvcAuth;
	}

	public ServiceAuthorization serviceAuthorizationSave(ServiceAuthorization serviceAuthorization) {
		Long idServiceauth = (Long) sessionFactory.getCurrentSession().save(serviceAuthorization);
		return (ServiceAuthorization) sessionFactory.getCurrentSession().load(ServiceAuthorization.class, idServiceauth);
	}

	public ServiceAuthorization populateServcAuth(KinHomeInfoDto homeInfoBean, KinChildDto childBean, String serviceCode, Long contractId,
												  KinHomeInfoDto savedBean, Long placementsAdultId) {
		Date today = Calendar.getInstance().getTime();
		ServiceAuthorization serviceAuthorization = new ServiceAuthorization();
		serviceAuthorization.setDtLastUpdate(today);
		serviceAuthorization.setCreatedPersonId(Long.valueOf(String.valueOf(KINSHIP_AUTOMATED_SYSTEM_ID)));
		serviceAuthorization.setCreatedDate(today);
		serviceAuthorization.setLastUpdatedPersonId(Long.valueOf(String.valueOf(KINSHIP_AUTOMATED_SYSTEM_ID)));

		serviceAuthorization.setCdSvcAuthCounty(savedBean.getResourceAddressCounty());
		if (!TypeConvUtil.isNullOrEmpty(homeInfoBean.getIdHomeResource())) {
			CapsResource capsResource = (CapsResource) sessionFactory.getCurrentSession().get(CapsResource.class,
					homeInfoBean.getIdHomeResource());
			if (TypeConvUtil.isNullOrEmpty(capsResource)) {
				throw new DataNotFoundException(
						messageSource.getMessage("record.not.found.capsresource", null, Locale.US));
			}
			serviceAuthorization.setCapsResource(capsResource);
		}

		if (!TypeConvUtil.isNullOrEmpty(contractId)) {
			Contract contract = (Contract) sessionFactory.getCurrentSession().get(Contract.class, contractId);
			if (TypeConvUtil.isNullOrEmpty(contract)) {
				throw new DataNotFoundException(
						messageSource.getMessage("record.not.found.contract", null, Locale.US));
			}
			serviceAuthorization.setContract(contract);
		}

		if (!TypeConvUtil.isNullOrEmpty(placementsAdultId)) {
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, placementsAdultId);
			if (TypeConvUtil.isNullOrEmpty(person)) {
				throw new DataNotFoundException(
						messageSource.getMessage("record.not.found.person", null, Locale.US));
			}
			serviceAuthorization.setPersonByIdPrimaryClient(person);
		}

		serviceAuthorization.setCdSvcAuthCategory(CATOFSVC_24);
		serviceAuthorization.setCdSvcAuthRegion(savedBean.getResourceRegion());
		serviceAuthorization.setCdSvcAuthService(serviceCode);
		serviceAuthorization.setDtSvcAuthEff(homeInfoBean.getPaymentStartDate());
		serviceAuthorization.setIndSvcAuthComplete(STRING_IND_Y);
		serviceAuthorization.setIndDontdComntySvc(STRING_IND_N);
		serviceAuthorization.setTxtSvcAuthComments("Kinship Automated System Created.");

		return serviceAuthorization;
	}

	public int insertServcAuthEventLink(KinHomeInfoDto homeInfoBean, Long serviceAuthId, Long eventId) {
		SQLQuery insertQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(insertServcAuthEventLinkSql)
				.setParameter("eventId", eventId)
				.setParameter("serviceAuthId", serviceAuthId)
				.setParameter("lastUpdateDate", new Date())
				.setParameter("idHomeCase", homeInfoBean.getIdHomeCase()));
		int result = insertQuery.executeUpdate();
		sessionFactory.getCurrentSession().flush();
		return result;
	}

	public Date insertServiceAuthDtil(KinHomeInfoDto homeInfoBean, Long childId, Long serviceAuthId,
									  String serviceCode, double rate, Date startDate, int numUnitsRemaining,
									  int months, int lineItem, boolean isTanf) {
		Date today = Calendar.getInstance().getTime();

		Date endDate = null;
		double totalUnitsDouble = 0;
		double totalAmount = 0.0;
		int totalUnits = 0;
		int MAX_UNITS_68O = 123;
		int MAX_UNITS_68P = 242;

		if (serviceCode.equalsIgnoreCase(CSVCCODE_68O) && numUnitsRemaining >= MAX_UNITS_68O) {
			endDate = DateHelper.addToDate(startDate,0,months, -1);
		} else if(serviceCode.equalsIgnoreCase(CSVCCODE_68O) && numUnitsRemaining < MAX_UNITS_68O) {
			endDate = DateHelper.addToDate(startDate,0,0, numUnitsRemaining-1);
		} else if (serviceCode.equalsIgnoreCase(CSVCCODE_68P) && isTanf && numUnitsRemaining >= MAX_UNITS_68P ) {
			endDate = DateHelper.addToDate(startDate,0,months, -1);
		} else if (serviceCode.equalsIgnoreCase(CSVCCODE_68P) && isTanf && numUnitsRemaining < MAX_UNITS_68P ) {
			endDate = DateHelper.addToDate(startDate,0,0, numUnitsRemaining-1);
		} else if (!isTanf && numUnitsRemaining > 0 ) {
			endDate = DateHelper.addToDate(startDate,0,0, numUnitsRemaining-1);
		} else if (!isTanf && numUnitsRemaining ==  0 ) {
			endDate = DateHelper.addToDate(startDate,0,months, -1);
		}

		totalUnitsDouble  = DateHelper.daysDifference(endDate,startDate)+1;

		totalUnits = (int)Math.round(totalUnitsDouble);

		if( totalUnits > 0) {
			totalAmount = totalUnits * rate;
			SvcAuthDetail svcAuthDetail = new SvcAuthDetail();

			ServiceAuthorization serviceAuthorization = (ServiceAuthorization) sessionFactory.getCurrentSession().get(
					ServiceAuthorization.class, serviceAuthId);
			if (TypeConvUtil.isNullOrEmpty(serviceAuthorization)) {
				throw new DataNotFoundException(
						messageSource.getMessage("record.not.found.serviceAuthorization", null, Locale.US));
			}
			svcAuthDetail.setServiceAuthorization(serviceAuthorization);

			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, childId);
			if (TypeConvUtil.isNullOrEmpty(person)) {
				throw new DataNotFoundException(
						messageSource.getMessage("record.not.found.person", null, Locale.US));
			}
			svcAuthDetail.setPerson(person);

			svcAuthDetail.setDtLastUpdate(today);
			svcAuthDetail.setCreatedPersonId(Long.valueOf(String.valueOf(KINSHIP_AUTOMATED_SYSTEM_ID)));
			svcAuthDetail.setCreatedDate(today);
			svcAuthDetail.setLastUpdatedPersonId(Long.valueOf(String.valueOf(KINSHIP_AUTOMATED_SYSTEM_ID)));

			svcAuthDetail.setCdSvcAuthDtlAuthType(CSVATYPE_INI);
			svcAuthDetail.setCdSvcAuthDtlPeriod(CNPERIOD_DAY);
			svcAuthDetail.setCdSvcAuthDtlSvc(serviceCode);
			svcAuthDetail.setCdSvcAuthDtlUnitType(CINVUTYP_DA2);
			svcAuthDetail.setDtSvcAuthDtl(today);
			svcAuthDetail.setDtSvcAuthDtlBegin(startDate);
			svcAuthDetail.setDtSvcAuthDtlEnd(endDate);
			svcAuthDetail.setDtSvcAuthDtlTerm(endDate);
			svcAuthDetail.setDtSvcAuthDtlShow(today);

			svcAuthDetail.setAmtSvcAuthDtlAmtReq(totalAmount);
			svcAuthDetail.setNbrSvcAuthDtlFreq(1L);
			svcAuthDetail.setNbrSvcAuthDtlLineItm(Long.valueOf(String.valueOf(lineItem)));
			svcAuthDetail.setNbrSvcAuthDtlSugUnit(Long.valueOf(String.valueOf(totalUnits)));
			svcAuthDetail.setNbrSvcAuthDtlUnitsReq(Double.valueOf(String.valueOf(totalUnits)));
			svcAuthDetail.setNbrSvcAuthDtlUnitRate(rate);

			sessionFactory.getCurrentSession().save(svcAuthDetail);
		}

		return endDate;
	}

	@Override
	public Long insertMonthlyExtensionSADetail(KinMonthlyExtPaymentDto childBean, Long serviceAuthId, double rate, int totalUnitsAllowed, int lineItem) {
		double totalAmount = totalUnitsAllowed * rate;
		Date today = Calendar.getInstance().getTime();
		SvcAuthDetail svcAuthDetail = new SvcAuthDetail();

		if (!TypeConvUtil.isNullOrEmpty(serviceAuthId)) {
			ServiceAuthorization serviceAuthorization = (ServiceAuthorization) sessionFactory.getCurrentSession().get(ServiceAuthorization.class, serviceAuthId);
			if (TypeConvUtil.isNullOrEmpty(serviceAuthorization)) {
				throw new DataNotFoundException(
						messageSource.getMessage("record.not.found.serviceAuthorization", null, Locale.US));
			}
			svcAuthDetail.setServiceAuthorization(serviceAuthorization);
		}
		if (!TypeConvUtil.isNullOrEmpty(childBean.getChildId())) {
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, childBean.getChildId());
			if (TypeConvUtil.isNullOrEmpty(person)) {
				throw new DataNotFoundException(
						messageSource.getMessage("record.not.found.person", null, Locale.US));
			}
			svcAuthDetail.setPerson(person);
		}

		svcAuthDetail.setDtLastUpdate(today);
		svcAuthDetail.setCreatedPersonId(Long.valueOf(String.valueOf(KINSHIP_AUTOMATED_SYSTEM_ID)));
		svcAuthDetail.setCreatedDate(today);
		svcAuthDetail.setLastUpdatedPersonId(Long.valueOf(String.valueOf(KINSHIP_AUTOMATED_SYSTEM_ID)));

		svcAuthDetail.setCdSvcAuthDtlAuthType(CSVATYPE_INI);
		svcAuthDetail.setCdSvcAuthDtlPeriod(CNPERIOD_DAY);
		svcAuthDetail.setCdSvcAuthDtlSvc(CSVCCODE_68P);
		svcAuthDetail.setCdSvcAuthDtlUnitType(CINVUTYP_DA2);
		svcAuthDetail.setDtSvcAuthDtl(today);
		svcAuthDetail.setDtSvcAuthDtlBegin(childBean.getStartDate());
		svcAuthDetail.setDtSvcAuthDtlEnd(childBean.getEndDate());
		svcAuthDetail.setDtSvcAuthDtlTerm(childBean.getEndDate());
		svcAuthDetail.setDtSvcAuthDtlShow(today);
		svcAuthDetail.setAmtSvcAuthDtlAmtReq(totalAmount);
		svcAuthDetail.setNbrSvcAuthDtlFreq(1L);
		svcAuthDetail.setNbrSvcAuthDtlLineItm((long) lineItem);
		svcAuthDetail.setNbrSvcAuthDtlSugUnit((long)totalUnitsAllowed);
		svcAuthDetail.setNbrSvcAuthDtlUnitsReq((double)totalUnitsAllowed);
		svcAuthDetail.setNbrSvcAuthDtlUnitRate(rate);

		sessionFactory.getCurrentSession().save(svcAuthDetail);

		return svcAuthDetail.getIdSvcAuthDtl();
	}

	@Override
	public List<ServiceAuthDto> getSvcAuthEventInfo(Long idCase) {
		List<ServiceAuthDto> serviceAuthDtoList = new ArrayList<>();
		Query queryEvent = (Query) sessionFactory.getCurrentSession().createSQLQuery(getSvcAuthEventInfoSql)
				.addScalar("idServiceAuthDtl", StandardBasicTypes.LONG)
				.addScalar("idServiceAuth", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("svcAuthDtlAuthType", StandardBasicTypes.STRING)
				.addScalar("serviceAuthService", StandardBasicTypes.STRING)
				.addScalar("dtServiveAuthDtlBegin", StandardBasicTypes.DATE)
				.addScalar("dtServiveAuthDtlEnd", StandardBasicTypes.DATE)
				.addScalar("dtServiveAuthDtlTerm", StandardBasicTypes.DATE)
				.addScalar("unitReq", StandardBasicTypes.LONG)
				.addScalar("serviceAuthAmount", StandardBasicTypes.DOUBLE)
				.addScalar("unitRate", StandardBasicTypes.LONG)
				.addScalar("dtApproval", StandardBasicTypes.DATE)
				.setResultTransformer(Transformers.aliasToBean(ServiceAuthDto.class));
		queryEvent.setParameter("idCase", idCase);
		serviceAuthDtoList = queryEvent.list();
		return serviceAuthDtoList;
	}


}