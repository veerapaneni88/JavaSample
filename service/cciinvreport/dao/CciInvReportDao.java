package us.tx.state.dfps.service.cciinvreport.dao;


import us.tx.state.dfps.common.domain.AfcarsResponse;
import us.tx.state.dfps.service.cciinvReport.dto.CciInvContactDto;
import us.tx.state.dfps.service.cciinvReport.dto.CciInvIntakePersonDto;
import us.tx.state.dfps.service.cciinvReport.dto.CciInvLetterDto;
import us.tx.state.dfps.service.cciinvReport.dto.CciInvReportPersonDto;
import us.tx.state.dfps.service.person.dto.CharacteristicsDto;

import java.util.Date;
import java.util.List;

public interface CciInvReportDao {

    public List<CciInvReportPersonDto> getPrincipals(Long idStage, String cdStagePersType);

    public List<CciInvReportPersonDto> getPersonSplInfo(Long idStage);

    public List<CharacteristicsDto> getColCharacteristicsByStage(Long idStage);

    public List<AfcarsResponse> getColAfcarsCharacteristicsByStage(Long idStage);

    public List<CharacteristicsDto> getPrnCharacteristicsByStage(Long idStage);

    public List<AfcarsResponse> getPrnAfcarsCharacteristicsByStage(Long idStage);

    public Date getDtInvInitiatedByStage(Long idCase, Long idStage);

    public List<CciInvIntakePersonDto> getIntakes(Long stageId) ;

    public List<CciInvContactDto> getContactList(Long idStage, String mergedStages);

    public List<CciInvReportPersonDto> getPrincCollatList(Long idEvent, Long idStage);

    public List<CciInvLetterDto> getLetterDetailList(Long idStage, Long idCase);

}
