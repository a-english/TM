
README v1.0 / 01 FEBUARY 2018

# Task Manager
## Introduction

Command-line program useful for programmers who want to keep track of how much time they spend on their projects.

## Usage

From specifications given:

> To execute the application from the command line you should use the
> following general format.
> 
> java TM \<command> \<data>
> 
> Where command can be one of, start, stop, describe, or summary.
> 
> java TM start <task name>	Logs the start time of a task with name <task name>
>	java TM stop <task name>	Logs the stop time of a task with name <task name>
>	java TM describe <task name> <description> (optional)<task size>	Logs the description of the task with name <task name>. Requires quotes around description. Optionally allows user to add a task size as well.
>	java TM summary <task name>	Provides a report of the activity and total time spent working on task with name <task name>
>	java TM summary		Provides a report of the activity and total time spent working on ALL tasks
>   java TM size <task name> <task size> Assigns a T-shirt size to a task.
>	java TM stats <task size>	 Displays statistical info for all entries of given size. 
>	java TM stats		Provides statistical data for all entries of all sizes. 
>	java TM rename <task name> <new name> 	Renames task in log 
>	java TM delete <task name> 		Remove current task from log. 



Inputting with zero arguments or an incorrect argument format displays these instructions.

## Reasoning

The log wrapper is mostly used for parsing to and from the text file with Java's date file.
It can be constructed either by three fields, which is best when creating a log to write to the file, or by a single text line as it would be read form the log.

Having a secondary LogList queue to keep track of logs on a task-by-task basis is sensible and improves readability.

Since reading involves multiple logs at a time, the read function is a part of the main class instead and uses Log.

A linked list is the data structure I chose for doing operations on the log because it is easy to expand to whatever size is needed, and it is simple to calculate the time by simply popping off logs.

Since there are many different options for input, a case selection is used, first to determine the number of arguments since trying to read an argument that does not exist causes an exception.

The calculate function seems like the most likely to fail, but since the user can easily edit the log itself to correct for forgetfulness it seems unnecessary to validate further.

When calculating the statistics for different sized tasks, it accepts any input. "Medium" will be calculated differently from "M" or "med". This allows the user to give herself extra specificity without excessive error checking. If they think it's bigger than medium but not quite large they can call it "kinda big" and the program will calculate it appropriately.

The stats calculator has preconditions that the file has already been loaded into a linked list of LogLists.

I opted to go with a rewrite option for Delete and Rename features because both tasks can be achieved with the help of a function that reads all entries, and one that clears the file and rewrites all entries. The postcondition of the write task is that the log file is now sorted by task.



## Known Issues

* The calculation function only works if the time log is accurate; ie, start stop start stop...
* Java util date incorrectly calculates noon as midnight. Should use Date instead.
* Output file can simply be edited, which could make parsing problematic if edited incorrectly.

## Areas for Improvement
* Could use a line that confirms successful operation on input commands

## Credits

Built using examples from stackoverflow.com, baledung.com, and avajava.com
Template for this documentation based on example from opensource.com