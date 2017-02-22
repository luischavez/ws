package net.frostleviathan.ws;

import oracle.forms.api.FException;
import oracle.forms.handler.IHandler;
import oracle.forms.properties.ID;
import oracle.forms.ui.VBean;

/**
 *
 * @author Frost
 */
public class MapBean extends VBean implements SocketListener {

    private static final ID POSITION_EVENT = ID.registerProperty("POSITION_EVENT");

    private static final ID LATITUDE_ID = ID.registerProperty("LATITUDE");
    private static final ID LONGITUDE_ID = ID.registerProperty("LONGITUDE");

    private IHandler handler;

    private float latitude;
    private float longitude;

    @Override
    public void init(IHandler ih) {
        super.init(ih);
        
        WebServer webServer = new WebServer(8081);
        webServer.startWebServer();

        SocketServer socketServer = new SocketServer(8887);
        socketServer.addListener(this);
        socketServer.startSocket();

        handler = ih;
    }

    @Override
    public void onPosition(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;

        try {
            PositionEvent event = new PositionEvent(handler, POSITION_EVENT);
            handler.setProperty(LATITUDE_ID, String.valueOf(latitude));
            handler.setProperty(LONGITUDE_ID, String.valueOf(longitude));

            dispatchCustomEvent(event);
        } catch (FException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Object getProperty(ID id) {
        if (LONGITUDE_ID == id) {
            return longitude;
        }
        if (LATITUDE_ID == id) {
            return latitude;
        }

        return super.getProperty(id);
    }

    @Override
    public boolean setProperty(ID id, Object o) {
        if (LONGITUDE_ID == id) {
            longitude = Float.valueOf((String) o);
        }
        if (LATITUDE_ID == id) {
            latitude = Float.valueOf((String) o);
        }

        return true;
    }

    public static void main(String... args) {
        MapBean bean = new MapBean();
        bean.init(null);
    }
    
}
