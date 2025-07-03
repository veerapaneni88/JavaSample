package us.tx.state.dfps.service.workload.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.Contact;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptCPSValueDto;
import us.tx.state.dfps.service.contact.dto.ChildFatalityContactDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.workload.dto.ContactDto;

/**
 *
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION ContactDao (Tuxedo Service Name:
 * CCMN04U, DAM Name: CSYS07D) Class Description:interface class for
 * ContactDaoImpl Apr 12, 2017 - 7:39:38 PM
 */

public interface ContactDao {

	/**
	 * This DAM is an AUD for the CONTACT table. Inputs: pSQLCA -- SQL
	 * communication area for Oracle return data. pInputDataRec -- Record
	 * containing a stage id. pOutputDataRec -- Record containing a stage table
	 * row
	 *
	 * Outputs: Service return code.
	 *
	 * NOTE: This DAM contains non-GENDAM generated code which would need to be
	 * copied if this DAM is re-GENDAM'd.
	 *
	 *
	 * Tuxedo Service Name: CCMN04U, DAM Name: CSYS07D
	 *
	 * @param contact
	 * @
	 */
	public void saveContact(Contact contact);

	/**
	 * This DAM is an AUD for the CONTACT table. Inputs: pSQLCA -- SQL
	 * communication area for Oracle return data. pInputDataRec -- Record
	 * containing a stage id. pOutputDataRec -- Record containing a stage table
	 * row
	 *
	 * Outputs: Service return code.
	 *
	 * NOTE: This DAM contains non-GENDAM generated code which would need to be
	 * copied if this DAM is re-GENDAM'd.
	 *
	 *
	 * Tuxedo Service Name: CCMN04U, DAM Name: CSYS07D
	 *
	 * @param contact
	 * @
	 */
	public void updateContact(Contact contact);

	/**
	 * This DAM is an AUD for the CONTACT table. Inputs: pSQLCA -- SQL
	 * communication area for Oracle return data. pInputDataRec -- Record
	 * containing a stage id. pOutputDataRec -- Record containing a stage table
	 * row
	 *
	 * Outputs: Service return code.
	 *
	 * NOTE: This DAM contains non-GENDAM generated code which would need to be
	 * copied if this DAM is re-GENDAM'd.
	 *
	 *
	 * Tuxedo Service Name: CCMN04U, DAM Name: CSYS07D
	 *
	 * @param contact
	 * @
	 */
	public void deleteContact(Contact contact);

	/**
	 * This DAM is an AUD for the CONTACT table. Inputs: pSQLCA -- SQL
	 * communication area for Oracle return data. pInputDataRec -- Record
	 * containing a stage id. pOutputDataRec -- Record containing a stage table
	 * row
	 *
	 * Outputs: Service return code.
	 *
	 * NOTE: This DAM contains non-GENDAM generated code which would need to be
	 * copied if this DAM is re-GENDAM'd.
	 *
	 *
	 * Tuxedo Service Name: CCMN04U, DAM Name: CSYS07D
	 *
	 * @param idContact
	 * @return @
	 */
	public Contact getContactEntityById(Long idContact);

	/**
	 * This DAM is an AUD for the CONTACT table. Inputs: pSQLCA -- SQL
	 * communication area for Oracle return data. pInputDataRec -- Record
	 * containing a stage id. pOutputDataRec -- Record containing a stage table
	 * row
	 *
	 * Outputs: Service return code.
	 *
	 * NOTE: This DAM contains non-GENDAM generated code which would need to be
	 * copied if this DAM is re-GENDAM'd.
	 *
	 *
	 * Tuxedo Service Name: CCMN04U, DAM Name: CSYS07D
	 *
	 * @param idContact
	 * @return @
	 */
	public ContactDto getContactById(Long idContact);

	public ContactDto getContactByEventId(Long idEvent);

	/**
	 * Method Name: insertContact Method Description:This method inserts record
	 * into CONTACT table.
	 *
	 * @param contact
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long insertContact(ContactDto contactDto);

	/**
	 * Method Name: updateContact Method Description: This method updates
	 * CONTACT table.
	 *
	 * @param contactDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long updateContact(ContactDto contactDto);

	/**
	 *
	 * Method Name: queryStage Method Description:This method queries the
	 * database to find the Stage type
	 *
	 * @param ulIdStage
	 * @return
	 * @throws DataNotFoundException
	 */
	public String queryStage(Long ulIdStage);

	/**
	 * Method Name: getCaseInitiationContact Method Description:Check for case
	 * initiation contacts in a stage , given a stage id
	 *
	 * @param stageId
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long getCaseInitiationContact(Long stageId);

	/**
	 *
	 * Method Name: fetchFacilityType Method Description: Method to fetch
	 * facility type by idStage
	 *
	 * @param idStage
	 * @return String @
	 */
	public String fetchFacilityType(Long idStage);

	/**
	 *
	 * Method Name: fetchCurrAbuseNglctInfo Method Description: Method to fetch
	 * abuse and neglect info
	 *
	 * @param idStage,idCase
	 * @return String
	 */
	public CFTRlsInfoRptCPSValueDto fetchCurrAbuseNglctInfo(Long idStage, Long idCase);

	/**
	 *
	 * Method Name: fetchPriorHistoryInfo Method Description: Method to fetch
	 * prior historyInfo
	 *
	 * @param idPerson
	 * @return idStage
	 */
	public List<CFTRlsInfoRptCPSValueDto> fetchPriorHistoryInfo(Long idPerson, Long idStage);

	/**
	 *
	 * Method Name: fetchChildFatalityInfo Method Description: Method to fetch
	 * child fatality info
	 *
	 * @param idStage,idPerson,idUser
	 * @return String
	 */
	public ChildFatalityContactDto fetchChildFatalityInfo(Long idStage, Long idPerson, Long userId);

	public ContactDto getByIdEvent(Long idEvent);
}
