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
    
    public void PrintParticipant()
    {
        System.out.println("#" + this.participantId);
        for (Map.Entry<Integer, Integer> m : this.declarations.entrySet())
    {
        System.out.println(" Declaration: " + m.getKey());
        System.out.println("              " + m.getValue());
        System.out.println();
    }
        System.out.println("Preferences: ");

        for (Integer i : this.preferences)
        {
            System.out.println(i);
            System.out.println();
        }
        
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

}
