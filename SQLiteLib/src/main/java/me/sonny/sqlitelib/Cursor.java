package me.sonny.sqlitelib;

import java.util.List;
import java.util.logging.Level;

/*
 * Cursor class provides an easy way to fetch data without having to parse Object every time.
 * Cursor will also have a feature to get data using column name String instead of index number.
 */
public class Cursor {

    private int position;

    private List<List<Object>> data;

    private List<String> columnNames;

    protected Cursor(List<List<Object>> data, List<String> columnNames) {
        this.data = data;
        this.columnNames = columnNames;
        position = -1;
    }

    /**
     * Returns the number of rows of fetched data
     * @return Returns the number of rows of fetched data
     */
    public int size() {
        return data.size();
    }

    /**
     * Move the position pivot to -1
     */
    public void beforeFirst() {
        position = -1;
    }

    /**
     * Move the position pivot to the size number of the fetched data
     */
    public void afterLast() {
        position = data.size();
    }

    /**
     * Move pivot to the next position.
     * @return If there is next, returns true. Otherwise false.
     */
    public boolean next() {
        if (data.size() > 0 && position < data.size()-1) {
            position++;
            return true;
        }

        return false;
    }

    /**
     * Move pivot to the previous position.
     * @return If there is previous, returns true. Otherwise false.
     */
    public boolean previous() {
        if (data.size() > 0 && position > 1) {
            position--;
            return true;
        }

        return false;
    }

    public boolean first() {
        if (data.size() > 0) {
            position = 0;
            return true;
        }

        return false;
    }

    public boolean last() {
        if (data.size() > 0) {
            position = data.size()-1;
            return true;
        }

        return false;
    }

    // Integer
    public Integer getInt(int column) {
        Integer value = null;

        try {
            value = (Integer) data.get(position).get(column);
        } catch (ClassCastException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.NOT_INTEGER);
        }

        return value;
    }
    public Integer getInt(String column) {
        Integer value = null;

        try {
            column = column.toUpperCase();
            value = (Integer) data.get(position).get(columnNames.indexOf(column));
        } catch (ClassCastException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.NOT_INTEGER);
        } catch (IndexOutOfBoundsException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.COLUMN_NOT_FOUND);
        }

        return value;
    }

    public Long getLong(int column) {
        Long value = null;

        try {
            value = (Long) data.get(position).get(column);
        } catch (ClassCastException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.NOT_LONG);
        }

        return value;
    }
    public Long getLong(String column) {
        Long value = null;

        try {
            column = column.toUpperCase();
            value = (Long) data.get(position).get(columnNames.indexOf(column));
        } catch (ClassCastException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.NOT_LONG);
        } catch (IndexOutOfBoundsException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.COLUMN_NOT_FOUND);
        }

        return value;
    }

    // Float
    public Float getFloat(int column) {
        Float value = null;

        try {
            value = (Float) data.get(position).get(column);
        } catch (ClassCastException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.NOT_FLOAT);
        }

        return value;
    }
    public Float getFloat(String column) {
        Float value = null;

        try {
            column = column.toUpperCase();
            value = (Float) data.get(position).get(columnNames.indexOf(column));
        } catch (ClassCastException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.NOT_FLOAT);
        } catch (IndexOutOfBoundsException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.COLUMN_NOT_FOUND);
        }

        return value;
    }

    // Double
    // *SQLite do not natively support double-precision. This is simply casting the value as Double.*
    public Double getDouble(int column) {
        Double value = null;

        try {
            value = (Double) data.get(position).get(column);
        } catch (ClassCastException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.NOT_DOUBLE);
        }

        return value;
    }
    public Double getDouble(String column) {
        Double value = null;

        try {
            column = column.toUpperCase();
            value = (Double) data.get(position).get(columnNames.indexOf(column));
        } catch (ClassCastException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.NOT_DOUBLE);
        } catch (IndexOutOfBoundsException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.COLUMN_NOT_FOUND);
        }

        return value;
    }

    // Boolean
    // *Boolean is actually an Integer in SQLite, True is a placeholder for 1, False for 0.*
    public Boolean getBoolean(int column) {
        Integer value;

        try {
            value = (Integer) data.get(position).get(column);

            if (value == null) {
                return null;
            } else if (value == 0) {
                return false;
            } else if (value == 1) {
                return true;
            } else {
                SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.NOT_BOOLEAN);
            }
        } catch (ClassCastException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.NOT_BOOLEAN);
        }

        return null;
    }
    public Boolean getBoolean(String column) {
        Integer value;

        try {
            column = column.toUpperCase();
            value = (Integer) data.get(position).get(columnNames.indexOf(column));

            if (value == null) {
                return null;
            } else if (value == 0) {
                return false;
            } else if (value == 1) {
                return true;
            } else {
                SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.NOT_BOOLEAN);
            }
        } catch (ClassCastException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.NOT_BOOLEAN);
        } catch (IndexOutOfBoundsException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.COLUMN_NOT_FOUND);
        }

        return null;
    }

    // String
    public String getString(int column) {
        String value = data.get(position).get(column).toString();

        return value;
    }
    public String getString(String column) {
        String value = null;
        try {
            column = column.toUpperCase();
            value = data.get(position).get(columnNames.indexOf(column)).toString();
        } catch (IndexOutOfBoundsException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.COLUMN_NOT_FOUND);
        }

        return value;
    }

    // Custom Type
    // For the data types that are not listed in the above.
    public <T> T getValue(int column, Class<T> type) {
        Object value = data.get(position).get(column);
        if (value == null) {
            return null;
        }

        return type.cast(value);
    }
    public <T> T getValue(String column, Class<T> type) {
        Object value = null;

        try {
            column = column.toUpperCase();
            int index = columnNames.indexOf(column);
            value = data.get(position).get(index);
            if (value == null) {
                return null;
            }
        } catch (IndexOutOfBoundsException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.COLUMN_NOT_FOUND);
        }

        return type.cast(value);
    }
}