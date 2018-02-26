import java.io.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;


public class TM {
	
	
	public void instructions() {
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
		int numberOfArguments=args.length;
		String cmd, data, description, size, name, newname;
		
		//since there are many options for number of inputs, needs to take into account number of arguments
		switch (numberOfArguments) {
		case 1:
			cmd=args[0];
			if (cmd.equals(util.summary))
			{
				readAll();
				for (int i=0; i<tasks.size(); i++)
					tasks.get(i).print();
				
				
			}
			else if (cmd.equals(util.stats))
			{
				readAll();
				LinkedList<String> sizes = new LinkedList<String>();
				for(int i=0; i<tasks.size(); i++)
				{
					if (!sizes.contains(tasks.get(i).size))
							sizes.push(tasks.get(i).size);
				}
				//sizes not contains a list of all the different sizes in the log file
				for (int i=0; i<sizes.size(); i++)
				{
					stats(sizes.get(i));
				}
			}
			else
				instructions();
			break;
		case 2:
			cmd=args[0];
			name=args[1];
			if (cmd.equals(util.summary))
			{
				//summary with two args means print out all logs that correspond to certain 
				LogList log=new LogList(name);
				log.print();
			}
			else if (cmd.equals(util.start))
			{
				TMModel.startTask(name);
			}
			else if(cmd.equals(util.stop))
			{
				TMModel.stopTask(name);
			}
			else if (cmd.equals(util.delete))
			{
				delete(name);
			}
			else
			{
				//invalid arguments
				instructions();
			}
			break;
		case 3:
			cmd=args[0];
			name=args[1];
			if (cmd.equals(util.description))
			{
				description=args[2];
				
				//take name and description and print
				Log log=new Log(cmd, name, description);
				log.write();
			}	
			else if(cmd.equals(util.size))
			{
				size=args[2];
				Log log=new Log(cmd, name, size);
				log.write();
			}
			else if(cmd.equals(util.rename))
			{
				newname=args[2];
				rename(name, newname);
			}
			else
				instructions();
			break;
		case 4:	//summary+description
		{
			cmd=args[0];
			name=args[1];
			description=args[2];
			size=args[3];

			Log log=new Log(util.description, name, description);
			log.write();
			log=new Log(util.size, name, size);
			log.write();
			break;
		}
		
		default: //user input an invalid number of arguments
			instructions();
		}
		
	}
	
	
	void summary(LogList task){
			
			System.out.println("Name: \t\t"+task.name);

			//if(size!=null && !size.isEmpty()){
				System.out.print("Size: \t\t" + task.size() +"\n");
			//}if(description!=null && !description.isEmpty()){
				System.out.print("Description: \t"+task.description()+"\n");
			//}
			System.out.print("\nTotal time spent: "+task.getTime()+"\n\n");
			
		}
	}
	
}


