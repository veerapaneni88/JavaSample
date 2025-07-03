package us.tx.state.dfps.service.interstatecompact.dao;

import java.util.List;

import us.tx.state.dfps.service.icpforms.dto.IcpcChildDetailsDto;
import us.tx.state.dfps.service.icpforms.dto.IcpcInfoDto;
import us.tx.state.dfps.service.icpforms.dto.IcpcTransmittalDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<ICP forms
 * Dao interface for populating interstate compact forms > May 1, 2018- 10:19:01
 * AM © 2017 Texas Department of Family and Protective Services
 */

public interface ICPformsDao {

	/**
	 * Dam Name: csec0cd Method Name: getIcpcChildDetails Method Description:
	 * Retrieves name, ssn, id_person from event_person_link, person, person_id.
	 * One row.
	 * 
	 * @param idEvent
	 * @return IcpcChildDetailsDto
	 */
	IcpcChildDetailsDto getIcpcChildDetails(Long idEvent);

	/**
	 * Dam Name: CSEC1AD Method Name: getIcpcReqAndTitle Method Description:
	 * This DAO retrieves Title IVE and ID ICPC Request.
	 * 
	 * @param idEvent
	 * @return IcpcChildDetailsDto
	 */
	IcpcChildDetailsDto getIcpcReqAndTitle(Long idEvent);

	/**
	 * Dam Name: CSEC1CD Method Name: getAddressandName Method Description: This
	 * DAO retrieves addresses and name
	 * 
	 * @param idIcpcRequest
	 * @return IcpcChildDetailsDto
	 */
	IcpcChildDetailsDto getAddressandName(Long idIcpcRequest);

	/**
	 * Dam Name: CLSC25D Method Name: getPersonDetailsByIdReqest Method
	 * Description: This DAO retrieves the Names, address, phones for a
	 * IdIcpcRequest.
	 * 
	 * @param idIcpcRequest
	 * @return List<IcpcChildDetailsDto>
	 */
	List<IcpcChildDetailsDto> getPersonDetailsByIdReqest(Long idIcpcRequest);

	/**
	 * Dam Name: CSES18D Method Name: getIdEventStage Method Description: This
	 * DAO retrieves stage id that is linked to an event.
	 * 
	 * @param idEvent
	 * @return IcpcChildDetailsDto
	 */
	IcpcChildDetailsDto getIdEventStage(Long idEvent);

	/**
	 * Dam Name: CSECF2D Method Name: getICPCinfo Method Description: This DAO
	 * retrieves codes, placement date, reason text from ICPC related tables.
	 * 
	 * @param idEvent
	 * @return List<IcpcInfoDto>
	 */
	List<IcpcInfoDto> getICPCinfo(Long idEvent);

	/**
	 * Dam Name: CSECF3D Method Name: getNmResource Method Description: This DAO
	 * retrieves nmResource.
	 * 
	 * @param idEvent
	 * @return List<IcpcInfoDto>
	 */
	List<IcpcInfoDto> getNmResource(Long idEvent);

	/**
	 * Dam Name: CSECF4D Method Name: getPersonInfo Method Description: This DAO
	 * retrieves person information.
	 * 
	 * @param idEvent
	 * @return List<IcpcChildDetailsDto>
	 */
	List<IcpcChildDetailsDto> getPersonInfo(Long idEvent);

	/**
	 * Dam Name: CSECF7D Method Name: getDetailedPersonInfo Method Description:
	 * This DAO retrieves detailed person information.
	 * 
	 * @param idPerson
	 * @return IcpcChildDetailsDto
	 */
	IcpcChildDetailsDto getDetailedPersonInfo(Long idPerson);

	/**
	 * Dam Name: CSECF8D Method Name: getDetailedPersonInfoWithNmResource Method
	 * Description: This DAO retrieves detailed person information and
	 * NM_RESOURCE from CAPS_RESOURCE Table.
	 * 
	 * @param idResource
	 * @return IcpcChildDetailsDto
	 */
	IcpcChildDetailsDto getDetailedPersonInfoWithNmResource(Long idResource);

