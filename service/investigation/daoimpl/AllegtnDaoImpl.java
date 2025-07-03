package us.tx.state.dfps.service.investigation.daoimpl;

import java.util.*;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Allegation;
import us.tx.state.dfps.common.domain.FacilAlleg;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.apsserviceplan.dao.ApsServicePlanDao;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.DisplayAllegDtlReq;
import us.tx.state.dfps.service.common.response.DisplayAllegDtlRes;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.investigation.dao.AllegtnDao;
import us.tx.state.dfps.service.investigation.dto.AllegationDetailDto;
import us.tx.state.dfps.service.investigation.dto.AllegtnPrsnDto;
import us.tx.state.dfps.service.investigation.dto.AllgtnFindDupDto;
import us.tx.state.dfps.service.investigation.dto.PerpVictmDto;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Apr 3, 2017 - 10:24:02 AM
 */
@Repository
public class AllegtnDaoImpl implements AllegtnDao {

	@Value("${AllegtnDaoImpl.getInvAdminStageAllegn}")
	private transient String getInvAdminStageAllegnSql;

	@Value("${AllegtnDaoImpl.getDeliveryStageAllegn}")
	private transient String getDeliveryStageAllegnSql;

	@Value("${AllegtnDaoImpl.getIntakeStageAllegn}")
	private transient String getIntakeStageAllegnSql;

	@Value("${AllegtnDaoImpl.getAllegtnDtlByIdStage}")
	private transient String getAllegtnDtlByIdStage;

	@Value("${AllegtnDaoImpl.getVictimCount}")
	private transient String getVictimCount;

	@Value("${AllegtnDaoImpl.getPerpCount}")
	private transient String getPerpCount;

	@Value("${AllegtnDaoImpl.findDuplicatesADDSQL}")
	private transient String findDuplicatesADDSQL;

	@Value("${AllegtnDaoImpl.findDuplicatesUPDTSQL}")
	private transient String findDuplicatesUPDTSQL;

	@Value("${AllegtnDaoImpl.findDuplicatesADDWithPerp}")
	private transient String findDuplicatesADDWithPerp;

	@Value("${AllegtnDaoImpl.findDuplicatesUPDTWithPerp}")
	private transient String findDuplicatesUPDTWithPerp;

	@Value("${AllegtnDaoImpl.getVictimUnKnownPerp}")
	private transient String getVictimUnKnownPerp;

	@Value("${AllegtnDaoImpl.getVictimNonPerp}")
	private transient String getVictimNonPerp;

	@Value("${AllegtnDaoImpl.getPerpNonVictim}")
	private transient String getPerpNonVictim;

	@Value("${AllegtnDaoImpl.getPerpAndVictim}")
	private transient String getPerpAndVictim;

	@Value("${AllegtnDaoImpl.getAllegationById}")
	private transient String getAllegationByIdSql;

	@Value("${AllegtnDaoImpl.getChildDeathDetailsSql}")
	private transient String getChildDeathDetailsSql;

	@Value("${AllegtnDaoImpl.fetchDtIntakeForIdStage}")
	private transient String fetchDtIntakeForIdStage;

	@Value("${AllegtnDaoImpl.getAllegationHistoryList}")
	private transient String getAllegationHistoryListSql;

	@Value("${AllegtnDaoImpl.getVictimPersonId}")
	private transient String getVictimPersonIdHql;

	@Value("${AllegtnDaoImpl.getDistinctDisposition}")
	private transient String getDistinctDispHql;

	@Value("${AllegtnDaoImpl.getAllegationProblemCount}")
	private transient String getAllegationProblemCountSql;

	@Value("${AllegtnDaoImpl.getInjuryAllegationCount}")
	private transient String getInjuryAllegationCount;

	@Value("${AllegtnDaoImpl.deleteSPSourcesForAllegationByAllegationIdAndServicePlanIdSql}")
	private transient String deleteSPSourcesForAllegationByAllegationIdAndServicePlanIdSql;

