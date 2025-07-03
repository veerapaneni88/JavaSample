package us.tx.state.dfps.service.person.service;

import us.tx.state.dfps.service.common.request.EducListDtlSaveiDto;
import us.tx.state.dfps.service.common.response.EducListDtlSaveoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Name:
 * EducListDtlSaveService Class Description: service for save,update and delete
 * educational detail March 2018- 8:05:58 PM © 2017 Texas Department of Family
 * and Protective Services
 */
public interface EducListDtlSaveService {
	public EducListDtlSaveoDto saveEducationalDetail(EducListDtlSaveiDto educListDtlSaveiDto);

	public void saveEducationNeed_aud(EducListDtlSaveiDto educListDtlSaveiDto, String addOrDelete,
			EducListDtlSaveoDto educListDtlSaveoDto);

}
