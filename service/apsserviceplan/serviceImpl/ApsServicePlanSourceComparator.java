package us.tx.state.dfps.service.apsserviceplan.serviceImpl;

import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanSourceDto;

import java.io.Serializable;
import java.util.Comparator;

public class ApsServicePlanSourceComparator implements Comparator<ApsServicePlanSourceDto>, Serializable {

    public int compare(ApsServicePlanSourceDto source1, ApsServicePlanSourceDto source2) {

        if (null == source1 || null == source2)
            return 0;

        //remove all numeric characters in Object source1
        String strSource1 = source1.toString().replaceAll("\\d", "");
        //remove all numeric characters in Object source2
        String strSource2 = source2.toString().replaceAll("\\d", "");

        if (strSource1.compareToIgnoreCase(strSource2) != 0) {
            return strSource1.compareToIgnoreCase(strSource2);
        } else {
            //remove all non-numeric characters in Object source1
            Integer intObjSource1 = Integer.parseInt("".equals(source1.toString().replaceAll("\\D", ""))
                    ? "0" : source1.toString().replaceAll("\\D", ""));
            //remove all non-numeric characters in Object source2
            Integer intObjSource2 = Integer.parseInt("".equals(source1.toString().replaceAll("\\D", ""))
                    ? "0" : source2.toString().replaceAll("\\D", ""));
            return intObjSource1.compareTo(intObjSource2);
        }
    }
}
