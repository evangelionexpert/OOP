package ru.nsu.fit.smolyakov.evaluationsuite.entity.course.assignment;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

//TODO кажется я не те аннотации использую, поменять потом
@ToString(exclude = {"assignment"})
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignmentStatus implements Serializable {
    public static final LocalDate NOT_STARTED = LocalDate.MAX; // todo ??

    @NonNull
    final Assignment assignment;

    @NonNull
    @Setter
    String identifierAlias;

    @Setter
    String branch;

    @NonNull @Setter String message = "(non overridden) empty message";


    final Pass pass = new Pass();
    final Grade grade = new Grade();

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NonNull
    public class Pass implements Serializable { // todo rename
        LocalDate started = LocalDate.MAX;
        LocalDate finished = LocalDate.MAX;

        public boolean isSkippedSoftDeadline() {
            return this.started.isAfter(AssignmentStatus.this.assignment.getSoftDeadline().plusDays(1));
        }

        public boolean isSkippedHardDeadline() {
            return this.finished.isAfter(AssignmentStatus.this.assignment.getHardDeadline().plusDays(1));
        }

        public boolean isStarted() {
            return !this.started.equals(NOT_STARTED);
        }

        public boolean isFinished() {
            return !this.finished.equals(NOT_STARTED);
        }
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public class Grade implements Serializable {
        boolean buildPassed = false;
        boolean javadocPassed = false;

        @Setter(AccessLevel.NONE)
        @Getter(AccessLevel.NONE)
        Double jacocoCoverage = null;

        @Setter(AccessLevel.NONE)
        @Getter(AccessLevel.NONE)
        Double overriddenTaskPoints = null;
        
        public boolean isTestsPassed() {
            return isTestsCompile() &&
                jacocoCoverage >= AssignmentStatus.this.assignment.getJacocoPassCoefficient();
        }

        public boolean isTestsCompile() {
            return jacocoCoverage != null;
        }

        public void setJacocoCoverage(double coverage) {
            this.jacocoCoverage = coverage;
        }

        public Optional<Double> getJacocoCoverage() {
            return Optional.ofNullable(jacocoCoverage);
        }

        public void setTestsPassed() {
            if (AssignmentStatus.this.assignment.isRunTests()) {
                this.jacocoCoverage = 1.0;
            }
        }

        public void overrideTaskPoints(double points) {
            this.overriddenTaskPoints = points;
        }

        public void notOverrideTaskPoints() {
            this.overriddenTaskPoints = null;
        }

        public boolean isOverridden() {
            return Objects.nonNull(this.overriddenTaskPoints);
        }

        public double getCalculatedTaskPoints() {
            double sum = 0;
            int amount = 0;

            sum += (buildPassed ? 1 : 0);
            amount++;
            sum += (javadocPassed ? 1 : 0);
            amount++;

            if (AssignmentStatus.this.assignment.isRunTests()) {
                sum += (jacocoCoverage != null ? jacocoCoverage : 0);
                amount++;
            }
            return AssignmentStatus.this.assignment.getSolvedPoints() * (sum / amount);
        }

        public Optional<Double> getOverriddenTaskPoints() {
            return Optional.ofNullable(overriddenTaskPoints);
        }

        public double getEarnedPoints() {
            if (!AssignmentStatus.this.getPass().isFinished()) {
                return 0.0;
            } else if (this.isOverridden()) {
                return this.overriddenTaskPoints;
            } else {
                return this.getCalculatedTaskPoints();
            }
        }

        public double getFine() {
            double fine = 0;
            if (AssignmentStatus.this.getPass().isSkippedSoftDeadline()) {
                fine += AssignmentStatus.this.assignment.getSoftDeadlineSkipFine();
            }
            if (AssignmentStatus.this.getPass().isSkippedHardDeadline()) {
                fine += AssignmentStatus.this.assignment.getHardDeadlineSkipFine();
            }

            return fine;
        }

        public double getResultingPoints() {
            if (!AssignmentStatus.this.getPass().isFinished()) {
                return getFine();
            } else {
                return getEarnedPoints() + getFine();
            }
        }
    }

    public AssignmentStatus(@NonNull Assignment assignment, @NonNull String identifierAlias, String branch) {
        this.assignment = assignment;
        this.identifierAlias = identifierAlias;
        this.branch = branch;
    }

    public boolean hasBranch() {
        return Objects.nonNull(this.branch);
    }

    public Optional<String> getBranch() {
        return Optional.ofNullable(this.branch);
    }
}
