package us.tx.state.dfps.service.investigation.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.ApsInvstDetail;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewDto;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Apr 3, 2017 - 12:22:48 PM
 */
public interface ApsInvstDetailDao {
	/**
	 * 
	 * Method Description:legacy service name - CINV44D
	 * 
	 * @param uIdStage
	 * @return @
	 */
	List<ApsInvstDetail> getApsInvstDetailbyParentId(Long uIdStage);

	/**
	 * This DAM inserts/updates APS_INVST_DETAIl
	 * 
	 * Service Name: CCMN03U, DAM Name: CINV24D
	 * 
	 * @param apsInvstDetail
	 * @
	 */
	void saveApsInvstDetail(ApsInvstDetail apsInvstDetail);

	/**
	 * This DAM inserts/updates APS_INVST_DETAIl
	 * 
	 * Service Name: CCMN03U, DAM Name: CINV24D
	 * 
	 * @param apsInvstDetail
	 * @
	 */
	void updateApsInvstDetail(ApsInvstDetail apsInvstDetail);

	/**
	 * This DAM inserts/updates APS_INVST_DETAIl
	 * 
	 * Service Name: CCMN03U, DAM Name: CINV24D
	 * 
	 * @param apsInvstDetail
	 * @
	 */
	void deleteApsInvstDetail(ApsInvstDetail apsInvstDetail);



	/*
	 * DAM Name: CLSCA1D
	 * Return the APS case review
	 */
	ApsCaseReviewDto getApsInvestigationDetails(Long caseId,String stageType);


	/**
	 * <p> Returns up to 100 id_events from the APS_INV_NARR table for a given CASE. </p>
	 * @param stageId
	 * @param caseId
	 * @return
	 */
	List<Long> getInvNarrativeEvents (Long caseId, Long stageId);


}
