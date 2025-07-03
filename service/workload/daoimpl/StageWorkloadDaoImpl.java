package us.tx.state.dfps.service.workload.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.Query;
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

import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.workload.dao.StageWorkloadDao;
import us.tx.state.dfps.service.workload.dto.RcciMrefDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN20S Class
 * Description: Operations for Stage Apr 14, 2017 - 12:19:52 PM
 */
@Repository
public class StageWorkloadDaoImpl implements StageWorkloadDao {

	@Autowired
	MessageSource messageSource;

	@Value("${StageDaoImpl.getStageById}")
	private String getStageByIdSql;

	@Value("${StageDaoImpl.getStagesByCaseId}")
	private String getStagesByCaseIdSql;

	@Value("${StageDaoImpl.searchStageByNameAndPersonId}")
	private String searchStageByNameAndPersonIdSql;

	@Value("${StageDaoImpl.updateStagesByUnitId}")
	private String updateStagesByUnitIdSql;

	@Value("${StageDaoImpl.updateStageCloseByStageId}")
	private String updateStageCloseByStageIdSql;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${StageDaoImpl.getStagePersonLinkByStageRole}")
	private String getStagePersonLinkByStageRoleSql;

	@Value("${StageDaoImpl.getRcciMrefs}")
	private String getRcciMrefs;

	@Value("${StageDaoImpl.getRcciMrefsGroupBy}")
	private String getRcciMrefsGroupBy;

	public StageWorkloadDaoImpl() {

	}

	/**
	 * 
	 * Method Description:getStageById
	 * 
	 * @param id
	 * @return @ @
	 */
	// CINT06S
	@Override
	public Stage getStageById(Long id) {

		Stage stage = null;

		Query queryStage = sessionFactory.getCurrentSession().createQuery(getStageByIdSql);
		queryStage.setParameter("idSearch", id);

		stage = (Stage) queryStage.uniqueResult();

		if (TypeConvUtil.isNullOrEmpty(stage)) {
			throw new DataNotFoundException(messageSource.getMessage("stage.not.found.attributes", null, Locale.US));
		}

		return stage;

	}

	/**
	 * 
	 * Method Description:getStagesByCaseId
	 * 
	 * @param caseId
	 * @return @ @
	 */
	// CCMN20S
	@Override
	@SuppressWarnings("unchecked")
	public List<Stage> getStagesByCaseId(Long caseId) {

		List<Stage> stages = null;

		Query queryStage = sessionFactory.getCurrentSession().createQuery(getStagesByCaseIdSql);
		queryStage.setParameter("idSearch", caseId);
		queryStage.setMaxResults(100);
		stages = queryStage.list();

		if (TypeConvUtil.isNullOrEmpty(stages)) {
			throw new DataNotFoundException(messageSource.getMessage("stage.not.found.attributes", null, Locale.US));
		}

		return stages;
	}

	/**
	 * 
	 * Method Description:searchStageIdsFromLinkByPersonId
	 * 
	 * @param personId
	 * @return @ @
	 */
	// CCMN88S
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> searchStageIdsFromLinkByPersonId(Long personId) {

		List<Long> stageIds = new ArrayList<Long>();

		Criteria cr = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class)
				.setProjection(Projections.projectionList().add(Projections.property("idStage"), "idStage"));
		if (personId != null) {
			cr.add(Restrictions.eq("idPerson", personId));
			cr.setMaxResults(100);

			stageIds = (List<Long>) cr.list();

		}

		if (TypeConvUtil.isNullOrEmpty(stageIds)) {
			throw new DataNotFoundException(messageSource.getMessage("stage.not.found.attributes", null, Locale.US));
		}

