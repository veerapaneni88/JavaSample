package us.tx.state.dfps.service.admin.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.domain.PersonId;
import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.service.admin.dto.EmpPersonIdDto;
import us.tx.state.dfps.service.admin.service.PersonIdService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.RetrvPersonIdentifiersReq;
import us.tx.state.dfps.service.common.request.SavePersonIdentifiersReq;
import us.tx.state.dfps.service.common.response.RetrvPersonIdentifiersRes;
import us.tx.state.dfps.service.common.response.SavePersonIdentifiersRes;
import us.tx.state.dfps.service.person.dao.PersonIdDao;
import us.tx.state.dfps.service.person.dto.PersonIdentifiersDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN04S Class
 * Description: Operations for PersonId Apr 14, 2017 - 11:17:22 AM
 */
@Service
@Transactional
public class PersonIdServiceImpl implements PersonIdService {

	@Autowired
	PersonIdDao personIdDao;

	/**
	 * 
	 * Method Description:getEmpPersonIdDto
	 * 
	 * @param personId
	 * @return
	 */
	@Override
	public EmpPersonIdDto getEmpPersonIdDto(PersonId personId) {
		EmpPersonIdDto empPersonIdDto = new EmpPersonIdDto();
		if (personId != null) {
			if (personId.getIdPersonId() != null) {
				empPersonIdDto.setIdPersonId(personId.getIdPersonId());
			}
			if (personId.getPerson() != null) {
				if (personId.getPerson().getIdPerson() != null) {
					empPersonIdDto.setIdPerson(personId.getPerson().getIdPerson());
				}
			}
			if (personId.getCdPersonIdType() != null) {
				empPersonIdDto.setCdPersonIdType(personId.getCdPersonIdType());
			}
			if (personId.getIndPersonIdInvalid() != null) {
				empPersonIdDto.setIndPersonIDInvalid(personId.getIndPersonIdInvalid());
			}
			if (personId.getDtPersonIdEnd() != null) {
				empPersonIdDto.setDtPersonIDEnd(personId.getDtPersonIdEnd());
			}
			if (personId.getDtPersonIdStart() != null) {
				empPersonIdDto.setDtPersonIDStart(personId.getDtPersonIdStart());
			}
			if (personId.getNbrPersonIdNumber() != null) {
				empPersonIdDto.setPersonIdNumber(personId.getNbrPersonIdNumber());
			}
			if (personId.getDescPersonId() != null) {
				empPersonIdDto.setIdDescPerson(personId.getDescPersonId());
			}
			if (personId.getDtLastUpdate() != null) {
				empPersonIdDto.setTsLastUpdate(personId.getDtLastUpdate());
			}
		}
		return empPersonIdDto;
	}

	/**
	 * 
	 * Method Description: This Method will retrieve the Person Identifiers for
	 * a Person List/Detail window by calling the DAO layer. Service Name :
	 * CINT19S
	 * 
	 * @param retrvPersonIdentifiersReq
	 *            - Request Object to be passed to the DAO layer
	 * @return RetrvPersonIdentifiersRes - Response Object to be returned from
	 *         the DAO layer
	 * 
	 * 
	 *         List<PersonDto> personDetailsReqList = new
	 *         ArrayList<PersonDto>(); PersonDetlsRes PersonDetlsRes = new
	 *         PersonDetlsRes(); personDetailsReqList =
	 *         personIdDao.getPersonIdentifiers(personDetailsReq);
	 *         PersonDetlsRes.setpersonDetailsReqList(personDetailsReqList);
	 *         PersonDetlsRes.setDtSystemDate(new Date());
	 * 
	 *         public PersonDetlsRes getPersonIdentifers(@RequestBody
	 *         PersonDetailsReq personDetailsReq)
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RetrvPersonIdentifiersRes getPersonIdentifiersDetailList(
			RetrvPersonIdentifiersReq retrvPersonIdentifiersReq) {
		List<PersonIdentifiersDto> personIdentifiersDtlsList = new ArrayList<PersonIdentifiersDto>();
		RetrvPersonIdentifiersRes retrvPersonIdentifiersRes = new RetrvPersonIdentifiersRes();
		personIdentifiersDtlsList = personIdDao.getPersonIdentifiers(retrvPersonIdentifiersReq);
		retrvPersonIdentifiersRes.setPersonIdentifiersDtlsList(personIdentifiersDtlsList);
		retrvPersonIdentifiersRes.setDtSystemDate(new Date());
		return retrvPersonIdentifiersRes;
	}

	/**
	 * 
	 * Method Description: This Method will add/update/delete the Person
	 * Identifiers for a Person by calling the DAO layer. Service Name : CINT23S
	 * 
	 * @param savePersonIdentifiersReq
	 *            - Request Object to be passed to the DAO layer
	 * @return SavePersonIdentifiersRes - Response Object to be returned from
	 *         the DAO layer
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SavePersonIdentifiersRes savePersonIdentifiersDetail(SavePersonIdentifiersReq savePersonIdentifiersReq) {
		SavePersonIdentifiersRes savePersonIdentifiersRes = new SavePersonIdentifiersRes();
		savePersonIdentifiersRes = personIdDao.savePersonIdentifiersDetail(savePersonIdentifiersReq);
		return savePersonIdentifiersRes;
	}

	/**
	 * 
	 * Method Description: get the placement event ID if the person has current
	 * AA medicaid; otherwise get a zero. Used as a yes/no, and if yes the event
	 * ID is used
	 * 
	 * @param ulIDPerson
	 *            - Person ID
	 * @return Long - Placement Event ID
	 * 
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public Long getPlacementIdIfPersonHasAaMedicaid(Long ulIDPerson) {
		return personIdDao.getPlacementIdIfPersonHasAaMedicaid(ulIDPerson);
	}

	/**
	 * 
	 * Method Description: Updates the person identifier end date of the person
	 * 
	 * @param SavePersonIdentifiersReq
	 * @return void
	 * 
	 */
	@Transactional
	public ServiceResHeaderDto updateIdType(SavePersonIdentifiersReq savePersonIdentifiersReq) {
		personIdDao.updateIdType(savePersonIdentifiersReq);
		ServiceResHeaderDto serviceResHeaderDto = new ServiceResHeaderDto();
		serviceResHeaderDto.setPerfErrInd(ServiceConstants.SUCCESS);
		return serviceResHeaderDto;
	}

