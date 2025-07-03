package us.tx.state.dfps.service.forms.util;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.dto.RCCIScreeningDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

import java.util.ArrayList;
import java.util.List;

@Component
public class RCCIScreeningPrefillData extends DocumentServiceUtil {

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
        RCCIScreeningDto prefillDto = (RCCIScreeningDto) parentDtoobj;

        // Initialize null DTOs
        if (ObjectUtils.isEmpty(prefillDto)) {
            prefillDto = new RCCIScreeningDto();
        }
        List<BookmarkDto> bookmarkDtoList = new ArrayList<BookmarkDto>();
        bookmarkDtoList.add(createBookmark(BookmarkConstants.ID_CASE,
                prefillDto.getCaseId()));
        bookmarkDtoList.add(createBookmark(BookmarkConstants.INTAKE_CALL_ID,
                prefillDto.getIntakeId()));
        bookmarkDtoList.add(createBookmark(BookmarkConstants.DATE,
                prefillDto.getIncomingDate()));

        PreFillDataServiceDto prefillData = new PreFillDataServiceDto();
        prefillData.setBookmarkDtoList(bookmarkDtoList);

        return prefillData;
    }

}
