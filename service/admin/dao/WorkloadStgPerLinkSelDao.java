package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.WorkloadStgPerLinkSelInDto;
import us.tx.state.dfps.service.admin.dto.WorkloadStgPerLinkSelOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Cinv51dDao
 * Aug 9, 2017- 1:13:44 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface WorkloadStgPerLinkSelDao {

	public List<WorkloadStgPerLinkSelOutDto> getWorkLoad(WorkloadStgPerLinkSelInDto workloadStgPerLinkSelInDto);
}
