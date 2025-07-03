package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.IncomingDetailStageInDto;
import us.tx.state.dfps.service.admin.dto.IncomingDetailStageOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for Cint07dDaoimpl Aug 10, 2017- 2:27:51 PM © 2017 Texas Department of Family
 * and Protective Services
 */
public interface IncomingDetailStageDao {

	/**
	 * 
	 * Method Name: getIncomingDetail Method Description: Fetch the incomedetail
	 * record for unique stage id. Cint07d
	 * 
	 * @param incomingDetailStageInDto
	 * @return List<IncomingDetailStageOutDto>
	 */
	public List<IncomingDetailStageOutDto> getIncomingDetail(IncomingDetailStageInDto incomingDetailStageInDto);
}
