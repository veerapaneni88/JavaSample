package us.tx.state.dfps.service.casepackage.serviceimpl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.RecordsRetention;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dao.CaseFileManagementDao;
import us.tx.state.dfps.service.casepackage.dao.RecordsRetentionDao;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseDto;
import us.tx.state.dfps.service.casepackage.dto.RecordsRetnDestDtlsDto;
import us.tx.state.dfps.service.casepackage.dto.RecordsRetnSaveInDto;
import us.tx.state.dfps.service.casepackage.service.RecordsRetentionService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.RecordsRetentionRtrvReq;
import us.tx.state.dfps.service.common.response.RecordsRetentionRtrvRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.ServiceLayerException;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:
 * RecordsRetentionService Description: This class is doing service
 * Implementation for CaseManageService Mar 24, 2017 - 7:50:07 PM
 */
@Service
@Transactional
public class RecordsRetentionServiceImpl implements RecordsRetentionService {

	@Autowired
	RecordsRetentionDao recordsRetentionDao;

	@Autowired
	CapsCaseDao capsCaseDao;
	
	@Autowired
	CaseFileManagementDao caseFileManagementDao;

	private static final String REC_RETN_SP_FAIL = "Records Retention Save Failed in Stored Proc Call with error code - ";
	
	/**
	 * Method Name:RecordsRetentionRtrvRes Method Description: This service will
	 * retrieve all columns for an ID Case from the RECORDS RETENTION table.
	 * There will be one row for a specified ID Case. Additionally, it will
	 * retrieve a full row from the CAPS CASE table to get the closure date for
	 * case. It calls DAMS: CCMNC5D - CASE SMP and CSES56D - REC RETN RTRV.
	 * 
	 * Service Name - CCFC19S
	 * 
	 * @param recordsRetentionRtrvReq
	 * @return recordsRetentionRtrvRes
	 * 
	 */
	@Override
	@Transactional
	public RecordsRetentionRtrvRes recordsRetentionRtrv(RecordsRetentionRtrvReq recordsRetentionRtrvReq) {

		CapsCaseDto capsCaseDto = null;

		RecordsRetentionRtrvRes recordsRetentionRtrvRes = new RecordsRetentionRtrvRes();

		if (!TypeConvUtil.isNullOrEmpty(recordsRetentionRtrvReq)) {

			if (!TypeConvUtil.isNullOrEmpty(recordsRetentionRtrvReq.getIdCase())) {
				// CCMNC5D
				capsCaseDto = capsCaseDao.getCaseDetails(recordsRetentionRtrvReq.getIdCase());

				if (!TypeConvUtil.isNullOrEmpty(capsCaseDto)) {

					// CSES56D
					recordsRetentionRtrvRes = recordsRetentionDao
							.getRecordsRetentionByCaseId(recordsRetentionRtrvReq.getIdCase());

					if (ObjectUtils.isEmpty(recordsRetentionRtrvRes)) {
						recordsRetentionRtrvRes = new RecordsRetentionRtrvRes();
					}
					if (TypeConvUtil.isNullOrEmpty(recordsRetentionRtrvRes.getDtRecRtnDstryActual())) {

						recordsRetentionRtrvRes.setDtRecRtnDstryActual(ServiceConstants.GENERIC_END_DATE);
					}

					if (TypeConvUtil.isNullOrEmpty(recordsRetentionRtrvRes.getDtRecRtnDstryElig())) {

						recordsRetentionRtrvRes.setDtRecRtnDstryElig(ServiceConstants.GENERIC_END_DATE);
					}

					if (!TypeConvUtil.isNullOrEmpty(capsCaseDto.getDtCaseClosed())) {
						recordsRetentionRtrvRes.setDtCaseClosed(capsCaseDto.getDtCaseClosed());
					}

				}
			}

		}

		return recordsRetentionRtrvRes;
	}

