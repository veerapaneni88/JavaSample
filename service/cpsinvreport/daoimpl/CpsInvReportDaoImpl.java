package us.tx.state.dfps.service.cpsinvreport.daoimpl;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import oracle.jdbc.OracleTypes;
import us.tx.state.dfps.arinvconclusion.dto.ArEaEligibilityDto;
import us.tx.state.dfps.common.domain.CpsChecklist;
import us.tx.state.dfps.common.dto.CpsChecklistDto;
import us.tx.state.dfps.service.childplan.dao.ChildServicePlanFormDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contact.dto.ContactNarrGuideDto;
import us.tx.state.dfps.service.contacts.dao.ContactOccuredDao;
import us.tx.state.dfps.service.cpsinv.dto.CpsChecklistItemDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvAllegDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvComDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvContactSdmSafetyAssessDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvCrimHistDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvIntakePersonPrincipalDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvPrincipalDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvReportIntakeDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvReportMergedDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvRiskDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvSdmSafetyRiskDto;
import us.tx.state.dfps.service.cpsinv.dto.ServRefDto;
import us.tx.state.dfps.service.cpsinvreport.dao.CpsInvReportDao;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.investigation.dao.SrvreferralslDao;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactStageDiDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactStageDoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:all dao
 * method declarations for CPSInvReport, these methods are specially converted
 * for CPSInvReport, different from the original DAM queries. Apr 4, 2018-
 * 11:11:02 AM © 2017 Texas Department of Family and Protective Services
 *  * **********  Change History *********************************
 * 07/13/2020 thompswa artf159096 : idHouseholdEvent needed for emergency assist
 * 01/31/2023 thompswa artf238090 PPM 73576 add idVictim,idAllegPrep to getAllegationsSafe.
 */
