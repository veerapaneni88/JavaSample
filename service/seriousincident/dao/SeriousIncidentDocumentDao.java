package us.tx.state.dfps.service.seriousincident.dao;

import us.tx.state.dfps.service.seriousincidentdocument.dto.HomeApprovalEventInfoDto;

public interface SeriousIncidentDocumentDao {
    HomeApprovalEventInfoDto getHomeApprovalEventInfo(Long idStage);
}
