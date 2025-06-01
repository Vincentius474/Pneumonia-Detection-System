package information;
/**
 * A class that represents a node
 */
class Node {
	
	/**
	 * class variables
	 */
    Patient patientData;
    Node next;

    /**
     * parameterized constructor
     * @param patientData node's element
     */
    public Node(Patient patientData) {
        this.patientData = patientData;
        this.next = null;  
    }
    
    /**
     * Modifiers
     */
    public void setNext(Node nextNode) {
    	this.next = nextNode;
    }
    
    public void setData(Patient data) {
    	this.patientData = data;
    }
    
    /**
     * Access methods
     */
    public Node getNext() {
    	return this.next;
    }
    
    public Patient getData() {
    	return this.patientData;
    }
    
}