@Repository
public class CpsInvReportDaoImpl implements CpsInvReportDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private SrvreferralslDao srvReferralsDao;

	@Autowired
	private ContactOccuredDao contactOccurredDao;

	@Autowired
	private ChildServicePlanFormDao childServicePlanFormDao;
	
	@Autowired
	private EventDao eventDao;

	@Value("${CpsInvReportDaoImpl.getCriminalHistory}")
	private transient String getCriminalHistorySql;

	@Value("${CpsInvReportDaoImpl.getRemovals}")
	private transient String getRemovalsSql;

	@Value("${CpsInvReportDaoImpl.getPrincipals}")
	private transient String getPrincipalsSql;

	@Value("${CpsInvReportDaoImpl.getContactNamesp1}")
	private transient String getContactNamesp1Sql;

	@Value("${CpsInvReportDaoImpl.getPriorIntStage}")
	private transient String getPriorIntStageSql;

	@Value("${CpsInvReportDaoImpl.getMergeHistory}")
	private transient String getMergeHistorySql;

	@Value("${CpsInvReportDaoImpl.getMref}")
	private transient String getMrefSql;

	@Value("${CpsInvReportDaoImpl.getValidSdmRa}")
	private transient String getValidSdmRaSql;

	@Value("${CpsInvReportDaoImpl.getRiskAssessment}")
	private transient String getRiskAssessmentSql;

	@Value("${CpsInvReportDaoImpl.getSafetyAssessment}")
	private transient String getSafetyAssessmentSql;

	@Value("${CpsInvReportDaoImpl.getRiskArea}")
	private transient String getRiskAreaSql;

	@Value("${CpsInvReportDaoImpl.getRiskFactors}")
	private transient String getRiskFactorsSql;

	@Value("${CpsInvReportDaoImpl.getSafetyFactors}")
	private transient String getSafetyFactorsSql;

	@Value("${CpsInvReportDaoImpl.getSdmQa}")
	private transient String getSdmQaSql;

	@Value("${CpsInvReportDaoImpl.getPersonSplInfo}")
	private transient String getPersonSplInfoSql;

	@Value("${CpsInvReportDaoImpl.getPersonSplInfosub1}")
	private transient String getPersonSplInfosub1Sql;

	@Value("${CpsInvReportDaoImpl.getPersonSplInfosub2}")
	private transient String getPersonSplInfosub2Sql;

	@Value("${CpsInvReportDaoImpl.getPrincipalsHistory}")
	private transient String getPrincipalsHistorySql;

	@Value("${CpsInvReportDaoImpl.getIntakes}")
	private transient String getIntakesSql;

	@Value("${CpsInvReportDaoImpl.getContactsp1}")
	private transient String getContactsp1Sql;

	@Value("${CpsInvReportDaoImpl.getContactsp2Contact}")
	private transient String getContactsp2ContactSql;

	@Value("${CpsInvReportDaoImpl.getContactsp2Sdm}")
	private transient String getContactsp2SdmSql;

	@Value("${CpsInvReportDaoImpl.getContactsp2IndNarr}")
	private transient String getContactsp2IndNarrSql;

	@Value("${CpsInvReportDaoImpl.getContactsp2RsnClosed}")
	private transient String getContactsp2RsnClosedSql;

	@Value("${CpsInvReportDaoImpl.getContactsp2ClientTime}")
	private transient String getContactsp2ClientTimeSql;

	@Value("${CpsInvReportDaoImpl.getContactsp2NmFull}")
	private transient String getContactsp2NmFullSql;

	@Value("${CpsInvReportDaoImpl.getSdmSafetyAssessments}")
	private transient String getSdmSafetyAssessmentsSql;

	@Value("${CpsInvReportDaoImpl.getAllegations}")
	private transient String getAllegationsSql;

	@Value("${CpsInvReportDaoImpl.getAllegationsSafe}")
	private transient String getAllegationsSafeSql;

	@Value("${CpsInvReportDaoImpl.getPrnInvAllegations}")
	private transient String getPrnInvAllegationsSql;

	@Value("${CpsInvReportDaoImpl.getSdmSafetyRiskAssessments}")
	private transient String getSdmSafetyRiskAssessmentsSql;

	@Value("${CpsInvReportDaoImpl.getChecklistItems}")
	private transient String getChecklistItemsSql;

	@Value("${CpsInvReportDaoImpl.getEmergencyAssistance}")
	private transient String getEmergencyAssistanceSql;

	@Value("${CpsInvReportDaoImpl.getPriorArStages}")
	private transient String getPriorArStagesSql;

	@Value("${CpsInvReportDaoImpl.getContactGuide}")
	private transient String getContactGuideSql;

	@Value("${CpsInvReportDaoImpl.getInrDetails}")
	private transient String getInrDetailsSql;
	
	public CpsInvReportDaoImpl() {
		super();
	}

	private static final Logger logger = Logger.getLogger(CpsInvReportDaoImpl.class);
    private static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";
	private static final Set<String> SDM_AS_CONTACT_STAGES = new HashSet<String>(
			Arrays.asList(new String[] { CodesConstant.CSTAGES_INV, CodesConstant.CSTAGES_AR }));
	// strings to .replace() for contacts/sdms
	private static final String TABLE1 = "TABLE1";
	private static final String CSTAGE = "CSTAGE";
	private static final String CWORKER = "CWORKER";
	private static final String CDATE = "CDATE";
	private static final String CNARR = "CNARR";
	private static final String SCREATED = "SCREATED";
	private static final String STYPE = "STYPE";
	private static final String SDATE = "SDATE";
	private static final String SDECISION = "SDECISION";
	private static final String SACTION = "SACTION";
	private static final String SDISCUSSION = "SDISCUSSION";
	private static final String CSAFPLAN = "CSAFPLAN";
	private static final String CFAMPLAN = "CFAMPLAN";
	private static final String CTYPE = "CTYPE";
	private static final String CPURPOSE = "CPURPOSE";
	private static final String CMETHOD = "CMETHOD";
	private static final String CLOCATION = "CLOCATION";
	private static final String CATTEMPTED = "CATTEMPTED";
	private static final String CCLIENTTIME = "CCLIENTTIME";
	private static final String CCLIENTHOURS = "CCLIENTHOURS";
	private static final String CCLIENTMINS = "CCLIENTMINS";
	private static final String CANNOUNCED = "CANNOUNCED";
	private static final String CRSNCLOSED = "CRSNCLOSED";
	private static final String CSIBVIS = "CSIBVIS";
	private static final String CKIN = "CKIN";
	private static final String CRSO = "CRSO";
	private static final String CRECCONS = "CRECCONS";
	private static final String CAMTNEEDED = "CAMTNEEDED";
	private static final String CRSNAMTNE = "CRSNAMTNE";
	private static final String SNMHOUSEHOLD = "SNMHOUSEHOLD";
    private static final String CRSNEXCPTREQ = "CRSNEXCPTREQ"; // artf128844
	private static final String IDTEMPLATE = "A.ID_DOCUMENT_TEMPLATE";
	private static final String NARRATIVE = "A.NARRATIVE";

	// replacements for contacts
	private static final String CONTACT = "C";
	private static final String ID_CONTACT_STAGE = "ID_CONTACT_STAGE";
	private static final String NM_EMPLOYEE_FULL_REPLACE = "NM_EMPLOYEE_LAST||','||NM_EMPLOYEE_FIRST";
	private static final String DT_CONTACT_OCCURRED = "DT_CONTACT_OCCURRED";
	private static final String CONTACT_SCR_TYPE = "'CON'";
	private static final String IND_SAF_PLAN_COMP = "IND_SAF_PLAN_COMP";
	private static final String IND_FAM_PLAN_COMP = "IND_FAM_PLAN_COMP";
	private static final String CD_CONTACT_TYPE = "CD_CONTACT_TYPE";
	private static final String CD_CONTACT_PURPOSE = "CD_CONTACT_PURPOSE";
	private static final String CD_CONTACT_METHOD = "CD_CONTACT_METHOD";
	private static final String CD_CONTACT_LOCATION = "CD_CONTACT_LOCATION";
	private static final String IND_CONTACT_ATTEMPTED = "IND_CONTACT_ATTEMPTED";
	private static final String IND_ANNOUNCED = "IND_ANNOUNCED";
	private static final String CD_STAGE_REASON_CLOSED = "CD_STAGE_REASON_CLOSED";
	private static final String EST_CONTACT_HOURS = "EST_CONTACT_HOURS";
	private static final String EST_CONTACT_MINS = "EST_CONTACT_MINS";
	private static final String IND_SIBLING_VISIT = "IND_SIBLING_VISIT";
	private static final String TXT_KIN_CAREGIVER = "TXT_KIN_CAREGIVER";
	private static final String CD_RSN_SCROUT = "CD_RSN_SCROUT";
	private static final String IND_REC_CONS = "IND_REC_CONS";
	private static final String AMT_NEEDED = "AMT_NEEDED";
	private static final String CD_RSN_AMTNE = "CD_RSN_AMTNE";
    private static final String CD_FTF_EXCEPTION_RSN = "CD_FTF_EXCEPTION_RSN"; // artf128844

	// replacements for sdms
	private static final String CPS_SA = "A";
	private static final String ID_STAGE = "ID_STAGE";
	private static final String DT_ASSESSED = "DT_ASSESSED";
	private static final String DT_CREATED = "DT_CREATED";
	private static final String CD_ASSMT_TYPE = "CD_ASSMT_TYPE";
	private static final String DT_ASSMT_COMPLETED = "DT_ASSMT_COMPLETED";
	private static final String CD_SAFETY_DECISION = "CD_SAFETY_DECISION";
	private static final String CD_UNSAFE_DECISION_ACTION = "CD_UNSAFE_DECISION_ACTION";
	private static final String TXT_ASSMT_DISCUSSION = "TXT_ASSMT_DISCUSSION";
	private static final String ORDER_BY_DT_OCCURRED = " ORDER BY dtOccured ";

	// other strings/replacements
	private static final String NULL = "NULL";
	private static final String ZERO = "0";
	private static final String UNION = " UNION ";
	private static final String UNIONALL = " UNION ALL ";

	/**
	 * Method Name: getRemovals Method Description: convert the clsce2d/clsce3d to
	 * get removals.
	 * 
	 * @param idStage
	 * @return List<CpsInvComDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvComDto> getRemovals(Long idStage) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getRemovalsSql)
				.addScalar("dtRemoval", StandardBasicTypes.DATE).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(CpsInvComDto.class));
		return query.list();
	}

	/**
	 * Method Name: getContactNames Method Description: convert the clscded to get
	 * Contact Names.
	 * 
	 * @param stageString
	 * @return List<CpsInvComDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvComDto> getContactNames(String stageString) {
		List<Long> idStages = new ArrayList<>();
		if (!ObjectUtils.isEmpty(stageString)) {
			for (String idStage : stageString.split(",")) {
				idStages.add(Long.valueOf(idStage));
			}
		}
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getContactNamesp1Sql)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("ctype", StandardBasicTypes.STRING).setParameterList("stageString", idStages)
				.setResultTransformer(Transformers.aliasToBean(CpsInvComDto.class));
		return query.list();

	}

	/**
	 * Method Name: getContactNames Method Description: convert the clscded to get
	 * Contact Names.
	 * 
	 * @param stageString
	 * @return List<CpsInvComDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvComDto> getLogContactNames(List<CpsInvReportMergedDto> cpsInvReportMergedDtoList) {

		CpsInvReportMergedDto cpsInvReportMergedDto = new CpsInvReportMergedDto();
		cpsInvReportMergedDto.setStrMergedStages("0");

		if (!ObjectUtils.isEmpty(cpsInvReportMergedDtoList) && 0 < cpsInvReportMergedDtoList.size()) {
			for (CpsInvReportMergedDto cpsInvReportMergedDto1 : cpsInvReportMergedDtoList) {
				if (!ZERO.equals(cpsInvReportMergedDto1.getStrMergedStages())) {
					if (ZERO.equals(cpsInvReportMergedDto.getStrMergedStages())) {
						cpsInvReportMergedDto.setStrMergedStages(cpsInvReportMergedDto1.getStrMergedStages());
					} else {
						cpsInvReportMergedDto.setStrMergedStages(cpsInvReportMergedDto.getStrMergedStages() + ','
								+ cpsInvReportMergedDto1.getStrMergedStages());
					}
				}
			}
		}

		List<Long> idStages = new ArrayList<>();
		if (!ObjectUtils.isEmpty(cpsInvReportMergedDto.getStrMergedStages())) {
			for (String idStage : cpsInvReportMergedDto.getStrMergedStages().split(",")) {
				if (!ObjectUtils.isEmpty(idStage) && !"null".equalsIgnoreCase(idStage))
					idStages.add(Long.valueOf(idStage));
			}
		}

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getContactNamesp1Sql)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("ctype", StandardBasicTypes.STRING).setParameterList("stageString", idStages)
				.setResultTransformer(Transformers.aliasToBean(CpsInvComDto.class));
		return query.list();
	}

	/**
	 * Method Name: getPrincipals Method Description: convert the clsce1d to get
	 * stage principals.
	 * 
	 * @param idStage
	 * @return List<CpsInvPrincipalDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvPrincipalDto> getPrincipals(Long idStage) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPrincipalsSql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("nbrPersonAge", StandardBasicTypes.STRING)
				.addScalar("dtPersonBirth", StandardBasicTypes.DATE).addScalar("cdPersonSex", StandardBasicTypes.STRING)
				.addScalar("dtPersonDeath", StandardBasicTypes.DATE)
				.addScalar("cdPersonDeath", StandardBasicTypes.STRING)
				.addScalar("cdPersLang", StandardBasicTypes.STRING)
				.addScalar("cdPersEthnGrp", StandardBasicTypes.STRING).addScalar("persRace", StandardBasicTypes.STRING)
				.addScalar("cdEthn", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("txtStagePersNote", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("cdPersChar", StandardBasicTypes.STRING)
				.addScalar("addrPersStLn1", StandardBasicTypes.STRING)
				.addScalar("addrPersCity", StandardBasicTypes.STRING)
				.addScalar("cdPersState", StandardBasicTypes.STRING).addScalar("nbrPersId", StandardBasicTypes.STRING)
				.addScalar("persZip", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(CpsInvPrincipalDto.class));
		return query.list();
	}

	/**
	 * Method Name: getCriminalHistory Method Description: convert the clsce0d to
	 * get principals criminal history
	 * 
	 * @param idStage
	 * @return List<CpsInvCrimHistDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvCrimHistDto> getCriminalHistory(Long idStage) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getCriminalHistorySql)
				.addScalar("prnChistName", StandardBasicTypes.STRING)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("dtRecCheckCompl", StandardBasicTypes.DATE)
				.addScalar("cdRecCheckType", StandardBasicTypes.STRING)
				.addScalar("txtRecCheckCmmnts", StandardBasicTypes.STRING)
				.addScalar("txtCrimHistCmnts", StandardBasicTypes.STRING)
				.addScalar("cdCrimHistAction", StandardBasicTypes.STRING)
				.addScalar("cdRecCheckStatus", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idRecCheck", StandardBasicTypes.LONG).addScalar("idCrimHist", StandardBasicTypes.LONG)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(CpsInvCrimHistDto.class));
		return query.list();
	}

	/**
	 * Method Name: getPriorIntStages Method Description: convert clscd2d to get
	 * prior INT stage of INV(or of A-R if A-R is prior)
	 * 
	 * @param idStage
	 * @return Long
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long getPriorIntStage(Long idStage) {
		Long idStagePrior = 0l;
		Query query = (sessionFactory.getCurrentSession().createSQLQuery(getPriorIntStageSql)).setParameter("idStage",
				idStage);
		List<BigDecimal> idStagePriorList = (List<BigDecimal>) query.list();
		return (!ObjectUtils.isEmpty(idStagePriorList)) ? idStagePriorList.get(0).longValue() : idStagePrior;

	}

	/**
	 * Method Name: getMergeHistory Method Description: for CPSInvReport
	 * 
	 * @param idCase
	 * @return List<Long>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getMergeHistory(Long idCase) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getMergeHistorySql).setParameter("idCase",
				idCase);
		return query.list();
	}

	/**
	 * Method Name: getMref Method Description: From DAM for csece2d
	 * 
	 * @param idStage
	 * @return String
	 */
	@Override
	public String getMref(Long idStage) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getMrefSql).setParameter("idStage", idStage);
		return (String) query.list().get(0);
	}

	/**
	 * Method Name: getValidSdmRa Method Description: convert the csecfad to get sdm
	 * risk assmt
	 * 
	 * @param idStage
	 * @return String
	 */
	@Override
	public String getValidSdmRa(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getValidSdmRaSql).setParameter("idStage",
				idStage);
		if (query.list().size() == 0)
			return null;
		return (String) query.list().get(0);
	}

	/**
	 * Method Name: getRiskAssessment Method Description: convert the csesb6d to get
	 * risk_assessment.
	 * 
	 * @param idStage
	 * @return List<CpsInvRiskDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvRiskDto> getRiskAssessment(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getRiskAssessmentSql)
				.addScalar("cdRiskAssmtPurpose", StandardBasicTypes.STRING)
				.addScalar("cdRiskAssmtRiskFind", StandardBasicTypes.STRING)
				.addScalar("indRiskAssmtIntranet", StandardBasicTypes.STRING)
				.addScalar("nbrVersion", StandardBasicTypes.LONG)
				.addScalar("txtAbuseNeglSumm", StandardBasicTypes.STRING)
				.addScalar("txtAbuseNeglHistEff", StandardBasicTypes.STRING)
				.addScalar("indAbuseNeglSearchComp", StandardBasicTypes.STRING)
				.addScalar("indAbuseNeglHistFound", StandardBasicTypes.STRING)
				.addScalar("txtFindRational", StandardBasicTypes.STRING)
				.addScalar("indAbuseNeglPrevInv", StandardBasicTypes.STRING)
				.addScalar("txtCrimHistEff", StandardBasicTypes.STRING)
				.addScalar("txtExtentAbuseNegl", StandardBasicTypes.STRING)
				.addScalar("txtCircumAbuseNegl", StandardBasicTypes.STRING)
				.addScalar("txtChildFunc", StandardBasicTypes.STRING)
				.addScalar("txtParentDailyFunc", StandardBasicTypes.STRING)
				.addScalar("txtParentPrac", StandardBasicTypes.STRING)
				.addScalar("txtParentDiscpl", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(CpsInvRiskDto.class));
		return query.list();
	}

	/**
	 * Method Name: getSafetyAssessment Method Description: convert the csesb7d to
	 * get safety_assessement.
	 * 
	 * @param idStage
	 * @return List<CpsInvSdmSafetyRiskDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvSdmSafetyRiskDto> getSafetyAssessment(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getSafetyAssessmentSql)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("nbrVersion", StandardBasicTypes.LONG)
				.addScalar("cdSafetyDecision", StandardBasicTypes.STRING)
				.addScalar("indAbuseNeglHistCompl", StandardBasicTypes.STRING)
				.addScalar("txtAbuseNeglHistCompl", StandardBasicTypes.STRING)
				.addScalar("indInsuffSafety", StandardBasicTypes.STRING)
				.addScalar("txtDecisionRational", StandardBasicTypes.STRING)
				.addScalar("indChildPresentDanger", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(CpsInvSdmSafetyRiskDto.class));
		return new ArrayList<CpsInvSdmSafetyRiskDto>(query.list());
	}

	/**
	 * Method Name: getRiskArea Method Description: convert the clsce5d to get
	 * RISK_AREA text for a given idStage.
	 * 
	 * @param idStage
	 * @return List<CpsInvSdmSafetyRiskDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvSdmSafetyRiskDto> getRiskArea(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getRiskAreaSql)
				.addScalar("txtArea", StandardBasicTypes.STRING)
				.addScalar("nbrVersion", StandardBasicTypes.LONG)
				.addScalar("txtConcernScale", StandardBasicTypes.STRING)
				.addScalar("cdRiskAreaConcernScale", StandardBasicTypes.STRING)
				.addScalar("scrIndDispFac", StandardBasicTypes.STRING)
				.addScalar("idRiskArea", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("cdRiskArea", StandardBasicTypes.STRING)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(CpsInvSdmSafetyRiskDto.class));
		return query.list();
	}

	/**
	 * Method Name: getRiskFactors Method Description: convert the clscd9d to get
	 * RISK_FACTORS text for a given idStage.
	 * 
	 * @param idStage
	 * @return List<CpsInvSdmSafetyRiskDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvSdmSafetyRiskDto> getRiskFactors(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getRiskFactorsSql)
				.addScalar("cdRiskFac", StandardBasicTypes.STRING).addScalar("txtFac", StandardBasicTypes.STRING)
				.addScalar("cdRiskArea", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(CpsInvSdmSafetyRiskDto.class));
		return query.list();
	}

	/**
	 * Method Name: getsafetyFactors Method Description: convert the clscd8d to get
	 * SAFETY_AREA text for a given idStage.
	 * 
	 * @param idStage
	 * @return List<CpsInvSdmSafetyRiskDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvSdmSafetyRiskDto> getSafetyFactors(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getSafetyFactorsSql)
				.addScalar("nbrVersion", StandardBasicTypes.LONG).addScalar("txtArea", StandardBasicTypes.STRING)
				.addScalar("txtDiscussFac", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(CpsInvSdmSafetyRiskDto.class));
		return query.list();
	}

	/**
	 * Method Name: getsafetyFactors Method Description: From DAM CLSCDDD FOR SDM
	 * Safety assessment questions-answers by stageId
	 * 
	 * @param String
	 *            stageString
	 * @return List<CpsInvSdmSafetyRiskDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvSdmSafetyRiskDto> getSdmQa(String stageString) {
		List<Long> idStages = new ArrayList<>();
		if (!ObjectUtils.isEmpty(stageString)) {
			for (String idStage : stageString.split(",")) {
				idStages.add(Long.valueOf(idStage));
			}
		}
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getSdmQaSql)
				.addScalar("cdSaAnswer", StandardBasicTypes.STRING).addScalar("txtOtherDesc", StandardBasicTypes.STRING)
				.addScalar("cdSection", StandardBasicTypes.STRING).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("nbrOrder", StandardBasicTypes.LONG).setParameterList("stageString", idStages)
				.setResultTransformer(Transformers.aliasToBean(CpsInvSdmSafetyRiskDto.class));
		return query.list();
	}

	/**
	 * Method Name: getPersonSplInfo Method Description: Retrieves person name, and
	 * information from the stage_person_link record. Simple replacement for
	 * cint66d. role 'R' is reporter, 'C' is collateral
	 * 
	 * @param String
	 *            role, Long idStage
	 * @return List<CpsInvIntakePersonPrincipalDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvIntakePersonPrincipalDto> getPersonSplInfo(String role, Long idStage) {
		String querySql = null;
		if (ServiceConstants.TODO_ACTIONS_REMINDER.equals(role)) {
			querySql = getPersonSplInfoSql + getPersonSplInfosub1Sql;
		} else
			querySql = getPersonSplInfoSql + getPersonSplInfosub2Sql;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(querySql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("cdPersonSex", StandardBasicTypes.STRING)
				.addScalar("txtStagePersNote", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(CpsInvIntakePersonPrincipalDto.class));
		return query.list();
	}

	/**
	 * Method Name: getPrincipalsHistory Method Description: convert clscd7d
	 * 
	 * @param Long
	 *            idStage
	 * @return List<CpsInvIntakePersonPrincipalDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvIntakePersonPrincipalDto> getPrincipalsHistory(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPrincipalsHistorySql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("dtCpsInvstDtlIntake", StandardBasicTypes.DATE)
				.addScalar("riskFinal", StandardBasicTypes.STRING)
				.addScalar("indSdmRiskType", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(CpsInvIntakePersonPrincipalDto.class));
		return query.list();
	}

	/**
	 * Method Name: getIntakes Method Description: Wrapped the clscgad with clsc29d
	 * to get intakes for the merged inv stage list from cmsc0ad.
	 * 
	 * @param String
	 *            stageString
	 * @return List<CpsInvIntakePersonPrincipalDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvIntakePersonPrincipalDto> getIntakes(String stageString) {
		List<Long> idStages = new ArrayList<>();
		if (!ObjectUtils.isEmpty(stageString)) {
			for (String idStage : stageString.split(",")) {
				// Warranty Defect Fix - Null Pointer Check - 10825
				if(!idStage.equalsIgnoreCase("null"))
				{
				idStages.add(Long.valueOf(idStage));
				}
			}
		}
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getIntakesSql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idPriorStage", StandardBasicTypes.LONG)
				.addScalar("txtStagePersNote", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("cdStageType", StandardBasicTypes.STRING).addScalar("dtStageStart", StandardBasicTypes.DATE)
				.setParameterList("stageString", idStages)
				.setResultTransformer(Transformers.aliasToBean(CpsInvIntakePersonPrincipalDto.class));
		return query.list();
	}

	/**
	 * 
	 * Method Name: getContacts Method Description:Retrieves all the contacts for a
	 * stage, closed to merge stages, prior stages for A-R Report also.Wrapped the
	 * clscdbd stage string in with this for the merged stage contact/sdm list(from
	 * cmsc0ad)
	 * 
	 * @param allStages
	 * @return sdmSafetyAssessDtoList
	 */
	@SuppressWarnings("unchecked")
	public List<CpsInvContactSdmSafetyAssessDto> getContacts(List<CpsInvReportMergedDto> allStages) {
		List<CpsInvContactSdmSafetyAssessDto> sdmSafetyAssessDtoList = new ArrayList<CpsInvContactSdmSafetyAssessDto>();
		int count = 0;
		StringBuilder sql = new StringBuilder();
		if (!ObjectUtils.isEmpty(allStages)) {
			for (CpsInvReportMergedDto stagesVb : allStages) {
				if (!ZERO.equals(stagesVb.getStrMergedStages())) {
					sql.append(getSql(CONTACT, stagesVb, allStages.size() - ++count));
				}
			}
			sql.append(ORDER_BY_DT_OCCURRED);
		}
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString())
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("nmEmployeeFull", StandardBasicTypes.STRING).addScalar("dtOccured", StandardBasicTypes.DATE)
				.addScalar("cdContactType", StandardBasicTypes.STRING).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("dtAssmtCompl", StandardBasicTypes.DATE)
				.addScalar("cdSafetyDecision", StandardBasicTypes.STRING)
				.addScalar("cdUnsafeDecisionAction", StandardBasicTypes.STRING)
				.addScalar("txtAssmtDiscussion", StandardBasicTypes.STRING)
				.addScalar("cdContactPurpose", StandardBasicTypes.STRING)
				.addScalar("indNarr", StandardBasicTypes.STRING).addScalar("cdScrType", StandardBasicTypes.STRING)
				.addScalar("indSafPlanComp", StandardBasicTypes.STRING)
				.addScalar("indFamPlanComp", StandardBasicTypes.STRING)
				.addScalar("cdContactMethod", StandardBasicTypes.STRING)
				.addScalar("cdContactLocation", StandardBasicTypes.STRING)
				.addScalar("indContactAttempted", StandardBasicTypes.STRING)
				.addScalar("indClientTime", StandardBasicTypes.STRING)
				.addScalar("estContactHours", StandardBasicTypes.LONG)
				.addScalar("estContactMins", StandardBasicTypes.LONG)
				.addScalar("indAnnounced", StandardBasicTypes.STRING).addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("cdStageRsnCLosed", StandardBasicTypes.STRING)
				.addScalar("indSiblingVisit", StandardBasicTypes.STRING)
				.addScalar("kinCaregiver", StandardBasicTypes.STRING)
				.addScalar("cdRsnScrout", StandardBasicTypes.STRING).addScalar("indRecCons", StandardBasicTypes.STRING)
				.addScalar("amtNeeded", StandardBasicTypes.LONG).addScalar("cdRsnAmtne", StandardBasicTypes.STRING)
				.addScalar("householdName", StandardBasicTypes.STRING)
				.addScalar("idTemplate",StandardBasicTypes.LONG)
				.addScalar("narrativeBlob",StandardBasicTypes.BLOB)
				.addScalar("cdFtfExceptionReason", StandardBasicTypes.STRING) //artf129590
				.addScalar("dtStageClose", StandardBasicTypes.DATE) //QCR 62702
				.setResultTransformer(Transformers.aliasToBean(CpsInvContactSdmSafetyAssessDto.class));
		sdmSafetyAssessDtoList = (List<CpsInvContactSdmSafetyAssessDto>) query.list();
		if(!ObjectUtils.isEmpty(sdmSafetyAssessDtoList)){
			sdmSafetyAssessDtoList.forEach(sdmSADto -> {
				if (!ObjectUtils.isEmpty(sdmSADto.getNarrativeBlob()))
					try {
						sdmSADto.setNarrative(TypeConvUtil.getNarrativeData(sdmSADto.getNarrativeBlob().getBinaryStream()));
						sdmSADto.setNarrativeBlob(null);
					} catch (SQLException e) {
						DataLayerException dataLayerException = new DataLayerException(e.getMessage());
						dataLayerException.initCause(e);
						throw dataLayerException;
					}
			});
		}

		return sdmSafetyAssessDtoList;
	}

	@Override
	public CpsInvContactSdmSafetyAssessDto getInrContactFields(Long idEvent) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getInrDetailsSql)
				.addScalar("dtDTContactOccurred", StandardBasicTypes.DATE)
				.addScalar("dtNotification", StandardBasicTypes.DATE)
				.addScalar("idCaseworker", StandardBasicTypes.LONG)
				.addScalar("idSupervisor", StandardBasicTypes.LONG)
				.addScalar("idDirector", StandardBasicTypes.LONG)
				.addScalar("txtSummDiscuss", StandardBasicTypes.STRING)
				.addScalar("txtIdentfdSafetyConc", StandardBasicTypes.STRING)
				.addScalar("nmCaseworker", StandardBasicTypes.STRING)
				.addScalar("nmSupervisor", StandardBasicTypes.STRING)
				.addScalar("nmDirector",StandardBasicTypes.STRING)
				.setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(CpsInvContactSdmSafetyAssessDto.class));

    return (CpsInvContactSdmSafetyAssessDto) query.uniqueResult();
	}

	/**
	 * Builds a sql string. INV and A-R has SDM_AS_CONTACT_STAGES, other stages
	 * never have CPS_SA
	 *
	 * @param stagesVb
	 * @param limit
	 * @return sb.toString()
	 */
	private String getAppendSql(CpsInvReportMergedDto stagesVb, int limit) {
		StringBuilder sb = new StringBuilder();
		if (!SDM_AS_CONTACT_STAGES.contains(stagesVb.getCdStage())) {
			sb.append(stagesVb.getStrMergedStages()).append(" ) ");
			if (0 < limit) {
				sb.append(UNIONALL);
			}
		} else {
			// if A-R or INV, get the SDMs-as-contacts
			sb.append(stagesVb.getStrMergedStages()).append(" ) ").append(UNIONALL).append(getSql(CPS_SA, stagesVb, limit))
					.append(stagesVb.getStrMergedStages()).append(" ) ");
			if (0 < limit) {
				sb.append(UNIONALL);
			}
		}
		return sb.toString();
	}

	/**
	 * Builds a sql string for getContacts
	 *
	 * @param type
	 * @param stagesVb
	 * @param limit
	 * @return sb.toString()
	 */
	private String getSql(String type, CpsInvReportMergedDto stagesVb, int limit) {
		StringBuilder sb = new StringBuilder();

		if (CONTACT.equals(type)) {
			sb.append(getConReplace(getContactsp1Sql, stagesVb)).append(getContactsp2ContactSql)
					.append(getAppendSql(stagesVb, limit));
		} else {
			sb.append(getSdmReplace(getContactsp1Sql)).append(getContactsp2SdmSql);
		}
		return sb.toString();
	}

	/**
	 * gets substitutions for contacts
	 *
	 * @param sql
	 * @return
	 */
	private String getConReplace(String sql, CpsInvReportMergedDto stagesVb) {
		String returnSql = new StringBuilder().append(sql).toString().replace(TABLE1, CONTACT)
				.replace(CSTAGE, ID_CONTACT_STAGE).replace(CWORKER, NM_EMPLOYEE_FULL_REPLACE)
				.replace(CDATE, DT_CONTACT_OCCURRED).replace(CNARR, getContactsp2IndNarrSql).replace(SCREATED, NULL)
				.replace(STYPE, CONTACT_SCR_TYPE).replace(SDATE, NULL).replace(SDECISION, NULL).replace(SACTION, NULL)
				.replace(SDISCUSSION, NULL).replace(CSAFPLAN, IND_SAF_PLAN_COMP).replace(CFAMPLAN, IND_FAM_PLAN_COMP)
				.replace(CTYPE, CD_CONTACT_TYPE).replace(CPURPOSE, CD_CONTACT_PURPOSE)
				.replace(CMETHOD, CD_CONTACT_METHOD).replace(CLOCATION, CD_CONTACT_LOCATION)
				.replace(CATTEMPTED, IND_CONTACT_ATTEMPTED).replace(CCLIENTTIME, getContactsp2ClientTimeSql)
				.replace(CCLIENTHOURS, EST_CONTACT_HOURS).replace(CCLIENTMINS, EST_CONTACT_MINS)
				.replace(CANNOUNCED, IND_ANNOUNCED).replace(CSIBVIS, IND_SIBLING_VISIT).replace(CKIN, TXT_KIN_CAREGIVER)
				.replace(CRSO, CD_RSN_SCROUT).replace(CRECCONS, IND_REC_CONS).replace(CAMTNEEDED, AMT_NEEDED)
				.replace(CRSNAMTNE, CD_RSN_AMTNE).replace(SNMHOUSEHOLD, NULL)
                .replace(CRSNEXCPTREQ, CD_FTF_EXCEPTION_RSN).toString(); // artf129590

		if ("INT".equals(stagesVb.getCdStage())) {
			returnSql = returnSql.replace(CRSNCLOSED, getContactsp2RsnClosedSql);
		} else {
			returnSql = returnSql.replace(CRSNCLOSED, CD_STAGE_REASON_CLOSED);
		}
		return returnSql;
	}

	/**
	 * gets substitutions for sdms as contacts
	 *
	 * @param sql
	 * @return
	 */
	private String getSdmReplace(String sql) {
		return sql.replace(TABLE1, CPS_SA).replace(CSTAGE, CPS_SA + '.' + ID_STAGE).replace(CWORKER, NULL)
				.replace(CDATE, DT_ASSESSED).replace(CNARR, NULL).replace(SCREATED, CPS_SA + '.' + DT_CREATED)
				.replace(STYPE, CD_ASSMT_TYPE).replace(SDATE, DT_ASSMT_COMPLETED).replace(SDECISION, CD_SAFETY_DECISION)
				.replace(SACTION, CD_UNSAFE_DECISION_ACTION).replace(SDISCUSSION, TXT_ASSMT_DISCUSSION)
				.replace(CSAFPLAN, NULL).replace(CFAMPLAN, NULL).replace(CTYPE, NULL).replace(CPURPOSE, NULL)
				.replace(CMETHOD, NULL).replace(CLOCATION, NULL).replace(CATTEMPTED, NULL).replace(CCLIENTTIME, NULL)
				.replace(CCLIENTHOURS, NULL).replace(CCLIENTMINS, NULL).replace(CANNOUNCED, NULL)
				.replace(CRSNCLOSED, CD_STAGE_REASON_CLOSED).replace(CSIBVIS, NULL).replace(CKIN, NULL)
				.replace(CRSO, NULL).replace(CRECCONS, NULL).replace(CAMTNEEDED, NULL).replace(CRSNAMTNE, NULL)
				.replace(IDTEMPLATE, NULL).replace(NARRATIVE, NULL)
				.replace(SNMHOUSEHOLD, getContactsp2NmFullSql)
                .replace(CRSNEXCPTREQ, NULL ); // artf129590

	}

	/**
	 * Method Name: getSdmSafetyAssessments Method Description:convert the clssbad
	 * to get Sdm Safety Assessments.
	 * 
	 * @param Long
	 *            idStage
	 * @return List<CpsInvContactSdmSafetyAssessDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvContactSdmSafetyAssessDto> getSdmSafetyAssessments(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getSdmSafetyAssessmentsSql)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("dtAssessed", StandardBasicTypes.DATE).addScalar("cdAssmtType", StandardBasicTypes.STRING)
				.addScalar("dtAssmtCompl", StandardBasicTypes.DATE)
				.addScalar("cdSafetyDecision", StandardBasicTypes.STRING)
				.addScalar("cdUnsafeDecisionAction", StandardBasicTypes.STRING)
				.addScalar("txtAssmtDiscussion", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(CpsInvContactSdmSafetyAssessDto.class));
		return query.list();
	}

	/**
	 * Method Name: getAllegations Method Description:convert the cses90d to get
	 * allegations.
	 * 
	 * @param Long
	 *            idStage
	 * @return List<CpsInvAllegDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvAllegDto> getAllegations(Long idStage) {
//		artf205003: setting fis=rst, last and middle names of victim and prepretrator
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getAllegationsSql)
				.addScalar("nmVicLast", StandardBasicTypes.STRING).addScalar("nmVicFirst", StandardBasicTypes.STRING)
				.addScalar("nmVicMiddle", StandardBasicTypes.STRING).addScalar("nmVicFull", StandardBasicTypes.STRING)
				.addScalar("nmPerpLast", StandardBasicTypes.STRING).addScalar("nmPerpFirst", StandardBasicTypes.STRING)
				.addScalar("nmPerpMiddle", StandardBasicTypes.STRING).addScalar("nmPerpFull", StandardBasicTypes.STRING)
				.addScalar("cdAllegType", StandardBasicTypes.STRING).addScalar("cdAllegDisp", StandardBasicTypes.STRING)
				.addScalar("cdAllegSev", StandardBasicTypes.STRING).addScalar("txtDispSev", StandardBasicTypes.STRING)
				.addScalar("cdChildFatality", StandardBasicTypes.STRING)
				.addScalar("dtPersonDeath", StandardBasicTypes.DATE).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(CpsInvAllegDto.class));
		return query.list();
	}

	/**
     * Method Name: getAllegationsSafe Method Description:convert the cses90d to get
     * allegations. artf238090, artf113751 To fix sql problem of multiple rows output from person merge view
     *
     * @param idStage
     * @param b
     * @return List<CpsInvAllegDto>
     */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvAllegDto> getAllegationsSafe(Long idStage, boolean b) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getAllegationsSafeSql)
				.addScalar("idVictim", StandardBasicTypes.LONG).addScalar("idAllegPrep", StandardBasicTypes.LONG)
				.addScalar("nmVicLast", StandardBasicTypes.STRING).addScalar("nmVicFirst", StandardBasicTypes.STRING)
				.addScalar("nmVicMiddle", StandardBasicTypes.STRING).addScalar("nmVicFull", StandardBasicTypes.STRING)
				.addScalar("nmPerpLast", StandardBasicTypes.STRING).addScalar("nmPerpFirst", StandardBasicTypes.STRING)
				.addScalar("nmPerpMiddle", StandardBasicTypes.STRING).addScalar("nmPerpFull", StandardBasicTypes.STRING)
				.addScalar("cdAllegType", StandardBasicTypes.STRING).addScalar("cdAllegDisp", StandardBasicTypes.STRING)
				.addScalar("cdAllegSev", StandardBasicTypes.STRING).addScalar("txtDispSev", StandardBasicTypes.STRING)
				.addScalar("cdChildFatality", StandardBasicTypes.STRING).addScalar("dtPersonDeath", StandardBasicTypes.DATE)
				.addScalar("decodeAllegType", StandardBasicTypes.STRING).addScalar("decodeAllegDisp", StandardBasicTypes.STRING)
				.setParameter("idStage", idStage)
				.setParameter("allgType", b ? CodesConstant.CCLICALT : CodesConstant.CABTPSP)
				.setParameter("dispType", b ? CodesConstant.CCIVALDS : CodesConstant.CCIVADSP)
				.setResultTransformer(Transformers.aliasToBean(CpsInvAllegDto.class));
		return query.list();
	}


	/**
	 * Method Name: getPrnInvAllegations Method Description:convert the clss97d to
	 * get investigation principals allegations.
	 * 
	 * @param Long
	 *            idStage
	 * @return List<CpsInvAllegDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvAllegDto> getPrnInvAllegations(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPrnInvAllegationsSql)
				.addScalar("cdAllegType", StandardBasicTypes.STRING).addScalar("idVictim", StandardBasicTypes.LONG)
				.addScalar("idAllegPrep", StandardBasicTypes.LONG).addScalar("cdAllegDisp", StandardBasicTypes.STRING)
				.addScalar("cdAllegSev", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdPersRole", StandardBasicTypes.STRING)
				.setParameter("idStage", idStage).setResultTransformer(Transformers.aliasToBean(CpsInvAllegDto.class));
		return query.list();
	}

	/**
	 * Method Name: getSdmSafetyRiskAssessments Method Description: This returns the
	 * sdm safety assessments for the input stage
	 * 
	 * @param idStage
	 * @return List<CpsInvContactSdmSafetyAssessDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvContactSdmSafetyAssessDto> getSdmSafetyRiskAssessments(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getSdmSafetyRiskAssessmentsSql)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("cdAssmtType", StandardBasicTypes.STRING).addScalar("dtAssessed", StandardBasicTypes.DATE)
				.addScalar("dtAssmtCompl", StandardBasicTypes.DATE)
				.addScalar("cdSafetyDecision", StandardBasicTypes.STRING)
				.addScalar("cdUnsafeDecisionAction", StandardBasicTypes.STRING)
				.addScalar("txtAssmtDiscussion", StandardBasicTypes.STRING)
				.addScalar("cdFinalRiskLevel", StandardBasicTypes.STRING)
				.addScalar("txtDiscOverrideReason", StandardBasicTypes.STRING)
				.addScalar("householdName", StandardBasicTypes.STRING)
				.addScalar("idHouseholdEvent", StandardBasicTypes.LONG) //artf159096
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(CpsInvContactSdmSafetyAssessDto.class));
		return (List<CpsInvContactSdmSafetyAssessDto>) query.list();
	}

	/**
	 * Method Name: getChecklistItems Method Description: Gets a list of checklist
	 * items for a given event (DAM: CLSS81D)
	 * 
	 * @param idEvent
	 * @return List<CpsChecklistItemDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsChecklistItemDto> getChecklistItems(Long idEvent) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getChecklistItemsSql)
				.addScalar("idCpsCheckListItem", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idCpsChecklist", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdSrvcReferred", StandardBasicTypes.STRING)
				.setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(CpsChecklistItemDto.class));
		return (List<CpsChecklistItemDto>) query.list();
	}

	/**
	 * Method Name: getCpsChecklist Method Description: DAM: CSESA2D converts from
	 * entity to DTO
	 * 
	 * @param idEvent
	 * @return
	 */
	@Override
	public CpsChecklistDto getCpsChecklist(Long idEvent) {
		CpsChecklist cpsChecklist = srvReferralsDao.getCpsChecklistByEventId(idEvent);
		CpsChecklistDto cpsChecklistDto = new CpsChecklistDto();
		if (!ObjectUtils.isEmpty(cpsChecklist)) {
			cpsChecklistDto.setIdEvent(cpsChecklist.getEvent().getIdEvent());
			cpsChecklistDto.setIdCase(cpsChecklist.getCapsCase().getIdCase());
			cpsChecklistDto.setIdStage(cpsChecklist.getStage().getIdStage());
			cpsChecklistDto.setIdCpsChecklist(cpsChecklist.getIdCpsChecklist());
			cpsChecklistDto.setTsLastUpdate(cpsChecklist.getDtLastUpdate());
			cpsChecklistDto.setDtFirstReferral(cpsChecklist.getDtFirstReferral());
			cpsChecklistDto.setIndSvcRefChklstNoRef(cpsChecklist.getIndReferral());
			cpsChecklistDto.setCdFamilyResponse(cpsChecklist.getCdFamilyResp());
			cpsChecklistDto.setChklstComments(cpsChecklist.getTxtComments());
			cpsChecklistDto.setIdAmtAnnualHouseholdIncome(cpsChecklist.getAmtAnnualHouseholdIncome());
			if (!ObjectUtils.isEmpty(cpsChecklist.getNbrNumberInHousehold())) {
				cpsChecklistDto.setNbrNumberInHousehold(cpsChecklist.getNbrNumberInHousehold().longValue());
			}
			cpsChecklistDto.setIndIncomeQualification(cpsChecklist.getIndIncomeQualification());
			cpsChecklistDto.setIndEligVerifiedByStaff(cpsChecklist.getIndEligVerifiedByStaff());
			cpsChecklistDto.setIndProblemNeglect(cpsChecklist.getIndProblemNeglect());
			cpsChecklistDto.setIndCitizenshipVerify(cpsChecklist.getIndCitizenshipVerify());
			cpsChecklistDto.setIndChildRmvlReturn(cpsChecklist.getIndChildRmvlReturn());
			cpsChecklistDto.setCdEarlyTermRsn(cpsChecklist.getCdEarlyTermRsn());
			cpsChecklistDto.setDtEligStart(cpsChecklist.getDtEligStart());
			cpsChecklistDto.setDtEligEnd(cpsChecklist.getDtEligEnd());
		}
		return cpsChecklistDto;
	}

	/**
	 * Method Name: getServicesReferrals Method Description: Gathers data for
	 * services and referrals
	 * 
	 * @param idStage
	 * @param idEvent
	 * @param cdStage
	 * @return ServRefDto
	 */
	@Override
	public ServRefDto getServicesReferrals(Long idStage, Long idEvent, String cdStage) {
		ServRefDto servRefDto = new ServRefDto();

		// CSYS15D - only if INV stage
		if (ServiceConstants.CSTAGES_INV.equals(cdStage)) {
			ContactStageDiDto inputDto = new ContactStageDiDto();
			inputDto.setUlIdStage(idStage);
			ContactStageDoDto outputDto = contactOccurredDao.csys15dQueryDao(inputDto);
			servRefDto.setDtContactOccurred(outputDto.getDtDTContactOccurred());
		}

		// only if idEvent != 0
		if (!ObjectUtils.isEmpty(idEvent) && !ServiceConstants.ZERO.equals(idEvent)) {
			// CSESA2D
			CpsChecklistDto cpsChecklistDto = getCpsChecklist(idEvent);
			servRefDto.setCpsChecklistDto(cpsChecklistDto);

			// CLSS81D
			List<CpsChecklistItemDto> cpsChecklistItemList = getChecklistItems(idEvent);
			servRefDto.setCpsChecklistItemList(cpsChecklistItemList);

			// CCMN45D
			List<EventDto> eventList = childServicePlanFormDao.fetchEventDetails(idEvent);
			servRefDto.setCdEventStatus(eventList.get(0).getCdEventStatus());
		}

		return servRefDto;
	}

	/**
	 * Method Name: getEventIdByStageAndEventType Method Description: Uses eventDao
	 * method and returns id of resulting event
	 * 
	 * @param idStage
	 * @param cdEventType
	 * @return Long
	 */
	@Override
	public Long getEventIdByStageAndEventType(Long idStage, String cdEventType) {
		return eventDao.getEventByStageIDAndEventType(idStage, cdEventType).getIdEvent();
	}

	/**
	 * Method Name: getEmergencyAssistance Method Description: Gets EA info based
	 * on stage
	 * 
	 * @param idStage
	 * @param cdStage
	 * @return List<ArEaEligibilityDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ArEaEligibilityDto> getEmergencyAssistance(Long idStage, String cdStage) {
		Long idEvent = ServiceConstants.ZERO;
		if (ServiceConstants.CSTAGES_INV.equals(cdStage)) {
			idEvent = getEventIdByStageAndEventType(idStage, ServiceConstants.EVENTTYPE_ELG);
		} else if (ServiceConstants.CSTAGES_AR.equals(cdStage)) {
			idEvent = getEventIdByStageAndEventType(idStage, ServiceConstants.CEVNTTYP_CCL);
		}
		idEvent = ObjectUtils.isEmpty(idEvent) ? ServiceConstants.ZERO : idEvent;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getEmergencyAssistanceSql)
				.addScalar("idEmergencyAssist", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("indEaResponse", StandardBasicTypes.STRING)
				.addScalar("cdEaQuestion", StandardBasicTypes.STRING).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(ArEaEligibilityDto.class));
		return (List<ArEaEligibilityDto>) query.list();
	}

	/**
	 * Method Name: getPriorArStages Method Description: Returns prior stages for
	 * inv merge stages
	 * 
	 * @param mergedStages
	 * @return List<Long>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getPriorArStages(String mergedStages) {
		
		List<Long> idStages = new ArrayList<>();
		if (!ObjectUtils.isEmpty(mergedStages)) {
			for (String idStage : mergedStages.split(",")) {
				idStages.add(Long.valueOf(idStage));
			}
		}		
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPriorArStagesSql)
				.setParameterList("mergedStages", idStages)).addScalar("idPriorStage", StandardBasicTypes.LONG);
		return (List<Long>) query.list();
	}

	/**
	 * Method Name: getPriorArStages Method Description: Returns prior stages for
	 * inv merge stages
	 * 
	 * @param mergedStages
	 * @return List<Long>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CpsInvReportMergedDto getArStages(String mergedStages, Long idCase) {
		CpsInvReportMergedDto allArStages = new CpsInvReportMergedDto();
		List<Long> idStages = new ArrayList<>();
		if (!ObjectUtils.isEmpty(mergedStages)) {
			for (String idStage : mergedStages.split(",")) {
				idStages.add(Long.valueOf(idStage));
			}
		}
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPriorArStagesSql)
				.setParameterList("mergedStages", idStages)).addScalar("idPriorStage", StandardBasicTypes.LONG);
		List<Long> idPriorStageList = (List<Long>) query.list();
		int count = 0;
		allArStages.setStrMergedStages(ZERO);// artf130753 set ZERO - Duplicate contacts displayed
		for (Long idPriorStage : idPriorStageList) {
			if (0 == count)
				allArStages.setStrMergedStages(idPriorStage.toString());
			else
				allArStages.setStrMergedStages(allArStages.getStrMergedStages() + "," + idPriorStage.toString());
			count++;
			// set up and call case_merge procedure
			StageDto stageDto = new StageDto();
			stageDto.setIdCase(idCase);
			stageDto.setIdStage(idPriorStage);
			stageDto.setCdStage("A-R");
			CpsInvReportMergedDto mergeStages = getMergedStages(stageDto);
			// set any merges in the merged stage string
			if (!ZERO.equals(mergeStages.getStrMergedStages())) {
				allArStages
						.setStrMergedStages(allArStages.getStrMergedStages() + ',' + mergeStages.getStrMergedStages());
			}
		}

		return allArStages;
	}
	
	/**
	 * 
	 *Method Name:	getPriorArIntakes
	 *Method Description:Method used to get the prior intake stage for AR stage
	 *@param arStagesBean
	 *@return CpsInvReportMergedDto
	 */
	 public CpsInvReportMergedDto getPriorArIntakes(CpsInvReportMergedDto arStagesBean) 
	  {		    
		    CpsInvReportMergedDto retval = new CpsInvReportMergedDto();
		    List<CpsInvIntakePersonPrincipalDto> cpsInvIntakePersonPrincipalDtos  = getIntakes(arStagesBean.getStrMergedStages());
		    for (CpsInvIntakePersonPrincipalDto cpsInvIntakePersonPrincipalDto : cpsInvIntakePersonPrincipalDtos) {
		    	Long arIntStage = cpsInvIntakePersonPrincipalDto.getIdPriorStage(); 
		    	if(ObjectUtils.isEmpty(retval.getStrMergedStages()))
		         {
		           //first time will not pre-pend comma
		           retval.setStrMergedStages(String.valueOf(arIntStage));
		         } else {
		           retval.setStrMergedStages(retval.getStrMergedStages() + ',' + arIntStage);
		         }
			}
		    return retval;
		 }

	/**
	 * 
	 * Method Name: getMergedStages Method Description: Method used to get the
	 * merged stages for the contact
	 * 
	 * @param stageDto
	 * @return CpsInvReportMergedDto
	 */
	@Override
	public CpsInvReportMergedDto getMergedStages(StageDto stageDto) {
		String retval = "";
		java.util.Date mergeDate = null;
		int rownum = 0;
		int errorCode = -1;
		String errorMessage = "";
		CpsInvReportMergedDto cpsInvReportMergedDto = new CpsInvReportMergedDto();
		SessionImpl sessionImpl = (SessionImpl) sessionFactory.getCurrentSession();
		Connection connection = sessionImpl.connection();
		CallableStatement callableStatement = null;
		try {
			if (getMergeHistory(stageDto.getIdCase()).size() > 0) {
				callableStatement = connection.prepareCall("{call PKG_CLSCDBD.PROC_CLSCDBD(?, ?, ?, ?, ?)}");
				callableStatement.setLong(1, stageDto.getIdCase());
				callableStatement.setLong(2, stageDto.getIdStage());
				callableStatement.setString(3, stageDto.getCdStage());

				callableStatement.registerOutParameter(4, 2003, "TYPE_CLSCDBDO");
				callableStatement.registerOutParameter(5, OracleTypes.VARCHAR);
				callableStatement.execute();

				rownum = callableStatement.getInt(5);
				StringBuilder sb = new StringBuilder();
				if (0 == rownum) {
					sb.append("0");
					errorCode = 0;
				}
				if (0 != rownum) {
					errorCode = 0;
					Array stageListArray = (Array) callableStatement.getObject(4);
					Object[] stageList = (Object[]) stageListArray.getArray();
					for (int i = 0; i < rownum; i++) {
						Struct stageListRec = (Struct) stageList[i];
						Object stageListAttr[] = stageListRec.getAttributes();
						if (i == 0) {
							mergeDate = (Date) stageListAttr[1];
						}
						if (i != 0 && i < rownum) {
							sb.append(",");
						}
						sb.append(new Integer(stageListAttr[0].toString()));
					}
				}
				retval = sb.toString();
				if (errorCode < 0) {
					errorMessage = "getMergedStages:Stored Procedure (PKG_CLSCDBD.PROC_CLSCDBD) call Exception";
					throw new DataLayerException(errorMessage);
				}
				cpsInvReportMergedDto.setDtEarliestStageStart(mergeDate);
				cpsInvReportMergedDto.setStrMergedStages(retval);
			}
		} catch (SQLException e) {
			DataLayerException dataLayerException = new DataLayerException(e.getMessage());
			dataLayerException.initCause(e);
			throw dataLayerException;
		}
		return cpsInvReportMergedDto;
	}

	/**
	 * Method Name: getAllIntStages Method Description: Add in the intake for the
	 * selected stage to the merged stage string. Then add in any intakes for prior
	 * AR stages then add intakes from stages closed to merge to the selected stage.
	 * 
	 * @param arStagesVb
	 * @param caseInfo
	 * @param intakes
	 * @return CpsInvReportMergedDto
	 */
	public CpsInvReportMergedDto getAllIntStages(CpsInvReportMergedDto arStagesVb, StageDto caseInfo,
			List<CpsInvReportIntakeDto> intakes) {
		CpsInvReportMergedDto allIntStagesDto = new CpsInvReportMergedDto();
		allIntStagesDto.setStrMergedStages(getPriorIntStage(caseInfo.getIdStage()).toString());
		CpsInvReportMergedDto priorArIntakes = new CpsInvReportMergedDto();
		priorArIntakes.setStrMergedStages(
				getPriorArIntakes(arStagesVb).getStrMergedStages());
		if (!ObjectUtils.isEmpty(priorArIntakes.getStrMergedStages())
				&& !ZERO.equals(priorArIntakes.getStrMergedStages())) {
			allIntStagesDto.setStrMergedStages(
					allIntStagesDto.getStrMergedStages() + ',' + priorArIntakes.getStrMergedStages());
		}
		if (!ObjectUtils.isEmpty(intakes)) {
			for (CpsInvReportIntakeDto intake : intakes) {
				allIntStagesDto
						.setStrMergedStages(allIntStagesDto.getStrMergedStages() + ',' + intake.getIdPriorStage());
			}
		}
		allIntStagesDto.setCdStage(ServiceConstants.CSTAGES_INT);
		return allIntStagesDto;
	}

	/**
	 *
	 * Method Name: getContactGuideList Method Description: Method used to get the
	 * contact guide details for all the contact in the search list
	 * 
	 * @param caseInfo
	 * @param dtSampleFrom
	 * @param dtSampleTo
	 * @return List<ContactNarrGuideDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ContactNarrGuideDto> getContactGuideList(StageDto caseInfo, Date dtSampleFrom, Date dtSampleTo) {
		String dtContactOccurdForm = DateUtils.fullISODateTimeFormat(dtSampleFrom, DATE_FORMAT);
		String dtContactOccurdTo = DateUtils.fullISODateTimeFormat(dtSampleTo, DATE_FORMAT);
		List<ContactNarrGuideDto> contactNarrGuideDtolist=new ArrayList<ContactNarrGuideDto>();	
		if(!ObjectUtils.isEmpty(caseInfo.getIdCase()))
		{
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getContactGuideSql)
				.addScalar("idContactGuideNarr", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdType", StandardBasicTypes.STRING)
				.addScalar("cdGuideRole", StandardBasicTypes.STRING).addScalar("nmFullName", StandardBasicTypes.STRING)
				.addScalar("idTemplate",StandardBasicTypes.LONG)
				.addScalar("narrativeBlob",StandardBasicTypes.BLOB)
				.setParameter("idCase", caseInfo.getIdCase()).setParameter("dtSampleFrom", dtContactOccurdForm)
				.setParameter("dtSampleTo", dtContactOccurdTo)
				.setResultTransformer(Transformers.aliasToBean(ContactNarrGuideDto.class));
		contactNarrGuideDtolist= (List<ContactNarrGuideDto>) query.list();
		if(!ObjectUtils.isEmpty(contactNarrGuideDtolist)){
			contactNarrGuideDtolist.forEach(cgnDto -> {
				if (!ObjectUtils.isEmpty(cgnDto.getNarrativeBlob()))
					try {
						cgnDto.setNarrative(TypeConvUtil.getNarrativeData(cgnDto.getNarrativeBlob().getBinaryStream()));
						cgnDto.setNarrativeBlob(null);
					} catch (SQLException e) {
						DataLayerException dataLayerException = new DataLayerException(e.getMessage());
						dataLayerException.initCause(e);
						throw dataLayerException;
					}
			});
		}
		}
		return contactNarrGuideDtolist;
	}
}
