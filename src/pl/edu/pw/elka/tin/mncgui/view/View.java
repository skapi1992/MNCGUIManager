package pl.edu.pw.elka.tin.mncgui.view;

import pl.edu.pw.elka.tin.MNC.MNCNetworkProtocol.MNCControlEvent;
import pl.edu.pw.elka.tin.mncgui.events.ViewEvent;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Pawel on 2015-01-08.
 */
public class View {

    private JFrame frame;
    private JScrollPane scrollPane;
    private JTextPane text_panel;
    private HTMLDocument doc;
    private JComboBox commandList;
    private JComboBox groupList;
    private JComboBox tokenList;
    private JPanel rightPanel;
    private JPanel leftPanel;
    private JPanel tokenPanel;

    public View(final BlockingQueue<ViewEvent> blockingQueue) {
        text_panel = new JTextPane();
        text_panel.setEditable(false);
        text_panel.setContentType("text/html");
        text_panel.setText("<html>" +
                "<head><style>body{margin:0; padding: 0; font-family: \"Courier New\", Courier, monospace; font-size: 11px;}" +
                ".critical{padding: 2px 0 0 0; margin: 0 0 1px 0; height: 16px; background-color: #000000; color: #FFFFFF;}" +
                ".inUniLog{padding: 2px 0 0 0; margin: 0 0 1px 0; height: 16px; background-color: #1aff14; color: #000000;}" +
                ".outUniLog{padding: 2px 0 0 0; margin: 0 0 1px 0; height: 16px; background-color: #03cdff; color: #000000;}" +
                ".inMulLog{padding: 2px 0 0 0; margin: 0 0 1px 0; height: 16px; background-color: #8fff8c; color: #000000;}" +
                ".outMulLog{padding: 2px 0 0 0; margin: 0 0 1px 0; height: 16px; background-color: #8ce8ff; color: #000000;}" +
                ".event{padding: 2px 0 0 0; margin: 0 0 1px 0; height: 16px; background-color: #ff9292; color: #000000;}</style></head>" +
                "<body>");
        doc = (HTMLDocument)text_panel.getStyledDocument();
        scrollPane = new JScrollPane(text_panel);

        frame = new JFrame("example");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
        frame.setPreferredSize(new Dimension(600, 398));
        frame.setMinimumSize(new Dimension(600, 398));
        Border eBorder = BorderFactory.createEtchedBorder();

        leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder(eBorder, "Log"));
        leftPanel.setPreferredSize(new Dimension(400, 398));
        leftPanel.add(scrollPane);

        rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(200, 438));
        rightPanel.setMaximumSize(new Dimension(200, frame.getMaximumSize().height));
        rightPanel.setMinimumSize(new Dimension(200, frame.getMinimumSize().height));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        {// wciecie dla poprawy czytelnosci - panele wewnetrzne bedace w rightPanel
            JPanel powerPanel = new JPanel();
            powerPanel.setBorder(BorderFactory.createTitledBorder(eBorder, "Power"));
            powerPanel.setPreferredSize(new Dimension(rightPanel.getMaximumSize().width, 100));
            powerPanel.setMinimumSize(new Dimension(rightPanel.getMinimumSize().width, 100));
            powerPanel.setMaximumSize(new Dimension(rightPanel.getMaximumSize().width, 100));
            {// wciecie dla poprawy czytelnosci - elementy wewnetrze powerPanel
                JButton exitBtn = new JButton();
                exitBtn.setBackground(Color.red);
                exitBtn.setPreferredSize(new Dimension(rightPanel.getMaximumSize().width - 20, 30));
                exitBtn.setFocusPainted(false);
                exitBtn.setText("Exit");
                exitBtn.setFont(new Font("Arial", Font.BOLD, 12));
                exitBtn.setActionCommand("exit");
                exitBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ViewEvent event = new ViewEvent(e.getActionCommand());
                        try {
                            blockingQueue.put(event);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                JButton shutDownBtn = new JButton();
                shutDownBtn.setPreferredSize(new Dimension(rightPanel.getMaximumSize().width - 20, 30));
                shutDownBtn.setFocusPainted(false);
                shutDownBtn.setText("Shutdown / Power On");
                shutDownBtn.setFont(new Font("Arial", Font.BOLD, 12));
                shutDownBtn.setActionCommand("shutdown/power_on");
                shutDownBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ViewEvent event = new ViewEvent(e.getActionCommand());
                        try {
                            blockingQueue.put(event);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                powerPanel.add(exitBtn);
                powerPanel.add(shutDownBtn);
            }
            JPanel commandPanel = new JPanel();
            commandPanel.setBorder(BorderFactory.createTitledBorder(eBorder, "Commands"));
            commandPanel.setPreferredSize(new Dimension(rightPanel.getMaximumSize().width, 160));
            commandPanel.setMinimumSize(new Dimension(rightPanel.getMinimumSize().width, 160));
            commandPanel.setMaximumSize(new Dimension(rightPanel.getMaximumSize().width, 160));
            {// wciecie dla poprawy czytelnosci - elementy wewnetrze commandPanel
                commandList = new JComboBox();
                commandList.setPreferredSize(new Dimension(rightPanel.getMaximumSize().width - 20, 30));
                commandList.setEditable(true);

                JLabel groupLbl = new JLabel("Select group");

                groupList = new JComboBox();
                groupList.setPreferredSize(new Dimension(rightPanel.getMaximumSize().width - 20, 30));

                JButton sendBtn = new JButton();
                sendBtn.setPreferredSize(new Dimension(rightPanel.getMaximumSize().width - 20, 30));
                sendBtn.setFocusPainted(false);
                sendBtn.setText("Send Command");
                sendBtn.setFont(new Font("Arial", Font.BOLD, 12));
                sendBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if(!commandList.getSelectedItem().toString().equals("")) {
                            ViewEvent event;
                            String str = commandList.getSelectedItem().toString();
                            int indexOf = str.indexOf(" (");
                            if(indexOf > 0){
                                if (str.substring(0, str.indexOf(" (")).equals("add group")) {
                                    event = new ViewEvent("add group", str.substring(str.indexOf("(")+1, str.indexOf(")")));
                                    try {
                                        blockingQueue.put(event);
                                    } catch (InterruptedException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            } else {
                                if(!groupList.getSelectedItem().toString().equals("")) {
                                    event = new ViewEvent(str, groupList.getSelectedItem().toString());
                                    try {
                                        blockingQueue.put(event);
                                    } catch (InterruptedException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                });

                commandPanel.add(commandList);
                commandPanel.add(groupLbl);
                commandPanel.add(groupList);
                commandPanel.add(sendBtn);
            }

            tokenPanel = new JPanel();
            tokenPanel.setBorder(BorderFactory.createTitledBorder(eBorder, "Tokens"));
            tokenPanel.setPreferredSize(new Dimension(rightPanel.getMaximumSize().width, 100));
            tokenPanel.setMinimumSize(new Dimension(rightPanel.getMinimumSize().width, 100));
            tokenPanel.setMaximumSize(new Dimension(rightPanel.getMaximumSize().width, 100));
            {// wciecie dla poprawy czytelnosci - elementy wewnetrze tokenPanel
                tokenList = new JComboBox();
                tokenList.setPreferredSize(new Dimension(rightPanel.getMaximumSize().width - 20, 30));

                JButton showBtn = new JButton();
                showBtn.setPreferredSize(new Dimension(rightPanel.getMaximumSize().width - 20, 30));
                showBtn.setFocusPainted(false);
                showBtn.setText("Show Token");
                showBtn.setActionCommand("show_token");
                showBtn.setFont(new Font("Arial", Font.BOLD, 12));
                showBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ViewEvent event = new ViewEvent(e.getActionCommand(), "", tokenList.getSelectedItem().toString());
                        try {
                            blockingQueue.put(event);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                tokenPanel.add(tokenList);
                tokenPanel.add(showBtn);
            }

            rightPanel.add(powerPanel);
            rightPanel.add(commandPanel);
        }
    }

    public void setVisible(String title, boolean isMonitor) {
        frame.setTitle(title);
        String[] monitorCommands = {"add group (name)", "remove group"};
        String[] driverCommands = {"add group (name)", "remove group", "send data", "force token transfer"};
        if(isMonitor) {
            for (int i = 0; i < monitorCommands.length; i++) {
                commandList.addItem(monitorCommands[i]);
            }
        }else {
            for (int i = 0; i < driverCommands.length; i++) {
                commandList.addItem(driverCommands[i]);
            }
            rightPanel.add(tokenPanel);
        }
        frame.add(leftPanel);
        frame.add(rightPanel);
        frame.pack();
        frame.setVisible(true);
    }

    public void addGroups(String[] groupNames) {
        for (int i = 0; i < groupNames.length; i++) {
            groupList.addItem(groupNames[i]);
        }
    }

    public void removeGroups() {
        groupList.removeAllItems();
    }

    public void addTokens(String[] tokenNames) {
        for (int i = 0; i < tokenNames.length; i++) {
            tokenList.addItem(tokenNames[i]);
        }
    }

    public void removeTokens() {
        tokenList.removeAllItems();
    }

    public void showDialog(String msg, String title) {
        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public void insertLog(String log, MNCControlEvent.TYPE type) {
        String cssClass;
        if(type == MNCControlEvent.TYPE.ReceiveFromMulticast){
            cssClass = "inMulLog";
        }else if(type == MNCControlEvent.TYPE.SendByMulticast) {
            cssClass = "outMulLog";
        }else if(type == MNCControlEvent.TYPE.ReceiveFromUnicast){
            cssClass = "inUniLog";
        }else if(type == MNCControlEvent.TYPE.SendByUnicast){
            cssClass = "outUniLog";
        }else if(type == MNCControlEvent.TYPE.ControllerStarted){
            cssClass = "critical";
        }else if(type == MNCControlEvent.TYPE.ControllerStoped){
            cssClass = "critical";
        }else{
            cssClass = "event";
        }
            Element e = doc.getElement(doc.getDefaultRootElement(), StyleConstants.NameAttribute, HTML.Tag.BODY);
        try {
            doc.insertBeforeEnd(e, "<div class="+cssClass+">"+log+"</div>");
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if (shouldScroll()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
                }

            });
        }
    }

    public void dispose() {
        frame.dispose();
    }

    private boolean shouldScroll() {
        int minimumValue = scrollPane.getVerticalScrollBar().getValue() + scrollPane.getVerticalScrollBar().getVisibleAmount();
        int maximumValue = scrollPane.getVerticalScrollBar().getMaximum();
        if(text_panel.getSelectedText() != null)
            return false;
        return maximumValue == minimumValue;
    }
}
