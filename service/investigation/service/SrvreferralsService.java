package us.tx.state.dfps.service.investigation.service;

import us.tx.state.dfps.service.common.request.PcspReq;
import us.tx.state.dfps.service.common.request.SrvreferralsReq;
import us.tx.state.dfps.service.common.response.PcspRes;
import us.tx.state.dfps.service.common.response.SrvreferralsRes;

public interface SrvreferralsService {

	/**
	 * 
	 * Method Description: This Method will retrieve data from the event,
	 * cps_checklist and cps_checklist_item tables. It uses the event id and
	 * stage id for existing records and a null event id for new records.
	 * Service Name: CINV54S
	 * 
	 * @param srvrflReq
	 * @return SrvreferralsRes @
	 */
	SrvreferralsRes getSrvrflInfo(SrvreferralsReq srvrflReq);

	/**
	 * 
	 * Method Description: This Method will insert or update the event and
	 * cps_checklist tables. Also it can delete or add rows to the
	 * cps_checklist_item table. Service Name: CINV55S
	 * 
	 * @param srvrflReq
	 * @return SrvreferralsRes @
	 */
	SrvreferralsRes saveOrUpdateSrvrflInfo(SrvreferralsReq srvrflReq);

	/**
	 * Retrieves the parental child safety placement details from the
	 * CHILD_SAFETY_PLCMT, PERSON, STAGE tables. Service Name: PCSPEjb
	 * 
	 * @param pcspReq
	 * @return PcspRes @
	 */
	PcspRes displayPCSPList(PcspReq pcspReq);

}
