package us.tx.state.dfps.service.investigation.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.domain.FacilAlleg;
import us.tx.state.dfps.service.common.request.GetFacilAllegDetailReq;
import us.tx.state.dfps.service.common.request.UpdtFacilAllegMultiDtlReq;
import us.tx.state.dfps.service.investigation.dto.AllegtnPrsnDto;
import us.tx.state.dfps.service.investigation.dto.FacilAllegDetailDto;
import us.tx.state.dfps.service.investigation.dto.FacilAllegInjuryDto;
import us.tx.state.dfps.service.investigation.dto.FacilInvstFacilDto;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:
 * CINV07S,CINV08S,CINV10S Class Description: This class is to
 * retrieves,saves,updates,multi update Facility Allegation Detail page.
 */
public interface FacilAllgDtlDao {

	/**
	 * 
	 * Method Description: Populates the Allegation List for Facility
	 * Allegations. legacy DAM name - CINV70S
	 * 
	 * @param idAllegation
	 * @return @
	 */
	public FacilAllegDetailDto getallegtnlist(GetFacilAllegDetailReq getFacilAllegDetailReq);

	/**
	 * 
	 * Method Description:This DAM retrieves data from the Facility Injury table
	 * to populate the Injury List/Detail window. legacy DAM name - CINV08D
	 * 
	 * @param idAllegation
	 * @return @
	 */
	public List<FacilAllegInjuryDto> getInjuryDtl(GetFacilAllegDetailReq getFacilAllegDetailReq);

	/**
	 * 
	 * Method Description: Retrieves data from PERSON and STAGE_PERSON_LINK for
	 * Allegation Detail and Facility Allegation Detail. legacy DAM name -
	 * CINVF8D
	 * 
	 * @param getFacilAllegDetailReq
	 * @return @
	 */
	public List<AllegtnPrsnDto> getFacilAllegDtl(GetFacilAllegDetailReq getFacilAllegDetailReq);

	/**
	 * 
	 * Method Description: Gets the most recently closed previous ID STAGE for a
	 * given ID STAGE. Retrieves DtIncomingCall from Incoming Detail table.
	 * legacy DAM name - CSEC54D,CCMNB5D
	 * 
	 * @param idStage
	 * @return @
	 */
	public Date getdtIncCall(GetFacilAllegDetailReq getFacilAllegDetailReq);

	/**
	 * 
	 * Method Description: Get's the list of facilities associated with the
	 * investigation legacy EJB Service name - Cinv07sEJB
	 * 
	 * @param getFacilAllegDetailReq
	 * @return @
	 */
	public List<FacilInvstFacilDto> getFacilitysInvCnclsnList(GetFacilAllegDetailReq getFacilAllegDetailReq);

	/**
	 * 
	 * Method Description:Retrieve the facility associated with the allegation
	 * legacy EJB Service name - Cinv07sEJB
	 * 
	 * @param getFacilAllegDetailReq
	 * @return @
	 */
	public FacilInvstFacilDto getFacilityInvCnclsn(GetFacilAllegDetailReq getFacilAllegDetailReq);

	/**
	 * 
	 * Method Description: Retrieves blank overall Disposition FAC.
	 * 
	 * @param idStage
	 * @
	 */
	void callBlankOverallDispositionFAC(Long idStage);

	/**
	 * 
	 * Method Description: updates overall Disposition FAC.
	 * 
	 * @param idStage
	 * @param cdDispositon
	 * @
	 */
	void updateOverallDispositionFAC(Long idStage, String cdDispositon);

	/**
	 * 
	 * Method Description: Deletes facil alleg details.
	 * 
	 * @param idAllegation
	 * @
	 */
	void deleteFacilAlleg(Long idAllegation);

	/**
	 * 
	 * Method Description: Loads facil alleg details.
	 * 
	 * @param idAllegation
	 * @
	 */
	FacilAlleg loadFacilAllegation(Long idAllegation);

	/**
	 * 
	 * Method Description: updates facil alleg details.
	 * 
	 * @param facilAlleg
	 * @param operation
	 * @param indFlush
	 * @
	 */
	Long updateFacilAlleg(FacilAlleg facilAlleg, String operation, boolean indFlush);

	/**
	 * 
	 * Method Description: This Method will changes the Event Status of a given
	 * event regardless of timestamp although timestamp is updated Dam Name:
	 * CCMN62D
	 * 
	 * @param ServiceReqHeaderDto
	 * @param eventDto
	 * @return ServiceResHeaderDto @
	 */
	public String getEventDetailsUpdate(Long idEvent, String cdEventStatus);

	/**
	 * 
	 * Method Description: Updates multiple facility allegation records with the
	 * same disposition and findings. Dam Name: cinv76d
	 * 
	 * @param facilAllegDetailDto
	 * @param cdReqFunc
	 * @
	 */
	public void updateMultiFacilAllgWithDisp(FacilAllegDetailDto facilAllegDetailDto, String cdReqFunc);

	/**
	 * 
	 * Method Description: Retrieves ID_CASE from Stage table given ID_STAGE.
	 * Dam Name: ccmnb6d
	 * 
	 * @param updtFacilAllegMultiDtlReq
	 * @
	 */
	public FacilAllegDetailDto retriveIdCase(UpdtFacilAllegMultiDtlReq updtFacilAllegMultiDtlReq);

}
