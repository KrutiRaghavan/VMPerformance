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

package com.vmware.vim25.mo.samples.alarm;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import com.vmware.vim25.Action;
import com.vmware.vim25.AlarmAction;
import com.vmware.vim25.AlarmSetting;
import com.vmware.vim25.AlarmSpec;
import com.vmware.vim25.AlarmTriggeringAction;
import com.vmware.vim25.GroupAlarmAction;
import com.vmware.vim25.MethodAction;
import com.vmware.vim25.MethodActionArgument;
import com.vmware.vim25.SendEmailAction;
import com.vmware.vim25.StateAlarmExpression;
import com.vmware.vim25.StateAlarmOperator;
import com.vmware.vim25.mo.AlarmManager;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * http://vijava.sf.net
 * @author Steve Jin
 */

public class CreateVmAlarm 
{
  public static void main(String[] args) throws RemoteException, MalformedURLException 
  {
    ServiceInstance si = new ServiceInstance(new URL("https://130.65.132.109/sdk"), "administrator", "12!@qwQW", true);
	InventoryNavigator inv = new InventoryNavigator(si.getRootFolder());
    ManagedEntity[] mes = (ManagedEntity[])inv.searchManagedEntities("VirtualMachine");
    for(int i=0;i<mes.length;i++)
    {
    	VirtualMachine vm=(VirtualMachine)mes[i];
    	try {
    
		    AlarmManager alarmMgr1 = si.getAlarmManager();
		    AlarmSpec spec = new AlarmSpec();
		    StateAlarmExpression expression = createStateAlarmExpression();
		    spec.setExpression(expression);
		    spec.setName(vm.getName()+"_4");
		    spec.setDescription("The Alarm is created for "+vm.getName()+"_4");
		    spec.setEnabled(true);    
		    AlarmSetting as = new AlarmSetting();
		    as.setReportingFrequency(0); 
		    as.setToleranceRange(0);
		    spec.setSetting(as);
            alarmMgr1.createAlarm(vm, spec);
            System.out.println("Alarm created with name "+vm.getName()+"_4");
            }
    	catch (RemoteException e) {
		System.out.println("Alarm with name "+vm.getName()+"_4"+" already set for "+vm.getName());
    	}
    
    }
    si.getServerConnection().logout();
	
  }

	  static StateAlarmExpression createStateAlarmExpression()
	  {
	    StateAlarmExpression expression = 
	      new StateAlarmExpression();
	    expression.setType("VirtualMachine");
	    expression.setStatePath("runtime.powerState");
	    expression.setOperator(StateAlarmOperator.isEqual);
	    expression.setRed("poweredOff");
	    return expression;
	  }

	  static MethodAction createPowerOnAction() 
	  {
	    MethodAction action = new MethodAction();
	    action.setName("PowerOnVM_Task");
	    MethodActionArgument argument = new MethodActionArgument();
	    argument.setValue(null);
	    action.setArgument(new MethodActionArgument[] { argument });
	    return action;
	  }
	  
	  static SendEmailAction createEmailAction() 
	  {
	    SendEmailAction action = new SendEmailAction();
	    action.setToList("arckruti88@gmail.com");
	    action.setCcList("arckruti88@gmail.com");
	    action.setSubject("Alarm - {testAlarm} on {testTarget}\n");
	    action.setBody("Description:{switched off}\n"
	        + "TriggeringSummary:{triggeringSummary}\n"
	        + "newStatus:{newStatus}\n"
	        + "oldStatus:{oldStatus}\n"
	        + "target:{target}");
	    return action;
	  }

	  static AlarmTriggeringAction createAlarmTriggerAction(
	      Action action) 
	  {
	    AlarmTriggeringAction alarmAction = 
	      new AlarmTriggeringAction();
	    alarmAction.setYellow2red(true);
	    alarmAction.setAction(action);
	    return alarmAction;
	  }
}