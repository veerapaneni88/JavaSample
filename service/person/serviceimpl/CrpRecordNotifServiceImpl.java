package us.tx.state.dfps.service.person.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.exception.FormsException;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CrpRecordNotifReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.CrpRecordNotifAndDetailsDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.CrpRecordNotifPrefillData;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.AbcsRecordsCheckDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.CrpRecordNotifDao;
import us.tx.state.dfps.service.person.dto.CrpPersonNameDto;
import us.tx.state.dfps.service.person.dto.PublicCentralRegistryDto;
import us.tx.state.dfps.service.person.dto.CentralRegistryCheckDto;
import us.tx.state.dfps.service.person.service.CrpRecordNotifService;
import us.tx.state.dfps.service.recordscheck.dto.*;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * service-business- IMPACT PHASE 2 Class Description: service
 *  for Central Registry Portal, to populate CRP Record Notif Forms Jan 25, 2024-
 *  08:26:00 AM © 2024 Texas Department of Family and Protective Services
 *
 * ********Change History**********
 * 04/22/2024 thompswa Initial.
 * 05/22/2024 thompswa artf266920 ltrhnot status.
 * 08/02/2024 thompswa artf268135 matched requests added.
 */
@Service
@Transactional
public class CrpRecordNotifServiceImpl implements CrpRecordNotifService {

	private static final Logger logger = Logger.getLogger(CrpRecordNotifServiceImpl.class);

	@Autowired
	CrpRecordNotifDao crpRecordNotifDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	CrpRecordNotifPrefillData crpRecordNotifPrefillData;
	
	/**message source. */
	@Autowired
	MessageSource messageSource;
	@Autowired
	EmployeeDao employeeDao;
	@Autowired
	AbcsRecordsCheckDao abcsRecordsCheckDao;

	@Autowired
	LookupDao lookupDao;

	private static final String WHITESPACE = " ";

