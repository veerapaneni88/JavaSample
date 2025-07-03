package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.common.domain.Workload;
import us.tx.state.dfps.service.admin.dao.StageStagePersonLinkDao;
import us.tx.state.dfps.service.admin.dto.StageStagePersonLinkInDto;
import us.tx.state.dfps.service.admin.dto.StageStagePersonLinkOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This query
 * will retrieve rows from the STAGE PERSON LINK based on ID_PERSON passed into
 * the dao. The DAOimpl will retrieve the row of the primary worker in the
 * subcare, adoption, or post-adoption stage, where the stage is open,
 * associated with the primary child from input. Aug 10, 2017- 11:35:23 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class StageStagePersonLinkDaoImpl implements StageStagePersonLinkDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${StageStagePersonLinkDaoImpl.retrieveStagePersonLinkPID}")
	private String retrieveStagePersonLinkPID;

	private static final Logger log = Logger.getLogger(StageStagePersonLinkDaoImpl.class);

	public StageStagePersonLinkDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: retrieveStagePersonLinkPID Method Description: It retrieves
	 * StagePersonLink record for provided person id.
	 * 
	 * @param pInputDataRec
	 * @return List<Clsc72doDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StageStagePersonLinkOutDto> retrieveStagePersonLinkPID(StageStagePersonLinkInDto pInputDataRec) {
		log.debug("Entering method StageStagePersonLinkQUERYdam in StageStagePersonLinkDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(retrieveStagePersonLinkPID)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("cdStage", StandardBasicTypes.STRING)
				.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(StageStagePersonLinkOutDto.class)));
		List<StageStagePersonLinkOutDto> liClsc72doDto = (List<StageStagePersonLinkOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liClsc72doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Clsc72dDaoImpl.no.stage.person.record.present", null, Locale.US));
		}
		log.debug("Exiting method StageStagePersonLinkQUERYdam in StageStagePersonLinkDaoImpl");
		return liClsc72doDto;
	}

	/**
	 * Method Name: getPrimaryWorkerIdForStage
	 * Method Description: Retrieve the primary (or historical primary) worker ID for a stage.
	 *
	 * @param ulIdStage the stage to search
	 * @return personId of the person who is the primary or historical primary for the stage.
	 */
	@Override
	public long getPrimaryWorkerIdForStage(Long ulIdStage) {
		long ulIdPersonPrimaryWorker = 0;
		String [] roleArray = {"PR", "HP"};
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);
		criteria.add(Restrictions.eq("idStage", ulIdStage));
		criteria.add(Restrictions.in("cdStagePersRole", roleArray));

		criteria.setProjection(Projections.property("idPerson"));

		// There is a Hibernate bug where using projection and uniqueResult causes NPR if no rows are
		// returned. We work around the bug by using list()
		List<Long> personIdList = criteria.list();
		if (personIdList != null && personIdList.size() > 0) {
			ulIdPersonPrimaryWorker = personIdList.get(0);
		}
		return ulIdPersonPrimaryWorker;
	}
}
