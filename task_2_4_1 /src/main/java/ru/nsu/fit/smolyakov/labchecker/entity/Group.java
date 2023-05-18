package ru.nsu.fit.smolyakov.labchecker.entity;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
@RequiredArgsConstructor
public class Group {
    String groupName;
    List<Student> studentList = new ArrayList<>();
}
