package us.tx.state.dfps.service.investigation.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.CpsInvstDetail;
import us.tx.state.dfps.common.domain.Stage;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Apr 3, 2017 - 12:22:48 PM
 */
public interface CpsInvstDetailDao {
	/**
	 * 
	 * Method Description:legacy DAM name - CINV95D
	 * 
	 * @param uIdStage
	 * @return @
	 */
	List<CpsInvstDetail> getCpsInvstDetailbyParentId(Long uIdStage);

	/**
	 * Method Description:legacy DAM name - CINVA8D
	 * 
	 * @param cpsInvstDetail
	 * @return @
	 */
	Long updtCpsInvstDetail(CpsInvstDetail cpsInvstDetail, String operation);

	/**
	 * This DAM will add, update, and delete from the CPS_INVST_DETAIL table
	 * 
	 * Service Name: CCMN03U, DAM Name: CINV12D
	 * 
	 * @param uIdEvent
	 * @return @
	 */
	CpsInvstDetail getCpsInvstDetailbyEventId(Long uIdEvent);

	/**
	 * Method Name: getCpsInvstDetailbyStageId Method Description: Method
	 * returns the CPS Invst Detail as per The Stage.
	 * 
	 * @param stage
	 * @return
	 * 
	 */
	CpsInvstDetail getCpsInvstDetailbyStageId(Stage stage);

}
