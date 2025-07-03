package us.tx.state.dfps.service.subcontractor.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ResourceReq;
import us.tx.state.dfps.service.common.request.SubcontrAreaServedReq;
import us.tx.state.dfps.service.common.request.SubcontrListRtrvReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.ResourceRes;
import us.tx.state.dfps.service.common.response.SubcontrAreaServedRes;
import us.tx.state.dfps.service.common.response.SubcontrListRtrvRes;
import us.tx.state.dfps.service.common.response.SubcontrListSaveRes;
import us.tx.state.dfps.service.kin.dto.ResourceServiceDto;
import us.tx.state.dfps.service.placement.dao.ContractDao;
import us.tx.state.dfps.service.placement.dto.ContractCountyPeriodInDto;
import us.tx.state.dfps.service.resource.detail.dto.RsrcLinkInsUpdDelInDto;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.resourcedetail.dao.RsrcLinkDao;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.subcontractor.controller.SbcntrListSaveValidator;
import us.tx.state.dfps.service.subcontractor.dao.CapsResourceRsrcLinkDao;
import us.tx.state.dfps.service.subcontractor.dao.CcntyregDao;
import us.tx.state.dfps.service.subcontractor.dao.ResourceServiceSvcDao;
import us.tx.state.dfps.service.subcontractor.dto.CapsResourceRsrcLinkInDto;
import us.tx.state.dfps.service.subcontractor.dto.CapsResourceRsrcLinkOutDto;
import us.tx.state.dfps.service.subcontractor.dto.CcntyregiDto;
import us.tx.state.dfps.service.subcontractor.dto.ResourceServiceSvcInDto;
import us.tx.state.dfps.service.subcontractor.dto.ResourceServiceSvcOutDto;
import us.tx.state.dfps.service.subcontractor.dto.SubcontrListRtrvoDto;
import us.tx.state.dfps.service.subcontractor.dto.SubcontrListSaveiDto;
import us.tx.state.dfps.service.subcontractor.service.SbcntrListService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION
 *
 * Class Description: This class will perform an add, update and/or delete to
 * the Resource Link table.
 *
 * Aug 16, 2017- 2:46:16 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Service
@Transactional
public class SbcntrListServiceImpl implements SbcntrListService {

	private static final String CAPS_UNIT_STATE_OFFICE = "99";
	private static final String CAPS_UNIT_SWI = "00";
	private static final String CAPS_DEFAULT_STATE_CD = "TX";

	@Autowired
	MessageSource messageSource;

	@Autowired
	RsrcLinkDao rsrcLinkInsUpdDelDao;

	@Autowired
	CapsResourceRsrcLinkDao capsResourceRsrcLinkDao;

	@Autowired
	ResourceServiceSvcDao resourceServiceSvcDao;

	@Autowired
	CcntyregDao ccntyregDao;

	@Autowired
	ContractDao contractDao;

	@Autowired
	CapsResourceDao capsResourceDao;

	@Autowired
	SbcntrListSaveValidator validator;

