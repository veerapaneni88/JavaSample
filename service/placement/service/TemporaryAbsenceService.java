package us.tx.state.dfps.service.placement.service;

import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.TemporaryAbsenceInfoReq;
import us.tx.state.dfps.service.common.response.PlacementRes;
import us.tx.state.dfps.service.common.response.TemporaryAbsenceEventsRes;
import us.tx.state.dfps.service.common.response.TemporaryAbsenceInfoRes;
import us.tx.state.dfps.service.common.response.TemporaryAbsenceRsrcRes;
import java.util.Date;

public interface TemporaryAbsenceService {

    /**
     * @param commonHelperReq
     * @return
     */
    public TemporaryAbsenceEventsRes getTemporaryAbsenceList(CommonHelperReq commonHelperReq);

    /**
     * @param idStage
     * @param idPlacementTa
     * @return
     */
    public TemporaryAbsenceInfoRes getTARequestDetails(Long idStage, Long idPlacementTa);

    /**
     * @param idResource
     * @return
     */
    public TemporaryAbsenceRsrcRes getResourceDetails(Long idResource);

    /**
     * @param taInfoReq
     * @return
     */
    public TemporaryAbsenceInfoRes saveTaDetailInfo(TemporaryAbsenceInfoReq taInfoReq);

    /**
     * @param placementEventId
     * @return
     */
    public PlacementRes getActiveTAsCountForPlacement(Long placementEventId);

    /**
     * @param taInfoReq
     * @return
     */
    public boolean isTAStartOrEndDtBeforePlcmtStart(TemporaryAbsenceInfoReq taInfoReq);

    /**
     * @param taInfoReq
     * @return
     */
    public boolean isTAStartOrEndDtBetweenRange(TemporaryAbsenceInfoReq taInfoReq);

    /**
     * @param placementTaId
     */
    public void deleteTAInfo(Long placementTaId,Long loginUserId);

    /**
     * @param taInfoReq
     * @return
     */
    public boolean isTAEndDtAfterPlcmtEnd(TemporaryAbsenceInfoReq taInfoReq);

    /**
     * @param taInfoReq
     * @return
     */
    public boolean isPlacementEnded(TemporaryAbsenceInfoReq taInfoReq);

    public TemporaryAbsenceInfoRes getOpenTAForActivePlacement(Long placementEventId);

    public TemporaryAbsenceInfoRes getTemporaryAbsenceByMissingChild(Long idPlacementTa);

    public TemporaryAbsenceInfoRes getTemporaryAbsenceById(Long idPlacementTa);
}
