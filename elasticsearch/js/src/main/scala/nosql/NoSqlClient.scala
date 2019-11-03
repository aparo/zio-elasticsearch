package elasticsearch.nosql

final case class NoSqlClient(baseUrl: String) extends AbstractClient

object NoSqlClient {
  type AccumuloRecord = Map[Key, Value]
  var testMode: Boolean = false
  var defaultTestDB = "test"
}
