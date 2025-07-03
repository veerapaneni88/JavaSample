/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Apr 19, 2018- 10:41:13 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dto.RiskAssessmentFactorDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.dto.RiskAssessmentNarrativeDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.pca.dto.StageCaseDtlDto;
import us.tx.state.dfps.service.riskassesment.dto.AllegationNameDtlDto;
import us.tx.state.dfps.service.riskassesment.dto.RiskFactorsDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: CIV30O00
 * Apr 19, 2018- 10:41:13 AM © 2017 Texas Department of Family and Protective
 * Services
 */
@Component
public class RiskAssessmentInvPrefillData extends DocumentServiceUtil {

	@Autowired
	private LookupDao lookupDao;

	/**
	 * Method Description: This method is used to prefill the data from the
	 * different Dao by passing Dao output Dtos and bookmark and form group
	 * bookmark Dto as objects as input request
	 * 
	 * @param parentDtoobj
	 * @param bookmarkDtoObj
	 * @return PreFillData
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		RiskAssessmentNarrativeDto riskAssmt = (RiskAssessmentNarrativeDto) parentDtoobj;

		if (null == riskAssmt.getStageCaseDtlDto()) {
			riskAssmt.setStageCaseDtlDto(new StageCaseDtlDto());
		}
		if (null == riskAssmt.getRiskAssessmentFactorDtoList()) {
			riskAssmt.setRiskAssessmentFactorDtoList(new ArrayList<RiskAssessmentFactorDto>());
		}
		if (null == riskAssmt.getRiskFactorsDtoList()) {
			riskAssmt.setRiskFactorsDtoList(new ArrayList<RiskFactorsDto>());
		}
		if (null == riskAssmt.getAllegationNameDtlDtoList()) {
			riskAssmt.setAllegationNameDtlDtoList(new ArrayList<AllegationNameDtlDto>());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		List<BlobDataDto> blobDataDtoForCallNarrative = new ArrayList<BlobDataDto>();

		// civ30u01
		for (AllegationNameDtlDto allegationNameDtlDto : riskAssmt.getAllegationNameDtlDtoList()) {
			List<FormDataGroupDto> formDataciv30u01List = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formDataciv30u01Para = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATION,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkciv30u01List = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkDisposition = createBookmark(BookmarkConstants.DISPOSITION,
					lookupDao.decode(ServiceConstants.CDISPSTN, allegationNameDtlDto.getCdAlledgeDisposition()));
			bookmarkciv30u01List.add(bookmarkDisposition);
			BookmarkDto bookmarkAllegation = createBookmark(BookmarkConstants.ALLEGATION,
					lookupDao.decode(ServiceConstants.CABALTYP, allegationNameDtlDto.getCdAllegeType()));
			bookmarkciv30u01List.add(bookmarkAllegation);
			if (!ObjectUtils.isEmpty(allegationNameDtlDto.getVictimCdNmSuffix())) {
				BookmarkDto bookmarkVictSuff = createBookmark(BookmarkConstants.VICTIM_SUFFIX,
						lookupDao.decode(ServiceConstants.CSUFFIX2, allegationNameDtlDto.getVictimCdNmSuffix()));
				bookmarkciv30u01List.add(bookmarkVictSuff);
			}
			if (!ObjectUtils.isEmpty(allegationNameDtlDto.getPerpetratorCdNmSuffix())) {
				BookmarkDto bookmarkAPSuff = createBookmark(BookmarkConstants.AP_SUFFIX,
						lookupDao.decode(ServiceConstants.CSUFFIX2, allegationNameDtlDto.getPerpetratorCdNmSuffix()));
				bookmarkciv30u01List.add(bookmarkAPSuff);
			}
			BookmarkDto bookmarkVicFirst = createBookmark(BookmarkConstants.VICTIM_FIRST,
					allegationNameDtlDto.getVictimNmFirst());
			bookmarkciv30u01List.add(bookmarkVicFirst);
			BookmarkDto bookmarkVicLast = createBookmark(BookmarkConstants.VICTIM_LAST,
					allegationNameDtlDto.getVictimNmLast());
			bookmarkciv30u01List.add(bookmarkVicLast);
			BookmarkDto bookmarkVicMid = createBookmark(BookmarkConstants.VICTIM_MIDDLE,
					allegationNameDtlDto.getVictimNmMiddle());
			bookmarkciv30u01List.add(bookmarkVicMid);
			BookmarkDto bookmarkAPFirst = createBookmark(BookmarkConstants.AP_FIRST,
					allegationNameDtlDto.getPerpetratorNmFirst());
			bookmarkciv30u01List.add(bookmarkAPFirst);
			BookmarkDto bookmarkAPLast = createBookmark(BookmarkConstants.AP_LAST,
					allegationNameDtlDto.getPerpetratorNmLast());
			bookmarkciv30u01List.add(bookmarkAPLast);
			BookmarkDto bookmarkAPMid = createBookmark(BookmarkConstants.AP_MIDDLE,
					allegationNameDtlDto.getPerpetratorNmMiddle());
			bookmarkciv30u01List.add(bookmarkAPMid);
			formDataciv30u01Para.setBookmarkDtoList(bookmarkciv30u01List);
			List<FormDataGroupDto> allParentList = new ArrayList<FormDataGroupDto>();
			// civ30o01 --> cfzco00(40)
			for (AllegationNameDtlDto allegationNameDtlDto1 : riskAssmt.getAllegationNameDtlDtoList()) {
				if (!ObjectUtils.isEmpty(allegationNameDtlDto1.getPerpetratorCdNmSuffix())) {
					List<FormDataGroupDto> formDataCommaBList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto formDataCommaBPara = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_B,
							FormGroupsConstants.TMPLAT_ALLEGATION);
					List<BookmarkDto> bookmarkCommaBList = new ArrayList<BookmarkDto>();
					formDataCommaBPara.setBookmarkDtoList(bookmarkCommaBList);
					formDataCommaBList.add(formDataCommaBPara);
					allParentList.addAll(formDataCommaBList);
				}
			}
			// civ30o01 --> cfzco00(41)
			for (AllegationNameDtlDto allegationNameDtlDto1 : riskAssmt.getAllegationNameDtlDtoList()) {
				if (!ObjectUtils.isEmpty(allegationNameDtlDto1.getPerpetratorCdNmSuffix())) {
					List<FormDataGroupDto> formDataCommaList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto formDataCommaPara = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
							FormGroupsConstants.TMPLAT_ALLEGATION);
					List<BookmarkDto> bookmarkCommaList = new ArrayList<BookmarkDto>();
					formDataCommaPara.setBookmarkDtoList(bookmarkCommaList);
					formDataCommaList.add(formDataCommaPara);
					allParentList.addAll(formDataCommaList);
				}
			}
			formDataciv30u01Para.setFormDataGroupList(allParentList);
			formDataciv30u01List.add(formDataciv30u01Para);
			formDataGroupList.addAll(formDataciv30u01List);
		}

		BookmarkDto bookmarkDtComp = createBookmark(BookmarkConstants.DATE_COMPLETED,
				riskAssmt.getStageCaseDtlDto().getDtCaseClosed());
		bookmarkNonFrmGrpList.add(bookmarkDtComp);
		BookmarkDto bookmarkRecAct = createBookmark(BookmarkConstants.RECOMMENDED_ACTION,
				lookupDao.decode(ServiceConstants.CCINVCLS, riskAssmt.getStageCaseDtlDto().getCdStageReasonClosed()));
		bookmarkNonFrmGrpList.add(bookmarkRecAct);
		BookmarkDto bookmarkTitleCaseNm = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				riskAssmt.getStageCaseDtlDto().getNmCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNm);
		BookmarkDto bookmarkTitleCaseNumb = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				riskAssmt.getStageCaseDtlDto().getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNumb);
		for (RiskAssessmentFactorDto riskAssessmentFactorDto : riskAssmt.getRiskAssessmentFactorDtoList()) {
			BookmarkDto bookmarkPurpose = createBookmark(BookmarkConstants.PURPOSE,
					lookupDao.decode(ServiceConstants.CPURPRSK, riskAssessmentFactorDto.getCdRiskAssmtPurpose()));
			bookmarkNonFrmGrpList.add(bookmarkPurpose);
		}
		for (RiskAssessmentFactorDto riskAssessmentFactorDto3 : riskAssmt.getRiskAssessmentFactorDtoList()) {
			BlobDataDto blobCallNarrative = createBlobData(BookmarkConstants.RISK_NARR, "RISK_ASSMT_NARR",
					String.valueOf(riskAssessmentFactorDto3.getIdEvent()));
			blobDataDtoForCallNarrative.add(blobCallNarrative);
		}
		for (RiskAssessmentFactorDto riskAssessmentFactorDto2 : riskAssmt.getRiskAssessmentFactorDtoList()) {
			BookmarkDto bookmarFinding = createBookmark(BookmarkConstants.FINDING,
					lookupDao.decode(ServiceConstants.CCRSKFND, riskAssessmentFactorDto2.getCdRiskAssmtRiskFind()));
			bookmarkNonFrmGrpList.add(bookmarFinding);
		}
		for (RiskAssessmentFactorDto riskAssessmentFactorDto2 : riskAssmt.getRiskAssessmentFactorDtoList()) {
			BookmarkDto bookmarkApAccess = createBookmark(BookmarkConstants.AP_ACCESS,
					lookupDao.decode(ServiceConstants.CRSKRESP, riskAssessmentFactorDto2.getCdRiskAssmtApAccess()));
			bookmarkNonFrmGrpList.add(bookmarkApAccess);
		}
		for (RiskAssessmentFactorDto riskAssessmentFactorDto2 : riskAssmt.getRiskAssessmentFactorDtoList()) {
			BookmarkDto bookmarkRiskFindDef = createBookmark(BookmarkConstants.RISK_FINDING_DEF,
					lookupDao.decode(ServiceConstants.CRAFNDDF, riskAssessmentFactorDto2.getCdRiskAssmtRiskFind()));
			bookmarkNonFrmGrpList.add(bookmarkRiskFindDef);
		}
		for (RiskFactorsDto riskFactorsDto : riskAssmt.getRiskFactorsDtoList()) {
			BookmarkDto bookmarkComments1 = createBookmark(BookmarkConstants.COMMENTS_1,
					riskFactorsDto.getTxtRiskFactorComment());
			bookmarkNonFrmGrpList.add(bookmarkComments1);
		}
		for (RiskFactorsDto riskFactorsDto : riskAssmt.getRiskFactorsDtoList()) {
			if (!ObjectUtils.isEmpty(riskFactorsDto.getCdRiskFactorResponse())) {
				BookmarkDto bookmarkResponse1 = createBookmark(BookmarkConstants.RESPONSE_1,
						lookupDao.decode(ServiceConstants.CRSKRESP, riskFactorsDto.getCdRiskFactorResponse()));
				bookmarkNonFrmGrpList.add(bookmarkResponse1);
			}
		}
		for (RiskFactorsDto riskFactorsDto : riskAssmt.getRiskFactorsDtoList()) {
			BookmarkDto bookmarkComments2 = createBookmark(BookmarkConstants.COMMENTS_2,
					riskFactorsDto.getTxtRiskFactorComment());
			bookmarkNonFrmGrpList.add(bookmarkComments2);
		}
		for (RiskFactorsDto riskFactorsDto : riskAssmt.getRiskFactorsDtoList()) {
			if (!ObjectUtils.isEmpty(riskFactorsDto.getCdRiskFactorResponse())) {
				BookmarkDto bookmarkResponse2 = createBookmark(BookmarkConstants.RESPONSE_2,
						lookupDao.decode(ServiceConstants.CRSKRESP, riskFactorsDto.getCdRiskFactorResponse()));
				bookmarkNonFrmGrpList.add(bookmarkResponse2);
			}
		}
		for (RiskFactorsDto riskFactorsDto : riskAssmt.getRiskFactorsDtoList()) {
			BookmarkDto bookmarkComments3 = createBookmark(BookmarkConstants.COMMENTS_3,
					riskFactorsDto.getTxtRiskFactorComment());
			bookmarkNonFrmGrpList.add(bookmarkComments3);
		}
		for (RiskFactorsDto riskFactorsDto : riskAssmt.getRiskFactorsDtoList()) {
			if (!ObjectUtils.isEmpty(riskFactorsDto.getCdRiskFactorResponse())) {
				BookmarkDto bookmarkResponse3 = createBookmark(BookmarkConstants.RESPONSE_3,
						lookupDao.decode(ServiceConstants.CRSKRESP, riskFactorsDto.getCdRiskFactorResponse()));
				bookmarkNonFrmGrpList.add(bookmarkResponse3);
			}
		}
		for (RiskFactorsDto riskFactorsDto : riskAssmt.getRiskFactorsDtoList()) {
			BookmarkDto bookmarkComments4 = createBookmark(BookmarkConstants.COMMENTS_4,
					riskFactorsDto.getTxtRiskFactorComment());
			bookmarkNonFrmGrpList.add(bookmarkComments4);
		}
		for (RiskFactorsDto riskFactorsDto : riskAssmt.getRiskFactorsDtoList()) {
			if (!ObjectUtils.isEmpty(riskFactorsDto.getCdRiskFactorResponse())) {
				BookmarkDto bookmarkResponse4 = createBookmark(BookmarkConstants.RESPONSE_4,
						lookupDao.decode(ServiceConstants.CRSKRESP, riskFactorsDto.getCdRiskFactorResponse()));
				bookmarkNonFrmGrpList.add(bookmarkResponse4);
			}
		}
		for (RiskFactorsDto riskFactorsDto : riskAssmt.getRiskFactorsDtoList()) {
			BookmarkDto bookmarkComments5 = createBookmark(BookmarkConstants.COMMENTS_5,
					riskFactorsDto.getTxtRiskFactorComment());
			bookmarkNonFrmGrpList.add(bookmarkComments5);
		}
		for (RiskFactorsDto riskFactorsDto : riskAssmt.getRiskFactorsDtoList()) {
			if (!ObjectUtils.isEmpty(riskFactorsDto.getCdRiskFactorResponse())) {
				BookmarkDto bookmarkResponse5 = createBookmark(BookmarkConstants.RESPONSE_5,
						lookupDao.decode(ServiceConstants.CRSKRESP, riskFactorsDto.getCdRiskFactorResponse()));
				bookmarkNonFrmGrpList.add(bookmarkResponse5);
			}
		}
		for (RiskFactorsDto riskFactorsDto : riskAssmt.getRiskFactorsDtoList()) {
			BookmarkDto bookmarkComments6 = createBookmark(BookmarkConstants.COMMENTS_6,
					riskFactorsDto.getTxtRiskFactorComment());
			bookmarkNonFrmGrpList.add(bookmarkComments6);
		}
		for (RiskFactorsDto riskFactorsDto : riskAssmt.getRiskFactorsDtoList()) {
			if (!ObjectUtils.isEmpty(riskFactorsDto.getCdRiskFactorResponse())) {
				BookmarkDto bookmarkResponse6 = createBookmark(BookmarkConstants.RESPONSE_6,
						lookupDao.decode(ServiceConstants.CRSKRESP, riskFactorsDto.getCdRiskFactorResponse()));
				bookmarkNonFrmGrpList.add(bookmarkResponse6);
			}
		}
		for (RiskFactorsDto riskFactorsDto : riskAssmt.getRiskFactorsDtoList()) {
			BookmarkDto bookmarkComments7 = createBookmark(BookmarkConstants.COMMENTS_7,
					riskFactorsDto.getTxtRiskFactorComment());
			bookmarkNonFrmGrpList.add(bookmarkComments7);
		}
		for (RiskFactorsDto riskFactorsDto : riskAssmt.getRiskFactorsDtoList()) {
			if (!ObjectUtils.isEmpty(riskFactorsDto.getCdRiskFactorResponse())) {
				BookmarkDto bookmarkResponse7 = createBookmark(BookmarkConstants.RESPONSE_7,
						lookupDao.decode(ServiceConstants.CRSKRESP, riskFactorsDto.getCdRiskFactorResponse()));
				bookmarkNonFrmGrpList.add(bookmarkResponse7);
			}
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		preFillData.setBlobDataDtoList(blobDataDtoForCallNarrative);
		return preFillData;
	}
}
