package us.tx.state.dfps.service.ppm.dao;

import java.util.List;

import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.common.request.PPMInfoReq;
import us.tx.state.dfps.service.forms.dto.PptDetailsOutDto;
import us.tx.state.dfps.service.person.dto.PPTParticipantDto;
import us.tx.state.dfps.service.servicedelivery.dto.ServiceDeliveryRtrvDtlsInDto;
import us.tx.state.dfps.service.servicedelivery.dto.ServiceDeliveryRtrvDtlsOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Dao
 * interface for Permanency Planning Meeting Information Feb 2, 2018- 6:48:27 PM
 * © 2017 Texas Department of Family and Protective Services
 */
public interface PPMDao {

	/**
	 * 
	 * Method Name: getPptAddress Method Description: This method retrieves the
	 * Participant address data using idPptEvent
	 * 
	 * @param idPptEvent
	 * @return @
	 */
	public PptDetailsOutDto getPptAddress(Long idPptEvent);

	/**
	 * 
	 * Method Name: getParticipantData Method Description: This method retrieves
	 * the Participant data using idPptPart
	 * 
	 * @param idPptPart
	 * @return @
	 */
	public PPTParticipantDto getParticipantData(Long idPptPart);

	/**
	 * 
	 * Method Name: getParticipantList Method Description: This method retrieves
	 * the Participant list using idPptEvent
	 * 
	 * @param idPptEvent
	 * @return List<PPTParticipantDto> @
	 */
	public List<PPTParticipantDto> getParticipantList(Long idPptEvent);

	/**
	 * Method Name: saveOrUpdatePPM Method Description: This method
	 * inserts/updates MMP info
	 * 
	 * @param pptDetailsOutDto
	 * @return PptDetailsOutDto
	 */
	public PptDetailsOutDto saveOrUpdatePPM(PptDetailsOutDto pptDetailsOutDto);

	/**
	 * Method Name: deletePPTParticipant Method Description: This method deletes
	 * PPT participant
	 * 
	 * @param idPptPart
	 * @return Boolean
	 */
	public Boolean deletePPTParticipant(Long idPptPart);

	/**
	 * Method Name: getPPTDetails Method Description: Get PPT details
	 * 
	 * @param idPptEvent
	 * @return PptDetailsOutDto
	 */
	public PptDetailsOutDto getPPTDetails(Long idPptEvent);

	/**
	 * csys06dQUERYdam - This DAM is used by a service delivery window.
	 * 
	 * @param pInputDataRec
	 * @return liCsys06doDto
	 * @throws DataNotFoundException
	 */
	ServiceDeliveryRtrvDtlsOutDto getPPTNarrDetails(ServiceDeliveryRtrvDtlsInDto pInputDataRec);

	/**
	 * Method Name: insertOrUpdatePPTInfo Method Description: This Method
	 * Performs AUD operations on PPT table
	 * 
	 * DAM Name: CAUD09D
	 */
	ServiceDeliveryRtrvDtlsOutDto insertOrUpdatePPTInfo(PptDetailsOutDto PptDetailsOutDto,
			ServiceReqHeaderDto archInputDto);

	ServiceDeliveryRtrvDtlsOutDto updateToDoTable(PPMInfoReq PPMInfoReq, ServiceReqHeaderDto archInputDto);

	/**
	 * Method Name: savePPTParticipant Method Description: Method Signature for
	 * Save or Update of PPT Participant.
	 * 
	 * @param pptParticipantDto
	 */
	void savePPTParticipant(PPTParticipantDto pptParticipantDto);

	/**
	 * 
	 * Method Name: populateAddressPpt Method Description: CCMN14D This DAM will
	 * perform a full row retrieval from PPT when the host input variable ID
	 * event matches an element in the table.
	 * 
	 * @param idPptEvent
	 * @return PptDetailsOutDto
	 */
	public PptDetailsOutDto populatePptAddress(Long idPptEvent);
}
