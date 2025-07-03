package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.contact.dto.ContactNarrFormDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;

@Component
public class ContactNarrPrefillData extends DocumentServiceUtil {
	
	@Autowired
	LookupDao lookup;

	/**
	 * Method Description: This method is used to prefill the data from the
	 * different Dao by passing Dao output Dtos and bookmark and form group
	 * bookmark Dto as objects as input request
	 * 
	 * @param parentDtoobj
	 * @param bookmarkDtoObj
	 * @return PreFillData
	 * 
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		ContactNarrFormDto contactNarrFormDto = (ContactNarrFormDto) parentDtoobj;
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();		
		StringBuilder bookmarkValue = new StringBuilder();
		bookmarkValue.append(contactNarrFormDto.getDtContactOccurred());
		bookmarkValue.append(FormConstants.HYPHEN);
		bookmarkValue.append(contactNarrFormDto.getTmScrTmCntct());
		bookmarkValue.append(FormConstants.HYPHEN);
		if(!ObjectUtils.isEmpty(contactNarrFormDto.getCdContactOthers())) {
		bookmarkValue.append(lookup.decode("COTHCNCT", contactNarrFormDto.getCdContactOthers()));
		bookmarkValue.append(FormConstants.HYPHEN);
		}
		bookmarkValue.append(lookup.decode("CCNTPURP", contactNarrFormDto.getCdContactPurpose()));
		bookmarkValue.append(FormConstants.HYPHEN);
		if(!ObjectUtils.isEmpty(contactNarrFormDto.getCdContactMethod())) {
		bookmarkValue.append(lookup.decode("CCNTMETH", contactNarrFormDto.getCdContactMethod()));
		}
		if(!ObjectUtils.isEmpty(contactNarrFormDto.getTxtNmPersonFull())) {
		bookmarkValue.append(FormConstants.HYPHEN);
		bookmarkValue.append(contactNarrFormDto.getTxtNmPersonFull());
		}
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkContactNarrative = createBookmark(BookmarkConstants.CONTACTNARRATIVE, bookmarkValue);
		bookmarkNonFrmGrpList.add(bookmarkContactNarrative);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}

}
