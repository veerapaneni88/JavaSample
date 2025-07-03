package us.tx.state.dfps.service.person.serviceimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.RecChkDetermHistory;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.EmailNotificationsReq;
import us.tx.state.dfps.service.common.request.RecordsCheckDetailReq;
import us.tx.state.dfps.service.common.request.RecordsCheckReq;
import us.tx.state.dfps.service.common.request.RecordsCheckStatusReq;
import us.tx.state.dfps.service.common.response.EmailNotificationsRes;
import us.tx.state.dfps.service.common.response.RecordsCheckListRes;
import us.tx.state.dfps.service.common.response.RecordsCheckRes;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.RecordsCheckDao;
import us.tx.state.dfps.service.person.dto.RecordsCheckDeterminationDto;
import us.tx.state.dfps.service.person.dto.RecordsCheckDto;
import us.tx.state.dfps.service.person.dto.RetrieveIdRecordsCheckNotifDto;
import us.tx.state.dfps.service.person.service.RecordsCheckService;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Jun 19,
 * 2017- 8:01:40 PM © 2017 Texas Department of Family and Protective Services
 *  *******Change History**********
 *  01/25/2021 nairl artf172936 : DEV BR 15.01 Indicator (IMPACT) for Person Who Has Access to CHRI P2
 *  02/12/2021 nairl artf172946 : DEV BR 21.01 Support Manual Entry of Results from DPS’ SecureSite into IMPACT P2
 */
@Service
@Transactional
public class RecordsCheckServiceImpl implements RecordsCheckService {

	@Autowired
	RecordsCheckDao recordsCheckDao;

	@Autowired
	PersonDao personDao;

	/**
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#
	 *      getDtCreatedOfPaperRecord(us.tx.state.dfps.service.common.request.
	 *      RecordsCheckReq)
	 */
	@Override
	public RecordsCheckRes getDtCreatedOfPaperRecord(RecordsCheckReq request) {
		return recordsCheckDao.getDtCreatedOfPaperRecord(request);

	}

	/**
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#
	 *      getMostRecentCASAFPSIdRecCheck(us.tx.state.dfps.service.common.request.
	 *      RecordsCheckReq)
	 */
	@Override
	public RecordsCheckRes getMostRecentCASAFPSIdRecCheck(RecordsCheckReq request) {

		return recordsCheckDao.getMostRecentCASAFPSIdRecCheck(request);
	}

	/**
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#
	 *      isPersonCasaProvisioned(us.tx.state.dfps.service.common.request.
	 *      RecordsCheckReq)
	 */
	@Override
	public RecordsCheckRes isPersonCasaProvisioned(RecordsCheckReq request) {

		return recordsCheckDao.isPersonCasaProvisioned(request);
	}

	/**
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#
	 *      retrieveRecordsCheckDocument(us.tx.state.dfps.service.common.request.
	 *      RecordsCheckReq)
	 */
	@Override
	public RecordsCheckRes retrieveRecordsCheckDocument(RecordsCheckReq request) {

		return recordsCheckDao.retrieveRecordsCheckDocument(request);
	}

	/**
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#
	 *      retrieveRecordsCheckNotifications(us.tx.state.dfps.service.common.request
	 *      .RecordsCheckReq)
	 */
	@Override
	public RecordsCheckRes retrieveRecordsCheckNotifications(RecordsCheckReq request) {

		return recordsCheckDao.retrieveRecordsCheckNotifications(request);
	}

	/**
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#
	 *      getRecordsCheckDetail(us.tx.state.dfps.service.common.request.
	 *      RecordsCheckReq)
	 */
	@Override
	public RecordsCheckRes getRecordsCheckDetail(RecordsCheckReq request) {
       RecordsCheckRes s=recordsCheckDao.getRecordsCheckDetail(request);
		return recordsCheckDao.getRecordsCheckDetail(request);
	}

