package us.tx.state.dfps.service.forms.util;

import org.springframework.stereotype.Repository;
import us.tx.state.dfps.monthlyassessmentadoption.dto.MonthlyAssessmentAdoptionDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

import java.util.ArrayList;
import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service
 * Authorization form used by CPS to refer clients for paid services under PRS
 * contracts. Mar 1, 2018- 1:52:54 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class MonthlyAssessmentAdoptionPrefillData extends DocumentServiceUtil {

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoObj) {

		MonthlyAssessmentAdoptionDto monthlyAssessmentAdoptionDto = (MonthlyAssessmentAdoptionDto) parentDtoObj;

		/**
		 * Description: Populating the non form group data into prefill data
		 * GroupName: None BookMark: Condition: None
		 */
		List<BookmarkDto> bookmarkDtoDefaultDtoList = new ArrayList<BookmarkDto>();
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setBookmarkDtoList(bookmarkDtoDefaultDtoList);
		preFillData.setFormDataGroupList(formDataGroupList);

		return preFillData;
	}

}
