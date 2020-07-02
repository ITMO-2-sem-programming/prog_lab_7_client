//package ru.itmo.deprecated;
//
//import ru.itmo.connection.ConnectionManager;
//import ru.itmo.connection.UserManager;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//
//public class Client {
//
//    public static void main(String[] args) throws IOException {
//        ConnectionManager con = new ConnectionManager("localhost", 9100);
//        UserManager um = new UserManager();
//        um.recogniseUser(con, new BufferedReader(new InputStreamReader(System.in)));
//    }
//}