	/**
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#isABCSCheck(
	 *      us.tx.state.dfps.service.common.request.RecordsCheckReq)
	 */
	@Override
	public RecordsCheckRes isABCSCheck(RecordsCheckReq request) {
		return recordsCheckDao.isABCSCheck(request);
	}

	/**
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#
	 *      isCasaFpsCheck(us.tx.state.dfps.service.common.request.RecordsCheckReq)
	 */
	@Override
	public RecordsCheckRes isCasaFpsCheck(RecordsCheckReq request) {
		return recordsCheckDao.isCasaFpsCheck(request);
	}

	/**
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#
	 *      getAbcsContractID(us.tx.state.dfps.service.common.request.
	 *      RecordsCheckReq)
	 */
	@Override
	public RecordsCheckRes getAbcsContractID(RecordsCheckReq request) {
		return recordsCheckDao.getAbcsContractID(request);
	}
	/**
	 * Added to implement artf172936
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#
	 *      getAbcsAccessData(us.tx.state.dfps.service.common.request.
	 *      RecordsCheckReq)
	 */
	@Override
	public RecordsCheckRes getAbcsAccessData(RecordsCheckReq request) {
		return recordsCheckDao.getAbcsAccessData(request);
	}

	/**
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#
	 *      callDPSWSNameSearchProcedure(us.tx.state.dfps.service.common.request.
	 *      RecordsCheckReq)
	 */
	@Override
	public void callDPSWSNameSearchProcedure(RecordsCheckReq request) {
		recordsCheckDao.callDPSWSNameSearchProcedure(request);

	}

	/**
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#
	 *      getPersonNameList(us.tx.state.dfps.service.common.request.
	 *      RecordsCheckReq)
	 */
	@Override
	public RecordsCheckRes getPersonNameList(RecordsCheckReq request) {

		return recordsCheckDao.getPersonNameList(request);
	}

	/**
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#isNameValid(
	 *      us.tx.state.dfps.service.common.request.RecordsCheckReq)
	 */
	@Override
	public RecordsCheckRes isNameValid(RecordsCheckReq recordsCheckReq) {
		return recordsCheckDao.isNameValid(recordsCheckReq.getPersonNameCheckDto());
	}

	/**
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#
	 *      hasPendingFingerprintCheck(us.tx.state.dfps.service.common.request.
	 *      RecordsCheckReq)
	 *      
	 */
	@Override
	public RecordsCheckRes hasPendingFingerprintCheck(RecordsCheckReq request) {
		return recordsCheckDao.hasPendingFingerprintCheck(request);
	}

	/**
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#
	 *      getScorContractNbr(us.tx.state.dfps.service.common.request.
	 *      RecordsCheckReq)
	 */
	@Override
	public RecordsCheckRes getScorContractNbr(RecordsCheckReq request) {
		return recordsCheckDao.getScorContractNbr(request);
	}

	/**
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#
	 *      generateFBIClearanceEmail(us.tx.state.dfps.service.common.request.
	 *      EmailNotificationsReq)
	 */
	@Override
	public EmailNotificationsRes generateFBIEligibleExhireEmail(EmailNotificationsReq request) {

		return recordsCheckDao.generateFBIEligibleExhireEmail(request);
	}

	@Override
	public EmailNotificationsRes generateEligibleEmail(EmailNotificationsReq request) {
		return recordsCheckDao.generateEligibleEmail(request);
	}

	@Override
	public EmailNotificationsRes generatePCSEligibleEmail(EmailNotificationsReq request) {
		return recordsCheckDao.generatePCSEligibleEmail(request);
	}


	/**
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#
	 *      generateFBIEligibleEmail(us.tx.state.dfps.service.common.request.
	 *      EmailNotificationsReq)
	 */

	@Override
	public EmailNotificationsRes generateIneligibleEmail(EmailNotificationsReq request) {

		return recordsCheckDao.generateIneligibleEmail(request);
	}

	@Override
	public EmailNotificationsRes generatePCSInEligibleEmail(EmailNotificationsReq request)
	{
		return recordsCheckDao.generatePCSIneligibleEmail(request);
	}

