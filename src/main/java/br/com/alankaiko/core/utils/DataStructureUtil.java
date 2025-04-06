package br.com.alankaiko.core.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class DataStructureUtil {
    public static Boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static Boolean isEmpty(Object[] list) {
        return list == null || list.length == 0;
    }

    public static <T> List<T> getList(List<T> list) {
        return (List) (ObjectUtil.isNull(list) ? new ArrayList() : list);
    }

    public static <T> Page<T> listToPage(List<T> list, Pageable pageable) {
        if (ObjectUtil.isNull(pageable)) {
            pageable = PageRequest.of(0, 10);
        }

        if (!isEmpty(list)) {
            int fromIndex = (int) ((Pageable) pageable).getOffset();
            int toIndex = Math.min(fromIndex + ((Pageable) pageable).getPageSize(), list.size());
            return new PageImpl(list.subList(fromIndex, toIndex), (Pageable) pageable, (long) list.size());
        } else {
            return new PageImpl(new ArrayList(), (Pageable) pageable, 0L);
        }
    }
}
