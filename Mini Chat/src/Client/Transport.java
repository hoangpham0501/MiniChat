package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Transport extends Thread{
	Client client;
	Socket socket;
	boolean status = true;
	DataInputStream dis;
	DataOutputStream dos;
	public Transport(Client cl,Socket soc){
		this.client = cl;
		this.socket = soc;
		try {
			dis = new DataInputStream(soc.getInputStream());
			dos=new DataOutputStream(soc.getOutputStream());
			this.start();
		} catch (IOException e) {
			client.appendLog("Cound not create DataInputStream\n");
			e.printStackTrace();
		}
	}
	public void run(){
		int command;
		String sender , message;
		Object obj = "CHAT";
		while(status){
			try {
				command=dis.read();
				switch(command){
				case Command.RECEIVE_MESSAGE:
					synchronized(obj){
						sender = dis.readUTF();
						message = dis.readUTF();
						client.appendLog(sender+" : "+message+"\n");
					}
					break;
				case Command.SEND_MESSAGE_FAILED:
					client.appendLog(dis.readUTF());
					break;
				}
			}catch (IOException e) {
				client.appendLog("Could not read data\n");
				//client.createOfflineStatus();
				status = false;
			}
		}
	}
	public synchronized void signin(String nick){
		try {
			dos.writeUTF(nick);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void shutDown(){
		status = false;
		try {
			dis.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendMessage(String to,String message){
		Object obj = "CHAT";
		try{
			synchronized(obj){
				dos.write(Command.SEND_MESSAGE);
				dos.writeUTF(to);
				dos.writeUTF(message);
				dos.flush();
			}
		}
		catch(Exception e){
			client.appendLog("Could not send message.\n");
		}
	}
}
