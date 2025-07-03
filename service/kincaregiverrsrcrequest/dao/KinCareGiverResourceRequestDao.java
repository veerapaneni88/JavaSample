package us.tx.state.dfps.service.kincaregiverrsrcrequest.dao;

import org.springframework.stereotype.Repository;
import us.tx.state.dfps.kincaregiverresourcerequest.dto.CaseWorkerAddressDto;
import us.tx.state.dfps.kincaregiverresourcerequest.dto.CaseWorkerNameDto;
import us.tx.state.dfps.kincaregiverresourcerequest.dto.CaseWorkerPhoneDto;
import us.tx.state.dfps.kincaregiverresourcerequest.dto.KinCareGiverContractInfoDto;
import us.tx.state.dfps.service.common.request.KinCareGiverResourceReq;

/**
 * Kinship Caregiver Resource/Contract Request Template (KIN10O00)
 * 07/21/2021 kurmav Artifact artf192721 : Prefill Service Dao for KIN10O00
 */
@Repository
public interface KinCareGiverResourceRequestDao {

    /**
     *
     * Method Name: getKinCareGiverContractInfo Method Description: Retrieves Contract Info (Placement duration, address)
     *  using ID STAGE, ID Case, ID Resource as input. Dam Name :CSECE1D
     *
     * @param careGiverResourceReq
     * @return KinCareGiverContractInfoDto
     */
    KinCareGiverContractInfoDto getKinCareGiverContractInfo(KinCareGiverResourceReq careGiverResourceReq);

    /**
     *
     * Method Name: getCaseWorkerName Method Description: Retrieves Primary Case Worker Name
     *  using ID STAGE as input. Dam Name :CCMN19D
     *
     * @param idStage
     * @return CaseWorkerNameDto
     */
    CaseWorkerNameDto getCaseWorkerName(Long idStage);

    /**
     *
     * Method Name: getCaseWorkerPhone Method Description: Retrieves phone type and phone
     * from the PERSON_PHONE table. Dam Name :CLSS0DD
     *
     * @param idPerson
     * @return CaseWorkerPhoneDto
     */
    CaseWorkerPhoneDto getCaseWorkerPhone(Long idPerson);

    /**
     *
     * Method Name: getCaseWorkerAddress Method Description: Retrieves active primary address, phone number, and name
     *  using idPerson as input. Dam Name :CSEC01D
     *
     * @param idPerson
     * @return CaseWorkerAddressDto
     */
    CaseWorkerAddressDto getCaseWorkerAddress(Long idPerson);

}
