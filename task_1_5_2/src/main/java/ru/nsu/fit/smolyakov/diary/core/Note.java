package ru.nsu.fit.smolyakov.diary.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * An entry class
 *
 * @param heading
 * @param contents
 * @param date
 */
public record Note(
        @JsonProperty("heading")
        String heading,

        @JsonProperty("text")
        String text,

        @JsonProperty("date")
        @JsonFormat(
//                pattern = DateTimeFormatter.ISO_ZONED_DATE_TIME
        )
        ZonedDateTime date
) {
    /**
     * Creates a new instance of {@code Note} with a specified
     * {@code heading} and {@code contents} and current date of creation
     * (latter is provided by {@link ZonedDateTime#now()}).
     *
     * @param  heading  a string literal that specifies a heading of an entry
     * @param  contents a string literal that specifies a text of an entry
     * @return a new instance of {@code Note}
     */
    public static Note create(String heading, String contents) {
        return new Note(heading, contents, ZonedDateTime.now(Clock.systemDefaultZone()));
    }

    /**
     * Returns {@code true} if this {@code Note} heading contains
     * a specified {@code keyword}.
     *
     * @param keyword a string to be found in the heading of this {@code Note}
     * @return {@code true} if this {@code Note} heading contains
     *         a specified {@code keyword}.
     */
    public boolean contains(String keyword) { // TODO maybe rename maybe not
        return heading.contains(keyword);
    }

    /**
     * Returns {@code true} if this {@code Note} was created
     * after a specified {@code date}.
     *
     * @param  date a specified {@link ZonedDateTime}
     * @return {@code true} if this {@code Note} was created
     *         after a specified {@code date}.
     */
    public boolean after(ZonedDateTime date) {
        return this.date.isAfter(date);
    }

    /**
     * Returns {@code true} if this {@code Note} was created
     * before a specified {@code date}.
     *
     * @param  date a specified {@link ZonedDateTime}
     * @return {@code true} if this {@code Note} was created
     *         before a specified {@code date}.
     */
    public boolean before(ZonedDateTime date) {
        return this.date.isBefore(date);
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return """
                Heading: %s
                Text: %s
                ------------
                Date: %s
                
                """
                .formatted(
                        heading,
                        text,
                        date.format(DateTimeFormatter.RFC_1123_DATE_TIME)
                );
    }
}
