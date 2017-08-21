package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NewSocket extends Thread{
	private static final String obj = "CHAT";
	private String NickName;
	private DataOutputStream dos;
	private DataInputStream dis;
	private boolean status = true;
	private Socket socket;
	private Server server;
	
	public NewSocket(Server ser,Socket soc){
		try {
			dos=new DataOutputStream(soc.getOutputStream());
			dis=new DataInputStream(soc.getInputStream());
			this.socket = soc;
			this.server = ser;
			this.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		int command;
		String receiver , message;
		try {
			
			NickName=dis.readUTF();
			System.out.println(NickName);
			
			if(server.hash.containsKey(NickName)){
				server.hash.get(NickName).shutDown("You must logoff because you have just sign in at another place");
			}
			
			server.hash.put(NickName, this);
			System.out.println("Nick name "+NickName+" signed in");
			while(status){
				command = dis.read();
				synchronized(obj){
					switch(command){
					case Command.SEND_MESSAGE:
						receiver = dis.readUTF();
						message = dis.readUTF();
						server.sendMessage(this.NickName, receiver, message);
						break;
					case Command.SIGN_OUT:
						status = false;
						server.hash.remove(this.NickName);
						socket.close();
					}
				}
			}
		} catch (Exception e) {
			server.hash.remove(this.NickName);
		}
	}
	
	public void shutDown(String comment){
		status = false;
		try {
			dos.writeUTF(comment);
			socket.close();
			server.hash.remove(this.NickName);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void pushMessage(String sender, String message){
		try{
			dos.write(Command.RECEIVE_MESSAGE);
			dos.writeUTF(sender);
			dos.writeUTF(message);
			dos.flush();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public synchronized void pushNotifyRetval(int retval,String notify){
		try{
			dos.write(retval);
			dos.writeUTF(notify);
			dos.flush();
		}catch(Exception ex){
			
		}
	}

}
