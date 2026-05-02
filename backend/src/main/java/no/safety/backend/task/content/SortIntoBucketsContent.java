package no.safety.backend.task.content;

import java.util.List;

/**
 * {@link TaskContent} for a task where the user drags items into labelled
 * buckets. Each item has a single correct bucket identified by
 * {@link Bucket#key()}.
 *
 * @param prompt question shown to the user
 * @param unplacedLabel label for the area holding items that have not been placed yet
 * @param buckets the buckets items can be sorted into
 * @param items the items the user has to sort
 */
public record SortIntoBucketsContent(
    Prompt prompt,
    String unplacedLabel,
    List<Bucket> buckets,
    List<Item> items
) implements TaskContent {

  /**
   * Question shown above the buckets.
   *
   * @param title heading for the question
   * @param text supporting body text
   */
  public record Prompt(String title, String text) {

  }

  /**
   * A target bucket items can be sorted into.
   *
   * @param key stable id
   * @param title title shown on the bucket
   * @param description description text shown on the bucket
   * @param backgroundColor background color of the bucket
   */
  public record Bucket(
      String key,
      String title,
      String description,
      String backgroundColor
  ) {

  }

  /**
   * A single draggable item the user has to sort into a {@link Bucket}.
   *
   * @param id stable identifier for this item
   * @param label text shown on the item
   * @param correctBucketKey {@link Bucket#key()} of the bucket this item belongs in
   * @param explanation feedback shown when the item is sorted
   */
  public record Item(
      String id,
      String label,
      String correctBucketKey,
      String explanation
  ) {

  }
}
