package com.company;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class Main {

    static Backend backend;

    public static void main(String[] args) throws Exception {
	    backend = new Backend();

	    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	    String command = "";

	    while( !(command = reader.readLine()).equals("quit") ) {
	        switch(command) {
                case "login": login(); break;
                case "logout": logout(); break;
                case "getClients": getClients(); break;
            }
        }
    }

    private static void login() {
        backend.login("Mike");
    }

    private static void logout() {
        backend.logout("Mike");
    }

    private static void getClients() {
        List<String> clients = backend.getClients();

        for(String str : clients) {
            System.out.println("Client " + str + " is online");
        }
    }
}
