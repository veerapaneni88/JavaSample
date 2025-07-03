package us.tx.state.dfps.service.person.serviceimpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.phoneticsearch.IIRHelper.Messages;
import us.tx.state.dfps.service.admin.dto.StageTaskInDto;
import us.tx.state.dfps.service.admin.service.StageEventStatusCommonService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PersonPhoneReq;
import us.tx.state.dfps.service.common.request.PhoneMassUpdateReq;
import us.tx.state.dfps.service.common.request.PhoneReq;
import us.tx.state.dfps.service.common.response.PhoneMassUpdateRes;
import us.tx.state.dfps.service.common.response.PhoneRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.person.dao.PersonPhoneDao;
import us.tx.state.dfps.service.person.dto.PersonPhoneRetDto;
import us.tx.state.dfps.service.person.service.PersonPhoneService;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN46S Class
 * Description: Service to call Dao. Apr 10, 2017 - 4:04:53 PM
 */
@Service
@Transactional
public class PersonPhoneServiceImpl implements PersonPhoneService {

	@Autowired
	PersonPhoneDao personPhoneDao;

	@Autowired
	StageEventStatusCommonService stageEventStatusCommonService;

	private static final Logger log = Logger.getLogger(PersonPhoneServiceImpl.class);

	/**
	 * 
	 * Method Description: This Method will retrieve information for the Phone
	 * List/Detail window. Service Name : CCMN46S
	 * 
	 * @param phonereq
	 * @return PhoneRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PhoneRes getPersonPhoneDetailList(PhoneReq phonereq) {
		List<PersonPhoneRetDto> personPhone = new ArrayList<PersonPhoneRetDto>();
		PhoneRes phoneRes = new PhoneRes();
		personPhone = personPhoneDao.getPersonPhoneDetailList(phonereq);
		phoneRes.setPhoneDtoList(personPhone);
		log.info("TransactionId :" + phonereq.getTransactionId());
		return phoneRes;
	}

	/**
	 ** Method Description: This Method will perform Add and Update of
	 * PersonPhone table. Tuxedo Service Name : CCMN31S Dam: CCMN95D
	 * 
	 * @param PersonPhoneReq
	 * @return PersonPhoneRes @
	 * 
	 */
	public PhoneRes savePersonPhoneDtls(PersonPhoneReq personPhoneReq) {
		PhoneRes phoneRes = new PhoneRes();
		Date endDate = ServiceConstants.GENERIC_END_DATE;
		if (personPhoneReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
			if (TypeConvUtil.isNullOrEmpty(personPhoneReq.getPersonPhoneRetDto().getDtPersonPhoneEnd())) {
				endDate = ServiceConstants.GENERIC_END_DATE;
			} else {
				Calendar cal = Calendar.getInstance();
				cal.setTime(personPhoneReq.getPersonPhoneRetDto().getDtPersonPhoneEnd());
				// cal.add(Calendar.DATE, 1);
				endDate = cal.getTime();
			}
		}
		StageTaskInDto pCCMN06UInputRec = new StageTaskInDto();
		pCCMN06UInputRec.setReqFuncCd(personPhoneReq.getReqFuncCd());
		pCCMN06UInputRec.setIdStage(personPhoneReq.getUlIdStage());
		if (personPhoneReq.getCdTask() != null)
			pCCMN06UInputRec.setCdTask(personPhoneReq.getCdTask());
		else
			pCCMN06UInputRec.setCdTask("");
		String stageEventStatus = stageEventStatusCommonService.checkStageEventStatus(pCCMN06UInputRec);
		if (StringUtils.isNotEmpty(stageEventStatus)) {
			switch (stageEventStatus) {
			case ServiceConstants.MSG_SYS_MULT_INST: {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(Messages.MSG_SYS_MULT_INST);
				phoneRes.setErrorDto(errorDto);
				return phoneRes;
			}

			case ServiceConstants.MSG_SYS_EVENT_STS_MSMTCH: {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(Messages.MSG_SYS_EVENT_STS_MSMTCH);
				phoneRes.setErrorDto(errorDto);
				return phoneRes;
			}

			case ServiceConstants.MSG_SYS_STAGE_CLOSED: {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(Messages.MSG_SYS_STAGE_CLOSED);
				phoneRes.setErrorDto(errorDto);
				return phoneRes;
			}
			default:
				break;
			}

		}
		phoneRes = personPhoneDao.savePersonPhoneDtls(personPhoneReq, endDate);
		return phoneRes;
	}

