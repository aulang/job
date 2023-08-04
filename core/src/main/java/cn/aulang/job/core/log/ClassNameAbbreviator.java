package cn.aulang.job.core.log;

/**
 * 包名省略器
 *
 * @author wulang
 */
public class ClassNameAbbreviator {

    private static final char DOT = '.';
    private static final int MAX_DOTS = 16;

    public ClassNameAbbreviator() {
    }

    public String abbreviate(String className) {
        if (className == null) {
            return "";
        }

        StringBuilder buf = new StringBuilder();


        int[] dotIndexesArray = new int[MAX_DOTS];
        int[] lengthArray = new int[MAX_DOTS + 1];

        int dotCount = computeDotIndexes(className, dotIndexesArray);

        if (dotCount == 0) {
            return className;
        }

        computeLengthArray(className, dotIndexesArray, lengthArray, dotCount);

        for (int i = 0; i <= dotCount; i++) {
            if (i == 0) {
                buf.append(className, 0, lengthArray[i] - 1);
            } else {
                buf.append(className, dotIndexesArray[i - 1], dotIndexesArray[i - 1] + lengthArray[i]);
            }
        }

        return buf.toString();
    }

    int computeDotIndexes(final String className, int[] dotArray) {
        int dotCount = 0;
        int k = 0;
        while (true) {
            k = className.indexOf(DOT, k);
            if (k != -1 && dotCount < MAX_DOTS) {
                dotArray[dotCount] = k;
                dotCount++;
                k++;
            } else {
                break;
            }
        }
        return dotCount;
    }

    void computeLengthArray(final String className, int[] dotArray, int[] lengthArray, int dotCount) {
        int len;
        for (int i = 0; i < dotCount; i++) {
            int previousDotPosition = -1;
            if (i > 0) {
                previousDotPosition = dotArray[i - 1];
            }

            int available = dotArray[i] - previousDotPosition - 1;

            len = Math.min(available, 1);

            lengthArray[i] = len + 1;
        }

        int lastDotIndex = dotCount - 1;
        lengthArray[dotCount] = className.length() - dotArray[lastDotIndex];
    }
}
