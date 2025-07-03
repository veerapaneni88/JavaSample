package us.tx.state.dfps.service.seriousincident.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.tx.state.dfps.service.common.request.SeriousIncidentReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.SeriousIncidentDocumentPrefillData;
import us.tx.state.dfps.service.seriousincident.dao.SeriousIncidentDocumentDao;
import us.tx.state.dfps.service.seriousincident.service.SeriousIncidentDocumentService;
import us.tx.state.dfps.service.seriousincidentdocument.dto.HomeApprovalEventInfoDto;
import us.tx.state.dfps.service.seriousincidentdocument.dto.SeriousIncidentDocumentDto;

@Service
@Transactional
public class SeriousIncidentDocumentServiceImpl implements SeriousIncidentDocumentService {

    @Autowired
    SeriousIncidentDocumentDao seriousIncidentDao;

    @Autowired
    SeriousIncidentDocumentPrefillData seriousIncidentPrefillData;

    @Override
    public PreFillDataServiceDto getSeriousIncidentData(SeriousIncidentReq seriousIncidentReq) {
        SeriousIncidentDocumentDto seriousIncidentDocumentDto = new SeriousIncidentDocumentDto();
        HomeApprovalEventInfoDto homeApprovalEventInfoDto = seriousIncidentDao.getHomeApprovalEventInfo(seriousIncidentReq.getIdStage());
        seriousIncidentDocumentDto.setHomeApprovalInfoDto(homeApprovalEventInfoDto);
        return seriousIncidentPrefillData.returnPrefillData(seriousIncidentDocumentDto);
    }
}
