package us.tx.state.dfps.service.legal.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.legal.dao.ToDoEventDao;
import us.tx.state.dfps.service.legal.dto.FetchToDoOutDto;
import us.tx.state.dfps.service.legal.dto.TodoDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Dao for
 * ToDoEventDaoImpl Sep 7, 2017- 4:40:51 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@Repository
public class ToDoEventDaoImpl implements ToDoEventDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${Ccmn42dDao.fetchToDoListForEvent}")
	private String fetchToDoListForEvent;

	@Value("${Ccmn42dDao.fetchOpenTodoForStageSql}")
	private String fetchOpenTodoForStageSql;

	private static final Logger log = Logger.getLogger(ToDoEventDaoImpl.class);

	/**
	 * Method Name: fetchToDoListForEvent Method Description: This method Calls
	 * the DAO to fetch the information from Event. DAM: Ccmn42d
	 * 
	 * @param idEvent
	 * @return List @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FetchToDoOutDto> fetchToDoListForEvent(long idEvent) {
		log.debug("Entering method fetchToDoListForEvent in ToDoEventDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchToDoListForEvent)
				.addScalar("cdTodoType", StandardBasicTypes.STRING).addScalar("cdTask", StandardBasicTypes.STRING)
				.addScalar("dtTodoDue", StandardBasicTypes.DATE).addScalar("txtTodoDesc", StandardBasicTypes.STRING)
				.addScalar("idTodo", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("scrTodoCreated", StandardBasicTypes.STRING)
				.addScalar("scrTaskDue", StandardBasicTypes.STRING)
				.addScalar("scrTaskCompleted", StandardBasicTypes.STRING)
				.addScalar("scrTaskUpdate", StandardBasicTypes.STRING)
				.addScalar("scrTaskInfo", StandardBasicTypes.STRING)
				.addScalar("scrTodoAssignedTo", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(FetchToDoOutDto.class)));
		sQLQuery1.setParameter("idEvent", idEvent);
		List<FetchToDoOutDto> fetchToDoOutDtos = (List<FetchToDoOutDto>) sQLQuery1.list();
		log.debug("Exiting method fetchToDoListForEvent in ToDoEventDaoImpl");
		return fetchToDoOutDtos;
	}

	/**
	 * Method Name: fetchOpenTodoForStage Method Description:Fetch the IdTodo of
	 * the open Next review Todo task for the stage. It should have only one
	 * Todo in the list.
	 * 
	 * @param idStage
	 * @return List<TodoDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<TodoDto> fetchOpenTodoForStage(Long idStage) {

		List<TodoDto> todoDtos = new ArrayList<>();
		TodoDto todoDto = new TodoDto();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);
		criteria.add(Restrictions.eq("stage.idStage", idStage));
		criteria.add(Restrictions.isNull("dtTodoCompleted"));
		criteria.addOrder(Order.desc("idTodo"));

		List<Todo> listToDo = (List<Todo>) criteria.list();
		if (!TypeConvUtil.isNullOrEmpty(listToDo)) {
			for (Todo todo : listToDo) {
				todoDto.setIdTodo(todo.getIdTodo());
				todoDto.setDtLastUpdate(todo.getDtLastUpdate());
				if (!TypeConvUtil.isNullOrEmpty(todo.getPersonByIdTodoPersAssigned())) {
					todoDto.setPersonByIdTodoPersAssigned(todo.getPersonByIdTodoPersAssigned().getIdPerson());
				}
				if (!TypeConvUtil.isNullOrEmpty(todo.getCapsCase())) {
					todoDto.setIdTodoCase(todo.getCapsCase().getIdCase());
				}
				if (!TypeConvUtil.isNullOrEmpty(todo.getEvent())) {
					todoDto.setIdTodoEvent(todo.getEvent().getIdEvent());
				}
				if (!TypeConvUtil.isNullOrEmpty(todo.getPersonByIdTodoPersCreator())) {
					todoDto.setPersonByIdTodoPersCreator(todo.getPersonByIdTodoPersCreator().getIdPerson());
				}
				if (!TypeConvUtil.isNullOrEmpty(todo.getPersonByIdTodoPersWorker())) {
					todoDto.setPersonByIdTodoPersWorker(todo.getPersonByIdTodoPersWorker().getIdPerson());
				}
				if (!TypeConvUtil.isNullOrEmpty(todo.getStage())) {
					todoDto.setIdTodoStage(todo.getStage().getIdStage());
				}
				todoDto.setDtTodoDue(todo.getDtTodoDue());
				todoDto.setCdTodoTask(todo.getCdTodoTask());

				todoDto.setTxtTodoDesc(todo.getTxtTodoDesc());
				todoDto.setCdTodoType(todo.getCdTodoType());
				todoDto.setTxtTodoLongDesc(todo.getTxtTodoLongDesc());
				todoDto.setDtTodoCreated(todo.getDtTodoCreated());
				todoDto.setDtTodoTaskDue(todo.getDtTodoTaskDue());
				todoDto.setDtTodoCompleted(todo.getDtTodoCompleted());
				todoDto.setNmTodoCreatorInit(todo.getNmTodoCreatorInit());
				if (!TypeConvUtil.isNullOrEmpty(todo.getTodoInfo())) {
					todoDto.setIdTodoInfo(todo.getTodoInfo().getIdTodoInfo());
				}
				todoDtos.add(todoDto);
			}

		}
		return todoDtos;

	}

}
