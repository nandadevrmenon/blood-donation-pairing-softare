import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.Map.Entry;

public class PairingAlgorithm {
	public static boolean[][] compatChart = 
		{{true,false,false,false,false,false,false,false},
		 {true,true,false,false,false,false,false,false},
		 {true,false,true,false,false,false,false,false},
		 {true,true,true,true,false,false,false,false},
		 {true,false,false,false,true,false,false,false},
		 {true,true,false,false,true,true,false,false},
		 {true,false,true,false,true,false,true,false},
		 {true,true,true,true,true,true,true,true}
		};
	
	
	public static LinkedHashMap<Integer,Donor> donors = new LinkedHashMap<Integer,Donor>();
	public static LinkedHashMap<Integer,Recipient> recipients = new LinkedHashMap<Integer,Recipient>();


	public PairingAlgorithm(File donors, File recipients) throws IOException, URISyntaxException {
	
		
		
		File donorsFile = donors;
		File recipientsFile = recipients;
	
		
		try {
			fillPatientMaps(donorsFile,recipientsFile);			//extracts info from files and makes donor and recipient objects and fills patient hashmaps
		}
		catch(IllegalArgumentException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}
		
		findAllMatches(compatChart); 
		
		arrangeRecipientsMap();
		
		assignPairs();
		
		printMatches();
		Donor.printHits();
		
		CodeSource codeSource = BloodDonationMain.class.getProtectionDomain().getCodeSource();
		File jarFile = new File(codeSource.getLocation().toURI().getPath());
		String jarDir = jarFile.getParentFile().getPath();
		File appFile = new File(jarDir+"/appointments.txt");
		appFile.createNewFile();
		
		arrangeDonorsMap();
		
		makeAppointments();
		
		writeAppointments(appFile);
		
		
		
		
	}
	
	
	
	
	//FUNCTIONSSS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	
	
	
	public static void fillPatientMaps(File donorsFile,File recipientsFile) throws FileNotFoundException,IllegalArgumentException {
		
		System.out.println("***************DONORS DIRECTORY***************");
		ArrayList<String[]> patients = extractInfo(donorsFile);
		
		
		ListIterator<String[]> itr = patients.listIterator();
		int IDcounter = 1;
		
		while(itr.hasNext()) {
			String[] details = itr.next();
			donors.put(IDcounter,new Donor(IDcounter,details));
			Donor.matchesPerID.put(IDcounter, 0);
			IDcounter++;
		}
		
		
		System.out.println("***************RECIPIENTS DIRECTORY***************");
		patients=extractInfo(recipientsFile);
		itr=patients.listIterator();
		
		while(itr.hasNext()) {
			String[] details = itr.next();
			recipients.put(IDcounter,new Recipient(IDcounter,details));
			IDcounter++;
		}
		
		
	}
	
	public static ArrayList<String[]> extractInfo(File patientsFile) throws FileNotFoundException,IllegalArgumentException{
		Scanner fileScanner = new Scanner(patientsFile);
		ArrayList<String[]> personDetailsArr = new ArrayList<String[]>();
		while(fileScanner.hasNextLine()) {
			String[] detailsArr = fileScanner.nextLine().split(";");			//splits each line into name an
			if(detailsArr.length ==1) continue;
			detailsArr[0] = detailsArr[0].trim();								//formats name and blood Group
			detailsArr[1] = detailsArr[1].trim().toUpperCase();
			if(hasValidBloodType(detailsArr[1])) {								//we only add the person to the arrayList if they have a valid blood type
				personDetailsArr.add(detailsArr);
				System.out.println(detailsArr[0]+" : "+ detailsArr[1]);
			}
			else {
				System.out.println(detailsArr[0]+": Blood Type not valid");
			}
			
		}
		
		fileScanner.close();
		if(personDetailsArr.size()==0) {
			throw new IllegalArgumentException("Either Donor or Recipient File is Empty");
			
		}
		return personDetailsArr;
	}
	
	
	public static boolean hasValidBloodType(String type) {		//this type has already been trimmed and made into uppercase
		String group;
		String rh;
		
		if(type.length()==2) {					//split blood group into the letters and the sign
			group = type.substring(0,1);
			rh = type.substring(1,2);
		}
		else if(type.length()==3)	{	
			group = type.substring(0,2);
			rh = type.substring(2,3);
		}
		else return false;				//if length is != 2 or 3 then it is not a vlid blood type
		
		if (!(group.equals("A")|| group.equals("O") || group.equals("AB") || group.equals("B"))){
			return false;
		}
		if (!(rh.equals("+")||rh.equals("-"))) {
			return false;
		}
		return true;	
	}
	
	
	public static void findAllMatches(boolean[][] compatChart){
		
		
		for(Entry<Integer,Recipient> recipEntry: recipients.entrySet()) {
			
			Recipient recipient = recipEntry.getValue();
			
			for(Entry<Integer,Donor> donorEntry: donors.entrySet()) {
				
				Donor donor = donorEntry.getValue();
				int donorID = donor.getID();
				
				boolean matchFound = checkCompatibility(donor,recipient);
				
				if(matchFound) {
					recipient.addDonorToMatches(donorID);
					Donor.countMatchFor(donorID);
					
				}
				
				
			}
		}
		
	}
	
	
	public static void arrangeRecipientsMap() {
		
		
		ArrayList<Entry<Integer,Recipient>> listOfRecipients = new ArrayList<Entry<Integer, Recipient>>(recipients.entrySet());
		
		Comparator<Entry<Integer,Recipient>> matchesComparator = new Comparator<Entry<Integer,Recipient>>() {
			@Override 
			public int compare(Entry<Integer, Recipient> e1, Entry<Integer, Recipient> e2) {
				int e1Matches = e1.getValue().getNumberOfMatches();
				int e2Matches = e2.getValue().getNumberOfMatches();
				
				if(e1Matches<e2Matches) return -1;
            	else if(e1Matches==e2Matches) return 0;
            	else return 1;
				
				} 
		};
		
		Collections.sort(listOfRecipients,matchesComparator);
		recipients.clear();
		
		for(Entry<Integer,Recipient> entry : listOfRecipients) {
			recipients.put(entry.getKey(), entry.getValue());
		}

		
	}
	
