package com.company;

import java.util.*;

public class Participant {

    public int participantId;
    public Map<Integer, Integer> declarations; // id of course type, id of course realization (-1 if none)
    public ArrayList<Integer> preferences; // id of other participants with whom, participant wants to be in group


    public Participant() {
        declarations = new HashMap<Integer, Integer>();
        preferences = new ArrayList<Integer>();
    }

    static public void GenerateParticipants()
    {
        Participant temp = new Participant();
        temp.participantId = Scheduler.participants.size();
        Random rand = new Random();

        for(int i = 0; i < Scheduler.courseTypesAmount; i++)
        {
            int courseType = rand.nextInt(12) - 6;
            if(courseType >= 0)
            {
                if(!temp.declarations.containsKey(courseType))
                temp.declarations.put(courseType, -1);
            }
        }

        if(temp.declarations.size() == 0)
            GenerateParticipants();
        else
            Scheduler.participants.add(temp);

    }

    public void GeneratePreferences()
    {
        Random rand = new Random();
        int  friendsAmount = rand.nextInt(6);

        for(int i = 0; i < friendsAmount; i++)
        {
            int preferedParticipant = rand.nextInt(Scheduler.participantsAmount + 100) - 100;
            if(preferedParticipant >= 0)
            {
                if(!this.preferences.contains(preferedParticipant))
                    this.preferences.add(preferedParticipant);
            }
        }
    }


    public String PrintParticipantIO()
    {
        int tempRoomID;
        String output = ""
                + "Participant ID #" + this.participantId + "\nCourse you are enrolled on:";
        for (Map.Entry<Integer, Integer> m : this.declarations.entrySet())
        {
            if(m.getValue() != -1)
            {
                 tempRoomID = (Scheduler.realizations.get(m.getValue())).roomId;
                 if(tempRoomID != -1)
                 {
                    output += "\n\t\t Realization: #" + m.getValue() + ",";
                    output += "\tRoom: #" + tempRoomID;
                 }
            }
        }
        return output + "\n";
    }

}
