package us.tx.state.dfps.service.casepackage.serviceimpl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.CaseFileManagement;
import us.tx.state.dfps.common.domain.Office;
import us.tx.state.dfps.common.domain.RecordsRetention;
import us.tx.state.dfps.common.domain.Unit;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dao.CaseFileManagementDao;
import us.tx.state.dfps.service.casepackage.dao.OfficeDao;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseDto;
import us.tx.state.dfps.service.casepackage.dto.CaseFileManagementDto;
import us.tx.state.dfps.service.casepackage.dto.OfficeDto;
import us.tx.state.dfps.service.casepackage.service.CaseFileManagementService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.UnitDao;
import us.tx.state.dfps.service.common.request.CaseFileManagementReq;
import us.tx.state.dfps.service.common.request.CaseFileMgtReq;
import us.tx.state.dfps.service.common.request.RecordsRetentionReq;
import us.tx.state.dfps.service.common.request.RtrvUnitIdReq;
import us.tx.state.dfps.service.common.response.CaseFileMgtRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.person.dto.UnitDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCFC21S Class
 * Description: This class is doing service Implementation for
 * CaseFileManagementService Mar 24, 2017 - 7:50:07 PM
 */
@Service
@Transactional
public class CaseFileManagementServicesImpl implements CaseFileManagementService {

	@Autowired
	CapsCaseDao capscaseDao;

	@Autowired
	CaseFileManagementDao caseFileManagementDao;

	@Autowired
	OfficeDao officeDao;

	@Autowired
	UnitDao rtrvNbrUnitDao;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	/**
	 * 
	 * Method Name: CaseFileManagementDto Method Description: This Method will
	 * retrieve all columns for an Id Case from the CASE FILE MANAGEMENT table.
	 * There will be one row for a specified Id Case. It will retrieve a full
	 * row from both the OFFICE and UNIT tables with the ID OFFICE and ID UNIT
	 * respectively. The service will also retrieve a full row from the CAPS
	 * CASE table to get the closure date for the Case. Finally, it will check
	 * to see if the person who entered the window is the primary worker.
	 * Service Name: CCFC21S
	 * 
	 * @param caseFileMgtReq
	 * @return CaseFileManagementDto
	 * 
	 */
	@Transactional
	public CaseFileManagementDto getCaseFileManagementRtrv(CaseFileMgtReq caseFileMgtReq) {
		CaseFileManagementDto casefile = new CaseFileManagementDto();
		try {
			CapsCaseDto capscase = capscaseDao.getCaseDetails(caseFileMgtReq.getIdCase());
			casefile = caseFileManagementDao.getCaseFileDetails(caseFileMgtReq.getIdCase());
			if (!TypeConvUtil.isNullOrEmpty(casefile)) {
				Long idOffice = 0l;
				Long idUnit = 0l;
				idOffice = casefile.getIdOffice();
				idUnit = casefile.getIdUnit();
				OfficeDto office = new OfficeDto();
				UnitDto unit = new UnitDto();
				if (ServiceConstants.CD_FILE_OFFICE_TYPE_PRS.equals(casefile.getCdCaseFileOfficeType())) {
					office = officeDao.getOfficeDetails(idOffice);
					unit = rtrvNbrUnitDao.getUnitDtlsById(idUnit);
				}
				Long primaryWorker = stagePersonLinkDao.getPrimaryCaseWorker(caseFileMgtReq);
				if (primaryWorker > 0) {
					casefile.setSysIndPrimaryWorker(Boolean.TRUE);
				} else {
					casefile.setSysIndPrimaryWorker(Boolean.FALSE);
				}
				casefile.setIdCaseFileCase(caseFileMgtReq.getIdCase());
				casefile.setCdCaseProgram(capscase.getCdCaseProgram());

				//artf254843: office can be null, added check to avoid NullPointerException.
				if(!ObjectUtils.isEmpty(office)) {
					casefile.setCdOfficeRegion(office.getCdOfficeRegion());
					casefile.setCdOfficeMail(office.getCdOfficeMail());
				}
				if (!ObjectUtils.isEmpty(unit))
					casefile.setUnit(unit.getNbrUnit());
				casefile.setDtWCDDtSystemDate(new Date());
			} else {
				if (!ObjectUtils.isEmpty(capscase)) {
					casefile = new CaseFileManagementDto();
					casefile.setCdCaseProgram(capscase.getCdCaseProgram());
					casefile.setDtCaseClosed(capscase.getDtCaseClosed());
				}
			}
		} catch (HibernateException e) {
			ServiceLayerException serviceLayerException = new ServiceLayerException(e.toString());
			serviceLayerException.initCause(e);
			throw serviceLayerException;
		}
		return casefile;
	}

	/**
	 * Method Name: manageRecordsRetention Method Description:This service will
	 * add/update all columns for an Id Case from the RECORDS RETENTION table.
	 * It will call DAM: CAUD75D - REC RETN AUD.
	 * 
	 * @return String
	 * 
	 */
	@Override
	@Transactional
	public String manageRecordsRetention(RecordsRetentionReq recordsRetentionReq) {
		String message = "";
		switch (recordsRetentionReq.getReqFuncCd()) {
		case "FUNC_CD_ADD":
			long id = caseFileManagementDao
					.insertRecordsRetention(createTransientRecordsRetention(recordsRetentionReq));
			message = String.format("Saved new RecordsRentention with %d", id);
			break;
		case "FUNC_CD_UPDATE":
			caseFileManagementDao.updateRecordsRetention(createTransientRecordsRetention(recordsRetentionReq));
			message = String.format("Updated successfully RecordsRentention .");
			break;
		case "FUNC_CD_DELETE":
			caseFileManagementDao.deleteRecordsRetention(createTransientRecordsRetention(recordsRetentionReq));
			message = String.format("Deleted successfully RecordsRentention .");
			break;
		default:
			throw new ServiceLayerException("Unsupported EFunc code" + recordsRetentionReq.getReqFuncCd());
		}
		return message;
	}