	/**
	 * 
	 * Method Description: This Method will retrieve information for the Phone
	 * pullback window. Service Name : PhoneList Pullback EJB
	 * 
	 * @param phonereq
	 * @return PhoneRes @,
	 * 
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PhoneRes getPersonPhonePullback(PhoneReq phonereq) {
		List<PersonPhoneRetDto> personPhone = new ArrayList<PersonPhoneRetDto>();
		PhoneRes phoneRes = new PhoneRes();
		personPhone = personPhoneDao.getPersonPhoneDetailPullback(phonereq);
		phoneRes.setPhoneDtoList(personPhone);
		log.info("TransactionId :" + phonereq.getTransactionId());
		return phoneRes;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
	public PhoneRes getPersonPhoneNumber(PhoneReq phonereq) {
		PersonPhoneRetDto personPhone = new PersonPhoneRetDto();
		PhoneRes phoneRes = new PhoneRes();
		personPhone = personPhoneDao.getPersonPhoneNumber(phonereq);
		phoneRes.setPhoneDto(personPhone);
		log.info("TransactionId :" + phonereq.getTransactionId());
		return phoneRes;
	}

	/**
	* 
	*/
	@Override
	public void updatePersonPhone(PersonPhoneReq request) {
		personPhoneDao.updatePersonPhone(request);
	}

	/**
	 * 
	 * Method Description: This Method will retrieve information for the Phone
	 * List/Detail window. Service Name : CCMN46S
	 * 
	 * @param phonereq
	 * @return PhoneRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PhoneRes getPersonFPPhoneDetailList(PhoneReq phonereq) {
		List<PersonPhoneRetDto> personPhone = new ArrayList<PersonPhoneRetDto>();
		PhoneRes phoneRes = new PhoneRes();
		personPhone = personPhoneDao.getPersonFPPhoneDetailList(phonereq);
		phoneRes.setPhoneDtoList(personPhone);
		log.info("TransactionId :" + phonereq.getTransactionId());
		return phoneRes;
	}

	/**
	 * Method Description: This Method will perform phone mass update.
	 * 
	 * @param phoneMassUpdateReq
	 * @return PhoneMassUpdateRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PhoneMassUpdateRes phoneMassUpdate(PhoneMassUpdateReq phoneMassUpdateReq) {
		PhoneMassUpdateRes phoneMassUpdateRes = new PhoneMassUpdateRes();
		List<PersonPhoneRetDto> massPersonPhoneRetDto = phoneMassUpdateReq.getPersonPhoneRetDtoList();
		PersonPhoneReq personPhoneReq = new PersonPhoneReq();
		for (PersonPhoneRetDto personPhoneRetDto : massPersonPhoneRetDto) {
			Date endDate = ServiceConstants.GENERIC_END_DATE;
			if (TypeConvUtil.isNullOrEmpty(personPhoneRetDto.getDtPersonPhoneEnd())) {
				endDate = ServiceConstants.GENERIC_END_DATE;
			} else {
				Calendar cal = Calendar.getInstance();
				cal.setTime(personPhoneRetDto.getDtPersonPhoneEnd());
				// cal.add(Calendar.DATE, 1);
				endDate = cal.getTime();
			}
			personPhoneReq.setReqFuncCd(personPhoneRetDto.getCdScrDataAction());
			personPhoneReq.setUlIdPerson(personPhoneRetDto.getIdPerson());
			personPhoneReq.setPersonPhoneRetDto(personPhoneRetDto);
			PhoneRes phoneRes = personPhoneDao.savePersonPhoneDtls(personPhoneReq, endDate);
			log.info("Saved Phone Ends with Status " + phoneRes.getReturnMessage());
		}
		phoneMassUpdateRes.setReturnMsg(ServiceConstants.SUCCESS);
		return phoneMassUpdateRes;
	}

	/**
	 * Method Name: getPersonPrimaryActivePhone Method Description: This method
	 * gets person primary phone for input person id
	 * 
	 * @param personId
	 * @return List<PersonPhoneRetDto>
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public PersonPhoneRetDto getPersonPrimaryActivePhone(Long personId) {
		return personPhoneDao.getPersonPrimaryActivePhone(personId);
	}

	/**
	 * 
	 * Method Name: getPersonPrimaryActivePhone Method Description:Reads the
	 * current primary phone for a person from snapshot table (SS_PERSON_PHONE)
	 * ( For example: This method is used for displaying the Select Forward
	 * person details in post person merge page)
	 * 
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @param idPerson
	 * @param idReferenceData
	 * @return
	 */
	@Override
	public PersonPhoneRetDto getPersonPrimaryActivePhone(String cdActionType, String cdSnapshotType, Long idPerson,
			Long idReferenceData) {
		return personPhoneDao.getPersonPrimaryActivePhone(idPerson, idReferenceData, cdActionType, cdSnapshotType);
	}
}
