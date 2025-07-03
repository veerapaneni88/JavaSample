package us.tx.state.dfps.service.financial.daoimpl;

import static us.tx.state.dfps.service.common.ServiceConstants.KINSHIP_AUTOMATED_SYSTEM_ID;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.Kinship;
import us.tx.state.dfps.common.domain.Name;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Placement;
import us.tx.state.dfps.common.domain.ServiceAuthorization;
import us.tx.state.dfps.common.domain.SsccList;
import us.tx.state.dfps.common.domain.SvcAuthDetail;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkInDto;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ServiceAuthDetailReq;
import us.tx.state.dfps.service.common.request.ServiceAuthorizationDetailReq;
import us.tx.state.dfps.service.common.response.ServiceAuthDetailRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.financial.dao.SvcAuthDetailDao;
import us.tx.state.dfps.service.financial.dto.ContractServiceDto;
import us.tx.state.dfps.service.financial.dto.LegalStatusValueDto;
import us.tx.state.dfps.service.financial.dto.ServiceAuthorizationDetailDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.EquivalentSvcDetailDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.KinshipDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.ServiceAuthDetailCodeDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.ServiceAuthDetailDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.ServiceAuthPersonDto;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StagePrincipalDto;
import us.tx.state.dfps.xmlstructs.outputstructs.RowQtyDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCON21S Class
 * Description: ServiceAuthorizationDetail DAO Implementation Apr 3, 2017 -
 * 10:07:58 AM
 * ************* Change History ****************************************************
 * 10/21/2019 SR 45217  HB4 changes, to exclude some validation messages for post PMC
 */

@Repository
public class SvcAuthDetailDaoImpl implements SvcAuthDetailDao {

	@Value("${ServiceAuthorizationDetailDaoImpl.getSerAuthDtl}")
	private String getSerAuthDtl;

	@Value("${ServiceAuthorizationDetailDaoImpl.selectPaymentDate}")
	private String selectPaymentDate;

	@Value("${ServiceAuthorizationDetailDaoImpl.getLegalEpisodeOfCare}")
	private String getLegalEpisodeOfCare;

	@Value("${ServiceAuthorizationDetailDaoImpl.isLegalEpisodePaymentExists}")
	private String isLegalEpisodePaymentExists;
	
	@Value("${ServiceAuthorizationDaoImpl.getExistingSiblingGrpForResouce}")
	private String getExistingSiblingGrpForResouce;

	@Value("${ServiceAuthorizationDaoImpl.selectLatestLegalStatus}")
	private String selectLatestLegalStatusSql;

	@Value("${ServiceAuthorizationDaoImpl.getCountSVCCCodeExistsCase1Sql}")
	private String getCountSVCCCodeExistsCase1Sql;

	@Value("${ServiceAuthorizationDaoImpl.getCountSVCCCodeExistsCase2Sql}")
	private String getCountSVCCCodeExistsCase2Sql;

	@Value("${ServiceAuthorizationDaoImpl.getCountSVCCCodeExistsCase3Sql}")
	private String getCountSVCCCodeExistsCase3Sql;

	@Value("${ServiceAuthorizationDaoImpl.getCountSVCCCodeExistsCase4Sql}")
	private String getCountSVCCCodeExistsCase4Sql;

	@Value("${ServiceAuthorizationDaoImpl.verifyDuplicate}")
	private String verifyDuplicate;

	@Value("${ServiceAuthorizationDaoImpl.andClauseWithServiceCode}")
	private String andClauseWithServiceCode;

	@Value("${ServiceAuthorizationDaoImpl.inClauseWithServiceCode}")
	private String inClauseWithServiceCode;

	@Value("${ServiceAuthorizationDaoImpl.otherKinServiceCodeClause}")
	private String otherKinServiceCodeClause;

	@Value("${ServiceAuthorizationDaoImpl.otherServiceCodeClause}")
	private String otherServiceCodeClause;
	
	@Value("${ServiceAuthorizationDaoImpl.getContractDetailSql}")
	private String getContractDetailSql;

	@Value("${ServiceAuthorizationDaoImpl.getContractSVCAndCNTYDetailSql}")
	private String getContractSVCAndCNTYDetailSql;

	@Value("${ServiceAuthorizationDaoImpl.getSituationStageDetailSql}")
	private String getSituationStageDetailSql;

	@Value("${ServiceAuthorizationDaoImpl.getInviceDetailSql}")
	private String getInviceDetailSql;

	@Value("${ServiceAuthorizationDaoImpl.getKinshipExistingDetailSql}")
	private String getKinshipExistingDetailSql;

	@Value("${ServiceAuthorizationDaoImpl.svcAuthDtlAuditSql}")
	private String svcAuthDtlAuditSql;

	@Value("${ServiceAuthorizationDaoImpl.fetchSvcAuthEventLinkSql}")
	private transient String fetchSvcAuthEventLinkSql;

	@Value("${ServiceAuthorizationDetailDaoImpl.getServiceAuthorizationDetailStartDate}")
	private  String getServiceAuthorizationDetailStartDateSql;

	@Value("${ServiceAuthorizationDetailDaoImpl.updateServiceAuthorizationDetailStartDate}")
	private  String updateServiceAuthorizationDetailStartDateSql;

	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private StagePersonLinkDao stagePersonLinkDao;
	private static final Logger log = Logger.getLogger(SvcAuthDetailDaoImpl.class);

	public SvcAuthDetailDaoImpl() {

	}

