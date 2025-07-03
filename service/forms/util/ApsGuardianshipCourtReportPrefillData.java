package us.tx.state.dfps.service.forms.util;

import org.springframework.stereotype.Repository;
import us.tx.state.dfps.service.admin.dto.AddrPersonLinkPhoneOutDto;
import us.tx.state.dfps.service.apsCourtReportGuardianship.dto.ApsCourtReportGuardianshipDataDto;
import us.tx.state.dfps.service.common.*;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.workload.dto.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps Guardianship Court Report Service -- aps court report for guardianship CIV22O00.
 * Dec 28, 2021- 1:52:46 PM © 2021 Texas Department of Family and
 * Protective Services
 */
@Repository
public class ApsGuardianshipCourtReportPrefillData extends DocumentServiceUtil {

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

        ApsCourtReportGuardianshipDataDto apsCourtReportGuardianshipDataDto = (ApsCourtReportGuardianshipDataDto) parentDtoobj;

        /**
         * Description: Populating the non form group data into prefill data
         * GroupName: None BookMark: Condition: None
         */
        List<FormDataGroupDto> formDataGroupList = new ArrayList<>();

        List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<>();

        // CSEC35D - worker details

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();

        BookmarkDto systemDate = createBookmark(BookmarkConstants.SYSTEM_DATE,
                formatter.format(date));
        bookmarkNonFrmGrpList.add(systemDate);

        if (null != apsCourtReportGuardianshipDataDto.getEmployeePersPhNameDto().getNmNameFirst()) {
            BookmarkDto bkWorkerNameFirst = createBookmark(BookmarkConstants.WORKER_NAME_FIRST,
                    apsCourtReportGuardianshipDataDto.getEmployeePersPhNameDto().getNmNameFirst());
            bookmarkNonFrmGrpList.add(bkWorkerNameFirst);
        }

        if (null != apsCourtReportGuardianshipDataDto.getEmployeePersPhNameDto().getNmNameMiddle()) {
            BookmarkDto bkWorkerNameMiddle = createBookmark(BookmarkConstants.WORKER_NAME_MIDDLE,
                    apsCourtReportGuardianshipDataDto.getEmployeePersPhNameDto().getNmNameMiddle());
            bookmarkNonFrmGrpList.add(bkWorkerNameMiddle);
        }

        if (null != apsCourtReportGuardianshipDataDto.getEmployeePersPhNameDto().getNmNameLast()) {
            BookmarkDto bkWorkerNameLast = createBookmark(BookmarkConstants.WORKER_NAME_LAST,
                    apsCourtReportGuardianshipDataDto.getEmployeePersPhNameDto().getNmNameLast());
            bookmarkNonFrmGrpList.add(bkWorkerNameLast);
        }

        if (null != apsCourtReportGuardianshipDataDto.getEmployeePersPhNameDto().getCdNameSuffix()) {
            BookmarkDto bkworkerNameSuffix = createBookmark(BookmarkConstants.WORKER_NAME_SUFFIX,
                    apsCourtReportGuardianshipDataDto.getEmployeePersPhNameDto().getCdNameSuffix());
            bookmarkNonFrmGrpList.add(bkworkerNameSuffix);
        }

        if (null != apsCourtReportGuardianshipDataDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1()) {
            BookmarkDto bkworkerAddressLn1 = createBookmark(BookmarkConstants.WORKER_ADDR_LN1,
                    apsCourtReportGuardianshipDataDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
            bookmarkNonFrmGrpList.add(bkworkerAddressLn1);
        }

        if (null != apsCourtReportGuardianshipDataDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2()) {
            BookmarkDto bkworkerAddressLn2 = createBookmark(BookmarkConstants.WORKER_ADDR_LN2,
                    apsCourtReportGuardianshipDataDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());
            bookmarkNonFrmGrpList.add(bkworkerAddressLn2);
        }

        if (null != apsCourtReportGuardianshipDataDto.getEmployeePersPhNameDto().getAddrMailCodeCity()) {
            BookmarkDto bkworkerAddressCity = createBookmark(BookmarkConstants.WORKER_ADDR_CITY,
                    apsCourtReportGuardianshipDataDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
            bookmarkNonFrmGrpList.add(bkworkerAddressCity);
        }

        if (null != apsCourtReportGuardianshipDataDto.getEmployeePersPhNameDto().getAddrMailCodeZip()) {
            BookmarkDto bkworkerAddressZip = createBookmark(BookmarkConstants.WORKER_ADDR_ZIP,
                    apsCourtReportGuardianshipDataDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
            bookmarkNonFrmGrpList.add(bkworkerAddressZip);
        }

        if (null != apsCourtReportGuardianshipDataDto.getEmployeePersPhNameDto().getNbrPhone()) {
            BookmarkDto bkworkerPhnNumber = createBookmark(BookmarkConstants.WORKER_PHONE,
                    TypeConvUtil.formatPhone(apsCourtReportGuardianshipDataDto.getEmployeePersPhNameDto().getNbrPhone()));
            bookmarkNonFrmGrpList.add(bkworkerPhnNumber);
        }

        if (null != apsCourtReportGuardianshipDataDto.getEmployeePersPhNameDto().getNbrPhoneExtension()) {
            FormDataGroupDto wrkrExtGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_WORKER_EXTENSION,
                    FormConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkwrkrExtList = new ArrayList<>();
            bookmarkwrkrExtList.add(createBookmark(BookmarkConstants.WORKER_EXTENSION,
                    apsCourtReportGuardianshipDataDto.getEmployeePersPhNameDto().getNbrPhoneExtension()));
            wrkrExtGroupDto.setBookmarkDtoList(bookmarkwrkrExtList);
            formDataGroupList.add(wrkrExtGroupDto);
        }

        // CLSC03D - Header Director Details
        FormDataGroupDto wardIdentifierGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_HEADER_DIRECTOR, FormConstants.EMPTY_STRING);
        List<BookmarkDto> bookmarkWardIdentifierList = new ArrayList<>();

        bookmarkWardIdentifierList.add(createBookmark(BookmarkConstants.HEADER_DIRECTOR_TITLE,
                apsCourtReportGuardianshipDataDto.getCodesTablesDto().getaDecode()));
        bookmarkWardIdentifierList.add(createBookmark(BookmarkConstants.HEADER_DIRECTOR_NAME,
                apsCourtReportGuardianshipDataDto.getCodesTablesDto().getbDecode()));

        wardIdentifierGroup.setBookmarkDtoList(bookmarkWardIdentifierList);
        formDataGroupList.add(wardIdentifierGroup);

        // CSEC35D - ward name details
        if (null != apsCourtReportGuardianshipDataDto.getNameDetailDto().getNmNameFirst()) {
            BookmarkDto bkWardFirstName = createBookmark(BookmarkConstants.WARD_NAME_FIRST,
                    apsCourtReportGuardianshipDataDto.getNameDetailDto().getNmNameFirst());
            bookmarkNonFrmGrpList.add(bkWardFirstName);
        }

        if (null != apsCourtReportGuardianshipDataDto.getNameDetailDto().getNmNameMiddle()) {
            BookmarkDto bkWardMiddleName = createBookmark(BookmarkConstants.WARD_NAME_MIDDLE,
                    apsCourtReportGuardianshipDataDto.getNameDetailDto().getNmNameMiddle());
            bookmarkNonFrmGrpList.add(bkWardMiddleName);
        }

        if (null != apsCourtReportGuardianshipDataDto.getNameDetailDto().getNmNameLast()) {
            BookmarkDto bkWardLastName = createBookmark(BookmarkConstants.WARD_NAME_LAST,
                    apsCourtReportGuardianshipDataDto.getNameDetailDto().getNmNameLast());
            bookmarkNonFrmGrpList.add(bkWardLastName);
        }

        if (null != apsCourtReportGuardianshipDataDto.getNameDetailDto().getCdNameSuffix()) {
            BookmarkDto bkWardSuffixName = createBookmarkWithCodesTable(BookmarkConstants.WARD_NAME_SUFFIX,
                    apsCourtReportGuardianshipDataDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
            bookmarkNonFrmGrpList.add(bkWardSuffixName);
        }
        if (null != apsCourtReportGuardianshipDataDto.getNameDetailDto().getCdNameSuffix()) {
            FormDataGroupDto wardSuffixGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_WARD,
                    FormConstants.EMPTY_STRING);
            formDataGroupList.add(wardSuffixGroupDto);
        }

        // CINV46D - ward address details
        if (null != apsCourtReportGuardianshipDataDto.getPhyAddr()) {
            setWardData(apsCourtReportGuardianshipDataDto, formDataGroupList, bookmarkNonFrmGrpList);
        }

        // CLSC41D - guardian details
        if (null != apsCourtReportGuardianshipDataDto.getGuardianDetailsDtoList()) {
            setGuardianData(apsCourtReportGuardianshipDataDto, formDataGroupList);
        }

        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
        preFillData.setFormDataGroupList(formDataGroupList);
        return preFillData;
    }

    private void setGuardianData(ApsCourtReportGuardianshipDataDto apsCourtReportGuardianshipDataDto, List<FormDataGroupDto> formDataGroupList) {
        for (GuardianDetailsDto guardianDetailsDto : apsCourtReportGuardianshipDataDto.getGuardianDetailsDtoList()) {
            List<FormDataGroupDto> guardianFormDataGroupList = new ArrayList<>();

            FormDataGroupDto guardianGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_GUARDIAN,
                    FormConstants.EMPTY_STRING);

            FormDataGroupDto guardianNameGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_GUARDIAN_NAME,
                    FormConstants.EMPTY_STRING);

            List<BookmarkDto> bookmarkGuardianName = new ArrayList<>();

//            if (null != guardianDetailsDto.getFirstName()) {
//                BookmarkDto guardianFName = createBookmark(BookmarkConstants.GUARDIAN_NAME_FIRST,
//                        guardianDetailsDto.getFirstName());
//                bookmarkGuardianName.add(guardianFName);
//            }
//            if (null != guardianDetailsDto.getLastName()) {
//                BookmarkDto guardianLName = createBookmark(BookmarkConstants.GUARDIAN_NAME_LAST,
//                        guardianDetailsDto.getLastName());
//                bookmarkGuardianName.add(guardianLName);
//            }
//            if (null != guardianDetailsDto.getMiddleName()) {
//                BookmarkDto guardianMName = createBookmark(BookmarkConstants.GUARDIAN_NAME_MIDDLE,
//                        guardianDetailsDto.getMiddleName());
//                bookmarkGuardianName.add(guardianMName);
//            }
//
//            if (null != guardianDetailsDto.getNameSuffix()) {
//                BookmarkDto guardianSName = createBookmark(BookmarkConstants.GUARDIAN_NAME_SUFFIX,
//                        guardianDetailsDto.getNameSuffix());
//                bookmarkGuardianName.add(guardianSName);
//            }

            if (null != guardianDetailsDto.getGuardFullName()) {
                BookmarkDto guardianFName = createBookmark(BookmarkConstants.GUARDIAN_FULL_NAME,
                        guardianDetailsDto.getGuardFullName());
                bookmarkGuardianName.add(guardianFName);
            }

            guardianNameGroupDto.setBookmarkDtoList(bookmarkGuardianName);
            guardianFormDataGroupList.add(guardianNameGroupDto);

            if (null != guardianDetailsDto.getAddrPersAddrStLn1()) {
                FormDataGroupDto guardianAddressGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_GUARDIAN_ADDRESS,
                        FormConstants.EMPTY_STRING);

                List<BookmarkDto> bookmarkGuardianAddr = new ArrayList<>();

                if (null != guardianDetailsDto.getAddrPersAddrStLn1()) {
                    BookmarkDto guardianAddrLn1 = createBookmark(BookmarkConstants.GUARDIAN_ADDR_LN1,
                            guardianDetailsDto.getAddrPersAddrStLn1());
                    bookmarkGuardianAddr.add(guardianAddrLn1);
                }

                if (null != guardianDetailsDto.getAddrPersAddrStLn2()) {
                    BookmarkDto guardianAddrLn2 = createBookmark(BookmarkConstants.GUARDIAN_ADDR_LN2,
                            guardianDetailsDto.getAddrPersAddrStLn2());
                    bookmarkGuardianAddr.add(guardianAddrLn2);
                }

                if (null != guardianDetailsDto.getAddrPersonAddrCity()) {
                    BookmarkDto guardianAddrCity = createBookmark(BookmarkConstants.GUARDIAN_ADDR_CITY,
                            guardianDetailsDto.getAddrPersonAddrCity());
                    bookmarkGuardianAddr.add(guardianAddrCity);
                }

                if (null != guardianDetailsDto.getCdPersonAddrState()) {
                    BookmarkDto guardianAddrSt = createBookmark(BookmarkConstants.GUARDIAN_ADDR_STATE,
                            guardianDetailsDto.getCdPersonAddrState());
                    bookmarkGuardianAddr.add(guardianAddrSt);
                }
                if (null != guardianDetailsDto.getAddrPersonAddrZip()) {
                    BookmarkDto guardianAddrZip = createBookmark(BookmarkConstants.GUARDIAN_ADDR_ZIP,
                            guardianDetailsDto.getAddrPersonAddrZip());
                    bookmarkGuardianAddr.add(guardianAddrZip);
                }

                guardianAddressGroupDto.setBookmarkDtoList(bookmarkGuardianAddr);
                guardianFormDataGroupList.add(guardianAddressGroupDto);
            }

