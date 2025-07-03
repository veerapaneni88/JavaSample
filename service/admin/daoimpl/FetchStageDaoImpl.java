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

import us.tx.state.dfps.service.admin.dao.FetchStageDao;
import us.tx.state.dfps.service.admin.dto.FetchStagediDto;
import us.tx.state.dfps.service.admin.dto.FetchStagedoDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This method
 * fetches stage details for a given stage Id Aug 7, 2017- 6:45:29 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class FetchStageDaoImpl implements FetchStageDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${Cint21dDaoImpl.getStageDtls}")
	private transient String getStageDtls;

	private static final Logger log = Logger.getLogger(FetchStageDaoImpl.class);

	/**
	 * Method Name: getStageDetails Method Description: Method to fetch the
	 * stage details for a given stage Id
	 * 
	 * @param pInputDataRec
	 * @return List<FetchStagedoDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FetchStagedoDto> getStageDetails(FetchStagediDto pInputDataRec) {
		Query queryStageValues = sessionFactory.getCurrentSession().createSQLQuery(getStageDtls)
				.addScalar("szCdStage", StandardBasicTypes.STRING)
				.addScalar("szCdStageClassification", StandardBasicTypes.STRING)
				.addScalar("szCdStageCnty", StandardBasicTypes.STRING)
				.addScalar("szCdStageCurrPriority", StandardBasicTypes.STRING)
				.addScalar("szCdStageInitialPriority", StandardBasicTypes.STRING)
				.addScalar("szCdStageProgram", StandardBasicTypes.STRING)
				.addScalar("szCdStageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("szCdStageRegion", StandardBasicTypes.STRING)
				.addScalar("szCdStageRsnPriorityChgd", StandardBasicTypes.STRING)
				.addScalar("szCdStageType", StandardBasicTypes.STRING)
				.addScalar("dtDtStageClose", StandardBasicTypes.DATE)
				.addScalar("dtDtStageStart", StandardBasicTypes.DATE).addScalar("ulIdCase", StandardBasicTypes.LONG)
				.addScalar("ulIdSituation", StandardBasicTypes.LONG).addScalar("ulIdStage", StandardBasicTypes.LONG)
				.addScalar("ulIdUnit", StandardBasicTypes.LONG).addScalar("bIndStageClose", StandardBasicTypes.STRING)
				.addScalar("szNmStage", StandardBasicTypes.STRING)
				.addScalar("szTxtStagePriorityCmnts", StandardBasicTypes.STRING)
				.addScalar("szTxtStageClosureCmnts", StandardBasicTypes.STRING)
				.addScalar("bIndIncmgAssignPn", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(FetchStagedoDto.class));

		queryStageValues.setParameter("hI_ulIdStage", pInputDataRec.getUlIdStage());
		List<FetchStagedoDto> stageValues = (List<FetchStagedoDto>) queryStageValues.list();
		if (TypeConvUtil.isNullOrEmpty(stageValues)) {
			throw new DataNotFoundException(messageSource.getMessage("stage.not.found.attributes", null, Locale.US));
		}

		return stageValues;
	}

}
