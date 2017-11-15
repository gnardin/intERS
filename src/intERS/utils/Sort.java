package intERS.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Sort {
  
  
  @SuppressWarnings("unchecked")
  public static Map<Integer, Double>
      descendentSortByValue(Map<Integer, Double> map) {
    List list = new LinkedList(map.entrySet());
    Collections.sort(list, new Comparator<Object>() {
      
      
      public int compare(Object o1, Object o2) {
        return -((Comparable) ((Map.Entry) (o1)).getValue())
            .compareTo(((Map.Entry) (o2)).getValue());
      }
    });
    
    Map<Integer, Double> result = new LinkedHashMap<Integer, Double>();
    for(Iterator<?> it = list.iterator(); it.hasNext();) {
      Map.Entry entry = (Map.Entry) it.next();
      result.put((Integer) entry.getKey(), (Double) entry.getValue());
    }
    return result;
  }
  
  
  @SuppressWarnings("unchecked")
  public static Map<Integer, Double>
      ascendingSortByValue(Map<Integer, Double> map) {
    List list = new LinkedList(map.entrySet());
    Collections.sort(list, new Comparator<Object>() {
      
      
      public int compare(Object o1, Object o2) {
        return ((Comparable) ((Map.Entry) (o1)).getValue())
            .compareTo(((Map.Entry) (o2)).getValue());
      }
    });
    
    Map<Integer, Double> result = new LinkedHashMap<Integer, Double>();
    for(Iterator<?> it = list.iterator(); it.hasNext();) {
      Map.Entry entry = (Map.Entry) it.next();
      result.put((Integer) entry.getKey(), (Double) entry.getValue());
    }
    return result;
  }
}