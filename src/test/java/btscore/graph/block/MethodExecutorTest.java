package btscore.graph.block;

import btscore.graph.block.MethodExecutor.InvocationResult;
import java.lang.reflect.Method;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author joostmeulenkamp
 */
public class MethodExecutorTest {

    @BeforeAll
    public static void loadBlockLibrary() {
        try {
            BlockLibraryLoader.loadBlocks();
            System.out.println("Loaded blocks: " + BlockLibraryLoader.BLOCK_LIBRARY.size());
        } catch (Exception e) {
            System.err.println("Exception in @BeforeAll: " + e);
            e.printStackTrace();
        }

    }

    @Test
    public void testMethodMathAdd_WhenOnePlusOne_ThenReturnTwo() {
        System.out.println("testMethodMathAdd_WhenOnePlusOne_ThenReturnTwo");
        
        BlockLibraryLoader.loadBlocks();

        // Create test data
        int a = 1;
        int b = 1;
        Method mathAdd = (Method) BlockLibraryLoader.BLOCK_LIBRARY.get("Math.add");
        MethodExecutor executor = new MethodExecutor(mathAdd);

        // Perform test
        InvocationResult rawResult = executor.invoke(a, b);

        // Prepare results
        int expected = 2;
        long result = (long) rawResult.data().get();

        // Evaluate results
        assertEquals(expected, result, "One plus one is NOT two.");
    }
    @Test
    public void testMethodMathAdd_WhenOnePlusOneTwoThree_ThenReturnTwoThreeFour() {
        System.out.println("testMethodMathAdd_WhenOnePlusOneTwoThree_ThenReturnTwoThreeFour");

        BlockLibraryLoader.loadBlocks();

        // Create test data
        int a = 1;
        List<Integer> b = List.of(1, 2, 3);
        Method mathAdd = (Method) BlockLibraryLoader.BLOCK_LIBRARY.get("Math.add");
        MethodExecutor executor = new MethodExecutor(mathAdd);

        // Perform test
        InvocationResult rawResult = executor.invoke(a, b);

        // Prepare results
        List<Integer> expected = List.of(2, 3, 4);
        List<?> result = (List<?>) rawResult.data().get();

        // Evaluate results
        int i = 0;
        for (int value : expected) {
            assertEquals(value, (long) result.get(i), "One plus one, two, three is NOT two, three, four.");
            i++;
        }

    }

}
