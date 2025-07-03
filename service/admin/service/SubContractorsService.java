package us.tx.state.dfps.service.admin.service;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.SubContractorsDto;
import us.tx.state.dfps.service.admin.dto.SubContractorsReq;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * SubContractorsService Feb 9, 2018- 5:56:19 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface SubContractorsService {

	/**
	 * Method Name: getSubContractorsDtoList Method Description: This method is
	 * used to getSubContractorsDtoList
	 * 
	 * @param subContractorsReq
	 * @ @return List<SubContractorsDto>
	 */
	public List<SubContractorsDto> getSubContractorsDtoList(SubContractorsReq subContractorsReq);
}
