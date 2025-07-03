package us.tx.state.dfps.service.forms.util;

import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.apslettertoclient.dto.ApslettertoClientServiceDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;

import java.util.ArrayList;
import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps Letter to Client when cannot locate-- CIV37o00.
 * Dec 02, 2021- 1:52:46 PM © 2021 Texas Department of Family and
 * Protective Services
 */

@Repository
public class ApslettertoClientServicePrefillData extends DocumentServiceUtil {

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

        ApslettertoClientServiceDto apslettertoClientServiceDto = (ApslettertoClientServiceDto) parentDtoobj;

        if (ObjectUtils.isEmpty(apslettertoClientServiceDto.getEmployeePersPhNameDto())) {
            apslettertoClientServiceDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
        }
        if (ObjectUtils.isEmpty(apslettertoClientServiceDto.getCodesTablesDto())) {
            apslettertoClientServiceDto.setCodesTablesDto(new CodesTablesDto());
        }
        if (ObjectUtils.isEmpty(apslettertoClientServiceDto.getCaseInfoDto())) {
            apslettertoClientServiceDto.setCaseInfoDto(new CaseInfoDto());
        }

        List<FormDataGroupDto> formDataGroupList = new ArrayList<>();
        List<BookmarkDto> bookmarkDtoDefaultDtoList = new ArrayList<>();

        populateHeaderData(apslettertoClientServiceDto, formDataGroupList);
        populateSystemDate(apslettertoClientServiceDto, bookmarkDtoDefaultDtoList);
        populateWorkerData(apslettertoClientServiceDto, formDataGroupList, bookmarkDtoDefaultDtoList);
        populateVictimData(apslettertoClientServiceDto, formDataGroupList);

