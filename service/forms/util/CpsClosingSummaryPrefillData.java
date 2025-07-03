package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.CpsClosingSummaryDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.pca.dto.StageCaseDtlDto;
import us.tx.state.dfps.service.workload.dto.ContactDto;

@Repository
public class CpsClosingSummaryPrefillData extends DocumentServiceUtil {

	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		CpsClosingSummaryDto contactNarrativeDto = (CpsClosingSummaryDto) parentDtoobj;

		if (null == contactNarrativeDto.getStageCaseDtlDto()) {
			contactNarrativeDto.setStageCaseDtlDto(new StageCaseDtlDto());
		}

		if (null == contactNarrativeDto.getContactDto()) {
			contactNarrativeDto.setContactDto(new ContactDto());
		}

		/**
		 * Description: Populating the non form group data into prefill data
		 * GroupName: None BookMark: Condition: None
		 */

		List<BookmarkDto> bookmarkDtoDefaultDtoList = new ArrayList<BookmarkDto>();
		List<BlobDataDto> blobDataDtoForContactNarrative = new ArrayList<BlobDataDto>();

		BookmarkDto bkTitleCaseName = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				contactNarrativeDto.getStageCaseDtlDto().getNmCase());
		bookmarkDtoDefaultDtoList.add(bkTitleCaseName);

		BookmarkDto bkTitleCaseNumber = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				contactNarrativeDto.getStageCaseDtlDto().getIdCase());
		bookmarkDtoDefaultDtoList.add(bkTitleCaseNumber);

		if (!ObjectUtils.isEmpty(contactNarrativeDto.getContactDto().getIdEvent())) {
			BlobDataDto blobContactData = createBlobData(BookmarkConstants.CPS_CLOS_SUM_NARR_BLOB,
					CodesConstant.CONTACT_NARRATIVE,
					Integer.valueOf(contactNarrativeDto.getContactDto().getIdEvent().toString()));
			blobDataDtoForContactNarrative.add(blobContactData);
		}

		PreFillDataServiceDto preFillDataServiceDto = new PreFillDataServiceDto();
		preFillDataServiceDto.setBookmarkDtoList(bookmarkDtoDefaultDtoList);

		preFillDataServiceDto.setBlobDataDtoList(blobDataDtoForContactNarrative);
		return preFillDataServiceDto;
	}
}
