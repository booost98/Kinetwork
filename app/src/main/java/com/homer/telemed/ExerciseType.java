package com.homer.telemed;

public class ExerciseType {
    private int exerciseTypeID;
    private String exerciseType;

    public ExerciseType(int exerciseTypeID, String exerciseType) {
        this.exerciseTypeID = exerciseTypeID;
        this.exerciseType = exerciseType;
    }

    public int getExerciseTypeID() {
        return exerciseTypeID;
    }

    public void setExerciseTypeID(int exerciseTypeID) {
        this.exerciseTypeID = exerciseTypeID;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    @Override
    public String toString() {
        return exerciseType;
    }
}

