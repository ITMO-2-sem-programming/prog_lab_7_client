package ru.itmo.main;

import ru.itmo.common.User;
import ru.itmo.common.exchange.request.CommandRequest;
import ru.itmo.common.exchange.request.Request;
import ru.itmo.common.exchange.response.CommandResponse;
import ru.itmo.common.exchange.response.Response;
import ru.itmo.connection.ConnectionManager;
import ru.itmo.connection.Serializer;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamCorruptedException;
import java.util.Iterator;


public class Main {

    private static ConnectionManager connectionManager;
    private static User user;


    public static void main(String[] args) {

        System.out.println("Client is running...");

        Validator.setSymbolsForNullValues(new String[] {""});

        try {
            connectionManager = new ConnectionManager("localhost", 44231);

        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }


        String command;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        UserManager um = new UserManager();

        boolean userRecognised = false;
        while (!userRecognised) {
            try {
                user = um.recogniseUser(connectionManager, reader);
//                user = new User("maxim", "maxim");
                userRecognised = true;
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }


        while (true) {
            try {
                System.out.println("Enter command : ");
                command = reader.readLine();
                System.out.println(runCommand(command));
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println("Error: Unknown error during reading the input was occurred.");
            }
        }

    }


    public static String runCommand(String command) { // Метод необходим для выполнения команд из файла.
        return runCommand(connectionManager, user, command, false, null);
    }


    public static String runCommand(ConnectionManager connectionManager, User user, String command, boolean executionOfScript, Iterator<String> iterator) {

        CommandRequest commandRequest;
        CommandResponse commandResponse;
        Request request;
        Response response;


        try {

            commandRequest = CommandManager.executeCommand(command, executionOfScript, iterator);

            if (commandRequest != null) {
                try {
                    commandRequest.setUser(user);
                    request = new Request(commandRequest);
                    connectionManager.sendRequest(request);

                    while (true) {
                        try {
                            response = connectionManager.receiveResponse();
                            break;
                        } catch (StreamCorruptedException ignored) {}
                    }

                    System.out.println(response);

                    return response.getCommandResponse().getMessage();

                } catch (IOException e) {
                    throw new IllegalArgumentException("Error: Can't connect server.");
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
            return "";
        } catch (IllegalArgumentException | IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    public static ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public static User getUser() {
        return user;
    }
}


