package us.tx.state.dfps.service.fahome.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.request.SeriousIncidentReq;
import us.tx.state.dfps.service.fahome.service.ReVerificationInfoService;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.SeriousIncidentDocumentPrefillData;
import us.tx.state.dfps.service.seriousincident.dao.SeriousIncidentDocumentDao;

@Service
@Transactional
public class ReVerificationInfoServiceImpl implements ReVerificationInfoService {

    @Autowired
    SeriousIncidentDocumentPrefillData seriousIncidentPreFillData;
    @Autowired
    private SeriousIncidentDocumentDao seriousIncidentDocumentDao;

    @Override
    public PreFillDataServiceDto getReVerificationDetails(SeriousIncidentReq reVerificationReq) {
      return null;
    }
}
