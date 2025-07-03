package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.service.admin.dao.UpdateToDoDao;
import us.tx.state.dfps.service.admin.dto.UpdateToDoDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * updates TODO Completed Date Aug 4, 2017- 12:16:12 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class UpdateToDoDaoImpl implements UpdateToDoDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${Cinv43dDaoImpl.updateTodoCompleted}")
	private transient String updateTodoCompleted;

	private static final Logger log = Logger.getLogger(UpdateToDoDaoImpl.class);

	/**
	 * 
	 * Description: This Method updates Todo details
	 * 
	 * @param updateToDoDto
	 * @return void
	 */
	@Override
	public String updateTODOEvent(UpdateToDoDto updateToDoDto) {
		log.debug("Entering method cinv43dAUDdam in Cinv43dDaoImpl");
		String message = ServiceConstants.EMPTY_STR;
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateTodoCompleted);
		sQLQuery1.setParameter("hI_ulIdEvent", updateToDoDto.getIdEvent());
		sQLQuery1.executeUpdate();
		message = ServiceConstants.FND_SUCCESS;
		log.debug("Exiting method updateTODOEvent in updateToDoDaoImpl");
		return message;
	}

	@Override
	public void completeTodoEvent(Long idEvent) {
		UpdateToDoDto updateToDoDto = new UpdateToDoDto();
		updateToDoDto.setIdEvent(idEvent);
		updateTODOEvent(updateToDoDto);
	}

	/**
	 * Method Name: completeTodo Method Description:Updates the
	 * DateToDoCompleted field to current date
	 * 
	 * @param updateToDoDto
	 * @return Long @
	 */
	@Override
	public Long completeTodo(UpdateToDoDto updateToDoDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);
		criteria.add(Restrictions.eq("event.idEvent", updateToDoDto.getIdEvent()));
		List<Todo> toDoList = criteria.list();
		for (Todo toDo : toDoList) {
			toDo.setDtTodoCompleted(new Date());
			sessionFactory.getCurrentSession().saveOrUpdate(toDo);
		}

		if (!TypeConvUtil.isNullOrEmpty(toDoList)) {
			return (long) toDoList.size();
		} else {
			return ServiceConstants.ZERO_VAL;
		}

	}

}
