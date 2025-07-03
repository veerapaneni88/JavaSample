package us.tx.state.dfps.service.arreport.daoimpl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.arinvconclusion.dto.ArInvCnclsnDto;
import us.tx.state.dfps.arinvconclusion.dto.PCSPDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtAreaValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtFactorValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtValueDto;
import us.tx.state.dfps.service.arreport.dao.ArReportDao;
import us.tx.state.dfps.service.arreport.dto.ArPrincipalsHistoryDto;
import us.tx.state.dfps.service.arreport.dto.ArRelationshipsDto;
import us.tx.state.dfps.service.arreport.dto.ArServiceReferralsDto;
import us.tx.state.dfps.service.casemanagement.dao.ArHelperDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityAllegationInfoDto;
import us.tx.state.dfps.service.investigation.dto.AllegationDetailDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dto.CharacteristicsDto;
import us.tx.state.dfps.service.riskassesment.dto.SdmSafetyRiskAssessmentsDto;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Calls
 * database to retrieve data for service Apr 3, 2018- 10:49:41 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class ArReportDaoImpl implements ArReportDao {
	private static final Logger logger = Logger.getLogger(ArReportDaoImpl.class);

	@Autowired
	SessionFactory sessionFactory;

	@Value("${ArReportDaoImpl.getContactList}")
	private transient String getContactListSql;

	@Value("${ArReportDaoImpl.getHistoricalPrincipals}")
	private transient String getHistoricalPrincipalsSql;

	@Value("${ArReportDaoImpl.getArAllegationsByStage}")
	private transient String getArAllegationsByStageSql;

	@Value("${ArReportDaoImpl.getArSafetyAssmtsByStage}")
	private transient String getArSafetyAssmtsByStageSql;

	@Value("${ArReportDaoImpl.getArSafetyFactorsByStage}")
	private transient String getArSafetyFactorsByStageSql;

	@Value("${ArReportDaoImpl.getArSafetyAssmtAreasAll}")
	private transient String getArSafetyAssmtAreasAllSql;

	@Value("${ArReportDaoImpl.selectServiceReferralsStage}")
	private transient String selectServiceReferralsStageSql;

	@Value("${ArReportDaoImpl.selectServiceReferralsServRef}")
	private transient String selectServiceReferralsServRefSql;

	@Value("${ArReportDaoImpl.getSafetyPlacements}")
	private transient String getSafetyPlacementsSql;

	@Value("${ArReportDaoImpl.selectARConclusion}")
	private transient String selectARConclusionSql;

	@Value("${ArReportDaoImpl.getMethFromIntake}")
	private transient String getMethFromIntakeSql;

	@Value("${ArReportDaoImpl.getSafetyAssmtInfo}")
	private transient String getSafetyAssmtInfoSql;

	@Value("${ArReportDaoImpl.getClosureApproval}")
	private transient String getClosureApprovalSql;

	@Value("${ArReportDaoImpl.getRelationships}")
	private transient String getRelationshipsSql;

	@Value("${ArReportDaoImpl.getMrefStatus}")
	private transient String getMrefStatusSql;

	@Value("${ArReportDaoImpl.getStageMergeInfo}")
	private transient String getStageMergeInfoSql;

	@Value("${ArReportDaoImpl.getIntakeAllegations}")
	private transient String getIntakeAllegationsSql;

	@Value("${ArReportDaoImpl.getPrnCharacteristics}")
	private transient String getPrnCharacteristicsSql;

	@Value("${ArReportDaoImpl.getPlanCompDateFam}")
	private transient String getPlanCompDateFamSql;

	@Value("${ArReportDaoImpl.getPlanCompDateSaf}")
	private transient String getPlanCompDateSafSql;

	@Value("${ArReportDaoImpl.getPriorStage}")
	private transient String getPriorStageSql;

	@Value("${ArReportDaoImpl.getARSafetyAssmt}")
	private String getARSafetyAssmtSql;
	
	@Value("${ArReportDaoImpl.getSdmSafetyRiskAssessments}")
	private String getSdmSafetyRiskAssessmentsSql;

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	ArHelperDao arHelperDao;

	@Autowired
	LookupDao lookupDao;

	/**
	 * Method Name: getContactList Method Description: Retrieves all the
	 * contacts for A-R stage, as well as the mref indicator from the case
	 * Legacy Dao: ARFormsDao.getContactList
	 * 
	 * @param idStage
	 * @param mergedStages
	 * @return List<ContactDto>
	 */
	@Override
	public List<ContactDto> getContactList(Long idStage, String mergedStages) {
		if (StringUtils.isBlank(mergedStages)) {
			return new ArrayList<ContactDto>();
		}
		StringBuilder sb = new StringBuilder();
		sb.append(getContactListSql);
		sb.append(" AND T.ID_CONTACT_STAGE IN (");
		sb.append(mergedStages);
		sb.append(" ) ORDER BY dtContactOccurred");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sb.toString())
				.addScalar("idContactStage", StandardBasicTypes.LONG)
				.addScalar("dtContactOccurred", StandardBasicTypes.DATE)
				.addScalar("cdContactPurpose", StandardBasicTypes.STRING)
				.addScalar("dtContactApprv", StandardBasicTypes.DATE)
				.addScalar("indEmergency", StandardBasicTypes.STRING)
				.addScalar("indSafPlanComp", StandardBasicTypes.STRING)
				.addScalar("indFamPlanComp", StandardBasicTypes.STRING)
				.addScalar("narrativeBlob", StandardBasicTypes.BLOB)
				.addScalar("idTemplate", StandardBasicTypes.LONG)
				.setParameter("idStage", idStage).setResultTransformer(Transformers.aliasToBean(ContactDto.class));
		List<ContactDto> resultList = (List<ContactDto>) query.list();

		if (!ObjectUtils.isEmpty(resultList)) {
			resultList.forEach(contactDto -> {
				if (!ObjectUtils.isEmpty(contactDto.getNarrativeBlob()))
					try {
						contactDto.setNarrative(TypeConvUtil.getNarrativeData(contactDto.getNarrativeBlob().getBinaryStream()));
						contactDto.setNarrativeBlob(null);
					} catch (SQLException e) {
						DataLayerException dataLayerException = new DataLayerException(e.getMessage());
						dataLayerException.initCause(e);
						throw dataLayerException;
					}
			});
		}

		return resultList;
	}
	
	

	/**
	 * Method Name: getHistoricalPrincipals Method Description:Returns
	 * historical list of principals (principals in other stages). When looking
	 * for 'other INV', we also make sure we don't pull in the INV immediately
	 * following this A-R Legacy Dao: ARFormsDao.getHistoricalPrincipals
	 * 
	 * @param idStage
	 * @return List<ArPrincipalsHistoryDto>
	 */
	@Override
	public List<ArPrincipalsHistoryDto> getHistoricalPrincipals(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getHistoricalPrincipalsSql)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("dtIntake", StandardBasicTypes.DATE).addScalar("riskFinding", StandardBasicTypes.STRING)
				.addScalar("cdStage", StandardBasicTypes.STRING).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(ArPrincipalsHistoryDto.class));
		List<ArPrincipalsHistoryDto> resultList = (List<ArPrincipalsHistoryDto>) query.list();
		return resultList;
	}

	/**
	 * Method Name: getArAllegationsByStage Method Description: Gets allegations
	 * for A-R and INV stages for a given person/stage Legacy Dao:
	 * ARFormsDao.getArAllegationsByStage
	 * 
	 * @param idStage
	 * @return List<FacilityAllegationInfoDto>
	 */
	@Override
	public List<FacilityAllegationInfoDto> getArAllegationsByStage(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getArAllegationsByStageSql)
				.addScalar("cdAllegType", StandardBasicTypes.STRING).addScalar("idVictim", StandardBasicTypes.LONG)
				.addScalar("idAllegedPerpetrator", StandardBasicTypes.LONG)
				.addScalar("cdAllegDisposition", StandardBasicTypes.STRING)
				.addScalar("cdAllegSeverity", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idAllegationStage", StandardBasicTypes.LONG)
				.addScalar("cdAllegIncidentStage", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(FacilityAllegationInfoDto.class));
		List<FacilityAllegationInfoDto> resultList = (List<FacilityAllegationInfoDto>) query.list();
		return resultList;
	}

	/**
	 * Method Name: getArSafetyAssmtsByStage Method Description: Gets Safety
	 * Assessments for Alternative Response stage Legacy Dao:
	 * ARSafetyAssmtDAO.getArSafetyAssmtsByStage
	 * 
	 * @param idStage
	 * @return List<ARSafetyAssmtValueDto>
	 */
	@Override
	public List<ARSafetyAssmtValueDto> getArSafetyAssmtsByStage(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getArSafetyAssmtsByStageSql)
				.addScalar("idArSafetyAssmt", StandardBasicTypes.INTEGER)
				.addScalar("idCase", StandardBasicTypes.INTEGER).addScalar("idStage", StandardBasicTypes.INTEGER)
				.addScalar("idEvent", StandardBasicTypes.INTEGER).addScalar("version", StandardBasicTypes.INTEGER)
				.addScalar("indAssmtType", StandardBasicTypes.STRING)
				.addScalar("furtherAssmt", StandardBasicTypes.STRING)
				.addScalar("immediateAction", StandardBasicTypes.STRING)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(ARSafetyAssmtValueDto.class));
		List<ARSafetyAssmtValueDto> resultList = (List<ARSafetyAssmtValueDto>) query.list();
		return resultList;
	}

	/**
	 * Method Name: getArSafetyFactorsByStage Method Description: Gets Safety
	 * Factors for Alternative Response stage Legacy Dao:
	 * ARSafetyAssmtDAO.getArSafetyFactorsByStage
	 * 
	 * @param idStage
	 * @return List<ARSafetyAssmtFactorValueDto>
	 */
	@Override
	public List<ARSafetyAssmtFactorValueDto> getArSafetyFactorsByStage(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getArSafetyFactorsByStageSql)
				.addScalar("idArSafetyFactor", StandardBasicTypes.INTEGER)
				.addScalar("idArSafetyAssmt", StandardBasicTypes.INTEGER)
				.addScalar("response", StandardBasicTypes.STRING).addScalar("idFactor", StandardBasicTypes.INTEGER)
				.addScalar("idFactorInitial", StandardBasicTypes.INTEGER)
				.addScalar("version", StandardBasicTypes.INTEGER).addScalar("indAssmtType", StandardBasicTypes.STRING)
				.addScalar("factorOrder", StandardBasicTypes.INTEGER).addScalar("idArea", StandardBasicTypes.INTEGER)
				.addScalar("idFactorDep", StandardBasicTypes.INTEGER)
				.addScalar("factorDepVal", StandardBasicTypes.STRING)
				.addScalar("indVertical", StandardBasicTypes.STRING).addScalar("factor", StandardBasicTypes.STRING)
				.addScalar("indFactor2", StandardBasicTypes.STRING)
				.addScalar("indFactorType", StandardBasicTypes.STRING)
				.addScalar("indRequiredSave", StandardBasicTypes.STRING)
				.addScalar("indRequiredSubmit", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(ARSafetyAssmtFactorValueDto.class));
		List<ARSafetyAssmtFactorValueDto> resultList = (List<ARSafetyAssmtFactorValueDto>) query.list();
		return resultList;
	}

	/**
	 * Method Name: getArSafetyAssmtAreasAll Method Description: Gets Safety
	 * Areas for Alternative Response stage Legacy Dao:
	 * ARSafetyAssmtDAO.getArSafetyAssmtAreasAll
	 * 
	 * @param idStage
	 * @return List<ARSafetyAssmtAreaValueDto>
	 */
	@Override
	public List<ARSafetyAssmtAreaValueDto> getArSafetyAssmtAreasAll(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getArSafetyAssmtAreasAllSql)
				.addScalar("idArea", StandardBasicTypes.INTEGER).addScalar("idAreaInitial", StandardBasicTypes.INTEGER)
				.addScalar("area", StandardBasicTypes.STRING).addScalar("areaOrder", StandardBasicTypes.INTEGER)
				.addScalar("indAssmtType", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(ARSafetyAssmtAreaValueDto.class));
		List<ARSafetyAssmtAreaValueDto> resultList = (List<ARSafetyAssmtAreaValueDto>) query.list();
		for (ARSafetyAssmtAreaValueDto result : resultList) {
			result.setIdStage(idStage.intValue());
		}
		return resultList;
	}

	/**
	 * Method Name: selectServiceReferrals Method Description: Method to
	 * retrieve service referrals for a stage ID or service referral ID. Legacy
	 * Dao: ArServReferralDao.selectServiceReferrals
	 * 
	 * @param idStage
	 * @param idServRef
	 * @return List<ArServiceReferralsDto>
	 */
	@Override
	public List<ArServiceReferralsDto> selectServiceReferrals(Long idStage, Long idServRef) {
		Query query = null;
		if (ServiceConstants.ZERO != idServRef) {
			query = sessionFactory.getCurrentSession().createSQLQuery(selectServiceReferralsServRefSql)
					.addScalar("idArServRefChklist", StandardBasicTypes.LONG)
					.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
					.addScalar("idCase", StandardBasicTypes.LONG).addScalar("indReferral", StandardBasicTypes.STRING)
					.addScalar("cdPersRef", StandardBasicTypes.STRING).addScalar("cdSrType", StandardBasicTypes.STRING)
					.addScalar("cdSrSubtype", StandardBasicTypes.STRING)
					.addScalar("dtReferral", StandardBasicTypes.DATE)
					.addScalar("txtComments", StandardBasicTypes.STRING)
					.addScalar("cdFinalOutcome", StandardBasicTypes.STRING)
					.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
					.addScalar("dtCreated", StandardBasicTypes.DATE)
					.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
					.addScalar("dtLastUpdate", StandardBasicTypes.DATE).setParameter("idServRef", idServRef)
					.setResultTransformer(Transformers.aliasToBean(ArServiceReferralsDto.class));
		} else if (ServiceConstants.ZERO != idStage) {
			query = sessionFactory.getCurrentSession().createSQLQuery(selectServiceReferralsStageSql)
					.addScalar("idArServRefChklist", StandardBasicTypes.LONG)
					.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
					.addScalar("idCase", StandardBasicTypes.LONG).addScalar("indReferral", StandardBasicTypes.STRING)
					.addScalar("cdPersRef", StandardBasicTypes.STRING).addScalar("cdSrType", StandardBasicTypes.STRING)
					.addScalar("cdSrSubtype", StandardBasicTypes.STRING)
					.addScalar("dtReferral", StandardBasicTypes.DATE)
					.addScalar("txtComments", StandardBasicTypes.STRING)
					.addScalar("cdFinalOutcome", StandardBasicTypes.STRING)
					.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
					.addScalar("dtCreated", StandardBasicTypes.DATE)
					.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
					.addScalar("dtLastUpdate", StandardBasicTypes.DATE).setParameter("idStage", idStage)
					.setResultTransformer(Transformers.aliasToBean(ArServiceReferralsDto.class));
		}
		List<ArServiceReferralsDto> resultList = (List<ArServiceReferralsDto>) query.list();
		return resultList;
	}

	/**
	 * Method Name: getSafetyPlacements Method Description: Gets safety
	 * placement information for given stage for A-R report and CpsInvReport
	 * Legacy Dao: ARFormsDao.getSafetyPlacements
	 * 
	 * @param idStage
	 * @return List<PCSPDto>
	 */
	@Override
	public List<PCSPDto> getSafetyPlacements(Long idStage) {		
		List<PCSPDto> resultList = new ArrayList<PCSPDto>();
		if(!ObjectUtils.isEmpty(idStage))
		{
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getSafetyPlacementsSql)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("nmPersCargvrFull", StandardBasicTypes.STRING).addScalar("dtStart", StandardBasicTypes.DATE)
				.addScalar("cdStatus", StandardBasicTypes.STRING).addScalar("dtEnd", StandardBasicTypes.DATE)
				.addScalar("cdEndRsn", StandardBasicTypes.STRING)
				.addScalar("indCargvrManual", StandardBasicTypes.STRING)
				.addScalar("pCSPComments", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(PCSPDto.class));
		resultList = (List<PCSPDto>) query.list();
		}
		return resultList;
	}

	/**
	 * Method Name: selectARConclusion Method Description: Gets Alternative
	 * Response conclusion Legacy Dao: ARConclusionDao.selectARConclusion
	 * 
	 * @param idStage
	 * @return ArInvCnclsnDto
	 */
	@Override
	public ArInvCnclsnDto selectARConclusion(Long idStage) {
		
		String feb2018Dt = null;
		Date creldateFeb2018 = null;
		try {
			feb2018Dt = lookupDao.simpleDecode(ServiceConstants.CRELDATE, ServiceConstants.CRELDATE_FEB_2018);
			creldateFeb2018 = DateUtils.toJavaDateFromInput(feb2018Dt);
		} catch (Exception e) {
			new ServiceLayerException(e.getMessage());
		}		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(selectARConclusionSql)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("dtBegun", StandardBasicTypes.DATE)
				.addScalar("dtCompleted", StandardBasicTypes.DATE)
				.addScalar("cdAROverallDisposition", StandardBasicTypes.STRING)
				.addScalar("indParentGivenGuide", StandardBasicTypes.STRING)
				.addScalar("indParentsLivingOutside", StandardBasicTypes.STRING)
				.addScalar("indAbsentParentGuide", StandardBasicTypes.STRING)
				.addScalar("indVrblWrtnNotifRights", StandardBasicTypes.STRING)
				.addScalar("indCopyGuideCpi", StandardBasicTypes.STRING)
				.addScalar("indNotifRightsUpld", StandardBasicTypes.STRING)
				.addScalar("indMultiplePersonsFound", StandardBasicTypes.STRING)
				.addScalar("indMultiplePersonsMerged", StandardBasicTypes.STRING)
				.addScalar("indMeth", StandardBasicTypes.STRING).addScalar("indFTMOffered", StandardBasicTypes.STRING)
				.addScalar("indFTMOccured", StandardBasicTypes.STRING)
				.addScalar("indLegalOrdinanceRequired", StandardBasicTypes.STRING)
				.addScalar("cdFamilyIncome", StandardBasicTypes.STRING).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.addScalar("indArEaEligible", StandardBasicTypes.STRING)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(ArInvCnclsnDto.class));
		ArInvCnclsnDto result = (ArInvCnclsnDto) query.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(result)) {
			query = sessionFactory.getCurrentSession().createSQLQuery(getMethFromIntakeSql).setParameter("idStage",
					idStage);
			result.setIndMethAllgdLongake((String) query.uniqueResult());
			if (DateUtils.isBefore(result.getDtCreated(), creldateFeb2018)) { // FEB_2018_IMPACT 2.0
				query = sessionFactory.getCurrentSession().createSQLQuery(getSafetyAssmtInfoSql)
						.addScalar("response", StandardBasicTypes.STRING).addScalar("idFactor", StandardBasicTypes.INTEGER)
						.setParameter("idCase", result.getIdCase())
						.setParameter("indAssmtType", ServiceConstants.AR_CLOSURE_SAFETY_ASSESSMENT_INDICATOR)
						.setParameter("idFactor76", ServiceConstants.ID_FACTOR_76)
						.setParameter("idFactor77", ServiceConstants.ID_FACTOR_77)
						.setParameter("idFactor78", ServiceConstants.ID_FACTOR_78)
						.setParameter("idFactor79", ServiceConstants.ID_FACTOR_79)
						.setResultTransformer(Transformers.aliasToBean(ARSafetyAssmtFactorValueDto.class));
				List<ARSafetyAssmtFactorValueDto> safetyList = (List<ARSafetyAssmtFactorValueDto>) query.list();
				for (ARSafetyAssmtFactorValueDto safetyDto : safetyList) {
					if (ServiceConstants.ID_FACTOR_76 == safetyDto.getIdFactor().longValue()) {
						result.setImmediateAction(safetyDto.getResponse());
					}
					if (ServiceConstants.ID_FACTOR_77 == safetyDto.getIdFactor().longValue()) {
						result.setChildSafety(safetyDto.getResponse());
					}
					if (ServiceConstants.ID_FACTOR_78 == safetyDto.getIdFactor().longValue()) {
						result.setFinalAssessmentConclusion(safetyDto.getResponse());
					}
					if (ServiceConstants.ID_FACTOR_79 == safetyDto.getIdFactor().longValue()) {
						result.setReasonRiskFindings(safetyDto.getResponse());
					}
				}
				if(arHelperDao.isRiskIndicated(result.getIdCase())>ServiceConstants.ZERO)
				{
					result.setIsRiskIndicated(true);
				}
			}
		}
		return result;
	}

	/**
	 * Method Name: getClosureApproval Method Description: Gets the stage
	 * closure date from approval so that it has a timestamp portion Legacy Dao:
	 * ARFormsDao.getClosureApproval
	 * 
	 * @param idEvent
	 * @return Date
	 */
	@Override
	public Date getClosureApproval(Long idEvent) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getClosureApprovalSql)
				.addScalar("dtApproversDeterm", StandardBasicTypes.DATE).setParameter("idEvent", idEvent);
		Date dtApproversDeterm = (Date) query.uniqueResult();
		return dtApproversDeterm;
	}

	/**
	 * Method Name: getRelationships Method Description: Returns a list of
	 * relationships Legacy Dao: ARFormsDao.getRelationships
	 * 
	 * @param idStage
	 * @param dtMaxComp
	 * @return List<ArRelationshipsDto>
	 */
	@Override
	public List<ArRelationshipsDto> getRelationships(Long idStage, Date dtMaxComp) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getRelationshipsSql)
				.addScalar("idPerson1", StandardBasicTypes.LONG).addScalar("nmPerson1Full", StandardBasicTypes.STRING)
				.addScalar("idPerson2", StandardBasicTypes.LONG).addScalar("nmPerson2Full", StandardBasicTypes.STRING)
				.addScalar("decodeCreltype", StandardBasicTypes.STRING)
				.addScalar("decodeCfamlrel", StandardBasicTypes.STRING)
				.addScalar("decodeCrelling", StandardBasicTypes.STRING)
				.addScalar("decodeCreldsep", StandardBasicTypes.STRING).addScalar("status", StandardBasicTypes.STRING)
				.setParameter("idStage", idStage).setParameter("dtMaxComp", DateUtils.stringDt(dtMaxComp))
				.setResultTransformer(Transformers.aliasToBean(ArRelationshipsDto.class));
		List<ArRelationshipsDto> resultList = (List<ArRelationshipsDto>) query.list();
		for (ArRelationshipsDto result : resultList) {
			StringBuilder sb = new StringBuilder();
			if (StringUtils.isNotBlank(result.getDecodeCreltype())) {
				sb.append(result.getDecodeCreltype());
				sb.append(" ");
			}
			if (StringUtils.isNotBlank(result.getDecodeCfamlrel())) {
				sb.append(result.getDecodeCfamlrel());
				sb.append(" ");
			}
			if (StringUtils.isNotBlank(result.getDecodeCrelling())) {
				sb.append(result.getDecodeCrelling());
				sb.append(" ");
			}
			if (StringUtils.isNotBlank(result.getDecodeCreldsep())) {
				sb.append(result.getDecodeCreldsep());
			}
			result.setRelationship(sb.toString());
		}
		return resultList;
	}

	/**
	 * Method Name: getMrefStatus Method Description: Returns the String mref
	 * status, used by A-R report Legacy Dao: ARFormsDao.getMrefStatus
	 * 
	 * @param idStage
	 * @return String
	 */
	@Override
	public String getMrefStatus(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getMrefStatusSql).setParameter("idStage",
				idStage);
		return (String) query.list().get(0);
	}

	/**
	 * Method Name: getStageMergeInfo Method Description: Returns the INT prior
	 * stage, used by A-R report Legacy Dao: ARFormsDao.getPriorIntStage
	 * 
	 * @param idStage
	 * @return Long
	 */
	@Override
	public Long getStageMergeInfo(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getStageMergeInfoSql).setParameter("idStage",
				idStage);
		List<BigDecimal> list = (List<BigDecimal>) query.list();
		return ObjectUtils.isEmpty(list) ? 0L : (list.get(0)).longValue();
	}

	/**
	 * Method Name: getIntakeAllegations Method Description: Retrieves the
	 * intake allegations for given stage ID Legacy Dao:
	 * ARFormsDao.getIntakeAllegations
	 * 
	 * @param idStage
	 * @return List<AllegationDetailDto>
	 */
	@Override
	public List<AllegationDetailDto> getIntakeAllegations(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getIntakeAllegationsSql)
				.addScalar("scrPersVictim", StandardBasicTypes.STRING)
				.addScalar("cdAllegType", StandardBasicTypes.STRING)
				.addScalar("scrAllegPerp", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(AllegationDetailDto.class));
		List<AllegationDetailDto> resultList = (List<AllegationDetailDto>) query.list();
		return resultList;
	}

	/**
	 * Method Name: getPrnCharacteristicsByStage Method Description: This
	 * returns the prn characteristics for the input stage Legacy Dao:
	 * CharacteristicsDao.getPrnCharacteristicsByStage
	 * 
	 * @param idStage
	 *            List<CharacteristicsDto>
	 * @return
	 */
	@Override
	public List<CharacteristicsDto> getPrnCharacteristicsByStage(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPrnCharacteristicsSql)
				.addScalar("idcharacId", StandardBasicTypes.LONG).addScalar("idpersonId", StandardBasicTypes.LONG)
				.addScalar("cdCharacCategory", StandardBasicTypes.STRING)
				.addScalar("cdCharacCode", StandardBasicTypes.STRING)
				.addScalar("dtCharacStart", StandardBasicTypes.DATE).addScalar("dtCharacEnd", StandardBasicTypes.DATE)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("cdStatus", StandardBasicTypes.STRING)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(CharacteristicsDto.class));
		List<CharacteristicsDto> resultList = (List<CharacteristicsDto>) query.list();
		return resultList;
	}

	/**
	 * Method Name: getPlanCompletionDate Method Description: Based on the plan
	 * type parameter, this method retrieves initial safety plan completion date
	 * or initial family plan completion date from contacts Legacy Dao:
	 * ARServReferralDao.getPlanCompletionDate
	 * 
	 * @param idStage
	 * @param planType
	 * @return
	 */
	@Override
	public Date getPlanCompletionDate(Long idStage, String planType) {
		String sql = getPlanCompDateFamSql;
		if (ServiceConstants.INI_SAF_PLAN_AR.equalsIgnoreCase(planType)) {
			sql = getPlanCompDateSafSql;
		}
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql).setParameter("idStage", idStage);
		Date occurred = (Date) query.uniqueResult();
		return occurred;
	}

	/**
	 * Method Name: getPriorStage Method Description: Returns details for the
	 * stage prior to the given stage as indicated by the STAGE_LINK table
	 * Legacy Dao: CaseUtility.getPriorStage
	 * 
	 * @param idStage
	 * @return StageDto
	 */
	@Override
	public StageDto getPriorStage(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPriorStageSql)
				.addScalar("idSituation", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("cdStage", StandardBasicTypes.STRING).addScalar("cdStageType", StandardBasicTypes.STRING)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("cdStageClassification", StandardBasicTypes.STRING)
				.addScalar("dtStageStart", StandardBasicTypes.DATE)
				.addScalar("indStageClose", StandardBasicTypes.STRING)
				.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("idUnit", StandardBasicTypes.LONG).addScalar("nmCase", StandardBasicTypes.STRING)
				.addScalar("dtStageClose", StandardBasicTypes.DATE).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(StageDto.class));
		return (StageDto) query.list().get(0);
	}

	/**
	 * Method Name: getARSafetyAssmt Method Description:This method is called
	 * from display method in SafetyAssmtConversation if the page has been
	 * previously saved. It retrieves back all the responses
	 * 
	 * @param idStage
	 * @param cdAssmtType
	 * @param idUser
	 * @return ARSafetyAssmtValueDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ARSafetyAssmtValueDto getARSafetyAssmt(Integer idStage, String cdAssmtType, Integer idUser)
			throws DataNotFoundException {
		List<ARSafetyAssmtValueDto> arSafetyAssmtValueDtoList = new ArrayList<ARSafetyAssmtValueDto>();
		arSafetyAssmtValueDtoList = (List<ARSafetyAssmtValueDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getARSafetyAssmtSql).addScalar("idArSafetyAssmt", StandardBasicTypes.INTEGER)
				.addScalar("idEvent", StandardBasicTypes.INTEGER).addScalar("idStage", StandardBasicTypes.INTEGER)
				.addScalar("idCase", StandardBasicTypes.INTEGER)
				.addScalar("txtImmediateAction", StandardBasicTypes.STRING)
				.addScalar("txtFurtherAssmt", StandardBasicTypes.STRING)
				.addScalar("nbrVersion", StandardBasicTypes.INTEGER)
				.addScalar("indAssmtType", StandardBasicTypes.STRING)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setParameter("indAssmtType", cdAssmtType).setParameter("nbrVersion", ServiceConstants.NBR_VERSION)
				.setResultTransformer(Transformers.aliasToBean(ARSafetyAssmtValueDto.class)).list();
		ARSafetyAssmtValueDto arSafetyAssmtValueDto = arSafetyAssmtValueDtoList.get(0);
		arSafetyAssmtValueDto.setVersion(ServiceConstants.NBR_VERSION);
		if (TypeConvUtil.isNullOrEmpty(arSafetyAssmtValueDtoList)) {
			throw new DataNotFoundException(messageSource.getMessage("ARSafetyAssmtDaoImpl.notFound", null, Locale.US));
		}

		return arSafetyAssmtValueDto;
	}
	
	
	/**
	 * Method Name: getSdmSafetyRiskAssessments Method Description: This
	 * returns the CPS SA and CPS RA details for AR Report Form for the input stage 	 * 
	 * @param idStage
	 *            List<SdmSafetyRiskAssessmentsDto>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SdmSafetyRiskAssessmentsDto> getSdmSafetyRiskAssessments(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getSdmSafetyRiskAssessmentsSql)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("cdAssmtType", StandardBasicTypes.STRING)
				.addScalar("dtAssessed", StandardBasicTypes.DATE)
				.addScalar("dtAssmtCompleted", StandardBasicTypes.DATE)
				.addScalar("cdSafetyDecision", StandardBasicTypes.STRING)
				.addScalar("cdUnSafeDecision", StandardBasicTypes.STRING)
				.addScalar("txtAssmtDiscussion", StandardBasicTypes.STRING)
				.addScalar("cdFinalRiskLevel", StandardBasicTypes.STRING)
				.addScalar("txtDiscOverrideReason", StandardBasicTypes.STRING)
				.addScalar("txtHouseHoldName", StandardBasicTypes.STRING)
				.addScalar("idHouseholdEvent", StandardBasicTypes.LONG)
				.setParameter("pstage", idStage)
				.setResultTransformer(Transformers.aliasToBean(SdmSafetyRiskAssessmentsDto.class));	
		List<SdmSafetyRiskAssessmentsDto> resultList = (List<SdmSafetyRiskAssessmentsDto>) query.list();
		return resultList;
	}
	
}
