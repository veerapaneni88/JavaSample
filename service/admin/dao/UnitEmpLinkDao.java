package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.EmpLinkInDto;
import us.tx.state.dfps.service.admin.dto.EmpLinkOutDto;
import us.tx.state.dfps.service.admin.dto.UnitEmpLinkDto;
import us.tx.state.dfps.service.admin.dto.UnitEmpLinkInDto;
import us.tx.state.dfps.service.admin.dto.UnitEmpLinkOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Csec04dDao
 * Aug 10, 2017- 2:22:35 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface UnitEmpLinkDao {

	public List<EmpLinkOutDto> getUnitEmpLinkRecord(EmpLinkInDto pInputDataRec);

	public List<UnitEmpLinkOutDto> getUnitEmpLinkRecord(UnitEmpLinkInDto pInputDataRec);

	List<UnitEmpLinkDto> getUnitEmpLinkDtl(UnitEmpLinkDto unitEmpLinkDto);

}
