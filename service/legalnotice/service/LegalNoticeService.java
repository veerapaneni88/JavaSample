package us.tx.state.dfps.service.legalnotice.service;

import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.service.common.request.LegalNoticeFormReq;
import us.tx.state.dfps.service.common.request.LegalNoticeReq;
import us.tx.state.dfps.service.common.request.MailDateSaveReq;
import us.tx.state.dfps.service.common.response.LegalNoticeListRes;
import us.tx.state.dfps.service.common.response.LegalNoticeRes;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * invoke to retrieve and save Legal Notice detail June 07, 2018- 1:34:02 PM ©
 * 2017 Texas Department of Family and Protective Services.
 */
public interface LegalNoticeService {
	/**
	 * 
	 * Method Name: getLegalNoticeList Method Description:
	 * 
	 * @param legalNoticeReq
	 * @return
	 */
	public LegalNoticeListRes getLegalNoticeList(LegalNoticeReq legalNoticeReq);

	/**
	 * 
	 * Method Name: getLegalNoticeForm Method Description:
	 * 
	 * @param legalNoticeReq
	 * @return
	 */
	public PreFillDataServiceDto getLegalNoticeForm(LegalNoticeFormReq legalNoticeFormReq);

	/**
	 * 
	 * Method Name: saveMailedDate Method Description: This method will save
	 * mailed date Method Description:
	 * 
	 * @param recepnt
	 * @return
	 */
	public ServiceResHeaderDto saveMailedDate(MailDateSaveReq mailDateSaveReq);

	/**
	 * 
	 * Method Name: fetchLegalNoticeDtl Method Description: This method will
	 * fetch legal notice detail
	 * 
	 * @param legalNoticeReq
	 * @return
	 */
	public LegalNoticeRes fetchLegalNoticeDtl(LegalNoticeReq legalNoticeReq);

	/**
	 * 
	 * Method Name: saveLegalNoticeDtl Method Description: This method will save
	 * legal notice detail
	 * 
	 * @param legalNoticeReq
	 * @return
	 */
	public ServiceResHeaderDto saveLegalNoticeDtl(LegalNoticeReq legalNoticeReq);

}
