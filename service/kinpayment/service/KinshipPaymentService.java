package us.tx.state.dfps.service.kinpayment.service;

import us.tx.state.dfps.service.common.request.KinCareGiverResourceReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

import org.springframework.stereotype.Service;

@Service
public interface KinshipPaymentService {

    PreFillDataServiceDto getKinReimbursementPaymentDetails(KinCareGiverResourceReq req);

    PreFillDataServiceDto getKinIntegrationPaymentDetails(KinCareGiverResourceReq req);
}
