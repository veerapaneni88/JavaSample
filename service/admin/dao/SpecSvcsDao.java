package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.SpecSvcsInDto;
import us.tx.state.dfps.service.admin.dto.SpecSvcsOutDto;
import us.tx.state.dfps.service.common.request.FacilityDetailSaveReq;
import us.tx.state.dfps.service.common.response.FacilityDetailRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: SpecSvcsDao
 * Feb 9, 2018- 3:49:46 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface SpecSvcsDao {

	/**
	 * Method Name: getSpecSvcsOutDtoList Method Description: This method is
	 * used to getSpecSvcsOutDtoList
	 * 
	 * @param pInputDataRec
	 * @ @return List<SpecSvcsOutDto>
	 */
	public List<SpecSvcsOutDto> getSpecSvcsOutDtoList(SpecSvcsInDto pInputDataRec);

	/**
	 * Method Name: specSvcsAud Method Description: This method is used to
	 * perform AUD operations on SPEC_SVCS table
	 * 
	 * @param facilityDetailSaveReq
	 * @ @return void
	 */
	public FacilityDetailRes specSvcsAud(FacilityDetailSaveReq facilityDetailSaveReq,
			FacilityDetailRes facilityDetailRes);

}
