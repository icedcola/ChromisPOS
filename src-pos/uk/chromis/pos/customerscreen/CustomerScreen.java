/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.chromis.pos.customerscreen;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import uk.chromis.pos.ticket.TicketInfo;
import uk.chromis.pos.ticket.TicketLineInfo;
import uk.chromis.pos.sales.JPanelTicket;

/**
 *
 * @author User
 */
public class CustomerScreen implements ListSelectionListener {
    
    private static JFrame mainFrame = new JFrame();
    private static JPanel ticketPanel = new JPanel();
    private static JPanel idlePanel = new JPanel();
    private static JPanel cardLayoutPane = new JPanel();
    private static CardLayout cardLayout = new CardLayout();
    private static JTable ticketTable;
    private static Object[][] tableData = new Object[1][2];
    private static final String COLUMN_NAMES[] = {"Item", "Price"};
    private JPanelTicket pairedTicketPanel;
    private Timer timer;
    
    private static Integer priceColumnWidth = 250;
    
    public CustomerScreen(JPanelTicket pairedticketPanel) {
        this.pairedTicketPanel = pairedticketPanel;
        
        ticketTable = new JTable(tableData, COLUMN_NAMES);
        mainFrame.setUndecorated(true);
        parseXMLStyle("custscreenimages/layout.xml");
        
        ticketPanel.add(ticketTable, BorderLayout.CENTER);
        
        mainFrame.add(cardLayoutPane);
        
        cardLayoutPane.setLayout(cardLayout);
        cardLayoutPane.add(ticketPanel, "ticketpanel");
        cardLayoutPane.add(idlePanel, "idlepanel");
        
        cardLayout.show(cardLayoutPane, "idlepanel");
        
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
    
    public static void setVisible(Boolean b) {
        mainFrame.setVisible(b);
        
    }
    
    public static void setTicket(TicketInfo ticket) {
        if (ticket == null) {
            cardLayout.show(cardLayoutPane, "idlepanel");
            return;
        }
        
        tableData = new Object[(ticket.getLinesCount())][2];
        
        for (TicketLineInfo ticketline : ticket.getLines()) {
            tableData[ticketline.getTicketLine()][0] = ticketline.getProductName();
            tableData[ticketline.getTicketLine()][1] = ticketline.printPriceTax();
        }
        
        ticketTable.setModel(new DefaultTableModel(tableData, COLUMN_NAMES));

        if (ticket.getLines().isEmpty()) {
            cardLayout.show(cardLayoutPane, "idlepanel");
        } else {
            cardLayout.show(cardLayoutPane, "ticketpanel");
        }
        
        ticketTable.getColumnModel().getColumn(1).setMaxWidth(priceColumnWidth);
        ticketTable.getColumnModel().getColumn(1).setMinWidth(priceColumnWidth);
        ticketTable.repaint();
        
        mainFrame.pack();
        
    }
    
    public static void parseXMLStyle(String filename) {
        Document layoutxml;
        SAXBuilder builder = new SAXBuilder();
        File xmlFile = new File(filename);

        try {
            layoutxml = builder.build(xmlFile);
            
            //Set table properties
            BorderLayout layoutTicketPanel = new BorderLayout();
            BorderLayout layoutIdlePanel = new BorderLayout();
            Integer border = 0;

            if (!"customerscreen".equals(layoutxml.getRootElement().getName())) {
                return;
            }

            ticketTable.setFont(ticketTable.getFont().deriveFont(Float.parseFloat(layoutxml.getRootElement().getAttributeValue("fontsize", "16"))));
            
            priceColumnWidth = Integer.parseInt(layoutxml.getRootElement().getAttributeValue("pricewidth", "250"));
            ticketTable.getColumnModel().getColumn(1).setMaxWidth(priceColumnWidth);
            ticketTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            
            mainFrame.setLocation(Integer.parseInt(layoutxml.getRootElement().getAttributeValue("startx", "0")),
                    Integer.parseInt(layoutxml.getRootElement().getAttributeValue("starty", "0")));
            
            border = Integer.parseInt(layoutxml.getRootElement().getAttributeValue("border", "5"));
            
            layoutTicketPanel.setHgap(border);
            layoutTicketPanel.setVgap(border);
            layoutIdlePanel.setHgap(border);
            layoutIdlePanel.setVgap(border);
            ticketPanel.setLayout(layoutTicketPanel);
            idlePanel.setLayout(layoutIdlePanel);
            
            if ("true".equals(layoutxml.getRootElement().getAttributeValue("fullscreen", "false"))) {
                GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
                gs[Integer.parseInt(layoutxml.getRootElement().getAttributeValue("screenid", "1"))].setFullScreenWindow(mainFrame);
            }

            parseXMLPanel(ticketPanel, layoutxml.getRootElement().getChild("ticketscreen"));
            parseXMLPanel(idlePanel, layoutxml.getRootElement().getChild("idlescreen"));

        } catch (JDOMException | IOException ex) {
            Logger.getLogger(CustomerScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void parseXMLPanel(JPanel panel, Element rootElement) {

        for (Object o : rootElement.getChildren()) {
            Element e = (Element) o;

            if ("imagefile".equals(e.getName())) {
                try {
                    BufferedImage image = ImageIO.read(new File(e.getText()));

                    //Scale image, if the user doesn't give either width or height
                    //then -1 is input, which keeps the ratio.  If both are -1, then original
                    //size is used.
                    JLabel imageLabel = new JLabel(new ImageIcon(image.getScaledInstance(
                            Integer.parseInt(e.getAttributeValue("width", "-1")),
                            Integer.parseInt(e.getAttributeValue("height", "-1")),
                            Image.SCALE_SMOOTH)));
                    panel.add(imageLabel, e.getAttributeValue("position", BorderLayout.PAGE_START));
                } catch (IOException ex) {
                    Logger.getLogger(CustomerScreen.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        setTicket(pairedTicketPanel.getActiveTicket());
    }
}
