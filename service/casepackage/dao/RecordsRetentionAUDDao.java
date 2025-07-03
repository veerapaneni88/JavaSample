package us.tx.state.dfps.service.casepackage.dao;

import us.tx.state.dfps.service.casepackage.dto.RecordRetentionDataInDto;
import us.tx.state.dfps.service.casepackage.dto.RecordRetentionDataOutDto;

public interface RecordsRetentionAUDDao {
	public void recordsRetentionAUD(RecordRetentionDataInDto recordRetentionDataInDto,
			RecordRetentionDataOutDto recordRetentionDataOutDto);

}