		return stageIds;

	}

	/**
	 * 
	 * Method Description:searchStageIdsFromLinkByPersonIdAndStageRole
	 * 
	 * @param personId
	 * @param stageRole
	 * @return @ @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> searchStageIdsFromLinkByPersonIdAndStageRole(Long personId, String stageRole) {
		List<Long> stages = new ArrayList<Long>();

		Criteria cr = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class).setProjection(
				Projections.projectionList().add(Projections.property("idStagePersonLink"), "idStagePersonLink"));

		if (personId != null) {
			cr.add(Restrictions.eq("idPerson", personId));
			cr.add(Restrictions.eq("cdStagePersRole", stageRole));
		}
		stages = (List<Long>) cr.list();

		if (TypeConvUtil.isNullOrEmpty(stages)) {
			throw new DataNotFoundException(messageSource.getMessage("stage.not.found.attributes", null, Locale.US));
		}

		return stages;

	}

	/**
	 * 
	 * Method Description:searchStageByNameAndPersonId
	 * 
	 * @param personFull
	 * @param ulIdPerson
	 * @return
	 * @ @ @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Stage> searchStageByNameAndPersonId(String personFull, Long ulIdPerson) {
		List<Stage> stages = null;
		List<String> roles = new ArrayList<String>();

		Query queryStage = sessionFactory.getCurrentSession().createQuery(searchStageByNameAndPersonIdSql);
		queryStage.setParameter("personFull", personFull);
		queryStage.setParameter("maxDate", ServiceConstants.GENERIC_END_DATE);
		queryStage.setParameter("program1", ServiceConstants.STAGE_PROGRAM_APS);
		queryStage.setParameter("program2", ServiceConstants.STAGE_PROGRAM_AFC);
		queryStage.setParameter("idPerson", ulIdPerson);
		roles.add(ServiceConstants.STAGE_PERS_ROLE_VC);
		roles.add(ServiceConstants.STAGE_PERS_ROLE_VP);
		roles.add(ServiceConstants.STAGE_PERS_ROLE_DB);
		roles.add(ServiceConstants.STAGE_PERS_ROLE_DV);
		roles.add(ServiceConstants.STAGE_PERS_ROLE_CL);
		roles.add(ServiceConstants.STAGE_PERS_ROLE_NO);
		roles.add(ServiceConstants.STAGE_PERS_ROLE_UK);
		queryStage.setParameterList("roles", roles);
		stages = queryStage.list();

		if (TypeConvUtil.isNullOrEmpty(stages)) {
			throw new DataNotFoundException(messageSource.getMessage("stage.not.found.attributes", null, Locale.US));
		}

		return stages;
	}

	/**
	 * 
	 * Method Description:updateStage
	 * 
	 * @param stage
	 * @ @
	 */
	@Override
	public void updateStage(Stage stage) {

		sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(stage));

	}

	/**
	 * 
	 * Method Description:getStagesByUnitId
	 * 
	 * @param unitId
	 * @return @ @
	 */
	@Override
	public void updateStagesByUnitId(Long unitId) {
		Long value1 = null;
		String value2 = null;

		Query queryStage = sessionFactory.getCurrentSession().createQuery(updateStagesByUnitIdSql);
		queryStage.setParameter("unitId", unitId);
		queryStage.setParameter("value1", value1);
		queryStage.setParameter("value2", value2);
		queryStage.executeUpdate();

	}

	/**
	 * If the required function is UPDATE: This DAM performs an update of a
	 * record in the STAGE table. If the ID STAGE in the record equals the ID
	 * STAGE value passed in the Input Message, then the DT STAGE CLOSE for the
	 * selected record is updated to the current system's date
	 * 
	 * Service Name - CCMN03U, CCMN88S, DAM Name - CCMND4D
	 * updateStageCloseByStageId
	 * 
	 * @param idStage
	 * @ @
	 */
	// CCMND4D
	@Override
	public void updateStageCloseByStageId(Long idStage, Date dtStageClose, String cdStageReasonClosed) {
		Stage stageUpdate = (Stage) sessionFactory.getCurrentSession().load(Stage.class, idStage);
		stageUpdate.setIndStageClose(ServiceConstants.Y);
		stageUpdate.setDtStageClose(dtStageClose);
		stageUpdate.setCdStageReasonClosed(cdStageReasonClosed);
		updateStage(stageUpdate);

	}

	/**
	 * This DAM will receive ID STAGE from the service and return the associated
	 * record from the STAGE_PERSON_LINK table where the staff person'r role is
	 * "Primary" (PR).
	 * 
	 * Service Name - CCMN88S, CCMN03U, DAM Name - CCMNG2D
	 * getStagePersonLinkByStageRole
	 * 
	 * @param idStage
	 * @return @ @
	 */
	// CCMNG2D
	@Override
	public StagePersonLinkDto getStagePersonLinkByStageRole(Long idStage) {
		StagePersonLinkDto stagePersonLinkDto = new StagePersonLinkDto();

		Query queryStage = sessionFactory.getCurrentSession().createSQLQuery(getStagePersonLinkByStageRoleSql)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("indStagePersInLaw", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersSearchInd", StandardBasicTypes.STRING)
				.addScalar("stagePersNotes", StandardBasicTypes.STRING)
				.addScalar("dtStagePersLink", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("indStagePersReporter", StandardBasicTypes.STRING)
				.addScalar("indStagePersEmpNew", StandardBasicTypes.STRING)
				.addScalar("idStagePersonLink", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(StagePersonLinkDto.class));

		queryStage.setParameter("idStage", idStage);
		queryStage.setParameter("stageRole", ServiceConstants.STAGE_PERS_ROLE_PR);
		stagePersonLinkDto = (StagePersonLinkDto) queryStage.uniqueResult();

		/*
		 * if (TypeConvUtil.isNullOrEmpty(stagePersonLinkDto)) { throw new
		 * DataNotFoundException(messageSource.getMessage(
		 * "stage.not.found.attributes", null, Locale.US)); }
		 */

		return stagePersonLinkDto;
	}

	// There were cases where get by stage id seemed more appropriate, but the query pulled too much data so it's
	// actually better to pull more data by searching by case and ignore the stuff we don't need.
	@Override
	public List<RcciMrefDto> getRcciMrefDataByCaseList(List<Long> idList) {
		List<RcciMrefDto> rcciMrefDtoList = null;
		boolean commaHasBeenSkipped = false;
		StringBuilder queryString = null;
		if (!ObjectUtils.isEmpty(idList)) {
			queryString = new StringBuilder(getRcciMrefs);
			queryString.append("(");
			for (Long currWorkloadCaseId : idList) {
				if (!commaHasBeenSkipped) {
					commaHasBeenSkipped = true;
					queryString.append(currWorkloadCaseId);
				} else {
					queryString.append(",").append(currWorkloadCaseId);
				}
			}
			queryString.append(")");
			queryString.append(getRcciMrefsGroupBy);
			Query tempQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryString.toString())
					.addScalar("rcciMrefCnt", StandardBasicTypes.INTEGER)
					.addScalar("idStage", StandardBasicTypes.LONG)
					.addScalar("nbrRsrcFacilCapacity", StandardBasicTypes.INTEGER)
					.addScalar("cdRsrcFacilType", StandardBasicTypes.STRING)
					.setResultTransformer(Transformers.aliasToBean(RcciMrefDto.class)));
			rcciMrefDtoList = (List<RcciMrefDto>) tempQuery.list();
		}
		return rcciMrefDtoList;
	}

	@Override
	public void updateClosureReason(Long idStage, String cdStageReasonClosed) {
		Stage stageUpdate = (Stage) sessionFactory.getCurrentSession().load(Stage.class, idStage);
		stageUpdate.setCdStageReasonClosed(cdStageReasonClosed);
		updateStage(stageUpdate);

	}
}
