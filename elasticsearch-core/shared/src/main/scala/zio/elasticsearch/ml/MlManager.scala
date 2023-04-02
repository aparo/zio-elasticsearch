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

package zio.elasticsearch.ml

import zio.elasticsearch.common._
import zio.elasticsearch._
import zio.json._
import zio.json.ast._
import zio._
import zio.exception._
import zio.elasticsearch.ml.clear_trained_model_deployment_cache.ClearTrainedModelDeploymentCacheRequest
import zio.elasticsearch.ml.clear_trained_model_deployment_cache.ClearTrainedModelDeploymentCacheResponse
import zio.elasticsearch.ml.close_job.CloseJobRequest
import zio.elasticsearch.ml.close_job.CloseJobResponse
import zio.elasticsearch.ml.delete_calendar.DeleteCalendarRequest
import zio.elasticsearch.ml.delete_calendar.DeleteCalendarResponse
import zio.elasticsearch.ml.delete_calendar_event.DeleteCalendarEventRequest
import zio.elasticsearch.ml.delete_calendar_event.DeleteCalendarEventResponse
import zio.elasticsearch.ml.delete_calendar_job.DeleteCalendarJobRequest
import zio.elasticsearch.ml.delete_calendar_job.DeleteCalendarJobResponse
import zio.elasticsearch.ml.delete_data_frame_analytics.DeleteDataFrameAnalyticsRequest
import zio.elasticsearch.ml.delete_data_frame_analytics.DeleteDataFrameAnalyticsResponse
import zio.elasticsearch.ml.delete_datafeed.DeleteDatafeedRequest
import zio.elasticsearch.ml.delete_datafeed.DeleteDatafeedResponse
import zio.elasticsearch.ml.delete_expired_data.DeleteExpiredDataRequest
import zio.elasticsearch.ml.delete_expired_data.DeleteExpiredDataResponse
import zio.elasticsearch.ml.delete_filter.DeleteFilterRequest
import zio.elasticsearch.ml.delete_filter.DeleteFilterResponse
import zio.elasticsearch.ml.delete_forecast.DeleteForecastRequest
import zio.elasticsearch.ml.delete_forecast.DeleteForecastResponse
import zio.elasticsearch.ml.delete_job.DeleteJobRequest
import zio.elasticsearch.ml.delete_job.DeleteJobResponse
import zio.elasticsearch.ml.delete_model_snapshot.DeleteModelSnapshotRequest
import zio.elasticsearch.ml.delete_model_snapshot.DeleteModelSnapshotResponse
import zio.elasticsearch.ml.delete_trained_model.DeleteTrainedModelRequest
import zio.elasticsearch.ml.delete_trained_model.DeleteTrainedModelResponse
import zio.elasticsearch.ml.delete_trained_model_alias.DeleteTrainedModelAliasRequest
import zio.elasticsearch.ml.delete_trained_model_alias.DeleteTrainedModelAliasResponse
import zio.elasticsearch.ml.estimate_model_memory.EstimateModelMemoryRequest
import zio.elasticsearch.ml.estimate_model_memory.EstimateModelMemoryResponse
import zio.elasticsearch.ml.evaluate_data_frame.EvaluateDataFrameRequest
import zio.elasticsearch.ml.evaluate_data_frame.EvaluateDataFrameResponse
import zio.elasticsearch.ml.explain_data_frame_analytics.ExplainDataFrameAnalyticsRequest
import zio.elasticsearch.ml.explain_data_frame_analytics.ExplainDataFrameAnalyticsResponse
import zio.elasticsearch.ml.flush_job.FlushJobRequest
import zio.elasticsearch.ml.flush_job.FlushJobResponse
import zio.elasticsearch.ml.forecast.ForecastRequest
import zio.elasticsearch.ml.forecast.ForecastResponse
import zio.elasticsearch.ml.get_buckets.GetBucketsRequest
import zio.elasticsearch.ml.get_buckets.GetBucketsResponse
import zio.elasticsearch.ml.get_calendar_events.GetCalendarEventsRequest
import zio.elasticsearch.ml.get_calendar_events.GetCalendarEventsResponse
import zio.elasticsearch.ml.get_calendars.GetCalendarsRequest
import zio.elasticsearch.ml.get_calendars.GetCalendarsResponse
import zio.elasticsearch.ml.get_categories.GetCategoriesRequest
import zio.elasticsearch.ml.get_categories.GetCategoriesResponse
import zio.elasticsearch.ml.get_data_frame_analytics.GetDataFrameAnalyticsRequest
import zio.elasticsearch.ml.get_data_frame_analytics.GetDataFrameAnalyticsResponse
import zio.elasticsearch.ml.get_data_frame_analytics_stats.GetDataFrameAnalyticsStatsRequest
import zio.elasticsearch.ml.get_data_frame_analytics_stats.GetDataFrameAnalyticsStatsResponse
import zio.elasticsearch.ml.get_datafeed_stats.GetDatafeedStatsRequest
import zio.elasticsearch.ml.get_datafeed_stats.GetDatafeedStatsResponse
import zio.elasticsearch.ml.get_datafeeds.GetDatafeedsRequest
import zio.elasticsearch.ml.get_datafeeds.GetDatafeedsResponse
import zio.elasticsearch.ml.get_filters.GetFiltersRequest
import zio.elasticsearch.ml.get_filters.GetFiltersResponse
import zio.elasticsearch.ml.get_influencers.GetInfluencersRequest
import zio.elasticsearch.ml.get_influencers.GetInfluencersResponse
import zio.elasticsearch.ml.get_job_stats.GetJobStatsRequest
import zio.elasticsearch.ml.get_job_stats.GetJobStatsResponse
import zio.elasticsearch.ml.get_jobs.GetJobsRequest
import zio.elasticsearch.ml.get_jobs.GetJobsResponse
import zio.elasticsearch.ml.get_memory_stats.GetMemoryStatsRequest
import zio.elasticsearch.ml.get_memory_stats.GetMemoryStatsResponse
import zio.elasticsearch.ml.get_model_snapshot_upgrade_stats.GetModelSnapshotUpgradeStatsRequest
import zio.elasticsearch.ml.get_model_snapshot_upgrade_stats.GetModelSnapshotUpgradeStatsResponse
import zio.elasticsearch.ml.get_model_snapshots.GetModelSnapshotsRequest
import zio.elasticsearch.ml.get_model_snapshots.GetModelSnapshotsResponse
import zio.elasticsearch.ml.get_overall_buckets.GetOverallBucketsRequest
import zio.elasticsearch.ml.get_overall_buckets.GetOverallBucketsResponse
import zio.elasticsearch.ml.get_records.GetRecordsRequest
import zio.elasticsearch.ml.get_records.GetRecordsResponse
import zio.elasticsearch.ml.get_trained_models.GetTrainedModelsRequest
import zio.elasticsearch.ml.get_trained_models.GetTrainedModelsResponse
import zio.elasticsearch.ml.get_trained_models_stats.GetTrainedModelsStatsRequest
import zio.elasticsearch.ml.get_trained_models_stats.GetTrainedModelsStatsResponse
import zio.elasticsearch.ml.infer_trained_model.InferTrainedModelRequest
import zio.elasticsearch.ml.infer_trained_model.InferTrainedModelResponse
import zio.elasticsearch.ml.info.InfoRequest
import zio.elasticsearch.ml.info.InfoResponse
import zio.elasticsearch.ml.open_job.OpenJobRequest
import zio.elasticsearch.ml.open_job.OpenJobResponse
import zio.elasticsearch.ml.post_calendar_events.PostCalendarEventsRequest
import zio.elasticsearch.ml.post_calendar_events.PostCalendarEventsResponse
import zio.elasticsearch.ml.post_data.PostDataRequest
import zio.elasticsearch.ml.post_data.PostDataResponse
import zio.elasticsearch.ml.preview_data_frame_analytics.PreviewDataFrameAnalyticsRequest
import zio.elasticsearch.ml.preview_data_frame_analytics.PreviewDataFrameAnalyticsResponse
import zio.elasticsearch.ml.preview_datafeed.PreviewDatafeedRequest
import zio.elasticsearch.ml.preview_datafeed.PreviewDatafeedResponse
import zio.elasticsearch.ml.put_calendar.PutCalendarRequest
import zio.elasticsearch.ml.put_calendar.PutCalendarResponse
import zio.elasticsearch.ml.put_calendar_job.PutCalendarJobRequest
import zio.elasticsearch.ml.put_calendar_job.PutCalendarJobResponse
import zio.elasticsearch.ml.put_data_frame_analytics.PutDataFrameAnalyticsRequest
import zio.elasticsearch.ml.put_data_frame_analytics.PutDataFrameAnalyticsResponse
import zio.elasticsearch.ml.put_datafeed.PutDatafeedRequest
import zio.elasticsearch.ml.put_datafeed.PutDatafeedResponse
import zio.elasticsearch.ml.put_filter.PutFilterRequest
import zio.elasticsearch.ml.put_filter.PutFilterResponse
import zio.elasticsearch.ml.put_job.PutJobRequest
import zio.elasticsearch.ml.put_job.PutJobResponse
import zio.elasticsearch.ml.put_trained_model.PutTrainedModelRequest
import zio.elasticsearch.ml.put_trained_model.PutTrainedModelResponse
import zio.elasticsearch.ml.put_trained_model_alias.PutTrainedModelAliasRequest
import zio.elasticsearch.ml.put_trained_model_alias.PutTrainedModelAliasResponse
import zio.elasticsearch.ml.put_trained_model_definition_part.PutTrainedModelDefinitionPartRequest
import zio.elasticsearch.ml.put_trained_model_definition_part.PutTrainedModelDefinitionPartResponse
import zio.elasticsearch.ml.put_trained_model_vocabulary.PutTrainedModelVocabularyRequest
import zio.elasticsearch.ml.put_trained_model_vocabulary.PutTrainedModelVocabularyResponse
import zio.elasticsearch.ml.requests._
import zio.elasticsearch.ml.reset_job.ResetJobRequest
import zio.elasticsearch.ml.reset_job.ResetJobResponse
import zio.elasticsearch.ml.revert_model_snapshot.RevertModelSnapshotRequest
import zio.elasticsearch.ml.revert_model_snapshot.RevertModelSnapshotResponse
import zio.elasticsearch.ml.set_upgrade_mode.SetUpgradeModeRequest
import zio.elasticsearch.ml.set_upgrade_mode.SetUpgradeModeResponse
import zio.elasticsearch.ml.start_data_frame_analytics.StartDataFrameAnalyticsRequest
import zio.elasticsearch.ml.start_data_frame_analytics.StartDataFrameAnalyticsResponse
import zio.elasticsearch.ml.start_datafeed.StartDatafeedRequest
import zio.elasticsearch.ml.start_datafeed.StartDatafeedResponse
import zio.elasticsearch.ml.start_trained_model_deployment.StartTrainedModelDeploymentRequest
import zio.elasticsearch.ml.start_trained_model_deployment.StartTrainedModelDeploymentResponse
import zio.elasticsearch.ml.stop_data_frame_analytics.StopDataFrameAnalyticsRequest
import zio.elasticsearch.ml.stop_data_frame_analytics.StopDataFrameAnalyticsResponse
import zio.elasticsearch.ml.stop_datafeed.StopDatafeedRequest
import zio.elasticsearch.ml.stop_datafeed.StopDatafeedResponse
import zio.elasticsearch.ml.stop_trained_model_deployment.StopTrainedModelDeploymentRequest
import zio.elasticsearch.ml.stop_trained_model_deployment.StopTrainedModelDeploymentResponse
import zio.elasticsearch.ml.update_data_frame_analytics.UpdateDataFrameAnalyticsRequest
import zio.elasticsearch.ml.update_data_frame_analytics.UpdateDataFrameAnalyticsResponse
import zio.elasticsearch.ml.update_datafeed.UpdateDatafeedRequest
import zio.elasticsearch.ml.update_datafeed.UpdateDatafeedResponse
import zio.elasticsearch.ml.update_filter.UpdateFilterRequest
import zio.elasticsearch.ml.update_filter.UpdateFilterResponse
import zio.elasticsearch.ml.update_job.UpdateJobRequest
import zio.elasticsearch.ml.update_job.UpdateJobResponse
import zio.elasticsearch.ml.update_model_snapshot.UpdateModelSnapshotRequest
import zio.elasticsearch.ml.update_model_snapshot.UpdateModelSnapshotResponse
import zio.elasticsearch.ml.update_trained_model_deployment.UpdateTrainedModelDeploymentRequest
import zio.elasticsearch.ml.update_trained_model_deployment.UpdateTrainedModelDeploymentResponse
import zio.elasticsearch.ml.upgrade_job_snapshot.UpgradeJobSnapshotRequest
import zio.elasticsearch.ml.upgrade_job_snapshot.UpgradeJobSnapshotResponse
import zio.elasticsearch.ml.validate.ValidateRequest
import zio.elasticsearch.ml.validate.ValidateResponse
import zio.elasticsearch.ml.validate_detector.ValidateDetectorRequest
import zio.elasticsearch.ml.validate_detector.ValidateDetectorResponse

