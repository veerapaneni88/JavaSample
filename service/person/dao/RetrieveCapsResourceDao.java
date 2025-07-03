package us.tx.state.dfps.service.person.dao;

import java.util.List;

import us.tx.state.dfps.service.person.dto.RtrvRsrcByStageInDto;
import us.tx.state.dfps.service.person.dto.RtrvRsrcByStageOutDto;

public interface RetrieveCapsResourceDao {
	public List<RtrvRsrcByStageOutDto> retrieveCapsResource(RtrvRsrcByStageInDto rtrvRsrcByStageInDto,
			RtrvRsrcByStageOutDto rtrvRsrcByStageOutDto);

}