	@Value("${AllegtnDaoImpl.getInvAdminStageAllegnForMPS}")
	private transient String getInvAdminStageAllegnSqlForMPS;

	@Value("${AllegtnDaoImpl.getVictimNonPerpForMPS}")
	private transient String getVictimNonPerpForMPS;

	@Value("${AllegtnDaoImpl.getPerpNonVictimForMPS}")
	private transient String getPerpNonVictimForMPS;

	@Value("${AllegtnDaoImpl.getPerpAndVictimForMPS}")
	private transient String getPerpAndVictimForMPS;

	@Value("${AllegtnDaoImpl.getAllegationId}")
	private transient String getAllegationId;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Autowired
	ApsServicePlanDao apsServicePlanDao;

	@Autowired
	MobileUtil mobileUtil;

	public AllegtnDaoImpl() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tx.us.dfps.impact.investigation.dao.AllegtnDao#getInvAdminStageAllegn
	 * (org.tx.us.dfps.impact.request.InvAllegListReq)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AllegationDetailDto> getInvAdminStageAllegn(Long uidStage) {
		List<AllegationDetailDto> allegationList = new ArrayList<>();
		if(mobileUtil.isMPSEnvironment()){
			Query getInvAdminStageAllegnQry = sessionFactory.getCurrentSession().createSQLQuery(getInvAdminStageAllegnSqlForMPS)
					.addScalar("scrPersVictim").addScalar("scrAllegPerp").addScalar("idVictim", StandardBasicTypes.LONG)
					.addScalar("cdAllegType").addScalar("cdAllegSeverity")
					.addScalar("idAllegedPerpetrator", StandardBasicTypes.LONG).addScalar("cdAllegDisposition")
					.addScalar("cdAllegIncidentStage").addScalar("idAllegation", StandardBasicTypes.LONG)
					.addScalar("idFacilResource", StandardBasicTypes.LONG).addScalar("nmFacilInvstFacility")
					.addScalar("indFatalAlleg").addScalar("dtPersonDeath").addScalar("indNearFatal")
					.addScalar("indRelinquishCstdy").addScalar("idStage", StandardBasicTypes.LONG)
					.setParameter("hi_ulIdStage", uidStage)
					.setResultTransformer(Transformers.aliasToBean(AllegationDetailDto.class));
			allegationList = (List<AllegationDetailDto>) getInvAdminStageAllegnQry.list();
		} else {
			Query getInvAdminStageAllegnQry = sessionFactory.getCurrentSession().createSQLQuery(getInvAdminStageAllegnSql)
					.addScalar("scrPersVictim").addScalar("scrAllegPerp").addScalar("idVictim", StandardBasicTypes.LONG)
					.addScalar("cdAllegType").addScalar("cdAllegSeverity")
					.addScalar("idAllegedPerpetrator", StandardBasicTypes.LONG).addScalar("cdAllegDisposition")
					.addScalar("cdAllegIncidentStage").addScalar("idAllegation", StandardBasicTypes.LONG)
					.addScalar("idFacilResource", StandardBasicTypes.LONG).addScalar("nmFacilInvstFacility")
					.addScalar("indFatalAlleg").addScalar("dtPersonDeath").addScalar("indNearFatal")
					.addScalar("indRelinquishCstdy").addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG).addScalar("scrCreatedPerson")
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG).addScalar("scrLastUpdatePerson")
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("dtIncdnt", StandardBasicTypes.DATE).addScalar("indDtInjury")
				.addScalar("tmIncdnt", StandardBasicTypes.DATE).addScalar("indTmInjury")
				.addScalar("cdIncdntLctn").addScalar("txtDescAllg")
					.setParameter("hi_ulIdStage", uidStage)
					.setResultTransformer(Transformers.aliasToBean(AllegationDetailDto.class));
			allegationList = (List<AllegationDetailDto>) getInvAdminStageAllegnQry.list();
		}
		return allegationList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tx.us.dfps.impact.investigation.dao.AllegtnDao#getDeliveryStageAllegn
	 * (org.tx.us.dfps.impact.request.InvAllegListReq)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AllegationDetailDto> getDeliveryStageAllegn(Long uidStage) {
		List<AllegationDetailDto> allegationList = new ArrayList<>();
		Query getDeliveryStageAllegnQry = sessionFactory.getCurrentSession().createSQLQuery(getDeliveryStageAllegnSql)
				.addScalar("scrPersVictim").addScalar("scrAllegPerp").addScalar("idVictim", StandardBasicTypes.LONG)
				.addScalar("cdAllegType").addScalar("cdAllegSeverity")
				.addScalar("idAllegedPerpetrator", StandardBasicTypes.LONG).addScalar("cdAllegDisposition")
				.addScalar("idFacilResource").addScalar("nmFacilInvstFacility").addScalar("indFatalAlleg")
				.addScalar("dtPersonDeath").addScalar("indNearFatal").addScalar("indRelinquishCstdy")
				.addScalar("idStage", StandardBasicTypes.LONG).setParameter("hi_ulIdStage", uidStage)
				.setResultTransformer(Transformers.aliasToBean(AllegationDetailDto.class));
		allegationList = (List<AllegationDetailDto>) getDeliveryStageAllegnQry.list();
		return allegationList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tx.us.dfps.impact.investigation.dao.AllegtnDao#getIntakeStageAllegn(
	 * org.tx.us.dfps.impact.request.InvAllegListReq)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AllegationDetailDto> getIntakeStageAllegn(Long uidStage) {
		List<AllegationDetailDto> allegationList = new ArrayList<>();
		Query getIntakeStageAllegnQry = sessionFactory.getCurrentSession().createSQLQuery(getIntakeStageAllegnSql)
				.addScalar("scrPersVictim").addScalar("scrAllegPerp").addScalar("idVictim", StandardBasicTypes.LONG)
				.addScalar("cdAllegType").addScalar("idAllegedPerpetrator", StandardBasicTypes.LONG)
				.addScalar("cdAllegIncidentStage").addScalar("idAllegation", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).setParameter("hi_ulIdStage", uidStage)
				.setResultTransformer(Transformers.aliasToBean(AllegationDetailDto.class));
		allegationList = (List<AllegationDetailDto>) getIntakeStageAllegnQry.list();
		return allegationList;
	}

	@Override
	public Long updateAllegation(Allegation allegation, String operation, boolean indFlush) {
		Long idAllegation = null;
		if (operation.equals(ServiceConstants.REQ_FUNC_CD_ADD))
			sessionFactory.getCurrentSession().persist(allegation);
		else if (operation.equals(ServiceConstants.REQ_FUNC_CD_UPDATE))
			sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(allegation));
		else if (operation.equals(ServiceConstants.REQ_FUNC_CD_DELETE))
			sessionFactory.getCurrentSession()
					.delete(sessionFactory.getCurrentSession().load(Allegation.class, allegation.getIdAllegation()));
		idAllegation = allegation.getIdAllegation();
		if (indFlush)
			sessionFactory.getCurrentSession().flush();
		return idAllegation;
	}

	@Override
	public Long getpersonIdVictim(Long idAllegation) {
		Long idvictim;
		idvictim = (Long) sessionFactory.getCurrentSession().createQuery(getVictimPersonIdHql)
				.setParameter("idAllegation", idAllegation).uniqueResult();
		return idvictim;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AllegtnPrsnDto> getAllegtnDtlByIdStage(DisplayAllegDtlReq displayAllegDtlReq) {
		List<AllegtnPrsnDto> allegationList = new ArrayList<>();
		Query getIntakeStageAllegnQry = sessionFactory.getCurrentSession().createSQLQuery(getAllegtnDtlByIdStage)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdStagePersRole").addScalar("tsLastUpdate")
				.addScalar("nmPersonFull").addScalar("cdPersonSuffix").addScalar("cdPersonMaritalStatus")
				.addScalar("dtPersonBirth").addScalar("dtPersonDeath").addScalar("nbrPersonAge", StandardBasicTypes.INTEGER)
				.addScalar("indPersonDOBAprrox")
				.setParameter("hi_uid_stage", displayAllegDtlReq.getUlIdStage())
				.setParameter("hi_persn_type", ServiceConstants.CPRSNALL_PRN)
				.setResultTransformer(Transformers.aliasToBean(AllegtnPrsnDto.class));
		allegationList = (List<AllegtnPrsnDto>) getIntakeStageAllegnQry.list();
		return allegationList;
	}

	@Override
	public DisplayAllegDtlRes getAllegtnDtlByIdAlegtn(DisplayAllegDtlReq displayAllegDtlReq) {
		DisplayAllegDtlRes aisplayAllegDtlRes = new DisplayAllegDtlRes();
		Allegation allegation = (Allegation) sessionFactory.getCurrentSession().load(Allegation.class,
				displayAllegDtlReq.getUlIdAllegation());
		aisplayAllegDtlRes.setCdAllegDisposition(allegation.getCdAllegDisposition());
		aisplayAllegDtlRes.setSzCdAllegIncidentStage(allegation.getCdAllegIncidentStage());
		aisplayAllegDtlRes.setSzCdAllegSeverity(allegation.getCdAllegSeverity());
		aisplayAllegDtlRes.setSzCdAllegType(allegation.getCdAllegType());
		aisplayAllegDtlRes.setSzTxtAllegDuration(allegation.getTxtAllegDuration());
		aisplayAllegDtlRes.setSzTxtDisptnSeverity(allegation.getTxtDispstnSeverity());
		aisplayAllegDtlRes.setTsLastUpdate(allegation.getDtLastUpdate());
		aisplayAllegDtlRes.setcIndCoSlpgChildDth(allegation.getIndCoSlpgChildDth());
		aisplayAllegDtlRes.setcIndCoSlpgSubstance(allegation.getIndCoSlpgSubstance());
		aisplayAllegDtlRes.setcIndFatalAlleg(allegation.getIndFatality());
		aisplayAllegDtlRes.setcIndNearFatal(allegation.getIndNearFatal());
		aisplayAllegDtlRes.setcIndRelinquishCstdy(allegation.getIndRelinquishCstdy());
		aisplayAllegDtlRes.setTxtAvApChngCmnt(allegation.getTxtAvApChngCmnt());
		// PPM 85809 - CPS Tracking Maltreatment of Children in CVS
		aisplayAllegDtlRes.setDtAllegedIncident(allegation.getDtAllegedIncident());
		aisplayAllegDtlRes.setIndApproxAllegedDate(allegation.getIndApproxAllegedDate());
		return aisplayAllegDtlRes;
	}

	@Override
	public Integer getVictimCount(Long idPerson, Long idStage, Long idAllegtn) {
		return (Integer) sessionFactory.getCurrentSession().createSQLQuery(getVictimCount)
				.addScalar("count", StandardBasicTypes.INTEGER).setParameter("hI_ulIdPerson", idPerson)
				.setParameter("hI_ulIdStage", idStage).setParameter("hI_ulIdAllegation", idAllegtn).uniqueResult();
	}

	@Override
	public Integer getPerpCount(Long idPerson, Long idStage, Long idAllegtn) {
		return (Integer) sessionFactory.getCurrentSession().createSQLQuery(getPerpCount)
				.addScalar("count", StandardBasicTypes.INTEGER).setParameter("hI_ulIdPerson", idPerson)
				.setParameter("hI_ulIdStage", idStage).setParameter("hI_ulIdAllegation", idAllegtn).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean findDuplicates(AllegationDetailDto allegationDetail, String operation) {
		List<AllgtnFindDupDto> allgtnFindDupList = new ArrayList<>();
		Boolean founddubplicates = Boolean.FALSE;
		String sql = null;
		if (!ObjectUtils.isEmpty(allegationDetail.getIdVictim())) {
			if (operation.equals(ServiceConstants.REQ_FUNC_CD_ADD))
				sql = findDuplicatesADDSQL;
			else
				sql = findDuplicatesUPDTSQL;
			if (!ObjectUtils.isEmpty(allegationDetail.getIdAllegedPerpetrator())) {
				if (operation.equals(ServiceConstants.REQ_FUNC_CD_ADD))
					sql = findDuplicatesADDWithPerp;
				else
					sql = findDuplicatesUPDTWithPerp;
			}
			Query query = sessionFactory.getCurrentSession().createSQLQuery(sql)
					.addScalar("idAllegation", StandardBasicTypes.LONG)
					.addScalar("occrCount", StandardBasicTypes.INTEGER)
					.setParameter("hI_uid_victim", allegationDetail.getIdVictim())
					.setParameter("hI_cdStagePersType", allegationDetail.getCdAllegType())
					.setParameter("hI_ulIdStage", allegationDetail.getIdStage())
					.setResultTransformer(Transformers.aliasToBean(AllgtnFindDupDto.class));
			if (!ObjectUtils.isEmpty(allegationDetail.getIdAllegedPerpetrator())) {
				query.setParameter("hI_ulIdPerson", allegationDetail.getIdAllegedPerpetrator());
			}
			allgtnFindDupList = (List<AllgtnFindDupDto>) query.list();
			for (AllgtnFindDupDto a : allgtnFindDupList) {
				if (!ObjectUtils.isEmpty(allegationDetail.getIdAllegation())
						&& !allegationDetail.getIdAllegation().equals(a.getIdAllegation()))
					founddubplicates = Boolean.TRUE;
				if (ObjectUtils.isEmpty(allegationDetail.getIdAllegation())
						&& operation.equals(ServiceConstants.REQ_FUNC_CD_ADD))
					founddubplicates = Boolean.TRUE;
			}
			if (!ObjectUtils.isEmpty(allgtnFindDupList) && allgtnFindDupList.size() > 1)
				founddubplicates = Boolean.TRUE;
		}
		return founddubplicates;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getDispositionsList(Long uidStage) {
		List<String> dispositionsList = new ArrayList<>();
		dispositionsList = (List<String>) sessionFactory.getCurrentSession().createQuery(getDistinctDispHql)
				.setParameter("hi_uidStage", uidStage).list();
		return dispositionsList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AllegtnPrsnDto> getVictimUnKnownPerp(Long uidStage) {
		List<AllegtnPrsnDto> victimList = new ArrayList<>();
		victimList = (List<AllegtnPrsnDto>) sessionFactory.getCurrentSession().createSQLQuery(getVictimUnKnownPerp)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdAllegDisposition")
				.setParameter("hi_uidStage", uidStage)
				.setResultTransformer(Transformers.aliasToBean(AllegtnPrsnDto.class)).list();
		return victimList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AllegtnPrsnDto> getVictimNonPerp(Long uidStage) {
		List<AllegtnPrsnDto> victimList = new ArrayList<>();
		if(mobileUtil.isMPSEnvironment()){
			victimList = (List<AllegtnPrsnDto>) sessionFactory.getCurrentSession().createSQLQuery(getVictimNonPerpForMPS)
					.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdAllegDisposition")
					.setParameter("hi_uidStage", uidStage)
					.setResultTransformer(Transformers.aliasToBean(AllegtnPrsnDto.class)).list();
		}else {
			victimList = (List<AllegtnPrsnDto>) sessionFactory.getCurrentSession().createSQLQuery(getVictimNonPerp)
					.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdAllegDisposition")
					.setParameter("hi_uidStage", uidStage)
					.setResultTransformer(Transformers.aliasToBean(AllegtnPrsnDto.class)).list();
		}
		return victimList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AllegtnPrsnDto> getPerpNonVictim(Long uidStage) {
		List<AllegtnPrsnDto> perpList = new ArrayList<>();
		if(mobileUtil.isMPSEnvironment()){
			perpList = (List<AllegtnPrsnDto>) sessionFactory.getCurrentSession().createSQLQuery(getPerpNonVictimForMPS)
					.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdAllegDisposition")
					.setParameter("hi_uidStage", uidStage)
					.setResultTransformer(Transformers.aliasToBean(AllegtnPrsnDto.class)).list();
		} else {
			perpList = (List<AllegtnPrsnDto>) sessionFactory.getCurrentSession().createSQLQuery(getPerpNonVictim)
					.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdAllegDisposition")
					.setParameter("hi_uidStage", uidStage)
					.setResultTransformer(Transformers.aliasToBean(AllegtnPrsnDto.class)).list();
		}
		return perpList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PerpVictmDto> getPerpAndVictim(Long uidStage) {
		List<PerpVictmDto> perpVictimList = new ArrayList<>();
		if(mobileUtil.isMPSEnvironment()){
			perpVictimList = (List<PerpVictmDto>) sessionFactory.getCurrentSession().createSQLQuery(getPerpAndVictimForMPS)
					.addScalar("idVictim", StandardBasicTypes.LONG).addScalar("cdAllegDispositionVict")
					.addScalar("idAllegedPerpetrator", StandardBasicTypes.LONG).addScalar("cdAllegDispositionPerp")
					.setParameter("hi_uidStage", uidStage)
					.setResultTransformer(Transformers.aliasToBean(PerpVictmDto.class)).list();
		} else {
			perpVictimList = (List<PerpVictmDto>) sessionFactory.getCurrentSession().createSQLQuery(getPerpAndVictim)
					.addScalar("idVictim", StandardBasicTypes.LONG).addScalar("cdAllegDispositionVict")
					.addScalar("idAllegedPerpetrator", StandardBasicTypes.LONG).addScalar("cdAllegDispositionPerp")
					.setParameter("hi_uidStage", uidStage)
					.setResultTransformer(Transformers.aliasToBean(PerpVictmDto.class)).list();
		}
		return perpVictimList;
	}

	/**
	 * This DAM adds, updates, or deletes a full row in the ALLEGATION table.
	 * 
	 * Service Name: CCMN03U, DAM Name : CINV07D
	 * 
	 * @param idAdminReview
	 * @return @
	 */
	@Override
	public Allegation getAllegationById(Long idAllegation) {
		Allegation allegation = (Allegation) sessionFactory.getCurrentSession().load(Allegation.class, idAllegation);
		return allegation;
	}

	/**
	 * This DAM is used by the CloseOpenStage common function (CCMN03U) to add a
	 * dummy row to the FACIL_ALLEG table.
	 * 
	 * Service Name: CCMN03U, DAM Name : CINVB4D
	 * 
	 * @param facilAlleg
	 * @
	 */
	@Override
	public void saveFacilAlleg(FacilAlleg facilAlleg) {
		sessionFactory.getCurrentSession().save(facilAlleg);
	}

	@Override
	public Date fetchDtIntakeForIdStage(Long ulIdStage) {
		if(ulIdStage == null){
			throw new IllegalArgumentException("Stage Id can not be null");
		}
		//checking for Field Validation Exception Stage Id can not be null
		return Optional.ofNullable((Date) sessionFactory.getCurrentSession().createSQLQuery(fetchDtIntakeForIdStage)
				.addScalar("intakeDate", StandardBasicTypes.TIMESTAMP).setParameter("hI_ulIdStage", ulIdStage)
				.uniqueResult()).orElse(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.investigation.dao.AllegtnDao#getVictimsByIdStage
	 * (java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Person> getVictimsByIdStage(Long idStage) {
		List<Person> victimList = new ArrayList<Person>();
		Stage stage = (Stage) sessionFactory.getCurrentSession().load(Stage.class, idStage);
		List<Allegation> allegationList = (List<Allegation>) sessionFactory.getCurrentSession()
				.createCriteria(Allegation.class).add(Restrictions.eq("stage", stage)).list();
		allegationList.forEach(allegation -> victimList.add(allegation.getPersonByIdVictim()));
		return victimList;
	}

	@Override
	public List<Allegation> getAllegationsByIdStage(Long idStage) {
		List<Allegation> allegationList = new ArrayList<Allegation>();
		Stage stage = (Stage) sessionFactory.getCurrentSession().load(Stage.class, idStage);
		allegationList.addAll((List<Allegation>) sessionFactory.getCurrentSession()
				.createCriteria(Allegation.class).add(Restrictions.eq("stage", stage)).list());
		return allegationList;
	}

	@Override
	public boolean hasChildDeathReportCompleted(Long personId) {
		try {
			Query query = sessionFactory.getCurrentSession()
					.createSQLQuery(getChildDeathDetailsSql)
					.addScalar("ID_EVENT", LongType.INSTANCE)
					.addScalar("ID_INV_LTRS", LongType.INSTANCE)
					.addScalar("CD_EVENT_STATUS", StringType.INSTANCE)
					.setParameter("personId", personId);
			boolean childDeathReportExists =  (long) query.list().size() > 0 ? true : false;
			return childDeathReportExists;
		} catch (Exception e) {
			System.out.println("Error getting child death details : "+e.getMessage());
			throw e;
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<PerpVictmDto> getAllegationDetailHistoryList(DisplayAllegDtlReq displayAllegDtlReq) {
		List<PerpVictmDto> perpVictimList = new ArrayList<>();

		perpVictimList = (List<PerpVictmDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getAllegationHistoryListSql).addScalar("nmPersonFullVictim", StandardBasicTypes.STRING)
				.addScalar("nmPersonFullAllegedPerp", StandardBasicTypes.STRING)
				.addScalar("nmPersonFullWorker", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.setParameter("idStage", displayAllegDtlReq.getUlIdStage())
				.setParameter("idAllegation", displayAllegDtlReq.getUlIdAllegation())
				.setResultTransformer(Transformers.aliasToBean(PerpVictmDto.class)).list();

		return perpVictimList;
	}

	/**
	 * Method helps to find the Allegation problem count added in Service Plan
	 *
	 * @param idAllegation allegation id
	 * @return return 1 is record exists else return 0
	 */
	@Override
	public int getAllegationProblemCount(Long idAllegation) {

		SQLQuery query= (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getAllegationProblemCountSql)
				.addScalar("problemCount",StandardBasicTypes.INTEGER)
				.setParameter("idAllegation", idAllegation);
		return  (Integer) query.uniqueResult();
	}

	/**
	 * Method helps to execute the query for deleting Allegation record in service plan table
	 *
	 * @param idStage selected stage
	 * @param idAllegation Allegation id for deletion
	 */
	@Override
	public void deleteSPSourceForAllegationByAllegationId(Long idStage, Long idAllegation) {
		ApsServicePlanDto apsServicePlanDto = apsServicePlanDao.getServicePlanByStage(idStage);
		if(ObjectUtils.isEmpty(apsServicePlanDto) || ObjectUtils.isEmpty(apsServicePlanDto.getId()) ){
			return;
		}
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(deleteSPSourcesForAllegationByAllegationIdAndServicePlanIdSql)
				.setParameter("idServicePlanId", apsServicePlanDto.getId())
				.setParameter("idAllegation", idAllegation);
		query.executeUpdate();

	}

	@Override
	public Integer getInjuryAllegationCount(Long allegationId) {
		return (Integer) sessionFactory.getCurrentSession().createSQLQuery(getInjuryAllegationCount)
				.addScalar("count", StandardBasicTypes.INTEGER)
				.setParameter("allegationId", allegationId).uniqueResult();
	}

	@Override
	public boolean getValidAllegations(Long idCase) {
		List<Integer> allegList = new ArrayList<>();
		allegList = (List<Integer>) sessionFactory.getCurrentSession().createSQLQuery(getAllegationId)
				.setParameter("idCase", idCase).list();
		if (allegList.size() == 0){
			return false;
		}
		else{
			return true;
		}
	}
}
