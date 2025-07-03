package us.tx.state.dfps.service.forms.util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.apsinhomecontact.dto.ApsInHomeContactSummaryDto;
import us.tx.state.dfps.service.common.*;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.common.utils.PersonUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;

import java.util.ArrayList;
import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Creates
 * prefill string to populate data on form October 22, 2021- 10:42:13 AM © 2017
 * Texas Department of Family and Protective Services
 * * **********  Change History *********************************
 * 05/06/2020 thompswa artf147748 : CPI June 2020 Project - adjustment for removal checklist data model change. Pre-release fixes artf152750
 */
@Component
public class ApsInHomeContactSummaryPrefillData extends DocumentServiceUtil {

    private static final Logger logger = Logger.getLogger(ApsInHomeContactSummaryPrefillData.class);

    @Autowired
    PersonUtil personUtil;
    @Autowired
    EventDao eventDao;
    @Autowired
    CaseUtils caseUtils;

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
        ApsInHomeContactSummaryDto prefillDto = (ApsInHomeContactSummaryDto) parentDtoobj;

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
        // CSEC01D
        if (!ObjectUtils.isEmpty(prefillDto.getEmpWorkerDto())) {
            bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.NOTIFY_FROM_NM_FIRST,
                    prefillDto.getEmpWorkerDto().getNmNameFirst()));
            bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.NOTIFY_FROM_NM_MIDDLE,
                    prefillDto.getEmpWorkerDto().getNmNameMiddle()));
            bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.NOTIFY_FROM_NM_LAST,
                    prefillDto.getEmpWorkerDto().getNmNameLast()));

            // parent group CFZCOOO
            if (StringUtils.isNotBlank(prefillDto.getEmpWorkerDto().getCdNameSuffix())) {
                formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
                        FormConstants.EMPTY_STRING));
                bookmarkNonFrmGrpList.add(createBookmarkWithCodesTable(BookmarkConstants.NOTIFY_FROM_NM_SUFFIX,
                        prefillDto.getEmpWorkerDto().getCdNameSuffix(), CodesConstant.CSUFFIX));
            }

            bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.NOTIFY_ADDR_STREET1,
                    prefillDto.getEmpWorkerDto().getAddrMailCodeStLn1()));

            bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.NOTIFY_ADDR_STREET2,
                    prefillDto.getEmpWorkerDto().getAddrMailCodeStLn2()));
            bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.NOTIFY_PHONE,
                    TypeConvUtil.formatPhone(prefillDto.getEmpWorkerDto().getNbrPhone())));
            bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.NOTIFY_EXTENSION,
                    prefillDto.getEmpWorkerDto().getNbrPhoneExtension()));
            bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.NOTIFY_ADDR_CITY,
                    prefillDto.getEmpWorkerDto().getAddrMailCodeCity()));
            bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.NOTIFY_ADDR_ZIP,
                    prefillDto.getEmpWorkerDto().getAddrMailCodeZip()));

        }
        // CSEC02D
        if (!ObjectUtils.isEmpty(prefillDto.getGenericCaseInfoDto())) {
            bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.NOTIFY_DATE,
                    DateUtils.stringDt(prefillDto.getGenericCaseInfoDto().getDtSysDtGenericSysdate())));
            bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.CONF_CASE_NAME,
                    prefillDto.getGenericCaseInfoDto().getNmCase()));
            bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.CONF_CASE_NUMBER,
                    prefillDto.getGenericCaseInfoDto().getIdCase()));
        }

        for (CaseInfoDto caseInfoDto : prefillDto.getCaseInfoPrincipalList()) {
            // parent group cfiv1001
            if(ServiceConstants.SELF.equals(caseInfoDto.getCdStagePersRelInt())){
                FormDataGroupDto victimGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_VICTIM,
                        FormConstants.EMPTY_STRING);
                List<FormDataGroupDto> victimGroupDtoList = new ArrayList<FormDataGroupDto>();
                List<BookmarkDto> bookmarkVictimList = new ArrayList<BookmarkDto>();

                bookmarkVictimList.add(createBookmark(BookmarkConstants.VICTIM_NAME, getFullName(
                        caseInfoDto.getNmNameLast(), caseInfoDto.getNmNameFirst(), caseInfoDto.getNmNameMiddle())));

                bookmarkVictimList.add(createBookmark(BookmarkConstants.VICTIM_DOB,
                        DateUtils.stringDt(caseInfoDto.getDtPersonBirth())));

                bookmarkVictimList.add(createBookmark(BookmarkConstants.VICTIM_SEX,  caseInfoDto.getCdPersonSex()));

                bookmarkVictimList.add(createBookmark(BookmarkConstants.PHONE_NUMBER,
                        TypeConvUtil.formatPhone(caseInfoDto.getNbrPersonPhone())));

                bookmarkVictimList.add(createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION,
                        caseInfoDto.getNbrPersonPhoneExtension()));

                bookmarkVictimList.add(createBookmark(BookmarkConstants.ADDR_LN_1,
                        caseInfoDto.getAddrPersAddrStLn1()));

                bookmarkVictimList.add(createBookmark(BookmarkConstants.ADDR_LN_2,
                        caseInfoDto.getAddrPersAddrStLn2()));

                bookmarkVictimList.add(createBookmark(BookmarkConstants.ADDR_CITY,
                        caseInfoDto.getAddrPersonAddrCity()));

                bookmarkVictimList.add(createBookmark(BookmarkConstants.ADDR_STATE,
                        caseInfoDto.getCdPersonAddrState()));

                bookmarkVictimList.add(createBookmark(BookmarkConstants.ADDR_ZIP,
                        caseInfoDto.getAddrPersonAddrZip()));

                victimGroupDto.setBookmarkDtoList(bookmarkVictimList);
                victimGroupDto.setFormDataGroupList(victimGroupDtoList);
                formDataGroupList.add(victimGroupDto);
            }
        }
        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setFormDataGroupList(formDataGroupList);
        preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
        return preFillData;
    }

    /**
     * To retrieve full name by last name, first name and middle name
     *
     * @param lastName
     * @param firstName
     * @param middleName
     * @return
     */
    private String getFullName(String lastName, String firstName, String middleName) {
        StringBuilder fullName = new StringBuilder();
        if (!ObjectUtils.isEmpty(lastName)) {
            fullName.append(lastName);
            fullName.append(ServiceConstants.COMMA);

        }
        if (!ObjectUtils.isEmpty(firstName)) {
            fullName.append(firstName);
            fullName.append(ServiceConstants.SPACE);

        }
        if (!ObjectUtils.isEmpty(middleName)) {
            fullName.append(middleName);

        }

        return fullName.toString();
    }

}