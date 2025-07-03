package us.tx.state.dfps.service.subcontractor.dao;

import java.util.List;

import us.tx.state.dfps.service.subcontractor.dto.CapsResourceRsrcLinkInDto;
import us.tx.state.dfps.service.subcontractor.dto.CapsResourceRsrcLinkOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for CapsResourceRsrcLinkDaoImpl Aug 2, 2017- 8:35:09 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface CapsResourceRsrcLinkDao {

	/**
	 * 
	 * Method Name: getResource
	 * 
	 * Method Description:This method will get data from CAPS_RESOURCE and
	 * RSRC_LINK tables.
	 * 
	 * @param pInputDataRec
	 *            - request containing the resource id
	 * @return List<CapsResourceRsrcLinkOutDto> - List of subcontractor
	 *         associations for the resource
	 */
	public List<CapsResourceRsrcLinkOutDto> getResource(CapsResourceRsrcLinkInDto pInputDataRec);

}
