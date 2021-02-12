package org.dreamcat.jwrap.compress;

import java.io.IOException;
import java.util.function.IntFunction;
import org.dreamcat.common.util.RandomUtil;
import org.junit.Test;

/**
 * Create by tuke on 2020/4/7
 */
public class SnappyUtilTest {

    @Test
    public void test() throws IOException {
        byte[] data = RandomUtil.choose72(100).getBytes();
        byte[] compressed = SnappyUtil.compress(data);
        byte[] uncompressed = SnappyUtil.uncompress(compressed);

        System.out.println(data.length + "," + new String(data));
        System.out.println(compressed.length + "," + new String(compressed));
        System.out.println(uncompressed.length + "," + new String(uncompressed));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRatio() throws IOException {
        IntFunction<String>[] fns = new IntFunction[]{
                RandomUtil::choose10,
                RandomUtil::choose16,
                RandomUtil::choose26,
                RandomUtil::choose36,
                RandomUtil::choose52,
                RandomUtil::choose62,
                RandomUtil::choose72,
        };

        for (int i = 1; i < 1000; i++) {
            System.out.print(i + "\t");
            for (IntFunction<String> fn : fns) {
                byte[] data = fn.apply(i).getBytes();
                byte[] compressed = SnappyUtil.compress(data);
                //byte[] uncompressed = SnappyUtil.uncompress(compressed);
                System.out.print(compressed.length + " ");
            }
            System.out.print("\n");
        }
    }
}
