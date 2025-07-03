/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: DAO Interface for the ICPC page services
 *Aug 03, 2018- 4:24:06 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.icpc.dao;

import java.util.List;
import java.util.Map;

import us.tx.state.dfps.common.domain.IcpcEventLink;
import us.tx.state.dfps.common.domain.IcpcRequestPersonLink;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.common.request.ICPCEmailLogReq;
import us.tx.state.dfps.service.common.request.ICPCPlacementReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationDto;
import us.tx.state.dfps.service.icpc.dto.ICPCAgencyDto;
import us.tx.state.dfps.service.icpc.dto.ICPCDocumentDto;
import us.tx.state.dfps.service.icpc.dto.ICPCPersonDto;
import us.tx.state.dfps.service.icpc.dto.ICPCPlacementRequestDto;
import us.tx.state.dfps.service.icpc.dto.ICPCPlacementStatusDto;
import us.tx.state.dfps.service.icpc.dto.ICPCRequestDto;
import us.tx.state.dfps.service.icpc.dto.ICPCResourceDto;
import us.tx.state.dfps.service.icpc.dto.ICPCTransmissionDto;
import us.tx.state.dfps.service.icpc.dto.ICPCTransmittalDto;
import us.tx.state.dfps.service.icpc.dto.NeiceStateParticpantDTO;
import us.tx.state.dfps.service.workload.dto.TodoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: DAO
 * Interface for the ICPC page services Aug 03, 2018- 4:24:06 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ICPCPlacementDao {

	/**
	 * 
	 * Method Name: getSummaryInfo Method Description: Get the ICPC Summary Info
	 * for the Case
	 * 
	 * @param idCase
	 * @return ICPCPlacementRequestDto
	 */
	public ICPCPlacementRequestDto getSummaryInfo(Long idCase);

	/**
	 * 
	 * Method Name: getTransmittalListInfo Method Description: Get the
	 * Transmittal List for the ICPC Request
	 * 
	 * @param idICPCRequest
	 * @return List<ICPCTransmittalDto>
	 */
	public List<ICPCTransmittalDto> getTransmittalListInfo(Long idICPCRequest);

	/**
	 * 
	 * Method Name: getAllTransmittalList Method Description: Get all the ICPC
	 * Transmittal List for the Case
	 * 
	 * @param idCase
	 * @return List<ICPCTransmittalDto>
	 */
	public List<ICPCTransmittalDto> getAllTransmittalList(Long idCase);

	/**
	 * 
	 * Method Name: getAllRequestList Method Description: Get all the ICPC
	 * Request List for the Case
	 * 
	 * @param idCase
	 * @return List<ICPCTransmittalDto>
	 */
	public List<ICPCRequestDto> getAllRequestList(Long idCase);

	/**
	 * Method Name: getTransmittalInfo Method Description: Get the Transmittal
	 * Info for the passed ICPC request ID or Transmittal ID
	 * 
	 * @param idTransmittal
	 * @param idICPCRequest
	 * @param idStage
	 * @return ICPCTransmittalDto
	 */
	public ICPCTransmittalDto getTransmittalInfo(Long idTransmittal, Long idICPCRequest, Long idStage);

	/**
	 * Method Name: getPlacementResourceName Method Description: Gets the
	 * resource name for the passed ICPC Request ID
	 * 
	 * @param idICPCRequest
	 * @return String
	 */
	public String getPlacementResourceName(Long idICPCRequest);

	/**
	 * Method Name: getTransmittalDetails Method Description: Get the
	 * Transmittal Details for the passed Transmittal ID
	 * 
	 * @param idTransmittal
	 * @return Map<String, String>
	 */
	public Map<String, String> getTransmittalDetails(Long idTransmittal);

	/**
	 * Method Name: getSiblings Method Description: Get the list of Siblings for
	 * the passed ICPC Request ID
	 * 
	 * @param idICPCRequest
	 * @return List<ICPCPersonDto>
	 */
	public List<ICPCPersonDto> getSiblings(Long idICPCRequest);

	/**
	 * Method Name: getSelectedChildren Method Description: <To be Added>
	 * 
	 * @param idTransmittal
	 * @return Map<Long, Long>
	 */
	public Map<Long, Long> getSelectedChildren(Long idTransmittal);

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
	 * 
	 * Method Name: getPersonRelation this method to get FTPersonRelationDto for
	 * guardian Method Description:
	 * 
	 * @param idPerson
	 * @param idPerson2
	 * @return
	 */
	public FTPersonRelationDto getPersonRelation(Long idPerson, Long idPerson2);

	/**
	 * Method Name: saveTransmittal Method Description: This method saves the
	 * transmittal detail page details.
	 * 
	 * @param icpcTransmittalDto
	 * @return Long
	 */
	public Long saveTransmittal(ICPCTransmittalDto icpcTransmittalDto);

	/**
	 * Method Name: getAllAgencyICPCVector Method Description: get all agency
	 * information
	 * 
	 * @return List<ICPCDocumentDto>
	 */
	public List<ICPCDocumentDto> getDocumentListInfo(Long idICPCRequest);

	/**
	 * Method Name: getAllAgencyICPCVector Method Description: get all agency
	 * information
	 * 
	 * @param idEvent
	 * @param idStage
	 * @return ICPCPlacementRequestDto
	 */
	public ICPCPlacementRequestDto getICPCPlacementRequestInfo(Long idEvent, Long idStage);

	/**
	 * 
	 * Method Name: getStatusPersonAddressLink Method Description: this method
	 * to get status person address link record
	 * 
	 * @param idIcpcPlacementStatus
	 * @param cdAddrType
	 * @return
	 */
	Long getStatusPersonAddressLink(Long idIcpcPlacementStatus, String cdAddrType);

	/**
	 * 
	 * Method Name: getStatusResourceAddressLink Method Description: this method
	 * to get status resource address link record
	 * 
	 * @param idIcpcPlacementStatus
	 * @param cdAddrType
	 * @return
	 */
	Long getStatusResourceAddressLink(Long idIcpcPlacementStatus, String cdAddrType);

	/**
	 * 
	 * Method Name: getrequestEnclosure Method Description:This method to get
	 * CD_ENCLOSURE for given id_event
	 * 
	 * @param idEvent
	 * @return
	 */
	List<String> getrequestEnclosure(Long idEvent);

	/**
	 * Method Name: getPrimaryWorkerInfo Method Description: get the Primary
	 * Worker Info
	 * 
	 * @param idStage
	 * @return ICPCPersonDto
	 */
	public ICPCPersonDto getPrimaryWorkerInfo(Long idStage);

	/**
	 * Method Name: deleteICPCTransmittal Method Description: Delete the
	 * transmittal details to the database from Placement Request Details page
	 * 
	 * @param idIcpcTransmittal
	 * @return void
	 */
	public void deleteICPCTransmittal(Long idIcpcTransmittal);

	/**
	 * Method Name: getEligibility Method Description: get Eligibility
	 * information
	 * 
	 * @param idPersonChild
	 * @return String
	 */
	public String getEligibility(Long idPersonChild);

	/**
	 * Method Name: getDisasterRlf Method Description: get Disaster Relief
	 * information
	 * 
	 * @param idPersonChild
	 * @param idICPCPlacementRequest
	 * @return String
	 */
	public String getDisasterRlf(Long idPersonChild, Long idICPCPlacementRequest);

	/**
	 * 
	 * Method Name: retrieveStageId Method Description:gets the idStage for the
	 * given idCase and INT stage
	 * 
	 * @param idCase
	 * @param cdStage
	 * @return
	 */
	public Long retrieveIntakeStageId(Long idCase, String cdStage);

	/**
	 * 
	 * Method Name: getIdICPCSubmission Method Description:Get a new Submission
	 * id
	 * 
	 * @param idStage
	 * @return Long
	 */
	public Long getIdICPCSubmission(Long idStage);

	/**
	 * Method Name: getIdICPCSubmissionByRequest Method Description:Get a new Submission id
	 * @param idIcpcRequest
	 * @return
	 */
	public Long getIdICPCSubmissionByRequest(Long idIcpcRequest);

	/**
	 * 
	 * Method Name: insertICPCSubmission Method Description: This method inserts
	 * record into ICPC_SUBMISSION table.
	 * 
	 * @param idStage
	 * @param idIcpcReqSubmission
	 * @param confirmation
	 * @param icpcCaseOtherState
	 * @param idLastUpdatePerson
	 * @return Long
	 */
	public Long insertICPCSubmission(Long idStage, Long idIcpcReqSubmission, String confirmation,
			String icpcCaseOtherState, Long idLastUpdatePerson);

	/**
	 * 
	 * Method Name: insertICPCRequest Method Description: This method inserts
	 * record into ICPC_REQUEST table.
	 * 
	 * @param idIcpcSubmission
	 * @param idIcpcRequestPrtl
	 * @param cdRequestType
	 * @param cdReceivingState
	 * @param cdSendingState
	 * @param idLastUpdatePerson
	 * @return Long
	 */
	public Long insertICPCRequest(Long idIcpcSubmission, Long idIcpcRequestPrtl, String cdRequestType,
			String cdReceivingState, String cdSendingState, Long idLastUpdatePerson,
			Long idSendingStageAgency,Long idRecevingStateAgency);

	/**
	 * 
	 * Method Name: insertICPCEventLink Method Description: This method inserts
	 * record into ICPC_EVENT_LINK table.
	 * @param idIcpcRequest
	 * @param idEvent
	 * @param idCase
	 * @param idLastUpdatePerson
	 * @param neiceCaseId
	 */
	public void insertICPCEventLink(Long idIcpcRequest, Long idEvent, Long idCase, Long idLastUpdatePerson, String neiceCaseId);

	/**
	 * 
	 * Method Name: insertPlacementRequest Method Description: This method
	 * inserts record into ICPC_PLACEMENT_REQUEST table.
	 * 
	 * @param icpcPlacementRequestDto
	 * @return Long
	 */
	public Long insertPlacementRequest(ICPCPlacementRequestDto icpcPlacementRequestDto);


	List<IcpcRequestPersonLink> getIcpcRequestPersonLinks(Long idIcpcPlacementRequest);

	/**
	 *
	 * Method Name: insertICPCRequestPersonLink Method Description: This method
	 * inserts record into ICPC_REQUEST_PERSON_LINK table.
	 *  @param idIcpcRequest
	 * @param idPerson
	 * @param cdRequestPersonType
	 * @param idLastUpdatePerson
	 * @param idPersonRelation
	 * @param neicePersonId
	 */
	public void insertICPCRequestPersonLink(Long idIcpcRequest, Long idPerson, String cdRequestPersonType,
											Long idLastUpdatePerson, Long idPersonRelation, String neicePersonId);

	/**
	 * 
	 * Method Name: insertICPCRequestPersonLink Method Description: This method
	 * inserts record into ICPC_REQUEST_PERSON_LINK table.
	 *  @param idIcpcRequest
	 * @param idPerson
     * @param cdRequestPersonType
     * @param idLastUpdatePerson
     * @param idPersonRelation
     * @param neicePersonId
	 * @param cdPlacementCode
     */
	public void insertICPCRequestPersonLink(Long idIcpcRequest, Long idPerson, String cdRequestPersonType,
											Long idLastUpdatePerson, Long idPersonRelation, String neicePersonId,
											String cdPlacementCode);

	/**
	 * 
	 * Method Name: insertICPCRequestEnclosure Method Description: This method
	 * inserts record into ICPC_REQUEST_ENCLOSURE table.
	 * 
	 * @param idEvent
	 * @param cdEnclosure
	 * @param idLastUpdatePerson
	 * @return Long
	 */
	public Long insertICPCRequestEnclosure(Long idEvent, String cdEnclosure, Long idLastUpdatePerson);

	/**
	 * 
	 * Method Name: insertICPCRequestResourceLink Method Description: This
	 * method inserts record into ICPC_REQUEST_RESOURCE_LINK table.
	 * 
	 * @param idIcpcRequest
	 * @param idResource
	 * @param idLastUpdatePerson
	 * @param idResourceNeice
	 */
	public void insertICPCRequestResourceLink(Long idIcpcRequest, Long idResource, Long idLastUpdatePerson, String idResourceNeice);

	/**
	 * 
	 * Method Name: insertEntity Method Description:This method inserts record
	 * into ENTITY table.
	 * 
	 * @param entity
	 * @param idLastUpdatePerson
	 * @return Long
	 */
	public Long insertEntity(String entity, Long idLastUpdatePerson);

	/**
	 * 
	 * Method Name: insertEntityAddress Method Description:This method inserts
	 * record into ENTITY_ADDRESS table.
	 * 
	 * @param idEntity
	 * @param icpcAgencyDto
	 * @param idLastUpdatePerson
	 * @return Long
	 */
	public void insertEntityAddress(Long idEntity, ICPCAgencyDto icpcAgencyDto, Long idLastUpdatePerson);

	/**
	 * 
	 * Method Name: insertEntityPhone Method Description:This method inserts
	 * record into ENTITY_PHONE table.
	 * 
	 * @param idEntity
	 * @param icpcAgencyDto
	 * @param idLastUpdatePerson
	 * @return Long
	 */
	public void insertEntityPhone(Long idEntity, ICPCAgencyDto icpcAgencyDto, Long idLastUpdatePerson);

	/**
	 * 
	 * Method Name: insertICPCAgencyType Method Description:This method inserts
	 * record into ICPC_AGENCY_TYPE table.
	 * 
	 * @param idEntity
	 * @param agencyType
	 * @param idLastUpdatePerson
	 */
	public void insertICPCAgencyType(Long idEntity, Long agencyType, Long idLastUpdatePerson);

	/**
	 * 
	 * Method Name: insertICPCPlacementRqstEnity Method Description: This method
	 * inserts record into ICPC_PLACEMENT_REQUEST table.
	 * 
	 * @param idEntity
	 * @param idIcpcPlacementRequest
	 * @param cdPlcmntRqstType
	 * @param IdCreatedPerson
	 * @return Long
	 */
	public Long insertICPCPlacementRqstEnity(Long idEntity, Long idIcpcPlacementRequest, String cdPlcmntRqstType,
			Long IdCreatedPerson);

	/**
	 *
	 * Method Name: insertICPCPlacementRqstEnityOther
	 * Method Description:This method inserts record into ICPC_PLCMT_REQ_ENTITY_OTHER table.
	 *
	 * @param idIcpcPlacementRqstEntity
	 * @param icpcAgencyDto
	 * @param idLastUpdatePerson
	 *
	 */
	void insertICPCPlacementRqstEnityOther(Long idIcpcPlacementRqstEntity, ICPCAgencyDto icpcAgencyDto,
												  Long idLastUpdatePerson);

	/**
	 *
	 * Method Name: updateICPCPlacementRqstEnityOther
	 * Method Description:This method inserts record into ICPC_PLCMT_REQ_ENTITY_OTHER table.
	 *
	 * @param idIcpcPlacementRqstEntity
	 * @param icpcAgencyDto
	 * @param idLastUpdatePerson
	 *
	 */
	void updateICPCPlacementRqstEnityOther(Long idIcpcPlacementRqstEntity, ICPCAgencyDto icpcAgencyDto,
										   Long idLastUpdatePerson);

	/**
	 * 
	 * Method Name: insertICPCRequestResourceLink Method Description: This
	 * method gets Agency id
	 * 
	 * @param icpcAgencyDto
	 * @param cdAgencyType
	 * @return Long
	 */
	public Long verifyAgencyExist(ICPCAgencyDto icpcAgencyDto, Long cdAgencyType);

	/**
	 * 
	 * Method Name: verifyAgencyExist Method Description: This method gets
	 * Agency id
	 * 
	 * @param toDoDesc
	 * @param idEvent
	 * @param idUser
	 */
	public void endDateToDo(Long idEvent, String toDoDesc, Long idUser);

	/**
	 * 
	 * Method Name: saveICPCSubmission Method Description:saves the ICPC Summary
	 * Details
	 * 
	 * @param icpcPlacementRequestDto
	 * @return void
	 */

	public void saveICPCSubmission(ICPCPlacementRequestDto icpcPlacementRequestDto);

	/**
	 * Method getValidPlacementRequests Method Description: This method
	 * generates the list for PEND and APRV Status which has a Placement 100-A
	 * Record
	 * 
	 * @param icpcPlacementReq
	 * @return List<EventValueDto>
	 */

	public List<EventValueDto> getValidPlacementRequests(ICPCPlacementReq icpcPlacementReq);

	/**
	 * Method getIcpcLegacyNumber Method Description: This method generates the
	 * Legacy number for the given record which doesn't have a list in Placement
	 * 100-A
	 * 
	 * @param idStage
	 * @return String
	 */

	public String getIcpcLegacyNumber(Long idStage);

	/**
	 * Method Name: getIcpcDocument Method Description: get the document details
	 * for the given idICPCDocuments from ICPC_DOCUMENT,ICPC_FILE_STORAGE
	 * 
	 * @param idICPCDocuments
	 * @return List<ICPCDocumentDto>
	 */
	public List<ICPCDocumentDto> getIcpcDocument(List<Long> idICPCDocuments);

	/**
	 * 
	 * Method Name: getCareType Method Description:
	 * 
	 * @param idICPCPlacementRequest
	 * @return
	 */
	public String getCareType(Long idICPCPlacementRequest);

	/**
	 * 
	 * Method Name: insertPlacementStatus Method Description:
	 * 
	 * @param icpcPlacementStatusDto
	 * @return
	 */
	public Long insertPlacementStatus(ICPCPlacementStatusDto icpcPlacementStatusDto);

	/**
	 * 
	 * Method Name: getEventFromRequest Method Description:
	 * 
	 * @param idICPCPlacementRequest
	 * @return
	 */
	public Long getEventFromRequest(Long idICPCPlacementRequest);

	/**
	 * 
	 * Method Name: updateICPCPlacementRequest Method Description: This method
	 * inserts record into ICPC_PLACEMENT_REQUEST table.
	 * 
	 * @param icpcPlacementRequestDto
	 */
	public void updateICPCPlacementRequest(ICPCPlacementRequestDto icpcPlacementRequestDto);

	/**
	 * 
	 * Method Name: deleteICPCRequestEnclosure Method Description: This method
	 * delete record from ICPC_REQUEST_ENCLOSURE table.
	 * 
	 * @param idEvent
	 */
	public void deleteICPCRequestEnclosure(Long idEvent);

	/**
	 * 
	 * Method Name: updateICPCRequestPersonLink Method Description: This method
	 * update record into ICPC_REQUEST_PERSON_LINK table.
	 * 
	 * @param idPerson
	 * @param icpcPersonDto
	 */
	public void updateICPCRequestPersonLink(ICPCPersonDto icpcPersonDto, Long idPerson);

	/**
	 *
	 * Method Name: updateICPCRequestPersonLink Method Description: This method
	 * update record into ICPC_REQUEST_PERSON_LINK table.
	 *
	 * @param idPerson
	 * @param icpcPersonDto
	 * @param cdPlacementCode
	 */
	void updateICPCRequestPersonLink(ICPCPersonDto icpcPersonDto, Long idPerson, String cdPlacementCode);

	/**
	 * 
	 * Method Name: deleteIcpcRequestPersonLink Method Description: This method
	 * delete a record from ICPC_REQUEST_PERSON_LINK table.
	 * 
	 * @param idIcpcRequest
	 * @param cdPersonType
	 */
	public void deleteICPCRequestPersonLink(Long idIcpcRequest, String cdPersonType);

	/**
	 *
	 * Method Name: deleteIcpcRequestPersonLink Method Description: This method
	 * delete a record from ICPC_REQUEST_PERSON_LINK table.
	 *
	 * @param idIcpcRequest
	 * @param cdPersonType
	 * @param idPerson
	 */
	void deleteICPCRequestPersonLinkByPerson(Long idIcpcRequest, String cdPersonType, Long idPerson);

	/**
	 * 
	 * Method Name: updateICPCRequestResourceLink Method Description: This
	 * method update a record from ICPC_REQUEST_RESOURCE_LINK table.
	 * 
	 * @param icpcResourceDto
	 * @param idLastUpdatePerson
	 *
	 */

	public void updateICPCRequestResourceLink(ICPCResourceDto icpcResourceDto, Long idLastUpdatePerson);

	/**
	 *
	 * Method Name: deleteICPCRequestResourceLink Method Description: This
	 * method delete a record from ICPC_REQUEST_RESOURCE_LINK table.
	 *
	 * @param idIcpcRequest
	 *
	 */
	boolean deleteICPCRequestResourceLinkById(Long idIcpcRequest);

	/**
	 * 
	 * Method Name: deleteICPCRequestResourceLink Method Description: This
	 * method delete a record from ICPC_REQUEST_RESOURCE_LINK table.
	 * 
	 * @param idIcpcRequestResourceLink
	 *
	 */
	public void deleteICPCRequestResourceLink(Long idIcpcRequestResourceLink);

	/**
	 * 
	 * Method Name: updateICPCRequest Method Description: This method update a
	 * record from ICPC_REQUEST table.
	 * 
	 * @param icpcPlacementRequestDto
	 *
	 */
	public void updateICPCRequest(ICPCPlacementRequestDto icpcPlacementRequestDto);

	/**
	 * Method Name: updateICPCPlacementRequestEntity Method Description: this
	 * method update a record from ICPC_PLACEMENT_REQUEST_ENTITY table (Agency
	 * Financially Responsible)
	 * 
	 * @param idIcpcPlcmntRqstEntity
	 * @param idEntity
	 * @param IdLastUpdatePerson
	 */
	public void updateICPCPlacementRequestEntity(Long idIcpcPlcmntRqstEntity, Long idEntity, Long IdLastUpdatePerson);

	/**
	 * Method Name: deleteICPCPlacementRequestEntity Method Description: this
	 * method delete a record from ICPC_PLACEMENT_REQUEST_ENTITY table (Agency
	 * Financially Responsible)
	 * 
	 * @param idIcpcPlacementRequest
	 * @param cdAgencyType
	 */
	public void deleteICPCPlacementRequestEntity(Long idIcpcPlacementRequest, String cdAgencyType);

	/**
	 * Method Name: deleteICPCPlacementRequestEntityOther
	 * Method Description: this method delete a record from ICPC_PLCMT_REQ_ENTITY_OTHER table (Agency
	 * Financially Responsible)
	 *
	 * @param idIcpcPlacementRequestEntity
	 */
	void deleteICPCPlacementRequestEntityOther(Long idIcpcPlacementRequestEntity);

	/**
	 * 
	 * Method Name: getIcpcDocument Method Description: this method is to update
	 * ICPC placement status report detail page
	 *
	 * @param icpcPlacementStatusDto
	 * @return ICPCDocumentRes
	 */
	public void updateIcpcPlacementStatus(ICPCPlacementStatusDto icpcPlacementStatusDto);

	/**
	 * 
	 * Method Name: insertIcpcPlcmntStatPerAddr Method Description:
	 * 
	 * @param idICPCPlacementStatus
	 * @param idPrsnAddress
	 * @param cdAddressType
	 * @param idLastUpdatePerson
	 */
	public void insertIcpcPlcmntStatPerAddr(Long idICPCPlacementStatus, Long idPrsnAddress, String cdAddressType,
			Long idLastUpdatePerson);

	/**
	 * 
	 * Method Name: insertIcpcPlcmntStatResAddr Method Description:
	 * 
	 * @param idICPCPlacementStatus
	 * @param idResAddress
	 * @param icpcadtp20
	 * @param idLastUpdatePerson
	 */
	public void insertIcpcPlcmntStatResAddr(Long idICPCPlacementStatus, Long idResAddress, String icpcadtp20,
			Long idLastUpdatePerson);

	/**
	 * Method Name: updateEvent MethoUpdate event d Description: this method
	 * updates the event table
	 * 
	 * @param idEvent
	 * @param eventValueBean
	 */
	public void updateEvent(Long idEvent, EventValueDto eventValueBean);

	/**
	 * Method Name:checkCorresponding100BPresent Method Description: this method
	 * check if 100B has been created
	 * 
	 * @param idICPCPlcmntRequest
	 * @return boolean
	 */
	public boolean checkCorresponding100BPresent(Long idICPCPlcmntRequest);

	/**
	 * 
	 * Method Name: updateIcpcEmailDtlLog Method Description: This method is to
	 * update ICPC placement Email detail Log tables(ICPC_EMAIL_LOG,
	 * ICPC_EMAIL_DOC_LOG) when the email has been sent successfully
	 *
	 * @param icpcEmailLogReq
	 * @return CommonStringRes
	 */
	public CommonStringRes updateIcpcEmailDtlLog(ICPCEmailLogReq icpcEmailLogReq);

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
	 * Method Name: deleteICPCRequest Method Description: This method delete
	 * record from ICPC_REQUEST,ICPC_EVENT_LINK,ICPC_PLACEMENT_REQUEST,
	 * ICPC_REQUEST_PERSON_LINK,ICPC_REQUEST_RESOURCE_LINK table.
	 * 
	 * @param icpcPlacementRequestDto
	 */
	public void deleteICPCRequest(ICPCPlacementRequestDto icpcPlacementRequestDto);
	
	/**
	 * 
	 *Method Name:	getIcpcPrimaryChild
	 *Method Description:This method is used to get the primary child for the icpc request
	 *@param idICPCRequest
	 *@return
	 */
	public Long getIcpcPrimaryChild(Long idICPCRequest);

	/**
	 * Method Name:	getTransmissionLst
	 * Method Description: This method is used to retrieve the matched pending Home Study Request else all
	 * the pending Home Study Request
	 *
	 * @param idStage
	 * @return List<ICPCTransmissionDto>
	 */
	List<ICPCTransmissionDto> getTransmissionLst(Long idStage);

	/**
	 *
	 * @param idIcpcTransmittal
	 * @return List<ICPCTransmissionDto>
	 */
	public List<ICPCTransmissionDto> getTransmissionChildLst(Long idIcpcTransmittal);

	/**
	 *
	 * @param idTransmission
	 * @param idNeiceTransmittalPerson
	 */
	public void saveTransmission(Long idTransmission, Long idNeiceTransmittalPerson);

	/**
	 *
	 * @param idIcpcTransmittal
	 * @param idPersonNeice
	 * @return
	 */
	List<ICPCTransmissionDto> getTransmissionAttachemnts(Long idIcpcTransmittal, Long idPersonNeice);

	/**
	 *
	 * @param idEvent
	 * @return
	 */
	List<Long> getSecondaryWorkersAssigned(Long idEvent);

	/**
	 * Method Name: getSendingAgencyInfo Method Description: Get the
	 * Transmittal Details for the passed Transmittal ID
	 *
	 * @param stateNm
	 * @return List<NeiceStateParticpantDTO>
	 */
	public List<NeiceStateParticpantDTO> getSendingAgencyInfo(String stateNm);

	/**
	 * Method Name: getIcpcEventLinkInfo Method Description: Get the
	 * Transmittal Details for the passed Transmittal ID
	 *
	 * @param eventId
	 * @return IcpcEventLink
	 */
	public IcpcEventLink getIcpcEventLinkInfo(Long eventId);

	/**
	 * Method Name: getIcpcRequestInfo Method Description: Get the
	 * Transmittal Details for the passed Transmittal ID
	 *
	 * @param idICPCRequest
	 * @return Map<Long, String>
	 */
	public Map<Long, String>  getIcpcRequestInfo(Long idICPCRequest);

	/**
	 * Method Name: getPlacementStatusByRequest
	 * Method Description: Get the Placement Status details by ICPC Request
	 *
	 * @param idICPCRequest
	 * @param getApproved
	 * @return ICPCPlacementStatusDto
	 */
	ICPCPlacementStatusDto getPlacementStatusByRequest(Long idICPCRequest, boolean getApproved);

	/**
	 * Method Name: saveTransmittalDocs
	 * Method Description: Save the Transmittal selected documents for Transmission
	 *
	 * @param icpcTransmittalDto
	 */
	void saveTransmittalDocs(ICPCTransmittalDto icpcTransmittalDto);

	/**
	 * Method Name: saveTransmittalStatus
	 * Method Description: Save the Transmittal Status received from MuleSoft
	 *
	 * @param icpcTransmittalDto
	 */
	void saveTransmittalStatus(ICPCTransmittalDto icpcTransmittalDto);

	/**
	 * Method Name: getTransmittalLinkedDocuments
	 * Method Description: Retrieve all the linked documents from Transmittal
	 *
	 * @param idIcpcTransmittal
	 */
	List<Long> getTransmittalLinkedDocuments(Long idIcpcTransmittal);

	/**
	 *
	 * @param idAttachment
	 * @return
	 */
    ICPCTransmissionDto getTransmissionAttachemnt(Long idAttachment);

	/**
	 * Method Name: getSiblingsRequests
	 * Method Description: Retrieve the ICPC Request IDs for all siblings
	 *
	 * @param idPersons
	 * @param idIcpcRequest
	 */
	List<ICPCPersonDto> getSiblingsRequests(List<Long> idPersons, Long idIcpcRequest);

	/**
	 * Method Name: getAllChildDocumentListInfo
	 * Method Description: Retrieves the documents from all the selected children
	 *
	 * @return List<ICPCDocumentDto>
	 */
	List<ICPCDocumentDto> getAllChildrenDocumentListInfo(List<Long> idICPCRequests);

	/**
	 * Method Name: saveTransmittalOtherMode
	 * Method Description: Save the Transmittal Other Mode
	 *
	 * @param icpcTransmittalDto
	 */
	void saveTransmittalOtherMode(ICPCTransmittalDto icpcTransmittalDto);

	/**
	 *
	 * @return
	 */
	List<NeiceStateParticpantDTO> getAllAgencyInfo();

	/**
	 * Method Name: getPlacementRequestDtlForAll
	 * Method Description: Retrieve Placement Request Details for all ID_ICPC_REQUEST's
	 *
	 * @param idIcpcRequests
	 * @return
	 */
	Map<Long, ICPCPlacementRequestDto> getPlacementRequestDtlForAll(List<Long> idIcpcRequests);

	/**
	 * Method Name: createAlert
	 * Method Description: This method is used to create the alert
	 *
	 * @param todoDto
	 */
	void createAlert(TodoDto todoDto);

	/**
	 * Method Name: updateAgencyEntityPhone
	 * Method Description: This method is used to update the Agency Entity Phone details
	 *
	 * @param idEntity
	 * @param nbrPhone
	 * @param idUser
	 */
	void updateAgencyEntityPhone(Long idEntity, Long nbrPhone, Long idUser);
}
