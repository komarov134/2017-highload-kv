package ru.mail.polis.neron;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @author neron
 */
public class Partitioner {

  private final int totalNodeCount;

  public Partitioner(final int totalNodeCount) {
    if (totalNodeCount <= 0) {
      throw new IllegalArgumentException("Node count should be positive: " + totalNodeCount);
    }
    this.totalNodeCount = totalNodeCount;
  }

  private int keyHash(@NotNull String key) {
    final int hash = key.hashCode();
    if (hash == Integer.MIN_VALUE) {
      return Integer.MAX_VALUE;
    } else {
      return Math.abs(key.hashCode());
    }
  }

  @NotNull
  private int[] allNodes(int hash) {
    final int[] nodes = new int[totalNodeCount];
    for (int i = 0; i < totalNodeCount; i++) {
      nodes[i] = hash++ % totalNodeCount;
    }
    return nodes;
  }

  @NotNull
  int[] nodes(@NotNull String key, int count) {
    if (count <= 0 || count > totalNodeCount) {
      throw new IllegalArgumentException("Incorrect 'count' parameter: " + count);
    }
    return Arrays.copyOfRange(allNodes(keyHash(key)), 0, count);
  }
}
