package us.tx.state.dfps.service.kinapproval.serviceimpl;

import static us.tx.state.dfps.service.approval.serviceimpl.ApprovalStatusServiceImpl.KINSHIP_AUTOMATED_SYSTEM_ID;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.kin.dto.KinHomeInfoDto;
import us.tx.state.dfps.service.kin.dto.ResourceServiceDto;
import us.tx.state.dfps.service.kinapproval.service.KinApprovalService;
import us.tx.state.dfps.service.kinhomeinfo.dao.KinHomeInfoDao;
import us.tx.state.dfps.service.kinhomeinfo.service.KinHomeInfoService;
import us.tx.state.dfps.service.kinresourceservice.service.KinResourceServiceService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * KinApprovalServiceImpl May 14, 2018- 8:52:24 AM © 2018 Texas Department of
 * Family and Protective Services
 */

@Service
@Transactional
public class KinApprovalServiceImpl implements KinApprovalService {

	@Autowired
	private KinResourceServiceService kinResService;

	@Autowired
	private KinHomeInfoService kinHomeInfoService;

	@Autowired
	KinHomeInfoDao kinHomeInfoDao;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-KinApprovalServiceLog");

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long doKinshipResourceApproval(KinHomeInfoDto inputKinHomeInfoDto) {
		Long updatedResult = 0l;
		try {
			KinHomeInfoDto kinHomeInfoDto = new KinHomeInfoDto();

			kinHomeInfoDto = kinHomeInfoService.getKinHomeInfo(inputKinHomeInfoDto.getIdHomeStage());
			kinHomeInfoDto.setUserId(inputKinHomeInfoDto.getUserId());
			kinHomeInfoDto.setPersonFullName(inputKinHomeInfoDto.getPersonFullName());

			String homeStatus = kinHomeInfoDto.getHomeStatus();

			Long idResource = kinHomeInfoDto.getIdHomeResource();

			if (ServiceConstants.HOME_PEND_APPRV_STATUS.equals(homeStatus)) {
				kinHomeInfoDto.setHomeStatus(ServiceConstants.HOME_APPRV_STATUS);
				kinHomeInfoService.updateKinHomeStatus(kinHomeInfoDto);
			} else if (ServiceConstants.HOME_PEND_COURT_ORDERED_STATUS.equals(homeStatus)) {
				kinHomeInfoDto.setHomeStatus(ServiceConstants.HOME_COURT_ORDERED_STATUS);
				kinHomeInfoService.updateKinHomeStatus(kinHomeInfoDto);
			} else if (ServiceConstants.HOME_PEND_CLOSED_STATUS.equals(homeStatus)) {
				kinHomeInfoDto.setHomeStatus(ServiceConstants.HOME_CLOSED_STATUS);
				kinHomeInfoService.updateKinHomeInfrmtnStatus(kinHomeInfoDto);
			}

			KinHomeInfoDto kinHomeResource = new KinHomeInfoDto();
			kinHomeResource = kinHomeInfoService.getResServiceInfo(kinHomeInfoDto);

			if (!(ServiceConstants.HOME_PEND_CLOSED_STATUS.equals(homeStatus))) {

				String indTrain = kinHomeInfoDao.getKinTrainCompleted(kinHomeInfoDto.getIdHomeStage());

				ResourceServiceDto inKinResService = new ResourceServiceDto();
				ResourceServiceDto resultOfKinResService = new ResourceServiceDto();

				if (!TypeConvUtil.isNullOrEmpty(idResource)) {
					inKinResService.setIdResource(idResource);
				}

				inKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68B);
				inKinResService.setCdRsrcSvcCnty(kinHomeResource.getResourceAddressCounty());

				resultOfKinResService = kinResService.getResourceService(inKinResService);

				if (!ObjectUtils.isEmpty(resultOfKinResService) &&
						!ObjectUtils.isEmpty(resultOfKinResService.getIdResourceService())) {
					resultOfKinResService = populateUpdateResourceService(resultOfKinResService, kinHomeResource,
							indTrain);
					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68B);
					kinResService.updateResourceService(resultOfKinResService);
				} else {
					resultOfKinResService = populateInsertResourceService(kinHomeResource, indTrain);
					if (!TypeConvUtil.isNullOrEmpty(idResource)) {
						resultOfKinResService.setIdResource(idResource);
					}

					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68B);
					kinResService.insertResourceService(resultOfKinResService);
				}

