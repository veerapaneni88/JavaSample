package us.tx.state.dfps.service.apssafetyassmt.dao;


import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentCaretakerDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentContactDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentNarrativeDto;
import us.tx.state.dfps.service.apssafetyassmt.dto.ApsSafetyAssessmentResponseDto;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * service-business- IMPACT APS MODERNIZATION Class
 * Description:APSafetyAssessmentDao Jan 04, 2022- 1:52:46 PM © 2022 Texas Department
 * of Family and Protective Services
 */
@Service
public interface ApsSafetyAssessmentDao {

    /**
     * Method Name: getApsSafetyAssessmentData Method Description: Aps Safety Assessment -- apssa.
     *
     * @param idEvent
     * @return ApsSafetyAssessmentDto
     */
    public ApsSafetyAssessmentDto getApsSafetyAssessmentData(Long idEvent);

    /**
     * Method Name: getContacts Method Description: getContacts -- apssa.
     *
     * @param idEvent
     * @return List<ApsSafetyAssessmentContactDto>
     */
    public List<ApsSafetyAssessmentContactDto> getContacts(Long idEvent);

    /**
     * Method Name: getSelectedCaretakersList Method Description: getSelectedCaretakersList -- apssa.
     *
     * @param idEvent
     * @return List<ApsSafetyAssessmentCaretakerDto>
     */
    public List<ApsSafetyAssessmentCaretakerDto> getSelectedCaretakersList(Long idEvent);

    /**
     * Method Name: getAvailableCaretakersList Method Description: getAvailableCaretakersList -- apssa.
     *
     * @param idStage
     * @return List<ApsSafetyAssessmentCaretakerDto>
     */
    public List<ApsSafetyAssessmentCaretakerDto> getAvailableCaretakersList(Long idStage);

    /**
     * Method Name: getNarrative Method Description: getNarrative -- apssa.
     *
     * @param idEvent
     * @return ApsSafetyAssessmentNarrativeDto
     */
    public ApsSafetyAssessmentNarrativeDto getNarrative(Long idEvent);

    /**
     * Method Name: getResponses Method Description: getResponses -- apssa.
     *
     * @param id
     * @return List<ApsSafetyAssessmentResponseDto>
     */
    public List<ApsSafetyAssessmentResponseDto>  getResponses(Long id);


    /**
     * Method Name: get Safety Assessment Contacts Method Description:
     * @param idCase
     * @return List<ApsSafetyAssessmentContactDto>
     */
    public List<ApsSafetyAssessmentContactDto> getSaContacts(Long idCase);


    /**
     * Method Name: get Safety Assessment Events Method Description:
     * @param idCase
     * @return List<ApsSafetyAssessmentDto>
     */
    public List<ApsSafetyAssessmentDto> getSaEvents(Long idCase);


}
