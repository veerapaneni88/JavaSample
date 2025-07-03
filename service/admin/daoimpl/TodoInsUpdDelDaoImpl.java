package us.tx.state.dfps.service.admin.daoimpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dao.TodoInsUpdDelDao;
import us.tx.state.dfps.service.admin.dto.TodoInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.TodoInsUpdDelOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION
 *
 * Class Description: Ccmn43dDaoImpl
 *
 * Aug 7, 2017- 9:37:39 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class TodoInsUpdDelDaoImpl implements TodoInsUpdDelDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${TodoInsUpdDelDaoImpl.todoNextVal}")
	private transient String todoNextVal;

	@Value("${TodoInsUpdDelDaoImpl.todoInsertInsertNull}")
	private transient String todoInsert;

	@Value("${TodoInsUpdDelDaoImpl.todoInsertNotNull}")
	private transient String todoInsertNotNull;

	@Value("${TodoInsUpdDelDaoImpl.todoUpdate}")
	private transient String todoUpdate;

	@Value("${TodoInsUpdDelDaoImpl.todoDelete}")
	private transient String todoDelete;

	private static final Logger log = Logger.getLogger(TodoInsUpdDelDaoImpl.class);

	public TodoInsUpdDelDaoImpl() {
		super();
	}

	/**
	 * This method performs insert , update and delete operations on ToDo table
	 * Ccmn43d
	 * 
	 * @param todoInsUpdDelInDto
	 * @return TodoInsUpdDelOutDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public TodoInsUpdDelOutDto cudTODO(TodoInsUpdDelInDto todoInsUpdDelInDto) {
		log.debug("Entering method cudTODO in TodoInsUpdDelDaoImpl");
		boolean isSuccess = false;
		int rowCount = 0;
		List<TodoInsUpdDelOutDto> todoInsUpdDelOutDtos = null;
		switch (todoInsUpdDelInDto.getCdReqFunction()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
		case ServiceConstants.REQ_FUNC_CD_ADD_KIN:
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(todoNextVal)
					.setResultTransformer(Transformers.aliasToBean(TodoInsUpdDelOutDto.class)));
			sQLQuery1.addScalar("idTodo", StandardBasicTypes.LONG);
			todoInsUpdDelOutDtos = (List<TodoInsUpdDelOutDto>) sQLQuery1.list();
			if (TypeConvUtil.isNullOrEmpty(todoInsUpdDelOutDtos)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Ccmn43dDaoImpl.not.found.ldIdTodo.todoNextVal", null, Locale.US));
			} else {

				String query = null;

				if (!ObjectUtils.isEmpty(todoInsUpdDelInDto.getIdTodoPersCreator())) {
					query = todoInsertNotNull;
				} else {
					query = todoInsert;
				}
				SQLQuery sQLQuery2 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(query);
				sQLQuery2.setParameter("hI_TxtTodoLongDesc", todoInsUpdDelInDto.getTodoLongDesc());
				if (!ObjectUtils.isEmpty(todoInsUpdDelInDto.getIdTodoPersCreator())) {
					sQLQuery2.setParameter("hI_ulIdTodoPersCreator", todoInsUpdDelInDto.getIdTodoPersCreator());
				}
				sQLQuery2.setParameter("hI_szCdTodoTask", todoInsUpdDelInDto.getCdTodoTask());
				sQLQuery2.setDate("hI_dtDtTodoDue", todoInsUpdDelInDto.getDtTodoDue());
				sQLQuery2.setParameter("hO_ldIdTodo", todoInsUpdDelOutDtos.get(0).getIdTodo());
				sQLQuery2.setParameter("hI_ulIdTodoPersWorker", todoInsUpdDelInDto.getIdTodoPersWorker());
				sQLQuery2.setParameter("hI_ulIdStage", todoInsUpdDelInDto.getIdStage());
				sQLQuery2.setParameter("hI_ulIdTodoPersAssigned", todoInsUpdDelInDto.getIdTodoPersAssigned());
				sQLQuery2.setParameter("hI_szTxtTodoDesc", todoInsUpdDelInDto.getTodoDesc());
				sQLQuery2.setParameter("hI_ulIdTodoInfo", todoInsUpdDelInDto.getIdTodoInfo());
				sQLQuery2.setParameter("hI_szCdTodoType", todoInsUpdDelInDto.getCdTodoType());
				sQLQuery2.setDate("hI_dtDtTodoCreated", todoInsUpdDelInDto.getDtTodoCreated());
				sQLQuery2.setDate("hI_dtDtTaskDue", todoInsUpdDelInDto.getDtTaskDue());
				sQLQuery2.setParameter("hI_ulIdCase", todoInsUpdDelInDto.getIdCase());
				sQLQuery2.setParameter("hI_ulIdEvent", todoInsUpdDelInDto.getIdEvent());
				sQLQuery2.setDate("hI_dtDtTodoCompleted", todoInsUpdDelInDto.getDtTodoCompleted());
				rowCount = sQLQuery2.executeUpdate();
				if (TypeConvUtil.isNullOrEmpty(rowCount)) {
					throw new DataNotFoundException(
							messageSource.getMessage("Ccmn43dDaoImpl.not.found.ldIdTodo.todoInsert", null, Locale.US));
				} else {
					isSuccess = true;
				}
			}
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			SQLQuery sQLQuery3 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(todoUpdate);
			sQLQuery3.setParameter("hI_TxtTodoLongDesc", todoInsUpdDelInDto.getTodoLongDesc());
			sQLQuery3.setParameter("hI_tsLastUpdate", todoInsUpdDelInDto.getTsLastUpdate());
			sQLQuery3.setParameter("hI_ulIdTodoPersCreator", todoInsUpdDelInDto.getIdTodoPersCreator());
			sQLQuery3.setParameter("hI_szCdTodoTask", todoInsUpdDelInDto.getCdTodoTask());
			sQLQuery3.setDate("hI_dtDtTodoDue", todoInsUpdDelInDto.getDtTodoDue());
			sQLQuery3.setParameter("hI_ldIdTodo", todoInsUpdDelInDto.getIdTodo());
			sQLQuery3.setParameter("hI_ulIdTodoPersWorker", todoInsUpdDelInDto.getIdTodoPersWorker());
			sQLQuery3.setParameter("hI_ulIdStage", todoInsUpdDelInDto.getIdStage());
			sQLQuery3.setParameter("hI_ulIdTodoPersAssigned", todoInsUpdDelInDto.getIdTodoPersAssigned());
			sQLQuery3.setParameter("hI_szTxtTodoDesc", todoInsUpdDelInDto.getTodoDesc());
			sQLQuery3.setParameter("hI_szCdTodoType", todoInsUpdDelInDto.getCdTodoType());
			sQLQuery3.setDate("hI_dtDtTodoCreated", todoInsUpdDelInDto.getDtTodoCreated());
			sQLQuery3.setDate("hI_dtDtTaskDue", todoInsUpdDelInDto.getDtTaskDue());
			sQLQuery3.setParameter("hI_ulIdCase", todoInsUpdDelInDto.getIdCase());
			sQLQuery3.setDate("hI_dtDtTodoCompleted", todoInsUpdDelInDto.getDtTodoCompleted());
			rowCount = sQLQuery3.executeUpdate();
			if (TypeConvUtil.isNullOrEmpty(rowCount)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Ccmn43dDaoImpl.not.found.ldIdTodo.todoUpdate", null, Locale.US));
			} else {
				isSuccess = true;
			}
			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			DateFormat dateFormat = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_YY_MM_DD);
			SQLQuery sQLQuery4 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(todoDelete);
			sQLQuery4.setParameter("hI_ldIdTodo", todoInsUpdDelInDto.getIdTodo());
			sQLQuery4.setParameter("hI_tsLastUpdate", dateFormat.format(todoInsUpdDelInDto.getTsLastUpdate()));
			rowCount = sQLQuery4.executeUpdate();
			if (TypeConvUtil.isNullOrEmpty(rowCount)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Ccmn43dDaoImpl.not.found.ldIdTodo.todoDelete", null, Locale.US));
			} else {
				isSuccess = true;
			}
		}
		TodoInsUpdDelOutDto todoInsUpdDelOutDto = null;
		if (isSuccess) {
			if (todoInsUpdDelInDto.getCdReqFunction().equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
				if (null != todoInsUpdDelOutDtos) {
					todoInsUpdDelOutDto = todoInsUpdDelOutDtos.get(ServiceConstants.Zero);
				}
			} else {
				todoInsUpdDelOutDto = new TodoInsUpdDelOutDto();
			}
		}
		log.debug("Exiting method cudTODO in TodoInsUpdDelDaoImpl");
		return todoInsUpdDelOutDto;
	}
}
