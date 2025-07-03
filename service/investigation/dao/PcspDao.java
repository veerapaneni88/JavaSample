package us.tx.state.dfps.service.investigation.dao;

import java.util.List;

import us.tx.state.dfps.arinvconclusion.dto.PCSPDto;
import us.tx.state.dfps.service.casepackage.dto.PcspDto;
import us.tx.state.dfps.service.person.dto.ChildSafetyPlacementDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This is DAO
 * class for PCSP> May 9, 2018- 4:20:38 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface PcspDao {

	/**
	 * 
	 * Method Name: updateChildIdOnPCSP Method Description: Update Child ID on
	 * PCSP table
	 * 
	 * @param fwdPersonId
	 * @param pcspDto
	 * @return
	 */
	public long updateChildIdOnPCSP(int fwdPersonId, PCSPDto pcspDto);

	/**
	 * 
	 * Method Name: updateCaregiverIdOnPCSP Method Description: Update Caregiver
	 * ID on PCSP table
	 * 
	 * @param fwdPersonId
	 * @param pcspDto
	 * @return
	 */
	public long updateCaregiverIdOnPCSP(int fwdPersonId, PCSPDto pcspDto);

	/**
	 * 
	 * Method Name: getPCSPListForPerson Method Description: Get PCSP List for
	 * Person
	 * 
	 * @param idChild
	 * @param idCareGiver
	 * @return
	 */
	public List<ChildSafetyPlacementDto> getPCSPListForPerson(int idChild, int idCareGiver);

	/**
	 * 
	 * Method Name: displayPCSPList Method Description: Get PCSP List for AR
	 * Conclusion Page Display
	 * 
	 * @param idCase
	 * @param cdStage
	 * @return List<PcspDto>
	 */
	public List<PcspDto> displayPCSPList(Long idCase, String cdStage);

	/**
	 * 
	 * Method Name: getPersonDetails Method Description: Retrieve PCSP child
	 * name and cargiver name
	 * 
	 * @param idStage
	 * @return List<PcspDto>
	 */

	public List<PcspDto> getPersonDetails(Long idStage);
}
