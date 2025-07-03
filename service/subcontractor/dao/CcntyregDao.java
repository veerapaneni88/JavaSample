package us.tx.state.dfps.service.subcontractor.dao;

import java.util.List;

import us.tx.state.dfps.service.subcontractor.dto.CcntyregDto;
import us.tx.state.dfps.service.subcontractor.dto.CcntyregiDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * 
 * Class Description:This interface has methods for
 * 
 * Jan 20, 2018- 2:19:03 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface CcntyregDao {

	/**
	 * 
	 * Method Name: getResourceServiceById
	 * 
	 * Method Description:This retrieves all counties for a specific region from
	 * the CCNTYREG table . DAM Name :CRES32D
	 * 
	 * @param pInputDataRec
	 * @return @
	 */
	public List<String> getRegionCnty(CcntyregiDto pInputDataRec);

	/**
	 * Method Name: getRegionFromCounty
	 * Method Description: Retrieves region from CCNTYREG table based on the county.
	 *
	 * @param ccntyRegDto
	 * @return String
	 */

	public String getRegionFromCounty(CcntyregDto ccntyRegDto) ;

	}