	/**
	 * Method Name: manageCaseFileManagement Method Description: This service
	 * will save all columns for an IdCase to the CASE FILE MANAGEMENT table.
	 * There will be one row for a specified IdCase. Furthermore, it will check
	 * to see if the MailCode/Region/Program specified exists as well as the
	 * Unit/Region/Program exists. Additionally, it will retrieve a full row
	 * from the CAPS CASE table to get the closure date for the Case.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String manageCaseFileManagement(CaseFileManagementReq caseFileManagementReq) {
		String message = "";
		UnitDto unitDto = rtrvNbrUnitDao.getUnitId(new RtrvUnitIdReq(caseFileManagementReq.getCdOfficeRegion(),
				caseFileManagementReq.getCdCaseProgram(), caseFileManagementReq.getNbrUnit()));
		Office office = officeDao.getOfficeName(caseFileManagementReq.getAddrMailCode(),
				caseFileManagementReq.getCdOfficeRegion(), caseFileManagementReq.getCdCaseProgram());
		switch (caseFileManagementReq.getReqFuncCd()) {
		case "FUNC_CD_ADD":
			long id = caseFileManagementDao
					.insertCaseFileManagement(createCaseFileManagement(caseFileManagementReq, unitDto, office));
			message = String.format("Saved new  CaseFileManagement with %d", id);
			break;
		case "FUNC_CD_UPDATE":
			caseFileManagementDao
					.updateCaseFileManagement(createCaseFileManagement(caseFileManagementReq, unitDto, office));
			message = String.format("Updated successfully CaseFileManagement .");
			break;
		case "FUNC_CD_DELETE":
			caseFileManagementDao
					.deleteCaseFileManagement(createCaseFileManagement(caseFileManagementReq, unitDto, office));
			message = String.format("Deleted successfully CaseFileManagement .");
			break;
		default:
			throw new ServiceLayerException("Unsupported EFunc code" + caseFileManagementReq.getReqFuncCd());
		}
		return message;
	}

	/**
	 * 
	 * Method Name: createTransientRecordsRetention Method Description:
	 * createTransientRecordsRetention
	 * 
	 * @param retentionReq
	 * @return RecordsRetention
	 */
	protected RecordsRetention createTransientRecordsRetention(RecordsRetentionReq retentionReq) {
		RecordsRetention recordsRetention = new RecordsRetention();
		BeanUtils.copyProperties(retentionReq, recordsRetention);
		return recordsRetention;
	}

	/**
	 * 
	 * Method Name: createCaseFileManagement Method Description: create the
	 * CaseFileManagement
	 * 
	 * @param caseFileManagementReq
	 * @param unitDto
	 * @param office
	 * @return CaseFileManagement
	 */
	protected CaseFileManagement createCaseFileManagement(CaseFileManagementReq caseFileManagementReq, UnitDto unitDto,
			Office office) {
		CaseFileManagement caseFileManagement = new CaseFileManagement();
		BeanUtils.copyProperties(caseFileManagementReq, caseFileManagement);
		caseFileManagement.setIdCaseFileCase(caseFileManagementReq.getUlIdCase());
		CapsCase capsCase = new CapsCase();
		capsCase.setIdCase(caseFileManagementReq.getUlIdCase());
		Unit unit = new Unit();
		unit.setIdUnit(unitDto.getIdUnit());
		unit.setNbrUnit(unitDto.getNbrUnit());
		caseFileManagement.setOffice(office);
		caseFileManagement.setUnit(unit);
		caseFileManagement.setCapsCase(capsCase);
		caseFileManagement.setOffice(office);
		caseFileManagement.setUnit(unit);
		return caseFileManagement;
	}

	/**
	 * Method Name: CaseFileMgtRes Method Description: This method will retrieve
	 * locating information. Service Name:CFMgmntList
	 * 
	 * @param caseFileMgtReq
	 * @return CaseFileMgtRes
	 * 
	 */
	@Transactional
	public CaseFileMgtRes getCFMList(CaseFileMgtReq caseFileMgtReq) {
		CaseFileMgtRes cFMgmntListRes = new CaseFileMgtRes();
		List<CaseFileManagementDto> cfMgmtList = null;
		List<CaseFileManagementDto> cfMgmtListMap = null;
		cfMgmtList = caseFileManagementDao.getCFMgmntList(caseFileMgtReq.getIdCase());
		cFMgmntListRes.setcFMgmtDtoList(cfMgmtList);
		Long caseId = cfMgmtList.get(cfMgmtList.size() - 1).getIdCaseFileCase();
		cFMgmntListRes.setIdCase(caseId);
		cfMgmtListMap = caseFileManagementDao.getSkpTrnInfo(caseFileMgtReq.getIdCase());
		Map<Long, String> result = cfMgmtListMap.stream().collect(LinkedHashMap<Long, String>::new,
				(m, c) -> m.put(c.getIdCaseFileCase(), c.getAddSkpTrn()), (m, u) -> {
				});
		cFMgmntListRes.setCaseFileManagementMap(result);
		return cFMgmntListRes;
	}
}
