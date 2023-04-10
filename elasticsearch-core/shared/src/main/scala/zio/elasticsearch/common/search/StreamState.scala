package zio.elasticsearch.common.search

import zio.Chunk
import zio.elasticsearch.queries.Query
import zio.elasticsearch.sort.Sort.Sort
import zio.json.ast.Json

final case class StreamState(
                              indices: Chunk[String],
                              query: Query,
                              sort: Sort,
                              size: Int = 100,
                              nextAfter: Chunk[Json] = Chunk.empty,
                              response: Option[SearchResponse] = None,
                              pit: Option[String] = None,
                              scrollId: Option[String] = None
                            )
