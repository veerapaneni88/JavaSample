package us.tx.state.dfps.service.apscareformsnarrative.dao;

import us.tx.state.dfps.common.domain.Care;
import us.tx.state.dfps.service.apscareformsnarrative.dto.ApsCareCategoryDto;
import us.tx.state.dfps.service.apscareformsnarrative.dto.ApsCareDomainDto;
import us.tx.state.dfps.service.apscareformsnarrative.dto.ApsCareFactorDto;

import java.util.List;

public interface ApsCareFormsNarrativeDao {

    /**
     * method to get Care data for idEvent CINVD9D
     * @param idEvent
     * @return
     */
    public Care getApsCareData(Long idEvent);

    /**
     * method to get Aps Care Domain Data for idEvent CINVE1D
     * @param idEvent
     * @return
     */
    public List<ApsCareDomainDto> getApsCareDomaindata(Long idEvent);

    /**
     * method to get Aps Care Category Data for idEvent CINVE2D
     * @param idEvent
     */
    public List<ApsCareCategoryDto> getApsCareCategoryData(Long idEvent);

    /**
     * method to get Aps Care Factor Data for idEvent CINVE3D
     * @param idEvent
     */
    public List<ApsCareFactorDto> getApsCareFactorData(Long idEvent);

}
