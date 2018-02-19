import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

class util{
	static String summary="summary", 
				  start="start", 
				  stop="stop", 
				  description="describe", 
				  size="size",
				  filename="TM.txt";
	
}
public class TM {
	
	//check for keywords
	
	public void instructions() {
		System.out.print("\n  To execute the application from the command line you should use the following general format.\n\n"+
		"\tjava TM <command> <data>\n\n"+
		"  Where command can be one of, start, stop, describe, or summary.\n\n"+
		"\tjava TM start <task name>\tLogs the start time of a task with name <task name>\n"+
		"\tjava TM stop <task name>\tLogs the stop time of a task with name <task name>\n"+
		"\tjava TM describe <task name> <description> (optional)<task size>\tLogs the description of the task with name <task name>. Requires quotes around description. Optionally allows user to add a task size as well.\n"+
		"\tjava TM summary <task name>\tProvides a report of the activity and total time spent working on task with name <task name>\n"+
		"\tjava TM summary\t\tProvides a report of the activity and total time spent working on ALL tasks\n"+
		"\tjava TM stats <task size>\t Displays statistical info for all entries of given size. \n\n"+
		"\tjava TM stats\t\tProvides statistical data for all entries of all sizes. \n\n"+
		"\tjava TM rename <task name> <new name> \tRenames task in log \n\n"+
		"\tjava TM delete <task name> \t\tRemove current task from log. \n\n");
	}
	public static void main(String[] args) {
		TM tm=new TM();
		tm.appMain(args);
	}

	public void appMain(String[] args) //non-static main wrapper
	{
		int numberOfArguments=args.length;
		String cmd, data, description, size;
		
		//since there are many options for number of inputs, needs to take into account number of arguments
		switch (numberOfArguments) {
		case 1:
			//Only one case exists that accepts one argument: summary all
			//if that is not the case selected, print instructions for user.
			cmd=args[0];
			if (cmd.equals(util.summary))
			{
				//print all the lines
				LinkedList<String> names= new LinkedList<String>();

				try {
					File file = new File(util.filename);
					FileReader fileReader = new FileReader(file);
					//reading file line by line, courtesy of http://www.avajava.com/tutorials/lessons/how-do-i-read-a-string-from-a-file-line-by-line.html
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					String line;
					
					Log tempLog;
					
					while((line = bufferedReader.readLine()) != null)
					{
						tempLog=new Log(line);
						if (!names.contains(tempLog.name))
							names.add(tempLog.name);
					}
					
					LogList tempList;
					String currentEntry;
					for(int i=0; i<names.size(); i++)
					{
						//creates and displays log lists for each project name found
						currentEntry=names.get(i);
						tempList=new LogList(currentEntry);
						tempList.print();
					}

					fileReader.close();
				}
				catch (Exception e) {
					System.out.print("Error reading from file.\n");
				}
				
			}
			else
				instructions();
			break;
		case 2:
			// either print summary with query, or input a start/stop
			cmd=args[0];
			data=args[1];

			if (cmd.equals(util.summary))
			{
				//summary with two args means print out all logs that correspond to certain 
				LogList log=new LogList(data);
				log.print();
			}
			else if ( cmd.equals(util.stop)  ||  cmd.equals(util.start))
			{
				//input is the same except for the keyword
				Date date=new Date();
				//take name and print info
				Log log= new Log(cmd, data, date.toString());
				log.write();
			}
			else
			{
				//invalid arguments
				instructions();
			}
			break;
		case 3:
			//Either adding description or size
			//if the case selected is neither of those, print instructions for user.
			cmd=args[0];
			data=args[1];
			if (cmd.equals(util.description))
			{
				description=args[2];
				
				//take name and description and print
				Log log=new Log(cmd, data, description);
				log.write();
			}	
			else if(cmd.equals(util.size))
			{
				size=args[2];
				Log log=new Log(cmd, data, size);
				log.write();
			}
			else
				instructions();
			break;
		case 4:	//summary+description
		{
			cmd=args[0];
			data=args[1];
			description=args[2];
			size=args[3];

			Log log=new Log(cmd, data, description);
			log.write();
			log=new Log(cmd, data, size);
			log.write();
			break;
		}
		
		default: //user input an invalid number of arguments
			instructions();
		}
		
	}
	
}

