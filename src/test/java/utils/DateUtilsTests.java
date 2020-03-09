package utils;

import com.propelleraero.gnsscompiler.compiler.utils.DateUtils;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class DateUtilsTests {

    @Test
    public void testHourBlockChar() {
        int[] inputs = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};
        String[] knownOutputs = {
                "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
                "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
                "w", "x"
        };
        String[] actual = new String[knownOutputs.length];

        for (int i = 0; i < knownOutputs.length; i++) {
            actual[i] = DateUtils.hourBlockChar(inputs[i]);
        }

        assertArrayEquals(knownOutputs, actual);
    }

}