class MlManager(client: ElasticSearchClient) {

  /*
   * Clear the cached results from a trained model deployment
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/clear-trained-model-deployment-cache.html
   *
   * @param modelId The unique identifier of the trained model.
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def clearTrainedModelDeploymentCache(
    modelId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, ClearTrainedModelDeploymentCacheResponse] = {
    val request = ClearTrainedModelDeploymentCacheRequest(
      modelId = modelId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    clearTrainedModelDeploymentCache(request)

  }

  def clearTrainedModelDeploymentCache(
    request: ClearTrainedModelDeploymentCacheRequest
  ): ZIO[Any, FrameworkException, ClearTrainedModelDeploymentCacheResponse] =
    client.execute[Json, ClearTrainedModelDeploymentCacheResponse](request)

  /*
   * Closes one or more anomaly detection jobs. A job can be opened and closed multiple times throughout its lifecycle.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-close-job.html
   *
   * @param jobId The name of the job to close
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoMatch Whether to ignore if a wildcard expression matches no jobs. (This includes `_all` string or when no jobs have been specified)
   * @param body body the body of the call
   * @param force True if the job should be forcefully closed
   * @param timeout Controls the time to wait until a job has closed. Default to 30 minutes
   */
  def closeJob(
    jobId: String,
    body: CloseJobRequestBody = CloseJobRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoMatch: Option[Boolean] = None,
    force: Option[Boolean] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, CloseJobResponse] = {
    val request = CloseJobRequest(
      jobId = jobId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoMatch = allowNoMatch,
      body = body,
      force = force,
      timeout = timeout
    )

    closeJob(request)

  }

  def closeJob(request: CloseJobRequest): ZIO[Any, FrameworkException, CloseJobResponse] =
    client.execute[CloseJobRequestBody, CloseJobResponse](request)

  /*
   * Deletes a calendar.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-delete-calendar.html
   *
   * @param calendarId The ID of the calendar to delete
   * @param jobId An identifier for the anomaly detection jobs. It can be a job identifier, a group name, or a
   * comma-separated list of jobs or groups.

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def deleteCalendar(
    calendarId: String,
    jobId: Seq[String] = Nil,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, DeleteCalendarResponse] = {
    val request = DeleteCalendarRequest(
      calendarId = calendarId,
      jobId = jobId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    deleteCalendar(request)

  }

  def deleteCalendar(request: DeleteCalendarRequest): ZIO[Any, FrameworkException, DeleteCalendarResponse] =
    client.execute[Json, DeleteCalendarResponse](request)

  /*
   * Deletes scheduled events from a calendar.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-delete-calendar-event.html
   *
   * @param calendarId The ID of the calendar to modify
   * @param eventId The ID of the event to remove from the calendar
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def deleteCalendarEvent(
    calendarId: String,
    eventId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, DeleteCalendarEventResponse] = {
    val request = DeleteCalendarEventRequest(
      calendarId = calendarId,
      eventId = eventId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    deleteCalendarEvent(request)

  }

  def deleteCalendarEvent(
    request: DeleteCalendarEventRequest
  ): ZIO[Any, FrameworkException, DeleteCalendarEventResponse] =
    client.execute[Json, DeleteCalendarEventResponse](request)

  /*
   * Deletes anomaly detection jobs from a calendar.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-delete-calendar-job.html
   *
   * @param calendarId The ID of the calendar to modify
   * @param jobId The ID of the job to remove from the calendar
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def deleteCalendarJob(
    calendarId: String,
    jobId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, DeleteCalendarJobResponse] = {
    val request = DeleteCalendarJobRequest(
      calendarId = calendarId,
      jobId = jobId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    deleteCalendarJob(request)

  }

  def deleteCalendarJob(request: DeleteCalendarJobRequest): ZIO[Any, FrameworkException, DeleteCalendarJobResponse] =
    client.execute[Json, DeleteCalendarJobResponse](request)

  /*
   * Deletes an existing data frame analytics job.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/delete-dfanalytics.html
   *
   * @param id The ID of the data frame analytics to delete
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param force True if the job should be forcefully deleted
   * @param timeout Controls the time to wait until a job is deleted. Defaults to 1 minute
   */
  def deleteDataFrameAnalytics(
    id: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    force: Boolean = false,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, DeleteDataFrameAnalyticsResponse] = {
    val request = DeleteDataFrameAnalyticsRequest(
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      force = force,
      timeout = timeout
    )

    deleteDataFrameAnalytics(request)

  }

  def deleteDataFrameAnalytics(
    request: DeleteDataFrameAnalyticsRequest
  ): ZIO[Any, FrameworkException, DeleteDataFrameAnalyticsResponse] =
    client.execute[Json, DeleteDataFrameAnalyticsResponse](request)

  /*
   * Deletes an existing datafeed.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-delete-datafeed.html
   *
   * @param datafeedId The ID of the datafeed to delete
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param force True if the datafeed should be forcefully deleted
   */
  def deleteDatafeed(
    datafeedId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    force: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, DeleteDatafeedResponse] = {
    val request = DeleteDatafeedRequest(
      datafeedId = datafeedId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      force = force
    )

    deleteDatafeed(request)

  }

  def deleteDatafeed(request: DeleteDatafeedRequest): ZIO[Any, FrameworkException, DeleteDatafeedResponse] =
    client.execute[Json, DeleteDatafeedResponse](request)

  /*
   * Deletes expired and unused machine learning data.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-delete-expired-data.html
   *
   * @param jobId The ID of the job(s) to perform expired data hygiene for
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param requestsPerSecond The desired requests per second for the deletion processes.
   * @param timeout How long can the underlying delete processes run until they are canceled
   */
  def deleteExpiredData(
    jobId: String,
    body: DeleteExpiredDataRequestBody = DeleteExpiredDataRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    requestsPerSecond: Option[Double] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, DeleteExpiredDataResponse] = {
    val request = DeleteExpiredDataRequest(
      jobId = jobId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      requestsPerSecond = requestsPerSecond,
      timeout = timeout
    )

    deleteExpiredData(request)

  }

  def deleteExpiredData(request: DeleteExpiredDataRequest): ZIO[Any, FrameworkException, DeleteExpiredDataResponse] =
    client.execute[DeleteExpiredDataRequestBody, DeleteExpiredDataResponse](request)

  /*
   * Deletes a filter.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-delete-filter.html
   *
   * @param filterId The ID of the filter to delete
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def deleteFilter(
    filterId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, DeleteFilterResponse] = {
    val request = DeleteFilterRequest(
      filterId = filterId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    deleteFilter(request)

  }

  def deleteFilter(request: DeleteFilterRequest): ZIO[Any, FrameworkException, DeleteFilterResponse] =
    client.execute[Json, DeleteFilterResponse](request)

  /*
   * Deletes forecasts from a machine learning job.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-delete-forecast.html
   *
   * @param jobId The ID of the job from which to delete forecasts
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoForecasts Whether to ignore if `_all` matches no forecasts
   * @param forecastId The ID of the forecast to delete, can be comma delimited list. Leaving blank implies `_all`
   * @param timeout Controls the time to wait until the forecast(s) are deleted. Default to 30 seconds
   */
  def deleteForecast(
    jobId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoForecasts: Option[Boolean] = None,
    forecastId: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, DeleteForecastResponse] = {
    val request = DeleteForecastRequest(
      jobId = jobId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoForecasts = allowNoForecasts,
      forecastId = forecastId,
      timeout = timeout
    )

    deleteForecast(request)

  }

