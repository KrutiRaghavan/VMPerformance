package com.vmware.vim25.mo.samples;

import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.tempuri.Service;
import org.tempuri.ServiceSoap;

import com.vmware.vim25.HostSystemConnectionState;
import com.vmware.vim25.VirtualMachineConnectionState;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.VirtualMachineSnapshot;
import com.vmware.vim25.mo.samples.vm.MigrateVM;

public class PingVM {
    
	public static void main(String[] args) throws Exception
	{
		PingVM pvm= new PingVM();
		pvm.checkHostStatus();
	}
    public void  pingVM(String VMip)
    {
    	InetAddress byname;
		try {
		
			Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 3 "+VMip);
			//This will return 0 if the host is reachable. Otherwise, you will get "2" as a return value.
		
			int returnVal = p1.waitFor();
			boolean reachable = (returnVal==0);
			System.out.println(reachable);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void  checkHostStatus() throws Exception
    {    int aliveVM=0, deadVM=0,aliveHost=0,deadHost=0;
    	InetAddress byname;
    	ArrayList<String> LiveHost =new ArrayList<String>(); 
    	boolean reachable,reachableHosts;
  	    //This will return 0 if the host is reachable. Otherwise, you will get "2" as a return value.
			ServiceInstance si = new ServiceInstance(new URL("https://130.65.132.109/sdk"), "administrator", "12!@qwQW", true);
			Folder rootFolder = si.getRootFolder();
			
			System.out.println("Heartbeat of VMs");

			System.out.println("\n============ Hosts ============");
			ManagedEntity[] hosts = new InventoryNavigator(rootFolder).searchManagedEntities(new String[][] { {"HostSystem", "name" }, }, true);
			//get All VMs from the VCenter
				ManagedEntity[] VMs = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
				//System.out.println("Host["+i+"]=" + hosts[i].getName()+ " :");
				for(int i=0;i<hosts.length;i++)
				{
					System.out.print("Host["+i+"]"+hosts[i].getName()+":");
				Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 "+hosts[i].getName());
				int returnVal1 = p1.waitFor();
				reachableHosts = (returnVal1==0);
				System.out.println(reachableHosts);
				if(reachableHosts)
					{aliveHost++;
					LiveHost.add(hosts[i].getName());
					} 
				else 
				{  
					deadHost++;
				}
				}
				System.out.println("Live Hosts: "+LiveHost);
				System.out.println("\n============ Virtual Machines ============");
				
				for(int j=0;j<VMs.length;j++)
				{
				VirtualMachine vm=(VirtualMachine)VMs[j];
				System.out.print("VM["+j+"]"+VMs[j].getName()+":");
				Process p2 = java.lang.Runtime.getRuntime().exec("ping -c 1 "+vm.getGuest().getIpAddress());
				int returnVal2 = p2.waitFor();
				reachable = (returnVal2==0);
				System.out.print(reachable +" ");
				if(!reachable && vm.getTriggeredAlarmState()!=null)
				{
					
					System.out.print("Alarm triggered in "+vm.getName());	
				}
				else if(!reachable && vm.getTriggeredAlarmState()==null)
				{
					
					System.out.println("VM off and Alarm not triggered in "+vm.getName());
					
					//VMSnapshot vms= new VMSnapshot();
					//VMSnapshot.getSnapshotInTree(vm, vm.getName()+".snapshot");
//					 VirtualMachineSnapshot vmsnap = VMSnapshot.getSnapshotInTree(
//					          vm, vm.getName()+".snapshot");
//					      if(vmsnap!=null)
//					      {
//					        Task task = vmsnap.revertToSnapshot_Task(null);
//					        if(task.waitForMe()==Task.SUCCESS)
//					        {
//					          System.out.println("Reverted to snapshot:" 
//					              + vm.getName()+".snapshot");
//					        }
//					      }
					      System.out.println("Migrating "+vm.getName()+ " to "+LiveHost.get(0)+".......");
					MigrateVM mvm=new MigrateVM();
					mvm.migrateVM(vm.getName(),LiveHost.get(0));
					
				}
				if(reachable)
					aliveVM++; 
				else 
			 		deadVM++;
			   	System.out.print("\n");
			}
			
			System.out.print("Alive VMs:"+ aliveVM);
			System.out.println("Dead VMs:"+ deadVM);
			System.out.print("Alive Hosts:"+ aliveHost);
			System.out.println("Dead Hosts:"+ deadHost);
			si.getServerConnection().logout();
			}
	
 
}


