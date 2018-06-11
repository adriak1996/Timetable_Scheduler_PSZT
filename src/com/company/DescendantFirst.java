package com.company;

public class DescendantFirst implements  IDescendantMethods{
    public int participantId;
    public int realizationId;
    public double fitnessFuntionValue;

    public DescendantFirst(int participantId, int realizationId)
    {
        this.participantId = participantId;
        this.realizationId = realizationId;
    }


    public double CalculateFitnessFunction()
    {
        int preferedCoursesNumber = Scheduler.participants.get(participantId).declarations.size();
        int preferedParticipantsInGroup = 0;

        Participant thisParticipant = Scheduler.participants.get(participantId);
        Realization thisRealization = Scheduler.realizations.get(realizationId);

        fitnessFuntionValue = 0;

        if(!thisParticipant.declarations.containsKey(thisRealization.courseTypeId)) return 0;
        // this realization is not interesting for participant


        for(int i = 0; i<thisParticipant.preferences.size(); i++)
        {
            Participant preferedFriend = Scheduler.participants.get(thisParticipant.preferences.get(i));
            if(preferedFriend.declarations.containsValue(thisRealization.courseTypeId)) ++preferedParticipantsInGroup;
        }

        fitnessFuntionValue = (preferedCoursesNumber*590 + (preferedParticipantsInGroup^2)*30);

        return fitnessFuntionValue;
    }
}