	/**
	 * Method name :saveSubContractorList
	 * 
	 * Method Description:This service will perform an add, update and/or delete
	 * to the Resource Link table.
	 * 
	 * Service name : CCON16S
	 * 
	 * @param liSbcntrListSaveiDto
	 *            - list of objects to be save/update or delete
	 * @return SbcntrListSaveRes - status of teh operation
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SubcontrListSaveRes saveSubContractorList(List<SubcontrListSaveiDto> liSbcntrListSaveiDto) {
		RsrcLinkInsUpdDelInDto pCAUD26DInputRec = null;
		SubcontrListSaveRes resp = new SubcontrListSaveRes();
		for (SubcontrListSaveiDto pInputMsg : liSbcntrListSaveiDto) {
			pCAUD26DInputRec = new RsrcLinkInsUpdDelInDto();
			pCAUD26DInputRec.setReqFuncCd(pInputMsg.getCdScrDataAction());
			if (!ObjectUtils.isEmpty(pInputMsg.getCdRsrcLinkService())) {
				pCAUD26DInputRec.setCdRsrcLinkService(pInputMsg.getCdRsrcLinkService());
			}
			if (!ObjectUtils.isEmpty(pInputMsg.getCdRsrcLinkType())) {
				pCAUD26DInputRec.setCdRsrcLinkType(pInputMsg.getCdRsrcLinkType());
			}
			if (!ObjectUtils.isEmpty(pInputMsg.getIdRsrcLink())) {
				pCAUD26DInputRec.setIdRsrcLink(pInputMsg.getIdRsrcLink());
			}
			if (!ObjectUtils.isEmpty(pInputMsg.getIdRsrcLinkChild())) {
				pCAUD26DInputRec.setIdRsrcLinkChild(pInputMsg.getIdRsrcLinkChild());
			}
			if (!ObjectUtils.isEmpty(pInputMsg.getIdRsrcLinkParent())) {
				pCAUD26DInputRec.setIdRsrcLinkParent(pInputMsg.getIdRsrcLinkParent());
			}
			if (!ObjectUtils.isEmpty(pInputMsg.getDtLastUpdate())) {
				pCAUD26DInputRec.setDtLastUpdate(pInputMsg.getDtLastUpdate());
			}
			resp = rsrcLinkInsUpdDelDao.saveRsrcLink(pCAUD26DInputRec);

		}
		return resp;
	}

	/**
	 * 
	 * Method Name: findSubcontractor
	 * 
	 * Method Description:This service will retrieve all resources that have
	 * been designated as sub contractors for the prime resource. It will also
	 * retrieve all services for which the prime resource may provide.
	 * 
	 * @param pInputMsg
	 * @return List<SubcontrListRtrvoDto>
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SubcontrListRtrvRes findSubcontractor(SubcontrListRtrvReq pInputMsg) {
		SubcontrListRtrvRes resp = new SubcontrListRtrvRes();
		List<SubcontrListRtrvoDto> liSbcntrListRtrvoDto = null;
		ResourceServiceSvcInDto pCLSS14DInputRec = new ResourceServiceSvcInDto();
		CapsResourceRsrcLinkInDto pCLSS18DInputRec = new CapsResourceRsrcLinkInDto();
		pCLSS18DInputRec.setIdRsrcLinkParent(pInputMsg.getIdRsrcLinkParent());
		// call Clss18d
		List<CapsResourceRsrcLinkOutDto> liClss18doDto = capsResourceRsrcLinkDao.getResource(pCLSS18DInputRec);
		if (!ObjectUtils.isEmpty(liClss18doDto)) {
			liSbcntrListRtrvoDto = new ArrayList<>();
			for (CapsResourceRsrcLinkOutDto objClss18doDto : liClss18doDto) {
				SubcontrListRtrvoDto objSbcntrListRtrvoDto = new SubcontrListRtrvoDto();
				objSbcntrListRtrvoDto.setCdRsrcLinkService(objClss18doDto.getCdRsrcLinkService());
				objSbcntrListRtrvoDto.setNmResource(objClss18doDto.getNmResource());
				objSbcntrListRtrvoDto.setDtLastUpdate(objClss18doDto.getRsrcLinkLastUpdate());
				objSbcntrListRtrvoDto.setIdRsrcLink(objClss18doDto.getIdRsrcLink());
				objSbcntrListRtrvoDto.setIdRsrcLinkChild(objClss18doDto.getIdRsrcLinkChild());
				liSbcntrListRtrvoDto.add(objSbcntrListRtrvoDto);
			}
			resp.setSbcntrListRtrvoDtoList(liSbcntrListRtrvoDto);
		} /*
			 * else { throw new DataNotFoundException(messageSource.getMessage(
			 * "sbcntrList.input.data", null, Locale.US)); }
			 */

