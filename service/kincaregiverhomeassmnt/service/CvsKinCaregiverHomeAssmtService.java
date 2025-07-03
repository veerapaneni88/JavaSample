package us.tx.state.dfps.service.kincaregiverhomeassmnt.service;

import org.springframework.stereotype.Service;
import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business - Kinship CareGiver Home Assessment (CVSKINHOMEASSESSMENT)
 * 02/20/2025 thompswa ppm84014 : Prefill Service for CVSKINHOMEASSESSMENT
 */
public interface CvsKinCaregiverHomeAssmtService {

    /**
     * Method Name: getCvsKinCaregiverHomeAssmt Method Description: Request to get
     * the Home Assessment Data in prefill data format
     *
     * @param kinCaregiverReq
     * @return
     */
    PreFillDataServiceDto getCvsKinCaregiverHomeAssmt(PopulateFormReq kinCaregiverReq);

}