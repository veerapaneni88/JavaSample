package us.tx.state.dfps.service.forms.util;


import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.lookup.service.LookupService;
import us.tx.state.dfps.service.servicedelivery.dto.ClosureFormDto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ServiceDlvryClosurePrefillData extends DocumentServiceUtil {

    @Autowired
    LookupService lookupService;


    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoObj) {

        ClosureFormDto closureFormDto = (ClosureFormDto) parentDtoObj;
        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        List<BookmarkDto> bookmarkGroupList = new ArrayList<>();

        bookmarkGroupList.add(createBookmark(BookmarkConstants.TITLE_CASE_NAME, closureFormDto.getCaseName()));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.TITLE_CASE_NUMBER, closureFormDto.getCaseNumber()));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.TITLE_STAGE_PROGRAM, closureFormDto.getCdStageProgram()));
        bookmarkGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.STAGE_CODE, closureFormDto.getStageCode(), CodesConstant.CSTAGES));
        bookmarkGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.STAGE_TYPE, closureFormDto.getStageType(), CodesConstant.CSTGTYPE));
        bookmarkGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.STAGE_CLOSURE_REASON, closureFormDto.getReasonSelected(), CodesConstant.CAPSCLSV));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.STAGE_DECISION_DATE, closureFormDto.getDtSvcDelvDecision()));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.ECS_IND, closureFormDto.getIndECS()));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.ECS_IND_VER, closureFormDto.getIndECSVer()));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.CD_CLIENT_ADVISED, closureFormDto.getCdClientAdvised()));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.CLIENT_ADVISED_DATE, closureFormDto.getDtClientAdvised()));
        bookmarkGroupList.add(createBookmark(BookmarkConstants.STAGE_CLOSURE_COMMENTS, closureFormDto.getStageClosureCmnts()));
        preFillData.setBookmarkDtoList(bookmarkGroupList);

        return preFillData;
    }
}
