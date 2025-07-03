package us.tx.state.dfps.service.pca.service;

import us.tx.state.dfps.service.common.request.PcaApplicationReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: PcaService
 * will have all operation which are mapped to PCA module. Feb 9, 2018- 2:01:02
 * PM © 2017 Texas Department of Family and Protective Services
 */
public interface PcaService {

	/**
	 * Method Description: This method is used to retrieve the information for
	 * ADOPTION ASSISTANCE/PCA DENIAL LETTER by passing IdStage and IdPerson as
	 * input request
	 * 
	 * @param pcaApplicationReq
	 * @return PcaApplicationRes @
	 */
	PreFillDataServiceDto getDenailLetter(PcaApplicationReq pcaApplicationReq);
}
