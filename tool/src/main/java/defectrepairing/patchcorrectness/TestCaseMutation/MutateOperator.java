package defectrepairing.patchcorrectness.TestCaseMutation;

public class MutateOperator {

	final static int INT_ADD_ONE = 1;
	final static int INT_SUB_ONE = 2;
	final static int INT_TO_ZERO = 3;
	final static int INT_TO_ONE = 4;
	final static int INT_TO_MINUSONE = 5;
	final static int INT_TO_MAX = 6;
	final static int INT_TO_MIN = 7;
	final static int INT_ADD_TWO = 8;
	final static int INT_SUB_TWO = 9;
	final static int INT_ADD_THREE = 10;
	final static int INT_SUB_THREE = 11;

	final static int DOUBLE_ADD_ONE = -1;
	final static int DOUBLE_SUB_ONE = -2;
	final static int DOUBLE_MUL_TWO = -3;
	final static int DOUBLE_DIV_TWO = -4;
	final static int DOUBLE_TO_ZERO = -5;
	final static int DOUBLE_TO_ONE = -6;
	final static int DOUBLE_TO_MINUSONE = -7;

	final static int DOUBLE = 1;
	final static int INT = 0;

	public int gettype(String input) {
		try {
			Integer.parseInt(input);
			return INT;
		} catch (NumberFormatException e) {
			// do nothing
		}
		try {
			Double.parseDouble(input);
			return DOUBLE;
		} catch (NumberFormatException e) {
			// do nothing
		}
		return -1;
	}

	public String randommutate(String input) {
		int type = gettype(input);
		int operation = -1;
		if (type == DOUBLE) {
			operation = -((int) (Math.random() * 7) + 1);
		}
		if (type == INT) {
			operation = (int) (Math.random() * 11) + 1;
		}
		return mutate(input, type, operation);
	}

	public String mutate(String input, int type, int operation) {
		if (type == INT)
			return mutateint(input, operation);
		if (type == DOUBLE)
			return mutatedouble(input, operation);
		return input;
	}

	public String mutatedouble(String input, int operation) {
		double number = Double.parseDouble(input);
		switch (operation) {
		case DOUBLE_ADD_ONE:
			return String.valueOf(number + 1);
		case DOUBLE_SUB_ONE:
			return String.valueOf(number - 1);
		case DOUBLE_MUL_TWO:
			return String.valueOf(number * 2);
		case DOUBLE_DIV_TWO:
			return String.valueOf(number / 2);
		case DOUBLE_TO_ZERO:
			return String.valueOf(0.0);
		case DOUBLE_TO_ONE:
			return String.valueOf(1.0);
		case DOUBLE_TO_MINUSONE:
			return String.valueOf(-1.0);
		default:
			return input;
		}

	}

	public String mutateint(String input, int operation) {
		int number = Integer.parseInt(input);
		switch (operation) {
		case INT_ADD_ONE:
			return String.valueOf(number + 1);
		case INT_SUB_ONE:
			return String.valueOf(number - 1);
		case INT_ADD_TWO:
			return String.valueOf(number + 2);
		case INT_SUB_TWO:
			return String.valueOf(number - 2);
		case INT_ADD_THREE:
			return String.valueOf(number + 3);
		case INT_SUB_THREE:
			return String.valueOf(number - 3);
		case INT_TO_ZERO:
			return String.valueOf(0);
		case INT_TO_ONE:
			return String.valueOf(1);
		case INT_TO_MINUSONE:
			return String.valueOf(-1);
		case INT_TO_MAX:
			return "2147483647";
		case INT_TO_MIN:
			return "-2147483648";
		default:
			return input;
		}
	}

}
