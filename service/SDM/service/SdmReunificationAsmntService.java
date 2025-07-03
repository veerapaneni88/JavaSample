/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: Service Interface for SDM Reunification Assessment Page.
 *Jun 12, 2018- 4:40:38 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.SDM.service;

import us.tx.state.dfps.service.common.request.SdmReunificationAsmntFetchReq;
import us.tx.state.dfps.service.common.request.SdmReunificationAsmntReq;
import us.tx.state.dfps.service.common.response.SdmReunificationAsmntFetchRes;
import us.tx.state.dfps.service.common.response.SdmReunificationAsmntSaveRes;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

public interface SdmReunificationAsmntService {

	public SdmReunificationAsmntFetchRes fetchSdmReunificationAsmnt(
			SdmReunificationAsmntFetchReq sdmReunificationAsmntReq);

	public SdmReunificationAsmntSaveRes sdmReunificationAsmntAUD(SdmReunificationAsmntReq sdmReunificationAsmntReq);

	public Boolean isHouseholdHavePendAsmnt(Long idStage, Long idHousehold, Long idEvent);

	public Boolean isParentHavePendAsmnt(Long idStage, Long idParent, Long idEvent);

	public PreFillDataServiceDto fetchSdmReunificationAsmntForm(SdmReunificationAsmntFetchReq sdmReunificationAsmntReq);

}
