/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Mar 28, 2018- 3:34:12 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.prt.daoimpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.PrtActionPlan;
import us.tx.state.dfps.common.domain.PrtParticipant;
import us.tx.state.dfps.common.domain.PrtPermStatusLookup;
import us.tx.state.dfps.common.domain.PrtPersonLink;
import us.tx.state.dfps.common.exception.TimeMismatchException;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.ServiceConstants.ActionPlanType;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.prt.dao.PRTActionPlanDao;
import us.tx.state.dfps.service.prt.dto.PRTConnectionDto;
import us.tx.state.dfps.service.prt.dto.PRTEventLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTParticipantDto;
import us.tx.state.dfps.service.prt.dto.PRTPermStatusLookupDto;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.subcare.dto.PRTActionPlanDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 28, 2018- 3:34:12 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class PRTActionPlanDaoImpl implements PRTActionPlanDao {

	private static final Logger log = Logger.getLogger("ServiceBusiness-PRTActionPlanDaoImpl");

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PRTActionPlanDaoImpl.deletePrtPermGoalSql}")
	private String deletePrtPermGoalSql;

	@Value("${PRTActionPlanDaoImpl.deletePrtConnForPersonSql}")
	private String deletePrtConnForPersonSql;

	@Value("${PRTActionPlanDaoImpl.deleteParticipantForActPlnSql}")
	private String deleteParticipantForActPlnSql;

	@Value("${PRTActionPlanDaoImpl.deletePersonLinkForActPlnSql}")
	private String deletePersonLinkForActPlnSql;

	@Value("${PRTActionPlanDaoImpl.deletePrtEventLinkForActPlnSql}")
	private String deletePrtEventLinkForActPlnSql;

	@Value("${PRTActionPlanDaoImpl.deletePrtActionPlanSql}")
	private String deletePrtActionPlanSql;

	@Value("${PRTActionPlanDaoImpl.deletePrtStrategySql}")
	private String deletePrtStrategySql;

	@Value("${PRTActionPlanDaoImpl.delPrtTasksByStrategy}")
	private String delPrtTasksByStrategy;

	@Value("${PRTActionPlanDaoImpl.delPrtTaskPersonLinkByStrategy}")
	private String delPrtTaskPersonLinkByStrategy;

	@Value("${PRTActionPlanDaoImpl.delEventPersonLink}")
	private String delEventPersonLink;

	@Value("${PRTActionPlanDaoImpl.delEventSql}")
	private String delEventSql;

	@Value("${PRTActionPlanDaoImpl.selectActionPlanUsingEventIdSql}")
	private String selectActionPlanUsingEventIdSql;

	@Value("${PRTActionPlanDaoImpl.deletePrtGoalsForChild}")
	private String deletePrtGoalsForChild;

	@Value("${PRTActionPlanDaoImpl.deletePrtConnSql}")
	private String deletePrtConnSql;

	@Value("${PRTActionPlanDaoImpl.selectPrtEventLinkWithIdStageA}")
	private String selectPrtEventLinkWithIdStageA;

	@Value("${PRTActionPlanDaoImpl.selectPrtEventLinkWithIdStageB}")
	private String selectPrtEventLinkWithIdStageB;

	@Value("${PRTActionPlanDaoImpl.deletePrtParticipantSql}")
	private String deletePrtParticipantSql;

	@Value("${BasePRTDaoImpl.updatePRTContactToPersLink}")
	private String updatePRTContactToPersLink;

	@Value("${PRTActionPlanDaoImpl.sqlInsertPrtParticipant}")
	private String insertPrtParticipantSql;

	@Value("${PRTActionPlanDaoImpl.sqlFetchPrtParticipant}")
	private String fetchPrtParticipantSql;

	@Value("${BasePRTDaoIpml.selectPrtChildrenSql}")
	private String selectPrtChildrenSql;

	@Value("${BasePRTDaoIpml.selectPrtPermGoalsSql}")
	private String selectPrtPermGoalsSql;

	@Value("${BasePRTDaoIpml.selectPrtTaskSql}")
	private String selectPrtTaskSql;

	@Value("${BasePRTDaoIpml.selectPrtProcTaskSql}")
	private String selectPrtProcTaskSql;

	@Value("${BasePRTDaoIpml.selectChildrenForTaskSql}")
	private String selectChildrenForTaskSql;

	@Value("${BasePRTDaoIpml.selectPRTConnectionsSql}")
	private String selectPRTConnectionsSql;

	@Value("${BasePRTDaoIpml.selectPRTParticipantsSql}")
	private String selectPRTParticipantsSql;

	@Value("${BasePRTDaoIpml.populateEventIdForChildSql}")
	private String populateEventIdForChildSql;

	@Value("${PRTActionDaoIpml.selectActionPlanSql}")
	private String selectActionPlanSql;

	@Value("${PRTActionPlanDaoImpl.selectQuery}")
	private String selectQuerySql;

	@Value("${PRTActionPlanDaoImpl.getTimeStampByCase}")
	private String getTimeStampByCase;

	@Value("${PRTActionPlanDaoImpl.getTimeStampByStage}")
	private String getTimeStampByStage;

	/**
	 * Method Name: selectActionPlanUsingEventId Method Description: This method
	 * fetches single row from PRT_ACTION_PLAN table using Event Id.
	 * 
	 * @param idActionPlanEvent
	 * @return PRTActionPlanDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PRTActionPlanDto selectActionPlanUsingEventId(Long idActionPlanEvent) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectActionPlanUsingEventIdSql)
				.addScalar("idPrtActionPlan", StandardBasicTypes.LONG).addScalar("dtComplete", StandardBasicTypes.DATE)
				.addScalar("brsAchPerm", StandardBasicTypes.STRING)
				.addScalar("brsTriedBefore", StandardBasicTypes.STRING)
				.addScalar("brsNotTriedBefore", StandardBasicTypes.STRING)
				.addScalar("brsTryConcr", StandardBasicTypes.STRING)
				.addScalar("brsYthPlnPerm", StandardBasicTypes.STRING)
				.addScalar("dbrfRecomend", StandardBasicTypes.STRING)
				.addScalar("dbrfExplToFam", StandardBasicTypes.STRING)
				.addScalar("dbrfUnanswrQstn", StandardBasicTypes.STRING)
				.addScalar("dbrfOthrCase", StandardBasicTypes.STRING)
				.addScalar("adtnlConnections", StandardBasicTypes.STRING)
				.addScalar("prtcpntComments", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.DATE).addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("idUnitWorker", StandardBasicTypes.LONG).setParameter("idActionPlanEvent", idActionPlanEvent)
				.setResultTransformer(Transformers.aliasToBean(PRTActionPlanDto.class));
		List<PRTActionPlanDto> actionPlanDtos = query.list();
		return actionPlanDtos.get(ServiceConstants.Zero);
	}

	/**
	 * This method deletes PRT Participant for Action Plan.
	 *
	 * @param actionPlanId
	 *            the action plan id
	 */
	public void deleteParticipantsForActPlan(Long actionPlanId) {

		int rowCount = ServiceConstants.Zero;
		SQLQuery deleteQuery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(deleteParticipantForActPlnSql);
		deleteQuery.setParameter("actionPlanId", actionPlanId);
		rowCount = deleteQuery.executeUpdate();
		log.info(deleteParticipantForActPlnSql + " - " + rowCount);
	}

	/**
	 * This method deletes all PRT Goals for the Child.
	 *
	 * @param prtPersonLinkId
	 *            the prt person link id
	 */
	public void deletePrtGoalsForChild(Long prtPersonLinkId) {

		int rowCount = ServiceConstants.Zero;
		SQLQuery deleteQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deletePrtPermGoalSql);
		deleteQuery.setParameter("prtPersonLinkId", prtPersonLinkId);
		rowCount = deleteQuery.executeUpdate();
		log.info(deletePrtPermGoalSql + " - " + rowCount);
	}

	/**
	 * This method deletes all PRT Connections for the Child.
	 *
	 * @param prtPersonLinkId
	 *            the prt person link id
	 */
	public void deletePrtConnections(Long prtPersonLinkId) {
		int rowCount = ServiceConstants.Zero;
		SQLQuery deleteQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deletePrtConnForPersonSql);
		deleteQuery.setParameter("prtPersonLinkId", prtPersonLinkId);
		rowCount = deleteQuery.executeUpdate();
		log.info(deletePrtConnForPersonSql + " - " + rowCount);
	}

	/**
	 * This method deletes PRT Strategy.
	 *
	 * @param prtStrategyId
	 *            the prt strategy id
	 */
	public void deletePrtStrategy(Long prtStrategyId) {

		int rowCount = ServiceConstants.Zero;
		SQLQuery deleteQuery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(delPrtTaskPersonLinkByStrategy);
		deleteQuery.setParameter("prtStrategyId", prtStrategyId);
		rowCount = deleteQuery.executeUpdate();
		log.info(delPrtTaskPersonLinkByStrategy + " - " + rowCount);

		rowCount = ServiceConstants.Zero;
		deleteQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(delPrtTasksByStrategy);
		deleteQuery.setParameter("prtStrategyId", prtStrategyId);
		rowCount = deleteQuery.executeUpdate();
		log.info(delPrtTasksByStrategy + " - " + rowCount);

		rowCount = ServiceConstants.Zero;
		deleteQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deletePrtStrategySql);
		deleteQuery.setParameter("prtStrategyId", prtStrategyId);
		rowCount = deleteQuery.executeUpdate();
		log.info(deletePrtStrategySql + " - " + rowCount);

	}

	/**
	 * This method deletes PRT PersonLink for Action Plan.
	 *
	 * @param actionPlanId
	 *            the action plan id
	 */
	public void deletePersonLinkForActPlan(long actionPlanId) {

		int rowCount = ServiceConstants.Zero;
		SQLQuery deleteQuery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(deletePersonLinkForActPlnSql);
		deleteQuery.setParameter("actionPlanId", actionPlanId);
		rowCount = deleteQuery.executeUpdate();
		log.info(deletePersonLinkForActPlnSql + " - " + rowCount);
	}

	/**
	 * This method deletes PRT Event Link.
	 *
	 * @param actionPlanId
	 *            the action plan id
	 */
	public void deletePrtEventLink(Long actionPlanId) {

		int rowCount = ServiceConstants.Zero;
		SQLQuery deleteQuery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(deletePrtEventLinkForActPlnSql);
		deleteQuery.setParameter("actionPlanId", actionPlanId);
		rowCount = deleteQuery.executeUpdate();
		log.info(deletePrtEventLinkForActPlnSql + " - " + rowCount);

	}

	/**
	 * This method deletes PRT Event.
	 *
	 * @param eventId
	 *            the event id
	 */
	public void deletePrtEvent(Long eventId) {

		int rowCount = ServiceConstants.Zero;
		SQLQuery deleteQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(delEventPersonLink);
		deleteQuery.setParameter("eventId", eventId);
		rowCount = deleteQuery.executeUpdate();
		log.info(delEventPersonLink + " - " + rowCount);

		rowCount = ServiceConstants.Zero;
		deleteQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(delEventSql);
		deleteQuery.setParameter("eventId", eventId);
		rowCount = deleteQuery.executeUpdate();
		log.info(delEventSql + " - " + rowCount);
	}

	/**
	 * This method (deleteActionPlan) deletes action Plan.
	 * 
	 * @param eventId
	 *            the event id
	 */
	public Long deleteActionPlan(Long actionPlanId) {

		Long rowCount = ServiceConstants.Zero_Value;
		SQLQuery deleteQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deletePrtActionPlanSql);
		deleteQuery.setParameter("actionPlanId", actionPlanId);
		rowCount = Long.valueOf(deleteQuery.executeUpdate());
		log.info(deletePrtActionPlanSql + " - " + rowCount);

		return rowCount;

	}

	/**
	 * This method inserts record into PRT_ACTION_PLAN table.
	 *
	 * @param actionPlan
	 *            the action plan
	 * @returns idPrtActionPlan - newly created Id.
	 */
	public long insertActionPlan(PRTActionPlanDto actionPlan) {
		PrtActionPlan prtActionPlan = new PrtActionPlan();
		prtActionPlan.setDtLastUpdate(new Date());
		prtActionPlan.setIdLastUpdatePerson(actionPlan.getIdLastUpdatePerson()); // ID_LAST_UPDATE_PERSON
		prtActionPlan.setDtCreated(new Date());// DT_CREATED
		prtActionPlan.setIdCreatedPerson(actionPlan.getIdCreatedPerson()); // ID_CREATED_PERSON
		prtActionPlan.setDtComplete(actionPlan.getDtComplete()); // DT_COMPLETE
		prtActionPlan.setIdUnitWorker(actionPlan.getIdUnitWorker()); // ID_UNIT_WORKER
		prtActionPlan.setTxtBrsAchPerm(actionPlan.getBrsAchPerm()); // TXT_BRS_ACH_PERM
		prtActionPlan.setTxtBrsTriedBefore(actionPlan.getBrsNotTriedBefore()); // TXT_BRS_TRIED_BEFORE
		prtActionPlan.setTxtBrsNotTriedBefore(actionPlan.getBrsNotTriedBefore()); // TXT_BRS_NOT_TRIED_BEFORE
		prtActionPlan.setTxtBrsTryConcr(actionPlan.getBrsTryConcr()); // TXT_BRS_TRY_CONCR
		prtActionPlan.setTxtBrsYthPlnPerm(actionPlan.getBrsYthPlnPerm()); // TXT_BRS_YTH_PLN_PERM
		prtActionPlan.setTxtDbrfRecomend(actionPlan.getDbrfRecomend()); // TXT_DBRF_RECOMEND
		prtActionPlan.setTxtDbrfExplToFam(actionPlan.getDbrfExplToFam()); // TXT_DBRF_EXPL_TO_FAM
		prtActionPlan.setTxtDbrfUnanswrQstn(actionPlan.getDbrfUnanswrQstn()); // TXT_DBRF_UNANSWR_QSTN
		prtActionPlan.setTxtDbrfOthrCase(actionPlan.getDbrfOthrCase()); // TXT_DBRF_OTHR_CASE
		prtActionPlan.setTxtAdtnlConnections(actionPlan.getAdtnlConnections()); // TXT_ADTNL_CONNECTIONS
		prtActionPlan.setTxtPrtcpntComments(actionPlan.getPrtcpntComments()); // TXT_PRTCPNT_COMMENTS
		long idPrtActionPlan = (long) sessionFactory.getCurrentSession().save(prtActionPlan);
		return idPrtActionPlan;
	}

	/**
	 * This method updates PRT_PARTICIPANT table using PRTPersonLinkValueBean.
	 *
	 * @param participant
	 *            the participant
	 * @return the prt participant
	 */
	public PrtParticipant updatePRTParticipants(PRTParticipantDto participant) {
		PrtParticipant prtParticipant = null;

		Long participantId = participant.getIdPrtParticipant();
		if (participantId != null) {
			prtParticipant = (PrtParticipant) sessionFactory.getCurrentSession().get(PrtParticipant.class,
					participantId);
		}

		prtParticipant.setIdLastUpdatePerson(participant.getIdLastUpdatePerson()); // ID_LAST_UPDATE_PERSON
		prtParticipant.setCdPrtRole(participant.getCdPrtRole()); // CD_PRT_ROLE

		sessionFactory.getCurrentSession().update(prtParticipant);
		return prtParticipant;

	}

	/**
	 * This method updates PRT_ACTION_PLAN table using PRTPersonLinkValueBean.
	 *
	 * @param actionPlan
	 *            the action plan
	 * @return the prt action plan
	 */
	public PrtActionPlan updateActionPlan(PRTActionPlanDto actionPlan) {

		PrtActionPlan prtActionPlan = new PrtActionPlan();
		Long prtActionPlanId = actionPlan.getIdPrtActionPlan();
		if (prtActionPlanId != null) {
			prtActionPlan = (PrtActionPlan) sessionFactory.getCurrentSession().get(PrtActionPlan.class,
					prtActionPlanId);
		}
		if (prtActionPlan.getDtLastUpdate().compareTo(actionPlan.getDtLastUpdate()) != 0) {
			throw new TimeMismatchException();
		}
		prtActionPlan.setIdLastUpdatePerson(actionPlan.getIdLastUpdatePerson()); // ID_LAST_UPDATE_PERSON
		prtActionPlan.setDtComplete(actionPlan.getDtComplete()); // DT_COMPLETE
		prtActionPlan.setIdUnitWorker(actionPlan.getIdUnitWorker()); // ID_UNIT_WORKER
		prtActionPlan.setTxtBrsAchPerm(actionPlan.getBrsAchPerm()); // TXT_BRS_ACH_PERM
		prtActionPlan.setTxtBrsTriedBefore(actionPlan.getBrsNotTriedBefore()); // TXT_BRS_TRIED_BEFORE
		prtActionPlan.setTxtBrsNotTriedBefore(actionPlan.getBrsNotTriedBefore()); // TXT_BRS_NOT_TRIED_BEFORE
		prtActionPlan.setTxtBrsTryConcr(actionPlan.getBrsTryConcr()); // TXT_BRS_TRY_CONCR
		prtActionPlan.setTxtBrsYthPlnPerm(actionPlan.getBrsYthPlnPerm()); // TXT_BRS_YTH_PLN_PERM
		prtActionPlan.setTxtDbrfRecomend(actionPlan.getDbrfRecomend()); // TXT_DBRF_RECOMEND
		prtActionPlan.setTxtDbrfExplToFam(actionPlan.getDbrfExplToFam()); // TXT_DBRF_EXPL_TO_FAM
		prtActionPlan.setTxtDbrfUnanswrQstn(actionPlan.getDbrfUnanswrQstn()); // TXT_DBRF_UNANSWR_QSTN
		prtActionPlan.setTxtDbrfOthrCase(actionPlan.getDbrfOthrCase()); // TXT_DBRF_OTHR_CASE
		prtActionPlan.setTxtAdtnlConnections(actionPlan.getAdtnlConnections()); // TXT_ADTNL_CONNECTIONS
		prtActionPlan.setTxtPrtcpntComments(actionPlan.getPrtcpntComments()); // TXT_PRTCPNT_COMMENTS

		prtActionPlan.setDtLastUpdate(actionPlan.getDtLastUpdate());
		prtActionPlan.setDtCreated(actionPlan.getDtCreated());

		prtActionPlan.setIdCreatedPerson(actionPlan.getIdCreatedPerson());

		sessionFactory.getCurrentSession().update(prtActionPlan);
		return prtActionPlan;
	}

	/**
	 * Gets the event by id.
	 *
	 * @param eventId
	 *            the event id
	 * @return the event by id
	 */
	public Event getEventById(Long eventId) {
		Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, eventId);
		return event;
	}

	/**
	 * Method Name: selectPrtPermStatusLookup Method Description: This method
	 * fetches a list of all entries in PRT_PERM_STATUS_LOOKUP table
	 * 
	 * @param
	 * @return PRTActionPlanRes
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PRTPermStatusLookupDto> selectPrtPermStatusLookup() {
		List<PRTPermStatusLookupDto> permStatusList = new ArrayList<PRTPermStatusLookupDto>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PrtPermStatusLookup.class);
		List<PrtPermStatusLookup> lookupList = criteria.list();

		if (lookupList != null) {
			for (PrtPermStatusLookup prtPermStatusLookup : lookupList) {
				PRTPermStatusLookupDto pRTPermStatusLookupValueDto = new PRTPermStatusLookupDto();
				pRTPermStatusLookupValueDto.setIdPrtPermStatusLookup(prtPermStatusLookup.getIdPrtPermStatusLookup());
				try {
					pRTPermStatusLookupValueDto.setDesc(prtPermStatusLookup.getTxtDesc().getSubString(1,
							(int) prtPermStatusLookup.getTxtDesc().length()));
				} catch (SQLException e) {

				}
				permStatusList.add(pRTPermStatusLookupValueDto);
			}
		}
		return permStatusList;
	}

	/**
	 * Method Name: selectPrtEventLinkWithIdStage Method Description: This
	 * method fetches a list of all PrtEventLinkWithIdStage
	 * 
	 * @param Long
	 *            idPrtActPlnOrFollowup, ActionPlanType planType
	 * @return List <PRTEventLinkDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PRTEventLinkDto> selectPrtEventLinkWithIdStage(Long idPrtActPlnOrFollowup, ActionPlanType planType) {

		SQLQuery query;

		if (planType == ActionPlanType.ACTION_PLAN) {
			query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectPrtEventLinkWithIdStageA)
					.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
					.addScalar("idPrtActionPlan", StandardBasicTypes.LONG)
					.addScalar("idPrtActplnFollowup", StandardBasicTypes.LONG)
					.addScalar("dtCreated", StandardBasicTypes.DATE)
					.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
					.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
					.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
					.setParameter("idPLNFL", idPrtActPlnOrFollowup)
					.setResultTransformer(Transformers.aliasToBean(PRTEventLinkDto.class));
		} else {
			query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectPrtEventLinkWithIdStageB)
					.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
					.addScalar("idPrtActionPlan", StandardBasicTypes.LONG)
					.addScalar("idPrtActplnFollowup", StandardBasicTypes.LONG)
					.addScalar("dtCreated", StandardBasicTypes.DATE)
					.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
					.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
					.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
					.setParameter("idPLNFL", idPrtActPlnOrFollowup)
					.setResultTransformer(Transformers.aliasToBean(PRTEventLinkDto.class));
		}

		List<PRTEventLinkDto> listResults = query.list();

		return listResults;
	}

	/**
	 * This method deletes all PRT Connections for the Child.
	 *
	 * @param prtPersonLinkId
	 *            the prt person link id
	 */
	public Long deletePrtParticipant(Long idPrtParticipant) {
		Long rowCount = ServiceConstants.ZERO_VAL;
		SQLQuery deleteQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deletePrtParticipantSql);
		deleteQuery.setParameter("idPrtParticipant", idPrtParticipant);
		rowCount = Long.valueOf(deleteQuery.executeUpdate());
		log.info(deletePrtConnForPersonSql + " - " + rowCount);
		return rowCount;
	}

	/**
	 * Method Name: updatePRTContactToPersLink Method Description: this method
	 * will update the PRT action plan
	 * 
	 * @param PRTParticipantDto
	 * @return idPrtParticipant
	 */
	@Override
	public Long updatePRTContactToPersLink(PRTPersonLinkDto pRTPersonLinkValueDto) {

		Event event = null;
		PrtActionPlan prtActionPlan = null;
		Person person = null;

		Long idPerson = pRTPersonLinkValueDto.getIdPerson();
		if (idPerson != null) {
			person = (Person) sessionFactory.getCurrentSession().get(Person.class, idPerson);
		}

		Long prtActionPlanId = pRTPersonLinkValueDto.getIdPrtActionPlan();
		if (prtActionPlanId != null) {
			prtActionPlan = (PrtActionPlan) sessionFactory.getCurrentSession().get(PrtActionPlan.class,
					prtActionPlanId);
		}
		Long idEvent = pRTPersonLinkValueDto.getIdContactEvent();
		if (!ObjectUtils.isEmpty(idEvent)) {
			event = (Event) sessionFactory.getCurrentSession().get(Event.class, idEvent);
		}
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PrtPersonLink.class);
		criteria.add(Restrictions.eq("prtActionPlan", prtActionPlan));
		criteria.add(Restrictions.eq("person", person));

		PrtPersonLink prtPersonLink = (PrtPersonLink) criteria.uniqueResult();

		prtPersonLink.setEvent(event);

		sessionFactory.getCurrentSession().update(prtPersonLink);
		return prtPersonLink.getIdPrtPersonLink();
	}

	/**
	 * Method Name: insertPRTParticipant Method Description: this method inserts
	 * record into PRT_PARTICIPANT table
	 * 
	 * @param PRTParticipantDto
	 * @return idPrtParticipant
	 */
	@Override
	public Long insertPRTParticipant(PRTParticipantDto participantDto) {
		PrtActionPlan prtActionPlan = null;
		Person person = null;
		PrtParticipant prtParticipant = new PrtParticipant();
		prtParticipant.setIdCreatedPerson(participantDto.getIdCreatedPerson());
		prtParticipant.setIdLastUpdatePerson(participantDto.getIdLastUpdatePerson());
		prtParticipant.setCdPrtRole(participantDto.getCdPrtRole());
		prtParticipant.setDtCreated(new Date());
		prtParticipant.setDtLastUpdate(new Date());
		Long idPrtActionPlan = participantDto.getIdPrtActionPlan();
		if (idPrtActionPlan != null) {
			prtActionPlan = (PrtActionPlan) sessionFactory.getCurrentSession().get(PrtActionPlan.class,
					idPrtActionPlan);
		}
		Long idPerson = participantDto.getIdPerson();
		if (idPerson != null) {
			person = (Person) sessionFactory.getCurrentSession().get(Person.class, idPerson);
		}

		prtParticipant.setPrtActionPlan(prtActionPlan);
		prtParticipant.setPerson(person);

		Long count = (Long) sessionFactory.getCurrentSession().save(prtParticipant);
		return count;
	}

	/**
	 * Method Name: selectPRTParticipants Method Description:
	 * 
	 * @param prtActionPlanId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<PRTParticipantDto> fetchPRTParticipants(Long prtActionPlanId) {

		ArrayList<PRTParticipantDto> PrtPartList;

		Query fetchParticipant = sessionFactory.getCurrentSession().createSQLQuery(fetchPrtParticipantSql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
				.addScalar("idPrtParticipant", StandardBasicTypes.LONG).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("idPrtActionPlan", StandardBasicTypes.LONG).addScalar("cdPrtRole", StandardBasicTypes.STRING)
				.addScalar("strEmail", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(PRTParticipantDto.class));

		fetchParticipant.setParameter("pP_IdPrtActionPlan", prtActionPlanId);

		PrtPartList = (ArrayList<PRTParticipantDto>) fetchParticipant.list();

		if (PrtPartList != null)
			return PrtPartList;
		else
			return null;
	}

	/**
	 * This method fetches PRT_PERSON_LINK rows for the given Action Plan.
	 * 
	 * @param idPrtActionPlan
	 * 
	 * @return List<PRTPersonLinkDto> or null if record not found.
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PRTPersonLinkDto> selectPrtChildren(Long idPrtActionPlan) {

		List<PRTPersonLinkDto> prtPersonList = new ArrayList<PRTPersonLinkDto>();
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectPrtChildrenSql)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("dtBirth", StandardBasicTypes.DATE)
				.addScalar("idChildSUBStage", StandardBasicTypes.LONG)
				.addScalar("idPrtPersonLink", StandardBasicTypes.LONG).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("idPrtActionPlan", StandardBasicTypes.LONG)
				.addScalar("idPrtActplnFollowup", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("idContactEvent", StandardBasicTypes.LONG)
				.addScalar("idPrtPermStatusLookup", StandardBasicTypes.LONG)
				.addScalar("cdLastPermDsc", StandardBasicTypes.STRING).addScalar("dtPrtExit", StandardBasicTypes.DATE)
				.addScalar("cdExitReason", StandardBasicTypes.STRING)
				.addScalar("cdRcmndPrimaryGoal", StandardBasicTypes.STRING)
				.addScalar("cdRcmndConcurrentGoal", StandardBasicTypes.STRING)
				.addScalar("indNoConGoal", StandardBasicTypes.STRING)
				.addScalar("idPrevPermStatus", StandardBasicTypes.LONG).setParameter("idPrtActionPlan", idPrtActionPlan)
				.setResultTransformer(Transformers.aliasToBean(PRTPersonLinkDto.class));
		List<PRTPersonLinkDto> prtPersonLinkDtos = query.list();
		if (!CollectionUtils.isEmpty(prtPersonLinkDtos)) {
			prtPersonList = prtPersonLinkDtos;
		}

		return prtPersonList;
	}

	/**
	 * This method populates Event Id associated with the Child.
	 * 
	 * @param List
	 *            <PRTPersonLinkDto> children
	 * @param idActPlanOrFollowUp
	 * @param ActionPlanType
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long populateEventIdForChild(List<PRTPersonLinkDto> prtPersonList, Long idPrtActionPlan,
			ActionPlanType actionPlan) {

		String keyColumn = (actionPlan == ActionPlanType.ACTION_PLAN)
				? " EVTLINK.ID_PRT_ACTION_PLAN = :idPrtActionPlan "
				: " EVTLINK.ID_PRT_ACTPLN_FOLLOWUP = :idPrtActionPlan ";
		String selectQry = populateEventIdForChildSql + keyColumn;
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectQry)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idChildEventId", StandardBasicTypes.LONG).setParameter("idPrtActionPlan", idPrtActionPlan)
				.setResultTransformer(Transformers.aliasToBean(PRTPersonLinkDto.class));

		List<PRTPersonLinkDto> prtPersonLinkValueDtos = query.list();
		if (!TypeConvUtil.isNullOrEmpty(prtPersonLinkValueDtos)) {

			for (PRTPersonLinkDto child : prtPersonLinkValueDtos) {
				long idPerson = child.getIdPerson();
				long idEvent = child.getIdChildEventId();
				if (!CollectionUtils.isEmpty(prtPersonList)) {
					PRTPersonLinkDto prtPersonLinkDto = prtPersonList.stream()
							.filter(prtPerson -> prtPerson.getIdPerson().equals(idPerson)).findFirst().get();
					if (!ObjectUtils.isEmpty(prtPersonLinkDto)) {
						prtPersonLinkDto.setIdChildEventId(idEvent);
					}
				}

			}
		}

		return (long) prtPersonLinkValueDtos.size();
	}

	/**
	 * This method returns select Query for the table for Action Plan or
	 * Followup
	 * 
	 * @param tableName
	 * @param ActionPlanType
	 * 
	 * @return select Query
	 * 
	 */
	protected String getSelectQry(String tableName, ActionPlanType planType) {
		String keyColumn = (planType == ActionPlanType.ACTION_PLAN) ? " ID_PRT_ACTION_PLAN "
				: " ID_PRT_ACTPLN_FOLLOWUP ";
		String selectQuery = selectQuerySql.replaceFirst("%table", tableName).replaceFirst("%col", keyColumn);
		return selectQuery;
	}

	/**
	 * This method fetches single row from PRT_CONNECTION table.
	 * 
	 * @param idPrtPersonLink
	 * @param idStage
	 * 
	 * @return List <PRTConnectionValueBean> or null if record not found.
	 *
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PRTConnectionDto> selectPRTConnections(Long idPrtPersonLink, Long idChildSUBStage) {
		List<PRTConnectionDto> prtConnections = new ArrayList<PRTConnectionDto>();
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectPRTConnectionsSql)
				.addScalar("idPrtConnection", StandardBasicTypes.LONG)
				.addScalar("idPrtPersonLink", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.DATE).addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("relIntDecode", StandardBasicTypes.STRING).setParameter("idPrtPersonLink", idPrtPersonLink)
				.setParameter("idStage", idChildSUBStage)
				.setResultTransformer(Transformers.aliasToBean(PRTConnectionDto.class));

		List<PRTConnectionDto> prtConnectionValueDtos = query.list();
		if (!TypeConvUtil.isNullOrEmpty(prtConnectionValueDtos)) {
			for (PRTConnectionDto prtcon : prtConnectionValueDtos) {
				PRTConnectionDto prtConnection = new PRTConnectionDto();

				prtConnection.setIdPrtConnection(prtcon.getIdPrtConnection());
				prtConnection.setIdPrtPersonLink(prtcon.getIdPrtPersonLink());
				prtConnection.setIdPerson(prtcon.getIdPerson());
				prtConnection.setNmPersonFull(prtcon.getNmPersonFull());
				prtConnection.setConnRellong(prtcon.getConnRellong());
				prtConnection.setConnRellongDecode(prtcon.getRelIntDecode());

				prtConnection.setDtCreated(prtcon.getDtCreated());
				prtConnection.setIdCreatedPerson(prtcon.getIdCreatedPerson());
				prtConnection.setDtLastUpdate(prtcon.getDtLastUpdate());
				prtConnection.setIdLastUpdatePerson(prtcon.getIdLastUpdatePerson());
				prtConnection.setSelectedByUser(true);
				prtConnections.add(prtConnection);
			}
		}
		return prtConnections;
	}

	/**
	 * This method rows from PRT_PARTICIPANT table.
	 * 
	 * @param idPrtActionPlan
	 * 
	 * @return List<PRTParticipantValueBean> or null if record not found.
	 *
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PRTParticipantDto> selectPRTParticipants(Long idPrtActionPlan) {
		List<PRTParticipantDto> paricipantList = new ArrayList<PRTParticipantDto>();
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectPRTParticipantsSql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
				.addScalar("idPrtParticipant", StandardBasicTypes.LONG).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("idPrtActionPlan", StandardBasicTypes.LONG).addScalar("cdPrtRole", StandardBasicTypes.STRING)
				.addScalar("strEmail", StandardBasicTypes.STRING).setParameter("idPrtActionPlan", idPrtActionPlan)
				.setResultTransformer(Transformers.aliasToBean(PRTParticipantDto.class));

		List<PRTParticipantDto> prtParticipantValueDtos = query.list();
		if (!TypeConvUtil.isNullOrEmpty(prtParticipantValueDtos)) {
			for (PRTParticipantDto prtParticipant : prtParticipantValueDtos) {
				PRTParticipantDto participant = new PRTParticipantDto();
				participant.setIdPrtParticipant(prtParticipant.getIdPrtParticipant());
				participant.setDtCreated(prtParticipant.getDtCreated());
				participant.setIdCreatedPerson(prtParticipant.getIdCreatedPerson());
				participant.setDtLastUpdate(prtParticipant.getDtLastUpdate());
				participant.setIdLastUpdatePerson(prtParticipant.getIdLastUpdatePerson());
				participant.setIdPrtActionPlan(prtParticipant.getIdPrtActionPlan());
				participant.setIdPerson(prtParticipant.getIdPerson());
				participant.setCdPrtRole(prtParticipant.getCdPrtRole());
				participant.setNmPersonFull(prtParticipant.getNmPersonFull());
				participant.setNbrPersonPhone(prtParticipant.getNbrPersonPhone());
				participant.setStrEmail(prtParticipant.getStrEmail());
				paricipantList.add(participant);
			}
		}
		return paricipantList;
	}

	/**
	 * Method Name: selectActionPlan Method Description:
	 * 
	 * @param idPrtActionPlan
	 * @return PRTActionPlanDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PRTActionPlanDto selectActionPlan(Long idPrtActionPlan) {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectActionPlanSql)
				.addScalar("idPrtActionPlan", StandardBasicTypes.LONG).addScalar("dtComplete", StandardBasicTypes.DATE)
				.addScalar("brsAchPerm", StandardBasicTypes.STRING)
				.addScalar("brsTriedBefore", StandardBasicTypes.STRING)
				.addScalar("brsNotTriedBefore", StandardBasicTypes.STRING)
				.addScalar("brsTryConcr", StandardBasicTypes.STRING)
				.addScalar("brsYthPlnPerm", StandardBasicTypes.STRING)
				.addScalar("dbrfRecomend", StandardBasicTypes.STRING)
				.addScalar("dbrfExplToFam", StandardBasicTypes.STRING)
				.addScalar("dbrfUnanswrQstn", StandardBasicTypes.STRING)
				.addScalar("dbrfOthrCase", StandardBasicTypes.STRING)
				.addScalar("adtnlConnections", StandardBasicTypes.STRING)
				.addScalar("prtcpntComments", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.DATE).addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("idUnitWorker", StandardBasicTypes.LONG).setParameter("idPrtActionPlan", idPrtActionPlan)
				.setResultTransformer(Transformers.aliasToBean(PRTActionPlanDto.class));

		List<PRTActionPlanDto> actionPlanDtos = query.list();

		return actionPlanDtos.get(ServiceConstants.Zero);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.prt.dao.PRTActionPlanDao#getStageIdsForActPlan(
	 * java. lang.Long)
	 */
	@Override
	public List<Long> getStageIdsForActPlan(Long idActionPlan) {
		List<Long> stageIdList = new ArrayList<Long>();

		List<PRTEventLinkDto> prtActPlnEventList = selectPrtEventLinkWithIdStage(idActionPlan,
				ActionPlanType.ACTION_PLAN);
		if (!CollectionUtils.isEmpty(prtActPlnEventList)) {
			stageIdList = prtActPlnEventList.stream().map(PRTEventLinkDto::getIdStage).collect(Collectors.toList());
		}
		return stageIdList;
	}

	@Override
	public Date getTimeStamp(CommonHelperReq commonHelperReq) {
		String getTimeStampQuery = ServiceConstants.EMPTY_STRING;
		Query query = null;
		if (!ObjectUtils.isEmpty(commonHelperReq.getIdCase()) && ObjectUtils.isEmpty(commonHelperReq.getIdStage())) {
			getTimeStampQuery = getTimeStampByCase.replaceFirst("%table", commonHelperReq.getTableName());
			query = sessionFactory.getCurrentSession().createSQLQuery(getTimeStampQuery)
					.setParameter("idCase", commonHelperReq.getIdCase())
					.setParameter("idEvent", commonHelperReq.getIdEvent());
		}
		if (ObjectUtils.isEmpty(commonHelperReq.getIdCase()) && !ObjectUtils.isEmpty(commonHelperReq.getIdStage())) {
			getTimeStampQuery = getTimeStampByStage.replaceFirst("%table", commonHelperReq.getTableName());
			query = sessionFactory.getCurrentSession().createSQLQuery(getTimeStampQuery)
					.setParameter("idStage", commonHelperReq.getIdStage())
					.setParameter("idEvent", commonHelperReq.getIdEvent());
		}
		Date dtLastUpdate = null;
		if (!ObjectUtils.isEmpty(query)) {
			dtLastUpdate = (Date) query.uniqueResult();
		}
		return dtLastUpdate;
	}

}
