package information;
/**
 * This class represents patient's record
 */
class Patient {

	/**
	 * class variables
	 */
    String patientID, name;
    int age;
    String gender, diagnosis, admissionDate, dischargeDate;
    String symptoms, comorbidities, treatmentPlan, outcome;
    String scanId;


    /**
     * Parameterized constructor
     * @param patientID - id assigned to patient 
     * @param name - patient's name
     * @param age - patient's age
     * @param gender - patient's gender
     * @param diagnosis - patient's diagnosis
     * @param admissionDate - admitted date
     * @param dischargeDate - discharge date
     * @param symptoms - patient's symptoms
     * @param comorbidities - patient's comorbidities
     * @param treatmentPlan - patient's treatment plan
     * @param outcome - patient final recovery status
     * @param scanId - scan identification id
     */
    public Patient(String patientID, String name, int age, String gender, String diagnosis, 
                   String admissionDate, String dischargeDate, String symptoms, 
                   String comorbidities, String treatmentPlan, String outcome, String scanId) {
        this.patientID = patientID;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.diagnosis = diagnosis;
        this.admissionDate = admissionDate;
        this.dischargeDate = dischargeDate;
        this.symptoms = symptoms;
        this.comorbidities = comorbidities;
        this.treatmentPlan = treatmentPlan;
        this.outcome = outcome;
        this.scanId = scanId;
    }

    /**
     * Method to display patient information
     */
    public String toString() {
    	
    	String returnString = ""
    			+ "\n" + "Patient ID: " + patientID 
    			+ "\n" + "Name: " + name
    			+ "\n" + "Age: " + age
    			+ "\n" + "Gender: " + gender
    			+ "\n" + "Pre-Diagnosis: " + diagnosis
    			+ "\n" + "Final Diagnosis: " + "_________________"
    			+ "\n" + "Admission Date: " + admissionDate
    			+ "\n" + "Discharge Date: " + dischargeDate
    			+ "\n" + "Symptoms: " + symptoms
    			+ "\n" + "Comorbidities: " + comorbidities
    			+ "\n" + "Treatment Plan: " + treatmentPlan
    			+ "\n" + "Outcome: " + outcome
    			+ "\n" + "Scan Id: " + scanId
    			+ "\n" + "\n-------------------------------------------------------------------\n";
    	
		return returnString;
    	
    }
}
