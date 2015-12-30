/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.chromis.pos.customerscreen;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
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
    private static final String columnNames[] = {"Item", "Price"};
    private static int itemCount = 0;
    
    public CustomerScreen() {
        System.out.println("Woot");
        ticketTable = new JTable(tableData, columnNames);
        mainFrame.add(ticketTable);
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
        
        ticketTable.setModel(new DefaultTableModel(tableData, columnNames));
    }
    
    public static void addLine(String itemName, String itemPrice) {
        tableData[itemCount][0] = itemName;
        tableData[itemCount][1] = itemPrice;
        itemCount += 1;
        System.out.println("add line...");
        System.out.println(itemName);
        System.out.println(itemPrice);
      
    }
    
    public static void clearLines() {
        tableData = new Object[10][2];
        itemCount = 0;
        System.out.println("clear lines...");
    }
    
}