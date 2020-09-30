package com.homer.telemed;

public class Exercise {
    private String exerciseDesc;
    private String exerciseVidLink;
    private int exerciseNum;

    public Exercise(String exerciseDesc, String exerciseVidLink, int exerciseNum) {
        this.exerciseDesc = exerciseDesc;
        this.exerciseVidLink = exerciseVidLink;
        this.exerciseNum = exerciseNum;
    }

    public String getExerciseDesc() {
        return exerciseDesc;
    }

    public void setExerciseDesc(String exerciseDesc) {
        this.exerciseDesc = exerciseDesc;
    }

    public String getExerciseVidLink() {
        return exerciseVidLink;
    }

    public void setExerciseVidLink(String exerciseVidLink) {
        this.exerciseVidLink = exerciseVidLink;
    }

    public int getExerciseNum() {
        return exerciseNum;
    }

    public void setExerciseNum(int exerciseNum) {
        this.exerciseNum = exerciseNum;
    }

    @Override
    public String toString() {
        return "Exercise " + exerciseNum;
    }
}
