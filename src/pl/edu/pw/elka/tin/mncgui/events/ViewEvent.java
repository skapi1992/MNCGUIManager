package pl.edu.pw.elka.tin.mncgui.events;

/**
 * View communicate with Controller using this object
 * Created by Pawel Stepien on 2015-01-10.
 */
public class ViewEvent {

    private String command = "";
    private String group = "";
    private String token = "";

    public ViewEvent() {
    }

    public ViewEvent(String command) {
        this.command = command;
    }

    public ViewEvent(String command, String group) {
        this.command = command;
        this.group = group;
    }

    public ViewEvent(String command, String group, String token) {
        this.command = command;
        this.group = group;
        this.token = token;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
