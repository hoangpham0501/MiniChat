package Server;

import java.net.ServerSocket;
import java.util.Hashtable;

public class Server {
	public Hashtable<String, NewSocket> hash;
	private  NewSocket newSocket;
	public Server() {
		hash = new Hashtable<String, NewSocket>();
		try {
			ServerSocket server = new ServerSocket(7000);
			System.out.println("Server started...");
			do {
				newSocket =  new NewSocket(this, server.accept());
			} while (true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Server();
	}

	public void sendMessage(String from, String to, String message) {
		// Kiem tra co doi tuong gui
		if (!hash.containsKey(to)) {
			hash.get(from).pushNotifyRetval(Command.SEND_MESSAGE_FAILED, "Nick name " + to + " is not availabe!!!");
		} else {
			try {
				// Tra ve doi tuong chua value lien ket voi key
				hash.get(to).pushMessage(from, message);
				hash.get(from).pushNotifyRetval(Command.SEND_MESSAGE_SUCCESSFULLY, "NULL");
			} catch (Exception ex) {
			}
		}
	}
}
