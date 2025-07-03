package us.tx.state.dfps.service.sscc.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.person.dto.MedicalConsenterDto;
import us.tx.state.dfps.service.sscc.dao.SSCCPlcmtOptCircumDao;
import us.tx.state.dfps.service.sscc.dto.*;
import us.tx.state.dfps.service.sscc.util.SSCCPlcmtOptCircumUtil;

import java.math.BigDecimal;
import java.util.*;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:Implementation Class for SSCC Placement Option Circumstances Data
 * Access. Aug 10, 2018- 6:29:19 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class SSCCPlcmtOptCircumDaoImpl implements SSCCPlcmtOptCircumDao {

	private static final Logger log = Logger.getLogger(SSCCPlcmtOptCircumDaoImpl.class);

	@Value("${SSCCOptCircumListDaoImpl.getDfpsStaffHql}")
	private String getDfpsStaff;

	@Value("${SSCCOptCircumListDaoImpl.getMedCnsntrForCpaOthrHmHql}")
	private String getMedCnsntrForCpaOthrHm;

	@Value("${SSCCOptCircumListDaoImpl.getGetPersonByNMDOBSSNSql}")
	private String getGetPersonByNMDOBSSN;

	@Value("${SSCCOptCircumListDaoImpl.getSSNByPersonSql}")
	private String getSSNByPerson;

	@Value("${SSCCOptCircumListDaoImpl.getMedicaidByPersonSql}")
	private String getMedicaidByPerson;

	@Value("${SSCCOptCircumListDaoImpl.getCorrospondingPlcmtDtSql}")
	private String getCorrospondingPlcmtDt;

	@Value("${SSCCPlcmtOptCircumDaoImpl.getCountMcCourtAuthSql}")
	private String getCountMcCourtAuth;

	@Value("${SSCCPlcmtOptCircumDaoImpl.getAgncyHmMedCnsntrSql}")
	private String getAgncyHmMedCnsntr;

	@Value("${SSCCPlcmtOptCircumDaoImpl.excpCareLimitSql}")
	private String excpCareLimit;

	@Value("${SSCCPlcmtOptCircumDaoImpl.excpCareDaysUsedSql}")
	private String excpCareDaysUsed;

	@Value("${SSCCPlcmtOptCircumDaoImpl.getMaxDtPlcmtEndHql}")
	private String getMaxDtPlcmtEnd;

	@Value("${SSCCPlcmtOptCircumDaoImpl.getActivePlcmtCntHql}")
	private String getActivePlcmtCnt;

	@Value("${SSCCPlcmtOptCircumDaoImpl.getSSCCPlcmtInPlcmtCntWthAgncyHql}")
	private String getSSCCPlcmtInPlcmtCntWthAgncy;

	@Value("${SSCCPlcmtOptCircumDaoImpl.getSSCCPlcmtInPlcmtCntWoAgncyHql}")
	private String getSSCCPlcmtInPlcmtCntWoAgncy;
	
	@Value("${SSCCPlcmtOptCircumDaoImpl.getActiveMedCnsntrSql}")
	private String getActiveMedCnsntrSql;

	@Value("${SSCCPlcmtOptCircumDaoImpl.getCrspndingPlcmtSILPairHql}")
	private String getCrspndingPlcmtSILPairHql;

	@Autowired
	SessionFactory sessionFactory;

	/**
	 * Method Name: readSSCCPlcmtHeader Method Description: This dao method
	 * reads
	 * 
	 * @param idSSCCPlcmtHeader
	 * @return ssccPlcmtHeaderDto
	 */
	@Override
	public SsccPlcmtHeader readSSCCPlcmtHeader(Long idSSCCPlcmtHeader) {
		log.info("readSSCCPlcmtHeader method of SSCCPlcmtOptCircumDaoImpl : Execution Started.");
		// Fetch the Entity on the base of idSSCCPlcmtHeader
		SsccPlcmtHeader ssccPlcmtHdr = (SsccPlcmtHeader) sessionFactory.getCurrentSession().load(SsccPlcmtHeader.class,
				idSSCCPlcmtHeader);
		log.info("readSSCCPlcmtHeader method of SSCCPlcmtOptCircumDaoImpl : returning Response");
		return ssccPlcmtHdr;
	}

	/**
	 * Method Name: getDfpsStaff Method Description: Method returns the DFPS
	 * staff assigned to the Current Stage
	 * 
	 * @param idStage
	 * @return dfpsStaff
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<Long> getDfpsStaff(Long idStage) {
		log.info("getDfpsStaff method of SSCCPlcmtOptCircumDaoImpl : Execution Started.");
		Set<Long> dfpsStaff = new HashSet<>();
		List<Long> resultList = sessionFactory.getCurrentSession().createSQLQuery(getDfpsStaff)
				.addScalar("person", StandardBasicTypes.LONG).setParameter("idStage", idStage).list();
		if (!ObjectUtils.isEmpty(resultList)) {
			dfpsStaff.addAll(resultList);
		}
		log.info("getDfpsStaff method of SSCCPlcmtOptCircumDaoImpl : Returning Response");
		return dfpsStaff;
	}

	/**
	 * Method Name: getCpaOthrMedCnsntrs Method Description: Method returns
	 * Medical Consentrs for CPA/Other Homes from SSCC Placement resource link
	 * 
	 * @param idRsrcSSCC
	 * @param idRsrcFacil
	 * @return medCnsntr
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<Long> getCpaOthrMedCnsntrs(Long idRsrcSSCC, Long idRsrcFacil) {
		log.info("getCpaOthrMedCnsntrs method of SSCCPlcmtOptCircumDaoImpl : Execution Started.");
		Set<Long> medCnsntr = new HashSet<>();
		List<Long> resultList = sessionFactory.getCurrentSession().createQuery(getMedCnsntrForCpaOthrHm)
				.setParameter("idRsrcSscc", idRsrcSSCC).setParameter("idRsrcMember", idRsrcFacil).list();
		if (!ObjectUtils.isEmpty(resultList)) {
			medCnsntr.addAll(resultList);
		}
		log.info("getCpaOthrMedCnsntrs method of SSCCPlcmtOptCircumDaoImpl : Returning Response");
		return medCnsntr;
	}

	/**
	 * Method Name: MapfindMedCnsntrPersByNmDobSsn Method Description: Method
	 * fetches and retuns the List of person ids on the basis of dob, name and
	 * ssn.
	 * 
	 * @param ssccPlcmtMedCnsntr
	 * @return personList
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> findMedCnsntrPersByNmDobSsn(SSCCPlcmtMedCnsntrDto ssccPlcmtMedCnsntr) {
		log.info("findMedCnsntrPersByNmDobSsn method of SSCCPlcmtOptCircumDaoImpl : Execution Started.");
		List<Long> personList = (List<Long>) sessionFactory.getCurrentSession().createSQLQuery(getGetPersonByNMDOBSSN)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.setParameter("dtPersonBirth", ssccPlcmtMedCnsntr.getDtPersonBirth())
				.setParameter("nmPersonFirst", ssccPlcmtMedCnsntr.getNmPersonFirst())
				.setParameter("nmPersonLast", ssccPlcmtMedCnsntr.getNmPersonLast())
				.setParameter("nbrPersonSsn", ssccPlcmtMedCnsntr.getNbrPersonSsn()).list();
		log.info("findMedCnsntrPersByNmDobSsn method of SSCCPlcmtOptCircumDaoImpl : returning Response");
		return personList;

	}

	/**
	 * Method Name: getSSNByPerson Method Description: Method fetches the SSN
	 * for the given person id.
	 * 
	 * @param idPerson
	 * @return personSSN
	 */
	@Override
	public String getSSNByPerson(Long idPerson) {
		log.info("getSSNByPerson method of SSCCPlcmtOptCircumDaoImpl : Execution Started.");
		String personSSN = (String) sessionFactory.getCurrentSession().createSQLQuery(getSSNByPerson)
				.addScalar("ssnNumber", StandardBasicTypes.STRING).setParameter("idPerson", idPerson).uniqueResult();
		log.info("getSSNByPerson method of SSCCPlcmtOptCircumDaoImpl : Returning Response");
		return personSSN;
	}

	/**
	 * Method Name: getMedicaidByPerson Method Description: Method fetches the Medicaid
	 * for the given person id.
	 *
	 * @param idPerson
	 * @return person Medicaid
	 */
	@Override
	public String getMedicaidByPerson(Long idPerson) {
		String personMedicaidNumber = (String) sessionFactory.getCurrentSession().createSQLQuery(getMedicaidByPerson)
				.addScalar("medicaidNumber", StandardBasicTypes.STRING).setParameter("idPerson", idPerson).uniqueResult();
		return personMedicaidNumber;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.sscc.dao.SSCCPlcmtOptCircumDao#
	 * saveSSCCPlcmtHeader(us.tx.state.dfps.common.domain.SsccPlcmtHeader)
	 */
	@Override
	public void saveSSCCPlcmtHeader(SsccPlcmtHeader ssccPlcmtHeader) {
		if (!StringUtils.isEmpty(ssccPlcmtHeader.getIdSSCCPlcmtHeader())) {
			sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(ssccPlcmtHeader));
		} else {
			sessionFactory.getCurrentSession().saveOrUpdate(ssccPlcmtHeader);
		}
	}

	/**
	 * Method Name: getCorrospondingPlcmtDt Method Description: Method retuns
	 * the Dt placement start for the give idStage
	 * 
	 * @param idStage
	 * @param idRsrcSSCC
	 * @param idRsrcFacil
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Date getCorrospondingPlcmtDt(Long idStage, Long idRsrcSSCC, Long idRsrcFacil) {
		log.info("getCorrospondingPlcmtDt method of SSCCPlcmtOptCircumDaoImpl : Execution Started.");
		Date dtPlcmtStart = null;
		List<Date> plcmtDateLst = (List<Date>) sessionFactory.getCurrentSession()
				.createSQLQuery(getCorrospondingPlcmtDt).addScalar("dtPlcmtStart", StandardBasicTypes.DATE)
				.setParameter("idStage", idStage).setParameter("idRsrcSSCC", idRsrcSSCC)
				.setParameter("idRsrcFacil", idRsrcFacil).list();
		if (!ObjectUtils.isEmpty(plcmtDateLst)) {
			dtPlcmtStart = plcmtDateLst.get(0);
		}
		log.info("getCorrospondingPlcmtDt method of SSCCPlcmtOptCircumDaoImpl : Returning Response");
		return dtPlcmtStart;
	}

	/**
	 * Method Name: getMcCourtAuthCnt Method Description: Method retuns the
	 * count of active medical Concenter with ind_court_auth as Y.
	 * 
	 * @param idSSCCReferral
	 * @return
	 */
	@Override
	public Long getMcCourtAuthCnt(Long idSSCCReferral) {
		BigDecimal count = (BigDecimal) sessionFactory.getCurrentSession().createSQLQuery(getCountMcCourtAuth)
				.setParameter("idSSCCReferral", idSSCCReferral).uniqueResult();
		return count.longValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.sscc.dao.SSCCPlcmtOptCircumDao#saveSSCCPlcmtName
	 * (us.tx.state.dfps.common.domain.SsccPlcmtName)
	 */
	@Override
	public void saveSSCCPlcmtName(SsccPlcmtName ssccPlcmtName) {
		sessionFactory.getCurrentSession().saveOrUpdate(ssccPlcmtName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.sscc.dao.SSCCPlcmtOptCircumDao#
	 * saveSSCCPlcmtMedCnsntr(us.tx.state.dfps.common.domain.SsccPlcmtMedCnsntr)
	 */
	@Override
	public void saveSSCCPlcmtMedCnsntr(SsccPlcmtMedCnsntr ssccPlcmtMedCnsntr) {
		sessionFactory.getCurrentSession().saveOrUpdate(ssccPlcmtMedCnsntr);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.sscc.dao.SSCCPlcmtOptCircumDao#saveSSCCPlcmtInfo
	 * (us.tx.state.dfps.common.domain.SsccPlcmtInfo)
	 */
	@Override
	public void saveSSCCPlcmtInfo(SsccPlcmtInfo ssccPlcmtInfo) {
		sessionFactory.getCurrentSession().saveOrUpdate(ssccPlcmtInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.sscc.dao.SSCCPlcmtOptCircumDao#
	 * saveSsccPlcmtPlaced(us.tx.state.dfps.common.domain.SsccPlcmtPlaced)
	 */
	@Override
	public void saveSSCCPlcmtPlaced(SsccPlcmtPlaced ssccPlcmtPlaced) {
		sessionFactory.getCurrentSession().saveOrUpdate(ssccPlcmtPlaced);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.sscc.dao.SSCCPlcmtOptCircumDao#
	 * saveSsccPlcmtCircumstance(us.tx.state.dfps.common.domain.
	 * SsccPlcmtCircumstance)
	 */
	@Override
	public void saveSsccPlcmtCircumstance(SsccPlcmtCircumstance ssccPlcmtCircumstance) {
		sessionFactory.getCurrentSession().saveOrUpdate(ssccPlcmtCircumstance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.sscc.dao.SSCCPlcmtOptCircumDao#
	 * getSSCCPlcmtPlaced(java.lang.Long)
	 */
	@Override
	public SsccPlcmtPlaced getSSCCPlcmtPlaced(Long idSSCCPlcmtPlaced) {
		// Fetch the Entity on the base of idSSCCPlcmtPlaced
		SsccPlcmtPlaced ssccPlcmtPlaced = (SsccPlcmtPlaced) sessionFactory.getCurrentSession()
				.createCriteria(SsccPlcmtPlaced.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.add(Restrictions.eq("idSSCCPlcmtPlaced", idSSCCPlcmtPlaced)).uniqueResult();
		return ssccPlcmtPlaced;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.sscc.dao.SSCCPlcmtOptCircumDao#getSSCCPlcmtName(
	 * java.lang.Long)
	 */
	@Override
	public SsccPlcmtName getSSCCPlcmtName(Long idSSCCPlcmtName) {
		// Fetch the Entity on the base of idSSCCPlcmtName
		SsccPlcmtName ssccPlcmtName = (SsccPlcmtName) sessionFactory.getCurrentSession()
				.createCriteria(SsccPlcmtName.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.add(Restrictions.eq("idSSCCPlcmtName", idSSCCPlcmtName)).uniqueResult();
		return ssccPlcmtName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.sscc.dao.SSCCPlcmtOptCircumDao#
	 * getSSCCPlcmtMedCnsntr(java.lang.Long)
	 */
	@Override
	public SsccPlcmtMedCnsntr getSSCCPlcmtMedCnsntr(Long idSSCCPlcmtMedCnsntr) {
		// Fetch the Entity on the base of idSSCCPlcmtMedCnsntr
		SsccPlcmtMedCnsntr ssccPlcmtMedCnsntr = (SsccPlcmtMedCnsntr) sessionFactory.getCurrentSession()
				.createCriteria(SsccPlcmtMedCnsntr.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.add(Restrictions.eq("idSSCCPlcmtMedCnsntr", idSSCCPlcmtMedCnsntr)).uniqueResult();
		return ssccPlcmtMedCnsntr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.sscc.dao.SSCCPlcmtOptCircumDao#getSSCCPlcmtInfo(
	 * java.lang.Long)
	 */
	@Override
	public SsccPlcmtInfo getSSCCPlcmtInfo(Long idSSCCPlcmtInfo) {
		// Fetch the Entity on the base of idSSCCPlcmtInfo
		SsccPlcmtInfo ssccPlcmtInfo = (SsccPlcmtInfo) sessionFactory.getCurrentSession()
				.createCriteria(SsccPlcmtInfo.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.add(Restrictions.eq("idSSCCPlcmtInfo", idSSCCPlcmtInfo)).uniqueResult();
		return ssccPlcmtInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.sscc.dao.SSCCPlcmtOptCircumDao#
	 * getSSCCPlcmtCircumstance(java.lang.Long)
	 */
	@Override
	public SsccPlcmtCircumstance getSSCCPlcmtCircumstance(Long idSSCCPlcmtCircumstance) {
		// Fetch the Entity on the base of idSSCCPlcmtCircumstance
		SsccPlcmtCircumstance ssccPlcmtCircumstance = (SsccPlcmtCircumstance) sessionFactory.getCurrentSession()
				.createCriteria(SsccPlcmtCircumstance.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.add(Restrictions.eq("idSSCCPlcmtCircumstance", idSSCCPlcmtCircumstance)).uniqueResult();
		return ssccPlcmtCircumstance;
	}

	/**
	 * Method Name: getSavedSSCCMedCnsntr Method Description: Method to fetch
	 * the SSCCPlcmntMedCnsntr saved record for given placement Header and
	 * medCnsntrType.
	 * 
	 * @param idSSCCPlcmtHeader
	 * @param medCnsntrType
	 * @return SsccPlcmtMedCnsntr
	 */
	@Override
	public SsccPlcmtMedCnsntr getSavedSSCCMedCnsntr(Long idSSCCPlcmtHeader, String medCnsntrType) {
		log.info("getSavedSSCCMedCnsntr method of SSCCPlcmtOptCircumDaoImpl : Execution Started.");
		// Fetch the Med Concenter saved in DB for given header and type.
		SsccPlcmtMedCnsntr ssccPlcmtMedCnsntr = (SsccPlcmtMedCnsntr) sessionFactory.getCurrentSession()
				.createCriteria(SsccPlcmtMedCnsntr.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.add(Restrictions.eq("ssccPlcmtHeader", idSSCCPlcmtHeader))
				.add(Restrictions.eq("cdMedConsenterType", medCnsntrType)).addOrder(Order.desc("nbrVersion"))
				.setMaxResults(1).uniqueResult();
		log.info("getSavedSSCCMedCnsntr method of SSCCPlcmtOptCircumDaoImpl : Returning Response.");
		return !ObjectUtils.isEmpty(ssccPlcmtMedCnsntr) ? ssccPlcmtMedCnsntr : null;
	}

	/**
	 * Method Name: getAgencyHmMedCnsntr Method Description: Method to fetch the
	 * Hasmap of Primary and Secondary Med Concenter for Agency Homes from
	 * resource caretaker id/personid linkage
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public HashMap<String, Long> getAgencyHmMedCnsntr(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("getAgencyHmMedCnsntr method of SSCCPlcmtOptCircumDaoImpl : Execution Started.");
		HashMap<String, Long> medCnsntr = new HashMap<>();
		List<AgncyHmMedCnsntrDto> agncyHmMedCnsntr = (List<AgncyHmMedCnsntrDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getAgncyHmMedCnsntr).addScalar("idCaretaker", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("rsrcPerCrtkr", StandardBasicTypes.LONG)
				.setParameter("idResource", ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdRsrcFacil())
				.setResultTransformer(Transformers.aliasToBean(AgncyHmMedCnsntrDto.class)).list();
		// get Only first row - FPRI and Second Row SPRI
		if (!ObjectUtils.isEmpty(agncyHmMedCnsntr)) {
			if (!ObjectUtils.isEmpty(agncyHmMedCnsntr.get(0))) {
				medCnsntr.put(CodesConstant.CMCTYPE_FPRI, agncyHmMedCnsntr.get(0).getIdPerson());
				ssccPlcmtOptCircumDto.setDupCrGvr(agncyHmMedCnsntr.get(0).getRsrcPerCrtkr().longValue() > 1l);
			}
			if (agncyHmMedCnsntr.size() > 1 && !ObjectUtils.isEmpty(agncyHmMedCnsntr.get(1))) {
				medCnsntr.put(CodesConstant.CMCTYPE_SPRI, agncyHmMedCnsntr.get(1).getIdPerson());
				if (agncyHmMedCnsntr.get(1).getRsrcPerCrtkr().longValue() > 1l) {
					ssccPlcmtOptCircumDto.setDupCrGvr(true);
				}
			}
			// If the List has size more than 2, Certainly duplicate Med Cnsntr
			// available, hence set the flag.
			if (agncyHmMedCnsntr.size() > 2) {
				ssccPlcmtOptCircumDto.setDupCrGvr(true);
			}
		}
		log.info("getAgencyHmMedCnsntr method of SSCCPlcmtOptCircumDaoImpl : Returning Response.");
		return medCnsntr;
	}

	/**
	 * Method Name: excpCareBudgetDaysExceeded Method Description: Method checks
	 * if number of days for exceptional care has exceeded the budgeted limit.
	 * 
	 * @param ssccPlcmtHeaderDto
	 * @param useDtPlcmtStart
	 * @return Boolean
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Boolean excpCareBudgetDaysExceeded(SSCCPlcmtHeaderDto ssccPlcmtHeaderDto, Boolean useDtPlcmtStart) {
		log.info("excpCareBudgetDaysExceeded method of SSCCPlcmtOptCircumDaoImpl : Execution Started.");
		long expCareLimitDays = 0l;
		long expCareDaysUsed = 0l;
		useDtPlcmtStart = Boolean.TRUE;

		List<BigDecimal> expCareLimitDaysList = sessionFactory.getCurrentSession().createSQLQuery(excpCareLimit)
				.setParameter("idSSCCReferral", ssccPlcmtHeaderDto.getIdSSCCReferral()).list();
		expCareLimitDays = expCareLimitDaysList.get(0).longValue();
		List<BigDecimal> expCareDaysUsedList = sessionFactory.getCurrentSession().createSQLQuery(excpCareDaysUsed)
				.setParameter("idSSCCReferral", ssccPlcmtHeaderDto.getIdSSCCReferral()).list();
		expCareDaysUsed = (!ObjectUtils.isEmpty(expCareDaysUsedList)
				&& !ObjectUtils.isEmpty(expCareDaysUsedList.get(0))) ? expCareDaysUsedList.get(0).longValue()
						: ServiceConstants.ZERO;
		if (useDtPlcmtStart) {
			Date today = new Date();
			if (ssccPlcmtHeaderDto.getDtPlcmtStart().before(today)) {
				expCareDaysUsed += DateUtils.daysDifference(today, ssccPlcmtHeaderDto.getDtPlcmtStart());
			}
		}
		// check if budgeted days excceded
		log.info("excpCareBudgetDaysExceeded method of SSCCPlcmtOptCircumDaoImpl : Returing Response.");
		return expCareDaysUsed >= expCareLimitDays;
	}

	/**
	 * Method Name: checkHeaderStatusChanged Method Description: Method Checks
	 * if the Header Status is changed in DB
	 * 
	 * @param ssccPlcmtHdrDto
	 * @return boolean
	 */
	@Override
	public Boolean checkHeaderStatusChanged(SSCCPlcmtHeaderDto ssccPlcmtHdrDto) {
		String currentStatus = (String) sessionFactory.getCurrentSession().createCriteria(SsccPlcmtHeader.class)
				.setProjection(Projections.property("cdStatus"))
				.add(Restrictions.eq("idSSCCPlcmtHeader", ssccPlcmtHdrDto.getIdSSCCPlcmtHeader())).uniqueResult();
		return !ssccPlcmtHdrDto.getCdStatus().equalsIgnoreCase(currentStatus);
	}

	/**
	 * Method Name: checkDtPlcmtStartChanged Method Description: Method checks
	 * if the DtPlcmtStart is changed in DB.
	 * 
	 * @param ssccPlcmtHdrDto
	 * @return
	 */
	@Override
	public Boolean checkDtPlcmtStartChanged(SSCCPlcmtHeaderDto ssccPlcmtHdrDto) {
		Date dtPlcmtStart = (Date) sessionFactory.getCurrentSession().createCriteria(SsccPlcmtHeader.class)
				.setProjection(Projections.property("dtPlcmtStart"))
				.add(Restrictions.eq("idSSCCPlcmtHeader", ssccPlcmtHdrDto.getIdSSCCPlcmtHeader())).uniqueResult();
		return ssccPlcmtHdrDto.getDtPlcmtStart().compareTo(dtPlcmtStart) != ServiceConstants.Zero_INT;
	}

	/**
	 * 
	 * Method Name: deletePlcmtHeader Method Description: this method deletes
	 * placement header
	 * 
	 * @param idSSCCPlcmtHeader
	 */
	@Override
	public void deletePlcmtHeader(Long idSSCCPlcmtHeader) {
		SsccPlcmtHeader ssccPlcmtHeader = (SsccPlcmtHeader) sessionFactory.getCurrentSession()
				.load(SsccPlcmtHeader.class, idSSCCPlcmtHeader);
		if (!ObjectUtils.isEmpty(ssccPlcmtHeader)) {
			sessionFactory.getCurrentSession().delete(ssccPlcmtHeader);
		}
	}

	/**
	 * 
	 * Method Name: deletePlcmtMedCnsntrs Method Description: This method
	 * deletes placement medical consenters
	 * 
	 * @param idSSCCPlcmtMedCnsntrs
	 */
	@Override
	public void deletePlcmtMedCnsntrs(List<Long> idSSCCPlcmtMedCnsntrs) {
		if (!ObjectUtils.isEmpty(idSSCCPlcmtMedCnsntrs)) {
			idSSCCPlcmtMedCnsntrs.forEach(o -> {
				SsccPlcmtMedCnsntr ssccPlcmtMedCnsntr = (SsccPlcmtMedCnsntr) sessionFactory.getCurrentSession()
						.load(SsccPlcmtMedCnsntr.class, o);
				sessionFactory.getCurrentSession().delete(ssccPlcmtMedCnsntr);
			});
		}
	}

	/**
	 * 
	 * Method Name: deletePlcmtCircumstance Method Description: This method
	 * deletes the placement circumstance
	 * 
	 * @param idSSCCPlcmtCircumstance
	 */
	@Override
	public void deletePlcmtCircumstance(Long idSSCCPlcmtCircumstance) {
		SsccPlcmtCircumstance ssccPlcmtCircumstance = (SsccPlcmtCircumstance) sessionFactory.getCurrentSession()
				.load(SsccPlcmtCircumstance.class, idSSCCPlcmtCircumstance);
		if (!ObjectUtils.isEmpty(ssccPlcmtCircumstance)) {
			sessionFactory.getCurrentSession().delete(ssccPlcmtCircumstance);
		}
	}

	/**
	 * 
	 * Method Name: deletePlcmtName Method Description: This method deletes the
	 * placement name.
	 * 
	 * @param idSSCCPlcmtName
	 */
	@Override
	public void deletePlcmtName(Long idSSCCPlcmtName) {
		SsccPlcmtName ssccPlcmtName = (SsccPlcmtName) sessionFactory.getCurrentSession().load(SsccPlcmtName.class,
				idSSCCPlcmtName);
		if (!ObjectUtils.isEmpty(ssccPlcmtName)) {
			sessionFactory.getCurrentSession().delete(ssccPlcmtName);
		}
	}

	/**
	 * 
	 * Method Name: deletePlcmtInfo Method Description: This method deletes
	 * placement info
	 * 
	 * @param idSSCCPlcmtInfo
	 */
	@Override
	public void deletePlcmtInfo(Long idSSCCPlcmtInfo) {
		SsccPlcmtInfo ssccPlcmtInfo = (SsccPlcmtInfo) sessionFactory.getCurrentSession().load(SsccPlcmtInfo.class,
				idSSCCPlcmtInfo);
		if (!ObjectUtils.isEmpty(ssccPlcmtInfo)) {
			sessionFactory.getCurrentSession().delete(ssccPlcmtInfo);
		}
	}

	/**
	 * 
	 * Method Name: deletePlcmtPlaced Method Description: This method deletes
	 * placement placed.
	 * 
	 * @param idSsccPlcmtPlaced
	 */
	@Override
	public void deletePlcmtPlaced(Long idSsccPlcmtPlaced) {
		SsccPlcmtPlaced ssccPlcmtPlaced = (SsccPlcmtPlaced) sessionFactory.getCurrentSession()
				.load(SsccPlcmtPlaced.class, idSsccPlcmtPlaced);
		if (!ObjectUtils.isEmpty(ssccPlcmtPlaced)) {
			sessionFactory.getCurrentSession().delete(ssccPlcmtPlaced);
		}
	}

	/**
	 * 
	 * Method Name: deletePlcmtNarr Method Description: This method deletes
	 * placement narrative.
	 * 
	 * @param idSSCCPlcmtNarr
	 */
	@Override
	public void deletePlcmtNarr(Long idSSCCPlcmtNarr) {
		SsccPlcmtNarr ssccPlcmtNarr = (SsccPlcmtNarr) sessionFactory.getCurrentSession().load(SsccPlcmtNarr.class,
				idSSCCPlcmtNarr);
		if (!ObjectUtils.isEmpty(ssccPlcmtNarr)) {
			sessionFactory.getCurrentSession().delete(ssccPlcmtNarr);
		}
	}

	/**
	 * Method Name: checkForRevChange Method Description: This method checks if
	 * the four medical consenter types person id have been changed if so
	 * returns true
	 * 
	 * @param ssccMedCnsntrDtoMap
	 * @return boolean
	 */
	@Override
	public Boolean checkForRevChange(HashMap<String, SSCCPlcmtMedCnsntrDto> ssccMedCnsntrDtoMap) {
		log.info("checkForRevChange method of SSCCPlcmtOptCircumDaoImpl : Execution Started.");
		SSCCPlcmtMedCnsntrDto ssccPlcmtMedCnsntr = ssccMedCnsntrDtoMap.get(CodesConstant.CMCTYPE_FPRI);
		// Compare the FPRI idMedCnsntrPerson if it is changed.
		if (!ObjectUtils.isEmpty(ssccPlcmtMedCnsntr)
				&& SSCCPlcmtOptCircumUtil.isNonZeroLong(ssccPlcmtMedCnsntr.getIdMedConsenterPerson())) {
			// Get the record in DB
			SsccPlcmtMedCnsntr ssccPlcmtMedCnsntrDB = (SsccPlcmtMedCnsntr) sessionFactory.getCurrentSession()
					.load(SsccPlcmtMedCnsntr.class, ssccPlcmtMedCnsntr.getIdSSCCPlcmtMedCnsntr());
			// Compare and return the boolean.
			if (!ssccPlcmtMedCnsntr.getIdMedConsenterPerson().equals(ssccPlcmtMedCnsntrDB.getIdMedConsenterPerson())) {
				log.info("checkForRevChange method of SSCCPlcmtOptCircumDaoImpl : Returning Response.");
				return true;
			}
		}
		// Compare SPRI Med Concenter.
		ssccPlcmtMedCnsntr = ssccMedCnsntrDtoMap.get(CodesConstant.CMCTYPE_SPRI);
		if (!ObjectUtils.isEmpty(ssccPlcmtMedCnsntr)
				&& SSCCPlcmtOptCircumUtil.isNonZeroLong(ssccPlcmtMedCnsntr.getIdMedConsenterPerson())) {
			// Get the record in DB
			SsccPlcmtMedCnsntr ssccPlcmtMedCnsntrDB = (SsccPlcmtMedCnsntr) sessionFactory.getCurrentSession()
					.load(SsccPlcmtMedCnsntr.class, ssccPlcmtMedCnsntr.getIdSSCCPlcmtMedCnsntr());
			// Compare and return the boolean.
			if (!ssccPlcmtMedCnsntr.getIdMedConsenterPerson().equals(ssccPlcmtMedCnsntrDB.getIdMedConsenterPerson())) {
				log.info("checkForRevChange method of SSCCPlcmtOptCircumDaoImpl : Returning Response.");
				return true;
			}
		}

		// Compare FBUP.
		ssccPlcmtMedCnsntr = ssccMedCnsntrDtoMap.get(CodesConstant.CMCTYPE_FBUP);
		if (!ObjectUtils.isEmpty(ssccPlcmtMedCnsntr)
				&& SSCCPlcmtOptCircumUtil.isNonZeroLong(ssccPlcmtMedCnsntr.getIdMedConsenterPerson())) {
			// Get the record in DB
			SsccPlcmtMedCnsntr ssccPlcmtMedCnsntrDB = (SsccPlcmtMedCnsntr) sessionFactory.getCurrentSession()
					.load(SsccPlcmtMedCnsntr.class, ssccPlcmtMedCnsntr.getIdSSCCPlcmtMedCnsntr());
			// Compare and return the boolean.
			if (!ssccPlcmtMedCnsntr.getIdMedConsenterPerson().equals(ssccPlcmtMedCnsntrDB.getIdMedConsenterPerson())) {
				log.info("checkForRevChange method of SSCCPlcmtOptCircumDaoImpl : Returning Response.");
				return true;
			}
		}
		// Compary SBUP
		ssccPlcmtMedCnsntr = ssccMedCnsntrDtoMap.get(CodesConstant.CMCTYPE_SBUP);
		if (!ObjectUtils.isEmpty(ssccPlcmtMedCnsntr)
				&& SSCCPlcmtOptCircumUtil.isNonZeroLong(ssccPlcmtMedCnsntr.getIdMedConsenterPerson())) {
			// Get the record in DB
			SsccPlcmtMedCnsntr ssccPlcmtMedCnsntrDB = (SsccPlcmtMedCnsntr) sessionFactory.getCurrentSession()
					.load(SsccPlcmtMedCnsntr.class, ssccPlcmtMedCnsntr.getIdSSCCPlcmtMedCnsntr());
			// Compare and return the boolean.
			if (!ssccPlcmtMedCnsntr.getIdMedConsenterPerson().equals(ssccPlcmtMedCnsntrDB.getIdMedConsenterPerson())) {
				log.info("checkForRevChange method of SSCCPlcmtOptCircumDaoImpl : Returning Response.");
				return true;
			}
		}
		log.info("checkForRevChange method of SSCCPlcmtOptCircumDaoImpl : Returning Response.");
		return false;
	}

	/**
	 * Method Name: getMaxDtPlcmtEnd Method Description: This method returns
	 * maximum placement end date for child
	 * 
	 * @param idActiveRef
	 * @return
	 */
	@Override
	public Date getMaxDtPlcmtEnd(Long idActiveRef) {
		log.info("getMaxDtPlcmtEnd method of SSCCPlcmtOptCircumDaoImpl : Execution Started");
		Date maxDate = (Date) sessionFactory.getCurrentSession().createQuery(getMaxDtPlcmtEnd)
				.setParameter("idReferral", idActiveRef).uniqueResult();
		log.info("getMaxDtPlcmtEnd method of SSCCPlcmtOptCircumDaoImpl : Returning Response.");
		return maxDate;
	}

	/**
	 * Method Name: getActivePlcmtCnt Method Description: This method returns
	 * number of active placements
	 * 
	 * @param idActiveRef
	 * @return count
	 */
	@Override
	public Long getActivePlcmtCnt(Long idActiveRef) {
		log.info("getActivePlcmtCnt method of SSCCPlcmtOptCircumDaoImpl : Execution Started");
		Long count = (Long) sessionFactory.getCurrentSession().createQuery(getActivePlcmtCnt)
				.setParameter("idReferral", idActiveRef).uniqueResult();
		log.info("getActivePlcmtCnt method of SSCCPlcmtOptCircumDaoImpl : Returning Response.");
		return count;
	}

	/**
	 * Method Name: getSSCCPlcmtInPlcmtCnt Method Description: This method
	 * returns number of active placements
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return count
	 */
	@Override
	public Long getSSCCPlcmtInPlcmtCnt(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("getSSCCPlcmtInPlcmtCnt method of SSCCPlcmtOptCircumDaoImpl : Execution Started");
		Query query = null;
		if(SSCCPlcmtOptCircumUtil.isNonZeroLong(ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdRsrcAgency())){
			query = sessionFactory.getCurrentSession().createQuery(getSSCCPlcmtInPlcmtCntWthAgncy);
		}else{
			query = sessionFactory.getCurrentSession().createQuery(getSSCCPlcmtInPlcmtCntWoAgncy);
		}
		Long count = (Long) query.setParameter("idReferral", ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCReferral())
				.setParameter("idPlcmtHeader", ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCPlcmtHeader())
				.setParameter("idPlcmtName", ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdSSCCPlcmtName())
				.uniqueResult();
		log.info("getSSCCPlcmtInPlcmtCnt method of SSCCPlcmtOptCircumDaoImpl : Returning Response.");
		return count;
	}

	/**
	 * Method Name: getCrspndingPlcmtSILPair Method Description:This method
	 * returns corresponding SSCC Placement Living Arrangement count for any
	 * parent for the child in stage
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	@Override
	public Long getCrspndingPlcmtSILPair(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		log.info("getCrspndingPlcmtSILPair method of SSCCPlcmtOptCircumDaoImpl : Execution Started");
		Long count = (Long) sessionFactory.getCurrentSession().createQuery(getCrspndingPlcmtSILPairHql)
				.setParameter("idStage", ssccPlcmtOptCircumDto.getIdStage())
				.setParameter("idRsrcSSCC", ssccPlcmtOptCircumDto.getSsccResourceDto().getIdSSCCResource())
				.setParameter("idRsrcFacil", ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getIdRsrcFacil())
				.setParameter("livingArr", ssccPlcmtOptCircumDto.getSsccPlcmtNameDto().getCdPlcmtLivArr())
				.uniqueResult();
		log.info("getCrspndingPlcmtSILPair method of SSCCPlcmtOptCircumDaoImpl : Returning Response.");
		return count;
	}

	/**
	 * Method Name: getActiveMedCnsntr Method Description: This method returns a
	 * Hash Map of Active Medical Consenters by Med consenter types
	 * 
	 * @param idActiveRef
	 * @return activeMedCnsntrMap
	 */
	@SuppressWarnings("unchecked")
	@Override
	public HashMap<String, MedicalConsenterDto> getActiveMedCnsntr(Long idActiveRef) {
		log.info("getActiveMedCnsntr method of SSCCPlcmtOptCircumDaoImpl : Execution Started.");
		HashMap<String, MedicalConsenterDto> activeMedCnsntrMap = new HashMap<>();
		List<MedicalConsenterDto> activeMedCnsntrs = sessionFactory.getCurrentSession()
				.createSQLQuery(getActiveMedCnsntrSql).addScalar("idMedConsenter", StandardBasicTypes.LONG)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idMedConsenterPerson", StandardBasicTypes.LONG)
				.addScalar("cdMedConsenterType", StandardBasicTypes.STRING)
				.addScalar("cdCourtAuth", StandardBasicTypes.STRING).addScalar("cdDfpsDesig", StandardBasicTypes.STRING)
				.addScalar("dtMedConsStart", StandardBasicTypes.DATE).addScalar("dtMedConsEnd", StandardBasicTypes.DATE)
				.setParameter("idReferral", idActiveRef)
				.setResultTransformer(Transformers.aliasToBean(MedicalConsenterDto.class)).list();

		if (!ObjectUtils.isEmpty(activeMedCnsntrs)) {
			activeMedCnsntrs.stream().forEach(medCnsntr -> {
				activeMedCnsntrMap.put(medCnsntr.getCdMedConsenterType(), medCnsntr);
			});
		}
		// check if budgeted days excceded
		log.info("getActiveMedCnsntr method of SSCCPlcmtOptCircumDaoImpl : aciveMedCnstr " + activeMedCnsntrMap.size());
		return activeMedCnsntrMap;
	}

	/**
	 * Method Name: updatePersonStatusOnSSCCPlcmt Method Description: Method
	 * updates the Med Concenter Person Status to Active, if It is Inactive or
	 * Null in DB when Placement is created.
	 * 
	 * @param idPerson
	 */
	@Override
	public void updatePersonStatusOnSSCCPlcmt(Long idPerson) {
		log.info("updatePersonStatusOnSSCCPlcmt method of SSCCPlcmtOptCircumDaoImpl : Execution Started.");
		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, idPerson);
		if (StringUtils.isEmpty(person.getCdPersonStatus()) || (!StringUtils.isEmpty(person.getCdPersonStatus())
				&& CodesConstant.CPERSTAT_I.equals(person.getCdPersonStatus()))) {
			person.setCdPersonStatus(CodesConstant.CPERSTAT_A);
			sessionFactory.getCurrentSession().update(person);
			log.info("updatePersonStatusOnSSCCPlcmt method of SSCCPlcmtOptCircumDaoImpl : Updated Person Status");
		}
	}

	/**
	 * Method Name: getDupOptOrCircumStatus Method Description: Method checks if
	 * any Outstanding Placemtn is already available.
	 * 
	 * @param idSSCCReferal
	 * @param cdSSCCPlcmtType
	 * @return
	 */
	@Override
	public String getDupOptOrCircumStatus(Long idSSCCReferal, String cdSSCCPlcmtType) {
		log.info("getDupOptOrCircumStatus method of SSCCPlcmtOptCircumDaoImpl : Execution Started.");
		String status = ServiceConstants.EMPTY_STRING;
		if (CodesConstant.CSSCCPTY_10.equals(cdSSCCPlcmtType)) {
			List<String> statusList = Arrays.asList(CodesConstant.CSSCCSTA_30, CodesConstant.CSSCCSTA_50,
					CodesConstant.CSSCCSTA_60, CodesConstant.CSSCCSTA_90, CodesConstant.CSSCCSTA_110);
			status = (String) sessionFactory.getCurrentSession().createCriteria(SsccPlcmtHeader.class)
					.setProjection(Projections.property("cdStatus"))
					.add(Restrictions.eq("ssccReferral.idSSCCReferral", idSSCCReferal))
					.add(Restrictions.eq("cdSSCCPlcmtType", CodesConstant.CSSCCPTY_10))
					.add(Restrictions.in("cdStatus", statusList)).uniqueResult();
		} else {
			List<String> statusList = Arrays.asList(CodesConstant.CSSCCSTA_60, CodesConstant.CSSCCSTA_50);
			status = (String) sessionFactory.getCurrentSession().createCriteria(SsccPlcmtHeader.class)
					.setProjection(Projections.property("cdStatus"))
					.add(Restrictions.eq("ssccReferral.idSSCCReferral", idSSCCReferal))
					.add(Restrictions.ne("cdSSCCPlcmtType", CodesConstant.CSSCCPTY_10))
					.add(Restrictions.in("cdStatus", statusList)).uniqueResult();
		}
		log.info("getDupOptOrCircumStatus method of SSCCPlcmtOptCircumDaoImpl : Returning Response");
		return status;
	}

	/**
	 * Method Name: getSSCCPlcmtNarrative Method Description: Method fetches the
	 * Unique Narrative Record for the passed idPlcmtHdr and nbrVersion.
	 * 
	 * @param idSSCCPlcmtHdr
	 * @param nbrVersion
	 * @return
	 */
	@Override
	public SsccPlcmtNarr getSSCCPlcmtNarrative(Long idSSCCPlcmtHdr, Long nbrVersion) {
		log.info("getSSCCPlcmtNarrative method of SSCCPlcmtOptCircumDaoImpl : Execution Started.");
		SsccPlcmtNarr ssccPlcmtNarr = (SsccPlcmtNarr) sessionFactory.getCurrentSession()
				.createCriteria(SsccPlcmtNarr.class)
				.add(Restrictions.eq("ssccPlcmtHeader.idSSCCPlcmtHeader", idSSCCPlcmtHdr))
				.add(Restrictions.eq("nbrVersion", nbrVersion)).uniqueResult();
		log.info("getSSCCPlcmtNarrative method of SSCCPlcmtOptCircumDaoImpl : Returning Response");
		return ssccPlcmtNarr;
	}

	/**
	 * Method Name: saveSSCCPlcmtNarr Method Description: Method saves the
	 * narrative in DB
	 * 
	 * @param ssccPlcmtName
	 */
	@Override
	public void saveSSCCPlcmtNarr(SsccPlcmtNarr ssccPlcmtName) {
		sessionFactory.getCurrentSession().save(ssccPlcmtName);
	}

	/**
	 * Method Name: savePlcmtIssueNarr Method Description: Method Creates the
	 * Placement Issue Narrative when Saving SSCC Placment to Placement page.
	 * 
	 * @param ssccPlcmtNarrDto
	 * @param idCase
	 * @param idEvent
	 */
	@Override
	public void savePlcmtIssueNarr(SSCCPlcmtNarrDto ssccPlcmtNarrDto, Long idCase, Long idEvent) {
		log.info("savePlcmtIssueNarr method of SSCCPlcmtOptCircumDaoImpl : Execution Started.");
		SsccPlcmtNarr ssccNarr = getSSCCPlcmtNarrative(ssccPlcmtNarrDto.getIdSSCPlcmtHeader(),
				ssccPlcmtNarrDto.getNbrVersion());
		PlcmtIssuesNarr plcmtNarr = new PlcmtIssuesNarr();
		plcmtNarr.setIdEvent(idEvent);
		plcmtNarr.setIdCase(idCase);
		plcmtNarr.setIdDocumentTemplate(ssccNarr.getIdDocumentTemplate());
		plcmtNarr.setNarrative(ssccNarr.getNarrative());
		plcmtNarr.setDtLastUpdate(ssccNarr.getDtLastUpdate());
		sessionFactory.getCurrentSession().saveOrUpdate(plcmtNarr);
		log.info("savePlcmtIssueNarr method of SSCCPlcmtOptCircumDaoImpl : Returning Response");
	}
}
