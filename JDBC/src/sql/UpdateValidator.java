package sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import dbms.Constants;
import utilities.Logs;

public class UpdateValidator {

	private String[] keys;
	private ArrayList<String> query;
	private int mode;
	private Logs logger;
    
	public UpdateValidator() {
		logger = new Logs();
		initialize();
	}

	private void setKeys(String[] keys) {
		this.keys = keys;
	}

	public int getMode() {
		return mode;
	}

	private void initialize() {
		keys = null;
		query = new ArrayList<String>();
		mode = Constants.INVALID;
	}

	// validates UPDATE syntax
	public ArrayList<String> validateUpdate(String[] keys) throws SQLException {
		initialize();
		setKeys(keys);
		if (keys.length < Constants.UPDATE_MIN) {
			declareError("UPDATE", "Missing Information!");
		} else {
			try {
				if (keys[Constants.NEXT_KEY].equalsIgnoreCase("set")) {
					query.add(keys[Constants.NEXT_KEY - 1].toLowerCase()); // table name to lower
					if (keys[Constants.NEXT_KEY + 2].equals("=")) {
						int i = validateEquality(Constants.NEXT_KEY + 2);
						test(i, Constants.UPDATE_REC, "UPDATE");
					} else {
						declareError("UPDATE", "Incorrect \"=\"!");
					}
				} else {
					declareError("UPDATE", "\"SET\" Keyword Is Missing!");
				}
			} catch (Exception e) {
				declareError("UPDATE", e.getMessage());
				throw new SQLException(e.getMessage());
			}
		}
		return query;
	}

	// validates after SET syntax
	private int validateEquality(int i) throws SQLException {
		while (true) {
			if (keys[i].equals("=")) {
				query.add(keys[i - 1].toLowerCase()); // toLowerCase()
				appendType(i + 1);
				query.add(keys[i + 1].replace("'", ""));
				if (keys[i + 2].equals(",")) {
					i += 4;
				} else {
					break;
				}
			} else {
				return i - 2;
			}
		}
		return i + 2;
	}

	private void appendType(int i) throws SQLException {
        if (keys[i].startsWith("'")) {
            query.add("varchar");
        } else if (Pattern.matches("[+-]?[0-9]+", keys[i])) {
            query.add("int");
        } else if (Pattern.matches(
                "((?:19|20)\\d\\d)-(0?[1-9]|1[012])-([12][0-9]|3[01]|0?[1-9])",
                keys[i])) {
            query.add("date");
        }else if(Pattern.matches("[+-]?([0-9]*[.])?[0-9]+",keys[i])) {
            query.add("float");
        }
        else {
        	logger.error("The Value You have Entered Does not Match Any DataType!");
            throw new SQLException("The Value You have Entered Does not Match Any DataType!");
        }
    }

	// tests the end of the query
	private void test(int i, int cur, String order) throws SQLException {
		String check = keys[i].toLowerCase();
		switch (check) {
		case ";": {
			if (i == keys.length - 1) {
				mode = cur;
			} else {
				declareError(order, "Multiple Semicolons!");
			}
			break;
		}
		case "where": {
			if (i == keys.length - 5) { // only one where
				validateWhere(i + 2, cur, order); // i+2 >> operator index
			} else {
				declareError(order, "Incorrect Where Syntax!");
			}
			break;
		}
		default: {
			declareError(order, "Undefined!");
		}
		}
	}

	// validates conditions
	private void validateWhere(int i, int cur, String order) throws SQLException {
		if (keys[i].equals(">") || keys[i].equals("<") || keys[i].equals("=")) {
			query.add("where");
			query.add(keys[i - 1].toLowerCase()); // toLowerCase()
			query.add(keys[i]);
			appendType(i + 1);
			query.add(keys[i + 1].replace("'", ""));
			test(i + 2, cur, order);
		} else {
			declareError(order, "No Logical Operator Found In The Condition!");
		}
	}

	private void declareError(String order, String message) throws SQLException {
		logger.error("Invalid " + order + " Statement!");
    	logger.error(message);
		mode = Constants.INVALID;
		throw new SQLException("Invalid " + order + " Statement! >> " + message);
	}
}
