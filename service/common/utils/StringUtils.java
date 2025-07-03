package us.tx.state.dfps.service.common.utils;

public class StringUtils {

    public static boolean isNullorEmplty(String s){
        if(s.isEmpty() || s == null){
            return true;
        }
        else return false;
    }
}
