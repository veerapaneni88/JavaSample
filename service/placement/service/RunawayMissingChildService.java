/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Feb 15, 2018- 2:54:25 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.placement.service;

import us.tx.state.dfps.service.common.request.ChildRecoveryRetreiveReq;
import us.tx.state.dfps.service.common.request.ChildRecoverySaveReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.MissingChildRetrieveReq;
import us.tx.state.dfps.service.common.request.MissingChildSaveReq;
import us.tx.state.dfps.service.common.response.*;


/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Feb 15, 2018- 2:54:25 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface RunawayMissingChildService {
	/**
	 * 
	 * Method Name: fetchRunawayMissingList Method Description: This method to
	 * call service impl to fetch the list of Missing child detail and child
	 * recovery detail.
	 * 
	 * @param commonHelperReq
	 * @return RunawayChildMissingRes
	 */
	public RunawayChildMissingRes fetchRunawayMissingList(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Name: fetchMissingChildDetail Method Description: This method to
	 * call service impl to fetch the Missing child detail
	 * 
	 * @param msngChldReq
	 * @return MissingChildDetailRes
	 */
	public MissingChildDetailRes fetchMissingChildDetail(MissingChildRetrieveReq msngChldReq);

	/**
	 * 
	 * Method Name: fetchChildRecoveryDetail Method Description: This method to
	 * call service impl to fetch the child recovery detail
	 * 
	 * @param chldRcvryReq
	 * @return ChildRecoveryDetailRes
	 */
	public ChildRecoveryDetailRes fetchChildRecoveryDetail(ChildRecoveryRetreiveReq chldRcvryReq);

	/**
	 * 
	 * Method Name: saveChildRecoveryDetail Method Description: This method to
	 * call service impl to save the child recovery detail
	 * 
	 * @param chldRcvySaveReq
	 * @return ChildRecoveryDetailSaveRes
	 */
	public ChildRecoveryDetailSaveRes saveChildRecoveryDetail(ChildRecoverySaveReq chldRcvySaveReq);

	/**
	 *
	 * Method Name: deleteChildRecoveryDetail Method Description: This method to
	 * call service impl to delete the child recovery detail
	 *
	 * @param chldRcvySaveReq
	 * @return ChildRecoveryDetailSaveRes
	 */
	public ChildRecoveryDetailSaveRes deleteChildRecoveryDetail(ChildRecoverySaveReq chldRcvySaveReq);

	/**
	 * 
	 * Method Name: saveMissingChildDetail Method Description: This method to
	 * call service impl to save the Missing child detail
	 * 
	 * @param msngChildSaveReq
	 * @return MissingChildDetailSaveRes
	 */
	public MissingChildDetailSaveRes saveMissingChildDetail(MissingChildSaveReq msngChildSaveReq);

	/**
	 *
	 * Method Name: saveMissingChildDetail Method Description: This method to
	 * call service impl to save the Missing child detail
	 *
	 * @param msngChildSaveReq
	 * @return MissingChildDetailSaveRes
	 */
	public MissingChildDetailSaveRes deleteMissingChildDetail(MissingChildSaveReq msngChildSaveReq);

	/**
	 *
	 * Method Name: fetchMissingChildIds Method Description: This method to
	 * call service impl to fetch the Missing child id and Notification id
	 *
	 * @param idEvent
	 * @return MissingChildIdsRes
	 */
	public MissingChildIdsRes fetchMissingChildIds(Long idEvent);

	/**
	 *
	 * Method Name: fetchChildRecoveryIds Method Description: This method to
	 * call service impl to fetch the Missing child id, Notification id and Child recovery id
	 *
	 * @param idEvent
	 * @return ChildRecoveryIdsRes
	 */
	public ChildRecoveryIdsRes fetchChildRecoveryIds(Long idEvent);

	/**
	 *
	 * Method Name: fetchChildRecoveryLastUpdate Method Description: This method to
	 * call service impl to fetch the Missing child id, Notification id and Child recovery id, and the dtLastUpdate
	 *
	 * @param idEvent
	 * @return ChildRecoveryIdsRes
	 */
	public ChildRecoveryLastUpdIdsRes fetchChildRecoveryLastUpdate(Long idEvent);

	/**
	 *
	 * Method Name: fetchMissingChild Method Description: This method to
	 * call service impl to fetch the Missing child record
	 *
	 * @param idChldMsngDtl
	 * @return ChildRecoveryIdsRes
	 */
	public MissingChildRes fetchMissingChild(Long idChldMsngDtl);

	/**
	 *
	 * Method Name: fetchDetailForValidation Method Description: This method to
	 * call service impl to fetch the Missing child record detail for validation
	 *
	 * @param idPerson
	 * @param idCase
	 * @return RunawayMsngRcvryRes
	 */
	public RunawayMsngRcvryRes fetchDetailForValidation(Long idPerson, Long idCase);

	/**
	 *
	 * Method Name: fetchDtRemoval Method Description: This method to
	 * call service impl to fetch the dt removal record detail for validation
	 *
	 * @param idStage
	 * @return RunawayMsngDtRemovalRes
	 */
	public RunawayMsngDtRemovalRes fetchDtRemoval(Long idStage);
}
