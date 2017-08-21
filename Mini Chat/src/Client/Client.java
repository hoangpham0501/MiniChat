package Client;
import java.awt.*;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

//import de.javasoft.plaf.synthetica.SyntheticaAluOxideLookAndFeel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


//@SuppressWarnings("serial")
public class Client extends JFrame
{
	JButton btSend = new JButton("Send");
	JButton btClear = new JButton("Clear");
	JButton btNew = new JButton("New Chat");
	JButton Chat;
    JButton Refresh;
    JButton History = new JButton("History");
	
	JPanel p0 = new JPanel();
	JPanel p1 = new JPanel();
	JPanel p2 = new JPanel();
	JScrollPane p3 = new JScrollPane();
	JPanel p4 = new JPanel();
	JPanel p5 = new JPanel();
	JPanel p6 = new JPanel();
	JPanel p7 = new JPanel();
	
	JTextField jtfFrom = new JTextField(25);
	JTextField jtfTo = new JTextField(25);
	JTextField jtfMs = new JTextField(30);
	
	
	JTextArea jtaLog = new JTextArea(10,10);
	JScrollPane jsp = new JScrollPane(jtaLog);
	
	Transport tran;
	Socket socket;
	JFileChooser chooser;
	FileWriter f;
	
	public String user1;
	private JTable jtable = new JTable();
    private DefaultTableModel tableModel = new DefaultTableModel();
	private Connection conn; 

	private void loadGui(){
		setLayout(new BorderLayout());
		
		p0.add(new JLabel("From: "));
		jtfFrom = new JTextField(user1);
		jtfFrom.setEditable(false);
		jtfTo = new JTextField();
		jtfTo.setEditable(false);
		jtaLog.setEditable(false);
		p0.add(jtfFrom);
		p0.add(btNew);
		p0.add(new JLabel("To: "));
		p0.add(jtfTo);
		this.add(p0 , BorderLayout.NORTH);
		
		p1.setLayout(new BorderLayout());
		p1.add(jsp , BorderLayout.CENTER);
		
		this.add(p1 , BorderLayout.CENTER);
			
		p2.add(new JLabel("Message"));
		p2.add(jtfMs);
		p2.add(btSend);
		p2.add(btClear);
		this.add(p2 , BorderLayout.SOUTH);
		
		String []colsName = {"Online"};
        tableModel.setColumnIdentifiers(colsName);  //dat tieu de cho table
        jtable.setModel(tableModel);    // ket noi jtable voi tableModel
        //jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        p3 = new JScrollPane(jtable);
         
        p7 = new JPanel(new GridLayout(1,1));
        p7.setPreferredSize(new Dimension(100,70));
        p7.add(p3);

        //this.add(panel1, BorderLayout.NORTH);
        
        p4 = new JPanel(new GridLayout(1,2));
        Chat = new JButton("Chat");
    	Refresh = new JButton("Refresh");     
    	p4.add(Chat);
    	p4.add(Refresh);
    	//p4.setPreferredSize(new Dimension(150,30));
    	
    	p6 = new JPanel(new GridLayout(1,1));
    	p6.add(History);
    	//p6.setPreferredSize(new Dimension(150,30));
    	
    	connectSQL();
        updateData(view()); 
    	
    	p5 = new JPanel(new GridLayout(3,1));
    	p5.add(p7, BorderLayout.NORTH);
    	p5.add(p4, BorderLayout.SOUTH);
    	p5.add(p6, BorderLayout.SOUTH);
    	this.add(p5, BorderLayout.EAST);
		
		this.setVisible(true);
		this.setSize(600, 400);
		this.setLocation(400,150);
		
	}
	
