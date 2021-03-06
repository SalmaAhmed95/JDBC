package jdbc;
 
import java.io.InputStream;
 
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
 
public class JResultSet implements ResultSet {
 
    private ArrayList<LinkedHashMap<String, String>> tableMap;
    private ArrayList<LinkedHashMap<String, String>> MetaData;
    private ArrayList<String> colMapping;
    private JStatement sqlStatement;
    private int cursor;
    private boolean isClosed;
    private LinkedHashMap<String, String> meta;
    private LinkedHashMap<String, String> metaD;
 
    public JResultSet(ArrayList<LinkedHashMap<String, String>> tableMap) {
        this.tableMap = tableMap;
        MetaData = new ArrayList<LinkedHashMap<String, String>>();
        meta = new LinkedHashMap<String, String>();
        if (tableMap.size() > 2) {
            meta = tableMap.get(2);
        }
        metaD = new LinkedHashMap<String, String>();
        Iterator itr = meta.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry m = (Map.Entry) itr.next();
            String colLabel = (String) m.getKey();
            String Type = tableMap.get(1).get(colLabel);
            metaD.put(colLabel, Type);
        }
        MetaData.add(tableMap.get(0));
        MetaData.add(metaD);
        tableMap.remove(0);
        tableMap.remove(0);
        colMapping = new ArrayList<String>();
        cursor = 0;
        isClosed = false;
        if (tableMap.size() > 0) {
            mapCols();
        }
 
    }
 
    public void mapCols() {
        LinkedHashMap<String, String> map = tableMap.get(0);
        Iterator itr = map.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry mp = (Map.Entry) itr.next();
            String colLabel = (String) mp.getKey();
            colMapping.add(colLabel);
        }
    }
 
    protected void setStatement(JStatement statement) {
        sqlStatement = statement;
    }
 
    public int getSize() {
        return tableMap.size();
    }
 
    private void checkExistance() throws SQLException {
        if (sqlStatement.isClosed()) {
            throw new SQLException("The Statement That Created This Statement Has Been Closed!");
        }
        if (isClosed) {
            throw new SQLException("The ResultSet Has Been Closed!");
        }
    }
 
    private void checkType(String key, String type) throws SQLException {
        String colType = MetaData.get(1).get(key);
        if (!colType.equals(type))
            throw new SQLException("Type is not an " + type + "!");
    }
 
    @Override
    public boolean absolute(int row) throws SQLException { // modified
        checkExistance();
        if (row ==0) {
            cursor = 0;
            return false;
        } else if (row > tableMap.size()) {
            cursor = tableMap.size() + 1;
            return false;
        } else if (row > 0) {
            cursor = row;
        } else {
            cursor = tableMap.size() + row + 1;
        }
        return true;
    }
 
    @Override
    public boolean isBeforeFirst() throws SQLException {
        checkExistance();
        if (cursor == 0 && tableMap.size() != 0) // modification
            return true;
        return false;
    }
 
    @Override
    public boolean isAfterLast() throws SQLException {
        checkExistance();
        if (cursor == tableMap.size() + 1 && tableMap.size() != 0) // modification
            return true;
        return false;
    }
 
    @Override
    public boolean isFirst() throws SQLException {
        checkExistance();
        if (cursor == 1 && tableMap.size() != 0) // modification
            return true;
        return false;
    }
 
    @Override
    public boolean isLast() throws SQLException {
        checkExistance();
        if (cursor == tableMap.size() && tableMap.size() != 0) // modification
            return true;
        return false;
    }
 
    @Override
    public void beforeFirst() throws SQLException {
        checkExistance();
        cursor = 0;
    }
 
    @Override
    public void afterLast() throws SQLException {
        checkExistance();
        cursor = tableMap.size() + 1;
 
    }
 
    @Override
    public boolean first() throws SQLException {
        checkExistance();
        if (tableMap.size() > 0) {
            cursor = 1;
            return true;
        }
        return false;
    }
 
    @Override
    public boolean last() throws SQLException {
        checkExistance();
        if (tableMap.size() > 0) {
            cursor = tableMap.size();
            return true;
        }
        return false;
    }
 
    @Override
    public boolean next() throws SQLException { // modified
        checkExistance();
        if (cursor < tableMap.size()) {
            cursor++;
            return true;
        }
        if (cursor == tableMap.size()) {
            cursor++;
        }
        return false;
    }
 
    @Override
    public boolean previous() throws SQLException { // modified
        checkExistance();
        if (cursor > 1) {
            cursor--;
            return true;
        }
        if (cursor == 1) {
            cursor--;
        }
        return false;
    }
 
    @Override
    public Object getObject(int columnIndex) throws SQLException {
        checkExistance();
        if (columnIndex < 1 || columnIndex > tableMap.get(0).size()) {
            throw new SQLException("Invalid column !");
        }
        if (cursor > tableMap.size() || cursor < 1) {
            throw new SQLException("Invalid Row !");
        }
        String key = colMapping.get(columnIndex - 1);
        String colType = MetaData.get(1).get(key);
        if(colType.equals("int")){
            int value=Integer.parseInt(tableMap.get(cursor - 1).get(key));
            return (Object)value;
        }
        else if(colType.equals("varchar")){
            String value = tableMap.get(cursor - 1).get(key);
            return (Object)value;
        }
        else if(colType.equals("float")){
            float value = Float.parseFloat(tableMap.get(cursor - 1).get(key));
            return (Object)value;
        }
        else if(colType.equals("date")){
            Date value = java.sql.Date.valueOf(tableMap.get(cursor - 1).get(key));
            return (Object)value;
        }
        return null;
       
    }
 
    @Override
    public int getInt(int columnIndex) throws SQLException {
        checkExistance();
        if (columnIndex < 1 || columnIndex > tableMap.get(0).size()) {
            throw new SQLException("Invalid column !");
        }
        if (cursor > tableMap.size() || cursor < 1) {
            throw new SQLException("Invalid Row !");
        }
 
        String key = colMapping.get(columnIndex - 1);
        checkType(key, "int");
        if (tableMap.get(cursor - 1).get(key).equals(null))
            return 0;
        int value = Integer.parseInt(tableMap.get(cursor - 1).get(key));
        return value;
    }
 
    @Override
    public int getInt(String columnLabel) throws SQLException {
        checkExistance();
        int columnIndex = colMapping.indexOf(columnLabel);
        if (columnIndex < 0) {
            throw new SQLException("Invalid column !");
        }
        if (cursor > tableMap.size() || cursor < 1) {
            throw new SQLException("Invalid Row !");
        }
        checkType(columnLabel, "int");
        if (tableMap.get(cursor - 1).get(columnLabel).equals(null))
            return 0;
        int value = Integer.parseInt(tableMap.get(cursor - 1).get(columnLabel));
        return value;
    }
 
    @Override
    public String getString(int columnIndex) throws SQLException {
        checkExistance();
        if (columnIndex < 1 || columnIndex > tableMap.get(0).size()) {
            throw new SQLException("Invalid column !");
        }
        if (cursor > tableMap.size() || cursor < 1) {
            throw new SQLException("Invalid Row !");
        }
        String key = colMapping.get(columnIndex - 1);
        checkType(key, "varchar");
        if (tableMap.get(cursor - 1).get(key).equals("null"))
            return null;
        String value = tableMap.get(cursor - 1).get(key);
        return value;
    }
 
    @Override
    public String getString(String columnLabel) throws SQLException {
        checkExistance();
        int columnIndex = colMapping.indexOf(columnLabel);
        if (columnIndex < 0) {
            throw new SQLException("Invalid column !");
        }
        if (cursor > tableMap.size() || cursor < 1) {
            throw new SQLException("Invalid Row !");
        }
        checkType(columnLabel, "varchar");
        if (tableMap.get(cursor - 1).get(columnLabel).equals("null"))
            return null;
        String value = tableMap.get(cursor - 1).get(columnLabel);
        return value;
    }
 
    @Override
    public float getFloat(int columnIndex) throws SQLException {
        checkExistance();
        if (columnIndex < 1 || columnIndex > tableMap.get(0).size()) {
            throw new SQLException("Invalid column !");
        }
        if (cursor > tableMap.size() || cursor < 1) {
            throw new SQLException("Invalid Row !");
        }
        String key = colMapping.get(columnIndex - 1);
        checkType(key, "float");
        if (tableMap.get(cursor - 1).get(key).equals(null))
            return 0;
        float value = Float.parseFloat(tableMap.get(cursor - 1).get(key));
        return value;
    }
 
    @Override
    public float getFloat(String columnLabel) throws SQLException {
        checkExistance();
        int columnIndex = colMapping.indexOf(columnLabel);
        if (columnIndex < 0 || columnIndex > tableMap.get(0).size() - 1) {
            throw new SQLException("Invalid column !");
        }
        if (cursor > tableMap.size() || cursor < 1) {
            throw new SQLException("Invalid Row !");
        }
        checkType(columnLabel, "float");
        if (tableMap.get(cursor - 1).get(columnLabel).equals(null))
            return 0;
        float value = Float.parseFloat(tableMap.get(cursor - 1).get(columnLabel));
        return value;
    }
 
    @Override
    public Date getDate(int columnIndex) throws SQLException {
        checkExistance();
        if (columnIndex < 1 || columnIndex > tableMap.get(0).size()) {
            throw new SQLException("Invalid column !");
        }
        if (cursor > tableMap.size() || cursor < 1) {
            throw new SQLException("Invalid Row !");
        }
        String key = colMapping.get(columnIndex - 1);
        checkType(key, "date");
        if (tableMap.get(cursor - 1).get(key).equals("null"))
            return null;
 
        Date value = java.sql.Date.valueOf(tableMap.get(cursor - 1).get(key));
        return value;
    }
 
    @Override
    public Date getDate(String columnLabel) throws SQLException {
        checkExistance();
        int columnIndex = colMapping.indexOf(columnLabel);
        if (columnIndex < 0) {
            throw new SQLException("Invalid column !");
        }
        if (cursor > tableMap.size() || cursor < 1) {
            throw new SQLException("Invalid Row !");
        }
        checkType(columnLabel, "date");
        if (tableMap.get(cursor - 1).get(columnLabel).equals("null"))
            return null;
        Date value = java.sql.Date.valueOf(tableMap.get(cursor - 1).get(columnLabel));
        return value;
    }
 
    @Override
    public int findColumn(String columnLabel) throws SQLException {
        checkExistance();
        int columnIndex = colMapping.indexOf(columnLabel) + 1;
        if (columnIndex < 0) {
            throw new SQLException("Invalid column !");
        }
        return columnIndex;
    }
 
    @Override
    public Statement getStatement() throws SQLException {
        checkExistance();
        return sqlStatement;
    }
 
    @Override
    public void close() throws SQLException {
        isClosed = true;
    }
 
    @Override
    public boolean isClosed() throws SQLException {
        return isClosed;
    }
 
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        checkExistance();
        JResultSetMetaData metaData = new JResultSetMetaData(tableMap.get(0).size(), MetaData, colMapping);
        metaData.setResultSet(this);
        return metaData;
    }
 
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public boolean wasNull() throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public byte getByte(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public short getShort(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public long getLong(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public double getDouble(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Time getTime(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public byte getByte(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public short getShort(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public long getLong(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public double getDouble(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Time getTime(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public SQLWarning getWarnings() throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public void clearWarnings() throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public String getCursorName() throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Object getObject(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public int getRow() throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public boolean relative(int rows) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public void setFetchDirection(int direction) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public int getFetchDirection() throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public void setFetchSize(int rows) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public int getFetchSize() throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public int getType() throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public int getConcurrency() throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public boolean rowUpdated() throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public boolean rowInserted() throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public boolean rowDeleted() throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public void updateNull(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateNull(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void insertRow() throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateRow() throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void deleteRow() throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void refreshRow() throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void cancelRowUpdates() throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public void moveToInsertRow() throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void moveToCurrentRow() throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Array getArray(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Array getArray(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public URL getURL(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public URL getURL(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
       
    }
 
    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public int getHoldability() throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public String getNString(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public String getNString(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
 
    }
 
    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        throw new java.lang.UnsupportedOperationException();
    }
 
}