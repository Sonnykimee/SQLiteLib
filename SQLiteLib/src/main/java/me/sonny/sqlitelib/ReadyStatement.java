package me.sonny.sqlitelib;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadyStatement {

    private String statement;

    public ReadyStatement(String statement) {
        this.statement = statement;
    }

    public void changeStatement(String statement) {
        this.statement = statement;
    }

    public void set(String value) {
        Pattern pattern = Pattern.compile("(?<!')\\?(?!')");
        Matcher matcher = pattern.matcher(statement);

        if (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            statement = statement.substring(0, start) + value + statement.substring(end);
        }
    }

    public String getStatement() {
        return statement;
    }
}
