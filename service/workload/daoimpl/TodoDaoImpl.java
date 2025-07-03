package us.tx.state.dfps.service.workload.daoimpl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CaseTodoReq;
import us.tx.state.dfps.service.common.request.PriorityClosureSaveReq;
import us.tx.state.dfps.service.common.request.ToDoUtilityReq;
import us.tx.state.dfps.service.common.request.TodoReq;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataMismatchException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.exception.TodoDetailsNotFoundException;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.service.workload.dto.TodoInfoCommonDto;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN11S Class
 * Description: This Method extends BaseDao and implements TodoDao. This is used
 * to retrieve Todo details from database. Mar 23, 2017 - 4:50:30 PM
 * * *************** Change History ****************************************
 * 11/1/2019  kanakas artf129782: Licensing Investigation Conclusion
 */
@Repository
public class TodoDaoImpl implements TodoDao {

	@Autowired
	MessageSource messageSource;

	@Value("${TodoDaoImpl.getTodoList}")
	private String Todosql;

	@Value("${TodoDaoImpl.getTodoListByCase}")
	private String TodosqlCase;

	@Value("${TodoDaoImpl.getTodoListByAlert}")
	private String TodosqlAlert;

	@Value("${TodoDaoImpl.getTodoListByAlertAndCase}")
	private String TodosqlAlertCase;

	@Value("${TodoDaoImpl.getCaseTodoList}")
	private String getCaseTodoList;

	@Value("${Todo.getTodoDetails}")
	private String getTodoDetailsSql;

	@Value("${ToDoDaoImpl.getTodoBypersonId}")
	private String getTodoBypersonIdSql;

	@Value("${ToDoDaoImpl.getTodoByTodoStageIdTask}")
	private String getTodoByTodoStageIdTaskSql;

	@Value("${TododaoImpl.getTodoUpdateDel}")
	private String getTodoUpdateDelSql;

	@Value("${Todo.getTodoDetailsbyStageTask}")
	private String getTodoDetailsbyStageTaskSql;

	@Value("${Contact.getContactCount}")
	private String getContactCountSql;

	@Value("${TododaoImpl.getTodoInfoByTodoInfo}")
	private String getTodoInfoByTodoInfoSql;

	@Value("${TododaoImpl.getTodoByStageId}")
	private String getTodoByStageIdSql;

	@Value("${TododaoImpl.getTodoByEventId}")
	private String getTodoByEventIdSql;

	@Value("${TododaoImpl.getTodoIdForApproval}")
	private String getTodoIdForApprovalSql;

	@Value("${TododaoImpl.getPrintTaskExists}")
	private String getPrintTaskExistsSql;

	@Value("${TodoDaoImpl.selectToDosForEvent}")
	private String selectToDosForEventSql;

	@Value("${ToDoDaoImpl.selectToDosForStageSql}")
	private String selectToDosForStageSql;

	@Value("${ToDoDaoImpl.selectCclTaskRecord}")
	private String selectCclTaskRecordSql;

	@Value("${ToDoDaoImpl.sqlSelectSupervisorForStage}")
	private String sqlSelectSupervisorForStage;

	@Value("${TodoDaoImpl.isTodoExists}")
	private String isTodoExists;

	@Value("${ToDoDaoImpl.deleteToDosForStageSql}")
	private String deleteToDosForStageSql;

	@Value("${ToDoDaoImpl.getAlertPopupSql}")
	private String alertPopupSql;

	@Value("${ToDoDaoImpl.findSupervisorAlert}")
	private String findSupervisorAlertSql;

	@Value("${ToDoDaoImpl.findTodoCaseDueDtList}")
	private String findTodoCaseDueDtListSql;

	@Value("${ToDoDaoImpl.getTasksByStageIdTask}")
	private String tasksByStageIdTaskSql;

	@Value("${ToDoDaoImpl.getTasksByCaseAndtaskCode}")
	private String tasksByCaseAndtaskCodeSql;

	@Value("${ToDoDaoImpl.checkPersonHasGuardianshipApprovalByIdPerson}")
	private String findGuardianshipApproveSql;

	@Value("${ToDoDaoImpl.checkCaseClosureApprover}")
	private String checkCaseClosureApproverSql;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private CapsCaseDao capsCaseDao;

	@Autowired
	private StageDao stageDao;
	
	private static final Logger log = Logger.getLogger(TodoDaoImpl.class);

	public TodoDaoImpl() {

	}

