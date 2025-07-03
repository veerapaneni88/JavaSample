
package us.tx.state.dfps.service.nytd.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.nytd.dto.NytdReportPeriodDto;
import us.tx.state.dfps.service.nytd.dto.NytdSearchResultDto;
import us.tx.state.dfps.service.nytd.dto.NytdSearchValueDto;
import us.tx.state.dfps.service.nytd.dto.NytdYouthContactInfoDto;
import us.tx.state.dfps.service.nytd.dto.NytdYouthHistoryDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: NytdDao Apr
 * 6, 2018- 12:52:26 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public interface NytdDao {
	/**
	 * 
	 * Method Description: This Method will provide details of current reporting
	 * period
	 * 
	 * @param Date
	 * @return NytdReportPeriodValueDto
	 * @throws HibernateException
	 * @
	 */
	public NytdReportPeriodDto getNytdReportingPeriod(Date date);

	/**
	 * Method Name: setNewPersonView Method Description: Set the # in the record
	 * that identifies this record as not viewed.
	 * 
	 * @param nytdID
	 * @param idAssignedPerson
	 */
	public void setNewPersonView(Long nytdID, Long idAssignedPerson);

	/**
	 * Method Name: saveNytdYouthOutcomeReportingStatus Method Description: Save
	 * the Reporting Status for this youth record.
	 * 
	 * @param nytdID
	 * @param reportingStatus
	 */
	public void saveNytdYouthOutcomeReportingStatus(Long nytdID, String reportingStatus);

	/**
	 * Method Name: saveSurveyAppAuthInfo Method Description: This method saves
	 * Authentication Info record into impact_rqst_nytd@nytd table.
	 * 
	 * @param idRegPersonNytd
	 * @param idStaff
	 * @return
	 */
	public void saveSurveyAppAuthInfo(Long idStaff);

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
	 * Method Name: searchNytdPopulation Method Description: Get the NYTD
	 * Population based on Input Criteria
	 * 
	 * @param nytdSearchValueDto
	 * @return List<NytdSearchResultDto>
	 */
	public List<NytdSearchResultDto> searchNytdPopulation(NytdSearchValueDto nytdSearchValueDto);
}