	public static void assignPairs(){
	
		for(Entry<Integer,Recipient> recipEntry: recipients.entrySet()) {
			
			Recipient recipient = recipEntry.getValue();
			int matchedDonorID=0;
			
			if(recipient.getNumberOfMatches()==1) {
				matchedDonorID = recipient.accessDonorIDAt(0);
			}
			else if (recipient.getNumberOfMatches()>1){
				matchedDonorID = findBestDonor(recipient);
			}
			else continue;
			
			recipient.setFinalMatch(matchedDonorID);
			Donor.recalibrateMatchesPerID(matchedDonorID,recipient);
			donors.get(matchedDonorID).addRecipientHit(recipient.getID());
			Donor.addHitFor(matchedDonorID);
			
		}
		
	}
	
	
	
	public static int findBestDonor(Recipient recipient) {
		
		int size = recipient.getNumberOfMatches();
		int donorIDWithLeastMatches = recipient.accessDonorIDAt(0);
		int leastMatchesYet = Donor.getNumberOfMatchesForID(donorIDWithLeastMatches);
		
		
		for(int i = 1 ; i <size ; i++) {
			int nextDonorID = recipient.accessDonorIDAt(i);
			int nextDonorsMatches = Donor.getNumberOfMatchesForID(nextDonorID);
			if(nextDonorsMatches<leastMatchesYet) {
				donorIDWithLeastMatches = nextDonorID;
				leastMatchesYet=nextDonorsMatches;
			}
			
		}
		
		return donorIDWithLeastMatches;
		
	}

	

	
	
	
	public static void printMatches() {
		
		
		for(Entry<Integer,Recipient> recipEntry: recipients.entrySet()) {
			System.out.println();
			
			Recipient recipient = recipEntry.getValue();
			
			if(recipient.getNumberOfMatches()!=0) {
				System.out.println(recipient.getName()+": "+donors.get(recipient.getFinalDonorID()).getName());		//prints out the matches
			}
			
		}
	
		
	}
	
	
	
	
	public static void arrangeDonorsMap() {
		
		
		ArrayList<Entry<Integer,Donor>> listOfDonors = new ArrayList<Entry<Integer, Donor>>(donors.entrySet());
		
		Comparator<Entry<Integer,Donor>> matchesComparator = new Comparator<Entry<Integer,Donor>>() {
			@Override 
			public int compare(Entry<Integer, Donor> d1, Entry<Integer, Donor> d2) {
				int d1Hits = d1.getValue().getTotalHits();
				int d2Hits = d2.getValue().getTotalHits();
				
				if(d1Hits>d2Hits) return -1;
            	else if(d1Hits==d2Hits) return 0;
            	else return 1;
				
				} 
		};
		
		Collections.sort(listOfDonors,matchesComparator);
		donors.clear();
		
		for(Entry<Integer,Donor> entry : listOfDonors) {
			donors.put(entry.getKey(), entry.getValue());
		}

		
	}


