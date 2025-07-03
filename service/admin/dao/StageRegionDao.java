package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.StageRegionInDto;
import us.tx.state.dfps.service.admin.dto.StageRegionOutDto;

public interface StageRegionDao {

	public List<StageRegionOutDto> getStageDtls(StageRegionInDto pInputDataRec);
}
