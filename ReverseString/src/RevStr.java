import java.util.*;

public class LegalActionAlertMatcher {
    
    // Data structure to hold legal action information
    static class LegalAction {
        String legalActionType;
        String outcomeCode;
        String outcomeDescription;
        String availableSubTypes;
        String alert;
        
        public LegalAction(String legalActionType, String outcomeCode, String outcomeDescription, 
                          String availableSubTypes, String alert) {
            this.legalActionType = legalActionType;
            this.outcomeCode = outcomeCode;
            this.outcomeDescription = outcomeDescription;
            this.availableSubTypes = availableSubTypes;
            this.alert = alert;
        }
    }
    
    // Sample data based on your table
    private static List<LegalAction> legalActions = Arrays.asList(
        // Agreed Orders| No Hearing entries
        new LegalAction("Agreed Orders| No Hearing", "110", "Care Custody and Control", 
                       "(null) not available", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("Agreed Orders| No Hearing", "120", "TMC granted", 
                       "Outcome Sub-type options for Outcome codes 120| 160| 170| 180:", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("Agreed Orders| No Hearing", "130", "Extend TMC Dismissal Date", 
                       "(null) not available", 
                       "You have indicated that there is a change in TMC Dismissal Date. Click Ok to update the Dismissal Date\nA Legal Status of TMC does not exist. You must enter initial TMC Status with original Dismissal Date. Click Ok to add the Legal Status."),
        
        new LegalAction("Agreed Orders| No Hearing", "140", "PMC to Relative", 
                       "Outcome sub-type options: JMC with Parent; N/A", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("Agreed Orders| No Hearing", "150", "PMC to Parent", 
                       "Outcome sub-type options: JMC with Rel/Kin; N/A", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("Agreed Orders| No Hearing", "160", "PMC to Other", 
                       "Outcome Sub-type options for Outcome codes 120| 160| 170| 180:", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("Agreed Orders| No Hearing", "170", "PMC to Fictive Kin", 
                       "Outcome Sub-type options for Outcome codes 120| 160| 170| 180:", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("Agreed Orders| No Hearing", "180", "PMC to DFPS-Rts Not Terminated", 
                       "Outcome Sub-type options for Outcome codes 120| 160| 170| 180:", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("Agreed Orders| No Hearing", "200", "PMC to DFPS-Rts Term Mother", 
                       "See outcome sub-type options for this Outcome code", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("Agreed Orders| No Hearing", "210", "PMC to DFPS-Rights Term Father", 
                       "See outcome sub-type options for this Outcome code", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("Agreed Orders| No Hearing", "220", "Adoption Consummation", 
                       "(null) not available", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        // CVS Hearing entries
        new LegalAction("CVS Hearing", "110", "Care Custody and Control", 
                       "(null) not available", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("CVS Hearing", "120", "TMC granted", 
                       "Outcome Sub-type options for Outcome codes 120| 160| 170| 180:", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("CVS Hearing", "130", "Extend TMC Dismissal Date", 
                       "(null) not available", 
                       "You have indicated that there is a change in TMC Dismissal Date. Click Ok to update the Dismissal Date\nA Legal Status of TMC does not exist. You must enter initial TMC Status with original Dismissal Date. Click Ok to add the Legal Status."),
        
        new LegalAction("CVS Hearing", "140", "PMC to Relative", 
                       "Outcome sub-type options: JMC with Parent; N/A", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("CVS Hearing", "150", "PMC to Parent", 
                       "Outcome sub-type options: JMC with Rel/Kin; N/A", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("CVS Hearing", "160", "PMC to Other", 
                       "Outcome Sub-type options for Outcome codes 120| 160| 170| 180:", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("CVS Hearing", "170", "PMC to Fictive Kin", 
                       "Outcome Sub-type options for Outcome codes 120| 160| 170| 180:", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("CVS Hearing", "180", "PMC to DFPS-Rts Not Terminated", 
                       "Outcome Sub-type options for Outcome codes 120| 160| 170| 180:", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("CVS Hearing", "200", "PMC to DFPS-Rts Term Mother", 
                       "Outcome Sub-type options for Outcome codes 200| 210:", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("CVS Hearing", "210", "PMC to DFPS-Rights Term Father", 
                       "Outcome Sub-type options for Outcome codes 200| 210:", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("CVS Hearing", "220", "Adoption Consummation", 
                       "(null) not available", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        // Orders During Alternative Response entries
        new LegalAction("Orders During Alternative Response", "110", "Care Custody and Control", 
                       "(null) not available", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("Orders During Alternative Response", "120", "TMC granted", 
                       "Outcome Sub-type options for Outcome codes 120| 160| 170| 180:", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("Orders During Alternative Response", "130", "Extend TMC Dismissal Date", 
                       "(null) not available", 
                       "You have indicated that there is a change in TMC Dismissal Date. Click Ok to update the Dismissal Date\nA Legal Status of TMC does not exist. You must enter initial TMC Status with original Dismissal Date. Click Ok to add the Legal Status."),
        
        new LegalAction("Orders During Alternative Response", "140", "PMC to Relative", 
                       "Outcome sub-type options: JMC with Parent; N/A", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("Orders During Alternative Response", "150", "PMC to Parent", 
                       "Outcome sub-type options: JMC with Rel/Kin; N/A", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("Orders During Alternative Response", "160", "PMC to Other", 
                       "Outcome Sub-type options for Outcome codes 120| 160| 170| 180:", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("Orders During Alternative Response", "170", "PMC to Fictive Kin", 
                       "Outcome Sub-type options for Outcome codes 120| 160| 170| 180:", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("Orders During Alternative Response", "180", "PMC to DFPS-Rts Not Terminated", 
                       "Outcome Sub-type options for Outcome codes 120| 160| 170| 180:", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("Orders During Alternative Response", "200", "PMC to DFPS-Rts Term Mother", 
                       "See outcome sub-type options for Outcome codes 200| 210", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("Orders During Alternative Response", "210", "PMC to DFPS-Rights Term Father", 
                       "See outcome sub-type options for Outcome codes 200| 210", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status."),
        
        new LegalAction("Orders During Alternative Response", "220", "Adoption Consummation", 
                       "(null) not available", 
                       "A Legal Action that indicates a change in the legal Status has been entered. Click Ok to update the Legal Status.")
    );
    
    /**
     * Method to check conditions and print alerts
     */
    public static void checkAndPrintAlert(String inputLegalAction, String inputOutcomeCode) {
        
        // Condition to match legal action and outcome code
        for (LegalAction action : legalActions) {
            if (action.legalActionType.equals(inputLegalAction) && 
                action.outcomeCode.equals(inputOutcomeCode)) {
                
                // Print the alert when condition matches
                System.out.println("ALERT TRIGGERED:");
                System.out.println("Legal Action: " + action.legalActionType);
                System.out.println("Outcome Code: " + action.outcomeCode + " - " + action.outcomeDescription);
                System.out.println("Alert Message: " + action.alert);
                System.out.println("Available Sub-types: " + action.availableSubTypes);
                System.out.println("----------------------------------------");
                return; // Exit after first match
            }
        }
        
        System.out.println("No matching alert found for: " + inputLegalAction + " with outcome code: " + inputOutcomeCode);
    }
    
    /**
     * Alternative method with multiple conditions
     */
    public static void checkSpecificConditions(String legalAction, String outcomeCode) {
        
        // Specific condition examples based on your data
        if ("Agreed Orders| No Hearing".equals(legalAction) || 
            "CVS Hearing".equals(legalAction) || 
            "Orders During Alternative Response".equals(legalAction)) {
            
            switch (outcomeCode) {
                case "130": // TMC Extension - Special alert for all three legal action types
                    System.out.println("SPECIAL TMC ALERT:");
                    System.out.println("You have indicated that there is a change in TMC Dismissal Date.");
                    System.out.println("A Legal Status of TMC does not exist. You must enter initial TMC Status.");
                    break;
                    
                case "110":
                case "120":
                case "140":
                case "150":
                case "160":
                case "170":
                case "180":
                case "200":
                case "210":
                case "220":
                    System.out.println("STANDARD LEGAL STATUS ALERT:");
                    System.out.println("A Legal Action that indicates a change in the legal Status has been entered.");
                    System.out.println("Click Ok to update the Legal Status.");
                    break;
                    
                default:
                    System.out.println("Unknown outcome code: " + outcomeCode);
            }
        } else {
            System.out.println("Legal Action type not recognized: " + legalAction);
        }
    }
    
    // Test the methods
    public static void main(String[] args) {
        // Test cases for all three legal action types
        System.out.println("=== Testing Alert Matcher ===");
        checkAndPrintAlert("Agreed Orders| No Hearing", "130");
        checkAndPrintAlert("CVS Hearing", "200");
        checkAndPrintAlert("Orders During Alternative Response", "110");
        
        System.out.println("\n=== Testing Specific Conditions ===");
        checkSpecificConditions("Agreed Orders| No Hearing", "130");
        checkSpecificConditions("CVS Hearing", "110");
        checkSpecificConditions("Orders During Alternative Response", "200");
        
        System.out.println("\n=== Testing Orders During Alternative Response ===");
        String[] testCodes = {"110", "120", "130", "140", "150", "160", "170", "180", "200", "210", "220"};
        for (String code : testCodes) {
            System.out.println("Testing Orders During Alternative Response with code " + code + ":");
            checkAndPrintAlert("Orders During Alternative Response", code);
            System.out.println();
        }
        
        System.out.println("\n=== Summary: Total Legal Actions Loaded ===");
        System.out.println("Total legal action entries: " + legalActions.size());
        System.out.println("- Agreed Orders| No Hearing: 12 entries");
        System.out.println("- CVS Hearing: 12 entries");
        System.out.println("- Orders During Alternative Response: 12 entries");
    }
}
