package us.tx.state.dfps.service.dcr.dao;

import java.util.List;

import us.tx.state.dfps.service.dcr.dto.DayCareFacilityDto;
import us.tx.state.dfps.service.dcr.dto.DayCarePersonDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:this class
 * has method implementations of DayCareReqPersonDao Nov 1, 2017- 3:35:35 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface DayCareReqPersonDao {

	/**
	 * Method Name: deleteTypeOfService Method Description:delete type of
	 * service data from DAYCARE_PERSON_LINK table
	 * 
	 * @param dayCareRequestValueDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long deleteTypeOfService(DayCarePersonDto dayCareRequestValueDto);

	/**
	 * Method Name: insertDayCarePersonLink Method Description:This method
	 * inserts single record into DAYCARE_PERSON_LINK table.
	 * 
	 * @param dayCarePersonDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long insertDayCarePersonLink(DayCarePersonDto dayCarePersonDto);

	/**
	 * Method Name: deleteDayCarePersonFacilLink Method Description:This method
	 * deletes all the records from DAYCARE_PERSON_FACIL_LINK table for the
	 * Child.
	 * 
	 * @param idDaycarePersonLink
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long deleteDayCarePersonFacilLink(Long idDaycarePersonLink);

	/**
	 * Method Name: retrieveDayCarePersonFacilLink Method Description:This
	 * method fetches data from DAYCARE_PERSON_FACIL_LINK for the Child.
	 * 
	 * @param idDaycarePersonLink
	 * @param trueval
	 * @return List<DayCareFacilityDto>
	 * @throws DataNotFoundException
	 */
	public List<DayCareFacilityDto> retrieveDayCarePersonFacilLink(Long idDaycarePersonLink, Boolean trueval);

	/**
	 * Method Name: insertDayCarePersonFacilLink Method Description:This method
	 * inserts record into DAYCARE_PERSON_FACIL_LINK table.
	 * 
	 * @param dayCareFacilityDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long insertDayCarePersonFacilLink(DayCareFacilityDto dayCareFacilityDto);

	/**
	 * Method Name: deleteDayCarePersonFacilLink Method Description:This method
	 * deletes the given Facility from DAYCARE_PERSON_FACIL_LINK table for the
	 * Child.
	 * 
	 * @param idFacility
	 * @param idDaycarePersonLink
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long deleteDayCarePersonFacilLink(Long idFacility, Long idDaycarePersonLink);

	/**
	 * Method Name: updateDayCarePersonLink Method Description:This method
	 * updates records into DAYCARE_PERSON_LINK table.
	 * 
	 * @param dayCarePersonDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long updateDayCarePersonLink(DayCarePersonDto dayCarePersonDto);

	/**
	 * 
	 * Method Name: retrieveDayCarePersonLink Method Description:
	 * 
	 * @param idDayCareRequest
	 * @return
	 */
	List<DayCarePersonDto> retrieveDayCarePersonLink(Long idDayCareRequest);

	/**
	 * artf263275 : based on the defect it was decided to change the query to match legacy impact
	 * Method Name: getDayCarePersonsInfo Method Description:
	 *
	 * @param idDayCareRequest
	 * @return
	 */
	List<DayCarePersonDto> getDayCarePersonsInfo(Long idDayCareRequest);

	/**
	 * 
	 * Method Name: hasChangedSystemResponses Method Description:
	 * 
	 * @param idPerson
	 * @param idDayCareRequest
	 */
	boolean hasChangedSystemResponses(int idPerson, int idDayCareRequest);

	/**
	 * Method Name: populateAddress Method Description:
	 * 
	 * @param idStage
	 * @return
	 */
	List<DayCarePersonDto> populateAddress(Long idStage);

	/**
	 * Method Name: deleteDayCarePersonLink Method Description:This method
	 * deletes the given idPerson from DAYCARE_PERSON_LINK table for the Child.
	 * 
	 * @param idDayCareRequest
	 * @param idPerson
	 * @return int
	 */
	int deleteDayCarePersonLink(Long idDayCareRequest, Long idPerson);

	Long retrieveSvcAuthPersonCount(Long idEvent);

}
