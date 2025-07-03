package us.tx.state.dfps.service.admin.service;

import java.util.List;

import us.tx.state.dfps.common.domain.PersonId;
import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.service.admin.dto.EmpPersonIdDto;
import us.tx.state.dfps.service.common.request.RetrvPersonIdentifiersReq;
import us.tx.state.dfps.service.common.request.SavePersonIdentifiersReq;
import us.tx.state.dfps.service.common.response.RetrvPersonIdentifiersRes;
import us.tx.state.dfps.service.common.response.SavePersonIdentifiersRes;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.person.dto.PersonIdentifiersDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN04S Class
 * Description: Operations for PersonId Apr 14, 2017 - 11:17:22 AM
 */
public interface PersonIdService {
	/**
	 * 
	 * Method Description:getEmpPersonIdDto
	 * 
	 * @param personId
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN04S
	public EmpPersonIdDto getEmpPersonIdDto(PersonId personId) throws DataNotFoundException;

	/**
	 * 
	 * Method Description: This Method will retrieve information for the Person
	 * Identifiers List/Detail window. Service Name : CINT19S
	 * 
	 * @param retrvPersonIdentifiersReq
	 *            - request object for the service
	 * @return RetrvPersonIdentifiersRes - response object for the service
	 * 
	 */
	public RetrvPersonIdentifiersRes getPersonIdentifiersDetailList(
			RetrvPersonIdentifiersReq retrvPersonIdentifiersReq);

	/**
	 * 
	 * Method Description: This Method will Add/Update/Delete Person Identifiers
	 * for a person. Service Name : CINT23S
	 * 
	 * @param savePersonIdentifiersReq
	 *            - request object for the service
	 * @return SavePersonIdentifiersRes - response object for the service
	 * 
	 * @throws DataNotFoundException
	 */
	public SavePersonIdentifiersRes savePersonIdentifiersDetail(SavePersonIdentifiersReq savePersonIdentifiersReq);

	/**
	 * 
	 * Method Description: get the placement event ID if the person has current
	 * AA medicaid; otherwise get a zero. Used as a yes/no, and if yes the event
	 * ID is used. EJB Service called during adding a Person Identifier
	 * 
	 * @param ulIdPerson
	 *            - Peson ID for whom the placement ID has to be fetched
	 * @return Long - Fetched Placement ID
	 * 
	 */
	public Long getPlacementIdIfPersonHasAaMedicaid(Long ulIdPerson);

	/**
	 * 
	 * Method Description: Updates the person identifier end date of the person
	 * 
	 * @param SavePersonIdentifiersReq
	 * @return void
	 * 
	 */
	public ServiceResHeaderDto updateIdType(SavePersonIdentifiersReq savePersonIdentifiersReq);

	/**
	 * Method Name: isAssmntPerson Method Description:to check if a person is
	 * associated with PROC or COMP
	 * 
	 * @param personId
	 * @param stageId
	 * @return Boolean @
	 */
	public Boolean isAssmntPerson(Long personId, Long stageId);

	/**
	 * Method Name: isPlcmntPerson Method Description:to check if a person is
	 * associated with an open or closed PCSP placement
	 * 
	 * @param caseId
	 * @param personId
	 * @param cdStage
	 * @return Boolean @
	 */
	public Boolean isPlcmntPerson(Long idCase, Long personId, String stageId);

	public RetrvPersonIdentifiersRes getPersonIdentifierByIdType(RetrvPersonIdentifiersReq retrvPersonIdentifiersReq);

	/**
	 * Method Name: fetchIdentifiersList Method Description:Fetches list of
	 * person identifiers for a Person from Snapshot table (SS_PERSON_ID)
	 * 
	 * @param idPerson
	 * @param getbActiveFlag
	 * @param idReferenceData
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @return List<PersonIdentifierValueDto>
	 */
	public List<PersonIdentifiersDto> fetchIdentifiersList(Long idPerson, Boolean getbActiveFlag, Long idReferenceData,
			String cdActionType, String cdSnapshotType);

	/**
	 * 
	 * Method Name: fetchIdentifiersList Method Description:Fetches list of
	 * person identifiers for a Person
	 * 
	 * @param idPerson
	 * @param getbActiveFlag
	 * @return
	 */
	public List<PersonIdentifiersDto> fetchIdentifiersList(Long idPerson, Boolean getbActiveFlag);

}
