package com.devsync.util;

import com.devsync.domain.entity.Task;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {

    public static String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }


    public static long calculateRemainingDays(Task task) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = task.getDueDate();

        return Duration.between(now, dueDate).toDays();
    }

}
