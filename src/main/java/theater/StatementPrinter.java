package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 */
public class StatementPrinter {
    @SuppressWarnings({"checkstyle:VisibilityModifier", "checkstyle:SuppressWarnings"})
    public Invoice invoice;
    @SuppressWarnings({"checkstyle:VisibilityModifier", "checkstyle:SuppressWarnings"})
    public Map<String, Play> plays;

    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    /**
     * Returns a formatted statement of the invoice associated with this printer.
     * @return the formatted statement
     * @throws RuntimeException if one of the play types is not known
     */
    @SuppressWarnings({"checkstyle:FinalLocalVariable", "checkstyle:SuppressWarnings", "checkstyle:MagicNumber", "checkstyle:NeedBraces", "checkstyle:VariableDeclarationUsageDistance"})
    public String statement() {
        StringBuilder result = new StringBuilder("Statement for " + invoice.getCustomer() + System.lineSeparator());
        for (Performance p : invoice.getPerformances()) {

            result.append(String.format("  %s: %s (%s seats)%n",
                    getPlay(p).getName(),
                    usd(getAmount(p)),
                    p.getAudience()));

        }
        result.append(String.format("Amount owed is %s%n", usd(getTotalAmount())));
        result.append(String.format("You earned %s credits%n", getTotalVolumeCredits()));
        return result.toString();
    }

    private int getTotalAmount() {
        int totalAmount = 0;
        for (Performance p : invoice.getPerformances()) {

            totalAmount += getAmount(p);
        }
        return totalAmount;
    }

    private int getTotalVolumeCredits() {
        int volumeCredits = 0;
        for (Performance p : invoice.getPerformances()) {

            // add volume credits
            volumeCredits += getVolumeCredits(p, volumeCredits);
        }
        return volumeCredits;
    }

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:SuppressWarnings"})
    private String usd(int totalAmount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(totalAmount / 100);
    }

    @SuppressWarnings({"checkstyle:ParameterName", "checkstyle:SuppressWarnings"})
    private int getVolumeCredits(Performance performance, int volumeCredits) {
        int result = 0;
        result += Math.max(performance.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
        if ("comedy".equals(getPlay(performance).getType())) {
            result += performance.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }
        return result;
    }

    @SuppressWarnings({"checkstyle:ParameterName", "checkstyle:SuppressWarnings"})
    private Play getPlay(Performance p) {
        return plays.get(p.getPlayID());
    }

    @SuppressWarnings({"checkstyle:ParameterName", "checkstyle:SuppressWarnings", "checkstyle:MagicNumber"})
    private int getAmount(Performance performance) {
        int result = 0;
        switch (getPlay(performance).getType()) {
            case "tragedy":
                result = 40000;
                if (performance.getAudience() > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    result += 1000 * (performance.getAudience() - 30);
                }
                break;
            case "comedy":
                result = Constants.COMEDY_BASE_AMOUNT;
                if (performance.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    result += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.getAudience() - Constants.COMEDY_AUDIENCE_THRESHOLD));
                }
                result += Constants.COMEDY_AMOUNT_PER_AUDIENCE * performance.getAudience();
                break;
            default:
                throw new RuntimeException(
                        String.format("unknown type: %s", getPlay(performance).getType()));
        }
        return result;
    }

}
