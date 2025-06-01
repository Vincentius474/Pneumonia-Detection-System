package information;
import java.io.*;

/**
 * This class represents a singly linked list data structure
 */
public class SinglyLinkedList {
	
	/**
	 * class variables
	 */
    Node head;
    Node tail;
    
    /**
     * Method to insert a new patient node into the linked list
     * @param patientData data to insert in a node
     */
    public void insert(Patient patientData) {
        Node newNode = new Node(patientData);
        if (head == null) {
            head = newNode;
        } else {
            Node temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = newNode;
        }
        tail = newNode;
    }
    
    /**
     * Method to read data from a file and populate the linked list
     * @param fileName name of the file containing the data
     * @throws IOException an exception thrown/raised
     */
    public void readDataFromFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;

        // Skip header
        br.readLine();

        while ((line = br.readLine()) != null) {
            String[] data = line.split("\\|");

            // Remove leading and trailing spaces for each column
            for (int i = 0; i < data.length; i++) {
                data[i] = data[i].trim();
            }

            // Create a new Patient object using the data
            Patient patient = new Patient(data[0], data[1], Integer.parseInt(data[2]), data[3], 
                                          data[4], data[5], data[6], data[7], data[8], 
                                          data[9], data[10], data[11]);

            // Insert the patient into the linked list
            insert(patient);
        }

        br.close();
    }
    
    /**
     * Search for a patient by ID
     * @param id - patient's id
     * @return - patient's data
     */
    public String searchByPatientID(String id) {
    	
    	String results = null;
        Node temp = head;
        boolean found = false;

        while (temp != null) {
            if (temp.patientData.scanId.equalsIgnoreCase(id)) {
                System.out.println("Patient Found:");
                results = temp.patientData.toString();
                found = true;
                break;
            }
            temp = temp.next;
        }

        if (!found) {
        	results = "System Alert!! No patient found with ID: " + id;
        }
        
        return results;
        
    }
    
}
