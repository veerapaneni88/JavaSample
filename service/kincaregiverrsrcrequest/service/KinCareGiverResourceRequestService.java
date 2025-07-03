package us.tx.state.dfps.service.kincaregiverrsrcrequest.service;

import org.springframework.stereotype.Service;
import us.tx.state.dfps.service.common.request.KinCareGiverResourceReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * Kinship Caregiver Resource/Contract Request Template (KIN10O00)
 * 07/21/2021 kurmav Artifact artf192721 : Prefill Service for KIN10O00
 */
@Service
public interface KinCareGiverResourceRequestService {

    /**
     * Method Name: getKinCareGiverResource Method Description: Request to get
     * Kinship Caregiver Resource/Contract Request Template in prefill data format
     *
     * @param careGiverResourceReq
     * @return
     */
    PreFillDataServiceDto getKinCareGiverResource(KinCareGiverResourceReq careGiverResourceReq);
}
