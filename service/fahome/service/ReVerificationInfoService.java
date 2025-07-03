package us.tx.state.dfps.service.fahome.service;

import us.tx.state.dfps.service.common.request.SeriousIncidentReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

public interface ReVerificationInfoService {

    public PreFillDataServiceDto getReVerificationDetails(SeriousIncidentReq reVerificationReq);
}
