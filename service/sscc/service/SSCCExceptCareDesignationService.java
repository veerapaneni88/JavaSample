/**
 * 
 */
package us.tx.state.dfps.service.sscc.service;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.SSCCExceptCareDesiReq;
import us.tx.state.dfps.service.common.response.SSCCExceptCareDesiRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Feb 15, 2018- 2:54:25 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface SSCCExceptCareDesignationService {

	SSCCExceptCareDesiRes getEligibilityPlcmtInfo(SSCCExceptCareDesiReq sSCCExceptCareDesiReq);

	SSCCExceptCareDesiRes fetchSSCCResourceInfo(SSCCExceptCareDesiReq sSCCExceptCareDesiReq);

	SSCCExceptCareDesiRes getExistECDesigAndSsccRsrcContractInfo(SSCCExceptCareDesiReq sSCCExceptCareDesiReq);

	/**
	 * This is a list service the list of Exceptional Care records for a Person
	 * from SSCC_EXCEPT_CARE_DESIG table
	 *
	 * @param SSCCExceptCareDesiReq
	 * @return SSCCExceptCareDesiRes
	 * @throws InvalidRequestException
	 * @
	 */

	SSCCExceptCareDesiRes getSSCCExpCareServiceList(SSCCExceptCareDesiReq sSCCExceptCareDesiReq);

	SSCCExceptCareDesiRes saveOrUpdateExceptCareDesig(SSCCExceptCareDesiReq sSCCExceptCareDesiReq);

	SSCCExceptCareDesiRes getExcpCareOnSaveAndApprove(SSCCExceptCareDesiReq sSCCExceptCareDesiReq);

	SSCCExceptCareDesiRes getSSCCTimelineList(SSCCExceptCareDesiReq sSCCExceptCareDesiReq);

	/**
	 * This is a list service that gets an sscc placement for stage id
	 *
	 * @param SSCCExceptCareDesiReq
	 * @return SSCCExceptCareDesiRes
	 * @throws InvalidRequestException
	 * @
	 */

	SSCCExceptCareDesiRes getActiveSsccPlcmt(SSCCExceptCareDesiReq sSCCExceptCareDesiReq);

	/**
	 * This is a list service that gets an active child sscc referral from the
	 * SSCC_REFERRAL table
	 *
	 * @param SSCCExceptCareDesiReq
	 * @return SSCCExceptCareDesiRes
	 * @throws InvalidRequestException
	 * @
	 */

	SSCCExceptCareDesiRes getActiveChildPlcmtRefferal(SSCCExceptCareDesiReq sSCCExceptCareDesiReq);

	/**
	 * This is a list service that Updates exceptional care designation status
	 *
	 * @param SSCCExceptCareDesiReq
	 * @return SSCCExceptCareDesiRes
	 * @throws InvalidRequestException
	 * @
	 */

	Boolean getUpdateSSCCExceptCareDesigStatus(SSCCExceptCareDesiReq sSCCExceptCareDesiReq);

	/**
	 * This is a list service that Updates CD_EXCEPT_CARE_STATUS in the
	 * SSCC_LIST table
	 *
	 * @param SSCCExceptCareDesiReq
	 * @return SSCCExceptCareDesiRes
	 * @throws InvalidRequestException
	 * @
	 */

	Boolean getUpdateCdExceptCareStatus(SSCCExceptCareDesiReq sSCCExceptCareDesiReq);

	SSCCExceptCareDesiRes getUpdateSSCCExceptCare(SSCCExceptCareDesiReq sSCCExceptCareDesiReq);

	SSCCExceptCareDesiRes getExceptCareList(CommonHelperReq commonHelperReq);

	SSCCExceptCareDesiRes insertTimeLine(SSCCExceptCareDesiReq sSCCExceptCareDesiReq);

}