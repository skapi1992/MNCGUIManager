package pl.edu.pw.elka.tin.MNC.MNCNetworkProtocol;

import java.io.Serializable;

/**
 * Created by przemek on 11.01.15.
 */
public class MNCControlEvent implements Serializable{
    public static enum TYPE{
        ReceiveFromMulticast, Command, Start
    }

    public final TYPE type;
    public final Object data;
    public String group = null;

    public MNCControlEvent(TYPE t, Object d){
        type = t;
        data = d;
    }

    public MNCControlEvent(TYPE t, Object d, String group){
        type = t;
        data = d;
        this.group = group;
    }

    public TYPE getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public String getGroup() {
        return group;
    }
}
