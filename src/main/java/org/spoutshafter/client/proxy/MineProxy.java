package org.spoutshafter.client.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.regex.Pattern;
import java.net.BindException;


public class MineProxy extends Thread {
	public static String authServer;
	
	public static Pattern SKIN_URL = Pattern.compile("http://s3\\.amazonaws\\.com/MinecraftSkins/(.+?)\\.png");
	public static Pattern CLOAK_URL = Pattern.compile("http://s3\\.amazonaws\\.com/MinecraftCloaks/(.+?)\\.png");
	public static Pattern GETVERSION_URL = Pattern.compile("http://session\\.minecraft\\.net/game/getversion\\.jsp");
	public static Pattern JOINSERVER_URL = Pattern.compile("http://session\\.minecraft\\.net/game/joinserver\\.jsp(.*)");
	public static Pattern CHECKSERVER_URL = Pattern.compile("http://session\\.minecraft\\.net/game/checkserver\\.jsp(.*)");
	public static Pattern AUDIOFIX_URL = Pattern.compile("http://s3\\.amazonaws\\.com/MinecraftResources/");
	public static Pattern DL_BUKKIT = Pattern.compile("http://dl.bukkit.org/(.+?)");
	public static Pattern DL_SPOUT = Pattern.compile("http://www.spoutcraft.org/(.+?)");
	//public static Pattern LOGIN_URL = Pattern.compile("login\\.minecraft\\.net/");
	
	public float version = 0;
	private int port = -1;
	
	public Hashtable<String, byte[]> skinCache;
	public Hashtable<String, byte[]> cloakCache;
	
	public MineProxy(float version, String currentAuthServer) {
		this.setName("MineProxy Thread");
		MineProxy.authServer = currentAuthServer; // TODO maybe change this leave it for now 
		
		try {
			this.version = version;
			
			this.skinCache = new Hashtable<String, byte[]>();
			this.cloakCache = new Hashtable<String, byte[]>();
		} catch(Exception e) {
			System.out.println("Proxy starting error:");
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			ServerSocket server = null;
			int port = 9000; // A lot of other applications use the 80xx range,
								// let's try for some less crowded real-estate
			while (port < 12000) { // That should be enough
				try {
					System.out.println("Trying to proxy on port " + port);
					server = new ServerSocket(port);
					this.port = port;
					System.out.println("Proxying successful");
					break;
				} catch (BindException ex) {
					port++;
				}

			}
			
			while(true) {
				Socket connection = server.accept();
				
				MineProxyHandler handler = new MineProxyHandler(this, connection);
				handler.start();
			}
		} catch(IOException e) {
			// TODO What do I do here?
		}
	}
	

	public int getPort() {
		while (port < 0) {
			try {
				sleep(50);
			} catch (InterruptedException e) {
				System.out.println("Interrupted while waiting for port");
				e.printStackTrace();
			}
		}
		return port;
	}
}
