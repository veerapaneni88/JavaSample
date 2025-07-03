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

import us.tx.state.dfps.service.admin.dao.StageRetDao;
import us.tx.state.dfps.service.admin.dto.StagediDto;
import us.tx.state.dfps.service.admin.dto.StagedoDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CINV50S Aug
 * 7, 2017- 7:03:37 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class StageRetDaoImpl implements StageRetDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${Cses71dDaoImpl.getStageDetls}")
	private transient String getStageDetls;

	private static final Logger log = Logger.getLogger(StageRetDaoImpl.class);

	/**
	 * Method Description: getStageValues - Method will query the Stage table to
	 * fetch the stage details for a given stage Id
	 * 
	 * @param pInputDataRec
	 * @return stageValues @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StagedoDto> getStageValues(StagediDto pInputDataRec) {
		log.debug("Entering method getStageValues in Cses71dDaoImpl");
		Query queryStageValues = sessionFactory.getCurrentSession().createSQLQuery(getStageDetls)
				.addScalar("ulIdStage", StandardBasicTypes.LONG).addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("szCdStageType", StandardBasicTypes.STRING).addScalar("ulIdUnit", StandardBasicTypes.LONG)
				.addScalar("ulIdCase", StandardBasicTypes.LONG).addScalar("ulIdSituation", StandardBasicTypes.LONG)
				.addScalar("dtDtStageClose", StandardBasicTypes.DATE)
				.addScalar("szCdStageClassification", StandardBasicTypes.STRING)
				.addScalar("szCdStageCurrPriority", StandardBasicTypes.STRING)
				.addScalar("szCdStageInitialPriority", StandardBasicTypes.STRING)
				.addScalar("szCdStageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("szCdStageRsnPriorityChgd", StandardBasicTypes.STRING)
				.addScalar("bIndStageClose", StandardBasicTypes.STRING)
				.addScalar("szCdStageCnty", StandardBasicTypes.STRING).addScalar("szNmStage", StandardBasicTypes.STRING)
				.addScalar("szCdStageRegion", StandardBasicTypes.STRING)
				.addScalar("dtDtStageStart", StandardBasicTypes.DATE)
				.addScalar("szCdStageProgram", StandardBasicTypes.STRING)
				.addScalar("szCdStage", StandardBasicTypes.STRING)
				.addScalar("szTxtStageClosureCmnts", StandardBasicTypes.STRING)
				.addScalar("szTxtStagePriorityCmnts", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(StagedoDto.class));
		queryStageValues.setParameter("hI_ulIdStage", pInputDataRec.getUlIdStage());

		List<StagedoDto> stageValues = (List<StagedoDto>) queryStageValues.list();
		if (TypeConvUtil.isNullOrEmpty(stageValues)) {
			throw new DataNotFoundException(messageSource.getMessage("stage.not.found.attributes", null, Locale.US));
		}
		log.debug("Exiting method cses71dQUERYdam in Cses71dDaoImpl");
		return stageValues;
	}

	/**
	 * 
	 * Method Name: getStageDtails Method Description:Gets stage details.
	 * 
	 * @param cses71di
	 * @return @
	 */
	@Override
	public StagedoDto getStageDtails(StagediDto cses71di) {
		log.debug("Entering method getStageValues in Cses71dDaoImpl");
		Query queryStageValues = sessionFactory.getCurrentSession().createSQLQuery(getStageDetls)
				.addScalar("ulIdStage", StandardBasicTypes.LONG).addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("szCdStageType", StandardBasicTypes.STRING).addScalar("ulIdUnit", StandardBasicTypes.LONG)
				.addScalar("ulIdCase", StandardBasicTypes.LONG).addScalar("ulIdSituation", StandardBasicTypes.LONG)
				.addScalar("dtDtStageClose", StandardBasicTypes.DATE)
				.addScalar("szCdStageClassification", StandardBasicTypes.STRING)
				.addScalar("szCdStageCurrPriority", StandardBasicTypes.STRING)
				.addScalar("szCdStageInitialPriority", StandardBasicTypes.STRING)
				.addScalar("szCdStageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("szCdStageRsnPriorityChgd", StandardBasicTypes.STRING)
				.addScalar("bIndStageClose", StandardBasicTypes.STRING)
				.addScalar("szCdStageCnty", StandardBasicTypes.STRING).addScalar("szNmStage", StandardBasicTypes.STRING)
				.addScalar("szCdStageRegion", StandardBasicTypes.STRING)
				.addScalar("dtDtStageStart", StandardBasicTypes.DATE)
				.addScalar("szCdStageProgram", StandardBasicTypes.STRING)
				.addScalar("szCdStage", StandardBasicTypes.STRING)
				.addScalar("szTxtStageClosureCmnts", StandardBasicTypes.STRING)
				.addScalar("szTxtStagePriorityCmnts", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(StagedoDto.class));
		queryStageValues.setParameter("hI_ulIdStage", cses71di.getUlIdStage());

		StagedoDto stageValues = (StagedoDto) queryStageValues.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(stageValues)) {
			throw new DataNotFoundException(messageSource.getMessage("stage.not.found.attributes", null, Locale.US));
		}
		log.debug("Exiting method cses71dQUERYdam in Cses71dDaoImpl");
		return stageValues;

	}

}
