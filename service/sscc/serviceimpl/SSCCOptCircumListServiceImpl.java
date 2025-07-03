/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 9, 2018- 2:53:32 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.sscc.serviceimpl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.domain.SsccPlcmtHeader;
import us.tx.state.dfps.service.admin.dto.SSCCPlcmtOptCircumListDto;
import us.tx.state.dfps.service.casepackage.dao.SSCCTimelineDao;
import us.tx.state.dfps.service.casepackage.dto.SSCCListDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.sscc.dao.SSCCListDao;
import us.tx.state.dfps.service.sscc.dao.SSCCOptCircumListDao;
import us.tx.state.dfps.service.sscc.dao.SSCCPlcmtOptCircumDao;
import us.tx.state.dfps.service.sscc.dto.SSCCPlcmtOptCircumDto;
import us.tx.state.dfps.service.sscc.service.SSCCOptCircumListService;
import us.tx.state.dfps.service.sscc.util.SSCCPlcmtOptCircumUtil;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 9, 2018- 2:53:32 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class SSCCOptCircumListServiceImpl implements SSCCOptCircumListService {

	@Autowired
	SSCCOptCircumListDao ssccOptCircumListDao;

	@Autowired
	SSCCPlcmtOptCircumDao ssccPlcmtOptCircumDao;

	@Autowired
	SSCCTimelineDao ssccTimelineDao;

	@Autowired
	SSCCListDao ssccListDao;

	@Autowired
	SSCCPlcmtOptCircumUtil ssccPlcmtOptCircumUtil;

	/**
	 * Method Name: fetchSSCCListResults Method Description:Fetches the SSCC
	 * Placement Options and Circumstances associated to the stage and sets to
	 * the PaginationResultsBean object as a list.
	 * 
	 * @param idStage
	 * @param userDto
	 * @return PaginationResultDto
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<SSCCPlcmtOptCircumListDto> fetchSSCCOptCircumList(Long idStage) {
		return ssccOptCircumListDao.retrieveSSCCOptCircumList(idStage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.sscc.service.SSCCOptCircumListService#
	 * rescindAndRetrieveList(us.tx.state.dfps.service.sscc.dto.
	 * SSCCPlcmtOptCircumDto)
	 */
	@Override
	public List<SSCCPlcmtOptCircumListDto> rescindAndRetrieveList(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		rescind(ssccPlcmtOptCircumDto);
		return fetchSSCCOptCircumList(ssccPlcmtOptCircumDto.getIdStage());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.sscc.service.SSCCOptCircumListService#rescind(us
	 * .tx.state.dfps.service.sscc.dto.SSCCPlcmtOptCircumDto)
	 */
	@Override
	public void rescind(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {
		String textDescForTimeline = ServiceConstants.EMPTY_STR;
		// save SSCC Placement header
		saveSSCCPlcmtHeader(ssccPlcmtOptCircumDto);
		// fetch SSCC List
		List<SSCCListDto> ssccList = ssccListDao.fetchSSCCList(ssccPlcmtOptCircumDto.getIdActiveRef());
		if (!TypeConvUtil.isNullOrEmpty(ssccList)) {
			SSCCListDto ssccListDto = ssccList.get(0);
			if (ssccPlcmtOptCircumDto.getIsOption()) {
				ssccListDto.setDtPlcmtOptionStatus(new Date());
				textDescForTimeline = ServiceConstants.TIMELINE_RESCIND_OPTION;
			} else {
				ssccListDto.setDtPlcmtCircStatus(new Date());
				textDescForTimeline = ServiceConstants.TIMELINE_RESCIND_CIRCUMSTANCE;

			}
			ssccTimelineDao.insertSSCCTimeline(
					ssccPlcmtOptCircumUtil.populateSSCCTimelineValue(ssccPlcmtOptCircumDto, textDescForTimeline));
			ssccListDto.setIdSSCCReferral(ssccPlcmtOptCircumDto.getIdActiveRef());
			ssccListDao.saveSSCCList(ssccListDto);
		}

	}

	private void saveSSCCPlcmtHeader(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto) {

		SsccPlcmtHeader ssccPlcmtHeader = ssccPlcmtOptCircumDao
				.readSSCCPlcmtHeader(ssccPlcmtOptCircumDto.getSsccPlcmtHeaderDto().getIdSSCCPlcmtHeader());
		ssccPlcmtHeader.setCdStatus(CodesConstant.CSSCCSTA_70);
		// save SSCC header
		ssccPlcmtOptCircumDao.saveSSCCPlcmtHeader(ssccPlcmtHeader);

	}
}
