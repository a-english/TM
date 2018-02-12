import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class TM {
	
	//check for keywords
	String SUMMARY="summary";
	String START="start";
	String STOP="stop";
	String DESCRIPTION="describe";
	
	public void instructions() {
		System.out.print("\n  To execute the application from the command line you should use the following general format.\n\n"+
		"\tjava TM <command> <data>\n\n"+
		"  Where command can be one of, start, stop, describe, or summary.\n\n"+
		"\tjava TM start <task name>\tLogs the start time of a task with name <task name>\n"+
		"\tjava TM stop <task name>\tLogs the stop time of a task with name <task name>\n"+
		"\tjava TM describe <task name> <description> (optional)<task size>\tLogs the description of the task with name <task name>. Requires quotes around description. Optionally allows user to add a task size as well.\n"+
		"\tjava TM summary <task name>\tProvides a report of the activity and total time spent working on task with name <task name>\n"+
		"\tjava TM summary\t\tProvides a report of the activity and total time spent working on ALL tasks\n"+
		"\tjava TM size <task name> <task size> Adds a size to the task. \n\n");
	}
	public static void main(String[] args) {
		TM tm=new TM();
		tm.appMain(args);
	}

	public void appMain(String[] args) //non-static main wrapper
	{
		int numberOfArguments=args.length;
		String cmd, data, description;
		
		//since there are many options for number of inputs, needs to take into account number of arguments
		switch (numberOfArguments) {
		case 1:
			//Only one case exists that accepts one argument: summary all
			//if that is not the case selected, print instructions for user.
			cmd=args[0];
			if (cmd.equals(SUMMARY))
			{
				//print all the lines
				read("");//blank string used to signify no name given; print all
			}
			else
				instructions();
			break;
		case 2:
			// either print summary with query, or input a start/stop
			cmd=args[0];
			data=args[1];

			if (cmd.equals(SUMMARY))
			{
				//summary with two args means print out all logs that correspond to certain 
				read(data);
			}
			else if ( cmd.equals(STOP)  ||  cmd.equals(START))
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
			//Only one case exists that accepts three argument: adding description
			//if that is not the case selected, print instructions for user.
			cmd=args[0];
			data=args[1];
			description=args[2];
			if (cmd.equals(DESCRIPTION))
			{
				//TODO: check if description already exists
				//because I will probably forget if I put in a description or not
				
				//take name and description and print
				Log log=new Log(cmd, data, description);
				log.write();
			}
			else
				instructions();
			break;
		
		default: //user input an invalid number of arguments
			instructions();
		}
		
	}
	
	void read(String name){
		try {
			File file = new File("TM.txt");
			FileReader fileReader = new FileReader(file);
			//reading file line by line, courtesy of http://www.avajava.com/tutorials/lessons/how-do-i-read-a-string-from-a-file-line-by-line.html
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
		if(name==""){
			//no name given, read all
			//Simple read out - no queue necessary
				while ((line = bufferedReader.readLine()) != null) {
					stringBuffer.append(line);
					stringBuffer.append("\n");
				}
				fileReader.close();
				System.out.println("Complete summary:");
				System.out.println(stringBuffer.toString());
		}else{
			//find all logs with name
			LinkedList<Log> queue = new LinkedList<Log>();
			String first;
			while ((line = bufferedReader.readLine()) != null) {
				first=line.split(" ")[0];	//grab the first word of the line
				if(first.equals(name)) {	//if it is the query, grab this line.
					queue.add(new Log(line));
				}
			}
			fileReader.close();
			//first look for a description
			int index=findDescription(queue);
			if (index!=-1)	//description exists
			{
				//move description to front of queue
				//This makes it read more nicely in the summary
				//It also simplifies calculation later.
				Log description=queue.get(index);
				queue.remove(index);
				queue.addFirst(description);
			}
			if (queue.isEmpty())
			{
				System.out.print("No reccords were found named " +name+ "\n");
			}
			else {
			//print out your queue
			for (int i=0; i<queue.size(); i++)
				queue.get(i).print();
			System.out.print("Total minutes spent:"+calculate(queue));
			}
		}

		}
		catch(IOException e)
			{
				System.out.print("Error reading from file.\n");
			}
		}
	
	int calculate(LinkedList<Log> queue)
	{
		//description should be the first entry, if present.
		int sum=0;
		if(queue.getFirst().type.equals(DESCRIPTION))
			queue.pop();
		//if program has been used correctly the queue will now have only time logs alternating start and stop
		//but that's a big if
		Date start, stop;
		long milliseconds=0;
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy"); //same format as default input, which is fine
		while(!queue.isEmpty()){
			try {
				start=dateFormat.parse(queue.get(0).input);
				stop=dateFormat.parse(queue.get(1).input);
				//http://www.baeldung.com/java-date-difference
				milliseconds+= Math.abs(stop.getTime() - start.getTime());
				queue.pop();
				queue.pop();
			}
			catch(Exception e){
				System.out.print("Error reading log time file.\n");
				return -1;
			}
			sum=(int) TimeUnit.MINUTES.convert(milliseconds, TimeUnit.MILLISECONDS);
		}
		return sum;
	}
	
	int findDescription (LinkedList<Log> queue) {
		//function finds description in queue, if there is one
		//returns -1 if there is no description present in the file
		for (int i=0; i<queue.size(); i++)
		{
			if (queue.get(i).type.equals(DESCRIPTION))
				return i;
		}
		return -1;
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
	File file=new File("TM.txt");
			
	public void write()
	{
		FileWriter writer;
		try {
			writer=new FileWriter(file, true);
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
		System.out.print(name+" "+type+" "+input+"\n");
	}
		
}
