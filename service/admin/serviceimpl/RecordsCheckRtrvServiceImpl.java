package us.tx.state.dfps.service.admin.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.service.admin.dao.CriminalHistoryRecsCheckCountDao;
import us.tx.state.dfps.service.admin.dao.NamePersonDao;
import us.tx.state.dfps.service.admin.dao.PersonPortfolioDao;
import us.tx.state.dfps.service.admin.dao.PersonRecChkDetermHistoryDao;
import us.tx.state.dfps.service.admin.dao.RecordsCheckPersonDao;
import us.tx.state.dfps.service.admin.dto.CriminalHistoryRecsCheckCountInDto;
import us.tx.state.dfps.service.admin.dto.CriminalHistoryRecsCheckCountOutDto;
import us.tx.state.dfps.service.admin.dto.NamePersonInDto;
import us.tx.state.dfps.service.admin.dto.NamePersonOutDto;
import us.tx.state.dfps.service.admin.dto.PersonPortfolioInDto;
import us.tx.state.dfps.service.admin.dto.PersonPortfolioOutDto;
import us.tx.state.dfps.service.admin.dto.PersonRecChkDetermHistoryInDto;
import us.tx.state.dfps.service.admin.dto.PersonRecChkDetermHistoryOutDto;
import us.tx.state.dfps.service.admin.dto.RecordsCheckPersonInDto;
import us.tx.state.dfps.service.admin.dto.RecordsCheckPersonOutDto;
import us.tx.state.dfps.service.admin.dto.RecordsCheckRtrviDto;
import us.tx.state.dfps.service.admin.service.RecordsCheckRtrvService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.RecordsCheckListRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.person.dto.RecordsCheckDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This service
 * will retrieve all rows from the Records Check Table for a given
 * IdRecCheckPerson(maximum page size retrieved is 11 rows). Aug 7, 2017-
 * 2:32:38 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class RecordsCheckRtrvServiceImpl implements RecordsCheckRtrvService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	RecordsCheckPersonDao recordsCheckRetrvDao;

	@Autowired
	PersonPortfolioDao personDetailsDao;

	@Autowired
	NamePersonDao personNameDao;

	@Autowired
	PersonRecChkDetermHistoryDao recCheckDeterHistDao;

	@Autowired
	CriminalHistoryRecsCheckCountDao recChckCriminalHistoryDao;

	private static final Logger log = Logger.getLogger("ServiceBusiness-RecordsCheckRtrvService");

	/**
	 * 
	 * Method Name: callRecordsCheckRtrvService Method Description:This service
	 * will retrieve all rows from the Records Check Table for a given
	 * IdRecCheckPerson(maximum page size retrieved is 11 rows).
	 * 
	 * @param pInputMsg
	 * @return RecordsCheckRtrvRes
	 * 
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RecordsCheckListRes callRecordsCheckRtrvService(RecordsCheckRtrviDto pInputMsg) {
		log.debug("Entering method callRecordsCheckRtrvService in RecordsCheckRtrvServiceImpl");
		RecordsCheckListRes recordsCheckRtrvResult = new RecordsCheckListRes();
		List<RecordsCheckDto> lipOutputMsg = new ArrayList<>();
		RecordsCheckPersonInDto idInputRecChkPersonDto = new RecordsCheckPersonInDto();
		RecordsCheckPersonOutDto recCheckOutRowsDto = new RecordsCheckPersonOutDto();
		PersonPortfolioInDto recPersonIDDto = new PersonPortfolioInDto();
		NamePersonInDto recInputPersonIDDto = new NamePersonInDto();
		NamePersonOutDto recOutPersonNameDto = new NamePersonOutDto();
		PersonRecChkDetermHistoryOutDto recRowsOutDto = new PersonRecChkDetermHistoryOutDto();
		List<RecordsCheckPersonOutDto> lirecCheckOutRowsDto = new ArrayList<RecordsCheckPersonOutDto>();
		if (null != idInputRecChkPersonDto) {
		}
		if (null != recCheckOutRowsDto) {
		}
		idInputRecChkPersonDto.setIdRecCheckPerson(pInputMsg.getIdRecCheckPerson());
		idInputRecChkPersonDto.setPageNbr(pInputMsg.getPageNbr());
		idInputRecChkPersonDto.setPageSizeNbr(pInputMsg.getPageSizeNbr());
		lirecCheckOutRowsDto = recordsCheckRetrvDao.recCheckDtls(idInputRecChkPersonDto, recCheckOutRowsDto);
		if (!ObjectUtils.isEmpty(lirecCheckOutRowsDto)) {
			for (RecordsCheckPersonOutDto recordsCheckPersonOutDto : lirecCheckOutRowsDto) {
				RecordsCheckDto recordsCheckDto = new RecordsCheckDto();
				recordsCheckDto.setRecCheckDpsIncomplete(ServiceConstants.STRING_IND_N);
				// pOutputMsg.setDtWCDDtSystemDate(ServiceConstants.NULL_VALDAT);
				recordsCheckDto.setCdCheckType(recordsCheckPersonOutDto.getCdRecCheckCheckType());
				recordsCheckDto.setRecCheckCheckType(recordsCheckPersonOutDto.getCdRecCheckCheckType());
				recordsCheckDto.setRecCheckEmpType(recordsCheckPersonOutDto.getCdRecCheckEmpType());
				recordsCheckDto.setRecCheckStatus(recordsCheckPersonOutDto.getCdRecCheckStatus());
				recordsCheckDto.setRecCheckComments(recordsCheckPersonOutDto.getTxtRecCheckComments());
				recordsCheckDto.setNmRequestedBy(recordsCheckPersonOutDto.getNmPersonFull());
				recordsCheckDto.setIdRecCheck(recordsCheckPersonOutDto.getIdRecCheck());
				recordsCheckDto.setIdRecCheckRequestor(recordsCheckPersonOutDto.getIdRecCheckRequestor());
				recordsCheckDto.setIdStage(!StringUtils.isEmpty(recordsCheckPersonOutDto.getIdStage())
						? recordsCheckPersonOutDto.getIdStage() : ServiceConstants.ZERO_VAL);
				recordsCheckDto.setDtClrdEmailRequested(recordsCheckPersonOutDto.getDtClrdEmailRequested());
				recordsCheckDto.setDtRecCheckRequest(recordsCheckPersonOutDto.getDtRecCheckRequest());
				recordsCheckDto.setDtRecCheckCompleted(recordsCheckPersonOutDto.getDtRecCheckCompleted());
				// recordsCheckDto.setDtLastUpdateStr(recordsCheckPersonOutDto.getTsLastUpdate());
				recordsCheckDto.setDtLastUpdate(recordsCheckPersonOutDto.getTsLastUpdate());
				recordsCheckDto.setDtDetermFinal(recordsCheckPersonOutDto.getDtDetermFinal());
				recordsCheckDto.setRecChkDeterm(recordsCheckPersonOutDto.getCdRecChkDeterm());
				if ((!ServiceConstants.DPS_CODE.equalsIgnoreCase(recordsCheckDto.getCdCheckType()))
						&& (TypeConvUtil.isNullOrEmpty(recordsCheckDto.getDtRecCheckCompleted()))
						&& (ServiceConstants.DPS_STAT_OVERDUE.equalsIgnoreCase(recordsCheckDto.getRecCheckStatus()))
						&& (ServiceConstants.DPS_STAT_DPS_REJECT.equalsIgnoreCase(recordsCheckDto.getRecCheckStatus()))
						&& (ServiceConstants.DPS_STAT_PRS_REJECT.equalsIgnoreCase(recordsCheckDto.getRecCheckStatus()))
						&& (recordsCheckDto.getRecCheckDpsIncomplete() != ServiceConstants.STRING_IND_Y)) {
					recordsCheckDto.setRecCheckDpsIncomplete(ServiceConstants.STRING_IND_Y);
				}
				recordsCheckDto.setIndClearedEmail(recordsCheckPersonOutDto.getIndClearedEmail());
				recordsCheckDto.setIndAccptRej(recordsCheckPersonOutDto.getIndAccptRej());
				recordsCheckDto.setIndReviewNow(recordsCheckPersonOutDto.getIndReviewNow());
				List<PersonRecChkDetermHistoryOutDto> personRecChkDetermHistoryOutDtoList = new ArrayList<PersonRecChkDetermHistoryOutDto>();
				personRecChkDetermHistoryOutDtoList = retrieveAllRecordChecks(pInputMsg, recRowsOutDto,
						recordsCheckDto.getIdRecCheck());
				if (personRecChkDetermHistoryOutDtoList.size() > 0) {
					for (PersonRecChkDetermHistoryOutDto personRecChkDetermHistoryOutDto : personRecChkDetermHistoryOutDtoList) {
						recordsCheckDto.setRowQty(new Long(personRecChkDetermHistoryOutDto.getRowQty()));
						recordsCheckDto.setIdRecCheck(personRecChkDetermHistoryOutDto.getIdRecCheck());
						// pOutputMsg.setIdRecChkDetermHist(personRecChkDetermHistoryOutDto.getIdRecChkDetermHist());
						recordsCheckDto.setRecChkDeterm(personRecChkDetermHistoryOutDto.getCdRecChkDeterm());
						recordsCheckDto.setPersonFullName(personRecChkDetermHistoryOutDto.getNmPersonFull());
						// recordsCheckDto.setDtRecCheckRequest(personRecChkDetermHistoryOutDto.getDtRecChkDeterminCreated());
						recordsCheckDto.setCompletedCkboxChkd(
								ServiceConstants.Y.equals(personRecChkDetermHistoryOutDto.getIndComplete()) ? true
										: false);
					}
				}
				recPersonIDDto.setIdPerson(recordsCheckPersonOutDto.getIdRecCheckRequestor());
				List<PersonPortfolioOutDto> liCcmn44doDto = new ArrayList<PersonPortfolioOutDto>();
				liCcmn44doDto = personDetailsDao.getPersonRecord(recPersonIDDto);
				if (liCcmn44doDto.size() > 0) {
					for (PersonPortfolioOutDto personPortfolioOutDto : liCcmn44doDto) {
						recordsCheckDto.setNmRequestedBy(personPortfolioOutDto.getNmPersonFull());
						recordsCheckDto.setEthnicity(personPortfolioOutDto.getCdPersonEthnicGroup());
						recordsCheckDto.setPersonFullName(personPortfolioOutDto.getNmPersonFull());
						recordsCheckDto.setPersonFirstName(personPortfolioOutDto.getNmPersonFirst());
						recordsCheckDto.setPersonLastName(personPortfolioOutDto.getNmPersonLast());
						if (StringUtils.isEmpty(personPortfolioOutDto.getPersonAge())) {
							recordsCheckDto.setPersonAge(ServiceConstants.Zero);
						} else {
							recordsCheckDto.setPersonAge(personPortfolioOutDto.getPersonAge());
						}
						recordsCheckDto.setIndPersonDobApprx(personPortfolioOutDto.getIndPersonDobApprox());
						recordsCheckDto.setPersonSex(personPortfolioOutDto.getPersonSex());
						if (ObjectUtils.isEmpty(recInputPersonIDDto) || ObjectUtils.isEmpty(recOutPersonNameDto)) {
						}
						recInputPersonIDDto.setIdPerson(pInputMsg.getIdRecCheckPerson());
						List<NamePersonOutDto> liCsec35doDto = new ArrayList<NamePersonOutDto>();
						liCsec35doDto = personNameDao.prsnDtls(recInputPersonIDDto, recOutPersonNameDto);
						{
							for (NamePersonOutDto csec35doDto : liCsec35doDto) {
								recordsCheckDto.setPersonFirstName(csec35doDto.getNmNameFirst());
								recordsCheckDto.setPersonLastName(csec35doDto.getNmNameLast());
							}
						}
					}
				}
				lipOutputMsg.add(recordsCheckDto);
			}
		}
		log.debug("Exiting method callRecordsCheckRtrvService in RecordsCheckRtrvServiceImpl");
		recordsCheckRtrvResult.setRecordCheckListDto(lipOutputMsg);
		return recordsCheckRtrvResult;
	}

	/**
	 * 
	 * Method Name: retrieveAllRecordChecks Method Description:This service will
	 * retrieve all rows from the Records Check Table for a given
	 * IdRecCheckPerson(maximum page size retrieved is 11 rows). Equivallent to
	 * CallCLSSB7D of Legacy
	 * 
	 * @param pInputMsg
	 * @param recRowsOutDto
	 * @param ulIdRecCheck
	 * @return List<PersonRecChkDetermHistoryOutDto>
	 * 
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PersonRecChkDetermHistoryOutDto> retrieveAllRecordChecks(RecordsCheckRtrviDto pInputMsg,
			PersonRecChkDetermHistoryOutDto recRowsOutDto, long ulIdRecCheck) {
		log.debug("Entering method getRecCheckHistory in RecordsCheckRtrvServiceImpl");
		List<PersonRecChkDetermHistoryOutDto> liClssb7doDto = new ArrayList<PersonRecChkDetermHistoryOutDto>();
		PersonRecChkDetermHistoryInDto pCLSSB7DInputRec = new PersonRecChkDetermHistoryInDto();
		pCLSSB7DInputRec.setIdRecCheck(ulIdRecCheck);
		liClssb7doDto = recCheckDeterHistDao.getRecordCheckDeterminationDtls(pCLSSB7DInputRec);
		log.debug("Exiting method getRecCheckHistory in RecordsCheckRtrvServiceImpl");
		return liClssb7doDto;
	}

	/**
	 * 
	 * Method Name: hasCHActions Method Description:
	 * 
	 * @param ulIdRecCheck
	 * 
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void hasCHActions(long ulIdRecCheck) {
		log.debug("Entering method hasCHActions in RecordsCheckRtrvServiceImpl");
		CriminalHistoryRecsCheckCountInDto pCINVF1DInputRec = new CriminalHistoryRecsCheckCountInDto();
		CriminalHistoryRecsCheckCountOutDto pCINVF1DOutputRec = new CriminalHistoryRecsCheckCountOutDto();
		pCINVF1DInputRec.setUlIdRecCheck(ulIdRecCheck);
		List<CriminalHistoryRecsCheckCountOutDto> liCinvf1doDto = recChckCriminalHistoryDao
				.rtrvCriminalHistoryRecords(pCINVF1DInputRec, pCINVF1DOutputRec);
		log.debug("Exiting method hasCHActions in RecordsCheckRtrvServiceImpl");
	}
}
