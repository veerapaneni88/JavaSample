package us.tx.state.dfps.service.sscc.daoimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.CnsrvtrshpRemoval;
import us.tx.state.dfps.common.domain.LegalStatus;
import us.tx.state.dfps.common.domain.ServiceAuthorization;
import us.tx.state.dfps.common.domain.SsccChildPlan;
import us.tx.state.dfps.common.domain.SsccDaycareRequest;
import us.tx.state.dfps.common.domain.SsccExceptCareDesig;
import us.tx.state.dfps.common.domain.SsccList;
import us.tx.state.dfps.common.domain.SsccParameters;
import us.tx.state.dfps.common.domain.SsccPlcmtHeader;
import us.tx.state.dfps.common.domain.SsccReferral;
import us.tx.state.dfps.common.domain.SsccReferralEvent;
import us.tx.state.dfps.common.domain.SsccReferralFamily;
import us.tx.state.dfps.common.domain.SsccServiceAuthorization;
import us.tx.state.dfps.common.domain.SsccTimeline;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.common.domain.SvcAuthDetail;
import us.tx.state.dfps.common.domain.SvcAuthEventLink;
import us.tx.state.dfps.common.domain.SvcAuthValid;
import us.tx.state.dfps.common.dto.AssignmentGroupDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.SSCCListDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCParameterDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefFamilyDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefListDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefPlcmtDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCResourceDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCTimelineDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.childplan.dto.SSCCChildPlanDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.FormattingUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.sscc.dao.SSCCPlacementNetworkDao;
import us.tx.state.dfps.service.sscc.dao.SSCCRefDao;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.subcare.dto.StgPersonLinkDto;
import us.tx.state.dfps.service.workload.dao.ApprovalDao;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.service.workload.service.ToDoService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:SSCCRefDaoImpl Aug 3, 2018- 12:29:23 PM © 2018 Texas Department
 * of Family and Protective Services.
 */
@Repository
@SuppressWarnings("unchecked")
public class SSCCRefDaoImpl implements SSCCRefDao {

	/** The fetch SSCC cntrct regionfor stage county sql. */
	@Value("${SSCCRefDaoImpl.fetchSSCCCntrctRegionforStageCounty}")
	private String fetchSSCCCntrctRegionforStageCountySql;

	/** The fetch active SSCC referrals for stage sql. */
	@Value("${SSCCRefDaoImpl.fetchActiveSSCCReferralsForStage}")
	private String fetchActiveSSCCReferralsForStageSql;

	/** The fetch active SSCC referrals for stage plcmt clause sql. */
	@Value("${SSCCRefDaoImpl.fetchActiveSSCCReferralsForStagePlcmtClause}")
	private String fetchActiveSSCCReferralsForStagePlcmtClauseSql;

	/** The fetch active SSCC referrals for stage family clause sql. */
	@Value("${SSCCRefDaoImpl.fetchActiveSSCCReferralsForStageFamilyClause}")
	private String fetchActiveSSCCReferralsForStageFamilyClauseSql;

	/** The fetch SSCC cntrct region SUB sql. */
	@Value("${SSCCRefDaoImpl.fetchSSCCCntrctRegionSUB}")
	private String fetchSSCCCntrctRegionSUBSql;

	/** The fetch SSCC cntrct region SUBCPB clause sql. */
	@Value("${SSCCRefDaoImpl.fetchSSCCCntrctRegionSUBCPBClause}")
	private String fetchSSCCCntrctRegionSUBCPBClauseSql;

	/** The fetch SSCC cntrct region SUBREG clause sql. */
	@Value("${SSCCRefDaoImpl.fetchSSCCCntrctRegionSUBREGClause}")
	private String fetchSSCCCntrctRegionSUBREGClauseSql;

	/** The fetch SSCC cntrct region SUB clause sql. */
	@Value("${SSCCRefDaoImpl.fetchSSCCCntrctRegionSUBClause}")
	private String fetchSSCCCntrctRegionSUBClauseSql;

	/** The fetch SSCC resource info sql. */
	@Value("${SSCCRefDaoImpl.fetchSSCCResourceInfo}")
	private String fetchSSCCResourceInfoSql;

	/** The is user SSCC external sql. */
	@Value("${SSCCRefDaoImpl.isUserSSCCExternal}")
	private String isUserSSCCExternalSql;

	/** The fetch referrals for case mobile sql. */
	@Value("${SSCCRefDaoImpl.fetchReferralsForCaseMobile}")
	private String fetchReferralsForCaseMobileSql;

	/** The fetch referrals for case sql. */
	@Value("${SSCCRefDaoImpl.fetchReferralsForCase}")
	private String fetchReferralsForCaseSql;

	/** The fetch SSCC not rescind ref for case sql. */
	@Value("${SSCCRefDaoImpl.fetchSSCCNotRescindRefForCase}")
	private String fetchSSCCNotRescindRefForCaseSql;

	/** The fetch SSCC not rescind ref for case status clause sql. */
	@Value("${SSCCRefDaoImpl.fetchSSCCNotRescindRefForCaseStatusClause}")
	private String fetchSSCCNotRescindRefForCaseStatusClauseSql;

	/** The fetch SSCC not rescind ref for case plcmt ref clause sql. */
	@Value("${SSCCRefDaoImpl.fetchSSCCNotRescindRefForCasePlcmtRefClause}")
	private String fetchSSCCNotRescindRefForCasePlcmtRefClauseSql;

	/** The fetch SSCC not rescind ref for case family ref clause sql. */
	@Value("${SSCCRefDaoImpl.fetchSSCCNotRescindRefForCaseFamilyRefClause}")
	private String fetchSSCCNotRescindRefForCaseFamilyRefClauseSql;

	/** The fetch SSCC referrals for stage plcmt ref clause sql. */
	@Value("${SSCCRefDaoImpl.fetchSSCCReferralsForStagePlcmtRefClause}")
	private String fetchSSCCReferralsForStagePlcmtRefClauseSql;

	/** The fetch SSCC referrals for stage sql. */
	@Value("${SSCCRefDaoImpl.fetchSSCCReferralsForStage}")
	private String fetchSSCCReferralsForStageSql;

	/** The fetch SSCC referrals for stage status clause sql. */
	@Value("${SSCCRefDaoImpl.fetchSSCCReferralsForStageStatusClause}")
	private String fetchSSCCReferralsForStageStatusClauseSql;

	/** The fetch SSCC referrals for stage family ref clause sql. */
	@Value("${SSCCRefDaoImpl.fetchSSCCReferralsForStageFamilyRefClause}")
	private String fetchSSCCReferralsForStageFamilyRefClauseSql;

	/** The delete new SSCC referral sql. */
	@Value("${SSCCRefDaoImpl.deleteNewSSCCReferral}")
	private String deleteNewSSCCReferralSql;

	/** The has active case SSCC referral sql. */
	@Value("${SSCCRefDaoImpl.hasActiveCaseSSCCReferralSql}")
	private String hasActiveCaseSSCCReferralSql;

	/** The fetch active SSCC ref for stage. */
	@Value("${SSCCRefDaoImpl.fetchActiveSSCCRefForStage}")
	private String fetchActiveSSCCRefForStage;

	/** The user has SSCC catchment access. */
	@Value("${SSCCRefDaoImpl.userHasSSCCCatchmentAccess}")
	private String userHasSSCCCatchmentAccess;

	/** The fetch referral by PK. */
	@Value("${SSCCRefDaoImpl.fetchReferralByPK}")
	private String fetchReferralByPK;

	/** The fetch DFPS staff assignedto stage. */
	@Value("${SSCCRefDaoImpl.fetchDFPSStaffAssignedtoStage}")
	private String fetchDFPSStaffAssignedtoStage;

	/** The fetch SSCC secondary for stage. */
	@Value("${SSCCRefDaoImpl.fetchSSCCSecondaryForStage}")
	private String fetchSSCCSecondaryForStage;

	/** The get SSCC staff in catchment. */
	@Value("${SSCCRefDaoImpl.getSSCCStaffInCatchment}")
	private String getSSCCStaffInCatchment;

	/** The fetch legal status for ref. */
	@Value("${SSCCRefDaoImpl.fetchLegalStatusForRef}")
	private String fetchLegalStatusForRef;

	/** The fetch case information for PC. */
	@Value("${SSCCRefDaoImpl.fetchCaseInformationForPC}")
	private String fetchCaseInformationForPC;

	/** The has active SSCC child plan content. */
	@Value("${SSCCRefDaoImpl.hasActiveSSCCChildPlanContent}")
	private String hasActiveSSCCChildPlanContent;

	/** The fetch aprv svc auth list sql. */
	@Value("${SSCCRefDaoImpl.fetchAprvSvcAuthList}")
	private String fetchAprvSvcAuthListSql;
	
	@Value("${SSCCRefDaoImpl.fetchAprvSvcAuthListWhere}")
	private String fetchAprvSvcAuthListWhereSql;
	
	@Value("${SSCCRefDaoImpl.fetchActRefForPlcmtRef}")
	private String fetchActRefForPlcmtRefSql;

  @Value("${SSCCRefDaoImpl.excludePCSservices}")
  private String excludePCSservices;

	@Value("${SSCCRefDaoImpl.fetchActRefForFamilyRef}")
	private String fetchActRefForFamilyRefSql;

	/** The is plcmt dates within ref range sql. */
	@Value("${SSCCRefDaoImpl.isPlcmtDatesWithinRefRangeSql}")
	private String isPlcmtDatesWithinRefRangeSql;

	/** The has active svcfor ref person sql. */
	@Value("${SSCCRefDaoImpl.hasActiveSvcforRefPerson}")
	private String hasActiveSvcforRefPersonSql;

	/** The update SSCC child plan topic status sql. */
	@Value("${SSCCRefDaoImpl.updateSSCCChildPlanTopicStatus}")
	private String updateSSCCChildPlanTopicStatusSql;

	/** The fetch SSCC referral family person list sql. */
	@Value("${SSCCRefDaoImpl.fetchSSCCReferralFamilyPersonList}")
	private String fetchSSCCReferralFamilyPersonListSql;

	/** The fetch ref plcmt cnsrvtrshp removal dt sql. */
	@Value("${SSCCRefDaoImpl.fetchRefPlcmtCnsrvtrshpRemovalDt}")
	private String fetchRefPlcmtCnsrvtrshpRemovalDtSql;

	/** The fetch SSCC plcmt end dt list sql. */
	@Value("${SSCCRefDaoImpl.fetchSSCCPlcmtEndDtList}")
	private String fetchSSCCPlcmtEndDtListSql;

	/** The has fam ref max dt discharge in case sql. */
	@Value("${SSCCRefDaoImpl.hasFamRefMaxDtDischargeInCase}")
	private String hasFamRefMaxDtDischargeInCaseSql;

	/** The has plcmt ref max dt discharge in stage sql. */
	@Value("${SSCCRefDaoImpl.hasPlcmtRefMaxDtDischargeInStage}")
	private String hasPlcmtRefMaxDtDischargeInStageSql;

	/** The update plcmt header status hql. */
	@Value("${SSCCRefDaoImpl.updatePlcmtHeaderStatus}")
	private String updatePlcmtHeaderStatusHql;

	/** The update SSCC child plan status hql. */
	@Value("${SSCCRefDaoImpl.updateSSCCChildPlanStatus}")
	private String updateSSCCChildPlanStatusHql;

	/** The update SSCC service auth hql. */
	@Value("${SSCCRefDaoImpl.updateSSCCServiceAuth}")
	private String updateSSCCServiceAuthHql;

	/** The delete SSCC plcmt circumstance sql. */
	@Value("${SSCCRefDaoImpl.deleteSSCCPlcmtCircumstance}")
	private String deleteSSCCPlcmtCircumstanceSql;

	/** The delete SSCC plcmt med cnsntr sql. */
	@Value("${SSCCRefDaoImpl.deleteSSCCPlcmtMedCnsntr}")
	private String deleteSSCCPlcmtMedCnsntrSql;

	/** The delete SSCC plcmt info sql. */
	@Value("${SSCCRefDaoImpl.deleteSSCCPlcmtInfo}")
	private String deleteSSCCPlcmtInfoSql;

	/** The delete SSCC plcmt placed sql. */
	@Value("${SSCCRefDaoImpl.deleteSSCCPlcmtPlaced}")
	private String deleteSSCCPlcmtPlacedSql;

	/** The delete SSCC child plan particip sql. */
	@Value("${SSCCRefDaoImpl.deleteSSCCChildPlanParticip}")
	private String deleteSSCCChildPlanParticipSql;

	/** The delete SSCC child plan topic sql. */
	@Value("${SSCCRefDaoImpl.deleteSSCCChildPlanTopic}")
	private String deleteSSCCChildPlanTopicSql;

	/** The delete SSCC plcmt name sql. */
	@Value("${SSCCRefDaoImpl.deleteSSCCPlcmtName}")
	private String deleteSSCCPlcmtNameSql;

	/** The delete SSCC plcmt narr sql. */
	@Value("${SSCCRefDaoImpl.deleteSSCCPlcmtNarr}")
	private String deleteSSCCPlcmtNarrSql;

	/** The has active SSCC child plan sql. */
	@Value("${SSCCRefDaoImpl.hasActiveSSCCChildPlan}")
	private String hasActiveSSCCChildPlanSql;

	/** The fetch discharged SSCC referralsfor stage sql. */
	@Value("${SSCCRefDaoImpl.fetchDischargedSSCCReferralsforStage}")
	private String fetchDischargedSSCCReferralsforStageSql;

	@Value("${SSCCRefDaoImpl.queryDtEndForPersonInFamilyReferralSql}")
	private String queryDtEndForPersonInFamilyReferralSql;

