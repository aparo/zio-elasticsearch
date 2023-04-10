/*
 * Copyright 2019-2023 Alberto Paro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zio.elasticsearch.watcher
import zio._
import zio.json._
import zio.json.ast._
final case class SlackAttachment(
  @jsonField("author_icon") authorIcon: Option[String] = None,
  @jsonField("author_link") authorLink: Option[String] = None,
  @jsonField("author_name") authorName: String,
  color: Option[String] = None,
  fallback: Option[String] = None,
  fields: Option[Chunk[SlackAttachmentField]] = None,
  footer: Option[String] = None,
  @jsonField("footer_icon") footerIcon: Option[String] = None,
  @jsonField("image_url") imageUrl: Option[String] = None,
  pretext: Option[String] = None,
  text: Option[String] = None,
  @jsonField("thumb_url") thumbUrl: Option[String] = None,
  title: String,
  @jsonField("title_link") titleLink: Option[String] = None,
  ts: Option[Long] = None
)

object SlackAttachment {
  implicit lazy val jsonCodec: JsonCodec[SlackAttachment] =
    DeriveJsonCodec.gen[SlackAttachment]
}
