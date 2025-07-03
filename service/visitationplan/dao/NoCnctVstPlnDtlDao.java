package us.tx.state.dfps.service.visitationplan.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import us.tx.state.dfps.common.domain.VisitPlanNoCntct;
import us.tx.state.dfps.common.domain.VisitPlanPartcpnt;
import us.tx.state.dfps.service.common.request.NoCnctVstPlnDtlReq;
import us.tx.state.dfps.service.common.request.VisitationPlanDtlReq;
import us.tx.state.dfps.service.common.response.VisitationPlanDtlRes;
import us.tx.state.dfps.visitationplan.dto.NoCnctVstPlnDetailDto;
import us.tx.state.dfps.visitationplan.dto.VisitationPlanDetailDto;
import us.tx.state.dfps.visitationplan.dto.VstPlanPartcpntDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:this class
 * is for No Contact Visitation Plan Detail service Sep 20, 2018- 12:08:39 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface NoCnctVstPlnDtlDao {

	/**
	 * Method Name: populateSaveNoContactVisitationPlnDetails Method
	 * Description:
	 * 
	 * @param noCnctVstPlnDetailDto
	 * @return Long
	 */
	Long saveNoContactVisitationPlnDetails(NoCnctVstPlnDetailDto noCnctVstPlnDetailDto);

	/**
	 * Method Name: reteriveVisitPlanPartcpnt Method Description:
	 * 
	 * @param idnoCnctVstPlanPartcpnt
	 * @param idStage
	 * @return List<VstPlanPartcpntDto>
	 */
	List<VstPlanPartcpntDto> reteriveVisitPlanPartcpnt(Long idVstPlan, Long idStage, Date dtCreated, String plan);

	/**
	 * Method Name: reteriveNoContactVisitPlan Method Description:
	 * 
	 * @param idEvent
	 * @param idStage
	 * @return NoCnctVstPlnDetailDto
	 */
	NoCnctVstPlnDetailDto reteriveNoContactVisitPlan(Long idEvent, Long idStage);

	/**
	 * Method Name: saveVisitPlanPartcpnt Method Description:
	 * 
	 * @param noCnctVstPlnDetailDto
	 * @param visitPlanNoCntct
	 * @return List<VisitPlanPartcpnt>
	 */
	List<VisitPlanPartcpnt> populateSaveVisitPlanPartcpnt(NoCnctVstPlnDetailDto noCnctVstPlnDetailDto,
			VisitPlanNoCntct visitPlanNoCntct);

	/**
	 * Method Name: updateNoContactVisitationPlnDetails Method Description:
	 * 
	 * @param noCnctVstPlnDtlReq
	 * @return NoCnctVstPlnDetailDto
	 */
	NoCnctVstPlnDetailDto updateNoContactVisitationPlnDetails(NoCnctVstPlnDtlReq noCnctVstPlnDtlReq);

	/**
	 * 
	 * Method Name: reteriveVisitationPlanDetail Method Description: This method
	 * is used to get the visitation Plan Detail.
	 * 
	 * @param idEvent
	 * @param idStage
	 * @return VisitationPlanDetailDto
	 */
	public VisitationPlanDetailDto reteriveVisitationPlanDetail(Long idEvent, Long idStage);

	/**
	 * 
	 * Method Name: saveVisitationPlnDetail Method Description: This method is
	 * used to save the Visitation Plan detail Screen.
	 * 
	 * @param visitationPlanDetailDto
	 * @return Long
	 */
	public Long saveVisitationPlnDetail(VisitationPlanDetailDto visitationPlanDetailDto);

	/**
	 * 
	 * Method Name: updateVisitationPlnDetail Method Description: This method is
	 * used to update the existing visitation Plan.
	 * 
	 * @param visitationPlanDtlReq
	 * @return VisitationPlanDetailDto
	 */
	public VisitationPlanDetailDto updateVisitationPlnDetail(VisitationPlanDtlReq visitationPlanDtlReq);

	/**
	 * 
	 * Method Name: deleteVisitationPlanDtl Method Description:This method is
	 * used to delete the visitation Plan and No contact visitation Plan.
	 * 
	 * @param idEvent
	 * @return String
	 */
	public String deleteVisitationPlanDtl(Long idEvent);

	/**
	 * 
	 * Method Name: visitationPlanExist Method Description:This method is used
	 * to find out Whether a Visitation Plan for exists for given participant.
	 * 
	 * @param idStage
	 * @param participantMap
	 * @return VisitationPlanDtlRes
	 */
	public VisitationPlanDtlRes visitationPlanExist(Long idStage,
			HashMap<VstPlanPartcpntDto, List<VstPlanPartcpntDto>> participantMap, Long idEvent);
}
