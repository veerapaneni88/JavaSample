package us.tx.state.dfps.service.IndianChildStatus.service;

import us.tx.state.dfps.service.common.request.IndianChildStatusInfoReq;
import us.tx.state.dfps.service.common.response.IndianChildStatusInfoRes;


/**
 *
 * IndianChildStatusService 2022 Texas Department of
 * Family and Protective Services
 */
public interface IndianChildStatusService {

    /**
     *
     * @param IndianChildStatusInfoReq
     * @return IndianChildStatusInfoRes
     */
   IndianChildStatusInfoRes  saveIndianChildStatusDetails(IndianChildStatusInfoReq indianChildStatusInfoReq);

    /**
     *
     * @param IndianChildStatusInfoReq
     * @return IndianChildStatusInfoRes
     */
   IndianChildStatusInfoRes getIndianChildStatusDetails(IndianChildStatusInfoReq indianChildStatusInfoReq);

    /**
     *
     * @param IndianChildStatusInfoReq
     * @return IndianChildStatusInfoRes
     */
    IndianChildStatusInfoRes getFederalTribeStateList(IndianChildStatusInfoReq indianChildStatusInfoReq);


    /**
     *
     * @param IndianChildStatusInfoReq
     * @return IndianChildStatusInfoRes
     */
    IndianChildStatusInfoRes  saveIndianTribeNotification(IndianChildStatusInfoReq indianChildStatusInfoReq);


    /**
     *
     * @param IndianChildStatusInfoReq
     * @return IndianChildStatusInfoRes
     */
    IndianChildStatusInfoRes  getIcwaTribeNotifDetails(IndianChildStatusInfoReq indianChildStatusInfoReq);


    /**
     *
     * @param IndianChildStatusInfoReq
     * @return IndianChildStatusInfoRes
     */
    IndianChildStatusInfoRes  getIcwaTribeNotifRecord(IndianChildStatusInfoReq indianChildStatusInfoReq);

    /**
      * @param IndianChildStatusInfoReq
     * @return IndianChildStatusInfoRes
      */
    IndianChildStatusInfoRes deleteIcwaTribeNotif(IndianChildStatusInfoReq indianChildStatusInfoReq);
}
