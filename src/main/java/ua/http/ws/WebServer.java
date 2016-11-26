package ua.http.ws;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class WebServer {
    private ServerSocket serverSocket;

    public WebServer(String[] params) {}

    public static void main(String[] args) throws IOException, InterruptedException {
        new WebServer(args).start();
    }

    public void start() throws IOException, InterruptedException {
        serverSocket = new ServerSocket(8080);
        new Thread(() -> {
            try {
                Socket socket = serverSocket.accept();
                RequestReader reader = new RequestReader(socket.getInputStream());
                RequestDeserializer deserializer = new RequestDeserializer();
                Request request = deserializer.deserializeRequest(reader.readSingleRequest());
                RequestHandler handler = new RequestHandler();
                Response response = handler.handle(request);
                ResponseWriter writer = new ResponseWriter(socket.getOutputStream());
                ResponseSerializer serializer = new ResponseSerializer();
                writer.writeSingleResponse(serializer.serialize(response));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void stop() throws IOException {
        serverSocket.close();
    }
}