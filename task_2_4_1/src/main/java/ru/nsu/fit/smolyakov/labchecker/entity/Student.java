package ru.nsu.fit.smolyakov.labchecker.entity;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@NonNull
@Builder
public class Student {
    String nickName;
    String fullName;
    String repoUrl;
    String defaultBranchName;
    String docsBranch;

    @Singular("newAssignment")
    List<AssignmentStatus> assignmentStatusList;
    @Singular("newLesson")
    List<LessonStatus> lessonStatusList;
}