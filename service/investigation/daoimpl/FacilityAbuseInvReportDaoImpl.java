package us.tx.state.dfps.service.investigation.daoimpl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.casepackage.dao.CaseDao;
import us.tx.state.dfps.service.casepackage.dto.CaseInfoDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.extreq.ExtreqDto;
import us.tx.state.dfps.service.forms.dto.ApsFacilNarrDto;
import us.tx.state.dfps.service.investigation.dao.FacilityAbuseInvReportDao;
import us.tx.state.dfps.service.investigation.dto.ContactNarrDto;
import us.tx.state.dfps.service.person.daoimpl.Base64;
import us.tx.state.dfps.service.workload.dto.ExternalDocumentDetailDto;
import us.tx.state.dfps.service.workload.dto.StagePersDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Makes calls
 * to DB and sends data to service Apr 30, 2018- 3:16:57 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class FacilityAbuseInvReportDaoImpl implements FacilityAbuseInvReportDao {

	@Autowired
	private SessionFactory sessionFactory;	
	
	@Autowired
	CaseDao caseDao;

	@Value("${FacilityAbuseInvReportDaoImpl.getContactNarr}")
	private String getContactNarrSql;

	@Value("${FacilityAbuseInvReportDaoImpl.getExtDocAlleg}")
	private String getExtDocAllegSql;

	@Value("${FacilityAbuseInvReportDaoImpl.getPrimaryWorker}")
	private String getPrimaryWorkerSql;

	@Value("${FacilityAbuseInvReportDaoImpl.getApprovalDate}")
	private String getApprovalDateSql;

	@Value("${FacilityAbuseInvReportDaoImpl.getApprover}")
	private String getApproverSql;
	
	@Value("${FacilityAbuseInvReportDaoImpl.getStageIdForDataFix}")
	private String getStageIdForDataFixSql;
	
	// Warranty Defect Fix - 11775 - To execute the Data Fix for only 3 Cases
	@Value("${FacilityAbuseInvReportDaoImpl.getExcludeDataFixCaseId}")
	private String getExcludeDataFixCaseId;
	
	// SD 56377 : get APS Facility Narrative by stage
	@Value("${FacilityAbuseInvReportDaoImpl.getApsFacilNarr}")
	private String getApsFacilNarrSql;
	
	// APS_FACIL_NARR  Added the code for  SD 56377: R2 Sev 5 Defect 10107
	// SD 56377 :update APS Facility Narrative e.g. with new evidence list
	@Value("${FacilityAbuseInvReportDaoImpl.updateApsFacilNarr}")
	private String updateApsFacilNarrSql;

	//Start Added the code for  SD 56377: R2 Sev 5 Defect 10107
	// SD 56377 :this method return APS Facility Narrative for a given stage id. 
	//   it also converts blob to byte array.
	@Override
	public ApsFacilNarrDto getApsFacilNarr(Long idStage) throws SQLException {
		ApsFacilNarrDto  apsFacilNarrDto  = null; //new ApsFacilNarrDto();
		byte[] theBytes = null;
		Blob blob = null;
		Query query =  sessionFactory.getCurrentSession()
				.createSQLQuery(getApsFacilNarrSql)
				.addScalar("idDocumentTemplate", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("documentBlob", StandardBasicTypes.BLOB)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(ApsFacilNarrDto.class));
		apsFacilNarrDto = (ApsFacilNarrDto) query.uniqueResult();
		
		if(!ObjectUtils.isEmpty(apsFacilNarrDto)) {
			blob = apsFacilNarrDto.getDocumentBlob();
			if(!ObjectUtils.isEmpty(blob) && 0<blob.length()) {
				theBytes = blob.getBytes(1L, (int) blob.length());
				apsFacilNarrDto.setNarrativeBytes(theBytes);
			}
		}
		return apsFacilNarrDto;
	}
	
	// Added the code for  SD 56377: R2 Sev 5 Defect 10107
	// This method will update the docuemntBlob in APS Narrative 
	public void updateApsFacilNarr(byte[] document, Long idStage, Date dtLastUpdate) throws SQLException {
		Query queryUpdateApsFacilNarr = sessionFactory.getCurrentSession().createQuery(updateApsFacilNarrSql);
			Blob documentBlob = new javax.sql.rowset.serial.SerialBlob(document);
			queryUpdateApsFacilNarr.setParameter("documentBlob", documentBlob);
			queryUpdateApsFacilNarr.setParameter("idStage", idStage);
			queryUpdateApsFacilNarr.setParameter("dtLastUpdate", dtLastUpdate); ;
			queryUpdateApsFacilNarr.executeUpdate();
		}


	
	/**
	 * Method Name: getContactNarr Method Description: This method retreives
	 * contact narr (DAM: CLSC13D)
	 * 
	 * @param idStage
	 * @param dtSearchDateFrom
	 * @param dtSearchDateTo
	 * @return List<ContactNarrDto>
	 */
	@Override
	public List<ContactNarrDto> getContactNarr(Long idStage, Date dtSearchDateFrom, Date dtSearchDateTo) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getContactNarrSql)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("dtLastUpdateContact", StandardBasicTypes.DATE)
				.addScalar("idContactWorker", StandardBasicTypes.LONG)
				.addScalar("idContactStage", StandardBasicTypes.LONG)
				.addScalar("dtContactOccurred", StandardBasicTypes.DATE)
				.addScalar("dtCntctNextSummSue", StandardBasicTypes.DATE)
				.addScalar("indContactAttempted", StandardBasicTypes.STRING)
				.addScalar("cdContactType", StandardBasicTypes.STRING)
				.addScalar("cdContactPurpose", StandardBasicTypes.STRING)
				.addScalar("cdContactLocation", StandardBasicTypes.STRING)
				.addScalar("cdContactMethod", StandardBasicTypes.STRING)
				.addScalar("cdContactOthers", StandardBasicTypes.STRING)
				.addScalar("dtCntctMnthlySummBeg", StandardBasicTypes.DATE)
				.addScalar("dtCntctMnthlySummEnd", StandardBasicTypes.DATE)
				.addScalar("dtContactApprv", StandardBasicTypes.DATE).addScalar("idName", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdateName", StandardBasicTypes.DATE).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("indNameInvalid", StandardBasicTypes.STRING)
				.addScalar("nmNameFirst", StandardBasicTypes.STRING)
				.addScalar("nmNameMiddle", StandardBasicTypes.STRING).addScalar("nmNameLast", StandardBasicTypes.STRING)
				.addScalar("indNamePrimary", StandardBasicTypes.STRING)
				.addScalar("cdNameSuffix", StandardBasicTypes.STRING)
				.addScalar("dtNameStartDate", StandardBasicTypes.DATE)
				.addScalar("dtNameEndDate", StandardBasicTypes.DATE).setParameter("idStage", idStage)
				.setParameter("dtSearchDateFrom", DateUtils.stringDt(dtSearchDateFrom))
				.setParameter("dtSearchDateTo", DateUtils.stringDt(dtSearchDateTo))
				.setParameter("dtMaxDate", DateUtils.stringDt(ServiceConstants.MAX_DATE))
				.setResultTransformer(Transformers.aliasToBean(ContactNarrDto.class));
		return (List<ContactNarrDto>) query.list();
	}

	/**
	 * Method Name: getExtDocAlleg Method Description: Retrieves all info about
	 * Ext Documentation Allegation based upon idCase (DAM: CLSC22D)
	 * 
	 * @param idCase
	 * @param dtSearchDateFrom
	 * @param dtSearchDateTo
	 * @return List<ExternalDocumentDetailDto>
	 */
	@Override
	public List<ExternalDocumentDetailDto> getExtDocAlleg(Long idCase, Date dtSearchDateFrom, Date dtSearchDateTo) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getExtDocAllegSql)
				.addScalar("dtExtDocObtained", StandardBasicTypes.DATE).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("extDocLocation", StandardBasicTypes.STRING)
				.addScalar("cdExtDocType", StandardBasicTypes.STRING)
				.addScalar("extDocDetails", StandardBasicTypes.STRING)
				.addScalar("idExtDocumentation", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).setParameter("idCase", idCase)
				.setParameter("dtSearchDateFrom", DateUtils.stringDt(dtSearchDateFrom))
				.setParameter("dtSearchDateTo", DateUtils.stringDt(dtSearchDateTo))
				.setResultTransformer(Transformers.aliasToBean(ExternalDocumentDetailDto.class));
		return (List<ExternalDocumentDetailDto>) query.list();
	}

	/**
	 * Method Name: getPrimaryWorker Method Description: Gets name of primary
	 * worker plus other information (DAM: CCMN30D)
	 * 
	 * @param idStage
	 * @return StagePersDto
	 */
	@Override
	public StagePersDto getPrimaryWorker(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPrimaryWorkerSql)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("cdStageCnty", StandardBasicTypes.STRING).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("idStagePersonLink", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(StagePersDto.class));
		List<StagePersDto> list = (List<StagePersDto>) query.list();
		return ObjectUtils.isEmpty(list) ? null : list.get(0);
	}

	/**
	 * Method Name: getApprovalDate Method Description: Selects Most Recent
	 * Approval Date for a given Approval ID (DAM: CSESF5D)
	 * 
	 * @param idApproval
	 * @param cdApproversStatus
	 * @return Date
	 */
	@Override
	public Date getApprovalDate(Long idApproval, String cdApproversStatus) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getApprovalDateSql)
				.setParameter("idApproval", idApproval).setParameter("cdApproversStatus", cdApproversStatus);
		return (Date) query.uniqueResult();
	}

	/**
	 * Method Name: getApprover Method Description: Return the approver name
	 * associated with a particular EventID (DAM: CSECF0D)
	 * 
	 * @param idEvent
	 * @return ExtreqDto
	 */
	@Override
	public ExtreqDto getApprover(Long idEvent) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getApproverSql)
				.addScalar("idApproval", StandardBasicTypes.LONG).addScalar("idApprovalPerson", StandardBasicTypes.LONG)
				.addScalar("dtApprovalDate", StandardBasicTypes.DATE).addScalar("idApprovers", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdApproversStatus", StandardBasicTypes.STRING)
				.addScalar("txtApproversCmnts", StandardBasicTypes.STRING)
				.addScalar("cdEmployeeClass", StandardBasicTypes.STRING)
				.addScalar("txtEmployeeClass", StandardBasicTypes.STRING)
				.addScalar("nmEmployeeFirst", StandardBasicTypes.STRING)
				.addScalar("nmEmployeeMiddle", StandardBasicTypes.STRING)
				.addScalar("nmEmployeeLast", StandardBasicTypes.STRING).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(ExtreqDto.class));
		List<ExtreqDto> list = (List<ExtreqDto>) query.list();
		return ObjectUtils.isEmpty(list) ? null : list.get(0);
	}
	
	
	
	/**
	 * Method Name: getStageIdForDataFix Method Description: Return the idStage to Launch the 
	 * APS Abuse and Neglect in Editable Mode
	 *  
	 * @param idStage
	 * @return Long
	 */
	public Long getStageIdForDataFix(Long idStage) {
		Long stageIdForDataFix=ServiceConstants.Zero_Value;
		/*
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getStageIdForDataFixSql)
				.setParameter("idStage", idStage);
		BigDecimal outputStage=(BigDecimal) query.uniqueResult();		
		if(!ObjectUtils.isEmpty(outputStage))
		{
			stageIdForDataFix=idStage;
		}
		*/
		// Warranty Defect Fix - 11775 - To execute the Data Fix for only 3 Cases
		String excludedDataFixCaseId=getExcludeDataFixCaseId;		
		excludedDataFixCaseId=excludedDataFixCaseId.replaceAll("\t", "");		
		String[] DataFixCaseId=excludedDataFixCaseId.split(",");				
		CaseInfoDto caseInfoDto=caseDao.getCaseInfo(idStage);		
		for(String caseId:DataFixCaseId)
		{
			if(caseId.equals(caseInfoDto.getIdCase().toString()))
			{
				stageIdForDataFix=idStage;
			}			
		}		
		
		return stageIdForDataFix;
	}

}