	/**
	 * Method Name: isAssmntPerson Method Description:to check if a person is
	 * associated with PROC or COMP
	 * 
	 * @param personId
	 * @param stageId
	 * @return Boolean @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isAssmntPerson(Long personId, Long stageId) {

		boolean isAssmntPerson = false;

		if (personIdDao.isAssmntChildOhmExists(personId, stageId)
				|| personIdDao.isAssmntCaregiverExists(personId, stageId)) {
			isAssmntPerson = true;
		}
		return isAssmntPerson;

	}

	/**
	 * Method Name: isPlcmntPerson Method Description:to check if a person is
	 * associated with an open or closed PCSP placement
	 * 
	 * @param caseId
	 * @param personId
	 * @param cdStage
	 * @return Boolean @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isPlcmntPerson(Long idCase, Long IdPerson, String cdStage) {
		boolean isPlcmntPerson = false;

		if (isPCSPStage(cdStage) && (personIdDao.isPlcmntCaregiverExists(idCase, IdPerson)
				|| personIdDao.isPlcmntChildExists(idCase, IdPerson))) {
			isPlcmntPerson = true;
		}
		return isPlcmntPerson;
	}

	/**
	 * Method Name: isPCSPStage Method Description:Private method to check if
	 * PCSP is available in a stage
	 * 
	 * @param cdStage
	 * @return boolean
	 */
	private boolean isPCSPStage(String cdStage) {
		boolean isPCSPStage = false;
		if ((!StringUtils.isEmpty(cdStage)) && (ServiceConstants.CSTAGES_AR.equals(cdStage)
				|| ServiceConstants.CSTAGES_INV.equals(cdStage) || ServiceConstants.CSTAGES_FPR.equals(cdStage)
				|| ServiceConstants.CSTAGES_FSU.equals(cdStage) || ServiceConstants.CSTAGES_FRE.equals(cdStage)))
			isPCSPStage = true;
		return isPCSPStage;
	}

	/**
	 * Method Description: This Method will retrieve the Person Identifiers for
	 * a person List/Detail window.
	 * 
	 * @param retrvPersonIdentifiersReq
	 * @return RetrvPersonIdentifiersRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RetrvPersonIdentifiersRes getPersonIdentifierByIdType(RetrvPersonIdentifiersReq retrvPersonIdentifiersReq) {
		List<PersonIdentifiersDto> personIdentifiersDtlsList = new ArrayList<PersonIdentifiersDto>();
		RetrvPersonIdentifiersRes retrvPersonIdentifiersRes = new RetrvPersonIdentifiersRes();

		personIdentifiersDtlsList = personIdDao.getPersonIdentifierByIdType(retrvPersonIdentifiersReq);
		retrvPersonIdentifiersRes.setPersonIdentifiersDtlsList(personIdentifiersDtlsList);
		retrvPersonIdentifiersRes.setDtSystemDate(new Date());
		return retrvPersonIdentifiersRes;
	}

	/**
	 * Method Name: fetchIdentifiersList Method Description:Fetches list of
	 * person identifiers for a Person from Snapshot table (SS_PERSON_ID)
	 * 
	 * @param idPerson
	 * @param getbActiveFlag
	 * @param idReferenceData
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @return List<PersonIdentifierValueDto>
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public List<PersonIdentifiersDto> fetchIdentifiersList(Long idPerson, Boolean getbActiveFlag, Long idReferenceData,
			String cdActionType, String cdSnapshotType) {
		return personIdDao.getPersonIdentifiersList(idPerson, getbActiveFlag, idReferenceData, cdActionType,
				cdSnapshotType);
	}

	/**
	 * Method Name: fetchIdentifiersList Method Description: Get Identifiers for
	 * input person id.
	 * 
	 * @param fetchIdentifiersReq
	 * @return FetchIdentifiersRes
	 * 
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<PersonIdentifiersDto> fetchIdentifiersList(Long idPerson, Boolean activeFlag) {
		return personIdDao.getPersonIdentifiersList(idPerson, activeFlag);
	}
}
