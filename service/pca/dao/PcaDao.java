package us.tx.state.dfps.service.pca.dao;

import java.util.List;

import us.tx.state.dfps.service.pca.dto.PcaEligApplicationDto;
import us.tx.state.dfps.service.pca.dto.PlacementDtlDto;
import us.tx.state.dfps.service.pca.dto.ResourcePlcmntDto;
import us.tx.state.dfps.service.pca.dto.StageCaseDtlDto;
import us.tx.state.dfps.service.person.dto.SupervisorDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PcaServiceImpl will implemented all operation defined in
 * PcaService Interface related PCA module. Feb 9, 2018- 2:02:21 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface PcaDao {

	/**
	 * Method Description: This method is used to retrieve the information for
	 * stage and capscase table by passing idStage as input request. Dam Name:
	 * CSEC02D
	 * 
	 * @param pcaApplicationReq
	 * @return PcaApplicationRes
	 */
	StageCaseDtlDto getStageAndCaseDtls(Long idStage);

	/**
	 * Method Description: This method is used to retrieve the information for
	 * Caps_Resource and Placement table by passing idPersonPlcmtChild as input
	 * request. Dam Name: CSES28D
	 * 
	 * @param idPersonPlcmtChild
	 * @return ResourcePlcmntDto
	 */
	ResourcePlcmntDto getResourcePlcmntDtls(Long idPersonPlcmtChild);

	/**
	 * Method Description: This method is used to retrieve placement eventid
	 * from PCA_ELG_APPLICATION table by passing idPerson as input. Dam Name:
	 * CSECE0D
	 * 
	 * @param idPerson
	 * @return List<PcaEligApplicationDto>
	 */
	List<PcaEligApplicationDto> getPlcmntEvent(Long idPerson);

	/**
	 * Method Description: This method is used to retrieve placement details
	 * from Placement table by passing input as idPlcmntevent Dam Name:CSES37D
	 * 
	 * @param idPlcmntEvent
	 * @return PlacementDtlDto
	 */
	PlacementDtlDto getPlcmntDtls(Long idPlcmntEvent);

	/**
	 * Method Description: This method is used to retrieve an employee's
	 * supervisor name and ID from Person, Unit, Unit_Emp_Link tables by passing
	 * idPerson as input. Dam Name: CCMN60D
	 * 
	 * @param idPerson
	 * @return SupervisorDto
	 */

	SupervisorDto getSupervisorPersonId(Long idPerson);
}
