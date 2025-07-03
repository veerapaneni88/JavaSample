package us.tx.state.dfps.service.investigation.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.Contact;
import us.tx.state.dfps.common.domain.CpsChecklist;
import us.tx.state.dfps.common.domain.CpsChecklistItem;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.service.casepackage.dto.PcspDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PcspReq;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.investigation.dao.SrvreferralslDao;
import us.tx.state.dfps.service.workload.dto.TodoDto;

/**
 * 
 * @author VISWAV
 *
 */
@Repository()
public class SrvreferralsDaoImpl implements SrvreferralslDao {

	@Value("${ServicesAndReferralsDaoImpl.getContactInfo.sql}")
	private String getContactInfoSql;

	@Value("${SrvreferralsDaoImpl.getPcsp}")
	private String getPcsp;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	public SrvreferralsDaoImpl() {

	}

	/**
	 * 
	 * Method Description: This Method will retrieve the EventId from Event
	 * table. Dam Name: CCMN45D
	 * 
	 * @param uidEvent
	 * @return Event @
	 */
	@Override
	public CpsChecklist getCpsChecklistByEventId(Long eventId) {

		Object obj = null;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpsChecklist.class);
		criteria.createCriteria("event", "evnt");
		criteria.add(Restrictions.eq("evnt.idEvent", eventId));

		obj = criteria.setMaxResults(1).uniqueResult();

