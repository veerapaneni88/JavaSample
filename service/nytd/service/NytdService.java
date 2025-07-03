package us.tx.state.dfps.service.nytd.service;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.nytd.dto.NytdReportPeriodDto;
import us.tx.state.dfps.service.nytd.dto.NytdSearchResultDto;
import us.tx.state.dfps.service.nytd.dto.NytdSearchValueDto;
import us.tx.state.dfps.service.nytd.dto.NytdYouthContactInfoDto;
import us.tx.state.dfps.service.nytd.dto.NytdYouthHistoryDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is the interface for NytdServiceImpl Apr 30, 2018- 4:21:06 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface NytdService {
	/**
	 * Method Name: setNewPersonView Method Description: Set the # in the record
	 * that identifies this record as not viewed.
	 * 
	 * @param nytdID
	 * @param idAssignedPerson
	 * @return boolean
	 */
	public boolean setNewPersonView(Long nytdID, Long idAssignedPerson);

	/**
	 * Method Name: saveNytdYouthOutcomeReportingStatus Method Description: Save
	 * the Reporting Status for this youth record.
	 * 
	 * @param nytdID
	 * @param reportingStatus
	 * @return boolean
	 */
	public boolean saveNytdYouthOutcomeReportingStatus(Long nytdID, String reportingStatus);

	/**
	 * Method Name: saveSurveyAppAuthInfo Method Description: This method saves
	 * Authentication Info record into impact_rqst_nytd@nytd table.
	 * 
	 * @param idRegPersonNytd
	 * @param idStaff
	 * @return
	 */
	public boolean saveSurveyAppAuthInfo(Long idStaff);

	/**
	 * Method Name: retrieveNytdYouthHistory Method Description: Get the List of
	 * Nytd Youth History records.
	 * 
	 * @param idStage
	 * @return NytdYouthValueDto
	 */
	public NytdYouthHistoryDto retrieveNytdYouthHistory(Long idStage);

	/**
	 * Method Name: retrieveNytdYouthContactInfo Method Description:Get
	 * YouthContactInfoValueBean containing the info related to NYTD youth and
	 * youth designated contact primary and other current information.
	 * 
	 * @param idStage
	 * @param idPerson
	 * @return YouthContactInfoValueDto
	 */
	public NytdYouthContactInfoDto retrieveNytdYouthContactInfo(Long idStage, Long idPerson);

	/**
	 * Method Name: getNytdReportingPeriod Method Description: Get the NYTD
	 * Reporting Period as a ValueBean for an input date
	 * 
	 * @param dtNytdReportPeriod
	 * @return NytdReportPeriodValueDto
	 */
	public NytdReportPeriodDto getNytdReportingPeriod(Date dtNytdReportPeriod);

	/**
	 * Method Name: getCurrNytdReportingPeriod Method Description: Get the NYTD
	 * Reporting Period as a ValueBean for an input date
	 * 
	 * @param dtNytdReportPeriod
	 * @return NytdReportPeriodValueDto
	 */

	public NytdReportPeriodDto getCurrNytdReportingPeriod();

	/**
	 * Method Name: searchNytdPopulation Method Description: Get the NYTD
	 * Population based on Input Criteria
	 * 
	 * @param nytdSearchValueDto
	 * @return List<NytdSearchResultDto>
	 */
	public List<NytdSearchResultDto> searchNytdPopulation(NytdSearchValueDto nytdSearchValueDto);
}
