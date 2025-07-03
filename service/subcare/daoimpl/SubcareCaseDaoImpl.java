package us.tx.state.dfps.service.subcare.daoimpl;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.EducationalNeed;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.subcare.dao.SubcareCaseDao;
import us.tx.state.dfps.service.subcare.dto.SubcareChildContactDto;
import us.tx.state.dfps.service.subcare.dto.SubcareLegalEnrollDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Dao class
 * for Subcare Case Management Tool form csc40o00 May 8, 2018- 3:55:56 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class SubcareCaseDaoImpl implements SubcareCaseDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${SubcareDaoImpl.getTcmContact}")
	private String getTcmContactSql;

	@Value("${SubcareDaoImpl.getPlcmntContact}")
	private String getPlcmntContactSql;

	@Value("${SubcareDaoImpl.getAppointment}")
	private String getAppointmentSql;

	@Value("${SubcareDaoImpl.getRemoval}")
	private String getRemovalSql;

	@Value("${SubcareDaoImpl.getHearingDate}")
	private String getHearingDateSql;

	@Value("${SubcareDaoImpl.getLatestPpt}")
	private String getLatestPptSql;

	@Value("${SubcareDaoImpl.getChildFpos}")
	private String getChildFposSql;

	@Value("${SubcareDaoImpl.getChildPlan}")
	private String getChildPlanSql;

	@Value("${SubcareDaoImpl.getGmthContact}")
	private String getGmthContactSql;

	@Value("${SubcareDaoImpl.getLegalStatus}")
	private String getLegalStatusSql;

	@Value("${SubcareDaoImpl.getStatusDeterm}")
	private String getStatusDetermSql;

	@Value("${SubcareDaoImpl.getGreatestEnroll}")
	private String getGreatestEnrollSql;

	@Value("${SubcareDaoImpl.getConcurrentGoals}")
	private String getConcurrentGoals;

	@Value("${SubcareDaoImpl.getCVSMonthlyList}")
	private String getCVSMonthlyList;

	@Value("${SubcareDaoImpl.getToDoListType}")
	private String getToDoListType;

	/**
	 * Method Name: getTcmContact CSECB1D Method Description: This DAM retreives
	 * the most recent TCM contact
	 * 
	 * @param idStage
	 * @return Date
	 */
	@Override
	public Date getTcmContact(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getTcmContactSql)
				.setParameter("idStage", idStage).setParameter("cdEventType", ServiceConstants.CONTACT);
		return (Date) query.uniqueResult();
	}

	/**
	 * Method Name: getPlcmntContact CSECB2D Method Description: This DAM finds
	 * the most recent placement contact.
	 * 
	 * @param idStage
	 * @return Date
	 */
	@Override
	public Date getPlcmntContact(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPlcmntContactSql).setParameter("idStage",
				idStage);
		return (Date) query.uniqueResult();
	}

	/**
	 * Method Name: getAppointment CSECB3D Method Description: This will find
	 * the latest medical/mental appointment based on reason for the assessment
	 * 
	 * @param idPerson
	 * @param cdProfAssmtApptRsn
	 * @return Date
	 */
	@Override
	public Date getAppointment(Long idPerson, String cdProfAssmtApptRsn) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getAppointmentSql)
				.setParameter("idPerson", idPerson).setParameter("cdProfAssmtApptRsn", cdProfAssmtApptRsn);
		return (Date) query.uniqueResult();
	}

	/**
	 * Method Name: getRemoval CSECB4D Method Description:This DAM gets key
	 * information from the most recent conservators** hip removal for the PC in
	 * the subcare stage
	 * 
	 * @param idVictim
	 * @param idCase
	 * @return Date
	 */
	@Override
	public SubcareChildContactDto getRemoval(Long idVictim, Long idCase) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getRemovalSql)
				.addScalar("dtRemoval", StandardBasicTypes.DATE).addScalar("cdRemovalReason", StandardBasicTypes.STRING)
				.setParameter("idVictim", idVictim).setParameter("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(SubcareChildContactDto.class));
		List<SubcareChildContactDto> list = (List<SubcareChildContactDto>) query.list();
		return ObjectUtils.isEmpty(list) ? null : list.get(0);
	}

	/**
	 * Method Name: getHearingDate CSECB5D Method Description: DAM returns the
	 * LAST REVIEW HEARING date
	 * 
	 * @param idStage
	 * @return SubcareChildContactDto
	 */
	@Override
	public Date getHearingDate(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getHearingDateSql)
				.setParameter("idStage", idStage).setParameter("cdEventType", ServiceConstants.LEGAL)
				.setParameter("cdLegalAct", ServiceConstants.CCOR)
				.setParameter("cdLegSubAct1", ServiceConstants.CCOR_120)
				.setParameter("cdLegSubAct2", ServiceConstants.CCOR_300)
				.setParameter("cdLegSubAct3", ServiceConstants.CCOR_310)
				.setParameter("cdLegSubAct4", ServiceConstants.CCOR_320);
		List<Date> dateList = (List<Date>) query.list();
		Date hearingDate = null;
		if(!ObjectUtils.isEmpty(dateList)) {
			hearingDate = dateList.get(0);
		}
		return hearingDate;
	}

	/**
	 * Method Name: getLatestPpt CSECB7D Method Description: This finds the
	 * latest PPT for the stage.
	 * 
	 * @param idStage
	 * @return Date
	 */
	@Override
	public Date getLatestPpt(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getLatestPptSql).setParameter("idStage",
				idStage);
		return (Date) query.uniqueResult();
	}

	/**
	 * Method Name: getChildFpos CSECB8D Method Description:This DAM finds the
	 * date of the most recent FPOS for the Primary Child
	 * 
	 * @param idPerson
	 * @return Date
	 */
	@Override
	public Date getChildFpos(Long idPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getChildFposSql).setParameter("idPerson",
				idPerson);
		List<Date> dtList = (List<Date>) query.list();
		if(!ObjectUtils.isEmpty(dtList)) return dtList.get(0);
		else return null;
	}

	/**
	 * Method Name: getChildPlan CSVC46D Method Description: Gets latest
	 * approved child plan
	 * 
	 * @param idPerson
	 * @return SubcareChildContactDto
	 */
	@Override
	public SubcareChildContactDto getChildPlan(Long idPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getChildPlanSql)
				.addScalar("idChildPlanEvent", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdCspPlanPermGoal", StandardBasicTypes.STRING)
				.addScalar("cdCspPlanType", StandardBasicTypes.STRING)
				.addScalar("dtCspPermGoalTarget", StandardBasicTypes.DATE)
				.addScalar("dtCspNxtReview", StandardBasicTypes.DATE)
				.addScalar("txtCspLength", StandardBasicTypes.STRING)
				.addScalar("txtCspLosDisc", StandardBasicTypes.STRING)
				.addScalar("txtCspPart", StandardBasicTypes.STRING).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idEventStage", StandardBasicTypes.LONG).addScalar("cdEventType", StandardBasicTypes.STRING)
				.addScalar("idEventPerson", StandardBasicTypes.LONG).addScalar("cdTask", StandardBasicTypes.STRING)
				.addScalar("txtEventDesc", StandardBasicTypes.STRING)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.addScalar("dtApprovDeterm", StandardBasicTypes.DATE).setParameter("idPerson", idPerson)
				.setParameter("eventStatus", ServiceConstants.EVENTSTATUS_APPROVE)
				.setResultTransformer(Transformers.aliasToBean(SubcareChildContactDto.class));
		List<SubcareChildContactDto> list = (List<SubcareChildContactDto>) query.list();
		return ObjectUtils.isEmpty(list) ? null : list.get(0);
	}

	/**
	 * Method Name: getGmthContact CSVC47D Method Description: Queries for most
	 * recent GMTH contact for a given stage, most recent based on date of
	 * contact (dt_contact_occurred)
	 * 
	 * @param idStage
	 * @return SubcareChildContactDto
	 */
	@Override
	public SubcareChildContactDto getGmthContact(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getGmthContactSql)
				.addScalar("dtContactOccured", StandardBasicTypes.DATE)
				.addScalar("cdEventType", StandardBasicTypes.STRING).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idEventStage", StandardBasicTypes.LONG).addScalar("idEventPerson", StandardBasicTypes.LONG)
				.addScalar("txtEventDescr", StandardBasicTypes.STRING).addScalar("cdTask", StandardBasicTypes.STRING)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(SubcareChildContactDto.class));
		List<SubcareChildContactDto> list = (List<SubcareChildContactDto>) query.list();
		return ObjectUtils.isEmpty(list) ? null : list.get(0);
	}

	/**
	 * Method Name: getLegalStatus CSES78D Method Description:This dam retrieves
	 * a full row from LEGAL_STATUS.
	 * 
	 * @param idCase
	 * @param idPerson
	 * @return SubcareLegalEnrollDto
	 */
	@Override
	public SubcareLegalEnrollDto getLegalStatus(Long idCase, Long idPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getLegalStatusSql)
				.addScalar("idLegalStatEvent", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("cdLegalStatCnty", StandardBasicTypes.STRING)
				.addScalar("cdLegalStatStatus", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatStatus", StandardBasicTypes.DATE)
				.addScalar("txtLegalStatCauseNbr", StandardBasicTypes.STRING)
				.addScalar("txtLegalStatCourtNbr", StandardBasicTypes.STRING).setParameter("idCase", idCase)
				.setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(SubcareLegalEnrollDto.class));
		List<SubcareLegalEnrollDto> list = (List<SubcareLegalEnrollDto>) query.list();
		return ObjectUtils.isEmpty(list) ? null : list.get(0);
	}

	/**
	 * Method Name: getStatusDeterm CLSS64D Method Description:This DAM will
	 * determine whether an Id_Person passed in has the Adoption Consumated Code
	 * as its most recent Legal Status.
	 * 
	 * @param idPerson
	 * @param cdLegalStatStatus
	 * @return SubcareLegalEnrollDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SubcareLegalEnrollDto getStatusDeterm(Long idPerson, String cdLegalStatStatus) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getStatusDetermSql)
				.addScalar("idLegalStatEvent", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("txtLegalStatCauseNbr", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatTmcDissmiss", StandardBasicTypes.DATE).setParameter("idPerson", idPerson)
				.setParameter("cdLegalStatStatus", cdLegalStatStatus)
				.setResultTransformer(Transformers.aliasToBean(SubcareLegalEnrollDto.class));
		List<SubcareLegalEnrollDto> list = (List<SubcareLegalEnrollDto>) query.list();
		return ObjectUtils.isEmpty(list) ? null : list.get(0);
	}

	/**
	 * Method Name: getGreatestEnroll CSES33D Method Description: This Dam will
	 * do a full row retrieval of EDUCATION_HIST with the greatest enroll date
	 * 
	 * @param idPerson
	 * @return SubcareLegalEnrollDto
	 */
	@Override
	public SubcareLegalEnrollDto getGreatestEnroll(Long idPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getGreatestEnrollSql)
				.addScalar("idHist", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("addrHistCity", StandardBasicTypes.STRING)
				.addScalar("addrHistCnty", StandardBasicTypes.STRING)
				.addScalar("addrHistState", StandardBasicTypes.STRING)
				.addScalar("addrHistStrLn1", StandardBasicTypes.STRING)
				.addScalar("addrHistStrLn2", StandardBasicTypes.STRING)
				.addScalar("addrHistZip", StandardBasicTypes.STRING)
				.addScalar("cdHistEnrollGrade", StandardBasicTypes.STRING)
				.addScalar("cdEdNeed", StandardBasicTypes.STRING)
				.addScalar("cdHistWithGrade", StandardBasicTypes.STRING)
				.addScalar("dtHistEnrollDate", StandardBasicTypes.DATE)
				.addScalar("dtHistWithDate", StandardBasicTypes.DATE)
				.addScalar("indHistTeaSchool", StandardBasicTypes.STRING)
				.addScalar("nbrHistPhone", StandardBasicTypes.STRING)
				.addScalar("nbrHistPhoneExt", StandardBasicTypes.STRING)
				.addScalar("nmHistSchool", StandardBasicTypes.STRING)
				.addScalar("nmHistSchDist", StandardBasicTypes.STRING)
				.addScalar("txtHistAddrCmnt", StandardBasicTypes.STRING)
				.addScalar("dtLastArdiep", StandardBasicTypes.DATE)
				.addScalar("txtSpecialAccmdtns", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(SubcareLegalEnrollDto.class));
		List<SubcareLegalEnrollDto> list = (List<SubcareLegalEnrollDto>) query.list();
		return ObjectUtils.isEmpty(list) ? null : list.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getSchoolProgramList(Long idPerson) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(EducationalNeed.class)
				.add(Restrictions.eq("person.idPerson", idPerson)).setProjection(Projections.projectionList()
						.add(Projections.property("cdEducationalNeed"), "cdEducationalNeed"));
		List<String> results = cr.list();

		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getConcurrentGoals(Long idCase) {
		Query query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getConcurrentGoals)
				.setParameter("idCase", idCase);
		List<String> results = query.list();
		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Date> getCVSMonthlyList(Long idStage, Long idPerson) {
		Query query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCVSMonthlyList)
				.setParameter("idStage", idStage).setParameter("idPerson", idPerson);
		List<Date> results = query.list();
		return results;
	}

	@Override
	public Date getNextDate(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getHearingDateSql)
				.setParameter("idStage", idStage).setParameter("cdEventType", ServiceConstants.LEGAL)
				.setParameter("cdLegalAct", ServiceConstants.CCOR)
				.setParameter("cdLegSubAct1", ServiceConstants.CCOR_120)
				.setParameter("cdLegSubAct2", ServiceConstants.CCOR_300)
				.setParameter("cdLegSubAct3", ServiceConstants.CCOR_310)
				.setParameter("cdLegSubAct4", ServiceConstants.CCOR_320);
		List<Date> dateList = (List<Date>) query.list();
		Date nextDate = null;
		if(!ObjectUtils.isEmpty(dateList)) {
			nextDate = dateList.get(0);
		}
		return nextDate;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CaseInfoDto> getToDoListType(Long idCase) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getToDoListType)
				.addScalar("cdTodoType", StandardBasicTypes.STRING).addScalar("txtTodoDesc", StandardBasicTypes.STRING)
				.setParameter("idCase", idCase).setResultTransformer(Transformers.aliasToBean(CaseInfoDto.class));
		List<CaseInfoDto> list = query.list();
		return list;
	}

	@Override
	public String getIndication(Long idPerson) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(Person.class)
				.add(Restrictions.eq("idPerson", idPerson)).setProjection(Projections.projectionList()
						.add(Projections.property("indEducationPortfolio"), "indEducationPortfolio"));
		String results = (String) cr.uniqueResult();
		return results;
	}
}
