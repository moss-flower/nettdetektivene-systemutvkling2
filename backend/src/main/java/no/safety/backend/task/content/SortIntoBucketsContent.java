package no.safety.backend.task.content;

import java.util.List;

public record SortIntoBucketsContent(
    Prompt prompt,
    String unplacedLabel,
    List<Bucket> buckets,
    List<Item> items
) implements TaskContent {

  public record Prompt(String title, String text) {

  }

  public record Bucket(
      String key,
      String title,
      String description,
      String backgroundColor
  ) {

  }

  public record Item(
      String id,
      String label,
      String correctBucketKey,
      String explanation
  ) {

  }
}
