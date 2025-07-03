package us.tx.state.dfps.service.disasterplan.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.common.dto.PersonAddressDto;
import us.tx.state.dfps.common.dto.WorkerDetailDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.DisasterPlanReq;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.disasterplan.dto.DisasterPlanDto;
import us.tx.state.dfps.service.disasterplan.service.DisasterPlanService;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.DisasterPlanFormPrefillData;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.subcare.dto.ResourceAddressDto;
import us.tx.state.dfps.service.subcare.dto.ResourcePhoneDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:DisasterPlanServiceImpl will implemented all operation defined in
 * DisasterPlanService Interface related DisasterPlan module. Feb 9, 2018-
 * 2:01:28 PM © 2017 Texas Department of Family and Protective Services
 */

@Service
@Transactional
public class DisasterPlanServiceImpl implements DisasterPlanService {

	@Autowired
	DisasterPlanDao disasterPlanDao;

	@Autowired
	CapsResourceDao capsResourceDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	DisasterPlanFormPrefillData disasterPlanFormPrefillData;

	/**
	 * Service Name: CFAD19S Description: DISASTER PLAN FOR DFPS FOSTER/ADOPTIVE
	 * & KINSHIP HOMES
	 * 
	 * @param disasterPlanReq
	 * @return disasterPlanDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public PreFillDataServiceDto getDisasterPlan(DisasterPlanReq disasterPlanReq) {

		String cdPersAddrLinkType = ServiceConstants.EVAC_ADDRESS_PERS;
		Long idCase = ServiceConstants.ZERO_VAL;

		// validate request input
		DisasterPlanDto disasterPlanDto = new DisasterPlanDto();
		Long idJobPersSupv = ServiceConstants.ZERO_VAL;
		Long idPrimaryWorker = ServiceConstants.ZERO_VAL;
		/* get case-stage info from stage id */
		GenericCaseInfoDto genericCaseInfoDto = new GenericCaseInfoDto();
		genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(disasterPlanReq.getIdStage());

		disasterPlanDto.setGenericCaseInfoDto(genericCaseInfoDto);
		idCase = genericCaseInfoDto.getIdCase();

		/* retrieve worker */
		idPrimaryWorker = disasterPlanDao.getPrimaryWorkerOrSupervisor(idCase);

		/* retrieves worker info */
		WorkerDetailDto workerDetailDto = new WorkerDetailDto();

		if (!ObjectUtils.isEmpty(idPrimaryWorker)) {
			workerDetailDto = disasterPlanDao.getWorkerInfoById(idPrimaryWorker);
		}
		if (ServiceConstants.BUSINESS_PHONE.equals(workerDetailDto.getCdPersonPhoneType())
				|| ServiceConstants.BUSINESS_CELL.equals(workerDetailDto.getCdPersonPhoneType())) {
			workerDetailDto.setNbrPersonPhone(workerDetailDto.getNbrMailCodePhone());
			workerDetailDto.setNbrPersonPhoneExtension(workerDetailDto.getNbrMailCodePhoneExt());
		}
		if (ServiceConstants.ZERO_VAL < disasterPlanReq.getIdResource()) {
			workerDetailDto.setIdName(disasterPlanReq.getIdResource());
		} else {
			workerDetailDto.setIdName(ServiceConstants.ZERO_VAL);
		}
		disasterPlanDto.setWorkerDetailDto(workerDetailDto);

		idJobPersSupv = disasterPlanDto.getWorkerDetailDto().getIdJobPersSupv();

		/* retrieves worker (Supv) info */
		WorkerDetailDto supvDetailDto = new WorkerDetailDto();

		if(!ObjectUtils.isEmpty(idJobPersSupv)) {
			supvDetailDto = disasterPlanDao.getWorkerInfoById(idJobPersSupv);
		}
		if (ServiceConstants.BUSINESS_PHONE.equals(supvDetailDto.getCdPersonPhoneType())
				|| ServiceConstants.BUSINESS_CELL.equals(supvDetailDto.getCdPersonPhoneType())) {
			supvDetailDto.setNbrPersonPhone(supvDetailDto.getNbrMailCodePhone());
			supvDetailDto.setNbrPersonPhoneExtension(supvDetailDto.getNbrMailCodePhoneExt());
		}
		if (ServiceConstants.ZERO_VAL < disasterPlanReq.getIdResource()) {
			supvDetailDto.setIdName(disasterPlanReq.getIdResource());
		}
		disasterPlanDto.setWorkerSupvDetailDto(supvDetailDto);

		if (ServiceConstants.ZERO_VAL < disasterPlanReq.getIdResource()) {
			/* retrieve primary address for rsrc */
			// ResourceAddressDto resourceAddressDto = new ResourceAddressDto();
			ResourceAddressDto resourceAddressDto = disasterPlanDao.getResourceAddress(disasterPlanReq.getIdResource());
			if (null != resourceAddressDto) {
				disasterPlanDto.setResourceAddress(resourceAddressDto);
			} else {
				resourceAddressDto = new ResourceAddressDto();
				resourceAddressDto.setIdResource(ServiceConstants.ZERO_VAL);
				disasterPlanDto.setResourceAddress(resourceAddressDto);
			}

			if (null != disasterPlanDto.getResourceAddress().getCdRsrcAddrSchDist()) {
				//artf212958 : passing county code to get school dist code
				disasterPlanDto.getResourceAddress().setTxtRsrcAddrComments(
						capsResourceDao.getSchDistName(disasterPlanDto.getResourceAddress().getCdRsrcAddrSchDist(),
								disasterPlanDto.getResourceAddress().getCdRsrcAddrCounty()));

			} else {
				disasterPlanDto.getResourceAddress().setTxtRsrcAddrComments(ServiceConstants.NULL_STRING);
			}
		} else {
			/* retrieve evac addr for plcmt adult */
			// PersonAddressDto personAddressDto = new PersonAddressDto();
			PersonAddressDto personAddressDto = disasterPlanDao.getPersonAddress(disasterPlanReq.getIdPerson(),
					cdPersAddrLinkType, null);
			if (personAddressDto == null) {
				/*
				 ** Set group TMPLAT_PERS_EVAC_ADDRESS_INPUT test attribute to 0
				 ** to display the editable evacuation address field.
				 */
				personAddressDto = new PersonAddressDto();
				personAddressDto.setIdPerson(ServiceConstants.LONG_ZERO_VAL);
			}
			disasterPlanDto.setPersonAddressDto(personAddressDto);

		}

		List<ResourcePhoneDto> resourcePhoneList = capsResourceDao.getResourcePhone(disasterPlanReq.getIdResource());
		if (null != resourcePhoneList) {
			disasterPlanDto.setResourcePhoneList(resourcePhoneList);
		} else {
			resourcePhoneList = new ArrayList<ResourcePhoneDto>();
			ResourcePhoneDto resourcePhoneDto = new ResourcePhoneDto();
			resourcePhoneDto.setIdResource(ServiceConstants.LONG_ZERO_VAL);
			resourcePhoneList.add(resourcePhoneDto);
			disasterPlanDto.setResourcePhoneList(resourcePhoneList);
		}

		return disasterPlanFormPrefillData.returnPrefillData(disasterPlanDto);
	}

}
