package us.tx.state.dfps.service.forms.util;

import org.apache.commons.lang.StringUtils;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.workload.dto.NotifToRequestorDto;

import java.util.ArrayList;
import java.util.List;

public class NotifToReporterAdminReviewSpPrefillData extends DocumentServiceUtil {

    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
        NotifToRequestorDto prefillDto = (NotifToRequestorDto) parentDtoobj;

        List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

        // group ccf13o00
		if (StringUtils.isNotBlank(prefillDto.getAdministrativeReviewInvestigativeFindingsDto().getNbrMailCodePhone())) {
        FormDataGroupDto user2ExtGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_USER2_PHONE_EXT,
                FormConstants.EMPTY_STRING);
        List<BookmarkDto> bookmarkUser2ExtList = new ArrayList<BookmarkDto>();
        BookmarkDto bookmarkUser2Ext = createBookmark(BookmarkConstants.USER2_PHONE_EXT_EXTENSION,
                prefillDto.getAdministrativeReviewInvestigativeFindingsDto().getNbrMailCodePhone());
        bookmarkUser2ExtList.add(bookmarkUser2Ext);
        user2ExtGroupDto.setBookmarkDtoList(bookmarkUser2ExtList);
        formDataGroupList.add(user2ExtGroupDto);
    }
        List<BookmarkDto> bookmarkNonFormGroupList = new ArrayList<BookmarkDto>();

        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setFormDataGroupList(formDataGroupList);
        preFillData.setBookmarkDtoList(bookmarkNonFormGroupList);
        return preFillData;
    }

}
