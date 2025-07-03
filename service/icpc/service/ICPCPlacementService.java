/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 03, 2018- 4:23:34 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.icpc.service;

import java.util.List;
import java.util.Map;

import us.tx.state.dfps.common.domain.IcpcEventLink;
import us.tx.state.dfps.common.domain.IcpcRequest;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.ICPCEmailLogReq;
import us.tx.state.dfps.service.common.request.ICPCPlacementReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.FTRelationshipRes;
import us.tx.state.dfps.service.common.response.ICPCPlacementRes;
import us.tx.state.dfps.service.common.response.ListTransmissionRes;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationDto;
import us.tx.state.dfps.service.icpc.dto.*;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service
 * Interface for the ICPC page services Aug 03, 2018- 4:23:34 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ICPCPlacementService {

	/**
	 * 
	 * Method Name: getSummaryInfo Method Description: Get the ICPC Summary Info
	 * for the Case
	 * 
	 * @param idCase
	 *            - Case ID
	 * @return ICPCPlacementRequestDto
	 */
	public ICPCPlacementRequestDto getSummaryInfo(Long idCase);

	/**
	 * Method Name: getTransmittalInfo Method Description: Get the transmittal
	 * details for the passed transmittal ID and the ICPC Request ID
	 * 
	 * @param idTransmittal
	 *            - Transmittal ID
	 * @param idrequest
	 *            - ICPC Request ID
	 * @param idStage
	 *            - Stage ID
	 * @return ICPCTransmittalDto
	 */
	public ICPCTransmittalDto getTransmittalInfo(Long idTransmittal, Long idrequest, Long idStage);

	/**
	 * 
	 * Method Name: getPlacementStatusInfo Method Description: get ICPC status
	 * report detail information for display Method Description:
	 * 
	 * @param idEvent
	 * @return
	 */
	public ICPCPlacementStatusDto getPlacementStatusInfo(Long idEvent);

	/**
	 * Method Name: saveTransmittal Method Description: This method saves the
	 * transmittal detail page details.
	 * 
	 * @param icpcTransmittalDto
	 * @return Long
	 */
	public Long saveTransmittal(ICPCTransmittalDto icpcTransmittalDto);

	/**
	 * 
	 * Method Name: getICPCPlacementRequestDetail Method Description: get all
	 * ICPC placement request detail
	 * 
	 * @param idEvent
	 * @param idStage
	 * @param eligibility
	 * @return ICPCPlacementRequestDto
	 */

	public ICPCPlacementRequestDto getICPCPlacementRequestDetail(Long idEvent, Long idStage, Boolean eligibility);

	/**
	 * Method Name: deleteTransmittal Method Description: Delete the transmittal
	 * details to the database from Placement Request Details page
	 * 
	 * @param icpcPlacementReq
	 * @return void
	 */
	public void deleteICPCTransmittal(Long idIcpcTransmittal);

	/**
	 * 
	 * Method Name: saveICPCSummary Method Description:save the ICPC Summary
	 * details
	 * 
	 * @param iCPCPlacementRequestDto
	 */
	public void saveICPCSummary(ICPCPlacementRequestDto iCPCPlacementRequestDto);

	/**
	 * 
	 * Method Name: savePlacementRequest Method Description:save the ICPC
	 * Placement Request details
	 * 
	 * @param ICPCPlacementRequestDBDto
	 * @return Long
	 */
	public ICPCPlacementRequestDto savePlacementRequest(ICPCPlacementRequestDBDto icpcPlacementRequestDBDto);

	/**
	 * Method Name: updatePlacementRequest Method Description: update the ICPC
	 * Placement Request details
	 * 
	 * @param ICPCPlacementRequestDBDto
	 * @return List<ICPCAgencyDto>
	 */
	public List<ICPCAgencyDto> updatePlacementRequest(ICPCPlacementRequestDBDto icpcPlacementRequestDBDto);

	/**
	 * 
	 * Method Name: getPersonRelation Method Description: this method to get
	 * FTPersonRelationDto for guardian Method Description:
	 * 
	 * @param idRelatedPerson
	 * @param idPersonRelation
	 * @return FTPersonRelationDto
	 */
	public FTPersonRelationDto getPersonRelation(Long idPersonRelation, Long idRelatedPerson);

	// ICPCPlacement Request Selector Page

	/**
	 * Method Name:getListPlacementRequest Method Description: This method
	 * generates the list for PEND and APRV Status which has a Placement 100-A
	 * Record
	 * 
	 * @param ICPCPlacementReq
	 * @return ICPCPlacementRes
	 */

	public ICPCPlacementRes getListPlacementRequest(ICPCPlacementReq icpcPlacementReq);

	/**
	 * Method Name: getIcpcDocument Method Description: get the document details
	 * for the given idICPCDocuments
	 * 
	 * @param idICPCDocuments
	 * @return List<ICPCDocumentDto>
	 */
	public List<ICPCDocumentDto> getIcpcDocument(List<Long> idICPCDocuments);

	/**
	 * 
	 * Method Name: savePlacementStatus Method Description: this method is to
	 * save placement status record
	 * 
	 * @param icpcPlacementReq
	 * @return
	 */
	public ICPCPlacementRes savePlacementStatus(ICPCPlacementReq icpcPlacementReq);

	/**
	 * 
	 * Method Name: getIcpcDocument Method Description: this method is to update
	 * ICPC placement status report detail page
	 *
	 * @param icpcEmailDocumentReq
	 * @return ICPCDocumentRes
	 */
	public ICPCPlacementRes updatePlacementStatus(ICPCPlacementReq icpcPlacementReq);

	/**
	 * 
	 * Method Name: updateIcpcEmailDtlLog Method Description: This method is to
	 * update ICPC placement Email detail Log when emai has been sent.
	 *
	 * @param icpcEmailLogReq
	 * @return CommonStringRes
	 */
	public CommonStringRes updateIcpcEmailDtlLog(ICPCEmailLogReq icpcEmailLogReq);

	/**
	 * 
	 * Method Name: getPersonRelation Method Description:
	 * 
	 * @param commonHelperReq
	 * @return
	 */
	public FTRelationshipRes getPersonRelation(CommonHelperReq commonHelperReq);

	/**
	 * Method Name: getRecentAprvEvent Method Description:This method is used to
	 * retrieve most recent approved CPS idEvent.
	 * 
	 * @param idStage
	 * @return Long
	 */
	public Long getRecentAprvEvent(Long idStage);

	/**
	 * 
	 * Method Name: deletePlacementRequest Method Description:Delete the ICPC
	 * Placement Request details
	 * 
	 * @param ICPCPlacementRequestDBDto
	 * @return Long
	 */
	public Long deletePlacementRequest(ICPCPlacementRequestDBDto icpcPlacementRequestDBDto);

	/**
	 * Method Name:	getTransmissionLst
	 * Method Description: This method is used to retrieve the matched pending Home Study Request else all
	 * the pending Home Study Request
	 *
	 * @param idStage
	 * @return ListTransmissionRes
	 */
	ListTransmissionRes getTransmissionLst(Long idStage);

	/**
	 *
	 * @param idIcpcTransmittal
	 * @return ListTransmissionRes
	 */
	public ListTransmissionRes getTransmissionChildLst(Long idIcpcTransmittal);

	/**
	 *
	 * @param idIcpcTransmittal
	 * @param idPersonNeice
	 * @return
	 */
	public ListTransmissionRes getTransmissionAttachments(Long idIcpcTransmittal, Long idPersonNeice);

	/**
	 *  @param icpcPlacementReq
	 *
	 */
	public void saveNeiceTransmission(ICPCPlacementReq icpcPlacementReq);

	/**
	 *
	 * @param stateNm
	 * @return ListTransmissionRes
	 */
	public ListTransmissionRes getSendingAgencyInfo(String stateNm);

	/**
	 *
	 * @param eventId
	 * @return IcpcEventLink
	 */
	public IcpcEventLink getIcpcEventLinkInfo(Long eventId);

	/**
	 *
	 * @param idICPCRequest
	 * @return Map<Long, String>
	 */
	public Map<Long, String> getIcpcRequestInfo(Long idICPCRequest);

	/**
	 * Method Name: validateCreateTransmittal
	 * Method Description: Retrieve the Placement Status Details and Document Details for Create
	 * Transmittal Validation
	 *
	 * @param icpcTransmittalDto
	 * @return ICPCPlacementRes
	 */
	ICPCPlacementRes validateCreateTransmittal(ICPCTransmittalDto icpcTransmittalDto);

	/**
	 * Method Name: saveTransmittalDocs
	 * Method Description: Save the Transmittal selected documents for Transmission
	 *
	 * @param icpcTransmittalDto
	 * @return ICPCPlacementRes
	 */
	void saveTransmittalDocs(ICPCTransmittalDto icpcTransmittalDto);

	/**
	 * Method Name: saveTransmittalStatus
	 * Method Description: Save the Transmittal Status received from MuleSoft
	 *
	 * @param icpcTransmittalDto
	 * @return ICPCPlacementRes
	 */
	void saveTransmittalStatus(ICPCTransmittalDto icpcTransmittalDto);

	/**
	 * Method Name: saveTransmittalOtherMode
	 * Method Description: Save the Transmittal Other Mode
	 *
	 * @param icpcTransmittalDto
	 */
	void saveTransmittalOtherMode(ICPCTransmittalDto icpcTransmittalDto);

	/**
	 *
	 * @param idAttachment
	 * @return
	 */
    ListTransmissionRes getTransmissionAttachment(Long idAttachment);
}
