package us.tx.state.dfps.service.admin.daoimpl;

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

import us.tx.state.dfps.service.admin.dao.StageUnitDao;
import us.tx.state.dfps.service.admin.dto.StageUnitInDto;
import us.tx.state.dfps.service.admin.dto.StageUnitOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

@Repository
public class StageUnitDaoImpl implements StageUnitDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${StageUnitDaoImpl.getStageDetails}")
	private String getStageDetails;

	private static final Logger log = Logger.getLogger(StageUnitDaoImpl.class);

	public StageUnitDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getStageDetails Method Description: This method will get
	 * data from Stage table.
	 * 
	 * @param pInputDataRec
	 * @return List<StageUnitOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StageUnitOutDto> getStageDetails(StageUnitInDto pInputDataRec) {
		log.debug("Entering method getStageDetails in StageUnitDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getStageDetails)
				.setResultTransformer(Transformers.aliasToBean(StageUnitOutDto.class)));
		sQLQuery1.addScalar("idStage", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("tsLastUpdate", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdStageType", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("idUnit", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idCase", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idSituation", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("dtStageClose", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("cdStageClassification", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdStageCurrPriority", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdStageInitialPriority", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdStageRsnPriorityChgd", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indStageClose", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdStageCnty", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("nmStage", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdStageRegion", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtStageStart", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("cdStageProgram", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdStage", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("stagePriorityCmnts", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("stageClosureCmnts", StandardBasicTypes.STRING);
		sQLQuery1.setParameter("hI_ulIdCase", pInputDataRec.getIdCase());
		List<StageUnitOutDto> liClsc59doDto = (List<StageUnitOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liClsc59doDto) && liClsc59doDto.size() == 0) {
			throw new DataNotFoundException(messageSource.getMessage("liClsc59d.not.found.ulIdStage", null, Locale.US));
		}
		log.debug("Exiting method clsc59dQUERYdam in StageUnitDaoImpl");
		return liClsc59doDto;
	}
}
