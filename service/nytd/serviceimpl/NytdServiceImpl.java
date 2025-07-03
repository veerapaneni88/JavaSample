package us.tx.state.dfps.service.nytd.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.nytd.dao.NytdDao;
import us.tx.state.dfps.service.nytd.dto.NytdReportPeriodDto;
import us.tx.state.dfps.service.nytd.dto.NytdSearchResultDto;
import us.tx.state.dfps.service.nytd.dto.NytdSearchValueDto;
import us.tx.state.dfps.service.nytd.dto.NytdYouthContactInfoDto;
import us.tx.state.dfps.service.nytd.dto.NytdYouthHistoryDto;
import us.tx.state.dfps.service.nytd.service.NytdService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * implements the method in LegalActionSaveService Oct 25, 2017- 10:36:53 AM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class NytdServiceImpl implements NytdService {
	private static final String EXITING_SAVE_NYTD_YOUTH_OUTCOME_REPORTING_STATUS = "Exiting saveNytdYouthOutcomeReportingStatus from NytdServiceImpl class";
	private static final String EXITING_SAVE_SURVEY_APP_AUTH_INFO = "Exiting saveSurveyAppAuthInfo from NytdServiceImpl class";
	private static final String EXITING_RETRIEVE_NYTD_YOUTH_HISTORY = "Exiting retrieveNytdYouthHistory from NytdServiceImpl class";
	private static final String EXITING_RETRIEVE_NYTD_YOUTH_CONTACT_INFO = "Exiting retrieveNytdYouthContactInfo from NytdServiceImpl class";
	private static final String EXITING_GET_NYTD_REPORTING_PERIOD = "Exiting getNytdReportingPeriod from NytdServiceImpl class";
	private static final String EXITING_GET_CURR_NYTD_REPORTING_PERIOD = "Exiting getCurrNytdReportingPeriod from NytdServiceImpl class";
	private static final String EXITING_SET_NEW_PERSON_VIEW = "Exiting setNewPersonView from NytdServiceImpl class";

	@Autowired
	MessageSource messageSource;

	@Autowired
	NytdDao nytdDao;

	private static final Logger log = Logger.getLogger("ServiceBusiness-NytdServiceImplLog");

	/**
	 * Method Name: setNewPersonView Method Description: Set the # in the record
	 * that identifies this record as not viewed.
	 * 
	 * @param idNytd
	 * @param idAssignedPerson
	 * @return true
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public boolean setNewPersonView(Long idNytd, Long idAssignedPerson) {
		nytdDao.setNewPersonView(idNytd, idAssignedPerson);
		log.debug(EXITING_SET_NEW_PERSON_VIEW);
		return true;
	}

	/**
	 * Method Name: saveNytdYouthOutcomeReportingStatus Method Description: Save
	 * the Reporting Status for this youth record.
	 * 
	 * @param idNytd
	 * @param reportingStatus
	 * @return true
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public boolean saveNytdYouthOutcomeReportingStatus(Long idNytd, String reportingStatus) {
		nytdDao.saveNytdYouthOutcomeReportingStatus(idNytd, reportingStatus);
		log.debug(EXITING_SAVE_NYTD_YOUTH_OUTCOME_REPORTING_STATUS);
		return true;
	}

	/**
	 * Method Name: saveSurveyAppAuthInfo Method Description: This method saves
	 * Authentication Info record into impact_rqst_nytd@nytd table.
	 * 
	 * @param idRegPersonNytd
	 * @param idStaff
	 * @return true
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public boolean saveSurveyAppAuthInfo(Long idStaff) {
		nytdDao.saveSurveyAppAuthInfo(idStaff);
		log.debug(EXITING_SAVE_SURVEY_APP_AUTH_INFO);
		return true;
	}

	/**
	 * Method Name: retrieveNytdYouthHistory Method Description: Get the List of
	 * Nytd Youth History records.
	 * 
	 * @param idStage
	 * @return nytdYouthValueDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public NytdYouthHistoryDto retrieveNytdYouthHistory(Long idStage) {

		NytdYouthHistoryDto nytdYouthValueDto = null;

		nytdYouthValueDto = nytdDao.retrieveNytdYouthHistory(idStage);
		log.debug(EXITING_RETRIEVE_NYTD_YOUTH_HISTORY);
		return nytdYouthValueDto;
	}

	/**
	 * Method Name: retrieveNytdYouthContactInfo Method Description:Get
	 * YouthContactInfoValueBean containing the info related to NYTD youth and
	 * youth designated contact primary and other current information.
	 * 
	 * @param idStage
	 * @param idPerson
	 * @return youthContactInfoValueDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public NytdYouthContactInfoDto retrieveNytdYouthContactInfo(Long idStage, Long idPerson) {
		NytdYouthContactInfoDto youthContactInfoValueDto = null;
		youthContactInfoValueDto = nytdDao.retrieveNytdYouthContactInfo(idStage, idPerson);
		log.debug(EXITING_RETRIEVE_NYTD_YOUTH_CONTACT_INFO);
		return youthContactInfoValueDto;
	}

	/**
	 * Method Name: getNytdReportingPeriod Method Description: Get the NYTD
	 * Reporting Period as a ValueBean for an input date
	 * 
	 * @param dtNytdReportPeriod
	 * @return nytdReportPeriodValueDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public NytdReportPeriodDto getNytdReportingPeriod(Date dtNytdReportPeriod) {
		NytdReportPeriodDto nytdReportPeriodValueDto = null;
		nytdReportPeriodValueDto = nytdDao.getNytdReportingPeriod(dtNytdReportPeriod);
		log.debug(EXITING_GET_NYTD_REPORTING_PERIOD);
		return nytdReportPeriodValueDto;
	}

	/**
	 * Method Name: getCurrNytdReportingPeriod Method Description: Get the NYTD
	 * Reporting Period as a ValueBean for an input date
	 * 
	 * @param dtNytdReportPeriod
	 * @return nytdReportPeriodValueDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public NytdReportPeriodDto getCurrNytdReportingPeriod() {
		NytdReportPeriodDto nytdReportPeriodValueDto = null;
		nytdReportPeriodValueDto = nytdDao.getNytdReportingPeriod(new Date());
		log.debug(EXITING_GET_CURR_NYTD_REPORTING_PERIOD);
		return nytdReportPeriodValueDto;
	}

	/**
	 * Method Name: searchNytdPopulation Method Description: Get the NYTD
	 * Population based on Input Criteria
	 * 
	 * @param nytdSearchValueDto
	 * @return List<NytdSearchResultDto>
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<NytdSearchResultDto> searchNytdPopulation(NytdSearchValueDto nytdSearchValueDto) {
		List<NytdSearchResultDto> result = new ArrayList<NytdSearchResultDto>();
		result = nytdDao.searchNytdPopulation(nytdSearchValueDto);
		return result;
	}
}
