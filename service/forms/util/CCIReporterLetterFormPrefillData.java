package us.tx.state.dfps.service.forms.util;

import org.springframework.stereotype.Component;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.investigation.dto.CCIReporterLetterFormDto;

import java.util.ArrayList;
import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Generates
 * prefill string used to populate form CFIV5000 Apr 16, 2018- 9:26:03 AM © 2017
 * Texas Department of Family and Protective Services
 */
@Component
public class CCIReporterLetterFormPrefillData extends DocumentServiceUtil {

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoObj) {
		List<BookmarkDto> bookmarkList = new ArrayList<BookmarkDto>();

		if (parentDtoObj != null) {
			CCIReporterLetterFormDto prefillDto = (CCIReporterLetterFormDto) parentDtoObj;

			// Director info
			BookmarkDto bookmarkHeaderDirectorTitle = createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_TITLE,
					ServiceConstants.UNIT_REGION_001, CodesConstant.CBRDTTLE);
			bookmarkList.add(bookmarkHeaderDirectorTitle);
			BookmarkDto bookmarkHeaderDirectorName = createBookmarkWithCodesTable(BookmarkConstants.HEADER_DIRECTOR_NAME,
					ServiceConstants.UNIT_REGION_001, CodesConstant.CBRDNAME);
			bookmarkList.add(bookmarkHeaderDirectorName);

			BookmarkDto bookmarkSystemDate = createBookmark(BookmarkConstants.SYSTEM_DATE,
					TypeConvUtil.formDateFormat(prefillDto.getSystemDate()));
			bookmarkList.add(bookmarkSystemDate);

			StringBuilder reporterNameBuilder = new StringBuilder();
			if (prefillDto.getReporterPerson() != null) {
				reporterNameBuilder.append(prefillDto.getReporterPerson().getNmPersonFirst())
						.append(" ")
						.append(prefillDto.getReporterPerson().getNmPersonLast());
			}
			BookmarkDto bookmarkReporterName = createBookmark(BookmarkConstants.REPORTER_NAME,
					reporterNameBuilder.toString());
			bookmarkList.add(bookmarkReporterName);

			StringBuilder reporterAddressBuilder = new StringBuilder()
					.append(prefillDto.getReporterAddress().getStreetLn1()).append("<br>");
			String street2 = prefillDto.getReporterAddress().getStreetLn2();
			if (!TypeConvUtil.isNullOrEmpty(street2)) {
				reporterAddressBuilder.append(street2).append("<br>");
			}
			reporterAddressBuilder.append(prefillDto.getReporterAddress().getCity())
					.append(", ")
					.append(prefillDto.getReporterAddress().getState())
					.append(" ")
					.append(prefillDto.getReporterAddress().getZip());
			BookmarkDto bookmarkReporterAddress = createBookmark(BookmarkConstants.TXT_REPORTER_ADDRESS,
					reporterAddressBuilder.toString());
			bookmarkList.add(bookmarkReporterAddress);

			StringBuilder operationNumberBuilder = new StringBuilder().append(prefillDto.getOperationDetails().getNbrAcclaim());
			Integer nbrAgency = prefillDto.getOperationDetails().getNbrAgency();
			if (!TypeConvUtil.isNullOrEmpty(nbrAgency)) {
				operationNumberBuilder.append("-").append(nbrAgency.toString());
			}
			Short nbrBranch = prefillDto.getOperationDetails().getNbrBranch();
			if (!TypeConvUtil.isNullOrEmpty(nbrBranch)) {
				operationNumberBuilder.append("-").append(nbrBranch.toString());
			}
			BookmarkDto bookmarkOperationNumber = createBookmark(BookmarkConstants.OPERATION_NUMBER,
					operationNumberBuilder.toString());
			bookmarkList.add(bookmarkOperationNumber);


			BookmarkDto bookmarkOperationName = createBookmark(BookmarkConstants.OPERATION_NAME,
					prefillDto.getOperationDetails().getNmResource());
			bookmarkList.add(bookmarkOperationName);


			StringBuilder investigatorNameBuilder = new StringBuilder()
					.append(prefillDto.getInvestigatorPerson().getNmPersonFirst())
					.append(" ")
					.append(prefillDto.getInvestigatorPerson().getNmPersonLast());
			BookmarkDto bookmarkInvestigatorName = createBookmark(BookmarkConstants.WORKER_NAME,
					investigatorNameBuilder.toString());
			bookmarkList.add(bookmarkInvestigatorName);

			BookmarkDto bookmarkInvestigatorPhone = createBookmark(BookmarkConstants.WORKER_PHONE,
					TypeConvUtil.formatPhone(prefillDto.getInvestigatorPhone().getPhone()));
			bookmarkList.add(bookmarkInvestigatorPhone);
		}
		// Populate prefill object
		PreFillDataServiceDto prefillData = new PreFillDataServiceDto();
		prefillData.setBookmarkDtoList(bookmarkList);
		return prefillData;

	}
}
