package us.tx.state.dfps.service.casepackage.service;

import java.util.List;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseDto;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseSearchDto;
import us.tx.state.dfps.service.casepackage.dto.CapsEmailDto;
import us.tx.state.dfps.service.common.request.ActedUponAssgnReq;
import us.tx.state.dfps.service.common.request.CFMgmntReq;
import us.tx.state.dfps.service.common.request.CaseFileMgmtReq;
import us.tx.state.dfps.service.common.request.CasePersonListReq;
import us.tx.state.dfps.service.common.request.CaseSearchInputReq;
import us.tx.state.dfps.service.common.request.CommonEventIdReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.NytdReq;
import us.tx.state.dfps.service.common.request.RecordRetentionDataSaveReq;
import us.tx.state.dfps.service.common.request.WorkingNarrativeReq;
import us.tx.state.dfps.service.common.response.ActedUponAssgnRes;
import us.tx.state.dfps.service.common.response.CFMgmntRes;
import us.tx.state.dfps.service.common.response.CaseFileMgmtSaveRes;
import us.tx.state.dfps.service.common.response.CaseSearchRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.NytdRes;
import us.tx.state.dfps.service.common.response.RecordRetentionSaveRes;
import us.tx.state.dfps.service.common.response.WorkingNarrativeRes;
import us.tx.state.dfps.common.dto.LawEnforcementAgencyInfo;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN20S Class
 * Description: This class is use for retrieving CapsCase
 *
 */
public interface CapsCaseService {

	/**
	 * getCaseByInput
	 * 
	 * Service Name- CCMN20S, DAM- CCMN13D
	 * 
	 * @param caseSearchReq
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */
	public List<CapsCaseSearchDto> getCaseByInput(CaseSearchInputReq caseSearchReq,ErrorDto errorDto) throws DataNotFoundException;

	/**
	 * caseSearch Service Name- CCMN20S
	 * 
	 * @param caseSearchReq
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */
	public CaseSearchRes caseSearch(CaseSearchInputReq caseSearchReq) throws DataNotFoundException;

	/**
	 * Get the List of Nytd Youth History records.
	 * 
	 * @param NytdReq
	 * @return NytdRes
	 * @throws DataNotFoundException
	 * @
	 */
	public NytdRes retrieveNytdYouthHistory(NytdReq nytdReq) throws DataNotFoundException;

	/**
	 * Method returns information from Case table using idCase.
	 * 
	 * @param commonHelperReq
	 * @return CapsCaseDto @
	 */
	public CapsCaseDto getCaseInfo(CommonHelperReq commonHelperReq);

	WorkingNarrativeRes saveWorkingNarrative(WorkingNarrativeReq workingNarrativeReq);

	WorkingNarrativeRes getWorkingNarrative(CommonHelperReq workingNarrativeReq);

	WorkingNarrativeRes createNewNarrativeUsing(WorkingNarrativeReq workingNarrativeReq);

	CommonHelperRes getEvents(CommonEventIdReq eventIdList);

	CommonHelperRes getStageIdsList(CommonHelperReq workingNarrativeReq);

	/**
	 * Method Description: This method is used to retrieve the information for
	 * Case Person List form by passing IdStage as input request
	 * 
	 * 
	 * @param CasePersonListReq
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getCasePersonListForm(CasePersonListReq casePersonListReq);

	/**
	 * 
	 * Method Name: caseFileMgmtSave Method Description: save caseFileMgmt
	 * 
	 * @param CaseFileMgmtReq
	 * @return CaseFileMgmtSaveRes @
	 */
	public CaseFileMgmtSaveRes caseFileMgmtSave(CaseFileMgmtReq caseFileMgmtReq);

	/**
	 * 
	 * Method Name: actedUponAssign Method Description: Updates the IND STAGE
	 * PERS EMP NEW field in the STAGE PERSON LINK table for a certain person
	 * and a certain STAGE.
	 * 
	 * @param actedUponAssgnReq
	 * @return ActedUponAssgnRes @
	 */
	public ActedUponAssgnRes actedUponAssign(ActedUponAssgnReq actedUponAssgnReq);

	/**
	 * 
	 * Method Name: getCFMgmntInfo Method Description: Retrieve the Locating
	 * Information
	 * 
	 * @param searchReq
	 * @return CFMgmntRes @
	 */
	public CFMgmntRes getCFMgmntInfo(CFMgmntReq searchReq);

	/**
	 * 
	 * Method Name: saveRecordRetention Method Description: save RecordRetention
	 * data Ccfc20s
	 * 
	 * @param recordRetentionDataSaveReq
	 * @return RecordRetentionSaveRes @
	 */
	public RecordRetentionSaveRes saveRecordRetention(RecordRetentionDataSaveReq recordRetentionDataSaveReq);

	public String sendEmployeeEmail(Long idEvent, CapsEmailDto capsEmailDto, String hostName);

	/**
	 * Method Name: saveLEInvolvmentAgencyInfo Method Description: save LE Involvement Agency Info
	 * @param agencyInfo
	 * @return LEAgencyInfoSaveRes
	 */
	CommonStringRes saveLEInvolvmentAgencyInfo(LawEnforcementAgencyInfo agencyInfo);

}
