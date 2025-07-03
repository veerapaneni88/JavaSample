package us.tx.state.dfps.service.person.serviceimpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.TletsCheck;
import us.tx.state.dfps.service.common.request.TletsReq;
import us.tx.state.dfps.service.common.response.TletsRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.person.dao.TletsDao;
import us.tx.state.dfps.service.person.dto.TletsDto;
import us.tx.state.dfps.service.person.service.TletsService;

@Service
@Transactional
public class TletsServiceImpl implements TletsService {

	@Autowired
	TletsDao tletsDao;

	/**
	 * Method Description: This method is used to call TletsDao method to
	 * retrieve information for populating Texas Law Enforcement
	 * Telecommunications System (TLETS) List window.
	 * 
	 * @param TletsReq
	 * @ @return tletsOut
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public TletsRes getTletsList(TletsReq tletsReq) {

		TletsRes tletsOut = new TletsRes();
		List<TletsDto> tletsList = new ArrayList<>();
		tletsList = tletsDao.getTletsList(tletsReq);
		tletsOut.setTletsDtlList(tletsList);
		return tletsOut;
	}

	/**
	 * Method Description: This method is used to call TletsDao method to
	 * retrieve information for populating Texas Law Enforcement
	 * Telecommunications System (TLETS) Check window.
	 * 
	 * @param TletsReq
	 * @ @return tletsOut
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public TletsRes getTletsCheckDtl(TletsReq tletsReq) {

		TletsRes tletsOut = new TletsRes();
		TletsDto tletsDtl = new TletsDto();

		Long tletsId = tletsReq.getTletsCheckId();
		tletsDtl = tletsDao.getTletsCheckDtl(tletsId);

		tletsOut.setTletsDtl(tletsDtl);

		return tletsOut;
	}

	/**
	 * Method Description: This method is used to call TletsDao method to
	 * Retrieve, Add and Update on populating Texas Law Enforcement
	 * Telecommunications System (TLETS) Check window. Service Name : TLETS
	 * Check
	 * 
	 * @param TletsReq
	 * @ @return tletsOut
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public TletsRes audTletsDetails(TletsReq tletsReq) {

		TletsRes tletsRes = new TletsRes();
		TletsDto tletsDtl = new TletsDto();

		Date sysDate = new Date();
		TletsCheck tletsCheck = new TletsCheck();

		if (!TypeConvUtil.isNullOrEmpty(tletsReq)) {

			if (!TypeConvUtil.isNullOrEmpty(tletsReq.getTletsCheckId()))
				tletsCheck.setIdTletsCheck(tletsReq.getTletsCheckId());

			if (!TypeConvUtil.isNullOrEmpty(tletsReq.getTletslastUpdatedPersonId()))
				tletsCheck.setIdLastUpdatePerson(tletsReq.getTletslastUpdatedPersonId());

			if (!TypeConvUtil.isNullOrEmpty(tletsReq.getTletsCreatedPersonId()))
				tletsCheck.setIdCreatedPerson(tletsReq.getTletsCreatedPersonId());

			Stage stage = new Stage();
			stage.setIdStage(tletsReq.getStageId());
			if (!TypeConvUtil.isNullOrEmpty(tletsReq.getStageId()))
				tletsCheck.setStage(stage);

			CapsCase capsCase = new CapsCase();
			capsCase.setIdCase(tletsReq.getCaseId());
			if (!TypeConvUtil.isNullOrEmpty(tletsReq.getCaseId()))
				tletsCheck.setCapsCase(capsCase);

			if (!TypeConvUtil.isNullOrEmpty(tletsReq.getCdTlets()))
				tletsCheck.setCdTlets(tletsReq.getCdTlets());

			if (!TypeConvUtil.isNullOrEmpty(tletsReq.getTletsCreatedDate())) {
				tletsCheck.setDtCreated(tletsReq.getTletsCreatedDate());
			} else {
				tletsCheck.setDtCreated(sysDate);
			}

			if (!TypeConvUtil.isNullOrEmpty(tletsReq.getTletsConductedDate())) {

				Date conductedDate = tletsReq.getTletsConductedDate();
				Calendar cal = Calendar.getInstance();
				cal.setTime(conductedDate);
				// cal.add(Calendar.DATE, 1);
				conductedDate = cal.getTime();
				tletsCheck.setDtConducted(conductedDate);
			} else {
				tletsCheck.setDtConducted(sysDate);
			}

			tletsCheck.setDtLastUpdate(sysDate);

			if (!TypeConvUtil.isNullOrEmpty(tletsReq.getIndTletsInvalid()))
				tletsCheck.setIndInvalid(tletsReq.getIndTletsInvalid());

			if (!TypeConvUtil.isNullOrEmpty(tletsReq.getDtTletsInvalid()))
				tletsCheck.setDtInvalid(tletsReq.getDtTletsInvalid());

			Person person = new Person();
			person.setIdPerson(tletsReq.getIdPerson());
			if (!TypeConvUtil.isNullOrEmpty(tletsReq.getIdPerson()))
				tletsCheck.setPerson(person);

			tletsDtl = tletsDao.saveTlets(tletsCheck, tletsReq.getcReqFuncCd());

			tletsRes.setTletsDtl(tletsDtl);

		}

		return tletsRes;
	}

}