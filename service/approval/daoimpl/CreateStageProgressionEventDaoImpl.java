package us.tx.state.dfps.service.approval.daoimpl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.approval.dto.CreateStageProgressionEventReq;
import us.tx.state.dfps.approval.dto.CreateStageProgressionEventRes;
import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.common.domain.TodoInfo;
import us.tx.state.dfps.service.approval.dao.CreateStageProgressionEventDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: June 20,
 * 2018- 4:47:32 PM © 2018 Texas Department of Family and Protective Services
 */
@Repository
public class CreateStageProgressionEventDaoImpl implements CreateStageProgressionEventDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CreateStageProgressionEventDaoImpl.createStageProgressionEventAddSql}")
	private String createStageProgressionEventAddSql;

	@Value("${CreateStageProgressionEventDaoImpl.todoPrimaryKeyAutoGenSql}")
	private String todoPrimaryKeyAutoGenSql;

	/**
	 * Method Name: createStageProgressionEvent Method Description: This DAM
	 * will ADD a record to the TO*DO table. Used to create a Task To*Do linked
	 * to a Stage Progression created event Dam Name: CCMNJ6D Service Name:
	 * CCMN35S
	 * 
	 * @param CreateStageProgressionEventReq
	 * @return CreateStageProgressionEventRes with primary key
	 */
	@Override
	public CreateStageProgressionEventRes createStageProgressionEvent(
			CreateStageProgressionEventReq createStageProgressionEventReq) {
		CreateStageProgressionEventRes createStageProgressionEventRes = new CreateStageProgressionEventRes();

		switch (createStageProgressionEventReq.getReqFuncCd()) {

		case ServiceConstants.REQ_FUNC_CD_ADD:
			// Create new Todo to insert
			Todo newTodo = new Todo();

			if (!TypeConvUtil.isNullOrEmpty(createStageProgressionEventReq.getTodoLongDesc())) {
				newTodo.setTxtTodoLongDesc(createStageProgressionEventReq.getTodoLongDesc());
			}
			if (!TypeConvUtil.isNullOrEmpty(createStageProgressionEventReq.getIdTodoPersCreator())) {
				Person creator = new Person();
				creator.setIdPerson(createStageProgressionEventReq.getIdTodoPersCreator());
				newTodo.setPersonByIdTodoPersCreator(creator);
			}
			if (!TypeConvUtil.isNullOrEmpty(createStageProgressionEventReq.getCdTodoTask())) {
				newTodo.setCdTodoTask((createStageProgressionEventReq.getCdTodoTask()));
			}
			if (!TypeConvUtil.isNullOrEmpty(createStageProgressionEventReq.getDtTodoDue())) {
				newTodo.setDtTodoDue((createStageProgressionEventReq.getDtTodoDue()));
			}
			if (!TypeConvUtil.isNullOrEmpty(createStageProgressionEventReq.getIdTodoPersWorker())) {
				Person worker = new Person();
				worker.setIdPerson(createStageProgressionEventReq.getIdTodoPersWorker());
				newTodo.setPersonByIdTodoPersWorker(worker);
			}
			if (!TypeConvUtil.isNullOrEmpty(createStageProgressionEventReq.getIdStage())) {
				Stage stage = new Stage();
				stage.setIdStage(createStageProgressionEventReq.getIdStage());
				newTodo.setStage(stage);
			}
			if (!TypeConvUtil.isNullOrEmpty(createStageProgressionEventReq.getIdTodoPersAssigned())) {
				Person assignedPerson = new Person();
				assignedPerson.setIdPerson(createStageProgressionEventReq.getIdTodoPersAssigned());
				newTodo.setPersonByIdTodoPersAssigned(assignedPerson);
			}
			if (!TypeConvUtil.isNullOrEmpty(createStageProgressionEventReq.getTxtTodoDesc())) {
				newTodo.setTxtTodoDesc(createStageProgressionEventReq.getTxtTodoDesc());
			}
			if (!TypeConvUtil.isNullOrEmpty(createStageProgressionEventReq.getIdTodoInfo())) {
				TodoInfo todoInfo = new TodoInfo();
				todoInfo.setIdTodoInfo(createStageProgressionEventReq.getIdTodoInfo());
				newTodo.setTodoInfo(todoInfo);
			}
			if (!TypeConvUtil.isNullOrEmpty(createStageProgressionEventReq.getCdTodoType())) {
				newTodo.setCdTodoType(createStageProgressionEventReq.getCdTodoType());
			}
			if (!TypeConvUtil.isNullOrEmpty(createStageProgressionEventReq.getDtTodoCreated())) {
				newTodo.setDtTodoCreated(createStageProgressionEventReq.getDtTodoCreated());
			}
			if (!TypeConvUtil.isNullOrEmpty(createStageProgressionEventReq.getLastUpdate())) {
				newTodo.setDtLastUpdate(createStageProgressionEventReq.getLastUpdate());
			}
			if (!TypeConvUtil.isNullOrEmpty(createStageProgressionEventReq.getDtTaskDue())) {
				newTodo.setDtTodoTaskDue(createStageProgressionEventReq.getDtTaskDue());
			}
			if (!TypeConvUtil.isNullOrEmpty(createStageProgressionEventReq.getIdCase())) {
				CapsCase capsCase = new CapsCase();
				capsCase.setIdCase(createStageProgressionEventReq.getIdCase());
				newTodo.setCapsCase(capsCase);
			}
			if (!TypeConvUtil.isNullOrEmpty(createStageProgressionEventReq.getIdEvent())) {
				Event event = new Event();
				event.setIdEvent(createStageProgressionEventReq.getIdEvent());
				newTodo.setEvent(event);
			}
			if (!TypeConvUtil.isNullOrEmpty(createStageProgressionEventReq.getDtTodoCompleted())) {
				newTodo.setDtTodoCompleted(createStageProgressionEventReq.getDtTodoCompleted());
			}
			// will return the inserted record primary key
			Long primaryKey = (Long) sessionFactory.getCurrentSession().save(newTodo);
			createStageProgressionEventRes.setIdTodo(primaryKey);

			break;

		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			// TODO: Implement req basis
			break;

		case ServiceConstants.REQ_FUNC_CD_DELETE:
			// TODO: Implement req basis
			break;

		default:
			break;

		}
		return createStageProgressionEventRes;
	}
}
