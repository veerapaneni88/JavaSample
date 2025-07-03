package us.tx.state.dfps.service.approval.service;

import us.tx.state.dfps.service.webservices.gold.dto.GoldNarrativeDto;

public interface GroupBeanService {

   StringBuilder load(GoldNarrativeDto dto, Long group, int depth);

}
