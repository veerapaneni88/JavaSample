package us.tx.state.dfps.service.conservatorship.daoimpl;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.conservatorship.dao.StageUpdByStageStartIdDao;
import us.tx.state.dfps.service.cvs.dto.StageUpdByStageStartIdInDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Class
 * Updates Stage Details Aug 14, 2017- 9:06:16 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class StageUpdByStageStartIdDaoImpl implements StageUpdByStageStartIdDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${StageUpdByStageStartIdDaoImpl.setStgDetails}")
	private String setStgDetails;

	private static final Logger log = Logger.getLogger(StageUpdByStageStartIdDaoImpl.class);

	/**
	 * Description:Method to Update Stage details
	 * 
	 * @param stageUpdByStageStartIdInDto
	 * @return void @
	 */
	@Override
	public int setStgDetails(StageUpdByStageStartIdInDto stageUpdByStageStartIdInDto) {
		log.debug("Entering method setStgDetails in StageUpdByStageStartIdDaoImpl");
		Query Query1 = (sessionFactory.getCurrentSession().createQuery(setStgDetails)
				.setParameter("hI_dtDtStageStart", stageUpdByStageStartIdInDto.getDtStageStart())
				.setParameter("hI_ulIdStage", stageUpdByStageStartIdInDto.getIdStage()));
		int rowCount = Query1.executeUpdate();
		if (rowCount <= 0) {
			throw new DataNotFoundException(messageSource.getMessage("Cinvc4d.not.updated", null, Locale.US));
		}
		log.debug("Exiting method setStgDetails in StageUpdByStageStartIdDaoImpl");
		return rowCount;
	}

	/**
	 * This method updates the stage
	 * 
	 * @param stageUpdByStageStartIdInDto
	 * @return @
	 */
	@Override
	public long updateStage(StageUpdByStageStartIdInDto stageUpdByStageStartIdInDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Stage.class);
		if (ServiceConstants.REQ_FUNC_CD_UPDATE
				.equals(stageUpdByStageStartIdInDto.getArchInputStruct().getCreqFuncCd())) {
			criteria.add(Restrictions.eq("idStage", stageUpdByStageStartIdInDto.getIdStage()));
			Stage stage = (Stage) criteria.uniqueResult();
			stage.setDtStageStart(stageUpdByStageStartIdInDto.getDtStageStart());
			sessionFactory.openSession().saveOrUpdate(stage);
		}
		return criteria.list().size();
	}
}
