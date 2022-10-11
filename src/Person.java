
public abstract class Person {

	private String name;
	private String blood;
	private int ID;
	
	
	public Person(int id , String name , String blood){
		ID=id;
		this.name=name;
		this.blood=blood;
	}
	
	public String getName() {
		return this.name;
	}

	
	public String getBlood() {
		return this.blood;
	}

	
	public int getID() {
		return this.ID;
	}

	


}
