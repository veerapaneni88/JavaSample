package us.tx.state.dfps.service.fcl.dao;

import us.tx.state.dfps.common.domain.ChildSxMutalIncdnt;
import us.tx.state.dfps.web.fcl.dto.SexualVictimHistoryDto;

import java.util.List;

public interface MutualNonAggressiveIncidentDao {
    List<ChildSxMutalIncdnt> getMutualNonAggressiveIncidents(Long idPerson);

    List<ChildSxMutalIncdnt> getIdPersonMutualIncidents(Long idPersonMutual);

    void saveMutualNonAggressiveIncidents(SexualVictimHistoryDto sexualVictimHistoryDto);

    void deleteMutualNonAggressiveIncidents(List<Long> incidentIds, Long idPerson);
}
