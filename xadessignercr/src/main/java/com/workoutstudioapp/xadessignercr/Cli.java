package com.workoutstudioapp.xadessignercr;

public class Cli {
	public static void main(String[] args) {
		String action = null;
		// sign
		String keyPath = null;
		String keyPassword = null;
		String xmlInPath = null;
		String xmlOutPath = null;
		// send
		String endPoint = null;
		String xmlPath = null;
		String username = null;
		String password = null;
		
		if (args.length != 5) {
			showUsage();
			System.exit(-1);
		}
		action = args[0];
		if ("sign".equals(action)) {
			keyPath = args[1];
			keyPassword = args[2];
			xmlInPath = args[3];
			xmlOutPath = args[4];
			Signer signer = new Signer();
			signer.sign(keyPath, keyPassword, xmlInPath, xmlOutPath);
			System.exit(0);
		} else if ("send".equals(action)) {
			endPoint = args[1];
			xmlPath = args[2];
			username = args[3];
			password = args[4];
			Sender sender = new Sender();
			sender.send(endPoint, xmlPath, username, password);
			System.exit(0);
		} else if ("query".equals(action)) {
			endPoint = args[1];
			xmlPath = args[2];
			username = args[3];
			password = args[4];
			Sender sender = new Sender();
			sender.query(endPoint, xmlPath, username, password);
			System.exit(0);
		} else {
			showUsage();
			System.exit(-1);
		}
	}
	public static void showUsage() {
		System.out.println("Usage:");
		System.out.println("java -jar xades-signer-cr sign <keyPath> <keyPassword> <xmlInPath> <xmlOutPath>");
		System.out.println("java -jar xades-signer-cr send <endPoint> <xmlPath> <username> <password>");
		System.out.println("java -jar xades-signer-cr query <endPoint> <xmlPath> <username> <password>");
	}
}