            FormDataGroupDto guardianPhoneGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_GUARDIAN_PHONE,
                    FormConstants.EMPTY_STRING);

            List<BookmarkDto> bookmarkGuardianPhn = new ArrayList<>();

            if (null != guardianDetailsDto.getNbrPersonPhone()) {
                BookmarkDto guardianPhn = createBookmark(BookmarkConstants.GUARDIAN_PHONE,
                        TypeConvUtil.formatPhone(guardianDetailsDto.getNbrPersonPhone()));
                bookmarkGuardianPhn.add(guardianPhn);
            }
            guardianPhoneGroupDto.setBookmarkDtoList(bookmarkGuardianPhn);
            guardianFormDataGroupList.add(guardianPhoneGroupDto);


            if (null != guardianDetailsDto.getNbrPersonPhoneExtension()) {
                List<FormDataGroupDto> extGroupDTOList = new ArrayList<>();

                FormDataGroupDto guardianExtensionGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_GUARDIAN_EXTENSION,
                        FormConstants.EMPTY_STRING);

                List<BookmarkDto> bookmarkGuardianExtension = new ArrayList<>();
                BookmarkDto guardianExtension = createBookmark(BookmarkConstants.GUARDIAN_EXTENSION,
                        guardianDetailsDto.getNbrPersonPhoneExtension());
                bookmarkGuardianExtension.add(guardianExtension);
                guardianExtensionGroupDto.setBookmarkDtoList(bookmarkGuardianExtension);
                extGroupDTOList.add(guardianExtensionGroupDto);
                guardianPhoneGroupDto.setFormDataGroupList(extGroupDTOList);
            }

            guardianGroupDto.setFormDataGroupList(guardianFormDataGroupList);
            formDataGroupList.add(guardianGroupDto);
        }
    }

    private void setWardData(ApsCourtReportGuardianshipDataDto apsCourtReportGuardianshipDataDto, List<FormDataGroupDto> formDataGroupList, List<BookmarkDto> bookmarkNonFrmGrpList) {
        for (AddrPersonLinkPhoneOutDto addrPersonLinkPhoneOutDto : apsCourtReportGuardianshipDataDto.getPhyAddr()) {

            if (null != addrPersonLinkPhoneOutDto.getAddrPersAddrStLn1()) {
                BookmarkDto bkWardAddrLn1 = createBookmark(BookmarkConstants.WARD_ADDR_LN1,
                        addrPersonLinkPhoneOutDto.getAddrPersAddrStLn1());
                bookmarkNonFrmGrpList.add(bkWardAddrLn1);
            }
            if (null != addrPersonLinkPhoneOutDto.getAddrPersAddrStLn2()) {
                BookmarkDto bkWardAddrLn2 = createBookmark(BookmarkConstants.WARD_ADDR_LN2,
                        addrPersonLinkPhoneOutDto.getAddrPersAddrStLn2());
                bookmarkNonFrmGrpList.add(bkWardAddrLn2);
            }

            if (null != addrPersonLinkPhoneOutDto.getAddrCity()) {
                BookmarkDto bkWardAddrCity = createBookmark(BookmarkConstants.WARD_ADDR_CITY,
                        addrPersonLinkPhoneOutDto.getAddrCity());
                bookmarkNonFrmGrpList.add(bkWardAddrCity);
            }

            if (null != addrPersonLinkPhoneOutDto.getCdAddrState()) {
                BookmarkDto bkWardAddrCity = createBookmark(BookmarkConstants.WARD_ADDR_STATE,
                        addrPersonLinkPhoneOutDto.getCdAddrState());
                bookmarkNonFrmGrpList.add(bkWardAddrCity);
            }
            if (null != addrPersonLinkPhoneOutDto.getAddrZip()) {
                BookmarkDto bkWardAddrZip = createBookmark(BookmarkConstants.WARD_ADDR_ZIP,
                        addrPersonLinkPhoneOutDto.getAddrZip());
                bookmarkNonFrmGrpList.add(bkWardAddrZip);
            }

            if (null != addrPersonLinkPhoneOutDto.getPhone()) {
                BookmarkDto bkWardPhone = createBookmark(BookmarkConstants.WARD_PHONE,
                        TypeConvUtil.formatPhone(addrPersonLinkPhoneOutDto.getPhone()));
                bookmarkNonFrmGrpList.add(bkWardPhone);
            }

            if (null != addrPersonLinkPhoneOutDto.getPhoneExtension()) {
                FormDataGroupDto wardPhnExtGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_WARD_EXTENSION,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkwardPhnExtList = new ArrayList<>();
                BookmarkDto bkWardPhnExt = createBookmark(BookmarkConstants.WARD_EXTENSION,
                        addrPersonLinkPhoneOutDto.getPhoneExtension());
                bookmarkwardPhnExtList.add(bkWardPhnExt);
                wardPhnExtGroupDto.setBookmarkDtoList(bookmarkwardPhnExtList);
                formDataGroupList.add(wardPhnExtGroupDto);
            }
        }
    }
}