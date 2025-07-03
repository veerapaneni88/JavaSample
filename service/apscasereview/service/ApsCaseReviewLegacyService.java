package us.tx.state.dfps.service.apscasereview.service;

import us.tx.state.dfps.service.common.request.ApsCaseReviewRequest;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

import org.springframework.stereotype.Service;
/**
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * APS Case Review (Legacy)
 * @author CHITLA
 * Feb, 2022© Texas Department of Family and Protective Services
 */
@Service
public interface ApsCaseReviewLegacyService {
    /**
     * Method Name: getApsCaseReviewLegacyVersionInformation
     * Method Description: APS Case Review legacy version - CFIV2800
     * @param request
     * @return PreFillDataServiceDto
     */

    PreFillDataServiceDto getApsCaseReviewLegacyVersionInformation(ApsCaseReviewRequest request);
}