	@Override
	public CommonFormRes getCrpRecordNotif(CrpRecordNotifReq crpRecordNotifReq) {
		logger.info(
				"Entering into getCrpRecordNotif Method-TransactionID:" + crpRecordNotifReq.getTransactionId());

		CommonFormRes commonFormRes = new CommonFormRes();
		CrpRecordNotifAndDetailsDto crpRecordNotifAndDetailsDto = new CrpRecordNotifAndDetailsDto();
		ResourceContractInfoDto resourceContractInfoDto = new ResourceContractInfoDto();
		CentralRegistryCheckDto centralRegistryCheckDto = new CentralRegistryCheckDto();
		EmployeePersonDto empRequestorDto = new EmployeePersonDto();
		Long idRequest = 0L;
		List<Long> idRequestList = new ArrayList<>();
		String contentEditableStatus = ServiceConstants.FALSE; // artf266920
		// set all the dao results to crpDto for prefillData service.
		CrpRecordNotifFormsDto crpDto = new CrpRecordNotifFormsDto();

		crpDto.setDocName(lookupDao.decode(CodesConstant.CRPNOTTY, crpRecordNotifReq.getDocType()));

		crpRecordNotifAndDetailsDto = getCrpRecordNotifAndDetailsDto(crpRecordNotifReq);
		if (!ObjectUtils.isEmpty(crpRecordNotifAndDetailsDto) && 0 < crpRecordNotifAndDetailsDto.getIdRequest()) {
			idRequest = crpRecordNotifAndDetailsDto.getIdRequest();
			if (null == crpRecordNotifReq.getIdCrpRecordNotif()
					|| ! (0L < crpRecordNotifReq.getIdCrpRecordNotif())) {
				// always the fields are editable on first launch
				contentEditableStatus = ServiceConstants.TRUE; // artf266920
			} else {
				if (Arrays.asList(CodesConstant.CNOTSTAT_NEW, CodesConstant.CNOTSTAT_DRFT)
						.contains(crpRecordNotifAndDetailsDto.getCdNotifctnStat()))
					contentEditableStatus = ServiceConstants.TRUE; // artf266920
			}
			crpDto.setContentEditableStatus(contentEditableStatus);
			centralRegistryCheckDto = getCentralRegistryCheckDto(idRequest);
			if (!ObjectUtils.isEmpty(centralRegistryCheckDto)) crpDto.setCentralRegistryCheckDto( centralRegistryCheckDto);

			// artf268135 add the request and then add matched requests
			idRequestList.add(idRequest);
			List<CrpPersonNameDto> crpPersonNameList = crpRecordNotifDao.getCrpPersonNames(idRequest);
			if (!ObjectUtils.isEmpty(crpPersonNameList) && 0 < crpPersonNameList.size()) {
				crpDto.setCrpPersonNameList( crpPersonNameList);
				for (CrpPersonNameDto crpPersonNameDto : crpPersonNameList) {
					idRequestList.add(crpPersonNameDto.getIdRequest());
				}
			}

			List<PublicCentralRegistryDto> crpBatchResultsList = crpRecordNotifDao.getCrpBatchResult(idRequestList);
			if (!ObjectUtils.isEmpty(crpBatchResultsList) && 0 < crpBatchResultsList.size()) {
				List<PublicCentralRegistryDto> acpAndPostList = crpBatchResultsList
						.stream()
						.filter(o -> ServiceConstants.A.equals(o.getIndMatchClear()) && ServiceConstants.Y.equals(o.getIndSavePost()))
						.collect(Collectors.toList());
				crpDto.setCrpBatchResultsList( acpAndPostList );
			} else {
				crpDto.setCrpBatchResultsList( Arrays.asList( new PublicCentralRegistryDto()));
			}
			resourceContractInfoDto = crpRecordNotifDao.getResourceContractInfo(crpRecordNotifReq.getIdCrpCheck());
			if (!ServiceConstants.Y.equals(centralRegistryCheckDto.getIndSingleUser())) {
				crpDto.setResourceContractInfoDto(resourceContractInfoDto);
			}
		} else {
		throw new FormsException(messageSource.getMessage("forms.error.prefill.message", null, Locale.US),ServiceConstants.COMMON_BUSINESS_EXCEPTION);
		}
		crpDto.setCrpRecordNotifAndDetailsDto( crpRecordNotifAndDetailsDto);

		EmployeePersonDto empSenderDto = abcsRecordsCheckDao.getStaffContactInfo(crpRecordNotifReq.getIdUser());
		EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(crpRecordNotifReq.getIdUser());
		if (!ObjectUtils.isEmpty(employeePersPhNameDto)) {
			crpDto.setEmployeePersPhNameDto(employeePersPhNameDto);
		}

		if (!ServiceConstants.Y.equals(centralRegistryCheckDto.getIndSingleUser()) && !ObjectUtils.isEmpty(resourceContractInfoDto)
				&& ServiceConstants.ZERO < resourceContractInfoDto.getIdrecCheckRequestor()) {
			empRequestorDto = abcsRecordsCheckDao.getStaffContactInfo(resourceContractInfoDto.getIdrecCheckRequestor());
			crpDto.setEmpRequestorDto(empRequestorDto);
		}

		// Populate email info
		AbcsRecordsCheckDto abcsRecordsCheckDto = new AbcsRecordsCheckDto();
		abcsRecordsCheckDto.setSender(crpRecordNotifReq.getIdUser().toString());

		abcsRecordsCheckDto.setTxtSndrEmail(determineSenderByContractType(resourceContractInfoDto));
		if (!ServiceConstants.Y.equals(centralRegistryCheckDto.getIndSingleUser())
				&& !ObjectUtils.isEmpty(resourceContractInfoDto)) {
			// the recipient field
			abcsRecordsCheckDto.setRecipientEmail(
					ServiceConstants.Y.equals(resourceContractInfoDto.getIndBgcRsltRecpnt())
							? centralRegistryCheckDto.getTxtEmailAddress() : resourceContractInfoDto.getTxtEmailAddress());
		} else {
			abcsRecordsCheckDto.setRecipientEmail(!ObjectUtils.isEmpty(centralRegistryCheckDto)
					? centralRegistryCheckDto.getTxtEmailAddress()
					: ServiceConstants.EMPTY_STRING);
		}
		abcsRecordsCheckDto.setRecipient(String.valueOf(idRequest));
		crpDto.setAbcsRecordsCheckDto(abcsRecordsCheckDto);

		PreFillDataServiceDto preFillDataServiceDto = crpRecordNotifPrefillData.returnPrefillData(crpDto);
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(preFillDataServiceDto));
		commonFormRes.setAbcsRecordsCheckReqDto(abcsRecordsCheckDto);
		return commonFormRes;
	}

	/**
	 * Method Description: This Service is used to retrieve the notification
	 * details for the crp email actions. Also pass the indSingleUser to
	 * clear the form message in the email and add the notify message.
	 *
	 * @param crpRecordNotifReq
	 * @return CommonFormRes
	 */

	@Override
	public CrpRecordNotifAndDetailsDto getCrpRecordNotifAndDetails(CrpRecordNotifReq crpRecordNotifReq) {
		CrpRecordNotifAndDetailsDto crpRecordNotifAndDetailsDto = new CrpRecordNotifAndDetailsDto();
		CentralRegistryCheckDto centralRegistryCheckDto = new CentralRegistryCheckDto();

		crpRecordNotifAndDetailsDto = getCrpRecordNotifAndDetailsDto(crpRecordNotifReq);

		centralRegistryCheckDto = getCentralRegistryCheckDto(crpRecordNotifAndDetailsDto.getIdRequest());
		// pass IndSingleUser to emailAction
		if (ServiceConstants.Y.equals(centralRegistryCheckDto.getIndSingleUser())) {
			crpRecordNotifAndDetailsDto.setIndSingleUser(ServiceConstants.Y);
			crpRecordNotifAndDetailsDto.setNameFull(TypeConvUtil.getNameFirstMiddleInitialLast(
					centralRegistryCheckDto.getNmFirst(), ServiceConstants.EMPTY_STRING
					, centralRegistryCheckDto.getNmLast(), false));
			crpRecordNotifAndDetailsDto.setIndSecureNotification(ServiceConstants.N);
		}
		return crpRecordNotifAndDetailsDto;
	}

	/**
	 * Returns the sender email based on Contract Type with default OTHR
	 *
	 * @param resourceContractInfoDto
	 * @return sender
	 */
	private String determineSenderByContractType(ResourceContractInfoDto resourceContractInfoDto) {
		String sender;
		if (!ObjectUtils.isEmpty(resourceContractInfoDto) && !TypeConvUtil.isNullOrEmpty(resourceContractInfoDto.getCdCntrctType())
				&& !TypeConvUtil.isNullOrEmpty(lookupDao.simpleDecodeSafe(CodesConstant.CCBCUEML, resourceContractInfoDto.getCdCntrctType()))) {
			sender = lookupDao.simpleDecodeSafe(CodesConstant.CCBCUEML, resourceContractInfoDto.getCdCntrctType());
		} else{
			sender = lookupDao.simpleDecodeSafe(CodesConstant.CCBCUEML, CodesConstant.CCBCUEML_OTHR);
		}
		return sender;
	}

	/**
	 * Returns the centralRegistryCheckDto using idRequest
	 *
	 * @param idRequest
	 * @return centralRegistryCheckDto
	 */
	public CentralRegistryCheckDto getCentralRegistryCheckDto(Long idRequest) {
		CentralRegistryCheckDto centralRegistryCheckDto = new CentralRegistryCheckDto();
		centralRegistryCheckDto = crpRecordNotifDao.getCrpReqDtl(idRequest);
		return centralRegistryCheckDto;
	}

	/**
	 * Returns the crpRecordNotifAndDetailsDto using idRequest
	 *
	 * @param crpRecordNotifReq
	 * @return crpRecordNotifAndDetailsDto
	 */
	public CrpRecordNotifAndDetailsDto getCrpRecordNotifAndDetailsDto(CrpRecordNotifReq crpRecordNotifReq) {
		CrpRecordNotifAndDetailsDto crpRecordNotifAndDetailsDto = new CrpRecordNotifAndDetailsDto();
		crpRecordNotifAndDetailsDto = crpRecordNotifDao.getCrpRecordNotifAndDetails(crpRecordNotifReq);
		return crpRecordNotifAndDetailsDto;
	}

}
