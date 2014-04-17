package com.vmware.vim25.mo.samples;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class HostOps {
	
	 public void PowerOffHost() throws RemoteException, MalformedURLException, InterruptedException
	 {
		 ServiceInstance si = new ServiceInstance(new URL("https://130.65.132.210/sdk"), "administrator", "12!@qwQW", true);
			Folder rootFolder = si.getRootFolder();
	        System.out.println(rootFolder.getName());
	        //VirtualMachine vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine","130.65.132.211");
	        HostSystem vm = (HostSystem) new InventoryNavigator(rootFolder).searchManagedEntity("HostSystem","130.65.132.219");
	        System.out.println(vm.getName());
	        Task task= vm.shutdownHost_Task(true);
	        
	        //Task task= vm.powerDownHostToStandBy(30, true);
	        //task.wait();
	       // task.waitForMe();
	        task.waitForTask();
	        System.out.println("Host" + vm.getName() + " powered off.");
	 }
	 
	 public void PowerOnHost() throws RemoteException, MalformedURLException
	 {
		 ServiceInstance si = new ServiceInstance(new URL("https://130.65.132.210/sdk"), "administrator", "12!@qwQW", true);
			Folder rootFolder = si.getRootFolder();
	        System.out.println(rootFolder.getName());
	        //VirtualMachine vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine","130.65.132.211");
	        HostSystem vm = (HostSystem) new InventoryNavigator(rootFolder).searchManagedEntity("HostSystem","130.65.132.219");
	        System.out.println(vm.getName());
	        Task task= vm.powerUpHostFromStandBy(2);
	        System.out.println("Host" + vm.getName() + " powered on.");
	 }

public static void main(String args[]) throws RemoteException, MalformedURLException
{
	HostOps ho=new HostOps();
	try {
		ho.PowerOffHost();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
}
