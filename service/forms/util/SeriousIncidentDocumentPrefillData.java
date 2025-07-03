package us.tx.state.dfps.service.forms.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.seriousincidentdocument.dto.SeriousIncidentDocumentDto;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SeriousIncidentDocumentPrefillData extends DocumentServiceUtil {

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
        SeriousIncidentDocumentDto seriousIncidentDocumentDto = (SeriousIncidentDocumentDto) parentDtoobj;
        List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
        List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<BookmarkDto>();

        if (!ObjectUtils.isEmpty(seriousIncidentDocumentDto.getHomeApprovalInfoDto())) {
            BookmarkDto bookmarkDtHomeApprovalDate = createBookmark(BookmarkConstants.HOME_APPROVAL,
                    DateUtils.stringDt(seriousIncidentDocumentDto.getHomeApprovalInfoDto().getDtEventOccured()));
            bookmarkNonFormGrpList.add(bookmarkDtHomeApprovalDate);
            BookmarkDto bookmarkDtHomePreferredCapacity = createBookmark(BookmarkConstants.HOME_PREF_CAP,
                    seriousIncidentDocumentDto.getHomeApprovalInfoDto().getPreferredCapacity());
            bookmarkNonFormGrpList.add(bookmarkDtHomePreferredCapacity);
        }

        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setFormDataGroupList(formDataGroupList);
        preFillData.setBookmarkDtoList(bookmarkNonFormGrpList);

        return preFillData;
    }
}
