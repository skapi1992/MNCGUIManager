package pl.edu.pw.elka.tin.mncgui.controller;

import pl.edu.pw.elka.tin.MNC.MNCNetworkProtocol.MNCControlEvent;
import pl.edu.pw.elka.tin.mncgui.model.Model;
import pl.edu.pw.elka.tin.mncgui.view.View;
import pl.edu.pw.elka.tin.mncgui.events.ViewEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
/*
Koncepcja na kolorowanie skladni itp.
otrzymane komendy:

    krytyczne (jeden kolor)
        - power on
        - shutdown
    logi
        - wchodzace polaczenia (jakis kolor)
        - wychodzace polaczenia (jakis kolor)

    wazne/decyzyjne (jakis kolor)
        - wzialem token
        - oddalem token
        - dodalem grupe
        - odszedlem z grupy
*/

/**
 * MVC Controller Class
 * Created by Pawel Stepien on 2015-01-08.
 */
public class Controller implements Runnable{

    private Socket clientSocket = null;
    private Model model;
    private View view;
    private final BlockingQueue<ViewEvent> blockingQueue;
    private final BlockingQueue<MNCControlEvent> receivedData;
    private boolean working = true;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;

    public Controller(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.blockingQueue = new ArrayBlockingQueue<ViewEvent>(10);
        this.receivedData = new LinkedBlockingQueue<MNCControlEvent>();
        this.model = new Model();
        new Thread(new DataReceiver()).start();
        try {
            this.out = new ObjectOutputStream(clientSocket.getOutputStream());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void run() {
        this.view = new View(blockingQueue);
        new Thread(new WatchingDriverEvents()).start();
        startWatchingViewEvents();
    }

    /**
     * Method to get commands from View
     */
    public void startWatchingViewEvents(){
        while(getWorking())
        {
            try
            {
                ViewEvent event = blockingQueue.take();
                reactOnViewEvent(event);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public synchronized void setWorking(boolean working){
        this.working = working;
    }

    public synchronized boolean getWorking(){
        return working;
    }

    public void reactOnViewEvent(ViewEvent event) {
        String[] groupName = new String[1];
        if(event.getCommand() == "exit") {
            view.dispose();
            setWorking(false);
        }else if(event.getCommand().equals("shutdown/power_on")) {
            sendCommand(new MNCControlEvent(MNCControlEvent.TYPE.Command, event.getCommand()));
        }else if(event.getCommand().equals("show_token")) {
            groupName[0] = event.getToken();
            sendCommand(new MNCControlEvent(MNCControlEvent.TYPE.Command, event.getCommand(), groupName));
        }else if(event.getCommand().equals("add group")) {
            groupName[0] = event.getGroup();
            sendCommand(new MNCControlEvent(MNCControlEvent.TYPE.Command, event.getCommand(), groupName));
        }else if(event.getCommand().equals("remove group")) {
            groupName[0] = event.getGroup();
            sendCommand(new MNCControlEvent(MNCControlEvent.TYPE.Command, event.getCommand(), groupName));
        }else if(event.getCommand().equals("send data")) {
            groupName[0] = event.getGroup();
            sendCommand(new MNCControlEvent(MNCControlEvent.TYPE.Command, event.getCommand(), groupName));
        }else if(event.getCommand().equals("force token transfer")) {
            groupName[0] = event.getGroup();
            sendCommand(new MNCControlEvent(MNCControlEvent.TYPE.Command, event.getCommand(), groupName));
        }
    }

    public void sendCommand(MNCControlEvent event){
        if(getWorking())
            try {
                out.writeObject(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    /**
     * Class reacting on commands from drivers
     */
    private class WatchingDriverEvents implements Runnable{

        @Override
        public void run() {
            while(getWorking())
            {
                try
                {
                    MNCControlEvent mncControlEvent = receivedData.take();
                    reactOnDriverEvent(mncControlEvent);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }

        public void reactOnDriverEvent(MNCControlEvent mncControlEvent){
            if(mncControlEvent.getType() == MNCControlEvent.TYPE.Start) {
                view.setVisible(mncControlEvent.getName(), (Boolean)mncControlEvent.getData());
                view.addGroups(mncControlEvent.getGroup());
            }else if(mncControlEvent.getType() == MNCControlEvent.TYPE.MyGroups) {
                view.removeGroups();
                view.addGroups(mncControlEvent.getGroup());
            }else if(mncControlEvent.getType() == MNCControlEvent.TYPE.MyTokens) {
                view.removeTokens();
                view.addTokens(mncControlEvent.getGroup());
            }else if(mncControlEvent.getType() == MNCControlEvent.TYPE.TokenInfo) {
                view.showDialog((String)mncControlEvent.getData(),"Token: "+mncControlEvent.getGroup()[0]);
            }
            //TODO obsluga dodaj/usun token/grupe
        }
    }

    private class DataReceiver implements Runnable{

        @Override
        public void run(){
            try {
                in = new ObjectInputStream(clientSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(getWorking()) {
                try {
                    MNCControlEvent mncControlEvent = (MNCControlEvent) in.readObject();
                    if(mncControlEvent == null) {
                        setWorking(false);
                        break;
                    }
                    if(getWorking())
                        receivedData.put(mncControlEvent);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}