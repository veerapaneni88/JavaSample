package us.tx.state.dfps.service.admin.dao;


import us.tx.state.dfps.service.admin.dto.ApsInHomeTasksDto;

import java.util.List;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CLSS25D
 * Description: Apr 12, 2017 - 1:59:28 PM
 */

public interface ApsInhomeTasksDao {

    /**
     *
     * Method Description:legacy service name - CLSS25D
     *
     * @param getIdSvcAuth
     * @return @
     */

    public List<ApsInHomeTasksDto> getInhomeTasks(Long getIdSvcAuth);


    /**
     *
     * @param getIdSvcAuth
     * @return
     */
    public List<ApsInHomeTasksDto> getInhomeTasksBySrvicsId(Long getIdSvcAuth);

    /**
     *
     * @param apsInHomeTasksDtoList
     * @param idSvcAuth
     */
    public void saveAPSInhomeTasks(List<String> apsInHomeTasksDtoList, Long idSvcAuth);
}
