/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 11, 2017- 5:30:45 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.casemanagement.service;

import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.ExternalDocumentationAUDReq;
import us.tx.state.dfps.service.common.response.ExternalDocumentationRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 11, 2017- 5:30:45 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface ExternalDocumentService {

	public Long externaldocumentationAUD(ExternalDocumentationAUDReq externalDocumentationAUDReq);

	public ExternalDocumentationRes fetchExternaldocumentation(CommonHelperReq commonHelperReq);

}