	/**
	 * Method Name: saveRecordsRetention Method Description: This method is used
	 * to do all updated to the RECORDS_RETENTION table. This is the equivalent
	 * of legacy Tuxedo service CCFC51U. The method currently handles only add
	 * and update. It can be changed to add delete functionality too
	 * 
	 * @param recordsRetnSaveInDto
	 * @return void
	 */
	@Override
	public void saveRecordsRetention(RecordsRetnSaveInDto recordsRetnSaveInDto) {
		
		Long idCase = recordsRetnSaveInDto.getIdCase();
		String saveMode = ServiceConstants.BLANK;
		// Get the Destruction Date - Call to proc_CalcRecRetn procedure -
		// Equivalent of Legacy ccmnj8d
		RecordsRetnDestDtlsDto recordsRetnDestDtlsDto = recordsRetentionDao
				.getDestructionDate(idCase);
		Date destructionDateActual = recordsRetnDestDtlsDto.getDtDestruction();
				
		// Destruction Date would have been fetched if the call to the stored
		// procedure was successful. If the destruction date does not come back,
		// it may be an issue with the stored procedure call. Check the else
		// part for the code
		if (!TypeConvUtil.isNullOrEmpty(destructionDateActual)) {
			/*
			 ** Add if/else statement based on value of
			 * recordsRetnSaveInDto->indRuledOutOrAdm. If the case is in Admin
			 * Review (Y), call REQ_FUNC_CD_UPDATE, if the case is not Admin
			 * Review then call REQ_FUNC_CD_ADD.
			 */
			/*
			 ** For the ARI stage, a check is made if any rows are present in the
			 ** Records Retention table for a given case ID. If yes, then the row
			 * is updated else a row is inserted.
			 */
			if (ServiceConstants.YES.equalsIgnoreCase(recordsRetnSaveInDto.getIndRuledOutOrAdm())) {
				// Query the Records Retention table for a particular case ID.
				RecordsRetentionRtrvRes recordsRetentionRtrvRes = recordsRetentionDao
						.getRecordsRetentionByCaseId(idCase);
				// If no record is returned, record should be inserted, else
				// existing record should be updated
				if (ObjectUtils.isEmpty(recordsRetentionRtrvRes)) {
					saveMode = ServiceConstants.ADD;
				} else if (ObjectUtils.isEmpty(recordsRetentionRtrvRes.getIdRecRtnCase())) {
					saveMode = ServiceConstants.ADD;
				} else {
					saveMode = ServiceConstants.UPDATE;
				}
			} else {
				saveMode = ServiceConstants.ADD;
			}
			// Populate the input to Save/Update the Records Retention table
			RecordsRetention recordsRetnEntity = new RecordsRetention();
			recordsRetnEntity.setCdRecRtnRetenType(recordsRetnDestDtlsDto.getCdRecordRetnType());
			CapsCase capsCase = new CapsCase();
			capsCase.setIdCase(idCase);
			recordsRetnEntity.setCapsCase(capsCase);
			recordsRetnEntity.setDtRecRtnDstryActual(destructionDateActual);
			recordsRetnEntity.setDtRecRtnDstryElig(destructionDateActual);
			recordsRetnEntity.setDtLastUpdate(new Date());
			// Based on the save mode, either a new record is inserted into the
			// RECORDS_RETENTION table or an existing record is updated. Instead
			// of the two different methods below, a single method can be
			// written to do saveOrUpdate. Keeping the below code to use already
			// created methods
			if (ServiceConstants.ADD.equalsIgnoreCase(saveMode)) {
				caseFileManagementDao.insertRecordsRetention(recordsRetnEntity);
			} else if (ServiceConstants.UPDATE.equalsIgnoreCase(saveMode)) {
				caseFileManagementDao.updateRecordsRetention(recordsRetnEntity);
			}
		} else {
			if (recordsRetnDestDtlsDto.getErrorCode() != 0) {
				throw new ServiceLayerException(REC_RETN_SP_FAIL + recordsRetnDestDtlsDto.getErrorCode()
						+ ServiceConstants.EMPTY_STRING + recordsRetnDestDtlsDto.getErrorDescription());
			}
		}
	}
}
