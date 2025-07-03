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

import us.tx.state.dfps.service.admin.dao.FetchToDoDao;
import us.tx.state.dfps.service.admin.dto.FetchToDoDto;
import us.tx.state.dfps.service.admin.dto.FetchToDodiDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Dao for
 * FetchToDoDaoImpl Sep 8, 2017- 3:49:50 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@Repository
public class FetchToDoDaoImpl implements FetchToDoDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${Cses08dDaoImpl.getTodoInfo}")
	private transient String getTodoInfo;

	private static final Logger log = Logger.getLogger(FetchToDoDaoImpl.class);

	/**
	 * Method Name: getTodoInfo Method Description: This method retrieves
	 * details from Info table
	 * 
	 * @param pInputDataRec
	 * @return List<FetchToDoDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FetchToDoDto> getTodoInfo(FetchToDodiDto pInputDataRec) {
		log.debug("Entering method getTodoInfo in FetchToDoDaoImpl");

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getTodoInfo)
				.addScalar("idTodoInfo", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.STRING)
				.addScalar("cdTodoInfo", StandardBasicTypes.STRING)
				.addScalar("cdTodoInfoPersAssignd", StandardBasicTypes.STRING)
				.addScalar("cdTodoInfoTask", StandardBasicTypes.STRING)
				.addScalar("cdTodoInfoType", StandardBasicTypes.STRING)
				.addScalar("nbrTodoInfoDueDd", StandardBasicTypes.SHORT)
				.addScalar("nbrTodoInfoDueMm", StandardBasicTypes.SHORT)
				.addScalar("nbrTodoInfoDueYy", StandardBasicTypes.SHORT)
				.addScalar("nbrTodoInfoTaskDueDd", StandardBasicTypes.SHORT)
				.addScalar("nbrTodoInfoTaskDueMm", StandardBasicTypes.SHORT)
				.addScalar("nbrTodoInfoTaskDueYy", StandardBasicTypes.SHORT)
				.addScalar("txtTodoInfoDesc", StandardBasicTypes.STRING)
				.addScalar("txtTodoInfoLongDesc", StandardBasicTypes.STRING)
				.addScalar("indTodoInfoEnabled", StandardBasicTypes.STRING)
				.setParameter("cdTodoInfo", pInputDataRec.getCdTodoInfo())
				.setResultTransformer(Transformers.aliasToBean(FetchToDoDto.class)));

		List<FetchToDoDto> liCses08doDto = (List<FetchToDoDto>) sQLQuery1.list();

		if (TypeConvUtil.isNullOrEmpty(liCses08doDto)) {
			throw new DataNotFoundException(messageSource.getMessage("TodoInfo.not.found", null, Locale.US));
		}

		log.debug("Exiting method getTodoInfo in Cses08dDaoImpl");
		return liCses08doDto;
	}

}
