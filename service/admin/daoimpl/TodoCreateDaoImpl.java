package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.common.domain.TodoInfo;
import us.tx.state.dfps.service.admin.dao.TodoCreateDao;
import us.tx.state.dfps.service.admin.dto.EventStageDiDto;
import us.tx.state.dfps.service.admin.dto.EventStageDoDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateLikeExpression;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.lookup.dao.LookupDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION
 *
 * Class Description: Ccmn43dDaoImpl
 *
 * Aug 7, 2017- 9:37:39 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class TodoCreateDaoImpl implements TodoCreateDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private LookupDao lookupDao;

	private static final Logger log = Logger.getLogger(TodoCreateDaoImpl.class);

	/**
	 * This method performs insert , update and delete operations on ToDo table
	 * 
	 * @param pInputDataRec
	 * @param pOutputDataRec
	 * @return Ccmn43doDto @
	 */
	@Override
	public EventStageDoDto cudTODO(EventStageDiDto pInputDataRec) {
		log.debug("Entering method cudTODO in Ccmn43dDaoImpl");
		boolean isSuccess = false;
		Long todoID = ServiceConstants.ZERO_VAL;
		EventStageDoDto ccmn43doDto = null;
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCReqFuncCd())) {
			switch (pInputDataRec.getCReqFuncCd()) {
			case ServiceConstants.REQ_FUNC_CD_ADD:
			case ServiceConstants.REQ_FUNC_CD_ADD_KIN:
				todoID = insertTodoDtls(pInputDataRec);
				break;
			case ServiceConstants.REQ_FUNC_CD_UPDATE:
				isSuccess = updateTodoDtls(pInputDataRec);
				break;
			case ServiceConstants.REQ_FUNC_CD_DELETE:
				isSuccess = deleteTodoDtls(pInputDataRec);
			}
			boolean isTodoIdValid = !TypeConvUtil.isNullOrEmpty(todoID);
			if (isSuccess || isTodoIdValid) {
				ccmn43doDto = new EventStageDoDto();
				if (isTodoIdValid) {
					ccmn43doDto.setIdTodo(todoID);
				} else {
					throw new DataNotFoundException(
							messageSource.getMessage("Ccmn43dDaoImpl.resource.not.found.idTodo", null, Locale.US));
				}

			}
		}
		log.debug("Exiting method cudTODO in Ccmn43dDaoImpl");
		return ccmn43doDto;
	}

	/**
	 * Method Name: insertTodoDtls
	 * 
	 * Method Description: This method inserts todo details
	 * 
	 * @param pInputDataRec
	 * @return Long
	 */
	private Long insertTodoDtls(EventStageDiDto pInputDataRec) {
		log.debug("Entering method insertTodoDtls in Ccmn43dDaoImpl");
		Todo todo = new Todo();
		todo = setTodoDtls(pInputDataRec, todo);
		sessionFactory.getCurrentSession().save(todo);
		long todoID = ServiceConstants.Zero;
		todoID = todo.getIdTodo();
		log.debug("Exiting method insertRsrcLink in Ccmn43dDaoImpl");
		return todoID;

	}

	/**
	 * Method Name: updateTodoDtls
	 * 
	 * Method Description: this method updates existing todo details
	 * 
	 * @param pInputDataRec
	 * @param isSuccess
	 * @return boolean
	 */
	private boolean updateTodoDtls(EventStageDiDto pInputDataRec) {
		log.debug("Entering method updateTodoDtls in Ccmn43dDaoImpl");
		boolean isSuccess = false;
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getLdIdTodo())
				&& !TypeConvUtil.isNullOrEmpty(pInputDataRec.getTsLastUpdate())) {
			Todo todoDtls = getTodoDtls(pInputDataRec);
			todoDtls = setTodoDtls(pInputDataRec, todoDtls);
			sessionFactory.getCurrentSession().saveOrUpdate(todoDtls);
			isSuccess = true;
		}
		log.debug("Exiting method updateTodoDtls in Ccmn43dDaoImpl");

		return isSuccess;
	}

	/**
	 * Method Name: deleteTodoDtls
	 * 
	 * Method Description: This method deletes todo details
	 * 
	 * @param pInputDataRec
	 * @param isSuccess
	 * @return boolean
	 */
	private boolean deleteTodoDtls(EventStageDiDto pInputDataRec) {
		log.debug("Entering method deleteTodoDtls in Ccmn43dDaoImpl");
		boolean isSuccess = false;
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getLdIdTodo())
				&& !TypeConvUtil.isNullOrEmpty(pInputDataRec.getTsLastUpdate())) {
			Todo todoDtls = getTodoDtls(pInputDataRec);
			todoDtls = setTodoDtls(pInputDataRec, todoDtls);
			sessionFactory.getCurrentSession().delete(todoDtls);
			isSuccess = true;
		}

		log.debug("Exiting method deleteTodoDtls in Ccmn43dDaoImpl");
		return isSuccess;
	}

	/**
	 * Method Name: getTodoDtls
	 * 
	 * Method Description: This method retrieves Todo record if present in
	 * database
	 *
	 * @param pInputDataRec
	 * @return Todo
	 */
	private Todo getTodoDtls(EventStageDiDto pInputDataRec) {
		Date minDate = pInputDataRec.getTsLastUpdate();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);
		criteria.add(Restrictions.eq("idTodo", pInputDataRec.getLdIdTodo()));
		if (ServiceConstants.UPDATE.equals(pInputDataRec.getCReqFuncCd())) {
			criteria.add(
					new DateLikeExpression("dtLastUpdate", TypeConvUtil.formatDate(pInputDataRec.getTsLastUpdate())));
		} else {
			criteria.add(Restrictions.le("dtLastUpdate", minDate));
		}

		Todo todoDtls = (Todo) criteria.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(todoDtls)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Ccmn43dDaoImpl.resource.not.found.idTodo", null, Locale.US));
		}
		return todoDtls;
	}

	/**
	 * Method Name: setTodoDtls
	 * 
	 * Method Description: This method maps Todo with pInputDataRec
	 * 
	 * @param pInputDataRec
	 * @param todo
	 * @return
	 */
	private Todo setTodoDtls(EventStageDiDto pInputDataRec, Todo todo) {
		log.debug("Entering method setTodoDtls in Ccmn43dDaoImpl");
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getTxtTodoLongDesc())) {
			todo.setTxtTodoDesc(pInputDataRec.getTxtTodoLongDesc());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getUlIdTodoPersCreator())) {
			Person personCreator = (Person) sessionFactory.getCurrentSession().get(Person.class,
					pInputDataRec.getUlIdTodoPersCreator());
			if (TypeConvUtil.isNullOrEmpty(personCreator)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Ccmn43dDaoImpl.resource.not.found.personCreator", null, Locale.US));
			}
			todo.setPersonByIdTodoPersCreator(personCreator);
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getSzCdTodoTask())) {
			todo.setCdTodoTask(pInputDataRec.getSzCdTodoTask());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getDtDtTodoDue())) {
			todo.setDtTodoDue(pInputDataRec.getDtDtTodoDue());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getUlIdTodoPersWorker())) {
			Person personWorker = (Person) sessionFactory.getCurrentSession().get(Person.class,
					pInputDataRec.getUlIdTodoPersWorker());
			if (TypeConvUtil.isNullOrEmpty(personWorker)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Ccmn43dDaoImpl.resource.not.found.personWorker", null, Locale.US));
			}
			todo.setPersonByIdTodoPersWorker(personWorker);
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getUlIdStage())) {
			Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, pInputDataRec.getUlIdStage());
			if (TypeConvUtil.isNullOrEmpty(stage)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Ccmn43dDaoImpl.resource.not.found.stage", null, Locale.US));
			}
			todo.setStage(stage);
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getUlIdTodoPersAssigned())) {
			Person personAssigned = (Person) sessionFactory.getCurrentSession().get(Person.class,
					pInputDataRec.getUlIdTodoPersAssigned());
			if (TypeConvUtil.isNullOrEmpty(personAssigned)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Ccmn43dDaoImpl.resource.not.found.personAssigned", null, Locale.US));
			}
			todo.setPersonByIdTodoPersAssigned(personAssigned);
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getSzTxtTodoDesc())) {
			todo.setTxtTodoDesc(pInputDataRec.getSzTxtTodoDesc());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getUlIdTodoInfo())) {
			TodoInfo todoInfo = (TodoInfo) sessionFactory.getCurrentSession().get(TodoInfo.class,
					pInputDataRec.getUlIdTodoInfo());
			if (TypeConvUtil.isNullOrEmpty(todoInfo)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Ccmn43dDaoImpl.resource.not.found.todoInfo", null, Locale.US));
			}
			todo.setTodoInfo(todoInfo);
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getSzCdTodoType())) {
			todo.setCdTodoType(pInputDataRec.getSzCdTodoType());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getDtDtTodoCreated())) {
			todo.setDtTodoCreated(pInputDataRec.getDtDtTodoCreated());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getDtDtTaskDue())) {
			todo.setDtTodoTaskDue(pInputDataRec.getDtDtTaskDue());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getUlIdCase())) {
			CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class,
					pInputDataRec.getUlIdCase());
			if (TypeConvUtil.isNullOrEmpty(capsCase)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Ccmn43dDaoImpl.resource.not.found.capsCase", null, Locale.US));
			}
			todo.setCapsCase(capsCase);
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getUlIdEvent())) {
			Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, pInputDataRec.getUlIdEvent());
			if (TypeConvUtil.isNullOrEmpty(event)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Ccmn43dDaoImpl.resource.not.found.event", null, Locale.US));
			}
			todo.setEvent(event);
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getDtDtTodoCompleted())) {
			todo.setDtTodoCompleted(pInputDataRec.getDtDtTodoCompleted());
		}
		todo.setDtLastUpdate(new Date());
		log.debug("Exiting method setTodoDtls in Ccmn43dDaoImpl");
		return todo;
	}

	/**
	 * 
	 * Method Name: audToDo Method Description:This method will call specific
	 * SQLs based in the Impact or MPS call
	 * 
	 * @param eventStageDiDto
	 * @return EventStageDoDto @
	 */
	@Override
	public EventStageDoDto audToDo(EventStageDiDto eventStageDiDto) {
		if (ServiceConstants.SERVER_IMPACT) {
			return audToDoImpact(eventStageDiDto);
		} else {
			return audToDoMobile(eventStageDiDto);
		}
	}

	/**
	 * Method Name: audToDoImpact Method Description:This method is called in
	 * MPS to perform AUD operation on Todo.
	 * 
	 * @param eventStageDiDto
	 * @return EventStageDoDto
	 */

	@Override
	public EventStageDoDto audToDoImpact(EventStageDiDto eventStageDiDto) {
		EventStageDoDto eventStageDoDto = new EventStageDoDto();

		Long idToDo = ServiceConstants.Zero_Value;
		if (ServiceConstants.REQ_FUNC_CD_ADD.equals(eventStageDiDto.getServiceInputDto().getCreqFuncCd())) {
			Todo todo = new Todo();
			Date dtDtSystemDate = new Date();
			todo.setCdTodoType(eventStageDiDto.getSzCdTodoType());
			todo.setCdTodoTask(eventStageDiDto.getSzCdTodoTask());
			todo.setDtTodoCompleted(eventStageDiDto.getDtDtTodoCompleted());
			todo.setDtTodoCreated(eventStageDiDto.getDtDtTodoCreated());
			todo.setDtTodoDue(eventStageDiDto.getDtDtTodoDue());
			todo.setDtTodoTaskDue(eventStageDiDto.getDtDtTaskDue());
			todo.setDtLastUpdate(dtDtSystemDate);
			if (!ObjectUtils.isEmpty(eventStageDiDto.getUlIdCase())) {
				CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class,
						eventStageDiDto.getUlIdCase());
				todo.setCapsCase(capsCase);
			}
			if (!ObjectUtils.isEmpty(eventStageDiDto.getUlIdTodoPersCreator())) {
				Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
						eventStageDiDto.getUlIdTodoPersCreator());
				todo.setPersonByIdTodoPersCreator(person);
			}
			Person person1 = (Person) sessionFactory.getCurrentSession().get(Person.class,
					eventStageDiDto.getUlIdTodoPersAssigned());
			todo.setPersonByIdTodoPersAssigned(person1);
			if (!ObjectUtils.isEmpty(eventStageDiDto.getUlIdTodoPersWorker())) {
				Person person2 = (Person) sessionFactory.getCurrentSession().get(Person.class,
						eventStageDiDto.getUlIdTodoPersWorker());
				todo.setPersonByIdTodoPersWorker(person2);
			}
			if (!ObjectUtils.isEmpty(eventStageDiDto.getUlIdStage())) {
				Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class,
						eventStageDiDto.getUlIdStage());
				todo.setStage(stage);
			}
			if (!ObjectUtils.isEmpty(eventStageDiDto.getUlIdEvent())) {
				Event event = (Event) sessionFactory.getCurrentSession().get(Event.class,
						eventStageDiDto.getUlIdEvent());
				todo.setEvent(event);
			}
			todo.setTxtTodoDesc(eventStageDiDto.getSzTxtTodoDesc() != null ? eventStageDiDto.getSzTxtTodoDesc()
					: ServiceConstants.EMPTY_STRING);
			todo.setTxtTodoLongDesc(eventStageDiDto.getTxtTodoLongDesc() != null ? eventStageDiDto.getTxtTodoLongDesc()
					: ServiceConstants.EMPTY_STRING);
			if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getUlIdTodoInfo())) {
				TodoInfo todoInfo = (TodoInfo) sessionFactory.getCurrentSession().get(TodoInfo.class,
						eventStageDiDto.getUlIdTodoInfo());
				todo.setTodoInfo(todoInfo);
			}
			idToDo = (Long) sessionFactory.getCurrentSession().save(todo);

		} else if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(eventStageDiDto.getServiceInputDto().getCreqFuncCd())) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);
			criteria.add(Restrictions.eq("idTodo", eventStageDiDto.getLdIdTodo()));
			criteria.add(Restrictions.le("dtLastUpdate", eventStageDiDto.getTsLastUpdate()));
			List<Todo> todoList = criteria.list();
			for (Todo todo : todoList) {
				sessionFactory.getCurrentSession().delete(todo);
			}
			if (todoList.size() == ServiceConstants.Zero) {
				throw new DataNotFoundException(lookupDao.getMessageByNumber("1403"));
			}
		} else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(eventStageDiDto.getServiceInputDto().getCreqFuncCd())) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);
			criteria.add(Restrictions.eq("idTodo", eventStageDiDto.getLdIdTodo()));
			criteria.add(Restrictions.eq("dtLastUpdate", eventStageDiDto.getTsLastUpdate()));
			List<Todo> todoList = criteria.list();
			for (Todo todo : todoList) {
				todo.setCdTodoType(eventStageDiDto.getSzCdTodoType());
				todo.setCdTodoTask(eventStageDiDto.getSzCdTodoTask());
				todo.setDtTodoCompleted(eventStageDiDto.getDtDtTodoCompleted());
				todo.setDtTodoCreated(eventStageDiDto.getDtDtTodoCreated());
				todo.setDtTodoDue(eventStageDiDto.getDtDtTodoDue());
				todo.setDtTodoTaskDue(eventStageDiDto.getDtDtTaskDue());
				CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class,
						eventStageDiDto.getUlIdCase());
				if (TypeConvUtil.isNullOrEmpty(capsCase)) {
					throw new DataNotFoundException(
							messageSource.getMessage("IdCase.not.found.attributes", null, Locale.US));
				}
				todo.setCapsCase(capsCase);
				Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
						eventStageDiDto.getUlIdTodoPersCreator());
				if (TypeConvUtil.isNullOrEmpty(person)) {
					throw new DataNotFoundException(
							messageSource.getMessage("TodoPersCreator.not.found.attributes", null, Locale.US));
				}
				todo.setPersonByIdTodoPersCreator(person);
				Person person1 = (Person) sessionFactory.getCurrentSession().get(Person.class,
						eventStageDiDto.getUlIdTodoPersAssigned());
				if (TypeConvUtil.isNullOrEmpty(person1)) {
					throw new DataNotFoundException(
							messageSource.getMessage("TodoPersAssigned.not.found.attributes", null, Locale.US));
				}
				todo.setPersonByIdTodoPersAssigned(person1);
				Person person2 = (Person) sessionFactory.getCurrentSession().get(Person.class,
						eventStageDiDto.getUlIdTodoPersWorker());
				if (TypeConvUtil.isNullOrEmpty(person2)) {
					throw new DataNotFoundException(
							messageSource.getMessage("IdTodoPersWorker.not.found.attributes", null, Locale.US));
				}
				todo.setPersonByIdTodoPersWorker(person);
				Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class,
						eventStageDiDto.getUlIdStage());
				if (TypeConvUtil.isNullOrEmpty(stage)) {
					throw new DataNotFoundException(
							messageSource.getMessage("IdStage.not.found.attributes", null, Locale.US));
				}
				todo.setStage(stage);
				todo.setTxtTodoDesc(eventStageDiDto.getSzTxtTodoDesc() != null ? eventStageDiDto.getSzTxtTodoDesc()
						: ServiceConstants.EMPTY_STRING);
				todo.setTxtTodoLongDesc(eventStageDiDto.getTxtTodoLongDesc() != null
						? eventStageDiDto.getTxtTodoLongDesc() : ServiceConstants.EMPTY_STRING);
				todo.setDtLastUpdate(eventStageDiDto.getTsLastUpdate());
				idToDo = (Long) sessionFactory.getCurrentSession().save(todo);
			}
			if (todoList.size() == ServiceConstants.Zero) {
				throw new DataNotFoundException(lookupDao.getMessageByNumber("1403"));
			}
		} else {
			throw new DataNotFoundException(lookupDao.getMessageByNumber("1403"));
		}

		eventStageDoDto.setIdTodo(idToDo);

		return eventStageDoDto;
	}

	/**
	 * 
	 * Method Name: audToDoMobile Method Description:This method is called in
	 * Impact to perform AUD operation on Todo.
	 * 
	 * @param eventStageDiDto
	 * @return EventStageDoDto @
	 */
	@Override
	public EventStageDoDto audToDoMobile(EventStageDiDto eventStageDiDto) {
		EventStageDoDto eventStageDoDto = new EventStageDoDto();
		if (ServiceConstants.REQ_FUNC_CD_ADD.equals(eventStageDiDto.getServiceInputDto().getCreqFuncCd())) {
			sessionFactory.getCurrentSession().save(getInsertSQL(eventStageDiDto));
		} else if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(eventStageDiDto.getServiceInputDto().getCreqFuncCd())) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);
			criteria.add(Restrictions.eq("idTodo", eventStageDiDto.getLdIdTodo()));
			List<Todo> todoList = criteria.list();
			for (Todo todo : todoList) {
				sessionFactory.getCurrentSession().delete(todo);
			}

		} else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(eventStageDiDto.getServiceInputDto().getCreqFuncCd())) {
			sessionFactory.getCurrentSession().save(getUpdateSQL(eventStageDiDto));
		} else {
			throw new DataNotFoundException(lookupDao.getMessageByNumber("13550"));
		}

		return eventStageDoDto;
	}

	/**
	 * Method Name:getInsertSQL Method Description:Returns the insert sql for
	 * mobile application
	 * 
	 * @param eventStageDiDto
	 * @return Todo @
	 */
	private Todo getInsertSQL(EventStageDiDto eventStageDiDto) {

		Todo todo = new Todo();

		if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getSzCdTodoType())
				&& !ServiceConstants.EMPTY_STRING.equals(eventStageDiDto.getSzCdTodoType())) {

			todo.setCdTodoType(eventStageDiDto.getSzCdTodoType());
		}

		if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getSzCdTodoTask())
				&& !ServiceConstants.EMPTY_STRING.equals(eventStageDiDto.getSzCdTodoTask())) {
			todo.setCdTodoTask(eventStageDiDto.getSzCdTodoTask());
		}

		if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getDtDtTodoCompleted())) {
			todo.setDtTodoCompleted(eventStageDiDto.getDtDtTodoCompleted());
		}

		if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getDtDtTodoCreated())) {
			todo.setDtTodoCreated(eventStageDiDto.getDtDtTodoCreated());
		}

		if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getDtDtTodoDue())) {
			todo.setDtTodoDue(eventStageDiDto.getDtDtTodoDue());
		}

		if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getDtDtTaskDue())) {
			todo.setDtTodoTaskDue(eventStageDiDto.getDtDtTaskDue());
		}

		if (ServiceConstants.Zero_Value != eventStageDiDto.getUlIdCase()) {
			CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class,
					eventStageDiDto.getUlIdCase());
			if (TypeConvUtil.isNullOrEmpty(capsCase)) {
				throw new DataNotFoundException(
						messageSource.getMessage("IdCase.not.found.attributes", null, Locale.US));
			}
			todo.setCapsCase(capsCase);
		}

		if (ServiceConstants.Zero_Value != eventStageDiDto.getUlIdTodoPersCreator()) {
			Person createdPerson = (Person) sessionFactory.getCurrentSession().get(Person.class,
					eventStageDiDto.getUlIdTodoPersCreator());
			if (TypeConvUtil.isNullOrEmpty(createdPerson)) {
				throw new DataNotFoundException(
						messageSource.getMessage("TodoPersCreator.not.found.attributes", null, Locale.US));
			}
			todo.setPersonByIdTodoPersCreator(createdPerson);
		}

		if (ServiceConstants.Zero_Value != eventStageDiDto.getUlIdTodoPersAssigned()) {
			Person assignedPerson = (Person) sessionFactory.getCurrentSession().get(Person.class,
					eventStageDiDto.getUlIdTodoPersAssigned());
			if (TypeConvUtil.isNullOrEmpty(assignedPerson)) {
				throw new DataNotFoundException(
						messageSource.getMessage("TodoPersAssigned.not.found.attributes", null, Locale.US));
			}
			todo.setPersonByIdTodoPersAssigned(assignedPerson);
		}

		if (ServiceConstants.Zero_Value != eventStageDiDto.getUlIdTodoPersWorker()) {
			Person workerPerson = (Person) sessionFactory.getCurrentSession().get(Person.class,
					eventStageDiDto.getUlIdTodoPersWorker());
			if (TypeConvUtil.isNullOrEmpty(workerPerson)) {
				throw new DataNotFoundException(
						messageSource.getMessage("TodoPersWorker.not.found.attributes", null, Locale.US));
			}
			todo.setPersonByIdTodoPersWorker(workerPerson);
		}

		if (ServiceConstants.Zero_Value != eventStageDiDto.getUlIdStage()) {
			Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, eventStageDiDto.getUlIdStage());
			if (TypeConvUtil.isNullOrEmpty(stage)) {
				throw new DataNotFoundException(messageSource.getMessage("IdStage.notfound", null, Locale.US));
			}
			todo.setStage(stage);
		}

		if (ServiceConstants.Zero_Value != eventStageDiDto.getUlIdEvent()) {
			Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, eventStageDiDto.getUlIdEvent());
			if (TypeConvUtil.isNullOrEmpty(event)) {
				throw new DataNotFoundException(
						messageSource.getMessage("IdStage.not.found.attributes", null, Locale.US));
			}
			todo.setEvent(event);
		}

		if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getSzTxtTodoDesc())
				&& !ServiceConstants.EMPTY_STRING.equals(eventStageDiDto.getSzTxtTodoDesc())) {
			todo.setTxtTodoDesc(eventStageDiDto.getSzTxtTodoDesc());
		}

		if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getTxtTodoLongDesc())
				&& !ServiceConstants.EMPTY_STRING.equals(eventStageDiDto.getTxtTodoLongDesc())) {
			todo.setTxtTodoLongDesc(eventStageDiDto.getTxtTodoLongDesc());
		}

		if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getCreatedBy())
				&& !ServiceConstants.EMPTY_STRING.equals(eventStageDiDto.getCreatedBy())) {
			todo.setNmTodoCreatorInit(eventStageDiDto.getCreatedBy());
		}

		if (ServiceConstants.Zero_Value != eventStageDiDto.getUlIdTodoInfo()) {
			TodoInfo todoInfo = (TodoInfo) sessionFactory.getCurrentSession().get(Event.class,
					eventStageDiDto.getUlIdTodoInfo());
			if (TypeConvUtil.isNullOrEmpty(todoInfo)) {
				throw new DataNotFoundException(
						messageSource.getMessage("TodoInfo.not.found.attributes", null, Locale.US));
			}
			todo.setTodoInfo(todoInfo);
		}

		return todo;
	}

	/**
	 * Method Name:getUpdateSQL Method Desc:Returns the update sql string for
	 * mobile application
	 * 
	 * @param eventStageDiDto
	 * @return Todo @
	 */
	private Todo getUpdateSQL(EventStageDiDto eventStageDiDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);
		criteria.add(Restrictions.eq("idTodo", eventStageDiDto.getLdIdTodo()));
		if (ServiceConstants.SERVER_IMPACT) {
			criteria.add(Restrictions.eq("dtLastUpdate", eventStageDiDto.getTsLastUpdate()));
		}
		Todo todo = (Todo) criteria.uniqueResult();

		if (ServiceConstants.SERVER_IMPACT) {
			todo.setDtLastUpdate(eventStageDiDto.getTsLastUpdate());
		}

		if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getSzCdTodoType())) {
			todo.setCdTodoType(eventStageDiDto.getSzCdTodoType());
		}

		if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getSzCdTodoTask())) {
			todo.setCdTodoTask(eventStageDiDto.getSzCdTodoTask());
		}

		if (!DateUtils.isNull(eventStageDiDto.getDtDtTodoCompleted())) {
			todo.setDtTodoCompleted(eventStageDiDto.getDtDtTodoCompleted());
		}

		if (!DateUtils.isNull(eventStageDiDto.getDtDtTodoDue())) {
			todo.setDtTodoDue(eventStageDiDto.getDtDtTodoDue());
			criteria.add(Restrictions.eq("dtTodoDue", eventStageDiDto.getDtDtTodoDue()));
		}

		if (!DateUtils.isNull(eventStageDiDto.getDtDtTaskDue())) {
			todo.setDtTodoTaskDue(eventStageDiDto.getDtDtTaskDue());
		}

		if (ServiceConstants.Zero_Value != eventStageDiDto.getUlIdCase()) {
			CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class,
					eventStageDiDto.getUlIdCase());
			if (TypeConvUtil.isNullOrEmpty(capsCase)) {
				throw new DataNotFoundException(messageSource.getMessage("IdCase.notfound", null, Locale.US));
			}
			todo.setCapsCase(capsCase);
		}

		if (ServiceConstants.Zero_Value != eventStageDiDto.getUlIdTodoPersCreator()) {
			Person createdPerson = (Person) sessionFactory.getCurrentSession().get(Person.class,
					eventStageDiDto.getUlIdTodoPersCreator());
			if (TypeConvUtil.isNullOrEmpty(createdPerson)) {
				throw new DataNotFoundException(messageSource.getMessage("TodoPersCreator.notfound", null, Locale.US));
			}
			todo.setPersonByIdTodoPersCreator(createdPerson);
		}

		if (ServiceConstants.Zero_Value != eventStageDiDto.getUlIdTodoPersWorker()) {
			Person workerPerson = (Person) sessionFactory.getCurrentSession().get(Person.class,
					eventStageDiDto.getUlIdTodoPersWorker());
			if (TypeConvUtil.isNullOrEmpty(workerPerson)) {
				throw new DataNotFoundException(messageSource.getMessage("TodoPersWorker.notfound", null, Locale.US));
			}
			todo.setPersonByIdTodoPersWorker(workerPerson);
		}

		if (ServiceConstants.Zero_Value != eventStageDiDto.getUlIdStage()) {
			Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, eventStageDiDto.getUlIdStage());
			if (TypeConvUtil.isNullOrEmpty(stage)) {
				throw new DataNotFoundException(messageSource.getMessage("IdStage.notfound", null, Locale.US));
			}
			todo.setStage(stage);
		}

		if (ServiceConstants.Zero_Value != eventStageDiDto.getUlIdEvent()) {
			Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, eventStageDiDto.getUlIdEvent());
			if (TypeConvUtil.isNullOrEmpty(event)) {
				throw new DataNotFoundException(messageSource.getMessage("IdStage.notfound", null, Locale.US));
			}
			todo.setEvent(event);

			if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getSzTxtTodoDesc())) {
				todo.setTxtTodoDesc(eventStageDiDto.getSzTxtTodoDesc());
			}

			if (!TypeConvUtil.isNullOrEmpty(eventStageDiDto.getTxtTodoLongDesc())) {
				todo.setTxtTodoLongDesc(eventStageDiDto.getTxtTodoLongDesc());
			}
		}
		return todo;
	}

	/**
	 * 
	 * Method Name: completeTodo Method Description:This will Complete the Todo
	 * by putting the dt completed.
	 * 
	 * @param eventStageDiDto
	 * @return Long @
	 */
	@Override
	public Long completeTodo(EventStageDiDto eventStageDiDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);
		criteria.add(Restrictions.eq("idTodo", eventStageDiDto.getLdIdTodo()));

		List<Todo> todoList = criteria.list();
		for (Todo todo : todoList) {
			todo.setDtTodoCompleted(eventStageDiDto.getDtDtTodoCompleted());
			sessionFactory.getCurrentSession().saveOrUpdate(todo);
		}
		if (todoList.size() == ServiceConstants.Zero) {
			throw new DataNotFoundException(lookupDao.getMessageByNumber("1403"));
		}
		return (long) criteria.list().size();
	}

}
