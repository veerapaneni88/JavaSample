package us.tx.state.dfps.service.fcl.service;

import us.tx.state.dfps.web.fcl.dto.SexualVictimHistoryDto;

import java.util.List;

public interface MutualNonAggressiveService {
    public SexualVictimHistoryDto getMutualNonAggressiveIncidents(Long idPerson);

    public void saveOrUpdateMutualNonAggressiveIncidents(SexualVictimHistoryDto sexualVictimHistoryDto);

    public void deleteMutualNonAggressiveIncidents(List<Long> incidentIds, Long idPerson);
}