	/** The session factory. */
	@Autowired
	private SessionFactory sessionFactory;

	/** The message source. */
	@Autowired
	MessageSource messageSource;

	/** The approval dao. */
	@Autowired
	ApprovalDao approvalDao;

	/** The s SCC plcmnt ntwrk dao. */
	@Autowired
	SSCCPlacementNetworkDao sSCCPlcmntNtwrkDao;

	/** The formatting utils. */
	@Autowired
	FormattingUtils formattingUtils;

	/** The todo service. */
	@Autowired
	ToDoService todoService;

	/** The to do dao. */
	@Autowired
	TodoDao toDoDao;

	/** The stage person link dao. */
	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	@Value("${SSCCRefDaoImpl.fetchSSCCRefCount}")
	private String fetchSSCCRefCountSql;

	/**
	 * Instantiates a new SSCC ref dao impl.
	 */
	public SSCCRefDaoImpl() {

	}

	/**
	 * Method Name:fetchSSCCCntrctRegionforStageCounty Method Desctription:
	 * Method fetches the contract region for the stage using stage county.
	 *
	 * @param idStage
	 *            the id stage
	 * @return SSCCParameterDto
	 */
	@Override
	public SSCCParameterDto fetchSSCCCntrctRegionforStageCounty(Long idStage) {
		Query queryCapsCase = sessionFactory.getCurrentSession().createSQLQuery(fetchSSCCCntrctRegionforStageCountySql)
				.addScalar("cdCntrctRegion", StandardBasicTypes.STRING)
				.addScalar("idSSCCCatchment", StandardBasicTypes.LONG)
				.addScalar("cdSSCCCatchment", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(SSCCParameterDto.class));
		queryCapsCase.setParameter("idStage", idStage);
		queryCapsCase.setParameter("date", new Date());
		List<SSCCParameterDto> ssccParameterDtos = queryCapsCase.list();
		// return one record since the query returns duplicate records, as per
		// legacy
		return (!ObjectUtils.isEmpty(ssccParameterDtos) && !ssccParameterDtos.isEmpty()) ? ssccParameterDtos.get(0)
				: null;
	}

