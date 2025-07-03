package us.tx.state.dfps.service.workload.daoimpl;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.AdminAllegation;
import us.tx.state.dfps.common.domain.AdminReview;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.workload.dao.AdminReviewDao;
import us.tx.state.dfps.service.workload.dto.AdminReviewDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN03U Class
 * Description: AdminReview DAO Class Apr 3, 2017 - 3:45:39 PM
 */

@Repository
public class AdminReviewDaoImpl implements AdminReviewDao {

	@Value("${AdminReviewDaoImpl.getAdminReviewById}")
	private String getAdminReviewByIdSql;

	@Value("${AdminReviewDaoImpl.getAdminReviewOpenStagesByPerson}")
	private String getAdminReviewOpenStagesByPersonSql;

	@Value("${AdminReviewDaoImpl.getAdminReviewOpenExists}")
	private String getAdminReviewOpenExistsSql;

	@Value("${AdminReviewDaoImpl.getAdminReviewOpenByStageId}")
	private String getAdminReviewOpenByStageIdSql;

	@Value("${AdminReviewDaoImpl.getAllegationsByStageIdPersonId}")
	private String getAllegationsByStageIdPersonIdSql;

	@Autowired
	private SessionFactory sessionFactory;

	public AdminReviewDaoImpl() {

	}

	/**
	 * This dam will update rows on the Admin Review table.
	 * 
	 * Service Name: CCMN03U, DAM Name : CAUDA3D
	 * 
	 * @param adminReview
	 * @
	 */
	@Override
	public void saveAdminReview(AdminReview adminReview) {

		sessionFactory.getCurrentSession().save(adminReview);
		sessionFactory.getCurrentSession().flush();
	}

	/**
	 * This dam will update rows on the Admin Review table.
	 * 
	 * Service Name: CCMN03U, DAM Name : CAUDA3D
	 * 
	 * @param adminReview
	 * @
	 */
	@Override
	public void updateAdminReview(AdminReview adminReview) {

		sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(adminReview));

	}

	/**
	 * This dam will update rows on the Admin Review table.
	 * 
	 * Service Name: CCMN03U, DAM Name : CAUDA3D
	 * 
	 * @param adminReview
	 * @
	 */
	@Override
	public void deleteAdminReview(AdminReview adminReview) {

		sessionFactory.getCurrentSession().delete(adminReview);

	}

	/**
	 * This dam will update rows on the Admin Review table.
	 * 
	 * Service Name: CCMN03U, DAM Name : CAUDA3D
	 * 
	 * @param idAdminReview
	 * @
	 */
	@Override
	public AdminReview getAdminReviewById(Long idAdminReview) {
		AdminReview adminReview = new AdminReview();
		Query queryAdminReview = sessionFactory.getCurrentSession().createQuery(getAdminReviewByIdSql);
		queryAdminReview.setParameter("idAdminReview", idAdminReview);
		return adminReview;
	}

	@Override
	public List<Long> getAdminReviewOpenStagesByPerson(Long personId, Long stageId) {
		List<Long> stageList;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getAdminReviewOpenStagesByPersonSql)
				.addScalar("idStage",StandardBasicTypes.LONG);
		query.setParameter("personId", personId);
		query.setParameter("stageId", stageId);
		stageList = query.list();
		if (ObjectUtils.isEmpty(stageList)) {
			stageList = new ArrayList<>();
		}
		return stageList;
	}

	@Override
	public List<Long> getAdminReviewOpenExists(Long stageId) {
		List<Long> stageList;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getAdminReviewOpenExistsSql)
				.addScalar("idStage",StandardBasicTypes.LONG);
		query.setParameter("stageId", stageId);
		stageList = query.list();
		if (ObjectUtils.isEmpty(stageList)) {
			stageList = new ArrayList<>();
		}
		return stageList;
	}

	@Override
	public AdminReviewDto getAdminReviewOpenStagesByStageId(Long idStage) {
//		Query queryAdminReview = sessionFactory.getCurrentSession().createQuery(getAdminReviewOpenByStageIdSql);
//		queryAdminReview.setParameter("idStage", idStage);
//		return (AdminReview) queryAdminReview.list().get(0);


		return  (AdminReviewDto) sessionFactory.getCurrentSession().createSQLQuery(getAdminReviewOpenByStageIdSql)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idStageRelated", StandardBasicTypes.LONG)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(AdminReviewDto.class)).list().get(0);
	}

	@Override
	public List<AllegationDto> getAllegationsByStageIdPersonId(Long idStage, Long idPerson) {

		return (List<AllegationDto>) sessionFactory.getCurrentSession().createSQLQuery(getAllegationsByStageIdPersonIdSql)
				.addScalar("idAllegationStage", StandardBasicTypes.LONG)
				.addScalar("idVictim", StandardBasicTypes.LONG)
				.addScalar("idAllegedPerpetrator", StandardBasicTypes.LONG)
				.addScalar("cdAllegDisposition", StandardBasicTypes.STRING)
				.addScalar("cdAllegIncidentStage", StandardBasicTypes.STRING)
				.addScalar("cdAllegSeverity", StandardBasicTypes.STRING)
				.addScalar("cdAllegType", StandardBasicTypes.STRING)
				.addScalar("allegDuration", StandardBasicTypes.STRING)
				.addScalar("idCase", StandardBasicTypes.LONG)
				.setParameter("idStage", idStage)
				.setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(AllegationDto.class)).list();
	}

	/**
	 * This dam will update rows on the Admin Allegation table.
	 *
	 * Service Name: CCFC42S, DAM Name : CMSC43D
	 *
	 * @param AdminAllegation
	 * @
	 */
	@Override
	public void saveAdminAllegation(AdminAllegation adminAllegation) {

		sessionFactory.getCurrentSession().save(adminAllegation);
		sessionFactory.getCurrentSession().flush();

	}
}
