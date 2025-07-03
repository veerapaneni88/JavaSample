package us.tx.state.dfps.service.placement.dao;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import us.tx.state.dfps.common.domain.PlacementTa;
import us.tx.state.dfps.service.placement.dto.TemporaryAbsenceDto;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;

public interface TemporaryAbsenceDao {

    /**
     * @param idStage
     * @return
     */
    PlacementDto getOpenPlacementForStage(Long idStage);

    /**
     * @param taDto
     * @param cReqFun
     * @return
     */
    TemporaryAbsenceDto saveOrUpdateTaInfo(TemporaryAbsenceDto taDto, String cReqFun);

    /**
     * @param idCase
     * @param idStage
     * @return
     */
    List<TemporaryAbsenceDto> getTemporaryAbsenceList(Long idCase, Long idStage);

    /**
     * @param idPlacementTa
     * @return
     */
    PlacementTa getTemporaryAbsenceById(Long idPlacementTa);

    /**
     * this method will validate if the TA start date or end dt added or updated from UI falls before
     * placement start or end date
     * @param idPlcmtEvent
     * @param taStartDt
     * @return true if TA start date or end date are before plcmt start date
     */
    public boolean isTAStartOrEndDtBeforePlcmtStart(Long idPlcmtEvent, Date taStartDt);

    /**
     * this method will validate if the TA start date or end dt added or updated from UI falls between
     * existing TA
     * @param idPlcmtEvent
     * @param taStartDt
     * @return true if TA start date or end date are between existing TA date range
     */
    public boolean isTAStartOrEndDtBetweenRange(Long idStage, Date dtStart,Date dtEnd, Long idPlacementTa, String temporaryAbsenceType) throws ParseException;

    /**
     * @param stageId
     * @return
     */
    Long getActiveTemporaryAbsencesForActivePlacements(Long stageId);

    /**
     * @param placementEventId
     * @return
     */
    Long getActiveTemporaryAbsencesForActivePlacement(Long placementEventId);

    /**
     * @param idCase
     * @param idStage
     * @return
     */
    Long getPrimaryChildId(Long idCase, Long idStage);

    /**
     * @param stageId
     * @return
     */
    Long checkActivePlacementsCountForStageId(Long stageId);

    /**
     * @param placementTaId
     * @param loginUserId
     */
    void deleteTaInfo(Long placementTaId,Long loginUserId);

    /**
     * @param idPlcmtEvent
     * @param taEndDt
     * @return
     */
    public boolean isTAEndDtAfterPlcmtEnd(Long idPlcmtEvent, Date taEndDt);

    /**
     * @param idPlcmtEvent
     * @return
     */
    public boolean isPlacementEnded(Long idPlcmtEvent);
    
    /**
     * 
     * @param idEvent
     * @return
     */
    public Long getPlacementTaByEventId(Long idEvent);
    
    /**
     * PPM 65209
     * @param idStage
     * @return
     */
    public boolean isOpenTAPlacementPresent(Long idStage);

    public Long getActiveTAForActivePlacement(Long placementEventId);

    public void updateTAStartDate(Long idPlacementTa, Date startDate, Long idUser);

    public void updateTAEndDate(Long idPlacementTa, Date endDate, Long idUser);

    public TemporaryAbsenceDto getActiveTAForPlacement(Long idPlacementEvent);

    public PlacementTa getPlacementTAByMissingChild(Long idChildMissingDtl);

	public TemporaryAbsenceDto getLatestMissingTA(Long idPlcmtEvent);

}