	public void connectSQL(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/MiniChat", "root","123456");
        } 
        catch (Exception e) {
            e.printStackTrace();
        }    
    }
	
	private void addListener(){
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				//connectSQL();
				try{
	    			//Tao doi tuong DatabaseMetaData de lay thong tin cua csdl
	    			//Statement stm = conn.createStatement();
	    			if(tran!=null){
	    				Logout();
	    				saveFile("./"+jtfFrom.getText()+jtfTo.getText());
	    				tran.shutDown();	
	    			}
	    			System.exit(0);
				}
	    			catch(Exception e){
	    				System.out.println(e);
	    			}
			}
		});
		
		btSend.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				sendMessage();
			}
		});
		
		btClear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				jtfMs.setText("");
			}
		});
		
		btNew.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				new Client(jtfFrom.getText());
			}
		});
		
		jtfMs.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				sendMessage();
			}
		});
		
		Chat.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int column = 0;
		 		int row = jtable.getSelectedRow();
		 		String value = jtable.getModel().getValueAt(row,column).toString();
		 		jtfTo.setText(value);
		 		
			}
		});
		
		Refresh.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				tableModel.setRowCount(0);
				updateData(view());
			}
		});
		
		History.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int column = 0;
		 		int row = jtable.getSelectedRow();
		 		String value = jtable.getModel().getValueAt(row,column).toString();
				readFile("./"+jtfFrom.getText()+value);
			}
		});
	}
	
	public ResultSet view(){
        ResultSet result = null;
        String sql = "select * from user where status = \""+1+"\" and username != \""+jtfFrom.getText()+"\"";
        try {
            Statement statement = (Statement) conn.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
	
	public void updateData(ResultSet result){
        String []colsName = {"Online"};
        tableModel.setColumnIdentifiers(colsName); 
 
        try {
            while(result.next()){ 
                String rows[] = new String[1];
                rows[0] = result.getString(2); 
                tableModel.addRow(rows);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
 
    }
	
	public void Logout(){
		connectSQL();
		try{
			//Tao doi tuong DatabaseMetaData de lay thong tin cua csdl
			Statement stm = conn.createStatement();
				stm.executeUpdate("Update user set status = (\'"+0+"\') where username = \""+jtfFrom.getText()+"\"");
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	public void saveFile(String filename)
	{
		try{
			String content = jtaLog.getText();
			f = new FileWriter(filename);
			f.write(content);
			f.flush();
			f.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void readFile(String filename)
	{
		try{
			//Xoa het noi dung hien co trong Textarea
			jtaLog.setText("");
			//Mo tep va doc
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while((s = br.readLine()) != null){
				//Chen them noi dung vao Textarea
				jtaLog.append(s+ "\n");
			}
			fr.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void sendMessage(){
		if(tran == null){
			JOptionPane.showMessageDialog(this,"Please Login","Error",JOptionPane.ERROR_MESSAGE);
		}
		else{
			if(jtfTo.getText().isEmpty()){
				JOptionPane.showMessageDialog(this,"Please enter receiver","Error",JOptionPane.ERROR_MESSAGE);
			}
			else{
				if(jtfMs.getText().isEmpty())
					return;
				System.out.println(jtfTo.getText());
				tran.sendMessage(jtfTo.getText(), jtfMs.getText());
				appendLog("Me : "+jtfMs.getText()+"\n");
				jtfMs.setText("");
			}
		}
	}
	
	public void createOnlineStatus(){
		try {
			socket=new Socket("localhost",7000);
			tran=new Transport(this,socket);
			tran.signin(jtfFrom.getText());
		}
		catch (UnknownHostException e) {
			this.appendLog("Could not connect to server\n");
			e.printStackTrace();
			return;
		} 
		catch (IOException e) {			
			this.appendLog("IOEXception\n");
			e.printStackTrace();
			return;
		}
		btSend.setEnabled(true);
		jtfFrom.setEnabled(false);
	}
	
	public Client(String name1){
		super("Mini Chat");
		user1 = name1;
		loadGui();
		createOnlineStatus();
		addListener();
	}
	public void appendLog(String text){
		jtaLog.append(text);
	}
	
}

 