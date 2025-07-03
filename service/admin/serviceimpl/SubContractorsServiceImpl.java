package us.tx.state.dfps.service.admin.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dao.FacilityLocDao;
import us.tx.state.dfps.service.admin.dao.ResourceAddressRsrcLinkDao;
import us.tx.state.dfps.service.admin.dto.FacilityLocInDto;
import us.tx.state.dfps.service.admin.dto.FacilityLocOutDto;
import us.tx.state.dfps.service.admin.dto.ResourceAddressRsrcLinkInDto;
import us.tx.state.dfps.service.admin.dto.ResourceAddressRsrcLinkOutDto;
import us.tx.state.dfps.service.admin.dto.SubContractorsDto;
import us.tx.state.dfps.service.admin.dto.SubContractorsReq;
import us.tx.state.dfps.service.admin.service.SubContractorsService;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Retrieves a
 * list of subcontractors of a resource Feb 9, 2018- 5:39:41 PM © 2017 Texas
 * Department of Family and Protective Services
 */

@Service
@Transactional
public class SubContractorsServiceImpl implements SubContractorsService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	ResourceAddressRsrcLinkDao resourceAddressRsrcLinkDao;

	@Autowired
	FacilityLocDao facilityLocDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.admin.service.SubContractorsService#
	 * getSubContractorsDtoList(us.tx.state.dfps.service.admin.dto.
	 * SubContractorsReq)
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<SubContractorsDto> getSubContractorsDtoList(SubContractorsReq subContractorsReq) {

		ResourceAddressRsrcLinkInDto resourceAddressRsrcLinkInDto = new ResourceAddressRsrcLinkInDto();
		List<SubContractorsDto> subContractorsDtoList = new ArrayList<SubContractorsDto>();
		if (!ObjectUtils.isEmpty(subContractorsReq.getIdResource())) {
			resourceAddressRsrcLinkInDto.setIdResource(subContractorsReq.getIdResource());
		}
		if (!ObjectUtils.isEmpty(subContractorsReq.getCdRsrcLinkType())) {
			resourceAddressRsrcLinkInDto.setCdRsrcLinkType(subContractorsReq.getCdRsrcLinkType());
		}
		List<ResourceAddressRsrcLinkOutDto> resources = resourceAddressRsrcLinkDao
				.searchSubContractor(resourceAddressRsrcLinkInDto);
		if (!ObjectUtils.isEmpty(resources)) {
			resources.forEach(resourceAddressRsrcLinkOutDto -> {
				SubContractorsDto subContractorsDto = new SubContractorsDto();
				subContractorsDto.setNmResource(resourceAddressRsrcLinkOutDto.getNmResource());
				subContractorsDto.setIdResource(resourceAddressRsrcLinkOutDto.getIdResource());
				subContractorsDto.setCdRsrcSvcCnty(resourceAddressRsrcLinkOutDto.getCdRsrcSvcCnty());
				subContractorsDto.setCdRsrcFacilType(resourceAddressRsrcLinkOutDto.getCdRsrcFacilType());
				if (!ObjectUtils.isEmpty(resourceAddressRsrcLinkOutDto.getNbrRsrcFacilAcclaim())) {
					subContractorsDto.setNbrRsrcFacilAcclaim(resourceAddressRsrcLinkOutDto.getNbrRsrcFacilAcclaim());
				}
				subContractorsDtoList.add(subContractorsDto);
			});
		}
		if (subContractorsReq.getCdRsrcLinkType().equalsIgnoreCase(ServiceConstants.AGENCY_LINK_TYPE)) {
			if (!ObjectUtils.isEmpty(subContractorsDtoList)) {
				subContractorsDtoList.forEach(subContractorsDto -> {

					FacilityLocInDto facilityLocInDto = new FacilityLocInDto();
					if (!ObjectUtils.isEmpty(subContractorsDto.getIdResource())) {
						facilityLocInDto.setIdResource(subContractorsDto.getIdResource());
					}
					List<FacilityLocOutDto> facilityLocDetails = facilityLocDao.getFacilityCare(facilityLocInDto);
					if (!ObjectUtils.isEmpty(facilityLocDetails)) {
						FacilityLocOutDto facilityLocOutDto = (FacilityLocOutDto) facilityLocDetails.get(0);
						if (!ObjectUtils.isEmpty(facilityLocOutDto.getNbrFlocLevelsOfCare())) {
							subContractorsDto.setNbrFlocLevelsOfCare(facilityLocOutDto.getNbrFlocLevelsOfCare());
						}
						subContractorsDto.setDtFlocEffect(facilityLocOutDto.getDtFlocEffect());
						subContractorsDto.setCdFlocStatus1(facilityLocOutDto.getCdFlocStatus1());
						subContractorsDto.setCdFlocStatus2(facilityLocOutDto.getCdFlocStatus2());
						subContractorsDto.setCdFlocStatus3(facilityLocOutDto.getCdFlocStatus3());
						subContractorsDto.setCdFlocStatus4(facilityLocOutDto.getCdFlocStatus4());
						subContractorsDto.setCdFlocStatus5(facilityLocOutDto.getCdFlocStatus5());
						subContractorsDto.setCdFlocStatus6(facilityLocOutDto.getCdFlocStatus6());
						subContractorsDto.setCdFlocStatus7(facilityLocOutDto.getCdFlocStatus7());
						subContractorsDto.setCdFlocStatus8(facilityLocOutDto.getCdFlocStatus8());
						subContractorsDto.setCdFlocStatus9(facilityLocOutDto.getCdFlocStatus9());
						subContractorsDto.setCdFlocStatus10(facilityLocOutDto.getCdFlocStatus10());
						subContractorsDto.setCdFlocStatus11(facilityLocOutDto.getCdFlocStatus11());
						subContractorsDto.setCdFlocStatus12(facilityLocOutDto.getCdFlocStatus12());
						subContractorsDto.setCdFlocStatus13(facilityLocOutDto.getCdFlocStatus13());
						subContractorsDto.setCdFlocStatus14(facilityLocOutDto.getCdFlocStatus14());
						subContractorsDto.setCdFlocStatus15(facilityLocOutDto.getCdFlocStatus15());
					}

				});
			}
		}
		return subContractorsDtoList;
	}
}