  def deleteForecast(request: DeleteForecastRequest): ZIO[Any, FrameworkException, DeleteForecastResponse] =
    client.execute[Json, DeleteForecastResponse](request)

  /*
   * Deletes an existing anomaly detection job.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-delete-job.html
   *
   * @param jobId The ID of the job to delete
   * @param deleteUserAnnotations Specifies whether annotations that have been added by the
   * user should be deleted along with any auto-generated annotations when the job is
   * reset.
   * @server_default false

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param force True if the job should be forcefully deleted
   * @param waitForCompletion Should this request wait until the operation has completed before returning
   */
  def deleteJob(
    jobId: String,
    deleteUserAnnotations: Boolean,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    force: Boolean = false,
    waitForCompletion: Boolean = true
  ): ZIO[Any, FrameworkException, DeleteJobResponse] = {
    val request = DeleteJobRequest(
      jobId = jobId,
      deleteUserAnnotations = deleteUserAnnotations,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      force = force,
      waitForCompletion = waitForCompletion
    )

    deleteJob(request)

  }

  def deleteJob(request: DeleteJobRequest): ZIO[Any, FrameworkException, DeleteJobResponse] =
    client.execute[Json, DeleteJobResponse](request)

  /*
   * Deletes an existing model snapshot.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-delete-snapshot.html
   *
   * @param jobId The ID of the job to fetch
   * @param snapshotId The ID of the snapshot to delete
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def deleteModelSnapshot(
    jobId: String,
    snapshotId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, DeleteModelSnapshotResponse] = {
    val request = DeleteModelSnapshotRequest(
      jobId = jobId,
      snapshotId = snapshotId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    deleteModelSnapshot(request)

  }

  def deleteModelSnapshot(
    request: DeleteModelSnapshotRequest
  ): ZIO[Any, FrameworkException, DeleteModelSnapshotResponse] =
    client.execute[Json, DeleteModelSnapshotResponse](request)

  /*
   * Deletes an existing trained inference model that is currently not referenced by an ingest pipeline.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/delete-trained-models.html
   *
   * @param modelId The ID of the trained model to delete
   * @param modelAlias The model alias to delete.

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param force True if the model should be forcefully deleted
   * @param timeout Controls the amount of time to wait for the model to be deleted.
   */
  def deleteTrainedModel(
    modelId: String,
    modelAlias: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    force: Option[Boolean] = None,
    timeout: String = "30s"
  ): ZIO[Any, FrameworkException, DeleteTrainedModelResponse] = {
    val request = DeleteTrainedModelRequest(
      modelId = modelId,
      modelAlias = modelAlias,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      force = force,
      timeout = timeout
    )

    deleteTrainedModel(request)

  }

  def deleteTrainedModel(request: DeleteTrainedModelRequest): ZIO[Any, FrameworkException, DeleteTrainedModelResponse] =
    client.execute[Json, DeleteTrainedModelResponse](request)

  /*
   * Deletes a model alias that refers to the trained model
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/delete-trained-models-aliases.html
   *
   * @param modelId The trained model where the model alias is assigned
   * @param modelAlias The trained model alias to delete
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def deleteTrainedModelAlias(
    modelId: String,
    modelAlias: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, DeleteTrainedModelAliasResponse] = {
    val request = DeleteTrainedModelAliasRequest(
      modelId = modelId,
      modelAlias = modelAlias,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    deleteTrainedModelAlias(request)

  }

  def deleteTrainedModelAlias(
    request: DeleteTrainedModelAliasRequest
  ): ZIO[Any, FrameworkException, DeleteTrainedModelAliasResponse] =
    client.execute[Json, DeleteTrainedModelAliasResponse](request)

  /*
   * Estimates the model memory
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-apis.html
   *
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def estimateModelMemory(
    body: EstimateModelMemoryRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, EstimateModelMemoryResponse] = {
    val request = EstimateModelMemoryRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    estimateModelMemory(request)

  }

  def estimateModelMemory(
    request: EstimateModelMemoryRequest
  ): ZIO[Any, FrameworkException, EstimateModelMemoryResponse] =
    client.execute[EstimateModelMemoryRequestBody, EstimateModelMemoryResponse](request)

  /*
   * Evaluates the data frame analytics for an annotated index.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/evaluate-dfanalytics.html
   *
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def evaluateDataFrame(
    body: EvaluateDataFrameRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, EvaluateDataFrameResponse] = {
    val request = EvaluateDataFrameRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    evaluateDataFrame(request)

  }

  def evaluateDataFrame(request: EvaluateDataFrameRequest): ZIO[Any, FrameworkException, EvaluateDataFrameResponse] =
    client.execute[EvaluateDataFrameRequestBody, EvaluateDataFrameResponse](request)

  /*
   * Explains a data frame analytics config.
   * For more info refers to http://www.elastic.co/guide/en/elasticsearch/reference/current/explain-dfanalytics.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param id The ID of the data frame analytics to explain
   */
  def explainDataFrameAnalytics(
    body: ExplainDataFrameAnalyticsRequestBody = ExplainDataFrameAnalyticsRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    id: Option[String] = None
  ): ZIO[Any, FrameworkException, ExplainDataFrameAnalyticsResponse] = {
    val request = ExplainDataFrameAnalyticsRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      id = id
    )

