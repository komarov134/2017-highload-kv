package ru.mail.polis.neron;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author neron
 */
public class PartitionerTest {

  @NotNull
  private static String randomKey() {
    return Long.toHexString(ThreadLocalRandom.current().nextLong());
  }

  @Test
  public void nodesSubSet() {
    for (int totalNodeCount = 2; totalNodeCount < 15; totalNodeCount++) {
      Partitioner partitioner = new Partitioner(totalNodeCount);
      String key = randomKey();
      int nodeCount = ThreadLocalRandom.current().nextInt(totalNodeCount);
      nodeCount = nodeCount == 0 ? 1 : nodeCount;
      int[] nodes = partitioner.nodes(key, totalNodeCount);
      int[] subNodes = partitioner.nodes(key, nodeCount);
      Assert.assertArrayEquals(Arrays.copyOf(nodes, subNodes.length), subNodes);
    }

  }
}
