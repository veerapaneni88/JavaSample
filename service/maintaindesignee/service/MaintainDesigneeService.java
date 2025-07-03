package us.tx.state.dfps.service.maintaindesignee.service;

import java.util.List;

import us.tx.state.dfps.common.dto.MaintainDesigneeDto;
import us.tx.state.dfps.service.common.request.EmpTempAssignReq;

public interface MaintainDesigneeService {

	/**
	 * CLSS16D - This DAM retrieves all designee assignments for the given
	 * employee
	 * 
	 * @param MaintainDesigneeReq
	 * @return MaintainDesigneeRes
	 */
	public List<MaintainDesigneeDto> getDesigneeDtls(Long idPerson);

	/**
	 * CARC17D - This DAM adds/updates/deletes the EmpTempAssign Table
	 * 
	 * @param EmpTempAssignReq
	 * @return Long
	 */
	public Long updateEmpTempAssign(EmpTempAssignReq empTempAssignReq);

}
