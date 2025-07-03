/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Mar 7, 2018- 10:41:28 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.forms.daoimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.forms.dao.FamilyPlanEvalDao;
import us.tx.state.dfps.service.forms.dto.FamilyAssmtFactDto;
import us.tx.state.dfps.service.forms.dto.FamilyChildNameGaolDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanEvaItemDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanEvalDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanGoalDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanItemDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanParticipantsDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanTaskDto;
import us.tx.state.dfps.service.person.dto.EventDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 7, 2018- 10:41:28 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class FamilyPlanEvalDaoImpl implements FamilyPlanEvalDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${FamilyPlanEvalDaoImpl.getFamilyPlanEvalTable}")
	private transient String getFamilyPlanEvalTable;

	@Value("${FamilyPlanEvalDaoImpl.getFamilyPlanTable}")
	private transient String getFamilyPlanTable;

	@Value("${FamilyPlanEvalDaoImpl.getFamilyPlanEvalItems}")
	private transient String getFamilyPlanEvalItems;

	@Value("${FamilyPlanEvalDaoImpl.getDistinctPricipleNames1}")
	private transient String getDistinctPricipleNames1;

	@Value("${FamilyPlanEvalDaoImpl.getDistinctPricipleNames2}")
	private transient String getDistinctPricipleNames2;

	@Value("${FamilyPlanEvalDaoImpl.getFamilyChildNameGaol1}")
	private transient String getFamilyChildNameGaol1;

	@Value("${FamilyPlanEvalDaoImpl.getFamilyChildNameGaol2}")
	private transient String getFamilyChildNameGaol2;

	@Value("${FamilyPlanEvalDaoImpl.getFamilyPlanParticipants}")
	private transient String getFamilyPlanParticipants;

	@Value("${FamilyPlanEvalDaoImpl.getFamilyPlanTasks}")
	private transient String getFamilyPlanTasks;

	@Value("${FamilyPlanEvalDaoImpl.getInitialConcernsPlan1}")
	private transient String getInitialConcernsPlan1;

	@Value("${FamilyPlanEvalDaoImpl.getInitialConcernsPlan2}")
	private transient String getInitialConcernsPlan2;

	@Value("${FamilyPlanEvalDaoImpl.getFamDateComplete}")
	private transient String getFamDateComplete;

	@Value("${FamilyPlanEvalDaoImpl.getIndicatartorVals}")
	private transient String getIndicatartorVals;

	@Value("${FamilyPlanEvalDaoImpl.getDateCompletedEval}")
	private transient String getDateCompletedEval1;

	@Value("${FamilyPlanEvalDaoImpl.getInitialConcerns1}")
	private transient String getInitialConcerns1;

	@Value("${FamilyPlanEvalDaoImpl.getInitialConcerns2}")
	private transient String getInitialConcerns2;

	@Value("${FamilyPlanEvalDaoImpl.getFamilyPlanGoals}")
	private transient String getFamilyPlanGoals;

	/**
	 * 
	 * Method Name: getFamilyPlanTable Method Description:(CSVC41D) Queries
	 * FAMILY_PLAN table.
	 * 
	 * @param idFamilyPlanEvaluation
	 * @return @
	 */
	@Override
	public FamilyPlanDto getFamilyPlanTable(Long idEvent) {
		FamilyPlanDto familyPlanDto = new FamilyPlanDto();
		familyPlanDto = (FamilyPlanDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getFamilyPlanTable).setParameter("idEvent", idEvent))
						.addScalar("idFamilyPlan", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idCase", StandardBasicTypes.LONG)
						.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("dtCompleted", StandardBasicTypes.DATE)
						.addScalar("cdPlanType", StandardBasicTypes.STRING)
						.addScalar("dtNextReview", StandardBasicTypes.DATE)
						.addScalar("txtRsnCpsInvlvmnt", StandardBasicTypes.STRING)
						.addScalar("indClientComments", StandardBasicTypes.STRING)
						.addScalar("txtStrngthsRsrcs", StandardBasicTypes.STRING)
						.addScalar("txtNotParticipate", StandardBasicTypes.STRING)
						.addScalar("dtNextDue", StandardBasicTypes.DATE)
						.addScalar("evalCompleted", StandardBasicTypes.DATE)
						.addScalar("txtPermGoalComments", StandardBasicTypes.STRING)
						.addScalar("cdFgdmConference", StandardBasicTypes.STRING)
						.addScalar("txtCommunitySupports", StandardBasicTypes.STRING)
						.addScalar("txtHopesDreams", StandardBasicTypes.STRING)
						.addScalar("txtOtherParticipants", StandardBasicTypes.STRING)
						.addScalar("txtRespChildsEducation", StandardBasicTypes.STRING)
						.addScalar("nbrFamPlanVersion", StandardBasicTypes.STRING)
						.addScalar("txtCelebration", StandardBasicTypes.STRING)
						.addScalar("txtPurposeReconference", StandardBasicTypes.STRING)
						.addScalar("txtNextReviewMMYYYY", StandardBasicTypes.STRING)
						.addScalar("idEvalEvent", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(FamilyPlanDto.class)).uniqueResult();
		return familyPlanDto;
	}

	/**
	 * 
	 * Method Name: getFamilyPlanEvalTable Method Description:(CSVC43D) Queries
	 * FAMILY_PLAN_EVAL table.
	 * 
	 * @param idEvent
	 * @return @
	 */
	@Override
	public FamilyPlanEvalDto getFamilyPlanEvalTable(Long idEvent) {
		FamilyPlanEvalDto familyPlanEvalDto = new FamilyPlanEvalDto();
		familyPlanEvalDto = (FamilyPlanEvalDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getFamilyPlanEvalTable).setParameter("idEvent", idEvent))
						.addScalar("idFamilyPlanEvaluation", StandardBasicTypes.LONG)
						.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("dtCompleted", StandardBasicTypes.DATE)
						.addScalar("dtNextDue", StandardBasicTypes.DATE)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("nbrFamPlanEvalVersion", StandardBasicTypes.LONG)
						.addScalar("txtOtherParticipants", StandardBasicTypes.STRING)
						.addScalar("txtCelebration", StandardBasicTypes.STRING)
						.addScalar("txtPurposeReconfernce", StandardBasicTypes.STRING)
						.addScalar("txtNextDueMMYYY", StandardBasicTypes.STRING)
						.addScalar("dtPlanCompleted", StandardBasicTypes.DATE)
						.addScalar("cdFgdmConference", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(FamilyPlanEvalDto.class)).uniqueResult();
		return familyPlanEvalDto;
	}

	/**
	 * 
	 * Method Name: getFamilyPlanEvalItems Method Description:(CSVC40D) Queries
	 * the FAMILY_PLAN_EVAL_ITEM table to get the evaluation items.
	 * 
	 * @param idEvent
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FamilyPlanEvaItemDto> getFamilyPlanEvalItems(Long idEvent) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFamilyPlanEvalItems)
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanEvaItemDto.class)));
		sQLQuery1.addScalar("cdAreaConcern", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("idFamilyPlanEvalItem", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("idFamPlanEvalItem", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idFamilyPlanItem", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idCase", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("txtItemEvaluation", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtCompleted", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("dtNewDue", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("txtNewConcerns", StandardBasicTypes.STRING);
		sQLQuery1.setParameter("idEvent", idEvent);
		List<FamilyPlanEvaItemDto> familyAssmtFactList = (List<FamilyPlanEvaItemDto>) sQLQuery1.list();
		return familyAssmtFactList;
	}

	/**
	 * 
	 * Method Name: getDistinctPricipleNames Method Description:(CLSC23D) Simple
	 * retrieval of a list of distinct principal names from table NAME,
	 * FAMILY_ASSMT_FACTORS for a given ID_FAM_ASSMT_EVENT
	 * 
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FamilyAssmtFactDto> getDistinctPricipleNames(Long idSvcPlanEvent, Date effectiveDate) {
		FamilyAssmtFactDto familyAssmtFactDto = new FamilyAssmtFactDto();
		familyAssmtFactDto = (FamilyAssmtFactDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getDistinctPricipleNames1).setParameter("idSvcPlanEvent", idSvcPlanEvent)
				.setParameter("effectiveDate", effectiveDate)).addScalar("idEventStage", StandardBasicTypes.LONG)
						.addScalar("dtPlanEffective", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(FamilyAssmtFactDto.class)).uniqueResult();

		if (!ObjectUtils.isEmpty(familyAssmtFactDto) && !ObjectUtils.isEmpty(familyAssmtFactDto.getIdEventStage())) {
			Long idEventStage = familyAssmtFactDto.getIdEventStage();
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(getDistinctPricipleNames2)
					.setResultTransformer(Transformers.aliasToBean(FamilyAssmtFactDto.class)));
			sQLQuery1.addScalar("nmNameFirst", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("nmNameMiddle", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("nmNameLast", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("idName", StandardBasicTypes.LONG);
			sQLQuery1.addScalar("cdNameSuffix", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("cdStagePersType", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("cdStagePersRole", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("comma", StandardBasicTypes.STRING);
			sQLQuery1.setParameter("idSvcPlanEvent", idSvcPlanEvent);
			sQLQuery1.setParameter("idEventStage", idEventStage);
			List<FamilyAssmtFactDto> familyAssmtFactList = (List<FamilyAssmtFactDto>) sQLQuery1.list();
			return familyAssmtFactList;
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * 
	 * Method Name: getFamilyChildNameGaol Method Description:(CLSCB5D) This
	 * will be used to query the EVENT_PERSON_LINK, PERSON, STAGE_PERSON_LINK,
	 * and STAGE tables to retrieve the name and goal of child in a family plan.
	 * 
	 * @return
	 * @ @throws
	 *       ParseException
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public List<FamilyChildNameGaolDto> getFamilyChildNameGaol(Long idEvent) {
		FamilyChildNameGaolDto familyChildNameGaolDto = new FamilyChildNameGaolDto();
		familyChildNameGaolDto = (FamilyChildNameGaolDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getFamilyChildNameGaol1).setParameter("idEvent", idEvent))
						.addScalar("dtEffectiveDate", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(FamilyChildNameGaolDto.class)).uniqueResult();
		if (!ObjectUtils.isEmpty(familyChildNameGaolDto)
				&& !ObjectUtils.isEmpty(familyChildNameGaolDto.getDtEffectiveDate())) {
			Date effectDate = familyChildNameGaolDto.getDtEffectiveDate();
			Query sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFamilyChildNameGaol2)
					.addScalar("cdFamPlanPermGoal", StandardBasicTypes.STRING)
					.addScalar("idName", StandardBasicTypes.LONG).addScalar("nmNameFirst", StandardBasicTypes.STRING)
					.addScalar("nmNameMiddle", StandardBasicTypes.STRING)
					.addScalar("nmNameLast", StandardBasicTypes.STRING)
					.addScalar("cdNameSuffix", StandardBasicTypes.STRING)
					.addScalar("dtFamPlanPermGoalTarget", StandardBasicTypes.DATE)
					.setParameter("dtEffectiveDate", effectDate).setParameter("idEvent", idEvent)
					.setResultTransformer(Transformers.aliasToBean(FamilyChildNameGaolDto.class));
			List<FamilyChildNameGaolDto> goalsList = (List<FamilyChildNameGaolDto>) sQLQuery1.list();
			return goalsList;
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * 
	 * Method Name: getFamilyPlanParticipants Method Description:(CLSC24D)
	 * Retrieval of the list FAMILY PLAN Participants.
	 * 
	 * @param idSvcPlanEvent
	 * @param idEvent
	 * @param dtTodayDate
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FamilyPlanParticipantsDto> getFamilyPlanParticipants(Long idSvcPlanEvent, Date effectiveDate) {
		FamilyPlanParticipantsDto familyPlanParticipantsDto = new FamilyPlanParticipantsDto();
		familyPlanParticipantsDto = (FamilyPlanParticipantsDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getDistinctPricipleNames1).setParameter("idSvcPlanEvent", idSvcPlanEvent)
				.setParameter("effectiveDate", effectiveDate)).addScalar("idEventStage", StandardBasicTypes.LONG)
						.addScalar("dtPlanEffective", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(FamilyPlanParticipantsDto.class)).uniqueResult();
		if (!ObjectUtils.isEmpty(familyPlanParticipantsDto)
				&& !ObjectUtils.isEmpty(familyPlanParticipantsDto.getIdEventStage())) {
			Long idEventStage = familyPlanParticipantsDto.getIdEventStage();
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(getFamilyPlanParticipants)
					.setResultTransformer(Transformers.aliasToBean(FamilyPlanParticipantsDto.class)));
			sQLQuery1.addScalar("nmNameFirst", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("nmNameMiddle", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("nmNameLast", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("idName", StandardBasicTypes.LONG);
			sQLQuery1.addScalar("cdNameSuffix", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("cdStagePersType", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("cdStagePersRole", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("indFamPlanPrincipal", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("indFamPlanPart", StandardBasicTypes.STRING);
			sQLQuery1.setParameter("idSvcPlanEvent", idSvcPlanEvent);
			sQLQuery1.setParameter("idEventStage", idEventStage);
			List<FamilyPlanParticipantsDto> FamilyPlanParticipantsList = (List<FamilyPlanParticipantsDto>) sQLQuery1
					.list();
			return FamilyPlanParticipantsList;
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * 
	 * Method Name: getFamilyPlanGoals Method Description:(CSVC56D) Retrieval of
	 * FAMILY PLAN GOALS list. FAMILY_PLAN_GOAL.TXT_GOAL
	 * 
	 * @param idEvent
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FamilyPlanGoalDto> getFamilyPlanGoals(Long idEvent) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFamilyPlanGoals)
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanGoalDto.class)));
		sQLQuery1.addScalar("idFamilyPlanGoal", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("txtGoal", StandardBasicTypes.STRING);
		sQLQuery1.setParameter("idEvent", idEvent);
		List<FamilyPlanGoalDto> FamilyPlanGoalList = (List<FamilyPlanGoalDto>) sQLQuery1.list();
		return FamilyPlanGoalList;
	}

	/**
	 * 
	 * Method Name: getFamilyPlanTasks Method Description:(CSVC52D) Queries the
	 * FAMILY_PLAN_TASK table.
	 * 
	 * @param idEvent
	 * @param idSvcPlanEvent
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FamilyPlanTaskDto> getFamilyPlanTasks(Long idEvent, Long idSvcPlanEvent) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFamilyPlanTasks)
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanTaskDto.class)));
		sQLQuery1.addScalar("idFamilyPlanTask", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("idFamilyPlanItem", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idEvent", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idCase", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("indCourtOrdered", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("txtTask", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtCreated", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("dtCompleted", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("dtApproved", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("dtNoLongerNeeded", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("idCompletedEvent", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indCompletedFamPlan", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indCpsAssigned", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indCpsOrdered", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indFamilyAssigned", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indFamilyOrdered", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indParentsAssigned", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indParentsOrdered", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("txtAssignedTo", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("txtCourtOrderedTo", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("txtDtCompleted", StandardBasicTypes.STRING);
		sQLQuery1.setParameter("idSvcPlanEvent", idSvcPlanEvent);
		sQLQuery1.setParameter("idEvent", idEvent);
		List<FamilyPlanTaskDto> FamilyPlanTaskList = (List<FamilyPlanTaskDto>) sQLQuery1.list();
		return FamilyPlanTaskList;
	}

	/**
	 * 
	 * Method Name: getInitialConcernsPlan Method Description:(CSVC54D) Queries
	 * the FAMILY_PLAN_ITEM table to select Initial Concerns of a PLAN
	 * 
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FamilyPlanItemDto> getInitialConcernsPlan(Long idEvent, String dtApr08Rollout) {
		// Warranty Defect - 12042 - Logic has been corrected to pull the Initial Concerns
		FamilyPlanItemDto familyPlanItemDto = new FamilyPlanItemDto();
		List<FamilyPlanItemDto> familyPlanItemList=new ArrayList<FamilyPlanItemDto>();
		familyPlanItemDto = (FamilyPlanItemDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getInitialConcernsPlan1).setParameter("idEvent", idEvent)
				.setParameter("dtApr08Rollout", dtApr08Rollout)).addScalar("dtEventCreated", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(FamilyPlanItemDto.class)).uniqueResult();
		if (ObjectUtils.isEmpty(familyPlanItemDto)) {
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getInitialConcernsPlan2)
					.setResultTransformer(Transformers.aliasToBean(FamilyPlanItemDto.class)));
			sQLQuery1.addScalar("idFamilyPlanItem", StandardBasicTypes.LONG);
			sQLQuery1.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
			sQLQuery1.addScalar("idEvent", StandardBasicTypes.LONG);
			sQLQuery1.addScalar("idCase", StandardBasicTypes.LONG);
			sQLQuery1.addScalar("cdAreaConcern", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("cdInitialLevelConcern", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("cdPrevLevelConcern", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("cdCurrentLevelConcern", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("txtItemGoals", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("dtInitiallyAddressed", StandardBasicTypes.DATE);
			sQLQuery1.addScalar("indIdentifiedInRiskAssmnt", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("txtInitialConcerns", StandardBasicTypes.STRING);			
			sQLQuery1.addScalar("idAddressedEvent", StandardBasicTypes.LONG);
			sQLQuery1.addScalar("indAddressedFamPlan", StandardBasicTypes.STRING);
			sQLQuery1.setParameter("idEvent", idEvent);
			familyPlanItemList = (List<FamilyPlanItemDto>) sQLQuery1.list();			
		}
		return familyPlanItemList;
	}

	/**
	 * 
	 * Method Name: getFamDateComplete Method Description:(CSVC58D) Queries the
	 * FAMILY_PLAN_EVAL table to get the dtCompleted of every evaluation for a
	 * given family plan.
	 * 
	 * @param idEvent
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FamilyPlanEvalDto> getFamDateComplete(Long idEvent) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFamDateComplete)
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanEvalDto.class)));
		sQLQuery1.addScalar("dtCompleted", StandardBasicTypes.DATE);
		sQLQuery1.setParameter("idEvent", idEvent);
		List<FamilyPlanEvalDto> FamilyPlanTaskList = (List<FamilyPlanEvalDto>) sQLQuery1.list();
		return FamilyPlanTaskList;
	}

	/**
	 * 
	 * Method Name: getIndicatartorVals Method Description:(CSESC8D) This DAM
	 * will select for indicator values for a family plan event created
	 * before/after 27Apr08.
	 * 
	 * @param dtEventOccurred
	 * @return @
	 */
	@Override
	public EventDto getIndicatartorVals(String dtApr08Rollout, String version3, String version2, Long idEvent) {
		EventDto eventDto = new EventDto();
		eventDto = (EventDto) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getIndicatartorVals)
				.setParameter("idEvent", idEvent).setParameter("dtApr08Rollout", dtApr08Rollout)
				.setParameter("version3", version3).setParameter("version2", version2))
						.addScalar("indInitConcernsVer", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(EventDto.class)).uniqueResult();
		return eventDto;
	}

	/**
	 * 
	 * Method Name: getDateCompletedEval Method Description:(CSVC53D) Queries
	 * the FAMILY_PLAN and the FAMILY_PLAN_EVAL tables to get the Dt Completed
	 * of the plan and every evaluation for a given family plan.
	 * 
	 * @param idEvent
	 * @param rollOut
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FamilyPlanDto> getDateCompletedEval(Long idEvent, String dtApr08Rollout) {
		FamilyPlanDto familyPlanDto = new FamilyPlanDto();
		familyPlanDto = (FamilyPlanDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getInitialConcerns1).setParameter("idEvent", idEvent)
				.setParameter("dtApr08Rollout", dtApr08Rollout)).addScalar("dtEventCreated", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(FamilyPlanDto.class)).uniqueResult();
		if (!ObjectUtils.isEmpty(familyPlanDto) && !ObjectUtils.isEmpty(familyPlanDto.getDtEventCreated())) {
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getDateCompletedEval1)
					.setResultTransformer(Transformers.aliasToBean(FamilyPlanDto.class)));
			sQLQuery1.addScalar("idEvent", StandardBasicTypes.LONG);
			sQLQuery1.addScalar("dtCompleted", StandardBasicTypes.DATE);
			sQLQuery1.setParameter("idEvent", idEvent);
			List<FamilyPlanDto> FamilyPlanList = (List<FamilyPlanDto>) sQLQuery1.list();
			return FamilyPlanList;
		}
		return Collections.EMPTY_LIST;
	}

	// add
	/**
	 * 
	 * Method Name: getInitialConcerns Method Description:(CSVC55D) Queries the
	 * FAMILY_PLAN_ITEM table to select Initial Concerns of a PLAN and queries
	 * the FAMILY_PLAN and FAMILY_PLAN_EVAL tables for DT_COMPLETED which is
	 * passed to the form as DT_INITIALLY_ADDRESSED.
	 * 
	 * @param dtEventOccurred
	 * @param idEvent
	 * @param rollOut
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FamilyPlanDto> getInitialConcerns(Long idEvent, String dtApr08Rollout) {
		FamilyPlanDto familyPlanDto = new FamilyPlanDto();
		familyPlanDto = (FamilyPlanDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getInitialConcerns1).setParameter("idEvent", idEvent)
				.setParameter("dtApr08Rollout", dtApr08Rollout)).addScalar("dtEventCreated", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(FamilyPlanDto.class)).uniqueResult();
		if (!ObjectUtils.isEmpty(familyPlanDto) && !ObjectUtils.isEmpty(familyPlanDto.getDtEventCreated())) {
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getInitialConcerns2)
					.setResultTransformer(Transformers.aliasToBean(FamilyPlanDto.class)));
			sQLQuery1.addScalar("cdAreaConcern", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("idFamilyPlanItem", StandardBasicTypes.LONG);
			sQLQuery1.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
			sQLQuery1.addScalar("idEvent", StandardBasicTypes.LONG);
			sQLQuery1.addScalar("idCase", StandardBasicTypes.LONG);
			sQLQuery1.addScalar("cdInitialLevelConcern", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("cdPrevLevelConcern", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("cdCurrentLevelConcern", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("txtItemGoals", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("dtCompleted", StandardBasicTypes.DATE);
			sQLQuery1.addScalar("indIdentifiedInRiskAssmnt", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("txtInitialConcerns", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("idAddressedEvent", StandardBasicTypes.LONG);
			sQLQuery1.addScalar("indAddressedFamPlan", StandardBasicTypes.STRING);
			sQLQuery1.setParameter("idEvent", idEvent);
			List<FamilyPlanDto> FamilyPlanList = (List<FamilyPlanDto>) sQLQuery1.list();
			return FamilyPlanList;
		}
		return Collections.EMPTY_LIST;
	}

}
