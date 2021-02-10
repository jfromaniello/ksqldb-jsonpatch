KSQLDB user-defined function (UDF) to compute the [JSONPATCH](http://jsonpatch.com/) of two json objects.

Based on https://github.com/miguno/ksql-udf-examples


## Example

```
CREATE STREAM mystream (id VARCHAR, data VARCHAR)
    WITH (kafka_topic='mystream', partitions=1, value_format='json')


SELECT id, ARRAY_JSONPATCH(LATEST_BY_OFFSET(data, 2, false)) as data
FROM mystream
GROUP BY id
EMIT CHANGES;
```

Then we can insert like this:

```
insert into mystream(id, data) values('foo', '{"foo": 123}');
insert into mystream(id, data) values('foo', '{"foo": 123, "bar": 456}');
```

And a query using the udf will look like this:

```
ksql> SELECT id, JSONPATCH(LATEST_BY_OFFSET(data, 2, false)) as data
>FROM mystream
>GROUP BY id
>EMIT CHANGES;
>
+--------------------------------------------------------------------------------------------------------------+--------------------------------------------------------------------------------------------------------------+
|ID                                                                                                            |DATA                                                                                                          |
+--------------------------------------------------------------------------------------------------------------+--------------------------------------------------------------------------------------------------------------+
|foo                                                                                               |[{"op":"replace","path":"","value":{"foo":123}}]                                                              |
|foo                                                                                               |[{"op":"add","path":"/bar","value":456}]                                                                      |
q^CQuery terminated

```
