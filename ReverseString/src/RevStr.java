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
     * Enhanced method with multiple conditions including sub-type validation
     */
    public static void checkSpecificConditionsWithSubTypes(String legalAction, String outcomeCode, String selectedSubType) {
        
        // Check if legal action type is valid
        if ("Agreed Orders| No Hearing".equals(legalAction) || 
            "CVS Hearing".equals(legalAction) || 
            "Orders During Alternative Response".equals(legalAction)) {
            
            // Validate sub-type based on outcome code
            boolean isValidSubType = validateSubType(outcomeCode, selectedSubType);
            
            if (!isValidSubType) {
                System.out.println("INVALID SUB-TYPE ERROR:");
                System.out.println("The selected sub-type '" + selectedSubType + "' is not valid for outcome code: " + outcomeCode);
                System.out.println("Valid sub-types for this outcome code: " + getValidSubTypes(outcomeCode));
                return;
            }
            
            switch (outcomeCode) {
                case "130": // TMC Extension - Special alert for all three legal action types
                    System.out.println("SPECIAL TMC ALERT:");
                    System.out.println("You have indicated that there is a change in TMC Dismissal Date.");
                    System.out.println("A Legal Status of TMC does not exist. You must enter initial TMC Status.");
                    System.out.println("Selected Sub-type: " + (selectedSubType != null ? selectedSubType : "None available"));
                    break;
                    
                case "110":
                case "220":
                    System.out.println("STANDARD LEGAL STATUS ALERT:");
                    System.out.println("A Legal Action that indicates a change in the legal Status has been entered.");
                    System.out.println("Click Ok to update the Legal Status.");
                    System.out.println("Selected Sub-type: " + (selectedSubType != null ? selectedSubType : "None available"));
                    break;
                    
                case "120":
                case "160":
                case "170":
                case "180":
                    System.out.println("STANDARD LEGAL STATUS ALERT:");
                    System.out.println("A Legal Action that indicates a change in the legal Status has been entered.");
                    System.out.println("Click Ok to update the Legal Status.");
                    System.out.println("Selected Sub-type: " + selectedSubType);
                    System.out.println("Valid sub-types for codes 120|160|170|180: Multiple options available");
                    break;
                    
                case "140":
                case "150":
                    System.out.println("STANDARD LEGAL STATUS ALERT:");
                    System.out.println("A Legal Action that indicates a change in the legal Status has been entered.");
                    System.out.println("Click Ok to update the Legal Status.");
                    System.out.println("Selected Sub-type: " + selectedSubType);
                    System.out.println("Valid sub-types: JMC with Parent; JMC with Rel/Kin; N/A");
                    break;
                    
                case "200":
                case "210":
                    System.out.println("STANDARD LEGAL STATUS ALERT:");
                    System.out.println("A Legal Action that indicates a change in the legal Status has been entered.");
                    System.out.println("Click Ok to update the Legal Status.");
                    System.out.println("Selected Sub-type: " + selectedSubType);
                    System.out.println("Valid sub-types for codes 200|210: Specific options available");
                    break;
                    
                default:
                    System.out.println("Unknown outcome code: " + outcomeCode);
            }
        } else {
            System.out.println("Legal Action type not recognized: " + legalAction);
        }
    }
    
    /**
     * Validate if the selected sub-type is valid for the given outcome code
     */
    private static boolean validateSubType(String outcomeCode, String selectedSubType) {
        if (selectedSubType == null) {
            // Check if sub-type is required for this outcome code  
            return !isSubTypeRequired(outcomeCode);
        }
        
        switch (outcomeCode) {
            case "110":
            case "130":
            case "220":
                // These codes have "(null) not available" - no sub-type should be selected
                return false;
                
            case "120":
            case "160":
            case "170":
            case "180":
                // These codes have "Outcome Sub-type options for Outcome codes 120|160|170|180"
                return isValidForGroup120(selectedSubType);
                
            case "140":
                // "JMC with Parent; N/A"
                return selectedSubType.equals("JMC with Parent") || selectedSubType.equals("N/A");
                
            case "150":
                // "JMC with Rel/Kin; N/A"
                return selectedSubType.equals("JMC with Rel/Kin") || selectedSubType.equals("N/A");
                
            case "200":
            case "210":
                // These have specific sub-type options for 200|210
                return isValidForGroup200(selectedSubType);
                
            default:
                return false;
        }
    }
    
    /**
     * Check if sub-type is required for the outcome code
     */
    private static boolean isSubTypeRequired(String outcomeCode) {
        switch (outcomeCode) {
            case "110":
            case "130":
            case "220":
                return false; // "(null) not available"
            default:
                return true; // Sub-type is required for other codes
        }
    }
    
    /**
     * Validate sub-types for outcome codes 120, 160, 170, 180
     */
    private static boolean isValidForGroup120(String subType) {
        // This would contain the actual valid sub-types for this group
        // For demo purposes, accepting common sub-types
        String[] validSubTypes = {"Standard", "Modified", "Temporary", "Extended"};
        for (String valid : validSubTypes) {
            if (valid.equals(subType)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Validate sub-types for outcome codes 200, 210
     */
    private static boolean isValidForGroup200(String subType) {
        // This would contain the actual valid sub-types for this group
        String[] validSubTypes = {"Rights Terminated", "Voluntary Relinquishment", "Court Ordered"};
        for (String valid : validSubTypes) {
            if (valid.equals(subType)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get valid sub-types for display in error messages
     */
    private static String getValidSubTypes(String outcomeCode) {
        switch (outcomeCode) {
            case "110":
            case "130":
            case "220":
                return "(null) not available";
                
            case "120":
            case "160":
            case "170":
            case "180":
                return "Standard, Modified, Temporary, Extended";
                
            case "140":
                return "JMC with Parent; N/A";
                
            case "150":
                return "JMC with Rel/Kin; N/A";
                
            case "200":
            case "210":
                return "Rights Terminated, Voluntary Relinquishment, Court Ordered";
                
            default:
                return "Unknown";
        }
    }
    
    /**
     * Original method with multiple conditions (kept for backward compatibility)
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
        
        System.out.println("\n=== Testing Original Specific Conditions ===");
        checkSpecificConditions("Agreed Orders| No Hearing", "130");
        checkSpecificConditions("CVS Hearing", "110");
        checkSpecificConditions("Orders During Alternative Response", "200");
        
        System.out.println("\n=== Testing Enhanced Conditions with Sub-Types ===");
        
        // Test valid sub-types
        System.out.println("--- Valid Sub-Type Tests ---");
        checkSpecificConditionsWithSubTypes("CVS Hearing", "140", "JMC with Parent");
        checkSpecificConditionsWithSubTypes("CVS Hearing", "150", "JMC with Rel/Kin");
        checkSpecificConditionsWithSubTypes("CVS Hearing", "120", "Standard");
        checkSpecificConditionsWithSubTypes("CVS Hearing", "200", "Rights Terminated");
        
        System.out.println("\n--- Invalid Sub-Type Tests ---");
        // Test invalid sub-types
        checkSpecificConditionsWithSubTypes("CVS Hearing", "110", "Invalid Sub-Type"); // Should fail - no sub-type allowed
        checkSpecificConditionsWithSubTypes("CVS Hearing", "140", "Wrong Sub-Type"); // Should fail - invalid sub-type
        checkSpecificConditionsWithSubTypes("CVS Hearing", "120", "Invalid Option"); // Should fail - invalid sub-type
        
        System.out.println("\n--- Null Sub-Type Tests ---");
        // Test null sub-types
        checkSpecificConditionsWithSubTypes("CVS Hearing", "110", null); // Should pass - no sub-type required
        checkSpecificConditionsWithSubTypes("CVS Hearing", "140", null); // Should fail - sub-type required
        
        System.out.println("\n=== Summary: Total Legal Actions Loaded ===");
        System.out.println("Total legal action entries: " + legalActions.size());
        System.out.println("- Agreed Orders| No Hearing: 12 entries");
        System.out.println("- CVS Hearing: 12 entries");
        System.out.println("- Orders During Alternative Response: 12 entries");
    }
}
