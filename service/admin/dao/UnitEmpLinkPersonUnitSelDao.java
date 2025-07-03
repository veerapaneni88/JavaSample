package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.UnitEmpLinkPersonUnitSelInDto;
import us.tx.state.dfps.service.admin.dto.UnitEmpLinkPersonUnitSelOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Ccmn60dDao
 * Aug 10, 2017- 3:04:29 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface UnitEmpLinkPersonUnitSelDao {

	public List<UnitEmpLinkPersonUnitSelOutDto> getPersonName(UnitEmpLinkPersonUnitSelInDto pInputDataRec);
}
