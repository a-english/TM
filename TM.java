import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

class util{
	//check for keywords
	static String summary="summary", 
				  start="start", 
				  stop="stop", 
				  description="describe", 
				  size="size",
				  rename="rename",
				  delete="delete",
				  stats="stats",
				  filename="TM.txt";
	public static String TimeFormat(int minutes)
	{
		String time="";
		int hours;
		if (minutes>0) {
			hours=minutes/60;
			minutes=minutes%60;
			if (hours==1)
				time="1 hour and ";
			else if (hours>1)
				time = hours +" hours and ";
			time+= minutes+ " minutes.";
		}
		else
			time="Unknown";
		return time;
	}
	
}

public class TM {
	
	LinkedList<LogList> tasks;
	
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
		"\tjava TM stats <task size>\t Displays statistical info for all entries of given size. \n"+
		"\tjava TM stats\t\tProvides statistical data for all entries of all sizes. \n"+
		"\tjava TM rename <task name> <new name> \tRenames task in log \n"+
		"\tjava TM delete <task name> \t\tRemove current task from log. \n\n");
	}
	public static void main(String[] args) {
		TM tm=new TM();
		tm.appMain(args);
	}
	
	public void delete(String name)
	{
		readAll();
		int index=-1;
		for(int i=0; i<tasks.size(); i++)
		{
			if (tasks.get(i).name.equals(name))
				index=i;
		}
		if (index==-1)
		{
			System.out.print("No task with name " + name + " found in log.\n");
		}
		else
		{
			tasks.remove(index);
		}
		writeAll();
	}
	
	public void rename(String oldname, String newname)
	{
		readAll();
		for(int i=0; i<tasks.size(); i++)
		{
			if(tasks.get(i).name.equals(oldname))
			{
				tasks.get(i).name=newname;
				writeAll();
			}
		}
	}
	
	public void writeAll()
	{
		FileWriter writer;
		try {
			writer=new FileWriter(util.filename, false);	//not flagged for appent
			writer.write(""); //empty string - clears file
			writer.close();
		}
		catch( IOException e) {
			System.out.print("Error opening file for erasing");
			return;
		}
		for(int i=0; i<tasks.size(); i++)
			tasks.get(i).write();
	}
	
	public void readAll()
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

			fileReader.close();
		}
		catch (Exception e) {
			System.out.print("Error reading from file.\n");
		}
			
			LogList tempList;
			String currentEntry;
			tasks=new LinkedList<LogList>();
			for(int i=0; i<names.size(); i++)
			{
				//creates log lists for each project name found
				currentEntry=names.get(i);
				tasks.push(new LogList(currentEntry));
			}

		
	}

	public void appMain(String[] args) //non-static main wrapper
	{
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

			if (cmd.equals(util.summary))
			{
				name=args[1];
				//summary with two args means print out all logs that correspond to certain 
				LogList log=new LogList(name);
				log.print();
			}
			else if ( cmd.equals(util.stop)  ||  cmd.equals(util.start))
			{
				data=args[1];
				//input is the same except for the keyword
				Date date=new Date();
				//take name and print info
				Log log= new Log(cmd, data, date.toString());
				log.write();
			}
			else if (cmd.equals(util.stats))
			{
				name=args[1];
				readAll();
				stats(name);
			}
			else if (cmd.equals(util.delete))
			{
				name=args[1];
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
	
	
	void stats(String size)
	{
		//System.out.print("Currently finding stats for "+size+".\n");
		int time;
		int min=0, max=0, total=0, average=0, number=0;
		LogList currentTask;
		try {
		for(int i=0; i<tasks.size(); i++) {
			currentTask=tasks.get(i);
			if (currentTask.size.equals(size))
			{
				number++;
				time=currentTask.calculate();
				if(number==1)
				{
					min=time;max=time;
				}
				else {
				if(time>max)
					max=time;
				if(time<min&&time>0)
					min=time;
				}
				total+=time;
			}
		}/*
		if(number==0)
		{
			System.out.print("Unable to process stats for "+size+".\n");
		}*/
		if (number>1)
		{
			average=total/number;
			System.out.print("STATS FOR "+size+" TASKS\n--------------------\n"+
					"Total time:\t\t"+util.TimeFormat(total)+
					"\nAverage time per task\t"+util.TimeFormat(average)+
					"\nFastest time\t\t"+util.TimeFormat(min)+
					"\nSlowest time\t\t"+util.TimeFormat(max)+"\n\n");
			
		}
		}
		catch(Exception e) {	
			System.out.print("Unable to process stats for "+size+".\n");
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
		String type;
		//look for special entries
		//if there is more than one entry for description, it appends.
		for (int i=0; i<queue.size(); i++)
		{
			type=queue.get(i).type;
			if(!(type.equals(util.start)||type.equals(util.stop)))
				{if (queue.get(i).type.equals(util.size)){
					//System.out.print("Found size for "+name+": "+queue.get(i).input +".\n");
					size=queue.get(i).input;
				}
				if (queue.get(i).type.equals(util.description)){
					description+=queue.get(i).input+"\n";
				}
				queue.remove(i);
			}
		}
		//any non-time entries should be extracted from queue now
	}
	
	void print()
	{
		System.out.println("Name: \t\t"+name+"\n");
		
		if(description!=null && !description.isEmpty())
			{
			System.out.print("Description: \t"+description+"\n");
			}
		if(size!=null && !size.isEmpty())
			{
			System.out.print("Size: \t\t" + size +"\n");
			}
		for (int i=0; i<queue.size(); i++)
			queue.get(i).print();
		int hours, minutes=calculate();
		System.out.print("\nTotal time spent: "+util.TimeFormat(minutes)+"\n\n");
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
			//	System.out.print("Error reading log time file.\n");
				return -1;
			}
			sum=(int) TimeUnit.MINUTES.convert(milliseconds, TimeUnit.MILLISECONDS);
		}
		return sum;
	}
	
	void write() {
		Log temp;
		try
		{
			temp=new Log(util.size,name,size);
			temp.write();
		}
		catch(Exception e) {/*no size given*/}
		try {
			temp=new Log(util.description,name,description);
			temp.write();
		}
		catch(Exception e) {/*no description given*/}
		
		for(int i=0; i<queue.size(); i++)
			queue.pop().write();
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
			if(type==util.description)
			{
				StringTokenizer st= new StringTokenizer(input, "\n");
				for(int i=0; i<st.countTokens(); i++)
				{
					writer.write(name+" "+type+" "+st.nextToken()+"\n");
				}
			}
			else
				writer.write(name+"\t"+type+"\t"+input+"\n"); //does not display nicely in windows
			
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
		System.out.print(type+"\t"+input+"\n");
	}
		
}
