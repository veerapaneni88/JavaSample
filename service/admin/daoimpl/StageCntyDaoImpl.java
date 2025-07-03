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

import us.tx.state.dfps.service.admin.dao.StageCntyDao;
import us.tx.state.dfps.service.admin.dto.StageCntyInDto;
import us.tx.state.dfps.service.admin.dto.StageCntyOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Method will
 * query the Stage table to fetch the stage details for a given stage Id Aug 7,
 * 2017- 7:03:37 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class StageCntyDaoImpl implements StageCntyDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${StageCntyDaoImpl.getStageDetls}")
	private transient String getStageDetls;

	private static final Logger log = Logger.getLogger(StageCntyDaoImpl.class);

	public StageCntyDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getStageValues Method Description: This method gets data
	 * from STAGE table.
	 * 
	 * @param pInputDataRec
	 * @return List<StageCntyOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StageCntyOutDto> getStageValues(StageCntyInDto pInputDataRec) {
		log.debug("Entering method StageCntyQUERYdam in StageCntyDaoImpl");
		Query queryStageValues = sessionFactory.getCurrentSession().createSQLQuery(getStageDetls)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("cdStageType", StandardBasicTypes.STRING).addScalar("idUnit", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idSituation", StandardBasicTypes.LONG)
				.addScalar("dtStageClose", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdStageClassification", StandardBasicTypes.STRING)
				.addScalar("cdStageCurrPriority", StandardBasicTypes.STRING)
				.addScalar("cdStageInitialPriority", StandardBasicTypes.STRING)
				.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("cdStageRsnPriorityChgd", StandardBasicTypes.STRING)
				.addScalar("indStageClose", StandardBasicTypes.STRING)
				.addScalar("cdStageCnty", StandardBasicTypes.STRING).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("cdStageRegion", StandardBasicTypes.STRING)
				.addScalar("dtStageStart", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING).addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("stageClosureCmnts", StandardBasicTypes.STRING)
				.addScalar("stagePriorityCmnts", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(StageCntyOutDto.class));
		queryStageValues.setParameter("hI_ulIdStage", pInputDataRec.getIdStage());
		List<StageCntyOutDto> stageValues = (List<StageCntyOutDto>) queryStageValues.list();
		if (TypeConvUtil.isNullOrEmpty(stageValues)) {
			throw new DataNotFoundException(messageSource.getMessage("stage.not.found.attributes", null, Locale.US));
		}
		log.debug("Exiting method StageCntyQUERYdam in StageCntyDaoImpl");
		return stageValues;
	}
}