	/**
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#
	 *      generateFBIEligibleEmail(us.tx.state.dfps.service.common.request.
	 *      EmailNotificationsReq)
	 */

	/**
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#
	 *      generatePSClearanceEmail(us.tx.state.dfps.service.common.request.
	 *      EmailNotificationsReq)
	 */
	@Override
	public EmailNotificationsRes generatePSClearanceEmail(EmailNotificationsReq request) {
		return recordsCheckDao.generatePSClearanceEmail(request);
	}

	/**
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#hasEmailSent(
	 *      us.tx.state.dfps.service.common.request.RecordsCheckReq)
	 */
	@Override
	public RecordsCheckRes hasEmailSent(RecordsCheckReq request) {
		return recordsCheckDao.hasEmailSent(request);
	}

	/**
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#
	 *      getRecordDocumentTsLastUpdate(us.tx.state.dfps.service.common.request.
	 *      RecordsCheckReq)
	 */
	@Override
	public RecordsCheckRes getRecordDocumentTsLastUpdate(RecordsCheckReq request) {
		return recordsCheckDao.getRecordDocumentTsLastUpdate(request);
	}

	/**
	 * @see us.tx.state.dfps.service.person.service.RecordsCheckService#
	 *      deleteDocumentPdbRecord(us.tx.state.dfps.service.common.request.
	 *      RecordsCheckReq)
	 */
	@Override
	public void deleteDocumentPdbRecord(RecordsCheckDetailReq request) {
		recordsCheckDao.deleteDocumentPdbRecord(request);
	}

