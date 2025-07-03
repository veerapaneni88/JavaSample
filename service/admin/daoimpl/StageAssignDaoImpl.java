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

import us.tx.state.dfps.service.admin.dao.StageAssignDao;
import us.tx.state.dfps.service.admin.dto.StageAssignInDto;
import us.tx.state.dfps.service.admin.dto.StageAssignOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * will query the Stage table to fetch the stage details for a given stage Id
 * Aug 7, 2017- 6:45:29 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class StageAssignDaoImpl implements StageAssignDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${StageAssignDaoImpl.getStageDtls}")
	private transient String getStageDtls;

	private static final Logger log = Logger.getLogger(StageAssignDaoImpl.class);

	public StageAssignDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getStageDetails Method Description: This method will get
	 * data from STAGE table.
	 * 
	 * @param pInputDataRec
	 * @return List<StageAssignOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StageAssignOutDto> getStageDetails(StageAssignInDto pInputDataRec) {
		log.debug("Entering method StageAssignQUERYdam in StageAssignDaoImpl");
		Query queryStageValues = sessionFactory.getCurrentSession().createSQLQuery(getStageDtls).addScalar("cdStage")
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
				.addScalar("stagePriorityCmnts", StandardBasicTypes.STRING)
				.addScalar("stageClosureCmnts", StandardBasicTypes.STRING)
				.addScalar("indIncmgAssignPn", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(StageAssignOutDto.class));
		queryStageValues.setParameter("hI_ulIdStage", pInputDataRec.getIdStage());
		List<StageAssignOutDto> stageValues = (List<StageAssignOutDto>) queryStageValues.list();
		if (TypeConvUtil.isNullOrEmpty(stageValues)) {
			throw new DataNotFoundException(messageSource.getMessage("stage.not.found.attributes", null, Locale.US));
		}
		log.debug("Exiting method StageAssignQUERYdam in StageAssignDaoImpl");
		return stageValues;
	}
}
