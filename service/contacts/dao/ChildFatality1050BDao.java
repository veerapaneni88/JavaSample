package us.tx.state.dfps.service.contacts.dao;

import java.util.List;
import java.util.Map;

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

/**
 * EJB Name : ChildFatality1050BDao.java
 *
 */
public interface ChildFatality1050BDao {
	/**
	 * This method inserts record into FT_RLS_INFO_RPT_CPS table.
	 * 
	 * @param rlsInfoRptCPS
	 * @return CFTRlsInfoRptCPSValueModBean @
	 */
	public CFTRlsInfoRptCPSValueModBean insertRlsInfoRptCPS(CFTRlsInfoRptCPSValueModBean rlsInfoRptCPS);

	/**
	 * This method updates FT_RLS_INFO_RPT_CPS table.
	 * 
	 * @param rlsInfoRptCPS
	 * @return CFTRlsInfoRptCPSValueModBean @
	 */
	public CFTRlsInfoRptCPSValueModBean updateRlsInfoRptCPS(CFTRlsInfoRptCPSValueModBean rlsInfoRptCPS);

	/**
	 * This method deletes record from FT_RLS_INFO_RPT_CPS table.
	 * 
	 * @param idFtRlsInfoRptCPS
	 * @return long @
	 */
	public long deleteRlsInfoRptCPS(long idFtRlsInfoRptCPS);

	/**
	 * This method deletes record from FT_RLS_INFO_RPT_CPS table.
	 * 
	 * @param idFtInfoCpaOrAlleg
	 * @return long @
	 */
	public long deleteRlsInfoCPA(long idFtInfoCpaOrAlleg);

	/**
	 * This method deletes record from FT_RLS_INFO_RPT_ALLEG_DISP table.
	 * 
	 * @param idFtInfoCpaOrAlleg
	 * @return long @
	 */
	public long deleteFtInfoAllegDisp(long idFtInfoCpaOrAlleg);

	/**
	 * This method inserts record into FT_RLS_INFO_RPT table.
	 * 
	 * @param cftRlsInfoRpt
	 * @return @
	 */
	public long insertCFTRlsInfoRpt(CFTRlsInfoRptValueDto cftRlsInfoRpt);

	/**
	 * This method inserts record into FT_RLS_INFO_RPT_ALLEG_DISP table.
	 * 
	 * @param cftRlsInfoRptAllegDispValueDto
	 * @return int
	 */
	public long insertRlsInfoAllegDisposition(CFTRlsInfoRptAllegDispValueDto cftRlsInfoRptAllegDispValueDto);

	/**
	 * This method inserts record into FT_RLS_INFO_RPT_CPA table.
	 * 
	 * @param cftRlsInfoRptCPAValueDto
	 * @return long
	 *
	 */
	public long saveRlsInfoRptCPA(CFTRlsInfoRptCPAValueDto cftRlsInfoRptCPAValueDto);

	/**
	 * 
	 * Method Name: selectCFTRlsInfoRpt Method Description:This method fetches
	 * data from FT_RLS_INFO_RPT table using idEvent
	 * 
	 * @param idEvent
	 * @return List<FtRlsInfoRptDto>
	 */
	public CFTRlsInfoRptValueDto selectCFTRlsInfoRpt(long idEvent);

	/**
	 * 
	 * Method Name: selectRlsInfoRptCPS Method Description:This method fetches
	 * data from FT_RLS_INFO_RPT_CPS table.Current and Prior History for CPS.
	 * 
	 * @param idFtRlsInfoRpt
	 * @returnList<FtRlsInfoRptCpsDto>
	 */
	public List<CFTRlsInfoRptCPSValueDto> selectRlsInfoRptCPS(long idFtRlsInfoRpt);

	/**
	 * 
	 * Method Name: selectCodeTableRows Method Description:This method fetches
	 * data from code_table_row table using 'ReasonRlngshmnt' nm_table.
	 * 
	 * @return Map<String, String>
	 */
	public Map<String, String> selectCodeTableRows();

