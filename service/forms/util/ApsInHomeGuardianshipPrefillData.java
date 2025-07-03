package us.tx.state.dfps.service.forms.util;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.apsinhomecontact.dto.ApsInHomeGuardianReferralDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.PersonIncomeResourceDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

import java.util.ArrayList;
import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Creates
 * prefill string to populate data on form December 01, 2021- 10:42:13 AM © 2017
 * Texas Department of Family and Protective Services
 */
@Component
public class ApsInHomeGuardianshipPrefillData extends DocumentServiceUtil {

    public static final String DEFAULT_CITIZENSHIP = "Undetermined Immigration Status";

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
        ApsInHomeGuardianReferralDto prefillDto = (ApsInHomeGuardianReferralDto) parentDtoobj;

        List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
        List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

        // Initializing null DTOs
        if (ObjectUtils.isEmpty(prefillDto.getGenericCaseInfoDto())) {
            prefillDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
        }

        if (ObjectUtils.isEmpty(prefillDto.getCaseInfoPrincipalList())) {
            prefillDto.setCaseInfoPrincipalList(new ArrayList<CaseInfoDto>());
        }

        if (ObjectUtils.isEmpty(prefillDto.getEmpWorkerDto())) {
            prefillDto.setEmpWorkerDto(new EmployeePersPhNameDto());
        }

