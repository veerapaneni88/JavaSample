package us.tx.state.dfps.service.forms.util;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.apscontactlognarrative.dao.ApsContactLogNarrativeDao;
import us.tx.state.dfps.service.apscontactlognarrative.dto.APSContactLogNarrativeDto;
import us.tx.state.dfps.service.apscontactlognarrative.dto.APSSafetyAssessmentContactDto;
import us.tx.state.dfps.service.apscontactlognarrative.dto.APSSafetyAssessmentDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.utils.PersonUtil;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class APSContactLogNarrativePrefillData extends DocumentServiceUtil {

    @Autowired
    PersonUtil personUtil;

    @Autowired
    ApsContactLogNarrativeDao apsContactLogNarrativeDao;

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
        APSContactLogNarrativeDto prefillDto = (APSContactLogNarrativeDto) parentDtoobj;

        List<FormDataGroupDto> formDataGroupList = new ArrayList<>();
        List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<>();
        boolean isWithinDateRange = false;

        BookmarkDto bookmarkTitleCaseNumber = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
                prefillDto.getGenericCaseInfoDto().getIdCase());
        bookmarkNonFrmGrpList.add(bookmarkTitleCaseNumber);

        BookmarkDto bookmarkTitleCaseName = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
                prefillDto.getGenericCaseInfoDto().getNmStage());
        bookmarkNonFrmGrpList.add(bookmarkTitleCaseName);

        BookmarkDto bookmarkCntDtfrm = createBookmark(BookmarkConstants.CONTACT_DATE_FROM,
                prefillDto.getDtSampleFrom());
        bookmarkNonFrmGrpList.add(bookmarkCntDtfrm);
        BookmarkDto bookmarkCntDtTo = createBookmark(BookmarkConstants.CONTACT_DATE_TO,
                prefillDto.getDtSampleTo());
        bookmarkNonFrmGrpList.add(bookmarkCntDtTo);


        for (Long idEvent : prefillDto.getSortedContactEventIds()) {
            Long apsSaEventId = apsContactLogNarrativeDao.getApsSaEventId(idEvent);
            // populate contacts created through safety Assessment
            APSSafetyAssessmentDto apsSafetyAssessmentDto ;
            List<APSSafetyAssessmentContactDto> contactDBListforSorting;
            List<FormDataGroupDto> tmpltAssmntContactDtolist = new ArrayList<>();
            /*
             * Contacts created through Safety Assessment are grouped by Safety Assessment and contacts created through contact faceplate are displayed individually.
             */
            if (null != apsSaEventId) {

                apsSafetyAssessmentDto = apsContactLogNarrativeDao.getApsSafetyAssessmentResults(apsSaEventId);
                contactDBListforSorting = apsContactLogNarrativeDao.getApsSaSafetyContactData(apsSaEventId);
                apsSafetyAssessmentDto.setContactList(contactDBListforSorting.stream()
                        .sorted(Comparator.comparing(APSSafetyAssessmentContactDto::getDateContactOccurred)).collect(Collectors.toList()));
                for (APSSafetyAssessmentContactDto apsSafetyAssessmentContactDto : contactDBListforSorting) {
                    if (!DateUtils.isBefore(apsSafetyAssessmentContactDto.getDateContactOccurred(), DateUtils.stringToDate(prefillDto.getDtSampleFrom())) &&
                            !DateUtils.isAfter(apsSafetyAssessmentContactDto.getDateContactOccurred(), DateUtils.stringToDate(prefillDto.getDtSampleTo()))) {
                        isWithinDateRange = true;
                        break;
                    }
                }
                if (isWithinDateRange) {

                    FormDataGroupDto assesmentTmplt = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACT,
                            FormConstants.EMPTY_STRING);
                    FormDataGroupDto saTypeGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SA_TYPE,
                            FormConstants.EMPTY_STRING);


                    setTypeData(apsSafetyAssessmentDto, saTypeGroup);
                    tmpltAssmntContactDtolist.add(saTypeGroup);

                    FormDataGroupDto sftyDsnGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SAFETY_DECISION,
                            FormConstants.EMPTY_STRING);
                    List<BookmarkDto> sftyDsnBmkList = new ArrayList<>();
                    BookmarkDto bookmarkSftyDeci = createBookmarkWithCodesTable(BookmarkConstants.SAFETY_DECISION,
                            apsSafetyAssessmentDto.getSavedSafetyDecisionCode(), CodesConstant.CSAFEDEC);
                    sftyDsnBmkList.add(bookmarkSftyDeci);
                    sftyDsnGroup.setBookmarkDtoList(sftyDsnBmkList);
                    tmpltAssmntContactDtolist.add(sftyDsnGroup);


                    setSafetyAssessmentContactData(apsSafetyAssessmentDto, tmpltAssmntContactDtolist);
                    FormDataGroupDto tmplatCiNrrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_F2F_NARR,
                            FormConstants.EMPTY_STRING);
                    List<BlobDataDto> blobList = new ArrayList<>();//
                    BlobDataDto blobF2fNarr = createBlobData(BookmarkConstants.F2F_NARR,
                            BookmarkConstants.APS_SA_NARR, apsSaEventId.toString());
                    blobList.add(blobF2fNarr);
                    tmplatCiNrrGroup.setBlobDataDtoList(blobList);
                    tmpltAssmntContactDtolist.add(tmplatCiNrrGroup);
                    assesmentTmplt.setFormDataGroupList(tmpltAssmntContactDtolist);
                    formDataGroupList.add(assesmentTmplt);
                }

            } else {
                setApsSafetyAssesmentContactsToPrefilDto(prefillDto, formDataGroupList, idEvent);
            }
        }

        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setFormDataGroupList(formDataGroupList);
        preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
        return preFillData;
    }

    /**
     * Method help to set the Safety assessment data into profile data
     *
     * @param apsSafetyAssessmentDto    requested data
     * @param tmpltAssmntContactDtolist contact details
     */
    private void setSafetyAssessmentContactData
    (APSSafetyAssessmentDto apsSafetyAssessmentDto, List<FormDataGroupDto> tmpltAssmntContactDtolist) {
        for (APSSafetyAssessmentContactDto apsSafetyAssessmentContactDto : apsSafetyAssessmentDto.getContactList()) {

            if (CodesConstant.CASE_INITIATION_CONTACT.equals(apsSafetyAssessmentContactDto.getContactType())) {
                FormDataGroupDto tmplatCiGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CI,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> tmplatCibmkList = new ArrayList<>();
                BookmarkDto bookmarkSumDtCi = createBookmark(BookmarkConstants.SUM_DATE_CI,
                        DateUtils.formatDatetoString(apsSafetyAssessmentContactDto.getDateContactOccurred()));
                tmplatCibmkList.add(bookmarkSumDtCi);
                BookmarkDto bookmarkSumTmCi = createBookmark(BookmarkConstants.SUM_TIME_CI,
                        apsSafetyAssessmentContactDto.getTimeContactOccurred());
                tmplatCibmkList.add(bookmarkSumTmCi);
                tmplatCiGroup.setBookmarkDtoList(tmplatCibmkList);
                tmpltAssmntContactDtolist.add(tmplatCiGroup);

                FormDataGroupDto tmplatCiNrrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CI_NARR,
                        FormConstants.EMPTY_STRING);
                List<BlobDataDto> blobList = new ArrayList<>();//
                BlobDataDto blobF2fNarr = createBlobData(BookmarkConstants.CI_NARR,
                        BookmarkConstants.CONTACT_NARRATIVE, apsSafetyAssessmentContactDto.getContactEventId().toString());
                blobList.add(blobF2fNarr);
                tmplatCiNrrGroup.setBlobDataDtoList(blobList);
                tmpltAssmntContactDtolist.add(tmplatCiNrrGroup);

            } else {
                if (FormConstants.Y.equals(apsSafetyAssessmentContactDto.getIndContactAttempted())) {
                    FormDataGroupDto tmlatAttmptGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_ATTMPT, FormConstants.EMPTY_STRING);
                    List<BookmarkDto> tmplatf2fBmkList = new ArrayList<>();
                    BookmarkDto bookmarkSumDtCi = createBookmark(BookmarkConstants.SUM_ATTMPT_F2F_DATE,
                            DateUtils.formatDatetoString(apsSafetyAssessmentContactDto.getDateContactOccurred()));
                    tmplatf2fBmkList.add(bookmarkSumDtCi);
                    BookmarkDto bookmarkSumTmCi = createBookmark(BookmarkConstants.SUM_ATTMPT_F2F_TIME,
                            apsSafetyAssessmentContactDto.getTimeContactOccurred());
                    tmplatf2fBmkList.add(bookmarkSumTmCi);
                    tmlatAttmptGroup.setBookmarkDtoList(tmplatf2fBmkList);
                    tmpltAssmntContactDtolist.add(tmlatAttmptGroup);

                } else {
                    FormDataGroupDto tmlatAttmptGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_F2F, FormConstants.EMPTY_STRING);
                    List<BookmarkDto> tmplatf2fBmkList = new ArrayList<>();
                    BookmarkDto bookmarkSumDtCi = createBookmark(BookmarkConstants.SUM_ACTUAL_F2F_DATE,
                            DateUtils.formatDatetoString(apsSafetyAssessmentContactDto.getDateContactOccurred()));
                    tmplatf2fBmkList.add(bookmarkSumDtCi);
                    BookmarkDto bookmarkSumTmCi = createBookmark(BookmarkConstants.SUM_ACTUAL_F2F_TIME,
                            apsSafetyAssessmentContactDto.getTimeContactOccurred());
                    tmplatf2fBmkList.add(bookmarkSumTmCi);
                    tmlatAttmptGroup.setBookmarkDtoList(tmplatf2fBmkList);
                    tmpltAssmntContactDtolist.add(tmlatAttmptGroup);

                }
            }
        }
    }

    /**
     * Method helps to determine the type and adding into form group
     *
     * @param apsSafetyAssessmentDto safety assessment data
     * @param saTypeGroup            form group
     */
    private void setTypeData(APSSafetyAssessmentDto apsSafetyAssessmentDto, FormDataGroupDto saTypeGroup) {
        if (apsSafetyAssessmentDto.getAssessmentType().equals(CodesConstant.CAPSFAT_INIT)) {
            List<BookmarkDto> saTypeBmkList = new ArrayList<>();
            BookmarkDto bookmarkFundsExpldLbl = createBookmark(BookmarkConstants.TYPE, FormConstants.SAFETY_ASSMT);
            saTypeBmkList.add(bookmarkFundsExpldLbl);
            saTypeGroup.setBookmarkDtoList(saTypeBmkList);
        } else {
            List<BookmarkDto> saTypeBmkList = new ArrayList<>();
            BookmarkDto bookmarkFundsExpldLbl = createBookmark(BookmarkConstants.TYPE, FormConstants.SAFETY_REAS);
            saTypeBmkList.add(bookmarkFundsExpldLbl);
            saTypeGroup.setBookmarkDtoList(saTypeBmkList);
        }
    }

    /**
     * Method helps to set the safety Assessment contacts data into prefill form
     *
     * @param prefillDto        form data
     * @param formDataGroupList form group list
     * @param idEvent           event id
     */
    private void setApsSafetyAssesmentContactsToPrefilDto(APSContactLogNarrativeDto prefillDto, List<FormDataGroupDto> formDataGroupList, Long idEvent) {
        APSSafetyAssessmentContactDto contactDB = apsContactLogNarrativeDao.getApsSaSafetyContactDataByEventId(idEvent);
        if (null != contactDB && !DateUtils.isBefore(contactDB.getDateContactOccurred(), DateUtils.stringToDate(prefillDto.getDtSampleFrom())) &&
                (!DateUtils.isAfter(contactDB.getDateContactOccurred(), DateUtils.stringToDate(prefillDto.getDtSampleTo())) || !DateUtils.isAfterToday(contactDB.getDateContactOccurred()))) {
            FormDataGroupDto tmpltContact = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACT,
                    FormConstants.EMPTY_STRING);
            List<FormDataGroupDto> tmpltContactDtolist = new ArrayList<>();

            FormDataGroupDto tmpltCntTypeGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACT_TYPE,
                    FormConstants.EMPTY_STRING);

            List<BookmarkDto> cntTypeBmkList = new ArrayList<>();
            BookmarkDto bookmarkCtType = createBookmarkWithCodesTable(BookmarkConstants.CONTACT_TYPE,
                    contactDB.getCodeContactType(), CodesConstant.CCNTCTYP);
            cntTypeBmkList.add(bookmarkCtType);
            tmpltCntTypeGroup.setBookmarkDtoList(cntTypeBmkList);
            tmpltContactDtolist.add(tmpltCntTypeGroup);

            if (FormConstants.Y.equals(contactDB.getIndContactAttempted())) {
                FormDataGroupDto tmplatCiGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACT_ATTMPT,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> tmplatCibmkList = new ArrayList<>();
                BookmarkDto bookmarkFtoFDt = createBookmark(BookmarkConstants.ATTMPT_F2F_DATE,
                        DateUtils.formatDatetoString(contactDB.getDateContactOccurred()));
                tmplatCibmkList.add(bookmarkFtoFDt);
                BookmarkDto bookmarkFtoFTm = createBookmark(BookmarkConstants.ATTMPT_F2F_TIME,
                        contactDB.getTimeContactOccurred());
                tmplatCibmkList.add(bookmarkFtoFTm);
                tmplatCiGroup.setBookmarkDtoList(tmplatCibmkList);
                tmpltContactDtolist.add(tmplatCiGroup);
            } else {
                FormDataGroupDto tmplatCntDtGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACT_DATE,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> tmplatCntDtbmkList = new ArrayList<>();
                BookmarkDto bookmarkDtOcrd = createBookmark(BookmarkConstants.CONTACT_DATE_OCCURRED,
                        DateUtils.formatDatetoString(contactDB.getDateContactOccurred()));
                tmplatCntDtbmkList.add(bookmarkDtOcrd);
                BookmarkDto bookmarkSumTmOcrd = createBookmark(BookmarkConstants.CONTACT_TIME_OCCURRED,
                        contactDB.getTimeContactOccurred());
                tmplatCntDtbmkList.add(bookmarkSumTmOcrd);
                tmplatCntDtGroup.setBookmarkDtoList(tmplatCntDtbmkList);
                tmpltContactDtolist.add(tmplatCntDtGroup);
            }
            if (!ObjectUtils.isEmpty(contactDB.getContactPurpose())) {
                FormDataGroupDto cntprpsGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACT_PURPOSE,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> cntprpsbmkList = new ArrayList<>();
                BookmarkDto bookmarkCtprpse = createBookmarkWithCodesTable(BookmarkConstants.CONTACT_PURPOSE,
                        contactDB.getContactPurpose(), CodesConstant.CCNTPURP);
                cntprpsbmkList.add(bookmarkCtprpse);
                cntprpsGroup.setBookmarkDtoList(cntprpsbmkList);
                tmpltContactDtolist.add(cntprpsGroup);
            }
            if (!ObjectUtils.isEmpty(contactDB.getContactMethodType())) {
                FormDataGroupDto cntMthdGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACT_METHOD,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> cntMthdbmkList = new ArrayList<>();
                BookmarkDto bookmarkCntMtd = createBookmarkWithCodesTable(BookmarkConstants.CONTACT_METHOD,
                        contactDB.getContactMethodType(), CodesConstant.CCNTMETH);
                cntMthdbmkList.add(bookmarkCntMtd);
                cntMthdGroup.setBookmarkDtoList(cntMthdbmkList);
                tmpltContactDtolist.add(cntMthdGroup);
            }
            if (!ObjectUtils.isEmpty(contactDB.getLocation())) {
                FormDataGroupDto cntLtnGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACT_LOC,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> cntLocbmkList = new ArrayList<>();
                BookmarkDto bookmarkCtloc = createBookmarkWithCodesTable(BookmarkConstants.CONTACT_LOCATION,
                        contactDB.getLocation(), CodesConstant.CCNCTLOC);
                cntLocbmkList.add(bookmarkCtloc);
                cntLtnGroup.setBookmarkDtoList(cntLocbmkList);
                tmpltContactDtolist.add(cntLtnGroup);
            }
            FormDataGroupDto tmpltCtWrkrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CT_WORKER,
                    FormConstants.EMPTY_STRING);
            List<BookmarkDto> wrkBmkList = new ArrayList<>();
            BookmarkDto bookmarkSftyDeci = createBookmark(BookmarkConstants.CONTACT_WORKER,
                    contactDB.getContactWorkerFullName());
            wrkBmkList.add(bookmarkSftyDeci);
            tmpltCtWrkrGroup.setBookmarkDtoList(wrkBmkList);
            tmpltContactDtolist.add(tmpltCtWrkrGroup);
            setContactPersonDetails(contactDB, tmpltContactDtolist);
            FormDataGroupDto nrrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACT_NARR,
                    FormConstants.EMPTY_STRING);
            List<BlobDataDto> blobDataDtoList = new ArrayList<>();
            BlobDataDto blobF2fNarr = createBlobData(BookmarkConstants.CONTACT_NARRATIVE,
                    BookmarkConstants.CONTACT_NARRATIVE, contactDB.getContactEventId().toString());
            blobDataDtoList.add(blobF2fNarr);
            nrrGroup.setBlobDataDtoList(blobDataDtoList);
            tmpltContactDtolist.add(nrrGroup);

            tmpltContact.setFormDataGroupList(tmpltContactDtolist);
            formDataGroupList.add(tmpltContact);
        }
    }

    /**
     * Method helps to set the contact person details into contact list
     *
     * @param contactDB           requested data
     * @param tmpltContactDtolist template contact list
     */
    private void setContactPersonDetails(APSSafetyAssessmentContactDto contactDB, List<FormDataGroupDto> tmpltContactDtolist) {
        if (CollectionUtils.isNotEmpty(contactDB.getContactFullName())) {
            FormDataGroupDto nameGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACT_NAME, FormGroupsConstants.TMPLAT_SVC_CONTACT);
            List<FormDataGroupDto> formDataContactPersonGroupList = new ArrayList<>();
            for (String contactName : contactDB.getContactFullName()) {
                FormDataGroupDto personGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PERSON_NAME,
                        FormConstants.EMPTY_STRING);
                List<BookmarkDto> personBmkList = new ArrayList<>();
                BookmarkDto bookmarkpersonLbl = createBookmark(BookmarkConstants.CONTACT_NAME_FULL, contactName);
                personBmkList.add(bookmarkpersonLbl);
                personGroup.setBookmarkDtoList(personBmkList);
                formDataContactPersonGroupList.add(personGroup);
            }
            nameGrp.setFormDataGroupList(formDataContactPersonGroupList);
            tmpltContactDtolist.add(nameGrp);
        }
    }
}
