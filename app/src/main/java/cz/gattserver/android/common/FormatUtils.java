package cz.gattserver.android.common;

public class FormatUtils {

    private FormatUtils() {
    }

    public static String formatRatingStars(double rating) {
        return formatRatingStars((int) rating);
    }

    public static String formatRatingStars(int rating) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++)
            sb.append(i < rating ? "★" : "☆");
        return sb.toString();
    }

}
