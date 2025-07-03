/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: Interface for Data Access object for Sdm Reunification Assessment Page.
 *Jun 12, 2018- 5:12:50 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.SDM.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.reunificationAssessment.dto.ReunificationAssessmentDto;
import us.tx.state.dfps.service.reunificationAssessment.dto.ReunificationAssessmentQuestionsDto;
import us.tx.state.dfps.service.workload.dto.EventDto;

public interface SdmReunificationAsmntDao {

	public Long addSdmReunificationAssessment(ReunificationAssessmentDto reunificatoinAsmntDto);

	public ReunificationAssessmentDto fetchSdmReunificationAssessment(Long idEvent);

	public Long updateSdmReunificationAssessment(ReunificationAssessmentDto reunificationAsmntDto);

	public void deleteSdmReunificationAssessment(ReunificationAssessmentDto reunificationAssessmentDto);

	public List<ReunificationAssessmentQuestionsDto> fetchQuestionListForAsmnt(ReunificationAssessmentDto asmentDto);

	public Long getReunificationAsmntLkpVersion();

	public List<EventDto> getidAsmntEventsForStageAndHshld(Long idStage, Long idPerson);

	public Boolean anyChildWithRtnHome(Long idEvent);

	public List<EventDto> getidAsmntEventsForStageAndParent(Long idStage, Long idParent);

	public Date getLatestAssessmentDateInStage(Long idStage, Long idEvent);

}