	/**
	 * 
	 * Method Description: This Method will receive Id Svc Auth and return List
	 * of Auth Detail records and its correspond Nm Person Full based upon Id
	 * Person. Service: CCON21S DAM: CLSC20D
	 * 
	 * @param serviceAuthorizationDetailReq
	 * @return List<ServiceAuthorizationDetailDto> @
	 */
	@SuppressWarnings("unchecked")
	public List<ServiceAuthorizationDetailDto> getSerAuthDetail(
			ServiceAuthorizationDetailReq serviceAuthorizationDetailReq) {

		List<ServiceAuthorizationDetailDto> svcAuthDtlList = new ArrayList<ServiceAuthorizationDetailDto>();

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getSerAuthDtl)
				.addScalar("idSvcAuthDtl", StandardBasicTypes.LONG).addScalar("idSvcAuth", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdSvcAuthDtlAuthType").addScalar("cdSvcAuthDtlPeriod").addScalar("cdSvcAuthDtlSvc")
				.addScalar("cdSvcAuthDtlUnitType").addScalar("dtSvcAuthDtl", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtSvcAuthDtlBegin", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtSvcAuthDtlEnd", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtSvcAuthDtlTerm", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtSvcAuthDtlShow", StandardBasicTypes.TIMESTAMP).addScalar("svcAuthDtlFreq")
				.addScalar("svcAuthDtlLineItm").addScalar("svcAuthDtlSugUnit").addScalar("svcAuthDtlUnitsReq")
				.addScalar("amtSvcAuthDtlAmtReq").addScalar("amtSvcAuthDtlAmtUsed").addScalar("svcAuthDtlUnitRate")
				.addScalar("svcAuthDtlUnitUsed").addScalar("cdPersonSex").addScalar("nmPersonFull")
				.addScalar("personAge").addScalar("dtPersonDeath", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPersonBirth", StandardBasicTypes.TIMESTAMP).addScalar("cdPersonReligion")
				.addScalar("cdPersonChar").addScalar("indPersonDobApprox").addScalar("cdPersonLivArr")
				.addScalar("cdPersGuardCnsrv").addScalar("cdPersonStatus").addScalar("cdPersonDeath")
				.addScalar("cdPersonMaritalStatus").addScalar("personOccupation").addScalar("cdPersonLanguage")
				.addScalar("cdPersonEthnicGroup").addScalar("indPersCancelHist")
				.setParameter("svcId", serviceAuthorizationDetailReq.getIdSvcAuth())
				.setResultTransformer(Transformers.aliasToBean(ServiceAuthorizationDetailDto.class));
		svcAuthDtlList = query.list();
		return svcAuthDtlList;
	}

	/**
	 * Method Name: selectLatestLegalStatus Method Description: This method
	 * fetches Latest Legal Status Record for the given Person and Legal Status
	 * from the database.
	 * 
	 * @param idPerson
	 * @param cdLegalStatStatus
	 * @return LegalStatusValueDto @
	 */
	@Override
	public LegalStatusValueDto selectLatestLegalStatus(Long idPerson, String cdLegalStatStatus) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectLatestLegalStatusSql)
				.addScalar("idLegalStatEvent", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("cdLegalStatCnty", StandardBasicTypes.STRING)
				.addScalar("cdLegalStatStatus", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatStatusDt", StandardBasicTypes.DATE)
				.addScalar("txtLegalStatCauseNbr", StandardBasicTypes.STRING)
				.addScalar("txtLegalStatCourtNbr", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatTmcDismiss", StandardBasicTypes.DATE)
				.addScalar("indCsupSend", StandardBasicTypes.STRING).addScalar("cdCourtNbr", StandardBasicTypes.STRING)
				.addScalar("cdDischargeRsn", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setParameter("cdLegalStatStatus", cdLegalStatStatus)
				.setResultTransformer(Transformers.aliasToBean(LegalStatusValueDto.class));

		LegalStatusValueDto legalStatusValueDto = (LegalStatusValueDto) sqlQuery.uniqueResult();

		return legalStatusValueDto;
	}

	/**
	 * 
	 * Method Name: getCountSVCCCodeExists Method Description:Service
	 * Authorization Detail Window when the save pushbutton is clicked.This DAM
	 * is a new service code exists in the Equivalency table for the given time
	 * period and open stages for the client. This DAM will also be used to
	 * check if the service code is exempt from the Equivalency table edit by
	 * querying the Non_Equivalency table. Finally, this DAM will be used to see
	 * if the service code exists in the Equivalency table for the given time
	 * period when a user adds new Contract Services to a Contract.
	 * 
	 * @param equivalentSvcDetailDto
	 * @param reqFunc
	 * @return RowQtyDto
	 */
	@Override
	public RowQtyDto getCountSVCCCodeExists(EquivalentSvcDetailDto equivalentSvcDetailDto, String reqFunc) {
		RowQtyDto rowQty = new RowQtyDto();
		if (!ObjectUtils.isEmpty(equivalentSvcDetailDto)
				&& StringUtils.isNotEmpty(equivalentSvcDetailDto.getReqFuncCd())) {
			SQLQuery sqlQuery = null;
			switch (equivalentSvcDetailDto.getReqFuncCd()) {
			case ServiceConstants.ONE:
				sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCountSVCCCodeExistsCase1Sql)
						.addScalar("rowQty", StandardBasicTypes.LONG)
						.setParameter("idEvent", equivalentSvcDetailDto.getIdEvent())
						.setParameter("cdEquivSvcDtlService", equivalentSvcDetailDto.getCdEquivSvcDtlService())
						.setParameter("dtEquivStartDate", equivalentSvcDetailDto.getDtEquivStartDate())
						.setParameter("dtEquivEndDate", equivalentSvcDetailDto.getDtEquivEndDate())
						.setResultTransformer(Transformers.aliasToBean(RowQtyDto.class));
				break;
			case ServiceConstants.TWO:
				sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCountSVCCCodeExistsCase2Sql)
						.addScalar("rowQty", StandardBasicTypes.LONG)
						.setParameter("idPerson", equivalentSvcDetailDto.getIdPerson())
						.setParameter("cdEquivSvcDtlService", equivalentSvcDetailDto.getCdEquivSvcDtlService())
						.setParameter("dtEquivStartDate", equivalentSvcDetailDto.getDtEquivStartDate())
						.setParameter("dtEquivEndDate", equivalentSvcDetailDto.getDtEquivEndDate())
						.setResultTransformer(Transformers.aliasToBean(RowQtyDto.class));
				break;
			case ServiceConstants.THREE:
				sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCountSVCCCodeExistsCase3Sql)
						.addScalar("rowQty", StandardBasicTypes.LONG)
						.setParameter("cdEquivSvcDtlService", equivalentSvcDetailDto.getCdEquivSvcDtlService())
						.setParameter("dtEquivStartDate", equivalentSvcDetailDto.getDtEquivStartDate())
						.setParameter("dtEquivEndDate", equivalentSvcDetailDto.getDtEquivEndDate())
						.setResultTransformer(Transformers.aliasToBean(RowQtyDto.class));
				break;
			case ServiceConstants.FOUR:
				sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCountSVCCCodeExistsCase4Sql)
						.addScalar("rowQty", StandardBasicTypes.LONG)
						.setParameter("cdEquivSvcDtlService", equivalentSvcDetailDto.getCdEquivSvcDtlService())
						.setParameter("dtEquivStartDate", equivalentSvcDetailDto.getDtEquivStartDate())
						.setParameter("dtEquivEndDate", equivalentSvcDetailDto.getDtEquivEndDate())
						.setResultTransformer(Transformers.aliasToBean(RowQtyDto.class));
				break;
			default:
				break;

			}
			if (!ObjectUtils.isEmpty(sqlQuery)) {
				rowQty = (RowQtyDto) sqlQuery.uniqueResult();
			}
		}
		return rowQty;

	}

	/**
	 * 
	 * Method Name: callVerifyDuplicate Method Description: to verify that a
	 * completed service_auth does not exist for a given stage for service code
	 * 69A. It combines CSESD9D,CSESG5D and CSEC17D
	 * 
	 * @param cdAuthDtlSvc
	 *            - contains service code
	 * @param idStage
	 *            - stage id
	 * @param dataAtion
	 *            -
	 * @return Long
	 */
	@Override
	public Long callVerifyDuplicate(ServiceAuthDetailDto serviceAuthDetailDto, ServiceAuthDetailReq serviceAuthDtlReq) {
		StringBuilder dynamic = new StringBuilder();
		SQLQuery sqlQuery;
		dynamic.append(verifyDuplicate);
		// verify that a completed service_auth does not exist for a given stage
		// for service code 69A
		if (StringUtils.isNotEmpty(serviceAuthDetailDto.getAuthDtlSvc())
				&& ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(serviceAuthDetailDto.getScrDataAction())
				&& CodesConstant.CSVCCODE_69A.equalsIgnoreCase(serviceAuthDetailDto.getAuthDtlSvc())) {
			dynamic.append(andClauseWithServiceCode);
			sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(dynamic.toString())
					.addScalar("rowQty", StandardBasicTypes.LONG)
					.setParameter("idStage", serviceAuthDtlReq.getIdStage())
					.setParameter("cdAuthDtlSvc", serviceAuthDetailDto.getAuthDtlSvc())
					.setResultTransformer(Transformers.aliasToBean(RowQtyDto.class));
		}
		// This condition is if the service is one in kinship list.
		else if (ServiceConstants.KINSHIP_LIST.contains(serviceAuthDetailDto.getAuthDtlSvc())) {
			dynamic.append(inClauseWithServiceCode);
			sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(dynamic.toString())
					.addScalar("rowQty", StandardBasicTypes.LONG)
					.setParameter("idSvcAuthDtl", serviceAuthDetailDto.getIdSvcAuthDtl())
					.setParameter("idResource", serviceAuthDtlReq.getIdResource())
					.setParameter("idPerson", serviceAuthDetailDto.getIdPerson())
					.setResultTransformer(Transformers.aliasToBean(RowQtyDto.class));
		}
		//HB4 CHANGES - SR 45217
		else if (ServiceConstants.KINSHIP_HB4_LIST.contains(serviceAuthDetailDto.getAuthDtlSvc())) {
			dynamic.append(otherKinServiceCodeClause);
			sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(dynamic.toString())
								.addScalar("rowQty", StandardBasicTypes.LONG)
								.setParameter("idSvcAuthDtl", serviceAuthDetailDto.getIdSvcAuthDtl())
								.setParameter("idResource", serviceAuthDtlReq.getIdResource())
								.setParameter("idPerson", serviceAuthDetailDto.getIdPerson())
								.setParameter("dtAuthBegin", serviceAuthDetailDto.getDtSvcAuthDtlTerm())
								.setParameter("dtAuthTerm", serviceAuthDetailDto.getDtSvcAuthDtlBegin())
								.setResultTransformer(Transformers.aliasToBean(RowQtyDto.class));
		 }
		// if the service is other than kinship service codes
		else {
			dynamic.append(otherServiceCodeClause);
			sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(dynamic.toString())
					.addScalar("rowQty", StandardBasicTypes.LONG)
					.setParameter("idSvcAuthDtl", serviceAuthDetailDto.getIdSvcAuthDtl())
					.setParameter("cdAuthDtlSvc", serviceAuthDetailDto.getAuthDtlSvc())
					.setParameter("idResource", serviceAuthDtlReq.getIdResource())
					.setParameter("dtAuthBegin", serviceAuthDetailDto.getDtSvcAuthDtlTerm())
					.setParameter("dtAuthTerm", serviceAuthDetailDto.getDtSvcAuthDtlBegin())
					.setParameter("idPerson", serviceAuthDetailDto.getIdPerson())
					.setResultTransformer(Transformers.aliasToBean(RowQtyDto.class));
		}
		RowQtyDto rowQtyDto = (RowQtyDto) sqlQuery.uniqueResult();
		return rowQtyDto.getRowQty();

	}

	/**
	 * 
	 * Method Name: retrieveBudgetAmount Method Description: Retrieves budget
	 * amount available for a service.
	 * 
	 * @param serviceAuthDetailDto
	 * @param serviceAuthDtlReq
	 * @return ContractServiceDto
	 */
	@Override
	public ContractServiceDto retrieveBudgetAmount(ServiceAuthDetailDto serviceAuthDetailDto,
			ServiceAuthDetailReq serviceAuthDtlReq) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getContractDetailSql)
				.addScalar("idContract", StandardBasicTypes.LONG).addScalar("idCntrctWkr", StandardBasicTypes.LONG)
				.addScalar("nbrCnsvcPeriod", StandardBasicTypes.LONG)
				.addScalar("nbrCnsvcVersion", StandardBasicTypes.LONG)
				.addScalar("nbrCnsvcLineItem", StandardBasicTypes.LONG).addScalar("idCnsvc", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdCnsvcService", StandardBasicTypes.STRING)
				.addScalar("cdCnsvcPaymentType", StandardBasicTypes.STRING)
				.addScalar("indCnsvcNewRow", StandardBasicTypes.STRING)
				.addScalar("nbrCnsvcUnitType", StandardBasicTypes.STRING)
				.addScalar("nbrCnsvcFedMatch", StandardBasicTypes.LONG)
				.addScalar("nbrCnsvcLocalMatch", StandardBasicTypes.LONG)
				.addScalar("nbrCnsvcUnitRate", StandardBasicTypes.FLOAT)
				.addScalar("amtCnsvcAdminAllUsed", StandardBasicTypes.DOUBLE)
				.addScalar("amtCnsvcEquip", StandardBasicTypes.DOUBLE)
				.addScalar("amtCnsvcEquipUsed", StandardBasicTypes.DOUBLE)
				.addScalar("amtCnsvcFrgBenft", StandardBasicTypes.DOUBLE)
				.addScalar("amtCnsvcFrgBenftUsed", StandardBasicTypes.DOUBLE)
				.addScalar("amtCnsvcOffItemUsed", StandardBasicTypes.DOUBLE)
				.addScalar("amtCnsvcOther", StandardBasicTypes.DOUBLE)
				.addScalar("amtCnsvcOtherUsed", StandardBasicTypes.DOUBLE)
				.addScalar("amtCnsvcSalary", StandardBasicTypes.DOUBLE)
				.addScalar("amtCnsvcSalaryUsed", StandardBasicTypes.DOUBLE)
				.addScalar("amtCnsvcSupply", StandardBasicTypes.DOUBLE)
				.addScalar("amtCnsvcSupplyUsed", StandardBasicTypes.DOUBLE)
				.addScalar("amtCnsvcTravel", StandardBasicTypes.DOUBLE)
				.addScalar("amtCnsvcTravelUsed", StandardBasicTypes.DOUBLE)
				.addScalar("amtCnsvcUnitRate", StandardBasicTypes.DOUBLE)
				.addScalar("amtCnsvcUnitRateUsed", StandardBasicTypes.DOUBLE)
				.setParameter("nbrCnperPeriod", serviceAuthDtlReq.getNbrCnperPeriod())
				.setParameter("idContract", serviceAuthDtlReq.getIdContract())
				.setParameter("nbrCnverVersion", serviceAuthDtlReq.getNbrCnverVersion())
				.setParameter("authDtlLineItm", serviceAuthDetailDto.getAuthDtlLineItm())
				.setParameter("authDtlSvc", serviceAuthDetailDto.getAuthDtlSvc())
				.setResultTransformer(Transformers.aliasToBean(ContractServiceDto.class));
		ContractServiceDto contractServiceDto = (ContractServiceDto) sqlQuery.uniqueResult();
		return contractServiceDto;

	}

	/**
	 * 
	 * Method Name: checkForTimeMisMatchException Method Description: This
	 * method used to find the time mismatch exception
	 * 
	 * @param serviceAuthDetailDto
	 *            -input to table for which data to be inserted
	 * @param errorDto
	 */
	@Override
	public void checkForTimeMisMatchException(ServiceAuthDetailDto serviceAuthDetailDto, ErrorDto errorDto) {
		SvcAuthDetail svcAuthDetail = (SvcAuthDetail) sessionFactory.getCurrentSession().get(SvcAuthDetail.class,
				serviceAuthDetailDto.getIdSvcAuthDtl());
		Timestamp newLastUpdatedTime = DateUtils.getDateyyyyMMddHHmmss(serviceAuthDetailDto.getDtLastUpdate());
		if (!ObjectUtils.isEmpty(svcAuthDetail) && !svcAuthDetail.getDtLastUpdate().equals(newLastUpdatedTime)) {
			errorDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
		}
	}

	/**
	 * 
	 * Method Name: performAUDForServiceAuthDetail(CAUD13DI) Method Description:
	 * This method used to save the data into SVC_AUTH_DETAIL table
	 * 
	 * @param serviceAuthDetailDto
	 *            -input to table for which data to be inserted
	 * @return Long idSvcAuthDtl
	 */
	@Override
	public Long performAUDForServiceAuthDetail(ServiceAuthDetailDto serviceAuthDetailDto) {

		SvcAuthDetail svcAuthDetail = new SvcAuthDetail();
		if (!ObjectUtils.isEmpty(serviceAuthDetailDto.getIdSvcAuthDtl())
				&& ServiceConstants.ZERO < serviceAuthDetailDto.getIdSvcAuthDtl()) {
			svcAuthDetail = (SvcAuthDetail) sessionFactory.getCurrentSession().get(SvcAuthDetail.class,
					serviceAuthDetailDto.getIdSvcAuthDtl());
		}
		ServiceAuthorization serviceAuth = new ServiceAuthorization();
		serviceAuth = (ServiceAuthorization) sessionFactory.getCurrentSession().get(ServiceAuthorization.class,
				serviceAuthDetailDto.getIdSvcAuth());
		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
				serviceAuthDetailDto.getIdPerson());
		Long idName = (Long) sessionFactory.getCurrentSession().createCriteria(Name.class)
				.setProjection(Projections.max("idName"))
				.add(Restrictions.eq("person.idPerson", serviceAuthDetailDto.getIdPerson()))
				.add(Restrictions.eq("indNamePrimary", ServiceConstants.Y))
				.add(Restrictions.eq("dtNameEndDate", DateUtils.getDefaultFutureDate())).uniqueResult();
		//Modified the code to check the null condition for Warranty defect 12007
		if (!ObjectUtils.isEmpty(idName)) {
			Name name = (Name) sessionFactory.getCurrentSession().get(Name.class, idName);
			svcAuthDetail.setName(name);
		}
		svcAuthDetail.setPerson(person);
		svcAuthDetail.setServiceAuthorization(serviceAuth);		
		svcAuthDetail.setAddrVendorCity(serviceAuthDetailDto.getVendorCity());
		svcAuthDetail.setNmVendor(serviceAuthDetailDto.getNmVendorName());
		svcAuthDetail.setAddrVendorState(serviceAuthDetailDto.getVendorState());
		svcAuthDetail.setAddrVendorStreetLn1(serviceAuthDetailDto.getVendorStreetLn1());
		svcAuthDetail.setAddrVendorStreetLn2(serviceAuthDetailDto.getVendorStreetLn2());
		svcAuthDetail.setAddrVendorZip(serviceAuthDetailDto.getVendorZip());
		svcAuthDetail.setAddrVendorZipSuff(serviceAuthDetailDto.getVendorZipSuff());
		if (!ObjectUtils.isEmpty(serviceAuthDetailDto.getVendorPhone()))
			svcAuthDetail.setAddrVendorPhone(serviceAuthDetailDto.getVendorPhone().replaceAll("[^0-9.]", ""));
		svcAuthDetail.setAddrVendorPhoneExt(serviceAuthDetailDto.getVendorPhoneExt());
		svcAuthDetail.setAmtSvcAuthDtlAmtReq(serviceAuthDetailDto.getAuthDtlAmtReq());
		svcAuthDetail.setAmtSvcAuthDtlAmtUsed(serviceAuthDetailDto.getAuthDtlAmtUsed());
		svcAuthDetail.setCdSvcAuthDtlAuthType(serviceAuthDetailDto.getAuthDtlAuthType());
		svcAuthDetail.setCdSvcAuthDtlPeriod(serviceAuthDetailDto.getAuthDtlPeriod());
		svcAuthDetail.setCdSvcAuthDtlSvc(serviceAuthDetailDto.getAuthDtlSvc());
		svcAuthDetail.setDtSvcAuthDtlBegin(serviceAuthDetailDto.getDtSvcAuthDtlBegin());
		svcAuthDetail.setDtSvcAuthDtlEnd(serviceAuthDetailDto.getDtSvcAuthDtlEnd());
		svcAuthDetail.setDtSvcAuthDtlTerm(serviceAuthDetailDto.getDtSvcAuthDtlTerm());
		svcAuthDetail.setDtSvcAuthDtl(serviceAuthDetailDto.getDtSvcAuthDtl());
		svcAuthDetail.setDtSvcAuthDtlShow(serviceAuthDetailDto.getDtSvcAuthDtlShow());
		svcAuthDetail.setDtTermRecorded(serviceAuthDetailDto.getDtTermRecorded());
		svcAuthDetail.setCdSvcAuthDtlUnitType(serviceAuthDetailDto.getAuthDtlUnitType());
		svcAuthDetail.setNbrSvcAuthDtlUnitRate(serviceAuthDetailDto.getAuthDtlUnitRate());
		svcAuthDetail.setNbrSvcAuthDtlFreq(serviceAuthDetailDto.getAuthDtlFreq());
		svcAuthDetail.setNbrSvcAuthDtlUnitsReq(serviceAuthDetailDto.getAuthDtlUnitReq());
		svcAuthDetail.setNbrSvcAuthDtlUnitUsed(serviceAuthDetailDto.getAuthDtlUnitUsed());
		svcAuthDetail.setNbrSvcAuthDtlSugUnit(serviceAuthDetailDto.getAuthDtlSugUnit());
		svcAuthDetail.setNbrSvcAuthDtlLineItm(serviceAuthDetailDto.getAuthDtlLineItm());
		svcAuthDetail.setIdTermRecordedPerson(serviceAuthDetailDto.getIdTermRecordedPerson());
		if (!ObjectUtils.isEmpty(serviceAuthDetailDto.getIndRmndrReqrd())) {
			svcAuthDetail.setIndRmndrReqrd(serviceAuthDetailDto.getIndRmndrReqrd());
		}
		if (ObjectUtils.isEmpty(svcAuthDetail.getIndRmndrSent())) {
			svcAuthDetail.setIndRmndrSent(ServiceConstants.N);
		}

		svcAuthDetail.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(svcAuthDetail);
		return svcAuthDetail.getIdSvcAuthDtl();
	}

	/**
	 * 
	 * Method Name: performAUDForKinship Method Description:This method used to
	 * save the data into KINSHIP table
	 * 
	 * @param kinshipDto
	 *            -input to table for which data to be inserted
	 * @return LOng idKinship
	 */
	@Override
	public Long performAUDForKinship(KinshipDto kinshipDto) {
		// Update Kinship CAUDK1D for all person with end date as current date
		Kinship kinship = new Kinship();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Kinship.class);
		criteria.add(Restrictions.isNull("dtKinshipEnd"));
		criteria.add(Restrictions.eq("person.idPerson", kinshipDto.getIdPerson()));
		List<Kinship> kinshipList = criteria.list();
		if (!ObjectUtils.isEmpty(kinshipList)) {
			kinshipList.forEach(kinshipEntity -> {
				kinshipEntity.setDtKinshipEnd(new Date());
				sessionFactory.getCurrentSession().saveOrUpdate(kinshipEntity);
			});
		}
		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, kinshipDto.getIdPerson());
		SvcAuthDetail svcAuthDetail = (SvcAuthDetail) sessionFactory.getCurrentSession().get(SvcAuthDetail.class,
				kinshipDto.getIdSvcAuthDtl());
		kinship.setDtSvcAuthDtlBegin(kinshipDto.getDtSvcAuthDtlBegin());
		kinship.setDtSvcAuthDtlTerm(kinshipDto.getDtSvcAuthDtlTerm());
		kinship.setPerson(person);
		kinship.setSvcAuthDetail(svcAuthDetail);
		kinship.setDtLastUpdate(new Date());
		if (ServiceConstants.REQ_FUNC_CD_UPD.equals(kinshipDto.getReqFuncCd())
				&& !ObjectUtils.isEmpty(kinshipDto.getIdKinship())
				&& ServiceConstants.ZERO < kinshipDto.getIdKinship()) {
			kinship.setIdKinship(kinshipDto.getIdKinship());
		}
		sessionFactory.getCurrentSession().saveOrUpdate(kinship);
		return kinship.getIdKinship();
	}

	/**
	 * 
	 * Method Name: retrieveServiceAuthDetail Method Description: Call
	 * Service(Cses25D) To retrieve ServiceAuthDetail Data by using
	 * idSvcAuthDetail which is unique key for SVC_AUTH_DTL table
	 * 
	 * @param idSvcAuthDtl
	 *            - unique key for SVC_AUTH_DTL table
	 * @return ServiceAuthDetailDto -which holds Service AUthorization Detail
	 *         data
	 */
	@Override
	public ServiceAuthDetailDto retrieveServiceAuthDetail(Long idSvcAuthDtl) {
		SvcAuthDetail svcAuthDetail = new SvcAuthDetail();
		ServiceAuthDetailDto serviceAuthDetailDto = new ServiceAuthDetailDto();
		if (!ObjectUtils.isEmpty(idSvcAuthDtl) && ServiceConstants.ZERO < idSvcAuthDtl) {
			svcAuthDetail = (SvcAuthDetail) sessionFactory.getCurrentSession().get(SvcAuthDetail.class, idSvcAuthDtl);
		}
		if (!ObjectUtils.isEmpty(svcAuthDetail.getIdSvcAuthDtl())
				&& ServiceConstants.ZERO < svcAuthDetail.getIdSvcAuthDtl()) {
			serviceAuthDetailDto.setIdSvcAuthDtl(svcAuthDetail.getIdSvcAuthDtl());
			serviceAuthDetailDto.setVendorCity(svcAuthDetail.getAddrVendorCity());
			serviceAuthDetailDto.setNmVendorName(svcAuthDetail.getNmVendor());
			serviceAuthDetailDto.setVendorState(svcAuthDetail.getAddrVendorState());
			serviceAuthDetailDto.setVendorStreetLn1(svcAuthDetail.getAddrVendorStreetLn1());
			serviceAuthDetailDto.setVendorStreetLn2(svcAuthDetail.getAddrVendorStreetLn2());
			serviceAuthDetailDto.setVendorZip(svcAuthDetail.getAddrVendorZip());
			serviceAuthDetailDto.setVendorZipSuff(svcAuthDetail.getAddrVendorZipSuff());
			serviceAuthDetailDto.setVendorPhone(svcAuthDetail.getAddrVendorPhone());
			serviceAuthDetailDto.setVendorPhoneExt(svcAuthDetail.getAddrVendorPhoneExt());
			serviceAuthDetailDto.setAuthDtlAmtReq(svcAuthDetail.getAmtSvcAuthDtlAmtReq());
			serviceAuthDetailDto.setAuthDtlAmtUsed(svcAuthDetail.getAmtSvcAuthDtlAmtUsed());
			serviceAuthDetailDto.setAuthDtlAuthType(svcAuthDetail.getCdSvcAuthDtlAuthType());
			serviceAuthDetailDto.setAuthDtlPeriod(svcAuthDetail.getCdSvcAuthDtlPeriod());
			serviceAuthDetailDto.setAuthDtlSvc(svcAuthDetail.getCdSvcAuthDtlSvc());
			serviceAuthDetailDto.setDtSvcAuthDtlBegin(svcAuthDetail.getDtSvcAuthDtlBegin());
			serviceAuthDetailDto.setDtSvcAuthDtlEnd(svcAuthDetail.getDtSvcAuthDtlEnd());
			serviceAuthDetailDto.setDtSvcAuthDtlTerm(svcAuthDetail.getDtSvcAuthDtlTerm());
			serviceAuthDetailDto.setDtSvcAuthDtl(svcAuthDetail.getDtSvcAuthDtl());
			serviceAuthDetailDto.setDtSvcAuthDtlShow(svcAuthDetail.getDtSvcAuthDtlShow());
			serviceAuthDetailDto.setDtTermRecorded(svcAuthDetail.getDtTermRecorded());
			serviceAuthDetailDto.setAuthDtlUnitType(svcAuthDetail.getCdSvcAuthDtlUnitType());
			serviceAuthDetailDto.setAuthDtlUnitRate(svcAuthDetail.getNbrSvcAuthDtlUnitRate());
			serviceAuthDetailDto.setAuthDtlFreq(svcAuthDetail.getNbrSvcAuthDtlFreq());
			serviceAuthDetailDto.setAuthDtlUnitReq(svcAuthDetail.getNbrSvcAuthDtlUnitsReq());
			serviceAuthDetailDto.setAuthDtlUnitUsed(svcAuthDetail.getNbrSvcAuthDtlUnitUsed());
			serviceAuthDetailDto.setAuthDtlSugUnit(svcAuthDetail.getNbrSvcAuthDtlSugUnit());
			serviceAuthDetailDto.setAuthDtlLineItm(svcAuthDetail.getNbrSvcAuthDtlLineItm());
			serviceAuthDetailDto.setIdTermRecordedPerson(svcAuthDetail.getIdTermRecordedPerson());
			serviceAuthDetailDto.setDtLastUpdate(svcAuthDetail.getDtLastUpdate());
			if (!ObjectUtils.isEmpty(svcAuthDetail.getName())
					&& !ObjectUtils.isEmpty(svcAuthDetail.getName().getIdName())) {
				serviceAuthDetailDto.setIdName(svcAuthDetail.getName().getIdName());
			}
			if (!ObjectUtils.isEmpty(svcAuthDetail.getPerson())
					&& !ObjectUtils.isEmpty(svcAuthDetail.getPerson().getIdPerson())) {
				serviceAuthDetailDto.setIdPerson(svcAuthDetail.getPerson().getIdPerson());
			}
			serviceAuthDetailDto.setIndRmndrReqrd(svcAuthDetail.getIndRmndrReqrd());
		}
		return serviceAuthDetailDto;
	}

	/**
	 * 
	 * Method Name: rtrvCntrctSvcAndCntyList Method Description: retrieve
	 * details from tables CONTRACT_SERVICE, CONTRACT_COUNTY , STAGE_PERSON_LINK
	 * ,PERSON,Situation and Stage
	 * 
	 * @param serviceAuthDtlReq
	 *            - data contains Id_contract which retrieves the record
	 * @return ServiceAuthorizationDetailRes - list of ContarctService and
	 *         County Details and Situation And Stage details and
	 *         StagePersonLink details
	 */
	@Override
	public ServiceAuthDetailRes rtrvCntrctSvcAndCntyList(ServiceAuthDetailReq serviceAuthDtlReq) {
		ServiceAuthDetailRes serviceAuthorizationRes = new ServiceAuthDetailRes();
		ErrorDto errorDto = new ErrorDto();
		Long cnPerPeriod=0l;
		Long nbrVersion=0l;
		if(!ObjectUtils.isEmpty(serviceAuthDtlReq.getNbrCnperPeriod())) {
			cnPerPeriod=serviceAuthDtlReq.getNbrCnperPeriod();
		}
		if(!ObjectUtils.isEmpty(serviceAuthDtlReq.getNbrCnverVersion())) {
			nbrVersion=serviceAuthDtlReq.getNbrCnverVersion();
		}
		// get details from Contract Service and Contract County table
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getContractSVCAndCNTYDetailSql)
				.addScalar("cnSvcPaymentType", StandardBasicTypes.STRING)
				.addScalar("cnSvcService", StandardBasicTypes.STRING)
				.addScalar("cnSvcUnitType", StandardBasicTypes.STRING)
				.addScalar("cnSvcUnitRate", StandardBasicTypes.DOUBLE)
				.addScalar("cnSvcLineItem", StandardBasicTypes.LONG)
				.setParameter("idContract", serviceAuthDtlReq.getIdContract())
				.setParameter("nbrCnperPeriod", cnPerPeriod)
				.setParameter("nbrCnverVersion", nbrVersion)
				.setParameter("svcAuthCounty", serviceAuthDtlReq.getSvcAuthCounty())
				.setResultTransformer(Transformers.aliasToBean(ServiceAuthDetailCodeDto.class));
		List<ServiceAuthDetailCodeDto> serviceAuthCodeList = sqlQuery.list();
		// get details from Situation and Stage table
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSituationStageDetailSql)
				.addScalar("dtStageStart", StandardBasicTypes.DATE).addScalar("dtStageClose", StandardBasicTypes.DATE)
				.addScalar("dtSituationOpened", StandardBasicTypes.DATE)
				.setParameter("idStage", serviceAuthDtlReq.getIdStage())
				.setResultTransformer(Transformers.aliasToBean(ServiceAuthDetailRes.class));
		serviceAuthorizationRes = (ServiceAuthDetailRes) query.uniqueResult();
		if (!ObjectUtils.isEmpty(serviceAuthCodeList)) {
			serviceAuthorizationRes.setServiceAuthCodeList(serviceAuthCodeList);
		} else {
			errorDto.setErrorCode(ServiceConstants.MSG_CON_CONTRACT_SVC);
		}
		if ((ObjectUtils.isEmpty(errorDto.getErrorCode()) || ServiceConstants.ZERO == errorDto.getErrorCode())
				&& StringUtils.isNotEmpty(serviceAuthDtlReq.getReqFuncCd())
				&& ServiceConstants.WINDOW_MODE_NEW.equalsIgnoreCase(serviceAuthDtlReq.getReqFuncCd())
				&& (ObjectUtils.isEmpty(serviceAuthDtlReq.getIdSvcAuthDtl())
						|| ServiceConstants.ZERO == serviceAuthDtlReq.getIdSvcAuthDtl())) {
			// get details from STAGE_PERSON_LINK and Person table
			List<StagePrincipalDto> stagePrincipalDtoList = stagePersonLinkDao.getStagePrincipalByIdStageType(
					serviceAuthDtlReq.getIdStage(), serviceAuthDtlReq.getStagePersType());
			if (!ObjectUtils.isEmpty(stagePrincipalDtoList)) {
				List<ServiceAuthPersonDto> serviceAuthPersonList = new ArrayList<>();
				stagePrincipalDtoList.parallelStream().forEach(stagePrsn -> {
					ServiceAuthPersonDto serviceAuthPrsnDto = new ServiceAuthPersonDto();
					serviceAuthPrsnDto.setIdPerson(stagePrsn.getIdPerson());
					serviceAuthPrsnDto.setNmPersonFull(!StringUtils.isEmpty(stagePrsn.getCdPersonSuffix())
							? stagePrsn.getNmPersonFull() + " " + stagePrsn.getCdPersonSuffix()
							: stagePrsn.getNmPersonFull());
					serviceAuthPrsnDto.setStagePersRelInt(stagePrsn.getCdStagePersRelInt());
					serviceAuthPrsnDto.setStagePersRole(stagePrsn.getCdStagePersRole());
					serviceAuthPersonList.add(serviceAuthPrsnDto);
				});
				serviceAuthorizationRes.setServiceAuthPersonList(serviceAuthPersonList);
			} else {
				errorDto.setErrorCode(ServiceConstants.MSG_CON_PRINCIPLE);
			}

		}
		if (!ObjectUtils.isEmpty(errorDto.getErrorCode())) {
			serviceAuthorizationRes.setErrorDto(errorDto);
		}
		return serviceAuthorizationRes;
	}

	/**
	 * 
	 * Method Name: getPlacementHistory Method Description:
	 * 
	 * @param idPlcmtChild-
	 *            idPerson whose age is > 18
	 * @param idPlcmtAdult-
	 *            primary client id
	 * @param idRsrcFacil-
	 *            resource id
	 * @param serviceCode-
	 *            service from SvcHeader
	 * @param cdPlcmtLivArr-Living
	 *            Arrangement
	 * @return List<PlacementDto> - get List of Placement
	 */
	@Override
	public List<PlacementDto> getPlacementHistory(Long idPlcmtChild, Long idPlcmtAdult, Long idRsrcFacil,
			String serviceCode, String cdPlcmtLivArr) {
		List<PlacementDto> placementDtoList = new ArrayList<PlacementDto>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Placement.class);
		criteria.add(Restrictions.eq("personByIdPlcmtChild.idPerson", idPlcmtChild));
		criteria.add(Restrictions.eq("capsResourceByIdRsrcFacil.idResource", idRsrcFacil));
		criteria.add(Restrictions.eq("cdPlcmtActPlanned", ServiceConstants.A));
		criteria.add(Restrictions.eq("cdPlcmtLivArr", cdPlcmtLivArr));
		if (ServiceConstants.KINSHIP_LIST.contains(serviceCode)) {
			criteria.add(Restrictions.eq("personByIdPlcmtAdult.idPerson", idPlcmtAdult));
		}
		criteria.addOrder(Order.desc("dtPlcmtStart"));
		List<Placement> placementList = criteria.list();
		if (!ObjectUtils.isEmpty(placementList)) {
			placementList.parallelStream().forEach(placement -> {
				PlacementDto placementDto = new PlacementDto();
				placementDto.setDtPlcmtStart(placement.getDtPlcmtStart());
				placementDto.setDtPlcmtEnd(placement.getDtPlcmtEnd());
				placementDto.setCdPlcmtRemovalRsn(placement.getCdPlcmtRemovalRsn());
				if (!ObjectUtils.isEmpty(placement.getPersonByIdPlcmtChild())
						&& !ObjectUtils.isEmpty(placement.getPersonByIdPlcmtChild().getIdPerson())) {
					placementDto.setIdPlcmtChild(placement.getPersonByIdPlcmtChild().getIdPerson());
				}
				placementDto.setCdPlcmtLivArr(placement.getCdPlcmtLivArr());
				placementDtoList.add(placementDto);
			});
			;
		}
		return placementDtoList;
	}

	/**
	 * 
	 * Method Name: retrieveKinshipDetail Method Description: retrieve List
	 * Kinship detail for idPerson(CSESA7D)
	 * 
	 * @param idPerson
	 * @return KinshipDto
	 */
	@Override
	public KinshipDto retrieveKinshipDetail(Long idPerson) {
		KinshipDto kinshipDto = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Kinship.class);
		criteria.add(Restrictions.eq("person.idPerson", idPerson));
		criteria.setProjection(Projections.max("idKinship"));
		Long idKinship = (Long) criteria.uniqueResult();
		if(!ObjectUtils.isEmpty(idKinship)){
			Kinship kinship = (Kinship) sessionFactory.getCurrentSession().load(Kinship.class, idKinship);
			if (!ObjectUtils.isEmpty(kinship)) {
				kinshipDto = new KinshipDto();
				BeanUtils.copyProperties(kinship, kinshipDto);
				kinshipDto.setIdSvcAuthDtl(kinship.getSvcAuthDetail().getIdSvcAuthDtl());
			}
		}
		return kinshipDto;
	}

	/**
	 * 
	 * Method Name: retrieveInvoiceDetail Method Description: retrieve Invoice
	 * phase from INVOICE and DELVRD_SVC_DTL table(CSESA8D)
	 * 
	 * @param idSvcAuthDtl
	 *            - service authorization detail id
	 * @return List<String> - Invoice phase
	 */
	@Override
	public List<String> retrieveInvoiceDetail(Long idSvcAuthDtl) {
		List<String> cdInvoicePhase = new ArrayList<String>();
		if (!ObjectUtils.isEmpty(idSvcAuthDtl) && ServiceConstants.ZERO < idSvcAuthDtl) {
			SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getInviceDetailSql)
					.setParameter("idSvcAuthDtl", idSvcAuthDtl);
			cdInvoicePhase = (List<String>) query.list();
		}
		return cdInvoicePhase;
	}

	/**
	 * 
	 * Method Name: getPlacementHistory Method Description: This function finds
	 * existing kinship Records.CLSS92D DAM
	 * 
	 * @param idSvcAuthDtl
	 * @return List<PersonDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonDto> retrieveKinshipForExisting(Long idSvcAuthDtl) {
		List<PersonDto> personDtoList = new ArrayList<>();
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getKinshipExistingDetailSql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.setParameter("idSvcAuthDtl", idSvcAuthDtl).setResultTransformer(Transformers.aliasToBean(PersonDto.class));
		personDtoList = query.list();
		return personDtoList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.financial.dao.SvcAuthDetailDao#
	 * getMaxPlacementEndDtRecord(java.lang.Long, java.lang.Long,
	 * java.lang.Long, java.lang.String, java.lang.String)
	 */
	@Override
	public PlacementDto getMaxPlacementEndDtRecord(Long idPlcmtChild, Long idRsrcFacil, String serviceCode,
			String cdPlcmtLivArr) {
		PlacementDto plcmtDto = new PlacementDto();
		Criteria maxEndDateCriteria = sessionFactory.getCurrentSession().createCriteria(Placement.class);
		maxEndDateCriteria.add(Restrictions.eq("personByIdPlcmtChild.idPerson", idPlcmtChild));
		maxEndDateCriteria.add(Restrictions.eq("cdPlcmtActPlanned", ServiceConstants.A));
		maxEndDateCriteria.setProjection(Projections.max("dtPlcmtEnd"));
		Date mxDtPlcmtEnd = (Date) maxEndDateCriteria.uniqueResult();
		if (!ObjectUtils.isEmpty(mxDtPlcmtEnd)) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Placement.class);
			criteria.add(Restrictions.eq("personByIdPlcmtChild.idPerson", idPlcmtChild));
			criteria.add(Restrictions.eq("capsResourceByIdRsrcFacil.idResource", idRsrcFacil));
			criteria.add(Restrictions.eq("cdPlcmtActPlanned", ServiceConstants.A));
			criteria.add(Restrictions.eq("cdPlcmtLivArr", cdPlcmtLivArr));
			criteria.add(Restrictions.eq("dtPlcmtEnd", mxDtPlcmtEnd));
			plcmtDto = (PlacementDto) criteria.uniqueResult();
		}
		return !ObjectUtils.isEmpty(plcmtDto) ? plcmtDto : new PlacementDto();
	}

	/**
	 * Method Name: getTermDayCareSvcAuth Method Description: retrieve the count
	 * of service auth detail audit based on the idSvcAuthDtl
	 */
	@Override
	public boolean getTermDayCareSvcAuth(Long idSvcAuthDtl) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(svcAuthDtlAuditSql);
		query.setParameter("idSvcAuthDtl", idSvcAuthDtl);
		if (((BigDecimal) query.uniqueResult()).longValue() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * This method sets SSCC_LIST table with IND_NONSSCC_SVC_AUTH = 'Y'
	 * 
	 * EJB Name : ServiceAuthBean.java
	 * 
	 * @param idSSCCReferral
	 * @return long
	 */
	public long updateSSCCListDao(long idSSCCReferral) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccList.class);
		criteria.add(Restrictions.eq("ssccReferral.idSSCCReferral", idSSCCReferral));
		List<SsccList> ssccLists = criteria.list();
		for (SsccList ssccList : ssccLists) {
			ssccList.setIndNonssccSvcAuth("Y");
			sessionFactory.getCurrentSession().saveOrUpdate(ssccList);
		}
		return ssccLists.size();
	}

	/**
	 * MethodName: updateSSCCListDC MethodDescription:This method updates
	 * SSCC_LIST table with IND_SSCC_DAYCARE = 'Y', DT_SSCC_DAYCARE = system
	 * date for Day Care Requests. EJB Name : ServiceAuthBean.java
	 * 
	 * @param idSSCCReferral
	 * @return long
	 */
	@Override
	public long updateSSCCListDCDao(long idSSCCReferral) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccList.class);
		criteria.add(Restrictions.eq("ssccReferral.idSSCCReferral", idSSCCReferral));
		List<SsccList> ssccLists = criteria.list();
		for (SsccList ssccList : ssccLists) {
			ssccList.setIndSsccDaycare("Y");
			Date date = new Date();
			ssccList.setDtSsccDaycare(date);
			sessionFactory.getCurrentSession().saveOrUpdate(ssccList);
		}
		return ssccLists.size();
	}

	/**
	 * Method Name: getServiceAuthorizationEventDetails Method Description: This
	 * method fetches the Service Authorization Event details
	 * 
	 * @param idCase
	 * @param idStage
	 * @param eventTypeCode
	 * @return List<ServiceAuthEventLinkValueDto>
	 */
	@Override
	public List<SvcAuthEventLinkInDto> getServiceAuthorizationEventDetails(Long idCase, Long idStage,
			String eventTypeCode) {
		Map<String, SvcAuthEventLinkInDto> eventMap = new HashMap<String, SvcAuthEventLinkInDto>();

		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(fetchSvcAuthEventLinkSql);
		query.setParameter("idEventStage", idStage);
		query.setParameter("cdTask", eventTypeCode);
		query.setParameter("idCase", idCase);

		query.addScalar("svcAuthId", StandardBasicTypes.LONG).addScalar("svcAuthEventId", StandardBasicTypes.LONG)
				.addScalar("txtEventDescription", StandardBasicTypes.STRING)
				.addScalar("personId", StandardBasicTypes.LONG);

		List<SvcAuthEventLinkInDto> serviceAuthEventLinkValueDtos = query
				.setResultTransformer(Transformers.aliasToBean(SvcAuthEventLinkInDto.class)).list();
		for (SvcAuthEventLinkInDto serviceAuthEventLinkValueDto : serviceAuthEventLinkValueDtos) {
			Long said = serviceAuthEventLinkValueDto.getSvcAuthId();
			Long saeid = serviceAuthEventLinkValueDto.getSvcAuthEventId();
			Long idPerson = serviceAuthEventLinkValueDto.getPersonId();
			String eventKey = new StringBuilder(said.intValue()).append(ServiceConstants.PIPE).append(saeid)
					.append(ServiceConstants.PIPE).append(idCase).toString();
			if (eventMap.containsKey(eventKey)) {
				SvcAuthEventLinkInDto authEventLinkValueDto = eventMap.get(eventKey);
				authEventLinkValueDto.getPersons().add(idPerson);
			} else {
				serviceAuthEventLinkValueDto.setCaseId(idCase);
				serviceAuthEventLinkValueDto.getPersons().add(idPerson);
				eventMap.put(eventKey, serviceAuthEventLinkValueDto);
			}
		}
		List<SvcAuthEventLinkInDto> serviceAuthEventLinkValueDtosRet = new ArrayList<>(eventMap.values());
		return serviceAuthEventLinkValueDtosRet;
	}

	/**
	 * 
	 * Method Name: selectPaymentDate Method Description: Retrieve the Kinship
	 * child ID_SVC_AUTH_DTL from Kinship table and the service auth effective
	 * date.
	 * 
	 * @param idPersonInput
	 * @return
	 */
	@Override
	public ServiceAuthDetailDto selectPaymentDate(Long idPersonInput) {
		List<ServiceAuthDetailDto> serviceAuthDetailDto = new ArrayList<>();
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectPaymentDate)
				.addScalar("idSvcAuthDtl", StandardBasicTypes.LONG)
				.addScalar("dtSvcAuthDtlBegin", StandardBasicTypes.DATE).setParameter("idPersonInput", idPersonInput)
				.setResultTransformer(Transformers.aliasToBean(ServiceAuthDetailDto.class));
		serviceAuthDetailDto = query.list();
		return serviceAuthDetailDto.get(0);
	}

	/**
	 * 
	 * Method Name: getLegalEpisodeOfCare Method Description: For a given person
	 * get the episode of care.
	 * 
	 * @param idPersonInput
	 * @param dtEffective
	 * @return
	 */
	@Override
	public ServiceAuthDetailDto getLegalEpisodeOfCare(Long idPersonInput, Date dtEffective) {
		List<ServiceAuthDetailDto> serviceAuthDetailDto = new ArrayList<>();
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getLegalEpisodeOfCare)
				.addScalar("dtLegalEpisodeEnterls", StandardBasicTypes.DATE)
				.addScalar("dtLegalEpisodeTermls", StandardBasicTypes.DATE).setParameter("idPersonInput", idPersonInput)
				.setParameter("dtEffective", DateUtils.stringDt(dtEffective)).setResultTransformer(Transformers.aliasToBean(ServiceAuthDetailDto.class));
		serviceAuthDetailDto = query.list();
		return serviceAuthDetailDto.get(0);
	}
	
	/**
	 * 
	 *Method Name:	getExistingSiblingGrpForResouce
	 *Method Description: This method fetches the Sibling Group list for a given Resource
	 *@param idResource
	 *@return
	 */
	@Override
	public List<ServiceAuthDetailDto> getExistingSiblingGrpForResouce(Long idResource) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getExistingSiblingGrpForResouce)
				.addScalar("idContract", StandardBasicTypes.LONG)
				.addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idSvcAuth", StandardBasicTypes.LONG)
				.addScalar("authDtlSvc", StandardBasicTypes.STRING)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.setParameter("idResource", idResource).setResultTransformer(Transformers.aliasToBean(ServiceAuthDetailDto.class));
		List<ServiceAuthDetailDto> siblinggroup = query.list();
		return siblinggroup;
	}

	/**
	 * 
	 * Method Name: isLegalEpisodePaymentExists Method Description:This method
	 * checks whether a payment already exist for a given legal status episode.
	 * 
	 * @param serviceAuthDetailDto
	 * @return
	 */
	@Override
	public boolean isLegalEpisodePaymentExists(ServiceAuthDetailDto serviceAuthDetailDto) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(isLegalEpisodePaymentExists)
				.addScalar("qty", StandardBasicTypes.INTEGER)
				.setParameter("idSvcAuthDtl", serviceAuthDetailDto.getIdSvcAuthDtl())
				.setParameter("dtSvcAuthDtlBegin", DateUtils.stringDt(serviceAuthDetailDto.getDtSvcAuthDtlBegin()))
				.setParameter("dtLegalEpisodeEnterls", DateUtils.stringDt(serviceAuthDetailDto.getDtLegalEpisodeEnterls()))
				.setParameter("dtLegalEpisodeTermls", DateUtils.stringDt(serviceAuthDetailDto.getDtLegalEpisodeTermls()));
		Integer qty = (Integer) query.uniqueResult();
		if (!ObjectUtils.isEmpty(qty) && qty > 0) {
			return true;
		}
		return false;
	}

	@Override
	public void termServiceAuthDetails(Long resourceId) {
		SimpleDateFormat sqlDateFormat = new SimpleDateFormat(ServiceConstants.DATE_FORMAT);
		String currentDate = sqlDateFormat.format(new Date());
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getServiceAuthorizationDetailStartDateSql)
				.addScalar("idSvcAuthDtl", StandardBasicTypes.LONG)
				.addScalar("dtSvcAuthDtlBegin", StandardBasicTypes.DATE)
				.setParameter("resourceId", resourceId)
				.setParameter("currentDate", currentDate)
				.setResultTransformer(Transformers.aliasToBean(ServiceAuthDetailDto.class));
		List<ServiceAuthDetailDto> serviceAuthDetailDtos = query.list();

		if(!CollectionUtils.isEmpty(serviceAuthDetailDtos)) {
			serviceAuthDetailDtos.stream().forEach(serviceAuthDetailDto -> {

				Query queryUpdate = sessionFactory.getCurrentSession().createSQLQuery(updateServiceAuthorizationDetailStartDateSql);
				queryUpdate.setParameter("termDate", serviceAuthDetailDto.getDtSvcAuthDtlBegin());
				queryUpdate.setParameter("termRecordedPersonId", KINSHIP_AUTOMATED_SYSTEM_ID);
				queryUpdate.setParameter("lastUpdatedPersonId", KINSHIP_AUTOMATED_SYSTEM_ID);
				queryUpdate.setParameter("idSvcAuthDtl", serviceAuthDetailDto.getIdSvcAuthDtl());

				queryUpdate.executeUpdate();
			});
		}
	}


}
