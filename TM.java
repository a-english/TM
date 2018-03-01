import java.io.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;


public class TM {

	
	public void usage() {
		System.out.print("\n  To execute the application from the command line you should use the following general format.\n\n"+
		"\tjava TM <command> <data>\n\n"+
		"  Where command can be one of, start, stop, describe, or summary.\n\n"+
		"\tjava TM start <task name>\tLogs the start time of a task with name <task name>\n"+
		"\tjava TM stop <task name>\tLogs the stop time of a task with name <task name>\n"+
		"\tjava TM describe <task name> <description> (optional)<task size>\tLogs the description of the task with name <task name>. Requires quotes around description. Optionally allows user to add a task size as well.\n"+
		"\tjava TM summary <task name>\tProvides a report of the activity and total time spent working on task with name <task name>\n"+
		"\tjava TM summary\t\tProvides a report of the activity and total time spent working on ALL tasks\n"+
		"\tjava TM size <task name> <size>\tAssign a t-shit size to a task.\n"+
		"\tjava TM rename <task name> <new name> \tRenames task in log \n"+
		"\tjava TM delete <task name> \t\tRemove current task from log. \n\n");
	}
	public static void main(String[] args) {
		TM tm=new TM();
		tm.appMain(args);
	}
	

	public void appMain(String[] args) //non-static main wrapper
	{
		TMModel Model=new TMModel();
		try {
			switch(args[0]){
			case "start":
				Model.startTask(args[1]);
				break;
			case "stop":
				Model.stopTask(args[1]);
				break;
			case "describe":
				Model.describeTask(args[1], args[2]);
				if(args.length==4)
					Model.sizeTask(args[1], args[3]);
				break;
			case "summary":
				if(args.length==1)
					summaryAll();
				else
					summary(args[1]);
				break;
			case "size":
				Model.sizeTask(args[1],args[2]);
				break;
			case "rename":
				Model.renameTask(args[1],args[2]);
				break;
			case "delete":
				Model.deleteTask(args[1]);
				break;
			default:
				usage();
			}
		}catch(Exception e){
			usage();
		}
		
	}
	
	void summaryAll() {
		Set<String> names = TMMiodel.taskNames();
		for (String name : names)
		{
			summary(name);
		}
		for (Size s : Size.values())
		{
			System.out.print("STATS FOR "+s+" TASKS\n--------------------\n"+
				"\nAverage time per task\t"+TMModel.avgTimeForSize(s)+
				"\nFastest time\t\t"+TMModel.minTimeForSize(s)+
				"\nSlowest time\t\t"+TMModel.maxTimeForSize(s)+"\n\n");
		}
	}
	void summary(String name){
			LogList task=new LogList(name);
			
			System.out.println("Name: \t\t"+task.name);

			//if(size!=null && !size.isEmpty()){
				System.out.print("Size: \t\t" + task.size() +"\n");
			//}if(description!=null && !description.isEmpty()){
				System.out.print("Description: \t"+task.description()+"\n");
			//}
			System.out.print("\nTotal time spent: "+task.getTime()+"\n\n");
			
		}
	
	
}


