package us.tx.state.dfps.service.forms.util;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.dto.PersonAddressDto;
import us.tx.state.dfps.notiftoparentprofsp.dto.NotifToParentProfSpCpsDto;
import us.tx.state.dfps.service.common.*;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.DocumentServiceUtil;
import us.tx.state.dfps.service.legal.dto.PersonDetailsdoDto;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.workload.dto.AdminReviewDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PrimaryWorkerDto;
import us.tx.state.dfps.service.workload.dto.StageReviewDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component("notifToParentProfSpCpsPrefillData")
public class NotifToParentProfSpCpsPrefillData extends DocumentServiceUtil {

    private static final Logger logger = Logger.getLogger(NotifToParentProfSpCpsPrefillData.class);


    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentObj) {

        NotifToParentProfSpCpsDto prefillDto = (NotifToParentProfSpCpsDto) parentObj;
        List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
        List<BookmarkDto> bookmarkNonFormGroupList = new ArrayList<BookmarkDto>();

        if (ObjectUtils.isEmpty(prefillDto.getAdminReviewDto())) {
            prefillDto.setAdminReviewDto(new AdminReviewDto());
        }

        if (ObjectUtils.isEmpty(prefillDto.getEmployeePersPhNameDto())) {
            prefillDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
        }
        if (ObjectUtils.isEmpty(prefillDto.getPersonDetailsdoDtoList())) {
            prefillDto.setPersonDetailsdoDtoList(new ArrayList<PersonDetailsdoDto>());
        }

        if (ObjectUtils.isEmpty(prefillDto.getNameDetailDto())) {
            prefillDto.setNameDetailDto(new NameDetailDto());
        }
        if (ObjectUtils.isEmpty(prefillDto.getPersonAddressDto())) {
            prefillDto.setPersonAddressDto(new PersonAddressDto());
        }
        if (ObjectUtils.isEmpty(prefillDto.getPrimaryWorkerDto())) {
            prefillDto.setPrimaryWorkerDto(new PrimaryWorkerDto());
        }
        if (ObjectUtils.isEmpty(prefillDto.getStageReviewNoDto())) {
            prefillDto.setStageReviewNoDto(new ArrayList<StageReviewDto>());
        }
        if (ObjectUtils.isEmpty(prefillDto.getStageReviewYesDto())) {
            prefillDto.setStageReviewYesDto(new ArrayList<StageReviewDto>());
        }

        /*
        *code for ccf24o00 and group ccf24o03
        *CLSC65D
        **/
        for (StageReviewDto stageReviewDto : prefillDto.getStageReviewYesDto()) {
            FormDataGroupDto priorAllegGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIOR_ALLEGATIONS,
                    FormConstants.EMPTY_STRING);
            priorAllegGroupDto.setBookmarkDtoList(Arrays.asList(
                    createBookmarkWithCodesTable(BookmarkConstants.PRIOR_DISPOSTION, stageReviewDto.getCdAdminAllegDispostion(), CodesConstant.CDISPSTN),
                    createBookmarkWithCodesTable(BookmarkConstants.PRIOR_SEVERITY, stageReviewDto.getCdAdminAllegSeverity(), CodesConstant.CSEVERTY),
                    createBookmarkWithCodesTable(BookmarkConstants.PRIOR_ALLEGATIONS, stageReviewDto.getCdAdminAllegType(), CodesConstant.CABALTYP),
                    createBookmarkWithCodesTable(BookmarkConstants.VICTIM_NAME_SUFFIX, stageReviewDto.getCdNameSuffix(), CodesConstant.CSUFFIX),
                    createBookmark(BookmarkConstants.VICTIM_NAME_FIRST, stageReviewDto.getNmNameFirst()),
                    createBookmark(BookmarkConstants.VICTIM_NAME_LAST, stageReviewDto.getNmNameLast()),
                    createBookmark(BookmarkConstants.VICTIM_NAME_MIDDLE, stageReviewDto.getNmNameMiddle())
            ));
            formDataGroupList.add(priorAllegGroupDto);

        }

        /**
         *code for ccf24o00 and group ccf24o06
         *CLSC65D
         **/
        for (StageReviewDto stageReviewDto : prefillDto.getStageReviewNoDto()) {
            FormDataGroupDto currAllegGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CURRENT_ALLEGATIONS,
                    FormConstants.EMPTY_STRING);
            currAllegGroupDto.setBookmarkDtoList(Arrays.asList(
                    createBookmarkWithCodesTable(BookmarkConstants.PRIOR_DISPOSITION, stageReviewDto.getCdAdminAllegDispostion(), CodesConstant.CDISPSTN),
                    createBookmarkWithCodesTable(BookmarkConstants.PRIOR_SEVERITY, stageReviewDto.getCdAdminAllegSeverity(), CodesConstant.CSEVERTY),
                    createBookmarkWithCodesTable(BookmarkConstants.PRIOR_ALLEGATION, stageReviewDto.getCdAdminAllegType(), CodesConstant.CABALTYP),
                    createBookmarkWithCodesTable(BookmarkConstants.VICTIM_NAME_SUFFIX, stageReviewDto.getCdNameSuffix(), CodesConstant.CSUFFIX),
                    createBookmark(BookmarkConstants.VICTIM_NAME_FIRST, stageReviewDto.getNmNameFirst()),
                    createBookmark(BookmarkConstants.VICTIM_NAME_LAST, stageReviewDto.getNmNameLast()),
                    createBookmark(BookmarkConstants.VICTIM_NAME_MIDDLE, stageReviewDto.getNmNameMiddle())
            ));
            formDataGroupList.add(currAllegGroupDto);

        }

        /**
         *code for ccf24o00 and group ccf24o01
         *CSES63D
         **/
        if (CodesConstant.CARVTYPE_010.equals(prefillDto.getAdminReviewDto().getCdAdminRvAppealType())) {
            //no need to create FormDataGroupDto as it does not contain any child/inner fields that would be a part of Bookmark Constants.
            formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_REVIEW_TYPE_2,
                    FormConstants.EMPTY_STRING));
        }

        /**
         *code for ccf24o00 and group ccf24o02
         *CSES63D
         **/
        if (! CodesConstant.CARVTYPE_010.equals(prefillDto.getAdminReviewDto().getCdAdminRvAppealType())) {
            formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_REVIEW_TYPE_1,
                    FormConstants.EMPTY_STRING));
        }

        /**
         *code for ccf24o00 and group ccf24o04
         *CSES63D
         **/
        for (PersonDetailsdoDto personDetailsdoDto : prefillDto.getPersonDetailsdoDtoList()) {
            if (CodesConstant.CSEX_F.equals(personDetailsdoDto.getCCdPersonSex())) {
                FormDataGroupDto tempFormDataSex2 = createFormDataGroup(FormGroupsConstants.TMPLAT_SEX_2,
                        FormConstants.EMPTY_STRING);
                formDataGroupList.add(tempFormDataSex2);
            } else {
                //code for ccf24o00 and group ccf24o05
                FormDataGroupDto tempFormDataSex1 = createFormDataGroup(FormGroupsConstants.TMPLAT_SEX_1,
                        FormConstants.EMPTY_STRING);
                formDataGroupList.add(tempFormDataSex1);
            }
        }


        /*
         * Populating the non form group data into prefill data. !!bookmarks
         */
        //CLSC03D
        bookmarkNonFormGroupList.addAll(Arrays.asList(
                createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_TITLE, CodesConstant.CBRDTTLE_001, CodesConstant.CBRDTTLE),
                createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_NAME, CodesConstant.CBRDNAME_001, CodesConstant.CBRDNAME)));

        //CSEC34D
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT_ADDR_ST_1,
                prefillDto.getPersonAddressDto().getAddrPersAddrStLn1()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT_ADDR_ST_2,
                prefillDto.getPersonAddressDto().getAddrPersAddrStLn2()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT_ADDR_CITY,
                prefillDto.getPersonAddressDto().getAddrPersonAddrCity()));
        bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.PARENT_ADDR_STATE,
                prefillDto.getPersonAddressDto().getCdPersonAddrState(), CodesConstant.CSTATE));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT_ADDR_ZIP,
                prefillDto.getPersonAddressDto().getAddrPersonAddrZip()));


        //Added Parent[[PARENT_NAME_FIRST]] [[PARENT_NAME_MIDDLE]] [[PARENT_NAME_LAST]] [[PARENT_NAME_SUFFIX]]
        //Added Parent Default Data for editable fields
        //CSEC35D
        bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.PARENT2_NAME_SUFFIX,
                prefillDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX));
        bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.PARENT_NAME_SUFFIX,
                prefillDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX2));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT_NAME_FIRST,
                prefillDto.getNameDetailDto().getNmNameFirst()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT2_NAME_FIRST,
                prefillDto.getNameDetailDto().getNmNameFirst()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT_NAME_LAST,
                prefillDto.getNameDetailDto().getNmNameLast()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT2_NAME_LAST,
                prefillDto.getNameDetailDto().getNmNameLast()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT_NAME_MIDDLE,
                prefillDto.getNameDetailDto().getNmNameMiddle()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.PARENT2_NAME_MIDDLE,
                prefillDto.getNameDetailDto().getNmNameMiddle()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER_NAME_FIRST,
                prefillDto.getRequestorNameDetailDto().getNmNameFirst()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER_NAME_LAST,
                prefillDto.getRequestorNameDetailDto().getNmNameLast()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.REQUESTER_NAME_MIDDLE,
                prefillDto.getRequestorNameDetailDto().getNmNameMiddle()));
        bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.REQUESTER_NAME_SUFFIX,
                prefillDto.getRequestorNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX));


        //CSEC01D
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.NOTIFICATION_DATE,
                DateUtils.stringDt(prefillDto.getEmployeePersPhNameDto().getDtEmpTermination())));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER_PHONE,
                !ObjectUtils.isEmpty(prefillDto.getEmployeePersPhNameDto().getNbrPhone())
                        ? TypeConvUtil.formatPhone(prefillDto.getEmployeePersPhNameDto().getNbrPhone()) : ServiceConstants.EMPTY_STRING));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER_PHONE_EXTENSION,
                prefillDto.getEmployeePersPhNameDto().getNbrPhoneExtension()));
        bookmarkNonFormGroupList.add(createBookmarkWithCodesTable(BookmarkConstants.USER_NAME_SUFFIX,
                prefillDto.getEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER_NAME_FIRST,
                prefillDto.getEmployeePersPhNameDto().getNmNameFirst()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER_NAME_LAST,
                prefillDto.getEmployeePersPhNameDto().getNmNameLast()));
        bookmarkNonFormGroupList.add(createBookmark(BookmarkConstants.USER_NAME_MIDDLE,
                prefillDto.getEmployeePersPhNameDto().getNmNameMiddle()));


        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setFormDataGroupList(formDataGroupList);
        preFillData.setBookmarkDtoList(bookmarkNonFormGroupList);
        logger.info("Returning the prefillData:" + preFillData);
        return preFillData;

    }
}