	/**
	 * Method Name: selectRlsInfoAllegDispositions Method Description:This
	 * method fetches data from FT_RLS_INFO_RPT_ALLEG_DISP table.Allegations of
	 * Abuse/Neglect in this Home in Last Five Years.
	 * 
	 * @param idFtRlsInfoRpt
	 * @return List<FtRlsInfoRptAllegDispDto>
	 */
	public List<CFTRlsInfoRptAllegDispValueDto> selectRlsInfoAllegDispositions(Long idFtRlsInfoRpt);

	/**
	 * Method Name: selectRlsInfoRptRsrc Method Description:This method fetches
	 * data from FT_RLS_INFO_RPT_RSRC table.
	 * 
	 * @param idFtRlsInfoRpt
	 * @return CFTRlsInfoRptResourceValueBean
	 */
	public CFTRlsInfoRptResourceValueDto selectRlsInfoRptRsrc(Long idFtRlsInfoRpt);

	/**
	 * Method Name: selectRlsInfoRptRsrcVoilations Method Description:This
	 * method fetches data from FT_RLS_INFO_RPT_RSRC_VOLTNS table
	 * 
	 * @param idFtRlsInfoRpt
	 * @return List<FtRlsInfoRptRsrcVoltnsDto>
	 */
	public List<CFTRlsInfoRptRsrcVoltnsValueDto> selectRlsInfoRptRsrcVoilations(Long idFtRlsInfoRpt);

	/**
	 * Method Name: selectRlsInfoRptCPA Method Description:This method fetches
	 * data from FT_RLS_INFO_RPT_CPA table.
	 * 
	 * @param idFtRlsInfoRpt
	 * @return List<FtRlsInfoRptCpaDto>
	 */
	public List<CFTRlsInfoRptCPAValueDto> selectRlsInfoRptCPA(Long idFtRlsInfoRpt);

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
	public Long insertRlsInfoRptCPS(CFTRlsInfoRptCPSValueModBean rlsInfoRptCPS, Long idrlsInfoRpt, String historyType,
			String sdmInfo, int counter);

	/**
	 * 
	 * Method Name: insertRlsInfoAllegDispositionBatch Method Description: This
	 * method inserts record into FT_RLS_INFO_RPT_ALLEG_DISP table.
	 * 
	 * @param rlsInfoAllegDispositions
	 * @param idrlsInfoRpt
	 * @return List<Long>
	 */
	public List<Long> insertRlsInfoAllegDispositionBatch(List<CFTRlsInfoRptAllegDispValueBean> rlsInfoAllegDispositions,
			Long idrlsInfoRpt);

	/**
	 * 
	 * Method Name: insertRlsInfoRptRsrc Method Description: This method inserts
	 * record into FT_RLS_INFO_RPT_RSRC table.
	 * 
	 * @param rlsInfoRptRsrc
	 * @return Long
	 */
	public Long insertRlsInfoRptRsrc(CFTRlsInfoRptResourceValueBean rlsInfoRptRsrc);

	/**
	 * 
	 * Method Name: insertRsrcViolationBatch Method Description: This method
	 * inserts record into FT_RLS_INFO_RPT_RSRC_VOLTNS table.
	 * 
	 * @param rlsInfoRptRsrcVoltns
	 * @param idrlsInfoRpt
	 * @return Long
	 */
	public List<Long> insertRsrcViolationBatch(List<CFTRlsInfoRptRsrcVoltnsValueBean> rlsInfoRptRsrcVoltns,
			Long idrlsInfoRpt);

	/**
	 * 
	 * Method Name: insertRlsInfoRptCPABatch Method Description: This method
	 * inserts record into FT_RLS_INFO_RPT_CPA table.
	 * 
	 * @param rlsInfoRptCPAList
	 * @param idrlsInfoRpt
	 * @return Long
	 */
	public List<Long> insertRlsInfoRptCPABatch(List<CFTRlsInfoRptCPAValueModBean> rlsInfoRptCPAList, Long idrlsInfoRpt);

	public Long updateRlsInfoRptRsrc(CFTRlsInfoRptResourceValueBean rlsInfoRptRsrc);

}