	/**
	 * Dam Name: CLSCH2D Method Name: getPersonNmType Method Description: This
	 * DAO retrieves detailed person information such as person full name, sex,
	 * type and birth.
	 * 
	 * @param idEvent
	 * @return List<IcpcChildDetailsDto>
	 */
	List<IcpcChildDetailsDto> getPersonNmType(Long idEvent);

	/**
	 * Dam Name: CLSCH3D Method Name: getPersonAddrInfo Method Description: This
	 * DAO retrieves detailed person information such as person full name,
	 * address, id status
	 * 
	 * @param idPerson
	 * @return List<IcpcChildDetailsDto>
	 */
	List<IcpcChildDetailsDto> getPersonAddrInfo(Long idPerson);

	/**
	 * Dam Name: CLSCH4D Method Name: getPersonResource Method Description: This
	 * DAO retrieves detailed person information such as nm resource, sex, type
	 * and birth.
	 * 
	 * @param idResource
	 * @return List<IcpcChildDetailsDto>
	 */
	List<IcpcChildDetailsDto> getPersonResource(Long idResource);

	/**
	 * Dam Name: CSEC1BD Method Name: getTransmittalInfo Method Description:
	 * This Dao retrieves transmittal information.
	 * 
	 * @param idTransmittal
	 * @return IcpcChildDetailsDto
	 */
	IcpcTransmittalDto getTransmittalInfo(Long idTransmittal);

	/**
	 * Dam Name: CSEC1DD Method Name: getTitleIVEind Method Description: This
	 * Dao retrieves Care Type Code from ICPC-100A Placement Request given
	 * ICPC-100B Placement Status information
	 * 
	 * @param idEvent
	 * @return List<IcpcChildDetailsDto>
	 */
	List<IcpcChildDetailsDto> getTitleIVEind(Long idEvent);

	/**
	 * Dam Name: CLSS2AD Method Name: getTransmittalInfo Method Description:
	 * This Dao retrieves transmittal indicator information.
	 * 
	 * @param idTransmittal
	 * @return List<IcpcTransmittalDto>
	 */
	List<IcpcTransmittalDto> getTransmittalIndicator(Long idTransmittal);

	/**
	 * Dam Name: CLSCH1D Method Name: getCountyAddress Method Description: This
	 * DAO retrieve county address from 100A
	 * 
	 * @param idEvent
	 * @return IcpcChildDetailsDto
	 */
	List<IcpcChildDetailsDto> getCountyAddress(Long idEvent);

	/**
	 * Dam Name: CSEC1ED Method Name: getCountyAddressForAgency Method
	 * Description: This DAO Retrieving address information and phone number of
	 * an agency depending on code type of that agency.
	 * 
	 * @param cdAgencyType,
	 *            inputStateDecode
	 * @return List<IcpcChildDetailsDto>
	 */
	List<IcpcChildDetailsDto> getCountyAddressForAgency(String cdAgencyType, String inputStateDecode);

	/**
	 * Dam Name: CLSS2BD Method Name: getChildrenFullName Method Description:
	 * This DAO retrieve Children full names giving the id_icpc_transmittal
	 * 
	 * @param idEvent
	 * @return List<IcpcChildDetailsDto>
	 */
	List<IcpcChildDetailsDto> getChildrenFullName(Long idTransmittal);

	/**
	 * Dam Name: CSEC1FD Method Name: getNumberOtherCase Method Description:
	 * This Dao retrieves Other Case Number from ICPC Submission table.
	 * 
	 * @param idTransmittal
	 * @return List<IcpcTransmittalDto>
	 */
	List<IcpcTransmittalDto> getNumberOtherCase(Long idTransmittal);

	/**
	 * Dam Name: CSECF9D Method Name: getPersonTypeAndRelation Method
	 * Description: This DAO retrieves idPerson cdType, code relation.
	 * 
	 * @param cdPersonType,
	 *            idPerson, idEvent
	 * @return IcpcChildDetailsDto
	 */
	IcpcChildDetailsDto getPersonTypeAndRelation(String cdPersonType, Long idPerson, Long idEvent);
}
