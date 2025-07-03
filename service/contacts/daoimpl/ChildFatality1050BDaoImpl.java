package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CodeTableRow;
import us.tx.state.dfps.common.domain.FtRlsInfoRpt;
import us.tx.state.dfps.common.domain.FtRlsInfoRptAllegDisp;
import us.tx.state.dfps.common.domain.FtRlsInfoRptCpa;
import us.tx.state.dfps.common.domain.FtRlsInfoRptCps;
import us.tx.state.dfps.common.domain.FtRlsInfoRptRsrc;
import us.tx.state.dfps.common.domain.FtRlsInfoRptRsrcVoltns;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptAllegDispValueBean;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptAllegDispValueDto;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptCPAValueDto;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptCPAValueModBean;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptCPSValueDto;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptCPSValueModBean;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptResourceValueBean;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptResourceValueDto;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptRsrcVoltnsValueBean;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptRsrcVoltnsValueDto;
import us.tx.state.dfps.service.contact.dto.CFTRlsInfoRptValueDto;
import us.tx.state.dfps.service.contact.dto.CFTSafetyAssessmentInfoDto;
import us.tx.state.dfps.service.contacts.dao.ChildFatality1050BDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business - IMPACT PHASE 2 MODERNIZATION Class Description: this class
 * is used for implementing all the child fatality related DB transaction Jul
 * 31, 2017- 1:04:37 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class ChildFatality1050BDaoImpl implements ChildFatality1050BDao {

	@Autowired
	SessionFactory sessionFactory;
	@Autowired
	MessageSource messageSource;
	
	private static final String SDMSA = "SDMSA";
	private static final String SDMRAFRL = "SDMRAFRL";

	/**
	 * Method Name: insertRlsInfoRptCPS Method Description: This method is used
	 * to inser the record in Info report CPS table
	 * 
	 * @param cftRlsInfoRptCPSValueDto
	 * @return CFTRlsInfoRptCPSValueModBean
	 */
	public CFTRlsInfoRptCPSValueModBean insertRlsInfoRptCPS(CFTRlsInfoRptCPSValueModBean cftRlsInfoRptCPSValueDto) {
		// getting the entity object to make and setting the values to make
		// insert the record into the table
		FtRlsInfoRptCps ftRlsInfoRptCps = new FtRlsInfoRptCps();
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getIdCreatedPerson()))
			ftRlsInfoRptCps.setIdLastUpdatePerson(cftRlsInfoRptCPSValueDto.getIdCreatedPerson());
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getIdLastUpdatePerson()))
			ftRlsInfoRptCps.setIdLastUpdatePerson(cftRlsInfoRptCPSValueDto.getIdLastUpdatePerson());
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getDtCreated()))
			ftRlsInfoRptCps.setDtCreated(cftRlsInfoRptCPSValueDto.getDtCreated());
		ftRlsInfoRptCps.setDtCreated(new Date());
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getDtLastUpdate()))
			ftRlsInfoRptCps.setDtLastUpdate(cftRlsInfoRptCPSValueDto.getDtLastUpdate());

		ftRlsInfoRptCps.setDtLastUpdate(new Date());
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getIdCreatedPerson()))
			ftRlsInfoRptCps.setIdCreatedPerson(cftRlsInfoRptCPSValueDto.getIdCreatedPerson());
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getIdFtRlsInfoRpt()))
			ftRlsInfoRptCps.setIdFtRlsInfoRpt(cftRlsInfoRptCPSValueDto.getIdFtRlsInfoRpt());
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getCdHistoryType()))
			ftRlsInfoRptCps.setCdHistoryType(cftRlsInfoRptCPSValueDto.getCdHistoryType());
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getIdPerson()))
			ftRlsInfoRptCps.setIdPerson(cftRlsInfoRptCPSValueDto.getIdPerson());
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getNmPersonFull()))
			ftRlsInfoRptCps.setNmPersonFull(cftRlsInfoRptCPSValueDto.getNmPersonFull());
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getIdCase()))
			ftRlsInfoRptCps.setIdCase(cftRlsInfoRptCPSValueDto.getIdCase());
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getIdStage()))
			ftRlsInfoRptCps.setIdStage(cftRlsInfoRptCPSValueDto.getIdStage());
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getDtIntake()))
			ftRlsInfoRptCps.setTxtDtIntake(cftRlsInfoRptCPSValueDto.getDtIntake());
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getAllegations()))
			ftRlsInfoRptCps.setTxtAllegations(cftRlsInfoRptCPSValueDto.getAllegations());
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getAllegDisposition()))
			ftRlsInfoRptCps.setTxtAllegDisposition(cftRlsInfoRptCPSValueDto.getAllegDisposition());
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getSafetyRiskAssmnt()))
			ftRlsInfoRptCps.setTxtSafetyRiskAssmnt(cftRlsInfoRptCPSValueDto.getSafetyRiskAssmnt());
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getCaseAction()))
			ftRlsInfoRptCps.setTxtCaseAction(cftRlsInfoRptCPSValueDto.getCaseAction());
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getServicesReferrals()))
			ftRlsInfoRptCps.setTxtServicesReferrals(cftRlsInfoRptCPSValueDto.getServicesReferrals());
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getServicesOther()))
			ftRlsInfoRptCps.setTxtServicesOther(cftRlsInfoRptCPSValueDto.getServicesOther());
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getIndNoServicesReferrals()))
			ftRlsInfoRptCps.setIndNoServicesReferrals(cftRlsInfoRptCPSValueDto.getIndNoServicesReferrals().charAt(0));
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getOthrActn()))
			ftRlsInfoRptCps.setTxtOthrActn(cftRlsInfoRptCPSValueDto.getOthrActn());
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getDtCvs()))
			ftRlsInfoRptCps.setTxtDtCvs(cftRlsInfoRptCPSValueDto.getDtCvs());
		if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getRiskAssmntMsg()))
			ftRlsInfoRptCps.setTxtRiskAssmntMsg(cftRlsInfoRptCPSValueDto.getRiskAssmntMsg());
		// calling the save method using hibernate to insert the record into the
		// table
		sessionFactory.getCurrentSession().save(ftRlsInfoRptCps);
		return cftRlsInfoRptCPSValueDto;
	}

	@Override
	public CFTRlsInfoRptCPSValueModBean updateRlsInfoRptCPS(CFTRlsInfoRptCPSValueModBean cftRlsInfoRptCPSValueDto) {
		FtRlsInfoRptCps ftRlsInfoRptCps = (FtRlsInfoRptCps) sessionFactory.getCurrentSession()
				.get(FtRlsInfoRptCps.class, (long) cftRlsInfoRptCPSValueDto.getIdFtRlsInfoRptCps());

		if (ftRlsInfoRptCps != null) {
			if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getIdLastUpdatePerson()))
				ftRlsInfoRptCps.setIdLastUpdatePerson(cftRlsInfoRptCPSValueDto.getIdLastUpdatePerson());
			if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getDtIntake()))
				ftRlsInfoRptCps.setTxtDtIntake(cftRlsInfoRptCPSValueDto.getDtIntake());
			if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getAllegations()))
				ftRlsInfoRptCps.setTxtAllegations(cftRlsInfoRptCPSValueDto.getAllegations());
			if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getAllegDisposition()))
				ftRlsInfoRptCps.setTxtAllegDisposition(cftRlsInfoRptCPSValueDto.getAllegDisposition());
			if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getSafetyRiskAssmnt()))
				ftRlsInfoRptCps.setTxtSafetyRiskAssmnt(cftRlsInfoRptCPSValueDto.getSafetyRiskAssmnt());
			if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getCaseAction()))
				ftRlsInfoRptCps.setTxtCaseAction(cftRlsInfoRptCPSValueDto.getCaseAction());
			if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getServicesReferrals()))
				ftRlsInfoRptCps.setTxtServicesReferrals(cftRlsInfoRptCPSValueDto.getServicesReferrals());
			if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getServicesOther()))
				ftRlsInfoRptCps.setTxtServicesOther(cftRlsInfoRptCPSValueDto.getServicesOther());
			if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getIndNoServicesReferrals()))
				ftRlsInfoRptCps
						.setIndNoServicesReferrals(cftRlsInfoRptCPSValueDto.getIndNoServicesReferrals().charAt(0));
			if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getOthrActn()))
				ftRlsInfoRptCps.setTxtOthrActn(cftRlsInfoRptCPSValueDto.getOthrActn());

			if (ServiceConstants.CHISTTYP_HO.equals(ftRlsInfoRptCps.getCdHistoryType())) {
				if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getIdCase()))
					ftRlsInfoRptCps.setIdCase(cftRlsInfoRptCPSValueDto.getIdCase());
				if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getIdStage()))
					ftRlsInfoRptCps.setIdStage(cftRlsInfoRptCPSValueDto.getIdStage());
				if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getIdPerson()))
					ftRlsInfoRptCps.setIdPerson(cftRlsInfoRptCPSValueDto.getIdPerson());
				if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getNmPersonFull()))
					ftRlsInfoRptCps.setNmPersonFull(cftRlsInfoRptCPSValueDto.getNmPersonFull());
			}
			if (!ObjectUtils.isEmpty(cftRlsInfoRptCPSValueDto.getDtCvs()))
				ftRlsInfoRptCps.setTxtDtCvs(cftRlsInfoRptCPSValueDto.getDtCvs());
			sessionFactory.getCurrentSession().saveOrUpdate(ftRlsInfoRptCps);
		} else {
			return insertRlsInfoRptCPS(cftRlsInfoRptCPSValueDto);
		}
		return cftRlsInfoRptCPSValueDto;
	}

	@Override
	public long deleteRlsInfoRptCPS(long idFtRlsInfoRptCPS) {
		FtRlsInfoRptCps ftRlsInfoRptCps = (FtRlsInfoRptCps) sessionFactory.getCurrentSession()
				.get(FtRlsInfoRptCps.class, idFtRlsInfoRptCPS);
		sessionFactory.getCurrentSession().delete(ftRlsInfoRptCps);
		return idFtRlsInfoRptCPS;
	}

	@Override
	public long deleteRlsInfoCPA(long idFtInfoCpaOrAlleg) {
		FtRlsInfoRptCpa ftRlsInfoRptCpa = (FtRlsInfoRptCpa) sessionFactory.getCurrentSession()
				.get(FtRlsInfoRptCpa.class, idFtInfoCpaOrAlleg);
		long result = ServiceConstants.ZERO_VAL;
		if (ftRlsInfoRptCpa != null) {
			result = ServiceConstants.ONE_VAL;
			sessionFactory.getCurrentSession().delete(ftRlsInfoRptCpa);
		}

		return result;
	}

	@Override
	public long deleteFtInfoAllegDisp(long idFtInfoCpaOrAlleg) {
		FtRlsInfoRptAllegDisp ftRlsInfoRptAllegDisp = (FtRlsInfoRptAllegDisp) sessionFactory.getCurrentSession()
				.get(FtRlsInfoRptAllegDisp.class, idFtInfoCpaOrAlleg);
		long result = ServiceConstants.ZERO_VAL;
		if (ftRlsInfoRptAllegDisp != null) {
			result = ServiceConstants.ONE_VAL;
		}
		sessionFactory.getCurrentSession().delete(ftRlsInfoRptAllegDisp);
		return result;
	}

	/**
	 * Method Name: insertCFTRlsInfoRpt Method Description: This method
	 * inserting the record in to Info report table
	 * 
	 * @param cftRlsInfoRpt
	 * @return Long
	 */
	@Override
	public long insertCFTRlsInfoRpt(CFTRlsInfoRptValueDto cftRlsInfoRpt) {
		// creating the object of entity for making a call to insert the record
		// into table
		FtRlsInfoRpt ftRlsInfoRpt = new FtRlsInfoRpt();
		ftRlsInfoRpt.setDtLastUpdate(new Date());
		ftRlsInfoRpt.setIdLastUpdatePerson(cftRlsInfoRpt.getIdLastUpdatePerson());
		ftRlsInfoRpt.setDtCreated(new Date());
		ftRlsInfoRpt.setIdCreatedPerson(cftRlsInfoRpt.getIdCreatedPerson());
		ftRlsInfoRpt.setIdEvent((long) cftRlsInfoRpt.getIdEvent());
		ftRlsInfoRpt.setIdPerson((long) cftRlsInfoRpt.getIdPerson());
		ftRlsInfoRpt.setNmPersonFull(cftRlsInfoRpt.getNmPersonFull());
		ftRlsInfoRpt.setCdReport(cftRlsInfoRpt.getCdReport());
		// calling the save method using hibernate to insert the record into the
		// table
		long idFtRlsInfoRpt = (long) sessionFactory.getCurrentSession().save(ftRlsInfoRpt);
		return idFtRlsInfoRpt;

	}

	@Override
	public long insertRlsInfoAllegDisposition(CFTRlsInfoRptAllegDispValueDto cftRlsInfoRptAllegDispValueDto) {
		FtRlsInfoRptAllegDisp ftRlsInfoRptAllegDisp = new FtRlsInfoRptAllegDisp();

		ftRlsInfoRptAllegDisp.setIdFtRlsInfoRpt((long) cftRlsInfoRptAllegDispValueDto.getIdFtRlsInfoRpt());
		ftRlsInfoRptAllegDisp.setDtInvStart(cftRlsInfoRptAllegDispValueDto.getDtInvStart());

		ftRlsInfoRptAllegDisp.setTxtAllegType(cftRlsInfoRptAllegDispValueDto.getTxtAllegType());
		ftRlsInfoRptAllegDisp.setTxtAllegDisposition(cftRlsInfoRptAllegDispValueDto.getTxtAllegDisposition());
		if (!ObjectUtils.isEmpty(cftRlsInfoRptAllegDispValueDto.getIndPendingAppeal())) {
			ftRlsInfoRptAllegDisp.setIndPendingAppeal(cftRlsInfoRptAllegDispValueDto.getIndPendingAppeal().charAt(0));
		}
		if (!ObjectUtils.isEmpty(cftRlsInfoRptAllegDispValueDto.getIndDeceasedAllegedVictim())) {
			ftRlsInfoRptAllegDisp.setIndDeceasedAllegedVictim(
					cftRlsInfoRptAllegDispValueDto.getIndDeceasedAllegedVictim().charAt(0));
		}
		ftRlsInfoRptAllegDisp.setDtCreated(new Date());
		ftRlsInfoRptAllegDisp.setDtLastUpdate(new Date());

		long idFtRlsInfoRptAllegDisp = (long) sessionFactory.getCurrentSession().save(ftRlsInfoRptAllegDisp);

		return idFtRlsInfoRptAllegDisp;

	}

	/**
	 * 
	 * Method Name: saveRlsInfoRptCPA Method Description:This method save data
	 * in FT_RLS_INFO_RPT_CPA table
	 * 
	 * @param cftRlsInfoRptCPAValueDto
	 * @return long
	 */
	@Override
	public long saveRlsInfoRptCPA(CFTRlsInfoRptCPAValueDto cftRlsInfoRptCPAValueDto) {

		FtRlsInfoRptCpa ftRlsInfoRptCpa = new FtRlsInfoRptCpa();
		ftRlsInfoRptCpa.setIdFtRlsInfoRpt(cftRlsInfoRptCPAValueDto.getIdFtRlsInfoRpt());
		ftRlsInfoRptCpa.setDtLastUpdate(new Date());
		ftRlsInfoRptCpa.setDtCreated(new Date());
		ftRlsInfoRptCpa.setNmCpa(cftRlsInfoRptCPAValueDto.getNmCpa());
		ftRlsInfoRptCpa.setDtCpaVerified(cftRlsInfoRptCPAValueDto.getDtCpaVerified());
		ftRlsInfoRptCpa.setDtCpaLicensed(cftRlsInfoRptCPAValueDto.getDtCpaLicensed());
		ftRlsInfoRptCpa.setDtAhVerifRlnqshd(cftRlsInfoRptCPAValueDto.getDtAHVerifRelinquished());
		ftRlsInfoRptCpa.setCdRsnRlnqshd(cftRlsInfoRptCPAValueDto.getReasonAHVerifRelinquished());

		long idFtRlsInfoRptCpa = (long) sessionFactory.getCurrentSession().save(ftRlsInfoRptCpa);

		return idFtRlsInfoRptCpa;

	}

	/**
	 * 
	 * Method Name: selectCFTRlsInfoRpt Method Description:This method fetches
	 * data from FT_RLS_INFO_RPT table using idEvent
	 * 
	 * @param idEvent
	 * @return List<FtRlsInfoRptDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CFTRlsInfoRptValueDto selectCFTRlsInfoRpt(long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FtRlsInfoRpt.class);
		criteria.add(Restrictions.eq("idEvent", idEvent));
		CFTRlsInfoRptValueDto cFTRlsInfoRptValueDto = new CFTRlsInfoRptValueDto();
		List<FtRlsInfoRpt> ftRlsInfoRptList = (List<FtRlsInfoRpt>) criteria.list();
		// populating the response from entity if it is having records
		if (!CollectionUtils.isEmpty(ftRlsInfoRptList)) {
			FtRlsInfoRpt ftRlsInfoRpt = ftRlsInfoRptList.get(0);
			cFTRlsInfoRptValueDto.setCdReport(ftRlsInfoRpt.getCdReport());
			cFTRlsInfoRptValueDto.setDtCreated(ftRlsInfoRpt.getDtCreated());
			cFTRlsInfoRptValueDto.setDtEnd(ftRlsInfoRpt.getDtEnd());
			cFTRlsInfoRptValueDto.setDtLastUpdate(ftRlsInfoRpt.getDtLastUpdate());
			cFTRlsInfoRptValueDto.setIdCreatedPerson((int) ftRlsInfoRpt.getIdCreatedPerson());
			cFTRlsInfoRptValueDto.setIdEvent(ftRlsInfoRpt.getIdEvent().intValue());
			cFTRlsInfoRptValueDto.setIdFtRlsInfoRpt((int) ftRlsInfoRpt.getIdFtRlsInfoRpt());
			cFTRlsInfoRptValueDto.setIdLastUpdatePerson((int) ftRlsInfoRpt.getIdLastUpdatePerson());
			cFTRlsInfoRptValueDto.setIdPerson(ftRlsInfoRpt.getIdPerson().intValue());
			cFTRlsInfoRptValueDto.setNmPersonFull(ftRlsInfoRpt.getNmPersonFull());
		}
		return cFTRlsInfoRptValueDto;
	}

	/**
	 * 
	 * Method Name: selectRlsInfoRptCPS Method Description:This method fetches
	 * data from FT_RLS_INFO_RPT_CPS table.Current and Prior History for CPS.
	 * 
	 * @param idFtRlsInfoRpt
	 * @returnList<FtRlsInfoRptCpsDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CFTRlsInfoRptCPSValueDto> selectRlsInfoRptCPS(long idFtRlsInfoRpt) {
		List<CFTRlsInfoRptCPSValueDto> listRlsInfoRptCpsDtos = new ArrayList<>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FtRlsInfoRptCps.class);
		criteria.add(Restrictions.eq("idFtRlsInfoRpt", idFtRlsInfoRpt));
		criteria.addOrder(Order.asc("idFtRlsInfoRptCps"));

		List<FtRlsInfoRptCps> ftRlsInfoRptCps = criteria.list();
		// populating the response from entity if it is having records
		if (!CollectionUtils.isEmpty(ftRlsInfoRptCps)) {
			for (FtRlsInfoRptCps ftRlsInfoRptCps2 : ftRlsInfoRptCps) {
				CFTRlsInfoRptCPSValueDto cFTRlsInfoRptCPSValueDto = new CFTRlsInfoRptCPSValueDto();
				if (!ObjectUtils.isEmpty(ftRlsInfoRptCps2.getCdHistoryType())
						&& !SDMSA.equalsIgnoreCase(ftRlsInfoRptCps2.getCdHistoryType())
						&& !SDMRAFRL.equalsIgnoreCase(ftRlsInfoRptCps2.getCdHistoryType())) {

					cFTRlsInfoRptCPSValueDto.setDtCreated(ftRlsInfoRptCps2.getDtCreated());
					cFTRlsInfoRptCPSValueDto.setDtLastUpdate(ftRlsInfoRptCps2.getDtLastUpdate());
					cFTRlsInfoRptCPSValueDto.setIdCase(ftRlsInfoRptCps2.getIdCase());
					cFTRlsInfoRptCPSValueDto.setIdCreatedPerson(ftRlsInfoRptCps2.getIdCreatedPerson());
					cFTRlsInfoRptCPSValueDto.setIdFtRlsInfoRpt(ftRlsInfoRptCps2.getIdFtRlsInfoRpt());
					cFTRlsInfoRptCPSValueDto.setIdFtRlsInfoRptCps(ftRlsInfoRptCps2.getIdFtRlsInfoRptCps());
					cFTRlsInfoRptCPSValueDto.setIdLastUpdatePerson(ftRlsInfoRptCps2.getIdLastUpdatePerson());
					cFTRlsInfoRptCPSValueDto.setIdPerson(ftRlsInfoRptCps2.getIdPerson());
					cFTRlsInfoRptCPSValueDto.setIdStage(ftRlsInfoRptCps2.getIdStage());
					if (!ObjectUtils.isEmpty(ftRlsInfoRptCps2.getIndNoServicesReferrals())) {
						cFTRlsInfoRptCPSValueDto.setIndNoServicesReferrals(
								String.valueOf(ftRlsInfoRptCps2.getIndNoServicesReferrals()));
					}
					cFTRlsInfoRptCPSValueDto.setNmPersonFull(ftRlsInfoRptCps2.getNmPersonFull());
					cFTRlsInfoRptCPSValueDto.setAllegations(ftRlsInfoRptCps2.getTxtAllegations());
					cFTRlsInfoRptCPSValueDto.setAllegDisposition(ftRlsInfoRptCps2.getTxtAllegDisposition());
					cFTRlsInfoRptCPSValueDto.setCaseAction(ftRlsInfoRptCps2.getTxtCaseAction());
					cFTRlsInfoRptCPSValueDto.setServicesReferrals(ftRlsInfoRptCps2.getTxtServicesReferrals());
					cFTRlsInfoRptCPSValueDto.setServicesOther(ftRlsInfoRptCps2.getTxtServicesOther());
					cFTRlsInfoRptCPSValueDto.setSafetyRiskAssmnt(ftRlsInfoRptCps2.getTxtSafetyRiskAssmnt());
					cFTRlsInfoRptCPSValueDto.setRiskAssmntMsg(ftRlsInfoRptCps2.getTxtRiskAssmntMsg());
					cFTRlsInfoRptCPSValueDto.setOthrActn(ftRlsInfoRptCps2.getTxtOthrActn());
					cFTRlsInfoRptCPSValueDto.setIntake(ftRlsInfoRptCps2.getTxtDtIntake());
					cFTRlsInfoRptCPSValueDto.setTxtDtCvs(ftRlsInfoRptCps2.getTxtDtCvs());
					cFTRlsInfoRptCPSValueDto.setCdHistoryType(ftRlsInfoRptCps2.getCdHistoryType());
					listRlsInfoRptCpsDtos.add(cFTRlsInfoRptCPSValueDto);
				} else {
					cFTRlsInfoRptCPSValueDto.setIdStage(ftRlsInfoRptCps2.getIdStage());
					cFTRlsInfoRptCPSValueDto.setIdPerson(ftRlsInfoRptCps2.getIdPerson());
					cFTRlsInfoRptCPSValueDto.setIdCase(ftRlsInfoRptCps2.getIdCase());
					cFTRlsInfoRptCPSValueDto.setIdFtRlsInfoRpt(ftRlsInfoRptCps2.getIdFtRlsInfoRpt());    
					//Defect# 12941- Modified the logic as per the legacy to filter the report for other history section 
		            int idx = listRlsInfoRptCpsDtos.indexOf(cFTRlsInfoRptCPSValueDto);		              
		            if (idx > -1){                
		            	CFTRlsInfoRptCPSValueDto cpsHistorySDM = listRlsInfoRptCpsDtos.get(idx);
		                  if (!ObjectUtils.isEmpty(cpsHistorySDM)){
		                      if (SDMSA.equalsIgnoreCase(ftRlsInfoRptCps2.getCdHistoryType())){                        
		                    	  List<CFTSafetyAssessmentInfoDto> cFTSafetyAssessmentInfoDtoList = cpsHistorySDM.getCftSafetyAssessmentInfoDtoList();
		                          if (ObjectUtils.isEmpty(cFTSafetyAssessmentInfoDtoList)){                      
		                        	  cFTSafetyAssessmentInfoDtoList = new ArrayList<CFTSafetyAssessmentInfoDto>();
		                          } 
		                          CFTSafetyAssessmentInfoDto cFTSafetyAssessmentInfoDto = new CFTSafetyAssessmentInfoDto();
		                          //indicator for CFTContactSubCPS.jsp to let it know that this info is existing data (no need to parse data to first time when 
		                          //this data comes from existing tables - Refer: ContactDAO >> getCFTSafetyAssessmentInfoDBList()
		                          cFTSafetyAssessmentInfoDto.setIdEvent(-1l);
		                          cFTSafetyAssessmentInfoDto.setEventDescription(ftRlsInfoRptCps2.getTxtSafetyRiskAssmnt());
		                          cFTSafetyAssessmentInfoDtoList.add(cFTSafetyAssessmentInfoDto);
		                          cpsHistorySDM.setCftSafetyAssessmentInfoDtoList(cFTSafetyAssessmentInfoDtoList);                      
		                     } else if (SDMRAFRL.equalsIgnoreCase(ftRlsInfoRptCps2.getCdHistoryType())){
		                       cpsHistorySDM.setTxtSDMRAFinalRiskLevel(ftRlsInfoRptCps2.getTxtSafetyRiskAssmnt());
		                     }
		                      listRlsInfoRptCpsDtos.remove(idx);
		                      listRlsInfoRptCpsDtos.add(cpsHistorySDM);
		                }
		            }
				}

			}
		}
		return listRlsInfoRptCpsDtos;
	}

	/**
	 * 
	 * Method Name: selectCodeTableRows Method Description:This method fetches
	 * data from code_table_row table using 'ReasonRlngshmnt' nm_table.
	 * 
	 * @return Map<String, String> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> selectCodeTableRows() {
		Map<String, String> resulthashMap = new HashMap<>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CodeTableRow.class);
		criteria.add(Restrictions.eq("nmTable", ServiceConstants.NM_TABLE));

		List<CodeTableRow> licodeTableRows = (List<CodeTableRow>) criteria.list();
		if (TypeConvUtil.isNullOrEmpty(licodeTableRows)) {
			throw new DataNotFoundException(
					messageSource.getMessage("ChildFatality1050BDaoImpl.notFound", null, Locale.US));
		}
		for (CodeTableRow codeTableRow : licodeTableRows) {
			resulthashMap.put(codeTableRow.getCdCode(), codeTableRow.getSdsCode());
		}
		return resulthashMap;
	}

	/**
	 * Method Name: selectRlsInfoAllegDispositions Method Description:This
	 * method fetches data from FT_RLS_INFO_RPT_ALLEG_DISP table.Allegations of
	 * Abuse/Neglect in this Home in Last Five Years
	 * 
	 * @param idFtRlsInfoRpt
	 * @return List<FtRlsInfoRptAllegDispDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CFTRlsInfoRptAllegDispValueDto> selectRlsInfoAllegDispositions(Long idFtRlsInfoRpt) {
		List<CFTRlsInfoRptAllegDispValueDto> listRlsInfoRptAllegDispDtos = new ArrayList<>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FtRlsInfoRptAllegDisp.class);
		criteria.add(Restrictions.eq("idFtRlsInfoRpt", idFtRlsInfoRpt));
		criteria.addOrder(Order.desc("dtInvStart"));
		List<FtRlsInfoRptAllegDisp> listRlsInfoRptAllegDisp = (List<FtRlsInfoRptAllegDisp>) criteria.list();
		// populating the response from entity if it is having records
		if (!CollectionUtils.isEmpty(listRlsInfoRptAllegDisp)) {
			for (FtRlsInfoRptAllegDisp ftRlsInfoRptAllegDisp : listRlsInfoRptAllegDisp) {
				CFTRlsInfoRptAllegDispValueDto cftRlsInfoRptAllegDispValueDto = new CFTRlsInfoRptAllegDispValueDto();
				cftRlsInfoRptAllegDispValueDto.setDtCreated(ftRlsInfoRptAllegDisp.getDtCreated());
				cftRlsInfoRptAllegDispValueDto.setDtInvStart(ftRlsInfoRptAllegDisp.getDtInvStart());
				cftRlsInfoRptAllegDispValueDto.setDtLastUpdate(ftRlsInfoRptAllegDisp.getDtLastUpdate());
				cftRlsInfoRptAllegDispValueDto.setIdFtRlsInfoRpt(ftRlsInfoRptAllegDisp.getIdFtRlsInfoRpt());
				cftRlsInfoRptAllegDispValueDto
						.setIdFtRlsInfoRptAllegDisp(ftRlsInfoRptAllegDisp.getIdFtRlsInfoRptAllegDisp());
				if (!ObjectUtils.isEmpty(ftRlsInfoRptAllegDisp.getIndDeceasedAllegedVictim())) {
					cftRlsInfoRptAllegDispValueDto.setIndDeceasedAllegedVictim(
							ftRlsInfoRptAllegDisp.getIndDeceasedAllegedVictim().toString());
				}
				if (!ObjectUtils.isEmpty(ftRlsInfoRptAllegDisp.getIndPendingAppeal())) {
					cftRlsInfoRptAllegDispValueDto
							.setIndPendingAppeal(ftRlsInfoRptAllegDisp.getIndPendingAppeal().toString());
				}
				cftRlsInfoRptAllegDispValueDto.setTxtAllegDisposition(ftRlsInfoRptAllegDisp.getTxtAllegDisposition());
				cftRlsInfoRptAllegDispValueDto.setTxtAllegType(ftRlsInfoRptAllegDisp.getTxtAllegType());
				listRlsInfoRptAllegDispDtos.add(cftRlsInfoRptAllegDispValueDto);
			}
		}

		return listRlsInfoRptAllegDispDtos;
	}

	/**
	 * Method Name: selectRlsInfoRptRsrc Method Description:This method fetches
	 * data from FT_RLS_INFO_RPT_RSRC table.
	 * 
	 * @param idFtRlsInfoRpt
	 * @return FtRlsInfoRptRsrcDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CFTRlsInfoRptResourceValueDto selectRlsInfoRptRsrc(Long idFtRlsInfoRpt) {
		CFTRlsInfoRptResourceValueDto cftRlsInfoRptResourceValueDto = new CFTRlsInfoRptResourceValueDto();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FtRlsInfoRptRsrc.class);
		criteria.add(Restrictions.eq("idFtRlsInfoRpt", idFtRlsInfoRpt));
		List<FtRlsInfoRptRsrc> liftRlsInfoRptRsrcs = (List<FtRlsInfoRptRsrc>) criteria.list();
		// populating the response from entity if it is having records
		if (!CollectionUtils.isEmpty(liftRlsInfoRptRsrcs)) {
			FtRlsInfoRptRsrc ftRlsInfoRptRsrc = liftRlsInfoRptRsrcs.get(0);
			cftRlsInfoRptResourceValueDto.setDtCreated(ftRlsInfoRptRsrc.getDtCreated());
			cftRlsInfoRptResourceValueDto.setDtLastUpdate(ftRlsInfoRptRsrc.getDtLastUpdate());
			cftRlsInfoRptResourceValueDto.setDtDateOperationLicensed(ftRlsInfoRptRsrc.getDtOperationLicensed());
			cftRlsInfoRptResourceValueDto.setIdCreatedPerson(ftRlsInfoRptRsrc.getIdCreatedPerson());
			cftRlsInfoRptResourceValueDto.setIdFtRlsInfoRpt(ftRlsInfoRptRsrc.getIdFtRlsInfoRpt());
			cftRlsInfoRptResourceValueDto.setIdFtRlsInfoRptRsrc(ftRlsInfoRptRsrc.getIdFtRlsInfoRptRsrc());
			cftRlsInfoRptResourceValueDto.setIdLastUpdatePerson(ftRlsInfoRptRsrc.getIdLastUpdatePerson());
			cftRlsInfoRptResourceValueDto.setNbrRsrc(ftRlsInfoRptRsrc.getNbrRsrc());
			cftRlsInfoRptResourceValueDto.setNmRsrc(ftRlsInfoRptRsrc.getNmRsrc());
			cftRlsInfoRptResourceValueDto.setTxtMinStndrdVltnTrain(ftRlsInfoRptRsrc.getTxtMinStndrdVltnTrain());
			cftRlsInfoRptResourceValueDto.setTxtOperationType(ftRlsInfoRptRsrc.getTxtOperationType());
			cftRlsInfoRptResourceValueDto.setTxtSummaryRemedialActn(ftRlsInfoRptRsrc.getTxtSummaryRemedialActn());
		}

		return cftRlsInfoRptResourceValueDto;
	}

	/**
	 * Method Name: selectRlsInfoRptRsrcVoilations Method Description: This
	 * method fetches data from FT_RLS_INFO_RPT_RSRC_VOLTNS table
	 * 
	 * @param idFtRlsInfoRpt
	 * @return List<FtRlsInfoRptRsrcVoltnsDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CFTRlsInfoRptRsrcVoltnsValueDto> selectRlsInfoRptRsrcVoilations(Long idFtRlsInfoRpt) {
		List<CFTRlsInfoRptRsrcVoltnsValueDto> listRlsInfoRptRsrcVoltnsDtos = new ArrayList<CFTRlsInfoRptRsrcVoltnsValueDto>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FtRlsInfoRptRsrcVoltns.class);
		criteria.add(Restrictions.eq("idFtRlsInfoRpt", idFtRlsInfoRpt));

		List<FtRlsInfoRptRsrcVoltns> liftRlsInfoRptRsrcVoltns = (List<FtRlsInfoRptRsrcVoltns>) criteria.list();
		if (TypeConvUtil.isNullOrEmpty(liftRlsInfoRptRsrcVoltns)) {
			throw new DataNotFoundException(
					messageSource.getMessage("ChildFatality1050BDaoImpl.notFound", null, Locale.US));
		}
		// populating the response from entity if it is having records
		for (FtRlsInfoRptRsrcVoltns ftRlsInfoRptRsrcVoltns : liftRlsInfoRptRsrcVoltns) {
			CFTRlsInfoRptRsrcVoltnsValueDto cftRlsInfoRptRsrcVoltnsValueDto = new CFTRlsInfoRptRsrcVoltnsValueDto();

			cftRlsInfoRptRsrcVoltnsValueDto.setDtCreated(ftRlsInfoRptRsrcVoltns.getDtCreated());
			cftRlsInfoRptRsrcVoltnsValueDto.setDtLastUpdate(ftRlsInfoRptRsrcVoltns.getDtLastUpdate());
			cftRlsInfoRptRsrcVoltnsValueDto.setDtViolation(ftRlsInfoRptRsrcVoltns.getDtViolation());
			cftRlsInfoRptRsrcVoltnsValueDto.setIdFtRlsInfoRpt(ftRlsInfoRptRsrcVoltns.getIdFtRlsInfoRpt());
			cftRlsInfoRptRsrcVoltnsValueDto
					.setIdFtRlsInfoRptRsrcVltns(ftRlsInfoRptRsrcVoltns.getIdFtRlsInfoRptRsrcVltns());
			cftRlsInfoRptRsrcVoltnsValueDto.setTxtTac(ftRlsInfoRptRsrcVoltns.getTxtTac());
			cftRlsInfoRptRsrcVoltnsValueDto.setTxtTacDesc(ftRlsInfoRptRsrcVoltns.getTxtTacDesc());
			listRlsInfoRptRsrcVoltnsDtos.add(cftRlsInfoRptRsrcVoltnsValueDto);
		}
		return listRlsInfoRptRsrcVoltnsDtos;
	}

	/**
	 * Method Name: selectRlsInfoRptCPA Method Description:This method fetches
	 * data from FT_RLS_INFO_RPT_CPA table.
	 * 
	 * @param idFtRlsInfoRpt
	 * @return List<FtRlsInfoRptCpaDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CFTRlsInfoRptCPAValueDto> selectRlsInfoRptCPA(Long idFtRlsInfoRpt) {
		List<CFTRlsInfoRptCPAValueDto> listRlsInfoRptCpaDtos = new ArrayList<CFTRlsInfoRptCPAValueDto>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FtRlsInfoRptCpa.class);
		criteria.add(Restrictions.eq("idFtRlsInfoRpt", idFtRlsInfoRpt));
		criteria.addOrder(Order.desc("dtAhVerifRlnqshd"));

		List<FtRlsInfoRptCpa> liftRlsInfoRptCpas = (List<FtRlsInfoRptCpa>) criteria.list();
		if (TypeConvUtil.isNullOrEmpty(liftRlsInfoRptCpas)) {
			throw new DataNotFoundException(
					messageSource.getMessage("ChildFatality1050BDaoImpl.notFound", null, Locale.US));
		}
		// populating the response from entity if it is having records
		for (FtRlsInfoRptCpa ftRlsInfoRptCpa : liftRlsInfoRptCpas) {
			CFTRlsInfoRptCPAValueDto cftRlsInfoRptCPAValueDto = new CFTRlsInfoRptCPAValueDto();
			cftRlsInfoRptCPAValueDto.setReasonAHVerifRelinquished(ftRlsInfoRptCpa.getCdRsnRlnqshd());
			cftRlsInfoRptCPAValueDto.setDtAHVerifRelinquished(ftRlsInfoRptCpa.getDtAhVerifRlnqshd());
			cftRlsInfoRptCPAValueDto.setDtCpaLicensed(ftRlsInfoRptCpa.getDtCpaLicensed());
			cftRlsInfoRptCPAValueDto.setDtCpaVerified(ftRlsInfoRptCpa.getDtCpaVerified());
			cftRlsInfoRptCPAValueDto.setDtCreated(ftRlsInfoRptCpa.getDtCreated());
			cftRlsInfoRptCPAValueDto.setDtLastUpdate(ftRlsInfoRptCpa.getDtLastUpdate());
			cftRlsInfoRptCPAValueDto.setIdFtRlsInfoRpt(ftRlsInfoRptCpa.getIdFtRlsInfoRpt());
			cftRlsInfoRptCPAValueDto.setIdFtRlsInfoRptCpa(ftRlsInfoRptCpa.getIdFtRlsInfoRptCpa());
			cftRlsInfoRptCPAValueDto.setNmCpa(ftRlsInfoRptCpa.getNmCpa());
			listRlsInfoRptCpaDtos.add(cftRlsInfoRptCPAValueDto);
		}
		return listRlsInfoRptCpaDtos;
	}

	/**
	 * 
	 * Method Name: insertRlsInfoRptCPS Method Description: This method inserts
	 * record into FT_RLS_INFO_RPT_CPS table.
	 * 
	 * @param rlsInfoRptCPS
	 * @param idrlsInfoRpt
	 * @param historyType
	 * @param sdmInfo
	 * @param counter
	 * @return Long
	 */
	@Override
	public Long insertRlsInfoRptCPS(CFTRlsInfoRptCPSValueModBean rlsInfoRptCPS, Long idrlsInfoRpt, String historyType,
			String sdmInfo, int counter) {
		// getting the entity object to make and setting the values to make
		// insert the record into the table
		FtRlsInfoRptCps ftRlsInfoRptCps = new FtRlsInfoRptCps();
		ftRlsInfoRptCps.setIdLastUpdatePerson(rlsInfoRptCPS.getIdLastUpdatePerson());
		ftRlsInfoRptCps.setIdCreatedPerson(rlsInfoRptCPS.getIdCreatedPerson());
		ftRlsInfoRptCps.setIdFtRlsInfoRpt(idrlsInfoRpt);
		ftRlsInfoRptCps.setCdHistoryType(historyType);
		ftRlsInfoRptCps.setIdPerson(rlsInfoRptCPS.getIdPerson());
		ftRlsInfoRptCps.setNmPersonFull(rlsInfoRptCPS.getNmPersonFull());
		ftRlsInfoRptCps.setIdCase(rlsInfoRptCPS.getIdCase());
		ftRlsInfoRptCps.setIdStage(rlsInfoRptCPS.getIdStage());
		ftRlsInfoRptCps.setTxtDtIntake(rlsInfoRptCPS.getDtIntake());
		ftRlsInfoRptCps.setTxtAllegations(rlsInfoRptCPS.getAllegations());
		ftRlsInfoRptCps.setTxtAllegDisposition(rlsInfoRptCPS.getAllegDisposition());
		ftRlsInfoRptCps.setTxtSafetyRiskAssmnt(sdmInfo);
		ftRlsInfoRptCps.setTxtCaseAction(rlsInfoRptCPS.getCaseAction());
		ftRlsInfoRptCps.setTxtServicesReferrals(rlsInfoRptCPS.getServicesReferrals());
		ftRlsInfoRptCps.setTxtServicesOther(rlsInfoRptCPS.getServicesOther());
		ftRlsInfoRptCps
				.setIndNoServicesReferrals(rlsInfoRptCPS.getIndNoServicesReferrals().charAt(ServiceConstants.Zero));
		ftRlsInfoRptCps.setTxtOthrActn(rlsInfoRptCPS.getOthrActn());
		ftRlsInfoRptCps.setTxtDtCvs(rlsInfoRptCPS.getDtCvs());
		ftRlsInfoRptCps.setTxtRiskAssmntMsg(String.valueOf(counter));
		//Defect 13525, To fix the oracle non-null constrains
		ftRlsInfoRptCps.setDtCreated(new Date());
		ftRlsInfoRptCps.setDtLastUpdate(new Date());
		// calling the save method using hibernate to insert the record into the
		// table
		Long idftRlsInfoRptCps = (Long) sessionFactory.getCurrentSession().save(ftRlsInfoRptCps);

		if (TypeConvUtil.isNullOrEmpty(idftRlsInfoRptCps)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Ccmnc1d.StageLink.not.inserted", null, Locale.US));
		}

		return idftRlsInfoRptCps;
	}

	/**
	 * 
	 * Method Name: insertRlsInfoAllegDispositionBatch Method Description: This
	 * method inserts record into FT_RLS_INFO_RPT_ALLEG_DISP table.
	 * 
	 * @param rlsInfoAllegDispositions
	 * @param idrlsInfoRpt
	 * @return List<Long>
	 */
	@Override
	public List<Long> insertRlsInfoAllegDispositionBatch(List<CFTRlsInfoRptAllegDispValueBean> rlsInfoAllegDispositions,
			Long idrlsInfoRpt) {

		List<Long> liIdFtRlsInfoRptAllegDisp = new ArrayList<>();
		if (TypeConvUtil.isNullOrEmpty(rlsInfoAllegDispositions)) {
			return null;
		}

		Long idFtRlsInfoRptAllegDisp = ServiceConstants.ZERO_VAL;
		for (CFTRlsInfoRptAllegDispValueBean cFTRlsInfoRptAllegDispValueDto : rlsInfoAllegDispositions) {
			// getting the entity object to make and setting the values to make
			// insert the record into the table
			FtRlsInfoRptAllegDisp ftRlsInfoRptAllegDisp = new FtRlsInfoRptAllegDisp();
			ftRlsInfoRptAllegDisp.setIdFtRlsInfoRpt(idrlsInfoRpt);
			ftRlsInfoRptAllegDisp.setDtInvStart(cFTRlsInfoRptAllegDispValueDto.getDtInvStart());
			ftRlsInfoRptAllegDisp.setTxtAllegType(cFTRlsInfoRptAllegDispValueDto.getAllegType());
			ftRlsInfoRptAllegDisp.setTxtAllegDisposition(cFTRlsInfoRptAllegDispValueDto.getAllegDisposition());
			ftRlsInfoRptAllegDisp.setIndPendingAppeal(
					cFTRlsInfoRptAllegDispValueDto.getIndPendingAppeal().charAt(ServiceConstants.Zero));
			ftRlsInfoRptAllegDisp.setIndDeceasedAllegedVictim(
					cFTRlsInfoRptAllegDispValueDto.getIndDeceasedAllegedVictim().charAt(ServiceConstants.Zero));
			ftRlsInfoRptAllegDisp.setDtCreated(cFTRlsInfoRptAllegDispValueDto.getDtCreated());
			ftRlsInfoRptAllegDisp.setDtLastUpdate(cFTRlsInfoRptAllegDispValueDto.getDtLastUpdate());
			// calling the save method using hibernate to insert the record into
			// the table
			idFtRlsInfoRptAllegDisp = (Long) sessionFactory.getCurrentSession().save(ftRlsInfoRptAllegDisp);
			liIdFtRlsInfoRptAllegDisp.add(idFtRlsInfoRptAllegDisp);

		}

		if (TypeConvUtil.isNullOrEmpty(liIdFtRlsInfoRptAllegDisp)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Ccmnc1d.StageLink.not.inserted", null, Locale.US));
		}

		return liIdFtRlsInfoRptAllegDisp;
	}

	/**
	 * 
	 * Method Name: insertRlsInfoRptRsrc Method Description: This method inserts
	 * record into FT_RLS_INFO_RPT_RSRC table.
	 * 
	 * @param rlsInfoRptRsrc
	 * @return Long @
	 */
	@Override
	public Long insertRlsInfoRptRsrc(CFTRlsInfoRptResourceValueBean rlsInfoRptRsrc) {
		// getting the entity object to make and setting the values to make
		// insert the record into the table
		FtRlsInfoRptRsrc ftRlsInfoRptRsrc = new FtRlsInfoRptRsrc();
		ftRlsInfoRptRsrc.setIdLastUpdatePerson(rlsInfoRptRsrc.getIdLastUpdatePerson());
		ftRlsInfoRptRsrc.setIdCreatedPerson(rlsInfoRptRsrc.getIdCreatedPerson());
		ftRlsInfoRptRsrc.setIdFtRlsInfoRpt(rlsInfoRptRsrc.getIdFtRlsInfoRpt());
		ftRlsInfoRptRsrc.setNmRsrc(rlsInfoRptRsrc.getNmRsrc());
		ftRlsInfoRptRsrc.setNbrRsrc(rlsInfoRptRsrc.getRsrc());
		ftRlsInfoRptRsrc.setTxtOperationType(rlsInfoRptRsrc.getOperationType());
		ftRlsInfoRptRsrc.setTxtMinStndrdVltnTrain(rlsInfoRptRsrc.getMinStndrdVltnTrain());
		ftRlsInfoRptRsrc.setTxtSummaryRemedialActn(rlsInfoRptRsrc.getSummaryRemedialActn());
		ftRlsInfoRptRsrc.setDtOperationLicensed(rlsInfoRptRsrc.getDtDateOperationLicensed());
		ftRlsInfoRptRsrc.setDtCreated(rlsInfoRptRsrc.getDtCreated());
		ftRlsInfoRptRsrc.setDtLastUpdate(rlsInfoRptRsrc.getDtLastUpdate());
		// calling the save method using hibernate to insert the record into the
		// table
		Long idftRlsInfoRptRsrc = (Long) sessionFactory.getCurrentSession().save(ftRlsInfoRptRsrc);
		if (TypeConvUtil.isNullOrEmpty(idftRlsInfoRptRsrc)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Ccmnc1d.StageLink.not.inserted", null, Locale.US));
		}

		return idftRlsInfoRptRsrc;
	}

	/**
	 * 
	 * Method Name: insertRsrcViolationBatch Method Description: This method
	 * inserts record into FT_RLS_INFO_RPT_RSRC_VOLTNS table.
	 * 
	 * @param rlsInfoRptRsrcVoltns
	 * @param idrlsInfoRpt
	 * @return Long @
	 */
	@Override
	public List<Long> insertRsrcViolationBatch(List<CFTRlsInfoRptRsrcVoltnsValueBean> liftRlsInfoRptRsrcVoltns,
			Long idrlsInfoRpt) {

		Long idFtRlsInfoRptRsrcVoltns = ServiceConstants.ZERO_VAL;
		List<Long> liIdFtRlsInfoRptRsrcVoltns = new ArrayList<>();
		for (CFTRlsInfoRptRsrcVoltnsValueBean cFTRlsInfoRptRsrcVoltnsValueDto : liftRlsInfoRptRsrcVoltns) {
			// getting the entity object to make and setting the values to make
			// insert the record into the table
			FtRlsInfoRptRsrcVoltns ftRlsInfoRptRsrcVoltns = new FtRlsInfoRptRsrcVoltns();
			ftRlsInfoRptRsrcVoltns.setDtLastUpdate(cFTRlsInfoRptRsrcVoltnsValueDto.getDtLastUpdate());
			ftRlsInfoRptRsrcVoltns.setDtCreated(cFTRlsInfoRptRsrcVoltnsValueDto.getDtCreated());
			ftRlsInfoRptRsrcVoltns.setIdFtRlsInfoRpt(idrlsInfoRpt);
			ftRlsInfoRptRsrcVoltns.setDtViolation(cFTRlsInfoRptRsrcVoltnsValueDto.getDtViolation());
			ftRlsInfoRptRsrcVoltns.setTxtTac(cFTRlsInfoRptRsrcVoltnsValueDto.getTac());
			ftRlsInfoRptRsrcVoltns.setTxtTacDesc(cFTRlsInfoRptRsrcVoltnsValueDto.getTacDesc());
			// calling the save method using hibernate to insert the record into
			// the table
			idFtRlsInfoRptRsrcVoltns = (Long) sessionFactory.getCurrentSession().save(ftRlsInfoRptRsrcVoltns);
			liIdFtRlsInfoRptRsrcVoltns.add(idFtRlsInfoRptRsrcVoltns);
		}
		if (TypeConvUtil.isNullOrEmpty(liIdFtRlsInfoRptRsrcVoltns)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Ccmnc1d.StageLink.not.inserted", null, Locale.US));
		}

		return liIdFtRlsInfoRptRsrcVoltns;
	}

	/**
	 * 
	 * Method Name: insertRlsInfoRptCPABatch Method Description: This method
	 * inserts record into FT_RLS_INFO_RPT_CPA table.
	 * 
	 * @param rlsInfoRptCPAList
	 * @param idrlsInfoRpt
	 * @return Long @
	 */
	@Override
	public List<Long> insertRlsInfoRptCPABatch(List<CFTRlsInfoRptCPAValueModBean> rlsInfoRptCPAList,
			Long idrlsInfoRpt) {

		Long idFtRlsInfoRptCpa = ServiceConstants.ZERO_VAL;
		List<Long> liIdFtRlsInfoRptCpa = new ArrayList<>();

		for (CFTRlsInfoRptCPAValueModBean cFTRlsInfoRptCPAValueDto : rlsInfoRptCPAList) {
			// getting the entity object to make and setting the values to make
			// insert the record into the table
			FtRlsInfoRptCpa ftRlsInfoRptCpa = new FtRlsInfoRptCpa();
			ftRlsInfoRptCpa.setDtLastUpdate(cFTRlsInfoRptCPAValueDto.getDtLastUpdate());
			ftRlsInfoRptCpa.setDtCreated(cFTRlsInfoRptCPAValueDto.getDtCreated());
			ftRlsInfoRptCpa.setIdFtRlsInfoRpt(idrlsInfoRpt);
			ftRlsInfoRptCpa.setNmCpa(cFTRlsInfoRptCPAValueDto.getNmCpa());
			ftRlsInfoRptCpa.setDtCpaVerified(cFTRlsInfoRptCPAValueDto.getDtCpaVerified());
			ftRlsInfoRptCpa.setDtCpaLicensed(cFTRlsInfoRptCPAValueDto.getDtCpaLicensed());
			ftRlsInfoRptCpa.setDtAhVerifRlnqshd(cFTRlsInfoRptCPAValueDto.getDtAHVerifRelinquished());
			ftRlsInfoRptCpa.setCdRsnRlnqshd(cFTRlsInfoRptCPAValueDto.getReasonAHVerifRelinquished());
			// calling the save method using hibernate to insert the record into
			// the table
			idFtRlsInfoRptCpa = (Long) sessionFactory.getCurrentSession().save(ftRlsInfoRptCpa);
			liIdFtRlsInfoRptCpa.add(idFtRlsInfoRptCpa);

		}

		if (TypeConvUtil.isNullOrEmpty(liIdFtRlsInfoRptCpa)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Ccmnc1d.StageLink.not.inserted", null, Locale.US));
		}
		return liIdFtRlsInfoRptCpa;
	}

	/**
	 * 
	 * Method Name: updateRlsInfoRptRsrc Method Description: This method updates
	 * record into FT_RLS_INFO_RPT_RSRC table.
	 * 
	 * @param rlsInfoRptRsrc
	 * @return Long @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long updateRlsInfoRptRsrc(CFTRlsInfoRptResourceValueBean rlsInfoRptRsrc) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FtRlsInfoRptRsrc.class);
		criteria.add(Restrictions.eq("idFtRlsInfoRpt", rlsInfoRptRsrc.getIdFtRlsInfoRpt()));
		List<FtRlsInfoRptRsrc> ftRlsInfoRptRsrcList = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(ftRlsInfoRptRsrcList)) {
			throw new DataNotFoundException(messageSource.getMessage("FtRlsInfoRptRsrc.NotFound", null, Locale.US));
		}
		for (FtRlsInfoRptRsrc ftRlsInfoRptRsrc : ftRlsInfoRptRsrcList) {
			ftRlsInfoRptRsrc.setIdLastUpdatePerson(rlsInfoRptRsrc.getIdLastUpdatePerson());
			ftRlsInfoRptRsrc.setDtLastUpdate(rlsInfoRptRsrc.getDtLastUpdate());
			ftRlsInfoRptRsrc.setTxtMinStndrdVltnTrain(rlsInfoRptRsrc.getMinStndrdVltnTrain());
			ftRlsInfoRptRsrc.setTxtSummaryRemedialActn(rlsInfoRptRsrc.getSummaryRemedialActn());
			sessionFactory.getCurrentSession().saveOrUpdate(ftRlsInfoRptRsrc);
		}
		return (long) ftRlsInfoRptRsrcList.size();
	}

}
