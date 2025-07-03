package us.tx.state.dfps.service.legalnotice.dao;

import java.util.List;

import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.service.legal.dto.LegalNoticeDtlDto;
import us.tx.state.dfps.service.legal.dto.LegalNoticeRecpntDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * to invoke legalNoticeDaoImpl. June 07, 2018- 1:38:23 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface LegalNoticeDao {
	/**
	 * 
	 * Method Name: getLegalNoticeList Method Description:
	 * 
	 * @param legalNoticeReq
	 * @return
	 */
	public List<LegalNoticeDtlDto> getLegalNoticeList(Long idCase, Long idStage);

	/**
	 * 
	 * Method Name: getNoticesForStage Method Description:
	 * 
	 * @param idStage
	 * @return
	 */
	public List<Long> getLglStsForCaseSameCause(Long idCase, Long idStage);

	/**
	 * 
	 * Method Name: saveLegalNoticeRecpnt Method Description:
	 * 
	 * @param recepnt
	 * @return
	 */
	public ServiceResHeaderDto saveLegalNoticeRecpnt(LegalNoticeRecpntDto recepnt, Long idLastUpdatedBy);

	/**
	 * 
	 * Method Name: fetchLegalNoticeDtl Method Description: This method will
	 * fetch legal notice detail
	 * 
	 * @param idCase
	 * @param idStage
	 * @return LegalNoticeDtlDto
	 */
	public LegalNoticeDtlDto fetchLegalNoticeDtl(Long idCase, Long idStage);

	/**
	 * 
	 * Method Name: saveLegalNoticeDtl Method Description: This method will save
	 * legal notice detail
	 * 
	 * @param legalNoticeReq
	 * @return
	 */
	public ServiceResHeaderDto saveLegalNoticeDtl(LegalNoticeDtlDto legalNoticeDtlDto, Long idUser,
			Boolean generateNotice) ;

	/**
	 * 
	 * Method Name: getLegalActionDtl Method Description: This method will fetch
	 * legal action detail
	 * 
	 * @param idCase
	 * @param idStage
	 * @return LegalNoticeDtlDto
	 */
	public LegalNoticeDtlDto getLegalActionDtl(Long idCase, Long idStage);
	
	/**
	 * 
	 * Method Name: getLegalStatusDtl Method Description: This method will fetch
	 * legal status detail
	 * 
	 * @param idCase
	 * @param idStage
	 * @return LegalNoticeDtlDto
	 */
	public LegalNoticeDtlDto getLegalStatusDtl(Long idCase, Long idStage, LegalNoticeDtlDto legalNoticeDtlDto) ;

}