        PreFillDataServiceDto preFillDataServiceDto = new PreFillDataServiceDto();
        preFillDataServiceDto.setFormDataGroupList(formDataGroupList);
        preFillDataServiceDto.setBookmarkDtoList(bookmarkDtoDefaultDtoList);
        return preFillDataServiceDto;
    }

    /**
     * method to populate Victim Prefill data
     * @param apslettertoClientServiceDto
     * @param formDataGroupList
     */
    private void populateVictimData(ApslettertoClientServiceDto apslettertoClientServiceDto, List<FormDataGroupDto> formDataGroupList) {
        if (null != apslettertoClientServiceDto.getCaseInfoDto()){

            FormDataGroupDto headerVictimGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_VICTIM,
                    FormConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkVictimList = new ArrayList<BookmarkDto>();

            BookmarkDto bkCaseVctAFName = createBookmark(BookmarkConstants.VICTIM_ADDR_NAME_FIRST,
                    apslettertoClientServiceDto.getCaseInfoDto().getNmNameFirst());
            bookmarkVictimList.add(bkCaseVctAFName);

            BookmarkDto bkCaseVctALName = createBookmark(BookmarkConstants.VICTIM_ADDR_NAME_MIDDLE,
                    apslettertoClientServiceDto.getCaseInfoDto().getNmNameMiddle());
            bookmarkVictimList.add(bkCaseVctALName);

            BookmarkDto bkCaseVctAMiddle = createBookmark(BookmarkConstants.VICTIM_ADDR_NAME_LAST,
                    apslettertoClientServiceDto.getCaseInfoDto().getNmNameLast());
            bookmarkVictimList.add(bkCaseVctAMiddle);

            BookmarkDto bkCaseVctFName = createBookmark(BookmarkConstants.VICTIM_NAME_FIRST,
                    apslettertoClientServiceDto.getCaseInfoDto().getNmNameFirst());
            bookmarkVictimList.add(bkCaseVctFName);

            BookmarkDto bkCaseVctLName = createBookmark(BookmarkConstants.VICTIM_NAME_MIDDLE,
                    apslettertoClientServiceDto.getCaseInfoDto().getNmNameMiddle());
            bookmarkVictimList.add(bkCaseVctLName);

            BookmarkDto bkCaseVctMiddle = createBookmark(BookmarkConstants.VICTIM_NAME_LAST,
                    apslettertoClientServiceDto.getCaseInfoDto().getNmNameLast());
            bookmarkVictimList.add(bkCaseVctMiddle);

            BookmarkDto bkCaseWVctAddrLnOne = createBookmark(BookmarkConstants.VICTIM_ADDRESS_LINE_1,
                    apslettertoClientServiceDto.getCaseInfoDto().getAddrPersAddrStLn1());
            bookmarkVictimList.add(bkCaseWVctAddrLnOne);

            BookmarkDto bkCaseVctAddrLnTwo = createBookmark(BookmarkConstants.VICTIM_ADDRESS_LINE_2 ,
                    apslettertoClientServiceDto.getCaseInfoDto().getAddrPersAddrStLn2());
            bookmarkVictimList.add(bkCaseVctAddrLnTwo);

            BookmarkDto bkCaseVctCity = createBookmark(BookmarkConstants.VICTIM_ADDRESS_CITY,
                    apslettertoClientServiceDto.getCaseInfoDto().getAddrPersonAddrCity());
            bookmarkVictimList.add(bkCaseVctCity);

            BookmarkDto bkCaseVctState = createBookmark(BookmarkConstants.VICTIM_ADDRESS_STATE,
                    apslettertoClientServiceDto.getCaseInfoDto().getCdPersonAddrState());
            bookmarkVictimList.add(bkCaseVctState);

            BookmarkDto bkCaseVctAddrZip = createBookmark(BookmarkConstants.VICTIM_ADDRESS_ZIP,
                    apslettertoClientServiceDto.getCaseInfoDto().getAddrPersonAddrZip());
            bookmarkVictimList.add(bkCaseVctAddrZip);

            if (null != apslettertoClientServiceDto.getEmployeePersPhNameDto().getCdNameSuffix()) {
                BookmarkDto bkCaseWorkerSuffix = createBookmarkWithCodesTable(BookmarkConstants.VICTIM_ADDR_NAME_SUFFIX,
                        apslettertoClientServiceDto.getCaseInfoDto().getCdNameSuffix(), CodesConstant.CSUFFIX);
                bookmarkVictimList.add(bkCaseWorkerSuffix);

                FormDataGroupDto tempComma2FrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_2,
                        FormConstants.EMPTY_STRING);
                FormDataGroupDto tempComma3FrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_3,
                        FormConstants.EMPTY_STRING);

                formDataGroupList.add(tempComma2FrmDataGrpDto);
                formDataGroupList.add(tempComma3FrmDataGrpDto);
            }
            headerVictimGroupDto.setBookmarkDtoList(bookmarkVictimList);
            formDataGroupList.add(headerVictimGroupDto);

        }
    }

    /**
     *  method to populate Worker prefill data
     * @param apslettertoClientServiceDto
     * @param formDataGroupList
     * @param bookmarkDtoDefaultDtoList
     */

    private void populateWorkerData(ApslettertoClientServiceDto apslettertoClientServiceDto, List<FormDataGroupDto> formDataGroupList, List<BookmarkDto> bookmarkDtoDefaultDtoList) {
        if (null != apslettertoClientServiceDto.getEmployeePersPhNameDto()) {

            BookmarkDto bkCaseWorkerJDesc = createBookmark(BookmarkConstants.WORKER_TITLE,
                    apslettertoClientServiceDto.getEmployeePersPhNameDto().getTxtJobDesc());
            bookmarkDtoDefaultDtoList.add(bkCaseWorkerJDesc);

            BookmarkDto bkCaseWorkerFName = createBookmark(BookmarkConstants.WORKER_NAME_FIRST,
                    apslettertoClientServiceDto.getEmployeePersPhNameDto().getNmNameFirst());
            bookmarkDtoDefaultDtoList.add(bkCaseWorkerFName);

            BookmarkDto bkCaseWorkerLName = createBookmark(BookmarkConstants.WORKER_NAME_LAST,
                    apslettertoClientServiceDto.getEmployeePersPhNameDto().getNmNameLast());
            bookmarkDtoDefaultDtoList.add(bkCaseWorkerLName);

            BookmarkDto bkCaseWorkerMiddle = createBookmark(BookmarkConstants.WORKER_NAME_MIDDLE,
                    apslettertoClientServiceDto.getEmployeePersPhNameDto().getNmNameMiddle());
            bookmarkDtoDefaultDtoList.add(bkCaseWorkerMiddle);

            BookmarkDto bkCaseWorkerAddrLnOne = createBookmark(BookmarkConstants.WORKER_ADDR_LN1,
                    apslettertoClientServiceDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
            bookmarkDtoDefaultDtoList.add(bkCaseWorkerAddrLnOne);

            BookmarkDto bkCaseWorkerAddrLnTwo = createBookmark(BookmarkConstants.WORKER_ADDR_LN2,
                    apslettertoClientServiceDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());
            bookmarkDtoDefaultDtoList.add(bkCaseWorkerAddrLnTwo);

            BookmarkDto bkCaseWorkerCity = createBookmark(BookmarkConstants.WORKER_ADDR_CITY,
                    apslettertoClientServiceDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
            bookmarkDtoDefaultDtoList.add(bkCaseWorkerCity);

            BookmarkDto bkCaseWorkerAddrZip = createBookmark(BookmarkConstants.WORKER_ADDR_ZIP,
                    apslettertoClientServiceDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
            bookmarkDtoDefaultDtoList.add(bkCaseWorkerAddrZip);

            BookmarkDto bkCaseWorkerPhone = createBookmark(BookmarkConstants.WORKER_PHONE,
                    TypeConvUtil.formatPhone(apslettertoClientServiceDto.getEmployeePersPhNameDto().getNbrPhone()));
            bookmarkDtoDefaultDtoList.add(bkCaseWorkerPhone);


            if (null != apslettertoClientServiceDto.getEmployeePersPhNameDto().getNbrPhoneExtension()) {
                BookmarkDto bkCaseWorkerExtn = createBookmark(BookmarkConstants.WORKER_EXT,
                        apslettertoClientServiceDto.getEmployeePersPhNameDto().getNbrPhoneExtension());

                bookmarkDtoDefaultDtoList.add(bkCaseWorkerExtn);
            }

            if (null != apslettertoClientServiceDto.getEmployeePersPhNameDto().getCdNameSuffix()) {
                BookmarkDto bkCaseWorkerSuffix = createBookmarkWithCodesTable(BookmarkConstants.WORKER_SUFFIX,
                        apslettertoClientServiceDto.getEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX);
                bookmarkDtoDefaultDtoList.add(bkCaseWorkerSuffix);

                FormDataGroupDto tempComma0FrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_3,
                        FormConstants.EMPTY_STRING);

                formDataGroupList.add(tempComma0FrmDataGrpDto);

            }

        }
    }

    /**
     * method to populate System Date prefill data
     * @param apslettertoClientServiceDto
     * @param bookmarkDtoDefaultDtoList
     */
    private void populateSystemDate(ApslettertoClientServiceDto apslettertoClientServiceDto, List<BookmarkDto> bookmarkDtoDefaultDtoList) {
        if (null != apslettertoClientServiceDto.getGenCaseInfoDto()) {
           BookmarkDto bookmarkSysDtGenericSysdate = createBookmark(BookmarkConstants.SYSTEM_DATE,
                   DateUtils.stringDt(apslettertoClientServiceDto.getGenCaseInfoDto().getDtSysDtGenericSysdate()));
           bookmarkDtoDefaultDtoList.add(bookmarkSysDtGenericSysdate);
       }
    }

    /**
     * method to populate header Prefill data
     * @param apslettertoClientServiceDto
     * @param formDataGroupList
     */
    private void populateHeaderData(ApslettertoClientServiceDto apslettertoClientServiceDto, List<FormDataGroupDto> formDataGroupList) {
        if (!ObjectUtils.isEmpty(apslettertoClientServiceDto.getCodesTablesDto())) {
            // parent group cfzz0601
            if (FormConstants.SYSCODE.equals(apslettertoClientServiceDto.getCodesTablesDto().getaCode())) {
                FormDataGroupDto headerDirectorGroupDto = createFormDataGroup(
                        FormGroupsConstants.TMPLAT_HEADER_DIRECTOR, FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkHeaderDirectorList = new ArrayList<BookmarkDto>();

                BookmarkDto bookmarkDirectorTitle = createBookmark(BookmarkConstants.HEADER_DIRECTOR_TITLE,
                        apslettertoClientServiceDto.getCodesTablesDto().getaDecode());
                bookmarkHeaderDirectorList.add(bookmarkDirectorTitle);
                BookmarkDto bookmarkDirectorName = createBookmark(BookmarkConstants.HEADER_DIRECTOR_NAME,
                        apslettertoClientServiceDto.getCodesTablesDto().getbDecode());
                bookmarkHeaderDirectorList.add(bookmarkDirectorName);

                headerDirectorGroupDto.setBookmarkDtoList(bookmarkHeaderDirectorList);
                formDataGroupList.add(headerDirectorGroupDto);
            }

            // parent group cfzz0501
            else if (FormConstants.SYSCODE.compareTo(apslettertoClientServiceDto.getCodesTablesDto().getaCode()) < 0) {
                FormDataGroupDto headerBoardGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HEADER_BOARD,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkHeaderBoardList = new ArrayList<BookmarkDto>();

                BookmarkDto bookmarkHeaderBoardCity = createBookmark(BookmarkConstants.HEADER_BOARD_CITY,
                        apslettertoClientServiceDto.getCodesTablesDto().getaDecode());
                bookmarkHeaderBoardList.add(bookmarkHeaderBoardCity);
                BookmarkDto bookmarkHeaderBoardName = createBookmark(BookmarkConstants.HEADER_BOARD_NAME,
                        apslettertoClientServiceDto.getCodesTablesDto().getbDecode());
                bookmarkHeaderBoardList.add(bookmarkHeaderBoardName);

                headerBoardGroupDto.setBookmarkDtoList(bookmarkHeaderBoardList);
                formDataGroupList.add(headerBoardGroupDto);
            }
        }
    }
}
