/*================================================================================
Copyright (c) 2008 VMware, Inc. All Rights Reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
this list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice, 
this list of conditions and the following disclaimer in the documentation 
and/or other materials provided with the distribution.

* Neither the name of VMware, Inc. nor the names of its contributors may be used
to endorse or promote products derived from this software without specific prior 
written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL VMWARE, INC. OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
POSSIBILITY OF SUCH DAMAGE.
================================================================================*/

package com.vmware.vim25.mo.samples;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.concurrent.*;
import com.vmware.vim25.*;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.Network;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class HelloVM
{static ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
static ScheduledExecutorService exec1 = Executors.newSingleThreadScheduledExecutor();
	public static void main(String[] args) throws Exception
	{
		
		long start = System.currentTimeMillis();
		URL url = new URL("https://130.65.132.109/sdk");
		ServiceInstance si = new ServiceInstance(url, "administrator", "12!@qwQW", true);
		long end = System.currentTimeMillis();

		System.out.println("time taken:" + (end-start)+"ms");
		Folder rootFolder = si.getRootFolder();
		String name = rootFolder.getName();
		System.out.println("root:" + name);
		ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
		//ManagedEntity mes = new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine","Team09_Ubuntu_Kruthika");
		if(mes==null || mes.length ==0)
		{
			return;
		}
		VirtualMachine vm = (VirtualMachine) mes[0];
		VirtualMachineSummary vms = vm.getSummary();
		VirtualMachineConfigSummary vmcs=  vms.getConfig();
		VirtualMachineConfigInfo vminfo = vm.getConfig();
		VirtualMachineCapability vmc = vm.getCapability();
		Network[] nw =vm.getNetworks();
		VirtualMachineQuickStats vmqs= vms.quickStats;
	    vm.getResourcePool();
		System.out.println("********Printing Stats for VM "+vm.getName()+"*********");
		//VirtualMachine vm = (VirtualMachine) mes;
		System.out.println("VM Name: " + vm.getName());
		System.out.println("GuestOS: " + vminfo.getGuestFullName());
		System.out.println("VM IP address: "+vm.getGuest().getIpAddress());
		//System.out.println("Host address: "+vm.getParent());
		VirtualMachineConnectionState connState=vm.getRuntime().connectionState;
		System.out.println("VM Power State: "+vm.getRuntime().getPowerState());
		System.out.println("*******CPU********");	
		//CPU allocation- If set to -1, no fixed upper limit on resource usage has been set.
		System.out.println("CPU Allocation: " +  vminfo.getCpuAllocation().getLimit());
		System.out.println("CPU Reservation: " +  vminfo.getCpuAllocation().getReservation());
		System.out.println("Number OF CPU : "+ vmcs.numCpu);
		//Hot plug- The CPU hot plug option lets you add CPU resources for a virtual machine while the machine is turned on.
		System.out.println("Multiple snapshot supported: " + vmc.isMultipleSnapshotsSupported());
		System.out.println("CPU Demand : "+ vmqs.getOverallCpuDemand());
		System.out.println("CPU Usage : "+ vmqs.getOverallCpuUsage());
		System.out.println("*******NETWORK********");
		System.out.println("Network Name :"+ nw[0].getName());
		System.out.println(nw[0].toString());
		si.getServerConnection().logout();
		
		

	exec.scheduleAtFixedRate(new Runnable() {
		public void run()
		{
		
			VMSnapshot vmSnap= new VMSnapshot();
			try {
				vmSnap.refreshSnapshot("130.65.132.109");
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		},0,10,TimeUnit.MINUTES);
	
	exec1.scheduleAtFixedRate(new Runnable() {
		public void run()
		{
			PingVM pingVM=new PingVM();
			try {
				//System.out.println("check");
				pingVM.checkHostStatus();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		},60,300,TimeUnit.SECONDS);
	
}
}
