/*
Cole Hartnett
CS 224 Professor Lee
3/9/2021
*********************************
This project is for a 224 Programming assignment
This class will ask the user for the name of a text file containing classes to be scheduled,
and will then schedule them to classrooms while providing output
*********************************
This class will have a constructor for the main that will ask the user to specify the main file
It will also have a function to partition the schedules
It will also define classes for a room and a lecture
*/
package com.company;

import java.io.*;
import java.util.*;

public class Main {
    //create a class to represent a lecture
    private class Lecture implements Comparable<Lecture>{
        private String id; //class name
        private int startTime;
        private int finishTime;

        //constructor
        private Lecture(String id, int startTime, int finishTime){
            this.id = id;
            this.startTime = startTime;
            this.finishTime = finishTime;
        }
        //getter methods
        public String getID(){
            return id;
        }
        public int getStartTime(){
            return startTime;
        }
        public int getFinishTime(){
            return finishTime;
        }
        //include a method that coverts an integer into a time displayed with a colon
        public String timeFromInt(int intTime){
            String stringTime = intTime + ""; //make string representation of int
            //use substring to instert a ":", first must check for length
            if (stringTime.length() == 3){
                stringTime = stringTime.substring(0,1) + ":" + stringTime.substring(1,3);
            }
            else if(stringTime.length() == 4){
                stringTime = stringTime.substring(0,2) + ":" + stringTime.substring(2,4);
            }
            return stringTime;
        }
        //Include a method that does the opposite by setting a string time into an into time with no colon
        public int intFromTime(String stringTime){
            stringTime = stringTime.replace(":", "");
            return Integer.parseInt(stringTime);
        }
        //override the method to compare lectures based for finish time for initial sorting
        @Override
        public int compareTo(Lecture compareLec){
            return this.getFinishTime() - compareLec.getFinishTime();
        }
        //Override toString method to print start and finish time neater
        @Override
        public String toString(){
            String stringST = timeFromInt(this.getStartTime());
            String stringFT = timeFromInt(this.getFinishTime());
            return "(" + getID() + ", " + stringST + ", " + stringFT + ")";
        }
    }
    //Create a class to represent a classroom
    private class Room implements Comparable<Room>{
        //lectures that the room holds
        private LinkedList<Lecture> lectures = new LinkedList<Lecture>();
        //the time the last lecture in said room finishes
        private int lastClass;
        //Room number to distinguish rooms
        private int roomNum;
        //constructor
        public Room(Lecture lecture, int roomNum){
            addLecture(lecture);
            this.roomNum = roomNum;
        }
        //include a method that allows the users to add new lectures to the schedule (needed for constructor)
        private void addLecture(Lecture l) {
            lectures.add(l);
            lastClass = (l.getFinishTime());
        }

        //List the getter methods for the class instances
        public int getLastClass() { return lastClass; }
        public int getRoomNum() { return roomNum; }
        public LinkedList<Lecture> getLectureList() { return lectures; }
        public Lecture getLastLec() { return lectures.getLast(); }
        //include an override to compare the finish times
        @Override
        public int compareTo(Room compareRoom){
            return this.getLastClass() - compareRoom.getLastClass();
        }

    }
    //Initialize a list of lectures to be stored
    ArrayList<Lecture> lectures = new ArrayList<Lecture>();
    //create a constructor for the class that will sort the intervals by ending time
    public Main(){
        //make a scanner for user input
        Scanner sc = new Scanner(System.in);
        //make a the file reader
        BufferedReader br;
        try{
            System.out.println("Please enter the fileName and don't include any parenthesis surrounding the lectures, only commas to separate lecture id and start/stop times: ");
            br = new BufferedReader(new FileReader(sc.nextLine()));
            String line = br.readLine();
            //while there are lectures to be scheduled
            while (line != null){
                String []data = line.split(", ");
                String lectureName = data[0];
                int startTime = Integer.parseInt(data[1]);
                int endTime = Integer.parseInt(data[2]);
                lectures.add(new Lecture(lectureName, startTime, endTime));
                line = br.readLine();
            }
        }
        catch(IOException e){
            System.out.println("File not found!");
        }
    }
    //create a method that will schedule the intervals
    public void intervalScheduling(){
        //sort the lectures
        Collections.sort(lectures);
        //list all the rooms and each of their lectures
        ArrayList<Room> allRooms = new ArrayList<Room>();
        //Create the priority queue
        PriorityQueue<Room> roomPrioQueue = new PriorityQueue<Room>();
        //begin the output
        String outPutMsg = "During the Output run: ";
        //set the number of rooms to 0
        int roomTotal =0;
        //add the first room into the queue and add the first lecture
        Room first = new Room(lectures.get(0), ++roomTotal);
        roomPrioQueue.add(first);
        allRooms.add(first);
        outPutMsg += "Classroom " + first.getRoomNum() + ":" + lectures.get(0);
        //loop over all the lectures
        for (int i=1; i<lectures.size(); i++) {
            //make temp room
            Room currRoom = roomPrioQueue.peek();
            //get current lecture
            Lecture currLec = lectures.get(i);
            //if the room is compatible
            if (currLec.getStartTime() >= currRoom.getLastClass()) {
                //add the lecture
                currRoom.addLecture(currLec);
                //remove from queue and add new one
                roomPrioQueue.remove();
                roomPrioQueue.add(currRoom);
                //change message
                outPutMsg += "\nClassroom " + currRoom.getRoomNum() + ":" + currLec;
            } else {
                //make a new room
                Room newRoom = new Room(currLec, ++roomTotal);
                //add it to the queue and list of all the rooms
                roomPrioQueue.add(newRoom);
                allRooms.add(newRoom);
                //change output message
                outPutMsg += "\nClassroom " + newRoom.getRoomNum() + ":" + currLec;
            }
        }
        //finish the output messsage
        outPutMsg += "\n\nAt the end of the Program Run: ";
        for (Room room: allRooms){
            outPutMsg += "\nClassroom " + room.getRoomNum() + ": " + room.getLectureList();
        }
        //print the output message
        System.out.println(outPutMsg);
    }
    public static void main(String[] args) {
	    Main program = new Main();
	    program.intervalScheduling();
    }
}
