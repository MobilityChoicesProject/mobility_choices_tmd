package at.fhv.transportClassifier.common;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Johannes on 12.03.2017.
 */
public class BinaryCollectionSearcher<T,TKey> implements Serializable{



  private long serialVersionUID = 8093624330552382219l;

    public int find(List<? extends T> items, TKey key,KeyComparator<T,TKey> comparator){


        int size = items.size();

        int lo = 0;
        int hi = size - 1;
        while (lo <= hi) {
            // Key is in a[lo..hi] or not present.
            int mid = lo + (hi - lo) / 2;

            T item = items.get(mid);
            int comparison = comparator.compare(item,key);

            if (  comparison > 0 )  hi = mid - 1;
            else if (comparison < 0) lo = mid + 1;
            else return mid;
        }
        return -lo-1;
    }

    public int findClosest(List<? extends T> items, TKey key,KeyComparator<T,TKey> comparator){

        if(items.size() == 0){
            return -1;
        }
        int index = find(items, key, comparator);
        int size = items.size();
        if(index<0){
            int insertPosition = (index+1)*-1;
            if(insertPosition>= size){
                index = size-1;
            }else{
                index = insertPosition;
            }

        }

        return index;

    }


}


