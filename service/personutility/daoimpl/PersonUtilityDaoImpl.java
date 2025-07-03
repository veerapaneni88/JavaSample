package us.tx.state.dfps.service.personutility.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.personutility.dao.PersonUtilityDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PersonUtilityDaoImpl Oct 10, 2017- 10:28:40 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class PersonUtilityDaoImpl implements PersonUtilityDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PersonUtilityDaoImpl.isPersonInOneOfThesePrograms}")
	private String isPersonInOneOfThesePrograms;

	@Value("${PersonUtilityDaoImpl.getPRNRaceEthnicityStat}")
	private String getPRNRaceEthnicityStat;

	@Value("${PersonUtilityDaoImpl.isPlcmntCaregiverExists}")
	private String isPlcmntCaregiverExists;

	@Value("${PersonUtilityDaoImpl.isPlcmntChildExists}")
	private String isPlcmntChildExists;

	@Value("${PersonUtilityDaoImpl.isAssmntChildOhmExists}")
	private String isAssmntChildOhmExists;

	@Value("${PersonUtilityDaoImpl.isAssmntCaregiverExists}")
	private String isAssmntCaregiverExists;

	/**
	 * Method Name: isPersonInOneOfThesePrograms Method Description:Returns true
	 * if given person exists in at least one given stage program
	 * 
	 * @param idPerson
	 * @param hashSet
	 * @return Boolean
	 */
	@Override
	public Boolean isPersonInOneOfThesePrograms(Long idPerson, HashSet hashSet) {
		Long idPersonLink = ServiceConstants.ZERO_VAL;
		if (hashSet.isEmpty()) {
			return false;
		}
		Query query = sessionFactory.getCurrentSession().createSQLQuery(isPersonInOneOfThesePrograms);
		List list = new ArrayList();
		if (hashSet.size() > 0) {
			for (Object object : hashSet) {
				list.add(object);
			}
		}

		query.setParameter(ServiceConstants.ID_PERSON_CONST, idPerson);
		query.setParameterList("cdStageProg", list);

		List<BigDecimal> idPersonList = query.list();
		if (!TypeConvUtil.isNullOrEmpty(idPersonList)) {
			idPersonLink = idPersonList.get(ServiceConstants.Zero).longValue();
		}

		return (idPersonLink != 0);
	}

	/**
	 * Method Name: getPRNRaceEthnicityStat Method Description:returns true if
	 * race and ethnicity data is not found for one or more Principals.
	 * 
	 * @param idStage
	 * @return Boolean
	 */
	@Override
	public Boolean getPRNRaceEthnicityStat(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPRNRaceEthnicityStat);

		query.setParameter(ServiceConstants.idStage, idStage);
		List<Long> idPersonList = query.list();

		return CollectionUtils.isNotEmpty(idPersonList);
	}

	/**
	 * Method Name: isPlcmntCaregiverExists Method Description:returns true if
	 * person(caregiver) is associated with PCSP Placement in a Case
	 * 
	 * @param caseId
	 * @param personId
	 * @return Boolean
	 * @throws DataNotFoundException
	 */
	@Override
	public Boolean isPlcmntCaregiverExists(Long caseId, Long personId) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(isPlcmntCaregiverExists);

		query.setParameter(ServiceConstants.IDCASE, caseId);
		query.setParameter("idPersonCrGvr", personId);

		BigDecimal count = (BigDecimal) query.uniqueResult();

		return (count.longValue() > ServiceConstants.ZERO_VAL);
	}

	/**
	 * Method Name: isAssmntChildOhmExists Method Description:Method returns
	 * true if person(child/other household member) is associated with PCSP
	 * Assessment in a Stage
	 * </p>
	 * 
	 * @param personId
	 * @param stageId
	 * @return Boolean
	 */
	@Override
	public Boolean isAssmntChildOhmExists(Long personId, Long stageId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(isAssmntChildOhmExists);

		query.setParameter(ServiceConstants.IDPERSON, personId);
		query.setParameter(ServiceConstants.idStage, stageId);

		BigDecimal count = (BigDecimal) query.uniqueResult();

		return (count.longValue() > ServiceConstants.ZERO_VAL);
	}

	/**
	 * Method Name: isAssmntCaregiverExists Method Description:Method returns
	 * true if person(caregiver) is associated with PCSP Assessment in a Stage
	 * </p>
	 * 
	 * @param personId
	 * @param stageId
	 * @return Boolean
	 */
	@Override
	public Boolean isAssmntCaregiverExists(Long personId, Long stageId) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(isAssmntCaregiverExists);

		query.setParameter(ServiceConstants.idStage, stageId);
		query.setParameter("idPersonCrGvr", personId);

		BigDecimal count = (BigDecimal) query.uniqueResult();

		return (count.longValue() > ServiceConstants.ZERO_VAL);
	}

	/**
	 * Method Name: isPlcmntChildExists Method Description:returns true if
	 * person(child) is associated with PCSP Placement in a Case
	 * 
	 * @param caseId
	 * @param personId
	 * @return Boolean
	 */
	@Override
	public Boolean isPlcmntChildExists(Long caseId, Long personId) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(isPlcmntChildExists);

		query.setParameter(ServiceConstants.IDCASE, caseId);
		query.setParameter(ServiceConstants.IDPERSON, personId);

		BigDecimal count = (BigDecimal) query.uniqueResult();

		return (count.longValue() > ServiceConstants.ZERO_VAL);

	}

}