	/**
	 * Method Name: getRecordsCheckList Method Description: This service is used
	 * to retrieve the list of Records Check
	 *
	 * @return RecordsCheckListRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public RecordsCheckListRes getRecordsCheckList(RecordsCheckReq recordsCheckReq) {
		RecordsCheckListRes recordCheckRetrieveRes = new RecordsCheckListRes();
		List<RecordsCheckDto> recordsCheckList = recordsCheckDao
					.getRecordsCheckList(recordsCheckReq.getIdRecCheckPerson(), recordsCheckReq.getIdRecCheck());
		boolean indDpsNotComplete=recordsCheckList.stream()
				.anyMatch(recordsCheck -> ServiceConstants.DPS_CODE.equals(recordsCheck.getRecCheckCheckType())
						&& ObjectUtils.isEmpty(recordsCheck.getDtRecCheckCompleted())
						&& !ServiceConstants.DPS_STAT_OVERDUE.equals(recordsCheck.getRecCheckStatus())
						&& !ServiceConstants.DPS_STAT_DPS_REJECT.equals(recordsCheck.getRecCheckStatus())
						&& !ServiceConstants.DPS_STAT_PRS_REJECT.equals(recordsCheck.getRecCheckStatus())
						);
		if(indDpsNotComplete) {
			recordCheckRetrieveRes.setIndRecCheckDpsIncomplete(ServiceConstants.INDICATOR_YES);
		}
		else {
			recordCheckRetrieveRes.setIndRecCheckDpsIncomplete(ServiceConstants.INDICATOR_NO);
		}
		
		if (ServiceConstants.ADD.equalsIgnoreCase(recordsCheckReq.getReqFuncCd())){
			//reset as this is ADD scenario
			recordsCheckList = new ArrayList<>();
		}

		recordsCheckList.stream().forEach(recordsCheck -> {
			// call DAM CINVF1D
			recordsCheck.setIndOutstandingCHAcpRej(recordsCheckDao.hasCHActions(recordsCheck.getIdRecCheck()));
			// call DAM CLSSB7D
			recordsCheck
					.setRecordsCheckDetermination(recordsCheckDao.getRechDetermHistory(recordsCheck.getIdRecCheck()));
		});
		recordCheckRetrieveRes.setRecordCheckListDto(recordsCheckList);
		// call DAM CCMN44D
		PersonDto personDto = personDao.getPersonById(recordsCheckReq.getIdRecCheckPerson());
		// set person information values to the response
		recordCheckRetrieveRes.setNmPersonFirst(personDto.getNmPersonFirst());
		recordCheckRetrieveRes.setNmPersonLast(personDto.getNmPersonLast());
		recordCheckRetrieveRes.setNmPersonFull(personDto.getNmPersonFull());
		recordCheckRetrieveRes.setPersonEthnicGroup(personDto.getCdPersonEthnicGroup());
		recordCheckRetrieveRes.setPersonSex(personDto.getCdPersonSex());
		recordCheckRetrieveRes.setPersonBirthDate(personDto.getDtPersonBirth());
		recordCheckRetrieveRes.setPersonAge(!ObjectUtils.isEmpty(personDto.getPersonAge())
				? personDto.getPersonAge().longValue() : ServiceConstants.ZERO);
		recordCheckRetrieveRes.setIndPersonDobApprox(personDto.getIndPersonDobApprox());
		return recordCheckRetrieveRes;

	}

	@Override
	public Integer getMaxIdRecCheckNotif() {

		Integer idRecCheckNotif;

		RetrieveIdRecordsCheckNotifDto idRecordsCheckNotifDto;

		idRecordsCheckNotifDto = recordsCheckDao.getMaxIdRecordsCheckNotif();

		idRecCheckNotif = idRecordsCheckNotifDto.getIdRecordsCheckNotif().intValue();

		return idRecCheckNotif;

	}

	/**
	 * 
	 * Method Name: recordsCheckAUDService Method Description:This method
	 * performs adds, updates, and deletes on the Records Check table given
	 * IdRecCheck
	 * 
	 * @param RecordsCheckDetailReq
	 * @return RecordsCheckListRes
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RecordsCheckListRes recordsCheckAUDService(RecordsCheckDetailReq recordsCheckDetailReq) {
		RecordsCheckListRes recordsCheckListRes = new RecordsCheckListRes();

		// call DAM CAUD87D
		recordsCheckListRes.setIdRecordCheck(recordsCheckDao.recordsCheckAUD(recordsCheckDetailReq));
		if (ServiceConstants.REQ_FUNC_CD_UPDATE.equalsIgnoreCase(recordsCheckDetailReq.getReqFuncCd())
				&& !ObjectUtils.isEmpty(recordsCheckDetailReq.getRecordsCheckDtoList().get(0).getRecChkDeterm())) {
			// Call DAM CLSSB7D for update only
			List<RecordsCheckDeterminationDto> recordsCheckDeterminationList = recordsCheckDao
					.getRechDetermHistory(recordsCheckDetailReq.getRecordsCheckDtoList().get(0).getIdRecCheck());
			//artf205009 - Comparing the person id if a different logged in user is selecting CLR in Determination drop down
			if (recordsCheckDeterminationList == null || recordsCheckDeterminationList.size() ==0 	||
					(recordsCheckDeterminationList != null && recordsCheckDeterminationList.size() >0
						&& recordsCheckDeterminationList.get(0).getIdPerson() == recordsCheckDetailReq.getRecordsCheckDtoList().get(0).getIdPerson()) ||
					(recordsCheckDeterminationList != null && recordsCheckDeterminationList.size() >0
							&& !recordsCheckDeterminationList.get(0).getRecChkDeterm().equalsIgnoreCase(recordsCheckDetailReq.getRecordsCheckDtoList().get(0).getRecChkDeterm()))) {
				// Set Entity values
				RecChkDetermHistory recChkDetermHistory = new RecChkDetermHistory();
				recChkDetermHistory.setIdPerson(recordsCheckDetailReq.getRecordsCheckDtoList().get(0).getIdPerson());
				recChkDetermHistory.setIdRecCheck(recordsCheckDetailReq.getRecordsCheckDtoList().get(0).getIdRecCheck());
				recChkDetermHistory.setIndComplete(recordsCheckDetailReq.getRecordsCheckDtoList().get(0).getIndComplete());
				recChkDetermHistory
						.setCdRecCheckDeterm(recordsCheckDetailReq.getRecordsCheckDtoList().get(0).getRecChkDeterm());
				// Call DAM CAUDL5D
				if (recordsCheckDeterminationList.stream()
						.filter(recordsCheckDeterminationDto -> recordsCheckDeterminationDto.getRecChkDeterm()
								.equals(recordsCheckDetailReq.getRecordsCheckDtoList().get(0).getRecChkDeterm()))
						.findAny().isPresent()) {
					// Set IdRecChkDetermHistory to update existing record
					recChkDetermHistory
							.setIdRecChkDetermHistory(
									recordsCheckDeterminationList
											.stream().filter(
													recordsCheckDeterminationDto -> recordsCheckDeterminationDto
															.getRecChkDeterm()
															.equals(recordsCheckDetailReq.getRecordsCheckDtoList().get(0)
																	.getRecChkDeterm()))
											.findAny().get().getIdRecChkDetermHist());
				} else {
					// to add new recCheckDetermHistory record
					recChkDetermHistory.setIdRecChkDetermHistory(ServiceConstants.LongZero);
					recChkDetermHistory.setDtCreated(new Date());
					recChkDetermHistory.setDtLastUpdate(new Date());
					recordsCheckDetailReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
				}
				recordsCheckDao.rechDetermHistoryAUD(recChkDetermHistory, recordsCheckDetailReq.getReqFuncCd());
			}
		}
		return recordsCheckListRes;
	}

	/**
	 * Method Name: updateRecordsCheckStatus Method Description:This method is
	 * for updating status for corresponding idRecCheck
	 * 
	 * @param RecordsCheckStatusReq
	 * @return RecordsCheckRes
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public RecordsCheckRes updateRecordsCheckStatus(RecordsCheckStatusReq recordsCheckStatusReq) {
		recordsCheckDao.updateRecordsCheckStatus(recordsCheckStatusReq);
		RecordsCheckRes recordsCheckRes = new RecordsCheckRes();
		recordsCheckRes.setIdRecCheck(recordsCheckStatusReq.getIdRecCheck());
		return recordsCheckRes;
	}

	/**
	 * Method Name: generateAlerts Method Description: This method is used to
	 * generate alerts for Person when the results are returned for the DPS
	 * Criminal History Record Check.
	 * 
	 * @param RecordsCheckRes
	 * @return RecordsCheckStatusReq
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public RecordsCheckRes generateAlerts(RecordsCheckStatusReq recordsCheckStatusReq) {
		recordsCheckDao.generateAlerts(recordsCheckStatusReq);
		return new RecordsCheckRes();
	}

	/**
	 * Method Name: getServiceCode Method Description:Retrives Service code from
	 * stored procedure call for a giving Record Check id
	 * 
	 * @param RecordsCheckStatusReq
	 * @return RecordsCheckRes
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })

	public RecordsCheckRes getServiceCode(RecordsCheckStatusReq recordsCheckStatusReq){
		RecordsCheckRes RecordsCheckRes = recordsCheckDao.getServiceCode(recordsCheckStatusReq);
		return RecordsCheckRes;
	}
	/* Added for artf172946 */
	/**
	 * Method Name: updateRecordsCheck
	 * Method Description:This method will update
	 * the determination,status,dtDetermFinal and accptRej fields for the corresponding
	 * idRecCheck
	 *
	 * @param recordsCheckDetailReq
	 * @return RecordsCheckRes
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public RecordsCheckRes updateRecordsCheck(RecordsCheckDetailReq recordsCheckDetailReq) {
		recordsCheckDao.updateRecordsCheck(recordsCheckDetailReq);
		RecordsCheckRes recordsCheckRes = new RecordsCheckRes();
		recordsCheckRes.setIdRecCheck(recordsCheckDetailReq.getRecordsCheckDtoList().get(0).getIdRecCheck());
		return recordsCheckRes;
	}

	/**
	 *
	 * Method Name: recordsCheckDetermHistoryAD
	 * Method Description:This service will add and delete Records Check Determination History for a given IdRecCheck
	 *
	 * @param recordsCheckDetailReq
	 * @return RecordsCheckListRes
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RecordsCheckListRes recdsCheckDetermHistoryAD(RecordsCheckDetailReq recordsCheckDetailReq) {
		    RecordsCheckListRes recordsCheckListRes = new RecordsCheckListRes();
     		RecChkDetermHistory recChkDetermHistory = new RecChkDetermHistory();
			if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(recordsCheckDetailReq.getReqFuncCd())) {
				recChkDetermHistory.setIdRecChkDetermHistory(ServiceConstants.LongZero);
				recChkDetermHistory.setIdPerson(recordsCheckDetailReq.getRecordsCheckDtoList().get(0).getIdPerson());
				recChkDetermHistory.setIdRecCheck(recordsCheckDetailReq.getRecordsCheckDtoList().get(0).getIdRecCheck());
				recChkDetermHistory.setIndComplete(recordsCheckDetailReq.getRecordsCheckDtoList().get(0).getIndComplete());
				recChkDetermHistory.setCdRecCheckDeterm(recordsCheckDetailReq.getRecordsCheckDtoList().get(0).getRecChkDeterm());
				recChkDetermHistory.setDtCreated(new Date());
				recChkDetermHistory.setDtLastUpdate(new Date());
			}
			if (ServiceConstants.REQ_FUNC_CD_DELETE.equalsIgnoreCase(recordsCheckDetailReq.getReqFuncCd())) {
				recChkDetermHistory.setIdRecCheck(recordsCheckDetailReq.getRecordsCheckDtoList().get(0).getIdRecCheck());
			}
    		recordsCheckDao.rechDetermHistoryAUD(recChkDetermHistory, recordsCheckDetailReq.getReqFuncCd());
		    return recordsCheckListRes;
	}
	/**
	 *
	 * Method Name: getRecCheckDetermHistory
	 * Method Description:This service will get Records Check Determination History for a given IdRecCheck
	 *
	 * @param idRecCheck
	 * @return RecordsCheckListRes
	 */
	@Override
	public RecordsCheckListRes getRecCheckDetermHistory(Long idRecCheck){
		RecordsCheckListRes recordsCheckListRes= new RecordsCheckListRes();
		RecordsCheckDto recordsCheckDto = new RecordsCheckDto();
		List<RecordsCheckDto> recordCheckListDto = new ArrayList<RecordsCheckDto>();
		List<RecordsCheckDeterminationDto> recordsCheckDeterminationList =	recordsCheckDao.getRechDetermHistory(idRecCheck);
		recordsCheckDto.setRecordsCheckDetermination(recordsCheckDeterminationList);
		recordCheckListDto.add(recordsCheckDto);
		recordsCheckListRes.setRecordCheckListDto(recordCheckListDto);
		return recordsCheckListRes;
	}

	/* End of artf172946 */

	@Override
	public RecordsCheckRes hasOriginatingFPCheck(RecordsCheckReq request) {
		return recordsCheckDao.hasOriginatingFPCheck(request);
	}

	@Override
	public RecordsCheckRes getSidOriginalFingerprint(RecordsCheckReq request){
		return recordsCheckDao.getSidOriginalFingerprint(request);
	}

	@Override
	public RecordsCheckRes isABCSCheckRapBack(RecordsCheckReq request) {
		return recordsCheckDao.isABCSCheckRapBack(request);
	}

	@Override
	public RecordsCheckRes getNewHireCount(Long idRecCheck){
		return recordsCheckDao.getNewHireCount(idRecCheck);
	}

	@Override
	public RecordsCheckRes getCdDetermination(Long idRecCheck){
		return recordsCheckDao.getCdDetermination(idRecCheck);
	}
}
