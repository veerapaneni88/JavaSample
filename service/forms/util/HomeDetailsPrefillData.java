package us.tx.state.dfps.service.forms.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.homedetails.dto.ChildrenPlacementInfoDto;
import us.tx.state.dfps.service.homedetails.dto.HomeDetailsDto;
import us.tx.state.dfps.service.homedetails.dto.HomeInfoDto;
import us.tx.state.dfps.service.homedetails.dto.HouseHoldMembersDto;

import java.util.ArrayList;
import java.util.List;

@Repository
public class HomeDetailsPrefillData extends DocumentServiceUtil {

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
        HomeDetailsDto homeDetailsDto = (HomeDetailsDto) parentDtoobj;

        if (ObjectUtils.isEmpty(homeDetailsDto.getHouseHoldMembersDtoList())) {
            homeDetailsDto.setHouseHoldMembersDtoList(new ArrayList<HouseHoldMembersDto>());
        }
        if (ObjectUtils.isEmpty(homeDetailsDto.getChildrenPlacementInfoDtoList())) {
            homeDetailsDto.setChildrenPlacementInfoDtoList(new ArrayList<ChildrenPlacementInfoDto>());
        }

        List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
        List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<BookmarkDto>();

        /**
         * Header Info Bookmark
         */
        if(!ObjectUtils.isEmpty(homeDetailsDto.getHomeInfoDtoList())) {
            bookmarkNonFormGrpList.addAll(prefillHeaderData(homeDetailsDto.getHomeInfoDtoList().get(0)));
            bookmarkNonFormGrpList.addAll(prefillHomeInfoData(formDataGroupList, homeDetailsDto.getHomeInfoDtoList().get(0)));
        }

        /**
         * Description: TMPLAT_FAD_GENERIC Group
         * SubGroups: TMPLAT_HOME_MEMB, TMPLAT_CURR_PLCMT
         */
        if (!ObjectUtils.isEmpty(homeDetailsDto)) {
            FormDataGroupDto homeInfoDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FAD_GENERIC,
                    FormConstants.EMPTY_STRING);
            List<BookmarkDto> homeInfoBookmarkList = new ArrayList<BookmarkDto>();
            List<FormDataGroupDto> sbGrpFormDataMembers = new ArrayList<FormDataGroupDto>();

            homeDetailsDto.getHomeInfoDtoList().stream()
                    .forEach(familyAddressDto -> sbGrpFormDataMembers.add(createFamilyAddressBookmark(familyAddressDto)));


            /**
             * TMPLAT_HOME_MEMB Bookmark
             * HOME MEMBERS List
             */
            homeDetailsDto.getHouseHoldMembersDtoList().stream()
                    .forEach(houseHoldMembersDto -> sbGrpFormDataMembers.add(createHouseMemberBookmark(houseHoldMembersDto)));

            /**
             * TMPLAT_CURR_PLCMT Bookmark
             * CHILDREN CURRENTLY IN PLACEMENT List
             */
            homeDetailsDto.getChildrenPlacementInfoDtoList().stream()
                    .forEach(childrenInfoDto -> sbGrpFormDataMembers.add(createChildrenBookmark(childrenInfoDto)));


