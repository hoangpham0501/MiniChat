package Login;

     

import javax.imageio.ImageIO;
import javax.swing.*;
import java.sql.*;
import Client.Client;
import Client.Transport;
import de.javasoft.plaf.synthetica.SyntheticaAluOxideLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
      
class Login extends JFrame implements ActionListener
{
	JButton Login;
    JButton Register;
    JPanel panel;
    JLabel label1,label2;
    final JTextField  text1,text2;
    private Connection conn; 
    
    public static class Logo extends JPanel
    {

        public Logo()
        {
            this.setLayout(new BorderLayout());
            // incorporated @nIcE cOw's comment about loading classpath resources
            ImageIcon icon;
			try {
				BufferedImage logo = ImageIO.read(getClass().getResourceAsStream("/Logo.jpg"));
				icon = new ImageIcon(logo);	
				JLabel label = new JLabel(icon);
	            this.add( label, BorderLayout.NORTH);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}     
        }
    }
    
    Login(){
    	label1 = new JLabel();
    	label1.setText("Username:");
    	text1 = new JTextField(20);
    	label2 = new JLabel();
    	label2.setText("Password:");
    	text2 = new JPasswordField(20);
      
    	Login = new JButton("Login");
    	Register = new JButton("Register");
       
    	panel=new JPanel(new GridLayout(3,2));
    	panel.add(label1);
    	panel.add(text1);
    	panel.add(label2);
    	panel.add(text2);
    	panel.add(Login);
    	panel.add(Register);
    	add(new Logo());
    	add(panel,BorderLayout.SOUTH);
    	Login.addActionListener(this);
    	Register.addActionListener(this);
    	setTitle("MINI CHAT");
    	setSize(480,220);
		setVisible(true);
		setLocation(500,200);
      }
    
    public void actionPerformed(ActionEvent ae){
    	if(ae.getActionCommand().equals("Login")){
    		String name = text1.getText();
    		String pass = text2.getText();
    		String encrypt = Encrypt.encryptMD5(pass);
    		if(name.equals("") || pass.equals("")){
    			JOptionPane.showMessageDialog(this,"Incorrect login or password","Error",JOptionPane.ERROR_MESSAGE);
    		}
    		try{
    			//Nap driver
    			Class.forName("com.mysql.jdbc.Driver");
    			//Ket noi csdl
    			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/MiniChat", "root","123456");
    			//Tao doi tuong DatabaseMetaData de lay thong tin cua csdl
    			Statement stm = conn.createStatement();
    			ResultSet rst = stm.executeQuery("select * from user where username = \""+name+"\" and password = \""+encrypt+"\"");
    			if(rst != null){
    				if(rst.next()){
    					if (rst.getString("password").equals(encrypt)) {
    						stm.executeUpdate("Update user set status = (\'"+1+"\') where username = \""+name+"\"");
    						dispose();
    						new Client(name);
    						
    					}
    					else {
    						JOptionPane.showMessageDialog(Login.this, "Password is match case.", "Login failed", JOptionPane.INFORMATION_MESSAGE);
    					}
    				}
    				else {
    					JOptionPane.showMessageDialog(Login.this, "Wrong username or password.", "Login failed", JOptionPane.INFORMATION_MESSAGE);
    				}
    			}
    		}
    		catch(Exception e){
    			System.out.println(e);
    		}
    	}
    	if(ae.getActionCommand().equals("Register")){
    		dispose();
    		new Register();
    	}
 }
    
    
      
    public static void main(String arg[])
    {
    	try {
    		UIManager.setLookAndFeel(new SyntheticaBlackEyeLookAndFeel());
    		//UIManager.setLookAndFeel(new SyntheticaAluOxideLookAndFeel());
    	} 
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	new Login();
    }
}
