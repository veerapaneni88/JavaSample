package us.tx.state.dfps.service.forms.util;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.KinHomeAssessmentDetail;
import us.tx.state.dfps.common.domain.KinHomeAssessmentDetailComments;
import us.tx.state.dfps.common.domain.PlacementAudit;
import us.tx.state.dfps.kincaregiverhomedetails.dto.CvsKinCaregiverHomeAssmtDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.PersonSearchDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * service-business - Kinship CareGiver Home Assessment (CVSKINHOMEASSESSMENT)
 * 02/20/2025 thompswa ppm84014 : Prefill Service for CVSKINHOMEASSESSMENT
 */
@Component
public class CvsKinCaregiverHomeAssmtPrefillData extends DocumentServiceUtil  {

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

        CvsKinCaregiverHomeAssmtDto prefillDto = (CvsKinCaregiverHomeAssmtDto) parentDtoobj;

        if (null == prefillDto.getCaseCaregiverInfo()) {
            prefillDto.setCaseCaregiverInfo( new KinHomeAssessmentDetail());
        }
        KinHomeAssessmentDetail homeInfo = prefillDto.getCaseCaregiverInfo();

        if (null == prefillDto.getKinPlacementList()) {
            prefillDto.setKinPlacementList( new ArrayList<PlacementAudit>());
        }
        List<PlacementAudit>  plcmtList = prefillDto.getKinPlacementList();

        if (null == prefillDto.getKinNameList()) {
            prefillDto.setKinNameList( new ArrayList<PersonSearchDto>());
        }

        if (null == prefillDto.getDncmComments()) {
            prefillDto.setDncmComments( new KinHomeAssessmentDetailComments());
        }

        if (null == prefillDto.getGlcmComments()) {
            prefillDto.setGlcmComments( new KinHomeAssessmentDetailComments());
        }

        List<BookmarkDto> bookmarkDtoList = new ArrayList<BookmarkDto>();
        List<FormDataGroupDto> formDataGroupDtoList = new ArrayList<FormDataGroupDto>();

