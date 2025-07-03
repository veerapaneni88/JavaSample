package us.tx.state.dfps.service.adminreviewforms.dao;

import us.tx.state.dfps.service.workload.dto.AdminReviewDto;


public interface AdminReviewFormsDao {


    public AdminReviewDto getNotificationRequest(Long id);

    //CSE65D
    public AdminReviewDto getAdminReviewByEventId(Long idEvent);

}

