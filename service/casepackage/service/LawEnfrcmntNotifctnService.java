package us.tx.state.dfps.service.casepackage.service;

import us.tx.state.dfps.service.common.request.LawEnfrcmntNotifctnReq;
import us.tx.state.dfps.service.common.response.LawEnfrcmntNotifctnRes;

public interface LawEnfrcmntNotifctnService {

	/**
	 * 
	 * Method Description: This Method will add row in the LAW_ENFRCMNT_NOTIFCTN
	 * table given ID_RESOURCE
	 * 
	 * @param LawEnfrcmntNotifctnReq
	 * @return LawEnfrcmntNotifctnRes @
	 */

	public LawEnfrcmntNotifctnRes saveLawEnforcementNotifcn(LawEnfrcmntNotifctnReq lawEnfrcmntNotifctnReq);

	/**
	 * 
	 * Method Description: This Method will fetch rows in the
	 * LAW_ENFRCMNT_NOTIFCTN table given ID_STAGE
	 * 
	 * @param LawEnfrcmntNotifctnReq
	 * @return LawEnfrcmntNotifctnRes @
	 */

	public LawEnfrcmntNotifctnRes getLawEnforcementNotifctnList(LawEnfrcmntNotifctnReq lawEnfrcmntNotifctnReq);

}
