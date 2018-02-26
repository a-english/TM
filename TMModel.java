import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.Set;
import java.util.StringTokenizer;

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

public class TMModel implements ITMModel{

	LinkedList<LogList> tasks=new LinkedList<>();
	
	public void writeAll()
	{
		FileWriter writer;
		try {
			writer=new FileWriter(util.filename, false);	//not flagged for append
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
		Set<String> names= new Set<String>();
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
	
    boolean startTask(String name) {
    	try {
			String date=LocalDateTime.now().toString();
			//take name and print info
			Log log= new Log(util.start, name, date);
			log.write();
    	}
    	catch(Exception e)
    	{
    		return false;
    	}
    	return true;
    }
    boolean stopTask(String name) {
    	try {
			String date=LocalDateTime.now().toString();
			//take name and print info
			Log log= new Log(util.stop, name, date);
			log.write();
    	}
    	catch(Exception e)
    	{
    		return false;
    	}
    	return true;
    }
    boolean describeTask(String name, String description);
    boolean sizeTask(String name, String size)
    {
    	
    }
    
    
    boolean deleteTask(String name) {
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
			return false;
		}
		tasks.remove(index);
		writeAll();
		return true;
    }
    
    boolean renameTask(String oldName, String newName) {

		readAll();
		int index=-1;
		for(int i=0; i<tasks.size()	; i++)
		{
			if(tasks.get(i).name.equals(newname))
			{
				return false;
			}
			if(tasks.get(i).name.equals(oldname))
			{
				if(index==-1) {
					index=i;
				}
			}
		}
		//index now holds index of task to be renamed, if it exists and there is 
		//no task with the new name yet
		
		tasks.get(index).rename(newname);
		writeAll();
		return true;
    
    }

    // return information about our tasks
    //
    String taskElapsedTime(String name);
    String taskSize(String name);
    String taskDescription(String name);

    // return information about some tasks
    //
    String minTimeForSize(String size);
    String maxTimeForSize(String size);
    String avgTimeForSize(String size);

    // return information about all tasks
    //
    String elapsedTimeForAllTasks();
    Set<String> taskNames();

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
			StringTokenizer st;
			queue = new LinkedList<Log>();
			while ((line = bufferedReader.readLine()) != null) {
				st=new StringTokenizer(line);
				if(st.nextToken().equals(name)) {	//if has the name we want, grab this line.
					queue.add(new Log(line));
				}
			}
			fileReader.close();
		}
		catch (Exception e) {
			System.out.print("Error opening file to read.");
		}
		//queue now hold all relevent entries
		String type;
		//look for special entries
		//if there is more than one entry for description, it appends.
		for (int i=0; i<queue.size(); i++){
			type=queue.get(i).type;
			if(!(type.equals(util.start)||type.equals(util.stop))) {
				if (queue.get(i).type.equals(util.size)){
					size=queue.get(i).input;
				}
				if (queue.get(i).type.equals(util.description)){
					description+=queue.get(i).input+"\n";
				}
				queue.remove(i);
				i--;
				}
			}
		//any non-time entries should be extracted from queue now
	}
	
	void rename(String newname)
	{
		name=newname;
		for (int i=0; i<queue.size(); i++)
		{
			queue.get(i).name=newname;
		}
	}
	
	
	
	int calculate()
	{
		//if program has been used correctly the queue will now have only time logs alternating start and stop
		//but that's a big if
		LocalDateTime start, stop;
		int minutes=0;
		for(int i=0;i<queue.size();i+=2){
			try {
				start=LocalDateTime.parse(queue.get(i).input);
				stop=LocalDateTime.parse(queue.get(i+1).input);
				minutes+=ChronoUnit.MINUTES.between(start,stop);
			}
			catch(Exception e){
				System.out.print(e.getMessage());
				return -1;
			}
		}
		return minutes;
	}
	
	void write() {
		Log temp;
		if(size!=null && !size.isEmpty()){
			temp=new Log(util.size,name,size);
			temp.write();
		}
		if(description!=null && !description.isEmpty()){
			temp=new Log(util.description,name,description);
			temp.write();
		}
		
		for(int i=0; i<queue.size(); i++)
		{
			queue.get(i).write();
		}
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
		StringTokenizer st=new StringTokenizer(line);
		name=st.nextToken();
		type=st.nextToken();
		input=st.nextToken("\n");
		input=input.substring(1); //delete tab deliminator
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
					writer.write(name+"\t"+type+"\t"+st.nextToken()+"\n");
					//has to write all lines
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
