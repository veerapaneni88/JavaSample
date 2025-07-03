package us.tx.state.dfps.service.kincaregiverhomeassmnt.dao;

import org.springframework.stereotype.Repository;
import us.tx.state.dfps.kincaregiverhomedetails.dto.KinCareGiverAddressDto;
import us.tx.state.dfps.kincaregiverhomedetails.dto.KinCareGiverCaseInfoDto;

/**
 * service-business - Kinship CareGiver Home Assessment Template (KIN12O00)
 * 07/19/2021 kurmav Artifact artf192718 : Prefill Service for KIN12O00
 */
@Repository
public interface KinCareGiverHomeAssmntDao {

    /**
     *
     * Method Name: getKinCareGiverCaseInfo Method Description: Retrieves Case Information (Case Name, Case Id)
     *  using ID STAGE as input. Dam Name :CSEC02D
     *
     * @param idStage
     * @return KinCareGiverCaseInfoDto
     */
    KinCareGiverCaseInfoDto getKinCareGiverCaseInfo(Long idStage);

    /**
     *
     * Method Name: getKinCareGiverAddress Method Description: Retrieves Care Giver Address Info
     * using idResource as input. Dam Name : CRES0AD
     *
     * @param idResource
     * @return KinCareGiverAddressDto
     */
    KinCareGiverAddressDto getKinCareGiverAddress(Long idResource);
}
