package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.StageDetailsDao;
import us.tx.state.dfps.service.admin.dto.StageDetailsDiDto;
import us.tx.state.dfps.service.admin.dto.StageDetailsDoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:StageDetailsDaoImpl Sep 8, 2017- 7:30:17 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class StageDetailsDaoImpl implements StageDetailsDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${Cint40dDaoImpl.getStageDetails}")
	private String getStageDetails;

	/**
	 * Method Name: getStageDtls Method Description: Retrieves the Stage
	 * Details.
	 * 
	 * @param stageDetailsDiDto
	 * @return List<StageDetailsDoDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StageDetailsDoDto> getStageDtls(StageDetailsDiDto stageDetailsDiDto) {

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getStageDetails)
				.setResultTransformer(Transformers.aliasToBean(StageDetailsDoDto.class)));
		sQLQuery1.addScalar("cdStage", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdStageClassification", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdStageCnty", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdStageCurrPriority", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdStageInitialPriority", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdStageProgram", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdStageRegion", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdStageRsnPriorityChgd", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdStageType", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtStageClose", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("dtStageStart", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("idCase", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idSituation", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idStage", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idUnit", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("nmStage", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("stagePriorityCmnts", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdClientAdvised", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indEcs", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indEcsVer", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("stageClosureCmnts", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indStageClose", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("dtClientAdvised", StandardBasicTypes.DATE);
		sQLQuery1.addScalar("indOpenCaseFoundAtIntake", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indIntakeFormallyScreened", StandardBasicTypes.STRING);

		sQLQuery1.setParameter("idStage", stageDetailsDiDto.getIdStage());

		return (List<StageDetailsDoDto>) sQLQuery1.list();
	}
}
