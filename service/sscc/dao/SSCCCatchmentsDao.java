package us.tx.state.dfps.service.sscc.dao;

import java.util.TreeMap;

public interface SSCCCatchmentsDao {

    public String getCatchmentsByRegion(String region);

    public TreeMap<String, String> getCatchmentsByRegionMap(String region);
}

