package us.tx.state.dfps.service.person.serviceimpl;

import java.sql.SQLException;
import java.util.HashMap;

import javax.sql.rowset.serial.SerialException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import us.tx.state.dfps.service.common.request.CriminalHistoryReq;
import us.tx.state.dfps.service.common.request.CriminalHistoryUpdateReq;
import us.tx.state.dfps.service.common.response.CrimHistoryRes;
import us.tx.state.dfps.service.common.response.CriminalHistoryRes;
import us.tx.state.dfps.service.person.dao.CriminalHistoryDao;
import us.tx.state.dfps.service.person.dto.CriminalHistoryValueBean;
import us.tx.state.dfps.service.person.service.CriminalHistoryService;

/**
 *
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCFC31S Class
 * Description: This class is doing service Implementation for CriminalHistory
 * Mar 24, 2017 - 4:23:07 PM
 */
@Service
@Transactional
public class CriminalHistoryServiceImpl implements CriminalHistoryService {

	@Autowired
	CriminalHistoryDao criminalHistoryDao;

	private static final Logger log = Logger.getLogger(PersonDtlServiceImpl.class);

	public CriminalHistoryServiceImpl() {
	}

	/**
	 * Method Name: saveCriminalHistoryNarrative
	 *
	 * @param CriminalHistoryValueBean
	 * @return CriminalHistoryRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public CriminalHistoryRes saveCriminalHistoryNarrative(CriminalHistoryValueBean criminalHistoryValueBean,
														   String result){
		return criminalHistoryDao.saveCriminalHistoryAndNarrative(criminalHistoryValueBean, result);
	}

	/**
	 * Method Name: updateCriminalHistoryRec Method Description: This service
	 * will Update rows in the Criminal History Table for a given IdCrimHist.
	 * Also, this service will delete rows from Crim Hist Narr Table for a given
	 * IdCrimHist when the XIndDeleteNarr flag is set for that IdCrimHist.
	 * Service Name: CCFC32S
	 *
	 * @param CriminalHistoryUpdateReq
	 * @return CriminalHistoryRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public CriminalHistoryRes updateCriminalHistoryRec(CriminalHistoryUpdateReq criminalHistoryUpdateReq) {
		CriminalHistoryRes criminalHistoryRes = new CriminalHistoryRes();
		criminalHistoryDao.criminalHistoryRecAUD(criminalHistoryUpdateReq);
		return criminalHistoryRes;
	}

	/**
	 *
	 * Method Description: This service will retrieve all rows from the Criminal
	 * History Table for a given ID_REC_CHECK. Service Name: CCFC31S
	 *
	 * @param CriminalHistoryReq
	 * @return CrimHistoryRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public CrimHistoryRes getCriminalHistoryList(@RequestBody CriminalHistoryReq criminalHistoryReq) {
		CrimHistoryRes crimHistoryRes = criminalHistoryDao.getCriminalHistoryList(criminalHistoryReq);
		return crimHistoryRes;
	}

	/**
	 * Method Name: checkCrimHistAction Method Description: This method to get
	 * the idPerson if the Criminal History Action is null for the given
	 * Id_Stage.
	 *
	 * @param idStage
	 * @return HashMap
	 */
	@SuppressWarnings("rawtypes")
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public HashMap checkCrimHistAction(long idStage) {
		return criminalHistoryDao.checkCrimHistAction(idStage);
	}

	/**
	 * Method Name: isCrimHistPending Method Description: To check if any DPS
	 * Criminal History check is pending.
	 *
	 * @param idStage
	 * @return Boolean
	 */
	public Boolean isCrimHistPending(Long idStage) {
		return criminalHistoryDao.isCrimHistPending(idStage);

	}

	/**
	 *
	 * Method Description:This Method will retrieve information for the criminal
	 * History window. Tuxedo Service Name:isCrimHistNarrPresentForCriminalHistortRecord
	 *
	 *
	 * @param crimHistoryReq
	 * @return
	 */
    @Override
    public boolean isCrimHistNarrPresentForRecordCheck(CriminalHistoryReq crimHistoryReq) {
		boolean criminalHistory;
		criminalHistory = criminalHistoryDao.isCrimHistNarrPresentForRecordCheck(crimHistoryReq);
		log.info("TransactionId :" + crimHistoryReq.getTransactionId());
		return criminalHistory;
    }

}
