/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Apr 18, 2018- 4:50:01 PM
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
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CIV21O00 Apr
 * 18, 2018- 4:50:01 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Component
public class RiskAssessmentPrefillData extends DocumentServiceUtil {

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

		BookmarkDto bookmarkRiskAct = createBookmark(BookmarkConstants.RISK_CASE_ACTION,
				lookupDao.decode(ServiceConstants.CCINVCLS, riskAssmt.getStageCaseDtlDto().getCdStageReasonClosed()));
		bookmarkNonFrmGrpList.add(bookmarkRiskAct);
		BookmarkDto bookmarkTitleCaseNm = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				riskAssmt.getStageCaseDtlDto().getNmCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNm);
		BookmarkDto bookmarkTitleCaseNumb = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				riskAssmt.getStageCaseDtlDto().getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNumb);	
		
		if(!ObjectUtils.isEmpty(riskAssmt.getRiskAssessmentFactorDtoList()))
		{			
			RiskAssessmentFactorDto riskAssessmentFactorDto=riskAssmt.getRiskAssessmentFactorDtoList().get(0);
			
			BookmarkDto bookmarkRiskAssmtPurpose = createBookmark(BookmarkConstants.RISK_PURPOSE,
						lookupDao.decode(ServiceConstants.CPURPRSK, riskAssessmentFactorDto.getCdRiskAssmtPurpose()));
			bookmarkNonFrmGrpList.add(bookmarkRiskAssmtPurpose);			
			
			BookmarkDto bookmarkAssmtRiskFind = createBookmark(BookmarkConstants.RISK_FINDING,
						lookupDao.decode(ServiceConstants.CCRSKFND, riskAssessmentFactorDto.getCdRiskAssmtRiskFind()));
			bookmarkNonFrmGrpList.add(bookmarkAssmtRiskFind);			
			
			BlobDataDto blobCallNarrative = createBlobData(BookmarkConstants.RISK_NARR, "RISK_ASSMT_NARR",
						Integer.valueOf(riskAssessmentFactorDto.getIdEvent().intValue()));
			blobDataDtoForCallNarrative.add(blobCallNarrative);						
		}
		

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		preFillData.setBlobDataDtoList(blobDataDtoForCallNarrative);
		return preFillData;
	}

}
