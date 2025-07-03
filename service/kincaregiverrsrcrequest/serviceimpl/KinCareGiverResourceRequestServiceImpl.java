package us.tx.state.dfps.service.kincaregiverrsrcrequest.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.tx.state.dfps.kincaregiverresourcerequest.dto.*;
import us.tx.state.dfps.service.common.request.KinCareGiverResourceReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.KinCareGiverResourceRequestPrefillData;
import us.tx.state.dfps.service.kincaregiverrsrcrequest.dao.KinCareGiverResourceRequestDao;
import us.tx.state.dfps.service.kincaregiverrsrcrequest.service.KinCareGiverResourceRequestService;

/**
 * Kinship Caregiver Resource/Contract Request Template (KIN10O00)
 * 07/21/2021 kurmav Artifact artf192721 : Prefill Service for KIN10O00
 */
@Service
@Transactional
public class KinCareGiverResourceRequestServiceImpl implements KinCareGiverResourceRequestService {

    @Autowired
    KinCareGiverResourceRequestDao kinCareGiverResourceRequestDao;

    @Autowired
    KinCareGiverResourceRequestPrefillData kinCareGiverResourceRequestPrefillData;

    /**
     * Method Name: getKinCareGiverResource Method Description: Request to get
     * Kinship Caregiver Resource/Contract Request Template in prefill data format
     *
     * @param careGiverResourceReq
     * @return
     */
    @Override
    public PreFillDataServiceDto getKinCareGiverResource(KinCareGiverResourceReq careGiverResourceReq) {

        KinCareGiverResourceRes careGiverResourceRes = new KinCareGiverResourceRes();

        KinCareGiverContractInfoDto careGiverContractInfo = kinCareGiverResourceRequestDao.getKinCareGiverContractInfo(careGiverResourceReq);

        careGiverResourceRes.setCareGiverContractInfo(careGiverContractInfo);

        CaseWorkerNameDto caseWorkerNameDto = kinCareGiverResourceRequestDao.getCaseWorkerName(careGiverResourceReq.getIdStage());

        careGiverResourceRes.setCaseWorkerNameDto(caseWorkerNameDto);

        CaseWorkerPhoneDto caseWorkerPhoneDto = kinCareGiverResourceRequestDao.getCaseWorkerPhone(careGiverResourceReq.getIdPerson());

        careGiverResourceRes.setCaseWorkerPhoneDto(caseWorkerPhoneDto);

        CaseWorkerAddressDto caseWorkerAddressDto = kinCareGiverResourceRequestDao.getCaseWorkerAddress(careGiverResourceReq.getIdPerson());

        careGiverResourceRes.setCaseWorkerAddressDto(caseWorkerAddressDto);

        return kinCareGiverResourceRequestPrefillData.returnPrefillData(careGiverResourceRes);
    }
}
