package us.tx.state.dfps.service.apscontactlognarrative.dao;

import us.tx.state.dfps.service.apscontactlognarrative.dto.*;
import us.tx.state.dfps.service.webservices.gold.dto.GoldCommunicationDto;
import us.tx.state.dfps.service.webservices.gold.dto.GoldNarrativeDto;

import java.sql.Blob;
import java.util.List;

/**
 * class for Aps Contact Log Narrative Dao
 */
public interface ApsContactLogNarrativeDao {

    /**
     * mehtod to getsafetyAssmtEventsByStage
     * @param idStage
     * @return
     */
    public List<Long> getsafetyAssmtEventsByStage(Long idStage);

    /**
     * mehtod to getContactEvents
     * @param idStage
     * @return
     */
    public List<Long> getContactEvents(Long idStage);

    /**
     * mehtod to getFirstContactEventBySA
     * @param apsSaEventId
     * @return
     */
    public Long getFirstContactEventBySA(Long apsSaEventId);

    /**
     * Method Name: getDtContactoccured Method Description: Method to getDtContactoccured
     * @param idEvent
     * @return
     */
    public APSSafetyAssessmentContactDto getDtContactoccured(Long idEvent);

    /**
     * Method Name: getApsSafetyAssessmentResults
     * Method Description: Method to get Aps SafetyAssessment Results
     * @param idEvent
     * @return
     */
    public APSSafetyAssessmentDto getApsSafetyAssessmentResults(Long idEvent);

    /**
     * Method Name: getApsSaSafetyContactData Method
     * Description: Method to get Aps SaSafety ContactData
     * @param idEvent
     * @return
     */
    public List<APSSafetyAssessmentContactDto> getApsSaSafetyContactData(Long idEvent);

    /**
     * Method Name: getApsAssessmentType Method
     * Description: Method to get Aps Assessment Type
     * @param idEvent
     * @return
     */
    public String getApsAssessmentType(Long idEvent);

    /**
     * Method Name: getApsSafetyResponseList
     * Description: Method to get Aps getSafetyResponse List
     * @param idEvent
     * @return
     */
    public Long getApsSaEventId(Long idEvent);

    /**
     * method to get getApsSaSafetyContactDataByEventId
     * @param idEvent
     * @return
     */
    public APSSafetyAssessmentContactDto getApsSaSafetyContactDataByEventId(Long idEvent);

    /**
     * method to get Person Contacted by Event Id
     * @param idEvent
     * @return
     */
    public List<String> getPersonsContacted(Long idEvent);

    GoldNarrativeDto getGuardianShipReferralNarrativeByEventId(Long idEvent);
}
