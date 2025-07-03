package us.tx.state.dfps.service.prt.service;

import us.tx.state.dfps.service.common.request.PRTActionPlanReq;
import us.tx.state.dfps.service.common.request.PRTActplanFollowUpReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION Class Description: This
 * method is used to call the DAO to fetch PRT contact Action Plan Response data
 * to return back to the controller July 2, 2018 - 12:44:36 PM © 2017 Texas
 * Department of Family and Protective Services
 * 
 */
public interface PRTContactService {
	/**
	 * Method Description: This service will get forms populated by receiving
	 * BasePRTSessionReq from controller, then retrieving data for
	 * PRTContactActionPlan form, then get them returned to
	 * prefillDataServiceDto
	 *
	 * @param BasePRTSessionReq
	 * @return PreFillDataServiceDto
	 */
	public PreFillDataServiceDto getPRTContactActionPopulated(PRTActionPlanReq pRTActionPlanReq);

	/**
	 * Method Description: This service will get forms populated by receiving
	 * BasePRTSessionReq from controller, then retrieving data for
	 * PRTContactFollowUp form, then get them returned to prefillDataServiceDto
	 *
	 * @param BasePRTSessionReq
	 * @return PreFillDataServiceDto
	 */
	public PreFillDataServiceDto getPRTContactFollowUpPopulated(PRTActplanFollowUpReq pRTActplanFollowUpReq);
}
