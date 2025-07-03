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

import us.tx.state.dfps.service.admin.dao.StageRegionDao;
import us.tx.state.dfps.service.admin.dto.StageRegionInDto;
import us.tx.state.dfps.service.admin.dto.StageRegionOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

@Repository
public class StageRegionDaoImpl implements StageRegionDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${StageRegionDaoImpl.getStageDetails}")
	private transient String getStageDetails;

	private static final Logger log = Logger.getLogger(StageRegionDaoImpl.class);

	public StageRegionDaoImpl() {
		super();
	}

	/**
	 *
	 * @param pInputDataRec
	 * @return List<Cint40doDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StageRegionOutDto> getStageDtls(StageRegionInDto pInputDataRec) {
		log.debug("Entering method StageRegionQUERYdam in StageRegionDaoImpl");
		Query queryStageValues = sessionFactory.getCurrentSession().createSQLQuery(getStageDetails).addScalar("cdStage")
				.addScalar("cdStageClassification").addScalar("cdStageCnty").addScalar("cdStageCurrPriority")
				.addScalar("cdStageInitialPriority").addScalar("cdStageProgram").addScalar("cdStageReasonClosed")
				.addScalar("cdStageRegion").addScalar("cdStageRsnPriorityChgd").addScalar("cdStageType")
				.addScalar("dtStageClose").addScalar("dtStageStart").addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idSituation", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idUnit", StandardBasicTypes.LONG).addScalar("nmStage").addScalar("stagePriorityCmnts")
				.addScalar("cdClientAdvised").addScalar("indEcs").addScalar("indEcsVer").addScalar("stageClosureCmnts")
				.addScalar("indStageClose").addScalar("tsLastUpdate").addScalar("dtClientAdvised")
				.addScalar("indOpenCaseFoundAtIntake").addScalar("indIntakeFormallyScreened")
				.setResultTransformer(Transformers.aliasToBean(StageRegionOutDto.class));
		queryStageValues.setParameter("hI_ulIdStage", pInputDataRec.getIdStage());
		List<StageRegionOutDto> stageDtos = (List<StageRegionOutDto>) queryStageValues.list();
		if (TypeConvUtil.isNullOrEmpty(stageDtos)) {
			throw new DataNotFoundException(messageSource.getMessage("stage.not.found.attributes", null, Locale.US));
		}
		log.debug("Exiting method StageRegionQUERYdam in StageRegionDaoImpl");
		return stageDtos;
	}
}