class LogList
{
	String name;
	String size;
	String description="";
	LinkedList<Log> queue;
	
	public LogList(String name){
		this.name=name;
		try {
			File file = new File(util.filename);
			FileReader fileReader = new FileReader(file);
			//reading file line by line, courtesy of http://www.avajava.com/tutorials/lessons/how-do-i-read-a-string-from-a-file-line-by-line.html
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
				
			int i=0;
			queue = new LinkedList<Log>();
			String first;
			while ((line = bufferedReader.readLine()) != null) {
				//TODO: use a string tokenizer and stop being a scrub
				first=line.split(" ")[0];	//grab the first word of the line
				if(first.equals(name)) {	//if it is the query, grab this line.
					queue.add(new Log(line));
				}
			}
			fileReader.close();
		}
		catch (Exception e) {
			System.out.print("Error opening file.");
		}
		//look for special entries
		//if there is more than one entry for either size or description, this takes the most recent
		//which is accidental, but convenient
		for (int i=0; i<queue.size(); i++)
		{
			if (queue.get(i).type.equals(util.description)){
				description+=queue.get(i).input+"\n";
				queue.remove(i);
			}
			else if (queue.get(i).type.equals(util.size)){
				size=queue.get(i).input;
				queue.remove(i);
			}
		}
		//any non-time entires should be extracted from queue now
	}
	
	void print()
	{
		System.out.println("Name: \t"+name+"\nDescription: \t"+
						   description +"\nSize: \t" + size +"\n");
		for (int i=0; i<queue.size(); i++)
			queue.get(i).print();
		int hours, minutes=calculate();
		if (minutes>0) {
			hours=minutes/60;
			minutes=minutes%60;
			System.out.print("\nTotal time spent: ");
			if (hours==1)
				System.out.print("1 hour and ");
			else if (hours>1)
				System.out.print(hours +" hours and ");
			System.out.print(minutes + " minutes.\n\n");
		}
		//if log is not set up correctly, does not try to display uselss time value
	}
	
	
	int calculate()
	{
		//if program has been used correctly the queue will now have only time logs alternating start and stop
		//but that's a big if
		Date start, stop;
		long milliseconds=0;
		int i=0;
		int sum=0;
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy"); //same format as default input, which is fine
		for(i=0;i<queue.size();i+=2){
			try {
				start=dateFormat.parse(queue.get(i).input);
				stop=dateFormat.parse(queue.get(i+1).input);
				//http://www.baeldung.com/java-date-difference
				milliseconds+= (stop.getTime() - start.getTime());
			}
			catch(Exception e){
				System.out.print("Error reading log time file.\n");
				return -1;
			}
			sum=(int) TimeUnit.MINUTES.convert(milliseconds, TimeUnit.MILLISECONDS);
		}
		return sum;
	}
	

			
}

class Log{
	//log converts data values into strings for output file
	//and vise versa
	
	String type, name, input;
	
	
	public Log(String type, String name, String input){
		this.type=type;
		this.name=name;
		this.input=input;
	}

	public Log(String line)
	{
		String type, name, input;
		name=line.split(" ")[0];
		type=line.split(" ")[1];
		input=line.substring(name.length()+type.length()+2); //the rest
		
		this.type=type;
		this.name=name;
		this.input=input;
	}
			
	public void write()
	{
		FileWriter writer;
		try {
			writer=new FileWriter(util.filename, true);
			writer.write(name+" "+type+" "+input+"\n"); //does not display nicely in windows
			
			writer.flush();
			writer.close();
		}
		catch( IOException e) {
			System.out.print("Error opening file for writing");
			return;
		}
	}
	
	public void print()
	{
		System.out.print(type+" "+input+"\n");
	}
		
}
