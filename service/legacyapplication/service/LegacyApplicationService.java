package us.tx.state.dfps.service.legacyapplication.service;

import java.lang.reflect.InvocationTargetException;

import us.tx.state.dfps.service.fce.LegacyApplicationDto;

public interface LegacyApplicationService {

	public LegacyApplicationDto read(Long idStage, Long idEvent, Long idLastUpdatePerson);

	public Long save(LegacyApplicationDto legacyApplication);
}
