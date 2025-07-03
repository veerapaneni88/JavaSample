package us.tx.state.dfps.service.IndianChildStatus.dao;

import us.tx.state.dfps.common.domain.IcwaChildTribeLink;
import us.tx.state.dfps.common.domain.IcwaTribeNotif;
import us.tx.state.dfps.common.domain.TribeChildStatus;
import us.tx.state.dfps.web.IndianChildStatus.dto.FederalStateTribeListDto;
import us.tx.state.dfps.web.IndianChildStatus.dto.IcwaInfoListDto;
import us.tx.state.dfps.web.IndianChildStatus.dto.IcwaTribeNotifDto;
import us.tx.state.dfps.web.IndianChildStatus.dto.TribeChildStatusDto;

import java.util.List;

/**
 *
 * Class Description: IndianChildStatusDao Interface July 01, 2022
 */
public interface IndianChildStatusDao {


    /**
      * @param tribeChildStatus
     * @return void
     */
    void saveTribeChildStatus(TribeChildStatus tribeChildStatus);

    /**
     * @param icwaTribeNotif
     * @return void
     */
   void saveTribeNotification(IcwaTribeNotif icwaTribeNotif);

    /**
     * @param icwaChildTribeLink
     * @return void
     */
    void saveIcwaTribeChildLink(IcwaChildTribeLink icwaChildTribeLink);


    /**
     * @param tribeChildStatus
     * @return void
     */
    void updateTribeChildStatus(TribeChildStatus tribeChildStatus);

    /**
     *
     * @param stageId
     * @return TribeChildStatusDto
     */
    TribeChildStatusDto getChildTribeStatusByStageId(Long stageId) ;


    /**
     * @param
     * @return List<FederalStateTribeListDto>
     */
    List<FederalStateTribeListDto> getFederalTribeStateList() ;

    /**
     * @param idTribeChildstatus
     * @return  List<IcwaInfoListDto>
     */
    List<IcwaInfoListDto> getIcwaTribeNotifDetails(Long idTribeChildstatus);

    /**
     * @param idIcwaTribeNotif
     * @return IcwaTribeNotifDto
     */
    IcwaTribeNotifDto getIcwaTribeNotifRecord(Long idIcwaTribeNotif);

 /**
  * @param icwaTribeNotif
  * @return void
  */
    void deleteIcwaTribeNotif(IcwaTribeNotif icwaTribeNotif);

    /**
     * @param icwaChildTribeLink
     * @return void
     */
    void deleteIcwaTribeChildLik(IcwaChildTribeLink icwaChildTribeLink);
}
