package us.tx.state.dfps.service.ssccchildplan.utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.casepackage.dao.SSCCTimelineDao;
import us.tx.state.dfps.service.casepackage.dto.SSCCTimelineDto;
import us.tx.state.dfps.service.childplan.dao.ChildPlanBeanDao;
import us.tx.state.dfps.service.childplan.dto.SSCCChildPlanDto;
import us.tx.state.dfps.service.childplan.dto.SSCCChildPlanGuideTopicDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.sscc.dao.SSCCRefDao;
import us.tx.state.dfps.service.ssccchildplan.dao.SSCCChildPlanDao;
import us.tx.state.dfps.service.subcare.dto.ChildPlanGuideTopicDto;

@Service
@Transactional
public class SSCCChildPlanUtility {
	private static final Logger LOG = Logger.getLogger("ServiceBusiness-SSCCChildPlanServiceLog");
	public static final String CHILD_PLAN_DETAIL_PAGE_MODE = "childPlanDetailPageMode";
	public static final String SSCC_REFERRAL_PAGE_MODE = "ssccReferralPageMode";
	public final static String TRACE_TAG = "SSCCChildPlanHelper";
	public static final String REQ_FUNC_CD_ADD = "A";
	public static final String REQ_FUNC_CD_UPDATE = "U";

	@Autowired
	private SSCCChildPlanDao ssccChildPlanDao;

	@Autowired
	private SSCCRefDao ssccRefDao;

	@Autowired
	private ChildPlanBeanDao childPlanBeanDao;

	@Autowired
	private SSCCTimelineDao ssccTimelineDao;

	/**
	 * /saves an sscc child plan guide topic to the corresponding child plan
	 * narrative table
	 *
	 * @param childPlanGuideTopicDto
	 *            the child plan guide topic dto
	 * @return the long
	 */
	public Long saveChildPlanGuideTopicForSSCCData(ChildPlanGuideTopicDto childPlanGuideTopicDto) {
		Long rowCount = ServiceConstants.ZERO_VAL;
		childPlanGuideTopicDto = childPlanBeanDao.getTopicDtLastUpdate(childPlanGuideTopicDto);
		childPlanBeanDao.saveGuideTopic(childPlanGuideTopicDto);
		return rowCount;
	}

	/**
	 * create sscc timeline record
	 * 
	 * @param valueBean
	 *            The SSCCChildPlanValueBean containing the details of the plan.
	 * @param table
	 *            The name of the table the timeline event refers to
	 * @param desc
	 *            The description of the timeline event
	 * @return
	 */
	public void createTimelineRecord(SSCCChildPlanDto ssccChildPlanDto, String table, String desc, Long user) {
		ssccChildPlanDto = ssccChildPlanDao.querySSCCChildPlan(ssccChildPlanDto);
		SSCCTimelineDto inSSCCTimelineDto = new SSCCTimelineDto();
		if (ssccRefDao.isUserSSCCExternal(user, ssccChildPlanDto.getCdRegion())) {
			inSSCCTimelineDto.setIdSSCCResource(ssccChildPlanDto.getIdRsrc());
		}
		inSSCCTimelineDto.setIdCreatedPerson(user);
		inSSCCTimelineDto.setIdLastUpdatePerson(user);
		inSSCCTimelineDto.setIdSsccReferral(ssccChildPlanDto.getIdSSCCReferral());
		inSSCCTimelineDto.setIdReference(ssccChildPlanDto.getIdSsccChildPlan());
		inSSCCTimelineDto.setCdTimelineTableName(table);
		inSSCCTimelineDto.setTxtTimelineDesc(desc);
		ssccRefDao.saveSSCCTimeline(inSSCCTimelineDto);
	}

	/**
	 * create sscc timeline record
	 * 
	 * @param valueBean
	 *            The SSCCChildPlanValueBean containing the details of the plan.
	 * @param table
	 *            The name of the table the timeline event refers to
	 * @param desc
	 *            The description of the timeline event
	 * @return
	 */
	public Long createTimelineRecord(SSCCChildPlanDto ssccChildPlanDto, SSCCTimelineDto ssccTimelineDto) {
		Long primaryKey = ServiceConstants.ZERO_VAL;
		ssccChildPlanDto = ssccChildPlanDao.querySSCCChildPlan(ssccChildPlanDto);
		if (ssccRefDao.isUserSSCCExternal(ssccTimelineDto.getIdCreatedPerson(), ssccChildPlanDto.getCdRegion())) {
			ssccTimelineDto.setIdSSCCResource(ssccChildPlanDto.getIdRsrc());
		}
		ssccTimelineDto.setIdSsccReferral(ssccChildPlanDto.getIdSSCCReferral());
		ssccTimelineDto.setIdReference(ssccChildPlanDto.getIdSsccChildPlan());
		primaryKey = ssccRefDao.saveSSCCTimeline(ssccTimelineDto);
		return primaryKey;
	}

	/**
	 * create sscc timeline record.
	 *
	 * @param ssccTimelineDto
	 *            the sscc timeline dto
	 * @return the list
	 */
	public List<SSCCTimelineDto> queryTimelineRecordList(SSCCTimelineDto ssccTimelineDto) {
		List<SSCCTimelineDto> ssccTimelineDtoList = new ArrayList<SSCCTimelineDto>();
		ssccTimelineDtoList = ssccTimelineDao.getSSCCTimelineList(ssccTimelineDto);
		return ssccTimelineDtoList;
	}

	public String getSSCCStatusString(String cdStatus) {

		int status = Integer.parseInt(cdStatus);
		String szStatus = "";
		switch (status) {
		case 20:
			szStatus = "Assigned - No Content";
			break;
		case 30:
			szStatus = "Assigned - Content";
			break;
		case 70:
			szStatus = "Rescinded";
			break;
		case 60:
			szStatus = "Ready to Review";
			break;
		case 140:
			szStatus = "Unlocked";
			break;
		case 90:
			szStatus = "Approved";
			break;
		case 100:
			szStatus = "Rejected";
			break;
		case 80:
			szStatus = "Re-Propose";
			break;
		default:
			break;
		}

		return szStatus;
	}

	public Boolean hasProposeStatus(SSCCChildPlanDto ssccChildPlanDto) {
		Boolean hasProposeStatus = Boolean.FALSE;
		if ((!TypeConvUtil.isNullOrEmpty(ssccChildPlanDto.getCdStatus()))
				&& ssccChildPlanDto.getCdStatus().equals(ServiceConstants.CSSCCSTA_60)
				|| (!TypeConvUtil.isNullOrEmpty(ssccChildPlanDto.getCdParticipStatus())
						&& ssccChildPlanDto.getCdParticipStatus().equals(ServiceConstants.CSSCCSTA_60)))
			return Boolean.TRUE;

		List<SSCCChildPlanGuideTopicDto> planGuideTopicDtoList = ssccChildPlanDao
				.querySSCCChildPlanTopicData(ssccChildPlanDto);
		for (SSCCChildPlanGuideTopicDto topic : planGuideTopicDtoList) {
			if (topic.getCdStatus().equals(ServiceConstants.CSSCCSTA_60)) {
				hasProposeStatus = Boolean.TRUE;
				break;
			}
		}

		return hasProposeStatus;
	}

}
