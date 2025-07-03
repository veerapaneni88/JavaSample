package us.tx.state.dfps.service.admin.serviceimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.service.admin.dao.*;
import us.tx.state.dfps.service.admin.dto.CapResourceInDto;
import us.tx.state.dfps.service.admin.dto.CapResourceOutDto;
import us.tx.state.dfps.service.admin.dto.FacilityDetailDto;
import us.tx.state.dfps.service.admin.dto.FacilityLocDto;
import us.tx.state.dfps.service.admin.dto.FacilityLocOrderedInDto;
import us.tx.state.dfps.service.admin.dto.FacilityLocOrderedOutDto;
import us.tx.state.dfps.service.admin.dto.FacilityServicePackageDto;
import us.tx.state.dfps.service.admin.dto.FacilityServiceTypeAudDto;
import us.tx.state.dfps.service.admin.dto.FacilityServiceTypeDto;
import us.tx.state.dfps.service.admin.dto.SpecSvcsInDto;
import us.tx.state.dfps.service.admin.dto.SpecSvcsOutDto;
import us.tx.state.dfps.service.admin.dto.SpecialServiceDto;
import us.tx.state.dfps.service.admin.service.FacilityDetailService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.FacilityDetailReq;
import us.tx.state.dfps.service.common.request.FacilityDetailSaveReq;
import us.tx.state.dfps.service.common.response.FacilityDetailRes;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.facilityservicetype.dto.FacilityServiceTypeInDto;
import us.tx.state.dfps.service.facilityservicetype.dto.FacilityServiceTypeOutDto;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;

