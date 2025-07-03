package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.RecordsCheckPersonInDto;
import us.tx.state.dfps.service.admin.dto.RecordsCheckPersonOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for RecordsCheckPersonDaoImpl Aug 7, 2017- 3:27:01 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface RecordsCheckPersonDao {

	/**
	 * 
	 * Method Name: recCheckDtls Method Description: This method retrieves data
	 * from RECORDS_CHECK and PERSON tables.
	 * 
	 * @param pInputDataRec
	 * @param pOutputDataRec
	 * @return List<RecordsCheckPersonOutDto>
	 */
	public List<RecordsCheckPersonOutDto> recCheckDtls(RecordsCheckPersonInDto pInputDataRec,
			RecordsCheckPersonOutDto pOutputDataRec);
}
