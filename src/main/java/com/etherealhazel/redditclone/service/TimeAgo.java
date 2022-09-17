package com.etherealhazel.redditclone.service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
* The TimeAgo class impliments a methods that take a time stamp and
* return an "ago" string equal to the amount of time that has passed
* between the current time and the time stamp.
* 
* @author Joshua Knippel
* @version 1.0
* @since 2022-08-28
* @see https://stackoverflow.com/questions/3859288/how-to-calculate-time-ago-in-java
*  
*/

public class TimeAgo {

    // time measurment units
    private static final List<String> TIME_UNIT_STRINGS = 
    Arrays.asList("year", "month", "day", "hour", "minute", "second");

    // time measurement units converted to milliseconds
    private static final List<Long> TIMES = Arrays.asList(
        TimeUnit.DAYS.toMillis(365),
        TimeUnit.DAYS.toMillis(30),
        TimeUnit.DAYS.toMillis(1),
        TimeUnit.HOURS.toMillis(1),
        TimeUnit.MINUTES.toMillis(1),
        TimeUnit.SECONDS.toMillis(1)
    );

    /** 
     * This method takes a long corresponding a timestamp in millisecod epoch 
     * time and returns an appropriatly scaled coresponding "ago" sting.
     * Examples:"3 seconds ago", "1 minute ago", "7 hours ago",
     * "5 days ago", "1 month ago", "4 years ago"
     * 
     * @param epochMilli target timestap for "ago" string translation.
     * @return target "ago" string Examples: 
     * "3 seconds ago", "1 minute ago",
     * "7 hours ago", "5 days ago",
     * "1 month ago", "4 years ago" 
     * 
     */
    public static String using(long epochMilli) {

        StringBuffer res = new StringBuffer();

        // loop from largest to smallest (year -> seconds) until you find the right unit
        for(int i = 0; i < TimeAgo.TIMES.size(); i++) {

            // get the specific time measurement unit converted to milliseconds 
            Long currentTimeUnit = TimeAgo.TIMES.get(i);
            // get the quotient to help choose time measurement unit
            long scaleQuotient = epochMilli/currentTimeUnit;

            // build the string as soon we come to first unit with a non fractinal return
            if (scaleQuotient>0) {
                res.append(scaleQuotient).append(" ")
                    .append( TimeAgo.TIME_UNIT_STRINGS.get(i))
                    .append(scaleQuotient!= 1 ? "s" : "") // plural check
                    .append(" ago"); 
                break;
            }
        }

        if ("".equals(res.toString())) { return "0 seconds ago"; }
        else { return res.toString(); }
    }
}
