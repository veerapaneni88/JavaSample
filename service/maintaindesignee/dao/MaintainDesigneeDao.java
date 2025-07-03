package us.tx.state.dfps.service.maintaindesignee.dao;

import java.util.List;

import us.tx.state.dfps.common.dto.MaintainDesigneeDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ARSafetyAssmtDao Sep 19, 2017- 2:19:16 PM © 2017 Texas Department
 * of Family and Protective Services
 */

/**
 * 
 * Method Description: Fetch the Designee Details
 * 
 * @param idPerson
 * @return List<MaintainDesigneeDto>
 * 
 */

public interface MaintainDesigneeDao {

	public List<MaintainDesigneeDto> getDesigneeDtls(Long idPerson);

}