	/**
	 * 
	 * Method Description: This Method will retrieve all the Todos for a certain
	 * person and a time period. Dam Name: CCMN17D
	 * 
	 * @param todoReq
	 * @return List<TodoDto>
	 */
	@SuppressWarnings("unchecked")
	public List<TodoDto> getTodoDetails(TodoReq todoReq) {
 			List<TodoDto> todoDtoList = new ArrayList<TodoDto>();
		String orderByColumn = "";
		if (null == todoReq.getOrderByColumn()) {
			todoReq.setOrderByColumn("");
		}
		switch (todoReq.getOrderByColumn()) {
		case "staffToDo_List_Column03":
			orderByColumn = "T.DT_TODO_DUE";
			break;
		case "staffToDo_List_Column04":
			orderByColumn = "S.NM_STAGE";
			break;
		case "staffToDo_List_Column05":
				orderByColumn = "T.ID_TODO_CASE";
				break;
		case "staffToDo_List_Column06":
			orderByColumn = "DECODE(T.NM_TODO_CREATOR_INIT,NULL,'SYSTEM',T.NM_TODO_CREATOR_INIT)";
			break;
		default:
			orderByColumn = "DECODE(i.cd_todo_info,'INV004',1), t.dt_todo_due,t.id_todo";
			break;
		}
		StringBuilder queryString = null;
		if (!ObjectUtils.isEmpty(todoReq.getIdCase()) && !ObjectUtils.isEmpty(todoReq.getToDoType())) {
			queryString = new StringBuilder(TodosqlAlertCase);
		}else if (!ObjectUtils.isEmpty(todoReq.getToDoType())){
			queryString = new StringBuilder(TodosqlAlert);
		}else if(!ObjectUtils.isEmpty(todoReq.getIdCase())){
			queryString = new StringBuilder(TodosqlCase);
		}else{
			queryString = new StringBuilder(Todosql);
		}
		if (!ObjectUtils.isEmpty(todoReq.getOrderByColumn())) {
			if (todoReq.isColumnDesc()) {
				orderByColumn = orderByColumn.concat(" DESC NULLS LAST");
			} else {
				orderByColumn = orderByColumn.concat(" ASC NULLS FIRST");
			}
		}
		queryString.append(ServiceConstants.SPACE);
		queryString.append(orderByColumn);
		Date beginDate = null;
		Calendar cal = Calendar.getInstance();
		if (null != todoReq.getFromDate()) {
			cal.setTime(todoReq.getFromDate());
			// cal.add(Calendar.DATE, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			beginDate = cal.getTime();
		}
		cal.setTime(todoReq.getToDate());
		// cal.add(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		Date toDate = cal.getTime();
		SQLQuery sqlQuery1 = null;
		if (!ObjectUtils.isEmpty(todoReq.getIdCase()) && !ObjectUtils.isEmpty(todoReq.getToDoType())) {
			sqlQuery1 = (SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(queryString.toString()).setParameter("id_Person", todoReq.getUlIdTodoPersAssigned())
					.setDate("begin", beginDate).setParameter("end", toDate).setParameter("cd_todo_type", todoReq.getToDoType())
					.setParameter("id_todo_case", todoReq.getIdCase());

		}else if (!ObjectUtils.isEmpty(todoReq.getToDoType())){
			sqlQuery1 = (SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(queryString.toString()).setParameter("id_Person", todoReq.getUlIdTodoPersAssigned())
					.setDate("begin", beginDate).setParameter("end", toDate).setParameter("cd_todo_type", todoReq.getToDoType());

		}else if(!ObjectUtils.isEmpty(todoReq.getIdCase())){
			sqlQuery1 = (SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(queryString.toString()).setParameter("id_Person", todoReq.getUlIdTodoPersAssigned())
					.setDate("begin", beginDate).setParameter("end", toDate).setParameter("id_todo_case", todoReq.getIdCase());

		}else{
			sqlQuery1 = (SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(queryString.toString()).setParameter("id_Person", todoReq.getUlIdTodoPersAssigned())
					.setDate("begin", beginDate).setParameter("end", toDate);

		}
		SQLQuery sQLQuery = (SQLQuery) sqlQuery1.addScalar("idTodo", StandardBasicTypes.LONG)
						.addScalar("dtTodoDue").addScalar("cdTodoTask").addScalar("todoDesc").addScalar("cdTodoType")
						.addScalar("dtTodoCreated").addScalar("idTodoStage", StandardBasicTypes.LONG)
						.addScalar("cdStageProgram").addScalar("idTodoCase", StandardBasicTypes.LONG)
						.addScalar("idTodoEvent", StandardBasicTypes.LONG).addScalar("cdTodoInfo")
						.addScalar("nmTodoCreatorInit").addScalar("nmStage").addScalar("cdStage")
						.addScalar("idTodoPersCreator", StandardBasicTypes.LONG)
						.addScalar("nmCase", StandardBasicTypes.STRING).addScalar("cdStageType")
						.addScalar("cdTaskEventType").addScalar("indStageClosure").addScalar("dtStageCreated")
						.addScalar("nmChild").addScalar("totalRecCount", StandardBasicTypes.LONG).addScalar("indAlertViewed")
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(TodoDto.class));
		todoDtoList = (List<TodoDto>) sQLQuery.list();

		int firstResult = 0;
		int endResults = 100;
		if (!ObjectUtils.isEmpty(todoReq.getTotalRecCount())) {
			if (todoReq.getPageNbr() == 0) {
				todoReq.setPageNbr(1);
			}
			firstResult = ((todoReq.getPageNbr() - 1) * todoReq.getPageSizeNbr());
			endResults = firstResult + todoReq.getPageSizeNbr();
		}

		int i = firstResult;
		List<TodoDto> assignWorkloadDtlsTempList = new ArrayList<>();
		if (!ObjectUtils.isEmpty(todoDtoList) && todoDtoList.size() > 250) {
			while (i < endResults && i < todoDtoList.size()) {
				assignWorkloadDtlsTempList.add(todoDtoList.get(i));
				i++;
			}
		} else {
			assignWorkloadDtlsTempList = todoDtoList;
		}

		log.info("TransactionId :" + todoReq.getTransactionId());
		return assignWorkloadDtlsTempList;
	}

	/**
	 * DAM Name: CCMN90D Method Description: This Method is used to retrieve the
	 * entire row from the _TODO table
	 * 
	 * @param ldIdTodo
	 * @return TodoDto @
	 */
	public TodoDto getTodoDtlsById(Long ldIdTodo) {
		TodoDto todoDto = new TodoDto();
		todoDto = (TodoDto) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getTodoDetailsSql)
				.setParameter("idTodo", ldIdTodo)).addScalar("idTodo", StandardBasicTypes.LONG)
						.addScalar("cdTodoType", StandardBasicTypes.STRING)
						.addScalar("cdTodoTask", StandardBasicTypes.STRING)
						.addScalar("dtTodoCompleted", StandardBasicTypes.TIMESTAMP)
						.addScalar("dtTodoCreated", StandardBasicTypes.TIMESTAMP)
						.addScalar("dtTodoDue", StandardBasicTypes.TIMESTAMP)
						.addScalar("dtTodoTaskDue", StandardBasicTypes.TIMESTAMP)
						.addScalar("idTodoCase", StandardBasicTypes.LONG)
						.addScalar("idTodoEvent", StandardBasicTypes.LONG)
						.addScalar("idTodoPersCreator", StandardBasicTypes.LONG)
						.addScalar("idTodoPersWorker", StandardBasicTypes.LONG)
						.addScalar("idTodoPersAssigned", StandardBasicTypes.LONG)
						.addScalar("idTodoStage", StandardBasicTypes.LONG)
						.addScalar("todoDesc", StandardBasicTypes.STRING)
						.addScalar("todoLongDesc", StandardBasicTypes.STRING)
						.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
						.setResultTransformer(Transformers.aliasToBean(TodoDto.class)).uniqueResult();
		return todoDto;
	}



	public Todo getTodoDetailById(Long idTodo) {
		return (Todo) sessionFactory.getCurrentSession().createCriteria(Todo.class)
				.add(Restrictions.eq("idTodo", idTodo)).uniqueResult();
	}

	/**
	 * 
	 * Method Description: This Method is used to retrieve todo List based on
	 * ID_Todo. Service Name: CCMN19S and CCMN35S DAM Name - CCMN43D
	 * 
	 * @param todoDto
	 * @param archIPStruct
	 * @return TodoDto
	 */
	public TodoDto todoAUD(TodoDto todoDto, ServiceReqHeaderDto ServiceReqHeaderDto) {
		TodoDto todoDtoId = new TodoDto();
		Todo todoEntity = new Todo();
		CapsCase capCase = new CapsCase();
		Person personCreator = new Person();
		Person personAssg = new Person();
		Person personWorker = new Person();
		Stage stage = new Stage();
		Event event = new Event();
		TodoInfo todoInfo = new TodoInfo();
		if (ServiceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_ADD)) {
			personAssg.setIdPerson(todoDto.getIdTodoPersAssigned());
			personCreator.setIdPerson(todoDto.getIdTodoPersCreator());
			personWorker.setIdPerson(todoDto.getIdTodoPersWorker());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(todoDto.getCdTodoType())))
				todoEntity.setCdTodoType(todoDto.getCdTodoType());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(todoDto.getCdTodoTask())))
				todoEntity.setCdTodoTask(todoDto.getCdTodoTask());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.passeDatTxt(todoDto.getDtTodoCompleted())))
				todoEntity.setDtTodoCompleted(todoDto.getDtTodoCompleted());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.passeDatTxt(todoDto.getDtTodoCreated())))
				todoEntity.setDtTodoCreated(todoDto.getDtTodoCreated());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.passeDatTxt(todoDto.getDtTodoDue())))
				todoEntity.setDtTodoDue(todoDto.getDtTodoDue());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.passeDatTxt(todoDto.getDtTodoTaskDue())))
				todoEntity.setDtTodoTaskDue(todoDto.getDtTodoTaskDue());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(todoDto.getIdTodoCase()))) {
				capCase.setIdCase(todoDto.getIdTodoCase());
				todoEntity.setCapsCase(capCase);
			}
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(todoDto.getIdTodoPersCreator())))
				todoEntity.setPersonByIdTodoPersCreator(personCreator);
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(todoDto.getIdTodoPersAssigned())))
				todoEntity.setPersonByIdTodoPersAssigned(personAssg);
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(todoDto.getIdTodoPersWorker())))
				todoEntity.setPersonByIdTodoPersWorker(personWorker);
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(todoDto.getIdTodoStage()))) {
				stage.setIdStage(todoDto.getIdTodoStage());
				todoEntity.setStage(stage);
			}
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(todoDto.getIdTodoEvent()))) {
				event = (Event) sessionFactory.getCurrentSession().createCriteria(Event.class)
						.add(Restrictions.eq("idEvent", todoDto.getIdTodoEvent())).uniqueResult();
				todoEntity.setEvent(event);
			}
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(todoDto.getTodoLongDesc())))
				todoEntity.setTxtTodoLongDesc(todoDto.getTodoLongDesc());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(todoDto.getTodoDesc())))
				todoEntity.setTxtTodoDesc(todoDto.getTodoDesc());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(todoDto.getIdTodoInfo()))) {
				todoInfo.setIdTodoInfo((todoDto.getIdTodoInfo()));
				todoEntity.setTodoInfo(todoInfo);
			}
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(todoDto.getIdTodo())))
				todoEntity.setIdTodo(todoDto.getIdTodo());
			todoEntity.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().save(todoEntity);
			todoDtoId.setIdTodo(todoEntity.getIdTodo());
		} else if (ServiceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_DELETE)) {
			//artf262865 : removed the date last update <= current date condition
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class)
					.add(Restrictions.eq("idTodo", todoDto.getIdTodo()));
			todoEntity = (Todo) criteria.uniqueResult();
			if (!TypeConvUtil.isNullOrEmpty(todoEntity)) {
				sessionFactory.getCurrentSession().delete(todoEntity);
			} else {
				throw new TodoDetailsNotFoundException(todoDto.getIdTodo());
			}
			todoDtoId.setIdTodo(todoEntity.getIdTodo());
		} else if (ServiceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_UPDATE)) {
			if (!TypeConvUtil.isNullOrEmpty(todoDto.getDtLastUpdate())) {
				Criteria cr = sessionFactory.getCurrentSession().createCriteria(Todo.class)
						.add(Restrictions.eq("idTodo", todoDto.getIdTodo()));
				todoEntity = (Todo) cr.uniqueResult();

				if (ObjectUtils.isEmpty(todoEntity)
						|| todoDto.getDtLastUpdate().compareTo(todoEntity.getDtLastUpdate()) != 0) {
					throw new DataLayerException("", Long.valueOf(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH), null);
				}

			} else {
				throw new DataMismatchException("Todo updated failed -" + "Dt Last Updated is null");
			}
			personAssg.setIdPerson(todoDto.getIdTodoPersAssigned());
			personCreator.setIdPerson(todoDto.getIdTodoPersCreator());
			personWorker.setIdPerson(todoDto.getIdTodoPersWorker());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(todoDto.getCdTodoType())))
				todoEntity.setCdTodoType(todoDto.getCdTodoType());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(todoDto.getCdTodoTask())))
				todoEntity.setCdTodoTask(todoDto.getCdTodoTask());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.passeDatTxt(todoDto.getDtTodoCompleted())))
				todoEntity.setDtTodoCompleted(todoDto.getDtTodoCompleted());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.passeDatTxt(todoDto.getDtTodoCreated())))
				todoEntity.setDtTodoCreated(todoDto.getDtTodoCreated());
			// if
			// (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.passeDatTxt(todoDto.getDtTodoDue())))
			todoEntity.setDtTodoDue(todoDto.getDtTodoDue());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.passeDatTxt(todoDto.getDtTodoTaskDue())))
				todoEntity.setDtTodoTaskDue(todoDto.getDtTodoTaskDue());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(todoDto.getIdTodoCase()))) {
				capCase.setIdCase(todoDto.getIdTodoCase());
				todoEntity.setCapsCase(capCase);
			}
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(todoDto.getIdTodoPersCreator())))
				todoEntity.setPersonByIdTodoPersCreator(personCreator);
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(todoDto.getIdTodoPersAssigned())))
				todoEntity.setPersonByIdTodoPersAssigned(personAssg);
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(todoDto.getIdTodoPersWorker())))
				todoEntity.setPersonByIdTodoPersWorker(personWorker);
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(todoDto.getIdTodoStage()))) {
				stage.setIdStage(todoDto.getIdTodoStage());
				todoEntity.setStage(stage);
			}
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(todoDto.getIdTodoEvent()))) {
				event.setIdEvent((todoDto.getIdTodoEvent()));
				todoEntity.setEvent(event);
			}
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(todoDto.getTodoDesc())))
				todoEntity.setTxtTodoDesc(todoDto.getTodoDesc());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(todoDto.getTodoLongDesc())))
				todoEntity.setTxtTodoLongDesc(todoDto.getTodoLongDesc());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(todoDto.getIdTodo())))
				todoEntity.setIdTodo(todoDto.getIdTodo());
			todoEntity.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(todoEntity));
			todoDtoId.setIdTodo(todoEntity.getIdTodo());
		}
		return todoDtoId;
	}

	@Override
	public String todoDeleteById(List<Long> ldIdTodo) {
		Todo todoEntity = new Todo();
		String rtnMsg = "";
		Long idTodo = null;
		for (int i = 0; i < ldIdTodo.size(); i++) {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(Todo.class)
					.add(Restrictions.eq("idTodo", ldIdTodo.get(i)));
			todoEntity = (Todo) cr.uniqueResult();
			idTodo = ldIdTodo.get(i);
			if (!TypeConvUtil.isNullOrEmpty(todoEntity)) {
				sessionFactory.getCurrentSession().delete(todoEntity);
				rtnMsg = ServiceConstants.SUCCESS;
				
			} else {
				throw new TodoDetailsNotFoundException(ldIdTodo.get(i));
			}
		}
		return rtnMsg;
	}

	@Override
	public String deleteRCIAlerts(long stageId) {
		Todo todoEntity = new Todo();
		String rtnMsg = "";
		List<Todo> todoList = sessionFactory.getCurrentSession().createCriteria(Todo.class)
				.add(Restrictions.eq("stage.idStage", stageId))
				.add(Restrictions.eq("cdTodoTask", ServiceConstants.RCL_ALERT_TASK_CODE))
				.add(Restrictions.eq("cdTodoType", ServiceConstants.ALERT_TODO)).list();

		if (!TypeConvUtil.isNullOrEmpty(todoEntity)) {
			for (Todo todoDetail : todoList) {
				sessionFactory.getCurrentSession().delete(todoDetail);
			}
			rtnMsg = ServiceConstants.SUCCESS;
		} else {
			throw new TodoDetailsNotFoundException(todoEntity.getIdTodo());
		}
		return rtnMsg;
	}


	/**
	 * 
	 * Method Description: The purpose of this dam (ccmn42dQUERYdam) is to retrieve
	 * records for display in the ListBox of the 'Case To Do List'window
	 * (ccmn31w.win). The NM_PERSON_FULL is retrieved from the PERSON table for the
	 * ID_TODO_PERS_ASSIGNED. The initials (First, Middle, Last) of the person are
	 * extracted from the NM_PERSON_FULL's that are retrieved (via the initials42
	 * function). Associated with a particular case (ID_TODO_CASE), and with a
	 * DT_TODO_DUE that falls between the dates passed into this DAM (From Date:
	 * pInputDataRec->dtDtTodoDue[0], and TO Date: pInputDataRec->dtDtTodoDue[1]).
	 * and with ( (T.CD_TODO_TYPE = 'A') OR (T.DT_TODO_COMPLETED IS NULL) ). The
	 * retrieved records are ORDERed BY ASSIGNED, DATE, or CREATOR, based on the
	 * pInputDataRec->ArchInputStruct.cReqFuncCd value. Service Name: CCMN12S DAM:
	 * CCMN42D
	 * 
	 * @param caseTodoReq
	 * @return List<TodoDto> @
	 */
	@SuppressWarnings("unchecked")
	public List<TodoDto> getTodoList(CaseTodoReq caseTodoReq) {
		List<TodoDto> todoList = new ArrayList<TodoDto>();
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getCaseTodoList)
				.addScalar("idTodo", StandardBasicTypes.LONG).addScalar("idTodoCase", StandardBasicTypes.LONG)
				.addScalar("idTodoEvent", StandardBasicTypes.LONG).addScalar("idTodoStage", StandardBasicTypes.LONG)
				.addScalar("idTodoInfo", StandardBasicTypes.LONG)
				.addScalar("idTodoPersAssigned", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtTodoTaskDue", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtTodoCompleted", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtTodoCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtTodoDue", StandardBasicTypes.TIMESTAMP).addScalar("cdStage").addScalar("cdStageProgram")
				.addScalar("todoDesc").addScalar("nmTodoCreatorInit").addScalar("cdTodoType").addScalar("cdTodoTask")
				.addScalar("todoAssignedTo").addScalar("nmCase").addScalar("cdStageType").addScalar("cdTaskEventType")
				.addScalar("indStageClosure", StandardBasicTypes.STRING).addScalar("dtStageCreated")
				.setParameter("case_todo_id", caseTodoReq.getUlIdCase()).setDate("dateFrom", caseTodoReq.getDtFrom())
				.setDate("dateTo", caseTodoReq.getDtTo()).setResultTransformer(Transformers.aliasToBean(TodoDto.class));
		todoList = query.list();
		log.info("TransactionId :" + caseTodoReq.getTransactionId());
		return todoList;
	}

	/**
	 * 
	 * Method Description:getTodoBypersonId
	 * 
	 * @param personId
	 * @return @ @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getTodoBypersonId(Long personId) {
		List<Long> todos = new ArrayList<>();
		Query queryTodo = sessionFactory.getCurrentSession().createQuery(getTodoBypersonIdSql);
		queryTodo.setParameter("idPerson", personId);
		queryTodo.setMaxResults(100);
		todos = (List<Long>) queryTodo.list();
		if (TypeConvUtil.isNullOrEmpty(todos)) {
			throw new DataNotFoundException(messageSource.getMessage("todo.not.found.personId", null, Locale.US));
		}
		return todos;
	}

	/**
	 * 
	 * Method Description: This Method is used to perform update operation in Todo
	 * table based on inputs. Service name - CCMN25s DAM Name : CCMN99D
	 * 
	 * @param idPerson
	 * @param personByIdTodoPersAssigned
	 * @return String @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<TodoDto> getTodoUpdateDel(Long idPerson, Long personByIdTodoPersAssigned, Long stageId,
			Date todoCompleteddt) {
		List<Todo> todoEntity = new ArrayList<Todo>();
		Person personDtls = new Person();
		List<TodoDto> todoDtoList = new ArrayList<TodoDto>();
		TodoDto todoDto = new TodoDto();
		Stage uIdStage = new Stage();
		uIdStage.setIdStage(stageId);
		personDtls.setIdPerson(idPerson);
		if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(idPerson))) {
			Query queryTodo = sessionFactory.getCurrentSession().createQuery(getTodoUpdateDelSql);
			queryTodo.setParameter("idPerson", personByIdTodoPersAssigned);
			queryTodo.setParameter("stageid", stageId);
			todoEntity = (List<Todo>) queryTodo.list();
			for (int i = 0; i < todoEntity.size(); i++) {
				todoEntity.get(i).setPersonByIdTodoPersAssigned(personDtls);
				sessionFactory.getCurrentSession()
						.saveOrUpdate((sessionFactory.getCurrentSession().merge(todoEntity.get(i))));
				todoDto.setIdTodo(todoEntity.get(i).getIdTodo());
				todoDtoList.add(todoDto);
			}
		}
		return todoDtoList;
	}

	/**
	 * Method Description:This dam was written specifically for intake so that the
	 * service will be able to find the LE Notification todo. The only information
	 * available will be the cd task and the id stage
	 * 
	 * Service Name: CCMN88S, DAM Name: CINT58D
	 * 
	 * @param idTodoStage
	 * @param cdTodTask
	 * @return @ @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<TodoDto> getTodoByTodoStageIdTask(Long idTodoStage, String cdTodTask) {
		List<TodoDto> todoList = new ArrayList<TodoDto>();
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getTodoByTodoStageIdTaskSql)
				.addScalar("idTodo", StandardBasicTypes.LONG).addScalar("cdTodoType", StandardBasicTypes.STRING)
				.addScalar("dtTodoCompleted", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtTodoCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtTodoDue", StandardBasicTypes.TIMESTAMP).addScalar("cdTodoTask", StandardBasicTypes.STRING)
				.addScalar("dtTodoTaskDue", StandardBasicTypes.TIMESTAMP)
				.addScalar("idTodoCase", StandardBasicTypes.LONG)
				.addScalar("idTodoPersCreator", StandardBasicTypes.LONG)
				.addScalar("idTodoPersAssigned", StandardBasicTypes.LONG)
				.addScalar("idTodoPersWorker", StandardBasicTypes.LONG)
				.addScalar("idTodoStage", StandardBasicTypes.LONG).addScalar("idTodoEvent", StandardBasicTypes.LONG)
				.addScalar("todoDesc", StandardBasicTypes.STRING).addScalar("todoLongDesc", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP).setParameter("idSearch", idTodoStage)
				.setParameter("searchTask", cdTodTask).setResultTransformer(Transformers.aliasToBean(TodoDto.class));
		todoList = query.list();
		return todoList;
	}

	/**
	 * This DAM will perform a full row retrieval from the TODO_INFO table.
	 * 
	 * Service Name: CSUB40U, DAM Name: CSES08D
	 * 
	 * @param cdTodoInfo
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public TodoInfoCommonDto getTodoInfoByTodoInfo(String cdTodoInfo) {
		TodoInfoCommonDto todoInfoCommonDto = new TodoInfoCommonDto();
		Query queryTodo = sessionFactory.getCurrentSession().createSQLQuery(getTodoInfoByTodoInfoSql)
				.addScalar("idTodoInfo", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdTodoInfo", StandardBasicTypes.STRING)
				.addScalar("cdTodoInfoPersAssigned", StandardBasicTypes.STRING)
				.addScalar("cdTodoInfoTask", StandardBasicTypes.STRING)
				.addScalar("cdTodoInfoType", StandardBasicTypes.STRING)
				.addScalar("todoInfoDueDD", StandardBasicTypes.STRING)
				.addScalar("todoInfoDueMM", StandardBasicTypes.STRING)
				.addScalar("todoInfoDueYY", StandardBasicTypes.STRING)
				.addScalar("todoInfoTaskDueDD", StandardBasicTypes.STRING)
				.addScalar("todoInfoTaskDueMM", StandardBasicTypes.STRING)
				.addScalar("todoInfoTaskDueYY", StandardBasicTypes.STRING)
				.addScalar("todoInfoDesc", StandardBasicTypes.STRING)
				.addScalar("todoInfoLongDesc", StandardBasicTypes.STRING)
				.addScalar("indTodoInfoEnabled", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(TodoInfoCommonDto.class));
		queryTodo.setParameter("cdTodoInfo", cdTodoInfo);
		queryTodo.setMaxResults(1);
		queryTodo.setFirstResult(0);
		List<TodoInfoCommonDto> list = (ArrayList<TodoInfoCommonDto>) queryTodo.list();
		if (null != list && list.size() > 0) {
			todoInfoCommonDto = list.get(0);
		}
		return todoInfoCommonDto;
	}

	/**
	 * 
	 * Method Description: Method for Priority Closure Save/Close. This retrieves
	 * Count of LE notifications.
	 * 
	 * 
	 * @param PriorityClosureSaveReq
	 *            Todo
	 * @return @ Tuxedo Service Name: CINT21S
	 */
	@Override
	public Long getTodobyStageTask(PriorityClosureSaveReq priorityClosureSaveReq) {
		Long count = 0l;
		// LeCount
		count = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getTodoDetailsbyStageTaskSql)
				.setParameter("idStage", priorityClosureSaveReq.getPriorityClosureDto().getIdStage()))
						.addScalar("LeCount", StandardBasicTypes.LONG).uniqueResult();
		return count;
	}

	/**
	 * 
	 * Method Description: Method for Priority Closure Save/Close. DT_TODO_COMPLETED etedThis Updates Todo
	 * table.
	 * 
	 * 
	 * @param PriorityClosureSaveReq
	 *            Todo
	 * @return @ Tuxedo Service Name: CINT21S
	 */
	public void updateTodoDate(PriorityClosureSaveReq priorityClosureSaveReq, Todo todo) {
		sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(todo));
	}

	/**
	 * 
	 * Method Description: Method for get the Contact Count for Priority Closure.
	 * 
	 * 
	 * @param PriorityClosureSaveReq
	 * @return Long count @ Tuxedo Service Name: CINT21S
	 */
	public Long getContactCount(PriorityClosureSaveReq priorityClosureSaveReq) {
		Long count = ServiceConstants.ZERO_VAL;
		count = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getContactCountSql)
				.setParameter("idStage", priorityClosureSaveReq.getPriorityClosureDto().getIdStage()))
						.addScalar("contactCount", StandardBasicTypes.LONG).uniqueResult();
		return count;
	}

	/**
	 * This dam is used by the stage progression CloseOpenStage function (ccmn03u)
	 * to retrieve two ID's (ID_EVENT and ID_TODO) and three timestamps (one from
	 * the EVENT table (ID_EVENT), one from the CONTACT table (ID_EVENT), and one
	 * from the _TODO table (ID_TODO)) for a particular ID_STAGE (the old stage).
	 * There could be none, or sets of these five numbers returned (two ID's and
	 * three timestamps). If there are multiple sets, it is for CD_CONTACT_TYPE =
	 * 'CMST', 'CS45' or 'CS60'.
	 * 
	 * Service Name : CCMN03U, DAM Name : CCMNH0D
	 * 
	 * @param idStage
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<TodoDto> getTodoByStageId(Long idStage) {
		List<TodoDto> todoDtoList = new ArrayList<>();
		Query queryStage = sessionFactory.getCurrentSession().createSQLQuery(getTodoByStageIdSql)
				.addScalar("idTodo", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idTodoEvent", StandardBasicTypes.LONG)
				.addScalar("dtContactLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtEventLastUpdate", StandardBasicTypes.TIMESTAMP)
				.setResultTransformer(Transformers.aliasToBean(TodoDto.class));
		queryStage.setParameter("idStage", idStage);
		todoDtoList = queryStage.list();
		return todoDtoList;
	}

	/**
	 * 
	 * Method Description: to fetch the Todo list for an event .CCMN42D
	 * 
	 * @param idEvent
	 * @return List<TodoDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<TodoDto> fetchToDoListForEvent(Long idEvent) {
		List<TodoDto> todoDtoList = new ArrayList<>();
		Query queryStage = sessionFactory.getCurrentSession().createSQLQuery(getTodoByEventIdSql)
				.addScalar("idTodo", StandardBasicTypes.LONG).addScalar("cdTodoType", StandardBasicTypes.STRING)
				.addScalar("cdTodoTask", StandardBasicTypes.STRING)
				.addScalar("dtTodoCompleted", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtTodoCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtTodoDue", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtTodoTaskDue", StandardBasicTypes.TIMESTAMP)
				.addScalar("idTodoInfo", StandardBasicTypes.LONG)
				.addScalar("idTodoPersAssigned", StandardBasicTypes.LONG)
				.addScalar("todoDesc", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.setResultTransformer(Transformers.aliasToBean(TodoDto.class));
		queryStage.setParameter("idEvent", idEvent);
		todoDtoList = queryStage.list();
		return todoDtoList;
	}

	/**
	 * Method Description: This service will returns a the event and todo id's for
	 * an approval given an event from the stage
	 * 
	 * @param ToDoUtilityReq
	 * @return ToDoUtilityRes @
	 */
	@Override
	public TodoDto getToDoIdForApproval(ToDoUtilityReq toDoUtilityReq) {
		TodoDto todoDto = new TodoDto();
		Query queryStage = sessionFactory.getCurrentSession().createSQLQuery(getTodoIdForApprovalSql)
				.addScalar("idTodo", StandardBasicTypes.LONG).addScalar("idTodoPersAssigned", StandardBasicTypes.LONG)
				.addScalar("idTodoCase", StandardBasicTypes.LONG).addScalar("nmCase", StandardBasicTypes.STRING)
				.addScalar("idTodoStage", StandardBasicTypes.LONG).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("cdStage", StandardBasicTypes.STRING).addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("cdStageType", StandardBasicTypes.STRING).addScalar("idTodoEvent", StandardBasicTypes.LONG)
				.addScalar("eventStatus", StandardBasicTypes.STRING).addScalar("cdTodoTask", StandardBasicTypes.STRING)
				.addScalar("cdTaskEventType", StandardBasicTypes.STRING)
				.addScalar("indStageClosure", StandardBasicTypes.STRING)
				.addScalar("eventDetailUrl", StandardBasicTypes.STRING).addScalar("todoDesc", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(TodoDto.class));
		queryStage.setParameter("idToDo", toDoUtilityReq.getIdToDo());
		todoDto = (TodoDto) queryStage.uniqueResult();
		return todoDto;
	}

	/**
	 * Delete all todos for the caseid passed
	 * 
	 * @param caseID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String toDoDeleteByCaseID(long caseID) {
		String returnMsg = "";
		CapsCase capsCase = capsCaseDao.getCapsCaseEntityById(caseID);
		List<Todo> todoList = (List<Todo>) sessionFactory.getCurrentSession().createCriteria(Todo.class)
				.add(Restrictions.eq("capsCase", capsCase)).list();
		for (Todo todoDetail : todoList) {
			sessionFactory.getCurrentSession().delete(todoDetail);
		}
		return returnMsg;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public CommonBooleanRes getPrintTaskExistsSql(Long idCase, Long idStage, Long idEvent) {
		CommonBooleanRes resp = new CommonBooleanRes();
		Long count = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPrintTaskExistsSql)
				.setParameter("idCase", idCase).setParameter("idStage", idStage).setParameter("idEvent", idEvent)
				.setParameter("cdTask", ServiceConstants.CD_TASK_PRINT_CPS)
				.setParameter("cdTodoType", ServiceConstants.TASK_TODO)).addScalar("count", StandardBasicTypes.LONG)
						.uniqueResult();
		if (count > 0) {
			resp.setExists(Boolean.TRUE);
		} else {
			resp.setExists(Boolean.FALSE);
		}
		return resp;
	}

	/**
	 * Method Name: selectToDosForStage Method Description: This function retrieves
	 * Open Tasks / Alerts of the given Type for the given Stage. If cdTodoInfoList
	 * is null, returns all Todos/Alerts for the Stage. EJB Name : ToDoBean.java
	 * 
	 * @param idInvStage
	 * @param cdTodoInfoList
	 * @return List<TodoDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<TodoDto> selectToDosForStage(Long idInvStage, List<String> cdTodoInfoList) {
		StringBuilder selectToDosForStageSqlBuilder = new StringBuilder(selectToDosForStageSql);
		if (!ObjectUtils.isEmpty(cdTodoInfoList)) {
			selectToDosForStageSqlBuilder.append(" AND tdi.cd_todo_info IN (");
			for (String cdTodoInfo : cdTodoInfoList) {
				selectToDosForStageSqlBuilder.append("\'" + cdTodoInfo + "\',");
			}
			selectToDosForStageSqlBuilder.deleteCharAt(selectToDosForStageSqlBuilder.length() - 1).append(')');
		}

		return (List<TodoDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(selectToDosForStageSqlBuilder.toString())
				.setLong(ServiceConstants.ID_TODO_STAGE, idInvStage))
						.addScalar(ServiceConstants.CD_TODO_INFO, StandardBasicTypes.STRING)
						.addScalar(ServiceConstants.IDTODO, StandardBasicTypes.LONG)
						.addScalar(ServiceConstants.DTLASTUPDATE, StandardBasicTypes.DATE)
						.addScalar(ServiceConstants.IDTODO_PERS_ASSIGNED, StandardBasicTypes.LONG)
						.addScalar(ServiceConstants.ID_TODO_CASE, StandardBasicTypes.LONG)
						.addScalar(ServiceConstants.ID_TODO_EVENT, StandardBasicTypes.LONG)
						.addScalar(ServiceConstants.IDTODO_PERS_CREATOR, StandardBasicTypes.LONG)
						.addScalar(ServiceConstants.ID_TODO_STAGE, StandardBasicTypes.LONG)
						.addScalar(ServiceConstants.IDTODO_PERS_WORKE, StandardBasicTypes.LONG)
						.addScalar(ServiceConstants.DT_TODO_DUE, StandardBasicTypes.DATE)
						.addScalar(ServiceConstants.CD_TODO_TASK_FIELD, StandardBasicTypes.STRING)
						.addScalar(ServiceConstants.TXT_TO_DO_DESC, StandardBasicTypes.STRING)
						.addScalar(ServiceConstants.CD_TODO_TYPE, StandardBasicTypes.STRING)
						.addScalar(ServiceConstants.TXT_TO_DO_LONG_DESC, StandardBasicTypes.STRING)
						.addScalar(ServiceConstants.DT_TODO_CREATED, StandardBasicTypes.DATE)
						.addScalar(ServiceConstants.DT_TODO_TASK_DUE, StandardBasicTypes.DATE)
						.addScalar(ServiceConstants.DT_TODO_COMPLETED, StandardBasicTypes.DATE)
						.addScalar(ServiceConstants.NM_TODO_CREATOR_INIT, StandardBasicTypes.STRING)
						.addScalar(ServiceConstants.ID_TODO_INFO, StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(TodoDto.class)).list();
	}

	/**
	 * Method Name: getSupervisorForStage Method Description: This function returns
	 * the Supervisor for the given Stage. EJB Name : ToDoBean.java
	 * 
	 * @param idStage
	 * @return long @
	 */
	public long getSupervisorForStage(long idStage) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlSelectSupervisorForStage);
		query.setLong("idStage", idStage);
		List<BigDecimal> idPersonBig = (List<BigDecimal>) query.list();
		if (CollectionUtils.isNotEmpty(idPersonBig)) {
			return idPersonBig.get(0).longValue();
		}

		return 0l;
	}

	/**
	 * Method Name: updateTodo Method Description: This method update _TODO Table.
	 * EJB Name : ToDoBean.java
	 * 
	 * @param todoLists
	 * @return long
	 */
	@Override
	public Long updateTodo(TodoDto todoDto) {
		Long result = ServiceConstants.ZERO;
		Todo todo = (Todo) sessionFactory.getCurrentSession().get(Todo.class, todoDto.getIdTodo());
		if (null != todo) {
			result = ServiceConstants.ONE_VAL;
		}
		if (todoDto.getIdTodoPersAssigned() != 0 && null != todo) {
			Person person = new Person();
			person.setIdPerson(todoDto.getIdTodoPersAssigned());
			todo.setPersonByIdTodoPersAssigned(person);
		}
		if (null != todo) {
			todo.setDtTodoDue(todoDto.getDtTodoDue());
			todo.setTxtTodoDesc(todoDto.getTodoDesc());
			todo.setTxtTodoLongDesc(todoDto.getTodoLongDesc());
			todo.setDtTodoCompleted(todoDto.getDtTodoCompleted());
			todo.setDtTodoTaskDue(todoDto.getDtTodoTaskDue());
		}
		sessionFactory.getCurrentSession().saveOrUpdate(todo);
		return result;
	}

	/**
	 * Method Name: getPersonFullName Method Description: This method returns full
	 * name for given person ID. EJB Name : ToDoBean.java
	 * 
	 * @param idPerson
	 * @return String
	 */
	public String getPersonFullName(Long idPerson) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Person.class);
		criteria.add(Restrictions.eq("idPerson", idPerson));
		ProjectionList reqcolumns = Projections.projectionList();
		reqcolumns.add(Projections.property("nmPersonFull"));
		criteria.setProjection(reqcolumns);
		return (String) criteria.uniqueResult();
	}

	/**
	 * Method Name: selectToDosForEvent Method Description:This function retrieves
	 * Open Tasks / Alerts of the given Type for the given Event.
	 * 
	 * @param idContactEvt
	 * @return List<TodoDto> @
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<TodoDto> selectToDosForEvent(Long idContactEvt) {
		List<TodoDto> todoDtoList = new ArrayList<>();
		Query queryStage = sessionFactory.getCurrentSession().createSQLQuery(selectToDosForEventSql)
				.addScalar("cdTodoInfo", StandardBasicTypes.STRING).addScalar("idTodo", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idTodoPersAssigned", StandardBasicTypes.LONG)
				.addScalar("idTodoCase", StandardBasicTypes.LONG).addScalar("idTodoEvent", StandardBasicTypes.LONG)
				.addScalar("idTodoPersCreator", StandardBasicTypes.LONG)
				.addScalar("idTodoStage", StandardBasicTypes.LONG)
				.addScalar("idTodoPersWorker", StandardBasicTypes.LONG).addScalar("dtTodoDue", StandardBasicTypes.DATE)
				.addScalar("cdTodoTask", StandardBasicTypes.STRING).addScalar("todoDesc", StandardBasicTypes.STRING)
				.addScalar("cdTodoType", StandardBasicTypes.STRING).addScalar("todoLongDesc", StandardBasicTypes.STRING)
				.addScalar("dtTodoCreated", StandardBasicTypes.DATE).addScalar("dtTodoTaskDue", StandardBasicTypes.DATE)
				.addScalar("dtTodoCompleted", StandardBasicTypes.DATE)
				.addScalar("nmTodoCreatorInit", StandardBasicTypes.STRING)
				.addScalar("idTodoInfo", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(TodoDto.class));
		queryStage.setParameter("idContactEvt", idContactEvt);
		todoDtoList = queryStage.list();
		if (TypeConvUtil.isNullOrEmpty(todoDtoList)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		return todoDtoList;
	}

	/**
	 * Method Name: updateToDosDao Method Description: Manual Stage Progression INT
	 * to A-R This function updates Todo Table. EJB Name : ToDoBean.java
	 * 
	 * @param toDosDto
	 * @return long[] @
	 */
	@Override
	public long[] updateToDosDao(List<TodoDto> toDosDto) {
		if ((toDosDto == null) || ((toDosDto != null) && (toDosDto.isEmpty()))) {
			return null;
		}
		long[] result = new long[toDosDto.size()];
		int i = 0;
		for (TodoDto todoDto : toDosDto) {
			Todo todo = (Todo) sessionFactory.getCurrentSession().get(Todo.class, todoDto.getIdTodo());

			Person person = new Person();
			person.setIdPerson(todoDto.getIdTodoPersAssigned());
			todo.setPersonByIdTodoPersAssigned(person);

			todo.setDtTodoDue(todoDto.getDtTodoDue());
			todo.setTxtTodoDesc(todoDto.getTodoDesc());
			todo.setTxtTodoLongDesc(todoDto.getTodoLongDesc());
			todo.setDtTodoCompleted(todoDto.getDtTodoCompleted());
			todo.setDtTodoTaskDue(todoDto.getDtTodoTaskDue());
			sessionFactory.getCurrentSession().saveOrUpdate(todo);
			result[i] = 1;
			i++;
		}

		return result;
	}

	/**
	 * Method Name: isTodoExists Method Description: This method checks if Active
	 * Todo Exists for the given user
	 * 
	 * @param idSvcAuthEvent
	 * @param cdTodoSaTwcTrnsUpdate
	 * @param userId
	 * @return Boolean @
	 */
	@Override
	public Boolean isTodoExists(Long idSvcAuthEvent, String cdTodoSaTwcTrnsUpdate, Long userId) {

		Boolean value = ServiceConstants.FALSEVAL;
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(isTodoExists)
				.addScalar("idTodo", StandardBasicTypes.LONG).setParameter("cdTodoInfo", cdTodoSaTwcTrnsUpdate)
				.setParameter("idTodoEvent", idSvcAuthEvent).setParameter("idTodoPersWorker", userId)
				.setResultTransformer(Transformers.aliasToBean(TodoDto.class));
		List<TodoDto> TodoDtoList = sQLQuery1.list();
		if (!TypeConvUtil.isNullOrEmpty(TodoDtoList)) {
			value = ServiceConstants.TRUEVAL;
		}
		return value;
	}

	/**
	 * Method Name: deleteTodosForStage Method Description: This function deletes
	 * Tasks / Alerts of the given Type for the given Stage. EJB Name :
	 * ToDoBean.java
	 * 
	 * @param idStage
	 * @param cdTodoInfoList
	 * @return
	 */
	@Override
	public long deleteTodosForStage(long idStage, List<String> todoInfoList) {
		long updateResult = ServiceConstants.ZERO_VAL;
		if (todoInfoList != null && !todoInfoList.isEmpty()) {
			StringBuilder deleteToDosForStageSqlBuilder = new StringBuilder(deleteToDosForStageSql);
			deleteToDosForStageSqlBuilder.append(" AND tdi.cd_todo_info IN (");
			for (String cdTodoInfo : todoInfoList) {
				deleteToDosForStageSqlBuilder.append("\'");
				deleteToDosForStageSqlBuilder.append(cdTodoInfo);
				deleteToDosForStageSqlBuilder.append("\',");
			}
			//Defect 11913 - use the string builder length
			deleteToDosForStageSqlBuilder.deleteCharAt(deleteToDosForStageSqlBuilder.length() - 1);
			deleteToDosForStageSqlBuilder.append(") )");
			Query query = sessionFactory.getCurrentSession().createSQLQuery(deleteToDosForStageSqlBuilder.toString());
			query.setLong(ServiceConstants.ID_TODO_STAGE, idStage);
			updateResult = query.executeUpdate();
		}
		return updateResult;
	}

	/**
	 * MethodName: getStage MethodDescription: get stage for given idStage
	 * 
	 * @param idStage
	 * @return
	 */
	@Override
	public Stage getStage(long idStage) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Stage.class);
		criteria.add(Restrictions.eq("idStage", idStage));
		Stage stage = (Stage) criteria.uniqueResult();

		return stage;
	}

	/**
	 * MethodName: isCPSSDMInvSafetyAssmt MethodDescription:
	 * 
	 * @param DtStageStart
	 * @return
	 */
	public boolean isCPSSDMInvSafetyAssmt(Date DtStageStart) {
		boolean isSDMSafetyAssmt = false;
		Date march15RelDt = null;
		try {
			march15RelDt = new SimpleDateFormat("mm/dd/yyyy").parse("03/29/2015");
		} catch (ParseException e) {
			throw new DataLayerException(e.getMessage());
		}
		if (null != DtStageStart && DateUtils.isAfter(DtStageStart, march15RelDt)) {
			isSDMSafetyAssmt = true;
		}
		return isSDMSafetyAssmt;
	}

	/**
	 * MethodName:getIncomingCallDateForIntake MethodDescription:
	 * 
	 * @param stageId
	 * @return
	 */
	public Date getIncomingCallDateForIntake(long stageId) {
		Date incomingCallDate = null;
		IncomingDetail incomingDetail = (IncomingDetail) sessionFactory.getCurrentSession().get(IncomingDetail.class,
				stageId);
		if (incomingDetail != null) {
			incomingCallDate = incomingDetail.getDtIncomingCall();
		}
		return incomingCallDate;
	}

	/**
	 * Method Name:deleteIncompleteTodos Method DEscription: This method deletes all
	 * the records from TO DO-table for a stage id that was not completed
	 * 
	 * @param idStage
	 *            the stage identifier
	 */
	@SuppressWarnings("unchecked")
	public long deleteIncompleteTodos(int idFromStage) {
		// criteria session created
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);

		// where condition in queries mapped
		criteria.add(Restrictions.eq("stage.idStage", (long) idFromStage));
		criteria.add(Restrictions.isNull(ServiceConstants.DT_TODO_COMPLETED));

		List<Todo> todo = (List<Todo>) criteria.list();

		// execute delete
		for (Todo i : todo)
			sessionFactory.getCurrentSession().delete(i);
		return criteria.list().size();
	}

	/**
	 * Method Name: deleteTodosForACase Method Description: This method deletes all
	 * the records from TO DO-table for a case. Usually called when a case is closed
	 * 
	 * @param idCase
	 * @return Long
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public Long deleteTodosForACase(Long idCase) throws DataNotFoundException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);

		criteria.add(Restrictions.eq("capsCase.idCase", idCase));

		List<Todo> list = criteria.list();

		for (Todo todo : list) {
			sessionFactory.getCurrentSession().delete(todo);
		}
		return (long) list.size();

	}

	/**
	 * Method isTodoCcL Method DEscription: This method gives the count of records
	 * with the task code 2560 and returns true if the count is zero else returns
	 * false.
	 * 
	 * @param idStage
	 *            return boolean
	 */
	@Override
	public Boolean isTodoCCL(Long idStage) {
		Boolean value = Boolean.FALSE;
		Long noOfRows = Long
				.valueOf(((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectCclTaskRecordSql)
						.setLong(ServiceConstants.ID_TODO_STAGE, idStage)).uniqueResult().toString());
		if (noOfRows == 0) {
			value = Boolean.TRUE;
		} else {
			value = Boolean.FALSE;
		}
		return value;

	}

	
	/**
	 * Method Name: deleteTodosForAEvent Method Description: This method deletes all
	 * the records from TO DO-table for a event. Usually called when approval event
	 * is deleted
	 * 
	 * @param idEvent
	 * @return Long
	 *
	 */
	public Long deleteTodosForAEvent(Long idEvent) {
		Todo todo = (Todo) sessionFactory.getCurrentSession().get(Todo.class, idEvent);
		if (!ObjectUtils.isEmpty(todo)) {
			sessionFactory.getCurrentSession().delete(todo);
			sessionFactory.getCurrentSession().flush();
		}
		return idEvent;
	}

	public Long deleteTodoListByEventId(Long idEvent) {
		List<Todo> todoList;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(Todo.class)
				.add(Restrictions.eq("event.idEvent", idEvent));
		todoList = (List<Todo>) cr.list();
		if (!TypeConvUtil.isNullOrEmpty(todoList)) {
			for (Todo todoDetail : todoList) {
				sessionFactory.getCurrentSession().delete(todoDetail);
			}
		}
		return idEvent;
	}

	public Long deleteTodoListByEventIdAndTaskCode(Long idEvent) {
		List<Todo> todoList;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(Todo.class)
				.add(Restrictions.eq("event.idEvent", idEvent))
				.add(Restrictions.eq("cdTodoTask", "9361"));
		todoList = (List<Todo>) cr.list();
		if (!TypeConvUtil.isNullOrEmpty(todoList)) {
			for (Todo todoDetail : todoList) {
				sessionFactory.getCurrentSession().delete(todoDetail);
			}
		}
		return idEvent;
	}



	public void deleteTodo(Long idToDo) {
		Todo todo = (Todo) sessionFactory.getCurrentSession().get(Todo.class, idToDo);
		if (!ObjectUtils.isEmpty(todo)) {
			sessionFactory.getCurrentSession().delete(todo);
			sessionFactory.getCurrentSession().flush();
		}
	}

	/**
	 * Method Name: updateToDoTaskDue Method Description:This method will update
	 * the todoTaskdue date for ScheduledCourtDate filed value in legal action
	 * screen. Added this method for warranty defect 11779
	 * 
	 * @param idTodo
	 * @param toDoTaskDue
	 */
	public void updateToDoTaskDue(Long idTodo, Date toDoTaskDue) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);
		criteria.add(Restrictions.eq("idTodo", idTodo));
		Todo todo = (Todo) criteria.uniqueResult();
		if (!ObjectUtils.isEmpty(todo)) {
			todo.setDtTodoTaskDue(toDoTaskDue);
			//Added the code to update the Todo Completed date for Warranty defect 11954
			Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, todo.getEvent().getIdEvent());
			if (!ObjectUtils.isEmpty(event) && !ObjectUtils.isEmpty(event.getCdEventStatus())
					&& CodesConstant.CEVTSTAT_COMP.equalsIgnoreCase(event.getCdEventStatus())) {
				todo.setDtTodoCompleted(event.getDtEventCreated());
			}
			sessionFactory.getCurrentSession().update(todo);
		}
	}
	
	@Override
	public void completeToDoStageClosure(Long idStage) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);
		criteria.add(Restrictions.eq("stage.idStage", idStage));
		criteria.add(Restrictions.isNull("dtTodoCompleted"));

		List<Todo> listToDo = criteria.list();

		if (!CollectionUtils.isEmpty(listToDo)) {
			listToDo.stream().forEach(t -> {
				t.setDtTodoCompleted(new Date());
				sessionFactory.getCurrentSession().update(t);
			});
		}
	}

	/**
	 *
	 * Method Description: This Method will retrieve all the Todos for RCI Alerts
	 *
	 * @param todoReq
	 * @return List<TodoDto>
	 */
	@SuppressWarnings("unchecked")
	public List<TodoDto> getRCIAlertTodoDetails(TodoReq todoReq) {
		List<TodoDto> todoDtoList = new ArrayList<TodoDto>();

		StringBuilder queryString = new StringBuilder(alertPopupSql);
		queryString.append(ServiceConstants.SPACE);

		SQLQuery sqlQuery1 = null;
		if (!ObjectUtils.isEmpty(todoReq.getIdCase())
				&& !ObjectUtils.isEmpty(todoReq.getUlIdTodoPersAssigned())
				&& !ObjectUtils.isEmpty(todoReq.getIdStage())
				&& !ObjectUtils.isEmpty(todoReq.getIdCase())
		) {
			sqlQuery1 = (SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(queryString.toString()).setParameter("id_Person", todoReq.getUlIdTodoPersAssigned())
					.setParameter("id_todo_stage", todoReq.getIdStage())
					.setParameter("id_todo_case", todoReq.getIdCase());

		}
		SQLQuery sQLQuery = (SQLQuery) sqlQuery1.addScalar("idTodo", StandardBasicTypes.LONG)
				.addScalar("dtTodoDue").addScalar("cdTodoTask").addScalar("todoDesc").addScalar("cdTodoType")
				.addScalar("dtTodoCreated").addScalar("idTodoStage", StandardBasicTypes.LONG)
				.addScalar("cdStageProgram").addScalar("idTodoCase", StandardBasicTypes.LONG)
				.addScalar("idTodoEvent", StandardBasicTypes.LONG).addScalar("cdTodoInfo")
				.addScalar("nmTodoCreatorInit").addScalar("nmStage").addScalar("cdStage")
				.addScalar("idTodoPersCreator", StandardBasicTypes.LONG)
				.addScalar("nmChild").addScalar("totalRecCount", StandardBasicTypes.LONG).addScalar("indAlertViewed")
				.addScalar("victimStage", StandardBasicTypes.LONG)
				.addScalar("invStage", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(TodoDto.class));
		todoDtoList = (List<TodoDto>) sQLQuery.list();

		int firstResult = 0;
		int endResults = 100;
		if (!ObjectUtils.isEmpty(todoReq.getTotalRecCount())) {
			if (todoReq.getPageNbr() == 0) {
				todoReq.setPageNbr(1);
			}
			firstResult = ((todoReq.getPageNbr() - 1) * todoReq.getPageSizeNbr());
			endResults = firstResult + todoReq.getPageSizeNbr();
		}

		int i = firstResult;
		List<TodoDto> assignWorkloadDtlsTempList = new ArrayList<>();
		if (!ObjectUtils.isEmpty(todoDtoList) && todoDtoList.size() > 250) {
			while (i < endResults && i < todoDtoList.size()) {
				assignWorkloadDtlsTempList.add(todoDtoList.get(i));
				i++;
			}
		} else {
			assignWorkloadDtlsTempList = todoDtoList;
		}

		log.info("TransactionId :" + todoReq.getTransactionId());
		return assignWorkloadDtlsTempList;
	}


	/**
	 * Method Name: updateToDosDao Method Description: rci update alert todos
	 *
	 * @param toDosDto
	 * @return long[] @
	 */
	@Override
	public long[] updateAlertToDos(List<TodoDto> toDosDto) {
		if (toDosDto == null || toDosDto.isEmpty()) {
			return null;
		}
		long[] result = new long[toDosDto.size()];
		int i = 0;
		for (TodoDto todoDto : toDosDto) {
			Todo todo = (Todo) sessionFactory.getCurrentSession().get(Todo.class, todoDto.getIdTodo());
			if (todoDto.getIdViewedPerson() != null) {
				Person person = new Person();
				person.setIdPerson(todoDto.getIdViewedPerson());
				todo.setIdViewedPerson(person);
				todo.setDtAlertViewed(new Date());
			}
			if (todoDto.getIndAlertViewed() != null) {
				todo.setIndAlertViewed(todoDto.getIndAlertViewed());
			}
			sessionFactory.getCurrentSession().saveOrUpdate(todo);
			result[i] = 1; // bizarre choice
			i++;
		}

		return result;
	}

	@Override
	public Long findSupervisorAlert(Long idWorkerAlert) {
		Long value = null;
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(findSupervisorAlertSql)
				.addScalar("idTodo", StandardBasicTypes.LONG)
				.setParameter("idTodo", idWorkerAlert)
				.setResultTransformer(Transformers.aliasToBean(TodoDto.class));
		List<TodoDto> todoDtoList = sQLQuery1.list();
		if (todoDtoList != null && todoDtoList.size() > 0) {
			value = todoDtoList.get(0).getIdTodo();
		}
		return value;
	}

	/**
	 * Set the extension day value for the to-do list passed. Note that to-do id will be null, logic is driven off
	 * stage id... We're just reusing the DTO. The query is unintentionally specific to INV stages, so reuse may be
	 * limited. Other stage types may not be able to calculate case start date.
	 *
	 * @param toDosDto
	 * @return void since we're setting values on the parameter objects
	 */
	@Override
	public void enhanceTodoListWithExtensions(List<TodoDto> toDosDto) {
		if (toDosDto != null && toDosDto.size() > 0) {
			// Find all extensions for all stages in parameter list. Note there will be multiple to-dos for the same stage.
			Set<Long> stageIdList = toDosDto.stream().map(TodoDto::getIdTodoStage).filter(Objects::nonNull).collect(Collectors.toSet());
			if (stageIdList != null && stageIdList.size() > 0) {
				SQLQuery query = ((SQLQuery) sessionFactory.openSession().createSQLQuery(findTodoCaseDueDtListSql)
						.addScalar("idTodoStage", StandardBasicTypes.LONG)  // adding a scalar is not normal, but hibernate fails with nulls without this because of the outer join
						.addScalar("dtCaseStart", StandardBasicTypes.DATE)
						.addScalar("extensionDays", StandardBasicTypes.INTEGER)
						.setParameterList("idStageList", stageIdList)
						.setResultTransformer(Transformers.aliasToBean(TodoDto.class)));
				List<TodoDto> todoDtoList = query.list();

				// Convert input list to a map by stage id for fast lookup.
				Map<Long, TodoDto> todoResultMap = todoDtoList.stream().collect(Collectors.toMap(TodoDto::getIdTodoStage, Function.identity()));

				// loop through the parameter DTOs and apply DB results.
				toDosDto.stream().forEach(currParamDto -> {
					TodoDto currResultTodo = todoResultMap.get(currParamDto.getIdTodoStage());
					if (currResultTodo != null) {
						currParamDto.setExtensionDays(currResultTodo.getExtensionDays());
						currParamDto.setDtCaseStart(currResultTodo.getDtCaseStart());
					}
				});
			}
		}
		return;
	}

	@Override
	public void updateToDoEvent(String cdTask, Long idStage, Long idEvent) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);
		criteria.add(Restrictions.eq("idTodoStage", idStage));
		criteria.add(Restrictions.eq("dtTodoCompleted", null));
		criteria.add(Restrictions.eq("cdTodoTask", cdTask));

		Todo todo = (Todo) criteria.uniqueResult();
		if (!ObjectUtils.isEmpty(todo)) {
			Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, idEvent);
			todo.setEvent(event);
			sessionFactory.getCurrentSession().update(todo);
		}
	}

	@Override
	public String deleteAlertsByStageAndTask(Long stageId,String taskCode) {
		Todo todoEntity = new Todo();
		String rtnMsg = "";
		List<Todo> todoList = sessionFactory.getCurrentSession().createCriteria(Todo.class)
				.add(Restrictions.eq("stage.idStage", stageId))
				.add(Restrictions.eq("cdTodoTask", taskCode))
				.add(Restrictions.eq("cdTodoType", ServiceConstants.ALERT_TODO)).list();

		if (!TypeConvUtil.isNullOrEmpty(todoEntity)) {
			for (Todo todoDetail : todoList) {
				sessionFactory.getCurrentSession().delete(todoDetail);
			}
			rtnMsg = ServiceConstants.SUCCESS;
		} else {
			throw new TodoDetailsNotFoundException(todoEntity.getIdTodo());
		}
		return rtnMsg;
	}

	@Override
	public List<TodoDto> getTasksByStageIdTask(Long idTodoStage, String cdTodTask) throws DataNotFoundException {
		{
			List<TodoDto> todoList = new ArrayList<TodoDto>();
			Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(tasksByStageIdTaskSql)
					.addScalar("idTodo", StandardBasicTypes.LONG).addScalar("cdTodoType", StandardBasicTypes.STRING)
					.addScalar("dtTodoCompleted", StandardBasicTypes.TIMESTAMP)
					.addScalar("dtTodoCreated", StandardBasicTypes.TIMESTAMP)
					.addScalar("dtTodoDue", StandardBasicTypes.TIMESTAMP).addScalar("cdTodoTask", StandardBasicTypes.STRING)
					.addScalar("dtTodoTaskDue", StandardBasicTypes.TIMESTAMP)
					.addScalar("idTodoCase", StandardBasicTypes.LONG)
					.addScalar("idTodoPersCreator", StandardBasicTypes.LONG)
					.addScalar("idTodoPersAssigned", StandardBasicTypes.LONG)
					.addScalar("idTodoPersWorker", StandardBasicTypes.LONG)
					.addScalar("idTodoStage", StandardBasicTypes.LONG).addScalar("idTodoEvent", StandardBasicTypes.LONG)
					.addScalar("todoDesc", StandardBasicTypes.STRING).addScalar("todoLongDesc", StandardBasicTypes.STRING)
					.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP).setParameter("idSearch", idTodoStage)
					.setParameter("searchTask", cdTodTask).setResultTransformer(Transformers.aliasToBean(TodoDto.class));
			todoList = query.list();
			return todoList;
		}
	}

	@Override
	public List<TodoDto> getTasksByCaseAndTask(Long idCase, String taskCode) {
		List<TodoDto> todoList = new ArrayList<TodoDto>();

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(tasksByCaseAndtaskCodeSql)
				.addScalar("idTodo", StandardBasicTypes.LONG).addScalar("cdTodoType", StandardBasicTypes.STRING)
				.addScalar("dtTodoCompleted", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtTodoCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtTodoDue", StandardBasicTypes.TIMESTAMP).addScalar("cdTodoTask", StandardBasicTypes.STRING)
				.addScalar("dtTodoTaskDue", StandardBasicTypes.TIMESTAMP)
				.addScalar("idTodoCase", StandardBasicTypes.LONG)
				.addScalar("idTodoPersCreator", StandardBasicTypes.LONG)
				.addScalar("idTodoPersAssigned", StandardBasicTypes.LONG)
				.addScalar("idTodoPersWorker", StandardBasicTypes.LONG)
				.addScalar("idTodoStage", StandardBasicTypes.LONG).addScalar("idTodoEvent", StandardBasicTypes.LONG)
				.addScalar("todoDesc", StandardBasicTypes.STRING).addScalar("todoLongDesc", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP).setParameter("idCase", idCase)
				.setParameter("taskCode", taskCode).setResultTransformer(Transformers.aliasToBean(TodoDto.class));
		todoList = query.list();
		return todoList;
	}

	@Override
	public void deleteAlertsByCaseAndTask(Long idCase, String taskCode) {
		{
			Todo todoEntity = new Todo();
			String rtnMsg = "";
			List<Todo> todoList = sessionFactory.getCurrentSession().createCriteria(Todo.class)
					.add(Restrictions.eq("capsCase.idCase", idCase))
					.add(Restrictions.eq("cdTodoTask", taskCode))
					.add(Restrictions.eq("cdTodoType", ServiceConstants.ALERT_TODO)).list();

			if (!TypeConvUtil.isNullOrEmpty(todoEntity)) {
				for (Todo todoDetail : todoList) {
					sessionFactory.getCurrentSession().delete(todoDetail);
				}
				rtnMsg = ServiceConstants.SUCCESS;
			} else {
				throw new TodoDetailsNotFoundException(todoEntity.getIdTodo());
			}
		}
	}


	@Override
	public void deleteTodosByTaskAndCase(Long idCase, String taskCode) {
		{
			List<Todo> todoList = sessionFactory.getCurrentSession().createCriteria(Todo.class)
					.add(Restrictions.eq("capsCase.idCase", idCase))
					.add(Restrictions.eq("cdTodoTask", taskCode))
					.add(Restrictions.isNull(ServiceConstants.DT_TODO_COMPLETED))
					.add(Restrictions.eq("cdTodoType", ServiceConstants.TASK_TODO)).list();

			if (!TypeConvUtil.isNullOrEmpty(todoList)) {
				for (Todo todoDetail : todoList) {
					sessionFactory.getCurrentSession().delete(todoDetail);
				}
			}
		}
	}

	/**
	 * Method helps to call the database to check Guardianship referral permission by person id
	 * if referral exists query will return 1 else 0
	 *
	 * @param idPerson selected person id
	 * @return if person has the permissions to approve the Guardianship Referral return true else false
	 */
	@Override
	public boolean checkForGdnRefAprvByPersonId(Long idPerson) {
		SQLQuery sQLQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(findGuardianshipApproveSql)
				.setParameter("idPerson", idPerson);
		Long count = ((BigDecimal) sQLQuery.uniqueResult()).longValue();
		return (count > 0);
	}
	/**
	 * Method helps to call the database to check if selected person has security class 'EXEC_STAFF' or 'SUPERVISOR'
	 *
	 * @param idPerson selected person id
	 * @return if person has the permissions to approve case closure return true else false
	 */
	@Override
	public boolean isCaseClosureApprover(Long idPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(checkCaseClosureApproverSql)
				.addScalar("REC_EXISTS", StandardBasicTypes.LONG);
		query.setParameter("idPerson", idPerson);
		Long count  = (Long) query.uniqueResult();
		return (count > 0);
	}

	@Override
	public void saveorUpdate(Todo todo) {
		sessionFactory.getCurrentSession().saveOrUpdate(todo);
	}

}
