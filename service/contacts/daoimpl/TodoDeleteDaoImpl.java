package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contacts.dao.TodoDeleteDao;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.xmlstructs.inputstructs.DeleteTodoDto;

@Repository
public class TodoDeleteDaoImpl implements TodoDeleteDao {
	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Autowired
	private LookupDao lookupDao;

	@SuppressWarnings("unchecked")
	public long deleteTodo(DeleteTodoDto deleteTodoDto) {
		if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(deleteTodoDto.getArchInputStructDto().getCreqFuncCd())) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);
			criteria.add(Restrictions.eq("event.idEvent", deleteTodoDto.getUlIdEvent()));
			List<Todo> todoList = criteria.list();

			if (!TypeConvUtil.isNullOrEmpty(todoList)) {
				for (Todo todo : todoList) {
					sessionFactory.getCurrentSession().delete(todo);
				}
				return todoList.size();
			}

			if (todoList.size() == ServiceConstants.Zero) {
				throw new DataLayerException(lookupDao.getMessageByNumber(ServiceConstants.SQL_NOT_FOUND));
			}
		} else {
			throw new DataLayerException(lookupDao.getMessageByNumber(ServiceConstants.ARC_ERR_BAD_FUNC_CD));
		}

		return ServiceConstants.Zero;

	}

}