	public static void makeAppointments() {
		
		
		int appointmentCycles = donors.entrySet().iterator().next().getValue().getTotalHits();	
		
		
																	//appointments are made by looping through the donorMap and each time we loop through it,
																	//we give each donor an appointment using the datePointer that moves after every twelve appointments. 
																	//After each iteration through donor array we just have to check if the date is 56 days after the first 
																	//donor's latest donation and if it is we iterate again and this makes sure that there is a 56 day gap
																	//between 2 consecutive donations of each donor  
		
		LocalDate datePointer = LocalDate.now();
		datePointer.plusDays(1);						//appointment setting starts from 1 day after current system date
		
		for(int i = 0 ;i <appointmentCycles;i++){
			
			int appointmentsInADay=0;
			
			for(Entry<Integer,Donor> donorEntry : donors.entrySet()) {
				Donor donor = donorEntry.getValue();
				
				
				if(donor.getTotalHits()>0){
					
					
					if(appointmentsInADay==12) {				//if we iht the daily limit we move date pointer 
						appointmentsInADay=0;
						datePointer = datePointer.plusDays(1);
					}
					
					if(datePointer.getDayOfWeek().getValue()==6) {		// if date pointer is now at a Saturday we move it to monday
						datePointer = datePointer.plusDays(2);
					}
					else if(datePointer.getDayOfWeek().getValue()==7) {// if date pointer is at a sunday we move it to monday
						datePointer = datePointer.plusDays(1);
					}
					
					
					donor.addAppointment(datePointer);		//we add the appointment for each donor
					appointmentsInADay++;
				}
				
				
			}
			LocalDate firstDonorLastApp =donors.entrySet().iterator().next().getValue().getAppointmentAt(i);	//after we iterate completely through the whole array we check if the date pointer is at 56 days after firts dnor's last appoitnment
			if(datePointer.isBefore(firstDonorLastApp.plusDays(56))) {
				datePointer =firstDonorLastApp.plusDays(57);
			}
			
			
			
		}
	}
		
	
	public static void writeAppointments(File appFile) throws FileNotFoundException {
	PrintWriter writer = new PrintWriter(appFile);						//we print out appointments in the order they were made 
	
	int totalIterations = donors.entrySet().iterator().next().getValue().getTotalHits();		//the maximum number of times we would iterate through donor array is the highest number of matches a donor has
												
	
	writer.write("-------------------------------------------------------------------"+"\n");
	for(int i =0 ;i<totalIterations;i++) {
		
		for(Entry<Integer,Donor> donorEntry : donors.entrySet()){	
		
			Donor donor = donorEntry.getValue();
			
			if(i == donor.getTotalHits()) {				//for efficiency
			break;
			}
			
			if(donor.getTotalHits()>0) {
				
				int recipientIndex=donor.getRecipientIDAt(i);			//we get the recipient index and print out the neccesary info
				Recipient recipient = recipients.get(recipientIndex);
				LocalDate appointment = donor.getAppointmentAt(i);
				String formattedDate = appointment.format(DateTimeFormatter.ofPattern("dd-MM-YYYY"));
				
				writer.write("Recipient: "+recipient.getName()+"\t\t\t");		//writes name
				writer.write("BloodGroup: "+recipient.getBlood()+"\n");			//write bloodgroup
				
				writer.write("Donor: \t"+donor.getName()+"\t\t\t");
				writer.write("BloodGroup: "+donor.getBlood()+"\n");
				
				writer.write("Date: "+formattedDate+"\n");
				writer.write("-------------------------------------------------------------------"+"\n");
			}

			

	}

	
	
	}
	
	
	writer.close();
}
		
		
	
	
	
	
	
	
	
	
	
	
	
	
	public static boolean checkCompatibility(Donor donor,Recipient recipient) {
		
		String donorBlood = donor.getBlood();				//gets whole blood group of a donor
		String recipientBlood = recipient.getBlood();		//gets whole blood group of the recipient
				
		int donorIndex = calcIndex(getGroup(donorBlood),getRh(donorBlood));		//calc index takes in group and Rh and find respective index of the blood group
		int recipientIndex =calcIndex(getGroup(recipientBlood),getRh(recipientBlood));
		
		
		return compatChart[recipientIndex][donorIndex];		//we look up those indexes in compatibility chart to find out if they are compatible
	}
	
	public static String getGroup(String bloodGroup) {
		if(bloodGroup.length()==2) {
			return bloodGroup.substring(0,1);
		}
		else return bloodGroup.substring(0,2);	//returns the alphabetical part of a bloodgroup
	}
	
	public static String getRh(String bloodGroup) {
		if(bloodGroup.length()==2) {
			return bloodGroup.substring(1);
		}
		else return bloodGroup.substring(2);	//returns the sign(rhesus factor) of the blood group
	}
	
	public static int calcIndex(String group,String rh) {
		int index=0;		//we assign index based on blood group and this index is used to access compatibility chart to get compatibility
		
		switch(group) {		//since blood group is already perfectly formatted we can use switch case with strings
		case "O":
			index = 0;
			break;
		case "A":
			index = 2;
			break;
		case "B" :
			index = 4;
			break;
		case "AB" :
			index = 6;
			
		}
		
		if(rh.equals("+")) {
			index++;
		}
		
		return index;
	}
	


}
