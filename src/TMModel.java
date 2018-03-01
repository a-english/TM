import java.io.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.StringTokenizer;


class util{
	
	static String filename="TM.txt";
	
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


public class TMModel implements ITMModel {

	LinkedList<LogList> tasks;
	
	@Override
	public boolean startTask(String name) {
		return newEntry("start", name, LocalTime.now().toString());
	}

	@Override
	public boolean stopTask(String name) {
		return newEntry("stop", name, LocalTime.now().toString());
	}

	@Override
	public boolean describeTask(String name, String description) {
		return newEntry("describe", name, description);
	}

	@Override
	public boolean sizeTask(String name, String size) {
		return newEntry("size", name, size);
	}
	
	public boolean newEntry(String type, String name, String data) {
		try {
			Log log=new Log(type, name, data);
			log.write();
		}
		catch (Exception e)
		{
			System.out.print(e.getMessage());
			return false;
		}
		return true;
		
	}

	@Override
	public boolean deleteTask(String name) {
		try {
			readAll();
			int index=-1;
			for(int i=0; i<tasks.size(); i++)
			{
				if (tasks.get(i).name.equals(name))
					index=i;
			}
			if (index==-1)
			{
				return false;
			}
			else
			{
				tasks.remove(index);
			}
			writeAll();
			return true;
		}
		catch(Exception e) {return false;}
	}

	@Override
	public boolean renameTask(String oldName, String newName) {
		try {
			readAll();
			int index=-1;
			for(int i=0; i<tasks.size()	; i++)
			{
				if(tasks.get(i).name.equals(newName))
				{
					return false;
				}
				if(tasks.get(i).name.equals(oldName))
				{
					if(index==-1) {
						index=i;
					}
				}
			}
			//index now holds index of task to be renamed, if it exists and there is 
			//no task with the new name yet
			
			tasks.get(index).rename(newName);
			writeAll();
			return true;
		}catch(Exception e) {return false;}
		
	}
	
	private void writeAll() throws Exception
	{
		FileWriter writer;
		writer=new FileWriter(util.filename, false);	//not flagged for append
		writer.write(""); //empty string - clears file
		writer.close();
		for(int i=0; i<tasks.size(); i++)
			tasks.get(i).write();
	}
	
	private void readAll() throws Exception
	{
		//print all the lines
		LinkedList<String> names= new LinkedList<String>();
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
			
			String currentEntry;
			tasks=new LinkedList<LogList>();
			for(int i=0; i<names.size(); i++)
			{
				//creates log lists for each project name found
				currentEntry=names.get(i);
				tasks.push(new LogList(currentEntry));
			}

		
	}

	@Override
	public String taskElapsedTime(String name) {
		return util.TimeFormat(calculateTime(name));
	}
	
	private int calculateTime(String name) {
		LogList log=new LogList(name);
		return log.timeSpent();
	}

	@Override
	public String taskSize(String name) {
		LogList log=new LogList(name);
		return log.size;
	}

	@Override
	public String taskDescription(String name) {
		LogList log=new LogList(name);
		return log.description;
	}

	@Override
	public String minTimeForSize(String size) {
		Set<String> names = taskNamesForSize(size);
		LogList temp;
		int min=-1;
		for(String name : names)
		{
			temp=new LogList(name);
			if((min==-1)||(min>temp.timeSpent())) {
				min=temp.timeSpent();
				}
			
		}
		return util.TimeFormat(min);
	}

	@Override
	public String maxTimeForSize(String size) {
		Set<String> names = taskNamesForSize(size);
		LogList temp;
		int max=-1;
		for(String name : names)
		{
			temp=new LogList(name);
			if((max==-1)||(max<temp.timeSpent())) {
				max=temp.timeSpent();
				}
			
		}
		return util.TimeFormat(max);
	}

	@Override
	public String avgTimeForSize(String size) {
		Set<String> names = taskNamesForSize(size);
		LogList temp;
		int total=0;
		for(String name : names)
		{
			temp=new LogList(name);
			total+=temp.timeSpent();
			
		}
		int average=total/names.size();
		return util.TimeFormat(average);
	}

	@Override
	public Set<String> taskNamesForSize(String size) {
		try {
			readAll();
			Set<String> names = new HashSet<>();
			for(LogList task : tasks)
			{
				if (task.size==size)
					names.add(task.name);
			}
			return names;
		}
		catch(Exception e) {
		return null;
		}
	}

	@Override
	public String elapsedTimeForAllTasks() {
		Set<String> names=taskNames();
		int time=0;
		for(String name : names)
		{
			time+=calculateTime(name);
		}
		return util.TimeFormat(time);
	}

	@Override
	public Set<String> taskNames() {
		try {
			readAll();
		}
		catch (Exception e) {return null;}
		Set<String> names = new HashSet<>();
		for(int i=0; i<tasks.size(); i++)
		{
			names.add(tasks.get(i).name);
		}
		return names;
	}

	@Override
	public Set<String> taskSizes() {
		try {
			readAll();
		}
		catch (Exception e) {
			return null;
			}
		//need to make sure only sizes with >1 entries are making it through
		Set<String> sizes = new HashSet<>();
		String temp;
		for(int i=0; i<tasks.size(); i++)
		{
			temp=tasks.get(i).size;
			for(int j=1; j<tasks.size(); j++){	
				if(temp==tasks.get(j).size) {
					sizes.add(tasks.get(i).size);
				}
			}
			//if it clears this loop without finding a duplicate, the entry does not belong
		}
		return sizes;
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
			if(!(type.equals("start")||type.equals("stop"))) {
				if (queue.get(i).type.equals("size")){
					size=queue.get(i).input;
				}
				if (queue.get(i).type.equals("describe")){
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
	
	
	
	int timeSpent()
	{
		//if program has been used correctly the queue will now have only time logs alternating start and stop
		//but that's a big if
		LocalDateTime start, stop;
		int minutes=0;
		try {
			for(int i=0;i<queue.size();i+=2){
				start=LocalDateTime.parse(queue.get(i).input);
				stop=LocalDateTime.parse(queue.get(i+1).input);
				minutes+=ChronoUnit.MINUTES.between(start,stop);
			}
		}
		catch(Exception e) {
			return -1;
		};
		return minutes;
	}
	
	void write() throws Exception {
		Log temp;
		if(size!=null && !size.isEmpty()){
			temp=new Log("size",name,size);
			temp.write();
		}
		if(description!=null && !description.isEmpty()){
			temp=new Log("describe",name,description);
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
			
	public void write() throws Exception
	{
		FileWriter writer;
		writer=new FileWriter(util.filename, true);
		if(type=="describe")
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
		
}