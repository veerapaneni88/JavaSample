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

import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceSelectStageDao;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvInDto;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceSelectStageDaoImpl Feb 7, 2018- 5:51:35 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class CaseMaintenanceSelectStageDaoImpl implements CaseMaintenanceSelectStageDao {
	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CaseMaintenanceSelectStageDaoImpl.strCINT21D_CURSORQuery}")
	private transient String strCINT21D_CURSORQuery;

	private static final Logger log = Logger.getLogger(CaseMaintenanceSelectStageDaoImpl.class);

	/**
	 * Method Name: selectStage Method Description:This Method is used to select
	 * stage DAM: Cint21d
	 * 
	 * @param stageRtrvInDto
	 * @param stageRtrvOutDto
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void selectStage(StageRtrvInDto stageRtrvInDto, StageRtrvOutDto stageRtrvOutDto) {
		log.debug("Entering method selectStage in CaseMaintenanceSelectStageDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strCINT21D_CURSORQuery)
				.addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("cdStageClassification", StandardBasicTypes.STRING)
				.addScalar("cdStageCnty", StandardBasicTypes.STRING)
				.addScalar("cdStageCurrPriority", StandardBasicTypes.STRING)
				.addScalar("cdStageInitialPriority", StandardBasicTypes.STRING)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("cdStageRegion", StandardBasicTypes.STRING)
				.addScalar("cdStageRsnPriorityChgd", StandardBasicTypes.STRING)
				.addScalar("cdStageType", StandardBasicTypes.STRING)
				.addScalar("dtStageClose", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtStageStart", StandardBasicTypes.TIMESTAMP).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idSituation", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idUnit", StandardBasicTypes.LONG).addScalar("indStageClose", StandardBasicTypes.STRING)
				.addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("txtStagePriorityCmnts", StandardBasicTypes.STRING)
				.addScalar("txtStageClosureCmnts", StandardBasicTypes.STRING)
				.addScalar("indIncmgAssignPn", StandardBasicTypes.STRING)
				.setParameter("idStage", stageRtrvInDto.getUlIdStage())
				.setResultTransformer(Transformers.aliasToBean(StageRtrvOutDto.class)));

		List<StageRtrvOutDto> stageRtrvOutDtos = new ArrayList<>();
		stageRtrvOutDtos = (List<StageRtrvOutDto>) sQLQuery1.list();

		if (!CollectionUtils.isEmpty(stageRtrvOutDtos)) {
			BeanUtils.copyProperties(stageRtrvOutDtos.get(0), stageRtrvOutDto);
		}

		log.debug("Exiting method selectStage in CaseMaintenanceSelectStageDaoImpl");
	}

}