		CpsChecklist cpsChecklist = null;
		if (obj != null) {
			cpsChecklist = (CpsChecklist) obj;
		}
		return cpsChecklist;
	}

	/**
	 * 
	 * Method Description: This Method will retrieve the earliest non-NULL DT
	 * CONTACT OCCURRED for a given ID STAGE. It will return NULL is a date does
	 * not exist Dam Name: CSYS15D
	 * 
	 * @param SrvrflReq
	 * @return Contact @
	 */
	@Override
	public Contact getContactByStageId(long SrvrflReq) {

		Contact contact = null;
		contact = (Contact) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(this.getGetContactInfoSql())
				.setParameter("idStage", SrvrflReq)).addScalar("dtContactOccurred", StandardBasicTypes.TIMESTAMP)
						.setResultTransformer(Transformers.aliasToBean(Contact.class)).setMaxResults(1).uniqueResult();

		return contact;

	}

	/**
	 * 
	 * Method Description: This Method will Deleting cps checklist items if
	 * exits Dam Name: CLSS81D
	 * 
	 * @param cpsChecklistItem
	 * @return deleteCpsChecklistItem @
	 */
	@Override
	public void deleteCpsChecklistItem(CpsChecklistItem cpsChecklistItem) {

		Object obj = sessionFactory.getCurrentSession().get(CpsChecklistItem.class,
				cpsChecklistItem.getIdCpsChecklistItem());
		if (obj != null) {
			CpsChecklistItem CpsChecklistItem1 = (CpsChecklistItem) obj;
			sessionFactory.getCurrentSession().delete(CpsChecklistItem1);
		}

	}

	/**
	 * 
	 * Method Description: This Method will Save or update checklist item in the
	 * table. Dam Name: CAUDE3D
	 * 
	 * @param cpsChecklist
	 * @return void @
	 */
	@Override
	public void saveCpsChecklistItem(CpsChecklistItem cpsChecklistItem) {

		sessionFactory.getCurrentSession().save(cpsChecklistItem);

	}

	/**
	 * 
	 * Method Description: This Method will retrieve the CPS checklist item from
	 * table CPS CheckList. Dam Name: CSESA2D
	 * 
	 * @param eventId
	 * @return CpsChecklist @
	 */
	@Override
	public CpsChecklist getCpsChecklist(Long uidCpsCheckList) {

		CpsChecklist CpsChecklist = null;
		CpsChecklist = (CpsChecklist) sessionFactory.getCurrentSession().get(CpsChecklist.class, uidCpsCheckList);

		return CpsChecklist;
	}

	/**
	 * 
	 * Method Description: This Method will Save cps checklist item in to the
	 * table. Dam Name: CAUDE3D
	 * 
	 * @param cpsChecklistItem
	 * @return saveCpsChecklistItem @
	 */
	@Override
	public void saveOrUpdateCpsChecklist(CpsChecklist cpsChecklist) {

		sessionFactory.getCurrentSession().saveOrUpdate(cpsChecklist);
	}

	/**
	 * 
	 * Method Description: This Method will Save or update checklist item in the
	 * TODO table. Dam Name: CINV43D
	 * 
	 * @param toDo
	 * @return void @
	 */
	@Override
	public void saveOrUpdateToDO(Todo toDo) {

		toDo.setDtTodoCompleted(new Date());
		sessionFactory.getCurrentSession().update(sessionFactory.getCurrentSession().merge(toDo));
	}

	/**
	 * 
	 * Method Description: This Method will retrieve the EventId from Event
	 * table. Dam Name: CCMN45D
	 * 
	 * @param uidEvent
	 * @return Event @
	 */
	@Override
	public Event getEventById(Long uidEvent) {
		Event event = null;

		event = (Event) sessionFactory.getCurrentSession().get(Event.class, (uidEvent));
		if (TypeConvUtil.isNullOrEmpty(event)) {
			throw new DataNotFoundException("Event id not found");
		}
		return event;
	}

	/**
	 * 
	 * Method Description: This Method will retrieve the caseId from caps_case
	 * table. Dam Name: CAUDE4D
	 * 
	 * @param uidCapsCase
	 * @return CapsCase @
	 */
	@Override
	public CapsCase getCaseById(Long uidCapsCase) {
		CapsCase capsCase = null;
		try {

			capsCase = (CapsCase) sessionFactory.getCurrentSession().load(CapsCase.class, (uidCapsCase));
		} catch (ObjectNotFoundException e) {
			DataNotFoundException dataNotFoundException = new DataNotFoundException(" capse case not found");
			dataNotFoundException.initCause(e);
			throw dataNotFoundException;
		}
		return capsCase;
	}

	/**
	 * 
	 * Method Description: This Method will retrieve the caseId from caps_case
	 * table. Dam Name: CAUDE4D
	 * 
	 * @param uidCapsCase
	 * @return CapsCase @
	 */
	@Override
	public Stage getStageById(Long uidStage) {
		Stage stage = null;
		try {

			stage = (Stage) sessionFactory.getCurrentSession().load(Stage.class, (uidStage));
		} catch (ObjectNotFoundException e) {
			DataNotFoundException dataNotFoundException = new DataNotFoundException("stage not found");
			dataNotFoundException.initCause(e);
			throw dataNotFoundException;
		} catch (HibernateException e) {
			DataLayerException dataLayerException = new DataLayerException(e.toString());
			dataLayerException.initCause(e);
			throw dataLayerException;
		}

		return stage;
	}

	public String getGetContactInfoSql() {
		return getContactInfoSql;
	}

	public void setGetContactInfoSql(String getContactInfoSql) {
		this.getContactInfoSql = getContactInfoSql;
	}

	/**
	 * Retrieves the parental child safety placement details from the
	 * CHILD_SAFETY_PLCMT, PERSON, STAGE tables. Service Name: PCSPEjb
	 * 
	 * @param pcspReq
	 * @return List<PCSPDto> @
	 */
	@SuppressWarnings("unchecked")
	public List<PcspDto> getPcspList(PcspReq pcspReq) {

		List<PcspDto> pcspList = new ArrayList<>();
		StringBuilder sql = new StringBuilder(getPcsp);
		if (ServiceConstants.CSTAGES_INV.equalsIgnoreCase(pcspReq.getCdStage())) {
			sql.append("AND STG.CD_STAGE IN ( 'INV', 'A-R')");
		} else if (ServiceConstants.CSTAGES_AR.equalsIgnoreCase(pcspReq.getCdStage())) {
			sql.append("AND STG.CD_STAGE = 'A-R'");
		}
		sql.append("ORDER BY CS.DT_END DESC, P1.NM_PERSON_FULL ASC");

		String pcspSql = sql.toString();
		try {
			pcspList = (List<PcspDto>) sessionFactory.getCurrentSession().createSQLQuery(pcspSql)
					.addScalar("idChildSafetyPlcmt", StandardBasicTypes.LONG)
					.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
					.addScalar("idCaregvrPerson", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
					.addScalar("cdEndRsn").addScalar("pcspComments").addScalar("cdStatus").addScalar("nmPersonFull")
					.addScalar("nmCaregvrFull").addScalar("cdStage")
					.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
					.addScalar("dtEnd", StandardBasicTypes.TIMESTAMP).addScalar("dtStart", StandardBasicTypes.TIMESTAMP)
					.addScalar("indCaregvrManual", StandardBasicTypes.STRING)
					.setParameter("idCase", pcspReq.getCaseId())
					.setResultTransformer(Transformers.aliasToBean(PcspDto.class)).list();

		} catch (DataNotFoundException ex) {
			DataNotFoundException dataNotFoundException = new DataNotFoundException(ex.toString());
			dataNotFoundException.initCause(ex);
			throw dataNotFoundException;
		}

		return pcspList;
	}

	/**
	 * 
	 * Method Description: This Method will Save or update checklist item in the
	 * TODO table. Dam Name: CINV43D
	 * 
	 * @param toDo
	 * @return void @
	 */
	@Override
	public void updateOrSaveToDO(TodoDto toDoDto) {
		Event event = (Event) sessionFactory.getCurrentSession().load(Event.class, (toDoDto.getIdTodoEvent()));
		for (Todo toDoEvent : event.getTodos()) {
			toDoEvent.setDtTodoCompleted(new Date());
			sessionFactory.getCurrentSession().saveOrUpdate(toDoEvent);
		}

	}
}
