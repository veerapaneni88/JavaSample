package us.tx.state.dfps.service.recertification.dao;

import us.tx.state.dfps.service.adoptionasstnc.ApplicationBackgroundDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Performs
 * some of the database operations for Adoption Assistance Eligibility Oct 10,
 * 2017- 10:23:07 AM © 2017 Texas Department of Family and Protective Services
 */
public interface AaeApplBackgroundDao {

	/**
	 * Method Name: selectLatestAdptAsstEligAppl Method Description:This method
	 * fetches latest AAE Application APRV using idPerson and idStage
	 * 
	 * @param idPerson
	 * @param idStage
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long selectLatestAdptAsstEligAppl(Long idPerson, Long idStage);

	/**
	 *
	 * @param idAppEvent
	 * @return ApplicationBackgroundValueDto
	 * @throws DataNotFoundException
	 */
	public ApplicationBackgroundDto selectAdptAsstEligAppFromEvent(Long idAppEvent);

	/**
	 *
	 * @param appValueDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long updateAdptAsstEligApplication(ApplicationBackgroundDto appValueDto);

	/**
	 *
	 * @param appValueDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long insertAdptAsstEligApplication(ApplicationBackgroundDto appDto);

	/**
	 *
	 * @param idAdptEligApplication
	 * @param idEvent
	 * @param idCase
	 * @param idCreatedPerson
	 * @return void
	 * @throws DataNotFoundException
	 */
	public Long insertAdptAsstAppEventLink(Long idAdptEligApplication, Long idNewEvent, Long idCase,
			Long idLastUpdatedByPerson);

	/**
	 *
	 * @param idAdptEligApplication
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long fetchLinkedADOApplEvent(Long idAdptEligApplication);

	/**
	 *
	 * @param idAdptEligApplication
	 * @return ApplicationBackgroundValueDto
	 * @throws DataNotFoundException
	 */
	public ApplicationBackgroundDto selectAdptAsstEligApplication(Long idAdptEligApplication);

	public Long selectLatestAdptAsstEligAppl(Long idSiblingApplPerson);

	/**
	 * Method Name: fetchIdEventForAppl Method Description: Fetches the id event
	 * for the application
	 * 
	 * @param idAdptEligApplication
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long fetchIdEventForAppl(Long idAdptEligApplication);

	/**
	 * Method Name: fetchIdEventOfLatestAppl Method Description: Fetches the id
	 * event of the latest Adoption Assistance application for the stage
	 * 
	 * @param idStage
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long fetchIdEventOfLatestAppl(Long idStage);

}
