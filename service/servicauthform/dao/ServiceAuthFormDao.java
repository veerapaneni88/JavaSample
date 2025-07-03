package us.tx.state.dfps.service.servicauthform.dao;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import us.tx.state.dfps.approval.dto.ApprovalFormDataDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.ClientInfoServiceAuthDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.DayCareDetailsDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.DayCareFacilServiceAuthDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.KinshipGroupInfoDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.MedicaidServiceAuthDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.OldestVictimNameDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.SelectForwardPersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 1, 2018- 1:32:37 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public interface ServiceAuthFormDao {

	/**
	 * 
	 * Method Name: getOldestVicName Method Description: Retrieves Oldest Victim
	 * Name using ID STAGE as input from eventValueDto. Dam Name :CSEC78D
	 * 
	 * @param idStage
	 * @param relType
	 * @return OldestVictimNameDto
	 */
	OldestVictimNameDto getOldestVicName(Long idStage, String relType);

	/**
	 * 
	 * Method Name: getNbrContractScor Method Description: Retrieves
	 * nbr_contract_scor from contract_period Dam Name : CMSC63D
	 * 
	 * @param idContract
	 * @param dtSvcAuthEff
	 * @return String
	 */
	String getNbrContractScor(Long idContract, Date dtSvcAuthEff);

	/**
	 * 
	 * Method Name: getminIdSvcAuthEvent Method Description: Get the original
	 * progressed svc auth event Dam Name: CallCSEC0DD
	 * 
	 * @param idSvcAuth
	 * @return Long
	 */
	Long getMinIdSvcAuthEvent(Long idSvcAuth);

	/**
	 * 
	 * Method Name: getDayCareDetails Method Description:DAM that retrieves
	 * daycare form 1800 info for the service authorization form 2054. The DAM
	 * uses outer joins to the C_FACILITY and C_FACILITY_ADDRESS snapshots of
	 * FACILITY@CLASS and FACILITY_ADDRESS@CLASS. DAM Name: CLSC27D
	 * 
	 * @param minIdSvcAuthEvent
	 * @return DayCareDetailsDto
	 */
	List<DayCareDetailsDto> getDayCareDetails(Long minIdSvcAuthEvent);

	/**
	 * 
	 * Method Name: getSelectForwardPerson Method Description:: Given the
	 * Primary Client person ID retrieved from the service auth table, bring
	 * back all the Person Merge Forward IDs. If no rows found, the person has
	 * not been merged. NO_ROWS_FOUND is okay DAM Name: CLSS71D
	 * 
	 * @param idPerson
	 * @return List<SelectForwardPersonDto>
	 */
	List<SelectForwardPersonDto> getSelectForwardPerson(Long idPerson);

	/**
	 * 
	 * Method Name: getPersonInStageLink Method Description:This DAM checks to
	 * see if a given PersMergeForward ID is on the Stage Person Link table for
	 * a given Stage. Dam-Name: CSES86D
	 * 
	 * @param idPersonMergeForward
	 * @param idStage
	 * @return int
	 */

	int getPersonInStageLink(Long idPersonMergeForward, Long idStage);

	/**
	 * 
	 * Method Name: getClientInfoServiceAuth Method Description:
	 * 
	 * @param idPerson
	 * @param idStage
	 * @return ClientInfoServiceAuthDto
	 */
	ClientInfoServiceAuthDto getClientInfoServiceAuth(Long idPerson, Long idStage);

	/**
	 * 
	 * Method Name: getMedicaidServiceAuth Method Description:
	 * 
	 * @param idPerson
	 * @return MedicaidServiceAuthDto
	 */
	List<MedicaidServiceAuthDto> getMedicaidServiceAuth(Long idPerson);

	/**
	 * 
	 * Method Name: getKinshipDetails Method Description:
	 * 
	 * @param idSvcAuth
	 * @return
	 */
	List<KinshipGroupInfoDto> getKinshipDetails(Long idSvcAuth);

	/**
	 * 
	 * Method Name: getPRPersonId Method Description:
	 * 
	 * @param idStage
	 * @param idEvent
	 * @return
	 */
	Long getPRPersonId(Long idEvent);

	/**
	 * 
	 * Method Name: getDayCareFacility Method Description:
	 * 
	 * @param idDaycarePersonLink
	 * @param idDaycareRequest
	 * @return
	 */
	List<DayCareFacilServiceAuthDto> getDayCareFacility(Long idDaycarePersonLink, Long idDaycareRequest);

	ApprovalFormDataDto getApprovalFormData(Long idEvent);

}
