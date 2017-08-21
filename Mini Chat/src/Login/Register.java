package Login;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.*;

import Client.Client;

import java.awt.*;
import de.javasoft.plaf.synthetica.SyntheticaAluOxideLookAndFeel;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

class Register extends JFrame implements ActionListener
{
	JButton Cancle;
    JButton Register;
    JPanel panel;
    JLabel label1,label2,label3;
    final JTextField  text1,text2, text3;
    private Connection conn; 
    
    Register(){
    	label1 = new JLabel();
        label1.setText("Username:");
        text1 = new JTextField(15);
       
        label2 = new JLabel();
        label2.setText("Password:");
        text2 = new JPasswordField(15);
         
        label3 = new JLabel();
        label3.setText("Confirm Password:");
        text3 = new JPasswordField(15);
        
        Cancle = new JButton("Cancle");
        Register = new JButton("Register");
         
        panel=new JPanel(new GridLayout(4,1));
        panel.add(label1);
        panel.add(text1);
        panel.add(label2);
        panel.add(text2);
        panel.add(label3);
        panel.add(text3);
        panel.add(Register);
        panel.add(Cancle);
        add(panel,BorderLayout.CENTER);
        Cancle.addActionListener(this);
        Register.addActionListener(this);
        setTitle("Register Form");
        this.setVisible(true);
		this.setSize(400, 150);
		this.setLocation(500,200);
    }
        
    public void actionPerformed(ActionEvent ae){
    	if(ae.getActionCommand().equals("Cancle")){
    		text1.setText("");
    		text2.setText("");
    		text3.setText("");
    		dispose();
    		new Login();
    	}
    	
    	if(ae.getActionCommand().equals("Register")){
    		String name = text1.getText();
    		String pass = text2.getText();
    		String cfpass = text3.getText();
    		if(name.equals("") || pass.equals("") || cfpass.equals("")){
        		JOptionPane.showMessageDialog(this,"Invaid username or password","Error",JOptionPane.ERROR_MESSAGE);
    		}
    		else {
    			String USERNAME_PATTERN = "^[a-zA-Z0-9_-]{3,15}$";
        		Boolean b = name.matches(USERNAME_PATTERN);
        		String PASSWORD_PATTERN = "^[a-zA-Z0-9-]{8,20}$";
    			Boolean c = pass.matches(PASSWORD_PATTERN);
        		if(b == false){
        			JOptionPane.showMessageDialog(this,"Userame must be at least 3 characters and maximum 15 characters in length!No special character","Error",JOptionPane.ERROR_MESSAGE);
        			
        		}	
        		else if(c == false){
            		JOptionPane.showMessageDialog(this,"Passwords must be at least 8 characters and maximum 20 characters in length!No special character","Error",JOptionPane.ERROR_MESSAGE);
            	}
        		else if(pass.equals(cfpass)){
        			try{
        				//Nap driver
        				Class.forName("com.mysql.jdbc.Driver");
        				//Ket noi csdl
        				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/MiniChat", "root","123456");
        				//Tao doi tuong DatabaseMetaData de lay thong tin cua csdl
        				Statement stm = conn.createStatement();
        				ResultSet rst = stm.executeQuery("select * from user where username = \""+name+"\"");
        				if(rst != null){
        					if(rst.next()){
        						if (rst.getString("username").equals(name)){
        							JOptionPane.showMessageDialog(this,"Username is available","Error",JOptionPane.ERROR_MESSAGE);
        						}
        					}
        					else {
        						String encrypt = Encrypt.encryptMD5(pass);
        						stm.executeUpdate("Insert into user(username,password) values(\'"+name+"\' , \'"+encrypt+ "\')");
        						dispose();
        						new Login();
        					}
        				}
        			}
        			catch(Exception e){
        				System.out.println(e);
        			}
        		}
        		else{
        			JOptionPane.showMessageDialog(this,"password does not match the confirm password","Error",JOptionPane.ERROR_MESSAGE);
        		}	
        	}
    	}		
    }
}
