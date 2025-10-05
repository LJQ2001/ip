
# Resonant User Guide

Welcome to **Resonant**, your friendly command-line task manager chatbot!  
Resonant helps you keep track of your daily tasks, deadlines, and events — all through simple text commands.

---


## 📖 Table of Contents
1. [Introduction](#-introduction)
2. [Quick Start](#-quick-start)
3. [Features](#-features)
    - [List Tasks](#list-tasks)
    - [Adding Todo](#adding-todo)
    - [Adding Deadlines](#adding-deadlines)
    - [Adding Events](#adding-events)
    - [Deleting Tasks](#deleting-tasks)
    - [Mark Tasks](#mark-tasks)
    - [Unmark Tasks](#unmark-task)
    - [Find Tasks](#find-tasks)
    - [Exit Program](#exit-program)
4. [Command Summary](#-command-summary)


---


# 💡 Introduction

**Resonant** is a simple text-based chatbot that helps you manage tasks quickly and efficiently.  
Type a command, press **Enter**, and Resonant will do the rest.

Example:
todo read book
deadline submit report /by 2025-10-10
event meeting /at Friday 2pm
list
bye

---

# ⚙️ Quick Start

1. Ensure you have Java 17 or above installed in your Computer.

2. Download the latest .jar file from 

3. opy the file to the folder you want to use as the home folder for Resonant.

4. Open a command terminal, cd into the folder you put the jar file in, and use the java -jar ip.jar command to run the application.

---

# 🚀 Features 

## List Tasks

Show all tasks that is added inside Resonant in the past

Example:
`list`



Expected outcome:
Resonant show all tasks that has been stored and their current status.

Example output :

```
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[T][ ] cooking
 3.[D][ ] assignment (by: choclate)
 4.[E][X] exam (from: monday 6pm to: 9pm)
```



## Adding Todo

Adding a general tasks without any date of time attached to it.

Example:
`todo <description>`

Example usage:
`todo read book`

Expected outcome:
Resonant adds a new todo task and confirms it has been added to your list.

Example output :

```
Got it. I've added this task:
       [T][ ] borrow book
     Now you have 5 tasks in the list.
```


## Adding deadlines


Adds a task that must be completed by a specific date and time.  
This helps you keep track of assignments, submissions, or other time-sensitive tasks.


Example:
`deadline <description> /by <date or time>`


Example usage:
`deadline submit report /by 2025-10-10`

Expected outcome:
Resonant adds a new deadline task and confirms it has been added to your list.  

Example output:

``` 
Got it. I've added this task:
[D][ ] submit report (by: 2025-10-10)
Now you have 3 tasks in the list.
```

## Adding Events

Adding a task that tasks that start at a specific date/time and ends at a specific date/time.

Example:
`event <description> /from <start> /to <end>`


Example usage:
`event project meeting /from Mon 2pm /to 4pm`

Expected outcome:
Resonant adds a new event task and confirms it has been added to your list.

Example output:

``` 
Got it. I've added this task:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     Now you have 7 tasks in the list.
```


## Deleting Tasks

Deleting a specific task from the list.

Example:
`delete <Number>`


Example usage:
```
list
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][X] return book (by: June 6th)
     3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
     4.[T][X] join sports club
     5.[T][ ] borrow book
    ____________________________________________________________

delete 3
```

Expected outcome:
Resonant will delete that task based on its numbered position on the list while confirming the deletion to the user.

Example output:

``` 
 Noted. I've removed this task:
       [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
     Now you have 4 tasks in the list.
     
 list  
     ____________________________________________________________  
     ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][X] return book (by: June 6th)
     3.[T][X] join sports club
     4.[T][ ] borrow book

```

## Mark Tasks

Marking a task as done or complete.

Example:
`mark <number>`


Example usage:
```
list
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[T][ ] cooking
3.[D][ ] assignment (by: choclate)
4.[E][X] exam (from: monday 6pm to: 9pm)`

mark 2
```

Expected outcome:
Resonant will mark the task based on its numbered position on the list with an X symbol.

Example output:

``` 
 Nice! I've marked this task as done:
   [T][X] cooking
   
 list
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[T][X] cooking
 3.[D][ ] assignment (by: choclate)
 4.[E][X] exam (from: monday 6pm to: 9pm)
```

## Unmark Task

Unmarking a task from complete to incomplete.

Example:
`unmark <number>`


Example usage:
```
list
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[T][X] cooking
3.[D][ ] assignment (by: choclate)
4.[E][X] exam (from: monday 6pm to: 9pm)`

unmark 2
```

Expected outcome:
Resonant will unmark the task based on its numbered position on the list by removing the X symbol from that task.

Example output:

``` 
 OK, I've marked this task as not done yet:
   [T][ ] cooking
   
 list
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[T][ ] cooking
 3.[D][ ] assignment (by: choclate)
 4.[E][X] exam (from: monday 6pm to: 9pm)
```

## Find Tasks

Find a specific task.

Example:
`find <keyword>`

Example usage:
```
list
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[T][ ] exam study
3.[E][X] exam (from: monday 6pm to: 9pm)
____________________________________________________________
____________________________________________________________

find exam
```

Expected outcome:
Resonant will find the tasks that has the keyword in the task's name.

Example output:
```
Example output:
1.[T][ ] exam study
2.[E][X] exam (from: monday 6pm to: 9pm)
```


## Exit Program

Exit resonant.

Example:
`bye`

Expected outcome:
The Resonant CLI program will exit and close with a goodbye message.

Example output:
```
 Bye. Hope to see you again soon!
```

---

# 🧭 Command Summary

| Command | Format | Example | Description |
|----------|---------|----------|--------------|
| **Todo** | `todo <description>` | `todo read book` | Adds a general task without a date/time. |
| **Deadline** | `deadline <description> /by <date or time>` | `deadline submit report /by 2025-10-10` | Adds a task with a specific due date/time. |
| **Event** | `event <description> /from <start> /to <end>` | `event project meeting /from Mon 2pm /to 4pm` | Adds an event with start and end times. |
| **List** | `list` | `list` | Displays all tasks currently saved. |
| **Mark** | `mark <task number>` | `mark 2` | Marks a specific task as done. |
| **Unmark** | `unmark <task number>` | `unmark 2` | Marks a specific task as not done yet. |
| **Delete** | `delete <task number>` | `delete 3` | Removes a task from the list. |
| **Find** | `find <keyword>` | `find book` | Finds tasks containing the given keyword. |
| **Exit** | `bye` | `bye` | Exits Resonant and saves all tasks. |


