package us.tx.state.dfps.service.person.serviceimpl;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.exception.FormsException;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.AbcsRecordCheckReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.dto.RecordsCheckNotifDto;
import us.tx.state.dfps.service.forms.util.AbcsRecordsCheckPrefillData;
import us.tx.state.dfps.service.person.dao.AbcsRecordsCheckDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.RecordsCheckNotifDao;
import us.tx.state.dfps.service.person.service.AbcsRecordsCheckService;
import us.tx.state.dfps.service.recordscheck.dto.AbcsDto;
import us.tx.state.dfps.service.recordscheck.dto.AbcsRecordsCheckDto;
import us.tx.state.dfps.service.recordscheck.dto.AddressPersonLinkDto;
import us.tx.state.dfps.service.recordscheck.dto.EmployeePersonDto;
import us.tx.state.dfps.service.recordscheck.dto.PopulateNotificationDto;
import us.tx.state.dfps.service.recordscheck.dto.ResourceContractInfoDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * AbcsRecordsCheckServiceImpl will implement all operation defined in
 * AbcsRecordsCheckService Interface related Record Check Screen for forms Mar
 * 14, 2018- 2:17:34 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Service
@Transactional
public class AbcsRecordsCheckServiceImpl implements AbcsRecordsCheckService {

	private static final Logger logger = Logger.getLogger(AbcsRecordsCheckServiceImpl.class);

	@Autowired
	AbcsRecordsCheckDao abcsRecordsCheckDao;

	@Autowired
	RecordsCheckNotifDao recordsCheckNotifDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	AbcsRecordsCheckPrefillData abcsRecordsCheckPrefillData;
	
	/**message source. */
	@Autowired
	MessageSource messageSource;

	private static final String WHITESPACE = " ";

