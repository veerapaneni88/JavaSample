package us.tx.state.dfps.service.admin.daoimpl;

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

import us.tx.state.dfps.service.admin.dao.TodoInfoDao;
import us.tx.state.dfps.service.admin.dto.TodoInfoInDto;
import us.tx.state.dfps.service.admin.dto.TodoInfoOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Retrieve a
 * data from ToDo Info Aug 10, 2017- 2:14:56 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class TodoInfoDaoImpl implements TodoInfoDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${TodoInfoDaoImpl.getTodoInfo}")
	private transient String getTodoInfo;

	private static final Logger log = Logger.getLogger(TodoInfoDaoImpl.class);

	public TodoInfoDaoImpl() {
		super();
	}

	/**
	 *
	 * @param pInputDataRec
	 * @return List<Cses08doDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<TodoInfoOutDto> getTodoInfo(TodoInfoInDto pInputDataRec) {
		log.debug("Entering method TodoInfoQUERYdam in TodoInfoDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getTodoInfo)
				.addScalar("idTodoInfo", StandardBasicTypes.LONG).addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("cdTodoInfo", StandardBasicTypes.STRING)
				.addScalar("cdTodoInfoPersAssignd", StandardBasicTypes.STRING)
				.addScalar("cdTodoInfoTask", StandardBasicTypes.STRING)
				.addScalar("todoInfoType", StandardBasicTypes.STRING)
				.addScalar("nbrTodoInfoDueDd", StandardBasicTypes.SHORT)
				.addScalar("nbrTodoInfoDueMm", StandardBasicTypes.SHORT)
				.addScalar("nbrTodoInfoDueYy", StandardBasicTypes.SHORT)
				.addScalar("nbrTodoInfoTaskDueDd", StandardBasicTypes.SHORT)
				.addScalar("nbrTodoInfoTaskDueMm", StandardBasicTypes.SHORT)
				.addScalar("nbrTodoInfoTaskDueYy", StandardBasicTypes.SHORT).addScalar("todoInfoDesc")
				.addScalar("todoInfoLongDesc").addScalar("indTodoInfoEnabled")
				.setParameter("hI_szCdTodoInfo", pInputDataRec.getCdTodoInfo())
				.setResultTransformer(Transformers.aliasToBean(TodoInfoOutDto.class)));
		List<TodoInfoOutDto> liCses08doDto = (List<TodoInfoOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCses08doDto) || liCses08doDto.size() == 0) {
			throw new DataNotFoundException(
					messageSource.getMessage("Cses08dDaoImpl.TodoInfo.not.found", null, Locale.US));
		}
		log.debug("Exiting method TodoInfoQUERYdam in TodoInfoDaoImpl");
		return liCses08doDto;
	}
}
