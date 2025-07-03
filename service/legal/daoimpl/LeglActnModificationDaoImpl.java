/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Oct 25, 2017- 11:00:26 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.legal.daoimpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Employee;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.EventPersonLink;
import us.tx.state.dfps.common.domain.LegalAction;
import us.tx.state.dfps.common.domain.LegalStatus;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.common.dto.EmailDetailsDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.legal.dao.LeglActnModificationDao;
import us.tx.state.dfps.service.legal.dao.ToDoEventDao;
import us.tx.state.dfps.service.legal.dto.FetchToDoOutDto;
import us.tx.state.dfps.service.legal.dto.LegalActionRtrvOutDto;
import us.tx.state.dfps.service.legal.dto.LegalActionsModificationDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.daoimpl.PersonDaoImpl;
import us.tx.state.dfps.xmlstructs.inputstructs.ServiceInputDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Oct 25, 2017- 11:00:26 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class LeglActnModificationDaoImpl implements LeglActnModificationDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private LookupDao lookupDao;

	@Autowired
	PersonDao pdao;
	@Autowired
	ToDoEventDao toDoEventDao;

	@Value("${LegalActionDaoImpl.fetchEmailAddress}")
	private String fetchEmailAddressSql;
	
	@Value("${LegalActionDaoImpl.fetchLatestTMCLegalStatus}")
	private String fetchLatestTMCLegalStatusSql;

	/**
	 * 
	 * Method Name: insertOrUpdateLegalAction Method Description:Inserts/Updates
	 * and Deletes the Legal Action. DAM: caud03d
	 * 
	 * @param legalActionsModificationDto
	 * @throws ParseException
	 * @
	 */
	@Override
	public LegalAction insertOrUpdateLegalAction(LegalActionsModificationDto legalActionsModificationDto) {

		ServiceInputDto serviceInputDto = legalActionsModificationDto.getServiceInputDto();
		Long idToDo = ServiceConstants.ZERO_VAL;
		LegalAction legalAction = new LegalAction();
		if (ServiceConstants.REQ_FUNC_CD_ADD.equals(serviceInputDto.getCreqFuncCd())) {

			legalAction.setIdLegalActEvent(legalActionsModificationDto.getIdLegalActEvent());
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
					legalActionsModificationDto.getIdPerson());
			if(ObjectUtils.isEmpty(person)) {
				person = pdao.getPersonByPersonId(legalActionsModificationDto.getIdLastUpdatePerson());
			}
			if (TypeConvUtil.isNullOrEmpty(person)) {
				throw new DataNotFoundException(
						messageSource.getMessage("person.personlist.data.notFound", null, Locale.US));
			}
			Event event = (Event) sessionFactory.getCurrentSession().get(Event.class,
					legalActionsModificationDto.getIdLegalActEvent());

			legalAction.setCdLegalActAction(legalActionsModificationDto.getCdLegalActAction());
			legalAction.setCdLegalActActnSubtype(legalActionsModificationDto.getCdLegalActActnSubtype());
			legalAction.setCdLegalActOutcome(legalActionsModificationDto.getCdLegalActOutcome());
			legalAction.setDtLegalActDateFiled(legalActionsModificationDto.getDtLegalActDateFiled());
			legalAction.setDtLegalActOutcomeDt(legalActionsModificationDto.getDtLegalActOutcomeDate());
			legalAction.setIndLegalActDocsNCase(legalActionsModificationDto.getCIndLegalActDocsNCase().charAt(0));
			legalAction.setTxtLegalActComment(legalActionsModificationDto.getTxtLegalActComment());
			legalAction.setIndFdtcGraduated(legalActionsModificationDto.getCIndFDTCGraduated());
			legalAction.setCdFdtcEndReason(legalActionsModificationDto.getCdFDTCEndReason());
			legalAction.setCdLegalActOutSub(legalActionsModificationDto.getCdLegalActOutSub());
			legalAction.setIdLastUpdatePerson(legalActionsModificationDto.getIdLastUpdatePerson());
			legalAction.setIndLegalActDocsNCase(legalActionsModificationDto.getCIndLegalActDocsNCase().charAt(0));
			legalAction.setIndLegalActActionTkn(legalActionsModificationDto.getCIndLegalActActionTkn().charAt(0));
			legalAction.setDtLastUpdate(legalActionsModificationDto.getTsLastUpdate());
			legalAction.setCdLegalActOutSub(legalActionsModificationDto.getCdLegalActOutSub());
			legalAction.setPerson(person);
			legalAction.setEvent(event);
			if(!ObjectUtils.isEmpty(legalActionsModificationDto.getCdQRTPCourtStatus()))
				legalAction.setCdQrtpCourtStatus(legalActionsModificationDto.getCdQRTPCourtStatus());

			idToDo = (Long) sessionFactory.getCurrentSession().save(legalAction);

			if (idToDo.equals(ServiceConstants.LONG_ZERO_VAL)) {
				throw new DataNotFoundException(lookupDao.getMessageByNumber("1403"));
			}
		} else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(serviceInputDto.getCreqFuncCd())) {
			legalAction = (LegalAction) sessionFactory.getCurrentSession().get(LegalAction.class,
					legalActionsModificationDto.getIdLegalActEvent());
			if(!ObjectUtils.isEmpty(legalAction)) {
			// DT_LAST_UPDATE
			legalAction.setDtLastUpdate(new Date());
			// ID_PERSON - Update Person only if the person is changed
			if (!(0 == legalActionsModificationDto.getIdPerson().compareTo(legalAction.getPerson().getIdPerson()))) {
				Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
						legalActionsModificationDto.getIdPerson());
				if (TypeConvUtil.isNullOrEmpty(person)) {
					throw new DataNotFoundException(
							messageSource.getMessage("person.personlist.data.notFound", null, Locale.US));
				}
				legalAction.setPerson(person);
			}
			// CD_LEGAL_ACT_ACTION
			if (!ObjectUtils.isEmpty(legalActionsModificationDto.getCdLegalActAction()))
				legalAction.setCdLegalActAction(legalActionsModificationDto.getCdLegalActAction());
			// CD_LEGAL_ACT_ACTN_SUBTYPE
			// ALM # 14945 : Override existing substype when user doesn't select any sub type for legal action & outcome save
			if (null != legalActionsModificationDto.getCdLegalActActnSubtype())
				legalAction.setCdLegalActActnSubtype(legalActionsModificationDto.getCdLegalActActnSubtype());
			// CD_LEGAL_ACT_OUTCOME
			if (!ObjectUtils.isEmpty(legalActionsModificationDto.getCdLegalActOutcome()))
				legalAction.setCdLegalActOutcome(legalActionsModificationDto.getCdLegalActOutcome());
			// DT_LEGAL_ACT_OUTCOME_DT
			if (!ObjectUtils.isEmpty(legalActionsModificationDto.getDtLegalActOutcomeDate()))
				legalAction.setDtLegalActOutcomeDt(legalActionsModificationDto.getDtLegalActOutcomeDate());
			// IND_LEGAL_ACT_DOCS_N_CASE
			if (!ObjectUtils.isEmpty(legalActionsModificationDto.getCIndLegalActDocsNCase()))
				legalAction.setIndLegalActDocsNCase(legalActionsModificationDto.getCIndLegalActDocsNCase().charAt(0));
			// IND_LEGAL_ACT_ACTION_TKN
			if (!ObjectUtils.isEmpty(legalActionsModificationDto.getCIndLegalActActionTkn()))
				legalAction.setIndLegalActActionTkn(legalActionsModificationDto.getCIndLegalActActionTkn().charAt(0));
			// TXT_LEGAL_ACT_COMMENT
			if (!ObjectUtils.isEmpty(legalActionsModificationDto.getTxtLegalActComment()))
				legalAction.setTxtLegalActComment(legalActionsModificationDto.getTxtLegalActComment());
			// IND_FDTC_GRADUATED
			if (!ObjectUtils.isEmpty(legalActionsModificationDto.getCIndFDTCGraduated()))
				legalAction.setIndFdtcGraduated(legalActionsModificationDto.getCIndFDTCGraduated());
			// CD_LEGAL_ACT_OUT_SUB
			if (!ObjectUtils.isEmpty(legalActionsModificationDto.getCdLegalActOutSub()))
				legalAction.setCdLegalActOutSub(legalActionsModificationDto.getCdLegalActOutSub());
			// CD_QRTP_COURT_STATUS
			if (!ObjectUtils.isEmpty(legalActionsModificationDto.getCdQRTPCourtStatus()))
				legalAction.setCdQrtpCourtStatus(legalActionsModificationDto.getCdQRTPCourtStatus());
			// DT_LEGAL_ACT_DATE_FILED
			if (!ObjectUtils.isEmpty(legalActionsModificationDto.getDtLegalActDateFiled()))
				try {
					legalAction.setDtLegalActDateFiled(
							DateUtils.getTimestamp(legalActionsModificationDto.getDtLegalActDateFiled(),
									legalActionsModificationDto.getScheduledTime()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			// CD_FDTC_END_REASON
			if (!ObjectUtils.isEmpty(legalActionsModificationDto.getCdFDTCEndReason()))
				legalAction.setCdFdtcEndReason(legalActionsModificationDto.getCdFDTCEndReason());
			// ID_LAST_UPDATE_PERSON
			if (!ObjectUtils.isEmpty(legalActionsModificationDto.getIdLastUpdatePerson()))
				legalAction.setIdLastUpdatePerson(legalActionsModificationDto.getIdLastUpdatePerson());

			sessionFactory.getCurrentSession().saveOrUpdate(legalAction);
			}
		} else if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(serviceInputDto.getCreqFuncCd())) {

			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LegalAction.class);
			criteria.add(Restrictions.eq("idLegalActEvent", legalActionsModificationDto.getIdLegalActEvent()));
			// criteria.add(Restrictions.eq("dtLastUpdate",
			// legalActionsModificationDto.getTsLastUpdate()));
			List<LegalAction> legalActionList = criteria.list();
			for (LegalAction legalActionVal : legalActionList) {
				sessionFactory.getCurrentSession().delete(legalActionVal);
			}

			Criteria criteria1 = sessionFactory.getCurrentSession().createCriteria(Todo.class);
			criteria1.add(Restrictions.eq("event.idEvent", legalActionsModificationDto.getIdLegalActEvent()));
			List<Todo> todoList = criteria1.list();
			for (Todo todo : todoList) {
				sessionFactory.getCurrentSession().delete(todo);
			}

			Criteria criteria2 = sessionFactory.getCurrentSession().createCriteria(EventPersonLink.class);
			criteria2.add(Restrictions.eq("event.idEvent", legalActionsModificationDto.getIdLegalActEvent()));
			List<EventPersonLink> eventPersonLinkList = criteria2.list();
			for (EventPersonLink eventPersonLink : eventPersonLinkList) {
				sessionFactory.getCurrentSession().delete(eventPersonLink);
			}
//			if (eventPersonLinkList.size() == ServiceConstants.Zero) {
//				throw new DataNotFoundException(lookupDao.getMessageByNumber("1403"));
//			}

			Criteria criteria3 = sessionFactory.getCurrentSession().createCriteria(Event.class);
			criteria3.add(Restrictions.eq("idEvent", legalActionsModificationDto.getIdLegalActEvent()));
			List<Event> eventList = criteria3.list();
			for (Event event : eventList) {
				sessionFactory.getCurrentSession().delete(event);
			}
			if (eventList.size() == ServiceConstants.Zero) {
				throw new DataNotFoundException(lookupDao.getMessageByNumber("1403"));
			}
		} else {
			throw new DataNotFoundException(lookupDao.getMessageByNumber("13550"));
		}
		//sessionFactory.getCurrentSession().flush();
		return legalAction;
	}

	/**
	 * 
	 * Method Name: fetchEmployeeEmail Method Description:This Method is used
	 * for fetching the primary and secondary case-workers employee email
	 * addresses based on the event id
	 * 
	 * @param idEventList
	 * @return List<EmailDetailsDto> @
	 */
	@Override
	public List<EmailDetailsDto> fetchEmployeeEmail(Long idEvent) {
		// Creating the New List of EmailDetailsDto to be sent as response
		List<EmailDetailsDto> emailDetailsDtoList = new ArrayList<EmailDetailsDto>();
		// creating the SQL query to fetch the list of emailDTO's
		emailDetailsDtoList = (List<EmailDetailsDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(fetchEmailAddressSql).setParameter("idEvent", idEvent))
						.addScalar("emailAddress", StandardBasicTypes.STRING)
						.addScalar("stageName", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(EmailDetailsDto.class)).list();

		return emailDetailsDtoList;
	}

	/**
	 * 
	 * Method Name: fetchLegalActionEventIds Method Description:This Method is
	 * used for fetching the List of eventId along with
	 * dtScheduledCourt,cdLegalActActnSubtype for the given idCase for the task
	 * code '3030', event type 'LEG', action type 'CCVS' and sub type in
	 * '30','40','60'
	 * 
	 * @param idCase
	 * @return List<LegalActionRtrvOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<LegalActionRtrvOutDto> fetchLegalActionEventIds(Long idCase) {
		List<LegalActionRtrvOutDto> legalActionRtrvOutDtos = new ArrayList<LegalActionRtrvOutDto>();
		// Creating the New List of EmailDetailsDto to be sent as response
		List<String> subTypeList = Arrays.asList("30", "40", "60");
		
		List<String> cdTask = Arrays.asList("3030", "4350", "8540", "7210", "5850", "9850");
		// creating the SQL query to fetch the list of idEvent's
		legalActionRtrvOutDtos = (List<LegalActionRtrvOutDto>) sessionFactory.getCurrentSession()
				.createCriteria(Event.class, "event").add(Restrictions.eq("event.idCase", idCase))
				.createCriteria("legalAction", "legal").add(Restrictions.in("event.cdTask", cdTask))
				.add(Restrictions.eq("event.cdEventType", "LEG"))
				.add(Restrictions.in("legal.cdLegalActActnSubtype", subTypeList))
				.setProjection(Projections.projectionList()
						.add(Projections.property("legal.idLegalActEvent"), "idLegalActEvent")
						.add(Projections.property("legal.cdLegalActActnSubtype"), "cdLegalActActnSubtype"))
				.addOrder(Order.desc("legal.dtLastUpdate"))
				.setResultTransformer(Transformers.aliasToBean(LegalActionRtrvOutDto.class)).list();

		// if the any idevent is present iterate the list
		if (!CollectionUtils.isEmpty(legalActionRtrvOutDtos)) {
			for (LegalActionRtrvOutDto legalActionRtrvOutDto : legalActionRtrvOutDtos) {
				// setting decode value for LegalActActnSubtype
				legalActionRtrvOutDto.setCdLegalActActnSubtype(
						lookupDao.decode(ServiceConstants.CCVS, legalActionRtrvOutDto.getCdLegalActActnSubtype()));
				// get the DtTodoDue from Todo table for the given idEvent
				List<FetchToDoOutDto> searchArrayToDoDtoList = toDoEventDao
						.fetchToDoListForEvent(legalActionRtrvOutDto.getIdLegalActEvent());
				if (!CollectionUtils.isEmpty(searchArrayToDoDtoList)) {
					for (FetchToDoOutDto searchTodoDto : searchArrayToDoDtoList) {
						if (searchTodoDto.getIdTodo() != ServiceConstants.ZERO_VAL) {
							legalActionRtrvOutDto.setDtScheduledCourtDate(DateUtils.stringDateAndTimestamp(searchTodoDto.getScrTaskDue()));
							break;
						}
					}
				}
			}
		}
		return legalActionRtrvOutDtos;
	}

	/* (non-Javadoc)
	 * @see us.tx.state.dfps.service.legal.dao.LeglActnModificationDao#checkTMCExists(java.lang.Long, java.lang.String)
	 */
	@Override
	public Long checkTMCExists(Long idStage, String cdLegalStatStatus) {
		Long idEvent = null;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchLatestTMCLegalStatusSql)
				.setParameter("idStage", idStage).setParameter("cdStatus", cdLegalStatStatus);
		sqlQuery.addScalar("idEvent", StandardBasicTypes.LONG);
		List<Long> idEventList = sqlQuery.list();
		if (!CollectionUtils.isEmpty(idEventList)) {
			idEvent = idEventList.get(0);
		}

		return idEvent;
	}

	/**
	 * 
	 * Method Name: getEmailAddress 
	 * Method Description:This Method is used
	 * for fetching the primary and secondary case-workers employee email
	 * addresses based on the employee id
	 * 
	 * @param idPersonList
	 * @return List<EmailDetailsDto>
	 */
	@Override
	public List<EmailDetailsDto> getEmailAddress(List<Long> idPersonList, Long idEvent) {
		//Warranty Defect#12114 - Issue fixed to avoid dupilcate outlook calender invite
		// Creating the New List of EmailDetailsDto to be sent as response
		List<EmailDetailsDto> emailDetailsDtoList = new ArrayList<EmailDetailsDto>();
		StringBuffer emailAddressQuery = new StringBuffer(fetchEmailAddressSql);
		emailAddressQuery.append(" AND EMP.ID_PERSON IN :idPersonList");
		// creating the SQL query to fetch the list of emailDTO's
		emailDetailsDtoList = (List<EmailDetailsDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(emailAddressQuery.toString()).setParameter("idEvent", idEvent).setParameterList("idPersonList", idPersonList))
						.addScalar("emailAddress", StandardBasicTypes.STRING)
						.addScalar("stageName", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(EmailDetailsDto.class)).list();

		return emailDetailsDtoList;
	}

}
