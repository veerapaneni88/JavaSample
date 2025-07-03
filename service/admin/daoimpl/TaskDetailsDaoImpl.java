package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.TaskDetailsDao;
import us.tx.state.dfps.service.admin.dto.TaskdiDto;
import us.tx.state.dfps.service.admin.dto.TaskdoDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CINV50S Aug
 * 7, 2017- 7:12:29 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class TaskDetailsDaoImpl implements TaskDetailsDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${Ccmn82dDaoImpl.getTaskDetails}")
	private transient String getTaskDetails;

	private static final Logger log = Logger.getLogger(TaskDetailsDaoImpl.class);

	/**
	 * Method Description: getStageDetails - Method will query the Stage table
	 * to fetch the stage details for a given stage Id
	 * 
	 * @param pInputDataRec
	 * @return liCcmn82doDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<TaskdoDto> getTaskDtls(TaskdiDto pInputDataRec) {
		log.debug("Entering method ccmn82dQUERYdam in Ccmn82dDaoImpl");
		Query queryTaskValues = sessionFactory.getCurrentSession().createSQLQuery(getTaskDetails)
				.addScalar("szCdTask", StandardBasicTypes.STRING).addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("szCdEventStatus", StandardBasicTypes.STRING)
				.addScalar("szCdEventType", StandardBasicTypes.STRING)
				.addScalar("szCdTaskListWindow", StandardBasicTypes.STRING)
				.addScalar("szCdTaskPrior", StandardBasicTypes.STRING).addScalar("szCdStage", StandardBasicTypes.STRING)
				.addScalar("szCdStageProgram", StandardBasicTypes.STRING)
				.addScalar("szCdTaskTopWindow", StandardBasicTypes.STRING)
				.addScalar("cIndTaskDetailEnable", StandardBasicTypes.STRING)
				.addScalar("bIndTaskEventCreate", StandardBasicTypes.STRING)
				.addScalar("bIndTaskEventNavig", StandardBasicTypes.STRING)
				.addScalar("cIndTaskListEnable", StandardBasicTypes.STRING)
				.addScalar("bIndTaskMultInstance", StandardBasicTypes.STRING)
				.addScalar("cIndTaskNewEnable", StandardBasicTypes.STRING)
				.addScalar("cIndTaskNewUsing", StandardBasicTypes.STRING)
				.addScalar("cIndTaskNuAcrossCase", StandardBasicTypes.STRING)
				.addScalar("cIndTaskRtrvPriorStage", StandardBasicTypes.STRING)
				.addScalar("bIndTaskTodoEnable", StandardBasicTypes.STRING)
				.addScalar("szTxtTaskDecode", StandardBasicTypes.STRING)
				.addScalar("cIndTaskShowInList", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(TaskdoDto.class));

		queryTaskValues.setParameter("hI_szCdTask", pInputDataRec.getSzCdTask());
		List<TaskdoDto> liCcmn82doDto = (List<TaskdoDto>) queryTaskValues.list();
		if (TypeConvUtil.isNullOrEmpty(liCcmn82doDto)) {
			throw new DataNotFoundException(messageSource.getMessage("stage.not.found.attributes", null, Locale.US));
		}
		log.debug("Exiting method ccmn82dQUERYdam in Ccmn82dDaoImpl");
		return liCcmn82doDto;
	}

}
