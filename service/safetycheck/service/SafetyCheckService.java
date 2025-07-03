package us.tx.state.dfps.service.safetycheck.service;

import us.tx.state.dfps.service.common.response.SafetyCheckRes;
import us.tx.state.dfps.service.hmm.dto.SafetyCheckDto;
/**
 * Class Description:SafetyCheckService is the Service class for Safety Check Detail Page
 */
public interface SafetyCheckService {

    /**
     * PPM 60692-artf178537-Start-Changes for Safety check List
     * Method Description: This method retrieves all the safety checks done for a resource
     *
     * @param idResource
     * @return SafetyCheckRes
     */
    SafetyCheckDto getSafetyCheckDetail(Long idResource);

    /**
     * PPM 60692-artf179776- Safety Check Details for Placement
     * Method Description: This method retrieves all the safety checks done for a placement
     * @param idStage
     * @return SafetyCheckRes
     */
    SafetyCheckRes getSafetyCheckListForPlcmnt(Long idStage);
    /**
     * PPM 60692-artf179566- loadSafetyDetail
     * Method Description: This method is used to fetch safety check Details
     * @param idResource,idHMSafetyCheck
     * @return SafetyCheckDto
     */
    SafetyCheckDto loadSafetyDetail(Long idResource,Long idHmSafetyCheck);

    /*Artifact artf179776 : Safety Check Details for Placement
     * This method fetches Safety check details for the record selected from the list.
     * @param idHMSafetyCheck
     * @param idEvent
     * @param idResource
     * @return SafetyCheckChildInfoDto
     * */
    SafetyCheckDto getSafetyCheckDetailForPlcmnt(Long idHmSafetyCheck, Long idEvent, Long idResource);
    /**
     * PPM 60692-artf179566- saveSafetyCheck
     * Method Description: This method is used to save safety check Details
     * @param safetyCheckDto,idUser
     * @return safetyCheckDto
     */
    SafetyCheckDto saveSafetyCheck(SafetyCheckDto safetyCheckDto,Long idUser);
    /**
     * PPM 60692-artf179566- deleteSafetyCheck
     * Method Description: This method is used to delete safety check Details
     * @param safetyCheckDto
     * @return safetyCheckDto
     */
    void deleteSafetyCheck(SafetyCheckDto safetyCheckDto);

    /**
     * PPM 60692-artf179566- deleteAttachment
     * Method Description: This method is used to delete Attachment
     * @param idHMSafetyCheckAttachment
     * @return void
     */
    void deleteAttachment(Long idHMSafetyCheckAttachment);

    Boolean isDocExist(Long idEvent);
}
