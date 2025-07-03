package us.tx.state.dfps.service.apssna.dao;

import org.springframework.stereotype.Service;
import us.tx.state.dfps.common.domain.ApsSna;
import us.tx.state.dfps.service.apssna.dto.ApsSnaAnswerDto;
import us.tx.state.dfps.service.apssna.dto.ApsSnaFormResponseDTO;
import us.tx.state.dfps.service.apssna.dto.ApsSnaResponseDto;
import us.tx.state.dfps.service.apssna.dto.ApsStrengthsAndNeedsAssessmentDto;

import java.util.List;

/**
 * service-business- IMPACT APS MODERNIZATION Class
 * Description:ApsSnaDao Jan 04, 2022- 1:52:46 PM © 2022 Texas Department
 * of Family and Protective Services
 */

@Service
public interface ApsSnaDao {

    /**
     * Gets the Sna events based on case.
     * @param idCase
     * @return List<ApsStrengthsAndNeedsAssessmentDto>
     */
    public List<ApsStrengthsAndNeedsAssessmentDto> getSnaEvents(Long idCase);


    /**
     * Gets the Sna responses based on eventId.
     * @param idEvent
     * @param code
     * @return List<ApsSnaResponseDto>
     */
    public List<ApsSnaResponseDto> getSnaResponses(Long idEvent, String code);

    /**
     * @param idEvent
     * @return
     */
    public ApsSna getApsSna(Long idEvent);

    /**
     * @param idApsSnaDomainLookup
     * @return
     */
    public List<ApsSnaAnswerDto> getApsSnaAnswers(Long idApsSnaDomainLookup);

    /**
     * @param idEvent
     * @return
     */
    public List<ApsSnaFormResponseDTO> getApsSnaResponseByIdEvent(Long idEvent) ;
}
