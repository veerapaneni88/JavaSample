package us.tx.state.dfps.service.forms.util;

import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.fahomestudy.dto.FAHomeStudyDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.homedetails.dto.HomeInfoDto;
import us.tx.state.dfps.service.homedetails.dto.HouseHoldMembersDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FAHomeStudyPrefillData extends DocumentServiceUtil {

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoObj) {

        FAHomeStudyDto homeStudyDto = (FAHomeStudyDto) parentDtoObj;

        if (ObjectUtils.isEmpty(homeStudyDto.getHouseHoldMembers())) {
            homeStudyDto.setHouseHoldMembers(new ArrayList<>());
        }

        List<FormDataGroupDto> formDataGroupList = new ArrayList<>();
        List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<>();

        bookmarkNonFormGrpList.addAll(prefillHeaderData(homeStudyDto.getHomeInfoDto()));
        if (!ObjectUtils.isEmpty(homeStudyDto.getHomeInfoDto())) {
            bookmarkNonFormGrpList.addAll(prefillAddressData(homeStudyDto.getHomeInfoDto()));
            bookmarkNonFormGrpList.add(createBookmark(BookmarkConstants.HOME_ANN_INCOME,
                    homeStudyDto.getHomeInfoDto().getAnnualIncome()));
        }
        if (!ObjectUtils.isEmpty(homeStudyDto.getHouseHoldMembers())) {
            List<HouseHoldMembersDto> headHouseHolds = homeStudyDto.getHouseHoldMembers().stream().filter(houseHoldMembersDto -> null != houseHoldMembersDto.getRoleInHome() && "1PR".equalsIgnoreCase(houseHoldMembersDto.getRoleInHome())).collect(Collectors.toList());
            List<HouseHoldMembersDto> otherHouseHolds = homeStudyDto.getHouseHoldMembers().stream().filter(houseHoldMembersDto -> null == houseHoldMembersDto.getRoleInHome() || !"1PR".equalsIgnoreCase(houseHoldMembersDto.getRoleInHome())).collect(Collectors.toList());
            List<FormDataGroupDto> sbGrpFormDataMembers = new ArrayList<>();
            headHouseHolds.stream()
                    .forEach(houseHoldMembersDto -> sbGrpFormDataMembers.add(createHeadMembersBookmark(houseHoldMembersDto, homeStudyDto.getHomeInfoDto(), FormGroupsConstants.TMPLAT_HEAD_HOUSEHOLD)));
            otherHouseHolds.stream()
                    .forEach(houseHoldMembersDto -> sbGrpFormDataMembers.add(createOtherMembersBookmark(houseHoldMembersDto, FormGroupsConstants.TMPLAT_OTHER_HOUSEHOLD)));
            formDataGroupList.addAll(sbGrpFormDataMembers);
        }
        if (!ObjectUtils.isEmpty(homeStudyDto.getInquiryDateDto())) {
            bookmarkNonFormGrpList.add(createBookmark(BookmarkConstants.INQ_DATE,
                    DateUtils.stringDt(homeStudyDto.getInquiryDateDto().getEffectiveDate())));
        }
        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setFormDataGroupList(formDataGroupList);
        preFillData.setBookmarkDtoList(bookmarkNonFormGrpList);
        return preFillData;
    }

    private List<BookmarkDto> prefillHeaderData(HomeInfoDto homeInfoDto) {
        List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<>();
        if (null != homeInfoDto) {
            bookmarkNonFormGrpList.add(createBookmark(BookmarkConstants.HOME_NAME,
                    homeInfoDto.getResourceName()));
            bookmarkNonFormGrpList.add(createBookmark(BookmarkConstants.FACILITY_ID,
                    homeInfoDto.getIdResource()));
            bookmarkNonFormGrpList.add(createBookmark(BookmarkConstants.CASE_ID,
                    homeInfoDto.getIdCase()));
        }
        return bookmarkNonFormGrpList;
    }

    private List<BookmarkDto> prefillAddressData(HomeInfoDto homeInfoDto) {
        List<BookmarkDto> homeInfoBookmarkList = new ArrayList<>();
        if (null != homeInfoDto) {
            homeInfoBookmarkList.add(createBookmark(BookmarkConstants.HD_HOME_NAME,
                    homeInfoDto.getResourceName()));
            homeInfoBookmarkList.add(createBookmark(BookmarkConstants.HD_STREET_LN1,
                    homeInfoDto.getStreetLine1()));
            homeInfoBookmarkList.add(createBookmark(BookmarkConstants.HD_STREET_LN2,
                    homeInfoDto.getStreetLine2()));
            homeInfoBookmarkList.add(createBookmark(BookmarkConstants.HD_CITY,
                    homeInfoDto.getCity()));
            homeInfoBookmarkList.add(createBookmarkWithCodesTable(BookmarkConstants.HD_COUNTY,
                    homeInfoDto.getCounty(), CodesConstant.CCOUNT));
            homeInfoBookmarkList.add(createBookmarkWithCodesTable(BookmarkConstants.HD_STATE,
                    homeInfoDto.getState(), CodesConstant.CSTATE));
            homeInfoBookmarkList.add(createBookmark(BookmarkConstants.HD_ZIP,
                    homeInfoDto.getZipCode()));
            homeInfoBookmarkList.add(createBookmark(BookmarkConstants.HD_PHONE,
                    homeInfoDto.getPhoneNumber()));
            homeInfoBookmarkList.add(createBookmark(BookmarkConstants.HD_PHONE_EXTENSION,
                    homeInfoDto.getPhoneNumberExtension()));
        }
        return homeInfoBookmarkList;
    }

    private FormDataGroupDto createHeadMembersBookmark(HouseHoldMembersDto houseHoldMembersDto, HomeInfoDto homeInfoDto, String template) {
        FormDataGroupDto houseHoldMembersGroupDto = createFormDataGroup(template,
                FormConstants.EMPTY_STRING);
        List<BookmarkDto> bookmarkHouseHoldMembersList = new ArrayList<>();
        bookmarkHouseHoldMembersList.add(createBookmark(BookmarkConstants.TMP1_NAME_FIRST,
                houseHoldMembersDto.getFirstName()));
        bookmarkHouseHoldMembersList.add(createBookmark(BookmarkConstants.TMP1_NAME_MIDDLE,
                houseHoldMembersDto.getMiddleName()));
        bookmarkHouseHoldMembersList.add(createBookmark(BookmarkConstants.TMP1_NAME_LAST,
                houseHoldMembersDto.getLastName()));
        bookmarkHouseHoldMembersList.add(createBookmark(BookmarkConstants.TMP1_AGE,
                DateUtils.calculatePersonsAgeInYears(houseHoldMembersDto.getDateOfBirth(), new Date())));
        bookmarkHouseHoldMembersList.add(createBookmark(BookmarkConstants.TMP1_DOB,
                DateUtils.stringDt(houseHoldMembersDto.getDateOfBirth())));
        bookmarkHouseHoldMembersList.add(createBookmarkWithCodesTable(BookmarkConstants.TMP1_SEX,
                houseHoldMembersDto.getPersonSex(), CodesConstant.CSEX));
        bookmarkHouseHoldMembersList.add(createBookmarkWithCodesTable(BookmarkConstants.TMP1_ETHNICITY,
                houseHoldMembersDto.getEthinicity(), CodesConstant.CETHNIC));
        bookmarkHouseHoldMembersList.add(createBookmarkWithCodesTable(BookmarkConstants.TMP1_LANGUAGE,
                houseHoldMembersDto.getLanguage(), CodesConstant.CLANG));
        bookmarkHouseHoldMembersList.add(createBookmarkWithCodesTable(BookmarkConstants.TMP1_EDUCATION,
                houseHoldMembersDto.getEducation(), CodesConstant.CEDUCLVL));
        bookmarkHouseHoldMembersList.add(createBookmark(BookmarkConstants.TMP1_OCCUPATION,
                houseHoldMembersDto.getOccupation()));
        houseHoldMembersGroupDto.setBookmarkDtoList(bookmarkHouseHoldMembersList);
        createGroupBookMarks(houseHoldMembersGroupDto, houseHoldMembersDto, homeInfoDto);
        return houseHoldMembersGroupDto;
    }

    private void createGroupBookMarks(FormDataGroupDto formGroupDto, HouseHoldMembersDto houseHoldMembersDto, HomeInfoDto homeInfoDto) {
        List<FormDataGroupDto> groupBookMarks = new ArrayList<>();

        FormDataGroupDto maritalStatusGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_MARITAL_STATUS,
                FormConstants.EMPTY_STRING);
        List<BookmarkDto> maritalStatusList = new ArrayList<>();
        maritalStatusList.add(createBookmarkWithCodesTable(BookmarkConstants.TMP4_MARITAL_STATUS, homeInfoDto.getMaritalStatus(), CodesConstant.CFAMSTRC));
        maritalStatusGroup.setBookmarkDtoList(maritalStatusList);

        FormDataGroupDto marriageDateGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_MARRIAGE_DATE,
                FormConstants.EMPTY_STRING);
        List<BookmarkDto> marriageDateList = new ArrayList<>();
        marriageDateList.add(createBookmark(BookmarkConstants.TMP3_MARRIAGE_DATE, DateUtils.stringDt(homeInfoDto.getDateMarried())));
        marriageDateGroup.setBookmarkDtoList(marriageDateList);

        groupBookMarks.add(createNameSuffixDto(FormGroupsConstants.TMPLAT_COMMA, BookmarkConstants.TMP1_NAME_SUFFIX, houseHoldMembersDto.getNameSuffix()));
        groupBookMarks.add(maritalStatusGroup);
        groupBookMarks.add(marriageDateGroup);

        formGroupDto.setFormDataGroupList(groupBookMarks);
    }

    private FormDataGroupDto createOtherMembersBookmark(HouseHoldMembersDto houseHoldMembersDto, String template) {
        FormDataGroupDto houseHoldMembersGroupDto = createFormDataGroup(template,
                FormConstants.EMPTY_STRING);
        List<BookmarkDto> bookmarkHouseHoldMembersList = new ArrayList<>();
        bookmarkHouseHoldMembersList.add(createBookmark(BookmarkConstants.TMP2_NAME_FIRST,
                houseHoldMembersDto.getFirstName()));
        bookmarkHouseHoldMembersList.add(createBookmark(BookmarkConstants.TMP2_NAME_MIDDLE,
                houseHoldMembersDto.getMiddleName()));
        bookmarkHouseHoldMembersList.add(createBookmark(BookmarkConstants.TMP2_NAME_LAST,
                houseHoldMembersDto.getLastName()));
        bookmarkHouseHoldMembersList.add(createBookmark(BookmarkConstants.TMP2_AGE,
                DateUtils.calculatePersonsAgeInYears(houseHoldMembersDto.getDateOfBirth(), new Date())));
        bookmarkHouseHoldMembersList.add(createBookmark(BookmarkConstants.TMP2_DOB,
                DateUtils.stringDt(houseHoldMembersDto.getDateOfBirth())));
        bookmarkHouseHoldMembersList.add(createBookmarkWithCodesTable(BookmarkConstants.TMP2_SEX,
                houseHoldMembersDto.getPersonSex(), CodesConstant.CSEX));
        bookmarkHouseHoldMembersList.add(createBookmarkWithCodesTable(BookmarkConstants.TMP2_ETHNIC,
                houseHoldMembersDto.getEthinicity(), CodesConstant.CETHNIC));
        bookmarkHouseHoldMembersList.add(createBookmarkWithCodesTable(BookmarkConstants.TMP2_EDUCATION,
                houseHoldMembersDto.getEducation(), CodesConstant.CEDUCLVL));
        bookmarkHouseHoldMembersList.add(createBookmark(BookmarkConstants.TMP2_OCCUPATION,
                houseHoldMembersDto.getOccupation()));
        bookmarkHouseHoldMembersList.add(createBookmarkWithCodesTable(BookmarkConstants.TMP2_ROLE_IN_HOME,
                houseHoldMembersDto.getRoleInHome(), CodesConstant.CFAHROLS));
        houseHoldMembersGroupDto.setBookmarkDtoList(bookmarkHouseHoldMembersList);
        List<FormDataGroupDto> groupBookMarks = new ArrayList<>();
        groupBookMarks.add(createNameSuffixDto(FormGroupsConstants.TMPLAT_COMMA, BookmarkConstants.TMP1_NAME_SUFFIX, houseHoldMembersDto.getNameSuffix()));
        houseHoldMembersGroupDto.setFormDataGroupList(groupBookMarks);
        return houseHoldMembersGroupDto;
    }

    private FormDataGroupDto createNameSuffixDto(String formGroupConstant, String BookmarkConstant, String suffix) {
        FormDataGroupDto suffixCommaGroupDto = createFormDataGroup(formGroupConstant,
                FormConstants.EMPTY_STRING);
        List<BookmarkDto> bookmarkMemberCommaList = new ArrayList<>();
        bookmarkMemberCommaList.add(createBookmarkWithCodesTable(BookmarkConstant, suffix, CodesConstant.CSUFFIX2));
        suffixCommaGroupDto.setBookmarkDtoList(bookmarkMemberCommaList);
        return suffixCommaGroupDto;
    }
}
