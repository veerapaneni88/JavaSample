package us.tx.state.dfps.service.investigation.dao;

import java.util.List;

import us.tx.state.dfps.service.investigation.dto.InvstRestraintDto;
import us.tx.state.dfps.service.investigation.dto.LicensingInvstDtlDto;
import us.tx.state.dfps.common.domain.AllegedSxVctmztn;
import us.tx.state.dfps.service.investigation.dto.AllegedSxVctmztnDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Dao class
 * for service cinv74s> Mar 27, 2018- 3:58:16 PM © 2017 Texas Department of
 * Family and Protective Services
 *  * *************** Change History ****************************************
 * 11/1/2019  kanakas artf129782: Licensing Investigation Conclusion
 * 06/09/2020 kanakas artf152402 : Prior investigation overwritten by later 
 */
public interface LicensingInvstSumDao {
	/**
	 * 
	 * Method Description:CSESE1D
	 * 
	 * @param uIdStage
	 * @return List<InvstRestraintDto> @
	 */
	List<InvstRestraintDto> getInvstConclusionResById(Long idStage);
	
	/**
	 * 
	 * Method Description:
	 * artf129782: Licensing Investigation Conclusion
	 * @param uIdStage
	 * @return List<AllegedSxVctmztnDto> @
	 */
	List<AllegedSxVctmztnDto> getAllegedSxVctmztnDtoListByStageId(Long idStage);

	/**
	 * Method Name: getLicensingInvstDtlDaobyParentId Method Description:
	 * 
	 * @param idStage
	 * @return LicensingInvstDtlDto
	 */
	LicensingInvstDtlDto getLicensingInvstDtlDaobyParentId(Long idStage);
	
	/**
	 * artf129782: Licensing Investigation Conclusion
	 * @param idStage
	 * @return AllegedSxVctmztn
	 * chnaged for artf152402
	 */
	AllegedSxVctmztn getAllegedSxVctmztnDtoById(Long idVictim, Long idStage);
	
	/**
	 * artf129782: Licensing Investigation Conclusion
	 * Method Description:
	 * 
	 * @param uIdStage
	 * @return List<AllegedSxVctmztnDto> @
	 */
	List<AllegedSxVctmztnDto> getAllegedSxVctmztnDtoByStageId(Long idStage);
	
	/**
     * artf152402: Prior investigation overwritten by later 
     * Method Description:
     * 
     * @param uIdStage
     * @return AllegedSxVctmztnDto 
     */
	 List<AllegedSxVctmztnDto> getAllegedSxVctmztnDtoByStageIdPid(Long idStage, Long idPerson);
}
