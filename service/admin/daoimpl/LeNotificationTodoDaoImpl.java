/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Feb 22, 2018- 11:22:14 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.admin.daoimpl;

import java.util.ArrayList;
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

import us.tx.state.dfps.service.admin.dao.LeNotificationTodoDao;
import us.tx.state.dfps.service.admin.dto.LeNotificationTodoDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Feb 22, 2018- 11:22:14 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class LeNotificationTodoDaoImpl implements LeNotificationTodoDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${Cint58diDtoDaoImpl.getTodoDetls}")
	private transient String getTodoDetls;

	private static final Logger log = Logger.getLogger(LeNotificationTodoDaoImpl.class);

	public LeNotificationTodoDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getTodoByIdStageAndTask Method Description:This method will
	 * get table by Stage Id and Task.
	 * 
	 * @param pInputDataRec
	 * @return leNotificationTodoDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<LeNotificationTodoDto> getTodoByIdStageAndTask(LeNotificationTodoDto leNotificationTodoDto) {
		log.debug("Entering method cint58dQUERYdam in Cint58dDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getTodoDetls)
				.setResultTransformer(Transformers.aliasToBean(LeNotificationTodoDto.class)));
		sQLQuery1.addScalar("idTodo", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("cdTodoType", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtTodoCompleted", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("dtTodoCreated", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("dtTodoDue", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("cdTodoTask", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("idTodoPersAssigned", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idTodoPersWorker", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("todoDesc", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("todoLongDesc", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("idEvent", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idStage", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idCase", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("dtTodoDue", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.setParameter("hI_ulIdStage", leNotificationTodoDto.getIdStage());
		sQLQuery1.setParameter("hI_szCdTodoTask", leNotificationTodoDto.getCdTodoTask());
		List<LeNotificationTodoDto> liCint58doDto = new ArrayList<LeNotificationTodoDto>();
		liCint58doDto = (List<LeNotificationTodoDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCint58doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("LeNotificationTodoDaoImpl.not.found.ulIdStage", null, Locale.US));
		}
		log.debug("Exiting method cint58dQUERYdam in LeNotificationTodoDaoImpl");
		return liCint58doDto;
	}

}
