package com.monitor.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MonitorGUI {
	
	static JTable tableLamp;
	static DefaultTableModel modelLamp;
	
	static JTable tableLoad;
	static DefaultTableModel modelLoad;
	
	static JFrame frame;
	
	public void configureFrame(){
		
		frame = new JFrame();
		
		JTabbedPane tabbedPane = new JTabbedPane();
		JPanel lampsPanel = new JPanel();
		modelLamp = new DefaultTableModel(); 
		
		modelLamp.addColumn("Time");
		modelLamp.addColumn("Address"); 
		modelLamp.addColumn("Pincode"); 
		modelLamp.addColumn("LampId");
		
		tableLamp = new JTable(modelLamp); 
		
		tableLamp.getColumnModel().getColumn(0).setPreferredWidth(200);
		tableLamp.getColumnModel().getColumn(1).setPreferredWidth(200);
		tableLamp.getColumnModel().getColumn(2).setPreferredWidth(100);
		tableLamp.getColumnModel().getColumn(3).setPreferredWidth(100);
	
		lampsPanel.add(tableLamp.getTableHeader());
		lampsPanel.add(tableLamp,BorderLayout.CENTER);
		
		JPanel electricityLoadPanel = new JPanel();
		modelLoad = new DefaultTableModel();
		modelLoad = new DefaultTableModel();
		
		modelLoad.addColumn("Time");
		modelLoad.addColumn("Address"); 
		modelLoad.addColumn("Pincode"); 
		modelLoad.addColumn("TransformerId");
		modelLoad.addColumn("value");
		tableLoad = new JTable(modelLoad); 
		
		tableLoad.getColumnModel().getColumn(0).setPreferredWidth(200);
		tableLoad.getColumnModel().getColumn(1).setPreferredWidth(200);
		tableLoad.getColumnModel().getColumn(2).setPreferredWidth(100);
		tableLoad.getColumnModel().getColumn(3).setPreferredWidth(100);
		tableLoad.getColumnModel().getColumn(4).setPreferredWidth(100);
		
		electricityLoadPanel.add(tableLoad.getTableHeader());
		electricityLoadPanel.add(tableLoad,BorderLayout.CENTER);
		tabbedPane.addTab("Street Lamps", lampsPanel);
		tabbedPane.addTab("Transformer Load", electricityLoadPanel);
		
		frame.add(tabbedPane);
		frame.setResizable(false);
		frame.setSize(700,500);
		frame.setVisible(true);
		
		}
	
	
	/*public void configureFrame(){
		JFrame f = new JFrame();
	    f.setLayout(new BorderLayout());
	    final JPanel p = new JPanel();
	    p.add(new JLabel("A Panel"));
	    f.add(p, BorderLayout.CENTER);

	    //create a button which will hide the panel when clicked.
	    JButton b = new JButton("HIDE");
	    b.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e){
	                p.setVisible(false);
	        }
	    });
	    
	    //JTable
	    Object rowData[][] = { { "Row1-Column1", "Row1-Column2", "Row1-Column3" },
	            { "Row2-Column1", "Row2-Column2", "Row2-Column3" } };
	    Object columnNames[] = { "Column One", "Column Two", "Column Three" };
	    JTable table = new JTable(rowData, columnNames);
	    JTable table = new JTable();
	    
	    f.add(b,BorderLayout.SOUTH);
	    f.add(table,BorderLayout.WEST);
	    f.setSize(400,500);
	   // f.pack();
	    f.setVisible(true);
	}
	*/
	public static void main(String args[]){
		MonitorGUI instance = new MonitorGUI();
		instance.configureFrame();
	}
	

}
