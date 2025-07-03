package us.tx.state.dfps.service.admin.daoimpl;

import java.util.ArrayList;
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

import us.tx.state.dfps.service.admin.dao.StageSituationDao;
import us.tx.state.dfps.service.admin.dto.StageSituationInDto;
import us.tx.state.dfps.service.admin.dto.StageSituationOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO Impl
 * for getting stage details> Aug 4, 2017- 12:27:23 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class StageSituationDaoImpl implements StageSituationDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(StageSituationDaoImpl.class);

	@Value("${StageSituationDaoImpl.getStageDetails}")
	private transient String getStageDetails;

	public StageSituationDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getStageDetails Method Description: This method will get
	 * data from STAGE table.
	 * 
	 * @param pInputDataRec
	 * @return List<StageSituationOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StageSituationOutDto> getStageDetails(StageSituationInDto pInputDataRec) {
		log.debug("Entering method StageSituationQUERYdam in StageSituationDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getStageDetails)
				.setResultTransformer(Transformers.aliasToBean(StageSituationOutDto.class)));
		sQLQuery1.addScalar("idStage", StandardBasicTypes.LONG).addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdStageType", StandardBasicTypes.STRING).addScalar("idUnit", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("dtStageClose", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdStageClassification", StandardBasicTypes.STRING)
				.addScalar("cdStageCurrPriority", StandardBasicTypes.STRING)
				.addScalar("cdStageInitialPriority", StandardBasicTypes.STRING)
				.addScalar("cdStageRsnPriorityChgd", StandardBasicTypes.STRING)
				.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("indStageClose", StandardBasicTypes.STRING)
				.addScalar("stagePriorityCmnts", StandardBasicTypes.STRING)
				.addScalar("cdStageCnty", StandardBasicTypes.STRING).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("cdStageRegion", StandardBasicTypes.STRING)
				.addScalar("dtStageStart", StandardBasicTypes.TIMESTAMP)
				.addScalar("idSituation", StandardBasicTypes.LONG)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING).addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("stageClosureCmnts", StandardBasicTypes.STRING)
				.setParameter("hI_ulIdCase", pInputDataRec.getIdCase());
		List<StageSituationOutDto> liClss30doDto = new ArrayList<>();
		liClss30doDto = (List<StageSituationOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liClss30doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Clss30dDaoImpl.not.found.stage", null, Locale.US));
		}
		log.debug("Exiting method StageSituationQUERYdam in StageSituationDaoImpl");
		return liClss30doDto;
	}
}
