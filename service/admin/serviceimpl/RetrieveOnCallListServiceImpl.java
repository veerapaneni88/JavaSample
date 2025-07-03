package us.tx.state.dfps.service.admin.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.admin.dao.RtrvOnCallDao;
import us.tx.state.dfps.service.admin.dto.RetrieveOnCallListiDto;
import us.tx.state.dfps.service.admin.dto.RetrieveOnCallListoDto;
import us.tx.state.dfps.service.admin.dto.RtrvOnCallInDto;
import us.tx.state.dfps.service.admin.dto.RtrvOnCallOutDto;
import us.tx.state.dfps.service.admin.service.RetrieveOnCallListService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.RetrieveOnCallListRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.person.dto.RtrvOnCallCntyDto;
import us.tx.state.dfps.service.workload.dao.OnCallDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:It retrieves
 * a full row of the ON_CALL table based on dynamic input. Aug 17, 2017- 5:55:41
 * PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class RetrieveOnCallListServiceImpl implements RetrieveOnCallListService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	RtrvOnCallDao objCcmn16dDao;

	@Autowired
	OnCallDao onCallDao;

	private static final Logger log = Logger.getLogger(RetrieveOnCallListServiceImpl.class);

	/**
	 *
	 * Method Name: callRetrieveOnCallListService Method Description: This
	 * Method retrieves a full row of the ON_CALL table based on dynamic input.
	 * 
	 * @param pInputMsg
	 * @return List<RetrieveOnCallListoDto> @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RetrieveOnCallListRes callRetrieveOnCallListService(RetrieveOnCallListiDto pInputMsg) {
		log.debug("Entering method callRetrieveOnCallListService in RetrieveOnCallListServiceImpl");
		List<RetrieveOnCallListoDto> response = null;
		RtrvOnCallInDto ccmn16diDto = new RtrvOnCallInDto();
		Boolean canLaunchReport = Boolean.TRUE;
		List<String> szCdOnCallCountyList = new ArrayList<String>();
		/* checks the conditions if the function is not ADD */
		if (!pInputMsg.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_FUNC_CD_ADD)) {
			ccmn16diDto.setCdRegion(pInputMsg.getSzCdRegion());
			/* selects all counties when region state wide is selected */
			if (ccmn16diDto.getCdRegion().equalsIgnoreCase(ServiceConstants.REGION_STATE_WIDE)) {
				ccmn16diDto.setCdCountyCounter(1l);
				szCdOnCallCountyList.add(ServiceConstants.ALLCOUNTIES);
				ccmn16diDto.setCdOnCallCounty(szCdOnCallCountyList);
			} else {
				pInputMsg.setSzCdCountyCounter(pInputMsg.getSzCdOnCallCounty().size());
				/* Iteration based on the counties selected */
				for (int i = 0; i < pInputMsg.getSzCdCountyCounter(); i++) {
					String szCdOnCallCounty = pInputMsg.getSzCdOnCallCounty().get(i);
					szCdOnCallCountyList.add(szCdOnCallCounty);
				}
				if (!TypeConvUtil.isNullOrEmpty(szCdOnCallCountyList)) {
					ccmn16diDto.setCdOnCallCounty(szCdOnCallCountyList);
				}
				if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getSzCdCountyCounter())) {
					ccmn16diDto.setCdCountyCounter(pInputMsg.getSzCdCountyCounter());
				}
			}
			ccmn16diDto.setCdOnCallProgram(pInputMsg.getSzCdOnCallProgram());
			if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getSzCdOnCallType())) {
				ccmn16diDto.setCdOnCallType(pInputMsg.getSzCdOnCallType());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getDtOnCallStart())) {
				ccmn16diDto.setStrOnCallStart(DateUtils.dateString(pInputMsg.getDtOnCallStart()));
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getTmOnCallStart())) {
				ccmn16diDto.setTmOnCallStart(pInputMsg.getTmOnCallStart());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getDtOnCallEnd())) {
				ccmn16diDto.setStrOnCallEnd(DateUtils.dateString(pInputMsg.getDtOnCallEnd()));
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getTmOnCallEnd())) {
				ccmn16diDto.setTmOnCallEnd(pInputMsg.getTmOnCallEnd());
			}
			/* Call to DAO based on the input */
			List<RtrvOnCallOutDto> resources = objCcmn16dDao.getOnCallForCountyProgram(ccmn16diDto);
			List<RtrvOnCallCntyDto> scheduleCounties;
			/* condition to check if the resources obtained */
			if (!TypeConvUtil.isNullOrEmpty(resources) && resources.size() > 0) {
				response = new ArrayList<RetrieveOnCallListoDto>();
				for (int i = 0; i < resources.size(); i++) {
					RtrvOnCallOutDto ccmn16doDto = (RtrvOnCallOutDto) resources.get(i);
					RetrieveOnCallListoDto pOutputMsg = new RetrieveOnCallListoDto();
					if (!TypeConvUtil.isNullOrEmpty(ccmn16doDto.getCdRegion())) {
						pOutputMsg.setCdRegion(ccmn16doDto.getCdRegion());
					} else {
						pOutputMsg.setCdRegion(pInputMsg.getSzCdRegion());
					}
					if (!TypeConvUtil.isNullOrEmpty(ccmn16doDto.getCdOnCallCounty())) {
						pOutputMsg.setCdOnCallCounty(ccmn16doDto.getCdOnCallCounty());
					}
					if (!TypeConvUtil.isNullOrEmpty(ccmn16doDto.getCdOnCallProgram())) {
						pOutputMsg.setCdOnCallProgram(ccmn16doDto.getCdOnCallProgram());
					}
					if (!TypeConvUtil.isNullOrEmpty(ccmn16doDto.getCdOnCallType())) {
						pOutputMsg.setCdOnCallType(ccmn16doDto.getCdOnCallType());
					}
					if (!TypeConvUtil.isNullOrEmpty(ccmn16doDto.getDtOnCallStartStr())) {
						pOutputMsg
								.setDtOnCallStart(DateUtils.stringDateAndTimestamp(ccmn16doDto.getDtOnCallStartStr()));
					}
					if (!TypeConvUtil.isNullOrEmpty(ccmn16doDto.getTmOnCallStart())) {
						pOutputMsg.setTmOnCallStart(ccmn16doDto.getTmOnCallStart());
					}
					if (!TypeConvUtil.isNullOrEmpty(ccmn16doDto.getDtOnCallEndStr())) {
						pOutputMsg.setDtOnCallEnd(DateUtils.stringDateAndTimestamp(ccmn16doDto.getDtOnCallEndStr()));
					}
					if (!TypeConvUtil.isNullOrEmpty(ccmn16doDto.getTmOnCallEnd())) {
						pOutputMsg.setTmOnCallEnd(ccmn16doDto.getTmOnCallEnd());
					}
					if (!TypeConvUtil.isNullOrEmpty(ccmn16doDto.getIdOnCall())) {
						pOutputMsg.setIdOnCall(ccmn16doDto.getIdOnCall());
					}
					if (!TypeConvUtil.isNullOrEmpty(ccmn16doDto.getOnCallFilled())) {
						pOutputMsg.setOnCallFilled(ccmn16doDto.getOnCallFilled());
					}
					if (!TypeConvUtil.isNullOrEmpty(ccmn16doDto.getCountOfCounty())) {
						pOutputMsg.setCountOfCounty(ccmn16doDto.getCountOfCounty());
					}
					if (!TypeConvUtil.isNullOrEmpty(ccmn16doDto.getTsLastUpdate())) {
						pOutputMsg.setTsLastUpdate(ccmn16doDto.getTsLastUpdate());
					}
					pOutputMsg.setDtWCDDtSystemDate(null);
					response.add(pOutputMsg);

					// The below code is added for setting the boolean which
					// will be used to decide whether to launch the On-Call
					// Report of not
					List<String> strScheduleCounties;
					if (canLaunchReport) {
						scheduleCounties = new ArrayList<>();
						scheduleCounties = onCallDao.getOnCallCounty(pOutputMsg.getCdRegion(),
								pOutputMsg.getIdOnCall());
						strScheduleCounties = new ArrayList<>();
						strScheduleCounties = scheduleCounties.stream().map(x -> x.getCdOnCallCounty())
								.collect(Collectors.toList());
						for (String cdSearchCounty : szCdOnCallCountyList) {
							if (!strScheduleCounties.contains(cdSearchCounty)) {
								canLaunchReport = Boolean.FALSE;
								break;
							}
						}
					}
				}
			}
		}
		log.debug("Exiting method callRetrieveOnCallListService in RetrieveOnCallListServiceImpl");
		RetrieveOnCallListRes retrieveOnCallListRes = new RetrieveOnCallListRes();
		retrieveOnCallListRes.setCanLaunchReport(canLaunchReport);
		retrieveOnCallListRes.setRetrieveOnCallListoDto(response);
		return retrieveOnCallListRes;
	}
}
