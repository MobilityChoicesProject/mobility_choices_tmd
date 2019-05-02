import static org.junit.Assert.assertEquals;

import at.fhv.transportClassifier.common.BinaryCollectionSearcher;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * Created by Johannes on 13.03.2017.
 */
public class BinarySearchUtilTestTest   {

    BinaryCollectionSearcher<Integer,Integer> binaryCollectionSearcher = new BinaryCollectionSearcher();

    @Test
    public void testEvenNumberAtEnd(){

        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);

       int index=  binaryCollectionSearcher.find(list,1,(item, o) -> {
            return item - o;
        });

        int index1=  binaryCollectionSearcher.find(list,7,(item, o) -> {
            return item - o;
        });


        int index2=  binaryCollectionSearcher.find(list,6,(item, o) -> {
            return item - o;
        });

        assertEquals(0,index);
        assertEquals(6,index1);
        assertEquals(5,index2);

    }









}