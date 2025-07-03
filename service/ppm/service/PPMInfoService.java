package us.tx.state.dfps.service.ppm.service;

import java.util.List;

import us.tx.state.dfps.service.common.request.PPMInfoReq;
import us.tx.state.dfps.service.common.response.PPMInfoRes;
import us.tx.state.dfps.service.forms.dto.PptDetailsOutDto;
import us.tx.state.dfps.service.person.dto.PPTParticipantDto;
import us.tx.state.dfps.service.ppm.dto.PPMInfoDto;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION Class Description: This
 * class is used to call the DAO to populate PPM Information parameters and
 * return back to the controller Sept 20, 2018 - 12:44:36 PM © 2018 Texas
 * Department of Family and Protective Services
 * 
 */
public interface PPMInfoService {

	/**
	 * Method Name: getParticipants Method Description: Fetch PPM participants
	 *
	 * @param idEvent
	 * @return ppmInfoDto
	 */
	public PPMInfoDto getParticipants(Long idEvent);

	/**
	 * Method Name: getPPMInfo Method Description: Fetch meeting
	 * information/location and PPM participants
	 *
	 * @param idEvent
	 * @return ppmInfoDto
	 */
	public PPMInfoDto getPPMInfo(Long idEvent);

	/**
	 * Method Name: deletePPTParticipant Method Description:deletes PPT
	 * Participant
	 * 
	 * @param ppmInfoDto
	 * @return Boolean
	 */
	public Boolean deletePPTParticipant(PPMInfoDto ppmInfoDto);

	/**
	 * Method Name: persistPPM Method Description: saves PPM information
	 * 
	 * @param ppmInfoDto
	 * @return PptDetailsOutDto
	 */
	public PptDetailsOutDto savePPM(PPMInfoDto ppmInfoDto);

	/**
	 * Method Name: getPPTParticipant Method Description: Method Signature to
	 * fetch the Saved Participant Details.
	 * 
	 * @param idPPTParticipant
	 * @return
	 */
	PPTParticipantDto getPPTParticipant(Long idPPTParticipant);

	/**
	 * Method Name: pptParticipantAUD Method Description: Method Signature for
	 * PPT Participant AUD: CSUB28S
	 * 
	 * @param pptParticipantDto
	 * @param reqFuncCd
	 * @return
	 */
	PPMInfoRes pptParticipantAUD(PPTParticipantDto pptParticipantDto, String reqFuncCd);

	/**
	 * Method Name: insertOrUpdatePPTInfo Method Description: This Method
	 * Performs AUD operations on PPT table based on the IdPptEvent
	 * 
	 * DAM Name: CAUD09D
	 */
	public PPMInfoRes savePPMInfo(PPMInfoReq ppmInfoReq);

	/**
	 * Method Name:getParticipantList Method description: This method fetches
	 * entire row from table (PPT_PARTICIPANT) based on the ID_EVENT passed. DAM
	 * Name: CSUB27S or EJB(getPPTParticipantList) Both query is same
	 */
	List<PPTParticipantDto> getParticipantList(PPMInfoReq ppmInfoReq);

	/**
	 * Method Name: fetchppmInfo Method Description: Fetch meeting
	 * information/location and PPM participants
	 *
	 * @param idEvent
	 * @return ppmInfoDto
	 */
	public PPMInfoDto fetchppmInfo(Long idEvent);
}
