package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.NamePersonInDto;
import us.tx.state.dfps.service.admin.dto.NamePersonOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * fetches the Name Details of a Person Using PersonID Aug 7, 2017- 3:27:33 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface NamePersonDao {

	/**
	 * 
	 * Method Name: PrsnDtls Method Description:This method retrieves data from
	 * NAME table.
	 * 
	 * @param pInputDataRec
	 * @param pOutputDataRec
	 * @return List<NamePersonOutDto>
	 * 
	 */
	public List<NamePersonOutDto> prsnDtls(NamePersonInDto pInputDataRec, NamePersonOutDto pOutputDataRec);
}
