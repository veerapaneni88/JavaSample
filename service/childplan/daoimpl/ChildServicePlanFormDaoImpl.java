package us.tx.state.dfps.service.childplan.daoimpl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
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
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ChildPlanItem;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.ChildPlanItemDto;
import us.tx.state.dfps.service.admin.dto.CapsPlacemntDto;
import us.tx.state.dfps.service.childplan.dao.ChildServicePlanFormDao;
import us.tx.state.dfps.service.childplan.dto.ChildParticipantRowDODto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanDetailsDto;
import us.tx.state.dfps.service.childplan.dto.ConcurrentGoalDto;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

@Repository
public class ChildServicePlanFormDaoImpl implements ChildServicePlanFormDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ChildServicePlanFormDaoImpl.getPersonChildDetails}")
	private String getPersonChildDetails;

	@Value("${ChildServicePlanFormDaoImpl.getEventDetails}")
	private String getEventDetails;

	@Value("${ChildServicePlanFormDaoImpl.getChildPlanDetailsSql}")
	private String getChildPlanDetailsSql;

	@Value("${ChildServicePlanFormDaoImpl.getChildPlanCountSql}")
	private String getChildPlanCountSql;

	@Value("${ChildServicePlanFormDaoImpl.getChildPlanParticipants}")
	private String getChildPlanParticipants;

	@Value("${ChildServicePlanFormDaoImpl.fetchEventDetails}")
	private String fetchEventDetails;

	@Value("${ChildServicePlanFormDaoImpl.getConcurrentData}")
	private String getConcurrentData;

	@Value("${ChildServicePlanFormDaoImpl.getResourcePlacementDetail}")
	private String getResourcePlacementDetail;

	private static final Logger log = Logger.getLogger(ChildServicePlanFormDaoImpl.class);

	/*
	 * Method Name: getIdPersonPerChildPlan Method Description: This method
	 * retrieves the id_person from the EVENT table for the worker who completes
	 * a given CHILD PLAN. DAM name : CSES93D
	 * 
	 * @param idEvent
	 * 
	 * @return EventDto
	 * 
	 * @
	 */
	@Override
	public EventDto getIdPersonPerChildPlan(Long idEvent) {
		log.debug("Entering method getIdPersonPerChildPlan in ChildServicePlanFormDaoImpl");
		EventDto eventDto = new EventDto();
		eventDto = (EventDto) sessionFactory.getCurrentSession().createCriteria(Event.class)
				.setProjection(Projections.projectionList().add(Projections.property("person.idPerson"), "idPerson"))
				.add(Restrictions.eq("idEvent", idEvent)).setResultTransformer(Transformers.aliasToBean(EventDto.class))
				.uniqueResult();

		log.debug("Exiting method getIdPersonPerChildPlan in ChildServicePlanFormDaoImpl");
		return eventDto;
	}

	/*
	 * Method Name:getPersonDetails Method Description: This method retrieves a
	 * row from the person table based on an input of IdPerson. It also links to
	 * the Name table to retreive Name information for that person. DAM name :
	 * CSEC74D
	 * 
	 * @param idPerson
	 * 
	 * @return PersonDto
	 * 
	 * @
	 */
	@Override
	public PersonDto getPersonDetails(Long idPerson) {
		log.debug("Entering method getPersonDetails in ChildServicePlanFormDaoImpl");
		PersonDto personDto = null;
		personDto = (PersonDto) sessionFactory.getCurrentSession().createCriteria(Person.class)
				.setProjection(Projections.projectionList().add(Projections.property("cdPersonDeath"), "cdPersonDeath")
						.add(Projections.property("cdPersonEthnicGroup"), "cdPersonEthnicGroup")
						.add(Projections.property("cdPersonLanguage"), "cdPersonLanguage")
						.add(Projections.property("cdPersonMaritalStatus"), "cdPersonMaritalStatus")
						.add(Projections.property("cdPersonReligion"), "cdPersonReligion")
						.add(Projections.property("cdPersonSex"), "cdPersonSex")
						.add(Projections.property("cdPersonStatus"), "cdPersonStatus")
						.add(Projections.property("dtPersonBirth"), "dtPersonBirth")
						.add(Projections.property("dtPersonDeath"), "dtPersonDeath")
						.add(Projections.property("nbrPersonAge"), "nbrPersnAge")
						.add(Projections.property("nmPersonFull"), "nmPersonFull")
						.add(Projections.property("txtPersonOccupation"), "txtPersonOccupation")
						.add(Projections.property("indPersCancelHist"), "indPersCancelHist")
						.add(Projections.property("cdPersGuardCnsrv"), "cdPersGuardCnsrv")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("cdPersonLivArr"), "cdPersonLivArr")
						.add(Projections.property("cdPersonChar"), "cdPersonChar")
						.add(Projections.property("indPersonDobApprox"), "indPersonDobApprox")
						.add(Projections.property("nmPersonFirst"), "nmPersonFirst")
						.add(Projections.property("nmPersonLast"), "nmPersonLast")
						.add(Projections.property("nmPersonMiddle"), "nmPersonMiddle")
						.add(Projections.property("cdPersonSuffix"), "cdPersonSuffix")
						.add(Projections.property("idPerson"), "idPerson"))
				.add(Restrictions.eq("idPerson", idPerson))
				.setResultTransformer(Transformers.aliasToBean(PersonDto.class)).uniqueResult();
		log.debug("Exiting method getPersonDetails in ChildServicePlanFormDaoImpl");
		return personDto;

	}

	/*
	 * Method Name:getPersonChildDetails Method Description: This method joins
	 * retrieves valued from Person and Child Plan where PERSON.IdPerson ==
	 * CHILD PLAN.IdPerson and CHILD PLAN.IdChild Plan Event == Host:IdChild
	 * PlanEvent. DAM name : CSES27D
	 * 
	 * @param idChPerson
	 * 
	 * @return PersonDto
	 * 
	 * @
	 */
	@Override
	public PersonDto getPersonChildDetails(Long idEvent) {
		PersonDto personDto = null;
		log.debug("Entering method getPersonChildDetails in ChildServicePlanFormDaoImpl");
		Query queryGetPersonChildDetails = sessionFactory.getCurrentSession().createSQLQuery(getPersonChildDetails)
				.addScalar("idChildPlanEvent", StandardBasicTypes.LONG)
				.addScalar("dtChildPlanLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idChPerson", StandardBasicTypes.LONG)
				.addScalar("cspPlanPermGoal", StandardBasicTypes.STRING)
				.addScalar("cdCspPlanType", StandardBasicTypes.STRING)
				.addScalar("dtcspPermGoalTarget", StandardBasicTypes.DATE)
				.addScalar("dtCspNextReview", StandardBasicTypes.DATE)
				.addScalar("txtCspLengthOfStay", StandardBasicTypes.STRING)
				.addScalar("txtCspLosDiscrepancy", StandardBasicTypes.STRING)
				.addScalar("txtCspParticpatnComment", StandardBasicTypes.STRING)
				.addScalar("cdPersonSex", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("nbrPersonAge", StandardBasicTypes.SHORT).addScalar("dtPersonDeath", StandardBasicTypes.DATE)
				.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("cdPersonReligion", StandardBasicTypes.STRING)
				.addScalar("cdPersonChar", StandardBasicTypes.STRING)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("cdPersonLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPersGuardCnsrv", StandardBasicTypes.STRING)
				.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
				.addScalar("cdPersonDeath", StandardBasicTypes.STRING)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("txtPersonOccupation", StandardBasicTypes.STRING)
				.addScalar("cdPersonLanguage", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("indPersCancelHist", StandardBasicTypes.STRING)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("cdPersonSuffix", StandardBasicTypes.STRING)
				.addScalar("dtInitialTransitionPlan", StandardBasicTypes.DATE)
				.addScalar("txtInfoNotAvailable", StandardBasicTypes.STRING)
				.addScalar("txtOtherAssmt", StandardBasicTypes.STRING)
				.addScalar("indNoConGoal", StandardBasicTypes.STRING)
				.addScalar("txtNoConsGoal", StandardBasicTypes.STRING)
				.addScalar("cdSsccPurpose", StandardBasicTypes.STRING).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(PersonDto.class));
		personDto = (PersonDto) queryGetPersonChildDetails.uniqueResult();
		log.debug("Exiting method getPersonChildDetails in ChildServicePlanFormDaoImpl");
		return personDto;
	}

	/*
	 * Method Name:getEventDetails Method Description: This method returns the
	 * EVENT Details based on the Event Id DAM name : CSES30D
	 * 
	 * @param idChPerson
	 * 
	 * @return PersonDto
	 * 
	 * @
	 */
	@Override
	public EventDto getEventDetails(Long idEvent) {
		EventDto eventDto = null;
		log.debug("Entering method getEventDetails in ChildServicePlanFormDaoImpl");
		Query queryGetEventDetails = sessionFactory.getCurrentSession().createSQLQuery(getEventDetails)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdEventType", StandardBasicTypes.STRING)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdEventTask", StandardBasicTypes.STRING)
				.addScalar("eventDescription", StandardBasicTypes.STRING)
				.addScalar("dtEventOccurred", StandardBasicTypes.DATE)
				.addScalar("idApprovalEvent", StandardBasicTypes.LONG).addScalar("idApproval", StandardBasicTypes.LONG)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(EventDto.class));
		eventDto = (EventDto) queryGetEventDetails.uniqueResult();
		log.debug("Exiting method getEventDetails in ChildServicePlanFormDaoImpl");
		return eventDto;
	}

	/*
	 * Method Name:getEventDetails Method Description: This method displays the
	 * "Date of Last Plan" field on the Child Plan form and populates the
	 * DT_LAST_UPDATE of the previous plan with respect to the most recent plan
	 * in the stage. It populates with the DT_LAST_UPDATE of the previous plan
	 * with respect to the child plan currently being viewed. For this to
	 * happen, the id_event of the child plan currently being viewed needs to be
	 * passed into this method DAM name : CSEC14D
	 * 
	 * @param idPerson, idChildPlanEvent
	 * 
	 * @return List<ChildPlanDetailsDto>
	 * 
	 * @
	 */

	@Override
	public ChildPlanDetailsDto getChildPlanDetails(Long idPerson, Long idChildPlanEvent) {

		log.debug("Entering method getChildPlanDetails in ChildServicePlanFormDaoImpl");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getChildPlanCountSql);
		query.setParameter("idPerson", idPerson);
		query.setParameter("idChildPlanEvent", idChildPlanEvent);

		BigDecimal tempcoun = (BigDecimal) query.uniqueResult();
		int tempcount = tempcoun.intValue();

		ChildPlanDetailsDto childPlanDetailsDt = null;

		Query childPlanDetailsQuery = sessionFactory.getCurrentSession().createSQLQuery(getChildPlanDetailsSql)
				.addScalar("idChildPlanEvent", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cspPlanPermGoal", StandardBasicTypes.STRING)
				.addScalar("cspPlanType", StandardBasicTypes.STRING)
				.addScalar("dtCspPermGoalTarget", StandardBasicTypes.DATE)
				.addScalar("dtCspNextReview", StandardBasicTypes.DATE)
				.addScalar("cspLengthOfStay", StandardBasicTypes.STRING)
				.addScalar("cspLossDiscrepancy", StandardBasicTypes.STRING)
				.addScalar("cspParticipationComment", StandardBasicTypes.STRING)
				.addScalar("dtCspPlanCompleted", StandardBasicTypes.DATE)
				.addScalar("dateOfLastPlan", StandardBasicTypes.DATE)
				.addScalar("cdSSCCPurpose", StandardBasicTypes.STRING)
				.setParameter("idChildPlanEvent", idChildPlanEvent).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(ChildPlanDetailsDto.class));
		
		
		List<ChildPlanDetailsDto> childPlaResultList=childPlanDetailsQuery.list();
		if(!ObjectUtils.isEmpty(childPlaResultList)){
			childPlanDetailsDt = (ChildPlanDetailsDto) childPlanDetailsQuery.list().get(0);
			if ( tempcount > 1) {
				ChildPlanDetailsDto previousPlanDto = (ChildPlanDetailsDto) childPlanDetailsQuery.list().get(1);
				childPlanDetailsDt.setDateOfLastPlan(previousPlanDto.getDtCspPlanCompleted());
			}
		}
		

		log.debug("Exiting method getChildPlanDetails in ChildServicePlanFormDaoImpl");
		return childPlanDetailsDt;
	}

	/*
	 * Method Name:getChildPlanParticipants Method Description: This method
	 * retrieves all child plan participants based on the ID EVENT. DAM name :
	 * CLSS20D
	 * 
	 * @param idChildPlanEvent
	 * 
	 * @return ChildParticipantRowDODto
	 * 
	 * @
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<ChildParticipantRowDODto> getChildPlanParticipants(Long idChildPlanEvent) {
		log.debug("Entering method getChildPlanParticipants in ChildServicePlanFormDaoImpl");
		List<ChildParticipantRowDODto> childParticipantRowDODto = null;
		Query childPlanParticipationQuery = sessionFactory.getCurrentSession().createSQLQuery(getChildPlanParticipants)
				.addScalar("ulIdChildPlanPart", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.DATE).addScalar("ulIdPerson", StandardBasicTypes.LONG)
				.addScalar("ulIdChildPlanEvent", StandardBasicTypes.LONG)
				.addScalar("szCdCspPartNotifType", StandardBasicTypes.STRING)
				.addScalar("szCdCspPartType", StandardBasicTypes.STRING)
				.addScalar("dtDtCspDateNotified", StandardBasicTypes.DATE)
				.addScalar("dtDtCspPartCopyGiven", StandardBasicTypes.DATE)
				.addScalar("dtDtCspPartParticipate", StandardBasicTypes.DATE)
				.addScalar("szNmCspPartFull", StandardBasicTypes.STRING)
				.addScalar("szSdsCspPartRelationship", StandardBasicTypes.STRING)
				.setParameter("idChildPlanEvent", idChildPlanEvent)
				.setResultTransformer(Transformers.aliasToBean(ChildParticipantRowDODto.class));
		childParticipantRowDODto = childPlanParticipationQuery.list();
		log.debug("Exiting method getChildPlanParticipants in ChildServicePlanFormDaoImpl");
		return childParticipantRowDODto;
	}

	/*
	 * Method Name:fetchEventDetails Method Description: This method retrieves
	 * all eventdetails based on the ID EVENT. DAM name : CCMN45D
	 * 
	 * @param idEvent
	 * 
	 * @return List<EventDto>
	 * 
	 * @
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<EventDto> fetchEventDetails(Long idEvent) {
		log.debug("Entering method fetchEventDetails in ChildServicePlanFormDaoImpl");
		List<EventDto> eventDtoList = null;
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchEventDetails)
				.addScalar("cdEventTask", StandardBasicTypes.STRING).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.addScalar("cdEventType", StandardBasicTypes.STRING)
				.addScalar("dtEventOccurred", StandardBasicTypes.DATE).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("eventDescription", StandardBasicTypes.STRING)
				.addScalar("dtEventCreated", StandardBasicTypes.DATE).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(EventDto.class));
		eventDtoList = (List<EventDto>) sQLQuery1.list();
		log.debug("Exiting method ccmn45dQUERYdam in Ccmn45dDaoImpl");
		return eventDtoList;

	}

	/*
	 * Method Name:getConcurrentData Method Description: This dam will retrieve
	 * all the information from cp_concurrent_goal table for a given id_event.
	 * DAM name :CSESF3D
	 * 
	 * @param idEvent
	 * 
	 * @return List<FetchFullConcurrentDto>
	 * 
	 * @
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<ConcurrentGoalDto> getConcurrentData(Long idEvent) {
		log.debug("Entering method getConcurrentData in ChildServicePlanFormDaoImpl");
		List<ConcurrentGoalDto> fetchFullConcurrentDtoList = null;
		SQLQuery getConcurrentDataSql = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getConcurrentData)
				.addScalar("idCpConGoal", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idChildPlanEvent", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdConcurrentGoal", StandardBasicTypes.STRING)
				.setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(ConcurrentGoalDto.class));
		fetchFullConcurrentDtoList = getConcurrentDataSql.list();
		log.debug("Exiting method getConcurrentData in ChildServicePlanFormDaoImpl");
		return fetchFullConcurrentDtoList;

	}

	/**
	 * Method Name: getResourcePlacementDetail Method Description: CSES28D - Get
	 * Resource and Placement details for person
	 * 
	 * @param idPerson
	 * @return List<CapsPlacemntDto>
	 */

	@Override
	public CapsPlacemntDto getResourcePlacementDetail(Long idPerson) {
		log.debug("Entering method getResourceDetail in LegalStatusDaoImpl");
		CapsPlacemntDto capsPlacemntDto = null;
		Query sQLQuery1 = sessionFactory.getCurrentSession().createSQLQuery(getResourcePlacementDetail)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).addScalar("tsLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idPlcmtAdult", StandardBasicTypes.LONG).addScalar("idPlcmtChild", StandardBasicTypes.LONG)
				.addScalar("idRsrcAgency", StandardBasicTypes.LONG).addScalar("idRsrcFacil", StandardBasicTypes.LONG)
				.addScalar("addrPlcmtCity", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtCnty", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtLn1", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtLn2", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtSt", StandardBasicTypes.STRING)
				.addScalar("addrPlcmtZip", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo1", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo2", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo3", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo4", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo5", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo6", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtInfo7", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtRemovalRsn", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtActPlanned", StandardBasicTypes.STRING)
				.addScalar("cdPlcmtType", StandardBasicTypes.STRING)
				.addScalar("dtPlcmtCaregvrDiscuss", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtChildDiscuss", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtChildPlan", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtEducLog", StandardBasicTypes.DATE).addScalar("dtPlcmtEnd", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtMeddevHistory", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtParentsNotif", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtPreplaceVisit", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtSchoolRecords", StandardBasicTypes.DATE)
				.addScalar("dtPlcmtStart", StandardBasicTypes.DATE)
				.addScalar("indPlcmtContCntct", StandardBasicTypes.STRING)
				.addScalar("indPlcmtEducLog", StandardBasicTypes.STRING)
				.addScalar("indPlcmetEmerg", StandardBasicTypes.STRING)
				.addScalar("indPlcmtNotApplic", StandardBasicTypes.STRING)
				.addScalar("indPlcmtSchoolDoc", StandardBasicTypes.STRING)
				.addScalar("indPlcmtWriteHistory", StandardBasicTypes.STRING)
				.addScalar("nbrPlcmtPhoneExt", StandardBasicTypes.STRING)
				.addScalar("nbrPlcmtTelephone", StandardBasicTypes.STRING)
				.addScalar("nmPlcmtAgency", StandardBasicTypes.STRING)
				.addScalar("nmPlcmtContact", StandardBasicTypes.STRING)
				.addScalar("nmPlcmtFacil", StandardBasicTypes.STRING)
				.addScalar("nmPlcmtPersonFull", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtAddrComment", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtDiscussion", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtDocuments", StandardBasicTypes.STRING)
				.addScalar("txtPlcmtRemovalRsn", StandardBasicTypes.STRING)
				.addScalar("idResource", StandardBasicTypes.LONG).addScalar("addrRsrcStLn1", StandardBasicTypes.STRING)
				.addScalar("addrRsrcStLn2", StandardBasicTypes.STRING)
				.addScalar("addrRsrcCity", StandardBasicTypes.STRING)
				.addScalar("cdRsrcState", StandardBasicTypes.STRING).addScalar("addrRsrcZip", StandardBasicTypes.STRING)
				.addScalar("addrRsrcAttn", StandardBasicTypes.STRING).addScalar("cdRsrcCnty", StandardBasicTypes.STRING)
				.addScalar("cdRsrcInvolClosure", StandardBasicTypes.STRING)
				.addScalar("cdRsrcClosureRsn", StandardBasicTypes.STRING)
				.addScalar("cdRsrcCampusType", StandardBasicTypes.STRING)
				.addScalar("cdRsrcCategory", StandardBasicTypes.STRING)
				.addScalar("cdRsrcCertBy", StandardBasicTypes.STRING)
				.addScalar("cdRsrcEthnicity", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeStatus", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType1", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType2", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType3", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType4", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType5", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType6", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFaHomeType7", StandardBasicTypes.STRING)
				.addScalar("cdRsrcFacilType", StandardBasicTypes.STRING)
				.addScalar("cdRsrcLanguage", StandardBasicTypes.STRING)
				.addScalar("cdRsrcMaintainer", StandardBasicTypes.STRING)
				.addScalar("cdRsrcMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("cdRsrcOperBy", StandardBasicTypes.STRING)
				.addScalar("cdRsrcOwnership", StandardBasicTypes.STRING)
				.addScalar("cdRsrcPayment", StandardBasicTypes.STRING)
				.addScalar("cdRsrcRecmndReopen", StandardBasicTypes.STRING)
				.addScalar("cdRsrcRegion", StandardBasicTypes.STRING)
				.addScalar("cdRsrcReligion", StandardBasicTypes.STRING)
				.addScalar("cdRsrcRespite", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSchDist", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSetting", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSourceInquiry", StandardBasicTypes.STRING)
				.addScalar("cdRsrcStatus", StandardBasicTypes.STRING).addScalar("cdRsrcType", StandardBasicTypes.STRING)
				.addScalar("dtRsrcMarriage", StandardBasicTypes.DATE).addScalar("dtRsrcCert", StandardBasicTypes.DATE)
				.addScalar("dtRsrcClose", StandardBasicTypes.DATE)
				.addScalar("idRsrcFaHomeEvent", StandardBasicTypes.LONG)
				.addScalar("idRsrcFaHomeStage", StandardBasicTypes.LONG)
				.addScalar("indRsrcCareProv", StandardBasicTypes.STRING)
				.addScalar("indRsrcEmergPlace", StandardBasicTypes.STRING)
				.addScalar("indRsrcInactive", StandardBasicTypes.STRING)
				.addScalar("indRsrindivStudy", StandardBasicTypes.STRING)
				.addScalar("indRsrcNonPrs", StandardBasicTypes.STRING)
				.addScalar("indRsrcTransport", StandardBasicTypes.STRING)
				.addScalar("indRsrcWriteHist", StandardBasicTypes.STRING)
				.addScalar("nmRsrcLastUpdate", StandardBasicTypes.STRING)
				.addScalar("nmResource", StandardBasicTypes.STRING)
				.addScalar("nmRsrcContact", StandardBasicTypes.STRING)
				.addScalar("nbrRsrcAnnualIncome", StandardBasicTypes.DOUBLE)
				.addScalar("nbrSchCampunbr", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcFacilAcclaim", StandardBasicTypes.LONG)
				.addScalar("nbrRsrcFacilCapacity", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcFMAgeMax", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcFMAgeMin", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcMlAgeMax", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcMlAgeMin", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcIntChildren", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcIntFeAgeMax", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcIntFeAgeMin", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcIntMaAgeMax", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcIntMaAgeMin", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcOpenSlots", StandardBasicTypes.SHORT)
				.addScalar("nbrRsrcPhn", StandardBasicTypes.STRING)
				.addScalar("nbrFacilPhoneExtension", StandardBasicTypes.STRING)
				.addScalar("nbrRsrcVid", StandardBasicTypes.STRING)
				.addScalar("txtRsrcAddrCmnts", StandardBasicTypes.STRING)
				.addScalar("txtRsrcComments", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(CapsPlacemntDto.class));

		capsPlacemntDto = (CapsPlacemntDto) sQLQuery1.uniqueResult();
		return capsPlacemntDto;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ChildPlanItemDto> geChildPlanItems(Long idChildPlanEvent) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(ChildPlanItem.class)
				.createAlias("event", "event").add(Restrictions.eq("event.idEvent", idChildPlanEvent))
				.setProjection(
						Projections.projectionList().add(Projections.property("idChildPlanItem"), "idChildPlanItem")
								.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
								.add(Projections.property("event.idEvent"), "idChildPlanEvent")
								.add(Projections.property("cdCspItemNeed"), "cdCspItemNeed")
								.add(Projections.property("cdCspItemService"), "cdCspItemService")
								.add(Projections.property("cdCspItemTask"), "cdCspItemTask")
								.add(Projections.property("txtCspItemMethodEval"), "txtCspItemMethodEval")
								.add(Projections.property("txtCspItemSvcFreq"), "txtCspItemSvcFreq")
								.add(Projections.property("txtCspItemTaskFreq"), "txtCspItemTaskFreq")
								.add(Projections.property("txtCspService"), "txtCspService")
								.add(Projections.property("txtCspTask"), "txtCspTask"))
				.setResultTransformer(Transformers.aliasToBean(ChildPlanItemDto.class));

		return cr.list();
	}
}