            homeInfoDto.setBookmarkDtoList(homeInfoBookmarkList);
            homeInfoDto.setFormDataGroupList(sbGrpFormDataMembers);
            formDataGroupList.add(homeInfoDto);
        }

        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setFormDataGroupList(formDataGroupList);
        preFillData.setBookmarkDtoList(bookmarkNonFormGrpList);

        return preFillData;
    }

    private List<BookmarkDto> prefillHeaderData(HomeInfoDto homeInfoDto){
        List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<BookmarkDto>();
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

    private List<BookmarkDto> prefillFamilyAddressData(HomeInfoDto homeInfoDto){
        List<BookmarkDto> homeInfoBookmarkList = new ArrayList<BookmarkDto>();
        if (null != homeInfoDto) {
            homeInfoBookmarkList.add(createBookmark(BookmarkConstants.STREET_LN1,
                    homeInfoDto.getStreetLine1()));
            homeInfoBookmarkList.add(createBookmark(BookmarkConstants.STREET_LN2,
                    homeInfoDto.getStreetLine2()));
            homeInfoBookmarkList.add(createBookmark(BookmarkConstants.CITY,
                    homeInfoDto.getCity()));
            homeInfoBookmarkList.add(createBookmarkWithCodesTable(BookmarkConstants.COUNTY,
                    homeInfoDto.getCounty(), CodesConstant.CCOUNT));
            homeInfoBookmarkList.add(createBookmarkWithCodesTable(BookmarkConstants.STATE,
                    homeInfoDto.getState(), CodesConstant.CSTATE));
            homeInfoBookmarkList.add(createBookmark(BookmarkConstants.ZIP,
                    homeInfoDto.getZipCode()));
        }
        return homeInfoBookmarkList;
    }

    private List<BookmarkDto> prefillHomeInfoData(List<FormDataGroupDto> formDataGroupList, HomeInfoDto homeInfoDto){
        List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<BookmarkDto>();
        if (null != homeInfoDto) {
            bookmarkNonFormGrpList.add(createBookmarkWithCodesTable(BookmarkConstants.HOME_CATEGORY,
                    homeInfoDto.getHomeCategory(), CodesConstant.CFACATEG));
            bookmarkNonFormGrpList.add(createBookmarkWithCodesTable(BookmarkConstants.HOME_TYPE_ONE,
                    homeInfoDto.getHomeType1(), CodesConstant.CFAHMTYP));
            bookmarkNonFormGrpList.add(createBookmarkWithCodesTable(BookmarkConstants.HOME_TYPE_TWO,
                    homeInfoDto.getHomeType2(), CodesConstant.CFAHMTYP));
            bookmarkNonFormGrpList.add(createBookmarkWithCodesTable(BookmarkConstants.HOME_TYPE_THREE,
                    homeInfoDto.getHomeType3(), CodesConstant.CFAHMTYP));
            bookmarkNonFormGrpList.add(createBookmarkWithCodesTable(BookmarkConstants.HOME_TYPE_FOUR,
                    homeInfoDto.getHomeType4(), CodesConstant.CFAHMTYP));
            bookmarkNonFormGrpList.add(createBookmarkWithCodesTable(BookmarkConstants.HOME_TYPE_FIVE,
                    homeInfoDto.getHomeType5(), CodesConstant.CFAHMTYP));
            bookmarkNonFormGrpList.add(createBookmarkWithCodesTable(BookmarkConstants.HOME_TYPE_SIX,
                    homeInfoDto.getHomeType6(), CodesConstant.CFAHMTYP));
            bookmarkNonFormGrpList.add(createBookmarkWithCodesTable(BookmarkConstants.HOME_TYPE_SEVEN,
                    homeInfoDto.getHomeType7(), CodesConstant.CFAHMTYP));
            bookmarkNonFormGrpList.add(createBookmarkWithCodesTable(BookmarkConstants.HOME_MARITAL,
                    homeInfoDto.getMaritalStatus(), CodesConstant.CFAMSTRC));
            if(StringUtils.isNotBlank(homeInfoDto.getStudyType()) && homeInfoDto.getStudyType().equals("T")) {
                formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_HOME_INDIV_TRAIN,
                        FormConstants.EMPTY_STRING));
            }else {
                formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_HOME_GROUP_TRAIN,
                        FormConstants.EMPTY_STRING));
            }
        }
        return bookmarkNonFormGrpList;
    }

    private FormDataGroupDto createChildrenBookmark(ChildrenPlacementInfoDto childrenInfoDto) {
        FormDataGroupDto childrenGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CURR_PLCMT,
                FormConstants.EMPTY_STRING);
        List<BookmarkDto> bookmarkChildrenList = new ArrayList<BookmarkDto>();
        bookmarkChildrenList.add(createBookmark(BookmarkConstants.CHILD_NAME_FIRST,
                childrenInfoDto.getFirstName()));
        bookmarkChildrenList.add(createBookmark(BookmarkConstants.CHILD_NAME_MIDDLE,
                childrenInfoDto.getMiddleName()));
        bookmarkChildrenList.add(createBookmark(BookmarkConstants.CHILD_NAME_LAST,
                childrenInfoDto.getLastName()));
        bookmarkChildrenList.add(createBookmark(BookmarkConstants.CHILD_DOB,
                DateUtils.stringDt(childrenInfoDto.getDateOfBirth())));
        bookmarkChildrenList.add(createBookmarkWithCodesTable(BookmarkConstants.CHILD_LIV_ARR,
                childrenInfoDto.getLivingArrangement(), CodesConstant.CPLLAFRM));
        bookmarkChildrenList.add(createBookmark(BookmarkConstants.CHILD_PLCMT_DT,
                DateUtils.stringDt(childrenInfoDto.getPlacementDate())));
        bookmarkChildrenList.add(createBookmarkWithCodesTable(BookmarkConstants.CHILD_AUTH_LOC,
                childrenInfoDto.getAuthorizedLoc(), CodesConstant.CATHPLOC));
        bookmarkChildrenList.add(createBookmark(BookmarkConstants.DATE_PLOC_START,
                DateUtils.stringDt(childrenInfoDto.getAuthorizedPlocStartDate())));
        if (StringUtils.isNotBlank(childrenInfoDto.getNameSuffix())) {
            createNameSuffixBookmark(childrenGroupDto, BookmarkConstants.CHILD_NAME_SUFFIX, childrenInfoDto.getNameSuffix());
        }
        childrenGroupDto.setBookmarkDtoList(bookmarkChildrenList);
        return childrenGroupDto;
    }

    private FormDataGroupDto createFamilyAddressBookmark(HomeInfoDto homeInfoDto) {
        FormDataGroupDto familyAddressGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HOME_ADDR,
                FormConstants.EMPTY_STRING);
        List<BookmarkDto> bookmarkFamilyAddressList = new ArrayList<BookmarkDto>();
        bookmarkFamilyAddressList.addAll(prefillFamilyAddressData(homeInfoDto));

        familyAddressGroupDto.setBookmarkDtoList(bookmarkFamilyAddressList);
        return familyAddressGroupDto;
    }

    private FormDataGroupDto createHouseMemberBookmark(HouseHoldMembersDto houseHoldMembersDto) {
        FormDataGroupDto houseHoldMembersGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HOME_MEMB,
                FormConstants.EMPTY_STRING);
        List<BookmarkDto> bookmarkHouseHoldMembersList = new ArrayList<BookmarkDto>();
        bookmarkHouseHoldMembersList.add(createBookmark(BookmarkConstants.HOME_MEMB_NAME_FIRST,
                houseHoldMembersDto.getFirstName()));
        bookmarkHouseHoldMembersList.add(createBookmark(BookmarkConstants.HOME_MEMB_NAME_MIDDLE,
                houseHoldMembersDto.getMiddleName()));
        bookmarkHouseHoldMembersList.add(createBookmark(BookmarkConstants.HOME_MEMB_NAME_LAST,
                houseHoldMembersDto.getLastName()));
        bookmarkHouseHoldMembersList.add(createBookmark(BookmarkConstants.HOME_MEMB_DOB,
                DateUtils.stringDt(houseHoldMembersDto.getDateOfBirth())));
        bookmarkHouseHoldMembersList.add(createBookmarkWithCodesTable(BookmarkConstants.HOME_MEMB_ROLE,
                houseHoldMembersDto.getRoleInHome(), CodesConstant.CFAHROLS));
        if (StringUtils.isNotBlank(houseHoldMembersDto.getNameSuffix())) {
            createNameSuffixBookmark(houseHoldMembersGroupDto, BookmarkConstants.PERSON_NAME_SUFFIX, houseHoldMembersDto.getNameSuffix());
        }
        houseHoldMembersGroupDto.setBookmarkDtoList(bookmarkHouseHoldMembersList);
        return houseHoldMembersGroupDto;
    }

    private void createNameSuffixBookmark(FormDataGroupDto formGroupDto, String bookMarkName, String suffix) {
        FormDataGroupDto suffixCommaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
                FormConstants.EMPTY_STRING);
        List<BookmarkDto> bookmarkMemberCommaList = new ArrayList<BookmarkDto>();
        List<FormDataGroupDto> nameSuffixDto = new ArrayList<FormDataGroupDto>();
        bookmarkMemberCommaList.add(createBookmarkWithCodesTable(bookMarkName, suffix, CodesConstant.CSUFFIX2));
        suffixCommaGroupDto.setBookmarkDtoList(bookmarkMemberCommaList);
        nameSuffixDto.add(suffixCommaGroupDto);
        formGroupDto.setFormDataGroupList(nameSuffixDto);
    }
}