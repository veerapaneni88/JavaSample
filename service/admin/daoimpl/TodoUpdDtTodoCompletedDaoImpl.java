package us.tx.state.dfps.service.admin.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.TodoUpdDtTodoCompletedDao;
import us.tx.state.dfps.service.admin.dto.TodoUpdDtTodoCompletedInDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * updates TODO Completed Date Aug 4, 2017- 12:16:12 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class TodoUpdDtTodoCompletedDaoImpl implements TodoUpdDtTodoCompletedDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${TodoUpdDtTodoCompletedDaoImpl.updateTodoCompleted}")
	private transient String updateTodoCompleted;

	private static final Logger log = Logger.getLogger(TodoUpdDtTodoCompletedDaoImpl.class);

	public TodoUpdDtTodoCompletedDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: updateTODOEvent Method Description: This method will update
	 * DT_TODO_COMPLETED for TODO table.
	 * 
	 * @param pInputDataRec
	 * @
	 */
	@Override
	public void updateTODOEvent(TodoUpdDtTodoCompletedInDto pInputDataRec) {
		log.debug("Entering method TodoUpdDtTodoCompletedQUERYdam in TodoUpdDtTodoCompletedDaoImpl");
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateTodoCompleted);
		sQLQuery1.setParameter("hI_ulIdEvent", pInputDataRec.getIdEvent());
		sQLQuery1.executeUpdate();
		/*
		 * if (rowCount == 0) { throw new DataNotFoundException(
		 * messageSource.getMessage(
		 * "TodoUpdDtTodoCompletedDaoImpl.not.found.todo", null, Locale.US)); }
		 */
		log.debug("Exiting method TodoUpdDtTodoCompletedQUERYdam in TodoUpdDtTodoCompletedDaoImpl");
	}
}
