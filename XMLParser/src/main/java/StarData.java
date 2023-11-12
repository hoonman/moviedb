public class StarData {

    String first_name;
    String last_name;
    boolean isInvalidEntry;

    String stage_name;
    int year;
    public StarData() {
    }

    @Override
    public String toString() {
        return "Star{" +
                "first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", isInvalidEntry=" + isInvalidEntry +
                ", stage_name='" + stage_name + '\'' +
                ", year=" + year +
                '}';
    }

    public StarData(String first_name, String last_name, String stage_name, int year) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.year = year;
        this.stage_name = stage_name;
    }



    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
    public String getStage_name() {
        return stage_name;
    }

    public void setStage_name(String stage_name) {
        this.stage_name = stage_name;
    }
    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
    public void setInvalidEntry(boolean invalidEntry) {
        isInvalidEntry = invalidEntry;
    }
    public boolean isInvalidEntry() {
        return isInvalidEntry;
    }

}
