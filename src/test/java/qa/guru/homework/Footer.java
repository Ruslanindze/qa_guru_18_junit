package qa.guru.homework;

public enum Footer {
    COLUMN_FIRST("Личный кабинет"),
    COLUMN_SECOND("Иностранцам"),
    COLUMN_THIRD("Карта центров обслуживания");

    private String footer;
    Footer(String str) {
        footer = str;
    }

    public String getStringValue() {
        return footer;
    }

}
