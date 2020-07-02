package ru.itmo.connection;


import ru.itmo.common.exchange.request.Request;
import ru.itmo.common.exchange.response.Response;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

// Client
public class ConnectionManager {

    private final DatagramChannel clientChannel;
    private final InetSocketAddress serverSocketAddress;

    private ByteBuffer buffer;
    private int bufferSize;


    public ConnectionManager(String serverAddress, int serverPort) throws IOException {

        // bind client to any empty port


        try {

            clientChannel = DatagramChannel.open();
            clientChannel.socket().bind(null); // Set to null later

            System.out.println("Client address : " + clientChannel.getLocalAddress());

            clientChannel.configureBlocking(true); // false // Difference from the 6th laB !!********************
        } catch (IOException e) {
            throw new IOException("Error: Can't connect any free port on the client-side.");
        }

        try {
            serverSocketAddress = new InetSocketAddress(serverAddress, serverPort);
//            clientChannel.connect(serverSocketAddress);
        } catch (IllegalArgumentException e) {
            throw new IOException("Can't connect to server.\n    Reason: " + e.getMessage());
        }

        bufferSize = 65536;

        buffer = ByteBuffer.allocate(bufferSize);
    }


    public void send(byte[] bytes) throws IOException {
//        checkConnection();
        buffer = ByteBuffer.allocate(bufferSize);

        try {
            buffer.put(bytes);
            buffer.flip();
        } catch (BufferOverflowException e) {
            throw new IllegalArgumentException("Error: ByteBuffer is too small to put bytes in it.");
        }

        clientChannel.send(buffer, serverSocketAddress);

    }


    public void sendRequest(Request request) throws IOException {
        send(Serializer.toByteArray(request));
    }


    public byte[] receive() throws IOException {
        buffer = ByteBuffer.allocate(bufferSize);
        SocketAddress readStatus = clientChannel.receive(buffer);

        if (readStatus == null)
            throw new StreamCorruptedException("Error: Channel is empty. Can't read anything.");

        return buffer.array();
    }


    public Response receiveResponse() throws IOException, ClassNotFoundException {
        return (Response) Serializer.toObject(receive());
    }


    private void checkConnection() {
         if (!clientChannel.isConnected()) throw new IllegalArgumentException("Can't connect server.");
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

}