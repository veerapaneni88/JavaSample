package us.tx.state.dfps.service.casemanagement.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceFetchTaskDao;
import us.tx.state.dfps.service.casepackage.dto.GetTaskInDto;
import us.tx.state.dfps.service.casepackage.dto.GetTaskOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceFetchTaskDaoImpl Feb 7, 2018- 5:50:55 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class CaseMaintenanceFetchTaskDaoImpl implements CaseMaintenanceFetchTaskDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CaseMaintenanceFetchTaskDaoImpl.strCCMN82D_CURSORQuery}")
	private transient String strCCMN82D_CURSORQuery;

	private static final Logger log = Logger.getLogger(CaseMaintenanceFetchTaskDaoImpl.class);

	/**
	 *
	 * Method Name: fetchTaskDtl Method Description:This Method is used to fetch
	 * task dtl
	 * 
	 * @param getTaskInDto
	 * @param getTaskOutDto
	 * @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void fetchTaskDtl(GetTaskInDto getTaskInDto, GetTaskOutDto getTaskOutDto) {
		log.debug("Entering method fetchTaskDtl in CaseMaintenanceFetchTaskDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strCCMN82D_CURSORQuery)
				.addScalar("cdTask", StandardBasicTypes.STRING).addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
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
				.addScalar("txtTaskDecode", StandardBasicTypes.STRING)
				.addScalar("indTaskShowInList", StandardBasicTypes.STRING)
				.setString("cdTask", getTaskInDto.getSzCdTask())
				.setResultTransformer(Transformers.aliasToBean(GetTaskOutDto.class)));

		List<GetTaskOutDto> taskOutDtos = new ArrayList<>();
		taskOutDtos = (List<GetTaskOutDto>) sQLQuery1.list();

		if (!CollectionUtils.isEmpty(taskOutDtos)) {
			BeanUtils.copyProperties(taskOutDtos.get(0), getTaskOutDto);
		}
		log.debug("Exiting method fetchTaskDtl in CaseMaintenanceFetchTaskDaoImpl");
	}

}
