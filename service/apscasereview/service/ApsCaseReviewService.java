package us.tx.state.dfps.service.apscasereview.service;

import us.tx.state.dfps.service.common.request.ApsCaseReviewRequest;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

import org.springframework.stereotype.Service;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps case review
 * Jan 21, 2022- 1:52:46 PM © 2022 Texas Department of Family and
 * Protective Services
 */
@Service
public interface ApsCaseReviewService {

    /**
     * Method Name: getApsCaseReviewShieldVersionInformation Method Description: Aps case review shield version -- apscr
     *
     * @param apsCaseReviewRequest
     * @return PreFillDataServiceDto
     */
    PreFillDataServiceDto getApsCaseReviewShieldVersionInformation(ApsCaseReviewRequest apsCaseReviewRequest);
}