	/**
	 * Fetch all the active SSCC Referrals for stage for a the input Referral
	 * type.
	 *
	 * @param idStage
	 *            the id stage
	 * @param strReferralType
	 *            the str referral type
	 * @return the list
	 */
	@Override
	public List<SSCCRefDto> fetchActiveSSCCReferralsForStage(Long idStage, String strReferralType) {
		String queryStringSSCC = ServiceConstants.EMPTY_STRING;
		queryStringSSCC = fetchActiveSSCCReferralsForStageSql;
		if (!TypeConvUtil.isNullOrEmpty(strReferralType)) {
			if (strReferralType.equals(ServiceConstants.PLACEMENT_REFERAAL)) {
				queryStringSSCC = queryStringSSCC + fetchActiveSSCCReferralsForStagePlcmtClauseSql;
			} else if (strReferralType.equals(ServiceConstants.FAMILY_REFERAAL)) {
				queryStringSSCC = queryStringSSCC + fetchActiveSSCCReferralsForStageFamilyClauseSql;
			}
		}
		Query queryCapsCase = sessionFactory.getCurrentSession().createSQLQuery(queryStringSSCC)
				.addScalar("idSSCCReferral", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdSSCCRefType", StandardBasicTypes.STRING)
				.addScalar("cdSSCCRefSubtype", StandardBasicTypes.STRING)
				.addScalar("cdRefStatus", StandardBasicTypes.STRING)
				.addScalar("cdRefSubStatus", StandardBasicTypes.STRING)
				.addScalar("cdCntrctRegion", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idSSCCResource", StandardBasicTypes.LONG)
				.addScalar("dtRecorded", StandardBasicTypes.TIMESTAMP)
				.addScalar("indPriorComm", StandardBasicTypes.STRING)
				.addScalar("dtRecordedSscc", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtRecordedDfps", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtExpectedPlcmt", StandardBasicTypes.TIMESTAMP)
				.addScalar("indLinkedPlcmtData", StandardBasicTypes.STRING)
				.addScalar("indLinkedSvcAuthData", StandardBasicTypes.STRING)
				.addScalar("cdRescindReason", StandardBasicTypes.STRING)
				.addScalar("txtRescindComment", StandardBasicTypes.STRING)
				.addScalar("cdDischargeReason", StandardBasicTypes.STRING)
				.addScalar("dtDischargePlanned", StandardBasicTypes.TIMESTAMP)
				.addScalar("indDischargeReadyReview", StandardBasicTypes.STRING)
				.addScalar("dtDischargeActual", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtLinkedSvcAuthData", StandardBasicTypes.TIMESTAMP)
				.addScalar("indRefAcknowledge", StandardBasicTypes.STRING)
				.addScalar("idSSCCCatchment", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(SSCCRefDto.class));
		queryCapsCase.setParameter("idStage", idStage);
		queryCapsCase.setParameter("cdStatus", ServiceConstants.SSCC_STATUS_40);

		if (!TypeConvUtil.isNullOrEmpty(strReferralType)) {
			if (strReferralType.equals(ServiceConstants.PLACEMENT_REFERAAL)) {
				queryCapsCase.setParameterList("types", ServiceConstants.SSCC_REF_TYPES);
			} else if (strReferralType.equals(ServiceConstants.FAMILY_REFERAAL)) {
				queryCapsCase.setParameter("type", ServiceConstants.SSCC_REF_TYPE_30);
			}
		}
		return queryCapsCase.list();
	}

	/**
	 * Fetch SSCC Contract Region for the given stage where region is fetched
	 * based on the Primary Child's Legal County.
	 *
	 * @param idCase
	 *            the id case
	 * @param idPrimaryChild
	 *            the id primary child
	 * @param cdStageType
	 *            the cd stage type
	 * @return SSCCParameterDto
	 */
	@Override
	public SSCCParameterDto fetchSSCCCntrctRegionSUB(Long idCase, Long idPrimaryChild, String cdStageType) {
		String queryStringCase = fetchSSCCCntrctRegionSUBSql;
		Date date = new Date();

		if (!TypeConvUtil.isNullOrEmpty(cdStageType)) {
			if (cdStageType.equals(CodesConstant.CSTGTYPE_CPB)) {
				queryStringCase = queryStringCase + fetchSSCCCntrctRegionSUBCPBClauseSql;
			} else if (cdStageType.equals(ServiceConstants.SUB_REG)) {
				queryStringCase = queryStringCase + fetchSSCCCntrctRegionSUBREGClauseSql;
			}
		}
		queryStringCase = queryStringCase + fetchSSCCCntrctRegionSUBClauseSql;
		Query queryCapsCase = sessionFactory.getCurrentSession().createSQLQuery(queryStringCase)
				.addScalar("cdCntrctRegion", StandardBasicTypes.STRING)
				.addScalar("idSSCCCatchment", StandardBasicTypes.LONG)
				.addScalar("cdSSCCCatchment", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(SSCCParameterDto.class));
		queryCapsCase.setParameter("idCase", idCase);
		queryCapsCase.setParameter("idPerson", idPrimaryChild);
		queryCapsCase.setParameter("date", date);

		if (!ObjectUtils.isEmpty(cdStageType)) {
			if (cdStageType.equals(CodesConstant.CSTGTYPE_CPB)) {
				queryCapsCase.setParameter("legalStatus", ServiceConstants.LEGAL_STATUS_CLAUSE_FOR_CPB);
			} else if (cdStageType.equals(ServiceConstants.SUB_REG)) {
				queryCapsCase.setParameterList("legalStatusList", ServiceConstants.LEGAL_STATUS_CLAUSE_FOR_REG);
			}
		}
		return (SSCCParameterDto) queryCapsCase.setMaxResults(1).uniqueResult();
	}

	/**
	 * Method fetches the SSCC Resource information for the input SSCC Contract
	 * Region.
	 *
	 * @param cdSSCCCntrctRegion
	 *            the cd SSCC cntrct region
	 * @param idSSCCCatchment
	 *            the id SSCC catchment
	 * @return SSCCResourceDto
	 */

	@Override
	public SSCCResourceDto fetchSSCCResourceInfo(String cdSSCCCntrctRegion, Long idSSCCCatchment) {
		SSCCResourceDto sSCCResourceDto = new SSCCResourceDto();
		List<SSCCResourceDto> sSCCResourceDtos = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(fetchSSCCResourceInfoSql).setParameter("cdRegion", cdSSCCCntrctRegion)
				.setParameter("idSSCC", idSSCCCatchment))
						.addScalar("idSSCCResource", StandardBasicTypes.LONG)
						.addScalar("nmSSCCResource", StandardBasicTypes.STRING)
						.addScalar("cdSSCCCntrctRegion", StandardBasicTypes.STRING)
						.addScalar("idSSCCContract", StandardBasicTypes.LONG)
						.addScalar("dtFamilyServiceReferral", StandardBasicTypes.TIMESTAMP)
						.addScalar("idResourceFamilySA", StandardBasicTypes.LONG)
						.addScalar("dtStart", StandardBasicTypes.TIMESTAMP)
						.addScalar("dtEnd", StandardBasicTypes.TIMESTAMP)
						.addScalar("idSSCCCatchment", StandardBasicTypes.LONG)
						.addScalar("cdSSCCCatchment", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(SSCCResourceDto.class)).list();
		// as per Legacy, iterate the list and return the last instance values.
		if (!CollectionUtils.isEmpty(sSCCResourceDtos)) {
			for (SSCCResourceDto resourceDto : sSCCResourceDtos) {
				sSCCResourceDto = resourceDto;
			}
		}
		return sSCCResourceDto;
	}

	/**
	 * Method returns true if the logged in user is SSCC External Staff.
	 * UNIT.IND_EXTERNAL = Y CD_UNIT_REGION = cdSSCCCntrctRegion
	 * UNIT.CD_UNIT_SPECIALIZATION = SSC EMPLOYEE. CD_EXTERNAL_TYPE = SSC
	 *
	 * @param idPerson
	 *            the id person
	 * @param cdSSCCCatchment
	 *            the cd SSCC catchment
	 * @return boolean
	 */

	@Override
	public boolean isUserSSCCExternal(Long idPerson, String cdSSCCCatchment) {
		boolean isUserSSCCExternal = false;
		SQLQuery queryCapsCase = sessionFactory.getCurrentSession().createSQLQuery(isUserSSCCExternalSql);
		queryCapsCase.setParameter("idPerson", idPerson);
		queryCapsCase.setParameter("indExternal", ServiceConstants.STRING_IND_Y);
		queryCapsCase.setParameter("cdSpecialization", ServiceConstants.UNIT_SPECIALIZATION_SSC);
		queryCapsCase.setParameter("cdType", ServiceConstants.EXTERNAL_TYPE_ESSC);
		queryCapsCase.setParameter("cdSSCC", cdSSCCCatchment);
		List<Long> personIds = queryCapsCase.list();
		if (!ObjectUtils.isEmpty(personIds)) {
			if (personIds.size() > ServiceConstants.Zero) {
				isUserSSCCExternal = true;
			}
		}
		return isUserSSCCExternal;
	}

	/**
	 * Method fetches a Referral information for all the referrals in the case,
	 * creates a list of SSCCReferralValueBean objects, sets the list into the
	 * SSCCRefListValueBean and returns it.
	 *
	 * @param sSCCRefListDto
	 *            the s SCC ref list dto
	 * @return SSCCRefListDto
	 */

	@Override
	public SSCCRefListDto fetchReferralsForCase(SSCCRefListDto sSCCRefListDto) {
		String queryStringCase = ServiceConstants.EMPTY_STRING;
		if (ServiceConstants.MOBILE_IMPACT) {
			queryStringCase = fetchReferralsForCaseMobileSql;
		} else {
			queryStringCase = fetchReferralsForCaseSql;
		}
		if (!TypeConvUtil.isNullOrEmpty(sSCCRefListDto) && !TypeConvUtil.isNullOrEmpty(sSCCRefListDto.getIdCase())) {
			Query queryCapsCase = sessionFactory.getCurrentSession().createSQLQuery(queryStringCase)
					.addScalar("idSSCCReferral", StandardBasicTypes.LONG)
					.addScalar("dtRecorded", StandardBasicTypes.TIMESTAMP)
					.addScalar("dtDischargeActual", StandardBasicTypes.TIMESTAMP)
					.addScalar("idStage", StandardBasicTypes.LONG)
					.addScalar("nmStage", StandardBasicTypes.STRING)
					.addScalar("cdSSCCRefType", StandardBasicTypes.STRING)
					.addScalar("cdSSCCRefSubtype", StandardBasicTypes.STRING)
					.addScalar("cdCntrctRegion", StandardBasicTypes.STRING)
					.addScalar("nmCreatedPerson", StandardBasicTypes.STRING)
					.addScalar("cdRescindReason", StandardBasicTypes.STRING)
					.addScalar("cdRefStatus", StandardBasicTypes.STRING)
					.setResultTransformer(Transformers.aliasToBean(SSCCRefDto.class));
			queryCapsCase.setParameter("idCase", sSCCRefListDto.getIdCase());
			queryCapsCase.setParameter("codeType", ServiceConstants.CODE_TYPE_CSSCCREF);
			sSCCRefListDto.setSsccReferralForCaseList(queryCapsCase.list());
		}
		return sSCCRefListDto;
	}

	/**
	 * Method fetches a list of all the referrals in the case that have not been
	 * rescinded.
	 *
	 * @param idCase
	 *            the id case
	 * @param cdStatus
	 *            the cd status
	 * @param strReferralType
	 *            the str referral type
	 * @return List
	 */
	@Override
	public List<SSCCRefDto> fetchSSCCNotRescindRefForCase(Long idCase, String cdStatus, String strReferralType) {
		String queryStringSSCC = ServiceConstants.EMPTY_STRING;
		queryStringSSCC = fetchSSCCNotRescindRefForCaseSql;
		if (!TypeConvUtil.isNullOrEmpty(cdStatus)) {
			queryStringSSCC = queryStringSSCC + fetchSSCCNotRescindRefForCaseStatusClauseSql;
		}
		if (!TypeConvUtil.isNullOrEmpty(strReferralType)) {
			if (strReferralType.equals(ServiceConstants.PLACEMENT_REFERAAL)) {
				queryStringSSCC = queryStringSSCC + fetchSSCCNotRescindRefForCasePlcmtRefClauseSql;
			} else if (strReferralType.equals(ServiceConstants.FAMILY_REFERAAL)) {
				queryStringSSCC = queryStringSSCC + fetchSSCCNotRescindRefForCaseFamilyRefClauseSql;
			}

		}
		Query queryCapsCase = sessionFactory.getCurrentSession().createSQLQuery(queryStringSSCC)
				.addScalar("idSSCCReferral", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdSSCCRefType", StandardBasicTypes.STRING)
				.addScalar("cdSSCCRefSubtype", StandardBasicTypes.STRING)
				.addScalar("cdRefStatus", StandardBasicTypes.STRING)
				.addScalar("cdRefSubStatus", StandardBasicTypes.STRING)
				.addScalar("cdCntrctRegion", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idSSCCResource", StandardBasicTypes.LONG)
				.addScalar("dtRecorded", StandardBasicTypes.TIMESTAMP)
				.addScalar("indPriorComm", StandardBasicTypes.STRING)
				.addScalar("dtRecordedSscc", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtRecordedDfps", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtExpectedPlcmt", StandardBasicTypes.TIMESTAMP)
				.addScalar("indLinkedPlcmtData", StandardBasicTypes.STRING)
				.addScalar("indLinkedSvcAuthData", StandardBasicTypes.STRING)
				.addScalar("cdRescindReason", StandardBasicTypes.STRING)
				.addScalar("txtRescindComment", StandardBasicTypes.STRING)
				.addScalar("cdDischargeReason", StandardBasicTypes.STRING)
				.addScalar("dtDischargePlanned", StandardBasicTypes.TIMESTAMP)
				.addScalar("indDischargeReadyReview", StandardBasicTypes.STRING)
				.addScalar("dtDischargeActual", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtLinkedSvcAuthData", StandardBasicTypes.TIMESTAMP)
				.addScalar("indRefAcknowledge", StandardBasicTypes.STRING)
				.addScalar("idSSCCCatchment", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(SSCCRefDto.class));
		queryCapsCase.setParameter("idCase", idCase);
		queryCapsCase.setParameterList("statusList", ServiceConstants.SSCC_STATUS_List);
		if (!TypeConvUtil.isNullOrEmpty(cdStatus)) {
			queryCapsCase.setParameter("cdStatus", cdStatus);
		}
		if (!TypeConvUtil.isNullOrEmpty(strReferralType)) {
			if (strReferralType.equals(ServiceConstants.PLACEMENT_REFERAAL)) {
				queryCapsCase.setParameterList("refTypes", ServiceConstants.SSCC_REF_TYPES);
			} else if (strReferralType.equals(ServiceConstants.FAMILY_REFERAAL)) {
				queryCapsCase.setParameter("refType", ServiceConstants.SSCC_REF_TYPE_30);
			}

		}
		return queryCapsCase.list();
	}

	/**
	 * This method will fetch the referrals for a stage with a specific status
	 * (for eg. Active) and a specific referral type.
	 *
	 * @param idStage
	 *            the id stage
	 * @param cdStatus
	 *            the cd status
	 * @param strReferralType
	 *            the str referral type
	 * @return List
	 */
	@Override
	public List<SSCCRefDto> fetchSSCCReferralsForStage(Long idStage, String cdStatus, String strReferralType) {
		String queryStringSSCC = ServiceConstants.EMPTY_STRING;
		queryStringSSCC = fetchSSCCReferralsForStageSql;
		if (!TypeConvUtil.isNullOrEmpty(cdStatus)) {
			queryStringSSCC = queryStringSSCC + fetchSSCCReferralsForStageStatusClauseSql;
		}
		if (!TypeConvUtil.isNullOrEmpty(strReferralType)) {
			if (strReferralType.equals(ServiceConstants.PLACEMENT_REFERAAL)) {
				queryStringSSCC = queryStringSSCC + fetchSSCCReferralsForStagePlcmtRefClauseSql;
			} else if (strReferralType.equals(ServiceConstants.FAMILY_REFERAAL)) {
				queryStringSSCC = queryStringSSCC + fetchSSCCReferralsForStageFamilyRefClauseSql;
			}

		}
		Query queryCapsCase = sessionFactory.getCurrentSession().createSQLQuery(queryStringSSCC)
				.addScalar("idSSCCReferral", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdSSCCRefType", StandardBasicTypes.STRING)
				.addScalar("cdSSCCRefSubtype", StandardBasicTypes.STRING)
				.addScalar("cdRefStatus", StandardBasicTypes.STRING)
				.addScalar("cdRefSubStatus", StandardBasicTypes.STRING)
				.addScalar("cdCntrctRegion", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idSSCCResource", StandardBasicTypes.LONG)
				.addScalar("dtRecorded", StandardBasicTypes.TIMESTAMP)
				.addScalar("indPriorComm", StandardBasicTypes.STRING)
				.addScalar("dtRecordedSscc", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtRecordedDfps", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtExpectedPlcmt", StandardBasicTypes.TIMESTAMP)
				.addScalar("indLinkedPlcmtData", StandardBasicTypes.STRING)
				.addScalar("indLinkedSvcAuthData", StandardBasicTypes.STRING)
				.addScalar("cdRescindReason", StandardBasicTypes.STRING)
				.addScalar("txtRescindComment", StandardBasicTypes.STRING)
				.addScalar("cdDischargeReason", StandardBasicTypes.STRING)
				.addScalar("dtDischargePlanned", StandardBasicTypes.TIMESTAMP)
				.addScalar("indDischargeReadyReview", StandardBasicTypes.STRING)
				.addScalar("dtDischargeActual", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtLinkedSvcAuthData", StandardBasicTypes.TIMESTAMP)
				.addScalar("indRefAcknowledge", StandardBasicTypes.STRING)
				.addScalar("idSSCCCatchment", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(SSCCRefDto.class));
		queryCapsCase.setParameter("idStage", idStage);
		if (!TypeConvUtil.isNullOrEmpty(cdStatus)) {
			queryCapsCase.setParameter("cdStatus", cdStatus);
		}
		if (!TypeConvUtil.isNullOrEmpty(strReferralType)) {
			if (strReferralType.equals(ServiceConstants.PLACEMENT_REFERAAL)) {
				queryCapsCase.setParameterList("refTypes", ServiceConstants.SSCC_REF_TYPES);
			} else if (strReferralType.equals(ServiceConstants.FAMILY_REFERAAL)) {
				queryCapsCase.setParameter("refType", ServiceConstants.SSCC_REF_TYPE_30);
			}

		}
		return queryCapsCase.list();
	}

	/**
	 * Method deletes all records in SSCC_REFERRAL table that have a 'NEW'
	 * status.
	 *
	 * @param idCase
	 *            the id case
	 */
	@Override
	public void deleteNewSSCCReferral(Long idCase) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(deleteNewSSCCReferralSql)
				.setParameter("idCase", idCase).setParameter("cdStatus", ServiceConstants.SSCC_STATUS_10);
		query.executeUpdate();
	}

	/**
	 * Check if Case has an active SSCC Referral.
	 *
	 * @param caseId
	 *            the case id
	 * @return HasSSCCReferralRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public String hasActiveSSCCReferral(Long caseId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(hasActiveCaseSSCCReferralSql)
				.setParameter("idCase", caseId).setParameter("cdStatus", CodesConstant.CSSCCSTA_40);
		return (query.list().size() > 0) ? ServiceConstants.STRING_IND_Y : ServiceConstants.STRING_IND_N;
	}

	/**
	 * Method Name: primaryChildHasLegalStatusForCase Method Description:Method
	 * returns true if the Primary Child has at least one Legal Status record
	 * for the case.
	 *
	 * @param idCase
	 *            the id case
	 * @param idPrimaryChild
	 *            the id primary child
	 * @return boolean
	 */
	public boolean primaryChildHasLegalStatusForCase(Long idCase, Long idPrimaryChild) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LegalStatus.class);
		criteria.add(Restrictions.eq("idCase", idCase));
		criteria.add(Restrictions.eq("person.idPerson", idPrimaryChild));
		List<LegalStatus> legalStatus = criteria.list();
		return legalStatus.size() > 0 ? true : false;

	}

	/**
	 * Method Name: fetchActiveSSCCRefForStage Method Description : Fetch all
	 * the active SSCC Referrals for stage for a the input Referral type
	 * 
	 * @param idStage
	 * @param cdRefType
	 * @return List
	 */
	@Override
	public List<SSCCRefDto> fetchActiveSSCCRefForStage(Long idStage, String strReferralType) {
		StringBuilder sql = new StringBuilder(fetchActiveSSCCRefForStage);
		if (!TypeConvUtil.isNullOrEmpty(strReferralType)
				&& ServiceConstants.PLACEMENT_REFERAAL.equals(strReferralType)) {
			sql.append(ServiceConstants.SQL_FETCH_REFERRAL_PLCMT_REF_CLAUSE);
		} else if (!TypeConvUtil.isNullOrEmpty(strReferralType)
				&& ServiceConstants.FAMILY_REFERAAL.equals(strReferralType))
			sql.append(ServiceConstants.SQL_FETCH_REFERRAL_FAMILY_REF_CLAUSE);
		Query query1 = sessionFactory.getCurrentSession().createSQLQuery(sql.toString())
				.addScalar("idSSCCReferral", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdSSCCRefType", StandardBasicTypes.STRING)
				.addScalar("cdSSCCRefSubtype", StandardBasicTypes.STRING)
				.addScalar("cdStatus", StandardBasicTypes.STRING).addScalar("cdSubStatus", StandardBasicTypes.STRING)
				.addScalar("cdCntrctRegion", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idRsrcSSCC", StandardBasicTypes.LONG)
				.addScalar("dtRecorded", StandardBasicTypes.TIMESTAMP)
				.addScalar("indPriorComm", StandardBasicTypes.STRING)
				.addScalar("dtRecordedSscc", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtRecordedDfps", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtExpectedPlcmt", StandardBasicTypes.TIMESTAMP)
				.addScalar("indLinkedPlcmtData", StandardBasicTypes.STRING)
				.addScalar("indLinkedSvcAuthData", StandardBasicTypes.STRING)
				.addScalar("cdRescindReason", StandardBasicTypes.STRING)
				.addScalar("txtRescindComment", StandardBasicTypes.STRING)
				.addScalar("cdDischargeReason", StandardBasicTypes.STRING)
				.addScalar("dtDischargePlanned", StandardBasicTypes.TIMESTAMP)
				.addScalar("indDischargeReadyReview", StandardBasicTypes.STRING)
				.addScalar("dtDischargeActual", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtLinkedSvcAuthData", StandardBasicTypes.TIMESTAMP)
				.addScalar("indRefAcknowledge", StandardBasicTypes.STRING)
				.addScalar("idSSCCCatchment", StandardBasicTypes.LONG).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(SSCCRefDto.class));
		return query1.list();
	}

	/**
	 * Method Name: userHasSSCCCatchmentAccess Method Description:Method returns
	 * true is user is In or Out assigned to a unit whose region matches the
	 * input region.
	 *
	 * @param idPerson
	 *            the id person
	 * @param cdSSCCCatchment
	 *            the cd SSCC catchment
	 * @return boolean
	 */
	public boolean userHasSSCCCatchmentAccess(Long idPerson, String cdSSCCCatchment) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(userHasSSCCCatchmentAccess);
		query.setParameter("idPerson", idPerson);
		query.setParameter("cdCatchment", cdSSCCCatchment);
		List<?> list = query.list();
		return (!ObjectUtils.isEmpty(list) && list.size() > 0) ? true : false;
	}

	/**
	 * Method Name: fetchReferralByPK Method Description:Fetches the Referral
	 * record for the given Primary Key.
	 *
	 * @param idSsccReferral
	 *            the id sscc referral
	 * @return SSCCRefDto
	 */
	@Override
	public SSCCRefDto fetchReferralByPK(Long idSsccReferral) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchReferralByPK)
				.addScalar("idSSCCReferral", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdSSCCRefType", StandardBasicTypes.STRING)
				.addScalar("cdSSCCRefSubtype", StandardBasicTypes.STRING)
				.addScalar("cdRefStatus", StandardBasicTypes.STRING)
				.addScalar("cdRefSubStatus", StandardBasicTypes.STRING)
				.addScalar("cdCntrctRegion", StandardBasicTypes.STRING).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idSSCCResource", StandardBasicTypes.LONG)
				.addScalar("dtRecorded", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtRecordedDfps", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtRecordedSscc", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtExpectedPlcmt", StandardBasicTypes.TIMESTAMP)
				.addScalar("indPriorComm", StandardBasicTypes.STRING)
				.addScalar("indRefAcknowledge", StandardBasicTypes.STRING)
				.addScalar("indLinkedPlcmtData", StandardBasicTypes.STRING)
				.addScalar("indLinkedSvcAuthData", StandardBasicTypes.STRING)
				.addScalar("indDischargeReadyReview", StandardBasicTypes.STRING)
				.addScalar("cdRescindReason", StandardBasicTypes.STRING)
				.addScalar("cdDischargeReason", StandardBasicTypes.STRING)
				.addScalar("txtRescindComment", StandardBasicTypes.STRING)
				.addScalar("dtDischargeActual", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtDischargePlanned", StandardBasicTypes.TIMESTAMP)
				.addScalar("nmSSCCResource", StandardBasicTypes.STRING).addScalar("idRsrcSSCC", StandardBasicTypes.LONG)
				.addScalar("nmStage", StandardBasicTypes.STRING).addScalar("nmCreatedPerson", StandardBasicTypes.STRING)
				.addScalar("nmLastUpdatePerson", StandardBasicTypes.STRING)
				.addScalar("idSSCCCatchment", StandardBasicTypes.LONG)
				.addScalar("cdSSCCCatchment", StandardBasicTypes.STRING).setParameter("idSSCCReferral", idSsccReferral)
				.setResultTransformer(Transformers.aliasToBean(SSCCRefDto.class)));
		return (SSCCRefDto) sQLQuery1.list().get(ServiceConstants.Zero);
	}

	/**
	 * Method Name: fetchDFPSStaffAssignedtoStage Method Description:Fetch
	 * Primary and Secondary staff assigned to stage.
	 *
	 * @param idStage
	 *            the id stage
	 * @return HashMap<Long ,AssignmentGroupDto>
	 */
	@Override
	public HashMap<Long, AssignmentGroupDto> fetchDFPSStaffAssignedtoStage(Long idStage) {

		HashMap<Long, AssignmentGroupDto> resultMap = new HashMap<>();
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(fetchDFPSStaffAssignedtoStage).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("cdStage", StandardBasicTypes.STRING).addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("cdStageType", StandardBasicTypes.STRING).addScalar("cdStageCnty", StandardBasicTypes.STRING)
				.addScalar("nmStage", StandardBasicTypes.STRING).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("idStagePersonLink", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(StageValueBeanDto.class)));
		List<StageValueBeanDto> stageValueBeanDtoList = sQLQuery1.list();
		for (StageValueBeanDto stageValueBeanDto : stageValueBeanDtoList) {
			AssignmentGroupDto assignmentGroupDto = new AssignmentGroupDto();
			assignmentGroupDto.setIdStage(stageValueBeanDto.getIdStage());
			assignmentGroupDto.setIdCase(stageValueBeanDto.getIdCase());
			assignmentGroupDto.setIdPerson(stageValueBeanDto.getIdPerson());
			assignmentGroupDto.setIdStagePerson(stageValueBeanDto.getIdStagePersonLink());
			assignmentGroupDto.setCdStage(stageValueBeanDto.getCdStage());
			assignmentGroupDto.setCdStageProgram(stageValueBeanDto.getCdStageProgram());
			assignmentGroupDto.setCdStagePersRole(stageValueBeanDto.getCdStagePersRole());
			assignmentGroupDto.setCdStageType(stageValueBeanDto.getCdStageType());
			assignmentGroupDto.setNmPersonFull(stageValueBeanDto.getNmPersonFull());
			assignmentGroupDto.setTsLastUpdate(stageValueBeanDto.getDtLastUpdate());
			resultMap.put(stageValueBeanDto.getIdPerson(), assignmentGroupDto);
		}
		return resultMap;
	}

	/**
	 * Method Name: fetchSSCCSecondaryForStage Method Description:Method fetches
	 * the SSCC Secondary Staff assigned to stage.
	 *
	 * @param idStage
	 *            the id stage
	 * @param cdSSCCCatchment
	 *            the cd SSCC catchment
	 * @return List<Long>
	 */
	@Override
	public List<Long> fetchSSCCSecondaryForStage(Long idStage, String cdSSCCCatchment) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchSSCCSecondaryForStage)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setParameter("cdSSCCCatchment", cdSSCCCatchment)
				.setResultTransformer(Transformers.aliasToBean(StageValueBeanDto.class)));
		List<StageValueBeanDto> stageValueBeanDtoList = sQLQuery1.list();
		return stageValueBeanDtoList.stream().map(o -> o.getIdPerson()).collect(Collectors.toList());
	}

	/**
	 * Method Name: getSSCCStaffInCatchment Method Description:Method returns an
	 * arraylist of all the staff in a given catchment.
	 *
	 * @param cdSSCCCatchment
	 *            the cd SSCC catchment
	 * @return List<Long>
	 */
	@Override
	public List<Long> getSSCCStaffInCatchment(String cdSSCCCatchment) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSSCCStaffInCatchment)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING).setParameter("cdSSCCCatchment", cdSSCCCatchment)
				.setResultTransformer(Transformers.aliasToBean(StageValueBeanDto.class)));

		List<StageValueBeanDto> litageValueBeanDtoS = (List<StageValueBeanDto>) sQLQuery1.list();
		return litageValueBeanDtoS.stream().map(o -> o.getIdPerson()).collect(Collectors.toList());
	}

	/**
	 * Method Name: fetchCaseProgram Method Description:Method returns the case
	 * program for the given case.
	 *
	 * @param idCase
	 *            the id case
	 * @return String
	 */

	@Override
	public String fetchCaseProgram(Long idCase) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CapsCase.class);
		criteria.add(Restrictions.eq("idCase", idCase));
		CapsCase capsCase = (CapsCase) criteria.uniqueResult();
		return (!ObjectUtils.isEmpty(capsCase)) ? capsCase.getCdCaseProgram() : null;
	}

	/**
	 * Method Name: fetchLegalStatusForCase Method Description: Fetches the
	 * Legal Status for the Primary Child in a given case.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @return SSCCRefDto
	 */
	@Override
	public SSCCRefDto fetchLegalStatusForCase(SSCCRefDto ssccRefDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LegalStatus.class);
		criteria.add(Restrictions.eq("idCase", ssccRefDto.getIdCase()));
		criteria.add(Restrictions.eq("person.idPerson", ssccRefDto.getIdPerson()));
		criteria.addOrder(Order.desc("dtLegalStatStatusDt"));
		criteria.setMaxResults(1);
		LegalStatus legalStatus = (LegalStatus) criteria.uniqueResult();
		SSCCRefPlcmtDto ssccRefPlcmtDto = new SSCCRefPlcmtDto();
		if(!ObjectUtils.isEmpty(legalStatus)){
			ssccRefPlcmtDto.setIdLegalStatusEvent(legalStatus.getIdLegalStatEvent());
			ssccRefPlcmtDto.setCdLegalStatusType(legalStatus.getCdLegalStatStatus());
			ssccRefPlcmtDto.setCdLegalCounty(legalStatus.getCdLegalStatCnty());
			ssccRefPlcmtDto.setDtLegalStatusEffective(legalStatus.getDtLegalStatStatusDt());
		}
		ssccRefDto.setSsccRefPlcmtDto(ssccRefPlcmtDto);
		return ssccRefDto;
	}

	/**
	 * Method Name: fetchLegalStatusForRef Method Description: Fetches the Legal
	 * Status information for the Referral Placement Information section from
	 * sscc_referral_event, legal_status and event tables.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @return the SSCC ref dto
	 */
	@Override
	public SSCCRefDto fetchLegalStatusForRef(SSCCRefDto ssccRefDto) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(fetchLegalStatusForRef)
				.addScalar("idLegalStatusEvent", StandardBasicTypes.LONG)
				.addScalar("cdLegalStatusType", StandardBasicTypes.STRING)
				.addScalar("cdLegalCounty", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatusEffective", StandardBasicTypes.TIMESTAMP)
				.setParameter("idSSCCReferral", ssccRefDto.getIdSSCCReferral())
				.setResultTransformer(Transformers.aliasToBean(SSCCRefPlcmtDto.class));
		SSCCRefPlcmtDto ssccRefPlcmtDto = (SSCCRefPlcmtDto) query.uniqueResult();
		ssccRefDto.setSsccRefPlcmtDto(ssccRefPlcmtDto);
		return ssccRefDto;
	}

	/**
	 * Method name: fetchRefPlcmtCnsrvtrshpRemovalDt Method Description: Fetches
	 * the conservator-ship removal date for Placement Referral from
	 * sscc_referral_event, event and cnsrvtrshp_removal tables.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @return the SSCC ref dto
	 */
	@Override
	public SSCCRefDto fetchRefPlcmtCnsrvtrshpRemovalDt(SSCCRefDto ssccRefDto) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(fetchRefPlcmtCnsrvtrshpRemovalDtSql)
				.addScalar("dtCnsrvtrshpRmvl", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCnsrvtrshpRmvlEvent", StandardBasicTypes.LONG)
				.setParameter("idSSCCReferral", ssccRefDto.getIdSSCCReferral())
				.setResultTransformer(Transformers.aliasToBean(SSCCRefPlcmtDto.class));
		SSCCRefPlcmtDto ssccRefPlcmtDto = (SSCCRefPlcmtDto) query.uniqueResult();
		ssccRefDto.setSsccRefPlcmtDto(ssccRefPlcmtDto);
		return ssccRefDto;
	}

	/**
	 * Method Name: fetchCaseInformationForPC Method Description: Method fetches
	 * the list of closed SUB stages from other cases where the person is the
	 * Primary Child of the SUB stage.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @return List<StageDto>
	 */
	@Override
	public List<StageDto> fetchCaseInformationForPC(SSCCRefDto ssccRefDto) {
		SQLQuery sqlquery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchCaseInformationForPC)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("dtStageClose", StandardBasicTypes.TIMESTAMP)
				.setParameter("idCase", ssccRefDto.getIdCase()).setParameter("idPerson", ssccRefDto.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(StageDto.class)));
		return (List<StageDto>) sqlquery.list();
	}

	/**
	 * Method Name: hasActiveSSCCFamilySvcRefInCase Method Description:
	 * hasActiveSSCCFamilySvcRefInCase.
	 *
	 * @param idCase
	 *            the id case
	 * @return boolean
	 */
	@Override
	public boolean hasActiveSSCCFamilySvcRefInCase(Long idCase) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccReferral.class);

		criteria.add(Restrictions.eq("cdSSCCRefType", "30"));
		criteria.add(Restrictions.eq("cdRefStatus", "40"));
		criteria.add(Restrictions.eq("idCase", idCase));

		List<SsccReferral> referrals = criteria.list();
		return (!ObjectUtils.isEmpty(referrals)) ? true : false;
	}

	/**
	 * Method Name: isPlcmtDatesWithinRefRange Method Description: Returns true
	 * if the given placement start and end dates are within the range of at
	 * least one discharged SSCC Referral for stage and resource.
	 *
	 * @param idPlcmtEvent
	 *            the id plcmt event
	 * @param idStage
	 *            the id stage
	 * @return boolean
	 */

	@Override
	public boolean isPlcmtDatesWithinRefRange(Long idPlcmtEvent, Long idStage) {
		Query query1 = sessionFactory.getCurrentSession().createSQLQuery(isPlcmtDatesWithinRefRangeSql)
				.addScalar("idSSCCReferral", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(SSCCRefDto.class));
		query1.setParameter("idPlcmtEvent", idPlcmtEvent);
		query1.setParameter("idStage", idStage);
		List<Long> list = query1.list();
		return (!ObjectUtils.isEmpty(list)) ? true : false;
	}

	/**
	 * Method Name: fetchCnsrvtrshpRemovalData Method Description:
	 * fetchCnsrvtrshpRemovalData.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @return SSCCRefDto
	 */
	@Override
	public SSCCRefDto fetchCnsrvtrshpRemovalData(SSCCRefDto ssccRefDto) {

		SSCCRefPlcmtDto ssccRefPlcmtDto = new SSCCRefPlcmtDto();
		if (ssccRefDto.getSsccRefPlcmtDto() != null) {
			ssccRefPlcmtDto = ssccRefDto.getSsccRefPlcmtDto();
		}
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CnsrvtrshpRemoval.class);
		criteria.add(Restrictions.eq("person.idPerson", ssccRefDto.getIdPerson()));
		criteria.addOrder(Order.asc("dtRemoval"));
		List<CnsrvtrshpRemoval> cnsrvtrshpRemovals = criteria.list();
		for (CnsrvtrshpRemoval cnsrvtrshpRemoval : cnsrvtrshpRemovals) {
			ssccRefPlcmtDto.setDtCnsrvtrshpRmvl(cnsrvtrshpRemoval.getDtRemoval());
			ssccRefPlcmtDto.setIdCnsrvtrshpRmvlEvent(cnsrvtrshpRemoval.getIdRemovalEvent());
		}
		ssccRefDto.setSsccRefPlcmtDto(ssccRefPlcmtDto);
		return ssccRefDto;
	}

	/**
	 * Method Name: saveSSCCTimeline Method Description: saveSSCCTimeline.
	 *
	 * @param timelineDto
	 *            the timeline dto
	 * @return long
	 */
	@Override
	public Long saveSSCCTimeline(SSCCTimelineDto timelineDto) {
		SsccTimeline ssccTimeline = new SsccTimeline();

		ssccTimeline.setDtCreated(new Date());
		ssccTimeline.setIdCreatedPerson(timelineDto.getIdCreatedPerson());
		ssccTimeline.setIdLastUpdatePerson(timelineDto.getIdLastUpdatePerson());
		ssccTimeline.setDtRecorded(new Date());

		ssccTimeline.setDtLastUpdate(new Date());

		SsccReferral ssccReferral = (SsccReferral) sessionFactory.getCurrentSession().load(SsccReferral.class,
				timelineDto.getIdSsccReferral());
		ssccTimeline.setSsccReferral(ssccReferral);
		if (!ObjectUtils.isEmpty(timelineDto.getIdSSCCResource())) {
			ssccTimeline.setIdRsrcSscc(timelineDto.getIdSSCCResource());
		}
		ssccTimeline.setIdReference(timelineDto.getIdReference());
		ssccTimeline.setCdTimelineTableName(timelineDto.getCdTimelineTableName());
		ssccTimeline.setTxtTimelineDesc(timelineDto.getTxtTimelineDesc());
		sessionFactory.getCurrentSession().save(ssccTimeline);
		return ssccTimeline.getIdSsccTimeline();
	}
	
	/**
	 * 
	 *Method Name:	saveSSCCRefFamily
	 *Method Description: this method is used to save the fmily referrals
	 *@param ssccRefFamilyDto
	 *@return
	 */
	@Override
	public Long saveSSCCRefFamily(SSCCRefFamilyDto ssccRefFamilyDto, String idUser){
		SsccReferralFamily ssccReferralFamily = new SsccReferralFamily();
		ssccReferralFamily.setIdLastUpdatePerson(Long.valueOf(idUser));
		ssccReferralFamily.setDtCreated(new Date());
		ssccReferralFamily.setIdCreatedPerson(Long.valueOf(idUser));
		SsccReferral ssccReferral = (SsccReferral) sessionFactory.getCurrentSession().load(SsccReferral.class, ssccRefFamilyDto.getIdSsccReferral());
		ssccReferralFamily.setSsccReferral(ssccReferral);
		ssccReferralFamily.setIdPerson(ssccRefFamilyDto.getIdPerson());
		ssccReferralFamily.setDtStart(ssccRefFamilyDto.getDtStart());
		ssccReferralFamily.setDtEnd(ssccRefFamilyDto.getDtEnd());
		ssccReferralFamily.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().save(ssccReferralFamily);
		return ssccReferralFamily.getIdSsccReferralFamily();
	}
	
	/**
	 * 
	 * Method Name: updateSSCCRefFamily Method Description: This method saves
	 * the end date with new date when user removes the person from the family
	 * referral
	 * 
	 * @param ssccRefFamilyDto
	 * @param idUser
	 * @return
	 */
	@Override
	public Long updateSSCCRefFamily(SSCCRefFamilyDto ssccRefFamilyDto, String idUser) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccReferralFamily.class);
		criteria.add(Restrictions.eq("idPerson", ssccRefFamilyDto.getIdPerson()));
		criteria.add(Restrictions.eq("ssccReferral.idSSCCReferral", ssccRefFamilyDto.getIdSsccReferral()));
		criteria.add(Restrictions.isNull("dtEnd"));
		SsccReferralFamily ssccReferralFamily = (SsccReferralFamily) criteria.uniqueResult();
		ssccReferralFamily.setIdLastUpdatePerson(Long.valueOf(idUser));
		//UC12 changes. Adding this check to populate the end date with new Date only the End Dt is not present.
		if(ObjectUtils.isEmpty(ssccRefFamilyDto.getDtEnd())) {
			ssccReferralFamily.setDtEnd(new Date());
		} else {
			ssccReferralFamily.setDtEnd(ssccRefFamilyDto.getDtEnd());
		}
		ssccReferralFamily.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(ssccReferralFamily);
		return ssccReferralFamily.getIdSsccReferralFamily();
	}

	/**
	 * Method updates an SSCC Referral record in the SSCC_REFERRAL table.
	 *
	 * @param ssccReferralDto
	 *            the sscc referral dto
	 * @return the long
	 * @returnSSCCRefDto
	 */
	@Override
	public Long updateSSCCRefHeader(SSCCRefDto ssccReferralDto) {
		SsccReferral referral;
		if (!ObjectUtils.isEmpty(ssccReferralDto.getIdSSCCReferral())) {
			referral = (SsccReferral) sessionFactory.getCurrentSession().load(SsccReferral.class,
					ssccReferralDto.getIdSSCCReferral());
		} else {
			referral = new SsccReferral();
		}
		ssccReferralDto.setIndPriorComm(!StringUtils.isEmpty(ssccReferralDto.getIndPriorComm())
				? ssccReferralDto.getIndPriorComm() : ServiceConstants.N);
		BeanUtils.copyProperties(ssccReferralDto, referral);
		referral.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(referral);
		return referral.getIdSSCCReferral();
	}

	/**
	 * Method updates an cdStatus in the SSCC_REFERRAL table for the input
	 * idSSCCReferral.
	 *
	 * @param cdStatus
	 *            the cd status
	 * @param idSsccReferral
	 *            the id sscc referral
	 * @return the int
	 */
	@Override
	public int updatePlcmtHeaderStatus(String cdStatus, Long idSsccReferral) {
		Query query = sessionFactory.getCurrentSession().createQuery(updatePlcmtHeaderStatusHql);
		query.setParameter("cdStatus", cdStatus).setParameter("idSsccReferral", idSsccReferral);
		int updatedResult = query.executeUpdate();
		return updatedResult;
	}

	/**
	 * Method updates the cd_status column in the sscc_child_plan_topic table.
	 *
	 * @param cdStatus
	 *            the cd status
	 * @param idSsccReferral
	 *            the id sscc referral
	 * @return the int
	 */
	@Override
	public int updateSSCCChildPlanTopicStatus(String cdStatus, Long idSsccReferral) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(updateSSCCChildPlanTopicStatusSql);
		query.setParameter("cdStatus", cdStatus);
		query.setParameter("idSSCCReferral", idSsccReferral);
		int rowsUpdated = query.executeUpdate();
		return rowsUpdated;

	}

	/**
	 * Method updates the cd_status column in the sscc_child_plan table.
	 *
	 * @param cdStatus
	 *            the cd status
	 * @param idSsccReferral
	 *            the id sscc referral
	 * @return the int
	 */
	@Override
	public int updateSSCCChildPlanStatus(String cdStatus, Long idSsccReferral) {
		Query query = sessionFactory.getCurrentSession().createQuery(updateSSCCChildPlanStatusHql);
		query.setParameter("cdStatus", cdStatus).setParameter("idSsccReferral", idSsccReferral);
		int updatedResult = query.executeUpdate();
		return updatedResult;
	}

	/**
	 * Method Name:updateSSCCServiceAuth Method Description: Method updates a
	 * row in the SSCC_SERVICE_AUTHORIZATION table.
	 *
	 * @param cdStatus
	 *            the cd status
	 * @param idSsccReferral
	 *            the id sscc referral
	 * @return the int
	 */
	@Override
	public int updateSSCCServiceAuth(String cdStatus, Long idSsccReferral) {
		Query query = sessionFactory.getCurrentSession().createQuery(updateSSCCServiceAuthHql);
		query.setParameter("cdStatus", cdStatus).setParameter("idSsccReferral", idSsccReferral);
		int updatedResult = query.executeUpdate();
		return updatedResult;
	}

	/**
	 * Method unassign's all SSCC Secondary Staff for stage Also, unassign's
	 * staff when necessary.
	 *
	 * @param ssccReferralDto
	 *            the sscc referral dto
	 */
	@Override
	public void unAssignAllSSCCSecondaryStaff(SSCCRefDto ssccReferralDto) {
		if (!ssccReferralDto.getIndUserSSCC()) {
			if (!ObjectUtils.isEmpty(ssccReferralDto.getSsccSecondaryStaff())) {
				ssccReferralDto.getSsccSecondaryStaff().forEach(o -> {
					deleteStagePersonLink(o, ssccReferralDto.getIdStage());
				});
			}

		}
	}

	/**
	 * Method Name: insertIntoStagePersonLink Method Description: inserts into
	 * insertIntoStagePersonLink.
	 *
	 * @param ssccReferralDto
	 *            the sscc referral dto
	 * @param idSecondaryStaff
	 *            the id secondary staff
	 * @return long
	 */
	@Override
	public Long insertIntoStagePersonLink(SSCCRefDto ssccReferralDto, Long idSecondaryStaff) {
		StagePersonLink stagePersonLink = new StagePersonLink();
		stagePersonLink.setIdStage(ssccReferralDto.getIdStage());
		stagePersonLink.setIdPerson(idSecondaryStaff);
		stagePersonLink.setIdCase(ssccReferralDto.getIdCase());
		stagePersonLink.setCdStagePersRole(CodesConstant.CSTFROLS_SE);
		stagePersonLink.setIndStagePersInLaw(ServiceConstants.STR_ZERO_VAL);
		stagePersonLink.setCdStagePersType(CodesConstant.CPRSNALL_STF);
		stagePersonLink.setCdStagePersSearchInd(ServiceConstants.STR_ZERO_VAL);
		stagePersonLink.setTxtStagePersNotes(ServiceConstants.EMPTY_STRING);
		stagePersonLink.setDtStagePersLink(new Date());
		stagePersonLink.setCdStagePersRelInt(ServiceConstants.EMPTY_STRING);
		stagePersonLink.setIndStagePersReporter(ServiceConstants.STR_ZERO_VAL);
		stagePersonLink.setIndStagePersPrSecAsgn(ServiceConstants.Y);
		stagePersonLink.setIndStagePersEmpNew(ServiceConstants.STR_ONE_VAL);
		stagePersonLink.setIndNmStage(Boolean.FALSE);
		stagePersonLink.setDtLastUpdate(new Date());
		StgPersonLinkDto stagePersonLinkDto = stagePersonLinkDao.updateStagePersonLinkDetails(stagePersonLink);
		return stagePersonLinkDto.getIdStagePersonLink();
	}

	/**
	 * Method Name: createSecondaryAssignTodo Method Description: This method is
	 * used to create todo's for secondary assigned staff.
	 *
	 * @param ssccReferralDto
	 *            the sscc referral dto
	 * @param userId
	 *            the user id
	 * @param idPesonAssigned
	 *            the id peson assigned
	 * @param idPrimaryForStage
	 *            the id primary for stage
	 * @return long
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	@Override
	public void createSecondaryAssignTodo(SSCCRefDto ssccReferralDto, String userId, long idPesonAssigned,
			long idPrimaryForStage) {
		Stage stage = (Stage) sessionFactory.getCurrentSession().load(Stage.class, ssccReferralDto.getIdStage());
		String workerInits = todoService.formatUserInitials(formattingUtils.formatName(idPrimaryForStage));
		TodoDto todoDto = new TodoDto();
		todoDto.setCdTodoType(ServiceConstants.ALERT_TODO);
		todoDto.setDtTodoCompleted(new Date());
		todoDto.setDtTodoCreated(new Date());
		todoDto.setDtTodoDue(new Date());
		todoDto.setIdTodoCase(stage.getCapsCase().getIdCase());
		todoDto.setIdTodoPersAssigned(idPesonAssigned);
		todoDto.setIdTodoPersWorker(idPrimaryForStage);
		todoDto.setIdTodoStage(ssccReferralDto.getIdStage());
		todoDto.setIdTodoPersCreator(Long.valueOf(userId));
		todoDto.setNmTodoCreatorInit(workerInits);
		todoDto.setTodoDesc(ServiceConstants.TODO_DESC_NEW_SEC);
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		toDoDao.todoAUD(todoDto, serviceReqHeaderDto);
	}

	/**
	 * Method Name: deleteStagePersonLink Method Description:
	 * deleteStagePersonLink.
	 *
	 * @param idStaffPerson
	 *            the id staff person
	 * @param idStage
	 *            the id stage
	 */
	@Override
	public void deleteStagePersonLink(Long idStaffPerson, Long idStage) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);

		criteria.add(Restrictions.eq("idPerson", idStaffPerson));
		criteria.add(Restrictions.eq("idStage", idStage));
		criteria.add(Restrictions.eq("cdStagePersRole", "SE"));

		List<StagePersonLink> stagePersonLinks = criteria.list();
		for (StagePersonLink personLink : stagePersonLinks) {
			sessionFactory.getCurrentSession().delete(personLink);
		}
	}

	/**
	 * Method Name: fetchSSCCReferralFamilyPersonList Method Description:
	 * Fetches SSCC Referral Family records for an SSCC Referral.
	 *
	 * @param idSSCCReferral
	 *            the id SSCC referral
	 * @return the array list
	 */
	@Override
	public ArrayList<SSCCRefFamilyDto> fetchSSCCReferralFamilyPersonList(Long idSSCCReferral) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(fetchSSCCReferralFamilyPersonListSql)
				.addScalar("idSsccReferralFamily", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("idSsccReferral", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("dtStart", StandardBasicTypes.TIMESTAMP).addScalar("dtEnd", StandardBasicTypes.TIMESTAMP)
				.addScalar("indSvcAuth", StandardBasicTypes.BOOLEAN)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("cdPersonSuffix", StandardBasicTypes.STRING)
				.addScalar("nmCreatedPerson", StandardBasicTypes.STRING)
				.addScalar("nmLastUpdatePerson", StandardBasicTypes.STRING)
				.addScalar("cdRelInt", StandardBasicTypes.STRING)
				.addScalar("dtPersonBirth", StandardBasicTypes.TIMESTAMP)
				.setResultTransformer(Transformers.aliasToBean(SSCCRefFamilyDto.class));
		query.setParameter("idSSCCReferral", idSSCCReferral);
		ArrayList<SSCCRefFamilyDto> ssccRefFamilyDtoList = (ArrayList<SSCCRefFamilyDto>) query.list();
		if (!ObjectUtils.isEmpty(ssccRefFamilyDtoList) && !ssccRefFamilyDtoList.isEmpty()) {
			//UC12 changes. Commenting the code to display the age and full name for all persons,not only persons with Svc Auth as Y.
			/*ssccRefFamilyDtoList.stream()
					.filter(o -> ((!ObjectUtils.isEmpty(o.getIndSvcAuth()) && o.getIndSvcAuth())
							|| !ObjectUtils.isEmpty(o.getDtEnd())))
					.peek(e -> e.setIndDisableRadioButton(Boolean.TRUE))
					.filter(o -> !ObjectUtils.isEmpty(o.getDtPersonBirth()))
					.peek(e -> e.setAge(DateUtils.getAge(e.getDtPersonBirth())))
					.filter(o -> !ObjectUtils.isEmpty(o.getCdPersonSuffix()))
					.peek(e -> e.getNmPersonFull().concat(" ").concat(e.getCdPersonSuffix()))
					.collect(Collectors.toList());*/
		   //UC12 changes. Added the below code to display age and full name for all persons.
			for (SSCCRefFamilyDto ssccRefFamilyDto : ssccRefFamilyDtoList) {
				if(!ObjectUtils.isEmpty(ssccRefFamilyDto.getDtEnd())) {
					ssccRefFamilyDto.setIndDisableRadioButton(Boolean.TRUE);
				}
				if(!ObjectUtils.isEmpty(ssccRefFamilyDto.getDtPersonBirth())) {
					ssccRefFamilyDto.setAge(DateUtils.getAge(ssccRefFamilyDto.getDtPersonBirth()));
				}
				if(!ObjectUtils.isEmpty(ssccRefFamilyDto.getCdPersonSuffix())) {
					ssccRefFamilyDto.setNmPersonFull(ssccRefFamilyDto.getNmPersonFull().concat(" ").concat(ssccRefFamilyDto.getCdPersonSuffix()));
				}
				
			}
			
		}
		return ssccRefFamilyDtoList;
	}

	/**
	 * Method Name: fetchSSCCResourceDtStart Method Description: This method is
	 * used to retrieve start date of the resource from the sscc_parameters
	 * table.
	 *
	 * @param idSSCCResource
	 *            the id SSCC resource
	 * @param idSSCCCatchment
	 *            the id SSCC catchment
	 * @param cdCntrctRegion
	 *            the cd cntrct region
	 * @return the date
	 */
	@Override
	public Date fetchSSCCResourceDtStart(Long idSSCCResource, Long idSSCCCatchment, String cdCntrctRegion, String refType) {
		Date dtStart = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccParameters.class);
		if (ServiceConstants.CSSCCREF_30.equals(refType)) {
			criteria.add(Restrictions.eq("idResourceFamilySA", idSSCCResource));
		} else {
			criteria.add(Restrictions.eq("idResource", idSSCCResource));
		}
		criteria.add(Restrictions.eq("idResource", idSSCCResource));
		criteria.add(Restrictions.eq("idSsccCatchment", idSSCCCatchment));
		criteria.add(Restrictions.eq("cdCntrctRegion", cdCntrctRegion));
		criteria.addOrder(Order.asc("dtStart")); // Defect 11488 - Add ordering to the get the minimum
		List<SsccParameters> list = criteria.list();
		if (!ObjectUtils.isEmpty(list)) {
			dtStart = list.stream().filter(o -> !ObjectUtils.isEmpty(o.getDtStart())).map(o -> o.getDtStart())
					.findFirst().get();
		}
		return dtStart;
	}

	/**
	 * Method Name: updateSsccReferralEvent Method Description: this method is
	 * used to update sscc referral event table.
	 *
	 * @param ssccRefPlcmtDto
	 *            the sscc ref plcmt dto
	 * @return the long
	 */
	@Override
	public Long updateSsccReferralEvent(SSCCRefPlcmtDto ssccRefPlcmtDto) {
		SsccReferralEvent ssccReferralEvent;
		if (!ObjectUtils.isEmpty(ssccRefPlcmtDto) && !ObjectUtils.isEmpty(ssccRefPlcmtDto.getIdSsccRefEvent())) {
			ssccReferralEvent = (SsccReferralEvent) sessionFactory.getCurrentSession().load(SsccReferralEvent.class,
					ssccRefPlcmtDto.getIdSsccRefEvent());
		} else {
			ssccReferralEvent = new SsccReferralEvent();
			ssccReferralEvent.setDtCreated(new Date());
		}
		if (!ObjectUtils.isEmpty(ssccRefPlcmtDto.getIdLastUpdatePerson())) {
			ssccReferralEvent.setIdLastUpdatePerson(ssccRefPlcmtDto.getIdLastUpdatePerson());
		}
		if (!ObjectUtils.isEmpty(ssccRefPlcmtDto.getIdCreatedPerson())) {
			ssccReferralEvent.setIdCreatedPerson(ssccRefPlcmtDto.getIdCreatedPerson());
		}
		if (!ObjectUtils.isEmpty(ssccRefPlcmtDto.getIdSSCCReferral())) {
			SsccReferral ssccReferral = (SsccReferral) sessionFactory.getCurrentSession().load(SsccReferral.class,
					ssccRefPlcmtDto.getIdSSCCReferral());
			ssccReferralEvent.setSsccReferral(ssccReferral);
		}
		if (!ObjectUtils.isEmpty(ssccRefPlcmtDto.getIdEvent())) {
			ssccReferralEvent.setIdEvent(ssccRefPlcmtDto.getIdEvent());
		}
		if (!ObjectUtils.isEmpty(ssccRefPlcmtDto.getCdEventType())) {
			ssccReferralEvent.setCdEventType(ssccRefPlcmtDto.getCdEventType());
		}
		ssccReferralEvent.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(ssccReferralEvent);
		return ssccReferralEvent.getIdSsccRefEvent();
	}

	/**
	 * Method Name: updateSSCCList Method Description: This method is used to
	 * update sscclist.
	 *
	 * @param ssccListDto
	 *            the sscc list dto
	 * @return the long
	 */
	@Override
	public Long updateSSCCList(SSCCListDto ssccListDto) {
		SsccList ssccList = new SsccList();
		if (!ObjectUtils.isEmpty(ssccListDto.getIdSsccList())) {
			ssccList = (SsccList) sessionFactory.getCurrentSession().load(SsccList.class, ssccListDto.getIdSsccList());
		}
		BeanUtils.copyProperties(ssccListDto, ssccList);
		SsccReferral ssccReferral = (SsccReferral) sessionFactory.getCurrentSession().load(SsccReferral.class,
				ssccListDto.getIdSSCCReferral());
		ssccList.setSsccReferral(ssccReferral);
		if (ObjectUtils.isEmpty(ssccListDto.getIndCpContentApprove())) {
			ssccList.setIndCpContentApprove(ServiceConstants.N);
		}
		if (ObjectUtils.isEmpty(ssccListDto.getIndCpContentPropose())) {
			ssccList.setIndCpContentPropose(ServiceConstants.N);
		}
		if (ObjectUtils.isEmpty(ssccListDto.getIndCpContentReject())) {
			ssccList.setIndCpContentReject(ServiceConstants.N);
		}
		if (ObjectUtils.isEmpty(ssccListDto.getIndCpContentRepropose())) {
			ssccList.setIndCpContentRepropose(ServiceConstants.N);
		}
		if (ObjectUtils.isEmpty(ssccListDto.getIndCpContentUnlock())) {
			ssccList.setIndCpContentUnlock(ServiceConstants.N);
		}
		if (ObjectUtils.isEmpty(ssccListDto.getIndDaycareValidated())) {
			ssccList.setIndDaycareValidated(ServiceConstants.N);
		}
		if (ObjectUtils.isEmpty(ssccListDto.getIndEfc())) {
			ssccList.setIndEfc(ServiceConstants.N);
		}
		if (ObjectUtils.isEmpty(ssccListDto.getIndEfcActive())) {
			ssccList.setIndEfcActive(ServiceConstants.N);
		}
		if (ObjectUtils.isEmpty(ssccListDto.getIndLegalStatusMissing())) {
			ssccList.setIndLegalStatusMissing(ServiceConstants.N);
		}
		if (ObjectUtils.isEmpty(ssccListDto.getIndNew())) {
			ssccList.setIndNew(ServiceConstants.N);
		}
		if (ObjectUtils.isEmpty(ssccListDto.getIndNonssccSvcAuth())) {
			ssccList.setIndNonssccSvcAuth(ServiceConstants.N);
		}
		if (ObjectUtils.isEmpty(ssccListDto.getIndSsccDaycare())) {
			ssccList.setIndSsccDaycare(ServiceConstants.N);
		}
		sessionFactory.getCurrentSession().saveOrUpdate(ssccList);
		return ssccList.getIdSsccList();
	}

	/**
	 * Method Name: deleteSSCCReferral Method Description: This method deletes
	 * the sscc referral header information from SSCC_REFERRAL table.
	 *
	 * @param idSSCCReferral
	 *            the id SSCC referral
	 * @return true, if successful
	 */
	@Override
	public boolean deleteSSCCReferral(Long idSSCCReferral) {
		boolean ssccReferralDeleted = false;
		SsccReferral ssccReferral = (SsccReferral) sessionFactory.getCurrentSession().load(SsccReferral.class,
				idSSCCReferral);
		if (!ObjectUtils.isEmpty(ssccReferral)) {
			sessionFactory.getCurrentSession().delete(ssccReferral);
			ssccReferralDeleted = true;
		}
		return ssccReferralDeleted;
	}

	/**
	 * Method Name: fetchVendorIdforReferral Method Description:Returns the
	 * Vendor Id from the batch_sscc_parameters table for a specific Resource Id
	 * and Region.
	 *
	 * @param sSCCRefDto
	 *            the s SCC ref dto
	 * @return strVendorId
	 */

	@Override
	public Long fetchVendorIdforReferral(SSCCRefDto sSCCRefDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccParameters.class);
		//For Family Referral, the Resource Id should be checked against the ID_RESOURCE_FAMILY_SA column.
		//For Child Referral, the Resource Id is stored in ID_RESOURCE.
		if (ServiceConstants.CSSCCREF_30.equals(sSCCRefDto.getCdSSCCRefType())) {
			criteria.add(Restrictions.eq("idResourceFamilySA", sSCCRefDto.getIdSSCCResource()));
		} else {
			criteria.add(Restrictions.eq("idResource", sSCCRefDto.getIdSSCCResource()));
		}
		criteria.add(Restrictions.eq("cdCntrctRegion", sSCCRefDto.getCdCntrctRegion()));
		criteria.add(Restrictions.eq("idSsccCatchment", sSCCRefDto.getIdSSCCCatchment()));
		criteria.add(Restrictions.le("dtStart", sSCCRefDto.getDtRecorded()));
		criteria.add(Restrictions.gt("dtEnd", sSCCRefDto.getDtRecorded()));
		List<SsccParameters> ssccParametersList = criteria.list();
		return (!ObjectUtils.isEmpty(ssccParametersList)) ? ssccParametersList.get(0).getIdVendor() : null;
	}

	/**
	 * Method Name: hasActiveSvcforRefPerson Method Description: true if
	 * referral person has any Service Authorization in PROC, COMP ,PEND status.
	 *
	 * @param sSCCRefDto
	 *            the s SCC ref dto
	 * @param vendorId
	 *            the vendor id
	 * @return Boolean
	 */

	@Override
	public boolean hasActiveSvcforRefPerson(SSCCRefDto sSCCRefDto, Long vendorId) {
		Query query1 = sessionFactory.getCurrentSession().createSQLQuery(hasActiveSvcforRefPersonSql)
				.addScalar("idSvcAuthDtl", StandardBasicTypes.LONG);
		long idPerson = ObjectUtils.isEmpty(sSCCRefDto.getIdPerson()) ? ServiceConstants.ZERO: sSCCRefDto.getIdPerson(); 
		query1.setParameter("idPerson", idPerson);
		if (CodesConstant.CSSCCREF_30.equals(sSCCRefDto.getCdSSCCRefType())) {
			query1.setParameter("cdStage", CodesConstant.CSTAGES_FRE);
			query1.setParameter("cdS", CodesConstant.CSTAGES_FSU);
		} else {
			query1.setParameter("cdStage", CodesConstant.CSTAGES_SUB);
			query1.setParameter("cdS", CodesConstant.CSTAGES_PAL);
		}
		query1.setParameter("nbrRsrcAddrVid", String.valueOf(vendorId));
		query1.setResultTransformer(Transformers.aliasToBean(SSCCRefDto.class));
		List<SSCCRefDto> sSCCDto = query1.list();
		return (!ObjectUtils.isEmpty(sSCCDto) && !sSCCDto.isEmpty()) ? true : false;

	}

	/**
	 * Method Name: hasSSCCActExcptCareDesig Method Description: Returns true if
	 * referral has any SSCC Exceptional care designations in PROPOSE status.
	 *
	 * @param idSsccReferral
	 *            the id sscc referral
	 * @return Boolean
	 */
	@Override
	public Boolean hasSSCCActExcptCareDesig(Long idSsccReferral) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccExceptCareDesig.class);
		criteria.add(Restrictions.eq("idSsccReferral", idSsccReferral));
		criteria.add(Restrictions.eq("cdStatus", "60"));
		List<SsccExceptCareDesig> ssccExceptCareDesigList = criteria.list();
		return (!ObjectUtils.isEmpty(ssccExceptCareDesigList) && !ssccExceptCareDesigList.isEmpty()) ? true : false;

	}

	/**
	 * Method Name: hasActiveSSCCSvcAuthReq Method Description: Returns true if
	 * the referral has any SSCC Service Auth Request in the following status 1.
	 * INITIATE 2. PROPOSE
	 *
	 * @param idSsccReferral
	 *            the id sscc referral
	 * @return Boolean
	 */
	@Override
	public Boolean hasActiveSSCCSvcAuthReq(Long idSsccReferral) {
		List<String> cdStatusList = Collections.unmodifiableList(Arrays.asList("20", "60"));
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccServiceAuthorization.class);
		criteria.add(Restrictions.eq("idSsccReferral", idSsccReferral));
		criteria.add(Restrictions.in("cdStatus", cdStatusList));
		List<SsccServiceAuthorization> ssccServiceAuthorizationList = criteria.list();
		return (!ObjectUtils.isEmpty(ssccServiceAuthorizationList) && !ssccServiceAuthorizationList.isEmpty()) ? true
				: false;
	}

	/**
	 * Method Name: hasActivePlcmtCircumforRef Method Description :Returns true
	 * if referral has any Placement Circumstances in the following status 1.
	 * PROPOSE 2. ACKNOWLEDGE
	 *
	 * @param idSsccReferral
	 *            the id sscc referral
	 * @return Boolean
	 */
	@Override
	public Boolean hasActivePlcmtCircumforRef(Long idSsccReferral) {
		List<String> cdStatusList = Collections.unmodifiableList(Arrays.asList("60", "50"));
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccPlcmtHeader.class);
		criteria.add(Restrictions.eq("ssccReferral.idSSCCReferral", idSsccReferral));
		criteria.add(Restrictions.ne("cdSSCCPlcmtType", "10"));
		criteria.add(Restrictions.in("cdStatus", cdStatusList));
		List<SsccPlcmtHeader> ssccPlcmtHeaderList = criteria.list();
		return (!ObjectUtils.isEmpty(ssccPlcmtHeaderList) && !ssccPlcmtHeaderList.isEmpty()) ? true : false;
	}

	/**
	 * Method Name: hasActivePlcmtOptionsforRef Method Description:.
	 *
	 * @param idSsccReferral
	 *            the id sscc referral
	 * @return the boolean
	 */

	@Override
	public Boolean hasActivePlcmtOptionsforRef(Long idSsccReferral) {
		List<String> cdStatusList = Collections.unmodifiableList(Arrays.asList("30", "60", "50", "90", "110"));
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccPlcmtHeader.class);
		criteria.add(Restrictions.eq("ssccReferral.idSSCCReferral", idSsccReferral));
		criteria.add(Restrictions.eq("cdSSCCPlcmtType", "10"));
		criteria.add(Restrictions.in("cdStatus", cdStatusList));
		List<SsccPlcmtHeader> ssccPlcmtHeaderList = criteria.list();
		return (!ObjectUtils.isEmpty(ssccPlcmtHeaderList) && !ssccPlcmtHeaderList.isEmpty()) ? true : false;
	}

	/**
	 * Method Name: fetchAprvSvcAuthList Method Description:
	 * fetchAprvSvcAuthList.
	 *
	 * @param sSCCRefDto
	 *            the s SCC ref dto
	 * @param vendorId
	 *            the vendor id
	 * @return List
	 */
	@Override
	public List<SSCCRefDto> fetchAprvSvcAuthList(SSCCRefDto sSCCRefDto, Long vendorId) {
		
		StringBuilder fetchAprvSvcAuthListSQL = new StringBuilder(fetchAprvSvcAuthListSql);
        if( !CodesConstant.CSSCCREF_30.equals(sSCCRefDto.getCdSSCCRefType())){
        	fetchAprvSvcAuthListSQL.append( fetchActRefForPlcmtRefSql );
        }
        else{
        	fetchAprvSvcAuthListSQL.append( fetchActRefForFamilyRefSql );
        }

        fetchAprvSvcAuthListSQL.append( fetchAprvSvcAuthListWhereSql );

    if (!CodesConstant.CSSCCREF_30.equals(sSCCRefDto.getCdSSCCRefType())) {
      fetchAprvSvcAuthListSQL.append(excludePCSservices);
    }
		Query query1 = sessionFactory.getCurrentSession().createSQLQuery(fetchAprvSvcAuthListSQL.toString())
				.addScalar("dtSvcAuthDtlTerm", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtSvcAuthDtlBegin", StandardBasicTypes.TIMESTAMP);

		if (!CodesConstant.CSSCCREF_30.equals(sSCCRefDto.getCdSSCCRefType())) {

			query1.setParameter("idPerson", sSCCRefDto.getIdPerson());
		} else {
			query1.setLong("idSSCCReferral", sSCCRefDto.getIdSSCCReferral());
		}

		if (CodesConstant.CSSCCREF_30.equals(sSCCRefDto.getCdSSCCRefType())) {
			query1.setParameter("cdStage", CodesConstant.CSTAGES_FRE);
			query1.setString("cdS", CodesConstant.CSTAGES_FSU);
		} else {
			query1.setParameter("cdStage", CodesConstant.CSTAGES_SUB);
			query1.setString("cdS", CodesConstant.CSTAGES_PAL);
		}
		query1.setParameter("nbrRsrcAddrVid", String.valueOf(vendorId));
		query1.setResultTransformer(Transformers.aliasToBean(SSCCRefDto.class));
		List<SSCCRefDto> listSvcAuthVal = query1.list();
		return listSvcAuthVal;
	}

	/**
	 * Method Name: hasActiveSSCCChildPlanContent Method Description:Method
	 * returns true if there is at least one active SSCC Child Plan topic for
	 * referral.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @return boolean
	 */
	@Override
	public boolean hasActiveSSCCChildPlanContent(SSCCRefDto ssccRefDto) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(hasActiveSSCCChildPlanContent);
		query.setParameter("idReferral", ssccRefDto.getIdSSCCReferral());
		List list = query.list();
		return (!TypeConvUtil.isNullOrEmpty(list) && list.size() > 0) ? true : false;
	}

	/**
	 * Method name: hasActiveSSCCPlcmtReferralExistsForStage Method Description:
	 * Method returns true if there is at least one active SSCC Family Service
	 * Referral for a given case.
	 *
	 * @param idSSCCReferral
	 *            the id SSCC referral
	 * @param idCase
	 *            the id case
	 * @return boolean
	 */

	@Override
	public boolean hasFamRefMaxDtDischargeInCase(Long idSSCCReferral, Long idCase) {
		List<SSCCRefDto> ssccReferrals = (ArrayList<SSCCRefDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(hasFamRefMaxDtDischargeInCaseSql).addScalar("idPerson", StandardBasicTypes.LONG)
				.setParameter("idSSCCReferral", idSSCCReferral).setParameter("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(SSCCRefDto.class)).list();
		return (!ObjectUtils.isEmpty(ssccReferrals)) ? true : false;
	}

	/**
	 * Method name: hasPlcmtRefMaxDtDischargeInStage Method Description: Method
	 * returns true if the given Placement Referral has the maximum actual
	 * discharge date for a given stage.
	 *
	 * @param idSSCCReferral
	 *            the id SSCC referral
	 * @param idStage
	 *            the id stage
	 * @return true, if successful
	 */

	@Override
	public boolean hasPlcmtRefMaxDtDischargeInStage(Long idSSCCReferral, Long idStage) {
		List<SSCCRefDto> ssccReferrals = (ArrayList<SSCCRefDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(hasPlcmtRefMaxDtDischargeInStageSql).addScalar("idPerson", StandardBasicTypes.LONG)
				.setParameter("idSSCCReferral", idSSCCReferral).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(SSCCRefDto.class)).list();
		return (!ObjectUtils.isEmpty(ssccReferrals)) ? true : false;
	}

	/**
	 * Method name: hasActiveSSCCPlcmtReferralExistsForStage Method Description:
	 * Method returns true if there is at least one active SSCC Family Service
	 * Referral for a given case.
	 *
	 * @param idStage
	 *            , placementReferaal
	 * @param strReferralType
	 *            the str referral type
	 * @return boolean
	 */

	@Override
	public boolean hasActiveSSCCPlcmtReferralExistsForStage(Long idStage, String strReferralType) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccReferral.class);
		criteria.add(Restrictions.eq("idStage", idStage));
		criteria.add(Restrictions.eq("cdRefStatus", "40"));
		if (strReferralType.equals(ServiceConstants.PLACEMENT_REFERAAL)) {
			String[] ssccRefType = { "10", "20" };
			criteria.add(Restrictions.in("cdSSCCRefType", ssccRefType));

		} else if (strReferralType.equals(ServiceConstants.FAMILY_REFERAAL)) {
			criteria.add(Restrictions.eq("cdSSCCRefType", "30"));
		}
		List<SsccReferral> ssccReferrals = criteria.list();
		return (!ObjectUtils.isEmpty(ssccReferrals)) ? true : false;
	}

	/**
	 * Method name: updateSSCCRefStatus Method Description: this method is used
	 * to update SSCC Referral Status.
	 *
	 * @param ssccRefDto
	 *            the sscc ref dto
	 * @param cdStatus
	 *            the cd status
	 */

	@Override
	public void updateSSCCRefStatus(SSCCRefDto ssccRefDto, String cdStatus) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccReferral.class);
		criteria.add(Restrictions.eq("idSSCCReferral", ssccRefDto.getIdSSCCReferral()));

		SsccReferral referral = (SsccReferral) criteria.uniqueResult();
		if (!ObjectUtils.isEmpty(referral)) {
			referral.setCdRefStatus(cdStatus);
			referral.setCdDischargeReason(ServiceConstants.EMPTY_STRING);
			referral.setDtDischargePlanned(null);
			referral.setIndDischargeReadyReview(ServiceConstants.EMPTY_STRING);
			referral.setDtDischargeActual(null);
			referral.setCdRefSubStatus(ServiceConstants.EMPTY_STRING);
			sessionFactory.getCurrentSession().saveOrUpdate(referral);
		}
	}

	/**
	 * Method Name: fetchSSCCPlcmtEndDtList Method Description: Method fetches a
	 * list of SSCC Placement End Dates for a stage and sscc resource id where
	 * placement start date is same as placement end date.
	 *
	 * @param idStage
	 *            the id stage
	 * @param idSSCCResource
	 *            the id SSCC resource
	 * @return the list
	 */
	@Override
	public List<PlacementDto> fetchSSCCPlcmtEndDtList(Long idStage, Long idSSCCResource) {
		List<PlacementDto> plcmList = (ArrayList<PlacementDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(fetchSSCCPlcmtEndDtListSql).addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("dtPlcmtStart", StandardBasicTypes.DATE).addScalar("dtPlcmtEnd", StandardBasicTypes.DATE)
				.setParameter("idStage", idStage).setParameter("idRsrcSSCC", idSSCCResource)
				.setResultTransformer(Transformers.aliasToBean(PlacementDto.class)).list();
		return plcmList;
	}

	/**
	 * This method is used to delete all the SSCC Timelines linked with
	 * idssccreferral.
	 */
	@Override
	public void deleteSSCCTimeline(Long idSSCCReferral) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccTimeline.class);
		criteria.add(Restrictions.eq("ssccReferral.idSSCCReferral", idSSCCReferral));
		List<SsccTimeline> sSCCTimeline = criteria.list();
		sSCCTimeline.forEach(sSCCTimelineDelete -> {
			sessionFactory.getCurrentSession().delete(sSCCTimelineDelete);
		});
	}

	/**
	 * This method is used to delete all the SSCC service authorizations linked
	 * with idSSCCReferral
	 */
	@Override
	public void deleteSSCCSvcAuth(Long idSSCCReferral) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccServiceAuthorization.class);
		criteria.add(Restrictions.eq("idSsccReferral", idSSCCReferral));
		List<SsccServiceAuthorization> sSCCServiceAuthorization = criteria.list();
		sSCCServiceAuthorization.forEach(sSCCServiceAuthorizationDelete -> {
			sessionFactory.getCurrentSession().delete(sSCCServiceAuthorizationDelete);
		});
		//Adding this for CBC R3. The Service Auth records are present in SERVICE_AUTHORIZATION table.
		criteria = sessionFactory.getCurrentSession().createCriteria(ServiceAuthorization.class);
		criteria.add(Restrictions.eq("idSsccReferral", idSSCCReferral));
		List<ServiceAuthorization> serviceAuthorization = criteria.list();
		serviceAuthorization.forEach(serviceAuthorizationDelete -> {//code added for CBC R3 changes
			Set<SvcAuthValid> svcAuthValid = serviceAuthorizationDelete.getSvcAuthValids();
			svcAuthValid.forEach(svcAuthValidDelete -> {
				sessionFactory.getCurrentSession().delete(svcAuthValidDelete);
			});
			Set<SvcAuthDetail> svcAuthDtlList = serviceAuthorizationDelete.getSvcAuthDetails();
			svcAuthDtlList.forEach(svcAuthDtlDelete -> {
				sessionFactory.getCurrentSession().delete(svcAuthDtlDelete);
			});
			Set<SvcAuthEventLink> svcAuthEvents = serviceAuthorizationDelete.getSvcAuthEventLinks();
			svcAuthEvents.forEach(svcAuthEventsDelete -> {
				sessionFactory.getCurrentSession().delete(svcAuthEventsDelete);
			});
			sessionFactory.getCurrentSession().delete(serviceAuthorizationDelete);
		});	
	}

	/**
	 * This method is used to delete all the sscc family referrals linked with
	 * idSSCCReferrals.
	 */
	@Override
	public void deleteSSCCRefFamily(Long idSSCCReferral) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccReferralFamily.class);
		criteria.add(Restrictions.eq("ssccReferral.idSSCCReferral", idSSCCReferral));
		List<SsccReferralFamily> sSCCReferralFamily = criteria.list();
		sSCCReferralFamily.forEach(sSCCReferralFamilyDelete -> {
			sessionFactory.getCurrentSession().delete(sSCCReferralFamilyDelete);
		});
	}

	/**
	 * This method is used to delete all the ssccReferralEvents linked with
	 * idSSCCReferral
	 */
	@Override
	public void deleteSSCCRefEvent(Long idSSCCReferral) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccReferralEvent.class);
		criteria.add(Restrictions.eq("ssccReferral.idSSCCReferral", idSSCCReferral));
		List<SsccReferralEvent> sSCCReferralEvent = criteria.list();
		sSCCReferralEvent.forEach(sSCCReferralEventDelete -> {
			sessionFactory.getCurrentSession().delete(sSCCReferralEventDelete);
		});
	}

	/**
	 * This method is used to delete all the SSCC placements linked with
	 * idSSCCReferral
	 */
	@Override
	public void deleteSSCCPlcmtHeader(Long idSSCCReferral) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccPlcmtHeader.class);
		criteria.add(Restrictions.eq("ssccReferral.idSSCCReferral", idSSCCReferral));
		List<SsccPlcmtHeader> sSCCPlcmtHeader = criteria.list();
		sSCCPlcmtHeader.forEach(sSCCPlcmtHeaderDelete -> {
			sessionFactory.getCurrentSession().delete(sSCCPlcmtHeaderDelete);
		});
	}

	/**
	 * This method is used to delete all the sscc Child Plans which are linked
	 * with idSSCCReferral.
	 */
	@Override
	public void deleteSSCCChildPlanParticip(Long idSsccReferral) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(deleteSSCCChildPlanParticipSql)
				.setParameter("idSSCCReferral", idSsccReferral);
		query.executeUpdate();

	}

	/**
	 * This method is used to delete all the sscc child plan topics linked with
	 * idSSCCReferral
	 */
	@Override
	public void deleteSSCCChildPlanTopic(Long idSsccReferral) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(deleteSSCCChildPlanTopicSql)
				.setParameter("idSSCCReferral", idSsccReferral);
		query.executeUpdate();
	}

	/**
	 * This method is used to delete all the sscc placement narratives linked
	 * with idSSCCReferral
	 */
	@Override
	public void deleteSSCCPlcmtNarr(Long idSsccReferral) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(deleteSSCCPlcmtNarrSql)
				.setParameter("idSSCCReferral", idSsccReferral);
		query.executeUpdate();
	}

	/**
	 * This method is used to delete all the sscc placements placed linked with
	 * idSSCCReferral
	 */
	@Override
	public void deleteSSCCPlcmtPlaced(Long idSsccReferral) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(deleteSSCCPlcmtPlacedSql)
				.setParameter("idSSCCReferral", idSsccReferral);
		query.executeUpdate();
	}

	/**
	 * This method is used to delete all the sscc placement info linked with
	 * idSSCCReferral
	 */
	@Override
	public void deleteSSCCPlcmtInfo(Long idSsccReferral) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(deleteSSCCPlcmtInfoSql)
				.setParameter("idSSCCReferral", idSsccReferral);
		query.executeUpdate();
	}

	/**
	 * This method is used to delete all the sscc placement medical consentors
	 * linked with idSSCCReferral
	 */
	@Override
	public void deleteSSCCPlcmtMedCnsntr(Long idSsccReferral) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(deleteSSCCPlcmtMedCnsntrSql)
				.setParameter("idSSCCReferral", idSsccReferral);
		query.executeUpdate();
	}

	/**
	 * This method is used to delete all the sscc placement name linked with
	 * idSSCCReferral
	 */
	@Override
	public void deleteSSCCPlcmtName(Long idSsccReferral) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(deleteSSCCPlcmtNameSql)
				.setParameter("idSSCCReferral", idSsccReferral);
		query.executeUpdate();
	}

	/**
	 * This method is used to delete all the sscc placement circumstances linked
	 * with idSSCCReferral
	 */
	@Override
	public void deleteSSCCPlcmtCircumstance(Long idSsccReferral) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(deleteSSCCPlcmtCircumstanceSql)
				.setParameter("idSSCCReferral", idSsccReferral);
		query.executeUpdate();
	}

	/**
	 * This method is used to delete all the sscc child plan linked with
	 * idSSCCReferral
	 */
	@Override
	public void deleteSSCCChildPlan(Long idSSCCReferral) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccChildPlan.class);
		criteria.add(Restrictions.eq("idSsccReferral", idSSCCReferral));
		List<SsccChildPlan> sSCCChildPlan = criteria.list();
		sSCCChildPlan.forEach(sSCCPlan -> {
			sessionFactory.getCurrentSession().delete(sSCCPlan);
		});

	}

	/**
	 * This method is used to delete all the sscc day care requests linked with
	 * idSSCCReferral
	 */
	@Override
	public void deleteSSCCDaycareRequest(Long idSSCCReferral) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccDaycareRequest.class);
		criteria.add(Restrictions.eq("idSsccReferral", idSSCCReferral));
		List<SsccDaycareRequest> sSCCDaycareRequest = criteria.list();
		sSCCDaycareRequest.forEach(sSCCDayRequest -> {
			sessionFactory.getCurrentSession().delete(sSCCDayRequest);
		});
	}

	/**
	 * This method is used to delete all the sscc exceptional care designee
	 * linked with idSSCCReferral
	 */
	@Override
	public void deleteSSCCExceptCareDesig(Long idSSCCReferral) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccExceptCareDesig.class);
		criteria.add(Restrictions.eq("idSsccReferral", idSSCCReferral));
		List<SsccExceptCareDesig> sSCCExceptCareDesig = criteria.list();
		sSCCExceptCareDesig.forEach(sSCCExceptDesig -> {
			sessionFactory.getCurrentSession().delete(sSCCExceptDesig);
		});
	}

	/**
	 * This method is used to delete all the sscc list linked with
	 * idSSCCReferral
	 */
	@Override
	public void deleteSSCCList(Long idSSCCReferral) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccList.class);
		criteria.add(Restrictions.eq("ssccReferral.idSSCCReferral", idSSCCReferral));
		List<SsccList> sSCCList = criteria.list();
		sSCCList.forEach(sSCCListDelete -> {
			sessionFactory.getCurrentSession().delete(sSCCListDelete);
		});
	}

	/**
	 * This method returns true if there is an SSCC Child Plan in PROC status
	 */
	@Override
	public boolean hasActiveSSCCChildPlan(Long idSSCCReferral) {
		Query query1 = sessionFactory.getCurrentSession().createSQLQuery(hasActiveSSCCChildPlanSql)
				.addScalar("idSsccChildPlan", StandardBasicTypes.LONG).setParameter("idSSCCReferral", idSSCCReferral)
				.setResultTransformer(Transformers.aliasToBean(SSCCChildPlanDto.class));
		List<SSCCChildPlanDto> ssccReferrals = query1.list();
		return (!ObjectUtils.isEmpty(ssccReferrals)) ? true : false;
	}

	/**
	 * Returns an array list of SSCCRefDto objects of discharged referrals
	 */
	@Override
	public List<SSCCRefDto> fetchDischargedSSCCReferralsforStage(SSCCRefDto ssccRefDto) {
		List<SSCCRefDto> sSCCRefDtoList = new ArrayList<>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccReferral.class);
		criteria.add(Restrictions.eq("cdRefStatus", "130"));
		criteria.add(Restrictions.eq("idSSCCCatchment", ssccRefDto.getIdSSCCCatchment()));
		criteria.add(Restrictions.eq("idStage", ssccRefDto.getIdStage()));
		criteria.add(Restrictions.eq("idRsrcSSCC", ssccRefDto.getIdSSCCResource()));
		List<SsccReferral> ssccreferralList = criteria.list();
		if (!ObjectUtils.isEmpty(ssccreferralList)) {
			ssccreferralList.forEach(o -> {
				SSCCRefDto sSCCRefDto = new SSCCRefDto();
				sSCCRefDto.setIdSSCCReferral(o.getIdSSCCReferral());
				sSCCRefDto.setDtDischargeActual(o.getDtDischargeActual());
				sSCCRefDto.setDtRecorded(o.getDtRecorded());
				sSCCRefDto.setDtRecordedDfps(o.getDtRecordedDfps());
				sSCCRefDto.setIdStage(o.getIdStage());
				sSCCRefDto.setCdSSCCRefType(o.getCdSSCCRefType());
				sSCCRefDto.setCdRefStatus(o.getCdRefStatus());
				sSCCRefDtoList.add(sSCCRefDto);
			});
		}
		return sSCCRefDtoList;
	}



	/**
	 * Method fetches the Actual Discharge Date of the most recent discharged
	 * SSCC Referral in the stage
	 */
	@Override
	public Date fetchPriorDischargeActualDt(Long idStage) {
		Date dtDischargeActual = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccReferral.class);
		criteria.add(Restrictions.eq("idStage", idStage));
		criteria.add(Restrictions.eq("cdRefStatus", CodesConstant.CSSCCSTA_130));
		criteria.addOrder(Order.desc("dtCreated"));
		List<SsccReferral> list = criteria.list();
		if (!ObjectUtils.isEmpty(list)) {
			dtDischargeActual = list.stream().filter(o -> !ObjectUtils.isEmpty(o.getDtDischargeActual()))
					.map(o -> o.getDtDischargeActual()).findFirst().get();
		}
		return dtDischargeActual;
	}

	/**
	 * Method Name: updateSSCCRefIndPlcmtData Method Description:
	 * 
	 * @param idSSCCReferral
	 */
	@Override
	public void updateSSCCRefIndPlcmtData(Long idSSCCReferral) {
		SsccReferral ssccReferral = (SsccReferral) sessionFactory.getCurrentSession().get(SsccReferral.class,
				idSSCCReferral);
		ssccReferral.setIndLinkedPlcmtData(ServiceConstants.Y);
		sessionFactory.getCurrentSession().saveOrUpdate(ssccReferral);
	}
	
	
	
	/**
	 * UC12 Changes.
	 * Method Name: updateSSCCRefFamily Method Description: This method saves
	 * the end date with new date when user removes the person from the family
	 * referral
	 * 
	 * @param ssccRefFamilyDto
	 * @param idUser
	 * @return
	 */
	@Override
	public void updateSSCCRefFamilyForUndoDischarge(SSCCRefDto ssccRefDto, Long idUser) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccReferralFamily.class);
		criteria.add(Restrictions.eq("ssccReferral.idSSCCReferral", ssccRefDto.getIdSSCCReferral()));
		criteria.add(Restrictions.eq("dtEnd",ssccRefDto.getDtDischargeActual()));
		
		List<SsccReferralFamily> ssccReferralFamilyList = criteria.list();
		ssccReferralFamilyList.forEach(ssccReferralFamily ->{
			if (!ObjectUtils.isEmpty(ssccReferralFamily)) {
				ssccReferralFamily.setIdLastUpdatePerson(idUser);
				ssccReferralFamily.setDtEnd(null);
				ssccReferralFamily.setDtLastUpdate(new Date());
				sessionFactory.getCurrentSession().saveOrUpdate(ssccReferralFamily);
			}
		});
	}

	/**
	 * Returns an array list of SSCCRefFamilyDto objects of person with end date
	 */
	@Override
	public SSCCRefFamilyDto fetchEndDateForPersonInFamReferral(Long idPerson, Long idCase) {
		SSCCRefFamilyDto sSCCFamRefDto = new SSCCRefFamilyDto();
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryDtEndForPersonInFamilyReferralSql)
				.addScalar("dtEnd", StandardBasicTypes.TIMESTAMP)
				.setParameter("idPerson", idPerson)
				.setParameter("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(SSCCRefFamilyDto.class));

		SSCCRefFamilyDto outSSCCRefFamilyDto = (SSCCRefFamilyDto) query.uniqueResult();

		if (!ObjectUtils.isEmpty(outSSCCRefFamilyDto)) {
			sSCCFamRefDto.setDtEnd(outSSCCRefFamilyDto.getDtEnd());
		}
		return sSCCFamRefDto;

	}

	/**
	 * code added for artf231094
	 * @param idSSCCReferral
	 * @param idPerson
	 * @return
	 */
	@Override
	public List<Long> fetchSSCCRefCount(Long idSSCCReferral, Long idPerson) {
		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchSSCCRefCountSql)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.setParameter("idSSCCReferral", idSSCCReferral)
				.setParameter("idPerson", idPerson));
		return sqlQuery.list();
	}

	/**
	 * Method Name: insertIntoStagePersonLinkForTransfer Method Description: inserts into
	 * insertIntoStagePersonLink.
	 *
	 * @param ssccReferralDto
	 *            the sscc referral dto
	 * @param sSCCRefFamilyDto
	 *            the id secondary staff
	 * @return long
	 */
	@Override
	public Long insertIntoStagePersonLinkForTransfer(SSCCRefDto ssccReferralDto, SSCCRefFamilyDto sSCCRefFamilyDto, Long oldStageId) {
		StgPersonLinkDto existingStagePersonLink=stagePersonLinkDao.getStagePersonLinkDetails(sSCCRefFamilyDto.getIdPerson(), oldStageId);
		StagePersonLink stagePersonLink = new StagePersonLink();
		BeanUtils.copyProperties(existingStagePersonLink, stagePersonLink);
		stagePersonLink.setIdStagePersonLink(0l);
		stagePersonLink.setIdStage(ssccReferralDto.getTransferToStage());
		stagePersonLink.setIdPerson(sSCCRefFamilyDto.getIdPerson());
		stagePersonLink.setIdCase(ssccReferralDto.getIdCase());
		stagePersonLink.setCdStagePersSearchInd(CodesConstant.CSRCHSTA_R);
		stagePersonLink.setDtStagePersLink(new Date());
		stagePersonLink.setCdStagePersRelInt(sSCCRefFamilyDto.getCdRelInt());
		stagePersonLink.setDtLastUpdate(new Date());
		StgPersonLinkDto stagePersonLinkDto = stagePersonLinkDao.updateStagePersonLinkDetails(stagePersonLink);
		return stagePersonLinkDto.getIdStagePersonLink();
	}

}
