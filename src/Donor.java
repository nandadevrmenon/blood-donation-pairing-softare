import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Donor extends Person{
	
	protected static HashMap<Integer,Integer> matchesPerID = new HashMap<Integer,Integer>(); 		//key: Donor ID  value: total number of compatible recipients
	protected static HashMap<Integer,Integer> hitsPerID = new HashMap<Integer,Integer>();			//Key: Donor ID  value: total 
	
	private ArrayList<LocalDate> appointments = new ArrayList<LocalDate>();
	
	private ArrayList<Integer> hitRecipientIDs= new ArrayList<Integer>();
	
	public Donor(Integer id, String[] details) {
		setID(id);
		setName(details[0]);
		setBloodGroup(details[1]);
		hitsPerID.put(id, 0);
	}
		
	
	public static void countMatchFor(int donorID) {
		int currentVal = matchesPerID.get(donorID);
		matchesPerID.replace(donorID, currentVal+1);		//we count number of matches in negative as this will help later in recalibration
	}
	
	public static void recalibrateMatchesPerID(int chosenDonorID,Recipient recipient) {
		ArrayList<Integer> matchIDs = recipient.getMatchedDonorIDs();
		for(Integer donorID :matchIDs) {
			if(donorID!=chosenDonorID) {
				int currentVal = matchesPerID.get(donorID);
				matchesPerID.replace(donorID,currentVal-1);
			}
		}
	}
	
	public static int  getNumberOfMatchesForID(int donorID) {
		return matchesPerID.get(donorID);
	}
	
	
	public static void addHitFor(int id) {
		int currentVal = hitsPerID.get(id);
		hitsPerID.replace(id,currentVal+1);
	}
	
	public int getTotalHits() {
		return hitsPerID.get(this.getID());
	}
	
	
	public void addRecipientHit(int id) {
		this.hitRecipientIDs.add(id);
	}
	
	public int getRecipientIDAt(int i) {
		return this.hitRecipientIDs.get(i);
	}
	
	public void addAppointment(LocalDate date) {
		appointments.add(date);
	}
	
	public LocalDate getAppointmentAt(int i){
		return appointments.get(i);
	}
	
	public boolean AllAppointmentsMade() {
		return (appointments.size()==this.getTotalAppointments());
	}
	
	public int getTotalAppointments() {
		return appointments.size();
	}
	
	
	
	// for testing
	
	
	public static void printmatches() {
		System.out.println("****************************");
		for(int i = 1 ; i< matchesPerID.size();i++) {
			System.out.println(matchesPerID.get(i));
		}
		System.out.println("****************************");
	}
	
	public static void printHits() {
		System.out.println("****************************");
		for(int i = 0 ; i< hitsPerID.size();i++) {
			System.out.println(hitsPerID.get(i+1));
		}
		System.out.println("****************************");
	}

}
