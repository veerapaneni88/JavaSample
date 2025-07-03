package us.tx.state.dfps.service.visitationplan.service;

import us.tx.state.dfps.service.common.request.NoCnctVstPlnDtlReq;
import us.tx.state.dfps.service.common.request.VisitationPlanDtlReq;
import us.tx.state.dfps.service.common.response.NoCnctVstPlnDtlRes;
import us.tx.state.dfps.service.common.response.VisitationPlanDtlRes;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.visitationplan.dto.VisitationPlanDetailDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:this class
 * is for No Contact Visitation Plan Detail service Sep 20, 2018- 12:08:39 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface NoCnctVstPlnDtlService {

	/**
	 * Method Name: retrieveNoContactVisitationPlnDetails Method Description: to
	 * retrieve no contact visitation plan detail
	 * 
	 * @param idStage
	 * @param idEvent
	 * @return NoCnctVstPlnDtlRes
	 */
	NoCnctVstPlnDtlRes retrieveNoContactVisitationPlnDetails(Long idStage, Long idEvent);

	/**
	 * Method Name: saveNoContactVisitationPlnDetails Method Description: To
	 * save no contact visitation plan details
	 * 
	 * @param noCnctVstPlnDtlReq
	 * @return NoCnctVstPlnDtlRes
	 */
	NoCnctVstPlnDtlRes saveNoContactVisitationPlnDetails(NoCnctVstPlnDtlReq noCnctVstPlnDtlReq);

	/**
	 * Method Name: retrieveVisitationPlnDetail Method Description: To get the
	 * visitation Plan detail.
	 * 
	 * @param idStage
	 * @param idEvent
	 * @param idCase
	 * @return VisitationPlanDetailDto
	 */
	VisitationPlanDetailDto retrieveVisitationPlnDetail(Long idStage, Long idEvent, Long idCase);

	/**
	 * 
	 * Method Name: saveVisitationPlnDetail Method Description: This method is
	 * used to Save/Update the New/Existing visitation Plan.
	 * 
	 * @param visitationPlanDtlReq
	 * @return VisitationPlanDtlRes
	 */
	VisitationPlanDtlRes saveVisitationPlnDetail(VisitationPlanDtlReq visitationPlanDtlReq);

	/**
	 * 
	 * Method Name: deleteVisitationPlanDtl Method Description:This method is
	 * used to delete the visitation Plan and No contact visitation Plan.
	 * 
	 * @param idEvent
	 * @return String
	 */
	public String deleteVisitationPlanDtl(Long idEvent);

	PreFillDataServiceDto retrieveVisitationPlnFormDetail(Long idStage, Long idEvent, Long idCase, String nmCase);

	PreFillDataServiceDto retrieveNoContactVisitationPlnFormDetails(Long idStage, Long idEvent, Long idCase,
			String nmCase);
}
