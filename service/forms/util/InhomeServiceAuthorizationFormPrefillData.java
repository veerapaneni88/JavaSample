package us.tx.state.dfps.service.forms.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dto.AddrPersonLinkPhoneOutDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.inhomeserviceAuth.dto.InhomeServiceAuthFormDataDto;
import us.tx.state.dfps.service.workload.dto.SVCAuthDetailRecDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service
 * Authorization form used by CPS to refer clients for paid services under PRS
 * contracts. Nov 30, 2021- 1:52:54 PM © 2021 Texas Department of Family and
 * Protective Services
 */
@Repository
public class InhomeServiceAuthorizationFormPrefillData extends DocumentServiceUtil {

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

        InhomeServiceAuthFormDataDto serviceAuthFormDataDto = (InhomeServiceAuthFormDataDto) parentDtoobj;
        String stageProgram = serviceAuthFormDataDto.getGenericCaseInfoDto().getCdStageProgram();
        String stage = serviceAuthFormDataDto.getGenericCaseInfoDto().getCdStage();
        String stageType = serviceAuthFormDataDto.getGenericCaseInfoDto().getCdStageType();

        /**
         * Description: Populating the non form group data into prefill data
         * GroupName: None BookMark: Condition: None
         */
        List<BookmarkDto> bookmarkDtoDefaultDtoList = new ArrayList<>();

        if (null != serviceAuthFormDataDto.getGenericCaseInfoDto().getDtSysDtGenericSysdate()) {
            BookmarkDto bkCasePrintDate = createBookmark(BookmarkConstants.PRINT_DATE,
                    DateUtils.stringDt(serviceAuthFormDataDto.getGenericCaseInfoDto().getDtSysDtGenericSysdate()));
            bookmarkDtoDefaultDtoList.add(bkCasePrintDate);
        }