import static us.tx.state.dfps.service.common.CodesConstant.CFACTYP2_26;
import static us.tx.state.dfps.service.common.CodesConstant.CFACTYP2_31;
import static us.tx.state.dfps.service.common.CodesConstant.CFACTYP2_33;
import static us.tx.state.dfps.service.common.CodesConstant.CFACTYP2_34;
import static us.tx.state.dfps.service.common.CodesConstant.CFACTYP2_35;
import static us.tx.state.dfps.service.common.CodesConstant.CFACTYP2_36;
import static us.tx.state.dfps.service.common.CodesConstant.CFACTYP2_60;
import static us.tx.state.dfps.service.common.CodesConstant.CFACTYP2_63;
import static us.tx.state.dfps.service.common.CodesConstant.CFACTYP2_64;
import static us.tx.state.dfps.service.common.CodesConstant.CFACTYP2_67;
import static us.tx.state.dfps.service.common.CodesConstant.CFACTYP2_68;
import static us.tx.state.dfps.service.common.CodesConstant.CFACTYP2_72;
import static us.tx.state.dfps.service.common.CodesConstant.CFACTYP2_80;
import static us.tx.state.dfps.service.common.CodesConstant.CFACTYP2_87;
import static us.tx.state.dfps.service.common.CodesConstant.CFACTYP2_QP;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Feb 2, 2018- 2:30:49 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class FacilityDetailServiceImpl implements FacilityDetailService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	SpecSvcsDao specSvcsDao;

	@Autowired
	FacilityLocOrderedDao facilityLocOrderedDao;

	@Autowired
	FacilityServiceTypeDao facilityServiceTypeDao;

	@Autowired
	CapsResourceDao capsResourceDao;

	@Autowired
	FacilityLocDao facilityLocDao;

	@Autowired
	FacilityServicePackageDao facilityServicePackageDao;

	public static final String FAC_SVC_TYPE_EMOT = "ED";
	public static final String PLCMT_LIV_ARR_64 = "64";
	public static final String RSRC_FACIL_TYP_RTC = "64";
	public static final String RSRC_TYP_RTC_EXIST = "Y";
	public static final String RSRC_TYP_RTC_NOT_EXIST = "N";

	protected static final List<String> facilityTypeCodesList = Arrays.asList(CFACTYP2_87, CFACTYP2_34, CFACTYP2_35, CFACTYP2_33, CFACTYP2_36,
																CFACTYP2_31, CFACTYP2_72, CFACTYP2_60, CFACTYP2_68, CFACTYP2_67, CFACTYP2_80,
																CFACTYP2_63, CFACTYP2_QP, CFACTYP2_64, CFACTYP2_26);

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.admin.service.FacilityDetailService#
	 * getFacilityDetails(us.tx.state.dfps.service.common.request.
	 * FacilityDetailReq)
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public List<FacilityDetailDto> getFacilityDetails(FacilityDetailReq facilityDetailReq) {
		List<FacilityDetailDto> facilityDetailDtoList = new ArrayList<>();
		FacilityDetailDto facilityDetailDto = new FacilityDetailDto();
		CapResourceInDto capResourceInDto = new CapResourceInDto();
		List<CapResourceOutDto> capResourceOutDtoList = new ArrayList<>();
		capResourceInDto.setIdResourceService(facilityDetailReq.getIdResource());
		capResourceOutDtoList = capsResourceDao.getResourceType(capResourceInDto);
		facilityDetailDtoList = getFacilityDetailDtoList(facilityDetailDtoList, facilityDetailDto,
				capResourceOutDtoList);
		try {
			FacilityLocOrderedInDto facilityLocOrderedInDto = new FacilityLocOrderedInDto();
			List<FacilityLocOrderedOutDto> facilityLocOrderedOutDtoList = new ArrayList<>();
			facilityLocOrderedInDto.setIdResource(facilityDetailReq.getIdResource());
			facilityLocOrderedOutDtoList = facilityLocOrderedDao
					.getFacilityLocOrderedOutDtoList(facilityLocOrderedInDto);
			facilityDetailDto = setFacilityLocList(facilityDetailDto, facilityLocOrderedOutDtoList);

			SpecSvcsInDto specSvcsInDto = new SpecSvcsInDto();
			List<SpecSvcsOutDto> specSvcsOutDtoList = new ArrayList<>();
			specSvcsInDto.setIdSpecSvcRsrc(facilityDetailReq.getIdResource());
			specSvcsOutDtoList = specSvcsDao.getSpecSvcsOutDtoList(specSvcsInDto);
			facilityDetailDto = setSpecialServiceList(facilityDetailDto, specSvcsOutDtoList);

			FacilityServiceTypeInDto facilityServiceTypeInDto = new FacilityServiceTypeInDto();
			List<FacilityServiceTypeOutDto> facilityServiceTypeOutDtoList = new ArrayList<>();
			facilityServiceTypeInDto.setIdResource(facilityDetailReq.getIdResource());
			facilityServiceTypeOutDtoList = facilityServiceTypeDao
					.getFacilityServiceTypeOutDtoList(facilityServiceTypeInDto);
			facilityDetailDto = setFacilityServiceTypeList(facilityDetailDto, facilityServiceTypeOutDtoList);

			if(checkFacilitySvcPkgDisplay(capResourceOutDtoList)){
				List<FacilityServicePackageDto> facilitySvcPkgDtoList = facilityServicePackageDao
						.getFacilityServicePackageDtoList(facilityDetailReq.getIdResource());
				facilityDetailDto = setFacilityServicePkgList(facilityDetailDto, facilitySvcPkgDtoList);

			}

		} catch (DataNotFoundException e) {
			ServiceLayerException serviceLayerException = new ServiceLayerException(
					messageSource.getMessage("resource.address.not.found", null, Locale.US));
			serviceLayerException.initCause(e);
			throw serviceLayerException;
		}
		return facilityDetailDtoList;
	}


	/**
	 * Method Name: getFacilityDetailDtoList Method Description: This method is
	 * used to getFacilityDetailDtoList
	 * 
	 * @param facilityDetailDtoList
	 * @param facilityDetailDto
	 * @param capsResourceOutDtoList
	 * @return List<FacilityDetailDto>
	 */
	public List<FacilityDetailDto> getFacilityDetailDtoList(List<FacilityDetailDto> facilityDetailDtoList,
			FacilityDetailDto facilityDetailDto, List<CapResourceOutDto> capsResourceOutDtoList) {
		if (!ObjectUtils.isEmpty(capsResourceOutDtoList)) {

			capsResourceOutDtoList.forEach(capsResource -> {
				facilityDetailDto.setCdRsrcCertBy(capsResource.getCdRsrcCertBy());
				facilityDetailDto.setCdRsrcOperBy(capsResource.getCdRsrcOperBy());
				facilityDetailDto.setCdRsrcSetting(capsResource.getCdRsrcSetting());
				facilityDetailDto.setCdRsrcPayment(capsResource.getCdRsrcPayment());
				facilityDetailDto.setDtRsrcCert(capsResource.getDtRsrcCert());
				facilityDetailDto.setDtRsrcClose(capsResource.getDtRsrcClose());
				facilityDetailDto.setTsLastUpdate(capsResource.getTsLastUpdate());
				facilityDetailDto.setNbrRsrcFacilCapacity(ObjectUtils.isEmpty(capsResource.getNbrRsrcFacilCapacity())
						? 0 : capsResource.getNbrRsrcFacilCapacity());
				facilityDetailDtoList.add(facilityDetailDto);
			});
		}
		return facilityDetailDtoList;
	}

	/**
	 * Method Name: setSpecialServiceList Method Description: This method is
	 * used to setSpecialServiceList in FacilityDetailDto
	 * 
	 * @param facilityDetailDto
	 * @param specSvcsOutDtoList
	 * @return FacilityDetailDto
	 */
	public FacilityDetailDto setSpecialServiceList(FacilityDetailDto facilityDetailDto,
			List<SpecSvcsOutDto> specSvcsOutDtoList) {
		if (specSvcsOutDtoList != null) {
			List<SpecialServiceDto> specialServiceDtoList = new ArrayList<SpecialServiceDto>();
			specSvcsOutDtoList.forEach(specSvcsOutDto -> {
				SpecialServiceDto specialServiceDto = new SpecialServiceDto();
				specialServiceDto.setIdSpecSvc(specSvcsOutDto.getIdSpecSvc());
				specialServiceDto.setIdSpecSvcRsrc(specSvcsOutDto.getIdSpecSvc());
				specialServiceDto.setCdSpecSvcs(specSvcsOutDto.getCdSpecSvcs());
				specialServiceDto.setTsLastUpdate(specSvcsOutDto.getTsLastUpdate());
				specialServiceDtoList.add(specialServiceDto);
			});
			facilityDetailDto.setSpecialServiceList(specialServiceDtoList);
		}
		return facilityDetailDto;
	}

	/**
	 * Method Name: setFacilityLocList Method Description: This method is used
	 * to setFacilityLocList in FacilityDetailDto
	 * 
	 * @param facilityDetailDto
	 * @param facilityLocOrderedOutDtoList
	 * @return FacilityDetailDto
	 */
	public FacilityDetailDto setFacilityLocList(FacilityDetailDto facilityDetailDto,
			List<FacilityLocOrderedOutDto> facilityLocOrderedOutDtoList) {
		if (!ObjectUtils.isEmpty(facilityLocOrderedOutDtoList)) {
			List<FacilityLocDto> facilityLocDtoList = new ArrayList<FacilityLocDto>();
			facilityLocOrderedOutDtoList.forEach(facilityLocOrderedOutDto -> {
				FacilityLocDto facilityLocDto = new FacilityLocDto();
				facilityLocDto.setIdFloc(facilityLocOrderedOutDto.getIdFloc());
				facilityLocDto.setDtFlocEffect(facilityLocOrderedOutDto.getDtFlocEffect());
				facilityLocDto.setDtFlocEnd(facilityLocOrderedOutDto.getDtFlocEnd());
				facilityLocDto.setTsLastUpdate(facilityLocOrderedOutDto.getTsLastUpdate());
				facilityLocDto
						.setNbrFlocLevelsOfCare(!StringUtils.isEmpty(facilityLocOrderedOutDto.getNbrFlocLevelsOfCare())
								? facilityLocOrderedOutDto.getNbrFlocLevelsOfCare().longValue() : null);
				facilityLocDto.setCdFlocStatus1(facilityLocOrderedOutDto.getCdFlocStatus1());
				facilityLocDto.setCdFlocStatus2(facilityLocOrderedOutDto.getCdFlocStatus2());
				facilityLocDto.setCdFlocStatus3(facilityLocOrderedOutDto.getCdFlocStatus3());
				facilityLocDto.setCdFlocStatus4(facilityLocOrderedOutDto.getCdFlocStatus4());
				facilityLocDto.setCdFlocStatus5(facilityLocOrderedOutDto.getCdFlocStatus5());
				facilityLocDto.setCdFlocStatus6(facilityLocOrderedOutDto.getCdFlocStatus6());
				facilityLocDto.setCdFlocStatus7(facilityLocOrderedOutDto.getCdFlocStatus7());
				facilityLocDto.setCdFlocStatus8(facilityLocOrderedOutDto.getCdFlocStatus8());
				facilityLocDto.setCdFlocStatus9(facilityLocOrderedOutDto.getCdFlocStatus9());
				facilityLocDto.setCdFlocStatus10(facilityLocOrderedOutDto.getCdFlocStatus10());
				facilityLocDto.setCdFlocStatus11(facilityLocOrderedOutDto.getCdFlocStatus11());
				facilityLocDto.setCdFlocStatus12(facilityLocOrderedOutDto.getCdFlocStatus12());
				facilityLocDto.setCdFlocStatus13(facilityLocOrderedOutDto.getCdFlocStatus13());
				facilityLocDto.setCdFlocStatus14(facilityLocOrderedOutDto.getCdFlocStatus14());
				facilityLocDto.setCdFlocStatus15(facilityLocOrderedOutDto.getCdFlocStatus15());
				facilityLocDtoList.add(facilityLocDto);
			});

			facilityDetailDto.setFacilityLocList(facilityLocDtoList);
		}
		return facilityDetailDto;
	}

	/**
	 * Method Name: setFacilityServiceTypeList Method Description: This method
	 * is used to setFacilityServiceTypeList in FacilityDetailDto
	 * 
	 * @param facilityDetailDto
	 * @param facilityServiceTypeOutDtoList
	 * @return FacilityDetailDto
	 */
	public FacilityDetailDto setFacilityServiceTypeList(FacilityDetailDto facilityDetailDto,
			List<FacilityServiceTypeOutDto> facilityServiceTypeOutDtoList) {
		if (!ObjectUtils.isEmpty(facilityServiceTypeOutDtoList)) {
			List<FacilityServiceTypeDto> facilityServiceTypeDtoList = new ArrayList<FacilityServiceTypeDto>();
			facilityServiceTypeOutDtoList.forEach(facilityServiceTypeOutDto -> {
				FacilityServiceTypeDto facilityServiceTypeDto = new FacilityServiceTypeDto();
				facilityServiceTypeDto.setIdFacilSvcType(facilityServiceTypeOutDto.getIdFacilSvcType());
				facilityServiceTypeDto.setDtEffective(facilityServiceTypeOutDto.getDtEffective());
				facilityServiceTypeDto.setDtEnd(facilityServiceTypeOutDto.getDtEnd());
				facilityServiceTypeDto.setCdFacilSvcType(facilityServiceTypeOutDto.getCdFacilSvcType());
				facilityServiceTypeDtoList.add(facilityServiceTypeDto);
			});
			facilityDetailDto.setFacilityServiceTypeList(facilityServiceTypeDtoList);
		}
		return facilityDetailDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.admin.service.FacilityDetailService#
	 * saveFacilityDetails(us.tx.state.dfps.service.common.request.
	 * FacilityDetailSaveReq)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FacilityDetailRes saveFacilityDetails(FacilityDetailSaveReq facilityDetailSaveReq) {
		FacilityDetailRes facilityDetailRes = new FacilityDetailRes();

		String indRsrcTypRTC = ServiceConstants.EMPTY_STRING;
		Integer facSvcRTCCount = 0;
		Integer facSvcNotRTCCount = 0;

		String indMaxDate = null;

		facilityDetailRes = capsResourceDao.updateFacilityDetailsInResource(facilityDetailSaveReq);

		if (!facilityDetailRes.getHasError()) {

			if (!ObjectUtils.isEmpty(facilityDetailSaveReq.getSpecSvcList()))
				facilityDetailRes = specSvcsDao.specSvcsAud(facilityDetailSaveReq, facilityDetailRes);

			if (!ObjectUtils.isEmpty(facilityDetailSaveReq.getFacilityLocList()))
				facilityLocDao.updateFacilityLoc(facilityDetailSaveReq, facilityDetailRes);

			// PPM 91056 - Removed the below code because the facility details will be updated by CLASS.
		}
		return facilityDetailRes;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public Integer getOpenHTFacilityServiceTypeCount(Long idResource) {
		Integer svcTypeCount = 0;
		FacilityServiceTypeInDto facilityServiceTypeInDto = new FacilityServiceTypeInDto();
		facilityServiceTypeInDto.setIdResource(idResource);
		facilityServiceTypeInDto.setCdFacilSvcType("HT");
		facilityServiceTypeInDto.setIndMaxEndDate("Y");
		svcTypeCount = facilityServiceTypeDao.getEdFacilityServiceTypeCount(facilityServiceTypeInDto);
		return svcTypeCount;
	}

	private FacilityDetailDto setFacilityServicePkgList(FacilityDetailDto facilityDetailDto, List<FacilityServicePackageDto> facilitySvcPkgDtoList) {
		if (!ObjectUtils.isEmpty(facilitySvcPkgDtoList)) {
			List<FacilityServicePackageDto> facilityServicePackageDtoList = new ArrayList<>();
			facilitySvcPkgDtoList.forEach(facilitySvcPkgDto -> {
				FacilityServicePackageDto facilityServicePkgDto = new FacilityServicePackageDto();
				facilityServicePkgDto.setActiveFcltySvcCodes(facilitySvcPkgDto.getActiveFcltySvcCodes());
				facilityServicePkgDto.setHoldFcltySvcCodes(facilitySvcPkgDto.getHoldFcltySvcCodes());
				facilityServicePkgDto.setDtEffective(facilitySvcPkgDto.getDtEffective());
				facilityServicePkgDto.setDtEnd(facilitySvcPkgDto.getDtEnd());
				facilityServicePkgDto.setIdFcltySvcPkg(facilitySvcPkgDto.getIdFcltySvcPkg());
				facilityServicePackageDtoList.add(facilityServicePkgDto);
			});
			facilityDetailDto.setFacilityServicePackageDtoList(facilityServicePackageDtoList);
		}
		return facilityDetailDto;

	}

	private boolean checkFacilitySvcPkgDisplay(List<CapResourceOutDto> capResourceOutDtoList) {
		if(capResourceOutDtoList!=null & !capResourceOutDtoList.isEmpty()){
			String facilityType = capResourceOutDtoList.get(0).getCdRsrcFacilType();
			if(facilityTypeCodesList.contains(facilityType)){
				return  true;
			}
		}
		return false;
	}

}