    explainDataFrameAnalytics(request)

  }

  def explainDataFrameAnalytics(
    request: ExplainDataFrameAnalyticsRequest
  ): ZIO[Any, FrameworkException, ExplainDataFrameAnalyticsResponse] =
    client.execute[ExplainDataFrameAnalyticsRequestBody, ExplainDataFrameAnalyticsResponse](request)

  /*
   * Forces any buffered data to be processed by the job.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-flush-job.html
   *
   * @param jobId The name of the job to flush
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param advanceTime Advances time to the given value generating results and updating the model for the advanced interval
   * @param body body the body of the call
   * @param calcInterim Calculates interim results for the most recent bucket or all buckets within the latency period
   * @param end When used in conjunction with calc_interim, specifies the range of buckets on which to calculate interim results
   * @param skipTime Skips time to the given value without generating results or updating the model for the skipped interval
   * @param start When used in conjunction with calc_interim, specifies the range of buckets on which to calculate interim results
   */
  def flushJob(
    jobId: String,
    body: FlushJobRequestBody = FlushJobRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    advanceTime: Option[String] = None,
    calcInterim: Option[Boolean] = None,
    end: Option[String] = None,
    skipTime: Option[String] = None,
    start: Option[String] = None
  ): ZIO[Any, FrameworkException, FlushJobResponse] = {
    val request = FlushJobRequest(
      jobId = jobId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      advanceTime = advanceTime,
      body = body,
      calcInterim = calcInterim,
      end = end,
      skipTime = skipTime,
      start = start
    )

    flushJob(request)

  }

  def flushJob(request: FlushJobRequest): ZIO[Any, FrameworkException, FlushJobResponse] =
    client.execute[FlushJobRequestBody, FlushJobResponse](request)

  /*
   * Predicts the future behavior of a time series by using its historical behavior.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-forecast.html
   *
   * @param jobId The ID of the job to forecast for
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param duration The duration of the forecast
   * @param expiresIn The time interval after which the forecast expires. Expired forecasts will be deleted at the first opportunity.
   * @param maxModelMemory The max memory able to be used by the forecast. Default is 20mb.
   */
  def forecast(
    jobId: String,
    body: ForecastRequestBody = ForecastRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    duration: Option[String] = None,
    expiresIn: Option[String] = None,
    maxModelMemory: Option[String] = None
  ): ZIO[Any, FrameworkException, ForecastResponse] = {
    val request = ForecastRequest(
      jobId = jobId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      duration = duration,
      expiresIn = expiresIn,
      maxModelMemory = maxModelMemory
    )

    forecast(request)

  }

  def forecast(request: ForecastRequest): ZIO[Any, FrameworkException, ForecastResponse] =
    client.execute[ForecastRequestBody, ForecastResponse](request)

  /*
   * Retrieves anomaly detection job results for one or more buckets.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-get-bucket.html
   *
   * @param jobId ID of the job to get bucket results from
   * @param timestamp The timestamp of the desired single bucket result
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param anomalyScore Filter for the most anomalous buckets
   * @param body body the body of the call
   * @param desc Set the sort direction
   * @param end End time filter for buckets
   * @param excludeInterim Exclude interim results
   * @param expand Include anomaly records
   * @param from skips a number of buckets
   * @param size specifies a max number of buckets to get
   * @param sort Sort buckets by a particular field
   * @param start Start time filter for buckets
   */
  def getBuckets(
    jobId: String,
    body: GetBucketsRequestBody = GetBucketsRequestBody(),
    timestamp: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    anomalyScore: Option[Double] = None,
    desc: Option[Boolean] = None,
    end: Option[String] = None,
    excludeInterim: Option[Boolean] = None,
    expand: Option[Boolean] = None,
    from: Option[Int] = None,
    size: Option[Int] = None,
    sort: Option[String] = None,
    start: Option[String] = None
  ): ZIO[Any, FrameworkException, GetBucketsResponse] = {
    val request = GetBucketsRequest(
      jobId = jobId,
      timestamp = timestamp,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      anomalyScore = anomalyScore,
      body = body,
      desc = desc,
      end = end,
      excludeInterim = excludeInterim,
      expand = expand,
      from = from,
      size = size,
      sort = sort,
      start = start
    )

    getBuckets(request)

  }

  def getBuckets(request: GetBucketsRequest): ZIO[Any, FrameworkException, GetBucketsResponse] =
    client.execute[GetBucketsRequestBody, GetBucketsResponse](request)

  /*
   * Retrieves information about the scheduled events in calendars.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-get-calendar-event.html
   *
   * @param calendarId The ID of the calendar containing the events
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param end Get events before this time
   * @param from Skips a number of events
   * @param jobId Get events for the job. When this option is used calendar_id must be '_all'
   * @param size Specifies a max number of events to get
   * @param start Get events after this time
   */
  def getCalendarEvents(
    calendarId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    end: Option[java.time.LocalDate] = None,
    from: Option[Int] = None,
    jobId: Option[String] = None,
    size: Option[Int] = None,
    start: Option[String] = None
  ): ZIO[Any, FrameworkException, GetCalendarEventsResponse] = {
    val request = GetCalendarEventsRequest(
      calendarId = calendarId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      end = end,
      from = from,
      jobId = jobId,
      size = size,
      start = start
    )

    getCalendarEvents(request)

  }

  def getCalendarEvents(request: GetCalendarEventsRequest): ZIO[Any, FrameworkException, GetCalendarEventsResponse] =
    client.execute[Json, GetCalendarEventsResponse](request)

  /*
   * Retrieves configuration information for calendars.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-get-calendar.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param calendarId The ID of the calendar to fetch
   * @param from skips a number of calendars
   * @param size specifies a max number of calendars to get
   */
  def getCalendars(
    body: GetCalendarsRequestBody = GetCalendarsRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    calendarId: Option[String] = None,
    from: Option[Int] = None,
    size: Option[Int] = None
  ): ZIO[Any, FrameworkException, GetCalendarsResponse] = {
    val request = GetCalendarsRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      calendarId = calendarId,
      from = from,
      size = size
    )

    getCalendars(request)

  }

  def getCalendars(request: GetCalendarsRequest): ZIO[Any, FrameworkException, GetCalendarsResponse] =
    client.execute[GetCalendarsRequestBody, GetCalendarsResponse](request)

  /*
   * Retrieves anomaly detection job results for one or more categories.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-get-category.html
   *
   * @param jobId The name of the job
   * @param categoryId The identifier of the category definition of interest
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param from skips a number of categories
   * @param partitionFieldValue Specifies the partition to retrieve categories for. This is optional, and should never be used for jobs where per-partition categorization is disabled.
   * @param size specifies a max number of categories to get
   */
  def getCategories(
    jobId: String,
    categoryId: String,
    body: GetCategoriesRequestBody = GetCategoriesRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    from: Option[Int] = None,
    partitionFieldValue: Option[String] = None,
    size: Option[Int] = None
  ): ZIO[Any, FrameworkException, GetCategoriesResponse] = {
    val request = GetCategoriesRequest(
      jobId = jobId,
      categoryId = categoryId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      from = from,
      partitionFieldValue = partitionFieldValue,
      size = size
    )

    getCategories(request)

  }

  def getCategories(request: GetCategoriesRequest): ZIO[Any, FrameworkException, GetCategoriesResponse] =
    client.execute[GetCategoriesRequestBody, GetCategoriesResponse](request)

  /*
   * Retrieves configuration information for data frame analytics jobs.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/get-dfanalytics.html
   *
   * @param id The ID of the data frame analytics to fetch
   * @param verbose Defines whether the stats response should be verbose.
   * @server_default false

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoMatch Whether to ignore if a wildcard expression matches no data frame analytics. (This includes `_all` string or when no data frame analytics have been specified)
   * @param excludeGenerated Omits fields that are illegal to set on data frame analytics PUT
   * @param from skips a number of analytics
   * @param size specifies a max number of analytics to get
   */
  def getDataFrameAnalytics(
    id: String,
    verbose: Boolean,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoMatch: Boolean = true,
    excludeGenerated: Boolean = false,
    from: Int = 0,
    size: Int = 100
  ): ZIO[Any, FrameworkException, GetDataFrameAnalyticsResponse] = {
    val request = GetDataFrameAnalyticsRequest(
      id = id,
      verbose = verbose,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoMatch = allowNoMatch,
      excludeGenerated = excludeGenerated,
      from = from,
      size = size
    )

    getDataFrameAnalytics(request)

  }

  def getDataFrameAnalytics(
    request: GetDataFrameAnalyticsRequest
  ): ZIO[Any, FrameworkException, GetDataFrameAnalyticsResponse] =
    client.execute[Json, GetDataFrameAnalyticsResponse](request)

  /*
   * Retrieves usage information for data frame analytics jobs.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/get-dfanalytics-stats.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoMatch Whether to ignore if a wildcard expression matches no data frame analytics. (This includes `_all` string or when no data frame analytics have been specified)
   * @param from skips a number of analytics
   * @param id The ID of the data frame analytics stats to fetch
   * @param size specifies a max number of analytics to get
   * @param verbose whether the stats response should be verbose
   */
  def getDataFrameAnalyticsStats(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoMatch: Boolean = true,
    from: Int = 0,
    id: Option[String] = None,
    size: Int = 100,
    verbose: Boolean = false
  ): ZIO[Any, FrameworkException, GetDataFrameAnalyticsStatsResponse] = {
    val request = GetDataFrameAnalyticsStatsRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoMatch = allowNoMatch,
      from = from,
      id = id,
      size = size,
      verbose = verbose
    )

    getDataFrameAnalyticsStats(request)

  }

  def getDataFrameAnalyticsStats(
    request: GetDataFrameAnalyticsStatsRequest
  ): ZIO[Any, FrameworkException, GetDataFrameAnalyticsStatsResponse] =
    client.execute[Json, GetDataFrameAnalyticsStatsResponse](request)

  /*
   * Retrieves usage information for datafeeds.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-get-datafeed-stats.html
   *
   * @param datafeedId The ID of the datafeeds stats to fetch
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoMatch Whether to ignore if a wildcard expression matches no datafeeds. (This includes `_all` string or when no datafeeds have been specified)
   */
  def getDatafeedStats(
    datafeedId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoMatch: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, GetDatafeedStatsResponse] = {
    val request = GetDatafeedStatsRequest(
      datafeedId = datafeedId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoMatch = allowNoMatch
    )

    getDatafeedStats(request)

  }

  def getDatafeedStats(request: GetDatafeedStatsRequest): ZIO[Any, FrameworkException, GetDatafeedStatsResponse] =
    client.execute[Json, GetDatafeedStatsResponse](request)

  /*
   * Retrieves configuration information for datafeeds.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-get-datafeed.html
   *
   * @param datafeedId The ID of the datafeeds to fetch
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoMatch Whether to ignore if a wildcard expression matches no datafeeds. (This includes `_all` string or when no datafeeds have been specified)
   * @param excludeGenerated Omits fields that are illegal to set on datafeed PUT
   */
  def getDatafeeds(
    datafeedId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoMatch: Option[Boolean] = None,
    excludeGenerated: Boolean = false
  ): ZIO[Any, FrameworkException, GetDatafeedsResponse] = {
    val request = GetDatafeedsRequest(
      datafeedId = datafeedId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoMatch = allowNoMatch,
      excludeGenerated = excludeGenerated
    )

    getDatafeeds(request)

  }

  def getDatafeeds(request: GetDatafeedsRequest): ZIO[Any, FrameworkException, GetDatafeedsResponse] =
    client.execute[Json, GetDatafeedsResponse](request)

  /*
   * Retrieves filters.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-get-filter.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param filterId The ID of the filter to fetch
   * @param from skips a number of filters
   * @param size specifies a max number of filters to get
   */
  def getFilters(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    filterId: Option[String] = None,
    from: Option[Int] = None,
    size: Option[Int] = None
  ): ZIO[Any, FrameworkException, GetFiltersResponse] = {
    val request = GetFiltersRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      filterId = filterId,
      from = from,
      size = size
    )

    getFilters(request)

  }

  def getFilters(request: GetFiltersRequest): ZIO[Any, FrameworkException, GetFiltersResponse] =
    client.execute[Json, GetFiltersResponse](request)

  /*
   * Retrieves anomaly detection job results for one or more influencers.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-get-influencer.html
   *
   * @param jobId Identifier for the anomaly detection job
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param desc whether the results should be sorted in decending order
   * @param end end timestamp for the requested influencers
   * @param excludeInterim Exclude interim results
   * @param from skips a number of influencers
   * @param influencerScore influencer score threshold for the requested influencers
   * @param size specifies a max number of influencers to get
   * @param sort sort field for the requested influencers
   * @param start start timestamp for the requested influencers
   */
  def getInfluencers(
    jobId: String,
    body: GetInfluencersRequestBody = GetInfluencersRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    desc: Option[Boolean] = None,
    end: Option[String] = None,
    excludeInterim: Option[Boolean] = None,
    from: Option[Int] = None,
    influencerScore: Option[Double] = None,
    size: Option[Int] = None,
    sort: Option[String] = None,
    start: Option[String] = None
  ): ZIO[Any, FrameworkException, GetInfluencersResponse] = {
    val request = GetInfluencersRequest(
      jobId = jobId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      desc = desc,
      end = end,
      excludeInterim = excludeInterim,
      from = from,
      influencerScore = influencerScore,
      size = size,
      sort = sort,
      start = start
    )

    getInfluencers(request)

  }

  def getInfluencers(request: GetInfluencersRequest): ZIO[Any, FrameworkException, GetInfluencersResponse] =
    client.execute[GetInfluencersRequestBody, GetInfluencersResponse](request)

  /*
   * Retrieves usage information for anomaly detection jobs.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-get-job-stats.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoMatch Whether to ignore if a wildcard expression matches no jobs. (This includes `_all` string or when no jobs have been specified)
   * @param jobId The ID of the jobs stats to fetch
   */
  def getJobStats(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoMatch: Option[Boolean] = None,
    jobId: Option[String] = None
  ): ZIO[Any, FrameworkException, GetJobStatsResponse] = {
    val request = GetJobStatsRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoMatch = allowNoMatch,
      jobId = jobId
    )

    getJobStats(request)

  }

  def getJobStats(request: GetJobStatsRequest): ZIO[Any, FrameworkException, GetJobStatsResponse] =
    client.execute[Json, GetJobStatsResponse](request)

  /*
   * Retrieves configuration information for anomaly detection jobs.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-get-job.html
   *
   * @param jobId The ID of the jobs to fetch
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoMatch Whether to ignore if a wildcard expression matches no jobs. (This includes `_all` string or when no jobs have been specified)
   * @param excludeGenerated Omits fields that are illegal to set on job PUT
   */
  def getJobs(
    jobId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoMatch: Option[Boolean] = None,
    excludeGenerated: Boolean = false
  ): ZIO[Any, FrameworkException, GetJobsResponse] = {
    val request = GetJobsRequest(
      jobId = jobId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoMatch = allowNoMatch,
      excludeGenerated = excludeGenerated
    )

    getJobs(request)

  }

  def getJobs(request: GetJobsRequest): ZIO[Any, FrameworkException, GetJobsResponse] =
    client.execute[Json, GetJobsResponse](request)

  /*
   * Returns information on how ML is using memory.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/get-ml-memory.html
   *
   * @param human Specify this query parameter to include the fields with units in the response. Otherwise only
   * the `_in_bytes` sizes are returned in the response.

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param masterTimeout Explicit operation timeout for connection to master node
   * @param nodeId Specifies the node or nodes to retrieve stats for.
   * @param timeout Explicit operation timeout
   */
  def getMemoryStats(
    human: Boolean = false,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    pretty: Boolean = false,
    masterTimeout: Option[String] = None,
    nodeId: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, GetMemoryStatsResponse] = {
    val request = GetMemoryStatsRequest(
      human = human,
      errorTrace = errorTrace,
      filterPath = filterPath,
      pretty = pretty,
      masterTimeout = masterTimeout,
      nodeId = nodeId,
      timeout = timeout
    )

    getMemoryStats(request)

  }

  def getMemoryStats(request: GetMemoryStatsRequest): ZIO[Any, FrameworkException, GetMemoryStatsResponse] =
    client.execute[Json, GetMemoryStatsResponse](request)

  /*
   * Gets stats for anomaly detection job model snapshot upgrades that are in progress.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-get-job-model-snapshot-upgrade-stats.html
   *
   * @param jobId The ID of the job. May be a wildcard, comma separated list or `_all`.
   * @param snapshotId The ID of the snapshot. May be a wildcard, comma separated list or `_all`.
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoMatch Whether to ignore if a wildcard expression matches no jobs or no snapshots. (This includes the `_all` string.)
   */
  def getModelSnapshotUpgradeStats(
    jobId: String,
    snapshotId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoMatch: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, GetModelSnapshotUpgradeStatsResponse] = {
    val request = GetModelSnapshotUpgradeStatsRequest(
      jobId = jobId,
      snapshotId = snapshotId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoMatch = allowNoMatch
    )

    getModelSnapshotUpgradeStats(request)

  }

  def getModelSnapshotUpgradeStats(
    request: GetModelSnapshotUpgradeStatsRequest
  ): ZIO[Any, FrameworkException, GetModelSnapshotUpgradeStatsResponse] =
    client.execute[Json, GetModelSnapshotUpgradeStatsResponse](request)

  /*
   * Retrieves information about model snapshots.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-get-snapshot.html
   *
   * @param jobId The ID of the job to fetch
   * @param snapshotId The ID of the snapshot to fetch
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param desc True if the results should be sorted in descending order
   * @param end The filter 'end' query parameter
   * @param from Skips a number of documents
   * @param size The default number of documents returned in queries as a string.
   * @param sort Name of the field to sort on
   * @param start The filter 'start' query parameter
   */
  def getModelSnapshots(
    jobId: String,
    snapshotId: String,
    body: GetModelSnapshotsRequestBody = GetModelSnapshotsRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    desc: Option[Boolean] = None,
    end: Option[java.time.LocalDate] = None,
    from: Option[Int] = None,
    size: Option[Int] = None,
    sort: Option[String] = None,
    start: Option[java.time.LocalDate] = None
  ): ZIO[Any, FrameworkException, GetModelSnapshotsResponse] = {
    val request = GetModelSnapshotsRequest(
      jobId = jobId,
      snapshotId = snapshotId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      desc = desc,
      end = end,
      from = from,
      size = size,
      sort = sort,
      start = start
    )

    getModelSnapshots(request)

  }

  def getModelSnapshots(request: GetModelSnapshotsRequest): ZIO[Any, FrameworkException, GetModelSnapshotsResponse] =
    client.execute[GetModelSnapshotsRequestBody, GetModelSnapshotsResponse](request)

  /*
   * Retrieves overall bucket results that summarize the bucket results of multiple anomaly detection jobs.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-get-overall-buckets.html
   *
   * @param jobId The job IDs for which to calculate overall bucket results
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoMatch Whether to ignore if a wildcard expression matches no jobs. (This includes `_all` string or when no jobs have been specified)
   * @param body body the body of the call
   * @param bucketSpan The span of the overall buckets. Defaults to the longest job bucket_span
   * @param end Returns overall buckets with timestamps earlier than this time
   * @param excludeInterim If true overall buckets that include interim buckets will be excluded
   * @param overallScore Returns overall buckets with overall scores higher than this value
   * @param start Returns overall buckets with timestamps after this time
   * @param topN The number of top job bucket scores to be used in the overall_score calculation
   */
  def getOverallBuckets(
    jobId: String,
    body: GetOverallBucketsRequestBody = GetOverallBucketsRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoMatch: Option[Boolean] = None,
    bucketSpan: Option[String] = None,
    end: Option[String] = None,
    excludeInterim: Option[Boolean] = None,
    overallScore: Option[Double] = None,
    start: Option[String] = None,
    topN: Option[Int] = None
  ): ZIO[Any, FrameworkException, GetOverallBucketsResponse] = {
    val request = GetOverallBucketsRequest(
      jobId = jobId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoMatch = allowNoMatch,
      body = body,
      bucketSpan = bucketSpan,
      end = end,
      excludeInterim = excludeInterim,
      overallScore = overallScore,
      start = start,
      topN = topN
    )

    getOverallBuckets(request)

  }

  def getOverallBuckets(request: GetOverallBucketsRequest): ZIO[Any, FrameworkException, GetOverallBucketsResponse] =
    client.execute[GetOverallBucketsRequestBody, GetOverallBucketsResponse](request)

  /*
   * Retrieves anomaly records for an anomaly detection job.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-get-record.html
   *
   * @param jobId The ID of the job
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param desc Set the sort direction
   * @param end End time filter for records
   * @param excludeInterim Exclude interim results
   * @param from skips a number of records
   * @param recordScore Returns records with anomaly scores greater or equal than this value
   * @param size specifies a max number of records to get
   * @param sort Sort records by a particular field
   * @param start Start time filter for records
   */
  def getRecords(
    jobId: String,
    body: GetRecordsRequestBody = GetRecordsRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    desc: Option[Boolean] = None,
    end: Option[String] = None,
    excludeInterim: Option[Boolean] = None,
    from: Option[Int] = None,
    recordScore: Option[Double] = None,
    size: Option[Int] = None,
    sort: Option[String] = None,
    start: Option[String] = None
  ): ZIO[Any, FrameworkException, GetRecordsResponse] = {
    val request = GetRecordsRequest(
      jobId = jobId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      desc = desc,
      end = end,
      excludeInterim = excludeInterim,
      from = from,
      recordScore = recordScore,
      size = size,
      sort = sort,
      start = start
    )

    getRecords(request)

  }

  def getRecords(request: GetRecordsRequest): ZIO[Any, FrameworkException, GetRecordsResponse] =
    client.execute[GetRecordsRequestBody, GetRecordsResponse](request)

  /*
   * Retrieves configuration information for a trained inference model.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/get-trained-models.html
   *
   * @param modelId The ID of the trained models to fetch
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoMatch Whether to ignore if a wildcard expression matches no trained models. (This includes `_all` string or when no trained models have been specified)
   * @param decompressDefinition Should the model definition be decompressed into valid JSON or returned in a custom compressed format. Defaults to true.
   * @param excludeGenerated Omits fields that are illegal to set on model PUT
   * @param from skips a number of trained models
   * @param include A comma-separate list of fields to optionally include. Valid options are 'definition' and 'total_feature_importance'. Default is none.
   * @param includeModelDefinition Should the full model definition be included in the results. These definitions can be large. So be cautious when including them. Defaults to false.
   * @param size specifies a max number of trained models to get
   * @param tags A comma-separated list of tags that the model must have.
   */
  def getTrainedModels(
    modelId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoMatch: Boolean = true,
    decompressDefinition: Boolean = true,
    excludeGenerated: Boolean = false,
    from: Int = 0,
    include: Option[String] = None,
    includeModelDefinition: Boolean = false,
    size: Int = 100,
    tags: Seq[String] = Nil
  ): ZIO[Any, FrameworkException, GetTrainedModelsResponse] = {
    val request = GetTrainedModelsRequest(
      modelId = modelId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoMatch = allowNoMatch,
      decompressDefinition = decompressDefinition,
      excludeGenerated = excludeGenerated,
      from = from,
      include = include,
      includeModelDefinition = includeModelDefinition,
      size = size,
      tags = tags
    )

    getTrainedModels(request)

  }

  def getTrainedModels(request: GetTrainedModelsRequest): ZIO[Any, FrameworkException, GetTrainedModelsResponse] =
    client.execute[Json, GetTrainedModelsResponse](request)

  /*
   * Retrieves usage information for trained inference models.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/get-trained-models-stats.html
   *
   * @param modelId The ID of the trained models stats to fetch
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoMatch Whether to ignore if a wildcard expression matches no trained models. (This includes `_all` string or when no trained models have been specified)
   * @param from skips a number of trained models
   * @param size specifies a max number of trained models to get
   */
  def getTrainedModelsStats(
    modelId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoMatch: Boolean = true,
    from: Int = 0,
    size: Int = 100
  ): ZIO[Any, FrameworkException, GetTrainedModelsStatsResponse] = {
    val request = GetTrainedModelsStatsRequest(
      modelId = modelId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoMatch = allowNoMatch,
      from = from,
      size = size
    )

    getTrainedModelsStats(request)

  }

  def getTrainedModelsStats(
    request: GetTrainedModelsStatsRequest
  ): ZIO[Any, FrameworkException, GetTrainedModelsStatsResponse] =
    client.execute[Json, GetTrainedModelsStatsResponse](request)

  /*
   * Evaluate a trained model.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/infer-trained-model.html
   *
   * @param modelId The unique identifier of the trained model.
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param timeout Controls the amount of time to wait for inference results.
   */
  def inferTrainedModel(
    modelId: String,
    body: InferTrainedModelRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    timeout: String = "10s"
  ): ZIO[Any, FrameworkException, InferTrainedModelResponse] = {
    val request = InferTrainedModelRequest(
      modelId = modelId,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      timeout = timeout
    )

    inferTrainedModel(request)

  }

  def inferTrainedModel(request: InferTrainedModelRequest): ZIO[Any, FrameworkException, InferTrainedModelResponse] =
    client.execute[InferTrainedModelRequestBody, InferTrainedModelResponse](request)

  /*
   * Returns defaults and limits used by machine learning.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/get-ml-info.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def info(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, InfoResponse] = {
    val request = InfoRequest(errorTrace = errorTrace, filterPath = filterPath, human = human, pretty = pretty)

    info(request)

  }

  def info(request: InfoRequest): ZIO[Any, FrameworkException, InfoResponse] =
    client.execute[Json, InfoResponse](request)

  /*
   * Opens one or more anomaly detection jobs.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-open-job.html
   *
   * @param jobId The ID of the job to open
   * @param timeout Controls the time to wait until a job has opened.
   * @server_default 30m

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   */
  def openJob(
    jobId: String,
    body: OpenJobRequestBody = OpenJobRequestBody(),
    timeout: Option[String] = None,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, OpenJobResponse] = {
    val request = OpenJobRequest(
      jobId = jobId,
      timeout = timeout,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body
    )

    openJob(request)

  }

  def openJob(request: OpenJobRequest): ZIO[Any, FrameworkException, OpenJobResponse] =
    client.execute[OpenJobRequestBody, OpenJobResponse](request)

  /*
   * Posts scheduled events in a calendar.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-post-calendar-event.html
   *
   * @param calendarId The ID of the calendar to modify
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def postCalendarEvents(
    calendarId: String,
    body: PostCalendarEventsRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, PostCalendarEventsResponse] = {
    val request = PostCalendarEventsRequest(
      calendarId = calendarId,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    postCalendarEvents(request)

  }

  def postCalendarEvents(request: PostCalendarEventsRequest): ZIO[Any, FrameworkException, PostCalendarEventsResponse] =
    client.execute[PostCalendarEventsRequestBody, PostCalendarEventsResponse](request)

  /*
   * Sends data to an anomaly detection job for analysis.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-post-data.html
   *
   * @param jobId The name of the job receiving the data
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param resetEnd Optional parameter to specify the end of the bucket resetting range
   * @param resetStart Optional parameter to specify the start of the bucket resetting range
   */
  def postData(
    jobId: String,
    body: Chunk[String],
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    resetEnd: Option[String] = None,
    resetStart: Option[String] = None
  ): ZIO[Any, FrameworkException, PostDataResponse] = {
    val request = PostDataRequest(
      jobId = jobId,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      resetEnd = resetEnd,
      resetStart = resetStart
    )

    postData(request)

  }

  def postData(request: PostDataRequest): ZIO[Any, FrameworkException, PostDataResponse] =
    client.execute[Chunk[String], PostDataResponse](request)

  /*
   * Previews that will be analyzed given a data frame analytics config.
   * For more info refers to http://www.elastic.co/guide/en/elasticsearch/reference/current/preview-dfanalytics.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param id The ID of the data frame analytics to preview
   */
  def previewDataFrameAnalytics(
    id: Option[String] = None,
    body: PreviewDataFrameAnalyticsRequestBody = PreviewDataFrameAnalyticsRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false
  ): ZIO[Any, FrameworkException, PreviewDataFrameAnalyticsResponse] = {
    val request = PreviewDataFrameAnalyticsRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      id = id
    )

    previewDataFrameAnalytics(request)

  }

  def previewDataFrameAnalytics(
    request: PreviewDataFrameAnalyticsRequest
  ): ZIO[Any, FrameworkException, PreviewDataFrameAnalyticsResponse] =
    client.execute[PreviewDataFrameAnalyticsRequestBody, PreviewDataFrameAnalyticsResponse](request)

  /*
   * Previews a datafeed.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-preview-datafeed.html
   *
   * @param datafeedId The ID of the datafeed to preview
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param end The end time when the datafeed preview should stop
   * @param start The start time from where the datafeed preview should begin
   */
  def previewDatafeed(
    datafeedId: String,
    body: PreviewDatafeedRequestBody = PreviewDatafeedRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    end: Option[String] = None,
    start: Option[String] = None
  ): ZIO[Any, FrameworkException, PreviewDatafeedResponse] = {
    val request = PreviewDatafeedRequest(
      datafeedId = datafeedId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      end = end,
      start = start
    )

    previewDatafeed(request)

  }

  def previewDatafeed(request: PreviewDatafeedRequest): ZIO[Any, FrameworkException, PreviewDatafeedResponse] =
    client.execute[PreviewDatafeedRequestBody, PreviewDatafeedResponse](request)

  /*
   * Instantiates a calendar.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-put-calendar.html
   *
   * @param calendarId The ID of the calendar to create
   * @param jobId An identifier for the anomaly detection jobs. It can be a job identifier, a group name, or a comma-separated list of jobs or groups.

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   */
  def putCalendar(
    calendarId: String,
    jobId: Seq[String] = Nil,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    body: Json = Json.Null
  ): ZIO[Any, FrameworkException, PutCalendarResponse] = {
    val request = PutCalendarRequest(
      calendarId = calendarId,
      jobId = jobId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body
    )

    putCalendar(request)

  }

  def putCalendar(request: PutCalendarRequest): ZIO[Any, FrameworkException, PutCalendarResponse] =
    client.execute[Json, PutCalendarResponse](request)

  /*
   * Adds an anomaly detection job to a calendar.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-put-calendar-job.html
   *
   * @param calendarId The ID of the calendar to modify
   * @param jobId The ID of the job to add to the calendar
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def putCalendarJob(
    calendarId: String,
    jobId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, PutCalendarJobResponse] = {
    val request = PutCalendarJobRequest(
      calendarId = calendarId,
      jobId = jobId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    putCalendarJob(request)

  }

  def putCalendarJob(request: PutCalendarJobRequest): ZIO[Any, FrameworkException, PutCalendarJobResponse] =
    client.execute[Json, PutCalendarJobResponse](request)

  /*
   * Instantiates a data frame analytics job.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/put-dfanalytics.html
   *
   * @param id The ID of the data frame analytics to create
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def putDataFrameAnalytics(
    id: String,
    body: PutDataFrameAnalyticsRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, PutDataFrameAnalyticsResponse] = {
    val request = PutDataFrameAnalyticsRequest(
      id = id,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    putDataFrameAnalytics(request)

  }

  def putDataFrameAnalytics(
    request: PutDataFrameAnalyticsRequest
  ): ZIO[Any, FrameworkException, PutDataFrameAnalyticsResponse] =
    client.execute[PutDataFrameAnalyticsRequestBody, PutDataFrameAnalyticsResponse](request)

  /*
   * Instantiates a datafeed.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-put-datafeed.html
   *
   * @param datafeedId The ID of the datafeed to create
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Ignore if the source indices expressions resolves to no concrete indices (default: true)
   * @param expandWildcards Whether source index expressions should get expanded to open or closed indices (default: open)
   * @param ignoreThrottled Ignore indices that are marked as throttled (default: true)
   * @param ignoreUnavailable Ignore unavailable indexes (default: false)
   */
  def putDatafeed(
    datafeedId: String,
    body: PutDatafeedRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreThrottled: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, PutDatafeedResponse] = {
    val request = PutDatafeedRequest(
      datafeedId = datafeedId,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      ignoreThrottled = ignoreThrottled,
      ignoreUnavailable = ignoreUnavailable
    )

    putDatafeed(request)

  }

  def putDatafeed(request: PutDatafeedRequest): ZIO[Any, FrameworkException, PutDatafeedResponse] =
    client.execute[PutDatafeedRequestBody, PutDatafeedResponse](request)

  /*
   * Instantiates a filter.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-put-filter.html
   *
   * @param filterId The ID of the filter to create
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def putFilter(
    filterId: String,
    body: PutFilterRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, PutFilterResponse] = {
    val request = PutFilterRequest(
      filterId = filterId,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    putFilter(request)

  }

  def putFilter(request: PutFilterRequest): ZIO[Any, FrameworkException, PutFilterResponse] =
    client.execute[PutFilterRequestBody, PutFilterResponse](request)

  /*
   * Instantiates an anomaly detection job.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-put-job.html
   *
   * @param jobId The ID of the job to create
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Ignore if the source indices expressions resolves to no concrete indices (default: true). Only set if datafeed_config is provided.
   * @param expandWildcards Whether source index expressions should get expanded to open or closed indices (default: open). Only set if datafeed_config is provided.
   * @param ignoreThrottled Ignore indices that are marked as throttled (default: true). Only set if datafeed_config is provided.
   * @param ignoreUnavailable Ignore unavailable indexes (default: false). Only set if datafeed_config is provided.
   */
  def putJob(
    jobId: String,
    body: PutJobRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreThrottled: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, PutJobResponse] = {
    val request = PutJobRequest(
      jobId = jobId,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      ignoreThrottled = ignoreThrottled,
      ignoreUnavailable = ignoreUnavailable
    )

    putJob(request)

  }

  def putJob(request: PutJobRequest): ZIO[Any, FrameworkException, PutJobResponse] =
    client.execute[PutJobRequestBody, PutJobResponse](request)

  /*
   * Creates an inference trained model.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/put-trained-models.html
   *
   * @param modelId The ID of the trained models to store
   * @param part The definition part number. When the definition is loaded for inference the definition parts are streamed in the
   * order of their part number. The first part must be `0` and the final part must be `total_parts - 1`.

   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param deferDefinitionDecompression If set to `true` and a `compressed_definition` is provided, the request defers definition decompression and skips relevant validations.
   */
  def putTrainedModel(
    modelId: String,
    part: Int,
    body: PutTrainedModelRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    deferDefinitionDecompression: Boolean = false
  ): ZIO[Any, FrameworkException, PutTrainedModelResponse] = {
    val request = PutTrainedModelRequest(
      modelId = modelId,
      part = part,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      deferDefinitionDecompression = deferDefinitionDecompression
    )

    putTrainedModel(request)

  }

  def putTrainedModel(request: PutTrainedModelRequest): ZIO[Any, FrameworkException, PutTrainedModelResponse] =
    client.execute[PutTrainedModelRequestBody, PutTrainedModelResponse](request)

  /*
   * Creates a new model alias (or reassigns an existing one) to refer to the trained model
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/put-trained-models-aliases.html
   *
   * @param modelId The trained model where the model alias should be assigned
   * @param modelAlias The trained model alias to update
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param reassign If the model_alias already exists and points to a separate model_id, this parameter must be true. Defaults to false.
   */
  def putTrainedModelAlias(
    modelId: String,
    modelAlias: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    reassign: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, PutTrainedModelAliasResponse] = {
    val request = PutTrainedModelAliasRequest(
      modelId = modelId,
      modelAlias = modelAlias,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      reassign = reassign
    )

    putTrainedModelAlias(request)

  }

  def putTrainedModelAlias(
    request: PutTrainedModelAliasRequest
  ): ZIO[Any, FrameworkException, PutTrainedModelAliasResponse] =
    client.execute[Json, PutTrainedModelAliasResponse](request)

  /*
   * Creates part of a trained model definition
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/put-trained-model-definition-part.html
   *
   * @param modelId The ID of the trained model for this definition part
   * @param part The part number
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def putTrainedModelDefinitionPart(
    modelId: String,
    part: String,
    body: PutTrainedModelDefinitionPartRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, PutTrainedModelDefinitionPartResponse] = {
    val request = PutTrainedModelDefinitionPartRequest(
      modelId = modelId,
      part = part,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    putTrainedModelDefinitionPart(request)

  }

  def putTrainedModelDefinitionPart(
    request: PutTrainedModelDefinitionPartRequest
  ): ZIO[Any, FrameworkException, PutTrainedModelDefinitionPartResponse] =
    client.execute[PutTrainedModelDefinitionPartRequestBody, PutTrainedModelDefinitionPartResponse](request)

  /*
   * Creates a trained model vocabulary
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/put-trained-model-vocabulary.html
   *
   * @param modelId The ID of the trained model for this vocabulary
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def putTrainedModelVocabulary(
    modelId: String,
    body: PutTrainedModelVocabularyRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, PutTrainedModelVocabularyResponse] = {
    val request = PutTrainedModelVocabularyRequest(
      modelId = modelId,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    putTrainedModelVocabulary(request)

  }

  def putTrainedModelVocabulary(
    request: PutTrainedModelVocabularyRequest
  ): ZIO[Any, FrameworkException, PutTrainedModelVocabularyResponse] =
    client.execute[PutTrainedModelVocabularyRequestBody, PutTrainedModelVocabularyResponse](request)

  /*
   * Resets an existing anomaly detection job.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-reset-job.html
   *
   * @param jobId The ID of the job to reset
   * @param deleteUserAnnotations Specifies whether annotations that have been added by the
   * user should be deleted along with any auto-generated annotations when the job is
   * reset.
   * @server_default false

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param waitForCompletion Should this request wait until the operation has completed before returning
   */
  def resetJob(
    jobId: String,
    deleteUserAnnotations: Boolean,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    waitForCompletion: Boolean = true
  ): ZIO[Any, FrameworkException, ResetJobResponse] = {
    val request = ResetJobRequest(
      jobId = jobId,
      deleteUserAnnotations = deleteUserAnnotations,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      waitForCompletion = waitForCompletion
    )

    resetJob(request)

  }

  def resetJob(request: ResetJobRequest): ZIO[Any, FrameworkException, ResetJobResponse] =
    client.execute[Json, ResetJobResponse](request)

  /*
   * Reverts to a specific snapshot.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-revert-snapshot.html
   *
   * @param jobId The ID of the job to fetch
   * @param snapshotId The ID of the snapshot to revert to
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param deleteInterveningResults Should we reset the results back to the time of the snapshot?
   */
  def revertModelSnapshot(
    jobId: String,
    snapshotId: String,
    body: RevertModelSnapshotRequestBody = RevertModelSnapshotRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    deleteInterveningResults: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, RevertModelSnapshotResponse] = {
    val request = RevertModelSnapshotRequest(
      jobId = jobId,
      snapshotId = snapshotId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      deleteInterveningResults = deleteInterveningResults
    )

    revertModelSnapshot(request)

  }

  def revertModelSnapshot(
    request: RevertModelSnapshotRequest
  ): ZIO[Any, FrameworkException, RevertModelSnapshotResponse] =
    client.execute[RevertModelSnapshotRequestBody, RevertModelSnapshotResponse](request)

  /*
   * Sets a cluster wide upgrade_mode setting that prepares machine learning indices for an upgrade.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-set-upgrade-mode.html
   *
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param enabled Whether to enable upgrade_mode ML setting or not. Defaults to false.
   * @param timeout Controls the time to wait before action times out. Defaults to 30 seconds
   */
  def setUpgradeMode(
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    enabled: Option[Boolean] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, SetUpgradeModeResponse] = {
    val request = SetUpgradeModeRequest(
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      enabled = enabled,
      timeout = timeout
    )

    setUpgradeMode(request)

  }

  def setUpgradeMode(request: SetUpgradeModeRequest): ZIO[Any, FrameworkException, SetUpgradeModeResponse] =
    client.execute[Json, SetUpgradeModeResponse](request)

  /*
   * Starts a data frame analytics job.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/start-dfanalytics.html
   *
   * @param id The ID of the data frame analytics to start
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param timeout Controls the time to wait until the task has started. Defaults to 20 seconds
   */
  def startDataFrameAnalytics(
    id: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    body: Json = Json.Null,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, StartDataFrameAnalyticsResponse] = {
    val request = StartDataFrameAnalyticsRequest(
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      timeout = timeout
    )

    startDataFrameAnalytics(request)

  }

  def startDataFrameAnalytics(
    request: StartDataFrameAnalyticsRequest
  ): ZIO[Any, FrameworkException, StartDataFrameAnalyticsResponse] =
    client.execute[Json, StartDataFrameAnalyticsResponse](request)

  /*
   * Starts one or more datafeeds.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-start-datafeed.html
   *
   * @param datafeedId The ID of the datafeed to start
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param body body the body of the call
   * @param end The end time when the datafeed should stop. When not set, the datafeed continues in real time
   * @param start The start time from where the datafeed should begin
   * @param timeout Controls the time to wait until a datafeed has started. Default to 20 seconds
   */
  def startDatafeed(
    datafeedId: String,
    body: StartDatafeedRequestBody = StartDatafeedRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    end: Option[String] = None,
    start: Option[String] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, StartDatafeedResponse] = {
    val request = StartDatafeedRequest(
      datafeedId = datafeedId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      body = body,
      end = end,
      start = start,
      timeout = timeout
    )

    startDatafeed(request)

  }

  def startDatafeed(request: StartDatafeedRequest): ZIO[Any, FrameworkException, StartDatafeedResponse] =
    client.execute[StartDatafeedRequestBody, StartDatafeedResponse](request)

  /*
   * Start a trained model deployment.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/start-trained-model-deployment.html
   *
   * @param modelId The unique identifier of the trained model.
   * @param priority

   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param cacheSize A byte-size value for configuring the inference cache size. For example, 20mb.
   * @param numberOfAllocations The total number of allocations this model is assigned across machine learning nodes.
   * @param queueCapacity Controls how many inference requests are allowed in the queue at a time.
   * @param threadsPerAllocation The number of threads used by each model allocation during inference.
   * @param timeout Controls the amount of time to wait for the model to deploy.
   * @param waitFor The allocation status for which to wait
   */
  def startTrainedModelDeployment(
    modelId: String,
    priority: TrainingPriority,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    cacheSize: Option[String] = None,
    numberOfAllocations: Int = 1,
    queueCapacity: Int = 1024,
    threadsPerAllocation: Int = 1,
    timeout: String = "20s",
    waitFor: String = "started"
  ): ZIO[Any, FrameworkException, StartTrainedModelDeploymentResponse] = {
    val request = StartTrainedModelDeploymentRequest(
      modelId = modelId,
      priority = priority,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      cacheSize = cacheSize,
      numberOfAllocations = numberOfAllocations,
      queueCapacity = queueCapacity,
      threadsPerAllocation = threadsPerAllocation,
      timeout = timeout,
      waitFor = waitFor
    )

    startTrainedModelDeployment(request)

  }

  def startTrainedModelDeployment(
    request: StartTrainedModelDeploymentRequest
  ): ZIO[Any, FrameworkException, StartTrainedModelDeploymentResponse] =
    client.execute[Json, StartTrainedModelDeploymentResponse](request)

  /*
   * Stops one or more data frame analytics jobs.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/stop-dfanalytics.html
   *
   * @param id The ID of the data frame analytics to stop
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoMatch Whether to ignore if a wildcard expression matches no data frame analytics. (This includes `_all` string or when no data frame analytics have been specified)
   * @param body body the body of the call
   * @param force True if the data frame analytics should be forcefully stopped
   * @param timeout Controls the time to wait until the task has stopped. Defaults to 20 seconds
   */
  def stopDataFrameAnalytics(
    id: String,
    body: Json = Json.Null,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoMatch: Option[Boolean] = None,
    force: Option[Boolean] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, StopDataFrameAnalyticsResponse] = {
    val request = StopDataFrameAnalyticsRequest(
      id = id,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoMatch = allowNoMatch,
      body = body,
      force = force,
      timeout = timeout
    )

    stopDataFrameAnalytics(request)

  }

  def stopDataFrameAnalytics(
    request: StopDataFrameAnalyticsRequest
  ): ZIO[Any, FrameworkException, StopDataFrameAnalyticsResponse] =
    client.execute[Json, StopDataFrameAnalyticsResponse](request)

  /*
   * Stops one or more datafeeds.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-stop-datafeed.html
   *
   * @param datafeedId The ID of the datafeed to stop
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoDatafeeds Whether to ignore if a wildcard expression matches no datafeeds. (This includes `_all` string or when no datafeeds have been specified)
   * @param allowNoMatch Whether to ignore if a wildcard expression matches no datafeeds. (This includes `_all` string or when no datafeeds have been specified)
   * @param body body the body of the call
   * @param force True if the datafeed should be forcefully stopped.
   * @param timeout Controls the time to wait until a datafeed has stopped. Default to 20 seconds
   */
  def stopDatafeed(
    datafeedId: String,
    body: StopDatafeedRequestBody = StopDatafeedRequestBody(),
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoDatafeeds: Option[Boolean] = None,
    allowNoMatch: Option[Boolean] = None,
    force: Option[Boolean] = None,
    timeout: Option[String] = None
  ): ZIO[Any, FrameworkException, StopDatafeedResponse] = {
    val request = StopDatafeedRequest(
      datafeedId = datafeedId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoDatafeeds = allowNoDatafeeds,
      allowNoMatch = allowNoMatch,
      body = body,
      force = force,
      timeout = timeout
    )

    stopDatafeed(request)

  }

  def stopDatafeed(request: StopDatafeedRequest): ZIO[Any, FrameworkException, StopDatafeedResponse] =
    client.execute[StopDatafeedRequestBody, StopDatafeedResponse](request)

  /*
   * Stop a trained model deployment.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/master/stop-trained-model-deployment.html
   *
   * @param modelId The unique identifier of the trained model.
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoMatch Whether to ignore if a wildcard expression matches no deployments. (This includes `_all` string or when no deployments have been specified)
   * @param body body the body of the call
   * @param force True if the deployment should be forcefully stopped
   */
  def stopTrainedModelDeployment(
    modelId: String,
    body: Json = Json.Null,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoMatch: Option[Boolean] = None,
    force: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, StopTrainedModelDeploymentResponse] = {
    val request = StopTrainedModelDeploymentRequest(
      modelId = modelId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoMatch = allowNoMatch,
      body = body,
      force = force
    )

    stopTrainedModelDeployment(request)

  }

  def stopTrainedModelDeployment(
    request: StopTrainedModelDeploymentRequest
  ): ZIO[Any, FrameworkException, StopTrainedModelDeploymentResponse] =
    client.execute[Json, StopTrainedModelDeploymentResponse](request)

  /*
   * Updates certain properties of a data frame analytics job.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/update-dfanalytics.html
   *
   * @param id The ID of the data frame analytics to update
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def updateDataFrameAnalytics(
    id: String,
    body: UpdateDataFrameAnalyticsRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, UpdateDataFrameAnalyticsResponse] = {
    val request = UpdateDataFrameAnalyticsRequest(
      id = id,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    updateDataFrameAnalytics(request)

  }

  def updateDataFrameAnalytics(
    request: UpdateDataFrameAnalyticsRequest
  ): ZIO[Any, FrameworkException, UpdateDataFrameAnalyticsResponse] =
    client.execute[UpdateDataFrameAnalyticsRequestBody, UpdateDataFrameAnalyticsResponse](request)

  /*
   * Updates certain properties of a datafeed.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-update-datafeed.html
   *
   * @param datafeedId The ID of the datafeed to update
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param allowNoIndices Ignore if the source indices expressions resolves to no concrete indices (default: true)
   * @param expandWildcards Whether source index expressions should get expanded to open or closed indices (default: open)
   * @param ignoreThrottled Ignore indices that are marked as throttled (default: true)
   * @param ignoreUnavailable Ignore unavailable indexes (default: false)
   */
  def updateDatafeed(
    datafeedId: String,
    body: UpdateDatafeedRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    allowNoIndices: Option[Boolean] = None,
    expandWildcards: Seq[ExpandWildcards] = Nil,
    ignoreThrottled: Option[Boolean] = None,
    ignoreUnavailable: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, UpdateDatafeedResponse] = {
    val request = UpdateDatafeedRequest(
      datafeedId = datafeedId,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      allowNoIndices = allowNoIndices,
      expandWildcards = expandWildcards,
      ignoreThrottled = ignoreThrottled,
      ignoreUnavailable = ignoreUnavailable
    )

    updateDatafeed(request)

  }

  def updateDatafeed(request: UpdateDatafeedRequest): ZIO[Any, FrameworkException, UpdateDatafeedResponse] =
    client.execute[UpdateDatafeedRequestBody, UpdateDatafeedResponse](request)

  /*
   * Updates the description of a filter, adds items, or removes items.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-update-filter.html
   *
   * @param filterId The ID of the filter to update
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def updateFilter(
    filterId: String,
    body: UpdateFilterRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, UpdateFilterResponse] = {
    val request = UpdateFilterRequest(
      filterId = filterId,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    updateFilter(request)

  }

  def updateFilter(request: UpdateFilterRequest): ZIO[Any, FrameworkException, UpdateFilterResponse] =
    client.execute[UpdateFilterRequestBody, UpdateFilterResponse](request)

  /*
   * Updates certain properties of an anomaly detection job.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-update-job.html
   *
   * @param jobId The ID of the job to create
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def updateJob(
    jobId: String,
    body: UpdateJobRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, UpdateJobResponse] = {
    val request = UpdateJobRequest(
      jobId = jobId,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    updateJob(request)

  }

  def updateJob(request: UpdateJobRequest): ZIO[Any, FrameworkException, UpdateJobResponse] =
    client.execute[UpdateJobRequestBody, UpdateJobResponse](request)

  /*
   * Updates certain properties of a snapshot.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-update-snapshot.html
   *
   * @param jobId The ID of the job to fetch
   * @param snapshotId The ID of the snapshot to update
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def updateModelSnapshot(
    jobId: String,
    snapshotId: String,
    body: UpdateModelSnapshotRequestBody,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, UpdateModelSnapshotResponse] = {
    val request = UpdateModelSnapshotRequest(
      jobId = jobId,
      snapshotId = snapshotId,
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    updateModelSnapshot(request)

  }

  def updateModelSnapshot(
    request: UpdateModelSnapshotRequest
  ): ZIO[Any, FrameworkException, UpdateModelSnapshotResponse] =
    client.execute[UpdateModelSnapshotRequestBody, UpdateModelSnapshotResponse](request)

  /*
   * Updates certain properties of trained model deployment.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-update-trained-model-deployment.html
   *
   * @param modelId The unique identifier of the trained model.
   * @param body body the body of the call
   */
  def updateTrainedModelDeployment(
    modelId: String,
    body: Json
  ): ZIO[Any, FrameworkException, UpdateTrainedModelDeploymentResponse] = {
    val request = UpdateTrainedModelDeploymentRequest(modelId = modelId, body = body)

    updateTrainedModelDeployment(request)

  }

  def updateTrainedModelDeployment(
    request: UpdateTrainedModelDeploymentRequest
  ): ZIO[Any, FrameworkException, UpdateTrainedModelDeploymentResponse] =
    client.execute[Json, UpdateTrainedModelDeploymentResponse](request)

  /*
   * Upgrades a given job snapshot to the current major version.
   * For more info refers to https://www.elastic.co/guide/en/elasticsearch/reference/current/ml-upgrade-job-model-snapshot.html
   *
   * @param jobId The ID of the job
   * @param snapshotId The ID of the snapshot
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   * @param timeout How long should the API wait for the job to be opened and the old snapshot to be loaded.
   * @param waitForCompletion Should the request wait until the task is complete before responding to the caller. Default is false.
   */
  def upgradeJobSnapshot(
    jobId: String,
    snapshotId: String,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean = false,
    timeout: Option[String] = None,
    waitForCompletion: Option[Boolean] = None
  ): ZIO[Any, FrameworkException, UpgradeJobSnapshotResponse] = {
    val request = UpgradeJobSnapshotRequest(
      jobId = jobId,
      snapshotId = snapshotId,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty,
      timeout = timeout,
      waitForCompletion = waitForCompletion
    )

    upgradeJobSnapshot(request)

  }

  def upgradeJobSnapshot(request: UpgradeJobSnapshotRequest): ZIO[Any, FrameworkException, UpgradeJobSnapshotResponse] =
    client.execute[Json, UpgradeJobSnapshotResponse](request)

  /*
   * Validates an anomaly detection job.
   * For more info refers to https://www.elastic.co/guide/en/machine-learning/current/ml-jobs.html
   *
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def validate(
    body: Detector,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, ValidateResponse] = {
    val request =
      ValidateRequest(body = body, errorTrace = errorTrace, filterPath = filterPath, human = human, pretty = pretty)

    validate(request)

  }

  def validate(request: ValidateRequest): ZIO[Any, FrameworkException, ValidateResponse] =
    client.execute[Detector, ValidateResponse](request)

  /*
   * Validates an anomaly detection detector.
   * For more info refers to https://www.elastic.co/guide/en/machine-learning/current/ml-jobs.html
   *
   * @param body body the body of the call
   * @param errorTrace When set to `true` Elasticsearch will include the full stack trace of errors
   * when they occur.
   * @server_default false

   * @param filterPath Comma-separated list of filters in dot notation which reduce the response
   * returned by Elasticsearch.

   * @param human When set to `true` will return statistics in a format suitable for humans.
   * For example `"exists_time": "1h"` for humans and
   * `"eixsts_time_in_millis": 3600000` for computers. When disabled the human
   * readable values will be omitted. This makes sense for responses being consumed
   * only by machines.
   * @server_default false

   * @param pretty If set to `true` the returned JSON will be "pretty-formatted". Only use
   * this option for debugging only.
   * @server_default false

   */
  def validateDetector(
    body: Detector,
    errorTrace: Boolean = false,
    filterPath: Chunk[String] = Chunk.empty[String],
    human: Boolean = false,
    pretty: Boolean
  ): ZIO[Any, FrameworkException, ValidateDetectorResponse] = {
    val request = ValidateDetectorRequest(
      body = body,
      errorTrace = errorTrace,
      filterPath = filterPath,
      human = human,
      pretty = pretty
    )

    validateDetector(request)

  }

  def validateDetector(request: ValidateDetectorRequest): ZIO[Any, FrameworkException, ValidateDetectorResponse] =
    client.execute[Detector, ValidateDetectorResponse](request)

}
