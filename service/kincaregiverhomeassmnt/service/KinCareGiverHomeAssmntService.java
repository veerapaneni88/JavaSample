package us.tx.state.dfps.service.kincaregiverhomeassmnt.service;

import org.springframework.stereotype.Service;
import us.tx.state.dfps.service.common.request.KinCareGiverHomeDetailsReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.kin.dto.KinHomeInfoDto;

/**
 * service-business - Kinship CareGiver Home Assessment Template (KIN12O00)
 * 07/19/2021 kurmav Artifact artf192718 : Prefill Service for KIN12O00
 */
@Service
public interface KinCareGiverHomeAssmntService {

    /**
     * Method Name: getKinCareGiverHomeDetails Method Description: Request to get
     * the Home Assessment Template Data in prefill data format
     *
     * @param careGiverDetailsReq
     * @return
     */
    PreFillDataServiceDto getKinCareGiverHomeDetails(KinCareGiverHomeDetailsReq careGiverDetailsReq);
}