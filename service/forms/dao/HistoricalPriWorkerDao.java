package us.tx.state.dfps.service.forms.dao;

import java.util.List;

import us.tx.state.dfps.service.forms.dto.HistoricalPriWorkerDto;
import us.tx.state.dfps.service.forms.dto.HistoricalPriWorkerInDto;

public interface HistoricalPriWorkerDao {
	public List<HistoricalPriWorkerDto> getHistoricalPriWorker(HistoricalPriWorkerInDto pInputDataRec);

}
