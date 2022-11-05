import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class BloodDonationMain implements ActionListener{
	
	public static void main(String[] args) {
		
		BloodDonationMain bdMain  = new BloodDonationMain();
		
	}
	
	
	JButton addDonorButton;
	JButton addRecipButton;
	JButton runAlgButton;
	File donorFile;
	File recipFile;
	
	private BloodDonationMain() {
		
		
		JFrame frame = new JFrame();
		JPanel upperPanel = new JPanel();
		JPanel lowerPanel = new JPanel();

		upperPanel.setBounds(0,0,300,200);
		upperPanel.setBackground(new Color(10,10,10));
		lowerPanel.setBounds(0,200,300,200);
		
		
		addDonorButton = new JButton("Add Donor file");
		addDonorButton.addActionListener(this);
		
		addRecipButton = new JButton("Add Recipinet File");
		addRecipButton.addActionListener(this);
			
		upperPanel.add(addDonorButton);
		upperPanel.add(addRecipButton);
		
		
		runAlgButton = new JButton("Run Pairing Algorithm");
		runAlgButton.addActionListener(this);
		lowerPanel.add(runAlgButton);


  

		frame.setTitle("Blood Donation Assistant");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(upperPanel);  
        frame.add(lowerPanel);
        frame.setResizable(false);
        frame.setSize(300,300);    
        frame.setLayout(null);    
     	frame.setVisible(true);    
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==addDonorButton) {
			
			JFileChooser donorFileChooser = new JFileChooser();
			donorFileChooser.setDialogTitle("Select Donor File");
			int response = donorFileChooser.showOpenDialog(null);	
			
			if(response == donorFileChooser.APPROVE_OPTION) {
				donorFile = donorFileChooser.getSelectedFile();
				System.out.println(donorFile.getAbsolutePath());
			}
		}
		if(e.getSource()==addRecipButton) {
			
			JFileChooser recipFileChooser = new JFileChooser();
			recipFileChooser.setDialogTitle("Select Recipient File");
			int response = recipFileChooser.showOpenDialog(null);	
			
			if(response == recipFileChooser.APPROVE_OPTION) {
				recipFile = recipFileChooser.getSelectedFile();
				System.out.println(recipFile.getAbsolutePath());
			}
		}
		if(e.getSource()==runAlgButton) {
			
			
			if(donorFile!=null && recipFile!=null){
				if(!checkFileValidity(donorFile)){
					showError("The Donor File Selected is not in the .txt Format","Invalid Donor File");
				}
				else if(!checkFileValidity(recipFile)){
					showError("The Recipient File Selected is not in the .txt Format","Invalid Recipient File");
				}
				else{
					try{
						PairingAlgorithm PA = new PairingAlgorithm(donorFile,recipFile);
					}
					catch(IOException ioe) {
						showError(ioe.getStackTrace().toString(),"IOException");
					} 
					catch (URISyntaxException e1) {
						showError(e1.getStackTrace().toString(),"URIException");
					}
				}
			
			}
			else {
				showError("You have not selected either donor or recipient files.","Null File Error");
			}
			
		}
		
	}
	
	private boolean checkFileValidity(File file) {
		if(!file.exists()) return false;
		if(!getExtension(file).equals("txt")) return false;
		return true;
	}
	
	
	
	private String getExtension(File file){
		String path= file.getAbsolutePath();
		String extension = "";

		int i = path.lastIndexOf('.');
		if (i >= 0) {
		    extension = path.substring(i+1);
		}
		return extension;
	}
	
	public static void showError(String message,String title) {
		JOptionPane.showMessageDialog(null,message,title,0);

	}

	
}