        /**
         * Populating the non form group data into prefill data.
         */
        bookmarkDtoList.addAll(Arrays.asList(
                createBookmark(BookmarkConstants.CAREGIVERNAME, getName(homeInfo.getKinCaregiverId(), prefillDto.getKinNameList()))
                , createBookmark(BookmarkConstants.CAREGIVERPID, homeInfo.getKinCaregiverId())
                , createBookmark(BookmarkConstants.CASE_ID, homeInfo.getCaseId())
                , createBookmark(BookmarkConstants.ASSMT_DTLABEL,
                        ServiceConstants.Y.equals(homeInfo.getAutoPopulated())
                                ? AUTO : MANUAL)
                , createBookmark(BookmarkConstants.ASSMT_DATE,
                        ServiceConstants.Y.equals(homeInfo.getAutoPopulated())
                                ? TypeConvUtil.formDateFormat( homeInfo.getServiceAuthorizedDate())
                                : TypeConvUtil.formDateFormat( homeInfo.getDtHmAssmtSubmitted()))
                , createBookmarkWithCodesTable(BookmarkConstants.HOME_APPROVAL, homeInfo.getCdApproved(), CodesConstant.KINAPRSN)
                , createBookmark(BookmarkConstants.YES_NO,
                        ServiceConstants.N.equals(homeInfo.getCdApproved())
                                ? DENIAL : APPROVAL)
                , createBookmark(BookmarkConstants.DT_APPROVAL,
                        ServiceConstants.N.equals(homeInfo.getCdApproved())
                                ? TypeConvUtil.formDateFormat( homeInfo.getDeniedDate())
                                : TypeConvUtil.formDateFormat( homeInfo.getApprovedDate()))
                , createBookmark(BookmarkConstants.COMMENTS, prefillDto.getGlcmComments().getComments())
        ));
        for (PlacementAudit plcmtChild : plcmtList) {
            FormDataGroupDto childGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD, ServiceConstants.EMPTY_STRING);
            formDataGroupDtoList.add(childGroup);
            childGroup.setBookmarkDtoList(Arrays.asList(
                    createBookmark(BookmarkConstants.CHILD_NAME, getName(plcmtChild.getIdPlcmtChild(), prefillDto.getKinNameList()))
                    , createBookmark(BookmarkConstants.CHILD_PLCMT_DT, TypeConvUtil.formDateFormat(plcmtChild.getDtPlcmtStart()))
            ));
        }
        if (ServiceConstants.N.equals(homeInfo.getCdApproved())) {
            FormDataGroupDto denialGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_DENIAL, ServiceConstants.EMPTY_STRING);
            formDataGroupDtoList.add(denialGroup);
            denialGroup.setBookmarkDtoList(Arrays.asList(
                    createBookmark(BookmarkConstants.DENIAL_CRIMHIST,
                            ServiceConstants.Y.equals(homeInfo.getIndCrimHist())
                                    ? CHECKED : ServiceConstants.EMPTY_STRING)
                    , createBookmark(BookmarkConstants.DENIAL_CRIMMARGIN,
                            ServiceConstants.Y.equals(homeInfo.getIndCrimHist())
                                    ? MARGINTOP25 : ServiceConstants.EMPTY_STRING)
                    , createBookmark(BookmarkConstants.DENIAL_CRIMEQ1,
                            ServiceConstants.Y.equals(homeInfo.getIndCrimHist())
                                    ? CRIMEQ1 : ServiceConstants.EMPTY_STRING)
                    , createBookmarkWithCodesTable(BookmarkConstants.DENIAL_CRIMEVAL,
                            ServiceConstants.Y.equals(homeInfo.getIndCrimHist())
                                    ? homeInfo.getIndCrimHistKinSafetyEval() : ServiceConstants.EMPTY_STRING, CodesConstant.CINVACAN)
                    , createBookmark(BookmarkConstants.DENIAL_CRIMEQ2,
                            ServiceConstants.Y.equals(homeInfo.getIndCrimHist())
                                    ? CRIMEQ2 : ServiceConstants.EMPTY_STRING)
                    , createBookmarkWithCodesTable(BookmarkConstants.DENIAL_CRIMADDENDUM,
                            ServiceConstants.Y.equals(homeInfo.getIndCrimHist())
                                    ? homeInfo.getIndCrimHistAddendum() : ServiceConstants.EMPTY_STRING, CodesConstant.CINVACAN)
                    , createBookmark(BookmarkConstants.DENIAL_ABUSENEGLECT,
                            ServiceConstants.Y.equals(homeInfo.getIndAbuseNeglectHist())
                                    ? CHECKED : ServiceConstants.EMPTY_STRING)
                    , createBookmark(BookmarkConstants.DENIAL_ABUSEMARGIN,
                            ServiceConstants.Y.equals(homeInfo.getIndAbuseNeglectHist())
                                    ? MARGINTOP25 : ServiceConstants.EMPTY_STRING)
                    , createBookmark(BookmarkConstants.DENIAL_ANHIST1,
                            ServiceConstants.Y.equals(homeInfo.getIndAbuseNeglectHist())
                                    ? ANHIST1 : ServiceConstants.EMPTY_STRING)
                    , createBookmarkWithCodesTable(BookmarkConstants.DENIAL_ABUSEEVAL,
                            ServiceConstants.Y.equals(homeInfo.getIndAbuseNeglectHist())
                                    ? homeInfo.getIndAbuseNeglKinSafetyEval() : ServiceConstants.EMPTY_STRING, CodesConstant.CINVACAN)
                    , createBookmark(BookmarkConstants.DENIAL_ANHIST2,
                            ServiceConstants.Y.equals(homeInfo.getIndAbuseNeglectHist())
                                    ? ANHIST2 : ServiceConstants.EMPTY_STRING)
                    , createBookmarkWithCodesTable(BookmarkConstants.DENIAL_ABUSEADDENDUM,
                            ServiceConstants.Y.equals(homeInfo.getIndAbuseNeglectHist())
                                    ? homeInfo.getIndAbuseNeglAddendum() : ServiceConstants.EMPTY_STRING, CodesConstant.CINVACAN)
                    , createBookmark(BookmarkConstants.DENIAL_OTHER,
                            ServiceConstants.Y.equals(homeInfo.getIndOtherRsn())
                                    ? CHECKED : ServiceConstants.EMPTY_STRING)
                    , createBookmark(BookmarkConstants.DENIAL_OTHERMARGIN,
                            ServiceConstants.Y.equals(homeInfo.getIndOtherRsn())
                                    ? MARGINTOP25 : ServiceConstants.EMPTY_STRING)
                    , createBookmark(BookmarkConstants.DENIAL_REASONLABEL,
                            ServiceConstants.Y.equals(homeInfo.getIndOtherRsn())
                                    ? REASONLABEL : ServiceConstants.EMPTY_STRING)
                    , createBookmark(BookmarkConstants.DENIAL_REASON,
                           ServiceConstants.Y.equals(homeInfo.getIndOtherRsn())
                                   ? prefillDto.getDncmComments().getComments() : ServiceConstants.EMPTY_STRING)
                    ));
        }


        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setFormDataGroupList(formDataGroupDtoList);
        preFillData.setBookmarkDtoList(bookmarkDtoList);
        return preFillData;
    }


    /**
     * return full name as last, first, middle initial string(or "Unknown").
     */
    private String getName( Long personId, List<PersonSearchDto> kinNameList )
    {
        StringBuilder name = new StringBuilder();
        for (PersonSearchDto person : kinNameList) {
            if (personId.equals(person.getIdPerson())) {
                name.append(TypeConvUtil.getNameWithSuffix(
                        person.getFirstName(),
                        person.getMiddleName(),
                        person.getLastName(),
                        ServiceConstants.EMPTY_STRING,
                        true));
                break;
            }

        }
        return name.toString();
    }
    private static final String APPROVAL = "Approval";
    private static final String DENIAL = "Denial";
    private static final String CHECKED = "checked";
    private static final String AUTO = "Date Service Authorization entered in IMPACT 2.0:";
    private static final String MANUAL = "Date Home Assessment Submitted:";
    private static final String MARGINTOP25 = " marginTop25";
    private static final String CRIMEQ1 = "Is a Kinship Safety Evaluation required to address Criminal History?:&nbsp;&nbsp;";
    private static final String CRIMEQ2 = "Is an Addendum required to address Criminal History?:&nbsp;&nbsp;";
    private static final String ANHIST1 = "Is a Kinship Safety Evaluation required to address DFPS Abuse/Neglect History?:&nbsp;&nbsp;";
    private static final String ANHIST2 = "Is an Addendum required to address DFPS Abuse/Neglect History?:&nbsp;&nbsp;";
    private static final String REASONLABEL = "Reason for Denial:";
}
