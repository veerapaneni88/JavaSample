package us.tx.state.dfps.service.safetycheck.dao;

import us.tx.state.dfps.common.domain.HMSafetyCheck;
import us.tx.state.dfps.service.hmm.dto.SafetyCheckChildInfoDto;
import us.tx.state.dfps.service.hmm.dto.SafetyCheckListDto;

import java.util.List;
/**
 * Class Description:SafetyCheckDao is the DAO for Safety Check Detail Page
 */
public interface SafetyCheckDao {

    /**
     * PPM 60692-artf178537-Start-Changes for Safety check List
     * Method Description: This method retrieves all the safety checks done for a resource
     *
     * @param idResource
     * @return List
     */
    List<SafetyCheckListDto> getSafetyCheckDetails(Long idResource);

    /**
     * PPM 60692-artf179776- Safety Check Details for Placement
     * This method gets a list of safety check records for placements
     * @param idStage
     * @return List
     */
    List<SafetyCheckListDto> getSafetyCheckListForPlcmnt(Long idStage);
    /**
     * PPM 60692-artf179566- getChildInfoForRsrc
     * Method Description: This method is used to fetch kids placed in an operation
     *
     * @param idResource
     * @return SafetyCheckDto
     */
    List<SafetyCheckChildInfoDto> getChildInfoForRsrc(Long idResource);

    /**
     * Artifact artf179776 : Safety Check Details for Placement
     * This method fetches Safety check details for the record selected from the list.
     * @param idHmSafetyCheck
     * @param idEvent
     * @return SafetyCheckChildInfoDto
     */
    SafetyCheckChildInfoDto getSafetyCheckDetailForPlcmnt(Long idHmSafetyCheck,Long idEvent);

    /**
     * Artifact artf179776 : Safety Check Details for Placement
     * This method fetches Safety check details for the record selected from the list by using event id
     * @param idEvent
     * @return SafetyCheckChildInfoDto
     */
    SafetyCheckChildInfoDto getSafetyCheckDetailForPlcmnt(Long idEvent);

    /**
     * PPM 60692-artf179566- loadHMSafetyCheck
     * Method Description: This method is used to fetch saved HMSafetyCheck entity
     *
     * @param idHMSafetyCheck
     * @return SafetyCheckDto
     */
    HMSafetyCheck loadHMSafetyCheck(Long idHMSafetyCheck);
    /**
     * PPM 60692-artf179566- saveOrUpdate
     * Method Description: This method is used to save or update safety check
     *
     * @param hmSafetyCheck
     */
    void saveOrUpdate(HMSafetyCheck hmSafetyCheck);
    /**
     * PPM 60692-artf179566- deleteSafetyCheck
     * Method Description: This method is used to delete safety check Details
     * @param hmSafetyCheck
     * @return void
     */
    void deleteSafetyCheck(HMSafetyCheck hmSafetyCheck);
    /**
     * PPM 60692-artf179566- deleteHmSafetyCheckChildInfo
     * Method Description: This method is used to delete the child records in the safety check table that has been saved once but unselected from front end afterwards
     * @param idHMSafetyCheckChildInfoList
     * @return void
     */
    void deleteHmSafetyCheckChildInfo(List<Long> idHMSafetyCheckChildInfoList);
    /**
     * PPM 60692-artf179566- hmSafetyCheckNarrExists
     * Method Description: This method is used to check is the narrative already exists
     *
     * @param idEvent
     * @return boolean
     */
    boolean hmSafetyCheckNarrExists(Long idEvent);
    /**
     * PPM 60692-artf179566- deleteAttachment
     * Method Description: This method is used to delete Attachment
     * @param idHMSafetyCheckAttachment
     * @return void
     */
    void deleteAttachment(Long idHMSafetyCheckAttachment);
}
