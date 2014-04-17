package com.vmware.vim25.mo.samples;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Date;

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.VirtualMachineSnapshotInfo;
import com.vmware.vim25.VirtualMachineSnapshotTree;
import com.vmware.vim25.mo.*;


public class VMSnapshot {
	
	public static void main(String[] args) throws RemoteException, MalformedURLException
	{
		VMSnapshot vms= new VMSnapshot();
		vms.addSnapshot("130.65.132.109");
	}
	
	
	public void refreshSnapshot(String host) throws RemoteException, MalformedURLException
	{  try {
		removeSnapshot(host);
	
		Thread.sleep(5000);
	
	addSnapshot(host);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		System.out.println("Snapshot is refreshed!");
	}
	synchronized public void addSnapshot(String host) throws RemoteException, MalformedURLException
	{
		ServiceInstance si = new ServiceInstance(
				new URL("https://130.65.132.109/sdk"),"administrator" ,"12!@qwQW", true);		
		Folder rootFolder = si.getRootFolder();
		String name = rootFolder.getName();
		System.out.println("root:" + name);
		ManagedEntity mes[]=new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		for(int i=0;i<mes.length;i++)
		{
		VirtualMachine vm = (VirtualMachine)mes[i];
		System.out.println("VM "+i+": "+ vm.getName());
		java.util.Date date= new java.util.Date();
		vm.createSnapshot_Task(vm.getName()+".snapshot", "Snapshot refreshed on "+new Timestamp(date.getTime()), false, false);
		System.out.println("Snapshot for "+vm.getName()+" is taken!");
		}
		
		
		
	}
	synchronized public void removeSnapshot(String host) throws RemoteException, MalformedURLException
	{
		ServiceInstance si = new ServiceInstance(
				new URL("https://130.65.132.109/sdk"),"administrator" ,"12!@qwQW", true);
		Folder rootFolder = si.getRootFolder();
		ManagedEntity mes[]=new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		for(int i=0;i<mes.length;i++)
		{
		VirtualMachine vm = (VirtualMachine)mes[i];
		
		vm.removeAllSnapshots_Task();
		System.out.println("Snapshot for "+vm.getName()+" is removed!");
		}
	}
	
	
	 static VirtualMachineSnapshot getSnapshotInTree(
		      VirtualMachine vm, String snapName)
		  {
		    if (vm == null || snapName == null) 
		    {
		      return null;
		    }

		    VirtualMachineSnapshotTree[] snapTree = 
		        vm.getSnapshot().getRootSnapshotList();
		    if(snapTree!=null)
		    {
		      ManagedObjectReference mor = findSnapshotInTree(
		          snapTree, snapName);
		      if(mor!=null)
		      {
		        return new VirtualMachineSnapshot(
		            vm.getServerConnection(), mor);
		      }
		    }
		    return null;
		  }

   static ManagedObjectReference findSnapshotInTree(
		      VirtualMachineSnapshotTree[] snapTree, String snapName)
		  {
		    for(int i=0; i <snapTree.length; i++) 
		    {
		      VirtualMachineSnapshotTree node = snapTree[i];
		      if(snapName.equals(node.getName()))
		      {
		        return node.getSnapshot();
		      } 
		      else 
		      {
		        VirtualMachineSnapshotTree[] childTree = 
		            node.getChildSnapshotList();
		        if(childTree!=null)
		        {
		          ManagedObjectReference mor = findSnapshotInTree(
		              childTree, snapName);
		          if(mor!=null)
		          {
		            return mor;
		          }
		        }
		      }
		    }
		    return null;
		  }

static void listSnapshots(VirtualMachine vm)
		  {
		    if(vm==null)
		    {
		      return;
		    }
		    VirtualMachineSnapshotInfo snapInfo = vm.getSnapshot();
		    VirtualMachineSnapshotTree[] snapTree = 
		      snapInfo.getRootSnapshotList();
		    printSnapshots(snapTree);
		  }

		  static void printSnapshots(
		      VirtualMachineSnapshotTree[] snapTree)
		  {
		    for (int i = 0; snapTree!=null && i < snapTree.length; i++) 
		    {
		      VirtualMachineSnapshotTree node = snapTree[i];
		      System.out.println("Snapshot Name : " + node.getName());           
		      VirtualMachineSnapshotTree[] childTree = 
		        node.getChildSnapshotList();
		      if(childTree!=null)
		      {
		        printSnapshots(childTree);
		      }
		    }
		  }
	
	

}
