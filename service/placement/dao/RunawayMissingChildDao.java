/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Feb 15, 2018- 3:13:10 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.placement.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.placement.dto.*;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Feb 15, 2018- 3:13:10 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface RunawayMissingChildDao {

	/**
	 * 
	 * Method Name: fetchRunawayMissingList Method Description: retrieves a full
	 * row from CHILD_MSNG_DTL and CHILD_RECOVERY_DTL table for given case and
	 * stage
	 * 
	 * @param commonHelperReq
	 * @return List<RunawayMissingDto>
	 */
	public List<RunawayMissingDto> fetchRunawayMissingList(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Name: fetchMissingChildDetail Method Description: Retrieves
	 * MissingChildDetail from CHILD_MSNG_DTL table for given ID_CHILD_MSNG_DTL
	 * 
	 * @param idChlsMsngDtl
	 * @param idEvent
	 * @return MissingChildDetailDto
	 *
	 */
	public MissingChildDetailDto fetchMissingChildDetail(Long idChlsMsngDtl, Long idEvent);

	/**
	 * 
	 * Method Name: fetchNotificationDetail Method Description: Retrieves
	 * NotificationPartiesDetail from NOTIFCTN_PARTIES table for given ID_EVENT
	 * 
	 * @param idEvent
	 * @return NotificationPartiesDto
	 *
	 */
	public NotificationPartiesDto fetchNotificationDetail(Long idEvent);

	/**
	 * 
	 * Method Name: fetchChildRecoveryDetail Method Description: Retrieves Child
	 * Recovery detail from CHILD_RECOVERY_DTL table for given ID_CHILD_MSNG_DTL
	 * 
	 * @param idChlsMsngDtl
	 * @param idEvent
	 * @return ChildRecoveryDetailDto
	 *
	 */
	public ChildRecoveryDetailDto fetchChildRecoveryDetail(Long idChlsMsngDtl, Long idEvent);

	/**
	 * 
	 * Method Name: fetchChildAbsenceReason Method Description:Fetch the Child
	 * Confirmed Reason for Absence detail for idChldRecoveryDtl from
	 * CHILD_CNFRMD_RSN_ABSENCE table from NOTIFCTN_PARTIES table through
	 * DaoImpl
	 * 
	 * @param idChldRecoveryDtl
	 * @return List<ChildAbsenceReasonDto>
	 */
	public List<ChildAbsenceReasonDto> fetchChildAbsenceReason(Long idChldRecoveryDtl);

	/**
	 * 
	 * Method Name: saveMissingChildDetail Method Description: This method used
	 * to save missing child detail into MSGN_CHLD_DTL
	 * 
	 * @param msngChildDetailDto
	 * @param cReqFun
	 * @return MissingChildDetailDto
	 */
	public MissingChildDetailDto saveMissingChildDetail(MissingChildDetailDto msngChildDetailDto, String cReqFun);

	public void deleteMissingChildDetail(MissingChildDetailDto msngChildDetailDto);

	/**
	 * 
	 * Method Name: saveMissingChildDetail Method Description: This method used
	 * to save Notification Party detail into MSNG_CHILD_RCVRY_NOTIFCTN
	 * 
	 * @param notificationPatiesDto
	 * @param cReqFun
	 * @return NotificationPartiesDto
	 */
	public NotificationPartiesDto saveNotificationDetail(NotificationPartiesDto notificationPatiesDto, String cReqFun);

	/**
	 *
	 * Method Name: deleteNotificationDetail Method Description: This method used
	 * to delete Notification Party detail into MSNG_CHILD_RCVRY_NOTIFCTN
	 *
	 * @param notificationPatiesDto
	 * @return
	 */
	public void deleteNotificationDetail(NotificationPartiesDto notificationPatiesDto);

	/**
	 * 
	 * Method Name: saveChildRecoveryDetail Method Description: This method used
	 * to save Notification Party detail into MSNG_CHILD_RCVRY_DTL
	 * 
	 * @param childRecoveryDetail
	 * @param cReqFun
	 * @param msngChildDetailDto
	 * @return
	 */
	public ChildRecoveryDetailDto saveChildRecoveryDetail(ChildRecoveryDetailDto childRecoveryDetail, String cReqFun,
			MissingChildDetailDto msngChildDetailDto);

	public void deleteChildRecoveryDetail(ChildRecoveryDetailDto childRecoveryDetail,
														  MissingChildDetailDto msngChildDetailDto);

	/**
	 * 
	 * Method Name: saveConfirmedRsnAbsDetail Method Description: This method
	 * used to save Child Runaway Absence Reason detail into
	 * ID_MSNG_CHILD_RNWY_RSN
	 * 
	 * @param chldAbsenceRsnList
	 * @param childRecoveryDetailDto
	 */
	public void saveConfirmedRsnAbsDetail(List<ChildAbsenceReasonDto> chldAbsenceRsnList, ChildRecoveryDetailDto childRecoveryDetailDto);

	public void deleteConfirmedRsnAbsDetail(List<ChildAbsenceReasonDto> chldAbsenceRsnList, ChildRecoveryDetailDto childRecoveryDetailDto);

	/**
	 * 
	 * Method Name: fetchDetailForValidation Method Description: This method
	 * will fetch Detail from Person table , CNSRVTRSHP_REMOVAL table Legal
	 * Status table for validation in Missing Child Detail and Child Recovery
	 * Detail page.
	 * 
	 * @param idPerson
	 * @param idCase
	 * @return RunawayMsngRcvryDto
	 */
	public RunawayMsngRcvryDto fetchDetailForValidation(Long idPerson, Long idCase);
	
	/**
	 * 
	 * Method Name: childRecoveryExist Method Description: This Method is 
	 * to check whether Child Recovery Detail exists or not
	 * @param idMsngChildDtl
	 * @return Boolean
	 */
	public Boolean childRecoveryExist(Long idMsngChildDtl);

	public Boolean isChildRecoveryExists(Long idMsngChildDtl);
	
	/**
	 * 
	 * Method Name: fetchPrimaryPerson Method Description: This Method is 
	 * to fetch Primary Child id for which we are recording runAway
	 * @param idStage
	 * @return Long
	 */
	public Long fetchPrimaryPerson(Long idStage);

	/*added for PPM 65209*/
	public Date getRecoveryDate(Long idMsngChildDtl);

	/**
	 *
	 * Method Name: fetchMissingChildIds Method Description: This Method is
	 * to fetch Missing Child id and Notification Id for which we are recording runAway
	 * @param idEvent
	 * @return Long
	 */
	public RunawayMissingIdsDto fetchMissingChildIds(Long idEvent);

	/**
	 *
	 * Method Name: fetchChildRecoveryIds Method Description: This Method is
	 * to fetch Missing Child id, Notification Id and Child recovery id for which we are recording runAway
	 * @param idEvent
	 * @return Long
	 */
	public RunawayChildRecoveryIdsDto fetchChildRecoveryIds(Long idEvent);

	/**
	 *
	 * Method Name: fetchDtRemoval Method Description: This Method is
	 * to fetch Dt Removal for which we are recording runAway
	 * @param idStage
	 * @return Long
	 */
	public RunawayMsngDtRemovalDto fetchDtRemoval(Long idStage);

}
