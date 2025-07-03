package us.tx.state.dfps.service.icpc.service;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.IcpcDocUploadReq;
import us.tx.state.dfps.service.common.response.IcpcDocUploadRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service for
 * ICPC Document upload Oct 6, 2017- 3:03:42 PM © 2017 Texas Department of
 * Family and Protective Services
 */

public interface IcpcDocumentUploadService {

	/**
	 * method name: insertPRTTask Description: This method saves PRT Task.
	 * 
	 * @param IcpcDocUploadReq
	 * @return IcpcDocUploadRes
	 * @throws InvalidRequestException
	 * @
	 */
	public IcpcDocUploadRes fetchDocument(IcpcDocUploadReq icpcDocUploadReq);

	public IcpcDocUploadRes saveDocument(IcpcDocUploadReq icpcDocUploadReq);

	void updateTaskComplete(Long idEvent, String cdType, Long userId);

}
