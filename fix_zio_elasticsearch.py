#!/usr/bin/env python3
import os
from glob import glob
import re

REGEX_CHANGES = [
    # (
    #     r"""c.downField\("(.*)"\).focus.flatMap\(_.asString\)""",
    #     """jObj.getOption[String]("$1")"""
    # ),
    (r'\bQueryContainer\b', 'Query'),
    (r'\bAggregationContainer\b', 'Aggregation'),
    (r'\bTaskInfos\b', 'Chunk[zio.elasticsearch.tasks.TaskInfo]'),
]

TEXT_CHANGES = [
    ('""1m""', '"1m"' ),
    ('""30s""', '"30s"' ),
    ('""20s""', '"20s"' ),
    ('""25s""', '"25s"' ),
    ('""10s""', '"10s"' ),
    ('""0""', '"0"' ),
    ('""1s""', '"1s"' ),
    ('""5d""', '"5d"' ),
    ('""5m""', '"5m"' ),
    ('"",""', '","' ),
    ('""started""', '"started"' ),
    ('(client: ElasticSearch)', '(client: ElasticSearchClient)' ),
    ('Option[String] | Option[Int]', 'Option[Json]' ),
    ('Option[Boolean] | Option[String]', 'Option[Json]' ),
    ('Option[Int] | Option[String]', 'Option[Json]' ),
    ('Option[String] | Option[Long]', 'Option[Json]' ),
    ('errorTrace: Boolean,', 'errorTrace: Boolean=false,'),
    ('filterPath: Chunk[String],', 'filterPath: Chunk[String]=Chunk.empty[String],'),
    ('human: Boolean,', 'human: Boolean=false,'),
    ('pretty: Boolean,', 'pretty: Boolean=false,'),
    ('masterTimeout: String,', 'masterTimeout: Option[String]=None,'),
    ('timeout: String,', 'timeout: Option[String]=None,'),
    ('HitsMetadata[TDocument]', 'HitResults'),
    ('ResponseItem[TDocument]', 'ResultDocument'),
    (' defaultOperator.OR', ' DefaultOperator.OR'),
    ('InlineGet[TDocument]', 'TDocument'),
    ('DurationValue[UnitNanos]', 'Long'),
    ('ccsMinimizeRoundtrips != "true"', 'ccsMinimizeRoundtrips != true'),
    ('ccsMinimizeRoundtrips != "true"', 'ccsMinimizeRoundtrips != true'),
    ('DurationValue[UnitSeconds]', 'Long'),
    ('DurationValue[UnitFloatMillis]', 'Double'),
    ('from: Int = "0",', 'from: Int = 0,'),
    ('size: Int = "100",', 'size: Int = 100,'),

    (
        "Double | null | None",
        "Option[Double] = None"
    ),
    (
        "match: Long,",
        "`match`: Long,"
    ),
    (
        "managed: Bool() = None",
        "managed: Option[Boolean] = None"
    )

]

PACKAGES = [
"elasticsearch-core"
]

files = sorted(
    list([
        y
        for pkg in PACKAGES
        for path in [os.path.join(pkg, "src", "main", "scala"),
                     os.path.join(pkg, "js", "src", "main", "scala"),
                     os.path.join(pkg, "jvm", "src", "main", "scala"),
                     os.path.join(pkg, "shared", "src", "main", "scala")]
        for x in os.walk(path)
        for y in glob(os.path.join(x[0], "*.scala"))
    ]))
for file in files:
    # print(file)
    original = open(file, "r").read()
    content = original

    for rx, new_value in REGEX_CHANGES:
        matches = re.finditer(rx, content, re.MULTILINE)
        for match in matches:
            item = match.group()
            groups = match.groups()
            rep = new_value
            if len(groups) > 0:
                for pos, g in enumerate(groups):
                    rep = rep.replace("$"+str(pos+1), g)
            content = content.replace(item, rep)

    for old_value, new_value in TEXT_CHANGES:
        content = content.replace(old_value, new_value)

    if original != content:
        print("Updating " + file)
        # print(content)
        with open(file, "w") as fo:
            fo.write(content)