        /*
         * Populating the non form group data into prefill data. !!bookmarks
         */
        // CSEC02D  Case intake information
        if (!ObjectUtils.isEmpty(prefillDto.getGenericCaseInfoDto())) {
 /*           bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.INTAKE_DATE,
                    DateUtils.stringDt(prefillDto.getGenericCaseInfoDto().getDtStageStart())));*/
            bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.INTAKE_DATE,
                    DateUtils.stringDt(prefillDto.getGenericCaseInfoDto().getDtCaseOpened())));
            bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.REFERRAL_DATE,
                    DateUtils.stringDt(prefillDto.getGenericCaseInfoDto().getDtSysDtGenericSysdate())));
            if (!ObjectUtils.isEmpty(prefillDto.getGenericCaseInfoDto().getCdStageProgram())) {
                bookmarkNonFrmGrpList.add(createBookmarkWithCodesTable(BookmarkConstants.REFERRAL_PROGRAM,
                        prefillDto.getGenericCaseInfoDto().getCdStageProgram(), CodesConstant.CCONPROG));
            }
            /*bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.REFERRAL_FORM_ID,
                    prefillDto.getGenericCaseInfoDto().getIdSituation()));*/
            bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.REFERRAL_FORM_ID,
                    prefillDto.getEventDto()!=null?prefillDto.getEventDto().getIdEvent():0));
            bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.WARD_CASE_NUMBER,
                    prefillDto.getGenericCaseInfoDto().getIdCase()));
        }

        // CSEC01D  populate worker and supervisor details
        if (!ObjectUtils.isEmpty(prefillDto.getEmpWorkerDto())) {
            FormDataGroupDto workerGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CASEWORKER,
                    FormConstants.EMPTY_STRING);
            List<FormDataGroupDto> workerGroupDtoList = new ArrayList<FormDataGroupDto>();
            List<BookmarkDto> bookmarkWorkerList = new ArrayList<BookmarkDto>();

            bookmarkWorkerList.add(createBookmark(BookmarkConstants.CASEWORKER_FIRST_NAME,
                    prefillDto.getEmpWorkerDto().getNmNameFirst()));

            bookmarkWorkerList.add(createBookmark(BookmarkConstants.CASEWORKER_LAST_NAME,
                    prefillDto.getEmpWorkerDto().getNmNameLast()));

            bookmarkWorkerList.add(createBookmark(BookmarkConstants.CASEWORKER_MIDDLE_NAME,
                    prefillDto.getEmpWorkerDto().getNmNameMiddle()));

            bookmarkWorkerList.add(createBookmark(BookmarkConstants.CASEWORKER_ADDRESS_CITY,
                    prefillDto.getEmpWorkerDto().getAddrMailCodeCity()));

            bookmarkWorkerList.add(createBookmark(BookmarkConstants.CASEWORKER_ADDRESS_LN1,
                    prefillDto.getEmpWorkerDto().getAddrMailCodeStLn1()));

            bookmarkWorkerList.add(createBookmark(BookmarkConstants.CASEWORKER_ADDRESS_LN2,
                    prefillDto.getEmpWorkerDto().getAddrMailCodeStLn2()));

            bookmarkWorkerList.add(createBookmark(BookmarkConstants.CASEWORKER_ADDRESS_ZIP,
                    prefillDto.getEmpWorkerDto().getAddrMailCodeZip()));

            bookmarkWorkerList.add(createBookmark(BookmarkConstants.CASEWORKER_BJN,
                    prefillDto.getEmpWorkerDto().getBjnJob()));

            bookmarkWorkerList.add(createBookmark(BookmarkConstants.CASEWORKER_PHONE,
                    TypeConvUtil.formatPhone(prefillDto.getEmpWorkerDto().getNbrPhone())));

            if (!ObjectUtils.isEmpty(prefillDto.getWorkerEmailDto().getCdEmpUnitRegion())) {
                bookmarkWorkerList.add(createBookmarkWithCodesTable(BookmarkConstants.CASEWORKER_DFPS_REGION,
                        prefillDto.getWorkerEmailDto().getCdEmpUnitRegion(), CodesConstant.CREGDIV));
            }

            workerGroupDto.setBookmarkDtoList(bookmarkWorkerList);


            if (!ObjectUtils.isEmpty(prefillDto.getEmpWorkerDto().getNbrPhoneExtension())) {
                FormDataGroupDto workerPhoneExtGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CASEWORKER_EXT,
                        FormGroupsConstants.TMPLAT_CASEWORKER);

                List<BookmarkDto> bkCaseWorkerExtnList = new ArrayList<BookmarkDto>();

                bkCaseWorkerExtnList.add(createBookmark(BookmarkConstants.CASEWORKER_EXT,
                        prefillDto.getEmpWorkerDto().getNbrPhoneExtension()));
                workerPhoneExtGrpDto.setBookmarkDtoList(bkCaseWorkerExtnList);
                // adding the subgroup as a list
                workerGroupDtoList.add(workerPhoneExtGrpDto);

            }

            if (!ObjectUtils.isEmpty(prefillDto.getEmpWorkerDto().getCdNameSuffix())) {

                FormDataGroupDto workerSuffixGroDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CASEWORKER_SUFFIX,
                        FormGroupsConstants.TMPLAT_CASEWORKER);

                List<BookmarkDto> bkCaseWorkerSuffixList = new ArrayList<BookmarkDto>();
                bkCaseWorkerSuffixList.add(createBookmarkWithCodesTable(BookmarkConstants.CASEWORKER_SUFFIX,
                        prefillDto.getEmpWorkerDto().getCdNameSuffix(), CodesConstant.CSUFFIX2));

                workerSuffixGroDto.setBookmarkDtoList(bkCaseWorkerSuffixList);

                // adding the subgroup as a list
                workerGroupDtoList.add(workerSuffixGroDto);

            }

            if (!ObjectUtils.isEmpty(prefillDto.getWorkerEmailDto())
                    && !ObjectUtils.isEmpty(prefillDto.getWorkerEmailDto().getTxtEmployeeEmailAddress())) {
                FormDataGroupDto emailGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CASEWORKER_EMAIL,
                        FormGroupsConstants.TMPLAT_CASEWORKER);
                List<BookmarkDto> bookmarkList = new ArrayList<BookmarkDto>();
                bookmarkList.add(createBookmark(BookmarkConstants.CASEWORKER_EMAIL,
                        prefillDto.getWorkerEmailDto().getTxtEmployeeEmailAddress()));
                emailGroup.setBookmarkDtoList(bookmarkList);
                workerGroupDtoList.add(emailGroup);
            }

            workerGroupDto.setFormDataGroupList(workerGroupDtoList);
            // adding the subgroup as a list
            formDataGroupList.add(workerGroupDto);

        }
        //populate supervisor details

        if (!ObjectUtils.isEmpty(prefillDto.getSupervisorDto())) {
            FormDataGroupDto supervisorGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SUPERVISOR,
                    FormConstants.EMPTY_STRING);
            List<FormDataGroupDto> supervisorGroupDtoList = new ArrayList<FormDataGroupDto>();
            List<BookmarkDto> bookmarkSupervsiorList = new ArrayList<BookmarkDto>();

            bookmarkSupervsiorList.add(createBookmark(BookmarkConstants.SUPERVISOR_FIRST_NAME,
                    prefillDto.getSupervisorDto().getNmNameFirst()));

            bookmarkSupervsiorList.add(createBookmark(BookmarkConstants.SUPERVISOR_LAST_NAME,
                    prefillDto.getSupervisorDto().getNmNameLast()));

            bookmarkSupervsiorList.add(createBookmark(BookmarkConstants.SUPERVISOR_MIDDLE_NAME,
                    prefillDto.getSupervisorDto().getNmNameMiddle()));

            bookmarkSupervsiorList.add(createBookmark(BookmarkConstants.SUPERVISOR_ADDRESS_CITY,
                    prefillDto.getSupervisorDto().getAddrMailCodeCity()));

            bookmarkSupervsiorList.add(createBookmark(BookmarkConstants.SUPERVISOR_ADDRESS_LN1,
                    prefillDto.getSupervisorDto().getAddrMailCodeStLn1()));

            bookmarkSupervsiorList.add(createBookmark(BookmarkConstants.SUPERVISOR_ADDRESS_LN2,
                    prefillDto.getSupervisorDto().getAddrMailCodeStLn2()));

            bookmarkSupervsiorList.add(createBookmark(BookmarkConstants.SUPERVISOR_ADDRESS_ZIP,
                    prefillDto.getSupervisorDto().getAddrMailCodeZip()));

            bookmarkSupervsiorList.add(createBookmark(BookmarkConstants.SUPERVISOR_BJN,
                    prefillDto.getSupervisorDto().getBjnJob()));

            bookmarkSupervsiorList.add(createBookmark(BookmarkConstants.SUPERVISOR_PHONE,
                    TypeConvUtil.formatPhone(prefillDto.getSupervisorDto().getNbrPhone())));
            supervisorGroupDto.setBookmarkDtoList(bookmarkSupervsiorList);


            if (!ObjectUtils.isEmpty(prefillDto.getSupervisorDto().getNbrPhoneExtension())) {
                FormDataGroupDto supervisorPhoneExtGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SUPERVISOR_EXT,
                        FormGroupsConstants.TMPLAT_SUPERVISOR);

                List<BookmarkDto> bkCaseWorkerExtnList = new ArrayList<BookmarkDto>();

                bkCaseWorkerExtnList.add(createBookmark(BookmarkConstants.SUPERVISOR_EXT,
                        prefillDto.getSupervisorDto().getNbrPhoneExtension()));
                supervisorPhoneExtGrpDto.setBookmarkDtoList(bkCaseWorkerExtnList);
                // adding the subgroup as a list
                supervisorGroupDtoList.add(supervisorPhoneExtGrpDto);
            }

            if (!ObjectUtils.isEmpty(prefillDto.getSupervisorDto().getCdNameSuffix())) {

                FormDataGroupDto supervisorSuffixGroDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SUPERVISOR_SUFFIX,
                        FormGroupsConstants.TMPLAT_SUPERVISOR);

                List<BookmarkDto> bkCaseWorkerSuffixList = new ArrayList<BookmarkDto>();
                bkCaseWorkerSuffixList.add(createBookmarkWithCodesTable(BookmarkConstants.SUPERVISOR_NAME_SUFFIX,
                        prefillDto.getSupervisorDto().getCdNameSuffix(), CodesConstant.CSUFFIX2));

                supervisorSuffixGroDto.setBookmarkDtoList(bkCaseWorkerSuffixList);

                // adding the subgroup as a list
                supervisorGroupDtoList.add(supervisorSuffixGroDto);

            }
            if (!ObjectUtils.isEmpty(prefillDto.getSupervisorEmailDto())
                    && !ObjectUtils.isEmpty(prefillDto.getSupervisorEmailDto().getTxtEmployeeEmailAddress())) {
                FormDataGroupDto emailGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SUPERVISOR_EMAIL,
                        FormGroupsConstants.TMPLAT_SUPERVISOR);
                List<BookmarkDto> bookmarkList = new ArrayList<BookmarkDto>();
                bookmarkList.add(createBookmark(BookmarkConstants.SUPERVISOR_EMAIL,
                        prefillDto.getSupervisorEmailDto().getTxtEmployeeEmailAddress()));
                emailGroup.setBookmarkDtoList(bookmarkList);
                supervisorGroupDtoList.add(emailGroup);
            }

            supervisorGroupDto.setFormDataGroupList(supervisorGroupDtoList);
            // adding the subgroup as a list
            formDataGroupList.add(supervisorGroupDto);
        }

        // Ward indentifier information
        //Add all applicable identifiers at once
        FormDataGroupDto wardIdentifierGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_WARD_IDENTIFIER,
                FormConstants.EMPTY_STRING);
        List<BookmarkDto> bookmarkWardIdentifierList = new ArrayList<BookmarkDto>();

        if (!ObjectUtils.isEmpty(prefillDto.getIdentifierStr())) {
//            FormDataGroupDto wardIdentifierGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_WARD_IDENTIFIER,
//                    FormConstants.EMPTY_STRING);
//            List<BookmarkDto> bookmarkWardIdentifierList = new ArrayList<BookmarkDto>();

            bookmarkWardIdentifierList.add(createBookmark(BookmarkConstants.WARD_IDENTIFIER_TYPE,
                    "SSN"));
            bookmarkWardIdentifierList.add(createBookmark(BookmarkConstants.WARD_IDENTIFIER_NUMBER,
                    prefillDto.getIdentifierStr()));
            /*wardIdentifierGroup.setBookmarkDtoList(bookmarkWardIdentifierList);
            formDataGroupList.add(wardIdentifierGroup);*/
        }

        // Add State Id to Ward indentifier information if it is available
        if (!ObjectUtils.isEmpty(prefillDto.getStateIdStr())) {

            bookmarkWardIdentifierList.add(createBookmark(BookmarkConstants.WARD_IDENTIFIER_TYPE,
                    CodesConstant.CNUMTYPE_STATE_PHOTO_ID_NUMBER));
            bookmarkWardIdentifierList.add(createBookmark(BookmarkConstants.WARD_IDENTIFIER_NUMBER,
                    prefillDto.getStateIdStr()));
        }

        // Add Driver's licence to Ward indentifier information if it is available
        if (!ObjectUtils.isEmpty(prefillDto.getDriverLicenceStr())) {

            bookmarkWardIdentifierList.add(createBookmark(BookmarkConstants.WARD_IDENTIFIER_TYPE,
                    CodesConstant.CNUMTYPE_DRIVERS_LICENSE_NUMBER));
            bookmarkWardIdentifierList.add(createBookmark(BookmarkConstants.WARD_IDENTIFIER_NUMBER,
                    prefillDto.getDriverLicenceStr()));
        }


        if (!ObjectUtils.isEmpty(prefillDto.getMedicaidNumStr())) {
            /*FormDataGroupDto wardIdentifierGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_WARD_IDENTIFIER,
                    FormConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkWardIdentifierList = new ArrayList<BookmarkDto>();*/

            bookmarkWardIdentifierList.add(createBookmark(BookmarkConstants.WARD_IDENTIFIER_TYPE,
                    "Medicaid #"));

            bookmarkWardIdentifierList.add(createBookmark(BookmarkConstants.WARD_IDENTIFIER_NUMBER,
                    prefillDto.getMedicaidNumStr()));

            /*wardIdentifierGroup.setBookmarkDtoList(bookmarkWardIdentifierList);
            formDataGroupList.add(wardIdentifierGroup);*/
        }

        wardIdentifierGroup.setBookmarkDtoList(bookmarkWardIdentifierList);
        formDataGroupList.add(wardIdentifierGroup);


        //WARD information
        if (!ObjectUtils.isEmpty(prefillDto.getWardInfo())) {
            FormDataGroupDto wardGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_WARD,
                    FormConstants.EMPTY_STRING);
            List<FormDataGroupDto> wardGroupDtoList = new ArrayList<FormDataGroupDto>();
            List<BookmarkDto> bookmarkWardList = new ArrayList<BookmarkDto>();

            if (!ObjectUtils.isEmpty(prefillDto.getWardInfo().getDob())) {
                bookmarkWardList.add(createBookmark(BookmarkConstants.WARD_DOB,
                        DateUtils.stringDt(DateUtils.stringDate((prefillDto.getWardInfo().getDob())))));
            }
            bookmarkWardList.add(createBookmark(BookmarkConstants.WARD_AGE,
                    prefillDto.getWardInfo().getNbrPersonAge()));

            bookmarkWardList.add(createBookmarkWithCodesTable(BookmarkConstants.WARD_SEX,
                    prefillDto.getWardInfo().getCdPersonSex(), CodesConstant.CSEX)); //CRSRCSEX BOTH

            if (!ObjectUtils.isEmpty(prefillDto.getWardInfo().getCdPersonEthnicGroup())) {
                //artf255891 - If PROPOSED WARD INFORMATION race contains "(" (ex:"Multiple  (Unable to Determine)") split and display
                // first string (ex:"Multiple") else display race complete decode value
                Object decodedValue = getDecodedValue(prefillDto.getWardInfo().getCdPersonEthnicGroup(), CodesConstant.CETHNIC);
                bookmarkWardList.add(createBookmark(BookmarkConstants.WARD_RACE, StringUtils.isEmpty(StringUtils.split((String) decodedValue, "("))
                        ? (String) decodedValue : StringUtils.split((String) decodedValue, "(")[0]));
            } else {
                bookmarkWardList.add(createBookmark(BookmarkConstants.WARD_RACE,""));
            }


            bookmarkWardList.add(createBookmarkWithCodesTable(BookmarkConstants.WARD_ETHNICITY,
                    prefillDto.getWardInfo().getCdEthniCity(), CodesConstant.CINDETHN));

            if (!ObjectUtils.isEmpty(prefillDto.getWardInfo().getCdPersonCitizenShip())) {
                /*bookmarkWardList.add(createBookmarkWithCodesTable(BookmarkConstants.WARD_CITIZENSHIP,
                        prefillDto.getWardInfo().getCdPersonCitizenShip(), CodesConstant.CCTZNSTA));*/
                bookmarkWardList.add(createBookmark(BookmarkConstants.WARD_CITIZENSHIP,
                        prefillDto.getWardInfo().getCdPersonCitizenShip()));
            } else {
                bookmarkWardList.add(createBookmark(BookmarkConstants.WARD_CITIZENSHIP,
                        DEFAULT_CITIZENSHIP));
            }

            bookmarkWardList.add(createBookmarkWithCodesTable(BookmarkConstants.WARD_STATUS,
                    prefillDto.getWardInfo().getCdPersonMaritalStatus(), CodesConstant.CMARSTAT));

            bookmarkWardList.add(createBookmarkWithCodesTable(BookmarkConstants.WARD_LANGUAGE,
                    prefillDto.getWardInfo().getCdPersonLanguage(), CodesConstant.CLANG));

            bookmarkWardList.add(createBookmark(BookmarkConstants.WARD_FIRST_NAME,
                    prefillDto.getWardInfo().getNmPersonFirst()));
            bookmarkWardList.add(createBookmark(BookmarkConstants.WARD_LAST_NAME,
                    prefillDto.getWardInfo().getNmPersonLast()));
            bookmarkWardList.add(createBookmark(BookmarkConstants.WARD_MIDDLE_NAME,
                    prefillDto.getWardInfo().getNmPersonMiddle()));


            bookmarkWardList.add(createBookmark(BookmarkConstants.WARD_ID,
                    prefillDto.getWardInfo().getIdPerson()));

            if (!ObjectUtils.isEmpty(prefillDto.getWardInfo().getCdPersonSuffix())) {
                FormDataGroupDto wardSuffixGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_WARD_SUFFIX,
                        FormGroupsConstants.TMPLAT_WARD);
                List<BookmarkDto> bookmarkWardSuffixList = new ArrayList<BookmarkDto>();
                bookmarkWardSuffixList.add(createBookmarkWithCodesTable(BookmarkConstants.WARD_SUFFIX,
                        prefillDto.getWardInfo().getCdPersonSuffix(), CodesConstant.CSUFFIX2));
                wardSuffixGroup.setBookmarkDtoList(bookmarkWardSuffixList);
                wardGroupDtoList.add(wardSuffixGroup);

            }
            wardGroupDto.setBookmarkDtoList(bookmarkWardList);
            wardGroupDto.setFormDataGroupList(wardGroupDtoList);
            // adding the subgroup as a list
            formDataGroupList.add(wardGroupDto);
        }

            //Ward Address information
            if (!ObjectUtils.isEmpty(prefillDto.getWardAddressInfoList())) {
                for (PersonDto wardAddressInfoDto : prefillDto.getWardAddressInfoList()) {
                FormDataGroupDto wardAddressGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_WARD_ADDRESS,
                        FormConstants.EMPTY_STRING);
                List<FormDataGroupDto> wardAddressGroupList = new ArrayList<FormDataGroupDto>();

                List<BookmarkDto> bookmarkWardAddressList = new ArrayList<BookmarkDto>();
                bookmarkWardAddressList.add(createBookmarkWithCodesTable(BookmarkConstants.WARD_ADDRESS_TYPE,
                        wardAddressInfoDto.getAddrPersonLink(), CodesConstant.CADDRTYP));

                bookmarkWardAddressList.add(createBookmark(BookmarkConstants.WARD_ADDRESS_LN1,
                            wardAddressInfoDto.getAddrPersonStLn1()));

                if (!ObjectUtils.isEmpty(wardAddressInfoDto.getAddrPersonStLn2())) {
                    bookmarkWardAddressList.add(createBookmark(BookmarkConstants.WARD_ADDRESS_LN2,
                            wardAddressInfoDto.getAddrPersonStLn2()));
                 }

                bookmarkWardAddressList.add(createBookmark(BookmarkConstants.WARD_ADDRESS_CITY,
                            wardAddressInfoDto.getAddrPersonCity()));
                bookmarkWardAddressList.add(createBookmark(BookmarkConstants.WARD_ADDRESS_STATE,
                            wardAddressInfoDto.getCdPersonState()));
                bookmarkWardAddressList.add(createBookmark(BookmarkConstants.WARD_ADDRESS_ZIP,
                            wardAddressInfoDto.getAddrPersonZip()));
                bookmarkWardAddressList.add(createBookmark(BookmarkConstants.WARD_PHONE,
                        TypeConvUtil.formatPhone(wardAddressInfoDto.getPersonPhone())));
                bookmarkWardAddressList.add(createBookmark(BookmarkConstants.UE_GROUPID,
                            wardAddressInfoDto.getIdPerson()));
                /*bookmarkWardAddressList.add(createBookmark(BookmarkConstants.WARD_EXT,
                        wardAddressInfoDto.getPhoneExtnsn()));*/

                if (!ObjectUtils.isEmpty(wardAddressInfoDto.getPhoneExtnsn())) {
                        FormDataGroupDto workerPhoneExtGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_WARD_EXT,
                                FormGroupsConstants.TMPLAT_WARD_ADDRESS);

                        List<BookmarkDto> bkWardExtnList = new ArrayList<BookmarkDto>();

                    bkWardExtnList.add(createBookmark(BookmarkConstants.WARD_EXT,
                                wardAddressInfoDto.getPhoneExtnsn()));
                        workerPhoneExtGrpDto.setBookmarkDtoList(bkWardExtnList);
                        // adding the subgroup as a list
                    wardAddressGroupList.add(workerPhoneExtGrpDto);

                    }


                  bookmarkWardAddressList.add(createBookmarkWithCodesTable(BookmarkConstants.WARD_ADDRESS_TYPE_2,
                            wardAddressInfoDto.getAddrPersonLink(), CodesConstant.CADDRTYP));
                  if (!ObjectUtils.isEmpty(wardAddressInfoDto.getCdPersonCounty())) {
                        bookmarkWardAddressList.add(createBookmarkWithCodesTable(BookmarkConstants.WARD_COUNTY,
                                wardAddressInfoDto.getCdPersonCounty(), CodesConstant.CCOUNT));
                    }


                if (!ObjectUtils.isEmpty(wardAddressInfoDto.getAddrPersonAttn())) {
                    FormDataGroupDto wardAddressFacilityGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_WARD_ADDRESS_FACILITY,
                            FormGroupsConstants.TMPLAT_WARD_ADDRESS);
                    List<BookmarkDto> bookmarkWardAddrFacilityList = new ArrayList<BookmarkDto>();
                    bookmarkWardAddrFacilityList.add(createBookmark(BookmarkConstants.WARD_ADDRESS_FACILITY_NAME,
                            wardAddressInfoDto.getAddrPersonAttn()));
                    bookmarkWardAddrFacilityList.add(createBookmark(BookmarkConstants.UE_GROUPID,
                            wardAddressInfoDto.getIdPerson()));

                    wardAddressFacilityGroup.setBookmarkDtoList(bookmarkWardAddrFacilityList);
                    wardAddressGroupList.add(wardAddressFacilityGroup);

                }
                wardAddressGroup.setBookmarkDtoList(bookmarkWardAddressList);
                wardAddressGroup.setFormDataGroupList(wardAddressGroupList);
                formDataGroupList.add(wardAddressGroup);

            }

        }
        if (!ObjectUtils.isEmpty(prefillDto.getAllegationStrList())) {
            FormDataGroupDto allegationGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATIONS,
                    FormConstants.EMPTY_STRING);
            List<BookmarkDto> bookmarkAllegationList = new ArrayList<BookmarkDto>();
            for (String allegationStr : prefillDto.getAllegationStrList()) {
                bookmarkAllegationList.add(createBookmark(BookmarkConstants.ALLEGATIONS,
                        allegationStr));
            }
            allegationGroup.setBookmarkDtoList(bookmarkAllegationList);
            formDataGroupList.add(allegationGroup);
        }

        //populate medical details
        if (!ObjectUtils.isEmpty(prefillDto.getMedicalInfoDtoList())) {
            for (PersonDto medicalPersonDto : prefillDto.getMedicalInfoDtoList()) {
                FormDataGroupDto medicalGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_MEDICAL,
                        FormConstants.EMPTY_STRING);
                List<FormDataGroupDto> medicalGroupDtoList = new ArrayList<FormDataGroupDto>();
                List<BookmarkDto> bookmarkMedicalList = new ArrayList<BookmarkDto>();

                bookmarkMedicalList.add(createBookmarkWithCodesTable(BookmarkConstants.MEDICAL_TYPE,
                        medicalPersonDto.getCdStagePersRelInt(), CodesConstant.CRPTRINT));

                bookmarkMedicalList.add(createBookmark(BookmarkConstants.MEDICAL_FIRST_NAME,
                        medicalPersonDto.getNmPersonFirst()));

                bookmarkMedicalList.add(createBookmark(BookmarkConstants.MEDICAL_LAST_NAME,
                        medicalPersonDto.getNmPersonLast()));

                bookmarkMedicalList.add(createBookmark(BookmarkConstants.MEDICAL_MIDDLE_NAME,
                        medicalPersonDto.getNmPersonMiddle()));

                bookmarkMedicalList.add(createBookmark(BookmarkConstants.MEDICAL_ADDRESS_CITY,
                        medicalPersonDto.getAddrPersonCity()));

                bookmarkMedicalList.add(createBookmark(BookmarkConstants.MEDICAL_ADDRESS_LN1,
                        medicalPersonDto.getAddrPersonStLn1()));

                bookmarkMedicalList.add(createBookmark(BookmarkConstants.MEDICAL_ADDRESS_LN2,
                        medicalPersonDto.getAddrPersonStLn2()));

                bookmarkMedicalList.add(createBookmark(BookmarkConstants.MEDICAL_ADDRESS_ZIP,
                        medicalPersonDto.getAddrPersonZip()));

                bookmarkMedicalList.add(createBookmark(BookmarkConstants.MEDICAL_ADDRESS_STATE,
                        medicalPersonDto.getCdPersonState()));

                bookmarkMedicalList.add(createBookmark(BookmarkConstants.MEDICAL_PHONE,
                        TypeConvUtil.formatPhone(medicalPersonDto.getPersonPhone())));

                if (!ObjectUtils.isEmpty(medicalPersonDto.getPhoneExtnsn())) {
                    FormDataGroupDto medicalPhoneExtGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_MEDICAL_EXT,
                            FormGroupsConstants.TMPLAT_MEDICAL);
                    List<BookmarkDto> medicalExtnList = new ArrayList<BookmarkDto>();
                    medicalExtnList.add(createBookmark(BookmarkConstants.MEDICAL_EXT,
                            medicalPersonDto.getPhoneExtnsn()));
                    medicalPhoneExtGrpDto.setBookmarkDtoList(medicalExtnList);
                    // adding the subgroup as a list
                    medicalGroupDtoList.add(medicalPhoneExtGrpDto);
                }
                if (!ObjectUtils.isEmpty(medicalPersonDto.getCdStagePersRole())) {
                    FormDataGroupDto medicalPerpGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_MEDICAL_PERP,
                            FormGroupsConstants.TMPLAT_MEDICAL);
                    List<BookmarkDto> medicalPerpList = new ArrayList<BookmarkDto>();
                    medicalPerpList.add(createBookmarkWithCodesTable(BookmarkConstants.MEDICAL_PERP,
                            medicalPersonDto.getCdStagePersRole(), CodesConstant.CROLEALL));
                    medicalPerpGrpDto.setBookmarkDtoList(medicalPerpList);
                    // adding the subgroup as a list
                    medicalGroupDtoList.add(medicalPerpGrpDto);
                }
                if (!ObjectUtils.isEmpty(medicalPersonDto.getCdPersonSuffix())) {
                    FormDataGroupDto medicalSuffixGroDto = createFormDataGroup(FormGroupsConstants.TMPLAT_MEDICAL_SUFFIX,
                            FormGroupsConstants.TMPLAT_MEDICAL);
                    List<BookmarkDto> bkCaseWorkerSuffixList = new ArrayList<BookmarkDto>();
                    bkCaseWorkerSuffixList.add(createBookmarkWithCodesTable(BookmarkConstants.MEDICAL_SUFFIX,
                            medicalPersonDto.getCdPersonSuffix(), CodesConstant.CSUFFIX2));

                    medicalSuffixGroDto.setBookmarkDtoList(bkCaseWorkerSuffixList);
                    // adding the subgroup as a list
                    medicalGroupDtoList.add(medicalSuffixGroDto);
                }
                bookmarkMedicalList.add(createBookmark(BookmarkConstants.UE_GROUPID,
                        medicalPersonDto.getIdPerson()));
                medicalGroupDto.setBookmarkDtoList(bookmarkMedicalList);
                medicalGroupDto.setFormDataGroupList(medicalGroupDtoList);
                // adding the subgroup as a list
                formDataGroupList.add(medicalGroupDto);
            }
        }

        if (!ObjectUtils.isEmpty(prefillDto.getProfessionalInfoDtoList())) {
            for (PersonDto professionalDto : prefillDto.getProfessionalInfoDtoList()){
                FormDataGroupDto professionalGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PROFESSIONAL,
                        FormConstants.EMPTY_STRING);
                List<FormDataGroupDto> professionalGroupDtoList = new ArrayList<FormDataGroupDto>();
                List<BookmarkDto> bookmarkProfessionalList = new ArrayList<BookmarkDto>();

                bookmarkProfessionalList.add(createBookmarkWithCodesTable(BookmarkConstants.PROFESSIONAL_TYPE,
                        professionalDto.getCdStagePersRelInt(), CodesConstant.CRPTRINT));

                bookmarkProfessionalList.add(createBookmark(BookmarkConstants.PROFESSIONAL_FIRST_NAME,
                        professionalDto.getNmPersonFirst()));

                bookmarkProfessionalList.add(createBookmark(BookmarkConstants.PROFESSIONAL_LAST_NAME,
                        professionalDto.getNmPersonLast()));

                bookmarkProfessionalList.add(createBookmark(BookmarkConstants.PROFESSIONAL_MIDDLE_NAME,
                        professionalDto.getNmPersonMiddle()));

                bookmarkProfessionalList.add(createBookmark(BookmarkConstants.PROFESSIONAL_ADDRESS_CITY,
                        professionalDto.getAddrPersonCity()));

                bookmarkProfessionalList.add(createBookmark(BookmarkConstants.PROFESSIONAL_ADDRESS_LN1,
                        professionalDto.getAddrPersonStLn1()));

                bookmarkProfessionalList.add(createBookmark(BookmarkConstants.PROFESSIONAL_ADDRESS_LN2,
                        professionalDto.getAddrPersonStLn2()));

                bookmarkProfessionalList.add(createBookmark(BookmarkConstants.PROFESSIONAL_ADDRESS_ZIP,
                        professionalDto.getAddrPersonZip()));

                bookmarkProfessionalList.add(createBookmark(BookmarkConstants.PROFESSIONAL_ADDRESS_STATE,
                        professionalDto.getCdPersonState()));

                bookmarkProfessionalList.add(createBookmark(BookmarkConstants.PROFESSIONAL_PHONE,
                        TypeConvUtil.formatPhone(professionalDto.getPersonPhone())));

                if (!ObjectUtils.isEmpty(professionalDto.getPhoneExtnsn())) {
                    FormDataGroupDto professionalPhoneExtGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PROFESSIONAL_EXT,
                            FormGroupsConstants.TMPLAT_PROFESSIONAL);
                    List<BookmarkDto> professionalExtnList = new ArrayList<BookmarkDto>();
                    professionalExtnList.add(createBookmark(BookmarkConstants.PROFESSIONAL_EXT,
                            professionalDto.getPhoneExtnsn()));
                    professionalPhoneExtGrpDto.setBookmarkDtoList(professionalExtnList);
                    // adding the subgroup as a list
                    professionalGroupDtoList.add(professionalPhoneExtGrpDto);
                }
                if (!ObjectUtils.isEmpty(professionalDto.getCdStagePersRole())) {
                    FormDataGroupDto professionalPerpGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PROFESSIONAL_PERP,
                            FormGroupsConstants.TMPLAT_PROFESSIONAL);
                    List<BookmarkDto> professionalPerpList = new ArrayList<BookmarkDto>();
                    professionalPerpList.add(createBookmarkWithCodesTable(BookmarkConstants.PROFESSIONAL_PERP,
                            professionalDto.getCdStagePersRole(), CodesConstant.CROLEALL));
                    professionalPerpGrpDto.setBookmarkDtoList(professionalPerpList);
                    // adding the subgroup as a list
                    professionalGroupDtoList.add(professionalPerpGrpDto);
                }
                if (!ObjectUtils.isEmpty(professionalDto.getCdPersonSuffix())) {
                    FormDataGroupDto professionalSuffixGroDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PROFESSIONAL_SUFFIX,
                            FormGroupsConstants.TMPLAT_PROFESSIONAL);
                    List<BookmarkDto> bkProfessionalSuffixList = new ArrayList<BookmarkDto>();
                    bkProfessionalSuffixList.add(createBookmarkWithCodesTable(BookmarkConstants.PROFESSIONAL_SUFFIX,
                            professionalDto.getCdPersonSuffix(), CodesConstant.CSUFFIX2));

                    professionalSuffixGroDto.setBookmarkDtoList(bkProfessionalSuffixList);
                    // adding the subgroup as a list
                    professionalGroupDtoList.add(professionalSuffixGroDto);
                }
                bookmarkProfessionalList.add(createBookmark(BookmarkConstants.UE_GROUPID,
                        professionalDto.getIdPerson()));
                professionalGroupDto.setBookmarkDtoList(bookmarkProfessionalList);
                professionalGroupDto.setFormDataGroupList(professionalGroupDtoList);
                // adding the subgroup as a list
                formDataGroupList.add(professionalGroupDto);
            }
        }
        // To retrieve persons information
        if (CollectionUtils.isNotEmpty(prefillDto.getPersonalInfoDtoList())) {
            for (PersonDto personalDto : prefillDto.getPersonalInfoDtoList()) {
                if (!ObjectUtils.isEmpty(personalDto.getNmPersonFirst()) || !ObjectUtils.isEmpty(personalDto.getNmPersonLast())) {
                    FormDataGroupDto personalGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PERSONAL,
                            FormConstants.EMPTY_STRING);
                    List<FormDataGroupDto> personalGroupDtoList = new ArrayList<FormDataGroupDto>();
                    List<BookmarkDto> bookmarkPersonalList = new ArrayList<BookmarkDto>();
                    bookmarkPersonalList.add(createBookmarkWithCodesTable(BookmarkConstants.PERSONAL_TYPE,
                            personalDto.getCdStagePersRelInt(), CodesConstant.CRPTRINT));

                    bookmarkPersonalList.add(createBookmark(BookmarkConstants.PERSONAL_FIRST_NAME,
                            personalDto.getNmPersonFirst()));

                    bookmarkPersonalList.add(createBookmark(BookmarkConstants.PERSONAL_LAST_NAME,
                            personalDto.getNmPersonLast()));

                    bookmarkPersonalList.add(createBookmark(BookmarkConstants.PERSONAL_MIDDLE_NAME,
                            personalDto.getNmPersonMiddle()));

                    bookmarkPersonalList.add(createBookmark(BookmarkConstants.PERSONAL_ADDRESS_CITY,
                            personalDto.getAddrPersonCity()));

                    bookmarkPersonalList.add(createBookmark(BookmarkConstants.PERSONAL_ADDRESS_LN1,
                            personalDto.getAddrPersonStLn1()));

                    bookmarkPersonalList.add(createBookmark(BookmarkConstants.PERSONAL_ADDRESS_LN2,
                            personalDto.getAddrPersonStLn2()));

                    bookmarkPersonalList.add(createBookmark(BookmarkConstants.PERSONAL_ADDRESS_ZIP,
                            personalDto.getAddrPersonZip()));

                    bookmarkPersonalList.add(createBookmark(BookmarkConstants.PERSONAL_ADDRESS_STATE,
                            personalDto.getCdPersonState()));

                    bookmarkPersonalList.add(createBookmark(BookmarkConstants.PERSONAL_PHONE,
                            TypeConvUtil.formatPhone(personalDto.getPersonPhone())));

                    bookmarkPersonalList.add(createBookmark(BookmarkConstants.PERSONAL_FAX,
                            ""));
                    bookmarkPersonalList.add(createBookmark(BookmarkConstants.PERSONAL_DOB,
                            (!ObjectUtils.isEmpty(personalDto.getDob())) ? DateUtils.stringDt(DateUtils.stringDate(personalDto.getDob())) : ""));

                    bookmarkPersonalList.add(createBookmark(BookmarkConstants.PERSONAL_EXT,
                            !ObjectUtils.isEmpty(personalDto.getPhoneExtnsn()) ? personalDto.getPhoneExtnsn() : ""));

                    /*bookmarkPersonalList.add(createBookmarkWithCodesTable(BookmarkConstants.PERSONAL_SUFFIX,
                            personalDto.getCdPersonSuffix(), CodesConstant.CSUFFIX2));*/

                    if (!ObjectUtils.isEmpty(personalDto.getCdPersonSuffix())) {
                        FormDataGroupDto personalSuffixGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PERSONAL_SUFFIX,
                                FormGroupsConstants.TMPLAT_PERSONAL);
                        List<BookmarkDto> bkPersonalSuffixList = new ArrayList<BookmarkDto>();

                        bkPersonalSuffixList.add(createBookmarkWithCodesTable(BookmarkConstants.PERSONAL_SUFFIX,personalDto.getCdPersonSuffix(), CodesConstant.CSUFFIX2));
                        personalSuffixGroupDto.setBookmarkDtoList(bkPersonalSuffixList);

                        personalGroupDtoList.add(personalSuffixGroupDto);

                    }
                    if (!ObjectUtils.isEmpty(personalDto.getCdStagePersRole())) {
                        FormDataGroupDto personalPerpGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PERSONAL_PERP,
                                FormGroupsConstants.TMPLAT_PERSONAL);
                        List<BookmarkDto> bkPersonalPerpList = new ArrayList<BookmarkDto>();

                        bkPersonalPerpList.add(createBookmarkWithCodesTable(BookmarkConstants.PERSONAL_PERP,personalDto.getCdStagePersRole(), CodesConstant.CROLEALL));
                        personalPerpGroupDto.setBookmarkDtoList(bkPersonalPerpList);

                        personalGroupDtoList.add(personalPerpGroupDto);
                    }

                    if (CodesConstant.CROLEALL_VC.equals(personalDto.getCdStagePersRole()) || CodesConstant.CROLEALL_VP.equals(personalDto.getCdStagePersRole())
                            || CodesConstant.CROLES_AP.equals(personalDto.getCdStagePersRole()) || CodesConstant.CROLEALL_DV.equals(personalDto.getCdStagePersRole())
                            || CodesConstant.CROLEALL_DB.equals(personalDto.getCdStagePersRole()) || CodesConstant.CROLEALL_DP.equals(personalDto.getCdStagePersRole())
                            || CodesConstant.CROLEALL_SP.equals(personalDto.getCdStagePersRole()))
                    {
                        bookmarkPersonalList.add(createBookmark(BookmarkConstants.REPRDO_PERSONAL_PERP+BookmarkConstants.UNDERSCORE+personalDto.getIdPerson(),
                                BookmarkConstants.REPRDO_PERSONAL_PERP+BookmarkConstants.YES+BookmarkConstants.UNDERSCORE+personalDto.getIdPerson()+BookmarkConstants.VALUE));
                    }
                    else
                    {
                        bookmarkPersonalList.add(createBookmark(BookmarkConstants.REPRDO_PERSONAL_PERP+BookmarkConstants.UNDERSCORE+personalDto.getIdPerson(),
                                BookmarkConstants.REPRDO_PERSONAL_PERP+BookmarkConstants.NO+BookmarkConstants.UNDERSCORE+personalDto.getIdPerson()+BookmarkConstants.VALUE));
                    }

                    if (null == personalDto.getDtPersonDeath())
                    {
                        bookmarkPersonalList.add(createBookmark(BookmarkConstants.REPRDO_DECEASED+BookmarkConstants.UNDERSCORE+personalDto.getIdPerson(),
                                BookmarkConstants.REPRDO_DECEASED+BookmarkConstants.NO+BookmarkConstants.UNDERSCORE+personalDto.getIdPerson()+BookmarkConstants.VALUE));
                    }
                    else
                    {
                        bookmarkPersonalList.add(createBookmark(BookmarkConstants.REPRDO_DECEASED+BookmarkConstants.UNDERSCORE+personalDto.getIdPerson(),
                                BookmarkConstants.REPRDO_DECEASED+BookmarkConstants.YES+BookmarkConstants.UNDERSCORE+personalDto.getIdPerson()+BookmarkConstants.VALUE));
                    }

                    bookmarkPersonalList.add(createBookmark(BookmarkConstants.UE_GROUPID,
                            personalDto.getIdPerson()));
                    personalGroupDto.setBookmarkDtoList(bookmarkPersonalList);
                    personalGroupDto.setFormDataGroupList(personalGroupDtoList);
                    // adding the subgroup as a list
                    formDataGroupList.add(personalGroupDto);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(prefillDto.getAssetInfoDtoList())) {
            for (PersonIncomeResourceDto assetInfoDto : prefillDto.getAssetInfoDtoList()) {
                FormDataGroupDto assetGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ASSET,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkAssetList = new ArrayList<BookmarkDto>();
                bookmarkAssetList.add(createBookmarkWithCodesTable(BookmarkConstants.ASSET_TYPE,
                        assetInfoDto.getCdIncRsrcType(), CodesConstant.CINCRSRC));

                bookmarkAssetList.add(createBookmark(BookmarkConstants.ASSET_AMT,
                        TypeConvUtil.convertToTwoDecimalPlace(assetInfoDto.getAmtIncRsrc())));

                bookmarkAssetList.add(createBookmark(BookmarkConstants.ASSET_DESC,
                        assetInfoDto.getIncRsrcDesc()));

                bookmarkAssetList.add(createBookmark(BookmarkConstants.UE_GROUPID,
                        assetInfoDto.getIdIncRsrc()));
                assetGroupDto.setBookmarkDtoList(bookmarkAssetList);
                // adding the subgroup as a list
                formDataGroupList.add(assetGroupDto);
            }
        }
        if (CollectionUtils.isNotEmpty(prefillDto.getAssetRealInfoDtoList())) {
            for (PersonIncomeResourceDto assetInfoDto : prefillDto.getAssetRealInfoDtoList()) {
                FormDataGroupDto assetGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ASSET_REAL,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkAssetList = new ArrayList<BookmarkDto>();
                bookmarkAssetList.add(createBookmarkWithCodesTable(BookmarkConstants.ASSET_REAL_TYPE,
                        assetInfoDto.getCdIncRsrcType(), CodesConstant.CINCRSRC));

                bookmarkAssetList.add(createBookmark(BookmarkConstants.ASSET_REAL_AMT,
                        TypeConvUtil.convertToTwoDecimalPlace(assetInfoDto.getAmtIncRsrc())));

                bookmarkAssetList.add(createBookmark(BookmarkConstants.ASSET_REAL_DESC,
                        assetInfoDto.getIncRsrcDesc()));

                bookmarkAssetList.add(createBookmark(BookmarkConstants.UE_GROUPID,
                        assetInfoDto.getIdIncRsrc()));

                assetGroupDto.setBookmarkDtoList(bookmarkAssetList);
                // adding the subgroup as a list
                formDataGroupList.add(assetGroupDto);
            }
        }

        if (CollectionUtils.isNotEmpty(prefillDto.getAssetPersonalInfoDtoList())) {
            for (PersonIncomeResourceDto assetInfoDto : prefillDto.getAssetPersonalInfoDtoList()) {
                FormDataGroupDto assetGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ASSET_PROPERTY,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> bookmarkAssetList = new ArrayList<BookmarkDto>();
                bookmarkAssetList.add(createBookmarkWithCodesTable(BookmarkConstants.ASSET_PROPERTY_TYPE,
                        assetInfoDto.getCdIncRsrcType(), CodesConstant.CINCRSRC));

                bookmarkAssetList.add(createBookmark(BookmarkConstants.ASSET_PROPERTY_AMT,
                        TypeConvUtil.convertToTwoDecimalPlace(assetInfoDto.getAmtIncRsrc())));

                bookmarkAssetList.add(createBookmark(BookmarkConstants.ASSET_PROPERTY_DESC,
                        assetInfoDto.getIncRsrcDesc()));

                bookmarkAssetList.add(createBookmark(BookmarkConstants.UE_GROUPID,
                        assetInfoDto.getIdIncRsrc()));

                assetGroupDto.setBookmarkDtoList(bookmarkAssetList);
                // adding the subgroup as a list
                formDataGroupList.add(assetGroupDto);
            }
        }
        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setFormDataGroupList(formDataGroupList);
        preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
        return preFillData;
    }

}