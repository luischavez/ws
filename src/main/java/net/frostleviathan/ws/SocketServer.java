package net.frostleviathan.ws;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Frost
 */
public class SocketServer extends WebSocketServer {
    
    private List<SocketListener> listeners;
    
    private float latitude;
    private float longitude;

    public SocketServer(int port) {
        super(new InetSocketAddress(port));
        listeners = new ArrayList<>();
    }
    
    public void addListener(SocketListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(SocketListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    @Override
    public void onOpen(WebSocket ws, ClientHandshake ch) {
        System.out.println("Se inicio el servidor");
    }

    @Override
    public void onClose(WebSocket ws, int code, String reason, boolean remote) {
        System.out.println(ws + " se desconecto del servidor!");
    }

    @Override
    public void onMessage(WebSocket ws, String message) {
        System.out.println("Mensaje: " + message);

        JSONParser parser = new JSONParser();

        try {
            JSONObject object = (JSONObject) parser.parse(message);

            latitude = Float.valueOf(object.get("latitude").toString());
            longitude = Float.valueOf(object.get("longitude").toString());
            
            for (SocketListener listener : listeners) {
                listener.onPosition(latitude, longitude);
            }

            send(latitude, longitude);
        } catch (ParseException ex) {
            ws.close();
            ex.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket ws, Exception exception) {
        System.err.println("Ocurrio un error!");
    }

    protected void send(float latitude, float longitude) {
        JSONObject object = new JSONObject();
        object.put("latitude", latitude);
        object.put("longitude", longitude);
        String json = object.toJSONString();

        Collection<WebSocket> connections = connections();
        synchronized (connections) {
            for (WebSocket connection : connections) {
                connection.send(json);
            }
        }
    }
    
    public void startSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SocketServer.this.start();
            }
        }).start();
    }

}
