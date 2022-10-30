import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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
	String donorFilePath;
	
	private BloodDonationMain() {
		
		
		JFrame frame = new JFrame();
		JPanel upperPanel = new JPanel();
		JPanel lowerPanel = new JPanel();

		upperPanel.setBounds(0,50,400,400);
		lowerPanel.setBounds(0,100,400,400);
		
		
		addDonorButton = new JButton("Add Donor file");
		addDonorButton.addActionListener(this);
		
		addRecipButton = new JButton("Add Recipinet File");
		addRecipButton.addActionListener(this);
			
		upperPanel.add(addDonorButton);
		upperPanel.add(addRecipButton);
		
		
		runAlgButton = new JButton("Run Pairing Algorithm");
		runAlgButton.addActionListener(this);

////		JLabel label = new JLabel("Welcome to Blood Donation Assistant!");


  

		frame.setTitle("Blood Donation Assistant");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(upperPanel);  
        frame.setResizable(false);
        frame.setSize(400,400);    
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
				donorFilePath = donorFileChooser.getSelectedFile().getAbsolutePath();
				System.out.println(donorFilePath);
			}
		}
		if(e.getSource()==addRecipButton) {
			
			JFileChooser recipFileChooser = new JFileChooser();
			recipFileChooser.setDialogTitle("Select Recipient File");
			int response = recipFileChooser.showOpenDialog(null);	
			
			if(response == recipFileChooser.APPROVE_OPTION) {
				donorFilePath = recipFileChooser.getSelectedFile().getAbsolutePath();
				System.out.println(donorFilePath);
			}
		}
		if(e.getSource()==runAlgButton) {
			try {
				PairingAlgorithm PA = new PairingAlgorithm();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}
	
	
}
