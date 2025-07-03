package us.tx.state.dfps.service.stageutility.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.stageutility.dao.StageUtilityDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * has functions to access Stage related information. Oct 12, 2017- 3:13:52 PM ©
 * 2017 Texas Department of Family and Protective Services
 *  * * * *************** Change History ****************************************
 * 11/1/2019  kanakas artf129782: Licensing Investigation Conclusion
 */
@Repository
public class StageUtilityDaoImpl implements StageUtilityDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private MessageSource messageSource;

	@Value("${StageUtilityDaoImpl.retrieveStageInfo}")
	private String retrieveStageInfo;

	@Value("${StageUtilityDaoImpl.getDeathReasonMissing}")
	private String getDeathReasonMissing;

	@Value("${StageUtilityDaoImpl.getCheckedOutStagesForPerson}")
	private String getCheckedOutStagesForPerson;

	@Value("${StageUtilityDaoImpl.getStageListForPC}")
	private String getStageListForPC;

	@Value("${StageUtilityDaoImpl.isPrimaryChildInOpenStage}")
	private String isPrimaryChildInOpenStage;

	@Value("${StageUtilityDaoImpl.findWorkersForStage}")
	private String findWorkersForStage;

	@Value("${StageUtilityDaoImpl.getStageListForChild}")
	private String getStageListForChild;

	/**
	 * 
	 * Method Name: retrieveStageInfo Method Description: This method retrieves
	 * information from Stage table using idStage.
	 * 
	 * @param idStage
	 * @return StageValueBeanDto @
	 */
	@Override
	public StageValueBeanDto retrieveStageInfo(Long idStage) {

		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(retrieveStageInfo)
				.setResultTransformer(Transformers.aliasToBean(StageValueBeanDto.class));
		sqlQuery.addScalar("idStage", StandardBasicTypes.LONG);
		sqlQuery.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
		sqlQuery.addScalar("cdStageType", StandardBasicTypes.STRING);
		sqlQuery.addScalar("idUnit", StandardBasicTypes.LONG);
		sqlQuery.addScalar("idCase", StandardBasicTypes.LONG);
		sqlQuery.addScalar("idSituation", StandardBasicTypes.LONG);
		sqlQuery.addScalar("dtStageClose", StandardBasicTypes.DATE);
		sqlQuery.addScalar("cdStageClassification", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdStageCurrPriority", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdStageInitialPriority", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdStageRsnPriorityChgd", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING);
		sqlQuery.addScalar("indStageClose", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdStageCnty", StandardBasicTypes.STRING);
		sqlQuery.addScalar("nmStage", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdStageRegion", StandardBasicTypes.STRING);
		sqlQuery.addScalar("dtStageStart", StandardBasicTypes.DATE);
		sqlQuery.addScalar("cdStageProgram", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdStage", StandardBasicTypes.STRING);
		sqlQuery.addScalar("stagePriorityCmnts", StandardBasicTypes.STRING);
		sqlQuery.addScalar("stageClosureCmnts", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdClientAdvised", StandardBasicTypes.STRING);
		sqlQuery.addScalar("indEcs", StandardBasicTypes.STRING);
		sqlQuery.addScalar("indEcsVer", StandardBasicTypes.STRING);
		sqlQuery.addScalar("indAssignStage", StandardBasicTypes.STRING);
		sqlQuery.addScalar("dtClientAdvised", StandardBasicTypes.DATE);
		sqlQuery.addScalar("dtMultiRef", StandardBasicTypes.DATE);
		sqlQuery.addScalar("dtStageCreated", StandardBasicTypes.DATE);
		sqlQuery.addScalar("indSecondApprover", StandardBasicTypes.STRING);
		sqlQuery.addScalar("indScreened", StandardBasicTypes.STRING);
		sqlQuery.addScalar("indFoundOpenCaseAtIntake", StandardBasicTypes.STRING);
		sqlQuery.addScalar("indFormallyScreened", StandardBasicTypes.STRING);

		sqlQuery.setParameter("idStage", idStage);
		StageValueBeanDto stageValueBeanDto = (StageValueBeanDto) sqlQuery.uniqueResult();

		if (TypeConvUtil.isNullOrEmpty(stageValueBeanDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("stageValueBeanDto.idStage.NotFound", null, Locale.US));
		}

		return stageValueBeanDto;
	}

	
	/**
	 * Method Name: findPrimaryChildForStage Method Description: This method
	 * returns Primary Child for the Stage.
	 * 
	 * @param idStage
	 * @return Long @
	 */
	@Override
	public Long findPrimaryChildForStage(Long idStage) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);

		criteria.add(Restrictions.eq("idStage", idStage));
		criteria.add(Restrictions.eq("cdStagePersRole", ServiceConstants.CROLES_PC));
		criteria.setProjection(Projections.property("idPerson"));

		Long personId = (Long) criteria.list().get(0);
		if (TypeConvUtil.isNullOrEmpty(personId)) {
			throw new DataNotFoundException(messageSource.getMessage("personId.NotFound", null, Locale.US));
		}
		return personId;

	}

	/**
	 * Method Name: findWorkersForStage Method Description:This method returns
	 * the primary and secondary workers assigned to the stage with the given
	 * security profiles. If the security profile list parameter is empty or
	 * null, it returns all the primary and secondary workers assigned to the
	 * stage.
	 * 
	 * @param idStage
	 * @param secProfiles
	 * @return List<Long> @
	 */
	@Override
	public List<Long> findWorkersForStage(Long idStage, List<String> secProfiles) {

		List<Long> eligWorkers = new ArrayList<>();
		String columnName = ServiceConstants.COLUMN_NAME;
		StringBuilder dynamicSQL = new StringBuilder();
		dynamicSQL.append(findWorkersForStage);
		dynamicSQL.append(createMutiValueWhereClause(columnName, secProfiles));

		SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(dynamicSQL.toString());
		sqlQuery.setParameter("idStage", idStage);

		List<BigDecimal> result = sqlQuery.list();

		if (TypeConvUtil.isNullOrEmpty(result)) {
			throw new DataNotFoundException(messageSource.getMessage("eligWorkers.idStage.NotFound", null, Locale.US));
		}
		for (BigDecimal idPerson : result) {
			if (!eligWorkers.contains(idPerson.longValue())) {
				eligWorkers.add(idPerson.longValue());
			}

		}

		return eligWorkers;
	}

	/**
	 * Method Name: createMutiValueWhereClause Method Description: This method
	 * will create multiple where clause for query
	 * 
	 * @param string
	 * @param secProfiles
	 * @return String
	 */
	private String createMutiValueWhereClause(String string, List<String> secProfiles) {

		StringBuilder valueWhereClause = new StringBuilder();
		if (secProfiles.size() > 0) {
			valueWhereClause.append(" AND ( ");
			for (int i = 0; i < secProfiles.size(); i++) {
				valueWhereClause.append(string).append(" , ").append(secProfiles.get(i)).append(", 1) = 1 ");
				if (i < secProfiles.size() - 1) {
					valueWhereClause.append(" OR ");
				}
			}
			valueWhereClause.append(" ) ");
		}
		return valueWhereClause.toString();

	}

	/**
	 * Method Name: getDeathReasonMissing Method Description: This method gets
	 * count of persons on stage with DOD but no death code, for use in
	 * validation of INV stage closures.
	 * 
	 * @param idStage
	 * @return Long @
	 */
	@Override
	public Long getDeathReasonMissing(Long idStage) {

		SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(getDeathReasonMissing);
		sqlQuery.setParameter("idStage", idStage);

		BigDecimal personCount = (BigDecimal) sqlQuery.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(personCount)) {
			throw new DataNotFoundException(messageSource.getMessage("personCount.NotFound", null, Locale.US));
		}
		return personCount.longValue();
	}

	/**
	 * Method Name: getCheckedOutStagesForPerson Method Description: This method
	 * Fetches the list of stages which has this person and are checkout to MPS
	 * 
	 * @param idPerson
	 * @return ArrayList<StagePersonValueDto @
	 */
	@Override
	public ArrayList<StagePersonValueDto> getCheckedOutStagesForPerson(Long idPerson) {

		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCheckedOutStagesForPerson)
				.setResultTransformer(Transformers.aliasToBean(StagePersonValueDto.class));
		sqlQuery.addScalar("indSensitiveCase", StandardBasicTypes.STRING);
		sqlQuery.addScalar("idPrimaryWorker", StandardBasicTypes.LONG);
		sqlQuery.addScalar("nmPersonFull", StandardBasicTypes.STRING);
		sqlQuery.addScalar("idPerson", StandardBasicTypes.LONG);
		sqlQuery.addScalar("cdStage", StandardBasicTypes.STRING);
		sqlQuery.addScalar("idStage", StandardBasicTypes.LONG);
		sqlQuery.addScalar("idCase", StandardBasicTypes.LONG);
		sqlQuery.addScalar("cdStageRegion", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdStageProgram", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdStagePersRole", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdStagePersType", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING);
		sqlQuery.addScalar("dtStageCheckout", StandardBasicTypes.DATE);

		sqlQuery.setParameter("idPerson", idPerson);

		ArrayList<StagePersonValueDto> stagePersonValueDtos = (ArrayList<StagePersonValueDto>) sqlQuery.list();
		if (TypeConvUtil.isNullOrEmpty(stagePersonValueDtos)) {
			throw new DataNotFoundException(
					messageSource.getMessage("stagePersonValueDtos.idPerson.NotFound", null, Locale.US));
		}
		return stagePersonValueDtos;
	}

	/**
	 * Method Name: getStageListForPC Method Description: This method is to
	 * check if a person in Primary Child in open stage
	 * 
	 * @param idPerson
	 * @param b
	 * @return ArrayList<StageValueBeanDto> @
	 */
	@Override
	public ArrayList<StageValueBeanDto> getStageListForPC(Long idPerson, Boolean b) {

		ArrayList<StageValueBeanDto> stageValueBeanDtos = null;
		if (b) {
			SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getStageListForPC)
					.setResultTransformer(Transformers.aliasToBean(StageValueBeanDto.class));
			sqlQuery.addScalar("idStage", StandardBasicTypes.LONG);
			sqlQuery.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
			sqlQuery.addScalar("cdStageType", StandardBasicTypes.STRING);
			sqlQuery.addScalar("idUnit", StandardBasicTypes.LONG);
			sqlQuery.addScalar("idCase", StandardBasicTypes.LONG);
			sqlQuery.addScalar("idSituation", StandardBasicTypes.LONG);
			sqlQuery.addScalar("dtStageClose", StandardBasicTypes.DATE);
			sqlQuery.addScalar("cdStageCurrPriority", StandardBasicTypes.STRING);
			sqlQuery.addScalar("cdStageRsnPriorityChgd", StandardBasicTypes.STRING);
			sqlQuery.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING);
			sqlQuery.addScalar("indStageClose", StandardBasicTypes.STRING);
			sqlQuery.addScalar("cdStageCnty", StandardBasicTypes.STRING);
			sqlQuery.addScalar("nmStage", StandardBasicTypes.STRING);
			sqlQuery.addScalar("cdStageRegion", StandardBasicTypes.STRING);
			sqlQuery.addScalar("dtStageStart", StandardBasicTypes.DATE);
			sqlQuery.addScalar("cdStageProgram", StandardBasicTypes.STRING);
			sqlQuery.addScalar("cdStage", StandardBasicTypes.STRING);
			sqlQuery.addScalar("stagePriorityCmnts", StandardBasicTypes.STRING);
			sqlQuery.addScalar("stageClosureCmnts", StandardBasicTypes.STRING);
			sqlQuery.addScalar("cdClientAdvised", StandardBasicTypes.STRING);
			sqlQuery.addScalar("indEcs", StandardBasicTypes.STRING);
			sqlQuery.addScalar("indEcsVer", StandardBasicTypes.STRING);
			sqlQuery.addScalar("indAssignStage", StandardBasicTypes.STRING);
			sqlQuery.addScalar("dtClientAdvised", StandardBasicTypes.DATE);
			sqlQuery.addScalar("dtMultiRef", StandardBasicTypes.DATE);
			sqlQuery.addScalar("dtStageCreated", StandardBasicTypes.DATE);
			sqlQuery.addScalar("indSecondApprover", StandardBasicTypes.STRING);
			sqlQuery.addScalar("indScreened", StandardBasicTypes.STRING);
			sqlQuery.addScalar("indFoundOpenCaseAtIntake", StandardBasicTypes.STRING);
			sqlQuery.addScalar("indFormallyScreened", StandardBasicTypes.STRING);

			sqlQuery.setParameter("idPerson", idPerson);

			stageValueBeanDtos = (ArrayList<StageValueBeanDto>) sqlQuery.list();

			if (TypeConvUtil.isNullOrEmpty(stageValueBeanDtos)) {
				throw new DataNotFoundException(
						messageSource.getMessage("stageValueBeanDto.idPerson.NotFound", null, Locale.US));
			}
		} else {
			SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(isPrimaryChildInOpenStage)
					.setResultTransformer(Transformers.aliasToBean(StageValueBeanDto.class));
			sqlQuery.addScalar("idStage", StandardBasicTypes.LONG);
			sqlQuery.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
			sqlQuery.addScalar("cdStageType", StandardBasicTypes.STRING);
			sqlQuery.addScalar("idUnit", StandardBasicTypes.LONG);
			sqlQuery.addScalar("idCase", StandardBasicTypes.LONG);
			sqlQuery.addScalar("idSituation", StandardBasicTypes.LONG);
			sqlQuery.addScalar("dtStageClose", StandardBasicTypes.DATE);
			sqlQuery.addScalar("cdStageCurrPriority", StandardBasicTypes.STRING);
			sqlQuery.addScalar("cdStageRsnPriorityChgd", StandardBasicTypes.STRING);
			sqlQuery.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING);
			sqlQuery.addScalar("indStageClose", StandardBasicTypes.STRING);
			sqlQuery.addScalar("cdStageCnty", StandardBasicTypes.STRING);
			sqlQuery.addScalar("nmStage", StandardBasicTypes.STRING);
			sqlQuery.addScalar("cdStageRegion", StandardBasicTypes.STRING);
			sqlQuery.addScalar("dtStageStart", StandardBasicTypes.DATE);
			sqlQuery.addScalar("cdStageProgram", StandardBasicTypes.STRING);
			sqlQuery.addScalar("cdStage", StandardBasicTypes.STRING);
			sqlQuery.addScalar("stagePriorityCmnts", StandardBasicTypes.STRING);
			sqlQuery.addScalar("stageClosureCmnts", StandardBasicTypes.STRING);
			sqlQuery.addScalar("cdClientAdvised", StandardBasicTypes.STRING);
			sqlQuery.addScalar("indEcs", StandardBasicTypes.STRING);
			sqlQuery.addScalar("indEcsVer", StandardBasicTypes.STRING);
			sqlQuery.addScalar("indAssignStage", StandardBasicTypes.STRING);
			sqlQuery.addScalar("dtClientAdvised", StandardBasicTypes.DATE);
			sqlQuery.addScalar("dtMultiRef", StandardBasicTypes.DATE);
			sqlQuery.addScalar("dtStageCreated", StandardBasicTypes.DATE);
			sqlQuery.addScalar("indSecondApprover", StandardBasicTypes.STRING);
			sqlQuery.addScalar("indScreened", StandardBasicTypes.STRING);
			sqlQuery.addScalar("indFoundOpenCaseAtIntake", StandardBasicTypes.STRING);
			sqlQuery.addScalar("indFormallyScreened", StandardBasicTypes.STRING);

			sqlQuery.setParameter("idPerson", idPerson);

			stageValueBeanDtos = (ArrayList<StageValueBeanDto>) sqlQuery.list();

			if (TypeConvUtil.isNullOrEmpty(stageValueBeanDtos)) {
				throw new DataNotFoundException(
						messageSource.getMessage("stageValueBeanDto.idPerson.NotFound", null, Locale.US));
			}
		}

		return stageValueBeanDtos;
	}

	/**
	 * Method Name: getStageListForPC Method Description: This method is to
	 * check if a Child in open stage
	 * 
	 * @param idPerson
	 * @param b
	 * @return ArrayList<StageValueBeanDto> @
	 */
	@Override
	public ArrayList<StageValueBeanDto> getStageListForChild(Long idPerson) {

		ArrayList<StageValueBeanDto> stageValueBeanDtos = null;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getStageListForPC)
				.setResultTransformer(Transformers.aliasToBean(StageValueBeanDto.class));
		sqlQuery.addScalar("idStage", StandardBasicTypes.LONG);
		sqlQuery.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
		sqlQuery.addScalar("cdStageType", StandardBasicTypes.STRING);
		sqlQuery.addScalar("idUnit", StandardBasicTypes.LONG);
		sqlQuery.addScalar("idCase", StandardBasicTypes.LONG);
		sqlQuery.addScalar("idSituation", StandardBasicTypes.LONG);
		sqlQuery.addScalar("dtStageClose", StandardBasicTypes.DATE);
		sqlQuery.addScalar("cdStageCurrPriority", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdStageRsnPriorityChgd", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING);
		sqlQuery.addScalar("indStageClose", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdStageCnty", StandardBasicTypes.STRING);
		sqlQuery.addScalar("nmStage", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdStageRegion", StandardBasicTypes.STRING);
		sqlQuery.addScalar("dtStageStart", StandardBasicTypes.DATE);
		sqlQuery.addScalar("cdStageProgram", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdStage", StandardBasicTypes.STRING);
		sqlQuery.addScalar("stagePriorityCmnts", StandardBasicTypes.STRING);
		sqlQuery.addScalar("stageClosureCmnts", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdClientAdvised", StandardBasicTypes.STRING);
		sqlQuery.addScalar("indEcs", StandardBasicTypes.STRING);
		sqlQuery.addScalar("indEcsVer", StandardBasicTypes.STRING);
		sqlQuery.addScalar("indAssignStage", StandardBasicTypes.STRING);
		sqlQuery.addScalar("dtClientAdvised", StandardBasicTypes.DATE);
		sqlQuery.addScalar("dtMultiRef", StandardBasicTypes.DATE);
		sqlQuery.addScalar("dtStageCreated", StandardBasicTypes.DATE);
		sqlQuery.addScalar("indSecondApprover", StandardBasicTypes.STRING);
		sqlQuery.addScalar("indScreened", StandardBasicTypes.STRING);
		sqlQuery.addScalar("indFoundOpenCaseAtIntake", StandardBasicTypes.STRING);
		sqlQuery.addScalar("indFormallyScreened", StandardBasicTypes.STRING);

		sqlQuery.setParameter("idPerson", idPerson);

		stageValueBeanDtos = (ArrayList<StageValueBeanDto>) sqlQuery.list();

		if (TypeConvUtil.isNullOrEmpty(stageValueBeanDtos)) {
			throw new DataNotFoundException(
					messageSource.getMessage("stageValueBeanDto.idPerson.NotFound", null, Locale.US));
		}
		return stageValueBeanDtos;
	}
	
	/**
	 * artf129782: Licensing Investigation Conclusion
	 * method name: updateVictimNotificationStatus
	 */
	public Long updateVictimNotificationStatus(Long idStage){
		
		Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class,idStage);
		
			stage.setIndVictimNotifStatus(ServiceConstants.STRING_IND_Y);
			
			if(stage != null){
				sessionFactory.getCurrentSession().saveOrUpdate(stage);
			}
		return idStage;
	}
}