        if (null != serviceAuthFormDataDto.getGenericCaseInfoDto().getNmCase()) {
            BookmarkDto bkCaseName = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
                    serviceAuthFormDataDto.getGenericCaseInfoDto().getNmCase());
            bookmarkDtoDefaultDtoList.add(bkCaseName);
        } else {
            BookmarkDto bkCaseName = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER, "");
            bookmarkDtoDefaultDtoList.add(bkCaseName);
        }

        if (null != serviceAuthFormDataDto.getGenericCaseInfoDto().getIdCase()) {
            BookmarkDto bkCaseNum = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
                    serviceAuthFormDataDto.getGenericCaseInfoDto().getIdCase());
            bookmarkDtoDefaultDtoList.add(bkCaseNum);
        } else {
            BookmarkDto bkCaseNum = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER, "");
            bookmarkDtoDefaultDtoList.add(bkCaseNum);
        }


        BookmarkDto bkTmplatContractId = createBookmark(BookmarkConstants.CONTRACT_ID,
                serviceAuthFormDataDto.getServiceAuthorizationDto().getIdContract());
        BookmarkDto bkTmplatContractNumber = createBookmark(BookmarkConstants.CONTRACT_NBR,
                serviceAuthFormDataDto.getTxScorContractNumber());
        bookmarkDtoDefaultDtoList.add(bkTmplatContractId);
        bookmarkDtoDefaultDtoList.add(bkTmplatContractNumber);


        /**
         * Description: Worker Details
         */
        List<FormDataGroupDto> fdParentGroupList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(serviceAuthFormDataDto.getResourceDto())) {

            BookmarkDto bkCaseWorkerPhone = createBookmark(BookmarkConstants.WRK_PHONE,
                    TypeConvUtil.formatPhone(serviceAuthFormDataDto.getEmployeePersPhNameDto().getNbrPhone()));
            bookmarkDtoDefaultDtoList.add(bkCaseWorkerPhone);

            BookmarkDto bkCaseWorkerCity = createBookmark(BookmarkConstants.WRK_CITY,
                    serviceAuthFormDataDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
            bookmarkDtoDefaultDtoList.add(bkCaseWorkerCity);

            BookmarkDto bkCaseWorkerAddrLnOne = createBookmark(BookmarkConstants.WRK_ADDR1,
                    serviceAuthFormDataDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
            bookmarkDtoDefaultDtoList.add(bkCaseWorkerAddrLnOne);

            if (null != serviceAuthFormDataDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2()) {
                BookmarkDto bkCaseWorkerAddrLnTwo = createBookmark(BookmarkConstants.WRK_ADDR2,
                        serviceAuthFormDataDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());
                bookmarkDtoDefaultDtoList.add(bkCaseWorkerAddrLnTwo);
            }

            BookmarkDto bkCaseWorkerAddrZip = createBookmark(BookmarkConstants.WRK_ZIP,
                    serviceAuthFormDataDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
            bookmarkDtoDefaultDtoList.add(bkCaseWorkerAddrZip);

            BookmarkDto bkCaseWorkerBjn = createBookmark(BookmarkConstants.BJN,
                    serviceAuthFormDataDto.getEmployeePersPhNameDto().getBjnJob());
            bookmarkDtoDefaultDtoList.add(bkCaseWorkerBjn);

            BookmarkDto bkCaseWorkerFName = createBookmark(BookmarkConstants.WRKER_FIRST_NAME,
                    serviceAuthFormDataDto.getEmployeePersPhNameDto().getNmNameFirst());
            bookmarkDtoDefaultDtoList.add(bkCaseWorkerFName);

            BookmarkDto bkCaseWorkerLName = createBookmark(BookmarkConstants.WRKER_LAST_NAME,
                    serviceAuthFormDataDto.getEmployeePersPhNameDto().getNmNameLast());
            bookmarkDtoDefaultDtoList.add(bkCaseWorkerLName);

            BookmarkDto bkCaseWorkerMiddle = createBookmark(BookmarkConstants.WRKER_MIDDLE_NAME,
                    serviceAuthFormDataDto.getEmployeePersPhNameDto().getNmNameMiddle());
            bookmarkDtoDefaultDtoList.add(bkCaseWorkerMiddle);

            if (null != serviceAuthFormDataDto.getEmployeePersPhNameDto().getNbrPhoneExtension()) {
                BookmarkDto bkCaseWorkerExtn = createBookmark(BookmarkConstants.WRK_EXT,
                        TypeConvUtil.formatPhoneWithExtn(serviceAuthFormDataDto.getEmployeePersPhNameDto().getNbrPhoneExtension()));

                bookmarkDtoDefaultDtoList.add(bkCaseWorkerExtn);
            }

            if (null != serviceAuthFormDataDto.getEmployeePersPhNameDto().getCdNameSuffix()) {
                BookmarkDto bkCaseWorkerSuffix = createBookmarkWithCodesTable(BookmarkConstants.WRKER_SUFFIX_NAME,
                        serviceAuthFormDataDto.getEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
                bookmarkDtoDefaultDtoList.add(bkCaseWorkerSuffix);

            }
        }

        /**
         * Description: TMPLAT_CONTRACT_PROVIDER
         */
        if (!ObjectUtils.isEmpty(serviceAuthFormDataDto.getResourceDto())) {
            FormDataGroupDto fdSbTmplatContractProvider = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTRACT_PROVIDER,
                    FormGroupsConstants.TMPLAT_CONTRACT);

            List<BookmarkDto> bkSbTmplatContractProviderList = new ArrayList<>();

            BookmarkDto bkTmplatContractProviderZip = createBookmark(BookmarkConstants.PROV_ZIP,
                    serviceAuthFormDataDto.getResourceDto().getAddrRsrcZip());
            bookmarkDtoDefaultDtoList.add(bkTmplatContractProviderZip);

            BookmarkDto bkTmplatContractProviderCity = createBookmark(BookmarkConstants.PROV_CITY,
                    serviceAuthFormDataDto.getResourceDto().getAddrRsrcCity());
            bookmarkDtoDefaultDtoList.add(bkTmplatContractProviderCity);

            BookmarkDto bkTmplatContractProviderAddrOne = createBookmark(BookmarkConstants.PROV_ADDR1,
                    serviceAuthFormDataDto.getResourceDto().getAddrRsrcStLn1());
            bookmarkDtoDefaultDtoList.add(bkTmplatContractProviderAddrOne);

            if (null != serviceAuthFormDataDto.getResourceDto().getAddrRsrcStLn2()) {
                BookmarkDto bkTmplatContractProviderAddrTwo = createBookmark(BookmarkConstants.PROV_ADDR2,
                        serviceAuthFormDataDto.getResourceDto().getAddrRsrcStLn2());
                bookmarkDtoDefaultDtoList.add(bkTmplatContractProviderAddrTwo);
            }

            BookmarkDto bkTmplatContractProviderState = createBookmark(BookmarkConstants.PROV_STATE,
                    serviceAuthFormDataDto.getResourceDto().getCdRsrcState());
            bookmarkDtoDefaultDtoList.add(bkTmplatContractProviderState);

            BookmarkDto bkTmplatContractProviderName = createBookmark(BookmarkConstants.PROV_NAME,
                    serviceAuthFormDataDto.getResourceDto().getNmResource());
            bookmarkDtoDefaultDtoList.add(bkTmplatContractProviderName);

            BookmarkDto bkTmplatSectionAPref = createBookmark(BookmarkConstants.PREF_SUBCONT,
                    serviceAuthFormDataDto.getServiceAuthorizationDto().getSvcAuthSecProvdr());
            bookmarkDtoDefaultDtoList.add(bkTmplatSectionAPref);

            fdSbTmplatContractProvider.setBookmarkDtoList(bkSbTmplatContractProviderList);

            /**
             * Description: TMPLAT_CONTRACT_AUTH Situation field has SvcAuth
             * ParentGroup:ccn01o03 GroupName: ccn01o04 SubGroups: BookMark:
             * TMPLAT_CONTRACT_AUTH Condition: None
             */
            // Modified the code to check the condition to display the service auth
            // Id in form for warranty defect 12001
            if (!ObjectUtils.isEmpty(serviceAuthFormDataDto.getSvcAuthDetailDtoList())) {
                FormDataGroupDto fdSbTmplatContractAuth = createFormDataGroup(
                        FormGroupsConstants.TMPLAT_CONTRACT_AUTH, FormGroupsConstants.TMPLAT_CONTRACT);

                List<BookmarkDto> bkSbTmplatContractAuthList = new ArrayList<>();
                BookmarkDto bkTmplatContractAuth = createBookmark(BookmarkConstants.CONTRACT_AUTH_ID,
                        serviceAuthFormDataDto.getGenericCaseInfoDto().getIdSituation());
                bkSbTmplatContractAuthList.add(bkTmplatContractAuth);
                fdSbTmplatContractAuth.setBookmarkDtoList(bkSbTmplatContractAuthList);

            }

            // for List of Services

            if (!ObjectUtils.isEmpty(serviceAuthFormDataDto.getSVCDetailRec())) {
                BigDecimal nbrUnits = null;
                BigDecimal rate = BigDecimal.ZERO;

                serviceAuthFormDataDto.getSVCDetailRec().sort(Comparator.comparing(SVCAuthDetailRecDto::getIdSvcAuthDtl));

                for (SVCAuthDetailRecDto svcAuthDtl : serviceAuthFormDataDto.getSVCDetailRec()) {
                    FormDataGroupDto fdSbTmplatSectionAService = createFormDataGroup(FormGroupsConstants.TMPLAT_SERVICE_LIST, FormConstants.EMPTY_STRING);
                    List<BookmarkDto> bkSbTmplatSectionAServiceList = new ArrayList<>();

                    if (null != svcAuthDtl.getDtSvcAuthDtlBegin()) {
                        BookmarkDto bkTmplatSectionAServiceBegin = createBookmark(
                                BookmarkConstants.BEG_DT,
                                DateUtils.stringDt(svcAuthDtl.getDtSvcAuthDtlBegin()));
                        bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceBegin);
                    }

                    if (null != svcAuthDtl.getDtSvcAuthDtlEnd()) {
                        BookmarkDto bkTmplatSectionAServiceEnd = createBookmark(BookmarkConstants.END_DT,
                                DateUtils.stringDt(svcAuthDtl.getDtSvcAuthDtlEnd()));
                        bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceEnd);
                    }

                    if (null != svcAuthDtl.getDtSvcAuthDtlTerm()) {
                        BookmarkDto bkTmplatSectionAServiceTermDate = createBookmark(
                                BookmarkConstants.TERM_DT,
                                DateUtils.stringDt(svcAuthDtl.getDtSvcAuthDtlTerm()));
                        bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceTermDate);
                    }
                    //Modified the code to set the decimal values for Warranty defect 12030
                    if (null != svcAuthDtl.getNbrSvcAuthDtlUnitsReq()) {
                        nbrUnits = svcAuthDtl.getNbrSvcAuthDtlUnitsReq();
                        nbrUnits = nbrUnits.setScale(ServiceConstants.INT_TWO);

                    }
                    BookmarkDto bkTmplatSectionAServiceUnits = createBookmark(BookmarkConstants.UNITS,
                            nbrUnits);
                    bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceUnits);

                    if (null != svcAuthDtl.getAmtSvcAuthDtlAmtReq()) {
                        BigDecimal amount = svcAuthDtl.getAmtSvcAuthDtlAmtReq();
                        amount = amount.setScale(ServiceConstants.INT_TWO);
                        BookmarkDto bkTmplatSectionAServiceAmt = createBookmark(BookmarkConstants.AMOUNT,
                                amount);
                        bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceAmt);
                    }
                    BookmarkDto bkTmplatSectionAServiceAuthType = createBookmark(
                            BookmarkConstants.AUTH_TYPE, svcAuthDtl.getCdSvcAuthDtlAuthType());
                    bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceAuthType);

                    BookmarkDto bkTmplatSectionAServiceDecode = createBookmarkWithCodesTable(
                            BookmarkConstants.SERVICE, svcAuthDtl.getCdSvcAuthDtlSvc(),
                            CodesConstant.CSVCCODE);
                    bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceDecode);

                    BookmarkDto bkTmplatSectionAServiceSvc = createBookmark(
                            BookmarkConstants.SERVICE_CODE, svcAuthDtl.getCdSvcAuthDtlSvc());
                    bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceSvc);

                    BookmarkDto bkTmplatSectionAServiceUnitType = createBookmark(
                            BookmarkConstants.UNIT_TYP, svcAuthDtl.getCdSvcAuthDtlUnitType());
                    bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceUnitType);

                    if( ServiceConstants.CPGRMS_APS.equals(stageProgram) &&
                        (ServiceConstants.CSTAGES_INV.equals(stage)
                            || (ServiceConstants.CSTAGES_SVC.equals(stage)
                            && (ServiceConstants.CSTAGES_TYPE_ICS.equals(stageType) || ServiceConstants.CSTAGES_TYPE_MNT.equals(stageType))) ) )
                    {
                        BookmarkDto bkTmplatSectionAServiceBegin = createBookmark(
                                BookmarkConstants.RATE, rate = rate.setScale(ServiceConstants.INT_TWO));
                        bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceBegin);
                    }

                    BookmarkDto bkTmplatSectionAServiceAuthDtl = createBookmark(
                            BookmarkConstants.SVC_AUTH_DTL, svcAuthDtl.getIdSvcAuthDtl());
                    bkSbTmplatSectionAServiceList.add(bkTmplatSectionAServiceAuthDtl);

                    fdSbTmplatSectionAService.setBookmarkDtoList(bkSbTmplatSectionAServiceList);
                    fdParentGroupList.add(fdSbTmplatSectionAService);
                }
            }

        }
        /**
         * Description: TMPLAT_SECTIONB_CLIENT_SUFFIX ParentGroup: ccn01o71
         * GroupName: ccn01o72 SubGroups: BookMark:
         * TMPLAT_SECTIONB_CLIENT_SUFFIX Condition: None
         */

        if(!TypeConvUtil.isNullOrEmpty(serviceAuthFormDataDto.getNameDetailDto())) {
            FormDataGroupDto fdSbTmplatSectionBClientSuffix = createFormDataGroup(
                    FormGroupsConstants.TMPLAT_SECTIONB_CLIENT_SUFFIX, FormGroupsConstants.TMPLAT_SECTIONB_CLIENT);

            List<BookmarkDto> bkSbTmplatSectionBClientSuffixList = new ArrayList<>();

            BookmarkDto bkSbTmplatSecBclientSuffixFname = createBookmarkWithCodesTable(
                    BookmarkConstants.CLIENT_SUFFIX_NAME,
                    serviceAuthFormDataDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
            bkSbTmplatSectionBClientSuffixList.add(bkSbTmplatSecBclientSuffixFname);

            fdSbTmplatSectionBClientSuffix.setBookmarkDtoList(bkSbTmplatSectionBClientSuffixList);
        }

        BookmarkDto bkPrTmplatOrigWorkerFName = createBookmark(BookmarkConstants.ORIG_WRKR_FIRST_NAME,
                serviceAuthFormDataDto.getHistoyEmployeePersPhNameDto().getNmNameFirst());
        bookmarkDtoDefaultDtoList.add(bkPrTmplatOrigWorkerFName);

        BookmarkDto bkPrTmplatOrigWorkerLName = createBookmark(BookmarkConstants.ORIG_WRKR_LAST_NAME,
                serviceAuthFormDataDto.getHistoyEmployeePersPhNameDto().getNmNameLast());
        bookmarkDtoDefaultDtoList.add(bkPrTmplatOrigWorkerLName);

        BookmarkDto bkPrTmplatOrigWorkerMName = createBookmark(BookmarkConstants.ORIG_WRKR_MIDDLE_NAME,
                serviceAuthFormDataDto.getHistoyEmployeePersPhNameDto().getNmNameMiddle());
        bookmarkDtoDefaultDtoList.add(bkPrTmplatOrigWorkerMName);

        BookmarkDto bkPrTmplatOrigWorkerId = createBookmark(BookmarkConstants.ORIG_WORKER_PERSON_ID,
                serviceAuthFormDataDto.getHistoyEmployeePersPhNameDto().getIdPerson());
        bookmarkDtoDefaultDtoList.add(bkPrTmplatOrigWorkerId);

        if (null != serviceAuthFormDataDto.getHistoyEmployeePersPhNameDto()) {

            BookmarkDto bkSbTmplatOrigWorkerSuffix = createBookmarkWithCodesTable(
                    BookmarkConstants.ORIG_WRKR_SUFFIX_NAME,
                    serviceAuthFormDataDto.getHistoyEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
            bookmarkDtoDefaultDtoList.add(bkSbTmplatOrigWorkerSuffix);

        }


        /**
         * Description: TMPLAT_SECTIONB_SVCAUTH ParentGroup: ccn01o70
         * GroupName: ccn01o79 SubGroups: BookMark: TMPLAT_SECTIONB_SVCAUTH
         * Condition:
         */
        BookmarkDto bkTmplatSecBSvcAuth = createBookmark(BookmarkConstants.COMMENTS,
                TypeConvUtil.formatTextValue(serviceAuthFormDataDto.getServiceAuthorizationDto().getSvcAuthComments()));
        bookmarkDtoDefaultDtoList.add(bkTmplatSecBSvcAuth);


        if (null != serviceAuthFormDataDto.getClienDto().getDtPersonBirth()) {
            BookmarkDto bkSbTmplatSecBClientRelDob = createBookmark(BookmarkConstants.CLIENT_DOB,
                    TypeConvUtil.formDateFormat(serviceAuthFormDataDto.getClienDto().getDtPersonBirth()));
            bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBClientRelDob);
        }


        if (null != serviceAuthFormDataDto.getClienDto().getDtPersonBirth()) {
            BookmarkDto bkSbTmplatSecBClientLivArr = createBookmarkWithCodesTable(BookmarkConstants.CLIENT_LIV_ARR,
                    serviceAuthFormDataDto.getClienDto().getCdPersonLivArr(), CodesConstant.CLIVARR);
            bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBClientLivArr);
        }

        if (!ObjectUtils.isEmpty(serviceAuthFormDataDto.getPersonIdDtoSsn()) && !ObjectUtils.isEmpty(serviceAuthFormDataDto.getPersonIdDtoSsn().getPersonIdNumber())) {
            BookmarkDto bkSbTmplatSecBClientRelSSN = createBookmark(BookmarkConstants.CLIENT_SSN,
                    TypeConvUtil.formatSSN(serviceAuthFormDataDto.getPersonIdDtoSsn().getPersonIdNumber()));
            bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBClientRelSSN);
        }
        if (!ObjectUtils.isEmpty(serviceAuthFormDataDto.getPersonIdDtoMed())) {
            BookmarkDto bkSbTmplatSecBClientRelMedic = createBookmark(BookmarkConstants.CLIENT_MEDIC,
                    serviceAuthFormDataDto.getPersonIdDtoMed().getPersonIdNumber());
            bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBClientRelMedic);
        }
        if (!ObjectUtils.isEmpty(serviceAuthFormDataDto.getClienDto().getCdPersonSex())) {
            BookmarkDto bkSbTmplatSecBClientRelSex = createBookmarkWithCodesTable(BookmarkConstants.CLIENT_SEX,
                    serviceAuthFormDataDto.getClienDto().getCdPersonSex(), CodesConstant.CSEX);
            bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBClientRelSex);
        }
        //artf256122  - CSEC35D getting the name details for client information in FORM
        if (null != serviceAuthFormDataDto.getClienDto()) {
            BookmarkDto bkSbTmplatSecBClientRelFname = createBookmark(BookmarkConstants.CLIENT_FIRST_NAME,
                    serviceAuthFormDataDto.getClienDto().getNmNameFirst());
            bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBClientRelFname);

            BookmarkDto bkSbTmplatSecBClientRelSfxNm = createBookmarkWithCodesTable(BookmarkConstants.CLIENT_SUFFIX_NAME,
                    serviceAuthFormDataDto.getClienDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
            bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBClientRelSfxNm);

            BookmarkDto bkSbTmplatSecBClientRelLname = createBookmark(BookmarkConstants.CLIENT_LAST_NAME,
                    serviceAuthFormDataDto.getClienDto().getNmNameLast());
            bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBClientRelLname);

            BookmarkDto bkSbTmplatSecBClientRelMname = createBookmark(BookmarkConstants.CLIENT_MIDDLE_NAME,
                    serviceAuthFormDataDto.getClienDto().getNmNameMiddle());
            bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBClientRelMname);

            BookmarkDto bkSbTmplatSecBClientRelId = createBookmark(BookmarkConstants.CLIENT_PERS_ID,
                    serviceAuthFormDataDto.getServiceAuthDetailDto().getIdPerson());
            bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBClientRelId);
        }

        for (AddrPersonLinkPhoneOutDto addr : serviceAuthFormDataDto.getClntAddr()) {

            BookmarkDto bkSbTmplatSecBclientLocZip = createBookmark(BookmarkConstants.CLIENT_ZIP,
                    addr.getAddrZip());
            bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBclientLocZip);

            if (!ObjectUtils.isEmpty(addr.getPhone())) {
                BookmarkDto bkSbTmplatSecBclientLocPh = createBookmark(BookmarkConstants.CLIENT_PHONE,
                        TypeConvUtil.formatPhone(addr.getPhone()));
                bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBclientLocPh);
            }

            BookmarkDto bkSbTmplatSecBclientLocCity = createBookmark(BookmarkConstants.CLIENT_CITY,
                    addr.getAddrCity());
            bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBclientLocCity);
            BookmarkDto bkSbTmplatSecBclientLocCounty = createBookmarkWithCodesTable(BookmarkConstants.CLIENT_COUNTY,
                    addr.getCdAddrCounty(), CodesConstant.CCOUNT);
            bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBclientLocCounty);

            BookmarkDto bkSbTmplatSecBclientLocLn1 = createBookmark(BookmarkConstants.CLIENT_ADDR1,
                    addr.getAddrPersAddrStLn1());
            bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBclientLocLn1);

            BookmarkDto bkSbTmplatSecBclientDtoHme = createBookmark(BookmarkConstants.CLIENT_DIR_TO_HOME,
                    serviceAuthFormDataDto.getServiceAuthorizationDto().getSvcAuthDirToHome());
            bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBclientDtoHme);

            BookmarkDto bkSbTmplatSecBclientAbtRes = createBookmarkWithCodesTable(BookmarkConstants.CLIENT_ABIL_TO_RESP,
                    serviceAuthFormDataDto.getServiceAuthorizationDto().getCdSvcAuthAbilToRespond(), CodesConstant.CABLRESP);
            bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBclientAbtRes);
            BookmarkDto bkSbTmplatSecBclientVref = createBookmark(BookmarkConstants.CLIENT_VERB_REF_DT,
                    DateUtils.stringDt(serviceAuthFormDataDto.getServiceAuthorizationDto().getDtSvcAuthVerbalReferl()));
            bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBclientVref);

            BookmarkDto bkSbTmplatSecBclientCnd = createBookmark(BookmarkConstants.CLIENT_HOME_ENVIR,
                    serviceAuthFormDataDto.getServiceAuthorizationDto().getSvcAuthHomeEnviron());
            bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBclientCnd);

            BookmarkDto bkSbTmplatSecBclientMedCDn = createBookmark(BookmarkConstants.CLIENT_CONDITIONS,
                    serviceAuthFormDataDto.getServiceAuthorizationDto().getSvcAuthMedCond());
            bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBclientMedCDn);

            if (null != addr.getAddrPersAddrStLn2()) {
                BookmarkDto bkSbTmplatSecBclientLocAddr2 = createBookmark(
                        BookmarkConstants.CLIENT_ADDR2, addr.getAddrPersAddrStLn2());
                bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBclientLocAddr2);
            }

            if (null != addr.getPhoneExtension()) {
                BookmarkDto bkSbTmplatSecBclientLocPhExtn = createBookmark(
                        BookmarkConstants.CLIENT_EXT, TypeConvUtil.formatPhoneWithExtn(addr.getPhoneExtension()));
                bookmarkDtoDefaultDtoList.add(bkSbTmplatSecBclientLocPhExtn);
            }

        }

        // get vendor Details
        if (!ObjectUtils.isEmpty(serviceAuthFormDataDto.getServiceAuthDetailDto())) {
            BookmarkDto bkPrTmplatApprvrNm = createBookmark(BookmarkConstants.VENDOR_NAME,
                    serviceAuthFormDataDto.getServiceAuthDetailDto().getNmVendorName());
            bookmarkDtoDefaultDtoList.add(bkPrTmplatApprvrNm);
            BookmarkDto bkPrTmplatApprvrAdd1 = createBookmark(BookmarkConstants.VENDOR_ADDR1,
                    serviceAuthFormDataDto.getServiceAuthDetailDto().getVendorStreetLn1());
            bookmarkDtoDefaultDtoList.add(bkPrTmplatApprvrAdd1);
            if (null != serviceAuthFormDataDto.getServiceAuthDetailDto().getVendorStreetLn2()) {
                BookmarkDto bkPrTmplatApprvrAdd2 = createBookmark(BookmarkConstants.VENDOR_ADDR2,
                        serviceAuthFormDataDto.getServiceAuthDetailDto().getVendorStreetLn2());
                bookmarkDtoDefaultDtoList.add(bkPrTmplatApprvrAdd2);
            }
            BookmarkDto bkPrTmplatApprvrFNm = createBookmark(BookmarkConstants.VENDOR_CITY,
                    serviceAuthFormDataDto.getServiceAuthDetailDto().getVendorCity());
            bookmarkDtoDefaultDtoList.add(bkPrTmplatApprvrFNm);
            BookmarkDto bkPrTmplatApprvrVSt = createBookmark(BookmarkConstants.VENDOR_STATE,
                    serviceAuthFormDataDto.getServiceAuthDetailDto().getVendorState());
            bookmarkDtoDefaultDtoList.add(bkPrTmplatApprvrVSt);
            BookmarkDto bkPrTmplatApprvrVZip = createBookmark(BookmarkConstants.VENDOR_ZIP,
                    serviceAuthFormDataDto.getServiceAuthDetailDto().getVendorZip());
            bookmarkDtoDefaultDtoList.add(bkPrTmplatApprvrVZip);
            BookmarkDto bkPrTmplatApprvrVZipSfx = createBookmark(BookmarkConstants.VENDOR_ZIP_SUFFIX,
                    serviceAuthFormDataDto.getServiceAuthDetailDto().getVendorZipSuff());
            bookmarkDtoDefaultDtoList.add(bkPrTmplatApprvrVZipSfx);
            BookmarkDto bkPrTmplatApprvrVPhn = createBookmark(BookmarkConstants.VENDOR_PHONE,
                    TypeConvUtil.formatPhone(serviceAuthFormDataDto.getServiceAuthDetailDto().getVendorPhone()));
            bookmarkDtoDefaultDtoList.add(bkPrTmplatApprvrVPhn);
            BookmarkDto bkPrTmplatApprvrVPhnExtn = createBookmark(BookmarkConstants.VENDOR_PHONE_EXT,
                    TypeConvUtil.formatPhoneWithExtn(serviceAuthFormDataDto.getServiceAuthDetailDto().getVendorPhoneExt()));
            bookmarkDtoDefaultDtoList.add(bkPrTmplatApprvrVPhnExtn);
        }


        if (null != serviceAuthFormDataDto.getNameDetailDto()) {
            if (!ObjectUtils.isEmpty(serviceAuthFormDataDto.getNameDetailDto().getNmNameFirst())) {
                BookmarkDto bkphysFname = createBookmark(BookmarkConstants.PHYS_FIRST_NAME,
                        serviceAuthFormDataDto.getNameDetailDto().getNmNameFirst());
                bookmarkDtoDefaultDtoList.add(bkphysFname);
            }
            if (!ObjectUtils.isEmpty(serviceAuthFormDataDto.getNameDetailDto().getNmNameLast())) {
                BookmarkDto bkphyLName = createBookmark(BookmarkConstants.PHYS_LAST_NAME,
                        serviceAuthFormDataDto.getNameDetailDto().getNmNameLast());
                bookmarkDtoDefaultDtoList.add(bkphyLName);
            }
            if (!ObjectUtils.isEmpty(serviceAuthFormDataDto.getNameDetailDto().getNmNameMiddle())) {
                BookmarkDto bkphyMName = createBookmark(BookmarkConstants.PHYS_MIDDLE_NAME,
                        serviceAuthFormDataDto.getNameDetailDto().getNmNameMiddle());
                bookmarkDtoDefaultDtoList.add(bkphyMName);
            }
            if (!ObjectUtils.isEmpty(serviceAuthFormDataDto.getNameDetailDto().getCdNameSuffix())) {
                BookmarkDto bkphyNmSfx = createBookmarkWithCodesTable(BookmarkConstants.PHYS_SUFFIX_NAME,
                        serviceAuthFormDataDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
                bookmarkDtoDefaultDtoList.add(bkphyNmSfx);
            }

        }

        if (null != serviceAuthFormDataDto.getPhyAddr()) {

            for (AddrPersonLinkPhoneOutDto phyAddr : serviceAuthFormDataDto.getPhyAddr()) {


                BookmarkDto bkPhyAddLocLn1 = createBookmark(BookmarkConstants.PHYS_ADDR1,
                        phyAddr.getAddrPersAddrStLn1());
                bookmarkDtoDefaultDtoList.add(bkPhyAddLocLn1);
                if (null != phyAddr.getAddrPersAddrStLn2()) {
                    BookmarkDto bkPhyAddLocLn2 = createBookmark(BookmarkConstants.PHYS_ADDR2,
                            phyAddr.getAddrPersAddrStLn2());
                    bookmarkDtoDefaultDtoList.add(bkPhyAddLocLn2);
                }
                BookmarkDto bkPhyCity = createBookmark(BookmarkConstants.PHYS_CITY,
                        phyAddr.getAddrCity());
                bookmarkDtoDefaultDtoList.add(bkPhyCity);
                BookmarkDto bkPhyState = createBookmark(BookmarkConstants.PHYS_STATE,
                        phyAddr.getCdAddrState());
                bookmarkDtoDefaultDtoList.add(bkPhyState);

                BookmarkDto bkPhyZip = createBookmark(BookmarkConstants.PHYS_ZIP,
                        phyAddr.getAddrZip());
                bookmarkDtoDefaultDtoList.add(bkPhyZip);

                if (!ObjectUtils.isEmpty(phyAddr.getPhone())) {
                    BookmarkDto bkPhyPh = createBookmark(BookmarkConstants.PHYS_PHONE,
                            TypeConvUtil.formatPhone(phyAddr.getPhone()));
                    bookmarkDtoDefaultDtoList.add(bkPhyPh);
                }

                if (!ObjectUtils.isEmpty(phyAddr.getPhoneExtension())) {
                    BookmarkDto bkPhyPhExtn = createBookmark(BookmarkConstants.PHYS_EXT,
                            TypeConvUtil.formatPhoneWithExtn(phyAddr.getPhoneExtension()));
                    bookmarkDtoDefaultDtoList.add(bkPhyPhExtn);
                }
            }
        }

        /**
         * Description: TMPLAT_APPROVER ParentGroup: ccn01o86 GroupName:
         * ccn01o87 SubGroups: ccn01o87 BookMark: TMPLAT_APPROVER Condition:
         * None
         */

        if (!ObjectUtils.isEmpty(serviceAuthFormDataDto.getApprovalFormDataDto())) {
            if (!ObjectUtils.isEmpty(serviceAuthFormDataDto.getApprovalFormDataDto().getDtApproversDetermination())) {
                BookmarkDto bkTmplatApprvrDt = createBookmark(BookmarkConstants.APPRV_DATE, DateUtils
                        .stringDt(serviceAuthFormDataDto.getApprovalFormDataDto().getDtApproversDetermination()));
                bookmarkDtoDefaultDtoList.add(bkTmplatApprvrDt);
            }

            BookmarkDto bkPrTmplatApprvrFNm = createBookmark(BookmarkConstants.APPRV_NAME_FIRST,
                    serviceAuthFormDataDto.getApprovalFormDataDto().getNmFirst());
            bookmarkDtoDefaultDtoList.add(bkPrTmplatApprvrFNm);

            BookmarkDto bkPrTmplatApprvrLNm = createBookmark(BookmarkConstants.APPRV_NAME_LAST,
                    serviceAuthFormDataDto.getApprovalFormDataDto().getNmLast());
            bookmarkDtoDefaultDtoList.add(bkPrTmplatApprvrLNm);

            BookmarkDto bkPrTmplatApprvrMNm = createBookmark(BookmarkConstants.APPRV_NAME_MIDDLE,
                    serviceAuthFormDataDto.getApprovalFormDataDto().getNmMiddle());
            bookmarkDtoDefaultDtoList.add(bkPrTmplatApprvrMNm);

            BookmarkDto bkPrTmplatApprvrTitle = createBookmark(BookmarkConstants.APPRV_TITLE,
                    serviceAuthFormDataDto.getApprovalFormDataDto().getTxtEmployeeClass());
            bookmarkDtoDefaultDtoList.add(bkPrTmplatApprvrTitle);
        }

        /**
         * Description: TMPLAT_ORIGWORKER_SUFFIX ParentGroup: ccn01o86
         * GroupName: ccn01o87 SubGroups: BookMark: TMPLAT_ORIGWORKER_SUFFIX
         * Condition: null != CdNameSuffix
         */

        if (null != serviceAuthFormDataDto.getEmployeePersPhNameDto().getCdNameSuffix()) {
            BookmarkDto bkPrTmplatApprvrSufxDecode = createBookmarkWithCodesTable(
                    BookmarkConstants.APPRV_NAME_SUFFIX,
                    serviceAuthFormDataDto.getApprovalFormDataDto().getNmSuffix(), CodesConstant.CSUFFIX2);

            bookmarkDtoDefaultDtoList.add(bkPrTmplatApprvrSufxDecode);

        }
        if (!CollectionUtils.isEmpty(serviceAuthFormDataDto.getApsInHomeTasks())) {
            //Adding the loop to iterate inhome task list and add them into parent group list to display in form
            serviceAuthFormDataDto.getApsInHomeTasks().forEach(inHomeTask -> {
                FormDataGroupDto inhomeServList = createFormDataGroup(FormGroupsConstants.TMPLAT_INHOME_TASKS,
                        FormConstants.EMPTY_STRING);
                if (null != inHomeTask.getInHomeSvcAuthTask()) {
                    List<BookmarkDto> bookmarkColList = new ArrayList<>();
                    BookmarkDto bkPrTmplatInhomeTasks = createBookmarkWithCodesTable(BookmarkConstants.INHOME_TASK,
                            inHomeTask.getInHomeSvcAuthTask(), CodesConstant.CINHMTSK);
                    bookmarkDtoDefaultDtoList.add(bkPrTmplatInhomeTasks);
                    bookmarkColList.add(bkPrTmplatInhomeTasks);
                    inhomeServList.setBookmarkDtoList(bookmarkColList);
                    fdParentGroupList.add(inhomeServList);
                }
            });
        }
        PreFillDataServiceDto preFillDataServiceDto = new PreFillDataServiceDto();
        preFillDataServiceDto.setBookmarkDtoList(bookmarkDtoDefaultDtoList);
        preFillDataServiceDto.setFormDataGroupList(fdParentGroupList);

        return preFillDataServiceDto;
    }
}
