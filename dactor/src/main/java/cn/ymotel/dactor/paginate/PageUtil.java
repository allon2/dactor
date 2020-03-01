package cn.ymotel.dactor.paginate;

import java.util.ArrayList;
import java.util.List;

public class PageUtil {
    public static int getFirstResult(int pageNumber, int pageSize) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("[pageSize] must great than zero");
        } else {
            return (pageNumber - 1) * pageSize;
        }
    }

    public static Integer[] generateLinkPageNumbers(int currentPageNumber, int lastPageNumber, int count) {
        int avg = count / 2;
        int startPageNumber = currentPageNumber - avg;
        if (startPageNumber <= 0) {
            startPageNumber = 1;
        }

        int endPageNumber = startPageNumber + count - 1;
        if (endPageNumber > lastPageNumber) {
            endPageNumber = lastPageNumber;
        }

        if (endPageNumber - startPageNumber < count) {
            startPageNumber = endPageNumber - count;
            if (startPageNumber <= 0) {
                startPageNumber = 1;
            }
        }

        List<Integer> result = new ArrayList();

        for(int i = startPageNumber; i <= endPageNumber; ++i) {
            result.add(new Integer(i));
        }

        return (Integer[])result.toArray(new Integer[result.size()]);
    }
    public static int computeLastPageNumber(int totalElements, int pageSize) {
        int result = totalElements % pageSize == 0 ? totalElements / pageSize : totalElements / pageSize + 1;
        if (result <= 1) {
            result = 1;
        }

        return result;
    }
    public static int computePageNumber(int pageNumber, int pageSize, int totalElements) {
        if (pageNumber <= 1) {
            return 1;
        } else {
            return 2147483647 != pageNumber && pageNumber <= computeLastPageNumber(totalElements, pageSize) ? pageNumber : computeLastPageNumber(totalElements, pageSize);
        }
    }
}
