package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.EventdiDto;
import us.tx.state.dfps.service.admin.dto.EventdoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Clsc71dDao
 * Aug 11, 2017- 1:38:30 AM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface EventRetDao {
	public List<EventdoDto> getEventValues(EventdiDto pInputDataRec);

}
