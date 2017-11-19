package com.thickman.aurora.util;

import android.support.annotation.ColorRes;

import com.thickman.aurora.R;
import com.thickman.aurora.enums.RecurringFrequency;
import com.thickman.aurora.object.Transaction;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TransactionUtils {

    private static final String POS_FORMAT = "$%.2f";
    private static final String NEG_FORMAT = "-$%.2f";
    private static final String ABS_FORMAT = "%.2f";
    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final String DATE_RECURRING_FORMAT = "On day %s each month";

    @ColorRes
    public static int getAmountColor(float amount) {
        if (amount < 0) return R.color.transaction_red;
        return R.color.transaction_green;
    }

    public static String getAmountFormat(float amount) {
        return getAmountFormat(amount, true);
    }

    public static String getAmountFormat(float amount, boolean showDollarSign) {
        String format = (showDollarSign ? (amount < 0 ? NEG_FORMAT : POS_FORMAT) : ABS_FORMAT);
        return String.format(format, Math.abs(amount));
    }

    public static String getDate(float dateMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        return sdf.format(dateMillis);
    }

    public static String getRecurringDateText(float dateMillis) {
        return String.format(DATE_RECURRING_FORMAT, splitDate(dateMillis)[1]);
    }

    public static String[] splitDate(float dateMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        String dateFormatted = sdf.format(dateMillis);
        return dateFormatted.split("/");
    }

    public static int[] splitDateInt(float dateMillis) {
        String split[] = splitDate(dateMillis);
        return new int[] { Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2]) };
    }

    public static Transaction copy(Transaction t) {
        return copy(t, -1);
    }

    public static Transaction copy(Transaction t, int schedulerId) {
        boolean fromScheduler = schedulerId >= 0;
        Transaction ret = new Transaction();
        ret.setId(fromScheduler ? schedulerId : ret.getId());
        ret.setRecurring(!fromScheduler && t.isRecurring());
        ret.setRecurringFrequency(fromScheduler ? RecurringFrequency.NONE.name() : t.getRecurringFrequency());
        ret.setDayOfMonth(t.getDayOfMonth());
        ret.setMonthOfYear(t.getMonthOfYear());
        ret.setYear(t.getYear());
        ret.setAmount(t.getAmount());
        ret.setCategory(t.getCategory());
        ret.setIncome(t.isIncome());
        ret.setPayee(t.getPayee());
        ret.setDate(t.getDate());
        return ret;
    }
}
