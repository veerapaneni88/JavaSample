package us.tx.state.dfps.service.forms.util;

import org.springframework.stereotype.Component;
import us.tx.state.dfps.populatenocletter.dto.PopulateNocLetterDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class PopulateNocLetterPrefillData extends DocumentServiceUtil{

    /**
     * Method Description: This method is used to prefill the data from the
     * different Dao by passing Dao output Dtos and bookmark and form group
     * bookmark Dto as objects as input request
     *
     * @param parentDtoobj
     * @return PreFillData
     *
     */
    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

        List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

        List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

        PopulateNocLetterDto populateNocLetterDto = (PopulateNocLetterDto) parentDtoobj;

        if (null != populateNocLetterDto.getGenCaseInfoDto()) {
            BookmarkDto bookmarkCaseNum = createBookmark(BookmarkConstants.CASE_NUMBER,
                    populateNocLetterDto.getGenCaseInfoDto().getIdCase());
            BookmarkDto bookmarkDtInvClosed = createBookmark(BookmarkConstants.DT_INV_CLOSED,
                    TypeConvUtil.formDateFormat(populateNocLetterDto.getGenCaseInfoDto().getDtStageClose()));
            bookmarkNonFrmGrpList.add(bookmarkCaseNum);
            bookmarkNonFrmGrpList.add(bookmarkDtInvClosed);
        }

        BookmarkDto bookmarkDtInvRptChanged = createBookmark(BookmarkConstants.DT_INV_RPT_CHANGED,
                TypeConvUtil.formDateFormat(new Date()));
        bookmarkNonFrmGrpList.add(bookmarkDtInvRptChanged);

        if (null != populateNocLetterDto.getRepPersonInfo()) {
            BookmarkDto bookmarkRepName = createBookmark(BookmarkConstants.REP_FULL_NAME,
                    populateNocLetterDto.getRepPersonInfo().getNmPersonFull());
            BookmarkDto bookmarkRepPhone = createBookmark(BookmarkConstants.REP_PHONE,
                    populateNocLetterDto.getRepPersonInfo().getPersonPhone());
            bookmarkNonFrmGrpList.add(bookmarkRepName);
            bookmarkNonFrmGrpList.add(bookmarkRepPhone);
        }

        if (null != populateNocLetterDto.getPersonNotifiedInfo()) {
            BookmarkDto bookmarkNotiPersonName = createBookmark(BookmarkConstants.NOTIFIED_PERSON_FULL_NAME,
                    populateNocLetterDto.getPersonNotifiedInfo().getNmPersonFull());
            bookmarkNonFrmGrpList.add(bookmarkNotiPersonName);
        }

        if (null != populateNocLetterDto.getPersonNotifiedInfo()) {
            FormDataGroupDto tempAddressFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PERSON_ADDRESS,
                    FormConstants.EMPTY_STRING);
            List<FormDataGroupDto> tempAddressFrmDataGrpList = new ArrayList<FormDataGroupDto>();
            List<BookmarkDto> bookmarkAddressList = new ArrayList<BookmarkDto>();
            BookmarkDto bookmarkAddressZipDto = createBookmark(BookmarkConstants.NOTIFY_ADDR_ZIP,
                    populateNocLetterDto.getPersonNotifiedInfo().getAddrPersonZip());
            BookmarkDto bookmarkAddressCityDto = createBookmark(BookmarkConstants.NOTIFY_ADDR_CITY,
                    populateNocLetterDto.getPersonNotifiedInfo().getAddrPersonCity());
            BookmarkDto bookmarkAddressStLn1Dto = createBookmark(BookmarkConstants.NOTIFY_ADDR_STREET1,
                    populateNocLetterDto.getPersonNotifiedInfo().getAddrPersonStLn1());
            BookmarkDto bookmarkAddressStateDto = createBookmark(BookmarkConstants.NOTIFY_ADDR_STATE,
                    populateNocLetterDto.getPersonNotifiedInfo().getCdPersonState());

            bookmarkAddressList.add(bookmarkAddressZipDto);
            bookmarkAddressList.add(bookmarkAddressCityDto);
            bookmarkAddressList.add(bookmarkAddressStLn1Dto);
            bookmarkAddressList.add(bookmarkAddressStateDto);

            tempAddressFrmDataGrpDto.setBookmarkDtoList(bookmarkAddressList);


            if (!TypeConvUtil.isNullOrEmpty(populateNocLetterDto.getPersonNotifiedInfo().getAddrPersonStLn2())) {
                FormDataGroupDto tempAddLine2FrmDataGrpDto = createFormDataGroup(
                        FormGroupsConstants.TMPLAT_PERSON_ADDRESS_LINE_2, FormGroupsConstants.TMPLAT_PERSON_ADDRESS);
                List<BookmarkDto> bookmarkAddressLineList = new ArrayList<BookmarkDto>();
                BookmarkDto bookmarkRepAddressLine2Dto = createBookmark(BookmarkConstants.NOTIFY_ADDR_STREET2,
                        populateNocLetterDto.getPersonNotifiedInfo().getAddrPersonStLn2());

                bookmarkAddressLineList.add(bookmarkRepAddressLine2Dto);
                tempAddLine2FrmDataGrpDto.setBookmarkDtoList(bookmarkAddressLineList);
                tempAddressFrmDataGrpList.add(tempAddLine2FrmDataGrpDto);
            }

            tempAddressFrmDataGrpDto.setFormDataGroupList(tempAddressFrmDataGrpList);
            formDataGroupList.add(tempAddressFrmDataGrpDto);
        }

        if (null != populateNocLetterDto.getContactInfo()) {
            BookmarkDto bookmarkDescOfChanges = createBookmark(BookmarkConstants.DESC_OF_CHANGES,
                    populateNocLetterDto.getContactInfo().getTxtClosureDesc());
            bookmarkNonFrmGrpList.add(bookmarkDescOfChanges);
        }

        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setFormDataGroupList(formDataGroupList);
        preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);

        return preFillData;
    }
}
