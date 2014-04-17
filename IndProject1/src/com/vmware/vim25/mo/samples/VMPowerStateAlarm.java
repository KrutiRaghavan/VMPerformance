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
import com.vmware.vim25.AlarmAction;
import com.vmware.vim25.AlarmSetting;
import com.vmware.vim25.AlarmSpec;
import com.vmware.vim25.GroupAlarmAction;
import com.vmware.vim25.SendEmailAction;
import com.vmware.vim25.StateAlarmExpression;

import java.net.URL;
import java.rmi.RemoteException;

import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;
import com.vmware.vim25.mo.util.*;

/**
*<pre>
*This is a sample which creates an Alarm to monitor the virtual
*machine's power state
*
*<b>Parameters:</b>
*vmname     [required] Name of the virtual machine
*alarm      [required] Name of the alarms
*
*</pre>
* @author sjin
*/

public class VMPowerStateAlarm 
{

	public static void main(String[] args) throws Exception 
	{

	   
		ServiceInstance si = new ServiceInstance(new URL(args[0]), args[1], args[2], true);
		AlarmManager am = si.getAlarmManager();
		Folder rootFolder = si.getRootFolder();
		VirtualMachine vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", args[3]);
		
		if(vm!=null && am!=null)
		{
			
			
			AlarmSpec spec = new AlarmSpec();
			StateAlarmExpression expression = createStateAlarmExpression();
		    AlarmAction emailAction = createAlarmTriggerAction(createEmailAction());
		    AlarmAction methodAction = createAlarmTriggerAction(createPowerOnAction());
				    GroupAlarmAction gaa = new GroupAlarmAction();
				  // spec.setAction(emailAction); 
				    gaa.setAction(new AlarmAction[]{emailAction, methodAction});
				    spec.setAction(gaa);
				    spec.setExpression(expression);
				    spec.setName("ParulVmPowerStateAlarm2");
				    
				    spec.setDescription("Monitor VM state and send email " +
				    		"and power it on if VM powers off");
				    spec.setEnabled(true);    
				    
				    AlarmSetting as = new AlarmSetting();
				    as.setReportingFrequency(0); //as often as possible
				    as.setToleranceRange(0);
				    
				    spec.setSetting(as);
		
			try
			{
				am.createAlarm(vm, spec);
				System.out.println("Successfully created Alarm: " );
			}
			catch(InvalidName in) 
			{
				System.out.println("Alarm name is empty or too long");
			}
			catch(DuplicateName dn)
			{
				System.out.println("Alarm with the name already exists");
			}
			catch(RemoteException re)
			{
				re.printStackTrace();
			}
			
		}


		else 
		{
			System.out.println("Either VM is not found or Alarm Manager is not available on this server.");
		}
		si.getServerConnection().logout();
	}

	static StateAlarmExpression createStateAlarmExpression()
	{   
	  StateAlarmExpression sae = new StateAlarmExpression();
	  sae.setOperator(StateAlarmOperator.isEqual);
	  sae.setRed("poweredOff");
	  sae.setYellow(null);
	  sae.setStatePath("runtime.powerState");
	  sae.setType("VirtualMachine");
	  return sae;
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
   static AlarmTriggeringAction createAlarmTriggerAction(Action action) throws Exception 
   {
      AlarmTriggeringAction alarmAction = new AlarmTriggeringAction();
      alarmAction.setYellow2red(true);
      alarmAction.setAction(action);
      return alarmAction;
   }
   
   static AlarmSpec createAlarmSpec(String alarmName, AlarmAction action, AlarmExpression expression) throws Exception 
   {      
	   AlarmSpec spec = new AlarmSpec();
	   spec.setAction(action);
	   spec.setExpression(expression);
	   spec.setName(alarmName);
	   spec.setDescription("Monitor VM state and send email if VM power's off");
	   spec.setEnabled(true);      
	   return spec;
   }

	private static OptionSpec[] constructOptions() 
	{
		OptionSpec [] useroptions = new OptionSpec[2];
		useroptions[0] = new OptionSpec("vmname", "String", 1, "Name of the virtual machine", null);
		useroptions[1] = new OptionSpec("alarm","String",1, "Name of the alarm", null);
		return useroptions;
	}
   
}
