package us.tx.state.dfps.service.resourcedetail.dao;

import java.util.List;

import us.tx.state.dfps.service.resource.detail.dto.RtrvCpaNameParentResIdOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Getting records from Resource and RsrcLink using idResource and
 * cdRsrcLinkType Feb 2, 2018- 2:00:48 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface RtrvParentRsrcIdDao {
	/**
	 * 
	 * Method Name: getCapsRsrcLink Method Description: This method is used for
	 * retrieving the capsResourceLink Dam : CSECE9D
	 * 
	 * @param idResource
	 * @param cdRsrcLinkType
	 * @return rtrvCpaNameParentResIdOutDtoList @
	 */
	public List<RtrvCpaNameParentResIdOutDto> getCapsRsrcLink(Long idResource, String cdRsrcLinkType);
}
