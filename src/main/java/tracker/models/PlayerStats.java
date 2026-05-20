package tracker.models;

public record PlayerStats(String uuid, String username, int kills, int deaths, double kdr) {
    public static double kdr(int kills, int deaths) {
        return kills / (double) Math.max(1, deaths);
    }
}
