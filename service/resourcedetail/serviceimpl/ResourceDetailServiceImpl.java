package us.tx.state.dfps.service.resourcedetail.serviceimpl;

import static us.tx.state.dfps.service.common.CodesConstant.CCNTYREG_999;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import us.tx.state.dfps.common.domain.ResourceEmail;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.PersonUtil;
import us.tx.state.dfps.service.resource.detail.dto.*;
import us.tx.state.dfps.service.resource.dto.SchoolDistrictDetailsDto;
import us.tx.state.dfps.service.resource.dto.SchoolDistrictDto;
import us.tx.state.dfps.service.resourcedetail.dao.*;
import us.tx.state.dfps.service.resourcedetail.service.ResourceDetailService;
import us.tx.state.dfps.service.resourcesearch.dao.ResourceSearchDao;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.subcare.dto.ResourceRtbExceptionDto;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Implementing
 * the ResourceDetailService interface Feb 5, 2018- 3:05:31 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class ResourceDetailServiceImpl implements ResourceDetailService {

	@Autowired
	private CapsRsrcDao capsRsrcDao;

	@Autowired
    private CapsResourceDao capsResourceDao;

	@Autowired
	private ResourceAddressDao resourceAddressDao;

	@Autowired
	private InvoicesIdVendorDao invoicesIdVendorDao;

	@Autowired
	private ResourceEmailInsUpdDelDao resourceEmailInsUpdDelDao;

	@Autowired
	private RsrcLinkDao rsrcLinkDao;

	@Autowired
	private RtrvParentRsrcIdDao rtrvParentRsrcIdDao;

	@Autowired
	private ResourceLanguageDao resourceLanguageDao;

	@Autowired
	private ResourcePhoneDao resourcePhoneDao;

	@Autowired
	private ResourceSearchDao resourceSearchDao;

	@Autowired
	private PersonUtil personUtil;

	public final static String OPERATION_RESOURCE_DETAILS = "ResourceDetails";
	public final static String OPERATION_ADDRESS = "Address";
	public final static String OPERATION_PHONE = "Phone";
	public final static String OPERATION_LANGUAGE = "Language";
	public final static String OPERATION_EMAIL = "Email";
	public final static String OPERATION_ADD_RESOURCE = "AddResource";

	private static final Logger log = Logger.getLogger(ResourceDetailServiceImpl.class);

	/**
	 * Description: This method is used for ResourceDetails insert, update and
	 * delete operations
	 * 
	 * @param resourceDetailInDto
	 * @return resourceDetailOutDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ResourceDetailOutDto saveResourceDetails(ResourceDetailInDto resourceDetailInDto) {
		log.debug("Entering method saveResourceDetails in ResourceDetailServiceImpl");
		ResourceDetailOutDto resourceDetailOutDto = new ResourceDetailOutDto();
		String operation = resourceDetailInDto.getRequestOperation();
		switch (operation) {
		case OPERATION_RESOURCE_DETAILS:
			capsRsrcDao.saveCapsResource(resourceDetailInDto);
			List<ResourceRtbExceptionDto> existingExceptions = resourceSearchDao.findResourceRtbExceptions(resourceDetailInDto.getIdResource());
			// create map of existing exceptions so we can do quick lookup to compare against the input exceptions.
			Map<Long, ResourceRtbExceptionDto> existingExceptionMap =
					existingExceptions.stream().collect(Collectors.toMap(ResourceRtbExceptionDto::getIdResourceRtbException,
							Function.identity()));

			if (resourceDetailInDto.getResourceRtbExceptionList() != null) {
				// find and only update changed comments.
				for (ResourceRtbExceptionDto currInputException : resourceDetailInDto.getResourceRtbExceptionList()) {
					ResourceRtbExceptionDto currExistingException = existingExceptionMap.get(currInputException.getIdResourceRtbException());
					if (currExistingException != null) {
						// consider "" and null as equivalent and do not update if that's the only change.
						if (currExistingException.getComments() == "") currExistingException.setComments(null);
						if (currInputException.getComments() == "") currInputException.setComments(null);
						if (!ObjectUtils.nullSafeEquals(currExistingException.getComments(), currInputException.getComments()) ||
								!ObjectUtils.nullSafeEquals(currExistingException.getEndedDate(), currInputException.getEndedDate())) {
							resourceSearchDao.updateResourceRtbException(currInputException);
						}
					}
				}
				// add new rows
				for (ResourceRtbExceptionDto currInputException : resourceDetailInDto.getResourceRtbExceptionList()) {
					if ("A".equals(currInputException.getModifyOperation()) || "AU".equals(currInputException.getModifyOperation())) {
						ResourceRtbExceptionDto newException = addResourceRtbException(currInputException);
						if (resourceDetailOutDto.getMapAddedRowNumToNewId() == null) {
							resourceDetailOutDto.setMapAddedRowNumToNewId(new HashMap<>());
						}
						// return the new ids
						resourceDetailOutDto.getMapAddedRowNumToNewId().put(currInputException.getRowNum().longValue(),
								newException.getIdResourceRtbException());
					}
				}
				// delete rows marked for deletion.
				for (ResourceRtbExceptionDto currInputException : resourceDetailInDto.getResourceRtbExceptionList()) {
					if ("D".equals(currInputException.getModifyOperation())) {
						ResourceRtbExceptionDto deletedException = deleteResourceRtbException(currInputException);
					}
				}
			}


			break;
		case OPERATION_ADDRESS:
			saveRsrcAddress(resourceDetailInDto);
			break;
		case OPERATION_PHONE:
			resourceDetailOutDto = resourcePhoneDao.saveResourcePhone(resourceDetailInDto);
			break;
		case OPERATION_EMAIL:
			resourceEmailHandler(resourceDetailInDto, resourceDetailOutDto);
			break;
		case OPERATION_LANGUAGE:
			Long returnId = resourceLanguageDao.saveResourceLanguage(resourceDetailInDto);
			if(Long.valueOf(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH).equals(returnId)){
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
				resourceDetailOutDto.setErrorDto(errorDto);
			}
			break;
		case OPERATION_ADD_RESOURCE:
			// If the Call is for Adding the Resource Save the Resource,
			// Address, Phone and Language Sequentially.
			Long idResource = capsRsrcDao.saveCapsResource(resourceDetailInDto);
			resourceDetailInDto.setIdResource(idResource);
			resourceDetailInDto.setCdScrDataAction(resourceDetailInDto.getReqFuncCd());
			saveRsrcAddress(resourceDetailInDto);
			resourcePhoneDao.saveResourcePhone(resourceDetailInDto);
			resourceLanguageDao.saveResourceLanguage(resourceDetailInDto);
			// Check if the Email Details are entered, If Yes, Save the Email
			// Details.
			if (!StringUtils.isEmpty(resourceDetailInDto.getRsrcEmailAddress())) {
				// If the Email is entered in Add Resource Page, set it as
				// Primary Email.
				resourceDetailInDto.setIndRsrcEmailPrimary(ServiceConstants.Y);
				resourceEmailHandler(resourceDetailInDto, resourceDetailOutDto);
			}
			break;
		default:
			break;
		}
		resourceDetailOutDto.setIdRsrc(resourceDetailInDto.getIdResource());
		updateCapsRsrcLink(resourceDetailInDto);
		log.debug("Exiting method saveResourceDetails in ResourceDetailServiceImpl");
		return resourceDetailOutDto;
	}

	/**
	 * Description:Updates address.
	 * 
	 * @param resourceDetailInDto
	 * @param resourceDetailOutDto
	 * @return @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void saveRsrcAddress(ResourceDetailInDto resourceDetailInDto) {
		log.debug("Entering method saveRsrcAddress in ResourceDetailServiceImpl");
		Long result = resourceAddressDao.saveResourceAddress(resourceDetailInDto);
		if (result > 0) {
			invoicesIdVendorDao.updateInvoice(resourceDetailInDto);
		}
		log.debug("Exiting method saveRsrcAddress in ResourceDetailServiceImpl");
	}

	/**
	 * Method Name: resourceEmailHandler Method Description: Handle Email detail
	 * (resource) Add, Update and Delete operation
	 * 
	 * @param resourceDetailInDto
	 * @param resourceDetailOutDto
	 * @return ResourceDetailOutDto @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ResourceDetailOutDto resourceEmailHandler(ResourceDetailInDto resourceDetailInDto,
			ResourceDetailOutDto resourceDetailOutDto) {
		log.debug("Entering method resourceEmailHandler in ResourceDetailServiceImpl");
		ResourceEmail resourceEmail = new ResourceEmail();
		resourceEmail.setIdResource(resourceDetailInDto.getIdResource());
		// Set the Id Rsrc Email if it Action type is Update and Incoming detail
		// has it.
		if (!StringUtils.isEmpty(resourceDetailInDto.getIdRsrcEmail())) {
			resourceEmail.setIdResourceEmail(resourceDetailInDto.getIdRsrcEmail());
		}
		resourceEmail.setCdEmailType(resourceDetailInDto.getCdRsrcEmailType());
		resourceEmail.setTxtEmailAddress(resourceDetailInDto.getRsrcEmailAddress());
		resourceEmail.setTxtEmailComment(resourceDetailInDto.getRsrcEmailComments());
		resourceEmail.setIndPrimary(resourceDetailInDto.getIndRsrcEmailPrimary());
		resourceEmail.setIdEmpLastUpdate(resourceDetailInDto.getIdPersonUpdate());
		Date dtStart = null == resourceDetailInDto.getDtRsrcEmailStart() ? new Date()
				: resourceDetailInDto.getDtRsrcEmailStart();
		resourceEmail.setDtStart(dtStart);
		Date dtLastUpdate = null == resourceDetailInDto.getDtLastUpdate() ? new Date()
				: resourceDetailInDto.getDtLastUpdate();
		resourceEmail.setDtLastUpdate(dtLastUpdate);

		if (!TypeConvUtil.isNullOrEmpty(resourceDetailInDto.getDtRsrcEmailEnd())) {
			resourceEmail.setDtEnd(resourceDetailInDto.getDtRsrcEmailEnd());
		} else {
			resourceEmail.setDtEnd(ServiceConstants.GENERIC_END_DATE);
		}
		String actionType = resourceDetailInDto.getReqFuncCd();
		Long idResource = resourceEmailInsUpdDelDao.handleResourceEmail(actionType, resourceEmail);
		resourceDetailOutDto.setIdRsrc(idResource);
		log.debug("Exiting method resourceEmailHandler in ResourceDetailServiceImpl");
		return resourceDetailOutDto;
	}

	/**
	 * 
	 * Method Name: updateCapsRsrcLink Method Description: This method will take
	 * care of setting up the parent linked record for child specific, SIL and
	 * special contracts only. The special thing about these relationships is
	 * that these are one to one rest of the of the relationship types are
	 * managed in different ways. (CPA/AH is imported from CLASS, CLASS AH/FAD
	 * HM is setup in FAD page, Con/SubCon is one to many and setup using
	 * different service)
	 * 
	 * @param resourceDetailInDto
	 */
	private void updateCapsRsrcLink(ResourceDetailInDto resourceDetailInDto) {
		if (ObjectUtils.isEmpty(resourceDetailInDto.getNbrRsrcFacilAcclaim())
				|| (!ObjectUtils.isEmpty(resourceDetailInDto.getNbrRsrcFacilAcclaim())
						&& ServiceConstants.ZERO.equals(resourceDetailInDto.getNbrRsrcFacilAcclaim()))) {
			String cReqFuncCd = null;
			List<RtrvCpaNameParentResIdOutDto> rtrvCpaNameParentResIdOutDtoList = null;
			String cdRsrcLinkType = null;
			if (ServiceConstants.STRING_IND_Y.equals(resourceDetailInDto.getIndRsrcChildSpecific())
					|| (ServiceConstants.STRING_IND_Y.equals(resourceDetailInDto.getIndChildSpecificSchedRate()))) {
				cdRsrcLinkType = ServiceConstants.CD_RSRC_LINK_CHILD_SPECIFIC;
			} else if (ServiceConstants.STRING_IND_Y.equals(resourceDetailInDto.getIndSpecialContract())) {
				cdRsrcLinkType = ServiceConstants.CD_RSRC_LINK_SPECIAL_CON;
			}
			if (!ObjectUtils.isEmpty(cdRsrcLinkType)) {
				rtrvCpaNameParentResIdOutDtoList = rtrvParentRsrcIdDao
						.getCapsRsrcLink(resourceDetailInDto.getIdResource(), cdRsrcLinkType);

				for (RtrvCpaNameParentResIdOutDto rtrvCpaNameParentResIdOutDto : rtrvCpaNameParentResIdOutDtoList) {
					RsrcLinkInsUpdDelInDto rsrcLinkInsUpdDelInDto = new RsrcLinkInsUpdDelInDto();
					/**
					 * if the parent record specified in input is not matching
					 * with one existing in database then update the parent
					 * record
					 */
					if (!StringUtils.isEmpty(rtrvCpaNameParentResIdOutDto.getIdRsrcLinkParent())
							&& !StringUtils.isEmpty(resourceDetailInDto.getIdRsrcLinkParent())
							&& (!rtrvCpaNameParentResIdOutDto.getIdRsrcLinkParent()
									.equals(resourceDetailInDto.getIdRsrcLinkParent()))) {
						cReqFuncCd = ServiceConstants.REQ_FUNC_CD_UPDATE;
						rsrcLinkInsUpdDelInDto.setIdRsrcLink(rtrvCpaNameParentResIdOutDto.getIdRsrcLink());
						/**
						 * if parent is specified in user input but link is not
						 * setup in database then create the link
						 */
					} else if (StringUtils.isEmpty(rtrvCpaNameParentResIdOutDto.getIdRsrcLinkParent())
							&& !StringUtils.isEmpty(resourceDetailInDto.getIdRsrcLinkParent())) {
						cReqFuncCd = ServiceConstants.REQ_FUNC_CD_ADD;
						/**
						 * if user cleared the parent in page and parent record
						 * is existing in database then delete the link
						 */
					} else if (!StringUtils.isEmpty(rtrvCpaNameParentResIdOutDto.getIdRsrcLinkParent())
							&& (StringUtils.isEmpty(resourceDetailInDto.getIdRsrcLinkParent())
									|| ServiceConstants.ZERO.equals(resourceDetailInDto.getIdRsrcLinkParent()))) {
						cReqFuncCd = ServiceConstants.REQ_FUNC_CD_DELETE;
						rsrcLinkInsUpdDelInDto.setIdRsrcLink(rtrvCpaNameParentResIdOutDto.getIdRsrcLink());
					}
					/**
					 * if record in rsrc_link need to be added, updated or
					 * deleted
					 */
					if (!ObjectUtils.isEmpty(cReqFuncCd)) {
						rsrcLinkInsUpdDelInDto.setReqFuncCd(cReqFuncCd);
						rsrcLinkInsUpdDelInDto.setIdRsrcLinkChild(resourceDetailInDto.getIdResource());
						rsrcLinkInsUpdDelInDto.setIdRsrcLinkParent(resourceDetailInDto.getIdRsrcLinkParent());
						rsrcLinkInsUpdDelInDto.setCdRsrcLinkService(ServiceConstants.CODE_02);
						rsrcLinkInsUpdDelInDto.setDtLastUpdate(resourceDetailInDto.getDtLastUpdate());
						if (ServiceConstants.STRING_IND_Y.equals(resourceDetailInDto.getIndRsrcChildSpecific())
								|| ServiceConstants.STRING_IND_Y
										.equals(resourceDetailInDto.getIndChildSpecificSchedRate())) {
							rsrcLinkInsUpdDelInDto.setCdRsrcLinkType(ServiceConstants.CD_RSRC_LINK_CHILD_SPECIFIC);
						} else if (ServiceConstants.STRING_IND_Y.equals(resourceDetailInDto.getIndSpecialContract())) {
							rsrcLinkInsUpdDelInDto.setCdRsrcLinkType(ServiceConstants.CD_RSRC_LINK_SPECIAL_CON);
						}
						/**
						 * Clear the parent record
						 */
						rsrcLinkDao.saveRsrcLink(rsrcLinkInsUpdDelInDto);
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.resourcedetail.service.ResourceDetailService#
	 * getSchooldistrict(us.tx.state.dfps.service.resource.detail.dto.
	 * SchoolDistrictReq)
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<SchoolDistrictDto> getSchooldistrict(SchoolDistrictReq schoolDistrictReq) {
		log.debug("Entering method getSchooldistrict in ResourceDetailServiceImpl");
		List<SchoolDistrictDto> response = new ArrayList<>();
		List<SchoolDistrictDetailsDto> resources = new ArrayList<>();
		if (!ObjectUtils.isEmpty(schoolDistrictReq.getScrAddrGenericAddrCnty()) &&
				!CCNTYREG_999.equals(schoolDistrictReq.getScrAddrGenericAddrCnty())) {
			resources.addAll(resourceAddressDao.getSchoolDistrict(schoolDistrictReq.getScrAddrGenericAddrCnty()));
		}
		if (!CollectionUtils.isEmpty(resources)) {
			for (SchoolDistrictDetailsDto schoolDistrictDetailsDto : resources) {
				SchoolDistrictDto schoolDistrictDto = new SchoolDistrictDto();
				schoolDistrictDto.setCdRsrcAddrSchDist(schoolDistrictDetailsDto.getCdSchDist());
				schoolDistrictDto.setTxtSchDistName(schoolDistrictDetailsDto.getTxtSchDistName());
				response.add(schoolDistrictDto);
			}
		}

		log.debug("Exiting method getSchooldistrict in ResourceDetailServiceImpl");
		return response;
	}

	private ResourceRtbExceptionDto addResourceRtbException(ResourceRtbExceptionDto newException) {
		Long newExceptionId = resourceSearchDao.addResourceRtbException(newException);

		// fetch newly created row so we get created date correct.
		List<ResourceRtbExceptionDto> exceptionList = resourceSearchDao.findResourceRtbExceptionById(newExceptionId);
		updateListWithNames(exceptionList);

		return exceptionList.get(0);
	}

	/**
	 * Note this is not an actual delete, but just setting the END_DATE.
	 * @param newException
	 * @return
	 */
	private ResourceRtbExceptionDto deleteResourceRtbException(ResourceRtbExceptionDto newException) {
		Long rowCountJunk = resourceSearchDao.updateResourceRtbException(newException);

		// fetch newly created row so we get created date correct.
		List<ResourceRtbExceptionDto> exceptionList = resourceSearchDao.findResourceRtbExceptionById(newException.getIdResourceRtbException());
		updateListWithNames(exceptionList);

		return exceptionList.get(0);
	}

	private ResourceRtbExceptionDto checkResourceRtbException(ResourceRtbExceptionDto resourceReq) {
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

    @Override
    public Long getResourceIdByStageId(Long stageId) {
        return capsResourceDao.getCapsResourceByStageId(stageId);
    }
}
