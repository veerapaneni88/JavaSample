package us.tx.state.dfps.service.seriousincident.service;

import us.tx.state.dfps.service.common.request.SeriousIncidentReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

public interface SeriousIncidentDocumentService {
    PreFillDataServiceDto getSeriousIncidentData(SeriousIncidentReq seriousIncidentReq);
}
