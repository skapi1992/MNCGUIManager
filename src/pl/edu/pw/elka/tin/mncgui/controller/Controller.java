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
            this.in = new ObjectInputStream(clientSocket.getInputStream());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void run() {
        //TODO odczytac info na temat czy polaczyl sie monitor czy sterownik i wywolac view wypelnionym poprawnie tytulem oraz parametrem czy to monitor czy kontroler
        String[] tokenNames= {"token1", "token2"};
        String[] groupNames= {"group1", "group2", "group3"};
        this.view = new View("example", blockingQueue, true, groupNames, tokenNames);
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
        if(event.getCommand() == "exit") {
            view.dispose();
            setWorking(false);
            try {
                clientSocket.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }else if(event.getCommand() == "shutdown/power_on") {
            //TODO
        }else if(event.getCommand() == "show_token") {
            //TODO
        }else{
            //TODO sytuacja gdy chemy wywolac jakies zadanie na grupie
        }
    }

    public void sendCommand(MNCControlEvent event){
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
            //TODO
            view.insertLog((String)mncControlEvent.getData());
        }
    }

    private class DataReceiver implements Runnable{

        @Override
        public void run(){
            while(getWorking()) {
                try {
                    MNCControlEvent mncControlEvent = (MNCControlEvent) in.readObject();
                    if(mncControlEvent == null) {
                        setWorking(false);
                        break;
                    }
                    receivedData.put(mncControlEvent);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}