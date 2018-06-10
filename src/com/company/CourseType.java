package com.company;

public class CourseType {
    public int courseTypeId;
    public int courseLength;                 // in hours
    public int maxParticipantAmount;
    public int costForHiringLecturer;
    public int courseCost;                   // for full course

    public CourseType(int courseLength, int maxParticipantAmount, int costForHiringLecturer, int courseCost)
    {
        this.courseTypeId = Scheduler.courseTypes.size();
        this.courseLength = courseLength;
        this.maxParticipantAmount = maxParticipantAmount;
        this.costForHiringLecturer = costForHiringLecturer;
        this.courseCost = courseCost;
    }

    public static void GetCourseTypes()
    {
        Scheduler.courseTypes.add(new CourseType(7,30,10000, 900));
        Scheduler.courseTypes.add(new CourseType(6,20,8000, 750));
        Scheduler.courseTypes.add(new CourseType(5,35,8000, 540));
        Scheduler.courseTypes.add(new CourseType(4,60,8000, 500));
        Scheduler.courseTypes.add(new CourseType(3,45,6000, 540));
        Scheduler.courseTypes.add(new CourseType(2,40,3000, 300));
    }

}