		if (ServiceConstants.STRING_IND_Y.equalsIgnoreCase(pInputMsg.getIndSbcntrPredisplay())) {
			pCLSS14DInputRec.setIdResource(pInputMsg.getIdRsrcLinkParent());
			/// call Clss14d
			List<ResourceServiceSvcOutDto> liClss14doDto = resourceServiceSvcDao
					.getResourceServiceDetails(pCLSS14DInputRec);
			resp.setResourceServiceSvcOutDtoList(liClss14doDto);
		}
		return resp;
	}

	/**
	 * 
	 * Method Name: SbcntrListRtrvo
	 * 
	 * Method Description:This is the retrieval service for the Area Served
	 * window in order to populate the Area Served list in the pre display. Rows
	 * will only be returned if the show indicator is set to yes. In addition,
	 * it will be determined if the service is contracted and stored in the
	 * contracted indicator for each row returned. prime resource may provide.
	 * 
	 * Service name : CRES05S
	 * 
	 * @param objSbcntrListRtrviDto
	 * @return SubcontrListRtrvRes @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SubcontrAreaServedRes getAreaServedList(SubcontrAreaServedReq subcontrAreaServedReq) {
		SubcontrAreaServedRes resp = new SubcontrAreaServedRes();
		ResourceServiceSvcInDto resourceServiceSvcInDto = new ResourceServiceSvcInDto();
		resourceServiceSvcInDto.setIdResource(subcontrAreaServedReq.getIdResource());
		// call CRES10D
		List<ResourceServiceDto> resourceServiceDtoList = resourceServiceSvcDao
				.getResourceServiceById(resourceServiceSvcInDto);
		resp.setResourceServiceDtoList(resourceServiceDtoList);
		if (ObjectUtils.isEmpty(resourceServiceDtoList)) {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorCode(ServiceConstants.MSG_NO_ROWS_RETURNED);
			resp.setErrorDto(errorDto);
		} else {
			resp.setTotalRecCount(Long.valueOf(resourceServiceDtoList.size()));
		}
		processRsrcServices(subcontrAreaServedReq, resourceServiceDtoList);

		return resp;
	}

	private void processRsrcServices(SubcontrAreaServedReq subcontrAreaServedReq,
			List<ResourceServiceDto> resourceServiceDtoList) {
		for (ResourceServiceDto resourceServiceDto : resourceServiceDtoList) {
			if (StringUtils.isEmpty(resourceServiceDto.getCdRsrcSvcCnty())
					&& (!CAPS_UNIT_STATE_OFFICE.equalsIgnoreCase(resourceServiceDto.getCdRsrcSvcRegion()))
					&& (!StringUtils.isEmpty(resourceServiceDto.getCdRsrcSvcRegion())
							&& !CAPS_UNIT_SWI.equalsIgnoreCase(resourceServiceDto.getCdRsrcSvcRegion()))
					&& (CAPS_DEFAULT_STATE_CD.equalsIgnoreCase(resourceServiceDto.getCdRsrcSvcState()))) {
				CcntyregiDto ccntyregiDto = new CcntyregiDto();
				ccntyregiDto.setCdRsrcSvcRegion(resourceServiceDto.getCdRsrcSvcRegion());
				// call CRES32D
				List<String> cdRsrcSvcCntyList = ccntyregDao.getRegionCnty(ccntyregiDto);
				ContractCountyPeriodInDto contractCountyPeriodInDto = new ContractCountyPeriodInDto();
				contractCountyPeriodInDto.setIdResource(subcontrAreaServedReq.getIdResource());
				contractCountyPeriodInDto.setCdCncntyService(resourceServiceDto.getCdRsrcSvcService());

				resourceServiceDto.setIndRsrcContracted(ServiceConstants.STRING_IND_N);
				for (String cdRsrcSvcCnty : cdRsrcSvcCntyList) {
					contractCountyPeriodInDto.setCdCncntyCounty(cdRsrcSvcCnty);
					// call CRES33D
					CommonStringRes commonStringRes = contractDao.getCountyContracted(contractCountyPeriodInDto);
					String scrIndRsrcContracted = commonStringRes.getCommonRes();
					resourceServiceDto.setIndRsrcContracted(scrIndRsrcContracted);
					if (ServiceConstants.STRING_IND_Y.equalsIgnoreCase(scrIndRsrcContracted)) {
						break;
					}

				}

			} else if ((CAPS_UNIT_STATE_OFFICE.equalsIgnoreCase(resourceServiceDto.getCdRsrcSvcRegion()))
					|| (CAPS_UNIT_SWI.equalsIgnoreCase(resourceServiceDto.getCdRsrcSvcRegion()))
					|| (!CAPS_DEFAULT_STATE_CD.equalsIgnoreCase(resourceServiceDto.getCdRsrcSvcState()))) {
				resourceServiceDto.setIndRsrcContracted(ServiceConstants.STRING_IND_N);
			} else {
				ContractCountyPeriodInDto contractCountyPeriodInDto = new ContractCountyPeriodInDto();
				contractCountyPeriodInDto.setIdResource(subcontrAreaServedReq.getIdResource());
				contractCountyPeriodInDto.setCdCncntyService(resourceServiceDto.getCdRsrcSvcService());
				contractCountyPeriodInDto.setCdCncntyCounty(resourceServiceDto.getCdRsrcSvcCnty());
				// call CRES33D
				CommonStringRes commonStringRes = contractDao.getCountyContracted(contractCountyPeriodInDto);
				resourceServiceDto.setIndRsrcContracted(commonStringRes.getCommonRes());
			}

		}
	}

	/**
	 * 
	 * Method Name: getResourceInfo
	 * 
	 * Method Description: This Method is used to get the basic resource
	 * information for validations
	 * 
	 * @param resourceReq
	 *            - request to hold resource id
	 * @return resourceRes - response with resource information
	 */
	@Override
	public ResourceRes getResourceInfoById(ResourceReq resourceReq) {
		ResourceRes resp = new ResourceRes();
		ResourceDto resourceDto = capsResourceDao.getResourceById(resourceReq.getIdResource());
		resp.setResourceDto(resourceDto);
		return resp;
	}
}
