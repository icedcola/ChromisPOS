/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.chromis.pos.customerscreen;

import java.awt.BorderLayout;
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
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import uk.chromis.pos.ticket.TicketInfo;
import uk.chromis.pos.ticket.TicketLineInfo;

/**
 *
 * @author User
 */
public class CustomerScreen {
    
    private static JFrame mainFrame = new JFrame();
    private static JTable ticketTable;
    private static Object[][] tableData = new Object[1][2];
    private static final String COLUMN_NAMES[] = {"Item", "Price"};
    
    public CustomerScreen() {
        
        
        ticketTable = new JTable(tableData, COLUMN_NAMES);
        
        parseXMLStyle("custscreenimages/layout.xml");
        
        mainFrame.add(ticketTable, BorderLayout.CENTER);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
    
    public static void setVisible(Boolean b) {
        mainFrame.setVisible(b);
        
    }
    
    public static void setTicket(TicketInfo ticket) {
        tableData = new Object[(ticket.getLinesCount())][2];
        
        for (TicketLineInfo ticketline : ticket.getLines()) {
            tableData[ticketline.getTicketLine()][0] = ticketline.getProductName();
            tableData[ticketline.getTicketLine()][1] = "$".concat(String.valueOf(ticketline.getValue()));
        }
        
        ticketTable.setModel(new DefaultTableModel(tableData, COLUMN_NAMES));
    }
    
    public static void parseXMLStyle(String filename) {
        Document layoutxml;
        SAXBuilder builder = new SAXBuilder();
        File xmlFile = new File(filename);
        
        
             
        try {
            layoutxml = builder.build(xmlFile);
            
            //Set table properties
            BorderLayout layout = new BorderLayout();

            layout.setHgap(Integer.parseInt(layoutxml.getRootElement().getAttributeValue("border", "5")));
            layout.setVgap(Integer.parseInt(layoutxml.getRootElement().getAttributeValue("border", "5")));
            
            ticketTable.setFont(ticketTable.getFont().deriveFont(Integer.parseInt(layoutxml.getRootElement().getAttributeValue("fontsize", "16"))));
             
            mainFrame.setLayout(layout);

            for (Object o : layoutxml.getRootElement().getChildren()) {
                Element e = (Element) o;
                if ("imagefile".equals(e.getName())) {
                    BufferedImage image = ImageIO.read(new File(e.getText()));

                    //Scale image, if the user doesn't give either width or height
                    //then -1 is input, which keeps the ratio.  If both are -1, then original
                    //size is used.
                    JLabel imageLabel = new JLabel(new ImageIcon(image.getScaledInstance(
                            Integer.parseInt(e.getAttributeValue("width", "-1")),
                            Integer.parseInt(e.getAttributeValue("height", "-1")),
                            Image.SCALE_SMOOTH)));
                    mainFrame.add(imageLabel, e.getAttributeValue("position", BorderLayout.PAGE_START));
                }
            }

        } catch (JDOMException ex) {
            Logger.getLogger(CustomerScreen.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CustomerScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        
    }

    private static Object getValueOrDefault(Object value, Object defaultValue) {
        System.out.println(value);
        System.out.println(defaultValue);
        return (value != null) ? value : defaultValue;
    }
}