				inKinResService.setCdRsrcSvcCnty(kinHomeResource.getResourceAddressCounty());
				inKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68C);

				resultOfKinResService = kinResService.getResourceService(inKinResService);

				if (!ObjectUtils.isEmpty(resultOfKinResService) &&
						!ObjectUtils.isEmpty(resultOfKinResService.getIdResourceService())) {
					resultOfKinResService = populateUpdateResourceService(resultOfKinResService, kinHomeResource,
							indTrain);
					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68C);
					kinResService.updateResourceService(resultOfKinResService);
				} else {
					resultOfKinResService = populateInsertResourceService(kinHomeResource, indTrain);
					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68C);
					if (!TypeConvUtil.isNullOrEmpty(idResource)) {
						resultOfKinResService.setIdResource(idResource);
					}

					kinResService.insertResourceService(resultOfKinResService);
				}

				inKinResService.setCdRsrcSvcCnty(kinHomeResource.getResourceAddressCounty());
				inKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68D);

				resultOfKinResService = kinResService.getResourceService(inKinResService);

				if (!ObjectUtils.isEmpty(resultOfKinResService) &&
						!ObjectUtils.isEmpty(resultOfKinResService.getIdResourceService())) {
					resultOfKinResService = populateUpdateResourceService(resultOfKinResService, kinHomeResource,
							indTrain);
					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68D);
					kinResService.updateResourceService(resultOfKinResService);
				} else {
					resultOfKinResService = populateInsertResourceService(kinHomeResource, indTrain);
					if (!TypeConvUtil.isNullOrEmpty(idResource)) {
						resultOfKinResService.setIdResource(idResource);
					}

					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68D);
					kinResService.insertResourceService(resultOfKinResService);
				}

				inKinResService.setCdRsrcSvcCnty(kinHomeResource.getResourceAddressCounty());
				inKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68E);

				resultOfKinResService = kinResService.getResourceService(inKinResService);

				if (!ObjectUtils.isEmpty(resultOfKinResService) &&
						!ObjectUtils.isEmpty(resultOfKinResService.getIdResourceService())) {
					resultOfKinResService = populateUpdateResourceService(resultOfKinResService, kinHomeResource,
							indTrain);
					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68E);
					kinResService.updateResourceService(resultOfKinResService);
				} else {

					resultOfKinResService = populateInsertResourceService(kinHomeResource, indTrain);
					if (!TypeConvUtil.isNullOrEmpty(idResource)) {
						resultOfKinResService.setIdResource(idResource);
					}

					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68E);
					kinResService.insertResourceService(resultOfKinResService);
				}

				inKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68F);
				inKinResService.setCdRsrcSvcCnty(kinHomeResource.getResourceAddressCounty());

				resultOfKinResService = kinResService.getResourceService(inKinResService);

				if (!ObjectUtils.isEmpty(resultOfKinResService) &&
						!ObjectUtils.isEmpty(resultOfKinResService.getIdResourceService())) {
					resultOfKinResService = populateUpdateResourceService(resultOfKinResService, kinHomeResource,
							indTrain);
					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68F);
					kinResService.updateResourceService(resultOfKinResService);
				} else {
					resultOfKinResService = populateInsertResourceService(kinHomeResource, indTrain);
					if (!TypeConvUtil.isNullOrEmpty(idResource)) {
						resultOfKinResService.setIdResource(idResource);
					}

					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68F);
					kinResService.insertResourceService(resultOfKinResService);
				}

				inKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68G);
				inKinResService.setCdRsrcSvcCnty(kinHomeResource.getResourceAddressCounty());

				resultOfKinResService = kinResService.getResourceService(inKinResService);

				if (!ObjectUtils.isEmpty(resultOfKinResService) &&
						!ObjectUtils.isEmpty(resultOfKinResService.getIdResourceService())) {
					resultOfKinResService = populateUpdateResourceService(resultOfKinResService, kinHomeResource,
							indTrain);
					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68G);
					kinResService.updateResourceService(resultOfKinResService);
				} else {
					resultOfKinResService = populateInsertResourceService(kinHomeResource, indTrain);
					if (!TypeConvUtil.isNullOrEmpty(idResource)) {
						resultOfKinResService.setIdResource(idResource);
					}

					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68G);
					kinResService.insertResourceService(resultOfKinResService);
				}

				inKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68H);
				inKinResService.setCdRsrcSvcCnty(kinHomeResource.getResourceAddressCounty());

				resultOfKinResService = kinResService.getResourceService(inKinResService);

				if (!ObjectUtils.isEmpty(resultOfKinResService) &&
						!ObjectUtils.isEmpty(resultOfKinResService.getIdResourceService())) {
					resultOfKinResService = populateUpdateResourceService(resultOfKinResService, kinHomeResource,
							indTrain);
					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68H);
					kinResService.updateResourceService(resultOfKinResService);
				} else {
					resultOfKinResService = populateInsertResourceService(kinHomeResource, indTrain);
					if (!TypeConvUtil.isNullOrEmpty(idResource)) {
						resultOfKinResService.setIdResource(idResource);
					}

					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68H);
					kinResService.insertResourceService(resultOfKinResService);
				}

				inKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68I);
				inKinResService.setCdRsrcSvcCnty(kinHomeResource.getResourceAddressCounty());

				resultOfKinResService = kinResService.getResourceService(inKinResService);

				if (!ObjectUtils.isEmpty(resultOfKinResService) &&
						!ObjectUtils.isEmpty(resultOfKinResService.getIdResourceService())) {
					resultOfKinResService = populateUpdateResourceService(resultOfKinResService, kinHomeResource,
							indTrain);
					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68I);
					kinResService.updateResourceService(resultOfKinResService);
				} else {
					resultOfKinResService = populateInsertResourceService(kinHomeResource, indTrain);
					if (!TypeConvUtil.isNullOrEmpty(idResource)) {
						resultOfKinResService.setIdResource(idResource);
					}

					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68I);
					kinResService.insertResourceService(resultOfKinResService);
				}

				inKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68J);
				inKinResService.setCdRsrcSvcCnty(kinHomeResource.getResourceAddressCounty());

				resultOfKinResService = kinResService.getResourceService(inKinResService);

				if (!ObjectUtils.isEmpty(resultOfKinResService) &&
						!ObjectUtils.isEmpty(resultOfKinResService.getIdResourceService())) {
					resultOfKinResService = populateUpdateResourceService(resultOfKinResService, kinHomeResource,
							indTrain);
					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68J);
					kinResService.updateResourceService(resultOfKinResService);
				} else {
					resultOfKinResService = populateInsertResourceService(kinHomeResource, indTrain);
					if (!TypeConvUtil.isNullOrEmpty(idResource)) {
						resultOfKinResService.setIdResource(idResource);
					}

					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68J);
					kinResService.insertResourceService(resultOfKinResService);
				}

				inKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68K);
				inKinResService.setCdRsrcSvcCnty(kinHomeResource.getResourceAddressCounty());

				resultOfKinResService = kinResService.getResourceService(inKinResService);

				if (!ObjectUtils.isEmpty(resultOfKinResService) &&
						!ObjectUtils.isEmpty(resultOfKinResService.getIdResourceService())) {
					resultOfKinResService = populateUpdateResourceService(resultOfKinResService, kinHomeResource,
							indTrain);
					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68K);
					kinResService.updateResourceService(resultOfKinResService);
				} else {
					resultOfKinResService = populateInsertResourceService(kinHomeResource, indTrain);
					if (!TypeConvUtil.isNullOrEmpty(idResource)) {
						resultOfKinResService.setIdResource(idResource);
					}

					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68K);
					kinResService.insertResourceService(resultOfKinResService);
				}

				inKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68L);
				inKinResService.setCdRsrcSvcCnty(kinHomeResource.getResourceAddressCounty());

				resultOfKinResService = kinResService.getResourceService(inKinResService);

				if (!ObjectUtils.isEmpty(resultOfKinResService) &&
						!ObjectUtils.isEmpty(resultOfKinResService.getIdResourceService())) {
					resultOfKinResService = populateUpdateResourceService(resultOfKinResService, kinHomeResource,
							indTrain);
					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68L);
					kinResService.updateResourceService(resultOfKinResService);
				} else {
					resultOfKinResService = populateInsertResourceService(kinHomeResource, indTrain);
					if (!TypeConvUtil.isNullOrEmpty(idResource)) {
						resultOfKinResService.setIdResource(idResource);
					}

					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68L);
					kinResService.insertResourceService(resultOfKinResService);
				}

				inKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68M);
				inKinResService.setCdRsrcSvcCnty(kinHomeResource.getResourceAddressCounty());

				resultOfKinResService = kinResService.getResourceService(inKinResService);

				if (!ObjectUtils.isEmpty(resultOfKinResService) &&
						!ObjectUtils.isEmpty(resultOfKinResService.getIdResourceService())) {
					resultOfKinResService = populateUpdateResourceService(resultOfKinResService, kinHomeResource,
							indTrain);
					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68M);
					kinResService.updateResourceService(resultOfKinResService);
				} else {
					resultOfKinResService = populateInsertResourceService(kinHomeResource, indTrain);
					if (!TypeConvUtil.isNullOrEmpty(idResource)) {
						resultOfKinResService.setIdResource(idResource);
					}

					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68M);
					kinResService.insertResourceService(resultOfKinResService);
				}

				inKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68N);
				inKinResService.setCdRsrcSvcCnty(kinHomeResource.getResourceAddressCounty());

				resultOfKinResService = kinResService.getResourceService(inKinResService);

				if (!ObjectUtils.isEmpty(resultOfKinResService) &&
						!ObjectUtils.isEmpty(resultOfKinResService.getIdResourceService())) {
					resultOfKinResService = populateUpdateResourceService(resultOfKinResService, kinHomeResource,
							indTrain);
					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68N);
					kinResService.updateResourceService(resultOfKinResService);
				} else {
					resultOfKinResService = populateInsertResourceService(kinHomeResource, indTrain);
					if (!TypeConvUtil.isNullOrEmpty(idResource)) {
						resultOfKinResService.setIdResource(idResource);
					}
					resultOfKinResService.setCdRsrcSvcService(ServiceConstants.KIN_HOME_SET_SERV_CODE_68N);
					kinResService.insertResourceService(resultOfKinResService);
				}
			} else {
				kinHomeInfoDto.setCdStageProgStage(ServiceConstants.KIN_HOME_STAGE_CD);
				kinHomeInfoDto.setCdStageProgProgram(ServiceConstants.KIN_HOME_STAGE_PROG_CPS);
				kinHomeInfoDto.setResourceClosureReason(kinHomeResource.getResourceClosureReason());

				kinHomeInfoDto.setResourceKinHomeType1(kinHomeResource.getResourceKinHomeType1());
				kinHomeInfoService.closeKinHome(kinHomeInfoDto);
			}
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}
		return updatedResult;
	}

	/**
	 * Populates an ResourceService value object for save with values from the
	 * KinHomeinfo and Training indicator flag.
	 * 
	 * @param ccmn34so
	 *            Returned by CCMN34S
	 * @return
	 */
	private ResourceServiceDto populateInsertResourceService(KinHomeInfoDto kinHomeInfo, String indTrain) {
		ResourceServiceDto resService = new ResourceServiceDto();
		resService.setCdRsrcSvcCnty(kinHomeInfo.getResourceAddressCounty());
		resService.setCdRsrcSvcState(kinHomeInfo.getResourceAddressState());
		resService.setCdRsrcSvcRegion(kinHomeInfo.getRegion());
		resService.setIndKinshipHomeAssmnt(ServiceConstants.KIN_HOME_SET_IND_Y);
		resService.setIndKinshipTraining(indTrain);
		resService.setIndKinshipIncome(kinHomeInfo.getResourceIncomeQualification());
		resService.setIndKinshipAgreement(kinHomeInfo.getResourceSignedAgreement());
		resService.setCdRsrcSvcCategRsrc(ServiceConstants.KIN_HOME_SET_RES_SERV_CAT);
		resService.setCdRsrcSvcProgram(ServiceConstants.KIN_HOME_SET_RES_SERV_PGRM);

		resService.setIndRsrcSvcCntyPartial(ServiceConstants.KIN_HOME_SET_IND_N);
		resService.setIndRsrcSvcIncomeBsed(ServiceConstants.KIN_HOME_SET_IND_N);
		resService.setIndRsrcSvcShowRow(ServiceConstants.KIN_HOME_SET_IND_Y);

		resService.setDtLastUpdate(new Date());
		resService.setLastUpdatedPersonId(KINSHIP_AUTOMATED_SYSTEM_ID);
		resService.setCreatedDate(new Date());
		resService.setCreatedPersonId(KINSHIP_AUTOMATED_SYSTEM_ID);

		return resService;
	}

	private ResourceServiceDto populateUpdateResourceService(ResourceServiceDto resServiceValueBean,
			KinHomeInfoDto kinHomeResource, String indTrain) {
		resServiceValueBean.setIndKinshipHomeAssmnt(ServiceConstants.KIN_HOME_SET_IND_Y);
		resServiceValueBean.setIndKinshipTraining(indTrain);
		resServiceValueBean.setIndKinshipIncome(kinHomeResource.getResourceIncomeQualification());
		resServiceValueBean.setIndKinshipAgreement(kinHomeResource.getResourceManualGiven());

		return resServiceValueBean;
	}
}