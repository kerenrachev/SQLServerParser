 
package Handlers;

import Objects.DBInfo;
import Objects.Machine; 
import SQL.SQLMachine;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class MachinesHandler{
  
    private ArrayList<Machine>  machines;
    private Machine             tempMachine;
    
    private final DefaultTableModel   machinesTableModel ;  
    private final String              tableName;
    private final DBInfo              connectionInfo;
    
    //Used to connect into the machine database
    private SQLMachine    sqlMachineConnector;
    
    public MachinesHandler(DefaultTableModel machinesTableModel,DBInfo connectionInfo)
    {
        this.machinesTableModel = machinesTableModel;
        this.connectionInfo     = connectionInfo;
        this.tableName          = connectionInfo.getTableMachines(); 
    }  
    private boolean ready(){
          return (sqlMachineConnector != null &&  sqlMachineConnector.hasConnectedToDb());
    }
    private void connect(){
         sqlMachineConnector = new SQLMachine(connectionInfo);  
    } 
    public ArrayList<String> getMachineNames(){
        if(!ready()) connect();
        return sqlMachineConnector.getMachineNames(tableName);
    }
    public int getIdByMachineName(String name){ 
        if(!ready()) connect();
        machines            = sqlMachineConnector.getDataBase(tableName); 
        Machine temp;
        for(int i = 0 ; i < machines.size(); i++){
            temp = machines.get(i);
            System.out.println(temp.getNameOfMachine() + " " + name);
            if(temp.getNameOfMachine().equals(name))
                return temp.getNumOfMachine();
        }
        return -1;
    }
    public void addMachine(String name,int machineNum,int speed,int charge,double cut, double bend ){
         if(sqlMachineConnector.addMachine(tableName,name,machineNum,speed,cut,bend,charge)){
            JOptionPane.showMessageDialog(null,"Success!");
            grabMachinesToTable(); 
        }else
            JOptionPane.showMessageDialog(null,"Failed!"); 
    } 
    public void removeMachine(int index){  
        String    machineName = (String)  machinesTableModel.getValueAt(index,0);
        int       machineId   = (Integer) machinesTableModel.getValueAt(index,5); 
        if(machineName.length() > 0  ){
            for (Machine machine : machines) {
                tempMachine = machine;
                System.out.println(tempMachine.getNameOfMachine() + " - " + tempMachine.getNumOfMachine());
                if(tempMachine.getNameOfMachine().equals(machineName) && machineId == tempMachine.getNumOfMachine())
                { 
                    if(sqlMachineConnector.deleteMachine(tableName,machineId)){
                        JOptionPane.showMessageDialog(null,"Success!");
                        grabMachinesToTable();
                    }else JOptionPane.showMessageDialog(null,"Failed!");
                    break;
                }
            }
        }
    }
    public void grabMachinesToTable(){
        machinesTableModel.setRowCount(0);
         
        if(!ready())
            connect();  
        machines            = sqlMachineConnector.getDataBase(tableName); 
        for (Machine machine : machines) {
            tempMachine = machine;
            machinesTableModel.addRow(new Object[]  {tempMachine.getNumOfMachine(),tempMachine.getMachineChargingTime(),tempMachine.getMachineBendingTime(),tempMachine.getMachineCutingTime(),tempMachine.getMachineSpeed(),tempMachine.getNameOfMachine()});
        } 
    }
}