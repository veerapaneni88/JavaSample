package us.tx.state.dfps.service.prt.daoimpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.PrtActionPlan;
import us.tx.state.dfps.common.domain.PrtActplnFollowup;
import us.tx.state.dfps.common.domain.PrtPersonLink;
import us.tx.state.dfps.common.domain.PrtTask;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.ServiceConstants.ActionPlanType;
import us.tx.state.dfps.service.common.request.PRTActplanFollowUpReq;
import us.tx.state.dfps.service.prt.dao.PRTActPlanFollowUpDao;
import us.tx.state.dfps.service.prt.dao.PRTDao;
import us.tx.state.dfps.service.prt.dto.PRTConnectionDto;
import us.tx.state.dfps.service.prt.dto.PRTEventLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskDto;
import us.tx.state.dfps.service.subcare.dto.PRTActPlanFollowUpDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * provides the Dao implementation for retrieving/saving PRT Follow-Up related
 * details. Mar 28, 2018- 3:33:42 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class PRTActPlanFollowUpDaoImpl implements PRTActPlanFollowUpDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	PRTDao prtDao;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-PRTActPlanFollowUpDaoImplLog");

	@Value("${PRTActPlanFollowUpDaoImpl.getPRTCompActPlnFollowup}")
	private String getPRTCompActPlnFollowupSql;

	@Value("${PRTActPlanFollowUpDaoImpl.getPRTConnInCompActPln}")
	private String getPRTConnInCompActPlnSql;

	@Value("${PRTActPlanFollowUpDaoImpl.fetchLatestLegalStatus}")
	private String fetchLatestLegalStatusSql;

	@Value("${PRTActPlanFollowUpDaoImpl.selectLatestFollowUp}")
	private String selectLatestFollowUpSql;

	@Value("${PRTActPlanFollowUpDaoImpl.fetchActionPlanInCOMP}")
	private String fetchActionPlanInCOMPSql;

	@Value("${PRTActPlanFollowUpDaoImpl.fetchOpenActionPlan}")
	private String fetchOpenActionPlanSql;

	@Value("${PRTActPlanFollowUpDaoImpl.selectLatestFollowUpForStage}")
	private String selectLatestFollowUpForStageSql;

	@Value("${PRTActPlanFollowUpDaoImpl.selectPrtChildrenNoStageId}")
	private String selectPrtChildrenNoStageIdSql;

	@Value("${PRTActPlanFollowUpDaoImpl.selectPrtChildrenNoStageId2}")
	private String selectPrtChildrenNoStageIdSql2;

	@Value("${PRTActPlanFollowUpDaoImpl.selectPrtChildrenFollowUp}")
	private String selectPrtChildrenFollowUpSql;

	@Value("${PRTActPlanFollowUpDaoImpl.selectFollowUpUsingEventId}")
	private String selectFollowUpUsingEventIdSql;

	@Value("${PRTActPlanFollowUpDaoImpl.selectPrtEventIdForChild}")
	private String selectPrtEventIdForChildSql;

	@Value("${PRTActPlanFollowUpDaoImpl.selectLatestFollowUpWithPermStatus}")
	private String selectLatestFollowUpWithPermStatusSql;

	@Value("${PRTActPlanFollowUpDaoImpl.selectPRTConnections}")
	private String selectPRTConnectionsSql;

	@Override
	public Long updatePRTFollowupContactToPersLink(PRTPersonLinkDto prtPersonLinkdto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PrtPersonLink.class);
		criteria.add(
				Restrictions.eq("prtActplnFollowup.idPrtActplnFollowup", prtPersonLinkdto.getIdPrtActplnFollowup()));
		criteria.add(Restrictions.eq("person.idPerson", prtPersonLinkdto.getIdPerson()));

		PrtPersonLink prtpersonlink = (PrtPersonLink) criteria.uniqueResult();

		Event event = new Event();
		event.setIdEvent(prtPersonLinkdto.getIdContactEvent());
		prtpersonlink.setEvent(event);

		sessionFactory.getCurrentSession().saveOrUpdate(prtpersonlink);

		return Integer.valueOf(criteria.list().size()).longValue();

	}

	/**
	 * Method Name: getPRTConnInCompActPlnFollowup Method Description: This
	 * method retrieves connections from COMP action plan followup.
	 * 
	 * @param prtActplanFollowUpReq
	 *            - The dto will hold the input paramters such as
	 *            idPerson,idEvent,idFollowUp.
	 * @return Long - The value of number of connections for each child in PRT
	 *         Follow-Up in COMP status.
	 */
	@Override
	public Long getPRTConnInCompActPlnFollowup(PRTActplanFollowUpReq prtActplanFollowUpReq) {
		Long connection = null;
		Long idFollowUp = prtActplanFollowUpReq.getIdFollowUp();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPRTCompActPlnFollowupSql);

		query.setParameter("idPrtActPlnFollowUp", idFollowUp);
		query.setParameter("idPerson", prtActplanFollowUpReq.getIdPerson());
		query.setParameter("idEvent", prtActplanFollowUpReq.getIdEvent());

		connection = !ObjectUtils.isEmpty(query.uniqueResult()) ? ((BigDecimal) query.uniqueResult()).longValue()
				: ServiceConstants.ZERO;
		return connection;
	}

	/**
	 * Method Name: getPRTConnInCompActPln Method Description:This method
	 * retrieves connections from existing COMP action plan.
	 * 
	 * @param prtActplanFollowUpReq
	 *            - The dto will hold the input paramters such as
	 *            idPerson,idEvent,idFollowUp.
	 * @return Long - The value of number of connections for each child in PRT
	 *         Action Plan in COMP status.
	 */
	@Override
	public Long getPRTConnInCompActPln(PRTActplanFollowUpReq prtActplanFollowUpReq) {
		Long connection = null;
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPRTConnInCompActPlnSql);

		query.setParameter("idPrtActionPlan", prtActplanFollowUpReq.getIdActionPlan());
		query.setParameter("idPerson", prtActplanFollowUpReq.getIdPerson());
		query.setParameter("idEvent", prtActplanFollowUpReq.getIdEvent());
		connection = !ObjectUtils.isEmpty(query.uniqueResult()) ? ((BigDecimal) query.uniqueResult()).longValue()
				: ServiceConstants.ZERO;
		return connection;
	}

	/**
	 * Method Name: selectLatestFollowUp Method Description:This method gets
	 * latest FollowUp for the given Action Plan.
	 * 
	 * @param idFollowUp
	 *            - The id of the action plan.
	 * @param eventStatus
	 *            - The event status.
	 * @return Long - The id of the follow-up.
	 */
	@Override
	public Long selectLatestFollowUp(Long idFollowUp, String eventStatus) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectLatestFollowUpSql);
		query.addScalar("idPrtActPlanFollowUp", StandardBasicTypes.LONG);
		query.setParameter("idPrtActionPlan", idFollowUp);
		query.setParameter("cdEventStatus", eventStatus);
		Long idPRTFollowUp = (Long) query.uniqueResult();
		return !ObjectUtils.isEmpty(idPRTFollowUp) ? idPRTFollowUp : ServiceConstants.ZERO;
	}

	/**
	 * Method Name: fetchActionPlanInCOMP Method Description:This method gets
	 * Action Plan in COMP status for the given Stage.
	 * 
	 * @param idStage
	 *            - The current id stage.
	 * @return - The id of the latest PRT action plan .
	 */
	@Override
	public Long fetchActionPlanInCOMP(Long idStage) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchActionPlanInCOMPSql);
		query.addScalar("idPrtActionPlan", StandardBasicTypes.LONG);
		query.setParameter("idEventStage", idStage);
		Long idActionPlan = (Long) query.uniqueResult();
		return !ObjectUtils.isEmpty(idActionPlan) ? idActionPlan : ServiceConstants.ZERO;

	}

	/**
	 * Method Name: fetchOpenActionPlan Method Description:This method gets Open
	 * Action Plan Id for the given Person.
	 * 
	 * @param idPerson
	 *            - The id person of child who is in a open Action Plan who was
	 *            not part of the closing PRT.
	 * @return - The id of the latest PRT Action Plan.
	 */
	@Override
	public Long fetchOpenActionPlan(Long idPerson) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchOpenActionPlanSql);
		query.addScalar("idPrtActionPlan", StandardBasicTypes.LONG);
		query.setParameter("idPerson", idPerson);
		Long idActionPlan = (Long) query.uniqueResult();
		return !ObjectUtils.isEmpty(idActionPlan) ? idActionPlan : ServiceConstants.ZERO;
	}

	/**
	 * Method Name: selectLatestFollowUpForStage Method Description:This method
	 * gets latest FollowUp for the given Action Plan and stage.
	 * 
	 * @param idActionPlan
	 *            - The id of the PRT Action Plan.
	 * @param idStage
	 *            - The id stage.
	 * @param eventStatus
	 *            - The event status.
	 * @return Long - The id of the PRT Follow-Up.
	 */
	@Override
	public Long selectLatestFollowUpForStage(Long idActionPlan, Long idStage, String eventStatus) {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectLatestFollowUpForStageSql);
		query.addScalar("idPrtActPlanFollowUp", StandardBasicTypes.LONG);
		query.setParameter("cdEventStatus", eventStatus);
		query.setParameter("idPrtActionplan", idActionPlan);
		query.setParameter("idEventStage", idStage);
		Long idPRTFollowUp = (Long) query.uniqueResult();
		return !ObjectUtils.isEmpty(idPRTFollowUp) ? idPRTFollowUp : ServiceConstants.ZERO;
	}

	/**
	 * Method Name: selectActionPlanFollowUp Method Description:This method
	 * fetches single row from PRT_ACTPLN_FOLLOWUP table.
	 * 
	 * @param idPrtActplnFollowup
	 *            - The id of the PRT Follow-Up.
	 * @return PRTActPlanFollowUpDto - The dto holds the PRT Follow-Up details.
	 */
	@Override
	public PRTActPlanFollowUpDto selectActionPlanFollowUp(Long idPrtActplnFollowup) {

		PrtActplnFollowup prtActplnFollowup = (PrtActplnFollowup) sessionFactory.getCurrentSession()
				.get(PrtActplnFollowup.class, idPrtActplnFollowup);

		PRTActPlanFollowUpDto prtActPlanFollowUpValueDto = new PRTActPlanFollowUpDto();
		BeanUtils.copyProperties(prtActplnFollowup, prtActPlanFollowUpValueDto);
		prtActPlanFollowUpValueDto.setAdditionalConnections(prtActplnFollowup.getTxtAdtnlConnections());
		prtActPlanFollowUpValueDto.setDebriefProgressMade(prtActplnFollowup.getTxtDbrfProgressMade());
		prtActPlanFollowUpValueDto.setDebriefChlgIdentified(prtActplnFollowup.getTxtDbrfChlgIdentified());
		prtActPlanFollowUpValueDto.setDebriefSolnIdentified(prtActplnFollowup.getTxtDbrfSolnIdentified());
		prtActPlanFollowUpValueDto.setChangeToApplaReason(prtActplnFollowup.getTxtChgtoApplaReason());

		return prtActPlanFollowUpValueDto;
	}

	/**
	 * Method Name: selectPrtChildren Method Description:This method fetches
	 * PRT_PERSON_LINK rows for the given Action Plan.
	 * 
	 * @param idPrtActplnFollowup
	 *            - The id of the PRT Action Plan or the PRT Follow-Up.
	 * @return List - The list of children .
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PRTPersonLinkDto> selectPrtChildren(Long idPrtActplnFollowup) {
		List<PRTPersonLinkDto> prtPersonLinkValueDtoList = sessionFactory.getCurrentSession()
				.createSQLQuery(selectPrtChildrenNoStageIdSql).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("dtBirth", StandardBasicTypes.DATE).addScalar("idPrtPersonLink", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.DATE).addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("idPrtActionPlan", StandardBasicTypes.LONG)
				.addScalar("idPrtActplnFollowup", StandardBasicTypes.LONG)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).addScalar("idContactEvent", StandardBasicTypes.LONG)
				.addScalar("idPrtPermStatusLookup", StandardBasicTypes.LONG).addScalar("cdLastPermDsc")
				.addScalar("dtPrtExit", StandardBasicTypes.DATE).addScalar("cdExitReason")
				.addScalar("cdRcmndPrimaryGoal").addScalar("cdRcmndConcurrentGoal").addScalar("indNoConGoal")
				.addScalar("idPrevPermStatus", StandardBasicTypes.LONG)
				.setParameter("idPrtActPlnFollowUp", idPrtActplnFollowup)
				.setResultTransformer(Transformers.aliasToBean(PRTPersonLinkDto.class)).list();

		List<PRTPersonLinkDto> prtPersonLinkValueDtoList2 = sessionFactory.getCurrentSession()
				.createSQLQuery(selectPrtChildrenNoStageIdSql2).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).setParameter("idPrtActPlnFollowUp", idPrtActplnFollowup)
				.setResultTransformer(Transformers.aliasToBean(PRTPersonLinkDto.class)).list();
		for (PRTPersonLinkDto child : prtPersonLinkValueDtoList2) {
			for (PRTPersonLinkDto child2 : prtPersonLinkValueDtoList) {
				if (child2.getIdPerson().equals(child.getIdPerson())) {
					child2.setIdChildSUBStage(child.getIdStage());
					break;
				}
			}
		}

		return prtPersonLinkValueDtoList;
	}

	/**
	 * Method Name: selectPRTConnections Method Description:This method
	 * overrides base class function. In addition to selecting connections, this
	 * function also sets selected by user flag, for the selected connections.
	 * 
	 * @param idPrtPersonLink
	 *            - The id Person of the child in the PRT Follow-Up.
	 * @param idStage
	 *            - The id stage.
	 * @return List - The list of connections for a particular child.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PRTConnectionDto> selectPRTConnections(Long idPrtPersonLink, Long idStage) {
		List<PRTConnectionDto> prtConnectionDtoList = new ArrayList<PRTConnectionDto>();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(selectPRTConnectionsSql)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("connRellong", StandardBasicTypes.STRING)
				.addScalar("connRellongDecode", StandardBasicTypes.STRING)
				.addScalar("idPrtConnection", StandardBasicTypes.LONG)
				.addScalar("idPrtPersonLink", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.DATE).addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.setParameter("idPrtPersonLink", idPrtPersonLink);

		if (ObjectUtils.isEmpty(idStage)) {
			query.setParameter("idStage", 0);
		} else {
			query.setParameter("idStage", idStage);
		}
		prtConnectionDtoList = query.setResultTransformer(Transformers.aliasToBean(PRTConnectionDto.class)).list();
		prtConnectionDtoList.forEach(connection -> {
			connection.setSelectedByUser(true);
		});
		return prtConnectionDtoList;
	}

	/**
	 * Method Name: getPRTParentTaskInfo Method Description:This method get
	 * parent task information for a given task.
	 * 
	 * @param parentIds
	 *            - The id tasks.
	 * @return List - PRT Task details.
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public List<PRTTaskDto> getPRTParentTaskInfo(List<Long> parentIds) {
		List<PRTTaskDto> prtTaskDtoList = new ArrayList<PRTTaskDto>();
		List<PrtTask> prtTaskList = sessionFactory.getCurrentSession().createCriteria(PrtTask.class)
				.add(Restrictions.in("idPrtTask", parentIds)).list();
		if (!CollectionUtils.isEmpty(prtTaskList)) {

			prtTaskList.forEach(prtTask -> {
				PRTTaskDto prtTaskValueDto = new PRTTaskDto();
				BeanUtils.copyProperties(prtTask, prtTaskValueDto);
				prtTaskDtoList.add(prtTaskValueDto);
			});

		}

		return prtTaskDtoList;

	}

	/**
	 * Method Name: insertActPlanFollowUp Method Description:This method inserts
	 * record into PRT_ACTPLN_FOLLOWUP table.
	 * 
	 * @param followUp
	 *            - The dto with the input values to be saved for the PRT
	 *            Follow-Up .
	 * @return Long - The identifier generated after saving/updating the PRT
	 *         Follow-Up.
	 */
	@Override
	public Long insertActPlanFollowUp(PRTActPlanFollowUpDto followUp) {

		PrtActplnFollowup prtActplnFollowup = new PrtActplnFollowup();
		if (!ObjectUtils.isEmpty(followUp.getIdPrtActionPlan())) {
			PrtActionPlan prtActionPlan = (PrtActionPlan) sessionFactory.getCurrentSession().get(PrtActionPlan.class,
					followUp.getIdPrtActionPlan());
			prtActplnFollowup.setPrtActionPlan(prtActionPlan);
		}
		if (!ObjectUtils.isEmpty(followUp.getIdPrtActplnFollowup()))
			prtActplnFollowup.setIdPrtActplnFollowup(followUp.getIdPrtActplnFollowup());
		if (!ObjectUtils.isEmpty(followUp.getIdLastUpdatePerson()))
			prtActplnFollowup.setIdLastUpdatePerson(followUp.getIdLastUpdatePerson());
		if (!ObjectUtils.isEmpty(followUp.getIdCreatedPerson()))
			prtActplnFollowup.setIdCreatedPerson(followUp.getIdCreatedPerson());
		if (!ObjectUtils.isEmpty(followUp.getIdSrcPrtActplnFollowup()))
			prtActplnFollowup.setIdSrcPrtActplnFollowup(followUp.getIdSrcPrtActplnFollowup());
		if (!ObjectUtils.isEmpty(followUp.getCdType()))
			prtActplnFollowup.setCdType(followUp.getCdType());
		if (!ObjectUtils.isEmpty(followUp.getAdditionalConnections()))
			prtActplnFollowup.setTxtAdtnlConnections(followUp.getAdditionalConnections());
		if (!ObjectUtils.isEmpty(followUp.getDebriefProgressMade()))
			prtActplnFollowup.setTxtDbrfProgressMade(followUp.getDebriefProgressMade());
		if (!ObjectUtils.isEmpty(followUp.getDebriefChlgIdentified()))
			prtActplnFollowup.setTxtDbrfChlgIdentified(followUp.getDebriefChlgIdentified());
		if (!ObjectUtils.isEmpty(followUp.getDebriefSolnIdentified()))
			prtActplnFollowup.setTxtDbrfSolnIdentified(followUp.getDebriefSolnIdentified());
		if (!ObjectUtils.isEmpty(followUp.getIndPdParticipBiannual()))
			prtActplnFollowup.setIndPdParticipBiannual(followUp.getIndPdParticipBiannual().charAt(0));
		if (!ObjectUtils.isEmpty(followUp.getChangeToApplaReason()))
			prtActplnFollowup.setTxtChgtoApplaReason(followUp.getChangeToApplaReason());
		if (!ObjectUtils.isEmpty(followUp.getDtComplete()))
			prtActplnFollowup.setDtComplete(followUp.getDtComplete());
		prtActplnFollowup.setDtCreated(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		prtActplnFollowup.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		Long idFollowUp = (Long) sessionFactory.getCurrentSession().save(prtActplnFollowup);
		return idFollowUp;

	}

	/**
	 * Method Name: updateActPlanFollowUp Method Description:This method updates
	 * PRT_ACTPLN_FOLLOWUP table using PRTActPlanFollowUpValueBean.
	 * 
	 * @param followUp
	 *            -The dto with the input values to be updated for the PRT
	 *            Follow-Up
	 */
	@Override
	public void updateActPlanFollowUp(PRTActPlanFollowUpDto followUp) {
		LOG.info("id prt plan followup" + followUp.getIdPrtActplnFollowup());
		PrtActplnFollowup prtActplnFollowup = (PrtActplnFollowup) sessionFactory.getCurrentSession()
				.get(PrtActplnFollowup.class, followUp.getIdPrtActplnFollowup());
		if (!ObjectUtils.isEmpty(followUp.getIdLastUpdatePerson()))
			prtActplnFollowup.setIdLastUpdatePerson(followUp.getIdLastUpdatePerson());
		if (!ObjectUtils.isEmpty(followUp.getCdType()))
			prtActplnFollowup.setCdType(followUp.getCdType());
		if (!ObjectUtils.isEmpty(followUp.getAdditionalConnections()))
			prtActplnFollowup.setTxtAdtnlConnections(followUp.getAdditionalConnections());
		if (!ObjectUtils.isEmpty(followUp.getDebriefProgressMade()))
			prtActplnFollowup.setTxtDbrfProgressMade(followUp.getDebriefProgressMade());
		if (!ObjectUtils.isEmpty(followUp.getDebriefChlgIdentified()))
			prtActplnFollowup.setTxtDbrfChlgIdentified(followUp.getDebriefChlgIdentified());
		if (!ObjectUtils.isEmpty(followUp.getDebriefSolnIdentified()))
			prtActplnFollowup.setTxtDbrfSolnIdentified(followUp.getDebriefSolnIdentified());
		if (!ObjectUtils.isEmpty(followUp.getIndPdParticipBiannual()))
			prtActplnFollowup.setIndPdParticipBiannual(followUp.getIndPdParticipBiannual().charAt(0));
		if (!ObjectUtils.isEmpty(followUp.getChangeToApplaReason()))
			prtActplnFollowup.setTxtChgtoApplaReason(followUp.getChangeToApplaReason());
		if (!ObjectUtils.isEmpty(followUp.getDtComplete()))
			prtActplnFollowup.setDtComplete(followUp.getDtComplete());

		prtActplnFollowup.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		sessionFactory.getCurrentSession().saveOrUpdate(prtActplnFollowup);

	}

	/**
	 * Method Name: selectFollowUpUsingEventId Method Description:This method
	 * fetches single row from PRT_ACTPLN_FOLLOWUP table using Event Id.
	 * 
	 * @param idPrtActionPlanFollowUpEvent
	 *            - The id event of the PRT Follow-Up.
	 * @return PRTActPlanFollowUpDto - The dto with the PRT Follow-Up details.
	 */
	@Override
	public PRTActPlanFollowUpDto selectFollowUpUsingEventId(Long idPrtActionPlanFollowUpEvent) {

		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(selectFollowUpUsingEventIdSql);
		query.addScalar("idPrtActplnFollowup", StandardBasicTypes.LONG);
		query.addScalar("idPrtActionPlan", StandardBasicTypes.LONG);
		query.addScalar("idSrcPrtActplnFollowup", StandardBasicTypes.LONG);
		query.addScalar("idCreatedPerson", StandardBasicTypes.LONG);
		query.addScalar("cdType", StandardBasicTypes.STRING);
		query.addScalar("additionalConnections", StandardBasicTypes.STRING);
		query.addScalar("debriefProgressMade", StandardBasicTypes.STRING);
		query.addScalar("debriefChlgIdentified", StandardBasicTypes.STRING);
		query.addScalar("debriefSolnIdentified", StandardBasicTypes.STRING);
		query.addScalar("indPdParticipBiannual", StandardBasicTypes.STRING);
		query.addScalar("changeToApplaReason", StandardBasicTypes.STRING);
		query.addScalar("dtComplete", StandardBasicTypes.DATE);
		query.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
		query.addScalar("dtCreated", StandardBasicTypes.DATE);
		query.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG);
		query.setParameter("idevent", idPrtActionPlanFollowUpEvent);
		query.setResultTransformer(Transformers.aliasToBean(PRTActPlanFollowUpDto.class));

		return (PRTActPlanFollowUpDto) query.uniqueResult();

	}

	/**
	 * Method Name: populateEventIdForChild Method Description:This method
	 * populates Event Id associated with the Child.
	 * 
	 * @param children
	 *            - The list of children in the PRT Action Plan.
	 * @param idActPlanOrFollowUp
	 *            - The id of Action Plan or Follow-Up.
	 * @param planType
	 *            - The plan either Action Plan or Follow-Up.
	 */
	@Override
	public void populateEventIdForChild(List<PRTPersonLinkDto> children, Long idActPlanOrFollowUp,
			ActionPlanType planType) {
		String keyColumn = (planType == ActionPlanType.ACTION_PLAN) ? " EVTLINK.ID_PRT_ACTION_PLAN = :idPrtActionPlan "
				: " EVTLINK.ID_PRT_ACTPLN_FOLLOWUP =:idPrtActionPlanFollowUp ";
		Query query = sessionFactory.getCurrentSession().createSQLQuery(selectPrtEventIdForChildSql + keyColumn)
				.addScalar("idEventStage", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idEvent", StandardBasicTypes.LONG);
		if (planType == ActionPlanType.ACTION_PLAN)
			query.setParameter("idPrtActionPlan", idActPlanOrFollowUp);
		else
			query.setParameter("idPrtActionPlanFollowUp", idActPlanOrFollowUp);

		query.setResultTransformer(Transformers.aliasToBean(PRTPersonLinkDto.class));
	}

	/**
	 * Method Name: selectLatestFollowUpWithPermStatus Method Description:This
	 * method gets Latest FollowUp for the Action Plan which has Perm Status
	 * populated.
	 * 
	 * @param idActionPlan
	 * @return
	 */
	@Override
	public Long selectLatestFollowUpWithPermStatus(Long idActionPlan) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(selectLatestFollowUpWithPermStatusSql)
				.addScalar("idPrtActplanFollowUp", StandardBasicTypes.LONG).setParameter("idActionPlan", idActionPlan);
		return !ObjectUtils.isEmpty(query.uniqueResult()) ? (Long) query.uniqueResult() : ServiceConstants.ZERO;
	}

	/**
	 * Method Name: getActPlanEventId Method Description:This method retrieves
	 * the event id of the Follow-Up using the id Action Plan.
	 * 
	 * @param idActionPlan
	 *            - The id of action plan.
	 * @return Long - The id of the follow-up.
	 */
	@Override
	public Long getActPlanEventId(Long idActionPlan) {
		Long idActionPlanEvent = 0l;
		if (!ObjectUtils.isEmpty(idActionPlan)) {
			List<PRTEventLinkDto> actPlanEvtList = prtDao.selectPrtEventLink(idActionPlan, ActionPlanType.ACTION_PLAN);
			if (!CollectionUtils.isEmpty(actPlanEvtList)) {
				idActionPlanEvent = actPlanEvtList.get(0).getIdEvent();
			}
		}
		return idActionPlanEvent;
	}

}
