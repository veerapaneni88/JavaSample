package us.tx.state.dfps.service.personutility.serviceimpl;

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.personutility.dao.PersonUtilityDao;
import us.tx.state.dfps.service.personutility.service.PersonUtilityService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PersonUtilityServiceImpl Oct 10, 2017- 10:27:22 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class PersonUtilityServiceImpl implements PersonUtilityService {

	@Autowired
	private PersonUtilityDao personUtilityDao;
	@Autowired
	// private PersonRaceEthnicityDao personRaceEthnicityDao;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-PersonUtilityServiceLog");

	/**
	 * Method Name: isPersonInOneOfThesePrograms Method Description:Returns true
	 * if given person exists in at least one given stage program
	 * 
	 * @param idPerson
	 * @param hashSet
	 * @return Boolean
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isPersonInOneOfThesePrograms(Long idPerson, HashSet hashSet) {
		LOG.debug("Entering method isPersonInOneOfThesePrograms in PersonUtilityService");

		Boolean result = null;
		try {

			result = personUtilityDao.isPersonInOneOfThesePrograms(idPerson, hashSet);
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}

		LOG.debug("Exiting method isPersonInOneOfThesePrograms in PersonUtilityService");
		return result;
	}

	/**
	 * Method Name: getPersonRaceEthnicity Method Description:returns true if
	 * race and ethnicity data is not found for one or more Principals.
	 * 
	 * @param personId
	 * @return RaceEthnicityDto
	 */
	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED,
	 * propagation = Propagation.REQUIRED) public RaceEthnicityDto
	 * getPersonRaceEthnicity(Long personId) { LOG.
	 * debug("Entering method getPersonRaceEthnicity in PersonUtilityService");
	 * 
	 * RaceEthnicityDto reb = new RaceEthnicityDto(); try {
	 * 
	 * String ethnicity = personRaceEthnicityDao.queryPersonEthnicity(personId);
	 * reb.setEthnicity(ethnicity); reb =
	 * personRaceEthnicityDao.queryPersonRace(personId, reb); } catch
	 * (DataNotFoundException e) { LOG.error(e.getMessage()); }
	 * 
	 * LOG.debug("Exiting method getPersonRaceEthnicity in PersonUtilityService"
	 * ); return reb; }
	 */
	/**
	 * Method Name: isPRNRaceStatMissing Method Description:
	 * 
	 * @param idStage
	 * @return Boolean
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isPRNRaceStatMissing(Long idStage) {
		LOG.debug("Entering method isPRNRaceStatMissing in PersonUtilityService");

		boolean bRaceStatMissing = false;
		try {

			// SQL call
			bRaceStatMissing = personUtilityDao.getPRNRaceEthnicityStat(idStage);
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}
		LOG.debug("Exiting method isPRNRaceStatMissing in PersonUtilityService");
		return bRaceStatMissing;

	}

	/**
	 * Method Name: isPlcmntPerson Method Description:to check if a person is
	 * associated with an open or closed PCSP placement
	 * 
	 * @param caseId
	 * @param personId
	 * @param cdStage
	 * @return Boolean
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isPlcmntPerson(Long caseId, Long personId, String cdStage) {
		LOG.debug("Entering method isPlcmntPerson in PersonUtilityService");

		boolean isPlcmntPerson = false;
		try {

			if (isPCSPStage(cdStage) && (personUtilityDao.isPlcmntCaregiverExists(caseId, personId)
					|| personUtilityDao.isPlcmntChildExists(caseId, personId))) {
				isPlcmntPerson = true;
			}
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}
		LOG.debug("Exiting method isPlcmntPerson in PersonUtilityService");
		return isPlcmntPerson;

	}

	/**
	 * Method Name: isAssmntPerson Method Description:to check if a person is
	 * associated with PROC or COMP
	 * 
	 * @param personId
	 * @param stageId
	 * @return Boolean
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isAssmntPerson(Long personId, Long stageId) {
		LOG.debug("Entering method isAssmntPerson in PersonUtilityService");

		boolean isAssmntPerson = false;
		try {

			if (personUtilityDao.isAssmntChildOhmExists(personId, stageId)
					|| personUtilityDao.isAssmntCaregiverExists(personId, stageId)) {
				isAssmntPerson = true;
			}
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}
		LOG.debug("Exiting method isAssmntPerson in PersonUtilityService");
		return isAssmntPerson;

	}

	/**
	 * Method Name: isPCSPStage Method Description:Private method to check if
	 * PCSP is available in a stage
	 * 
	 * @param cdStage
	 * @return boolean
	 */
	private boolean isPCSPStage(String cdStage) {
		boolean isPCSPStage = false;
		if (cdStage.equals(ServiceConstants.CSTAGES_AR) || cdStage.equals(ServiceConstants.CSTAGES_INV)
				|| cdStage.equals(ServiceConstants.CSTAGES_FPR) || cdStage.equals(ServiceConstants.CSTAGES_FSU)
				|| cdStage.equals(ServiceConstants.CSTAGES_FRE))
			isPCSPStage = true;
		return isPCSPStage;
	}
}
