package us.tx.state.dfps.service.workload.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.admin.service.UnitService;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.serviceimpl.CaseSummaryServiceImpl;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.AssignWorkloadReq;
import us.tx.state.dfps.service.common.response.AssignWorkloadRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.workload.dao.AssignWorkloadDao;
import us.tx.state.dfps.service.workload.dao.StageWorkloadDao;
import us.tx.state.dfps.service.workload.dto.AssignWorkloadDto;
import us.tx.state.dfps.service.workload.dto.RcciMrefDto;
import us.tx.state.dfps.service.workload.service.AssignWorkloadService;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN14S Class
 * Description: This class is doing service Implementation for AssignWorkload
 * Mar 23, 2017 - 3:23:07 PM
 */

@Service
@Transactional
public class AssignWorkloadServiceImpl implements AssignWorkloadService {

	@Autowired
	AssignWorkloadDao assignWorkloadDao;

	@Autowired
	UnitService unitService;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	StageWorkloadDao stageWorkloadDao;

	private static final Logger log = Logger.getLogger(AssignWorkloadServiceImpl.class);

	public AssignWorkloadServiceImpl() {

	}

	/**
	 * 
	 * Method Description: This Method is used to retrieve Assigned Workload to
	 * worker by giving person_id in request object(AssignWorkloadReq) Tuxedo
	 * Service Name: CCMN14S
	 * 
	 * @param assignWorkloadReq
	 * @return assignWorkloadServiceOutput
	 * @throws Exception
	 */

	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public AssignWorkloadRes getAssignWorkloadDetails(AssignWorkloadReq assignWorkloadReq) {
		AssignWorkloadRes assignWorkloadRes = new AssignWorkloadRes();
		String retMsg = "";
		if (!TypeConvUtil.isNullOrEmpty(assignWorkloadReq.getUlIdStage()) && assignWorkloadReq.getUlIdStage().size() > 0
				&& null != (assignWorkloadReq.getUlIdStage()).get(0))

		{
			if (assignWorkloadReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_FUNC_CD_UPDATE))
				retMsg = stagePersonLinkDao.StgPrsnLinkUpdt(assignWorkloadReq.getUlIdStage(),
						assignWorkloadReq.getUlIdPerson());
		}

		assignWorkloadRes.setRetMsg(retMsg);
		List<Long> idPersonList = new ArrayList<Long>();
		idPersonList.add(assignWorkloadReq.getUlIdPerson());
		List<AssignWorkloadDto> assignWorkloadDtoList = assignWorkloadDao.getAssignWorkloadDetails(assignWorkloadReq);

		// Get list of cases for RCL and CCL stages in the result
		final List<Long> rcciMrefCandidateList = new LinkedList<>();
		if (!ObjectUtils.isEmpty(assignWorkloadDtoList)) {
			assignWorkloadDtoList.stream().filter(dtl ->
					CodesConstant.CPGRMS_RCL.equals(dtl.getWkldStageProgram()) || CodesConstant.CPGRMS_CCL.equals(dtl.getWkldStageProgram()))
					.forEach(rcciCandidate ->
							rcciMrefCandidateList.add(rcciCandidate.getIdWkldCase()
					));
		}
		// do query to find RCCI Mref data for those stages' cases
		if (rcciMrefCandidateList != null) {
			List<RcciMrefDto> mrefDtoList = stageWorkloadDao.getRcciMrefDataByCaseList(rcciMrefCandidateList);

			if (!ObjectUtils.isEmpty(mrefDtoList)) {
				// convert to map
				Map<Long, RcciMrefDto> rcciMrefDtoMap = new HashMap<>();
				mrefDtoList.stream().forEach(currMref -> rcciMrefDtoMap.put(currMref.getIdStage(), currMref));

				// apply RCCI Mref data to result.
				assignWorkloadDtoList.stream().forEach(currWkld -> {
					RcciMrefDto currMref = rcciMrefDtoMap.get(currWkld.getIdWkldStage());
					if (currMref != null) {
						currWkld.setRcciMrefCount(CaseSummaryServiceImpl.applyRcciMrefThresholds(currMref.getRcciMrefCnt(),
								currWkld.getWkldStageProgram(), currMref.getNbrRsrcFacilCapacity(),
								currMref.getCdRsrcFacilType()));
						currWkld.setNbrRsrcFacilCapacity(currMref.getNbrRsrcFacilCapacity());
						currWkld.setCdRsrcFacilType(currMref.getCdRsrcFacilType());
					}
				});
			}
		}

		assignWorkloadRes.setbSysIndSupervisor((unitService.unitAccess(null, assignWorkloadReq.getSzCdUnitProgram(),
				assignWorkloadReq.getSzCdUnitRegion(), assignWorkloadReq.getSzNbrUnit().toString(), idPersonList))
						? ServiceConstants.STRING_IND_Y : ServiceConstants.STRING_IND_N);
		assignWorkloadRes.setAssignWorkloadDtoList(assignWorkloadDtoList);
		log.info("TransactionId :" + assignWorkloadReq.getTransactionId());
		return assignWorkloadRes;
	}
}
