/*
 * Copyright 2019 - NTTDATA Italia S.P.A. All Rights Reserved.
 */

package elasticsearch.nosql

final case class NoSqlClient(baseUrl: String) extends AbstractClient

object NoSqlClient {
  type AccumuloRecord = Map[Key, Value]
  var testMode: Boolean = false
  var defaultTestDB = "test"
}
