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

import us.tx.state.dfps.service.admin.dao.TaskTaskDetailsDao;
import us.tx.state.dfps.service.admin.dto.TaskInDto;
import us.tx.state.dfps.service.admin.dto.TaskOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Class will
 * query the Stage table to fetch the stage details for a given stage Id Aug 7,
 * 2017- 7:12:29 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class TaskTaskDetailsDaoImpl implements TaskTaskDetailsDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${TaskTaskDetailsDaoImpl.getTaskDetails}")
	private transient String getTaskDetails;

	private static final Logger log = Logger.getLogger(TaskTaskDetailsDaoImpl.class);

	public TaskTaskDetailsDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getTaskDtls Method Description: This method will get data
	 * from TASK table.
	 * 
	 * @param pInputDataRec
	 * @return List<TaskOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<TaskOutDto> getTaskDtls(TaskInDto pInputDataRec) {
		log.debug("Entering method TaskTaskDetailsQUERYdam in TaskTaskDetailsDaoImpl");
		Query queryTaskValues = sessionFactory.getCurrentSession().createSQLQuery(getTaskDetails)
				.addScalar("cdTask", StandardBasicTypes.STRING).addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.addScalar("cdEventType", StandardBasicTypes.STRING)
				.addScalar("cdTaskListWindow", StandardBasicTypes.STRING)
				.addScalar("cdTaskPrior", StandardBasicTypes.STRING).addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("cdTaskTopWindow", StandardBasicTypes.STRING)
				.addScalar("indTaskDetailEnable", StandardBasicTypes.STRING)
				.addScalar("indTaskEventCreate", StandardBasicTypes.STRING)
				.addScalar("indTaskEventNavig", StandardBasicTypes.STRING)
				.addScalar("indTaskListEnable", StandardBasicTypes.STRING)
				.addScalar("indTaskMultInstance", StandardBasicTypes.STRING)
				.addScalar("indTaskNewEnable", StandardBasicTypes.STRING)
				.addScalar("indTaskNewUsing", StandardBasicTypes.STRING)
				.addScalar("indTaskNuAcrossCase", StandardBasicTypes.STRING)
				.addScalar("indTaskRtrvPriorStage", StandardBasicTypes.STRING)
				.addScalar("indTaskTodoEnable", StandardBasicTypes.STRING)
				.addScalar("taskDecode", StandardBasicTypes.STRING)
				.addScalar("cIndTaskShowInList", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(TaskOutDto.class));
		queryTaskValues.setParameter("hI_szCdTask", pInputDataRec.getCdTask());
		List<TaskOutDto> liCcmn82doDto = (List<TaskOutDto>) queryTaskValues.list();
		if (TypeConvUtil.isNullOrEmpty(liCcmn82doDto)) {
			throw new DataNotFoundException(messageSource.getMessage("stage.not.found.attributes", null, Locale.US));
		}
		log.debug("Exiting method TaskTaskDetailsQUERYdam in TaskTaskDetailsDaoImpl");
		return liCcmn82doDto;
	}
}
