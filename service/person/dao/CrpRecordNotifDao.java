package us.tx.state.dfps.service.person.dao;

import us.tx.state.dfps.service.common.request.CrpRecordNotifReq;
import us.tx.state.dfps.service.common.response.FormsServiceRes;
import us.tx.state.dfps.service.forms.dto.CrpRecordNotifAndDetailsDto;
import us.tx.state.dfps.service.forms.dto.RecordsCheckNotifDto;
import us.tx.state.dfps.service.person.dto.*;
import us.tx.state.dfps.service.recordscheck.dto.ResourceContractInfoDto;

import java.util.List;

/**
 * service-business- IMPACT 2.0 Class Description:
 * CrpRecordNotifDao Interface to fetch the records from table which map
 * to public central registry screen. Jan 25, 2024- 9:58:00 PM © 2024 Texas Department
 * of Family and Protective Services
 *
 * ********Change History**********
 * 01/25/2024 thompswa Initial.
 * 08/02/2024 thompswa artf268135 matched requests added.
 */
public interface CrpRecordNotifDao {

	/**
	 * Method Description: This Method will retrieve the resource contract Info
	 * details form record check, resource address tables by passing idRecCheck
	 * as input.
	 *
	 * @param idRecCheck
	 * @return ResourceContractInfoDto @
	 */
	public ResourceContractInfoDto getResourceContractInfo(Long idRecCheck);

	/**
	 * Method Description: This Method will retrieve the resource contract Info
	 * details form record check, resource address tables by passing idRecCheck
	 * as input.
	 *
	 * @param idRequest
	 * @return centralRegistryCheckDto @
	 */
	public CentralRegistryCheckDto getCrpReqDtl(Long idRequest);

	/**
	 * Method Description: This Method will retrieve the Central Registry Check Person
	 * Names from CRP_RECORD_NAME@impactp, by passing idRequest as input.
	 *
	 * @param idRequest
	 * @return crpRequestList
	 */
	public List<CrpPersonNameDto>  getCrpPersonNames(Long idRequest);

	/**
	 * Method Description: This Method will retrieve the Central Registry Check Info
	 * details from CENTRAL_REGISTRY_CHECK@impactp, CRP_PERSON_ADDRESS@impactp tables
	 * by passing idCrpCheck as input.
	 *
	 * @param idRequestList  // artf268135
	 * @return List<PublicCentralRegistryDto>
	 */
	public List<PublicCentralRegistryDto> getCrpBatchResult(List<Long> idRequestList);

	/**
	 * Method Name: getCrpRecordNotifAndDetails
	 * Method Description: This Method will retrieve the Crp Record Notif Info
	 * details from CRP_RECORD_NOTIF table
	 * by passing idCrpRecordDetail as input.
	 *
	 * @param crpRecordNotifReq
	 * @return CrpRecordNotifAndDetailsDto
	 */
	public CrpRecordNotifAndDetailsDto getCrpRecordNotifAndDetails(CrpRecordNotifReq crpRecordNotifReq);

	/**
	 * Method Description: This method will fetch the crp record check notification
	 * details for action classes by passing idCrpRecordNotif as input
	 *
	 * @param idCrpRecordNotif
	 * @return CrpRecordNotifDto @
	 */
	public CrpRecordNotifDto getCrpRecordNotification(Long idCrpRecordNotif);

	/**
	 * Method Description: This method will update the crp record notif
	 * table for action classes by passing required input
	 *
	 * @param crpRecordNotifDto
	 * @return rtnMsg @
	 */
	public String updateCrpRecordNotif(CrpRecordNotifDto crpRecordNotifDto);

	/**
	 * Method Description: This method will create the new crp ecord notif
	 * notification detail.
	 *
	 * @param crpRecordNotifDto
	 * @return Long @
	 */
	public Long insertCrpRecordNotif(CrpRecordNotifDto crpRecordNotifDto);

	/**
	 * Method Description: This method will insert the new crp record
	 * notification pdf.
	 *
	 * @param crpRequestStatusDto
	 * return formResponseRes
	 */
	public String insertCrpRequestStatus(CrpRequestStatusDto crpRequestStatusDto);

}
