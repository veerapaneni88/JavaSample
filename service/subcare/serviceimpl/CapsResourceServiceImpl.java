package us.tx.state.dfps.service.subcare.serviceimpl;

//import com.sun.javafx.collections.MappingChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ResourceReq;
import us.tx.state.dfps.service.common.response.ResourceRes;
import us.tx.state.dfps.service.common.utils.PersonUtil;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.resource.dto.ResourceSearchResultDto;
import us.tx.state.dfps.service.resourcesearch.dao.ResourceSearchDao;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.subcare.dto.ResourceRtbExceptionDto;
import us.tx.state.dfps.service.subcare.service.CapsResourceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CapsResourceServiceImpl implements CapsResourceService {

	@Autowired
	CapsResourceDao capsResourceDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	private ResourceSearchDao resourceSearchDao;

	@Autowired
	private PersonUtil personUtil;

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public ResourceRes getResourceDtl(ResourceReq resourceReq) {
		ResourceRes result = new ResourceRes();
		ResourceDto resourceDto = capsResourceDao.getResourceDtl(resourceReq.getIdResource());
		if(null!=resourceReq && null != resourceReq.getRetriveRtbStatus() && ServiceConstants.YES.equalsIgnoreCase(resourceReq.getRetriveRtbStatus())){
			//RTB indicator update from query
			ResourceSearchResultDto resourceSearchValueDto = new ResourceSearchResultDto();
			resourceSearchValueDto.setAddResource(false);
			resourceSearchValueDto.setIdentificationNum(String.valueOf(resourceReq.getIdResource()));
			resourceSearchValueDto.setIdentificationType("RSC");
			List<ResourceSearchResultDto> resultList = resourceSearchDao.executeSearch(resourceSearchValueDto);
			if(!resultList.isEmpty()){
				ResourceSearchResultDto resourceSearchResultDto=resultList.get(0);
                if (!ObjectUtils.isEmpty(resourceSearchResultDto.getRtbStatus()) &&
                        resourceSearchResultDto.getRtbStatus().booleanValue()) {
                    resourceDto.setRtbStatus(ServiceConstants.YES);
                } else {
                    resourceDto.setRtbStatus(ServiceConstants.NO);

                }
			}
		}


		// artf187193 BR 1.2 Manual Override of RTB Indicator
		List<ResourceRtbExceptionDto> exceptionList = resourceSearchDao.findResourceRtbExceptions(resourceReq.getIdResource());
		resourceDto.setRtbExceptionList(exceptionList);

		// fill in names for person, createdBy, and updatedBy
		updateListWithNames(exceptionList);

		result.setResourceDto(resourceDto);
		return result;
	}

	public ResourceRtbExceptionDto checkResourceRtbException(ResourceRtbExceptionDto resourceReq) {
		List<ResourceRtbExceptionDto> exceptionList = resourceSearchDao.findResourceRtbExceptions(resourceReq.getIdResource());

		Long correctExceptionCount = 0l;
		if (exceptionList != null && exceptionList.size() > 0) {
			correctExceptionCount = exceptionList.stream().filter(currException -> currException.getEndedDate() == null && currException.getIdPerson().equals(resourceReq.getIdPerson())).count();
		}

		ResourceRtbExceptionDto retVal = new ResourceRtbExceptionDto();
		if (correctExceptionCount > 0l) {
			retVal.setIdPerson(resourceReq.getIdPerson());
			retVal.setIdResource(resourceReq.getIdResource());
		}
		return retVal;
	}

	private void updateListWithNames(List<ResourceRtbExceptionDto> exceptionList) {
		// Find the names for all IDs in result. Save the name each time we look it up, because it's likely to appear again.
		Map<Long, String> knownNames = new HashMap<>();
		exceptionList.stream().forEach(currException -> {
			if (currException.getIdPerson() != null) {
				String nameStr = knownNames.get(currException.getIdPerson());
				if (nameStr == null) {
					nameStr = personUtil.getPersonFullName(currException.getIdPerson());
					knownNames.put(currException.getIdPerson(), nameStr);
				}
				currException.setNmPerson(nameStr);
			}
			if (currException.getIdCreatedPerson() != null) {
				String nameStr = knownNames.get(currException.getIdCreatedPerson());
				if (nameStr == null) {
					nameStr = personUtil.getPersonFullName(currException.getIdCreatedPerson());
					knownNames.put(currException.getIdCreatedPerson(), nameStr);
				}
				currException.setNmCreatedPerson(nameStr);
			}
			if (currException.getIdUpdatedPerson() != null) {
				String nameStr = knownNames.get(currException.getIdUpdatedPerson());
				if (nameStr == null) {
					nameStr = personUtil.getPersonFullName(currException.getIdUpdatedPerson());
					knownNames.put(currException.getIdUpdatedPerson(), nameStr);
				}
				currException.setNmUpdatedPerson(nameStr);
			}
		});
	}
}
