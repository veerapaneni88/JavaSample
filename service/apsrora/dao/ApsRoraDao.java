package us.tx.state.dfps.service.apsrora.dao;

import us.tx.state.dfps.service.apsrora.dto.APSRoraAnswerDto;
import us.tx.state.dfps.service.apsrora.dto.APSRoraFollowupAnswerDto;
import us.tx.state.dfps.service.apsrora.dto.ApsRoraDto;
import us.tx.state.dfps.service.apsrora.dto.ApsRoraResponseDto;
import us.tx.state.dfps.service.apssna.dto.ApsStrengthsAndNeedsAssessmentDto;
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * service-business- IMPACT APS MODERNIZATION Class
 * Description:ApsRoraDao Jan 04, 2022- 1:52:46 PM © 2022 Texas Department
 * of Family and Protective Services
 */
@Service
public interface ApsRoraDao {

    /*
     code added apsrora form
    */
    public ApsRoraDto getRoraReportData(Long idEvent);

    /*
     code added for apsrora form
    */
    public List<ApsRoraResponseDto> getRoraResponseReportData(Long apsRoraId);

    /*
     code added for apsrora form
    */
    public List<APSRoraAnswerDto> getRoraAnswerReportData(Long apsRoraResponseId);

    /*
     code added for apsrora form
    */
    public List<APSRoraFollowupAnswerDto> getRoraFollowupAnswerReportData(Long apsRoraResponseId, Long apsRoraAnswerLookUpId);

    /**
     * Gets the Rora info based on case.
     * @param idCase
     * @return List<ApsRoraDto>
     */
    public List<ApsRoraDto> getRoraInformation(Long idCase);




}
