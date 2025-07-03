package us.tx.state.dfps.service.kinpayment.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.request.KinCareGiverResourceReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.KinshipPaymentPrefillData;
import us.tx.state.dfps.service.kin.dto.KinPaymentApplicationRes;
import us.tx.state.dfps.service.kinpayment.dao.KinshipPaymentDao;
import us.tx.state.dfps.service.kinpayment.service.KinshipPaymentService;

@Service
@Transactional
public class KinshipPaymentServiceImpl implements KinshipPaymentService {

    @Autowired
    KinshipPaymentDao kinshipPaymentDao;
    @Autowired
    KinshipPaymentPrefillData kinshipPaymentPrefillData;

    @Override
    public PreFillDataServiceDto getKinReimbursementPaymentDetails(KinCareGiverResourceReq req) {

        KinPaymentApplicationRes kinPaymentApplicationRes = new KinPaymentApplicationRes();
        kinPaymentApplicationRes.setKinPaymentCareGiverDto(kinshipPaymentDao.getKinCareGiverDetailsSql(req.getIdStage(), req.getIdResource()));
        kinPaymentApplicationRes.setKinPaymentChildDto(kinshipPaymentDao.getKinChildDetailsSql(req.getIdResource(), req.getIdPerson()));
        kinPaymentApplicationRes.setKinChildLegalOutcomeDate(kinshipPaymentDao.getKinLegalOutcomeDateSql(req.getIdCase(), req.getIdPerson()));
        return kinshipPaymentPrefillData.returnPrefillData(kinPaymentApplicationRes);
    }

    @Override
    public PreFillDataServiceDto getKinIntegrationPaymentDetails(KinCareGiverResourceReq req) {

        KinPaymentApplicationRes kinPaymentApplicationRes = new KinPaymentApplicationRes();
        kinPaymentApplicationRes.setKinPaymentCareGiverDto(kinshipPaymentDao.getKinCareGiverDetailsSql(req.getIdStage(), req.getIdResource()));
        return kinshipPaymentPrefillData.returnPrefillData(kinPaymentApplicationRes);
    }

}
