package us.tx.state.dfps.service.conservatorship.dao;

import java.util.List;

import us.tx.state.dfps.service.cvs.dto.StageStartCloseInDto;
import us.tx.state.dfps.service.cvs.dto.StageStartCloseOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is the interface for Ccmne1dDaoImpl Aug 11, 2017- 5:34:10 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface StageStartCloseDao {

	public List<StageStartCloseOutDto> getStageDtls(StageStartCloseInDto stageStartCloseInDto);
}
