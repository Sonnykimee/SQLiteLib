package me.sonny.sqlitelib;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class ReadyStatement {

    private PreparedStatement ps;

    protected ReadyStatement(PreparedStatement ps) {
        this.ps = ps;
    }

    protected PreparedStatement preparedStatement() {
        return ps;
    }

    public void setBoolean(int parameterIndex, boolean value) {
        try {
            ps.setBoolean(parameterIndex, value);
        } catch (SQLException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.PARAMETER_ERROR + ", Error Message: " + e.getMessage());
        }
    }

    public void setInt(int parameterIndex, int value) {
        try {
            ps.setInt(parameterIndex, value);
        } catch (SQLException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.PARAMETER_ERROR + ", Error Message: " + e.getMessage());
        }
    }

    public void setLong(int parameterIndex, long value) {
        try {
            ps.setLong(parameterIndex, value);
        } catch (SQLException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.PARAMETER_ERROR + ", Error Message: " + e.getMessage());
        }
    }

    public void setFloat(int parameterIndex, float value) {
        try {
            ps.setFloat(parameterIndex, value);
        } catch (SQLException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.PARAMETER_ERROR + ", Error Message: " + e.getMessage());
        }
    }

    public void setDouble(int parameterIndex, double value) {
        try {
            ps.setDouble(parameterIndex, value);
        } catch (SQLException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.PARAMETER_ERROR + ", Error Message: " + e.getMessage());
        }
    }

    public void setString(int parameterIndex, String value) {
        try {
            ps.setString(parameterIndex, value);
        } catch (SQLException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.PARAMETER_ERROR + ", Error Message: " + e.getMessage());
        }
    }
}
