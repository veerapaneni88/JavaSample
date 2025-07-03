package us.tx.state.dfps.service.placement.dao;

import us.tx.state.dfps.service.admin.dto.LegalStatusOutDto;
import us.tx.state.dfps.service.admin.dto.PlacementActPlannedOutDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * 
 * Class Name:PersonIdDtlsDao Class Description:This dam will retrieve the child
 * SSN based on the Id Person from CSEC15D. Oct 31, 2017- 4:28:25 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface PersonIdDtlsDao {

	/**
	 * Method Name:getPersonIdDtls Method Description:This dam will retrieve the
	 * child SSN based on the Id Person from CSEC15D.
	 * 
	 * @param pInputDataRec
	 * @param pOutputDataRec
	 * @return List<PersonIdDtlsOutDto> @
	 */
	public PersonDto getPersonIdRecords(Long idCase, Long idStage, Long idPerson);

	/**
	 * Method getLegalStatusRecords Method Description:This method returns the
	 * legalStatusDto
	 * 
	 * @param idPerson
	 * @param idCase
	 * @return @
	 */
	public LegalStatusOutDto getLegalStatusRecords(Long idPerson, Long idCase);

	/**
	 * Method Name:getPersonIdDtls Method Description:This method returns a
	 * placementRecord
	 * 
	 * @param idPerson
	 * @return @
	 */
	public PlacementActPlannedOutDto getPlacementRecord(Long idPerson);
	
	/**
	 * Method Description: This method retrieves information about the Primary
	 * Child in a case as per ADS Change. DAM Name:CSECD3D
	 * 
	 * @param stagePersDto
	 * @return PersonDto @
	 */
	public PersonDto getPersonIdRecordDtls(Long idCase, Long idStage, Long idPerson);

}
