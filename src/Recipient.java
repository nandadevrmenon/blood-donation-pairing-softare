import java.util.ArrayList;

public class Recipient extends Person{
	
	private ArrayList<Integer> matchedDonorIDs = new ArrayList<Integer>();
	private Integer finalDonorID;

	public Recipient(Integer id, String[] details) {
		super(id,details[0],details[1]);
	}
	
	
	
	public void addDonorToMatches(int id) {
		matchedDonorIDs.add(id);
	}
	
	public int accessDonorIDAt(int index) {
		return matchedDonorIDs.get(index);
	}
	
	public int getNumberOfMatches() {
		return matchedDonorIDs.size();
	}
	
	public void setFinalMatch(int ID) {
		this.finalDonorID=ID;
	}
	
	public int getFinalDonorID() {
		return this.finalDonorID;
	}
	
	public ArrayList<Integer> getMatchedDonorIDs(){
		return this.matchedDonorIDs;
	}
	
	
}
