package us.tx.state.dfps.service.apscasereview.dao;

import us.tx.state.dfps.service.apscasereview.ApsCaseReviewContactDto;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewServiceAuthDto;
import us.tx.state.dfps.service.contact.dto.NameDto;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * APS Case Review (Legacy)
 * @author CHITLA
 * Feb, 2022© Texas Department of Family and Protective Services
 */

@Repository
public interface ApsCaseReviewLegacyDao {

    List<Long> getCareEvents(Long idCase);

    List<Long> getOutcomeMatrixEvents(Long idCase);

    List<ApsCaseReviewServiceAuthDto> getDonatedCommunityServicesInfo(Long idCase);

    List<NameDto> getPersonContacted(Long idCase, Long idEvent);

    List<ApsCaseReviewContactDto> getContactsByCase(Long idCase);

    List<ApsCaseReviewContactDto> getContactInformation(Long idCase, Date dtSampleFrom, Date dtSampleTo);

    Long getPrimaryOrHistoricalPrimary(Long idCase, String cdStagePersRole);


}