	@Override
	public CommonFormRes getRecordsCheckNtfcnForm(AbcsRecordCheckReq abcsRecordCheckReq) {
		logger.info(
				"Entering into getRecordsCheckNtfcnForm Method-TransactionID:" + abcsRecordCheckReq.getTransactionId());
		CommonFormRes commonFormRes = new CommonFormRes();
		AbcsRecordsCheckDto abcsRecordsCheckReqDto = new AbcsRecordsCheckDto();
		RecordsCheckNotifDto recordsCheckNotifDto = new RecordsCheckNotifDto();
		AbcsDto abcsDto = new AbcsDto();
		abcsDto.setAbcsRecordCheckReq(abcsRecordCheckReq);
		ResourceContractInfoDto resourceContractInfoDto = abcsRecordsCheckDao
				.getResourceContractInfo(abcsRecordCheckReq.getIdRecCheck());
		abcsDto.setResourceContractInfoDto(resourceContractInfoDto);

		PopulateNotificationDto populateNotificationDto = new PopulateNotificationDto();
		if (!ObjectUtils.isEmpty(abcsRecordCheckReq.getIdRecordsCheckNotif())
				&& ServiceConstants.ZERO_VAL < abcsRecordCheckReq.getIdRecordsCheckNotif()) {
			recordsCheckNotifDto = recordsCheckNotifDao
					.getRecordsCheckNotification(abcsRecordCheckReq.getIdRecordsCheckNotif());
			abcsDto.setRecordsCheckNotifDto(recordsCheckNotifDto);
		} else {
			if (!ObjectUtils.isEmpty(abcsRecordCheckReq.getDocType())) {
				recordsCheckNotifDto.setCdNotifType(abcsRecordCheckReq.getDocType());
			}
			recordsCheckNotifDto.setCdNotifctnStat(CodesConstant.CNOTSTAT_NEW);
			abcsDto.setRecordsCheckNotifDto(recordsCheckNotifDto);
		}
		// Populate sender info
		abcsRecordsCheckReqDto.setSender(abcsRecordCheckReq.getIdUser().toString());
		EmployeePersonDto employeePersonDto = abcsRecordsCheckDao.getStaffContactInfo(abcsRecordCheckReq.getIdUser());
		abcsDto.setEmployeePersonDto(employeePersonDto);
		if (!ObjectUtils.isEmpty(employeePersonDto)) {
			List<EmployeePersonDto> employeeSupervisorDtoList = abcsRecordsCheckDao
					.getEmployeeSupervisorInfo(employeePersonDto.getIdPerson());
			abcsDto.setEmployeeSupervisorDtoList(employeeSupervisorDtoList);
			EmployeePersonDto employeeWorkerPhoneDto = abcsRecordsCheckDao
					.getEmployeeWorkPhoneInfo(employeePersonDto.getIdPerson());
			abcsDto.setEmployeeWorkerPhoneDto(employeeWorkerPhoneDto);
			populateNotificationDto.setSndrFullName(
					getSenderFnameLname(TypeConvUtil.formatFullName(employeePersonDto.getNmEmployeeFirst(),
							employeePersonDto.getNmEmployeeMiddle(), employeePersonDto.getNmEmployeeLast())));
			populateNotificationDto.setSndrPhone(getStaffPhone(employeeWorkerPhoneDto));
			populateNotificationDto.setSedSenderEmail(employeePersonDto.getTxtEmployeeEmailAddress());
		}
		// Populate info on the person the check was run on
		if (!ObjectUtils.isEmpty(resourceContractInfoDto)) {
			PersonDto personDto = personDao.getPersonById(resourceContractInfoDto.getIdrecCheckPerson());
			abcsDto.setPersonDto(personDto);
			if (!ObjectUtils.isEmpty(personDto)) {
				populateNotificationDto
						.setCheckFullName(personDto.getNmPersonLast() + ", " + personDto.getNmPersonFirst());
				populateNotificationDto
						.setCheckedPersonFnameLname(personDto.getNmPersonFirst() + " " + personDto.getNmPersonLast());
			}
			// the cregnot checked person is the poc ...
			if (CodesConstant.NOTIFTYP_CREGNOT.equals(recordsCheckNotifDto.getCdNotifType())) {
				abcsRecordsCheckReqDto.setRecipient(resourceContractInfoDto.getIdrecCheckPerson().toString());
				if (!ObjectUtils.isEmpty(personDto)) {
					populateNotificationDto
							.setPocFirstAndLastName(personDto.getNmPersonFirst() + " " + personDto.getNmPersonLast());
				}
				// the checked person is the addressee, not the resource
				// address
				AddressPersonLinkDto addressPersonLinkDto = abcsRecordsCheckDao
						.getPersonAddressDtl(resourceContractInfoDto.getIdrecCheckPerson());
				if (!ObjectUtils.isEmpty(addressPersonLinkDto)) {
					populateNotificationDto.setRsrcSt1(addressPersonLinkDto.getAddrPersAddrStLn1());
					populateNotificationDto.setRsrcSt2(addressPersonLinkDto.getAddrPersAddrStLn2());
					populateNotificationDto.setRsrcCity(addressPersonLinkDto.getAddrPersonAddrCity());
					populateNotificationDto.setRsrcState(addressPersonLinkDto.getCdPersonAddrState());
					populateNotificationDto.setRsrcZip(addressPersonLinkDto.getAddrPersonAddrZip());
					abcsDto.setAddressPersonLinkDto(addressPersonLinkDto);
				}
			}

		}
		else {
			throw new FormsException(messageSource.getMessage("forms.error.prefill.message", null, Locale.US),ServiceConstants.COMMON_BUSINESS_EXCEPTION);
		}
		// Populate the info for the person requesting the check - if not
		// cregnot artf19407
		if (!ObjectUtils.isEmpty(resourceContractInfoDto)
				&& CodesConstant.NOTIFTYP_CREGNOT != recordsCheckNotifDto.getCdNotifType()) {
			PersonDto personDto = personDao.getPersonById(resourceContractInfoDto.getIdrecCheckRequestor());


			Date backgroundSubmitedDate = recordsCheckNotifDto.getDtCreated();
			Long accountId = resourceContractInfoDto.getIdrecCheckRequestor();
			String agencyName =resourceContractInfoDto.getNmResource();
			Long resourceId = resourceContractInfoDto.getIdrecCheckPerson();

			populateNotificationDto.setBackgroundSubmitedDate(backgroundSubmitedDate);
			populateNotificationDto.setAccountId(accountId);
			populateNotificationDto.setAgencyName(agencyName);
			populateNotificationDto.setResourceId(resourceId);




			abcsDto.setPersonDto(personDto);
			abcsRecordsCheckReqDto.setRecipient(resourceContractInfoDto.getIdrecCheckRequestor().toString());
			if (!ObjectUtils.isEmpty(personDto)) {
				populateNotificationDto
						.setPocFirstAndLastName(personDto.getNmPersonFirst() + " " + personDto.getNmPersonLast());
			}
			// Retrieve the requestors person_email if 'Y' == background
			// check recipient indicator.
			EmployeePersonDto employeePersonEmai = abcsRecordsCheckDao
					.getStaffContactInfo(resourceContractInfoDto.getIdrecCheckRequestor());
			String pocEmail = ServiceConstants.EMPTY_STRING;
			if (null != employeePersonEmai.getTxtEmailAddress()) {
				pocEmail = employeePersonEmai.getTxtEmailAddress();
			}
			if (!ObjectUtils.isEmpty(pocEmail)
					&& ServiceConstants.YES.equals(resourceContractInfoDto.getIndBgcRsltRecpnt())) {
				populateNotificationDto.setRecpntEmail(pocEmail);
				abcsRecordsCheckReqDto.setRecipientEmail(populateNotificationDto.getRecpntEmail());
			} else if (!ObjectUtils.isEmpty(resourceContractInfoDto.getTxtEmailAddress())) {
				populateNotificationDto.setRecpntEmail(resourceContractInfoDto.getTxtEmailAddress());
				abcsRecordsCheckReqDto.setRecipientEmail(populateNotificationDto.getRecpntEmail());
			}
		}
		else {
			throw new FormsException(messageSource.getMessage("forms.error.prefill.message", null, Locale.US),ServiceConstants.COMMON_BUSINESS_EXCEPTION);
		}
		abcsDto.setPopulateNotificationDto(populateNotificationDto);

		PreFillDataServiceDto preFillDataServiceDto = abcsRecordsCheckPrefillData.returnPrefillData(abcsDto);
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(preFillDataServiceDto));
		commonFormRes.setAbcsRecordsCheckReqDto(abcsRecordsCheckReqDto);
		return commonFormRes;
	}

	@Override
	public AbcsRecordsCheckDto getAbcsRecordsCheckDetails(Long idRecCheck) {
		return abcsRecordsCheckDao.getAbcsRecordsCheckDetails(idRecCheck);
	}

	/**
	 * This method uses result from EmployeeDao for employee Firstname Lastname,
	 * artf22730
	 */
	private String getSenderFnameLname(String empFullName) {
		String[] fullName = (TypeConvUtil.toString(empFullName)).split(",");
		String[] firstPart = fullName[1].split(WHITESPACE);
		return new StringBuilder().append(firstPart[0]).append(ServiceConstants.SINGLE_WHITESPACE).append(fullName[0])
				.toString();
	}

	/**
	 * This method uses result from EmployeeDao for employee contact information
	 */
	private String getStaffPhone(EmployeePersonDto employeeWorkerPhoneDto) {
		String phone = "";
		if (null != employeeWorkerPhoneDto) {
			phone = TypeConvUtil.getPhoneWithFormat(employeeWorkerPhoneDto.getNbrPersonPhone(),
					employeeWorkerPhoneDto.getNbrPersonPhoneExtension());
		}
		return phone;
	}
}
